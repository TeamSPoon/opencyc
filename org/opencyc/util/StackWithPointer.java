package org.opencyc.util;

/*****************************************************************************
 * Extends the <tt>Stack</tt> class to provide a stack pointer.
 *
 * @version $0.1$
 * @author
 *      Stephen L. Reed<P>
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 *****************************************************************************/

import java.util.Stack;

public class StackWithPointer extends Stack {
    /**
     * Stack pointer.
     */
    public int sp = 0;

    public StackWithPointer() {
    }

    /**
     *
     * Push the argument onto the stack.
     * @param item object to be pushed onto the <tt>Stack</tt>
     * @return Object that was pushed onto the <tt>Stack</tt>
     *
     */
    public Object push ( Object item ) {
        sp++;
        return super.push(item);
    }

    /**
     *
     * Return the top of the stack, setting the new top of stack
     * item.
     * @return <tt>Object</tt> that was on the top of the <tt>Stack</tt>
     *
     */
    public Object pop() {
        --sp;
        return super.pop();
    }
}
