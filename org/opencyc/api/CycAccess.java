package org.opencyc.api;

import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.oro.util.*;
import org.opencyc.cycobject.*;
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
     * Converses with Cyc to perform an API command whose result is returned as a list.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public CycList converseList(Object command)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        response = converse(command);
        if (response[0].equals(Boolean.TRUE)) {
            if (response[1] == null)
                // Do not coerce empty lists to the symbol nil, instead return a list of size zero.
                return new CycList();
            else
                return (CycList) response[1];
        }
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
            if (response[1] == null)
                // Do not conflate null with the symbol NIL.
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
            baseKB = getConstantByName("BaseKB");
        if (isa == null)
            isa = getConstantByName("isa");
        if (genls == null)
            genls = getConstantByName("genls");
        if (genlMt == null)
            genlMt = getConstantByName("genlMt");
        if (comment == null)
            comment = getConstantByName("comment");
        if (collection == null)
            collection = getConstantByName("Collection");
        if (binaryPredicate == null)
            binaryPredicate = getConstantByName("BinaryPredicate");
        if (elementOf == null)
            elementOf = getConstantByName("elementOf");
        if (and == null)
            and = getConstantByName("and");
        if (or == null)
            or = getConstantByName("or");
        if (numericallyEqual == null)
            numericallyEqual = getConstantByName("numericallyEqual");
        if (plusFn == null)
            plusFn = getConstantByName("PlusFn");
        if (different == null)
            different = getConstantByName("different");
        if (thing == null)
            thing = getConstantByName("Thing");
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
     * Completes the instantiation of <tt>CycVariable</tt> returned by the binary api. The
     * binary api sends only variable ids, and the variable name must be retrieved
     * if the variable is not cached.  The variable id is not used when sending variables to
     * the binary api, instead the variable is output as a symbol.
     *
     * @param cycVariable the <tt>CycVariable</tt> whose name is to be completed
     * @return the completed <tt>CycVariable</tt> object, or a reference to the previously
     * cached instance
     */
    public CycVariable completeCycVariable (CycVariable cycVariable)
        throws IOException, UnknownHostException {
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
        command.add(CycSymbol.makeCycSymbol("assertion-formula"));
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
     * Returns true if CycConstant BINARYPREDICATE relates CycConstant ARG1 and CycConstant ARG2.
     */
    public boolean predicateRelates (CycConstant binaryPredicate,
                                     CycConstant arg1,
                                     CycConstant arg2)  throws IOException, UnknownHostException {
        Object [] response = {null, null};
        String command = "(pred-u-v-holds-in-any-mt " +
            binaryPredicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2.cycName() + ")";
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
     * Gets the plural generated phrase for a CycConstant (intended for collections).
     */
    public String getPluralGeneratedPhrase (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycConstant.cycName() + " '(#$plural)))");
    }

    /**
     * Gets the singular generated phrase for a CycConstant (intended for individuals).
     */
    public String getSingularGeneratedPhrase (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycConstant.cycName() + " '(#$singular)))");
    }

    /**
     * Gets the default generated phrase for a CycConstant (intended for predicates).
     */
    public String getGeneratedPhrase (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseString("(with-precise-paraphrase-on (generate-phrase " + cycConstant.cycName() + "))");
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
        return converseString("(string-substitute \" \" \"\\\"\" (with-all-mts (comment " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the isas for a CycConstant.
     */
    public CycList getIsas (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (isa " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the directly asserted true genls for a CycConstant collection.
     */
    public CycList getGenls (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (genls " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the minimum (most specific) genls for a CycConstant collection.
     */
    public CycList getMinGenls (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (min-genls " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the directly asserted true specs for a CycConstant collection.
     */
    public CycList getSpecs (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (specs " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the least specific specs for a CycConstant collection.
     */
    public CycList getMaxSpecs (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (max-specs " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the direct genls of the direct specs for a CycConstant collection.
     */
    public CycList getGenlSiblings (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (genl-siblings " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the siblings (direct specs of the direct genls) for a CycConstant collection.
     */
    public CycList getSiblings (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return getSpecSiblings(cycConstant);
    }

    /**
     * Gets a list of the siblings (direct specs of the direct genls) for a CycConstant collection.
     */
    public CycList getSpecSiblings (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (spec-siblings " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of all of the direct and indirect genls for a CycConstant collection.
     */
    public CycList getAllGenls (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(all-genls-in-any-mt " + cycConstant.cycName() + ")");
    }

    /**
     * Gets a list of all of the direct and indirect specs for a CycConstant collection.
     */
    public CycList getAllSpecs (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-specs " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of all of the direct and indirect genls for a CycConstant SPEC which are also specs of
     * CycConstant GENL.
     */
    public CycList getAllGenlsWrt (CycConstant spec, CycConstant genl )  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-genls-wrt " + spec.cycName() + " " + genl.cycName() + ")))");
    }

    /**
     * Gets a list of all of the dependent specs for a CycConstant collection.  Dependent specs are those direct and
     * indirect specs of the collection such that every path connecting the spec to a genl of the collection passes
     * through the collection.  In a typical taxomonmy it is expected that all-dependent-specs gives the same
     * result as all-specs.
     */
    public CycList getAllDependentSpecs (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (all-dependent-specs " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list with the specified number of sample specs of a CycConstant collection.  Attempts to return
     * leaves that are maximally differet with regard to their all-genls.
     */
    public CycList getSampleLeafSpecs (CycConstant cycConstant, int numberOfSamples)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (sample-leaf-specs " + cycConstant.cycName() + " " + numberOfSamples + "))");
    }

    /**
     * Returns true if CycConstant SPEC is a spec of CycConstant GENL.
     */
    public boolean isSpecOf (CycConstant spec, CycConstant genl)  throws IOException, UnknownHostException {
        return isGenlOf(genl, spec);
    }

    /**
     * Returns true if CycConstant GENL is a genl of CycConstant SPEC.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycConstant GENL is a genl of CycConstant SPEC
     */
    public boolean isGenlOf (CycConstant genl, CycConstant spec)  throws IOException,
                                                                         UnknownHostException {
        return converseBoolean("(genl-in-any-mt? " + spec.cycName() + " " + genl.cycName() + ")");
    }

    /**
     * Returns true if CycConstant GENL is a genl of CycConstant SPEC, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param genl the collection for genl determination
     * @param spec the collection for spec determination
     * @return <tt>true</tt> if CycConstant GENL is a genl of CycConstant SPEC
     */
    public boolean isGenlOf_Cached (CycConstant genl, CycConstant spec)
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
     * Returns true if CycConstant COLLECION1 and CycConstant COLLECTION2 are tacitly coextensional via mutual genls of each other.
     */
    public boolean areTacitCoextensional (CycConstant collection1, CycConstant collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (tacit-coextensional? " + collection1.cycName() + " " + collection2.cycName() + "))");
    }

    /**
     * Returns true if CycConstant COLLECION1 and CycConstant COLLECTION2 are asserted coextensional.
     */
    public boolean areAssertedCoextensional (CycConstant collection1, CycConstant collection2)  throws IOException, UnknownHostException {
        if (predicateRelates(getConstantByName("coExtensional"), collection1, collection2))
            return true;
        else if (predicateRelates(getConstantByName("coExtensional"), collection2, collection1))
            return true;
        else
            return false;
    }

    /**
     * Returns true if CycConstant COLLECION1 and CycConstant COLLECTION2 intersect with regard to all-specs.
     */
    public boolean areIntersecting (CycConstant collection1, CycConstant collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (collections-intersect? " + collection1.cycName() + " " + collection2.cycName() + "))");
    }

    /**
     * Returns true if CycConstant COLLECION1 and CycConstant COLLECTION2 are in a hierarchy.
     */
    public boolean areHierarchical (CycConstant collection1, CycConstant collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (hierarchical-collections? " + collection1.cycName() + " " + collection2.cycName() + "))");
    }

    /**
     * Gets a list of the justifications of why CycConstant SPEC is a SPEC of CycConstant GENL.
     * getWhyGenl("Dog", "Animal") -->
     * "(((#$genls #$Dog #$CanineAnimal) :TRUE)
     *    (#$genls #$CanineAnimal #$NonPersonAnimal) :TRUE)
     *    (#$genls #$NonPersonAnimal #$Animal) :TRUE))
     *
     */
    public CycList getWhyGenl (CycConstant spec, CycConstant genl)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-genl? " + spec.cycName() + " " + genl.cycName() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycConstant SPEC is a SPEC of CycConstant GENL.
     * getWhyGenlParaphrase("Dog", "Animal") -->
     * "a dog is a kind of canine"
     * "a canine is a kind of non-human animal"
     * "a non-human animal is a kind of animal"
     *
     */
    public ArrayList getWhyGenlParaphrase (CycConstant spec, CycConstant genl)
        throws IOException, UnknownHostException {
        CycList listAnswer =
            converseList("(with-all-mts (why-genl? " +
                         spec.cycName() + " " + genl.cycName() + "))");
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
     * Gets a list of the justifications of why CycConstant COLLECTION1 and a CycConstant COLLECTION2 intersect.
     * see getWhyGenl
     */
    public CycList getWhyCollectionsIntersect (CycConstant collection1,
                                                         CycConstant collection2)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-collections-intersect? " +
                            collection1.cycName() + " " + collection2.cycName() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycConstant COLLECTION1 and a CycConstant COLLECTION2 intersect.
     * see getWhyGenlParaphrase
     */
    public ArrayList getWhyCollectionsIntersectParaphrase (CycConstant collection1,
                                                           CycConstant collection2)
        throws IOException, UnknownHostException {
        CycList listAnswer = converseList("(with-all-mts (why-collections-intersect? " +
                                          collection1.cycName() + " " + collection2.cycName() + "))");
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
     * Gets a list of the collection leaves (most specific of the all-specs) for a CycConstant collection.
     */
    public CycList getCollectionLeaves (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (collection-leaves " + cycConstant.cycName() + "))");
    }

    /**
     * Gets a list of the collections asserted to be disjoint with a CycConstant collection.
     */
    public CycList getLocalDisjointWith (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (local-disjoint-with " + cycConstant.cycName() + "))");
    }

    /**
     * Returns true if CycConstant COLLECION1 and CycConstant COLLECTION2 are disjoint.
     */
    public boolean areDisjoint (CycConstant collection1, CycConstant collection2)  throws IOException, UnknownHostException {
        return converseBoolean("(with-all-mts (disjoint-with? " + collection1.cycName() + " " + collection2.cycName() + "))");
    }

    /**
     * Gets a list of the most specific collections (having no subsets) which contain a CycConstant term.
     */
    public CycList getMinIsas (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (min-isa " + cycConstant.cycName() + "))");
    }

    /**
     * Gets a list of the instances (who are individuals) of a CycConstant collection.
     */
    public CycList getInstances (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (instances " + cycConstant.cycName() + "))");
    }

    /**
     * Gets a list of the instance siblings of a CycConstant, for all collections of which it is an instance.
     */
    public CycList getInstanceSiblings (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (instance-siblings " + cycConstant.cycName() + "))");
    }

    /**
     * Gets a list of the collections of which the CycConstant is directly and indirectly an instance.
     */
    public CycList getAllIsa (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(all-isa-in-any-mt " + cycConstant.cycName() + ")");
    }

    /**
     * Gets a list of all the direct and indirect instances (individuals) for a CycConstant collection.
     */
    public CycList getAllInstances (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(all-instances-in-all-mts " + cycConstant.cycName() + ")");
    }

    /**
     * Returns true if CycConstant TERM is a instance of CycConstant COLLECTION.
     */
    public boolean isa (CycConstant term, CycConstant collection)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + term.cycName() + " " + collection.cycName() + ")");
    }

    /**
     * Gets a list of the justifications of why CycConstant TERM is an instance of CycConstant COLLECTION.
     * getWhyIsa("Brazil", "Country") -->
     * "(((#$isa #$Brazil #$IndependentCountry) :TRUE)
     *    (#$genls #$IndependentCountry #$Country) :TRUE))
     *
     */
    public CycList getWhyIsa (CycConstant spec, CycConstant genl)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (why-isa? " + spec.cycName() + " " + genl.cycName() + "))");
    }

    /**
     * Gets an English parapharse of the justifications of why CycConstant TERM is an instance of CycConstant COLLECTION.
     * getWhyGenlParaphase("Brazil", "Country") -->
     * "Brazil is an independent country"
     * "an  independent country is a kind of country"
     *
     */
    public ArrayList getWhyIsaParaphrase (CycConstant spec, CycConstant genl)  throws IOException {
        String command = "(with-all-mts (why-isa? " + spec.cycName() + " " + genl.cycName() + "))";
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
        return converseList("(remove-duplicates (with-all-mts (genl-predicates " + predicate.cycName() + ")))");
    }

    /**
     * Gets a list of the arg1Isas for a CycConstant predicate.
     */
    public CycList getArg1Isas (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (arg1-isa " + predicate.cycName() + ")))");
    }

    /**
     * Gets a list of the arg2Isas for a CycConstant predicate.
     */
    public CycList getArg2Isas (CycConstant predicate)  throws IOException, UnknownHostException {
       return converseList("(remove-duplicates (with-all-mts (arg2-isa " + predicate.cycName() + ")))");
    }

    /**
     * Gets a list of the argNIsas for a CycConstant predicate.
     */
    public CycList getArgNIsas (CycConstant predicate, int argPosition)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (argn-isa " + predicate.cycName() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets a list of the argNGenls for a CycConstant predicate.
     */
    public CycList getArgNGenls (CycConstant predicate, int argPosition)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (argn-genl " + predicate.cycName() +
                            " " + argPosition + ")))");
    }

    /**
     * Gets a list of the arg1Formats for a CycConstant predicate.
     */
    public CycList getArg1Formats (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (arg1-format " + predicate.cycName() + "))");
    }

    /**
     * Gets a list of the arg2Formats for a CycConstant predicate.
     */
    public CycList getArg2Formats (CycConstant predicate)  throws IOException, UnknownHostException {
        return converseList("(with-all-mts (arg2-format " + predicate.cycName() + "))");
    }

    /**
     * Gets a list of the disjointWiths for a CycConstant.
     */
    public CycList getDisjointWiths (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseList("(remove-duplicates (with-all-mts (local-disjoint-with " + cycConstant.cycName() + ")))");
    }

    /**
     * Gets a list of the coExtensionals for a CycConstant.  Limited to 120 seconds.
     */
    public CycList getCoExtensionals (CycConstant cycConstant)  throws IOException, UnknownHostException {
        CycList answer = converseList("(ask-template '?X '(#$coExtensional " + cycConstant.cycName() + " ?X) #$EverythingPSC nil nil 120)");
        answer.remove(cycConstant);
        return answer;
    }

    /**
     * Returns true if cycConstant is a Collection.
     */
    public boolean isCollection (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$Collection)");
    }

    /**
     * Returns true if cycConstant is a Collection, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param cycConstant the constant for determination as a Collection
     * @return <tt>true</tt> iff cycConstant is a Collection,
     */
    public boolean isCollection_Cached(CycConstant cycConstant)  throws IOException {
        boolean answer;
        Boolean isCollection = (Boolean) isCollectionCache.getElement(cycConstant);
        if (isCollection != null) {
            answer = isCollection.booleanValue();
            return answer;
        }
        answer = isCollection(cycConstant);
        isCollectionCache.addElement(cycConstant, new Boolean(answer));
        return answer;
    }

    /**
     * Returns true if cycConstant is an Individual.
     */
    public boolean isIndividual (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$Individual)");
    }

    /**
     * Returns true if cycConstant is a Function.
     */
    public boolean isFunction (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$Function-Denotational)");
    }

    /**
     * Returns true if cycConstant is a Predicate.
     */
    public boolean isPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$Predicate)");
    }

    /**
     * Returns true if cycConstant is a UnaryPredicate.
     */
    public boolean isUnaryPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$UnaryPredicate)");
    }

    /**
     * Returns true if cycConstant is a BinaryPredicate.
     */
    public boolean isBinaryPredicate (CycConstant cycConstant)  throws IOException, UnknownHostException {
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$BinaryPredicate)");
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
        return converseBoolean("(isa-in-any-mt? " + cycConstant.cycName() + " #$PublicConstant)");
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
        converseBoolean("(cyc-kill " + cycConstant.cycName() + ")");
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
            projectName = project.cycName();
        String cyclistName = "nil";
        if (cyclist != null)
            cyclistName = cyclist.cycName();
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
    public void assertGaf (CycConstant mt,
                           CycConstant predicate,
                           CycConstant arg1,
                           CycConstant arg2)   throws IOException, UnknownHostException {
        // (predicate <CycConstant> <CycConstant>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2.cycName() + ")" +
            mt.cycName() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycConstant mt,
                           CycConstant predicate,
                           CycConstant arg1,
                           String arg2)   throws IOException, UnknownHostException {
        // (predicate <CycConstant> <String>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.cycName() + " " +
            arg1.cycName() + " " +
            "\"" + arg2 + "\")" +
            mt.cycName() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycConstant mt,
                           CycConstant predicate,
                           CycConstant arg1,
                           CycList arg2)   throws IOException, UnknownHostException {
        // (predicate <CycConstant> <List>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2.cyclify() + ")" +
            mt.cycName() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycConstant mt,
                           CycConstant predicate,
                           CycConstant arg1,
                           int arg2)   throws IOException, UnknownHostException {
        // (predicate <CycConstant> <int>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2 + ")" +
            mt.cycName() + "))";
        converseVoid(command);
    }
    public void assertGaf (CycConstant mt,
                           CycConstant predicate,
                           CycConstant arg1,
                           CycConstant arg2,
                           CycConstant arg3)   throws IOException, UnknownHostException {
        // (predicate <CycConstant> <CycConstant> <CycConstant>)
        String command = withBookkeepingInfo() +
            "(cyc-assert '(" +
            predicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2.cycName() + " " +
            arg3.cycName() + ")" +
            mt.cycName() + "))";
        converseVoid(command);
    }

    /**
     * Assert a comment for the specified CycConstant in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     */
    public void assertComment (CycConstant cycConstant,
                               String comment,
                               CycConstant mt)   throws IOException, UnknownHostException {
        assertGaf(mt, getConstantByName("comment"), cycConstant, comment);
    }

    /**
     * Create a microtheory MT, with a comment, isa <mt type> and CycConstant genlMts.
     * An existing microtheory with
     * the same name is killed first, if it exists.
     */
    public CycConstant createMicrotheory (String mtName,
                                          String comment,
                                          CycConstant isaMt,
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
            CycConstant aGenlMt = (CycConstant) iterator.next();
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
        CycConstant worldLikeOursMt = this.getConstantByName("WorldLikeOursCollectorMt");
        CycConstant currentWorldDataMt = this.getConstantByName("CurrentWorldDataCollectorMt");
        CycConstant genlMt_Vocabulary = this.getConstantByName("genlMt-Vocabulary");

        CycConstant theoryMt = createMicrotheory(theoryMtName,
                                                 comment,
                                                 getConstantByName("TheoryMicrotheory"),
                                                 genlMts);
        CycConstant vocabMt = createMicrotheory(vocabMtName,
                                                vocabMtComment,
                                                getConstantByName("VocabularyMicrotheory"),
                                                new ArrayList());
        CycConstant dataMt = createMicrotheory(dataMtName,
                                               dataMtComment,
                                               getConstantByName("DataMicrotheory"),
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
                                     CycConstant mt)   throws IOException, UnknownHostException {
        assertGaf(mt, isa, cycConstant, collection);
    }

    /**
     * Assert that the CycConstant GENLS is a genls of CycConstant SPEC,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertGenls (CycConstant specCollection,
                             CycConstant genlsCollection,
                             CycConstant mt)   throws IOException, UnknownHostException {
        assertGaf(mt, genls, specCollection, genlsCollection);
    }

    /**
     * Assert that the CycConstant GENLS isa CycConstant ACOLLECTION,
     * in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertIsa (CycConstant cycConstant,
                             CycConstant aCollection,
                             CycConstant mt)   throws IOException, UnknownHostException {
        assertGaf(mt, isa, cycConstant, aCollection);
    }

    /**
     * Assert that the specified CycConstant is a #$BinaryPredicate in the specified defining microtheory MT.
     * The operation will be added to the KB transcript for replication and archive.
     */
    public void assertIsaBinaryPredicate (CycConstant cycConstant,
                                          CycConstant mt)   throws IOException, UnknownHostException {
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
                                    CycConstant mt)  throws IOException, UnknownHostException {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("(clet ((*cache-inference-results* nil) ");
        queryBuffer.append("       (*compute-inference-results* nil) ");
        queryBuffer.append("       (*unique-inference-result-bindings* t) ");
        queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
        queryBuffer.append("  (without-wff-semantics ");
        queryBuffer.append("    (ask-template '" + variable.cyclify() + " ");
        queryBuffer.append("                  '" + query.cyclify() + " ");
        queryBuffer.append("                  " + mt.cyclify() + " ");
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
                                CycConstant mt)  throws IOException, UnknownHostException {
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
                                          CycConstant mt) throws IOException {
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
    public int countAllInstances(CycConstant collection, CycConstant mt) throws IOException {
        return this.converseInt("(count-all-instances " +
                                collection.cyclify() + " " +
                                mt.cyclify() + ")");
    }

    /**
     * Returns the count of the instances of the given collection, implements a cache
     * to avoid asking the same question twice from the KB.
     *
     * @param collection the collection whose instances are counted
     * @param mt microtheory (including its genlMts) in which the count is determined
     * @return the count of the instances of the given collection
     */
    public int countAllInstances_Cached(CycConstant collection,
                                        CycConstant mt) throws IOException {
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
     * Gets a list of the backchaining rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which backchaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for backchaining rules takes place
     * @return a list of the backchaining rules which might apply to the given predicate
     */
    public CycList getBackchainRules (CycConstant predicate, CycConstant mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        command.append("(clet (backchain-rules) ");
        command.append("  (with-mt " + mt.cyclify() + " ");
        command.append("    (do-rule-index (rule " + predicate.cyclify() + " :pos nil :backward) ");
        command.append("       (cpush (assertion-formula rule) backchain-rules))) ");
        command.append("   backchain-rules)");
        this.traceOn();
        return converseList(command.toString());
    }

    /**
     * Gets a list of the forward chaining rules which might apply to the given predicate.
     *
     * @param predicate the predicate for which forward chaining rules are sought
     * @param mt the microtheory (and its genlMts) in which the search for forward chaining rules takes place
     * @return a list of the forward chaining rules which might apply to the given predicate
     */
    public CycList getForwardChainRules (CycConstant predicate, CycConstant mt)
        throws IOException, UnknownHostException {
        StringBuffer command = new StringBuffer();
        command.append("(clet (forward-chain-rules) ");
        command.append("  (with-mt " + mt.cyclify() + " ");
        command.append("    (do-rule-index (rule " + predicate.cyclify() + " :pos nil :forward) ");
        command.append("       (cpush (assertion-formula rule) forward-chain-rules))) ");
        command.append("   forward-chain-rules)");
        return converseList(command.toString());
    }


}
