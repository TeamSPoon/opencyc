package org.opencyc.api;

import java.net.*;
import java.io.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;

/**
 * Provides a binary connection and an ascii connection to the OpenCyc server.  The ascii connection is
 * legacy and its use is deprecated.<p>
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

    /**
     * Default host name for the OpenCyc server.
     */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /**
     * Default base tcp port for the OpenCyc server.
     */
    public static final int DEFAULT_BASE_PORT = 3600;

    /**
     * HTTP port offset for the OpenCyc server.
     */
    public static final int HTTP_PORT_OFFSET = 0;

    /**
     * ASCII port offset for the OpenCyc server.
     */
    public static final int ASCII_PORT_OFFSET = 1;

    /**
     * CFASL (binary) port offset for the OpenCyc server.
     */
    public static final int CFASL_PORT_OFFSET = 14;

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server.
     */
    public boolean trace = false;

    /**
     * The ascii interface input stream.
     */
    protected BufferedReader in;

    /**
     * The ascii interface output stream.
     */
    protected BufferedWriter out;

    /**
     * The binary interface input stream</tt>.
     */
    protected CfaslInputStream cfaslInputStream;

    /**
     * The binary interface output stream</tt>.
     */
    protected CfaslOutputStream cfaslOutputStream;

    /**
     * The name of the computer hosting the OpenCyc server.
     */
    protected String hostName;

    /**
     * The tcp port from which the asciiPort and cfaslPorts are derived.
     */
    protected int basePort;

    /**
     * The tcp port assigned to the the ascii connection to the OpenCyc server.
     */
    protected int asciiPort;

    /**
     * The tcp port assigned to the the binary connection to the OpenCyc server.
     */
    protected int cfaslPort;

    /**
     * The tcp socket assigned to the the ascii connection to the OpenCyc server.
     */
    protected Socket asciiSocket;

    /**
     * The tcp socket assigned to the the binary connection to the OpenCyc server.
     */
    protected Socket cfaslSocket;

    /**
     * The timer which optionally monitors the duration of requests to the OpenCyc server.
     */
    protected static final Timer notimeout = new Timer();

    /**
     * Indicates if the response from the OpenCyc server is a symbolic expression (enclosed in
     * parentheses).
     */
    protected boolean isSymbolicExpression = false;

    /**
     * A reference to the parent CycAccess object for dereferencing constants in ascii symbolic expressions.
     */
    protected CycAccess cycAccess;

    /**
     * Constructs a new CycConnection object using the default host name and base port number.
     */
    public CycConnection() throws IOException, UnknownHostException {
        this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT);
    }

    /**
     * Constructs a new CycConnection object using a given host name and the default base port number.
     *
     * @param host the name of the computer hosting the OpenCyc server.
     */
    public CycConnection(String host)
        throws IOException, UnknownHostException {
        this(host, DEFAULT_BASE_PORT);
    }

    /**
     * Constructs a new CycConnection object using the default host name and a given base port number.
     *
     * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
     */
    public CycConnection(int basePort)
        throws IOException, UnknownHostException {
        this(DEFAULT_HOSTNAME, basePort);
    }

    /**
     * Constructs a new CycConnection object using a given host name and a given base port number.
     *
     * @param host the name of the computer hosting the OpenCyc server.
     * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
     */
    public CycConnection(String hostName, int basePort)
        throws IOException, UnknownHostException {
        this.hostName = hostName;
        this.basePort = basePort;
        asciiPort = basePort + ASCII_PORT_OFFSET;
        cfaslPort = basePort + CFASL_PORT_OFFSET;
        initializeApiConnections();
        System.out.println("Connection " + asciiSocket);
        System.out.println("Connection " + cfaslSocket);
    }

    /**
     * Initializes the OpenCyc ascii socket and the OpenCyc binary socket connections.
     */
    private void initializeApiConnections() throws IOException, UnknownHostException {
        asciiSocket = new Socket(hostName, asciiPort);
        in = new BufferedReader(new InputStreamReader(asciiSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(asciiSocket.getOutputStream()));
        cfaslSocket = new Socket(hostName, cfaslPort);
        cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream(), this);
        cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream(), this);
    }

    /**
     * Close the cyc api sockets.
     */
    public void close () throws IOException {
        if (asciiSocket != null)
            asciiSocket.close();
        if (cfaslSocket != null)
            cfaslSocket.close();
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
                response[1] = CycAccess.current().makeCycList(answer);
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
