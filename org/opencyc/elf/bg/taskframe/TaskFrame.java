package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.goal.Goal;

import org.opencyc.elf.s.Sensor;

//// External Imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;

/**
 * TaskFrame specifies the materials, tools, and procedures to accomplish a task.  Generic task
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

  /**
   * Creates and returns a copy of this object as initialized by the task frame factory
   * when the task frame library is populated.  The remaining instance variables in the
   * cloned task frame are set by the job assinger according to the commanded task.
   */
  public Object clone () {
    TaskFrame taskFrame = new TaskFrame();
    taskFrame.setTaskName(taskName);
    List clonedScheduleInfos = new ArrayList();
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
    stringBuffer.append("[TaskFrame task: ");
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
    if (taskName.length() != 0)
      return taskName;
    HashSet scheduleNames = new HashSet();
    Iterator scheduleInfoIterator = scheduleInfos.iterator();
    while (scheduleInfoIterator.hasNext()) {
      TaskFrame.ScheduleInfo scheduleInfo = 
        (TaskFrame.ScheduleInfo) scheduleInfoIterator.next();
      scheduleNames.add(scheduleInfo.getSchedule().getName());
    }
    StringBuffer stringBuffer = new StringBuffer();
    Iterator scheduleNameIterator = scheduleNames.iterator();
    while (scheduleNameIterator.hasNext()) {
      stringBuffer.append(scheduleNameIterator.next().toString());
      stringBuffer.append(" / ");
    }
    if (stringBuffer.length() > 0)
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    return stringBuffer.toString();
  }

  /**
   * Sets the task name which is either an action name or a goal name.
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
  
  /**
   * Gets the task identifier consisting of a unique id for each task commanded.
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
  public List getTaskObjects () {
    return taskObjects;
  }

  /**
   * Sets the objects upon which the desired task is performed.
   * 
   * @param taskObjects the objects upon which the desired task is performed
   */
  public void setTaskObjects (List taskObjects) {
    this.taskObjects = taskObjects;
  }

  /**
   * Gets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @return the parameters that specify or modulate how the
   *         task should be performed
   */
  public List getTaskParameters () {
    return taskParameters;
  }

  /**
   * Sets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @param taskParameters the parameters that specify or modulate how the task
   *        should be performed
   */
  public void setTaskParameters (List taskParameters) {
    this.taskParameters = taskParameters;
  }

  /**
   * Gets all the actuators that are responsible for carrying out the task
   * 
   * @return all the actuators that are responsible for carrying
   *         out the task
   */
  public List getActuators () {
    List actuators = new ArrayList();
    Iterator iter = scheduleInfos.iterator();
    while (iter.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) iter.next();
      if (! actuators.contains(scheduleInfo.actuator))
        actuators.add(scheduleInfo.actuator);
    }
    return actuators;
  }

  /**
   * Gets all the sensors that are responsible for sensing phenomena related to the task
   * 
   * @return all the sensors that are responsible for sensing phenomena related to the task
   */
  public List getSensors () {
    List sensors = new ArrayList();
    Iterator iter = scheduleInfos.iterator();
    while (iter.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) iter.next();
      if (! sensors.contains(scheduleInfo.sensor))
        sensors.add(scheduleInfo.sensor);
    }
    return sensors;
  }

  /**
   * Gets all the resources which are required to execute the task.
   * 
   * @return all the resources which are required to execute the task
   */
  public List getTaskResources () {
    List taskResources = new ArrayList();
    Iterator scheduleIterator = scheduleInfos.iterator();
    while (scheduleIterator.hasNext()) {
      ScheduleInfo scheduleInfo = (ScheduleInfo) scheduleIterator.next();
      Iterator resourceIterator = scheduleInfo.getResources().iterator();
      while (resourceIterator.hasNext()) {
        Resource resource = (Resource) resourceIterator.next();
        if (! taskResources.contains(resource))
          taskResources.add(resource);
      }
    }
    return taskResources;
  }

  /**
   * Gets the constraints upon the performance of the task
   * 
   * @return the constraints upon the performance of the task
   */
  public List getTaskConstraints () {
    return taskConstraints;
  }

  /**
   * Sets the constraints upon the performance of the task
   * 
   * @param taskConstraints the constraints upon the performance of the task
   */
  public void setTaskConstraints (List taskConstraints) {
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
  public List getScheduleInfos () {
    return scheduleInfos;
  }

  /**
   * Sets the plans for accomplishing the task, or procedures for generating
   * plans, organized as a dictionary of execeptional states and associated
   * procedures for handling them
   * 
   * @param schedule the schedule of actions
   * @param actuator the actuator or virtual actuators(a lower level ELF) that achieves or accomplishes the schedule
   * @param sensor the sensor or virtual sensor (a lower level ELF) that senses the achievements or accomplishments 
   * of the schedule
   */
  public void addScheduleInfo (Schedule schedule,
                               Actuator actuator,
                               Sensor sensor) {
    ScheduleInfo scheduleInfo = new ScheduleInfo (schedule, actuator, sensor);
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
     * @param actuator the actuator or virtual actuators(a lower level ELF) that achieves or accomplishes the schedule
     * @param sensor the sensor or virtual sensor (a lower level ELF) that senses the achievements or accomplishments 
     * of the schedule
     */
    protected ScheduleInfo (Schedule schedule,
                  Actuator actuator,
                  Sensor sensor) {
      this.schedule = schedule;
      this.actuator = actuator;
      this.sensor = sensor;
    }
    
    /** Creates and returns a copy of this object. */
    public Object clone () {
      return new ScheduleInfo((Schedule) schedule.clone(),
                              actuator,
                              sensor);
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
    public List getResources () {
      List resources = new ArrayList();
      resources.add(actuator.getResources());
      Iterator iter = sensor.getResources().iterator();
      while (iter.hasNext()) {
        Resource resource = (Resource) iter.next();
        if (! resources.contains(resource))
          resources.add(resource);
      }
      return resources;
    }

    /**
     * Gets the actuator or virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
     *
     * @return the actuatorsor virtual actuators (a lower level ELF) that achieves or accomplishes the schedule
     */
    public Actuator getActuator () {
      return actuator;
    }
    
    /**
     * Gets the sensor or virtual sensor (a lower level ELF) that senses the achievements or accomplishments 
     * of the schedule
     *
     * @return the sensor or virtual sensor (a lower level ELF) that senses the achievements or accomplishments 
     * of the schedule
     */
    public Sensor getSensor () {
      return sensor;
    }
    
    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[ScheduleInfo schedule: ");
      stringBuffer.append(schedule.toString());
      stringBuffer.append(" actuator: ");
      stringBuffer.append(actuator.toString());
      stringBuffer.append(" sensor: ");
      stringBuffer.append(sensor.toString());
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
    
    /** the schedule of actions */
    protected Schedule schedule;
    
    /** the actuator or virtual actuator (a lower level ELF) that achieves or accomplishes the schedule */
    protected Actuator actuator;
    
    /** 
     * the sensor or virtual sensor (a lower level ELF) that senses the achievements or accomplishments 
     * of the schedule 
     */
    protected Sensor sensor;
  }
  
  //// Protected Area

  /**
   * Sets the schedule info objects, each containing a schedule, resources, actuator and sensor
   *
   * @param scheduleInfos the schedule info objects, each containing a schedule, resources, actuator and sensor
   */
  protected void setScheduleInfos (List scheduleInfos) {
    this.scheduleInfos = scheduleInfos;
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

  /** the schedule info objects, each containing a schedule, resources and actuator */
  protected List scheduleInfos = new ArrayList();

}