package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Result;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.taskframe.TaskCommand;


//// External Imports
import java.util.ArrayList;

/**
 * <P>PlanSelector selects the best plan from alternatives.
 *
 * @version $Id$
 * @author Stephen L. Reed  
 * @date August 12, 2003, 4:59 PM
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
public class PlanSelector extends NodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of PlanSelector. */
  public PlanSelector() {
  }
  
  //// Public Area
  
  //// Protected Area
  
  /**
   * Receives the schedule evaluation message from behavior generation.
   */
  protected void receiveScheduleEvaluation () {
    //TODO
    // receive via channel from behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // Result result
  }
  
  /**
   * Sends the plan selector status message to the appropriate scheduler
   */
  protected void sendPlanSelectorStatus () {
    //TODO
    // send via channel to the appropriate scheduler
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // Status status
    // send receivePlanSelectorStatus(taskCommand, schedule, status)
    // to (appropriate) scheduler
  }
  
  /**
   * Sends the post schedule message to behavior generation, which in turn forwards
   * the message to the world model.
   */
  protected void postSchedule () {
    //TODO
    // send via channel to behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send forwardPostSchedule(controlledResources, taskCommand, schedule)
    // to behaviorGeneration
  }

  /**
   * Sends the update schedule message to ?
   */
  protected void sendUdpateSchedule () {
    //TODO
    // send via channel to ?
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the execute schedule message to the appropriate executor.
   */
  protected void sendExecuteSchedule () {
    //TODO
    // send via channel to to the appropriate executor
    // TaskCommand taskCommand
    // Schedule schedule
    // send receiveExecuteSchedule(taskCommand, schedule) to (appropriate) executor
  }
  
  public void run() {
  }
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
