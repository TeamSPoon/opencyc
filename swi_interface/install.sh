#!/bin/sh

if [ -z "$SWI_HOME" ] ; then
  echo SWI_HOME is not set. try
  echo export SWI_HOME=/usr/lib/pl-5.0.6
  echo exiting..
  exit 1;
fi

cd `dirname $0`
pwd
# cat */interface.pl */utilities.pl > opencyc.pl

# cat */e2c.pl > e2c.pl

cp *.pl  $SWI_HOME/library

pl -g "ensure_loaded(library(opencyc)),testOpenCyc."

pl -g "ensure_loaded(library(e2c)),testE2C."

cd -






