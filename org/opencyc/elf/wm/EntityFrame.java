package org.opencyc.elf.wm;

//// External Imports
import java.util.Iterator;

//// Internal Imports
import org.opencyc.elf.bg.state.State;


/**
 * <P>
 * EntityFrame is describes an entity with a list of stateVariable/values.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class EntityFrame {
  /**
   * Creates a new instance of EntityFrame.
   */
  public EntityFrame() {
    state = new State();
  }

  /**
   * Creates a new instance of EntityFrame given an existing entityFrame.
   * 
   * @param entityFrame the given entityFrame
   */
  public EntityFrame(EntityFrame entityFrame) {
    state = (State) entityFrame.state.clone();
  }

  //// Public Area

  /**
   * Returns true if the given object equals this entityFrame.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this entityFrame
   */
  public boolean equals(Object obj) {
    if (obj instanceof EntityFrame) {
      return state.equals(((EntityFrame) obj).getState());
    }
    else {
      return false;
    }
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[EntityFrame:\n");
    stringBuffer.append(state.toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Return the entityFrame state.
   * 
   * @return the entityFrame state
   */
  public State getState() {
    return state;
  }

  /** DOCUMENT ME! */
  protected State state;

  //// Main
}