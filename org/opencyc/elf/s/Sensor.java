package org.opencyc.elf.s;

import org.opencyc.elf.NodeComponent;


/**
 * Provides Sensors for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Sensor extends NodeComponent {
  /**
   * Constructs a new Sensor object.
   */
  public Sensor() {
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "Sensor for " + node.getName();
  }
}