package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.bg.taskframe.TaskFrame;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.JobAssignmentStatus;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;

//// External Imports
import java.util.ArrayList;
import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * <P>
 * JobAssigner performs the non-temporal (e.g. spatial) task decomposition
 * among the available agents and resources.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
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
public class JobAssigner extends BufferedNodeComponent {
  //// Constructors

  /**
   * Creates a new instance of JobAssigner
   */
  public JobAssigner() {
  }

  /** 
   * Creates a new instance of JobAssigner with the given
   * input and output channels.
   *
   * @param node the containing ELF node
   * @param jobAssignerChannel the takable channel from which messages are input
   * @param executorChannel the puttable channel to which messages are output to the higher
   * level executor, or null if this is the highest level
   */
  public JobAssigner (Node node,
                      Takable jobAssignerChannel,
                      Puttable executorChannel) {
    setNode(node);
    node.getBehaviorGeneration().setJobAssigner(this);
    System.out.println("");
    getLogger().info("Creating JobAssigner");
    this.jobAssignerChannel = jobAssignerChannel;
    consumer = new Consumer(jobAssignerChannel,
                            executorChannel,
                            this);
    executor = new ThreadedExecutor();
    try {
      executor.execute(consumer);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  //// Public Area

  
  
  /** 
   * Gets the puttable channel for this node component to which other node
   * components can send messages.
   *
   * @return the puttable channel for this node component to which other node
   * components can send messages
   *
   */
  public Puttable getChannel() {
    return (Puttable) jobAssignerChannel;
  }  

  /**
   * Returns true if the given object equals this object.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this object
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof JobAssigner)) {
      return false;
    }

    //TODO
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    //TODO
    return "";
  }
  
  /**
   * Gets the list of schedulers for this job assigner
   *
   * @return the list of schedulers for this job assigner
   */
  public ArrayList getSchedulers () {
    return schedulers;
  }

  /**
   * Sets the list of schedulers for this job assigner
   *
   * @param schedulers the list of schedulers for this job assigner
   */
  public void setSchedulers (ArrayList schedulers) {
    this.schedulers = schedulers;
  }

  //// Protected Area

  /**
   * Thread which processes the input message channel.
   */
  protected class Consumer implements Runnable {
    
    /**
     * the takable channel from which messages are input
     */
    protected final Takable jobAssignerChannel;
    
    /**
     * the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     */
    protected Puttable executorChannel;
    
    /**
     * the parent node component
     */
    protected NodeComponent nodeComponent;
          
    /**
     * the node's commanded task
     */
    protected TaskCommand taskCommand;
    
    /**
     * the task frame for the current task command
     */
    protected TaskFrame taskFrame;
    
    /**
     * Creates a new instance of Consumer.
     *
     * @param jobAssignerChannel the takable channel from which messages are input
     * @param executorChannel the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     * @param nodeComponent the parent node component
     */
    protected Consumer (Takable jobAssignerChannel,
                        Puttable executorChannel,
                        NodeComponent nodeComponent) { 
      getLogger().info("Creating JobAssigner.Consumer");
      this.jobAssignerChannel = jobAssignerChannel;
      this.executorChannel = executorChannel;
      this.nodeComponent = nodeComponent;
    }

    /**
     * Reads messages from the input queue and processes them.
     */
    public void run () {
      getLogger().info("Running JobAssigner.Consumer");
      try {
        while (true) { 
          dispatchMsg((GenericMsg) jobAssignerChannel.take()); 
        }
      }
      catch (InterruptedException ex) {}
    }
     
    /**
     * Sets the puttable channel to which messages are output to the higher
     * level executor
     *
     * @param executorChannel the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     */
    public void setExecutorChannel (Puttable executorChannel) {
      this.executorChannel = executorChannel;
    }
    
    //TODO think about conversations and thread safety
    
    /**
     * Dispatches the given input channel message by type.
     *
     * @param genericMsg the given input channel message
     */
    void dispatchMsg (GenericMsg genericMsg) {
      if (genericMsg instanceof DoTaskMsg)
        processDoTaskMsg((DoTaskMsg) genericMsg);
      else if (genericMsg instanceof SchedulerStatusMsg)
        processSchedulerStatusMsg((SchedulerStatusMsg) genericMsg);
    }
  
    /**
     * Processes the do task message.
     *
     * @param doTaskMsg the do task message that contains the commanded task
     */
    protected void processDoTaskMsg (DoTaskMsg doTaskMsg) {
      taskCommand = doTaskMsg.getTaskCommand();
      //TOTO
    }
                
    /**
     * Processes the schedule status message.
     *
     * @param schedulerStatusMsg he schedule status message
     */
    protected void processSchedulerStatusMsg (SchedulerStatusMsg schedulerStatusMsg) {
      //TODO
      Status status = schedulerStatusMsg.getStatus();
    }
    
    /**
     * Sends the job assignment status message to the higher-level executor.
     */
    protected void sendJobAssignmentStatus () {
      //TODO
      Status status = new Status();
      
      JobAssignmentStatus jobAssignmentStatus = new JobAssignmentStatus();
      jobAssignmentStatus.setSender(nodeComponent);
      jobAssignmentStatus.setStatus(status);
    }
        
  }
  
  /**
   * Sends the decompose task frame message to ?.
   */
  protected void decomposeTaskFrame () {
    //TODO
    // send via channel to ?
    // TaskCommand taskCommand
  }
  
  /**
   * Decomposes the function for the current task frame. (May return
   * a list of scheduler/job pairs.)
   */
  protected void decomposeFunction () {
    //TODO
    // TaskCommand taskCommand
  }
    
  //// Private Area

  //// Internal Rep
  
  /**
   * the takable channel from which messages are input
   */
  protected Takable jobAssignerChannel;
    
  /**
   * the thread which processes the input channel of messages
   */
  protected Consumer consumer;
  
  /**
   * the executor of the consumer thread
   */
  protected Executor executor;
  
  /**
   * the list of schedulers for this job assigner
   */
  protected ArrayList schedulers;
  
  //// main
}