package org.opencyc.uml.core;

import java.util.*;

/**
 * Procedure from the UML Core package.
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
     * If isList is true: the ordered set of input pins representing
     * procedure arguments.  If isList is false: one input pin
     * representing the request object.
     */
    protected ArrayList argument = new ArrayList();

    /**
     * If isList is true: the ordered set of output pins representing
     * procedure results.  If isList is false: one output pin
     * representing the reply object.
     */
    protected ArrayList result = new ArrayList();

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
     * @param language the name of the language in which the body attribute is written
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
     * Gets the input pins representing procedure arguments.
     *
     * @return the input pins representing procedure arguments
     */
    public ArrayList getArgument () {
        return argument;
    }

    /**
     * Sets the input pins representing procedure arguments.
     *
     * @param argument the input pins representing procedure arguments
     */
    public void setArgument (ArrayList argument) {
        this.argument = argument;
    }

    /**
     * Gets the pins representing procedure results.
     *
     * @return the pins representing procedure results
     */
    public ArrayList getResult () {
        return result;
    }

    /**
     * Sets the pins representing procedure results.
     *
     * @param result the pins representing procedure results
     */
    public void setResult (ArrayList result) {
        this.result = result;
    }
}