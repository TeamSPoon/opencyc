package org.opencyc.elf.bg;

//// Internal Imports
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.planner.JobAssigner;
import org.opencyc.elf.bg.planner.PlanSelector;
import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.bg.taskframe.TaskFrame;

import org.opencyc.elf.Result;

//// External Imports

/** Provides Behavior Generation for the Elementary Loop Functioning (ELF).
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
public class BehaviorGeneration extends NodeComponent {

  //// Constructors
  
  /** Constructs a new BehaviorGeneration object.*/
  public BehaviorGeneration() {
  }

  /** Constructs a new BehaviorGeneration object given the containing node.
   *
   * @param node the containing node
   */
  public BehaviorGeneration(Node node) {
    setNode(node);
    node.setBehaviorGeneration(this);
  }

  //// Public Area
    
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    System.out.println("Accessing BehaviorGeneration node: " + node);
    return "BehaviorGeneration for " + node.getName();
  }
  

  /** Gets the job assigner
   *
   * @return the job assigner
   */
  public JobAssigner getJobAssigner () {
    return jobAssigner;
  }

  /** Sets the job assigner
   *
   * @param jobAssigner the job assigner
   */
  public void setJobAssigner (JobAssigner jobAssigner) {
    this.jobAssigner = jobAssigner;
  }

  /** Gets the plan selector
   *
   * @return the plan selector
   */
  public PlanSelector getPlanSelector () {
    return planSelector;
  }

  /** Sets the plan selector
   *
   * @param planSelector the plan selector
   */
  public void setPlanSelector (PlanSelector planSelector) {
    this.planSelector = planSelector;
  }
  
  //// Protected Area
    
  //// Private Area
  
  //// Internal Rep
  
  /** the job assigner */
  protected JobAssigner jobAssigner;
  
  /** the plan selector */
  protected PlanSelector planSelector;
    
}