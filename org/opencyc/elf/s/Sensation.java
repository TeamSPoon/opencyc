package org.opencyc.elf.s;

//// Internal Imports

//// External Imports

/** Sensation contains the sensory input from a sensor or virtual sensor (sensory perception).
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
public class Sensation {
  
  //// Constructors
  
  /** Creates a new instance of Sensation given the sensation name, object and sensed data associated with
   * the object. 
   *
   * @param name the sensation name
   * @param obj the object for which data is sensed
   * @param data the data associated with the object
   */
  public Sensation(String name, Object obj, Object data) {
    this.name = name;
    this.obj = obj;
    this.data = data;
  }
  
  /** Gets the sensation name.
   *
   * @return the sensation name
   */
  public String getname () {
    return name;
  }

  /** Gets the object for which data is sensed.
   *
   * @return the object for which data is sensed
   */
  public Object getObj () {
    return obj;
  }

  /** Gets the sensed data associated with the object
   *
   * @return the sensed data associated with the object
   */
  public Object getData () {
    return data;
  }

  /** the name of the console input sensation */
  public static final String CONSOLE_INPUT = "console input";  
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the sensation name */
  protected String name;
  
  /** the object for which data is sensed */
  protected Object obj;
  
  /** the sensed data associated with the object */
  protected Object data;
  
  //// Main
  
}
