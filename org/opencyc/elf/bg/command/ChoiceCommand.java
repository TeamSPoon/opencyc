package org.opencyc.elf.bg.command;

//// Internal Imports

//// External Imports
import java.util.List;

/** ChoiceCommand is the abstract superclass of all choice commands.  It provides
 * the list of relevant state variables.
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
public abstract class ChoiceCommand {
  
  //// Constructors
  
  /** Creates a new instance of ChoiceCommand.
   *
   * @param relevantStateVariables the list of relevant state variables
   */
  public ChoiceCommand(List relevantStateVariables) {
    this.relevantStateVariables = relevantStateVariables;
  }
  
  //// Public Area
  
  /** Gets the list of relevant state variables.
   *
   * @return the list of relevant state variables
   */
  public List getRelevantStateVariables () {
    return relevantStateVariables;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the list of relevant state variables */
  protected List relevantStateVariables;
  
  //// Main
  
}
