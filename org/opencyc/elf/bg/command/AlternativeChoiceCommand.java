package org.opencyc.elf.bg.command;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** AlternativeChoiceCommand a command having a set of alternative sub-commands.  The scheduler uses 
 * value judgement to choose the best alternative based upon experience with the specified state variables. 
 * A subsequent reward command references this command by name and creates an experience with the
 * applicable reward.
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
public class AlternativeChoiceCommand extends ChoiceCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of AlternativeChoiceCommand.
   * 
   * @param name the name of this alternative choice  command
   * @param alternativeCommands the alternative commands
   * @param relevantStateVariables the list of relevant state variables
   */
  public AlternativeChoiceCommand(String name, List alternativeCommands, List relevantStateVariables) {
    super(relevantStateVariables);
    this.name = name;
    this.alternativeCommands = alternativeCommands;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[AlternativeChoiceCommand among ");
    stringBuffer.append(alternativeCommands.toString());
    stringBuffer.append(" relevant state variables: ");
    stringBuffer.append(relevantStateVariables.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    List clonedAlternativeCommands = new ArrayList(alternativeCommands.size());
    Iterator iter = alternativeCommands.iterator();
    while (iter.hasNext())
      clonedAlternativeCommands.add(((Command) iter.next()).clone());
    return new AlternativeChoiceCommand(name, clonedAlternativeCommands, relevantStateVariables);
  }
  
  /** Gets the name of this alternative choice command 
   *
   * @return the name of this alternative choice command 
   */
  public String getName () {
    return name;
  }

  /** Gets the alternative commands.
   *
   * @return the alternative commands
   */
  public List getAlternativeCommands () {
    return alternativeCommands;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this alternative choice command */
  protected String name;
  
  /** the alternative commands */
  protected List alternativeCommands;
  
  //// Main
  
}
