package org.opencyc.conversation;

import java.util.*;
import org.opencyc.chat.*;
import org.opencyc.templateparser.*;

/**
 * Makes chat conversations which can be interpreted by the Interpreter.<p>
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
public class ConversationFactory {

    /**
     * Caches conversation objects to keep from making them twice.
     * name --> Conversation
     */
    protected static HashMap conversationCache = new HashMap();

    /**
     * Arcs which apply to every state.
     */
    protected static ArrayList globalArcs = new ArrayList();

    /**
     * Action object factory
     */
    protected ActionFactory actionFactory;

    /**
     * Template object factory
     */
    protected TemplateFactory templateFactory;

    protected static final State currentState =
        new State("currentState");

    /**
     * Constructs a new ConversationFactory object.
     */
    public ConversationFactory() {
        actionFactory = new ActionFactory();
        templateFactory = new TemplateFactory();
    }

    /**
     * Resets the conversation caches.
     */
    public static void reset() {
        globalArcs = new ArrayList();
        conversationCache = new HashMap();
    }

    /**
     * Initializes this object.
     */
    public void initialize () {
        templateFactory.makeAllTemplates();
        makeAllGlobalArcs();
        makeAllConversations();
    }

    /**
     * Initializes the global arcs
     */
    protected void makeAllGlobalArcs () {
        makeQuitArc();
        makeDoNotUnderstoodArc();
    }

    /**
     * Initialize all the conversations.
     */
    protected void makeAllConversations () {
        makeChat();
        makeDisambiguateTerm();
        makeTermQuery();
    }

    /**
     * Returns the Conversation having the given name;
     *
     * @param name the conversation name
     */
    public Conversation getConversation (String name) {
        return (Conversation) conversationCache.get(name);
    }

    /**
     * Returns the list of arcs which apply to every state.
     *
     * @return the list of arcs which apply to every state
     */
    public ArrayList getGlobalArcs () {
        return this.getGlobalArcs();
    }

    /**
      * Makes a "do-not-understand" arc for every conversation state.
      * Initial state is current-state.
      * 1. If we are in the current-state state and get a not-understand performative,
      * transition to the current-state state, and perform the do-not-understand action. <br>
      */
    public void makeDoNotUnderstoodArc () {
        Performative notUnderstandPerformative =
            new Performative("not-understand");
        Action doNotUnderstandAction =
            new Action("do-not-understand");
        Arc notUnderstandArc =
            new Arc(currentState,
                    notUnderstandPerformative,
                    currentState,
                    doNotUnderstandAction);
        globalArcs.add(notUnderstandArc);
    }

    /**
      * Makes a "quit" arc for every conversation state.
      * Initial state is current-state.
      * 1. If we are in the current-state state and get a quit performative,
      * transition to the final state, and perform the do-finalization action. <br>
      */
    public void makeQuitArc () {
        Performative quitPerformative =
            new Performative("quit");
        State finalState = new State("final");
        Action doFinalizationAction =
            new Action("do-finalization");
       Arc quitArc = new Arc(currentState,
                             quitPerformative,
                             finalState,
                             doFinalizationAction);

        globalArcs.add(quitArc);
    }

    /**
      * Makes a "chat" Conversation.
      * Initial state is ready.
      * 1. If we are in the ready state and get a term-query performative, transition to the ready state
      * and perform the do-term-query action. <br>
      */
    public Conversation makeChat () {
        Conversation chat = (Conversation) conversationCache.get("chat");
        if (chat != null)
            return chat;
        chat = new Conversation("chat");
        State readyState = new State("ready");
        chat.setInitialState(readyState);
        chat.addState(readyState);

        Performative termQueryPerformative =
            new Performative("term-query");
        Action doTermQueryAction =
            new Action("do-term-query");
        /**
         * 1. If we are in the ready state and get a term-query performative, transition
         * to the ready state and perform the do-term-query action.
         */
        Arc arc1 = new Arc(readyState,
                           termQueryPerformative,
                           readyState,
                           doTermQueryAction);
        conversationCache.put(chat.name, chat);
        return chat;
    }

    /**
     * Makes a "disambiguate-term" Conversation.
     * Initial state is start. <br>
     * 1. If we are in the start state and get a start performative,
     * transition to the disambiguate-term state and perform the
     * disambiguate-parse-term action. <br>
     * 2. If we are in the disambiguate-term state and get a term-match performative,
     * transition to the done state and perform the
     * disambiguate-term-done action. <br>
     * 3. If we are in the disambiguate-term state and get a term-choice performative,
     * transition to the term-choice state and perform the
     * disambiguate-term-choice action. <br>
     * 4. If we are in the term-choice state and get a term-chosen performative,
     * transition to the done state and perform the
     * disambiguate-term-done action. <br>
     */
    public Conversation makeDisambiguateTerm () {
        Conversation disambiguateTerm = (Conversation) conversationCache.get("disambiguate-term");
        if (disambiguateTerm != null)
            return disambiguateTerm;
        disambiguateTerm = new Conversation("disambiguate-term");

        State startState = new State("start");
        disambiguateTerm.addState(startState);
        disambiguateTerm.setInitialState(startState);
        State disambiguateTermState = new State("disambiguate-term");
        disambiguateTerm.addState(disambiguateTermState);
        State termChoiceState = new State("term-choice");
        disambiguateTerm.addState(termChoiceState);
        State doneState = new State("done");
        disambiguateTerm.addState(doneState);

        Action disambiguateParseTermAction =
            actionFactory.makeAction("do-disambiguate-parse-term");
        Action disambiguateTermDoneAction =
            actionFactory.makeAction("do-disambiguate-term-done");
        Action disambiguateTermChoiceAction =
            actionFactory.makeAction("do-disambiguate-term-choice");

        Performative startPerformative = new Performative("start");
        Performative termMatchPerformative = new Performative("term-match");
        Performative termChoicePerformative = new Performative("term-choice");
        Performative termChosenPerformative = new Performative("term-chosen");

        disambiguateTerm.setDefaultPerformative(startPerformative);

        /**
         * 1. If we are in the start state and get a start performative,
         * transition to the disambiguate-term state and perform the
         * disambiguate-parse-term action. <br>
         */
        Arc arc1 = new Arc(startState,
                           startPerformative,
                           disambiguateTermState,
                           disambiguateParseTermAction);

        /**
         * 2. If we are in the disambiguate-term state and get a term-match performative,
         * transition to the done state and perform the
         * disambiguate-term-done action.
         */
        Arc arc2 = new Arc(disambiguateTermState,
                           termMatchPerformative,
                           doneState,
                           disambiguateTermDoneAction);

        /**
         * 3. If we are in the disambiguate-term state and get a term-choice performative,
         * transition to the term-choice state and perform the
         * disambiguate-term-choice action.
         */
        Arc arc3 = new Arc(disambiguateTermState,
                           termChoicePerformative,
                           termChoiceState,
                           disambiguateTermChoiceAction);
        /**
         * 4. If we are in the term-choice state and get a term-chosen performative,
         * transition to the done state and perform the
         * disambiguate-term-done action.
         */
        Arc arc4 = new Arc(termChoiceState,
                           termChosenPerformative,
                           doneState,
                           disambiguateTermDoneAction);

        conversationCache.put(disambiguateTerm.name, disambiguateTerm);
        return disambiguateTerm;
    }

    /**
     * Makes a "term-query" Conversation.
     * Initial state is retrieve-fact. <br>
     * 1. If we are in the retrieve-fact state and get a term-query performative, transition to the
     * prompt-for-more state and perform the reply-with-first-fact action. <br>
     * 2. If we are in the prompt-for-more state and get a more performative, transition to the
     * prompt-for-more state and perform the reply-with-next-fact action. <br>
     * 3. If we are in the prompt-for-more state and get a done performative, transition to the done
     * state and perform no action.
     */
    public Conversation makeTermQuery () {
        Conversation termQuery = (Conversation) conversationCache.get("term-query");
        if (termQuery != null)
            return termQuery;
        termQuery = new Conversation("term-query");

        State retrieveFactState = new State("retrieve-fact");
        termQuery.setInitialState(retrieveFactState);
        termQuery.addState(retrieveFactState);

        State promptForMoreState = new State("prompt-for-more");
        termQuery.addState(promptForMoreState);

        State doneState = new State("done");
        termQuery.addState(doneState);

        Action replyWithFirstFactAction =
            actionFactory.makeAction("do-reply-with-first-fact");
        Action replyWithNextFactAction =
            actionFactory.makeAction("do-reply-with-next-fact");

        Performative termQueryPerformative = new Performative("term-query");
        Performative morePerformative = new Performative("more");
        Performative donePerformative = new Performative("done");

        /**
         * 1. If we are in the retrieve-fact state and get a term-query performative, transition
         * to the prompt-for-more state and perform the reply-with-first-fact action.
         */
        Arc arc1 = new Arc(retrieveFactState,
                           termQueryPerformative,
                           promptForMoreState,
                           replyWithFirstFactAction);

        /**
         * 2. If we are in the prompt-for-more state and get a more performative, transition
         * to the prompt-for-more state and perform the reply-with-next-fact action.
         */
        Arc arc3 = new Arc(promptForMoreState,
                           morePerformative,
                           promptForMoreState,
                           replyWithNextFactAction);
        /**
         * 3. If we are in the prompt-for-more state and get a done performative, transition to the done
         * state and perform no action.
         */
        Arc arc4 = new Arc(promptForMoreState,
                           donePerformative,
                           doneState,
                           null);

        conversationCache.put(termQuery.name, termQuery);
        return termQuery;
    }
}
