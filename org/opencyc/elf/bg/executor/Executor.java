package org.opencyc.elf.bg.executor;

import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.bg.planner.Planner;
import org.opencyc.elf.bg.procedure.Procedure;


/**
 * Provides the Executor for ELF BehaviorGeneration.<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Executor extends NodeComponent {
  /** the procedure to execute */
  protected Procedure procedureToExecute;

  /** the behavior generation instance which owns this executor */
  protected BehaviorGeneration behaviorGeneration;

  /** the planner whose plans this executor executes */
  protected Planner planner;

  /**
   * Constructs a new Executor object.
   */
  public Executor() {
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
}