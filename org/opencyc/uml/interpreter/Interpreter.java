package org.opencyc.uml.interpreter;

import java.util.*;
import javax.swing.tree.*;
import org.apache.commons.collections.*;
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
     * The state configuration, which is a tree consisting of a top state
     * at the root down to individual simple states at the leaves.  States
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
     * the java expression evaluator
     */
    protected ExpressionEvaluator expressionEvaluator;

    /**
     * Constructs a new Interpreter object.
     */
    public Interpreter() {
        initialization();
    }

    /**
     * Constructs a new Interpreter object given a state machine
     * to interpret.
     */
    public Interpreter(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
        initialization();
    }

    /**
     * Performs initialization of this object.
     */
    protected void initialization () {
        Log.makeLog("state-machine-interpreter.log");
        expressionEvaluator = new ExpressionEvaluator();
    }

    /**
     * Executes the Interpreter application.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.interpret();
    }

    /**
     * Interprets the state machine.
     */
    public void interpret () {
        while (true) {
            eventDispatcher();
            eventProcessor();
            fireSelectedTransitions();
        }
    }

    /**
     * Processes dispatched event instances according to the general semantics
     * of UML state machines and the specific form of this state machine.
     * Selects the transitions from active source states which are triggered
     * by the current event.
     */
    protected void eventProcessor () {
        selectedTransitions = new ArrayList();
        Iterator activeStatesIter = activeStates.keySet().iterator();
        while (activeStatesIter.hasNext()) {
            State state = (State) activeStatesIter.next();
            Iterator transitions = state.getOutgoing().listIterator();
            while (transitions.hasNext()) {
                Transition transition = (Transition) transitions.next();
                if (transition.getTrigger().equals(currentEvent)) {
                    BooleanExpression guardExpression = transition.getGuard().getexpression();
                    if ((guardExpression == null) ||
                        expressionEvaluator.evaluateBoolean(guardExpression)) {
                        selectedTransitions.add(transition);
                    }
                }
            }
        }
    }

    /**
     * Selects and dequeues event instances from the event queue for
     * processing.
     */
    protected void eventDispatcher () {
        if (eventQueue.isEmpty())
            currentEvent = null;
        else
            currentEvent = (Event) eventQueue.get();
    }

    /**
     * Fires the selected transitions and runs the associated
     * actions to completion.
     */
    protected void fireSelectedTransitions () {
        Iterator iter = selectedTransitions.iterator();
        while (iter.hasNext()) {
            Transition transition = (Transition) iter.next();
            StateVertex source = transition.getSource();
            if (source instanceof State)
                ((State) source).getStateInterpreter().interpretTransitionExit(transition);
            else {
                //TODO handle vertices
            }
            StateVertex target = transition.getTarget();
            if (target instanceof State)
                ((State) target).getStateInterpreter().interpretTransitionEntry(transition);
            else {
                //TODO handle vertices
            }
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
     * in the active state configuration tree.
     *
     * @param state the given state
     * @return the list of states from the root down to the given state
     * in the active state configuration tree
     */
    protected State[] getStatesFromRootTo (State state) {
        DefaultMutableTreeNode stateTreeNode = (DefaultMutableTreeNode) activeStates.get(state);
        return (State[]) stateTreeNode.getUserObjectPath();
    }


}