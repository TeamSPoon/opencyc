#!/bin/bash
loadjava -f -verbose -oci8 -u cyctest/cyctest ~/opencyc-0.6.0/lib/jakarta-oro-2.0.6.jar &
loadjava -f -verbose -oci8 -u cyctest/cyctest ~/opencyc-0.6.0/lib/violinStrings.jar &
loadjava -f -verbose -oci8 -u cyctest/cyctest ~/opencyc-0.6.0/lib/jdom.jar &
loadjava -f -verbose -oci8 -u cyctest/cyctest ~/opencyc-0.6.0/lib/xmlParserAPIs.jar &
loadjava -f -verbose -oci8 -u cyctest/cyctest oracle-opencyc.jar &
