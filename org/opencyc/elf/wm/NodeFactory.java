package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.Node;

import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.bg.planner.JobAssigner;
import org.opencyc.elf.bg.planner.PlanSelector;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.TaskFrame;

import org.opencyc.elf.s.Sensation;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;

/**
 * Factory that instantiates an Elementary Loop Functioning (ELF) node.  There is a singleton 
 * instance.
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
public class NodeFactory {
  
  //// Constructors
  
  /** Creates a new instance of NodeFactory. */
  public NodeFactory() {
    logger = Logger.getLogger("org.opencyc.elf.NodeFactory");
    nodeFactory = this;
  }
  
  //// Public Area
  
  /**
   * Gets the factory singleton instance.
   * 
   * @return the factory singleton instance
   */
  public static NodeFactory getInstance () {
    return nodeFactory;
  }
  
  /**
   * Makes a node given the set of task frames that it must process.
   *
   * @param taskFrameNames the set of task frames that it must process
   */
  public Node makeNode(List taskFrames) {
    HashSet scheduleNames = new HashSet();
    Iterator taskFrameIterator = taskFrames.iterator();
    while (taskFrameIterator.hasNext()) {
      TaskFrame taskFrame = (TaskFrame) taskFrameIterator.next();
      scheduleNames.add(taskFrame.getTaskName());
    }
    StringBuffer stringBuffer = new StringBuffer();
    Iterator scheduleNameIterator = scheduleNames.iterator();
    while (scheduleNameIterator.hasNext()) {
      stringBuffer.append(scheduleNameIterator.next().toString());
      stringBuffer.append(" / ");
    }
    if (stringBuffer.length() > 0)
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    String name = stringBuffer.toString();
    node = makeNodeShell(name);
    return node;
  }
  
  /**
   * Makes a shell node.
   *
   * @param name the node name
   * @return a shell node
   */
  public Node makeNodeShell (String name) {
    node = new Node(name);
    assembleNode();
    return node;
  }
   
  //// Protected Area
  
  /**
   * Assembles the given node.
   */
  protected void assembleNode () {
    makeBehaviorGenerationShell();
    makeWorldModelShell();
    node.setWorldModel(worldModel);
    makeValueJudgementShell();
    node.setValueJudgement(valueJudgement);
    makeSensoryPerceptionShell();
    node.setSensoryPerception(sensoryPerception);
  }
  
  /**
   * Makes a behavior generation shell.
   */
  protected void makeBehaviorGenerationShell () {
    behaviorGeneration = new BehaviorGeneration(node);
    List actionCapabilities = new ArrayList();
    actionCapabilities.add(Action.CONVERSE_WITH_USER);
    Channel jobAssignerChannel = new BoundedBuffer(CHANNEL_CAPACITY);
    jobAssigner = new JobAssigner(node, actionCapabilities, jobAssignerChannel);
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
    List sensationCapabilities = new ArrayList();
    sensationCapabilities.add(Sensation.CONSOLE_INPUT);
    String sensoryPerceptionName = "";
    Channel sensoryPerceptionChannel = new BoundedBuffer(CHANNEL_CAPACITY);
    sensoryPerception = new SensoryPerception(sensoryPerceptionName,               
                                              sensationCapabilities,
                                              sensoryPerceptionChannel);
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
  
  //// Private Area
  
  //// Internal Rep
    
  /**
   * the logger
   */
  protected Logger logger;
  
  /** the Elementary Loop Functioning (ELF) node */
  protected Node node;
  
  /** the behavior generation node component */
  protected BehaviorGeneration behaviorGeneration;
  
  /** the job assigner node component */
  protected JobAssigner jobAssigner;
  
  /** the plan selector node component */
  protected PlanSelector planSelector;
  
  /** the world model node component */
  protected WorldModel worldModel;
  
  /** the knowledge base node component */
  protected KnowledgeBase knowledgeBase;
  
  /** the plan simulator node component */
  protected PlanSimulator planSimulator;
  
  /** the predictor node component */
  protected Predictor predictor;
  
  /** the value judgement node component */
  protected ValueJudgement valueJudgement;
  
  /** the plan evaluator node component */
  protected PlanEvaluator planEvaluator;
  
  /** the entity evaluator node component */
  protected EntityEvaluator entityEvaluator;

  /** the sensory perception node component */
  protected SensoryPerception sensoryPerception;
  
  /** the estimator node component */
  protected Estimator estimator;
  
  /** the feature extractor node component */
  protected FeatureExtractor featureExtractor;
  
  /** the hypothesis evaluator node component */
  protected HypothesisEvaluator hypothesisEvaluator;
  
  /** the hypothesis former node component */
  protected HypothesisFormer hypothesisFormer;
  
  /** an actuator node component */
  protected Actuator actuator;
  
  /** a sensor node component */
  protected Sensor sensor;
  
  /**
   * the maximum number of items that can be put into an inter-process
   * communications channel
   */
  protected int CHANNEL_CAPACITY = 100;
  
  /** the node factory singleton instance */
  protected static NodeFactory nodeFactory;
  
  //// Main
  
}
