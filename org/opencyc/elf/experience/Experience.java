package org.opencyc.elf.experience;

import java.sql.Timestamp;

import java.util.ArrayList;

import org.opencyc.elf.ELFObject;
import org.opencyc.elf.bg.state.State;
import org.opencyc.elf.goal.Goal;
import org.opencyc.elf.goal.Value;


/**
 * Provides the Experience container for the Elementary Loop Functioning
 * (ELF).<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Experience extends ELFObject {
  /** when the experience occurred */
  protected Timestamp timestamp;

  /** the intial state of the experience */
  protected State initialState;

  /** the final state of the experience */
  protected State finalState;

  /** the goal that was sought from the initial state */
  protected Goal goal;

  /** the state transitions from the initial state to the final state */
  protected ArrayList transitions;

  /** the value of the experience */
  protected Value value;

  /**
   * Constructs a new experience object.
   */
  public Experience() {
  }

  /**
   * Gets when the experience occurred
   * 
   * @return when the experience occurred
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Sets when the experience occurred
   * 
   * @param timestamp when the experience occurred
   */
  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the intial state of the experience
   * 
   * @return the intial state of the experience
   */
  public State getInitialState() {
    return initialState;
  }

  /**
   * Sets the intial state of the experience
   * 
   * @param initialState the intial state of the experience
   */
  public void setInitialState(State initialState) {
    this.initialState = initialState;
  }

  /**
   * Gets the final state of the experience
   * 
   * @return the final state of the experience
   */
  public State getFinalState() {
    return finalState;
  }

  /**
   * Sets the final state of the experience
   * 
   * @param finalState the final state of the experience
   */
  public void setFinalState(State finalState) {
    this.finalState = finalState;
  }

  /**
   * Gets the goal that was sought from the initial state
   * 
   * @return the goal that was sought from the initial state
   */
  public Goal getGoal() {
    return goal;
  }

  /**
   * Sets the goal that was sought from the initial state
   * 
   * @param goal the goal that was sought from the initial state
   */
  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  /**
   * Gets the state transitions
   * 
   * @return the state transitions
   */
  public ArrayList getTransitions() {
    return transitions;
  }

  /**
   * Sets the state transitions
   * 
   * @param transitions the state transitions
   */
  public void setTransitions(ArrayList transitions) {
    this.transitions = transitions;
  }

  /**
   * Gets the value of the experience
   * 
   * @return the value of the experience
   */
  public Value getValue() {
    return value;
  }

  /**
   * Sets the value of the experience
   * 
   * @param value the value of the experience
   */
  public void setValue(Value value) {
    this.value = value;
  }
}