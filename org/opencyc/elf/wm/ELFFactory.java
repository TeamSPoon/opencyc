package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.LowestLevelNode;
import org.opencyc.elf.Node;

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
import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;

/**
 * Factory that instantiates an Elementary Loop Functioning (ELF) node.
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
public class ELFFactory {
  
  //// Constructors
  
  /** Creates a new instance of ELFFactory. */
  public ELFFactory() {
    logger = Logger.getLogger("org.opencyc.elf.ELFFactory");
  }
  
  //// Public Area
  
  /**
   * Makes a shell ELF node.
   *
   * @param name the node name
   * @return a shell ELF node
   */
  public Node makeNodeShell (String name) {
    node = new Node(name);
    assembleNode();
    return node;
  }
  
  /**
   * Makes a shell lowest level ELF node.
   *
   * @return a shell lowest level ELF node
   */
  public LowestLevelNode makeLowestLevelShellNode() {
    node = new LowestLevelNode();
    assembleNode();
    assembleLowestLevelNode();
    return (LowestLevelNode) node;
  }
  
  //// Protected Area
  
  /**
   * Assembles the given node.
   */
  protected void assembleNode () {
    jobAssignerChannel = new BoundedBuffer(CHANNEL_CAPACITY);
    sensoryPerceptionChannel = new BoundedBuffer(CHANNEL_CAPACITY);
    makeBehaviorGenerationShell();
    makeWorldModelShell();
    node.setWorldModel(worldModel);
    makeValueJudgementShell();
    node.setValueJudgement(valueJudgement);
    makeSensoryPerceptionShell();
    node.setSensoryPerception(sensoryPerception);
  }
  
  /**
   * Completes the assembly of the given lowest level node.
   */
  protected void assembleLowestLevelNode() {
    makeActuatorShell();
    ((LowestLevelNode) node).setActuator(actuator);
    makeSensorShell();
    ((LowestLevelNode) node).setSensor(sensor);
  }
  
  /**
   * Makes a behavior generation shell.
   */
  protected void makeBehaviorGenerationShell () {
    behaviorGeneration = new BehaviorGeneration(node);
    jobAssigner = new JobAssigner(node, jobAssignerChannel, null);
    planSelector = new PlanSelector();
    planSelector.setNode(node);
    behaviorGeneration.setPlanSelector(planSelector);
  }
  
  /**
   * Makes world model shell.
   */
  protected void makeWorldModelShell() {
    worldModel = new WorldModel();
    worldModel.setNode(node);
    knowledgeBase = new KnowledgeBase();
    worldModel.setKnowledgeBase(knowledgeBase);
    knowledgeBase.setNode(node);
    planSimulator = new PlanSimulator();
    worldModel.setPlanSimulator(planSimulator);
    planSimulator.setNode(node);
    predictor = new Predictor();
    worldModel.setPredictor(predictor);
    predictor.setNode(node);
  }
  
  /**
   * Makes a value judgement shell.
   */
  protected void makeValueJudgementShell () {
    valueJudgement = new ValueJudgement();
    valueJudgement.setNode(node);
    entityEvaluator = new EntityEvaluator();
    valueJudgement.setEntityEvaluator(entityEvaluator);
    entityEvaluator.setNode(node);
    planEvaluator = new PlanEvaluator();
    valueJudgement.setPlanEvaluator(planEvaluator);
    planEvaluator.setNode(node);
  }
  
  /**
   * Makes a sensory perception shell.
   */
  protected void makeSensoryPerceptionShell () {
    sensoryPerception = new SensoryPerception(sensoryPerceptionChannel, null);
    sensoryPerception.setNode(node);
    estimator = new Estimator();
    sensoryPerception.setEstimator(estimator);
    estimator.setNode(node);
    featureExtractor = new FeatureExtractor();
    sensoryPerception.setFeatureExtractor(featureExtractor);
    featureExtractor.setNode(node);
    hypothesisEvaluator = new HypothesisEvaluator();
    sensoryPerception.setHypothesisEvaluator(hypothesisEvaluator);
    hypothesisEvaluator.setNode(node);
    hypothesisFormer = new HypothesisFormer();
    sensoryPerception.setHypothesisFormer(hypothesisFormer);
    hypothesisFormer.setNode(node);
  }
  
  /**
   * Makes an actuator shell.
   */
  protected void makeActuatorShell () {
    //TODO
    //actuator = new Actuator();
    //actuator.setNode(node);
  }
  
  /**
   * Makes a sensor shell.
   */
  protected void makeSensorShell () {
    //TODO
    //sensor = new Sensor();
    //sensor.setNode(node);
  }
  
  //// Private Area
  
  //// Internal Rep
    
  /**
   * the logger
   */
  protected Logger logger;
  
  /**
   * the Elementary Loop Functioning (ELF) node
   */
  protected Node node;
  
  /**
   * the behavior generation node component
   */
  protected BehaviorGeneration behaviorGeneration;
  
  /**
   * the job assigner node component
   */
  protected JobAssigner jobAssigner;
  
  /**
   * the plan selector node component
   */
  protected PlanSelector planSelector;
  
  /**
   * the world model node component
   */
  protected WorldModel worldModel;
  
  /**
   * the knowledge base node component
   */
  protected KnowledgeBase knowledgeBase;
  
  /**
   * the plan simulator node component
   */
  protected PlanSimulator planSimulator;
  
  /**
   * the predictor node component
   */
  protected Predictor predictor;
  
  /**
   * the value judgement node component
   */
  protected ValueJudgement valueJudgement;
  
  /**
   * the plan evaluator node component
   */
  protected PlanEvaluator planEvaluator;
  
  /**
   * the entity evaluator node component
   */
  protected EntityEvaluator entityEvaluator;

  /**
   * the sensory perception node component
   */
  protected SensoryPerception sensoryPerception;
  
  /**
   * the estimator node component
   */
  protected Estimator estimator;
  
  /**
   * the feature extractor node component
   */
  protected FeatureExtractor featureExtractor;
  
  /**
   * the hypothesis evaluator node component
   */
  protected HypothesisEvaluator hypothesisEvaluator;
  
  /**
   * the hypothesis former node component
   */
  protected HypothesisFormer hypothesisFormer;
  
  /**
   * an actuator node component
   */
  protected Actuator actuator;
  
  /**
   * a sensor node component
   */
  protected Sensor sensor;
  
  /**
   * the maximum number of items that can be put into an inter-process
   * communications channel
   */
  protected int CHANNEL_CAPACITY = 100;
  
  /**
   * the job assigner channel
   */
  protected Channel jobAssignerChannel;
  
  /**
   * the higher level node's executor channel
   */
  protected Takable executorChannel;
  
  /**
   * the takable channel from which messages are input
   */
  protected Channel sensoryPerceptionChannel;

  /**
   * the puttable channel to which sensory processing messages are output for the next
   * higher level
   */
  protected Puttable nextHigherLevelSensoryPerceptionChannel;
  
  //// Main
  
}
