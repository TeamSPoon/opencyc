package org.opencyc.cycobject;

/**
 * OpenCyc <tt>CycAssertion</tt> object to model the attributes and behavior of an assertion .<p>
 *
 * @version $Id$
 * @author Stephen Reed
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 */

public class CycAssertion {

    private CycList cycList;

    public CycAssertion(CycList cycList) {
        this.cycList = cycList;
    }

    public CycList asCycList() {
        return cycList;
    }

    public boolean equals(Object object) {
        if (! (object instanceof CycAssertion))
            return false;
        CycAssertion cycAssertion = (CycAssertion) object;
        return cycList.equals(cycAssertion.asCycList());
    }

    public String toString() {
        return cycList.toString();
    }

    public String cyclify() {
        return cycList.cyclify();
    }
}