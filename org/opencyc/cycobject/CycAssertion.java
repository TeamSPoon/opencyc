package  org.opencyc.cycobject;

import  java.io.IOException;
import  java.util.*;
import  org.apache.oro.util.*;
import  org.opencyc.api.*;


/**
 * Provides the behavior and attributes of OpenCyc assertions.<p>
 * <p>
 * Assertions are communicated over the binary API using their Id number (an int).
 * The associated formula, microtheory, truth-value, direction, and remaining attributes are
 * is fetched later.
 *
 * @version $Id$
 * @author Stephen L. Reed
 * @author Dan Lipofsky
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
public class CycAssertion {
    /**
     * Least Recently Used Cache of CycAssertions, so that a reference to an existing <tt>CycAssertion</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cache = new CacheLRU(500);

    /**
     * Assertion id assigned by the local KB server.  Not globally unique.
     */
    protected Integer id;

    /**
     * The assertion in the form of a <tt>CycList</tt>.
     */
    private CycList formula;

    /**
     * Constructs an incomplete <tt>CycAssertion</tt> object given its local KB id.
     *
     * @param id the assertion id assigned by the local KB
     */
    public CycAssertion (Integer id) {
        this.id = id;
    }

    /**
     * Indicates whether the object is equal to this object.
     *
     * @return <tt>true</tt> if the object is equal to this object, otherwise
     * returns <tt>false</tt>
     */
    public boolean equals (Object object) {
        if (!(object instanceof CycAssertion))
            return  false;
        CycAssertion cycAssertion = (CycAssertion)object;
        return formula.equals(cycAssertion.id);
    }

    /**
     * Returns a <tt>String</tt> representation of the <tt>CycAssertion</tt>.
     *
     * @return a <tt>String</tt> representation of the <tt>CycAssertion</tt>
     */
    public String toString () {
        if (formula == null)
            return "assertion-with-id:" + id;
        else
            return formula.cyclify();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return formula.cyclify();
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
     * Returns the formula for this assertion.
     *
     * @return the formula for this assertion
     */
    public CycList getFormula () {
        if (formula == null) {
            CycAssertion cycAssertion = null;
            try {
                cycAssertion = CycAccess.current().completeCycAssertion(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            formula = cycAssertion.formula;
        }
        return formula;
    }

    /**
     * Sets the formula for this assertion.
     *
     * @param formula the formula for this assertion
     */
    public void setFormula (CycList formula) {
        this.formula = formula;
    }

    /**
     * Returns the id for this assertion.
     *
     * @return the id for this assertion
     */
    public Integer getId () {
        return id;
    }

    /**
     * Resets the Cyc assertion cache.
     */
    public static void resetCache() {
        cache = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycAssertion<tt> to the cache.
     */
    public static void addCache(CycAssertion cycAssertion) {
        cache.addElement(cycAssertion.id, cycAssertion);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with guid, returning null if not found in the cache.
     */
    public static CycAssertion getCache(Integer id) {
        return (CycAssertion) cache.getElement(id);
    }

    /**
     * Removes the cycConstant from the cache if it is contained within.
     */
    public static void removeCache(Integer id) {
        Object element = cache.getElement(id);
        if (element != null)
            cache.addElement(id, null);
    }

    /**
     * Returns the size of the <tt>CycAssertion</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycAssertion</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }
}







