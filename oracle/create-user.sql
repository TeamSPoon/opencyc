define username = &1
define userpwd = &2
create user &&username identified by &&userpwd
default tablespace users temporary tablespace temp
/
quit
/
