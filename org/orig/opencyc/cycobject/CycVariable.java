package org.opencyc.cycobject;


/*****************************************************************************
 * Provides the behavior and attributes of an OpenCyc variable, typically used
 * in rule and query expressions.
 *
 * @version $0.1$
 * @author
 *      Stephen L. Reed<P>
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 *****************************************************************************/

import java.util.*;

public class CycVariable {

    /**
     * Cache of CycVariables, so that a reference to an existing <tt>CycVariable</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static HashMap cache = new HashMap();

    /**
     * The variable represented as a <tt>String</tt>.
     */
    protected String variableName;

    /**
     * Construct a new <tt>CycVariable</tt> object.
     *
     * @param variableName a <tt>String</tt> name.
     */
    public static CycVariable makeCycVariable(String variableName) {
        if (cache.containsKey(variableName))
            return (CycVariable) cache.get(variableName);
        else {
            CycVariable cycVariable = new CycVariable(variableName);
            cache.put(variableName, cycVariable);
            return cycVariable;
        }
    }

    /**
     * Construct a new <tt>CycVariable</tt> object.  Non-public to enforce
     * use of the object cache.
     *
     * @param variableName the <tt>String</tt> name of the <tt>CycVariable</tt>.
     */
    protected CycVariable(String variableName) {
        if (variableName.startsWith("?"))
            this.variableName = variableName.substring(1);
        else
            this.variableName = variableName;
    }

    /**
     * Return the string representation of the <tt>CycVariable</tt>
     *
     * @return the representation of the <tt>CycVariable</tt> as a <tt>String</tt>
     */
    public String toString() {
        return variableName;
    }

    /**
     * Return the OpenCyc representation of the <tt>CycVariable</tt>
     *
     * @return the OpenCyc representation of the <tt>CycVariable</tt> as a
     * <tt>String</tt> prefixed by "?"
     */
    public String cyclify() {
        return "?" + variableName;
    }

    /**
     * Return <tt>true</tt> some object equals this <tt>CycVariable</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycVariable))
            return false;
        return ((CycVariable) object).toString().equals(variableName);
    }

    /**
     * Reset the <tt>CycVariable</tt> cache.
     */
    public static void resetCache() {
        cache = new HashMap();
    }

    /**
     * Retrieve the <tt>CycVariable</tt> with <tt>variableName</tt>,
     * returning null if not found in the cache.
     *
     * @return a <tt>CycVariable</tt> if found in the cache, otherwise
     * <tt>null</tt>
     */
    public static CycVariable getCache(String variableName) {
        if (cache.containsKey(variableName))
            return (CycVariable) cache.get(variableName);
        else
            return null;
    }

    /**
     * Remove the <tt>CycVariable</tt> from the cache if it is contained within.
     */
    public static void removeCache(CycVariable cycVariable) {
        if (cache.containsKey(cycVariable.variableName))
            cache.remove(cycVariable.variableName);
    }

    /**
     * Return the size of the <tt>CycVariable</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycVariable</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }
}