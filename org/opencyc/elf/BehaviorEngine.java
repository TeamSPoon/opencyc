package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.planner.JobAssignmentLibrary;
import org.opencyc.elf.bg.planner.ResourcePool;
import org.opencyc.elf.bg.planner.ScheduleLibrary;

//// External Imports
import java.util.logging.Logger;

/**
 * BehaviorEngine provides the main method for the behavior engine that
 * consists of a hierarchy of Elementary Loop Functioning (ELF) nodes.<br>
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
public class BehaviorEngine {
  
  //// Constructors
  
  /** Creates a new instance of BehaviorEngine */
  public BehaviorEngine() {
  }
  
  //// Public Area
  
  /**
   * Initializes the behavior engine.
   */
  public void initialize () {
    logger.info("Initializing BehaviorEngine");
    (new ResourcePool()).getInstance().initialize();
    (new JobAssignmentLibrary()).getInstance().initialize();
    (new ScheduleLibrary()).getInstance().initialize();
    
    //TODO
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
      
  /**
   * the logger
   */
  protected static Logger logger;
  
  //// Main
  
  /**
   * Provides the main method for the behavior engine.
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    logger = Logger.getLogger("org.opencyc.elf");
    logger.info("Creating BehaviorEngine");
    BehaviorEngine behaviorEngine = new BehaviorEngine();
    behaviorEngine.initialize();
    
    //TODO
    
    System.exit(0);
  }
  
}
