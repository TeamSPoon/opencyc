package org.opencyc.elf.experience;

import java.util.*;
import java.sql.Timestamp;
import org.opencyc.elf.*;
import org.opencyc.elf.goal.*;
import org.opencyc.uml.statemachine.*;
import org.opencyc.cycobject.*;

/**
 * Provides the Experience container for the Elementary Loop Functioning (ELF).<br>
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

public class Experience extends ELFObject {

    /**
     * when the experience occurred
     */
    protected Timestamp timestamp;

    /**
     * the intial state of the experience
     */
    protected State initialState;

    /**
     * the final state of the experience
     */
    protected State finalState;

    /**
     * the goal that was sought from the initial state
     */
    protected Goal goal;

    /**
     * the state transitions from the initial state to the final state
     */
    protected ArrayList transitions;

    /**
     * the value of the experience
     */
    protected Value value;

    /**
     * Constructs a new experience object.
     */
    public Experience() {
    }

    /**
     * Gets when the experience occurred
     *
     * @return when the experience occurred
     */
    public Timestamp getTimestamp () {
        return timestamp;
    }

    /**
     * Sets when the experience occurred
     *
     * @param xxx when the experience occurred
     */
    public void setTimestamp (Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the intial state of the experience
     *
     * @return the intial state of the experience
     */
    public State getInitialState () {
        return initialState;
    }

    /**
     * Sets the intial state of the experience
     *
     * @param initialState the intial state of the experience
     */
    public void setInitialState (State initialState) {
        this.initialState = initialState;
    }

    /**
     * Gets the final state of the experience
     *
     * @return the final state of the experience
     */
    public State getFinalState () {
        return finalState;
    }

    /**
     * Sets the final state of the experience
     *
     * @param finalState the final state of the experience
     */
    public void setFinalState (State finalState) {
        this.finalState = finalState;
    }

    /**
     * Gets the goal that was sought from the initial state
     *
     * @return the goal that was sought from the initial state
     */
    public Goal getGoal () {
        return goal;
    }

    /**
     * Sets the goal that was sought from the initial state
     *
     * @param goal the goal that was sought from the initial state
     */
    public void setGoal (Goal goal) {
        this.goal = goal;
    }
    /**
     * Gets the state transitions
     *
     * @return the state transitions
     */
    public ArrayList getTransitions () {
        return transitions;
    }

    /**
     * Sets the state transitions
     *
     * @param transitions the state transitions
     */
    public void setTransitions (ArrayList transitions) {
        this.transitions = transitions;
    }

    /**
     * Gets the value of the experience
     *
     * @return the value of the experience
     */
    public Value getValue () {
        return value;
    }

    /**
     * Sets the value of the experience
     *
     * @param xxx the value of the experience
     */
    public void setValue (Value value) {
        this.value = value;
    }
}