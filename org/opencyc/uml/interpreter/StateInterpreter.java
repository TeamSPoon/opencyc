package org.opencyc.uml.interpreter;

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
     * the interpreted active state
     */
    protected State state;

    /**
     * Constructs a new StateInterpreter object.
     */
    public StateInterpreter() {
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
    }

    /**
     * Interprets a transition from this state.
     *
     * @param transition the transistion
     */
    public void interpretTransitionExit (Transition transition) {
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

}