/**
 * Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * yepster@users.sourceforge.net
 */
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import org.opencyc.api.*;
import oracle.jdbc.*;
import oracle.sql.*;
import ViolinStrings.*;
/**
 * 
 * CycJsprocs.java
 *
 * @author Yeb Havinga
 *
 * A collection of java stored procedures that can be called from Oracle.
 * To fully use the object and method mappings of the CYC API, write
 * new jsprocs.
 * 
 * See also:
 * 
 * OpenCyc Api Documentation
 *   http://www.opencyc.org/doc/cycapi
 * OpenCyc Java Api Documentation
 *   http://www.cyc.com/doc/opencyc_api/java_api/
 * Oracle Java Developer's Guide
 *   http://download-west.oracle.com/otndoc/oracle9i//901_doc/java.901/a90209/toc.htm
 * Oracle Java Stored Procedures Developer's Guide (especially part 3)
 *   http://download-west.oracle.com/otndoc/oracle9i//901_doc/java.901/a90210/toc.htm
 * Oracle JDBC Developer's Guide and Reference (part 10 and 18 ->connecting to internal driver)
 *   http://download-west.oracle.com/otndoc/oracle9i//901_doc/java.901/a90211/toc.htm
 * 
 */

public class CycJsprocs {

    protected static boolean ignorerrors = true;

    /**
     * the CycAccess object
     */
    protected static CycAccess cycAccess;

    /**
     * Initialize connection to Cyc, connection to hostname
     * 
     * Because Oracle java stored procedure calls are calls to static methods, the
     * constructor isn't called. (or something).
     * http://download-west.oracle.com/otndoc/oracle9i/901_doc/java.901/a90210/03_pub.htm#28941
     *
     * That's why this makeConnection method is here for initialization.
     */
    public static void makeConnection( String hostname ) {
        try {
            cycAccess = new CycAccess( hostname );
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    // the trace is visible in the $ORACLE_BASE/admin/<instancename>/udump directory.
    // Use only for debugging, because this is very verbose!
       cycAccess.traceOn();
    }

    /**
     * Initialize connection to Cyc, default connection to localhost
     * 
     * Because Oracle java stored procedure calls are calls to static methods, the
     * constructor isn't called. (or something).
     * http://download-west.oracle.com/otndoc/oracle9i/901_doc/java.901/a90210/03_pub.htm#28941
     *
     * That's why this makeConnection method is here for initialization.
     */
    public static void makeConnection( ) {
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    // the trace is visible in the $ORACLE_BASE/admin/<instancename>/udump directory.
    // Use only for debugging, because this is very verbose!
       cycAccess.traceOn();
    }


    /**
     * End previously opened connection to Cyc.
     */
    public static void endConnection() {
        try {
            cycAccess.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Wrapper of Cyc Java API makeCycConstant.
     *
     * @param constant Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public static void makeCycConstant(String constant)
        throws RuntimeException {
        try {
            CycConstant cons = cycAccess.makeCycConstant(constant);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Wrapper of Cyc Java API assertGaf, procedure variant.
     * 
     * Asserts a ground atomic formula (gaf) in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     * @param mt the microtheory in which the assertion is made
     * @param gaf is e.g.
     * (#$predicate #$Cons1)
     * (#$predicate #$Cons1 #$Cons2)
     * (#$predicate #$Cons1 String2)
     * (#$predicate #$Cons1 '(#$Some #$List))
     */
    public static void assertGaf (String gaf, String mt)
        throws RuntimeException {
        try {
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList cgaf = cycAccess.makeCycList (gaf);
            cycAccess.assertGaf (cgaf, cmt);
        }
        catch (Exception e) {
            throw new RuntimeException (e.getMessage());
        }
    }

    /**
     * Unasserts the given ground atomic formula (gaf) in the specified microtheory MT.
     *
     * @param gaf the gaf in the form of a CycList
     * @param mt the microtheory in which the assertion is made
     */
    public static void unassertGaf (String gaf, String mt)
        throws RuntimeException {
        try {
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList cgaf = cycAccess.makeCycList (gaf);
            cycAccess.unassertGaf (cgaf, cmt);
        }
        catch (Exception e) {
            throw new RuntimeException (e.getMessage());
        }
    }


    /**
     * Wrapper of Cyc Java API assertGaf, function variant.
     * 
     * Same as previous, but returns something, so it can be called by a pl/sql function,
     * which in turn can be called by the select cyc.assertgaf construct.
     * Function should only compute values, not change state. But this is to enable use of e.g.
     * select cyc.assertgaf( 'mt', '(isa ' || employee.name || ' #$Person)' ) from employee.
     * 
     */
    public static String assertGafFunction (String gaf, String mt)
        throws RuntimeException {
        try {
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList cgaf = cycAccess.makeCycList (gaf);
            cycAccess.assertGaf (cgaf, cmt);
            return gaf;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Wrapper of Cyc Java API assertWithTranscript, procedure variant.
     * 
     * Asserts a sentence in the specified microtheory MT.  The operation
     * will be added to the KB transcript for replication and archive.
     *
     */
    public static void assertWithTranscript (String sentence, String mt)
        throws RuntimeException {
        try {
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            cycAccess.assertWithTranscript (sentence, cmt);
        }
        catch (Exception e) {
//ignoreerrors for import purposes            if( !ignorerrors ) {
                throw new RuntimeException(e.getMessage());
//            }
        }
    }

    /**
     * Wrapper of Cyc Java API assertWithTranscript, function variant
     * 
     * Same as previous, but returns something, so it can be called by a pl/sql function.
     */
    public static String assertWithTranscriptFunction (String sentence, String mt)
        throws RuntimeException {
        try {
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            cycAccess.assertWithTranscript (sentence, cmt);
            return sentence;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Wrapper of Cyc Java API createMicroTheory
     * 
     * with a comment, isa <mt type> and CycFort genlMts.
     * An existing microtheory with
     * the same name is killed first, if it exists.
     */
    public static void createMicrotheory (String mtName,
                                          String comment,
                                          String isaMt,
                                          oracle.sql.ARRAY genlMts)
        throws RuntimeException {
        try {
            Object[] mts = (Object[]) genlMts.getArray();
            ArrayList cGenlMts = new ArrayList ();
            for (int i=0; i<mts.length; i++)
            {
                  cGenlMts.add (mts[i]);
            }
            System.out.println( cGenlMts);
            CycConstant mt = cycAccess.createMicrotheory (mtName, comment, isaMt, cGenlMts);
            return;    // return nothing; in oracle this becomes a procedure.
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Wrapper of Cyc Java API createMicroTheorySystem
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
     */
    public static void createMicrotheorySystem (String mtRootName,
                                          String comment,
                                          oracle.sql.ARRAY genlMts)
        throws RuntimeException {
        try {
            Object[] mts = (Object[]) genlMts.getArray();
            ArrayList cGenlMts = new ArrayList ();
            for (int i=0; i<mts.length; i++)
            {
                  cGenlMts.add (mts[i]);
            }
            System.out.println( cGenlMts);
            CycConstant[] mt = cycAccess.createMicrotheorySystem (mtRootName, comment, cGenlMts);
            return;    // return nothing; in oracle this becomes a procedure.
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Turns a cyclist into an oracle.sql.ARRAY
     * Must be static because it's called from other static methods.
     */
    private static oracle.sql.ARRAY cycListToArray ( CycList cyclist )
        throws SQLException {
        // Make internal jdbc connection to db.
        OracleDriver ora = new OracleDriver(); 
        Connection conn = ora.defaultConnection();  // internal connection to db.
        // array's, see http://download-west.oracle.com/otndoc/oracle9i//901_doc/java.901/a90211/oraarr.htm#1000888
        // obtain a type descriptor
        ArrayDescriptor desc = ArrayDescriptor.createDescriptor("CYCLIST_TYPE", conn); 
        // create the ARRAY by calling the constructor 
        oracle.sql.ARRAY arrayanswer = new oracle.sql.ARRAY(desc, conn, cyclist.toArray()); 
        return arrayanswer;
    }

/*****************
  WORK IN PROGRESS AM TRYING TO MAKE SOMETHING TO RETURN A NESTED ARRAY TO ORACLE ARRAY TYPE
*********** THIS IS NOT FINISHED *********/
    /**
     * Turns a cyclist into an oracle.sql.ARRAY
     * Must be static because it's called from other static methods.
     */
    private static oracle.sql.ARRAY cycListToNestedArray (CycList cyclist_in)
        throws SQLException {
        // Make internal jdbc connection to db.
        OracleDriver ora = new OracleDriver(); 
        Connection conn = ora.defaultConnection();  // internal connection to db.
        // array's, see http://download-west.oracle.com/otndoc/oracle9i//901_doc/java.901/a90211/oraarr.htm#1000888
        // obtain a type descriptor
        ArrayDescriptor desc = ArrayDescriptor.createDescriptor ("CYCLIST_NESTED_TYPE", conn); 
        // create a new array from the cyclist.
        String elems [][] = new String [cyclist_in.size()][2];
        // Loop through the cyclist.
        for (int i=0; i<cyclist_in.size(); i++)
        {
            CycList sublist = (CycList)cyclist_in.get(i);
/*****************
  WORK IN PROGRESS AM TRYING TO MAKE SOMETHING TO RETURN A NESTED ARRAY TO ORACLE ARRAY TYPE
*********** THIS IS NOT FINISHED *********/
/// THIS MUST BECOME SOMETHING LIKE A ROW COPY              Array.set( elems[i], sublist.toArray() );
            String[] subarray = (String[])sublist.toArray();
            for (int j=0; j<2; j++)
            {
                elems[i][j] = subarray[j];
            }
        }
        // create the ARRAY by calling the constructor 
        oracle.sql.ARRAY arrayanswer = new oracle.sql.ARRAY(desc, conn, elems); 
        return arrayanswer;
    }


    /**
     * Wrapper of Cyc Java API askWithVariable.
     * 
     * Asks a question with one variable.
     *
     * @param query is e.g. (#$isa ?X #$Employee)
     * @param var is the variable of which bindings will be returned
     * @param mt is e.g. InferencePSC
     * @return oracle array type with bindings.
     */
    public static oracle.sql.ARRAY askWithVariable (String query, String var, String mt)
        throws RuntimeException, CycApiException, IOException, SQLException, UnknownHostException {
        try {
            CycList cquery = cycAccess.makeCycList (query);
            CycVariable cvar = new CycVariable (var);
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList answer = cycAccess.askWithVariable (cquery, cvar, cmt);

            return cycListToArray (answer);
        }
            catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Wrapper of Cyc Java API askWithVariable.
     * 
     * Asks a question with one variable.
     *
     * @param query is e.g. (#$isa ?X #$Employee)
     * @param var is the variable of which bindings will be returned
     * @param mt is e.g. InferencePSC
     * @param backchain is the maximum number of backchain steps to perform
     * @return oracle array type with bindings.
     */
    public static oracle.sql.ARRAY askWithVariable (String query, String var, String mt, int backchain )
        throws RuntimeException, CycApiException, IOException, SQLException, UnknownHostException {
        try {
            CycList cquery = cycAccess.makeCycList (query);
            CycVariable cvar = new CycVariable (var);
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList answer = cycAccess.askWithVariable (cquery, cvar, cmt, backchain );

            return cycListToArray (answer);
        }
            catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Wrapper of Cyc Java API askWithVariables.
     * 
     * Asks a question with one variable.
     *
     * @param query is e.g. (#$isa ?X #$Employee)
     * @param vars is an array with all variables of which bindings will be returned
     * @param mt is e.g. InferencePSC
     * @return oracle array type with bindings.
     */
    public static oracle.sql.ARRAY askWithVariables (String query, String vars, String mt)
        throws RuntimeException, CycApiException, IOException, SQLException, UnknownHostException {
        try {
            String[] varsArray = Strings.split(vars);       // van deze 5 regels een nieuwe functie maken
            ArrayList cVars = new ArrayList ();                     //
            for (int i=0; i<varsArray.length; i++)                        //
            {
                  cVars.add (CycObjectFactory.makeCycVariable(varsArray[i]));
            }
            CycList cquery = cycAccess.makeCycList (query);
//            CycVariable cvar = new CycVariable (var);
            CycFort cmt = cycAccess.getKnownConstantByName (mt);
            CycList answer = cycAccess.askWithVariables (cquery, cVars, cmt);

//            return cycListToNestedArray (answer);
            return cycListToArray (answer);
        }
            catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     *
     * Returns 1 if the ground query is true in the knowledge base, 0 otherwise.
     * 
     * @param query the query to be asked in the knowledge base
     * @param mt the microtheory in which the query is asked
     * @return 0 or 1 iff the query is true in the knowledge base
     */
    public static int isQueryTrue(String query, String mt) 
        throws RuntimeException {
        try {
            int answer = 0;
            CycList cquery = cycAccess.makeCycList(query);
            CycFort cmt = cycAccess.getKnownConstantByName(mt);
            if( cycAccess.isQueryTrue(cquery, cmt) ) {
                answer = 1;
            }
            return answer;
        }
            catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Get Backchain Rules for a given predicate
     * Please note!!! This is far from official or complete!!
     */
    public static oracle.sql.ARRAY getBackChainRules (String predicate)
        throws RuntimeException {
        try {
            CycList rules = cycAccess.getBackchainRules (
                cycAccess.getKnownConstantByName (predicate),
                cycAccess.getKnownConstantByName ("VWSRegelingHulpmiddelenMt"));
            return cycListToArray (rules);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is void.
     *
     * @param command the command string or CycList
     */
    public static void converseVoid (String command)
        throws RuntimeException {
        try {
            cycAccess.converseVoid( command );
        }
        catch (Exception e) {
  //          throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a list.  The symbol
     * nil is returned as the empty list.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static oracle.sql.ARRAY converseList (String command)
        throws RuntimeException {
        try {
            CycList list = cycAccess.converseList (command);
            return cycListToArray (list);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned as a string.  The symbol
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static oracle.sql.ARRAY converseString (String command)
        throws RuntimeException {
        try {
            CycList list = cycAccess.converseList (command);
            return cycListToArray (list);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned is an object (e.g. CycConstant)
     * Convert to string to enable conversion to VARCHAR2.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static String converseObjectToString (String command)
        throws RuntimeException {
        try {
            Object cycobject = cycAccess.converseObject( command );
            return cycobject.toString();
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned is an list
     * Convert to cyclified string to enable conversion to VARCHAR2.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static String converseEscapedList (String command)
        throws RuntimeException {
        try {
            CycList list  = cycAccess.converseList (command);
            return list.cyclifyWithEscapeChars();
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned is an list
     * Convert to cyclified string to enable conversion to VARCHAR2.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static String getKnownConstantByName (String name)
        throws RuntimeException {
        try {
            CycConstant constant = cycAccess.getKnownConstantByName (name);
            return constant.cyclify();
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
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
    public static synchronized void kill (String name)
        throws IOException, UnknownHostException, CycApiException {
        CycConstant cycConstant = cycAccess.getKnownConstantByName (name);
        cycAccess.kill(cycConstant);
    }

    /**
     * Converses with Cyc to perform an API command whose result is returned is an list
     * Convert to cyclified string to enable conversion to VARCHAR2.
     *
     * @param command the command string or CycList
     * @return the result of processing the API command
     */
    public static String escapeList(String cyclist)
        throws RuntimeException {
        try {
            CycList list  = cycAccess.makeCycList(cyclist);
            return list.cyclifyWithEscapeChars();
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage() );
        }
    }


}