package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** Plus is a binary operator that returns the sum of its first and second arguments.
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
public class Plus extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of Plus. */
  public Plus() {
    super();
  }
  
  /** Evaluates the given arguments within the given state and returns the sum.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() != 2)
      throw new BehaviorEngineException("Wrong number of arguments " + arguments);
    Number arg1Obj = (Number) evaluateArgument(arguments.get(0), state);
    Number arg2Obj = (Number) evaluateArgument(arguments.get(1), state);
    Number[] args = convertNumericArguments(arg1Obj, arg2Obj);
    return evaluate(args[0], args[1]);
  }
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(+ ");
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
  
  //// Public Area
  
  //// Protected Area
  
  /** Returns the sum of the given two numeric arguments which have the same class.
   *
   * @param arg1 the first numeric argument
   * @param arg2 the second numeric argument
   * @return the sum of the given two numeric arguments which have the same class
   */
  protected Object evaluate(Number arg1, Number arg2) {
    if (arg1 instanceof Integer) {
      long sum = arg1.longValue() + arg2.longValue();
      if (sum <= Integer.MAX_VALUE)
        return new Integer(arg1.intValue() + arg2.intValue());
      else
        return new Long(sum);
    }
    if (arg1 instanceof Long)
        return new Long(arg1.longValue() + arg2.longValue());
    if (arg1 instanceof Float)
      return new Float(arg1.floatValue() + arg2.floatValue());
    else
      return new Double(arg1.doubleValue() + arg2.doubleValue());
  }
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
