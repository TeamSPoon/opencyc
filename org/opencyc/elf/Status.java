package org.opencyc.elf;

//// Internal Imports

//// External Imports
import java.util.HashMap;

/** Status contains status information.  A dictinary is employed to economise on the number
 * of instance variables that otherwise would be required.
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
public class Status {
  
  //// Constructors
  
  /** Creates a new instance of Status. */
  public Status() {
  }
  
  //// Public Area

  /** Gets a value from the status dictionary given the key.  If the value is not present
   * then null is returned
   *
   * @param key the key for which a value (if present) will be returned
   * @return a value from the status dictionary given the key.  If the value is not present
   * then null is returned
   */
  public Object getValue (Object key) {
    return statusDictionary.get(key);
  }

  /** Returns true if the given key has any value in the status dictionary.
   *
   * @param key the given key
   * @return true if the given key has any value in the status dictionary
   */
  public boolean isTrue (Object key) {
    return statusDictionary.get(key) != null;
  }

  /** Puts the given key / value pair into the status dictionary.
   *
   * @param key the given key
   * @param value the given value
   */
  public void setValue (Object key, Object value) {
    statusDictionary.put(key, value);
  }

  /** Sets the given key as true in the status dictionary.
   * 
   * @param key the given key
   */
  public void setTrue (Object key) {
    statusDictionary.put(key, Boolean.TRUE);
  }
  
  /** When present, indicates to the job assigner that the reporting scheduler has finished
   * the assigned job.
   */
  public static String JOB_FINISHED = "job finished";
  
  /** When present, indicates to the job assigner that the reporting scheduler has stopped
   * the assigned job as directed.
   */
  public static String JOB_STOPPED = "job stopped";
  
  /** When present, indicates to the scheduler that the reporting executor has finished
   * executing the schedule.
   */
  public static String SCHEDULE_FINISHED = "schedule finished";
  
  /** When present, indicates to the scheduler that the reporting executor has stopped
   * the current schedule as directed.
   */
  public static String SCHEDULE_STOPPED = "schedule stopped";
  
  /** When present, indicates to the higher-level executor that the reporting job assigner 
   * or actuator has finished the commanded task.
   */
  public static String COMMAND_FINISHED = "command finished";
  
  /** When present, indicates to the higher-level executor that the reporting job assigner 
   * or actuator has stopped the commanded task as directed.
   */
  public static String COMMAND_STOPPED = "command stopped";
  
  /** When present, indicates to the receiver that the sender directly or indirectly
   * detected an exceptional condition without handling it.
   */
  public static String EXCEPTION = "exception";
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the status dictionary **/
  protected HashMap statusDictionary = new HashMap();
  
  //// Main

  
}
