package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.bg.taskframe.TaskFrame;

//// External Imports
import java.util.ArrayList;

/**
 * Provides the World Model for the Elementary Loop Functioning (ELF).<br>
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
public class WorldModel extends NodeComponent {
  
  //// Constructors
  
  /**
   * Constructs a new WorldModel object.
   */
  public WorldModel() {
  }

  //// Public Area
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "WorldModel for " + node.getName();
  }
  
  //// Protected Area
  
  /**
   * Receives the fetch task frame message forwarded from behavior genertion.  The message contains 
   * the task command identifying the task frame.
   */
  protected void receiveFetchTaskFrame () {
    //TODO
    // received via channel from behavior generation
    // TaskCommand taskCommand
  }
  
  /**
   * Forwards the task frame message from ? to behavior generation.
   */
  protected void forwardTaskFrame () {
    //TODO
    // send via channel to behavior generation 
    // TaskCommand taskCommand
    // TaskFrame taskFrame
    // send receiveTaskFrame(taskCommand, taskFrame) to behaviorGeneration
  }
  
  /**
   * Receives the request KB object message from ?.
   */
  protected void receiveRequestKBObject () {
    //TODO
    // received via channel from ?
    // Object obj
  }
  
  /**
   * Forwards the request KB object message to ?.
   */
  protected void forwardRequestKBObject () {
    //TODO
    // send via channel from ?
    // Object obj
  }

  /** 
   * Receives the KB object message from ?.
   */
  protected void receiveKBObject () {
    //TODO
    // received via channel from ?
    // Object obj
  }
  
  /**
   * Forwards the KB object message to ?.
   */
  protected void forwardKBObject () {
    //TODO
    // send via channel from ?
    // Object obj
  }
  
  /**
   * Receives an update KB object message from sensory perception
   */
  protected void receiveUpdate () {
    //TODO
    // received via channel from sensory perception
    // Object obj
    // Object data
  }
  
  /**
   * Receives a post schedule message forwarded from behavior generation.
   */
  protected void receivePostSchedule () {
    //TODO
    // received via channel from behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Forwards the request evaluate schedule message from ? to  value judgement.
   */
  protected void forwardRequestEvaluateSchedule () {
    //TODO
    // send via channel to value judgement
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // Send receiveEvaluateSchedule(controlledResources, taskCommand, schedule) to
    // value judgement
  }

  /**
   * Forwards the simulation failure notification message from ? to behavior generation.
   */
  protected void forwardSimulationFailureNotification () {
    //TODO
    // send via channel to behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send receiveSimulationFailureNotification(controlledResources, taskCommand, schedule)
    // to behaviorGeneration
  }
  
  /**
   * Forwards the predicted input message from ? to ?.
   */
  protected void forwardPredictedInput () {
    //TODO
    // send via channel to ?
    // Object obj
  }
  
  /**
   * Receives the simulate schedule message forwarded from behavior generation.
   */
  protected void receiveSimulateSchedule () {
    //TODO
    // receive via channel from behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  
  
  
  
  
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
}