package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Schedule contains the timing specification for a plan.  It can be
 * represented as a time-labeled or event-labeled sequence of actitivies or
 * events.
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

  /** Creates a new instance of Schedule. */
  public Schedule() {
  }

  //// Public Area

  /**
   * Gets the schedule name or description
   *
   * @return the schedule name or description
   */
  public String getName () {
    return name;
  }

  /**
   * Sets the schedule name or description
   *
   * @param name the schedule name or description
   */
  public void setName (String name) {
    this.name = name;
  }

  /**
   * Gets the list of planned actions
   *
   * @return the list of planned actions
   */
  public List getPlannedActions () {
    return plannedActions;
  }

  /**
   * Sets the list of planned actions
   *
   * @param plannedActions the list of planned actions
   */
  public void setPlannedActions (List plannedActions) {
    this.plannedActions = plannedActions;
  }

  /**
   * Gets the list of planned goals
   *
   * @return the list of planned goals
   */
  public List getPlannedGoals () {
    return plannedGoals;
  }

  /**
   * Sets the list of planned goals
   *
   * @param plannedGoals the list of planned goals
   */
  public void setPlannedGoals (List plannedGoals) {
    this.plannedGoals = plannedGoals;
  }

  /**
   * Gets the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   *
   * @return the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   */
  public List getPlannedGoalTimeMilliseconds () {
    return plannedGoalTimeMilliseconds;
  }

  /**
   * Sets the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   *
   * @param plannedGoalTimeMilliseconds the list of planned goal times, each of which is the planned duration 
   * of time in milliseconds to elapse from the time the plan commences exectution until the planned goal is 
   * achieved
   */
  public void setPlannedGoalTimeMilliseconds (List plannedGoalTimeMilliseconds) {
    this.plannedGoalTimeMilliseconds = plannedGoalTimeMilliseconds;
  }

  /**
   * Gets the name of the actuator or virtual actuator that achieves or accomplishes the schedule
   *
   * @return the name of the actuator or virtual actuator that achieves or accomplishes the schedule
   */
  public String getActuatorName () {
    return actuatorName;
  }

  /**
   * Sets the name of the actuator or virtual actuator that achieves or accomplishes the schedule
   *
   * @param actuatorName the name of the actuator or virtual actuator that achieves or accomplishes the schedule
   */
  public void setActuatorName (String actuatorName) {
    this.actuatorName = actuatorName;
  }

  /**
   * Gets the name of the sensor or virtual sensor that senses the achievements or accomplishments of the schedule
   *
   * @return the name of the sensor or virtual sensor that senses the achievements or accomplishments of the schedule
   */
  public String getSensorName () {
    return sensorName;
  }

  /**
   * Sets the name of the sensor or virtual sensor that senses the achievements or accomplishments of the schedule
   *
   * @param sensorName the name of the sensor or virtual sensor that senses the achievements or accomplishments of the schedule
   */
  public void setSensorName (String sensorName) {
    this.sensorName = sensorName;
  }

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
    Schedule that = (Schedule) obj;
    return 
      this.plannedActions.equals(that.plannedActions) &&
      this.plannedGoals.equals(that.plannedGoals) &&
      this.plannedGoalTimeMilliseconds.equals(that.plannedGoalTimeMilliseconds) &&
      this.actuatorName.equals(that.actuatorName) &&
      this.sensorName.equals(that.sensorName);
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Schedule ");
    stringBuffer.append(plannedActions.toString());
    stringBuffer.append(" actuator: ");
    stringBuffer.append(actuatorName);
    stringBuffer.append(" sensor: ");
    stringBuffer.append(sensorName);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a copy of this object
   */
  public Object clone () {
    Schedule schedule = new Schedule();
    List clonedPlannedActions = new ArrayList();
    Iterator iter = plannedActions.iterator();
    while (iter.hasNext()) {
      Action action = (Action) iter.next();
      clonedPlannedActions.add((Action) action.clone());
    }
    schedule.setPlannedActions(clonedPlannedActions);
    schedule.setPlannedGoalTimeMilliseconds(plannedGoalTimeMilliseconds);
    schedule.setPlannedGoals(plannedGoals);
    return schedule;
  }
  
  //// Protected Area

  // the schedule name or description
  protected String name = "";
  
  /** the list of planned actions */
  protected List plannedActions = new ArrayList();
  
  /** the list of planned goals */
  protected List plannedGoals = new ArrayList();
  
  /**
   * the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   */
  protected List plannedGoalTimeMilliseconds = new ArrayList();

  /** the name of the actuator or virtual actuator that achieves or accomplishes the schedule */
  protected String actuatorName = "";

  /** the name of the sensor or virtual sensor that senses the achievements or accomplishments of the schedule */
  protected String sensorName = "";
}