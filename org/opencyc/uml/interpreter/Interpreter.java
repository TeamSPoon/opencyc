package org.opencyc.uml.interpreter;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import org.apache.commons.collections.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.statemachine.*;
import org.opencyc.util.*;

/**
 * Interprets a UML StateMachine.
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
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

public class Interpreter {

    /**
     * The quiet verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int QUIET_VERBOSITY = 0;

    /**
     * The default verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * the event queue
     */
    protected UnboundedFifoBuffer eventQueue = new UnboundedFifoBuffer();

    /**
     * the current event
     */
    protected Event currentEvent;

    /**
     * the state machine
     */
    protected StateMachine stateMachine;

    /**
     * The state configuration of all states, which is a tree consisting of a top state
     * at the root down to individual simple states at the leaves.  States
     * other than the leaf states are composite states, and branches in
     * the state configuration are concurrent composite states.
     */
    protected DefaultTreeModel allStatesConfiguration;

    /**
     * a dictionary associating states with tree nodes in the all
     * states configuration
     */
    protected HashMap allStates = new HashMap();

    /**
     * The (active) state configuration, which is a tree consisting of a top state
     * at the root down to individual active simple states at the leaves.  States
     * other than the leaf states are composite states, and branches in
     * the state configuration are concurrent composite states.
     */
    protected DefaultTreeModel stateConfiguration;

    /**
     * a dictionary associating active states with tree nodes in the active
     * state configuration
     */
    protected HashMap activeStates;

    /**
     * The list of selected transitions for firing.  Each of these has an
     * active source state, a matching trigger event and a guard expression
     * which evaluates true.
     */
    protected ArrayList selectedTransitions;

    /**
     * an expression evaluator
     */
    protected ExpressionEvaluator expressionEvaluator;

    /**
     * the cyc access instance
     */
    protected CycAccess cycAccess;

    /**
     * the state machine definition microtheory
     */
    protected CycFort stateMachineDefinitionMt;

    /**
     * the expression evaluation state context
     */
    protected CycConstant stateMt;

    /**
     * the global temporary interpreter workspace
     */
    protected CycFort temporaryWorkspaceMt;

    /**
     * the state machine factory used to create and destroy events
     */
    protected StateMachineFactory stateMachineFactory;

    /**
     * indicates that the state machine has terminated
     */
    protected boolean isTerminated = false;

    /**
     * the context stack pool
     */
    protected ContextStackPool contextStackPool;

    /**
     * Constructs a new Interpreter object.
     */
    public Interpreter() {
    }

    /**
     * Constructs a new Interpreter object given a state machine
     * to interpret given the state machine to interpret
     *
     * @param stateMachine the state machine to interpret
     * @param cycAccess the Cyc access instance
     * @param contextStatePool the context state pool
     */
    public Interpreter(StateMachine stateMachine,
                       CycAccess cycAccess,
                       ContextStackPool contextStackPool)
        throws IOException, CycApiException, ExpressionEvaluationException {
        this(stateMachine,
             cycAccess,
             contextStackPool,
             Interpreter.DEFAULT_VERBOSITY);
    }

    /**
     * Constructs a new Interpreter object given the state machine
     * to interpret.
     *
     * @param stateMachine the state machine to interpret
     * @param cycAccess the Cyc access instance
     * @param contextStatePool the context state pool
     * @param verbosity indicates the verbosity of the interpreter's
     * diagnostic output - 9 = maximum, 0 = quiet
     */
    public Interpreter(StateMachine stateMachine,
                       CycAccess cycAccess,
                       ContextStackPool contextStackPool,
                       int verbosity)
        throws IOException, CycApiException, ExpressionEvaluationException {
        this.stateMachine = stateMachine;
        this.cycAccess = cycAccess;
        this.contextStackPool = contextStackPool;
        this.verbosity = verbosity;
        if (verbosity > 2)
            Log.current.println("Interpreting " + stateMachine.toString());
        initialize();
    }

    /**
     * Initializes this object.
     */
    protected void initialize ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        Log.makeLog("state-machine-interpreter.log");
        expressionEvaluator = new ExpressionEvaluator(cycAccess,
                                                      verbosity);
        stateMachineFactory = new StateMachineFactory();
        stateMachineFactory.setStateMachine(stateMachine);
        stateMachineFactory.setNamespace(stateMachine.getNamespace());
        CycFort stateMachineTerm = cycAccess.getKnownConstantByName(stateMachine.getName());
        stateMachineDefinitionMt =
            (CycFort) cycAccess.getArg2("umlStateMachineDefinition",
                                       stateMachineTerm,
                                       cycAccess.getKnownConstantByName("UMLStateMachineSpindleHeadMt"));
        if (verbosity > 2)
            Log.current.println("  defined in " + stateMachineDefinitionMt.cyclify());
        stateMt = contextStackPool.allocateStateMachineContextFrame(null,
                                                                    stateMachineDefinitionMt,
                                                                    stateMachineTerm);
        cycAccess.unassertMtContentsWithoutTranscript(stateMt);
    }

    /**
     * Executes the Interpreter application.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        try {
        Interpreter interpreter = new Interpreter();
        interpreter.interpret();
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Terminates the interpretation of the state machine.
     */
    public void terminate ()
        throws IOException, CycApiException, ExpressionEvaluationException  {
        if (verbosity > 2)
            Log.current.println("Terminating " + stateMachine.toString());
        isTerminated = true;
        contextStackPool.deallocateContextFrame(stateMt);
    }

    /**
     * Interprets the state machine.
     */
    public void interpret ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Interpreting UML state machine " + stateMachine.getName() +
                                "\n  with evaluation context " + stateMt);
        formAllStatesConfiguration();
        formInitialStateConfiguration();
        while (! isTerminated) {
            eventDispatcher();
            eventProcessor();
            fireSelectedTransitions();
            if (currentEvent != null) {
                if (verbosity > 2)
                    Log.current.println("Destroying " + currentEvent.toString());
                stateMachineFactory.destroyEvent(currentEvent);
                currentEvent = null;
            }
        }
    }

    /**
     * Forms the all states configuration for this
     * state machine.
     */
    protected void formAllStatesConfiguration () {
        State topState = stateMachine.getTop();
        if (verbosity > 2)
            Log.current.println("Forming all states configuration from " + topState.toString());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(topState);
        allStates.put(topState, root);
        allStatesConfiguration = new DefaultTreeModel(root);
        formAllStatesConfigurationFrom(topState, root);
    }

    /**
     * Recursively forms the all states configuration from the given
     * state and tree node.
     *
     * @param state the given state to be placed into the all states configuration
     * @param parentNode the parent tree node in the all states configuration tree
     */
    protected void formAllStatesConfigurationFrom(State state, DefaultMutableTreeNode parentNode) {
        if (state instanceof CompositeState) {
            CompositeState compositeState = (CompositeState) state;
            Iterator subVertices = compositeState.getSubVertex().iterator();
            while (subVertices.hasNext()) {
                StateVertex subVertex = (StateVertex) subVertices.next();
                if (subVertex instanceof State) {
                    DefaultMutableTreeNode stateNode = new DefaultMutableTreeNode(subVertex);
                    parentNode.add(stateNode);
                    allStates.put(subVertex, stateNode);
                    formAllStatesConfigurationFrom((State) subVertex, stateNode);
                }
            }
        }
    }

    /**
     * Forms the initial (active) state configuration for this
     * state machine.
     */
    protected void formInitialStateConfiguration ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        State topState = stateMachine.getTop();
        if (verbosity > 2)
            Log.current.println("Forming initial state configuration from " + topState.toString());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(topState);
        activeStates = new HashMap();
        activeStates.put(topState, root);
        stateConfiguration = new DefaultTreeModel(root);
        if (topState instanceof CompositeState) {
            CompositeState compositeTopState = (CompositeState) topState;
            if (compositeTopState.isConcurrent()) {
                //TODO
                throw new RuntimeException("Concurrent initial states are not implemented.");
            }
            Iterator subVertices = compositeTopState.getSubVertex().iterator();
            while (subVertices.hasNext()) {
                StateVertex subVertex = (StateVertex) subVertices.next();
                if (subVertex instanceof PseudoState &&
                    ((PseudoState) subVertex).getKind() == PseudoState.PK_INITIAL) {
                    if (verbosity > 2)
                        Log.current.println("Starting from " + subVertex.toString());
                    PseudoState initialState = (PseudoState) subVertex;
                    Transition transition = (Transition) initialState.getOutgoing().get(0);
                    transitionEnter(transition);
                }
            }
        }
        else {
            topState.isActive();
            topState.setStateInterpreter(new StateInterpreter(this, topState));
            topState.getStateInterpreter().enter();
        }
        if (verbosity > 2)
            System.out.print(displayStateConfigurationTree());
    }

    /**
     * Processes dispatched event instances according to the general semantics
     * of UML state machines and the specific form of this state machine.
     * Selects the transitions from active source states which are triggered
     * by the current event.
     */
    protected void eventProcessor ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        selectedTransitions = new ArrayList();
        Iterator activeStatesIter = activeStates.keySet().iterator();
        while (activeStatesIter.hasNext()) {
            State state = (State) activeStatesIter.next();
            if (verbosity > 2)
                Log.current.println("Considering transitions from " + state.toString());
            Iterator transitions = state.getInternalTransition().iterator();
            selectTransitions(transitions);
            transitions = state.getOutgoing().iterator();
            selectTransitions(transitions);
        }
    }

    /**
     * Selects transitions from the given state transition iterator.
     *
     * @param transitions the given state transition iterator
     */
    protected void selectTransitions (Iterator transitions)
        throws IOException, CycApiException, ExpressionEvaluationException {
        while (transitions.hasNext()) {
            Transition transition = (Transition) transitions.next();
            if (currentEventEnables(transition)) {
                if (verbosity > 2)
                    Log.current.println("  " + transition.toString() +
                                        " enabled by " + currentEvent.toString());
                BooleanExpression guardExpression = transition.getGuard().getexpression();
                if ((guardExpression == null) ||
                    expressionEvaluator.evaluateBoolean(guardExpression, stateMt)) {
                    if (verbosity > 2)
                        Log.current.println("    selected " + transition.toString());
                    selectedTransitions.add(transition);
                }
            }
        }
    }


    /**
     * Determines whether the given transition can be triggered by the current event.
     *
     * @param transition the given transition
     * @return whether the given transition can be triggered by the current event
     */
    protected boolean currentEventEnables (Transition transition) {
        Event trigger = transition.getTrigger();
        if (trigger == null)
            return true;
        return trigger.getClass().equals(currentEvent.getClass());
    }


    /**
     * Selects and dequeues event instances from the event queue for
     * processing.
     */
    protected void eventDispatcher () {
        if (eventQueue.isEmpty()) {
            currentEvent = null;
            if (verbosity > 2)
                Log.current.println("No events to dispatch");
        }
        else {
            currentEvent = (Event) eventQueue.get();
            if (verbosity > 2)
                Log.current.println("Dispatching " + currentEvent.toString());
        }
    }

    /**
     * Fires the selected transitions and runs the associated
     * actions to completion.
     */
    protected void fireSelectedTransitions ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        Iterator iter = selectedTransitions.iterator();
        while (iter.hasNext()) {
            Transition transition = (Transition) iter.next();
            if (transition.isSelfTransition())
                 internalTransition(transition);
            else {
                transitionExit(transition);
                transitionEnter(transition);
            }
        }
    }

    /**
     * Interprets the given internal transition.
     *
     * @param transition the given internal transition
     */
    protected void internalTransition (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        State targetState = (State) transition.getTarget();
        if (verbosity > 2)
            Log.current.println("Internal transition " + transition.toString() +
                                " in " + targetState.toString());
        targetState.getStateInterpreter().performTransitionEffect(transition);
    }

    /**
     * Transitions from a state with the given transition.
     *
     * @param transition the given transition
     */
    protected void transitionExit (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        StateVertex source = transition.getSource();
        if (source instanceof State) {
            State sourceState = (State) source;
            if (sourceState.getStateInterpreter() == null)
                sourceState.setStateInterpreter(new StateInterpreter(this, sourceState));
            sourceState.getStateInterpreter().interpretTransitionExit(transition);
        }
        else {
            //TODO handle vertices
            throw new RuntimeException("Transitions from vertices not yet implemented.");
        }
    }

    /**
     * Transitions into a state with the given transition.
     *
     * @param transition the given transition
     */
    protected void transitionEnter (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        StateVertex target = transition.getTarget();
        if (target instanceof State) {
            State targetState = (State) target;
            if (targetState.getStateInterpreter() == null)
                targetState.setStateInterpreter(new StateInterpreter(this, targetState));
            targetState.getStateInterpreter().interpretTransitionEntry(transition);
        }
        else {
            //TODO handle vertices
            throw new RuntimeException("Transitions into vertices not yet implemented.");
        }
    }

    /**
     * Adds an event to this state machine's event queue.
     *
     * @param event the event to add  to this state machine's event queue
     */
    public void enqueueEvent (Event event) {
        eventQueue.add(event);
    }

    /**
     * Gets the current value of the given state variable.
     *
     * @param stateVariable the given state variable
     * @return the current value of the given state variable
     */
    public Object getStateVariableValue (String stateVariable)
        throws IOException, CycApiException, ExpressionEvaluationException {
        return getStateVariableValue(cycAccess.getKnownConstantByName(stateVariable));
    }

    /**
     * Gets the current value of the given state variable.
     *
     * @param stateVariable the given state variable
     * @return the current value of the given state variable
     */
    public Object getStateVariableValue (CycFort stateVariable)
        throws IOException, CycApiException, ExpressionEvaluationException {
        return cycAccess.getArg2(cycAccess.getKnownConstantByName("softwareParameterValue"),
                                 stateVariable,
                                 stateMt);
    }

    /**
     * Gets the state machine
     *
     * @return the state machine
     */
    public StateMachine getStateMachine () {
        return stateMachine;
    }

    /**
     * Sets the state machine
     *
     * @param stateMachine the state machine
     */
    public void setStateMachine (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }


    /**
     * Gets the cyc access instance
     *
     * @return the cyc access instance
     */
    public CycAccess getCycAccess () {
        return cycAccess;
    }

    /**
     * Sets the cyc access instance
     *
     * @param cycAccess the cyc access instance
     */
    public void setCycAccess (CycAccess cycAccess) {
        this.cycAccess = cycAccess;
    }

    /**
     * Gets the state machine definition microtheory
     *
     * @return the state machine definition microtheory
     */
    public CycFort getStateMachineDefinitionMt () {
        return stateMachineDefinitionMt;
    }

    /**
     * Sets the state machine definition microtheory
     *
     * @param stateMachineDefinitionMt the state machine definition microtheory
     */
    public void setStateMachineDefinitionMt (CycFort stateMachineDefinitionMt) {
        this.stateMachineDefinitionMt = stateMachineDefinitionMt;
    }

    /**
     * Gets the expression evaluation state context
     *
     * @return the expression evaluation state context
     */
    public CycConstant getStateMt () {
        return stateMt;
    }

    /**
     * Gets the  verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.  0 --> quiet ... 9 -> maximum diagnostic input.
     *
     * @return  the  verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public int getVerbosity () {
        return verbosity;
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Gets the current event.
     *
     * @return the current event
     */
    public Event getCurrentEvent () {
        return currentEvent;
    }

    /**
     * Returns the Tree at the given active state.
     *
     * @param state the given active state
     * @return the sub tree of active states rooted at the given active state
     */
    public DefaultMutableTreeNode getActiveStatesRootedAt (State state) {
        Iterator activeStatesIter = activeStates.keySet().iterator();
        while (activeStatesIter.hasNext()) {
            State activeState = (State) activeStatesIter.next();
            if (state.equals(activeState)) {
                return (DefaultMutableTreeNode) activeStates.get(activeState);
            }
        }
        return null;
    }

    /**
     * Returns the list of states from the root down to the given state
     * in the all states configuration tree.
     *
     * @param state the given state
     * @return the list of states from the root down to the given state
     * in the all states configuration tree
     */
    protected Object[] getStatesFromRootTo (State state) {
        DefaultMutableTreeNode stateTreeNode = (DefaultMutableTreeNode) allStates.get(state);
        return (Object[]) stateTreeNode.getUserObjectPath();
    }

    /**
     * Returns the tree node associated with the given state in the
     * active configuration state tree.
     *
     * @param state the given state
     * @return  the tree node associated with the given state in the
     * active configuration state tree
     */
    public DefaultMutableTreeNode getActiveStateConfigurationTreeNode (State state) {
        return (DefaultMutableTreeNode) activeStates.get(state);
    }

    /**
     * Returns an indented string representation of the all states configuration
     * tree.
     */
    public String displayAllStatesConfigurationTree() {
        StringBuffer stringBuffer = new StringBuffer();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) allStatesConfiguration.getRoot();
        formStringTree(root,
                       0,
                       stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * Returns an indented string representation of the active state configuration
     * tree.
     */
    public String displayStateConfigurationTree() {
        StringBuffer stringBuffer = new StringBuffer();
        if (stateConfiguration != null) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) stateConfiguration.getRoot();
            formStringTree(root,
                           0,
                           stringBuffer);
        }
        return stringBuffer.toString();
    }

    /**
     * Recursively builds the indented string representation of the given
     * state configuration tree.
     *
     * @param stateTreeNode the current tree node
     * @param nestingDepth the nesting depth
     * @param stringBuffer the buffer containing the partially completed string
     * representation of the state configuration tree
     */
    protected void formStringTree(DefaultMutableTreeNode stateTreeNode,
                                  int nestingDepth,
                                  StringBuffer stringBuffer) {
        for (int i = 0; i < nestingDepth; i++)
            stringBuffer.append("  ");
        stringBuffer.append(stateTreeNode.getUserObject().toString());
        stringBuffer.append("\n");
        Enumeration children = stateTreeNode.children();
        while (children.hasMoreElements())
            formStringTree((DefaultMutableTreeNode) children.nextElement(),
                           nestingDepth + 1,
                           stringBuffer);
    }

    /**
     * Returns the parent state of the given state, or null if
     * given the top state.
     *
     * @param state the given state
     * @return the parent state of this state, or null if
     * given the top state
     */
    public State getParentState(State state) {
        if (state.equals(stateMachine.getTop()))
            return null;
        DefaultMutableTreeNode stateTreeNode = (DefaultMutableTreeNode) allStates.get(state);
        DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) stateTreeNode.getParent();
        return (State) parentTreeNode.getUserObject();
    }

    /**
     * Gets the state machine factory used by this interpreter for event
     * creation and destruction.
     *
     * @return  the state machine factory used by this interpreter for event
     * creation and destruction
     */
    public StateMachineFactory getStateMachineFactory () {
        return stateMachineFactory;
    }

    /**
     * Gets the dictionary of active states.
     *
     * @return the dictionary of active states
     */
    public HashMap getActiveStates () {
        return this.activeStates;
    }

    /**
     * Sets the active state configuration to the given
     * configuration tree
     *
     * @param stateConfiguration the given configuration tree, or null if
     * terminating the state machine
     */
    public void setStateConfiguration (DefaultTreeModel stateConfiguration) {
        this.stateConfiguration = stateConfiguration;
    }
    /**
     * Gets the global temporary interpreter workspace
     *
     * @return the global temporary interpreter workspace
     */
    public CycFort getTemporaryWorkspaceMt () {
        return temporaryWorkspaceMt;
    }

    /**
     * Sets the global temporary interpreter workspace
     *
     * @param temporaryWorkspaceMt
     */
    public void setTemporaryWorkspaceMt (CycFort temporaryWorkspaceMt) {
        this.temporaryWorkspaceMt = temporaryWorkspaceMt;
    }

    /**
     * Gets the context stack pool
     *
     * @return the context stack pool
     */
    public ContextStackPool getContextStackPool () {
        return contextStackPool;
    }

    /**
     * Sets the context stack pool
     *
     * @param ContextStackPool
     */
    public void setContextStackPool (ContextStackPool contextStackPool) {
        this.contextStackPool = contextStackPool;
    }

}