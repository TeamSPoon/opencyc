package org.opencyc.elf.wm;


//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

//// External Imports
import java.util.Hashtable;

/** Knowledge Base contains the known entities and their attributes.  There is a singleton 
 * instance of the knowledge base.
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
public class KnowledgeBase extends NodeComponent {
  
  //// Constructors
  
  /** Creates a new instance of KnowledgeBase and stores it in the singleton instance. */
  public KnowledgeBase() {
    knowledgeBase = this;
  }
    
  //// Public Area
    
  /** Gets the knowledge base singleton instance.
   *
   * @return the knowledge base singleton instance
   */
  public KnowledgeBase getInstance() {
    return knowledgeBase;
  }
  
  /** Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    return "KnowledgeBase for " + node.getName();
  }

  /** Gets the value for the given knowledge base object.
   *
   * @param obj the given knowledge base object
   * @return the value for the given knowledge base object
   */
  public Object get(Object obj) {
    return kbCache.get(obj);
  }

  /** Stores the given knowledge base object and its associated data.  It the object
   * currently exists, then its data is overwritten by the given data.
   *
   * @param obj the given knowledge base object
   * @param data the data associated with the given object
   */
  public void put(Object obj, Object data) {
    kbCache.put(obj, data);
  }
 
  //// Protected Area
 
  //// Private Area
  
  //// Internal Rep
  
  /** the knowledge base cache associating obj --> data */
  protected Hashtable kbCache = new Hashtable();
  
  /** the knowledge base singleton instance */
  protected static KnowledgeBase knowledgeBase;
      
  //// Main
  
}
