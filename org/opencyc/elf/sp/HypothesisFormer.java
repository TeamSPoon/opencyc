package org.opencyc.elf.sp;

import org.opencyc.elf.NodeComponent;


/**
 * Provides the HypothesisFormer for ELF SensoryPerception.<br>
 * 
 * @version $Id: HypothesisFormer.java,v 1.1 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class HypothesisFormer extends NodeComponent {
  /** the sensory perception instance which owns this hypothesis former */
  protected SensoryPerception sensoryPerception;

  /**
   * Constructs a new HypothesisFormer object.
   */
  public HypothesisFormer() {
  }

  /**
   * Gets the sensory perception instance which owns this hypothesis former
   * 
   * @return the sensory perception instance which owns this hypothesis former
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the sensory perception instance which owns this hypothesis former
   * 
   * @param sensoryPerception the sensory perception instance which owns this
   *        hypothesis former
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }
}