package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;

//// External Imports
import java.util.ArrayList;

/**
 * <P>JobAssignment contains non-temporal (e.g. spatial) task decompositions
 * among agents and resources.
 *
 * </p>
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
public class JobAssignment {
  
  //// Constructors
  
  /** Creates a new instance of JobAssignment. */
  public JobAssignment() {
  }
  
  //// Public Area
  
  /**
   * Gets the name of the commanded action to be accomplished
   *
   * @return the name of the commanded action to be accomplished
   */
  public String getActionName () {
    return actionName;
  }

  /**
   * Sets the name of the commanded action to be accomplished
   *
   * @param actionName the name of the commanded action to be accomplished
   */
  public void setActionName (String actionName) {
    this.actionName = actionName;
  }

  /**
   * Gets the resources required to schedule the tasks that accomplish the commanded action
   *
   * @return the resources required to schedule the tasks that accomplish the commanded action
   */
  public ArrayList getRequiredResources () {
    return requiredResources;
  }

  /**
   * Sets the resources required to schedule the tasks that accomplish the commanded action
   *
   * @param requiredResources the resources required to schedule the tasks that accomplish the commanded action
   */
  public void setRequiredResources (ArrayList requiredResources) {
    this.requiredResources = requiredResources;
  }

  /**
   * Gets the action to be performed by a qualified scheduler that will entirely or in part
   * accomplish the commanded action
   *
   * @return the action to be performed by a qualified scheduler that will entirely or in part
   * accomplish the commanded action
   */
  public Action getActionForScheduling () {
    return actionForScheduling;
  }

  /**
   * Sets the action to be performed by a qualified scheduler that will entirely or in part
   * accomplish the commanded action
   *
   * @param actionForScheduling the action to be performed by a qualified scheduler that will entirely or in part
   * accomplish the commanded action
   */
  public void setActionForScheduling (Action actionForScheduling) {
    this.actionForScheduling = actionForScheduling;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[JobAssignment for ");
    stringBuffer.append(requiredResources.toString());
    stringBuffer.append(" action: ");
    stringBuffer.append(actionName);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the name of the commanded action to be accomplished
   */
  protected String actionName;
  
  /**
   * the resources required to schedule the tasks that accomplish the commanded action
   */
  protected ArrayList requiredResources;
  
  /**
   * the action to be performed by a qualified scheduler that will entirely or in part
   * accomplish the commanded action
   */
  protected Action actionForScheduling;
  
  //// Main
  
}
