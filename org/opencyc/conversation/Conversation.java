package org.opencyc.conversation;

import java.util.*;
import org.opencyc.chat.*;

/**
 * Contains the attributes and behavior of a chat conversation.<p>
 *
 * The chat conversation is in the form of a text conversation using
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
public class Conversation {

    /**
     * name of the conversation
     */
    protected String name;

    /**
     * inital conversation state
     */
    protected State initialState;

    /**
     * dictionary of conversation states, stateName --> State
     */
    protected HashMap conversationFsmStates = new HashMap();

    /**
     * Constructs a new Conversation object given the conversation name
     *
     * @param name the conversation name
     */
    protected Conversation(String name) {
        this.name = name;
    }

    /**
     * Returns the conversation name
     *
     * @return the conversation name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the initial conversation state.
     *
     * @param  initialState the initial conversation state
     */
    public void setInitialState (State initialState) {
        this.initialState = initialState;
    }

    /**
     * Returns the initial conversation state.
     *
     * @return the initial conversation state
     */
    public State getInitialState() {
        return initialState;
    }

    /**
     * Records the stateId and associated State
     *
     * @param conversationFsmState the FSM node identified by its stateId
     */
    public void addState (State conversationFsmState) {
        addState(conversationFsmState.getStateId(), conversationFsmState);
    }

     /**
     * Records the stateId and associated State
     *
     * @param stateId the given stateId
     * @param conversationFsmState the FSM node identified by the stateId
     */
    public void addState (String stateId,
                                  State conversationFsmState) {
        conversationFsmStates.put(stateId, conversationFsmState);
    }
}