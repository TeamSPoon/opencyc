package org.opencyc.elf.bg.command;

//// Internal Imports
import org.opencyc.elf.bg.expression.ObjectMethodCall;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports

/** CallCommand is a command that applies the given method and evaluated arguments
 * to the given object for side effect without assignment.
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
public class CallCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of CallCommand.
   *
   * @param name the name of this command
   * @param objectMethodCall the object method call expression
   */
  public CallCommand(String name, ObjectMethodCall objectMethodCall) {
    this.name = name;
    this.objectMethodCall = objectMethodCall;
  }
  
  //// Public Area
  
  /**
   * Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(call ");
    stringBuffer.append(objectMethodCall.toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  /** Gets the name of the command
   *
   * @return name the name of the command
   *
   */
  public String getName() {
    return name;
  }
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    return new CallCommand(name, objectMethodCall);
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this command */
  protected final String name;
  
  /** the object method call expression */
  protected final ObjectMethodCall objectMethodCall;
}
