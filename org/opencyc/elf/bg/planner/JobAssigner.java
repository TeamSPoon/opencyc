package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.Status;

import org.opencyc.elf.bg.taskframe.TaskFrame;
import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.ArrayList;

/**
 * <P>
 * JobAssigner performs the non-temporal (e.g. spatial) task decomposition
 * among the available agents and resources.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
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
public class JobAssigner {
  //// Constructors

  /**
   * Creates a new instance of JobAssigner
   */
  public JobAssigner() {
  }

  //// Public Area

  /**
   * Performs job assignment for the given task command.
   *
   * @param taskCommand the given task command
   */
  public void doTask (TaskCommand taskCommand) {
  }
  
  /**
   * Receives the task command and task frame.
   * 
   * @param taskCommand the task command
   * @param taskFrame the task frame
   */
  public void receiveTaskFrame (TaskCommand taskCommand, TaskFrame taskFrame) {
    //TODO
  }

  /**
   * Sets the scheduler status given the list of controlled resources.
   *
   * @param status the scheduler status
   */
  public void schedulerStatus (ArrayList controlledResources,
                               Status status) {
  }
  
  /**
   * Schedules the given task command
   * 
   * @param taskCommand the given task command for scheduling
   */
  public void ScheduleJob (TaskCommand taskCommand) {
    //TODO
  }
  
  /**
   * Requests the task frame associated with the given task command.
   *
   * @param taskCommand the task command
   */
  public void fetchTaskFrame (TaskCommand taskCommand) {
    //TODO
    // send via channel
  }
  
  /**
   * Decomposes the given task frame, returning a list of Scheduler/Job pairs.
   *
   * @param the given task frame
   */
  public void decomposeTaskFrame (TaskFrame taskFrame) {
    //TODO
  }
  
  /**
   * Gets the job assigner status.
   *
   * @param status the job assigner status
   */
  public void JobAssignerStatus (Status status) {
    //TODO
  }
  
  
  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof JobAssigner)) {
      return false;
    }

    //TODO
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    //TODO
    return "";
  }

  //// Protected Area
  //// Private Area
  //// Internal Rep
}