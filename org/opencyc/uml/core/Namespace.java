package org.opencyc.uml.core;

import java.util.*;

/**
 * Namespace from the UML Core package.
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

public class Namespace extends ModelElement {

    /**
     * the set of model elements owned by this namespace
     */
    protected ArrayList ownedElement= new ArrayList();

    /**
     * Constructs a new Namespace object.
     */
    public Namespace() {
    }

    /**
     * Constructs a new Namespace object given its name.
     *
     * @param name the name of this namespace
     */
    public Namespace(String name) {
        this.name = name;
    }

    /**
     * Returns true if the given object is equal to this object, otherwise returns
     * false.
     *
     * @param object the given object
     * @return true if the given object is equal to this object, otherwise returns
     * false
     */
    public boolean equals (Object object) {
        if (! this.getClass().equals(object.getClass()))
            return false;
        Namespace that = (Namespace) object;
        return this.name.equals(that.name);
    }

    /**
     * Gets the set of model elements owned by this namespace.
     *
     * @return the set of model elements owned by this namespace
     */
    public ArrayList getOwnedElement () {
        return ownedElement;
    }

    /**
     * Sets the set of model elements owned by this namespace.
     *
     * @param ownedElement the set of model elements owned by this namespace
     */
    public void setOwnedElement (ArrayList ownedElement) {
        this.ownedElement = ownedElement;
    }

    /**
     * Adds the given model element to the list of model elements owned by
     * this namespace.
     *
     * @param modelElement the model element to be added to the list of elements
     * owned by this namespace
     */
    public void addOwnedElement (ModelElement modelElement) {
        ownedElement.add(modelElement);
    }

}