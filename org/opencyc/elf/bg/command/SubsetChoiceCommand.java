package org.opencyc.elf.bg.command;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** SubsetChoiceCommand is a type of command having a ordered list of sub-commands.  The scheduler 
 * uses value judgement to choose a subset of the subcommands based upon experience with the specified 
 * state variables.  A subsequent reward command references this command by name and creates an 
 * experience with the applicable reward.
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
public class SubsetChoiceCommand extends ChoiceCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of SubsetChoiceCommand.
   * 
   * @param name the name of this subset choice command
   * @param availableCommands the available commands from which a subset is chosen during scheduling
   * @param relevantStateVariables the list of relevant state variables
   */
  public SubsetChoiceCommand(String name, List availableCommands, List relevantStateVariables) {
    super(relevantStateVariables);
    this.name = name;
    this.availableCommands = availableCommands;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[OrderingChoiceCommand among ");
    stringBuffer.append(availableCommands.toString());
    stringBuffer.append(" relevant state variables: ");
    stringBuffer.append(relevantStateVariables.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    List clonedAvailableCommands = new ArrayList(availableCommands.size());
    Iterator iter = availableCommands.iterator();
    while (iter.hasNext())
      clonedAvailableCommands.add(((Command) iter.next()).clone());
    return new SubsetChoiceCommand(name, clonedAvailableCommands, relevantStateVariables);
  }
  
  /** Gets the name of this subset choice command
   *
   * @return the name of this subset choice command
   */
  public String getName () {
    return name;
  }

  /** Gets the available commands from which a subset is chosen during scheduling.
   *
   * @return the available commands from which a subset is chosen during scheduling
   */
  public List getavailableCommands () {
    return availableCommands;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this ordering choice command */
  protected String name;
  
  /** the available commands from which a subset is chosen during scheduling */
  protected List availableCommands;
  
  //// Main
  
}
