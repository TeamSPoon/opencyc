package org.opencyc.elf.bg.predicate;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.List;

/**
 * PredicateExpression contains a predicate and arguments that can be
 * evaluated to indicate the achivement of a goal (or alternately the
 * failure to achieve a goal).  A predicate expression can also govern
 * the selection of schedules by job assignment.
 *
 * @version $Id$
 * @author  reed
 *
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
public class PredicateExpression {
  
  //// Constructors
  
  /** Creates a new instance of PredicateExpression. */ 
 public PredicateExpression() {
  }
  
  /** 
   * Creates a new instance of a unary PredicateExpression given the
   * predicate and no arguments.
   *
   * @param predicate the unary predicate
   */
  public PredicateExpression(Predicate predicate) {
    this.predicate = predicate;
    arguments = new ArrayList();
  }
  
  /** 
   * Creates a new instance of a unary PredicateExpression given the
   * predicate and single argument.
   *
   * @param predicate the unary predicate
   * @param arg1 the single argument 
   */
  public PredicateExpression(Predicate predicate, Object arg1) {
    this.predicate = predicate;
    arguments = new ArrayList();
    arguments.add(arg1);
  }
  
  /** 
   * Creates a new instance of a binary PredicateExpression given the
   * predicate and two arguments.
   *
   * @param predicate the unary predicate
   * @param arg1 the first argument 
   * @param arg2 the second argument 
   */
  public PredicateExpression(Predicate predicate, Object arg1, Object arg2) {
    this.predicate = predicate;
    arguments = new ArrayList();
    arguments.add(arg1);
    arguments.add(arg2);
  }
  
  //// Public Area
  
  /**
   * Gets the goal predicate
   *
   * @return the goal predicate
   */
  public Predicate getPredicate () {
    return predicate;
  }

  /**
   * Sets the goal predicate
   *
   * @param predicate the goal predicate
   */
  public void setPredicate (Predicate predicate) {
    this.predicate = predicate;
  }

  /**
   * Gets the argument list
   *
   * @return the argument list
   */
  public List getArguments () {
    return arguments;
  }

  /**
   * Sets the argument list
   *
   * @param arguments the argument list
   */
  public void setArguments (List arguments) {
    this.arguments = arguments;
  }
  
 /**
   * Returns true if the given object equals this object.
   *
   * @param obj the given object
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (! (obj instanceof PredicateExpression))
      return false;
    PredicateExpression that = (PredicateExpression) obj;
    return
      this.predicate.equals(that.predicate) &&
      this.arguments.equals(that.arguments);
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return predicate.toString(arguments);
  }
 
  //// Protected Area
  
  /** the goal predicate */
  protected Predicate predicate;

  /** the argument list */
  protected List arguments;
  
  //// Private Area
  
  //// Internal Rep
  
}
