package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.state.State;

//// External Imports

/**
 * <P>
 * Schedule contains the timing specification for a plan.  It can be
 * represented as a time-labeled or event-labeled sequence of actitivies or
 * events.
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
public class Schedule {
  //// Constructors

  /**
   * Creates a new instance of Schedule
   */
  public Schedule() {
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
    if (!(obj instanceof Schedule)) {
      return false;
    }

    Schedule thatSchedule = (Schedule) obj;

    return getOutputTimeTrajectoryOfMotion().equals(thatSchedule.getOutputTimeTrajectoryOfMotion());
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Schedule ");
    stringBuffer.append(getOutputTimeTrajectoryOfMotion().toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Gets the state of the schedule
   * 
   * @return the state of the schedule
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the state of the schedule
   * 
   * @param state the state of the schedule
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
    return (Object[]) state.getStateValue(State.OUTPUT_TIME_TRAJECTORY_OF_MOTION);
  }

  /**
   * Sets the value of the state variable for the output time-trajectory of
   * motion which constists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @param outputTimeTrajectoryOfMotion the output time-trajectory of motion
   */
  public void setOutputTimeTrajectoryOfMotion(Object[] outputTimeTrajectoryOfMotion) {
    state.setStateValue(State.OUTPUT_TIME_TRAJECTORY_OF_MOTION, outputTimeTrajectoryOfMotion);
  }

  /**
   * Gets the value of the state variable for the trajectory of the action
   * vector, which consists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @return the trajectory of the action vector
   */
  public Object[] getActionVectorTrajectory() {
    return (Object[]) state.getStateValue(State.ACTION_VECTOR_TRAJECTORY);
  }

  /**
   * Sets the value of the state variable for the trajectory of the action
   * vector, which constists of a pair (Object[]) of ordered lists, the first
   * being a list of vectors and the second a list of time instances
   * 
   * @param actionVectorTrajectory the action vector trajectory
   */
  public void setActionVectorTrajectory(Object[] actionVectorTrajectory) {
    state.setStateValue(State.OUTPUT_TIME_TRAJECTORY_OF_MOTION, actionVectorTrajectory);
  }

  /**
   * Gets the value of the state variable for the trajectory of the input
   * control vector, which consists of a pair (Object[]) of ordered lists, the
   * first being a list of vectors and the second a list of time instances
   * 
   * @return the trajectory of the input control vector
   */
  public Object[] getInputContolVectorTrajectory() {
    return (Object[]) state.getStateValue(State.INPUT_CONTROL_VECTOR_TIME_TRAJECTORY);
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
    state.setStateValue(State.INPUT_CONTROL_VECTOR_TIME_TRAJECTORY, inputControlVectorTrajectory);
  }

  //// Protected Area

  /** the state of the plan */
  protected State state;

}