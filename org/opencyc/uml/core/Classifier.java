package org.opencyc.uml.core;

import java.util.ArrayList;

/**
 * Classifier from the UML Core Package
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

public abstract class Classifier extends GeneralizableElement {

    /**
     * the set of model elements owned by this namespace
     */
    protected ArrayList ownedElement = new ArrayList();

    /**
     * the ordered list of Features, like Attribute and Operation
     * owned by this Classifier
     */
    protected ArrayList feature = new ArrayList();

    /**
     * Constructs a new Classifier object.
     */
    public Classifier() {
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

    /**
     * Gets the ordered list of Features, like Attribute and Operation
     * owned by this Classifier
     *
     * @return the ordered list of Features
     */
    public ArrayList getFeature () {
        return feature;
    }

    /**
     * Sets the ordered list of Features, like Attribute and Operation
     * owned by this Classifier
     *
     * @param feature the ordered list of Features
     */
    public void setFeature (ArrayList feature) {
        this.feature = feature;
    }

    /**
     * Adds a Feature
     *
     * @param feature the given feature
     */
    public void addFeature (Feature feature) {
        this.feature.add(feature);
    }

}