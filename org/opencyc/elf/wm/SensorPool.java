package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.s.DirectSensor;

//// External Imports
import java.util.HashMap;

/** SensorPool provides a store in which sensors can be retrieved
 * by name.  It is initially populated by the sensor factory.  There is a singleton instance
 * of sensor factory.
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
public class SensorPool {
  
  //// Constructors
  
  /** Creates a new instance of SensorPool and stores it in the singleton instance. */
  public SensorPool() {
    sensorPool = this;
  }
  
  //// Public Area
  
  /** Gets the sensor library singleton instance.
   *
   * @return the sensor library singleton instance
   */
  public static SensorPool getInstance () {
    return sensorPool;
  }
  
  /** Gets the sensor associated with the given sensor name.
   *
   * @param name the given sensor name
   * @return the sensor associated with the given sensor name
   */
  public DirectSensor getSensor (String name) {
    return (DirectSensor)  sensorDictionary.get(name);
  }

  /** Sets the sensor associated with the given sensor name.
   *
   * @param name the given sensor name
   * @param sensor the given sensor
   */
  public void setSensor (String name, DirectSensor sensor) {
    sensorDictionary.put(name, sensor);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the sensor library singleton instance */
  protected static SensorPool sensorPool;
  
  /** the dictionary that associates a given sensor name with the sensor object */
  protected HashMap sensorDictionary = new HashMap();
  
  //// Main
  
}
