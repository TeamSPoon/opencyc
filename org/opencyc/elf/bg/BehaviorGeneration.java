package org.opencyc.elf.bg;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.bg.taskframe.TaskFrame;

import org.opencyc.elf.Result;

//// External Imports
import org.opencyc.elf.goal.Goal;

import java.util.ArrayList;



/**
 * Provides Behavior Generation for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
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
public class BehaviorGeneration extends NodeComponent {

  //// Constructors
  
  /**
   * Constructs a new BehaviorGeneration object.
   */
  public BehaviorGeneration() {
  }

  //// Public Area
    
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "BehaviorGeneration for " + node.getName();
  }

  /**
   * Gets the goal for generated behavior to achieve
   * 
   * @return the goal for generated behavior to achieve
   */
  public Goal getCommandedGoal() {
    return commandedGoal;
  }

  /**
   * Sets the goal for generated behavior to achieve
   * 
   * @param commandedGoal the goal for generated behavior to achieve
   */
  public void setCommandedGoal(Goal commandedGoal) {
    this.commandedGoal = commandedGoal;
  }

  /**
   * Gets the parent node's BehaviorGeneration object
   * 
   * @return the parent node's BehaviorGeneration object
   */
  public BehaviorGeneration getParentBehaviorGeneration() {
    return parentBehaviorGeneration;
  }

  /**
   * Sets the parent node's BehaviorGeneration object
   * 
   * @param parentBehaviorGeneration the parent node's BehaviorGeneration
   *        object
   */
  public void setParentBehaviorGeneration(BehaviorGeneration parentBehaviorGeneration) {
    this.parentBehaviorGeneration = parentBehaviorGeneration;
  }

  /**
   * Gets the child nodes' BehaviorGeneration objects
   * 
   * @return the child nodes' BehaviorGeneration objects
   */
  public ArrayList getChildrenBehaviorGeneration() {
    return childrenBehaviorGeneration;
  }

  /**
   * Sets the child nodes' BehaviorGeneration objects
   * 
   * @param childrenBehaviorGeneration the child nodes' BehaviorGeneration
   *        objects
   */
  public void setChildrenBehaviorGeneration(ArrayList childrenBehaviorGeneration) {
    this.childrenBehaviorGeneration = childrenBehaviorGeneration;
  }
  
  //// Protected Area
  
  /**
   * Receives the do task command message from the node.  The message contains the
   * task command to be performed.  Subsequently the doTask message is sent to job assigner.
   * Sub tasks may ultimately result from this activity.  
   */
  protected void doTask () {
    //TODO
    //receive via channel from node
    // TaskCommand taskCommand
    // send doTask(taskCommand) to jobAssigner
    // may trigger forwardDoSubTask(controlledResources, taskCommand)
  }
  
  /**
   * Receives the task frame message forwarded from world model.  Subsequently,
   * the task frame message is sent to job assigner
   */
  protected void receiveTaskFrame () {
    //TODO
    //receive via channel from world model
    // TaskCommand taskCommand
    // TaskFrame taskFrame
    // send receiveTaskFrame(taskCommand, taskFrame) to jobAssigner
  }
  
  /**
   * Receives the schedule evaluation message from ?.  The message is sent
   * to the plan selector.
   */
  protected void receiveScheduleEvaluation () {
    //TODO
    // receive via channel from ?
    // send via channel to the plan selector
    // ArrayList controlledResources
    // TaskFrame taskFrame
    // Schedule schedule
    // Result result
    // send receiveScheduleEvaluation(controlledResources, taskFrame, schedule, result)
  }
  
  /**
   * Receives the simulation failure notification message forwarded from world model.  The
   * message is sent to the appropriate scheduler.
   */
  protected void receiveSimulationFailureNotification () {
    //TODO
    // receive via channel from world model
    // send via channel to the appropriate scheduler
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // send receiveSimulationFailureNotification(taskCommand, schedule)
    // to (the appropriate) scheduler
  }
  
  /**
   * Receives the value judgement status message from value judgement and forwards
   * it on to the appropriate scheduler.
   */
  protected void receiveValueJudgementStatus () {
    //TODO
    // receive via channel from value judgement
    // send via channel fto the appropriate scheduler
    // ArrayList controlledResources
    // TaskFrame taskFrame
    // Schedule schedule
    // Status status
    // send receiveValueJudgementStatus(taskFrame, schedule, status) to
    // (the appropriate) scheduler
  }
  
  /**
   * Forwards the fetch task frame message from job assigner to world model.
   */
  protected void forwardFetchTaskFrame () {
    //TODO
    // receive via channel from job assigner
    // send via channel to world model
    // TaskCommand taskCommand
    // send receiveFetchTaskFrame(taskCommand) to worldModel
  }
  
  /**
   * Forwards the simulate schedule message from scheduler to the world model.
   */
  protected void forwardSimulateSchedule () {
    //TODO
    // receive via channel from scheduler
    // send via channel to world model
    // ArrayList controlledResources
    // TaskFrame taskFrame
    // Schedule schedule
    // send receiveSimulateSchedule(controlledResources, taskFrame, schedule) to worldModel
  }
  
  /**
   * Forwards the post schedule message from the plan selector to the world model.
   */
  public void forwardPostSchedule (ArrayList controlledResources,
                                  TaskCommand taskCommand,
                                  Schedule schedule) {
    //TODO
    // send via channel to world model
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send receivePostSchedule(controlledResources, taskCommand, schedule) to worldModel
  }
  
  /**
   * Forwards the do subtask message received from the executor to the node.
   */
  protected void forwardDoSubTask () {
    //TODO
    // send via channel to node
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // send doSubTask(controlledResources, taskCommand) to node
  }
  
  /**
   * Sends the behavior generation status message to the node.  Receipt of this message by
   * the node subsequently causes the node to send the status message to ?.  This action
   * is triggered by the receipt of the message from jobAssigner
   */
  protected void behaviorGenerationStatus () {
    //TODO
    // receive via channel from job assigner
    // send via channel to node (receiver not specified)
    // Status status
    // send status(status) message to node (receiver not specified)
  }

  //// Private Area
  //// Internal Rep
  
  /** the commanded goal for generated behavior to achieve */
  protected Goal commandedGoal;

  /**
   * the parent node's BehaviorGeneration object.  The topmost
   * BehaviorGeneration object has a value null here.
   */
  protected BehaviorGeneration parentBehaviorGeneration;

  /**
   * the child nodes' BehaviorGeneration objects.  The lowest level
   * BehavoriGeneration object has a value null here.
   */
  protected ArrayList childrenBehaviorGeneration;
  
}