package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.ActionFactory;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ScheduleLibrary provides the timing specification for a plan given the job to
 * perform.  There is a singleton instance.
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
public class ScheduleLibrary {
  
  //// Constructors
  
  /** Creates a new instance of ScheduleLibrary. */
  public ScheduleLibrary() {
    scheduleLibrary = this;
  }
  
  //// Public Area
  
  /**
   * Gets the singleton schedule library instance.
   *
   * @return the singleton schedule library instance
   */
  public static ScheduleLibrary getInstance () {
    return scheduleLibrary;
  }
  
  /**
   * Initializes the schedule library.
   */
  public void initialize() {
    // 
    Schedule schedule = new Schedule();
    ActionFactory actionFactory = new ActionFactory();
    
    // convserse with user --> console prompted input
    ArrayList plannedActions = new ArrayList();
    plannedActions.add(actionFactory.makeConsolePromptedInput());
    schedule.setPlannedActions(plannedActions);
    ArrayList plannedGoals = new ArrayList();
    addSchedule(Action.CONVERSE_WITH_USER, schedule);
  }
  
  /**
   * Gets the list of schedules that accomplish the given action name.
   *
   * @param actionName the given action name
   * @return the list of schedules that accomplish the given action name
   */
  public ArrayList getSchedules (String actionName) {
    ArrayList schedules = (ArrayList) scheduleDictionary.get(actionName);
    if (schedules == null)
      return new ArrayList();
    else
      return schedules;
  }
  
  //// Protected Area
   
  /**
   * Sets the list of schedules that accomplish the given action name.
   *
   * @param actionName the given action name
   * @param schedules the list of schedules that accomplish the given action name
   */
  protected void setSchedules (String actionName, ArrayList schedules) {
    scheduleDictionary.put(actionName, schedules);
  }

  /**
   * Adds a schedule to the list of schedules that accomplish the given action name.
   *
   * @param actionName the given action name
   * @param schedule the schedule that accomplishes the given action name
   */
  protected void addSchedule (String actionName, Schedule schedule) {
    ArrayList schedules = (ArrayList) scheduleDictionary.get(actionName);
    if (schedules == null)
      schedules = new ArrayList();
    schedules.add(schedule);
    scheduleDictionary.put(actionName, schedules);
  }

  //// Private Area
  
  //// Internal Rep
  
  /**
   * the dictionary that associates a given action name with the list of schedules that
   * accomplish it
   */
  protected HashMap scheduleDictionary = new HashMap();
  
  /**
   * the singleton schedule library instance
   */
  protected static ScheduleLibrary scheduleLibrary;
  
  //// Main
  
}
