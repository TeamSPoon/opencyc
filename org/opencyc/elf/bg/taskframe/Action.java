package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.ELFException;

import org.opencyc.elf.bg.state.State;

import org.opencyc.elf.bg.procedure.Procedure;

//// External Imports
import java.util.ArrayList;

import java.lang.reflect.*;

/**
 * <P>Action describes the action to be performed and may include a set of modifiers such as
 * priorities, mode, path constraints, acceptable cost, and required conditions
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
 * @date August 11, 2003, 2:57 PM
 * @version $Id$
 */
public class Action {
  
  //// Constructors
  
  /** Creates a new instance of Action. */
  public Action() {
  }
  
  //// Public Area
  
  /**
   * Gets the action state, including the procedure, parameters and action modifiers
   *
   * @return the action state, including the procedure, parameters and action modifiers
   */
  public State getState () {
    return state;
  }

  /**
   * Gets the procedure to be performed for this action.
   *
   * @return he procedure to be performed for this action
   */
  public Procedure getProcedure () {
    return (Procedure) state.getStateValue(State.PROCEDURE);
  }
  
  /**
   * Sets the procedure to be performed for this action.
   *
   * @param procedure the procedure to be performed for this action
   */
  public void setProcedure (Procedure procedure) {
    state.setStateValue(State.PROCEDURE, procedure);
  }
  
  /**
   * Gets the procedure parameter names for this action.
   *
   * @return the procedure parameter names for this action
   */
  public ArrayList getParameterNames () {
    return (ArrayList) getProcedure().getParameterNames();
  }
   
  /**
   * Gets the procedure parameter types for this action.
   *
   * @return the procedure parameter types for this action
   */
  public ArrayList getParameterTypes () {
    return (ArrayList) getProcedure().getParameterTypes();
  }
    
  /**
   * Gets the procedure parameter values for this action.
   *
   * @return the procedure parameter values for this action
   */
  public ArrayList getParameterValues () {
    return (ArrayList) state.getStateValue(State.PARAMETER_VALUES);
  }
  
  /**
   * Sets the procedure parameter values for this action.
   *
   * @param parameterNames the procedure parameter values for this action
   */
  public void setParameterValues (ArrayList parameterValues) {
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
    state.setStateValue(State.PARAMETER_VALUES, parameterValues);
  }
  
  /**
   * Gets the output type for this action.
   *
   * @return the output type for this action
   */
  public Class getOutputType () {
    return getProcedure().getOutputType();
  }
  
  /**
   * Gets the procedure output value for this (completed) action.
   *
   * @return the procedure parameter values for this (completed) action
   */
  public Object getOutputValue () {
    return state.getStateValue(State.PROCEDURE_OUTPUT_VALUE);
  }
    
  /**
   * Sets the action state, including the object, method, parameters and action modifiers
   *
   * @param state the action state, including the object, method, parameters and action modifiers
   */
  public void setState (State state) {
    this.state = state;
  }
  
  //// Protected Area
  
  /**
   * the action state, including the procedure, parameters and action modifiers
   */
  protected State state;
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
