package org.opencyc.elf.bg.taskframe;

//// Internal Imports
//// External Imports

/**
 * <P>
 * Task is a component of work to be done, or an activity to be performed.  It
 * represents the  assignment.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class Task {
  //// Constructors

  /**
   * Creates a new instance of Task
   */
  public Task() {
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
    if (!(obj instanceof Task)) {
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

  //// Protected Area
  //// Private Area
  //// Internal Rep  
}