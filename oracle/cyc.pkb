CREATE OR REPLACE PACKAGE BODY CYC
IS
/***************************************************************************
 *
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
 *
 * ========================================================================
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
 * Assert a SENTENCE
 *
 ***************************************************************************/
PROCEDURE assertWithTranscript( sentence_in IN VARCHAR2, mt_in IN VARCHAR2 )
AS LANGUAGE JAVA
	NAME 'CycJsprocs.assertWithTranscript( java.lang.String, java.lang.String )';

/***************************************************************************
 *
 * Assert a sentence as KE text with transcript, FUNCTION variant.
 *
 ***************************************************************************/
FUNCTION assertWithTranscript( sentence_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.assertWithTranscriptFunction( java.lang.String, java.lang.String )
		return java.lang.String';

/***************************************************************************
 *
 * Creates a new microtheory.
 *
 ***************************************************************************/
PROCEDURE createMicrotheory(
    mtname_in IN VARCHAR2,
    comment_in IN VARCHAR2,
    isamt_in IN VARCHAR2,
    genlmts_in IN cyclist_type  )
AS LANGUAGE JAVA
	NAME 'CycJsprocs.createMicrotheory(
	    java.lang.String,
	    java.lang.String,
	    java.lang.String,
	    oracle.sql.ARRAY )';

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
RETURN cyclist_type
AS LANGUAGE JAVA
	NAME 'CycJsprocs.getBackChainRules( java.lang.String )
		return oracle.sql.ARRAY';

/***************************************************************************
 *
 * Converse a command with the API that returns a cyclist
 *
 ***************************************************************************/
FUNCTION converseList( command_in IN VARCHAR2 )
RETURN cyclist_type
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseList( java.lang.String )
		return oracle.sql.ARRAY';

/***************************************************************************
 *
 * Converse a command with the API that returns a cyclist
 *
 ***************************************************************************/
FUNCTION converseString( command_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseString( java.lang.String )
		return java.lang.String';

/***************************************************************************
 *
 * Converse a command with the API that returns a object -> as string
 *
 ***************************************************************************/
FUNCTION converseObjectToString( command_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseObjectToString( java.lang.String )
		return java.lang.String';

/***************************************************************************
 *
 * Converse a command with the API that returns an object -> as cyclified string
 *
 ***************************************************************************/
FUNCTION converseEscapedList( command_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseEscapedList( java.lang.String )
		return java.lang.String';

/***************************************************************************
 *
 * Converse a command with the API that returns no result.
 *
 ***************************************************************************/
PROCEDURE converseVoid( command_in IN VARCHAR2 )
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseVoid( java.lang.String )';

/***************************************************************************
 *
 * Converse a command with the API that returns nothing (but cycjsprocs returns a cyclist)
 *
 ***************************************************************************/
FUNCTION converseVoid( command_in IN VARCHAR2 )
RETURN cyclist_type
AS LANGUAGE JAVA
	NAME 'CycJsprocs.converseList( java.lang.String )
		return oracle.sql.ARRAY';

/***************************************************************************
 *
 * Get the constant name. Return the sting
 *
 ***************************************************************************/
FUNCTION getKnownConstantByName( name_in IN VARCHAR2 )
RETURN VARCHAR2
AS LANGUAGE JAVA
	NAME 'CycJsprocs.getKnownConstantByName( java.lang.String )
		return java.lang.String';



END cyc;
/
QUIT
/
