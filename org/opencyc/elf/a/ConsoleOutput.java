package org.opencyc.elf.a;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.message.ActuateMsg;

import org.opencyc.elf.wm.ResourcePool;

//// External Imports
import java.util.ArrayList;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * ConsoleOutput is the console output actuator for the Elementary Loop Functioning (ELF).
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
public class ConsoleOutput extends NodeComponent implements Actuator {
  
  //// Constructors

  /** 
   * Creates a new instance of ConsoleOutput given its name and
   * required resources.
   *
   * @param name the actuator name
   * @param resources the resources requried by this actuator
   * @param actionCapabilities the actions that this actuator can accomplish
   */
  public ConsoleOutput(String name, 
                       ArrayList resources, 
                       ArrayList actionCapabilities) {
    this.name = name;
    this.resources = resources;
    this.actionCapabilities = actionCapabilities;
  }
  
  //// Public Area
  
  /** 
   * Initializes this console output actuator with the given input message channel and
   * starts the message consumer.
   *
   * @param actuatorChannel the takable channel from which messages are input
   */
  public void initialize(Takable actuatorChannel) {
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
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ConsoleOutput resources: ");
    stringBuffer.append(resources.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
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
  
  /** 
   * Gets the name of the actuator.
   *
   * @return the name of the actuator
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the resources requried by this actuator.
   *
   * @return the resources requried by this actuator
   */
  public ArrayList getResources() {
    return resources;
  }
  
  /**
   * Gets the actions that this actuator or virtual actuator (job assigner) can accomplish.
   *
   * @return the actions that this actuator or virtual actuator (job assigner) can accomplish
   */
  public ArrayList getActionCapabilities() {
    return actionCapabilities;
  }
  
  //// Protected Area
    
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
     * Outputs the data that is contained in the actuator message to the console.
     *
     * @param actuateMsg the given input channel message
     */
    protected void doAction (ActuateMsg actuateMsg) {
      Object obj = actuateMsg.getObj();
      Object data = actuateMsg.getData();
      System.out.println(data);
      System.out.flush();
    }
  
  }
  //// Private Area
  
  //// Internal Rep

  /** the name of the actuator */
  protected String name;
  
  /** the names of actions that this actuator can accomplish */
  protected ArrayList actionCapabilities;
  
  /** the resources requried by this actuator */
  protected ArrayList resources;
  
  /** the takable channel from which messages are input */
  protected Takable actuatorChannel = null;
    
  /** the thread which processes the input channel of messages */
  protected Consumer consumer;

  /** the executor of the consumer thread */
  protected Executor executor;
  
}