package org.opencyc.cycobject;

import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import org.opencyc.util.*;
import org.opencyc.xml.*;
import org.opencyc.api.*;
import ViolinStrings.*;

/**
 * Provides the behavior and attributes of an OpenCyc list, typically used
 * to represent assertions in their external (EL) form.
 *
 * @version $0.1$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
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

    /**
     * XML serialization tags.
     */
    public static final String cycListXMLTag = "list";
    public static final String integerXMLTag = "integer";
    public static final String doubleXMLTag = "double";
    public static final String stringXMLTag = "string";
    public static final String dottedElementXMLTag = "dotted-element";

    /**
     * XML serialization indentation.
     */
    public static int indentLength = 2;

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
        if (object2.equals(CycObjectFactory.nil))
            return cycList;
        if (object2 instanceof CycList) {
            CycList cycList2 = (CycList) object2;
            cycList.addAll(cycList2);
            if (! cycList2.isProperList)
                cycList.setDottedElement(cycList2.getDottedElement());
            return cycList;
        }
        cycList.setDottedElement(object2);
        return cycList;
    }

    /**
     * Creates and returns a copy of this <tt>CycList</tt>.
     *
     * @return a clone of this instance
     */
    public Object clone() {
        CycList newClone = new CycList(this);
        if (! this.isProperList())
            newClone.setDottedElement(this.getDottedElement());
        return newClone;
    }

    /**
     * Creates and returns a deep copy of this <tt>CycList</tt>.  In a deep copy,
     * directly embedded <tt>CycList</tt> objects are also deep copied.  Objects
     * which are not CycLists are cloned.
     *
     * @return a deep copy of this <tt>CycList</tt>
     */
    public CycList deepCopy() {
        CycList cycList = new CycList();
        if (! this.isProperList()) {
            if (this.dottedElement instanceof CycList)
                cycList.setDottedElement(((CycList) this.dottedElement).deepCopy());
            else
                cycList.setDottedElement(this.getDottedElement());
        }
        for (int i = 0; i < this.size(); i++) {
            Object element = this.get(i);
            if (element instanceof CycList)
                cycList.add(((CycList) element).deepCopy());
            else
                cycList.add(element);
        }
        return cycList;
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
                object instanceof Guid ||
                object instanceof Float ||
                object instanceof ByteArray ||
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
     * Returns true if formula is well-formed in the relevant mt.
     *
     * @param formula the given EL formula
     * @param mt the relevant mt
     * @return true if formula is well-formed in the relevant mt, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isFormulaWellFormed(CycFort mt)
        throws IOException, UnknownHostException, CycApiException {
        return CycAccess.current().isFormulaWellFormed(this, mt);
    }

    /**
     * Returns true if formula is well-formed Non Atomic Reifable Term.
     *
     * @param formula the given EL formula
     * @return true if formula is well-formed Non Atomic Reifable Term, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCycLNonAtomicReifableTerm()
        throws IOException, UnknownHostException, CycApiException {
        return CycAccess.current().isCycLNonAtomicReifableTerm(this);
    }

    /**
     * Returns true if formula is well-formed Non Atomic Un-reifable Term.
     *
     * @param formula the given EL formula
     * @return true if formula is well-formed Non Atomic Un-reifable Term, otherwise false
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public boolean isCycLNonAtomicUnreifableTerm()
        throws IOException, UnknownHostException, CycApiException {
        return CycAccess.current().isCycLNonAtomicUnreifableTerm(this);
    }

    /**
     * Creates a new <tt>CycList</tt> containing the given element.
     *
     * @param element the contents of the new <tt>CycList</tt>
     * @return a new <tt>CycList</tt> containing the given element
     */
    public static CycList list(Object element) {
        CycList result = new CycList();
        result.add(element);
        return result;
    }

    /**
     * Creates a new <tt>CycList</tt> containing the given two elements.
     *
     * @param element1 the first item of the new <tt>CycList</tt>
     * @param element2 the second item of the new <tt>CycList</tt>
     * @return a new <tt>CycList</tt> containing the given two elements
     */
    public static CycList list(Object element1,
                               Object element2) {
        CycList result = new CycList();
        result.add(element1);
        result.add(element2);
        return result;
    }

    /**
     * Creates a new <tt>CycList</tt> containing the given three elements.
     *
     * @param element1 the first item of the new <tt>CycList</tt>
     * @param element2 the second item of the new <tt>CycList</tt>
     * @param element3 the third item of the new <tt>CycList</tt>
     * @return a new <tt>CycList</tt> containing the given three elements
     */
    public static CycList list(Object element1,
                               Object element2,
                               Object element3) {
        CycList result = new CycList();
        result.add(element1);
        result.add(element2);
        result.add(element3);
        return result;
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
     * @return the <tt>Object</tt> which is the second element of the list.
     */
    public Object second() {
        if (size() < 1)
            throw new RuntimeException("Second element not available");
        return this.get(1);
    }

    /**
     * Returns the third element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the third element of the list.
     */
    public Object third() {
        if (size() < 2)
            throw new RuntimeException("Third element not available");
        return this.get(2);
    }

    /**
     * Returns the fourth element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the fourth element of the list.
     */
    public Object fourth() {
        if (size() < 3)
            throw new RuntimeException("Fourth element not available");
        return this.get(3);
    }

    /**
     * Returns the last element of the <tt>CycList</tt>.
     *
     * @return the <tt>Object</tt> which is the last element of the list.
     */
    public Object last() {
        if (size() == 0)
            throw new RuntimeException("Last element not available");
        return this.get(this.size() - 1);
    }

    /**
     * Returns the CycList after removing the first element, in in the case of a
     * dotted pair, returns the dotted element.
     *
     * @return the CycList after removing the first element, in in the case of a
     * dotted pair, returns the dotted element.
     */
    public Object rest() {
        if (this.size() == 0)
            throw new RuntimeException("Cannot remove first element of an empty list.");
        else if ((this.size() == 1) && (! this.isProperList))
            return this.getDottedElement();
        CycList cycList = new CycList(this);
        cycList.remove(0);
        return cycList;
    }

    /**
     * Adds the given element to this list if it is not already contained.
     */
    public void addNew(Object object) {
        if (! this.contains(object))
            this.add(object);
    }

    /**
     * Adds the given elements to this list if they are not already contained.
     */
    public void addAllNew(Collection objects) {
        Iterator iter = objects.iterator();
        while (true) {
            if (! iter.hasNext())
                break;
            this.addNew(iter.next());
        }
    }

    /**
     * Returns true iff this list contains duplicate elements.
     *
     * @return true iff this list contains duplicate elements
     */
    public boolean containsDuplicates() {
        if (! isProperList)
            if (this.contains(this.dottedElement))
                return true;
        for (int i = 0; i < this.size(); i++)
            for (int j = i + 1; j < this.size(); j++)
                if (this.get(i).equals(this.get(j)))
                    return true;
        return false;
    }

    /**
     * Returns a new <tt>CycList</tt> whose elements are the reverse of
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
     * Returns a <tt>CycList</tt> of the length N combinations of sublists from this
     * object.  This algorithm preserves the list order with the sublists.
     *
     * @param n the length of the sublist
     * @return a <tt>CycList</tt> of the length N combinations of sublists from this
     * object
     */
    public CycList combinationsOf(int n) {
        if (! isProperList)
           throw new RuntimeException(this + " is not a proper list");
        CycList result = new CycList();
        if (this.size() == 0 || n == 0)
            return result;
        return combinationsOfInternal(new CycList(this.subList(0, n)),
                                      new CycList(this.subList(n, this.size())));
    }

    /**
     * Provides the internal implementation <tt.combinationsOf</tt> using a recursive
     * algorithm.
     *
     * @param selectedItems a window of contiguous items to be combined
     * @param availableItems the complement of the selectedItems
     * @return a <tt>CycList</tt> of the combinations of sublists from the
     * selectedItems.
     */
    private static CycList combinationsOfInternal(CycList selectedItems, CycList availableItems) {
        CycList result = CycList.list(selectedItems);
        if (availableItems.size() == 0)
            return result;
        CycList combination = null;
        for (int i = 0; i < (selectedItems.size() - 1); i++)
            for (int j = 0; j < availableItems.size(); j++) {
                Object availableItem = availableItems.get(j);
                // Remove it (making copy), shift left, append replacement.
                combination = (CycList) selectedItems.clone();
                combination.remove(i + 1);
                combination.add(availableItem);
                result.add(combination);
            }
        CycList newSelectedItems = (CycList) selectedItems.rest();
        newSelectedItems.add(availableItems.first());
        CycList newAvailableItems = (CycList) availableItems.rest();
        result.addAll(combinationsOfInternal(newSelectedItems, newAvailableItems));
        return result;
    }

    /**
     * Returns a random ordering of the <tt>CycList</tt> without recursion.
     *
     * @return a random ordering of the <tt>CycList</tt> without recursion
     */
    public CycList randomPermutation() {
        Random random = new Random();
        int randomIndex = 0;
        CycList remainingList = (CycList) this.clone();
        CycList permutedList = new CycList();
        if (this.size() == 0)
            return remainingList;
        while (true) {
            if (remainingList.size() == 1) {
                permutedList.addAll(remainingList);
                return permutedList;
            }
            randomIndex = random.nextInt(remainingList.size() - 1);
            permutedList.add(remainingList.get(randomIndex));
            remainingList.remove(randomIndex);
        }

    }


    /**
     * Returns a new <tt>CycList</tt> with every occurrance of <tt>Object</tt> oldObject
     * replaced by <tt>Object</tt> newObject.  Substitute recursively into embedded
     * <tt>CycList</tt> objects.
     *
     * @return a new <tt>CycList</tt> with every occurrance of <tt>Object</tt> oldObject
     * replaced by <tt>Object</tt> newObject
     */
    public CycList subst(Object newObject, Object oldObject) {
        CycList result = new CycList();
        if (! isProperList)
            if (dottedElement.equals(oldObject))
                result.setDottedElement(newObject);
        for (int i = 0; i < this.size(); i++) {
            Object element = this.get(i);
            if (element.equals(oldObject))
                result.add(newObject);
            else if (element instanceof CycList)
                result.add(((CycList) element).subst(newObject, oldObject));
            else
                result.add(element);
        }
        return result;
    }

    /**
     * Returns a <tt>String</tt> representation of this
     * <tt>List</tt>.
     */
    public String toString() {
        return toStringHelper(false);
    }

    /**
     * Returns a <tt>String</tt> representation of this
     * <tt>List</tt> without causing  additional api calls to complete the name field of constants.
     */
    public String safeToString() {
        return toStringHelper(true);
    }

    /**
     * Returns a <tt>String</tt> representation of this
     * <tt>CycList</tt>.  When the parameter is true, the representation is created without causing
     * additional api calls to complete the name field of constants.
     *
     * @param safe when true, the representation is created without causing
     * additional api calls to complete the name field of constants
     * @return a <tt>String</tt> representation of this <tt>CycList</tt>
     */
    protected String toStringHelper(boolean safe) {
        StringBuffer result = new StringBuffer("(");
        for (int i = 0; i < this.size(); i++) {
            if (i > 0)
                result.append(" ");
            Object element = this.get(i);
            if (element == null)
                result.append("null");
            else if (element instanceof String)
                result.append("\"" + element + "\"");
            else if (safe) {
                try {
                    // If element understands the safeToString method, then use it.
                    Method safeToString = element.getClass().getMethod("safeToString", null);
                    result.append(safeToString.invoke(element, null));
                }
                catch (Exception e) {
                    result.append(element.toString());
                }
            }
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
   * Returns a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   * @param indent, the indent string that is added before the
   * <tt>String</tt> representation this <tt>CycList</tt>
   * @return a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   */
  public String toPrettyString(String indent) {
    StringBuffer result = new StringBuffer(indent + "(");
    for (int i = 0; i < this.size(); i++) {
      if (i > 0)
    result.append(" ");
      Object element = this.get(i);
      if (element instanceof String) {result.append("\"" + element + "\"");}
      else if (element instanceof CycList)
    {result.append("\n" + ((CycList)element).toPrettyString(indent + "  "));}
      else {result.append(element.toString());}
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
     * Embedded constants are prefixed with ""#$".  Embedded quote chars in strings
     * are escaped.
     *
     * @return a <tt>String</tt> representation in cyclified form.
     *
     */
  public String cyclifyWithEscapeChars() {
        StringBuffer result = new StringBuffer("(");
        String cyclifiedObject = null;
        for (int i = 0; i < this.size(); i++) {
            Object object = this.get(i);
            if (object == null)
                throw new RuntimeException("Invalid null element after " + result);
            if (object instanceof CycObject)
                cyclifiedObject = ((CycObject) object).cyclify();
            else if (object instanceof CycConstant)
                cyclifiedObject = ((CycConstant) object).cyclify();
            else if (object instanceof CycNart)
                cyclifiedObject = ((CycNart) object).cyclify();
            else if (object instanceof CycVariable)
                cyclifiedObject = ((CycVariable) object).cyclify();
            else if (object instanceof String) {
                String stringObject = escapeQuoteChars((String) object);
                cyclifiedObject = "\"" + stringObject + "\"";
            }
            else if (object instanceof CycList)
                cyclifiedObject = ((CycList) object).cyclifyWithEscapeChars();
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
                cyclifiedObject = ((CycList) dottedElement).cyclifyWithEscapeChars();
            else
                cyclifiedObject = dottedElement.toString();
            result.append(cyclifiedObject);
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
            if (object == null)
                throw new RuntimeException("Invalid null element after " + result);
            if (object instanceof CycObject)
                cyclifiedObject = ((CycObject) object).cyclify();
            else if (object instanceof CycConstant)
                cyclifiedObject = ((CycConstant) object).cyclify();
            else if (object instanceof CycNart)
                cyclifiedObject = ((CycNart) object).cyclify();
            else if (object instanceof CycVariable)
                cyclifiedObject = ((CycVariable) object).cyclify();
            else if (object instanceof String)
                cyclifiedObject = "\"" + (String) object + "\"";
            else if (object instanceof Double) {
                cyclifiedObject = object.toString();
            }
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
     * Inserts an escape character before each quote character in the given string.
     *
     * @param string the given string
     * @return the string with an escape character before each quote character
     */
    public String escapeQuoteChars(String string) {
        return Strings.change(string, "\"", "\\\"");
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return this.cyclifyWithEscapeChars();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>CycList</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>CycList</tt> api expression value
     */
    public Object cycListApiValue() {
        return this;
    }

    /**
     * Returns a new CycList, which is sorted in the default collating sequence.
     *
     * @return a new <tt>CycList</tt>, sorted in the default collating sequence.
     */
    public CycList sort() {
        CycList sortedList = new CycList(this);
        Collections.sort(sortedList, new CycListComparator());
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

    /**
     * Returns true if the proper list tree contains the given object anywhere in the tree.
     *
     * @param object the object to be found in the tree.
     * @return true if the proper list tree contains the given object anywhere in the tree
     */
    public boolean treeContains (Object object) {
        if (this.contains(object))
            return true;
        for (int i = 0; i < this.size(); i++) {
            Object element = this.get(i);
            if ((element instanceof CycList) &&
                (((CycList) element).treeContains(object)))
                return true;
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if the element is a member of this <tt>CycList</tt> and
     * no element in <tt>CycList</tt> otherElements precede it.
     *
     * @param element the element under consideration
     * @param otherElements the <tt>CycList</tt> of other elements under consideration
     * @return <tt>true</tt> if the element is a member of this <tt>CycList</tt> and
     * no elements in <tt>CycList</tt> otherElements contained in this <tt>CycList</tt>
     * precede it
     */
    public boolean doesElementPrecedeOthers(Object element, CycList otherElements) {
        for (int i = 0; i < this.size(); i++) {
            if (element.equals(this.get(i)))
                return true;
            if (otherElements.contains(this.get(i)))
                return false;
        }
        return false;
    }

    /**
     * Returns the XML representation of this object.
     *
     * @return the XML representation of this object
     */
    public String toXMLString () throws IOException {
        XMLStringWriter xmlStringWriter = new XMLStringWriter();
        toXML(xmlStringWriter, 0, false);
        return xmlStringWriter.toString();
    }

    /**
     * Prints the XML representation of the <ttt>CycList</tt> to an <tt>XMLWriter</tt>
     *
     * @param xmlWriter the output XML serialization writer
     * @param indent specifies by how many spaces the XML output should be indented
     * @param relative specifies whether the indentation should be absolute --
     * indentation with respect to the beginning of a new line, relative = false
     * -- or relative to the indentation currently specified in the indent_string field
     * of the xml_writer object, relative = true.
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        int startingIndent = xmlWriter.getIndentLength();
        xmlWriter.printXMLStartTag(cycListXMLTag, indent, relative, true);
        Iterator iterator = this.iterator();
        Object arg;
        while (iterator.hasNext()) {
            arg = iterator.next();
            toXML(arg, xmlWriter, indentLength, true);
        }
        if (! isProperList) {
            xmlWriter.printXMLStartTag(dottedElementXMLTag, indentLength, relative, true);
            toXML(dottedElement, xmlWriter, indentLength, true);
            xmlWriter.printXMLEndTag(dottedElementXMLTag, 0, true);
            xmlWriter.setIndent(-indentLength, true);
        }
        xmlWriter.printXMLEndTag(cycListXMLTag, 0, true);
        /*
        if (startingIndent != xmlWriter.getIndentLength())
            throw new RuntimeException("Starting indent " + startingIndent +
                                       " is not equal to ending indent " + xmlWriter.getIndentLength());
                                       */
    }

    /**
     * Writes a CycList element the the given XML output stream.
     *
     * @param object the object to be serialized as XML
     * @param xmlWriter the output XML serialization writer
     * @param indent specifies by how many spaces the XML output should be indented
     * @param relative specifies whether the indentation should be absolute --
     * indentation with respect to the beginning of a new line, relative = false
     * -- or relative to the indentation currently specified in the indent_string field
     * of the xml_writer object, relative = true.
     */
    public static void toXML(Object object, XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        int startingIndent = xmlWriter.getIndentLength();
        if (object instanceof Integer) {
            xmlWriter.printXMLStartTag(integerXMLTag, indentLength, true, false);
            xmlWriter.print(object.toString());
            xmlWriter.printXMLEndTag(integerXMLTag);
        }
        else if (object instanceof String) {
            xmlWriter.printXMLStartTag(stringXMLTag, indentLength, true, false);
            xmlWriter.print(TextUtil.doEntityReference((String) object));
            xmlWriter.printXMLEndTag(stringXMLTag);
        }
        else if (object instanceof Double) {
            xmlWriter.printXMLStartTag(doubleXMLTag, indentLength, true, false);
            xmlWriter.print(object.toString());
            xmlWriter.printXMLEndTag(doubleXMLTag);
        }
        else if (object instanceof CycFort)
             ((CycFort) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof ByteArray)
             ((ByteArray) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof CycVariable)
             ((CycVariable) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof CycSymbol)
             ((CycSymbol) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof Guid)
             ((Guid) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof CycList)
             ((CycList) object).toXML(xmlWriter, indentLength, true);
        else if (object instanceof CycAssertion)
             ((CycAssertion) object).toXML(xmlWriter, indentLength, true);
        else
            throw new RuntimeException("Invalid CycList object " + object);
        xmlWriter.setIndent(-indentLength, true);
        if (startingIndent != xmlWriter.getIndentLength())
            throw new RuntimeException("Starting indent " + startingIndent +
                                       " is not equal to ending indent " + xmlWriter.getIndentLength() +
                                       " for object " + object);
    }

    /**
     * Gets the value following the given keyword symbol.
     *
     * @param keyword the keyword symbol
     * @return the value following the given keyword symbol, or null if not found
     */
    public Object getValueForKeyword (CycSymbol keyword) {
        for (int i = 0; i < this.size() - 1; i++) {
            if (this.get(i).equals(keyword))
                return this.get(i + 1);
        }
        return null;
    }

    /**
     * Forms a quote expression for the given object and adds it to the list.
     *
     * @param object the object to be quoted and added to this list
     */
    public void addQuoted (Object object) {
        CycList cycList = new CycList();
        this.add(cycList);
        cycList.add(CycObjectFactory.quote);
        cycList.add(object);
    }
}
