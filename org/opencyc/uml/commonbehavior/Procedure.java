package org.opencyc.uml.commonbehavior;

import org.opencyc.uml.core.*;

/**
 * Procedure from the UML Common_Behavior package.
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

public class Procedure extends ModelElement {

    /**
     * the name of the language in which the body attribute is written.
     */
    protected String language;

    /**
     * the text of the procedure written in the given language
     */
    protected String body;

    /**
     * Determines whether the arguments to the procedure are passed as
     * attributes of a single object, or are passed separately.
     */
    protected boolean isList;

    /**
     * An expression the value of which is calculated by the procedure.  Used to
     * provide a detailed action model for an expression.
     */
    protected Expression expression;

    /**
     * A method which is performed by the procedure.  Used to provide a detailed
     * action model for a method.
     */
    protected Method method;

    /**
     * Constructs a new Procedure object.
     */
    public Procedure() {
    }

    /**
     * Gets the name of the language in which the body attribute is written.
     *
     * @return the name of the language in which the body attribute is written
     */
    public String getLanguage () {
        return language;
    }

    /**
     * Sets the name of the language in which the body attribute is written.
     *
     * @param xxxx the name of the language in which the body attribute is written
     */
    public void setLanguage (String language) {
        this.language = language;
    }

    /**
     * Gets the text of the procedure written in the given language.
     *
     * @return the text of the procedure written in the given language
     */
    public String getBody () {
        return body;
    }

    /**
     * Sets the text of the procedure written in the given language.
     *
     * @param body the text of the procedure written in the given language
     */
    public void setBody (String body) {
        this.body = body;
    }

    /**
     * Gets whether the arguments to the procedure are passed as
     * attributes of a single object, or are passed separately.
     *
     * @return whether the arguments to the procedure are passed as
     * attributes of a single object, or are passed separately.
     */
    public boolean isList () {
        return isList;
    }

    /**
     * Sets whether the arguments to the procedure are passed as
     * attributes of a single object, or are passed separately.
     *
     * @param isList whether the arguments to the procedure are passed as
     * attributes of a single object, or are passed separately.
     */
    public void setIsList (boolean isList) {
        this.isList = isList;
    }

    /**
     * Gets the expression the value of which is calculated by the procedure.
     *
     * @return the expression the value of which is calculated by the procedure
     */
    public Expression getExpression () {
        return expression;
    }

    /**
     * Sets the expression the value of which is calculated by the procedure.
     *
     * @param expression the expression the value of which is calculated by the procedure
     */
    public void setExpression (Expression expression) {
        this.expression = expression;
    }

    /**
     * Gets the method which is performed by the procedure
     *
     * @return the method which is performed by the procedure
     */
    public Method getMethod () {
        return method;
    }

    /**
     * Sets the method which is performed by the procedure
     *
     * @param method the method which is performed by the procedure
     */
    public void setMethod (Method method) {
        this.method = method;
    }
}