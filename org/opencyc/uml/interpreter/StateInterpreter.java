package org.opencyc.uml.interpreter;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.statemachine.*;

/**
 * Interprets an active state of a UML StateMachine.
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

public class StateInterpreter extends Thread {

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
     * the parent state machine interpreter
     */
    protected Interpreter interpreter;

    /**
     * the procedure interpreter
     */
    protected ProcedureInterpreter procedureInterpreter;

    /**
     * the interpreted active state
     */
    protected State state;

    /**
     * Constructs a new StateInterpreter object given the
     * parent state machine interpreter and the state to
     * interpret.
     *
     * @param interpreter the parent state machine interpreter
     * @param state the state to interpret
     */
    public StateInterpreter(Interpreter interpreter,
                            State state)
        throws IOException, CycApiException {
        this.interpreter = interpreter;
        verbosity = interpreter.getVerbosity();
        this.state = state;
        procedureInterpreter = new ProcedureInterpreter(interpreter.getCycAccess(),
                                                        interpreter.getStateMt(),
                                                        verbosity);
    }

    /**
     * indicates whether a thread is running this state.
     */
    protected boolean isThreadRunning = false;

    /**
     * Interprets the effects of a transition into the given state.
     */
    public void run () {
        isThreadRunning = true;

    }

    /**
     * Interprets a transition into this state.
     *
     * @param transition the transistion
     */
    public void interpretTransitionEntry (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println(transition.toString() + " entering " + state.toString());
        performTransitionEffect(transition);
        if (interpreter.isTerminated)
            return;
        performEntryActions(transition);
    }

    /**
     * Performs entry actions for the given entry state and for each of
     * its superstates disjoint from the superstates of the transition
     * source state.
     *
     * @param transition the transition
     */
    protected void performEntryActions (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        Object[] statesFromRootToTarget = interpreter.getStatesFromRootTo(state);
        //TODO think more about how to handle complex tranistions whose source is a vertex
        StateVertex sourceStateVertex = (StateVertex)transition.getSource();
        if (sourceStateVertex instanceof State) {
            State source = (State) sourceStateVertex;
            Object[] statesFromRootToSource = interpreter.getStatesFromRootTo(source);
            for (int i = 0; i < statesFromRootToTarget.length; i++)
                if ((i > statesFromRootToSource.length) ||
                    (! statesFromRootToSource[i].equals(statesFromRootToTarget[i])))
                    enterState((State) statesFromRootToTarget[i]);
        }
        else {
            for (int i = 0; i < statesFromRootToTarget.length; i++)
                enterState((State) statesFromRootToTarget[i]);
        }
    }

    /**
     * Performs the transition effect in this state and creates
     * the transition completion event.
     */
    public void performTransitionEffect (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        Procedure procedure = transition.getEffect();
        if (procedure != null) {
            if (verbosity > 2)
                Log.current.println("Evaluating effect " + procedure.toString());
            procedureInterpreter.interpretTransitionProcedure(transition);
        }
        if (state instanceof FinalState) {
            state.getContainer().getStateInterpreter().complete();
            return;
        }
        CompletionEvent completionEvent =
            interpreter.getStateMachineFactory().makeCompletionEvent(transition.toString() +
                                                                     "EntryInto" + state.toString(),
                                                                     "Completion of " + transition.toString() +
                                                                     " entering " + state.toString(),
                                                                     state);
        interpreter.enqueueEvent(completionEvent);
    }

    /**
     * Completes this composite state.  If this is the top state then
     * the state machine terminates.
     */
    protected void complete ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Completing " + state.toString());
        exit();
        if (state.equals(interpreter.getStateMachine().getTop()))
            interpreter.terminate();
    }


    /**
     * Enters the given state, which might be the state interpreted by
     * this state interpreter, or might be another state.
     *
     * @param entryState the given state
     */
    public void enterState (State entryState)
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (entryState.equals(state))
            enter();
        else {
            StateInterpreter stateInterpreter = entryState.getStateInterpreter();
            if (stateInterpreter == null) {
                stateInterpreter = new StateInterpreter(interpreter, entryState);
                entryState.setStateInterpreter(stateInterpreter);
            }
            stateInterpreter.enter();
        }
    }

    /**
     * Enters this state, performing the entry action and the do-activity.
     */
    public void enter ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Entering " + state.toString());
        state.setIsActive(true);

        if (! isTopState()) {
            DefaultMutableTreeNode parentStateNode =
                    interpreter.getActiveStateConfigurationTreeNode(getParentState());
            DefaultMutableTreeNode stateNode = new DefaultMutableTreeNode(state);
            parentStateNode.add(stateNode);
            interpreter.getActiveStates().put(state, stateNode);
        }

        if (state.getEntry() != null)
            procedureInterpreter.interpretStateEntryProcedure(state);
        if (state.getDoActivity() != null)
            new DoActivity(state);
    }

    /**
     * Interprets a transition from this state.
     *
     * @param transition the transistion
     */
    public void interpretTransitionExit (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (! isCompositeState()) {
            exit();
            return;
        }
        DefaultMutableTreeNode treeNode = interpreter.getActiveStatesRootedAt(state);
        ArrayList activeSubstateList = new ArrayList();
        Stack treeNodeStack = new Stack();
        treeNodeStack.push(treeNode);
        while (! treeNodeStack.isEmpty()) {
            treeNode = (DefaultMutableTreeNode) treeNodeStack.pop();
            activeSubstateList.add(treeNode.getUserObject());
            Enumeration children = treeNode.children();
            while (children.hasMoreElements())
                treeNodeStack.push(children.nextElement());
        }
        for (int i = activeSubstateList.size() - 1; i > -1; i--)
            ((State) activeSubstateList.get(i)).getStateInterpreter().exit();
    }

    /**
     * Exits this state.
     */
    public void exit () throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Exiting " + state.toString());
        DoActivity doActivityThread = state.getDoActivityThread();
        if (doActivityThread != null)
            doActivityThread.terminate();
        if (state.getExit() != null)
            procedureInterpreter.interpretStateExitProcedure(state);
        DefaultMutableTreeNode stateNode =
            (DefaultMutableTreeNode) interpreter.getActiveStates().get(state);
        interpreter.getActiveStates().remove(state);
        if (state.equals(interpreter.getStateMachine().getTop()))
            interpreter.setStateConfiguration((DefaultTreeModel) null);
        stateNode.removeFromParent();
        CompletionEvent completionEvent = new CompletionEvent(state);
        interpreter.enqueueEvent(completionEvent);
    }

    /**
     * Gets the interpreted active state
     *
     * @return the interpreted active state
     */
    public State getState () {
        return state;
    }

    /**
     * Sets the interpreted active state
     *
     * @param state the interpreted active state
     */
    public void setState (State state) {
        this.state = state;
    }

    /**
     * Gets whether a thread is running this state
     *
     * @return whether a thread is running this state
     */
    public boolean isThreadRunning () {
        return isThreadRunning;
    }

    /**
     * Gets whether the interpreted state is a composite
     * state.
     *
     * @return true if the interpreted state is a composite
     * state, otherwise returns false
     */
    public boolean isCompositeState () {
        return state instanceof CompositeState;
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
     * Returns true if the interpreted state is the top state.
     *
     * @return  true if the interpreted state is the top state
     */
    public boolean isTopState() {
        return state.equals(interpreter.getStateMachine().getTop());
    }

    /**
     * Returns the parent state of the this state, or null if
     * this is the top state.
     *
     * @param state the given state
     * @return the parent state of this state, or null if
     * given the top state
     */
    public State getParentState() {
        if (isTopState())
            return null;
        return interpreter.getParentState(state);
    }

}