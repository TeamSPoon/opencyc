/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;

//// External Imports
import java.util.*;
import java.io.*;

/**
 * <P>DefaultInferenceParameterDescriptions is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 9, 2005, 9:30 PM
 * @version $Id$
 */
public class DefaultInferenceParameterDescriptions extends HashMap implements InferenceParameterDescriptions {
  
  //// Constructors
  
  /** Creates a new instance of DefaultInferenceParameterDescriptions. */
  private DefaultInferenceParameterDescriptions(CycAccess cycAccess, long timeoutMsecs) throws IOException, TimeOutException, CycApiException {
    this.cycAccess = cycAccess;
    init(cycAccess, timeoutMsecs);
  }
  
  //// Public Area
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  /**
   *
   * @param key
   * @param value
   * @return
   */  
  public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
  }
  
  /**
   *
   * @param t
   */  
  public void putAll(java.util.Map t) {
    throw new UnsupportedOperationException();
  }
  
  /**
   *
   * @param key
   * @return
   */  
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  /**
   *
   * @return
   */  
  public CycAccess getCycAccess() {
    return cycAccess;
  }
  
  /**
   *
   * @return
   */  
  public String stringApiValue() {
    return null;
  }
  
  /**
   *
   * @param cycAccess
   * @return
   */  
  public synchronized static InferenceParameterDescriptions getDefaultInferenceParameterDescriptions(CycAccess cycAccess) {
    InferenceParameterDescriptions inferenceParameterDescriptions = (InferenceParameterDescriptions)defaultInferenceParameterDescriptions.get(cycAccess);
    return inferenceParameterDescriptions;
  }
  
  /**
   *
   * @param cycAccess
   * @param timeoutMsecs
   * @throws IOException
   * @throws TimeOutException
   * @throws CycApiException
   * @return
   */  
 public synchronized static InferenceParameterDescriptions loadInferenceParameterDescriptions(CycAccess cycAccess, long timeoutMsecs)
 throws IOException, TimeOutException, CycApiException {
   InferenceParameterDescriptions inferenceParameterDescriptions = (InferenceParameterDescriptions) defaultInferenceParameterDescriptions.get(cycAccess);
   if (inferenceParameterDescriptions != null) {
      return inferenceParameterDescriptions;
   }    
   inferenceParameterDescriptions = new DefaultInferenceParameterDescriptions(cycAccess, timeoutMsecs);
   defaultInferenceParameterDescriptions.put(cycAccess, inferenceParameterDescriptions);
   return inferenceParameterDescriptions;
 }
 
 public InferenceParameters getDefaultInferenceParameters() {
   DefaultInferenceParameters parameters = new DefaultInferenceParameters(cycAccess);
   java.util.Iterator iterator = keySet().iterator();
   while (iterator.hasNext()) {
     Object key = iterator.next();
     InferenceParameter parameter = (InferenceParameter)(get(key));
     parameters.put(key, parameter.getDefaultValue());
   }
   return parameters;
 }
 
 public String toString() {
   String str = "DefaultInferenceParameterDescriptions {\n";
   java.util.Iterator iterator = keySet().iterator();
   while (iterator.hasNext()) {
     Object key = iterator.next();
     InferenceParameter parameter = (InferenceParameter)(get(key));
     str += ("  " + parameter + "\n");
   }
   str += "}";
   return str;
 }
  
 //// Protected Area
  
  //// Private Area
  private void init(CycAccess cycAccess, long timeoutMsecs) 
  throws IOException, TimeOutException, CycApiException {
    String command = "(get-inference-parameter-information)";
    SubLWorkerSynch worker = new DefaultSubLWorkerSynch(command, cycAccess, timeoutMsecs);
    Object work = worker.getWork();
    
    if (!(isPossiblyEmptyCycList(work))) {
      throw new CycApiException("When calling " + worker 
        + "\n got unexpected result " + work);
    }
      
    if (work instanceof CycList) {
      CycList result = (CycList)work;
      
      for (Iterator iter = result.iterator(); iter.hasNext(); ) {
        Object obj = iter.next();
        if (!(obj instanceof CycSymbol)) {
          throw new CycApiException("When calling " + worker
          + "\n got unexpected result " + obj + " expected CycSymbol");
        }
        CycSymbol inferenceParameterClass = (CycSymbol)obj;
        if (!(iter.hasNext())) {
          throw new CycApiException("When calling " + worker
          + "\n got unexpected result " + obj + " not enough items");
        }
        obj = iter.next();
        if (!(isPossiblyEmptyCycList(obj))) {
          throw new CycApiException("When calling " + worker
          + "\n got unexpected result " + obj + " expected CycList");
        }
        if (obj instanceof CycList) {
          CycList inferenceParameterDescriptionForClass = (CycList)obj;
          parseInferenceParameterDescriptionForClass(inferenceParameterClass, inferenceParameterDescriptionForClass);
        }
      }
    }
  }
  
  private boolean isPossiblyEmptyCycList(Object obj) {
    if ((obj instanceof CycList) || (obj.equals(CycObjectFactory.nil))) {
      return true;
    }
    return false;
  }
 
  private void parseInferenceParameterDescriptionForClass(CycSymbol inferenceParameterClass, CycList inferenceParameterDescriptionForClass) 
  throws CycApiException {
    if (inferenceParameterClass.equals(BOOLEAN_INFERENCE_PARAMETER_CLASS)) {
      parseBooleanInferenceParameterDescription(inferenceParameterDescriptionForClass);
    } else if (inferenceParameterClass.equals(INTEGER_INFERENCE_PARAMETER_CLASS)) {
      parseIntegerInferenceParameterDescription(inferenceParameterDescriptionForClass);
    } else if (inferenceParameterClass.equals(FLOATING_POINT_INFERENCE_PARAMETER_CLASS)) {
      parseFloatingPointInferenceParameterDescription(inferenceParameterDescriptionForClass);
    } else if (inferenceParameterClass.equals(ENUMERATION_INFERENCE_PARAMETER_CLASS)) {
      parseEnumerationInferenceParameterDescription(inferenceParameterDescriptionForClass);
    } else {
      throw new CycApiException("Got unexpected inference parameter class " + inferenceParameterClass);
    }
  } 
  
  private void parseBooleanInferenceParameterDescription(CycList inferenceParameterDescriptionForClass)
  throws CycApiException {
    for (Iterator iter = inferenceParameterDescriptionForClass.iterator(); iter.hasNext(); ) {
      Object obj = iter.next();
      if (!(obj instanceof CycFort)) {
        throw new CycApiException("Expected a Cyc FORT; got " + obj);
      }
      CycFort id = (CycFort)obj;
      if (!iter.hasNext()) {
        throw new CycApiException("Unexpected end of parameter description");
      }
      obj = iter.next();
      if (!(obj instanceof CycList)) {
        throw new CycApiException("Expected a Cyc list; got " + obj);
      }
      CycList propertyList = (CycList)obj;
      Map propertyMap = parsePropertyList(propertyList);
      propertyMap.put(AbstractInferenceParameter.ID_SYMBOL, id);
      super.put(propertyMap.get(AbstractInferenceParameter.NAME_SYMBOL), 
        new DefaultBooleanInferenceParameter(propertyMap));
    }
  }
  
  private void parseIntegerInferenceParameterDescription(CycList inferenceParameterDescriptionForClass)
  throws CycApiException {
    for (Iterator iter = inferenceParameterDescriptionForClass.iterator(); iter.hasNext(); ) {
      Object obj = iter.next();
      if (!(obj instanceof CycFort)) {
        throw new CycApiException("Expected a Cyc FORT; got " + obj);
      }
      CycFort id = (CycFort)obj;
      if (!iter.hasNext()) {
        throw new CycApiException("Unexpected end of parameter description");
      }
      obj = iter.next();
      if (!(obj instanceof CycList)) {
        throw new CycApiException("Expected a Cyc list; got " + obj);
      }
      CycList propertyList = (CycList)obj;
      Map propertyMap = parsePropertyList(propertyList);
      propertyMap.put(AbstractInferenceParameter.ID_SYMBOL, id);
      super.put(propertyMap.get(AbstractInferenceParameter.NAME_SYMBOL), 
        new DefaultIntegerInferenceParameter(propertyMap));
    }
  }
   
  private void parseFloatingPointInferenceParameterDescription(CycList inferenceParameterDescriptionForClass) 
  throws CycApiException {
    for (Iterator iter = inferenceParameterDescriptionForClass.iterator(); iter.hasNext(); ) {
      Object obj = iter.next();
      if (!(obj instanceof CycFort)) {
        throw new CycApiException("Expected a Cyc FORT; got " + obj);
      }
      CycFort id = (CycFort)obj;
      if (!iter.hasNext()) {
        throw new CycApiException("Unexpected end of parameter description");
      }
      obj = iter.next();
      if (!(obj instanceof CycList)) {
        throw new CycApiException("Expected a Cyc list; got " + obj);
      }
      CycList propertyList = (CycList)obj;
      Map propertyMap = parsePropertyList(propertyList);
      propertyMap.put(AbstractInferenceParameter.ID_SYMBOL, id);
      super.put(propertyMap.get(AbstractInferenceParameter.NAME_SYMBOL), new DefaultFloatingPointInferenceParameter(propertyMap));
    }
  }
  
  private Object verifyParameterList (Iterator iter)
  throws CycApiException {
    Object obj = iter.next();
    if (!(obj instanceof CycFort)) {
      throw new CycApiException("Expected a Cyc FORT; got " + obj);
    }
    CycFort id = (CycFort)obj;
    if (!iter.hasNext()) {
      throw new CycApiException("Unexpected end of parameter description");
    }
    obj = iter.next();
    if (!(obj instanceof CycList)) {
      throw new CycApiException("Expected a Cyc list; got " + obj);
    }
    return obj;
  }
    
  private void parseEnumerationInferenceParameterDescription(CycList inferenceParameterDescriptionForClass) 
  throws CycApiException {
    for (Iterator iter = inferenceParameterDescriptionForClass.iterator(); iter.hasNext(); ) {
      Object obj = iter.next();
      if (!(obj instanceof CycFort)) {
        throw new CycApiException("Expected a Cyc FORT; got " + obj);
      }
      CycFort id = (CycFort)obj;
      if (!iter.hasNext()) {
        throw new CycApiException("Unexpected end of parameter description");
      }
      obj = iter.next();
      if (!(obj instanceof CycList)) {
        throw new CycApiException("Expected a Cyc list; got " + obj);
      }
      CycList propertyList = (CycList)obj;
      Map propertyMap = parsePropertyList(propertyList);
      propertyMap.put(AbstractInferenceParameter.ID_SYMBOL, id);
      super.put(propertyMap.get(AbstractInferenceParameter.NAME_SYMBOL), 
        new DefaultEnumerationInferenceParameter(propertyMap));
    }
  }  
  
  static Map parsePropertyList(CycList propertyList)
  throws CycApiException {
    if ((propertyList == null) || (propertyList.size() == 0)) { 
      return new HashMap(); 
    }
    if ((propertyList.size() % 2) != 0) {
      throw new CycApiException("Expected an even number of items; got " + propertyList.size() + 
      "\n Items: " + propertyList);
    }
    Map result = new HashMap();
    for (Iterator iter = propertyList.iterator(); iter.hasNext(); ) {
      Object key = iter.next();
      Object value = iter.next();
      if (value.equals(INTEGER_PLUS_INFINITY)) { value = MAX_LONG_VALUE; }
      else if (value.equals(REAL_PLUS_INFINITY)) { value = MAX_DOUBLE_VALUE; }
      result.put(key, value);
    }
    return result;
  }
  
  
  //// Internal Rep
  private static Map defaultInferenceParameterDescriptions = new HashMap();
  private CycAccess cycAccess;
  private final static CycSymbol BOOLEAN_INFERENCE_PARAMETER_CLASS = new CycSymbol(":BOOLEAN-INFERENCE-PARAMETERS");
  private final static CycSymbol INTEGER_INFERENCE_PARAMETER_CLASS = new CycSymbol(":INTEGER-INFERENCE-PARAMETERS");
  private final static CycSymbol FLOATING_POINT_INFERENCE_PARAMETER_CLASS = new CycSymbol(":REAL-NUMBER-INFERENCE-PARAMETERS");
  private final static CycSymbol ENUMERATION_INFERENCE_PARAMETER_CLASS = new CycSymbol(":ENUMERATION-INFERENCE-PARAMETERS");
  private final static CycSymbol INTEGER_PLUS_INFINITY = new CycSymbol(":INTEGER-PLUS-INFINITY");
  private final static CycSymbol REAL_PLUS_INFINITY = new CycSymbol(":REAL-PLUS-INFINITY");
  private final static Long MAX_LONG_VALUE = new Long(Long.MAX_VALUE);
  private final static Double MAX_DOUBLE_VALUE = new Double(Double.MAX_VALUE);
  
  
  //// Main
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      System.out.println("Starting...");
      CycAccess cycAccess = new CycAccess("localhost", 3600);
      InferenceParameterDescriptions parameters = new DefaultInferenceParameterDescriptions(cycAccess, 100000);
      System.out.println("PARAMETERS: " + parameters);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("Exiting...");
      System.exit(0);
    }
  }
}
