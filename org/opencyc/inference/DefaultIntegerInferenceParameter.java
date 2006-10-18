/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Import
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
public class DefaultIntegerInferenceParameter extends AbstractInferenceParameter 
implements IntegerInferenceParameter {
    
    //// Constructors
    
    /** Creates a new instance of DefaultBooleanInferenceParameter. */
    public DefaultIntegerInferenceParameter(Map propertyMap) {
      super(propertyMap);
      for (int i = 0, size = REQUIRED_SYMBOLS.length; i < size; i++) {
        if (propertyMap.get(REQUIRED_SYMBOLS[i]) == null) {
          throw new RuntimeException("Expected key not found in map " +
          REQUIRED_SYMBOLS[i] + 
        " for inference parameter " + propertyMap.get(AbstractInferenceParameter.ID_SYMBOL));
        }
      }
      Object maxValObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, MAX_VALUE_SYMBOL, Number.class);
      Object minValObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, MIN_VALUE_SYMBOL, Number.class);
      init(((Number)maxValObj).longValue(), ((Number)minValObj).longValue());
    }
    
    //// Public Area
    public boolean isValidValue(Object potentialValue) {      
      if (isAlternateValue(potentialValue)) { return true; }
      if (DefaultInferenceParameters.INFINITY_SYMBOL.equals(potentialValue)) { return true; }
      if (!(potentialValue instanceof Number)) { return false; }
      long potentialLong = ((Number)potentialValue).longValue();
      if (potentialLong > maxValue) { return false; }
      if (potentialLong < minValue) { return false; }
      return true;
    }
    
    public long getMaxValue() {
      return maxValue;
    }
    
    public long getMinValue() {
      return minValue;
    }

    public String toString() {
      return super.toString() + " min="+minValue + " max="+maxValue;
    }

    //// Protected Area
    
    //// Private Area
    private void init(long maxValue, long minValue) {
      this.maxValue = maxValue;
      this.minValue = minValue;
    }
    
    //// Internal Rep
    private long maxValue;
    private long minValue;
    private final static CycSymbol MAX_VALUE_SYMBOL = new CycSymbol(":MAX-VALUE");
    private final static CycSymbol MIN_VALUE_SYMBOL = new CycSymbol(":MIN-VALUE");
    private final static CycSymbol[] REQUIRED_SYMBOLS = {MAX_VALUE_SYMBOL,
      MIN_VALUE_SYMBOL};
    
    
    //// Main
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    

}
