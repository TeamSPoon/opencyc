package org.opencyc.elf.bg.list;

//// Internal Imports
import org.opencyc.elf.bg.expression.Operator;
import org.opencyc.elf.bg.expression.OperatorExpression;

import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;


/** Provides a suite of JUnit test cases for the org.opencyc.elf.bg.list package.
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
    
    // TheEmptyList
    new TheEmptyList();
    Operator theEmptyList = TheEmptyList.getInstance();
    Assert.assertNotNull(theEmptyList.evaluate(null, state));
    Assert.assertEquals(new ArrayList(), theEmptyList.evaluate(null, state));
    OperatorExpression operatorExpression1 = new OperatorExpression(theEmptyList);
    Assert.assertEquals("(the-empty-list)", operatorExpression1.toString());
    Assert.assertEquals(new ArrayList(), operatorExpression1.evaluate(state));
    
    // TheList
    new TheList();
    Operator theList = TheList.getInstance();
    operatorExpression1 = new OperatorExpression(theList, "xyz");
    Assert.assertTrue(operatorExpression1.evaluate(state) instanceof List);
    Assert.assertEquals(1, ((List) operatorExpression1.evaluate(state)).size());
    List list = new ArrayList();
    list.add("xyz");
    Assert.assertEquals(list, operatorExpression1.evaluate(state));
    operatorExpression1 = new OperatorExpression(theList, "xyz", new Integer(0), new Float(1.0));
    Assert.assertEquals("(the-list xyz 0 1.0)", operatorExpression1.toString());
    Assert.assertEquals(3, ((List) operatorExpression1.evaluate(state)).size());
    
    // LengthOfList
    new LengthOfList();
    Operator lengthOfList = LengthOfList.getInstance();
    operatorExpression1 = new OperatorExpression(lengthOfList, new ArrayList());
    Assert.assertEquals(new Integer(0), operatorExpression1.evaluate(state));
    list = new ArrayList();
    list.add("abc");
    list.add(new Double(100.0d));
    operatorExpression1 = new OperatorExpression(lengthOfList, list);
    Assert.assertEquals(new Integer(2), operatorExpression1.evaluate(state));
    StateVariable testStateVariable1 = new StateVariable(List.class, "test-state-variable1", "test state variable 1");
    operatorExpression1 = new OperatorExpression(lengthOfList, testStateVariable1);
    Assert.assertEquals("(length-of-list test-state-variable1)", operatorExpression1.toString());
    state.setStateValue(testStateVariable1, new ArrayList());
    Assert.assertEquals(new Integer(0), operatorExpression1.evaluate(state));
    state.setStateValue(testStateVariable1, list);
    Assert.assertEquals(new Integer(2), operatorExpression1.evaluate(state));
    
    // JoinLists
    new JoinLists();
    Operator joinLists = JoinLists.getInstance();
    operatorExpression1 = new OperatorExpression(joinLists, new ArrayList(), new ArrayList());
    Assert.assertEquals(new ArrayList(), operatorExpression1.evaluate(state));
    List list1 = new ArrayList();
    list1.add("abc");
    list1.add(new Double(100.0d));
    operatorExpression1 = new OperatorExpression(joinLists, list1, new ArrayList());
    Assert.assertEquals(list1, operatorExpression1.evaluate(state));
    Assert.assertEquals("(join-lists [abc, 100.0] [])", operatorExpression1.toString());
    operatorExpression1 = new OperatorExpression(joinLists, new ArrayList(), list1);
    Assert.assertEquals(list1, operatorExpression1.evaluate(state));
    List list2 = new ArrayList();
    list2.add(new Integer(0));
    list2.add("def");
    operatorExpression1 = new OperatorExpression(joinLists, list1, list2);
    Assert.assertTrue(operatorExpression1.evaluate(state) instanceof List);
    Assert.assertTrue(((List) operatorExpression1.evaluate(state)).size() == 4);
    Assert.assertTrue(((List) operatorExpression1.evaluate(state)).contains("abc"));
    Assert.assertTrue(((List) operatorExpression1.evaluate(state)).contains("def"));
    operatorExpression1 = new OperatorExpression(joinLists, list1, testStateVariable1);
    state.setStateValue(testStateVariable1, list1);
    Assert.assertTrue(((List) operatorExpression1.evaluate(state)).size() == 4);
    Assert.assertTrue(((List) operatorExpression1.evaluate(state)).contains("abc"));
    Assert.assertTrue(! ((List) operatorExpression1.evaluate(state)).contains("def"));
    
    // FirstInList
    new FirstInList();
    Operator firstInList = FirstInList.getInstance();
    operatorExpression1 = new OperatorExpression(firstInList, list1);
    Assert.assertEquals("abc", operatorExpression1.evaluate(state));
    state.setStateValue(testStateVariable1, list2);
    operatorExpression1 = new OperatorExpression(firstInList, testStateVariable1);
    Assert.assertEquals(new Integer(0), operatorExpression1.evaluate(state));
    Assert.assertEquals("(first-in-list test-state-variable1)", operatorExpression1.toString());
    
    // RestOfList
    new RestOfList();
    Operator restOfList = RestOfList.getInstance();
    operatorExpression1 = new OperatorExpression(restOfList, list1);
    List list3 = new ArrayList();
    list3.add(new Double(100.0d));
    Assert.assertEquals(list3, operatorExpression1.evaluate(state));
    Assert.assertEquals("(rest-of-list [abc, 100.0])", operatorExpression1.toString());
    
    // (join-lists (the-list (first-in-list [abc, 100.0])) (rest-of-list [abc, 100.0]))
    operatorExpression1 = new OperatorExpression(firstInList, list1);
    OperatorExpression operatorExpression2 = new OperatorExpression(restOfList, list1);
    OperatorExpression operatorExpression3 = 
      new OperatorExpression(theList, operatorExpression1);
    OperatorExpression operatorExpression4 = 
      new OperatorExpression(joinLists, operatorExpression3, operatorExpression2);
    Assert.assertEquals("(join-lists (the-list (first-in-list [abc, 100.0])) (rest-of-list [abc, 100.0]))", 
                        operatorExpression4.toString());
    Assert.assertEquals(list1, operatorExpression4.evaluate(state));
    
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