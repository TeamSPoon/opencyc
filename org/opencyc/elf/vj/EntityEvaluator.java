package org.opencyc.elf.vj;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.KBObjectPutMsg;
import org.opencyc.elf.message.PerceivedSensoryInputMsg;

//// External Imports

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * <P>EntityEvaluator is designed to evaluate perceived sensory inputs and update
 * the knowledge base with an worth judgement.
 *
 * @version $Id: SensoryPerception.java,v 1.1 2002/11/18 17:45:40 stephenreed
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
public class EntityEvaluator extends NodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of EntityEvaluator. */
  public EntityEvaluator() {
  }
  
  /** 
   * Creates a new instance of EntityEvaluator with the given
   * input and output message channels.
   *
   * @param entityEvaluatorChannel the takable channel from which messages are input
   * @param knowledgeBaseChannel the puttable channel to which messages are output
   */
  public EntityEvaluator(Takable entityEvaluatorChannel,
                         Puttable knowledgeBaseChannel) {
    consumer = new Consumer(entityEvaluatorChannel,
                            knowledgeBaseChannel,
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
    protected final Takable entityEvaluatorChannel;
    
    /**
     * the puttable channel to which messages are output for the knowledge base
     */
    protected final Puttable knowledgeBaseChannel;
    
    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param entityEvaluatorChannel the takable channel from which messages are input
     * @param knowledgeBaseChannel the puttable channel to which messages are output
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable entityEvaluatorChannel,
                        Puttable knowledgeBaseChannel,
                        NodeComponent nodeComponent) { 
      this.entityEvaluatorChannel = entityEvaluatorChannel;
      this.knowledgeBaseChannel = knowledgeBaseChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          processPerceivedSensoryInputMsg((PerceivedSensoryInputMsg) entityEvaluatorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
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
     * Sends the knowledge base object put message that updates the entity's worth.
     */
    protected void sendKBObjectPutMsg() {
      //TODO
      Object obj = null;
      Object data = null;
      
      KBObjectPutMsg kbObjectPutMsg = new KBObjectPutMsg();
      kbObjectPutMsg.setSender(nodeComponent);
      kbObjectPutMsg.setObj(obj);
      kbObjectPutMsg.setData(data);
      sendMsgToRecipient(knowledgeBaseChannel, kbObjectPutMsg);
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
