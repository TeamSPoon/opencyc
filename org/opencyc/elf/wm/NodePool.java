

//     DELETE THIS MODULE as it is no longer required, instead nodes will be
//     generated on the fly and discarded when done.  Only the actuators and
//     sensors are pooled.

package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.Node;

//// External Imports
import java.util.HashMap;

/**
 * NodePool provides a store in which ELF nodes can be retrieved
 * by name.  It is initially populated by the Node factory.  There is a singleton instance
 * of Node pool.
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
public class NodePool {
  
  //// Constructors
  
  /** Creates a new instance of NodePool and stores it in the singleton instance. */
  public NodePool() {
    nodePool = this;
  }
  
  //// Public Area
  
  /**
   * Gets the node pool singleton instance.
   *
   * @return the node pool singleton instance
   */
  public static NodePool getInstance () {
    return nodePool;
  }
  
  /**
   * Gets the ELF node associated with the given node name.
   *
   * @param name the given node name
   * @return the ELF node associated with the given node name
   */
  public Node getNode (String name) {
    return (Node)  nodeDictionary.get(name);
  }

  
  /**
   * Sets the node associated with the given node name.
   *
   * @param name the given node name
   * @param node the given node
   */
  public void setNode (String name, Node node) {
    nodeDictionary.put(name, node);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the node pool singleton instance */
  protected static NodePool nodePool;
  
  /** the dictionary that associates a given node name with the node object */
  protected HashMap nodeDictionary = new HashMap();
  
  //// Main
  
}
