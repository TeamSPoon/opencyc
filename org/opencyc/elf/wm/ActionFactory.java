package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.state.State;

import org.opencyc.elf.bg.taskframe.Action;

//// External Imports
import java.util.ArrayList;

/**
 * ActionFactory creates named instances of Action for subsequent elaboration
 * by behavior generation sub components.
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
  
  /** Creates a new instance of ActionFactory */
  public ActionFactory() {
  }
  
  //// Public Area
  
  /**
   * Makes a new abort action having no arguments and having no return value.
   */
  public Action makeAbort () {
    Action abortAction = new Action();
    abortAction.setName(Action.ABORT);
    abortAction.setParameterNames(new ArrayList());
    abortAction.setParameterTypes(new ArrayList());
    return abortAction;
  }
  
  /**
   * Makes a new console prompted input action having a prompt and having a string
   * return value.
   */
  public Action makeConsolePromptedInput () {
    Action consolePromptedInputAction = new Action();
    consolePromptedInputAction.setName(Action.CONSOLE_PROMPTED_INPUT);
    ArrayList parameterNames = new ArrayList();
    parameterNames.add("prompt");
    consolePromptedInputAction.setParameterNames(parameterNames);
    ArrayList parameterTypes = new ArrayList();
    parameterTypes.add(String.class);
    consolePromptedInputAction.setParameterTypes(parameterTypes);
    return consolePromptedInputAction;
  }
  
  /**
   * Makes a new converse with user action having a prompt and having a string
   * return value.
   */
  public Action makeConverseWithUser () {
    Action converseWithUserAction = new Action();
    converseWithUserAction.setName(Action.CONVERSE_WITH_USER);
    ArrayList parameterNames = new ArrayList();
    parameterNames.add("prompt");
    converseWithUserAction.setParameterNames(parameterNames);
    ArrayList parameterTypes = new ArrayList();
    parameterTypes.add(String.class);
    converseWithUserAction.setParameterTypes(parameterTypes);
    return converseWithUserAction;
  }
  
  /**
   * Makes a new emergency stop action having no arguments and having no return value.
   */
  public Action makeEmergencyStop () {
    Action emergencyStopAction = new Action();
    emergencyStopAction.setName(Action.EMERGENCY_STOP);
    emergencyStopAction.setParameterNames(new ArrayList());
    emergencyStopAction.setParameterTypes(new ArrayList());
    return emergencyStopAction;
  }
  
  /**
   * Makes a new init action having no arguments and having no return value.
   */
  public Action makeInit () {
    Action initAction = new Action();
    initAction.setName(Action.INIT);
    initAction.setParameterNames(new ArrayList());
    initAction.setParameterTypes(new ArrayList());
    return initAction;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main

}
