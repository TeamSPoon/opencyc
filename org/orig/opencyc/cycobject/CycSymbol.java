package org.opencyc.cycobject;

/*****************************************************************************
 * Provides the behavior and attributes of an OpenCyc symbol, typically used
 * to represent api function names, and non <tt>CycConstant</tt> parameters.
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

public class CycSymbol {

    /**
     * Cache of CycSymbols, so that a reference to an existing <tt>CycSymbol</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static HashMap cache = new HashMap();

    /**
     * Built in CycSymbols.
     */
    public static CycSymbol nil = makeCycSymbol("NIL");
    public static CycSymbol quote = makeCycSymbol("QUOTE");
    public static CycSymbol cons = makeCycSymbol("CONS");
    public static CycSymbol dot = makeCycSymbol(".");

    /**
     * The symbol represented as a <tt>String</tt>.
     */
    protected String symbolName;

    /**
     * Construct a new <tt>CycSymbol</tt> object.
     *
     * @param symbolName a <tt>String</tt> name.
     */
    public static CycSymbol makeCycSymbol(String symbolName) {
        if (cache.containsKey(symbolName))
            return (CycSymbol) cache.get(symbolName);
        else {
            CycSymbol cycSymbol = new CycSymbol(symbolName);
            cache.put(symbolName, cycSymbol);
            return cycSymbol;
        }
    }

    /**
     * Construct a new <tt>CycSymbol</tt> object.  Non-public to enforce
     * use of the object cache.
     *
     * @param symbolName the <tt>String</tt> name of the <tt>CycSymbol</tt>.
     */
    protected CycSymbol(String symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Return the string representation of the <tt>CycSymbol</tt>
     *
     * @return the representation of the <tt>CycSymbol</tt> as a <tt>String</tt>
     */
    public String toString() {
        return symbolName;
    }

    /**
     * Return <tt>true</tt> some object equals this <tt>CycSymbol</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycSymbol))
            return false;
        return ((CycSymbol) object).toString().equals(symbolName);
    }

    /**
     * Reset the <tt>CycSymbol</tt> cache.
     */
    public static void resetCache() {
        cache = new HashMap();
        nil = makeCycSymbol("NIL");
        quote = makeCycSymbol("QUOTE");
        cons = makeCycSymbol("CONS");
        dot = makeCycSymbol(".");
    }

    /**
     * Retrieve the <tt>CycSymbol</tt> with <tt>symbolName</tt>,
     * returning null if not found in the cache.
     *
     * @return a <tt>CycSymbol</tt> if found in the cache, otherwise <tt>null</tt>
     */
    public static CycSymbol getCache(String symbolName) {
        if (cache.containsKey(symbolName))
            return (CycSymbol) cache.get(symbolName);
        else
            return null;
    }

    /**
     * Remove the <tt>CycSymbol</tt> from the cache if it is contained within.
     */
    public static void removeCache(CycSymbol cycSymbol) {
        if (cache.containsKey(cycSymbol.symbolName))
            cache.remove(cycSymbol.symbolName);
    }

    /**
     * Return the size of the <tt>Guid</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycSymbol</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }

}