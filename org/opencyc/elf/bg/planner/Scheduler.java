package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.Status;

import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports

/**
 * <P>
 * Scheduler performs temporal task decomposition for a given assigned agent
 * and its allocated resources.
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
public class Scheduler {
  //// Constructors

  /**
   * Creates a new instance of Scheduler
   */
  public Scheduler() {
  }

  //// Public Area

  /**
   * Receives the task command for scheduling.
   *
   * @param taskCommand the task command
   */
  public void receiveScheduleJob (TaskCommand taskCommand) {
    //TODO
    // receive via channel
  }
  
  /**
   * Receives the status from the plan selector given the task command and
   * schedule
   *
   * @param taskCommand the task command
   * @param schedule the schedule
   * @param the plan selector status
   */
  public void receivePlanSelectorStatus (TaskCommand taskCommand,
                                         Schedule schedule,
                                         Status status) {
    //TODO
    // receive via channel
  }
  
  /**
   * Receives the status from the executor given the task command and
   * schedule
   *
   * @param taskCommand the task command
   * @param schedule the schedule
   * @param the executor status
   */
  public void receiveExecutorStatus (TaskCommand taskCommand,
                                     Schedule schedule,
                                     Status status) {
    //TODO
    // receive via channel
  }
  
  /**
   * Receives the status from value judgement given the task command and
   * schedule
   *
   * @param taskCommand the task command
   * @param schedule the schedule
   * @param the value judgement status
   */
  public void receiveValueJudgementStatus (TaskCommand taskCommand,
                                           Schedule schedule,
                                           Status status) {
    //TODO
    // receive via channel
  }

  
  
  // TODO
  
  
  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Scheduler)) {
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