package org.opencyc.elf.wm;

import java.util.*;

import junit.framework.*;


/**
 * Provides a suite of JUnit test cases for the org.opencyc.elf.wm package.
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
    testSuite.addTest(new UnitTest("testEntityFrame"));

    return testSuite;
  }

  /**
   * Tests EntityFrame object behavior.
   */
  public void testEntityFrame() {
    System.out.println("\n*** testEntityFrame ***");

    EntityFrame entityFrame1 = new EntityFrame();
    Object stateVariable1 = new Object();
    Object attribute1 = new Object();
    Assert.assertNull(entityFrame1.getState().getStateValue(stateVariable1));
    entityFrame1.getState().setStateValue(stateVariable1, "abc");
    Assert.assertEquals("abc", entityFrame1.getState().getStateValue(stateVariable1));

    EntityFrame entityFrame2 = new EntityFrame(entityFrame1);
    Assert.assertEquals(entityFrame1, entityFrame2);

    Object stateVariable2 = new Object();
    entityFrame2.getState().setStateValue(stateVariable2, "def");
    Assert.assertTrue(!entityFrame1.equals(entityFrame2));
    System.out.println(entityFrame2.toString());

    String context = "context";
    entityFrame2 = new EntityFrame(entityFrame1);
    entityFrame1.getState().setContext(context);
    Assert.assertEquals(context, entityFrame1.getState().getContext());
    Assert.assertTrue(!entityFrame1.equals(entityFrame2));
    entityFrame2.getState().setContext(context);
    Assert.assertTrue(entityFrame1.equals(entityFrame2));
    entityFrame2.getState().setContext("context2");
    Assert.assertTrue(!entityFrame1.equals(entityFrame2));

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
}