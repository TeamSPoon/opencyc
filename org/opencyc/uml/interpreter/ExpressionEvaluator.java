package org.opencyc.uml.interpreter;

import java.util.*;
import java.io.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.parser.wrapper.*;
import org.opencyc.uml.commonbehavior.*;

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
     * the tree interpreter which interprets java statements
     */
    TreeInterpreter treeInterpreter;

    /**
     * Constructs a new ExpressionEvaluator object given the tree
     * interpreter which interprets java statements
     *
     * @param treeInterpreter the given tree interpreter
     */
    public ExpressionEvaluator(TreeInterpreter treeInterpreter) {
        this.treeInterpreter = treeInterpreter;
    }

    /**
     * Evaluates the given boolean java expression in the context of the TreeInterpreter.
     *
     * @param booleanExpression the given boolean java expression
     * @return the result of evaluating the given boolean java expression
     */
    public boolean evaluateBoolean (BooleanExpression booleanExpression) {

        StringReader stringReader = new StringReader(booleanExpression.getBody());
        Object result = treeInterpreter.interpret(stringReader, "boolean expression");
        if (result instanceof Boolean)
            return ((Boolean) result).booleanValue();
        else
            throw new RuntimeException("Result of evaluating " + booleanExpression.getBody() +
                                       "\nis not a Boolean " + result.toString());
    }

    /**
     * Evaluates the given java expression in the context of the TreeInterpreter.
     *
     * @param expression the given java expression
     */
    public void evaluate (Expression expression) {
        StringReader stringReader = new StringReader(expression.getBody());
        treeInterpreter.interpret(stringReader, "boolean expression");
    }
}