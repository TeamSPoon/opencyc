package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.predicate.NotNull;

import org.opencyc.elf.bg.state.StateVariable;

import org.opencyc.elf.wm.ActuatorClassFactory;
import org.opencyc.elf.wm.ActionFactory;
import org.opencyc.elf.wm.ActionLibrary;
import org.opencyc.elf.wm.ActuatorFactory;
import org.opencyc.elf.wm.ActuatorPool;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.ExperienceLibrary;
import org.opencyc.elf.wm.GoalFactory;
import org.opencyc.elf.wm.GoalLibrary;
import org.opencyc.elf.wm.JobAssignmentFactory;
import org.opencyc.elf.wm.JobAssignmentLibrary;
import org.opencyc.elf.wm.KnowledgeBase;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.NodePool;
import org.opencyc.elf.wm.PredicateClassFactory;
import org.opencyc.elf.wm.ResourceFactory;
import org.opencyc.elf.wm.ResourcePool;
import org.opencyc.elf.wm.SensorFactory;
import org.opencyc.elf.wm.SensorPool;
import org.opencyc.elf.wm.StateVariableFactory;
import org.opencyc.elf.wm.StateVariableLibrary;
import org.opencyc.elf.wm.TaskFrameFactory;
import org.opencyc.elf.wm.TaskFrameLibrary;

//// External Imports
import java.util.logging.Logger;

/**
 * BehaviorEngine provides the main method for the behavior engine that
 * consists of a hierarchy of Elementary Loop Functioning (ELF) nodes.  There is a singleton
 * instance of behavior engine.
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
public class BehaviorEngine {
  
  //// Constructors
  
  /** Creates a new instance of BehaviorEngine and stores it in the singleton instance. */
  public BehaviorEngine() {
  }
  
  //// Public Area
  
  /** Initializes the behavior engine. */
  public void initialize () {
    logger = Logger.getLogger("org.opencyc.elf");
    logger.info("Initializing BehaviorEngine");
    (new ActuatorClassFactory()).getInstance().generate();
    (new PredicateClassFactory()).getInstance().generate();
    createSingletonInstances();
    
    //TODO
  }
  
  //// Protected Area
  
  /** Creates the singleton instances and populates the object libraries. */
  protected void createSingletonInstances () {
    new KnowledgeBase();
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    createPredicateSingletonInstances();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobAssignmentLibrary();
    (new JobAssignmentFactory()).getInstance().populateJobAssignmentLibrary();
    new TaskFrameLibrary();
    (new TaskFrameFactory()).getInstance().populateTaskFrameLibrary();
    new ExperienceLibrary();
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    new NodePool();
    (new NodeFactory()).getInstance().populateNodePool();
  }
  
  /** Creates the predicate singleton instances. */
  protected void createPredicateSingletonInstances () {
    new NotNull();
  }
  
  //// Private Area
  
  //// Internal Rep
      
  /** the logger */
  protected static Logger logger;
  
  /** the behavior engine singleton instance */
  protected static BehaviorEngine behaviorEngine;
  
  //// Main
  
  /**
   * Provides the main method for the behavior engine.
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    new BehaviorEngine();
    behaviorEngine.initialize();
    //TODO
    
    System.exit(0);
  }
  
}
