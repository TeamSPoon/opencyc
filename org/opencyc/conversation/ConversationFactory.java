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
     * Template object factory
     */
    protected TemplateFactory templateFactory;

    protected static final State currentState =
        new State("currentState");

    /**
     * Constructs a new ConversationFactory object.
     */
    public ConversationFactory() {
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
        makeDisambiguateTermQuery();
        makeDisambiguatePhrase();
        makeTermQuery();
        fixupSubConversationForwardReferences();
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
                    null,
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
                             null,
                             doFinalizationAction);

        globalArcs.add(quitArc);
    }

    /**
      * Makes a "chat" Conversation.
      * Initial state is ready.
      * 1. If we are in the ready state and get a disambiguate-term-query performative,
      * transition to the ready state and perform the do-disambiguate-term-query action. <br>
      */
    public Conversation makeChat () {
        Conversation chat = (Conversation) conversationCache.get("chat");
        if (chat != null)
            return chat;
        chat = new Conversation("chat");
        State readyState = new State("ready");
        chat.setInitialState(readyState);
        chat.addState(readyState);

        Performative disambiguateTermQueryPerformative =
            new Performative("disambiguate-term-query");
        Conversation disambiguateTermQuery = new Conversation ("disambiguate-term-query");

        /**
          * 1. If we are in the ready state and get a disambiguate-term-query performative,
          * transition to the ready state and perform the do-disambiguate-term-query action. <br>
         */
        Arc arc1 = new Arc(readyState,
                           disambiguateTermQueryPerformative,
                           readyState,
                           disambiguateTermQuery,
                           null);
        conversationCache.put(chat.name, chat);
        return chat;
    }

    /**
     * Makes a "disambiguate-term-query" Sub Conversation.  <br>
     * Input "disambiguation words" --> ArrayList disambiguationWords <br>
     *
     * Initial state is start. <br>
     * 1. If we are in the start state and get a start-new-conversation performative,
     * transition to the disambiguate-phrase state and perform the
     * disambiguate-phrase sub conversation. <br>
     *
     * 2. If we are in the disambiguate-phrase state and get a resume-previous-conversation
     * performative, transition to the term-query state and perform the
     * term-query sub conversation. <br>
     *
     * 3. If we are in the term-query state and get a resume-previous-conversation performative,
     * transition to the end state and perform the
     * end-sub-conversation action. <br>
     */
    public Conversation makeDisambiguateTermQuery () {
        Conversation disambiguateTermQuery =
            (Conversation) conversationCache.get("disambiguate-term-query");
        if (disambiguateTermQuery != null)
            return disambiguateTermQuery;
        disambiguateTermQuery = new Conversation("disambiguate-term-query");

        State startState = new State("start", disambiguateTermQuery, true);
        State disambiguatePhraseState = new State("disambiguate-phrase", disambiguateTermQuery);
        State termQueryState = new State("term-query-phrase", disambiguateTermQuery);
        State endState = new State("end", disambiguateTermQuery);

        /**
         * 1. If we are in the start state and get a start-new-conversation performative,
         * transition to the disambiguate-phrase state and perform the
         * disambiguate-phrase sub conversation.
         */
        new Arc(startState,
                new Performative("start"),
                disambiguatePhraseState,
                new Conversation("disambiguate-phrase"),
                null);

        /**
         * 2. If we are in the disambiguate-phrase state and get a resume-previous-conversation
         * performative, transition to the term-query state and perform the
         * term-query sub conversation.
         */
        new Arc(disambiguatePhraseState,
                new Performative("resume-previous-conversation"),
                termQueryState,
                new Conversation("term-query"),
                null);

        /**
         * 3. If we are in the term-query state and get a resume-previous-conversation performative,
         * transition to the end state and perform the
         * end-sub-conversation action.
         */
        new Arc(termQueryState,
                new Performative("resume-previous-conversation"),
                endState,
                new Conversation("end-sub-conversation"),
                null);

        conversationCache.put(disambiguateTermQuery.name, disambiguateTermQuery);
        return disambiguateTermQuery;
    }

    /**
     * Makes a "disambiguate-phrase" Sub Conversation.  <br>
     * Input "disambiguation words" --> ArrayList disambiguationWords <br>
     * Output "disambiguated term" --> CycFort disambiguatedTerm <br>
     *
     * Initial state is start. <br>
     * 1. If we are in the start state and get a start performative,
     * transition to the disambiguate-phrase state and perform the
     * do-disambiguate-parse-phase action. <br>
     *
     * 2. If we are in the disambiguate-phrase state and get a term-match performative,
     * transition to the end state and perform the do-end-sub-conversation action. <br>
     *
     * 3. If we are in the disambiguate-phrase state and get a term-choice performative,
     * transition to the term-choice state and perform the do-disambiguate-term-choice action. <br>
     *
     * 4. If we are in the term-choice state and get a choice-is-number performative,
     * transition to (stay in) the term-choice state and perform the
     * do-disambiguate-choice-is-number action. <br>
     *
     * 5. If we are in the term-choice state and get a choice-is-phrase performative,
     * transition to (stay in) the term-choice state and perform the
     * do-disambiguate-choice-is-term action. <br>
     *
     * 6. If we are in the term-choice state and get an end performative,
     * transition to the end state and perform the do-end-sub-conversation action. <br>
     */
    public Conversation makeDisambiguatePhrase () {
        Conversation disambiguatePhrase =
            (Conversation) conversationCache.get("disambiguate-phrase");
        if (disambiguatePhrase != null)
            return disambiguatePhrase;
        disambiguatePhrase = new Conversation("disambiguate-phrase");

        State startState = new State("start", disambiguatePhrase, true);
        State disambiguatePhraseState = new State("disambiguate-phrase", disambiguatePhrase);
        State termChoiceState = new State("term-choice", disambiguatePhrase);
        State endState = new State("end", disambiguatePhrase);

        /**
         * 1. If we are in the start state and get a start performative,
         * transition to the disambiguate-phrase state and perform the
         * do-disambiguate-parse-phrase action.
         */
        new Arc(startState,
                new Performative("start"),
                disambiguatePhraseState,
                null,
                new Action("do-disambiguate-parse-phrase"));

        /**
         * 2. If we are in the disambiguate-phrase state and get a term-match performative,
         * transition to the end state and perform the do-end-sub-conversation action.
         */
        new Arc(disambiguatePhraseState,
                new Performative("term-match"),
                endState,
                null,
                new Action("do-end-sub-conversation"));

        /**
         * 3. If we are in the disambiguate-phrase state and get a term-choice performative,
         * transition to the term-choice state and perform the do-disambiguate-term-choice action.
         */
        new Arc(disambiguatePhraseState,
                new Performative("term-choice"),
                termChoiceState,
                null,
                new Action("do-disambiguate-term-choice"));
        /**
         * 4. If we are in the term-choice state and get a choice-is-number performative,
         * transition to (stay in) the term-choice state and perform the
         * do-disambiguate-choice-is-number action.
         */
        new Arc(termChoiceState,
                new Performative("choice-is-number"),
                termChoiceState,
                null,
                new Action("do-disambiguate-choice-is-number"));
        /**
         * 5. If we are in the term-choice state and get a choice-is-phrase performative,
         * transition to (stay in) the term-choice state and perform the
         * do-disambiguate-choice-is-term action.
         */
        new Arc(termChoiceState,
                new Performative("choice-is-phrase"),
                termChoiceState,
                null,
                new Action("do-disambiguate-choice-is-term"));
        /**
         * 6. If we are in the term-choice state and get an end performative,
         * transition to the end state and perform the do-end-sub-conversation action.
         */
        new Arc(termChoiceState,
                new Performative("end"),
                endState,
                null,
                new Action("do-end-sub-conversation"));

        conversationCache.put(disambiguatePhrase.name, disambiguatePhrase);
        return disambiguatePhrase;
    }

    /**
     * Makes a "term-query" Conversation. <br>
     *
     * input "disambiguated term" --> CycFort disambiguatedTerm <br>
     *
     * Initial state is start. <br>
     *
     * 1. If we are in the start state and get a term-query performative, transition to the
     * retrieve-first-fact state and perform the do-reply-with-first-fact action. <br>
     *
     * 2. If we are in the retrieve-first-fact state and get a more performative, transition to the
     * prompt-for-more state and perform the do-reply-with-next-fact action. <br>
     *
     * 3. If we are in the prompt-for-more state and get a done performative, transition to the end
     * state and perform the do-end-sub-conversation action.
     */
    public Conversation makeTermQuery () {
        Conversation termQuery = (Conversation) conversationCache.get("term-query");
        if (termQuery != null)
            return termQuery;
        termQuery = new Conversation("term-query");

        State startState = new State("start", termQuery, true);
        State retrieveFactState = new State("retrieve-fact", termQuery);
        State promptForMoreState = new State("prompt-for-more", termQuery);
        State endState = new State("end", termQuery);

        /**
         * 1. If we are in the start state and get a term-query performative, transition to the
         * retrieve-first-fact state and perform the do-reply-with-first-fact action.
         */
        new Arc(startState,
                new Performative("term-query"),
                retrieveFactState,
                null,
                new Action("do-reply-with-first-fact"));


        /**
         * 2. If we are in the retrieve-first-fact state and get a more performative, transition to the
         * prompt-for-more state and perform the do-reply-with-next-fact action.
         */
        new Arc(retrieveFactState,
                new Performative("more"),
                promptForMoreState,
                null,
                new Action("do-reply-with-next-fact"));
        /**
         * 3. If we are in the prompt-for-more state and get a done performative, transition to the end
         * state and perform the do-end-sub-conversation action.
         */
        new Arc(promptForMoreState,
                new Performative("done"),
                endState,
                null,
                new Action("do-end-sub-conversation"));

        conversationCache.put(termQuery.name, termQuery);
        return termQuery;
    }

    /**
     * Fixes up the sub conversation forward references.
     */
    protected void fixupSubConversationForwardReferences () {
        Iterator conversations = conversationCache.values().iterator();
        ArrayList conversationsList = new ArrayList();
        while (conversations.hasNext())
            conversationsList.add(conversations.next());
        for (int j = 0; j < conversationsList.size(); j++) {
            ArrayList statesList = new ArrayList();
            Conversation conversation = (Conversation) conversationsList.get(j);
            Iterator states = conversation.conversationFsmStates.values().iterator();
            while (states.hasNext())
                statesList.add(states.next());
            for (int i = 0; i < statesList.size(); i++) {
                State state = (State) statesList.get(i);
                Iterator arcs = state.getArcs().iterator();
                while (arcs.hasNext()) {
                    Arc arc = (Arc) arcs.next();
                    Conversation subConversation = arc.getSubConversation();
                    if ((arc.getSubConversation() != null) &&
                        (! conversationsList.contains(subConversation))) {
                        arc.setSubConversation(this.getConversation(subConversation.getName()));
                    }
                }
            }
        }
    }
}
