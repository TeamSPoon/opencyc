package org.opencyc.elf.wm.state;

//// Internal Imports

//// External Imports
import java.util.Iterator;

/** StateIterator provides an iterator over the stateVariable/values.
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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
public class StateIterator implements Iterator {
  //// Constructors

  /** Creates a new instance of StateIterator
   * @param state DOCUMENT ME!
   */
  public StateIterator(State state) {
    this.state = state;
    iterator = state.stateVariableDictionary.keySet().iterator();
  }

  //// Public Area

  /** Returns <tt>true</tt> if the iteration has more elements. (In other words,
   * returns <tt>true</tt> if <tt>next</tt> would return an element rather
   * than throwing an exception.)
   * 
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return iterator.hasNext();
  }

  /** Returns the next element in the iteration.
   * 
   * @return the next element in the iteration.
   */
  public Object next() {
    return iterator.next();
  }

  /** Removes from the underlying collection the last element returned by the
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
  public void remove() {
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