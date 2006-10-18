/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.api.*;

//// External Imports
import java.util.*;

/**
 * <P>InferenceParameters is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 14, 2005, 2:41 PM
 * @version $Id$
 */
public interface InferenceParameters extends Map, Cloneable {  
  String stringApiValue();
  CycAccess getCycAccess();
  public Object clone();
}
