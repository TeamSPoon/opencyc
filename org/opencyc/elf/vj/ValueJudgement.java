package org.opencyc.elf.vj;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.Result;

import org.opencyc.elf.Status;

import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.ArrayList;


/**
 * Provides Value Judgement for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id: ValueJudgement.java,v 1.1 2002/11/18 17:45:42 stephenreed Exp
 *          $
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
public class ValueJudgement extends NodeComponent {
  
  //// Constructors
  
   /**
   * Constructs a new ValueJudgement object.
   */
  public ValueJudgement() {
  }

  
  //// Public Area
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "ValueJudgement for " + node.getName();
  }
  
  //// Protected Area
  
  /**
   * Receives the evaluate schedule message forwarded from world model.
   */
  protected void receiveEvaluateSchedule () {
    //TODO
    // receive via channel from world model
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the schedule evaluation message to behavior generation.
   */
  protected void sendScheduleEvaluation () {
    //TODO
    // send via channel to behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // Result result
    // send receiveScheduleEvaluation(controlledResources, taskCommand, schedule, result)
    // to behaviorGeneration
  }
  
  /**
   * Receives the predicted input message from ?.
   */
  protected void receivePredictedInput () {
    //TODO
    // receive via channel from ?
    // Object obj
  }
  
  /**
   * Receives the update message from sensory perception.
   */
  protected void receiveUpdate () {
    //TODO
    // receive via channel from sensory perception
    // Object obj
    // Object data
  }
  
  /**
   * Sends the value Judgement status message to behavior generation.
   */
  protected void sendValueJudgementStatus () {
    //TODO
    // send via channel to behavior generation
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // Status status
    // send receiveValueJudgementStatus(controlledResources, taskCommand, schedule, status)
    // to behaviorGeneration
  }
  
  
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
}