package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.a.ConsoleOutput;

import org.opencyc.elf.bg.predicate.NotNull;

import org.opencyc.elf.bg.state.StateVariable;

import org.opencyc.elf.bg.planner.JobAssignment;
import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.TaskFrame;

import org.opencyc.elf.goal.Goal;

import org.opencyc.elf.s.ConsoleInput;
import org.opencyc.elf.s.Sensor;

//// External Imports
import java.util.*;

import java.util.logging.Logger;

import junit.framework.*;

import org.doomdark.uuid.UUID;

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
   *
   * @return the unit test suite
   */
  public static Test suite() {
    logger = Logger.getLogger("org.opencyc.elf");
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testEntityFrame"));
    testSuite.addTest(new UnitTest("testStateVariableLibrary"));
    testSuite.addTest(new UnitTest("testActionLibrary"));
    testSuite.addTest(new UnitTest("testGoalLibrary"));
    testSuite.addTest(new UnitTest("testResourcePool"));
    testSuite.addTest(new UnitTest("testJobAssignmentLibrary"));
    testSuite.addTest(new UnitTest("testTaskFrameLibrary"));
    testSuite.addTest(new UnitTest("testActuatorPool"));
    testSuite.addTest(new UnitTest("testSensorPool"));
    return testSuite;
  }

  /** Tests EntityFrame object behavior. */
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
  
  /** Tests state variable library and state variable factory behavior. */
  public void testStateVariableLibrary() {
    System.out.println("\n*** testStateVariableLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    Assert.assertNotNull(StateVariableLibrary.getInstance());
    Assert.assertNotNull(StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_INPUT));
    Assert.assertNotNull(StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_PROMPT));
    Assert.assertNotNull(StateVariableLibrary.getInstance().getStateVariable(StateVariable.USER_INPUT));
    Assert.assertNotNull(StateVariableLibrary.getInstance().getStateVariable(StateVariable.USER_PROMPT));
    Assert.assertEquals("consoleInput", 
                        StateVariableLibrary.getInstance().getStateVariable(StateVariable.CONSOLE_INPUT).toString());
    Assert.assertEquals("userPrompt", 
                        StateVariableLibrary.getInstance().getStateVariable(StateVariable.USER_PROMPT).toString());
    System.out.println("*** testStateVariableLibrary OK ***");
  }
  
  /** Tests action library and action factory behavior. */
  public void testActionLibrary() {
    System.out.println("\n*** testActionLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    Assert.assertNotNull(ActionLibrary.getInstance());
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.ABORT));
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.CONSOLE_PROMPTED_INPUT));
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.CONVERSE_WITH_USER));
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.EMERGENCY_STOP));
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.INIT));
    Assert.assertEquals("[Action: console prompted input( prompt: null)]", 
                        ActionLibrary.getInstance().getAction(Action.CONSOLE_PROMPTED_INPUT).toString());
    System.out.println("*** testActionLibrary OK ***");
  }
  
  /** Tests goal library and goal factory behavior. */
  public void testGoalLibrary() {
    System.out.println("\n*** testGoalLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new NotNull();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    Assert.assertNotNull(GoalLibrary.getInstance());
    Assert.assertNotNull(GoalLibrary.getInstance().getGoal(Goal.GET_CONSOLE_PROMPTED_INPUT));
    Assert.assertEquals("(not-null consoleInput)", 
                        GoalLibrary.getInstance().getGoal(Goal.GET_CONSOLE_PROMPTED_INPUT).toString());
    Assert.assertNotNull(GoalLibrary.getInstance().getGoal(Goal.GET_USER_INPUT));
    Assert.assertEquals("(not-null userInput)", 
                        GoalLibrary.getInstance().getGoal(Goal.GET_USER_INPUT).toString());
    System.out.println("*** testGoalLibrary OK ***");
  }
  
  /** Tests resource pool and resource factory behavior. */
  public void testResourcePool() {
    System.out.println("\n*** testResourcePool ***");
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    Assert.assertNotNull(ResourcePool.getInstance());
    Assert.assertNotNull(ResourcePool.getInstance().getResource(Resource.CONSOLE));
    Assert.assertEquals("[Resource: console]", 
                        ResourcePool.getInstance().getResource(Resource.CONSOLE).toString());
    System.out.println("*** testResourcePool OK ***");
  }
  
  /** Tests job assignment library and job assignment factory behavior. */
  public void testJobAssignmentLibrary() {
    System.out.println("\n*** testJobAssignmentLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new NotNull();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobAssignmentLibrary();
    (new JobAssignmentFactory()).getInstance().populateJobAssignmentLibrary();
    Assert.assertNotNull(JobAssignmentLibrary.getInstance());
    JobAssignment jobAssignment = JobAssignmentLibrary.getInstance().getJobAssignment(Action.CONVERSE_WITH_USER);
    Assert.assertNotNull(jobAssignment);
    Assert.assertEquals("[JobAssignment for [[Resource: console]] action: converse with user]", 
                        jobAssignment.toString());
    Assert.assertNotNull(jobAssignment.getActionName());
    Assert.assertEquals("converse with user", jobAssignment.getActionName());
    Action action = jobAssignment.getActionForScheduling();
    Assert.assertNotNull(action);    
    Assert.assertEquals("[Action: console prompted input( prompt: null)]", action.toString());
    ArrayList requiredResources = jobAssignment.getRequiredResources();
    Assert.assertNotNull(requiredResources);
    Assert.assertEquals(1, requiredResources.size());
    Assert.assertEquals("[Resource: console]", requiredResources.get(0).toString());
    System.out.println("*** testJobAssignmentLibrary OK ***");
  }
  
  /** Tests task frame library and task frame factory behavior. */
  public void testTaskFrameLibrary() {
    System.out.println("\n*** testTaskFrameLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new NotNull();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobAssignmentLibrary();
    (new JobAssignmentFactory()).getInstance().populateJobAssignmentLibrary();
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    new TaskFrameLibrary();
    (new TaskFrameFactory()).getInstance().populateTaskFrameLibrary();
    Assert.assertNotNull(TaskFrameLibrary.getInstance());
    TaskFrame taskFrame = TaskFrameLibrary.getInstance().getTaskFrame(Action.CONVERSE_WITH_USER);
    Assert.assertNotNull(taskFrame);
    Assert.assertEquals("[TaskFrame task: converse with user goal: (not-null userInput)]", 
                        taskFrame.toString());
    ArrayList actuators = taskFrame.getActuators();
    Assert.assertNotNull(actuators);
    Assert.assertEquals(1, actuators.size());
    Assert.assertEquals("[ConsoleOutput resources: [[Resource: console]]]", actuators.get(0).toString());
    ArrayList sensors = taskFrame.getSensors();
    Assert.assertNotNull(sensors);
    Assert.assertEquals(1, sensors.size());
    Assert.assertEquals("[ConsoleInput resources: [[Resource: console]]]", sensors.get(0).toString());
    Assert.assertNotNull(taskFrame.getTaskId());
    Assert.assertTrue(taskFrame.getTaskId() instanceof UUID);
    Assert.assertNotNull(taskFrame.getTaskName());
    Assert.assertEquals("converse with user", taskFrame.getTaskName());
    Assert.assertNotNull(taskFrame.getTaskAction());
    Assert.assertEquals("[Action: converse with user( prompt: null)]", taskFrame.getTaskAction().toString());
    ArrayList scheduleInfos = taskFrame.getScheduleInfos();
    Assert.assertNotNull(scheduleInfos);
    Assert.assertEquals(1, scheduleInfos.size());
    TaskFrame.ScheduleInfo scheduleInfo = (TaskFrame.ScheduleInfo) scheduleInfos.get(0);
    Assert.assertEquals("[ScheduleInfo schedule: [Schedule []] " +
                        "actuator: [ConsoleOutput resources: [[Resource: console]]] " + 
                        "sensor: [ConsoleInput resources: [[Resource: console]]]]", 
                        scheduleInfo.toString());
    
    System.out.println("*** testTaskFrameLibrary OK ***");
  }
  
  /** Tests actuator pool and actuator factory behavior. */
  public void testActuatorPool() {
    System.out.println("\n*** testActuatorPool ***");
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    Assert.assertNotNull(ActuatorPool.getInstance().getActuator(Actuator.CONSOLE_OUTPUT));
    Assert.assertEquals("[ConsoleOutput resources: [[Resource: console]]]", 
                        ActuatorPool.getInstance().getActuator(Actuator.CONSOLE_OUTPUT).toString());
    System.out.println("*** testActuatorPool OK ***");
  }
  
  /** Tests sensor pool and sensor factory behavior. */
  public void testSensorPool() {
    System.out.println("\n*** testSensorPool ***");
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    Assert.assertNotNull(SensorPool.getInstance().getSensor(Sensor.CONSOLE_INPUT));
    Assert.assertEquals("[ConsoleInput resources: [[Resource: console]]]", 
                        SensorPool.getInstance().getSensor(Sensor.CONSOLE_INPUT).toString());
    System.out.println("*** testSensorPool OK ***");
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