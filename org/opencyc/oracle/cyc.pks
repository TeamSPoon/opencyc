--------------------------------------------------------------- 
-- Package: CYC
-- 
-- Interface to java stored procedures that interface with Cyc.
-- 
-- @author Yeb Havinga
-- 
-- Please note: Oracle is case insensitive.
----------------------------------------------------------------
CREATE OR REPLACE TYPE cyclist_type
AS TABLE OF VARCHAR2(4000);
/
----------------------------------------------------------------
-- Create the package specification.
-- Please see the package body and the java stored procedure source for more info.
----------------------------------------------------------------
CREATE OR REPLACE PACKAGE CYC
IS
PROCEDURE makeConnection;

PROCEDURE endConnection;

PROCEDURE makeCycConstant( constant_in IN VARCHAR2 );

PROCEDURE assertgaf( gaf_in IN VARCHAR2, mt_in IN VARCHAR2 );

PROCEDURE createMicrotheory(
    mtname_in IN VARCHAR2,
    comment_in IN VARCHAR2,
    isamt_in IN VARCHAR2,
    genlmts_in IN cyclist_type  );

FUNCTION assertgaf( gaf_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN VARCHAR2;

FUNCTION isQueryTrue( query_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN NUMBER;

FUNCTION askWithVariable( query_in IN VARCHAR2, variable_in IN VARCHAR2, mt_in VARCHAR2 )
RETURN cyclist_type;

FUNCTION getBackChainRules( predicate_in IN VARCHAR2 )
RETURN cyclist_type;

FUNCTION converseList( command_in IN VARCHAR2 )
RETURN cyclist_type;

PROCEDURE converseVoid( command_in IN VARCHAR2 );

FUNCTION converseVoid( command_in IN VARCHAR2 )
RETURN cyclist_type;


END cyc;
/
QUIT
/
