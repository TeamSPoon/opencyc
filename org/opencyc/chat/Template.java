package org.opencyc.chat;

/**
 * Contains a template for parsing a particular utterance, phrase or sentence
 * into an event and arguments.
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

public class Template implements Comparable {

    /**
     * The template expression which is a string to be matched that includes
     * symbolic variables prefixed by question marks.
     */
    protected String templateExpression;

    /**
     * the event associated with this template
     */
    protected DialogFsmEvent dialogFsmEvent;

    /**
     * Constructs a new Template object given the template expression
     * and event.
     *
     * @param templateExpression the template expression
     * @param dialogFsmEvent the event
     */
    protected Template(String templateExpression,
                    DialogFsmEvent dialogFsmEvent) {
        this.templateExpression = templateExpression;
        this.dialogFsmEvent = dialogFsmEvent;
    }

    /**
     * Returns the templateExpression.
     *
     * @return the templateExpression
     */
    public String getTemplateExpression () {
        return templateExpression;
    }

    /**
     * Returns the event.
     *
     * @return the event associated with this template
     */
    public DialogFsmEvent getDialogFsmEvent() {
        return dialogFsmEvent;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     *
     * @param object the reference object with which to compare.
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
     public int compareTo (Object object) {
        if (! (object instanceof Template))
            throw new ClassCastException("Must be a Template object");
        return this.templateExpression.compareTo(((Template) object).templateExpression);
     }

}

