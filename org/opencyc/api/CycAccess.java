package org.opencyc.api;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.oro.util.Cache;
import org.apache.oro.util.CacheLRU;
import org.opencyc.cycobject.CycAssertion;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycListParser;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.cycobject.ELMt;
import org.opencyc.cycobject.ELMtConstant;
import org.opencyc.cycobject.ELMtCycList;
import org.opencyc.cycobject.ELMtNart;
import org.opencyc.cycobject.Guid;
import org.opencyc.util.Log;


/**
 * Provides wrappers for the OpenCyc API.
 * 
 * <p>
 * Collaborates with the <tt>CycConnection</tt> class which manages the api connections.
 * </p>
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class CycAccess {
  /**
   * Dictionary of CycAccess instances, indexed by thread so that the application does not have to
   * keep passing around a CycAccess object reference.
   */
  public static HashMap cycAccessInstances = new HashMap();

  /**
   * Shared CycAccess instance when thread synchronization is entirely handled by the application.
   * Use of the CycAccess.current() method returns this reference if the lookup by process thread
   * fails.
   */
  public static CycAccess sharedCycAccessInstance = null;

  /**
   * When true performs tracing of binary mode messages with constant names displayed, which
   * involves recursive api requests.
   */
  public boolean traceWithNames = false;

  /**
   * indicates whether to eagerly obtain constant names for constants returned from the api
   */
  public boolean eagerlyObtainConstantNames = true;
  
  /**
   * the threshold above which constant names are automatically obtained for constants returned
   * from the api
   */
  public int eagerlyObtainConstantNamesThreshold = 10;
  
  /**
   * the list of constants returned from the api that currently have no constant name
   */
  public CycList constantsHavingNoName = new CycList();
  
  /**
   * Stack to prevent tracing of recursive api calls whose sole purpose is to obtain names for
   * traceWithNames.
   */
  protected Stack traceWithNamesStack = new Stack();

  /** Value indicating that the OpenCyc api socket is created and then closed for each api call. */
  public static final int TRANSIENT_CONNECTION = 1;

  /** Value indicating that the OpenCyc api should use one TCP socket for the entire session. */
  public static final int PERSISTENT_CONNECTION = 2;

  /** Value indicating that the OpenCyc api should use one TCP socket for the entire session. */
  public static final int XML_SOAP_CONNECTION = 3;

  /**
   * Default value indicating that the OpenCyc api should use one TCP socket for the entire
   * session.
   */
  public static final int DEFAULT_CONNECTION = PERSISTENT_CONNECTION;

  /**
   * Parameter indicating whether the OpenCyc binary api defers the completion of CycConstant
   * attributes until used for the first time.
   */
  public boolean deferObjectCompletion = true;

  /**
   * Parameter indicating whether the OpenCyc api should use one TCP socket for the entire session,
   * or if the socket is created and then closed for each api call, or if an XML SOAP service
   * provides the message transport.
   */
  public int persistentConnection = DEFAULT_CONNECTION;

  /** Parameter indicating the serial or concurrent messaging mode to the OpenCyc server. */
  public int messagingMode = CycConnection.DEFAULT_MESSAGING_MODE;

  /** Parameter indicating that compatibility with older versions of the OpenCyc api is desired. */
  protected boolean isLegacyMode = false;

  /** Default value for isLegacyMode is no compatibility with older versions of the OpenCyc api. */
  public static final boolean DEFAULT_IS_LEGACY_MODE = false;

  /** the Cyc server host name */
  protected String hostName;

  /** the Cyc server host tcp port number */
  protected int port;

  /** the Cyc server communication mode */
  protected int communicationMode;

  /** the Cyc server OK response code */
  protected static final Integer OK_RESPONSE_CODE = new Integer(200);

  /**
   * Parameter that, when true, causes a trace of the messages to and from the server. This
   * variable preserves the value of the CycConnection trace between instantiations when the
   * connection is transient.
   */
  protected int saveTrace = CycConnection.API_TRACE_NONE;

  /** Convenient reference to #$BaseKb. */
  public static ELMt baseKB = null;

  /** Convenient reference to #$isa. */
  public static CycConstant isa = null;

  /** Convenient reference to #$genls. */
  public static CycConstant genls = null;

  /** Convenient reference to #$genlMt. */
  public static CycConstant genlMt = null;

  /** Convenient reference to #$comment. */
  public static CycConstant comment = null;

  /** Convenient reference to #$Collection. */
  public static CycConstant collection = null;

  /** Convenient reference to #$binaryPredicate. */
  public static CycConstant binaryPredicate = null;

  /** Convenient reference to #$elementOf. */
  public static CycConstant elementOf = null;

  /** Convenient reference to #$numericallyEqual. */
  public static CycConstant numericallyEqual = null;
  
  /************************* constants needed by CycL parser *********/
  
  /** Convenient reference to #$True. */
  public static CycConstant trueConst = null; 
  
  /** Convenient reference to #$False. */
  public static CycConstant falseConst = null;
  
  /** Convenient reference to #$not. */
  public static CycConstant not = null;
  
  /** Convenient reference to #$and. */
  public static CycConstant and = null;

  /** Convenient reference to #$or. */
  public static CycConstant or = null;

  /** Convenient reference to #$xor. */
  public static CycConstant xorConst = null;
  
  /** Convenient reference to #$equiv. */
  public static CycConstant equivConst = null;
  
  /** Convenient reference to #$implies. */
  public static CycConstant impliesConst = null;
  
  /** Convenient reference to #$forAll. */
  public static CycConstant forAllConst = null;
  
  /** Convenient reference to #$thereExists. */
  public static CycConstant thereExistsConst = null;
  
  /** Convenient reference to #$thereExistExactly. */
  public static CycConstant thereExistExactlyConst = null;
  
  /** Convenient reference to #$thereExistAtMost. */
  public static CycConstant thereExistAtMostConst = null;
  
  /** Convenient reference to #$thereExistAtLeast. */
  public static CycConstant thereExistAtLeastConst = null;
  
  /** Convenient reference to #$ExapndSubLFn. */
  public static CycConstant expandSubLFnConst = null;
  
  /** Convenient reference to #$SubLQuoteFn. */
  public static CycConstant sublQuoteFnConst = null;
  
  /********************************************************************/


  /** Convenient reference to #$PlusFn. */
  public static CycConstant plusFn = null;

  /** Convenient reference to #$different. */
  public static CycConstant different = null;

  /** Convenient reference to #$Thing. */
  public static CycConstant thing = null;

  /** Convenient reference to #$InferencePSC. */
  public static ELMt inferencePSC = null;

  /** Convenient reference to #$UniversalVocabularyMt. */
  public static ELMt universalVocabularyMt = null;

  /** Convenient reference to #$bookkeepingMt. */
  public static ELMt bookkeepingMt = null;

  /** the current Cyc Cyclist (user) */
  private CycFort cyclist = null;

  /** the current Cyc project */
  private CycFort project = null;

  /** Least Recently Used Cache of ask results. */
  protected Cache askCache = new CacheLRU(500);

  /** Least Recently Used Cache of countAllInstances results. */
  protected Cache countAllInstancesCache = new CacheLRU(
                                                 500);

  /** Least Recently Used Cache of isCollection results. */
  protected Cache isCollectionCache = new CacheLRU(500);

  /** Least Recently Used Cache of isGenlOf results. */
  protected Cache isGenlOfCache = new CacheLRU(500);

  /**
   * Reference to <tt>CycConnection</tt> object which manages the api connection to the OpenCyc
   * server.
   */
  protected CycConnectionInterface cycConnection;
  
  /** the query properties */
  private final HashMap queryProperties = new HashMap();
  
  /**
   * Constructs a new CycAccess object.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess()
            throws IOException, UnknownHostException, CycApiException {
    this(CycConnection.DEFAULT_HOSTNAME, CycConnection.DEFAULT_BASE_PORT, 
         CycConnection.DEFAULT_COMMUNICATION_MODE, 
         CycAccess.DEFAULT_CONNECTION, 
         CycAccess.DEFAULT_IS_LEGACY_MODE);
  }
  
  public CycAccess(CycConnectionInterface conn)
            throws IOException, CycApiException {
    hostName = conn.getHostName();
    port = conn.getBasePort();
    communicationMode = CycConnection.BINARY_MODE;
    
    
    persistentConnection = XML_SOAP_CONNECTION;
    persistentConnection = PERSISTENT_CONNECTION;
    
    cycConnection = conn;
    commonInitialization();
  }

  /**
   * Constructs a new CycAccess object for a SOAP connection.
   * 
   * @param endpointURL the SOAP XML endpoint URL which indicates the Cyc API web services host
   * @param hostName the name of the computer hosting the Cyc server
   * @param port the Cyc server listening port
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(URL endpointURL, 
                   String hostName, 
                   int port)
            throws IOException, CycApiException {
    throw new RuntimeException("Not implememted");
  }

  /**
   * Constructs a new CycAccess object given a host name.
   * 
   * @param hostName the host name
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName)
            throws IOException, UnknownHostException, CycApiException {
    this(hostName, CycConnection.DEFAULT_BASE_PORT, CycConnection.DEFAULT_COMMUNICATION_MODE, 
         CycAccess.DEFAULT_CONNECTION, 
         false);
  }

  /**
   * Constructs a new CycAccess object given a host name and base port
   * 
   * @param hostName the host name
   * @param basePort the base (HTML serving) TCP socket port number
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName, int basePort)
            throws IOException, UnknownHostException, CycApiException {
    this(hostName, basePort, CycConnection.DEFAULT_COMMUNICATION_MODE, 
         CycAccess.DEFAULT_CONNECTION, 
         false);
  }

  /**
   * Constructs a new CycAccess object given a host name, port, communication mode, persistence
   * indicator and legacy mode.
   * 
   * @param hostName the host name
   * @param basePort the base (HTML serving) TCP socket port number
   * @param communicationMode either ASCII_MODE or BINARY_MODE
   * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with the
   *        OpenCyc server
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName, 
                   int basePort, 
                   int communicationMode, 
                   int persistentConnection)
            throws IOException, UnknownHostException, CycApiException {
    this(hostName, basePort, communicationMode, persistentConnection, false);
  }

  /**
   * Constructs a new CycAccess object given a host name, port, communication mode, persistence
   * indicator and legacy mode.
   * 
   * @param hostName the host name
   * @param basePort the base (HTML serving) TCP socket port number
   * @param communicationMode either ASCII_MODE or BINARY_MODE
   * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with the
   *        OpenCyc server
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName, 
                   int basePort, 
                   int communicationMode, 
                   boolean persistentConnection)
            throws IOException, UnknownHostException, CycApiException {
    this(hostName, basePort, communicationMode, 
         persistentConnection ? PERSISTENT_CONNECTION : TRANSIENT_CONNECTION, 
         false);
  }

  /**
   * Constructs a new CycAccess object given a host name, port, communication mode, persistence
   * indicator and legacy mode.
   * 
   * @param hostName the host name
   * @param basePort the base (HTML serving) TCP socket port number
   * @param communicationMode either ASCII_MODE or BINARY_MODE
   * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with the
   *        OpenCyc server
   * @param isLegacyMode indicates if legacy OpenCyc server compatibility is desired
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName, 
                   int basePort, 
                   int communicationMode, 
                   int persistentConnection, 
                   boolean isLegacyMode)
            throws IOException, UnknownHostException, CycApiException {
    this.hostName = hostName;
    this.port = basePort;
    this.communicationMode = communicationMode;
    this.persistentConnection = persistentConnection;
    this.isLegacyMode = isLegacyMode;

    if (persistentConnection == CycAccess.PERSISTENT_CONNECTION) {
      cycConnection = new CycConnection(hostName, 
                                        port, 
                                        communicationMode, 
                                        this);
    }

    commonInitialization();
  }

  /**
   * Constructs a new CycAccess object given a host name, port, communication mode, persistence
   * indicator, and messaging mode
   * 
   * @param hostName the host name
   * @param basePort the base (HTML serving) TCP socket port number
   * @param communicationMode either ASCII_MODE or BINARY_MODE
   * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with the
   *        OpenCyc server
   * @param messagingMode either SERIAL_MESSAGING_MODE or CONCURRENT_MESSAGING_MODE
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAccess(String hostName, 
                   int basePort, 
                   int communicationMode, 
                   int persistentConnection, 
                   int messagingMode)
            throws IOException, UnknownHostException, CycApiException {
    this.hostName = hostName;
    this.port = basePort;

    if (messagingMode == CycConnection.CONCURRENT_MESSAGING_MODE) {
      if (persistentConnection != PERSISTENT_CONNECTION) {
        throw new CycApiException("Concurrent Messaging requires Persistent Connections");
      }
    }

    this.communicationMode = communicationMode;
    this.persistentConnection = persistentConnection;
    this.messagingMode = messagingMode;

    if (persistentConnection == this.PERSISTENT_CONNECTION) {
      cycConnection = new CycConnection(hostName, 
                                        port, 
                                        communicationMode, 
                                        messagingMode, 
                                        this);
    }

    commonInitialization();
  }

  /**
   * Provides common local and remote CycAccess object initialization.
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  protected void commonInitialization()
                               throws IOException, CycApiException {
    if (Log.current == null) {
      Log.makeLog("cyc-api.log");
    }

    cycAccessInstances.put(Thread.currentThread(), 
                           this);

    if (sharedCycAccessInstance == null) {
      sharedCycAccessInstance = this;
    }

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
   * 
   * @throws RuntimeException when there is no CycAcess object for this thread
   */
  public static CycAccess current() {
    CycAccess cycAccess = (CycAccess) cycAccessInstances.get(Thread.currentThread());

    if (cycAccess == null) {
      if (sharedCycAccessInstance != null) {
        return sharedCycAccessInstance;
      }
      else {
        throw new RuntimeException("No CycAccess object for this thread");
      }
    }

    return cycAccess;
  }

  /**
   * Returns true if there is a CycAccess object for this thread.
   * 
   * @return true if there is a CycAccess object for this thread.
   */
  public static boolean hasCurrent() {
    CycAccess cycAccess = (CycAccess) cycAccessInstances.get(Thread.currentThread());

    if (cycAccess == null) {
      if (sharedCycAccessInstance != null) {
        return true;
      }
      else {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the shared <tt>CycAccess</tt> instance.
   * 
   * @param sharedCycAccessInstance shared<tt>CycAccess</tt> instance
   */
  public static void setSharedCycAccessInstance(CycAccess sharedCycAccessInstance) {
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
    if (cycConnection != null) {
      cycConnection.traceOnDetailed();
    }

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
   * Turns on the diagnostic trace of messages with constant names looked up via recursive api
   * request.
   */
  public void traceNamesOn() {
    traceWithNames = true;
  }

  /**
   * Turns on the diagnostic trace of messages with constant names looked up via recursive api
   * request.
   */
  public void traceNamesOff() {
    traceWithNames = false;
  }

  /**
   * gets the hostname of the connection
   * 
   * @return the hostname of the connection
   */
  public String getHostName() {
    // @hack: we dont actually know if this is a CycConnection object,
    // so this cast may fail woefully -> fix later
    return ((CycConnection) cycConnection).getHostName();
  }

  /**
   * gets the baseport of the connection
   * 
   * @return the baseport of the connection
   */
  public int getBasePort() {
    // @hack: we dont actually know if this is a CycConnection object,
    // so this cast may fail woefully -> fix later
    return ((CycConnection) cycConnection).getBasePort();
  }

  /**
   * Returns the CycConnection object.
   * 
   * @return the CycConnection object
   */
  public CycConnectionInterface getCycConnection() {
    return cycConnection;
  }

  /** Indicates whether the connection is closed */
  private boolean isClosed = false;

  /**
   * Closes the CycConnection object. Modified by APB to be able to handle multiple calls to
   * close() safely.
   */
  public synchronized void close() {
    if (isClosed) {
      return;
    }

    isClosed = true;

    if (cycConnection != null) {
      try {
        /*if (cycConnection instanceof RemoteCycConnection) {
          try {
            this.converseVoid(CycObjectFactory.END_CYC_CONNECTION);
          }
           catch (UnknownHostException e) {
          }
           catch (IOException e) {
          }
           catch (CycApiException e) {
          }
        }*/
      }

      // in case the org.opencyc.cycagent package is omitted
       catch (java.lang.NoClassDefFoundError e) {
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
   * Contains trace with names information
   * 
   * @version $Revision$
   * @author $author$
   */
  protected class TraceWithNamesInfo {
    /** indicates whether to trace with names */
    public boolean traceWithNames;

    /** indicates whether a constant name request is bypassed */
    public boolean bypassConstantNameRequest;

    /**
     * Creates a new TraceWithNamesInfo object.
     */
    public TraceWithNamesInfo() {
    }
  }
  
  /**
   * indicates that the thread is within the converse method
   */
  protected boolean isWithinConverse = false;
  
  /**
   * Converses with Cyc to perform an API command.  Creates a new connection for this command if
   * the connection is not persistent.
   * 
   * @param command the command string or CycList
   * 
   * @return the result as an object array of two objects
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  protected Object[] converse(Object command)
                       throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };

    // stack discipline is required to prevent tracing of recursive
    // name-seeking api requests.
    TraceWithNamesInfo traceWithNamesInfo = new TraceWithNamesInfo();
    boolean bypassConstantNameRequest = false;

    if (traceWithNames) {
      traceWithNamesInfo.traceWithNames = true;

      CycList commandCyclist;

      if (command instanceof String) {
        commandCyclist = this.makeCycList((String) command);
      }
      else {
        commandCyclist = (CycList) command;
      }

      if (commandCyclist.first().equals(CycObjectFactory.makeCycSymbol(
                                              "constant-name"))) {
        traceWithNamesInfo.bypassConstantNameRequest = true;
      }
      else {
        Log.current.println(commandCyclist.cyclify() + " --> cyc");
      }

      traceWithNames = false;
    }

    traceWithNamesStack.push(traceWithNamesInfo);

    if (persistentConnection == this.TRANSIENT_CONNECTION) {
      cycConnection = new CycConnection(hostName, 
                                        port, 
                                        communicationMode, 
                                        messagingMode, 
                                        this);
      cycConnection.setTrace(saveTrace);
    }

    response = cycConnection.converse(command);

    if (persistentConnection == this.TRANSIENT_CONNECTION) {
      saveTrace = cycConnection.getTrace();
      cycConnection.close();
    }

    traceWithNamesInfo = (TraceWithNamesInfo) traceWithNamesStack.pop();
    traceWithNames = traceWithNamesInfo.traceWithNames;
    bypassConstantNameRequest = traceWithNamesInfo.bypassConstantNameRequest;

    if (traceWithNames && !bypassConstantNameRequest) {
      String responseString;

      if (response[1] instanceof CycList) {
        responseString = ((CycList) response[1]).cyclify();
      }
      else if (response[1] instanceof CycFort) {
        responseString = ((CycFort) response[1]).cyclify();
      }
      else {
        responseString = response[1].toString();
      }

      Log.current.println("cyc --> " + responseString);
    }

    if (eagerlyObtainConstantNames  && (! isWithinConverse)) {
      if (response[1] instanceof CycList) {
        CycList constantNames = ((CycList) response[1]).treeConstants();
        Iterator iter = constantNames.iterator();
        while (iter.hasNext()) { 
          CycConstant cycConstant = (CycConstant) iter.next();
          if (cycConstant.safeGetName() == null)
            constantsHavingNoName.add(cycConstant);
        }
      }
      else if (response[1] instanceof CycConstant &&
               ((CycConstant) response[1]).safeGetName() == null)
        constantsHavingNoName.add(response[1]);
      if (constantsHavingNoName.size() >= eagerlyObtainConstantNamesThreshold) {
        CycList constantsHavingNoName1 = (CycList) constantsHavingNoName.clone();
        constantsHavingNoName = new CycList();
        isWithinConverse = true;
        obtainConstantNames(constantsHavingNoName1);
        isWithinConverse = false;
      }
    }
        
    return response;
  }

  /**
   * Obtains constant names for a list of constants
   *
   * @param constants the given list of constants having no name yet
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void obtainConstantNames (List constants)
                            throws IOException, UnknownHostException, CycApiException {
    CycList guidStrings = new CycList();
    Iterator iter = constants.iterator();
    while (iter.hasNext()) {
      CycConstant cycConstant = (CycConstant) iter.next();
      if (cycConstant.safeGetName() == null)
        guidStrings.add(cycConstant.getGuid().toString());
    }
    if (guidStrings.size() == 0)
      return;
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("constant-info-from-guid-strings"));
    command.addQuoted(guidStrings);
    CycList constantInfos = converseList(command);
    iter = constantInfos.iterator();
    while (iter.hasNext()) {
      Object constantInfoObject = iter.next();

      if (constantInfoObject instanceof CycList) {
        CycList constantInfo = (CycList) constantInfoObject;
        Guid guid = CycObjectFactory.makeGuid((String) constantInfo.first());
        CycConstant cycConstant = CycObjectFactory.getCycConstantCacheByGuid(guid);
        if (cycConstant != null) {
          String name = (String) constantInfo.second();
          cycConstant.setName(name);
          CycObjectFactory.addCycConstantCacheByName(cycConstant);
        }
      }
    }
  }
  
  /**
   * Obtains constant guids for a list of constants
   *
   * @param constants the given list of constants having no Guid yet
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void obtainConstantGuids (List constants)
                            throws IOException, UnknownHostException, CycApiException {
    CycList guidStrings = new CycList();
    Iterator iter = constants.iterator();
    while (iter.hasNext()) {
      CycConstant cycConstant = (CycConstant) iter.next();
      if (cycConstant.safeGetGuid() == null)
        guidStrings.add(cycConstant.getName());
    }
    if (guidStrings.size() == 0)
      return;
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("constant-info-from-name-strings"));
    command.addQuoted(guidStrings);
    //System.out.println("Calling: " + command);
    CycList constantInfos = converseList(command);
    iter = constantInfos.iterator();
    while (iter.hasNext()) {
      Object constantInfoObject = iter.next();

      if (constantInfoObject instanceof CycList) {
        CycList constantInfo = (CycList) constantInfoObject;
        String name = (String) constantInfo.second();
        //Guid guid = CycObjectFactory.makeGuid((String) constantInfo.first());
        CycConstant cycConstant = CycObjectFactory.getCycConstantCacheByName(name);
        if (cycConstant != null) {
          Guid guid = (Guid)constantInfo.first();
          cycConstant.setGuid(guid);
          CycObjectFactory.addCycConstantCacheByGuid(cycConstant);
        }
      }
    }
  }
    
  /**
   * Converses with Cyc to perform an API command whose result is returned as an object.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Object converseObject(Object command)
                        throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      return response[1];
    }
    else {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Converses with Cyc to perform an API command whose result is returned as a list.  The symbol
   * nil is returned as the empty list.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList converseList(Object command)
                       throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (response[1].equals(CycObjectFactory.nil)) {
        return new CycList();
      }
      else {
        if (response[1] instanceof CycList)
          return (CycList) response[1];
      }
    }
    String request;
    if (command instanceof CycList)
      request = ((CycList) command).cyclify();
    else
      request = (String) command;
    throw new CycApiException(response[1].toString() + "\nrequest: " + request);
  }

  /**
   * Converses with Cyc to perform an API command whose result is returned as a CycObject.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycObject converseCycObject(Object command)
                              throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (response[1].equals(CycObjectFactory.nil)) {
        return new CycList();
      }
      else {
        return (CycObject) response[1];
      }
    }
    else {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Converses with Cyc to perform an API command whose result is returned as a String.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws IOException if a data communication error occurs
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws CycApiException if the api request results in a cyc server error
   * @throws RuntimeException if the return from Cyc is not a string
   */
  public String converseString(Object command)
                        throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (!(response[1] instanceof String)) {
        throw new RuntimeException("Expected String but received (" + response[1].getClass() + 
                                   ") " + response[1] + "\n in response to command " + command);
      }

      return (String) response[1];
    }
    else {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Converses with Cyc to perform an API command whose result is returned as a boolean.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean converseBoolean(Object command)
                          throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (response[1].toString().equals("T")) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Converses with Cyc to perform an API command whose result is returned as an int.
   * 
   * @param command the command string or CycList
   * 
   * @return the result of processing the API command
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int converseInt(Object command)
                  throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      return (new Integer(response[1].toString())).intValue();
    }
    else {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Converses with Cyc to perform an API command whose result is void.
   * 
   * @param command the command string or CycList
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void converseVoid(Object command)
                    throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    response = converse(command);

    if (response[0].equals(Boolean.FALSE)) {
      String request;

      if (command instanceof CycList) {
        request = ((CycList) command).cyclify();
      }
      else {
        request = (String) command;
      }

      throw new CycApiException(response[1].toString() + "\nrequest: " + request);
    }
  }

  /**
   * Sets the print-readable-narts feature on.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void setReadableNarts()
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
    CycList guidStrings = new CycList();
    guidStrings.add("bd588111-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd588104-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd58810e-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880e5-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd588109-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880cc-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd588102-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("c0659a2b-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880f9-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880fa-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880fb-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd589d90-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880ae-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd63f343-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880f4-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd58915a-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("dff4a041-4da2-11d6-82c0-0002b34c7c9f");
    guidStrings.add("beaed5bd-9c29-11b1-9dad-c379636f7270");
    guidStrings.add("bd5880d9-9c29-11b1-9dad-c379636f7270"); // true
    guidStrings.add("bd5880d8-9c29-11b1-9dad-c379636f7270"); // false
    guidStrings.add("bde7f9f2-9c29-11b1-9dad-c379636f7270"); // xor
    guidStrings.add("bda887b6-9c29-11b1-9dad-c379636f7270"); // equiv
    guidStrings.add("bd5880f8-9c29-11b1-9dad-c379636f7270"); // implies
    guidStrings.add("bd5880f7-9c29-11b1-9dad-c379636f7270"); // forAll
    guidStrings.add("bd5880f6-9c29-11b1-9dad-c379636f7270"); // thereExists
    guidStrings.add("c10ae7b8-9c29-11b1-9dad-c379636f7270"); // thereExistExactly
    guidStrings.add("c10af932-9c29-11b1-9dad-c379636f7270"); // thereExistAtMost
    guidStrings.add("c10af5e7-9c29-11b1-9dad-c379636f7270"); // thereExistAtLeast
    guidStrings.add("94f07021-8b0d-11d7-8701-0002b3a8515d"); // SubLQuoteFn
    guidStrings.add("c0b2bc13-9c29-11b1-9dad-c379636f7270"); // ExpandSubLFn

    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("constant-info-from-guid-strings"));
    command.addQuoted(guidStrings);

    CycList constantInfos = converseList(command);
    Iterator iter = constantInfos.iterator();
    HashMap constantInfoDictionary = new HashMap();

    while (iter.hasNext()) {
      Object constantInfoObject = iter.next();

      if (constantInfoObject instanceof CycList) {
        CycList constantInfo = (CycList) constantInfoObject;
        Guid guid = CycObjectFactory.makeGuid((String) constantInfo.first());
        String name = (String) constantInfo.second();
        constantInfoDictionary.put(guid, name);
      }
    }

    Guid guid = null;
    
    if (this.trueConst == null) { 
      trueConst = makePrefetchedConstant("bd5880d9-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.falseConst == null) { 
      falseConst = makePrefetchedConstant("bd5880d8-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.xorConst == null) { 
      xorConst = makePrefetchedConstant("bde7f9f2-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.equivConst == null) { 
      equivConst = makePrefetchedConstant("bda887b6-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.impliesConst == null) { 
      impliesConst = makePrefetchedConstant("bd5880f8-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.forAllConst == null) { 
      forAllConst = makePrefetchedConstant("bd5880f7-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.thereExistsConst == null) { 
      thereExistsConst = makePrefetchedConstant("bd5880f6-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.thereExistExactlyConst == null) { 
      thereExistExactlyConst = makePrefetchedConstant("c10ae7b8-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.thereExistAtMostConst == null) { 
      thereExistAtMostConst = makePrefetchedConstant("c10af932-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.thereExistAtLeastConst == null) { 
      thereExistAtLeastConst = makePrefetchedConstant("c10af5e7-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    if (this.sublQuoteFnConst == null) { 
      sublQuoteFnConst = makePrefetchedConstant("94f07021-8b0d-11d7-8701-0002b3a8515d", constantInfoDictionary);
    }
    if (this.expandSubLFnConst == null) { 
      expandSubLFnConst = makePrefetchedConstant("c0b2bc13-9c29-11b1-9dad-c379636f7270", constantInfoDictionary);
    }
    
    if (baseKB == null) {
      guid = CycObjectFactory.makeGuid("bd588111-9c29-11b1-9dad-c379636f7270");
      baseKB = makeELMt(makeConstantWithGuidName(guid, 
                                                 (String) constantInfoDictionary.get(guid)));
      CycObjectFactory.addCycConstantCacheByName((CycConstant) baseKB);
      CycObjectFactory.addCycConstantCacheByGuid((CycConstant) baseKB);
    }

    if (isa == null) {
      guid = CycObjectFactory.makeGuid("bd588104-9c29-11b1-9dad-c379636f7270");
      isa = makeConstantWithGuidName(guid, 
                                     (String) constantInfoDictionary.get(
                                           guid));
      CycObjectFactory.addCycConstantCacheByName(isa);
      CycObjectFactory.addCycConstantCacheByGuid(isa);
    }

    if (genls == null) {
      guid = CycObjectFactory.makeGuid("bd58810e-9c29-11b1-9dad-c379636f7270");
      genls = makeConstantWithGuidName(guid, 
                                       (String) constantInfoDictionary.get(
                                             guid));
      CycObjectFactory.addCycConstantCacheByName(genls);
      CycObjectFactory.addCycConstantCacheByGuid(genls);
    }

    if (genlMt == null) {
      guid = CycObjectFactory.makeGuid("bd5880e5-9c29-11b1-9dad-c379636f7270");
      genlMt = makeConstantWithGuidName(guid, 
                                        (String) constantInfoDictionary.get(
                                              guid));
      CycObjectFactory.addCycConstantCacheByName(genlMt);
      CycObjectFactory.addCycConstantCacheByGuid(genlMt);
    }

    if (comment == null) {
      guid = CycObjectFactory.makeGuid("bd588109-9c29-11b1-9dad-c379636f7270");
      comment = makeConstantWithGuidName(guid, 
                                         (String) constantInfoDictionary.get(
                                               guid));
      CycObjectFactory.addCycConstantCacheByName(comment);
      CycObjectFactory.addCycConstantCacheByGuid(comment);
    }

    if (collection == null) {
      guid = CycObjectFactory.makeGuid("bd5880cc-9c29-11b1-9dad-c379636f7270");
      collection = makeConstantWithGuidName(guid, 
                                            (String) constantInfoDictionary.get(
                                                  guid));
      CycObjectFactory.addCycConstantCacheByName(collection);
      CycObjectFactory.addCycConstantCacheByGuid(collection);
    }

    if (binaryPredicate == null) {
      guid = CycObjectFactory.makeGuid("bd588102-9c29-11b1-9dad-c379636f7270");
      binaryPredicate = makeConstantWithGuidName(guid, 
                                                 (String) constantInfoDictionary.get(
                                                       guid));
      CycObjectFactory.addCycConstantCacheByName(binaryPredicate);
      CycObjectFactory.addCycConstantCacheByGuid(binaryPredicate);
    }

    if (elementOf == null) {
      guid = CycObjectFactory.makeGuid("c0659a2b-9c29-11b1-9dad-c379636f7270");
      elementOf = makeConstantWithGuidName(guid, 
                                           (String) constantInfoDictionary.get(
                                                 guid));
      CycObjectFactory.addCycConstantCacheByName(elementOf);
      CycObjectFactory.addCycConstantCacheByGuid(elementOf);
    }

    if (and == null) {
      guid = CycObjectFactory.makeGuid("bd5880f9-9c29-11b1-9dad-c379636f7270");
      and = makeConstantWithGuidName(guid, 
                                     (String) constantInfoDictionary.get(
                                           guid));
      CycObjectFactory.addCycConstantCacheByName(and);
      CycObjectFactory.addCycConstantCacheByGuid(and);
    }

    if (or == null) {
      guid = CycObjectFactory.makeGuid("bd5880fa-9c29-11b1-9dad-c379636f7270");
      or = makeConstantWithGuidName(guid, 
                                    (String) constantInfoDictionary.get(
                                          guid));
      CycObjectFactory.addCycConstantCacheByName(or);
      CycObjectFactory.addCycConstantCacheByGuid(or);
    }

    if (not == null) {
      guid = CycObjectFactory.makeGuid("bd5880fb-9c29-11b1-9dad-c379636f7270");
      not = makeConstantWithGuidName(guid, 
                                     (String) constantInfoDictionary.get(
                                           guid));
      CycObjectFactory.addCycConstantCacheByName(not);
      CycObjectFactory.addCycConstantCacheByGuid(not);
    }

    if (numericallyEqual == null) {
      guid = CycObjectFactory.makeGuid("bd589d90-9c29-11b1-9dad-c379636f7270");
      numericallyEqual = makeConstantWithGuidName(guid, 
                                                  (String) constantInfoDictionary.get(
                                                        guid));
      CycObjectFactory.addCycConstantCacheByName(numericallyEqual);
      CycObjectFactory.addCycConstantCacheByGuid(numericallyEqual);
    }

    if (plusFn == null) {
      guid = CycObjectFactory.makeGuid("bd5880ae-9c29-11b1-9dad-c379636f7270");
      plusFn = makeConstantWithGuidName(guid, 
                                        (String) constantInfoDictionary.get(
                                              guid));
      CycObjectFactory.addCycConstantCacheByName(plusFn);
      CycObjectFactory.addCycConstantCacheByGuid(plusFn);
    }

    if (different == null) {
      guid = CycObjectFactory.makeGuid("bd63f343-9c29-11b1-9dad-c379636f7270");
      different = makeConstantWithGuidName(guid, 
                                           (String) constantInfoDictionary.get(
                                                 guid));
      CycObjectFactory.addCycConstantCacheByName(different);
      CycObjectFactory.addCycConstantCacheByGuid(different);
    }

    if (thing == null) {
      guid = CycObjectFactory.makeGuid("bd5880f4-9c29-11b1-9dad-c379636f7270");
      thing = makeConstantWithGuidName(guid, 
                                       (String) constantInfoDictionary.get(
                                             guid));
      CycObjectFactory.addCycConstantCacheByName(thing);
      CycObjectFactory.addCycConstantCacheByGuid(thing);
    }

    if (inferencePSC == null) {
      guid = CycObjectFactory.makeGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
      inferencePSC = makeELMt(makeConstantWithGuidName(
                                    guid, 
                                    (String) constantInfoDictionary.get(
                                          guid)));
      CycObjectFactory.addCycConstantCacheByName((CycConstant) inferencePSC);
      CycObjectFactory.addCycConstantCacheByGuid((CycConstant) inferencePSC);
    }

    if ((!isLegacyMode) && (universalVocabularyMt == null)) {
      guid = CycObjectFactory.makeGuid("dff4a041-4da2-11d6-82c0-0002b34c7c9f");
      universalVocabularyMt = makeELMt(makeConstantWithGuidName(
                                             guid, 
                                             (String) constantInfoDictionary.get(
                                                   guid)));
      CycObjectFactory.addCycConstantCacheByName((CycConstant) universalVocabularyMt);
      CycObjectFactory.addCycConstantCacheByGuid((CycConstant) universalVocabularyMt);
    }

    if (bookkeepingMt == null) {
      guid = CycObjectFactory.makeGuid("beaed5bd-9c29-11b1-9dad-c379636f7270");
      bookkeepingMt = makeELMt(makeConstantWithGuidName(
                                     guid, 
                                     (String) constantInfoDictionary.get(
                                           guid)));
      CycObjectFactory.addCycConstantCacheByName((CycConstant) bookkeepingMt);
      CycObjectFactory.addCycConstantCacheByGuid((CycConstant) bookkeepingMt);
    }
  }
  
  private CycConstant makePrefetchedConstant(String guidStr, HashMap constantInfoDictionary) {
    Guid guid = CycObjectFactory.makeGuid(guidStr);
    CycConstant prefetchedConstant = makeConstantWithGuidName(guid, 
      (String)constantInfoDictionary.get(guid));
    CycObjectFactory.addCycConstantCacheByName(prefetchedConstant);
    CycObjectFactory.addCycConstantCacheByGuid(prefetchedConstant);
    return prefetchedConstant;
  }

  /**
   * Gets a known CycConstant by using its constant name.
   * 
   * @param constantName the name of the constant to be instantiated
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant getKnownConstantByName(String constantName)
                                     throws IOException, UnknownHostException, CycApiException {
    CycConstant cycConstant = getConstantByName(constantName);

    if (cycConstant == null) {
      throw new CycApiException("Expected constant not found " + constantName);
    }

    return cycConstant;
  }

  /**
   * Gets a CycConstant by using its constant name.
   * 
   * @param constantName the name of the constant to be instantiated
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise return null
   * 
   * @throws IOException if a data communication error occurs
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant getConstantByName(String constantName)
                                throws IOException, UnknownHostException, CycApiException {
    String name = constantName;

    if (constantName.startsWith("#$")) {
      name = name.substring(2);
    }

    CycConstant answer = CycObjectFactory.getCycConstantCacheByName(name);

    if (answer != null) {
      return answer;
    }

    answer = new CycConstant();
    answer.setName(name);

    Integer id = getConstantId(name);

    if (id == null) {
      return null;
    }

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
   * 
   * @return the ID for the given CycConstant, or null if the constant does not exist.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Integer getConstantId(CycConstant cycConstant)
                        throws IOException, UnknownHostException, CycApiException {
    return getConstantId(cycConstant.getName());
  }

  /**
   * Gets the ID for the given constant name.
   * 
   * @param constantName the name of the constant object for which the id is sought
   * 
   * @return the ID for the given constant name, or null if the constant does not exist.
   * 
   * @throws IOException if a data communication error occurs
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws CycApiException if the api request results in a cyc server error
   * @throws RuntimeException if a NumberFormatException is thrown when parsing the constant id
   */
  public Integer getConstantId(String constantName)
                        throws IOException, UnknownHostException, CycApiException {
    String command = "(fif " + "  (cand (boolean (find-constant \"" + constantName + "\"))\n" + 
                     "        (valid-constant? (find-constant \"" + constantName + "\")))" + 
                     "  (constant-internal-id (find-constant \"" + constantName + "\"))" + 
                     "  nil)";
    Object obj = converseObject(command);

    if (!(obj instanceof Integer)) {
      return null;
    }

    try {
      return new Integer(converseInt(command));
    }
     catch (NumberFormatException e) {
      e.printStackTrace();
      throw new RuntimeException("NumberFormatException\n" + e.getMessage() + "\nConstantName: " + 
                                 constantName);
    }
  }

  /**
   * Gets the constant ID for the given constant guid.
   * 
   * @param guid the name of the constant object for which the id is sought
   * 
   * @return the ID for the given constant name, or null if the constant does not exist.
   * 
   * @throws IOException if a data communication error occurs
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws CycApiException if the api request results in a cyc server error
   * @throws RuntimeException if a NumberFormatException is thrown when parsing the constant id
   */
  public Integer getConstantId(Guid guid)
                        throws IOException, UnknownHostException, CycApiException {
    String command = "(fif " + 
                     "  (cand (boolean (find-constant-by-external-id (string-to-guid \"" + 
                     guid.toString() + "\")))\n" + 
                     "        (valid-constant? (find-constant-by-external-id (string-to-guid \"" + 
                     guid.toString() + "\"))))" + 
                     "  (constant-internal-id (find-constant-by-external-id (string-to-guid \"" + 
                     guid.toString() + "\")))" + "  nil)";
    Object obj = converseObject(command);

    if (!(obj instanceof Integer)) {
      return null;
    }

    try {
      return new Integer(converseInt(command));
    }
     catch (NumberFormatException e) {
      e.printStackTrace();
      throw new RuntimeException("NumberFormatException\n" + e.getMessage() + "\nGUID: " + guid);
    }
  }

  /**
   * Gets the Guid for the given CycConstant, raising an exception if the constant does not exist.
   * 
   * @param cycConstant the <tt>CycConstant</tt> object for which the id is sought
   * 
   * @return the Guid for the given CycConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Guid getConstantGuid(CycConstant cycConstant)
                       throws IOException, UnknownHostException, CycApiException {
    return getConstantGuid(cycConstant.getName());
  }

  /**
   * Gets the Guid for the given constant name, raising an exception if the constant does not
   * exist.
   * 
   * @param constantName the name of the constant object for which the Guid is sought
   * 
   * @return the Guid for the given CycConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Guid getConstantGuid(String constantName)
                       throws IOException, UnknownHostException, CycApiException {
    String command = "(guid-to-string (constant-external-id (find-constant \"" + constantName + 
                     "\")))";

    return CycObjectFactory.makeGuid(converseString(command));
  }

  /**
   * Gets the Guid for the given constant id.
   * 
   * @param id the id of the <tt>CycConstant</tt> whose guid is sought
   * 
   * @return the Guid for the given CycConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Guid getConstantGuid(Integer id)
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
   * 
   * @return the <tt>CycConstant</tt> if found or <tt>null</tt> if not found
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant getConstantById(Integer id)
                              throws IOException, UnknownHostException, CycApiException {
    // Optimized for the binary api.
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("boolean"));

    CycList command1 = new CycList();
    command.add(command1);
    command1.add(CycObjectFactory.makeCycSymbol("find-constant-by-internal-id"));
    command1.add(id);

    boolean constantExists = converseBoolean(command);

    if (!constantExists) {
      return null;
    }

    CycConstant answer = new CycConstant();
    answer.setName(getConstantName(id));
    answer.setId(id);
    answer.setGuid(getConstantGuid(id));
    CycObjectFactory.addCycConstantCacheByName(answer);
    CycObjectFactory.addCycConstantCacheById(answer);

    return answer;
  }

  /**
   * Gets a <tt>CycAssertion</tt> by using its ID.
   * 
   * @param id the id of the <tt>CycAssertion</tt> sought
   * 
   * @return the <tt>CycAssertion</tt> if found or <tt>null</tt> if not found
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycAssertion getAssertionById(Integer id)
                              throws IOException, UnknownHostException, CycApiException {
    String command = "(find-assertion-by-id " + id + ")";
    Object obj = converseObject(command);

    if (obj.equals(new CycSymbol("NIL")))
      return null;
    else if (! (obj instanceof CycAssertion))
      throw new RuntimeException(obj + " is not a CycAssertion");
    else
      return (CycAssertion)obj;
  }

  /**
   * Gets the name for the given constant id.
   * 
   * @param id the id of the constant object for which the name is sought
   * 
   * @return the name for the given CycConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getConstantName(Integer id)
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
   * Gets the name for the given constant guid.
   * 
   * @param guid the guid of the constant object for which the name is sought
   * 
   * @return the name for the given CycConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getConstantName(Guid guid)
                         throws IOException, UnknownHostException, CycApiException {
    // Optimized for the binary api.
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("constant-name"));

    CycList command1 = new CycList();
    command.add(command1);
    command1.add(CycObjectFactory.makeCycSymbol("find-constant-by-external-id"));

    CycList command2 = new CycList();
    command1.add(command2);
    command2.add(CycObjectFactory.makeCycSymbol("string-to-guid"));
    command2.add(guid.toString());

    return converseString(command);
  }

  /**
   * Gets the name for the given variable id.
   * 
   * @param id the id of the variable object for which the name is sought
   * 
   * @return the name for the given CycVariable
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getVariableName(Integer id)
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
   * @param guidString the globally unique ID string of the constant to be instantiated
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant getKnownConstantByGuid(String guidString)
                                     throws IOException, UnknownHostException, CycApiException {
    Guid guid = CycObjectFactory.makeGuid(guidString);

    return getKnownConstantByGuid(guid);
  }

  /**
   * Gets a known CycConstant by using its GUID.
   * 
   * @param guid the globally unique ID of the constant to be instantiated
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant getKnownConstantByGuid(Guid guid)
                                     throws IOException, UnknownHostException, CycApiException {
    CycConstant cycConstant = getConstantByGuid(guid);

    if (cycConstant == null) {
      throw new CycApiException("Expected constant not found " + guid);
    }

    return cycConstant;
  }

  /**
   * Gets a CycConstant by using its GUID.
   * 
   * @param guid the GUID from which to find the constant
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise return <tt>null</tt>
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant getConstantByGuid(Guid guid)
                                throws IOException, UnknownHostException, CycApiException {
    CycConstant answer = CycObjectFactory.getCycConstantCacheByGuid(
                               guid);

    if (answer != null) {
      return answer;
    }

    answer = new CycConstant();
    answer.setGuid(guid);

    String command = "(fif (boolean (find-constant-by-external-id " + 
                     "                (string-to-guid \"" + guid.toString() + "\")))" + 
                     "  (constant-name (find-constant-by-external-id (string-to-guid \"" + 
                     guid.toString() + "\")))" + "  nil)";
    Object nameObj = converseObject(command);

    if (!(nameObj instanceof String)) {
      return null;
    }

    answer.setName((String) nameObj);

    Integer id = getConstantId((String) nameObj);

    if (id == null) {
      return null;
    }

    answer.setId(id);
    CycObjectFactory.addCycConstantCacheByName(answer);
    CycObjectFactory.addCycConstantCacheById(answer);
    CycObjectFactory.addCycConstantCacheByGuid(answer);

    return answer;
  }

  /**
   * Makes a known CycConstant by using its GUID and name. This method does not access the Cyc
   * server.
   * 
   * @param guidString the known GUID string from which to make the constant
   * @param constantName the known name to associate with the constant
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise return <tt>null</tt>
   */
  public CycConstant makeConstantWithGuidName(String guidString, 
                                              String constantName) {
    return makeConstantWithGuidName(CycObjectFactory.makeGuid(
                                          guidString), 
                                    constantName);
  }

  /**
   * Makes a known CycConstant by using its GUID and name. This method does not access the Cyc
   * server.
   * 
   * @param guid the known GUID from which to make the constant
   * @param constantName the known name to associate with the constant
   * 
   * @return the complete <tt>CycConstant</tt> if found, otherwise return <tt>null</tt>
   */
  public CycConstant makeConstantWithGuidName(Guid guid, 
                                              String constantName) {
    CycConstant answer = CycObjectFactory.getCycConstantCacheByGuid(
                               guid);

    if (answer != null) {
      if ((answer.safeGetName() == null) && (constantName != null)) { 
        answer.setName(constantName); 
        CycObjectFactory.addCycConstantCacheByName(answer);
      }
      return answer;
    }

    answer = new CycConstant();
    answer.setGuid(guid);
    answer.setName(constantName);
    CycObjectFactory.addCycConstantCacheByName(answer);
    CycObjectFactory.addCycConstantCacheByGuid(answer);

    return answer;
  }

  /**
   * Completes the instantiation of objects contained in the given <tt>CycList</tt>. The binary api
   * sends only constant ids, and the constant names and guids must be retrieved if the constant
   * is not cached.
   * 
   * @param object the <tt>CycConstant</tt> to be completed, or the <tt>Object</tt> whose embedded
   *        constants are to be completed
   * 
   * @return the completed object, or a reference to a cached instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Object completeObject(Object object)
                        throws IOException, UnknownHostException, CycApiException {
    if (object instanceof CycConstant) {
      return completeCycConstant((CycConstant) object);
    }
    else if (object instanceof CycList) {
      return completeCycList((CycList) object);
    }
    else if (object instanceof CycNart) {
      return completeCycNart((CycNart) object);
    }
    else {
      return object;
    }
  }

  /**
   * Completes the instantiation of <tt>CycConstant</tt> returned by the binary api. The binary api
   * sends only constant ids, and the constant names and guids must be retrieved if the constant
   * is not cached.
   * 
   * @param cycConstant the <tt>CycConstant</tt> whose name and guid are to be completed
   * 
   * @return the completed <tt>CycConstant</tt> object, or a reference to the previously cached
   *         instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant completeCycConstant(CycConstant cycConstant)
                                  throws IOException, UnknownHostException, CycApiException {
    cycConstant.setName(getConstantName(cycConstant.getId()));

    CycConstant cachedConstant = CycObjectFactory.getCycConstantCacheByName(
                                       cycConstant.getName());

    if (cachedConstant == null) {
      cycConstant.setGuid(getConstantGuid(cycConstant.getId()));
      CycObjectFactory.addCycConstantCacheByName(cycConstant);

      return cycConstant;
    }
    else {
      return cachedConstant;
    }
  }

  /**
   * Completes the instantiation of HL <tt>CycVariable</tt> returned by the binary api. The binary
   * api sends only HL variable ids, and the variable name must be retrieved if the variable is
   * not cached.  The variable id is not used when sending variables to the binary api, instead
   * the variable is output as a symbol.  In the case where an EL variable is returned by the
   * binary api, then then variable name is already present.
   * 
   * @param cycVariable the <tt>CycVariable</tt> whose name is to be completed
   * 
   * @return the completed <tt>CycVariable</tt> object, or a reference to the previously cached
   *         instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycVariable completeCycVariable(CycVariable cycVariable)
                                  throws IOException, UnknownHostException, CycApiException {
    if (cycVariable.name == null) {
      cycVariable.name = getVariableName(cycVariable.hlVariableId);
    }

    CycVariable cachedVariable = CycObjectFactory.getCycVariableCache(
                                       cycVariable.name);

    if (cachedVariable == null) {
      CycObjectFactory.addCycVariableCache(cycVariable);

      return cycVariable;
    }
    else {
      return cachedVariable;
    }
  }

  /**
   * Completes the instantiation of objects contained in the given <tt>CycList</tt>. The binary api
   * sends only constant ids, and the constant names and guids must be retrieved if the constant
   * is not cached.
   * 
   * @param cycList the <tt>CycList</tt> whose constants are to be completed
   * 
   * @return the given list with completed objects
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList completeCycList(CycList cycList)
                          throws IOException, UnknownHostException, CycApiException {
    for (int i = 0; i < cycList.size(); i++) {
      Object element = cycList.get(i);

      if (element instanceof CycList) {
        completeCycList((CycList) element);
      }
      else if (element instanceof CycConstant) {
        // Replace element with the completed constant, which might be previously cached.
        cycList.set(i, 
                    completeCycConstant((CycConstant) element));
      }
      else if (element instanceof CycNart) {
        // Replace element with the completed constant, which might be previously cached.
        cycList.set(i, 
                    completeCycNart((CycNart) element));
      }
      else if (element instanceof CycVariable) {
        // Replace element with the completed variable, which might be previously cached.
        cycList.set(i, 
                    completeCycVariable((CycVariable) element));
      }
      else {
        completeObject(element);
      }
    }

    return cycList;
  }

  /**
   * Completes the instantiation of a <tt>CycNart</tt> returned by the binary api. The binary api
   * sends only constant ids, and the constant names and guids must be retrieved if the constant
   * is not cached.  Also finds the id of the CycNart if the functor and arguments are
   * instantiated.
   * 
   * @param cycNart the <tt>CycNart</tt> whose constants are to be completed
   * 
   * @return the completely instantiated CycNart
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycNart completeCycNart(CycNart cycNart)
                          throws IOException, UnknownHostException, CycApiException {
    Integer id = cycNart.safeGetId();

    if ((id == null) && cycNart.hasFunctorAndArgs()) {
      id = findNartId(cycNart);

      if (id != null) {
        cycNart.setId(id);
      }
    }

    if (id == null) {
      throw new CycApiException("CycNart has no id " + cycNart.safeToString());
    }

    return getCycNartById(cycNart.getId());
  }

  /**
   * Finds the id of a CycNart given its formula.
   * 
   * @param cycNart the CycNart object with functor and arguments instantiated
   * 
   * @return the id of the nart if found in the KB, otherwise null
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Integer findNartId(CycNart cycNart)
                     throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("find-nart"));
    command.addQuoted(cycNart.toCycList());

    Object object = converseObject(command);

    if (object.equals(CycObjectFactory.nil)) {
      return null;
    }

    CycNart foundCycNart = null;

    if (object instanceof CycNart) {
      foundCycNart = (CycNart) object;
    }
    else {
      throw new CycApiException("findNart did not return an null or a nart " + object + " (" + 
                                object.getClass() + ")");
    }

    command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("nart-id"));
    command.add(foundCycNart);

    return new Integer(converseInt(command));
  }

  /**
   * Gets a CycNart by using its id.
   * 
   * @param id the nart id (local to the KB)
   * 
   * @return the CycNart
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycNart getCycNartById(Integer id)
                         throws IOException, UnknownHostException, CycApiException {
    CycNart cycNart = CycObjectFactory.getCycNartCache(
                            id);

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

        if (argument instanceof CycList) {
          arguments.set(i, 
                        new CycNart((CycList) argument));
        }
      }
    }

    return cycNart;
  }

  /**
   * Gets the CycNart object from a Cons object that lists the names of its functor and its
   * arguments.
   * 
   * @param elCons the given list which names the functor and arguments
   * 
   * @return a CycNart object from a Cons object that lists the names of its functor and its
   *         arguments
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
   * 
   * @return true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2 otherwise
   *         false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean predicateRelates(CycConstant binaryPredicate, 
                                  CycFort arg1, 
                                  CycFort arg2)
                           throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    String command = "(pred-u-v-holds-in-any-mt " + binaryPredicate.stringApiValue() + " " + 
                     arg1.stringApiValue() + " " + arg2.stringApiValue() + ")";
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (response[1] == null) {
        return false;
      }
      else if (response[1].toString().equals("T")) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      throw new CycApiException(response[1].toString());
    }
  }

  /**
   * Returns true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2.
   * 
   * @param binaryPredicate the predicate
   * @param arg1 the first argument related by the predicate
   * @param arg2 the second argument related by the predicate
   * @param mt the relevant mt
   * 
   * @return true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2 otherwise
   *         false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean predicateRelates(CycConstant binaryPredicate, 
                                  CycFort arg1, 
                                  CycFort arg2, 
                                  CycObject mt)
                           throws IOException, UnknownHostException, CycApiException {
    Object[] response = { null, null };
    String command = "(pred-u-v-holds " + binaryPredicate.stringApiValue() + " " + 
                     arg1.stringApiValue() + " " + arg2.stringApiValue() + " " + 
                     makeELMt(mt).stringApiValue() + ")";
    response = converse(command);

    if (response[0].equals(Boolean.TRUE)) {
      if (response[1] == null) {
        return false;
      }
      else if (response[1].toString().equals("T")) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      throw new CycApiException(response[1].toString());
    }
  }

  /**
   * Gets the imprecise plural generated phrase for a CycFort (intended for collections).
   * 
   * @param cycFort the term for paraphrasing
   * 
   * @return the imprecise plural generated phrase for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getImprecisePluralGeneratedPhrase(CycFort cycFort)
                                           throws IOException, UnknownHostException, 
                                                  CycApiException {
    return converseString("(with-precise-paraphrase-off (generate-phrase " + 
                          cycFort.stringApiValue() + " '(#$plural)))");
  }

  /**
   * Gets the plural generated phrase for a CycFort (intended for collections).
   * 
   * @param cycObject the term for paraphrasing
   * 
   * @return the plural generated phrase for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getPluralGeneratedPhrase(CycObject cycObject)
                                  throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseString("(with-precise-paraphrase-on (generate-phrase " + 
                          cycObject.stringApiValue() + " '(#$plural)))");
  }

  /**
   * Gets the imprecise singular generated phrase for a CycFort (intended for individuals).
   * 
   * @param cycObject the term for paraphrasing
   * 
   * @return the singular generated phrase for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getImpreciseSingularGeneratedPhrase(CycObject cycObject)
                                             throws IOException, UnknownHostException, 
                                                    CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseString("(with-precise-paraphrase-off (generate-phrase " + 
                          cycObject.stringApiValue() + " '(#$singular)))");
  }

  /**
   * Gets the singular generated phrase for a CycFort (intended for individuals).
   * 
   * @param cycObject the term for paraphrasing
   * 
   * @return the singular generated phrase for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getSingularGeneratedPhrase(CycObject cycObject)
                                    throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseString("(with-precise-paraphrase-on (generate-phrase " + 
                          cycObject.stringApiValue() + " '(#$singular)))");
  }

  /**
   * Gets the default generated phrase for a CycFort (intended for predicates).
   * 
   * @param cycObject the predicate term for paraphrasing
   * 
   * @return the default generated phrase for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getGeneratedPhrase(CycObject cycObject)
                            throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseString("(with-precise-paraphrase-on (generate-phrase " + 
                          cycObject.stringApiValue() + "))");
  }

  /**
   * Gets the paraphrase for a Cyc assertion.
   * 
   * @param assertion the assertion formula
   * 
   * @return the paraphrase for a Cyc assertion
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getParaphrase(CycList assertion)
                       throws IOException, UnknownHostException, CycApiException {
    return converseString("(with-precise-paraphrase-on (generate-phrase " + assertion.stringApiValue() + 
                          "))");
  }

  /**
   * Gets the imprecise paraphrase for a Cyc assertion.
   * 
   * @param assertionString the assertion formula
   * 
   * @return the imprecise paraphrase for a Cyc assertion
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getImpreciseParaphrase(String assertionString)
                                throws IOException, UnknownHostException, CycApiException {
    CycList assertion = this.makeCycList(assertionString);

    return converseString("(with-precise-paraphrase-off (generate-phrase " + 
                          assertion.stringApiValue() + "))");
  }

  /**
   * Gets the imprecise paraphrase for a Cyc assertion.
   * 
   * @param assertion the assertion formula
   * 
   * @return the imprecise paraphrase for a Cyc assertion
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getImpreciseParaphrase(CycList assertion)
                                throws IOException, UnknownHostException, CycApiException {
    return converseString("(with-precise-paraphrase-off (generate-phrase " + 
                          assertion.stringApiValue() + "))");
  }

  /**
   * Gets the comment for a CycFort.  Embedded quotes are replaced by spaces.
   * 
   * @param cycObject the term for which the comment is sought
   * 
   * @return the comment for the given CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getComment(CycObject cycObject)
                    throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    if (cycObject instanceof CycList)
      return null;
    String script = "(clet ((comment-string \n" + "         (with-all-mts (comment " + 
                    cycObject.stringApiValue() + ")))) \n" + "  (fif comment-string \n" + 
                    "       (string-substitute \" \" \"\\\"\" comment-string) \n" + 
                    "       \"\"))";

    return converseString(script);
  }

  /**
   * Gets the comment for a CycFort in the relevant mt. Embedded quotes are replaced by spaces.
   * 
   * @param cycFort the term for which the comment is sought
   * @param mt the relevant mt from which the comment is visible
   * 
   * @return the comment for the given CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public String getComment(CycFort cycFort, 
                           CycObject mt)
                    throws IOException, UnknownHostException, CycApiException {
    String script = "(clet ((comment-string \n" + "         (comment " + 
                    cycFort.stringApiValue() + " " + makeELMt(
                                                           mt).stringApiValue() + "))) \n" + 
                    "  (fif comment-string \n" + 
                    "       (string-substitute \" \" \"\\\"\" comment-string) \n" + 
                    "       \"\"))";

    return converseString(script);
  }

  /**
   * Gets the list of the isas for the given CycFort.
   * 
   * @param cycObject the term for which its isas are sought
   * 
   * @return the list of the isas for the given CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getIsas(CycObject cycObject)
                  throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(remove-duplicates (with-all-mts (isa " + cycObject.stringApiValue() + 
                        ")))");
  }

  /**
   * Gets the list of the isas for the given CycFort.
   * 
   * @param cycFort the term for which its isas are sought
   * @param mt the relevant mt
   * 
   * @return the list of the isas for the given CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getIsas(CycFort cycFort, 
                         CycObject mt)
                  throws IOException, UnknownHostException, CycApiException {
    return converseList("(isa " + cycFort.stringApiValue() + " " + makeELMt(
                                                                         mt).stringApiValue() + 
                        ")");
  }

  /**
   * Gets the list of the directly asserted true genls for the given CycFort collection.
   * 
   * @param cycObject the given term
   * 
   * @return the list of the directly asserted true genls for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenls(CycObject cycObject)
                   throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(remove-duplicates (with-all-mts (genls " + cycObject.stringApiValue() + 
                        ")))");
  }

  /**
   * Gets the list of the directly asserted true genls for the given CycFort collection.
   * 
   * @param cycFort the given term
   * @param mt the relevant mt
   * 
   * @return the list of the directly asserted true genls for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenls(CycFort cycFort, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    return converseList("(genls " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the minimum (most specific) genls for a CycFort collection.
   * 
   * @param cycFort the given collection term
   * 
   * @return a list of the minimum (most specific) genls for a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMinGenls(CycFort cycFort)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (min-genls " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets a list of the minimum (most specific) genls for a CycFort collection.
   * 
   * @param cycFort the collection
   * @param mt the microtheory in which to look
   * 
   * @return a list of the minimum (most specific) genls for a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMinGenls(CycFort cycFort, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(min-genls " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the directly asserted true specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the directly asserted true specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSpecs(CycFort cycFort)
                   throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (specs " + cycFort.stringApiValue() + 
                        ")))");
  }

  /**
   * Gets the list of the directly asserted true specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * @param mt the microtheory in which to look
   * 
   * @return the list of the directly asserted true specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSpecs(CycFort cycFort, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    return converseList("(specs " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the least specific specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the least specific specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMaxSpecs(CycFort cycFort)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (max-specs " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the least specific specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * @param mt the microtheory in which to look
   * 
   * @return the list of the least specific specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMaxSpecs(CycFort cycFort, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(max-specs " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the direct genls of the direct specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the direct genls of the direct specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenlSiblings(CycFort cycFort)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (genl-siblings " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the direct genls of the direct specs for the given CycFort collection.
   * 
   * @param cycFort the given collection
   * @param mt the microtheory in which to look
   * 
   * @return the list of the direct genls of the direct specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenlSiblings(CycFort cycFort, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(genl-siblings " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the siblings (direct specs of the direct genls) for the given CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSiblings(CycFort cycFort)
                      throws IOException, UnknownHostException, CycApiException {
    return getSpecSiblings(cycFort);
  }

  /**
   * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * @param mt the microtheory in which to look
   * 
   * @return the list of the siblings (direct specs of the direct genls) for the given CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSiblings(CycFort cycFort, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return getSpecSiblings(cycFort, 
                           mt);
  }

  /**
   * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the siblings (direct specs of the direct genls) for the given CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSpecSiblings(CycFort cycFort)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (spec-siblings " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the siblings (direct specs of the direct genls) for the given CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * @param mt the microtheory in which to look
   * 
   * @return the list of the siblings (direct specs of the direct genls) for the given CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSpecSiblings(CycFort cycFort, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(spec-siblings " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of all of the direct and indirect genls for the given CycFort collection.
   * 
   * @param cycFort the collection
   * 
   * @return the list of all of the direct and indirect genls for a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenls(CycFort cycFort)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-genls-in-any-mt " + cycFort.stringApiValue() + ")");
  }

  /**
   * Gets the list of all of the direct and indirect genls for a CycFort collection given a
   * relevant microtheory.
   * 
   * @param cycFort the collection
   * @param mt the relevant mt
   * 
   * @return the list of all of the direct and indirect genls for a CycFort collection given a
   *         relevant microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenls(CycFort cycFort, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-genls " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of all of the direct and indirect specs for a CycFort collection.
   * 
   * @param cycFort the collection
   * 
   * @return the list of all of the direct and indirect specs for the given collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecs(CycFort cycFort)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-specs " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the direct and indirect specs for the given collection in the given
   * microtheory.
   * 
   * @param cycFort the collection
   * @param mt the microtheory
   * 
   * @return the list of all of the direct and indirect specs for the given collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecs(CycFort cycFort, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-specs " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a hashset of all of the direct and indirect specs for a CycFort collection.
   * 
   * @param cycFort the collection
   * 
   * @return the hashset of all of the direct and indirect specs for the given collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecsHashSet(CycFort cycFort)
                             throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecs(cycFort));
  }

  /**
   * Gets the hashset of all of the direct and indirect specs for the given collection in the given
   * microtheory.
   * 
   * @param cycFort the collection
   * @param mt the given microtheory
   * 
   * @return the hashset of all of the direct and indirect specs for the given collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecsHashSet(CycFort cycFort, 
                                    CycObject mt)
                             throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecs(cycFort, 
                                   mt));
  }

  /**
   * Gets the list of all of the direct and indirect genls for a CycFort SPEC which are also specs
   * of CycFort GENL.
   * 
   * @param spec the given collection
   * @param genl the more general collection
   * 
   * @return the list of all of the direct and indirect genls for a CycFort SPEC which are also
   *         specs of CycFort GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenlsWrt(CycFort spec, 
                                CycFort genl)
                         throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-genls-wrt " + 
                        spec.stringApiValue() + " " + genl.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the direct and indirect genls for a CycFort SPEC which are also specs
   * of CycFort GENL.
   * 
   * @param spec the given collection
   * @param genl the more general collection
   * @param mt the relevant mt
   * 
   * @return the list of all of the direct and indirect genls for a CycFort SPEC which are also
   *         specs of CycFort GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenlsWrt(CycFort spec, 
                                CycFort genl, 
                                CycObject mt)
                         throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-genls-wrt " + spec.stringApiValue() + " " + genl.stringApiValue() + 
                        " " + makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of all of the dependent specs for a CycFort collection.  Dependent specs are
   * those direct and indirect specs of the collection such that every path connecting the spec to
   * a genl of the collection passes through the collection.  In a typical taxomonmy it is
   * expected that all-dependent-specs gives the same result as all-specs.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of all of the dependent specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllDependentSpecs(CycFort cycFort)
                               throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-dependent-specs " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the dependent specs for a CycFort collection.  Dependent specs are
   * those direct and indirect specs of the collection such that every path connecting the spec to
   * a genl of the collection passes through the collection.  In a typical taxomonmy it is
   * expected that all-dependent-specs gives the same result as all-specs.
   * 
   * @param cycFort the given collection
   * @param mt the relevant mt
   * 
   * @return the list of all of the dependent specs for the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllDependentSpecs(CycFort cycFort, 
                                      CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-dependent-specs " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list with the specified number of sample specs of the given CycFort collection.
   * Attempts to return leaves that are maximally differet with regard to their all-genls.
   * 
   * @param cycFort the given collection
   * @param numberOfSamples the maximum number of sample specs returned
   * 
   * @return the list with the specified number of sample specs of the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSampleLeafSpecs(CycFort cycFort, 
                                    int numberOfSamples)
                             throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (sample-leaf-specs " + cycFort.stringApiValue() + " " + 
                        numberOfSamples + "))");
  }

  /**
   * Gets the list with the specified number of sample specs of the given CycFort collection.
   * Attempts to return leaves that are maximally differet with regard to their all-genls.
   * 
   * @param cycFort the given collection
   * @param numberOfSamples the maximum number of sample specs returned
   * @param mt the relevant mt
   * 
   * @return the list with the specified number of sample specs of the given CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getSampleLeafSpecs(CycFort cycFort, 
                                    int numberOfSamples, 
                                    CycObject mt)
                             throws IOException, UnknownHostException, CycApiException {
    return converseList("(sample-leaf-specs " + cycFort.stringApiValue() + " " + numberOfSamples + 
                        " " + makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Returns the single most specific collection from the given list of collectons.
   * 
   * @param collections the given collections
   * 
   * @return the single most specific collection from the given list of collectons
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycFort getMinCol(CycList collections)
                    throws IOException, UnknownHostException, CycApiException {
    return (CycFort) converseObject("(with-all-mts (min-col " + collections.stringApiValue() + 
                                    "))");
  }

  /**
   * Returns the most specific collections from the given list of collectons.
   * 
   * @param collections the given collections
   * 
   * @return the most specific collections from the given list of collectons
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMinCols(final CycList collections)
                    throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert collections != null : "collections cannot be null";                  
                      
    return converseList("(with-all-mts (min-cols " + collections.stringApiValue() + 
                                    "))");
  }

  /**
   * Returns true if CycFort SPEC is a spec of CycFort GENL.
   * 
   * @param spec the considered spec collection
   * @param genl the considered genl collection
   * 
   * @return true if CycFort SPEC is a spec of CycFort GENL, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isSpecOf(CycFort spec, 
                          CycFort genl)
                   throws IOException, UnknownHostException, CycApiException {
    return isGenlOf(genl, 
                    spec);
  }

  /**
   * Returns true if CycFort SPEC is a spec of CycFort GENL.
   * 
   * @param spec the considered spec collection
   * @param genl the considered genl collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort SPEC is a spec of CycFort GENL, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isSpecOf(CycFort spec, 
                          CycFort genl, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    return isGenlOf(genl, 
                    spec, 
                    mt);
  }

  /**
   * Returns true if CycFort GENL is a genl of CycFort SPEC.
   * 
   * @param genl the collection for genl determination
   * @param spec the collection for spec determination
   * 
   * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlOf(CycFort genl, 
                          CycFort spec)
                   throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(genl-in-any-mt? " + spec.stringApiValue() + " " + 
                           genl.stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort GENL is a genl of CycFort SPEC, implements a cache to avoid asking the
   * same question twice from the KB.
   * 
   * @param genl the collection for genl determination
   * @param spec the collection for spec determination
   * 
   * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlOf_Cached(CycFort genl, 
                                 CycFort spec)
                          throws IOException, UnknownHostException, CycApiException {
    boolean answer;
    List args = new ArrayList();
    args.add(genl);
    args.add(spec);

    Boolean isGenlOf = (Boolean) isGenlOfCache.getElement(
                             args);

    if (isGenlOf != null) {
      answer = isGenlOf.booleanValue();

      return answer;
    }

    answer = isGenlOf(genl, 
                      spec);
    isGenlOfCache.addElement(args, 
                             new Boolean(answer));

    return answer;
  }

  /**
   * Returns true if CycFort GENL is a genl of CycFort SPEC in MT.
   * 
   * @param genl the collection for genl determination
   * @param spec the collection for spec determination
   * @param mt the microtheory for spec determination
   * 
   * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC in MT
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlOf(CycFort genl, 
                          CycFort spec, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(genl? " + spec.stringApiValue() + " " + genl.stringApiValue() + " " + 
                           makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in MT.
   * 
   * @param genlPred the predicate for genl-pred determination
   * @param specPred the predicate for spec-pred determination
   * @param mt the microtheory for subsumption determination
   * 
   * @return <tt>true</tt> if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in MT
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlPredOf(CycFort genlPred, 
                              CycFort specPred, 
                              CycObject mt)
                       throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(genl-predicate? " + specPred.stringApiValue() + " " + 
                           genlPred.stringApiValue() + " " + makeELMt(
                                                                   mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in any MT.
   * 
   * @param genlPred the predicate for genl-pred determination
   * @param specPred the predicate for spec-pred determination
   * 
   * @return <tt>true</tt> if CycFort GENLPRED is a genl-pred of CycFort SPECPRED in any MT
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlPredOf(CycFort genlPred, 
                              CycFort specPred)
                       throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (genl-predicate? " + specPred.stringApiValue() + " " + 
                           genlPred.stringApiValue() + "))");
  }

  /**
   * Returns true if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in MT.
   * 
   * @param genlPred the predicate for genl-inverse determination
   * @param specPred the predicate for spec-inverse determination
   * @param mt the microtheory for inverse subsumption determination
   * 
   * @return <tt>true</tt> if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in MT
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlInverseOf(CycFort genlPred, 
                                 CycFort specPred, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(genl-inverse? " + specPred.stringApiValue() + " " + 
                           genlPred.stringApiValue() + " " + makeELMt(
                                                                   mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in any MT.
   * 
   * @param genlPred the predicate for genl-inverse determination
   * @param specPred the predicate for spec-inverse determination
   * 
   * @return <tt>true</tt> if CycFort GENLPRED is a genl-inverse of CycFort SPECPRED in any MT
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlInverseOf(CycFort genlPred, 
                                 CycFort specPred)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (genl-inverse? " + specPred.stringApiValue() + " " + 
                           genlPred.stringApiValue() + "))");
  }

  /**
   * Returns true if CycFort GENLMT is a genl-mt of CycFort SPECPRED in mt-mt (currently
   * #$UniversalVocabularyMt).
   * 
   * @param genlMt the microtheory for genl-mt determination
   * @param specMt the microtheory for spec-mt determination
   * 
   * @return <tt>true</tt> if CycFort GENLMT is a genl-mt of CycFort SPECPRED in mt-mt (currently
   *         #$UniversalVocabularyMt)
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isGenlMtOf(CycObject genlMt, 
                            CycObject specMt)
                     throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(genl-mt? " + makeELMt(specMt).stringApiValue() + " " + 
                           makeELMt(genlMt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional via
   * mutual genls of each other.
   * 
   * @param collection1 the first given collection
   * @param collection2 the second given collection
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional via
   *         mutual genls of each other, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areTacitCoextensional(CycFort collection1, 
                                       CycFort collection2)
                                throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (tacit-coextensional? " + collection1.stringApiValue() + 
                           " " + collection2.stringApiValue() + "))");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional via
   * mutual genls of each other.
   * 
   * @param collection1 the first given collection
   * @param collection2 the second given collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional via
   *         mutual genls of each other, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areTacitCoextensional(CycFort collection1, 
                                       CycFort collection2, 
                                       CycObject mt)
                                throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(tacit-coextensional? " + collection1.stringApiValue() + " " + 
                           collection2.stringApiValue() + " " + makeELMt(
                                                                      mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional
   *         otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areAssertedCoextensional(CycFort collection1, 
                                          CycFort collection2)
                                   throws IOException, UnknownHostException, CycApiException {
    CycConstant coExtensional = this.getKnownConstantByGuid(
                                      "bd59083a-9c29-11b1-9dad-c379636f7270");

    if (predicateRelates(coExtensional, 
                         collection1, 
                         collection2)) {
      return true;
    }
    else if (predicateRelates(coExtensional, 
                              collection2, 
                              collection1)) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional
   *         otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areAssertedCoextensional(CycFort collection1, 
                                          CycFort collection2, 
                                          CycObject mt)
                                   throws IOException, UnknownHostException, CycApiException {
    CycConstant coExtensional = this.getKnownConstantByGuid(
                                      "bd59083a-9c29-11b1-9dad-c379636f7270");

    if (predicateRelates(coExtensional, 
                         collection1, 
                         collection2, 
                         mt)) {
      return true;
    }
    else if (predicateRelates(coExtensional, 
                              collection2, 
                              collection1, 
                              mt)) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs
   *         otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areIntersecting(CycFort collection1, 
                                 CycFort collection2)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (collections-intersect? " + 
                           collection1.stringApiValue() + " " + collection2.stringApiValue() + 
                           "))");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs
   *         otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areIntersecting(CycFort collection1, 
                                 CycFort collection2, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(collections-intersect? " + collection1.stringApiValue() + " " + 
                           collection2.stringApiValue() + " " + makeELMt(
                                                                      mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areHierarchical(CycFort collection1, 
                                 CycFort collection2)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (hierarchical-collections? " + 
                           collection1.stringApiValue() + " " + collection2.stringApiValue() + 
                           "))");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areHierarchical(CycFort collection1, 
                                 CycFort collection2, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(hierarchical-collections? " + collection1.stringApiValue() + 
                           collection2.stringApiValue() + " " + makeELMt(
                                                                      mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
   * getWhyGenl("Dog", "Animal") --> "(((#$genls #$Dog #$CanineAnimal) :TRUE) (#$genls
   * #$CanineAnimal #$NonPersonAnimal) :TRUE) (#$genls #$NonPersonAnimal #$Animal) :TRUE))
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * 
   * @return the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyGenl(CycFort spec, 
                            CycFort genl)
                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (why-genl? " + spec.stringApiValue() + " " + 
                        genl.stringApiValue() + "))");
  }

  /**
   * Gets the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
   * getWhyGenl("Dog", "Animal") --> "(((#$genls #$Dog #$CanineAnimal) :TRUE) (#$genls
   * #$CanineAnimal #$NonPersonAnimal) :TRUE) (#$genls #$NonPersonAnimal #$Animal) :TRUE))
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * @param mt the relevant mt
   * 
   * @return the list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyGenl(CycFort spec, 
                            CycFort genl, 
                            CycObject mt)
                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(why-genl? " + spec.stringApiValue() + " " + genl.stringApiValue() + 
                        " " + makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
   * GENL. getWhyGenlParaphrase("Dog", "Animal") --> "a dog is a kind of canine" "a canine is a
   * kind of non-human animal" "a non-human animal is a kind of animal"
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * 
   * @return the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
   *         GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyGenlParaphrase(CycFort spec, 
                                        CycFort genl)
                                 throws IOException, UnknownHostException, CycApiException {
    CycList listAnswer = converseList("(with-all-mts (why-genl? " + spec.stringApiValue() + " " + 
                                      genl.stringApiValue() + "))");
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    CycList iter = listAnswer;

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
   * GENL. getWhyGenlParaphrase("Dog", "Animal") --> "a dog is a kind of canine" "a canine is a
   * kind of non-human animal" "a non-human animal is a kind of animal"
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * @param mt the relevant mt
   * 
   * @return the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
   *         GENL
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyGenlParaphrase(CycFort spec, 
                                        CycFort genl, 
                                        CycObject mt)
                                 throws IOException, UnknownHostException, CycApiException {
    CycList listAnswer = converseList("(why-genl? " + spec.stringApiValue() + " " + 
                                      genl.stringApiValue() + " " + 
                                      makeELMt(mt).stringApiValue() + ")");
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    CycList iter = listAnswer;

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2
   * intersect. see getWhyGenl
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2
   *         intersect
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyCollectionsIntersect(CycFort collection1, 
                                            CycFort collection2)
                                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (why-collections-intersect? " + 
                        collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
  }

  /**
   * Gets the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2
   * intersect. see getWhyGenl
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return the list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2
   *         intersect
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyCollectionsIntersect(CycFort collection1, 
                                            CycFort collection2, 
                                            CycObject mt)
                                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(why-collections-intersect? " + collection1.stringApiValue() + " " + 
                        collection2.stringApiValue() + " " + makeELMt(
                                                                   mt).stringApiValue() + ")");
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
   * COLLECTION2 intersect. see getWhyGenlParaphrase
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
   *         COLLECTION2 intersect
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyCollectionsIntersectParaphrase(CycFort collection1, 
                                                        CycFort collection2)
                                                 throws IOException, UnknownHostException, 
                                                        CycApiException {
    CycList listAnswer = converseList("(with-all-mts (why-collections-intersect? " + 
                                      collection1.stringApiValue() + " " + 
                                      collection2.stringApiValue() + "))");
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    CycList iter = listAnswer;

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();


      //Log.current.println("assertion: " + assertion);
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
   * COLLECTION2 intersect. see getWhyGenlParaphrase
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return the English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort
   *         COLLECTION2 intersect
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyCollectionsIntersectParaphrase(CycFort collection1, 
                                                        CycFort collection2, 
                                                        CycObject mt)
                                                 throws IOException, UnknownHostException, 
                                                        CycApiException {
    CycList listAnswer = converseList("(with-all-mts (why-collections-intersect? " + 
                                      collection1.stringApiValue() + " " + 
                                      collection2.stringApiValue() + " " + 
                                      makeELMt(mt).stringApiValue() + ")");
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    CycList iter = listAnswer;

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();


      //Log.current.println("assertion: " + assertion);
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the list of the collection leaves (most specific of the all-specs) for a CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the collection leaves (most specific of the all-specs) for a CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getCollectionLeaves(CycFort cycFort)
                              throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (collection-leaves " + cycFort.stringApiValue() + "))");
  }

  /**
   * Gets the list of the collection leaves (most specific of the all-specs) for a CycFort
   * collection.
   * 
   * @param cycFort the given collection
   * @param mt the relevant mt
   * 
   * @return the list of the collection leaves (most specific of the all-specs) for a CycFort
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getCollectionLeaves(CycFort cycFort, 
                                     CycObject mt)
                              throws IOException, UnknownHostException, CycApiException {
    return converseList("(collection-leaves " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the collections asserted to be disjoint with a CycFort collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the collections asserted to be disjoint with a CycFort collection
   * 
   * @throws IOException if a communication error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getLocalDisjointWith(CycFort cycFort)
                               throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (local-disjoint-with " + cycFort.stringApiValue() + "))");
  }

  /**
   * Gets the list of the collections asserted to be disjoint with a CycFort collection.
   * 
   * @param cycFort the given collection
   * @param mt the relevant mt
   * 
   * @return the list of the collections asserted to be disjoint with a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getLocalDisjointWith(CycFort cycFort, 
                                      CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    return converseList("(local-disjoint-with " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areDisjoint(CycFort collection1, 
                             CycFort collection2)
                      throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (disjoint-with? " + collection1.stringApiValue() + " " + 
                           collection2.stringApiValue() + "))");
  }

  /**
   * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint.
   * 
   * @param collection1 the first collection
   * @param collection2 the second collection
   * @param mt the relevant mt
   * 
   * @return true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean areDisjoint(CycFort collection1, 
                             CycFort collection2, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(with-all-mts (disjoint-with? " + collection1.stringApiValue() + " " + 
                           collection2.stringApiValue() + " " + makeELMt(
                                                                      mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the most specific collections (having no subsets) which contain a CycFort
   * term.
   * 
   * @param cycFort the given term
   * 
   * @return the list of the most specific collections (having no subsets) which contain a CycFort
   *         term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMinIsas(CycFort cycFort)
                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (min-isa " + cycFort.stringApiValue() + "))");
  }

  /**
   * Gets the list of the most specific collections (having no subsets) which contain a CycFort
   * term.
   * 
   * @param cycFort the given term
   * @param mt the relevant mt
   * 
   * @return the list of the most specific collections (having no subsets) which contain a CycFort
   *         term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getMinIsas(CycFort cycFort, 
                            CycObject mt)
                     throws IOException, UnknownHostException, CycApiException {
    return converseList("(min-isa " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the instances (who are individuals) of a CycFort collection.
   * 
   * @param cycFort the given collection
   * 
   * @return the list of the instances (who are individuals) of a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInstances(CycFort cycFort)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (instances " + cycFort.stringApiValue() + "))");
  }

  /**
   * Gets the list of the instances (who are individuals) of a CycFort collection.
   * 
   * @param cycFort the given collection
   * @param mt the relevant mt
   * 
   * @return the list of the instances (who are individuals) of a CycFort collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInstances(CycFort cycFort, 
                              CycObject mt)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(instances " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the instance siblings of a CycFort, for all collections of which it is an
   * instance.
   * 
   * @param cycFort the given term
   * 
   * @return the list of the instance siblings of a CycFort, for all collections of which it is an
   *         instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInstanceSiblings(CycFort cycFort)
                              throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (instance-siblings " + cycFort.stringApiValue() + "))");
  }

  /**
   * Gets the list of the instance siblings of a CycFort, for all collections of which it is an
   * instance.
   * 
   * @param cycFort the given term
   * @param mt the relevant mt
   * 
   * @return the list of the instance siblings of a CycFort, for all collections of which it is an
   *         instance
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if an error is returned by the Cyc server
   */
  public CycList getInstanceSiblings(CycFort cycFort, 
                                     CycObject mt)
                              throws IOException, UnknownHostException, CycApiException {
    return converseList("(instance-siblings " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the collections of which the CycFort is directly and indirectly an instance.
   * 
   * @param cycFort the given term
   * 
   * @return the list of the collections of which the CycFort is directly and indirectly an
   *         instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllIsa(CycFort cycFort)
                    throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-isa-in-any-mt " + cycFort.stringApiValue() + ")");
  }

  /**
   * Gets the list of the collections of which the CycFort is directly and indirectly an instance.
   * 
   * @param cycFort the given term
   * @param mt the relevant mt
   * 
   * @return the list of the collections of which the CycFort is directly and indirectly an
   *         instance
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllIsa(CycFort cycFort, 
                           CycObject mt)
                    throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-isa " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of all the direct and indirect instances (individuals) for a CycFort collection.
   * 
   * @param cycFort the collection for which all the direct and indirect instances (individuals)
   *        are sought
   * 
   * @return the list of all the direct and indirect instances (individuals) for the given
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllInstances(CycFort cycFort)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-instances-in-all-mts " + cycFort.stringApiValue() + ")");
  }

  /**
   * Gets a list of all the direct and indirect instances for a CycFort collection in
   * the given microtheory.
   * 
   * @param cycFort the collection for which all the direct and indirect instances are sought
   * @param mt the relevant mt
   * 
   * @return the list of all the direct and indirect instances for the
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error given collection
   */
  public CycList getAllInstances(CycFort cycFort, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-instances " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of all the direct and indirect quoted instances for a CycFort collection in
   * the given microtheory.
   * 
   * @param cycFort the collection for which all the direct and indirect quoted instances are sought
   * @param mt the relevant mt
   * 
   * @return the list of all the direct and indirect quoted instances for the CycFort collection in
   * the given microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error given collection
   */
  public CycList getAllQuotedInstances(final CycFort cycFort, final CycObject mt) throws IOException, UnknownHostException, CycApiException {
    CycList results = null;
    final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?QUOTED-INSTANCE");
    final CycList query = new CycList();
    query.add(getKnownConstantByName("quotedIsa"));
    query.add(queryVariable);
    query.add(cycFort);
    return queryVariable(queryVariable, query, mt, null);
  }
  
  
  /**
   * Gets a hashset of all the direct and indirect instances (individuals) for a CycFort collection
   * in the given microtheory.
   * 
   * @param cycFort the collection for which all the direct and indirect instances (individuals)
   *        are sought
   * @param mt the microtheory in which the inference is performed
   * 
   * @return the list of all the direct and indirect instances (individuals) for the given
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllInstancesHashSet(CycFort cycFort, 
                                        CycObject mt)
                                 throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllInstances(cycFort, 
                                       mt));
  }

  /**
   * Gets a hashset of all the direct and indirect instances (individuals) for a CycFort collection
   * in the given microtheory.
   * 
   * @param cycFort the collection for which all the direct and indirect instances (individuals)
   *        are sought
   * 
   * @return the list of all the direct and indirect instances (individuals) for the given
   *         collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllInstancesHashSet(CycFort cycFort)
                                 throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllInstances(cycFort));
  }

  /**
   * Returns true if CycFort TERM is a instance of CycFort COLLECTION, defaulting to all
   * microtheories.
   * 
   * @param term the term
   * @param collectionName the name of the collection
   * 
   * @return <tt>true</tt> if CycFort TERM is a instance of the CycFort named by COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isa(CycFort term, 
                     String collectionName)
              throws IOException, UnknownHostException, CycApiException {
    return isa(term, 
               getKnownConstantByName(collectionName));
  }

  /**
   * Returns true if CycFort TERM is a instance of CycFort COLLECTION, defaulting to all
   * microtheories.
   * 
   * @param term the term
   * @param collection the collection
   * 
   * @return <tt>true</tt> if CycFort TERM is a instance of CycFort COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isa(CycObject term, 
                     CycFort collection)
              throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(isa-in-any-mt? " + term.stringApiValue() + " " + 
                           collection.stringApiValue() + ")");
  }

  /**
   * Returns true if CycFort TERM is a instance of CycFort COLLECTION, using the given microtheory.
   * Method implementation optimised for the binary api.
   * 
   * @param term the term
   * @param collection the collection
   * @param mt the microtheory in which the ask is performed
   * 
   * @return <tt>true</tt> if CycFort TERM is a instance of CycFort COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isa(CycFort term, 
                     CycFort collection, 
                     CycObject mt)
              throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("isa?"));
    command.add(term.cycListApiValue());
    command.add(collection.cycListApiValue());
    command.add(makeELMt(mt).cycListApiValue());

    return converseBoolean(command);
  }

  /**
   * Returns true if the quoted CycFort TERM is a instance of CycFort COLLECTION, in any microtheory.
   * Method implementation optimised for the binary api.
   * 
   * @param term the term
   * @param collection the collection
   * 
   * @return <tt>true</tt> if the quoted CycFort TERM is a instance of CycFort COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isQuotedIsa(CycFort term, CycFort collection)
              throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(getKnownConstantByName("quotedIsa"));
    query.add(term);
    query.add(collection);
    return isQueryTrue(query, inferencePSC, null);
  }

  /**
   * Gets the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
   * getWhyIsa("Brazil", "Country") --> "(((#$isa #$Brazil #$IndependentCountry) :TRUE) (#$genls
   * #$IndependentCountry #$Country) :TRUE))
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * 
   * @return the list of the justifications of why CycFort TERM is an instance of CycFort
   *         COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyIsa(CycFort spec, 
                           CycFort genl)
                    throws IOException, UnknownHostException, CycApiException {
    return converseList("(with-all-mts (why-isa? " + spec.stringApiValue() + " " + 
                        genl.stringApiValue() + "))");
  }

  /**
   * Gets the list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
   * getWhyIsa("Brazil", "Country") --> "(((#$isa #$Brazil #$IndependentCountry) :TRUE) (#$genls
   * #$IndependentCountry #$Country) :TRUE))
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * @param mt the relevant mt
   * 
   * @return the list of the justifications of why CycFort TERM is an instance of CycFort
   *         COLLECTION
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getWhyIsa(CycFort spec, 
                           CycFort genl, 
                           CycObject mt)
                    throws IOException, UnknownHostException, CycApiException {
    return converseList("(why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort TERM is an instance of
   * CycFort COLLECTION. getWhyGenlParaphase("Brazil", "Country") --> "Brazil is an independent
   * country" "an  independent country is a kind of country"
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * 
   * @return the English parapharse of the justifications of why CycFort TERM is an instance of
   *         CycFort COLLECTION
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyIsaParaphrase(CycFort spec, 
                                       CycFort genl)
                                throws IOException, CycApiException {
    String command = "(with-all-mts (why-isa? " + spec.stringApiValue() + " " + 
                     genl.stringApiValue() + "))";
    CycList listAnswer = converseList(command);
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the English parapharse of the justifications of why CycFort TERM is an instance of
   * CycFort COLLECTION. getWhyGenlParaphase("Brazil", "Country") --> "Brazil is an independent
   * country" "an  independent country is a kind of country"
   * 
   * @param spec the specialized collection
   * @param genl the more general collection
   * @param mt the relevant mt
   * 
   * @return the English parapharse of the justifications of why CycFort TERM is an instance of
   *         CycFort COLLECTION
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public List getWhyIsaParaphrase(CycFort spec, 
                                       CycFort genl, 
                                       CycObject mt)
                                throws IOException, CycApiException {
    String command = "(why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() + " " + 
                     makeELMt(mt).stringApiValue() + ")";
    CycList listAnswer = converseList(command);
    List answerPhrases = new ArrayList();

    if (listAnswer.size() == 0) {
      return answerPhrases;
    }

    for (int i = 0; i < listAnswer.size(); i++) {
      CycList assertion = (CycList) ((CycList) listAnswer.get(
                                           i)).first();
      answerPhrases.add(getParaphrase(assertion));
    }

    return answerPhrases;
  }

  /**
   * Gets the list of the genlPreds for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * 
   * @return the list of the more general predicates for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenlPreds(CycFort predicate)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (genl-predicates " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the genlPreds for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * @param mt the relevant mt
   * 
   * @return the list of the more general predicates for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getGenlPreds(CycFort predicate, 
                              CycObject mt)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(genl-predicates " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
   * 
   * @param predicate the predicate for which all the genlPreds are obtained
   * 
   * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenlPreds(CycConstant predicate)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-genl-predicates " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
   * 
   * @param predicate the predicate for which all the genlPreds are obtained
   * @param mt the relevant mt
   * 
   * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllGenlPreds(CycConstant predicate, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-genl-predicates " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of all of the direct and indirect specs-preds for the given predicate in all
   * microtheories.
   * 
   * @param cycFort the predicate
   * 
   * @return the list of all of the direct and indirect spec-preds for the given predicate in all
   *         microtheories.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecPreds(CycFort cycFort)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-spec-predicates " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the direct and indirect specs-preds for the given predicate in the
   * given microtheory.
   * 
   * @param cycFort the predicate
   * @param mt the microtheory
   * 
   * @return the list of all of the direct and indirect spec-preds for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecPreds(CycFort cycFort, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-spec-predicates " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the hashset of all of the direct and indirect specs-preds for the given predicate in all
   * microtheories.
   * 
   * @param cycFort the predicate
   * 
   * @return the hashset of all of the direct and indirect spec-preds for the given predicate in
   *         all microtheories.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecPredsHashSet(CycFort cycFort)
                                 throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecPreds(cycFort));
  }

  /**
   * Gets the hashset of all of the direct and indirect specs-preds for the given predicate in the
   * given microtheory.
   * 
   * @param cycFort the predicate
   * @param mt the microtheory
   * 
   * @return the hashset of all of the direct and indirect spec-preds for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecPredsHashSet(CycFort cycFort, 
                                        CycObject mt)
                                 throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecPreds(cycFort, 
                                       mt));
  }

  /**
   * Gets the list of all of the direct and indirect specs-inverses for the given predicate in all
   * microtheories.
   * 
   * @param cycFort the predicate
   * 
   * @return the list of all of the direct and indirect spec-inverses for the given predicate in
   *         all microtheories.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecInverses(CycFort cycFort)
                             throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (all-spec-inverses " + 
                        cycFort.stringApiValue() + ")))");
  }

  /**
   * Gets the list of all of the direct and indirect specs-inverses for the given predicate in the
   * given microtheory.
   * 
   * @param cycFort the predicate
   * @param mt the microtheory
   * 
   * @return the list of all of the direct and indirect spec-inverses for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecInverses(CycFort cycFort, 
                                    CycObject mt)
                             throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-spec-inverses " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the hashset of all of the direct and indirect specs-inverses for the given predicate in
   * all microtheories.
   * 
   * @param cycFort the predicate
   * 
   * @return the hashset of all of the direct and indirect spec-inverses for the given predicate in
   *         all microtheories.
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecInversesHashSet(CycFort cycFort)
                                    throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecInverses(cycFort));
  }

  /**
   * Gets the list of all of the direct and indirect specs-mts for the given microtheory in mt-mt
   * (currently #$UniversalVocabularyMt).
   * 
   * @param mt the microtheory
   * 
   * @return the list of all of the direct and indirect specs-mts for the given microtheory in
   *         mt-mt (currently #$UniversalVocabularyMt)
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getAllSpecMts(CycObject mt)
                        throws IOException, UnknownHostException, CycApiException {
    return converseList("(all-spec-mts " + makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the hashset of all of the direct and indirect specs-inverses for the given predicate in
   * the given microtheory.
   * 
   * @param cycFort the predicate
   * @param mt the microtheory
   * 
   * @return the hashset of all of the direct and indirect spec-inverses for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecInversesHashSet(CycFort cycFort, 
                                           CycObject mt)
                                    throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecInverses(cycFort, 
                                          mt));
  }

  /**
   * Gets the hashset of all of the direct and indirect specs-mts for the given microtheory in
   * mt-mt (currently #$UniversalVocabularyMt).
   * 
   * @param mt the microtheory
   * 
   * @return the hashset of all of the direct and indirect specs-mts for the given microtheory in
   *         mt-mt (currently #$UniversalVocabularyMt)
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public HashSet getAllSpecMtsHashSet(CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    return new HashSet(getAllSpecMts(mt));
  }

  /**
   * Gets a list of the arg1Isas for a CycConstant predicate.
   * 
   * @param predicate the predicate for which argument 1 contraints are sought.
   * 
   * @return the list of the arg1Isas for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg1Isas(CycObject predicate)
                      throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert predicate instanceof CycConstant || 
           predicate instanceof CycNart || 
           predicate instanceof CycList : predicate.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(remove-duplicates (with-all-mts (arg1-isa " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the arg1Isas for a CycConstant predicate given an mt.
   * 
   * @param predicate the predicate for which argument 1 contraints are sought.
   * @param mt the relevant microtheory
   * 
   * @return the list of the arg1Isas for a CycConstant predicate given an mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg1Isas(CycFort predicate, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg1-isa " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the arg2Isas for a CycConstant predicate.
   * 
   * @param predicate the predicate for which argument 2 contraints are sought.
   * 
   * @return the list of the arg1Isas for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg2Isas(CycObject predicate)
                      throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert predicate instanceof CycConstant || 
           predicate instanceof CycNart || 
           predicate instanceof CycList : predicate.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(remove-duplicates (with-all-mts (arg2-isa " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the arg2Isas for a CycConstant predicate given an mt.
   * 
   * @param predicate the predicate for which argument 2 contraints are sought.
   * @param mt the relevant microtheory
   * 
   * @return the list of the arg2Isas for a CycConstant predicate given an mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg2Isas(CycFort predicate, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg2-isa " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the arg3Isas for a CycConstant predicate.
   * 
   * @param predicate the predicate for which argument 3 contraints are sought.
   * 
   * @return the list of the arg1Isas for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg3Isas(CycFort predicate)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (arg3-isa " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the arg3Isas for a CycConstant predicate given an mt.
   * 
   * @param predicate the predicate for which argument 3 contraints are sought.
   * @param mt the relevant microtheory
   * 
   * @return the list of the arg1Isas for a CycConstant predicate given an mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg3Isas(CycFort predicate, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg3-isa " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the arg4Isas for a CycConstant predicate.
   * 
   * @param predicate the predicate for which argument 4 contraints are sought.
   * 
   * @return the list of the arg4Isas for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg4Isas(CycFort predicate)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (arg4-isa " + 
                        predicate.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the arg4Isas for a CycConstant predicate given an mt.
   * 
   * @param predicate the predicate for which argument 4 contraints are sought.
   * @param mt the relevant microtheory
   * 
   * @return the list of the arg4Isas for a CycConstant predicate given an mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg4Isas(CycFort predicate, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg4-isa " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the argNIsas for a CycConstant predicate.
   * 
   * @param predicate the predicate for which argument N contraints are sought.
   * @param argPosition the argument position of argument N
   * 
   * @return the list of the argNIsas for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArgNIsas(CycFort predicate, 
                             int argPosition)
                      throws IOException, UnknownHostException, CycApiException {
    String command = "(remove-duplicates \n" + "  (with-all-mts \n" + "    (argn-isa " + 
                     predicate.stringApiValue() + " " + Integer.toString(
                                                              argPosition) + ")))";

    return converseList(command);
  }

  /**
   * Gets the list of the argNIsas for a CycConstant predicate given an mt.
   * 
   * @param predicate the predicate for which argument contraints are sought.
   * @param argPosition the argument position of argument N
   * @param mt the relevant microtheory
   * 
   * @return the list of the arg1Isas for a CycConstant predicate given an mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArgNIsas(CycFort predicate, 
                             int argPosition, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    String command = "(remove-duplicates \n" + "  (with-all-mts \n" + "    (argn-isa \n" + 
                     "      " + predicate.stringApiValue() + "      " + 
                     Integer.toString(argPosition) + "      " + makeELMt(
                                                                      mt).stringApiValue() + 
                     ")))";

    return converseList(command);
  }

  /**
   * Gets the list of the interArgIsa1-2 isa constraint pairs for the given predicate.  Each item
   * of the returned list is a pair (arg1-isa arg2-isa) which means that when (#$isa arg1
   * arg1-isa) holds, (#$isa arg2 arg2-isa) must also hold for (predicate arg1 arg2 ..) to be well
   * formed.
   * 
   * @param predicate the predicate for interArgIsa1-2 contraints are sought.
   * 
   * @return the list of the interArgIsa1-2 isa constraint pairs for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInterArgIsa1_2s(CycFort predicate)
                             throws IOException, UnknownHostException, CycApiException {
    String command = "(remove-duplicates \n" + "  (with-all-mts \n" + "    (inter-arg-isa1-2 " + 
                     predicate.stringApiValue() + ")))";

    return converseList(command);
  }

  /**
   * Gets the list of the interArgIsa1-2 isa constraint pairs for the given predicate.  Each item
   * of the returned list is a pair (arg1-isa arg2-isa) which means that when (#$isa arg1
   * arg1-isa) holds, (#$isa arg2 arg2-isa) must also hold for (predicate arg1 arg2 ..) to be well
   * formed.
   * 
   * @param predicate the predicate for interArgIsa1-2 contraints are sought.
   * @param mt the relevant inference microtheory
   * 
   * @return the list of the interArgIsa1-2 isa constraint pairs for the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInterArgIsa1_2s(CycFort predicate, 
                                    CycObject mt)
                             throws IOException, UnknownHostException, CycApiException {
    String command = "(remove-duplicates \n" + "  (with-all-mts \n" + "    (inter-arg-isa1-2 " + 
                     "      " + predicate.stringApiValue() + "      " + 
                     makeELMt(mt).stringApiValue() + ")))";

    return converseList(command);
  }

  /**
   * Gets the list of the interArgIsa1-2 isa constraints for arg2, given the predicate and arg1.
   * 
   * @param predicate the predicate for interArgIsa1-2 contraints are sought.
   * @param arg1 the argument in position 1
   * 
   * @return the list of the interArgIsa1-2 isa constraints for arg2, given the predicate and arg1
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInterArgIsa1_2_forArg2(CycFort predicate, 
                                           CycFort arg1)
                                    throws IOException, UnknownHostException, CycApiException {
    CycList result = new CycList();
    ListIterator constraintPairs = getInterArgIsa1_2s(predicate).listIterator();

    while (constraintPairs.hasNext()) {
      CycList pair = (CycList) constraintPairs.next();

      if (pair.first().equals(arg1)) {
        result.add(pair.second());
      }
    }

    return result;
  }

  /**
   * Gets the list of the interArgIsa1-2 isa constraints for arg2, given the predicate and arg1.
   * 
   * @param predicate the predicate for interArgIsa1-2 contraints are sought.
   * @param arg1 the argument in position 1
   * @param mt the relevant inference microtheory
   * 
   * @return the list of the interArgIsa1-2 isa constraints for arg2, given the predicate and arg1
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getInterArgIsa1_2_forArg2(CycFort predicate, 
                                           CycFort arg1, 
                                           CycObject mt)
                                    throws IOException, UnknownHostException, CycApiException {
    CycList result = new CycList();
    ListIterator constraintPairs = getInterArgIsa1_2s(predicate, 
                                                      mt).listIterator();

    while (constraintPairs.hasNext()) {
      CycList pair = (CycList) constraintPairs.next();

      if (pair.first().equals(arg1)) {
        result.add(pair.second());
      }
    }

    return result;
  }

  /**
   * Gets the list of the resultIsa for a CycConstant function.
   * 
   * @param function the given function term
   * 
   * @return the list of the resultIsa for a CycConstant function
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getResultIsas(CycFort function)
                        throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (result-isa " + 
                        function.stringApiValue() + ")))");
  }

  /**
   * Gets the list of the resultIsa for a CycConstant function.
   * 
   * @param function the given function term
   * @param mt the relevant mt
   * 
   * @return the list of the resultIsa for a CycConstant function
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getResultIsas(CycFort function, 
                               CycObject mt)
                        throws IOException, UnknownHostException, CycApiException {
    return converseList("(result-isa " + function.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets the list of the argNGenls for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * @param argPosition the argument position for which the genls argument constraints are sought
   *        (position 1 = first argument)
   * 
   * @return the list of the argNGenls for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArgNGenls(CycFort predicate, 
                              int argPosition)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(remove-duplicates (with-all-mts (argn-genl " + 
                        predicate.stringApiValue() + " " + argPosition + ")))");
  }

  /**
   * Gets the list of the argNGenls for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * @param argPosition the argument position for which the genls argument constraints are sought
   *        (position 1 = first argument)
   * @param mt the relevant mt
   * 
   * @return the list of the argNGenls for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArgNGenls(CycFort predicate, 
                              int argPosition, 
                              CycObject mt)
                       throws IOException, UnknownHostException, CycApiException {
    return converseList("(argn-genl " + predicate.stringApiValue() + " " + argPosition + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the arg1Formats for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * 
   * @return a list of the arg1Formats for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg1Formats(CycObject predicate)
                         throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert predicate instanceof CycConstant || 
           predicate instanceof CycNart || 
           predicate instanceof CycList : predicate.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(with-all-mts (arg1-format " + predicate.stringApiValue() + "))");
  }

  /**
   * Gets a list of the arg1Formats for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * @param mt the relevant mt
   * 
   * @return a list of the arg1Formats for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg1Formats(CycFort predicate, 
                                CycObject mt)
                         throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg1-format " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the arg2Formats for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * 
   * @return a list of the arg2Formats for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg2Formats(CycObject predicate)
                         throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert predicate instanceof CycConstant || 
           predicate instanceof CycNart || 
           predicate instanceof CycList : predicate.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(with-all-mts (arg2-format " + predicate.stringApiValue() + "))");
  }

  /**
   * Gets a list of the arg2Formats for a CycConstant predicate.
   * 
   * @param predicate the given predicate term
   * @param mt the relevant mt
   * 
   * @return a list of the arg2Formats for a CycConstant predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg2Formats(CycFort predicate, 
                                CycObject mt)
                         throws IOException, UnknownHostException, CycApiException {
    return converseList("(arg2-format " + predicate.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the disjointWiths for a CycFort.
   * 
   * @param cycObject the given collection term
   * 
   * @return a list of the disjointWiths for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getDisjointWiths(CycObject cycObject)
                           throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseList("(remove-duplicates (with-all-mts (local-disjoint-with " + 
                        cycObject.stringApiValue() + ")))");
  }

  /**
   * Gets a list of the disjointWiths for a CycFort.
   * 
   * @param cycFort the given collection term
   * @param mt the relevant mt
   * 
   * @return a list of the disjointWiths for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getDisjointWiths(CycFort cycFort, 
                                  CycObject mt)
                           throws IOException, UnknownHostException, CycApiException {
    return converseList("(local-disjoint-with " + cycFort.stringApiValue() + " " + 
                        makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Gets a list of the coExtensionals for a CycFort.  Limited to 120 seconds.
   * 
   * @param cycObject the given collection term
   * 
   * @return a list of the coExtensionals for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getCoExtensionals(CycObject cycObject)
                            throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    CycList answer = null;
    try {
      answer = converseList("(ask-template '?X '(#$coExtensional " + cycObject.stringApiValue() + 
                            " ?X) #$EverythingPSC nil nil 120)");
    }
     catch (IOException e) {
      Log.current.println("getCoExtensionals - ignoring:\n" + e.getMessage());

      return new CycList();
    }

    answer.remove(cycObject);

    return canonicalizeList(answer);
  }

  /**
   * Gets a list of the coExtensionals for a CycFort.  Limited to 120 seconds.
   * 
   * @param cycFort the given collection term
   * @param mt the relevant mt for inference
   * 
   * @return a list of the coExtensionals for a CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getCoExtensionals(CycFort cycFort, 
                                   CycObject mt)
                            throws IOException, UnknownHostException, CycApiException {
    CycList answer = null;

    try {
      answer = converseList("(ask-template '?X '(#$coExtensional " + cycFort.stringApiValue() + 
                            " ?X) " + makeELMt(mt).stringApiValue() + " nil nil 120)");
    }
     catch (IOException e) {
      Log.current.println("getCoExtensionals - ignoring:\n" + e.getMessage());

      return new CycList();
    }

    answer.remove(cycFort);

    return canonicalizeList(answer);
  }

  /**
   * Returns true if the given term is a microtheory.
   * 
   * @param cycFort the constant for determination as a microtheory
   * 
   * @return <tt>true</tt> iff cycConstant is a microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isMicrotheory(CycFort cycFort)
                        throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Microtheory)");
  }

  /**
   * Returns true if the given term is a Collection.
   * 
   * @param cycObject the given term
   * 
   * @return true if the given term is a Collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isCollection(CycObject cycObject)
                       throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseBoolean("(isa-in-any-mt? " + cycObject.stringApiValue() + " #$Collection)");
  }

  public boolean isCollection(Object term)
                       throws IOException, UnknownHostException, CycApiException {
    if (term instanceof CycObject)
      return isCollection((CycObject)term);
    else
      return false;
  }

  /**
   * Returns true if the given term is a collection, implemented by a cache to avoid asking the same
   * question twice from the KB.
   * 
   * @param cycObject the given term
   * 
   * @return true if the given term is a collection
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isCollection_Cached(CycObject cycObject)
                              throws IOException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    boolean answer;
    Boolean isCollection = (Boolean) isCollectionCache.getElement(
                                 cycObject);

    if (isCollection != null) {
      answer = isCollection.booleanValue();

      return answer;
    }

    answer = isCollection(cycObject);
    isCollectionCache.addElement(cycObject, 
                                 new Boolean(answer));

    return answer;
  }

  public boolean isCollection_Cached(Object term)
                              throws IOException, CycApiException {
    if (term instanceof CycObject)
      return isCollection_Cached((CycObject)term);
    else
      return false;
  }

  /**
   * Returns true if the given term is an Individual.
   * 
   * @param cycObject the given term
   * 
   * @return true if the given term is an Individual
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isIndividual(CycObject cycObject)
                       throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseBoolean("(isa-in-any-mt? " + cycObject.stringApiValue() + " #$Individual)");
  }

  /**
   * Returns true if the given is a Function.
   * 
   * @param cycFort the given term
   * 
   * @return true if the given is a Function
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isFunction(CycFort cycFort)
                     throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + 
                           " #$Function-Denotational)");
  }

  /**
   * Returns true if the given term is an evaluatable predicate.
   * 
   * @param predicate the given term
   * 
   * @return true if true if the given term is an evaluatable predicate, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isEvaluatablePredicate(CycFort predicate)
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
   * Returns true if cycObject is a Predicate.
   * 
   * @param cycObject the term for determination as a predicate
   * 
   * @return true if cycObject is a Predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isPredicate(CycObject cycObject)
                      throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseBoolean("(isa-in-any-mt? " + cycObject.stringApiValue() + " #$Predicate)");
  }

  /**
   * Returns true if the given term is a UnaryPredicate.
   * 
   * @param cycObject the given term
   * 
   * @return true if true if the given term is a UnaryPredicate, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isUnaryPredicate(CycObject cycObject)
                           throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseBoolean("(isa-in-any-mt? " + cycObject.stringApiValue() + 
                           " #$UnaryPredicate)");
  }

  /**
   * Returns true if the cyc object is a BinaryPredicate.
   * 
   * @param cycObject the given cyc object
   * 
   * @return true if cycObject is a BinaryPredicate, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isBinaryPredicate(CycObject cycObject)
                            throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    return converseBoolean("(isa-in-any-mt? " + cycObject.stringApiValue() + 
                           " #$BinaryPredicate)");
  }

  /**
   * Returns true if the candidate name uses valid CycConstant characters.
   * 
   * @param candidateName the candidate name
   * 
   * @return true if the candidate name uses valid CycConstant characters
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isValidConstantName(String candidateName)
                              throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(new-constant-name-spec-p \"" + candidateName + "\")");
  }

  /**
   * Returns true if the candidate name is an available CycConstant name, case insensitive.
   * 
   * @param candidateName the candidate name
   * 
   * @return true if the candidate name uses valid CycConstant characters
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isConstantNameAvailable(String candidateName)
                                  throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(constant-name-available \"" + candidateName + "\")");
  }

  /**
   * Returns true if term is a quotedCollection, in any microtheory
   * 
   * @param cycFort the given CycFort term
   * 
   * @return true if term is a quotedCollection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isQuotedCollection(CycFort cycFort)
                             throws IOException, UnknownHostException, CycApiException {
    return this.isQuotedCollection(cycFort, 
                                   inferencePSC);
  }

  /**
   * Returns true if term is a quotedCollection is a quotedCollection.
   * 
   * @param cycFort the given CycFort term
   * @param mt the microtheory in which the query is made
   * 
   * @return true if term is a quotedCollection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isQuotedCollection(CycFort cycFort, 
                                    CycObject mt)
                             throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(getKnownConstantByName("quotedCollection"));
    query.add(cycFort);

    return this.isQueryTrue(query, 
                            mt);
  }

  /**
   * Returns true if cycConstant is a PublicConstant.
   * 
   * @param cycConstant the given constant
   * 
   * @return true if cycConstant is a PublicConstant
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isPublicConstant(CycConstant cycConstant)
                           throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + 
                           " #$PublicConstant)");
  }

  /**
   * Gets a list of the public Cyc constants.
   * 
   * @return a list of the public Cyc constants
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getPublicConstants()
                             throws IOException, UnknownHostException, CycApiException {
    // #$PublicConstant
    return getKbSubset(getKnownConstantByGuid("bd7abd90-9c29-11b1-9dad-c379636f7270"));
  }

  /**
   * Gets a list of the elements of the given CycKBSubsetCollection.
   * 
   * @param cycKbSubsetCollection the given CycKBSubsetCollection
   * 
   * @return a list of the elements of the given CycKBSubsetCollection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getKbSubset(CycFort cycKbSubsetCollection)
                      throws IOException, UnknownHostException, CycApiException {
    CycList answer = converseList("(ask-template '?X '(#$isa ?X " + 
                                  cycKbSubsetCollection.stringApiValue() + ") #$EverythingPSC)");

    return canonicalizeList(answer);
  }

  /**
   * Renames the given constant.
   * 
   * @param cycConstant the constant term to be renamed
   * @param newName the new constant name
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void rename(final CycConstant cycConstant, final String newName)
                         throws IOException, UnknownHostException, CycApiException {
    String command = wrapBookkeeping("(ke-rename-now " + cycConstant.stringApiValue() + "\"" + newName + "\")");
    Object result = converseObject(command);
    if (result.equals(CycObjectFactory.nil))
      throw new CycApiException(newName + " is an invalid new name for " + cycConstant.cyclify());
    CycObjectFactory.removeCaches(cycConstant);
    cycConstant.setName(newName);
    cycConstant.getGuid();
    CycObjectFactory.addCycConstantCacheByGuid(cycConstant);
    CycObjectFactory.addCycConstantCacheByName(cycConstant);
    CycObjectFactory.addCycConstantCacheById(cycConstant);
  }

  /**
   * Kills a Cyc constant.  If CYCCONSTANT is a microtheory, then all the contained assertions are
   * deleted from the KB, the Cyc Truth Maintenance System (TML) will automatically delete any
   * derived assertions whose sole support is the killed term(s).
   * 
   * @param cycConstant the constant term to be removed from the KB
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void kill(CycConstant cycConstant)
                         throws IOException, UnknownHostException, CycApiException {
    String command = wrapBookkeeping("(ke-kill-now " + cycConstant.stringApiValue() + ")");
    converseBoolean(command);
    CycObjectFactory.removeCaches(cycConstant);
  }

  /**
   * Kills a Cyc constant without issuing a transcript operation. If CYCCONSTANT is a microtheory,
   * then all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
   * (TMS) will automatically delete any derived assertions whose sole support is the killed
   * term(s).
   * 
   * @param cycConstant the constant term to be removed from the KB
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void killWithoutTranscript(CycConstant cycConstant)
                                          throws IOException, UnknownHostException, CycApiException {
    String command = wrapBookkeeping("(cyc-kill " + cycConstant.stringApiValue() + ")");
    converseBoolean(command);
    CycObjectFactory.removeCaches(cycConstant);
  }

  /**
   * Kills the given Cyc constants.  If CYCCONSTANT is a microtheory, then all the contained
   * assertions are deleted from the KB, the Cyc Truth Maintenance System (TMS) will automatically
   * delete any derived assertions whose sole support is the killed term(s).
   * 
   * @param cycConstants the list of constant terms to be removed from the KB
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void kill(CycConstant[] cycConstants)
                         throws IOException, UnknownHostException, CycApiException {
    for (int i = 0; i < cycConstants.length; i++)
      kill(cycConstants[i]);
  }

  /**
   * Kills the given Cyc constants.  If CYCCONSTANT is a microtheory, then all the contained
   * assertions are deleted from the KB, the Cyc Truth Maintenance System (TMS) will automatically
   * delete any derived assertions whose sole support is the killed term(s).
   * 
   * @param cycConstants the list of constant terms to be removed from the KB
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void kill(List cycConstants)
                         throws IOException, UnknownHostException, CycApiException {
    for (int i = 0; i < cycConstants.size(); i++)
      kill((CycConstant) cycConstants.get(i));
  }

  /**
   * Kills a Cyc NART (Non Atomic Reified Term).  If CYCFORT is a microtheory, then all the
   * contained assertions are deleted from the KB, the Cyc Truth Maintenance System (TMS) will
   * automatically delete any derived assertions whose sole support is the killed term(s).
   * 
   * @param cycFort the NART term to be removed from the KB
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public synchronized void kill(CycFort cycFort)
                         throws IOException, UnknownHostException, CycApiException {
    if (cycFort instanceof CycConstant) {
      kill((CycConstant) cycFort);
    }
    else {
      String command = wrapBookkeeping("(ke-kill-now " + cycFort.stringApiValue() + ")");
      converseBoolean(command);
    }
  }

  /**
   * Returns the value of the Cyclist.
   * 
   * @return the value of the Cyclist
   */
  public CycFort getCyclist() {
    return cyclist;
  }

  /**
   * Sets the value of the Cyclist, whose identity will be attached via #$myCreator bookkeeping
   * assertions to new KB entities created in this session.
   * 
   * @param cyclistName the name of the cyclist term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void setCyclist(String cyclistName)
                  throws IOException, UnknownHostException, CycApiException {
    if (!(cyclistName.startsWith("#$"))) {
      cyclistName = "#$" + cyclistName;
    }

    setCyclist((CycFort) getELCycTerm(cyclistName));
  }

  /**
   * Sets the value of the Cyclist, whose identity will be attached via #$myCreator bookkeeping
   * assertions to new KB entities created in this session.
   * 
   * @param cyclist the cyclist term
   */
  public void setCyclist(CycFort cyclist) {
    this.cyclist = cyclist;
  }

  /**
   * Returns the value of the project (KE purpose).
   * 
   * @return he value of the project (KE purpose)
   */
  public CycFort getKePurpose() {
    return project;
  }

  /**
   * Sets the value of the KE purpose, whose project name will be attached via #$myCreationPurpose
   * bookkeeping assertions to new KB entities created in this session.
   * 
   * @param projectName the string name of the KE Purpose term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void setKePurpose(String projectName)
                    throws IOException, UnknownHostException, CycApiException {
    if (!(projectName.startsWith("#$"))) {
      projectName = "#$" + projectName;
    }

    setKePurpose((CycFort) getELCycTerm(projectName));
  }

  /**
   * Sets the value of the KE purpose, whose project name will be attached via #$myCreationPurpose
   * bookkeeping assertions to new KB entities created in this session.
   * 
   * @param project the KE Purpose term
   */
  public void setKePurpose(CycFort project) {
    this.project = project;
  }

  /**
   * Asserts the given sentence, and then places it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithTranscript(CycList sentence, 
                                   CycObject mt)
                            throws IOException, UnknownHostException, CycApiException {
    assertWithTranscript(sentence.stringApiValue(), mt);
  }

  /**
   * Asserts the given sentence, and then places it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithTranscript(String sentence, 
                                   CycObject mt)
                            throws IOException, UnknownHostException, CycApiException {
    String projectName = "nil";

    if (project != null) {
      projectName = project.stringApiValue();
    }

    String cyclistName = "nil";

    if (cyclist != null) {
      cyclistName = cyclist.stringApiValue();
    }

    String command = "(clet ((*the-cyclist* " + cyclistName + ")\n" + 
                     "       (*ke-purpose* " + projectName + "))\n" + 
                     "  (ke-assert-now\n" + 
                     "    " + sentence + "\n" + 
                     "    " + makeELMt(mt).stringApiValue() + "))";
    boolean statusOk = converseBoolean(command);
    if (! statusOk)
      throw new CycApiException("Assertion failed in mt: " + makeELMt(mt).cyclify() + "\n" + sentence);
  }

  /**
   * Asserts the given sentence with bookkeeping, and then places it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithTranscriptAndBookkeeping(String sentence, CycObject mt)
                                          throws IOException, UnknownHostException, CycApiException {
    assertWithTranscriptAndBookkeeping(makeCycList(sentence), mt);
  }

  /**
   * Asserts the given sentence with bookkeeping, and then places it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithTranscriptAndBookkeeping(CycList sentence, CycObject mt)
      throws IOException, UnknownHostException, CycApiException {
    String projectName = "nil";
    if (project != null) {
      projectName = project.stringApiValue();
    }
    String cyclistName = "nil";
    if (cyclist != null) {
      cyclistName = cyclist.stringApiValue();
    }
    String command = "(with-bookkeeping-info \n" + "  (new-bookkeeping-info " + cyclistName + 
                     " (the-date) " + projectName + "(the-second))\n" + 
                     "  (clet ((*the-cyclist* " + cyclistName + ")\n" + 
                     "         (*ke-purpose* " + projectName + "))\n" + 
                     "    (ke-assert-now\n" + 
                     "      " + sentence.stringApiValue() + "\n" + 
                     "      " + makeELMt(mt).stringApiValue() + ")))";
    boolean statusOk = converseBoolean(command);
    if (! statusOk)
      throw new CycApiException("Assertion failed in mt: " + makeELMt(mt).cyclify() + "\n" + sentence + "\n" + command);
  }

  /**
   * Asserts the given sentence with bookkeeping and without placing it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithBookkeepingAndWithoutTranscript(CycList sentence, CycObject mt)
    throws IOException, UnknownHostException, CycApiException {
    assertWithBookkeepingAndWithoutTranscript(sentence.stringApiValue(), mt);
  }

  /**
   * Asserts the given sentence with bookkeeping and without placing it on the transcript queue.
   * 
   * @param sentence the given sentence for assertion
   * @param mt the microtheory in which the assertion is placed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertWithBookkeepingAndWithoutTranscript(String sentence, CycObject mt)
                                                 throws IOException, UnknownHostException, 
                                                        CycApiException {
    String projectName = "nil";

    if (project != null) {
      projectName = project.stringApiValue();
    }

    String cyclistName = "nil";

    if (cyclist != null) {
      cyclistName = cyclist.stringApiValue();
    }

    String command = "(with-bookkeeping-info \n" + "  (new-bookkeeping-info " + cyclistName + 
                     " (the-date) " + projectName + "(the-second))\n" + 
                     "  (clet ((*the-cyclist* " + cyclistName + ")\n" + 
                     "         (*ke-purpose* " + projectName + "))\n" + 
                     "    (cyc-assert\n" + 
                     "      " + sentence + "\n" + 
                     "      " + makeELMt(mt).stringApiValue() + ")))";
    boolean statusOk = converseBoolean(command);
    if (! statusOk)
      throw new CycApiException("Assertion failed in mt: " + makeELMt(mt).cyclify() + "\n" + sentence);
  }

  /**
   * Unasserts the given sentence with bookkeeping and without placing it on the transcript queue.
   * 
   * @param sentence the given sentence for unassertion
   * @param mt the microtheory from which the assertion is removed
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void unassertWithBookkeepingAndWithoutTranscript(CycList sentence, 
                                                          CycObject mt)
                                                   throws IOException, UnknownHostException, 
                                                          CycApiException {
    String command = wrapBookkeeping("(cyc-unassert " + sentence.stringApiValue() + 
                                     makeELMt(mt).stringApiValue() + ")");
    boolean unassertOk = converseBoolean(command);

    if (!unassertOk) {
      throw new CycApiException("Could not unassert from mt: " + makeELMt(
                                                                       mt) + "\n  " + 
                                sentence.cyclify());
    }
  }

  /**
   * Returns a with-bookkeeping-info macro expresssion.
   * 
   * @return a with-bookkeeping-info macro expresssion
   */
  protected String withBookkeepingInfo() {
    String projectName = "nil";

    if (project != null) {
      projectName = project.stringApiValue();
    }

    String cyclistName = "nil";

    if (cyclist != null) {
      cyclistName = cyclist.stringApiValue();
    }

    return "(with-bookkeeping-info (new-bookkeeping-info " + cyclistName + " (the-date) " + 
           projectName + "(the-second)) ";
  }

  /**
   * Finds a Cyc constant in the KB with the specified name
   * 
   * @param constantName the name of the new constant
   * 
   * @return the constant term or null if the argument name is null or if the term is not found
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant find(String constantName)
                   throws IOException, UnknownHostException, CycApiException {
    if (constantName == null) {
      return null;
    }

    return getConstantByName(constantName);
  }

  /**
   * Finds or creates a Cyc constant in the KB with the specified name.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param constantName the name of the new constant
   * 
   * @return the new constant term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant findOrCreate(String constantName)
                           throws IOException, UnknownHostException, CycApiException {
    return makeCycConstant(constantName);
  }

  /**
   * Creates a new permanent Cyc constant in the KB with the specified name.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param constantName the name of the new constant
   * 
   * @return the new constant term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant createNewPermanent(String constantName)
                                 throws IOException, UnknownHostException, CycApiException {
    return makeCycConstant(constantName);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycFort predicate, 
                        CycFort arg1, 
                        CycFort arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <CycFort>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate, which is a string
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycFort predicate, 
                        CycFort arg1, 
                        String arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <String>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate, which is a CycList
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycConstant predicate, 
                        CycFort arg1, 
                        CycList arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <List>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate, which is an int
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycConstant predicate, 
                        CycFort arg1, 
                        int arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <int>)
    assertGaf(mt, 
              predicate, 
              arg1, 
              new Integer(arg2));
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate, which is an Integer
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycFort predicate, 
                        CycFort arg1, 
                        Integer arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <int>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the binary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate, which is a Double
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycFort predicate, 
                        CycFort arg1, 
                        Double arg2)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <int>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT. The operation and its
   * bookkeeping info will be added to the KB transcript for replication and archive.
   * 
   * @param mt the microtheory in which the assertion is made
   * @param predicate the ternary predicate of the assertion
   * @param arg1 the first argument of the predicate
   * @param arg2 the second argument of the predicate
   * @param arg3 the third argument of the predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycObject mt, 
                        CycConstant predicate, 
                        CycFort arg1, 
                        CycFort arg2, 
                        CycFort arg3)
                 throws IOException, UnknownHostException, CycApiException {
    // (predicate <CycFort> <CycFort> <CycFort>)
    CycList sentence = new CycList();
    sentence.add(predicate);
    sentence.add(arg1);
    sentence.add(arg2);
    sentence.add(arg3);
    assertWithTranscriptAndBookkeeping(sentence, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param gaf the gaf in the form of a CycList
   * @param mt the microtheory in which the assertion is made
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGaf(CycList gaf, 
                        CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    assertWithTranscriptAndBookkeeping(gaf, 
                                       mt);
  }

  /**
   * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation is performed at the HL level
   * and does not perform wff-checking, nor forward inference, nor bookkeeping assertions, nor transcript recording.  The advantage of
   * this method is that it is fast.
   * 
   * @param gaf the gaf in the form of a CycList
   * @param mt the microtheory in which the assertion is made
   * @param strength the assertion strength (e.g. :default or :monotonic)
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertHLGaf(final CycList gaf, 
                         final CycObject mt,
                         final CycSymbol strength)
                 throws IOException, UnknownHostException, CycApiException {
    // (HL-ADD-ARGUMENT '(:ASSERTED-ARGUMENT <strength>) '(NIL ((<gaf>)) <mt> :FORWARD NIL)
    final CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("hl-add-argument"));
    final CycList command1 = new CycList();
    command1.add(CycObjectFactory.makeCycSymbol(":asserted-argument"));
    command1.add(strength);
    command.addQuoted(command1);
    final CycList command2 = new CycList();
    command2.add(CycObjectFactory.nil);
    final CycList command3 = new CycList();
    final CycList canonicalGaf = new CycList();
    final int gaf_size = gaf.size();
    for (int i = 0; i < gaf_size; i++) {
      final Object obj = gaf.get(i);
      if (obj instanceof CycNart)
        canonicalGaf.add(makeCycList(((CycNart) obj).stringApiValue()));
      else
        canonicalGaf.add(obj);
    }
    command3.add(canonicalGaf);
    command2.add(command3);
    command.addQuoted(command2);
    command.add(mt);
    command.add(CycObjectFactory.makeCycSymbol(":forward"));
    command.add(CycObjectFactory.nil);
    converseCycObject(command);
  }

  /**
   * Unasserts the given ground atomic formula (gaf) in the specified microtheory MT.
   * 
   * @param gaf the gaf in the form of a CycList
   * @param mt the microtheory in which the assertion is made
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void unassertGaf(CycList gaf, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    String command = wrapBookkeeping("(ke-unassert-now " + gaf.stringApiValue() + 
                                     makeELMt(mt).stringApiValue() + ")");
    converseVoid(command);
  }

  /**
   * Assert a nameString for the specified CycConstant in the specified lexical microtheory. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycConstantName the name of the given term
   * @param nameString the given name string for the term
   * @param mtName the name of the microtheory in which the name string is asserted
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertNameString(String cycConstantName, 
                               String nameString, 
                               String mtName)
                        throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(getKnownConstantByName(mtName)), 
              getKnownConstantByGuid("c0fdf7e8-9c29-11b1-9dad-c379636f7270"), 
              getKnownConstantByName(cycConstantName), 
              nameString);
  }

  /**
   * Assert a comment for the specified CycConstant in the specified microtheory MT.  The operation
   * will be added to the KB transcript for replication and archive.
   * 
   * @param cycConstantName the name of the given term
   * @param comment the comment string
   * @param mtName the name of the microtheory in which the comment is asserted
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertComment(String cycConstantName, 
                            String comment, 
                            String mtName)
                     throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(getKnownConstantByName(mtName)), 
              CycAccess.comment, 
              getKnownConstantByName(cycConstantName), 
              comment);
  }

  /**
   * Assert a comment for the specified CycFort in the specified microtheory. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given term
   * @param comment the comment string
   * @param mt the comment assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertComment(CycFort cycFort, 
                            String comment, 
                            CycObject mt)
                     throws IOException, UnknownHostException, CycApiException {
    ELMt elmt = makeELMt(mt);
    assertGaf(elmt, 
              CycAccess.comment, 
              cycFort, 
              comment);
  }

  /**
   * Assert a name string for the specified CycFort in the specified microtheory. The operation
   * will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given term
   * @param nameString the name string
   * @param mt the name string assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertNameString(CycFort cycFort, 
                               String nameString, 
                               CycObject mt)
                        throws IOException, UnknownHostException, CycApiException {
    ELMt elmt = makeELMt(mt);
    assertGaf(elmt, 
              this.getKnownConstantByGuid("c0fdf7e8-9c29-11b1-9dad-c379636f7270"), 
              cycFort, 
              nameString);
  }

  /**
   * Assert a paraphrase format for the specified CycFort in the #$EnglishParaphraseMt. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param relation the given term
   * @param genFormatString the genFormat string
   * @param genFormatList the genFormat argument substitution sequence
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenFormat(CycFort relation, 
                              String genFormatString, 
                              CycList genFormatList)
                       throws IOException, UnknownHostException, CycApiException {
    // (#$genFormat <relation> <genFormatString> <genFormatList>)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("beed06de-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(genFormatString);

    if (genFormatList.size() == 0) {
      sentence.add(CycObjectFactory.nil);
    }
    else {
      sentence.add(genFormatList);
    }

    assertGaf(sentence, 
              
    // #$EnglishParaphraseMt
    makeELMt(getKnownConstantByGuid("bda16220-9c29-11b1-9dad-c379636f7270")));
  }

  /**
   * Create a microtheory MT, with a comment, isa MT-TYPE and CycFort genlMts. An existing
   * microtheory with the same name is killed first, if it exists.
   * 
   * @param mtName the name of the microtheory term
   * @param comment the comment for the new microtheory
   * @param isaMt the type of the new microtheory
   * @param genlMts the list of more general microtheories
   * 
   * @return the new microtheory term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant createMicrotheory(String mtName, 
                                       String comment, 
                                       CycFort isaMt, 
                                       List genlMts)
                                throws IOException, UnknownHostException, CycApiException {
    CycConstant mt = getConstantByName(mtName);

    if (mt != null) {
      kill(mt);
    }

    mt = createNewPermanent(mtName);
    assertComment(mt, 
                  comment, 
                  baseKB);
    assertGaf(universalVocabularyMt, 
              isa, 
              mt, 
              isaMt);

    Iterator iterator = genlMts.iterator();

    while (true) {
      if (!iterator.hasNext()) {
        break;
      }

      CycFort aGenlMt = (CycFort) iterator.next();
      assertGaf(universalVocabularyMt, 
                genlMt, 
                mt, 
                aGenlMt);
    }

    return mt;
  }

  /**
   * Create a microtheory MT, with a comment, isa MT-TYPE and CycFort genlMts.
   * 
   * @param mt the microtheory term
   * @param comment the comment for the new microtheory
   * @param isaMt the type of the new microtheory
   * @param genlMts the list of more general microtheories
   * 
   * @return the new microtheory term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void createMicrotheory(CycFort mt, 
                                String comment, 
                                CycFort isaMt, 
                                List genlMts)
                                throws IOException, UnknownHostException, CycApiException {
    assertComment(mt, 
                  comment, 
                  baseKB);
    assertGaf(universalVocabularyMt, 
              isa, 
              mt, 
              isaMt);
    Iterator iterator = genlMts.iterator();
    while (true) {
      if (!iterator.hasNext()) {
        break;
      }
      final CycList gaf = new CycList(3);
      gaf.add(genlMt);
      gaf.add(mt);
      gaf.add(iterator.next());
      assertGaf(gaf, universalVocabularyMt);
    }
  }

  /**
   * Create a microtheory MT, with a comment, isa MT-TYPE and CycFort genlMts. An existing
   * microtheory with the same name is killed first, if it exists.
   * 
   * @param mtName the name of the microtheory term
   * @param comment the comment for the new microtheory
   * @param isaMtName the type (as a string) of the new microtheory
   * @param genlMts the list of more general microtheories (as strings)
   * 
   * @return the new microtheory term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant createMicrotheory(String mtName, 
                                       String comment, 
                                       String isaMtName, 
                                       List genlMts)
                                throws IOException, UnknownHostException, CycApiException {
    CycConstant mt = getConstantByName(mtName);

    if (mt != null) {
      kill(mt);
    }

    mt = createNewPermanent(mtName);
    assertComment(mt, 
                  comment, 
                  baseKB);
    assertIsa(mtName, 
              isaMtName);

    Iterator iterator = genlMts.iterator();

    while (true) {
      if (!iterator.hasNext()) {
        break;
      }

      String genlMtName = (String) iterator.next();
      assertGenlMt(mtName, 
                   genlMtName);
    }

    return mt;
  }

  /**
   * Create a microtheory system for a new mt.  Given a root mt name, create a theory ROOTMt,
   * create a vocabulary ROOTVocabMt, and a data ROOTDataMt.  Establish genlMt links for the
   * theory mt and data mt.  Assert that the theory mt is a genlMt of the
   * WorldLikeOursCollectorMt. Assert that the data mt is a genlMt of the collector
   * CurrentWorldDataMt.
   * 
   * @param mtRootName the root name of the microtheory system
   * @param comment the root comment of the microtheory system
   * @param genlMts the list of more general microtheories
   * 
   * @return an array of three elements consisting of the theory mt, vocabulary mt, and the data mt
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant[] createMicrotheorySystem(String mtRootName, 
                                               String comment, 
                                               List genlMts)
                                        throws IOException, UnknownHostException, CycApiException {
    //traceOn();
    CycConstant[] mts = { null, null, null };
    String theoryMtName = mtRootName + "Mt";
    String vocabMtName = mtRootName + "VocabMt";
    String vocabMtComment = "The #$VocabularyMicrotheory for #$" + theoryMtName;
    String dataMtName = mtRootName + "DataMt";
    String dataMtComment = "The #$DataMicrotheory for #$" + theoryMtName;
    CycConstant worldLikeOursMt = getKnownConstantByGuid(
                                        "bf4c781d-9c29-11b1-9dad-c379636f7270");
    CycConstant currentWorldDataMt = getKnownConstantByGuid(
                                           "bf192b1e-9c29-11b1-9dad-c379636f7270");
    CycConstant genlMt_Vocabulary = getKnownConstantByGuid(
                                          "c054a49e-9c29-11b1-9dad-c379636f7270");

    CycConstant theoryMicrotheory = getKnownConstantByGuid(
                                          "be5275a8-9c29-11b1-9dad-c379636f7270");
    CycConstant theoryMt = createMicrotheory(theoryMtName, 
                                             comment, 
                                             theoryMicrotheory, 
                                             genlMts);
    CycConstant vocabularyMicrotheory = getKnownConstantByGuid(
                                              "bda19dfd-9c29-11b1-9dad-c379636f7270");
    CycConstant vocabMt = createMicrotheory(vocabMtName, 
                                            vocabMtComment, 
                                            vocabularyMicrotheory, 
                                            new ArrayList());
    CycConstant dataMicrotheory = getKnownConstantByGuid(
                                        "be5275a8-9c29-11b1-9dad-c379636f7270");
    CycConstant dataMt = createMicrotheory(dataMtName, 
                                           dataMtComment, 
                                           dataMicrotheory, 
                                           new ArrayList());
    assertGaf(baseKB, 
              genlMt_Vocabulary, 
              theoryMt, 
              vocabMt);
    assertGaf(baseKB, 
              genlMt, 
              dataMt, 
              theoryMt);
    assertGaf(baseKB, 
              genlMt, 
              worldLikeOursMt, 
              theoryMt);
    assertGaf(baseKB, 
              genlMt, 
              currentWorldDataMt, 
              dataMt);
    mts[0] = theoryMt;
    mts[1] = vocabMt;
    mts[2] = dataMt;

    //traceOff();
    return mts;
  }

  /**
   * Assert that the specified CycConstant is a collection in the UniversalVocabularyMt. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given collection term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsaCollection(CycFort cycFort)
                           throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              isa, 
              cycFort, 
              collection);
  }

  /**
   * Assert that the specified CycConstant is a collection in the specified defining microtheory
   * MT. The operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given collection term
   * @param mt the assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsaCollection(CycFort cycFort, 
                                  CycObject mt)
                           throws IOException, UnknownHostException, CycApiException {
    ELMt elmt = makeELMt(mt);
    assertGaf(elmt, 
              isa, 
              cycFort, 
              collection);
  }

  /**
   * Assert that the genlsCollection is a genls of specCollection, in the specified defining
   * microtheory MT. The operation will be added to the KB transcript for replication and archive.
   * 
   * @param specCollectionName the name of the more specialized collection
   * @param genlsCollectionName the name of the more generalized collection
   * @param mtName the assertion microtheory name
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenls(String specCollectionName, 
                          String genlsCollectionName, 
                          String mtName)
                   throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(getKnownConstantByName(mtName)), 
              genls, 
              getKnownConstantByName(specCollectionName), 
              getKnownConstantByName(genlsCollectionName));
  }

  /**
   * Assert that the genlsCollection is a genls of specCollection, in the UniversalVocabularyMt The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param specCollectionName the name of the more specialized collection
   * @param genlsCollectionName the name of the more generalized collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenls(String specCollectionName, 
                          String genlsCollectionName)
                   throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              genls, 
              getKnownConstantByName(specCollectionName), 
              getKnownConstantByName(genlsCollectionName));
  }

  /**
   * Assert that the genlsCollection is a genls of specCollection, in the UniveralVocabularyMt. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param specCollection the more specialized collection
   * @param genlsCollection the more generalized collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenls(CycFort specCollection, 
                          CycFort genlsCollection)
                   throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              genls, 
              specCollection, 
              genlsCollection);
  }

  /**
   * Assert that the genlsCollection is a genls of specCollection, in the specified defining
   * microtheory MT. The operation will be added to the KB transcript for replication and archive.
   * 
   * @param specCollection the more specialized collection
   * @param genlsCollection the more generalized collection
   * @param mt the assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenls(CycFort specCollection, 
                          CycFort genlsCollection, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    ELMt elmt = makeELMt(mt);
    assertGaf(elmt, 
              genls, 
              specCollection, 
              genlsCollection);
  }

  /**
   * Assert that the more general predicate is a genlPreds of the more specialized predicate,
   * asserted in the UniversalVocabularyMt The operation will be added to the KB transcript for
   * replication and archive.
   * 
   * @param specPredName the name of the more specialized predicate
   * @param genlPredName the name of the more generalized predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenlPreds(String specPredName, 
                              String genlPredName)
                       throws IOException, UnknownHostException, CycApiException {
    CycConstant genlPreds = getKnownConstantByGuid("bd5b4951-9c29-11b1-9dad-c379636f7270");
    assertGaf(universalVocabularyMt, 
              genlPreds, 
              getKnownConstantByName(specPredName), 
              getKnownConstantByName(genlPredName));
  }

  /**
   * Assert that the more general predicate is a genlPreds of the more specialized predicate,
   * asserted in the UniversalVocabularyMt The operation will be added to the KB transcript for
   * replication and archive.
   * 
   * @param specPred the more specialized predicate
   * @param genlPred the more generalized predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenlPreds(CycFort specPred, 
                              CycFort genlPred)
                       throws IOException, UnknownHostException, CycApiException {
    CycConstant genlPreds = getKnownConstantByGuid("bd5b4951-9c29-11b1-9dad-c379636f7270");
    assertGaf(universalVocabularyMt, 
              genlPreds, 
              specPred, 
              genlPred);
  }

  /**
   * Assert that term1 is conceptually related to term2 in the specified microtheory. The operation
   * will be added to the KB transcript for replication and archive.
   * 
   * @param term1 the first symbol
   * @param term2 the second symbol
   * @param mt the microtheory in which the assertion is made
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertConceptuallyRelated(CycFort term1, 
                                        CycFort term2, 
                                        CycObject mt)
                                 throws IOException, UnknownHostException, CycApiException {
    CycConstant conceptuallyRelated = getKnownConstantByGuid(
                                            "bd58803e-9c29-11b1-9dad-c379636f7270");
    assertGaf(makeELMt(mt), 
              conceptuallyRelated, 
              term1, 
              term2);
  }

  /**
   * Assert that the more general micortheory is a genlMt of the more specialized microtheory,
   * asserted in the UniversalVocabularyMt The operation will be added to the KB transcript for
   * replication and archive.
   * 
   * @param specMtName the name of the more specialized microtheory
   * @param genlsMtName the name of the more generalized microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenlMt(String specMtName, 
                           String genlsMtName)
                    throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              genlMt, 
              getKnownConstantByName(specMtName), 
              getKnownConstantByName(genlsMtName));
  }

  /**
   * Assert that the more general micortheory is a genlMt of the more specialized microtheory,
   * asserted in the UniversalVocabularyMt The operation will be added to the KB transcript for
   * replication and archive.
   * 
   * @param specMt the more specialized microtheory
   * @param genlsMt the more generalized microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertGenlMt(CycFort specMt, 
                           CycFort genlsMt)
                    throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              this.genlMt, 
              specMt, 
              genlsMt);
  }

  /**
   * Assert that the cycFort is a collection in the UniversalVocabularyMt. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param cycFortName the collection element name
   * @param collectionName the collection name
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsa(String cycFortName, 
                        String collectionName)
                 throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              isa, 
              getKnownConstantByName(cycFortName), 
              getKnownConstantByName(collectionName));
  }

  /**
   * Assert that the cycFort is a collection, in the specified defining microtheory MT. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFortName the collection element name
   * @param collectionName the collection name
   * @param mtName the assertion microtheory name
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsa(String cycFortName, 
                        String collectionName, 
                        String mtName)
                 throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(getKnownConstantByName(mtName)), 
              isa, 
              getKnownConstantByName(cycFortName), 
              getKnownConstantByName(collectionName));
  }

  /**
   * Assert that the cycFort is a collection, in the specified defining microtheory MT. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the collection element
   * @param aCollection the collection
   * @param mt the assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsa(CycFort cycFort, 
                        CycFort aCollection, 
                        CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(mt), 
              isa, 
              cycFort, 
              aCollection);
  }

  /**
   * Assert that the cycFort term itself is a collection, in the given mt. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param cycFort the collection element
   * @param aCollection the collection
   * @param mt the assertion microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertQuotedIsa(CycFort cycFort, CycFort aCollection, CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    assertGaf(makeELMt(mt), 
              getKnownConstantByGuid("055544a2-4371-11d6-8000-00a0c9da2002"), 
              cycFort, 
              aCollection);
  }

  /**
   * Assert that the cycFort is a collection, in the UniversalVocabularyMt. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param cycFort the collection element
   * @param aCollection the collection
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsa(CycFort cycFort, 
                        CycFort aCollection)
                 throws IOException, UnknownHostException, CycApiException {
    assertGaf(universalVocabularyMt, 
              isa, 
              cycFort, 
              aCollection);
  }

  /**
   * Assert that the specified CycConstant is a #$BinaryPredicate in the specified defining
   * microtheory. The operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given term
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsaBinaryPredicate(CycFort cycFort)
                                throws IOException, UnknownHostException, CycApiException {
    assertIsa(cycFort, 
              binaryPredicate, 
              universalVocabularyMt);
  }

  /**
   * Assert that the specified CycConstant is a #$BinaryPredicate in the specified defining
   * microtheory. The operation will be added to the KB transcript for replication and archive.
   * 
   * @param cycFort the given term
   * @param mt the defining microtheory
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertIsaBinaryPredicate(CycFort cycFort, 
                                       CycObject mt)
                                throws IOException, UnknownHostException, CycApiException {
    assertIsa(cycFort, 
              binaryPredicate, 
              makeELMt(mt));
  }

  /**
   * Constructs a new CycList object by parsing a string.
   * 
   * @param string the string in CycL external (EL). For example: (#$isa #$Dog #$TameAnimal)
   * 
   * @return the new CycList object from parsing the given string
   * 
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList makeCycList(String string)
                      throws CycApiException {
    return (new CycListParser(this)).read(string);
  }

  /**
   * Constructs a new ELMt object by the given CycObject.
   * 
   * @param cycObject the given CycObject from which the ELMt is derived
   * 
   * @return the new ELMt object by the given CycObject
   * 
   * @throws IOException if a communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   * @throws IllegalArgumentException if the cycObject is not the correct type of thing for
   * making into an ELMt
   */
  public ELMt makeELMt(CycObject cycObject)
                throws IOException, CycApiException {
    ELMt result = null;

    if (cycObject instanceof ELMt) {
      result = (ELMt) cycObject;

      return result;
    }

    if (cycObject instanceof CycList) {
      // if NAUT, then cycObject remains a CycList, but if NART then
      // cycObject becomes a CycNart
      cycObject = canonicalizeHLMT((CycList) cycObject);
    }

    if (cycObject instanceof CycFort) {
      result = makeELMt( (CycFort)cycObject);
    }
    if (cycObject instanceof CycList) {
      // NAUT mts are represented by CycList objects
      result = ELMtCycList.makeELMtCycList((CycList) cycObject);
    }
    

    return result;
  }

  public ELMt makeELMt(CycFort cycObject) {
    ELMt result = null;
    if(cycObject instanceof CycConstant)
      result = ELMtConstant.makeELMtConstant((CycConstant)cycObject);
    else if(cycObject instanceof CycNart)
      result = ELMtNart.makeELMtNart((CycNart) cycObject);
    else {
      throw new IllegalArgumentException("CycObject: " + cycObject.cyclify() + 
                                         "is not a valid ELMt.");
    }
    return result;
  }
  
  /**
   * Constructs a new ELMt object by the given String.
   * 
   * @param elmtString the given CycObject from which the ELMt is derived
   * 
   * @return the new ELMt object by the given CycObject
   * 
   * @throws IOException if a communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public ELMt makeELMt(String elmtString)
                throws IOException, CycApiException {
    ELMt elmt = null;
    elmtString = elmtString.trim();

    if (elmtString.startsWith("(")) {
      CycList elmtCycList = makeCycList(elmtString);
      return makeELMt(canonicalizeHLMT(elmtCycList));
    }
    else {
      return makeELMt(getKnownConstantByName(elmtString));
    }
  }

  /**
   * Returns the canonical Heuristic Level Microtheory (HLMT) given a list  representation, which
   * can be either a Non Atomic Reified Term (NART), or a Non Atomic Un-reified Term (NAUT).  In
   * the case of NART, a CycNart object is returned, otherwise a CycList is returned.
   * 
   * @param cycList the given CycList NART/NAUT representation
   * 
   * @return the canonical Heuristic Level Microtheory (HLMT) given a list  representation, which
   *         can be either a Non Atomic Reified Term (NART), or a Non Atomic Un-reified Term
   *         (NAUT)
   * 
   * @throws IOException if a communication error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycObject canonicalizeHLMT(CycList cycList)
                             throws IOException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("canonicalize-hlmt"));
    command.addQuoted(cycList);

    return converseCycObject(command);
  }

  /**
   * Wraps the given api command string with the binding environment for bookkeeping assertions.
   * 
   * @param command the given command string
   * 
   * @return the given api command string with the binding environment for bookkeeping assertions
   */
  public String wrapBookkeeping(String command) {
    String projectName = "nil";

    if (project != null) {
      projectName = project.stringApiValue();
    }

    String cyclistName = "nil";

    if (cyclist != null) {
      cyclistName = cyclist.stringApiValue();
    }

    String wrappedCommand = "(with-bookkeeping-info (new-bookkeeping-info " + cyclistName + 
                            " (the-date) " + projectName + " (the-second))\n" + 
                            "  (clet ((*require-case-insensitive-name-uniqueness* nil)\n" + 
                            "         (*the-cyclist* " + cyclistName + ")\n" + 
                            "         (*ke-purpose* " + projectName + "))\n" + "    " + command + 
                            "))";

    return wrappedCommand;
  }

  /**
   * Returns a new <tt>CycConstant</tt> object using the constant name, recording bookkeeping
   * information and archiving to the Cyc transcript.
   * 
   * @param name Name of the constant. If prefixed with "#$", then the prefix is removed for
   *        canonical representation.
   * 
   * @return a new <tt>CycConstant</tt> object using the constant name, recording bookkeeping
   *         information and archiving to the Cyc transcript
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant makeCycConstant(String name)
                              throws UnknownHostException, IOException, CycApiException {
    String constantName = name;

    if (constantName.startsWith("#$")) {
      constantName = constantName.substring(2);
    }

    CycConstant cycConstant = this.getConstantByName(name);

    if (cycConstant != null) {
      return cycConstant;
    }

    String command = wrapBookkeeping("(ke-create-now \"" + constantName + "\")");
    Object object = converseObject(command);

    if (object instanceof CycConstant) {
      cycConstant = (CycConstant) object;
    }
    else {
      throw new CycApiException("Cannot create new constant for " + name);
    }

    cycConstant.getName();
    cycConstant.getGuid();
    CycObjectFactory.addCycConstantCacheByGuid(cycConstant);
    CycObjectFactory.addCycConstantCacheByName(cycConstant);
    CycObjectFactory.addCycConstantCacheById(cycConstant);

    return cycConstant;
  }

  /**
   * Returns a new unique <tt>CycConstant</tt> object using the constant start name prefixed by
   * TMP-, recording bookkeeping information and but without archiving to the Cyc transcript.  If
   * the start name begins with #$ that portion of the start name is ignored.
   * 
   * @param startName the starting name of the constant which will be made unique using a suffix.
   * 
   * @return a new <tt>CycConstant</tt> object using the constant starting name, recording
   *         bookkeeping information and archiving to the Cyc transcript
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant makeUniqueCycConstant(final String startName)
                                    throws UnknownHostException, IOException, CycApiException {
    String constantName = startName;

    if (constantName.startsWith("#$"))
      constantName = constantName.substring(2);
    String suffix = "";
    int suffixNum = 0;
    while (true ) {
      String command = "(constant-name-available \"" + startName + suffix + "\")";
      if (converseBoolean(command))
        break;
      if (suffix.length() == 0)
        suffixNum = ((int) (9 * Math.random())) + 1;
      else
        suffixNum = (suffixNum * 10) + ((int) (10 * Math.random()));
      suffix = String.valueOf(suffixNum);
    }
    return makeCycConstant(startName + suffix);
  }

  /**
   * Returns a new unique <tt>CycConstant</tt> object using the constant start name and prefix,
   * recording bookkeeping information and but without archiving to the Cyc transcript. If the
   * start name begins with #$ that portion of the start name is ignored.
   * 
   * @param startName the starting name of the constant which will be made unique using a suffix.
   * @param prefix the prefix
   * 
   * @return a new <tt>CycConstant</tt> object using the constant starting name, recording
   *         bookkeeping information and archiving to the Cyc transcript
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant makeUniqueCycConstant(String startName, 
                                           String prefix)
                                    throws UnknownHostException, IOException, CycApiException {
    String constantName = startName;

    if (constantName.startsWith("#$")) {
      constantName = constantName.substring(2);
    }

    String command = wrapBookkeeping("(gentemp-constant \"" + constantName + "\" \"" + prefix + 
                                     "\")");
    CycConstant cycConstant = (CycConstant) converseObject(
                                    command);
    cycConstant.getName();
    cycConstant.getGuid();
    CycObjectFactory.addCycConstantCacheByGuid(cycConstant);
    CycObjectFactory.addCycConstantCacheByName(cycConstant);
    CycObjectFactory.addCycConstantCacheById(cycConstant);

    return cycConstant;
  }

  /**
   * Asks a Cyc query (new inference parameters) and returns the binding list.
   * 
   * @param query the query expression
   * @param mt the inference microtheory
   * @param queryProperties queryProperties the list of query property keywords and values
   * 
   * @return the binding list resulting from the given query
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList askNewCycQuery(CycList query, 
                             CycObject mt, 
                             HashMap queryProperties)
                      throws IOException, UnknownHostException, CycApiException {
    String queryPropertiesString = "";
    if (queryProperties != null) {
      CycList parameterList = new CycList();
      Iterator iter = queryProperties.entrySet().iterator();

      if (iter.hasNext()) {
        while (iter.hasNext()) {
          Entry mapEntry = (Entry) iter.next();
          CycSymbol queryParameterKeyword = (CycSymbol) mapEntry.getKey();
          parameterList.add(queryParameterKeyword);

          Object queryParameterValue = mapEntry.getValue();
          parameterList.add(queryParameterValue);
        }
        queryPropertiesString = parameterList.stringApiValue();
      }
    }
    final String script =
      "(new-cyc-query "+ query.stringApiValue() + " " + makeELMt(mt).stringApiValue() + " " + queryPropertiesString + ")";
    return converseList(script);
  }

  /**
   * Returns true if the  Cyc query (new inference parameters) is proven true.
   * 
   * @param query the query expression
   * @param mt the inference microtheory
   * @param queryProperties queryProperties the list of query property keywords and values
   * 
   * @return true if the  Cyc query (new inference parameters) is proven true.
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public boolean isQueryTrue(CycList query, 
                             CycObject mt, 
                             HashMap queryProperties)
                      throws IOException, UnknownHostException, CycApiException {
    String queryPropertiesString = "";
    if (queryProperties != null) {
      CycList parameterList = new CycList();
      Iterator iter = queryProperties.entrySet().iterator();

      if (iter.hasNext()) {
        while (iter.hasNext()) {
          Entry mapEntry = (Entry) iter.next();
          CycSymbol queryParameterKeyword = (CycSymbol) mapEntry.getKey();
          parameterList.add(queryParameterKeyword);

          Object queryParameterValue = mapEntry.getValue();
          parameterList.add(queryParameterValue);
        }
        queryPropertiesString = parameterList.stringApiValue();
      }
    }
    final String script =
      "(new-cyc-query "+ query.stringApiValue() + " " + makeELMt(mt).stringApiValue() + " " + queryPropertiesString + ")";
    return ! converseObject(script).equals(CycObjectFactory.nil);
  }

  /**
   * Asks a Cyc query (new inference parameters) and returns the binding list for the given variable.
   * 
   * @param variable the unbound variable for which bindings are sought
   * @param query the query expression
   * @param mt the inference microtheory
   * @param queryProperties queryProperties the list of query property keywords and values
   * 
   * @return the binding list resulting from the given query
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList queryVariable(final CycVariable queryVariable,
                               final CycList query, 
                               final CycObject mt, 
                               final HashMap queryProperties)
                      throws IOException, UnknownHostException, CycApiException {
    String queryPropertiesString = "";
    if (queryProperties != null) {
      CycList parameterList = new CycList();
      Iterator iter = queryProperties.entrySet().iterator();

      if (iter.hasNext()) {
        while (iter.hasNext()) {
          Entry mapEntry = (Entry) iter.next();
          CycSymbol queryParameterKeyword = (CycSymbol) mapEntry.getKey();
          parameterList.add(queryParameterKeyword);

          Object queryParameterValue = mapEntry.getValue();
          parameterList.add(queryParameterValue);
        }
        queryPropertiesString = parameterList.stringApiValue();
      }
    }
    final String script =
      "(query-variable "+ queryVariable.stringApiValue() + " " +
      query.stringApiValue() + " " + makeELMt(mt).stringApiValue() + " " + queryPropertiesString + ")";
    
    return converseList(script);
  }

  /**
   * Asks a Cyc query (new inference parameters) and returns the binding list for the given variable list.
   * 
   * @param variables the list of unbound variables for which bindings are sought
   * @param query the query expression
   * @param mt the inference microtheory
   * @param queryProperties queryProperties the list of query property keywords and values
   * 
   * @return the binding list resulting from the given query
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList queryVariables(final CycList queryVariables,
                                final CycList query, 
                                final CycObject mt, 
                                final HashMap queryProperties)
                      throws IOException, UnknownHostException, CycApiException {
    String queryPropertiesString = "";
    if (queryProperties != null) {
      CycList parameterList = new CycList();
      Iterator iter = queryProperties.entrySet().iterator();

      if (iter.hasNext()) {
        while (iter.hasNext()) {
          Entry mapEntry = (Entry) iter.next();
          CycSymbol queryParameterKeyword = (CycSymbol) mapEntry.getKey();
          parameterList.add(queryParameterKeyword);

          Object queryParameterValue = mapEntry.getValue();
          parameterList.add(queryParameterValue);
        }
        queryPropertiesString = parameterList.stringApiValue();
      }
    }
    final String script =
      "(query-template "+ queryVariables.stringApiValue() + " " +
      query.stringApiValue() + " " + makeELMt(mt).stringApiValue() + " " + queryPropertiesString + ")";
    
    return converseList(script);
  }

  /**
   * Asks a Cyc query and returns the binding list. Properties:
   * @deprecated use askNewCycQuery
   * @param query the query expression
   * @param mt the inference microtheory
   * @param maxTransformationDepth the Integer maximum transformation depth or nil for no limit
   * @param maxNumber the Integer maximum number of returned bindings or nil for no limit
   * @param maxTimeSeconds the Integer maximum number of seconds inference duration or nil for no
   *        limit
   * @param maxProofDepth the Integer maximum number of levels in the proof tree or nil for no
   *        limit
   * 
   * @return the binding list of answers for the given query and inference property settings
   * 
   * @throws IOException if a communication error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList askCycQuery(CycList query, 
                             CycObject mt, 
                             Object maxTransformationDepth, 
                             Object maxNumber, 
                             Object maxTimeSeconds, 
                             Object maxProofDepth)
                      throws IOException, UnknownHostException, CycApiException {
    HashMap queryProperties = new HashMap();
    queryProperties.put(CycObjectFactory.makeCycSymbol(
                              ":max-transformation-depth"), 
                        maxTransformationDepth);
    queryProperties.put(CycObjectFactory.makeCycSymbol(
                              ":max-number"), 
                        maxNumber);
    queryProperties.put(CycObjectFactory.makeCycSymbol(
                              ":max-time"), 
                        maxTimeSeconds);
    queryProperties.put(CycObjectFactory.makeCycSymbol(
                              ":max-proof-depth"), 
                        maxProofDepth);
    queryProperties.put(CycObjectFactory.makeCycSymbol(
                              ":forget-extra-results?"), 
                        CycObjectFactory.t);

    return askCycQuery(query, 
                       mt, 
                       queryProperties);
  }

  /**
   * Asks a Cyc query and returns the binding list.
   * @deprecated
   * @param query the query expression
   * @param mt the inference microtheory
   * @param queryProperties queryProperties the list of query property keywords and values
   * 
   * @return the binding list resulting from the given query
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList askCycQuery(CycList query, 
                             CycObject mt, 
                             HashMap queryProperties)
                      throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("cyc-query"));
    command.addQuoted(query);
    command.add(makeELMt(mt));

    CycList parameterList = new CycList();
    Iterator iter = queryProperties.entrySet().iterator();

    if (iter.hasNext()) {
      while (iter.hasNext()) {
        Entry mapEntry = (Entry) iter.next();
        CycSymbol queryParameterKeyword = (CycSymbol) mapEntry.getKey();
        parameterList.add(queryParameterKeyword);

        Object queryParameterValue = mapEntry.getValue();
        parameterList.add(queryParameterValue);
      }

      command.addQuoted(parameterList);
    }

    return converseList(command);
  }

  /**
   * Returns a list of bindings for a query with a single unbound variable.
   * 
   * @deprecated
   * @param query the query to be asked in the knowledge base
   * @param variable the single unbound variable in the query for which bindings are sought
   * @param mt the microtheory in which the query is asked
   * 
   * @return a list of bindings for the query
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList queryVariable(final CycList query, 
                               final CycVariable variable, 
                               final CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    StringBuffer queryBuffer = new StringBuffer();
    queryBuffer.append("(clet ((*cache-inference-results* nil) ");
    queryBuffer.append("       (*compute-inference-results* nil) ");
    queryBuffer.append("       (*unique-inference-result-bindings* t) ");
    queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
    queryBuffer.append("  (without-wff-semantics ");
    queryBuffer.append("    (ask-template " + variable.stringApiValue() + " ");
    queryBuffer.append("                  " + query.stringApiValue() + " ");
    queryBuffer.append("                  " + makeELMt(
                                                    mt).stringApiValue() + " ");
    queryBuffer.append("                  0 nil nil nil)))");

    CycList answer = converseList(queryBuffer.toString());

    return canonicalizeList(answer);
  }

  /**
   * Returns a list of bindings for a query with a single unbound variable.
   * 
   * @deprecated use queryVariable
   * @param query the query to be asked in the knowledge base
   * @param variable the single unbound variable in the query for which bindings are sought
   * @param mt the microtheory in which the query is asked
   * 
   * @return a list of bindings for the query
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList askWithVariable(CycList query, 
                                 CycVariable variable, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    StringBuffer queryBuffer = new StringBuffer();
    queryBuffer.append("(clet ((*cache-inference-results* nil) ");
    queryBuffer.append("       (*compute-inference-results* nil) ");
    queryBuffer.append("       (*unique-inference-result-bindings* t) ");
    queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
    queryBuffer.append("  (without-wff-semantics ");
    queryBuffer.append("    (ask-template " + variable.stringApiValue() + " ");
    queryBuffer.append("                  " + query.stringApiValue() + " ");
    queryBuffer.append("                  " + makeELMt(
                                                    mt).stringApiValue() + " ");
    queryBuffer.append("                  0 nil nil nil)))");

    CycList answer = converseList(queryBuffer.toString());

    return canonicalizeList(answer);
  }

  /**
   * Returns a list of bindings for a query with unbound variables.  The bindings each consist of a
   * list in the order of the unbound variables list parameter, in which each bound term is the
   * binding for the corresponding variable.
   * 
   * @deprecated use queryVariables
   * @param query the query to be asked in the knowledge base
   * @param variables the list of unbound variables in the query for which bindings are sought
   * @param mt the microtheory in which the query is asked
   * 
   * @return a list of bindings for the query
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList askWithVariables(CycList query, 
                                  List variables, 
                                  CycObject mt)
                           throws IOException, UnknownHostException, CycApiException {
    StringBuffer queryBuffer = new StringBuffer();
    queryBuffer.append("(clet ((*cache-inference-results* nil) ");
    queryBuffer.append("       (*compute-inference-results* nil) ");
    queryBuffer.append("       (*unique-inference-result-bindings* t) ");
    queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
    queryBuffer.append("  (without-wff-semantics ");
    queryBuffer.append("    (ask-template " + (new CycList(variables)).stringApiValue() + " ");
    queryBuffer.append("                  " + query.stringApiValue() + " ");
    queryBuffer.append("                  " + mt.stringApiValue() + " ");
    queryBuffer.append("                  0 nil nil nil)))");

    CycList bindings = converseList(queryBuffer.toString());
    CycList canonicalBindings = new CycList();
    Iterator iter = bindings.iterator();

    while (iter.hasNext())
      canonicalBindings.add(this.canonicalizeList((CycList) iter.next()));

    return canonicalBindings;
  }

  /**
   * Returns <tt>true</tt> iff the query is true in the knowledge base.
   * 
   * @deprecated
   * @param query the query to be asked in the knowledge base
   * @param mt the microtheory in which the query is asked
   * 
   * @return <tt>true</tt> iff the query is true in the knowledge base
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isQueryTrue(CycList query, 
                             CycObject mt)
                      throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("cyc-query"));

    CycList command1 = new CycList();
    command.add(command1);
    command1.add(CycObjectFactory.quote);
    command1.add(canonicalizeList(query));
    command.add(makeELMt(mt));

    CycList response = converseList(command);

    return response.size() > 0;
  }

  /**
   * Returns <tt>true</tt> iff the query is true in the knowledge base, implements a cache to avoid
   * asking the same question twice from the KB.
   * 
   * @deprecated
   * @param query the query to be asked in the knowledge base
   * @param mt the microtheory in which the query is asked
   * 
   * @return <tt>true</tt> iff the query is true in the knowledge base
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isQueryTrue_Cached(CycList query, 
                                    CycObject mt)
                             throws IOException, CycApiException {
    boolean answer;
    Boolean isQueryTrue = (Boolean) askCache.getElement(
                                query);

    if (isQueryTrue != null) {
      answer = isQueryTrue.booleanValue();

      return answer;
    }

    answer = isQueryTrue(query, 
                         makeELMt(mt));
    askCache.addElement(query, 
                        new Boolean(answer));

    return answer;
  }

  /**
   * Returns the count of the instances of the given collection.
   * 
   * @param collection the collection whose instances are counted
   * @param mt microtheory (including its genlMts) in which the count is determined
   * 
   * @return the count of the instances of the given collection
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int countAllInstances(CycFort collection, 
                               CycObject mt)
                        throws IOException, CycApiException {
    return this.converseInt("(count-all-instances " + collection.stringApiValue() + " " + 
                            makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Returns the count of the instances of the given collection, implements a cache to avoid asking
   * the same question twice from the KB.
   * 
   * @param collection the collection whose instances are counted
   * @param mt microtheory (including its genlMts) in which the count is determined
   * 
   * @return the count of the instances of the given collection
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int countAllInstances_Cached(CycFort collection, 
                                      CycObject mt)
                               throws IOException, CycApiException {
    int answer;
    Integer countAllInstances = (Integer) countAllInstancesCache.getElement(
                                      collection);

    if (countAllInstances != null) {
      answer = countAllInstances.intValue();

      return answer;
    }

    answer = countAllInstances(collection, 
                               makeELMt(mt));
    countAllInstancesCache.addElement(collection, 
                                      new Integer(answer));

    return answer;
  }

  /**
   * Gets a list of the backchaining implication rules which might apply to the given rule.
   * 
   * @param predicate the predicate for which backward chaining implication rules are sought
   * @param formula the literal for which backward chaining implication rules are sought
   * @param mt the microtheory (and its genlMts) in which the search for backchaining implication
   *        rules takes place
   * 
   * @return a list of the backchaining implication rules which might apply to the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getBackchainImplicationRules(CycConstant predicate, 
                                              CycList formula, 
                                              CycObject mt)
                                       throws IOException, UnknownHostException, CycApiException {
    StringBuffer command = new StringBuffer();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
      command.append("(clet (backchain-rules formula) ");
      command.append("  (with-all-mts ");
      command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + 
                     " :pos nil :backward) ");
      command.append("       (csetq formula (assertion-el-formula rule)) ");
      command.append("       (pwhen (cand (eq (first formula) #$implies) ");
      command.append("                    (unify-el-possible " + formula.stringApiValue() + " ");
      command.append("                                          (third formula))) ");
      command.append("         (cpush formula backchain-rules)))) ");
      command.append("   backchain-rules)");
    }
    else {
      command.append("(clet (backchain-rules formula) ");
      command.append("  (with-mt " + makeELMt(mt).stringApiValue() + " ");
      command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + 
                     " :pos nil :backward) ");
      command.append("       (csetq formula (assertion-el-formula rule)) ");
      command.append("       (pwhen (cand (eq (first formula) #$implies) ");
      command.append("                    (unify-el-possible " + formula.stringApiValue() + " ");
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
   * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules
   *        takes place
   * 
   * @return a list of the forward chaining implication rules which might apply to the given
   *         predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getForwardChainRules(CycConstant predicate, 
                                      CycList formula, 
                                      CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    StringBuffer command = new StringBuffer();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
      command.append("(clet (backchain-rules formula) ");
      command.append("  (with-all-mts ");
      command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + 
                     " :pos nil :forward) ");
      command.append("       (csetq formula (assertion-el-formula rule)) ");
      command.append("       (pwhen (cand (eq (first formula) #$implies) ");
      command.append("                    (unify-el-possible " + formula.stringApiValue() + " ");
      command.append("                                          (third formula))) ");
      command.append("         (cpush formula backchain-rules)))) ");
      command.append("   backchain-rules)");
    }
    else {
      command.append("(clet (backchain-rules formula) ");
      command.append("  (with-mt " + makeELMt(mt).stringApiValue() + " ");
      command.append("    (do-predicate-rule-index (rule " + predicate.stringApiValue() + 
                     " :pos nil :forward) ");
      command.append("       (csetq formula (assertion-el-formula rule)) ");
      command.append("       (pwhen (cand (eq (first formula) #$implies) ");
      command.append("                    (unify-el-possible " + formula.stringApiValue() + " ");
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
   * @param mt the microtheory (and its genlMts) in which the search for backchaining rules takes
   *        place
   * 
   * @return a list of the backchaining implication rules which might apply to the given predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getBackchainRules(CycConstant predicate, 
                                   CycObject mt)
                            throws IOException, UnknownHostException, CycApiException {
    StringBuffer command = new StringBuffer();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
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
      command.append("  (with-mt " + makeELMt(mt).stringApiValue() + " ");
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
   * Gets a list of the forward chaining implication rules which might apply to the given
   * predicate.
   * 
   * @param predicate the predicate for which forward chaining rules are sought
   * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules
   *        takes place
   * 
   * @return a list of the forward chaining implication rules which might apply to the given
   *         predicate
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getForwardChainRules(CycConstant predicate, 
                                      CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    StringBuffer command = new StringBuffer();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
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
      command.append("  (with-mt " + makeELMt(mt).stringApiValue() + " ");
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
   * @param cycSymbol the KB symbol which will have a value bound
   * 
   * @return the value assigned to the symbol
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Object getSymbolValue(CycSymbol cycSymbol)
                        throws IOException, UnknownHostException, CycApiException {
    return converseObject("(symbol-value " + cycSymbol.stringApiValue() + ")");
  }

  /**
   * Sets a KB symbol to have the specified value.  This is intended mainly for test case setup. If
   * the symbol does not exist at the KB, then it will be created and assigned the value.
   * 
   * @param cycSymbol the KB symbol which will have a value bound
   * @param value the value assigned to the symbol
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void setSymbolValue(CycSymbol cycSymbol, 
                             Object value)
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
   * 
   * @return true iff cycList represents a well formed formula
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isWellFormedFormula(CycList cycList)
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
   * @param predicate the <tt>CycConstant</tt> predicate for which backchaining required status is
   *        sought
   * @param mt microtheory (including its genlMts) in which the backchaining required status is
   *        sought
   * 
   * @return <tt>true</tt> iff backchain inference on the given predicate is required
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isBackchainRequired(CycConstant predicate, 
                                     CycObject mt)
                              throws IOException, CycApiException {
    CycConstant backchainRequired = getKnownConstantByGuid(
                                          "beaa3d29-9c29-11b1-9dad-c379636f7270");

    return hasSomePredicateUsingTerm(backchainRequired, 
                                     predicate, 
                                     new Integer(1), 
                                     makeELMt(mt));
  }

  /**
   * Returns <tt>true</tt> iff backchain inference on the given predicate is encouraged.
   * 
   * @param predicate the <tt>CycConstant</tt> predicate for which backchaining encouraged status
   *        is sought
   * @param mt microtheory (including its genlMts) in which the backchaining encouraged status is
   *        sought
   * 
   * @return <tt>true</tt> iff backchain inference on the given predicate is encouraged
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isBackchainEncouraged(CycConstant predicate, 
                                       CycObject mt)
                                throws IOException, CycApiException {
    CycConstant backchainEncouraged = getKnownConstantByGuid(
                                            "c09d1cea-9c29-11b1-9dad-c379636f7270");

    return hasSomePredicateUsingTerm(backchainEncouraged, 
                                     predicate, 
                                     new Integer(1), 
                                     makeELMt(mt));
  }

  /**
   * Returns <tt>true</tt> iff backchain inference on the given predicate is discouraged.
   * 
   * @param predicate the <tt>CycConstant</tt> predicate for which backchaining discouraged status
   *        is sought
   * @param mt microtheory (including its genlMts) in which the backchaining discouraged status is
   *        sought
   * 
   * @return <tt>true</tt> iff backchain inference on the given predicate is discouraged
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isBackchainDiscouraged(CycConstant predicate, 
                                        CycObject mt)
                                 throws IOException, CycApiException {
    CycConstant backchainDiscouraged = getKnownConstantByGuid(
                                             "bfcbce14-9c29-11b1-9dad-c379636f7270");

    return hasSomePredicateUsingTerm(backchainDiscouraged, 
                                     predicate, 
                                     new Integer(1), 
                                     makeELMt(mt));
  }

  /**
   * Returns <tt>true</tt> iff backchain inference on the given predicate is forbidden.
   * 
   * @param predicate the <tt>CycConstant</tt> predicate for which backchaining forbidden status is
   *        sought
   * @param mt microtheory (including its genlMts) in which the backchaining forbidden status is
   *        sought
   * 
   * @return <tt>true</tt> iff backchain inference on the given predicate is forbidden
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isBackchainForbidden(CycConstant predicate, 
                                      CycObject mt)
                               throws IOException, CycApiException {
    CycConstant backchainForbidden = getKnownConstantByGuid(
                                           "bfa4e9d2-9c29-11b1-9dad-c379636f7270");

    return hasSomePredicateUsingTerm(backchainForbidden, 
                                     predicate, 
                                     new Integer(1), 
                                     makeELMt(mt));
  }

  /**
   * Returns <tt>true</tt> iff the predicate has the irreflexive property: (#$isa ?PRED
   * #$IrreflexsiveBinaryPredicate).
   * 
   * @param predicate the <tt>CycConstant</tt> predicate for which irreflexive status is sought
   * @param mt microtheory (including its genlMts) in which the irreflexive status is sought
   * 
   * @return <tt>true</tt> iff the predicate has the irreflexive property: (#$isa ?PRED
   *         #$IrreflexsiveBinaryPredicate)
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isIrreflexivePredicate(CycConstant predicate, 
                                        CycObject mt)
                                 throws IOException, CycApiException {
    CycConstant irreflexiveBinaryPredicate = getKnownConstantByGuid(
                                                   "bd654be7-9c29-11b1-9dad-c379636f7270");

    return this.isa(predicate, 
                    irreflexiveBinaryPredicate, 
                    makeELMt(mt));
  }

  /**
   * Returns <tt>true</tt> iff any ground formula instances exist having the given predicate, and
   * the given term in the given argument position.
   * 
   * @param term the term present at the given argument position
   * @param predicate the <tt>CycConstant</tt> predicate for the formula
   * @param argumentPosition the argument position of the given term in the ground formula
   * @param mt microtheory (including its genlMts) in which the existence is sought
   * 
   * @return <tt>true</tt> iff any ground formula instances exist having the given predicate, and
   *         the given term in the given argument position
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean hasSomePredicateUsingTerm(CycConstant predicate, 
                                           CycFort term, 
                                           Integer argumentPosition, 
                                           CycObject mt)
                                    throws IOException, CycApiException {
    CycList command = new CycList();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
      command.add(CycObjectFactory.makeCycSymbol("some-pred-value-in-any-mt"));
      command.add(term.cycListApiValue());
      command.add(predicate.cycListApiValue());
    }
    else {
      command.add(CycObjectFactory.makeCycSymbol("some-pred-value-in-relevant-mts"));
      command.add(term.cycListApiValue());
      command.add(predicate.cycListApiValue());
      command.add(makeELMt(mt).cycListApiValue());
    }

    command.add(argumentPosition);

    //this.traceOn();
    return converseBoolean(command);
  }

  /**
   * Returns the count of the assertions indexed according to the given pattern, using the best
   * index (from among the predicate and argument indices).  The formula can contain variables.
   * 
   * @param formula the formula whose indexed instances are counted
   * @param mt microtheory (including its genlMts) in which the count is determined
   * 
   * @return the count of the assertions indexed according to the given pattern, using the best
   *         index (from among the predicate and argument indices)
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int countUsingBestIndex(CycList formula, 
                                 CycObject mt)
                          throws IOException, CycApiException {
    CycList command = new CycList();
    ELMt inferencePsc = makeELMt(getKnownConstantByGuid(
                                       "bd58915a-9c29-11b1-9dad-c379636f7270"));
    ELMt everythingPsc = makeELMt(getKnownConstantByGuid(
                                        "be7f041b-9c29-11b1-9dad-c379636f7270"));

    if (makeELMt(mt).equals(inferencePsc) || makeELMt(mt).equals(
                                                   everythingPsc)) {
      command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));
    }
    else {
      command.add(CycObjectFactory.makeCycSymbol("with-mt"));
      command.add(makeELMt(mt).cycListApiValue());
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
   * Imports a MUC (Message Understanding Conference) formatted symbolic expression into cyc via
   * the function which parses the expression and creates assertions for the contained concepts
   * and relations between them.
   * 
   * @param mucExpression the MUC (Message Understanding Conference) formatted symbolic expression
   * @param mtName the name of the microtheory in which the imported assertions will be made
   * 
   * @return the number of assertions imported from the input MUC expression
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int importMucExpression(CycList mucExpression, 
                                 String mtName)
                          throws IOException, CycApiException {
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
   * Returns a list of parsing expressions, each consisting of a parsing span expression, and a
   * list of parsed terms.
   * <pre>
   * (RKF-PHRASE-READER "penguins" #$RKFEnglishLexicalMicrotheoryPSC #$InferencePSC)
   * ==>
   * (((0) (#$Penguin #$PittsburghPenguins)))
   * </pre>
   * 
   * @param text the phrase to be parsed
   * @param parsingMt the microtheory in which lexical info is asked
   * @param domainMt the microtherory in which the info about candidate terms is asked
   * 
   * @return a parsing expression consisting of a parsing span expression, and a list of parsed
   *         terms
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList rkfPhraseReader(String text, 
                                 String parsingMt, 
                                 String domainMt)
                          throws IOException, CycApiException {
    return rkfPhraseReader(text, 
                           getKnownConstantByName(parsingMt), 
                           getKnownConstantByName(domainMt));
  }

  /**
   * Returns a list of parsing expressions, each consisting of a parsing span expression, and a
   * list of parsed terms.
   * <pre>
   * (RKF-PHRASE-READER "penguins" #$RKFEnglishLexicalMicrotheoryPSC #$InferencePSC)
   * ==>
   * (((0) (#$Penguin #$PittsburghPenguins)))
   * </pre>
   * 
   * @param text the phrase to be parsed
   * @param parsingMt the microtheory in which lexical info is asked
   * @param domainMt the microtherory in which the info about candidate terms is asked
   * 
   * @return a parsing expression consisting of a parsing span expression, and a list of parsed
   *         terms
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList rkfPhraseReader(String text, 
                                 CycFort parsingMt, 
                                 CycFort domainMt)
                          throws IOException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("rkf-phrase-reader"));
    command.add(text);
    command.add(parsingMt);
    command.add(domainMt);

    return converseList(command);
  }

  /**
   * Returns a list of disambiguation expressions, corresponding to each of the terms in the given
   * list of objects.
   * <pre>
   * (GENERATE-DISAMBIGUATION-PHRASES-AND-TYPES (QUOTE (#$Penguin #$PittsburghPenguins)))
   * ==>
   * ((#$Penguin "penguin" #$Bird "bird")
   *  (#$PittsburghPenguins "the Pittsburgh Penguins" #$IceHockeyTeam "ice hockey team"))
   * </pre>
   * 
   * @param objects the list of terms to be disambiguated
   * 
   * @return a list of disambiguation expressions, corresponding to each of the terms in the given
   *         list of objects
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList generateDisambiguationPhraseAndTypes(CycList objects)
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
   * 
   * @return the arity of the given predicate, or zero if the argument is not a predicate
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public int getArity(CycFort predicate)
               throws IOException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("with-all-mts"));

    CycList command1 = new CycList();
    command.add(command1);
    command1.add(CycObjectFactory.makeCycSymbol("arity"));
    command1.add(predicate);

    Object object = this.converseObject(command);

    if (object instanceof Integer) {
      return ((Integer) object).intValue();
    }
    else {
      return 0;
    }
  }

  /**
   * Returns the list of arg2 values of binary gafs, given the predicate and arg1, looking in all
   * microtheories.
   * 
   * @param predicate the given predicate for the gaf pattern
   * @param arg1 the given first argument of the gaf
   * 
   * @return the list of arg2 values of the binary gafs
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getArg2s(CycFort predicate, 
                          Object arg1)
                   throws IOException, CycApiException {
    CycList query = new CycList();
    query.add(predicate);
    query.add(arg1);

    CycVariable variable = CycObjectFactory.makeCycVariable(
                                 "?arg2");
    query.add(variable);

    return (CycList) askWithVariable(query, 
                                     variable, 
                                     inferencePSC);
  }

  /**
   * Returns the single (first) arg2 value of a binary gaf, given the predicate and arg0, looking
   * in all microtheories.  Return null if none found.
   * 
   * @param predicate the given predicate for the gaf pattern
   * @param arg1 the given first argument of the gaf
   * 
   * @return the single (first) arg2 value of the binary gaf(s)
   * 
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public Object getArg2(CycFort predicate, 
                        Object arg1)
                 throws IOException, CycApiException {
    CycList arg2s = getArg2s(predicate, 
                             arg1);

    if (arg2s.isEmpty()) {
      return null;
    }
    else {
      return arg2s.first();
    }
  }

  /**
   * Returns true if formula is well-formed in the relevant mt.
   * 
   * @param formula the given EL formula
   * @param mt the relevant mt
   * 
   * @return true if formula is well-formed in the relevant mt, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isFormulaWellFormed(CycList formula, 
                                     CycObject mt)
                              throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(el-formula-ok? " + formula.stringApiValue() + " " + 
                           makeELMt(mt).stringApiValue() + ")");
  }

  /**
   * Returns true if formula is well-formed Non Atomic Reifable Term.
   * 
   * @param formula the given EL formula
   * 
   * @return true if formula is well-formed Non Atomic Reifable Term, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isCycLNonAtomicReifableTerm(CycList formula)
                                      throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(cycl-nart-p " + formula.stringApiValue() + ")");
  }

  /**
   * Returns true if formula is well-formed Non Atomic Un-reifable Term.
   * 
   * @param formula the given EL formula
   * 
   * @return true if formula is well-formed Non Atomic Un-reifable Term, otherwise false
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isCycLNonAtomicUnreifableTerm(CycList formula)
                                        throws IOException, UnknownHostException, CycApiException {
    return converseBoolean("(cycl-naut-p " + formula.stringApiValue() + ")");
  }

  /**
   * Creates a new Collector microtheory and links it more general mts.
   * 
   * @param mtName the name of the new collector microtheory
   * @param comment the comment for the new collector microtheory
   * @param genlMts the list of more general microtheories
   * 
   * @return the new microtheory
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createCollectorMt(String mtName, 
                                       String comment, 
                                       List genlMts)
                                throws IOException, CycApiException {
    CycConstant collectorMt = getKnownConstantByName("CollectorMicrotheory");

    return createMicrotheory(mtName, 
                             comment, 
                             collectorMt, 
                             genlMts);
  }

  /**
   * Asserts each of the given list of forts to be instances of the given collection in the
   * UniversalVocabularyMt
   * 
   * @param fortNames the list of forts
   * @param collectionName
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertIsas(List fortNames, 
                         String collectionName)
                  throws IOException, CycApiException {
    List forts = new ArrayList();

    for (int i = 0; i < forts.size(); i++) {
      Object fort = forts.get(i);

      if (fort instanceof String) {
        forts.add(getKnownConstantByName((String) fort));
      }
      else if (fort instanceof CycFort) {
        forts.add(fort);
      }
      else {
        throw new CycApiException(fort + " is neither String nor CycFort");
      }

      assertIsas(forts, 
                 getKnownConstantByName(collectionName));
    }
  }

  /**
   * Asserts each of the given list of forts to be instances of the given collection in the
   * UniversalVocabularyMt
   * 
   * @param forts the list of forts
   * @param collection
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertIsas(List forts, 
                         CycFort collection)
                  throws IOException, CycApiException {
    for (int i = 0; i < forts.size(); i++) {
      assertIsa((CycFort) forts.get(i), 
                collection);
    }
  }

  /**
   * Creates a new spindle microtheory in the given spindle system.
   * 
   * @param spindleMtName the name of the new spindle microtheory
   * @param comment the comment for the new spindle microtheory
   * @param spindleHeadMtName the name of the spindle head microtheory
   * @param spindleCollectorMtName the name of the spindle head microtheory
   * 
   * @return the new spindle microtheory in the given spindle system
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createSpindleMt(String spindleMtName, 
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
   * 
   * @return the new spindle microtheory in the given spindle system
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createSpindleMt(String spindleMtName, 
                                     String comment, 
                                     CycFort spindleHeadMt, 
                                     CycFort spindleCollectorMt)
                              throws IOException, CycApiException {
    CycConstant spindleMt = getKnownConstantByName("SpindleMicrotheory");
    List genlMts = new ArrayList();
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
   * Creates a new binary predicate term.
   * 
   * @param predicateName the name of the new binary predicate
   * @param predicateTypeName the type of binary predicate, for example
   *        #$TransitiveBinaryPredicate, which when null defaults to #$BinaryPredicate
   * @param comment the comment for the new binary predicate, or null
   * @param arg1IsaName the argument position one type constraint, or null
   * @param arg2IsaName the argument position two type constraint, or null
   * @param arg1FormatName the argument position one format constraint, or null
   * @param arg2FormatName the argument position two format constraint, or null
   * @param genlPredsName the more general binary predicate of which this new predicate is a
   *        specialization, that when null defaults to #$conceptuallyRelated
   * @param genFormatList the paraphrase generation list string, or null
   * @param genFormatString the paraphrase generation string, or null
   * 
   * @return the new binary predicate term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createBinaryPredicate(String predicateName, 
                                           String predicateTypeName, 
                                           String comment, 
                                           String arg1IsaName, 
                                           String arg2IsaName, 
                                           String arg1FormatName, 
                                           String arg2FormatName, 
                                           String genlPredsName, 
                                           String genFormatString, 
                                           String genFormatList)
                                    throws IOException, CycApiException {
    return createBinaryPredicate(predicateName, 
                                 find(predicateTypeName), 
                                 comment, 
                                 find(arg1IsaName), 
                                 find(arg2IsaName), 
                                 find(arg1FormatName), 
                                 find(arg2FormatName), 
                                 find(genlPredsName), 
                                 genFormatString, 
                                 makeCycList(genFormatList));
  }

  /**
   * Creates a new binary predicate term.
   * 
   * @param predicateName the name of the new binary predicate
   * @param predicateType the type of binary predicate, for example #$TransitiveBinaryPredicate,
   *        which when null defaults to #$BinaryPredicate
   * @param comment the comment for the new binary predicate, or null
   * @param arg1Isa the argument position one type constraint, or null
   * @param arg2Isa the argument position two type constraint, or null
   * @param arg1Format the argument position one format constraint, or null
   * @param arg2Format the argument position two format constraint, or null
   * @param genlPreds the more general binary predicate of which this new predicate is a
   *        specialization, that when null defaults to #$conceptuallyRelated
   * @param genFormatList the paraphrase generation list string, or null
   * @param genFormatString the paraphrase generation string, or null
   * 
   * @return the new binary predicate term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createBinaryPredicate(String predicateName, 
                                           CycFort predicateType, 
                                           String comment, 
                                           CycFort arg1Isa, 
                                           CycFort arg2Isa, 
                                           CycFort arg1Format, 
                                           CycFort arg2Format, 
                                           CycFort genlPreds, 
                                           String genFormatString, 
                                           CycList genFormatList)
                                    throws IOException, CycApiException {
    CycConstant predicate = findOrCreate(predicateName);

    if (predicateType == null) {
      assertIsa(predicate, 
                binaryPredicate);
    }
    else {
      assertIsa(predicate, 
                predicateType);
    }

    if (comment != null) {
      assertComment(predicate, 
                    comment, 
                    baseKB);
    }

    if (arg1Isa != null) {
      assertArgIsa(predicate, 
                   1, 
                   arg1Isa);
    }

    if (arg2Isa != null) {
      assertArgIsa(predicate, 
                   2, 
                   arg2Isa);
    }

    if (arg1Format != null) {
      assertArgFormat(predicate, 
                      1, 
                      arg1Format);
    }

    if (arg2Format != null) {
      assertArgFormat(predicate, 
                      2, 
                      arg2Format);
    }

    if (genlPreds == null) {
      assertGenlPreds(predicate, 
                      
      // #$conceptuallyRelated
      getKnownConstantByGuid("bd58803e-9c29-11b1-9dad-c379636f7270"));
    }
    else {
      assertGenlPreds(predicate, 
                      genlPreds);
    }

    if ((genFormatString != null) && (genFormatList != null)) {
      assertGenFormat(predicate, 
                      genFormatString, 
                      genFormatList);
    }

    return predicate;
  }

  /**
   * Creates a new KB subset collection term.
   * 
   * @param constantName the name of the new KB subset collection
   * @param comment the comment for the new KB subset collection
   * 
   * @return the new KB subset collection term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createKbSubsetCollection(String constantName, 
                                              String comment)
                                       throws IOException, CycApiException {
    CycConstant kbSubsetCollection = getKnownConstantByName(
                                           "KBSubsetCollection");
    CycConstant cycConstant = getConstantByName(constantName);

    if (cycConstant == null) {
      cycConstant = createNewPermanent(constantName);
    }

    assertIsa(cycConstant, 
              kbSubsetCollection);
    assertComment(cycConstant, 
                  comment, 
                  baseKB);
    assertGenls(cycConstant, 
                thing);

    CycFort variableOrderCollection = getKnownConstantByGuid(
                                            "36cf85d0-20a1-11d6-8000-0050dab92c2f");
    assertIsa(cycConstant, 
              variableOrderCollection, 
              baseKB);

    return cycConstant;
  }

  /**
   * Creates a new collection term.
   * 
   * @param collectionName the name of the new collection
   * @param comment the comment for the collection
   * @param commentMtName the name of the microtheory in which the comment is asserted
   * @param isaName the name of the collection of which the new collection is an instance
   * @param genlsName the name of the collection of which the new collection is a subset
   * 
   * @return the new collection term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant createCollection(String collectionName, 
                                      String comment, 
                                      String commentMtName, 
                                      String isaName, 
                                      String genlsName)
                               throws IOException, CycApiException {
    CycConstant collection = findOrCreate(collectionName);
    assertComment(collection, 
                  comment, 
                  getKnownConstantByName(commentMtName));
    assertIsa(collection, 
              getKnownConstantByName(isaName));
    assertGenls(collection, 
                getKnownConstantByName(genlsName));

    return collection;
  }

  /**
   * Creates a new collection term.
   * 
   * @param collectionName the name of the new collection
   * @param comment the comment for the collection
   * @param commentMt the microtheory in which the comment is asserted
   * @param isa the collection of which the new collection is an instance
   * @param genls the collection of which the new collection is a subset
   * 
   * @return the new collection term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollection(String collectionName, 
                                  String comment, 
                                  CycFort commentMt, 
                                  CycFort isa, 
                                  CycFort genls)
                           throws IOException, CycApiException {
    return createCollection(findOrCreate(collectionName), 
                            comment, 
                            commentMt, 
                            isa, 
                            genls);
  }

  /**
   * Creates a new collection term.
   * 
   * @param collection the new collection
   * @param comment the comment for the collection
   * @param commentMt the microtheory in which the comment is asserted
   * @param isa the collection of which the new collection is an instance
   * @param genls the collection of which the new collection is a subset
   * 
   * @return the new collection term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollection(CycFort collection, 
                                  String comment, 
                                  CycFort commentMt, 
                                  CycFort isa, 
                                  CycFort genls)
                           throws IOException, CycApiException {
    assertComment(collection, 
                  comment, 
                  commentMt);
    assertIsa(collection, 
              isa);
    assertGenls(collection, 
                genls);

    return collection;
  }

  /**
   * Creates a new individual term.
   * 
   * @param IndividualName the name of the new individual term
   * @param comment the comment for the individual
   * @param commentMt the microtheory in which the comment is asserted
   * @param isa the collection of which the new individual is an instance
   * 
   * @return the new individual term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createIndividual(String IndividualName, 
                                  String comment, 
                                  String commentMt, 
                                  String isa)
                           throws IOException, CycApiException {
    return createIndividual(IndividualName, 
                            comment, 
                            getKnownConstantByName(commentMt), 
                            getKnownConstantByName(isa));
  }

  /**
   * Creates a new individual term.
   * 
   * @param IndividualName the name of the new individual term
   * @param comment the comment for the individual
   * @param commentMt the microtheory in which the comment is asserted
   * @param isa the collection of which the new individual is an instance
   * 
   * @return the new individual term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createIndividual(String IndividualName, 
                                  String comment, 
                                  CycFort commentMt, 
                                  CycFort isa)
                           throws IOException, CycApiException {
    CycFort individual = findOrCreate(IndividualName);
    assertComment(individual, 
                  comment, 
                  commentMt);
    assertIsa(individual, 
              isa);

    return individual;
  }

  /**
   * Creates a new individual-denoting reifiable unary function term.
   * 
   * @param unaryFunction the new collection
   * @param comment the comment for the unary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1Isa the kind of objects this unary function takes as its argument
   * @param resultIsa the kind of object represented by this reified term
   * 
   * @return the new individual-denoting reifiable unary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createIndivDenotingUnaryFunction(String unaryFunction, 
                                                  String comment, 
                                                  String commentMt, 
                                                  String arg1Isa, 
                                                  String resultIsa)
                                           throws IOException, CycApiException {
    return createIndivDenotingUnaryFunction(findOrCreate(
                                                  unaryFunction), 
                                            comment, 
                                            getKnownConstantByName(
                                                  commentMt), 
                                            getKnownConstantByName(
                                                  arg1Isa), 
                                            getKnownConstantByName(
                                                  resultIsa));
  }

  /**
   * Creates a new individual-denoting reifiable unary function term.
   * 
   * @param unaryFunction the new collection
   * @param comment the comment for the unary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1Isa the kind of objects this unary function takes as its argument
   * @param resultIsa the kind of object represented by this reified term
   * 
   * @return the new individual-denoting reifiable unary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createIndivDenotingUnaryFunction(CycFort unaryFunction, 
                                                  String comment, 
                                                  CycFort commentMt, 
                                                  CycFort arg1Isa, 
                                                  CycFort resultIsa)
                                           throws IOException, CycApiException {
    assertComment(unaryFunction, 
                  comment, 
                  commentMt);


    // (#$isa unaryFunction #$UnaryFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd58af89-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$ReifiableFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd588002-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$IndividualDenotingFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd58fad9-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$Function-Denotational)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd5c40b0-9c29-11b1-9dad-c379636f7270"));
    assertArgIsa(unaryFunction, 
                 1, 
                 arg1Isa);
    assertResultIsa(unaryFunction, 
                    resultIsa);

    return unaryFunction;
  }

  /**
   * Creates a new collection-denoting reifiable unary function term.
   * 
   * @param unaryFunction the new collection
   * @param comment the comment for the unary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1Isa the isa type constraint for the argument
   * @param arg1GenlName the genls type constraint for the argument if it is a collection
   * @param resultIsa the isa object represented by this reified term
   * @param resultGenlName the genls object represented by this reified term
   * 
   * @return the new collection-denoting reifiable unary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollectionDenotingUnaryFunction(String unaryFunction, 
                                                       String comment, 
                                                       String commentMt, 
                                                       String arg1Isa, 
                                                       String arg1GenlName, 
                                                       String resultIsa, 
                                                       String resultGenlName)
                                                throws IOException, CycApiException {
    CycFort arg1Genl;

    if (arg1GenlName != null) {
      arg1Genl = getKnownConstantByName(arg1GenlName);
    }
    else {
      arg1Genl = null;
    }

    CycFort resultGenl;

    if (resultGenlName != null) {
      resultGenl = getKnownConstantByName(resultGenlName);
    }
    else {
      resultGenl = null;
    }

    return createCollectionDenotingUnaryFunction(findOrCreate(
                                                       unaryFunction), 
                                                 comment, 
                                                 getKnownConstantByName(
                                                       commentMt), 
                                                 getKnownConstantByName(
                                                       arg1Isa), 
                                                 arg1Genl, 
                                                 getKnownConstantByName(
                                                       resultIsa), 
                                                 resultGenl);
  }

  /**
   * Creates a new collection-denoting reifiable unary function term.
   * 
   * @param unaryFunction the new collection
   * @param comment the comment for the unary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1Isa the isa type constraint for the argument
   * @param arg1Genl the genls type constraint for the argument if it is a collection
   * @param resultIsa the isa object represented by this reified term
   * @param resultGenl the genls object represented by this reified term
   * 
   * @return the new collection-denoting reifiable unary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollectionDenotingUnaryFunction(CycFort unaryFunction, 
                                                       String comment, 
                                                       CycFort commentMt, 
                                                       CycFort arg1Isa, 
                                                       CycFort arg1Genl, 
                                                       CycFort resultIsa, 
                                                       CycFort resultGenl)
                                                throws IOException, CycApiException {
    assertComment(unaryFunction, 
                  comment, 
                  commentMt);


    // (#$isa unaryFunction #$UnaryFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd58af89-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$ReifiableFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd588002-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$CollectionDenotingFunction)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd58806a-9c29-11b1-9dad-c379636f7270"));


    // (#$isa unaryFunction #$Function-Denotational)
    assertIsa(unaryFunction, 
              getKnownConstantByGuid("bd5c40b0-9c29-11b1-9dad-c379636f7270"));
    assertArgIsa(unaryFunction, 
                 1, 
                 arg1Isa);

    if (arg1Genl != null) {
      assertArg1Genl(unaryFunction, 
                     arg1Genl);
    }

    assertResultIsa(unaryFunction, 
                    resultIsa);

    if (resultGenl != null) {
      assertResultGenl(unaryFunction, 
                       resultGenl);
    }

    return unaryFunction;
  }

  /**
   * Creates a new collection-denoting reifiable binary function term.
   * 
   * @param binaryFunction the new collection
   * @param comment the comment for the binary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1IsaName the collection of which the new binary function is an instance
   * @param arg2GenlsName the kind of objects this binary function takes as its first argument, or
   *        null
   * @param arg2IsaName the kind of objects this binary function takes as its second argument, or
   *        null
   * @param arg1GenlsName the general collections this binary function takes as its first argument,
   *        or null
   * @param resultIsa the kind of object represented by this reified term
   * 
   * @return the new collection-denoting reifiable binary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollectionDenotingBinaryFunction(String binaryFunction, 
                                                        String comment, 
                                                        String commentMt, 
                                                        String arg1IsaName, 
                                                        String arg2IsaName, 
                                                        String arg1GenlsName, 
                                                        String arg2GenlsName, 
                                                        String resultIsa)
                                                 throws IOException, CycApiException {
    CycFort arg1Isa = null;
    CycFort arg2Isa = null;
    CycFort arg1Genls = null;
    CycFort arg2Genls = null;

    if (arg1IsaName != null) {
      arg1Isa = getKnownConstantByName(arg1IsaName);
    }

    if (arg2IsaName != null) {
      arg1Isa = getKnownConstantByName(arg2IsaName);
    }

    if (arg1GenlsName != null) {
      arg1Genls = getKnownConstantByName(arg1GenlsName);
    }

    if (arg2GenlsName != null) {
      arg2Genls = getKnownConstantByName(arg2GenlsName);
    }

    return createCollectionDenotingBinaryFunction(findOrCreate(
                                                        binaryFunction), 
                                                  comment, 
                                                  getKnownConstantByName(
                                                        commentMt), 
                                                  arg1Isa, 
                                                  arg2Isa, 
                                                  arg1Genls, 
                                                  arg2Genls, 
                                                  getKnownConstantByName(
                                                        resultIsa));
  }

  /**
   * Creates a new collection-denoting reifiable binary function term.
   * 
   * @param binaryFunction the new collection
   * @param comment the comment for the binary function
   * @param commentMt the microtheory in which the comment is asserted
   * @param arg1Isa the kind of objects this binary function takes as its first argument, or null
   * @param arg2Isa the kind of objects this binary function takes as its first argument, or null
   * @param arg1Genls the general collections this binary function takes as its first argument, or
   *        null
   * @param arg2Genls the general collections this binary function takes as its second argument, or
   *        null
   * @param resultIsa the kind of object represented by this reified term
   * 
   * @return the new collection-denoting reifiable binary function term
   * 
   * @throws IOException if a communications error occurs
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycFort createCollectionDenotingBinaryFunction(CycFort binaryFunction, 
                                                        String comment, 
                                                        CycFort commentMt, 
                                                        CycFort arg1Isa, 
                                                        CycFort arg2Isa, 
                                                        CycFort arg1Genls, 
                                                        CycFort arg2Genls, 
                                                        CycFort resultIsa)
                                                 throws IOException, CycApiException {
    assertComment(binaryFunction, 
                  comment, 
                  commentMt);


    // (#$isa binaryFunction #$BinaryFunction)
    assertIsa(binaryFunction, 
              getKnownConstantByGuid("c0e7247c-9c29-11b1-9dad-c379636f7270"));


    // (#$isa binaryFunction #$ReifiableFunction)
    assertIsa(binaryFunction, 
              getKnownConstantByGuid("bd588002-9c29-11b1-9dad-c379636f7270"));


    // (#$isa binaryFunction #$CollectionDenotingFunction)
    assertIsa(binaryFunction, 
              getKnownConstantByGuid("bd58806a-9c29-11b1-9dad-c379636f7270"));


    // (#$isa binaryFunction #$Function-Denotational)
    assertIsa(binaryFunction, 
              getKnownConstantByGuid("bd5c40b0-9c29-11b1-9dad-c379636f7270"));

    if (arg1Isa != null) {
      assertArgIsa(binaryFunction, 
                   1, 
                   arg1Isa);
    }

    if (arg2Isa != null) {
      assertArgIsa(binaryFunction, 
                   2, 
                   arg2Isa);
    }

    if (arg1Genls != null) {
      assertArg1Genl(binaryFunction, 
                     arg1Genls);
    }

    if (arg2Genls != null) {
      assertArg2Genl(binaryFunction, 
                     arg2Genls);
    }

    assertResultIsa(binaryFunction, 
                    resultIsa);

    return binaryFunction;
  }

  /**
   * Assert an argument isa contraint for the given relation and argument position. The operation
   * will be added to the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * @param argPosition the given argument position
   * @param argNIsa the argument constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArgIsa(CycFort relation, 
                           int argPosition, 
                           CycFort argNIsa)
                    throws IOException, UnknownHostException, CycApiException {
    // (#$argIsa relation argPosition argNIsa)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("bee22d3d-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(new Integer(argPosition));
    sentence.add(argNIsa);
    assertGaf(sentence, 
              universalVocabularyMt);
  }

  /**
   * Assert an argument one genls contraint for the given relation. The operation will be added to
   * the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * @param argGenl the argument constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArg1Genl(CycFort relation, 
                             CycFort argGenl)
                      throws IOException, UnknownHostException, CycApiException {
    // (#$arg1Genl relation argGenl)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("bd588b1d-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(argGenl);
    assertGaf(sentence, 
              universalVocabularyMt);
  }

  /**
   * Assert an argument two genls contraint for the given relation. The operation will be added to
   * the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * @param argGenl the argument constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArg2Genl(CycFort relation, 
                             CycFort argGenl)
                      throws IOException, UnknownHostException, CycApiException {
    // (#$arg2Genl relation argGenl)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("bd58dcda-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(argGenl);
    assertGaf(sentence, 
              universalVocabularyMt);
  }

  /**
   * Assert an argument three genls contraint for the given relation. The operation will be added
   * to the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * @param argGenl the argument constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArg3Genl(CycFort relation, 
                             CycFort argGenl)
                      throws IOException, UnknownHostException, CycApiException {
    // (#$arg3Genl relation argGenl)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("bd58b8c3-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(argGenl);
    assertGaf(sentence, 
              universalVocabularyMt);
  }

  /**
   * Assert the isa result contraint for the given denotational function. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param denotationalFunction the given denotational function
   * @param resultIsa the function's isa result constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertResultIsa(CycFort denotationalFunction, 
                              CycFort resultIsa)
                       throws IOException, UnknownHostException, CycApiException {
    // (#$resultIsa denotationalFunction resultIsa)
    assertGaf(universalVocabularyMt, 
              getKnownConstantByGuid("bd5880f1-9c29-11b1-9dad-c379636f7270"), 
              denotationalFunction, 
              resultIsa);
  }

  /**
   * Assert the genls result contraint for the given denotational function. The operation will be
   * added to the KB transcript for replication and archive.
   * 
   * @param denotationalFunction the given denotational function
   * @param resultGenl the function's genls result constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertResultGenl(CycFort denotationalFunction, 
                               CycFort resultGenl)
                        throws IOException, UnknownHostException, CycApiException {
    // (#$resultGenl denotationalFunction resultGenls)
    assertGaf(universalVocabularyMt, 
              getKnownConstantByGuid("bd58d6ab-9c29-11b1-9dad-c379636f7270"), 
              denotationalFunction, 
              resultGenl);
  }

  /**
   * Returns true if this KB is OpenCyc.
   * 
   * @return true if this KB is OpenCyc, otherwise false
   * 
   * @throws IOException if a data communication error occurs
   * @throws UnknownHostException if cyc server host not found on the network
   */
  public boolean isOpenCyc()
                    throws IOException, UnknownHostException {
    boolean answer;

    try {
      answer = converseBoolean("(cyc-opencyc-feature)");
    }
     catch (CycApiException e) {
      answer = false;
    }

    return answer;
  }

  /**
   * Returns a constant whose name differs from the given name only by case. Used because Cyc by
   * default requires constant names to be unique by case.
   * 
   * @param name the name used to lookup an existing constant
   * 
   * @return a constant whose name differs from the given name only by case, otherwise null if none
   *         exists
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycConstant constantNameCaseCollision(String name)
                                        throws IOException, UnknownHostException, CycApiException {
    Object object = converseObject("(constant-name-case-collision \"" + name + "\")");

    if (object instanceof CycConstant) {
      return (CycConstant) object;
    }
    else {
      return null;
    }
  }

  /**
   * Returns the list of applicable binary predicates which are elements of any of the given list
   * of KBSubsetCollections.
   * 
   * @param kbSubsetCollections the list of KBSubsetCollections
   * 
   * @return the list of applicable binary predicates which are elements of any of the given list
   *         of KBSubsetCollections
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getApplicableBinaryPredicates(CycList kbSubsetCollections)
                                        throws IOException, UnknownHostException, CycApiException {
    CycList result = new CycList();

    for (int i = 0; i < kbSubsetCollections.size(); i++) {
      CycFort kbSubsetCollection = (CycFort) kbSubsetCollections.get(
                                         i);
      String query = "(#$and \n" + "  (#$isa ?binary-predicate " + kbSubsetCollection.stringApiValue() + 
                     ") \n" + "  (#$isa ?binary-predicate #$BinaryPredicate))";
      result.addAllNew(askWithVariable(makeCycList(query), 
                                       CycObjectFactory.makeCycVariable("?binary-predicate"), 
                                       inferencePSC));
    }

    return result;
  }

  /**
   * Returns the list of gafs in which the predicate is a element of the given list of predicates
   * and in which the given term appears in the first argument position.
   * 
   * @param cycFort the given term
   * @param predicates the given list of predicates
   * @param mt the relevant inference microtheory
   * 
   * @return the list of gafs in which the predicate is a element of the given list of predicates
   *         and in which the given term appears in the first argument position
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getGafs(CycFort cycFort, 
                         CycList predicates, 
                         CycObject mt)
                  throws IOException, UnknownHostException, CycApiException {
    CycList result = new CycList();

    for (int i = 0; i < predicates.size(); i++) {
      result.addAllNew(getGafs(cycFort, 
                               (CycFort) predicates.get(
                                     i), 
                               makeELMt(mt)));
    }

    return result;
  }

  /**
   * Returns the list of gafs in which the predicate is the given predicate and in which the given
   * term appears in the first argument position.
   * 
   * @param cycFort the given term
   * @param predicate the given predicate
   * @param mt the relevant inference microtheory
   * 
   * @return the list of gafs in which the predicate is a element of the given list of predicates
   *         and in which the given term appears in the first argument position
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getGafs(CycFort cycFort, 
                         CycFort predicate, 
                         CycObject mt)
                  throws IOException, UnknownHostException, CycApiException {
    CycList gafs = new CycList();
    String command = "(with-mt " + makeELMt(mt).stringApiValue() + "\n" + 
                     "  (pred-values-in-relevant-mts " + cycFort.stringApiValue() + " " + 
                     predicate.stringApiValue() + "))";
    CycList values = converseList(command);

    for (int i = 0; i < values.size(); i++) {
      CycList gaf = new CycList();
      gaf.add(predicate);
      gaf.add(cycFort);
      gaf.add(values.get(i));
      gafs.add(gaf);
    }

    return gafs;
  }

  /**
   * Returns the list of gafs in which the predicate is a element of the given list of predicates
   * and in which the given term appears in the first argument position.
   * 
   * @param cycObject the given term
   * @param predicates the given list of predicates
   * 
   * @return the list of gafs in which the predicate is a element of the given list of predicates
   *         and in which the given term appears in the first argument position
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getGafs(CycObject cycObject, 
                         CycList predicates)
                  throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
            
    CycList result = new CycList();
    for (int i = 0; i < predicates.size(); i++) {
      result.addAllNew(getGafs(cycObject, (CycObject) predicates.get(i)));
    }

    return result;
  }

  /**
   * Returns the list of gafs in which the predicate is the given predicate and in which the given
   * term appears in the first argument position.
   * 
   * @param cycObject the given term
   * @param predicate the given predicate
   * 
   * @return the list of gafs in which the predicate is a element of the given list of predicates
   *         and in which the given term appears in the first argument position
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getGafs(CycObject cycObject, 
                         CycObject predicate)
                  throws IOException, UnknownHostException, CycApiException {
    //// Preconditions
    assert cycObject instanceof CycConstant || 
           cycObject instanceof CycNart || 
           cycObject instanceof CycList : cycObject.cyclify() + " must be a CycConstant, CycNart or CycList";
    if (cycObject instanceof CycList)
      return getGafsForNaut((CycList) cycObject, predicate);
    CycList gafs = new CycList();
    String command = "(with-all-mts \n" + "  (pred-values-in-relevant-mts (canonicalize-term " + 
                     cycObject.stringApiValue() + ") " + "(canonicalize-term " + 
                     predicate.stringApiValue() + ")))";
    CycList values = converseList(command);

    for (int i = 0; i < values.size(); i++) {
      CycList gaf = new CycList();
      gaf.add(predicate);
      gaf.add(cycObject);
      gaf.add(values.get(i));
      gafs.add(gaf);
    }

    return gafs;
  }

  /**
   * Returns the list of gafs in which the predicate is the given predicate and in which the given
   * term appears in the first argument position.
   * 
   * @param cycObject the given term
   * @param predicate the given predicate
   * 
   * @return the list of gafs in which the predicate is a element of the given list of predicates
   *         and in which the given term appears in the first argument position
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getGafsForNaut(CycList naut, 
                         CycObject predicate)
                  throws IOException, UnknownHostException, CycApiException {
    final String command = 
      "(clet (assertions) " +
      "  (do-gaf-arg-index-naut (assertion " + naut.stringApiValue() + ")" +
      "    (pwhen (equal (formula-arg1 assertion) " + predicate.stringApiValue() + ")" +
      "      (cpush assertion assertions))) " +
      "  assertions)";
    final CycList gafs = converseList(command);

    //// Postconditions
    assert gafs != null : "gafs cannot be null";
    
    return gafs;
  }

  /**
   * Returns the list of tuples gathered from assertions in given microtheory in which the
   * predicate is the given predicate, in which the given term appears in the indexArg position
   * and in which the list of gatherArgs determines the assertion arguments returned as each
   * tuple.
   * 
   * @param term the term in the index argument position
   * @param predicate the given predicate
   * @param indexArg the argument position in which the given term appears
   * @param gatherArgs the list of argument Integer positions which indicate the assertion
   *        arguments to be returned as each tuple
   * @param mt the relevant inference microtheory
   * 
   * @return the list of tuples gathered from assertions in given microtheory in which the
   *         predicate is the given predicate, in which the given term appears in the indexArg
   *         position and in which the list of gatherArgs determines the assertion arguments
   *         returned as each tuple
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getPredicateValueTuplesInMt(CycFort term, 
                                             CycFort predicate, 
                                             int indexArg, 
                                             CycList gatherArgs, 
                                             CycObject mt)
                                      throws IOException, UnknownHostException, CycApiException {
    CycList tuples = new CycList();
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("pred-value-tuples-in-mt"));
    command.add(term);
    command.add(predicate);
    command.add(new Integer(indexArg));
    command.addQuoted(gatherArgs);
    command.add(makeELMt(mt));

    return converseList(command);
  }

  /**
   * Assert an argument contraint for the given relation and argument position. The operation will
   * be added to the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArg1FormatSingleEntry(CycFort relation)
                                   throws IOException, UnknownHostException, CycApiException {
    // (#$arg1Format relation SingleEntry)
    assertArgFormat(relation, 
                    1, 
                    getKnownConstantByGuid("bd5880eb-9c29-11b1-9dad-c379636f7270"));
  }

  /**
   * Assert an argument format contraint for the given relation and argument position. The
   * operation will be added to the KB transcript for replication and archive.
   * 
   * @param relation the given relation
   * @param argPosition the given argument position
   * @param argNFormat the argument format constraint
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public void assertArgFormat(CycFort relation, 
                              int argPosition, 
                              CycFort argNFormat)
                       throws IOException, UnknownHostException, CycApiException {
    // (#$argFormat relation argPosition argNFormat)
    CycList sentence = new CycList();
    sentence.add(getKnownConstantByGuid("bd8a36e1-9c29-11b1-9dad-c379636f7270"));
    sentence.add(relation);
    sentence.add(new Integer(argPosition));
    sentence.add(argNFormat);
    assertGaf(sentence, 
              baseKB);
  }

  /**
   * Asserts that the given DAML imported term is mapped to the given Cyc term.
   * 
   * @param cycTerm the mapped Cyc term
   * @param informationSource the external indexed information source
   * @param externalConcept the external concept within the information source
   * @param mt the assertion microtheory
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertSynonymousExternalConcept(String cycTerm, 
                                              String informationSource, 
                                              String externalConcept, 
                                              String mt)
                                       throws IOException, UnknownHostException, CycApiException {
    assertSynonymousExternalConcept(getKnownConstantByName(
                                          cycTerm), 
                                    getKnownConstantByName(
                                          informationSource), 
                                    externalConcept, 
                                    getKnownConstantByName(
                                          mt));
  }

  /**
   * Asserts that the given DAML imported term is mapped to the given Cyc term.
   * 
   * @param cycTerm the mapped Cyc term
   * @param informationSource the external indexed information source
   * @param externalConcept the external concept within the information source
   * @param mt the assertion microtheory
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertSynonymousExternalConcept(CycFort cycTerm, 
                                              CycFort informationSource, 
                                              String externalConcept, 
                                              CycObject mt)
                                       throws IOException, UnknownHostException, CycApiException {
    CycList gaf = new CycList();


    // #$synonymousExternalConcept
    gaf.add(getKnownConstantByGuid("c0e2af4e-9c29-11b1-9dad-c379636f7270"));
    gaf.add(cycTerm);
    gaf.add(informationSource);
    gaf.add(externalConcept);
    assertGaf(gaf, 
              makeELMt(mt));
  }

  /**
   * Gets the list of mappings from the specified information source given the inference
   * microtheory.  Each returned list item is the pair consisting of external concept string and
   * synonymous Cyc term.
   * 
   * @param informationSource the external indexed information source
   * @param mt the assertion microtheory
   * 
   * @return list of mappings from the specified information source given the inference
   * microtheory
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getSynonymousExternalConcepts(String informationSource, 
                                               String mt)
                                        throws IOException, UnknownHostException, CycApiException {
    return getSynonymousExternalConcepts(getKnownConstantByName(
                                               informationSource), 
                                         getKnownConstantByName(
                                               mt));
  }

  /**
   * Gets the list of mappings from the specified information source given the inference
   * microtheory.  Each returned list item is the pair consisting of external concept string and
   * synonymous Cyc term.
   * 
   * @param informationSource the external indexed information source
   * @param mt the assertion microtheory
   * 
   * @return the list of mappings from the specified information source given the inference
   * microtheory
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getSynonymousExternalConcepts(CycFort informationSource, 
                                               CycObject mt)
                                        throws IOException, UnknownHostException, CycApiException {
    CycList variables = new CycList();
    CycVariable cycTermVar = CycObjectFactory.makeCycVariable(
                                   "?cyc-term");
    variables.add(cycTermVar);

    CycVariable externalConceptVar = CycObjectFactory.makeCycVariable(
                                           "?externalConcept");
    variables.add(externalConceptVar);

    CycList query = new CycList();


    // #$synonymousExternalConcept
    query.add(getKnownConstantByGuid("c0e2af4e-9c29-11b1-9dad-c379636f7270"));
    query.add(cycTermVar);
    query.add(informationSource);
    query.add(externalConceptVar);

    return askWithVariables(query, 
                            variables, 
                            makeELMt(mt));
  }

  /**
   * Asserts a preferred name string for the given term using lexical singular count noun
   * assumptions.
   * 
   * @param cycTerm the Cyc term
   * @param phrase the preferred phrase for this term
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertGenPhraseCountNounSingular(CycFort cycTerm, 
                                               String phrase)
                                        throws IOException, UnknownHostException, CycApiException {
    CycList gaf = new CycList();


    // (#$genPhrase <term> #$CountNoun #$singular <phrase>) in
    // #$EnglishParaphaseMt
    gaf.add(getKnownConstantByGuid("bd5fb28e-9c29-11b1-9dad-c379636f7270"));
    gaf.add(cycTerm);
    gaf.add(getKnownConstantByGuid("bd588078-9c29-11b1-9dad-c379636f7270"));
    gaf.add(getKnownConstantByGuid("bd6757b8-9c29-11b1-9dad-c379636f7270"));
    gaf.add(phrase);

    ELMt elmt = makeELMt(getKnownConstantByGuid("bda16220-9c29-11b1-9dad-c379636f7270"));
    assertGaf(gaf, 
              elmt);
  }

  /**
   * Asserts a preferred name string for the given term using lexical singular count noun
   * assumptions.
   * 
   * @param cycTerm the Cyc term
   * @param phrase the preferred phrase for this term
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void assertGenPhraseCountNounPlural(CycFort cycTerm, 
                                             String phrase)
                                      throws IOException, UnknownHostException, CycApiException {
    CycList gaf = new CycList();


    // (#$genPhrase <term> #$CountNoun #$plural <phrase>) in
    // #$EnglishParaphaseMt
    gaf.add(getKnownConstantByGuid("bd5fb28e-9c29-11b1-9dad-c379636f7270"));
    gaf.add(cycTerm);
    gaf.add(getKnownConstantByGuid("bd588078-9c29-11b1-9dad-c379636f7270"));
    gaf.add(getKnownConstantByGuid("bd5a6853-9c29-11b1-9dad-c379636f7270"));
    gaf.add(phrase);

    ELMt elmt = makeELMt(getKnownConstantByGuid("bda16220-9c29-11b1-9dad-c379636f7270"));
    assertGaf(gaf, 
              elmt);
  }

  /**
   * Gets the list of name strings for the given CycFort.
   * 
   * @param cycFort the given FORT
   * @param mt the relevant inference microtheory
   * 
   * @return the list of name strings for the given CycFort
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public CycList getNameStrings(CycFort cycFort, 
                                CycObject mt)
                         throws IOException, UnknownHostException, CycApiException {
    // (#$nameString <cycFort> ?name-string)
    CycList query = new CycList();
    query.add(getKnownConstantByGuid("c0fdf7e8-9c29-11b1-9dad-c379636f7270"));
    query.add(cycFort);

    CycVariable variable = CycObjectFactory.makeCycVariable(
                                 "?name-string");
    query.add(variable);

    return askWithVariable(query, 
                           variable, 
                           makeELMt(mt));
  }

  /**
   * Ensures that the given term meets the given isa and genl wff constraints in the
   * UniversalVocabularyMt.
   * 
   * @param cycFort the given term
   * @param isaConstraintName the given isa type constraint, or null
   * @param genlsConstraintName the given genls type constraint, or null
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void ensureWffConstraints(String cycFort, 
                                   String isaConstraintName, 
                                   String genlsConstraintName)
                            throws IOException, UnknownHostException, CycApiException {
    CycConstant cycConstant = find(cycFort);
    CycConstant isaConstraint = null;
    CycConstant genlsConstraint = null;

    if (isaConstraintName != null) {
      isaConstraint = find(isaConstraintName);
    }

    if (genlsConstraintName != null) {
      genlsConstraint = find(genlsConstraintName);
    }

    ensureWffConstraints(cycConstant, 
                         isaConstraint, 
                         genlsConstraint);
  }

  /**
   * Ensures that the given term meets the given isa and genl wff constraints in the
   * UniversalVocabularyMt.
   * 
   * @param cycFort the given term
   * @param isaConstraint the given isa type constraint, or null
   * @param genlsConstraint the given genls type constraint, or null
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void ensureWffConstraints(CycFort cycFort, 
                                   CycFort isaConstraint, 
                                   CycFort genlsConstraint)
                            throws IOException, UnknownHostException, CycApiException {
    if ((isaConstraint != null) && 
            (!isa(cycFort, 
                  isaConstraint, 
                  universalVocabularyMt))) {
      assertIsa(cycFort, 
                isaConstraint);
    }

    if ((genlsConstraint != null) && 
            (!isSpecOf(cycFort, 
                       genlsConstraint, 
                       universalVocabularyMt))) {
      assertGenls(cycFort, 
                  genlsConstraint);
    }
  }

  /**
   * Returns the list of arg2 terms from binary gafs having the specified predicate and arg1
   * values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the list of arg2 terms from gafs having the specified predicate and arg1 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getArg2s(String predicate, 
                          String arg1, 
                          String mt)
                   throws IOException, UnknownHostException, CycApiException {
    return getArg2s(getKnownConstantByName(predicate), 
                    getKnownConstantByName(arg1), 
                    getKnownConstantByName(mt));
  }

  /**
   * Returns the list of arg2 terms from binary gafs having the specified predicate and arg1
   * values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the list of arg2 terms from gafs having the specified predicate and arg1 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getArg2s(String predicate, 
                          CycFort arg1, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    return getArg2s(getKnownConstantByName(predicate), 
                    arg1, 
                    makeELMt(mt));
  }

  /**
   * Returns the list of arg2 terms from binary gafs having the specified predicate and arg1
   * values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the list of arg2 terms from gafs having the specified predicate and arg1 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getArg2s(CycFort predicate, 
                          CycFort arg1, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(predicate);
    query.add(arg1);

    CycVariable variable = CycObjectFactory.makeCycVariable(
                                 "?arg2");
    query.add(variable);

    return askWithVariable(query, 
                           variable, 
                           makeELMt(mt));
  }

  /**
   * Returns the first arg2 term from binary gafs having the specified predicate and arg1 values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the first arg2 term from gafs having the specified predicate and arg1 values or null
   *         if none
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getArg2(String predicate, 
                        String arg1, 
                        String mt)
                 throws IOException, UnknownHostException, CycApiException {
    return getArg2(getKnownConstantByName(predicate), 
                   getKnownConstantByName(arg1), 
                   getKnownConstantByName(mt));
  }

  /**
   * Returns the first arg2 term from binary gafs having the specified predicate and arg1 values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the first arg2 term from gafs having the specified predicate and arg1 values or null
   *         if none
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getArg2(String predicate, 
                        CycFort arg1, 
                        CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    return getArg2(getKnownConstantByName(predicate), 
                   arg1, 
                   makeELMt(mt));
  }

  /**
   * Returns the first arg2 term from binary gafs having the specified predicate and arg1 values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the first arg2 term from gafs having the specified predicate and arg1 values or null
   *         if none
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getArg2(CycFort predicate, 
                        CycFort arg1, 
                        CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(predicate);
    query.add(arg1);

    CycVariable variable = CycObjectFactory.makeCycVariable(
                                 "?arg2");
    query.add(variable);

    CycList answer = askWithVariable(query, 
                                     variable, 
                                     makeELMt(mt));

    if (answer.size() > 0) {
      return answer.get(0);
    }
    else {
      return null;
    }
  }

  /**
   * Returns the first arg2 ground or non-term from assertions having the specified predicate and
   * arg1 values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the first arg2 ground or non-term from assertions having the specified predicate and
   *         arg1 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getAssertionArg2(String predicate, 
                                 String arg1, 
                                 String mt)
                          throws IOException, UnknownHostException, CycApiException {
    return getAssertionArg2(getKnownConstantByName(predicate), 
                            getKnownConstantByName(arg1), 
                            getKnownConstantByName(mt));
  }

  /**
   * Returns the first arg2 ground or non-term from assertions having the specified predicate and
   * arg1 values.
   * 
   * @param predicate the given predicate
   * @param arg1 the given arg1 term
   * @param mt the inference microtheory
   * 
   * @return the first arg2 ground or non-term from assertions having the specified predicate and
   *         arg1 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getAssertionArg2(CycFort predicate, 
                                 CycFort arg1, 
                                 CycObject mt)
                          throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("fpred-value-in-mt"));
    command.add(arg1);
    command.add(predicate);
    command.add(makeELMt(mt));

    return this.converseObject(command);
  }

  /**
   * Returns the first arg1 term from gafs having the specified predicate and arg2 values.
   * 
   * @param predicate the given predicate
   * @param arg2 the given arg2 term
   * @param mt the inference microtheory
   * 
   * @return the first arg1 term from gafs having the specified predicate and arg2 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getArg1(String predicate, 
                        String arg2, 
                        String mt)
                 throws IOException, UnknownHostException, CycApiException {
    return getArg1(getKnownConstantByName(predicate), 
                   getKnownConstantByName(arg2), 
                   getKnownConstantByName(mt));
  }

  /**
   * Returns the first arg1 term from gafs having the specified predicate and arg2 values.
   * 
   * @param predicate the given predicate
   * @param arg2 the given arg2 term
   * @param mt the inference microtheory
   * 
   * @return the first arg1 term from gafs having the specified predicate and arg2 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getArg1(CycFort predicate, 
                        CycFort arg2, 
                        CycObject mt)
                 throws IOException, UnknownHostException, CycApiException {
    CycList answer = getArg1s(predicate, 
                              arg2, 
                              makeELMt(mt));

    if (answer.size() > 0) {
      return answer.get(0);
    }
    else {
      return null;
    }
  }

  /**
   * Returns the list of arg1 terms from gafs having the specified predicate and arg2 values.
   * 
   * @param predicate the given predicate
   * @param arg2 the given arg2 term
   * @param mt the inference microtheory
   * 
   * @return the list of arg1 terms from gafs having the specified predicate and arg2 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getArg1s(String predicate, 
                          String arg2, 
                          String mt)
                   throws IOException, UnknownHostException, CycApiException {
    return getArg1s(getKnownConstantByName(predicate), 
                    getKnownConstantByName(arg2), 
                    getKnownConstantByName(mt));
  }

  /**
   * Returns the list of arg1 terms from gafs having the specified predicate and arg2 values.
   * 
   * @param predicate the given predicate
   * @param arg2 the given arg2 term
   * @param mt the inference microtheory
   * 
   * @return the list of arg1 terms from gafs having the specified predicate and arg2 values
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getArg1s(CycFort predicate, 
                          CycFort arg2, 
                          CycObject mt)
                   throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(predicate);

    CycVariable variable = CycObjectFactory.makeCycVariable(
                                 "?arg1");
    query.add(variable);
    query.add(arg2);

    return askWithVariable(query, 
                           variable, 
                           makeELMt(mt));
  }

  /**
   * Returns the Cyc image ID.
   * 
   * @return the Cyc image ID string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public String getCycImageID()
                       throws IOException, UnknownHostException, CycApiException {
    CycList query = new CycList();
    query.add(CycObjectFactory.makeCycSymbol("cyc-image-id"));

    return converseString(query);
  }

  /**
   * Returns the list of assertions contained in the given mt.
   * 
   * @param mt the given microtheory
   * 
   * @return the list of assertions contained in the given mt
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getAllAssertionsInMt(CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("gather-mt-index"));
    command.add(makeELMt(mt));

    return converseList(command);
  }

  /**
   * Unasserts all assertions from the given mt, with a transcript record of the unassert
   * operation.
   * 
   * @param mt the microtheory from which to delete all its assertions
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void unassertMtContentsWithTranscript(CycObject mt)
                                        throws IOException, UnknownHostException, CycApiException {
    CycList assertions = getAllAssertionsInMt(mt);
    Iterator iter = assertions.iterator();

    while (iter.hasNext()) {
      CycAssertion assertion = (CycAssertion) iter.next();
      String command = wrapBookkeeping("(ke-unassert-now " + assertion.stringApiValue() + 
                                       makeELMt(mt).stringApiValue() + ")");
      converseVoid(command);
    }
  }

  /**
   * Unasserts all assertions from the given mt, without a transcript record of the unassert
   * operation.
   * 
   * @param mt the microtheory from which to delete all its assertions
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void unassertMtContentsWithoutTranscript(CycObject mt)
                                           throws IOException, UnknownHostException, 
                                                  CycApiException {
    CycList assertions = getAllAssertionsInMt(mt);
    Iterator iter = assertions.iterator();

    while (iter.hasNext()) {
      CycAssertion assertion = (CycAssertion) iter.next();
      String command = "(cyc-unassert " + assertion.stringApiValue() + 
                       makeELMt(mt).stringApiValue() + "))";
      converseVoid(command);
    }
  }

  /**
   * Unasserts all assertions from the given mt having the given predicate and arg1, without a
   * transcript record of the unassert operation.
   * 
   * @param predicate the given predicate or null to match all predicates
   * @param arg1 the given arg1
   * @param mt the microtheory from which to delete the matched assertions
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public void unassertMatchingAssertionsWithoutTranscript(CycFort predicate, 
                                                          Object arg1, 
                                                          CycObject mt)
                                                   throws IOException, UnknownHostException, 
                                                          CycApiException {
    CycList assertions = getAllAssertionsInMt(mt);
    Iterator iter = assertions.iterator();

    while (iter.hasNext()) {
      CycAssertion assertion = (CycAssertion) iter.next();
      CycList sentence = assertion.getFormula();

      if (sentence.size() < 2) {
        continue;
      }

      if (!(arg1.equals(sentence.second()))) {
        continue;
      }

      if ((predicate != null) && (!(predicate.equals(sentence.first())))) {
        continue;
      }

      String command = "(cyc-unassert " + assertion.stringApiValue() + 
                       makeELMt(mt).stringApiValue() + "))";
      converseVoid(command);
    }
  }

  /**
   * Returns the list of Cyc terms whose denotation matches the given English string.
   * 
   * @param denotationString the given English denotation string
   * 
   * @return the list of Cyc terms whose denotation matches the given English string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getDenotsOfString(String denotationString)
                            throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("denots-of-string"));
    command.add(denotationString);

    return converseList(command);
  }

  /**
   * Returns the list of Cyc terms whose denotation matches the given English string and which are
   * instances of any of the given collections.
   * 
   * @param denotationString the given English denotation string
   * @param collections the given list of collections
   * 
   * @return the list of Cyc terms whose denotation matches the given English string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getDenotsOfString(String denotationString, 
                                   CycList collections)
                            throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("denots-of-string"));
    command.add(denotationString);

    CycList terms = converseList(command);
    CycList result = new CycList();
    Iterator collectionsIterator = collections.iterator();

    while (collectionsIterator.hasNext()) {
      CycFort collection = (CycFort) collectionsIterator.next();
      Iterator termsIter = terms.iterator();

      while (termsIter.hasNext()) {
        CycFort term = (CycFort) termsIter.next();

        if (this.isa(term, 
                     collection)) {
          result.add(term);
        }
      }
    }

    return result;
  }

  /**
   * Returns the list of Cyc terms whose denotation matches the given English multi-word string.
   * 
   * @param multiWordDenotationString the given English denotation multi-word string
   * 
   * @return the list of Cyc terms whose denotation matches the given English multi-word string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getMWSDenotsOfString(CycList multiWordDenotationString)
                               throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("mws-denots-of-string"));
    command.addQuoted(multiWordDenotationString);

    return converseList(command);
  }

  /**
   * Returns the list of Cyc terms whose denotation matches the given English multi-word string and
   * which are instances of any of the given collections.
   * 
   * @param multiWordDenotationString the given English denotation string
   * @param collections the given list of collections
   * 
   * @return the list of Cyc terms whose denotation matches the given English multi-word string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList getMWSDenotsOfString(CycList multiWordDenotationString, 
                                      CycList collections)
                               throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("mws-denots-of-string"));
    command.addQuoted(multiWordDenotationString);

    CycList terms = converseList(command);
    CycList result = new CycList();
    Iterator collectionsIterator = collections.iterator();

    while (collectionsIterator.hasNext()) {
      CycFort collection = (CycFort) collectionsIterator.next();
      Iterator termsIter = terms.iterator();

      while (termsIter.hasNext()) {
        CycFort term = (CycFort) termsIter.next();

        if (this.isa(term, 
                     collection)) {
          result.add(term);
        }
      }
    }

    return result;
  }

  /**
   * Returns true if the given symbol is defined as an api function.
   * 
   * @param symbolName the candidate api function symbol name
   * 
   * @return true if the given symbol is defined as an api function
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isFunctionBound(String symbolName)
                          throws IOException, UnknownHostException, CycApiException {
    CycSymbol cycSymbol = CycObjectFactory.makeCycSymbol(
                                symbolName);

    return isFunctionBound(cycSymbol);
  }

  /**
   * Returns true if the given symbol is defined as an api function.
   * 
   * @param cycSymbol the candidate api function symbol
   * 
   * @return rue if the given symbol is defined as an api function
   * 
   * @throws UnknownHostException if cyc server host not found on the network
   * @throws IOException if a data communication error occurs
   * @throws CycApiException if the api request results in a cyc server error
   */
  public boolean isFunctionBound(CycSymbol cycSymbol)
                          throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("boolean"));

    CycList command1 = new CycList();
    command1.add(CycObjectFactory.makeCycSymbol("fboundp"));
    command1.addQuoted(cycSymbol);
    command.add(command1);

    return converseBoolean(command);
  }

  /**
   * Returns the Heuristic Level (HL) object represented by the given string.
   * 
   * @param string the string which represents a number, quoted string, constant, naut or nart
   * 
   * @return the Heuristic Level (HL) object represented by the given string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getHLCycTerm(String string)
                      throws IOException, UnknownHostException, CycApiException {
    return converseObject("(canonicalize-term '" + string + ")");
  }

  /**
   * Returns the Epistimological Level (EL) object represented by the given string.
   * 
   * @param string the string which represents a number, quoted string, constant, naut or nart
   * 
   * @return the Epistimological Level (EL)object represented by the given string
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public Object getELCycTerm(String string)
                      throws IOException, UnknownHostException, CycApiException {
    return converseObject("(identity '" + string + ")");
  }

  /**
   * Returns a random constant.
   * 
   * @return a random constant
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycConstant getRandomConstant()
                                throws IOException, UnknownHostException, CycApiException {
    return (CycConstant) converseObject("(random-constant)");
  }

  /**
   * Returns a random nart (Non-Atomic Reified Term).
   * 
   * @return a random nart (Non-Atomic Reified Term)
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycNart getRandomNart()
                        throws IOException, UnknownHostException, CycApiException {
    return (CycNart) converseObject("(random-nart)");
  }

  /**
   * Returns a random assertion.
   * 
   * @return a random assertion
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycAssertion getRandomAssertion()
                        throws IOException, UnknownHostException, CycApiException {
    return (CycAssertion) converseObject("(random-assertion)");
  }

  /**
   * Returns the given list with EL NARTS transformed to CycNart objects.
   * 
   * @param cycList the given list
   * 
   * @return the given list with EL NARTS transformed to CycNart objects
   * 
   * @throws IOException if a communications error occurs
   * @throws UnknownHostException if the Cyc server cannot be found
   * @throws CycApiException if the Cyc server returns an error
   */
  public CycList canonicalizeList(CycList cycList)
                           throws IOException, UnknownHostException, CycApiException {
    CycList canonicalList = new CycList();
    Iterator iter = cycList.iterator();

    while (iter.hasNext()) {
      Object obj = iter.next();

      if (obj instanceof CycList)
        canonicalList.add(getHLCycTerm(((CycList) obj).cyclify()));
      else if (obj instanceof CycNart)
        canonicalList.add(getHLCycTerm(((CycNart) obj).cyclify()));
      else
        canonicalList.add(obj);
    }

    return canonicalList;
  }
  
  /**
   * Gets the assertion date for the given assertion, or zero if the date is not available.
   *
   * @return the assertion date for the given assertion
   */
  public Long getAssertionDate(CycAssertion cycAssertion)
                        throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("asserted-when"));
    command.addQuoted(cycAssertion);
    Object date = converseObject(command);
    if (date instanceof Integer)
      return new Long(((Integer) date).longValue());
    if (date instanceof Long)
      return (Long) date;
    if (date.equals(CycObjectFactory.nil))
      return new Long(0);
    else
      throw new CycApiException("unexpected type of date returned " + date.toString());
  }
  
  /**
   * Returns true if the given HL formula and microtheory correspond to a valid
   * assertion in that microtheory.
   * 
   * @param hlFormula the given HL formula
   * @param mt the candidate assertion microtheory
   */
  public boolean isAssertionValid(CycList hlFormula, CycFort mt) 
                        throws IOException, UnknownHostException, CycApiException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("find-assertion"));
    command.addQuoted(hlFormula);
    command.add(mt);
    Object response = converseObject(command);
    return ! response.equals(CycObjectFactory.nil);
  }
  
  /** Asserts that the given term is dependent upon the given independent term.  If the latter is
   * killed, then the truth maintenance kills the dependent term.
   *
   * @param dependentTerm the dependent term
   * @param independentTerm the independent term
   * @param mt the assertion microtheory
   */
  public void assertTermDependsOn(final CycFort dependentTerm, final CycFort independentTerm, final CycFort mt)  throws IOException, CycApiException {
    // assert (#$termDependsOn <dependentTerm> <independentTerm>) in #$UniversalVocabularyMt
    assertGaf(mt, getKnownConstantByGuid("bdf02d74-9c29-11b1-9dad-c379636f7270"), dependentTerm, independentTerm);
  }
  
  /** Asserts that the given term is defined in the given mt.  If the mt is
   * subsequently killed, then the truth maintenance kills the dependent term.
   *
   * @param dependentTerm the dependent term
   * @param mt the defining microtheory
   */
  public void assertDefiningMt(final CycFort dependentTerm, final CycFort mt)  throws IOException, CycApiException {
    // assert (#$definingMt <dependentTerm> <mt>) in #$BaseKB
    assertGaf(baseKB, getKnownConstantByGuid("bde5ec9c-9c29-11b1-9dad-c379636f7270"), dependentTerm, mt);
  }
  
  /** Returns the XML datetime string corresponding to the given CycL date
   *
   * @param date the date naut
   * @return the XML datetime string corresponding to the given CycL date
   */
  public String xmlDatetimeString(final CycList date)  throws IOException, CycApiException {
    //// Preconditions
    assert date != null : "date cannot be null";
    assert isa(date, getKnownConstantByName("Date")) : date.cyclify() + " must be a Date";
    
    final CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("cyc-date-encode-string"));
    command.add("XML-datetime");
    command.addQuoted(date);
    String xmlDateString = converseString(command);
    if (xmlDateString.endsWith("TNIL:NIL:NILZ"))
      return xmlDateString.substring(0, xmlDateString.length() - 13);
    else
      return xmlDateString;
  }
  
  /** Initializes the query properties. */
  public void initializeQueryProperties() throws IOException, CycApiException {
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allowed-rules"), CycObjectFactory.makeCycSymbol(":all"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":result-uniqueness"), CycObjectFactory.makeCycSymbol(":bindings"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allow-hl-predicate-transformation?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allow-unbound-predicate-transformation?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allow-evaluatable-predicate-transformation?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":intermediate-step-validation-level"), CycObjectFactory.makeCycSymbol(":all"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":negation-by-failure?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allow-indeterminate-results?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allow-abnormality-checking?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":disjunction-free-el-vars-policy"), CycObjectFactory.makeCycSymbol(":compute-intersection"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":allowed-modules"), CycObjectFactory.makeCycSymbol(":all"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":completeness-minimization-allowed?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":direction"), CycObjectFactory.makeCycSymbol(":backward"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":equality-reasoning-method"), CycObjectFactory.makeCycSymbol(":czer-equal"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":equality-reasoning-domain"), CycObjectFactory.makeCycSymbol(":all"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":max-problem-count"), new Long(100000));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":transformation-allowed?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":add-restriction-layer-of-indirection?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":evaluate-subl-allowed?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":rewrite-allowed?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":abduction-allowed?"), CycObjectFactory.nil);
    // dynamic query properties
    queryProperties.put(CycObjectFactory.makeCycSymbol(":max-number"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":max-time"), new Integer(30));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":max-transformation-depth"), new Integer(0));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":block?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":max-proof-depth"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":cache-inference-results?"), CycObjectFactory.nil);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":answer-language"), CycObjectFactory.makeCycSymbol(":el"));
    queryProperties.put(CycObjectFactory.makeCycSymbol(":continuable?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":browsable?"), CycObjectFactory.t);
    queryProperties.put(CycObjectFactory.makeCycSymbol(":productivity-limit"), new Long(2000000));
    
    final Object[] queryPropertySymbols = queryProperties.keySet().toArray();
    final int queryPropertySymbols_length = queryPropertySymbols.length;
    for (int i = 0; i < queryPropertySymbols_length; i++) {
      final CycSymbol queryPropertySymbol = (CycSymbol) queryPropertySymbols[i];
      final CycList command = new CycList();
      command.add(CycObjectFactory.makeCycSymbol("query-property-p"));
      command.addQuoted(queryPropertySymbol);
      if (! converseBoolean(command))
        throw new CycApiException(queryPropertySymbol.toString() + " is not a query-property-p");
    }
  }
  
  /** Returns the default HL query propoerties.
   *
   * @return the default HL query propoerties
   */
  public HashMap getHLQueryProperties() {
    return queryProperties;
  }
  
}
