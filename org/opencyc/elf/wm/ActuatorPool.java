package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.Actuator;

//// External Imports
import java.util.HashMap;

/** ActuatorPool provides a store in which actuators can be retrieved
 * by name.  It is initially populated by the actuator factory.  There is a singleton instance
 * of actuator pool.
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
public class ActuatorPool {
  
  //// Constructors
  
  /** Creates a new instance of ActuatorPool and stores it in the singleton instance. */
  public ActuatorPool() {
    actuatorPool = this;
  }
  
  //// Public Area
  
  /** Gets the actuator library singleton instance.
   *
   * @return the actuator library singleton instance
   */
  public static ActuatorPool getInstance () {
    return actuatorPool;
  }
  
  /** Gets the actuator associated with the given actuator name.
   *
   * @param name the given actuator name
   * @return the actuator associated with the given actuator name
   */
  public Actuator getActuator (String name) {
    return (Actuator)  actuatorDictionary.get(name);
  }

  /** Sets the actuator associated with the given actuator name.
   *
   * @param name the given actuator name
   * @param actuator the given actuator
   */
  public void setActuator (String name, Actuator actuator) {
    actuatorDictionary.put(name, actuator);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the actuator library singleton instance */
  protected static ActuatorPool actuatorPool;
  
  /** the dictionary that associates a given actuator name with the actuator object */
  protected HashMap actuatorDictionary = new HashMap();
  
  //// Main
  
}
