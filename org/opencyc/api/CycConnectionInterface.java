package org.opencyc.api;

import java.io.*;
import  org.opencyc.util.*;

/**
 * Defines the interface for local and remote CycConnection objects<p>
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
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

public interface CycConnectionInterface {

    /**
     * Close the api sockets and streams.
     */
    public void close ();

    /**
     * Send a message to Cyc and return the <tt>Boolean</tt> true as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element.  If an error occurs the first element is <tt>Boolean</tt>
     * false and the second element is the error message string.
     *
     * @param message the api command
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message) throws IOException, CycApiException;

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the second element is the error message string.
     *
     * @param message the api command which must be a String or a CycList
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message, Timer timeout)
        throws IOException, TimeOutException, CycApiException;

    /**
     * Returns the trace value.
     *
     * @return the trace value
     */
    public int getTrace();

    /**
     * Sets the trace value.
     * @param trace the trace value
     */
    public void setTrace(int trace);

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff();

    /**
     * Turns on the diagnostic trace of socket messages.
     */
    public void traceOn();

    /**
     * Turns on the detailed diagnostic trace of socket messages.
     */
    public void traceOnDetailed();

}