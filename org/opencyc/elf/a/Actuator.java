package org.opencyc.elf.a;

//// Internal Imports
import org.opencyc.elf.bg.planner.Resource;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** Actuator defines the actuator interface for the Elementary Loop Functioning (ELF).
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
public interface Actuator {
  
  /** Gets the name of the actuator or virtual actuator (job assigner)
   *
   * @return the name of the actuator or virtual actuator (job assigner).
   */
  public String getName();
  
  /** Gets the resources required by this actuator or virtual actuator (job assigner).
   *
   * @return the resources required by this actuator or virtual actuator (job assigner)
   */
  public List getResources();
  
  /** Gets the names of actions that this actuator or virtual actuator (job assigner) can accomplish.
   *
   * @return the names of actions that this actuator or virtual actuator (job assigner) can accomplish
   */
  public List getActionCapabilities();
  
  /** the console output actuator name */
  public static final String CONSOLE_OUTPUT = "console output";
  
  
}
