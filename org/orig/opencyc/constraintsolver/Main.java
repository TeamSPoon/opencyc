package org.opencyc.constraintsolver;

/**
 * Main program for a finite domain constraint solver optimized to work with the
 * OpenCyc Knowledge Base.<p>
 *
 * @version $Id$
 * @author Stephen Reed
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 */

import org.opencyc.cycobject.*;
public class Main {

    public Main() {
    }
    public static void main(String[] args) {
        //org.opencyc.cycobject.UnitTest.runTests();
        org.opencyc.constraintsolver.UnitTest.runTests();
    }
}