package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** OperatorExpression contains an operator and arguments that can be
 * evaluated to return a value.
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
public class OperatorExpression implements Expression {
  
  //// Constructors
  
  /** Creates a new instance of OperatorExpression */
  public OperatorExpression() {
  }
    
  /**  Creates a new instance of a unary OperatorExpression given the
   * operator and no arguments.
   *
   * @param operator the unary operator
   */
  public OperatorExpression(Operator operator) {
    this.operator = operator;
    arguments = new ArrayList();
  }
  
  /**  Creates a new instance of a unary OperatorExpression given the
   * operator and single argument.
   *
   * @param operator the unary operator
   * @param arg1 the single argument 
   */
  public OperatorExpression(Operator operator, Object arg1) {
    this.operator = operator;
    arguments = new ArrayList();
    arguments.add(arg1);
  }
  
  /** Creates a new instance of a binary OperatorExpression given the
   * operator and two arguments.
   *
   * @param operator the binary operator
   * @param arg1 the first argument 
   * @param arg2 the second argument 
   */
  public OperatorExpression(Operator operator, Object arg1, Object arg2) {
    this.operator = operator;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
  }
  
  /** Creates a new instance of a OperatorExpression given the
   * operator and three arguments.
   *
   * @param operator the operator
   * @param arg1 the first argument 
   * @param arg2 the second argument 
   * @param arg3 the third argument 
   */
  public OperatorExpression(Operator operator, Object arg1, Object arg2, Object arg3) {
    this.operator = operator;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
    arguments.add(arg3);
  }
  
  //// Public Area
  
  /** Evaluates the operator and arguments within the given state, returning the result.
   *
   * @return the result of evaluating the operator and arguments within the given state
   */
  public Object evaluate(State state) {
    return operator.evaluate(arguments, state);
  }
  
  /** Gets the operator
   *
   * @return the operator
   */
  public Operator getOperator () {
    return operator;
  }

  /** Sets the operator
   *
   * @param operator the operator
   */
  public void setOperator (Operator operator) {
    this.operator = operator;
  }

  /** Gets the argument list
   *
   * @return the argument list
   */
  public List getArguments () {
    return arguments;
  }

  /** Sets the argument list
   *
   * @param arguments the argument list
   */
  public void setArguments (List arguments) {
    this.arguments = arguments;
  }
    
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return operator.toString(arguments);
  }
 
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the operator */
  protected Operator operator;

  /** the argument list */
  protected List arguments;
}
