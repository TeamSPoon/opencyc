package org.opencyc.elf.bg.dictionary;

//// Internal Imports
import org.opencyc.cycobject.CycList;

import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/** DictionaryKeys is an arity one operator.  Its sole argument is a dictionary and it
 * returns the list of keys in the dictionary.
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
public class DictionaryKeys extends Operator {
  
  //// Constructors
  
  /** Creates a new instance of DictionaryKeys */
  public DictionaryKeys() {
    super();
  }
  
  //// Public Area
  
  /** Returns the list of keys contained in the given dictionary.
   *
   * @param arguments the given arguments to evaluate (dictionary)
   * @param state the given state
   * @return the list of keys contained in the given dictionary
   */
  public Object evaluate(List arguments, State state) {
    if (arguments.size() != 1)
      throw new BehaviorEngineException("One argument required " + arguments);
    Hashtable dictionary = (Hashtable) evaluateArgument(arguments.get(0), state);
    CycList list = new CycList();
    Enumeration keys = dictionary.keys();
    while (keys.hasMoreElements())
      list.add(keys.nextElement());
    return list;
  }
  
  /** Returns a string representation of this operator given the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(dictionary-keys ");
    stringBuffer.append(arguments.get(0).toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
