package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.HashMap;

/** StateVariableLibrary contains the state variable dictionary.  The association of
 * state variable to state value is done by the State object.  There is a singleton instance
 * of the state variable library.
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
public class StateVariableLibrary {
  
  //// Constructors
  
  /** Creates a new instance of StateVariableLibrary and stores it in the singleton instance. */
  public StateVariableLibrary() {
    stateVariableLibrary = this;
  }
  
  //// Public Area
  
  /** Gets the state variable library singleton instance.
   *
   * @return the state variable library singleton instance
   */
  public static StateVariableLibrary getInstance () {
    return stateVariableLibrary;
  }
  
  /** Gets the state variable associated with the given state variable name.
   *
   * @param name the given state variable name
   * @return the state variable associated with the given state variable name
   */
  public StateVariable getStateVariable (String name) {
    return (StateVariable) stateVariableDictionary.get(name);
  }

  /** Sets the state variable associated with the given state variable name.
   *
   * @param name the given state variable name
   * @param stateVariable the given state variable
   */
  public void setStateVariable (String name, StateVariable stateVariable) {
    stateVariableDictionary.put(name, stateVariable);
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the state variable library singleton instance */
  protected static StateVariableLibrary stateVariableLibrary;
  
  /** the dictionary that associates a given state variable name with the state variable object */
  protected HashMap stateVariableDictionary = new HashMap();
  
  //// Main
  
}
