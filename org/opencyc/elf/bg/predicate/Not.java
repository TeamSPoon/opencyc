package org.opencyc.elf.bg.predicate;

//// Internal Imports
import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** Not is a predicate of arity one that returns true if its argument predicate 
 * expressions evaluates to true, otherwise it returns false.
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
public class Not extends Operator implements Predicate {
  
  //// Constructors
  
  /** Creates a new instance of Not */
  public Not() {
    super();
  }
  
  //// Public Area
  
  /** Evaluates the given argument predicate expression and returns the result.
   *
   * @param arguments the given predicate expressions to evaluate
   * @param state the given state
   * @return the result of evaluating the given predicate expressions
   */
  public Object evaluate(List arguments, State state) {
    PredicateExpression predicateExpression = (PredicateExpression) arguments.get(0);
    if (predicateExpression.evaluate(state).equals(Boolean.TRUE))
      return Boolean.FALSE;
    else
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
    stringBuffer.append("(not ");
    stringBuffer.append(arguments.get(0).toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
 /** Returns true if the given object equals this object.
   *
   * @param obj the given object
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    return obj instanceof Not;
  }
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
}
