package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.Job;

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * JobLibrary provides a library of non-temporal (for example spatial) task decompositions
 * among agents and resources.  For each command name there is an entry that is a list of alternative 
 * sets of jobs. There is a singleton instance of the job library.
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
public class JobLibrary {
  
  //// Constructors
  
  /** Creates a new instance of JobLibrary and stores it in the singleton instance. */
  public JobLibrary() {
    jobLibrary = this;
  }
  
  //// Public Area
  
  /** Gets the singleton job library instance
   *
   * @return the singleton job library instance
   */
  public static JobLibrary getInstance () {
    return jobLibrary;
  }

  /** Gets the job sets that accomplishes the given command name.
   *
   * @param commandName the given command name
   * @return the job sets that accomplishes the given command name
   */
  public List getJobSet (String commandName) {
    return (List) jobDictionary.get(commandName);
  }
  
  //// Protected Area
  
  /** Adds a job set that accomplishes the given command name.
   *
   * @param actionName the given command name
   * @param jobSet the job set
   */
  public void addJobSet (String actionName, List jobSet) {
    List jobs = (List) jobDictionary.get(actionName);
    if (jobs == null)
      jobs = new ArrayList();
    jobs.add(jobSet);
    jobDictionary.put(actionName, jobs);
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the singleton job library instance */
  protected static JobLibrary jobLibrary;
  
  /** the dictionary that associates a given command name with the list of alterative job sets that accomplish it */ 
  protected HashMap jobDictionary = new HashMap();
  
  //// Main
  
}
