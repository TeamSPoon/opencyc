package org.opencyc.elf.message;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.List;

/** Contains the schedule consistency evaluation message which is sent from one
 * scheduler to the requesting peer scheduler in a node, in response to the
 * schedule consistency request message.
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
public class ScheduleConsistencyEvaluationMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of ScheduleConsistencyEvaluationMsg
   *
   * @param sender the sender of the message
   * @param schedule a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   * @param controlledResources the resources controlled by this node
   */
  public ScheduleConsistencyEvaluationMsg(NodeComponent sender,
                                          Schedule schedule, 
                                          List controlledResources) {
    this.sender = sender;
    this.schedule = schedule;
    this.controlledResources = controlledResources;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ScheduleConsistencyEvaluationMsg: ");
    stringBuffer.append(schedule.toString());
    stringBuffer.append(" controlledResources: ");
    stringBuffer.append(controlledResources.toString());
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

  /** Gets a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   *
   * @return a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  public Schedule getSchedule () {
    return schedule;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the resources controlled by this node
   */
  protected List controlledResources;
  
  /** a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  protected Schedule schedule;
  
  //// Main
  
}
