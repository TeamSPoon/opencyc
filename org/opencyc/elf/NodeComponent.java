package org.opencyc.elf;

//// Internal Imports
import org.opencyc.elf.bg.procedure.Procedure;

import org.opencyc.elf.message.GenericMsg;

//// External Imports
import java.util.ArrayList;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Puttable;

/**
 * Provides common attributes and behavior for Elementary Loop Functioning
 * (ELF) node components.<br>
 * Each node component contains one or more process threads that communicates with other 
 * node components by sending asychronous messages.
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
public abstract class NodeComponent extends ELFObject {

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
   * Gets the logger for this node.
   *
   * @return the logger for this node
   */
  public Logger getLogger () {
    return node.getLogger();
  }
  
  /**
   * Sends the given message through the given channel to the recipient.
   *
   * @param channel the communication channel
   * @param genericMsg the message to be sent to the recipient
   */
  public void sendMsgToRecipient(Puttable channel, 
                                 GenericMsg genericMsg) {
    try {
      channel.put(genericMsg);
    }
    catch (InterruptedException e) {
    }
  }

  //// Protected Area
    
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the ELF node that contains this object
   */
  protected Node node;

  //// Main
}