package org.opencyc.elf.bg.state;

//// Internal Imports

//// External Imports

/**
 * <P>StateVariable contains the name of an ELF state variable.  This class is required to
 * distinguish state variables from string literals used in goal predicate expressions.
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
public class StateVariable {
  
  //// Constructors
  
  /** 
   * Creates a new instance of StateVariable given its name.
   *
   * @param type the state variable type
   * @param name the state variable name
   * @param comment the comment
   */
  public StateVariable(Class type, String name, String comment) {
    this.type = type;
    this.name = name;
    this.comment = comment;
  }
  
  //// Public Area
  
  /**
   * Gets the state variable type
   *
   * @return the state variable type
   */
  public Class getType () {
    return type;
  }

  /**
   * Gets the state variable name
   *
   * @return the state variable name
   */
  public String getName () {
    return name;
  }

  /**
   * Gets the state variable comment
   *
   * @return the state variable comment
   */
  public String getComment () {
    return comment;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return name;
  }
  
  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof StateVariable)) {
      return false;
    }
    StateVariable that = (StateVariable) obj;
    return this.name.equals(that.name);
  }
  
  
  /** 
   * the console input state variable
   */
  public static StateVariable CONSOLE_INPUT;
  
  /** 
   * the console prompt state variable
   */
  public static StateVariable CONSOLE_PROMPT;
  
  /**
   * Initializes the state variables.
   */
  public static void initialize () {
    CONSOLE_INPUT = new StateVariable(String.class, "consoleInput", "the console input string");
    CONSOLE_PROMPT = new StateVariable(String.class, "consolePrompt", "the console prompt string");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the state variable type
   */
  protected Class type;
  
  /**
   * the state variable name
   */
  protected String name;
  
  /**
   * the state variable comment
   */
  protected String comment;
  
  //// Main
  
}
