package org.opencyc.conversation;

import java.util.*;
import org.opencyc.chat.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Contains the attributes and behavior of a chat conversation interpreter.<p>
 *
 * The chat conversation is in the form of a text conversation using
 * asynchronous receiving and sending of messages. This interpreter contains
 * all the possible finite state machine actions and interprets the
 * actions required for a state transition.
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

public class Interpreter {

    /**
     * reference to the parent ChatterBot
     */
    protected ChatterBot chatterBot;

    /**
     * Reference to the user model, which contains user state and history
     * information, persisted in the Cyc KB.
     */
    protected ChatUserModel chatUserModel;

    /**
     * finite state machine current node
     */
    protected State currentState;

    /**
     * Makes Template objects for the TemplateParser.
     */
    protected TemplateFactory templateFactory;

    /**
     * Parses the users input text.
     */
    protected TemplateParser templateParser;

    /**
     * Performs conversation actions.
     */
    protected Performer performer;

    /**
     * Constructs a new Interpreter without arguments.  Used for unit
     * testing.
     */
    protected Interpreter () {
    }

    /**
     * Constructs a new Interpreter object given the parent
     * ChatterBot.
     *
     * @param chatterBot the parent ChatterBot
     * @param chatUserNickname the preferred name (possibly not unique) of the
     * chat partner
     * @param chatUserUniqueId the unique id assigned to the user by the chat
     * system
     */
    public Interpreter(ChatterBot chatterBot,
                       String chatUserNickname,
                       String chatUserUniqueId) {
        Log.makeLog();
        this.chatterBot = chatterBot;
        chatUserModel = chatterBot.getChatUserModel(chatUserUniqueId);
        initialize();
    }

    /**
     * Receives the given chat message from the given chat partner, obtains
     * the performative, transitions to the corresponding state and performs
     * the required action.
     *
     * @param chatPartner the name of the chat partner
     * @param chatMessage the chat message
     */
    public void receiveChatMessage (String chatMessage) {
        ParseResults parseResults = templateParser.parse(chatMessage);
        Performative performative = parseResults.getPerformative();
        Arc arc = lookupArc(performative);
        transitionState(arc);
        performer.performArc(currentState, arc.getAction());
    }

    /**
     * Returns the arc which corresponds to the performative received in the
     * current state.
     *
     * @param performative the performative
     * @return the arc which corresponds to the performative received in the
     * current state
     */
    protected Arc lookupArc (Performative performative) {
        for (int i = 0; i < ConversationFactory.globalArcs.size(); i++) {
            Arc arc =
                (Arc) ConversationFactory.globalArcs.get(i);
            if (performative.equals(arc.getPerformative()))
                return arc;
        }
        Iterator arcs = this.currentState.getArcs().iterator();
        while (arcs.hasNext()) {
            Arc arc = (Arc) arcs.next();
            if (performative.equals(arc.getPerformative()))
                return arc;
        }
        Log.current.errorPrintln("No valid arc for state " +
                                 currentState.toString() +
                                 "\nmatching performative " + performative);
        return null;
    }

    /**
     * Transitions to the to-state given in the arc, and performs the action.
     *
     * @param arc the finite state machine arc
     */
    protected void transitionState (Arc arc) {
        if (! (arc.getTransitionToState().equals(ConversationFactory.currentState)))
            currentState = arc.getTransitionToState();
        State previousState = arc.getTransitionFromState();
    }

    /**
     * Initializes the fsm interpreter
     */
    public void initialize () {
        templateFactory = new TemplateFactory();
        templateFactory.makeAllTemplates();
        templateParser = new TemplateParser();
        templateParser.initialize();
        performer = new Performer(this);

    }

    /**
     * Sets the current state of the fsm interpreter.  Used to set the initial
     * state only.
     *
     * @param currentState the current state
     */
    public void setCurrentState (State currentState) {
        this.currentState = currentState;
    }

    /**
     * Sends the given chat message into the chat system.
     *
     * @param chatMessage the chat message
     */
    public void sendChatMessage (String chatMessage) throws ChatException {
        chatterBot.sendChatMessage(chatMessage);
    }
}