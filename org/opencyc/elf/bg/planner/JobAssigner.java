package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.bg.command.Command;
import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.JobAssignerStatusMsg;
import org.opencyc.elf.message.ReleaseMsg;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;
import org.opencyc.elf.wm.JobLibrary;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** JobAssigner performs the non-temporal (for example spatial) task decomposition
 * among the available agents and resources.  The input task command results in a job
 * assignments for one or more schedulers.
 *
 * @version $Id$
 * @author Stephen L. Reed
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class JobAssigner extends BufferedNodeComponent implements Actuator {
  
  //// Constructors
  
  /** Creates a new instance of JobAssigner within the given node, that can accompish
   * the given named actions.
   *
   * @param node the containing ELF node
   * @param jobAssignerChannel the takable channel from which messages are input
   */
  public JobAssigner(Node node, Takable jobAssignerChannel) {
    setNode(node);
    this.jobAssignerChannel = jobAssignerChannel;
    node.getBehaviorGeneration().setJobAssigner(this);
  }
  
  //// Public Area
  
  /** Initializes with the given input and output channels and starts consuming task commands.
   *
   * @param executorChannel the puttable channel to which messages are output to the higher
   * level executor, or null if this is the highest level
   */
  public void initialize(Puttable executorChannel) {
    getLogger().info("Initializing JobAssigner");
    consumer = new Consumer(jobAssignerChannel, executorChannel, this);
    executor = new ThreadedExecutor();
    try {
      executor.execute(consumer);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /** Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   *
   */
  public Puttable getChannel() {
    return (Puttable) jobAssignerChannel;
  }
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[JobAssigner ");
    if (taskCommand != null)
      stringBuffer.append(taskCommand.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the list of scheduler infos for this job assigner
   *
   * @return the list of scheduler infos for this job assigner
   */
  public List getSchedulerInfos() {
    return schedulerInfos;
  }
  
  /** Gets the name of the virtual actuator.
   *
   * @return the name of the virtual actuator
   */
  public String getName() {
    return getNode().toString();
  }
  
  /** Gets the resources required by this virtual actuator by iterating over the
   * resources required by the contained schedulers.
   *
   * @return the resources required by this virtual actuator
   */
  public List getResources() {
    List resources = new ArrayList();
    Iterator schedulerIterator = schedulerInfos.iterator();
    while (schedulerIterator.hasNext()) {
      JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerIterator.next();
      Scheduler scheduler = schedulerInfo.scheduler;
      Iterator resourceIterator = scheduler.getResources().iterator();
      while (resourceIterator.hasNext()) {
        Resource resource = (Resource) resourceIterator.next();
        if (! resources.contains(resource))
          resources.add(resource);
      }
    }
    return resources;
  }
  
  //// Protected Area
  
  /** Thread which processes the input message channel. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable jobAssignerChannel;
    
    /** the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     */
    protected Puttable executorChannel;
    
    /** the reference to this node component as a message sender */
    protected NodeComponent sender;
    
    /** Creates a new instance of Consumer.
     *
     * @param jobAssignerChannel the takable channel from which messages are input
     * @param executorChannel the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     * @param sender the reference to this node component as a message sender
     */
    protected Consumer(Takable jobAssignerChannel,
                       Puttable executorChannel,
                       NodeComponent sender) {
      getLogger().info("Creating JobAssigner.Consumer");
      this.jobAssignerChannel = jobAssignerChannel;
      this.executorChannel = executorChannel;
      this.sender = sender;
    }
    
    /** Reads messages from the input queue and processes them. */
    public void run() {
      getLogger().info("Running JobAssigner.Consumer");
      try {
        while (true) {
          dispatchMsg((GenericMsg) jobAssignerChannel.take());
        }
      }
      catch (InterruptedException ex) {}
    }
    
    /** Sets the puttable channel to which messages are output to the higher
     * level executor
     *
     * @param executorChannel the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     */
    public void setExecutorChannel(Puttable executorChannel) {
      this.executorChannel = executorChannel;
    }
    
    /** Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg(GenericMsg genericMsg) {
      if (genericMsg instanceof DoTaskMsg)
        processDoTaskMsg((DoTaskMsg) genericMsg);
      else if (genericMsg instanceof SchedulerStatusMsg)
        processSchedulerStatusMsg((SchedulerStatusMsg) genericMsg);
      else
        throw new RuntimeException("Unhandled message " + genericMsg);
    }
    
    /** Processes the do task message.
     *
     * @param doTaskMsg the do task message that contains the commanded task
     */
    protected void processDoTaskMsg(DoTaskMsg doTaskMsg) {
      getLogger().info("JobAssigner proccessing " + doTaskMsg);
      //TODO throw exception if busy schedulers
      //TODO handle abort task command
      taskCommand = doTaskMsg.getTaskCommand();
      getLogger().info("Do task: " + taskCommand);
      Command command = taskCommand.getCommand();
      List jobSets = JobLibrary.getInstance().getJobSets(command.getName());
      List jobs = determineBestJobSet(jobSets);
      assignJobsToSchedulers(jobs);
      sendJobsToSchedulers();
      getLogger().info("JobAssigner completed assignment of " + doTaskMsg);
    }
        
    /** Chooses the best of the alternative job sets.
     *
     * @param jobSets the alternative job sets
     * @return the best of the alternative job sets 
     */
    protected List determineBestJobSet(List jobSets) {
      //TODO For now just use the first one.
      return (List) jobSets.get(0);
    }
    
    /** Assigns the given job set to the schedulers in two passes.  In the first pass jobs
     * having direct actuators and sensors are assigned to schedulers (direct schedulers) which have 
     * matching direct actuators and sensors.  New direct schedulers are created for new direct actuators
     * and direct sensors appearing in the jobs.  Unmatched direct schedulers are releaseed.  In the
     * second pass, jobs are assinged to the first available scheduler.  New schedulers are created
     * in the event of an insufficient number of available schedulers.  Conversely, if any schedulers 
     * remain available after all the jobs have been assigned, then these schedulers are released.
     *
     * @param jobs the given jobs to assign to schedulers
     */
    protected void assignJobsToSchedulers(List jobs) {
      // mark all schedulers as available
      Iterator iter = schedulerInfos.iterator();
      while (iter.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) iter.next();
        schedulerInfo.isAssigned = true;
      }
      // pass one that assigns matching direct schedules to existing direct schedulers
      List passTwoJobs = new ArrayList();
      List unmatchedDirectJobs = new ArrayList();
      Iterator jobIterator = jobs.iterator();
      while (jobIterator.hasNext()) {
        boolean foundMatchingScheduler = false;
        Job job = (Job) jobIterator.next();
        if (job.getDirectActuatorName() == null &&
            job.getDirectSensorName() == null) {
         
          passTwoJobs.add(job);
          continue;
        }
        Iterator schedulerIterator = schedulerInfos.iterator();
        while (schedulerIterator.hasNext()) {
          JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerIterator.next();
          if (! schedulerInfo.isAssigned)
            continue;
          Job previousJob = schedulerInfo.job;
          if ((job.getDirectActuatorName() != null &&
               previousJob.getDirectActuatorName() != null &&
               job.getDirectActuatorName().equals(previousJob.getDirectActuatorName())) ||
              (job.getDirectSensorName() != null &&
               previousJob.getDirectSensorName() != null &&
               job.getDirectSensorName().equals(previousJob.getDirectSensorName()))) {
            getLogger().info("matching scheduler:" + schedulerInfo.scheduler + " job: " + job);
            schedulerInfo.isAssigned = false;
            schedulerInfo.job = job;
            foundMatchingScheduler = true;
            break;
          }
        }
        if (! foundMatchingScheduler)
          unmatchedDirectJobs.add(job);
      }
      Iterator unmatchedDirectJobIterator = unmatchedDirectJobs.iterator();
      while (unmatchedDirectJobIterator.hasNext()) {
        Job job = (Job) unmatchedDirectJobIterator.next();
        createScheduler(job);
      }
      // pass two that assigns remaining schedules to existing schedulers
      jobIterator = passTwoJobs.iterator();
      Iterator schedulerIterator = schedulerInfos.iterator();
      while (jobIterator.hasNext()) {
        Job job = (Job) jobIterator.next();
        boolean jobAssigned = false;
        while (schedulerIterator.hasNext()) {
          JobAssigner.SchedulerInfo schedulerInfo = 
            (JobAssigner.SchedulerInfo) schedulerIterator.next();
          if (schedulerInfo.isAssigned) {
            schedulerInfo.isAssigned = false;
            schedulerInfo.job = job;
            jobAssigned = true;
            getLogger().info("Using scheduler: " + schedulerInfo.scheduler + 
                             " for job: " + job);
            break;
          }
        }
        if (! jobAssigned)
          createScheduler(job);
      }
      releaseUnusedSchedulers();
    }

    /** Creates a new scheduler for the given job.
     *
     * @param job the given job
     */
    protected void createScheduler(Job job) {
      Channel schedulerChannel = new BoundedBuffer(NodeFactory.CHANNEL_CAPACITY);
      Scheduler scheduler = new Scheduler(getNode(), schedulerChannel);
      scheduler.initialize((Puttable) jobAssignerChannel);
      JobAssigner.SchedulerInfo schedulerInfo = new JobAssigner.SchedulerInfo();
      schedulerInfo.job = job;
      schedulerInfo.scheduler = scheduler;
      schedulerInfos.add(schedulerInfo);
      getLogger().info("Created new scheduler: " + scheduler + " for job: " + job);
    }
    
    /** Releases the unused schedulers. */
    protected void releaseUnusedSchedulers() {
      List releasedSchedulers = new ArrayList();
      Iterator schedulerInfoIterator = schedulerInfos.iterator();
      while (schedulerInfoIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerInfoIterator.next();
        if (schedulerInfo.isAssigned)
          releasedSchedulers.add(schedulerInfo);
      }
      Iterator releasedSchedulerIterator = releasedSchedulers.iterator();
      while (releasedSchedulerIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) releasedSchedulerIterator.next();
        getLogger().info("Releasing scheduler: " + schedulerInfo.scheduler);
        schedulerInfos.remove(schedulerInfo);
        releaseScheduler(schedulerInfo.scheduler);
      }
    }
    
    /** Releases the given unused scheduler.
     *
     * @param scheduler the given unused scheduler
     */
    protected void releaseScheduler(Scheduler scheduler) {
      ReleaseMsg releaseMsg = new ReleaseMsg(sender);
      sender.sendMsgToRecipient(scheduler.getChannel(), releaseMsg);
    }
    
    /** Sends the assigned jobs to the corresponding schedulers */ 
    protected void sendJobsToSchedulers() {
      Iterator schedulerInfoIterator = schedulerInfos.iterator();
      while (schedulerInfoIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerInfoIterator.next();
        schedulerInfo.isBusy = true;
        Job job = schedulerInfo.job;
        Scheduler scheduler = schedulerInfo.scheduler;
        ScheduleJobMsg scheduleJobMsg = new ScheduleJobMsg(sender, job);
        sender.sendMsgToRecipient(scheduler.getChannel(), scheduleJobMsg);
      }
    }

    /** Processes the schedule status message.
     *
     * @param schedulerStatusMsg he schedule status message
     */
    protected void processSchedulerStatusMsg(SchedulerStatusMsg schedulerStatusMsg) {
      Scheduler scheduler = (Scheduler) schedulerStatusMsg.getSender();
      Status status = schedulerStatusMsg.getStatus();
      boolean isBusy = (status.getValue(Status.SCHEDULE_FINISHED) != null);
      if (! isBusy)
        recordSchedulerCompletedSchedule(scheduler);
      checkIfAllSchedulersDone();
    }
    
    /** Records that the given scheduler has completed its assigned schedule.
     * 
     * @param scheduler the given scheduler
     */
    protected void recordSchedulerCompletedSchedule(Scheduler scheduler) {
      Iterator schedulerInfoIterator = schedulerInfos.iterator();
      while (schedulerInfoIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerInfoIterator.next();
        if (schedulerInfo.scheduler.equals(scheduler)) {
          schedulerInfo.isBusy = false;
          return;
        }
      }
      throw new RuntimeException("Scheduler: " + scheduler + " not found");
    }
    
    /** Checks if all the schedulers have completed their schedules and if so then sends
     * the task completed status message to the higher level exector.  If this is the highest
     * level job assigner, then the system exits.
     */
    protected void checkIfAllSchedulersDone() {
      Iterator schedulerInfoIterator = schedulerInfos.iterator();
      while (schedulerInfoIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerInfoIterator.next();
        if (schedulerInfo.isBusy)
          return;
      }
      if (this.executorChannel == null) {
        getLogger().info("The last task at the topmost level is done.");
        System.exit(0);
      }
      else {
        getLogger().info("The commanded task " + taskCommand + " is done");
        Status status = new Status();
        status.setValue(Status.COMMAND_FINISHED, Boolean.TRUE);
        JobAssignerStatusMsg jobAssignerStatusMsg = new JobAssignerStatusMsg(sender, status);
        sender.sendMsgToRecipient(executorChannel, jobAssignerStatusMsg);
      }
    }   
  }
  
  /** Contains the current schedulers and their assigned jobs. */
  protected class SchedulerInfo {
    /** the scheduler */
    protected Scheduler scheduler;
    
    /** the scheduler's job */
    protected Job job;
    
    /** indicates that the scheduler is available for job assignment */
    protected boolean isAssigned = false;
    
    /** indicates that the scheduler is busy with an assigned job */
    protected boolean isBusy = false;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the takable channel from which messages are input */
  protected Takable jobAssignerChannel;
  
  /** the thread which processes the input channel of messages */
  protected Consumer consumer;
  
  /** the executor of the consumer thread */
  protected Executor executor;
  
  /** the node's commanded task */
  protected TaskCommand taskCommand;
    
  /** the list of scheduler infos for this job assigner */
  protected List schedulerInfos = new ArrayList();
  
  //// main
}