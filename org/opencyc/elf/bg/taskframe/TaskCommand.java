package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.elf.bg.command.Command;
import org.opencyc.elf.goal.Goal;
import org.opencyc.elf.goal.GoalTime;

//// External Imports

/** TaskCommand is an instruction to perform the given command.  The next command is also
 * provided for consideration and response.
 * 
 * @version $Id$
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
 */
public class TaskCommand {
  
  //// Constructors

  /** Creates a new instance of TaskCommand given the command and next command.
   * 
   * @param command the action or goal command plus modifiers
   * @param nextCommand the planned next action or goal command plus modifiers
   */
  public TaskCommand(Command command, Command nextCommand) {
    this.command = command;
    this.nextCommand = nextCommand;
  }

  //// Public Area

  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[TaskCommand: ");
    stringBuffer.append(command.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /** Gets the action or goal command plus modifiers.
   *
   * @return the action or goal command plus modifiers
   */
  public Command getCommand () {
    return command;
  }


  /** Gets the planned next action or goal command plus modifiers.
   *
   * @return the planned next action or goal command plus modifiers
   */
  public Command getNextCommand () {
    return nextCommand;
  }


  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the action or goal command plus modifiers */
  protected Command command;
  
  /** the planned next action or goal command plus modifiers */
  protected Command nextCommand;
    
}