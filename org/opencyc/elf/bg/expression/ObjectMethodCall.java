package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** ObjectMethodCall calls the given method on the given evaluated object using the given
 * evaluated arguments.
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
public class ObjectMethodCall implements Expression {
  
  //// Constructors
  
  /** Creates a new instance of ObjectMethodCall.
   *
   * @param objClass the object class 
   * @param objInstance the object instance
   * @param method the object method
   * @param arguments the method arguments
   */
  public ObjectMethodCall(Class objClass, 
                          Object objInstance, 
                          Method method, 
                          List arguments) {
    this.objClass = objClass;
    this.objInstance = objInstance;
    this.method = method;
    this.arguments = arguments;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(((");
    stringBuffer.append(objClass.toString());
    stringBuffer.append(") ");
    stringBuffer.append(method.toString());
    stringBuffer.append(").");
    Iterator iter = arguments.iterator();
    while (iter.hasNext()) {
      stringBuffer.append(" ");
      stringBuffer.append(iter.next().toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
 
  /** Evaluates the instance object and method arguments within the given state, 
   * returning the result.
   *
   * @return the result of evaluating the instance object and method arguments within 
   * the given state
   *
   */
  public Object evaluate(State state) {
    Object result = null;
    if (objInstance == null) {
      //TODO call static method
    }
    else {
      //TODO call instance method
    }
    return result;
  }
  
  /**
   * Gets the object class.
   *
   * @return the object class
   */
  public Class getObjClass () {
    return objClass;
  }

  /**
   * Gets the object instance.
   *
   * @return the object instance
   */
  public Object getObjInstance () {
    return objInstance;
  }

  /**
   * Gets the object method.
   *
   * @return the object method
   */
  public Method getMethod () {
    return method;
  }

  /**
   * Gets the method arguments.
   *
   * @return the method arguments
   */
  public List getArguments () {
    return arguments;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the object class */
  protected final Class objClass;
  
  /** the object instance */
  protected final Object objInstance;
  
  /** the object method */
  protected final Method method;
  
  /** the method arguments */
  protected final List arguments;
  
}
