package org.opencyc.elf.wm.state;

//// Internal Imports
import org.opencyc.elf.Node;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.StateVariableFactory;
import org.opencyc.elf.wm.StateVariableLibrary;

//// External Imports
import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Provides a suite of JUnit test cases for the org.opencyc.elf.bg.state
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
   *
   * @return the test suite
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testState"));

    return testSuite;
  }

  /** Tests State object behavior. */
  public void testState() {
    System.out.println("\n*** testState ***");
    new NodeFactory();
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    
    StateVariable stateVariable1 = new StateVariable(String.class,
                                                     "stateVariable1", 
                                                     "test state variable stateVariable1");
    State state = new State(null);
    Assert.assertTrue(! state.isStateVariable(stateVariable1));
    state.setStateValue(stateVariable1, "abc");
    Assert.assertTrue(state.isStateVariable(stateVariable1));
    Assert.assertEquals("abc", state.getStateValue(stateVariable1));
    Node node = NodeFactory.getInstance().makeNodeShell();
    Node parentNode = NodeFactory.getInstance().makeNodeShell();
    node.setParentNode(parentNode);
    State parentState = parentNode.getWorldModel().getState();
    state = node.getWorldModel().getState();
    parentState.setStateValue(stateVariable1, "def");
    Assert.assertEquals("def", state.getStateValue(stateVariable1));
    Assert.assertEquals("def", parentState.getStateValue(stateVariable1));
    StateVariable stateVariable2 = new StateVariable(Integer.class,
                                                     "stateVariable2", 
                                                     "test state variable stateVariable2");
    Assert.assertTrue(! parentState.isStateVariable(stateVariable2));
    Assert.assertTrue(! state.isStateVariable(stateVariable2));
    parentState.setStateValue(stateVariable2, new Integer(-1));
    Assert.assertTrue(parentState.isStateVariable(stateVariable2));
    Assert.assertTrue(state.isStateVariable(stateVariable2));
    Assert.assertEquals(new Integer(-1), state.getStateValue(stateVariable2));
    Assert.assertEquals(new Integer(-1), parentState.getStateValue(stateVariable2));
    state.setStateValue(stateVariable2, new Integer(99));
    Assert.assertEquals(new Integer(99), state.getStateValue(stateVariable2));
    Assert.assertEquals(new Integer(-1), parentState.getStateValue(stateVariable2));
    
    System.out.println("*** testState OK ***");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
  /** Main method in case tracing is prefered over running JUnit.
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}