package org.opencyc.elf.wm;


//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.KBObjectPutMsg;
import org.opencyc.elf.message.KBObjectRequestMsg;
import org.opencyc.elf.message.KBObjectResponseMsg;

//// External Imports
import java.util.Hashtable;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * <P>Knowledge Base contains the known entities and their attributes.
 *
 * @version $Id$
 * @author Stephen L. Reed  
 * @date August 12, 2003, 4:59 PM
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
public class KnowledgeBase extends NodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of KnowledgeBase. */
  public KnowledgeBase() {
  }
  
  /** 
   * Creates a new instance of KnowledgeBase with the given
   * input message channel.
   *
   * @param knowledgeBaseChannel the takable channel from which messages are input
   */
  public KnowledgeBase(Takable knowledgeBaseChannel) {
    consumer = new Consumer(knowledgeBaseChannel, this);
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
   * Thread which processes the input channel of messages.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable knowledgeBaseChannel;

    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param knowledgeBaseChannel the takable channel from which messages are input
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable knowledgeBaseChannel, 
                        NodeComponent nodeComponent) { 
      this.knowledgeBaseChannel = knowledgeBaseChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          dispatchMsg((GenericMsg) knowledgeBaseChannel.take()); 
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
      if (genericMsg instanceof KBObjectRequestMsg)
        processKBObjectRequestMsg((KBObjectRequestMsg) genericMsg);
      else if (genericMsg instanceof KBObjectPutMsg)
        processKBObjectPutMsg((KBObjectPutMsg) genericMsg);
    }
  
    /**
     * Processes the knowledge base object request message.
     */
    protected void processKBObjectRequestMsg(KBObjectRequestMsg kbObjectRequestMsg) {
      KBObjectResponseMsg kbObjectResponseMsg = new KBObjectResponseMsg();
      kbObjectResponseMsg.setSender(nodeComponent);
      kbObjectResponseMsg.setInReplyToMsg(kbObjectRequestMsg);
      Object obj = kbObjectRequestMsg.getObj();
      kbObjectResponseMsg.setObj(obj);
      Object data = kbCache.get(obj);
      kbObjectResponseMsg.setData(data);
      sendMsgToRecipient(kbObjectRequestMsg.getReplyToChannel(), kbObjectResponseMsg);
    }

    /**
     * Processes the knowledge base object put message.
     */
    protected void processKBObjectPutMsg(KBObjectPutMsg kbObjectPutMsg) {
      Object obj = kbObjectPutMsg.getObj();
      Object data = kbObjectPutMsg.getData();
      kbCache.put(obj, data);
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the knowledge base cache associating obj --> data
   */
  protected Hashtable kbCache = new Hashtable();
    
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
