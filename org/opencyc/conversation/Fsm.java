package org.opencyc.conversation;

import java.util.*;
import org.opencyc.chat.*;

/**
 * Contains the attributes and behavior of a finite state machine instance.<p>
 *
 * Fsms have a class structure in which instances of a class have the
 * same arcs and states, with the exception that instances may have
 * differing actions and sub fsms on their arcs.<br>
 *
 * Subclass fsms inherit states and arcs from their superclass fsm.
 * A subclass fsm may override arcs which would otherwise be inherited.
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
public class Fsm {

    /**
     * name of the fsm instance
     */
    protected String name;

    /**
     * class of the fsm instance
     */
    protected FsmClass fsmClass;

    /**
     * inital fsm state
     */
    protected State initialState;

    /**
     * default performative for the initial state
     */
    protected Performative defaultPerformative;

    /**
     * dictionary of fsm states, stateName --> State
     */
    protected HashMap fsmStates = new HashMap();

    /**
     * Constructs a new Fsm object given the fsm name
     *
     * @param name the fsm name
     */
    protected Fsm(String name) {
        this.name = name;
    }

    /**
     * Returns the fsm name.
     *
     * @return the fsm name
     */
    public String getName () {
        return name;
    }

    /**
     * Returns the fsm class.
     *
     * @return the fsm class
     */
    public FsmClass getFsmClass () {
        return fsmClass;
    }

    /**
     * Sets the initial fsm state.
     *
     * @param  initialState the initial fsm state
     */
    public void setInitialState (State initialState) {
        this.initialState = initialState;
    }

    /**
     * Returns the initial fsm state.
     *
     * @return the initial fsm state
     */
    public State getInitialState() {
        return initialState;
    }

    /**
     * Sets the default performative.
     *
     * @param defaultPerformative the default performative for the initial state, for those
     * cases in which the performative is computed, rather than input directly from the user
     */
    public void setDefaultPerformative (Performative defaultPerformative) {
        this.defaultPerformative = defaultPerformative;
    }

    /**
     * Returns the default performative.
     *
     * @return the default performative for the initial state, for those
     * cases in which the performative is computed, rather than input directly from the user
     */
    public Performative getDefaultPerformative () {
        return defaultPerformative;
    }

    /**
     * Records the stateId and associated State
     *
     * @param fsmFsmState the FSM node identified by its stateId
     */
    public void addState (State fsmState) {
        addState(fsmState.getStateId(), fsmState);
    }

     /**
     * Records the stateId and associated State
     *
     * @param stateId the given stateId
     * @param fsmState the FSM node identified by the stateId
     */
    public void addState (String stateId,
                          State fsmState) {
        fsmStates.put(stateId, fsmState);
    }

    /**
     * Returns the state having the given id.
     *
     * @param stateId the state id
     * @return the state having the given id
     */
    public State getState (String stateId) {
        return (State) fsmStates.get(stateId);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString () {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[fsm: ");
        stringBuffer.append(name);
        stringBuffer.append(", class: ");
        stringBuffer.append(fsmClass.getName());
        stringBuffer.append("\nstates:\n");
        Iterator states = fsmStates.values().iterator();
        while (states.hasNext()) {
            State state = (State) states.next();
            stringBuffer.append(state.toString());
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * Validates fsm integrity.
     */
    protected void validateIntegrity () {
        ArrayList statesList = new ArrayList();
        Iterator states = fsmStates.values().iterator();
        while (states.hasNext())
            statesList.add(states.next());
        if (statesList.size() == 0)
            throw new RuntimeException("fsm has no states\n" + this);
        for (int i = 0; i < statesList.size(); i++) {
            State state = (State) statesList.get(i);
            Iterator arcs = state.getArcs().iterator();
            while (arcs.hasNext()) {
                Arc arc = (Arc) arcs.next();
                Arc arcByPerformative = state.getArc(arc.getPerformative());
                if (arc != arcByPerformative)
                    throw new RuntimeException("arcs list does not contain arc\n" +
                                               arc.toString());
                State transitionFromState = arc.getTransitionFromState();
                State stateById = this.getState(transitionFromState.getStateId());
                if (transitionFromState != stateById) {
                    throw new RuntimeException("fsm class transitionFromState not found\n" +
                                               transitionFromState.toString() +
                                               "\nfsm:\n" + this.toString());
                }
                State transitionToState = arc.getTransitionToState();
                if (! statesList.contains(transitionToState))
                    throw new RuntimeException("fsm class transitionToState not found\n" +
                                               transitionToState.toString() +
                                               "\nfsm:\n" + this.toString());
                Fsm subFsm = arc.getSubFsm();
                if (((arc.getAction() != null) && (subFsm != null)))
                    throw new RuntimeException("arc has both an action and a subFsm\n" +
                                               arc.toString());
            }
        }
    }

}