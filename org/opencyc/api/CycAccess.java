package org.opencyc.api;

import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.oro.util.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.cycagent.*;

/**
 * Provides wrappers for the OpenCyc API.<p>
 *
 * Collaborates with the <tt>CycConnection</tt> class which manages the api connections.
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
public class CycAccess {

    /**
     * Dictionary of CycAccess instances, indexed by thread so that the application does not
     * have to keep passing around a CycAccess object reference.
     */
    public static HashMap cycAccessInstances = new HashMap();

    /**
     * Shared CycAccess instance when thread synchronization is entirely handled by the application. Use of
     * the CycAccess.current() method returns this reference if the lookup by process thread fails.
     */
    public static CycAccess sharedCycAccessInstance = null;

    /**
     * Value indicating that the OpenCyc api socket is created and then closed for each api call.
     */
    public static final boolean TRANSIENT_CONNECTION = false;

    /**
     * Value indicating that the OpenCyc api should use one TCP socket for the entire session.
     */
    public static final boolean PERSISTENT_CONNECTION = true;

    /**
     * Default value indicating that the OpenCyc api should use one TCP socket for the entire session.
     */
    public static final boolean DEFAULT_CONNECTION = PERSISTENT_CONNECTION;

    /**
     * Parameter indicating whether the OpenCyc binary api defers the completion of CycConstant attributes
     * until used for the first time.
     */
    public boolean deferObjectCompletion = true;

    /**
     * Parameter indicating whether the OpenCyc api should use one TCP socket for the entire
     * session, or if the socket is created and then closed for each api call.
     */
    public boolean persistentConnection;

    /**
     * Parameter indicating the serial or concurrent messaging mode to the OpenCyc server.
     */
    public int messagingMode = CycConnection.DEFAULT_MESSAGING_MODE;

    protected String hostName;
    protected int port;
    protected int communicationMode;
    protected static final Integer OK_RESPONSE_CODE = new Integer(200);

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server. This
     * variable preserves the value of the CycConnection trace between instantiations when the
     * connection is transient.
     */
    protected int saveTrace = CycConnection.API_TRACE_NONE;

    /**
     * Convenient reference to #$BaseKb.
     */
    public static CycConstant baseKB = null;

    /**
     * Convenient reference to #$isa.
     */
    public static CycConstant isa = null;

    /**
     * Convenient reference to #$genls.
     */
    public static CycConstant genls = null;

    /**
     * Convenient reference to #$genlMt.
     */
    public static CycConstant genlMt = null;

    /**
     * Convenient reference to #$comment.
     */
    public static CycConstant comment = null;

    /**
     * Convenient reference to #$Collection.
     */
    public static CycConstant collection = null;

    /**
     * Convenient reference to #$binaryPredicate.
     */
    public static CycConstant binaryPredicate = null;

    /**
     * Convenient reference to #$elementOf.
     */
    public static CycConstant elementOf = null;

    /**
     * Convenient reference to #$and.
     */
    public static CycConstant and = null;

    /**
     * Convenient reference to #$or.
     */
    public static CycConstant or = null;

    /**
     * Convenient reference to #$not.
     */
    public static CycConstant not = null;

    /**
     * Convenient reference to #$numericallyEqual.
     */
    public static CycConstant numericallyEqual = null;

    /**
     * Convenient reference to #$PlusFn.
     */
    public static CycConstant plusFn = null;

    /**
     * Convenient reference to #$different.
     */
    public static CycConstant different = null;

    /**
     * Convenient reference to #$Thing.
     */
    public static CycConstant thing = null;

    /**
     * Convenient reference to #$InferencePSC.
     */
    public static CycConstant inferencePSC = null;

    /**
     * Convenient reference to #$UniversalVocabularyMt.
     */
    public static CycConstant universalVocabularyMt = null;

    /**
     * Convenient reference to #$bookkeepingMt.
     */
    public static CycConstant bookkeepingMt = null;

    private CycConstant cyclist = null;
    private CycConstant project = null;

    /**
     * Least Recently Used Cache of ask results.
     */
    protected Cache askCache = new CacheLRU(500);

    /**
     * Least Recently Used Cache of countAllInstances results.
     */
    protected Cache countAllInstancesCache = new CacheLRU(500);

    /**
     * Least Recently Used Cache of isCollection results.
     */
    protected Cache isCollectionCache = new CacheLRU(500);

    /**
     * Least Recently Used Cache of isGenlOf results.
     */
    protected Cache isGenlOfCache = new CacheLRU(500);

    /**
     * Reference to <tt>CycConnection</tt> object which manages the api connection to the OpenCyc server.
     */
    protected CycConnectionInterface cycConnection;

    /**
     * Constructs a new CycAccess object.
     *
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAccess() throws IOException, UnknownHostException, CycApiException {
        this(CycConnection.DEFAULT_HOSTNAME,
             CycConnection.DEFAULT_BASE_PORT,
             CycConnection.DEFAULT_COMMUNICATION_MODE,
             CycAccess.DEFAULT_CONNECTION);
    }

    /**
     * Constructs a new CycAccess object to the given CycProxyAgent in the given
     * agent community.
     *
     * @param myAgentName the name of the local agent
     * @param cycProxyAgentName the name of the cyc proxy agent
     * @param agentCommunity the agent community to which the cyc proxy agent belongs
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAccess (String myAgentName,
                      String cycProxyAgentName,
                      int agentCommunity) throws IOException, CycApiException  {
        communicationMode = CycConnection.BINARY_MODE;
        persistentConnection = PERSISTENT_CONNECTION;
        cycConnection = new RemoteCycConnection(myAgentName, cycProxyAgentName, agentCommunity);
        commonInitialization();
    }

    /**
     * Constructs a new CycAccess object given a host name, port, communication mode and persistence indicator.
     *
     * @param hostName the host name
     * @param basePort the base (HTML serving) TCP socket port number
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAccess(String hostName,
                     int basePort,
                     int communicationMode,
                     boolean persistentConnection)
        throws IOException, UnknownHostException, CycApiException {
        this.hostName = hostName;
        this.port = basePort;
        this.communicationMode = communicationMode;
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection(hostName, port, communicationMode, this);
        commonInitialization();
    }

    /**
     * Constructs a new CycAccess object given a host name, port, communication mode,
     * persistence indicator, and messaging mode
     *
     * @param hostName the host name
     * @param basePort the base (HTML serving) TCP socket port number
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     * @param messagingMode either SERIAL_MESSAGING_MODE or CONCURRENT_MESSAGING_MODE
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAccess(String hostName,
                     int basePort,
                     int communicationMode,
                     boolean persistentConnection,
                     int messagingMode)
        throws IOException, UnknownHostException, CycApiException {
        this.hostName = hostName;
        this.port = basePort;
        if (messagingMode == CycConnection.CONCURRENT_MESSAGING_MODE)
            if (persistentConnection != PERSISTENT_CONNECTION)
                throw new CycApiException("Concurrent Messaging requires Persistent Connections");
        this.communicationMode = communicationMode;
        this.persistentConnection = persistentConnection;
        this.messagingMode = messagingMode;
        if (persistentConnection)
            cycConnection = new CycConnection(hostName,
                                              port,
                                              communicationMode,
                                              messagingMode,
                                              this);
        commonInitialization();
    }

    /**
     * Provides common local and remote CycAccess object initialization.
     *
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    protected  void commonInitialization() throws IOException, CycApiException {
        cycAccessInstances.put(Thread.currentThread(), this);
        if (sharedCycAccessInstance == null)
            sharedCycAccessInstance = this;
        initializeConstants();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return cycConnection.connectionInfo();
    }

    /**
     * Returns the <tt>CycAccess</tt> object for this thread.
     *
     * @return the <tt>CycAccess</tt> object for this thread
     */
    public static CycAccess current() {
        CycAccess cycAccess = (CycAccess) cycAccessInstances.get(Thread.currentThread());
        if (cycAccess == null) {
            if (sharedCycAccessInstance != null)
                return sharedCycAccessInstance;
            else
                throw new RuntimeException("No CycAccess object for this thread");
            }
        return cycAccess;
    }

    /**
     * Sets the shared <tt>CycAccess</tt> instance.
     *
     * @param the shared <tt>CycAccess</tt> instance
     */
    public static void setSharedCycAccessInstance (CycAccess sharedCycAccessInstance) {
        CycAccess.sharedCycAccessInstance = sharedCycAccessInstance;
    }

    /**
     * Turns on the diagnostic trace of socket messages.
     */
    public void traceOn() {
        cycConnection.traceOn();
        saveTrace = CycConnection.API_TRACE_MESSAGES;
    }

    /**
     * Turns on the detailed diagnostic trace of socket messages.
     */
    public void traceOnDetailed() {
        if (cycConnection != null)
            cycConnection.traceOnDetailed();
        saveTrace = CycConnection.API_TRACE_DETAILED;
    }

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff() {
        cycConnection.traceOff();
        saveTrace = CycConnection.API_TRACE_NONE;
    }

    /**
     * Returns the CycConnection object.
     *
     * @return the CycConnection object
     */
    public CycConnectionInterface getCycConnection() {
        return cycConnection;
    }

    /**
     * Closes the CycConnection object.
     */
    public void close() {
        if (cycConnection != null) {
            if (cycConnection instanceof RemoteCycConnection)
                try {
                    this.converseVoid(CycObjectFactory.END_CYC_CONNECTION);
                }
                catch (UnknownHostException e) {
                }
                catch (IOException e) {
                }
                catch (CycApiException e) {
                }
            cycConnection.close();
        }
        cycAccessInstances.remove(Thread.currentThread());
    }

    /**
     * Returns the communication mode.
     *
     * @return the communication mode
     */
    public int getCommunicationMode() {
        return communicationMode;
    }

    /**
     * Converses with Cyc to perform an API command.  Creates a new connection for this command
     * if the connection is not persistent.
     *
     * @param command the command string or CycList
     * @return the result as an object array of two objects
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    protected Object [] converse(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        if (! persistentConnection) {
            cycConnection = new CycConnection(hostName,
                                              port,
                                              communicationMode,
                                              messagingMode,
                                              this);
            cycConnection.setTrace(saveTrace);
        }
        response = cycConnection.converse(command);
        if (! persistentConnection) {
            saveTrace = cycConnection.getTrace();
            cycConnection.close();
        }
        return response;
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as an object.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Object converseObject(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE))
            return response[1];
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a list.  The symbol
     * nil is returned as the empty list.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList converseList(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE))
            if (response[1].equals(CycObjectFactory.nil))
                return new CycList();
            else
                return (CycList) response[1];
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a String.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String converseString(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (! (response[1] instanceof String))
                throw new RuntimeException("Expected String but received (" + response[1].getClass() + ") " +
                                           response[1] + "\n in response to command " + command);
            return (String) response[1];
        }
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a boolean.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean converseBoolean(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (response[1].toString().equals("T"))
                return true;
            else
                return false;
        }
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as an int.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int converseInt(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            return (new Integer(response[1].toString())).intValue();
        }
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is void.
     *
     * @param command the command string or CycList
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void converseVoid(Object command)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.FALSE))
            throw new CycApiException(response[1].toString());
    }

    /**
     * Sets the *print-readable-narts* feature on.
     *
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void setReadableNarts ()
        throws IOException, UnknownHostException, CycApiException {
        converseVoid("(csetq *print-readable-narts t)");
    }

    /**
     * Initializes common cyc constants.
     *
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    private void initializeConstants()
        throws IOException, UnknownHostException, CycApiException {
        if (baseKB == null)
            baseKB = getKnownConstantByGuid("bd588111-9c29-11b1-9dad-c379636f7270");
        if (isa == null)
            isa = getKnownConstantByGuid("bd588104-9c29-11b1-9dad-c379636f7270");
        if (genls == null)
            genls = getKnownConstantByGuid("bd58810e-9c29-11b1-9dad-c379636f7270");
        if (genlMt == null)
            genlMt = getKnownConstantByGuid("bd5880e5-9c29-11b1-9dad-c379636f7270");
        if (comment == null)
            comment = getKnownConstantByGuid("bd588109-9c29-11b1-9dad-c379636f7270");
        if (collection == null)
            collection = getKnownConstantByGuid("bd5880cc-9c29-11b1-9dad-c379636f7270");
        if (binaryPredicate == null)
            binaryPredicate = getKnownConstantByGuid("bd588102-9c29-11b1-9dad-c379636f7270");
        if (elementOf == null)
            elementOf = getKnownConstantByGuid("c0659a2b-9c29-11b1-9dad-c379636f7270");
        if (and == null)
            and = getKnownConstantByGuid("bd5880f9-9c29-11b1-9dad-c379636f7270");
        if (or == null)
            or = getKnownConstantByGuid("bd5880fa-9c29-11b1-9dad-c379636f7270");
        if (not == null)
            not = getKnownConstantByGuid("bd5880fb-9c29-11b1-9dad-c379636f7270");
        if (numericallyEqual == null)
            //numericallyEqual = getKnownConstantByGuid("bd589d90-9c29-11b1-9dad-c379636f7270");
            numericallyEqual = getConstantByGuid(new Guid("bd589d90-9c29-11b1-9dad-c379636f7270"));
        if (plusFn == null)
            plusFn = getKnownConstantByGuid("bd5880ae-9c29-11b1-9dad-c379636f7270");
        if (different == null)
            different = getKnownConstantByGuid("bd63f343-9c29-11b1-9dad-c379636f7270");
        if (thing == null)
            thing = getKnownConstantByGuid("bd5880f4-9c29-11b1-9dad-c379636f7270");
        if (inferencePSC == null)
            inferencePSC = getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        if (universalVocabularyMt == null)
            universalVocabularyMt = getKnownConstantByGuid("dff4a041-4da2-11d6-82c0-0002b34c7c9f");
    }

    /**
     * Gets a known CycConstant by using its constant name.
     *
     * @param constantName the name of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
     */
    public CycConstant getKnownConstantByName (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant cycConstant = getConstantByName(constantName);
        if (cycConstant == null)
            throw new CycApiException("Expected constant not found " + constantName);
        return cycConstant;
    }

    /**
     * Gets a CycConstant by using its constant name.
     *
     * @param constantName the name of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise return null
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant getConstantByName (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        String name = constantName;
        if (constantName.startsWith("#$"))
            name = name.substring(2);
        CycConstant answer = CycObjectFactory.getCycConstantCacheByName(name);
        if (answer != null)
            return answer;
        answer = new CycConstant();
        answer.setName(name);
        Integer id = getConstantId(name);
        if (id == null)
            return null;
        answer.setId(id);
        answer.setGuid(getConstantGuid(name));
        CycObjectFactory.addCycConstantCacheByName(answer);
        CycObjectFactory.addCycConstantCacheById(answer);
        return answer;
    }

    /**
     * Gets the ID for the given CycConstant.
     *
     * @param cycConstant the <tt>CycConstant</tt> object for which the id is sought
     * @return the ID for the given CycConstant, or null if the constant does not exist.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Integer getConstantId (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return getConstantId(cycConstant.getName());
    }

    /**
     * Gets the ID for the given constant name.
     *
     * @param constantName the name of the constant object for which the id is sought
     * @return the ID for the given constant name, or null if the constant does not exist.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Integer getConstantId (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        String command = "(cand (boolean (find-constant \"" + constantName + "\"))\n" +
                         "      (valid-constant (find-constant \"" + constantName + "\")))";
        boolean constantExists = converseBoolean(command);
        if (constantExists) {
            command = "(constant-internal-id (find-constant \"" + constantName + "\"))";
            try {
                return new Integer(converseInt(command));
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("NumberFormatException\n" + e.getMessage() +
                                           "\nConstantName: " + constantName);
            }
        }
        else
            return null;
    }

    /**
     * Gets the Guid for the given CycConstant, raising an exception if the constant does not
     * exist.
     *
     * @param cycConstant the <tt>CycConstant</tt> object for which the id is sought
     * @return the Guid for the given CycConstant
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Guid getConstantGuid (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return getConstantGuid(cycConstant.getName());
    }

    /**
     * Gets the Guid for the given constant name, raising an exception if the constant does not
     * exist.
     *
     * @param constantName the name of the constant object for which the Guid is sought
     * @return the Guid for the given CycConstant
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Guid getConstantGuid (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        String command = "(guid-to-string (constant-external-id (find-constant \"" +
                         constantName + "\")))";
        return CycObjectFactory.makeGuid(converseString(command));
    }

    /**
     * Gets the Guid for the given constant id.
     *
     * @param id the id of the <tt>CycConstant</tt> whose guid is sought
     * @return the Guid for the given CycConstant
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Guid getConstantGuid (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("guid-to-string"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("constant-external-id"));
        CycList command2 = new CycList();
        command1.add(command2);
        command2.add(CycObjectFactory.makeCycSymbol("find-constant-by-internal-id"));
        command2.add(id);
        return CycObjectFactory.makeGuid(converseString(command));
    }

    /**
     * Gets a <tt>CycConstant</tt> by using its ID.
     *
     * @param id the id of the <tt>CycConstant</tt> sought
     * @return the <tt>CycConstant</tt> if found or <tt>null</tt> if not found
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant getConstantById (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("boolean"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("find-constant-by-internal-id"));
        command1.add(id);
        boolean constantExists = converseBoolean(command);
        if (! constantExists)
            return null;
        CycConstant answer = new CycConstant();
        answer.setName(getConstantName(id));
        answer.setId(id);
        answer.setGuid(getConstantGuid(id));
        CycObjectFactory.addCycConstantCacheByName(answer);
        CycObjectFactory.addCycConstantCacheById(answer);
        return answer;
    }

    /**
     * Gets the name for the given constant id.
     *
     * @param id the id of the constant object for which the name is sought
     * @return the name for the given CycConstant
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getConstantName (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("constant-name"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("find-constant-by-internal-id"));
        command1.add(id);
        return converseString(command);
    }

    /**
     * Gets the name for the given variable id.
     *
     * @param id the id of the variable object for which the name is sought
     * @return the name for the given CycVariable
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getVariableName (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("variable-name"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("find-variable-by-id"));
        command1.add(id);
        return converseString(command);
    }

    /**
     * Gets a known CycConstant by using its GUID string.
     *
     * @param guid the globally unique ID string of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant getKnownConstantByGuid (String guidString)
        throws IOException, UnknownHostException, CycApiException {
        Guid guid = CycObjectFactory.makeGuid(guidString);
        return getKnownConstantByGuid(guid);
    }

    /**
     * Gets a known CycConstant by using its GUID.
     *
     * @param guid the globally unique ID of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant getKnownConstantByGuid (Guid guid)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant cycConstant = getConstantByGuid(guid);
        if (cycConstant == null)
            throw new CycApiException("Expected constant not found " + guid);
        return cycConstant;
    }

    /**
     * Gets a CycConstant by using its GUID.
     *
     * @param guid the GUID from which to find the constant
     * @return the complete <tt>CycConstant</tt> if found, otherwise return <tt>null</tt>
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant getConstantByGuid (Guid guid)
        throws IOException, UnknownHostException, CycApiException {
        String command = "(boolean (find-constant-by-external-id (string-to-guid \"" + guid + "\")))";
        boolean constantExists = converseBoolean(command);
        if (! constantExists)
            return null;
        command = "(constant-name (find-constant-by-external-id (string-to-guid \"" + guid + "\")))";
        String constantName = this.converseString(command);
        return getConstantByName(constantName);
    }

    /**
     * Completes the instantiation of objects contained in the given <tt>CycList</tt>. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved if the constant is
     * not cached.
     *
     * @param object the <tt>CycConstant</tt> to be completed, or the <tt>Object</tt> whose
     * embedded constants are to be completed
     * @return the completed object, or a reference to a cached instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Object completeObject (Object object)
        throws IOException, UnknownHostException, CycApiException {
        if (object instanceof CycConstant)
            return completeCycConstant((CycConstant) object);
        else if (object instanceof CycList)
            return completeCycList((CycList) object);
        else if (object instanceof CycNart)
            return completeCycNart((CycNart) object);
        else if (object instanceof CycAssertion)
            return completeCycAssertion((CycAssertion) object);
        else
            return object;
    }

    /**
     * Completes the instantiation of <tt>CycConstant</tt> returned by the binary api. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved
     * if the constant is not cached.
     *
     * @param cycConstant the <tt>CycConstant</tt> whose name and guid are to be completed
     * @return the completed <tt>CycConstant</tt> object, or a reference to the previously cached instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant completeCycConstant (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        cycConstant.setName(getConstantName(cycConstant.getId()));
        CycConstant cachedConstant = CycObjectFactory.getCycConstantCacheByName(cycConstant.getName());
        if (cachedConstant == null) {
            cycConstant.setGuid(getConstantGuid(cycConstant.getId()));
            CycObjectFactory.addCycConstantCacheByName(cycConstant);
            return cycConstant;
        }
        else
            return cachedConstant;
    }

    /**
     * Completes the instantiation of HL <tt>CycVariable</tt> returned by the binary api. The
     * binary api sends only HL variable ids, and the variable name must be retrieved
     * if the variable is not cached.  The variable id is not used when sending variables to
     * the binary api, instead the variable is output as a symbol.  In the case where an EL
     * variable is returned by the binary api, then then variable name is already present.
     *
     * @param cycVariable the <tt>CycVariable</tt> whose name is to be completed
     * @return the completed <tt>CycVariable</tt> object, or a reference to the previously
     * cached instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycVariable completeCycVariable (CycVariable cycVariable)
        throws IOException, UnknownHostException, CycApiException {
        if (cycVariable.name == null)
            cycVariable.name = getVariableName(cycVariable.id);
        CycVariable cachedVariable = CycObjectFactory.getCycVariableCache(cycVariable.name);
        if (cachedVariable == null) {
            CycObjectFactory.addCycVariableCache(cycVariable);
            return cycVariable;
        }
        else
            return cachedVariable;
    }

    /**
     * Completes the instantiation of objects contained in the given <tt>CycList</tt>. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved if the constant is
     * not cached.
     *
     * @param cycList the <tt>CycList</tt> whose constants are to be completed
     * @param the completed <tt>CycList</tt> object
     * @return the given list with completed objects
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList completeCycList (CycList cycList)
        throws IOException, UnknownHostException, CycApiException {
        for (int i = 0; i < cycList.size(); i++) {
            Object element = cycList.get(i);
            if (element instanceof CycList)
                completeCycList((CycList) element);
            else if (element instanceof CycConstant)
                // Replace element with the completed constant, which might be previously cached.
                cycList.set(i, completeCycConstant((CycConstant) element));
            else if (element instanceof CycNart)
                // Replace element with the completed constant, which might be previously cached.
                cycList.set(i, completeCycNart((CycNart) element));
            else if (element instanceof CycVariable)
                // Replace element with the completed variable, which might be previously cached.
                cycList.set(i, completeCycVariable((CycVariable) element));
            else
                completeObject(element);
        }
        return cycList;
    }

    /**
     * Completes the instantiation of a <tt>CycNart</tt> returned by the binary api. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved
     * if the constant is not cached.  Also finds the id of the CycNart if the functor
     * and arguments are instantiated.
     *
     * @param cycNart the <tt>CycNart</tt> whose constants are to be completed
     * @param the completed <tt>CycNart</tt> object
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycNart completeCycNart (CycNart cycNart)
        throws IOException, UnknownHostException, CycApiException {
        Integer id = cycNart.getId();
        if (id == null && cycNart.hasFunctorAndArgs()) {
            id = findNartId(cycNart);
            if (id != null)
                cycNart.setId(id);
        }
        if (id == null)
            throw new CycApiException("CycNart has no id " + cycNart.safeToString());
        return getCycNartById(cycNart.getId());
    }

    /**
     * Completes the instantiation of a <tt>CycAssertion</tt> returned by the binary api. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved
     * if the constant is not cached.
     *
     * @param cycAssertion the <tt>CycAssertion</tt> whose constants are to be completed
     * @param the completed <tt>CycAssertion</tt> object
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAssertion completeCycAssertion (CycAssertion cycAssertion)
        throws IOException, UnknownHostException, CycApiException {
        return getAssertionById(cycAssertion.getId());
    }

    /**
     * Finds the id of a CycNart given its formula.
     *
     * @param cycNart the CycNart object with functor and arguments instantiated
     * @return the id of the nart if found in the KB, otherwise null
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Integer findNartId (CycNart cycNart)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("find-nart"));
        command.addQuoted(cycNart.toCycList());
        Object object = converseObject(command);
        if (object.equals(CycObjectFactory.nil))
            return null;
        CycNart foundCycNart = null;
        if (object instanceof CycNart)
            foundCycNart = (CycNart) object;
        else
            throw new CycApiException("findNart did not return an null or a nart " + object +
                                      " (" + object.getClass() + ")");
        command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("nart-id"));
        command.add(foundCycNart);
        return new Integer(converseInt(command));
    }

    /**
     * Gets a CycNart by using its id.
     *
     * @param id the nart id (local to the KB)
     * @return the CycNart
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycNart getCycNartById (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        CycNart cycNart = CycObjectFactory.getCycNartCache(id);
        if (cycNart != null) {
            return cycNart;
        }
        else {
            cycNart = new CycNart();
            cycNart.setId(id);
        }
        CycObjectFactory.addCycNartCache(cycNart);
        CycList command = new CycList();
        if (communicationMode == CycConnection.BINARY_MODE) {
            command.add(CycObjectFactory.makeCycSymbol("nart-hl-formula"));
            CycList command1 = new CycList();
            command.add(command1);
            command1.add(CycObjectFactory.makeCycSymbol("find-nart-by-id"));
            command1.add(id);
            CycList formula = converseList(command);
            cycNart.setFunctor((CycFort) formula.first());
            cycNart.setArguments((CycList) formula.rest());
        }
        else {
            command.add(CycObjectFactory.makeCycSymbol("nart-el-formula"));
            CycList command1 = new CycList();
            command.add(command1);
            command1.add(CycObjectFactory.makeCycSymbol("find-nart-by-id"));
            command1.add(id);
            CycList formula = converseList(command);
            cycNart.setFunctor((CycFort) formula.first());
            cycNart.setArguments((CycList) formula.rest());
            List arguments = cycNart.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                Object argument = arguments.get(i);
                if (argument instanceof CycList)
                    arguments.set(i, new CycNart((CycList) argument));
            }
        }
        return cycNart;
    }

    /**
     * Gets a CycAssertion by using its id.
     *
     * @param id the assertion id (which is local to the given KB).
     * @return the assertion
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycAssertion getAssertionById (Integer id)
        throws IOException, UnknownHostException, CycApiException {
        CycAssertion cycAssertion = CycObjectFactory.getAssertionCache(id);
        if (cycAssertion != null) {
            if (cycAssertion.getFormula() != null)
                return cycAssertion;
        }
        else
            cycAssertion = new CycAssertion(id);
        CycObjectFactory.addAssertionCache(cycAssertion);
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("assertion-el-formula"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("find-assertion-by-id"));
        command1.add(id);
        cycAssertion.setFormula(converseList(command));
        return cycAssertion;
    }

    /**
     * Gets the CycNart object from a Cons object that lists the names of
     * its functor and its arguments.
     *
     * @param elCons the given list which names the functor and arguments
     * @return a CycNart object from a Cons object that lists the names of
     * its functor and its arguments
     */
    public CycNart getCycNartFromCons(CycList elCons) {
        return new CycNart(elCons);
    }

    /**
     * Returns true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2.
     *
     * @param binaryPredicate the predicate
     * @param arg1 the first argument related by the predicate
     * @param arg2 the second argument related by the predicate
     * @return true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean predicateRelates (CycConstant binaryPredicate,
                                     CycFort arg1,
                                     CycFort arg2)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        String command = "(pred-u-v-holds-in-any-mt " +
            binaryPredicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + ")";
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (response[1] == null)
                return false;
            else if (response[1].toString().equals("T"))
                return true;
            else
                return false;
        }
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Returns true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2.
     *
     * @param binaryPredicate the predicate
     * @param arg1 the first argument related by the predicate
     * @param arg2 the second argument related by the predicate
     * @param mt the relevant mt
     * @return true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean predicateRelates (CycConstant binaryPredicate,
                                     CycFort arg1,
                                     CycFort arg2,
                                     CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        Object [] response = {null, null};
        String command = "(pred-u-v-holds " +
            binaryPredicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + " " +
            mt.stringApiValue() + ")";
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (response[1] == null)
                return false;
            else if (response[1].toString().equals("T"))
                return true;
            else
                return false;
        }
        else
            throw new CycApiException(response[1].toString());
    }

    /**
     * Gets the imprecise plural generated phrase for a CycFort (intended for collections).
     *
     * @param cycFort the term for paraphrasing
     * @return the imprecise plural generated phrase for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getImprecisePluralGeneratedPhrase (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-off (generate-phrase " + cycFort.stringApiValue() + " '(#$plural)))");
    }

    /**
     * Gets the plural generated phrase for a CycFort (intended for collections).
     *
     * @param cycFort the term for paraphrasing
     * @return the plural generated phrase for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getPluralGeneratedPhrase (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + " '(#$plural)))");
    }

    /**
     * Gets the imprecise singular generated phrase for a CycFort (intended for individuals).
     *
     * @param cycFort the term for paraphrasing
     * @return the singular generated phrase for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getImpreciseSingularGeneratedPhrase (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-off (generate-phrase " + cycFort.stringApiValue() + " '(#$singular)))");
    }

    /**
     * Gets the singular generated phrase for a CycFort (intended for individuals).
     *
     * @param cycFort the term for paraphrasing
     * @return the singular generated phrase for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getSingularGeneratedPhrase (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + " '(#$singular)))");
    }

    /**
     * Gets the default generated phrase for a CycFort (intended for predicates).
     *
     * @param cycFort the predicate term for paraphrasing
     * @return the default generated phrase for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getGeneratedPhrase (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the paraphrase for a Cyc assertion.
     *
     * @param assertion the assertion formula
     * @return the paraphrase for a Cyc assertion
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getParaphrase (CycList assertion)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-on (generate-phrase '" + assertion.cyclify() + "))");
    }

    /**
     * Gets the imprecise paraphrase for a Cyc assertion.
     *
     * @param assertion the assertion formula
     * @return the imprecise paraphrase for a Cyc assertion
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getImpreciseParaphrase (CycList assertion)
        throws IOException, UnknownHostException, CycApiException {
        return converseString("(with-precise-paraphrase-off (generate-phrase '" + assertion.cyclify() + "))");
    }

    /**
     * Gets the comment for a CycFort.  Embedded quotes are replaced by spaces.
     *
     * @param cycFort the term for which the comment is sought
     * @return the comment for the given CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getComment (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        String script =
            "(clet ((comment-string \n" +
            "         (with-all-mts (comment " + cycFort.stringApiValue() + ")))) \n" +
            "  (fif comment-string \n" +
            "       (string-substitute \" \" \"\\\"\" comment-string) \n" +
            "       \"\"))";
        return converseString(script);
    }

    /**
     * Gets the comment for a CycFort in the relevant mt.
     * Embedded quotes are replaced by spaces.
     *
     * @param cycFort the term for which the comment is sought
     * @param mt the relevant mt from which the comment is visible
     * @return the comment for the given CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public String getComment (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        String script =
            "(clet ((comment-string \n" +
            "         (comment " + cycFort.stringApiValue() + " " + mt.stringApiValue() + "))) \n" +
            "  (fif comment-string \n" +
            "       (string-substitute \" \" \"\\\"\" comment-string) \n" +
            "       \"\"))";
        return converseString(script);
    }

    /**
     * Gets the list of the isas for the given CycFort.
     *
     * @param cycFort the term for which its isas are sought
     * @return the list of the isas for the given CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getIsas (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (isa " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the isas for the given CycFort.
     *
     * @param cycFort the term for which its isas are sought
     * @param mt the relevant mt
     * @return the list of the isas for the given CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getIsas (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(isa " + cycFort.stringApiValue() +
                              " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the directly asserted true genls for the given CycFort collection.
     *
     * @param cycFort the given term
     * @return the list of the directly asserted true genls for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenls (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (genls " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the directly asserted true genls for the given CycFort collection.
     *
     * @param cycFort the given term
     * @param mt the relevant mt
     * @return the list of the directly asserted true genls for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenls (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(genls " + cycFort.stringApiValue() +
                              " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the minimum (most specific) genls for a CycFort collection.
     *
     * @param cycFort the given collection term
     * @return a list of the minimum (most specific) genls for a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMinGenls (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (min-genls " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the minimum (most specific) genls for a CycFort collection.
     *
     * @param cycFort the collection
     * @param mt the microtheory in which to look
     * @return a list of the minimum (most specific) genls for a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMinGenls (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(min-genls " + cycFort.stringApiValue() +
                              " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the directly asserted true specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the directly asserted true specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSpecs (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the directly asserted true specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the microtheory in which to look
     * @return the list of the directly asserted true specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSpecs (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(specs " + cycFort.stringApiValue() +
                              " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the least specific specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the least specific specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMaxSpecs (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (max-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the least specific specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the microtheory in which to look
     * @return the list of the least specific specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMaxSpecs (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(max-specs " + cycFort.stringApiValue() +
                              " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the direct genls of the direct specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the direct genls of the direct specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenlSiblings (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (genl-siblings " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the direct genls of the direct specs for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the microtheory in which to look
     * @return the list of the direct genls of the direct specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenlSiblings (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(genl-siblings " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the siblings (direct specs of the direct genls) for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSiblings (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return getSpecSiblings(cycFort);
    }

    /**
     * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the microtheory in which to look
     * @return the list of the siblings (direct specs of the direct genls) for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSiblings (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return getSpecSiblings(cycFort, mt);
    }

    /**
     * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the siblings (direct specs of the direct genls) for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSpecSiblings (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (spec-siblings " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the microtheory in which to look
     * @return the list of the siblings (direct specs of the direct genls) for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSpecSiblings (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(spec-siblings " + cycFort.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of all of the direct and indirect genls for the given CycFort collection.
     *
     * @param cycFort the collection
     * @return the list of all of the direct and indirect genls for a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenls (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-genls-in-any-mt " + cycFort.stringApiValue() + ")");
    }

    /**
     * Gets the list of all of the direct and indirect genls for a CycFort collection
     * given a relevant microtheory.
     *
     * @param cycFort the collection
     * @param mt the relevant mt
     * @return the list of all of the direct and indirect genls for a CycFort collection
     * given a relevant microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenls (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-genls " + cycFort.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of all of the direct and indirect specs for a CycFort collection.
     *
     * @param cycFort the collection
     * @return the list of all of the direct and indirect specs for the given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecs (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the direct and indirect specs for the given collection
     * in the given microtheory.
     *
     * @param cycFort the collection
     * @return the list of all of the direct and indirect specs for the given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecs (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-specs " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a hashset of all of the direct and indirect specs for a CycFort collection.
     *
     * @param cycFort the collection
     * @return the hashset of all of the direct and indirect specs for the given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecsHashSet (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecs(cycFort));
    }

    /**
     * Gets the hashset of all of the direct and indirect specs for the given collection
     * in the given microtheory.
     *
     * @param cycFort the collection
     * @return the hashset of all of the direct and indirect specs for the given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecsHashSet (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecs(cycFort, mt));
    }

    /**
     * Gets the list of all of the direct and indirect genls for a CycFort SPEC which are also specs of
     * CycFort GENL.
     *
     * @param spec the given collection
     * @param genl the more general collection
     * @return the list of all of the direct and indirect genls for a CycFort SPEC which are also specs of
     * CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenlsWrt (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-genls-wrt " + spec.stringApiValue() + " " + genl.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the direct and indirect genls for a CycFort SPEC which are also specs of
     * CycFort GENL.
     *
     * @param spec the given collection
     * @param genl the more general collection
     * @param mt the relevant mt
     * @return the list of all of the direct and indirect genls for a CycFort SPEC which are also specs of
     * CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenlsWrt (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-genls-wrt " + spec.stringApiValue() + " " + genl.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of all of the dependent specs for a CycFort collection.  Dependent specs are those direct and
     * indirect specs of the collection such that every path connecting the spec to a genl of the collection passes
     * through the collection.  In a typical taxomonmy it is expected that all-dependent-specs gives the same
     * result as all-specs.
     *
     * @param cycFort the given collection
     * @return the list of all of the dependent specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllDependentSpecs (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-dependent-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the dependent specs for a CycFort collection.  Dependent specs are those direct and
     * indirect specs of the collection such that every path connecting the spec to a genl of the collection passes
     * through the collection.  In a typical taxomonmy it is expected that all-dependent-specs gives the same
     * result as all-specs.
     *
     * @param cycFort the given collection
     * @param mt the relevant mt
     * @return the list of all of the dependent specs for the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllDependentSpecs (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-dependent-specs " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list with the specified number of sample specs of the given CycFort collection.
     * Attempts to return leaves that are maximally differet with regard to their all-genls.
     *
     * @param cycFort the given collection
     * @param numberOfSamples the maximum number of sample specs returned
     * @return the list with the specified number of sample specs of the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSampleLeafSpecs (CycFort cycFort, int numberOfSamples)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (sample-leaf-specs " + cycFort.stringApiValue() + " " + numberOfSamples + "))");
    }

    /**
     * Gets the list with the specified number of sample specs of the given CycFort collection.
     * Attempts to return leaves that are maximally differet with regard to their all-genls.
     *
     * @param cycFort the given collection
     * @param numberOfSamples the maximum number of sample specs returned
     * @param mt the relevant mt
     * @return the list with the specified number of sample specs of the given CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getSampleLeafSpecs (CycFort cycFort, int numberOfSamples, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(sample-leaf-specs " + cycFort.stringApiValue() + " " + numberOfSamples +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort SPEC is a spec of CycFort GENL.
     *
     * @param spec the considered spec collection
     * @param genl the considered genl collection
     * @return true if CycFort SPEC is a spec of CycFort GENL, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isSpecOf (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException, CycApiException {
        return isGenlOf(genl, spec);
    }

    /**
     * Returns true if CycFort SPEC is a spec of CycFort GENL.
     *
     * @param spec the considered spec collection
     * @param genl the considered genl collection
     * @param mt the relevant mt
     * @return true if CycFort SPEC is a spec of CycFort GENL, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isSpecOf (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return isGenlOf(genl, spec, mt);
    }

    /**
     * Returns true if CycFort GENL is a genl of CycFort SPEC.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlOf (CycFort genl, CycFort spec)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(genl-in-any-mt? " + spec.stringApiValue() + " " + genl.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort GENL is a genl of CycFort SPEC, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlOf_Cached (CycFort genl, CycFort spec)
        throws IOException,  UnknownHostException, CycApiException {
        boolean answer;
        ArrayList args = new ArrayList();
        args.add(genl);
        args.add(spec);
        Boolean isGenlOf = (Boolean) isGenlOfCache.getElement(args);
        if (isGenlOf != null) {
            answer = isGenlOf.booleanValue();
            return answer;
        }
        answer = isGenlOf(genl, spec);
        isGenlOfCache.addElement(args, new Boolean(answer));
        return answer;
    }

    /**
     * Returns true if CycFort GENL is a genl of CycFort SPEC in MT.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @param mt the microtheory for spec determination
     * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC in MT
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlOf (CycFort genl, CycFort spec, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(genl? " + spec.stringApiValue() +
                               " " + genl.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in MT.
     *
     * @param genlPred the predicate for genl-pred determination
     * @param specPred the predicate for spec-pred determination
     * @param mt the microtheory for subsumption determination
     * @return <tt>true</tt> if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in MT
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlPredOf (CycFort genlPred, CycFort specPred, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(genl-predicate? " + specPred.stringApiValue() +
                               " " + genlPred.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in any MT.
     *
     * @param genlPred the predicate for genl-pred determination
     * @param specPred the predicate for spec-pred determination
     * @return <tt>true</tt> if CycFort GENLPRED is a genl-pred of CycFort SPECPRED
     * in any MT
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlPredOf (CycFort genlPred, CycFort specPred)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (genl-predicate? " + specPred.stringApiValue() +
                               " " + genlPred.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in MT.
     *
     * @param genlPred the predicate for genl-inverse determination
     * @param specPred the predicate for spec-inverse determination
     * @param mt the microtheory for inverse subsumption determination
     * @return <tt>true</tt> if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in MT
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlInverseOf (CycFort genlPred, CycFort specPred, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(genl-inverse? " + specPred.stringApiValue() +
                               " " + genlPred.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in any MT.
     *
     * @param genlPred the predicate for genl-inverse determination
     * @param specPred the predicate for spec-inverse determination
     * @return <tt>true</tt> if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED
     * in any MT
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlInverseOf (CycFort genlPred, CycFort specPred)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (genl-inverse? " + specPred.stringApiValue() +
                               " " + genlPred.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort GENLMT is a genl-mt of CycFort SPECPRED in *mt-mt*
     * (currently #$UniversalVocabularyMt).
     *
     * @param genlMt the microtheory for genl-mt determination
     * @param specMt the microtheory for spec-mt determination
     * @return <tt>true</tt> if CycFort GENLMT is a genl-mt of CycFort SPECPRED in *mt-mt*
     * (currently #$UniversalVocabularyMt)
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isGenlMtOf (CycFort genlMt, CycFort specMt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(genl-mt? " + specMt.stringApiValue() +
                               " " + genlMt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional
     * via mutual genls of each other.
     *
     * @param collection1 the first given collection
     * @param collection2 the second given collection
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional
     * via mutual genls of each other, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areTacitCoextensional (CycFort collection1, CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (tacit-coextensional? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional
     * via mutual genls of each other.
     *
     * @param collection1 the first given collection
     * @param collection2 the second given collection
     * @param mt the relevant mt
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional
     * via mutual genls of each other, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areTacitCoextensional (CycFort collection1, CycFort collection2, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(tacit-coextensional? " + collection1.stringApiValue() +
                               " " + collection2.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areAssertedCoextensional (CycFort collection1, CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant coExtensional = this.getKnownConstantByGuid("bd59083a-9c29-11b1-9dad-c379636f7270");
        if (predicateRelates(coExtensional, collection1, collection2))
            return true;
        else if (predicateRelates(coExtensional, collection2, collection1))
            return true;
        else
            return false;
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areAssertedCoextensional (CycFort collection1,
                                             CycFort collection2,
                                             CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant coExtensional = this.getKnownConstantByGuid("bd59083a-9c29-11b1-9dad-c379636f7270");
        if (predicateRelates(coExtensional, collection1, collection2, mt))
            return true;
        else if (predicateRelates(coExtensional, collection2, collection1, mt))
            return true;
        else
            return false;
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areIntersecting (CycFort collection1, CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (collections-intersect? " + collection1.stringApiValue() + " " +
                               collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areIntersecting (CycFort collection1, CycFort collection2, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(collections-intersect? " + collection1.stringApiValue() + " " +
                               collection2.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy,
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areHierarchical (CycFort collection1, CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (hierarchical-collections? " + collection1.stringApiValue() +
                               " " + collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy,
     * otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areHierarchical (CycFort collection1, CycFort collection2, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(hierarchical-collections? " + collection1.stringApiValue() +
                               collection2.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenl("Dog", "Animal") -->
     * "(((#$genls #$Dog #$CanineAnimal) :TRUE)
     *    (#$genls #$CanineAnimal #$NonPersonAnimal) :TRUE)
     *    (#$genls #$NonPersonAnimal #$Animal) :TRUE))
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @result the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyGenl (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (why-genl? " + spec.stringApiValue() + " " + genl.stringApiValue() + "))");
    }

    /**
     * Gets the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenl("Dog", "Animal") -->
     * "(((#$genls #$Dog #$CanineAnimal) :TRUE)
     *    (#$genls #$CanineAnimal #$NonPersonAnimal) :TRUE)
     *    (#$genls #$NonPersonAnimal #$Animal) :TRUE))
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @param mt the relevant mt
     * @result the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyGenl (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(why-genl? " + spec.stringApiValue() + " " + genl.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenlParaphrase("Dog", "Animal") -->
     * "a dog is a kind of canine"
     * "a canine is a kind of non-human animal"
     * "a non-human animal is a kind of animal"
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @return the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyGenlParaphrase (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException, CycApiException {
        CycList listAnswer =
            converseList("(with-all-mts (why-genl? " +
                         spec.stringApiValue() + " " + genl.stringApiValue() + "))");
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        CycList iter = listAnswer;

        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenlParaphrase("Dog", "Animal") -->
     * "a dog is a kind of canine"
     * "a canine is a kind of non-human animal"
     * "a non-human animal is a kind of animal"
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @param mt the relevant mt
     * @return the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyGenlParaphrase (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList listAnswer =
            converseList("(why-genl? " +
                         spec.stringApiValue() + " " + genl.stringApiValue() +
                         " " + mt.stringApiValue() + ")");
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        CycList iter = listAnswer;

        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect.
     * see getWhyGenl
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyCollectionsIntersect (CycFort collection1,
                                               CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (why-collections-intersect? " +
                            collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Gets the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect.
     * see getWhyGenl
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyCollectionsIntersect (CycFort collection1,
                                               CycFort collection2,
                                               CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(why-collections-intersect? " +
                            collection1.stringApiValue() + " " + collection2.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
     * COLLECTION2 intersect.
     * see getWhyGenlParaphrase
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
     * COLLECTION2 intersect
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyCollectionsIntersectParaphrase (CycFort collection1,
                                                           CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        CycList listAnswer = converseList("(with-all-mts (why-collections-intersect? " +
                                          collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        CycList iter = listAnswer;

        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            //System.out.println("assertion: " + assertion);
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
     * COLLECTION2 intersect.
     * see getWhyGenlParaphrase
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
     * COLLECTION2 intersect
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyCollectionsIntersectParaphrase (CycFort collection1,
                                                           CycFort collection2,
                                                           CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList listAnswer = converseList("(with-all-mts (why-collections-intersect? " +
                                          collection1.stringApiValue() + " " +
                                          collection2.stringApiValue() +
                                          " " + mt.stringApiValue() + ")");
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        CycList iter = listAnswer;

        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            //System.out.println("assertion: " + assertion);
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the list of the collection leaves (most specific of the all-specs) for a CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the collection leaves (most specific of the all-specs) for a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getCollectionLeaves (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (collection-leaves " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the list of the collection leaves (most specific of the all-specs) for a CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the relevant mt
     * @return the list of the collection leaves (most specific of the all-specs) for a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getCollectionLeaves (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(collection-leaves " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the collections asserted to be disjoint with a CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the collections asserted to be disjoint with a CycFort collection
     */
    public CycList getLocalDisjointWith (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (local-disjoint-with " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the list of the collections asserted to be disjoint with a CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the relevant mt
     * @return the list of the collections asserted to be disjoint with a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getLocalDisjointWith (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(local-disjoint-with " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areDisjoint (CycFort collection1, CycFort collection2)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (disjoint-with? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint.
     *
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @param mt the relevant mt
     * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean areDisjoint (CycFort collection1, CycFort collection2, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(with-all-mts (disjoint-with? " + collection1.stringApiValue() +
                               " " + collection2.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the most specific collections (having no subsets) which contain a CycFort term.
     *
     * @param cycFort the given term
     * @return the list of the most specific collections (having no subsets) which contain a CycFort term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMinIsas (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (min-isa " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the list of the most specific collections (having no subsets) which contain a CycFort term.
     *
     * @param cycFort the given term
     * @param mt the relevant mt
     * @return the list of the most specific collections (having no subsets) which contain a CycFort term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getMinIsas (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(min-isa " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the instances (who are individuals) of a CycFort collection.
     *
     * @param cycFort the given collection
     * @return the list of the instances (who are individuals) of a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getInstances (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (instances " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the list of the instances (who are individuals) of a CycFort collection.
     *
     * @param cycFort the given collection
     * @param mt the relevant mt
     * @return the list of the instances (who are individuals) of a CycFort collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getInstances (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(instances " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the instance siblings of a CycFort, for all collections of which
     * it is an instance.
     *
     * @param cycFort the given term
     * @return the list of the instance siblings of a CycFort, for all collections of which
     * it is an instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getInstanceSiblings (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (instance-siblings " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the list of the instance siblings of a CycFort, for all collections of which
     * it is an instance.
     *
     * @param cycFort the given term
     * @param mt the relevant mt
     * @return the list of the instance siblings of a CycFort, for all collections of which
     * it is an instance
     */
    public CycList getInstanceSiblings (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(instance-siblings " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the collections of which the CycFort is directly and indirectly an instance.
     *
     * @param cycFort the given term
     * @return the list of the collections of which the CycFort is directly and indirectly an instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllIsa (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-isa-in-any-mt " + cycFort.stringApiValue() + ")");
    }

    /**
     * Gets the list of the collections of which the CycFort is directly and indirectly an instance.
     *
     * @param cycFort the given term
     * @param mt the relevant mt
     * @return the list of the collections of which the CycFort is directly and indirectly an instance
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllIsa (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-isa " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of all the direct and indirect instances (individuals) for a CycFort
     * collection.
     *
     * @param cycFort the collection for which all the direct and indirect instances
     * (individuals) are sought
     * @return the list of all the direct and indirect instances (individuals) for the
     * given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllInstances (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-instances-in-all-mts " + cycFort.stringApiValue() + ")");
    }

    /**
     * Gets a list of all the direct and indirect instances (individuals) for a CycFort
     * collection in the given microtheory.
     *
     * @param cycFort the collection for which all the direct and indirect instances
     * (individuals) are sought
     * @param mt the relevant mt
     * @return the list of all the direct and indirect instances (individuals) for the
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     * given collection
    */
    public CycList getAllInstances (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-instances " + cycFort.stringApiValue()
                            + " " + mt.stringApiValue()+ ")");
    }

    /**
     * Gets a hashset of all the direct and indirect instances (individuals) for a CycFort
     * collection in the given microtheory.
     *
     * @param cycFort the collection for which all the direct and indirect instances
     * (individuals) are sought
     * @param mt the microtheory in which the inference is performed
     * @return the list of all the direct and indirect instances (individuals) for the
     * given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
    */
    public HashSet getAllInstancesHashSet (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllInstances(cycFort, mt));
    }

    /**
     * Gets a hashset of all the direct and indirect instances (individuals) for a CycFort
     * collection in the given microtheory.
     *
     * @param cycFort the collection for which all the direct and indirect instances
     * (individuals) are sought
     * @return the list of all the direct and indirect instances (individuals) for the
     * given collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
    */
    public HashSet getAllInstancesHashSet (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllInstances(cycFort));
    }

    /**
     * Returns true if CycFort TERM is a instance of CycFort COLLECTION, defaulting to all microtheories.
     *
     * @param term the term
     * @param collection the collection
     * @return <tt>true</tt> if CycFort TERM is a instance of CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isa (CycFort term, CycFort collection)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + term.stringApiValue() + " " + collection.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort TERM is a instance of CycFort COLLECTION, using the given microtheory.
     * Method implementation optimised for the binary api.
     *
     * @param term the term
     * @param collection the collection
     * @param mt the microtheory in which the ask is performed
     * @return <tt>true</tt> if CycFort TERM is a instance of CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isa (CycFort term, CycFort collection, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("isa?"));
        command.add(term.cycListApiValue());
        command.add(collection.cycListApiValue());
        command.add(mt.cycListApiValue());
        return converseBoolean(command);
    }

    /**
     * Gets the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyIsa("Brazil", "Country") -->
     * "(((#$isa #$Brazil #$IndependentCountry) :TRUE)
     *    (#$genls #$IndependentCountry #$Country) :TRUE))
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @return the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyIsa (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() + "))");
    }

    /**
     * Gets the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyIsa("Brazil", "Country") -->
     * "(((#$isa #$Brazil #$IndependentCountry) :TRUE)
     *    (#$genls #$IndependentCountry #$Country) :TRUE))
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @param mt the relevant mt
     * @return the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getWhyIsa (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() +
                            " " + mt.stringApiValue()+ ")");
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyGenlParaphase("Brazil", "Country") -->
     * "Brazil is an independent country"
     * "an  independent country is a kind of country"
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @result the English parapharse of the justifications of why CycFort TERM is an instance of
     * CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyIsaParaphrase (CycFort spec, CycFort genl)
        throws IOException, CycApiException {
        String command = "(with-all-mts (why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() + "))";
        CycList listAnswer = converseList(command);
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the English parapharse of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyGenlParaphase("Brazil", "Country") -->
     * "Brazil is an independent country"
     * "an  independent country is a kind of country"
     *
     * @param spec the specialized collection
     * @param genl the more general collection
     * @param mt the relevant mt
     * @result the English parapharse of the justifications of why CycFort TERM is an instance of
     * CycFort COLLECTION
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public ArrayList getWhyIsaParaphrase (CycFort spec, CycFort genl, CycFort mt)
        throws IOException, CycApiException {
        String command = "(why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() +
                         " " + mt.stringApiValue()+ ")";
        CycList listAnswer = converseList(command);
        ArrayList answerPhrases = new ArrayList();
        if (listAnswer.size() == 0)
            return answerPhrases;
        for (int i = 0; i < listAnswer.size(); i++) {
            CycList assertion = (CycList) ((CycList) listAnswer.get(i)).first();
            answerPhrases.add(getParaphrase(assertion));
        }

    return answerPhrases;
    }

    /**
     * Gets the list of the genlPreds for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @result the list of the more general predicates for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenlPreds (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (genl-predicates " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the genlPreds for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @param mt the relevant mt
     * @result the list of the more general predicates for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getGenlPreds (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(genl-predicates " + predicate.stringApiValue() +
                            " " + mt.stringApiValue()+ ")");
    }

    /**
     * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
     *
     * @parameter predicate the predicate for which all the genlPreds are obtained
     * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenlPreds (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-genl-predicates " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
     *
     * @parameter predicate the predicate for which all the genlPreds are obtained
     * @param mt the relevant mt
     * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllGenlPreds (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-genl-predicates " + predicate.stringApiValue() +
                            " " + mt.stringApiValue()+ ")");
    }

    /**
     * Gets the list of all of the direct and indirect specs-preds for the given predicate
     * in all microtheories.
     *
     * @param cycFort the predicate
     * @return the list of all of the direct and indirect spec-preds for the given predicate
     * in all microtheories.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecPreds (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-spec-predicates " +
                            cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the direct and indirect specs-preds for the given predicate
     * in the given microtheory.
     *
     * @param cycFort the predicate
     * @param mt the microtheory
     * @return the list of all of the direct and indirect spec-preds for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecPreds (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-spec-predicates " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the hashset of all of the direct and indirect specs-preds for the given predicate
     * in all microtheories.
     *
     * @param cycFort the predicate
     * @return the hashset of all of the direct and indirect spec-preds for the given predicate
     * in all microtheories.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecPredsHashSet (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecPreds(cycFort));
    }

    /**
     * Gets the hashset of all of the direct and indirect specs-preds for the given predicate
     * in the given microtheory.
     *
     * @param cycFort the predicate
     * @param mt the microtheory
     * @return the hashset of all of the direct and indirect spec-preds for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecPredsHashSet (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecPreds(cycFort, mt));
    }

    /**
     * Gets the list of all of the direct and indirect specs-inverses for the given predicate
     * in all microtheories.
     *
     * @param cycFort the predicate
     * @return the list of all of the direct and indirect spec-inverses for the given predicate
     * in all microtheories.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecInverses (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (all-spec-inverses " +
                            cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets the list of all of the direct and indirect specs-inverses for the given predicate
     * in the given microtheory.
     *
     * @param cycFort the predicate
     * @param mt the microtheory
     * @return the list of all of the direct and indirect spec-inverses for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecInverses (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-spec-inverses " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the hashset of all of the direct and indirect specs-inverses for the given predicate
     * in all microtheories.
     *
     * @param cycFort the predicate
     * @return the hashset of all of the direct and indirect spec-inverses for the given predicate
     * in all microtheories.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecInversesHashSet (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecInverses(cycFort));
    }

    /**
     * Gets the list of all of the direct and indirect specs-mts for the given microtheory
     * in *mt-mt* (currently #$UniversalVocabularyMt).
     *
     * @param mt the microtheory
     * @return the list of all of the direct and indirect specs-mts for the given microtheory
     * in *mt-mt* (currently #$UniversalVocabularyMt)
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getAllSpecMts (CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(all-spec-mts " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the hashset of all of the direct and indirect specs-inverses for the given predicate
     * in the given microtheory.
     *
     * @param cycFort the predicate
     * @param mt the microtheory
     * @return the hashset of all of the direct and indirect spec-inverses for the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecInversesHashSet (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecInverses(cycFort, mt));
    }

    /**
     * Gets the hashset of all of the direct and indirect specs-mts for the given microtheory
     * in *mt-mt* (currently #$UniversalVocabularyMt).
     *
     * @param mt the microtheory
     * @return the hashset of all of the direct and indirect specs-mts for the given microtheory
     * in *mt-mt* (currently #$UniversalVocabularyMt)
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public HashSet getAllSpecMtsHashSet (CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return new HashSet(getAllSpecMts(mt));
    }

    /**
     * Gets a list of the arg1Isas for a CycConstant predicate.
     *
     * @param predicate the predicate for which argument 1 contraints are sought.
     * @return the list of the arg1Isas for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg1Isas (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (arg1-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the arg1Isas for a CycConstant predicate given an mt.
     *
     * @param predicate the predicate for which argument 1 contraints are sought.
     * @param mt the relevant microtheory
     * @return the list of the arg1Isas for a CycConstant predicate given an mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg1Isas (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg1-isa " + predicate.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the arg2Isas for a CycConstant predicate.
     *
     * @param predicate the predicate for which argument 2 contraints are sought.
     * @return the list of the arg1Isas for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg2Isas (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (arg2-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the arg2Isas for a CycConstant predicate given an mt.
     *
     * @param predicate the predicate for which argument 2 contraints are sought.
     * @param mt the relevant microtheory
     * @return the list of the arg2Isas for a CycConstant predicate given an mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg2Isas (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg2-isa " + predicate.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the arg3Isas for a CycConstant predicate.
     *
     * @param predicate the predicate for which argument 3 contraints are sought.
     * @return the list of the arg1Isas for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg3Isas (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (arg3-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the arg3Isas for a CycConstant predicate given an mt.
     *
     * @param predicate the predicate for which argument 3 contraints are sought.
     * @param mt the relevant microtheory
     * @return the list of the arg1Isas for a CycConstant predicate given an mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg3Isas (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg3-isa " + predicate.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the arg4Isas for a CycConstant predicate.
     *
     * @param predicate the predicate for which argument 4 contraints are sought.
     * @return the list of the arg4Isas for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg4Isas (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (arg4-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the arg4Isas for a CycConstant predicate given an mt.
     *
     * @param predicate the predicate for which argument 4 contraints are sought.
     * @param mt the relevant microtheory
     * @return the list of the arg4Isas for a CycConstant predicate given an mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg4Isas (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg4-isa " + predicate.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the argNIsas for a CycConstant predicate.
     *
     * @param predicate the predicate for which argument N contraints are sought.
     * @return the list of the argNIsas for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArgNIsas (CycConstant predicate, int argPosition)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (argn-isa " + predicate.stringApiValue() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets the list of the argNIsas for a CycConstant predicate given an mt.
     *
     * @param predicate the predicate for which argument 1 contraints are sought.
     * @param mt the relevant microtheory
     * @return the list of the arg1Isas for a CycConstant predicate given an mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArgNIsas (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(argn-isa " + predicate.stringApiValue() + " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the resultIsa for a CycConstant function.
     *
     * @param function the given function term
     * @return the list of the resultIsa for a CycConstant function
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getResultIsas (CycConstant function)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (result-isa " + function.stringApiValue() + ")))");
    }

    /**
     * Gets the list of the resultIsa for a CycConstant function.
     *
     * @param function the given function term
     * @param mt the relevant mt
     * @return the list of the resultIsa for a CycConstant function
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getResultIsas (CycConstant function, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(result-isa " + function.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets the list of the argNGenls for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @param argPosition the argument position for which the genls argument
     * constraints are sought (position 1 = first argument)
     * @return the list of the argNGenls for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArgNGenls (CycConstant predicate, int argPosition)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (argn-genl " + predicate.stringApiValue() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets the list of the argNGenls for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @param argPosition the argument position for which the genls argument
     * constraints are sought (position 1 = first argument)
     * @param mt the relevant mt
     * @return the list of the argNGenls for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArgNGenls (CycConstant predicate, int argPosition, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(argn-genl " + predicate.stringApiValue() +
                            " " + argPosition +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the arg1Formats for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @return a list of the arg1Formats for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg1Formats (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (arg1-format " + predicate.stringApiValue() + "))");
    }

    /**
     * Gets a list of the arg1Formats for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @param mt the relevant mt
     * @return a list of the arg1Formats for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg1Formats (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg1-format " + predicate.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the arg2Formats for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @return a list of the arg2Formats for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg2Formats (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(with-all-mts (arg2-format " + predicate.stringApiValue() + "))");
    }

    /**
     * Gets a list of the arg2Formats for a CycConstant predicate.
     *
     * @param predicate the given predicate term
     * @param mt the relevant mt
     * @return a list of the arg2Formats for a CycConstant predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg2Formats (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(arg2-format " + predicate.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the disjointWiths for a CycFort.
     *
     * @param cycFort the given collection term
     * @return a list of the disjointWiths for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getDisjointWiths (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(remove-duplicates (with-all-mts (local-disjoint-with " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the disjointWiths for a CycFort.
     *
     * @param cycFort the given collection term
     * @param mt the relevant mt
     * @return a list of the disjointWiths for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getDisjointWiths (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(local-disjoint-with " + cycFort.stringApiValue() +
                            " " + mt.stringApiValue() + ")");
    }

    /**
     * Gets a list of the coExtensionals for a CycFort.  Limited to 120 seconds.
     *
     * @param cycFort the given collection term
     * @return a list of the coExtensionals for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getCoExtensionals (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        CycList answer = null;
        try {
            answer = converseList("(ask-template '?X '(#$coExtensional " +
                                  cycFort.stringApiValue() + " ?X) #$EverythingPSC nil nil 120)");
        }
        catch (IOException e) {
            System.out.println("getCoExtensionals - ignoring:\n" + e.getMessage());
            return new CycList();
        }
        answer.remove(cycFort);
        return answer;
    }

    /**
     * Gets a list of the coExtensionals for a CycFort.  Limited to 120 seconds.
     *
     * @param cycFort the given collection term
     * @param mt the relevant mt for inference
     * @return a list of the coExtensionals for a CycFort
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getCoExtensionals (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList answer = null;
        try {
            answer = converseList("(ask-template '?X '(#$coExtensional " +
                                  cycFort.stringApiValue() + " ?X) " +
                                  mt.stringApiValue() +
                                  " nil nil 120)");
        }
        catch (IOException e) {
            System.out.println("getCoExtensionals - ignoring:\n" + e.getMessage());
            return new CycList();
        }
        answer.remove(cycFort);
        return answer;
    }

    /**
     * Returns true if cycConstant is a microtheory.
     *
     * @param cycConstant the constant for determination as a microtheory
     * @return <tt>true</tt> iff cycConstant is a microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isMicrotheory (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Microtheory)");
    }

    /**
     * Returns true if cycConstant is a Collection.
     *
     * @param cycConstant the constant for determination as a Collection
     * @return <tt>true</tt> iff cycConstant is a Collection,
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCollection (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Collection)");
    }

    /**
     * Returns true if cycConstant is a Collection, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param cycConstant the constant for determination as a Collection
     * @return <tt>true</tt> iff cycConstant is a Collection,
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCollection_Cached(CycFort cycFort)  throws IOException, CycApiException {
        boolean answer;
        Boolean isCollection = (Boolean) isCollectionCache.getElement(cycFort);
        if (isCollection != null) {
            answer = isCollection.booleanValue();
            return answer;
        }
        answer = isCollection(cycFort);
        isCollectionCache.addElement(cycFort, new Boolean(answer));
        return answer;
    }

    /**
     * Returns true if cycConstant is an Individual.
     *
     * @param cycFort the given term
     * @return true if cycConstant is an Individual
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isIndividual (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Individual)");
    }

    /**
     * Returns true if cycConstant is a Function.
     *
     * @param cycConstant the given term
     * @return true if cycConstant is a Function, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isFunction (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$Function-Denotational)");
    }

    /**
     * Returns true if cycConstant is an evaluatable predicate.
     *
     * @param predicate the given term
     * @return true if cycConstant is an evaluatable predicate, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isEvaluatablePredicate (CycConstant predicate)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("evaluatable-predicate?"));
        command1.add(predicate);
        return converseBoolean(command);
    }

    /**
     * Returns true if cycFort is a Predicate.
     *
     * @param cycFort the term for determination as a predicate
     * @return true if cycFort is a Predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isPredicate (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        if (cycFort instanceof CycNart)
            return false;
        else
            return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Predicate)");
    }

    /**
     * Returns true if cycConstant is a UnaryPredicate.
     *
     * @param cycConstant the given term
     * @return true if cycConstant is a UnaryPredicate, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isUnaryPredicate (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$UnaryPredicate)");
    }

    /**
     * Returns true if cycConstant is a BinaryPredicate.
     *
     * @param cycConstant the given term
     * @return true if cycConstant is a BinaryPredicate, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isBinaryPredicate (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$BinaryPredicate)");
    }

    /**
     * Returns true if the candidate name uses valid CycConstant characters.
     *
     * @param candidateName the candidate name
     * @return true if the candidate name uses valid CycConstant characters
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isValidConstantName (String candidateName)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(new-constant-name-spec-p \"" + candidateName + "\")");
    }

    /**
     * Returns true if the candidate name is an available CycConstant name, case insensitive.
     *
     * @param candidateName the candidate name
     * @return true if the candidate name uses valid CycConstant characters
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isConstantNameAvailable (String candidateName)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(constant-name-available \"" + candidateName + "\")");
    }

    /**
     * Returns true if term is a quotedCollection, in any microtheory
     *
     * @param cycFort the given CycFort term
     * @return true if term is a quotedCollection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isQuotedCollection (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant inferencePSC = getKnownConstantByName("InferencePSC");
        return this.isQuotedCollection(cycFort, inferencePSC);
    }

    /**
     * Returns true if term is a quotedCollection is a quotedCollection.
     *
     * @param cycFort the given CycFort term
     * @param mt the microtheory in which the query is made
     * @return true if term is a quotedCollection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isQuotedCollection (CycFort cycFort, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList query = new CycList();
        query.add(getKnownConstantByName("quotedCollection"));
        query.add(cycFort);
        return this.isQueryTrue(query, mt);
    }

    /**
     * Returns true if cycConstant is a PublicConstant.
     *
     * @param cycConstant the given constant
     * @return true if cycConstant is a PublicConstant
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isPublicConstant (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$PublicConstant)");
    }

    /**
     * Gets a list of the public Cyc constants.
     *
     * @return a list of the public Cyc constants
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getPublicConstants ()
        throws IOException, UnknownHostException, CycApiException {
        // #$PublicConstant
        return getKbSubset(getKnownConstantByGuid("bd7abd90-9c29-11b1-9dad-c379636f7270"));
    }

    /**
     * Gets a list of the elements of the given CycKBSubsetCollection.
     *
     * @param cycKbSubsetCollection the given CycKBSubsetCollection
     * @return a list of the elements of the given CycKBSubsetCollection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getKbSubset (CycFort cycKbSubsetCollection)
        throws IOException, UnknownHostException, CycApiException {
        return converseList("(ask-template '?X '(#$isa ?X " +
                            cycKbSubsetCollection.stringApiValue() +
                            ") #$EverythingPSC)");
    }

    /**
     * Kills a Cyc constant.  If CYCCONSTANT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     *
     * @param cycConstant the constant term to be removed from the KB
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public synchronized void kill (CycConstant cycConstant)
        throws IOException, UnknownHostException, CycApiException {
        converseBoolean("(cyc-kill " + cycConstant.stringApiValue() + ")");
        CycObjectFactory.removeCaches(cycConstant);
    }

    /**
     * Kills the given Cyc constants.  If CYCCONSTANT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     *
     * @param cycConstants the list of constant terms to be removed from the KB
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public synchronized void kill (CycConstant[] cycConstants)
        throws IOException, UnknownHostException, CycApiException {
        for (int i = 0; i < cycConstants.length; i++)
            kill(cycConstants[i]);
    }

    /**
     * Kills the given Cyc constants.  If CYCCONSTANT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     *
     * @param cycConstants the list of constant terms to be removed from the KB
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public synchronized void kill (ArrayList cycConstants)
        throws IOException, UnknownHostException, CycApiException {
        for (int i = 0; i < cycConstants.size(); i++)
            kill((CycConstant) cycConstants.get(i));
    }

    /**
     * Kills a Cyc NART (Non Atomic Reified Term).  If CYCFORT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     *
     * @param cycFort the NART term to be removed from the KB
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public synchronized  void kill (CycFort cycFort)
        throws IOException, UnknownHostException, CycApiException {
        converseBoolean("(cyc-kill '" + cycFort.toString() + ")");
    }

    /**
     * Sets the value of the Cyclist, whose identity will be attached
     * via #$myCreator bookkeeping assertions to new KB entities created
     * in this session.
     *
     * @param cyclistName the name of the cyclist term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void setCyclist (String cyclistName)
        throws IOException, UnknownHostException, CycApiException {
        setCyclist(getConstantByName(cyclistName));
    }

    /**
     * Sets the value of the Cyclist, whose identity will be attached
     * via #$myCreator bookkeeping assertions to new KB entities created
     * in this session.
     *
     * @param cyclis the cyclist term
     */
    public void setCyclist (CycConstant cyclist) {
        this.cyclist = cyclist;
    }

    /**
     * Sets the value of the KE purpose, whose project name will be attached
     * via #$myCreationPurpose bookkeeping assertions to new KB entities
     * created in this session.
     *
     * @param projectName the string name of the KE Purpose term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void setKePurpose (String projectName)
        throws IOException, UnknownHostException, CycApiException {
        setKePurpose(getConstantByName(projectName));
    }

    /**
     * Sets the value of the KE purpose, whose project name will be attached
     * via #$myCreationPurpose bookkeeping assertions to new KB entities
     * created in this session.
     *
     * @param project the KE Purpose term
     */
    public void setKePurpose (CycConstant project) {
        this.project = project;
    }

    /**
     * Asserts the given sentence and also places it on the transcript queue
     * with default strength and direction.
     *
     * @param sentence the given sentence for assertion
     * @param mt the microtheory in which the assertion is placed
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertWithTranscript (CycList sentence, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        String command =
            "(clet ((*the-cyclist* " + cyclist.cyclify() + "))\n" +
            "  (ke-assert-now\n" +
            "    '" + sentence.cyclify() + "\n" +
            "    " + mt.cyclify() + "))";
        converseVoid(command);
    }

    /**
     * Returns a with-bookkeeping-info macro expresssion.
     *
     * @return a with-bookkeeping-info macro expresssion
     */
    protected String withBookkeepingInfo () {
        String projectName = "nil";
        if (project != null)
            projectName = project.stringApiValue();
        String cyclistName = "nil";
        if (cyclist != null)
            cyclistName = cyclist.stringApiValue();
        return "(with-bookkeeping-info (new-bookkeeping-info " +
            cyclistName + " (the-date) " +
            projectName + "(the-second)) ";
    }

    /**
     * Finds or creates a Cyc constant in the KB with the specified name.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param constantName the name of the new constant
     * @return the new constant term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant findOrCreate (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        return createNewPermanent(constantName);
    }

    /**
     * Creates a new permanent Cyc constant in the KB with the specified name.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param constantName the name of the new constant
     * @return the new constant term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant createNewPermanent (String constantName)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant cycConstant = getConstantByName(constantName);
        if (cycConstant != null)
            return cycConstant;
        String name = constantName;
        if (name.startsWith("#$"))
            name = name.substring(2);
        String command = withBookkeepingInfo() +
            "(cyc-create-new-permanent \"" + name + "\"))";
        converseVoid(command);
        return getConstantByName(name);
    }

    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param predicate the binary predicate of the assertion
     * @param arg1 the first argument of the predicate
     * @param arg2 the second argument of the predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycFort arg2)
        throws IOException, UnknownHostException, CycApiException {
        // (predicate <CycFort> <CycFort>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param predicate the binary predicate of the assertion
     * @param arg1 the first argument of the predicate
     * @param arg2 the second argument of the predicate, which is a string
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           String arg2)
        throws IOException, UnknownHostException, CycApiException {
        // (predicate <CycFort> <String>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            "\"" + arg2 + "\")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param predicate the binary predicate of the assertion
     * @param arg1 the first argument of the predicate
     * @param arg2 the second argument of the predicate, which is a CycList
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycList arg2)
        throws IOException, UnknownHostException, CycApiException {
        // (predicate <CycFort> <List>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param predicate the binary predicate of the assertion
     * @param arg1 the first argument of the predicate
     * @param arg2 the second argument of the predicate, which is an int
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           int arg2)
        throws IOException, UnknownHostException, CycApiException {
        // (predicate <CycFort> <int>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2 + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }

    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param predicate the ternary predicate of the assertion
     * @param arg1 the first argument of the predicate
     * @param arg2 the second argument of the predicate
     * @param arg3 the third argument of the predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycFort arg2,
                           CycFort arg3)
        throws IOException, UnknownHostException, CycApiException {
        // (predicate <CycFort> <CycFort> <CycFort>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + " " +
            arg3.stringApiValue() + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }

    /**
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param gaf the gaf in the form of a CycList
     * @param mt the microtheory in which the assertion is made
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGaf (CycList gaf,
                           CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        String command = withBookkeepingInfo() +
            "(cyc-assert '" +
            gaf.stringApiValue() +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }

    /**
     * Unasserts the given ground atomic formula (gaf) in the specified microtheory MT.
     *
     * @param gaf the gaf in the form of a CycList
     * @param mt the microtheory in which the assertion is made
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void unassertGaf (CycList gaf,
                           CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        String command = withBookkeepingInfo() +
            "(cyc-unassert '" +
            gaf.stringApiValue() +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }

    /**
     * Assert a nameString for the specified CycConstant in the specified lexical microtheory.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycConstantName the name of the given term
     * @param nameString the given name string for the term
     * @param mtName the name of the microtheory in which the name string is asserted
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertNameString (String cycConstantName,
                                  String nameString,
                                  String mtName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(getKnownConstantByName(mtName),
                  getKnownConstantByGuid("c0fdf7e8-9c29-11b1-9dad-c379636f7270"),
                  getKnownConstantByName(cycConstantName),
                  nameString);
    }

    /**
     * Assert a nameString for the specified CycConstant in the specified lexical microtheory.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFort the given term
     * @param nameString the given name string for the term
     * @param mt the microtheory in which the name string is asserted
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertNameString (CycFort cycFort,
                                  String nameString,
                                  CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(mt,
                  getKnownConstantByGuid("c0fdf7e8-9c29-11b1-9dad-c379636f7270"),
                  cycFort,
                  nameString);
    }

   /**
     * Assert a comment for the specified CycConstant in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param cycConstantName the name of the given term
     * @param comment the comment string
     * @param mtName the name of the microtheory in which the comment is asserted
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertComment (String cycConstantName,
                               String comment,
                               String mtName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(getKnownConstantByName(mtName),
                  CycAccess.comment,
                  getKnownConstantByName(cycConstantName),
                  comment);
    }

    /**
     * Assert a comment for the specified CycConstant in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param cycConstant the givent term
     * @param comment the comment string
     * @param mt the comment assertion microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertComment (CycConstant cycConstant,
                               String comment,
                               CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(mt, CycAccess.comment, cycConstant, comment);
    }

    /**
     * Create a microtheory MT, with a comment, isa <mt type> and CycFort genlMts.
     * An existing microtheory with
     * the same name is killed first, if it exists.
     *
     * @param mtName the name of the microtheory term
     * @param comment the comment for the new microtheory
     * @param isMt the type of the new microtheory
     * @param genlMts the list of more general microtheories
     * @return the new microtheory term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant createMicrotheory (String mtName,
                                          String comment,
                                          CycFort isaMt,
                                          ArrayList genlMts)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant mt = getConstantByName(mtName);
        if (mt != null) {
            kill(mt);
        }
        mt = createNewPermanent(mtName);
        assertComment(mt, comment, baseKB);
        assertGaf(universalVocabularyMt, isa, mt, isaMt);
        Iterator iterator = genlMts.iterator();
        while (true) {
            if (! iterator.hasNext())
                break;
            CycFort aGenlMt = (CycFort) iterator.next();
            assertGaf(universalVocabularyMt, genlMt, mt, aGenlMt);
        }
    return mt;
    }

    /**
     * Create a microtheory MT, with a comment, isa <mt type> and CycFort genlMts.
     * An existing microtheory with
     * the same name is killed first, if it exists.
     *
     * @param mtName the name of the microtheory term
     * @param comment the comment for the new microtheory
     * @param isMt the type (as a string) of the new microtheory
     * @param genlMts the list of more general microtheories (as strings)
     * @return the new microtheory term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant createMicrotheory (String mtName,
                                          String comment,
                                          String isaMtName,
                                          ArrayList genlMts)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant mt = getConstantByName(mtName);
        if (mt != null) {
            kill(mt);
        }
        mt = createNewPermanent(mtName);
        assertComment(mt, comment, baseKB);
        assertIsa(mtName, isaMtName);
        Iterator iterator = genlMts.iterator();
        while (true) {
            if (! iterator.hasNext())
                break;
            String genlMtName = (String) iterator.next();
            assertGenlMt(mtName, genlMtName);
        }
    return mt;
    }

    /**
     * Create a microtheory system for a new mt.  Given a root mt name, create a theory <Root>Mt,
     * create a vocabulary <Root>VocabMt, and a data <Root>DataMt.  Establish genlMt links for the
     * theory mt and data mt.  Assert that the theory mt is a genlMt of the WorldLikeOursCollectorMt.
     * Assert that the data mt is a genlMt of the collector CurrentWorldDataMt.
     *
     * @param mtRootName the root name of the microtheory system
     * @param comment the root comment of the microtheory system
     * @param genlMts the list of more general microtheories
     * @return an array of three elements consisting of the theory mt, vocabulary mt,
     * and the data mt
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant[] createMicrotheorySystem (String mtRootName,
                                                  String comment,
                                                  ArrayList genlMts)
        throws IOException, UnknownHostException, CycApiException {
        //traceOn();
        CycConstant[] mts = {null, null, null};
        String theoryMtName = mtRootName + "Mt";
        String vocabMtName = mtRootName + "VocabMt";
        String vocabMtComment = "The #$VocabularyMicrotheory for #$"+ theoryMtName;
        String dataMtName = mtRootName + "DataMt";
        String dataMtComment = "The #$DataMicrotheory for #$"+ theoryMtName;
        CycConstant worldLikeOursMt = getKnownConstantByGuid("bf4c781d-9c29-11b1-9dad-c379636f7270");
        CycConstant currentWorldDataMt = getKnownConstantByGuid("bf192b1e-9c29-11b1-9dad-c379636f7270");
        CycConstant genlMt_Vocabulary = getKnownConstantByGuid("c054a49e-9c29-11b1-9dad-c379636f7270");

        CycConstant theoryMicrotheory = getKnownConstantByGuid("be5275a8-9c29-11b1-9dad-c379636f7270");
        CycConstant theoryMt = createMicrotheory(theoryMtName,
                                                 comment,
                                                 theoryMicrotheory,
                                                 genlMts);
        CycConstant vocabularyMicrotheory =
            getKnownConstantByGuid("bda19dfd-9c29-11b1-9dad-c379636f7270");
        CycConstant vocabMt = createMicrotheory(vocabMtName,
                                                vocabMtComment,
                                                vocabularyMicrotheory,
                                                new ArrayList());
        CycConstant dataMicrotheory = getKnownConstantByGuid("be5275a8-9c29-11b1-9dad-c379636f7270");
        CycConstant dataMt = createMicrotheory(dataMtName,
                                               dataMtComment,
                                               dataMicrotheory,
                                               new ArrayList());
        assertGaf(baseKB, genlMt_Vocabulary, theoryMt, vocabMt);
        assertGaf(baseKB, genlMt, dataMt, theoryMt);
        assertGaf(baseKB, genlMt, worldLikeOursMt, theoryMt);
        assertGaf(baseKB, genlMt, currentWorldDataMt, dataMt);
        mts[0] = theoryMt;
        mts[1] = vocabMt;
        mts[2] = dataMt;
        //traceOff();
        return mts;
    }

    /**
     * Assert that the specified CycConstant is a collection in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFort the given collection term
     * @param mt the assertion microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsaCollection (CycFort cycFort,
                                     CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(mt, isa, cycFort, collection);
    }

    /**
     * Assert that the genlsCollection is a genls of specCollection,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specCollectionName the name of the more specialized collection
     * @param genlsCollectionName the name of the more generalized collection
     * @param mtName the assertion microtheory name
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenls (String specCollectionName,
                             String genlsCollectionName,
                             String mtName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(getKnownConstantByName(mtName),
                  genls,
                  getKnownConstantByName(specCollectionName),
                  getKnownConstantByName(genlsCollectionName));
    }

    /**
     * Assert that the genlsCollection is a genls of specCollection,
     * in the UniversalVocabularyMt
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specCollectionName the name of the more specialized collection
     * @param genlsCollectionName the name of the more generalized collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenls (String specCollectionName,
                             String genlsCollectionName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt,
                  genls,
                  getKnownConstantByName(specCollectionName),
                  getKnownConstantByName(genlsCollectionName));
    }

    /**
     * Assert that the genlsCollection is a genls of specCollection,
     * in the UniveralVocabularyMt.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specCollection the more specialized collection
     * @param genlsCollection the more generalized collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenls (CycFort specCollection,
                             CycFort genlsCollection)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt, genls, specCollection, genlsCollection);
    }

    /**
     * Assert that the genlsCollection is a genls of specCollection,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specCollection the more specialized collection
     * @param genlsCollection the more generalized collection
     * @param mt the assertion microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenls (CycFort specCollection,
                             CycFort genlsCollection,
                             CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(mt, genls, specCollection, genlsCollection);
    }

    /**
     * Assert that the more general micortheory is a genlMt of the more specialized
     * microtheory, asserted in the UniversalVocabularyMt
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specMtName the name of the more specialized microtheory
     * @param genlMtName the name of the more generalized microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenlMt (String specMtName,
                              String genlsMtName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt,
                  genlMt,
                  getKnownConstantByName(specMtName),
                  getKnownConstantByName(genlsMtName));
    }

    /**
     * Assert that the more general micortheory is a genlMt of the more specialized
     * microtheory, asserted in the UniversalVocabularyMt
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param specMtName the more specialized microtheory
     * @param genlMtName the more generalized microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertGenlMt (CycFort specMt,
                              CycFort genlsMt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt,
                  genlMt,
                  specMt,
                  genlsMt);
    }

    /**
     * Assert that the cycFort is a collection in the UniversalVocabularyMt.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFortName the collection element name
     * @param collectionName the collection name
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsa (String cycFortName,
                           String collectionName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt,
                  isa,
                  getKnownConstantByName(cycFortName),
                  getKnownConstantByName(collectionName));
    }

    /**
     * Assert that the cycFort is a collection,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFortName the collection element name
     * @param collectionName the collection name
     * @param mtName the assertion microtheory name
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsa (String cycFortName,
                           String collectionName,
                           String mtName)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(getKnownConstantByName(mtName),
                  isa,
                  getKnownConstantByName(cycFortName),
                  getKnownConstantByName(collectionName));
    }

    /**
     * Assert that the cycFort is a collection,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFort the collection element
     * @param aCollecton the collection
     * @param mt the assertion microtheory
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsa (CycFort cycFort,
                           CycFort aCollection,
                           CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(mt, isa, cycFort, aCollection);
    }

    /**
     * Assert that the cycFort is a collection,
     * in the UniversalVocabularyMt.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycFort the collection element
     * @param aCollecton the collection
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsa (CycFort cycFort,
                           CycFort aCollection)
        throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt, isa, cycFort, aCollection);
    }

    /**
     * Assert that the specified CycConstant is a #$BinaryPredicate in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     *
     * @param cycConstant the given term
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsaBinaryPredicate (CycConstant cycConstant,
                                          CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        assertIsa(cycConstant, binaryPredicate, mt);
    }

    /**
     * Constructs a new <tt>CycList<tt> object by parsing a string.
     *
     * @param string the string in CycL external (EL). For example:<BR>
     * <code>(#$isa #$Dog #$TameAnimal)</code>
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList makeCycList(String string) throws CycApiException {
        return (new CycListParser(this)).read(string);
    }

    /**
     * Constructs a new <tt>CycConstant</tt> object using the constant name.
     *
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant makeCycConstant(String name)
        throws UnknownHostException, IOException, CycApiException {
        CycConstant cycConstant = this.getConstantByName(name);
        if (cycConstant == null) {
            cycConstant = this.createNewPermanent(name);
            if (cycConstant == null)
                throw new CycApiException("Cannot create new constant for " + name);
            CycObjectFactory.addCycConstantCacheByName(cycConstant);
            CycObjectFactory.addCycConstantCacheById(cycConstant);
        }
        return cycConstant;
    }

    /**
     * Returns a list of bindings for a query with a single unbound variable.
     *
     * @param query the query to be asked in the knowledge base
     * @param variable the single unbound variable in the query for which bindings are sought
     * @param mt the microtheory in which the query is asked
     * @return a list of bindings for the query
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList askWithVariable (CycList query,
                                    CycVariable variable,
                                    CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("(clet ((*cache-inference-results* nil) ");
        queryBuffer.append("       (*compute-inference-results* nil) ");
        queryBuffer.append("       (*unique-inference-result-bindings* t) ");
        queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
        queryBuffer.append("  (without-wff-semantics ");
        queryBuffer.append("    (ask-template '" + variable.stringApiValue() + " ");
        queryBuffer.append("                  '" + query.stringApiValue() + " ");
        queryBuffer.append("                  " + mt.stringApiValue() + " ");
        queryBuffer.append("                  0 nil nil nil)))");
        return converseList(queryBuffer.toString());
    }

    /**
     * Returns a list of bindings for a query with unbound variables.
     *
     * @param query the query to be asked in the knowledge base
     * @param variables the list of unbound variables in the query for which bindings are sought
     * @param mt the microtheory in which the query is asked
     * @return a list of bindings for the query
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList askWithVariables (CycList query,
                                     ArrayList variables,
                                     CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("(clet ((*cache-inference-results* nil) ");
        queryBuffer.append("       (*compute-inference-results* nil) ");
        queryBuffer.append("       (*unique-inference-result-bindings* t) ");
        queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
        queryBuffer.append("  (without-wff-semantics ");
        queryBuffer.append("    (ask-template '" + (new CycList(variables)).stringApiValue() + " ");
        queryBuffer.append("                  '" + query.stringApiValue() + " ");
        queryBuffer.append("                  " + mt.stringApiValue() + " ");
        queryBuffer.append("                  0 nil nil nil)))");
        return converseList(queryBuffer.toString());
    }

    /**
     * Returns <tt>true</tt> iff the ground query is true in the knowledge base.
     *
     * @param query the query to be asked in the knowledge base
     * @param mt the microtheory in which the query is asked
     * @return <tt>true</tt> iff the query is true in the knowledge base
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isQueryTrue (CycList query,
                                CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("removal-ask"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.quote);
        command1.add(query);
        command.add(mt);
        CycList response = converseList(command);
        return response.size() > 0;
    }

    /**
     * Returns <tt>true</tt> iff the query is true in the knowledge base, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param query the query to be asked in the knowledge base
     * @param mt the microtheory in which the query is asked
     * @return <tt>true</tt> iff the query is true in the knowledge base
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isQueryTrue_Cached (CycList query,
                                          CycFort mt)
        throws IOException, CycApiException {
        boolean answer;
        Boolean isQueryTrue = (Boolean) askCache.getElement(query);
        if (isQueryTrue != null) {
            answer = isQueryTrue.booleanValue();
            return answer;
        }
        answer = isQueryTrue(query, mt);
        askCache.addElement(query, new Boolean(answer));
        return answer;
    }

    /**
     * Returns the count of the instances of the given collection.
     *
     * @param collection the collection whose instances are counted
     * @param mt microtheory (including its genlMts) in which the count is determined
     * @return the count of the instances of the given collection
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int countAllInstances(CycFort collection, CycFort mt) throws IOException, CycApiException {
        return this.converseInt("(count-all-instances " +
                                collection.stringApiValue() + " " +
                                mt.stringApiValue() + ")");
    }

    /**
     * Returns the count of the instances of the given collection, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param collection the collection whose instances are counted
     * @param mt microtheory (including its genlMts) in which the count is determined
     * @return the count of the instances of the given collection
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int countAllInstances_Cached(CycFort collection,
                                        CycFort mt)
        throws IOException, CycApiException {
        int answer;
        Integer countAllInstances = (Integer) countAllInstancesCache.getElement(collection);
        if (countAllInstances != null) {
            answer = countAllInstances.intValue();
            return answer;
        }
        answer = countAllInstances(collection, mt);
        countAllInstancesCache.addElement(collection, new Integer(answer));
        return answer;
    }

    /**
     * Gets a list of the backchaining implication rules which might apply to the given rule.
     *
     * @param predicate the predicate for which backward chaining implication rules are sought
     * @param formula the literal for which backward chaining implication rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for backchaining implication rules takes place
     * @return a list of the backchaining implication rules which might apply to the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getBackchainImplicationRules (CycConstant predicate, CycList formula, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer command = new StringBuffer();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-all-mts ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " :pos nil :backward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + formula.stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " :pos nil :backward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + formula.stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        //this.traceOn();
        return converseList(command.toString());
    }

    /**
     * Gets a list of the forward chaining implication rules which might apply to the given rule.
     *
     * @param predicate the predicate for which forward chaining implication rules are sought
     * @param formula the literal for which forward chaining implication rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules takes place
     * @return a list of the forward chaining implication rules which might apply to the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getForwardChainRules (CycConstant predicate, CycList formula, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer command = new StringBuffer();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-all-mts ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " :pos nil :forward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + formula.stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " :pos nil :forward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + formula.stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        return converseList(command.toString());
    }

    /**
     * Gets a list of the backchaining implication rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which backchaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for backchaining rules takes place
     * @return a list of the backchaining implication rules which might apply to the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getBackchainRules (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer command = new StringBuffer();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-all-mts ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " ");
            command.append("                                :sense :pos ");
            command.append("                                :done nil ");
            command.append("                                :direction :backward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " ");
            command.append("                                :sense :pos ");
            command.append("                                :done nil ");
            command.append("                                :direction :backward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        //this.traceOn();
        return converseList(command.toString());
    }

    /**
     * Gets a list of the forward chaining implication rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which forward chaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules takes place
     * @return a list of the forward chaining implication rules which might apply to the given predicate
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getForwardChainRules (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        StringBuffer command = new StringBuffer();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-all-mts ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " ");
            command.append("                                :sense :pos ");
            command.append("                                :done nil ");
            command.append("                                :direction :forward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + " ");
            command.append("                                :sense :pos ");
            command.append("                                :done nil ");
            command.append("                                :direction :forward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        return converseList(command.toString());
    }

    /**
     * Gets the value of a given KB symbol.  This is intended mainly for test case setup.
     *
     * @param symbol the KB symbol which will have a value bound
     * @return the value assigned to the symbol
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Object getSymbolValue (CycSymbol cycSymbol)
        throws IOException, UnknownHostException, CycApiException {
        return converseObject("(symbol-value '" + cycSymbol + ")");
    }

    /**
     * Sets a KB symbol to have the specified value.  This is intended mainly for test case setup.  If the symbol does
     * not exist at the KB, then it will be created and assigned the value.
     *
     * @param symbol the KB symbol which will have a value bound
     * @param value the value assigned to the symbol
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void setSymbolValue (CycSymbol cycSymbol, Object value)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("csetq"));
        command.add(cycSymbol);
        command.add(value);
        converseVoid(command);
    }

    /**
     * Returns <tt>true</tt> iff <tt>CycList</tt> represents a well formed formula.
     *
     * @param cycList the candidate well-formed-formula
     * @return true iff cycList represents a well formed formula
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isWellFormedFormula (CycList cycList)
        throws IOException, UnknownHostException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("el-wff?"));
        CycList command2 = new CycList();
        command1.add(command2);
        command2.add(CycObjectFactory.quote);
        command2.add(cycList);
        return converseBoolean(command);
    }

    /**
     * Returns <tt>true</tt> iff backchain inference on the given predicate is required.
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which backchaining required status is sought
     * @param mt microtheory (including its genlMts) in which the backchaining required status is sought
     * @return <tt>true</tt> iff backchain inference on the given predicate is required
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isBackchainRequired(CycConstant predicate, CycFort mt)
        throws IOException, CycApiException {
        CycConstant backchainRequired =
            getKnownConstantByGuid("beaa3d29-9c29-11b1-9dad-c379636f7270");
        return hasSomePredicateUsingTerm(backchainRequired,
                                         predicate,
                                         new Integer(1),
                                         mt);
    }

    /**
     * Returns <tt>true</tt> iff backchain inference on the given predicate is encouraged.
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which backchaining encouraged status is sought
     * @param mt microtheory (including its genlMts) in which the backchaining encouraged status is sought
     * @return <tt>true</tt> iff backchain inference on the given predicate is encouraged
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isBackchainEncouraged(CycConstant predicate, CycFort mt)
        throws IOException, CycApiException {
        CycConstant backchainEncouraged =
            getKnownConstantByGuid("c09d1cea-9c29-11b1-9dad-c379636f7270");
        return hasSomePredicateUsingTerm(backchainEncouraged,
                                         predicate,
                                         new Integer(1),
                                         mt);
    }

    /**
     * Returns <tt>true</tt> iff backchain inference on the given predicate is discouraged.
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which backchaining discouraged status is sought
     * @param mt microtheory (including its genlMts) in which the backchaining discouraged status is sought
     * @return <tt>true</tt> iff backchain inference on the given predicate is discouraged
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isBackchainDiscouraged(CycConstant predicate, CycFort mt)
        throws IOException, CycApiException {
        CycConstant backchainDiscouraged =
            getKnownConstantByGuid("bfcbce14-9c29-11b1-9dad-c379636f7270");
        return hasSomePredicateUsingTerm(backchainDiscouraged,
                                         predicate,
                                         new Integer(1),
                                         mt);
    }

    /**
     * Returns <tt>true</tt> iff backchain inference on the given predicate is forbidden.
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which backchaining forbidden status is sought
     * @param mt microtheory (including its genlMts) in which the backchaining forbidden status is sought
     * @return <tt>true</tt> iff backchain inference on the given predicate is forbidden
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isBackchainForbidden(CycConstant predicate, CycFort mt)
        throws IOException, CycApiException {
        CycConstant backchainForbidden =
            getKnownConstantByGuid("bfa4e9d2-9c29-11b1-9dad-c379636f7270");
        return hasSomePredicateUsingTerm(backchainForbidden,
                                         predicate,
                                         new Integer(1),
                                         mt);
    }

    /**
     * Returns <tt>true</tt> iff the predicate has the irreflexive property:
     * (#$isa ?PRED #$IrreflexsiveBinaryPredicate).
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which irreflexive status is sought
     * @param mt microtheory (including its genlMts) in which the irreflexive status is sought
     * @return <tt>true</tt> iff the predicate has the irreflexive property:
     * (#$isa ?PRED #$IrreflexsiveBinaryPredicate)
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isIrreflexivePredicate(CycConstant predicate, CycFort mt)
        throws IOException, CycApiException {
        CycConstant irreflexiveBinaryPredicate =
            getKnownConstantByGuid("bd654be7-9c29-11b1-9dad-c379636f7270");
        return this.isa(predicate, irreflexiveBinaryPredicate, mt);
    }

    /**
     * Returns <tt>true</tt> iff any ground formula instances exist having the given predicate,
     * and the given term in the given argument position.
     *
     * @param term the term present at the given argument position
     * @param predicate the <tt>CycConstant</tt> predicate for the formula
     * @param argumentPosition the argument position of the given term in the ground formula
     * @param mt microtheory (including its genlMts) in which the existence is sought
     * @return <tt>true</tt> iff any ground formula instances exist having the given predicate,
     * and the given term in the given argument position
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean hasSomePredicateUsingTerm(CycConstant predicate,
                                             CycFort term,
                                             Integer argumentPosition,
                                             CycFort mt) throws IOException, CycApiException {
        CycList command = new CycList();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.add(CycObjectFactory.makeCycSymbol("some-pred-value-in-any-mt"));
            command.add(term.cycListApiValue());
            command.add(predicate.cycListApiValue());
        }
        else {
            command.add(CycObjectFactory.makeCycSymbol("some-pred-value-in-relevant-mts"));
            command.add(term.cycListApiValue());
            command.add(predicate.cycListApiValue());
            command.add(mt.cycListApiValue());
        }
        command.add(argumentPosition);
        //this.traceOn();
        return converseBoolean(command);
    }

    /**
     * Returns the count of the assertions indexed according to the given pattern,
     * using the best index (from among the predicate and argument indices).  The formula
     * can contain variables.
     *
     * @param formula the formula whose indexed instances are counted
     * @param mt microtheory (including its genlMts) in which the count is determined
     * @return the count of the assertions indexed according to the given pattern,
     * using the best index (from among the predicate and argument indices)
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int countUsingBestIndex(CycList formula, CycFort mt) throws IOException, CycApiException {
        CycList command = new CycList();
        CycConstant inferencePsc = this.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
        CycConstant everythingPsc = this.getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
        if (mt.equals(inferencePsc) ||
            mt.equals(everythingPsc)) {
            command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));
        }
        else {
            command.add(CycObjectFactory.makeCycSymbol("with-mt"));
            command.add(mt.cycListApiValue());
        }
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("best-index-count"));
        CycList command2 = new CycList();
        command1.add(command2);
        command2.add(CycObjectFactory.quote);
        command2.add(formula.cycListApiValue());
        command1.add(CycObjectFactory.t);
        command1.add(CycObjectFactory.t);
        //this.traceOn();
        return converseInt(command);
    }

    /**
     * Imports a MUC (Message Understanding Conference) formatted symbolic expression into
     * cyc via the function which parses the expression and creates assertions for the
     * contained concepts and relations between them.
     *
     * @param mucExpression the MUC (Message Understanding Conference) formatted symbolic
     * expression
     * @param mtName the name of the microtheory in which the imported assertions will be made
     * @return the number of assertions imported from the input MUC expression
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int importMucExpression(CycList mucExpression,
                                   String mtName) throws IOException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("convert-netowl-sexpr-to-cycl-assertions"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.quote);
        command1.add(mucExpression.cycListApiValue());
        command.add(mtName);
        //this.traceOn();
        return converseInt(command);
    }

    /**
     * Returns a list of parsing expressions, each consisting of a parsing span expression,
     * and a list of parsed terms.
     * <pre>
     * (RKF-PHRASE-READER "penguins" #$RKFEnglishLexicalMicrotheoryPSC #$InferencePSC)
     * ==>
     * (((0) (#$Penguin #$PittsburghPenguins)))
     * </pre>
     *
     * @param text the phrase to be parsed
     * @param parsingMt the microtheory in which lexical info is asked
     * @param domainMt the microtherory in which the info about candidate terms is asked
     * @return a parsing expression consisting of a parsing span expression, and a list
     * of parsed terms
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList rkfPhraseReader (String text, CycFort parsingMt, CycFort domainMt)
        throws IOException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("rkf-phrase-reader"));
        command.add(text);
        command.add(parsingMt);
        command.add(domainMt);
        return converseList(command);
    }

    /**
     * Returns a list of disambiguation expressions, corresponding to each of the terms
     * in the given list of objects.
     *
     * <pre>
     * (GENERATE-DISAMBIGUATION-PHRASES-AND-TYPES (QUOTE (#$Penguin #$PittsburghPenguins)))
     * ==>
     * ((#$Penguin "penguin" #$Bird "bird")
     *  (#$PittsburghPenguins "the Pittsburgh Penguins" #$IceHockeyTeam "ice hockey team"))
     * </pre>
     *
     * @param objects the list of terms to be disambiguated
     * @return a list of disambiguation expressions, corresponding to each of the terms
     * in the given list of objects
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList generateDisambiguationPhraseAndTypes (CycList objects)
        throws IOException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("generate-disambiguation-phrases-and-types"));
        command.addQuoted(objects);
        return converseList(command);
    }

    /**
     * Returns the arity of the given predicate.
     *
     * @param predicate the given predicate whose number of arguments is sought
     * @return the arity of the given predicate, or zero if the argument is not
     * a predicate
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public int getArity (CycFort predicate)
        throws IOException, CycApiException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycObjectFactory.makeCycSymbol("arity"));
        command1.add(predicate);
        Object object = this.converseObject(command);
        if (object instanceof Integer)
            return ((Integer) object).intValue();
        else
            return 0;
    }

    /**
     * Returns the list of arg2 values of binary gafs, given the predicate
     * and arg1, looking in all microtheories.
     *
     * @param predicate the given predicate for the gaf pattern
     * @param arg1 the given first argument of the gaf
     * @return the list of arg2 values of the binary gafs
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycList getArg2s (CycFort predicate, Object arg1)
        throws IOException, CycApiException {
        CycList query = new CycList();
        query.add(predicate);
        query.add(arg1);
        CycVariable variable = CycObjectFactory.makeCycVariable("?arg2");
        query.add(variable);
        return (CycList) askWithVariable(query, variable, inferencePSC);
    }

    /**
     * Returns the single (first) arg2 value of a binary gaf, given the predicate
     * and arg0, looking in all microtheories.  Return null if none found.
     *
     * @param predicate the given predicate for the gaf pattern
     * @param arg1 the given first argument of the gaf
     * @return the single (first) arg2 value of the binary gaf(s)
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Object getArg2 (CycFort predicate, Object arg1)
        throws IOException, CycApiException {
        CycList arg2s = getArg2s(predicate, arg1);
        if (arg2s.isEmpty())
            return null;
        else
            return arg2s.first();
    }

    /**
     * Returns true if formula is well-formed in the relevant mt.
     *
     * @param formula the given EL formula
     * @param mt the relevant mt
     * @return true if formula is well-formed in the relevant mt, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isFormulaWellFormed (CycList formula, CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(el-formula-ok? '" + formula.stringApiValue() +
                               " " + mt.stringApiValue() + ")");
    }

    /**
     * Returns true if formula is well-formed Non Atomic Reifable Term.
     *
     * @param formula the given EL formula
     * @return true if formula is well-formed Non Atomic Reifable Term, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCycLNonAtomicReifableTerm (CycList formula)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(cycl-nart-p '" + formula.stringApiValue() + ")");
    }

    /**
     * Returns true if formula is well-formed Non Atomic Un-reifable Term.
     *
     * @param formula the given EL formula
     * @return true if formula is well-formed Non Atomic Un-reifable Term, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCycLNonAtomicUnreifableTerm (CycList formula)
        throws IOException, UnknownHostException, CycApiException {
        return converseBoolean("(cycl-naut-p '" + formula.stringApiValue() + ")");
    }

    /**
     * Creates a new Collector microtheory and links it more general mts.
     *
     * @param mtName the name of the new collector microtheory
     * @param comment the comment for the new collector microtheory
     * @param genlMts the list of more general microtheories
     */
    public CycConstant createCollectorMt (String mtName,
                                          String comment,
                                          ArrayList genlMts)
        throws IOException, CycApiException {
        CycConstant collectorMt = getKnownConstantByName("CollectorMicrotheory");
        return this.createMicrotheory(mtName, comment, collectorMt, genlMts);
        }

    /**
     * Asserts each of the given list of forts to be instances of
     * the given collection in the UniversalVocabularyMt
     *
     * @param fortNames the list of forts
     * @param collectionName
     */
    public void assertIsas (ArrayList fortNames,
                            String collectionName)
        throws IOException, CycApiException {
        ArrayList forts = new ArrayList();
        for (int i = 0; i < forts.size(); i++) {
            Object fort = forts.get(i);
            if (fort instanceof String)
                forts.add(getKnownConstantByName((String) fort));
            else if (fort instanceof CycFort)
                forts.add(fort);
            else
                throw new CycApiException(fort + " is neither String nor CycFort");
            assertIsas(forts,
                       getKnownConstantByName(collectionName));
        }
    }

    /**
     * Asserts each of the given list of forts to be instances of
     * the given collection in the UniversalVocabularyMt
     *
     * @param forts the list of forts
     * @param collection
     */
    public void assertIsas (ArrayList forts,
                            CycFort collection)
        throws IOException, CycApiException {
        for (int i = 0; i < forts.size(); i++) {
            assertIsa((CycFort) forts.get(i), collection);
        }
    }

    /**
     * Creates a new spindle microtheory in the given spindle system.
     *
     * @param spindleMtName the name of the new spindle microtheory
     * @param comment the comment for the new spindle microtheory
     * @param spindleHeadMtName the name of the spindle head microtheory
     * @param spindleCollectorMtName the name of the spindle head microtheory
     */
    public CycConstant createSpindleMt (String spindleMtName,
                                        String comment,
                                        String spindleHeadMtName,
                                        String spindleCollectorMtName)
        throws IOException, CycApiException {
        return createSpindleMt(spindleMtName,
                               comment,
                               getKnownConstantByName(spindleHeadMtName),
                               getKnownConstantByName(spindleCollectorMtName));
    }

    /**
     * Creates a new spindle microtheory in the given spindle system.
     *
     * @param spindleMtName the name of the new spindle microtheory
     * @param comment the comment for the new spindle microtheory
     * @param spindleHeadMt the spindle head microtheory
     * @param spindleCollectorMt the spindle head microtheory
     */
    public CycConstant createSpindleMt (String spindleMtName,
                                        String comment,
                                        CycFort spindleHeadMt,
                                        CycFort spindleCollectorMt)
        throws IOException, CycApiException {
        CycConstant spindleMt = getKnownConstantByName("SpindleMicrotheory");
        ArrayList genlMts = new ArrayList();
        genlMts.add(spindleHeadMt);
        CycConstant mt = this.createMicrotheory(spindleMtName,
                                                comment,
                                                spindleMt,
                                                genlMts);
        assertGaf(universalVocabularyMt,
                  genlMt,
                  spindleCollectorMt,
                  mt);
        return mt;
        }

    /**
     * Creates a new KB subset collection term.
     *
     * @param constantName the name of the new KB subset collection
     * @param comment the comment for the new KB subset collection
     */
    public CycConstant createKbSubsetCollection (String constantName,
                                                 String comment)
        throws IOException, CycApiException {
        CycConstant kbSubsetCollection = getKnownConstantByName("KBSubsetCollection");
        CycConstant cycConstant = getConstantByName(constantName);
        if (cycConstant == null)
            cycConstant = createNewPermanent(constantName);
        assertIsa(cycConstant, kbSubsetCollection);
        assertComment(cycConstant, comment, baseKB);
        assertGenls(cycConstant, thing);
        CycFort variableOrderCollection =
            getKnownConstantByGuid("36cf85d0-20a1-11d6-8000-0050dab92c2f");
        assertIsa(cycConstant,
                  variableOrderCollection,
                  baseKB);
        return cycConstant;
        }

}
