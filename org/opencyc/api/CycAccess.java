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
     * Response code returned by the OpenCyc api.
     */
    public Integer responseCode = null;

    /**
     * Parameter indicating whether the OpenCyc api should use one TCP socket for the entire
     * session, or if the socket is created and then closed for each api call.
     */
    public boolean persistentConnection = true;

    private boolean trace = false;
    private String hostName = CycConnection.DEFAULT_HOSTNAME;
    private int port = CycConnection.DEFAULT_PORT;
    private static final Integer OK_RESPONSE_CODE = new Integer(200);
    private CycConnection cycConnection;

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
     * Constructs a new CycAccess object.
     */
    public CycAccess() throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        if (persistentConnection)
            cycConnection = new CycConnection();
        initializeConstants();
    }
    /**
     * Constructs a new CycAccess object given a host name.
     *
     * @param hostName the host name
     */
    public CycAccess(String hostName) throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.hostName = hostName;
        if (persistentConnection)
            cycConnection = new CycConnection(hostName);
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given a port.
     *
     * @param port the TCP socket port number
     */
    public CycAccess(int port) throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.port = port;
        if (persistentConnection)
            cycConnection = new CycConnection(port);
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given a host name and port.
     *
     * @param hostName the host name
     * @param port the TCP socket port number
     */
    public CycAccess(String hostName, int port) throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.hostName = hostName;
        this.port = port;
        if (persistentConnection)
            cycConnection = new CycConnection(port);
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given an indicator for a persistent socket connection.
     *
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public CycAccess(boolean persistentConnection) throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection();
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given a host name.
     *
     * @param hostName the host name
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public CycAccess(String hostName, boolean persistentConnection) throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.hostName = hostName;
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection(hostName);
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given a port.
     *
     * @param port the TCP socket port number
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public CycAccess(int port, boolean persistentConnection)
        throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.port = port;
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection(port);
        initializeConstants();
    }

    /**
     * Constructs a new CycAccess object given a host name and port.
     *
     * @param hostName the host name
     * @param port the TCP socket port number
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public CycAccess(String hostName, int port, boolean persistentConnection)
        throws IOException, UnknownHostException {
        cycAccessInstances.put(Thread.currentThread(), this);
        this.hostName = hostName;
        this.port = port;
        this.persistentConnection = persistentConnection;
        if (persistentConnection)
            cycConnection = new CycConnection(port);
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
     * @param command the command string
     */
    private Object [] converse(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        if (! persistentConnection)
            cycConnection = new CycConnection(hostName, port);
        response = cycConnection.converse(command);
        if (! persistentConnection)
            cycConnection.close();
        return response;
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a list.
     *
     * @param command the command string
     * @return the result of processing the API command
     */
    public CycList converseList(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            return (CycList) response[1];
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a String.
     *
     * @param command the command string
     * @return the result of processing the API command
     */
    public String converseString(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            return (String) response[1];
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a boolean.
     *
     * @param command the command string
     * @return the result of processing the API command
     */
    public boolean converseBoolean(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            if (response[1].equals("T"))
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
     * @param command the command string
     * @return the result of processing the API command
     */
    public int converseInt(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            return (new Integer(response[1].toString())).intValue();
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Converses with Cyc to perform an API command whose result is void.
     *
     * @param command the command string
     */
    public void converseVoid(String command)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        response = converse(command);
        responseCode = (Integer) response[0];
        if (! responseCode.equals(OK_RESPONSE_CODE))
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
     * Gets a CycConstant by using its GUID.
     */
    public CycConstant getConstantByGuid (Guid guid)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        String command = "(find-constant-by-guid (string-to-guid \"" + guid + "\"))";
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            String constantName = (String) response[1];
            return CycConstant.makeCycConstant(guid, constantName);
        }
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Gets a CycConstant by using its constant name.
     */
    public CycConstant getConstantByName (String constantName)
        throws IOException, UnknownHostException {
        CycConstant answer = CycConstant.getCache(constantName);
        if (answer != null)
            return answer;
        Object [] response = {new Integer(0), ""};
        String command;
        if (constantName.startsWith("#$"))
           command = "(constant-guid " + constantName + ")";
        else
           command = "(constant-guid #$" + constantName + ")";
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            String guid = ((String) response[1]).substring(3, 39);
            answer = CycConstant.makeCycConstant(guid, constantName);
            return answer;
        }
        else if (((String) response[1]).indexOf("is not an existing constant") > -1)
            return null;
        else
            throw new IOException(response[1].toString());
    }

    /**
     * Gets a CycNart object from a Cons object that lists the names of
     * its functor and its arguments.
     */

    public CycNart getCycNartFromCons(CycList elCons) {
        return new CycNart(elCons);
    }

    /*
        throws java.net.UnknownHostException, java.io.IOException, java.lang.IllegalArgumentException {
        LinkedList arguments = new LinkedList();
        ListIterator iterator = elCons.toLinkedList().listIterator();
        Object functorObject = iterator.next();
        CycFort functor = null;
    String functorString = null;
    if (functorObject instanceof java.lang.String) {
      functorString = (String)functorObject;
    }
    else if (functorObject instanceof com.cyc.util.Atom) {
      functorString = functorObject.toString();
    }
        if (functorString instanceof java.lang.String) {
      try {
            functor = getConstantByName(functorString);
      }
      catch (IOException e) {
        System.err.println(functorString + " is not a constant!");
      }
    }
        else if (functorObject instanceof CycList) {
            functor = getCycNartFromCons((Cons)functorObject);
    }
        while (iterator.hasNext()) {
            Object argObject = iterator.next();
        String argString = null;
        if (argObject instanceof java.lang.String) {
          argString = (String)argObject;
        }
        else if (argObject instanceof com.cyc.util.Atom) {
          argString = argObject.toString();
        }
        Object preArg = null;
        Object arg = null;
        if (argString instanceof java.lang.String && argString.startsWith("#$")) {
          try {
        preArg = getConstantByName(argString);
          }
          catch (IOException e) {
        System.err.println(argString + " is not a constant!");
          }
        }
        if (preArg instanceof com.cyc.util.CycConstant) {
          arg = preArg;
        }
        else if (argString instanceof java.lang.String) {
          System.err.println("Warning: String argument: " + argString);
          arg = argString;
        }
        else if (argObject instanceof CycList) {
          arg = getCycNartFromCons((Cons)argObject);
        }
        if (arg != null) {
          arguments.add(arg);
        }
        else {
          throw new IllegalArgumentException("Could not construct FORT argument from " + argObject);
        }
        }
    if (functor == null || arguments.isEmpty()) {
      throw new IllegalArgumentException(elCons.toString());
    }
    return new CycNart(functor, arguments);
    }
    */

    /**
     * Returns true if CycConstant BINARYPREDICATE relates CycConstant ARG1 and CycConstant ARG2.
     */
    public boolean predicateRelates (CycConstant binaryPredicate,
                                     CycConstant arg1,
                                     CycConstant arg2)  throws IOException, UnknownHostException {
        Object [] response = {new Integer(0), ""};
        String command = "(pred-u-v-holds-in-any-mt " +
            binaryPredicate.cycName() + " " +
            arg1.cycName() + " " +
            arg2.cycName() + ")";
        response = converse(command);
        responseCode = (Integer) response[0];
        if (responseCode.equals(OK_RESPONSE_CODE)) {
            if (response[1].equals("T"))
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
        String command = withBookkeepingInfo() +
            "(cyc-create-new-permanent \"" + constantName + "\"))";
        converseString(command);
        return getConstantByName(constantName);
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
        CycConstant cycConstant = CycConstant.getCache(name);
        if (cycConstant == null) {
            cycConstant = this.getConstantByName(name);
            if (cycConstant == null)
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
        CycList response = converseList("(removal-ask '" + query.cyclify() + " " + mt.cyclify() +
                                        ")");
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


}
