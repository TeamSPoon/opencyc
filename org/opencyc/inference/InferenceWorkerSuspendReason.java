/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.cycobject.CycSymbol;

//// External Imports

/**
 * <P>InferenceWorkerSuspendReason is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author bklimt
 * @date October 31, 2005, 10:29 AM
 * @version $Id$
 */
public class InferenceWorkerSuspendReason {
  
  //// Constructors
  
  /** Creates a new instance of InferenceWorkerSuspendReason. */
  protected InferenceWorkerSuspendReason(String description) {
    this.description = description;
  }
  
  //// Public Area
  
  public static InferenceWorkerSuspendReason EXHAUST =
    new InferenceWorkerSuspendReason("Exhausted");
  
  public static InferenceWorkerSuspendReason MAX_TIME =
    new InferenceWorkerSuspendReason("Max time reached");
  
  public static InferenceWorkerSuspendReason MAX_NUMBER =
    new InferenceWorkerSuspendReason("Max results reached");
  
  public static InferenceWorkerSuspendReason ABORTED =
    new InferenceWorkerSuspendReason("Aborted");
  
  public static InferenceWorkerSuspendReason INTERRUPT =
    new InferenceWorkerSuspendReason("Interrupted");
  
  private static final CycSymbol EXHAUST_SYMBOL = new CycSymbol(":EXHAUST");
  private static final CycSymbol EXHAUST_TOTAL_SYMBOL = new CycSymbol(":EXHAUST-TOTAL");
  private static final CycSymbol MAX_TIME_SYMBOL = new CycSymbol(":MAX-TIME");
  private static final CycSymbol MAX_NUMBER_SYMBOL = new CycSymbol(":MAX-NUMBER");
  private static final CycSymbol INTERRUPT_SYMBOL = new CycSymbol(":INTERRUPT");
  private static final CycSymbol ABORT_SYMBOL = new CycSymbol(":ABORT");
  private static final CycSymbol NIL_SYMBOL = new CycSymbol("NIL");
  
  public static InferenceWorkerSuspendReason createFromCycSymbol(CycSymbol symbol) {
    if (symbol.equals(EXHAUST_SYMBOL) || symbol.equals(EXHAUST_TOTAL_SYMBOL)) {
      return EXHAUST;
    } else if (symbol.equals(MAX_TIME_SYMBOL)) {
      return MAX_TIME;
    } else if (symbol.equals(MAX_NUMBER_SYMBOL)) {
      return MAX_NUMBER;
    } else if (symbol.equals(INTERRUPT_SYMBOL)) {
      return INTERRUPT;
    } else if (symbol.equals(ABORT_SYMBOL) || symbol.equals(NIL_SYMBOL) || symbol == null) {
      return ABORTED;
    } else {
      throw new IllegalArgumentException("Unable to create InferenceWorkerSuspendReason from "+symbol);
    }
  }
  
  public String toString() {
    return description;
  }
  
  //// Protected Area
  
  //// Private Area
  private String description;
  
  //// Internal Rep
  
  //// Main
  
}
