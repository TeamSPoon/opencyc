package org.opencyc.uml.interpreter;

import java.io.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.util.*;

/**
 * Evaluates a UML Expression.
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

public class ExpressionEvaluator {


    /**
     * The quiet verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int QUIET_VERBOSITY = 0;

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
     * the reference to the parent CycAccess object which provides Cyc api
     * services
     */
    protected CycAccess cycAccess;

    /**
     * #$EqualsInProgramFn
     */
    protected CycConstant equalsInProgramFn;

    /**
     * #$ProgramAssignmentFn
     */
    protected CycConstant programAssignmentFn;

    /**
     * #$ProgramBlockFn
     */
    protected CycConstant programBlockFn;

    /**
     * #$ProgramConditionFn
     */
    protected CycConstant programConditionFn;

    /**
     * #$softwareParameterValue
     */
    protected CycConstant softwareParameterValue;

    /**
     * #$evaluate
     */
    protected CycConstant evaluate;

    /**
     * ?value
     */
    protected CycConstant trueValue;

    /**
     * Constructs a new ExpressionEvaluator object.
     *
     * @param cycAccess the reference to the parent CycAccess object which provides Cyc api
     * services
     * @param verbosity the output verbosity for this object
     */
    public ExpressionEvaluator(CycAccess cycAccess, int verbosity)
        throws IOException, CycApiException {
        this.cycAccess = cycAccess;
        this.verbosity = verbosity;
        equalsInProgramFn = cycAccess.getKnownConstantByName("EqualsInProgramFn");
        programAssignmentFn = cycAccess.getKnownConstantByName("ProgramAssignmentFn");
        programBlockFn = cycAccess.getKnownConstantByName("ProgramBlockFn");
        programConditionFn = cycAccess.getKnownConstantByName("ProgramConditionFn");
        softwareParameterValue = cycAccess.getKnownConstantByName("softwareParameterValue");
        evaluate = cycAccess.getKnownConstantByName("evaluate");
        trueValue = cycAccess.getKnownConstantByName("True");
    }

    /**
     * Evaluates the given boolean expression in the state context.
     *
     * @param booleanExpression the given boolean java expression
     * @param evaluationContext the given  state evaluation context
     * @return the result of evaluating the given boolean java expression
     */
    public boolean evaluateBoolean (BooleanExpression booleanExpression,
                                    CycFort evaluationContext)
        throws IOException, CycApiException, ExpressionEvaluationException {
        Object answer = evaluateCycLExpression((CycList) booleanExpression.getBody(),
                                               evaluationContext);
        if (answer.equals(Boolean.TRUE))
            return true;
        else if (answer.equals(Boolean.FALSE))
            return false;
        else
            throw new RuntimeException("Expected Boolean answer not returned: " + answer.toString());
    }

    /**
     * Evaluates the given expression in the state context.
     *
     * @param expression the given java expression
     * @param evaluationContext the given  state evaluation context
     */
    public void evaluate (Expression expression,
                          CycFort evaluationContext)
        throws IOException, CycApiException, ExpressionEvaluationException {
        evaluateCycLExpression(expression.getBody(),
                               evaluationContext);
    }

    /**
     * Evaluates the given CycL expression in the state context.
     *
     * @param cycLExpression the given CycL expression
     * @param evaluationContext the given  state evaluation context
     * @return the value of the evaluated CycL expression
     */
    public Object evaluateCycLExpression (Object cycLExpression,
                                          CycFort evaluationContext)
        throws IOException, CycApiException, ExpressionEvaluationException {
        if (verbosity > 2)
            Log.current.println("Evaluating CycL expression: " + cycLExpression);
        CycList cycLExpressionList;
        if (cycLExpression instanceof CycNart) {
            cycLExpressionList = new CycList();
            cycLExpressionList.add(((CycNart) cycLExpression).getFunctor());
            cycLExpressionList.addAll(((CycNart) cycLExpression).getArguments());
        }
        else
            cycLExpressionList = (CycList) cycLExpression;
        if (cycLExpressionList.first().equals(programAssignmentFn)) {
            evaluateProgramAssignmentFn((CycFort) cycLExpressionList.second(),
                                        cycLExpressionList.third(),
                                        evaluationContext);
        }
        else if (cycLExpressionList.first().equals(programBlockFn)) {
            evaluateProgramBlockFn((CycList) cycLExpressionList.rest(),
                                   evaluationContext);
        }
        else if (cycLExpressionList.first().equals(programConditionFn)) {
            return evaluateProgramConditionFn((CycList) cycLExpressionList.second(),
                                              evaluationContext);
        }
        else if (cycLExpressionList.first().equals(equalsInProgramFn)) {
            return evaluateEqualsInProgramFn(cycLExpressionList.second(),
                                             cycLExpressionList.third(),
                                            evaluationContext);
        }
        else
            throw new ExpressionEvaluationException("Unhandled expression " + cycLExpression +
                                                    "\nclass: " + cycLExpression.getClass());
        return null;
    }

    /**
     * Evaluates #$EqualsInProgramFn which evaluates the two given arguments for equality
     * and returns True if the two evaluated arguments are equal, otherwise return False.
     *
     * @param arg1 the first given argument
     * @param arg2 the second given argument
     * @return True if the two evaluated arguments are equal, otherwise return False
     * @param evaluationContext the given  state evaluation context
     */
    public Boolean evaluateEqualsInProgramFn (Object arg1,
                                              Object arg2,
                                             CycFort evaluationContext)
        throws IOException, CycApiException, ExpressionEvaluationException {
        Object value1 = evaluateCycLObject(arg1, evaluationContext);
        Object value2 = evaluateCycLObject(arg2, evaluationContext);
        if (value1.equals(value2)) {
            if (verbosity > 2)
                Log.current.println("  " + value1 + " equals " + value2);
            return Boolean.TRUE;
        }
        else {
            if (verbosity > 2)
                Log.current.println("  " + value1 + " does not equal " + value2);
            return Boolean.FALSE;
        }
    }

    /**
     * Evaluates #$ProgramBlockFn which evaluates the given boolean expression.
     *
     * @param cycLExpressions the given expressions in this program block to evaluate
     * @param evaluationContext the given  state evaluation context
     * @return the symbol NIL
     */
    public CycSymbol evaluateProgramBlockFn (CycList cycLExpressions,
                                               CycFort evaluationContext)
        throws IOException, CycApiException, ExpressionEvaluationException {
        Iterator iter = cycLExpressions.iterator();
        while (iter.hasNext())
            evaluateCycLExpression(iter.next(), evaluationContext);
        return CycObjectFactory.nil;
    }

    /**
     * Evaluates #$ProgramConditionFn which evaluates the given list of expressions
     * sequentially.
     *
     * @param cycLExpressions the given boolean expression
     * @param evaluationContext the given  state evaluation context
     * @return the result of evaluating the given boolean expression
     */
    public Boolean evaluateProgramConditionFn (CycList cycLExpression,
                                               CycFort evaluationContext)
            throws IOException, CycApiException, ExpressionEvaluationException {
        boolean answer;
        answer = cycAccess.isQueryTrue(cycLExpression, evaluationContext);
        if (verbosity > 2)
            Log.current.println(cycLExpression.cyclify() + "\n evaluates to " + answer);
        return new Boolean(answer);
    }

    /**
     * Evaluates #$ProgramAssignmentFn which assigns the given value expression to the given
     * #$SoftwareParameter object.
     *
     * @param softwareParameter the given #$SoftwareParameter object
     * @valueExpression the value expression to evaluated and assigned
     * @param evaluationContext the given  state evaluation context
     * @return the symbol NIL
     */
    public CycSymbol evaluateProgramAssignmentFn (CycFort softwareParameter,
                                                  Object valueExpression,
                                                  CycFort evaluationContext)
        throws IOException, CycApiException {
        if (verbosity > 2)
            Log.current.println("Assigning value " + valueExpression + " to " + softwareParameter.cyclify());
        Object value = evaluateCycLObject(valueExpression, evaluationContext);
        CycList sentence = new CycList();
        if (value instanceof CycList ||
            value instanceof CycFort) {
            sentence.add(evaluationContext);
            sentence.add(softwareParameterValue);
            sentence.add(softwareParameter);
            sentence.add(value);
        }
        else if (value instanceof Boolean ||
            value instanceof String ||
            value instanceof Integer ||
            value instanceof Long ||
            value instanceof Character ||
            value instanceof Float ||
            value instanceof Double) {
            sentence.add(softwareParameterValue);
            sentence.add(softwareParameter);
            sentence.add(value);
        }
        else
            throw new RuntimeException("Unhandled value assignment");
        cycAccess.assertWithBookkeepingAndWithoutTranscript (sentence, evaluationContext);
        return CycObjectFactory.nil;
    }

    /**
     * Returns the result of evaluating the given CycL object in the given
     * state evaluation context.
     *
     * @param cycLObject the given CycL object
     * @param evaluationContext the given  state evaluation context
     * @return the result of evaluating the given CycL object in the current
     * state context
     */
    public Object evaluateCycLObject (Object cycLObject, CycFort evaluationContext)
            throws IOException, CycApiException {
        Object value = CycObjectFactory.nil;
        if (cycLObject instanceof Boolean ||
            cycLObject instanceof String ||
            cycLObject instanceof Integer ||
            cycLObject instanceof Long ||
            cycLObject instanceof Character ||
            cycLObject instanceof Float ||
            cycLObject instanceof Double)
            value = cycLObject;
        else if (cycLObject instanceof CycFort) {
            value = cycAccess.getArg2(softwareParameterValue,
                                      (CycFort) cycLObject,
                                      evaluationContext);
        }
        else if (cycLObject instanceof CycList) {
            CycList query = new CycList();
            query.add(cycAccess.getKnownConstantByName("evaluate"));
            CycVariable variable = CycObjectFactory.makeCycVariable("?x");
            query.add(variable);
            query.add(cycLObject);
            if (verbosity > 2)
                Log.current.println("evaluation query in mt: " + evaluationContext.cyclify() +
                                    "\n  " + query.cyclify());
            CycList answer = cycAccess.askWithVariable(query, variable, evaluationContext);
            if (answer.isEmpty())
                throw new RuntimeException("Unevaluatable expression \n" + query.toPrettyString("") +
                                           "\n in mt " + evaluationContext.cyclify());
            value = answer.first();
        }
        //TODO handle other kinds of evaluatable objects
        if (verbosity > 2)
            Log.current.println("Value of " + cycLObject + " is " + value);
        return value;
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