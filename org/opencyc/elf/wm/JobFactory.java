package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.DirectActuator;
import org.opencyc.elf.bg.command.Command;
import org.opencyc.elf.bg.command.Parameter;
import org.opencyc.elf.bg.planner.Job;
import org.opencyc.elf.bg.planner.Resource;
import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.s.DirectSensor;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** JobFactory populates the job library.  There is a singleton instance.
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
public class JobFactory {
  
  //// Constructors
  
  /** Creates a new instance of JobFactory and stores it in the singleton instance. */
  public JobFactory() {
    jobFactory = this;
  }
  
  //// Public Area
  
  /** Gets the job factory singleton instance.
   *
   * @return the job factory singleton instance
   */
  public JobFactory getInstance () {
    return jobFactory;
  }
  
  /** Poplulates the job library. */
  public void populateJobLibrary () {
    // converse with user
    Command command = ActionLibrary.getInstance().getAction(Action.CONVERSE_WITH_USER);
    List requiredResources = new ArrayList();
    requiredResources.add(ResourcePool.getInstance().getResource(Resource.CONSOLE));
    String directActuatorName = DirectActuator.CONSOLE_OUTPUT;
    String directSensorName = DirectSensor.CONSOLE_INPUT;
    Job job = new Job(command, requiredResources, directActuatorName, directSensorName);
    List jobSet = new ArrayList();
    jobSet.add(job);
    JobLibrary.getInstance().addJobSet(Action.CONVERSE_WITH_USER, jobSet);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the job factory singleton instance */
  protected static JobFactory jobFactory;
  
  //// Main
  
}
