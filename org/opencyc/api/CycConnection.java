package  org.opencyc.api;

import  java.net.*;
import  java.io.*;
import  java.util.Hashtable;
import  java.util.Iterator;
import  ViolinStrings.*;
import  org.doomdark.uuid.*;
import  org.opencyc.util.*;
import  org.opencyc.cycobject.*;

/**
 * Provides a binary connection and an ascii connection to the OpenCyc server.  The ascii connection is
 * legacy and its use is deprecated.<p>
 *
 * Collaborates with the <tt>CycAccess</tt> class which wraps the api functions.  CycAccess may be
 * specified as null in the CycConnection constructors when the binary api is used.
 *
 * Concurrent api requests are supported for binary (cfasl) mode. This is implemented by two
 * socket connections, the first being for asynchronous api requests sent to Cyc, and the
 * second for the asychronous api responses received from Cyc.
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
public class CycConnection implements CycConnectionInterface {

    /**
     * Default host name for the OpenCyc server.
     */
    public static String DEFAULT_HOSTNAME = "localhost";

    /**
     * Default base tcp port for the OpenCyc server.
     */
    public static final int DEFAULT_BASE_PORT = 3600;
    //public static final int DEFAULT_BASE_PORT = 3640;

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
     * No api trace.
     */
    public static final int API_TRACE_NONE = 0;

    /**
     * Message-level api trace.
     */
    public static final int API_TRACE_MESSAGES = 1;

    /**
     * Detailed api trace.
     */
    public static final int API_TRACE_DETAILED = 2;

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server.
     */
    protected int trace = API_TRACE_NONE;
    //protected int trace = API_TRACE_MESSAGES;

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
    public static final int DEFAULT_COMMUNICATION_MODE = BINARY_MODE;

    /**
     * Indicator for whether to use the binary or acsii connection with OpenCyc.
     */
    protected int communicationMode = 0;

    /**
     * Serial messaging mode to the OpenCyc server.
     */
    public static final int SERIAL_MESSAGING_MODE = 1;

    /**
     * Concurrent messaging mode to the OpenCyc server.
     */
    public static final int CONCURRENT_MESSAGING_MODE = 2;

    /**
     * Default messaging mode to the OpenCyc server.
     */
    public static final int DEFAULT_MESSAGING_MODE = SERIAL_MESSAGING_MODE;

    /**
     * Messaging mode to the OpenCyc server.
     */
    public int messagingMode = DEFAULT_MESSAGING_MODE;

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
     * The tcp port assigned to the ascii connection to the OpenCyc server.
     */
    protected int asciiPort;

    /**
     * The tcp port assigned to the binary connection to the OpenCyc server.
     */
    protected int cfaslPort;

    /**
     * The tcp socket assigned to the ascii connection to the OpenCyc server.
     */
    protected Socket asciiSocket;

    /**
     * The tcp socket assigned to the binary connection to the OpenCyc server.
     *
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
     * An indicator for ascii communications mode that strings should retain their quote delimiters.
     */
    protected boolean quotedStrings;

    /**
     * outbound request serial id
     */
    public int apiRequestId = 0;

    /**
     * The default priority of a task-processor request.
     */
    protected static final int DEFAULT_PRIORITY = 3;

    /**
     * name of my api client
     */
    protected String myClientName = "api client";

    /**
     * Implements an association:  apiRequestId --> waiting thread for the response.
     * Used when submitting concurrent requests to the task-processor.
     */
    protected Hashtable waitingReplyThreads = new Hashtable();

    /**
     * Implements an association:  apiRequestId --> response message.
     * Used when submitting concurrent requests to the task-processor.
     */
    protected Hashtable responseMessages = new Hashtable();

    /**
     * handles responses from task-processor requests in binary communication mode.
     */
    protected TaskProcessorBinaryResponseHandler taskProcessorBinaryResponseHandler;

    /**
     * Indicates to the taskProcessor response handlers that the server connection
     * is closed.
     */
    protected boolean taskProcessingEnded = false;

    /**
     * Universally Unique ID that identifies this CycConnection to the Cyc server. It
     * is used when establishing the (second) asychronous socket connection.
     */
    protected UUID uuid;

    /**
     * Constructs a new CycConnection using the given socket obtained from the parent AgentManager
     * listener.
     *
     * @param cfaslSocket tcp socket which forms the binary connection to the OpenCyc server
     */
    public CycConnection (Socket cfaslSocket) throws IOException {
        this.cfaslSocket = cfaslSocket;
        hostName = cfaslSocket.getInetAddress().getHostName();
        basePort = cfaslSocket.getPort() - CFASL_PORT_OFFSET;
        asciiPort = basePort + ASCII_PORT_OFFSET;
        communicationMode = BINARY_MODE;
        cycAccess = null;
        cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream());
        cfaslInputStream.trace = trace;
        cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream());
        cfaslOutputStream.trace = trace;
    }

    /**
     * Constructs a new CycConnection object using the default host name, default base port number
     * and binary communication mode.  When CycAccess is null as in this case, diagnostic output is
     * reduced.
     */
    public CycConnection ()
        throws IOException, UnknownHostException, CycApiException {
        this(DEFAULT_HOSTNAME,
             DEFAULT_BASE_PORT, DEFAULT_COMMUNICATION_MODE, null);
    }

    /**
     * Constructs a new CycConnection object using the default host name, default base port number
     * with binary communication mode, and the given CycAccess object.
     *
     * @param cycAccess the given CycAccess object which provides api services over
     * this CycConnection object
     */
    public CycConnection (CycAccess cycAccess)
        throws IOException, UnknownHostException, CycApiException {
        this(DEFAULT_HOSTNAME,
             DEFAULT_BASE_PORT, DEFAULT_COMMUNICATION_MODE, cycAccess);
    }

    /**
     * Constructs a new CycConnection object using a given host name, the given base port number,
     * the given communication mode, and the given CycAccess object
     *
     * @param host the name of the computer hosting the OpenCyc server.
     * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param cycAccess the given CycAccess object which provides api services over
     * this CycConnection object
     */
    public CycConnection (String hostName,
                          int basePort,
                          int communicationMode,
                          CycAccess cycAccess)
        throws IOException, UnknownHostException, CycApiException {
        this (hostName,
              basePort,
              communicationMode,
              CycConnection.DEFAULT_MESSAGING_MODE,
              cycAccess);
    }

    /**
     * Constructs a new CycConnection object using a given host name, the given base port number,
     * the given communication mode, and the given CycAccess object
     *
     * @param host the name of the computer hosting the OpenCyc server.
     * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param messagingMode either SERIAL_MESSAGING_MODE or CONCURRENT_MESSAGING_MODE
     * @param cycAccess the given CycAccess object which provides api services over
     * this CycConnection object
     */
    public CycConnection (String hostName,
                          int basePort,
                          int communicationMode,
                          int messagingMode,
                          CycAccess cycAccess)
        throws IOException, UnknownHostException, CycApiException {
        this.hostName = hostName;
        this.basePort = basePort;
        asciiPort = basePort + ASCII_PORT_OFFSET;
        cfaslPort = basePort + CFASL_PORT_OFFSET;
        if ((communicationMode != ASCII_MODE)
                && (communicationMode != BINARY_MODE))
            throw  new CycApiException("Invalid communication mode " + communicationMode);
        this.communicationMode = communicationMode;
        if ((messagingMode != SERIAL_MESSAGING_MODE)
                && (messagingMode != CONCURRENT_MESSAGING_MODE))
            throw  new CycApiException("Invalid messaging mode " + messagingMode);
        if ((communicationMode == ASCII_MODE)
                && (messagingMode == CONCURRENT_MESSAGING_MODE))
            this.messagingMode = SERIAL_MESSAGING_MODE;
        else
            this.messagingMode = messagingMode;
        this.cycAccess = cycAccess;
        initializeApiConnections();
        if (trace > API_TRACE_NONE) {
            if (communicationMode == ASCII_MODE)
                System.out.println("Ascii connection " + asciiSocket);
            else
                System.out.println("Binary connection " + cfaslSocket);
        }
        if (this.messagingMode == CONCURRENT_MESSAGING_MODE)
            initializeConcurrentProcessing();
    }

    /**
     * Initializes the OpenCyc ascii socket and the OpenCyc binary socket connections.
     */
    private void initializeApiConnections () throws IOException,
        UnknownHostException {
        if (communicationMode == ASCII_MODE) {
            asciiSocket = new Socket(hostName, asciiPort);
            in = new BufferedReader(new InputStreamReader(asciiSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(asciiSocket.getOutputStream()));
        }
        else {
            cfaslSocket = new Socket(hostName, cfaslPort);
            cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream());
            cfaslInputStream.trace = trace;
            cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream());
            cfaslOutputStream.trace = trace;
        }
    }

    /**
     * Initializes the concurrent processing mode.  Use serial messaging mode to
     * ensure the Cyc task processors are initialized, then start this
     * connection's taskProcessor response handler thread.
     */
    protected void initializeConcurrentProcessing ()
        throws IOException, UnknownHostException, CycApiException {
        taskProcessorBinaryResponseHandler =
            new TaskProcessorBinaryResponseHandler(Thread.currentThread());
        taskProcessorBinaryResponseHandler.start();

        // sleep until listening socket is initialized.
        while (true) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }
        }
        // Send request to Cyc server to open its outbound socket to
        // our awaiting listener.
        CycList request = new CycList();
        request.add(new CycSymbol("INITIALIZE-JAVA-API-SOCKET"));
        uuid = UUIDGenerator.getInstance().generateTimeBasedUUID();
        request.add(uuid.toString());
        request.add(InetAddress.getLocalHost().getHostName());
        request.add(new Integer(TaskProcessorBinaryResponseHandler.LOCAL_CLIENT_LISTENER_PORT));
        sendBinary(request);
        // ignore response
        receiveBinary();
    }

    /**
     * Ensures that the api socket connections are closed when this object is garbage collected.
     */
    protected void finalize() {
        close();
    }

    /**
     * Close the api sockets and streams.
     */
    public void close () {
        if (asciiSocket != null) {
            if (out != null) {
                try {
                    out.write("(API-QUIT)\n");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error quitting the api connection " + e.getMessage());
                }
                try {
                    out.flush();
                }
                catch (Exception e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error finalizing the api connection " + e.getMessage());
                }
            }
            if (asciiSocket != null) {
                try {
                    asciiSocket.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error closing the api connection " + e.getMessage());
                }
            }
        }
        if (cfaslSocket != null) {
            if (cfaslOutputStream != null) {
                CycList command;
                if (messagingMode == CONCURRENT_MESSAGING_MODE) {
                    command = new CycList();
                    command.add(CycObjectFactory.makeCycSymbol("CLOSE-JAVA-API-SOCKET"));
                    command.add(uuid.toString());
                    try {
                        sendBinary(command);
                        if (cfaslInputStream != null)
                            receiveBinary();
                    }
                    catch (Exception e) {
                    }
                }
                command = new CycList();
                command.add(CycObjectFactory.makeCycSymbol("API-QUIT"));
                try {
                    cfaslOutputStream.writeObject(command);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error quitting the api connection " + e.getMessage());
                }
                try {
                    cfaslOutputStream.flush();
                }
                catch (Exception e) {
                }
            }
            if (cfaslInputStream != null) {
                try {
                    cfaslInputStream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error finalizing the api connection " + e.getMessage());
                }
            }
            if (cfaslSocket != null) {
                try {
                    cfaslSocket.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error closing the api connection " + e.getMessage());
                }
            }
        }
        if (messagingMode == CONCURRENT_MESSAGING_MODE) {
            taskProcessingEnded = true;
            interruptAllWaitingReplyThreads();

            try {
                taskProcessorBinaryResponseHandler.join();
            }
            catch (InterruptedException e) {
            }
        }

    }

    /**
     * Return the name of the host to which the CycConnection is established.
     * @return the name of the Host to which this <tt>CycConnection</tt> is
     * connected.
     */
    public String getHostName() {return this.hostName;}

    /**
     * Return the base port to which the CycConnection is established.
     * @return the port to which this <tt>CycConnection</tt> is
     * connected.
     */
    public int getBasePort() {return this.basePort;}

    /**
     * Return the ASCII port to which the CycConnection is established.
     * @return the ASCII to which this <tt>CycConnection</tt> is
     * connected.
     */
    public int getAsciiPort() {return this.asciiPort;}

    /**
     * Return the CFASL port to which the CycConnection is established.
     * @return the CFASL port to which this <tt>CycConnection</tt> is
     * connected.
     */
    public int getCfaslPort() {return this.cfaslPort;}

    /**
     * Send a message to Cyc and return the <tt>Boolean</tt> true as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element.  If an error occurs the first element is <tt>Boolean</tt>
     * false and the second element is the error message string.
     *
     * @param message the api command
     * @return an array of two objects, the first is an response status object
     * either a Boolean (binary mode) or Integer (ascii mode), and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message) throws IOException,
        CycApiException {
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
     * @return an array of two objects, the first is a Boolean response status
     * object, and the second is the response object or error string.
     */
    public Object[] converse (Object message, Timer timeout)
        throws IOException, TimeOutException, CycApiException {
        if (communicationMode == CycConnection.ASCII_MODE) {
            CycList messageCycList;
            if (message instanceof String) {
                if (cycAccess == null)
                    throw new RuntimeException("CycAccess is required to process commands in string form");
                messageCycList = cycAccess.makeCycList((String) message);
            }
            else if (message instanceof CycList)
                messageCycList = (CycList) message;
            else
                throw new CycApiException("Invalid class for message " + message);
            String messageString = messageCycList.cyclifyWithEscapeChars();
            return  converseAscii(messageString, timeout);
        }
        else { // Binary (CFASL) mode
            CycList messageCycList;
            if (message instanceof CycList)
                messageCycList = (CycList) message;
            else if (message instanceof String) {
                if (cycAccess == null)
                    throw new RuntimeException("CycAccess is required to process commands in string form");
                messageCycList = cycAccess.makeCycList((String) message);
            }
            else
                throw new CycApiException("Invalid class for message " + message);
            messageCycList = substituteForBackquote(messageCycList, timeout);
            return  converseBinary(messageCycList, timeout);
        }
    }

    /**
     * Substitute a READ-FROM-STRING expression for expressions directly containing a
     * backquote symbol.  This transformation is only required for the binary api,
     * which does not parse the backquoted expression.
     *
     * @param messageCyclist the input expression to be checked for directly containing
     * a backquote symbol.
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return the expression with a READ-FROM-STRING expression substituted for
     * expressions directly containing a backquote symbol
     */
    protected CycList substituteForBackquote(CycList messageCycList,
                                             Timer timeout)
        throws IOException, CycApiException {
        if (messageCycList.treeContains(CycObjectFactory.backquote)) {
            CycList substituteCycList = new CycList();
            substituteCycList.add(CycObjectFactory.makeCycSymbol("read-from-string"));
            substituteCycList.add(messageCycList.cyclify());
            Object[] response = converseBinary(substituteCycList, timeout);
            if ((response[0].equals(Boolean.TRUE)) &&
                    (response[1] instanceof CycList)) {
                CycList backquoteExpression = (CycList) response[1];
                return backquoteExpression.subst(CycObjectFactory.makeCycSymbol("api-bq-list"),
                                                 CycObjectFactory.makeCycSymbol("bq-list"));
            }
            throw new CycApiException("Invalid backquote substitution in " + messageCycList +
                                      "\nstatus" + response[0] + "\nmessage " + response[1]);

        }
        return messageCycList;
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the second element is the error message string.
     * The concurrent mode of Cyc server communication is supported by
     * Cyc's pool of transaction processor threads, each of which can
     * concurrently process an api request.
     *
     * @param message the api command
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    protected Object[] converseBinary (CycList message,
                                                    Timer timeout)
        throws IOException, TimeOutException, CycApiException {
        if (this.messagingMode == SERIAL_MESSAGING_MODE) {
            sendBinary(message);
            return receiveBinary();
        }
        else { // CONCURRENT_MESSAGING_MODE
            CycSymbol taskProcessorRequestSymbol =
                CycObjectFactory.makeCycSymbol("task-processor-request");
            Integer id = null;
            CycList taskProcessorRequest = null;
            if (message.first().equals(taskProcessorRequestSymbol)) {
                // client has supplied the task-processor-request form
                taskProcessorRequest = message;
                id = (Integer) message.third();
            }
            else {
                id = nextApiRequestId();
                taskProcessorRequest = new CycList();
                // function
                taskProcessorRequest.add(taskProcessorRequestSymbol);
                // request
                taskProcessorRequest.add(message);
                // id
                taskProcessorRequest.add(id);
                // priority
                taskProcessorRequest.add(new Integer(DEFAULT_PRIORITY));
                // requestor
                taskProcessorRequest.add(myClientName);
                // client-bindings
                taskProcessorRequest.add(CycObjectFactory.nil);
                // uuid to identify this client
                taskProcessorRequest.add(uuid.toString());
            }
            waitingReplyThreads.put(id, Thread.currentThread());
            sendBinary(taskProcessorRequest);
            while (true) {
                try {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {
                    break;
                }
            }
            Object[] answer =  {
                null, null
            };
            CycList taskProcessorResponse = (CycList) responseMessages.get(id);
            responseMessages.remove(id);
            Object response = taskProcessorResponse.get(5);
            Object status = taskProcessorResponse.get(6);
            if (status == CycObjectFactory.nil) {
                // OK
                answer[0] = Boolean.TRUE;
                answer[1] = response;
            }
            else {
                // Error, status contains the error message
                answer[0] = Boolean.FALSE;
                answer[1] = status;
            }
            return answer;
        }
    }

    /**
     * Returns the next apiRequestId.
     *
     * @return the next apiRequestId
     */
    public synchronized Integer nextApiRequestId () {
        return new Integer(++apiRequestId);
    }

    /**
     * Sends an object to the CYC server.  If the connection is not already open, it is
     * opened.  The object must be a valid CFASL-translatable: an Integer, Float, Double,
     * Boolean, String, or cyc object.
     *
     * @param message the api command
     */
    public synchronized void sendBinary (Object message) throws IOException {
        if (trace > API_TRACE_NONE) {
            if (message instanceof CycList)
                System.out.println(((CycList) message).safeToString() + " --> cyc");
            else if (message instanceof CycFort)
                System.out.println(((CycFort) message).safeToString() + " --> cyc");
            else
                System.out.println(message + " --> cyc");
        }
        cfaslOutputStream.writeObject(message);
        cfaslOutputStream.flush();
    }

    /**
     * Receives an object from the CYC server.
     *
     * @return an array of two objects, the first is a Boolean response, and the second is the
     * response object or error string.
     */
    public synchronized Object[] receiveBinary () throws IOException, CycApiException {
        Object status = cfaslInputStream.readObject();
        Object response = cfaslInputStream.readObject();
        Object[] answer =  {
            null, null
        };
        if (status == null ||
                status.equals(CycObjectFactory.nil)) {
            answer[0] = Boolean.FALSE;
            answer[1] = response;
            if (trace > API_TRACE_NONE) {
                String responseString = null;
                if (response instanceof CycList)
                    responseString = ((CycList) response).safeToString();
                else if (response instanceof CycFort)
                    responseString = ((CycFort) response).safeToString();
                else
                    responseString = response.toString();
                System.out.println("received error = (" + status + ") " + responseString);
            }
            return answer;
        }
        answer[0] = Boolean.TRUE;
        if (cycAccess == null)
            answer[1] = response;
        else if (cycAccess.deferObjectCompletion)
            answer[1] = response;
        else
            answer[1] = cycAccess.completeObject(response);
        if (trace > API_TRACE_NONE) {
            String responseString = null;
            if (response instanceof CycList)
                responseString = ((CycList) response).safeToString();
            else if (response instanceof CycFort)
                responseString = ((CycFort) response).safeToString();
            else
                responseString = response.toString();
            System.out.println("cyc --> (" + answer[0] + ") " + responseString);
        }
        return  answer;
    }

    /**
     * Receives a binary (cfasl) api request from a cyc server.  Unlike the api response handled
     * by the receiveBinary method, this method does not expect an input status object.
     *
     * @return the api request expression.
     */
    public CycList receiveBinaryApiRequest () throws IOException,
        CycApiException {
        CycList apiRequest = (CycList) cfaslInputStream.readObject();
        if (trace > API_TRACE_NONE) {
            System.out.println("cyc --> (api-request) " + apiRequest.safeToString());
        }
        return apiRequest;
    }

    /**
     * Sends a binary (cfasl) api response to a cyc server.  This method prepends a status
     * object (the symbol T) to the message.
     *
     * @param the api response object
     */
    public void sendBinaryApiResponse (Object message) throws IOException,
        CycApiException {
        if (trace > API_TRACE_NONE) {
            String messageString = null;
            if (message instanceof CycList)
                messageString = ((CycList) message).safeToString();
            else if (message instanceof CycFort)
                messageString = ((CycFort) message).safeToString();
            else
                messageString = message.toString();
            System.out.println("(" + CycObjectFactory.t + ") " + messageString + " --> cyc");
        }
        CycList apiResponse = new CycList();
        apiResponse.add(CycObjectFactory.t);
        apiResponse.add(message);
        cfaslOutputStream.writeObject(apiResponse);
    }

    /**
     * Send a message to Cyc and return the Boolean response as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the first element is Boolean.FALSE and the second element
     * is the error message string.
     * The concurrent mode of Cyc server communication is supported by
     * Cyc's pool of transaction processor threads, each of which can
     * concurrently process an api request.
     *
     * @param message the api command
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    synchronized protected Object[] converseAscii (String message,
                                                   Timer timeout)
        throws IOException, TimeOutException, CycApiException {
        isSymbolicExpression = false;
        Object[] response = converseUsingAsciiStrings(message, timeout);
        if (response[0].equals(Boolean.TRUE)) {
            String answer = ((String)response[1]).trim();
            if (StringUtils.isDelimitedString(answer)) {
                response[1] = StringUtils.removeDelimiters(answer);
                // Return the string.
                return response;
            }
            if (isSymbolicExpression) {
                // Recurse to complete contained CycConstant, CycNart objects.
                if (cycAccess == null)
                    throw new RuntimeException("CycAccess is required to process commands in string form");
                response[1] = CycAccess.current().makeCycList(answer);
                // Return the CycList object.
                return response;
            }
            if (answer.equals("NIL")) {
                response[1] = CycObjectFactory.nil;
                // Return the symbol nil.
                return response;
            }
            if (answer.startsWith("#$")) {
                if (cycAccess == null)
                    throw new RuntimeException("CycAccess is required to process commands in string form");
                response[1] = CycAccess.current().makeCycConstant(answer);
                // Return the constant.
                return response;
            }
            if (answer.startsWith("?")) {
                response[1] = CycObjectFactory.makeCycVariable(answer);
                // Return the variable.
                return response;
            }
            if (StringUtils.isNumeric(answer)) {
                response[1] = new Integer(answer);
                // Return the number.
                return response;
            }
            if (CycSymbol.isValidSymbolName(answer)) {
                response[1] = CycObjectFactory.makeCycSymbol(answer);
                // Return the symbol.
                return response;
            }

            try {
                double doubleAnswer = Double.parseDouble(answer);
                response[1] = new Double(doubleAnswer);
                // Return the double.
                return response;
            }
            catch (NumberFormatException e) {
            }
            if (answer.endsWith("d0") &&
                    (ViolinStrings.Strings.indexOfAnyOf(answer.substring(0,
                                                                         answer.length() - 2), "0123456789") > -1)) {
                String floatPart = answer.substring(0, answer.length() - 2);
                response[1] = new Double(floatPart);
                // Return the double.
                return response;
            }
            throw new CycApiException("Ascii api response not understood " + answer);
        }
        else
            return response;
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of a object array, and the Cyc response string as the second
     * element.
     */
    protected Object[] converseUsingAsciiStrings (String message,
                                                  Timer timeout)
        throws IOException, CycApiException, TimeOutException {
        if (trace > API_TRACE_NONE)
            System.out.println(message + " --> cyc");
        out.write(message);
        if (! message.endsWith("\n"))
            out.newLine();
        out.flush();
        if (trace > API_TRACE_NONE)
            System.out.print("cyc --> ");
        Object[] answer = readAsciiCycResponse(timeout);
        if (trace > API_TRACE_NONE)
            System.out.println();
        return  answer;
    }

    /**
     * Read the cyc response.
     */
    private Object[] readAsciiCycResponse (Timer timeout) throws IOException,
        TimeOutException {
        Object[] answer =  {
            null, null
        };
        // Parse the response code digits.
        StringBuffer responseCodeDigits = new StringBuffer();
        while (true) {
            if (timeout != null)
                timeout.checkForTimeOut();
            int ch = in.read();
            if (trace > API_TRACE_NONE)
                System.out.print((char)ch);
            if (ch == ' ')
                break;
            responseCodeDigits.append((char)ch);
        }
        int responseCode = 0;
        try {
            responseCode = (new Integer(responseCodeDigits.toString().trim())).intValue();
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Invalid response code digits " + responseCodeDigits);
        }
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
            answer[1] = readQuotedString();
        else
            answer[1] = readAtom();
        // Read the terminating newline.
        ch = in.read();
        if (trace > API_TRACE_NONE)
            System.out.print((char)ch);
        return  answer;
    }

    /**
     * Reads a complete symbolic expression as an ascii string.
     *
     * @return a complete symbolic expression as an ascii string
     */
    private String readSymbolicExpression () throws IOException {
        int parenLevel = 0;
        boolean isQuotedString = false;
        StringBuffer result = new StringBuffer();
        int ch = in.read();
        if (trace > API_TRACE_NONE)
            System.out.print((char)ch);
        parenLevel++;
        result.append((char)ch);
        while (parenLevel != 0) {
            ch = in.read();
            if (trace > API_TRACE_NONE)
                System.out.print((char)ch);
            if (ch == '"')
                if (isQuotedString)
                    isQuotedString = false;
                else
                    isQuotedString = true;
            if (! isQuotedString) {
                if (ch == '(')
                    parenLevel++;
                if (ch == ')')
                    parenLevel--;
            }
            result.append((char) ch);
        }
        return  result.toString();
    }

    /**
     * Reads a quoted string
     *
     * @return the quoted string read
     */
    private String readQuotedString () throws IOException {
        StringBuffer result = new StringBuffer();
        int ch = in.read();
        if (trace > API_TRACE_NONE)
            System.out.print((char)ch);
        boolean escapedChar = false;
        while (true) {
            ch = in.read();
            if (trace > API_TRACE_NONE)
                System.out.print((char)ch);
            if ((ch == '"')  && (! escapedChar))
                return  "\"" + result.toString() + "\"";
            if (escapedChar)
                escapedChar = false;
            else if (ch == '\\')
                escapedChar = true;
            result.append((char)ch);
        }
    }

    /**
     * Reads an atom.
     *
     * @return the atom read as an ascii string
     */
    private String readAtom () throws IOException {
        StringBuffer result = new StringBuffer();
        while (true) {
            in.mark(1);
            int ch = in.read();
            if (trace > API_TRACE_NONE)
                System.out.print((char)ch);
            if (ch == '\r')
                break;
            if (ch == '\n')
                break;
            result.append((char)ch);
        }
        in.reset();
        return  result.toString();
    }

    /**
     * Returns the trace value.
     */
    public int getTrace() {
        return trace;
    }

    /**
     * Sets the trace value.
     * @param trace the trace value
     */
    public void setTrace(int trace) {
        this.trace = trace;
    }

    /**
     * Turns on the diagnostic trace of socket messages.
     */
    public void traceOn() {
        trace = API_TRACE_MESSAGES;
        if (communicationMode == BINARY_MODE) {
            cfaslInputStream.trace = trace;
            cfaslOutputStream.trace = trace;
        }
    }

    /**
     * Turns on the detailed diagnostic trace of socket messages.
     */
    public void traceOnDetailed() {
        trace = API_TRACE_DETAILED;
        if (communicationMode == BINARY_MODE) {
            cfaslInputStream.trace = trace;
            cfaslOutputStream.trace = trace;
        }
    }

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff() {
        trace = API_TRACE_NONE;
        if (communicationMode == BINARY_MODE) {
            cfaslInputStream.trace = trace;
            cfaslOutputStream.trace = trace;
        }
    }

    /**
     * Returns connection information, suitable for diagnostics.
     */
    public String connectionInfo () {
        return "host " + hostName +
            ", asciiPort " + asciiPort +
            ", cfaslPort " + cfaslPort;
    }

    /**
     * Recovers from a socket error by interrupting all the
     * waiting reply threads.  Each awakened thread will detect
     * the error condition and throw an IOExecption.
     */
    protected void interruptAllWaitingReplyThreads() {
        Iterator iter = waitingReplyThreads.values().iterator();
        while(iter.hasNext()) {
            Thread thread = (Thread) iter.next();
            thread.interrupt();
        }
    }

    /**
     * Class TaskProcessorBinaryResponseHandler handles responses
     * from task-processor requests in binary communication mode.
     */
    protected class TaskProcessorBinaryResponseHandler extends Thread {

        /**
         * Well known port where the java api listens for requests from cyc clients.
         */
        public static final int LOCAL_CLIENT_LISTENER_PORT = 3615;

        /**
         * Maximum number of local cyc clients supported by this listener.
         */
        public static final int MAX_LOCAL_CLIENT_CLIENTS = 50;

        /**
         * The socket which listens for new connections.
         */
        protected ServerSocket listenerSocket = null;

        /**
         * The socket which receives asychronous inbound messages
         * from the Cyc server.
         */
        protected Socket inboundSocket = null;

        /**
         * The binary interface input stream which receives asychronous
         * messages from the Cyc server.</tt>.
         */
        public CfaslInputStream inboundStream;

        /**
         * Reference to the parent thread which will sleep until
         * this handler is initialized.
         */
        protected Thread parentThread;

        /**
         * The (ignore) message from the Cyc server to test if the
         * connection is alive.
         */
        protected CycList ignoreMessage;

        /**
         * Constructs a TaskProcessorBinaryResponseHandler object.
         */
        public TaskProcessorBinaryResponseHandler(Thread parentThread) {
            this.parentThread = parentThread;
            ignoreMessage = new CycList();
            ignoreMessage.add(new CycSymbol("IGNORE"));
        }

        /**
         * Blocks until the next task-processor response is available,
         * then awakens the client thread that made the request.
         */
        public void run() {
            // Create a listener socket awaiting the Cyc connection.
            try {
                listenerSocket = new ServerSocket(LOCAL_CLIENT_LISTENER_PORT,
                        MAX_LOCAL_CLIENT_CLIENTS);
                parentThread.interrupt();
                inboundSocket = listenerSocket.accept();
                inboundStream = new CfaslInputStream(inboundSocket.getInputStream());
                inboundStream.trace = trace;
                listenerSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Handle messsages received on the asychronous inbound Cyc connection.
            while (true) {
                Object status = null;
                CycList taskProcessorResponse = null;
                try {
                    status = inboundStream.readObject();
                    taskProcessorResponse = (CycList) inboundStream.readObject();
                 }
                catch (Exception e) {
                    if (taskProcessingEnded) {
                        if (trace > API_TRACE_NONE)
                            System.out.println("Ending binary mode task processor handler.");
                        return;
                    }
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
                if (trace > API_TRACE_NONE) {
                    String responseString = null;
                    System.out.println("cyc --> (" + status + ") " +
                                       taskProcessorResponse.safeToString());
                }
                if (taskProcessorResponse.equals(ignoreMessage))
                    continue;
                Integer id = (Integer) taskProcessorResponse.get(2);
                responseMessages.put(id, taskProcessorResponse);
                Thread clientThread = (Thread) waitingReplyThreads.get(id);
                waitingReplyThreads.remove(id);
                clientThread.interrupt();
            }
        }
    }

}







