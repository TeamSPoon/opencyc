package org.opencyc.conversation;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.chat.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Contains the attributes and behavior of a chat conversation interpreter.<p>
 *
 * The chat conversation is in the form of a text conversation using
 * asynchronous receiving and sending of messages. This interpreter models the
 * chat interaction with nested conversations in a stack.  Mixed initiative is
 * supported by a dictionary of conversation stacks, one of which is active,
 * and the rest suspended.  Sub conversations are passed a list of attribute/value
 * pairs which form the initial state attributes.  When done, sub conversations
 * pass back result attribute/value pairs to the calling conversation's state
 * attributes.
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
     * dictionary of conversation stacks
     * conversationStackId --> conversation stack
     */
    protected HashMap conversationStacks;

    /**
     * Reference to the current conversation stack, which is a stack of
     * ConversationInfo elements.  Each of these elements contains
     * the conversation and its state attributes.
     *
     */
    protected StackWithPointer conversationStack;

    /**
     * reference to the active conversation
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
     * Next computed Performative, or null if none.
     */
    protected Performative nextPerformative;

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
                       String chatUserUniqueId,
                       String conversationStackId,
                       Conversation conversation) {
        Log.makeLog();
        this.chatterBot = chatterBot;
        chatUserModel = chatterBot.getChatUserModel(chatUserUniqueId);
        initialize(conversationStackId, conversation);
    }

    /**
     * Receives the given chat message from the given chat partner, obtains
     * the performative, transitions to the corresponding state and performs
     * the required action.
     *
     * @param chatPartner the name of the chat partner
     * @param chatMessage the chat message
     */
    public void receiveChatMessage (String chatMessage)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
        ParseResults parseResults = templateParser.parse(chatMessage);
        setStateAttribute("parse results", parseResults);
        Performative performative = parseResults.getPerformative();
        nextPerformative = performative;
        while (nextPerformative != null) {
            Arc arc = lookupArc(nextPerformative);
            Action action = arc.getAction();
            action.setContent(nextPerformative.getContent());
            nextPerformative = null;
            transitionState(arc);
            Conversation subConversation = arc.getSubConversation();
            if (subConversation != null)
                performer.performArc(currentState, subConversation);
            else
                performer.performArc(currentState, action);
        }
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
            if (performative.getPerformativeName().equals(arc.getPerformative().getPerformativeName()))
                return arc;
        }
        Iterator arcs = currentState.getArcs().iterator();
        while (arcs.hasNext()) {
            Arc arc = (Arc) arcs.next();
            if (performative.getPerformativeName().equals(arc.getPerformative().getPerformativeName()))
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
     * Initializes the fsm interpreter.
     *
     * @param conversationStackId provides an id for the first
     * conversation stack
     * @param conversation the initial conversation, which is also the
     * sole object on the conversation stack
     */
    public void initialize (String conversationStackId,
                            Conversation conversation) {
        templateFactory = new TemplateFactory();
        templateFactory.makeAllTemplates();
        templateParser = new TemplateParser();
        templateParser.initialize();
        performer = new Performer(this);
        conversationStack = new StackWithPointer();
        this.conversation = conversation;
        ConversationStateInfo conversationStateInfo =
            new ConversationStateInfo(conversation, stateAttributes);
        pushConversationStateInfo(conversationStateInfo);
        conversationStacks = new HashMap();
        conversationStacks.put(conversationStackId, conversationStack);
        currentState = conversation.getInitialState();
    }

    /**
     * Sends the given chat message into the chat system.
     *
     * @param chatMessage the chat message
     */
    public void sendChatMessage (String chatMessage) throws ChatException {
        chatterBot.sendChatMessage(chatMessage);
    }

    /**
     * Sets the value for the given state attribute.
     *
     * @param attribute the key object
     * @param value the value object
     */
    public void setStateAttribute (String attribute, Object value) {
        stateAttributes.put(attribute, value);
    }

    /**
     * Sets the value of the computed next performative.
     *
     * @param nextAction the computed next performative
     */
    public void setNextPerformative (Performative nextPerformative) {
        this.nextPerformative = nextPerformative;
    }

    /**
     * Returns the value for the given state attribute.
     *
     * @param attribute the key object
     * @retrun the value for the given attribute
     */
    public Object getStateAttribute (Object attribute) {
        return stateAttributes.get(attribute);
    }

    /**
     * Sets up the given sub conversation and the input arguments as
     * a list of attribute/value pairs.
     *
     * @param conversation the new conversation
     * @param arguments a list of Object arrays of length two, the first array element is the
     * attribute and the second array element is its value
     */
    public void setupSubConversation (Conversation conversation,
                                      ArrayList arguments) {
        ConversationStateInfo conversationStateInfo =
            new ConversationStateInfo(conversation,
                                      new HashMap());
        pushConversationStateInfo(conversationStateInfo);
        currentState = conversation.getInitialState();
        for (int i = 0; i < arguments.size(); i++) {
            Object [] attributeValuePair = (Object []) arguments.get(i);
            String attribute = (String) attributeValuePair[0];
            Object value = attributeValuePair[1];
            setStateAttribute(attribute, value);
        }
        nextPerformative = new Performative("start-new-conversation");
    }

    /**
     * Pushes the given conversation state onto the current
     * conversation stack.
     *
     * @param conversationStateInfo the new conversation and its state
     */
    public void pushConversationStateInfo (ConversationStateInfo conversationStateInfo) {
        if (conversationStack.size() > 0) {
            ConversationStateInfo suspendedConversationStateInfo =
                (ConversationStateInfo) conversationStack.peek();
            suspendedConversationStateInfo.currentState = currentState;
        }
        conversationStack.push(conversationStateInfo);
    }

    /**
     * Pops the conversation state stack and restores the previous
     * conversation state.
     */
    public void popConversationStateInfo () {
        ArrayList results = (ArrayList) getStateAttribute("subConversation results");
        ConversationStateInfo conversationStateInfo =
            (ConversationStateInfo) conversationStack.pop();
        this.conversation = conversationStateInfo.conversation;
        this.currentState = conversationStateInfo.currentState;
        this.stateAttributes = conversationStateInfo.stateAttributes;
        for (int i = 0; i < results.size(); i++) {
            Object [] attributeValuePair = (Object []) results.get(i);
            String attribute = (String) attributeValuePair[0];
            Object value = attributeValuePair[1];
            setStateAttribute(attribute, value);
        }
        nextPerformative = new Performative("resume-previous-conversation");
    }

}