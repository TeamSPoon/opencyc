package org.opencyc.elf.bg;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.bg.taskframe.TaskFrame;

import org.opencyc.elf.vj.Result;

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
   * Performs the commanded task.
   *
   * @taskCommand the commanded task
   */
  public void doTask (TaskCommand taskCommand) {
    //TODO
  }
  
  /**
   * Receives the task command and task frame.
   * 
   * @param taskCommand the task command
   * @param taskFrame the task frame
   */
  public void receiveTaskFrame (TaskCommand taskCommand, TaskFrame taskFrame) {
    //TODO
  }
  
  /**
   * Receives the value judgement result for the given schedule, controlled resources and task
   * frame.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskFrame the given task frame
   * @param schedule the given schedule
   * @param result the received value judgement result
   */
  public void receiveScheduleEvaluation (ArrayList controlledResources,
                                         TaskFrame taskFrame,
                                         Schedule schedule,
                                         Result result) {
    //TODO
    // receive via channel
  }
  
  /**
   * Receives a task command failure notification.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskCommand the given task command
   */
  public void failureNotification (ArrayList controlledResources,
                                   TaskCommand taskCommand) {
    //TODO
    // receive via channel
  }
  
  /**
   * Receives the status from value judgement with regard to the given list of controlled
   * resources, task frame and schedule.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskFrame the given task frame
   * @param schedule the given schedule
   * @param status the received value judgement status
   */
  public void receiveValueJudgementStatus (ArrayList controlledResources,
                                           TaskFrame taskFrame,
                                           Schedule schedule,
                                           Status status) {
    //TODO
    // receive via channel
  }
  
  /**
   * Forwards a request for the task frame associated with the given task command.
   *
   * @param taskCommand the task command
   */
  public void forwardFetchTaskFrame (TaskCommand taskCommand) {
    //TODO
    // send via channel
  }
  
  /**
   * Forwards the schedule corresponding to the given controlled resources and
   * task command.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskCommand the given task command
   * @param schedule the schedule
   */
  public void forwardSchedule (ArrayList controlledResources,
                               TaskCommand taskCommand,
                               Schedule schedule) {
    //TODO
    // send via channel
  }
  
  /**
   * Forwards the schedule corresponding to the given controlled resources and
   * task command.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskCommand the given task command
   * @param schedule the schedule
   */
  public void forwardPostSchedule (ArrayList controlledResources,
                                  TaskCommand taskCommand,
                                  Schedule schedule) {
    //TODO
    // send via channel
  }
  
  /**
   * Commands a subnode to perform the given task command using the given
   * controlled resources.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskCommand the given task command
   */
  public void doSubTask (ArrayList controlledResources,
                         TaskCommand taskCommand) {
    //TODO
  }
  
  /**
   * Gets the behavior generation status.
   *
   * @param status the behavior generation status
   */
  public void behaviorGenerationStatus (Status status) {
    //TODO
  }
  
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
  
  //// Private Area
  //// Internal Rep
}