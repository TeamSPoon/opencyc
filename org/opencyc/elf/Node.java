package org.opencyc.elf;

import java.util.*;


/**
 * Provides the Node container for the Elementary Loop Functioning (ELF).<br>
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

public class Node extends ELFObject {


    /**
     * the dictionary of nodes by name
     * name --> Node
     */
    protected static HashMap nodes = new HashMap();

    /**
     * the purpose description
     */
    protected String purpose;

    /**
     * the World Model for this node
     */
    protected WorldModel worldModel;

    /**
     * the ValueJudgement for this node
     */
    protected ValueJudgement valueJudgement;

    /**
     * the BehaviorGeneration for this node
     */
    protected BehaviorGeneration behaviorGeneration;

    /**
     * the Actuator for this node
     */
    protected Actuator actuator;

    /**
     * the Sensor for this node
     */
    protected Sensor sensor;

    /**
     * the SensoryPerception
     */
    protected SensoryPerception sensoryPerception;

    /**
     * The parent node of this node for the task at hand.  The topmost
     * node has value null for parentNode.
     */
    protected Node parentNode;

    /**
     * The Child nodes of this node for the task at hand.  The lowest
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

    /**
     * Gets the dictionary of nodes by name
     *
     * @return the dictionary of nodes by name
     */
    public HashMap getNodes () {
        return nodes;
    }

    /**
     * Sets the dictionary of nodes by name
     *
     * @param nodes the dictionary of nodes by name
     */
    public void setNodes (HashMap nodes) {
        this.nodes = nodes;
    }

    /**
     * Gets the unique name of this node
     *
     * @return the unique name of this node
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the unique name of this node
     *
     * @param name the unique name of this node
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Gets the purpose description
     *
     * @return the purpose description
     */
    public String getPurpose () {
        return purpose;
    }

    /**
     * Sets the purpose description
     *
     * @param purpose the purpose description
     */
    public void setPurpose (String purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the World Model for this node
     *
     * @return the World Model for this node
     */
    public WorldModel getWorldModel () {
        return worldModel;
    }

    /**
     * Sets the World Model for this node
     *
     * @param worldModel the World Model for this node
     */
    public void setWorldModel (WorldModel worldModel) {
        this.worldModel = worldModel;
    }

    /**
     * Gets the ValueJudgement for this node
     *
     * @return the ValueJudgement for this node
     */
    public ValueJudgement getValueJudgement () {
        return valueJudgement;
    }

    /**
     * Sets the ValueJudgement for this node
     *
     * @param valueJudgement the ValueJudgement for this node
     */
    public void setValueJudgement (ValueJudgement valueJudgement) {
        this.valueJudgement = valueJudgement;
    }

    /**
     * Gets the BehaviorGeneration for this node
     *
     * @return the BehaviorGeneration for this node
     */
    public BehaviorGeneration getBehaviorGeneration () {
        return behaviorGeneration;
    }

    /**
     * Sets the BehaviorGeneration for this node
     *
     * @param behaviorGeneration the BehaviorGeneration for this node
     */
    public void setBehaviorGeneration (BehaviorGeneration behaviorGeneration) {
        this.behaviorGeneration = behaviorGeneration;
    }

    /**
     * Gets the Actuator for this node
     *
     * @return the Actuator for this node
     */
    public Actuator getActuator () {
        return actuator;
    }

    /**
     * Sets the Actuator for this node
     *
     * @param actuator the Actuator for this node
     */
    public void setActuator (Actuator actuator) {
        this.actuator = actuator;
    }

    /**
     * Gets the Sensor for this node
     *
     * @return the Sensor for this node
     */
    public Sensor getSensor () {
        return sensor;
    }

    /**
     * Sets the Sensor for this node
     *
     * @param sensor the Sensor for this node
     */
    public void setSensor (Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Gets the SensoryPerception
     *
     * @return the SensoryPerception
     */
    public SensoryPerception getSensoryPerception () {
        return sensoryPerception;
    }

    /**
     * Sets the SensoryPerception
     *
     * @param sensoryPerception the SensoryPerception
     */
    public void setSensoryPerception (SensoryPerception sensoryPerception) {
        this.sensoryPerception = sensoryPerception;
    }

    /**
     * Gets the parent node of this node for the task at hand
     *
     * @return the parent node of this node for the task at hand
     */
    public Node getParentNode () {
        return parentNode;
    }

    /**
     * Sets the parent node of this node for the task at hand
     *
     * @param parentNode the parent node of this node for the task at hand
     */
    public void setParentNode (Node parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Gets the Child nodes of this node for the task at hand
     *
     * @return the Child nodes of this node for the task at hand
     */
    public ArrayList getChildNodes () {
        return childNodes;
    }

    /**
     * Sets the Child nodes of this node for the task at hand
     *
     * @param childNodes the Child nodes of this node for the task at hand
     */
    public void setChildNodes (ArrayList childNodes) {
        this.childNodes = childNodes;
    }
}