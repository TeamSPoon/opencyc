package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.state.State;


//// External Imports

/**
 * <P>
 * Resource contains the attributes and behavior of a resource that must be
 * allocated to an agent in order for that perform a given task.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author reed <p><p><p><p><p>
 */
public class Resource {
  //// Constructors

  /**
   * Creates a new instance of Resource
   */
  public Resource() {
  }

  //// Public Area

  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Resource)) {
      return false;
    }

    //TODO
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[");
    stringBuffer.append(resourceName);
    stringBuffer.append("\n");
    stringBuffer.append(state.toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Gets the resource name
   * 
   * @return the resource name
   */
  public String getResourceName() {
    return resourceName;
  }

  /**
   * Sets the resource name
   * 
   * @param resourceName the resource name
   */
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  /**
   * Gets the state of the resource
   * 
   * @return the state of the resource
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the state of the resource
   * 
   * @param state the state of the resource
   */
  public void setState(State state) {
    this.state = state;
  }

  //// Protected Area

  /** the resource name */
  protected String resourceName;

  /** the state of the resource */
  protected State state;

  //// Private Area
  //// Internal Rep
}