package org.opencyc.conversation;

import java.io.*;
import java.net.*;
import java.util.*;
import ViolinStrings.*;
import org.opencyc.api.*;
import org.opencyc.chat.*;
import org.opencyc.cycobject.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Performs actions for the chat conversation interpreter.<p>
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

public class Performer {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * reference to the parent Interpreter object
     */
    protected Interpreter interpreter;

    /**
     * reference to the ConversationFactory object
     */
    protected ConversationFactory conversationFactory;

    /**
     * reference to the TemplateParser object
     */
    protected TemplateParser templateParser;

    /**
     * The context for RKF-related inferences involving all and only english lexical mts.
     */
    protected CycConstant rkfEnglishLexicalMicrotheoryPsc;

    /**
     * #$InferencePSC is a problem solving context in which all assertions in the
     *  entire KB are initially visible.
     */
    protected CycConstant inferencePsc;

    /**
     * Constructs a new Performer object given the reference to the parent
     * finite state machine interpreter object.
     *
     * @param interpreter the parent finite state machine interpreter
     */
    public Performer(Interpreter interpreter) {
        Log.makeLog();
        this.interpreter = interpreter;
        if (interpreter.chatterBot != null) {
            this.conversationFactory = interpreter.chatterBot.conversationFactory;
        }
        else {
            // When unit testing, no ChatterBot is present.
            conversationFactory = new ConversationFactory();
            conversationFactory.initialize();
        }
        this.templateParser = interpreter.templateParser;
    }

    /**
     * Performs the given sub conversation with the given current state.
     *
     * @param currentState the current state of the finite state machine
     * @param subConversation the sub conversation to be performed.
     */
    protected void performArc (State currentState, Conversation subConversation)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
        ArrayList arguments =
            (ArrayList) interpreter.getStateAttribute("subConversation arguments");
        interpreter.setupSubConversation(subConversation, arguments);
    }

    /**
     * Performs the given action with the given current state.
     *
     * @param currentState the current state of the finite state machine
     * @param action the action to be performed
     */
    protected void performArc (State currentState, Action action)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
        if (action.getName().equals("do-not-understand")) {
            doNotUnderstand(currentState, action);
        }
        else if (action.getName().equals("do-finalization")) {
            doFinalization();
        }
        else if (action.getName().equals("do-disambiguate-term-query")) {
            doDisambiguateTermQuery(currentState, action);
        }
        else if (action.getName().equals("do-disambiguate-parse-term")) {
            doDisambiguateParseTermAction(currentState, action);
        }
        else if (action.getName().equals("do-disambiguate-term-choice")) {
            doDisambiguateTermChoiceAction(currentState, action);
        }
        else if (action.getName().equals("do-disambiguate-choice-is-number")) {
            doDisambiguateChoiceIsNumberAction(currentState, action);
        }
        else if (action.getName().equals("do-disambiguate-choice-is-phrase")) {
            doDisambiguateChoiceIsPhraseAction(currentState, action);
        }
        else if (action.getName().equals("do-disambiguate-term-done")) {
            doDisambiguateTermDoneAction(currentState);
        }
        else if (action.getName().equals("reply-with-first-fact")) {
            doReplyWithFirstFact(currentState, action);
        }
        else if (action.getName().equals("reply-with-next-fact")) {
            doReplyWithNextFact(currentState, action);
        }
        else {
            Log.current.errorPrintln("Unhandled action " + action);
        }
    }

    /**
     * Performs the do-not-understand action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doNotUnderstand (State currentState, Action action)
        throws ChatException {
        interpreter.chatterBot.sendChatMessage("I do not understand");
    }

    /**
     * Performs the do-finalization action.
     *
     * @param action the action object.
     *
     */
    protected void doFinalization () {
        interpreter.chatterBot.finalize();
    }

    /**
     * Performs the do-disambiguate-parse-term action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doDisambiguateParseTermAction (State currentState, Action action)
        throws CycApiException, UnknownHostException, IOException {
        ArrayList disambiguationWords =
            (ArrayList) interpreter.getStateAttribute("disambiguation words");
        String disambiguationPhrase = StringUtils.wordsToPhrase(disambiguationWords);
        CycList terms = parseTermsString(disambiguationPhrase);
        System.out.println("terms " + terms.cyclify());
        interpreter.setStateAttribute("disambiguation terms", terms);
        CycList disambiguationPhraseAndTypes =
            CycAccess.current().generateDisambiguationPhraseAndTypes(terms);
        interpreter.setStateAttribute("disambiguation phrase and types",
                                      disambiguationPhraseAndTypes);
        for (int i = 0; i < disambiguationPhraseAndTypes.size(); i++) {
            CycList disambiguationPhraseAndType = (CycList) disambiguationPhraseAndTypes.get(i);
            CycFort term = (CycFort) disambiguationPhraseAndType.first();
            String termString = (String) disambiguationPhraseAndType.second();
            CycFort type = (CycFort) disambiguationPhraseAndType.third();
            String typeString = (String) disambiguationPhraseAndType.fourth();

            if ((disambiguationPhraseAndTypes.size() == 1) ||
                (termString.equals(disambiguationPhrase))) {
                interpreter.setStateAttribute("disambiguated term",
                                              term);
                interpreter.setStateAttribute("disambiguated type string",
                                              typeString);
                interpreter.setNextPerformative(new Performative("term-match"));
                return;
            }
            String pluralPhrase = CycAccess.current().getImprecisePluralGeneratedPhrase(term);
            if ((disambiguationPhraseAndTypes.size() == 1) ||
                (pluralPhrase.equals(disambiguationPhrase))) {
                interpreter.setStateAttribute("disambiguated term",
                                              term);
                interpreter.setNextPerformative(new Performative("term-match"));
                return;
            }
            interpreter.setNextPerformative(new Performative("term-choice"));
            return;
        }
    }

    /**
     * Returns the terms whose parse covers the given text.
     *
     * @param the text phrase
     * @return the terms whose parse covers the given text
     */
    protected CycList parseTermsString (String text)
        throws CycApiException, IOException, UnknownHostException {
        if (rkfEnglishLexicalMicrotheoryPsc  == null)
            rkfEnglishLexicalMicrotheoryPsc =
                CycAccess.current().getKnownConstantByGuid("bf6df6e3-9c29-11b1-9dad-c379636f7270");
        if (inferencePsc == null)
            inferencePsc =
                CycAccess.current().getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycList parseExpressions =
            CycAccess.current().rkfPhraseReader(text,
                                                rkfEnglishLexicalMicrotheoryPsc,
                                                inferencePsc);
        CycList answer = new CycList();
        for (int i = 0; i < parseExpressions.size(); i++) {
            CycList parseExpression = (CycList) parseExpressions.first();
            CycList spanExpression = (CycList) parseExpression.first();
            if (spanExpression.size() == Strings.numWords(text))
                answer.addAllNew((CycList) parseExpression.second());
            else if (verbosity > 2)
                Log.current.println("Bypassing parse\n" + parseExpression.cyclify());
        }
        return answer;
    }

    /**
     * Performs the do-disambiguate-term-choice action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doDisambiguateTermChoiceAction (State currentState, Action action)
        throws CycApiException, ChatException, IOException {
        CycList disambiguationPhraseAndTypes =
            (CycList) interpreter.getStateAttribute("disambiguation phrase and types");
        CycFort knowsAbout =
            CycAccess.current().getKnownConstantByGuid("bd59038b-9c29-11b1-9dad-c379636f7270");
        CycFort cyc =
            CycAccess.current().getKnownConstantByGuid("bd588065-9c29-11b1-9dad-c379636f7270");
        ArrayList termDisambiguationTemplates = new ArrayList();
        for (int i = 0; i < disambiguationPhraseAndTypes.size(); i++) {
            CycList disambiguationPhraseAndType = (CycList) disambiguationPhraseAndTypes.get(i);
            CycFort term = (CycFort) disambiguationPhraseAndType.first();
            String termString = (String) disambiguationPhraseAndType.second();
            CycFort type = (CycFort) disambiguationPhraseAndType.third();
            String typeString = (String) disambiguationPhraseAndType.fourth();
            CycList iKnowAbout = new CycList();
            iKnowAbout.add(knowsAbout);
            iKnowAbout.add(cyc);
            iKnowAbout.add(term);
            String response = CycAccess.current().getParaphrase(iKnowAbout);

            interpreter.chatterBot.sendChatMessage(response);

            Template choiceIsNumberTemplate =
                TemplateFactory.makeChoiceIsNumberTemplate(new Integer(i + 1), term);
            termDisambiguationTemplates.add(choiceIsNumberTemplate);
            Template choiceIsPhraseTemplate =
                TemplateFactory.makeChoiceIsPhraseTemplate(termString, term);
            termDisambiguationTemplates.add(choiceIsPhraseTemplate);
        }

        termDisambiguationTemplates.add(TemplateFactory.makeDoneTemplate());
        termDisambiguationTemplates.add(TemplateFactory.makeQuitTemplate());
        templateParser.setRelevantTemplates(termDisambiguationTemplates);
        interpreter.chatterBot.sendChatMessage("Please choose by phrase or position");

    }

    /**
     * Performs the do-disambiguate-choice-is-number action
     *
     * @param currentState the current conversation state
     * @param action the action containing the numeric choice
     */
    protected void doDisambiguateChoiceIsNumberAction (State currentState, Action action) {
        Object [] content = (Object []) action.getContent();
        CycFort term = (CycFort) content[1];
        interpreter.setStateAttribute("disambiguated term",
                                      term);
        interpreter.setNextPerformative(new Performative("done"));
    }

    /**
     * Performs the do-disambiguate-choice-is-phrase action
     *
     * @param currentState the current conversation state
     * @param action the action containing the phrase choice
     */
    protected void doDisambiguateChoiceIsPhraseAction (State currentState, Action action) {
        Object [] content = (Object []) action.getContent();
        CycFort disambiguatedTerm = (CycFort) content[1];
        interpreter.setStateAttribute("disambiguated term",
                                      disambiguatedTerm);
        interpreter.setNextPerformative(new Performative("done"));
    }

    /**
     * Performs the do-disambiguate-term-done action
     *
     * @param currentState the current conversation state
     */
    protected void doDisambiguateTermDoneAction (State currentState) {
        CycFort disambiguatedTerm = (CycFort) interpreter.getStateAttribute("disambiguated term");
        interpreter.popConversationStateInfo();
        interpreter.setStateAttribute("disambiguated term",
                                      disambiguatedTerm);
    }

    /**
     * Performs the do-disambiguate term-query action.  First performs a disambiguate-term
     * subconversation to obtain the correct term for the query.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doDisambiguateTermQuery (State currentState, Action action)
        throws CycApiException, IOException, UnknownHostException {
        ParseResults parseResults =
            (ParseResults) interpreter.getStateAttribute("parse results");
        ArrayList queryWords =
            parseResults.getTextBinding(CycObjectFactory.makeCycVariable("?term"));
        interpreter.setStateAttribute("query words", queryWords);
        //Conversation disambiguateTerm = conversationFactory.makeDisambiguateTerm();
        Object [] attributeValuePair = {"disambiguation words", queryWords};
        ArrayList arguments = new ArrayList();
        arguments.add(attributeValuePair);
        //interpreter.setupSubConversation(disambiguateTerm, arguments);
        //interpreter.setNextPerformative(disambiguateTerm.getDefaultPerformative());
    }

    /**
     * Performs the reply-with-first-fact action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doReplyWithFirstFact (State currentState, Action action) {
    }

    /**
     * Performs the repy-with-next-fact action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doReplyWithNextFact (State currentState, Action action) {
    }


    /**
     * Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
}