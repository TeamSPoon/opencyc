package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.Resource;

//// External Imports
import java.util.HashMap;

/**
 * ResourceFactory populates the resource pool.  There is
 * a singleton instance.
 *
 * @version $Id$
 * @author  reed
 *
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
public class ResourceFactory {
  
  //// Constructors
  
  /** Creates a new instance of ResourceFactory and stores it in the singleton instance. */
  public ResourceFactory() {
    resourceFactory = this;
  }
  
  //// Public Area
  
  /**
   * Gets the resource factory singleton instance.
   *
   * @return the resource factory singleton instance
   */
  public ResourceFactory getInstance() {
    return resourceFactory;
  }
  
  /** Populates the resource pool. */
  public static void populateResourcePool () {
    ResourcePool resourcePool = ResourcePool.getInstance();
    // console
    Resource resource = new Resource();
    resource.setResourceName(Resource.CONSOLE);
    resourcePool.setResource(Resource.CONSOLE, resource);
    // user
    resource = new Resource();
    resource.setResourceName(Resource.USER);
    resourcePool.setResource(Resource.USER, resource);
    
    //TODO
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the resource factory singleton instance */
  protected static ResourceFactory resourceFactory;
  
  //// Main
  
}
