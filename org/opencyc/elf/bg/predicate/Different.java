package org.opencyc.elf.bg.predicate;

//// Internal Imports
import org.opencyc.cycobject.CycList;

import org.opencyc.elf.BehaviorEngineException;

import org.opencyc.elf.bg.expression.Operator;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Different is a predicate of variable aritythat returns true if its arguments
 * are all different from each other.
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
public class Different extends Operator implements Predicate {
  
  //// Constructors
  
  /** Creates a new instance of Different */
  public Different() {
    super();
  }
  
  //// Public Area
    
  /** 
   * Evaluates the given arguments and returns if all are different from each other.
   *
   * @param arguments the given arguments to evaluate
   * @param state the given state
   * @return true if the first and second are not equal to each other
   */
   public Object evaluate(List arguments, State state) {
     if (arguments.size() == 2) {
       // most common case
       Object argument1 = evaluateArgument(arguments.get(0), state);
       Object argument2 = evaluateArgument(arguments.get(1), state);
       if (argument1 == null) 
         return new Boolean(argument2 != null);
       else if (argument2 == null)
         return new Boolean(argument1 != null);
       else
         return new Boolean(! argument1.equals(argument2));
     }
     else if (arguments.size() < 2)
       throw new BehaviorEngineException("At least two arguments are required " + arguments);
     // for three or more arguments use a hash set to detect duplicates
     boolean haveNullValue = false;
     HashSet hashSet = new HashSet();
     Iterator iter = arguments.iterator();
     while (iter.hasNext()) {
       Object evaluatedArg = evaluateArgument(iter.next(), state);
       if (evaluatedArg == null) {
         if (haveNullValue)
           return Boolean.FALSE;
         else
           haveNullValue = true;
       }
       else
         hashSet.add(evaluatedArg);
     }
     if (haveNullValue)
       return new Boolean(hashSet.size() == (arguments.size() - 1));
     else
       return new Boolean(hashSet.size() == arguments.size());
  }
  
  /**
   * Returns a string representation of this predicate given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(List arguments) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(different");
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
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
}
