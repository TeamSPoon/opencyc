package org.opencyc.elf.sp;

import org.opencyc.elf.NodeComponent;


/**
 * Provides the Estimator for ELF SensoryPerception.<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class Estimator extends NodeComponent {
  /** the sensory perception instance which owns this estimator */
  protected SensoryPerception sensoryPerception;

  /**
   * Constructs a new Estimator object.
   */
  public Estimator() {
  }

  /**
   * Gets the sensory perception instance which owns this estimator
   * 
   * @return the sensory perception instance which owns this estimator
   */
  public SensoryPerception getSensoryPerception() {
    return sensoryPerception;
  }

  /**
   * Sets the sensory perception instance which owns this estimator
   * 
   * @param sensoryPerception the sensory perception instance which owns this
   *        estimator
   */
  public void setSensoryPerception(SensoryPerception sensoryPerception) {
    this.sensoryPerception = sensoryPerception;
  }
}