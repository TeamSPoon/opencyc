package org.opencyc.elf.wm;

//// Internal Imports

//// External Imports

/**
 * TaskFrameFactory populates the task frame library.  There is a singleton instance.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 * @date September 5, 2003, 3:31 PM
 * @version $Id$
 */
public class TaskFrameFactory {
  
  //// Constructors
  
  /** Creates a new instance of TaskFrameFactory. */
  public TaskFrameFactory() {
    taskFrameFactory = this; 
  }
  
  //// Public Area
  
  /**
   * Gets the task frame factory singleton instance.
   *
   * @return the task frame factory singleton instance
   */
  public TaskFrameFactory getInstance () {
    return taskFrameFactory;
  }
  
  /**
   * Poplulates the task frame library.
   */
  public void populateTaskFrameLibrary () {
    //TODO
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the task frame factory singleton instance */
  protected static TaskFrameFactory taskFrameFactory;
  
  //// Main
  
}
