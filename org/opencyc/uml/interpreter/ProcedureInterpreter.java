package org.opencyc.uml.interpreter;

import java.util.*;
import java.io.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
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
     * the procedure binding term which is the association
     * between the procedure and a particular calling transition or state
     */
    protected CycFort procedureBinding;

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
     * the expression evaluation state context
     */
    protected CycFort stateMt;

    /**
     * the procedure CycFort
     */
    protected CycFort procedureTerm;

    /**
     * #$umlProcedureEvaluationContext
     */
    protected CycConstant umlProcedureEvaluationContext;

    /**
     * #$umlProcedureBinding-CalledByStateEntry
     */
    protected CycConstant umlProcedureBinding_CalledByStateEntry;

    /**
     * #$umlProcedureBinding-CalledByStateDoActivity
     */
    protected CycConstant umlProcedureBinding_CalledByStateDoActivity;

    /**
     * #$umlProcedureBinding-CalledByStateExit
     */
    protected CycConstant umlProcedureBinding_CalledByStateExit;

    /**
     * #$umlProcedureBinding-CalledByTransition
     */
    protected CycConstant umlProcedureBinding_CalledByTransition;

    /**
     * #$umlProcedureBinding-Procedure
     */
    protected CycConstant umlProcedureBinding_Procedure;

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
     * @param stateMt the expression evaluation state context
     * @param verbosity the output verbosity for this object
     */
    public ProcedureInterpreter(CycAccess cycAccess,
                                CycFort stateMt,
                                int verbosity)
        throws IOException, CycApiException {
        this.cycAccess = cycAccess;
        this.stateMt = stateMt;
        this.verbosity = verbosity;
        expressionEvaluator = new ExpressionEvaluator(cycAccess, stateMt, verbosity);
        umlProcedureEvaluationContext = cycAccess.getKnownConstantByName("umlProcedureEvaluationContext");
        umlProcedureBinding_CalledByStateEntry = cycAccess.getKnownConstantByName("umlProcedureBinding-CalledByStateEntry");
        umlProcedureBinding_CalledByStateDoActivity = cycAccess.getKnownConstantByName("umlProcedureBinding-CalledByStateDoActivity");
        umlProcedureBinding_CalledByStateExit = cycAccess.getKnownConstantByName("umlProcedureBinding-CalledByStateExit");
        umlProcedureBinding_CalledByTransition = cycAccess.getKnownConstantByName("umlProcedureBinding-CalledByTransition");
        umlProcedureBinding_Procedure = cycAccess.getKnownConstantByName("umlProcedureBinding-Procedure");
        umlProcedureInputBinding = cycAccess.getKnownConstantByName("umlProcedureInputBinding");
        umlProcedureOutputBinding = cycAccess.getKnownConstantByName("umlProcedureOutputBinding");
        softwareParameterValue = cycAccess.getKnownConstantByName("softwareParameterValue");
        softwareParameterFromSyntaxFn = cycAccess.getKnownConstantByName("SoftwareParameterFromSyntaxFn");
    }

    /**
     * Interprets the procedure which is called from the calling transition.
     *
     * @param transition the transition which called the procedure to be interpreted
     */
    public void interpretTransitionProcedure (Transition transition)
        throws IOException, CycApiException, ExpressionEvaluationException {
        procedure = transition.getEffect();
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                " at " + transition.toString() +
                                "\n  " + procedure.getBody());
        // get procedure binding term
        CycFort procedureTerm = cycAccess.getKnownConstantByName(procedure.getName());
        CycList query = new CycList();
        query.add(CycAccess.and);
        CycList query1 = new CycList();
        query1.add(umlProcedureBinding_CalledByTransition);
        CycVariable cycVariable = CycObjectFactory.makeCycVariable("?PROCEDURE-BINDING");
        query1.add(cycVariable);
        query1.add(cycAccess.getKnownConstantByName(transition.toString()));
        query.add(query1);
        CycList query2 = new CycList();
        query2.add(umlProcedureBinding_Procedure);
        query2.add(cycVariable);
        query2.add(cycAccess.getKnownConstantByName(procedure.toString()));
        query.add(query2);
        CycList queryResult =
            cycAccess.askWithVariable(query, cycVariable, stateMt);
        procedureBinding = (CycFort) queryResult.first();
        interpretProcedure();
    }

    /**
     * Interprets the procedure which is called from the given state as its
     * entry action.
     *
     * @param state the state whose entry action called the procedure
     */
    public void interpretStateEntryProcedure (State state)
        throws IOException, CycApiException, ExpressionEvaluationException {
        procedure = state.getEntry();
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                " at entry action for " + state.toString() +
                                "\n  " + procedure.getBody());
        // get procedure binding term
        CycFort procedureTerm = cycAccess.getKnownConstantByName(procedure.getName());
        procedureBinding =
            (CycFort) cycAccess.getArg1(umlProcedureBinding_Procedure,
                                        procedureTerm,
                                        stateMt);
        interpretProcedure();
    }

    /**
     * Interprets theprocedure which is called from the given state as its
     * entry action.
     *
     * @param state the state whose doActivity called the procedure
     */
    public void interpretStateDoActivityProcedure (State state)
        throws IOException, CycApiException, ExpressionEvaluationException {
        procedure = state.getDoActivity();
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                " at doActivity for " + state.toString() +
                                "\n  " + procedure.getBody());
        //TODO
    }

    /**
     * Interprets the given procedure which is called from the given state as its
     * entry action.
     *
     * @param procedure the procedure to interpret
     * @param state the state whose exit action called the procedure
     */
    public void interpretStateExitProcedure (State state)
        throws IOException, CycApiException, ExpressionEvaluationException {
        procedure = state.getExit();
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                " at exit action for " + state.toString() +
                                "\n  " + procedure.getBody());
        //TODO
    }

    /**
     * Interprets the procedure using the procedure binding term.
     */
    protected void interpretProcedure ()
        throws IOException, CycApiException, ExpressionEvaluationException {
        // clear interpretation context
        CycFort evaluationContext =
            (CycFort) cycAccess.getArg2(umlProcedureEvaluationContext,
                                        procedureTerm,
                                        stateMt);
        cycAccess.unassertMtContentsWithoutTranscript(evaluationContext);
        // bind input values
        CycList inputBindingGafs =
            cycAccess.getGafs(umlProcedureInputBinding, stateMt);
        Iterator iter = inputBindingGafs.iterator();
        while (iter.hasNext()) {
            CycList inputBindingGaf = (CycList) iter.next();
            CycFort thisProcedureBinding = (CycFort) inputBindingGaf.second();
            if (! procedureBinding.equals(thisProcedureBinding))
                continue;
            CycFort inputPin = (CycFort) inputBindingGaf.third();
            CycNart inputPinParameter = new CycNart(softwareParameterFromSyntaxFn, inputPin);
            CycFort stateVariable = (CycFort) inputBindingGaf.fourth();
            Object value =
                cycAccess.getArg2(softwareParameterValue,
                                  inputPinParameter,
                                  stateMt);
            CycList softwareParameterValueSentence = new CycList();
            softwareParameterValueSentence.add(softwareParameterValue);
            softwareParameterValueSentence.add(stateVariable);
            softwareParameterValueSentence.add(value);
            cycAccess.assertWithBookkeepingAndWithoutTranscript(softwareParameterValueSentence,
                                                                evaluationContext);
        }

        // evaluate the procedure body
        expressionEvaluator.evaluateCycLExpression((CycList) procedure.getBody());

        // bind output values
        CycList outputBindingGafs =
            cycAccess.getGafs(umlProcedureOutputBinding, stateMt);
        iter = outputBindingGafs.iterator();
        while (iter.hasNext()) {
            CycList outputBindingGaf = (CycList) iter.next();
            CycFort thisProcedureBinding = (CycFort) outputBindingGaf.second();
            if (! procedureBinding.equals(thisProcedureBinding))
                continue;
            CycFort outputPin = (CycFort) outputBindingGaf.third();
            CycFort stateVariable = (CycFort) outputBindingGaf.fourth();
            Object value =
                cycAccess.getArg2(softwareParameterValue,
                                  stateVariable,
                                  evaluationContext);
            CycList softwareParameterValueSentence = new CycList();
            softwareParameterValueSentence.add(softwareParameterValue);
            CycNart outputPinParameter = new CycNart(softwareParameterFromSyntaxFn, outputPin);
            softwareParameterValueSentence.add(outputPinParameter);
            softwareParameterValueSentence.add(value);
            cycAccess.assertWithBookkeepingAndWithoutTranscript(softwareParameterValueSentence,
                                                                stateMt);
        }
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
     * Gets the procedure binding term which is the association
     * between the procedure and a particular calling transition or state.
     *
     * @return procedure binding term
     */
    public CycFort getProcedureBinding () {
        return procedureBinding;
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