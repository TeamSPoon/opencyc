package org.opencyc.elf.sp;

/**
 * Provides the HypothesisEvaluator for ELF SensoryPerception.<br>
 * 
 * @version $Id: HypothesisEvaluator.java,v 1.1 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class HypothesisEvaluator {
  /** the sensory perception instance which owns this hypothesis evaluator */
  protected SensoryPerception sensoryPerception;

  /**
   * Constructs a new HypothesisEvaluator object.
   */
  public HypothesisEvaluator() {
  }

  /**
   * Gets the sensory perception instance which owns this hypothesis evaluator
   * 
   * @return the sensory perception instance which owns this hypothesis
   *         evaluator
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the sensory perception instance which owns this hypothesis evaluator
   * 
   * @param sensoryPerception the sensory perception instance which owns this
   *        hypothesis evaluator
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }
}