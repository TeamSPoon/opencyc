package org.opencyc.elf;

import java.util.*;


/**
 * Provides the Node container for the Elementary Loop Functioning (ELF).<br>
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

public class Node {


    /**
     * Dictionary of nodes by name.
     * name --> Node
     */
    static HashMap nodes = new HashMap();

    /**
     * unique name of this node
     */
    protected String name;

    /**
     * purpose description
     */
    protected String purpose;

    /**
     * World Model for this node
     */
    protected WorldModel worldModel;

    /**
     * ValueJudgement for this node
     */
    protected ValueJudgement valueJudgement;

    /**
     * BehaviorGeneration for this node
     */
    protected BehaviorGeneration behaviorGeneration;

    /**
     * Actuator for this node
     */
    protected Actuator actuator;

    /**
     * Sensor for this node
     */
    protected Sensor sensor;

    /**
     * SensoryPerception
     */
    protected SensoryPerception sensoryPerception;

    /**
     * Parent node of this node for the task at hand.  The topmost
     * node has value null for parentNode.
     */
    protected Node parentNode;

    /**
     * Child nodes of this node for the task at hand.  The lowest
     * level nodes in the hierarchy have null for childNodes;
     */
    protected ArrayList childNodes;


    /**
     * Constructs a new Node object.
     */
    public Node() {
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Node: ");
        stringBuffer.append(name);
        stringBuffer.append("  purpose: ");
        stringBuffer.append(purpose);
        return stringBuffer.toString();
    }
}