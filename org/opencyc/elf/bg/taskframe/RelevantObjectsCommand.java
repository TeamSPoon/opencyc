package org.opencyc.elf.bg.taskframe;

//// Internal Imports
import org.opencyc.cycobject.CycFort;
import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** RelevantObjectsCommand is a command that sets a given state variable to the possibly relevant set of objects 
 * with respect to the given set of relevant state variables and a relevancy relationship.  See 
 * RelevantObjectsLearningCommand.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
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
 * @version $Id$
 */
public class RelevantObjectsCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of RelevantObjectsCommand.
   *
   * @param name the name of this relevant objects command 
   * @param relevantStateVariables the list of relevant state variables
   * @param relevantObjects the state variable whose value will be set to the output list of relevant objects
   * @param relevancyRelationship the relevancy relationship 
   */
  public RelevantObjectsCommand(String name, 
                                List relevantStateVariables,
                                StateVariable relevantObjects,
                                CycFort relevancyRelationship) {
    this.name = name;
    this.relevantStateVariables = relevantStateVariables;
    this.relevantObjects = relevantObjects;
    this.relevancyRelationship = relevancyRelationship;
  }
  
  //// Public Area
  
  /** Executes this relevant objects command, returning the most relevant objects from
   * the list of possibly relevant objects.
   *
   * @param state the state
   */
  public void execute(State state) {
    List possiblyRelevantObjectsList;
    //TODO machine learning, for no possibilities are relevant
    List relevantObjectsList = new ArrayList();
    state.setStateValue(relevantObjects, relevantObjectsList);
  }  
  
  /** Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString () {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[RelevantObjectsCommand in context ");
    stringBuffer.append(relevantStateVariables.toString());
    stringBuffer.append(" with respect to ");
    stringBuffer.append(relevancyRelationship.cyclify());
    stringBuffer.append(" output state variable: ");
    stringBuffer.append(relevantObjects.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  /** Creates and returns a copy of this object.
   *
   * @return a copy of this object
   */
  public Object clone() {
    return new RelevantObjectsCommand(name, 
                                      relevantStateVariables, 
                                      relevantObjects,
                                      relevancyRelationship);
  }
  
  /** Gets the name of this relevant objects command
   *
   * @return the name of this relevant objects command
   */
  public String getName() {
    return name;
  }
  
  /** Gets the list of relevant state variables.
   *
   * @return the list of relevant state variables
   */
  public List getRelevantStateVariables () {
    return relevantStateVariables;
  }

  /** Gets the state variable whose value will be set to the output list of relevant objects.
   *
   * @return the state variable whose value will be set to the output list of relevant objects
   */
  public StateVariable getRelevantObjects () {
    return relevantObjects;
  }

  /** Gets the relevancy relationship
   *
   * @return the relevancy relationship
   */
  public CycFort getRelevancyRelationship () {
    return relevancyRelationship;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this relevant objects command */
  protected String name;
  
  /** the list of relevant state variables */
  protected List relevantStateVariables;
  
  /** the state variable whose value will be set to the output list of relevant objects */
  protected StateVariable relevantObjects;
  
  /** the relevancy relationship */
  protected CycFort relevancyRelationship;
  
  //// Main
  
}
