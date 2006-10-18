/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

//// External Imports
import java.util.*;

/**
 * <P>AbstractInferenceParameter is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 9, 2005, 8:49 PM
 * @version $Id$
 */
public abstract class AbstractInferenceParameter implements InferenceParameter {
    
    //// Constructors
    
    /** Creates a new instance of AbstractInferenceParameter. */
    public AbstractInferenceParameter(Map propertyMap) {
      if (propertyMap == null) {
        throw new RuntimeException("Got null parameter map");
      }
      if (propertyMap.size() < REQUIRED_SYMBOLS.length) {
        throw new RuntimeException("Got too few symbols in map");
      }
      for (int i = 0, size = REQUIRED_SYMBOLS.length; i < size; i++) {
        if (propertyMap.get(REQUIRED_SYMBOLS[i]) == null) {
          throw new RuntimeException("Expected key not found in map " + 
            REQUIRED_SYMBOLS[i] + 
            " for inference Parameter " + propertyMap.get(ID_SYMBOL));
        }
      } 
      Object nameObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, NAME_SYMBOL, CycSymbol.class);
      Object idObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, ID_SYMBOL, CycFort.class);
      Object shortDescObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, SHORT_DESC_SYMBOL, String.class);
      Object longDescObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, LONG_DESC_SYMBOL, String.class);
      Object queryStaticParamObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, QUERY_STATIC_PARAMETER_SYMBOL, CycSymbol.class);      
      Object basicParamObj = DefaultInferenceParameterValueDescription.
        verifyObjectType(propertyMap, BASIC_PARAMETER_SYMBOL, CycSymbol.class);      
      Object alternateValueObj = propertyMap.get(ALTERNATE_VALUE_SYMBOL);
      if (!(alternateValueObj instanceof CycList)) {
        if (alternateValueObj.equals(CycObjectFactory.nil)) {
          alternateValueObj = null;
        } else {
          throw new RuntimeException("Expected a CycList or nil; got " + alternateValueObj);
        }  
      }
      init(propertyMap.get(DEFAULT_VALUE_SYMBOL),
        (CycSymbol)nameObj, 
        (CycFort)idObj,
        (String)shortDescObj,
        (String)longDescObj,
        (CycSymbol)basicParamObj,
        (CycSymbol)queryStaticParamObj,
        (CycList)alternateValueObj);
    }
    

    //// Public Area
    public Object getDefaultValue() {
      return defaultValue;
    }
      
    public CycSymbol getKeyword() {
      return keyword;
    }    
   
    public CycFort getId() {
      return id;
    }    
    
    public String getLongDescription() {
      return longDescription;
    }
    
    public String getShortDescription() {
      return shortDescription;
    }
    
    public boolean isBasicParameter() {
      return isBasicParameter;
    }
    
    public boolean isQueryStaticParameter() {
      return isQueryStaticParameter;
    }

    public InferenceParameterValueDescription getAlternateValue() {
      return alternateValue;
    }
    
    public abstract boolean isValidValue(Object potentialValue);
    
    public boolean isAlternateValue(Object value) {
      if (alternateValue == null) {
        return false;
      }
      return alternateValue.getValue().equals(value);
    }
    
    public String toString() {
      String str = getKeyword().toString() +
          " shortDescription=\"" + getShortDescription() + "\"" +
          " type=" + getClass().getName().replaceAll("^org\\.opencyc\\.inference\\.","") +
          " isBasicParameter=" + isBasicParameter() +
          " isQueryStaticParameter=" + isQueryStaticParameter() +
          " defaultValue=" + getDefaultValue();
      if (getAlternateValue() != null) {
          str += " alternateValue=" + getAlternateValue();
      }
      return str;
    }
    
    //// Protected Area
    
    //// Private Area
    private void init (Object defaultValue, CycSymbol keyword,
        CycFort id, String shortDescription, String longDescription, 
        CycSymbol isBasicParameter, CycSymbol isQueryStaticParameter, CycList alternateValue) {
      this.defaultValue = defaultValue;
      this.keyword = keyword;
      this.id = id;
      this.longDescription = longDescription;
      this.shortDescription = shortDescription;
      if (alternateValue != null) {
        this.alternateValue = 
          new DefaultInferenceParameterValueDescription(DefaultInferenceParameterDescriptions.
          parsePropertyList(alternateValue));
      }
      
      if (CycObjectFactory.t.equals(isBasicParameter)) {
        this.isBasicParameter = true; 
      } else if (CycObjectFactory.nil.equals(isBasicParameter)) {
        this.isBasicParameter = false;
      } else {
        throw new RuntimeException("Got unexpected boolean value " + isBasicParameter);
      }

      if (CycObjectFactory.t.equals(isQueryStaticParameter)) {
        this.isQueryStaticParameter = true; 
      } else if (CycObjectFactory.nil.equals(isQueryStaticParameter)) {
        this.isQueryStaticParameter = false;
      } else {
        throw new RuntimeException("Got unexpected boolean value " + isQueryStaticParameter);
      }
    }
        
    //// Internal Rep
    private Object defaultValue;
    private CycSymbol keyword;
    private CycFort id;
    private String shortDescription;
    private String longDescription;
    private boolean isBasicParameter;
    private boolean isQueryStaticParameter;
    private InferenceParameterValueDescription alternateValue;
    
    private final static CycSymbol DEFAULT_VALUE_SYMBOL = new CycSymbol(":DEFAULT-VALUE");
    final static CycSymbol NAME_SYMBOL = new CycSymbol(":NAME");
    final static CycSymbol ID_SYMBOL = new CycSymbol(":ID");
    final static CycSymbol SHORT_DESC_SYMBOL = new CycSymbol(":SHORT-DESC");
    final static CycSymbol LONG_DESC_SYMBOL = new CycSymbol(":LONG-DESC");
    private final static CycSymbol BASIC_PARAMETER_SYMBOL = new CycSymbol(":BASIC?");
    private final static CycSymbol QUERY_STATIC_PARAMETER_SYMBOL = new CycSymbol(":QUERY-STATIC?");
    private final static CycSymbol ALTERNATE_VALUE_SYMBOL = new CycSymbol(":ALTERNATE-VALUE");
   
    private final static CycSymbol[] REQUIRED_SYMBOLS = { DEFAULT_VALUE_SYMBOL,  
      NAME_SYMBOL, ID_SYMBOL, SHORT_DESC_SYMBOL, LONG_DESC_SYMBOL, 
      BASIC_PARAMETER_SYMBOL, QUERY_STATIC_PARAMETER_SYMBOL, ALTERNATE_VALUE_SYMBOL };
      
    //// Main
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    
  
    
}
