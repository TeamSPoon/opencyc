package org.opencyc.cycobject;

import java.util.*;
import java.io.*;
import org.opencyc.util.*;
import org.opencyc.api.*;

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
     * Constructs a CycList from the given xml databinding object.
     *
     * @pararm cycListXmlDataBinding the xml databinding object
     */
    public CycList (CycListXmlDataBinding cycListXmlDataBinding) {
        for (int i = 0; i < cycListXmlDataBinding.getElementList(); i++) {
            Object element = cycListXmlDataBinding.elementList.get(i);
            if (element instanceof CycConstantXmlDataBinding)
                this.add(new CycConstant((CycConstantXmlDataBinding) element));
            else if (element instanceof CycNartXmlDataBinding)
                this.add(new CycNart((CycNartXmlDataBinding) element));
            else if (element instanceof CycSymbolXmlDataBinding)
                this.add(new CycSymbol((CycSymbolXmlDataBinding) element));
            else if (element instanceof CycVariableXmlDataBinding)
                this.add(new CycVariable((CycVariableXmlDataBinding) element));
            else if (element instanceof GuidXmlDataBinding)
                this.add(new CycVariable((GuidXmlDataBinding) element));
            else
                this.add(element);
        }
        this.addAll(cycListXmlDataBinding.elementList);
        this.isProperList = cycListXmlDataBinding.getIsProperListIndicator();
        if (element instanceof CycConstantXmlDataBinding)
            this.dottedElement = new CycConstant((CycConstantXmlDataBinding) element);
        else if (element instanceof CycNartXmlDataBinding)
            this.dottedElement = new CycNart((CycNartXmlDataBinding) element);
        else if (element instanceof CycSymbolXmlDataBinding)
            this.dottedElement = new CycSymbol((CycSymbolXmlDataBinding) element);
        else if (element instanceof CycVariableXmlDataBinding)
            this.dottedElement = new CycVariable((CycVariableXmlDataBinding) element);
        else if (element instanceof GuidXmlDataBinding)
            this.dottedElement = new CycVariable((GuidXmlDataBinding) element);
        else
            this.dottedElement = element;
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
     * Returns the CycList after removing the first element.
     *
     * @return new <tt>CycList</tt> with first element removed.
     */
    public CycList rest() {
        if (this.size() == 0)
            throw new RuntimeException("Cannot remove first element of an empty list.");
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
        CycList newSelectedItems = selectedItems.rest();
        newSelectedItems.add(availableItems.first());
        CycList newAvailableItems = availableItems.rest();
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
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return cyclify();
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
     * Returns the CycListXmlDataBinding object which contains this CycList.  The
     * xml databinding object can be subsequently serialized into xml.
     *
     * @return the CycListXmlDataBinding object which contains this CycList
     */
    public CycListXmlDataBinding toCycListXmlDataBinding () {
        CycListXmlDataBinding cycListXmlDataBinding = new CycListXmlDataBinding();
        ArrayList elementList = new ArrayList();
        for (int i = 0; i < this.size(); i++) {
            Object element = this.get(i);
            if (element instanceof CycConstant)

        }
        cycListXmlDataBinding.setElementList(new ArrayList(this));
        cycListXmlDataBinding.setIsProperListIndicator(this.isProperList);
        cycListXmlDataBinding.setDottedElement(this.dottedElement);
        return cycListXmlDataBinding;
    }
}
