package org.opencyc.uml.core;

/**
 * Comment from the UML Core Package
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

public class Comment extends ModelElement {

    /**
     * the annotated element
     */
    protected ModelElement annotatedElement;

    /**
     * the comment
     */
    protected String body;

    /**
     * Constructs a new Comment object.
     */
    public Comment() {
    }

    /**
     * Gets the annotated element
     *
     * @return the annotated element
     */
    public ModelElement getAnnotatedElement () {
        return annotatedElement;
    }

    /**
     * Sets the annotated element
     *
     * @param annotatedElement the annotated element
     */
    public void setAnnotatedElement (ModelElement annotatedElement) {
        this.annotatedElement = annotatedElement;
    }

    /**
     * Gets the comment
     *
     * @return the comment
     */
    public String getBody () {
        return body;
    }

    /**
     * Sets the comment
     *
     * @param body the comment
     */
    public void setBody (String body) {
        this.body = body;
    }
}