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
    protected ArrayList simplifiedRules;

    /**
     * Reference to the parent list of domain populating constraint rules.
     */
    protected ArrayList domainPopulationRules;

    /**
     * Reference to the parent list of constraint rules.
     */
    protected ArrayList constraintRules;

    /**
     * Reference to the constraint problem's ArgumentTypeConstrainer object.
     */
    protected ArgumentTypeConstrainer argumentTypeConstrainer;

    /**
     * Reference to the constraint problem's VariableDomainPopulator object.
     */
    protected VariableDomainPopulator variableDomainPopulator;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = ConstraintProblem.DEFAULT_VERBOSITY;

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
        variableDomainPopulator = constraintProblem.variableDomainPopulator;
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
     * rules.  If a ground fact discovered among the rule set is proven false, then immediately
     * return the value false.  If a rule has no instances, then immediately return the value false
     *
     * @return <tt>false</tt> if no further backchaining is possible and a rule cannot be satisfied,
     * otherwise return <tt>true</tt>
     */
    public boolean extractRulesAndDomains() throws IOException {
        simplifiedRules.addAll(Rule.simplifyRuleExpression(constraintProblem.problem));
        // Sort by ascending arity to find likely unsatisfiable facts first.
        Collections.sort(simplifiedRules);
        for (int i = 0; i < simplifiedRules.size(); i++) {
            Rule rule = (Rule) simplifiedRules.get(i);
            if ((constraintProblem.backchainer.backchainDepth ==
                 constraintProblem.backchainer.maxBackchainDepth) &&
                (! isRuleSatisfiable(rule)))
                return false;
            if (rule.isExtensionalVariableDomainPopulatingRule())
                // Extensional rules that explicitly define the value domain will rank best.
                rule.nbrFormulaInstances = 1;
            else
                rule.nbrFormulaInstances =
                    CycAccess.current().countUsingBestIndex(rule.formula, constraintProblem.mt);
            for (int j = 0; j < rule.getVariables().size(); j++) {
                VariablePopulationItem variablePopulationItem =
                    new VariablePopulationItem((CycVariable) rule.getVariables().get(j),
                                               rule);
                variableDomainPopulator.add(variablePopulationItem);
            }
        }
        variableDomainPopulator.populateDomains();

        System.exit(1);
        if (verbosity > 1)
            constraintProblem.displayConstraintRules();
        return true;
    }

    /**
     * Returns <tt>true</tt> iff the rule cannot be satisfied.
     *
     * @param rule the rule to check in the KB
     * @return <tt>true</tt> iff the rule cannot be satisfied
     */
    protected boolean isRuleSatisfiable(Rule rule) throws IOException {
        if (rule.isGround()) {
            if (verbosity > 3)
                System.out.println("Ground fact with no backchaining possible\n" + rule);
            boolean isTrueFact;
            if (rule.isEvaluatable())
                isTrueFact = Rule.evaluateConstraintRule(rule.getFormula());
            else
                isTrueFact = CycAccess.current().isQueryTrue(rule.getFormula(), constraintProblem.mt);
            if (verbosity > 3)
                System.out.println("  --> " + isTrueFact);
            if (! isTrueFact)
                return false;
        }
        CycConstant term = null;
        Integer argumentPostion = null;
        for (int j = 0; j < rule.getArguments().size(); j++) {
            Object argument = rule.getArguments().get(j);
            if (argument instanceof CycConstant) {
                term = (CycConstant) argument;
                argumentPostion = new Integer(j + 1);
                break;
            }
        }
        if (term != null) {
            boolean someInstancesExist =
                CycAccess.current().hasSomePredicateUsingTerm(rule.getPredicate(),
                                                              term,
                                                              argumentPostion,
                                                              constraintProblem.mt);
            if (! someInstancesExist) {
                if (verbosity > 3)
                    System.out.println("No instances exist and with no backchaining possible\n" + rule);
                return false;
            }
        }
        return true;
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
            System.out.println("\nConsidering \n" + candidateRule.cyclify() +
                               " as candidate domain populating rule");
        candidateRule.nbrFormulaInstances =
            CycAccess.current().countUsingBestIndex(candidateRule.formula, constraintProblem.mt);

        // TODO loop for variables, handle no variable case.
        // Each rule can populate contained varables, if the number of instances is lower
        // than the previous rule for that variable.


        CycVariable variable = (CycVariable) candidateRule.getVariables().get(0);
        ArrayList tempDomainPopulationRules = (ArrayList) domainPopulationRules.clone();
        for (int i = 0; i < tempDomainPopulationRules.size(); i++) {
            Rule domainPopulationRule = (Rule) tempDomainPopulationRules.get(i);
            CycVariable domainPopulationVariable =
                (CycVariable) domainPopulationRule.getVariables().get(0);
            if (variable.equals(domainPopulationVariable)) {
                if (verbosity > 3)
                    System.out.println("Comparing to \n" + domainPopulationRule.cyclify());
                CycConstant domainPopulationPredicate = domainPopulationRule.getPredicate();
                CycConstant candidatePredicate = candidateRule.getPredicate();
                if (domainPopulationPredicate.equals(CycAccess.elementOf)) {
                    if (candidatePredicate.equals(CycAccess.elementOf))
                        // Add another #$elementOf domain population rule
                        placeAsDomainPopulationRule(candidateRule);
                    else {
                        // Existing #$elementOf domain population rule takes
                        // priority over the non-#$elementOf candidate rule.
                        placeConstraintRule(candidateRule);
                    }
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
                            placeConstraintRule(candidateRule);
                        else
                            // Candidate #$genls rule is more specific - takes priority over
                            // (and replaces) the existing #$genls domain population rule.
                            replaceDomainPopulationRule(domainPopulationRule, candidateRule);
                    }
                    else {
                        // Existing #$genls domain population rule takes
                        // priority over the non-#$genls candidate rule.
                        placeConstraintRule(candidateRule);
                    }
                    return;
                }
                if (domainPopulationPredicate.equals(CycAccess.isa) &&
                    (! (domainPopulationRule.getArguments().get(1) instanceof CycVariable))) {
                    if (candidatePredicate.equals(CycAccess.isa)) {
                        Object object = domainPopulationRule.getArguments().get(1);
                        CycFort domainPopulationCollection;
                        if (object instanceof CycList)
                            domainPopulationCollection = new CycNart((CycList) object);
                        else
                            domainPopulationCollection = (CycFort) object;
                        object = candidateRule.getArguments().get(1);
                        CycFort candidateCollection;
                        if (object instanceof CycList)
                            candidateCollection = new CycNart((CycList) object);
                        else
                            candidateCollection = (CycFort) object;
                        if (CycAccess.current().isGenlOf(candidateCollection,
                                                         domainPopulationCollection))
                            // Existing #$isa constraint is more specific and takes
                            // priority over the candidate #$isa rule.
                            placeConstraintRule(candidateRule);
                        else
                            // Candidate #$isa rule is more specific - takes priority over
                            // (and replaces) the existing #$isa domain population rule.
                            replaceDomainPopulationRule(domainPopulationRule, candidateRule);
                    }
                    else {
                        // Existing #$isa domain population rule takes
                        // priority over the non-#$isa candidate rule.
                        placeConstraintRule(candidateRule);
                    }
                    return;
                }
                // Existing constraint is not proveably more general.
                placeConstraintRule(candidateRule);
                return;
            }
        }
        // No existing rules for comparison.
        placeAsDomainPopulationRule(candidateRule);
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
            System.out.println("\nConsidering \n" + candidateRule.cyclify() +
                               " as candidate constraint rule");
        if (candidateRule.getArity() == 1 &&
            candidateRule.getPredicate().equals(CycAccess.isa) &&
            candidateRule.getArguments().get(1).equals(CycAccess.thing)) {
            if (verbosity > 3)
                System.out.println("  dropped because rule is trivially true");
            return;
        }
        ArrayList tempConstraintRules = (ArrayList) constraintRules.clone();
        for (int i = 0; i < tempConstraintRules.size(); i++) {
            Rule constraintRule = (Rule) tempConstraintRules.get(i);
            if (candidateRule.isSubsumedBy(constraintRule)) {
                if (verbosity > 3)
                    System.out.println(candidateRule.cyclify() + "\n  is subsumed by constraint rule\n" +
                                       constraintRule.cyclify());
                this.replaceConstraintRule(constraintRule, candidateRule);
                return;
            }
        }
        ArrayList tempDomainPopulationRules = (ArrayList) domainPopulationRules.clone();
        for (int i = 0; i < tempDomainPopulationRules.size(); i++) {
            Rule domainPopulationRule = (Rule) tempDomainPopulationRules.get(i);
            if (candidateRule.subsumes(domainPopulationRule)) {
                if (verbosity > 3)
                    System.out.println(candidateRule.cyclify() + "\n  subsumes domain population rule\n" +
                                       domainPopulationRule.cyclify() + "\n  and is is dropped");
                return;
            }
        }
        placeAsConstraintRule(candidateRule);
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
     * @param domainPopulationRule the domain population rule which is to be replaced
     * @param candidateRule the given candidate domain population rule
     */
    protected void replaceDomainPopulationRule(Rule domainPopulationRule,
                                               Rule candidateRule) throws IOException {
        domainPopulationRules.remove(domainPopulationRule);
        domainPopulationRules.add(candidateRule);
        placeConstraintRule(domainPopulationRule);
        if (verbosity > 3) {
            System.out.println(domainPopulationRule.cyclify() + "\n  replaced with \n" +
                               candidateRule.cyclify() +
                               " as domain populating rule");
            System.out.println("  placed \n" + domainPopulationRule.cyclify() +
                               " as constraint rule");
        }
    }

    /**
     * Replaces the given constraintRule with the candidate rule.
     *
     * @param constraintRule the constraint rule which is to be replaced
     * @param candidateRule the given candidate constraint rule
     */
    protected void replaceConstraintRule(Rule constraintRule,
                                         Rule candidateRule) throws IOException {
        constraintRules.remove(constraintRule);
        placeConstraintRule(candidateRule);
        if (verbosity > 3) {
            System.out.println(constraintRule.cyclify() + "\n  replaced with \n" +
                               candidateRule.cyclify() +
                               " as constraint rule");
        }
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


}