package org.opencyc.constraintsolver;

/**
 * <tt>Rule</tt> object to model the attributes and behavior of a constraint rule.<p>
 *
 * @version $Id$
 * @author Stephen Reed
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 */

import java.util.*;
import org.opencyc.cycobject.*;

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
     * Simplify a rule expression.<p>
     * (#$and (<rule1> <rule2> ... <ruleN>) becomes <rule1> <rule2> ... <ruleN>
     *
     * @return an <tt>ArrayList</tt> of <tt>Rule</tt> objects.
     */
    public static ArrayList simplifyRuleExpression(CycList cycList) {
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
     * Construct a new <tt>Rule</tt> object.
     *
     * @param rule the rule's formula, which must be a well formed OpenCyc
     * query represented by a <tt>CycList</tt>.
     */
    public Rule (CycList rule) {
        this.rule = rule;
        gatherVariables();
    }

    /**
     * Gather the unique variables from the rule's formula.
     */
    private void gatherVariables() {
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
     * Get the rule's formula.
     *
     * @return a <tt>CycList</tt> which is the rule's formula.
     */
    public CycList getRule() {
        return rule;
    }

    /**
     * Get the rule's variables.
     *
     * @return the <tt>ArrayList</tt> which lists the unique <tt>CycVariables</tt> that are
     * used in the rule's formula.
     */
    public ArrayList getVariables() {
        return variables;
    }

    /**
     * Get the rule's arity, which is the number of variables
     * in this <tt>Rule</tt>.
     *
     * @return an <tt>int</tt> which is the number of <tt>CycVariables</tt>
     * in the rule's formula.
     */
    public int getArity() {
        return variables.size();
    }

    /**
     * Return <tt>true</tt> if the object equals this object.
     *
     * @return <tt>boolean</tt> indicating equality of an object with this object.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Rule))
            return false;
        Rule  thatRule = (Rule) object;
        return this.rule.equals(thatRule.getRule());
    }

    /**
     * Return the predicate of this <tt>Rule</tt> object.
     *
     * @return the predicate <tt>CycConstant</tt> or <tt>CycSymbol</tt>
     * of this <tt>Rule</tt> object
     */
    public Object getPredicate() {
        return (CycConstant) rule.first();
    }

    /**
     * Return <tt>true</tt> if this is a variable domain populating <tt>Rule</tt>.
     *
     * @return <tt>boolean</tt> indicating if this is a variable domain populating
     * <tt>Rule</tt>.
     */
    public boolean isVariableDomainPopulatingRule() {
        if (this.getArity() != 1)
            // Only unary rules can populate a domain.
            return false;
        //TODO put elementOf in the right place
        //if (this.predicate().equals(CycConstant.elementOf))
        if (this.getPredicate().toString().equals("elementOf"))
            return true;
        if (this.getPredicate().toString().equals("isa"))
            return true;
        else
            return false;
    }

    /**
     * Return a string representation of the <tt>Rule</tt>.
     *
     * @return the rule's formula formated as a <tt>String</tt>.
     */
    public String toString() {
        return rule.toString();
    }
    /**
     * Return a cyclified string representation of the rule's formula.
     * Embedded constants are prefixed with ""#$".
     *
     * @return a cyclified <tt>String</tt>.
     */

    public String cyclify() {
        return rule.cyclify();
    }
}