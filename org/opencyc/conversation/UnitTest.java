package org.opencyc.conversation;

import junit.framework.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.fsm</tt> package<p>
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
public class UnitTest extends TestCase {

    /**
     * Creates a <tt>UnitTest</tt> object with the given name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Returns the test suite.
     *
     * @return the test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();
        testSuite.addTest(new UnitTest("testAction"));
        testSuite.addTest(new UnitTest("testPerformative"));
        testSuite.addTest(new UnitTest("testState"));
        testSuite.addTest(new UnitTest("testArc"));
        testSuite.addTest(new UnitTest("testFsmFactory"));
        testSuite.addTest(new UnitTest("testInterpreter"));
        testSuite.addTest(new UnitTest("testPerformer"));
        return testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit GUI.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests the Action object.
     */
    public void testAction () {
        System.out.println("\n**** testAction ****");
        String name = "my-action";
        Action action = new Action(name);
        Assert.assertNotNull(action);
        Assert.assertEquals(name, action.getName());
        Assert.assertEquals(name, action.toString());
        Action action2 = new Action(name);
        Assert.assertEquals(action, action2);

        System.out.println("**** testAction OK ****");
    }

    /**
     * Tests the Performative object.
     */
    public void testPerformative () {
        System.out.println("\n**** testPerformative ****");
        String performativeName = "my-performative";
        Performative performative =
            new Performative(performativeName);
        Assert.assertNotNull(performative);
        Assert.assertEquals(performativeName, performative.getPerformativeName());
        Assert.assertEquals(performativeName, performative.toString());
        Performative performative2 =
            new Performative(performativeName);
        Assert.assertEquals(performative, performative2);
        performative.setContent(new CycList());
        Assert.assertTrue(! performative.equals(performative2));
        performative2.setContent(new CycList());
        Assert.assertTrue(performative.equals(performative2));

        ArrayList performatives = new ArrayList();
        performatives.add(performative);

        performative2.performativeName = "a-performative";
        performatives.add(performative2);
        Collections.sort(performatives);
        Assert.assertEquals(2, performatives.size());
        Assert.assertEquals(performative2, performatives.get(0));
        Assert.assertEquals(performative, performatives.get(1));

        System.out.println("**** testPerformative OK ****");
    }

    /**
     * Tests the State object.
     */
    public void testState () {
        System.out.println("\n**** testState ****");

        State readyState = new State("ready");
        Assert.assertNotNull(readyState);
        Assert.assertEquals("ready", readyState.getStateId());
        State finalState = new State("final");
        Assert.assertEquals("final", finalState.getStateId());

        Action doTermQueryAction = new Action("do-term-query");
        Action doFinalizationAction = new Action("do-finalization");
        Performative termQueryPerformative = new Performative("term-query");
        Performative quitPerformative = new Performative("quit");
        /**
         * 1. If we are in the ready state and get a term-query performative, transition
         * to the ready state and perform the do-term-query action.
         */
        Arc arc1 = new Arc(readyState,
                           termQueryPerformative,
                           readyState,
                           null,
                           doTermQueryAction);
        Assert.assertNotNull(readyState.arcs);
        Assert.assertEquals(1, readyState.getArcs().size());
        Assert.assertTrue(readyState.getArcs().contains(arc1));
        Assert.assertEquals(arc1, readyState.getArc(termQueryPerformative));
        /**
         * 2. If we are in the ready state and get a quit performative, transition
         * to the final state and perform the do-finalization action.
         */
        Arc arc2 = new Arc(readyState,
                           quitPerformative,
                           finalState,
                           null,
                           doFinalizationAction);
        Assert.assertEquals(2, readyState.getArcs().size());
        Assert.assertTrue(readyState.getArcs().contains(arc1));
        Assert.assertTrue(readyState.getArcs().contains(arc2));
        Assert.assertEquals(arc1, readyState.getArc(termQueryPerformative));
        Assert.assertEquals(arc2, readyState.getArc(quitPerformative));

        ArrayList states = new ArrayList();
        states.add(readyState);
        states.add(finalState);
        Collections.sort(states);
        Assert.assertEquals(2, states.size());
        Assert.assertEquals(finalState, states.get(0));
        Assert.assertEquals(readyState, states.get(1));

        System.out.println("**** testState OK ****");
    }

    /**
     * Tests the Arc object.
     */
    public void testArc () {
        System.out.println("\n**** testArc ****");

        State readyState = new State("ready");
        State finalState = new State("final");
        Action doTermQueryAction = new Action("do-term-query");
        Action doFinalizationAction = new Action("do-finalization");
        Performative termQueryPerformative = new Performative("term-query");
        Performative quitPerformative = new Performative("quit");
        /**
         * 1. If we are in the ready state and get a term-query performative, transition
         * to the ready state and perform the do-term-query action.
         */
        Arc arc1 = new Arc(readyState,
                           termQueryPerformative,
                           readyState,
                           null,
                           doTermQueryAction);
        Assert.assertNotNull(arc1);
        Assert.assertEquals(readyState, arc1.transitionFromState);
        Assert.assertEquals(termQueryPerformative, arc1.getPerformative());
        Assert.assertEquals(readyState, arc1.getTransitionToState());
        Assert.assertEquals(doTermQueryAction, arc1.getAction());
        Arc arc1Clone = new Arc(readyState,
                               termQueryPerformative,
                               readyState,
                               null,
                               doTermQueryAction);
        Assert.assertEquals(arc1, arc1Clone);
        Assert.assertEquals("[ready, term-query, null, do-term-query, ready]", arc1.toString());
        /**
         * 2. If we are in the ready state and get a quit performative, transition
         * to the final state and perform the do-finalization action.
         */
        Arc arc2 = new Arc(readyState,
                          quitPerformative,
                          finalState,
                          null,
                          doFinalizationAction);
        Assert.assertNotNull(arc2);
        Assert.assertEquals(readyState, arc2.transitionFromState);
        Assert.assertEquals(quitPerformative, arc2.getPerformative());
        Assert.assertEquals(finalState, arc2.getTransitionToState());
        Assert.assertEquals(doFinalizationAction, arc2.getAction());
        Assert.assertTrue(! arc1.equals(arc2));
        Assert.assertEquals("[ready, quit, null, do-finalization, final]", arc2.toString());
        ArrayList arcs = new ArrayList();
        arcs.add(arc1);
        arcs.add(arc2);
        Collections.sort(arcs);
        Assert.assertEquals(2, arcs.size());
        Assert.assertEquals(arc2, arcs.get(0));
        Assert.assertEquals(arc1, arcs.get(1));

        System.out.println("**** testArc OK ****");
    }

    /**
     * Tests the FsmFactory object.
     */
    public void testFsmFactory () {
        System.out.println("\n**** testFsmFactory ****");
        FsmFactory.reset();
        FsmFactory fsmFactory = new FsmFactory();
        fsmFactory.makeAllFsmClasses();
        fsmFactory.makeAllFsms();
        Iterator fsmClasses = fsmFactory.fsmClassStore.values().iterator();
        ArrayList fsmClassesList = new ArrayList();
        while (fsmClasses.hasNext())
            fsmClassesList.add(fsmClasses.next());
        for (int j = 0; j < fsmClassesList.size(); j++) {
            ArrayList statesList = new ArrayList();
            FsmClass fsmClass = (FsmClass) fsmClassesList.get(j);
            Iterator states = fsmClass.fsmStates.values().iterator();
            while (states.hasNext())
                statesList.add(states.next());
            // all states have arcs from them.
            Assert.assertTrue(statesList.size() > 0);
            for (int i = 0; i < statesList.size(); i++) {
                State state = (State) statesList.get(i);
                Iterator arcs = state.getArcs().iterator();
                while (arcs.hasNext()) {
                    Arc arc = (Arc) arcs.next();
                    // Each transitionFromState is a state in this fsm
                    Assert.assertTrue(statesList.contains(arc.getTransitionFromState()));
                    // Each transitionToState is a state in this fsm
                    Assert.assertTrue(statesList.contains(arc.getTransitionToState()));
                    Fsm subFsm = arc.getSubFsm();
                    // No arc has both an action and a subFsm.
                    Assert.assertTrue(arc.toString(),
                                      ! ((arc.getAction() != null) &&
                                         (subFsm != null)));
                    if (arc.getSubFsm() != null) {
                        // When specified, the sub fsm is a valid fsm
                        Assert.assertTrue(fsmClassesList.contains(subFsm));
                    }
                }
            }
        }
        Iterator fsms = fsmFactory.fsmStore.values().iterator();
        ArrayList fsmsList = new ArrayList();
        while (fsms.hasNext())
            fsmsList.add(fsms.next());
        for (int j = 0; j < fsmsList.size(); j++) {
            ArrayList statesList = new ArrayList();
            Fsm fsm = (Fsm) fsmsList.get(j);
            Iterator states = fsm.fsmStates.values().iterator();
            while (states.hasNext())
                statesList.add(states.next());
            // all states have arcs from them.
            Assert.assertTrue(statesList.size() > 0);
            for (int i = 0; i < statesList.size(); i++) {
                State state = (State) statesList.get(i);
                Iterator arcs = state.getArcs().iterator();
                while (arcs.hasNext()) {
                    Arc arc = (Arc) arcs.next();
                    // Each transitionFromState is a state in this fsm
                    Assert.assertTrue(statesList.contains(arc.getTransitionFromState()));
                    // Each transitionToState is a state in this fsm
                    Assert.assertTrue(statesList.contains(arc.getTransitionToState()));
                    Fsm subFsm = arc.getSubFsm();
                    // No arc has both an action and a subFsm.
                    Assert.assertTrue(arc.toString(),
                                      ! ((arc.getAction() != null) &&
                                         (subFsm != null)));
                    if (arc.getSubFsm() != null) {
                        // When specified, the sub fsm is a valid fsm
                        Assert.assertTrue(fsmsList.contains(subFsm));
                    }
                }
            }
        }

        System.out.println("**** testFsmFactory OK ****");
    }

    /**
     * Tests the Interpreter object.
     */
    public void testInterpreter () {
        System.out.println("\n**** testInterpreter ****");

        FsmFactory.reset();
        FsmFactory fsmFactory = new FsmFactory();
        fsmFactory.initialize();
        Fsm chat = fsmFactory.getFsm("chat");
        Interpreter interpreter = new Interpreter();
        interpreter.initialize("initial", chat);
        Assert.assertNotNull(interpreter);
        Assert.assertNotNull(interpreter.fsmStacks);
        Assert.assertNotNull(interpreter.fsmStacks.get("initial"));
        Assert.assertTrue(interpreter.fsmStacks.get("initial") instanceof StackWithPointer);
        StackWithPointer fsmStack =
            (StackWithPointer) interpreter.fsmStacks.get("initial");
        Assert.assertEquals(1, fsmStack.size());
        Assert.assertTrue(fsmStack.peek() instanceof FsmStateInfo);
        FsmStateInfo fsmStateInfo =
            (FsmStateInfo) fsmStack.peek();
        Assert.assertEquals(chat, fsmStateInfo.getFsm());
        Assert.assertNull(fsmStateInfo.getCurrentState());
        Assert.assertEquals("ready", interpreter.currentState.getStateId());
        String chatMessage = "xxxx";
        ParseResults parseResults = interpreter.templateParser.parse(chatMessage);
        Assert.assertTrue(! parseResults.isCompleteParse);
        Performative performative = parseResults.getPerformative();
        Assert.assertEquals("not-understand", performative.getPerformativeName());
        Arc arc = interpreter.lookupArc(performative);
        interpreter.transitionState(arc);
        Assert.assertEquals("ready", interpreter.currentState.getStateId());

        interpreter.initialize("initial", chat);
        Assert.assertEquals("ready", interpreter.currentState.getStateId());
        chatMessage = "quit";
        parseResults = interpreter.templateParser.parse(chatMessage);
        Assert.assertTrue(parseResults.isCompleteParse);
        performative = parseResults.getPerformative();
        Assert.assertEquals("quit", performative.getPerformativeName());
        arc = interpreter.lookupArc(performative);
        interpreter.transitionState(arc);
        Assert.assertEquals("final", interpreter.currentState.getStateId());

        interpreter.initialize("initial", chat);
        fsmStack =
            (StackWithPointer) interpreter.fsmStacks.get("initial");
        Assert.assertEquals(1, fsmStack.size());
        Assert.assertTrue(fsmStack.peek() instanceof FsmStateInfo);
        fsmStateInfo =
            (FsmStateInfo) fsmStack.peek();
        Assert.assertEquals(chat, fsmStateInfo.getFsm());
        Assert.assertEquals("ready", interpreter.currentState.getStateId());
        chatMessage = "what do you know about penguins?";
        parseResults = interpreter.templateParser.parse(chatMessage);
        Assert.assertTrue(parseResults.isCompleteParse);
        performative = parseResults.getPerformative();
        Assert.assertEquals("disambiguate-term-query", performative.getPerformativeName());
        arc = interpreter.lookupArc(performative);
        interpreter.transitionState(arc);
        Assert.assertEquals("ready", interpreter.currentState.getStateId());
        ArrayList expectedTextBinding = new ArrayList();
        expectedTextBinding.add("penguins");
        ArrayList actualTextBinding =
            parseResults.getTextBinding(CycObjectFactory.makeCycVariable("term"));
        Assert.assertEquals(expectedTextBinding, actualTextBinding);
        Fsm disambiguatePhrase = fsmFactory.getFsm("disambiguate-phrase");
        CycList disambiguationWords = new CycList();
        disambiguationWords.add("penguins");
        Object [] attributeValuePair = {"disambiguation words", disambiguationWords};
        ArrayList arguments = new ArrayList();
        arguments.add(attributeValuePair);
        interpreter.setupSubFsm(disambiguatePhrase, arguments);
        Assert.assertEquals("ready", fsmStateInfo.getCurrentState().getStateId());
        Assert.assertEquals(2, fsmStack.size());
        Assert.assertTrue(fsmStack.peek() instanceof FsmStateInfo);
        fsmStateInfo = (FsmStateInfo) fsmStack.peek();
        Assert.assertNull(fsmStateInfo.getCurrentState());
        Assert.assertEquals(disambiguatePhrase, fsmStateInfo.getFsm());
        Assert.assertNotNull(interpreter.nextPerformative);

        System.out.println("**** testInterpreter OK ****");
    }

    /**
     * Tests the Performer object.
     */
    public void testPerformer () {
        System.out.println("\n**** testPerformer ****");

        Interpreter interpreter = new Interpreter();
        Performer performer = new Performer(interpreter);

        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
            //System.out.println("CycAccess initialized");
            CycList terms = performer.parseTermsString("penguins");
            // #$Penguin
            Assert.assertTrue(terms.contains(cycAccess.getKnownConstantByGuid("bd58a986-9c29-11b1-9dad-c379636f7270")));
            // #$PittsburghPenguins
            Assert.assertTrue(terms.contains(cycAccess.getKnownConstantByGuid("c08dec11-9c29-11b1-9dad-c379636f7270")));

            CycList words = new CycList();
            words.add("penguins");
            String disambiguationPhrase = StringUtils.wordsToPhrase(words);
            terms = performer.parseTermsString(disambiguationPhrase);
            // #$Penguin
            Assert.assertTrue(terms.contains(cycAccess.getKnownConstantByGuid("bd58a986-9c29-11b1-9dad-c379636f7270")));
            // #$PittsburghPenguins
            Assert.assertTrue(terms.contains(cycAccess.getKnownConstantByGuid("c08dec11-9c29-11b1-9dad-c379636f7270")));

        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testPerformer OK ****");
    }


}