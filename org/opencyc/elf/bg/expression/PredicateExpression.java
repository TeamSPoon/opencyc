package org.opencyc.elf.bg.expression;

//// Internal Imports

//// External Imports
import java.util.ArrayList;

/**
 * <P>
 * PredicateExpression defines the behavior for objects that evaluate
 * arguments and return a boolean result.  The arguments can be objects or
 * state variables.
 * </p>
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
public abstract class PredicateExpression {
  
  //// Constructors

  /**
   * Constructs a new PredicateExpression object.
   */
  public PredicateExpression() {
    predicateExpression = this;
  }
  
  //// Public Area
  
  /**
   * Evaluates the given arguments and returns the result.  The semantics
   * of the predicate are defined by each implementing class.
   *
   * @param arguments the given arguments to evaluate
   */
  public static boolean eval (ArrayList arguments) {
    return predicateExpression.evalInternal(arguments);
  }
  
  /**
   * Evaluates the given arguments and returns the result.  The semantics
   * of the predicate are defined by each implementing class.
   *
   * @param arguments the given arguments to evaluate
   */
  protected abstract boolean evalInternal (ArrayList arguments);
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep

  /**
   * the singleton instance of predicate expression
   */
  protected static PredicateExpression predicateExpression;
  
  //// Main
}
