package org.opencyc.elf.sp;

/**
 * Provides the FeatureExtractor for ELF SensoryPerception.<br>
 * 
 * @version $Id: FeatureExtractor.java,v 1.1 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class FeatureExtractor {
  /** the sensory perception instance which owns this feature extractor */
  protected SensoryPerception sensoryPerception;

  /**
   * Constructs a new FeatureExtractor object.
   */
  public FeatureExtractor() {
  }

  /**
   * Gets the sensory perception instance which owns this feature extractor
   * 
   * @return the sensory perception instance which owns this feature extractor
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the sensory perception instance which owns this feature extractor
   * 
   * @param sensoryPerception the sensory perception instance which owns this
   *        feature extractor
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }
}