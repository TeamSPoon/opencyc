package org.opencyc.constraintsolver;

import java.util.*;
import java.io.*;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

/**
 * Provides additional constraint rules through backwards KB inference using the input constraint
 * rules as a starting point.  Domain populating rules can be supplemented via backchaining,
 * because the answer to the query involves a search for bindings in the KB which are provided by
 * the domain populating rules.  Ordinary (non domain populating) rules on the other hand are not
 * supplemented by backchaining because they serve to restrict the domain values, as opposed to
 * populating them.<p>
 *
 * The <tt>Backchainer</tt> provides the additional constraint rules as a preparation step
 * before beginning the forward checking search for permitted bindings.
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
 */
public class Backchainer {

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = 9;

    /**
     * Reference to the parent <tt>ConstraintProblem</tt> object.
     */
    protected ConstraintProblem constraintProblem;

    /**
     * <tt>Unifier</tt> for this <tt>Backchainer</tt>.
     */
    protected Unifier unifier = new Unifier(this);

    /**
     * current depth of backchaining from an input constraint rule.
     */
    protected int backchainDepth = 0;

    /**
     * Maximum depth of backchaining from an input constraint rule.
     */
    protected int maxBackchainDepth = 0;

    /**
     * Indicates whether to backchain on predicates #$isa and #$genl
     */
    protected boolean sbhlBackchain = false;

    /**
     * Constructs a new <tt>Backchainer</tt> object given the parent <tt>ConstraintProblem</tt>
     * object.
     *
     * @param constraintProblem the parent constraint problem
     */
    public Backchainer(ConstraintProblem constraintProblem) {
        this.constraintProblem = constraintProblem;
    }

    /**
     * Solves a recursive constraint problem to obtain additional bindings for the variable in the given
     * unary rule, via backchaining on the unary rule (which might be a partially instantiated form of
     * a higher arity rule).
     *
     * @param rule the unary rule for which additional bindings are sought via backchaining
     * @return the <tt>ArrayList</tt> of values found
     */
    public ArrayList backchain(Rule rule) throws IOException {
        ArrayList values = new ArrayList();
        if (rule.getVariables().size() != 1)
            throw new RuntimeException("Attempt to backchain on non-unary rule " + rule);
        CycVariable variable = (CycVariable) rule.getVariables().get(0);
        if (verbosity > 1)
            System.out.println("Backchaining at depth " + backchainDepth + " on\n  " + rule +
                               "\n  to get bindings for " + variable);

        ArrayList backchainRules = getBackchainRules(rule);
        for (int i = 0; i < backchainRules.size(); i++) {
            Rule backchainRule = (Rule) backchainRules.get(i);
            if (verbosity > 1)
                System.out.println("\nRecursive constraint problem to solve\n  " + backchainRule);
            // Reuse the existing <tt>CycAccess</tt> object for the new constraint problem.
            ConstraintProblem backchainProblem =
                new ConstraintProblem(constraintProblem.cycAccess);
            backchainProblem.setVerbosity(verbosity);
            //backchainProblem.setVerbosity(9);
            // Request all solutions.
            backchainProblem.nbrSolutionsRequested = null;
            backchainProblem.mt = constraintProblem.mt;
            // Keep the same limit on maximum backchain depth
            backchainProblem.setMaxBackchainDepth(this.maxBackchainDepth);
            // Increment the depth of backchaining.
            backchainProblem.backchainer.backchainDepth = this.backchainDepth + 1;
            ArrayList solutions = backchainProblem.solve(backchainRule.rule);
            boolean solutionFound = false;
            for (int j = 0; j < solutions.size(); j++) {
                ArrayList solutionBindings = (ArrayList) solutions.get(j);
                if (verbosity > 3)
                    System.out.println("  Found bindings " + solutionBindings +
                                       "\n  for " + variable);
                for (int k = 0; k < solutionBindings.size(); k++) {
                    Binding binding = (Binding) solutionBindings.get(k);
                    if (binding.getCycVariable().equals(variable)) {
                        Object value = binding.getValue();
                        if (verbosity > 1)
                            System.out.println("  adding value " + value + " for " + variable);
                        values.add(value);
                        solutionFound = true;
                    }
                }
            }
            if (verbosity > 1) {
                if (! solutionFound)
                    System.out.println("  No bindings found for " + variable);
            }
        }
        return values;
    }

    /**
     * Performs backchaining inference to augment the input constraint domain-populating
     * constraint rule set.
     *
     * @param domainPopulationRules collection of the rules which populate variable domains
     * @return the augmented input constraint domain-populating constraint rule set
     */
    public ArrayList getBackchainRules(ArrayList domainPopulationRules) throws IOException {
        ArrayList result = new ArrayList();
        for (int i = 0; i < domainPopulationRules.size(); i++) {
            Rule domainPopulationRule = (Rule) domainPopulationRules.get(i);
            result.addAll(getBackchainRules(domainPopulationRule));
        }
        return result;
    }

    /**
     * Returns the sets of conjunctive antecentant rules which can prove the given rule.
     *
     * @param rule a rule is to be proven via backchaining
     * @return the sets of conjunctive antecentant rules which can prove the given rule
     */
    public ArrayList getBackchainRules(Rule rule) throws IOException {
        ArrayList result = new ArrayList();
        if (verbosity > 3)
            System.out.println("getting rules to conclude\n" + rule);
        ArrayList candidateImplicationRules = gatherRulesConcluding(rule);
        int nbrAcceptedRules = 0;
        int nbrCandidateRules = candidateImplicationRules.size();
        for (int i = 0; i < nbrCandidateRules; i++) {
            Rule candidateImplicationRule = (Rule) candidateImplicationRules.get(i);
            if (verbosity > 4)
                System.out.println("\nConsidering implication rule\n" + candidateImplicationRule.cyclify());
            HornClause hornClause = new HornClause(candidateImplicationRule);
            ArrayList antecedants = unifier.semanticallyUnify(rule, hornClause);
            if (antecedants != null) {
                if (verbosity > 4)
                    System.out.println("Unified antecedants\n" + antecedants);
                nbrAcceptedRules++;
                CycList conjunctiveAntecedantRule = new CycList();
                conjunctiveAntecedantRule.add(CycAccess.and);
                for (int j = 0; j < antecedants.size(); j++) {
                    Rule antecedant = (Rule) antecedants.get(j);
                    conjunctiveAntecedantRule.add(antecedant.getRule());
                }
                result.add(new Rule(conjunctiveAntecedantRule));
            }
        }
        if (verbosity > 1) {
            System.out.println("\nSummary of accepted backchain rules");
            for (int i = 0; i < result.size(); i++)
                System.out.println("  " + ((Rule) result.get(i)).cyclify());
            System.out.println("Accepted " + nbrAcceptedRules + " backchain rules from " +
                               nbrCandidateRules + " candidates");
        }
        return result;
    }

    /**
     * Gathers the implication rules which conclude the given rule.
     *
     * @param rule the rule to be proven via backchaining
     * @return the implication rules which conclude the given rule
     */
    public ArrayList gatherRulesConcluding(Rule rule) throws IOException {
        ArrayList result = new ArrayList();
        CycConstant predicate = rule.getPredicate();
        if (! this.sbhlBackchain &&
            (predicate.equals(CycAccess.isa) || predicate.equals(CycAccess.genls))) {
            if (verbosity > 3)
                System.out.println("backchain inference bypassed for predicate " + predicate);
            return result;
        }
        if (CycAccess.current().isBackchainForbidden(predicate,
                                                     constraintProblem.mt)) {
            if (verbosity > 3)
                System.out.println("backchain inference forbidden for predicate " + predicate);
            return result;
        }
        if (CycAccess.current().isBackchainDiscouraged(predicate,
                                                       constraintProblem.mt)) {
            if (verbosity > 3)
                System.out.println("backchain inference discouraged for predicate " + predicate);
            return result;
        }
        CycList backchainRules = CycAccess.current().getBackchainRules(predicate,
                                                                       constraintProblem.mt);
        for (int i = 0; i < backchainRules.size(); i++) {
            CycList cycListRule = (CycList) backchainRules.get(i);
            if (HornClause.isValidHornExpression(cycListRule)) {
                Rule backchainRule = new Rule(cycListRule);
                result.add(backchainRule);
            }
            else {
                if (verbosity > 3)
                    System.out.println("dropped ill-formed (backward) rule " + cycListRule.cyclify());
            }
        }
        CycList forwardChainRules = CycAccess.current().getForwardChainRules(rule.getPredicate(),
                                                                             constraintProblem.mt);
        for (int i = 0; i < forwardChainRules.size(); i++) {
            CycList cycListRule = (CycList) forwardChainRules.get(i);
            if (HornClause.isValidHornExpression(cycListRule)) {
                Rule forwardChainRule = new Rule(cycListRule);
                result.add(forwardChainRule);
            }
            else {
                if (verbosity > 3)
                    System.out.println("dropped ill-formed (forward) rule " + cycListRule.cyclify());
            }
        }
        return result;
    }

    /**
     * Sets whether backchaining is performed on rules with the predicate of #$isa or #$genls.  Large
     * numbers of rules conclude #$isa or #$genls, which are not usually relevant - so the default is
     * false.
     *
     * @param sbhlBackchain whether backchaining is performed on rules with the predicate of #$isa or #$genls
     */
    public void setSbhlBackchain(boolean sbhlBackchain) {
        this.sbhlBackchain = sbhlBackchain;
    }

    /**
     * Sets the maximum depth of backchaining from an input constraint rule. A value of zero indicates
     * no backchaining.
     *
     * @param maxBackchainDepth the maximum depth of backchaining, or zero if no backchaing on the input
     * constraint rules
     */
    public void setMaxBackchainDepth(int maxBackchainDepth) {
        this.maxBackchainDepth = maxBackchainDepth;
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        unifier.setVerbosity(verbosity);
    }
}