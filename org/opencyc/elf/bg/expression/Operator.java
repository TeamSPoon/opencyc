package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.List;

/** Operator defines the behavior for objects that evaluate
 * arguments and return a result boject.  The arguments can be objects or
 * state variables.
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
public abstract class Operator {
  
  //// Constructors
  
  /** Creates a new instance of Operator. */
  public Operator() {
    this.operator = this;
  }
  
  //// Public Area
  
  /** Gets the singleton instance of operator.
   *
   * @return the singleton instance of operator
   */
  public static Operator getInstance () {
    return operator;
  }
  
  /** Evaluates the given arguments within the given state and returns the result.  The semantics
   * of the operator are defined by each implementing class.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public abstract Object evaluate (List arguments, State state);
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public abstract String toString(List arguments);
  
  /** Performs type conversion on the given numeric arguments and returns the numeric
   * objects having the same type in an array.
   *
   * @param arg1Num the first numeric argument
   * @param arg2Num the second numeric argument
   * @ return the converted numeric objects in an array
   */
  public Number[] convertNumericArguments(Number arg1Num, Number arg2Num) {
    Number[] result = {arg1Num, arg2Num};
    if (! arg1Num.getClass().equals(arg2Num.getClass())) {
      if (arg1Num instanceof Integer) {
        if (arg2Num instanceof Long)
          result[0] = new Long(arg1Num.longValue());
        else {
          result[0] = new Double(arg1Num.doubleValue());
          result[1] = new Double(arg2Num.doubleValue());
        }
      }
      else if (arg1Num instanceof Long) {
        if (arg2Num instanceof Integer)
          result[1] = new Long(arg2Num.longValue());
        else {
          result[0] = new Double(arg1Num.doubleValue());
          result[1] = new Double(arg2Num.doubleValue());
        }
      }
      else {
        result[0] = new Double(arg1Num.doubleValue());
        result[1] = new Double(arg2Num.doubleValue());
      }
    }
    return result;
  }
  
  //// Protected Area
  
  /** Dereferences the given argument within the given state if the argument is a state 
   * variable.
   *
   * @param argument the given argument
   * @param state the given state
   * @return the value of the state variable when the given argument is a state variable,
   * otherwise return the argument
   */
  protected Object evaluateArgument (Object argument, State state) {
    if (argument instanceof StateVariable) {
      return state.getStateValue((StateVariable) argument);
    }
    else
      return argument;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the singleton instance of operator */
  protected static Operator operator;
  
  //// Main
  
}
