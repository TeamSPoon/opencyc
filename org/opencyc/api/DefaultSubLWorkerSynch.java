/* $Id$
 *
 * Copyright (c) 2004 - 2005 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.api;

//// Internal Imports
import java.io.IOException;
import java.util.List;

import org.opencyc.cycobject.CycList;
import org.opencyc.util.TimeOutException;

/**
  * <P>SubLWorkerSynch is designed to provide a handle for a particular 
 * communication event with a Cyc server in a synchronous manner. 
 * DefaultSubLWorkerSynch provides the default
 * implementation while SubLWorker and DefaultSubLWorker provide
 * asynchronous communications capabilities. Currently, SubLWorkerSynchs are one-shot,
 * i.e., a new SubLWorkerSynch needs to be created for every new communication.
 * SubLWorkerSynchs are cancelable, time-outable and provide means for incremental
 * return results.
 *  
 * <P>Example usage: <code>
 * try {
 *    CycAccess access = new CycAccess("localhost", 3640);
 *    SubLWorkerSynch worker = new DefaultSubLWorkerSynch("(+ 1 1)", access);
 *    Object work = worker.getWork();
 *    System.out.println("Got worker: " + worker + "\nGot result: " + work + ".");
 *  } catch (Exception e) {
 *    e.printStackTrace();
 *  }
 * </code>
 *
 * <p>Copyright 2004 Cycorp, Inc., license is open source GNU LGPL.
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
 * @author tbrussea
 * @date March 25, 2004, 2:01 PM
 * @version $Id$
 */
public class DefaultSubLWorkerSynch 
extends DefaultSubLWorker 
implements SubLWorkerSynch, SubLWorkerListener {
  
  //// Constructors
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a String
   * @param access the Cyc server that should process the SubL command
   */
  public DefaultSubLWorkerSynch(String subLCommand, CycAccess access) {
    this(access.makeCycList(subLCommand), access);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a String
   * @param access the Cyc server that should process the SubL command
   * @param timeoutMsecs the max time to wait in msecs for the work to
   * be completed before giving up (0 means to wait forever, and negative
   * values will cause an exception to be thrown). When communications time
   * out, an abort command is sent back to the Cyc server so processing will
   * stop there as well.
   */  
  public DefaultSubLWorkerSynch(String subLCommand, CycAccess access, 
      long timeoutMsecs) {
    this(access.makeCycList(subLCommand), access, timeoutMsecs);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a String
   * @param access the Cyc server that should process the SubL command
   * @param expectIncrementalResults boolean indicating wether to expect
   * incremental results
   */  
  public DefaultSubLWorkerSynch(String subLCommand, CycAccess access, 
      boolean expectIncrementalResults) {
    this(access.makeCycList(subLCommand), access, expectIncrementalResults);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a String
   * @param access the Cyc server that should process the SubL command
   * @param expectIncrementalResults boolean indicating wether to expect
   * incremental results
   * @param timeoutMsec the max time to wait in msecs for the work to
   * be completed before giving up (0 means to wait forever, and negative
   * values will cause an exception to be thrown). When communications time
   * out, an abort command is sent back to the Cyc server so processing will
   * stop there as well.
   */  
  public DefaultSubLWorkerSynch(String subLCommand, CycAccess access, 
      boolean expectIncrementalResults, long timeoutMsec) {
    this(access.makeCycList(subLCommand), access, 
      expectIncrementalResults, timeoutMsec);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a CycList
   * @param access the Cyc server that should process the SubL command
   */  
  public DefaultSubLWorkerSynch(CycList subLCommand, CycAccess access) {
    this(subLCommand, access, false);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a CycList
   * @param access the Cyc server that should process the SubL command
   * @param timeoutMsecs the max time to wait in msecs for the work to
   * be completed before giving up (0 means to wait forever, and negative
   * values will cause an exception to be thrown). When communications time
   * out, an abort command is sent back to the Cyc server so processing will
   * stop there as well.
   */  
  public DefaultSubLWorkerSynch(CycList subLCommand, CycAccess access, 
      long timeoutMsecs) {
    this( subLCommand, access, false, timeoutMsecs);
  }
  
  /** Creates a new instance of DefaultSubLWorkerSynch.
   * @param subLCommand the SubL command that does the work as a CycList
   * @param access the Cyc server that should process the SubL command
   * @param expectIncrementalResults boolean indicating wether to expect
   * incremental results
   */  
  public DefaultSubLWorkerSynch(CycList subLCommand, CycAccess access, 
      boolean expectIncrementalResults) {
    this( subLCommand, access, expectIncrementalResults, 0);
  }
  
  /** Creates a new instance of DerfaultSubLWorker.
   * @param subLCommand the SubL command that does the work as a CycList
   * @param access the Cyc server that should process the SubL command
   * @param expectIncrementalResults boolean indicating wether to expect
   * incremental results
   * @param timeoutMsecs the max time to wait in msecs for the work to
   * be completed before giving up (0 means to wait forever, and negative
   * values will cause an exception to be thrown). When communications time
   * out, an abort command is sent back to the Cyc server so processing will
   * stop there as well.
   */  
  public DefaultSubLWorkerSynch(CycList subLCommand, CycAccess access, 
      boolean expectIncrementalResults, long timeoutMsecs) {
    super(subLCommand, access, expectIncrementalResults, timeoutMsecs);
    addListener(this);
  }
  
  //// Public Area
  
  /** This method starts communications with the Cyc server, waits for the work
   * to be performed, then returns the resultant work.
   * @throws IOException thown when there is a problem with the communications
   * protocol with the CycServer
   * @throws TimeOutException thrown if the worker takes to long to return results
   * @throws CycApiException thrown if any other error occurs
   * @return The work produced by this SubLWorkerSynch
   */
  public Object getWork() 
  throws IOException, TimeOutException, CycApiException {
    if (getStatus() == SubLWorkerStatus.NOT_STARTED_STATUS) {
      start();
    }
    if (getStatus() == SubLWorkerStatus.WORKING_STATUS) {
      try {
        synchronized (lock) {
          lock.wait(getTimeoutMsecs());
          if (getStatus() == SubLWorkerStatus.WORKING_STATUS) {
            try {
              this.abort();
            } catch (IOException xcpt) {
              throw xcpt;
            } finally {
              this.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(this,
                SubLWorkerStatus.EXCEPTION_STATUS, 
                new TimeOutException("Communications took more than: " 
                + getTimeoutMsecs() + " msecs.\nWhile trying to execute: \n" 
                + getSubLCommand().stringApiValue())));
            }
          }
        }
      } catch (Exception xcpt) {
        throw new RuntimeException(xcpt);
      }
    }
    if (getException() != null) { 
      try {
        throw getException(); 
      } catch (IOException ioe) {
        throw ioe; 
      } catch (TimeOutException toe) {
        throw toe;
      } catch (CycApiException cae) {
        throw cae;
      } catch (Exception xcpt) {
        throw new RuntimeException(xcpt);
      }
    }
    return work;
  }
  
  /** Ignore.
   * @param event the event object with details about this event
   */  
  public void notifySubLWorkerStarted(SubLWorkerEvent event) {}
  
  /** Saves any  available work.
   * @param event the event object with details about this event
   */  
  public void notifySubLWorkerDataAvailable(SubLWorkerEvent event) {
    appendWork(event.getWork());
  }  
  
  /** Make sure to save any applicable exceptions, 
   * @param event the event object with details about this event
   */  
  public void notifySubLWorkerTerminated(SubLWorkerEvent event) {
    setException(event.getException());
    synchronized (lock) {
      lock.notifyAll();
    }
  }
  
  /** Returns the exception thrown in the process of doing the work.
   * The value will be null if now exception has been thrown.
   * @return the exception thrown in the process of doing the work
   */  
  public Exception getException() { return e; }
  
  //// Protected Area
  
  /** Sets the exception.
   * @param e The exception that was thrown while processing this worker
   */  
  protected void setException(Exception e) {
    this.e = e;
  }
  
  /** Make sure to keep track of the resulting work, especially in the
   * case if incremental return results.
   * @param newWork The lastest batch of work.
   */  
  protected void appendWork(Object newWork) {
    if (expectIncrementalResults()) {
      if (work == null) {
        work = new CycList();
      }
      if (newWork != CycObjectFactory.nil) {
        ((List)work).addAll((List)newWork);
      }
    }
    else {
      work = newWork;
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
  private Object lock = new Object();
  private Object work = null;
  private Exception e = null;
  
  /** For tesing puposes only. */
  static SubLWorkerSynch testWorker; 
  
  //// Main
  
  /** Test main method and example usage.
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      CycAccess access = new CycAccess("localhost", 3640);
      SubLWorkerSynch worker = new DefaultSubLWorkerSynch("(+ 1 1)", access);
      Object work = worker.getWork();
      System.out.println("Got worker: " + worker + "\nGot result: " + work + ".");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      final CycAccess access = new CycAccess("localhost", 3640);
      Thread workerThread = new Thread() {
        public void run() {
          try {
            System.out.println("Starting work.");
            testWorker = new DefaultSubLWorkerSynch("(do-assertions (a))", access);
            Object obj = testWorker.getWork();
            System.out.println("Finished work with " + testWorker.getStatus().getName()
              + ", received: " + obj);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      workerThread.start();
      Thread.currentThread().sleep(10000);
      System.out.println("About to cancel work.");
      testWorker.cancel();
      System.out.println("Canceled work.");
      
      System.out.println("\nGiving chance to get ready ....\n");
      Thread.currentThread().sleep(1000);

      System.out.println( "\nOk, second round ....\n\n");      
      workerThread = new Thread() {
        public void run() {
          try {
            System.out.println("Starting work.");
            testWorker = new DefaultSubLWorkerSynch("(do-assertions (a))", access);
            Object obj = testWorker.getWork();
            System.out.println("Finished work with " + testWorker.getStatus().getName()
              + ", received: " + obj);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      workerThread.start();
      Thread.currentThread().sleep(10000);
      System.out.println("About to abort work.");
      testWorker.abort();
      System.out.println("Aborted work.");

      System.out.println("\nGiving chance to get ready ....\n");
      Thread.currentThread().sleep(1000);

      System.out.println( "\nOk, third round ....\n\n");
      workerThread = new Thread() {
        public void run() {
          long timeBefore = 0, timeAfter = 0;
          try {
            System.out.println("Starting work.");
            timeBefore = System.currentTimeMillis();
            testWorker = new DefaultSubLWorkerSynch("(do-assertions (a))", access, 500);
            Object obj = testWorker.getWork();
            timeAfter = System.currentTimeMillis();
            System.out.println("Finished work with " + testWorker.getStatus().getName()
              + " after " + (timeAfter - timeBefore) 
              + " millisecs (should be about 500), received: " + obj);
          } catch (Exception e) {
            timeAfter = System.currentTimeMillis();
            System.out.println( "The current time is: " + (timeAfter - timeBefore) 
              + " millisecs (should be about 500)");
            e.printStackTrace(); 
          }
        }
      };
      workerThread.start();
      Thread.currentThread().sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }
  
}
