package org.opencyc.elf.bg.executor;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.BehaviorGeneration;

import org.opencyc.elf.bg.planner.Planner;
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.procedure.Procedure;

import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.ArrayList;

/**
 * Provides the Executor for ELF BehaviorGeneration.<br>
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
public class Executor extends NodeComponent {
  //// Constructors
  /**
   * Constructs a new Executor object.
   */
  public Executor() {
  }

  //// Public Area

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "Executor for " + node.getName();
  }
  
  /**
   * Gets the procedure to execute
   * 
   * @return the procedure to execute
   */
  public Procedure getProcedureToExecute() {
    return procedureToExecute;
  }

  /**
   * Sets the procedure to execute
   * 
   * @param procedureToExecute the procedure to execute
   */
  public void setProcedureToExecute(Procedure procedureToExecute) {
    this.procedureToExecute = procedureToExecute;
  }

  /**
   * Gets the behavior generation instance
   * 
   * @return the behavior generation instance
   */
  public BehaviorGeneration getBehaviorGeneration() {
    return behaviorGeneration;
  }

  /**
   * Sets the behavior generation instance
   * 
   * @param behaviorGeneration the behavior generation instance
   */
  public void setBehaviorGeneration(BehaviorGeneration behaviorGeneration) {
    this.behaviorGeneration = behaviorGeneration;
  }

  /**
   * Gets the planner whose plans this executor executes
   * 
   * @return the planner whose plans this executor executes
   */
  public Planner getPlanner() {
    return planner;
  }

  /**
   * Sets the planner whose plans this executor executes
   * 
   * @param planner the planner whose plans this executor executes
   */
  public void setPlanner(Planner planner) {
    this.planner = planner;
  }

  //// Protected Area
  
  /**
   * Receives the update schedule message from ?
   */
  protected void receiveUpdateSchedule () {
    // TODO
    // receive via channel from ?
    // TaskCommnd taskCommand
    // Schedule schedule
  }

  /**
   * Receives the execute schedule message from plan selector. 
   * (scheduler should be the intermediary)
   */
  protected void receiveExecuteSchedule () {
    // TODO
    // receive via channel from ?
    // TaskCommnd taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the do subtask message to behavior generation, for subsequent forwarding to
   * the next highest level node
   */
  protected void doSubTask () {
    // TODO
    // send via channel to ?
    // ArrayList controlledResources
    // TaskCommnd taskCommand
  }
  
  /**
   * Sends the executor status to its scheduler.
   */
  protected void sendExecutorStatus () {
    // TODO
    // send via channel to ?
    // ArrayList controlledResources
    // TaskCommnd taskCommand
    // Schedule schedule
    // Status status
    // send receiveExecutorStatus(taskCommand, schedule, status) to (its) scheduler
  }
  
  public void run() {
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the procedure to execute */
  protected Procedure procedureToExecute;

  /** the behavior generation instance which owns this executor */
  protected BehaviorGeneration behaviorGeneration;

  /** the planner whose plans this executor executes */
  protected Planner planner;
}