package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.planner.ConditionalScheduleSet;
import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.goal.Goal;

import org.opencyc.elf.s.Sensor;

import org.opencyc.elf.wm.ActuatorPool;

//// External Imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;

/** TaskFrame specifies the materials, tools, and procedures to accomplish a task.  Generic task
 * frames are strored in the task frame library and from the library cloned instances are
 * provided to the job assigner when decomposing task commands.
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
public class TaskFrame {
  
  //// Constructors

  /** Creates a new instance of TaskFrame. */
  public TaskFrame () {
  }

  //// Public Area

  /** Creates and returns a copy of this object as initialized by the task frame factory
   * when the task frame library is populated.  The remaining instance variables in the
   * cloned task frame are set by the job assinger according to the commanded task.
   */
  public Object clone () {
    TaskFrame taskFrame = new TaskFrame();
    taskFrame.setTaskName(taskName);
    List clonedScheduleAlternatives = new ArrayList();
    Iterator alternativeIterator = scheduleAlternatives.iterator();
    while (alternativeIterator.hasNext()) {
      List conditionalScheduleSets = (List) alternativeIterator.next();
      List clonedConditionalScheduleSets = new ArrayList();
      Iterator iter = conditionalScheduleSets.iterator();
      while (iter.hasNext()) {
        ConditionalScheduleSet conditionalScheduleSet = (ConditionalScheduleSet) iter.next();
        clonedConditionalScheduleSets.add((ConditionalScheduleSet) conditionalScheduleSet.clone());
      }
      clonedScheduleAlternatives.add(clonedConditionalScheduleSets);
    }
    taskFrame.setScheduleAlternatives(clonedScheduleAlternatives);
    taskFrame.setTaskAction((Action) taskAction.clone());
    taskFrame.setTaskGoal(taskGoal);    
    return taskFrame;
  }
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[TaskFrame task: ");
    stringBuffer.append(taskName);
    stringBuffer.append(" goal: ");
    stringBuffer.append(taskGoal);
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /** Gets the task name which is either an action name or a goal name.
   * 
   * @return the task name which is either an action name or a goal name
   */
  public String getTaskName () {
    return taskName;
  }

  /** Sets the task name which is either an action name or a goal name.
   * 
   * @param taskName the task name which is either an action name or a goal name
   */
  public void setTaskName (String taskName) {
    this.taskName = taskName;
  }

  /** Generates a unique identifier for this task frame. */
  public void generateTaskId () {
    taskId = new UUID();
  }
  
  /** Gets the task identifier consisting of a unique id for each task commanded.
   * 
   * @return the task identifier consisting of a unique id for each task
   *         commanded
   */
  public UUID getTaskId () {
    return taskId;
  }

  /** Gets the action to be accomplished by this task
   *
   * @return the action to be accomplished by this task
   */
  public Action getTaskAction () {
    return taskAction;
  }

  /** Sets the action to be accomplished by this task
   *
   * @param taskAction the action to be accomplished by this task
   */
  public void setTaskAction (Action taskAction) {
    this.taskAction = taskAction;
  }

  /** Gets the desired state to be acheived or maintained by this task
   * 
   * @return the desired state to be acheived or maintained by this
   *         task
   */
  public Goal getTaskGoal () {
    return taskGoal;
  }

  /** Sets the desired state to be acheived or maintained by this task
   * 
   * @param taskGoal the desired state to be acheived or maintained by this
   *        task
   */
  public void setTaskGoal (Goal taskGoal) {
    this.taskGoal = taskGoal;
  }

  /** Gets the objects upon which the desired task is performed.
   * 
   * @return taskObjects the objects upon which the desired task is performed
   */
  public List getTaskObjects () {
    return taskObjects;
  }

  /** Sets the objects upon which the desired task is performed.
   * 
   * @param taskObjects the objects upon which the desired task is performed
   */
  public void setTaskObjects (List taskObjects) {
    this.taskObjects = taskObjects;
  }

  /** Gets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @return the parameters that specify or modulate how the
   *         task should be performed
   */
  public List getTaskParameters () {
    return taskParameters;
  }

  /** Sets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @param taskParameters the parameters that specify or modulate how the task
   *        should be performed
   */
  public void setTaskParameters (List taskParameters) {
    this.taskParameters = taskParameters;
  }

  /** Gets the constraints upon the performance of the task
   * 
   * @return the constraints upon the performance of the task
   */
  public List getTaskConstraints () {
    return taskConstraints;
  }

  /** Sets the constraints upon the performance of the task
   * 
   * @param taskConstraints the constraints upon the performance of the task
   */
  public void setTaskConstraints (List taskConstraints) {
    this.taskConstraints = taskConstraints;
  }

  /** Gets the schedule alternaives, each of which is a list of conditional schedule sets
   * 
   * @return the schedule alternaives, each of which is a list of conditional schedule sets
   */
  public List getScheduleAlternatives () {
    return scheduleAlternatives;
  }
 
  /** Adds athe schedule alternaives, each of which is a list of conditional schedule sets.
   *
   * @param conditionalScheduleSets the given alternative conditional schedule sets to add
   */
  public void addScheduleAlternatives(List conditionalScheduleSets) {
    scheduleAlternatives.add(conditionalScheduleSets);
  }
  
  //// Protected Area

  /** Sets the schedule alternaives, each of which is a list of conditional schedule sets.
   *
   * @param scheduleAlternatives the schedule alternaives, each of which is a list of conditional schedule sets
   */
  protected void setScheduleAlternatives (List scheduleAlternatives) {
    this.scheduleAlternatives = scheduleAlternatives;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the task name which is either an action name or a goal name */
  protected String taskName = "";

  /** the task identifier consisting of a unique id for each task commanded */
  protected UUID taskId;

  /** the action to be accomplished by this task */
  protected Action taskAction;
  
  /** the desired state to be acheived or maintained by this task */
  protected Goal taskGoal;

  /** the objects upon which the desired task is performed */
  protected List taskObjects = new ArrayList();

  /** the parameters that specify or modulate how the task should be performed */
  protected List taskParameters = new ArrayList();

  /** the constraints upon the performance of the task */
  protected List taskConstraints = new ArrayList();

  /** the schedule alternaives, each of which is a list of conditional schedule sets */
  protected List scheduleAlternatives = new ArrayList();
}