package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.bg.predicate.PredicateExpression;

//// External Imports

/** ConditionalCommand a conditional command to be accomplished if the associated predicate
 * expression evaluates true in the current state.
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
public class ConditionalCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of ConditionalAction.
   * 
   * @param name the name of this iterated command
   * @param predicateExpression the conditional predicate expression
   * @param command the commnad to be accomplished if the predicate expression evaluates true
   */
  public ConditionalCommand(String name, PredicateExpression predicateExpression, Command command) {
    this.name = name;
    this.predicateExpression = predicateExpression;
    this.command = command;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ConditionalCommand when ");
    stringBuffer.append(predicateExpression.toString());
    stringBuffer.append(" ");
    stringBuffer.append(command.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    return new ConditionalCommand(name, predicateExpression, (Command) command.clone());
  }
  
  /** Gets the conditional predicate expression
   *
   * @return the conditional predicate expression
   */
  public PredicateExpression getPredicateExpression () {
    return predicateExpression;
  }

  /** Gets the name of this conditional command
   *
   * @return the name of this conditional command
   */
  public String getName () {
    return name;
  }

  /** Gets the commnad to be accomplished if the predicate expression evaluates true
   *
   * @return the commnad to be accomplished if the predicate expression evaluates true
   */
  public Command getCommand () {
    return command;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the conditional predicate expression */
  protected PredicateExpression predicateExpression;
  
  /** the name of this conditional command */
  protected String name;
  
  /** the commnad to be accomplished if the predicate expression evaluates true */
  protected Command command;
  
  //// Main
  
}
