package org.opencyc.elf.bg.list;

//// Internal Imports
import org.opencyc.cycobject.CycList;
import org.opencyc.elf.BehaviorEngineException;
import org.opencyc.elf.bg.expression.Operator;
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** TheList is a variable arity operator that returns a list containing its arguments.
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
public class TheList extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of TheList. */
  public TheList() {
    super();
  }
  
  /** Returns the list consisting of the evaluated arguments.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   */
  public Object evaluate(List arguments, State state) {
    CycList list = new CycList();
    Iterator iter = arguments.iterator();
    while (iter.hasNext())
      list.add(evaluateArgument(iter.next(), state));
    return list;
  }
  
  /** Returns a string representation of this operator given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(the-list");
    Iterator iter = arguments.iterator();
    while (iter.hasNext()) {
      stringBuffer.append(" ");
      Object obj = iter.next();
      if (obj instanceof String) {
        stringBuffer.append('"');
        stringBuffer.append(obj);
        stringBuffer.append('"');
      }
      else if (obj instanceof CycList)
        stringBuffer.append(((CycList) obj).cyclify());
      else
        stringBuffer.append(obj.toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Public Area
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
