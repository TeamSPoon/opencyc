package org.opencyc.elf.bg.predicate;

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


/** Provides a suite of JUnit test cases for the org.opencyc.elf.bg.predicate
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
    testSuite.addTest(new UnitTest("testPredicateExpression"));

    return testSuite;
  }

  /** Tests predicate expression behavior. */
  public void testPredicateExpression() {
    System.out.println("\n*** testPredicateExpression ***");
    State state = new State();
    
    // True
    True true1 = new True();
    PredicateExpression predicateExpression1 = new PredicateExpression(true1);
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    Assert.assertEquals("TRUE", predicateExpression1.toString());
    
    // NotNull
    NotNull notNull = new NotNull();
    predicateExpression1 = new PredicateExpression(notNull, "abc");
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    Assert.assertEquals("(not-null \"abc\")", predicateExpression1.toString());
    predicateExpression1 = new PredicateExpression(notNull, null);
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    // Equals
    Equals equals = new Equals();
    predicateExpression1 = new PredicateExpression(equals, "abc", "abc");
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    Assert.assertEquals("(equal \"abc\" \"abc\")", predicateExpression1.toString());
    predicateExpression1 = new PredicateExpression(equals, "abc", "def");
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, new Integer(100), new Integer(100));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, null, null);
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, "abc", null);
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    // Different
    Different different = new Different();
    predicateExpression1 = new PredicateExpression(different, new Integer(100), "abc");
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    Assert.assertEquals("(different 100 \"abc\")", predicateExpression1.toString());
    predicateExpression1 = new PredicateExpression(different, null, "abc");
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, "abc", "abc");
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, "abc", "def", new Integer(0));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, "abc", "def", null);
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, "abc", "def", "abc");
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, null, "def", null);
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    // using state variables
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    StateVariable consoleInputStateVariable = 
      StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_INPUT);
    StateVariable consolePromptStateVariable = 
      StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_PROMPT);
    predicateExpression1 = new PredicateExpression(equals, consoleInputStateVariable, consolePromptStateVariable);
    Assert.assertEquals("(equal consoleInput consolePrompt)", predicateExpression1.toString());
    state.setStateValue(consoleInputStateVariable, "abc");
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    state.setStateValue(consolePromptStateVariable, "abc");
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    state.setStateValue(consolePromptStateVariable, "def");
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    // Not
    Not not = new Not();
    predicateExpression1 = new PredicateExpression(true1);
    Assert.assertEquals("TRUE", predicateExpression1.toString());
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    PredicateExpression predicateExpression2 = new PredicateExpression(not, predicateExpression1);
    Assert.assertEquals("(not TRUE)", predicateExpression2.toString());
    Assert.assertEquals(Boolean.FALSE, predicateExpression2.evaluate(state));
    PredicateExpression predicateExpression3 = new PredicateExpression(not, predicateExpression2);
    Assert.assertEquals("(not (not TRUE))", predicateExpression3.toString());
    Assert.assertEquals(Boolean.TRUE, predicateExpression3.evaluate(state));
    
    // And
    And and = new And();
    predicateExpression1 = new PredicateExpression(true1);
    predicateExpression2 = new PredicateExpression(true1);
    predicateExpression3 = new PredicateExpression(and, predicateExpression1, predicateExpression2);
    Assert.assertEquals(Boolean.TRUE, predicateExpression3.evaluate(state));
    predicateExpression3 = new PredicateExpression(true1);
    PredicateExpression predicateExpression4 = 
      new PredicateExpression(and, 
                              predicateExpression1, 
                              predicateExpression2, 
                              predicateExpression3);
    Assert.assertEquals(Boolean.TRUE, predicateExpression4.evaluate(state));
    Assert.assertEquals("(and TRUE TRUE TRUE)", predicateExpression4.toString());
    predicateExpression2 = new PredicateExpression(equals, "abc", "def");
    predicateExpression4 = 
      new PredicateExpression(and, 
                              predicateExpression1, 
                              predicateExpression2, 
                              predicateExpression3);
    Assert.assertEquals("(and TRUE (equal \"abc\" \"def\") TRUE)", predicateExpression4.toString());
    Assert.assertEquals(Boolean.FALSE, predicateExpression4.evaluate(state));
    
    // Or
    Or or = new Or();
    predicateExpression1 = new PredicateExpression(true1);
    predicateExpression2 = new PredicateExpression(true1);
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertEquals(Boolean.TRUE, predicateExpression3.evaluate(state));
    Assert.assertEquals("(or TRUE TRUE)", predicateExpression3.toString());
    predicateExpression1 = new PredicateExpression(equals, "abc", "def");
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertEquals(Boolean.TRUE, predicateExpression3.evaluate(state));
    predicateExpression2 = new PredicateExpression(equals, "abc", "def");
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertEquals("(or (equal \"abc\" \"def\") (equal \"abc\" \"def\"))", predicateExpression3.toString());
    Assert.assertEquals(Boolean.FALSE, predicateExpression3.evaluate(state));
    
    // LessThan
    LessThan lessThan = new LessThan();
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(1), new Integer(2));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    Assert.assertEquals("(< 1 2)", predicateExpression1.toString());
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(1), new Long(2L));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(1), new Float(2.0));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(1), new Double(2.0d));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Long(1L), new Integer(2));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(1L), new Long(2L));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(1L), new Float(2.0));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(1L), new Double(2.0d));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Float(1.0), new Integer(2));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(1.0), new Long(2L));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(1.0), new Float(2.0));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(1.0), new Double(2.0d));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Double(1.0d), new Integer(2));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(1.0d), new Long(2L));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(1.0d), new Float(2.0));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(1.0d), new Double(2.0d));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(3), new Integer(2));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(3), new Long(2L));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(3), new Float(2.0));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(3), new Double(2.0d));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Long(3L), new Integer(2));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(3L), new Long(2L));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(3L), new Float(2.0));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Long(3L), new Double(2.0d));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Float(3.0), new Integer(2));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(3.0), new Long(2L));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(3.0), new Float(2.0));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Float(3.0), new Double(2.0d));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    predicateExpression1 = new PredicateExpression(lessThan, new Double(3.0d), new Integer(2));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(3.0d), new Long(2L));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(3.0d), new Float(2.0));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(lessThan, new Double(3.0d), new Double(2.0d));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    
    StateVariable testStateVariable1 = new StateVariable(Integer.class, "test-state-variable1", "test state variable 1");
    predicateExpression1 = new PredicateExpression(lessThan, new Integer(99), testStateVariable1);
    Assert.assertEquals("(< 99 test-state-variable1)", predicateExpression1.toString());
    state.setStateValue(testStateVariable1, new Integer(100));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    state.setStateValue(testStateVariable1, new Integer(-100));
    Assert.assertEquals(Boolean.FALSE, predicateExpression1.evaluate(state));
    StateVariable testStateVariable2 = new StateVariable(Integer.class, "test-state-variable2", "test state variable 2");
    predicateExpression1 = new PredicateExpression(lessThan, testStateVariable1, testStateVariable2);
    state.setStateValue(testStateVariable1, new Integer(-100));
    state.setStateValue(testStateVariable2, new Integer(-99));
    Assert.assertEquals(Boolean.TRUE, predicateExpression1.evaluate(state));
    
    System.out.println("*** testPredicateExpression OK ***");
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