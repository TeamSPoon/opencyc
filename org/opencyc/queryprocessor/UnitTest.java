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
            testSuite.addTest(new UnitTest("testHashJoiner"));
            //testSuite.addTest(new UnitTest("testQueryProcessor1"));
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
            Assert.fail(e.getMessage());
        }
        ArrayList queryLiterals = new ArrayList();
        queryLiterals.add(queryLiteral1);
        queryLiterals.add(queryLiteral2);
        queryLiterals.add(queryLiteral3);
        queryLiterals.add(queryLiteral4);
        LiteralAsker literalAsker = new LiteralAsker();
        literalAsker.setVerbosity(5);
        ArrayList bindingSets = null;
        try {
            bindingSets = literalAsker.ask(queryLiterals, mt);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        HashJoiner hashJoiner = new HashJoiner();
        hashJoiner.setVerbosity(5);
        BindingSet joinedBindingSets = null;
        try {
            joinedBindingSets = hashJoiner.join(bindingSets);
        }
        catch (Exception e) {
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
        System.out.println(europeanCathedralsString);
        QueryProcessor europeanCathedralsQuery = new QueryProcessor();
        europeanCathedralsQuery.setVerbosity(9);
        // Request two solutions.
        // europeanCathedralsQuery.nbrSolutionsRequested = new Integer(2);
        // Request all solutions.
        europeanCathedralsQuery.nbrSolutionsRequested = null;
        try {
            europeanCathedralsQuery.mt =
                CycAccess.current().getConstantByName("TourAndVacationPackageItinerariesMt");
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        ArrayList solutions = europeanCathedralsQuery.ask(CycAccess.current().makeCycList(europeanCathedralsString));
        Assert.assertNotNull(solutions);

        System.out.println("** testQueryProcessor1 OK **");
    }
}