package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;

//// External Imports
import java.util.HashMap;

/**
 * <P>ActionLibrary provides a store in which partially instantiated actions can be retrieved
 * by name.  It is initially populated by the action factory.  There is a singleton instance
 * of action libary.
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
public class ActionLibrary {
  
  //// Constructors
  
  /** Creates a new instance of ActionLibrary and stores it in the singleton instance. */
  public ActionLibrary() {
    actionLibrary = this;
  }
  
  //// Public Area
  
  /**
   * Gets the action library singleton instance.
   *
   * @return the action library singleton instance
   */
  public static ActionLibrary getInstance () {
    return actionLibrary;
  }
  
  /**
   * Gets the action associated with the given action name.
   *
   * @param name the given action name
   * @return the cloned action associated with the given action name
   */
  public Action getAction (String name) {
    Action action = (Action)  actionDictionary.get(name);
    return (Action) action.clone();
  }

  /**
   * Sets the action associated with the given action name.
   *
   * @param name the given action name
   * @param action the given action
   */
  public void setAction (String name, Action action) {
    actionDictionary.put(name, action);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the action library singleton instance */
  protected static ActionLibrary actionLibrary;
  
  /**
   * the dictionary that associates a given action name with the action object
   */
  protected HashMap actionDictionary = new HashMap();
  
  //// Main
  
}
