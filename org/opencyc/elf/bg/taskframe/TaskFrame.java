package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.goal.Goal;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;

import org.doomdark.uuid.UUID;

/**
 * TaskFrame specifies the materials, tools, and procedures to accomplish a task.  Generaic task
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

  /**
   * Creates a new instance of TaskFrame and gives it a unique ID.
   */
  public TaskFrame () {
  }

  //// Public Area

  /**
   * Creates and returns a copy of this object as initialized by the task frame factory
   * when the task frame library is populated.  The remaining instance variables in the
   * cloned task frame are set by the job assinger according to the commanded task.
   */
  public Object clone () {
    TaskFrame taskFrame = new TaskFrame();
    taskFrame.setTaskName(taskName);
    ArrayList clonedScheduleInfos = new ArrayList();
    Iterator iter = scheduleInfos.iterator();
    while (iter.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) iter.next();
      clonedScheduleInfos.add((ScheduleInfo) scheduleInfo.clone());
    }
    taskFrame.setScheduleInfos(clonedScheduleInfos);
    taskFrame.setTaskAction((Action) taskAction.clone());
    taskFrame.setTaskGoal(taskGoal);    
    return taskFrame;
  }
  
  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof TaskFrame)) {
      return false;
    }
    TaskFrame that = (TaskFrame) obj;
    return this.taskId.equals(that.taskId);
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[");
    stringBuffer.append(taskName);
    stringBuffer.append(" goal: ");
    stringBuffer.append(taskGoal);
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Gets the task name which is either an action name or a goal name.
   * 
   * @return the task name which is either an action name or a goal name
   */
  public String getTaskName () {
    return taskName;
  }

  /**
   * Sets the task name which is either an action name or a goal name.
   * 
   * @param taskName the task name which is either an action name or a goal name
   */
  public void setTaskName (String taskName) {
    this.taskName = taskName;
  }

  /**
   * Generates a unique identifier for this task frame.
   */
  public void generateTaskId () {
    taskId = new UUID();
  }
  
  /**
   * Gets the task identifier consisting of a unique id for each task
   * commanded.
   * 
   * @return the task identifier consisting of a unique id for each task
   *         commanded
   */
  public UUID getTaskId () {
    return taskId;
  }

  /**
   * Gets the action to be accomplished by this task
   *
   * @return the action to be accomplished by this task
   */
  public Action getTaskAction () {
    return taskAction;
  }

  /**
   * Sets the action to be accomplished by this task
   *
   * @param taskAction the action to be accomplished by this task
   */
  public void setTaskAction (Action taskAction) {
    this.taskAction = taskAction;
  }

  /**
   * Gets the desired state to be acheived or maintained by this task
   * 
   * @return the desired state to be acheived or maintained by this
   *         task
   */
  public Goal getTaskGoal () {
    return taskGoal;
  }

  /**
   * Sets the desired state to be acheived or maintained by this task
   * 
   * @param taskGoal the desired state to be acheived or maintained by this
   *        task
   */
  public void setTaskGoal (Goal taskGoal) {
    this.taskGoal = taskGoal;
  }

  /**
   * Gets the objects upon which the desired task is performed.
   * 
   * @return taskObjects the objects upon which the desired task is performed
   */
  public ArrayList getTaskObjects () {
    return taskObjects;
  }

  /**
   * Sets the objects upon which the desired task is performed.
   * 
   * @param taskObjects the objects upon which the desired task is performed
   */
  public void setTaskObjects (ArrayList taskObjects) {
    this.taskObjects = taskObjects;
  }

  /**
   * Gets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @return the parameters that specify or modulate how the
   *         task should be performed
   */
  public ArrayList getTaskParameters () {
    return taskParameters;
  }

  /**
   * Sets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @param taskParameters the parameters that specify or modulate how the task
   *        should be performed
   */
  public void setTaskParameters (ArrayList taskParameters) {
    this.taskParameters = taskParameters;
  }

  /**
   * Gets all the actuators that are responsible for carrying out the task
   * 
   * @return all the actuators that are responsible for carrying
   *         out the task
   */
  public ArrayList getActuators () {
    ArrayList actuators = new ArrayList();
    Iterator iter = scheduleInfos.iterator();
    while (iter.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) iter.next();
      actuators.addAll(scheduleInfo.actuators);
    }
    return actuators;
  }

  /**
   * Gets all the resources which are required to execute the task.
   * 
   * @return all the resources which are required to execute the task
   */
  public ArrayList getTaskResources () {
    ArrayList taskResources = new ArrayList();
    Iterator iter = scheduleInfos.iterator();
    while (iter.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) iter.next();
      taskResources.addAll(scheduleInfo.resources);
    }
    return taskResources;
  }

  /**
   * Gets the constraints upon the performance of the task
   * 
   * @return the constraints upon the performance of the task
   */
  public ArrayList getTaskConstraints () {
    return taskConstraints;
  }

  /**
   * Sets the constraints upon the performance of the task
   * 
   * @param taskConstraints the constraints upon the performance of the task
   */
  public void setTaskConstraints (ArrayList taskConstraints) {
    this.taskConstraints = taskConstraints;
  }

  /**
   * Gets the plans for accomplishing the task, or procedures for generating
   * plans, organized as a dictionary of execeptional states and associated
   * procedures for handling them
   * 
   * @return the plans for accomplishing the task, or procedures
   *         for generating plans, organized as a dictionary of execeptional
   *         states and associated procedures for handling them
   */
  public ArrayList getScheduleInfos () {
    return scheduleInfos;
  }

  /**
   * Sets the plans for accomplishing the task, or procedures for generating
   * plans, organized as a dictionary of execeptional states and associated
   * procedures for handling them
   * 
   * @param schedule the schedule of actions
   * @param resources the resources required by the schedule
   * @param actuators the actuators or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
   */
  public void addScheduleInfo (Schedule schedule,
                               ArrayList resources,
                               ArrayList actuators) {
    ScheduleInfo scheduleInfo = new ScheduleInfo (schedule, resources, actuators);
    scheduleInfos.add(scheduleInfo);
  }

  /**
   * ScheduleInfo contains the information about one schedule that contributes to achieving the
   * task goal or to accomplishing the task action.
   */
  public class ScheduleInfo {
    
    /**
     * Constructs a new ScheduleInfo object given the schedule, resources and actuators.
     *
     * @param schedule the schedule of actions
     * @param resources the resources required by the schedule
     * @param actuators the actuators or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
     */
    protected ScheduleInfo (Schedule schedule,
                  ArrayList resources,
                  ArrayList actuators) {
      this.schedule = schedule;
      this.resources = resources;
      this.actuators = actuators;
    }
    
    /**
     * Creates and returns a copy of this object.
     */
    public Object clone () {
      return new ScheduleInfo((Schedule) schedule.clone(),
                              resources,
                              actuators);
    }
  
    /**
     * Gets the schedule of actions
     *
     * @return the schedule of actions
     */
    public Schedule getSchedule () {
      return schedule;
    }

    /**
     * Gets the resources required by the schedule
     *
     * @return the resources required by the schedule
     */
    public ArrayList getResources () {
      return resources;
    }

    /**
     * Gets the actuators or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
     *
     * @return the actuators or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
     */
    public ArrayList getActuators () {
      return actuators;
    }
    
    /** the schedule of actions */
    public Schedule schedule;
    
    /** the resources required by the schedule */
    public ArrayList resources;
    
    /** the actuators or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule */
    public ArrayList actuators;
    
  }
  
  //// Protected Area

  /**
   * Sets the schedule info objects, each containing a schedule, resources and actuator
   *
   * @param scheduleInfos the schedule info objects, each containing a schedule, resources and actuator
   */
  protected void setScheduleInfos (ArrayList scheduleInfos) {
    this.scheduleInfos = scheduleInfos;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the task name which is either an action name or a goal name */
  protected String taskName;

  /** the task identifier consisting of a unique id for each task commanded */
  protected UUID taskId;

  /** the action to be accomplished by this task */
  protected Action taskAction;
  
  /** the desired state to be acheived or maintained by this task */
  protected Goal taskGoal;

  /** the objects upon which the desired task is performed */
  protected ArrayList taskObjects = new ArrayList();

  /** the parameters that specify or modulate how the task should be performed */
  protected ArrayList taskParameters = new ArrayList();

  /** the constraints upon the performance of the task */
  protected ArrayList taskConstraints = new ArrayList();

  /** the schedule info objects, each containing a schedule, resources and actuator */
  protected ArrayList scheduleInfos = new ArrayList();

}