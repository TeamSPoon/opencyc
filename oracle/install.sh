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
echo "It will 1 - drop and create the Oracle user called 'cyctest'."
echo " 2 - load java classes from the OpenCyc Java API into cyctests schema."
echo " 3 - load the Oracle OpenCyc Interface Java source and PL/SQL package."
echo " 4 - execute a small test script."
echo 

echo "What is the directory in which you installed OpenCyc?"
echo "example: /home/yeb/opencyc-0.7.0"
read OPENCYC_DIR

echo 
echo What is the password of the Oracle system user?
echo This is needed to drop and create the Oracle user called 'cyctest'.
echo If you press enter, this script will end.

read SYSTEM_PWD
if [ "$SYSTEM_PWD" = "" ]
then
  exit;
fi

#--------------------------------
ORA_USERPWD=cyctest/cyctest
echo "Deleting cyctest user. Ignore the errors at the first time."
sqlplus system/$SYSTEM_PWD @ drop-cyctest.sql
echo "Create cyctest user."
sqlplus system/$SYSTEM_PWD @ create-cyctest.sql
loadjava -f -verbose -oci8 -u $ORA_USERPWD $OPENCYC_DIR/lib/jakarta-oro-2.0.3.jar
loadjava -f -verbose -oci8 -u $ORA_USERPWD $OPENCYC_DIR/lib/ViolinStrings.jar
loadjava -f -verbose -oci8 -u $ORA_USERPWD $OPENCYC_DIR/lib/jdom.jar
loadjava -f -verbose -oci8 -u $ORA_USERPWD $OPENCYC_DIR/lib/xerces.jar
loadjava -f -verbose -oci8 -u $ORA_USERPWD $OPENCYC_DIR/lib/jug.jar
loadjava -f -verbose -oci8 -u $ORA_USERPWD oracle-opencyc.jar
sqlplus $ORA_USERPWD @ resolve-cycaccess.sql
loadjava -f -resolve -verbose -oci8 -u $ORA_USERPWD CycJsprocs.java
sqlplus $ORA_USERPWD @ cyc.pks
sqlplus $ORA_USERPWD @ cyc.pkb
sqlplus $ORA_USERPWD @ test.sql
