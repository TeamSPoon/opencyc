package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.cycobject.CycFort;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.bg.planner.Schedule;
import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.wm.state.State;

//// External Imports
import java.util.ArrayList;

/** Provides the World Model for the Elementary Loop Functioning (ELF).  There is one instance of
 * world model for each node.
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
  
  /** Constructs a new WorldModel object. */
  public WorldModel() {
  }

  //// Public Area
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "WorldModel for " + node.toString();
  }
  
  /** Gets the state.
   *
   * @return the state 
   */
  public State getState () {
    return state;
  }

  /** Sets the state.
   *
   * @param state the state 
   */
  public void setState (State state) {
    this.state = state;
  }

  /** Gets the knowledge base node component
   *
   * @return the knowledge base node component
   */
  public KnowledgeBase getKnowledgeBase () {
    return knowledgeBase;
  }

  /** Sets the knowledge base node component
   *
   * @param knowledgeBase the knowledge base node component
   */
  public void setKnowledgeBase (KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }

  /**
   * Gets the plan simulator node component
   *
   * @return the plan simulator node component
   */
  public PlanSimulator getPlanSimulator () {
    return planSimulator;
  }

  /** Sets the plan simulator node component
   *
   * @param planSimulator the plan simulator node component
   */
  public void setPlanSimulator (PlanSimulator planSimulator) {
    this.planSimulator = planSimulator;
  }

  /** Gets the predictor node component
   *
   * @return the predictor node component
   */
  public Predictor getPredictor () {
    return predictor;
  }

  /** Sets the predictor node component
   *
   * @param predictor the predictor node component
   */
  public void setPredictor (Predictor predictor) {
    this.predictor = predictor;
  }

  /** Gets the entity of attention
   *
   * @return the entity of attention
   */
  public CycFort getEntityOfAttention () {
    return entityOfAttention;
  }

  /** Sets the entity of attention
   *
   * @param entityOfAttention the entity of attention
   */
  public void setEntityOfAttention (CycFort entityOfAttention) {
    this.entityOfAttention = entityOfAttention;
  }

  //// Protected Area
   
  //// Private Area
  
  //// Internal Rep
  
  /** the state */
  protected State state;
  
  /** the knowledge base node component */
  protected KnowledgeBase knowledgeBase;
  
  /** the plan simulator node component */
  protected PlanSimulator planSimulator;
  
  /** the predictor node component */
  protected Predictor predictor;
  
  /** the entity of attention */
  protected CycFort entityOfAttention;
  
  //// Main
}