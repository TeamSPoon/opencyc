package org.opencyc.elf.wm;

/**
 * Provides the simulator-predictor for the ELF WorldModel.<br>
 * 
 * @version $Id: SimulatorPredictor.java,v 1.1 2002/11/17 03:08:17 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class SimulatorPredictor {
  /** the world model which owns this simulator-predictor */
  protected WorldModel worldModel;

  /**
   * Constructs a new SimulatorPredictor object.
   */
  public SimulatorPredictor() {
  }

  /**
   * Gets the world model which owns this simulator-predictor
   * 
   * @return the world model which owns this simulator-predictor
   */
  public WorldModel getWorldModel() {
    return worldModel;
  }

  /**
   * Sets the world model which owns this simulator-predictor
   * 
   * @param worldModel the world model which owns this simulator-predictor
   */
  public void setWorldModel(WorldModel worldModel) {
    this.worldModel = worldModel;
  }
}