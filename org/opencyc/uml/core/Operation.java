package org.opencyc.uml.core;

import org.opencyc.uml.statemachine.*;

/**
 * Operation from the UML Core package.
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

public class Operation extends BehavioralFeature {

    /**
     * the kind of concurrency
     */
    protected int concurrency;

    public static final int CCK_SEQUENTIAL = 1;
    public static final int CCK_GUARDED = 2;
    public static final int CCK_CONCURRENT = 3;

    /**
     * When true, indicates that this operation must not inherit
     * a declaration of the same operation.
     */
    protected boolean isRoot;

    /**
     * When true, indicates that the implementation of this operation
     * may not be overriden by a descendant class.
     */
    protected boolean isLeaf;

    /**
     * indicates whether this operation is abstract
     */
    protected boolean isAbstract;

    /**
     * the specification for this operation
     */
    protected String specification;

    /**
     * (Extension) the state machine implementing this Operation
     */
    protected StateMachine stateMachine;

    /**
     * Constructs a new Operation object.
     */
    public Operation() {
    }

    /**
     * Gets the kind of concurrency.
     *
     * @return the kind of concurrency
     */
    public int getConcurrency () {
        return concurrency;
    }

    /**
     * Sets the kind of concurrency.
     *
     * @param concurrency the kind of concurrency
     */
    public void setConcurrency (int concurrency) {
        this.concurrency = concurrency;
    }

    /**
     * Gets whether this operation must not inherit
     * a declaration of the same operation
     *
     * @return whether this operation must not inherit
     * a declaration of the same operation
     */
    public boolean isRoot () {
        return isRoot;
    }

    /**
     * Sets whether this operation must not inherit
     * a declaration of the same operation
     *
     * @param isLeaf whether this operation must not inherit
     * a declaration of the same operation
     */
    public void setIsRoot (boolean isRoot) {
        this.isRoot = isRoot;
    }

    /**
     * Gets whether the implementation of this operation
     * may not be overriden by a descendant class
     *
     * @return whether the implementation of this operation
     * may not be overriden by a descendant class
     */
    public boolean isLeaf () {
        return isLeaf;
    }

    /**
     * Sets whether the implementation of this operation
     * may not be overriden by a descendant class
     *
     * @param isLeaf whether the implementation of this operation
     * may not be overriden by a descendant class
     */
    public void setIsLeaf (boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * Gets whether this operation is abstract
     *
     * @return whether this operation is abstract
     */
    public boolean isAbstract () {
        return isAbstract;
    }

    /**
     * Sets whether this operation is abstract
     *
     * @param isAbstract whether this operation is abstract
     */
    public void setIsAbstract (boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * Gets the specification for this operation
     *
     * @return the specification for this operation
     */
    public String getSpecification () {
        return specification;
    }

    /**
     * Sets the specification for this operation
     *
     * @param specification the specification for this operation
     */
    public void setSpecification (String specification) {
        this.specification = specification;
    }

    /**
     * Gets the state machine implementing this Operation
     *
     * @return the state machine implementing this Operation
     */
    public StateMachine getstateMachine () {
        return stateMachine;
    }

    /**
     * Sets the state machine implementing this Operation
     *
     * @param stateMachine the state machine implementing this Operation
     */
    public void setStateMachine (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

}