package org.opencyc.elf.a;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.ActuateMsg;

//// External Imports
import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * Provides Actuators for the Elementary Loop Functioning (ELF).
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
public class Actuator extends NodeComponent {
  
  //// Constructors

  /**
   * Constructs a new Actuator object.
   */
  public Actuator() {
  }

  /** 
   * Creates a new instance of Actuator with the given
   * input message channel.
   *
   * @param actuatorChannel the takable channel from which messages are input
   */
  public Actuator(Takable actuatorChannel) {
    this.actuatorChannel = actuatorChannel;
    consumer = new Consumer(actuatorChannel, this);
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
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "Actuator for " + node.getName();
  }
  
  /** 
   * Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   *
   */
  public Puttable getChannel() {
    return (Puttable) actuatorChannel;
  }
  
  //// Protected Area
  
  
  //TODO think about moving execution logic here via an action look-up table
  // Action will contain an actionName and the parameter list
  
  
  /**
   * Thread which processes the input channel of messages.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable actuatorChannel;

    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param actuatorChannel the takable channel from which messages are input
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable actuatorChannel, 
                        NodeComponent nodeComponent) { 
      this.actuatorChannel = actuatorChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      try {
        while (true) { 
          doAction((ActuateMsg) actuatorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }

    /**
     * Performs the action on the given object using the command and parameters
     * given by the actuation data.
     *
     * @param actuateMsg the given input channel message
     */
    void doAction (ActuateMsg actuateMsg) {
      Object obj = actuateMsg.getObj();
      Object data = actuateMsg.getData();
      //TODO
    }
  
  }
  //// Private Area
  
  //// Internal Rep

  /**
   * the takable channel from which messages are input
   */
  protected Takable actuatorChannel = null;
    
  /**
   * the thread which processes the input channel of messages
   */
  protected Consumer consumer;

  /**
   * the executor of the consumer thread
   */
  protected Executor executor;
  
}