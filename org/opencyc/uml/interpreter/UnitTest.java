package  org.opencyc.uml.interpreter;

import  java.util.*;
import  java.net.*;
import  java.io.*;
import  junit.framework.*;
import  koala.dynamicjava.interpreter.*;
import  koala.dynamicjava.parser.wrapper.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;
import  org.opencyc.uml.core.*;
import  org.opencyc.uml.action.*;
import  org.opencyc.uml.commonbehavior.*;
import  org.opencyc.uml.datatypes.Multiplicity;
import  org.opencyc.uml.statemachine.*;


/**
 * Provides a unit test suite for the <tt>org.opencyc.uml.interpreter</tt> package<p>
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
    public UnitTest (String name) {
        super(name);
    }

    /**
     * Returns the test suite.
     *
     * @return the test suite
     */
    public static Test suite () {
        TestSuite testSuite = new TestSuite();
        //testSuite.addTest(new UnitTest("testJavaInterpreter"));
        //testSuite.addTest(new UnitTest("testExpressionEvaluation"));
        //testSuite.addTest(new UnitTest("testContextFrames"));
        testSuite.addTest(new UnitTest("testProcedureInterpretation"));
        //testSuite.addTest(new UnitTest("testSimpleStateMachine"));
        //testSuite.addTest(new UnitTest("testCycExtractor"));
        return  testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests java interpreter, which is no longer used for expression evaluation.
     */
    public void testJavaInterpreter () {
        System.out.println("\n**** testJavaInterpreter ****");
        // Create the interpreter. It will use the default JavaCC parser.
        TreeInterpreter interpreter = new TreeInterpreter(new JavaCCParserFactory());
        Integer integer1 = new Integer(1);
        interpreter.defineVariable("integer1", integer1);
        StringReader stringReader = new StringReader("integer1.intValue() + 1;");
        Object result = interpreter.interpret(stringReader, "statement");
        Assert.assertTrue(result instanceof Integer);
        Assert.assertEquals(new Integer(2), result);
        StringBuffer statements = new StringBuffer();
        statements.append("String testString;\n");
        statements.append("testString = \"abcdef\";\n");
        statements.append("testString.substring(3);\n");
        result = interpreter.interpret(new StringReader(statements.toString()),
                "statements");
        Assert.assertTrue(result instanceof String);
        Assert.assertEquals("def", result);
        statements = new StringBuffer();
        statements.append("String testString2;\n");
        statements.append("testString2 = testString + \"1234\";\n");
        statements.append("testString2.startsWith(\"abc\");\n");
        result = interpreter.interpret(new StringReader(statements.toString()),
                "statements");
        Assert.assertTrue(result instanceof Boolean);
        Assert.assertEquals(Boolean.TRUE, result);
        System.out.println("\n**** testJavaInterpreter OK ****");
    }

    /**
     * Tests expression evaluation.
     */
    public void testExpressionEvaluation () {
        System.out.println("\n**** testExpressionEvaluation ****");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost", 3620, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                //cycAccess.traceNamesOn();
                cycAccess.setKePurpose("DAMLProject");
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost", 3600, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                cycAccess.setKePurpose("OpenCyc");
            }
            else {
                cycAccess = new CycAccess();
                cycAccess.setKePurpose("OpenCyc");
            }
            cycAccess.setCyclist(cycAccess.getKnownConstantByName("Cyc"));
            CycFort stateMt = cycAccess.getKnownConstantByName("UMLStateMachineInterpreter-TemporaryWorkspaceMt");
            cycAccess.unassertMtContentsWithoutTranscript(stateMt);
            Assert.assertEquals(0, cycAccess.getAllAssertionsInMt(stateMt).size());
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(cycAccess,
                    ExpressionEvaluator.QUIET_VERBOSITY);
            Expression expression = new Expression();
            String expressionText = "(#$ProgramAssignmentFn #$TestStateMachine-X 0)";
            expression.setBody(cycAccess.makeCycList(expressionText));
            expressionEvaluator.evaluate(expression, stateMt);
            String queryText = "(#$softwareParameterValue #$TestStateMachine-X 0)";
            CycList query = cycAccess.makeCycList(queryText);
            Assert.assertTrue(cycAccess.isQueryTrue(query, stateMt));
            Assert.assertEquals(1, cycAccess.getAllAssertionsInMt(stateMt).size());
            queryText = "(#$evaluate ?X (#$PlusFn 1 2))";
            query = cycAccess.makeCycList(queryText);
            CycVariable variable = CycObjectFactory.makeCycVariable("?x");
            Object answer = cycAccess.askWithVariable(query, variable, stateMt);
            Assert.assertTrue(answer instanceof CycList);
            Assert.assertEquals(1, ((CycList)answer).size());
            answer = ((CycList)answer).first();
            Assert.assertTrue(answer instanceof Integer);
            Assert.assertEquals(3, ((Integer)answer).intValue());
            queryText = "(#$evaluate ?X (#$GetParameterValueFn #$TestStateMachine-X))";
            query = cycAccess.makeCycList(queryText);
            answer = cycAccess.askWithVariable(query, variable, stateMt);
            Assert.assertTrue(answer instanceof CycList);
            Assert.assertEquals(1, ((CycList)answer).size());
            answer = ((CycList)answer).first();
            Assert.assertTrue(answer instanceof Integer);
            Assert.assertEquals(0, ((Integer)answer).intValue());
            cycAccess.unassertMtContentsWithoutTranscript(stateMt);
            Assert.assertEquals(0, cycAccess.getAllAssertionsInMt(stateMt).size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("\n**** testExpressionEvaluation ****");
    }

    /**
     * Tests creation and destruction of evaluation context frames.
     */
    public void testContextFrames () {
        System.out.println("\n**** testContextFrames ****");
        Log.makeLog("unit-test.log");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost", 3620, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                //cycAccess.traceNamesOn();
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost", 3600, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
            }
            else
                cycAccess = new CycAccess();
            ContextStackPool contextStackPool = new ContextStackPool(cycAccess,
                    cycAccess.getKnownConstantByName("UMLStateMachineInterpreter-TemporaryWorkspaceMt"),
                    ContextStackPool.QUIET_VERBOSITY);
            Assert.assertEquals(ContextStackPool.DEFAULT_CONTEXT_FRAMES_COUNT, contextStackPool.contextFrames.size());
            CycConstant definitionMt = cycAccess.getKnownConstantByName("UMLStateMachineTest01Mt");
            CycConstant contextFrame1 = contextStackPool.allocateContextFrame(null,
                    definitionMt);
            CycConstant contextFrame2 = contextStackPool.allocateContextFrame(contextFrame1,
                    definitionMt);
            CycConstant contextFrame3 = contextStackPool.allocateContextFrame(contextFrame2,
                    definitionMt);
            CycConstant contextFrame4 = contextStackPool.allocateContextFrame(contextFrame3,
                    definitionMt);
            CycConstant contextFrame5 = contextStackPool.allocateContextFrame(contextFrame4,
                    definitionMt);
            CycConstant contextFrame6 = contextStackPool.allocateContextFrame(contextFrame5,
                    definitionMt);
            CycConstant contextFrame7 = contextStackPool.allocateContextFrame(contextFrame6,
                    definitionMt);
            CycConstant contextFrame8 = contextStackPool.allocateContextFrame(contextFrame7,
                    definitionMt);
            CycConstant contextFrame9 = contextStackPool.allocateContextFrame(contextFrame8,
                    definitionMt);
            CycConstant contextFrame10 = contextStackPool.allocateContextFrame(contextFrame9,
                    definitionMt);
            Assert.assertEquals(contextFrame9, contextStackPool.getParentContextFrame(contextFrame10));
            contextStackPool.deallocateContextFrame(contextFrame10);
            Assert.assertEquals(contextFrame10, contextStackPool.allocateContextFrame(contextFrame9,
                    definitionMt));
            contextStackPool.destroyContextStack();
            Assert.assertNull(contextStackPool.contextFrames);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("\n**** testContextFrames ****");
    }

    /**
     * Tests procedure interpretation.
     */
    public void testProcedureInterpretation () {
        System.out.println("\n**** testProcedureInterpretation ****");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost", 3620, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                //cycAccess.traceNamesOn();
                cycAccess.setKePurpose("DAMLProject");
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost", 3600, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                cycAccess.setKePurpose("OpenCyc");
            }
            else {
                cycAccess = new CycAccess();
                cycAccess.setKePurpose("OpenCyc");
            }
            //cycAccess.traceNamesOn();
            cycAccess.setCyclist(cycAccess.getKnownConstantByName("Cyc"));
            CycFort definitionMt = cycAccess.getKnownConstantByName("UMLStateMachineTest01Mt");
            ContextStackPool contextStackPool = new ContextStackPool(cycAccess,
                    cycAccess.getKnownConstantByName("UMLStateMachineInterpreter-TemporaryWorkspaceMt"),
                                                     ContextStackPool.QUIET_VERBOSITY);
            CycConstant stateMt = contextStackPool.allocateContextFrame(null, definitionMt);
            Assert.assertEquals(0, cycAccess.getAllAssertionsInMt(stateMt).size());
            ProcedureInterpreter procedureInterpreter =
                    new ProcedureInterpreter(cycAccess,
                                             definitionMt,
                                             contextStackPool,
                                             Interpreter.DEFAULT_VERBOSITY);
            Procedure initializeProcedure = new Procedure();
            initializeProcedure.setName("UMLProcedure-InitializeNumberToZero");
            Object procedureBody = cycAccess.getArg2("umlBody", "UMLProcedure-InitializeNumberToZero",
                    "UMLProcedureDefinition-InitializeNumberToZeroMt");
            initializeProcedure.setBody(procedureBody);
            Transition transition1 = new Transition();
            transition1.setName("TestStateMachine-Transition1");
            transition1.setEffect(initializeProcedure);
            ArrayList inputBindings = new ArrayList();
            ArrayList outputBindings = new ArrayList();
            OutputBinding outputBinding = new OutputBinding();
            OutputPin outputPin = new OutputPin();
            outputPin.setName("UMLProcedure-InitializeNumberToZero-OutputPin-X");
            outputBinding.setBoundOutputPin(outputPin);
            StateVariable stateVariable = new StateVariable();
            stateVariable.setName("TestStateMachine-X");
            outputBinding.setBoundOutputStateVariable(stateVariable);
            outputBindings.add(outputBinding);
            procedureInterpreter.interpretProcedure(initializeProcedure,
                                                    inputBindings,
                                                    outputBindings);
            contextStackPool.destroyContextStack();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("\n**** testProcedureInterpretation ****");
    }

    /**
     * Tests simple state machine.
     */
    public void testSimpleStateMachine () {
        System.out.println("\n**** testSimpleStateMachine ****");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost", 3620, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                //cycAccess.traceNamesOn();
                cycAccess.setKePurpose("DAMLProject");
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost", 3600, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                cycAccess.setKePurpose("OpenCyc");
            }
            else {
                cycAccess = new CycAccess();
                cycAccess.setKePurpose("OpenCyc");
            }
            StateMachineFactory stateMachineFactory = new StateMachineFactory();
            //  state machine
            String namespaceName = "test namespace";
            String name = "TestStateMachine";
            String commentString = "This is the test comment for testStateMachine.";
            StateMachine stateMachine = stateMachineFactory.makeStateMachine(namespaceName,
                    name, commentString);
            Assert.assertTrue(stateMachine instanceof StateMachine);
            Assert.assertTrue(stateMachine.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, stateMachine.getNamespace().getName());
            Assert.assertTrue(stateMachine.getNamespace().getOwnedElement().contains(stateMachine));
            Assert.assertEquals(name, stateMachine.getName());
            Assert.assertTrue(stateMachine.getComment() instanceof Comment);
            Assert.assertEquals(commentString, stateMachine.getComment().getBody());
            Assert.assertEquals(stateMachine, stateMachine.getComment().getAnnotatedElement());
            CycConstant classifierTerm = cycAccess.getKnownConstantByName("TestStateMachineContext");
            stateMachineFactory.associateClassifierToStateMachine(classifierTerm.toString(),
                    commentString);
            Assert.assertNotNull(stateMachine.getContext());
            Assert.assertTrue(stateMachine.getContext() instanceof Classifier);
            Assert.assertEquals(classifierTerm.toString(), stateMachine.getContext().getName());
            name = "TestStateMachine-X";
            commentString = "test state machine counter variable";
            java.lang.Class type = null;
            try {
                type = java.lang.Class.forName("java.lang.String");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Assert.fail();
            }
            Expression initialValue = null;
            stateMachineFactory.addStateVariableToClassifier(name, commentString,
                    StructuralFeature.SK_INSTANCE, type, StructuralFeature.CK_CHANGEABLE,
                    new Multiplicity(1, 1, false), StructuralFeature.OK_UNORDERED,
                    initialValue);
            //  procedures
            name = "UMLProcedure-InitializeNumberToZero";
            commentString = "Initializes the variable to the value zero.";
            String language = "CycL";
            Object body = cycAccess.getArg2("umlBody", name, "UMLProcedureDefinition-InitializeNumberToZeroMt");
            boolean isList = false;
            Procedure initializeNumberToZero = stateMachineFactory.makeProcedure(name,
                    commentString, language, body, isList);
            Assert.assertTrue(initializeNumberToZero instanceof Procedure);
            Assert.assertTrue(initializeNumberToZero.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, initializeNumberToZero.getNamespace().getName());
            Assert.assertTrue(initializeNumberToZero.getNamespace().getOwnedElement().contains(initializeNumberToZero));
            Assert.assertEquals(name, initializeNumberToZero.getName());
            Assert.assertTrue(initializeNumberToZero.getComment() instanceof Comment);
            Assert.assertEquals(commentString, initializeNumberToZero.getComment().getBody());
            Assert.assertEquals(initializeNumberToZero, initializeNumberToZero.getComment().getAnnotatedElement());
            Assert.assertEquals(language, initializeNumberToZero.getLanguage());
            Assert.assertEquals(body, initializeNumberToZero.getBody());
            Assert.assertEquals(isList, initializeNumberToZero.isList());
            name = "x";
            commentString = "the variable X initialized to zero.";
            type = null;
            try {
                type = java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveInt");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Assert.fail();
            }
            OutputPin outputPinX1 = stateMachineFactory.addOutputPinToProcedure(name,
                    commentString, initializeNumberToZero, type);
            Assert.assertTrue(outputPinX1 instanceof OutputPin);
            Assert.assertTrue(outputPinX1.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, outputPinX1.getNamespace().getName());
            Assert.assertTrue(outputPinX1.getNamespace().getOwnedElement().contains(outputPinX1));
            Assert.assertEquals(name, outputPinX1.getName());
            Assert.assertTrue(outputPinX1.getComment() instanceof Comment);
            Assert.assertEquals(commentString, outputPinX1.getComment().getBody());
            Assert.assertEquals(outputPinX1, outputPinX1.getComment().getAnnotatedElement());
            Assert.assertTrue(initializeNumberToZero.getResult().contains(outputPinX1));
            Assert.assertEquals(initializeNumberToZero, outputPinX1.getProcedure());
            name = "UMLProcedure-IncrementNumber";
            commentString = "Increments the given number by one.";
            language = "CycL";
            body = cycAccess.getArg2("umlBody", name, "UMLProcedureDefinition-IncrementNumberMt");
            isList = false;
            Procedure increment = stateMachineFactory.makeProcedure(name, commentString,
                    language, body, isList);
            Assert.assertTrue(increment instanceof Procedure);
            Assert.assertTrue(increment.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, increment.getNamespace().getName());
            Assert.assertTrue(increment.getNamespace().getOwnedElement().contains(increment));
            Assert.assertEquals(name, increment.getName());
            Assert.assertTrue(increment.getComment() instanceof Comment);
            Assert.assertEquals(commentString, increment.getComment().getBody());
            Assert.assertEquals(increment, increment.getComment().getAnnotatedElement());
            Assert.assertEquals(language, increment.getLanguage());
            Assert.assertEquals(body, increment.getBody());
            Assert.assertEquals(isList, increment.isList());
            name = "x";
            commentString = "the given number to be incremented";
            InputPin inputPinX = stateMachineFactory.addInputPinToProcedure(name,
                    commentString, increment, type);
            Assert.assertTrue(inputPinX instanceof InputPin);
            Assert.assertTrue(inputPinX.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, inputPinX.getNamespace().getName());
            Assert.assertTrue(inputPinX.getNamespace().getOwnedElement().contains(inputPinX));
            Assert.assertEquals(name, inputPinX.getName());
            Assert.assertTrue(inputPinX.getComment() instanceof Comment);
            Assert.assertEquals(commentString, inputPinX.getComment().getBody());
            Assert.assertEquals(inputPinX, inputPinX.getComment().getAnnotatedElement());
            Assert.assertTrue(increment.getArgument().contains(inputPinX));
            Assert.assertEquals(increment, inputPinX.getProcedure());
            commentString = "the incremented number";
            OutputPin outputPinX = stateMachineFactory.addOutputPinToProcedure(name,
                    commentString, increment, type);
            Assert.assertTrue(outputPinX instanceof OutputPin);
            Assert.assertTrue(outputPinX.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, outputPinX.getNamespace().getName());
            Assert.assertTrue(outputPinX.getNamespace().getOwnedElement().contains(outputPinX));
            Assert.assertEquals(name, outputPinX.getName());
            Assert.assertTrue(outputPinX.getComment() instanceof Comment);
            Assert.assertEquals(commentString, outputPinX.getComment().getBody());
            Assert.assertEquals(outputPinX, outputPinX.getComment().getAnnotatedElement());
            Assert.assertTrue(increment.getResult().contains(outputPinX));
            Assert.assertEquals(increment, outputPinX.getProcedure());
            //  events (no events in this test)
            //  states
            name = "TestStateMachine-TopState";
            commentString = "Top state for the test state machine.";
            CompositeState container = null;
            Procedure entry = null;
            Procedure exit = null;
            Procedure doActivity = null;
            boolean isConcurrent = false;
            CompositeState topState = stateMachineFactory.makeCompositeState(name,
                    commentString, container, entry, exit, doActivity, isConcurrent);
            stateMachine.setTop(topState);
            Assert.assertTrue(topState instanceof CompositeState);
            Assert.assertTrue(topState.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, topState.getNamespace().getName());
            Assert.assertTrue(topState.getNamespace().getOwnedElement().contains(topState));
            Assert.assertEquals(name, topState.getName());
            Assert.assertTrue(topState.getComment() instanceof Comment);
            Assert.assertEquals(commentString, topState.getComment().getBody());
            Assert.assertEquals(topState, topState.getComment().getAnnotatedElement());
            Assert.assertEquals(container, topState.getContainer());
            Assert.assertEquals(entry, topState.getEntry());
            Assert.assertEquals(exit, topState.getExit());
            Assert.assertEquals(doActivity, topState.getDoActivity());
            Assert.assertEquals(isConcurrent, topState.isConcurrent());
            Assert.assertEquals(0, topState.getDeferrableEvent().size());
            Assert.assertTrue(!topState.isRegion());
            Assert.assertEquals(topState, stateMachine.getTop());
            name = "TestStateMachine-InitialState";
            commentString = "Initial state for the test state machine.";
            container = topState;
            int kind = PseudoState.PK_INITIAL;
            PseudoState initialState = stateMachineFactory.makePseudoState(name,
                    commentString, container, kind);
            Assert.assertTrue(initialState instanceof PseudoState);
            Assert.assertTrue(initialState.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, initialState.getNamespace().getName());
            Assert.assertTrue(initialState.getNamespace().getOwnedElement().contains(initialState));
            Assert.assertEquals(name, initialState.getName());
            Assert.assertTrue(initialState.getComment() instanceof Comment);
            Assert.assertEquals(commentString, initialState.getComment().getBody());
            Assert.assertEquals(initialState, initialState.getComment().getAnnotatedElement());
            Assert.assertEquals(container, initialState.getContainer());
            Assert.assertEquals(kind, initialState.getKind());
            Assert.assertTrue(topState.getSubVertex().contains(initialState));
            name = "TestStateMachine-CounterState";
            commentString = "Counter state for the test state machine.";
            SimpleState counterState = stateMachineFactory.makeSimpleState(name,
                    commentString, container, entry, exit, doActivity);
            Assert.assertTrue(counterState instanceof SimpleState);
            Assert.assertTrue(counterState.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, counterState.getNamespace().getName());
            Assert.assertTrue(counterState.getNamespace().getOwnedElement().contains(counterState));
            Assert.assertEquals(name, counterState.getName());
            Assert.assertTrue(counterState.getComment() instanceof Comment);
            Assert.assertEquals(commentString, counterState.getComment().getBody());
            Assert.assertEquals(counterState, counterState.getComment().getAnnotatedElement());
            Assert.assertEquals(container, counterState.getContainer());
            Assert.assertTrue(topState.getSubVertex().contains(counterState));
            Assert.assertEquals(entry, counterState.getEntry());
            Assert.assertEquals(exit, counterState.getExit());
            Assert.assertEquals(doActivity, counterState.getDoActivity());
            Assert.assertEquals(0, counterState.getDeferrableEvent().size());
            name = "TestStateMachine-FinalState";
            commentString = "Final state for the test state machine.";
            FinalState finalState = stateMachineFactory.makeFinalState(name, commentString,
                    container, entry, exit, doActivity);
            Assert.assertTrue(finalState instanceof FinalState);
            Assert.assertTrue(finalState.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, finalState.getNamespace().getName());
            Assert.assertTrue(finalState.getNamespace().getOwnedElement().contains(finalState));
            Assert.assertEquals(name, finalState.getName());
            Assert.assertTrue(finalState.getComment() instanceof Comment);
            Assert.assertEquals(commentString, finalState.getComment().getBody());
            Assert.assertEquals(finalState, finalState.getComment().getAnnotatedElement());
            Assert.assertEquals(container, finalState.getContainer());
            Assert.assertTrue(topState.getSubVertex().contains(finalState));
            Assert.assertEquals(entry, finalState.getEntry());
            Assert.assertEquals(exit, finalState.getExit());
            Assert.assertEquals(doActivity, finalState.getDoActivity());
            Assert.assertEquals(0, finalState.getDeferrableEvent().size());
            //  state vertices (no state vertices in this test)
            //  transistions
            name = "TestStateMachine-Transition1";
            commentString = "Transition 1 for the test state machine.";
            String guardExpressionLanguage = null;
            Object guardExpressionBody = null;
            Procedure effect = initializeNumberToZero;
            Event trigger = null;
            StateVertex source = initialState;
            StateVertex target = counterState;
            Transition transition1 = stateMachineFactory.makeTransition(name, commentString,
                    guardExpressionLanguage, guardExpressionBody, effect, trigger,
                    source, target);
            Assert.assertTrue(transition1 instanceof Transition);
            Assert.assertTrue(transition1.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, transition1.getNamespace().getName());
            Assert.assertTrue(transition1.getNamespace().getOwnedElement().contains(transition1));
            Assert.assertEquals(name, transition1.getName());
            Assert.assertTrue(transition1.getComment() instanceof Comment);
            Assert.assertEquals(commentString, transition1.getComment().getBody());
            Assert.assertEquals(transition1, transition1.getComment().getAnnotatedElement());
            Assert.assertEquals(effect, transition1.getEffect());
            Assert.assertNull(transition1.getGuard());
            Assert.assertEquals(source, transition1.getSource());
            Assert.assertEquals(target, transition1.getTarget());
            Assert.assertEquals(stateMachine, transition1.getStateMachine());
            Assert.assertEquals(trigger, transition1.getTrigger());
            Assert.assertTrue(!transition1.isSelfTransition());
            Assert.assertTrue(initialState.getOutgoing().contains(transition1));
            Assert.assertTrue(counterState.getIncoming().contains(transition1));
            name = "TestStateMachine-Transition2";
            commentString = "Transition 2 for the test state machine.";
            guardExpressionLanguage = "CycL";
            guardExpressionBody = cycAccess.getArg2("umlBody", "TestStateMachine-BooleanExpression1",
                    "UMLStateMachineTest01Mt");
            effect = increment;
            trigger = null;
            source = counterState;
            target = counterState;
            Transition transition2 = stateMachineFactory.makeTransition(name, commentString,
                    guardExpressionLanguage, guardExpressionBody, effect, trigger,
                    source, target);
            Assert.assertTrue(transition2 instanceof Transition);
            Assert.assertTrue(transition2.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, transition2.getNamespace().getName());
            Assert.assertTrue(transition2.getNamespace().getOwnedElement().contains(transition2));
            Assert.assertEquals(name, transition2.getName());
            Assert.assertTrue(transition2.getComment() instanceof Comment);
            Assert.assertEquals(commentString, transition2.getComment().getBody());
            Assert.assertEquals(transition2, transition2.getComment().getAnnotatedElement());
            Assert.assertEquals(effect, transition2.getEffect());
            Assert.assertNotNull(transition2.getGuard());
            Assert.assertTrue(transition2.getGuard() instanceof Guard);
            Assert.assertNotNull(transition2.getGuard().getexpression());
            Guard guard = transition2.getGuard();
            Assert.assertEquals(name, guard.getName());
            Assert.assertEquals(commentString, guard.getComment().getBody());
            Assert.assertEquals(transition2, guard.getTransition());
            Assert.assertTrue(guard.getNamespace().getOwnedElement().contains(guard));
            Assert.assertEquals(guardExpressionBody, guard.getexpression().getBody());
            Assert.assertEquals(source, transition2.getSource());
            Assert.assertEquals(target, transition2.getTarget());
            Assert.assertEquals(stateMachine, transition2.getStateMachine());
            Assert.assertEquals(trigger, transition2.getTrigger());
            Assert.assertTrue(transition2.isSelfTransition());
            Assert.assertTrue(((State)source).getInternalTransition().contains(transition2));
            name = "TestStateMachine-Transition3";
            commentString = "Transition 3 for the test state machine.";
            guardExpressionLanguage = "CycL";
            guardExpressionBody = cycAccess.getArg2("umlBody", "TestStateMachine-BooleanExpression2",
                    "UMLStateMachineTest01Mt");
            effect = null;
            trigger = null;
            source = counterState;
            target = finalState;
            Transition transition3 = stateMachineFactory.makeTransition(name, commentString,
                    guardExpressionLanguage, guardExpressionBody, effect, trigger,
                    source, target);
            Assert.assertTrue(transition3 instanceof Transition);
            Assert.assertTrue(transition3.getNamespace() instanceof Namespace);
            Assert.assertEquals(namespaceName, transition3.getNamespace().getName());
            Assert.assertTrue(transition3.getNamespace().getOwnedElement().contains(transition3));
            Assert.assertEquals(name, transition3.getName());
            Assert.assertTrue(transition3.getComment() instanceof Comment);
            Assert.assertEquals(commentString, transition3.getComment().getBody());
            Assert.assertEquals(transition3, transition3.getComment().getAnnotatedElement());
            Assert.assertEquals(effect, transition3.getEffect());
            Assert.assertNotNull(transition3.getGuard());
            Assert.assertTrue(transition3.getGuard() instanceof Guard);
            Assert.assertNotNull(transition3.getGuard().getexpression());
            guard = transition3.getGuard();
            Assert.assertEquals(name, guard.getName());
            Assert.assertEquals(commentString, guard.getComment().getBody());
            Assert.assertEquals(transition3, guard.getTransition());
            Assert.assertTrue(guard.getNamespace().getOwnedElement().contains(guard));
            Assert.assertEquals(guardExpressionBody, guard.getexpression().getBody());
            Assert.assertEquals(source, transition3.getSource());
            Assert.assertEquals(target, transition3.getTarget());
            Assert.assertEquals(stateMachine, transition3.getStateMachine());
            Assert.assertEquals(trigger, transition3.getTrigger());
            Assert.assertTrue(!transition3.isSelfTransition());
            Assert.assertTrue(source.getOutgoing().contains(transition3));
            Assert.assertTrue(target.getIncoming().contains(transition3));
            interpretStateMachine(stateMachine, cycAccess);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("\n**** testSimpleStateMachine ****");
    }

    /**
     * Tests the given instantiated state machine
     *
     * @param stateMachine the given instantiated state machine
     * @param cycAccess the given cyc server connection
     */
    protected void interpretStateMachine (StateMachine stateMachine, CycAccess cycAccess) {
        Interpreter interpreter = null;
        try {
            CycFort temporaryWorkspaceMt = cycAccess.getKnownConstantByName("UMLStateMachineInterpreter-TemporaryWorkspaceMt");
            int verbosity = Interpreter.QUIET_VERBOSITY;
            ContextStackPool contextStackPool = new ContextStackPool(cycAccess,
                    temporaryWorkspaceMt, ContextStackPool.QUIET_VERBOSITY);
            interpreter = new Interpreter(stateMachine, cycAccess, contextStackPool,
                    verbosity);
            Assert.assertNotNull(interpreter);
            Assert.assertTrue(interpreter instanceof Interpreter);
            Assert.assertTrue(interpreter.eventQueue.isEmpty());
            Assert.assertNull(interpreter.getCurrentEvent());
            Assert.assertEquals(stateMachine, interpreter.getStateMachine());
            interpreter.formAllStatesConfiguration();
            if (verbosity > 2)
                System.out.print(interpreter.displayAllStatesConfigurationTree());
            interpreter.formInitialStateConfiguration();
            Assert.assertEquals(new Integer(0), interpreter.getStateVariableValue("TestStateMachine-X"));
            if (verbosity > 2)
                System.out.print(interpreter.displayStateConfigurationTree());
            interpreter.eventDispatcher();
            Assert.assertNotNull(interpreter.getCurrentEvent());
            Assert.assertTrue(interpreter.getCurrentEvent() instanceof CompletionEvent);
            interpreter.eventProcessor();
            interpreter.fireSelectedTransitions();
            interpreter.getStateMachineFactory().destroyEvent(interpreter.getCurrentEvent());
            interpreter.currentEvent = null;
            Assert.assertNotNull(stateMachine.getTop());
            Assert.assertTrue(stateMachine.getTop() instanceof CompositeState);
            Assert.assertNotNull(stateMachine.getTop().getStateInterpreter());
            Assert.assertEquals(stateMachine.getTop(), stateMachine.getTop().getStateInterpreter().getState());
            Assert.assertEquals(new Integer(1), interpreter.getStateVariableValue("TestStateMachine-X"));
            if (verbosity > 2)
                System.out.print(interpreter.displayStateConfigurationTree());
            for (int i = 2; i < 10; i++) {
                interpreter.eventDispatcher();
                interpreter.eventProcessor();
                interpreter.fireSelectedTransitions();
                Assert.assertEquals(new Integer(i), interpreter.getStateVariableValue("TestStateMachine-X"));
            }
            if (verbosity > 2)
                System.out.print(interpreter.displayStateConfigurationTree());
            interpreter.eventDispatcher();
            interpreter.eventProcessor();
            interpreter.fireSelectedTransitions();
            if (verbosity > 2)
                System.out.print(interpreter.displayStateConfigurationTree());
            contextStackPool.destroyContextStack();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests simple state machine extraction from Cyc.
     */
    public void testCycExtractor () {
        System.out.println("\n**** testCycExtractor ****");
        Log.makeLog("unit-test.log");
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost", 3620, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
                //cycAccess.traceNamesOn();
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost", 3600, CycConnection.DEFAULT_COMMUNICATION_MODE,
                        true);
            }
            else
                cycAccess = new CycAccess();
            CycExtractor cycExtractor = new CycExtractor(cycAccess, CycExtractor.QUIET_VERBOSITY);
            StateMachine stateMachine = cycExtractor.extract("TestStateMachine");
            Assert.assertTrue(stateMachine instanceof StateMachine);
            Assert.assertTrue(stateMachine.getNamespace() instanceof Namespace);
            Assert.assertEquals("TestStateMachineNamespace", stateMachine.getNamespace().getName());
            Assert.assertTrue(stateMachine.getNamespace().getOwnedElement().contains(stateMachine));
            Assert.assertEquals("TestStateMachine", stateMachine.getName());
            Assert.assertTrue(stateMachine.getComment() instanceof Comment);
            Assert.assertEquals("This is a test individual #$UMLStateMachine.",
                    stateMachine.getComment().getBody());
            Assert.assertEquals(stateMachine, stateMachine.getComment().getAnnotatedElement());
            StateMachineReport stateMachineReport = new StateMachineReport(stateMachine);
            System.out.println("*------- state machine description -------*");
            stateMachineReport.report();
            System.out.println("*----- end state machine description -----*");
            interpretStateMachine(stateMachine, cycAccess);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("\n**** testCycExtractor ****");
    }
}



