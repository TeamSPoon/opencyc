package org.opencyc.elf.bg.expression;

//// Internal Imports
import org.opencyc.elf.wm.StateVariableFactory;
import org.opencyc.elf.wm.StateVariableLibrary;

import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;


/** Provides a suite of JUnit test cases for the org.opencyc.elf.bg.expression
 * package.
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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
public class UnitTest extends TestCase {
  
  //// Constructors
  
  /** Construct a new UnitTest object.
   * 
   * @param name the test case name.
   */
  public UnitTest(String name) {
    super(name);
  }
  
  //// Public Area

  /** Runs the unit tests
   * @return the test suite
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testOperatorExpression"));

    return testSuite;
  }

  /** Tests operator expression behavior. */
  public void testOperatorExpression() {
    System.out.println("\n*** testOperatorExpression ***");
    State state = new State();
    
    // Plus
    Plus plus = new Plus();
    OperatorExpression operatorExpression1 = new OperatorExpression(plus, new Integer(1), new Integer(2));
    Assert.assertEquals(new Integer(3), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Integer(1), new Long(2L));
    Assert.assertEquals(new Long(3L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Integer(1), new Float(2.0));
    Assert.assertEquals(new Float(3.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(plus, new Integer(1), new Double(2.0d));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(plus, new Long(1L), new Integer(2));
    Assert.assertEquals(new Long(3L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Long(1L), new Long(2L));
    Assert.assertEquals(new Long(3L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Long(1L), new Float(2.0));
    Assert.assertEquals(new Float(3.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(plus, new Long(1L), new Double(2.0d));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(plus, new Float(1.0), new Integer(2));
    Assert.assertEquals(new Float(3.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(plus, new Float(1.0), new Long(2L));
    Assert.assertEquals(new Float(3.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(plus, new Float(1.0), new Float(2.0));
    Assert.assertEquals(new Float(3.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(plus, new Float(1.0), new Double(2.0d));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(plus, new Double(1.0d), new Integer(2));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Double(1.0d), new Long(2L));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Double(1.0d), new Float(2.0));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(plus, new Double(1.0d), new Double(2.0d));
    Assert.assertEquals(new Double(3.0d), operatorExpression1.evaluate(state));
        
    StateVariable testStateVariable1 = new StateVariable(Integer.class, "test-state-variable1", "test state variable 1");
    operatorExpression1 = new OperatorExpression(plus, new Integer(99), testStateVariable1);
    state.setStateValue(testStateVariable1, new Integer(100));
    Assert.assertEquals(new Integer(199), operatorExpression1.evaluate(state));
    state.setStateValue(testStateVariable1, new Integer(-100));
    Assert.assertEquals(new Integer(-1), operatorExpression1.evaluate(state));
    StateVariable testStateVariable2 = new StateVariable(Integer.class, "test-state-variable2", "test state variable 2");
    operatorExpression1 = new OperatorExpression(plus, testStateVariable1, testStateVariable2);
    state.setStateValue(testStateVariable1, new Integer(-100));
    state.setStateValue(testStateVariable2, new Integer(-99));
    Assert.assertEquals(new Integer(-199), operatorExpression1.evaluate(state));
    
    // Minus
    Minus minus = new Minus();
    operatorExpression1 = new OperatorExpression(minus, new Integer(1));
    Assert.assertEquals(new Integer(-1), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Long(1L));
    Assert.assertEquals(new Long(-1L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Float(1.0));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Double(1.0d));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));    
    
    operatorExpression1 = new OperatorExpression(minus, new Integer(1), new Integer(2));
    Assert.assertEquals(new Integer(-1), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Integer(1), new Long(2L));
    Assert.assertEquals(new Long(-1L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Integer(1), new Float(2.0));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Integer(1), new Double(2.0d));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(minus, new Long(1L), new Integer(2));
    Assert.assertEquals(new Long(-1L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Long(1L), new Long(2L));
    Assert.assertEquals(new Long(-1L), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Long(1L), new Float(2.0));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Long(1L), new Double(2.0d));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(minus, new Float(1.0), new Integer(2));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Float(1.0), new Long(2L));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Float(1.0), new Float(2.0));
    Assert.assertEquals(new Float(-1.0).toString(), operatorExpression1.evaluate(state).toString());
    operatorExpression1 = new OperatorExpression(minus, new Float(1.0), new Double(2.0d));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    
    operatorExpression1 = new OperatorExpression(minus, new Double(1.0d), new Integer(2));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Double(1.0d), new Long(2L));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Double(1.0d), new Float(2.0));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, new Double(1.0d), new Double(2.0d));
    Assert.assertEquals(new Double(-1.0d), operatorExpression1.evaluate(state));
        
    operatorExpression1 = new OperatorExpression(minus, new Integer(99), testStateVariable1);
    state.setStateValue(testStateVariable1, new Integer(100));
    Assert.assertEquals(new Integer(-1), operatorExpression1.evaluate(state));
    state.setStateValue(testStateVariable1, new Integer(-100));
    Assert.assertEquals(new Integer(199), operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(minus, testStateVariable1, testStateVariable2);
    state.setStateValue(testStateVariable1, new Integer(-100));
    state.setStateValue(testStateVariable2, new Integer(-99));
    Assert.assertEquals(new Integer(-1), operatorExpression1.evaluate(state));
    
    // (+ 1 (- 10 8)) = 3
    operatorExpression1 = new OperatorExpression(minus, new Integer(10), new Integer(8));
    OperatorExpression operatorExpression2 = new OperatorExpression(plus, new Integer(1), operatorExpression1);
    Assert.assertEquals(new Integer(3), operatorExpression2.evaluate(state));
    
    System.out.println("*** testOperatorExpression OK ***");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
  /** Main method in case tracing is prefered over running JUnit.
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}