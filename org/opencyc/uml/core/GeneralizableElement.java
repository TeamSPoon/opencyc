package org.opencyc.uml.core;

import java.util.ArrayList;

/**
 * GeneralizableElement from the UML Core Package
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

public class GeneralizableElement extends ModelElement {

    /**
     * when true, indicates that the GeneralizableElement may not have ancestors
     */
    protected boolean isRoot;

    /**
     * when true, indicates that the GeneralizableElement may not have descendents
     */
    protected boolean isLeaf;

    /**
     * when true, indicates that an instance of the GeneralizableElement must be an
     * instance of a child of the GeneralizableElement and when false, indicates that
     * an instance of the GeneralizableElement may not have to be an instance of the
     * child
     */
    protected boolean isAbstract;

    /**
     * the set of parent GeneralizableElement objects
     */
    protected ArrayList generalization = new ArrayList();

    /**
     * the set of child GeneralizableElement objects
     */
    protected ArrayList specialization = new ArrayList();

    /**
     * Constructs a new GeneralizableElement object.
     */
    public GeneralizableElement() {
    }

    /**
     * Gets the ancestors indicator
     *
     * @return the ancestors indicator
     */
    public boolean isRoot () {
        return isRoot;
    }

    /**
     * Sets the ancestors indicator
     *
     * @param isRoot the ancestors indicator
     */
    public void setIsRoot (boolean isRoot) {
        this.isRoot = isRoot;
    }

    /**
     * Gets the descendants indicator
     *
     * @return the descendants indicator
     */
    public boolean isLeaf () {
        return isLeaf;
    }

    /**
     * Sets the descendants indicator
     *
     * @param isLeaf the descendants indicator
     */
    public void setIsLeaf (boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * Gets the abstract indicator
     *
     * @return the abstract indicator
     */
    public boolean isAbstract () {
        return isAbstract;
    }

    /**
     * Sets the abstract indicator
     *
     * @param isAbstract the abstract indicator
     */
    public void setIsAbstract (boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * Gets the set of parent GeneralizableElement objects
     *
     * @return the set of parent GeneralizableElement objects
     */
    public ArrayList getGeneralization () {
        return generalization;
    }

    /**
     * Sets the set of parent GeneralizableElement objects
     *
     * @param generalization the set of parent GeneralizableElement objects
     */
    public void setGeneralization (ArrayList generalization) {
        this.generalization = generalization;
    }

    /**
     * Adds a parent GeneralizableElement.
     *
     * @param generalizableElement the given parent GeneralizableElement
     */
    public void addGeneralization (GeneralizableElement generalizableElement) {
        generalization.add(generalizableElement);
    }

    /**
     * Gets the set of child GeneralizableElement objects
     *
     * @return the set of child GeneralizableElement objects
     */
    public ArrayList getSpecialization () {
        return specialization;
    }

    /**
     * Sets the set of child GeneralizableElement objects
     *
     * @param specialization the set of child GeneralizableElement objects
     */
    public void setSpecialization (ArrayList specialization) {
        this.specialization = specialization;
    }

    /**
     * Adds a child GeneralizableElement.
     *
     * @param generalizableElement the given parent GeneralizableElement
     */
    public void addSpecialization (GeneralizableElement generalizableElement) {
        specialization.add(generalizableElement);
    }

}