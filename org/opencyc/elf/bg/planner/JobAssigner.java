package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

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
public class JobAssigner extends NodeComponent {
  //// Constructors

  /**
   * Creates a new instance of JobAssigner
   */
  public JobAssigner() {
  }

  //// Public Area

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

  /**
   * Receives the do task message from behavior generation.  This
   * triggers the fetch task message to be sent back to behavior generation.
   */
  protected void doTask () {
    //TODO
    // received via channel from behavior generation
    // TaskCommand taskCommand
    fetchTaskFrame();
  }
  
  /**
   * Receives the receive task frame message from behavior generation.  This triggers
   * the decompose task frame message.
   */
  protected void receiveTaskFrame () {
    //TODO
    // received via channel from behavior generation
    // TaskCommand taskCommand
    // TaskFrame taskFrame
    decomposeTaskFrame();
  }

  /**
   * Receives the scheduler status message from a scheduler
   */
  protected void schedulerStatus () {
    //TODO
    // received via channel from a scheduler
    // ArrayList controlledResources
    // Status status
  }
  
  /**
   * Receives the schedule job message from ?.  Subsequently the message is
   * sent to the appropriate scheduler.
   */
  protected void ScheduleJob (TaskCommand taskCommand) {
    //TODO
    // received via channel from ?
    // send via channel to appropriate scheduler
    // TaskCommand taskCommand
    // send receiveScheduleJob(taskCommand) to (appropriate) scheduler
  }
  
  /**
   * Sends the fetch task frame message to behavior generation
   */
  protected void fetchTaskFrame () {
    //TODO
    // send via channel to behavior generation
    // TaskCommand taskCommand
    // send forwardFetchTaskFrame(taskCommand) to behaviorGeneration
  }
  
  /**
   * Sends the decompose task frame message to ?.
   */
  protected void decomposeTaskFrame () {
    //TODO
    // send via channel to ?
    // TaskCommand taskCommand
  }
  
  /**
   * Decomposes the function for the current task frame.
   */
  protected void decomposeFunction () {
    //TODO
    // TaskCommand taskCommand
  }
  
  /**
   * Sends the job assigner status message to behavior generation
   */
  protected void JobAssignerStatus () {
    //TODO
    // send via channel to behavior generation
    // Status status
    // send behaviorGenerationStatus(status) to behaviorGeneration
  }
  
  //// Private Area
  //// Internal Rep
}