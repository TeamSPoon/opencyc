package org.opencyc.elf.bg.executor;

//// Internal Imports
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.a.DirectActuator;

import org.opencyc.elf.bg.BehaviorGeneration;

import org.opencyc.elf.bg.planner.JobAssigner;
import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.planner.Scheduler;

import org.opencyc.elf.bg.taskframe.Command;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.goal.Goal;

import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.message.ExecuteScheduleMsg;
import org.opencyc.elf.message.ExecutorStatusMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ReleaseMsg;

import org.opencyc.elf.s.DirectSensor;

import org.opencyc.elf.sp.SensoryPerception;

import org.opencyc.elf.wm.ActuatorPool;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.SensorPool;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** Provides the Executor for ELF BehaviorGeneration.
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
public class Executor extends BufferedNodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of Executor with the given input and output channels.
   *
   * @param node the containing ELF node
   * @param executorChannel the takable channel from which messages are input from the
   * associated scheduler
   */
  public Executor (Node node,
                   Takable executorChannel) {
    setNode(node);
    this.executorChannel = executorChannel;
    this.executor = this;
  }

  //// Public Area

  /** Initializes this executor and begins consuming schedules.
   *
   * @param schedulerChannel the puttable channel to which messages are output to the
   * associated scheduler
   */
  public void initialize(Puttable schedulerChannel) {
    getLogger().info("Initializing Executor");
    consumer = new Consumer(executorChannel,
                            schedulerChannel,
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
    return "Executor for " + node.toString();
  }
  
  /** returns the input channel for this buffered node component. 
   *
   * @return the input channel for this buffered node component
   */
  public Puttable getChannel() {
    return (Puttable) executorChannel;
  }
  //// Protected Area
  
  /** Thread which processes the input message channel. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable executorChannel;
    
    /** the puttable channel to which messages are output to the scheduler */
    protected final Puttable schedulerChannel;
    
    /** the reference to this node component as a message sender */
    protected NodeComponent sender;
          
    /** the node's controlled resources */
    protected List controlledResources;
    
    /** Creates a new instance of Consumer.
     *
     * @param executorChannel the takable channel from which messages are input
     * @param schedulerChannel the puttable channel to which messages are output to the
     * scheduler
     * @param sender the reference to this node component as a message sender
     */
    protected Consumer (Takable executorChannel,
                        Puttable schedulerChannel,
                        NodeComponent sender) { 
      getLogger().info("Creating Executor.Consumer");
      this.executorChannel = executorChannel;
      this.schedulerChannel = schedulerChannel;
      this.sender = sender;
    }

    /** Reads messages from the input queue and processes them. */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) executorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
     
    /** Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof ExecuteScheduleMsg)
        processExecutorScheduleMsg((ExecuteScheduleMsg) genericMsg);
      else
        throw new RuntimeException("Unhandled message " + genericMsg);
    }
    
    /** Processes the execute schedule message. 
     * 
     * @param executeSceduleMsg the execute schedule message
     */
    protected void processExecutorScheduleMsg(ExecuteScheduleMsg executeScheduleMsg) {
      executor.schedule = executeScheduleMsg.getSchedule();
      getLogger().info("Executing " + executor.schedule);
      controlledResources = executeScheduleMsg.getControlledResources();
      setupRequiredActuator();
      setupRequiredSensor();
      scheduleSequencer = new ScheduleSequencer();
      scheduleSequencerExecutor = new ThreadedExecutor();
      try {
        scheduleSequencerExecutor.execute(scheduleSequencer);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    
    /** Handles the direct sensor if one is required by the schedule, otherwise handles
     * the initialization of a lower level node.
     */
    protected void setupRequiredActuator() {
      String directActuatorName = executor.schedule.getDirectActuatorName();
      if (directActuatorName == null)
          initializeLowerLevelNode();
      else {
        if (executor.actuator == null)
          obtainDirectActuator();
        else if (executor.actuator instanceof DirectActuator &&
                 (! ((DirectActuator) executor.actuator).getName().equals(directActuatorName))) {
          releaseDirectActuator();
          obtainDirectActuator();
        }
      }
    }
    
    /** Releases the previous direct actuator. */
    protected void releaseDirectActuator() {
      ReleaseMsg releaseMsg = new ReleaseMsg(executor);
      executor.sendMsgToRecipient(executor.actuator.getChannel(), releaseMsg);
      executor.actuator = null;
    }
    
    /** Obtains the required direct actutor and attaches it to this executor. */
    protected void obtainDirectActuator() {
      String directActuatorName = executor.schedule.getDirectActuatorName();
      getLogger().info("Obtaining the actuator named " + directActuatorName);
      executor.actuator = ActuatorPool.getInstance().getActuator(directActuatorName);
      executor.actuatorChannel = executor.actuator.getChannel();
      executor.actuatorChannel = new BoundedBuffer(NodeFactory.CHANNEL_CAPACITY);
      ((DirectActuator) executor.actuator).initialize();
    }
    
    /** Handles the direct sensor if one is required by the schedule, including releasing
     * an existing sensor if it is no longer required.
     */
    protected void setupRequiredSensor() {
      String directSensorName = executor.schedule.getDirectSensorName();
      if (directSensorName != null) {
        if (executor.directSensor == null)
          obtainDirectSensor();
        else if (! executor.directSensor.getName().equals(directSensorName)) {
          releaseDirectSensor();
          obtainDirectSensor();
        }
      }
      else if (directSensor != null)
        releaseDirectSensor();
    }
    
    /** Releases the previous sensor from this node's sensory perception. */
    protected void releaseDirectSensor() {
      ReleaseMsg releaseMsg = new ReleaseMsg(executor);
      executor.sendMsgToRecipient(executor.directSensor.getChannel(), releaseMsg);
      executor.getNode().getSensoryPerception().removeSensor(executor.directSensor);
    }
    
    /** Obtains the required direct sensor and attaches it to this node's sensory perception. */
    protected void obtainDirectSensor() {
      String directSensorName = executor.schedule.getDirectSensorName();
      getLogger().info("Obtaining the sensor named " + directSensorName);
      executor.directSensor = SensorPool.getInstance().getSensor(directSensorName);
      executor.getNode().getSensoryPerception().addSensor(executor.directSensor);
      executor.directSensor.initialize((Puttable) executor.getNode().getSensoryPerception().getChannel());      
    }
    
    /** Connects this node to the new lower level node by initializing the lower level job assigner
     * and sensory perception.
     */
    protected void initializeLowerLevelNode () {
      getLogger().info("Executing " + executor.schedule);
      Node lowerLevelNode = NodeFactory.getInstance().makeNodeShell();
      executor.getNode().addChildNode(lowerLevelNode);
      executor.getNode().setParentNode(executor.getNode());
      JobAssigner lowerLevelJobAssigner = lowerLevelNode.getBehaviorGeneration().getJobAssigner();
      lowerLevelJobAssigner.initialize(executor.getChannel());
      executor.actuator = lowerLevelJobAssigner;
      executor.actuatorChannel = executor.actuator.getChannel();
      SensoryPerception lowerLevelSensoryPerception = lowerLevelNode.getSensoryPerception();
      executor.getNode().getSensoryPerception().addSensor(lowerLevelSensoryPerception);
      lowerLevelSensoryPerception.initialize(executor.getNode().getSensoryPerception().getChannel());
    }
  }
  
  /** Interruptable thread which executes the input schedule. */
  protected class ScheduleSequencer implements Runnable {
    
    /** Constructs a new ScheduleExecutor object */
    ScheduleSequencer() {
      executor.stopSchedule = false;
    }
    
    /** Executes the input schedule. */
    public void run() {
      List plannedCommands = executor.schedule.getPlannedCommands();
      getLogger().info("Executing the sequence of commands " + plannedCommands.toString());
      //TODO handle macro command by expanding macros (recursively) before sequencing
      Command command = null;
      Command nextCommand = null;
      // TODO for now ignore timing
      Iterator commandIterator = plannedCommands.iterator();
      while (true) {
        if (executor.stopSchedule || (! commandIterator.hasNext())) {
          reportScheduleFinished();
          return;
        }
        if (command instanceof Goal &&
            executor.actuator instanceof DirectActuator) {
            //TODO wait for goal to occur and continue or timeout and return with
            // timeout status msg
          continue;
        }
        
        //TODO handle perceive command
        
        //TODO handle alternative choice command
        //TODO handle conditional command
        //TODO handle iterated command
        //TODO handle learning episode command
        //TODO handle ordering choice command
        //TODO handle subset choice command
        
        command = (Command) commandIterator.next();
        TaskCommand taskCommand = new TaskCommand(command, nextCommand);
        DoTaskMsg doTaskMsg = new DoTaskMsg(executor, taskCommand);
        executor.sendMsgToRecipient(executor.actuatorChannel, doTaskMsg);
      }
    }    
    
    /** Reports to the scheduler that the current sequence of commands is finished. */
    protected void reportScheduleFinished() {
      Status status = new Status();
      status.setTrue(Status.SCHEDULE_FINISHED);
      ExecutorStatusMsg executorStatusMsg = new ExecutorStatusMsg(executor, status);
    }
    
  }
    
  //// Private Area
  
  //// Internal Rep
  
  /** the takable channel from which messages are input */
  protected Takable executorChannel;

  /** the puttable channel to which messages are output */
  protected Puttable actuatorChannel;

  /** the thread which processes the input channel of messages */
  protected Consumer consumer;

  /** the consumer thread executor */
  protected EDU.oswego.cs.dl.util.concurrent.Executor consumerExecutor;
  
  /** the thread which sequences through the schedule and sends the commands to the actuator */
  protected ScheduleSequencer scheduleSequencer;
  
  /** the schedule sequencer thread executor */
  protected EDU.oswego.cs.dl.util.concurrent.Executor scheduleSequencerExecutor;
  
  /** the executor for this schedule */
  protected org.opencyc.elf.bg.executor.Executor executor;
    
  /** the schedule to execute */
  protected Schedule schedule;
  
  /** when true, indicates that the schedule sequencer is to stop processing the schedule */
  protected boolean stopSchedule = false;

  /** the actuator to which this executor sends commands */
  protected Actuator actuator;
  
  /** the direct sensor required by the current schedule */
  protected DirectSensor directSensor;
  
}