package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Result;
import org.opencyc.elf.Status;

import org.opencyc.elf.a.DirectActuator;

import org.opencyc.elf.bg.predicate.PredicateExpression;

import org.opencyc.elf.bg.taskframe.Command;

import org.opencyc.elf.message.ExecuteScheduleMsg;
import org.opencyc.elf.message.ExecutorStatusMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ReplanMsg;
import org.opencyc.elf.message.ScheduleConsistencyEvaluationMsg;
import org.opencyc.elf.message.ScheduleConsistencyRequestMsg;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;

import org.opencyc.elf.s.DirectSensor;

import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.ScheduleLibrary;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** Scheduler performs temporal task decomposition for a given assigned agent
 * and its allocated resources.  The given job is used to retreive schedule sets from
 * the schedule library.  Each schedule set is an alternative set of sequential commands for
 * accomplishing the job.  The alternative schedule sets are ranked according to desirability,
 * then the scheduler finds the first set containing an eligible schedule, which is sent
 * to the executor for execution.  The remaining schedules in the schedule set are retained and
 * retried for eligibility as the executor completes its current schedule.  When no schedules
 * are eligible, then the job assigned to the schedule is deemed complete and a status message
 * to that effect is sent back to the job assigner.
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
public class Scheduler extends BufferedNodeComponent {
  
  //// Constructors

  /** Creates a new instance of Scheduler with the given
   * input and output channels.
   *
   * @param node the containing ELF node
   * @param schedulerChannel the takable channel from which messages are input from the
   * job assigner
   */
  public Scheduler (Node node,
                    Takable schedulerChannel) {
    setNode(node);
    this.schedulerChannel = schedulerChannel;         
  }

  //// Public Area

  /** Initializes this scheduler and begins consuming jobs to schedule.
   *
   * @param jobAssignerChannel the puttable channel to which messages are output to the
   * job assigner
   */
  public void initialize(Puttable jobAssignerChannel) {
    getLogger().info("Initializing Scheduler");
    consumer = new Consumer(schedulerChannel,
                            jobAssignerChannel,
                            this);
    consumerExecutor = new ThreadedExecutor();
    try {
      consumerExecutor.execute(consumer);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
 
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Scheduler ");
    if (job != null)
      stringBuffer.append(job.toString());
    if (schedule != null) {
      stringBuffer.append(" schedule: ");
      stringBuffer.append(schedule.toString());
    }
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /** Gets the executor for this sceduler
   *
   * @return the executor for this sceduler
   */
  public org.opencyc.elf.bg.executor.Executor getExecutor () {
    return executor;
  }

  /** Sets the executor for this sceduler
   *
   * @param executor the executor for this sceduler
   */
  public void setExecutor (org.opencyc.elf.bg.executor.Executor executor) {
    this.executor = executor;
  }

  /** Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   */
  public Puttable getChannel() {
    return (Puttable) schedulerChannel;
  }

  /** Gets the resources required by this scheduler.
   *
   * @return the resources required by this scheduler
   */
  public List getResources() {
    List resources = new ArrayList();
    //TODO iterate through the task frame schedule infos
    return resources;
  }
  
  //// Protected Area
  
  /** Thread which processes the input message channel. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable schedulerChannel;
    
    /** the puttable channel to which messages are output to the job assigner */
    protected final Puttable jobAssignerChannel;
    
    /** the reference to this node component as a message sender */
    protected NodeComponent thisScheduler;
          
    /** the node's controlled resources */
    protected List controlledResources;
    
    /** Creates a new instance of Consumer.
     *
     * @param schedulerChannel the takable channel from which messages are input
     * @param jobAssignerChannel the puttable channel to which messages are output to the
     * job assigner
     * @param thisScheduler the reference to this node component as a message sender
     */
    protected Consumer (Takable schedulerChannel,
                        Puttable jobAssignerChannel,
                        NodeComponent thisScheduler) { 
      getLogger().info("Creating Scheduler.Consumer");
      this.schedulerChannel = schedulerChannel;
      this.jobAssignerChannel = jobAssignerChannel;
      this.thisScheduler = thisScheduler;
    }

    /** Reads messages from the input queue and processes them. */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) schedulerChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
     
    /** Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof ScheduleJobMsg)
        processScheduleJobMsg((ScheduleJobMsg) genericMsg);
      else if (genericMsg instanceof ReplanMsg)
        processReplanMsg((ReplanMsg) genericMsg);
      else if (genericMsg instanceof ExecutorStatusMsg)
        processExecutorStatusMsg((ExecutorStatusMsg) genericMsg); 
      else if (genericMsg instanceof ScheduleConsistencyRequestMsg)
        processScheduleConsistencyRequestMsg((ScheduleConsistencyRequestMsg) genericMsg); 
      else
        throw new RuntimeException("Unhandled message " + genericMsg);
    }
  
    /** Processes the schedule job message.
     *
     * @param scheduleJobMsg the schedule job message
     */
    protected void processScheduleJobMsg (ScheduleJobMsg scheduleJobMsg) {
      getLogger().info("Scheduler proccessing " + scheduleJobMsg);
      job = scheduleJobMsg.getJob();
      Command command = job.getCommand();
      List scheduleSets = ScheduleLibrary.getInstance().getScheduleSets(command.getName());
      List schedules = determineBestScheduleSet(scheduleSets);
      Schedule schedule = determineEligibleSchedule(schedules);
      if (executor == null)
        createExecutor(schedule);
      getLogger().info("Scheduler sending to executor " + schedule);
      controlledResources = new ArrayList();
      //TODO populate controlledResources
      ExecuteScheduleMsg executeScheduleMsg = new ExecuteScheduleMsg(thisScheduler, schedule, controlledResources);
      thisScheduler.sendMsgToRecipient(executor.getChannel(), executeScheduleMsg);
      
    }
                
    /** Returns the first schedule from the schedules whose predicate expression is null.  
     *
     * @param schedules the given schedule
     * @return the first schedule from the schedules whose predicate expression is null
     */
    protected Schedule determineEligibleSchedule(List schedules) {
      getLogger().info("Considering schedules " + schedules);
      Iterator scheduleIterator = schedules.iterator();
      while (scheduleIterator.hasNext()) {
        Schedule schedule = (Schedule) scheduleIterator.next();
        if (schedule.getPredicateExpression() == null) 
          return schedule;
      }
      throw new BehaviorEngineException("No schedule to send to executor among " + schedules);
    }
    
    /** Determines the best of the alternative schedule sets.
     *
     * @param scheduleSets the alternative schedule sets
     * @return the the best of the alternative schedule sets 
     */
    protected List determineBestScheduleSet(List scheduleSets) {
      //TODO for now just return the first one
      return (List) scheduleSets.get(0);
    }
    
    /** Creates a new executor for the given schedule.
     *
     * @param schedule the given schedule
     */
    protected void createExecutor(Schedule schedule) {
      Channel executorChannel = new BoundedBuffer(NodeFactory.CHANNEL_CAPACITY);
      executor = new org.opencyc.elf.bg.executor.Executor(getNode(), executorChannel);
      executor.initialize((Puttable) schedulerChannel);
      getLogger().info("Created new executor: " + executor + " for schedule: " + schedule);
    }
    
    /** Processes the replan message.
     *
     * @param replanMsg the replan message
     */
    protected void processReplanMsg (ReplanMsg replanMsg) {
      //TODO
    }
        
    /** Processes the executor status message.
     *
     * @param executorStatusMsg the executor status message
     */
    protected void processExecutorStatusMsg (ExecutorStatusMsg executorStatusMsg) {
      Status status = executorStatusMsg.getStatus();
      if (status.isTrue(Status.SCHEDULE_FINISHED)) {
        status = new Status();
        status.setTrue(Status.SCHEDULE_FINISHED);
        SchedulerStatusMsg schedulerStatusMsg = new SchedulerStatusMsg(thisScheduler, status);
        thisScheduler.sendMsgToRecipient(jobAssignerChannel, schedulerStatusMsg);
      }
      else if (status.isTrue(Status.EXCEPTION)) 
        handleExecutorException(status);
      else
        throw new BehaviorEngineException("Executor status not handled " + status); 
    }
    
    /** Handles an executor exception by finding the first schedule whose predicate expression evaluates true
     * in the current exceptional state and sending it to the executor.  If no predicate expression 
     * evaluates true, then the exception status is sent to the parent job assigner for this scheduler.
     *
     * @param status the exception status from the executor
     */
    protected void handleExecutorException(Status status) {
      Iterator scheduleIterator = conditionalSchedules.iterator();
      while (scheduleIterator.hasNext()) {
        Schedule schedule = (Schedule) scheduleIterator.next();
        PredicateExpression predicateExpression = schedule.getPredicateExpression();
        if (predicateExpression != null &&
            predicateExpression.evaluate(thisScheduler.getNode().getWorldModel().getState())) {
          ExecuteScheduleMsg executeScheduleMsg = new ExecuteScheduleMsg(thisScheduler, schedule, controlledResources);
          thisScheduler.sendMsgToRecipient(executor.getChannel(), executeScheduleMsg);
          return;
        }
      }
      SchedulerStatusMsg schedulerStatusMsg = new SchedulerStatusMsg(thisScheduler, status);
      thisScheduler.sendMsgToRecipient(jobAssignerChannel, schedulerStatusMsg);
    }
  
    /** Processes the schedule consistency message.
     *
     * @param scheduleConsistencyRequestMsg the schedule consistency request message
     */
    protected void processScheduleConsistencyRequestMsg (ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg) {
      List peerControlledResources = scheduleConsistencyRequestMsg.getControlledResources();
      Command peerCommand = scheduleConsistencyRequestMsg.getCommand();
      Schedule peerSchedule = scheduleConsistencyRequestMsg.getSchedule();
      //TODO
    }
    
    /** Sends the scheduler status message to the job assigner. */
    protected void sendSchedulerStatusMsg () {
      //TODO
      Status status = new Status();
      
      SchedulerStatusMsg schedulerStatusMsg = new SchedulerStatusMsg(thisScheduler, status);
      thisScheduler.sendMsgToRecipient(jobAssignerChannel, schedulerStatusMsg);
    }
    
    /** Sends a schedule consistency request to a peer scheduler.
     *
     * @param peerScheduler the peer scheduler
     */
    protected void sendScheduleConsistencyRequestMsg (Scheduler peerScheduler) {
      ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg = 
        new ScheduleConsistencyRequestMsg(thisScheduler,
                                          controlledResources,
                                          job.getCommand(),
                                          schedule);
      scheduleConsistencyRequestMsg.setReplyToChannel((Puttable) schedulerChannel);
      thisScheduler.sendMsgToRecipient(peerScheduler.getChannel(), 
                                scheduleConsistencyRequestMsg);
    }    

    /** Sends a schedule consistency evaluation message to a peer scheduler in
     * response to its schedule consistency request.
     *
     * @param scheduleConsistencyRequestMsg the given schedule consistency request message
     */
    protected void sendScheduleConsistencyEvaluationMsg (ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg) {
      ScheduleConsistencyEvaluationMsg scheduleConsistencyEvaluationMsg = 
        new ScheduleConsistencyEvaluationMsg(thisScheduler, schedule, controlledResources);
      scheduleConsistencyEvaluationMsg.setInReplyToMsg(scheduleConsistencyRequestMsg);
      thisScheduler.sendMsgToRecipient(scheduleConsistencyRequestMsg.getReplyToChannel(),
                                scheduleConsistencyEvaluationMsg);
    }    
  }
  
  //// Private Area

  //// Internal Rep

  /** the takable channel from which messages are input */
  protected Takable schedulerChannel;

  /** the thread which processes the input channel of messages */
  protected Consumer consumer;

  /** the consumer thread executor */
  protected EDU.oswego.cs.dl.util.concurrent.Executor consumerExecutor;
  
  /** the executor for this scheduler */
  protected org.opencyc.elf.bg.executor.Executor executor;
  
  /** the assigned job to schedule */
  protected Job job;
  
  /** the set of conditional schedules that accomplish the assigned job */
  protected List conditionalSchedules;
  
  /** the current schedule sent to the executor for execution */
  protected Schedule schedule;
    
}