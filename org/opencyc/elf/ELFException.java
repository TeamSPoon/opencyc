package org.opencyc.elf;

//// Internal Imports

//// External Imports

/**
 * <P>ELFException is designed to...
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 * @date August 27, 2003, 6:17 PM
 * @version $Id$
 */
public class ELFException extends RuntimeException {
  
  //// Constructors
  
  /** 
   * Constructs a new runtime exception with null as its detail message. The cause is 
   * not initialized, and may subsequently be initialized by a call to 
   * Throwable.initCause(java.lang.Throwable). 
   */
  public ELFException() {
    super();
  }
  
  /** 
   * Constructs a new runtime exception with the specified detail message. The cause is 
   * not initialized, and may subsequently be initialized by a call to 
   * Throwable.initCause(java.lang.Throwable). 
   *
   * @param message the detail message. The detail message is saved for later retrieval by 
   * the Throwable.getMessage() method
   */
  public ELFException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new runtime exception with the specified detail message and cause.  Note that 
   * the detail message associated with cause is not automatically incorporated in this runtime 
   * exception's detail message. 
   *
   * @param message the detail message (which is saved for later retrieval by the 
   * Throwable.getMessage() method)
   * @param cause the cause (which is saved for later retrieval by the 
   * Throwable.getCause() method). (A null value is permitted, and indicates that the cause is 
   * nonexistent or unknown.)
   */
  public ELFException(String message, Throwable cause) {
    super(message, cause);
  }
  
  
  //// Public Area
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
