package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.Result;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.ExecutorStatusMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ReplanMsg;
import org.opencyc.elf.message.ScheduleConsistencyEvaluationMsg;
import org.opencyc.elf.message.ScheduleConsistencyRequestMsg;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;

//// External Imports

import java.util.ArrayList;
import java.util.List;

import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * <P>
 * Scheduler performs temporal task decomposition for a given assigned agent
 * and its allocated resources.
 * </p>
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
public class Scheduler extends NodeComponent {
  //// Constructors

  /** Creates a new instance of Scheduler */
  public Scheduler() {
  }

  /** 
   * Creates a new instance of Scheduler with the given
   * input and output channels.
   *
   * @param schedulerChannel the takable channel from which messages are input
   * @param jobAssignerChannel the puttable channel to which messages are output to the
   * job assigner
   */
  public Scheduler (Takable schedulerChannel,
                    Puttable jobAssignerChannel) {
    this.schedulerChannel = schedulerChannel;           
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

  //// Public Area

  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Scheduler)) {
      return false;
    }

    //TODO
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    //TODO
    return "";
  }

  /**
   * Gets the executor for this sceduler
   *
   * @return the executor for this sceduler
   */
  public org.opencyc.elf.bg.executor.Executor getExecutor () {
    return executor;
  }

  /**
   * Sets the executor for this sceduler
   *
   * @param executor the executor for this sceduler
   */
  public void setExecutor (org.opencyc.elf.bg.executor.Executor executor) {
    this.executor = executor;
  }

  /**
   * Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   */
  public Puttable getChannel() {
    return (Puttable) schedulerChannel;
  }

  /**
   * Gets the resources required by this scheduler.
   *
   * @return the resources required by this scheduler
   */
  public List getResources() {
    List resources = new ArrayList();
    //TODO iterate through the task frame schedule infos
    return resources;
  }
  
  //// Protected Area
  
  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable schedulerChannel;
    
    /** the puttable channel to which messages are output to the job assigner */
    protected final Puttable jobAssignerChannel;
    
    /** the parent node component */
    protected NodeComponent nodeComponent;
          
    /** the node's controlled resources */
    protected List controlledResources;
    
    /** the node's commanded task */
    protected TaskCommand taskCommand;
        
    /** the schedule which is planned to accomplish the commanded task */
    protected Schedule schedule;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param schedulerChannel the takable channel from which messages are input
     * @param jobAssignerChannel the puttable channel to which messages are output to the
     * job assigner
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable schedulerChannel,
                        Puttable jobAssignerChannel,
                        NodeComponent nodeComponent) { 
      this.schedulerChannel = schedulerChannel;
      this.jobAssignerChannel = jobAssignerChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) schedulerChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
     
    //TODO think about conversations and thread safety
    
    /**
     * Dispatches the given input channel message by type.
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
    }
  
    /**
     * Processes the schedule job message.
     *
     * @param scheduleJobMsg the schedule job message
     */
    protected void processScheduleJobMsg (ScheduleJobMsg scheduleJobMsg) {
      taskCommand = scheduleJobMsg.getTaskCommand();
      //TODO
    }
                
    /**
     * Processes the replan message.
     *
     * @param replanMsg the replan message
     */
    protected void processReplanMsg (ReplanMsg replanMsg) {
      //TODO
    }
        
    /**
     * Processes the executor status message.
     *
     * @param executorStatusMsg the executor status message
     */
    protected void processExecutorStatusMsg (ExecutorStatusMsg executorStatusMsg) {
      //TODO
      Status status = executorStatusMsg.getStatus();
    }
    
    /**
     * Processes the schedule consistency message.
     *
     * @param scheduleConsistencyRequestMsg the schedule consistency request message
     */
    protected void processScheduleConsistencyRequestMsg (ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg) {
      List peerControlledResources = scheduleConsistencyRequestMsg.getControlledResources();
      TaskCommand peerTaskCommand = scheduleConsistencyRequestMsg.getTaskCommand();
      Schedule peerSchedule = scheduleConsistencyRequestMsg.getSchedule();
      //TODO
    }
    
    /** Sends the scheduler status message to the job assigner. */
    protected void sendSchedulerStatusMsg () {
      //TODO
      Status status = new Status();
      
      SchedulerStatusMsg schedulerStatusMsg = new SchedulerStatusMsg();
      schedulerStatusMsg.setSender(nodeComponent);
      schedulerStatusMsg.setStatus(status);
      nodeComponent.sendMsgToRecipient(jobAssignerChannel, schedulerStatusMsg);
    }
    
    /**
     * Sends a schedule consistency request to a peer scheduler.
     *
     * @param peerScheduler the peer scheduler
     */
    protected void sendScheduleConsistencyRequestMsg (Scheduler peerScheduler) {
      ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg = 
        new ScheduleConsistencyRequestMsg();
      scheduleConsistencyRequestMsg.setSender(nodeComponent);
      scheduleConsistencyRequestMsg.setReplyToChannel((Puttable) schedulerChannel);
      scheduleConsistencyRequestMsg.setControlledResources(controlledResources);
      scheduleConsistencyRequestMsg.setTaskCommand(taskCommand);
      scheduleConsistencyRequestMsg.setSchedule(schedule);
      nodeComponent.sendMsgToRecipient(peerScheduler.getChannel(), 
                                       scheduleConsistencyRequestMsg);
    }    

    /**
     * Sends a schedule consistency evaluation message to a peer scheduler in
     * response to its schedule consistency request.
     *
     * @param scheduleConsistencyRequestMsg the given schedule consistency request message
     */
    protected void sendScheduleConsistencyEvaluationMsg (ScheduleConsistencyRequestMsg scheduleConsistencyRequestMsg) {
      ScheduleConsistencyEvaluationMsg scheduleConsistencyEvaluationMsg = 
        new ScheduleConsistencyEvaluationMsg();
      scheduleConsistencyEvaluationMsg.setSender(nodeComponent);
      scheduleConsistencyEvaluationMsg.setInReplyToMsg(scheduleConsistencyRequestMsg);
      scheduleConsistencyEvaluationMsg.setControlledResources(controlledResources);
      scheduleConsistencyEvaluationMsg.setTaskCommand(taskCommand);
      scheduleConsistencyEvaluationMsg.setSchedule(schedule);
      nodeComponent.sendMsgToRecipient(scheduleConsistencyRequestMsg.getReplyToChannel(),
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
  org.opencyc.elf.bg.executor.Executor executor;
}