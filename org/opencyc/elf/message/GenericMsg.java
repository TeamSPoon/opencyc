package org.opencyc.elf.message;

//// Internal Imports
import org.opencyc.elf.NodeComponent;


//// External Imports
import EDU.oswego.cs.dl.util.concurrent.Puttable;

/**
 * Provides generic message attributes and behavior that is extended by subclasses
 * for specific message types.
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

abstract public class GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of GenericMsg. */
  public GenericMsg() {
  }
  
  //// Public Area
  
  /**
   * Returns a string representation of this object.
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[");
    stringBuffer.append(this.getClass().getName());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /**
   * Gets the sender of the message
   *
   * @return the sender of the message
   */
  public NodeComponent getSender () {
    return sender;
  }

  /**
   * Sets the sender of the message
   *
   * @param sender the sender of the message
   */
  public void setSender (NodeComponent sender) {
    this.sender = sender;
  }

  /**
   * Gets the in-reply-to message, or null if this message is unsolicited
   *
   * @return the in-reply-to message, or null if this message is unsolicited
   */
  public GenericMsg getInReplyToMsg () {
    return inReplyToMsg;
  }

  /**
   * Sets the in-reply-to message, or null if this message is unsolicited
   *
   * @param inReplyToMsg the in-reply-to message, or null if this message is unsolicited
   */
  public void setInReplyToMsg (GenericMsg inReplyToMsg) {
    this.inReplyToMsg = inReplyToMsg;
  }

  /**
   * Gets the reply-to channel, or null if no response is required
   *
   * @return the reply-to channel, or null if no response is required
   */
  public Puttable getReplyToChannel () {
    return replyToChannel;
  }

  /**
   * Sets the reply-to channel, or null if no response is required
   *
   * @param replyToChannel the reply-to channel, or null if no response is required
   */
  public void setReplyToChannel (Puttable replyToChannel) {
    this.replyToChannel = replyToChannel;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the sender of the message
   */
  protected NodeComponent sender;
  
  /**
   * the in-reply-to message, or null if this message is unsolicited
   */
  protected GenericMsg inReplyToMsg;
  
  /**
   * the reply-to channel, or null if no response is required
   */
  protected Puttable replyToChannel;

  //// Main
  
}
