package org.opencyc.inferencesupport;

import java.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;
import java.io.IOException;

/**
 * <tt>QueryLiteral</tt> object to model the attributes and behavior of a query literal.<p>
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
 * @see UnitTest#testQueryLiteral
 */
public class QueryLiteral  implements Comparable{

    /**
     * The number of instances matching this query literal formula in the KB. Value of -1
     * indicates the variable is not yet set.
     */
    public int nbrFormulaInstances = -1;

    /**
     * The query literal formula as an OpenCyc query.
     */
    protected CycList formula;

    /**
     * The collection of <tt>CycVariables</tt> used in the query literal.  There should
     * be at least one, because if there are no variables, then the query literal is
     * either always true or always false and has no effect on the
     * query's solution.
     */
    protected ArrayList variables;

    /**
     * Value which indicates that a given query literal subsumes another given query literal.
     */
    public static final int SUBSUMES = 1;

    /**
     * Value which indicates that a given query literal is subsumed by another given query literal.
     */
    public static final int SUBSUMED_BY = 2;

    /**
     * Value which indicates that a given rquery literal neither subsumes another given query literal or is
     * subsumed by another given query literal.
     */
    public static final int NO_SUBSUMPTION = 3;


    /**
     * Constructs a new <tt>QueryLiteral</tt> object from a <tt>CycList</tt> <tt>String</tt>
     * representation.<p>
     *
     * @param formulaString the query literal's formula <tt>String</tt>, which must be a well formed OpenCyc
     * query represented by a <tt>CycList</tt>.
     */
    public QueryLiteral (String formulaString) {
        formula = CycAccess.current().makeCycList(formulaString);
        gatherVariables();
    }

    /**
     * Constructs a new <tt>QueryLiteral</tt> object from a <tt>CycList</tt>.<p>
     *
    * @param formula the query literal's formula, which must be a well formed OpenCyc
     * query represented by a <tt>CycList</tt>.
     */
    public QueryLiteral(CycList formula) {
        this.formula = formula;
        gatherVariables();
    }

    /**
     * Simplifies a query literal expression.<p>
     * (#$and (<query literal1> <query literal2> ... <query literalN>)
     * becomes <query literal1> <query literal2> ... <query literalN>
     *
     * @param cycList the query literal expression that is simplified
     * @return an <tt>ArrayList</tt> of <tt>QueryLiteral</tt> objects.
     * @see UnitTest#testQueryLiteral
     */
    public static ArrayList simplifyQueryLiteralExpression(CycList cycList) throws IOException {
        ArrayList queryLiterals = new ArrayList();
        if (cycList.size() < 2)
            throw new RuntimeException("Invalid query literal: " + cycList);
        Object object = cycList.first();
        if (object instanceof CycConstant &&
            ((CycConstant) object).equals(CycAccess.and))
            for (int i = 1; i < cycList.size(); i++)
                queryLiterals.add(new QueryLiteral((CycList) cycList.get(i)));
        else
            queryLiterals.add(new QueryLiteral(cycList));
        return queryLiterals;
    }

    /**
     * Gathers the unique variables from the query literal's formula.
     */
    protected void gatherVariables() {
        HashSet uniqueVariables = new HashSet();
        Enumeration e = formula.cycListVisitor();
        while (true) {
            if (! e.hasMoreElements())
                break;
            Object element = e.nextElement();
            if (element instanceof CycVariable)
                uniqueVariables.add(element);
        }
        variables = new ArrayList(uniqueVariables);
    }


    /**
     * Gets the query literal's formula.
     *
     * @return a <tt>CycList</tt> which is the query literal's formula.
     */
    public CycList getFormula() {
        return formula;
    }

    /**
     * Returns the query literal's variables.
     *
     * @return the <tt>ArrayList</tt> which lists the unique <tt>CycVariables</tt> that are
     * used in the query literal's formula.
     */
    public ArrayList getVariables() {
        return variables;
    }

    /**
     * Returns the query literal's arity which is defined to be the number of variables, not
     * necessarily equalling the arity of the query literal's first predicate.
     *
     * @return query literal's arity which is defined to be the number of variables, not
     * necessarily equalling the arity of the query literal's first predicate
     */
    public int getArity() {
        return variables.size();
    }

    /**
     * Returns <tt>true</tt> if the object equals this object.
     *
     * @param object the object for comparison
     * @return <tt>boolean</tt> indicating equality of an object with this object.
     */
    public boolean equals(Object object) {
        if (! (object instanceof QueryLiteral))
            return false;
        QueryLiteral thatQueryLiteral = (QueryLiteral) object;
        return this.formula.equals(thatQueryLiteral.getFormula());
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
        if (! (object instanceof QueryLiteral))
            throw new ClassCastException("Must be a QueryLiteral object");
        return (new Integer(this.getArity())).compareTo(new Integer(((QueryLiteral) object).getArity()));
     }

    /**
     * Returns a value indicating the subsumption relationship, or lack of subsumption
     * relationship between this query literal and another query literal.
     *
     * @param queryLiteral the query literal for subsumption determination
     * @return <tt>QueryLiteral.SUBSUMES</tt> if this query literal subsumes the given query literal,
     * <tt>QueryLiteral.SUBSUMED_BY</tt> if this query literal is subsumed by the given query literal,
     * <tt>QueryLiteral.NO_SUBSUMPTION</tt> if this query literal is neither subsumed by the given query literal, nor
     * subsumes the given query literal
     */
    public int determineSubsumption(QueryLiteral queryLiteral) throws IOException {
        if (this.equals(queryLiteral))
            return SUBSUMES;
        if (! (this.getPredicate().equals(queryLiteral.getPredicate())))
            return NO_SUBSUMPTION;
        if (this.getArity() != queryLiteral.getArity())
            return NO_SUBSUMPTION;
        int answer = 0;
        for (int i = 0; i < this.getArguments().size(); i++) {
            Object thisArgument = this.getArguments().get(i);
            Object thatArgument = queryLiteral.getArguments().get(i);
            if (thisArgument.equals(thatArgument))
                continue;
            if (thisArgument instanceof CycVariable) {
                if (thatArgument instanceof CycVariable) {
                    if (! (thisArgument.equals(thatArgument)))
                        return NO_SUBSUMPTION;
                }
                else if (answer == SUBSUMED_BY)
                    return NO_SUBSUMPTION;
                else if (answer == 0) {
                    answer = SUBSUMES;
                    continue;
                }
            }
            if (thatArgument instanceof CycVariable) {
                if (answer == SUBSUMES)
                    return NO_SUBSUMPTION;
                else
                    answer = SUBSUMED_BY;
            }
            if (! (thisArgument instanceof CycConstant) ||
                ! (thatArgument instanceof CycConstant))
                return NO_SUBSUMPTION;
            if (! (CycAccess.current().isCollection_Cached((CycConstant) thisArgument)))
                return NO_SUBSUMPTION;
            if (! (CycAccess.current().isCollection_Cached((CycConstant) thatArgument)))
                return NO_SUBSUMPTION;
            if (CycAccess.current().isGenlOf_Cached((CycConstant) thisArgument,
                                                    (CycConstant) thatArgument)) {
                if (answer == SUBSUMED_BY)
                    return NO_SUBSUMPTION;
                else if (answer == 0) {
                    answer = SUBSUMES;
                    continue;
                }
            }
            if (CycAccess.current().isGenlOf_Cached((CycConstant) thatArgument,
                                                    (CycConstant) thisArgument)) {
                if (answer == SUBSUMES)
                    return NO_SUBSUMPTION;
                else if (answer == 0) {
                    answer = SUBSUMED_BY;
                    continue;
                }
            }
            return NO_SUBSUMPTION;
        }
        if (answer == 0)
            return SUBSUMES;
        else
            return answer;
    }

    /**
     * Returns whether this query literal is subsumed by the given query literal.
     *
     * @param queryLiteral the given queryLiteral for subsumption determination.
     * @return <tt>true</tt> iff this query literal is subsumed by the given <tt>QueryLiteral</tt> object.
     */
    public boolean isSubsumedBy(QueryLiteral queryLiteral) throws IOException {
        if (this.equals(queryLiteral))
            return true;
        else
            return this.determineSubsumption(queryLiteral) == QueryLiteral.SUBSUMED_BY;
    }

    /**
     * Returns whether this query literal subsumes the given query literal.
     *
     * @param queryLiteral the given query literal for subsumption determination.
     * @return <tt>true</tt> iff this query literal subsumes the given <tt>QueryLiteral</tt> object.
     */
    public boolean subsumes(QueryLiteral queryLiteral) throws IOException {
        return this.determineSubsumption(queryLiteral) == QueryLiteral.SUBSUMES;
    }

    /**
     * Creates and returns a copy of this <tt>QueryLiteral</tt>.
     *
     * @return a clone of this instance
     */
    public Object clone() {
        return new QueryLiteral((CycList) this.formula.clone());
    }

    /**
     * Returns the predicate of this <tt>QueryLiteral</tt> object.
     *
     * @return the predicate <tt>CycConstant</tt> or <tt>CycSymbol</tt>
     * of this <tt>QueryLiteral</tt> object
     */
    public CycConstant getPredicate() {
        return (CycConstant) formula.first();
    }

    /**
     * Returns the arguments of this <tt>QueryLiteral</tt> object.
     *
     * @return the arguments of this <tt>QueryLiteral</tt> object
     */
    public CycList getArguments() {
        return (CycList) formula.rest();
    }

    /**
     * Substitutes an object for a variable.
     *
     * @param oldVariable the variable to replaced
     * @parma newObject the <tt>Object</tt> to be substituted for the variable
     */
    public void substituteVariable(CycVariable variable, Object newObject) {
        if (! (variables.contains(variable)))
            throw new RuntimeException(variable + " is not a variable of " + this);
        variables.remove(variable);
        if (newObject instanceof CycVariable)
            variables.add(newObject);
        formula = formula.subst(newObject, variable);
    }

    /**
     * Returns <tt>true</tt> if this <tt>QueryLiteral</tt> is a #$different query literal.
     *
     * @return <tt>boolean</tt> indicating if this <tt>QueryLiteral</tt> is a #$different
     * query literal
     */
    public boolean isAllDifferent() throws IOException{
        if (this.getArity() < 2)
            return false;
        if (this.getPredicate().equals(CycAccess.different))
            return true;
        else
            return false;
    }

    /**
     * Returns <tt>true</tt> if this <tt>QueryLiteral</tt> is a simple evaluatable query literal,
     * which can be answered without KB lookup.  Typically an evaluatable constraint
     * queryLiteral is a relational operator applied to a primitive data type.
     *
     *
     * @return <tt>true</tt> if this <tt>QueryLiteral</tt> is a simple evaluatable query literal,
     * which can be answered without KB lookup
     */
    public boolean isEvaluatable() throws IOException {
        if (this.getArguments().size() < 2)
            return false;
        if (this.getPredicate().equals(CycAccess.numericallyEqual))
            return hasEvaluatableNumericalArgs();
        else if (this.getPredicate().toString().equals("or") ||
                 this.getPredicate().toString().equals("and")) {
            for (int i = 0; i < this.getArguments().size(); i++) {
                QueryLiteral orArgument = new QueryLiteral((CycList) this.getArguments().get(i));
                if (! orArgument.isEvaluatable())
                    return false;
            }
            return true;
        }
        else
            return false;
    }

    /**
     * Returns <tt>true</tt> if this <tt>QueryLiteral</tt> has simple evaluatable numerical arguments.
     * Numbers and variables return <tt>true</tt> and functional expressions return
     * <tt>true</tt> iff their arguments are simple numerical expressions.
     *
     *
     * @return <tt>true</tt> if this <tt>QueryLiteral</tt> has simple evaluatable numerical arguments
     */
    public boolean hasEvaluatableNumericalArgs() throws IOException {
        CycList args = this.getFormula().rest();
        for (int i = 0; i < args.size(); i++) {
            Object arg = args.get(i);
            if (arg instanceof CycVariable)
                continue;
            else if (arg instanceof Integer)
                continue;
            else if (arg instanceof CycNart) {
                CycNart cycNart = (CycNart) arg;
                if (cycNart.getFunctor().equals(CycAccess.plusFn)) {
                    Object plusFnArg = cycNart.getArguments().get(0);
                    if (plusFnArg instanceof CycVariable)
                        continue;
                    if (plusFnArg instanceof Integer)
                        continue;
                }
            }
            else if (arg instanceof CycList) {
                CycList cycList = (CycList) arg;
                if (cycList.first().equals(CycAccess.plusFn)) {
                    Object plusFnArg = cycList.second();
                    if (plusFnArg instanceof CycVariable)
                        continue;
                    if (plusFnArg instanceof Integer)
                        continue;
                }
            }
            else
                return false;
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> iff this is a ground formula having no variables.
     *
     * @return <tt>true</tt> iff this is a ground formula having no variables
     */
    public boolean isGround() {
        return this.getArity() == 0;
    }

    /**
     * Returns <tt>true</tt> iff this is a formula having one variable.
     *
     * @return <tt>true</tt> iff this is a formula having one variable
     */
    public boolean isUnary() {
        return this.getArity() == 1;
    }

    /**
     * Returns <tt>true</tt> iff the predicate has the irreflexive property:
     * (#$isa ?PRED #$IrreflexsiveBinaryPredicate).
     *
     * @param mt the microtheory in which the irreflexive property is sought
     * @return <tt>true</tt> iff the predicate has the irreflexive property:
     * (#$isa ?PRED #$IrreflexsiveBinaryPredicate)
     */
    public boolean isIrreflexive(CycFort mt) throws IOException {
        return CycAccess.current().isIrreflexivePredicate(this.getPredicate(), mt);
    }

    /**
     * Returns a string representation of the <tt>QueryLiteral</tt>.
     *
     * @return the query literal's formula formated as a <tt>String</tt>.
     */
    public String toString() {
        return formula.toString();
    }

    /**
     * Returns a cyclified string representation of the query literal's formula.
     * Embedded constants are prefixed with ""#$".
     *
     * @return a cyclified <tt>String</tt>.
     */
    public String cyclify() {
        return formula.cyclify();
    }

    /**
     * Returns a new <tt>QueryLiteral</tt> which is the result of substituting the given
     * <tt>Object</tt> value for the given <tt>CycVariable</tt>.
     *
     * @param cycVariable the variable for substitution
     * @param value the value which is substituted for each occurrance of the variable
     * @return a new <tt>QueryLiteral</tt> which is the result of substituting the given
     * <tt>Object</tt> value for the given <tt>CycVariable</tt>
     */
    public QueryLiteral instantiate(CycVariable cycVariable, Object value) {
        if (! variables.contains(cycVariable))
            throw new RuntimeException("Cannot instantiate " + cycVariable +
                                       " in query literal " + this);
        CycList newQueryLiteral = formula.subst(value, cycVariable);
        return new QueryLiteral(newQueryLiteral);
    }
}