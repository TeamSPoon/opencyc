package org.opencyc.elf.bg.taskframe;

//// Internal Imports

import org.opencyc.elf.goal.Goal;
import org.opencyc.elf.goal.GoalTime;

//// External Imports

/**
 * <P>
 * TaskCommand is an instruction to perform a named task.
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
public class TaskCommand {
  //// Constructors

  /**
   * Creates a new instance of TaskCommand
   */
  public TaskCommand() {
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
    if (!(obj instanceof TaskCommand)) {
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
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[TaskCommand: ");
    if (actionCommand != null)
      stringBuffer.append(actionCommand.toString());
    if (goalCommand != null)
      stringBuffer.append(goalCommand.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /**
   * Gets the action command plus modifiers
   *
   * @return the action command plus modifiers
   */
  public Action getActionCommand () {
    return actionCommand;
  }

  /**
   * Sets the action command plus modifiers
   *
   * @param actionCommand the action command plus modifiers
   */
  public void setActionCommand (Action actionCommand) {
    this.actionCommand = actionCommand;
  }

  /**
   * Gets the goal to be accomplish, or the state to be maintained, plus modifiers
   *
   * @return the goal to be accomplish, or the state to be maintained, plus modifiers
   */
  public Goal getGoalCommand () {
    return goalCommand;
  }

  /**
   * Sets the goal to be accomplish, or the state to be maintained, plus modifiers
   *
   * @param goalCommand the goal to be accomplish, or the state to be maintained, plus modifiers
   */
  public void setGoalCommand (Goal goalCommand) {
    this.goalCommand = goalCommand;
  }

  /**
   * Gets the goal time constraint plus modifiers such as tolerance
   *
   * @return the goal time constraint plus modifiers such as tolerance
   */
  public GoalTime getgoalTime () {
    return goalTime;
  }

  /**
   * Sets the goal time constraint plus modifiers such as tolerance
   *
   * @param goalTime the goal time constraint plus modifiers such as tolerance
   */
  public void setGoalTime (GoalTime goalTime) {
    this.goalTime = goalTime;
  }

  /**
   * Gets the planned next action command plus modifiers
   *
   * @return the planned next action command plus modifiers
   */
  public Action getNextActionCommand () {
    return nextActionCommand;
  }

  /**
   * Sets the planned next action command plus modifiers
   *
   * @param nextActionCommand the planned next action command plus modifiers
   */
  public void setNextActionCommand (Action nextActionCommand) {
    this.nextActionCommand = nextActionCommand;
  }

  /**
   * Gets the planned next goal to be accomplish, or the state to be maintained, plus modifiers
   *
   * @return the planned next goal to be accomplish, or the state to be maintained, plus modifiers
   */
  public Goal getNextGoalCommand () {
    return nextGoalCommand;
  }

  /**
   * Sets the planned next goal to be accomplish, or the state to be maintained, plus modifiers
   *
   * @param nextGoalCommand the planned next goal to be accomplish, or the state to be maintained, plus modifiers
   */
  public void setNextGoalCommand (Goal nextGoalCommand) {
    this.nextGoalCommand = nextGoalCommand;
  }

  /**
   * Gets the planned next goal time constraint plus modifiers such as tolerance
   *
   * @return the planned next goal time constraint plus modifiers such as tolerance
   */
  public GoalTime getNextGoalTime () {
    return nextGoalTime;
  }

  /**
   * Sets the planned next goal time constraint plus modifiers such as tolerance
   *
   * @param nextGoalTime the planned next goal time constraint plus modifiers such as tolerance
   */
  public void setNextGoalTime (GoalTime nextGoalTime) {
    this.nextGoalTime = nextGoalTime;
  }

  //// Protected Area
  
  /**
   * the action command plus modifiers
   */
  protected Action actionCommand;
  
  /**
   * the goal to be accomplish, or the state to be maintained, plus modifiers
   */
  protected Goal goalCommand;
  
  /**
   * the goal time constraint plus modifiers such as tolerance
   */
  protected GoalTime goalTime;
  
  /**
   * the planned next action command plus modifiers
   */
  protected Action nextActionCommand;
  
  /**
   * the planned next goal to be accomplish, or the state to be maintained, plus modifiers
   */
  protected Goal nextGoalCommand;
  
  /**
   * the planned next goal time constraint plus modifiers such as tolerance
   */
  protected GoalTime nextGoalTime;
  
  //// Private Area
  //// Internal Rep
}