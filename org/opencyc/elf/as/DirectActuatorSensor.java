package org.opencyc.elf.as;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

//// External Imports
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;

/** DirectActuatorSensor is the abstract super class of all direct actuator-sensors. Each instance of
 * actuator-sensor is a tightly coupled (state-sharing) actuator and sensor in which the actuator's action 
 * causes a sensation to be observed by the sensor.  Chief among phenomena modeled by this class are software 
 * APIs in which an API request action results in an observed API response. 
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

public abstract class DirectActuatorSensor extends NodeComponent implements ActuatorSensor {
  
  //// Constructors
  
  /** Creates a new instance of DirectActuatorSensor given its name and
   * required resources.
   *
   * @param name tthe name of the actuator-sensor
   * @param resources the resources requried by this actuator-sensor
   * @param actuatorSensorChannel the takable channel from which messages are input
   */
  public DirectActuatorSensor(String name, 
                              List resources,
                              Takable actuatorSensorChannel) {
    this.name = name;
    this.resources = resources;
    this.actuatorSensorChannel = actuatorSensorChannel;
    logger = Logger.getLogger("org.opencyc.elf");
  }
  
   //// Public Area
  
  public Puttable getChannel() {
    return (Puttable) actuatorSensorChannel;
  }
  
  public String getName() {
    return name;
  }
  
  public java.util.List getResources() {
    return resources;
  }
  
  public java.util.List getSensationCapabilities() {
    return sensationCapabilities;
  }
  
  /** the Cyc API actuator-sensor name */
  public static final String CYC_API = "cyc api";
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of the actuator-sensor */
  protected String name;
  
  /** the resources requried by this actuator-sensor */
  protected List resources;
  
  /** the names of sensations that this actuator-sensor can sense */
  protected List sensationCapabilities;
  
  /** the takable channel from which messages are input */
  protected Takable actuatorSensorChannel = null;
    
  /** the executor of the consumer thread */
  protected Executor executor;
  
  /** the logger */
  protected static Logger logger;
  //// Main
  
}
