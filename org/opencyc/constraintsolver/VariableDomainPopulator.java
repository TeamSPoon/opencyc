package org.opencyc.constraintsolver;

import java.util.*;
import java.io.*;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

/**
 * <tt>VariableDomainPopulator</tt> object to contain information about variables
 * and applicable rules, and which determines the best rule to populate each
 * variable.  For high cardinality variables, which are those variables having
 * a domain size exceeding a high cardinality threshold, the domain population is
 * typically postponed until the forward checking search has instantiated enough
 * other variables so that the high cardinality variable's domain is more efficiently
 * obtained from the KB.<p>
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
 * @see UnitTest#testConstraintProblem
 */
public class VariableDomainPopulator {
    /**
     * Reference to the parent <tt>ConstraintProblem</tt> object.
     */
    protected ConstraintProblem constraintProblem;

    /**
     * Reference to the parent <tt>ValueDomains</tt> object.
     */
    protected ValueDomains valueDomains;

    /**
     * Reference to the parent list of domain populating constraint rules.
     */
    protected ArrayList domainPopulationRules;

    /**
     * Reference to the parent list of constraint rules.
     */
    protected ArrayList constraintRules;

    /**
     * List of <tt>VariablePopulation</tt> objects used to determine the best domain population rule
     * for each variable.
     */
    protected ArrayList candidateVariablePopulators = new ArrayList();

    /**
     * The default value of the variable value domain size beyond which the initial values are not
     * all fetched from the KB.
     */
    public static final int DEFAULT_DOMAIN_SIZE_THRESHOLD = 50;

    /**
     * The value of the variable value domain size beyond which the initial values are not
     * all fetched from the KB.
     */
    protected int domainSizeThreshold = DEFAULT_DOMAIN_SIZE_THRESHOLD;

    /**
     * variable --> VariableDomainPopulator<br>
     */
    protected HashMap variableDomainPopulators = new HashMap();

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = ConstraintProblem.DEFAULT_VERBOSITY;

    /**
     * Constructs a new <tt>VariableDomainPopulator</tt> object.
     */
    public VariableDomainPopulator(ConstraintProblem constraintProblem) {
        this.constraintProblem = constraintProblem;
        this.domainPopulationRules = constraintProblem.domainPopulationRules;
        this.constraintRules = constraintProblem.constraintRules;
        this.valueDomains = constraintProblem.valueDomains;
    }

    /**
     * Adds the given <tt>VariablePopulationItem</tt> object to the list
     * of variable populator items.  Each item contains a variable, an applicable
     * rule, and the number of values that rule would yield if asked in the KB.
     */
    public void add(VariablePopulationItem variablePopulationItem) {
        this.candidateVariablePopulators.add(variablePopulationItem);
    }

    /**
     * Determines the best domain population rule to populate each constraint problem variable, and populates
     * those which do not exceed the domain size threshold.
     */
    public void populateDomains() throws IOException {
        Collections.sort(candidateVariablePopulators);
        for (int i = 0; i < candidateVariablePopulators.size(); i++) {
            VariablePopulationItem variablePopulationItem =
                (VariablePopulationItem) candidateVariablePopulators.get(i);
            if (verbosity > 3)
                System.out.println("Considering candidate domain population for " +
                                   variablePopulationItem.cycVariable.cyclify() +
                                   "\n  " + variablePopulationItem.rule +
                                   "\n  which has " + variablePopulationItem.getNbrFormulaInstances() + " instances");
            if (i == 0) {
                if (verbosity > 3)
                    System.out.println("  First variable must always be populated");
                variableDomainPopulators.put(variablePopulationItem.cycVariable, variablePopulationItem);
                domainPopulationRules.add(variablePopulationItem.rule);
                valueDomains.varsDictionary.put(variablePopulationItem.cycVariable, new ArrayList());
                continue;
            }
            if (variableDomainPopulators.containsKey(variablePopulationItem.cycVariable)) {
                if (verbosity > 3)
                    System.out.println("  Variable already has the best domain populating rule");
                constraintRules.add(variablePopulationItem.rule);
                continue;
            }
            if (variablePopulationItem.getNbrFormulaInstances() > domainSizeThreshold) {
                if (verbosity > 3)
                    System.out.println("  Rule's domain size exceeds the threshold of " + domainSizeThreshold +
                                       ", domain population postponed");
                variablePopulationItem.rule = null;
                variableDomainPopulators.put(variablePopulationItem.cycVariable, variablePopulationItem);
                valueDomains.varsDictionary.put(variablePopulationItem.cycVariable, new ArrayList());
                continue;
            }
            if (verbosity > 3)
                System.out.println("  Will populate using this rule");
                domainPopulationRules.add(variablePopulationItem.rule);
                valueDomains.varsDictionary.put(variablePopulationItem.cycVariable, new ArrayList());
        }
    }

    /**
     * Returns <tt>true</tt> iff this variable's domain is too large for efficient
     * processing.  The constraint solver will employ other rules to populate
     * domain values via OpenCyc KB queries.
     *
     * @param cycVariable the variable under consideration
     * @return <tt>true</tt> iff this variable's domain is too large for efficient
     * processing
     */
    public boolean isPostponedHighCardinalityDomain(CycVariable cycVariable) {
        VariablePopulationItem variablePopulationItem =
            (VariablePopulationItem) variableDomainPopulators.get(cycVariable);
        boolean answer = variablePopulationItem.getNbrFormulaInstances() > domainSizeThreshold;
        if (verbosity > 8)
            System.out.println("\nhigh cardinality domain for " + cycVariable + " --> " + answer);
        return answer;
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Returns <tt>true</tt> iff the given <tt>Rule</tt> was used to populate the domain
     * of the given high cardinality variable.
     *
     * @param rule the rule under consideration
     * @param cycVariable the variable under consideration
     * @return <tt>true</tt> iff the given <tt>Rule</tt> was used to populate the domain
     * of the given high cardinality variable
     */
    public boolean isPopulatingRule(Rule rule, CycVariable cycVariable) {
        return rule.equals(getPopulatingRule(cycVariable));
    }

    /**
     * Sets the domain size threshold, beyond which the population of a
     * variable's domain is typically postponed until the forward checking
     * search.
     *
     * @param domainSizeThreshold domain size threshold
     */
    public void setDomainSizeThreshold(int domainSizeThreshold) {
        this.domainSizeThreshold = domainSizeThreshold;
    }

    /**
     * Gets the domain size of the high cardinality variable.
     *
     * @param cycVariable the variable under consideration
     * @return <tt>int</tt> the domain size of the high cardinality variable
     */
    public int getDomainSize(CycVariable cycVariable) {
        VariablePopulationItem variablePopulationItem =
            (VariablePopulationItem) variableDomainPopulators.get(cycVariable);
        return variablePopulationItem.getNbrFormulaInstances();
    }

    /**
     * Sets the domain-populating <tt>Rule</tt> of the high cardinality variable.
     *
     * @param cycVariable the high-cardinality variable under consideration
     * @parma rule the domain-populating rule
     */
    public void setPopulatingRule(CycVariable cycVariable, Rule rule) {
        VariablePopulationItem variablePopulationItem =
            (VariablePopulationItem) variableDomainPopulators.get(cycVariable);
        variablePopulationItem.rule = rule;
        if (verbosity > 8)
            System.out.println("\nset high cardinality populating rule for " +
                               cycVariable.cyclify() + " to " + rule.cyclify());
    }

    /**
     * Gets the domain-populating <tt>Rule</tt> of the high cardinality variable.
     *
     * @param cycVariable the variable under consideration
     * @return the domain-populating <tt>Rule</tt> of the high cardinality variable
     */
    public Rule getPopulatingRule(CycVariable cycVariable) {
        VariablePopulationItem variablePopulationItem =
            (VariablePopulationItem) variableDomainPopulators.get(cycVariable);
        return variablePopulationItem.rule;
    }

    /**
     * Populates the domain by asking a query.
     *
     * @param rule the query to asked in the KB
     * @param cycVariable the variable whose value domain is to be populated by the results of the query
     *
     */
    protected void populateDomainViaQuery(Rule rule, CycVariable cycVariable) throws IOException {
        CycList domainValuesCycList =
            CycAccess.current().askWithVariable (rule.getFormula(),
                                                 cycVariable,
                                                 constraintProblem.mt);
        ArrayList domainValues = new ArrayList();
        domainValues.addAll(domainValuesCycList);

        if (constraintProblem.backchainer.maxBackchainDepth >
            constraintProblem.backchainer.backchainDepth) {
            if (verbosity > 3)
                System.out.println("maxBackchainDepth " +
                                   constraintProblem.backchainer.maxBackchainDepth +
                                   " > " + constraintProblem.backchainer.backchainDepth +
                                   "\n  for rule\n" + rule);
            ArrayList backchainDomainValues = constraintProblem.backchainer.backchain(rule);
            if (verbosity > 3)
                System.out.println("Adding backchain domain values " + backchainDomainValues +
                                   "\n  for " + cycVariable);
        }
        constraintProblem.valueDomains.varsDictionary.put(cycVariable, domainValues);
    }

    /**
     * Initializes the value domains for each variable.
     */
    public void initializeDomains() throws IOException {
        for (int i = 0; i < domainPopulationRules.size(); i++) {
            Rule rule = (Rule) domainPopulationRules.get(i);
            if (rule.isExtensionalVariableDomainPopulatingRule()) {
                CycVariable cycVariable = (CycVariable) rule.getVariables().get(0);
                if (valueDomains.domains.containsKey(cycVariable))
                    throw new RuntimeException("Duplicate domain specifying rule for " +
                                               cycVariable);
                valueDomains.domains.put(cycVariable, null);
                if (valueDomains.varsDictionary.containsKey(cycVariable))
                    throw new RuntimeException("Duplicate varsDictionary entry for " + cycVariable);
                CycList theSet =  (CycList) rule.getFormula().third();
                if (! (theSet.first().toString().equals("TheSet")))
                    throw new RuntimeException("Invalid TheSet entry for " + cycVariable);
                ArrayList domainValues = new ArrayList(theSet.rest());
                valueDomains.varsDictionary.put(cycVariable, domainValues);
            }
            else if (rule.getArity() == 1) {
                CycVariable cycVariable = (CycVariable) rule.getVariables().get(0);
                populateDomainViaQuery(rule, cycVariable);
            }
            else {

                //TODO handle domain population for arity > 1

            }
        }
        if (verbosity > 1)
            valueDomains.displayVariablesAndDomains();
    }

}