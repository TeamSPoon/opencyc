package org.opencyc.elf.a;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

//// External Imports
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;

/** DirectActuator is the abstract super class of all direct actuators, as opposed to
 * virtual actuators (job assigner).
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
public abstract class DirectActuator extends NodeComponent implements Actuator {
  
  //// Constructors
  
  /** Creates a new instance of DirectActuator given its name and
   * required resources.
   *
   * @param name the actuator name
   * @param resources the resources requried by this actuator
   * @param actionCapabilities the actions that this actuator can accomplish
   */
  public DirectActuator(String name, 
                       List resources, 
                       List actionCapabilities) {
    this.name = name;
    this.resources = resources;
    this.actionCapabilities = actionCapabilities;
    logger = Logger.getLogger("org.opencyc.elf");
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ConsoleOutput resources: ");
    stringBuffer.append(resources.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   */
  public Puttable getChannel() {
    return (Puttable) actuatorChannel;
  }
  
  /** Gets the name of the actuator.
   *
   * @return the name of the actuator
   */
  public String getName() {
    return name;
  }
  
  /** Gets the resources requried by this actuator.
   *
   * @return the resources requried by this actuator
   */
  public List getResources() {
    return resources;
  }
  
  /** Gets the actions that this actuator can accomplish.
   *
   * @return the actions that this actuatorcan accomplish
   */
  public List getActionCapabilities() {
    return actionCapabilities;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of the actuator */
  protected String name;
  
  /** the names of actions that this actuator can accomplish */
  protected List actionCapabilities;
  
  /** the resources requried by this actuator */
  protected List resources;
  
  /** the takable channel from which messages are input */
  protected Takable actuatorChannel = null;
    
  /** the executor of the consumer thread */
  protected Executor executor;
  
  /** the logger */
  protected static Logger logger;
  
  //// Main
  
}
