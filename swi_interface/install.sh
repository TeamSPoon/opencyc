#!/bin/sh

cat */interface.pl > opencyc.pl

cat */e2c.pl > e2c.pl

cp *.pl  $SWI_HOME/library

pl -g "[opencyc],testOpenCyc."

pl -g "[e2c],testE2C."





