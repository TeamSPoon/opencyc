package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.state.StateVariable;

//// External Imports
import java.util.*;

import java.util.logging.Logger;

import junit.framework.*;


/**
 * Provides a suite of JUnit test cases for the org.opencyc.elf.wm package.
 * 
 * <p></p>
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
  
  /**
   * Construct a new UnitTest object.
   * 
   * @param name the test case name.
   */
  public UnitTest(String name) {
    super(name);
  }

  //// Public Area
  
  /**
   * Runs the unit tests
   * @return the unit test suite
   */
  public static Test suite() {
    logger = Logger.getLogger("org.opencyc.elf");
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testEntityFrame"));

    return testSuite;
  }

  /**
   * Tests EntityFrame object behavior.
   */
  public void testEntityFrame() {
    System.out.println("\n*** testEntityFrame ***");

    EntityFrame entityFrame1 = new EntityFrame();
    StateVariable stateVariable1 = new StateVariable(String.class, 
                                                     "stateVariable1", 
                                                     "test state variable stateVariable1");
    Object attribute1 = new Object();
    Assert.assertNull(entityFrame1.getState().getStateValue(stateVariable1));
    entityFrame1.getState().setStateValue(stateVariable1, "abc");
    Assert.assertEquals("abc", entityFrame1.getState().getStateValue(stateVariable1));

    EntityFrame entityFrame2 = new EntityFrame(entityFrame1);
    Assert.assertEquals(entityFrame1, entityFrame2);

    StateVariable stateVariable2 = new StateVariable(String.class, 
                                                     "stateVariable2", 
                                                     "test state variable stateVariable2");
    entityFrame2.getState().setStateValue(stateVariable2, "def");
    Assert.assertTrue(!entityFrame1.equals(entityFrame2));
    System.out.println(entityFrame2.toString());

    int iteratorCount = 0;
    Object iterator1 = entityFrame1.getState().stateVariables();
    Assert.assertTrue(iterator1 instanceof Iterator);

    Iterator iterator2 = entityFrame1.getState().stateVariables();

    while (iterator2.hasNext()) {
      Object stateVariable = iterator2.next();
      iteratorCount++;
      Assert.assertEquals(stateVariable1, stateVariable);
    }

    Assert.assertEquals(1, iteratorCount);
    Assert.assertTrue(entityFrame1.getState().isStateVariable(stateVariable1));

    System.out.println("*** testEntityFrame OK ***");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the logger
   */
  protected static Logger logger;

  //// Main
  
  /**
   * Main method in case tracing is prefered over running JUnit.
   * @param args the command line arguments (unused)
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

}