package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.bg.planner.JobAssigner;
import org.opencyc.elf.bg.planner.PlanSelector;

import org.opencyc.elf.s.Sensor;

import org.opencyc.elf.sp.Estimator;
import org.opencyc.elf.sp.FeatureExtractor;
import org.opencyc.elf.sp.HypothesisEvaluator;
import org.opencyc.elf.sp.HypothesisFormer;
import org.opencyc.elf.sp.SensoryPerception;

import org.opencyc.elf.vj.EntityEvaluator;
import org.opencyc.elf.vj.PlanEvaluator;
import org.opencyc.elf.vj.ValueJudgement;

import org.opencyc.elf.wm.KnowledgeBase;
import org.opencyc.elf.wm.PlanSimulator;
import org.opencyc.elf.wm.Predictor;
import org.opencyc.elf.wm.WorldModel;

//// External Imports
import junit.framework.*;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;

/**
 * Provides a suite of JUnit test cases for the org.opencyc.elf package.
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
   * @return the test suite
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testELFFactory"));

    return testSuite;
  }

  /**
   * Tests ELFFactory object behavior.
   */
  public void testELFFactory() {
    System.out.println("\n*** testELFFactory ***");
    ELFFactory elfFactory = new ELFFactory();
    Node node = elfFactory.makeNodeShell();
    
    Assert.assertNotNull(node.getBehaviorGeneration());
    Assert.assertTrue(node.getBehaviorGeneration() instanceof BehaviorGeneration);
    BehaviorGeneration behaviorGenertation = node.getBehaviorGeneration();
    Assert.assertEquals(node, behaviorGenertation.getNode());
    
    Assert.assertNotNull(behaviorGenertation.getJobAssigner());
    Assert.assertTrue(behaviorGenertation.getJobAssigner() instanceof JobAssigner);
    JobAssigner jobAssigner = behaviorGenertation.getJobAssigner();
    Assert.assertEquals(node, jobAssigner.getNode());

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
    
    System.out.println("*** testELFFactory OK ***");
  }

    //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
  /**
   * Main method in case tracing is prefered over running JUnit.
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
    System.exit(0);
  }
}
