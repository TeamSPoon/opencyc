package org.opencyc.inferencesupport;

import org.opencyc.cycobject.*;
import org.opencyc.api.*;
import java.util.*;
import java.io.*;

/**
 * <tt>BindingSet</tt> object to contain bindings for query literals.  This object is
 * created in an unpopulated state and is populated during query processing.<p>
 *
 * @version $Id$
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
 *
 */
public class BindingSet implements Comparable {
    /**
     * The literal for which these bindings were obtained.
     */
    protected QueryLiteral queryLiteral;

    /**
     * The microtheory in which the variable bindings were obtained
     */
    protected CycFort mt;

    /**
     * The list of binding value lists associated with each variable.  The value <tt>null</tt>
     * means that the bindings for this query literal have not yet been determined.
     */
    protected ArrayList bindingValues;

    /**
     * Constructs a new <tt>BindingSet</tt> object with the given <tt>QueryLiteral</tt>.
     *
     * @param queryLiteral the literal for which bindings are obtained
     * @param mt the microtheory in which the variable bindings are obtained
     */
    public BindingSet(QueryLiteral queryLiteral, CycFort mt) {
        this.queryLiteral = queryLiteral;
        this.mt = mt;
    }

    /**
     * Returns the literal for this binding set.
     *
     * @return the <tt>QueryLiteral</tt> for this binding set
     */
    public QueryLiteral getQueryLiteral() {
        return queryLiteral;
    }

    /**
     * Gets the binding values for this binding set.
     *
     * @return the list of binding value lists associated with each variable,
     */
    public ArrayList getBindingValues() {
        return bindingValues;
    }

    /**
     * Sets the binding values for this binding set.
     *
     * @param bindingValues the list of binding value lists associated with each variable,
     */
    public void setBindingValues(ArrayList bindingValues) {
        this.bindingValues = bindingValues;
    }

    /**
     * Sets the number of instances of this literal in the KB.
     *
     * @param nbrFormulaInstances the number of instances of this literal in the KB
     */
    public void getNbrInstances(int nbrFormulaInstances) {
        queryLiteral.nbrFormulaInstances = nbrFormulaInstances;
    }

    /**
     * Gets the number of instances of this literal in the KB.
     *
     * @return the number of instances matching this literal formula in the KB. Value of -1
     * indicates the variable is not yet set
     */
    public int getNbrInstances() throws IOException {
        int nbrFormulaInstances = queryLiteral.nbrFormulaInstances;
        if (nbrFormulaInstances == -1)
            nbrFormulaInstances = CycAccess.current().countUsingBestIndex(queryLiteral.getFormula(),
                                                                          mt);
        queryLiteral.nbrFormulaInstances = nbrFormulaInstances;
        return nbrFormulaInstances;
    }

    /**
     * Returns the size of the binding set for this literal.
     *
     * @return the size of the binding set for this literal
     */
    public int size() {
        return bindingValues.size();
    }

    /**
     * Returns the variables in this binding set.
     *
     * @return the variables in this binding set
     */
    public ArrayList getVariables() {
        return queryLiteral.getVariables();
    }

    /**
     * Returns the microtheory in which the variable bindings were obtained.
     *
     * @return the microtheory in which the variable bindings were obtained
     */
    public CycFort getMt() {
        return mt;
    }

    /**
     * Displays the binding set.
     */
    public void displayBindingSet() {
        System.out.println("Binding Set for " + this.getQueryLiteral().cyclify() +
                            " in " + mt.cyclify());
        System.out.println("  variables " + this.getVariables());
        for (int i = 0; i < this.getBindingValues().size(); i++) {
            CycList bindingList = (CycList) getBindingValues().get(i);
            System.out.println("  " + bindingList.cyclify());
        }
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     *
     * @param object the reference object with which to compare.
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
     public int compareTo (Object object) {
        if (! (object instanceof BindingSet))
            throw new ClassCastException("Must be a BindingSet object");
        return (new Integer(this.size())).compareTo(new Integer(((BindingSet) object).size()));
     }

    /**
     * Provides the hash code appropriate for the <tt>BindingSet</tt>.
     *
     * @return the hash code for the <tt>BindingSet</tt>
     */
    public int hashCode() {
        return this.queryLiteral.hashCode();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>BindingSet</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (object instanceof BindingSet &&
            this.queryLiteral.equals(((BindingSet)object).queryLiteral)) {
            return true;
        }
        else
            return false;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return this.queryLiteral.cyclify();
    }



}