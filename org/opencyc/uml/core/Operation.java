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
     * kind of concurrency
     */
    public int concurrency;

    public static final int CCK_SEQUENTIAL = 1;
    public static final int CCK_GUARDED = 2;
    public static final int CCK_CONCURRENT = 3;

    /**
     * indicates whether this operation is root
     */
    public boolean isRoot;

    /**
     * indicates whether this operation is a leaf
     */
    public boolean isLeaf;

    /**
     * indicates whether this operation is abstract
     */
    public boolean isAbstract;

    /**
     * specification for this operation
     */
    public String specification;

    /**
     * (Extension) the state machine implementing this Operation
     */
    public StateMachine stateMachine;

    /**
     * Constructs a new Operation object.
     */
    public Operation() {
    }
}