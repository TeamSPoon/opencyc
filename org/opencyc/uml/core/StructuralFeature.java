package org.opencyc.uml.core;

import org.opencyc.uml.datatypes.Multiplicity;

/**
 * StructuralFeature from the UML Core Package
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

public class StructuralFeature extends Feature {

    /**
     * the changeability of this feature
     */
    protected int changeability;

    public static final int CK_CHANGEABLE = 1;
    public static final int CK_FROZEN = 2;
    public static final int CK_ADD_ONLY = 3;

    /**
     * the multiplicity of this feature
     */
    protected Multiplicity multiplicity;

    /**
     * the ordering of this feature
     */
    protected int ordering;

    public static final int OK_UNORDERED = 1;
    public static final int OK_ORDERED = 2;

    /**
     * the target scope of this feature
     */
    protected int targetScope;

    public static final int SK_INSTANCE = 1;
    public static final int SK_CLASSIFIER = 1;

    /**
     * the type of this feature - either a Classifier or
     * Data Type (i. e. java class)
     */
    protected Object type;

    /**
     * Constructs a new StructuralFeature object
     */
    public StructuralFeature() {
    }

    /**
     * Gets the changeability of this feature
     *
     * @return the changeability of this feature
     */
    public int getChangeability () {
        return changeability;
    }

    /**
     * Sets the changeability of this feature
     *
     * @param changeability the changeability of this feature
     */
    public void setChangeability (int changeability) {
        this.changeability = changeability;
    }

    /**
     * Gets the multiplicity of this feature
     *
     * @return the multiplicity of this feature
     */
    public Multiplicity getMultiplicity () {
        return multiplicity;
    }

    /**
     * Sets the multiplicity of this feature
     *
     * @param multiplicity the multiplicity of this feature
     */
    public void setMultiplicity (Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Gets the ordering of this feature
     *
     * @return the ordering of this feature
     */
    public int getOrdering () {
        return ordering;
    }

    /**
     * Sets the ordering of this feature
     *
     * @param ordering the ordering of this feature
     */
    public void setOrdering (int ordering) {
        this.ordering = ordering;
    }

    /**
     * Gets the target scope of this feature
     *
     * @return the target scope of this feature
     */
    public int getTargetScope () {
        return targetScope;
    }

    /**
     * Sets the target scope of this feature
     *
     * @param targetScope the target scope of this feature
     */
    public void setTargetScope (int targetScope) {
        this.targetScope = targetScope;
    }

    /**
     * Gets the type of this feature - either a Classifier or
     * Data Type (i. e. java class)
     *
     * @return the type of this feature
     */
    public Object getType () {
        return type;
    }

    /**
     * Sets the type of this feature - either a Classifier or
     * Data Type (i. e. java class)
     *
     * @param type the type of this feature
     */
    public void setType (Object type) {
        this.type = type;
    }

}