package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.BehaviorGeneration;

import org.opencyc.elf.bg.planner.JobAssigner;
import org.opencyc.elf.bg.planner.PlanSelector;
import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.DoTaskMsg;

import org.opencyc.elf.sp.*;
import org.opencyc.elf.vj.EntityEvaluator;
import org.opencyc.elf.vj.PlanEvaluator;
import org.opencyc.elf.vj.ValueJudgement;

import org.opencyc.elf.wm.*;

//// External Imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Puttable;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/** Provides a suite of JUnit test cases for the org.opencyc.elf package.
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
    testSuite.addTest(new UnitTest("testNodeFactory"));
    testSuite.addTest(new UnitTest("testBehaviorGeneration"));
    testSuite.addTest(new UnitTest("testBehaviorEngine"));
    return testSuite;
  }

  /** Tests NodeFactory object behavior.
   */
  public void testNodeFactory() {
    System.out.println("\n*** testNodeFactory ***");
    
    logger.info("Creating NodeFactory");
    NodeFactory nodeFactory = new NodeFactory();
    Node node = nodeFactory.makeNodeShell("test-node");
    
    Assert.assertNotNull(node.getLogger());
    Assert.assertTrue(node.getLogger() instanceof Logger);
    
    Assert.assertNotNull(node.getBehaviorGeneration());
    Assert.assertTrue(node.getBehaviorGeneration() instanceof BehaviorGeneration);
    BehaviorGeneration behaviorGenertation = node.getBehaviorGeneration();
    Assert.assertEquals(node, behaviorGenertation.getNode());
    
    Assert.assertNotNull(behaviorGenertation.getJobAssigner());
    Assert.assertTrue(behaviorGenertation.getJobAssigner() instanceof JobAssigner);
    JobAssigner jobAssigner = behaviorGenertation.getJobAssigner();
    Assert.assertEquals(node, jobAssigner.getNode());
    Assert.assertNotNull(jobAssigner.getChannel());
    Assert.assertTrue(jobAssigner.getChannel() instanceof Puttable);

    Assert.assertNotNull(behaviorGenertation.getPlanSelector());
    Assert.assertTrue(behaviorGenertation.getPlanSelector() instanceof PlanSelector);
    PlanSelector planSelector = behaviorGenertation.getPlanSelector();
    Assert.assertEquals(node, planSelector.getNode());

    Assert.assertNotNull(node.getWorldModel());
    Assert.assertTrue(node.getWorldModel() instanceof WorldModel);
    WorldModel worldModel = node.getWorldModel();
    Assert.assertEquals(node, worldModel.getNode());
    
    Assert.assertNotNull(worldModel.getKnowledgeBase());
    Assert.assertTrue(worldModel.getKnowledgeBase() instanceof KnowledgeBase);
    KnowledgeBase knowledgeBase = worldModel.getKnowledgeBase();
    Assert.assertEquals(node, knowledgeBase.getNode());
    
    Assert.assertNotNull(worldModel.getPlanSimulator());
    Assert.assertTrue(worldModel.getPlanSimulator() instanceof PlanSimulator);
    PlanSimulator planSimulator = worldModel.getPlanSimulator();
    Assert.assertEquals(node, planSimulator.getNode());
    
    Assert.assertNotNull(worldModel.getPredictor());
    Assert.assertTrue(worldModel.getPredictor() instanceof Predictor);
    Predictor predictor = worldModel.getPredictor();
    Assert.assertEquals(node, predictor.getNode());
    
    Assert.assertNotNull(node.getValueJudgement());
    Assert.assertTrue(node.getValueJudgement() instanceof ValueJudgement);
    ValueJudgement valueJudgement = node.getValueJudgement();
    Assert.assertEquals(node, valueJudgement.getNode());
    
    Assert.assertNotNull(valueJudgement.getEntityEvaluator());
    Assert.assertTrue(valueJudgement.getEntityEvaluator() instanceof EntityEvaluator);
    EntityEvaluator entityEvaluator = valueJudgement.getEntityEvaluator();
    Assert.assertEquals(node, entityEvaluator.getNode());
    
    Assert.assertNotNull(valueJudgement.getPlanEvaluator());
    Assert.assertTrue(valueJudgement.getPlanEvaluator() instanceof PlanEvaluator);
    PlanEvaluator planEvaluator = valueJudgement.getPlanEvaluator();
    Assert.assertEquals(node, planEvaluator.getNode());
    
    Assert.assertNotNull(node.getSensoryPerception());
    Assert.assertTrue(node.getSensoryPerception() instanceof SensoryPerception);
    SensoryPerception sensoryPerception = node.getSensoryPerception();
    Assert.assertEquals(node, sensoryPerception.getNode());
    Assert.assertNotNull(sensoryPerception.getChannel());
    Assert.assertTrue(sensoryPerception.getChannel() instanceof Puttable);
        
    Assert.assertNotNull(sensoryPerception.getEstimator());
    Assert.assertTrue(sensoryPerception.getEstimator() instanceof Estimator);
    Estimator estimator = sensoryPerception.getEstimator();
    Assert.assertEquals(node, estimator.getNode());
    
    Assert.assertNotNull(sensoryPerception.getFeatureExtractor());
    Assert.assertTrue(sensoryPerception.getFeatureExtractor() instanceof FeatureExtractor);
    FeatureExtractor featureExtractor = sensoryPerception.getFeatureExtractor();
    Assert.assertEquals(node, featureExtractor.getNode());
    
    Assert.assertNotNull(sensoryPerception.getHypothesisEvaluator());
    Assert.assertTrue(sensoryPerception.getHypothesisEvaluator() instanceof HypothesisEvaluator);
    HypothesisEvaluator hypothesisEvaluator = sensoryPerception.getHypothesisEvaluator();
    Assert.assertEquals(node, hypothesisEvaluator.getNode());
    
    Assert.assertNotNull(sensoryPerception.getHypothesisFormer());
    Assert.assertTrue(sensoryPerception.getHypothesisFormer() instanceof HypothesisFormer);
    HypothesisFormer hypothesisFormer = sensoryPerception.getHypothesisFormer();
    Assert.assertEquals(node, hypothesisFormer.getNode());
    
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
    }
    System.out.println("*** testNodeFactory OK ***");
  }

  /** Tests BehaviorGeneration object behavior. */
  public void testBehaviorGeneration() {
    System.out.println("\n*** testBehaviorGeneration ***");
    
    logger.info("Testing behavior generation");
    new KnowledgeBase();
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobLibrary();
    (new JobFactory()).getInstance().populateJobLibrary();
    new ExperienceLibrary();
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    new NodeFactory();
    Node node = NodeFactory.getInstance().makeNodeShell("test-node");
    
    ActionFactory actionFactory = new ActionFactory();
    Action converseWithUserAction = ActionLibrary.getInstance().getAction(Action.CONVERSE_WITH_USER);
    List parameterValues = new ArrayList();
    parameterValues.add(">");
    converseWithUserAction.setParameterValues(parameterValues);
    
    Assert.assertEquals("converse with user", converseWithUserAction.getName());
    Assert.assertEquals("prompt", converseWithUserAction.getParameterNames().get(0));
    Assert.assertEquals("[Action: converse with user( prompt: \">\")]", 
                        converseWithUserAction.toString());    
    node.getSensoryPerception().initialize((Puttable) null);
    node.getBehaviorGeneration().getJobAssigner().initialize((Puttable) null);
    //jobAssigner generates consolePromptedInput action for the ConsoleActuator
    TaskCommand taskCommand = new TaskCommand(converseWithUserAction, null);
    DoTaskMsg doTaskMsg = new DoTaskMsg((NodeComponent) null, taskCommand);
    Assert.assertEquals("[TaskCommand: [Action: converse with user( prompt: \">\")]]", 
                        taskCommand.toString());
    try {
      node.getBehaviorGeneration().getJobAssigner().getChannel().put(doTaskMsg);
    }
    catch (InterruptedException e) {
      Assert.fail(e.getMessage());
    }
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
    }
    System.out.println("*** testBehaviorGeneration OK ***");
  }
  
  /** Tests BehaviorEngine.
   */
  public void testBehaviorEngine () {
    System.out.println("\n*** testBehaviorEngine ***");
    logger.info("Testing behavior engine");
    BehaviorEngine behaviorEngine = new BehaviorEngine();
    behaviorEngine.initialize();
    Assert.assertNotNull(ResourcePool.getInstance());
    Assert.assertNotNull(ResourcePool.getInstance().getResource(Resource.CONSOLE));
    Assert.assertEquals("[Resource: console]", ResourcePool.getInstance().getResource(Resource.CONSOLE).toString());
    Assert.assertNotNull(JobLibrary.getInstance());
    Assert.assertNotNull(JobLibrary.getInstance().getJobSet(Action.CONVERSE_WITH_USER));
    Assert.assertEquals("[[Job for [[Resource: console]] action: converse with user]]", 
                        JobLibrary.getInstance().getJobSet(Action.CONVERSE_WITH_USER).toString());
    behaviorEngine.execute();
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
    }
    System.out.println("*** testBehaviorEngine OK ***");
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the logger */
  protected static Logger logger;
  
  //// Main
  
  /** Main method in case tracing is prefered over running JUnit.
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    logger = Logger.getLogger("org.opencyc.elf");
    TestRunner.run(suite());
    System.exit(0);
  }
}
