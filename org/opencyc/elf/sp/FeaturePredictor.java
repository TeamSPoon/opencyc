package org.opencyc.elf.sp;

/**
 * Provides the FeaturePredictor for ELF SensoryPerception.<br>
 * 
 * @version $Id: FeaturePredictor.java,v 1.1 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class FeaturePredictor {
  /** the sensory perception instance which owns this feature predictor */
  protected SensoryPerception sensoryPerception;

  /**
   * Constructs a new FeaturePredictor object.
   */
  public FeaturePredictor() {
  }

  /**
   * Gets the sensory perception instance which owns this feature predictor
   * 
   * @return the sensory perception instance which owns this feature predictor
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the sensory perception instance which owns this feature predictor
   * 
   * @param sensoryPerception the sensory perception instance which owns this
   *        feature predictor
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }
}