package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.bg.executor.Executor;
import org.opencyc.elf.bg.procedure.Procedure;
import org.opencyc.elf.goal.Goal;


//// External Imports

/**
 * Provides the Planner for ELF BehaviorGeneration.<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public abstract class Planner extends NodeComponent {
  //// Constructors

  /**
   * Constructs a new Planner object.
   */
  public Planner() {
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
    if (!(obj instanceof Planner)) {
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

  /**
   * Gets the goal for generated behavior to achieve
   * 
   * @return the goal for generated behavior to achieve
   */
  public Goal getCommandedGoal() {
    return commandedGoal;
  }

  /**
   * Sets the goal for generated behavior to achieve
   * 
   * @param commandedGoal the goal for generated behavior to achieve
   */
  public void setCommandedGoal(Goal commandedGoal) {
    this.commandedGoal = commandedGoal;
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
   * Gets the executor for this planner
   * 
   * @return the executor for this planner
   */
  public Executor getExecutor() {
    return executor;
  }

  /**
   * Sets the executor for this planner
   * 
   * @param executor the executor for this planner
   */
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  /**
   * Gets the generated plan to execute
   * 
   * @return the generated plan to execute
   */
  public Procedure getProcedure() {
    return procedureToExecute;
  }

  /**
   * Sets the generated plan to execute
   * 
   * @param procedureToExecute the generated plan to execute
   */
  public void setProcedure(Procedure procedureToExecute) {
    this.procedureToExecute = procedureToExecute;
  }

  //// Protected Area

  /** the commanded goal for generated behavior to achieve */
  protected Goal commandedGoal;

  /** the behavior generation instance owning this planner */
  protected BehaviorGeneration behaviorGeneration;

  /** the executor for this planner */
  protected Executor executor;

  /** the generated plan to execute */
  protected Procedure procedureToExecute;

  //// Private Area
  //// Internal Rep
}