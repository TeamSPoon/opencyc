package org.opencyc.uml.statemachine;

import java.util.*;

/**
 * CompositeState from the UML State_Machines package.
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

public class CompositeState extends State {

    /**
     * indicates concurrent processes
     */
    protected boolean isConcurrent;

    /**
     * A derived boolean value that indicates whether this composite state
     * is a direct substate of a concurrent state.
     */
    protected boolean isRegion;

    /**
     * the set of state vertices that are owned by this composite state
     */
    protected ArrayList subVertex = new ArrayList();

    /**
     * Constructs a new CompositeState object.
     */
    protected CompositeState() {
    }

    /**
     * Gets whether concurrent processes.
     *
     * @return whether concurrent processes
     */
    public boolean isConcurrent () {
        return isConcurrent;
    }

    /**
     * Sets whether concurrent processes.
     *
     * @param isConcurrent whether concurrent processes
     */
    public void setIsConcurrent (boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    /**
     * Gets the set of state vertices that are owned by this composite state.
     *
     * @return the set of state vertices that are owned by this composite state
     */
    public ArrayList getSubVertex () {
        return subVertex;
    }

    /**
     * Sets the set of state vertices that are owned by this composite state.
     *
     * @param subVertex the set of state vertices that are owned by this composite state
     */
    public void setSubVertex (ArrayList subVertex) {
        this.subVertex = subVertex;
    }

    /**
     * Gets whether this composite state
     * is a direct substate of a concurrent state.
     *
     * @return whether this composite state
     * is a direct substate of a concurrent state
     */
    public boolean isRegion () {
        return isRegion;
    }

    /**
     * Sets whether this composite state
     * is a direct substate of a concurrent state.
     *
     * @param isRegion whether this composite state
     * is a direct substate of a concurrent state
     */
    public void setIsRegion (boolean isRegion) {
        this.isRegion = isRegion;
    }
}