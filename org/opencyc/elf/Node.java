package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.BehaviorGeneration;
import org.opencyc.elf.bg.Status;

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
   * Performs the commanded task.
   *
   * @taskCommand the commanded task
   */
  public void doTask (TaskCommand taskCommand) {
    //TODO
    //send via channel bg.doTask(taskCommand)
  }
  
  /**
   * Receives data about an object from Sensory Perception.
   *
   * @param obj the perceived object
   * @param data the sensed data for the perceived object
   */
  public void receiveSensoryPerceptionData (Object obj, Object data) {
    //TODO
    //receive via channel
  }
  
  /**
   * Receives status from a subnode.
   *
   * @param status the subnode's status
   */
  public void receiveSubNodeStatus (Status status) {
    //TODO
    //receive via channel
  }
    
  /**
   * Receives a request for a Knowledge Base object.
   *
   * @parm obj the object about which information is requested from the Knowledge Base
   */
  public void receiveRequestKBObject (Object obj) {
    //TODO
    //receive via channel
  }

  /**
   * Receives the requested Knowledge Base object.
   *
   * @parm obj the requested Knowledge Base object
   */
  public void receiveKBObject (Object obj) {
    //TODO
    //receive via channel
  }
  
  /**
   * Receives operator input.
   *
   * @param input the input from the operator
   */
  public void operatorInput(String input) {
    //TODO
    //receive via channel
  }
  
  /**
   * Sends Sensory Perception data about a perceived object.
   *
   * @param obj the perceived object
   * @param data the data about the perceived object
   */
  public void sendSensoryPerceptionData (Object obj, Object data) {
    //TODO
    //send via channel
  }
  
  /**
   * Sends the given list of controlled resources and the given task command to
   * a sub node.
   *
   * @param controlledResources the given list of controlled resources
   * @param taskCommnd the given task command
   */
  public void doSubTask (ArrayList controlledResources, TaskCommand taskCommnd) {
    //TODO
    //send via channel
  }
  
  /**
   * Forwards a request for a Knowledge Base object.
   *
   * @parm obj the object about which information is requested from the Knowledge Base
   */
  public void forwardRequestKBObject (Object obj) {
    //TODO
    //send via channel
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
  
  //// Protected Area
  
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
  
  //// Private Area
  //// Internal Rep
}