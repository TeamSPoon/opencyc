prompt Create's an 'after logon on schema' trigger
prompt to automatically connect to OpenCyc.
prompt edit the file if OpenCyc doesn't run on the same server
prompt as the database server.
CREATE OR REPLACE TRIGGER "AFTERLOGON"
	AFTER LOGON ON SCHEMA
BEGIN
-- Make a connection to OpenCyc running on localhost.
    cyc.makeConnection( );

-- Alternatively, make a connection to OpenCyc running on a different host.
/*  cyc.makeConnection( '10.20.30.40' ); */

END;
/
quit
/
