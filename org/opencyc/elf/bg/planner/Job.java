package org.opencyc.elf.bg.planner;

//// Internal Imports
//// External Imports

/**
 * <P>
 * Job is the description of one or many actions (activites) required to
 * perform the task and formulate it in terms of the resources to be used.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class Job {
  //// Constructors

  /**
   * Creates a new instance of Job
   */
  public Job() {
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
    if (!(obj instanceof Job)) {
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