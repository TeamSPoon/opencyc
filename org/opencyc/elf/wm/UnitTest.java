package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.a.DirectActuator;
import org.opencyc.elf.bg.planner.Job;
import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.predicate.NotNull;
import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.goal.Goal;
import org.opencyc.elf.s.DirectSensor;
import org.opencyc.elf.s.Sensor;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Provides a suite of JUnit test cases for the org.opencyc.elf.wm package.
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
  
  /** Runs the unit tests
   *
   * @return the unit test suite
   */
  public static Test suite() {
    logger = Logger.getLogger("org.opencyc.elf");
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testStateVariableLibrary"));
    testSuite.addTest(new UnitTest("testActionLibrary"));
    testSuite.addTest(new UnitTest("testGoalLibrary"));
    testSuite.addTest(new UnitTest("testResourcePool"));
    testSuite.addTest(new UnitTest("testJobLibrary"));
    testSuite.addTest(new UnitTest("testActuatorPool"));
    testSuite.addTest(new UnitTest("testSensorPool"));
    return testSuite;
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
    Assert.assertNotNull(ActionLibrary.getInstance().getAction(Action.INITIALIZE));
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
    Assert.assertNotNull(GoalLibrary.getInstance().getGoal(Goal.PERCEIVE_SENSATION));
    Assert.assertEquals("(not-null \"consoleInput\")", 
                        GoalLibrary.getInstance().getGoal(Goal.PERCEIVE_SENSATION).toString());
    Assert.assertNotNull(GoalLibrary.getInstance().getGoal(Goal.PERCEIVE_SENSATION));
    Assert.assertEquals("(not-null \"userInput\")", 
                        GoalLibrary.getInstance().getGoal(Goal.PERCEIVE_SENSATION).toString());
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
  
  /** Tests job library and job factory behavior. */
  public void testJobLibrary() {
    System.out.println("\n*** testJobLibrary ***");
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new NotNull();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobLibrary();
    (new JobFactory()).getInstance().populateJobLibrary();
    Assert.assertNotNull(JobLibrary.getInstance());
    List jobSets = JobLibrary.getInstance().getJobSets(Action.CONVERSE_WITH_USER);
    Assert.assertNotNull(jobSets);
    Assert.assertEquals(1, jobSets.size());
    List jobSet = (List) jobSets.get(0);
    Assert.assertNotNull(jobSet);
    Assert.assertEquals(1, jobSet.size());
    Job job = (Job) jobSet.get(0);
    Assert.assertNotNull(job);
    Assert.assertEquals("[JobAssignment for [Action: converse with user( prompt: null)] using: [[Resource: console]]]", 
                        job.toString());
    Assert.assertNotNull(job.getCommandName());
    Assert.assertEquals("", job.getCommandName());
    List requiredResources = job.getRequiredResources();
    Assert.assertNotNull(requiredResources);
    Assert.assertEquals(1, requiredResources.size());
    Assert.assertEquals("[Resource: console]", requiredResources.get(0).toString());
    System.out.println("*** testJobLibrary OK ***");
  }
  
  /** Tests actuator pool and actuator factory behavior. */
  public void testActuatorPool() {
    System.out.println("\n*** testActuatorPool ***");
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    Assert.assertNotNull(ActuatorPool.getInstance().getActuator(DirectActuator.CONSOLE_OUTPUT));
    Assert.assertEquals("[ConsoleOutput resources: [[Resource: console]]]", 
                        ActuatorPool.getInstance().getActuator(DirectActuator.CONSOLE_OUTPUT).toString());
    System.out.println("*** testActuatorPool OK ***");
  }
  
  /** Tests sensor pool and sensor factory behavior. */
  public void testSensorPool() {
    System.out.println("\n*** testSensorPool ***");
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    Assert.assertNotNull(SensorPool.getInstance().getSensor(DirectSensor.CONSOLE_INPUT));
    Assert.assertEquals("[DirectSensor resources: [[Resource: console]]]", 
                        SensorPool.getInstance().getSensor(DirectSensor.CONSOLE_INPUT).toString());
    System.out.println("*** testSensorPool OK ***");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the logger */
  protected static Logger logger;

  //// Main
  
  /** Main method in case tracing is prefered over running JUnit.
   *
   * @param args the command line arguments (unused)
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }

}