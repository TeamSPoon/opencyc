/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.cycobject.*;

//// External Imports
import java.util.*;

/**
 * <P>DefaultBooleanInferenceParameter is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 9, 2005, 9:09 PM
 * @version $Id$
 */
public class DefaultBooleanInferenceParameter extends AbstractInferenceParameter implements BooleanInferenceParameter {
    
    //// Constructors
    
    /** Creates a new instance of DefaultBooleanInferenceParameter. */
  public DefaultBooleanInferenceParameter(Map propertyMap) {
    super(propertyMap);
  }
  
    //// Public Area
    public boolean isValidValue(Object potentialValue) {
      if (isAlternateValue(potentialValue)) {
        return true;
      }
      if (potentialValue instanceof Boolean) { return true; }
      return false;
    }
    
    //// Protected Area
    
    //// Private Area
    
    //// Internal Rep
    
    //// Main
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    
    
    
}
