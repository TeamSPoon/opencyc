package org.opencyc.conversation;

/**
 * Contains the attributes and behavior of a chat conversation Finite
 * State Machine Arc.<p>
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

public class Arc implements Comparable {

    /**
     * finite state machine state transition from state
     */
    protected State transitionFromState;

    /**
     * finite state machine performative
     */
    protected Performative performative;

    /**
     * finite state machine state transition to state
     */
    protected State transitionToState;

    /**
     * finite state machine state action
     */
    protected Action action;

    /**
     * Constructs a new Arc object, given the current state, performative,
     * transition-to state and the action to take.  Hooks the new arc into
     * the current state.
     *
     * @param transitionFromState the tranistion-from state
     * @param performative the transition trigger performative
     * @param transitionToState the transition-to state
     * @param action the action to take
     */
    public Arc (State transitionFromState,
                Performative performative,
                State transitionToState,
                Action action) {
        this.transitionFromState = transitionFromState;
        this.performative = performative;
        this.transitionToState = transitionToState;
        this.action = action;
        transitionFromState.addArc(this);
    }

    /**
     * Returns the finite state machine state transition from state.
     *
     * @return the finite state machine state transition from state
     */
    public State getTransitionFromState () {
        return transitionFromState;
    }

    /**
     * Returns the finite state machine state performative.
     *
     * @return the finite state machine state performative
     */
    public Performative getPerformative () {
        return performative;
    }

    /**
     * Returns the finite state machine state transition to state.
     *
     * @return the finite state machine state transition to state
     */
    public State getTransitionToState () {
        return transitionToState;
    }

    /**
     * Returns the finite state machine state action.
     *
     * @return the finite state machine state action
     */
    public Action getAction () {
        return action;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     *
     * @param object the reference object with which to compare.
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
     public int compareTo (Object object) {
        if (! (object instanceof Arc))
            throw new ClassCastException("Must be a Arc object");
        Arc that = (Arc) object;
        if (this.transitionFromState.equals(that.transitionFromState))
            return this.performative.compareTo(that.performative);
        else
            return this.transitionFromState.compareTo(that.transitionFromState);
     }

    /**
     * Returns <tt>true</tt> iff some object equals this object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Arc))
            return false;
        return ((Arc) object).performative.equals(performative) &&
               ((Arc) object).action.equals(action) &&
               ((Arc) object).transitionToState.equals(transitionToState);
    }

    /**
     * Returns the string representation of the <tt>Arc</tt>
     *
     * @return the representation of the <tt>Arc</tt> as a <tt>String</tt>
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(transitionFromState);
        stringBuffer.append(", ");
        stringBuffer.append(performative);
        stringBuffer.append(", ");
        stringBuffer.append(action);
        stringBuffer.append(", ");
        stringBuffer.append(transitionToState);
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

}