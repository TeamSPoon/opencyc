package org.opencyc.elf;

import java.util.ArrayList;

import org.opencyc.elf.bg.procedure.Procedure;


/**
 * Provides common attributes and behavior for Elementary Loop Functioning
 * (ELF) node components.<br>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public abstract class NodeComponent extends ELFObject {
  /**
   * The default verbosity of this object's output.  0 --> quiet ... 9 ->
   * maximum diagnostic input.
   */
  public static final int DEFAULT_VERBOSITY = 3;

  /**
   * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   */
  protected int verbosity = DEFAULT_VERBOSITY;

  /** Reference to the ELF Node which contains this object. */
  protected Node node;

  /** indicates a pending interruption */
  protected boolean pendingInterruption = false;

  /** the interruption procedure to execute */
  protected Procedure interruptionRequest;

  /**
   * Gets the ELF Node which contains this object.
   * 
   * @return the ELF Node which contains this object
   */
  public Node getNode() {
    return node;
  }

  /**
   * Requests an interruption of the current processing of this node component
   * to execute the given procedure and to return the the output value.
   * 
   * @param interruptionRequest the given interruption procedure to execute
   * 
   * @return the output value
   */
  public Object interrupt(Procedure interruptionRequest) {
    this.interruptionRequest = interruptionRequest;
    pendingInterruption = true;

    try {
      while (true) {
        Thread.sleep(100);

        if (!pendingInterruption) {
          break;
        }
      }
    }
     catch (InterruptedException e) {
    }

    return interruptionRequest.execute(new ArrayList());
  }

  /**
   * Sets the ELF Node which contains this object.
   * 
   * @param node the ELF Node which contains this object
   */
  public void setNode(Node node) {
    this.node = node;
  }

  /**
   * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   * 
   * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
   */
  public void setVerbosity(int verbosity) {
    this.verbosity = verbosity;
  }
}