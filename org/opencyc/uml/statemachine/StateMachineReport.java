package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.action.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.util.*;

/**
 * Reports the static structure a state machine.
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

public class StateMachineReport {

    /**
     * the state machine
     */
    StateMachine stateMachine;

    /**
     * the top state
     */
    StateVertex topState;

    /**
     * Constructs a new StateMachineReport object.
     */
    public StateMachineReport (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Performs the state machine report.
     */
    public void report () {
        reportStateMachine();
        reportState(topState);
    }

    /**
     * Reports the state machine object.
     */
    protected void reportStateMachine() {
        Log.current.println("StateMachine namespace: " + stateMachine.getNamespace().getName());
        Log.current.println("StateMachine name: " + stateMachine.getName());
        topState = stateMachine.getTop();
        Log.current.println("StateMachine top state: " + topState.getName());
        Log.current.println("StateMachine top context: " + stateMachine.getContext().toString());
    }

    /**
     * Reports the given state and any child states.
     */
    protected void reportState (StateVertex stateVertex) {
        if (stateVertex instanceof PseudoState)
            reportPseudoState((PseudoState) stateVertex);
        else if (stateVertex instanceof SimpleState)
            reportSimpleState((SimpleState) stateVertex);
        else if (stateVertex instanceof FinalState)
            reportFinalState((FinalState) stateVertex);
        else if (stateVertex instanceof CompositeState) {
            reportCompositeState((CompositeState) stateVertex);
            Iterator iter = ((CompositeState) stateVertex).getSubVertex().iterator();
            while (iter.hasNext())
                reportState((StateVertex) iter.next());
        }
        else
            throw new RuntimeException("Unhandled StateVertex type " + stateVertex.toString());
    }

    /**
     * Reports the given pseudo state.
     *
     * @param pseudoState the given pseudo state
     */
    protected void reportPseudoState (PseudoState pseudoState) {
        Log.current.println("PseudoState: " + pseudoState.getName());
        if (pseudoState.getKind() == PseudoState.PK_CHOICE)
            Log.current.println("  kind: Choice");
        else if (pseudoState.getKind() == PseudoState.PK_DEEPHISTORY)
            Log.current.println("  kind: DeepHistory");
        else if (pseudoState.getKind() == PseudoState.PK_FORK)
            Log.current.println("  kind: Fork");
        else if (pseudoState.getKind() == PseudoState.PK_INITIAL)
            Log.current.println("  kind: Initial");
        else if (pseudoState.getKind() == PseudoState.PK_JOIN)
            Log.current.println("  kind: Join");
        else if (pseudoState.getKind() == PseudoState.PK_JUNCTION)
            Log.current.println("  kind: Junction");
        else if (pseudoState.getKind() == PseudoState.PK_SHALLOWHISTORY)
            Log.current.println("  kind: ShallowHistory");
        else
            throw new RuntimeException("Unhandled kind " + pseudoState.toString());
        if (pseudoState.getContainer() != null)
            Log.current.println("  container: " + pseudoState.getContainer());
        Iterator iter = pseudoState.getOutgoing().iterator();
        while (iter.hasNext()) {
            Transition outgoingTransition = (Transition) iter.next();
            reportTransitionDetails(outgoingTransition, "outgoing");
        }
    }

    /**
     * Reports the given simple state.
     *
     * @param simpleState the given simple state
     */
    protected void reportSimpleState (SimpleState simpleState) {
        Log.current.println("SimpleState: " + simpleState.getName());
        reportStateDetails(simpleState);
    }

    /**
     * Reports the given final state.
     *
     * @param finalState the given final state
     */
    protected void reportFinalState (FinalState finalState) {
        Log.current.println("FinalState: " + finalState.getName());
        reportStateDetails(finalState);
    }

    /**
     * Reports the given composite state.
     *
     * @param compositeState the given composite state
     */
    protected void reportCompositeState (CompositeState compositeState) {
        Log.current.println("CompositeState: " + compositeState.getName());
        reportStateDetails(compositeState);
    }

    /**
     * Reports the state details
     */
    protected void reportStateDetails (State state) {
        if (state.getContainer() != null)
            Log.current.println("  container: " + state.getContainer());
        if (state.getStateMachine() != null)
            Log.current.println("  stateMachine: " + state.getStateMachine());
        if (state.getEntry() != null)
            Log.current.println("  entry: " + state.getEntry());
        if (state.getExit() != null)
            Log.current.println("  exit: " + state.getExit());
        if (state.getDoActivity() != null)
            Log.current.println("  doActivity: " + state.getDoActivity());
        Iterator iter = state.getInternalTransition().iterator();
        while (iter.hasNext()) {
            Transition internalTransition = (Transition) iter.next();
            reportTransitionDetails(internalTransition, "internalTransition");
        }
        iter = state.getOutgoing().iterator();
        while (iter.hasNext()) {
            Transition outgoingTransition = (Transition) iter.next();
            reportTransitionDetails(outgoingTransition, "outgoing");
        }
    }

    /**
     * Reports the transition details
     *
     * @param transition the transition to report
     * @param description the transition description
     */
    protected void reportTransitionDetails (Transition transition, String description) {
        if (transition.getSource().equals(transition.getTarget()))
            Log.current.print("  " + description + ": " +
                              transition.toString());
        else
            Log.current.print("  " + description + ": " +
                              transition.toString() + " to " + transition.getTarget().toString());
        if (transition.getGuard() != null) {
            Log.current.print(" when " +
                              transition.getGuard().getexpression().getBody());
        }
        if (transition.getEffect() != null) {
            Log.current.print("\n    do " +
                              transition.getEffect().getBody());
        }
        Log.current.println();
    }
}