/*
 * CycUtils.java
 *
 * Created on March 21, 2002, 4:54 PM
 */

package org.opencyc.util;

import org.opencyc.api.CycAccess;

/**
 * This is a placeholder class for general cyc utilities.
 * All methods in this class are static.
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
 * @author  tbrussea
 * @version $Id$
 */
public class CycUtils {
    
  /*
   * Creates a new instance of CycUtils and hides it since no instances 
   * of this class need ever be made. All methods here are static. 
   */
  private CycUtils() {}
    
  /** 
   * Evaluates the given SubL expression given on the cyc image
   * provided by the CycAccess object given. Really just a thin wrapper
   * around "CycAccess.converseObject()" because I found that
   * to be a very non-intuitive method name.  Currently all
   * exceptions are caught and stack traces printed to standard
   * err. I expect that the API on this method may change in the near future
   * to throw appropriate exceptions.
   * @param conn The CycAccess object to use for communications
   * with the appropriate Cyc image.
   * @param subl The string that represents the SubL expression that
   * needs to be evaluated.
   * @return The value of evaluating the passed in subl expression or
   * null if an error occurred.
   **/
  public static synchronized Object evalSubL(CycAccess connection, 
					     String subl) {
    Object result = null;
    try {
      //System.out.println("Submitting: " + subl);
      result = connection.converseObject(subl);
    } catch (Exception e) {
      System.err.println("converseString(" + subl + ") failed");
      e.printStackTrace();
    }
    return result;
  }
  
  /**
   * Evalutes the given subl expression on the given Cyc image in the 
   * background. When the evaluation is complete the CycWorkerListener
   * passed to this method is notified via an event callback.
   * @param conn The CycAccess object to use for communications
   * with the appropriate Cyc image.
   * @param subl The string that represents the SubL expression that
   * needs to be evaluated.
   * @param cwl The CycWorkerListener that should be notified of
   * the background tasks progress.
   * @return The CycWorker object that is doing the work. It will be
   * either already be started.
   * @see CycWorker
   * @see CycWorkerListener
   */
  public static CycWorker evalSubLInBackground(final CycAccess conn,
					       final String subl,
					       final CycWorkerListener cwl) {
    CycWorker worker = new CycWorker() {
	public Object construct() { return evalSubL(conn, subl); }
      };
    if(cwl != null) { worker.addListener(cwl); }
    worker.start();
    return worker;
  }
    
}
