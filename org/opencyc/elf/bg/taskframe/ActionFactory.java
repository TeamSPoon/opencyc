package org.opencyc.elf.bg.taskframe;

//// Internal Imports

//// External Imports
import java.util.ArrayList;

/**
 * <P>ActionFactory creates named instances of Action for subsequent elaboration
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
 * @date August 11, 2003, 2:57 PM
 * @version $Id$
 */
public class ActionFactory {
  
  
  //// Constructors
  
  /** Creates a new instance of ActionFactory */
  public ActionFactory() {
  }
  
  //// Public Area
  
  /**
   * Makes a new init action having no arguments and having no return value.
   */
  public Action makeInit () {
    Action initAction = new Action();
    initAction.setName("init");
    initAction.setParameterTypes(new ArrayList());
    initAction.setParameterValues(new ArrayList());
    initAction.setOutpuType(null);
    return initAction;
  }
  
  /**
   * Makes a new abort action having no arguments and having no return value.
   */
  public Action makeAbort () {
    Action abortAction = new Action();
    abortAction.setName("abort");
    abortAction.setParameterTypes(new ArrayList());
    abortAction.setParameterValues(new ArrayList());
    abortAction.setOutpuType(null);
    return abortAction;
  }
  
  /**
   * Makes a new emergency stop action having no arguments and having no return value.
   */
  public Action makeEmergencyStop () {
    Action emergencyStopAction = new Action();
    emergencyStopAction.setName("emergency stop");
    emergencyStopAction.setParameterTypes(new ArrayList());
    emergencyStopAction.setParameterValues(new ArrayList());
    emergencyStopAction.setOutpuType(null);
    return emergencyStopAction;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main

}
