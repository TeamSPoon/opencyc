package org.opencyc.elf.wm.state;

//// Internal Imports
import org.opencyc.cycobject.CycList;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;

//// External Imports
import java.util.Hashtable;
import java.util.Iterator;

/** State provides the container for the list of stateVariable/values, and states form 
 * a binding stack within the node hierarchy.
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
public class State extends NodeComponent {
  
  //// Constructors
  
  /** Constructs a new instance of this object. */
  public State(Node node) {
    this.node = node;
    stateVariableDictionary = new Hashtable();
  }

  //// Public Area
    
  /** Returns true if the given object equals this state.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this state
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof State))
      return false;
    State that = (State) obj;
    return this.stateVariableDictionary.equals(that.stateVariableDictionary);
  }

  /** Returns a hash code value for the object. 
   *
   * @return a hash code value for the object
   */
  public int hashCode() {
    return this.stateVariableDictionary.hashCode();
  }
  
  /** Returns a string representation of this object.
   * 
   * @return string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[State :\n");
    Iterator iter = stateVariableDictionary.keySet().iterator();

    while (iter.hasNext()) {
      stringBuffer.append("  [State Variable: ");

      Object stateVariable = iter.next();
      stringBuffer.append(stateVariable.toString());
      stringBuffer.append(": ");

      Object value = stateVariableDictionary.get(stateVariable);
      if (value instanceof String) {
        stringBuffer.append('"');
        stringBuffer.append(value);
        stringBuffer.append('"');
      }
      else if (value instanceof CycList)
        stringBuffer.append(((CycList) value).cyclify());
      else
        stringBuffer.append(value.toString());
      stringBuffer.append("]\n");
    }

    if (stringBuffer.charAt(stringBuffer.length() - 1) == '\n') {
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /** Returns true if the given object is a state variable of this state.
   * 
   * @param obj the given object
   * @return true if the given object is a state variable of this state
   */
  public boolean isStateVariable(Object obj) {
    if (stateVariableDictionary.containsKey(obj))
      return true;
    if (node == null || node.getParentNode() == null)
      return false;
    else
      return node.getParentNode().getWorldModel().getState().isStateVariable(obj);
  }

  /** Sets the given state state variable to the given value.
   * 
   * @param stateVariable variable the state variable
   * @param value the stateVariable's value
   */
  public void setStateValue(StateVariable stateVariable, Object value) {
    if (stateVariable.getType().isInstance(value))
      stateVariableDictionary.put(stateVariable, value);
    else
      throw new IllegalArgumentException(value + " is not an instance of " + stateVariable.getType());
  }

  /** Gets the value of the for the given  state variable.
   * 
   * @param stateVariable the states's stateVariable
   * 
   * @return the stateVariable for the given stateVariable
   */
  public Object getStateValue(Object stateVariable) {
    if (stateVariableDictionary.containsKey(stateVariable))
      return stateVariableDictionary.get(stateVariable);
    if (node == null || node.getParentNode() == null)
      return null;
    else
      return node.getParentNode().getWorldModel().getState().getStateValue(stateVariable);
  }

  //// Protected Area

  //// Private Area
  
  //// Internal Rep
  
  /** the state represented as a dictionary of concepts and a dictionary of
   * stateVariable/values.
   */
  protected Hashtable stateVariableDictionary;

  //// Main
  

}