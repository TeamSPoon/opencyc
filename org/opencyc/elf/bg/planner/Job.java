package org.opencyc.elf.bg.planner;

//// Internal Imports

//// External Imports
import java.util.List;

/**
 * Contains a job to be scheduled and executed.  A job is a non-temporal 
 * (for example spatial) decomposition of the commanded task for an agent (lower-level node or
 * actuator/sensor).  When a job assigner processes its current commanded task, it creates one
 * or more job assignments, each of which is sent to a scheduler.
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
public class Job {
  
  //// Constructors
  
  /** Creates a new instance of JobAssignment for the given job.
   *
   * @param commandName the name of the command to be temporally decomposed into subtasks/subgoals and scheduled
   * @param requiredResources the resources required to schedule the subtasks/subgoals that accomplish the assigned job
   * @param directActuatorName the name of the direct actuator that achieves or accomplishes the assigned job
   * @param directSensorName the name of the direct sensor that senses the achievements or accomplishments of 
   * the assigned job
   */
  public Job(String commandName,
             List requiredResources, 
             String directActuatorName, 
             String directSensorName) {
    this.commandName = commandName;
    this.requiredResources = requiredResources;
    this.directActuatorName = directActuatorName;
    this.directSensorName = directSensorName;
  }
  
  //// Public Area
  

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[JobAssignment for ");
    stringBuffer.append(commandName);
    stringBuffer.append(" using: ");
    stringBuffer.append(requiredResources.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** the name of the command to be temporally decomposed into subtasks/subgoals and scheduled.
   *
   * @return the name of the command to be temporally decomposed into subtasks/subgoals and scheduled
   */
  public String getCommandName () {
    return commandName;
  }

  /** Gets the resources required to schedule the subtasks/subgoals that accomplish the assigned job.
   *
   * @return the resources required to schedule the subtasks/subgoals that accomplish the assigned job
   */
  public List getRequiredResources () {
    return requiredResources;
  }

  /** Gets the name of the direct actuator that achieves or accomplishes the assigned job
   *
   * @return the name of the direct actuator that achieves or accomplishes the assigned job
   */
  public String getDirectActuatorName () {
    return directActuatorName;
  }

  /** Gets the name of the direct sensor that senses the achievements or accomplishments of the assigned job.
   *
   * @return the name of the direct sensor that senses the achievements or accomplishments of the assigned job
   */
  public String getDirectSensorName () {
    return directSensorName;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of the command to be temporally decomposed into subtasks/subgoals and scheduled */
  protected String commandName;
  
  /** the resources required to schedule the subtasks/subgoals that accomplish the assigned job */
  protected List requiredResources;
  
  /** the name of the direct actuator that achieves or accomplishes the assigned job */
  protected String directActuatorName = "";

  /** the name of the direct sensor that senses the achievements or accomplishments of the assigned job */
  protected String directSensorName = "";
  
  //// Main
  
}
