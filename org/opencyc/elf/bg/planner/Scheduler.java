package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.Status;

import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports

/**
 * <P>
 * Scheduler performs temporal task decomposition for a given assigned agent
 * and its allocated resources.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
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
public class Scheduler extends NodeComponent {
  //// Constructors

  /**
   * Creates a new instance of Scheduler
   */
  public Scheduler() {
  }

  //// Public Area

  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Scheduler)) {
      return false;
    }

    //TODO
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    //TODO
    return "";
  }

  //// Protected Area
  
  /**
   * Receives schedule job message from the job assigner
   */
  protected void receiveScheduleJob () {
    //TODO
    // receive via channel from the job assigner
    // TaskCommand taskCommand
  }
  
  /**
   * Receives the plan selector status message from ?
   *
   * @param taskCommand the task command
   * @param schedule the schedule
   * @param the plan selector status
   */
  protected void receivePlanSelectorStatus () {
    //TODO
    // receive via channel from ?
    // TaskCommand taskCommand
    // Schedule schedule
    // Status status
  }
  
  /**
   * Receives the executor status message from the executor associated with
   * this scheduler.
   */
  protected void receiveExecutorStatus () {
    //TODO
    // receive via channel from he executor associated with this scheduler
    // TaskCommand taskCommand
    // Schedule schedule
    // Status status
  }
  
  /**
   * Receives the value judgement status from behavior generation which forwarded it
   * from value judgement.
   */
  protected void receiveValueJudgementStatus () {
    //TODO
    // receive via channel from ?
    // TaskCommand taskCommand
    // Schedule schedule
    // Status status
  }

  /**
   * Receives simulation failure notification message forwarded from behavior generation on
   * behalf of world model.
   */
  protected void receiveSimulationFailureNotification () {
    // TODO
    // receive via channel from behavior generation
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Receives the check schedule consistent message from ?.
   */
  protected void receiveCheckScheduleConsistent () {
    // TODO
    // receive via channel from ?
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }

  /**
   * Receives the schedule consistency evaluation message from ?.
   */
  protected void receiveScheduleConsistencyEvaluation () {
    // TODO
    // receive via channel from 
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the request simulate schedule message to behavior generation
   */
  protected void requestSimulateSchedule () {
    // TODO
    // send via channel to ?
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send forwardSimulateSchedule(controlledResources, taskCommand, schedule)
    // to behaviorGeneration
  }
  
  /**
   * Sends the check if schedule consistent message to ?
   */
  protected void checkIfScheduleConsistent () {
    // TODO
    // send via channel?
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the schedule consistency evaluation message to ?
   */
  protected void sendScheduleConsistencyEvaluation () {
    // TODO
    // send via channel to ?
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the scheduler status message to job assigner.
   */
  protected void schedulerStatus () {
    // TODO
    // send via channel to ?
    // ArrayList controlledResources
    // Status status
    // send schedulerStatus(controlledResources, status) to jobAssigner
  }
  
  //// Private Area
  //// Internal Rep
}