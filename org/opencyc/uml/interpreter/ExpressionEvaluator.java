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
     * the expression evaluation state context
     */
    protected CycFort stateMt;

    /**
     * #$ProgramAssignmentFn
     */
    protected CycConstant programAssignmentFn;

    /**
     * #$ProgramBlockFn
     */
    protected CycConstant programBlockFn;

    /**
     * #$softwareParameterValue
     */
    protected CycConstant softwareParameterValue;

    /**
     * Constructs a new ExpressionEvaluator object.
     *
     * @param cycAccess the reference to the parent CycAccess object which provides Cyc api
     * services
     * @param stateMt the expression evaluation state context
     * @param verbosity the output verbosity for this object
     */
    public ExpressionEvaluator(CycAccess cycAccess, CycFort stateMt, int verbosity)
        throws IOException, CycApiException {
        this.cycAccess = cycAccess;
        this.stateMt = stateMt;
        this.verbosity = verbosity;
        programAssignmentFn = cycAccess.getKnownConstantByName("ProgramAssignmentFn");
        programBlockFn = cycAccess.getKnownConstantByName("ProgramBlockFn");
        softwareParameterValue = cycAccess.getKnownConstantByName("softwareParameterValue");
    }

    /**
     * Evaluates the given boolean java expression in the context of the TreeInterpreter.
     *
     * @param booleanExpression the given boolean java expression
     * @return the result of evaluating the given boolean java expression
     */
    public boolean evaluateBoolean (BooleanExpression booleanExpression)
        throws IOException, CycApiException {
        Object answer = evaluateCycLExpression((CycList) booleanExpression.getBody());
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
     */
    public void evaluate (Expression expression)
        throws IOException, CycApiException {
        evaluateCycLExpression(expression.getBody());
    }

    /**
     * Evaluates the given CycL expression in the state context.
     *
     * @param cycLExpression the given CycL expression
     * @return the value of the evaluated CycL expression
     */
    public Object evaluateCycLExpression (Object cycLExpression)
        throws IOException, CycApiException {
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
                                        cycLExpressionList.third());
        }
        else if (cycLExpressionList.first().equals(programBlockFn)) {
            evaluateProgramBlockFn((CycList) cycLExpressionList.rest());
        }
        return null;
    }

    /**
     * Evaluates #$ProgramBlockFn which evaluates the given list of expressions
     * sequentially.
     *
     * @param cycLExpressions the given expressions in this program block to evaluate
     * @return the symbol NIL
     */
    public CycSymbol evaluateProgramBlockFn (CycList cycLExpressions)
        throws IOException, CycApiException {
        Iterator iter = cycLExpressions.iterator();
        while (iter.hasNext())
            evaluateCycLExpression(iter.next());
        return CycObjectFactory.nil;
    }

    /**
     * Evaluates #$ProgramAssignmentFn which assigns the given value expression to the given
     * #$SoftwareParameter object.
     *
     * @param softwareParameter the given #$SoftwareParameter object
     * @valueExpression the value expression to evaluated and assigned
     * @return the symbol NIL
     */
    public CycSymbol evaluateProgramAssignmentFn (CycFort softwareParameter,
                                                  Object valueExpression)
        throws IOException, CycApiException {
        Object value = evaluateCycLObject(valueExpression);
        if (value instanceof CycList)
            cycAccess.assertGaf(stateMt,
                                softwareParameterValue,
                                softwareParameter,
                                (CycList) value);
        else if (value instanceof CycFort)
            cycAccess.assertGaf(stateMt,
                                softwareParameterValue,
                                softwareParameter,
                                (CycFort) value);
        else if (value instanceof Boolean ||
            value instanceof String ||
            value instanceof Integer ||
            value instanceof Long ||
            value instanceof Character ||
            value instanceof Float ||
            value instanceof Double) {
            CycList sentence = new CycList();
            sentence.add(softwareParameterValue);
            sentence.add(softwareParameter);
            sentence.add(value);
            cycAccess.assertWithBookkeepingAndWithoutTranscript (sentence, stateMt);
        }
        return CycObjectFactory.nil;
    }

    /**
     * Returns the result of evaluating the given CycL object in the current
     * state context.
     *
     * @param cycLObject the given CycL object
     * @return the result of evaluating the given CycL object in the current
     * state context
     */
    public Object evaluateCycLObject (Object cycLObject)
            throws IOException, CycApiException {
        if (cycLObject instanceof Boolean ||
            cycLObject instanceof String ||
            cycLObject instanceof Integer ||
            cycLObject instanceof Long ||
            cycLObject instanceof Character ||
            cycLObject instanceof Float ||
            cycLObject instanceof Double)
            return cycLObject;

        //TODO handle other kinds of evaluatable objects
        return CycObjectFactory.nil;
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