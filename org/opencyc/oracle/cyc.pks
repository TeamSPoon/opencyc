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
AS TABLE OF VARCHAR2(200);
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

FUNCTION assertgaf( gaf_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN VARCHAR2;

FUNCTION isQueryTrue( query_in IN VARCHAR2, mt_in IN VARCHAR2 )
RETURN NUMBER;

FUNCTION askWithVariable( query_in IN VARCHAR2, variable_in IN VARCHAR2, mt_in VARCHAR2 )
RETURN cyclist_type;

FUNCTION getBackChainRules( predicate_in IN VARCHAR2 )
RETURN VARCHAR2;

END cyc;
/
QUIT
/
