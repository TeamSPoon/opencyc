package org.opencyc.elf.bg.state;

import java.util.*;

import junit.framework.*;


/**
 * Provides a suite of JUnit test cases for the org.opencyc.elf.bg.state
 * package.
 * 
 * <p></p>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class UnitTest extends TestCase {
  /**
   * Main method in case tracing is prefered over running JUnit.
   * @param args DOCUMENT ME!
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  /**
   * Construct a new UnitTest object.
   * 
   * @param name the test case name.
   */
  public UnitTest(String name) {
    super(name);
  }

  /**
   * Runs the unit tests
   * @return DOCUMENT ME!
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testSituation"));

    return testSuite;
  }

  /**
   * Tests Situation object behavior.
   */
  public void testSituation() {
    System.out.println("\n*** testSituation ***");

    Situation situation1 = new Situation();
    Object stateVariable1 = new Object();
    Object attribute1 = new Object();
    Assert.assertNull(situation1.getState().getStateValue(stateVariable1));
    situation1.getState().setStateValue(stateVariable1, "abc");
    Assert.assertEquals("abc", situation1.getState().getStateValue(stateVariable1));

    Situation situation2 = new Situation(situation1);
    Assert.assertEquals(situation1, situation2);

    Object stateVariable2 = new Object();
    situation2.getState().setStateValue(stateVariable2, "def");
    Assert.assertTrue(!situation1.equals(situation2));
    System.out.println(situation2.toString());

    String context = "context";
    situation2 = new Situation(situation1);
    situation1.getState().setContext(context);
    Assert.assertEquals(context, situation1.getState().getContext());
    Assert.assertTrue(!situation1.equals(situation2));
    situation2.getState().setContext(context);
    Assert.assertTrue(situation1.equals(situation2));
    situation2.getState().setContext("context2");
    Assert.assertTrue(!situation1.equals(situation2));

    int iteratorCount = 0;
    Object iterator1 = situation1.getState().stateVariables();
    Assert.assertTrue(iterator1 instanceof Iterator);

    Iterator iterator2 = situation1.getState().stateVariables();

    while (iterator2.hasNext()) {
      Object stateVariable = iterator2.next();
      iteratorCount++;
      Assert.assertEquals(stateVariable1, stateVariable);
    }

    Assert.assertEquals(1, iteratorCount);
    Assert.assertTrue(situation1.getState().isStateVariable(stateVariable1));

    System.out.println("*** testSituation OK ***");
  }
}