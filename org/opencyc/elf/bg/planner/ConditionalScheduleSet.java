package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.predicate.PredicateExpression;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConditionalScheduleSet contains a predicate expression and a set of schedules.  One or
 * more conditional schedule sets are contained in the task frame as a list.  The job 
 * assigner iterates over this list and takes action on the first element whose predicate
 * expression evaluates to true.
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
public class ConditionalScheduleSet {
  
  //// Constructors
  
  /** 
   * Creates a new instance of ConditionalScheduleSet given the predicate expression and
   * schedule set. 
   *
   * @param predicateExpression the given predicate expression
   * @param scheduleSet the given schedule set
   */
  public ConditionalScheduleSet(PredicateExpression predicateExpression, List scheduleSet) {
    this.predicateExpression = predicateExpression;
    this.scheduleSet = scheduleSet;
  }
  
  //// Public Area
  
  /**
   * Creates and returns a copy of this object as initialized by the task frame factory
   * when the task frame library is populated.  The remaining instance variables in the
   * cloned task frame are set by the job assinger according to the commanded task.
   */
  public Object clone () {
    List clonedScheduleSet = new ArrayList();
    Iterator iter = scheduleSet.iterator();
    while (iter.hasNext()) {
      Schedule schedule = (Schedule) iter.next();
      clonedScheduleSet.add(schedule.clone());
    }
    return new ConditionalScheduleSet(predicateExpression, clonedScheduleSet);
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ConditionalScheduleSet condition: ");
    stringBuffer.append(predicateExpression.toString());
    stringBuffer.append(" scheduleSet: ");
    stringBuffer.append(scheduleSet);
    stringBuffer.append("]");

    return stringBuffer.toString();
  }
  
  /**
   * Gets  the predicate expression 
   *
   * @return  the predicate expression 
   */
  public PredicateExpression getPredicateExpression () {
    return predicateExpression;
  }

  /**
   * Gets the set of schedules in this schedule set, where each schedule is for a different actuator
   *
   * @return the set of schedules in this schedule set, where each schedule is for a different actuator
   */
  public List getScheduleSet () {
    return scheduleSet;
  }

  /**
   * Gets all names of the actuators that are responsible for carrying out the task
   * 
   * @return all names of the actuators that are responsible for carrying out the task
   */
  public List getActuatorNames () {
    List actuatorNames = new ArrayList();
    Iterator iter = scheduleSet.iterator();
    while (iter.hasNext()) {
      Schedule schedule = (Schedule) iter.next();
      if (! actuatorNames.contains(schedule.getActuatorName()))
        actuatorNames.add(schedule.getActuatorName());
    }
    return actuatorNames;
  }

  /**
   * Gets all the names of the sensors that are responsible for sensing phenomena related to the task
   * 
   * @return all the names of the sensors that are responsible for sensing phenomena related to the task
   */
  public List getSensorNames () {
    List sensorNames = new ArrayList();
    Iterator iter = scheduleSet.iterator();
    while (iter.hasNext()) {
      Schedule schedule = (Schedule) iter.next();
      if (! sensorNames.contains(schedule.getSensorName()))
        sensorNames.add(schedule.getSensorName());
    }
    return sensorNames;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the predicate expression */
  protected PredicateExpression predicateExpression;
  
  /** the set of schedules in this schedule set, where each schedule is for a different actuator */
  protected List scheduleSet = new ArrayList();
  
  //// Main
  
}
