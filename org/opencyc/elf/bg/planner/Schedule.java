package org.opencyc.elf.bg.planner;

//// Internal Imports

//// External Imports
import java.util.ArrayList;

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
   * Gets the list of planned actions
   *
   * @return the list of planned actions
   */
  public ArrayList getPlannedActions () {
    return plannedActions;
  }

  /**
   * Sets the list of planned actions
   *
   * @param plannedActions the list of planned actions
   */
  public void setPlannedActions (ArrayList plannedActions) {
    this.plannedActions = plannedActions;
  }

  /**
   * Gets the list of planned goals
   *
   * @return the list of planned goals
   */
  public ArrayList getPlannedGoals () {
    return plannedGoals;
  }

  /**
   * Sets the list of planned goals
   *
   * @param plannedGoals the list of planned goals
   */
  public void setPlannedGoals (ArrayList plannedGoals) {
    this.plannedGoals = plannedGoals;
  }

  /**
   * Gets the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   *
   * @return the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   */
  public ArrayList getPlannedGoalTimeMilliseconds () {
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
  public void setPlannedGoalTimeMilliseconds (ArrayList plannedGoalTimeMilliseconds) {
    this.plannedGoalTimeMilliseconds = plannedGoalTimeMilliseconds;
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
      this.plannedGoalTimeMilliseconds.equals(that.plannedGoalTimeMilliseconds);
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
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  //// Protected Area

  /**
   * the list of planned actions
   */
  protected ArrayList plannedActions;
  
  /**
   * the list of planned goals
   */
  protected ArrayList plannedGoals;
  
  /**
   * the list of planned goal times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned goal is achieved
   */
  protected ArrayList plannedGoalTimeMilliseconds;

}