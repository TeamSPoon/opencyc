package org.opencyc.conversation;

import java.util.*;

/**
 * Contains the conversation and its state.<p>
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

public class ConversationStateInfo {

    /**
     * the conversation
     */
    protected Conversation conversation;

    /**
     * finite state machine current node
     */
    protected State currentState;

    /**
     * Dictionary of state attribute and object values.
     */
    protected HashMap stateAttributes = new HashMap();

    /**
     * Constructs a new ConversationStateInfo object given the conversation and
     * state attributes.
     */
    public ConversationStateInfo (Conversation conversation,
                                  HashMap stateAttributes) {
        this.conversation = conversation;
        this.stateAttributes = stateAttributes;
    }

    /**
     * Sets the current state.
     *
     * @param currentState the current state
     */
    public void setCurrentState (State currentState) {
        this.currentState = currentState;
    }

    /**
     * Returns the current state.
     *
     * @return the current state
     */
    public State getCurrentState () {
        return currentState;
    }

    /**
     * Returns the conversation.
     *
     * @return the conversation
     */
    public Conversation getConversation () {
        return conversation;
    }

    /**
     * Returns the state attributes.
     *
     * @return the state attributes
     */
    public HashMap getStateAttributes () {
        return stateAttributes;
    }

}