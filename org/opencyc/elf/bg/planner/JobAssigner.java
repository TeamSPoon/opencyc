package org.opencyc.elf.bg.planner;

//// Internal Imports
//// External Imports

/**
 * <P>
 * JobAssigner performs the non-temporal (e.g. spatial) task decomposition
 * among the available agents and resources.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class JobAssigner {
  //// Constructors

  /**
   * Creates a new instance of JobAssigner
   */
  public JobAssigner() {
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
    if (!(obj instanceof JobAssigner)) {
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