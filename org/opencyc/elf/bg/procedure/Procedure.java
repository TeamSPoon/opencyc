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
 * Procedure contains the procedure framework that specific procedures extend.  Procedures
 * are not directly executable, they provide the source code for the action that
 * interprets the procedure.
 * </p>
 * 
 * @version $Id$
 * @author Stephen L. Reed  
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Procedure {
  
  //// Constructors

  /**
   * Creates a new instance of Procedure.
   * @param namespace the procedure namespace
   * @param name the procedure name
   * @param parameterTypes the types of the procedure parameters
   * @param outputType the type of the procedure output
   */
  public Procedure(String namespace, String name, ArrayList parameterTypes, Class outputType) {
    this.namespace = namespace;
    this.name = name;
    this.parameterTypes = parameterTypes;
  }

  //// Public Area

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
    Procedure that = (Procedure) obj;
    if (! this.namespace.equals(that.namespace))
      return false;
    else
      return (this.name.equals(that.name));
  }

  /**
   * Gets the procedure name
   *
   * @return the procedure name
   */
  public String getName () {
    return name;
  }

  /**
   * Sets the procedure name
   *
   * @param name the procedure name
   */
  public void setName (String name) {
    this.name = name;
  }

  /**
   * Gets the procedure namespace
   *
   * @return the procedure namespace
   */
  public String getNamespace () {
    return namespace;
  }

  /**
   * Sets the procedure namespace
   *
   * @param namespace the procedure namespace
   */
  public void setNamespace (String namespace) {
    this.namespace = namespace;
  }
  
  /**
   * Gets the parameter names each of which is a String
   *
   * @return the parameter names each of which is a String
   */
  public ArrayList getParameterNames () {
    return parameterNames;
  }

  /**
   * Sets the parameter names each of which is a String
   *
   * @param parameterNames the parameter names each of which is a String
   */
  public void setParameterNames (ArrayList parameterNames) {
    this.parameterNames = parameterNames;
  }

  /**
   * Gets the parameter types each of which is a Class
   *
   * @return the parameter types each of which is a Class
   */
  public ArrayList getParameterTypes () {
    return parameterTypes;
  }

  /**
   * Sets the parameter types each of which is a Class
   *
   * @param parameterTypes the parameter types each of which is a Class
   */
  public void setParameterTypes (ArrayList parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  /**
   * Gets the output type
   *
   * @return the output type
   */
  public Class getOutputType () {
    return outputType;
  }

  /**
   * Sets the output type
   *
   * @param outputType the output type
   */
  public void setOutputType (Class outputType) {
    this.outputType = outputType;
  }
  
  //// Protected Area

  //// Private Area
  
  //// Internal Rep
  
  /** the procedure name */
  protected String name;

  /** the procedure namespace */
  protected String namespace;

  /** the parameter names each of which is a String */
  protected ArrayList parameterNames;

  /** the parameter types each of which is a Class */
  protected ArrayList parameterTypes;

  /** the output type */
  protected Class outputType;

  //// Main
  

}