package org.opencyc.elf.sp;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ObservedInputMsg;
import org.opencyc.elf.message.PerceivedSensoryInputMsg;

import org.opencyc.elf.s.Sensor;

//// External Imports
import java.util.ArrayList;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * Provides Sensory Perception for the Elementary Loop Functioning (ELF).<br>
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
public class SensoryPerception extends NodeComponent {
  
  //// Constructors
  
  /**
   * Constructs a new SensoryPerception object.
   */
  public SensoryPerception() {
  }

  /** 
   * Creates a new instance of SensoryPerception with the given
   * input and output message channels.
   *
   * @param sensoryPerceptionChannel the takable channel from which messages are input
   * @param nextHigherLevelSensoryPerceptionChannel the puttable channel to which messages are output
   * entity evaluator node component in value judgement
   */
  public SensoryPerception(Takable sensoryPerceptionChannel,
                           Puttable nextHigherLevelSensoryPerceptionChannel) {
    this.sensoryPerceptionChannel = sensoryPerceptionChannel;
    consumer = new Consumer(sensoryPerceptionChannel,
                            nextHigherLevelSensoryPerceptionChannel,
                            this);
    executor = new ThreadedExecutor();
    try {
      executor.execute(consumer);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  //// Public Area
  
  /** 
   * Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   *
   */
  public Puttable getChannel() {
    return (Puttable) sensoryPerceptionChannel;
  }  

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "SensoryPerception for " + node.getName();
  }

  /**
   * Gets the parent node's SensoryPerception object.
   * 
   * @return the parent node's SensoryPerception object
   */
  public SensoryPerception getParentSensoryPerception() {
    return parentSensoryPerception;
  }

  /**
   * Sets the parent node's SensoryPerception object.
   * 
   * @param parentSensoryPerception the parent node's SensoryPerception object
   */
  public void setParentSensoryPerception(SensoryPerception parentSensoryPerception) {
    this.parentSensoryPerception = parentSensoryPerception;
  }

  /**
   * Gets the child nodes' SensoryPerception objects.
   * 
   * @return the child nodes' SensoryPerception objects
   */
  public ArrayList getChildrenSensoryPerception() {
    return childrenSensoryPerception;
  }

  /**
   * Sets the child nodes' SensoryPerception objects.
   * 
   * @param childrenSensoryPerception the child nodes' SensoryPerception
   *        objects
   */
  public void setChildrenSensoryPerception(ArrayList childrenSensoryPerception) {
    this.childrenSensoryPerception = childrenSensoryPerception;
  }
  

  /**
   * Gets the estimator node component
   *
   * @return the estimator node component
   */
  public Estimator getEstimator () {
    return estimator;
  }

  /**
   * Sets the estimator node component
   *
   * @param estimator the estimator node component
   */
  public void setEstimator (Estimator estimator) {
    this.estimator = estimator;
  }

  /**
   * Gets the feature extractor node component
   *
   * @return the feature extractor node component
   */
  public FeatureExtractor getFeatureExtractor () {
    return featureExtractor;
  }

  /**
   * Sets the feature extractor node component
   *
   * @param featureExtractor the feature extractor node component
   */
  public void setFeatureExtractor (FeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }

  /**
   * Gets the hypothesis evaluator node component
   *
   * @return the hypothesis evaluator node component
   */
  public HypothesisEvaluator getHypothesisEvaluator () {
    return hypothesisEvaluator;
  }

  /**
   * Sets the hypothesis evaluator node component
   *
   * @param hypothesisEvaluator the hypothesis evaluator node component
   */
  public void setHypothesisEvaluator (HypothesisEvaluator hypothesisEvaluator) {
    this.hypothesisEvaluator = hypothesisEvaluator;
  }

  /**
   * Gets the hypothesis former node component
   *
   * @return the hypothesis former node component
   */
  public HypothesisFormer getHypothesisFormer () {
    return hypothesisFormer;
  }

  /**
   * Sets the hypothesis former node component
   *
   * @param xxx the hypothesis former node component
   */
  public void setHypothesisFormer (HypothesisFormer hypothesisFormer) {
    this.hypothesisFormer = hypothesisFormer;
  }

  //// Protected Area
    
  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable sensoryPerceptionChannel;
    
    /**
     * the puttable channel to which sensory processing messages are output for the next
     * higher level
     */
    protected final Puttable nextHigherLevelSensoryPerceptionChannel;

    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param sensoryPerceptionChannel the takable channel from which messages are input
     * @param nextHigherLevelSensoryPerceptionChannel the puttable channel to which messages are output
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable sensoryPerceptionChannel,
                        Puttable nextHigherLevelSensoryPerceptionChannel,
                        NodeComponent nodeComponent) { 
      this.sensoryPerceptionChannel = sensoryPerceptionChannel;
      this.nextHigherLevelSensoryPerceptionChannel = nextHigherLevelSensoryPerceptionChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) sensoryPerceptionChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }

    /**
     * Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof ObservedInputMsg)
        processObservedInputMsg((ObservedInputMsg) genericMsg);
      else if (genericMsg instanceof PerceivedSensoryInputMsg)
        processPerceivedSensoryInputMsg((PerceivedSensoryInputMsg) genericMsg);
    }
      
    /**
     * Processes the observed input message.
     */
    protected void processObservedInputMsg(ObservedInputMsg observedInputMsg) {
      Object obj = observedInputMsg.getObj();
      Object data = observedInputMsg.getData();
      //TODO
    }
    
    /**
     * Processes the perceived sensory input message received from a next level lower sensory
     * processing node component.
     */
    protected void processPerceivedSensoryInputMsg(PerceivedSensoryInputMsg perceivedSensoryInputMsg) {
      Object obj = perceivedSensoryInputMsg.getObj();
      Object data = perceivedSensoryInputMsg.getData();
      //TODO
    }
    
    /**
     * Sends the output-perceived sensory-input message to (1) the predictor node
     * component within the world model, to (2) the entity evaluator node component within value
     * judgement, and to (3) the sensory processing node component at the next highest level.
     */
    protected void sendPerceivedSensoryInputMsg() {
      //TODO
      Object obj = null;
      Object data = null;
      
      PerceivedSensoryInputMsg perceivedSensoryInputMsg = new PerceivedSensoryInputMsg();
      perceivedSensoryInputMsg.setSender(nodeComponent);
      perceivedSensoryInputMsg.setObj(obj);
      perceivedSensoryInputMsg.setData(data);
      sendMsgToRecipient(nextHigherLevelSensoryPerceptionChannel, perceivedSensoryInputMsg);
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
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
   * the parent sensory perception node component
   */
  protected SensoryPerception parentSensoryPerception;
  
  /**
   * the children sensory perception node compontents
   */
  protected ArrayList childrenSensoryPerception;
  
  /**
   * the takable channel from which messages are input
   */
  protected Takable sensoryPerceptionChannel;
    
  /**
   * the thread which processes the input channel of messages
   */
  protected Consumer consumer;
  
  /**
   * the executor of the observed input consumer thread
   */
  protected Executor executor;
  
  //// Main
  
}