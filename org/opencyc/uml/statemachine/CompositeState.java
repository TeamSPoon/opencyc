package org.opencyc.uml.statemachine;

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
     * the sub vertex
     */
    protected StateVertex subVertex;

    /**
     * Constructs a new CompositeState object.
     */
    protected CompositeState() {
    }

    /**
     * Gets whether concurrent processes
     *
     * @return whether concurrent processes
     */
    public boolean isConcurrent () {
        return isConcurrent;
    }

    /**
     * Sets whether concurrent processes
     *
     * @param isConcurrent whether concurrent processes
     */
    public void setIsConcurrent (boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    /**
     * Gets the sub vertex
     *
     * @return the sub vertex
     */
    public StateVertex getSubVertex () {
        return subVertex;
    }

    /**
     * Sets the sub vertex
     *
     * @param subVertex the sub vertex
     */
    public void setSubVertex (StateVertex subVertex) {
        this.subVertex = subVertex;
    }
}