package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.core.*;


/**
 * StateVertex from the UML State_Machines package.
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

public class StateVertex extends ModelElement {

    /**
     * the container of this state vertex
     */
    protected CompositeState container;

    /**
     * the outgoing Transitions from this state vertex
     */
    protected ArrayList outgoing = new ArrayList();

    /**
     * the incoming Transitions into this state vertex
     */
    protected ArrayList incoming = new ArrayList();


    /**
     * Constructs a new StateVertex object.
     */
    public StateVertex() {
    }

    /**
     * Gets the container of this state vertex
     *
     * @return the container of this state vertex
     */
    public CompositeState getContainer () {
        return container;
    }

    /**
     * Sets the container of this state vertex
     *
     * @param container the container of this state vertex
     */
    public void setContainer (CompositeState container) {
        this.container = container;
    }

    /**
     * Gets the outgoing Transitions from this state vertex
     *
     * @return the outgoing Transitions from this state vertex
     */
    public List getOutgoing () {
        return outgoing;
    }

    /**
     * Sets the outgoing Transitions from this state vertex
     *
     * @param outgoing the outgoing Transitions from this state vertex
     */
    public void setOutgoing (ArrayList outgoing) {
        this.outgoing = outgoing;
    }

    /**
     * Gets the incoming Transitions into this state vertex
     *
     * @return the incoming Transitions into this state vertex
     */
    public List getIncoming () {
        return incoming;
    }

    /**
     * Sets the incoming Transitions into this state vertex
     *
     * @param incoming the incoming Transitions into this state vertex
     */
    public void setIncoming (ArrayList incoming) {
        this.incoming = incoming;
    }
}