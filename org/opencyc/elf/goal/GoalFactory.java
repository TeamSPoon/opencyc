package org.opencyc.elf.goal;

//// Internal Imports
import org.opencyc.elf.bg.predicate.NotNull;

import org.opencyc.elf.bg.state.StateVariable;

//// External Imports
import java.util.ArrayList;

/**
 * <P>GoalFactory is designed to create goals.  There is a singleton instance.
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
public class GoalFactory {
  
  //// Constructors
  
  /** Creates a new instance of GoalFactory. */
  public GoalFactory() {
    goalFactory = this;
  }
  
  //// Public Area
  
  /**
   * Gets the singleton goal factory instance.
   *
   * @return the singleton goal factory instance
   */
  public static GoalFactory getInstance () {
    return goalFactory;
  }
  
  /**
   * Makes a new console prompted input goal that achieves a string return value.
   */
  public Goal makeConsolePromptedInput () {
    Goal goal = new Goal();
    goal.setPredicateExpression(new PredicateExpression(NotNull.getInstance(),
                                                        StateVariable.CONSOLE_PROMPT));
    goal.setImportance(new Importance(Importance.NEUTRAL));
    return goal;
  }  
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the singleton goal factory instance
   */
  protected static GoalFactory goalFactory;
  
  //// Main
  
}
