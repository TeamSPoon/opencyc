package org.opencyc.queryprocessor;

import java.util.*;
import java.io.*;
import junit.framework.*;
import org.opencyc.cycobject.*;
import org.opencyc.inferencesupport.*;
import org.opencyc.api.*;

/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.queryprocessor</tt> package.<p>
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
            //testSuite.addTest(new UnitTest("testQueryParser"));
            //testSuite.addTest(new UnitTest("testLiteralAsker"));
            //testSuite.addTest(new UnitTest("testHashJoiner"));
            //testSuite.addTest(new UnitTest("testQueryProcessor1"));
            //testSuite.addTest(new UnitTest("testQueryProcessor3"));
            //testSuite.addTest(new UnitTest("testQueryProcessor4"));
            //testSuite.addTest(new UnitTest("testQueryProcessor5"));
            //testSuite.addTest(new UnitTest("testQueryProcessor6"));
            //testSuite.addTest(new UnitTest("testQueryProcessor7"));
            //testSuite.addTest(new UnitTest("testQueryProcessor8"));
            //testSuite.addTest(new UnitTest("testQueryProcessor9"));
            //testSuite.addTest(new UnitTest("testQueryProcessor10"));
            testSuite.addTest(new UnitTest("testQueryProcessor11"));
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
     * Tests the <tt>QueryParser</tt> class.
     */
    public void testQueryParser() {
        System.out.println("** testQueryParser **");

        QueryProcessor queryProcessor = new QueryProcessor();
        int verbosity = 3;
        queryProcessor.solution = new Solution(null, verbosity);
        String queryString1 =
            "(#$and " +
            "  (#$isa ?country #$WesternEuropeanCountry) " +
            "  (#$isa ?cathedral #$Cathedral) " +
            "  (#$countryOfCity ?country ?city) " +
            "  (#$objectFoundInLocation ?cathedral ?city)) ";
        QueryLiteral queryLiteral1 = null;
        QueryLiteral queryLiteral2 = null;
        QueryLiteral queryLiteral3 = null;
        QueryLiteral queryLiteral4 = null;
        try {
            CycList query1 = CycAccess.current().makeCycList(queryString1);
            queryProcessor.query = query1;
            queryProcessor.queryLiterals = QueryLiteral.simplifyQueryLiteralExpression(query1);
            QueryParser queryParser = queryProcessor.queryParser;
            queryParser.extractQueryLiterals();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(queryProcessor.queryLiterals);
        try {
            queryLiteral1 = new QueryLiteral("(#$isa ?country #$WesternEuropeanCountry)");
            queryLiteral2 = new QueryLiteral("(#$isa ?cathedral #$Cathedral)");
            queryLiteral3 = new QueryLiteral("(#$countryOfCity ?country ?city)");
            queryLiteral4 = new QueryLiteral("(#$objectFoundInLocation ?cathedral ?city)");
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("queryLiterals\n" + queryProcessor.queryLiterals);
        Assert.assertEquals(4, queryProcessor.queryLiterals.size());
        Assert.assertTrue(queryProcessor.queryLiterals.contains(queryLiteral1));
        Assert.assertTrue(queryProcessor.queryLiterals.contains(queryLiteral2));
        Assert.assertTrue(queryProcessor.queryLiterals.contains(queryLiteral3));
        Assert.assertTrue(queryProcessor.queryLiterals.contains(queryLiteral4));

        System.out.println("** testProblemParser OK **");
    }

    /**
     * Tests the <tt>LiteralAsker</tt> class.
     */
    public void testLiteralAsker() {
        System.out.println("** testLiteralAsker **");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        QueryLiteral queryLiteral1 = null;
        QueryLiteral queryLiteral2 = null;
        QueryLiteral queryLiteral3 = null;
        QueryLiteral queryLiteral4 = null;
        CycFort mt = null;
        try {
            queryLiteral1 = new QueryLiteral("(#$isa ?country #$WesternEuropeanCountry)");
            queryLiteral2 = new QueryLiteral("(#$isa ?cathedral #$Cathedral)");
            queryLiteral3 = new QueryLiteral("(#$countryOfCity ?country ?city)");
            queryLiteral4 = new QueryLiteral("(#$objectFoundInLocation ?cathedral ?city)");
            mt = CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        ArrayList queryLiterals = new ArrayList();
        queryLiterals.add(queryLiteral1);
        queryLiterals.add(queryLiteral2);
        queryLiterals.add(queryLiteral3);
        queryLiterals.add(queryLiteral4);
        LiteralAsker literalAsker = new LiteralAsker();
        literalAsker.setVerbosity(0);
        ArrayList bindingSets = null;
        try {
            bindingSets = literalAsker.ask(queryLiterals, mt);
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        for (int i = 0; i < bindingSets.size(); i++) {
            BindingSet bindingSet = (BindingSet) bindingSets.get(i);
            //bindingSet.displayBindingSet();
        }
        try {
            cycAccess.close();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("** testLiteralAsker OK **");
    }

    /**
     * Tests the <tt>HashJoinerr</tt> class.
     */
    public void testHashJoiner() {
        System.out.println("** testHashJoiner **");

        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        QueryLiteral queryLiteral1 = null;
        QueryLiteral queryLiteral2 = null;
        QueryLiteral queryLiteral3 = null;
        QueryLiteral queryLiteral4 = null;
        CycFort mt = null;
        try {
            queryLiteral1 = new QueryLiteral("(#$isa ?country #$WesternEuropeanCountry)");
            queryLiteral2 = new QueryLiteral("(#$isa ?cathedral #$Cathedral)");
            queryLiteral3 = new QueryLiteral("(#$countryOfCity ?country ?city)");
            queryLiteral4 = new QueryLiteral("(#$objectFoundInLocation ?cathedral ?city)");
            mt = CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        ArrayList queryLiterals = new ArrayList();
        queryLiterals.add(queryLiteral1);
        queryLiterals.add(queryLiteral2);
        queryLiterals.add(queryLiteral3);
        queryLiterals.add(queryLiteral4);
        LiteralAsker literalAsker = new LiteralAsker();
        literalAsker.setVerbosity(3);
        ArrayList bindingSets = null;
        try {
            bindingSets = literalAsker.ask(queryLiterals, mt);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        HashJoiner hashJoiner = new HashJoiner();
        hashJoiner.literalAsker = literalAsker;
        hashJoiner.setVerbosity(3);
        BindingSet joinedBindingSets = null;
        try {
            joinedBindingSets = hashJoiner.join(bindingSets);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(joinedBindingSets);

        try {
            cycAccess.close();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        System.out.println("** testHashJoiner OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor1() {
        System.out.println("** testQueryProcessor1 **");

        // European Cathedrals
        String europeanCathedralsString =
            "(#$and " +
            "  (#$isa ?country #$WesternEuropeanCountry) " +
            "  (#$isa ?city #$City) " +
            "  (#$isa ?cathedral #$Cathedral) " +
            "  (#$countryOfCity ?country ?city) " +
            "  (#$objectFoundInLocation ?cathedral ?city)) ";
        QueryProcessor europeanCathedralsQuery = new QueryProcessor();
        europeanCathedralsQuery.setVerbosity(3);
        // Request two solutions.
        // europeanCathedralsQuery.nbrSolutionsRequested = new Integer(2);
        // Request all solutions.
        europeanCathedralsQuery.nbrSolutionsRequested = null;
        ArrayList solutions = null;
        try {
            europeanCathedralsQuery.mt =
                CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
            solutions = europeanCathedralsQuery.ask(CycAccess.current().makeCycList(europeanCathedralsString));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(solutions);

        System.out.println("** testQueryProcessor1 OK **");
    }


    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor3() {
        System.out.println("** testQueryProcessor3 **");

        // European Cathedrals with arg type discovery
        String whatIsInAustinString =
            "(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)";
        System.out.println(whatIsInAustinString);
        QueryProcessor whatIsInAustinProblem2 = new QueryProcessor();
        whatIsInAustinProblem2.setVerbosity(3);
        // Request one solution.
        //whatIsInAustinProblem2.nbrSolutionsRequested = new Integer(1);
        // Request all solutions.
        whatIsInAustinProblem2.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem2.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem2.ask(whatIsInAustinString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor3 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor4() {
        System.out.println("** testQueryProcessor4 **");

        // One variable query.
        String oneVariableQueryString =
            "(#$and (#$isa ?WHAT #$CarvedArtwork) (#$provenanceOfArtObject #$CityOfAustinTX ?WHAT))";
        System.out.println(oneVariableQueryString);
        QueryProcessor oneVariableQueryProblem = new QueryProcessor();
        oneVariableQueryProblem.setVerbosity(3);
        // Request all solutions.
        oneVariableQueryProblem.nbrSolutionsRequested = null;
        try {
            oneVariableQueryProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = oneVariableQueryProblem.ask(oneVariableQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor4 OK **");
    }


    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor5() {
        System.out.println("** testQueryProcessor5 **");

        // One variable query.
        String oneVariableQueryString =
            "(#$and (#$groupMembers ?C ?C) (#$objectFoundInLocation ?C #$CityOfAustinTX))";
        System.out.println(oneVariableQueryString);
        QueryProcessor oneVariableQueryProblem = new QueryProcessor();
        oneVariableQueryProblem.setVerbosity(3);
        // Request all solutions.
        oneVariableQueryProblem.nbrSolutionsRequested = null;
        try {
            oneVariableQueryProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = oneVariableQueryProblem.ask(oneVariableQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor5 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor6() {
        System.out.println("** testQueryProcessor6 **");

        // query with nart.
        String nartQueryString =
            "(#$isa ?STORAGE (#$StoreFn #$CarvedArtwork))";
        System.out.println(nartQueryString);
        QueryProcessor nartProblem = new QueryProcessor();
        nartProblem.setVerbosity(3);
        // Request all solutions.
        nartProblem.nbrSolutionsRequested = null;
        try {
            nartProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = nartProblem.ask(nartQueryString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor6 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor7() {
        System.out.println("** testQueryProcessor7 **");

        // NFn.
        String nFnString =
            "(#$and (#$physicalParts #$CityOfAustinTX (#$NFn ?COL ?N ?INDEX)) " +
            "       (#$groupMembers (#$NFn ?COL ?N ?INDEX) ?MEMBER))";
        System.out.println(nFnString);
        QueryProcessor nFnProblem = new QueryProcessor();
        nFnProblem.setVerbosity(3);
        // Request all solutions.
        nFnProblem.nbrSolutionsRequested = null;
        try {
            nFnProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = nFnProblem.ask(nFnString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor7 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor8() {
        System.out.println("** testQueryProcessor8 **");

        // domain population rules are mostly high cardinality isa rules.
        String isaDomainString =
            "(#$and " +
            "  (#$isa (#$GovernmentFn ?STATE) #$Organization) " +
            "  (#$physicalExtent (#$GovernmentFn ?STATE) ?PLACE) " +
            "  (#$physicalParts ?PLACE ?CAP) " +
            "  (#$isa ?CAP #$ConstructionArtifact))";
        System.out.println(isaDomainString);
        QueryProcessor isaDomainProblem = new QueryProcessor();
        isaDomainProblem.setVerbosity(3);
        // Request all solutions.
        isaDomainProblem.nbrSolutionsRequested = null;
        try {
            isaDomainProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = isaDomainProblem.ask(isaDomainString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor8 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor9() {
        System.out.println("** testQueryProcessor9 **");

        // domain population rules are all high cardinality isa rules.
        String problemString =
            "(#$and " +
            "  (#$temporallySubsumes ?TIME ?MT-TIME) " +
            "  (#$mtTime ?MT ?MT-TIME) " +
            "  (#$holdsIn ?TIME (#$on-Physical ?WHAT #$CityOfAustinTX)))";
        System.out.println(problemString);
        QueryProcessor problem = new QueryProcessor();
        problem.setVerbosity(3);
        // Request all solutions.
        problem.nbrSolutionsRequested = null;
        try {
            problem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = problem.ask(problemString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor9 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor10() {
        System.out.println("** testQueryProcessor10 **");

        // zebra constraint problem.
        String problemString =
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
        System.out.println(problemString);
        QueryProcessor problem = new QueryProcessor();
        problem.setVerbosity(9);
        // Request all solutions.
        problem.nbrSolutionsRequested = null;
        try {
            problem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = problem.ask(problemString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor10 OK **");
    }

    /**
     * Tests the <tt>QueryProcessor</tt> class.
     */
    public void testQueryProcessor11() {
        System.out.println("** testQueryProcessor11 **");

        String problemString =
            "(#$and " +
            "  (#$assertedSentence " +
            "    (#$isa ?COUNTRY #$WesternEuropeanCountry)) " +
            "  (#$assertedSentence " +
            "    (#$isa ?CATHEDRAL #$Cathedral)) " +
            "  (#$assertedSentence " +
            "    (#$countryOfCity ?COUNTRY ?CITY)) " +
            "  (#$assertedSentence  " +
            "    (#$objectFoundInLocation ?CATHEDRAL ?CITY)))";
            //"  (#$genlPreds ?OBJECT-SPEC #$objectFoundInLocation) " +
            //"  (#$assertedSentence  " +
            //"    (?OBJECT-SPEC ?CATHEDRAL ?CITY)))";
        System.out.println(problemString);
        QueryProcessor problem = new QueryProcessor();
        problem.setVerbosity(1);
        // Request all solutions.
        problem.nbrSolutionsRequested = null;
        try {
            problem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = problem.ask(problemString);
        Assert.assertNotNull(solutions);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail(e.getMessage());
        }

        System.out.println("** testQueryProcessor11 OK **");
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
        QueryProcessor whatIsInAustinProblem = new QueryProcessor(cycAccess);
        whatIsInAustinProblem.setVerbosity(9);
        // Request one solution.
        //whatIsInAustinProblem.nbrSolutionsRequested = new Integer(1);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            whatIsInAustinProblem.query = CycAccess.current().makeCycList(whatIsInAustinString);
            whatIsInAustinProblem.queryParser.extractQueryLiterals();
            whatIsInAustinProblem.backchainer.mt = whatIsInAustinProblem.mt;
            whatIsInAustinProblem.backchainer.setVariables(whatIsInAustinProblem.getVariables());
            ArrayList backchainRules =
                whatIsInAustinProblem.backchainer.getBackchainRules(whatIsInAustinProblem.queryLiterals);
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
        QueryProcessor whatIsInAustinProblem = new QueryProcessor();
        whatIsInAustinProblem.setVerbosity(3);
        whatIsInAustinProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem.ask(whatIsInAustinString);
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
        QueryProcessor whatIsInAustinProblem = new QueryProcessor();
        //whatIsInAustinProblem.setVerbosity(9);
        whatIsInAustinProblem.setVerbosity(2);
        whatIsInAustinProblem.setMaxBackchainDepth(2);
        // Request all solutions.
        whatIsInAustinProblem.nbrSolutionsRequested = null;
        try {
            whatIsInAustinProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsInAustinProblem.ask(whatIsInAustinString);
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
        QueryProcessor whatIsACarvedArtworkProblem = new QueryProcessor();
        whatIsACarvedArtworkProblem.setVerbosity(3);
        whatIsACarvedArtworkProblem.setSbhlBackchain(true);
        whatIsACarvedArtworkProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsACarvedArtworkProblem.nbrSolutionsRequested = null;
        try {
            whatIsACarvedArtworkProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsACarvedArtworkProblem.ask(whatIsACarvedArtworkString);
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
        QueryProcessor whatIsACarvedArtworkProblem = new QueryProcessor();
        whatIsACarvedArtworkProblem.setVerbosity(3);
        whatIsACarvedArtworkProblem.setSbhlBackchain(true);
        whatIsACarvedArtworkProblem.setMaxBackchainDepth(1);
        // Request all solutions.
        whatIsACarvedArtworkProblem.nbrSolutionsRequested = null;
        try {
            whatIsACarvedArtworkProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = whatIsACarvedArtworkProblem.ask(whatIsACarvedArtworkString);
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
        QueryProcessor austinIsaHarborProblem = new QueryProcessor();
        austinIsaHarborProblem.setVerbosity(3);
        // Request all solutions.
        austinIsaHarborProblem.nbrSolutionsRequested = null;
        try {
            austinIsaHarborProblem.mt =
                CycAccess.current().getConstantByName("InferencePSC");
            ArrayList solutions = austinIsaHarborProblem.ask(austinIsaHarborString);
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
