package org.opencyc.elf.bg.predicate;

//// Internal Imports
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.List;

/** Equals is a predicate of arity two that returns true if its arguments
 * are equal to each other.
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
public class Equals extends Predicate {
  
  //// Constructors
  
  /** Creates a new instance of Equals */
  public Equals() {
    super();
  }
  
  //// Public Area
    
  /** Evaluates the given arguments and returns true if the first and second are
   * equal to each other.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   * @return true if the first and second are equal to each other
   */
   public Boolean evaluate(List arguments, State state) {
     Object argument1 = evaluateArgument(arguments.get(0), state);
     Object argument2 = evaluateArgument(arguments.get(1), state);
     if (argument1 == null)
       return new Boolean(argument2 == null);
     else if (argument2 == null)
       return new Boolean(argument1 == null);
     else
       return new Boolean(argument1.equals(argument2));
  }
  
  /** Returns a string representation of this predicate given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(equal ");
    stringBuffer.append(arguments.get(0).toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
}
