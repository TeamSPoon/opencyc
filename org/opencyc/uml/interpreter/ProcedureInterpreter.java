package org.opencyc.uml.interpreter;

import java.util.*;
import java.io.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;

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
        expressionEvaluator = new ExpressionEvaluator(cycAccess, stateMt, verbosity);
        this.verbosity = verbosity;
    }

    /**
     * Interprets the given procedure.
     *
     * @param procedure the procedure to interpret
     */
    public void interpret (Procedure procedure)
        throws IOException, CycApiException, ExpressionEvaluationException {
        this.procedure = procedure;
        if (verbosity > 2)
            Log.current.println("Interpreting " + procedure.toString() +
                                "\n  " + procedure.getBody());
        expressionEvaluator.evaluateCycLExpression((CycList) procedure.getBody());
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