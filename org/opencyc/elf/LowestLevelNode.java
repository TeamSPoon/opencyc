package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.s.Sensor;

//// External Imports

/**
 * Provides the lowest level Node container for the Elementary Loop Functioning
 * (ELF).<br>
 * 
 * @version $Id: LowestLevelNode.java,v 1.4 2003/01/17 14:56:44 stephenreed Exp
 *          $
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
public class LowestLevelNode extends Node {

  //// Constructors
  
  /**
   * Constructs a new LowestLevelNode object.
   */
  public LowestLevelNode() {
  }

  //// Public Area
  
  /**
   * Gets the Actuator for this node
   * 
   * @return the Actuator for this node
   */
  public Actuator getActuator() {
    return actuator;
  }

  /**
   * Sets the Actuator for this node
   * 
   * @param actuator the Actuator for this node
   */
  public void setActuator(Actuator actuator) {
    this.actuator = actuator;
  }

  /**
   * Gets the Sensor for this node
   * 
   * @return the Sensor for this node
   */
  public Sensor getSensor() {
    return sensor;
  }

  /**
   * Sets the Sensor for this node
   * 
   * @param sensor the Sensor for this node
   */
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
  
  //// Protected Area
    
  //// Private Area
  
  //// Internal Rep
  
  /** the Actuator for this node */
  protected Actuator actuator;

  /** the Sensor for this node */
  protected Sensor sensor;
  
  //// Main
}