package org.opencyc.elf.message;


//// Internal Imports
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.ArrayList;

/**
 * Provides the container for the simulate schedule message that is sent from
 * an executor to the plan simulator.
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
public class SimulateScheduleMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of SimulateScheduleMsg */
  public SimulateScheduleMsg() {
  }
  
  /**
   * Gets the resources controlled by this node
   *
   * @return the resources controlled by this node
   */
  public ArrayList getControlledResources () {
    return controlledResources;
  }

  /**
   * Sets the resources controlled by this node
   *
   * @param controlledResources the resources controlled by this node
   */
  public void setControlledResources (ArrayList controlledResources) {
    this.controlledResources = controlledResources;
  }

  /**
   * Gets the node's commanded task
   *
   * @return the node's commanded task
   */
  public TaskCommand getTaskCommand () {
    return taskCommand;
  }

  /**
   * Sets the node's commanded task
   *
   * @param taskCommandthe node's commanded task
   */
  public void setTaskCommand (TaskCommand taskCommand) {
    this.taskCommand = taskCommand;
  }

  /**
   * Gets a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   *
   * @return a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  public Schedule getSchedule () {
    return schedule;
  }

  /**
   * Sets a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   *
   * @param schedule a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  public void setSchedule (Schedule schedule) {
    this.schedule = schedule;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the resources controlled by this node
   */
  protected ArrayList controlledResources;
  
  /**
   * the node's commanded task
   */
  protected TaskCommand taskCommand;
  
  /**
   * a proposed schedule of actions to carry out the commanded task using
   * the node's contolled resources
   */
  protected Schedule schedule;
  
  //// Main
}
