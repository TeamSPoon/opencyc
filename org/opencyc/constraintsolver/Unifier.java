package org.opencyc.constraintsolver;

import java.util.*;
import java.io.*;
import org.opencyc.cycobject.*;

/**
 * Provides attribute and behavior for a formula unifier, used by the constraint solver
 * during backchaining.<p>
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
public class Unifier {

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = 9;

    /**
     * Reference to the collection of additional argument type constraint rules.
     */
    ArrayList argumentTypeConstraintRules;

    /**
     * Reference to the parent <tt>Backchainer</tt> object.
     */
    protected Backchainer backchainer;

    /**
     * Constructs a new <tt>Unifier</tt> object given the parent
     * <tt>Backchainer</tt> object.
     *
     * @param backchainer the parent <tt>Backchainer</tt> ojbect
     */
    public Unifier(Backchainer backchainer) {
        this.backchainer = backchainer;
    }

    /**
     * Unifies a constraint rule with the consequent of a horn clause and returns a list of
     * the antecedants with the required variable renamings and substitutions, or returns
     * <tt>null</tt> if no unification is possible.
     *
     * @param rule the constraint rule which is the subject of the backchaining operation
     * @param hornClause the horn clause whose consequent is to be unified with the constraint
     * rule
     * @return an <tt>ArrayList</tt> of the antecedants with the required variable renamings and
     * substitutions if unification succeeds otherwise return <tt>null</tt>
     */
    public ArrayList unify(Rule rule, HornClause hornClause) throws IOException {
        if (verbosity > 3)
            System.out.println("Attempting to unify \n" + rule + "\n" + hornClause);
        if (! (rule.getPredicate().equals(hornClause.getConsequent().getPredicate()))) {
            if (verbosity > 3)
                System.out.println("predicates not equal \n" + rule.getPredicate() +
                " " + hornClause.getConsequent().getPredicate());
            return null;
        }
        // Clone a new horn clause having no variables in common with those variable which
        // are already included in the constraint problem.
        HornClause unifiedHornClause = (HornClause) hornClause.clone();
        hornClause.renameVariables(backchainer.constraintProblem.variables, verbosity);

        // Visit each corresponding argument position in the rule and in the horn clause
        // consequent.
        CycList consequentArguments = unifiedHornClause.getConsequent().getArguments();
        CycList ruleArguments = rule.getArguments();

        for (int i = 0; i < ruleArguments.size(); i++) {
            Object ruleArgument = ruleArguments.get(i);
            Object consequentArgument = consequentArguments.get(i);
            if (ruleArgument instanceof CycVariable) {
                if (consequentArgument instanceof CycVariable) {
                    // Unify a rule variable.
                    unifiedHornClause.substituteVariable((CycVariable) consequentArgument,
                                                         ruleArgument,
                                                         verbosity);
                if (verbosity > 3)
                    System.out.println("at argument position " + (i + 1) +
                                       ". " + ((CycVariable) ruleArgument).cyclify() +
                                       " substituted for " +
                                       ((CycVariable) consequentArgument).cyclify());
                }
                else {
                    if (verbosity > 3)
                        System.out.println("at argument position " + (i + 1) +
                                           ", " + ruleArgument + " is a variable " +
                                           " but " + consequentArgument + " is not a variable");
                    return null;
                }

            }
            else if (consequentArgument instanceof CycVariable) {
                // Unify a horn clause consequent variable with a rule term.
                if (unifiedHornClause.substituteVariable((CycVariable) consequentArgument,
                                                         ruleArgument,
                                                         verbosity)) {
                    if (verbosity > 3)
                        System.out.println("at argument position " + (i + 1) +
                                           ". " + ruleArgument + " substituted for " +
                                           ((CycVariable) consequentArgument).cyclify());
                }
                else {
                    if (verbosity > 3)
                        System.out.println("  unification abandoned because formula not wff");
                    return null;
                }
            }
            else if (! (ruleArgument.equals(consequentArgument))) {
                // Otherwise respective terms in the rule and horn clause consequent must be
                // equal for unification to succeed.
                if (verbosity > 3)
                    System.out.println("at argument position " + (i + 1) +
                                       ", " + ruleArgument + " does not equal " +
                                       consequentArgument);
                return null;
            }
        }
        return unifiedHornClause.getAntecedantConjuncts();
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
}