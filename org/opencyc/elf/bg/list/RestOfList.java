package org.opencyc.elf.bg.list;

//// Internal Imports
import org.opencyc.cycobject.CycList;

import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** RestOfList is an arity one operator that returns the a new list consisting of all but the first 
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
public class RestOfList extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of RestOfList. */
  public RestOfList() {
    super();
  }
  
  /** Evaluates the given argument within the given state and returns all but the first element of the list.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public Object evaluate(List arguments, State state) {
    List list = (List) evaluateArgument(arguments.get(0), state);
    if (list.size() == 0)
      throw new BehaviorEngineException("Cannot operation on an empty list " + arguments);
    List restOfList = new ArrayList();
    Iterator iter = list.iterator();
    iter.next();
    while(iter.hasNext())
      restOfList.add(iter.next());
    return restOfList;
  }
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(rest-of-list ");
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
