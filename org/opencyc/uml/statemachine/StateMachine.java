package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.core.*;

/**
 * StateMachine from the UML State_Machines package.
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

public class StateMachine extends ModelElement {

    /**
     * The context Classifier of this state machine, which contains the
     * variables that distinguish the state, and the operations which
     * can be performed upon state transitions.  Note that java Object
     * directly represents UML Classifier for ease of interpretation.
     */
    protected Object context;

    /**
     * the top state of this state machine
     */
    protected State top;

    /**
     * the Transitions for this state machine
     */
    protected ArrayList transitions = new ArrayList();

    /**
     * the submachine state for this state machine
     */
    protected SubmachineState submachineState;

    /**
     * Constructs a new StateMachine object.
     */
    public StateMachine() {
    }

    /**
     * Gets the context of this state machine
     *
     * @return the context of this state machine
     */
    public Object getContext () {
        return context;
    }

    /**
     * Sets the context of this state machine
     *
     * @param context the context of this state machine
     */
    public void setContext (Object context) {
        this.context = context;
    }

    /**
     * Gets the top state of this state machine
     *
     * @return the top state of this state machine
     */
    public State getTop () {
        return top;
    }

    /**
     * Sets the top state of this state machine
     *
     * @param top the top state of this state machine
     */
    public void setTop (State top) {
        this.top = top;
    }

    /**
     * Gets the Transitions for this state machine
     *
     * @return the Transitions for this state machine
     */
    public List getTransitions () {
        return transitions;
    }

    /**
     * Sets the Transitions for this state machine
     *
     * @param transitions the Transitions for this state machine
     */
    public void setTransitions (ArrayList transitions) {
        this.transitions = transitions;
    }

    /**
     * Gets the submachine state for this state machine
     *
     * @return the submachine state for this state machine
     */
    public SubmachineState getSubmachineState () {
        return submachineState;
    }

    /**
     * Sets the submachine state for this state machine
     *
     * @param submachineState the submachine state for this state machine
     */
    public void setSubmachineState (SubmachineState submachineState) {
        this.submachineState = submachineState;
    }

}