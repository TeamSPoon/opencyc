package org.opencyc.elf.bg;

import java.util.ArrayList;

import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.goal.Goal;


/**
 * Provides Behavior Generation for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class BehaviorGeneration extends NodeComponent {
  /** the commanded goal for generated behavior to achieve */
  protected Goal commandedGoal;

  /**
   * the parent node's BehaviorGeneration object.  The topmost
   * BehaviorGeneration object has a value null here.
   */
  protected BehaviorGeneration parentBehaviorGeneration;

  /**
   * the child nodes' BehaviorGeneration objects.  The lowest level
   * BehavoriGeneration object has a value null here.
   */
  protected ArrayList childrenBehaviorGeneration;

  /**
   * Constructs a new BehaviorGeneration object.
   */
  public BehaviorGeneration() {
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "BehaviorGeneration for " + node.getName();
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
   * Gets the parent node's BehaviorGeneration object
   * 
   * @return the parent node's BehaviorGeneration object
   */
  public BehaviorGeneration getParentBehaviorGeneration() {
    return parentBehaviorGeneration;
  }

  /**
   * Sets the parent node's BehaviorGeneration object
   * 
   * @param parentBehaviorGeneration the parent node's BehaviorGeneration
   *        object
   */
  public void setParentBehaviorGeneration(BehaviorGeneration parentBehaviorGeneration) {
    this.parentBehaviorGeneration = parentBehaviorGeneration;
  }

  /**
   * Gets the child nodes' BehaviorGeneration objects
   * 
   * @return the child nodes' BehaviorGeneration objects
   */
  public ArrayList getChildrenBehaviorGeneration() {
    return childrenBehaviorGeneration;
  }

  /**
   * Sets the child nodes' BehaviorGeneration objects
   * 
   * @param childrenBehaviorGeneration the child nodes' BehaviorGeneration
   *        objects
   */
  public void setChildrenBehaviorGeneration(ArrayList childrenBehaviorGeneration) {
    this.childrenBehaviorGeneration = childrenBehaviorGeneration;
  }
}