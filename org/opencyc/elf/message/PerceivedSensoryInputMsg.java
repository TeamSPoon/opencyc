package org.opencyc.elf.message;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

//// External Imports

/** Provides the container for the perceived input message, that
 * is sent from sensory processing.
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
public class PerceivedSensoryInputMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of PerceivedSensoryInputMsg. 
   *
   * @param sender the sender of the message
   * @param obj the object for which data is perceived
   * @param data the data associated with the object
   */
  public PerceivedSensoryInputMsg(NodeComponent sender, Object obj, Object data) {
    this.sender = sender;
    this.obj = obj;
    this.data = data;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ObservedInputMsg: ");
    stringBuffer.append(obj.toString());
    stringBuffer.append(" data: ");
    stringBuffer.append(data.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the object for which data is perceived.
   *
   * @return the object for which data is perceived
   */
  public Object getObj () {
    return obj;
  }

  /** Gets the perceived data associated with the object
   *
   * @return the perceived data associated with the object
   */
  public Object getData () {
    return data;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the object for which data is perceived */
  protected Object obj;
  
  /** the perceived data associated with the object */
  protected Object data;
  
  //// Main
  
}
