package org.opencyc.elf.bg.planner;

//// Internal Imports
import org.opencyc.elf.BufferedNodeComponent;
import org.opencyc.elf.Node;
import org.opencyc.elf.NodeComponent;
import org.opencyc.elf.Status;

import org.opencyc.elf.a.Actuator;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.TaskFrame;
import org.opencyc.elf.bg.taskframe.TaskCommand;

import org.opencyc.elf.message.DoTaskMsg;
import org.opencyc.elf.message.GenericMsg;
import org.opencyc.elf.message.JobAssignmentStatus;
import org.opencyc.elf.message.SchedulerStatusMsg;
import org.opencyc.elf.message.ScheduleJobMsg;

import org.opencyc.elf.wm.TaskFrameLibrary;

//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.Puttable;
import EDU.oswego.cs.dl.util.concurrent.Takable;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * JobAssigner performs the non-temporal (for example spatial) task decomposition
 * among the available agents and resources.
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
public class JobAssigner extends BufferedNodeComponent implements Actuator {

  //// Constructors

  /**
   * Creates a new instance of JobAssigner within the given node, that can accompish
   * the given named actions.
   *
   * @param node the containing ELF node
   * @param actionCapabilities the names of actions that this virtual actuator can accomplish
   * @param jobAssignerChannel the takable channel from which messages are input
   */
  public JobAssigner (Node node,
                      List actionCapabilities,
                      Takable jobAssignerChannel) {
    setNode(node);
    this.actionCapabilities = actionCapabilities;
    this.jobAssignerChannel = jobAssignerChannel;
    node.getBehaviorGeneration().setJobAssigner(this);
  }
  
  //// Public Area

  /** 
   * Initializes with the given input and output channels and starts consuming task commands.
   *
   * @param executorChannel the puttable channel to which messages are output to the higher
   * level executor, or null if this is the highest level
   */
  public void initialize (Puttable executorChannel) {
    getLogger().info("Initializing JobAssigner");
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
  public List getSchedulers () {
    return schedulers;
  }

  /**
   * Sets the list of schedulers for this job assigner
   *
   * @param schedulers the list of schedulers for this job assigner
   */
  public void setSchedulers (List schedulers) {
    this.schedulers = schedulers;
  }

  /** 
   * Gets the name of the virtual actuator.
   *
   * @return the name of the virtual actuator
   */
  public String getName() {
    return getNode().getName();
  }
  
  /**
   * Gets the resources required by this virtual actuator by iterating over the 
   * resources required by the contained schedulers.
   *
   * @return the resources required by this virtual actuator
   */
  public List getResources() {
    List resources = new ArrayList();
    Iterator schedulerIterator = schedulers.iterator();
    while (schedulerIterator.hasNext()) {
      Scheduler scheduler = (Scheduler) schedulerIterator.next();
      Iterator resourceIterator = scheduler.getResources().iterator();
      while (resourceIterator.hasNext()) {
        Resource resource = (Resource) resourceIterator.next();
        if (! resources.contains(resource))
          resources.add(resource);
      }
    }
    return resources;
  }
  
  /**
   * Gets the names of actions that this virtual actuator can accomplish.
   *
   * @return names of the actions that this virtual actuator can accomplish
   */
  public List getActionCapabilities() {
    return actionCapabilities;
  }
  
  //// Protected Area

  /** Thread which processes the input message channel. */
  protected class Consumer implements Runnable {
    
    /** the takable channel from which messages are input */
    protected final Takable jobAssignerChannel;
    
    /**
     * the puttable channel to which messages are output to the higher
     * level executor, or null if this is the highest level
     */
    protected Puttable executorChannel;
    
    /** the parent node component */
    protected NodeComponent nodeComponent;
          
    /** the node's commanded task */
    protected TaskCommand taskCommand;
    
    /** the task frame for the current task command */
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

    /** Reads messages from the input queue and processes them. */
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
      getLogger().info("JobAssigner proccessing " + doTaskMsg);
      //TODO handle goals if the task command is not specified
      taskCommand = doTaskMsg.getTaskCommand();
      getLogger().info("Do task: " + taskCommand);
      Action actionCommand = taskCommand.getActionCommand();
      String taskFrameName = actionCommand.getName();
      getLogger().info("task name " + taskFrameName);
      TaskFrame taskFrame = TaskFrameLibrary.getInstance().getTaskFrame(taskFrameName);
      Iterator scheduleInfoIterator = taskFrame.getScheduleInfos().iterator();
      while (scheduleInfoIterator.hasNext()) {
        TaskFrame.ScheduleInfo scheduleInfo = 
          (TaskFrame.ScheduleInfo) scheduleInfoIterator.next();
        
      }
      
      // find agents that can collectively execute the task
      
      // find the best schedule for each agent
      
    }
        
    /**
     * Processes the schedule status message.
     *
     * @param schedulerStatusMsg he schedule status message
     */
    protected void processSchedulerStatusMsg (SchedulerStatusMsg schedulerStatusMsg) {
      Status status = schedulerStatusMsg.getStatus();
      //TODO
    }
    
    /** Sends the job assignment status message to the higher-level executor. */
    protected void sendJobAssignmentStatus () {
      //TODO
      Status status = new Status();
      
      JobAssignmentStatus jobAssignmentStatus = new JobAssignmentStatus();
      jobAssignmentStatus.setSender(nodeComponent);
      jobAssignmentStatus.setStatus(status);
    }
        
  }
  
  /** Sends the decompose task frame message to ?. */
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
  
  /** the names of actions that this virtual actuator can accomplish */
  protected List actionCapabilities;
  
  /** the takable channel from which messages are input */
  protected Takable jobAssignerChannel;
    
  /** the thread which processes the input channel of messages */
  protected Consumer consumer;
  
  /** the executor of the consumer thread */
  protected Executor executor;
  
  /** the list of schedulers for this job assigner */
  protected List schedulers;
  
  //// main
}