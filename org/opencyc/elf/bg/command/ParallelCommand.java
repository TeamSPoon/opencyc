package org.opencyc.elf.bg.command;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ParallelCommand is a command in which its subcommands may be executed in parallel.
 *
 * @version $Id$
 * @author  reed
 *
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
public class ParallelCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of ParallelCommand.
   * 
   * @param name the name of this parallel command
   * @param parallelCommands the subcommands that can be executed in parallel 
   * by the executor
   */
  public ParallelCommand(String name, List parallelCommands) {
    this.name = name;
    this.parallelCommands = parallelCommands;
  }
  
  //// Public Area
  
  /**
   * Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[ParallelCommand with ");
    stringBuffer.append(parallelCommands.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Gets the name of the command
   *
   * @return name the name of the command
   */
  public String getName() {
    return name;
  }
  
  /** Creates and returns a copy of this object. 
   *
   * @return a copy of this object
   */
  public Object clone() {
    List clonedParallelCommands = new ArrayList(parallelCommands.size());
    Iterator iter = parallelCommands.iterator();
    while (iter.hasNext())
      clonedParallelCommands.add(((Command) iter.next()).clone());
    return new ParallelCommand(name, clonedParallelCommands);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this parallel command */
  protected String name;
  
  /** the parallel commands */
  protected List parallelCommands;
  
}
