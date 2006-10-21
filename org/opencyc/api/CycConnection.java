package org.opencyc.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.DefaultCycObject;
import org.opencyc.util.Log;
import org.opencyc.util.StringUtils;
import org.opencyc.util.TimeOutException;
import org.opencyc.util.Timer;
import org.opencyc.util.UUID;

/**
 * Provides a binary connection and an ascii connection to the OpenCyc server.
 * The ascii connection is legacy and its use is deprecated.
 * 
 * <p>
 * Collaborates with the <tt>CycAccess</tt> class which wraps the api
 * functions. CycAccess may be specified as null in the CycConnection
 * constructors when the binary api is used. Concurrent api requests are
 * supported for binary (cfasl) mode. This is implemented by two socket
 * connections, the first being for asynchronous api requests sent to Cyc, and
 * the second for the asychronous api responses received from Cyc.
 * </p>
 * 
 * @version $Id$
 * @author Stephen L. Reed
 *         <p>
 *         <p>
 *         <p>
 *         <p>
 *         <p>
 */
public class CycConnection implements CycConnectionInterface {
	/** Default host name for the OpenCyc server. */
	public static String DEFAULT_HOSTNAME = "localhost";

	// public static String DEFAULT_HOSTNAME = "207.207.8.185";

	/** Default base tcp port for the OpenCyc server. */
	public static final int DEFAULT_BASE_PORT = 3600;

	/** HTTP port offset for the OpenCyc server. */
	public static final int HTTP_PORT_OFFSET = 0;

	/** ASCII port offset for the OpenCyc server. */
	public static final int ASCII_PORT_OFFSET = 1;

	/** CFASL (binary) port offset for the OpenCyc server. */
	public static final int CFASL_PORT_OFFSET = 14;

	/**
	 * the listening port of this connection in which a value of zero indicates
	 * that the local operating system chooses a free port
	 */
	public static int clientPort = 0;

	/**
	 * the client host name of the connection in which a value of null indicates
	 * that the local operating system provides the host name
	 */
	public static String clientHostName = null;

	/** No api trace. */
	public static final int API_TRACE_NONE = 0;

	/** Message-level api trace. */
	public static final int API_TRACE_MESSAGES = 1;

	/** Detailed api trace. */
	public static final int API_TRACE_DETAILED = 2;

	/**
	 * Parameter that, when true, causes a trace of the messages to and from the
	 * server.
	 */
	protected int trace = API_TRACE_NONE;

	// protected int trace = API_TRACE_MESSAGES;
	// protected int trace = API_TRACE_DETAILED;

	/** Ascii mode connnection to the OpenCyc server. */
	public static final int ASCII_MODE = 1;

	/** CFASL (binary) mode connnection to the OpenCyc server. */
	public static final int BINARY_MODE = 2;

	/** Default communication mode connnection to the OpenCyc server. */
	public static final int DEFAULT_COMMUNICATION_MODE = BINARY_MODE;

	/**
	 * When true, indicates that two Cyc host socket connections will be
	 * established at the cfasl listening port, one for outbound (from here) api
	 * requests and the other for inbound api responses. When false, indicates
	 * that the inbound api responses are received by a listening java
	 * client-side socket. Passive connections are client-firewall friendly.
	 */
	public boolean passiveConnection = true;

	/** Indicator for whether to use the binary or acsii connection with OpenCyc. */
	protected int communicationMode = 0;

	/** Serial messaging mode to the OpenCyc server. */
	public static final int SERIAL_MESSAGING_MODE = 1;

	/** Concurrent messaging mode to the OpenCyc server. */
	public static final int CONCURRENT_MESSAGING_MODE = 2;

	/** Default messaging mode to the OpenCyc server. */
	public static final int DEFAULT_MESSAGING_MODE = CONCURRENT_MESSAGING_MODE;

	/** Messaging mode to the OpenCyc server. */
	public int messagingMode = DEFAULT_MESSAGING_MODE;

	/** The ascii interface input stream. */
	protected BufferedReader in;

	/** The ascii interface output stream. */
	protected BufferedWriter out;

	/** The binary interface input stream. */
	protected CfaslInputStream cfaslInputStream;

	/** The binary interface output stream. */
	protected CfaslOutputStream cfaslOutputStream;

	/** The name of the computer hosting the OpenCyc server. */
	protected String hostName;

	/** The tcp port from which the asciiPort and cfaslPorts are derived. */
	protected int basePort;

	/** The tcp port assigned to the ascii connection to the OpenCyc server. */
	protected int asciiPort;

	/** The tcp port assigned to the binary connection to the OpenCyc server. */
	protected int cfaslPort;

	/** The tcp socket assigned to the ascii connection to the OpenCyc server. */
	protected Socket asciiSocket;

	/** The tcp socket assigned to the binary connection to the OpenCyc server. */
	protected Socket cfaslSocket;

	/**
	 * The timer which optionally monitors the duration of requests to the
	 * OpenCyc server.
	 */
	protected static final Timer notimeout = new Timer();

	/**
	 * Indicates if the response from the OpenCyc server is a symbolic
	 * expression (enclosed in parentheses).
	 */
	protected boolean isSymbolicExpression = false;

	/**
	 * A reference to the parent CycAccess object for dereferencing constants in
	 * ascii symbolic expressions.
	 */
	protected CycAccess cycAccess;

	/**
	 * An indicator for ascii communications mode that strings should retain
	 * their quote delimiters.
	 */
	protected boolean quotedStrings;

	/** outbound request serial id */
	static protected int apiRequestId = 0;

	/** The default priority of a task-processor request. */
	public static final int DEFAULT_PRIORITY = 3;

	/** name of my api client */
	protected String myClientName = "api client";

	/**
	 * Implements an association: apiRequestId --> waiting thread info, where
	 * waiting thread info is an array of two objects: 1. the latch waiting for
	 * the response from the Cyc server (number 1 in no longer valid
	 * 
	 * @todo fix this description) 2. the api-request in CycList form Used when
	 *       submitting concurrent requests to the task-processor.
	 */
	protected Hashtable waitingReplyThreads = new Hashtable();

	/**
	 * handles responses from task-processor requests in binary communication
	 * mode.
	 */
	protected TaskProcessorBinaryResponseHandler taskProcessorBinaryResponseHandler;

	/**
	 * Indicates to the taskProcessor response handlers that the server
	 * connection is closed.
	 */
	protected boolean taskProcessingEnded = false;

	/** Indicates that the task processing thread is dead */
	protected boolean taskProcessingThreadDead = false;

	/**
	 * Universally Unique ID that identifies this CycConnection to the Cyc
	 * server. It is used when establishing the (second) asychronous socket
	 * connection.
	 */
	protected UUID uuid;

	/**
	 * Constructs a new CycConnection using the given socket obtained from the
	 * parent AgentManager listener.
	 * 
	 * @param cfaslSocket
	 *            tcp socket which forms the binary connection to the OpenCyc
	 *            server
	 * 
	 * @throws IOException
	 *             when communication error occurs
	 */
	public CycConnection(Socket cfaslSocket) throws IOException {
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
	 * Constructs a new CycConnection object using the default host name,
	 * default base port number and binary communication mode. When CycAccess is
	 * null as in this case, diagnostic output is reduced.
	 * 
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 * @throws IOException
	 *             when communications error occurs
	 * @throws CycApiException
	 *             when an api error occurs
	 */
	public CycConnection() throws IOException, UnknownHostException,
			CycApiException {
		this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT, DEFAULT_COMMUNICATION_MODE,
				null);
	}

	/**
	 * Constructs a new CycConnection object using the default host name,
	 * default base port number with binary communication mode, and the given
	 * CycAccess object.
	 * 
	 * @param cycAccess
	 *            the given CycAccess object which provides api services over
	 *            this CycConnection object
	 * 
	 * @throws CycApiException
	 *             when a Cyc api exception occurs
	 * @throws IOException
	 *             when communication error occurs
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 */
	public CycConnection(CycAccess cycAccess) throws IOException,
			UnknownHostException, CycApiException {
		this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT, DEFAULT_COMMUNICATION_MODE,
				cycAccess);
	}

	/**
	 * Constructs a new CycConnection object using a given host name, the given
	 * base port number, the given communication mode, and the given CycAccess
	 * object
	 * 
	 * @param hostName
	 *            the cyc server host name
	 * @param basePort
	 *            the base tcp port on which the OpenCyc server is listening for
	 *            connections.
	 * @param communicationMode
	 *            either ASCII_MODE or BINARY_MODE
	 * @param cycAccess
	 *            the given CycAccess object which provides api services over
	 *            this CycConnection object
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	public CycConnection(String hostName, int basePort, int communicationMode,
			CycAccess cycAccess) throws IOException, UnknownHostException,
			CycApiException {
		this(hostName, basePort, communicationMode,
				CycConnection.DEFAULT_MESSAGING_MODE, cycAccess);
	}

	/**
	 * Constructs a new CycConnection object using a given host name, the given
	 * base port number, the given communication mode, and the given CycAccess
	 * object
	 * 
	 * @param hostName
	 *            the cyc server host name
	 * @param basePort
	 *            the base tcp port on which the OpenCyc server is listening for
	 *            connections.
	 * @param communicationMode
	 *            either ASCII_MODE or BINARY_MODE
	 * @param messagingMode
	 *            either SERIAL_MESSAGING_MODE or CONCURRENT_MESSAGING_MODE
	 * @param cycAccess
	 *            the given CycAccess object which provides api services over
	 *            this CycConnection object
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	public CycConnection(String hostName, int basePort, int communicationMode,
			int messagingMode, CycAccess cycAccess) throws IOException,
			UnknownHostException, CycApiException {
		this.hostName = hostName;
		this.basePort = basePort;
		asciiPort = basePort + ASCII_PORT_OFFSET;
		cfaslPort = basePort + CFASL_PORT_OFFSET;

		if ((communicationMode != ASCII_MODE)
				&& (communicationMode != BINARY_MODE)) {
			throw new CycApiException("Invalid communication mode "
					+ communicationMode);
		}

		this.communicationMode = communicationMode;

		if ((messagingMode != SERIAL_MESSAGING_MODE)
				&& (messagingMode != CONCURRENT_MESSAGING_MODE)) {
			throw new CycApiException("Invalid messaging mode " + messagingMode);
		}

		if ((communicationMode == ASCII_MODE)
				&& (messagingMode == CONCURRENT_MESSAGING_MODE)) {
			this.messagingMode = SERIAL_MESSAGING_MODE;
		} else {
			this.messagingMode = messagingMode;
		}

		final ConnectionTimer connectionTimer = new ConnectionTimer();
		connectionTimer.start();
		this.cycAccess = cycAccess;
		initializeApiConnections();

		if (trace > API_TRACE_NONE) {
			if (communicationMode == ASCII_MODE) {
				Log.current.println("Ascii connection " + asciiSocket);
			} else {
				Log.current.println("Binary connection " + cfaslSocket);
			}
		}

		uuid = UUID.randomUUID();

		if (this.messagingMode == CONCURRENT_MESSAGING_MODE) {
			initializeConcurrentProcessing();
		}

		/**
		 * for testing the connection timer try { Thread.sleep(100000); } catch
		 * (InterruptedException e) { }
		 */
		connectionTimer.isCycConnectionEstablished = true;
	}

	/**
	 * Sets the client listening port on which api response are received from
	 * Cyc
	 * 
	 * @param _clientPort
	 *            the given port number, or zero if the operating system is to
	 *            choose a free port
	 */
	public static void setClientPort(int _clientPort) {
		clientPort = _clientPort;
	}

	/**
	 * Sets the client host name on which api response are received from Cyc.
	 * This is used when executing the client behind a firewall and using SSH
	 * port forwarding. The client must appear to reside on the same host as
	 * Cyc.
	 * 
	 * @param _clientHostName
	 *            the given port number, or zero if the operating system is to
	 *            choose a free port
	 */
	public static void setClientHostName(String _clientHostName) {
		clientHostName = _clientHostName;
	}

	public int getConnectionType() {
		return CycAccess.PERSISTENT_CONNECTION;
	}

	/**
	 * Initializes the OpenCyc ascii socket and the OpenCyc binary socket
	 * connections.
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 */
	private void initializeApiConnections() throws IOException,
			UnknownHostException {
		if (Log.current == null) {
			Log.makeLog("cyc-api.log");
		}

		if (communicationMode == ASCII_MODE) {
			asciiSocket = new Socket(hostName, asciiPort);
			int val = asciiSocket.getReceiveBufferSize();
			asciiSocket.setReceiveBufferSize(val * 2);
			asciiSocket.setTcpNoDelay(true);
			in = new BufferedReader(new InputStreamReader(asciiSocket
					.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(asciiSocket
					.getOutputStream()));
		} else {
			cfaslSocket = new Socket(hostName, cfaslPort);
			int val = cfaslSocket.getReceiveBufferSize();
			cfaslSocket.setReceiveBufferSize(val * 2);
			cfaslSocket.setTcpNoDelay(true);
			cfaslInputStream = new CfaslInputStream(cfaslSocket
					.getInputStream());
			cfaslInputStream.trace = trace;
			cfaslOutputStream = new CfaslOutputStream(cfaslSocket
					.getOutputStream());
			cfaslOutputStream.trace = trace;
		}
	}

	/**
	 * Initializes the concurrent processing mode. Use serial messaging mode to
	 * ensure the Cyc task processors are initialized, then start this
	 * connection's taskProcessor response handler thread.
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws UnknownHostException
	 *             when the cyc server cannot be found
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	protected void initializeConcurrentProcessing() throws IOException,
			UnknownHostException, CycApiException {
		taskProcessorBinaryResponseHandler = new TaskProcessorBinaryResponseHandler(
				Thread.currentThread(), this, clientPort);

		// the start method will not return until the inbound socket
		// has had time to initialize
		taskProcessorBinaryResponseHandler.start();

		if (!passiveConnection) {
			// Send request to Cyc server to open its outbound socket to
			// our awaiting listener.
			CycList request = new CycList();
			request.add(new CycSymbol("INITIALIZE-JAVA-API-SOCKET"));
			request.add(uuid.toString());
			if (clientHostName != null)
				request.add(clientHostName);
			else
				request.add(InetAddress.getLocalHost().getHostAddress());
			request
					.add(new Integer(
							taskProcessorBinaryResponseHandler.localClientListenerPort));
			sendBinary(request);
			// ignore response
			receiveBinary();
		}
	}

	/**
	 * Ensures that the api socket connections are closed when this object is
	 * garbage collected.
	 */
	protected void finalize() {
		close();
	}

	/**
	 * Close the api sockets and streams.
	 */
	public void close() {
		if (asciiSocket != null) {
			if (trace > API_TRACE_NONE) {
				Log.current.println("Closing asciiSocket " + asciiSocket);
			}

			if (out != null) {
				try {
					out.write("(API-QUIT)\n");
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error quitting the api connection "
							+ e.getMessage());
				}

				try {
					out.flush();
				} catch (Exception e) {
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error finalizing the api connection "
							+ e.getMessage());
				}
			}

			if (asciiSocket != null) {
				try {
					asciiSocket.close();
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error closing the api connection "
							+ e.getMessage());
				}
			}
		}

		if (isValidBinaryConnection()) {
			if (trace > API_TRACE_NONE) {
				Log.current.println("Closing cfaslSocket " + cfaslSocket);
			}

			if (cfaslOutputStream != null) {
				CycList command;

				if (messagingMode == CONCURRENT_MESSAGING_MODE) {
					if (trace > API_TRACE_NONE) {
						Log.current.println("Closing server api socket, uuid: "
								+ uuid);
					}
					try {
						if (!passiveConnection) {
							command = new CycList();
							command.add(CycObjectFactory
									.makeCycSymbol("CLOSE-JAVA-API-SOCKET"));
							command.add(uuid.toString());
							sendBinary(command);
							if (cfaslInputStream != null)
								receiveBinary();
						}
					} catch (Exception e) {
					}
				}

				if (trace > API_TRACE_NONE) {
					Log.current.println("Sending API-QUIT to server");
				}

				command = new CycList();
				command.add(CycObjectFactory.makeCycSymbol("API-QUIT"));

				try {
					cfaslOutputStream.writeObject(command);
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error quitting the api connection "
							+ e.getMessage());
				}

				try {
					cfaslOutputStream.flush();
				} catch (Exception e) {
				}
			}

			if (cfaslInputStream != null) {
				if (trace > API_TRACE_NONE) {
					Log.current.println("Closing cfaslInputStream");
				}

				try {
					cfaslInputStream.close();
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error finalizing the api connection "
							+ e.getMessage());
				}
			}

			if (cfaslSocket != null) {
				if (trace > API_TRACE_NONE) {
					Log.current.println("Closing cfaslSocket");
				}

				try {
					cfaslSocket.close();
				} catch (Exception e) {
					Log.current.printStackTrace(e);
					Log.current.println("Error closing the api connection "
							+ e.getMessage());
				}
			}
		}

		if (messagingMode == CONCURRENT_MESSAGING_MODE) {
			taskProcessingEnded = true;

			if (trace > API_TRACE_NONE) {
				Log.current
						.println("Interrupting any threads awaiting replies");
			}

			interruptAllWaitingReplyThreads();

			try {
				taskProcessorBinaryResponseHandler.interrupt();
				taskProcessorBinaryResponseHandler.close();
				if (trace > API_TRACE_NONE) {
					Log.current
							.println("Waiting at most 500 milliseconds for the taskProcessorBinaryResponseHandler thread to die");
				}

				taskProcessorBinaryResponseHandler.join(500);

				if (!taskProcessingThreadDead) {
					if (trace > API_TRACE_NONE) {
						Log.current
								.println("The taskProcessorBinaryResponseHandler thread has not died, so continuing");
					}
				}
			} catch (Exception e) {
			}
		}

		if (trace > API_TRACE_NONE) {
			Log.current.println("Connection closed for " + connectionInfo());
		}
	}

	/**
	 * Return the name of the host to which the CycConnection is established.
	 * 
	 * @return the name of the Host to which this <tt>CycConnection</tt> is
	 *         connected.
	 */
	public String getHostName() {
		return this.hostName;
	}

	/**
	 * Return the base port to which the CycConnection is established.
	 * 
	 * @return the port to which this <tt>CycConnection</tt> is connected.
	 */
	public int getBasePort() {
		return this.basePort;
	}

	/**
	 * Return the ASCII port to which the CycConnection is established.
	 * 
	 * @return the ASCII to which this <tt>CycConnection</tt> is connected.
	 */
	public int getAsciiPort() {
		return this.asciiPort;
	}

	/**
	 * Return the CFASL port to which the CycConnection is established.
	 * 
	 * @return the CFASL port to which this <tt>CycConnection</tt> is
	 *         connected.
	 */
	public int getCfaslPort() {
		return this.cfaslPort;
	}

	/**
	 * Send a message to Cyc and return the <tt>Boolean</tt> true as the first
	 * element of an object array, and the cyc response Symbolic Expression as
	 * the second element. If an error occurs the first element is
	 * <tt>Boolean</tt> false and the second element is the error message
	 * string.
	 * 
	 * @param message
	 *            the api command
	 * 
	 * @return an array of two objects, the first is an response status object
	 *         either a Boolean (binary mode) or Integer (ascii mode), and the
	 *         second is the response object or error string.
	 * 
	 * @throws IOException
	 *             when a commuications error occurs
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	public Object[] converse(Object message) throws IOException,
			CycApiException {
		return converse(message, notimeout);
	}

	/**
	 * Send a message to Cyc and return the response code as the first element
	 * of an object array, and the cyc response Symbolic Expression as the
	 * second element, spending no less time than the specified timer allows but
	 * throwing a <code>TimeOutException</code> at the first opportunity where
	 * that time limit is exceeded. If an error occurs the second element is the
	 * error message string.
	 * 
	 * @param message
	 *            the api command which must be a String or a CycList
	 * @param timeout
	 *            a <tt>Timer</tt> object giving the time limit for the api
	 *            call
	 * 
	 * @return an array of two objects, the first is a Boolean response status
	 *         object, and the second is the response object or error string.
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws TimeOutException
	 *             when the time limit is exceeded
	 * @throws CycApiException
	 *             when a Cyc api error occurs
	 * @throws RuntimeException
	 *             if CycAccess is not present
	 */
	public Object[] converse(Object message, Timer timeout) throws IOException,
			TimeOutException, CycApiException {
		if (communicationMode == CycConnection.ASCII_MODE) {
			CycList messageCycList;

			if (message instanceof String) {
				if (cycAccess == null) {
					throw new RuntimeException(
							"CycAccess is required to process commands in string form");
				}

				messageCycList = cycAccess.makeCycList((String) message);
			} else if (message instanceof CycList) {
				messageCycList = (CycList) message;
			} else {
				throw new CycApiException("Invalid class for message "
						+ message);
			}

			String messageString = messageCycList.cyclifyWithEscapeChars();

			return converseAscii(messageString, timeout);
		} else { // Binary (CFASL) mode

			CycList messageCycList;

			if (message instanceof CycList) {
				messageCycList = (CycList) message;
			} else if (message instanceof String) {
				if (cycAccess == null) {
					throw new RuntimeException(
							"CycAccess is required to process commands in string form");
				}

				messageCycList = cycAccess.makeCycList((String) message);
			} else {
				throw new CycApiException("Invalid class for message "
						+ message);
			}

			messageCycList = substituteForBackquote(messageCycList, timeout);

			return converseBinary(messageCycList, timeout);
		}
	}

	/**
	 * Substitute a READ-FROM-STRING expression for expressions directly
	 * containing a backquote symbol. This transformation is only required for
	 * the binary api, which does not parse the backquoted expression.
	 * 
	 * @param messageCycList
	 *            the given expression
	 * @param timeout
	 *            a <tt>Timer</tt> object giving the time limit for the api
	 *            call
	 * 
	 * @return the expression with a READ-FROM-STRING expression substituted for
	 *         expressions directly containing a backquote symbol
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws CycApiException
	 *             when a Cyc api error occurs
	 */
	protected CycList substituteForBackquote(CycList messageCycList,
			Timer timeout) throws IOException, CycApiException {
		if (messageCycList.treeContains(CycObjectFactory.backquote)) {
			CycList substituteCycList = new CycList();
			substituteCycList.add(CycObjectFactory
					.makeCycSymbol("read-from-string"));
			substituteCycList.add(messageCycList.cyclify());

			Object[] response = converseBinary(substituteCycList, timeout);

			if ((response[0].equals(Boolean.TRUE))
					&& (response[1] instanceof CycList)) {
				CycList backquoteExpression = (CycList) response[1];

				return backquoteExpression.subst(CycObjectFactory
						.makeCycSymbol("api-bq-list"), CycObjectFactory
						.makeCycSymbol("bq-list"));
			}

			throw new CycApiException("Invalid backquote substitution in "
					+ messageCycList + "\nstatus" + response[0] + "\nmessage "
					+ response[1]);
		}

		return messageCycList;
	}

	private class WaitingWorkerInfo {

		SubLWorker worker;

		CycList taskProcessorRequest;

		WaitingWorkerInfo(SubLWorker worker, CycList taskProcessorRequest) {
			this.worker = worker;
			this.taskProcessorRequest = taskProcessorRequest;
		}

		SubLWorker getWorker() {
			return worker;
		}

		CycObject getMessage() {
			return (CycObject) taskProcessorRequest.get(1);
		}

	}

	/**
	 * Send a message to Cyc and return the response code as the first element
	 * of an object array, and the cyc response Symbolic Expression as the
	 * second element, spending no less time than the specified timer allows but
	 * throwing a <code>TimeOutException</code> at the first opportunity where
	 * that time limit is exceeded. If an error occurs the second element is the
	 * error message string. The concurrent mode of Cyc server communication is
	 * supported by Cyc's pool of transaction processor threads, each of which
	 * can concurrently process an api request.
	 * 
	 * @param message
	 *            the api command
	 * @param timeout
	 *            a <tt>Timer</tt> object giving the time limit for the api
	 *            call
	 * 
	 * @return an array of two objects, the first is an Boolean response code,
	 *         and the second is the response object or error string.
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws TimeOutException
	 *             when the time limit is exceeded
	 * @throws CycApiException
	 *             when a Cyc api error occurs
	 */
	public Object[] converseBinary(CycList message, Timer timeout)
			throws IOException, TimeOutException, CycApiException {
		DefaultSubLWorkerSynch worker = new DefaultSubLWorkerSynch(message,
				this.cycAccess);
		Object[] result = new Object[2];
		try {
			result[1] = worker.getWork();
		} catch (IOException xcpt) {
			throw xcpt;
		} catch (TimeOutException xcpt) {
			throw xcpt;
		} catch (CycApiServerSideException xcpt) {
			// @note: this implements a legacy API of converseBinary()
			result[0] = Boolean.FALSE;
			result[1] = xcpt.getMessage();
			return result;
		} catch (CycApiException xcpt) {
			throw xcpt;
		} catch (Exception xcpt) {
			throw new RuntimeException(xcpt);
		}
		result[0] = worker.getStatus() == SubLWorkerStatus.FINISHED_STATUS ? Boolean.TRUE
				: Boolean.FALSE;
		return result;
	}

	public void cancelCommunication(SubLWorker worker)
			throws java.io.IOException {
		Integer id = worker.getId();
		if (id.intValue() < 0) {
			// @note serial communications cannot be canceled right now
			return;
		}
		String command = "(fif (" + "terminate-active-task-process" + " "
				+ worker.getId() + " \"" + uuid + "\" " + ":cancel"
				+ ") '(ignore) '(ignore))";
		sendBinary(cycAccess.makeCycList(command));
		// the SubL implementation of CANCEL will send a CANCEL event back,
		// which will cleanup the waiting thread info and signal the termination
		// event, so no need to perform event signaling and cleanup
	}

	public void abortCommunication(SubLWorker worker)
			throws java.io.IOException {
		Integer id = worker.getId();
		if (id.intValue() < 0) {
			// @note serial communications cannot be canceled right now
			return;
		}
		try {
			String command = "(fif (" + "terminate-active-task-process" + " "
					+ worker.getId() + " \"" + uuid + "\" " + ":abort"
					+ ") '(ignore) '(ignore))";
			sendBinary(cycAccess.makeCycList(command));
		} finally {
			// the SubL implementation of ABORT will not send anything back,
			// so we do need to perform event signaling and cleanup
			worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
					SubLWorkerStatus.ABORTED_STATUS, null));
			waitingReplyThreads.remove(id);
		}
	}

	private boolean inAWTEventThread() {
		try {
			return javax.swing.SwingUtilities.isEventDispatchThread();
		} catch (Throwable e) {
			return false;
		}
	}

	protected void converseBinary(CycList message, Timer timeout,
			SubLWorker worker) throws IOException, TimeOutException,
			CycApiException {

		if ((worker instanceof SubLWorkerSynch) && inAWTEventThread()) {
			throw new CycApiException(
					"Invalid attempt to communicate with Cyc "
							+ "from the AWT event thread.\n\n" + worker);
		}
		if (this.messagingMode == SERIAL_MESSAGING_MODE) {
			SubLWorkerEvent event = new SubLWorkerEvent(worker, new Integer(-1));
			worker.fireSubLWorkerStartedEvent(event);
			sendBinary(message);
			Object[] result = receiveBinary();
			Object work = result[1];
			worker.fireSubLWorkerDataAvailableEvent(new SubLWorkerEvent(worker,
					work, 100.0f));
			worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
					SubLWorkerStatus.FINISHED_STATUS, null));
		} else { // CONCURRENT_MESSAGING_MODE
			CycSymbol taskProcessorRequestSymbol = CycObjectFactory
					.makeCycSymbol("task-processor-request");
			Integer id = null;
			CycList taskProcessorRequest = null;
			if (message.first().equals(taskProcessorRequestSymbol)) {
				// client has supplied the task-processor-request form
				taskProcessorRequest = message;
				id = (Integer) message.third();
			} else {
				id = nextApiRequestId();
				taskProcessorRequest = new CycList();
				taskProcessorRequest.add(taskProcessorRequestSymbol); // function
				taskProcessorRequest.add(message); // request
				taskProcessorRequest.add(id); // id
				taskProcessorRequest.add(new Integer(DEFAULT_PRIORITY)); // priority
				taskProcessorRequest.add(myClientName); // requestor
				taskProcessorRequest.add(CycObjectFactory.nil); // client-bindings
				taskProcessorRequest.add(uuid.toString()); // uuid to identify
															// this client
			}
			WaitingWorkerInfo waitingWorkerInfo = new WaitingWorkerInfo(worker,
					taskProcessorRequest);
			// tell everyone this is getting started
			waitingReplyThreads.put(id, waitingWorkerInfo);
			SubLWorkerEvent event = new SubLWorkerEvent(worker, id);
			worker.fireSubLWorkerStartedEvent(event);
			// start communication
			sendBinary(taskProcessorRequest);
		} // end-else: CONCURRENT_MESSAGING_MODE
	}

	/**
	 * Returns the next apiRequestId.
	 * 
	 * @return the next apiRequestId
	 */
	static public synchronized Integer nextApiRequestId() {
		return new Integer(++apiRequestId);
	}

	/**
	 * Sends an object to the CYC server. If the connection is not already open,
	 * it is opened. The object must be a valid CFASL-translatable: an Integer,
	 * Float, Double, Boolean, String, or cyc object.
	 * 
	 * @param message
	 *            the api command
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 */
	public synchronized void sendBinary(Object message) throws IOException {
		if (trace > API_TRACE_NONE) {
			if (message instanceof CycList) {
				Log.current.println(((CycList) message).safeToString()
						+ " --> cyc");
			} else if (message instanceof CycFort) {
				Log.current.println(((CycFort) message).safeToString()
						+ " --> cyc");
			} else {
				Log.current.println(message + " --> cyc");
			}
		}
		cfaslOutputStream.writeObject(message);
		cfaslOutputStream.flush();
	}

	/**
	 * Receives an object from the CYC server.
	 * 
	 * @return an array of two objects, the first is a Boolean response, and the
	 *         second is the response object or error string.
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	public synchronized Object[] receiveBinary() throws IOException,
			CycApiException {
		Object status = cfaslInputStream.readObject();
		Object response = cfaslInputStream.readObject();
		Object[] answer = { null, null };

		if ((status == null) || status.equals(CycObjectFactory.nil)) {
			answer[0] = Boolean.FALSE;
			answer[1] = response;

			if (trace > API_TRACE_NONE) {
				String responseString = null;

				if (response instanceof CycList) {
					responseString = ((CycList) response).safeToString();
				} else if (response instanceof CycFort) {
					responseString = ((CycFort) response).safeToString();
				} else {
					responseString = response.toString();
				}

				Log.current.println("received error = (" + status + ") "
						+ responseString);
			}

			return answer;
		}

		answer[0] = Boolean.TRUE;

		if (cycAccess == null) {
			answer[1] = response;
		} else if (cycAccess.deferObjectCompletion) {
			answer[1] = response;
		} else {
			answer[1] = cycAccess.completeObject(response);
		}

		if (trace > API_TRACE_NONE) {
			String responseString = null;

			if (response instanceof CycList) {
				responseString = ((CycList) response).safeToString();
			} else if (response instanceof CycFort) {
				responseString = ((CycFort) response).safeToString();
			} else {
				responseString = response.toString();
			}

			Log.current
					.println("cyc --> (" + answer[0] + ") " + responseString);
		}

		return answer;
	}

	/**
	 * Receives a binary (cfasl) api request from a cyc server. Unlike the api
	 * response handled by the receiveBinary method, this method does not expect
	 * an input status object.
	 * 
	 * @return the api request expression.
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws CycApiException
	 *             when a Cyc API exception occurs
	 */
	public CycList receiveBinaryApiRequest() throws IOException,
			CycApiException {
		CycList apiRequest = (CycList) cfaslInputStream.readObject();

		if (trace > API_TRACE_NONE) {
			Log.current.println("cyc --> (api-request) "
					+ apiRequest.safeToString());
		}

		return apiRequest;
	}

	/**
	 * Sends a binary (cfasl) api response to a cyc server. This method prepends
	 * a status object (the symbol T) to the message.
	 * 
	 * @param message
	 *            the given binary api response
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 */
	public void sendBinaryApiResponse(Object message) throws IOException,
			CycApiException {
		if (trace > API_TRACE_NONE) {
			String messageString = null;

			if (message instanceof CycList) {
				messageString = ((CycList) message).safeToString();
			} else if (message instanceof CycFort) {
				messageString = ((CycFort) message).safeToString();
			} else {
				messageString = message.toString();
			}

			Log.current.println("(" + CycObjectFactory.t + ") " + messageString
					+ " --> cyc");
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
	 * where that time limit is exceeded. If an error occurs the first element
	 * is Boolean.FALSE and the second element is the error message string. The
	 * concurrent mode of Cyc server communication is supported by Cyc's pool of
	 * transaction processor threads, each of which can concurrently process an
	 * api request.
	 * 
	 * @param message
	 *            the api command
	 * @param timeout
	 *            a <tt>Timer</tt> object giving the time limit for the api
	 *            call
	 * 
	 * @return an array of two objects, the first is an Integer response code,
	 *         and the second is the response object or error string.
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws TimeOutException
	 *             when the time limit is exceeded
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 * @throws RuntimeException
	 *             if CycAccess is not present
	 */

	synchronized protected Object[] converseAscii(String message, Timer timeout)
			throws IOException, TimeOutException, CycApiException {
		isSymbolicExpression = false;

		Object[] response = converseUsingAsciiStrings(message, timeout);

		if (response[0].equals(Boolean.TRUE)) {
			String answer = ((String) response[1]).trim();

			if (StringUtils.isDelimitedString(answer)) {
				response[1] = StringUtils.removeDelimiters(answer);

				// Return the string.
				return response;
			}

			if (isSymbolicExpression) {
				// Recurse to complete contained CycConstant, CycNart objects.
				if (cycAccess == null) {
					throw new RuntimeException(
							"CycAccess is required to process commands in string form");
				}

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
				if (cycAccess == null) {
					throw new RuntimeException(
							"CycAccess is required to process commands in string form");
				}

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
			} catch (NumberFormatException e) {
			}

			if (answer.endsWith("d0")
					&& (StringUtils.hasNumericChar(answer.substring(0, answer
							.length() - 2)))) {
				String floatPart = answer.substring(0, answer.length() - 2);
				response[1] = new Double(floatPart);

				// Return the double.
				return response;
			}

			throw new CycApiException("Ascii api response not understood "
					+ answer);
		} else {
			return response;
		}
	}

	/**
	 * Send a message to Cyc and return the response code as the first element
	 * of a object array, and the Cyc response string as the second element.
	 * 
	 * @param message
	 *            the given ascii message
	 * @param timeout
	 *            the given time limit
	 * 
	 * @return eturn the response code as the first element of a object array,
	 *         and the Cyc response string as the second element
	 * 
	 * @throws IOException
	 *             when a communication error occurs
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 * @throws TimeOutException
	 *             when the time limit is exceeded
	 */
	protected Object[] converseUsingAsciiStrings(String message, Timer timeout)
			throws IOException, CycApiException, TimeOutException {
		if (trace > API_TRACE_NONE) {
			Log.current.println(message + " --> cyc");
		}

		out.write(message);

		if (!message.endsWith("\n")) {
			out.newLine();
		}

		out.flush();

		if (trace > API_TRACE_NONE) {
			Log.current.print("cyc --> ");
		}

		Object[] answer = readAsciiCycResponse(timeout);

		if (trace > API_TRACE_NONE) {
			Log.current.println();
		}

		return answer;
	}

	/**
	 * Read the cyc response.
	 * 
	 * @param timeout
	 *            the given time limit
	 * 
	 * @return the Cyc response
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 * @throws TimeOutException
	 *             when the time limit is exceeded
	 * @throws RuntimeException
	 *             if CycAccess is not present
	 */
	private Object[] readAsciiCycResponse(Timer timeout) throws IOException,
			TimeOutException {
		Object[] answer = { null, null };

		// Parse the response code digits.
		StringBuffer responseCodeDigits = new StringBuffer();

		while (true) {
			if (timeout != null) {
				timeout.checkForTimeOut();
			}

			int ch = in.read();

			if (trace > API_TRACE_NONE) {
				Log.current.print((char) ch);
			}

			if (ch == ' ') {
				break;
			}

			responseCodeDigits.append((char) ch);
		}

		int responseCode = 0;

		try {
			responseCode = (new Integer(responseCodeDigits.toString().trim()))
					.intValue();
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid response code digits "
					+ responseCodeDigits);
		}

		if (responseCode == 200) {
			answer[0] = Boolean.TRUE;
		} else {
			answer[0] = Boolean.FALSE;
		}

		in.mark(1);

		int ch = in.read();
		in.reset();

		if (ch == '(') {
			isSymbolicExpression = true;
			answer[1] = readSymbolicExpression();
		} else if (ch == '"') {
			answer[1] = readQuotedString();
		} else {
			answer[1] = readAtom();
		}

		// Read the terminating newline.
		ch = in.read();

		if (trace > API_TRACE_NONE) {
			Log.current.print((char) ch);
		}

		return answer;
	}

	/**
	 * Reads a complete symbolic expression as an ascii string.
	 * 
	 * @return a complete symbolic expression as an ascii string
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 */
	private String readSymbolicExpression() throws IOException {
		int parenLevel = 0;
		boolean isQuotedString = false;
		StringBuffer result = new StringBuffer();
		int ch = in.read();

		if (trace > API_TRACE_NONE) {
			Log.current.print((char) ch);
		}

		parenLevel++;
		result.append((char) ch);

		while (parenLevel != 0) {
			ch = in.read();

			if (trace > API_TRACE_NONE) {
				Log.current.print((char) ch);
			}

			if (ch == '"') {
				if (isQuotedString) {
					isQuotedString = false;
				} else {
					isQuotedString = true;
				}
			}

			if (!isQuotedString) {
				if (ch == '(') {
					parenLevel++;
				}

				if (ch == ')') {
					parenLevel--;
				}
			}

			result.append((char) ch);
		}

		return result.toString();
	}

	/**
	 * Reads a quoted string
	 * 
	 * @return the quoted string read
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 */
	private String readQuotedString() throws IOException {
		StringBuffer result = new StringBuffer();
		int ch = in.read();

		if (trace > API_TRACE_NONE) {
			Log.current.print((char) ch);
		}

		boolean escapedChar = false;

		while (true) {
			ch = in.read();

			if (trace > API_TRACE_NONE) {
				Log.current.print((char) ch);
			}

			if ((ch == '"') && (!escapedChar)) {
				return "\"" + result.toString() + "\"";
			}

			if (escapedChar) {
				escapedChar = false;

				if (ch == 'n') {
					result.append('\n');
				} else {
					result.append((char) ch);
				}
			} else if (ch == '\\') {
				escapedChar = true;
			} else {
				result.append((char) ch);
			}
		}
	}

	/**
	 * Reads an atom.
	 * 
	 * @return the atom read as an ascii string
	 * 
	 * @throws IOException
	 *             when a communications error occurs
	 */
	private String readAtom() throws IOException {
		StringBuffer result = new StringBuffer();

		while (true) {
			in.mark(1);

			int ch = in.read();

			if (trace > API_TRACE_NONE) {
				Log.current.print((char) ch);
			}

			if (ch == '\r') {
				break;
			}

			if (ch == '\n') {
				break;
			}

			result.append((char) ch);
		}

		in.reset();

		return result.toString();
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
		setTrace(API_TRACE_DETAILED);
	}

	/**
	 * Turns off the diagnostic trace of socket messages.
	 */
	public void traceOff() {
		setTrace(API_TRACE_NONE);
	}

	/**
	 * Returns the trace value.
	 * 
	 * @return the trace value
	 */
	public int getTrace() {
		return trace;
	}

	/**
	 * Sets the socket messages diagnostic trace value.
	 * 
	 * @param trace
	 *            the new socket messages diagnostic trace value
	 */
	public void setTrace(int trace) {
		this.trace = trace;

		if (communicationMode == BINARY_MODE) {
			cfaslInputStream.trace = trace;
			cfaslOutputStream.trace = trace;

			if (taskProcessorBinaryResponseHandler != null) {
				taskProcessorBinaryResponseHandler.inboundStream.trace = trace;
			}
		}
	}

	/**
	 * Answers true iff this is a valid binary (cfasl) connection to Cyc.
	 * 
	 * @return true iff this is a valid binary (cfasl) connection to Cyc
	 */
	public boolean isValidBinaryConnection() {
		if (cfaslSocket == null) {
			return false;
		}

		// TODO enable the below statement when tomcat servers are typically 1.4
		// compliant
		// return cfaslSocket.isConnected();
		try {
			OutputStream outputStream = cfaslSocket.getOutputStream();
		} catch (IOException e) {
			return false;
		}

		if ((taskProcessorBinaryResponseHandler == null)
				|| (taskProcessorBinaryResponseHandler.inboundSocket == null)) {
			return false;
		}

		try {
			OutputStream outputStream = taskProcessorBinaryResponseHandler.inboundSocket
					.getOutputStream();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Returns connection information, suitable for diagnostics.
	 * 
	 * @return connection information, suitable for diagnostics
	 */
	public String connectionInfo() {
		return "host " + hostName + ", asciiPort " + asciiPort + ", cfaslPort "
				+ cfaslPort;
	}

	/**
	 * Gets the UUID that identifies this java api client connection.
	 * 
	 * @return the UUID that identifies this java api client connection
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Sets the client name of this api connection.
	 * 
	 * @param myClientName
	 *            the client name of this api connection
	 */
	public void setMyClientName(String myClientName) {
		this.myClientName = myClientName;
	}

	/**
	 * Gets the client name of this api connection.
	 * 
	 * @return the client name of this api connection
	 */
	public String getMyClientname() {
		return myClientName;
	}

	/**
	 * Recovers from a socket error by interrupting all the waiting reply
	 * threads. Each awakened thread will detect the error condition and throw
	 * an IOExecption.
	 */
	protected void interruptAllWaitingReplyThreads() {
		Iterator iter = waitingReplyThreads.values().iterator();

		while (iter.hasNext()) {
			WaitingWorkerInfo waitingWorkerInfo = (WaitingWorkerInfo) iter
					.next();
			if (trace > API_TRACE_NONE) {
				Log.current.println("Interrupting reply worker "
						+ waitingWorkerInfo.getWorker());
			}
			try {
				waitingWorkerInfo.worker.cancel();
			} catch (java.io.IOException xcpt) {
				if (trace > API_TRACE_NONE) {
					Log.current.println("Could not interrupt reply worker "
							+ waitingWorkerInfo.getWorker() + ": exception: "
							+ xcpt);
				}
			}
		}
	}

	/**
	 * Gets the dictionary of waiting reply thread information objects.
	 * 
	 * @return the dictionary of waiting reply thread information objects
	 */
	public Hashtable getWaitingReplyThreadInfos() {
		return waitingReplyThreads;
	}

	/**
	 * Resets the Cyc task processor which is currently processing the
	 * api-request specified by the given id. If none of the task processors is
	 * currently processessing the specified api-request, then the reset request
	 * is ignored. When reset, the Cyc task processor returns an error message
	 * to the waiting client thread. The error message consists of
	 * "reset\nTHE-API-REQUEST".
	 * 
	 * @param id
	 *            the id of the api-request which is to be interrupted and
	 *            cancelled
	 * 
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 * @throws IOException
	 *             when a communication error occurs
	 */
	public void resetTaskProcessorById(Integer id) throws CycApiException,
			IOException {
		resetTaskProcessorById(id.intValue());
	}

	/**
	 * Resets the Cyc task processor which is currently processing the
	 * api-request specified by the given id. If none of the task processors is
	 * currently processessing the specified api-request, then the reset request
	 * is ignored. When reset, the Cyc task processor returns an error message
	 * to the waiting client thread.
	 * 
	 * @param id
	 *            the id of the api-request which is to be interrupted and
	 *            cancelled
	 * 
	 * @throws CycApiException
	 *             when a Cyc API error occurs
	 * @throws IOException
	 *             when a communications error occurs
	 */
	public void resetTaskProcessorById(int id) throws CycApiException,
			IOException {
		CycList command = new CycList();
		command.add(CycObjectFactory
				.makeCycSymbol("reset-api-task-processor-by-id"));
		command.add(myClientName);
		command.add(new Integer(id));
		cycAccess.converseCycObject(command);
	}

	/**
	 * Class TaskProcessorBinaryResponseHandler handles responses from
	 * task-processor requests in binary communication mode.
	 */
	protected class TaskProcessorBinaryResponseHandler extends Thread {

		/**
		 * specifies a free port where the java api listens for responses from
		 * the Cyc server
		 */
		public int localClientListenerPort;

		/** Maximum number of local cyc clients supported by this listener. */
		public static final int MAX_LOCAL_CLIENT_CLIENTS = 50;

		/** The socket which listens for new connections. */
		protected ServerSocket listenerSocket = null;

		/**
		 * The socket which receives asychronous inbound messages from the Cyc
		 * server.
		 */
		protected Socket inboundSocket = null;

		/**
		 * The binary interface input stream which receives asychronous messages
		 * from the Cyc server
		 */
		public CfaslInputStream inboundStream;

		/**
		 * The binary interface output stream, which is the output side of the
		 * bidirectional socket, is used only to start up and close down the
		 * socket.
		 */
		protected CfaslOutputStream inboundOutputStream;

		/**
		 * Reference to the parent thread which will sleep until this handler is
		 * initialized.
		 */
		protected Thread parentThread;

		/**
		 * The (ignore) message from the Cyc server to test if the connection is
		 * alive.
		 */
		protected CycList ignoreMessage;

		/** the parent CycConnection */
		protected CycConnection cycConnection;

		/** the synchronization object to ensure that the streams are ready */
		private Object lockObject;

		private boolean initialized;

		/**
		 * Constructs a TaskProcessorBinaryResponseHandler object.
		 * 
		 * @param parentThread
		 *            the parent thread of this thread
		 * @param cycConnection
		 *            the parent CycConnection
		 * @param localClientListenerPort
		 *            pecifies a free port where the java api listens for
		 *            responses from the Cyc server
		 */
		public TaskProcessorBinaryResponseHandler(Thread parentThread,
				CycConnection cycConnection, int localClientListenerPort) {
			this.parentThread = parentThread;
			this.cycConnection = cycConnection;
			this.localClientListenerPort = localClientListenerPort;
			ignoreMessage = new CycList();
			ignoreMessage.add(new CycSymbol("IGNORE"));
		}

		public void start() {
			initializeSynchronization();
			super.start();
			waitOnSetupToComplete();
		}

		/**
		 * Opens the response socket with Cyc, blocks until the next
		 * task-processor response is available, then awakens the client thread
		 * that made the request.
		 */
		public void run() {
			Thread.currentThread()
					.setName("TaskProcessorBinaryResponseHandler");
			try {
				if (cycConnection.passiveConnection) {
					// Open a second api socket connection and use it for
					// asychronous api responses.
					inboundSocket = new Socket(hostName, cfaslPort);
					int val = inboundSocket.getReceiveBufferSize();
					inboundSocket.setReceiveBufferSize(val * 2);
					inboundSocket.setTcpNoDelay(true);
					inboundStream = new CfaslInputStream(inboundSocket
							.getInputStream());
					inboundStream.trace = trace;
					inboundOutputStream = new CfaslOutputStream(inboundSocket
							.getOutputStream());
					// Send request to Cyc server to start sending back api
					// responses on this socket connection.
					CycList request = new CycList();
					request.add(new CycSymbol(
							"INITIALIZE-JAVA-API-PASSIVE-SOCKET"));
					request.add(cycConnection.uuid.toString());
					inboundOutputStream.writeObject(request);
					inboundOutputStream.flush();
					// ignore the first response
					inboundStream.readObject();
					inboundStream.readObject();
				} else {
					// Create a listener socket awaiting the Cyc connection.
					listenerSocket = new ServerSocket(localClientListenerPort,
							MAX_LOCAL_CLIENT_CLIENTS);
					localClientListenerPort = listenerSocket.getLocalPort();
					parentThread.interrupt();
					inboundSocket = listenerSocket.accept();
					inboundStream = new CfaslInputStream(inboundSocket
							.getInputStream());
					inboundStream.trace = trace;
					listenerSocket.close();
				}
			} catch (IOException e) {
				Log.current.printStackTrace(e);
				System.exit(1);
			}
			// this is probably to signal that we are ready to go
			notifySetupCompleted();

			// Handle messsages received on the asychronous inbound Cyc
			// connection.
			while (true) {
				Object status = null;
				CycList taskProcessorResponse = null;

				try {
					status = inboundStream.readObject();
					taskProcessorResponse = (CycList) inboundStream
							.readObject();

					// Log.current.println("taskProcessorResponse: " +
					// taskProcessorResponse.safeToString());
				} catch (Exception e) {
					if (taskProcessingEnded) {
						if (trace > API_TRACE_NONE) {
							Log.current
									.println("Ending binary mode task processor handler.");
						}
					}

					if (e instanceof CfaslInputStreamClosedException) {
						if (trace > API_TRACE_NONE) {
							Log.current.errorPrintln(e.getMessage());
							// Log.current.printStackTrace(e);
						}
					} else if (e instanceof RuntimeException) {
						Log.current.errorPrintln(e.getMessage());
						Log.current.printStackTrace(e);
						continue;
					} else if (trace > API_TRACE_NONE) {
						Log.current
								.println("Cyc Server ended binary mode task processor handler.");
					}

					taskProcessingThreadDead = true;

					return;
				}

				if (trace > API_TRACE_NONE) {
					String responseString = null;
					Log.current.println("cyc --> (" + status + ") "
							+ taskProcessorResponse.safeToString());
				}

				if (taskProcessorResponse.equals(ignoreMessage)) {
					continue;
				}

				try {
					// @note and please explain, why are these not constants?
					Integer id = (Integer) taskProcessorResponse.get(2);
					Object response = taskProcessorResponse.get(5);
					Object taskStatus = taskProcessorResponse.get(6);
					// handle Cyc images that either support or do not support
					// (legacy) the finished flag
					Object finishedFlag = (taskProcessorResponse.size() > 7) ? taskProcessorResponse
							.get(7)
							: CycObjectFactory.t;

					boolean finished = !(finishedFlag == CycObjectFactory.nil);

					WaitingWorkerInfo waitingWorkerInfo = (WaitingWorkerInfo) waitingReplyThreads
							.get(id);
					if (waitingWorkerInfo == null) {
						continue;
					}
					SubLWorker worker = waitingWorkerInfo.getWorker();

					if (taskStatus == CycObjectFactory.nil) {
						// no error occurred, no exceptions
						List referencedConstants = DefaultCycObject
								.getReferencedConstants(response);
						for (Iterator iter = referencedConstants.listIterator(); iter
								.hasNext();) {
							if (((CycConstant) iter.next()).safeGetName() != null) {
								iter.remove();
							}
						}
						if (referencedConstants.size() > 0) {
							new GetConstantNamesAndForwardResultsThread(
									referencedConstants, worker, response,
									finished).start();
						} else {
							worker
									.fireSubLWorkerDataAvailableEvent(new SubLWorkerEvent(
											worker, response, -1.0f));
							if (finished) {
								worker
										.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(
												worker,
												SubLWorkerStatus.FINISHED_STATUS,
												null));
							}
						}
					} else {
						// Error, status contains the error message

						// @ToDo need to diferrentiate between exceptions and
						// cancel messages!!!!!!!!!
						finished = true;
						if (taskStatus instanceof String) {
							worker
									.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(
											worker,
											SubLWorkerStatus.EXCEPTION_STATUS,
											new CycApiServerSideException(
													taskStatus.toString())));
						} else if (taskStatus instanceof CycSymbol) {
							worker
									.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(
											worker,
											SubLWorkerStatus.CANCELED_STATUS,
											null));
						}
					}

					if (worker.isDone()) {
						waitingReplyThreads.remove(id);
					}
				} catch (Exception xcpt) {
					Log.current.errorPrintln(xcpt.getMessage());
					Log.current.printStackTrace(xcpt);
					continue;
				}
			} // while-forever
		}

		/**
		 * Closes the passive inbound api response socket by sending an
		 * (api-quit) through it to Cyc.
		 */
		public void close() throws IOException {
			CycList request = new CycList();
			request.add(new CycSymbol("API-QUIT"));
			// request.add(cycConnection.uuid.toString());
			inboundOutputStream.writeObject(request);
			inboundOutputStream.flush();
		}

		private void waitOnSetupToComplete() {
			// avoid blocking on this ptr, which would stop the
			// notifySetupCompleted method from working correctly
			synchronized (lockObject) {
				boolean isInitialized = false;
				synchronized (this) {
					isInitialized = this.initialized;
				}
				while (!isInitialized) {
					try {
						lockObject.wait();
					} catch (InterruptedException xcpt) {
						System.err
								.println("Interrupted during wait(): " + xcpt);
					}
					synchronized (this) {
						isInitialized = this.initialized;
					}
				}
			}
		}

		private void initializeSynchronization() {
			synchronized (this) {
				initialized = false;
				lockObject = new String("Lock object");
			}
		}

		private void notifySetupCompleted() {
			synchronized (this) {
				initialized = true;
			}
			synchronized (lockObject) {
				lockObject.notify();
			}
		}
	}

	private static class GetConstantNamesAndForwardResultsThread extends Thread {

		public GetConstantNamesAndForwardResultsThread(List partialConstants,
				SubLWorker worker, Object response, boolean isFinished) {
			this.partialConstants = partialConstants;
			this.worker = worker;
			this.response = response;
			this.isFinished = isFinished;
		}

		public void run() {
			try {
				worker.getCycServer().obtainConstantNames(partialConstants);
				worker.fireSubLWorkerDataAvailableEvent(new SubLWorkerEvent(
						worker, response, -1.0f));
				if (isFinished) {
					worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(
							worker, SubLWorkerStatus.FINISHED_STATUS, null));
				}
			} catch (Exception xcpt) {
				Log.current.printStackTrace(xcpt);
			}
		}

		private List partialConstants;

		private SubLWorker worker;

		private Object response;

		private boolean isFinished;
	}

	/**
	 * Provides a timer thread for cancelling the connection if it takes too
	 * long to establish.
	 */
	private class ConnectionTimer extends Thread {

		/** Constucts a new ConnectionTimer instance. */
		ConnectionTimer() {
		}

		/**
		 * Waits for either the CycConnection constructor thread to set the done
		 * indicator, or kills the connection after the timeout is exceeded.
		 */
		public void run() {
			try {
				while (!isCycConnectionEstablished) {
					Thread.sleep(WAIT_TIME_INCREMENT);
					timerMillis = timerMillis + WAIT_TIME_INCREMENT;
					if (timerMillis > TIMEOUT_MILLIS)
						throw new InterruptedException();
				}
			} catch (InterruptedException e) {
				Log.current
						.println("Timeout while awaiting Cyc connection establishment, closing sockets");
				// close the socket connections to Cyc and kill any awaiting api
				// request threads
				if (trace == CycConnection.API_TRACE_NONE)
					trace = CycConnection.API_TRACE_MESSAGES;
				close();
			}
		}

		/** the timeout duration in milliseconds (one minute) */
		final long TIMEOUT_MILLIS = 60000;

		/** the wait time increment */
		final long WAIT_TIME_INCREMENT = 1000;

		/** the wait time so far in milliseconds */
		long timerMillis = 0;

		/**
		 * set by the CycConnection constructor process to indicate that the
		 * connection to Cyc is established
		 */
		boolean isCycConnectionEstablished = false;

	}

	public void converseBinary(SubLWorker worker) throws IOException,
			TimeOutException, CycApiException {
		converseBinary(worker.getSubLCommand(),new Timer(300));
	}

}
