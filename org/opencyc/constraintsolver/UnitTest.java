package org.opencyc.constraintsolver;

import java.util.*;
import java.io.*;
import junit.framework.*;
import org.opencyc.cycobject.*;

/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.constraintsolver</tt> package.<p>
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
public class UnitTest extends TestCase {

    /**
     * Constructs a new UnitTest object.
     * @param name the test case name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Runs the unit tests
     */
    public static void runTests() {
        TestSuite testSuite = new TestSuite(UnitTest.class);
        //testSuite.addTest(new UnitTest("testHelloWorld"));
        //testSuite.addTest(new UnitTest("testRule"));
        //testSuite.addTest(new UnitTest("testHornClause"));
        //testSuite.addTest(new UnitTest("testBinding"));
        //testSuite.addTest(new UnitTest("testSolution"));
        //testSuite.addTest(new UnitTest("testRuleEvaluator"));
        //testSuite.addTest(new UnitTest("testConstraintProblem"));
        TestResult testResult = new TestResult();
        testSuite.run(testResult);
    }

    /**
     * Tests the test harness itself.
     */
    public void testHelloWorld() {
        System.out.println("** testHelloWorld **");
        Assert.assertTrue(true);
        System.out.println("** testHelloWorld OK **");
    }

    /**
     * Tests the <tt>RuleEvaluator</tt> class.
     */
    public void testRuleEvaluator() {
        System.out.println("** testRuleEvaluator **");

        RuleEvaluator ruleEvaluator = new RuleEvaluator(new ConstraintProblem());
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$numericallyEqual 1 1)")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$numericallyEqual 2 1)")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$different 2 1)")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$different 2 2)")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$different \"a\" \"b\")")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$different \"a\" \"a\")")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$not (#$different 1 1))")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$not (#$not (#$different 1 1)))")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 3))")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$and (#$numericallyEqual 1 2) (#$numericallyEqual 3 3))")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 4))")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 3) (#$numericallyEqual 4 4))")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$or (#$numericallyEqual 1 2) (#$numericallyEqual 3 3))")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$or (#$numericallyEqual 1 2) (#$numericallyEqual 3 4))")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$numericallyEqual 2 (#$PlusFn 1))")));
        Assert.assertTrue(ruleEvaluator.ask(new Rule("(#$numericallyEqual (#$PlusFn 1) 2)")));
        Assert.assertTrue(! ruleEvaluator.ask(new Rule("(#$numericallyEqual (#$PlusFn 1) 5)")));

        System.out.println("** testRuleEvaluator OK **");
    }

    /**
     * Tests the <tt>Binding</tt> class.
     */
    public void testBinding() {
        System.out.println("** testBinding **");

        Binding binding1 = new Binding(CycVariable.makeCycVariable("?x"), "abc");
        Assert.assertNotNull(binding1);
        Assert.assertEquals(CycVariable.makeCycVariable("?x"), binding1.getCycVariable());
        Assert.assertEquals("abc", binding1.getValue());
        Assert.assertEquals("x = \"abc\"", binding1.toString());

        System.out.println("** testBinding OK **");
    }

    /**
     * Tests the <tt>Rule</tt> class.
     */
    public void testRule() {
        System.out.println("** testRule **");

        // Construction
        String ruleAsString = "(#$isa ?x #$Cathedral)";
        Rule rule1 = new Rule (new CycList(ruleAsString));
        Assert.assertNotNull(rule1);
        Assert.assertNotNull(rule1.getRule());
        CycList cycList = rule1.getRule();
        Assert.assertEquals(ruleAsString, cycList.cyclify());
        Assert.assertEquals(ruleAsString, rule1.cyclify());

        // Equality
        Rule rule2 = new Rule (new CycList(ruleAsString));
        Assert.assertEquals(rule1.toString(), rule2.toString());
        Assert.assertEquals(rule1.cyclify(), rule2.cyclify());
        Assert.assertEquals(rule1, rule2);

        // gatherVariables and arity.
        Assert.assertEquals(1, rule1.getArity());
        Assert.assertTrue(rule1.getVariables().contains(CycVariable.makeCycVariable("?x")));

        // simplifyRuleExpression
        CycList ruleExpression = new CycList("(isa ?x Cathedral)");
        ArrayList rules = Rule.simplifyRuleExpression(ruleExpression);
        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        Assert.assertTrue(rules.get(0) instanceof Rule);
        Rule rule3 = (Rule) rules.get(0);
        Assert.assertEquals(ruleExpression.cyclify(), rule3.cyclify());

        // instantiate
        Rule rule4 = new Rule("(#$isa ?x #$Cathedral)");
        Rule rule5 = rule4.instantiate(CycVariable.makeCycVariable("?x"),
                                       CycConstant.makeCycConstant("#$NotreDame"));
        Assert.assertEquals("(#$isa #$NotreDame #$Cathedral)", rule5.cyclify());

        // isDifferent
        Rule rule6 = new Rule("(#$isa ?x #$Cathedral)");
        Assert.assertTrue(! rule6.isAllDifferent());
        Rule rule7 = new Rule("(#$different ?x ?y)");
        Assert.assertTrue(rule7.isAllDifferent());

        // isEvaluatable
        Rule rule8 = new Rule("(#$isa ?x #$Cathedral)");
        Assert.assertTrue(! rule8.isEvaluatable());
        Rule rule9 = new Rule("(#$numericallyEqual ?x 1)");
        Assert.assertTrue(rule9.isEvaluatable());
        Rule rule10 = new Rule("(#$and (#$isa ?x #$Cathedral) (#$numericallyEqual ?x 2))");
        Assert.assertTrue(! rule10.isEvaluatable());
        Rule rule11 = new Rule("(#$and (#$numericallyEqual 1 (#$PlusFn ?x)) (#$numericallyEqual ?x 2))");
        Assert.assertTrue(rule11.isEvaluatable());
        Rule rule12 = new Rule("(#$or (#$numericallyEqual 1 (#$PlusFn ?x)) (#$numericallyEqual ?x 2))");
        Assert.assertTrue(rule11.isEvaluatable());


        // evaluateConstraintRule
        CycList cycList13 = new CycList("(#$numericallyEqual 0 0)");
        Assert.assertTrue(Rule.evaluateConstraintRule(cycList13));
        CycList cycList14 = new CycList("(#$numericallyEqual 1 0)");
        Assert.assertTrue(! Rule.evaluateConstraintRule(cycList14));
        CycList cycList15 = new CycList("(#$numericallyEqual 0 1)");
        Assert.assertTrue(! Rule.evaluateConstraintRule(cycList15));
        CycList cycList16 = new CycList("(#$numericallyEqual (#$PlusFn 0) 1)");
        Assert.assertTrue(Rule.evaluateConstraintRule(cycList16));
        CycList cycList17 = new CycList("(#$numericallyEqual (#$PlusFn 3) 1)");
        Assert.assertTrue(! Rule.evaluateConstraintRule(cycList17));
        CycList cycList18 = new CycList("(#$or (#$numericallyEqual (#$PlusFn 3) 1) " +
                                        "      (#$numericallyEqual 4 (#$PlusFn 3)))");
        Assert.assertTrue(Rule.evaluateConstraintRule(cycList18));
        CycList cycList19 = new CycList("(#$or (#$numericallyEqual (#$PlusFn 3) 1) " +
                                        "      (#$numericallyEqual 4 (#$PlusFn 7)))");
        Assert.assertTrue(! Rule.evaluateConstraintRule(cycList19));
        CycList cycList20 = new CycList("(#$and (#$numericallyEqual (#$PlusFn 3) 4) " +
                                        "       (#$numericallyEqual 4 (#$PlusFn 3)))");
        Assert.assertTrue(Rule.evaluateConstraintRule(cycList20));
        CycList cycList21 = new CycList("(#$and (#$numericallyEqual (#$PlusFn 3) 1) " +
                                        "       (#$numericallyEqual 4 (#$PlusFn 7)))");
        Assert.assertTrue(! Rule.evaluateConstraintRule(cycList21));

        // substituteVariable
        Rule rule22 = new Rule("(#$isa ?x #$Cathedral)");
        rule22.substituteVariable(CycVariable.makeCycVariable("?x"),
                                  CycVariable.makeCycVariable("?cathedral"));
        Assert.assertEquals("(#$isa ?cathedral #$Cathedral)", rule22.cyclify());
        Rule rule23 = new Rule("(#$isa ?x #$Cathedral)");
        rule23.substituteVariable(CycVariable.makeCycVariable("?x"),
                                  CycConstant.makeCycConstant("NotreDameCathedral"));
        Assert.assertEquals("(#$isa #$NotreDameCathedral #$Cathedral)", rule23.cyclify());

        //Zebra Puzzle rules
        String zebraPuzzleString =
            "(#$and " +
            "  (#$or " +
            "    (#$numericallyEqual ?norwegian (#$PlusFn ?blue 1)) " +
            "    (#$numericallyEqual ?blue (#$PlusFn ?norwegian 1))) " +
            "  (#$numericallyEqual ?japanese ?volkswagen) " +
            "  (#$numericallyEqual ?mercedes-benz ?orange-juice) " +
            "  (#$or " +
            "    (#$numericallyEqual ?ford (#$PlusFn ?horse 1)) " +
            "    (#$numericallyEqual ?horse (#$PlusFn ?ford 1))) " +
            "  (#$or " +
            "    (#$numericallyEqual ?chevrolet (#$PlusFn ?fox 1)) " +
            "    (#$numericallyEqual ?fox (#$PlusFn ?chevrolet 1))) " +
            "  (#$numericallyEqual ?norwegian 1) " +
            "  (#$numericallyEqual ?milk 3) " +
            "  (#$numericallyEqual ?ford ?yellow) " +
            "  (#$numericallyEqual ?oldsmobile ?snails) " +
            "  (#$numericallyEqual ?green (#$PlusFn ?ivory 1)) " +
            "  (#$numericallyEqual ?ukranian ?eggnog) " +
            "  (#$numericallyEqual ?cocoa ?green) " +
            "  (#$numericallyEqual ?spaniard ?dog) " +
            "  (#$numericallyEqual ?english ?red) " +
            "  (#$different ?ford ?chevrolet ?oldsmobile ?mercedes-benz ?volkswagen) " +
            "  (#$different ?orange-juice ?cocoa ?eggnog ?milk ?water) " +
            "  (#$different ?dog ?snails ?horse ?fox ?zebra) " +
            "  (#$different ?english ?spaniard ?norwegian ?japanese ?ukranian) " +
            "  (#$different ?blue ?red ?green ?yellow ?ivory) " +
            "  (#$elementOf ?blue (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?chevrolet (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?cocoa (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?dog (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?eggnog (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?english (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ford (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?fox (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?green (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?horse (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ivory (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?japanese (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?mercedes-benz (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?milk (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?norwegian (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?oldsmobile (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?orange-juice (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?red (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?snails (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?spaniard (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ukranian (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?volkswagen (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?water (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?yellow (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?zebra (#$TheSet 1 2 3 4 5))) ";
        CycList zebraPuzzleCycList = new CycList(zebraPuzzleString);
        ArrayList zebraPuzzleRules = Rule.simplifyRuleExpression(zebraPuzzleCycList);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("unit-test-output.txt");
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (int i = 0; i < zebraPuzzleRules.size(); i++) {
            //System.out.println(((Rule) zebraPuzzleRules.get(i)).cyclify());
            printWriter.println(((Rule) zebraPuzzleRules.get(i)).cyclify());
        }
        printWriter.close();
        System.out.println("** Rule OK **");
    }

    /**
     * Tests the <tt>HornClause</tt> class.
     */
    public void testHornClause() {
        System.out.println("** testHornClause **");

        // constructor
        String hornClauseString =
            "(#$implies " +
            "  (#$and " +
            "    (#$isa ?boat #$Boat) " +
            "    (#$isa ?bodyOfWater #$BodyOfWater) " +
            "    (#$floatingOn ?boat ?bodyOfWater)) " +
            "  (#$objectFoundInLocation ?boat ?bodyOfWater))";
        HornClause hornClause1 = new HornClause(hornClauseString);
        Assert.assertEquals("(#$objectFoundInLocation ?boat ?bodyOfWater)",
                            hornClause1.consequent.cyclify());
        Assert.assertEquals(3, hornClause1.getAntecedantConjuncts().size());
        Assert.assertEquals(2, hornClause1.getVariables().size());
        Assert.assertTrue(
            hornClause1.getVariables().contains(CycVariable.makeCycVariable("?boat")));
        Assert.assertTrue(
            hornClause1.getVariables().contains(CycVariable.makeCycVariable("?bodyOfWater")));
        Assert.assertTrue(
            hornClause1.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?boat #$Boat)")));
        Assert.assertTrue(
            hornClause1.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?bodyOfWater #$BodyOfWater)")));
        Assert.assertTrue(
            hornClause1.getAntecedantConjuncts().contains(
                new Rule("(#$floatingOn ?boat ?bodyOfWater)")));

        // clone()
        HornClause hornClause2 = (HornClause) hornClause1.clone();
        Assert.assertEquals(hornClause1.toString(), hornClause2.toString());
        Assert.assertEquals(hornClause1.cyclify(), hornClause2.cyclify());
        Assert.assertEquals(hornClause1, hornClause2);
        Assert.assertTrue(hornClause1 != hornClause2);

        // substituteVariable
        HornClause hornClause3 = (HornClause) hornClause1.clone();
        hornClause3.substituteVariable(
            CycVariable.makeCycVariable("?boat"),
            CycVariable.makeCycVariable("?waterCraft"));
        Assert.assertTrue(
            ! (hornClause3.getVariables().contains(CycVariable.makeCycVariable("?boat"))));
        Assert.assertTrue(
            hornClause3.getVariables().contains(CycVariable.makeCycVariable("?waterCraft")));
        Assert.assertEquals(3, hornClause3.getAntecedantConjuncts().size());
        Assert.assertEquals(2, hornClause3.getVariables().size());
        Assert.assertTrue(
            hornClause3.getVariables().contains(CycVariable.makeCycVariable("?bodyOfWater")));
        Assert.assertTrue(
            hornClause3.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?waterCraft #$Boat)")));
        Assert.assertTrue(
            hornClause3.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?bodyOfWater #$BodyOfWater)")));
        Assert.assertTrue(
            hornClause3.getAntecedantConjuncts().contains(
                new Rule("(#$floatingOn ?waterCraft ?bodyOfWater)")));

        HornClause hornClause4 = (HornClause) hornClause1.clone();
        hornClause4.substituteVariable(
            CycVariable.makeCycVariable("?boat"),
            CycConstant.makeCycConstant("#$MyWaterCraft"));
        Assert.assertTrue(
            ! (hornClause4.getVariables().contains(CycVariable.makeCycVariable("?boat"))));
        Assert.assertEquals(3, hornClause4.getAntecedantConjuncts().size());
        Assert.assertEquals(1, hornClause4.getVariables().size());
        Assert.assertTrue(
            hornClause4.getVariables().contains(CycVariable.makeCycVariable("?bodyOfWater")));
        Assert.assertTrue(
            hornClause4.getAntecedantConjuncts().contains(
                new Rule("(#$isa #$MyWaterCraft #$Boat)")));
        Assert.assertTrue(
            hornClause4.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?bodyOfWater #$BodyOfWater)")));
        Assert.assertTrue(
            hornClause4.getAntecedantConjuncts().contains(
                new Rule("(#$floatingOn #$MyWaterCraft ?bodyOfWater)")));


        // renameVariables
        HornClause hornClause5 = (HornClause) hornClause1.clone();
        ArrayList otherVariables = new ArrayList();
        Assert.assertTrue(hornClause5.equals(hornClause1));
        hornClause5.renameVariables(otherVariables, 9);
        Assert.assertTrue(hornClause5.equals(hornClause1));

        otherVariables.add(CycVariable.makeCycVariable("?animal"));
        hornClause5.renameVariables(otherVariables, 9);
        Assert.assertTrue(hornClause5.equals(hornClause1));

        otherVariables.add(CycVariable.makeCycVariable("?boat"));
        hornClause5.renameVariables(otherVariables, 9);
        Assert.assertEquals("(#$objectFoundInLocation ?boat_1 ?bodyOfWater)",
                            hornClause5.consequent.cyclify());
        Assert.assertEquals(3, hornClause5.getAntecedantConjuncts().size());
        Assert.assertEquals(2, hornClause5.getVariables().size());
        Assert.assertTrue(
            ! (hornClause5.getVariables().contains(CycVariable.makeCycVariable("?boat"))));
        Assert.assertTrue(
            hornClause5.getVariables().contains(CycVariable.makeCycVariable("?bodyOfWater")));
        Assert.assertTrue(
            ! (hornClause5.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?boat #$Boat)"))));
        Assert.assertTrue(
            hornClause5.getAntecedantConjuncts().contains(
                new Rule("(#$isa ?bodyOfWater #$BodyOfWater)")));
        Assert.assertTrue(
            ! (hornClause5.getAntecedantConjuncts().contains(
                new Rule("(#$floatingOn ?boat ?bodyOfWater)"))));

        System.out.println("** testHornClause OK **");
    }

    /**
     * Tests the <tt>Unifier</tt> class.
     */
    public void testUnifier() {
        System.out.println("** testUnifier **");

        ConstraintProblem constraintProblem = new ConstraintProblem();
        Unifier unifier = constraintProblem.backchainer.unifier;

        // unify
        Rule rule1 = new Rule("(#$objectFoundInLocation #$CityOfAustinTX ?where)");
        String hornClauseString =
            "(#$implies " +
            "  (#$and " +
            "    (#$isa ?boat #$Boat) " +
            "    (#$isa ?bodyOfWater #$BodyOfWater) " +
            "    (#$floatingOn ?boat ?bodyOfWater)) " +
            "  (#$objectFoundInLocation ?boat ?bodyOfWater))";
        HornClause hornClause1 = new HornClause(hornClauseString);
        ArrayList unifiedConjuncts = unifier.unify(rule1, hornClause1);
        Assert.assertEquals(3, unifiedConjuncts.size());
        Assert.assertTrue(unifiedConjuncts.contains(new Rule("(#$isa #$CityOfAustinTX #$Boat)")));
        Assert.assertTrue(unifiedConjuncts.contains(new Rule("(#$isa ?where #$BodyOfWater)")));
        Assert.assertTrue(unifiedConjuncts.contains(
            new Rule("(#$floatingOn #$CityOfAustinTX ?where)")));

        Rule rule2 = new Rule("(#$doneBy #$CityOfAustinTX ?what)");
        String hornClauseString2 =
            "(#$implies " +
            "  (#$and " +
            "    (#$isa ?boat #$Boat) " +
            "    (#$isa ?bodyOfWater #$BodyOfWater) " +
            "    (#$floatingOn ?boat ?bodyOfWater)) " +
            "  (#$objectFoundInLocation ?boat ?bodyOfWater))";
        HornClause hornClause2 = new HornClause(hornClauseString2);
        ArrayList unifiedConjuncts2 = unifier.unify(rule2, hornClause2);
        Assert.assertNull(unifiedConjuncts2);

        Rule rule3 = new Rule("(#$objectFoundInLocation #$CityOfAustinTX ?where)");
        String hornClauseString3 =
            "(#$implies " +
            "  (#$and " +
            "    (#$isa ?boat #$Boat) " +
            "    (#$isa ?bodyOfWater #$BodyOfWater) " +
            "    (#$floatingOn ?boat ?bodyOfWater)) " +
            "  (#$objectFoundInLocation #$CityOfHoustonTX ?bodyOfWater))";
        HornClause hornClause3 = new HornClause(hornClauseString3);
        ArrayList unifiedConjuncts3 = unifier.unify(rule3, hornClause3);
        Assert.assertNull(unifiedConjuncts2);

        System.out.println("** testUnifier OK **");
    }

    /**
     * Tests the <tt>Solution</tt> class.
     */
    public void testSolution() {
        System.out.println("** testSolution **");

        // constructor
        ConstraintProblem constraintProblem = new ConstraintProblem();
        Solution solution = new Solution(constraintProblem);

        // getCurrentSolution
        Assert.assertTrue(solution.getCurrentSolution().size() == 0);

        // getSolutions
        Assert.assertTrue(solution.getSolutions().size() == 1);
        Binding binding1 = new Binding(CycVariable.makeCycVariable("?x"), new Long(1));

        // addBindingToCurrentSolution
        solution.addBindingToCurrentSolution(binding1);
        Assert.assertTrue(solution.getCurrentSolution().size() == 1);
        Assert.assertTrue(solution.getCurrentSolution().contains(binding1));

        // removeBindingFromCurrentSolution
        solution.removeBindingFromCurrentSolution(binding1);
        Assert.assertTrue(solution.getCurrentSolution().size() == 0);

        // addBindingToCurrentSolution
        Binding binding2 = new Binding(CycVariable.makeCycVariable("?y"), new Long(2));
        Binding binding3 = new Binding(CycVariable.makeCycVariable("?z"), new Long(3));
        solution.addBindingToCurrentSolution(binding2);
        solution.addBindingToCurrentSolution(binding3);
        Assert.assertTrue(solution.getCurrentSolution().size() == 2);

        // addSolution
        solution.addSolution(new ArrayList());
        Assert.assertTrue(solution.getSolutions().size() == 2);
        Assert.assertTrue(solution.getCurrentSolution().size() == 0);
        solution.addBindingToCurrentSolution(binding1);
        Assert.assertTrue(solution.getCurrentSolution().size() == 1);
        Assert.assertTrue(solution.getCurrentSolution().contains(binding1));

        // recordNewSolution
        solution.addBindingToCurrentSolution(binding2);
        solution.addBindingToCurrentSolution(binding3);
        Assert.assertTrue(solution.getCurrentSolution().size() == 3);
        Assert.assertTrue(solution.getCurrentSolution().contains(binding2));
        solution.recordNewSolution(binding2);
        Assert.assertTrue(solution.getSolutions().size() == 3);
        Assert.assertTrue(solution.getCurrentSolution().size() == 2);
        Assert.assertTrue(! solution.getCurrentSolution().contains(binding2));

        // finalizeAllSolutions
        solution.addSolution(new ArrayList());
        Assert.assertTrue(solution.getSolutions().size() == 4);
        Assert.assertTrue(solution.getCurrentSolution().size() == 0);
        constraintProblem.nbrSolutionsRequested = null;
        solution.nbrSolutionsFound = 3;
        solution.finalizeAllSolutions();
        Assert.assertTrue(solution.getSolutions().size() == 3);
        Assert.assertTrue(solution.getCurrentSolution().size() == 2);

        System.out.println("** testSolution OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem() {
        System.out.println("** testConstraintProblem **");

        //Zebra Puzzle
        String zebraPuzzleString =
            "(#$and " +
            "  (#$or " +
            "    (#$numericallyEqual ?norwegian (#$PlusFn ?blue 1)) " +
            "    (#$numericallyEqual ?blue (#$PlusFn ?norwegian 1))) " +
            "  (#$numericallyEqual ?japanese ?volkswagen) " +
            "  (#$numericallyEqual ?mercedes-benz ?orange-juice) " +
            "  (#$or " +
            "    (#$numericallyEqual ?ford (#$PlusFn ?horse 1)) " +
            "    (#$numericallyEqual ?horse (#$PlusFn ?ford 1))) " +
            "  (#$or " +
            "    (#$numericallyEqual ?chevrolet (#$PlusFn ?fox 1)) " +
            "    (#$numericallyEqual ?fox (#$PlusFn ?chevrolet 1))) " +
            "  (#$numericallyEqual ?norwegian 1) " +
            "  (#$numericallyEqual ?milk 3) " +
            "  (#$numericallyEqual ?ford ?yellow) " +
            "  (#$numericallyEqual ?oldsmobile ?snails) " +
            "  (#$numericallyEqual ?green (#$PlusFn ?ivory 1)) " +
            "  (#$numericallyEqual ?ukranian ?eggnog) " +
            "  (#$numericallyEqual ?cocoa ?green) " +
            "  (#$numericallyEqual ?spaniard ?dog) " +
            "  (#$numericallyEqual ?english ?red) " +
            "  (#$different ?ford ?chevrolet ?oldsmobile ?mercedes-benz ?volkswagen) " +
            "  (#$different ?orange-juice ?cocoa ?eggnog ?milk ?water) " +
            "  (#$different ?dog ?snails ?horse ?fox ?zebra) " +
            "  (#$different ?english ?spaniard ?norwegian ?japanese ?ukranian) " +
            "  (#$different ?blue ?red ?green ?yellow ?ivory) " +
            "  (#$elementOf ?blue (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?chevrolet (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?cocoa (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?dog (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?eggnog (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?english (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ford (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?fox (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?green (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?horse (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ivory (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?japanese (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?mercedes-benz (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?milk (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?norwegian (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?oldsmobile (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?orange-juice (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?red (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?snails (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?spaniard (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?ukranian (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?volkswagen (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?water (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?yellow (#$TheSet 1 2 3 4 5)) " +
            "  (#$elementOf ?zebra (#$TheSet 1 2 3 4 5))) ";
        CycList zebraPuzzleCycList = new CycList(zebraPuzzleString);
        ArrayList zebraPuzzleRules = Rule.simplifyRuleExpression(zebraPuzzleCycList);

        ConstraintProblem zebraProblem = new ConstraintProblem();
        zebraProblem.setVerbosity(1);
        ArrayList solutions = zebraProblem.solve(zebraPuzzleCycList);
        Assert.assertNotNull(solutions);

        // test extractRulesAndDomains()
        Assert.assertEquals(19, zebraProblem.getNbrConstraintRules());
        Assert.assertEquals(25, zebraProblem.getNbrDomainPopulationRules());

        // test gatherVariables()
        Assert.assertEquals(25, zebraProblem.getNbrVariables());

        // test ValueDomains.initializeDomains()
        Assert.assertEquals(25, zebraProblem.valueDomains.domains.size());
        Assert.assertEquals(25, zebraProblem.valueDomains.varsDictionary.size());
        CycVariable blue = CycVariable.makeCycVariable("?blue");
        Assert.assertNotNull(zebraProblem.valueDomains.varsDictionary.get(blue));
        Assert.assertTrue(zebraProblem.valueDomains.varsDictionary.get(blue) instanceof ArrayList);
        ArrayList domainValues = (ArrayList) zebraProblem.valueDomains.varsDictionary.get(blue);
        Assert.assertEquals(5, domainValues.size());
        Assert.assertTrue(domainValues.contains(new Long(1)));
        Assert.assertTrue(domainValues.contains(new Long(2)));
        Assert.assertTrue(domainValues.contains(new Long(3)));
        Assert.assertTrue(domainValues.contains(new Long(4)));
        Assert.assertTrue(domainValues.contains(new Long(5)));

        // test ValueDomains.domainHasValue(CycVariable cycVariable, Object value)
        Assert.assertTrue(zebraProblem.valueDomains.domainHasValue(blue, new Long(1)));
        Assert.assertTrue(! (zebraProblem.valueDomains.domainHasValue(blue, new Long(6))));

        // test ValueDomains.getDomainValues(CycVariable cycVariable)
        ArrayList domainValues2 = zebraProblem.valueDomains.getDomainValues(blue);
        Assert.assertEquals(domainValues, domainValues2);

        // test ValueDomains.initializeDomainValueMarking()
        Assert.assertNotNull(zebraProblem.valueDomains.domains.get(blue));
        Assert.assertTrue((zebraProblem.valueDomains.domains.get(blue)) instanceof HashMap);
        HashMap domainValueMarks = (HashMap) zebraProblem.valueDomains.domains.get(blue);
        Assert.assertTrue(domainValueMarks.containsKey(new Long(1)));
        Assert.assertNotNull(domainValueMarks.get(new Long(1)));

        // test HighCardinalityDomains
        Assert.assertTrue(zebraProblem.highCardinalityDomains.highCardinalityDomains.size() == 0);

        // test NodeConsistencyAchiever.applyUnaryRulesAndPropagate()
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.unaryConstraintRules.size() == 2);
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.affectedVariables.contains(CycVariable.makeCycVariable("?milk")));
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.affectedVariables.contains(CycVariable.makeCycVariable("?norwegian")));
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.allDifferentRules.size() == 5);
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.singletons.contains(CycVariable.makeCycVariable("milk")));
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.singletons.contains(CycVariable.makeCycVariable("norwegian")));

        // test ForwardCheckingSearcher.search()


        System.out.println("** testConstraintProblem OK **");
    }

}