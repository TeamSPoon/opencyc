package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.ELFException;

import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.Iterator;
import java.util.List;

/** Action describes the action to be performed and may include a set of modifiers such as
 * priorities, mode, path constraints, acceptable cost, and required conditions.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author Stephen L. Reed  
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * @version $Id$
 */
public class Action {
  
  //// Constructors
  
  /** Creates a new instance of Action. */
  public Action() {
  }
  
  //// Public Area

  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Action: ");
    stringBuffer.append(name);
    stringBuffer.append("(");
    for (int i = 0; i < getParameterNames().size(); i++) {
      String parameterName = (String) getParameterNames().get(i);
      stringBuffer.append(" ");
      stringBuffer.append(parameterName);
      stringBuffer.append(": ");
      if (getParameterValues() != null) {
        Object parameterValue = getParameterValues().get(i);
        if (parameterValue instanceof String) {
          stringBuffer.append('"');
          stringBuffer.append(parameterValue.toString());
          stringBuffer.append('"');
        }
        else
          stringBuffer.append(parameterValue.toString());
      }
      else
        stringBuffer.append("null");
    }
    stringBuffer.append(")]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object.
   *
   * @return a partially instantiated copy of this object
   */
  public Object clone () {
    Action action = new Action();
    action.setName(name);
    action.setParameterNames(parameterNames);
    action.setParameterTypes(parameterTypes);
    action.setOutputType(outputType);
    return action;
  }
  
  /** Gets the name of the action
   *
   * @return name the name of the action
   */
  public String getName () {
    return name;
  }

  /** Gets the parameter names for this action.
   *
   * @return the parameter names for this action
   */
  public List getParameterNames () {
    return parameterNames;
  }
   
  /** Gets the parameter types for this action.
   *
   * @return the parameter types for this action
   */
  public List getParameterTypes () {
    return parameterTypes;
  }
    
  /** Gets the parameter values for this action.
   *
   * @return the parameter values for this action
   */
  public List getParameterValues () {
    return parameterValues;
  }
  
  /** Sets the parameter values for this action.
   *
   * @param parameterValues the parameter values for this action
   */
  public void setParameterValues (List parameterValues) {
    if (parameterValues.size() != getParameterTypes().size())
      throw new ELFException("Number of parameter values (" + parameterValues.size() +
                             ") does not match the number of parameter types (" +
                             getParameterTypes().size() + ")");
    for (int i = 0; i < parameterValues.size(); i++) {
      Object parameterValue = parameterValues.get(i);
      Class parameterType = (Class) getParameterTypes().get(i);
      if (! (parameterType.isInstance(parameterValue))) {
        throw new ELFException("parameter values (" + parameterValue +
                               ") is not an instance of parameter type (" +
                               parameterType + ")");
      }
    }
    this.parameterValues = parameterValues;
  }
  
  /** Gets the output type for this action.
   *
   * @return the output type for this action
   */
  public Class getOutputType () {
    return outputType;
   }
  
  /** Gets the output value for this (completed) action.
   *
   * @return the parameter values for this (completed) action
   */
  public Object getOutputValue () {
    return outputValue;
  }
    
  
  //// Protected Area
  
  /** Sets the name of the action
   *
   * @param name the name of the action
   */
  public void setName (String name) {
    this.name = name;
  }  
  
  /** Sets the parameter names for this action.
   *
   * @param parameterNames the parameter names for this action
   */
  public void setParameterNames (List parameterNames) {
    this.parameterNames = parameterNames;
  }
   
  /** Sets the parameter types for this action.
   *
   * @param parameterTypes the parameter types for this action
   */
  public void setParameterTypes (List parameterTypes) {
    this.parameterTypes = parameterTypes;
  }
   
  /** Sets the output type for this action.
   *
   * @param outputType the output type for this action
   */
  public void setOutputType (Class outputType) {
    this.outputType = outputType;
  }
  /** Sets the output value for this action.
   *
   * @param outputValue the output valuefor this action
   */
  public void setOutputValue (Object outputValue) {
    this.outputValue = outputValue;
  }
   
  /** the abort action name */
  public static final String ABORT = "abort";
  
  /** the console prompted input action name */
  public static final String CONSOLE_PROMPTED_INPUT = "console prompted input";
  
  /** the converse with user action name */
  public static final String CONVERSE_WITH_USER = "converse with user";
  
  /** the emergency stop action name */
  public static final String EMERGENCY_STOP = "emergency stop";
  
  /** the init action name */
  public static final String INIT = "init";
  
  //// Private Area
  
  //// Internal Rep
    
  /** the name of the action */
  protected String name;
  
  /** the parameter names for this action */
  protected List parameterNames;
  
  /** the parameter types for this action */
  protected List parameterTypes;
  
  /** the parameter values for this action */
  protected List parameterValues;
  
  /** the output type for this action */
  protected Class outputType;
  
  /** the output value for this (completed) action */
  protected Object outputValue;
  
  //// Main
  
}
