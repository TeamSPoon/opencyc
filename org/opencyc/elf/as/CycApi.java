package org.opencyc.elf.as;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.message.ActuateMsg;
import org.opencyc.elf.message.ObservedInputMsg;
import org.opencyc.elf.message.ReleaseMsg;

import org.opencyc.elf.s.Sensation;

import org.opencyc.elf.wm.ResourcePool;

//// External Imports
import java.util.List;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/** CycApi provides an actuator-sensor that interacts with the Cyc API.  An API request action results in
 * an API response sensation.
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
public class CycApi extends DirectActuatorSensor {
  
  //// Constructors
  
  /** Creates a new instance of CycApi given its name and
   * required resources.
   *
   * @param name tthe name of the actuator-sensor
   * @param resources the resources requried by this actuator-sensor
   * @param actuatorSensorChannel the takable channel from which messages are input
   */
  public CycApi(String name, 
                List resources,
                Takable actuatorSensorChannel) {
    super(name, resources, actuatorSensorChannel);
  }
  
  //// Public Area
  
  //// Protected Area
  
  /** Thread which processes the input channel of messages. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable actuatorSensorChannel;

    /** the puttable channel to which messages are output */
    protected final Puttable sensoryPerceptionChannel;

    /** the parent node component */
    protected NodeComponent sender;
    
    /** Creates a new instance of Consumer.
     *
     * @param actuatorSensorChannel the takable channel from which messages are input
     * @param sensoryPerceptionChannel the puttable channel to which messages are output
     * @param sender the parent node component
     */
    protected Consumer (Takable actuatorSensorChannel,
                        Puttable sensoryPerceptionChannel,
                        NodeComponent sender) { 
      this.actuatorSensorChannel = actuatorSensorChannel;
      this.sensoryPerceptionChannel = sensoryPerceptionChannel; 
      this.sender = sender;
    }

    /** Reads messages from the input queue and processes them. */
    public void run () {
      try {
        while (true) { 
          doAction((ActuateMsg) actuatorSensorChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }

    /** Outputs the data that is contained in the actuator message to the connected
     * Cyc server as an API function request.
     *
     * @param actuateMsg the given input channel message
     */
    protected void doAction (ActuateMsg actuateMsg) {
      Object obj = actuateMsg.getObj();
      Object data = actuateMsg.getData();
      System.out.println(data);
      System.out.flush();
      ObservedInputMsg observedInputMsg = 
        new ObservedInputMsg(sender, new Sensation(Sensation.CYC_API_RESPONSE, obj, data));
      sendMsgToRecipient(sensoryPerceptionChannel, observedInputMsg);
    }
  
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the thread which processes the input channel of messages */
  protected Consumer consumer;

  //// Main
  
}
