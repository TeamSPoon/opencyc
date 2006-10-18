package  org.opencyc.cycobject;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.util.StringUtils;
import org.opencyc.xml.TextUtil;
import org.opencyc.xml.XMLStringWriter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import org.opencyc.xml.XMLWriter;


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
public class CycList extends ArrayList implements CycObject, List, Serializable {

  static final long serialVersionUID = 2031704553206469327L;
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
   * Constructs a new empty <tt>CycList</tt> object of the given size.
   *
   * @param size the initial size of the list
   */
  public CycList(final int size) {
    super(size);
  }
  
  /**
   * Constructs a new <tt>CycList</tt> object, containing the elements of the
   * specified collection, in the order they are returned by the collection's iterator.
   *
   * @param c the collection of assumed valid OpenCyc objects.
   */
  public CycList(final Collection c) {
    super(c);
    if (c instanceof CycList) {
      if (!((CycList)c).isProperList()) {
        if (this.size() > 0) {
          super.remove(this.size() - 1);
        }
        setDottedElement(((CycList)c).getDottedElement());
      }
    }
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
  public CycList(final Object firstElement, final Collection remainingElements) {
    this.add(firstElement);
    addAll(remainingElements);
  }
  
  /**
   * Constructs a new <tt>CycList</tt> object, containing as its sole element
   * <tt>element</tt>
   *
   * @param element the object which becomes the head of the <tt>CycList</tt>
   */
  public CycList(final Object element) {
    this.add(element);
  }
  
  /**
   * Constructs a new <tt>CycList</tt> object, containing as its first element
   * <tt>element1</tt>, and <tt>element2</tt> as its second element.
   *
   * @param element1 the object which becomes the head of the <tt>CycList</tt>
   * @param element2 the object which becomes the second element of the <tt>CycList</tt>
   */
  public CycList(final Object element1, final Object element2) {
    this.add(element1);
    this.add(element2);
  }
  
  /** Returns a new proper CycList having the given element as it's initial element.
   *
   * @param obj the initial element
   * @return a new proper CycList having the given element as it's initial element
   */
  public static CycList makeCycList(final Object obj) {
    
    //// Preconditions 
    if (obj == null)
      throw new NullPointerException("obj cannot be null");
    
    final CycList cycList = new CycList();
    cycList.add(obj);
    return cycList;
  }
  
  /** Returns a new proper CycList having the given two elements as it's initial elements.
   *
   * @param obj1 the first element
   * @param obj2 the second element
   * @return a new proper CycList having the given two elements as it's initial elements
   */
  public static CycList makeCycList(final Object obj1, final Object obj2) {
    
    //// Preconditions 
    if (obj1 == null)
      throw new NullPointerException("obj1 cannot be null");
    if (obj2 == null)
      throw new NullPointerException("obj2 cannot be null");
    
    final CycList cycList = new CycList();
    cycList.add(obj1);
    cycList.add(obj2);
    return cycList;
  }
  
  /** Returns a new proper CycList having the given three elements as it's initial elements.
   *
   * @param obj1 the first element
   * @param obj2 the second element
   * @param obj3 the third element
   * @return a new proper CycList having the given three elements as it's initial elements
   */
  public static CycList makeCycList(final Object obj1, final Object obj2, final Object obj3) {
    
    //// Preconditions 
    if (obj1 == null)
      throw new NullPointerException("obj1 cannot be null");
    if (obj2 == null)
      throw new NullPointerException("obj2 cannot be null");
    if (obj3 == null)
      throw new NullPointerException("obj3 cannot be null");
    
    final CycList cycList = new CycList();
    cycList.add(obj1);
    cycList.add(obj2);
    cycList.add(obj3);
    return cycList;
  }
  
  /** Returns a new proper CycList having the given four elements as it's initial elements.
   *
   * @param obj1 the first element
   * @param obj2 the second element
   * @param obj3 the third element
   * @param obj4 the fourth element
   * @return a new proper CycList having the given four elements as it's initial elements
   */
  public static CycList makeCycList(final Object obj1, final Object obj2, final Object obj3, final Object obj4) {
    
    //// Preconditions 
    if (obj1 == null)
      throw new NullPointerException("obj1 cannot be null");
    if (obj2 == null)
      throw new NullPointerException("obj2 cannot be null");
    if (obj3 == null)
      throw new NullPointerException("obj3 cannot be null");
    if (obj4 == null)
      throw new NullPointerException("obj4 cannot be null");
    
    final CycList cycList = new CycList();
    cycList.add(obj1);
    cycList.add(obj2);
    cycList.add(obj3);
    cycList.add(obj4);
    return cycList;
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
  public static CycList construct(final Object object1, final Object object2) {
    final CycList cycList = new CycList(object1);
    if (object2.equals(CycObjectFactory.nil))
      return  cycList;
    if (object2 instanceof CycList) {
      final CycList cycList2 = (CycList)object2;
      cycList.addAll(cycList2);
      if (!cycList2.isProperList)
        cycList.setDottedElement(cycList2.getDottedElement());
      return  cycList;
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
    return new CycList(this);
  }
  
  /**
   * Creates and returns a deep copy of this <tt>CycList</tt>.  In a deep copy,
   * directly embedded <tt>CycList</tt> objects are also deep copied.  Objects
   * which are not CycLists are cloned.
   *
   * @return a deep copy of this <tt>CycList</tt>
   */
  public CycList deepCopy() {
    final CycList cycList = new CycList();
    if (!this.isProperList()) {
      if (this.dottedElement instanceof CycList)
        cycList.setDottedElement(((CycList)this.dottedElement).deepCopy());
      else
        cycList.setDottedElement(this.getDottedElement());
    }
    for (int i = 0; i < super.size(); i++) {
      final Object element = this.get(i);
      if (element instanceof CycList)
        cycList.add(((CycList)element).deepCopy());
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
  public void setDottedElement(final Object dottedElement) {
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
  
  /** Returns the CycList size including the optional dotted element.  Note that this fools list iterators. 
   *
   * @return the CycList size including the optional dotted element
   */
  public int size() {
    int result = super.size();
    if (!isProperList()) { result++; }
    return result;
  }

  public int getProperListSize() {
    return super.size();
  }
  
  /**
   * Answers true iff the CycList contains valid elements.  This is a necessary, but
   * not sufficient condition for CycL well-formedness.
   */
  public boolean isValid() {
    for (int i = 0; i < this.size(); i++) {
      final Object object = this.get(i);
      if (object instanceof String ||
      object instanceof Integer ||
      object instanceof Guid ||
      object instanceof Float ||
      object instanceof ByteArray ||
      object instanceof CycConstant ||
      object instanceof CycNart)
        continue;
      else if (object instanceof CycList) {
        if (!((CycList)object).isValid())
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
   * @deprecated use CycAccess.isFormulaWellFormed(this, mt);
   */
  public boolean isFormulaWellFormed(final ELMt mt)
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
   * @deprecated use CycAccess.isCycLNonAtomicReifableTerm();
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
   * @deprecated use CycAccess.isCycLNonAtomicUnreifableTerm();
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
  public static CycList list(final Object element) {
    final CycList result = new CycList();
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
  public static CycList list(final Object element1, final Object element2) {
    final CycList result = new CycList();
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
  public static CycList list(final Object element1, final Object element2, final Object element3) {
    final CycList result = new CycList();
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
    if(size() == 0)
      throw new RuntimeException("First element not available for an empty CycList");
    return this.get(0);
  }
  
  /**
   * Returns the second element of the <tt>CycList</tt>.
   *
   * @return the <tt>Object</tt> which is the second element of the list.
   */
  public Object second() {
    if(size() < 1)
      throw new RuntimeException("Second element not available");
    return this.get(1);
  }
  
  /**
   * Returns the third element of the <tt>CycList</tt>.
   *
   * @return the <tt>Object</tt> which is the third element of the list.
   */
  public Object third() {
    if(size() < 2)
      throw new RuntimeException("Third element not available");
    return this.get(2);
  }
  
  /**
   * Returns the fourth element of the <tt>CycList</tt>.
   *
   * @return the <tt>Object</tt> which is the fourth element of the list.
   */
  public Object fourth() {
    if(size() < 3)
      throw new RuntimeException("Fourth element not available");
    return this.get(3);
  }
  
  /**
   * Returns the last element of the <tt>CycList</tt>.
   *
   * @return the <tt>Object</tt> which is the last element of the list.
   */
  public Object last() {
    if(size() == 0)
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
    if(this.size() == 0)
      throw new RuntimeException("Cannot remove first element of an empty list.");
    else if((super.size() == 1) && (!this.isProperList))
      return this.getDottedElement();
    final CycList cycList = new CycList(this);
    cycList.remove(0);
    return cycList;
  }
  
  /**
   * Appends the given elements to the end of the list and returns the list (useful when nesting method calls).
   *
   * @param cycList the elements to add
   * @return the list after adding the given elements to the end
   */
  public CycList appendElements(final CycList cycList) {
      addAll(cycList);
      return this;
  }
  
  /**
   * Appends the given element to the end of the list and returns the list (useful when nesting method calls).
   *
   * @param object the object element to add
   * @return the list after adding the given element to the end
   */
  public CycList appendElement(final Object object) {
      add(object);
      return this;
  }
  
  /**
   * Appends the given element to the end of the list and returns the list (useful when nesting method calls).
   *
   * @param i the integer element to add
   * @return the list after adding the given element to the end
   */
  public CycList appendElement(final int i) {
      add(new Integer(i));
      return this;
  }
  
  /**
   * Appends the given element to the end of the list and returns the list (useful when nesting method calls).
   *
   * @param l the long element to add
   * @return the list after adding the given element to the end
   */
  public CycList appendElement(final long l) {
      add(new Long(l));
      return this;
  }
  
  /**
   * Appends the given element to the end of the list and returns the list (useful when nesting method calls).
   *
   * @param b the boolean element to add
   * @return the list after adding the given element to the end
   */
  public CycList appendElement(final boolean b) {
      add(new Boolean(b));
      return this;
  }
  
  /**
   * Adds the given integer to this list by wrapping it with an Integer object.
   *
   * @param i the given integer to add
   */
  public void add(final int i) {
    this.add(new Integer(i));
  }
  
  /**
   * Adds the given long to this list by wrapping it with an Long object.
   *
   * @param l the given long to add
   */
  public void add(final long l) {
    this.add(new Long(l));
  }
  
  /**
   * Adds the given float to this list by wrapping it with a Float object.
   *
   * @param f the given float to add
   */
  public void add(final float f) {
    this.add(new Float(f));
  }
  
  /**
   * Adds the given double to this list by wrapping it with a Double object.
   *
   * @param d the given double to add
   */
  public void add(final double d) {
    this.add(new Double(d));
  }
  
  /**
   * Adds the given boolean to this list by wrapping it with a Boolean object.
   *
   * @param b the given boolean to add
   */
  public void add(final boolean b) {
    this.add(new Boolean(b));
  }
  
  /**
   * Adds the given element to this list if it is not already contained.
   */
  public void addNew(final Object object) {
    if(!this.contains(object))
      this.add(object);
  }
  
  /**
   * Adds the given elements to this list if they are not already contained.
   */
  public void addAllNew(final Collection objects) {
    final Iterator iter = objects.iterator();
    while (true) {
      if(!iter.hasNext())
        break;
      this.addNew(iter.next());
    }
  }
  
  public boolean addAll(Collection col) {
    boolean result = super.addAll(col);
    if (col instanceof CycList) {
      CycList cycList = (CycList)col;
      if (!cycList.isProperList()) { 
        if (isProperList()) {
          setDottedElement(cycList.getDottedElement());
        } else {
          add(getDottedElement());
          setDottedElement(cycList.getDottedElement());
        }
      }
    }
    return result;
  }
  
  /**
   * Returns true iff this list contains duplicate elements.
   *
   * @return true iff this list contains duplicate elements
   */
  public boolean containsDuplicates() {
    if(!isProperList)
      if(this.contains(this.dottedElement))
        return  true;
    for(int i = 0; i < this.size(); i++)
      for(int j = i + 1; j < this.size(); j++)
        if(this.get(i).equals(this.get(j)))
          return true;
    return false;
  }
  
  /**
   * Destructively delete duplicates from the list.
   * @return <code>this</code> list with the duplicates deleted.
   */
  public CycList deleteDuplicates() {
    if(this.isProperList)
      if(this.contains(this.dottedElement))
        this.setDottedElement(null);
    for(int i = 0; i< this.size(); i++)
      for(int j = i+1; j<this.size(); j++) {
        if(this.get(i).equals(this.get(j))) {
          this.remove(j);
          j--;
        }
      }
    return this;
  }
  
  /**
   * Remove duplicates from the list.  Just like #deleteDuplicates but
   * non-destructive.
   * @return A new list with the duplicates removed.
   */
  public CycList removeDuplicates() {
    final CycList result = this.deepCopy();
    return result.deleteDuplicates();
  }
  
  /**
   * Flatten the list. Recursively iterate through tree, and return a list of
   * the atoms found.
   * @return List of atoms in <code>this</code> CycList.
   */
  public CycList flatten() {
    final CycList result = new CycList();
    final Iterator i = this.iterator();
    while(i.hasNext()) {
      Object obj = i.next();
      if(obj instanceof CycList)
        result.addAll(((CycList)obj).flatten());
      else
        result.add(obj);
    } //end while
    if(!isProperList)
      result.add(getDottedElement());
    return result;
  }
  
  /**
   * Returns a new <tt>CycList</tt> whose elements are the reverse of
   * this <tt>CycList</tt>, which is unaffected.
   *
   * @return new <tt>CycList</tt> with elements reversed.
   */
  public CycList reverse() {
    if (!isProperList)
      throw new RuntimeException(this + " is not a proper list and cannot be reversed");
    final CycList result = new CycList();
    for(int i = (this.size() - 1); i >= 0; i--)
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
    if(!isProperList)
      throw new RuntimeException(this + " is not a proper list");
    final CycList result = new CycList();
    if(this.size() == 0 || n == 0)
      return result;
    return combinationsOfInternal(new CycList(this.subList(0, n)), new CycList(this.subList(n, this.size())));
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
  private static CycList combinationsOfInternal(final CycList selectedItems, final CycList availableItems) {
    final CycList result = CycList.list(selectedItems);
    if(availableItems.size() == 0)
      return result;
    CycList combination = null;
    for(int i = 0; i < (selectedItems.size() - 1); i++)
      for(int j = 0; j < availableItems.size(); j++) {
        final Object availableItem = availableItems.get(j);
        // Remove it (making copy), shift left, append replacement.
        combination = (CycList)selectedItems.clone();
        combination.remove(i + 1);
        combination.add(availableItem);
        result.add(combination);
      }
    final CycList newSelectedItems = (CycList)selectedItems.rest();
    newSelectedItems.add(availableItems.first());
    final CycList newAvailableItems = (CycList)availableItems.rest();
    result.addAll(combinationsOfInternal(newSelectedItems, newAvailableItems));
    return result;
  }
  
  /**
   * Returns a random ordering of the <tt>CycList</tt> without recursion.
   *
   * @return a random ordering of the <tt>CycList</tt> without recursion
   */
  public CycList randomPermutation() {
    final Random random = new Random();
    int randomIndex = 0;
    final CycList remainingList = (CycList)this.clone();
    final CycList permutedList = new CycList();
    if (this.size() == 0)
      return remainingList;
    while(true) {
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
  public CycList subst(final Object newObject, final Object oldObject) {
    final CycList result = new CycList();
    if(!isProperList)
      result.setDottedElement((dottedElement.equals(oldObject)) ? oldObject : newObject);
    for(int i = 0; i < getProperListSize(); i++) {
      final Object element = this.get(i);
      if(element.equals(oldObject))
        result.add(newObject);
      else if(element instanceof CycList)
        result.add(((CycList)element).subst(newObject, oldObject));
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
   * <tt>CycList</tt>.  When the parameter is true, the representation is created without causing
   * additional api calls to complete the name field of constants.
   *
   * @param safe when true, the representation is created without causing
   * additional api calls to complete the name field of constants
   * @return a <tt>String</tt> representation of this <tt>CycList</tt>
   */
  protected String toStringHelper(final boolean safe) {
    final StringBuffer result = new StringBuffer("(");
    for(int i = 0; i < super.size(); i++) {
      if(i > 0)
        result.append(" ");
      final Object element = this.get(i);
      if(element == null)
        result.append("null");
      else if(element instanceof String)
        result.append("\"" + element + "\"");
      else if(safe) {
        try {
          // If element understands the safeToString method, then use it.
          final Method safeToString = element.getClass().getMethod("safeToString", null);
          result.append(safeToString.invoke(element, null));
        } catch (Exception e) {
          result.append(element.toString());
        }
      }
      else
        result.append(element.toString());
    }
    if (!isProperList) {
      result.append(" . ");
      if(dottedElement instanceof String) {
        result.append("\"");
        result.append(dottedElement);
        result.append("\"");
      }
      else if(safe) {
        try {
          // If dottedElement understands the safeToString method, then use it.
          final Method safeToString = dottedElement.getClass().getMethod("safeToString", null);
          result.append(safeToString.invoke(dottedElement, null));
        } catch (Exception e) {
          result.append(dottedElement.toString());
        }
      }
      else
        result.append(dottedElement.toString());
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
    return  toPrettyStringInt(indent, "  ", "\n", false, false);
  }
  
  /**
   * Returns a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt> with embedded strings escaped.
   * @param indent, the indent string that is added before the
   * <tt>String</tt> representation this <tt>CycList</tt>
   * @return a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   */
  public String toPrettyEscapedCyclifiedString(String indent) {
    return  toPrettyStringInt(indent, "  ", "\n", true, true);
  }
  
  /**
   * Returns a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   * @param indent, the indent string that is added before the
   * <tt>String</tt> representation this <tt>CycList</tt>
   * @return a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   */
  public String toPrettyCyclifiedString(String indent) {
    return  toPrettyStringInt(indent, "  ", "\n", true, false);
  }
  
  /**
   * Returns an HTML `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   * @param indent, the indent string that is added before the
   * <tt>String</tt> representation this <tt>CycList</tt>
   * @return an HTML `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   */

  public String toHTMLPrettyString (final String indent) {
    return "<html><body>" + toPrettyStringInt(indent, "&nbsp&nbsp", "<br>", false, false) + "</body></html>";
  }
  
  /**
   * Returns a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   * @param indent, the indent string that is added before the
   * <tt>String</tt> representation this <tt>CycList</tt>
   * @param incrementIndent, the indent string that to the <tt>String</tt> 
   * representation this <tt>CycList</tt>is added at each level
   * of indenting
   * @param newLineString, the string added to indicate a new line
   * @param shouldCyclify indicates that the output constants should have #$ prefix
   * @param shouldEscape indicates that embedded strings should have appropriate escapes for the SubL reader
   * @return a `pretty-printed' <tt>String</tt> representation of this
   * <tt>CycList</tt>.
   */
  public String toPrettyStringInt(final String indent, 
      final String incrementIndent, final String newLineString,
      final boolean shouldCyclify, final boolean shouldEscape) {
    final StringBuffer result = new StringBuffer(indent + "(");
    for(int i = 0; i < super.size(); i++) {
      Object element = this.get(i);
      if (element instanceof CycNart) {
        CycList newElem = new CycList();
        newElem.add(((CycNart)element).getFunctor());
        newElem.addAll(((CycNart)element).getArguments());
        element = newElem;
      }
      if(element instanceof String) {
        if(i > 0) { result.append(" "); }
        result.append('"');
        if (shouldEscape)
          result.append(StringUtils.escapeDoubleQuotes((String) element));
        else
          result.append(element);
        result.append('"');
      } else if (element instanceof CycList) {
        result.append(newLineString + ((CycList)element).
          toPrettyStringInt(indent + incrementIndent, incrementIndent, 
          newLineString, shouldCyclify, shouldEscape));
      } else {
        if(i > 0) { result.append(" "); }
        if (shouldCyclify) {
          if (shouldEscape)
            result.append(DefaultCycObject.cyclify(element));
          else
            result.append(DefaultCycObject.cyclifyWithEscapeChars(element));
        } else {
          result.append(element.toString());
        }
      }
    }
    if(!isProperList) {
      result.append(" . ");
      if(dottedElement instanceof String) {
        result.append("\"");
        if (shouldEscape)
          result.append(StringUtils.escapeDoubleQuotes((String) dottedElement));
        else
          result.append(dottedElement);
        result.append("\"");
      } else {
        result.append(this.dottedElement.toString());
      }
    }
    result.append(")");
    return result.toString();
  }
  
  public boolean equals(Object o) {
    if (o == this) { return true; }
    if (o == null) { return false; }
    if (!(o instanceof List)) { return false; }
    if (!isProperList()) {
      if (!(o instanceof CycList)) { return false; }
      if (((CycList)o).isProperList()) { return false; }
    } else {
      if (o instanceof CycList) {
        if (!((CycList)o).isProperList()) { return false; }
      }
    }
    java.util.ListIterator e1 = listIterator();
    java.util.ListIterator e2 = ((List)o).listIterator();
    while(e1.hasNext() && e2.hasNext()) {
      Object o1 = e1.next();
      Object o2 = e2.next();
      if (o1 instanceof CycList) {
        if (!((CycList)o1).isProperList()) {
          if (!(o2 instanceof CycList)) { return false; }
          if (((CycList)o2).isProperList()) { return false; }
        } else {
          if (o2 instanceof CycList) {
            if (!((CycList)o2).isProperList()) { return false; }
          }
        }
      } 
      if (!(o1==null ? o2==null : o1.equals(o2))) { return false; }
    }
    return !(e1.hasNext() || e2.hasNext());
  }
  
  /** Returns true if the given object is equal to this object as EL CycL expressions
   *
   * @param o the given object
   * @return true if the given object is equal to this object as EL CycL expressions, otherwise
   * return false
   */
  public boolean equalsAtEL(Object o) {
    if (o == this) { return true; }
    if (o == null) { return false; }
    if (o instanceof CycNart) {
      o = ((CycNart)o).toCycList();
    }
    if (!(o instanceof List)) { return false; }
    if (!isProperList()) {
      if (!(o instanceof CycList)) { return false; }
      if (((CycList)o).isProperList()) { return false; }
    } else {
      if (o instanceof CycList) {
        if (!((CycList)o).isProperList()) { return false; }
      }
    }
    java.util.ListIterator e1 = listIterator();
    java.util.ListIterator e2 = ((List)o).listIterator();
    while(e1.hasNext() && e2.hasNext()) {
      Object o1 = e1.next();
      if ((o1 != null) && (o1 instanceof CycNart)) { o1 = ((CycNart)o1).toCycList(); }
      Object o2 = e2.next();
      if ((o2 != null) && (o2 instanceof CycNart)) { o2 = ((CycNart)o2).toCycList(); }
      if (o1 instanceof CycList) {
        if (!((CycList)o1).isProperList()) {
          if (!(o2 instanceof CycList)) { return false; }
          if (((CycList)o2).isProperList()) { return false; }
        } else {
          if (o2 instanceof CycList) {
            if (!((CycList)o2).isProperList()) { return false; }
          }
        }
        if (!(o1==null ? o2==null : ((CycList)o1).equalsAtEL(o2))) { return false; }
      } else if ((o1 instanceof Integer && o2 instanceof Long) || (o1 instanceof Long && o2 instanceof Integer)) {
        return ((Number) o1).longValue() == ((Number) o2).longValue();
      } else if ((o1 instanceof Float && o2 instanceof Double) || (o1 instanceof Double && o2 instanceof Float)) {
        return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
      } else {
        if (!(o1==null ? o2==null : o1.equals(o2))) { return false; }
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }
  

 public int compareTo( Object o) {
    if (o == this) { return 0; }
    if (o == null) { return 1; }
    if (!(o instanceof List)) { return 1; }
    if (!isProperList()) {
      if (!(o instanceof CycList)) { return 1; }
      if (((CycList)o).isProperList()) { return 1; }
    } else {
      if (o instanceof CycList) {
        if (!((CycList)o).isProperList()) { return -1; }
      }
    }
    java.util.ListIterator e1 = listIterator();
    java.util.ListIterator e2 = ((List)o).listIterator();
    while(e1.hasNext() && e2.hasNext()) {
      Object o1 = e1.next();
      Object o2 = e2.next();

      if (o1==o2) continue;
      if (o1==null) return -1;
      if (o2==null) return 1;

 
      if(!(o1 instanceof Comparable)) return 1;
      if(!(o2 instanceof Comparable)) return -1;

      Comparable co1 = (Comparable) o1;
      Comparable co2 = (Comparable) o2;


      if (co1 instanceof CycList) {
        if (!((CycList)co1).isProperList()) {
          if (!(co2 instanceof CycList)) { return 1; }
          if (((CycList)co2).isProperList()) { return 1; }
        } else {
          if (co2 instanceof CycList) {
            if (!((CycList)co2).isProperList()) { return -1; }
          }
        }
      } 
      
      int ret=co1.compareTo(co2);
      if(ret!=0) return ret;
    }
    if (e1.hasNext()) return 1;
    if (e2.hasNext()) return -1;
    return 0;
 }


  /**
   * Returns a cyclified string representation of the OpenCyc <tt>CycList</tt>.
   * Embedded constants are prefixed with ""#$".  Embedded quote and backslash
   * chars in strings are escaped.
   *
   * @return a <tt>String</tt> representation in cyclified form.
   *
   */
  public String cyclifyWithEscapeChars() {
    final StringBuffer result = new StringBuffer("(");
    String cyclifiedObject = null;
    for(int i = 0; i < super.size(); i++) {
      final Object object = this.get(i);
      cyclifiedObject = DefaultCycObject.cyclifyWithEscapeChars(object);
      if (i > 0)
        result.append(" ");
      result.append(cyclifiedObject);
    }
    if(!isProperList) {
      result.append(" . ");
      result.append(DefaultCycObject.cyclifyWithEscapeChars(dottedElement));
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
    final StringBuffer result = new StringBuffer("(");
    String cyclifiedObject = null;
    for(int i = 0; i < super.size(); i++) {
      Object object = this.get(i);
      if(object == null)
        throw new RuntimeException("Invalid null element after " + result);      
      if (i > 0)
        result.append(" ");
      result.append(DefaultCycObject.cyclify(object));
    }
    if(!isProperList) {
      result.append(" . ");
      result.append(DefaultCycObject.cyclify(dottedElement));
    }
    result.append(")");
    return result.toString();
  }
  
  public HashMap getPrettyStringDetails() {
    HashMap map = new HashMap();
    getPrettyStringDetails(this, "", 0, new CycList(), map);
    int[] loc = { 0, toPrettyString("").length() };
    map.put(new CycList(), loc);
    return map;
  }
  
  private static int getPrettyStringDetails(final CycList list, final String indent, int currentPos, final CycList argPos, final HashMap map) {
    // System.out.println( "list=" + list + " // indent= " + indent + " // currentPos= " + currentPos + "// argPos= " + argPos + "// map= " + map);
    String str;
    CycList newArgPos;
    String tab = "  ";
    str = indent + "(";
    currentPos += str.length();
    String cyclifiedObject = null;
    int tempPos;
    for (int i = 0, size = list.size(); i < size; i++) {
      if (i > 0) {
        str = " ";
        currentPos += str.length();
      }
      if ((!list.isProperList()) && ((i + 1) >= size)) {
        currentPos += 2;
      }
      Object element = list.get(i);
      if (element instanceof CycNart) {
        element = ((CycNart)element).toCycList();
      }
      if (element instanceof String) {
        str = "\"" + element + "\"";
        newArgPos = argPos.deepCopy();
        newArgPos.add(new Integer(i));
        int[] loc = {currentPos, currentPos + str.length()};
        // System.out.println( "-- adding " + newArgPos + " referencing [" + loc[0] + "," + loc[1] + "] to the map " + map);
        map.put(newArgPos, loc);
        currentPos += str.length();
      } else if (element instanceof CycList) {
        argPos.add(new Integer(i));
        tempPos = currentPos + indent.length() + tab.length();
        currentPos = getPrettyStringDetails((CycList)element,
        indent + tab, currentPos, argPos, map);
        int[] loc = {tempPos, currentPos};
        CycList deepCopy = argPos.deepCopy();
        map.put(deepCopy, loc);
        // System.out.println( "-- adding " + deepCopy + " referencing [" + loc[0] + "," + loc[1] + "] to the map " + map);
        argPos.remove(argPos.size() - 1);
      } else {
        str = element.toString();
        newArgPos = argPos.deepCopy();
        newArgPos.add(new Integer(i));
        int[] loc = {currentPos, currentPos + str.length()};
        // System.out.println( "-- adding " + newArgPos + " referencing [" + loc[0] + "," + loc[1] + "] to the map " + map);
        map.put(newArgPos, loc);
        currentPos += str.length();
      }
    }
    str = ")";
    return currentPos + str.length();
  }

  /**
   * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
   *
   * @return this object in a form suitable for use as an <tt>String</tt> api expression value
   */
  public String stringApiValue() {
    final StringBuffer result = new StringBuffer(300);
    if (isProperList()) {
      result.append("(list");
      final String cyclifiedObject = null;
      for(int i = 0; i < this.size(); i++) {
        final Object object = this.get(i);
        result.append(" ");
        result.append(DefaultCycObject.stringApiValue(object));
      }
    } else {
      result.append("(list*"); // note the asterisk, which results in a dotted list
      final String cyclifiedObject = null;
      for(int i = 0; i < super.size(); i++) {
        final Object object = this.get(i);
        result.append(" ");
        result.append(DefaultCycObject.stringApiValue(object));
      }
      result.append(" ");
      if(dottedElement == null)
        result.append(new CycSymbol("NIL").stringApiValue());
      else
        result.append(DefaultCycObject.stringApiValue(dottedElement));
    }
    result.append(")");
    return result.toString();
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
    final CycList sortedList = new CycList(this);
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
   * Returns the list of constants found in the tree
   *
   * @param object the object to be found in the tree.
   * @return the list of constants found in the tree
   */
  public CycList treeConstants() {
    final CycList constants = new CycList();
    final Stack stack = new Stack();
    stack.push(this);
    while(!stack.empty()) {
      final Object obj = stack.pop();
      if (obj instanceof CycConstant) {
        constants.add(obj);
      } else if (obj instanceof CycAssertion) {
        stack.push(((CycAssertion)obj).getMt());
        pushTreeConstantElements(((CycAssertion)obj).getFormula(), stack);
      } else if (obj instanceof CycNart) {
        stack.push(((CycNart)obj).getFunctor());
        pushTreeConstantElements(((CycNart)obj).getArguments(), stack);
      } else if(obj instanceof CycList) {
        pushTreeConstantElements(((CycList)obj), stack);
      }
    }
    return constants;
  }
  
  private void pushTreeConstantElements(List list, Stack stack) {
    final Iterator iter = list.iterator();
    while (iter.hasNext()) {
      stack.push(iter.next());
    }
  }
  
  public Object get(int index) {
    if ((index == (size() - 1)) && (!isProperList())) {
      return getDottedElement();
    } else {
      return super.get(index);
    }
  }
  
 /**
  * Replaces the element at the specified position in this list with
  * the specified element.
  *
  * @param index index of element to replace.
  * @param element element to be stored at the specified position.
  * @return the element previously at the specified position.
  * @throws    IndexOutOfBoundsException if index out of range
  *		  <tt>(index &lt; 0 || index &gt;= size())</tt>.
  */
  public Object set(int index, Object element) {
    if ((index == (size() - 1)) && (!isProperList())) {
      final Object oldValue = getDottedElement();
      setDottedElement(element);
      return oldValue;
    } 
    else
      return super.set(index, element);
  }
  
  /**
   * This behaves like the SubL function GETF
   */
  public Object getf(CycSymbol indicator) {
    int indicatorIndex = firstEvenIndexOf(indicator);
    if (indicatorIndex == -1) { // the indicator is not present
      return null;
    }
    else {
      return get(indicatorIndex + 1);
    }
  }
  
  private int firstEvenIndexOf(Object elem) {
    if (elem == null) {
      for (int i = 0; i < size(); i = i + 2) {
        if (get(i) == null) {
          return i;
        }
      }
    }
    else {
	    for (int i = 0; i < size(); i = i + 2) {
        if (elem.equals(get(i))) {
  		    return i;
        }
      }
  	}
  	return -1;
  }
  
  /**
   * Returns a <tt>CycList</tt> of all the indices of the given element within this CycList.
   *
   * @param elem The element to search for in the list
   * @return a <tt>CycList</tt> of all the indices of the given element within this CycList.
   */
  public CycList allIndicesOf(Object elem) {
    CycList result = new CycList();
    if (elem == null) {
	    for (int i = 0; i < size(); i++) {
        if (get(i) == null) {
          result.add(i);
        }
      }
    }
    else {
	    for (int i = 0; i < size(); i++) {
        if (elem.equals(get(i))) {
          result.add(i);
        }
      }
  	}
    return result;
  }
  
  /**
   * Returns the list of objects of the specified type found in the tree.
   * 
   * @param cls What class to select from the tree
   * @return the list of objects of type <code>cls</code> found in the tree
   */
  public CycList treeGather(Class cls) {
    final CycList result = new CycList();
    final Stack stack = new Stack();
    stack.push(this);
    while(!stack.empty()) {
      final Object obj = stack.pop();
      if(cls.isInstance(obj))
        result.add(obj);
      else if(obj instanceof CycList) {
        CycList l = (CycList)obj;
        final Iterator iter = l.iterator();
        while(iter.hasNext()) 
          stack.push(iter.next());
        if(!l.isProperList)
          stack.push(l.getDottedElement());
      }
    }
    return result;
  }
  
  /**
   * Returns true if the proper list tree contains the given object anywhere in the tree.
   *
   * @param object the object to be found in the tree.
   * @return true if the proper list tree contains the given object anywhere in the tree
   */
  public boolean treeContains(Object object) {
    if (object instanceof CycNart) {
      object = ((CycNart)object).toCycList();
    }
    if(this.contains(object)) {
      return true;
    }
    for (int i = 0; i < this.size(); i++) {
      Object element = this.get(i);
      if (element instanceof CycNart) {
        element = ((CycNart)element).toCycList();
      }
      if (element.equals(object)) {
        return true;
      }
      if ((element instanceof CycList) && (((CycList)element).treeContains(object))) {
        return true;
      }
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
  public boolean doesElementPrecedeOthers(final Object element, final CycList otherElements) {
    for(int i = 0; i < this.size(); i++) {
      if(element.equals(this.get(i)))
        return true;
      if(otherElements.contains(this.get(i)))
        return false;
    }
    return false;
  }
  
  /**
   * Returns the XML representation of this object.
   *
   * @return the XML representation of this object
   */
  public String toXMLString() throws IOException {
    final XMLStringWriter xmlStringWriter = new XMLStringWriter();
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
  public void toXML(final XMLWriter xmlWriter, final int indent, final boolean relative) throws IOException {
    final int startingIndent = xmlWriter.getIndentLength();
    xmlWriter.printXMLStartTag(cycListXMLTag, indent, relative, true);
    final Iterator iterator = this.iterator();
    Object arg;
    while(iterator.hasNext()) {
      arg = iterator.next();
      if ((!iterator.hasNext()) && (!isProperList())) { break; }
      toXML(arg, xmlWriter, indentLength, true);
    }
    if(!isProperList) {
      xmlWriter.printXMLStartTag(dottedElementXMLTag, indentLength, relative,
      true);
      toXML(dottedElement, xmlWriter, indentLength, true);
      xmlWriter.printXMLEndTag(dottedElementXMLTag, 0, true);
      xmlWriter.setIndent(-indentLength, true);
    }
    xmlWriter.printXMLEndTag(cycListXMLTag, 0, true);        /*
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
  public static void toXML(final Object object, final XMLWriter xmlWriter, final int indent, final boolean relative) throws IOException {
    final int startingIndent = xmlWriter.getIndentLength();
    if(object instanceof Integer) {
      xmlWriter.printXMLStartTag(integerXMLTag, indentLength, true, false);
      xmlWriter.print(object.toString());
      xmlWriter.printXMLEndTag(integerXMLTag);
    }
    else if(object instanceof String) {
      xmlWriter.printXMLStartTag(stringXMLTag, indentLength, true, false);
      xmlWriter.print(TextUtil.doEntityReference((String)object));
      xmlWriter.printXMLEndTag(stringXMLTag);
    }
    else if(object instanceof Double) {
      xmlWriter.printXMLStartTag(doubleXMLTag, indentLength, true, false);
      xmlWriter.print(object.toString());
      xmlWriter.printXMLEndTag(doubleXMLTag);
    }
    else if(object instanceof CycFort)
      ((CycFort)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof ByteArray)
      ((ByteArray)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof CycVariable)
      ((CycVariable)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof CycSymbol)
      ((CycSymbol)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof Guid)
      ((Guid)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof CycList)
      ((CycList)object).toXML(xmlWriter, indentLength, true);
    else if(object instanceof CycAssertion)
      ((CycAssertion)object).toXML(xmlWriter, indentLength, true);
    else
      throw new RuntimeException("Invalid CycList object " + object);
    xmlWriter.setIndent(-indentLength, true);
    if(startingIndent != xmlWriter.getIndentLength())
      throw new RuntimeException("Starting indent " + startingIndent + " is not equal to ending indent "
      + xmlWriter.getIndentLength() + " for object " + object);
  }
  
  /**
   * Gets the value following the given keyword symbol.
   *
   * @param keyword the keyword symbol
   * @return the value following the given keyword symbol, or null if not found
   */
  public Object getValueForKeyword(final CycSymbol keyword) {
    for(int i = 0; i < this.size() - 1; i++) {
      if(this.get(i).equals(keyword))
        return this.get(i + 1);
    }
    return null;
  }
  
  /**
   * Forms a quote expression for the given object and adds it to the list.
   *
   * @param object the object to be quoted and added to this list
   */
  public void addQuoted(final Object object) {
    final CycList cycList = new CycList();
    this.add(cycList);
    cycList.add(CycObjectFactory.quote);
    cycList.add(object);
  }
  
  /**
   * Returns the object from the this CycList according to the
   * path specified by the given (n1 n2 ...) zero-indexed path expression.
   *
   * @param cycList the given CycList
   * @param pathSpecification the given (n1 n2 ...) zero-indexed path expression
   * @return the object from this CycList according to the
   * path specified by the given (n1 n2 ...) zero-indexed path expression
   */
  public Object getSpecifiedObject(final CycList pathSpecification) {
    if (pathSpecification.size() == 0) { return this; }
    Object answer = (CycList) this.clone();
    CycList tempPathSpecification = pathSpecification;
    int index = 0;
    try {
      while (! tempPathSpecification.isEmpty()) {
        index = ((Integer) tempPathSpecification.first()).intValue();
        if(answer instanceof CycNart) {
          if(index == 0) {
            answer = ((CycNart)answer).getFunctor();
          } else {
            answer = ((CycNart)answer).getArguments().get(index-1);
          }
        } else {
          answer = ((CycList)answer).get(index);
        }
        tempPathSpecification = (CycList) tempPathSpecification.rest();
      }
      return answer;
    } catch (Exception e) {
      throw new RuntimeException("Can't get object specified by path expression: '" +
                                 pathSpecification + "' in forumla: '" + this + "'.  answer: " + answer + 
                                 " index: " + index + "\n" +
                                 StringUtils.getStringForException(e));
    }
  }
  
  /**
   * Sets the object in this CycList to the given value according to the
   * path specified by the given ((n1 n2 ...) zero-indexed path expression.
   *
   * @param cycList the given CycList
   * @param pathSpecification the (n1 n2 ...) zero-indexed path expression
   * @param value the given value
   */
  public void setSpecifiedObject(CycList pathSpecification, final Object value) {
    CycList parentContainer = null;
    Object container = this;
    int parentIndex = -1;
    int index = ((Integer) pathSpecification.first()).intValue();
    pathSpecification = (CycList) pathSpecification.rest();
    while(true) {
      if(container instanceof CycNart) {
        // after the first iteration the imbedded container can be a CycNart
        container = ((CycNart) container).toCycList();
        parentContainer.set(parentIndex, container);
      }
      if(pathSpecification.isEmpty()) { break; }
      parentContainer = (CycList) container;
      if(container instanceof CycList) {
        container = ((CycList) container).get(index);
      } else {
        throw new RuntimeException("Don't know a path into: " + container);
      }
      parentIndex = index;
      index = ((Integer) pathSpecification.first()).intValue();
      pathSpecification = (CycList) pathSpecification.rest();
    }
    if(container instanceof CycList) {
      container = ((CycList)container).set(index,  value);
    } else if(container instanceof CycNart) {
      if(index == 0) {
        ((CycNart)container).setFunctor((CycFort)value);
      } else {
        ((CycNart)container).getArguments().set(index-1, value);
      }
    } else {
      throw new RuntimeException("Don't know about: " + container);
    }
  }
  
  /** Returns a list of arg postions that describe all the locations where 
   * the given term can be found in this CycList. An arg position is a flat 
   * list of Integers that give the nths (0 based) to get to a particular 
   * sub term in a tree.
   * @param term The term to search for
   * @return The list of all arg postions where term can be found
   */  
  public List getArgPositionsForTerm(final Object term) {
    if (this.equals(term)) { return new ArrayList(); }
    List result = new ArrayList();
    List curArgPosition = new ArrayList();
    internalGetArgPositionsForTerm(term, this, curArgPosition, result);
    return result;
  }
  
  /** Private method used to implement getArgPositionForTerm() functionality.
   * @param term The term to search for
   * @param subTree The current sub part of the tree being explored
   * @param curPosPath The current arg position being explored
   * @param result Current store of arg positions found so far
   */
  private static void internalGetArgPositionsForTerm(Object term, Object subTree, 
      final List curPosPath, final List result) {
    if (term instanceof CycNart) {
      term = ((CycNart)term).toCycList();
    }
    if (term == subTree) {
      final List newArgPos = new ArrayList(curPosPath);
      result.add(newArgPos);
      return;
    }
    if (subTree == null) { return; }
    if (subTree instanceof CycNart) {
      subTree = ((CycNart)subTree).toCycList();
    }
    if (subTree.equals(term)) {
      final List newArgPos = new ArrayList(curPosPath);
      result.add(newArgPos);
      return;
    }
    if ((subTree instanceof CycList) && ((CycList)subTree).treeContains(term)) {
      int newPos = 0;
      for (Iterator iter = ((List)subTree).iterator(); iter.hasNext(); newPos++) {
        final List newPosPath = new ArrayList(curPosPath);
        newPosPath.add(new Integer(newPos));
        internalGetArgPositionsForTerm(term, iter.next(), newPosPath, result);
      }
    }
  }  
  
  public List getReferencedConstants() {
    return treeConstants();
  }

  
  //// serializable
    private void writeObject(ObjectOutputStream stream) throws java.io.IOException {
    stream.defaultWriteObject();
    if (!isProperList) {
      stream.writeBoolean(false);
      stream.writeObject( this.dottedElement);
    }
    else {
      stream.writeBoolean( true);
    }
  }
   
  private void readObject(ObjectInputStream stream) throws java.io.IOException, 
  java.lang.ClassNotFoundException {
    stream.defaultReadObject();
    isProperList = stream.readBoolean();
    if (!isProperList)
      dottedElement = stream.readObject();
  }

 
}



