package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.sp.SensoryPerception;

import org.opencyc.elf.vj.ValueJudgement;

import org.opencyc.elf.wm.WorldModel;

//// External Imports
import java.util.HashMap;
import java.util.List;

import java.util.logging.Logger;

/**
 * Provides the Node container for the Elementary Loop Functioning (ELF).
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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

  //// Constructors
  
  /**
   * Constructs a new Node object.
   */
  public Node() {
    logger = Logger.getLogger("org.opencyc.elf.Node");
  }

  /**
   * Constructs a new Node object with the given name.
   *
   * @param name the given node name
   */
  public Node(String name) {
    this.name = name;
    logger = Logger.getLogger("org.opencyc.elf.Node");
  }
  
  //// Public Area
  
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
  public HashMap getNodes() {
    return nodes;
  }

  /**
   * Sets the dictionary of nodes by name
   * 
   * @param nodes the dictionary of nodes by name
   */
  public void setNodes(HashMap nodes) {
    this.nodes = nodes;
  }

  /**
   * Gets the unique name of this node
   * 
   * @return the unique name of this node
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the unique name of this node
   * 
   * @param name the unique name of this node
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the purpose description
   * 
   * @return the purpose description
   */
  public String getPurpose() {
    return purpose;
  }

  /**
   * Sets the purpose description
   * 
   * @param purpose the purpose description
   */
  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  /**
   * Gets the World Model for this node
   * 
   * @return the World Model for this node
   */
  public WorldModel getWorldModel() {
    return worldModel;
  }

  /**
   * Sets the World Model for this node
   * 
   * @param worldModel the World Model for this node
   */
  public void setWorldModel(WorldModel worldModel) {
    this.worldModel = worldModel;
  }

  /**
   * Gets the ValueJudgement for this node
   * 
   * @return the ValueJudgement for this node
   */
  public ValueJudgement getValueJudgement() {
    return valueJudgement;
  }

  /**
   * Sets the ValueJudgement for this node
   * 
   * @param valueJudgement the ValueJudgement for this node
   */
  public void setValueJudgement(ValueJudgement valueJudgement) {
    this.valueJudgement = valueJudgement;
  }

  /**
   * Gets the BehaviorGeneration for this node
   * 
   * @return the BehaviorGeneration for this node
   */
  public BehaviorGeneration getBehaviorGeneration() {
    return behaviorGeneration;
  }

  /**
   * Sets the BehaviorGeneration for this node
   * 
   * @param behaviorGeneration the BehaviorGeneration for this node
   */
  public void setBehaviorGeneration(BehaviorGeneration behaviorGeneration) {
    this.behaviorGeneration = behaviorGeneration;
  }

  /**
   * Gets the SensoryPerception
   * 
   * @return the SensoryPerception
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the SensoryPerception
   * 
   * @param sensoryPerception the SensoryPerception
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }

  /**
   * Gets the parent node of this node for the task at hand
   * 
   * @return the parent node of this node for the task at hand
   */
  public Node getParentNode() {
    return parentNode;
  }

  /**
   * Sets the parent node of this node for the task at hand
   * 
   * @param parentNode the parent node of this node for the task at hand
   */
  public void setParentNode(Node parentNode) {
    this.parentNode = parentNode;
  }

  /**
   * Gets the Child nodes of this node for the task at hand
   * 
   * @return the Child nodes of this node for the task at hand
   */
  public List getChildNodes() {
    return childNodes;
  }

  /**
   * Sets the Child nodes of this node for the task at hand
   * 
   * @param childNodes the Child nodes of this node for the task at hand
   */
  public void setChildNodes(List childNodes) {
    this.childNodes = childNodes;
  }

  /**
   * Gets the list of sibling nodes at this ELF resolution level
   * 
   * @return the list of sibling nodes at this ELF resolution level
   */
  public List getSiblingNodes() {
    return siblingNodes;
  }

  /**
   * Sets the list of sibling nodes at this ELF resolution level
   * 
   * @param siblingNodes the list of sibling nodes at this ELF resolution level
   */
  public void setSiblingNodes(List siblingNodes) {
    this.siblingNodes = siblingNodes;
  }
  
  /**
   * Gets the logger
   *
   * @return the logger
   */
  public Logger getLogger () {
    return logger;
  }
  
  /** the converse with user node name */
  public static final String CONVERSE_WITH_USER = "converse with user";
  
  //// Protected Area
  
  //// Private Area

  //// Internal Rep
  
  /** the node name */
  protected String name;
  
  /** the dictionary of nodes by name, name --> Node */
  protected static HashMap nodes = new HashMap();

  /** the purpose description */
  protected String purpose;

  /** the World Model for this node */
  protected WorldModel worldModel;

  /** the ValueJudgement for this node */
  protected ValueJudgement valueJudgement;

  /** the BehaviorGeneration for this node */
  protected BehaviorGeneration behaviorGeneration;

  /** the SensoryPerception */
  protected SensoryPerception sensoryPerception;

  /**
   * The parent node of this node for the task at hand.  The topmost node has
   * value null for parentNode.
   */
  protected Node parentNode;
  /**
   * The Child nodes of this node for the task at hand.  The lowest level nodes
   * in the hierarchy have null for childNodes;
   */
  protected List childNodes;

  /** the list of sibling nodes at this ELF resolution level */
  protected List siblingNodes;
  
  /** the logger */
  protected Logger logger;
}