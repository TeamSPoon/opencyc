package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
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
public class Action implements Command {
  
  //// Constructors
  
  /** Creates a new instance of Action. 
   *
   * @param name the name of the action
   * @param inputParameters the input formal parameters for this action
   * @param outputParameters the output formal parameters for this action which are 
   * customized by the schedule factory
   */
  public Action(String name, List inputParameters, List outputParameters) {
    this.name = name;
    this.inputParameters = inputParameters;
    this.outputParameters = outputParameters;
  }
  
  //// Public Area

  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Action ");
    stringBuffer.append(name);
    if (inputParameters.size() > 0) {
      stringBuffer.append(" input (");
      for (int i = 0; i < inputParameters.size(); i++) {
        Parameter parameter = (Parameter) inputParameters.get(i);
        stringBuffer.append(" ");
        stringBuffer.append(parameter.getName());
        stringBuffer.append("(");
        stringBuffer.append(parameter.getType().toString());
        stringBuffer.append("): ");
        if (inputParameters != null) {
          Object parameterValue = inputParameters.get(i);
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
      stringBuffer.append(") ");
    }
    if (outputParameters.size() > 0) {
      stringBuffer.append(" input (");
      for (int i = 0; i < outputParameters.size(); i++) {
        Parameter parameter = (Parameter) outputParameters.get(i);
        stringBuffer.append(" ");
        stringBuffer.append(parameter.getName());
        stringBuffer.append("(");
        stringBuffer.append(outputStateVariables.get(i).toString());
        stringBuffer.append("): ");
      }
      stringBuffer.append(") ");
    }
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object. The action parameter values are not
   * cloned so that the receiver may set them as appropriate for the task.
   *
   * @return a partially instantiated copy of this object
   */
  public Object clone () {
    return new Action(name, inputParameters, outputParameters);
  }
  
  /** Gets the name of the action
   *
   * @return name the name of the action
   */
  public String getName () {
    return name;
  }

  /** Gets the input parameter values for this action which are customized by the schedule factory.
   *
   * @return tthe input parameter values for this action which are customized by the schedule factory
   */
  public List getInputParameters () {
    return inputParameters;
  }
    
  /** Gets the output parameter values for this action which are customized by the schedule factory.
   *
   * @return tthe output parameter values for this action which are customized by the schedule factory
   */
  public List getOutputParameters () {
    return outputParameters;
  }
    
  /** Sets the input parameter values for this action.
   *
   * @param inputParameterValues the input parameter values for this action
   */
  public void setInputParameterValues (List inputParameterValues) {
    if (inputParameterValues.size() != inputParameters.size())
      throw new BehaviorEngineException("Number of parameter values (" + inputParameterValues.size() +
                                        ") does not match the number of input parameters (" +
                                        inputParameters.size() + ")");
    for (int i = 0; i < inputParameterValues.size(); i++) {
      Object parameterValue = inputParameterValues.get(i);
      Parameter parameter = (Parameter) inputParameters.get(i);
      Class parameterType = parameter.getType();
      if (! (parameterType.isInstance(parameterValue))) {
        throw new BehaviorEngineException("parameter values (" + parameterValue +
                                          ") is not an instance of parameter type (" +
                                          parameterType + ")");
      }
    }
    this.inputParameterValues = inputParameterValues;
  }
       
  /** Sets the output state variables for this action.
   *
   * @param inputParameterValues the input parameter values for this action
   */
  public void setOutputStateVariables (List outputStateVariables) {
    if (outputStateVariables.size() != outputParameters.size())
      throw new BehaviorEngineException("Number of output state variables (" + outputStateVariables.size() +
                                        ") does not match the number of input parameters (" +
                                        outputParameters.size() + ")");
    for (int i = 0; i < outputStateVariables.size(); i++) {
      StateVariable stateVariable = (StateVariable) outputStateVariables.get(i);
      Parameter parameter = (Parameter) outputParameters.get(i);
      Class parameterType = parameter.getType();
      if (! (parameterType.isInstance(stateVariable.getType()))) {
        throw new BehaviorEngineException("state variable (" + stateVariable +
                                          ") is not an instance of output parameter type (" +
                                          parameterType + ")");
      }
    }
    this.outputStateVariables = outputStateVariables;
  }
     
  /** the abort action name */
  public static final String ABORT = "Abort";
  
  /** the console prompted input action name */
  public static final String CONSOLE_PROMPTED_INPUT = "ConsolePromptedInput";
  
  /** the converse with user action name */
  public static final String CONVERSE_WITH_USER = "ConverseWithUser";
  
  /** the emergency stop action name */
  public static final String EMERGENCY_STOP = "EmergencyStop";
  
  /** the init action name */
  public static final String INITIALIZE = "Initialize";
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
    
  /** the name of the action */
  protected final String name;
  
  /** the input formal parameters for this action */
  protected final List inputParameters;
  
  /** the input parameter values for this action which are customized by the schedule factory */
  protected List inputParameterValues;
  
  /** the output formal parameters for this action which are customized by the schedule factory */
  protected final List outputParameters;
  
  /** the output state variables for this action which are customized by the schedule factory */
  protected List outputStateVariables;
  
  //// Main
  
}
