package org.opencyc.elf.bg.procedure;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//// Internal Imports
import org.opencyc.elf.bg.state.Situation;
import org.opencyc.elf.bg.state.State;


/**
 * <P>
 * Procedure is designed to...
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public abstract class Procedure {
  //// Constructors

  /**
   * Creates a new instance of Procedure.
   * @param namespace DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param parameterTypes DOCUMENT ME!
   * @param outputType DOCUMENT ME!
   */
  public Procedure(String namespace, String name, ArrayList parameterTypes, Class outputType) {
    this.namespace = namespace;
    this.name = name;
    this.parameterTypes = parameterTypes;
    state = new State();
    register();
  }

  //// Public Area

  /**
   * Executes this procedure given its parameter list
   * 
   * @param inputs the list of parameter values
   * 
   * @return the output of the procedure
   */
  public abstract Object execute(ArrayList inputs);

  //// Protected Area

  /** the class dictionary of execute methods */
  protected static HashMap executeMethodDictionary;

  /** the procedure name */
  protected String name;

  /** the procedure namespace */
  protected String namespace;

  /** the parameter types */
  protected ArrayList parameterTypes;

  /** the output type */
  protected Class outputType;

  /**
   * Registers this procedure's execute method so that it may be subsequently
   * called with its parameter list and without its list of parameter types.
   */
  protected void register() {
  }

  /**
   * Returns true if the given object equals this procedure.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this procedure
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Procedure)) {
      return false;
    }

    Procedure thatProcedure = (Procedure) obj;

    //TODO
    return true;
  }

  /** DOCUMENT ME! */
  protected State state;
}