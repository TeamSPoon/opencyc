package org.opencyc.elf.vj;

import org.opencyc.elf.NodeComponent;


/**
 * Provides Value Judgement for the Elementary Loop Functioning (ELF).<br>
 * 
 * @version $Id: ValueJudgement.java,v 1.1 2002/11/18 17:45:42 stephenreed Exp
 *          $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class ValueJudgement extends NodeComponent {
  /**
   * Constructs a new ValueJudgement object.
   */
  public ValueJudgement() {
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "ValueJudgement for " + node.getName();
  }
}