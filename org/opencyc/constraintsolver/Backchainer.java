package org.opencyc.constraintsolver;

import java.util.*;
import org.opencyc.cycobject.*;

/**
 * Provides additional constraint rules through backwards KB inference using the input constraint
 * rules as a starting point.  Only domain populating rules can be supplemented via backchaining,
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
     * Maximum depth of backchaining from an input constraint rule.
     */
    protected int maxBackchainDepth = 6;

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
     * Performs backchaining inference to augment the input constraint domain-populating
     * constraint rule set.
     *
     * @param domainPopulationRules collection of the rules which populate variable domains
     * @return the augmented input constraint domain-populating constraint rule set
     */
    public ArrayList backchain(ArrayList domainPopulationRules) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < domainPopulationRules.size(); i++) {
            Rule domainPopulationRule = (Rule) domainPopulationRules.get(i);
            result.addAll(backchain(domainPopulationRule));
        }
        return result;
    }

    /**
     * Returns the implication rules which can prove the given rule.
     *
     * @param rule a rule is to be proven via backchaining
     * @return the implication rules which can prove the given rule
     */
    public ArrayList backchain(Rule rule) {
        ArrayList result = new ArrayList();
        int backchainDepth = rule.getBackchainDepth();
        if (backchainDepth >= this.maxBackchainDepth) {
            if (verbosity > 3)
                System.out.println("backchaining limit reached for\n" + rule);
            return result;
        }
        int newBackchainDepth = backchainDepth + 1;
        if (verbosity > 3)
            System.out.println("backchaining on\n" + rule);

        ArrayList candidateImplicationRules = gatherRulesConcluding(rule);

        // TODO implement rule filtering


        return result;
    }

    /**
     * Gathers the implication rules which conclude the given rule.
     *
     * @param rule the rule to be proven via backchaining
     * @return the implication rules which coonclde the given rule
     */
    public ArrayList gatherRulesConcluding(Rule rule) {
        ArrayList result = new ArrayList();

        //TODO implement

        return result;
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