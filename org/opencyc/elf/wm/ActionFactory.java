package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.Parameter;
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** ActionFactory populates the action libary.  There is a singleton instance.
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
public class ActionFactory {
  
  //// Constructors
  
  /** Creates a new instance of ActionFactory and stores it in the singleton instance. */
  public ActionFactory() {
    actionFactory = this;
  }
  
  //// Public Area
  
  /** Gets the action factory singleton instance.
   *
   * @return the action factory singleton instance
   */
  public static ActionFactory getInstance() {
    return actionFactory;
  }
  
  /** Populates the action library. */
  public void populateActionLibrary () {
    // abort
    String name = Action.ABORT;
    List inputParameters = new ArrayList();
    List outputParameters = new ArrayList();
    Action action = new Action(name, inputParameters, outputParameters);
    ActionLibrary.getInstance().setAction(action.getName(), action);
    
    // console prompted input
    name = Action.CONSOLE_PROMPTED_INPUT;
    inputParameters = new ArrayList();
    Parameter parameter = new Parameter(Parameter.PROMPT, String.class);
    inputParameters.add(parameter);
    outputParameters = new ArrayList();
    action = new Action(name, inputParameters, outputParameters);
    ActionLibrary.getInstance().setAction(action.getName(), action);
    
    // converse with user
    name = Action.CONVERSE_WITH_USER;
    inputParameters = new ArrayList();
    outputParameters = new ArrayList();
    parameter = new Parameter(Parameter.USER_INPUT, String.class);
    outputParameters.add(parameter);
    action = new Action(name, inputParameters, outputParameters);
    ActionLibrary.getInstance().setAction(action.getName(), action);
    
    // emergency stop
    name = Action.EMERGENCY_STOP;
    inputParameters = new ArrayList();
    outputParameters = new ArrayList();
    action = new Action(name, inputParameters, outputParameters);
    ActionLibrary.getInstance().setAction(action.getName(), action);
    
    // initialize
    name = Action.INITIALIZE;
    inputParameters = new ArrayList();
    outputParameters = new ArrayList();
    action = new Action(name, inputParameters, outputParameters);
    ActionLibrary.getInstance().setAction(action.getName(), action);
  }
    
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the action factory singleton instance */
  protected static ActionFactory actionFactory;
  
  //// Main

}
