package org.opencyc.constraintsolver;

import java.util.*;
import org.opencyc.cycobject.*;

/**
 * <tt>Rule</tt> object to model the attributes and behavior of a constraint rule.<p>
 *
 * @version $Id$
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
 *
 * @see UnitTest#testRule
 */
public class Rule {

    /**
     * The constraint rule formula as an OpenCyc query.
     */
    protected CycList rule;

    /**
     * The collection of <tt>CycVariables</tt> used in the rule.  There should
     * be at least one, because if there are no variables, then the rule is
     * either always true or always false and has no effect on the
     * constraint problem's solution.
     */
    protected ArrayList variables;

    /**
     * Constructs a new <tt>Rule</tt> object from a <tt>CycList</tt> <tt>String</tt>
     * representation.<p>
     *
     * @param ruleString the rule's formula <tt>String</tt>, which must be a well formed OpenCyc
     * query represented by a <tt>CycList</tt>.
     */
    protected Rule (String ruleString) {
        rule = new CycList(ruleString);
        gatherVariables();
    }

    /**
     * Constructs a new <tt>Rule</tt> object from a <tt>CycList</tt>.<p>
     *
     * <pre>
     *  String ruleAsString = "(#$isa ?x #$Cathedral)";
     *  Rule rule1 = new Rule (new CycList(ruleAsString));
     * </pre>
     *
     * @param rule the rule's formula, which must be a well formed OpenCyc
     * query represented by a <tt>CycList</tt>.
     */
    protected Rule(CycList rule) {
        this.rule = rule;
        gatherVariables();
    }
    /**
     * Simplifies a rule expression.<p>
     * (#$and (<rule1> <rule2> ... <ruleN>) becomes <rule1> <rule2> ... <ruleN>
     *
     * @param cycList the rule expression that is simplified
     * @return an <tt>ArrayList</tt> of <tt>Rule</tt> objects.
     * @see UnitTest#testRule
     */
    protected static ArrayList simplifyRuleExpression(CycList cycList) {
        ArrayList rules = new ArrayList();
        if (cycList.size() < 2)
            throw new RuntimeException("Invalid rule: " + cycList);
        //TODO - use static value from CycConstant class.
        if (cycList.first().toString().equals("and"))
            for (int i = 1; i < cycList.size(); i++)
                rules.add(new Rule((CycList) cycList.get(i)));
        else
            rules.add(new Rule(cycList));
        return rules;
    }

    /**
     * Gathers the unique variables from the rule's formula.
     */
    protected void gatherVariables() {
        HashSet uniqueVariables = new HashSet();
        Enumeration e = rule.cycListVisitor();
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
     * Gets the rule's formula.
     *
     * @return a <tt>CycList</tt> which is the rule's formula.
     */
    protected CycList getRule() {
        return rule;
    }

    /**
     * Gets the rule's variables.
     *
     * @return the <tt>ArrayList</tt> which lists the unique <tt>CycVariables</tt> that are
     * used in the rule's formula.
     */
    protected ArrayList getVariables() {
        return variables;
    }

    /**
     * Gets the rule's arity, which is the number of variables
     * in this <tt>Rule</tt>.
     *
     * @return an <tt>int</tt> which is the number of <tt>CycVariables</tt>
     * in the rule's formula.
     */
    protected int getArity() {
        return variables.size();
    }

    /**
     * Returns <tt>true</tt> if the object equals this object.
     *
     * @param object the object for comparison
     * @return <tt>boolean</tt> indicating equality of an object with this object.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Rule))
            return false;
        Rule  thatRule = (Rule) object;
        return this.rule.equals(thatRule.getRule());
    }

    /**
     * Returns the predicate of this <tt>Rule</tt> object.
     *
     * @return the predicate <tt>CycConstant</tt> or <tt>CycSymbol</tt>
     * of this <tt>Rule</tt> object
     */
    protected Object getPredicate() {
        return (CycConstant) rule.first();
    }

    /**
     * Returns <tt>true</tt> if this is a variable domain populating <tt>Rule</tt>.
     *
     * @return <tt>boolean</tt> indicating if this is a variable domain populating
     * <tt>Rule</tt>.
     */
    protected boolean isVariableDomainPopulatingRule() {
        return isIntensionalVariableDomainPopulatingRule() ||
               isExtensionalVariableDomainPopulatingRule();
    }

    /**
     * Returns <tt>true</tt> if this <tt>Rule</tt> is a #$different constraint rule.
     *
     * @return <tt>boolean</tt> indicating if this <tt>Rule</tt> is a #$different
     * constraint rule
     */
    protected boolean isAllDifferent() {
        if (this.getArity() < 2)
            return false;
        //TODO make right
        if (this.getPredicate().toString().equals("different"))
            return true;
        else
            return false;
    }

    /**
     * Returns <tt>true</tt> if this is an intensional variable domain populating <tt>Rule</tt>.
     * An extensional rule is one in which values are queried from the OpenCyc KB.
     *
     * @return <tt>boolean</tt> indicating if this is an intensional variable domain populating
     * <tt>Rule</tt>.
     */
    protected boolean isIntensionalVariableDomainPopulatingRule() {
        if (this.getArity() != 1)
            // Only unary rules can populate a domain.
            return false;
        //TODO make right
        if (this.getPredicate().toString().equals("isa"))
            return true;
        else
            return false;
    }

    /**
     * Returns <tt>true</tt> if this is an extensional variable domain populating <tt>Rule</tt>.
     * An extensional rule is one in which all the values are listed.
     *
     * @return <tt>boolean</tt> indicating if this is an extensional variable domain populating
     * <tt>Rule</tt>.
     */
    protected boolean isExtensionalVariableDomainPopulatingRule() {
        if (this.getArity() != 1)
            // Only unary rules can populate a domain.
            return false;
        //TODO put elementOf in the right place
        //if (this.predicate().equals(CycConstant.elementOf))
        if (this.getPredicate().toString().equals("elementOf"))
            return true;
        else
            return false;
    }

    /**
     * Returns a string representation of the <tt>Rule</tt>.
     *
     * @return the rule's formula formated as a <tt>String</tt>.
     */
    public String toString() {
        return rule.toString();
    }

    /**
     * Returns a cyclified string representation of the rule's formula.
     * Embedded constants are prefixed with ""#$".
     *
     * @return a cyclified <tt>String</tt>.
     */
    protected String cyclify() {
        return rule.cyclify();
    }

    /**
     * Returns a new <tt>Rule</tt> which is the result of substituting the given
     * <tt>Object</tt> value for the given <tt>CycVariable</tt>.
     *
     * @param cycVariable the variable for substitution
     * @param value the value which is substituted for each occurrance of the variable
     * @return a new <tt>Rule</tt> which is the result of substituting the given
     * <tt>Object</tt> value for the given <tt>CycVariable</tt>
     */
    protected Rule instantiate(CycVariable cycVariable, Object value) {
        if (! variables.contains(cycVariable))
            throw new RuntimeException("Cannot instantiate " + cycVariable +
                                       " in rule " + this);
        CycList newRule = rule.subst(value, cycVariable);
        return new Rule(newRule);
    }
}