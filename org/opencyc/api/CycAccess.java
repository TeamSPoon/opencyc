package org.opencyc.api;

import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.oro.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.constraintsolver.*;
import org.opencyc.util.*;

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
    static HashMap cycAccessInstances = new HashMap();

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
    public static final boolean DEFAULT_PERSISTENT_CONNECTION = PERSISTENT_CONNECTION;

    /**
     * Parameter indicating whether the OpenCyc api should use one TCP socket for the entire
     * session, or if the socket is created and then closed for each api call.
     */
    public boolean persistentConnection;

    private boolean trace = false;
    private String hostName;
    private int port;
    private int communicationMode;
    private static final Integer OK_RESPONSE_CODE = new Integer(200);

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
    protected CycConnection cycConnection;

    /**
     * Constructs a new CycAccess object.
     */
    public CycAccess() throws IOException, UnknownHostException {
        this(CycConnection.DEFAULT_HOSTNAME,
             CycConnection.DEFAULT_BASE_PORT,
             CycConnection.DEFAULT_COMMUNICATION_MODE,
             CycAccess.DEFAULT_PERSISTENT_CONNECTION);
    }
    /**
     * Constructs a new CycAccess object given a host name, port, communication mode and persistence indicator.
     *
     * @param hostName the host name
     * @param port the TCP socket port number
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public CycAccess(String hostName, int port, int communicationMode, boolean persistentConnection)
        throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.hostName = hostName;
        this.port = port;
        this.communicationMode = communicationMode;
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection(hostName, port, communicationMode, this);
        initializeConstants();
    }

    /**
     * Returns the <tt>CycAccess</tt> object for this thread.
     *
     * @return the <tt>CycAccess</tt> object for this thread
     */
    public static CycAccess current() {
        CycAccess cycAccess = (CycAccess) cycAccessInstances.get(Thread.currentThread());
        if (cycAccess == null)
            throw new RuntimeException("No CycAccess object for this thread");
        return cycAccess;
    }


    /**
     * Turns on the diagnostic trace of socket messages.
     */
    public void traceOn() {
        cycConnection.trace = true;
    }

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff() {
        cycConnection.trace = false;
    }

    /**
     * Returns the CycConnection object.
     *
     * @return the CycConnection object
     */
    public CycConnection getCycConnection() {
        return cycConnection;
    }

    /**
     * Closes the CycConnection object.
     */
    public void close() throws IOException {
        if (cycConnection != null)
            cycConnection.close();
        cycAccessInstances.remove(Thread.currentThread());
    }

    /**
     * Converses with Cyc to perform an API command.  Creates a new connection for this command
     * if the connection is not persistent.
     *
     * @param command the command string or CycList
     */
    private Object [] converse(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        if (! persistentConnection)
            cycConnection = new CycConnection(hostName, port, communicationMode, this);
        response = cycConnection.converse(command);
        if (! persistentConnection)
            cycConnection.close();
        return response;
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as an object.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public Object converseObject(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE))
            return response[1];
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a list.  The symbol
     * nil is returned as the empty list.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public CycList converseList(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE))
            if (response[1].equals(CycSymbol.nil))
                return new CycList();
            else
                return (CycList) response[1];
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a String.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public String converseString(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (! (response[1] instanceof String))
                throw new RuntimeException("Expected String but received (" + response[1].getClass() + ") " +
                                           response[1] + "\n in response to command " + command);
            return (String) response[1];
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a boolean.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public boolean converseBoolean(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (response[1].toString().equals("T"))
                return true;
            else
                return false;
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as an int.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public int converseInt(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            return (new Integer(response[1].toString())).intValue();
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is void.
     *
     * @param command the command string or CycList
     */
    public void converseVoid(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.FALSE))
            throw new IOException(response[1].toString());
    }

    /**
     * Sets the *print-readable-narts* feature on.
     */
    public void setReadableNarts (String guid)  throws IOException, UnknownHostException {
        converseVoid("(csetq *print-readable-narts t)");
    }

    /**
     * Initializes common cyc constants.
     */
    private void initializeConstants()    throws IOException, UnknownHostException {
        if (baseKB == null)
            baseKB = getKnownConstantByName("BaseKB");
        if (isa == null)
            isa = getKnownConstantByName("isa");
        if (genls == null)
            genls = getKnownConstantByName("genls");
        if (genlMt == null)
            genlMt = getKnownConstantByName("genlMt");
        if (comment == null)
            comment = getKnownConstantByName("comment");
        if (collection == null)
            collection = getKnownConstantByName("Collection");
        if (binaryPredicate == null)
            binaryPredicate = getKnownConstantByName("BinaryPredicate");
        if (elementOf == null)
            elementOf = getKnownConstantByName("elementOf");
        if (and == null)
            and = getKnownConstantByName("and");
        if (or == null)
            or = getKnownConstantByName("or");
        if (numericallyEqual == null)
            numericallyEqual = getKnownConstantByName("numericallyEqual");
        if (plusFn == null)
            plusFn = getKnownConstantByName("PlusFn");
        if (different == null)
            different = getKnownConstantByName("different");
        if (thing == null)
            thing = getKnownConstantByName("Thing");
    }

    /**
     * Gets a known CycConstant by using its constant name.
     *
     * @param constantName the name of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise throw an exception
     */
    public CycConstant getKnownConstantByName (String constantName)
        throws IOException, UnknownHostException {
        CycConstant cycConstant = getConstantByName(constantName);
        if (cycConstant == null)
            throw new RuntimeException("Expected constant not found " + constantName);
        return cycConstant;
    }

    /**
     * Gets a CycConstant by using its constant name.
     *
     * @param constantName the name of the constant to be instantiated
     * @return the complete <tt>CycConstant</tt> if found, otherwise return null
     */
    public CycConstant getConstantByName (String constantName)
        throws IOException, UnknownHostException {
        String name = constantName;
        if (constantName.startsWith("#$"))
            name = name.substring(2);
        CycConstant answer = CycConstant.getCache(name);
        if (answer != null)
            return answer;
        answer = new CycConstant();
        answer.name = name;
        Integer id = getConstantId(name);
        if (id == null)
            return null;
        answer.id = id;
        answer.guid = getConstantGuid(name);
        CycConstant.addCache(answer);
        return answer;
    }

    /**
     * Gets the ID for the given CycConstant.
     *
     * @param cycConstant the <tt>CycConstant</tt> object for which the id is sought
     * @return the ID for the given CycConstant, or null if the constant does not exist.
     */
    public Integer getConstantId (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return getConstantId(cycConstant.name);
    }

    /**
     * Gets the ID for the given constant name.
     *
     * @param constantName the name of the constant object for which the id is sought
     * @return the ID for the given constant name, or null if the constant does not exist.
     */
    public Integer getConstantId (String constantName)  throws IOException, UnknownHostException {
        String command = "(boolean (find-constant \"" + constantName + "\"))";
        boolean constantExists = converseBoolean(command);
        if (constantExists) {
            command = "(constant-id (find-constant \"" + constantName + "\"))";
            return new Integer(converseInt(command));
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
     */
    public Guid getConstantGuid (CycConstant cycConstant)
        throws IOException, UnknownHostException {
        return getConstantGuid(cycConstant.name);
    }

    /**
     * Gets the Guid for the given constant name, raising an exception if the constant does not
     * exist.
     *
     * @param constantName the name of the constant object for which the Guid is sought
     * @return the Guid for the given CycConstant
     */
    public Guid getConstantGuid (String constantName)
        throws IOException, UnknownHostException {
        String command = "(guid-to-string (constant-guid (find-constant \"" +
                         constantName + "\")))";
        return Guid.makeGuid(converseString(command));
    }

    /**
     * Gets the Guid for the given constant id.
     *
     * @param id the id of the <tt>CycConstant</tt> whose guid is sought
     * @return the Guid for the given CycConstant
     */
    public Guid getConstantGuid (Integer id)
        throws IOException, UnknownHostException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("guid-to-string"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("constant-guid"));
        CycList command2 = new CycList();
        command1.add(command2);
        command2.add(CycSymbol.makeCycSymbol("find-constant-by-id"));
        command2.add(id);
        return Guid.makeGuid(converseString(command));
    }

    /**
     * Gets a <tt>CycConstant</tt> by using its ID.
     *
     * @param id the id of the <tt>CycConstant</tt> sought
     * @return the <tt>CycConstant</tt> if found or <tt>null</tt> if not found
     */
    public CycConstant getConstantById (Integer id)  throws IOException, UnknownHostException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("boolean"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("find-constant-by-id"));
        command1.add(id);
        boolean constantExists = converseBoolean(command);
        if (! constantExists)
            return null;
        CycConstant answer = new CycConstant();
        answer.name = getConstantName(id);
        answer.id = id;
        answer.guid = getConstantGuid(id);
        CycConstant.addCache(answer);
        return answer;
    }

    /**
     * Gets the name for the given constant id.
     *
     * @param id the id of the constant object for which the name is sought
     * @return the name for the given CycConstant
     */
    public String getConstantName (Integer id)
        throws IOException, UnknownHostException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("constant-name"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("find-constant-by-id"));
        command1.add(id);
        return converseString(command);
    }

    /**
     * Gets the name for the given variable id.
     *
     * @param id the id of the variable object for which the name is sought
     * @return the name for the given CycVariable
     */
    public String getVariableName (Integer id)
        throws IOException, UnknownHostException {
        // Optimized for the binary api.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("variable-name"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("find-variable-by-id"));
        command1.add(id);
        return converseString(command);
    }

    /**
     * Gets a CycConstant by using its GUID.
     */
    public CycConstant getConstantByGuid (Guid guid)  throws IOException, UnknownHostException {
        String command = "(boolean (find-constant-by-guid (string-to-guid \"" + guid + "\")))";
        boolean constantExists = converseBoolean(command);
        if (! constantExists)
            return null;
        command = "(constant-name (find-constant-by-guid (string-to-guid \"" + guid + "\")))";
        String constantName = this.converseString(command);
        return this.getConstantByName(constantName);
    }

    /**
     * Completes the instantiation of objects contained in the given <tt>CycList</tt>. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved if the constant is
     * not cached.
     *
     * @param object the <tt>CycConstant</tt> to be completed, or the <tt>Object</tt> whose
     * embedded constants are to be completed
     * @return the completed object, or a reference to a cached instance
     */
    public Object completeObject (Object object) throws IOException, UnknownHostException {
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
     */
    public CycConstant completeCycConstant (CycConstant cycConstant) throws IOException, UnknownHostException {
        cycConstant.name = getConstantName(cycConstant.id);
        CycConstant cachedConstant = CycConstant.getCache(cycConstant.name);
        if (cachedConstant == null) {
            cycConstant.guid = getConstantGuid(cycConstant.id);
            CycConstant.addCache(cycConstant);
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
     */
    public CycVariable completeCycVariable (CycVariable cycVariable)
        throws IOException, UnknownHostException {
        if (cycVariable.name == null)
            cycVariable.name = getVariableName(cycVariable.id);
        CycVariable cachedVariable = CycVariable.getCache(cycVariable.name);
        if (cachedVariable == null) {
            CycVariable.addCache(cycVariable);
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
     */
    public CycList completeCycList (CycList cycList) throws IOException, UnknownHostException {
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
     * if the constant is not cached.
     *
     * @param cycNart the <tt>CycNart</tt> whose constants are to be completed
     * @param the completed <tt>CycNart</tt> object
     */
    public CycNart completeCycNart (CycNart cycNart) throws IOException, UnknownHostException {
        return getCycNartById(cycNart.id);
    }

    /**
     * Completes the instantiation of a <tt>CycAssertion</tt> returned by the binary api. The
     * binary api sends only constant ids, and the constant names and guids must be retrieved
     * if the constant is not cached.
     *
     * @param cycAssertion the <tt>CycAssertion</tt> whose constants are to be completed
     * @param the completed <tt>CycAssertion</tt> object
     */
    public CycAssertion completeCycAssertion (CycAssertion cycAssertion) throws IOException, UnknownHostException {
        return getAssertionById(cycAssertion.getId());
    }

    /**
     * Gets a CycNart by using its id.
     */
    public CycNart getCycNartById (Integer id)  throws IOException, UnknownHostException {
        CycNart cycNart = CycNart.getCache(id);
        if (cycNart != null) {
            return cycNart;
        }
        else {
            cycNart = new CycNart();
            cycNart.id = id;
        }
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("nart-el-formula"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("find-nart-by-id"));
        command1.add(id);
        CycList formula = converseList(command);
        cycNart.setFunctor((CycFort) formula.first());
        cycNart.setArguments(formula.rest());
        return cycNart;
    }

    /**
     * Gets a CycAssertion by using its id.
     */
    public CycAssertion getAssertionById (Integer id)  throws IOException, UnknownHostException {
        CycAssertion cycAssertion = CycAssertion.getCache(id);
        if (cycAssertion != null) {
            if (cycAssertion.getFormula() != null)
                return cycAssertion;
        }
        else
            cycAssertion = new CycAssertion(id);
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("assertion-el-formula"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("find-assertion-by-id"));
        command1.add(id);
        cycAssertion.setFormula(converseList(command));
        return cycAssertion;
    }

    /**
     * Gets a CycNart object from a Cons object that lists the names of
     * its functor and its arguments.
     */
    public CycNart getCycNartFromCons(CycList elCons) {
        return new CycNart(elCons);
    }

    /**
     * Returns true if CycConstant BINARYPREDICATE relates CycFort ARG1 and CycFort ARG2.
     */
    public boolean predicateRelates (CycConstant binaryPredicate,
                                     CycFort arg1,
                                     CycFort arg2)  throws IOException, UnknownHostException {
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
            throw new IOException(response[1].toString());
    }

    /**
     * Gets the plural generated phrase for a CycFort (intended for collections).
     */
    public String getPluralGeneratedPhrase (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + " '(#$plural)))");
    }

    /**
     * Gets the singular generated phrase for a CycFort (intended for individuals).
     */
    public String getSingularGeneratedPhrase (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + " '(#$singular)))");
    }

    /**
     * Gets the default generated phrase for a CycFort (intended for predicates).
     */
    public String getGeneratedPhrase (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets the paraphrase for a Cyc assertion.
     */
    public String getParaphrase (CycList assertion)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase '" + assertion.cyclify() + "))");
    }

    /**
     * Gets the comment for a CycConstant.  Embedded quotes are replaced by spaces.
     */
    public String getComment (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseString("(string-substitute \" \" \"\\\"\" (with-all-mts (comment " + cycConstant.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the isas for a CycFort.
     */
    public CycList getIsas (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (isa " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the directly asserted true genls for a CycFort collection.
     */
    public CycList getGenls (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (genls " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the minimum (most specific) genls for a CycFort collection.
     */
    public CycList getMinGenls (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (min-genls " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the directly asserted true specs for a CycFort collection.
     */
    public CycList getSpecs (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the least specific specs for a CycFort collection.
     */
    public CycList getMaxSpecs (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (max-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the direct genls of the direct specs for a CycFort collection.
     */
    public CycList getGenlSiblings (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (genl-siblings " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the siblings (direct specs of the direct genls) for a CycFort collection.
     */
    public CycList getSiblings (CycFort cycFort)  throws IOException, UnknownHostException {
        return getSpecSiblings(cycFort);
    }

    /**
     * Gets a list of the siblings (direct specs of the direct genls) for a CycFort collection.
     */
    public CycList getSpecSiblings (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (spec-siblings " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of all of the direct and indirect genls for a CycFort collection.
     */
    public CycList getAllGenls (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(all-genls-in-any-mt " + cycFort.stringApiValue() + ")");
    }

    /**
     * Gets a list of all of the direct and indirect specs for a CycFort collection.
     */
    public CycList getAllSpecs (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of all of the direct and indirect genls for a CycFort SPEC which are also specs of
     * CycFort GENL.
     */
    public CycList getAllGenlsWrt (CycFort spec, CycFort genl )  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-genls-wrt " + spec.stringApiValue() + " " + genl.stringApiValue() + ")))");
    }

    /**
     * Gets a list of all of the dependent specs for a CycFort collection.  Dependent specs are those direct and
     * indirect specs of the collection such that every path connecting the spec to a genl of the collection passes
     * through the collection.  In a typical taxomonmy it is expected that all-dependent-specs gives the same
     * result as all-specs.
     */
    public CycList getAllDependentSpecs (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-dependent-specs " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list with the specified number of sample specs of a CycFort collection.  Attempts to return
     * leaves that are maximally differet with regard to their all-genls.
     */
    public CycList getSampleLeafSpecs (CycFort cycFort, int numberOfSamples)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (sample-leaf-specs " + cycFort.stringApiValue() + " " + numberOfSamples + "))");
    }

    /**
     * Returns true if CycFort SPEC is a spec of CycFort GENL.
     */
    public boolean isSpecOf (CycFort spec, CycFort genl)  throws IOException, UnknownHostException {
        return isGenlOf(genl, spec);
    }

    /**
     * Returns true if CycFort GENL is a genl of CycFort SPEC.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
     */
    public boolean isGenlOf (CycFort genl, CycFort spec)  throws IOException,
                                                                         UnknownHostException {
        return converseBoolean("(genl-in-any-mt? " + spec.stringApiValue() + " " + genl.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort GENL is a genl of CycFort SPEC, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycFort GENL is a genl of CycFort SPEC
     */
    public boolean isGenlOf_Cached (CycFort genl, CycFort spec)
        throws IOException,  UnknownHostException {
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
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are tacitly coextensional via mutual genls of each other.
     */
    public boolean areTacitCoextensional (CycFort collection1, CycFort collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (tacit-coextensional? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are asserted coextensional.
     */
    public boolean areAssertedCoextensional (CycFort collection1, CycFort collection2)  throws IOException, UnknownHostException {
        if (predicateRelates(getKnownConstantByName("coExtensional"), collection1, collection2))
            return true;
        else if (predicateRelates(getKnownConstantByName("coExtensional"), collection2, collection1))
            return true;
        else
            return false;
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 intersect with regard to all-specs.
     */
    public boolean areIntersecting (CycFort collection1, CycFort collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (collections-intersect? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are in a hierarchy.
     */
    public boolean areHierarchical (CycFort collection1, CycFort collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (hierarchical-collections? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Gets a list of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenl("Dog", "Animal") -->
     * "(((#$genls #$Dog #$CanineAnimal) :TRUE)
     *    (#$genls #$CanineAnimal #$NonPersonAnimal) :TRUE)
     *    (#$genls #$NonPersonAnimal #$Animal) :TRUE))
     *
     */
    public CycList getWhyGenl (CycFort spec, CycFort genl)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-genl? " + spec.stringApiValue() + " " + genl.stringApiValue() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort GENL.
     * getWhyGenlParaphrase("Dog", "Animal") -->
     * "a dog is a kind of canine"
     * "a canine is a kind of non-human animal"
     * "a non-human animal is a kind of animal"
     *
     */
    public ArrayList getWhyGenlParaphrase (CycFort spec, CycFort genl)
        throws IOException, UnknownHostException {
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
     * Gets a list of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect.
     * see getWhyGenl
     */
    public CycList getWhyCollectionsIntersect (CycFort collection1,
                                                         CycFort collection2)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-collections-intersect? " +
                            collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycFort COLLECTION1 and a CycFort COLLECTION2 intersect.
     * see getWhyGenlParaphrase
     */
    public ArrayList getWhyCollectionsIntersectParaphrase (CycFort collection1,
                                                           CycFort collection2)
        throws IOException, UnknownHostException {
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
     * Gets a list of the collection leaves (most specific of the all-specs) for a CycFort collection.
     */
    public CycList getCollectionLeaves (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (collection-leaves " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets a list of the collections asserted to be disjoint with a CycFort collection.
     */
    public CycList getLocalDisjointWith (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (local-disjoint-with " + cycFort.stringApiValue() + "))");
    }

    /**
     * Returns true if CycFort COLLECION1 and CycFort COLLECTION2 are disjoint.
     */
    public boolean areDisjoint (CycFort collection1, CycFort collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (disjoint-with? " + collection1.stringApiValue() + " " + collection2.stringApiValue() + "))");
    }

    /**
     * Gets a list of the most specific collections (having no subsets) which contain a CycFort term.
     */
    public CycList getMinIsas (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (min-isa " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets a list of the instances (who are individuals) of a CycFort collection.
     */
    public CycList getInstances (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (instances " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets a list of the instance siblings of a CycFort, for all collections of which it is an instance.
     */
    public CycList getInstanceSiblings (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (instance-siblings " + cycFort.stringApiValue() + "))");
    }

    /**
     * Gets a list of the collections of which the CycFort is directly and indirectly an instance.
     */
    public CycList getAllIsa (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(all-isa-in-any-mt " + cycFort.stringApiValue() + ")");
    }

    /**
     * Gets a list of all the direct and indirect instances (individuals) for a CycFort collection.
     */
    public CycList getAllInstances (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(all-instances-in-all-mts " + cycFort.stringApiValue() + ")");
    }

    /**
     * Returns true if CycFort TERM is a instance of CycFort COLLECTION, defaulting to all microtheories.
     *
     * @param term the term
     * @param collection the collection
     * @return <tt>true</tt> if CycFort TERM is a instance of CycFort COLLECTION
     */
    public boolean isa (CycFort term, CycFort collection)  throws IOException, UnknownHostException {
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
     */
    public boolean isa (CycFort term, CycFort collection, CycFort mt)
        throws IOException, UnknownHostException {
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("isa?"));
        command.add(term.cycListApiValue());
        command.add(collection.cycListApiValue());
        command.add(mt.cycListApiValue());
        return converseBoolean(command);
    }

    /**
     * Gets a list of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyIsa("Brazil", "Country") -->
     * "(((#$isa #$Brazil #$IndependentCountry) :TRUE)
     *    (#$genls #$IndependentCountry #$Country) :TRUE))
     *
     */
    public CycList getWhyIsa (CycFort spec, CycFort genl)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-isa? " + spec.stringApiValue() + " " + genl.stringApiValue() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycFort TERM is an instance of CycFort COLLECTION.
     * getWhyGenlParaphase("Brazil", "Country") -->
     * "Brazil is an independent country"
     * "an  independent country is a kind of country"
     *
     */
    public ArrayList getWhyIsaParaphrase (CycFort spec, CycFort genl)  throws IOException {
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
     * Gets a list of the genlPreds for a CycConstant predicate.
     */
    public CycList getGenlPreds (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (genl-predicates " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the arg1Isas for a CycConstant predicate.
     */
    public CycList getArg1Isas (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (arg1-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the arg2Isas for a CycConstant predicate.
     */
    public CycList getArg2Isas (CycConstant predicate)  throws IOException, UnknownHostException {
       return converseList("(remove-duplicates (with-all-mts (arg2-isa " + predicate.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the argNIsas for a CycConstant predicate.
     */
    public CycList getArgNIsas (CycConstant predicate, int argPosition)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (argn-isa " + predicate.stringApiValue() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets a list of the argNGenls for a CycConstant predicate.
     */
    public CycList getArgNGenls (CycConstant predicate, int argPosition)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (argn-genl " + predicate.stringApiValue() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets a list of the arg1Formats for a CycConstant predicate.
     */
    public CycList getArg1Formats (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (arg1-format " + predicate.stringApiValue() + "))");
    }

    /**
     * Gets a list of the arg2Formats for a CycConstant predicate.
     */
    public CycList getArg2Formats (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (arg2-format " + predicate.stringApiValue() + "))");
    }

    /**
     * Gets a list of the disjointWiths for a CycFort.
     */
    public CycList getDisjointWiths (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (local-disjoint-with " + cycFort.stringApiValue() + ")))");
    }

    /**
     * Gets a list of the coExtensionals for a CycFort.  Limited to 120 seconds.
     */
    public CycList getCoExtensionals (CycFort cycFort)  throws IOException, UnknownHostException {
        CycList answer = converseList("(ask-template '?X '(#$coExtensional " + cycFort.stringApiValue() + " ?X) #$EverythingPSC nil nil 120)");
        answer.remove(cycFort);
        return answer;
    }

    /**
     * Returns true if cycConstant is a Collection.
     */
    public boolean isCollection (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Collection)");
    }

    /**
     * Returns true if cycConstant is a Collection, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param cycConstant the constant for determination as a Collection
     * @return <tt>true</tt> iff cycConstant is a Collection,
     */
    public boolean isCollection_Cached(CycFort cycFort)  throws IOException {
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
     */
    public boolean isIndividual (CycFort cycFort)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycFort.stringApiValue() + " #$Individual)");
    }

    /**
     * Returns true if cycConstant is a Function.
     */
    public boolean isFunction (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$Function-Denotational)");
    }

    /**
     * Returns true if cycConstant is an evaluatable predicate.
     */
    public boolean isEvaluatablePredicate (CycConstant predicate)  throws IOException, UnknownHostException {
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("with-all-mts"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("evaluatable-predicate?"));
        command1.add(predicate);
        return converseBoolean(command);
    }

    /**
     * Returns true if cycConstant is a Predicate.
     */
    public boolean isPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$Predicate)");
    }

    /**
     * Returns true if cycConstant is a UnaryPredicate.
     */
    public boolean isUnaryPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$UnaryPredicate)");
    }

    /**
     * Returns true if cycConstant is a BinaryPredicate.
     */
    public boolean isBinaryPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$BinaryPredicate)");
    }

    /**
     * Returns true if the candidate name is a valid CycConstant name.
     */
    public boolean isValidConstantName (String candidateName)  throws IOException, UnknownHostException {
        return converseBoolean("(new-constant-name-spec-p \"" + candidateName + "\")");
    }

    /**
     * Returns true if cycConstant is a PublicConstant.
     */
    public boolean isPublicConstant (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.stringApiValue() + " #$PublicConstant)");
    }

    /**
     * Gets a list of the public Cyc constants.
     */
    public CycList getPublicConstants ()  throws IOException, UnknownHostException {
        return converseList("(ask-template '?X '(#$isa ?X #$PublicConstant) #$EverythingPSC)");
    }

    /**
     * Kills a Cyc constant.  If CYCCONSTANT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     */
    public synchronized void kill (CycConstant cycConstant)   throws IOException, UnknownHostException {
        converseBoolean("(cyc-kill " + cycConstant.stringApiValue() + ")");
        CycConstant.removeCache(cycConstant);
    }

    public synchronized void kill (CycConstant[] cycConstants)   throws IOException, UnknownHostException {
        for (int i = 0; i < cycConstants.length; i++)
            kill(cycConstants[i]);
    }

    public synchronized void kill (ArrayList cycConstants)   throws IOException, UnknownHostException {
        for (int i = 0; i < cycConstants.size(); i++)
            kill((CycConstant) cycConstants.get(i));
    }

    /**
     * Kills a Cyc NART (Non Atomic Reified Term).  If CYCFORT is a microtheory, then
     * all the contained assertions are deleted from the KB, the Cyc Truth Maintenance System
     * (TML) will automatically delete any derived assertions whose sole support is the killed
     * term(s).
     */
    public synchronized  void kill (CycFort cycFort)   throws IOException, UnknownHostException {
        converseBoolean("(cyc-kill '" + cycFort.toString() + ")");
    }

    /**
     * Sets the value of the Cyclist, whose identity will be attached
     * via #$myCreator bookkeeping assertions to new KB entities created
     * in this session.
     */
    public void setCyclist (String cyclistName)   throws IOException, UnknownHostException {
        setCyclist(getConstantByName(cyclistName));
    }
    public void setCyclist (CycConstant cyclist) {
        this.cyclist = cyclist;
    }

    /**
     * Sets the value of the KE purpose, whose project name will be attached
     * via #$myCreationPurpose bookkeeping assertions to new KB entities
     * created in this session.
     */
    public void setKePurpose (String projectName)   throws IOException, UnknownHostException {
        setKePurpose(getConstantByName(projectName));
    }
    public void setKePurpose (CycConstant project) {
        this.project = project;
    }

    /**
     * Returns a with-bookkeeping-info macro expresssion.
     */
    private String withBookkeepingInfo () {
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
     * Creates a new permanent Cyc constant in the KB with the specified name.  The operation
     * will be added to the KB transcript for replication and archive.
     */
    public CycConstant createNewPermanent (String constantName)   throws IOException, UnknownHostException {
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
     * will be added to the KB transcript for replication and archive.  Alternative method
     * signatures accomodate various arities, and argument datatypes.
     */
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycFort arg2)   throws IOException, UnknownHostException {
        // (predicate <CycFort> <CycFort>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           String arg2)   throws IOException, UnknownHostException {
        // (predicate <CycFort> <String>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            "\"" + arg2 + "\")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycList arg2)   throws IOException, UnknownHostException {
        // (predicate <CycFort> <List>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2.stringApiValue() + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           int arg2)   throws IOException, UnknownHostException {
        // (predicate <CycFort> <int>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.stringApiValue() + " " +
            arg1.stringApiValue() + " " +
            arg2 + ")" +
            mt.stringApiValue() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycFort mt,
                           CycConstant predicate,
                           CycFort arg1,
                           CycFort arg2,
                           CycFort arg3)   throws IOException, UnknownHostException {
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
     * Assert a comment for the specified CycConstant in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     */
    public void assertComment (CycConstant cycConstant,
                               String comment,
                               CycFort mt)   throws IOException, UnknownHostException {
        assertGaf(mt, getKnownConstantByName("comment"), cycConstant, comment);
    }

    /**
     * Create a microtheory MT, with a comment, isa <mt type> and CycFort genlMts.
     * An existing microtheory with
     * the same name is killed first, if it exists.
     */
    public CycConstant createMicrotheory (String mtName,
                                          String comment,
                                          CycFort isaMt,
                                          ArrayList genlMts)   throws IOException, UnknownHostException {
        CycConstant mt = getConstantByName(mtName);
        if (mt != null) {
            this.kill(mt);
        }
        mt = this.createNewPermanent(mtName);
        assertComment(mt, comment, baseKB);
        assertGaf(baseKB, isa, mt, isaMt);
        Iterator iterator = genlMts.iterator();
        while (true) {
            if (! iterator.hasNext())
                break;
            CycFort aGenlMt = (CycFort) iterator.next();
            assertGaf(baseKB, genlMt, mt, aGenlMt);
        }
    return mt;
    }

    /**
     * Create a microtheory system for a new mt.  Given a root mt name, create a theory <Root>Mt,
     * create a vocabulary <Root>VocabMt, and a data <Root>DataMt.  Establish genlMt links for the
     * theory mt and data mt.  Assert that the theory mt is a genlMt of the WorldLikeOursCollectorMt.
     * Assert that the data mt is a genlMt of the collector CurrentWorldDataMt.
     */
    public CycConstant[] createMicrotheorySystem (String mtRootName,
                                                  String comment,
                                                  ArrayList genlMts) throws IOException, UnknownHostException {
        //traceOn();
        CycConstant[] mts = {null, null, null};
        String theoryMtName = mtRootName + "Mt";
        String vocabMtName = mtRootName + "VocabMt";
        String vocabMtComment = "The #$VocabularyMicrotheory for #$"+ theoryMtName;
        String dataMtName = mtRootName + "DataMt";
        String dataMtComment = "The #$DataMicrotheory for #$"+ theoryMtName;
        CycConstant worldLikeOursMt = this.getKnownConstantByName("WorldLikeOursCollectorMt");
        CycConstant currentWorldDataMt = this.getKnownConstantByName("CurrentWorldDataCollectorMt");
        CycConstant genlMt_Vocabulary = this.getKnownConstantByName("genlMt-Vocabulary");

        CycConstant theoryMt = createMicrotheory(theoryMtName,
                                                 comment,
                                                 getKnownConstantByName("TheoryMicrotheory"),
                                                 genlMts);
        CycConstant vocabMt = createMicrotheory(vocabMtName,
                                                vocabMtComment,
                                                getKnownConstantByName("VocabularyMicrotheory"),
                                                new ArrayList());
        CycConstant dataMt = createMicrotheory(dataMtName,
                                               dataMtComment,
                                               getKnownConstantByName("DataMicrotheory"),
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
     */
    public void assertIsaCollection (CycConstant cycConstant,
                                     CycFort mt)   throws IOException, UnknownHostException {
        assertGaf(mt, isa, cycConstant, collection);
    }

    /**
     * Assert that the CycConstant GENLS is a genls of CycFort SPEC,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertGenls (CycFort specCollection,
                             CycFort genlsCollection,
                             CycFort mt)   throws IOException, UnknownHostException {
        assertGaf(mt, genls, specCollection, genlsCollection);
    }

    /**
     * Assert that the CycFort GENLS isa CycFort ACOLLECTION,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertIsa (CycFort cycFort,
                             CycFort aCollection,
                             CycFort mt)   throws IOException, UnknownHostException {
        assertGaf(mt, isa, cycFort, aCollection);
    }

    /**
     * Assert that the specified CycConstant is a #$BinaryPredicate in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertIsaBinaryPredicate (CycConstant cycConstant,
                                          CycFort mt)   throws IOException, UnknownHostException {
        assertIsa(cycConstant, binaryPredicate, mt);
    }

    /**
     * Constructs a new <tt>CycList<tt> object by parsing a string.
     *
     * @param string the string in CycL external (EL). For example:<BR>
     * <code>(#$isa #$Dog #$TameAnimal)</code>
     */
    public CycList makeCycList(String string) {
        return (new CycListParser(this)).read(string);
    }

    /**
     * Constructs a new <tt>CycConstant</tt> object using the constant name.
     *
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public CycConstant makeCycConstant(String name)
        throws UnknownHostException, IOException {
        CycConstant cycConstant = this.getConstantByName(name);
        if (cycConstant == null) {
            cycConstant = this.createNewPermanent(name);
            if (cycConstant == null)
                throw new RuntimeException("Cannot create new constant for " + name);
            CycConstant.addCache(cycConstant);
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
     */
    public CycList askWithVariable (CycList query,
                                    CycVariable variable,
                                    CycFort mt)  throws IOException, UnknownHostException {
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
     * Returns <tt>true</tt> iff the query is true in the knowledge base.
     *
     * @param query the query to be asked in the knowledge base
     * @param mt the microtheory in which the query is asked
     * @return <tt>true</tt> iff the query is true in the knowledge base
     */
    public boolean isQueryTrue (CycList query,
                                CycFort mt)  throws IOException, UnknownHostException {
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("removal-ask"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.quote);
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
     */
    public boolean isQueryTrue_Cached (CycList query,
                                          CycFort mt) throws IOException {
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
     */
    public int countAllInstances(CycFort collection, CycFort mt) throws IOException {
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
     */
    public int countAllInstances_Cached(CycFort collection,
                                        CycFort mt) throws IOException {
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
     * Gets a list of the backchaining rules which might apply to the given rule.
     *
     * @param rule the rule for which backchaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for backchaining rules takes place
     * @return a list of the backchaining rules which might apply to the given predicate
     */
    public CycList getBackchainRules (Rule rule, CycFort mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        if (mt.equals(this.getKnownConstantByName("InferencePSC")) ||
            mt.equals(this.getKnownConstantByName("EverythingPSC"))) {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-all-mts ");
            command.append("    (do-rule-index (rule " + rule.getPredicate().stringApiValue() + " :pos nil :backward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + rule.getFormula().stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-rule-index (rule " + rule.getPredicate().stringApiValue() + " :pos nil :backward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + rule.getFormula().stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        //this.traceOn();
        return converseList(command.toString());
    }

    /**
     * Gets a list of the forward chaining rules which might apply to the given rule.
     *
     * @param rule the rule for which backchaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules takes place
     * @return a list of the forward chaining rules which might apply to the given predicate
     */
    public CycList getForwardChainRules (Rule rule, CycFort mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        if (mt.equals(this.getKnownConstantByName("InferencePSC")) ||
            mt.equals(this.getKnownConstantByName("EverythingPSC"))) {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-all-mts ");
            command.append("    (do-rule-index (rule " + rule.getPredicate().stringApiValue() + " :pos nil :forward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + rule.getFormula().stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules formula) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-rule-index (rule " + rule.getPredicate().stringApiValue() + " :pos nil :forward) ");
            command.append("       (csetq formula (assertion-el-formula rule)) ");
            command.append("       (pwhen (cand (eq (first formula) #$implies) ");
            command.append("                    (unify-el-possible '" + rule.getFormula().stringApiValue() + " ");
            command.append("                                          (third formula))) ");
            command.append("         (cpush formula backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        return converseList(command.toString());
    }

    /**
     * Gets a list of the backchaining rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which backchaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for backchaining rules takes place
     * @return a list of the backchaining rules which might apply to the given predicate
     */
    public CycList getBackchainRules (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        if (mt.equals(this.getKnownConstantByName("InferencePSC")) ||
            mt.equals(this.getKnownConstantByName("EverythingPSC"))) {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-all-mts ");
            command.append("    (do-rule-index (rule " + predicate.stringApiValue() + " :pos nil :backward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-rule-index (rule " + predicate.stringApiValue() + " :pos nil :backward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        //this.traceOn();
        return converseList(command.toString());
    }

    /**
     * Gets a list of the forward chaining rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which forward chaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules takes place
     * @return a list of the forward chaining rules which might apply to the given predicate
     */
    public CycList getForwardChainRules (CycConstant predicate, CycFort mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        if (mt.equals(this.getKnownConstantByName("InferencePSC")) ||
            mt.equals(this.getKnownConstantByName("EverythingPSC"))) {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-all-mts ");
            command.append("    (do-rule-index (rule " + predicate.stringApiValue() + " :pos nil :forward) ");
            command.append("       (pwhen (eq (first (assertion-el-formula rule)) #$implies) ");
            command.append("         (cpush (assertion-el-formula rule) backchain-rules)))) ");
            command.append("   backchain-rules)");
        }
        else {
            command.append("(clet (backchain-rules) ");
            command.append("  (with-mt " + mt.stringApiValue() + " ");
            command.append("    (do-rule-index (rule " + predicate.stringApiValue() + " :pos nil :forward) ");
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
     */
    public Object getSymbolValue (CycSymbol cycSymbol)
        throws IOException, UnknownHostException {
        return converseObject("(symbol-value '" + cycSymbol + ")");
    }

    /**
     * Sets a KB symbol to have the specified value.  This is intended mainly for test case setup.  If the symbol does
     * not exist at the KB, then it will be created and assigned the value.
     *
     * @param symbol the KB symbol which will have a value bound
     * @param value the value assigned to the symbol
     */
    public void setSymbolValue (CycSymbol cycSymbol, Object value)
        throws IOException, UnknownHostException {
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("csetq"));
        command.add(cycSymbol);
        command.add(value);
        converseVoid(command);
    }

    /**
     * Returns <tt>true</tt> iff <tt>CycList</tt> represents a well formed formula.
     */
    public boolean isWellFormedFormula (CycList cycList)  throws IOException, UnknownHostException {
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("with-all-mts"));
        CycList command1 = new CycList();
        command.add(command1);
        command1.add(CycSymbol.makeCycSymbol("el-wff?"));
        CycList command2 = new CycList();
        command1.add(command2);
        command2.add(CycSymbol.quote);
        command2.add(cycList);
        return converseBoolean(command);
    }

    /**
     * Returns <tt>true</tt> iff backchain inference on the given predicate is required.
     *
     * @param predicate the <tt>CycConstant</tt> predicate for which backchaining required status is sought
     * @param mt microtheory (including its genlMts) in which the backchaining required status is sought
     * @return <tt>true</tt> iff backchain inference on the given predicate is required
     */
    public boolean isBackchainRequired(CycConstant predicate, CycFort mt) throws IOException {
        return hasSomePredicateUsingTerm(getKnownConstantByName("backchainRequired"),
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
     */
    public boolean isBackchainEncouraged(CycConstant predicate, CycFort mt) throws IOException {
        return hasSomePredicateUsingTerm(getKnownConstantByName("backchainEncouraged"),
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
     */
    public boolean isBackchainDiscouraged(CycConstant predicate, CycFort mt) throws IOException {
        return hasSomePredicateUsingTerm(getKnownConstantByName("backchainDiscouraged"),
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
     */
    public boolean isBackchainForbidden(CycConstant predicate, CycFort mt) throws IOException {
        if (predicate.equals(getKnownConstantByName("objectFoundInLocation")))
            return false;
        return hasSomePredicateUsingTerm(getKnownConstantByName("backchainForbidden"),
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
     */
    public boolean isIrreflexivePredicate(CycConstant predicate, CycFort mt) throws IOException {
        return this.isa(predicate, getKnownConstantByName("IrreflexiveBinaryPredicate"), mt);
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
     */
    public boolean hasSomePredicateUsingTerm(CycConstant predicate,
                                             CycFort term,
                                             Integer argumentPosition,
                                             CycFort mt) throws IOException {
        CycList command = new CycList();
        if (mt.equals(this.getKnownConstantByName("InferencePSC")) ||
            mt.equals(this.getKnownConstantByName("EverythingPSC"))) {
            command.add(CycSymbol.makeCycSymbol("some-pred-value-in-any-mt"));
            command.add(term.cycListApiValue());
            command.add(predicate.cycListApiValue());
        }
        else {
            command.add(CycSymbol.makeCycSymbol("some-pred-value-in-relevant-mts"));
            command.add(term.cycListApiValue());
            command.add(predicate.cycListApiValue());
            command.add(mt.cycListApiValue());
        }
        command.add(argumentPosition);
        //this.traceOn();
        return converseBoolean(command);
    }

}
