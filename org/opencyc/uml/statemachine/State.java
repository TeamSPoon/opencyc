package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.interpreter.*;

/**
 * State from the UML State_Machines package.
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

public abstract class State extends StateVertex {

    /**
     * the entry procedure for this state
     */
    protected Procedure entry;

    /**
     * the exit procedure for this state
     */
    protected Procedure exit;

    /**
     * the deferrable events for this state
     */
    protected ArrayList deferrableEvent = new ArrayList();

    /**
     * the do activity for this state
     */
    protected Procedure doActivity;

    /**
     * the internal transitions for this state
     */
    protected ArrayList internalTransition = new ArrayList();

    /**
     * the state machine for this state (if top)
     */
    protected StateMachine stateMachine;

    /**
     * the state interpreter for this state
     */
    protected StateInterpreter stateInterpreter;

    /**
     * Indicates whether this state is currently active during execution of
     * the state machine.  A state becomes active when it is entered as a
     * result of some transition, and becomes inactive if it is exited as a
     * result of a transition.
     */
    protected boolean isActive;


    /**
     * the do activity thread for this state
     */
    protected DoActivity doActivityThread;

    /**
     * Constructs a new State object.
     */
    public State() {
    }

    /**
     * Gets the entry procedure for this state.
     *
     * @return the entry procedure for this state
     */
    public Procedure getEntry () {
        return entry;
    }

    /**
     * Sets the entry procedure for this state.
     *
     * @param entry the entry procedure for this state
     */
    public void setEntry (Procedure entry) {
        this.entry = entry;
    }

    /**
     * Gets the exit procedure for this state.
     *
     * @return the exit procedure for this state
     */
    public Procedure getExit () {
        return exit;
    }

    /**
     * Sets the exit procedure for this state.
     *
     * @param exit the exit procedure for this state
     */
    public void setExit (Procedure exit) {
        this.exit = exit;
    }

    /**
     * Gets the deferrable events for this state.
     *
     * @return the deferrable events for this state
     */
    public ArrayList getDeferrableEvent () {
        return deferrableEvent;
    }

    /**
     * Sets the deferrable events for this state.
     *
     * @param deferrableEvent the deferrable events for this state
     */
    public void setDeferrableEvent (ArrayList deferrableEvent) {
        this.deferrableEvent = deferrableEvent;
    }

    /**
     * Gets the do activity for this state.
     *
     * @return the do activity for this state
     */
    public Procedure getDoActivity () {
        return doActivity;
    }

    /**
     * Sets the do activity for this state.
     *
     * @param doActivity the do activity for this state
     */
    public void setDoActivity (Procedure doActivity) {
        this.doActivity = doActivity;
    }

    /**
     * Gets the state machine for this state (if top).
     *
     * @return the state machine for this state
     */
    public StateMachine getStateMachine () {
        return stateMachine;
    }

    /**
     * Sets the state machine for this state (if top).
     *
     * @param stateMachine the state machine for this state
     */
    public void setStateMachine (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }


    /**
     * Gets whether this state is currently active during execution of
     * the state machine.
     *
     * @return whether this state is currently active during execution of
     * the state machine
     */
    public boolean isActive () {
        return isActive;
    }

    /**
     * Sets whether this state is currently active during execution of
     * the state machine.
     *
     * @param isActive whether this state is currently active during execution of
     * the state machine
     */
    public void setIsActive (boolean isActive) {
        this.isActive = isActive;
    }


    /**
     * Gets the do activity thread for this state.
     *
     * @return the do activity thread for this state
     */
    public DoActivity getDoActivityThread () {
        return doActivityThread;
    }

    /**
     * Sets the do activity thread for this state.
     *
     * @param doActivityThread the do activity thread for this state
     */
    public void setDoActivityThread (DoActivity doActivityThread) {
        this.doActivityThread = doActivityThread;
    }

    /**
     * Gets the internal transitions for this state.
     *
     * @return the internal transitions for this state
     */
    public ArrayList getInternalTransition () {
        return internalTransition;
    }

    /**
     * Sets the internal transitions for this state.
     *
     * @param internalTransition the internal transitions for this state
     */
    public void setInternalTransition (ArrayList internalTransition) {
        this.internalTransition = internalTransition;
    }
    /**
     * Gets the state interpreter for this state.
     *
     * @return the state interpreter for this state
     */
    public StateInterpreter getStateInterpreter () {
        return stateInterpreter;
    }

    /**
     * Sets the state interpreter for this state.
     *
     * @param stateInterpreter the state interpreter for this state
     */
    public void setStateInterpreter (StateInterpreter stateInterpreter) {
        this.stateInterpreter = stateInterpreter;
    }

}