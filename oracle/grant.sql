define username = &1
/
grant connect,resource,javauserpriv to &&username
/
grant select any table to &username
/
--begin
-- dbms_java.grant_permission( '&&username', 'SYS:java.util.PropertyPermission',
--'org.opencyc.util.log', 'write' );
--end;
/
quit
/
