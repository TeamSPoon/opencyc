package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.ActionFactory;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <P>
 * JobAssignmentLibrary provides a library of non-temporal (e.g. spatial) task decompositions
 * among agents and resources.
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
public class JobAssignmentLibrary {
  
  //// Constructors
  
  /** Creates a new instance of JobLibrary. */
  public JobAssignmentLibrary() {
  }
  
  //// Public Area
  
  /**
   * Initializes the job library.
   */
  public void initialize() {
    // converse with user
    JobAssignment jobAssignment = new JobAssignment();
    jobAssignment.setActionName(Action.CONVERSE_WITH_USER);
    ArrayList requiredResources = new ArrayList();
    requiredResources.add(ResourceFactory.getResource(Resource.CONSOLE));
    jobAssignment.setRequiredResources(requiredResources);
    ActionFactory actionFactory = new ActionFactory();
    Action action = actionFactory.makeConsolePromptedInput();
    jobAssignment.setActionForScheduling(action);
  }
 
  /**
   * Gets the job assignment that accomplishes the given action name.
   *
   * @param action the given action name
   * @return the job assignment that accomplishes the given action name
   */
  public JobAssignment getJobAssignment (String actionName) {
    return (JobAssignment) jobAssignmentDictionary.get(actionName);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the dictionary that associates a given action name with the list of schedules that
   * accomplish it
   */
  protected HashMap jobAssignmentDictionary = new HashMap();
  
  //// Main
  
}
