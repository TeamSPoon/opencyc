package org.opencyc.uml.core;

/**
 * Feature from the UML Core package.
 *
 * A feature is a property, such as operation or attribute, which is
 * encapsulated within a Classifier.
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

public abstract class Feature extends ModelElement {

    /**
     * Owner scope of this feature, specifying whether it appears
     * in in each Instance of the Classifier or whether it appears
     * only once for the entire Classifier.
     */
    protected int ownerScope;

    public static final int SK_INSTANCE = 1;
    public static final int SK_CLASSIFIER = 2;

    /**
     * the Classifier declaring this feature
     */
    protected Classifier owner;

    /**
     * Constructs a new Feature object.
     */
    public Feature() {
    }

    /**
     * Gets the owner scope of this feature.
     *
     * @return the owner scope of this feature
     */
    public int getOwnerScope () {
        return ownerScope;
    }

    /**
     * Sets the owner scope of this feature.
     *
     * @param ownerScope the owner scope of this feature
     */
    public void setOwnerScope (int ownerScope) {
        this.ownerScope = ownerScope;
    }

    /**
     * Gets the Classifier declaring this feature.
     *
     * @return the Classifier declaring this feature
     */
    public Classifier getOwner () {
        return owner;
    }

    /**
     * Sets the Classifier declaring this feature.
     *
     * @param owner the Classifier declaring this feature
     */
    public void setOwner (Classifier owner) {
        this.owner = owner;
    }
}