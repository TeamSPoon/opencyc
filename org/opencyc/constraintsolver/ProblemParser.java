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
     * one or more solutions.
     */
    public void extractRulesAndDomains() throws IOException {
        constraintProblem.simplifiedRules = Rule.simplifyRuleExpression(constraintProblem.problem);
        for (int i = 0; i < constraintProblem.simplifiedRules.size(); i++) {
            Rule rule = (Rule) constraintProblem.simplifiedRules.get(i);
            if (rule.isVariableDomainPopulatingRule())
                constraintProblem.domainPopulationRules.add(rule);
            else {
                constraintProblem.constraintRules.add(rule);
                ArrayList argConstraints = constraintProblem.argumentTypeConstrainer.retrieveArgumentTypeConstraintRules(rule);
                constraintProblem.domainPopulationRules.addAll(argConstraints);
            }
        }
        if (verbosity > 1)
            constraintProblem.displayConstraintRules();
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
        for (int i = 0; i < constraintProblem.domainPopulationRules.size(); i++) {
            Rule rule = (Rule) constraintProblem.domainPopulationRules.get(i);
            if (rule.isExtensionalVariableDomainPopulatingRule()) {
                CycVariable cycVariable = (CycVariable) rule.getVariables().get(0);
                if (constraintProblem.valueDomains.domains.containsKey(cycVariable))
                    throw new RuntimeException("Duplicate domain specifying rule for " + cycVariable);
                constraintProblem.valueDomains.domains.put(cycVariable, null);
                if (constraintProblem.valueDomains.varsDictionary.containsKey(cycVariable))
                    throw new RuntimeException("Duplicate varsDictionary entry for " + cycVariable);
                CycList theSet =  (CycList) rule.getRule().third();
                if (! (theSet.first().toString().equals("TheSet")))
                    throw new RuntimeException("Invalid TheSet entry for " + cycVariable);
                ArrayList domainValues = new ArrayList(theSet.rest());
                constraintProblem.valueDomains.varsDictionary.put(cycVariable, domainValues);
            }
            else if (rule.isIntensionalVariableDomainPopulatingRule()) {
                CycVariable cycVariable = (CycVariable) rule.getVariables().get(0);
                CycConstant collection = (CycConstant) rule.getArguments().second();
                int nbrInstances = CycAccess.current().countAllInstances(collection,
                                                                         constraintProblem.mt);
                if (verbosity > 3) {
                    System.out.println("\nIntensional variable domain populating rule\n" + rule);
                    System.out.println("  nbrInstances " + nbrInstances);
                }
                if (nbrInstances > constraintProblem.highCardinalityDomains.domainSizeThreshold) {
                    if (verbosity > 3)
                        System.out.println("  domain size " + nbrInstances +
                                           " exceeded high cardinality threshold of " +
                                           constraintProblem.highCardinalityDomains.domainSizeThreshold);
                    constraintProblem.highCardinalityDomains.setDomainSize(cycVariable,
                                                                           new Integer(nbrInstances));
                    constraintProblem.valueDomains.varsDictionary.put(cycVariable, new ArrayList());
                }
                else {
                    // Get the domain values by asking a query.
                    CycList domainValuesCycList =
                        CycAccess.current().askWithVariable (rule.getRule(),
                                                             cycVariable,
                                                             constraintProblem.mt);
                ArrayList domainValues = new ArrayList();
                domainValues.addAll(domainValuesCycList);
                constraintProblem.valueDomains.varsDictionary.put(cycVariable, domainValues);
                }
            }
            else {
                if (verbosity > 1)
                    System.out.println("Unhandled domain population rule:\n" + rule);
            }
        }
        if (verbosity > 1)
            constraintProblem.valueDomains.displayVariablesAndDomains();
    }


}