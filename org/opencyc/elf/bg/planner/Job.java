package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;

import org.opencyc.elf.goal.Goal;

//// External Imports

/** Job contains the assigned task or goal, created by the job assinger when it
 * spatially (or parallel) decomposes the commanded task.  The job is input to the
 * scheduler which develops a schedule (timed sequence of planned subtasks or planned 
 * subgoals) to accomplish its job. 
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
public class Job {
  
  //// Constructors

  /** Creates a new instance of Job */
  public Job(Action assignedJobAction, Goal assignedJobGoal) {
    this.assignedJobAction = assignedJobAction;
    this.assignedJobGoal = assignedJobGoal;
  }
  
  //// Public Area

  /** Gets the action assigned to a scheduler by the job assigner which has spatially decomposed its commanded task
   *
   * @return the action assigned to a scheduler by the job assigner which has spatially decomposed its commanded task
   */
  public Action getAssignedJobAction () {
    return assignedJobAction;
  }

  /** Gets the goal assigned to a scheduler by the job assigner which has spatially decomposed its commanded task
   *
   * @return the goal assigned to a scheduler by the job assigner which has spatially decomposed its commanded task
   */
  public Goal getAssignedJobGoal () {
    return assignedJobGoal;
  }

  //// Protected Area

  //// Private Area

  //// Internal Rep

  /** the action assigned to a scheduler by the job assigner which has spatially decomposed its commanded task */
  protected Action assignedJobAction;
  
  /** the goal assigned to a scheduler by the job assigner which has spatially decomposed its commanded task */
  protected Goal assignedJobGoal;
  
}
