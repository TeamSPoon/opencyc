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
    Assert.assertTrue(predicateExpression1.evaluate(state));
    // NotNull
    NotNull notNull = new NotNull();
    predicateExpression1 = new PredicateExpression(notNull, "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(notNull, null);
    Assert.assertTrue(! predicateExpression1.evaluate(state));
    // Equals
    Equals equals = new Equals();
    predicateExpression1 = new PredicateExpression(equals, "abc", "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, "abc", "def");
    Assert.assertTrue(! predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, new Integer(100), new Integer(100));
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, null, null);
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, "abc", null);
    Assert.assertTrue(! predicateExpression1.evaluate(state));
    // Different
    Different different = new Different();
    predicateExpression1 = new PredicateExpression(different, new Integer(100), "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(different, null, "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, "abc", "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    // using state variables
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    StateVariable consoleInputStateVariable = 
      StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_INPUT);
    StateVariable consolePromptStateVariable = 
      StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_PROMPT);
    predicateExpression1 = new PredicateExpression(equals, consoleInputStateVariable, consolePromptStateVariable);
    state.setStateValue(consoleInputStateVariable, "abc");
    Assert.assertTrue(! predicateExpression1.evaluate(state));
    state.setStateValue(consolePromptStateVariable, "abc");
    Assert.assertTrue(predicateExpression1.evaluate(state));
    state.setStateValue(consolePromptStateVariable, "def");
    Assert.assertTrue(! predicateExpression1.evaluate(state));
    // Not
    Not not = new Not();
    predicateExpression1 = new PredicateExpression(true1);
    Assert.assertTrue(predicateExpression1.evaluate(state));
    PredicateExpression predicateExpression2 = new PredicateExpression(not, predicateExpression1);
    Assert.assertTrue(! predicateExpression2.evaluate(state));
    PredicateExpression predicateExpression3 = new PredicateExpression(not, predicateExpression2);
    Assert.assertTrue(predicateExpression3.evaluate(state));
    // And
    And and = new And();
    predicateExpression1 = new PredicateExpression(true1);
    predicateExpression2 = new PredicateExpression(true1);
    predicateExpression3 = new PredicateExpression(and, predicateExpression1, predicateExpression2);
    Assert.assertTrue(predicateExpression3.evaluate(state));
    predicateExpression3 = new PredicateExpression(true1);
    PredicateExpression predicateExpression4 = 
      new PredicateExpression(and, 
                              predicateExpression1, 
                              predicateExpression2, 
                              predicateExpression3);
    Assert.assertTrue(predicateExpression4.evaluate(state));
    predicateExpression2 = new PredicateExpression(equals, "abc", "def");
    predicateExpression4 = 
      new PredicateExpression(and, 
                              predicateExpression1, 
                              predicateExpression2, 
                              predicateExpression3);
    Assert.assertTrue(! predicateExpression4.evaluate(state));
    // Or
    Or or = new Or();
    predicateExpression1 = new PredicateExpression(true1);
    predicateExpression2 = new PredicateExpression(true1);
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertTrue(predicateExpression3.evaluate(state));
    predicateExpression1 = new PredicateExpression(equals, "abc", "def");
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertTrue(predicateExpression3.evaluate(state));
    predicateExpression2 = new PredicateExpression(equals, "abc", "def");
    predicateExpression3 = new PredicateExpression(or, predicateExpression1, predicateExpression2);
    Assert.assertTrue(! predicateExpression3.evaluate(state));
    
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