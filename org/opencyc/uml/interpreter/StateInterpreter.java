package org.opencyc.uml.interpreter;

import java.util.*;
import javax.swing.tree.*;
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
                            State state) {
        this.interpreter = interpreter;
        this.state = state;
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
    public void interpretTransitionEntry (Transition transition) {
        if (verbosity > 2)
            Log.current.println(transition.toString() + " entering " + state.toString());
        Procedure procedure = transition.getEffect();
        if (procedure != null)
            new ProcedureInterpreter(procedure);
        if (! transition.isSelfTransition())
            performEntryActions(transition);
    }

    /**
     * Performs entry actions for the given entry state and for each of
     * its superstates disjoint from the superstates of the transition
     * source state.
     *
     * @param transition the transition
     */
    protected void performEntryActions (Transition transition) {
        State[] statesFromRootToTarget = interpreter.getStatesFromRootTo(state);
        //TODO think more about how to handle complex tranistions whose source is a vertex
        State source = (State)transition.getSource();
        State[] statesFromRootToSource = interpreter.getStatesFromRootTo(source);
        for (int i = 0; i < statesFromRootToTarget.length; i++) {
            if ((i > statesFromRootToSource.length) ||
                (! statesFromRootToSource[i].equals(statesFromRootToTarget[i]))) {
                    State state = statesFromRootToTarget[i];
                    StateInterpreter stateInterpreter = state.getStateInterpreter();
                    if (stateInterpreter == null)
                        state.setStateInterpreter(new StateInterpreter(interpreter, state));
                    stateInterpreter.enter();
            }
        }
    }

    /**
     * Enters this state, performing the entry action and the do-activity.
     */
    protected void enter () {
        if (verbosity > 2)
            Log.current.println("Entering " + state.toString());
        state.setIsActive(true);
        if (state.getEntry() != null)
            new ProcedureInterpreter(state.getEntry());
        if (state.getDoActivity() != null)
            new DoActivity(state);
    }

    /**
     * Interprets a transition from this state.
     *
     * @param transition the transistion
     */
    public void interpretTransitionExit (Transition transition) {
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
    protected void exit () {
        if (verbosity > 2)
            Log.current.println("Exiting " + state.toString());
        DoActivity doActivityThread = state.getDoActivityThread();
        doActivityThread.terminate();
        if (state.getExit() != null)
            new ProcedureInterpreter(state.getExit());
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

}