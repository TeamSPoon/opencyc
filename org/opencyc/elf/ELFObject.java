package org.opencyc.elf;

/**
 * Provides common behavior and attributes for all Elementary Loop Functioning
 * (ELF) objects.<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class ELFObject {
  /** the unique name of this ELF object */
  protected String name;

  /**
   * Constructs a new ELFObject object.
   */
  public ELFObject() {
  }

  /**
   * Gets the unique name of this ELF object
   * 
   * @return the unique name of this ELF object
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return name;
  }

  /**
   * Sets the unique name of this ELF object
   * 
   * @param name the unique name of this ELF object
   */
  public void setName(String name) {
    this.name = name;
  }
}