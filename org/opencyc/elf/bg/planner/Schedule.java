package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Command;

import org.opencyc.elf.bg.predicate.PredicateExpression;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Schedule contains the timing specification for a plan.  It can be represented 
 * as a time-labeled or event-labeled sequence of actitivies or events.  The scheduler
 * processes its current job assignment by evaluating alternative schedule sets and choosing
 * the best set.  Each schedule in the schedule set has an associated predicate expression.
 * The scheduler passes to the executor the first schedule found that has a predicate expression 
 * of null.  In the event that the executor sends an exception status message back to the scheduler, 
 * the scheduler evaluates the non-null predicate expressions in turn and sends to the executor
 * the schedule whose associated predicate expression evaluates true. 
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

  /** Creates a new instance of Schedule given its contents.
   *
   * @param predicateExpression the predicate expression
   * @param plannedCommands the list of planned commands that accomplish the assigned action or achieve the assigned goal
   * @param plannedTimeMilliseconds the list of planned command times, each of which is the planned duration of time 
   * in milliseconds to elapse from the time the plan commences exectution until the planned command is acomplished
   * @param directActuatorName the name of the direct actuator that achieves or accomplishes the schedule
   * @param directSensorName he name of the direct sensor that senses the achievements or accomplishments of the schedule
   */
  public Schedule(PredicateExpression predicateExpression, 
                  List plannedCommands, 
                  List plannedTimeMilliseconds, 
                  String directActuatorName, 
                  String directSensorName) {
    this.predicateExpression = predicateExpression;
    this.plannedCommands = plannedCommands;
    this.directActuatorName = directActuatorName;
    this.directSensorName = directSensorName;
  }

  //// Public Area

  /** Gets the list of planned commands that accomplish the assigned action or achieve the assigned goal.
   *
   * @return the list of planned commands that accomplish the assigned action or achieve the assigned goal
   */
  public List getPlannedCommands () {
    return plannedCommands;
  }

  /** Gets the list of planned command times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned command is acomplished.
   *
   * @return the list of planned command times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned command is acomplished
   */
  public List getplannedTimeMilliseconds () {
    return plannedTimeMilliseconds;
  }

  /** Gets the name of the direct actuator that achieves or accomplishes the schedule
   *
   * @return the name of the direct actuator that achieves or accomplishes the schedule
   */
  public String getDirectActuatorName () {
    return directActuatorName;
  }

  /** Gets the name of the direct sensor that senses the achievements or accomplishments of the schedule
   *
   * @return the name of the direct sensor that senses the achievements or accomplishments of the schedule
   */
  public String getDirectSensorName () {
    return directSensorName;
  }

  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Schedule ");
    stringBuffer.append(plannedCommands.toString());
    stringBuffer.append(" when: ");
    stringBuffer.append(predicateExpression.toString());
    stringBuffer.append(" direct actuator: ");
    stringBuffer.append(directActuatorName);
    stringBuffer.append(" direct sensor: ");
    stringBuffer.append(directSensorName);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /*** Creates and returns a copy of this object.
   *
   * @return a copy of this object
   */
  public Object clone () {
    List clonedPlannedCommands = new ArrayList();
    Iterator iter = plannedCommands.iterator();
    while (iter.hasNext()) {
      Command command = (Command) iter.next();
      clonedPlannedCommands.add(command.clone());
    }
    Schedule schedule = new Schedule(predicateExpression, 
                                     clonedPlannedCommands, 
                                     plannedTimeMilliseconds, 
                                     directActuatorName, 
                                     directSensorName);
    
    return schedule;
  }
  
  /** Gets  the predicate expression 
   *
   * @return  the predicate expression 
   */
  public PredicateExpression getPredicateExpression () {
    return predicateExpression;
  }
  
  //// Protected Area

  //// Private Area

  //// Internal Rep

  /** the predicate expression */
  protected PredicateExpression predicateExpression;
  
  /** the list of planned commands that accomplish the assigned action or achieve the assigned goal */
  protected List plannedCommands = new ArrayList();
  
  /** the list of planned command times, each of which is the planned duration of time in milliseconds to
   * elapse from the time the plan commences exectution until the planned command is acomplished
   */
  protected List plannedTimeMilliseconds = new ArrayList();

  /** the name of the direct actuator that achieves or accomplishes the schedule */
  protected String directActuatorName = "";

  /** the name of the direct sensor that senses the achievements or accomplishments of the schedule */
  protected String directSensorName = "";
}