package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.sp.SensoryPerception;

import org.opencyc.elf.vj.ValueJudgement;

import org.opencyc.elf.wm.WorldModel;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides the Node container for the Elementary Loop Functioning (ELF).<br>
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
public class Node extends ELFObject {

  //// Constructors
  
  /**
   * Constructs a new Node object.
   */
  public Node() {
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
  
  //// Protected Area
  
  
  // remove below:
  
  /**
   * Sends the do task message to behavior generation.
   */
  protected void doTask () {
    //TODO
    //send via channel to behavior generation
    // TaskCommand taskCommand
    // send doTask(taskCommand) to behaviorGeneration
  }
  
  /**
   * Receives the sensory perception data message from the next lower level node.
   */
  protected void receiveSensoryPerceptionData () {
    //TODO
    //receive via channelfrom the next lower level node
    // Object obj
    // Object data
  }
  
  /**
   * Receives subnode status message from ?
   */
  protected void receiveSubNodeStatus () {
    //TODO
    //receive via channel from ?
    // Status status
  }
    
  /**
   * Receives the request knowledge base object message from ?
   */
  public void receiveRequestKBObject () {
    //TODO
    //receive via channel from ?
    // Object obj
  }

  /**
   * Receives the knowledge base object message from ?
   */
  protected void receiveKBObject () {
    //TODO
    //receive via channel from ?
    // Object obj
  }
  
  /**
   * Receives operator input message from ?.
   */
  protected void operatorInput() {
    //TODO
    //receive via channel
    // String input
  }
  
  /**
   * Sends the sensory perception data message to the next highest level node, on
   * behalf of sensory perception.
   */
  protected void sendSensoryPerceptionData () {
    //TODO
    //received via channel from sensory perception
    //send via channel to the node at the next highest level
    // Object obj
    // Object data
    // send receiveSensoryPerceptionData(obj, data) to (the next highest level) node
  }
  
  /**
   * Sends the do subtask message to the appropriate subnode.  The message was received 
   * from behavior generation and originated from an executor.
   */
  protected void doSubTask () {
    //TODO
    //receive via channel from behavior generation
    //send via channel to the appropriate sub node
    // ArrayList controlledResources
    // TaskCommand
    //
  }
  
  /**
   * Forwards the request knowledge base object message from ? to ?
   */
  protected void forwardRequestKBObject () {
    //TODO
    // receive via channel from ?
    // send via channel to ?
    // Object obj
  }
  
  /**
   * Sends the knowledge base object message to ?
   */
  protected void sendKBObject () {
    //TODO
    //send via channel to ?
    // Object obj
  }
  
  /**
   * Sends the status message to ?.  This message originates from the job
   * assigner and is forwarded to the node by behavior generation.
   */
  protected void status () {
    //TODO
    //received via channel from behavior generation
    //send via channel to ?
    // ArrayList controlledResources
    // Status status
  }
  
  /**
   * Sends operator output message to ?
   */
  protected void operatorOutput () {
    //TODO
    //send via channel to ?  
    // String output
  }
  
  //// Private Area

  //// Internal Rep
  
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
  
}