package org.opencyc.elf.s;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.message.ObservedInputMsg;

import org.opencyc.elf.wm.ResourcePool;

//// External Imports
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Puttable;

/** ConsoleInput is the ELF sensor for console input.
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
public class ConsoleInput extends DirectSensor {
  
  //// Constructors
  
  /**
   * Constructs a new ConsoleInput object given its name.
   *
   * @param name the sensor name
   * @param resources the resources required by this sensor
   * @param sensationCapabilities the names of sensations that this sensor can sense
   */
  public ConsoleInput (String name, List resources, List sensationCapabilities) {
    super(name, resources, sensationCapabilities);
  }

  //// Public Area
  
  /** Initializes this instance of ConsoleInput with the given
   * output message channel.
   *
   * @param sensoryPerceptionChannel the puttable channel to which messages are output
   */
  public void Initialize (Puttable sensoryPerceptionChannel) {
    producer = new Producer(sensoryPerceptionChannel, this);
  }
  
  //// Protected Area
  
  /** Thread which processes the output channel of messages. */
  protected class Producer implements Runnable {
    
    /** the puttable channel to which messages are output */
    protected final Puttable sensoryPerceptionChannel;

    /** the parent node component */
    protected NodeComponent nodeComponent;
    
    /** Creates a new instance of Consumer
     *
     * @param sensoryPerceptionChannel the puttable channel to which messages are output
     * @param nodeComponent the parent node component
     */
    protected Producer (Puttable sensoryPerceptionChannel,
                        NodeComponent nodeComponent) { 
      this.sensoryPerceptionChannel = sensoryPerceptionChannel; 
      this.nodeComponent = nodeComponent;
    }

    /** Senses the World and writes messages to the output channel. */
    public void run () {
      while (true) {
        senseWorld();
        sendObservedInputMsg();
      }
    }

    /** Senses the world. */
    protected void senseWorld () {
      try {
        String data = bufferedReader.readLine();
      }
      catch (IOException e) {
        logger.severe(e.getMessage());
      }

    }
    
    /** Sends the sensed object message. */
    protected void sendObservedInputMsg () {
      ObservedInputMsg observedInputMsg = 
        new ObservedInputMsg(new Sensation(Sensation.CONSOLE_INPUT, obj, data));
      observedInputMsg.setSender(nodeComponent);
      sendMsgToRecipient(sensoryPerceptionChannel, observedInputMsg);
    }
      
    /** the sensed object */
    protected Object obj = ResourcePool.getInstance().getResource(Resource.CONSOLE);
    
    /** the sensed data associated with the object */
    protected Object data;
    
    /** the console buffered input reader */
    protected BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
 
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the thread which processes the input channel of messages */
  protected Producer producer;
  
  //// Main
  
}