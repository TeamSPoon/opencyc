create user cyctest identified by cyctest
default tablespace users temporary tablespace temp
/
grant connect,resource,javauserpriv to cyctest
/
grant select any table to cyctest
/
begin
 dbms_java.grant_permission( 'CYCTEST', 'SYS:java.util.PropertyPermission',
'org.opencyc.util.log', 'write' );
end;
/
quit
/
