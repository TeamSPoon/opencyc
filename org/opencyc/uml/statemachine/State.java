package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.commonbehavior.*;

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

public class State extends StateVertex {

    /**
     * the entry action for this state
     */
    protected Action entry;

    /**
     * the exit action for this state
     */
    protected Action exit;

    /**
     * the deferrable events for this state
     */
    protected ArrayList deferrableEvent = new ArrayList();

    /**
     * the do activity for this state
     */
    protected Action doActivity;

    /**
     * the state machine for this state (if top)
     */
    protected StateMachine stateMachine;


    /**
     * Constructs a new State object.
     */
    public State() {
    }


    /**
     * Gets the entry action for this state.
     *
     * @return the entry action for this state
     */
    public Action getEntry () {
        return entry;
    }

    /**
     * Sets the entry action for this state.
     *
     * @param entry the entry action for this state
     */
    public void setEntry (Action entry) {
        this.entry = entry;
    }

    /**
     * Gets the exit action for this state.
     *
     * @return the exit action for this state
     */
    public Action getExit () {
        return exit;
    }

    /**
     * Sets the exit action for this state.
     *
     * @param exit the exit action for this state
     */
    public void setExit (Action exit) {
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
    public Action getDoActivity () {
        return doActivity;
    }

    /**
     * Sets the do activity for this state.
     *
     * @param doActivity the do activity for this state
     */
    public void setDoActivity (Action doActivity) {
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

}