package org.opencyc.api;

import java.net.*;
import java.io.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;

/**
 * Provides a connection to the OpenCyc server.<p>
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
public class CycConnection {

    public BufferedReader in;
    public BufferedWriter out;

    private CycAccess cycAccess;
    private Socket apiSocket;
    private String hostName;
    private int port;
    private static final Timer notimeout = new Timer();

    public static final String DEFAULT_HOSTNAME = "localhost";
    public static final int DEFAULT_PORT = 3601;
    public boolean trace = false;
    private boolean isSymbolicExpression = false;

    /**
     * Constructs a new CycConnection object.
     */

    public CycConnection(CycAccess cycAccess) throws IOException, UnknownHostException {
        this(DEFAULT_HOSTNAME, DEFAULT_PORT, cycAccess);
    }
    public CycConnection(String host, CycAccess cycAccess)
        throws IOException, UnknownHostException {
        this(host, DEFAULT_PORT, cycAccess);
    }
    public CycConnection(int port, CycAccess cycAccess)
        throws IOException, UnknownHostException {
        this(DEFAULT_HOSTNAME, port, cycAccess);
    }
    public CycConnection(String hostName, int port, CycAccess cycAccess)
        throws IOException, UnknownHostException {
        this.hostName = hostName;
        this.port = port;
        this.cycAccess = cycAccess;
        initializeApiConnection();
        System.out.println("Connection " + apiSocket);
    }

    /**
     * Initialize a cyc api socket.
     */
    private void initializeApiConnection() throws IOException, UnknownHostException {
        apiSocket = new Socket(hostName, DEFAULT_PORT);
        in = new BufferedReader(new InputStreamReader(apiSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(apiSocket.getOutputStream()));
    }

    /**
     * Close the cyc api socket.
     */
    public void close () throws IOException {
        if (apiSocket != null)
            apiSocket.close();
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the second element is the error message string.
     */
    public Object[] converse(String message, Timer timeout)
      throws IOException, TimeOutException {
        isSymbolicExpression = false;
        Object [] response = converseUsingStrings(message, timeout);
        if (((Integer) response[0]).intValue() == 200) {
            String answer = ((String) response[1]).trim();
            if (isSymbolicExpression)
                // Recurse if list contains CycConstant objects.
                response[1] = cycAccess.makeCycList(answer);
            else if (answer.equals("NIL"))
                response[1] = new CycList();
            else
                response[1] = answer;
        }
        return response;
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element.  If an error occurs the second element is the error
     * message string.
     */
  public Object[] converse(String message) throws IOException {
    return converse(message, notimeout);
  }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of a object array, and the Cyc response string as the second
     * element.
     */
    private Object [] converseUsingStrings(String message, Timer timeout)
      throws IOException, TimeOutException {
        if (trace)
            System.out.println(message + " --> cyc");
        out.write(message);
        if (! message.endsWith("\n"))
            out.newLine();
        out.flush();
        if (trace)
            System.out.print("cyc --> ");
        Object [] answer = readCycResponse(timeout);
        if (trace)
            System.out.println();
        return answer;
    }

    /**
     * Read the cyc response.
     */
     private Object[] readCycResponse(Timer timeout) throws IOException, TimeOutException {
         Object [] answer = {new Integer(0), ""};
         // Parse the response code digits.
         StringBuffer responseCodeDigits = new StringBuffer();
         while (true) {
         timeout.checkForTimeOut();
             int ch = in.read();
             if (trace)
                 System.out.print((char) ch);
             if (ch == ' ')
                 break;
             responseCodeDigits.append((char) ch);
         }
         answer[0] = new Integer(responseCodeDigits.toString().trim());
         in.mark(1);
         int ch = in.read();
         in.reset();
         if (ch == '(') {
             isSymbolicExpression = true;
             answer[1] = readSymbolicExpression();
         }
         else if (ch == '"')
             answer[1] = readString();
         else
             answer[1] = readAtom();
         // Read the terminating newline.
         ch = in.read();
         if (trace)
             System.out.print((char) ch);
         return answer;
     }


    /**
     * Read a symbolic expression.
     */
    private String readSymbolicExpression() throws IOException {
        int parenLevel = 0;
        boolean isQuotedString = false;
        StringBuffer result = new StringBuffer();

        int ch = in.read();
        if (trace)
            System.out.print((char) ch);
        parenLevel++;
        result.append((char) ch);

        while (parenLevel != 0) {
            ch = in.read();
            if (trace)
                System.out.print((char) ch);
            if (ch == '"')
                if (isQuotedString)
                    isQuotedString = false;
            else
                isQuotedString = true;

            if (! isQuotedString) {
                if (ch == '(')
                    parenLevel++;
                if  (ch == ')')
                    parenLevel--;
            }
            result.append((char) ch);
            if ((! isQuotedString) &&
                (result.length() > 3) &&
                (result.toString().endsWith(" . ")))
                // Convert lists of the form ( x y . z) into (x y z).
                result.setLength(result.length() - 2);

        }
        return result.toString();
    }

    /**
     * Read a quoted string.
     */
    private String readString() throws IOException {
        StringBuffer result = new StringBuffer();
        int ch = in.read();
        if (trace)
            System.out.print((char) ch);

        while (true) {
            ch = in.read();
            if (trace)
                System.out.print((char) ch);
            if (ch == '"')
                return result.toString();
            result.append((char) ch);
        }
    }

    /**
     * Read an atom.
     */
    private String readAtom() throws IOException {
        StringBuffer result = new StringBuffer();
        while (true) {
            in.mark(1);
            int ch = in.read();
            if (trace)
                System.out.print((char) ch);
            if (ch == '\n')
                break;
            result.append((char) ch);
        }
        in.reset();
        return result.toString();
    }
}
