package org.opencyc.elf.message;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.planner.Schedule;

//// External Imports
import java.util.List;

/** Contains the schedule consistency request message which is sent from one
 * scheduler to each of its peer schedulers in a node.
 *
 * @version $Id$
 * @author  reed
 *
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
public class ScheduleConsistencyRequestMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of ScheduleConsistencyRequestMsg 
   *
   * @param sender the sender of the message
   * @param controlledResources the resources controlled by this node
   * @param commandName the requesting scheduler's command name
   * @param schedule the requesting scheduler's proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  public ScheduleConsistencyRequestMsg(NodeComponent sender,
                                       List controlledResources,
                                       String commandName,
                                       Schedule schedule) {
    this.sender = sender;
    this.controlledResources = controlledResources;
    this.commandName = commandName;
    this.schedule = schedule;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ScheduleConsistencyRequestMsg: ");
    stringBuffer.append(controlledResources.toString());
    stringBuffer.append(" command: ");
    stringBuffer.append(commandName);
    stringBuffer.append(" schedule: ");
    stringBuffer.append(schedule.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the resources controlled by this node
   *
   * @return the resources controlled by this node
   */
  public List getControlledResources () {
    return controlledResources;
  }

  /** Gets the requesting scheduler's command name.
   *
   * @return the requesting scheduler's command name
   */
  public String getCommandName () {
    return commandName;
  }

  /** Gets the requesting scheduler's proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   *
   * @return the requesting scheduler's proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  public Schedule getSchedule () {
    return schedule;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the resources controlled by this node */
  protected List controlledResources;
  
  /** the requesting scheduler's command name */
  protected String commandName;
  
  /** the requesting scheduler's proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  protected Schedule schedule;
  
  //// Main
  
}
