package org.opencyc.uml.statemachine;

import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;

/**
 * Guard from the UML State_Machines package.
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

public class Guard extends ModelElement {

    /**
     * the guard evaluatable expression
     */
    public BooleanExpression expression;

    /**
     * the transition to take if the guard expression evaluates
     * to true.
     */
    public Transition transition;

    /**
     * Constucts a new Guard object.
     */
    public Guard() {
    }

    /**
     * Gets the guard evaluatable expression.
     *
     * @return the guard evaluatable expression
     */
    public BooleanExpression getexpression () {
        return expression;
    }

    /**
     * Sets the guard evaluatable expression.
     *
     * @param expression the guard evaluatable expression
     */
    public void setexpression (BooleanExpression expression) {
        this.expression = expression;
    }

    /**
     * Gets the transition to take if the guard expression evaluates
     * to true
     *
     * @return the transition to take if the guard expression evaluates
     * to true
     */
    public Transition getTransition () {
        return transition;
    }

    /**
     * Sets the transition to take if the guard expression evaluates
     * to true
     *
     * @param transition the transition to take if the guard expression evaluates
     * to true
     */
    public void setTransition (Transition transition) {
        this.transition = transition;
    }
}