package org.opencyc.elf.bg.dictionary;

//// Internal Imports
import org.opencyc.cycobject.CycList;

import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Hashtable;
import java.util.List;

/** DictionaryRemove is an arity two operator.  The first argument is the key object, the
 * second argument is the dictionary object.  This operator removes the key and the 
 * value associated with the key in the dictionary.  The evaluated key is returned.
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
public class DictionaryRemove extends Operator {
  
  /** Creates a new instance of DictionaryRemove */
  public DictionaryRemove() {
    super();
  }
  
  /** Evaluates the given arguments within the given state and returns the dictionary
   * after adding the key/value pair.
   *
   * @param arguments the given arguments to evaluate (key, value, dictionary)
   * @param state the given state
   * @return the evaluated key argument
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() != 2)
      throw new BehaviorEngineException("Two arguments required " + arguments);
    Object key = evaluateArgument(arguments.get(0), state);
    Hashtable dictionary = (Hashtable) evaluateArgument(arguments.get(1), state);
    dictionary.remove(key);
    return key;
  }
  
  /** Returns a string representation of this operator given the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(dictionary-remove ");
    Object obj = arguments.get(0);
    if (obj instanceof String) {
      stringBuffer.append('"');
      stringBuffer.append(obj);
      stringBuffer.append('"');
    }
    if (obj instanceof CycList)
      stringBuffer.append(((CycList)obj).cyclify());
    else
      stringBuffer.append(obj.toString());
    stringBuffer.append(" ");
    stringBuffer.append(arguments.get(1).toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
}
