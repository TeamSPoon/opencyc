#!/bin/bash
echo 
echo "Oracle OpenCyc Interface, Copyright (C) 2002 Yeb Havinga."
echo "The Oracle OpenCyc interface comes with ABSOLUTELY NO WARRANTY."
echo "Enter 'l' for details of the GNU Public License."
echo "Press enter to continue."
read INPUT

if [ "$INPUT" = "l" ]
then
  more license.txt
  echo Press enter to continue.
  read INPUT
fi

echo
echo "This is the default install script for the Oracle OpenCyc interface."
echo "It will 
echo " 1 - Ask a few things.
echo " 2 - load java classes from the OpenCyc Java API into the users schema."
echo " 3 - load the Oracle OpenCyc Interface Java source and PL/SQL package."
echo " 4 - execute a small test script that connect to OpenCyc at localhost."
echo 

echo "What is the directory in which you installed OpenCyc?"
echo "example: /home/yeb/opencyc-0.7.0"
read OPENCYC_DIR

echo "What is the Oracle username to import the interface into?"
echo "This user will be created and granted rights to."
echo "example: cyctest"
read USERNAME

echo "What is this users password?"
read PASSWORD

echo 
echo What is the password of the Oracle system user?
echo This is needed to drop and create the Oracle user called $USERNAME
echo If you press enter, this script will end.

read SYSTEM_PWD
if [ "$SYSTEM_PWD" = "" ]
then
  exit;
fi



#--------------------------------
##echo "Deleting cyctest user. Ignore the errors at the first time."
##sqlplus system/$SYSTEM_PWD @ drop-cyctest.sql
sqlplus system/$SYSTEM_PWD @ create-user.sql $USERNAME $PASSWORD
sqlplus system/$SYSTEM_PWD @ grant.sql $USERNAME $PASSWORD
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD $OPENCYC_DIR/lib/jakarta-oro-2.0.3.jar
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD $OPENCYC_DIR/lib/ViolinStrings.jar
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD $OPENCYC_DIR/lib/jdom.jar
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD $OPENCYC_DIR/lib/xerces.jar
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD $OPENCYC_DIR/lib/jug.jar
loadjava -f -verbose -oci8 -u $USERNAME/$PASSWORD oracle-opencyc.jar
sqlplus $USERNAME/$PASSWORD @ resolve-cycaccess.sql
loadjava -f -resolve -verbose -oci8 -u $USERNAME/$PASSWORD CycJsprocs.java
sqlplus $USERNAME/$PASSWORD @ cyc.pks
sqlplus $USERNAME/$PASSWORD @ cyc.pkb
sqlplus $USERNAME/$PASSWORD @ test.sql
