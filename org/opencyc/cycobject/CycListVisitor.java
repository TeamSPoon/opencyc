package org.opencyc.cycobject;

import java.util.*;

/*****************************************************************************
 * Implements an <tt>Enumeration<tt> for <tt>CycList</tt> objects which traverses
 * recursively into embedded CycLists, in a depth-first fashion, returning the
 * objects which are both non-CycList and non-nil.
 *
 * @version $0.1$
 * @author
 *      Stephen L. Reed<P>
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 *****************************************************************************/

public class CycListVisitor implements Enumeration {

    /**
     * Contains the next <tt>Object</tt> in the sequence of non-CycList
     * elements of the <tt>CycList</tt> tree structure.
     */
    protected Object nextElement = null;

    /**
     * Stack of <tt>CycList</tt> <tt>Iterators</tt>
     */
    protected Stack iterators = new Stack();


    /**
     * Construct a new <tt>CycListEnumeration</tt> object.
     *
     * @param the <tt>CycList</tt> for recursive enumeration.
     */
    public CycListVisitor(CycList cycList) {
        iterators.push(cycList.iterator());
        getNextElement();
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return  <tt>true</tt> if and only if this enumeration object
     *           contains at least one more element to provide;
     *          <tt>false</tt> otherwise.
     */
    public boolean hasMoreElements() {
        return nextElement != null;
    }

    /**
     * Returns the next element of this enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return     the next element of this <tt>Enumeration</tt>.
     * @exception  NoSuchElementException  if no more elements exist.
     */
    public Object nextElement() {
        if (nextElement == null)
            throw new NoSuchElementException();
        Object answer = nextElement;
        // Stay one ahead to facilitate the determination of hasMoreElements.
        getNextElement();
        return answer;
    }

    /**
     * Get the next element in the sequence.  This method uses recursive descent.
     */
    protected void getNextElement() {
        nextElement = null;
        if (iterators.empty())
            return;
        while (true) {
            Iterator iterator = (Iterator) iterators.peek();
            if (! iterator.hasNext()) {
                iterators.pop();
                return;
            }
            Object element = iterator.next();
            if (element.equals(CycSymbol.nil))
                continue;
            if (! (element instanceof CycList)) {
                nextElement = element;
                return;
            }
            iterators.push(((CycList) element).iterator());
            getNextElement();
        }
    }

}