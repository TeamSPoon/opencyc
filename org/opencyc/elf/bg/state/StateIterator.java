package org.opencyc.elf.bg.state;

//// Internal Imports
//// External Imports
import java.util.Iterator;


/**
 * <P>
 * StateIterator provides an iterator over the stateVariable/values.
 * </p>
 * 
 * @version $Id: BehaviorGeneration.java,v 1.3 2002/11/19 02:42:53 stephenreed
 *          Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class StateIterator implements Iterator {
  //// Constructors

  /**
   * Creates a new instance of StateIterator
   * @param state DOCUMENT ME!
   */
  public StateIterator(State state) {
    this.state = state;
    iterator = state.stateVariableDictionary.keySet().iterator();
  }

  //// Public Area

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other words,
   * returns <tt>true</tt> if <tt>next</tt> would return an element rather
   * than throwing an exception.)
   * 
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return iterator.hasNext();
  }

  /**
   * Returns the next element in the iteration.
   * 
   * @return the next element in the iteration.
   * 
   */
  public Object next() {
    return iterator.next();
  }

  /**
   * Removes from the underlying collection the last element returned by the
   * iterator (optional operation).  This method can be called only once per
   * call to <tt>next</tt>.  The behavior of an iterator is unspecified if the
   * underlying collection is modified while the iteration is in progress in
   * any way other than by calling this method.
   * 
   * @exception UnsupportedOperationException if the <tt>remove</tt> operation
   *            is not supported by this Iterator.
   * @exception IllegalStateException if the <tt>next</tt> method has not yet
   *            been called, or the <tt>remove</tt> method has already been
   *            called after the last call to the <tt>next</tt> method.
   * @throws java.lang.UnsupportedOperationException DOCUMENT ME!
   */
  public void remove() throws UnsupportedOperationException, IllegalStateException {
    throw new java.lang.UnsupportedOperationException("remove method is not supported for this Iterator");
  }

  //// Protected Area

  /** the state over which this iterator operates */
  protected State state;

  /** the state state variable iterator */
  Iterator iterator;

  //// Private Area
  //// Internal Rep
  //// Main
}