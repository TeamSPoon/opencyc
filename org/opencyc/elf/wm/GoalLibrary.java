package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.goal.Goal;

//// External Imports
import java.util.HashMap;

/**
 * GoalLibrary contains the goal dictionary.  There is a singleton instance
 * of the goal library.
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
public class GoalLibrary {
  
  //// Constructors
  
  /** Creates a new instance of GoalLibrary and stores it in the singleton instance. */
  public GoalLibrary() {
    goalLibrary = this;
  }
  
  //// Public Area
  
  /**
   * Gets the goal library singleton instance.
   *
   * @return the goal library singleton instance
   */
  public static GoalLibrary getInstance () {
    return goalLibrary;
  }
  
  /**
   * Gets the goal associated with the given goal name.
   *
   * @param name the given goal name
   * @return the goal associated with the given goal name
   */
  public Goal getGoal (String name) {
    return (Goal) goalDictionary.get(name);
  }

  /**
   * Sets the goal associated with the given goal name.
   *
   * @param name the given goal name
   * @param goal the given goal
   */
  public void setGoal (String name, Goal goal) {
    goalDictionary.put(name, goal);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the goal library singleton instance */
  protected static GoalLibrary goalLibrary;
  
  /**
   * the dictionary that associates a given goal name with the goal object
   */
  protected HashMap goalDictionary = new HashMap();
  
  //// Main
  
}
