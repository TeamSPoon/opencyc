package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.dictionary.DictionaryEnter;
import org.opencyc.elf.bg.dictionary.DictionaryKeys;
import org.opencyc.elf.bg.dictionary.DictionaryLookup;
import org.opencyc.elf.bg.dictionary.DictionaryRemove;
import org.opencyc.elf.bg.dictionary.DictionaryValues;
import org.opencyc.elf.bg.dictionary.TheEmptyDictionary;
import org.opencyc.elf.bg.expression.Minus;
import org.opencyc.elf.bg.expression.Plus;
import org.opencyc.elf.bg.list.FirstInList;
import org.opencyc.elf.bg.list.JoinLists;
import org.opencyc.elf.bg.list.LengthOfList;
import org.opencyc.elf.bg.list.RestOfList;
import org.opencyc.elf.bg.list.TheEmptyList;
import org.opencyc.elf.bg.list.TheList;
import org.opencyc.elf.bg.predicate.And;
import org.opencyc.elf.bg.predicate.Different;
import org.opencyc.elf.bg.predicate.Equals;
import org.opencyc.elf.bg.predicate.LessThan;
import org.opencyc.elf.bg.predicate.Not;
import org.opencyc.elf.bg.predicate.NotNull;
import org.opencyc.elf.bg.predicate.Or;
import org.opencyc.elf.bg.predicate.True;
import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.Command;
import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.wm.ActionFactory;
import org.opencyc.elf.wm.ActionLibrary;
import org.opencyc.elf.wm.ActuatorClassFactory;
import org.opencyc.elf.wm.ActuatorFactory;
import org.opencyc.elf.wm.ActuatorPool;
import org.opencyc.elf.wm.ExperienceLibrary;
import org.opencyc.elf.wm.GoalFactory;
import org.opencyc.elf.wm.GoalLibrary;
import org.opencyc.elf.wm.JobFactory;
import org.opencyc.elf.wm.JobLibrary;
import org.opencyc.elf.wm.KnowledgeBase;
import org.opencyc.elf.wm.NodeFactory;
import org.opencyc.elf.wm.PredicateClassFactory;
import org.opencyc.elf.wm.ResourceFactory;
import org.opencyc.elf.wm.ResourcePool;
import org.opencyc.elf.wm.SensorFactory;
import org.opencyc.elf.wm.SensorPool;
import org.opencyc.elf.wm.StateVariableFactory;
import org.opencyc.elf.wm.StateVariableLibrary;
import org.opencyc.elf.wm.state.State;

//// External Imports
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import java.util.logging.Logger;

/** BehaviorEngine provides the main method for the behavior engine that consists of a hierarchy of
 * Elementary Loop Functioning (ELF) nodes.  There is a singleton instance of behavior engine.
 * 
 * @version $Id$
 * @author Stephen L. Reed   <p><p><p><p><p>
 */
public class BehaviorEngine {
  
  //// Constructors

  /** Creates a new instance of BehaviorEngine and stores it in the singleton instance.
   */
  public BehaviorEngine() {
  }

  //// Public Area

  /** Initializes the behavior engine.
   */
  public void initialize() {
    logger = Logger.getLogger("org.opencyc.elf");
    logger.info("Initializing BehaviorEngine");
    (new ActuatorClassFactory()).getInstance().generate();
    (new PredicateClassFactory()).getInstance().generate();
    createSingletonInstances();
    //TODO root command
  }

  /** Executes the behavior engine
   */
  public void execute() {
    Node node = NodeFactory.getInstance().makeNodeShell();

    // no node superior to the root node.
    node.getSensoryPerception().initialize((Puttable) null);
    node.getBehaviorGeneration().getJobAssigner().initialize(
          (Puttable) null);
    node.getWorldModel().setState(new State());

    Command rootCommand = ActionLibrary.getInstance().getAction(Action.CONVERSE_WITH_USER);
    TaskCommand taskCommand = new TaskCommand(rootCommand, null);

    DoTaskMsg doTaskMsg = new DoTaskMsg(null, taskCommand);

    try {
      node.getBehaviorGeneration().getJobAssigner().getChannel().put(
            doTaskMsg);
    }
     catch (InterruptedException e) {
      logger.severe(e.getMessage());
    }
  }

  //// Protected Area

  /** Creates the singleton instances and populates the object libraries.
   */
  protected void createSingletonInstances() {
    new KnowledgeBase();
    new StateVariableLibrary();
    (new StateVariableFactory()).getInstance().populateStateVariableLibrary();
    createOperatorSingletonInstances();
    new ActionLibrary();
    (new ActionFactory()).getInstance().populateActionLibrary();
    new GoalLibrary();
    (new GoalFactory()).getInstance().populateGoalLibrary();
    new ResourcePool();
    (new ResourceFactory()).getInstance().populateResourcePool();
    new JobLibrary();
    (new JobFactory()).getInstance().populateJobLibrary();
    new ExperienceLibrary();
    new ActuatorPool();
    (new ActuatorFactory()).getInstance().populateActuatorPool();
    new SensorPool();
    (new SensorFactory()).getInstance().populateSensorPool();
    new NodeFactory();
  }

  /** Creates the operator singleton instances.
   */
  protected void createOperatorSingletonInstances() {
    new DictionaryEnter();
    new DictionaryKeys();
    new DictionaryLookup();
    new DictionaryRemove();
    new DictionaryValues();
    new TheEmptyDictionary();
    new Minus();
    new Plus();
    new FirstInList();
    new JoinLists();
    new LengthOfList();
    new RestOfList();
    new TheEmptyList();
    new TheList();
    new And();
    new Different();
    new Equals();
    new LessThan();
    new Not();
    new NotNull();
    new Or();
    new True();
  }

  //// Private Area
  
  //// Internal Rep

  /** the logger */
  protected static Logger logger;

  /** the behavior engine singleton instance */
  protected static BehaviorEngine behaviorEngine;

  //// Main

  /** Provides the main method for the behavior engine.
   * 
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    new BehaviorEngine();
    behaviorEngine.initialize();
    behaviorEngine.execute();
    System.exit(0);
  }
}