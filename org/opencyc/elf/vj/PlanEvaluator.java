package org.opencyc.elf.vj;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.EvaluateScheduleMsg;
import org.opencyc.elf.message.ScheduleEvaluationResultMsg;

//// External Imports

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

import java.util.ArrayList;

/**
 * <P>PlanEvaluator is designed to evaluate plans and thus assist plan selection.
 *
 * @version $Id: SensoryPerception.java,v 1.1 2002/11/18 17:45:40 stephenreed
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
public class PlanEvaluator extends NodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of PlanEvaluator. */
  public PlanEvaluator() {
  }
  
  /** 
   * Creates a new instance of PlanEvaluator with the given
   * input and output message channels.
   *
   * @param planEvaluationChannel the takable channel from which messages are input
   * @param planSelectorChannel the puttable channel to which messages are output
   */
  public PlanEvaluator (Takable planEvaluatorChannel,
                        Puttable planSelectorChannel) {
    consumer = new Consumer(planEvaluatorChannel,
                            planSelectorChannel,
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
  
  //// Public Area
  
  //// Protected Area
  
  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable planEvaluatorChannel;
    
    /**
     * the puttable channel to which messages are output for the plan selector
     */
    protected final Puttable planSelectorChannel;
    
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
      
    /**
     * Creates a new instance of Consumer.
     *
     * @param planEvaluatorChannel the takable channel from which messages are input
     * @param planSelectorChannel the puttable channel to which messages are output
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable planEvaluatorChannel,
                        Puttable planSelectorChannel,
                        NodeComponent nodeComponent) { 
      this.planEvaluatorChannel = planEvaluatorChannel;
      this.planSelectorChannel = planSelectorChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          processEvaluateScheduleMsg((EvaluateScheduleMsg) planEvaluatorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
      
    /**
     * Evaluates the schedule from the plan simulator and sends the result to the plan selector. 
     */
    protected void processEvaluateScheduleMsg(EvaluateScheduleMsg evaluateScheduleMsg) {
      controlledResources = evaluateScheduleMsg.getControlledResources();
      taskCommand = evaluateScheduleMsg.getTaskCommand();
      schedule = evaluateScheduleMsg.getSchedule();
      //TODO
    }
    
    /**
     * Sends the schedule evaluation result message to the plan selector.
     */
    protected void sendScheduleEvaluationResultMsg() {
      //TODO
      Object result = null;
      
      ScheduleEvaluationResultMsg scheduleEvaluationResultMsg = new ScheduleEvaluationResultMsg();
      scheduleEvaluationResultMsg.setSender(nodeComponent);
      scheduleEvaluationResultMsg.setControlledResources(controlledResources);
      scheduleEvaluationResultMsg.setTaskCommand(taskCommand);
      scheduleEvaluationResultMsg.setSchedule(schedule);
      sendMsgToRecipient(planSelectorChannel, scheduleEvaluationResultMsg);
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
