package org.opencyc.elf.bg.state;

//// Internal Imports
//// External Imports
import java.util.Iterator;


/**
 * <P>
 * Situation is designed to contain a situation described by a list of
 * stateVariable/values.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Situation {
  //// Constructors

  /**
   * Creates a new instance of Situation.
   */
  public Situation() {
    state = new State();
  }

  /**
   * Creates a new instance of Situation given an existing situation.
   * 
   * @param situation the given situation
   */
  public Situation(Situation situation) {
    state = (State) situation.state.clone();
  }

  //// Public Area

  /**
   * Returns true if the given object equals this situation.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this situation
   */
  public boolean equals(Object obj) {
    if (obj instanceof Situation) {
      return state.equals(((Situation) obj).getState());
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
    stringBuffer.append("[Situation:\n");
    stringBuffer.append(state.toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Return the situation state.
   * 
   * @return the situation state
   */
  public State getState() {
    return state;
  }

  /** DOCUMENT ME! */
  protected State state;

  //// Main
}