package org.opencyc.elf.goal;

//// Internal Imports
import org.opencyc.elf.bg.predicate.Predicate;

import org.opencyc.elf.goal.PredicateExpression;

//// External Imports
import java.util.ArrayList;

/**
 * Provides the Goal container for the Elementary Loop Functioning (ELF).
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
public class Goal {
  //// Constructors
  /**
   * Constructs a new Goal object.
   */
  public Goal() {
  }

  //// Public Area
  

  /**
   * Gets the goal name
   *
   * @return the goal name
   */
  public String getGoalName () {
    return goalName;
  }

  /**
   * Sets the goal name
   *
   * @param goalName the goal name
   */
  public void setGoalName (String goalName) {
    this.goalName = goalName;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return predicateExpression.toString();
  }
  
  /**
   * Gets the predicate expression which when true, indicates that the goal
   * is achieved
   *
   * @return the predicate expression which when true, indicates that the goal
   * is achieved
   */
  public PredicateExpression getPredicateExpression () {
    return predicateExpression;
  }

  /**
   * Sets the predicate expression which when true, indicates that the goal
   * is achieved
   *
   * @param predicateExpression the predicate expression which when true, indicates that 
   * the goal is achieved
   */
  public void setPredicateExpression (PredicateExpression predicateExpression) {
    this.predicateExpression = predicateExpression;
  }

  /**
   * Gets the list of predicate expressions which if true, indicate goal failure
   *
   * @return the list of predicate expressions which if true, indicate goal failure
   */
  public ArrayList getfailurePredicateExpressions () {
    return failurePredicateExpressions;
  }

  /**
   * Sets the list of predicate expressions which if true, indicate goal failure
   *
   * @param failurePredicateExpressions the list of predicate expressions which if true, 
   * indicate goal failure
   */
  public void setfailurePredicateExpressions (ArrayList failurePredicateExpressions) {
    this.failurePredicateExpressions = failurePredicateExpressions;
  }

  /**
   * Gets the goal importance
   * 
   * @return the goal importance
   */
  public Importance getImportance() {
    return importance;
  }

  /**
   * Sets the goal importance
   * 
   * @param importance the goal importance
   */
  public void setImportance(Importance importance) {
    this.importance = importance;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the goal name
   */
  protected String goalName;
  
  /**
   * the predicate expression which when true, indicates that the goal
   * is achieved
   */
  protected PredicateExpression predicateExpression;

  /** 
   * the list of predicate expressions which if true, indicate goal failure 
   */
  protected ArrayList failurePredicateExpressions = new ArrayList();

  /** 
   * the goal importance 
   */
  protected Importance importance;

  //// Main
}