package org.opencyc.elf.s;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.sp.SensoryPerception;

//// External Imports
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Puttable;

/** DirectSensor is the abstract super class of all direct sensors, as opposed to
 * virtual sensors (sensory perception).
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
public abstract class DirectSensor extends NodeComponent  implements Sensor {
  
  //// Constructors
  
  /** Constructs a new DirectSensor object given its name.
   *
   * @param name the sensor name
   * @param resources the resources required by this sensor
   * @param sensationCapabilities the names of sensations that this sensor can sense
   */
  public DirectSensor (String name, List resources, List sensationCapabilities) {
    this.name = name;
    this.resources = resources;
    this.sensationCapabilities = sensationCapabilities;
    logger = Logger.getLogger("org.opencyc.elf");
  }

  
  //// Public Area
  
  /** Initializes this direct sensor instance with the given
   * output message channel.
   *
   * @param sensoryPerceptionChannel the puttable channel to which messages are output
   */
  public abstract void initialize (Puttable sensoryPerceptionChannel);
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[DirectSensor resources: ");
    stringBuffer.append(resources.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the name of the sensor.
   *
   * @return the name of the sensor
   */
  public String getName() {
    return name;
  }
  
  /** Gets the resources required by this sensor.
   *
   * @return the resources required by this sensor
   */
  public List getResources() {
    return resources;
  }
  
  /** Gets the names of sensations that this sensor can sense.
   *
   * @return the names of sensations that this sensor can sense
   */
  public List getSensationCapabilities() {
    return sensationCapabilities;
  }
  
  /** the console input sensor name */
  public static final String CONSOLE_INPUT = "console input";

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of the sensor */
  protected String name;
  
  /** the names of sensations that this sensor can sense */
  protected List sensationCapabilities;
  
  /** the resources required by this sensor */
  protected List resources;
  
  /** the logger */
  protected static Logger logger;
  
  //// Main
  
}
