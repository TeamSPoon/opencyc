/* $Id$
 *
 * Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.elf.bg.predicate;

//// Internal Imports

//// External Imports

/**
 * <P>True is designed to...
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 * @date September 16, 2003, 3:13 PM
 * @version $Id$
 */
public class True extends Predicate {
  
  //// Constructors
  
  /** Creates a new instance of True. */
  public True() {
    super();
  }
  
  //// Public Area
    
  /**
   * Evaluates the given arguments and returns the result.  The semantics
   * of the predicate are defined by each implementing class.
   *
   * @param arguments the given arguments to evaluate
   */
  public boolean evaluate(java.util.List arguments) {
    return true;
  }
  
  /**
   * Returns a string representation of this predicate given
   * the arguments.
   *
   * @param arguments the given arguments to evaluate
   * @return a string representation of this object
   */
  public String toString(java.util.List arguments) {
    return "TRUE";
  }
  
 /**
   * Returns true if the given object equals this object.
   *
   * @param obj the given object
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    return obj instanceof True;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
