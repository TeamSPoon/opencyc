package org.opencyc.elf.goal;

//// Internal Imports
import org.opencyc.elf.BehaviorEngineException;
import org.opencyc.elf.bg.command.Command;
import org.opencyc.elf.bg.command.Parameter;
import org.opencyc.elf.bg.predicate.Predicate;
import org.opencyc.elf.bg.predicate.PredicateExpression;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Provides the Goal container for the behavior engine.
 * 
 * @version $Id$
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
 */
public class Goal implements Command {
  
  //// Constructors
  
  /** Constructs a new Goal object.
   *
   * @param name the goal name
   * @param predicateExpression the predicate expression which when true, indicates that the goal
   * is achieved
   * @param failurePredicateExpressions the list of predicate expressions which if true, indicate 
   * goal failure
   * @param importance the goal importance
   * @param goalTime the goal time constraint plus modifiers such as tolerance
   * @param inputParameters the input parameter values for this goal which are customized by the 
   * schedule factory
   */
  public Goal(String name,
              PredicateExpression predicateExpression,
              List failurePredicateExpressions,
              Importance importance,
              GoalTime goalTime,
              List inputParameters) {
    this.name = name;
    this.predicateExpression = predicateExpression;
    this.failurePredicateExpressions = failurePredicateExpressions;
    this.importance = importance;
    this.goalTime = goalTime;
    this.inputParameters = inputParameters;
  }

  //// Public Area
  
  /** Creates and returns a copy of this object. The default values for goal time
   * and goal importance can be modified by the receiver.
   */
  public Object clone() {
    return new Goal(name,
                    predicateExpression,
                    failurePredicateExpressions,
                    (Importance) importance.clone(),
                    (GoalTime) goalTime.clone(),
                    inputParameters);
  }
  
  /** Gets the goal name
   *
   * @return the goal name
   */
  public String getName () {
    return name;
  }

  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Goal: ");
    stringBuffer.append(name);
    stringBuffer.append(" ");
    stringBuffer.append(predicateExpression.toString());
    stringBuffer.append(" ");
    stringBuffer.append("(");
    for (int i = 0; i < inputParameters.size(); i++) {
      Parameter inputParameter = (Parameter) inputParameters.get(i);
      stringBuffer.append(" ");
      stringBuffer.append(inputParameter.getName());
      stringBuffer.append(": ");
      if (inputParameterValues != null) {
        Object parameterValue = inputParameterValues.get(i);
        if (parameterValue == null)
          stringBuffer.append("null");
        else if (parameterValue instanceof String) {
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
  
  /** Gets the predicate expression which when true, indicates that the goal
   * is achieved
   *
   * @return the predicate expression which when true, indicates that the goal
   * is achieved
   */
  public PredicateExpression getPredicateExpression () {
    return predicateExpression;
  }

  /** Gets the list of predicate expressions which if true, indicate goal failure
   *
   * @return the list of predicate expressions which if true, indicate goal failure
   */
  public List getfailurePredicateExpressions () {
    return failurePredicateExpressions;
  }

  /** Gets the goal importance
   * 
   * @return the goal importance
   */
  public Importance getImportance() {
    return importance;
  }

  /** Gets the goal time constraint plus modifiers such as tolerance.
   *
   * @return the goal time constraint plus modifiers such as tolerance
   */
  public GoalTime getGoalTime () {
    return goalTime;
  }

  /** Gets the input formal parameters for this goal.
   *
   * @return the input formal parameters for this goal
   */
  public List getInputParameters () {
    return inputParameters;
  }
   
  /** Gets the input parameter values for this goal which are customized by the schedule factory.
   *
   * @return the input parameter values for this goal which are customized by the schedule factory
   */
  public List getInputParameterValues () {
    return inputParameterValues;
  }
    
  /** Sets the parameter values for this goal.
   *
   * @param inputParameterValues the input parameter values for this goal
   */
  public void setParameterValues (List inputParameterValues) {
    if (inputParameterValues.size() != inputParameters.size())
      throw new BehaviorEngineException("Number of input parameter values (" + inputParameterValues.size() +
                                        ") does not match the number of input parameters (" +
                                        inputParameters.size() + ")");
    for (int i = 0; i < inputParameterValues.size(); i++) {
      Object inputParameterValue = inputParameterValues.get(i);
      Class inputParameterType = ((Parameter) inputParameters.get(i)).getType();
      if (! (inputParameterType.isInstance(inputParameterValue))) {
        throw new BehaviorEngineException("parameter value (" + inputParameterValue +
                                          ") is not an instance of parameter type (" +
                                          inputParameterType + ")");
      }
    }
    this.inputParameterValues = inputParameterValues;
  }
  
  /** goal name for console prompted input */
  public static final String PERCEIVE_SENSATION = "PerceiveSensation";   
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the goal name
   */
  protected final String name;
  
  /** the predicate expression which when true, indicates that the goal
   * is achieved
   */
  protected final PredicateExpression predicateExpression;

  /**  the list of predicate expressions which if true, indicate goal failure */
  protected final List failurePredicateExpressions;

  /** the goal importance */
  protected final Importance importance;

  /** the goal time constraint plus modifiers such as tolerance */
  protected final GoalTime goalTime;
  
  /** the input formal parameters for this goal */
  protected final List inputParameters;
  
  /** the input parameter values for this goal which are customized by the schedule factory */
  protected List inputParameterValues;
  
  //// Main
}