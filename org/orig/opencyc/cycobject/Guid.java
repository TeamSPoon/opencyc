package org.opencyc.cycobject;

/*****************************************************************************
 * Provides the behavior and attributes of an OpenCyc GUID (Globally Unique
 * IDentifier). Each OpenCyc constant has an associated guid.
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

public class Guid {

    /**
     * Cache of guids, so that a reference to an existing <tt>Guid</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static HashMap cache = new HashMap();

    /**
     * The GUID in string form.
     */
    protected String guidString;

    /**
     * Construct a new Guid object.
     *
     * @param guid a <tt>String</tt> form of a GUID.
     */
    public static Guid makeGuid(String guidString) {
        if (cache.containsKey(guidString))
            return (Guid) cache.get(guidString);
        else {
            Guid guid = new Guid(guidString);
            cache.put(guidString, guid);
            return guid;
        }
    }

    /**
     * Construct a new <tt>Guid</tt> object. Non-public to enforce the
     * use of the cache during object creation.
     */
    protected Guid(String guidString) {
        this.guidString = guidString;
    }

    /**
     * Return <tt>true</tt> if the object equals this object.
     *
     * @return <tt>boolean</tt> indicating equality of an object with this object.
     */
    public boolean equals(Object object) {
        if (object instanceof Guid &&
            this.guidString.equals(((Guid) object).guidString)) {
            return true;
        }
        else
            return false;
    }

    /**
     * Return a string representation of the <tt>Guid</tt>.
     *
     * @return the <tt>Guid</tt> formated as a <tt>String</tt>.
     */
    public String toString() {
        return guidString;
    }

    /**
     * Reset the <tt>Guid</tt> cache.
     */
    public static void resetCache() {
        cache = new HashMap();
    }

    /**
     * Retrieve the <tt>Guid</tt> with <tt>guidName</tt>,
     * returning null if not found in the cache.
     *
     * @return the <tt>Guid</tt> if it is found in the cache, otherwise
     * <tt>null</tt>
     */
    public static Guid getCache(String guidName) {
        if (cache.containsKey(guidName))
            return (Guid) cache.get(guidName);
        else
            return null;
    }

    /**
     * Remove the <tt>Guid</tt> from the cache if it is contained within.
     */
    public static void removeCache(Guid guid) {
        if (cache.containsKey(guid.guidString))
            cache.remove(guid.guidString);
    }

    /**
     * Return the size of the <tt>Guid</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>Guid</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }

}