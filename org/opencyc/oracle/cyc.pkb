CREATE OR REPLACE PACKAGE BODY CYC
IS
/***************************************************************************
 *
 * Package body: CYC
 *
 * Interface to java stored procedures that interface with Cyc.
 *
 * @author Yeb Havinga
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
 * Naming conventions:
 * IN parameters 	'<name>_in'
 * OUT parameters	'<name>_out'
 * Cursors		'<name>_cursor'
 * Records		'<name>_record'
 * All other variables (always internal to procedure) without in, out, record or cursors.
 * Internal procs: all lowercase, separated by _
 * External procs: like the external procedures.
 * Please note: Oracle is case insensitive.
 * 
 ***************************************************************************/

/***************************************************************************
 *
 * Make a persistent connection to CYC.
 *
 * Currently, only persistent connections to Cyc (see CycAccess.java) are
 * supported in this Oracle -> Cyc interface.
 *
 ***************************************************************************/
PROCEDURE makeConnection
AS LANGUAGE JAVA
	NAME 'CycJsprocs.makeConnection()';

/***************************************************************************
 *
 * End the connection to CYC.
 *
 ***************************************************************************/
PROCEDURE endConnection
AS LANGUAGE JAVA
	NAME 'CycJsprocs.endConnection()';

/***************************************************************************
 *
 * Make a new constant.
 *
 ***************************************************************************/
PROCEDURE makeCycConstant( constant_in IN VARCHAR2 )
AS LANGUAGE JAVA
	NAME 'CycJsprocs.makeCycConstant( java.lang.String )';

/***************************************************************************
 *
 * Assert a GAF
 *
 ***************************************************************************/
PROCEDURE assertGaf( gaf_in IN VARCHAR2, mt_in IN VARCHAR2 )
AS LANGUAGE JAVA
	NAME 'CycJsprocs.assertGaf( java.lang.String, java.lang.String )';

/***************************************************************************
 *
 * Assert a GAF as a FUNCTION, can be called from SQL.
 *
 ***************************************************************************/
FUNCTION assertGaf( gaf_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.assertGafFunction( java.lang.String, java.lang.String )
		return java.lang.String';

/***************************************************************************
 *
 * Is a query True. Returns 1 if true, 0 if false.
 *
 ***************************************************************************/
FUNCTION isQueryTrue( query_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN NUMBER
AS LANGUAGE JAVA
	NAME 'CycJsprocs.isQueryTrue( java.lang.String, java.lang.String )
		return int';

/***************************************************************************
 *
 * Performs a query, returns a table of varchar2 with bindings of the variable.
 *
 ***************************************************************************/
FUNCTION askWithVariable( query_in IN VARCHAR2, variable_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN cyclist_type
AS LANGUAGE JAVA
	NAME 'CycJsprocs.askWithVariable( java.lang.String, java.lang.String, java.lang.String )
		return oracle.sql.ARRAY';

/***************************************************************************
 *
 * Get the backchain rules for a predicate.
 *
 ***************************************************************************/
FUNCTION getBackChainRules( predicate_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.getBackChainRules( java.lang.String )
		return java.lang.String';

END cyc;
/
QUIT
/