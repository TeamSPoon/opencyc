package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.Schedule;

//// External Imports
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/** ScheduleLibrary provides a store in which schedules sets can be retrieved
 * by name.  It is initially populated by the schedule factory.  There is a singleton 
 * instance of schedule libary.
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
  
  /** Creates a new instance of ScheduleLibrary and stores it in the singleton instance. */
  public ScheduleLibrary() {
    scheduleLibrary = this;
  }
  
  //// Public Area
  
  /** Gets the schedule library singleton instance.
   *
   * @return the schedule library singleton instance
   */
  public static ScheduleLibrary getInstance () {
    return scheduleLibrary;
  }
  
  /** Gets the schedules associated with the given job name.
   *
   * @param jobName the given job name
   * @return the schedules associated with the given job name
   */
  public List getSchedules (String jobName) {
    return (List) scheduleDictionary.get(jobName);
  }

  //// Protected Area
  
  /** Adds the given schedule to the set of schedules associated with the given job name.
   *
   * @param jobName the given action name
   * @param action the given action
   */
  protected void addSchedule (String jobName, Schedule schedule) {
    List schedules = (List) scheduleDictionary.get(schedule);
    if (schedules == null)
      schedules = new ArrayList();
    schedules.add(schedule);
    scheduleDictionary.put(jobName, schedules);
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the schedule library singleton instance */
  protected static ScheduleLibrary scheduleLibrary;
  
  /** the dictionary that associates a given command name with the set of schedules that accomplish it */
  protected HashMap scheduleDictionary = new HashMap();
  
  //// Main
  
}
