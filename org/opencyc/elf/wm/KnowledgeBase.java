package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.NodeComponent;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.taskframe.TaskCommand;
import org.opencyc.elf.bg.taskframe.TaskFrame;

//// External Imports

/**
 * <P>Knowledge Base contains the known entities and their attributes.
 *
 * @version $Id$
 * @author Stephen L. Reed  
 * @date August 12, 2003, 4:59 PM
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
  
  /** Creates a new instance of KnowledgeBase. */
  public KnowledgeBase() {
  }
  
  //// Public Area
  
  //// Protected Area
  
  /**
   * Receives the fetch task frame message from world model.
   */
  protected void receiveFetchTaskFrame () {
    //TODO
    // receive via channel from world model
    // TaskCommand taskCommand
  }
  
  /**
   * Sends the task frame to the world model.
   */
  protected void sendTaskFrame () {
    //TODO
    // send via channel to the world model
    // TaskCommand taskCommand
    // TaskFrame taskFrame
    // send forwardTaskFrame(taskCommand, taskFrame) to worldModel
  }
  
  /**
   * Receives a request KB object message from ?.
   */
  protected void receiveRequestKBObject () {
    //TODO
    // receive via channel from ?
    // Object obj
  }
  
  /**
   * Sends a request KB object message to ?.
   */
  protected void requestKBObject () {
    //TODO
    // send via channel to ?
    // Object obj
  }
  
  /**
   * Sends a KB object message to ?.
   */
  protected void sendKBObject () {
    //TODO
    // send via channel to ?
    // Object obj
  }
  
  /**
   * Receives a KB object message from ?.
   */
  protected void receiveKBObject () {
    //TODO
    // receive via channel from ?
    // Object obj
  }
  
  /**
   * Receives an update message from ?.
   */
  protected void receiveUpdate () {
    //TODO
    // receive via channel from ?
    // Object obj
    // Object data
  }
  
  /**
   * Receives the post schedule message from ?
   */
  protected void receivePostSchedule () {
    //TODO
    // receive via channel from ?
    // TaskCommand taskCommand
    // Schedule schedule
  }
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
