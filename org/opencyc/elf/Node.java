package org.opencyc.elf;

import java.util.ArrayList;
import java.util.HashMap;

import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.sp.SensoryPerception;
import org.opencyc.elf.vj.ValueJudgement;
import org.opencyc.elf.wm.WorldModel;


/**
 * Provides the Node container for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Node extends ELFObject {
  /** the dictionary of nodes by name name --> Node */
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
  protected ArrayList childNodes;

  /** the list of sibling nodes at this ELF resolution level */
  protected ArrayList siblingNodes;

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
  public ArrayList getChildNodes() {
    return childNodes;
  }

  /**
   * Sets the Child nodes of this node for the task at hand
   * 
   * @param childNodes the Child nodes of this node for the task at hand
   */
  public void setChildNodes(ArrayList childNodes) {
    this.childNodes = childNodes;
  }

  /**
   * Gets the list of sibling nodes at this ELF resolution level
   * 
   * @return the list of sibling nodes at this ELF resolution level
   */
  public ArrayList getSiblingNodes() {
    return siblingNodes;
  }

  /**
   * Sets the list of sibling nodes at this ELF resolution level
   * 
   * @param siblingNodes the list of sibling nodes at this ELF resolution level
   */
  public void setSiblingNodes(ArrayList siblingNodes) {
    this.siblingNodes = siblingNodes;
  }
}