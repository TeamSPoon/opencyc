package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.taskframe.TaskCommand;

//// External Imports
import java.util.ArrayList;

/**
 * Provides the simulator-predictor for the ELF WorldModel.<br>
 * 
 * @version $Id: SimulatorPredictor.java,v 1.1 2002/11/17 03:08:17 stephenreed
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
public class SimulatorPredictor extends NodeComponent {
  
  //// Constructors
  
  /**
   * Constructs a new SimulatorPredictor object.
   */
  public SimulatorPredictor() {
  }

  //// Public Area
  
  /**
   * Gets the world model which owns this simulator-predictor
   * 
   * @return the world model which owns this simulator-predictor
   */
  public WorldModel getWorldModel() {
    return worldModel;
  }

  /**
   * Sets the world model which owns this simulator-predictor
   * 
   * @param worldModel the world model which owns this simulator-predictor
   */
  public void setWorldModel(WorldModel worldModel) {
    this.worldModel = worldModel;
  }
  
  //// Protected Area
  
  /**
   * Sends the request evaluate schedule message to the world model, which forwards it
   * to value judgement.
   */
  protected void requestEvaluateSchedule () {
    //TODO
    // send via channel to world model
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send forwardRequestEvaluateSchedule(controlledResources, taskCommand, schedule)
    // to worldModel
  }
  
  /**
   * Receives the simulate schedule message from the world model.
   */
  protected void receiveSimulateSchedule () {
    //TODO
    // receive via channel from world model
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  /**
   * Sends the simulation failure notification message to the world model.
   */
  protected void sendSimulationFailureNotification () {
    //TODO
    // send via channel to the world model
    // ArrayList controlledResources
    // TaskCommand taskCommand
    // Schedule schedule
    // send forwardSimulationFailureNotification(controlledResources, taskCommand, schedule)
    // to worldModel
  }
  
  /**
   * Sends the predicted input message to ?.
   */
  protected void sendPredictedInput () {
    //TODO
    // send via channel to ?
    // Object obj
  }
  
  
  
  
  
  //// Private Area
  
  //// Internal Rep
  
  /** the world model which owns this simulator-predictor */
  protected WorldModel worldModel;

  //// Main
}