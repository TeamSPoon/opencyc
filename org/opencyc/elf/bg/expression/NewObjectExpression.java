package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** NewObjectExpression constructs a new object of the given class with the given
 * arguments.
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
public class NewObjectExpression implements Expression {
  
  //// Constructors
  
  /** Creates a new instance of NewObjectExpression given the object class.
   *
   * @param objClass the object class
   */
  public NewObjectExpression(Class objClass) {
    this.objClass = objClass;
    arguments = new ArrayList();
  }
  
  /** Creates a new instance of NewObjectExpression given the object class and a single
   * argument.
   *
   * @param objClass the object class
   * @param arg1 the single argument to the class constructor
   */
  public NewObjectExpression(Class objClass, Object arg1) {
    this.objClass = objClass;
    arguments = new ArrayList();
    arguments.add(arg1);
  }
  
  /** Creates a new instance of NewObjectExpression given the object class and two
   * arguments.
   *
   * @param objClass the object class
   * @param arg1 the first argument to the class constructor
   * @param arg2 the second argument to the class constructor
   */
  public NewObjectExpression(Class objClass, Object arg1, Object arg2) {
    this.objClass = objClass;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
  }
  
  /** Creates a new instance of NewObjectExpression given the object class and three
   * arguments.
   *
   * @param objClass the object class
   * @param arg1 the first argument to the class constructor
   * @param arg2 the second argument to the class constructor
   * @param arg3 the third argument to the class constructor
   */
  public NewObjectExpression(Class objClass, Object arg1, Object arg2, Object arg3) {
    this.objClass = objClass;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
    arguments.add(arg3);
  }
  
  /** Creates a new instance of NewObjectExpression given the object class and four
   * arguments.
   *
   * @param objClass the object class
   * @param arg1 the first argument to the class constructor
   * @param arg2 the second argument to the class constructor
   * @param arg3 the third argument to the class constructor
   * @param arg4 the third argument to the class constructor
   */
  public NewObjectExpression(Class objClass, 
                             Object arg1, 
                             Object arg2, 
                             Object arg3,
                             Object arg4) {
    this.objClass = objClass;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
    arguments.add(arg3);
    arguments.add(arg4);
  }
  
  //// Public Area
  
  /**
   * Gets the object class.
   *
   * @return the object class
   */
  public Class getObjClass () {
    return objClass;
  }

  /**
   * Gets the constructor arguments.
   *
   * @return the constructor arguments
   */
  public List getArguments () {
    return arguments;
  }

  /**
   * Sets the constructor arguments.
   *
   * @param arguments the constructor arguments
   */
  public void setaArguments (List arguments) {
    this.arguments = arguments;
  }

  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(new ");
    stringBuffer.append(objClass.toString());
    Iterator iter = arguments.iterator();
    while (iter.hasNext()) {
      stringBuffer.append(" ");
      stringBuffer.append(iter.next().toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
 
  /** Returns the new object resulting from constructing the given class using
   * the given evaluated arguments.
   *
   * @return the new object resulting from constructing the given evaluated class using
   * the given evaluated arguments
   *
   */
  public Object evaluate(State state) {
    Object newObject = null;
    List evaluatedArguments = new ArrayList();
    Iterator iter = arguments.iterator();
    while (iter.hasNext())
      evaluatedArguments.add(Operator.evaluateArgument(iter.next(), state));
    //TODO constuct new object
    return newObject;
  }
    
      
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the object class */
  protected final Class objClass;
  
  /** the constructor arguments */
  protected List arguments;
  
}
