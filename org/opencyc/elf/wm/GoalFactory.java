package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.command.Parameter;
import org.opencyc.elf.bg.predicate.NotNull;
import org.opencyc.elf.bg.predicate.Predicate;
import org.opencyc.elf.bg.predicate.PredicateExpression;
import org.opencyc.elf.goal.Goal;
import org.opencyc.elf.goal.GoalTime;
import org.opencyc.elf.goal.Importance;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** GoalFactory is designed to create goals.  There is a singleton instance of goal factory.
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
public class GoalFactory {
  
  //// Constructors
  
  /** Creates a new instance of GoalFactory and stores it in the singleton instance. */
  public GoalFactory() {
    goalFactory = this;
  }
  
  //// Public Area
  
  /** Gets the singleton goal factory instance.
   *
   * @return the singleton goal factory instance
   */
  public static GoalFactory getInstance () {
    return goalFactory;
  }
  
  /** Populates the goal library. */
  public void populateGoalLibrary() {
    // perceived sensation
    String name = Goal.PERCEIVE_SENSATION;
    Parameter parameter = new Parameter(Parameter.USER_INPUT,  String.class);
    PredicateExpression predicateExpression = 
      new PredicateExpression((Predicate)NotNull.getInstance(),
                              parameter);
    List failurePredicateExpressions = new ArrayList();
    Importance importance = new Importance(Importance.NEUTRAL);
    GoalTime goalTime = new GoalTime(GoalTime.TEN_MINUTES, 0);
    List inputParameters = new ArrayList();
    inputParameters.add(parameter);
    Goal goal = new Goal(name, 
                         predicateExpression,
                         failurePredicateExpressions,
                         importance,
                         goalTime,
                         inputParameters);
    GoalLibrary.getInstance().setGoal(name,goal); 
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the singleton goal factory instance */
  protected static GoalFactory goalFactory;
  
  //// Main
  
}
