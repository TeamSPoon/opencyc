package org.opencyc.elf.sp;

import java.util.ArrayList;

import org.opencyc.elf.NodeComponent;


/**
 * Provides Sensory Perception for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id: SensoryPerception.java,v 1.1 2002/11/18 17:45:40 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class SensoryPerception extends NodeComponent {
  /**
   * Reference to the parent node's SensoryPerception object.  The topmost
   * SensoryPerception object has a value null here.
   */
  protected SensoryPerception parentSensoryPerception;

  /**
   * Reference to the child nodes' SensoryPerception objects.  The lowest level
   * SensoryPerception object has a value null here.
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
    return "SensoryPerception for " + node.getName();
  }

  /**
   * Gets the parent node's SensoryPerception object.
   * 
   * @return the parent node's SensoryPerception object
   */
  public SensoryPerception getParentSensoryPerception() {
    return parentSensoryPerception;
  }

  /**
   * Sets the parent node's SensoryPerception object.
   * 
   * @param parentSensoryPerception the parent node's SensoryPerception object
   */
  public void setParentSensoryPerception(SensoryPerception parentSensoryPerception) {
    this.parentSensoryPerception = parentSensoryPerception;
  }

  /**
   * Gets the child nodes' SensoryPerception objects.
   * 
   * @return the child nodes' SensoryPerception objects
   */
  public ArrayList getChildrenSensoryPerception() {
    return childrenSensoryPerception;
  }

  /**
   * Sets the child nodes' SensoryPerception objects.
   * 
   * @param childrenSensoryPerception the child nodes' SensoryPerception
   *        objects
   */
  public void setChildrenSensoryPerception(ArrayList childrenSensoryPerception) {
    this.childrenSensoryPerception = childrenSensoryPerception;
  }
}