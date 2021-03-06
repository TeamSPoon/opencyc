package org.opencyc.elf.sp;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ObservedInputMsg;
import org.opencyc.elf.message.PerceivedSensoryInputMsg;

import org.opencyc.elf.s.Sensation;
import org.opencyc.elf.s.Sensor;

//// External Imports
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** Provides Sensory Perception for the Elementary Loop Functioning (ELF).
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
public class SensoryPerception extends NodeComponent implements Sensor {
  
  //// Constructors
  
  /**
   * Creates a new instance of SensoryPerception with the given name,
   * the names of sensations that this virtual sensor can sense, and given
   * input channel from which messages are input.
   *
   * @param name the sensory perception name
   * @param sensationCapabilities the names of sensations that this virtual sensor can sense
   * @param sensoryPerceptionChannel the takable channel from which messages are input
   */
  public SensoryPerception(String name, 
                           List sensationCapabilities,
                           Takable sensoryPerceptionChannel) {
    this.name = name;
    this.sensationCapabilities = sensationCapabilities;
    this.sensoryPerceptionChannel = sensoryPerceptionChannel;
  }
  
  //// Public Area
  
  /**  Initializes with the given output message channels and starts the message 
   * consumer process.
   *
   * @param nextHigherLevelSensoryPerceptionChannel the puttable channel to which messages are output
   * entity evaluator node component in value judgement
   */
  public void initialize(Puttable nextHigherLevelSensoryPerceptionChannel) {
    getLogger().info("Initializing SensoryPerception");
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

  /** Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   *
   */
  public Puttable getChannel() {
    return (Puttable) sensoryPerceptionChannel;
  }  

  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "SensoryPerception for " + node.toString();
  }

  /** Adds the given sensor to the list of sensors reporting to this sensory
   * perception.
   *
   * @param sensor the given direct sensor or lower level sensory perception to be 
   * removed 
   */
  public void addSensor (Sensor sensor) {
    sensors.add(sensor);
  }
  
  /** Removes the given sensor from the list of sensors reporting to this sensory
   * perception.
   *
   * @param sensor the given direct sensor or lower level sensory perception to be 
   * removed 
   */
  public void removeSensor (Sensor sensor) {
    sensors.remove(sensor);
  }
  
  /** Gets the estimator node component
   *
   * @return the estimator node component
   */
  public Estimator getEstimator () {
    return estimator;
  }

  /** Sets the estimator node component
   *
   * @param estimator the estimator node component
   */
  public void setEstimator (Estimator estimator) {
    this.estimator = estimator;
  }

  /** Gets the feature extractor node component
   *
   * @return the feature extractor node component
   */
  public FeatureExtractor getFeatureExtractor () {
    return featureExtractor;
  }

  /** Sets the feature extractor node component
   *
   * @param featureExtractor the feature extractor node component
   */
  public void setFeatureExtractor (FeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }

  /** Gets the hypothesis evaluator node component
   *
   * @return the hypothesis evaluator node component
   */
  public HypothesisEvaluator getHypothesisEvaluator () {
    return hypothesisEvaluator;
  }

  /** Sets the hypothesis evaluator node component
   *
   * @param hypothesisEvaluator the hypothesis evaluator node component
   */
  public void setHypothesisEvaluator (HypothesisEvaluator hypothesisEvaluator) {
    this.hypothesisEvaluator = hypothesisEvaluator;
  }

  /** Gets the hypothesis former node component
   *
   * @return the hypothesis former node component
   */
  public HypothesisFormer getHypothesisFormer () {
    return hypothesisFormer;
  }

  /** Sets the hypothesis former node component
   *
   * @param hypothesisFormer the hypothesis former node component
   */
  public void setHypothesisFormer (HypothesisFormer hypothesisFormer) {
    this.hypothesisFormer = hypothesisFormer;
  }

  /**  Gets the name of the virtual sensor.
   *
   * @return the name of the virtual sensor
   */
  public String getName() {
    return name;
  }
  
  /** Gets the resources required by this virtual sensor.
   *
   * @return the resources required by this virtual sensor
   */
  public List getResources() {
    List resources = new ArrayList();
    //TODO
    return resources;
  }
  
  /** Gets the names of sensations that this virtual sensor can sense.
   *
   * @return the names of sensations that this virtual sensor can sense
   */
  public List getSensationCapabilities() {
    return sensationCapabilities;
  }
   
  //// Protected Area
    
  /** Thread which processes the input message channel. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable sensoryPerceptionChannel;
    
    /** the puttable channel to which sensory processing messages are output for the next
     * higher level
     */
    protected final Puttable nextHigherLevelSensoryPerceptionChannel;

    /** the sensory perception which sends messages */
    protected NodeComponent sender;
    
    /** Creates a new instance of Consumer.
     *
     * @param sensoryPerceptionChannel the takable channel from which messages are input
     * @param nextHigherLevelSensoryPerceptionChannel the puttable channel to which messages are output
     * @param sender the sensory perception which sends messages 
     */
    protected Consumer (Takable sensoryPerceptionChannel,
                        Puttable nextHigherLevelSensoryPerceptionChannel,
                        NodeComponent sender) { 
      this.sensoryPerceptionChannel = sensoryPerceptionChannel;
      this.nextHigherLevelSensoryPerceptionChannel = nextHigherLevelSensoryPerceptionChannel;
      this.sender = sender;
    }

    /** Reads messages from the input queue and processes them. */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) sensoryPerceptionChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }

    /** Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof ObservedInputMsg)
        processObservedInputMsg((ObservedInputMsg) genericMsg);
      else if (genericMsg instanceof PerceivedSensoryInputMsg)
        processPerceivedSensoryInputMsg((PerceivedSensoryInputMsg) genericMsg);
    }
      
    /** Processes the observed input message. */
    protected void processObservedInputMsg(ObservedInputMsg observedInputMsg) {
      Sensation sensation = observedInputMsg.getSensation();
      //TODO
    }
    
    /** Processes the perceived sensory input message received from a next level lower sensory
     * processing node component.
     */
    protected void processPerceivedSensoryInputMsg(PerceivedSensoryInputMsg perceivedSensoryInputMsg) {
      Object obj = perceivedSensoryInputMsg.getObj();
      Object data = perceivedSensoryInputMsg.getData();
      //TODO
    }
    
    /** Sends the output-perceived sensory-input message to (1) the predictor node
     * component within the world model, to (2) the entity evaluator node component within value
     * judgement, and to (3) the sensory processing node component at the next highest level.
     */
    protected void sendPerceivedSensoryInputMsg() {
      //TODO
      Object obj = null;
      Object data = null;
      
      PerceivedSensoryInputMsg perceivedSensoryInputMsg = 
        new PerceivedSensoryInputMsg(sender, obj, data);
      sendMsgToRecipient(nextHigherLevelSensoryPerceptionChannel, perceivedSensoryInputMsg);
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the sensory perception name */
  protected String name;
  
  /** the names of sensations that this virtual sensor can sense */
  protected List sensationCapabilities;
  
  /** the estimator node component */
  protected Estimator estimator;
  
  /** the feature extractor node component */
  protected FeatureExtractor featureExtractor;
  
  /** the hypothesis evaluator node component */
  protected HypothesisEvaluator hypothesisEvaluator;
  
  /** the hypothesis former node component */
  protected HypothesisFormer hypothesisFormer;
  
  /** the direct sensors or lower level sensory perception objects that send sensations to this
   sensory perception */
  protected List sensors = new ArrayList();
  
  /** the takable channel from which messages are input */
  protected Takable sensoryPerceptionChannel;
    
  /** the thread which processes the input channel of messages */
  protected Consumer consumer;
  
  /** the executor of the observed input consumer thread */
  protected Executor executor;
  
  //// Main
  
}