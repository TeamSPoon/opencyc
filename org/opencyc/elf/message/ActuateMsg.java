package org.opencyc.elf.message;

//// Internal Imports

//// External Imports

/**
 * Provides the container for actuate message, that
 * is sent from an executor to an actuator.
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
public class ActuateMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of ActuateMsg. */
  public ActuateMsg() {
  }
  
  /**
   * Gets the object which is to be acted upon in the case where the actuator can act upon 
   * one of several objects, or null if the actuator can only act upon one object.
   *
   * @return the object which is to be acted upon in the case where the actuator can act upon 
   * one of several objects, or null if the actuator can only act upon one object
   */
  public Object getObj () {
    return obj;
  }

  /**
   * Sets the object which is to be acted upon in the case where the actuator can act upon 
   * one of several objects, or null if the actuator can only act upon one object.
   *
   * @param obj the object which is to be acted upon in the case where the actuator can act upon 
   * one of several objects, or null if the actuator can only act upon one object
   */
  public void setObj (Object obj) {
    this.obj = obj;
  }

  /**
   * Gets the action data specifying the parameters of the commanded action.
   *
   * @return the action data specifying the parameters of the commanded action
   */
  public Object getData () {
    return data;
  }

  /**
   * Sets the action data specifying the parameters of the commanded action.
   *
   * @param data the action data specifying the parameters of the commanded action
   */
  public void setData (Object data) {
    this.data = data;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the object which is to be acted upon in the case where the actuator can act upon 
   * one of several objects, or null if the actuator can only act upon one object.
   */  
  protected Object obj;
  
  /**
   * the action data specifying the parameters of the commanded action
   */  
  protected Object data;
  
  //// Main
}
