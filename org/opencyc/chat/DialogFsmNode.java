package org.opencyc.chat;

/**
 * Contains the attributes and behavior of a chat dialog Finite
 * State Machine Node.<p>
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

import java.util.*;

public class DialogFsmNode {

    /**
     * state identifier within the dialog
     */
    protected String stateId;

    /**
     * event --> DialogFsmArc
     */
    protected HashMap dialogFsmArcs = new HashMap();

    /**
     * Constructs a new DialogFsmNode object.
     */
    public DialogFsmNode(String stateId) {
        this.stateId = stateId;
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
     * Records the arc to take when the its event is observed.
     *
     * @param dialogFsmArc the FSM arc which specifies a transition-to state and an action
     */
    public void addDialogFsmArc (DialogFsmArc dialogFsmArc) {
         addDialogFsmArc(dialogFsmArc.getDialogFsmEvent(), dialogFsmArc);
    }

    /**
     * Records the arc to take when the given event is observed.
     *
     * @param dialogFsmEvent the given event
     * @param dialogFsmArc the FSM arc which specifies a transition-to state and an action
     */
    public void addDialogFsmArc (DialogFsmEvent dialogFsmEvent,
                                 DialogFsmArc dialogFsmArc) {
         dialogFsmArcs.put(dialogFsmEvent, dialogFsmArc);
    }

    /**
     * Returns the arc to take for the given event.
     *
     * @param dialogFsmEvent the given event
     * @return the FSM arc which specifies a transition-to state and an action
     */
    public DialogFsmArc getDialogFsmArc (DialogFsmEvent dialogFsmEvent) {
        return (DialogFsmArc) dialogFsmArcs.get(dialogFsmEvent);
    }
}