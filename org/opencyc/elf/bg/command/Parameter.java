package org.opencyc.elf.bg.command;

//// Internal Imports

//// External Imports

/**
 * Parameter contains the name and type of formal parameters for actions and goals.
 *
 * @version $Id$
 * @author  reed
 *
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
public class Parameter {
  
  //// Constructors
  
  /** Creates a new instance of Parameter given the name and type.
   *
   * @param name the parameter name
   * @param type the parameter type
   */
  public Parameter(String name, Class type) {
    this.name = name;
    this.type = type;
  }
  
  //// Public Area
  
  /**
   * Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Parameter ");
    stringBuffer.append(name);
    stringBuffer.append(" (");
    stringBuffer.append(type.toString());
    stringBuffer.append(")]");
    return stringBuffer.toString();
  }
  
  /**
   * Gets the parameter name.
   *
   * @return the parameter name
   */
  public String getName () {
    return name;
  }

  /**
   * Gets the parameter type.
   *
   * @return the parameter type
   */
  public Class getType () {
    return type;
  }

  /** the name of the prompt input parameter */
  public static final String PROMPT = "?PROMPT"; 
  
  /** the name of the user input parameter */
  public static final String USER_INPUT = "?USER-INPUT"; 
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the parameter name */
  protected final String name;
  
  /** the parameter type */
  protected final Class type;

}
