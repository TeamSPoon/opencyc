package org.opencyc.elf.wm.state;

//// Internal Imports

//// External Imports

/**
 * StateVariable contains the name of an ELF state variable.  This class is required to
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
  
  /** the user input state variable name */ 
  public static String USER_INPUT = "userInput";
  
  /** the user prompt state variable name */ 
  public static String USER_PROMPT = "userPrompt";
  
  /** the console input state variable name */ 
  public static String CONSOLE_INPUT = "consoleInput";
  
  /** the console prompt state variable name */ 
  public static String CONSOLE_PROMPT = "consolePrompt";
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the state variable type */
  protected Class type;
  
  /** the state variable name */
  protected String name;
  
  /** the state variable comment */
  protected String comment;
  
  //// Main
  
}
