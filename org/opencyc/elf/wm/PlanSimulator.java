package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.EvaluateScheduleMsg;
import org.opencyc.elf.message.PredictedInputMsg;
import org.opencyc.elf.message.PredictionRequestMsg;
import org.opencyc.elf.message.SimulateScheduleMsg;
import org.opencyc.elf.message.SimulationFailureNotificationMsg;

//// External Imports

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

import java.util.ArrayList;

/**
 * Provides the plan simulator for the ELF WorldModel.<br>
 * 
 * @version $Id: SimulatorPredictor.java,v 1.1 2002/11/17 03:08:17 stephenreed
 *          Exp $
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
public class PlanSimulator extends NodeComponent {
  
  //// Constructors
  
  /**
   * Constructs a new PlanSimulator object.
   */
  public PlanSimulator() {
  }

  /** 
   * Creates a new instance of PlanSimulator with the given
   * input and output message channels.
   *
     * @param planEvaluatorChannel the puttable channel to which messages are output for the plan
     * evaluator
     * @param predictorChannel the puttable channel to which messages are output for the predictor
   */
  public PlanSimulator (Takable planSimulatorChannel,
                        Puttable planEvaluatorChannel,
                        Puttable predictorChannel) {
    consumer = new Consumer(planSimulatorChannel,
                            planEvaluatorChannel,
                            predictorChannel,
                            this);
    executor = new ThreadedExecutor();
    try {
      executor.execute(consumer);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public Puttable getChannel() {
  }
  
  //// Public Area
  
  //// Protected Area
  
  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable planSimulatorChannel;
    
    /**
     * the puttable channel to which messages are output for the plan evaluator
     */
    protected final Puttable planEvaluatorChannel;
    
    /**
     * the puttable channel to which messages are output for the predictor
     */
    protected final Puttable predictorChannel;
    
    /**
     * the scheduler's input channel used for a failure
     * notification message
     */
    protected Puttable schedulerChannel;
    
    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * the resources controlled by this node
     */
    protected ArrayList controlledResources;
    
    /**
     * the node's commanded task
     */
    protected TaskCommand taskCommand;
    
    /**
     * the proposed schedule for evaluation
     */
    protected Schedule schedule;
      
    protected SimulateScheduleMsg simulateScheduleMsg;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param planSimulatorChannel the takable channel from which messages are input
     * @param planEvaluatorChannel the puttable channel to which messages are output for the plan
     * evaluator
     * @param predictorChannel the puttable channel to which messages are output for the predictor
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable planSimulatorChannel,
                        Puttable planEvaluatorChannel,
                        Puttable predictorChannel,
                        NodeComponent nodeComponent) { 
      this.planSimulatorChannel = planSimulatorChannel;
      this.planEvaluatorChannel = planEvaluatorChannel;
      this.predictorChannel = predictorChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) planSimulatorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
      
    /**
     * Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof PredictedInputMsg)
        processPredictedInputMsg((PredictedInputMsg) genericMsg);
      else if (genericMsg instanceof SimulateScheduleMsg)
        processSimulateScheduleMsg((SimulateScheduleMsg) genericMsg);
    }
  
    /**
     * Simulates the schedule from an executor and sends the result to the plan evaluator.
     *
     * @param simulateScheduleMsg the simulate schedule message
     */
    protected void processSimulateScheduleMsg(SimulateScheduleMsg simulateScheduleMsg) {
      this.simulateScheduleMsg = simulateScheduleMsg;
      controlledResources =  simulateScheduleMsg.getControlledResources();
      taskCommand =  simulateScheduleMsg.getTaskCommand();
      schedule =  simulateScheduleMsg.getSchedule();
      schedulerChannel = simulateScheduleMsg.getReplyToChannel();
      //TODO
    }
    
    /**
     * Requests a predicted input from the predictor.
     */
    protected void requestPredictedInput () {
      //TODO
      Object obj = null;
      PredictedInputMsg predictedInputMsg = new PredictedInputMsg();
      predictedInputMsg.setSender(nodeComponent);
      predictedInputMsg.setReplyToChannel((Puttable) planSimulatorChannel);
      predictedInputMsg.setObj(obj);
      sendMsgToRecipient(predictorChannel, predictedInputMsg);
    }
    
    
    /**
     * Processes the predicted input message.
     *
     * @param predictedInputMsg the predicted input message
     */
    protected void processPredictedInputMsg(PredictedInputMsg predictedInputMsg) {
      Object obj = predictedInputMsg.getObj();
      Object data = predictedInputMsg.getData();
      //TODO
    }
    
    /**
     * Sends the evaluate schedule message to the plan evaluator.
     */
    protected void sendEvaluateScheduleMsg() {
      //TODO
      Object result = null;
      
      EvaluateScheduleMsg evaluateScheduleMsg = new EvaluateScheduleMsg();
      evaluateScheduleMsg.setSender(nodeComponent);
      evaluateScheduleMsg.setControlledResources(controlledResources);
      evaluateScheduleMsg.setTaskCommand(taskCommand);
      evaluateScheduleMsg.setSchedule(schedule);
      sendMsgToRecipient(planEvaluatorChannel, evaluateScheduleMsg);
    }

    /**
     * Sends the simulation failure notification message back to
     * the scheduler which requested the plan simulation.
     */
    protected void sendSimulationFailureNotificationMsg() {
      //TODO
      Object result = null;
      
      SimulationFailureNotificationMsg simulationFailureNotificationMsg = 
        new SimulationFailureNotificationMsg();
      simulationFailureNotificationMsg.setSender(nodeComponent);
      simulationFailureNotificationMsg.setInReplyToMsg(simulateScheduleMsg);
      simulationFailureNotificationMsg.setControlledResources(controlledResources);
      simulationFailureNotificationMsg.setTaskCommand(taskCommand);
      simulationFailureNotificationMsg.setSchedule(schedule);
      sendMsgToRecipient(schedulerChannel, simulationFailureNotificationMsg);
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the thread which processes the input channel of messages
   */
  Consumer consumer;
  
  /**
   * the executor of the consumer thread
   */
  Executor executor;
  
  //// Main
}