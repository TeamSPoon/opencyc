package org.opencyc.uml.core;

import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;

/**
 * Parameter from the UML Core package.
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

public class Parameter extends ModelElement {

    /**
     * the default value for this parameter
     */
    protected Expression defaultValue;

    /**
     * the parameter direction kind
     */
    protected int kind;

    public static final int PDK_IN = 1;
    public static final int PDK_INOUT = 2;
    public static final int PDK_OUT = 3;
    public static final int PDK_RETURN = 4;

    /**
     * the behavioral feature using this parameter
     */
    protected BehavioralFeature behavioralFeature;

    /**
     * the type of this parameter
     */
    protected Class type;

    /**
     * Constructs a new Parameter object.
     */
    public Parameter() {
    }

    /**
     * Gets the default value for this parameter
     *
     * @return the default value for this parameter
     */
    public Expression getDefaultValue () {
        return defaultValue;
    }

    /**
     * Sets the default value for this parameter
     *
     * @param defaultValue the default value for this parameter
     */
    public void setDefaultValue (Expression defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the parameter direction kind
     *
     * @return the parameter direction kind
     */
    public int getKind () {
        return kind;
    }

    /**
     * Sets the parameter direction kind
     *
     * @param kind the parameter direction kind
     */
    public void setKind (int kind) {
        this.kind = kind;
    }

    /**
     * Gets the behavioral feature using this parameter
     *
     * @return the behavioral feature using this parameter
     */
    public BehavioralFeature getBehavioralFeature () {
        return behavioralFeature;
    }

    /**
     * Sets the behavioral feature using this parameter
     *
     * @param behavioralFeature the behavioral feature using this parameter
     */
    public void setBehavioralFeature (BehavioralFeature behavioralFeature) {
        this.behavioralFeature = behavioralFeature;
    }

    /**
     * Gets the type of this parameter
     *
     * @return the type of this parameter
     */
    public Class getType () {
        return type;
    }

    /**
     * Sets the type of this parameter
     *
     * @param type the type of this parameter
     */
    public void setType (Class type) {
        this.type = type;
    }

}