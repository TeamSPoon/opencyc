package org.opencyc.constraintsolver;

/**
 * <tt>Rule</tt> object to model the attributes and behavior of a constraint problem.<p>
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

public class ConstraintProblem {

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public int verbosity = 3;

    /**
     * When <tt>true</tt> randomize the order of the variables and domain values before
     * beginning the search for a solution.  Do this when tuning search heuristics to avoid
     * bias for a particular order of input.
     */
    public boolean randomizeInput = false;

    /**
     * The value of the variable value domain size beyond which the initial values are not
     * all fetched from the KB using #$isa, rather some other more specific constraint
     * rule populates the variable domain as needed.
     */
    public int domainSizeThreshold = 100;

    /**
     * The number of solutions requested.  When <tt>null</tt>, all solutions are sought.
     */
    public Integer nbrSolutionsRequested = new Integer(1);

    /**
     * The input problem <tt>CycList</tt>.
     */
    protected CycList problem = null;

    /**
     * Collection of the simplified constraint rules derived
     * from the input problem <tt>CycList</tt>.  These include
     * <tt>#$elementOf</tt> predicates which populate variable
     * domains and then are discarded from the constraint rule
     * set.
     */
    protected ArrayList simplifiedRules;

    /**
     * Collection of the rules which populate variable domains.
     */
    protected ArrayList domainPopulationRules = new ArrayList();

    /**
     * Collection of the constraint rules used in the search for
     * solution(s).
     */
    protected ArrayList constraintRules = new ArrayList();

    /**
     * Dictionary of dictionaries of integers used to mark domain values
     * during search.  The purpose of marking is to eliminate values from
     * the solution.  First key indexes by constraint variable, second key
     * indexes by domain value for the variable, and the integer represents
     * the search level at which the variable domain value was marked.
     */
    protected HashMap domains = new HashMap();

    /**
     * variable --> domain populating rule<p>
     * Dictionary of items describing whether the domain of the key
     * variable is too large to handle efficiently.  For high cardinality
     * domains, the domain size is determined from the KB without asking
     * for all of the values.  For variables not exceeding the <tt>domainSizeThreshold</tt>,
     * the dictionary contains a value of <tt>null</tt>.
     */
    protected HashMap highCardinalityDomains = new HashMap();

    /**
     * Collection of the constraint variables as <tt>CycVariable</tt> objects.
     */
    protected ArrayList variables = new ArrayList();

    /**
     * Dictionary of variable --> domain value list.
     */
    protected HashMap varsDictionary = new HashMap();

    /**
     * List of solutions where each solution is a list of constraint variable -
     * domain value bindings which satisfy all the constraint rules.
     */
    protected ArrayList solutions = new ArrayList();

    /**
     * Number of KB asks performed during the search for solution(s).
     */
    protected int nbrAsks = 0;

    /**
     * Number of search steps performed during the search for solution(s).
     */
    protected int nbrSteps = 0;

    /**
     * Number of solutions found by the search.  Will not be more than the
     * number requested if <tt>nbrSolutionsRequested</tt> is not <tt>null</tt>.
     */
    protected int nbrSolutionsFound = 0;

    /**
     * Construct a new <tt>ConstraintProblem</tt> object.
     */
    public ConstraintProblem() {
    }

    /**
     * Solve a constraint problem and return a list of solutions if one or more
     * was found, otherwise return <tt>null</tt>.
     *
     * @param problem a constraint problem in the form of an OpenCyc query <tt>CycList</tt>
     * @return an <tt>ArrayList</tt> of solutions or <tt>null</tt> if no solutions were
     * found.  Each solution is an <tt>ArrayList</tt> of variable binding <tt>ArrayList</tt>
     * objects, each binding having the form of an <tt>ArrayList</tt> where the first
     * element is the <tt>CycVariable</tt> and the second element is the domain value
     * <tt>Object</tt>.
     */
    public ArrayList solve(CycList problem) {
        this.problem = problem;
        extractRulesAndDomains();
        gatherVariables();

        return solutions;
    }

    /**
     * Simplify the input problem into its constituent <tt>Rule</tt> objects,
     * then divide the input rules into those which populate the variable
     * domains, and those which subsequently constrain the search for
     * one or more solutions.
     */
    protected void extractRulesAndDomains() {
        simplifiedRules = Rule.simplifyRuleExpression(problem);
        for (int i = 0; i < simplifiedRules.size(); i++) {
            Rule rule = (Rule) simplifiedRules.get(i);
            if (rule.isVariableDomainPopulatingRule())
                domainPopulationRules.add(rule);
            else
                constraintRules.add(rule);
        }
    }

    /**
     * Gather the unique variables used in this constraint problem.
     */
    protected void gatherVariables() {
        HashSet uniqueVariables = new HashSet();
        for (int i = 0; i < simplifiedRules.size(); i++) {
            Rule rule = (Rule) simplifiedRules.get(i);
            uniqueVariables.addAll(rule.getVariables());
        }
        variables.addAll(uniqueVariables);
        //System.out.println(variables);
    }

    /**
     * Return the number of variable domain populating <tt>Rule</tt>
     * objects derived from the input problem.
     *
     * @return the number of variable domain populating <tt>Rule</tt> objects.
     */
    public int getNbrDomainPopulationRules() {
        return domainPopulationRules.size();
    }

    /**
     * Return the number of constraint <tt>Rule</tt> objects derived from
     * the input problem.
     *
     * @return the number of constraint <tt>Rule</tt> objects.
     */
    public int getNbrConstraintRules() {
        return constraintRules.size();
    }

    /**
     * Return the number of <tt>Variable</tt> objects derived from
     * the input problem.
     *
     * @return the number of <tt>CycVariable</tt> objects.
     */
    public int getNbrVariables() {
        return variables.size();
    }



}