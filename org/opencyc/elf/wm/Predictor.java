package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.PerceivedSensoryInputMsg;
import org.opencyc.elf.message.PredictedInputMsg;
import org.opencyc.elf.message.PredictionRequestMsg;

//// External Imports

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * Provides the predictor for the ELF WorldModel.<br>
 * 
 * @version $Id: SimulatorPredictor.java,v 1.1 2002/11/17 03:08:17 stephenreed
 *          Exp $
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
public class Predictor extends NodeComponent {
  
  /** Creates a new instance of Predictor */
  public Predictor() {
  }
  
  /** 
   * Creates a new instance of Predictor with the given
   * input and output channels.
   *
   * @param predictorChannel the takable channel from which messages are input
   */
  public Predictor (Takable predictorChannel,
                    Puttable planEvaluatorChannel) {
    consumer = new Consumer(predictorChannel,
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
  
  //// Protected Area
  
  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable predictorChannel;
    
    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
          
    /**
     * Creates a new instance of Consumer.
     *
     * @param predictorChannel the takable channel from which messages are input
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable predictorChannel,
                        NodeComponent nodeComponent) { 
      this.predictorChannel = predictorChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) predictorChannel.take()); 
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
      if (genericMsg instanceof PredictionRequestMsg)
        respondToPredictionRequestMsg((PredictionRequestMsg) genericMsg);
      else if (genericMsg instanceof PerceivedSensoryInputMsg)
        processPerceivedSensoryInputMsg((PerceivedSensoryInputMsg) genericMsg);
    }
  
    /**
     * Responds to the prediction request message.
     *
     * @param predictionRequestMsg the prediction request message
     */
    protected void respondToPredictionRequestMsg(PredictionRequestMsg predictionRequestMsg) {
      Object obj = predictionRequestMsg.getObj();
      //TODO
      Object data = null;
      
      PredictedInputMsg predictedInputMsg = new PredictedInputMsg();
      predictedInputMsg.setSender(nodeComponent);
      predictedInputMsg.setInReplyToMsg(predictionRequestMsg);
      predictedInputMsg.setObj(obj);
      predictedInputMsg.setData(data);
      sendMsgToRecipient(predictedInputMsg.getReplyToChannel(), predictedInputMsg);
    }
    
    /**
     * Processes the perceived sensory input message.
     *
     * @param perceivedSensoryInputMsg the perceived sensory input message
     */
    protected void processPerceivedSensoryInputMsg(PerceivedSensoryInputMsg perceivedSensoryInputMsg) {
      Object obj = perceivedSensoryInputMsg.getObj();
      Object data = perceivedSensoryInputMsg.getData();
      //TODO
    }
    
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the thread which processes the input channel of messages
   */
  Consumer consumer;
  
  /**
   * the executor of the consumer thread
   */
  Executor executor;
  
  //// Main

}
