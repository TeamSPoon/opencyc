package org.opencyc.queryprocessor;

import java.util.*;
import java.io.*;
import org.apache.commons.collections.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;
import org.opencyc.inferencesupport.*;

/**
 * <tt>HashJoiner</tt> object to model the attributes and behavior of a query
 * processor using the hash join algorithm.<p>
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
 * @see UnitTest#testHashJoiner
 */
public class HashJoiner {

    /**
     * The binding sets remaining to be joined.
     */
    ArrayList remainingBindingSets;

    /**
     * The build binding set from which a hash map is built.
     */
    BindingSet buildBindingSet;

    /**
     * The probe binding set which is iterated over against the hash map created from the build binding set.
     */
    BindingSet probeBindingSet;

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Constructs a new <tt>HashJoiner</tt> object.
     */
    public HashJoiner() {
    }

    /**
     * Joins the given binding sets into a single binding set.
     *
     * @param bindingSets the list of binding sets to be joined
     * @return the binding set which is the result of the join operation
     */
    public BindingSet join(ArrayList bindingSets) throws IOException {
        remainingBindingSets = (ArrayList) bindingSets.clone();
        if (bindingSets.size() == 0)
            throw new RuntimeException("Invalid binding sets size " + bindingSets.size());
        Collections.sort(remainingBindingSets);
        BindingSet joinedBindingSet = (BindingSet) remainingBindingSets.get(0);
        remainingBindingSets.remove(0);
        if (verbosity > 3)
            System.out.println("Starting binding set for join is \n" + joinedBindingSet +
                               " " + joinedBindingSet.getNbrInstances() + " instances");
        while (remainingBindingSets.size() > 0) {
            // In case there are no variables in common, choose the lowest cardinality binding set candidate.
            int index = 0;
            for (int i = 1; i < remainingBindingSets.size(); i++) {
        if (verbosity > 3)
            System.out.println("Starting binding set for join is \n" + joinedBindingSet +
                               " " + joinedBindingSet.getNbrInstances() + " instances");
                BindingSet remainingBindingSet = (BindingSet) remainingBindingSets.get(i);
                ArrayList joinedBindingSetVariables = joinedBindingSet.getVariables();
                ArrayList remainingBindingSetVariables = remainingBindingSet.getVariables();
                if (verbosity > 3)
                    System.out.println("  candidate binding set \n  " + remainingBindingSet +
                                       " " + remainingBindingSet.getNbrInstances() + " instances");
                if (OcCollectionUtils.hasIntersection(joinedBindingSetVariables,
                                                      remainingBindingSetVariables)) {
                    index = i;
                    break;
                }
            }
            BindingSet bestBindingSetForJoin = (BindingSet) remainingBindingSets.get(index);
            remainingBindingSets.remove(index);
            if (verbosity > 3)
                System.out.println("  best binding set for join \n  " + bestBindingSetForJoin);
            joinedBindingSet = join(joinedBindingSet, bestBindingSetForJoin);
        }
        return joinedBindingSet;
    }

    /**
     * Joins two binding sets into a single binding set.  The larger of the two binding sets will be
     * used to probe the smaller one as hash match.
     *
     * @param bindingSet1 a binding set to be joined
     * @param bindingSet2 another binding set to be joined
     * @return the binding set which is the result of the join operation
     */
    public BindingSet join(BindingSet bindingSet1, BindingSet bindingSet2) {
        if (verbosity > 3)
            System.out.println("Joining \n  " + bindingSet1 + "\n  " + bindingSet2);
        if (bindingSet1.size() < bindingSet2.size())
            return joinBuildProbe(bindingSet1, bindingSet2);
        else
            return joinBuildProbe(bindingSet2, bindingSet1);
    }

    /**
     * Joins a probe binding set with a build binding set.
     *
     * @param bindingSet1 the binding set from which a hash table is built
     * @param bindingSet2 the binding set which is probed against the build binding set
     * @return the binding set which is the result of the join operation
     */
    public BindingSet joinBuildProbe(BindingSet buildBindingSet, BindingSet probeBindingSet) {
        this.buildBindingSet = buildBindingSet;
        this.probeBindingSet = probeBindingSet;
        if (verbosity > 3)
            System.out.println("Joining \n  build " + buildBindingSet + "\n  probe " + probeBindingSet);

        CycList joinedFormula = new CycList();
        joinedFormula.add(CycAccess.and);
        joinedFormula.add(buildBindingSet.getQueryLiteral().getFormula());
        joinedFormula.add(probeBindingSet.getQueryLiteral().getFormula());
        QueryLiteral joinedQueryLiteral = new QueryLiteral(joinedFormula);
        if (verbosity > 3)
            System.out.println("  joined query \n  " + joinedFormula.cyclify());

        BindingSet joinedBindingSet = new BindingSet(joinedQueryLiteral, buildBindingSet.getMt());
        ArrayList joiningVariables = new ArrayList(CollectionUtils.intersection(buildBindingSet.getVariables(),
                                                                                probeBindingSet.getVariables()));
        if (verbosity > 3)
            System.out.println("  joining variables " + joiningVariables);

        int joinedVariablesSize = joiningVariables.size();
        if (joinedVariablesSize == 0)
            throw new RuntimeException("product not yet implemented for \n" + buildBindingSet +
                                       "\n" + probeBindingSet);
        // Build step.
        HashMap hashMap = new HashMap();
        ArrayList buildVariables = buildBindingSet.getVariables();
        int buildVariablesSize = buildVariables.size();
        for (int i = 0; i < buildBindingSet.size(); i++) {
            CycList key = new CycList();
            CycList bindingValueList = (CycList) buildBindingSet.getBindingValues().get(i);
            for (int j = 0; j < joinedVariablesSize; j++) {
                for (int k = 0; k < buildVariablesSize; k++)
                if (joiningVariables.get(j).equals(buildVariables.get(k)))
                    key.add(bindingValueList.get(j));
            }
            hashMap.put(key, bindingValueList);
            if (verbosity > 3)
                System.out.println("  indexing " + bindingValueList.cyclify() + "\n  key " + key.cyclify());
        }
        // Probe step.
        ArrayList probeVariables = probeBindingSet.getVariables();
        int probeVariablesSize = probeVariables.size();
        for (int i = 0; i < probeBindingSet.size(); i++) {
            CycList key = new CycList();
            CycList bindingValueList = (CycList) probeBindingSet.getBindingValues().get(i);
            for (int j = 0; j < joinedVariablesSize; j++) {
                for (int k = 0; k < probeVariablesSize; k++)
                if (joiningVariables.get(j).equals(probeVariables.get(k)))
                    key.add(bindingValueList.get(j));
            }
            boolean keyFound = hashMap.containsKey(key);
            if (verbosity > 3)
                System.out.println("  probing " + bindingValueList.cyclify() + "\n  key " + key.cyclify() + " --> " + keyFound);
        }

        return joinedBindingSet;
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