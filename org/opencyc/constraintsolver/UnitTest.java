package org.opencyc.constraintsolver;

import java.util.*;
import java.io.*;
import junit.framework.*;
import org.opencyc.cycobject.*;
import org.opencyc.inferencesupport.*;
import org.opencyc.api.*;

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
     * Main method in case tracing is prefered over running JUnit GUI.
     */
    public static void main(String[] args) {
        boolean allTests = false;
        //boolean allTests = true;
        runTests(allTests);
    }

    /**
     * Runs the unit tests
     */
    public static void runTests(boolean allTests) {
        TestSuite testSuite;
        if (allTests)
            testSuite = new TestSuite(UnitTest.class);
        else {
            testSuite = new TestSuite();
            //testSuite.addTest(new UnitTest("testHelloWorld"));
            //testSuite.addTest(new UnitTest("testRuleEvaluator"));
            //testSuite.addTest(new UnitTest("testArgumentTypeConstrainer"));
            //testSuite.addTest(new UnitTest("testProblemParser"));
            //testSuite.addTest(new UnitTest("testConstraintProblem1"));
            //testSuite.addTest(new UnitTest("testConstraintProblem2"));
            //testSuite.addTest(new UnitTest("testConstraintProblem3"));
            //testSuite.addTest(new UnitTest("testConstraintProblem4"));
            //testSuite.addTest(new UnitTest("testConstraintProblem5"));
            //testSuite.addTest(new UnitTest("testConstraintProblem6"));
            //testSuite.addTest(new UnitTest("testConstraintProblem7"));
            //testSuite.addTest(new UnitTest("testConstraintProblem8"));
            testSuite.addTest(new UnitTest("testConstraintProblem9"));
            //testSuite.addTest(new UnitTest("testBackchainer1"));
            //testSuite.addTest(new UnitTest("testBackchainer2"));

            //testSuite.addTest(new UnitTest("testBackchainer3"));

            //testSuite.addTest(new UnitTest("testBackchainer4"));
            //testSuite.addTest(new UnitTest("testBackchainer5"));
            //testSuite.addTest(new UnitTest("testBackchainer6"));
        }
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
     * Tests the <tt>ProblemParser</tt> class.
     */
    public void testProblemParser() {
        System.out.println("** testProblemParser **");

        ConstraintProblem constraintProblem = new ConstraintProblem();
        String problemString1 =
            "(#$and " +
            "  (#$isa ?country #$WesternEuropeanCountry) " +
            "  (#$isa ?cathedral #$Cathedral) " +
            "  (#$countryOfCity ?country ?city) " +
            "  (#$objectFoundInLocation ?cathedral ?city)) ";
        ConstraintRule rule1 = null;
        ConstraintRule rule2 = null;
        ConstraintRule rule3 = null;
        ConstraintRule rule4 = null;
        ConstraintRule rule5 = null;
        ConstraintRule rule6 = null;
        try {
            CycList problem1 = CycAccess.current().makeCycList(problemString1);
            constraintProblem.problem = problem1;
            constraintProblem.simplifiedRules = ConstraintRule.simplifyConstraintRuleExpression(problem1);
            ProblemParser problemParser = constraintProblem.problemParser;
            problemParser.extractRulesAndDomains();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(constraintProblem.constraintRules);
        Assert.assertNotNull(constraintProblem.domainPopulationRules);
        try {
            rule1 = new ConstraintRule("(#$isa ?country #$WesternEuropeanCountry)");
            rule2 = new ConstraintRule("(#$isa ?cathedral #$Cathedral)");
            rule3 = new ConstraintRule("(#$countryOfCity ?country ?city)");
            rule4 = new ConstraintRule("(#$objectFoundInLocation ?cathedral ?city)");
            rule5 = new ConstraintRule("(#$isa ?city #$City)");
            rule6 = new ConstraintRule("(#$isa ?country #$Country)");
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("domainPopulationRules\n" + constraintProblem.domainPopulationRules);
        Assert.assertEquals(3, constraintProblem.domainPopulationRules.size());
        Assert.assertTrue(constraintProblem.domainPopulationRules.contains(rule1));
        Assert.assertTrue(constraintProblem.domainPopulationRules.contains(rule2));
        Assert.assertTrue(constraintProblem.domainPopulationRules.contains(rule5));
        System.out.println("constraintRules\n" + constraintProblem.constraintRules);
        Assert.assertEquals(2, constraintProblem.constraintRules.size());
        Assert.assertTrue(constraintProblem.constraintRules.contains(rule3));
        Assert.assertTrue(constraintProblem.constraintRules.contains(rule4));

        System.out.println("** testProblemParser OK **");
    }

    /**
     * Tests the <tt>RuleEvaluator</tt> class.
     */
    public void testRuleEvaluator() {
        System.out.println("** testRuleEvaluator **");

        ConstraintProblem constraintProblem = new ConstraintProblem();
        RuleEvaluator ruleEvaluator = constraintProblem.ruleEvaluator;
        try {
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$numericallyEqual 1 1)")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$numericallyEqual 2 1)")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$different 2 1)")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$different 2 2)")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$different \"a\" \"b\")")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$different \"a\" \"a\")")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$not (#$different 1 1))")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$not (#$not (#$different 1 1)))")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 3))")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$and (#$numericallyEqual 1 2) (#$numericallyEqual 3 3))")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 4))")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$and (#$numericallyEqual 1 1) (#$numericallyEqual 3 3) (#$numericallyEqual 4 4))")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$or (#$numericallyEqual 1 2) (#$numericallyEqual 3 3))")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$or (#$numericallyEqual 1 2) (#$numericallyEqual 3 4))")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$numericallyEqual 2 (#$PlusFn 1))")));
            Assert.assertTrue(ruleEvaluator.ask(new ConstraintRule("(#$numericallyEqual (#$PlusFn 1) 2)")));
            Assert.assertTrue(! ruleEvaluator.ask(new ConstraintRule("(#$numericallyEqual (#$PlusFn 1) 5)")));
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        System.out.println("** testRuleEvaluator OK **");
    }

    /**
     * Tests the <tt>ArgumentTypeConstrainer</tt> class.
     */
    public void testArgumentTypeConstrainer() {
        System.out.println("** testArgumentTypeConstrainer **");

        ConstraintProblem constraintProblem = null;
        try {
            constraintProblem = new ConstraintProblem();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try{
            ConstraintRule rule1 = new ConstraintRule("(#$holdsIn (#$YearFn 1993) (#$totalDebt ?X (#$BillionDollars 7)))");
            ArrayList argConstraints =
                constraintProblem.argumentTypeConstrainer.retrieveArgumentTypeConstraintRules(rule1);
            ConstraintRule rule2 = new ConstraintRule ("(#$isa ?X #$GeographicalRegion)");
            Assert.assertNotNull(argConstraints);
            Assert.assertEquals(1, argConstraints.size());
            Assert.assertTrue(argConstraints.contains(rule2));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        try{
            ConstraintRule rule1 = new ConstraintRule("(#$countryOfCity ?country ?city)");
            ArrayList argConstraints =
                constraintProblem.argumentTypeConstrainer.retrieveArgumentTypeConstraintRules(rule1);
            ConstraintRule rule2 = new ConstraintRule ("(#$isa ?country #$Country)");
            ConstraintRule rule3 = new ConstraintRule ("(#$isa ?city #$City)");
            Assert.assertNotNull(argConstraints);
            Assert.assertEquals(2, argConstraints.size());
            Assert.assertTrue(argConstraints.contains(rule2));
            Assert.assertTrue(argConstraints.contains(rule3));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            CycAccess.current().close();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        System.out.println("** testArgumentTypeConstrainer OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem1() {
        System.out.println("** testConstraintProblem1 **");

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
        ConstraintProblem zebraProblem = new ConstraintProblem();
        CycAccess cycAccess = zebraProblem.cycAccess;
        CycList zebraPuzzleCycList = cycAccess.makeCycList(zebraPuzzleString);
        try {
            ArrayList zebraPuzzleRules = ConstraintRule.simplifyConstraintRuleExpression(zebraPuzzleCycList);
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        zebraProblem.setVerbosity(1);
        ArrayList solutions = zebraProblem.solve(zebraPuzzleCycList);
        Assert.assertNotNull(solutions);

        // test extractRulesAndDomains()
        zebraProblem.displayConstraintRules();
        Assert.assertEquals(17, zebraProblem.getNbrConstraintRules());
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
        Assert.assertTrue(domainValues.contains(new Integer(1)));
        Assert.assertTrue(domainValues.contains(new Integer(2)));
        Assert.assertTrue(domainValues.contains(new Integer(3)));
        Assert.assertTrue(domainValues.contains(new Integer(4)));
        Assert.assertTrue(domainValues.contains(new Integer(5)));

        // test ValueDomains.domainHasValue(CycVariable cycVariable, Object value)
        Assert.assertTrue(zebraProblem.valueDomains.domainHasValue(blue, new Integer(1)));
        Assert.assertTrue(! (zebraProblem.valueDomains.domainHasValue(blue, new Integer(6))));

        // test ValueDomains.getDomainValues(CycVariable cycVariable)
        ArrayList domainValues2 = zebraProblem.valueDomains.getDomainValues(blue);
        Assert.assertEquals(domainValues, domainValues2);

        // test ValueDomains.initializeDomainValueMarking()
        Assert.assertNotNull(zebraProblem.valueDomains.domains.get(blue));
        Assert.assertTrue((zebraProblem.valueDomains.domains.get(blue)) instanceof HashMap);
        HashMap domainValueMarks = (HashMap) zebraProblem.valueDomains.domains.get(blue);
        Assert.assertTrue(domainValueMarks.containsKey(new Integer(1)));
        Assert.assertNotNull(domainValueMarks.get(new Integer(1)));

        // test VariableDomainPopulator
        Assert.assertTrue(zebraProblem.variableDomainPopulator.variableDomainPopulators.size() == 0);

        // test NodeConsistencyAchiever.applyUnaryRulesAndPropagate()
        Assert.assertEquals(20, zebraProblem.nodeConsistencyAchiever.unaryConstraintRules.size());
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.affectedVariables.contains(CycVariable.makeCycVariable("?milk")));
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.affectedVariables.contains(CycVariable.makeCycVariable("?norwegian")));
        Assert.assertEquals(5, zebraProblem.nodeConsistencyAchiever.allDifferentRules.size());
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.singletons.contains(CycVariable.makeCycVariable("milk")));
        Assert.assertTrue(zebraProblem.nodeConsistencyAchiever.singletons.contains(CycVariable.makeCycVariable("norwegian")));

        System.out.println("** testConstraintProblem1 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem2() {
        System.out.println("** testConstraintProblem2 **");

        // European Cathedrals with arg type discovery
        String europeanCathedralsString2 =
            "(#$and " +
            "  (#$isa ?country #$WesternEuropeanCountry) " +
            "  (#$isa ?cathedral #$Cathedral) " +
            "  (#$countryOfCity ?country ?city) " +
            "  (#$objectFoundInLocation ?cathedral ?city)) ";
        System.out.println(europeanCathedralsString2);
        ConstraintProblem europeanCathedralsProblem2 = new ConstraintProblem();
        europeanCathedralsProblem2.setVerbosity(1);
        // Request one solution.
        europeanCathedralsProblem2.nbrSolutionsRequested = new Integer(1);
        // Request all solutions.
        //europeanCathedralsProblem2.nbrSolutionsRequested = null;
        try {
            europeanCathedralsProblem2.mt =
                CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
            ArrayList solutions = europeanCathedralsProblem2.solve(CycAccess.current().makeCycList(europeanCathedralsString2));
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        // European Cathedrals
        String europeanCathedralsString =
            "(#$and " +
            "  (#$isa ?country #$WesternEuropeanCountry) " +
            "  (#$isa ?city #$City) " +
            "  (#$isa ?cathedral #$Cathedral) " +
            "  (#$countryOfCity ?country ?city) " +
            "  (#$objectFoundInLocation ?cathedral ?city)) ";
        System.out.println(europeanCathedralsString);
        ConstraintProblem europeanCathedralsProblem = new ConstraintProblem();
        europeanCathedralsProblem.setVerbosity(1);
        // Request two solutions.
        // europeanCathedralsProblem.nbrSolutionsRequested = new Integer(2);
        // Request all solutions.
        europeanCathedralsProblem.nbrSolutionsRequested = null;
        try {
            europeanCathedralsProblem.mt =
                CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        ArrayList solutions = europeanCathedralsProblem.solve(CycAccess.current().makeCycList(europeanCathedralsString));
        Assert.assertNotNull(solutions);


        System.out.println("** testConstraintProblem2 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem3() {
        System.out.println("** testConstraintProblem3 **");

        // European Cathedrals with arg type discovery
        String whatIsInAustinString =
            "(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)";
        System.out.println(whatIsInAustinString);
        ConstraintProblem whatIsInAustinProblem2 = new ConstraintProblem();
        whatIsInAustinProblem2.setVerbosity(8);
        // Request one solution.
        //whatIsInAustinProblem2.nbrSolutionsRequested = new Integer(1);
        // Request all solutions.
        whatIsInAustinProblem2.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem2.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem2.solve(whatIsInAustinString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem3 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem4() {
        System.out.println("** testConstraintProblem4 **");

        // One variable query.
        String oneVariableQueryString =
            "(#$and (#$isa ?WHAT #$CarvedArtwork) (#$provenanceOfArtObject #$CityOfAustinTX ?WHAT))";
        System.out.println(oneVariableQueryString);
        ConstraintProblem oneVariableQueryProblem = new ConstraintProblem();
        oneVariableQueryProblem.setVerbosity(9);
        // Request all solutions.
        oneVariableQueryProblem.nbrSolutionsRequested = null;
        try {
            oneVariableQueryProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = oneVariableQueryProblem.solve(oneVariableQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem4 OK **");
    }


    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem5() {
        System.out.println("** testConstraintProblem5 **");

        // One variable query.
        String oneVariableQueryString =
            "(#$and (#$groupMembers ?C ?C) (#$objectFoundInLocation ?C #$CityOfAustinTX))";
        System.out.println(oneVariableQueryString);
        ConstraintProblem oneVariableQueryProblem = new ConstraintProblem();
        oneVariableQueryProblem.setVerbosity(9);
        // Request all solutions.
        oneVariableQueryProblem.nbrSolutionsRequested = null;
        try {
            oneVariableQueryProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = oneVariableQueryProblem.solve(oneVariableQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem5 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem6() {
        System.out.println("** testConstraintProblem6 **");

        // query with nart.
        String nartQueryString =
            "(#$isa ?STORAGE (#$StoreFn #$CarvedArtwork))";
        System.out.println(nartQueryString);
        ConstraintProblem nartProblem = new ConstraintProblem();
        nartProblem.setVerbosity(9);
        // Request all solutions.
        nartProblem.nbrSolutionsRequested = null;
        try {
            nartProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = nartProblem.solve(nartQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem6 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem7() {
        System.out.println("** testConstraintProblem7 **");

        // NFn.
        String nFnString =
            "(#$and (#$physicalParts #$CityOfAustinTX (#$NFn ?COL ?N ?INDEX)) " +
            "       (#$groupMembers (#$NFn ?COL ?N ?INDEX) ?MEMBER))";
        System.out.println(nFnString);
        ConstraintProblem nFnProblem = new ConstraintProblem();
        nFnProblem.setVerbosity(9);
        // Request all solutions.
        nFnProblem.nbrSolutionsRequested = null;
        try {
            nFnProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = nFnProblem.solve(nFnString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem7 OK **");
    }

    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem8() {
        System.out.println("** testConstraintProblem8 **");

        // domain population rules are mostly high cardinality isa rules.
        String isaDomainString =
            "(#$and " +
            "  (#$isa (#$GovernmentFn ?STATE) #$Organization) " +
            "  (#$physicalExtent (#$GovernmentFn ?STATE) ?PLACE) " +
            "  (#$physicalParts ?PLACE ?CAP) " +
            "  (#$isa ?CAP #$ConstructionArtifact))";
        System.out.println(isaDomainString);
        ConstraintProblem isaDomainProblem = new ConstraintProblem();
        isaDomainProblem.setVerbosity(9);
        // Request all solutions.
        isaDomainProblem.nbrSolutionsRequested = null;
        try {
            isaDomainProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = isaDomainProblem.solve(isaDomainString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem8 OK **");
    }

//(and (temporallySubsumes ?TIME ?MT-TIME) (mtTime ?MT ?MT-TIME) (holdsIn ?TIME (on-Physical ?WHAT CityOfAustinTX)))
    /**
     * Tests the <tt>ConstraintProblem</tt> class.
     */
    public void testConstraintProblem9() {
        System.out.println("** testConstraintProblem9 **");

        // domain population rules are all high cardinality isa rules.
        String problemString =
            "(#$and " +
            "  (#$temporallySubsumes ?TIME ?MT-TIME) " +
            "  (#$mtTime ?MT ?MT-TIME) " +
            "  (#$holdsIn ?TIME (#$on-Physical ?WHAT #$CityOfAustinTX)))";
        System.out.println(problemString);
        ConstraintProblem problem = new ConstraintProblem();
        problem.setVerbosity(9);
        // Request all solutions.
        problem.nbrSolutionsRequested = null;
        try {
            problem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = problem.solve(problemString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testConstraintProblem9 OK **");
    }


    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer1() {
        System.out.println("** testBackchainer1 **");

        // what is in Austin?
        String whatIsInAustinString =
            "(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)";
        System.out.println(whatIsInAustinString);
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        ConstraintProblem whatIsInAustinProblem = new ConstraintProblem(cycAccess);
        whatIsInAustinProblem.setVerbosity(8);
        // Request one solution.
        //whatIsInAustinProblem.nbrSolutionsRequested = new Integer(1);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            whatIsInAustinProblem.problem = CycAccess.current().makeCycList(whatIsInAustinString);
            whatIsInAustinProblem.problemParser.extractRulesAndDomains();
            ArrayList backchainRules =
                whatIsInAustinProblem.backchainer.getBackchainRules(whatIsInAustinProblem.domainPopulationRules);
            Assert.assertNotNull(backchainRules);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testBackchainer1 OK **");
    }

    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer2() {
        System.out.println("** testBackchainer2 **");
        // what is in Austin?
        String whatIsInAustinString =
            "(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)";
        System.out.println(whatIsInAustinString);
        ConstraintProblem whatIsInAustinProblem = new ConstraintProblem();
        whatIsInAustinProblem.setVerbosity(3);
        whatIsInAustinProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem.solve(whatIsInAustinString);
            for (int i = 0; i < solutions.size(); i++)
                System.out.println(solutions.get(i));
            //Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        System.out.println("** testBackchainer2 OK **");
    }

    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer3() {
        System.out.println("** testBackchainer3 **");
        // what is in Austin? to depth 2
        String whatIsInAustinString =
            "(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)";
        System.out.println(whatIsInAustinString);
        ConstraintProblem whatIsInAustinProblem = new ConstraintProblem();
        //whatIsInAustinProblem.setVerbosity(9);
        whatIsInAustinProblem.setVerbosity(2);
        whatIsInAustinProblem.setMaxBackchainDepth(2);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem.solve(whatIsInAustinString);
            for (int i = 0; i < solutions.size(); i++)
                System.out.println(solutions.get(i));
            //Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        System.out.println("** testBackchainer3 OK **");
    }

    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer4() {
        System.out.println("** testBackchainer4 **");
        // what is a CarvedArtwork? to depth 1
        String whatIsACarvedArtworkString =
            "(#$isa ?WHAT #$CarvedArtwork)";
        System.out.println(whatIsACarvedArtworkString);
        ConstraintProblem whatIsACarvedArtworkProblem = new ConstraintProblem();
        whatIsACarvedArtworkProblem.setVerbosity(9);
        whatIsACarvedArtworkProblem.setSbhlBackchain(true);
        whatIsACarvedArtworkProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsACarvedArtworkProblem.nbrSolutionsRequested = null;
        try {
            whatIsACarvedArtworkProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsACarvedArtworkProblem.solve(whatIsACarvedArtworkString);
            for (int i = 0; i < solutions.size(); i++)
                System.out.println(solutions.get(i));
            //Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        System.out.println("** testBackchainer4 OK **");
    }

    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer5() {
        System.out.println("** testBackchainer5 **");
        String whatIsACarvedArtworkString =
            "(#$holdsIn ?SIT (#$pathState #$CityOfAustinTX #$PathBlocked))";
        System.out.println(whatIsACarvedArtworkString);
        ConstraintProblem whatIsACarvedArtworkProblem = new ConstraintProblem();
        whatIsACarvedArtworkProblem.setVerbosity(9);
        whatIsACarvedArtworkProblem.setSbhlBackchain(true);
        whatIsACarvedArtworkProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsACarvedArtworkProblem.nbrSolutionsRequested = null;
        try {
            whatIsACarvedArtworkProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsACarvedArtworkProblem.solve(whatIsACarvedArtworkString);
            for (int i = 0; i < solutions.size(); i++)
                System.out.println(solutions.get(i));
            //Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }
        System.out.println("** testBackchainer5 OK **");
    }

    /**
     * Tests the <tt>Backchainer</tt> class.
     */
    public void testBackchainer6() {
        System.out.println("** testBackchainer6 **");

        // austinIsaHarbor.
        String austinIsaHarborString =
            "(#$and (#$isa ?WHAT #$Ship) " +
            "       (#$objectFoundInLocation ?WHAT ?L) " +
            "       (#$isa ?L #$PortCity) " +
            "       (#$isa #$CityOfAustinTX #$Harbor) " +
            "       (#$geographicalSubRegions ?L #$CityOfAustinTX))";
        System.out.println(austinIsaHarborString);
        ConstraintProblem austinIsaHarborProblem = new ConstraintProblem();
        austinIsaHarborProblem.setMaxBackchainDepth(1);
        austinIsaHarborProblem.setVerbosity(9);
        // Request all solutions.
        austinIsaHarborProblem.nbrSolutionsRequested = null;
        try {
            austinIsaHarborProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = austinIsaHarborProblem.solve(austinIsaHarborString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testBackchainer6 OK **");
    }

}