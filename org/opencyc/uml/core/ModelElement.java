package org.opencyc.uml.core;

/**
 * ModelElement from the UML Core Package.
 *
 * An element is an atomic constitutent of a model.
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

public abstract class ModelElement extends Element {

    /**
     * the identifier for the ModelElement within its containing
     * namespace
     */
    protected String name;

    /**
     * the model elment namespace
     */
    protected Namespace namespace;

    /**
     * the model element comment
     */
    protected Comment comment;

    /**
     * Constructs a new Element object.
     */
    public ModelElement() {
    }

    /**
     * Returns true if the given object is equal to this object, otherwise returns
     * false.  Equality is determined by the namespace and name of the model element.
     *
     * @param object the given object
     * @return true if the given object is equal to this object, otherwise returns
     * false
     */
    public boolean equals (Object object) {
        if (! this.getClass().equals(object.getClass()))
            return false;
        ModelElement that = (ModelElement) object;
        if (! this.name.equals(that.name))
            return false;
        if (this.namespace == null) {
            if (that.namespace == null)
                return true;
            else
                return false;
        }
        if (that.namespace == null)
            return false;
        return this.namespace.equals(that.namespace);
    }


    /**
     * Gets the identifier for the ModelElement within its containing
     * namespace
     *
     * @return the identifier for the ModelElement within its containing
     * namespace
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the identifier for the ModelElement within its containing
     * namespace
     *
     * @param name the identifier for the ModelElement within its containing
     * namespace
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Gets the model elment namespace
     *
     * @return the model elment namespace
     */
    public Namespace getNamespace () {
        return namespace;
    }

    /**
     * Sets the model elment namespace
     *
     * @param namespace the model elment namespace
     */
    public void setNamespace (Namespace namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets the model element comment
     *
     * @return the model element comment
     */
    public Comment getComment () {
        return comment;
    }

    /**
     * Sets the model element comment
     *
     * @param comment the model element comment
     */
    public void setComment (Comment comment) {
        this.comment = comment;
    }
}