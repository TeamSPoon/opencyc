package org.opencyc.elf.bg.taskframe;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;

import org.doomdark.uuid.UUID;

import org.opencyc.elf.bg.state.State;

//// Internal Imports
import org.opencyc.elf.goal.Goal;


/**
 * <P>
 * TaskFrame specifies the materials, tools, and procedures to accomplish a
 * task.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class TaskFrame {
  //// Constructors

  /**
   * Creates a new instance of TaskFrame
   */
  public TaskFrame() {
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
    if (!(obj instanceof Class)) {
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
    stringBuffer.append("[");
    stringBuffer.append(taskName);
    stringBuffer.append(" goal: ");
    stringBuffer.append(taskGoal);
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Gets the task name from the library of tasks the system knows how to
   * perform.
   * 
   * @return taskName the task name from the library of tasks the system knows
   *         how to perform
   */
  public String getTaskName() {
    return taskName;
  }

  /**
   * Sets the task name from the library of tasks the system knows how to
   * perform.
   * 
   * @param taskName the task name from the library of tasks the system knows
   *        how to perform
   */
  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  /**
   * Gets the task identifier consisting of a unique id for each task
   * commanded, which provides a method for keeping track of tasks in a queue.
   * 
   * @return taskId the task identifier consisting of a unique id for each task
   *         commanded, which provides a method for keeping track of tasks in
   *         a queue
   */
  public UUID getTaskId() {
    return taskId;
  }

  /**
   * Sets the task identifier consisting of a unique id for each task
   * commanded, which provides a method for keeping track of tasks in a queue.
   * 
   * @param taskId the task identifier consisting of a unique id for each task
   *        commanded, which provides a method for keeping track of tasks in a
   *        queue
   */
  public void setTaskId(UUID taskId) {
    this.taskId = taskId;
  }

  /**
   * Gets the desired state to be acheived or maintained by this task
   * 
   * @return taskGoal the desired state to be acheived or maintained by this
   *         task
   */
  public Goal getTaskGoal() {
    return taskGoal;
  }

  /**
   * Sets the desired state to be acheived or maintained by this task
   * 
   * @param taskGoal the desired state to be acheived or maintained by this
   *        task
   */
  public void setTaskGoal(Goal taskGoal) {
    this.taskGoal = taskGoal;
  }

  /**
   * Gets the objects upon which the desired task is performed.
   * 
   * @return taskObjects the objects upon which the desired task is performed
   */
  public State getTaskObjects() {
    return taskObjects;
  }

  /**
   * Sets the objects upon which the desired task is performed.
   * 
   * @param taskObjects the objects upon which the desired task is performed
   */
  public void setTaskObjects(State taskObjects) {
    this.taskObjects = taskObjects;
  }

  /**
   * Gets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @return taskParameters the parameters that specify or modulate how the
   *         task should be performed
   */
  public State getTaskParameters() {
    return taskParameters;
  }

  /**
   * Sets the parameters that specify or modulate how the task should be
   * performed
   * 
   * @param taskParameters the parameters that specify or modulate how the task
   *        should be performed
   */
  public void setTaskParameters(State taskParameters) {
    this.taskParameters = taskParameters;
  }

  /**
   * Gets the agents (actuators) that are responsible for carrying out the task
   * 
   * @return agents the agents (actuators) that are responsible for carrying
   *         out the task
   */
  public ArrayList getAgents() {
    return agents;
  }

  /**
   * Sets the agents (actuators) that are responsible for carrying out the task
   * 
   * @param agents the agents (actuators) that are responsible for carrying out
   *        the task
   */
  public void setAgents(ArrayList agents) {
    this.agents = agents;
  }

  /**
   * Gets the tools, resources, conditions and state information, aside from
   * task objects, which are required to execute the task
   * 
   * @return taskRequirements
   */
  public State gettaskRequirements() {
    return taskRequirements;
  }

  /**
   * Sets the tools, resources, conditions and state information, aside from
   * task objects, which are required to execute the task
   * 
   * @param taskRequirements the tools, resources, conditions and state
   *        information, aside from task objects, which are required to
   *        execute the task
   */
  public void setXXX(State taskRequirements) {
    this.taskRequirements = taskRequirements;
  }

  /**
   * Gets the constraints upon the performance of the task
   * 
   * @return taskConstraints the constraints upon the performance of the task
   */
  public State getTaskConstraints() {
    return taskConstraints;
  }

  /**
   * Sets the constraints upon the performance of the task
   * 
   * @param taskConstraints the constraints upon the performance of the task
   */
  public void setTaskConstraints(State taskConstraints) {
    this.taskConstraints = taskConstraints;
  }

  /**
   * Gets the plans for accomplishing the task, or procedures for generating
   * plans, organized as a dictionary of execeptional states and associated
   * procedures for handling them
   * 
   * @return taskProcedures the plans for accomplishing the task, or procedures
   *         for generating plans, organized as a dictionary of execeptional
   *         states and associated procedures for handling them
   */
  public HashMap getTaskProcedures() {
    return taskProcedures;
  }

  /**
   * Sets the plans for accomplishing the task, or procedures for generating
   * plans, organized as a dictionary of execeptional states and associated
   * procedures for handling them
   * 
   * @param taskProcedures the plans for accomplishing the task, or procedures
   *        for generating plans, organized as a dictionary of execeptional
   *        states and associated procedures for handling them
   */
  public void setTaskProcedures(HashMap taskProcedures) {
    this.taskProcedures = taskProcedures;
  }

  //// Protected Area

  /** the task name from the library of tasks the system knows how to perform */
  protected String taskName;

  /**
   * the task identifier consisting of a unique id for each task commanded,
   * which provides a method for keeping track of tasks in a queue
   */
  protected UUID taskId;

  /** the desired state to be acheived or maintained by this task */
  protected Goal taskGoal;

  /** the objects upon which the desired task is performed */
  protected State taskObjects;

  /** the parameters that specify or modulate how the task should be performed */
  protected State taskParameters;

  /** the agents (actuators) that are responsible for carrying out the task */
  protected ArrayList agents = new ArrayList();

  /**
   * the tools, resources, conditions and state information, aside from task
   * objects, which are required to execute the task
   */
  protected State taskRequirements;

  /** the constraints upon the performance of the task */
  protected State taskConstraints;

  /**
   * the plans for accomplishing the task, or procedures for generating plans,
   * organized as a dictionary of execeptional states and associated
   * procedures for handling them
   */
  protected HashMap taskProcedures;

  //// Private Area
  //// Internal Rep
}