package org.opencyc.elf.bg.list;

//// Internal Imports
import org.opencyc.cycobject.CycList;
import org.opencyc.elf.BehaviorEngineException;
import org.opencyc.elf.bg.expression.Operator;
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** NthInList is an arity one operator that returns the a new list consisting of all but the first 
 * element of its list argument.  An exception is thrown if the given list is empty.
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
public class NthInList extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of NthInList. */
  public NthInList() {
    super();
  }
  
  /** Evaluates the given arguments within the given state and returns the nth element of the
   * first list argument where the second argument is a positive integer indicating the element position.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() != 2)
      throw new BehaviorEngineException("Invalid number of arguments " + arguments);
    List list = (List) evaluateArgument(arguments.get(0), state);
    if (list.size() == 0)
      throw new BehaviorEngineException("Cannot operate on an empty list " + arguments);
    Integer position = (Integer) evaluateArgument(arguments.get(1), state);
    return list.get(position.intValue());
  }
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(nth-in-list ");
    Object obj = arguments.get(0);
    if (obj instanceof CycList)
      stringBuffer.append(((CycList) obj).cyclify());
    else
      stringBuffer.append(obj.toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Public Area
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
