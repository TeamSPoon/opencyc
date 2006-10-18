/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

//// External Imports
import java.util.*;

/**
 * <P>DefaultInferenceParameters is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 14, 2005, 2:46 PM
 * @version $Id$
 */
public class DefaultInferenceParameters extends HashMap
implements InferenceParameters  {
  
  //// Constructors
  
  /** Creates a new instance of DefaultInferenceParameters. */
  public DefaultInferenceParameters(CycAccess cycAccess) {
    this.cycAccess = cycAccess;
  }
  
  /** Creates a new instance of DefaultInferenceParameters. */
  public DefaultInferenceParameters(CycAccess cycAccess, boolean shouldReturnAnswersInHL) {
    this.cycAccess = cycAccess;
    if (shouldReturnAnswersInHL) {
      getAnswersInHL();
    } else {
      getAnswersInEL();
    }
  }
  
  //// Public Area
  
  public CycAccess getCycAccess() {
    return cycAccess;
  }
  
  public Object put(Object key, Object value) {
    return put((CycSymbol)key, value);
  }
  
  public void getAnswersInHL() {
    put(ANSWER_LANGUAGE, HL);
  }
  
  public void getAnswersInEL() {
    put(ANSWER_LANGUAGE, EL);
  }
  
  public Object put(CycSymbol parameterName, Object value) {
    // @Hack the following  if statements are a hack and should be removed as soon as support
    // for the following inference parameters are supported... --Tony
    if (":ALLOWED-RULES".equals(parameterName.toString())) { return null; }
    if (":ALLOWED-MODULES".equals(parameterName.toString())) { return null; }
    if (":FORBIDDEN-RULES".equals(parameterName.toString())) { return null; }
    if (":NON-EXPLANATORY-SENTENCE".equals(parameterName.toString())) { return null; }
    InferenceParameterDescriptions descriptions = 
      (InferenceParameterDescriptions)DefaultInferenceParameterDescriptions.
      getDefaultInferenceParameterDescriptions(cycAccess);
    if (descriptions == null) {
      throw new RuntimeException("Cannot find inference parameter descriptions");
    }
    String valueStr = value.toString();
    InferenceParameter param =(InferenceParameter)descriptions.get(parameterName);
    if (param == null) {
      throw new RuntimeException("No parameter found by name " + parameterName);
    }
    // @Hack the following 2 if statements are kinda hacky...we probably want to define conversion
    // functions in each type of inference parameter then call something like
    // value = param.parseValue(). --Tony
    if ("PlusInfinity".equals(valueStr)) { value = INFINITY_SYMBOL; }
    if (param instanceof BooleanInferenceParameter) {
      if (!(value instanceof Boolean)) {
        if (value.toString().equals(CycObjectFactory.nil.toString())) {
          value = Boolean.FALSE;
        } else if (value.toString().equals(CycObjectFactory.t.toString())) {
          value = Boolean.TRUE;
        } else {
          throw new RuntimeException("Got invalid boolean value " + value);
        }
      }
    }
    if (!param.isValidValue(value)) {
      throw new RuntimeException("Got invalid value " + value + " for parameter " + parameterName);
    }
    return super.put(parameterName, value);
  }
  
  public String stringApiValue() {
    if (size() <= 0) {
      return CycObjectFactory.nil.stringApiValue();
    }
    StringBuffer buf = new StringBuffer("(LIST ");
    for (Iterator iter = keySet().iterator(); iter.hasNext(); ) {
      Object key = iter.next();
      buf.append(DefaultCycObject.stringApiValue(key));
      buf.append(" ");
      Object val = get(key);
      if (val instanceof Boolean) {
        if (((Boolean)val).booleanValue()) {
          buf.append(CycObjectFactory.t.stringApiValue());
        } else {
          buf.append(CycObjectFactory.nil.stringApiValue());
        }
      } else {
        buf.append(DefaultCycObject.stringApiValue(val));
      }
      if (iter.hasNext()) {
        buf.append(" ");
      }
    }
    buf.append(")");
    return buf.toString();
  }
  
  public Object clone() {
    DefaultInferenceParameters copy = new DefaultInferenceParameters(cycAccess);
    java.util.Iterator iterator = this.keySet().iterator();
    while (iterator.hasNext()) {
      Object key = iterator.next();
      Object value = this.get(key); // note: this might should be cloned
      copy.put(key, value);
    }
    return copy;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  private CycAccess cycAccess;
  
  final static CycSymbol INFINITY_SYMBOL = new CycSymbol(":POSITIVE-INFINITY");
  
  final static CycSymbol ANSWER_LANGUAGE = new CycSymbol(":ANSWER-LANGUAGE");
  
  final static CycSymbol HL = new CycSymbol(":HL");
  
  final static CycSymbol EL = new CycSymbol(":EL");
  
  //// Main
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      System.out.println("Starting...");
      CycAccess cycAccess = new CycAccess("localhost", 3600);
      InferenceParameters parameters = new DefaultInferenceParameters(cycAccess);
      parameters.put(new CycSymbol(":MAX-NUMBER"), new Integer(10));
      parameters.put(new CycSymbol(":PROBABLY-APPROXIMATELY-DONE"), new Double(0.5));
      parameters.put(new CycSymbol(":ABDUCTION-ALLOWED?"), Boolean.TRUE);
      parameters.put(new CycSymbol(":EQUALITY-REASONING-METHOD"), new CycSymbol(":CZER-EQUAL"));
      try {
        parameters.put(new CycSymbol(":MAX-NUMBER"), new CycSymbol(":BINDINGS"));
        System.out.println("Failed to catch exception.");
      } catch (Exception e) {} // ignore
      try {
        parameters.put(new CycSymbol(":PROBABLY-APPROXIMATELY-DONE"), new CycSymbol(":BINDINGS"));
        System.out.println("Failed to catch exception.");
      } catch (Exception e) {} // ignore
      try {
        parameters.put(new CycSymbol(":ABDUCTION-ALLOWED?"), new CycSymbol(":BINDINGS"));
        System.out.println("Failed to catch exception.");
      } catch (Exception e) {} // ignore
      try {
        parameters.put(new CycSymbol(":EQUALITY-REASONING-METHOD"), new Double(0.5));
        System.out.println("Failed to catch exception.");
      } catch (Exception e) {} // ignore
      System.out.println("PARAMETERS: " + parameters.stringApiValue());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("Exiting...");
      System.exit(0);
    }
  }
  
  
}
