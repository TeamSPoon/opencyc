package org.opencyc.elf.a;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.taskframe.Command;
import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.wm.ResourcePool;

//// External Imports
import java.util.List;
import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** ConsoleOutput is the console output actuator for the Elementary Loop Functioning (ELF).
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
public class ConsoleOutput extends DirectActuator {
  
  //// Constructors

  /** Creates a new instance of ConsoleOutput given its name and
   * required resources.
   *
   * @param name the actuator name
   * @param resources the resources requried by this actuator
   * @param actuatorChannel the takable channel from which messages are input
   */
  public ConsoleOutput(String name, 
                       List resources,
                       Takable actuatorChannel) {
    super(name, resources, actuatorChannel);
  }
  
  //// Public Area
  
  /** Initializes this console output actuator with the given input message channel and
   * starts the message consumer.
   */
  public void initialize() {
    getLogger().info("Initializing ConsoleOutput " + name);
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
  
  //// Protected Area
    
  /** Thread which processes the input channel of messages. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable actuatorChannel;

    /** the parent node component */
    protected NodeComponent nodeComponent;
    
    /** Creates a new instance of Consumer.
     *
     * @param actuatorChannel the takable channel from which messages are input
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable actuatorChannel, 
                        NodeComponent nodeComponent) { 
      this.actuatorChannel = actuatorChannel;
      this.nodeComponent = nodeComponent;
    }

    /** Reads messages from the input queue and processes them. */
    public void run () {
      getLogger().info("Waiting for commanded actions");
      try {
        while (true) { 
          doAction((DoTaskMsg) actuatorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }

    /** Outputs the data that is contained in the actuator message to the console.
     *
     * @param doTaskMsg the message from the input channel
     */
    protected void doAction (DoTaskMsg doTaskMsg) {
      nodeComponent.getLogger().info("Received " + doTaskMsg);
      TaskCommand taskCommand = doTaskMsg.getTaskCommand();
      Command command = taskCommand.getCommand();
      getLogger().info("Command: " + command.toString());
      //TODO
      //System.out.println(data);
      //System.out.flush();
    }
  
  }
  
  //// Private Area
  
  //// Internal Rep

  /** the thread which processes the input channel of messages */
  protected Consumer consumer;

  //// Main
  
}