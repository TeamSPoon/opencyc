package org.opencyc.elf.wm;

import org.opencyc.elf.NodeComponent;


/**
 * Provides the World Model for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class WorldModel extends NodeComponent {
  /**
   * Constructs a new WorldModel object.
   */
  public WorldModel() {
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "WorldModel for " + node.getName();
  }
}