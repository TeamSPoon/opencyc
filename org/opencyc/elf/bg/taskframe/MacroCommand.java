package org.opencyc.elf.bg.taskframe;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** MacroCommand is a type of command having a list of commands that replace the macro
 * command when sequenced by the executor.
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
public class MacroCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of MacroCommand.
   * 
   * @param name the name of this macro command
   * @param expansionCommands the expansion commands which replace this macro command during sequencing
   * by the executor
   */
  public MacroCommand(String name, List expansionCommands) {
    this.name = name;
    this.expansionCommands = expansionCommands;
  }
  
  //// Public Area
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[MacroCommand with expansion ");
    stringBuffer.append(expansionCommands.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    List clonedExpansionCommands = new ArrayList(expansionCommands.size());
    Iterator iter = expansionCommands.iterator();
    while (iter.hasNext())
      clonedExpansionCommands.add(((Command) iter.next()).clone());
    return new MacroCommand(name, clonedExpansionCommands);
  }
  
  /** Gets the name of this subset choice command
   *
   * @return the name of this subset choice command
   */
  public String getName () {
    return name;
  }

  /** Gets the expansion commands which replace this macro command during sequencing by the executor.
   *
   * @return the expansion commands which replace this macro command during sequencing by the executor
   */
  public List getExpansionCommands () {
    return expansionCommands;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this ordering choice command */
  protected String name;
  
  /** the expansion commands which replace this macro command during sequencing by the executor */
  protected List expansionCommands;
  
  //// Main
  
}
