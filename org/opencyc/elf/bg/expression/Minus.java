package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** Minus is either a unary or numeric binary operator. When one argument is given, the argument is negated and
 * returned.  When two arguments are given the second is subtracted from the first and the result returned.
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
public class Minus extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of Plus. */
  public Minus() {
    super();
  }
  
  /** Evaluates the given arguments within the given state and returns the sum.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() == 1) {
      Number arg1Obj = (Number) evaluateArgument(arguments.get(0), state);
      if (arg1Obj instanceof Integer)
        return new Integer(0 - arg1Obj.intValue());
      else if (arg1Obj instanceof Long)
        return new Long(0 - arg1Obj.longValue());
      else if (arg1Obj instanceof Float)
        return new Float(0 - arg1Obj.floatValue());
      else
        return new Double(0 - arg1Obj.doubleValue());
    }
    else if (arguments.size() == 2) {
      Number arg1Obj = (Number) evaluateArgument(arguments.get(0), state);
      Number arg2Obj = (Number) evaluateArgument(arguments.get(1), state);
      Number[] args = convertNumericArguments(arg1Obj, arg2Obj);
      return evaluate(args[0], args[1]);
    }
    else
      throw new BehaviorEngineException("Wrong number of arguments " + arguments);
  }
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(- ");
    Iterator iter = arguments.iterator();
    if (iter.hasNext())
      stringBuffer.append(iter.next().toString());
    while (iter.hasNext()) {
      stringBuffer.append(" ");
      Object obj = iter.next();
      stringBuffer.append(iter.next().toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Public Area
  
  //// Protected Area
  
  /** Returns the difference of the given two numeric arguments which have the same class.
   *
   * @param arg1 the first numeric argument
   * @param arg2 the second numeric argument
   * @return the difference of the given two numeric arguments which have the same class
   */
  protected Object evaluate(Number arg1, Number arg2) {
    if (arg1 instanceof Integer) {
      long difference = arg1.longValue() - arg2.longValue();
      if (difference >= Integer.MIN_VALUE || difference <= Integer.MAX_VALUE)
        return new Integer(arg1.intValue() - arg2.intValue());
      else
        return new Long(difference);
    }
    if (arg1 instanceof Long)
        return new Long(arg1.longValue() - arg2.longValue());
    if (arg1 instanceof Float)
      return new Float(arg1.floatValue() - arg2.floatValue());
    else
      return new Double(arg1.doubleValue() - arg2.doubleValue());
  }
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
