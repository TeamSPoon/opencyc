package org.opencyc.uml.interpreter;

import java.util.*;
import java.io.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import org.opencyc.uml.action.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.statemachine.*;

/**
 * Interprets a procedure of a UML StateMachine.
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

public class ProcedureInterpreter {

    /**
     * The default verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * the procedure to interpret
     */
    protected Procedure procedure;

    /**
     * the expression evaluator
     */
    protected ExpressionEvaluator expressionEvaluator;

    /**
     * the reference to the parent CycAccess object which provides Cyc api
     * services
     */
    protected CycAccess cycAccess;

    /**
     * the state machine definition microtheory
     */
    protected CycFort stateMachineDefinitionMt;

    /**
     * the procedure definition microtheory
     */
    protected CycFort procedureDefinitionMt;

    /**
     * the context stack pool
     */
    protected ContextStackPool contextStackPool;

    /**
     * the expression evaluation state context
     */
    protected CycConstant contextFrame;

    /**
     * the procedure CycFort
     */
    protected CycFort procedureTerm;

    /**
     * #$umlProcedureInputBinding
     */
    protected CycConstant umlProcedureInputBinding;

    /**
     * #$umlProcedureOutputBinding
     */
    protected CycConstant umlProcedureOutputBinding;

    /**
     * #$softwareParameterValue
     */
    protected CycConstant softwareParameterValue;

    /**
     * #$SoftwareParameterFromSyntaxFn
     */
    protected CycConstant softwareParameterFromSyntaxFn;

    /**
     * Constructs a new ProcedureInterpreter object.
     *
     * @param cycAccess the reference to the parent CycAccess object which provides Cyc api
     * services
     * @param stateMachineDefinitionMt the state machine definition microtheory
     * @param contextStackPool the context frame pool
     * @param verbosity the output verbosity for this object
     */
    public ProcedureInterpreter(CycAccess cycAccess,
                                CycFort stateMachineDefinitionMt,
                                ContextStackPool contextStackPool,
                                int verbosity)
        throws IOException, CycApiException {
        this.cycAccess = cycAccess;
        this.stateMachineDefinitionMt = stateMachineDefinitionMt;
        this.contextStackPool = contextStackPool;
        this.verbosity = verbosity;
        initialize();
    }

    /**
     * Initializes this procedure interpreter.
     */
    protected void initialize ()
        throws IOException, CycApiException {
        expressionEvaluator = new ExpressionEvaluator(cycAccess, verbosity);
        softwareParameterValue = cycAccess.getKnownConstantByName("softwareParameterValue");
        softwareParameterFromSyntaxFn = cycAccess.getKnownConstantByName("SoftwareParameterFromSyntaxFn");
        if (verbosity > 2)
            Log.current.println("Creating ProcedureInterpreter for " +
                                "\n  stateMachineDefinitionMt: " + stateMachineDefinitionMt.cyclify());
    }


    /**
     * Interprets the procedure using the procedure binding term.
     *
     * @param procedure the procedure to evaluate
     * @param inputBindings the list of input pin bindings
     * @param outputBindings the list of output pin bindings
     * @param parentContextFrame the caller's context frame which becomes the parent
     * of the context frame allocated for this procedure's evaluation
     */
    protected void interpretProcedure (Procedure procedure,
                                       ArrayList inputBindings,
                                       ArrayList outputBindings,
                                       CycConstant parentContextFrame)
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                "\n  body: " + procedure.getBody());
        procedureTerm = cycAccess.getKnownConstantByName(procedure.toString());
        contextFrame =
            contextStackPool.allocateProcedureContextFrame(parentContextFrame,
                                                           stateMachineDefinitionMt,
                                                           procedureTerm);
        if (verbosity > 2)
            Log.current.println("Input bindings: " + inputBindings.toString());
        Iterator iter = inputBindings.iterator();
        while (iter.hasNext()) {
            InputBinding inputBinding = (InputBinding) iter.next();
            InputPin inputPin = inputBinding.getBoundInputPin();
            CycFort inputPinTerm = cycAccess.getKnownConstantByName(inputPin.toString());
            CycNart inputPinParameter = new CycNart(softwareParameterFromSyntaxFn, inputPinTerm);
            Object valueExpression = inputBinding.getBoundInputValueExpression();
            Object value = null;
            if (valueExpression instanceof StateVariable) {
                // lookup the value of the state variable
                value = cycAccess.getArg2(softwareParameterValue,
                                          cycAccess.getKnownConstantByName(valueExpression.toString()),
                                          parentContextFrame);
            }
            else
                // a literal
                value = valueExpression;
            if (verbosity > 2)
                Log.current.println(value.toString() + " --> " + inputPinParameter.cyclify());
            //TODO interpret evaluatable expressions as inpput arguments

            CycList softwareParameterValueSentence = new CycList();
            softwareParameterValueSentence.add(softwareParameterValue);
            softwareParameterValueSentence.add(inputPinParameter);
            softwareParameterValueSentence.add(value);
            cycAccess.assertWithBookkeepingAndWithoutTranscript(softwareParameterValueSentence,
                                                                contextFrame);
        }

        // evaluate the procedure body
        expressionEvaluator.evaluateCycLExpression(procedure.getBody(),
                                                   contextFrame);

        // bind output values
        if (verbosity > 2)
            Log.current.println("Output bindings: " + outputBindings.toString());
        iter = outputBindings.iterator();
        while (iter.hasNext()) {
            OutputBinding outputBinding = (OutputBinding)  iter.next();
            OutputPin outputPin = outputBinding.getBoundOutputPin();
            CycFort outputPinTerm = cycAccess.getKnownConstantByName(outputPin.toString());
            StateVariable stateVariable = outputBinding.getBoundOutputStateVariable();
            CycFort stateVariableTerm = cycAccess.getKnownConstantByName(stateVariable.toString());
            CycNart outputPinParameter = new CycNart(softwareParameterFromSyntaxFn, outputPinTerm);
            Object value =
                cycAccess.getArg2(softwareParameterValue,
                                  outputPinParameter,
                                  contextFrame);
            CycList softwareParameterValueSentence = new CycList();
            softwareParameterValueSentence.add(softwareParameterValue);
            softwareParameterValueSentence.add(stateVariableTerm);
            softwareParameterValueSentence.add(value);
            cycAccess.unassertMatchingAssertionsWithoutTranscript(softwareParameterValue,
                                                                  stateVariableTerm,
                                                                  parentContextFrame);
            if (verbosity > 2)
                Log.current.println("asserting " + softwareParameterValueSentence +
                                    " in " + parentContextFrame);
            cycAccess.assertWithBookkeepingAndWithoutTranscript(softwareParameterValueSentence,
                                                                parentContextFrame);
        }
        contextStackPool.deallocateContextFrame(contextFrame);
    }

    /**
     * Gets the procedure to interpret.
     *
     * @return the procedure to interpret
     */
    public Procedure getProcedure () {
        return procedure;
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

}