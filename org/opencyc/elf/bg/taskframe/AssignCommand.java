package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports

/** AssignCommand is a command that assigns the evaluated second argument to its
 * first argument which is a state variable.
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
public class AssignCommand implements Command {
  
  /** Creates a new instance of AssignCommand.
   *
   * @param name the name of this command
   */
  public AssignCommand(String name) {
    this.name = name;
  }
  
  /** Returns the name of this assign command.
   *
   * @return the name of this assign command
   */
  public String getName() {
    return name;
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    return new AssignCommand(name);
  }
  
  /** the name of this assign command */
  protected String name;
 
  /** the state variable that receives the assigned value */
  protected StateVariable assignee;
  
  /** the literal or evaluatable expression that is assigned */
  protected Object assignedObject;
}
