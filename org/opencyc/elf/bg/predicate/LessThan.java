package org.opencyc.elf.bg.predicate;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** LessThan is a binary predicate that returns true if all of its
 * first argument is numerically less than its second argument.
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
public class LessThan extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of LessThan */
  public LessThan() {
    super();
  }
  
  //// Public Area
  
  /** Evaluates the given argument predicate expressions and returns the result.
   *
   * @param arguments the given predicate expressions to evaluate
   * @param state the given state
   * @return the result of evaluating the given predicate expressions
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() != 2)
      throw new BehaviorEngineException("Wrong number of arguments " + arguments);
    Number arg1Obj = (Number) evaluateArgument(arguments.get(0), state);
    Number arg2Obj = (Number) evaluateArgument(arguments.get(0), state);
    Number[] args = convertNumericArguments(arg1Obj, arg2Obj);
    return evaluate(args[0], args[1]);
  }
  
  /** Returns a string representation of this predicate given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(< ");
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
  
  /** Returns true if the first numeric argument is less than the second
   * numeric argument.
   *
   * @param arg1 the first numeric argument
   * @param arg2 the second numeric argument
   * @return true if the first numeric argument is less than the second
   * numeric argument
   */
  protected Boolean evaluate(Number arg1, Number arg2) {
    if (arg1 instanceof Integer && arg2 instanceof Integer)
      return new Boolean(((Integer) arg1).compareTo(arg2) < 0);
    if (arg1 instanceof Long && arg2 instanceof Long)
      return new Boolean(((Long) arg1).compareTo(arg2) < 0);
    if (arg1 instanceof Float && arg2 instanceof Float)
      return new Boolean(((Float) arg1).compareTo(arg2) < 0);
    else
      return new Boolean(((Double) arg1).compareTo(arg2) < 0);
  }
  
  //// Private Area
  
  //// Internal Rep
  
}
