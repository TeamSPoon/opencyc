package org.opencyc.elf;

import java.util.*;

/**
 * Provides Sensory Perception for the Elementary Loop Functioning (ELF).<br>
 *
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

public class SensoryPerception extends NodeComponent {

    /**
     * Reference to the parent node's SensoryPerception object.  The
     * topmost SensoryPerception object has a value null here.
     */
    protected SensoryPerception parentSensoryPerception;

    /**
     * Reference to the child nodes' SensoryPerception objects.  The
     * lowest level SensoryPerception object has a value null here.
     */
    protected ArrayList childrenSensoryPerception;

    /**
     * Constructs a new SensoryPerception object.
     */
    public SensoryPerception() {
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "SensoryPerception for " + node.name;
    }

    /**
     * Gets the parent node's SensoryPerception object.
     *
     * @return the parent node's SensoryPerception object
     */
    public SensoryPerception getParentSensoryPerception () {
        return parentSensoryPerception;
    }

    /**
     * Sets the parent node's SensoryPerception object.
     *
     * @param parentSensoryPerception the parent node's SensoryPerception object
     */
    public void setParentSensoryPerception (SensoryPerception parentSensoryPerception) {
        this.parentSensoryPerception = parentSensoryPerception;
    }

    /**
     * Gets the child nodes' SensoryPerception objects.
     *
     * @return the child nodes' SensoryPerception objects
     */
    public ArrayList getChildrenSensoryPerception () {
        return childrenSensoryPerception;
    }

    /**
     * Sets the child nodes' SensoryPerception objects.
     *
     * @param childrenSensoryPerception the child nodes' SensoryPerception objects
     */
    public void setChildrenSensoryPerception (ArrayList childrenSensoryPerception) {
        this.childrenSensoryPerception = childrenSensoryPerception;
    }
}