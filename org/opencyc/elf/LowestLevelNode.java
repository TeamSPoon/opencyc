package org.opencyc.elf;

import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.s.Sensor;


/**
 * Provides the lowest level Node container for the Elementary Loop Functioning
 * (ELF).<br>
 * 
 * @version $Id: LowestLevelNode.java,v 1.4 2003/01/17 14:56:44 stephenreed Exp
 *          $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class LowestLevelNode extends Node {
  /** the Actuator for this node */
  protected Actuator actuator;

  /** the Sensor for this node */
  protected Sensor sensor;

  /**
   * Constructs a new LowestLevelNode object.
   */
  public LowestLevelNode() {
  }

  /**
   * Gets the Actuator for this node
   * 
   * @return the Actuator for this node
   */
  public Actuator getActuator() {
    return actuator;
  }

  /**
   * Sets the Actuator for this node
   * 
   * @param actuator the Actuator for this node
   */
  public void setActuator(Actuator actuator) {
    this.actuator = actuator;
  }

  /**
   * Gets the Sensor for this node
   * 
   * @return the Sensor for this node
   */
  public Sensor getSensor() {
    return sensor;
  }

  /**
   * Sets the Sensor for this node
   * 
   * @param sensor the Sensor for this node
   */
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
}