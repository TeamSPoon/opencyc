package org.opencyc.conversation;

import java.util.*;

/**
 * Contains the attributes and behavior of a chat fsm Finite
 * State Machine State.<p>
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
public class State implements Comparable {

    /**
     * state identifier within the fsm
     */
    protected String stateId;

    /**
     * performative name --> Arc
     */
    protected HashMap arcs = new HashMap();

    /**
     * Constructs a new State object.
     *
     * @param stateId unique state identifier within the fsm
     */
    public State(String stateId) {
        this.stateId = stateId;
    }

    /**
     * Constructs a new State object.
     *
     * @param stateId unique state identifier within the fsm
     * @param fsmClass the fsm class containing this new state
     */
    public State(String stateId, FsmClass fsmClass) {
        this.stateId = stateId;
        fsmClass.addState(this);
    }

    /**
     * Constructs a new State object.
     *
     * @param stateId unique state identifier within the fsm
     * @param fsm the fsm containing this new state
     */
    public State(String stateId, Fsm fsm) {
        this.stateId = stateId;
        fsm.addState(this);
    }

    /**
     * Returns the state id.
     *
     * @return the state id
     */
    public String getStateId () {
        return stateId;
    }

    /**
     * Returns the collection of the arcs from this state.
     *
     * @return the collection of the arcs from this state
     */
    public Collection getArcs () {
        return arcs.values();
    }

    /**
     * Records the arc to take when the its performative is observed.
     *
     * @param arc the FSM arc which specifies a transition-to state and an action
     */
    public void addArc (Arc arc) {
         addArc(arc.getPerformative(), arc);
    }

    /**
     * Records the arc to take when the given performative is observed.
     *
     * @param performative the given performative
     * @param arc the FSM arc which specifies a transition-to state and an action
     */
    public void addArc (Performative performative,
                                 Arc arc) {
         arcs.put(performative.performativeName, arc);
    }

    /**
     * Returns the arc to take for the given performative.
     *
     * @param performative the given performative
     * @return the FSM arc which specifies a transition-to state and an action
     */
    public Arc getArc (Performative performative) {
        return (Arc) arcs.get(performative.performativeName);
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
        if (! (object instanceof State))
            throw new ClassCastException("Must be a State object");
        return this.stateId.compareTo(((State) object).stateId);
     }

    /**
     * Returns <tt>true</tt> iff some object equals this object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof State))
            return false;
        return ((State) object).stateId.equals(stateId) &&
               ((State) object).arcs.equals(arcs);
    }

    /**
     * Returns the string representation of the <tt>State</tt>
     *
     * @return the representation of the <tt>State</tt> as a <tt>String</tt>
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n  ");
        stringBuffer.append(stateId.toString());
        stringBuffer.append(", ");
        stringBuffer.append(arcs.size());
        stringBuffer.append(" arcs");
        Iterator arcsIterator = arcs.values().iterator();
        while (arcsIterator.hasNext()) {
            Arc arc = (Arc) arcsIterator.next();
            stringBuffer.append("\n    ");
            stringBuffer.append(arc.toString());
        }
        return stringBuffer.toString();
    }

    /**
     * Creates a clone of this object.
     */
    public Object clone () {
        State state = new State(this.stateId);
        state.arcs = (HashMap) this.arcs.clone();
        return state;
    }

}