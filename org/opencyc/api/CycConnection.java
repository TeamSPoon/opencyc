package  org.opencyc.api;

import  java.net.*;
import  java.io.*;
import  org.opencyc.util.*;
import  org.opencyc.cycobject.*;


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
     * Ascii mode connnection to the OpenCyc server.
     */
    public static final int ASCII_MODE = 1;
    /**
     * CFASL (binary) mode connnection to the OpenCyc server.
     */
    public static final int BINARY_MODE = 2;
    /**
     * Default communication mode connnection to the OpenCyc server.
     */
    public static final int DEFAULT_COMMUNICATION_MODE = ASCII_MODE;
    /**
     * Indicator for whether to use the binary or acsii connection with OpenCyc.
     */
    protected int communicationMode = 0;
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
     * Constructs a new CycConnection object using the default host name, default base port number and
     * binary communication mode.
     */
    public CycConnection () throws IOException, UnknownHostException {
        this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT, DEFAULT_COMMUNICATION_MODE);
    }

    /**
     * Constructs a new CycConnection object using a given host name, the given base port number, and
     * the given communication mode.
     *
     * @param host the name of the computer hosting the OpenCyc server.
     * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     */
    public CycConnection (String hostName, int basePort, int communicationMode) throws IOException,
            UnknownHostException {
        this.hostName = hostName;
        this.basePort = basePort;
        asciiPort = basePort + ASCII_PORT_OFFSET;
        cfaslPort = basePort + CFASL_PORT_OFFSET;
        this.communicationMode = communicationMode;
        if ((communicationMode != ASCII_MODE) && (communicationMode != BINARY_MODE))
            throw  new RuntimeException("Invalid communication mode " + communicationMode);
        initializeApiConnections();
        if (trace) {
            if (communicationMode == ASCII_MODE)
                System.out.println("Ascii connection " + asciiSocket);
            else
                System.out.println("Binary connection " + cfaslSocket);
        }
    }

    /**
     * Initializes the OpenCyc ascii socket and the OpenCyc binary socket connections.
     */
    private void initializeApiConnections () throws IOException, UnknownHostException {
        if (communicationMode == ASCII_MODE) {
            asciiSocket = new Socket(hostName, asciiPort);
            in = new BufferedReader(new InputStreamReader(asciiSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(asciiSocket.getOutputStream()));
        }
        else {
            cfaslSocket = new Socket(hostName, cfaslPort);
            cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream(), this);
            cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream(), this);
        }
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
     * Send a message to Cyc and return the <tt>Boolean</tt> true as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element.  If an error occurs the first element is <tt>Boolean</tt>
     * false and the second element is the error message string.
     *
     * @param message the api command
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message) throws IOException {
        return  converse(message, notimeout);
    }

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
    public Object[] converse (Object message, Timer timeout) throws IOException, TimeOutException {
        if (communicationMode == CycConnection.ASCII_MODE) {
            String messageString;
            if (message instanceof String)
                messageString = (String) message;
            else if (message instanceof CycList)
                messageString = ((CycList) message).cyclify();
            else
                throw new RuntimeException("Invalid class for message " + message);
            return  converseAscii(messageString, timeout);
        }
        else {
            CycList messageCycList;
            if (message instanceof CycList)
                messageCycList = (CycList) message;
            else if (message instanceof String)
                messageCycList = this.cycAccess.makeCycList((String) message);
            else
                throw new RuntimeException("Invalid class for message " + message);
            return  converseBinary(messageCycList, timeout);
        }
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the second element is the error message string.
     *
     * @param message the api command
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    synchronized protected Object[] converseBinary (Object message, Timer timeout)
        throws IOException, TimeOutException {
        sendBinary(message);
        return  receiveBinary();
    }

    /**
     * Sends an object to the CYC server.  If the connection is not already open, it is
     * opened.  The object must be a valid CFASL-translatable: an Integer, Float, Double,
     * Boolean, String, Symbol, ArrayList, or an implementor of the CfaslTranslatingObject.
     *
     * @param message the api command
     */
    private void sendBinary (Object message) throws IOException {
        if (trace)
            System.out.println("send = " + message);
        cfaslOutputStream.writeObject(message);
        cfaslOutputStream.flush();
    }

    /**
     * Receives an object from the CYC server.
     *
     * @return an array of two objects, the first is a Boolean response, and the second is the
     * response object or error string.
     */
    private Object[] receiveBinary () throws IOException {
        Object[] answer =  {
            null, null
        };
        if (cfaslInputStream.readObject() == null)
            answer[0] = Boolean.FALSE;
        else
            answer[0] = Boolean.TRUE;
        answer[1] = cfaslInputStream.readObject();
        if (trace)
            System.out.println("receive = (" + answer[0] + ") " + answer[1]);
        return  answer;
    }

    /**
     * Send a message to Cyc and return the Boolean response as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the first element is Boolean.FALSE and the second element
     * is the error message string.
     *
     * @param message the api command
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    synchronized protected Object[] converseAscii (String message, Timer timeout) throws IOException,
            TimeOutException {
        isSymbolicExpression = false;
        Object[] response = converseUsingStrings(message, timeout);
        if (response[0].equals(Boolean.TRUE)) {
            String answer = ((String)response[1]).trim();
            if (isSymbolicExpression)
                // Recurse if list contains CycConstant objects.
                response[1] = CycAccess.current().makeCycList(answer);
            else if (answer.equals("NIL"))
                response[1] = new CycList();
            else
                response[1] = answer;
        }
        return  response;
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of a object array, and the Cyc response string as the second
     * element.
     */
    protected Object[] converseUsingStrings (String message, Timer timeout) throws IOException, TimeOutException {
        if (trace)
            System.out.println(message + " --> cyc");
        out.write(message);
        if (!message.endsWith("\n"))
            out.newLine();
        out.flush();
        if (trace)
            System.out.print("cyc --> ");
        Object[] answer = readCycResponse(timeout);
        if (trace)
            System.out.println();
        return  answer;
    }

    /**
     * Read the cyc response.
     */
    private Object[] readCycResponse (Timer timeout) throws IOException, TimeOutException {
        Object[] answer =  {
            null, null
        };
        // Parse the response code digits.
        StringBuffer responseCodeDigits = new StringBuffer();
        while (true) {
            timeout.checkForTimeOut();
            int ch = in.read();
            if (trace)
                System.out.print((char)ch);
            if (ch == ' ')
                break;
            responseCodeDigits.append((char)ch);
        }
        int responseCode = (new Integer(responseCodeDigits.toString().trim())).intValue();
        if (responseCode == 200)
            answer[0] = Boolean.TRUE;
        else
            answer[0] = Boolean.FALSE;
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
            System.out.print((char)ch);
        return  answer;
    }

    /**
     * Read a symbolic expression.
     */
    private String readSymbolicExpression () throws IOException {
        int parenLevel = 0;
        boolean isQuotedString = false;
        StringBuffer result = new StringBuffer();
        int ch = in.read();
        if (trace)
            System.out.print((char)ch);
        parenLevel++;
        result.append((char)ch);
        while (parenLevel != 0) {
            ch = in.read();
            if (trace)
                System.out.print((char)ch);
            if (ch == '"')
                if (isQuotedString)
                    isQuotedString = false;
                else
                    isQuotedString = true;
            if (!isQuotedString) {
                if (ch == '(')
                    parenLevel++;
                if (ch == ')')
                    parenLevel--;
            }
            result.append((char)ch);
            if ((!isQuotedString) && (result.length() > 3) && (result.toString().endsWith(" . ")))
                // Convert lists of the form ( x y . z) into (x y z).
                result.setLength(result.length() - 2);
        }
        return  result.toString();
    }

    /**
     * Read a quoted string.
     */
    private String readString () throws IOException {
        StringBuffer result = new StringBuffer();
        int ch = in.read();
        if (trace)
            System.out.print((char)ch);
        while (true) {
            ch = in.read();
            if (trace)
                System.out.print((char)ch);
            if (ch == '"')
                return  result.toString();
            result.append((char)ch);
        }
    }

    /**
     * Read an atom.
     */
    private String readAtom () throws IOException {
        StringBuffer result = new StringBuffer();
        while (true) {
            in.mark(1);
            int ch = in.read();
            if (trace)
                System.out.print((char)ch);
            if (ch == '\n')
                break;
            result.append((char)ch);
        }
        in.reset();
        return  result.toString();
    }
}



