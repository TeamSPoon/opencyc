package org.opencyc.cycobject;

/*****************************************************************************
 * Provides a comparator for the <tt>sort</tt> method of the
 * <tt>CycList</tt> class.
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

public class CycListComparator implements Comparator {

    /**
     * Construct a new CycListComparator object.
     */
    public CycListComparator() {
    }

    /**
     * Compare two <tt>CycList</tt> elements, according to their string
     * representations.
     *
     * @param o1 an Object for comparison
     * @param o2 another Object for comparison.
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     * @exception ClassCastException - if the arguments' types prevent them from
     * being compared by this Comparator
     */
    public int compare (Object o1, Object o2) {
        String string1 = o1.toString();
        String string2 = o2.toString();
        return string1.compareTo(string2);
    }

    /**
     * Return <tt>true</tt> if some other object is equal to this <tt>Comparator</tt>
     *
     * @param object the reference object with which to compare.
     * @return <tt>true</tt> only if the specified object is also a
     * comparator and it imposes the same ordering as this comparator.
     */
     public boolean equals (Object object) {
        return object instanceof CycListComparator;
     }
}