package org.opencyc.elf.goal;

/**
 * Provides the Value container for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Value {
  /**
   * Values range from -1 for most negative, to zero for neutral, to +1 for
   * most positive.
   */
  float value;

  /**
   * Constructs a new Value object.
   */
  public Value() {
  }

  /**
   * Gets the value
   * 
   * @return the value
   */
  public float getValue() {
    return value;
  }

  /**
   * Sets the value
   * 
   * @param value the value
   * @throws IllegalArgumentException DOCUMENT ME!
   */
  public void setValue(float value) {
    if ((value < -1.0) || (value > 1.0)) {
      throw new IllegalArgumentException(value + " is not in the range [-1.0 ... +1.0]");
    }

    this.value = value;
  }
}