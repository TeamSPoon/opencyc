package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.TaskFrame;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.JobAssignerStatusMsg;
import org.opencyc.elf.message.ReleaseMsg;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;

import org.opencyc.elf.wm.ActionLibrary;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.TaskFrameLibrary;
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
 * among the available agents and resources.  In many cases the task frame as retrieved from
 * the task frame library will aleady contain decomposed schedules for the task command.  In
 * that case a complete schedule is passed to the scheduler which then passes it to the 
 * executor.  Otherwise the spatially decomposed task command is passed to the scheduler which
 * computes a schedule to accomplish it. 
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
   * @param actionCapabilities the names of actions that this virtual actuator can accomplish
   * @param jobAssignerChannel the takable channel from which messages are input
   */
  public JobAssigner(Node node, List actionCapabilities, Takable jobAssignerChannel) {
    setNode(node);
    this.actionCapabilities = actionCapabilities;
    this.jobAssignerChannel = jobAssignerChannel;
    node.getBehaviorGeneration().setJobAssigner(this);
    thisJobAssigner = this;
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
    return getNode().getName();
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
  
  /** Gets the names of actions that this virtual actuator can accomplish.
   *
   * @return names of the actions that this virtual actuator can accomplish
   */
  public List getActionCapabilities() {
    return actionCapabilities;
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
    
    /** the parent node component */
    protected NodeComponent nodeComponent;
    
    /** the task frame for the current task command */
    protected TaskFrame taskFrame;
    
    /** Creates a new instance of Consumer.
     *
     * @param jobAssignerChannel the takable channel from which messages are input
     * @param executorChannel the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     * @param nodeComponent the parent node component
     */
    protected Consumer(Takable jobAssignerChannel,
    Puttable executorChannel,
    NodeComponent nodeComponent) {
      getLogger().info("Creating JobAssigner.Consumer");
      this.jobAssignerChannel = jobAssignerChannel;
      this.executorChannel = executorChannel;
      this.nodeComponent = nodeComponent;
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
      //TODO handle goals if the task command is not specified
      //TODO throw exception if busy schedulers
      //TODO handle abort task command
      taskCommand = doTaskMsg.getTaskCommand();
      getLogger().info("Do task: " + taskCommand);
      Action actionCommand = taskCommand.getActionCommand();
      String taskFrameName = actionCommand.getName();
      getLogger().info("task name " + taskFrameName);
      TaskFrame taskFrame = TaskFrameLibrary.getInstance().getTaskFrame(taskFrameName);
      List conditionalScheduleSets = determineBestScheduleSet(taskFrame);
      List scheduleSet = determineScheduleSet(conditionalScheduleSets);
      assignSchedulesToSchedulers(scheduleSet);
      sendSchedulesToSchedulers();
      getLogger().info("JobAssigner completed assignment of " + doTaskMsg);
    }
        
    /** Chooses the best of the alternative schedule sets.
     *
     * @param taskFrame the task frame
     * @return the best of the alternative schedule sets 
     */
    protected List determineBestScheduleSet(TaskFrame taskFrame) {
      //TODO Use the plan simulator and plan evaluator to chose the best schedule from the schedule set.
      // For now just use the first one.
      return (List) taskFrame.getScheduleAlternatives().get(0);
    }
    
    /** Returns the schedule set for the first true schedule set predicate.
     *
     * @param conditionalScheduleSets the conditional schedule sets
     * @return the schedule set for the first true schedule set predicate
     */
    protected List determineScheduleSet(List conditionalScheduleSets) {
      State state = getNode().getWorldModel().getState();
      Iterator iter = conditionalScheduleSets.iterator();
      while (iter.hasNext()) {
        ConditionalScheduleSet conditionalScheduleSet = (ConditionalScheduleSet) iter.next();
        if (conditionalScheduleSet.getPredicateExpression().evaluate(state))
          return conditionalScheduleSet.getScheduleSet();
      }
      getLogger().severe("no valid schedule for " + taskFrame);
      throw new RuntimeException("no valid schedule for " + taskFrame);
    }
    
    /** Assigns the given schedule set to the schedulers in two passes.  In the first pass schedules
     * having direct actuators and sensors are assigned to schedulers (direct schedulers) which have 
     * matching direct actuators and sensors.  New direct schedulers are created for new direct actuators
     * and direct sensors appearing in the schedules.  Unmatched direct schedulers are releaseed.  In the
     * second pass, schedules are assinged to the first available scheduler.  New schedulers are created
     * in the event of an insufficient number of available schedulers.  Conversely, if any schedulers 
     * remain available after all the schedules have been assigned, then these schedulers are released.
     *
     * @param scheduleSet the given schedule set to assign to schedulers
     */
    protected void assignSchedulesToSchedulers(List scheduleSet) {
      // mark all schedulers as available
      Iterator iter = schedulerInfos.iterator();
      while (iter.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) iter.next();
        schedulerInfo.isAssigned = true;
      }
      // pass one that assigns matching direct schedules to existing direct schedulers
      List passTwoSchedules = new ArrayList();
      List unmatchedDirectSchedules = new ArrayList();
      Iterator scheduleIterator = scheduleSet.iterator();
      while (scheduleIterator.hasNext()) {
        boolean foundMatchingScheduler = false;
        Schedule schedule = (Schedule) scheduleIterator.next();
        if (schedule.getDirectActuatorName() == null &&
            schedule.getDirectSensorName() == null) {
         
          passTwoSchedules.add(schedule);
          continue;
        }
        Iterator schedulerIterator = schedulerInfos.iterator();
        while (schedulerIterator.hasNext()) {
          JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerIterator.next();
          if (! schedulerInfo.isAssigned)
            continue;
          Schedule previousSchedule = schedulerInfo.schedule;
          if ((schedule.getDirectActuatorName() != null &&
               previousSchedule.getDirectActuatorName() != null &&
               schedule.getDirectActuatorName().equals(previousSchedule.getDirectActuatorName())) ||
              (schedule.getDirectSensorName() != null &&
               previousSchedule.getDirectSensorName() != null &&
               schedule.getDirectSensorName().equals(previousSchedule.getDirectSensorName()))) {
            getLogger().info("matching scheduler:" + schedulerInfo.scheduler + " schedule: " + schedule);
            schedulerInfo.isAssigned = false;
            schedulerInfo.schedule = schedule;
            foundMatchingScheduler = true;
            break;
          }
        }
        if (! foundMatchingScheduler)
          unmatchedDirectSchedules.add(schedule);
      }
      Iterator unmatchedDirectScheduleIterator = unmatchedDirectSchedules.iterator();
      while (unmatchedDirectScheduleIterator.hasNext()) {
        Schedule schedule = (Schedule) unmatchedDirectScheduleIterator.next();
        createScheduler(schedule);
      }
      // pass two that assigns remaining schedules to existing schedulers
      scheduleIterator = passTwoSchedules.iterator();
      Iterator schedulerIterator = schedulerInfos.iterator();
      while (scheduleIterator.hasNext()) {
        Schedule schedule = (Schedule) scheduleIterator.next();
        boolean scheduleAssigned = false;
        while (schedulerIterator.hasNext()) {
          JobAssigner.SchedulerInfo schedulerInfo = 
            (JobAssigner.SchedulerInfo) schedulerIterator.next();
          if (schedulerInfo.isAssigned) {
            schedulerInfo.isAssigned = false;
            schedulerInfo.schedule = schedule;
            scheduleAssigned = true;
            getLogger().info("Using scheduler: " + schedulerInfo.scheduler + 
                             " for schedule: " + schedule);
            break;
          }
        }
        if (! scheduleAssigned)
          createScheduler(schedule);
      }
      releaseUnusedSchedulers();
    }

    /** Creates a new scheduler for the given schedule.
     *
     * @param schedule the given schedule
     */
    protected void createScheduler(Schedule schedule) {
      Channel schedulerChannel = new BoundedBuffer(NodeFactory.CHANNEL_CAPACITY);
      Scheduler scheduler = new Scheduler(getNode(), schedulerChannel);
      scheduler.initialize((Puttable) jobAssignerChannel);
      JobAssigner.SchedulerInfo schedulerInfo = new JobAssigner.SchedulerInfo();
      schedulerInfo.schedule = schedule;
      schedulerInfo.scheduler = scheduler;
      schedulerInfos.add(schedulerInfo);
      getLogger().info("Created new scheduler: " + scheduler + " for schedule: " + schedule);
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
      ReleaseMsg releaseMsg = new ReleaseMsg();
      releaseMsg.setSender(thisJobAssigner);
      try {
        scheduler.getChannel().put(releaseMsg);
      }
      catch (InterruptedException e) {
      }
    }
    
    /** Sends the assigned schedules to the corresponding schedulers */ 
    protected void sendSchedulesToSchedulers() {
      Iterator schedulerInfoIterator = schedulerInfos.iterator();
      while (schedulerInfoIterator.hasNext()) {
        JobAssigner.SchedulerInfo schedulerInfo = (JobAssigner.SchedulerInfo) schedulerInfoIterator.next();
        schedulerInfo.isBusy = true;
        Schedule schedule = schedulerInfo.schedule;
        Scheduler scheduler = schedulerInfo.scheduler;
        ScheduleJobMsg scheduleJobMsg = new ScheduleJobMsg();
        scheduleJobMsg.setSender(thisJobAssigner);
        scheduleJobMsg.setTaskCommand(taskCommand);
        scheduleJobMsg.setSchedule(schedule);
        try {
          scheduler.getChannel().put(scheduleJobMsg);
          getLogger().info("Sent schedule: " + schedule + " to scheduler: " + scheduler);
        }
        catch (InterruptedException e) {
        }
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
        JobAssignerStatusMsg jobAssignerStatusMsg = new JobAssignerStatusMsg();
        jobAssignerStatusMsg.setSender(thisJobAssigner);
        Status status = new Status();
        status.setValue(Status.TASK_FINISHED, Boolean.TRUE);
        jobAssignerStatusMsg.setStatus(status);
        try {
          executorChannel.put(jobAssignerStatusMsg);
        }
        catch (InterruptedException e) {
        }
      }
    }   
  }
  
  /** Contains the current schedulers and their assigned schedules. */
  protected class SchedulerInfo {
    /** the scheduler */
    protected Scheduler scheduler;
    
    /** the schedule */
    protected Schedule schedule;
    
    /** indicates that the scheduler is available for assigning a schedule */
    protected boolean isAssigned = false;
    
    /** indicates that the scheduler is busy with an assigned schedule */
    protected boolean isBusy = false;
  }
  
  /** Sends the decompose task frame message to ?. */
  protected void decomposeTaskFrame() {
    //TODO
    // send via channel to ?
    // TaskCommand taskCommand
  }
  
  /** Decomposes the function for the current task frame. (May return
   * a list of scheduler/job pairs.)
   */
  protected void decomposeFunction() {
    //TODO
    // TaskCommand taskCommand
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the names of actions that this virtual actuator can accomplish */
  protected List actionCapabilities;
  
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
  
  /** a convenient reference to this object for use in the Consumer thread */
  protected JobAssigner thisJobAssigner;
  
  //// main
}