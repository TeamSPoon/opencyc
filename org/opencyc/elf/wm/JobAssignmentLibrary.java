package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.JobAssignment;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * JobAssignmentLibrary provides a library of non-temporal (for example spatial) task decompositions
 * among agents and resources.  There is a singleton instance.
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
  
  /** Creates a new instance of JobLibrary and stores it in the singleton instance. */
  public JobAssignmentLibrary() {
    jobAssignmentLibrary = this;
  }
  
  //// Public Area
  
  /** Gets the singleton job assignment library instance
   *
   * @return the singleton job assignment library instance
   */
  public static JobAssignmentLibrary getInstance () {
    return jobAssignmentLibrary;
  }

  /** Gets the job assignments that accomplishes the given action name.
   *
   * @param actionName the given action name
   * @return the job assignments that accomplishes the given action name
   */
  public List getJobAssignment (String actionName) {
    return (List) jobAssignmentDictionary.get(actionName);
  }
  
  //// Protected Area
  
  /** Adds the job assignment that accomplishes the given action name.
   *
   * @param actionName the given action name
   * @param jobAssignment the job assignment that accomplishes the given action name
   */
  public void addJobAssignment (String actionName, JobAssignment jobAssignment) {
    List jobAssignments = (List) jobAssignmentDictionary.get(actionName);
    if (jobAssignments == null)
      jobAssignments = new ArrayList();
    jobAssignments.add(jobAssignment);
    jobAssignmentDictionary.put(actionName, jobAssignments);
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the singleton job assignment library instance */
  protected static JobAssignmentLibrary jobAssignmentLibrary;
  
  /** the dictionary that associates a given action name with the list of schedules that accomplish it */ 
  protected HashMap jobAssignmentDictionary = new HashMap();
  
  //// Main
  
}
