package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.state.State;


//// External Imports

/**
 * <P>
 * Plan contains the set of subtasks or subgoals that are designed to
 * accomplish the task or job, or a contains the sequence (or a set of
 * seequences for a set of agents) of subtasks intended to generate a series
 * of subgoals leading from the starting state to the goal state.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class Plan {
  //// Constructors

  /**
   * Creates a new instance of Plan
   */
  public Plan() {
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
    if (!(obj instanceof Plan)) {
      return false;
    }

    Plan thatPlan = (Plan) obj;

    return getOutputTimeTrajectoryOfMotion().equals(thatPlan.getOutputTimeTrajectoryOfMotion());
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Plan ");
    stringBuffer.append(getOutputTimeTrajectoryOfMotion().toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Gets the state of the plan
   * 
   * @return the state of the plan
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the state of the plan
   * 
   * @param state the state of the plan
   */
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Gets the value of the state variable for the output time-trajectory of
   * motion which constists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @return the output time-trajectory of motion
   */
  public Object[] getOutputTimeTrajectoryOfMotion() {
    return (Object[]) state.getStateValue(OUTPUT_TIME_TRAJECTORY_OF_MOTION);
  }

  /**
   * Sets the value of the state variable for the output time-trajectory of
   * motion which constists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @param outputTimeTrajectoryOfMotion the output time-trajectory of motion
   */
  public void setOutputTimeTrajectoryOfMotion(Object[] outputTimeTrajectoryOfMotion) {
    state.setStateValue(OUTPUT_TIME_TRAJECTORY_OF_MOTION, outputTimeTrajectoryOfMotion);
  }

  /**
   * Gets the value of the state variable for the trajectory of the action
   * vector, which consists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @return the trajectory of the action vector
   */
  public Object[] getActionVectorTrajectory() {
    return (Object[]) state.getStateValue(ACTION_VECTOR_TRAJECTORY);
  }

  /**
   * Sets the value of the state variable for the trajectory of the action
   * vector, which constists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @param actionVectorTrajectory the action vector trajectory
   */
  public void setActionVectorTrajectory(Object[] actionVectorTrajectory) {
    state.setStateValue(OUTPUT_TIME_TRAJECTORY_OF_MOTION, actionVectorTrajectory);
  }

  /**
   * Gets the value of the state variable for the trajectory of the input
   * control vector, which consists of a pair (Object[]) of ordered lists, the
   * first being a list of vectors and the second a list of time instances
   * 
   * @return the trajectory of the input control vector
   */
  public Object[] getInputContolVectorTrajectory() {
    return (Object[]) state.getStateValue(INPUT_CONTROL_VECTOR_TIME_TRAJECTORY);
  }

  /**
   * Sets the value of the state variable for the trajectory of the input
   * control vector, which consists of a pair (Object[]) of ordered lists, the
   * first being a list of vectors and the second a list of time instances
   * 
   * @param inputControlVectorTrajectory the trajectory of the input control
   *        vector
   */
  public void setInputControlVectorTrajectory(Object[] inputControlVectorTrajectory) {
    state.setStateValue(INPUT_CONTROL_VECTOR_TIME_TRAJECTORY, inputControlVectorTrajectory);
  }

  //// Protected Area

  /**
   * the state variable for the output time-trajectory of motion which
   * constists of a pair (Object[]) of ordered lists, the first being a list
   * of vectors and the second a list of time instances
   */
  protected static final String OUTPUT_TIME_TRAJECTORY_OF_MOTION = "outputTimeTrajectoryOfMotion";

  /**
   * the state variable for the trajectory of the action vector, which consists
   * of a pair (Object[]) of ordered lists, the first being a list of vectors
   * and the second a list of time instances
   */
  protected static final String ACTION_VECTOR_TRAJECTORY = "actionVectorTrajectory";

  /**
   * the state variable for the trajectory of the input control vector, which
   * consists of a pair (Object[]) of ordered lists, the first being a list of
   * vectors and the second a list of time instances
   */
  protected static final String INPUT_CONTROL_VECTOR_TIME_TRAJECTORY = "inputControlVectorTimeTrajectory";

  /** the state of the plan */
  protected State state;

  //// Private Area
  //// Internal Rep
}