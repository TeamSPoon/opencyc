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