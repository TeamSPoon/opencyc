package org.opencyc.conversation;

import java.util.*;
import org.opencyc.chat.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Makes finite state machines which can be interpreted by the Interpreter.<p>
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
public class FsmFactory {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 1;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Stores fsm class objects by name.
     * name --> FsmClass
     */
    protected static HashMap fsmClassStore = new HashMap();

    /**
     * Stores fsm objects by name.
     * name --> Fsm
     */
    protected static HashMap fsmStore = new HashMap();

    /**
     * Template object factory
     */
    protected TemplateFactory templateFactory;

    /**
     * Constructs a new FsmFactory object.
     */
    public FsmFactory() {
        Log.makeLog();
        templateFactory = new TemplateFactory();
    }

    /**
     * Resets the fsm stores.
     */
    public static void reset() {
        fsmClassStore = new HashMap();
        fsmStore = new HashMap();
    }

    /**
     * Initializes this object.
     */
    public void initialize () {
        templateFactory.makeAllTemplates();
        makeAllFsmClasses();
        makeAllFsms();
    }

    /**
     * Make all the fsm classes.  Make superclass before
     * making the subclasses.
     */
    protected void makeAllFsmClasses () {
        makeRootFsmClass();
        makeStartEndFsmClass();
        makeChatFsmClass();
        makeDisambiguateTermQueryFsmClass();
        makeDisambiguatePhraseFsmClass();
        makeTermQueryFsmClass();
    }

    /**
     * Make all the fsms.
     */
    protected void makeAllFsms () {
        makeChat();
        makeDisambiguateTermQuery();
        makeDisambiguatePhrase();
        makeTermQuery();
        fixupSubFsmForwardReferences();
    }

    /**
     * Returns the FsmClass having the given name;
     *
     * @param name the fsmClass name
     */
    public static FsmClass getFsmClass (String name) {
        return (FsmClass) fsmClassStore.get(name);
    }

    /**
     * Returns the Fsm having the given name;
     *
     * @param name the fsm name
     */
    public static Fsm getFsm (String name) {
        return (Fsm) fsmStore.get(name);
    }

    /**
      * Makes a "do-not-understand" arc given the current state.
      * 1. If we are in the current-state state and get a not-understand performative,
      * transition to the current-state state, and perform the do-not-understand action.
      *
      * @param currentState the current state
      */
    public void makeDoNotUnderstoodArc (State currentState) {
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
    }

    /**
      * Makes a "quit" arc given the current state.
      * 1. If we are in the current-state state and get a quit performative,
      * transition to the final state, and perform the do-finalization action. <br>
      *
      * @param currentState the current state
      */
    public void makeQuitArc (State currentState) {
        Performative quitPerformative =
            new Performative("quit");
        State quitState = new State("quit");
        Action doFinalizationAction =
            new Action("do-finalization");
       Arc quitArc = new Arc(currentState,
                             quitPerformative,
                             quitState,
                             null,
                             doFinalizationAction);
    }

    /**
     * Makes a root fsm class object.
     * The root class has a start state and a quit arc.
     */
    public void makeRootFsmClass () {
        FsmClass fsmClass = new FsmClass("root", (FsmClass) null);
        State startState = new State("start", fsmClass);
        State quitState = new State("quit", fsmClass);
        makeQuitArc(startState);
        fsmClass.setInitialState(startState);
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("root"));
        }

    /**
     * Makes a start-end fsm class object.
     * The start-end class inherits from root and has an end state.
     */
    public void makeStartEndFsmClass () {
        FsmClass fsmClass = FsmClass.makeSubClass("start-end", "root");
        State endState = new State("end", fsmClass);
        makeQuitArc(endState);
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("start-end"));
    }

    /**
     * Makes a chat fsm class object.
     *
     * 1. If we are in the start state and get a disambiguate-term-query performative,
     * transition to the start state and perform the do-disambiguate-term-query action. <br>
     */
    public void makeChatFsmClass () {
        FsmClass fsmClass = FsmClass.makeSubClass("chat", "root");

        Performative disambiguateTermQueryPerformative =
            new Performative("disambiguate-term-query");
        Fsm disambiguateTermQuery = new Fsm ("disambiguate-term-query");

        /**
          * 1. If we are in the start state and get a disambiguate-term-query performative,
          * transition to the start state and perform the do-disambiguate-term-query action.
         */
        new Arc(fsmClass.getState("start"),
                disambiguateTermQueryPerformative,
                fsmClass.getState("start"),
                disambiguateTermQuery,
                null);
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("chat"));
    }

    /**
      * Makes a "chat" Fsm.
      */
    public void makeChat () {
        Fsm fsm = FsmClass.makeInstance("chat", "chat");
        fsm.validateIntegrity();
        fsmStore.put(fsm.name, fsm);
        if (verbosity > 2)
            Log.current.println("made fsm\n" + getFsm("chat"));
    }

    /**
     * Makes a "disambiguate-term-query" Fsm class object.  <br>
     * Input "disambiguation words" --> ArrayList disambiguationWords <br>
     *
     * 1. If we are in the start state and get a start-new-fsm performative,
     * transition to the disambiguate-phrase state and perform the
     * disambiguate-phrase sub fsm. <br>
     *
     * 2. If we are in the disambiguate-phrase state and get a resume-previous-fsm
     * performative, transition to the term-query state and perform the
     * term-query sub fsm. <br>
     *
     * 3. If we are in the term-query state and get a resume-previous-fsm performative,
     * transition to the end state and perform the
     * end-sub-fsm action. <br>
     */
    public void makeDisambiguateTermQueryFsmClass () {
        FsmClass fsmClass = FsmClass.makeSubClass("disambiguate-term-query", "start-end");

        State disambiguatePhraseState = new State("disambiguate-phrase", fsmClass);
        State termQueryState = new State("term-query-phrase", fsmClass);

        /**
         * 1. If we are in the start state and get a start-new-fsm performative,
         * transition to the disambiguate-phrase state and perform the
         * disambiguate-phrase sub fsm.
         */
        new Arc(fsmClass.getState("start"),
                new Performative("start"),
                disambiguatePhraseState,
                new Fsm("disambiguate-phrase"),
                null);

        /**
         * 2. If we are in the disambiguate-phrase state and get a resume-previous-fsm
         * performative, transition to the term-query state and perform the
         * term-query sub fsm.
         */
        new Arc(disambiguatePhraseState,
                new Performative("resume-previous-fsm"),
                termQueryState,
                new Fsm("term-query"),
                null);

        /**
         * 3. If we are in the term-query state and get a resume-previous-fsm performative,
         * transition to the end state and perform the
         * end-sub-fsm action.
         */
        new Arc(termQueryState,
                new Performative("resume-previous-fsm"),
                fsmClass.getState("end"),
                new Fsm("end-sub-fsm"),
                null);
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("disambiguate-term-query"));
    }

    /**
      * Makes a "disambiguate-term-query" Fsm.
      */
    public void makeDisambiguateTermQuery () {
        Fsm fsm = FsmClass.makeInstance("disambiguate-term-query", "disambiguate-term-query");
        fsm.validateIntegrity();
        fsmStore.put(fsm.name, fsm);
        if (verbosity > 2)
            Log.current.println("made fsm\n" + getFsm("disambiguate-term-query"));
    }

    /**
     * Makes a "disambiguate-phrase" Fsm class.  <br>
     * Input "disambiguation words" --> ArrayList disambiguationWords <br>
     * Output "disambiguated term" --> CycFort disambiguatedTerm <br>
     *
     * 1. If we are in the start state and get a start performative,
     * transition to the disambiguate-phrase state and perform the
     * do-disambiguate-parse-phase action. <br>
     *
     * 2. If we are in the disambiguate-phrase state and get a term-match performative,
     * transition to the end state and perform the do-end-sub-fsm action. <br>
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
     * transition to the end state and perform the do-end-sub-fsm action. <br>
     */
    public void makeDisambiguatePhraseFsmClass () {
        FsmClass fsmClass = FsmClass.makeSubClass("disambiguate-phrase", "start-end");

        State disambiguatePhraseState = new State("disambiguate-phrase", fsmClass);
        State termChoiceState = new State("term-choice", fsmClass);

        /**
         * 1. If we are in the start state and get a start performative,
         * transition to the disambiguate-phrase state and perform the
         * do-disambiguate-parse-phrase action.
         */
        new Arc(fsmClass.getState("start"),
                new Performative("start"),
                disambiguatePhraseState,
                null,
                new Action("do-disambiguate-parse-phrase"));

        /**
         * 2. If we are in the disambiguate-phrase state and get a term-match performative,
         * transition to the end state and perform the do-end-sub-fsm action.
         */
        new Arc(disambiguatePhraseState,
                new Performative("term-match"),
                fsmClass.getState("end"),
                null,
                new Action("do-end-sub-fsm"));

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
         * transition to the end state and perform the do-end-sub-fsm action.
         */
        new Arc(termChoiceState,
                new Performative("end"),
                fsmClass.getState("end"),
                null,
                new Action("do-end-sub-fsm"));
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("disambiguate-phrase"));
    }

    /**
      * Makes a "disambiguate-phrase" Fsm.
      */
    public void makeDisambiguatePhrase () {
        Fsm fsm = FsmClass.makeInstance("disambiguate-phrase", "disambiguate-phrase");
        fsm.validateIntegrity();
        fsmStore.put(fsm.name, fsm);
        if (verbosity > 2)
            Log.current.println("made fsm\n" + getFsm("disambiguate-phrase"));
    }

    /**
     * Makes a "term-query" Fsm class. <br>
     *
     * input "disambiguated term" --> CycFort disambiguatedTerm <br>
     *
     * 1. If we are in the start state and get a term-query performative, transition to the
     * retrieve-first-fact state and perform the do-reply-with-first-fact action. <br>
     *
     * 2. If we are in the retrieve-first-fact state and get a more performative, transition to the
     * prompt-for-more state and perform the do-reply-with-next-fact action. <br>
     *
     * 3. If we are in the prompt-for-more state and get a done performative, transition to the end
     * state and perform the do-end-sub-fsm action.
     */
    public void makeTermQueryFsmClass () {
        FsmClass fsmClass = FsmClass.makeSubClass("term-query", "start-end");

        State retrieveFactState = new State("retrieve-fact", fsmClass);
        State promptForMoreState = new State("prompt-for-more", fsmClass);

        /**
         * 1. If we are in the start state and get a term-query performative, transition to the
         * retrieve-first-fact state and perform the do-reply-with-first-fact action.
         */
        Arc arc1 =
        new Arc(fsmClass.getState("start"),
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
         * state and perform the do-end-sub-fsm action.
         */
        new Arc(promptForMoreState,
                new Performative("done"),
                fsmClass.getState("end"),
                null,
                new Action("do-end-sub-fsm"));
        fsmClass.validateIntegrity();
        fsmClassStore.put(fsmClass.name, fsmClass);
        if (verbosity > 2)
            Log.current.println("made fsm class\n" + getFsmClass("term-query"));
    }

    /**
      * Makes a "term-query" Fsm.
      */
    public void makeTermQuery () {
        Fsm fsm = FsmClass.makeInstance("term-query", "term-query");
        fsm.validateIntegrity();
        fsmStore.put(fsm.name, fsm);
        if (verbosity > 2)
            Log.current.println("made fsm\n" + getFsm("term-query"));
    }

    /**
     * Fixes up the sub fsm forward references.
     */
    protected void fixupSubFsmForwardReferences () {
        Iterator fsms = fsmStore.values().iterator();
        ArrayList fsmsList = new ArrayList();
        while (fsms.hasNext())
            fsmsList.add(fsms.next());
        for (int j = 0; j < fsmsList.size(); j++) {
            ArrayList statesList = new ArrayList();
            Fsm fsm = (Fsm) fsmsList.get(j);
            Iterator states = fsm.fsmStates.values().iterator();
            while (states.hasNext())
                statesList.add(states.next());
            for (int i = 0; i < statesList.size(); i++) {
                State state = (State) statesList.get(i);
                Iterator arcs = state.getArcs().iterator();
                while (arcs.hasNext()) {
                    Arc arc = (Arc) arcs.next();
                    Fsm subFsm = arc.getSubFsm();
                    if ((arc.getSubFsm() != null) &&
                        (! fsmsList.contains(subFsm))) {
                        arc.setSubFsm(this.getFsm(subFsm.getName()));
                    }
                }
            }
        }
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
