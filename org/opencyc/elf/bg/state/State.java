package org.opencyc.elf.bg.state;

//// Internal Imports

//// External Imports
import java.util.Hashtable;
import java.util.Iterator;

/**
 * <P>
 * State provides the container for the list of stateVariable/values.
 * </p>
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
public class State {
  
  //// Constructors
  
  /**
   * Constructs a new instance of this object.
   */
  public State() {
    stateVariableDictionary = new Hashtable();
  }

  //// Public Area
    
  /**
   * Returns true if the given object equals this state.
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

  /**
   * Returns a string representation of this object.
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
      stringBuffer.append(value.toString());
      stringBuffer.append("]\n");
    }

    if (stringBuffer.charAt(stringBuffer.length() - 1) == '\n') {
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Returns a clone of this state.
   * 
   * @return a clone of this state
   */
  public Object clone() {
    State state = new State();

    Iterator iter = stateVariables();

    while (iter.hasNext()) {
      StateVariable stateVariable = (StateVariable) iter.next();
      Object value = null;
      try {
        value = ((State) getStateValue(stateVariable)).clone();
      }
      //TOTO replace with CloneNotSupportedException
       catch (Exception e) {
        value = getStateValue(stateVariable);
      }
      state.setStateValue(stateVariable, value);
    }
    return state;
  }

  /**
   * Returns an iterator over the state variables.
   * 
   * @return an iterator over the state variables
   */
  public Iterator stateVariables() {
    return new StateIterator(this);
  }

  /**
   * Returns true if the given object is a state variable of this state.
   * 
   * @param obj the given object
   * @return true if the given object is a state variable of this state
   */
  public boolean isStateVariable(Object obj) {
    return stateVariableDictionary.containsKey(obj);
  }

  /**
   * Sets the given state state variable to the given value.
   * 
   * @param stateVariable variable the state variable
   * @param value the stateVariable's value
   */
  public void setStateValue(StateVariable stateVariable, Object value) {
    stateVariableDictionary.put(stateVariable, value);
  }

  /**
   * Gets the value of the for the given  state variable.
   * 
   * @param stateVariable the states's stateVariable
   * 
   * @return the stateVariable for the given stateVariable
   */
  public Object getStateValue(Object stateVariable) {
    return stateVariableDictionary.get(stateVariable);
  }

  //// Protected Area

  //// Private Area
  
  //// Internal Rep
  
  /**
   * the state represented as a dictionary of concepts and a dictionary of
   * stateVariable/values.
   */
  protected Hashtable stateVariableDictionary;

  //// Main
  

}