package org.opencyc.constraintsolver;

import org.opencyc.cycobject.*;
import java.util.*;

/**
 * The <tt>ForwardCheckingSearcher</tt> object to perform forward checking search for one or
 * more solutions to the <tt>ConstraintProblem</tt>.<p>
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 OpenCyc.org, license is open source GNU LGPL.
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
public class ForwardCheckingSearcher {

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = 8;

    /**
     * Reference to the parent <tt>ConstraintProblem</tt> object.
     */
    protected ConstraintProblem constraintProblem;

    /**
     * Constructs a new <tt>FowardCheckingSearcher</tt> object.
     *
     * @param constraintProblem the parent constraint problem
     */
    public ForwardCheckingSearcher(ConstraintProblem constraintProblem) {
        this.constraintProblem = constraintProblem;
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    protected void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Performs a depth-first search of the solution space, using forward checking
     * to prune alternatives.  Employs recursion to search subtrees.
     *
     * @param variables is the <tt>ArrayList</tt> of remaining variables to solve
     * @param level is the current depth of the search
     * @return <tt>true</tt> if a solution is found in this subtree.
     */
    protected boolean search(ArrayList variables, int level) {
        CycVariable selectedVariable = selectVariable(variables);
        ArrayList remainingDomain = constraintProblem.valueDomains.getUnmarkedDomainValues(selectedVariable);
        ArrayList remainingVariables = (ArrayList) variables.clone();
        remainingVariables.remove(selectedVariable);
        if (verbosity > 2) {
            System.out.println("\nSearching level   " + level);
            System.out.println("  variable         " + selectedVariable);
            System.out.println("  remaining domain " + remainingDomain);
            System.out.println("  remaining vars   " + remainingVariables);
        }
        // Iterate through the unmarked domain values, solving the subtree recursively,
        // backtracking when required.
        for (int i = 0; i < remainingDomain.size(); i++) {
            Object selectedValue = remainingDomain.get(i);
            //Solution object

        }
        // No solution down this branch.
        return false;
    }

    /**
     * From the list of variables, heuristically chooses the one most likely to
     * narrow the remaining search space.
     *
     * @param variables the <tt>ArrayList</tt> of variables from which the choice is made
     * @return the variable most likely to narrow the remaining search space
     */
    protected CycVariable selectVariable (ArrayList variables) {
        if (variables.size() == 1)
            return (CycVariable) variables.get(0);
        ArrayList annotatedVariables = new ArrayList();
        Integer remainingDomainSize = null;
        Integer degree = null;
        for (int i = 0; i < variables.size(); i++) {
            CycVariable variable = (CycVariable) variables.get(i);
            remainingDomainSize = new Integer(constraintProblem.valueDomains.getUnmarkedDomainSize(variable));
            degree = new Integer(constraintDegree(variable, variables));
            annotatedVariables.add(new VariableSelectionAttributes(variable,
                                                                   remainingDomainSize,
                                                                   degree));
        }
        Collections.sort(annotatedVariables);
        if (verbosity > 7) {
            System.out.println("\nHeuristic selection order");
            for (int i = 0; i < annotatedVariables.size(); i++) {
                System.out.println("  " + (VariableSelectionAttributes) annotatedVariables.get(i));
            }
        }
        return ((VariableSelectionAttributes) annotatedVariables.get(0)).cycVariable;
    }

    /**
     * Returns the number of constraint rules applicable to variable and one or more
     * of the other variables.
     *
     * @param variable the variable which must be used in the counted constraint rules
     * @param variables the counted constraint rules must use only these variables and no others.
     * @return the number of constraint rules applicable to variable and one or more
     * of the other variables
     */
    protected int constraintDegree(CycVariable variable, ArrayList variables) {
        int degree = 0;
        ArrayList ruleVariables = null;
        for (int i = 0; i < constraintProblem.constraintRules.size(); i++) {
            Rule rule = (Rule) constraintProblem.constraintRules.get(i);
            ruleVariables = rule.getVariables();
            if (ruleVariables.contains(variable) &&
                variables.containsAll(ruleVariables)) {
                degree++;
                if (verbosity > 8)
                    System.out.println("Rule " + rule + "\n  between " +
                                       variable + " and " + variables);
            }
        }
        if (verbosity > 8)
            System.out.println("Constraint degree for " + variable + " is " + degree);
        return degree;
    }











}