package org.opencyc.cycobject;

import java.util.*;
import java.io.*;
import org.opencyc.util.*;

/**
 * Provides the behavior and attributes of an OpenCyc list, typically used
 * to represent assertions in their external (EL) form.
 *
 * @version $0.1$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 OpenCyc.org, license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class CycList extends ArrayList {

    private boolean isProperList = true;
    private Object dottedElement;

    /**
     * Constructs a new empty <tt>CycList</tt> object.
     */
    public CycList() {
    }

    /**
     * Constructs a new <tt>CycList</tt> object, containing the elements of the
     * specified collection, in the order they are returned by the collection's iterator.
     *
     * @param c the collection of assumed valid OpenCyc objects.
     */
    public CycList(Collection c) {
        super(c);
    }

    /**
     * Constructs a new <tt>CycList</tt> object, containing as its first element
     * <tt>firstElement</tt>, and containing as its remaining elements the
     * contents of the <tt>Collection</tt> remaining elements.
     *
     * @param firstElement the object which becomes the head of the <tt>CycList</tt>
     * @param remainingElements a <tt>Collection</tt>, whose elements become the
     * remainder of the <tt>CycList</tt>
     */
    public CycList(Object firstElement,
                   Collection remainingElements) {
        this.add(firstElement);
        addAll(remainingElements);
    }

    /**
     * Constructs a new <tt>CycList</tt> object, containing as its sole element
     * <tt>element</tt>
     *
     * @param element the object which becomes the head of the <tt>CycList</tt>
     */
    public CycList(Object element) {
        this.add(element);
    }

    /**
     * Constructs a new <tt>CycList</tt> object, containing as its first element
     * <tt>element1</tt>, and <tt>element2</tt> as its second element.
     *
     * @param element1 the object which becomes the head of the <tt>CycList</tt>
     * @param element2 the object which becomes the second element of the <tt>CycList</tt>
     */
    public CycList(Object element1, Object element2) {
        this.add(element1);
        this.add(element2);
    }

    /**
     * Constructs a CycList using the semantics of Lisp symbolic expressions.<br>
     * 1.  construct(a, NIL) --> (a)<br>
     * 2.  construct(a, b) --> (a . b)<br>
     *
     * @param object1 the first <tt>Object</tt> in the <tt>CycList</tt>
     * @param object2 <tt>NIL</tt> or an <tt>Object</tt>
     * @return <tt>CycList</tt> (object) if <tt>object2</tt> is <tt>NIL</tt>,
     * otherwise return the improper <tt>CycList</tt> (object1 . object2)
     */
    public static CycList construct(Object object1, Object object2) {
        CycList cycList = new CycList(object1);
        if (object2.equals(CycSymbol.nil))
            return cycList;
        if (object2 instanceof CycList) {
            cycList.addAll((CycList) object2);
            return cycList;
        }
        cycList.setDottedElement(object2);
        return cycList;
    }

    /**
     * Constructs a new <tt>CycList<tt> object by parsing a string.
     *
     * @param string the string in CycL external (EL). For example:<BR>
     * <code>(#$isa #$Dog #$TameAnimal)</code>
     */
    public CycList(String string) {
        CycList cycList = (new CycListParser()).read(string);
        addAll(cycList);
        if (! cycList.isProperList())
            setDottedElement(cycList.getDottedElement());
    }

    /**
     * Gets the dotted element.
     *
     * @return the <tt>Object</tt> which forms the dotted element of this <tt>CycList</tt>
     */
    public Object getDottedElement() {
        return dottedElement;
    }

    /**
     * Sets the dotted element and set the improper list attribute to <tt>true</tt>.
     */
    public void setDottedElement(Object dottedElement) {
        this.dottedElement = dottedElement;
        this.isProperList = false;
    }

    /**
     * Returns <tt>true</tt> if this is a proper list.
     *
     * @return <tt>true</tt> if this is a proper list, otherwise return <tt>false</tt>
     */
    public boolean isProperList() {
        return isProperList;
    }
    /**
     * Answers true iff the CycList contains valid elements.  This is a necessary, but
     * not sufficient condition for CycL well-formedness.
     */
    public boolean isValid() {
        for (int i = 0; i < this.size(); i++) {
            Object object = this.get(i);
            if (object instanceof String ||
                object instanceof Integer ||
                object instanceof Long ||
                //object instanceof GUID ||
                object instanceof Float ||
                object instanceof CycConstant ||
                object instanceof CycNart)
                continue;
            else if (object instanceof CycList) {
                if (! ((CycList) object).isValid())
                     return false;
            }
            else
                return false;
        }
        return true;
    }

    /**
     * Returns the first element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the first element of the list.
     */
    public Object first() {
        if (size() == 0)
            throw new RuntimeException("First element not available for an empty CycList");
        return this.get(0);
    }

    /**
     * Returns the second element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the first second of the list.
     */
    public Object second() {
        if (size() < 1)
            throw new RuntimeException("First element not available for an empty CycList");
        return this.get(1);
    }

    /**
     * Returns the third element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the third element of the list.
     */
    public Object third() {
        if (size() < 2)
            throw new RuntimeException("First element not available for an empty CycList");
        return this.get(2);
    }

    /**
     * Returns the CycList after removing the first element.
     *
     * @ret urn new <tt>CycList</tt> with first element removed.
     */
    public CycList rest() {
        if (this.size() == 0)
            throw new RuntimeException("Cannot remove first element of an empty list.");
        CycList cycList = new CycList(this);
        cycList.remove(0);
        return cycList;
    }

    /**
     * Returns a new <tt>List</tt> whose elements are the reverse of
     * this <tt>CycList</tt>, which is unaffected.
     *
     * @return new <tt>CycList</tt> with elements reversed.
     */
    public CycList reverse() {
        if (! isProperList)
           throw new RuntimeException(this + " is not a proper list and cannot be reversed");
        CycList result = new CycList();
        for (int i = (this.size() - 1); i >= 0; i--)
            result.add(this.get(i));
        return result;
    }

    /**
     * Returns a <tt>String</tt> representation of this
     * <tt>List</tt>.
     */
    public String toString() {
        StringBuffer result = new StringBuffer("(");
        for (int i = 0; i < this.size(); i++) {
            if (i > 0)
                result.append(" ");
            Object element = this.get(i);
            if (element instanceof String)
                result.append("\"" + element + "\"");
            else
                result.append(element.toString());
        }
        if (!isProperList) {
            result.append(" . ");
            if (dottedElement instanceof String) {
                result.append("\"");
                result.append(dottedElement);
                result.append("\"");
            }
            else
                result.append(this.dottedElement.toString());
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Returns a cyclified string representation of the OpenCyc <tt>CycList</tt>.
     * Embedded constants are prefixed with ""#$".
     *
     * @return a <tt>String</tt> representation in cyclified form.
     *
     */
    public String cyclify() {
        StringBuffer result = new StringBuffer("(");
        String cyclifiedObject = null;
        for (int i = 0; i < this.size(); i++) {
            Object object = this.get(i);
            if (object instanceof CycConstant)
                cyclifiedObject = ((CycConstant) object).cyclify();
            else if (object instanceof CycNart)
                cyclifiedObject = ((CycNart) object).cyclify();
            else if (object instanceof CycVariable)
                cyclifiedObject = ((CycVariable) object).cyclify();
            else if (object instanceof String)
                cyclifiedObject = "\"" + object + "\"";
            else if (object instanceof CycList)
                cyclifiedObject = ((CycList) object).cyclify();
            else
                cyclifiedObject = object.toString();
            if (i > 0)
                result.append(" ");
            result.append(cyclifiedObject);
        }
        if (! isProperList) {
            result.append(" . ");
            if (dottedElement instanceof CycConstant)
                cyclifiedObject = ((CycConstant) dottedElement).cyclify();
            else if (dottedElement instanceof CycNart)
                cyclifiedObject = ((CycNart) dottedElement).cyclify();
            else if (dottedElement instanceof CycList)
                cyclifiedObject = ((CycList) dottedElement).cyclify();
            else
                cyclifiedObject = dottedElement.toString();
            result.append(cyclifiedObject);
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Returns a new CycList, which is sorted in the default collating sequence.
     *
     * @return a new <tt>CycList</tt>, sorted in the default collating sequence.
     */
    public CycList sort() {
        CycList sortedList = new CycList(this);
        Collections.sort(sortedList ,new CycListComparator());
        return sortedList;
    }

    /**
     * Returns a <tt>CycListVisitor</tt> enumeration of the non-CycList and non-nil elements.
     *
     * @return a <tt>CycListVisitor</tt> enumeration of the non-CycList and non-nil elements.
     */
    public CycListVisitor cycListVisitor() {
        return new CycListVisitor(this);
    }

}
