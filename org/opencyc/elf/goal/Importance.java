package org.opencyc.elf.goal;

/**
 * Provides the Importance container for the Elementary Loop Functioning
 * (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Importance {
  /**
   * Importances range from 0.0 for least important, to +1 for most important.
   */
  float importance;

  /**
   * Constructs a new Importance object.
   */
  public Importance() {
  }

  /**
   * Gets the importance
   * 
   * @return the importance
   */
  public float getImportance() {
    return importance;
  }

  /**
   * Sets the importance
   * 
   * @param importance the importance
   * @throws IllegalArgumentException DOCUMENT ME!
   */
  public void setImportance(float importance) {
    if ((importance < 0.0) || (importance > 1.0)) {
      throw new IllegalArgumentException(importance + " is not in the range [0.0 ... +1.0]");
    }

    this.importance = importance;
  }
}