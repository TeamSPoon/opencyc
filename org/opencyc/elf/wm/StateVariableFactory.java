package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.state.StateVariable;

//// External Imports

/**
 * StateVariableLibrary populates the state variable library.  There is a singleton instance.
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
public class StateVariableFactory {
  
  //// Constructors
  
  /** Creates a new instance of StateVariableFactory and stores it in the singleton instance. */
  public StateVariableFactory() {
    stateVariableFactory = this;
  }
  
  //// Public Area
  
  /**
   * Gets the state variable factory singleton instance.
   *
   * @return the state variable factory singleton instance
   */
  public static StateVariableFactory getInstance () {
    return stateVariableFactory;
  }
  
  /**
   * Populates the state variable libarary.
   */
  public void populateStateVariableLibrary () {
    StateVariable stateVariable = new StateVariable(String.class,
                                                    StateVariable.USER_PROMPT,
                                                    "the user prompt string");
    StateVariableLibrary.getInstance().setStateVariable(stateVariable.getName(), stateVariable);
    stateVariable = new StateVariable(String.class,
                                      StateVariable.USER_INPUT,
                                      "the user input string");
    StateVariableLibrary.getInstance().setStateVariable(stateVariable.getName(), stateVariable);
    stateVariable = new StateVariable(String.class,
                                      StateVariable.CONSOLE_PROMPT,
                                      "the console prompt string");
    StateVariableLibrary.getInstance().setStateVariable(stateVariable.getName(), stateVariable);
    stateVariable = new StateVariable(String.class,
                                      StateVariable.CONSOLE_INPUT,
                                      "the console input string");
    StateVariableLibrary.getInstance().setStateVariable(stateVariable.getName(), stateVariable);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the state variable factory singleton instance */
  protected static StateVariableFactory stateVariableFactory;
  
  //// Main
  
}
