package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.bg.planner.Resource;

//// External Imports
import java.util.HashMap;

/**
 * <P>ResourcePool contains the resources available to all ELF nodes.  There is a 
 * singleton instance.
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
public class ResourcePool {
  
  //// Constructors
  
  /** Creates a new instance of ResourcePool */
  public ResourcePool() {
    resourcePool = this;
  }
  
  //// Public Area
  
  /**
   * Initializes the resource pool.
   */
  public void initialize () {
    ResourceFactory resourceFactory = new ResourceFactory();
    resourceFactory.makeResources();
  }
  
  /**
   * Gets the singleton resource pool instance.
   *
   * @return the singleton resource pool instance
   */
  public static ResourcePool getInstance () {
    return resourcePool;
  }
  
  /**
   * Gets the resource named by the given name.
   *
   * @param name the given resource name
   * @return the resource named by the given name
   */
  public Resource getResource (String name) {
    return (Resource) resourceDictionary.get(name);
  }
  
  /**
   * Sets the resource named by the given name.
   *
   * @param name the given resource name
   * @param resource the resource named by the given name
   */
  public void setResource (String name, Resource resource) {
    resourceDictionary.put(name, resource);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the singleton resource pool instance
   */
  protected static ResourcePool resourcePool;
  
  /**
   * the resource dictionary whose key is the unique resource name and 
   * whose value is the named resource
   */
  protected HashMap resourceDictionary = new HashMap();

  //// Main
  
}
