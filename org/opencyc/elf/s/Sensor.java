package org.opencyc.elf.s;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.message.ObservedInputMsg;

//// External Imports

import EDU.oswego.cs.dl.util.concurrent.Puttable;

/**
 * Provides Sensors for the Elementary Loop Functioning (ELF).<br>
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
public class Sensor extends NodeComponent {
  
  //// Constructors
  
  /**
   * Constructs a new Sensor object.
   */
  public Sensor () {
  }

  /** 
   * Creates a new instance of Senso with the given
   * output message channel.
   *
   * @param sensorChannel the puttable channel to which messages are output
   */
  public Sensor (Puttable sensorChannel) {
    producer = new Producer(sensorChannel, this);
  }
  //// Public Area
  
  /**
   * Provides the method to be executed when the thread is started.
   */  
  public void run() {
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "Sensor for " + node.getName();
  }
  
  //// Protected Area
  
  /**
   * Thread which processes the output channel of messages.
   */
  protected class Producer implements Runnable {
    
    /**
     * the puttable channel to which messages are output
     */
    protected final Puttable sensorChannel;

    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
    
    /**
     * Creates a new instance of Consumer
     *
     * @param sensorChannel the puttable channel to which messages are output
     * @nodeComponent the parent node component
     */
    protected Producer (Puttable sensorChannel,
                        NodeComponent nodeComponent) { 
      this.sensorChannel = sensorChannel; 
      this.nodeComponent = nodeComponent;
    }

    /**
     * Senses the World and writes messages to the output channel.
     */
    public void run () {
      while (true) {
        senseWorld();
        sendObservedInputMsg();
      }
    }

    /**
     * Senses the world.
     */
    protected void senseWorld () {
    }
    
    /**
     * Sends the sensed object message.
     */
    protected void sendObservedInputMsg () {
      ObservedInputMsg observedInputMsg = new ObservedInputMsg();
      observedInputMsg.setSender(nodeComponent);
      observedInputMsg.setObj(obj);
      observedInputMsg.setData(data);
      sendMsgToRecipient(sensorChannel, observedInputMsg);
    }
      
    /**
     * the object for which data is sensed
     */  
    protected Object obj;

    /**
     * the sensed data associated with the object
     */  
    protected Object data;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the thread which processes the input channel of messages
   */
  Producer producer;
  
  //// Main
  
}