package org.opencyc.elf;


//// Internal Imports
import org.opencyc.elf.bg.procedure.Procedure;

//// External Imports
import EDU.oswego.cs.dl.util.concurrent.Puttable;

/**
 * Provides common attributes and behavior for Elementary Loop Functioning
 * (ELF) node components which have an input message buffer.
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
public abstract class BufferedNodeComponent extends NodeComponent {
  //// Constructors


  //// Public Area
  
  /**
   * Gets the ELF Node that contains this object.
   * 
   * @return the ELF Node that contains this object
   */
  public Node getNode() {
    return node;
  }

  /**
   * Sets the ELF Node that contains this object.
   * 
   * @param node the ELF Node thatcontains this object
   */
  public void setNode(Node node) {
    this.node = node;
  }

  /**
   * Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   */
  public abstract Puttable getChannel ();
  
  //// Protected Area
    
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the ELF node that contains this object
   */
  protected Node node;

  //// Main
}
