package org.opencyc.elf.a;

import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.procedure.Procedure;


/**
 * Provides Actuators for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Actuator extends NodeComponent {
  /** the commanded action */
  protected Procedure commandedAction;

  /**
   * Constructs a new Actuator object.
   */
  public Actuator() {
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "Actuator for " + node.getName();
  }

  /**
   * Gets the commanded action
   * 
   * @return the commanded action
   */
  public Procedure getCommandedAction() {
    return commandedAction;
  }

  /**
   * Sets the commanded action
   * 
   * @param commandedAction the commanded action
   */
  public void setCommandedAction(Procedure commandedAction) {
    this.commandedAction = commandedAction;
  }
}