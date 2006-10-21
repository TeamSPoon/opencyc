/* $Id$
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

//// External Imports
import java.util.*;
import javax.swing.event.EventListenerList;

/**
 * <P>DefaultInferenceWorker is designed to...
 *
 * <p>Copyright 2005 Cycorp, Inc., license is open source GNU LGPL.
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
 *
 * @author tbrussea, zelal
 * @date July 27, 2005, 11:55 AM
 * @version $Id$
 */
public class DefaultInferenceWorker extends DefaultSubLWorker implements InferenceWorker {
  
  //// Constructors
  
  /**
   * Creates a new instance of DefaultInferenceWorker.
   * @param query
   * @param mt
   * @param queryProperties
   * @param access
   * @param timeoutMsecs
   */
  public DefaultInferenceWorker(String query, ELMt mt, InferenceParameters queryProperties, 
      CycAccess access, long timeoutMsecs) {
    this(access.makeCycList(query), mt, queryProperties, 
      null, null, false, access, timeoutMsecs);
  }
  
  /**
   * Creates a new instance of DefaultInferenceWorker.
   * @param query
   * @param mt
   * @param queryProperties
   * @param access
   * @param timeoutMsecs
   */
  public DefaultInferenceWorker(CycList query, ELMt mt, InferenceParameters queryProperties, 
      CycAccess access, long timeoutMsecs) {
    super(createInferenceCommand(query, mt, queryProperties, 
      null, null, false, access), access, true, timeoutMsecs);
    init();
  }
  
  /**
   * Creates a new instance of DefaultInferenceWorker.
   * @param query
   * @param mt
   * @param queryProperties
   * @param nlGenerationProperties
   * @param answerProcessingFunction
   * @param optimizeVariables
   * @param access
   * @param timeoutMsecs
   */
  public DefaultInferenceWorker(String query, ELMt mt, InferenceParameters queryProperties, 
      Map nlGenerationProperties, CycSymbol answerProcessingFunction, 
      boolean optimizeVariables, CycAccess access, long timeoutMsecs) {
    this(access.makeCycList(query), mt, queryProperties, nlGenerationProperties, 
      answerProcessingFunction, optimizeVariables, access, timeoutMsecs);
  }
  
  /**
   * Creates a new instance of DefaultInferenceWorker.
   * @param query
   * @param mt
   * @param queryProperties
   * @param nlGenerationProperties
   * @param answerProcessingFunction
   * @param optimizeVariables
   * @param access
   * @param timeoutMsecs
   */
  public DefaultInferenceWorker(CycList query, ELMt mt, InferenceParameters queryProperties, 
      Map nlGenerationProperties, CycSymbol answerProcessingFunction, 
      boolean optimizeVariables, CycAccess access, long timeoutMsecs) {
    super(createInferenceCommand(query, mt, queryProperties, 
      nlGenerationProperties, answerProcessingFunction, optimizeVariables, access), 
      access, true, timeoutMsecs);
    this.answerProcessingFunction = answerProcessingFunction;
    init();
  }
  
  //// Public Area
  
  public void releaseInferenceResources(long timeoutMsecs)
  throws java.io.IOException, org.opencyc.util.TimeOutException, CycApiException {
    abort();
    SubLWorkerSynch subLWorker = new DefaultSubLWorkerSynch("(destroy-problem-store " +
      "(find-problem-store-by-id " + getProblemStoreId() + "))", 
      getCycServer(), timeoutMsecs);
    subLWorker.getWork();
  }
  
  public static void releaseAllInferenceResourcesForClient(CycAccess cycAccess, long timeoutMsecs)
  throws java.io.IOException, org.opencyc.util.TimeOutException, CycApiException {
    SubLWorkerSynch subLWorker = new DefaultSubLWorkerSynch("(open-cyc-release-inference-resources-for-client)", cycAccess, timeoutMsecs);
    subLWorker.getWork();
  }
  
  
  /**
   * Returns all the InferenceWorkerListeners listening in on this
   * InferenceWorker's events
   * @return all the InferenceWorkerListeners listening in on this
   * InferenceWorker's events
   */
  public Object[] getInferenceListeners() {
    synchronized (inferenceListeners) {
      return inferenceListeners.getListeners(inferenceListenerClass);
    }
  }
  
  /**
   * Adds the given listener to this InferenceWorker.
   * @param listener the listener that wishes to listen
   * for events sent out by this InferenceWorker
   */
  public void addInferenceListener(InferenceWorkerListener listener) {
    synchronized (inferenceListeners) {
      inferenceListeners.add(inferenceListenerClass, listener);
    }
  }
  
  /** 
   * Removes the given listener from this InferenceWorker.
   * @param listener the listener that no longer wishes
   * to receive events from this InferenceWorker
   */
  public void removeInferenceListener(InferenceWorkerListener listener) {
    synchronized (inferenceListeners) {
      inferenceListeners.remove(inferenceListenerClass, listener);
    }
  }
  
  /** Removes all listeners from this InferenceWorker. */
  public void removeAllInferenceListeners() { 
    synchronized (inferenceListeners) {
      Object[] listenerArray = inferenceListeners.getListenerList();
      for (int i = 0, size = listenerArray.length; i < size; i += 2) {
        inferenceListeners.remove((Class)listenerArray[i], 
          (EventListener)listenerArray[i+1]);
      }
    }
  }
  
  //public void continueInference() {
  //  throw new UnsupportedOperationException("continueInference() needs to be implemented.");
  //}
  
  // with infinite patience
  public void interruptInference() {
    interruptInference(null);
  }
  
  // with said patience
  public void interruptInference(int patience) {
    interruptInference(new Integer(patience));
  }
  
  protected void interruptInference(Integer patience) {
    String command = createInferenceInterruptCommand(patience);
    DefaultSubLWorker newWorker = new DefaultSubLWorker(command, getCycServer(), true, 0);
    SubLWorkerListener listener = new SubLWorkerListener() {
      public void notifySubLWorkerStarted(SubLWorkerEvent event) {}
      public void notifySubLWorkerDataAvailable(SubLWorkerEvent event) {}
      public void notifySubLWorkerTerminated(SubLWorkerEvent event) {}
    };
    newWorker.addListener(listener);
    try {
      //System.out.println("running "+command);
      newWorker.start();
      //Object result = newWorker.getWork();
      //System.out.println(result);
    } catch (java.io.IOException ioe) {
      throw new RuntimeException("Failed to continue inference (IOException).");
    }
  }  
  
  public void continueInference(InferenceParameters queryProperties) {
    String command = createInferenceContinuationCommand(queryProperties);
    DefaultSubLWorker newWorker = new DefaultSubLWorker(command, getCycServer(), true, getTimeoutMsecs());
    /*newWorker.addListener(new SubLWorkerListener() {
      public void notifySubLWorkerStarted(SubLWorkerEvent event) {}
      public void notifySubLWorkerDataAvailable(SubLWorkerEvent event) {}
      public void notifySubLWorkerTerminated(SubLWorkerEvent event) {}
    });*/
    newWorker.addListener(new SubLWorkerListener() {
      public void notifySubLWorkerStarted(SubLWorkerEvent event) {
        doSubLWorkerStarted(event);
      }
      public void notifySubLWorkerDataAvailable(SubLWorkerEvent event) {
        doSubLWorkerDataAvailable(event);
      }
      public void notifySubLWorkerTerminated(SubLWorkerEvent event) {
        doSubLWorkerTerminated(event);
      }
    });
    try {
      newWorker.start();
    } catch (java.io.IOException ioe) {
      throw new RuntimeException("Failed to continue inference (IOException).");
    }
    //throw new UnsupportedOperationException("continueInference() needs to be implemented.");
  }
  
  public synchronized void abort() throws java.io.IOException {
    //String command = createInferenceAbortionCommand();
    //DefaultSubLWorkerSynch newWorker = new DefaultSubLWorkerSynch(command, getCycServer(), false, getTimeoutMsecs());
    //newWorker.getWork();
    if (this.suspendReason == InferenceWorkerSuspendReason.INTERRUPT) {
      this.suspendReason = InferenceWorkerSuspendReason.ABORTED;
    }
    super.abort();
  }
  
  /**
   *
   * @param index
   * @return
   */  
  public Object getAnswerAt(int index) { return answers.get(index); }
  
  /**
   *
   * @return
   */  
  public int getAnswersCount() { return answers.size(); }
  
  /**
   *
   * @return
   */  
  public List getAnswers() { 
    synchronized (answers) {
      return new CycList(answers);
    }
  }
  
  /**
   *
   * @param startIndex
   * @param endIndex
   * @return
   */  
  public List getAnswers(int startIndex, int endIndex) { 
    return new ArrayList(answers.subList(startIndex, endIndex));
  }
  
  /**
   *
   * @return
   */  
  public int getInferenceId() { return inferenceId; }
  
  /**
   *
   * @return
   */  
  public InferenceStatus getInferenceStatus() { return status; }
  
  /**
   *
   * @return
   */  
  public int getProblemStoreId() { return problemStoreId; }
  
  /**
   * Returns a string representation of the InferenceWorker.
   * @return a string representation of the InferenceWorker
   */
  public String toString() {
    return toString(2);
  }
  
  /** Returns a string representation of the InferenceWorker.
   * @return a string representation of the InferenceWorker
   * @param indentLength the number of spaces to preceed each line of 
   * output String
   */
  public String toString(int indentLength) {
    StringBuffer nlBuff = new StringBuffer();
    nlBuff.append(System.getProperty("line.separator"));
    for (int i = 1; i < indentLength; i++) { nlBuff.append(" "); }
    String nl = nlBuff.toString();
    String sp = nl.substring(1);
    nlBuff.append(super.toString(indentLength));
    nlBuff.append("Inference id: ").append(inferenceId).append(nl);
    nlBuff.append("ProblemStore id: ").append(problemStoreId).append(nl);
    nlBuff.append("Status: ").append(status).append(nl);
    nlBuff.append("Suspend reason: ").append(suspendReason).append(nl);
    nlBuff.append("Answers: ").append(answers).append(nl);
    return "" + nlBuff;
  }
  
  /**
   *
   * @return
   */  
  public InferenceWorkerSuspendReason getSuspendReason() {
    return suspendReason;
  }
  
  //// Protected Area
  
  //// Private Area
  
  private void init() {
    this.addListener(new SubLWorkerListener() {
      public void notifySubLWorkerStarted(SubLWorkerEvent event) {
        doSubLWorkerStarted(event);
      }
      public void notifySubLWorkerDataAvailable(SubLWorkerEvent event) {
        doSubLWorkerDataAvailable(event);
      }
      public void notifySubLWorkerTerminated(SubLWorkerEvent event) {
        doSubLWorkerTerminated(event);
      }
    });
  }
  
  private void doSubLWorkerStarted(SubLWorkerEvent event) {
    InferenceStatus oldStatus = status;
    status = InferenceStatus.STARTED;
    Object[] curListeners = inferenceListeners.getListenerList();
    List errors = new ArrayList();
    for (int i = curListeners.length-2; i >= 0; i -= 2) {
      if (curListeners[i] == inferenceListenerClass) {
        try {
          ((InferenceWorkerListener)curListeners[i+1]).
            notifyInferenceStatusChanged(oldStatus, status, null, this);
        } catch (Exception e) {
          errors.add(e);
        }
      }
    }
    if (errors.size() > 0) {
      throw new RuntimeException((Exception)errors.get(0)); // @hack
    }
  }
  
  private void doSubLWorkerDataAvailable(SubLWorkerEvent event) {
    Object obj = event.getWork();
    if ((obj == null) || (!(obj instanceof CycList))) {
      if (CycObjectFactory.nil.equals(obj)) { return; }
      throw new RuntimeException("Got invalid result from inference: " + obj);
    }
    CycList data = (CycList)obj;
    if (data.size() < 2) {
      throw new RuntimeException("Got wrong number of arguments " 
        + "from inference result: " + data);
    }
    Object obj2 = data.get(0);
    if ((obj2 == null) || (!(obj2 instanceof CycSymbol))) {
      throw new RuntimeException("Got bad result keyword " 
        + "from inference result: " + obj2);
    }
    CycSymbol keyword = (CycSymbol)obj2;
    if (keyword.toString().equalsIgnoreCase(":INFERENCE-START")) {
      handleInferenceInitializationResult(data);
    } else if (keyword.toString().equalsIgnoreCase(":INFERENCE-ANSWER")) {
      //System.out.println("Considering adding "+data);
      handleInferenceAnswerResult(data);
    } else if (keyword.toString().equalsIgnoreCase(":INFERENCE-STATUS")) {
      handleInferenceStatusChangedResult(data);
    }
  }
  
  private void doSubLWorkerTerminated(SubLWorkerEvent event) {
    Object[] curListeners = inferenceListeners.getListenerList();
    List errors = new ArrayList();
    for (int i = curListeners.length-2; i >= 0; i -= 2) {
      if (curListeners[i] == inferenceListenerClass) {
        try {
          ((InferenceWorkerListener)curListeners[i+1]).
            notifyInferenceTerminated(this, event.getException());
        } catch (Exception e) {
          errors.add(e);
        }
      }
    }
    if (errors.size() > 0) {
      throw new RuntimeException((Exception)errors.get(0)); // @hack
    }
  }
  
  private void handleInferenceInitializationResult(CycList data) {
    if (data.size() != 3) {
      throw new RuntimeException("Got wrong number of arguments " 
        + "from inference result (expected 3): " + data);
    }
    Object problemStoreObj = data.get(1);
    Object inferenceObj = data.get(2);
    if ((problemStoreObj == null) || (!(problemStoreObj instanceof Number))) {
      throw new RuntimeException("Got bad inference problem store id: " + problemStoreObj);
    }
    if ((inferenceObj == null) || (!(inferenceObj instanceof Number))) {
      throw new RuntimeException("Got bad inference id: " + inferenceObj);
    }
    problemStoreId = ((Number)problemStoreObj).intValue();
    inferenceId = ((Number)inferenceObj).intValue();
    Object[] curListeners = inferenceListeners.getListenerList();
    List errors = new ArrayList();
    for (int i = curListeners.length-2; i >= 0; i -= 2) {
      if (curListeners[i] == inferenceListenerClass) {
        try {
          ((InferenceWorkerListener)curListeners[i+1]).
            notifyInferenceCreated(this);
        } catch (Exception e) {
          errors.add(e);
        }
      }
    }
    if (errors.size() > 0) {
      throw new RuntimeException((Exception)errors.get(0)); // @hack
    }
  }
  
  private void handleInferenceAnswerResult(CycList data) {
    if (data.size() != 2) {
      throw new RuntimeException("Got wrong number of arguments " 
        + "from inference result (expected 2): " + data);
    }
    Object newAnswers = data.get(1);
    if ((newAnswers == null) || (!(newAnswers instanceof CycList))) {
      throw new RuntimeException("Got bad inference answers list: " + newAnswers);
    }
    int curLastAnswerId = lastAnswerId;
    lastAnswerId += ((List)newAnswers).size();
    answers.addAll((List)newAnswers);
    Object[] curListeners = inferenceListeners.getListenerList();
    List errors = new ArrayList();
    for (int i = curListeners.length-2; i >= 0; i -= 2) {
      if (curListeners[i] == inferenceListenerClass) {
        try {
          ((InferenceWorkerListener)curListeners[i+1]).
            notifyInferenceAnswersAvailable(this, curLastAnswerId, (List)newAnswers);
        } catch (Exception e) {
          errors.add(e);
        }
      }
    }
    if (errors.size() > 0) {
      throw new RuntimeException((Exception)errors.get(0)); // @hack
    }
  }
  
  private synchronized void handleInferenceStatusChangedResult(CycList data) {
    if (data.size() != 3) {
      throw new RuntimeException("Got wrong number of arguments " 
        + "from inference status changed (expected 3): " + data);
    }
    Object statusObj = data.get(1);
    if ((statusObj == null) || (!(statusObj instanceof CycSymbol))) {
      throw new RuntimeException("Got bad inference status: " + statusObj);
    }
    InferenceStatus newStatus = InferenceStatus.findInferenceStatus((CycSymbol)statusObj);
    if (status == null) {
      throw new RuntimeException("Got bad inference status name: " + statusObj);
    }
    if (data.get(2) instanceof CycSymbol || data.get(2) == null) {
      suspendReason = InferenceWorkerSuspendReason.createFromCycSymbol((CycSymbol)data.get(2));
    } else {
      throw new RuntimeException("Unable to create InferenceWorkerSuspendReason from (" +
                                 data.get(2).getClass().getName()+") "+data.get(2).toString());
    }
    InferenceStatus oldStatus = status;
    status = newStatus;
    Object[] curListeners = inferenceListeners.getListenerList();
    List errors = new ArrayList();
    for (int i = curListeners.length-2; i >= 0; i -= 2) {
      if (curListeners[i] == inferenceListenerClass) {
        try {
          ((InferenceWorkerListener)curListeners[i+1]).
            notifyInferenceStatusChanged(oldStatus, status, suspendReason, this);
        } catch (Exception e) {
          errors.add(e);
        }
      }
    }
    if (errors.size() > 0) {
      throw new RuntimeException((Exception)errors.get(0)); // @hack
    }
  }
  
  /**
   * (define-api open-cyc-start-continuable-query (sentence mt &optional properties 
   * (nl-generation-properties *default-open-cyc-nl-generation-properties*) 
   * inference-answer-process-function 
   * (incremental-results? *use-api-task-processor-incremental-results?*) 
   * (optimize-query-sentence-variables? t)) 
   **/
  protected static String createInferenceCommand(CycList query, ELMt mt, 
      InferenceParameters queryProperties, Map nlGenerationProperties, 
      CycSymbol answerProcessingFunction, boolean optimizeVariables, CycAccess cycAccess) {
    // @ToDo Pass queryProperties and nlGenerationProperties!!!!!!!!!!!!
    if (queryProperties == null) {
      queryProperties = new DefaultInferenceParameters(cycAccess);
    }
    if ((answerProcessingFunction != null) &&(!answerProcessingFunction.shouldQuote())) {
      answerProcessingFunction = new CycSymbol(answerProcessingFunction.getPackageName(), 
      answerProcessingFunction.getSymbolName());
    }
    String processingFnStr = ((answerProcessingFunction != null) ? 
      answerProcessingFunction.stringApiValue() : "nil" );
    queryProperties.put(new CycSymbol(":CONTINUABLE?"), Boolean.TRUE);
    return "(open-cyc-start-continuable-query " + query.stringApiValue() + " " 
      + mt.stringApiValue() + " " + queryProperties.stringApiValue() + " nil " 
      + processingFnStr + " t " + (optimizeVariables ? "t" : "nil") + ")";
  }
  
  /**
   * @param patience - seconds to wait; 0 -> no patience ; null -> inf patience
   **/
  protected static String createInferenceInterruptCommand(int problemStoreId, int inferenceId, Integer patience) {
    String patienceStr = patience == null ? "NIL" : patience.toString();
    return "(cdr (list (inference-interrupt (find-inference-by-ids " +
            problemStoreId + " " + inferenceId + ") " + patienceStr + ")))";
  }

  protected String createInferenceInterruptCommand(Integer patience) {
    return DefaultInferenceWorker.createInferenceInterruptCommand(
      problemStoreId, inferenceId, patience);
  }
  
  /**
   * (define-api open-cyc-continue-query (problem-store-id inference-id properties
   * &optional (nl-generation-properties *default-open-cyc-nl-generation-properties*)
   * inference-answer-process-function
   * (incremental-results? *use-api-task-processor-incremental-results?*))
   **/
  protected String createInferenceContinuationCommand(InferenceParameters queryProperties) {
    if (queryProperties == null) {
      queryProperties = new DefaultInferenceParameters(getCycServer());
    }
    if ((answerProcessingFunction != null) &&(!answerProcessingFunction.shouldQuote())) {
      answerProcessingFunction = new CycSymbol(answerProcessingFunction.getPackageName(), 
      answerProcessingFunction.getSymbolName());
    }
    String processingFnStr = ((answerProcessingFunction != null) ? 
      answerProcessingFunction.stringApiValue() : "nil" );
    queryProperties.put(new CycSymbol(":CONTINUABLE?"), Boolean.TRUE);
    return "(cdr (list (open-cyc-continue-query " + problemStoreId + " " 
      + inferenceId + " " + queryProperties.stringApiValue() + " nil " 
      + processingFnStr + " t)))";
  }
  
  protected String createInferenceAbortionCommand() {
    return "(cdr (list (inference-abort (find-inference-by-ids " +
      problemStoreId + " " + inferenceId + "))))";
  }
  
  //// Internal Rep
  
  private volatile int problemStoreId;
  
  private volatile int inferenceId;
  
  private volatile InferenceStatus status = InferenceStatus.NOT_STARTED;
  
  private List answers = Collections.synchronizedList(new ArrayList());
  
  /** This holds the list of registered SubLWorkerListener listeners. */
  private EventListenerList inferenceListeners = new EventListenerList();
  
  private static Class inferenceListenerClass = InferenceWorkerListener.class;
  
  private int lastAnswerId = 0;
  
  private InferenceWorkerSuspendReason suspendReason = null;
  
  protected CycSymbol answerProcessingFunction;
  
  //// Main
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      CycAccess access = new CycAccess("CycServer", 3640);
      ELMt inferencePSC = access.makeELMt("#$InferencePSC");
      InferenceWorker worker = new DefaultInferenceWorker("(#$isa ?X #$Dog)", 
        inferencePSC, null, access, 50000);
      worker.addInferenceListener(new InferenceWorkerListener() { 
        
        public void notifyInferenceCreated(InferenceWorker inferenceWorker) {
          System.out.println("GOT CREATED EVENT\n" + inferenceWorker);
        }
        
        public void notifyInferenceStatusChanged(InferenceStatus oldStatus, InferenceStatus newStatus, 
            InferenceWorkerSuspendReason suspendReason, InferenceWorker inferenceWorker) {
          System.out.println("GOT STATUS CHANGED EVENT\n" + inferenceWorker);
        }
        
        public void notifyInferenceAnswersAvailable(InferenceWorker inferenceWorker, 
            int startAnswerId, List newAnswers) {
          System.out.println("GOT NEW ANSWERS EVENT\n" + inferenceWorker);
        }
        
        public void notifyInferenceTerminated(InferenceWorker inferenceWorker, Exception e) {
          System.out.println("GOT TERMINATED EVENT\n" + inferenceWorker);
          if (e != null) {
            e.printStackTrace();
          }
          System.exit(0);
        }
      });
      worker.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
}
