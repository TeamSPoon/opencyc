package org.opencyc.chat;

/**
 * Contains the attributes and behavior of a chat dialog.<p>
 *
 * The chat dialog is in the form of a text conversation using
 * asynchronous receiving and sending of messages.
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

public class Dialog {

    /**
     * name of the dialog
     */
    protected String name;

    /**
     * inital dialog state
     */
    protected DialogFsmNode initialDialogFsmNode;

    /**
     * dictionary of dialog states, stateName --> DialogFsmNode
     */
    protected HashMap dialogFsmNodes;

    /**
     * Constructs a new Dialog object given the dialog name
     *
     * @param name the dialog name
     */
    protected Dialog(String name) {
        this.name = name;
    }

    /**
     * Returns the dialog name
     *
     * @return the dialog name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the initial dialog state.
     *
     * @param  initialDialogFsmNode the initial dialog state
     */
    public void setInitialDialogFsmNode (DialogFsmNode initialDialogFsmNode) {
        this.initialDialogFsmNode = initialDialogFsmNode;
    }

    /**
     * Returns the initial dialog state.
     *
     * @return the initial dialog state
     */
    public DialogFsmNode getInitialDialogFsmNode() {
        return initialDialogFsmNode;
    }

    /**
     * Records the stateId and associated DialogFsmNode
     *
     * @param dialogFsmNode the FSM node identified by its stateId
     */
    public void addDialogFsmNode (DialogFsmNode dialogFsmNode) {
        addDialogFsmNode(dialogFsmNode.getStateId(), dialogFsmNode);
    }

     /**
     * Records the stateId and associated DialogFsmNode
     *
     * @param stateId the given stateId
     * @param dialogFsmNode the FSM node identified by the stateId
     */
    public void addDialogFsmNode (String stateId,
                                  DialogFsmNode dialogFsmNode) {
        dialogFsmNodes.put(stateId, dialogFsmNode);
    }
}