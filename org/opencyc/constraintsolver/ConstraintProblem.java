package org.opencyc.constraintsolver;

import java.util.*;
import java.io.IOException;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

/**
 * <tt>Rule</tt> object to model the attributes and behavior of a constraint problem.<p>
 * A <tt>ProblemParser</tt> object is created to parse the input constraint problem
 * representation.<br>
 * A <tt>ValueDomains</tt> object is created to model the variables and their value domains.<br>
 * A <tt>HighCardinalityDomains</tt> object is created to model variables whose value domain
 * cardinality exceeds a threshold for special case processing.
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
public class ConstraintProblem {

    /**
     * CycAccess object which provides the OpenCyc api.
     */
    CycAccess cycAccess;

    /**
     * <tt>ValueDomains</tt> object for this <tt>ConstraintProblem</tt>.
     */
    protected ValueDomains valueDomains = new ValueDomains(this);

    /**
     * High cardinality domains for this <tt>ConstraintProblem</tt>.
     */
    protected HighCardinalityDomains highCardinalityDomains = new HighCardinalityDomains();

    /**
     * <tt>NodeConsistencyAchiever</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected NodeConsistencyAchiever nodeConsistencyAchiever = new NodeConsistencyAchiever(this);

    /**
     * <tt>RuleEvaluator</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected RuleEvaluator ruleEvaluator = new RuleEvaluator(this);

    /**
     * <tt>ArgumentTypeConstrainer</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected ArgumentTypeConstrainer argumentTypeConstrainer = new ArgumentTypeConstrainer(this);

    /**
     * <tt>Backchainer</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected Backchainer backchainer = new Backchainer(this);

    /**
     * <tt>Solution</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected Solution solution = new Solution(this);

    /**
     * <tt>ForwardCheckingSearcher</tt> for this <tt>ConstraintProblem</tt>.
     */
    protected ForwardCheckingSearcher forwardCheckingSearcher;

    /**
     * The OpenCyc microtheory in which the constraint rules should be asked.
     */
    public CycConstant mt;

    /**
     * When <tt>true</tt> randomizes the order of the variables and domain values before
     * beginning the search for a solution.  Do this when tuning search heuristics to avoid
     * bias for a particular order of input.
     */
    public boolean randomizeInput = false;

    /**
     * The number of solutions requested.  When <tt>null</tt>, all solutions are sought.
     */
    public Integer nbrSolutionsRequested = new Integer(1);

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = 8;

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
     * <tt>ProblemParser</tt> object for this <tt>ConstraintProblem</tt>.
     */
    protected ProblemParser problemParser = new ProblemParser(this);

    /**
     * Collection of additional argument type constraint rules.
     */
    ArrayList argumentTypeConstraintRules = new ArrayList();

    /**
     * Collection of the constraint variables as <tt>CycVariable</tt> objects.
     */
    protected ArrayList variables = new ArrayList();

    /**
     * Number of KB asks performed during the search for solution(s).
     */
    protected int nbrAsks = 0;

    /**
     * Constructs a new <tt>ConstraintProblem</tt> object.
     */
    public ConstraintProblem() {
        try {
            cycAccess = new CycAccess();
            mt = cycAccess.makeCycConstant("EverythingPSC");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot access OpenCyc server " + e.getMessage());
            System.exit(1);
        }
        ruleEvaluator.initialize();
    }

    /**
     * Set the value of the variable value domain size beyond which the initial values
     * are not all fetched from the KB using #$isa, rather some other more specific
     * constraint rule populates the variable domain as needed.
     *
     * @param domainSizeThreshold an <tt>int</tt> which is the new threshold.
     */
    public void setDomainSizeThreshold(int domainSizeThreshold) {
        highCardinalityDomains.domainSizeThreshold = domainSizeThreshold;
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        problemParser.setVerbosity(verbosity);
        valueDomains.setVerbosity(verbosity);
        highCardinalityDomains.setVerbosity(verbosity);
        argumentTypeConstrainer.setVerbosity(verbosity);
        backchainer.setVerbosity(verbosity);
        nodeConsistencyAchiever.setVerbosity(verbosity);
        ruleEvaluator.setVerbosity(verbosity);
        if (forwardCheckingSearcher != null)
            forwardCheckingSearcher.setVerbosity(verbosity);
        solution.setVerbosity(verbosity);
    }

    /**
     * Solves a constraint problem and return a list of solutions if one or more
     * was found, otherwise returns <tt>null</tt>.
     *
     * @param problem a constraint problem in the form of an OpenCyc query string
     * @return an <tt>ArrayList</tt> of solutions or <tt>null</tt> if no solutions were
     * found.  Each solution is an <tt>ArrayList</tt> of variable binding <tt>ArrayList</tt>
     * objects, each binding having the form of an <tt>ArrayList</tt> where the first
     * element is the <tt>CycVariable</tt> and the second element is the domain value
     * <tt>Object</tt>.
     */
    public ArrayList solve(String problemString) {
        return solve(cycAccess.makeCycList(problemString));
    }

    /**
     * Solves a constraint problem and return a list of solutions if one or more
     * was found, otherwise returns <tt>null</tt>.
     *
     * @param problem a constraint problem in the form of an OpenCyc query <tt>CycList</tt>
     * @return an <tt>ArrayList</tt> of solutions or <tt>null</tt> if no solutions were
     * found.  Each solution is an <tt>ArrayList</tt> of variable binding <tt>ArrayList</tt>
     * objects, each binding having the form of an <tt>ArrayList</tt> where the first
     * element is the <tt>CycVariable</tt> and the second element is the domain value
     * <tt>Object</tt>.
     */
    public ArrayList solve(CycList problem) {
        long startMilliseconds = System.currentTimeMillis();
        this.problem = problem;
        try {
            problemParser.extractRulesAndDomains();
            problemParser.gatherVariables();
            problemParser.initializeDomains();
            nodeConsistencyAchiever.applyUnaryRulesAndPropagate();
            valueDomains.initializeDomainValueMarking();
            if (verbosity > 0) {
                if (nbrSolutionsRequested == null)
                    System.out.println("Solving for all solutions");
                else if (nbrSolutionsRequested.intValue() == 1)
                    System.out.println("Solving for the first solution");
                else
                    System.out.println("Solving for " + nbrSolutionsRequested + " solutions");
            }
            forwardCheckingSearcher  = new ForwardCheckingSearcher(this);
            forwardCheckingSearcher.search(variables, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error accessing OpenCyc " + e.getMessage());
            System.exit(1);
        }
        long endMilliseconds = System.currentTimeMillis();
        if (verbosity > 0)
            System.out.println((endMilliseconds - startMilliseconds) + " milliseconds");

        return solution.solutions;
    }

   /**
     * Returns the number of variable domain populating <tt>Rule</tt>
     * objects derived from the input problem.
     *
     * @return the number of variable domain populating <tt>Rule</tt> objects.
     */
    public int getNbrDomainPopulationRules() {
        return domainPopulationRules.size();
    }

    /**
     * Returns the number of constraint <tt>Rule</tt> objects derived from
     * the input problem.
     *
     * @return the number of constraint <tt>Rule</tt> objects.
     */
    public int getNbrConstraintRules() {
        return constraintRules.size();
    }

    /**
     * Returns the number of <tt>Variable</tt> objects derived from
     * the input problem.
     *
     * @return the number of <tt>CycVariable</tt> objects.
     */
    public int getNbrVariables() {
        return variables.size();
    }

    /**
     * Displays the input constraint rules.
     */
    public void displayConstraintRules() {
        System.out.println("Domain Population Rules\n");
        for (int i = 0; i < domainPopulationRules.size(); i++)
            System.out.println("  " + domainPopulationRules.get(i));

        System.out.println("\nConstraint Rules\n");
        for (int i = 0; i < constraintRules.size(); i++)
            System.out.println("  " + constraintRules.get(i));
    }

    /**
     * Displays the variables and their value domains.
     */
    public void displayVariablesAndDomains() {
        valueDomains.displayVariablesAndDomains();
    }
}