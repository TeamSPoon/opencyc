package org.opencyc.constraintsolver;

import java.util.*;
import java.io.IOException;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

/**
 * <tt>ProblemParser</tt> object to model the attributes and behavior of
 * a parser which inputs the constraint problem representation and sets up
 * the parent <tt>ConstraintProblem</tt> object.<p>
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
public class ProblemParser {
    /**
     * Reference to the parent <tt>ConstraintProblem</tt> object.
     */
    protected ConstraintProblem constraintProblem;

    /**
     * Reference to the parent list of simplified constraint rules.
     */
    ArrayList simplifiedRules;

    /**
     * Reference to the parent list of domain populating constraint rules.
     */
    ArrayList domainPopulationRules;

    /**
     * Reference to the parent list of constraint rules.
     */
    ArrayList constraintRules;

    /**
     * Reference to the constraint problem's ValueDomains object.
     */
    ValueDomains valueDomains;

    /**
     * Reference to the constraint problem's ArgumentTypeConstrainer object.
     */
    ArgumentTypeConstrainer argumentTypeConstrainer;

    /**
     * Reference to the constraint problem's HighCardinalityDomains object.
     */
    HighCardinalityDomains highCardinalityDomains;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = 9;

    /**
     * Constructs a new <tt>ProblemParser</tt> object for the parent
     * <tt>ConstraintProblem</tt>.
     *
     * @param constraintProblem the parent constraint problem
     */
    public ProblemParser(ConstraintProblem constraintProblem) {
        this.constraintProblem = constraintProblem;
        simplifiedRules = constraintProblem.simplifiedRules;
        domainPopulationRules = constraintProblem.domainPopulationRules;
        constraintRules = constraintProblem.constraintRules;
        argumentTypeConstrainer = constraintProblem.argumentTypeConstrainer;
        highCardinalityDomains = constraintProblem.highCardinalityDomains;
        valueDomains = constraintProblem.valueDomains;
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
     * Simplifies the input problem into its constituent <tt>Rule</tt> objects,
     * then divides the input rules into those which populate the variable
     * domains, and those which subsequently constrain the search for
     * one or more solutions.  Obtains additional argument type constraints for the constraint
     * rules.
     */
    public void extractRulesAndDomains() throws IOException {
        simplifiedRules = Rule.simplifyRuleExpression(constraintProblem.problem);
        for (int i = 0; i < simplifiedRules.size(); i++) {
            Rule rule = (Rule) simplifiedRules.get(i);
            if (rule.isVariableDomainPopulatingRule())
                placeDomainPopulatingRule(rule);
            else {
                placeConstraintRule(rule);
            }
        }
        if (verbosity > 1)
            constraintProblem.displayConstraintRules();
    }

    /**
     * Places the best domain populating rules into the the set of domainPopulationRules and puts
     * any other non-subsumed candidate domain populating rule into the set of problem constraint
     * rules.  The priority for determining the best domain populating rule is, from best to worst:
     * #$elementOf (accept all), #$genls (choose least cardinality), #$isa (choose least cardinality).
     *
     * @param candidateRules a list of candidate domain population rules
     */
    protected void placeDomainPopulatingRules(ArrayList candidateRules) throws IOException {
        for (int i = 0; i < candidateRules.size(); i++)
            placeDomainPopulatingRule((Rule) candidateRules.get(i));
    }

    /**
     * Places the best domain populating rule into the the set of domainPopulationRules and puts
     * any other non-subsumed candidate domain populating rule into the set of problem constraint
     * rules.
     *
     * @param candidateRule the given candidate domain population rules
     */
    protected void placeDomainPopulatingRule(Rule candidateRule) throws IOException {
        if (verbosity > 3)
            System.out.println("Considering \n" + candidateRule.cyclify() +
                               " as candidate domain populating rule");
        if (candidateRule.getArity() != 1)
            throw new RuntimeException("candidateRule does not have arity=1 " +
                                       candidateRule.cyclify());
        CycVariable variable = (CycVariable) candidateRule.getVariables().get(0);
        ArrayList tempDomainPopulationRules = (ArrayList) domainPopulationRules.clone();
        for (int i = 0; i < tempDomainPopulationRules.size(); i++) {
            Rule domainPopulationRule = (Rule) tempDomainPopulationRules.get(i);
            CycVariable domainPopulationVariable =
                (CycVariable) domainPopulationRule.getVariables().get(0);
            if (domainPopulationVariable.equals(variable)) {
                if (verbosity > 3)
                    System.out.println("Comparing to \n" + domainPopulationRule.cyclify());
                CycConstant domainPopulationPredicate = domainPopulationRule.getPredicate();
                CycConstant candidatePredicate = candidateRule.getPredicate();
                if (domainPopulationPredicate.equals(CycAccess.elementOf)) {
                    if (candidatePredicate.equals(CycAccess.elementOf))
                        // Add another #$elementOf domain population rule
                        placeAsDomainPopulationRule(candidateRule);
                    else
                        // Existing #$elementOf domain population rule takes
                        // priority over the non-#$elementOf candidate rule.
                        placeAsConstraintRule(candidateRule);
                    return;
                }
                if (candidatePredicate.equals(CycAccess.elementOf)) {
                    // Candidate #$elementOf rule takes priority over (and replaces) the
                    // existing non-#$elementOf domain population rule.
                    replaceDomainPopulationRule(domainPopulationRule, candidateRule);
                    return;
                }
                if (domainPopulationPredicate.equals(CycAccess.genls)) {
                    if (candidatePredicate.equals(CycAccess.genls)) {
                        CycConstant domainPopulationCollection =
                            (CycConstant) domainPopulationRule.getArguments().get(1);
                        CycConstant candidateCollection =
                            (CycConstant) candidateRule.getArguments().get(1);
                        if (CycAccess.current().isGenlOf(candidateCollection,
                                                         domainPopulationCollection))
                            // Existing #$genls constraint is more specific and takes
                            // priority over the candidate #$genls rule.
                            placeAsConstraintRule(candidateRule);
                        else
                            // Candidate #$genls rule is more specific - takes priority over
                            // (and replaces) the existing #$genls domain population rule.
                            replaceDomainPopulationRule(domainPopulationRule, candidateRule);
                    }
                    else
                        // Existing #$genls domain population rule takes
                        // priority over the non-#$genls candidate rule.
                        placeAsConstraintRule(candidateRule);
                    return;
                }
                if (domainPopulationPredicate.equals(CycAccess.isa)) {
                    if (candidatePredicate.equals(CycAccess.isa)) {
                        CycConstant domainPopulationCollection =
                            (CycConstant) domainPopulationRule.getArguments().get(1);
                        CycConstant candidateCollection =
                            (CycConstant) candidateRule.getArguments().get(1);
                        if (CycAccess.current().isGenlOf(candidateCollection,
                                                         domainPopulationCollection))
                            // Existing #$isa constraint is more specific and takes
                            // priority over the candidate #$isa rule.
                            placeAsConstraintRule(candidateRule);
                        else
                            // Candidate #$isa rule is more specific - takes priority over
                            // (and replaces) the existing #$isa domain population rule.
                            replaceDomainPopulationRule(domainPopulationRule, candidateRule);
                    }
                    else
                        // Existing #$genls domain population rule takes
                        // priority over the non-#$genls candidate rule.
                        placeAsConstraintRule(candidateRule);
                    return;
                }
                throw new RuntimeException("Could not place candidate domain populating rule " +
                                           candidateRule.cyclify());
            }
        }
        placeAsDomainPopulationRule(candidateRule);
    }

    /**
     * Places the candidate rule into the the set of domainPopulationRules
     *
     * @param candidateRule the given candidate domain population rule
     */
    protected void placeAsDomainPopulationRule(Rule candidateRule) {
        domainPopulationRules.add(candidateRule);
        if (verbosity > 3)
            System.out.println("  placed \n" + candidateRule.cyclify() +
                               " as domain population rule");
    }

    /**
     * Places the candidate rule into the the set of constraintRules. If the predicate is
     * neither #$isa nor #$genls, then gathers the type constraints of the predicate as
     * additional candidate domain population rules.
     *
     * @param candidateRule the given candidate domain population rule
     */
    protected void placeAsConstraintRule(Rule candidateRule) throws IOException {
        constraintRules.add(candidateRule);
        if (verbosity > 3)
            System.out.println("  placed \n" + candidateRule.cyclify() +
                               " as constraint rule");
        CycConstant predicate = candidateRule.getPredicate();
        if ((! (predicate.equals(CycAccess.isa))) &&
            (! (predicate.equals(CycAccess.genls)))) {
            ArrayList argConstraints =
                argumentTypeConstrainer.retrieveArgumentTypeConstraintRules(candidateRule);
            placeDomainPopulatingRules(argConstraints);
        }
    }

    /**
     * Replaces the given domainPopulatingRule with the candidate rule,
     * moving the domainPopulatingRule into the the set of constraintRules
     *
     * @param candidateRule the given candidate domain population rule
     */
    protected void replaceDomainPopulationRule(Rule domainPopulationRule, Rule candidateRule) {
        domainPopulationRules.remove(domainPopulationRule);
        constraintRules.add(domainPopulationRule);
        domainPopulationRules.add(candidateRule);
        if (verbosity > 3) {
            System.out.println("  replaced with \n" + candidateRule.cyclify() +
                               " as domain populating rule");
            System.out.println("  placed \n" + domainPopulationRule.cyclify() +
                               " as constraint rule");
        }
    }

    /**
     * Places the given constraint rule into the set of constraint rules if it does not subsume any
     * existing member of the constraint rule set.  Replaces an existing member of the constraint
     * rule set if the given rule is subsumed by the existing constraint rule.
     *
     * @param candidateRule the candidate constraint rule for placement into the set of constraint
     * rules
     */
    public void placeConstraintRule(Rule candidateRule) throws IOException {
        if (verbosity > 3)
            System.out.println("Considering \n" + candidateRule.cyclify() +
                               " as candidate constraint rule");
        ArrayList tempConstraintRules = (ArrayList) constraintRules.clone();
        for (int i = 0; i < tempConstraintRules.size(); i++) {
            Rule constraintRule = (Rule) tempConstraintRules.get(i);

            //TODO subsumption test

        }
        placeAsConstraintRule(candidateRule);
    }

    /**
     * Gathers the unique variables used in this constraint problem.
     */
    public void gatherVariables() {
        HashSet uniqueVariables = new HashSet();
        for (int i = 0; i < constraintProblem.simplifiedRules.size(); i++) {
            Rule rule = (Rule) constraintProblem.simplifiedRules.get(i);
            uniqueVariables.addAll(rule.getVariables());
        }
        constraintProblem.variables.addAll(uniqueVariables);
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
                CycList theSet =  (CycList) rule.getRule().third();
                if (! (theSet.first().toString().equals("TheSet")))
                    throw new RuntimeException("Invalid TheSet entry for " + cycVariable);
                ArrayList domainValues = new ArrayList(theSet.rest());
                valueDomains.varsDictionary.put(cycVariable, domainValues);
            }
            else if (rule.isIntensionalVariableDomainPopulatingRule()) {
                CycVariable cycVariable = (CycVariable) rule.getVariables().get(0);
                CycConstant collection = (CycConstant) rule.getArguments().second();
                int nbrInstances =
                    CycAccess.current().countAllInstances_Cached(collection,
                                                                 constraintProblem.mt);
                if (verbosity > 3) {
                    System.out.println("\nIntensional variable domain populating rule\n" + rule);
                    System.out.println("  nbrInstances " + nbrInstances);
                }
                if (nbrInstances > highCardinalityDomains.domainSizeThreshold) {
                    if (verbosity > 3)
                        System.out.println("  domain size " + nbrInstances +
                                           " exceeded high cardinality threshold of " +
                                           highCardinalityDomains.domainSizeThreshold);
                    highCardinalityDomains.setDomainSize(cycVariable,
                                                         new Integer(nbrInstances));
                    valueDomains.varsDictionary.put(cycVariable, new ArrayList());
                }
                else {
                    // Get the domain values by asking a query.
                    CycList domainValuesCycList =
                        CycAccess.current().askWithVariable (rule.getRule(),
                                                             cycVariable,
                                                             constraintProblem.mt);
                ArrayList domainValues = new ArrayList();
                domainValues.addAll(domainValuesCycList);
                valueDomains.varsDictionary.put(cycVariable, domainValues);
                }
            }
            else {
                if (verbosity > 1)
                    System.out.println("Unhandled domain population rule:\n" + rule);
            }
        }
        if (verbosity > 1)
            valueDomains.displayVariablesAndDomains();
    }

}