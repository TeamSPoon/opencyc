package org.opencyc.elf.bg.predicate;

//// Internal Imports
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** And is a predicate of variable arity that returns true if all of its
 * argument predicate expressions evaluate to true.
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
public class And extends Predicate {
  
  //// Constructors
  
  /** Creates a new instance of And */
  public And() {
    super();
  }
  
  //// Public Area
  
  /** Evaluates the given argument predicate expressions and returns the result.
   *
   * @param arguments the given predicate expressions to evaluate
   * @param state the given state
   * @return the result of evaluating the given predicate expressions
   */
  public Boolean evaluate(List arguments, State state) {
    Iterator iter = arguments.iterator();
    while (iter.hasNext()) {
      PredicateExpression predicateExpression = (PredicateExpression) iter.next();
      if (predicateExpression.evaluate(state).equals(Boolean.FALSE))
        return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }
  
  /** Returns a string representation of this predicate given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(and ");
    Iterator iter = arguments.iterator();
    if (iter.hasNext())
      stringBuffer.append(iter.next().toString());
    while (iter.hasNext()) {
      stringBuffer.append(" ");
      stringBuffer.append(iter.next().toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
}
