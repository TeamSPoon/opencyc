package org.opencyc.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.util.Log;
import org.opencyc.util.StringUtils;
import org.opencyc.util.TimeOutException;
import org.opencyc.util.Timer;
import org.opencyc.util.UUID;

/**
 * Provides a Cyc API connection via the XML SOAP protocol.  
 *
 * @author  reed
 */
public class SOAPCycConnection implements CycConnectionInterface {
    
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
     * The timer which optionally monitors the duration of requests to the OpenCyc server.
     */
    protected static final Timer notimeout = new Timer();

    /**
     * Indicates if the response from the OpenCyc server is a symbolic expression (enclosed in
     * parentheses).
     */
    protected boolean isSymbolicExpression = false;

    /**
     * The ascii interface input stream.
     */
    protected BufferedReader in;
    
    /**
     * A reference to the parent CycAccess object for dereferencing constants in ascii symbolic expressions.
     */
    protected CycAccess cycAccess;

    /**
     * An indicator for ascii communications mode that strings should retain their quote delimiters.
     */
    protected boolean quotedStrings;
    
    /**
     * the SOAP XML endpoint URL which indicates the Cyc API web services host
     */
    protected URL endpointURL;
    
    /** 
     * Creates a new instance of SOAPCycConnection 
     */
    public SOAPCycConnection() {
        try {
            endpointURL = new URL("http://localhost:8080/axis/CycSOAPService.jws");
            // for use with tcp monitor
            //endpointURL = new URL("http://localhost:9080/axis/CycSOAPService.jws");
        }
        catch (MalformedURLException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }
    
    /** 
     * Creates a new instance of SOAPCycConnection using the given endpoint URL and the given
     * CycAccess API method provider.
     *
     * @param endpointURL the SOAP XML endpoint URL which indicates the Cyc API web services host
     * @param cycAccess the parent CycAccess object
     */
    public SOAPCycConnection (URL endpointURL, CycAccess cycAccess) {
        this.endpointURL = endpointURL;
        this.cycAccess = cycAccess;
    }
    
    /**
     * Provides the main method for the CycSOAPClient application.
     *
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        Log.makeLog("Cyc-SOAP-client.log");
        try {
            SOAPCycConnection soapCycConnection = new SOAPCycConnection();
            String subLMessage = "(isa #$TransportationDevice)";
            String result = soapCycConnection.remoteSubLInteractor(subLMessage);
            Log.current.println("subLMessage=" + subLMessage + " result=" + result);
            subLMessage = "(constant-name #$BaseKB)";
            result = soapCycConnection.remoteSubLInteractor(subLMessage);
            Log.current.println("subLMessage=" + subLMessage + " result=" + result);
            subLMessage = "(+ 1 2)";
            result = soapCycConnection.remoteSubLInteractor(subLMessage);
            Log.current.println("subLMessage=" + subLMessage + " result=" + result);
            subLMessage = "(rest '(a))";
            result = soapCycConnection.remoteSubLInteractor(subLMessage);
            Log.current.println("subLMessage=" + subLMessage + " result=" + result);
            CycAccess cycAccess =
                new CycAccess(new URL("http://localhost:8080/axis/CycSOAPService.jws"),CycConnection.DEFAULT_HOSTNAME,CycConnection.DEFAULT_BASE_PORT);
            Log.current.println("CycAccess created");
            soapCycConnection = (SOAPCycConnection) cycAccess.getCycConnection();
            Log.current.println("Trying helloWorld");
            soapCycConnection.helloWorld();
            for (int i = 0; i < 10; i++) {
                result = soapCycConnection.remoteSubLInteractor("(isa #$TransportationDevice)");
                Log.current.println(i + " " + result);
            }
            result = soapCycConnection.remoteSubLInteractor("(an-error)");
            Log.current.println("error=" + result); 
            Object[] response = soapCycConnection.converse("(isa #$TransportationDevice)");
            Log.current.println("response[0]=" + response[0].toString());
            Log.current.println("response[1]=" + response[1].toString());
        }
        catch( Exception e ) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }
    
    /**
     * Provides a remote SubL Interactor.
     *
     * @param subLMessage the SubL request message
     */
    protected String remoteSubLInteractor (String subLMessage)
        throws ServiceException, MalformedURLException, RemoteException {
        String methodName = "subLInteractor";
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(endpointURL);
        call.setOperationName(methodName);
        call.addParameter("name",
                          XMLType.XSD_STRING,
                          ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);
        return (String) call.invoke(new Object[] {subLMessage});
    }
    
    /**
     * Provides a simple test of the SOAP service without Cyc access.
     */
    protected void helloWorld ()
        throws ServiceException, MalformedURLException, RemoteException {
        String methodName = "getHelloWorldMessage";
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(endpointURL);
        call.setOperationName(methodName);
        call.addParameter("name",
                          XMLType.XSD_STRING,
                          ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);
        String result = (String) call.invoke(new Object[] {"AXIS"});
        Log.current.println(result);
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
    public Object[] converse(Object message) throws IOException, CycApiException {
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
    public Object[] converse (Object message, Timer timeout)
        throws IOException, TimeOutException, CycApiException {
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
                if (trace > API_TRACE_NONE)
                    Log.current.println("response[1]=" + ((CycList) response[1]).cyclify());
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
            Log.current.println(message + " --> cyc");
        String result = null;
        try {
            result = remoteSubLInteractor(message) + "\n";
            //System.out.println("result from SOAP service=" + result);
        }
        catch (ServiceException e) {
            throw new CycApiException("Wrapped javax.xml.rpc.ServiceException " + e.getMessage());
        }
        in = new BufferedReader(new StringReader(result));
        if (trace > API_TRACE_NONE)
            Log.current.print("cyc --> ");
        Object[] answer = readAsciiCycResponse(timeout);
        if (trace > API_TRACE_NONE)
            Log.current.println();
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
        // peek at the next character
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
            Log.current.print((char)ch);
        if (((String) answer[1]).startsWith("(CycApiException "))
            answer[0] = Boolean.FALSE;
        else
            answer[0] = Boolean.TRUE;
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
            Log.current.print((char)ch);
        parenLevel++;
        result.append((char)ch);
        while (parenLevel != 0) {
            ch = in.read();
            if (trace > API_TRACE_NONE)
                Log.current.print((char)ch);
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
            Log.current.print((char)ch);
        boolean escapedChar = false;
        while (true) {
            ch = in.read();
            if (trace > API_TRACE_NONE)
                Log.current.print((char)ch);
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
                Log.current.print((char)ch);
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
     * Close the api sockets and streams.
     */
    public void close () {
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
    }

    /**
     * Turns on the detailed diagnostic trace of socket messages.
     */
    public void traceOnDetailed() {
        trace = API_TRACE_DETAILED;
    }

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff() {
        trace = API_TRACE_NONE;
    }

    /**
     * Returns connection information, suitable for diagnostics.
     *
     * @return connection information, suitable for diagnostics
     */
    public String connectionInfo () {
        return "Cyc API Web Service at " + endpointURL.toString();
    }

    /** Returns the UUID that identifies this java api client connection.
     *
     * @return the UUID that identifies this java api client connection
     *
     */
    public UUID getUuid() {
        return null;
    }

	public void abortCommunication(SubLWorker worker) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void cancelCommunication(SubLWorker worker) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public Object[] converseBinary(CycList message, Timer timeout) throws IOException, TimeOutException, CycApiException {
		// TODO Auto-generated method stub
		return null;
	}

	public void converseBinary(SubLWorker worker) throws IOException, TimeOutException, CycApiException {
		// TODO Auto-generated method stub
		
	}

	public int getBasePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getConnectionType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHostName() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
