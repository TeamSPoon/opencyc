package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.taskframe.TaskFrame;

//// External Imports
import java.util.HashMap;

/** TaskFrameLibrary provides the library of task frames.  There is a singleton instance.
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
public class TaskFrameLibrary {
  
  //// Constructors
  
  /** Creates a new instance of TaskFrameLibrary and stores it in the singleton instance. */
  public TaskFrameLibrary() {
    taskFrameLibrary = this;
  }
  
  //// Public Area
  
  /** Gets the task frame library singleton instance.
   *
   * @return the task frame library singleton instance
   */
  public static TaskFrameLibrary getInstance() {
    return taskFrameLibrary;
  }
  
  /**
   * Gets the task frame given the task name.
   *
   * @param taskName the task name
   * @return the task frame given the task name
   */
  public TaskFrame getTaskFrame (String taskName) {
    TaskFrame taskFrame = (TaskFrame) taskFrameDictionary.get(taskName);
    TaskFrame clonedTaskFrame = (TaskFrame) taskFrame.clone();
    clonedTaskFrame.generateTaskId();
    return clonedTaskFrame;
  }
    
  /** Gets the root task frame
   *
   * @return the root task frame
   */
  public TaskFrame getRootTaskFrame () {
    return rootTaskFrame;
  }

  /** Sets the root task frame
   *
   * @param rootTaskFrame the root task frame
   */
  public void setRootTaskFrame (TaskFrame rootTaskFrame) {
    this.rootTaskFrame = rootTaskFrame;
  }

  //// Protected Area
  
  /** Adds a named task frame to the dictionary.
   *
   * @param taskFrame the named task frame
   */
  protected void addTaskFrame (TaskFrame taskFrame) {
    taskFrameDictionary.put(taskFrame.getTaskName(), taskFrame);
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the task frame library singleton instance */
  protected static TaskFrameLibrary taskFrameLibrary;
  
  /** the task frame dictionary, task name --> task frame */
  protected HashMap taskFrameDictionary = new HashMap();
  
  /** the root task frame */
  protected TaskFrame rootTaskFrame;
  
  //// Main
  
}
