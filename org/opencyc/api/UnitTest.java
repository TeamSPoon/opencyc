package org.opencyc.api;

import junit.framework.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.api</tt> package<p>
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

    public UnitTest(String name) {
        super(name);
    }

    public static void runTests() {
        TestSuite testSuite = new TestSuite();
        //testSuite.addTest(new UnitTest("testAsciiCycConnection"));
        //testSuite.addTest(new UnitTest("testBinaryCycConnection"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess1"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess1"));
        //testSuite.addTest(new UnitTest("testCycAccess2"));
        //testSuite.addTest(new UnitTest("testCycAccess3"));
        //testSuite.addTest(new UnitTest("testCycAccess4"));
        //testSuite.addTest(new UnitTest("testCycAccess5"));
        //testSuite.addTest(new UnitTest("testCycAccess6"));
        //testSuite.addTest(new UnitTest("testMakeValidConstantName"));
        TestResult testResult = new TestResult();
        testSuite.run(testResult);
    }

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        runTests();
    }

    public void testMakeValidConstantName () {
        System.out.println("**** testMakeValidConstantName ****");
        String candidateName = "abc";
        Assert.assertEquals(candidateName, CycConstant.makeValidConstantName(candidateName));
        candidateName = "()[]//abc";
        String expectedValidName = "______abc";
        Assert.assertEquals(expectedValidName, CycConstant.makeValidConstantName(candidateName));
        System.out.println("**** testMakeValidConstantName OK ****");
    }

    public void testAsciiCycConnection () {
        System.out.println("**** testAsciiCycConnection ****");


        CycConnection cycConnection = null;
        try {
            CycAccess cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                                CycConnection.DEFAULT_BASE_PORT,
                                                CycConnection.DEFAULT_COMMUNICATION_MODE,
                                                CycAccess.PERSISTENT_CONNECTION);
            cycConnection = cycAccess.cycConnection;
            //cycConnection.trace = true;
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // Test return of atom.
        String command = "(+ 2 3)";
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("5", response[1]);

        // Test return of string.
        command = "(quote " + '\"' + "abc" + '\"' + ")";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = "(quote (a b (c d (e) f)))";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test function evaluation.
        command = "(member? #$Dog '(#$DomesticPet #$Dog))";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("T", response[1]);

        // Test KB Ask.
        command = "(removal-ask '(#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt)";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertTrue(response[1] instanceof CycList);
        Assert.assertEquals("((((T T)) ((:GENLS (#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt :TRUE-DEF))))",
                            ((CycList) response[1]).cyclify());

        try {
            cycConnection.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycConnection OK ****");
    }

    public void testBinaryCycConnection () {
        System.out.println("**** testBinaryCycConnection ****");

        CycConnection cycConnection = null;
        try {
            cycConnection = new CycConnection(CycConnection.DEFAULT_HOSTNAME,
                                              3640,
                                              CycConnection.BINARY_MODE);
            //cycConnection.trace = true;
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Test return of atom.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("+"));
        command.add(new Long(2));
        command.add(new Long(3));
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("5", response[1].toString());

        // Test return of string.
        command = new CycList();
        command.add(CycSymbol.quote);
        command.add("abc");
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = new CycList();
        command.add(CycSymbol.quote);
        CycList cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycSymbol.makeCycSymbol("a"));
        cycList2.add(CycSymbol.makeCycSymbol("b"));
        CycList cycList3 = new CycList();
        cycList2.add(cycList3);
        cycList3.add(CycSymbol.makeCycSymbol("c"));
        cycList3.add(CycSymbol.makeCycSymbol("d"));
        CycList cycList4 = new CycList();
        cycList3.add(cycList4);
        cycList4.add(CycSymbol.makeCycSymbol("e"));
        cycList3.add(CycSymbol.makeCycSymbol("f"));
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test error return
        command = new CycList();
        command.add(CycSymbol.nil);
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals("(CYC-EXCEPTION MESSAGE \"Invalid API Request: NIL is not a valid API function symbol\")",
                            response[1].toString());

        try {
            cycConnection.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycConnection OK ****");
    }

    public void testAsciiCycAccess1 () {
        System.out.println("**** testAsciiCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      CycAccess.TRANSIENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        // getConstantByName.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByName("#$Dog");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("bd58daa0-9c29-11b1-9dad-c379636f7270", cycConstant.guid.toString());

        // getConstantByGuid.
        try {
            cycConstant = cycAccess.getConstantByGuid(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cycName());
        Assert.assertEquals("Dog", cycConstant.name);

        // getConstantById
        cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantById(23200);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cycName());
        Assert.assertEquals("Dog", cycConstant.name);
        Assert.assertEquals(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"),
                            cycConstant.guid);

        // getComment.
        String comment = null;
        try {
            comment = cycAccess.getComment(cycAccess.getConstantByName("#$Raindrop"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("The collection of drops of liquid water emitted by clouds in instances of #$RainProcess.",
                            comment);

        // getIsas.
        List isas = null;
        try {
            isas = cycAccess.getIsas(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        try {
            Assert.assertTrue(isas.contains(cycAccess.getConstantByName("OrganismClassificationType")));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 1 OK ****");
    }

    public void testBinaryCycAccess1 () {
        System.out.println("**** testBinaryCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.TRANSIENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        // getConstantByName.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByName("#$Dog");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("bd58daa0-9c29-11b1-9dad-c379636f7270", cycConstant.guid.toString());

        // getConstantByGuid.
        try {
            cycConstant = cycAccess.getConstantByGuid(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cycName());
        Assert.assertEquals("Dog", cycConstant.name);

        // getConstantById
        cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantById(23200);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cycName());
        Assert.assertEquals("Dog", cycConstant.name);
        Assert.assertEquals(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"),
                            cycConstant.guid);

        // getComment.
        String comment = null;
        try {
            comment = cycAccess.getComment(cycAccess.getConstantByName("#$Raindrop"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("The collection of drops of liquid water emitted by clouds in instances of #$RainProcess.",
                            comment);

        // getIsas.
        List isas = null;
        try {
            isas = cycAccess.getIsas(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        try {
            Assert.assertTrue(isas.contains(cycAccess.getConstantByName("OrganismClassificationType")));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 1 OK ****");
    }

    public void testCycAccess2 () {
        System.out.println("**** testCycAccess 2 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        // getGenls.
        List genls = null;
        try {
            genls = cycAccess.getGenls(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertEquals("(CanineAnimal DomesticatedAnimal)", genls.toString());

        // getGenlPreds.
        List genlPreds = null;
        try {
            genlPreds = cycAccess.getGenlPreds(cycAccess.getConstantByName("#$target"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlPreds);
        Assert.assertTrue((genlPreds.toString().equals("(preActors)")) ||
                          (genlPreds.toString().equals("(actors)")));

        // getArg1Formats.
        List arg1Formats = null;
        try {
            arg1Formats = cycAccess.getArg1Formats(cycAccess.getConstantByName("#$target"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("()", arg1Formats.toString());

        // getArg1Formats.
        arg1Formats = null;
        try {
            arg1Formats = cycAccess.getArg1Formats(cycAccess.getConstantByName("#$constantName"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("(SingleEntry)", arg1Formats.toString());


        // getArg2Formats.
        List arg2Formats = null;
        try {
            arg2Formats = cycAccess.getArg2Formats(cycAccess.getConstantByName("#$internalParts"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Formats);
        Assert.assertEquals("(SetTheFormat)", arg2Formats.toString());

        // getDisjointWiths.
        List disjointWiths = null;
        try {
            disjointWiths = cycAccess.getDisjointWiths(cycAccess.getConstantByName("#$Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(disjointWiths);
        Assert.assertEquals("(Animal)", disjointWiths.toString());

        // getCoExtensionals.
        List coExtensionals = null;
        try {
            coExtensionals = cycAccess.getCoExtensionals(cycAccess.getConstantByName("#$CycLTerm"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("(CycLExpression)", coExtensionals.toString());

        // getCoExtensionals.
        coExtensionals = null;
        try {
            coExtensionals = cycAccess.getCoExtensionals(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("()", coExtensionals.toString());

        // getArg1Isas.
        List arg1Isas = null;
        try {
            arg1Isas = cycAccess.getArg1Isas(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Isas);
        Assert.assertEquals("(Event)", arg1Isas.toString());

        // getArg2Isas.
        List arg2Isas = null;
        try {
            arg2Isas = cycAccess.getArg2Isas(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Isas);
        Assert.assertEquals("(SomethingExisting)", arg2Isas.toString());

        // getArgNIsas.
        List argNIsas = null;
        try {
            argNIsas = cycAccess.getArgNIsas(cycAccess.getConstantByName("#$doneBy"), 1);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argNIsas);
        Assert.assertEquals("(Event)", argNIsas.toString());

        // getArgNGenls.
        List argGenls = null;
        try {
            argGenls = cycAccess.getArgNGenls(cycAccess.getConstantByName("#$superTaxons"), 2);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argGenls);
        Assert.assertEquals("(Organism-Whole)", argGenls.toString());

        // isCollection.
        boolean answer = false;
        try {
            answer = cycAccess.isCollection(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isCollection.
        answer = true;
        try {
            answer = cycAccess.isCollection(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // isBinaryPredicate.
        answer = false;
        try {
            answer = cycAccess.isBinaryPredicate(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isBinaryPredicate.
        answer = true;
        try {
            answer = cycAccess.isBinaryPredicate(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // getPluralGeneratedPhrase.
        String phrase = null;
        try {
            phrase = cycAccess.getPluralGeneratedPhrase(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("dogs (domesticated animals)", phrase);

        // getSingularGeneratedPhrase.
        phrase = null;
        try {
            phrase = cycAccess.getSingularGeneratedPhrase(cycAccess.getConstantByName("#$Brazil"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("Brazil (country)", phrase);

        // getGeneratedPhrase.
        phrase = null;
        try {
            phrase = cycAccess.getGeneratedPhrase(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("doer", phrase);



        //--------- last.
        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testCycAccess 2 OK ****");
    }

    public void testCycAccess3 () {
        System.out.println("**** testCycAccess 3 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getParaphrase.
        String phrase = null;
        try {
            //cycAccess.traceOn();
            phrase = cycAccess.getParaphrase(cycAccess.makeCycList("(#$isa #$Brazil #$Country)"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("Brazil (country) is a country (political entity)", phrase);

        // getComment.
        String comment = null;
        try {
            comment = cycAccess.getComment(cycAccess.getConstantByName("#$MonaLisa-Painting"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("Mona Lisa, the #$OilPainting by #$LeonardoDaVinci-TheArtist", comment);

        // getIsas.
        List isas = null;
        try {
            isas = cycAccess.getIsas(cycAccess.getConstantByName("#$Brazil"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        Assert.assertEquals("(Entity IndependentCountry PublicConstant)", isas.toString());

        // getGenls.
        List genls = null;
        try {
            genls = cycAccess.getGenls(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertEquals("(CanineAnimal DomesticatedAnimal)", genls.toString());

        // getMinGenls.
        List minGenls = null;
        try {
            minGenls = cycAccess.getMinGenls(cycAccess.getConstantByName("#$Lion"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(minGenls);
        Assert.assertTrue(minGenls instanceof CycList);
        minGenls = ((CycList) minGenls).sort();
        Assert.assertEquals("(FelidaeFamily)", minGenls.toString());

        // getSpecs.
        List specs = null;
        try {
            specs = cycAccess.getSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(specs);
        Assert.assertTrue(specs instanceof CycList);
        specs = ((CycList) specs).sort();
        Assert.assertEquals("(Coyote-Animal Dog Fox Jackal Wolf)", specs.toString());

        // getMaxSpecs.
        List maxSpecs = null;
        try {
            maxSpecs = cycAccess.getMaxSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(maxSpecs);
        Assert.assertTrue(maxSpecs instanceof CycList);
        maxSpecs = ((CycList) maxSpecs).sort();
        Assert.assertEquals("(Coyote-Animal Dog Fox Jackal Wolf)", maxSpecs.toString());

        // getGenlSiblings.
        List genlSiblings = null;
        try {
            genlSiblings = cycAccess.getGenlSiblings(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlSiblings);
        Assert.assertTrue(genlSiblings instanceof CycList);
        genlSiblings = ((CycList) genlSiblings).sort();
        Assert.assertEquals("(Animal DomesticPet FemaleAnimal JuvenileAnimal)", genlSiblings.toString());

        // getSiblings.
        List siblings = null;
        try {
            siblings = cycAccess.getSiblings(cycAccess.getConstantByName("#$Dog"));
            Assert.assertNotNull(siblings);
            Assert.assertTrue(siblings instanceof CycList);
            Assert.assertTrue(siblings.contains(cycAccess.getConstantByName("Goose-Domestic")));
            Assert.assertTrue(siblings.contains(cycAccess.getConstantByName("Goat-Domestic")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getSpecSiblings.
        List specSiblings = null;
        try {
            specSiblings = cycAccess.getSpecSiblings(cycAccess.getConstantByName("#$Dog"));
            Assert.assertNotNull(specSiblings);
            Assert.assertTrue(specSiblings instanceof CycList);
            Assert.assertTrue(specSiblings.contains(cycAccess.getConstantByName("Goose-Domestic")));
            Assert.assertTrue(specSiblings.contains(cycAccess.getConstantByName("Goat-Domestic")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllGenls.
        List allGenls = null;
        try {
            allGenls = cycAccess.getAllGenls(cycAccess.getConstantByName("#$ExistingObjectType"));
            Assert.assertNotNull(allGenls);
            Assert.assertTrue(allGenls instanceof CycList);
            Assert.assertTrue(allGenls.contains(cycAccess.getConstantByName("ObjectType")));
            Assert.assertTrue(allGenls.contains(cycAccess.getConstantByName("Thing")));
            }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllSpecs.
        List allSpecs = null;
        try {
            allSpecs = cycAccess.getAllSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
            Assert.assertNotNull(allSpecs);
            Assert.assertTrue(allSpecs instanceof CycList);
            Assert.assertTrue(allSpecs.contains(cycAccess.getConstantByName("Jackal")));
            Assert.assertTrue(allSpecs.contains(cycAccess.getConstantByName("Retriever-Dog")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllGenlsWrt.
        List allGenlsWrt = null;
        try {
            allGenlsWrt = cycAccess.getAllGenlsWrt(cycAccess.getConstantByName("Dog"),
                                                   cycAccess.getConstantByName("#$Animal"));
            Assert.assertNotNull(allGenlsWrt);
            Assert.assertTrue(allGenlsWrt instanceof CycList);
            Assert.assertTrue(allGenlsWrt.contains(cycAccess.getConstantByName("TameAnimal")));
            Assert.assertTrue(allGenlsWrt.contains(cycAccess.getConstantByName("AirBreathingVertebrate")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllDependentSpecs.
        List allDependentSpecs = null;
        try {
            allDependentSpecs = cycAccess.getAllDependentSpecs(cycAccess.getConstantByName("CanineAnimal"));
            Assert.assertNotNull(allDependentSpecs);
            Assert.assertTrue(allDependentSpecs instanceof CycList);
            Assert.assertTrue(allDependentSpecs.contains(cycAccess.getConstantByName("Wolf-Gray")));
            Assert.assertTrue(allDependentSpecs.contains(cycAccess.getConstantByName("Wolf")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getSampleLeafSpecs.
        List sampleLeafSpecs = null;
        try {
            sampleLeafSpecs = cycAccess.getSampleLeafSpecs(cycAccess.getConstantByName("CanineAnimal"), 3);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(sampleLeafSpecs);
        Assert.assertTrue(sampleLeafSpecs instanceof CycList);
        //System.out.println("sampleLeafSpecs: " + sampleLeafSpecsArrayList);
        Assert.assertTrue(sampleLeafSpecs.size() > 0);

        // isSpecOf.
        boolean answer = true;
        try {
            answer = cycAccess.isSpecOf(cycAccess.getConstantByName("#$Dog"),
                                        cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isGenlOf.
        answer = true;
        try {
            answer = cycAccess.isGenlOf(cycAccess.getConstantByName("CanineAnimal"),
                                        cycAccess.getConstantByName("Wolf"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areTacitCoextensional.
        answer = true;
        try {
            answer = cycAccess.areTacitCoextensional(cycAccess.getConstantByName("SinglePurposeDevice"),
                                                     cycAccess.getConstantByName("PhysicalDevice"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areAssertedCoextensional.
        answer = true;
        try {
            answer = cycAccess.areAssertedCoextensional(cycAccess.getConstantByName("SinglePurposeDevice"),
                                                        cycAccess.getConstantByName("PhysicalDevice"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areIntersecting.
        answer = true;
        //cycAccess.traceOn();
        try {
            answer = cycAccess.areIntersecting(cycAccess.getConstantByName("DomesticatedAnimal"),
                                               cycAccess.getConstantByName("TameAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);
        //cycAccess.traceOff();

        // areHierarchical.
        answer = true;
        try {
            answer = cycAccess.areHierarchical(cycAccess.getConstantByName("CanineAnimal"),
                                               cycAccess.getConstantByName("Wolf"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        //--------- last.
        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testCycAccess 3 OK ****");
    }

    public void testCycAccess4 () {
        System.out.println("**** testCycAccess 4 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getWhyGenl.
        CycList whyGenl = null;
        try {
            whyGenl = cycAccess.getWhyGenl(cycAccess.getConstantByName("Dog"),
                                           cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyGenl);
        CycConstant whyGenlFirst = (CycConstant) ((CycList) ((CycList) whyGenl.first()).first()).second();
        CycConstant whyGenlLast = (CycConstant) ((CycList) ((CycList) whyGenl.last()).first()).third();
        try {
            Assert.assertEquals(cycAccess.getConstantByName("Dog"), whyGenlFirst);
            Assert.assertEquals(cycAccess.getConstantByName("Animal"), whyGenlLast);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // getWhyGenlParaphrase.
        ArrayList whyGenlParaphrase = null;
        try {
            whyGenlParaphrase = cycAccess.getWhyGenlParaphrase(cycAccess.getConstantByName("Dog"),
                                                               cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyGenlParaphrase);
        //System.out.println(whyGenlParaphrase);
        Assert.assertTrue(whyGenlParaphrase.contains("a domesticated animal (tame animal) is a kind of tame animal"));

        // getWhyCollectionsIntersect.
        List whyCollectionsIntersect = null;
        try {
            whyCollectionsIntersect =
                cycAccess.getWhyCollectionsIntersect(cycAccess.getConstantByName("DomesticatedAnimal"),
                                                     cycAccess.getConstantByName("NonPersonAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyCollectionsIntersect);
        Assert.assertTrue(whyCollectionsIntersect instanceof CycList);
        CycList expectedWhyCollectionsIntersect =
            cycAccess.makeCycList("(((#$genls #$DomesticatedAnimal #$TameAnimal) :TRUE) " +
                                  "((#$genls #$TameAnimal #$NonPersonAnimal) :TRUE))");
        Assert.assertEquals(expectedWhyCollectionsIntersect.toString(), whyCollectionsIntersect.toString());
        Assert.assertEquals(expectedWhyCollectionsIntersect, whyCollectionsIntersect);

        // getWhyCollectionsIntersectParaphrase.
        ArrayList whyCollectionsIntersectParaphrase = null;
        try {
            //cycAccess.traceOn();
            whyCollectionsIntersectParaphrase =
                cycAccess.getWhyCollectionsIntersectParaphrase(cycAccess.getConstantByName("DomesticatedAnimal"),
                                                               cycAccess.getConstantByName("NonPersonAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyCollectionsIntersectParaphrase);
        //System.out.println(whyCollectionsIntersectParaphrase);
        Assert.assertTrue(whyCollectionsIntersectParaphrase.contains("a domesticated animal (tame animal) is a kind of tame animal"));

        // getCollectionLeaves.
        List collectionLeaves = null;
        try {
            collectionLeaves = cycAccess.getCollectionLeaves(cycAccess.getConstantByName("CanineAnimal"));
            Assert.assertNotNull(collectionLeaves);
            Assert.assertTrue(collectionLeaves instanceof CycList);
            Assert.assertTrue(collectionLeaves.contains(cycAccess.getConstantByName("RedWolf")));
            Assert.assertTrue(collectionLeaves.contains(cycAccess.getConstantByName("SanJoaquinKitFox")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getLocalDisjointWith.
        List localDisjointWiths = null;
        try {
            localDisjointWiths = cycAccess.getLocalDisjointWith(cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(localDisjointWiths, cycAccess.makeCycList("(#$Animal)"));

        // areDisjoint.
        boolean answer = true;
        try {
            answer = cycAccess.areDisjoint(cycAccess.getConstantByName("Animal"),
                                           cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // getMinIsas.
        List minIsas = null;
        try {
            minIsas = cycAccess.getMinIsas(cycAccess.getConstantByName("Wolf"));
            Assert.assertTrue(minIsas.contains(cycAccess.getConstantByName("OrganismClassificationType")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getInstances.
        List instances = null;
        try {
            instances = cycAccess.getInstances(cycAccess.getConstantByName("Animal"));
            Assert.assertTrue(instances instanceof CycList);
            Assert.assertTrue(((CycList) instances).contains(cycAccess.getConstantByName("Bigfoot")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getInstanceSiblings.
        List instanceSiblings = null;
        try {
            instanceSiblings = cycAccess.getInstanceSiblings(cycAccess.getConstantByName("Bigfoot"));
            Assert.assertTrue(instanceSiblings.contains(cycAccess.getConstantByName("Oceanus-TheTitan")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllIsa.
        List allIsas = null;
        try {
            //cycAccess.traceOn();
            allIsas = cycAccess.getAllIsa(cycAccess.getConstantByName("Animal"));
            //System.out.println(allIsas);
            Assert.assertTrue(allIsas.contains(cycAccess.getConstantByName("#$OrganismClassificationType")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllInstances.
        List allInstances = null;
        try {
            allInstances = cycAccess.getAllInstances(cycAccess.getConstantByName("Plant"));
            Assert.assertTrue(allInstances.contains(cycAccess.getConstantByName("TreatyOak")));
            Assert.assertTrue(allInstances.contains(cycAccess.getConstantByName("BurningBushOldTestament")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // isa.
        answer = true;
        try {
            answer = cycAccess.isa(cycAccess.getConstantByName("TreatyOak"),
                                   cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);



        //--------- last.
        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testCycAccess 4 OK ****");
    }

    public void testCycAccess5 () {
        System.out.println("**** testCycAccess 5 ****");
        CycConstant.resetCache();

        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // createNewPermanent.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.name);

        // kill.
        try {
            cycAccess.kill(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // assertComment.
        cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.name);

        CycConstant baseKb = null;
        try {
            baseKb = cycAccess.getConstantByName("BaseKB");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("BaseKB", baseKb.name);
        String assertedComment = "A test comment";
        try {
            cycAccess.assertComment(cycConstant, assertedComment, baseKb);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        String comment = null;
        try {
            comment = cycAccess.getComment(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(assertedComment, comment);

        try {
            cycAccess.kill(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestConstant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // isValidConstantName.
        boolean answer = true;
        try {
            answer = cycAccess.isValidConstantName("abc");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName(" abc");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName("[abc]");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // createMicrotheory.
        CycConstant mt = null;
        ArrayList genlMts = new ArrayList();
        try {
            genlMts.add(cycAccess.getConstantByName("ModernMilitaryMt"));
            mt = cycAccess.createMicrotheory("CycAccessTestMt",
                                             "a unit test comment for the CycAccessTestMt microtheory.",
                                             cycAccess.getConstantByName("Microtheory"),
                                             genlMts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(mt);
        try {
            cycAccess.kill(mt);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // createMicrotheorySystem.
        CycConstant[] mts = {null, null, null};
        genlMts = new ArrayList();
        try {
            genlMts.add(cycAccess.getConstantByName("ModernMilitaryMt"));
            mts = cycAccess.createMicrotheorySystem("CycAccessTest",
                                                    "a unit test comment for the CycAccessTestMt microtheory.",
                                                    genlMts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(mts.length == 3);
        Assert.assertNotNull(mts[0]);
        Assert.assertEquals("#$CycAccessTestMt", mts[0].cycName());
        Assert.assertNotNull(mts[1]);
        Assert.assertEquals("#$CycAccessTestVocabMt", mts[1].cycName());
        Assert.assertNotNull(mts[2]);
        Assert.assertEquals("#$CycAccessTestDataMt", mts[2].cycName());
        try {
            cycAccess.kill(mts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // askWithVariable
        try {
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)");
            CycVariable variable = CycVariable.makeCycVariable("?WHAT");
            mt = CycAccess.current().getConstantByName("EverythingPSC");
            CycList response = CycAccess.current().askWithVariable(query, variable, mt);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains(CycAccess.current().getConstantByName("#$UniversityOfTexasAtAustin")));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // isQueryTrue
        try {
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfAustinTX)");
            mt = CycAccess.current().getConstantByName("EverythingPSC");
            Assert.assertTrue(CycAccess.current().isQueryTrue(query, mt));
            query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfHoustonTX)");
            Assert.assertTrue(! CycAccess.current().isQueryTrue(query, mt));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // countAllInstances
        try {
            cycAccess = CycAccess.current();
            Assert.assertTrue(cycAccess.countAllInstances(cycAccess.getConstantByName("Country"),
                                                          cycAccess.getConstantByName("WorldGeographyMt")) > 0);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        //--------- last.
        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testCycAccess 5 OK ****");
    }

    public void testCycAccess6 () {
        System.out.println("**** testCycAccess 6 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // Test common constants.
        try {
            Assert.assertEquals(cycAccess.getConstantByName("and"), CycAccess.and);
            Assert.assertEquals(cycAccess.getConstantByName("BaseKB"), CycAccess.baseKB);
            Assert.assertEquals(cycAccess.getConstantByName("BinaryPredicate"), CycAccess.binaryPredicate);
            Assert.assertEquals(cycAccess.getConstantByName("comment"), CycAccess.comment);
            Assert.assertEquals(cycAccess.getConstantByName("different"), CycAccess.different);
            Assert.assertEquals(cycAccess.getConstantByName("elementOf"), CycAccess.elementOf);
            Assert.assertEquals(cycAccess.getConstantByName("genlMt"), CycAccess.genlMt);
            Assert.assertEquals(cycAccess.getConstantByName("genls"), CycAccess.genls);
            Assert.assertEquals(cycAccess.getConstantByName("isa"), CycAccess.isa);
            Assert.assertEquals(cycAccess.getConstantByName("numericallyEqual"), CycAccess.numericallyEqual);
            Assert.assertEquals(cycAccess.getConstantByName("or"), CycAccess.or);
            Assert.assertEquals(cycAccess.getConstantByName("PlusFn"), CycAccess.plusFn);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Test getBackchainRules.
        try {
            //cycAccess.traceOn();
            CycList backchainRules =
                cycAccess.getBackchainRules(cycAccess.getConstantByName("#$doneBy"),
                                            cycAccess.getConstantByName("HumanActivitiesMt"));
            Assert.assertNotNull(backchainRules);
            //for (int i = 0; i < backchainRules.size(); i++)
            //    System.out.println(((CycList) backchainRules.get(i)).cyclify());
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Test getForwardChainRules.
        try {
            cycAccess.traceOn();
            CycList forwardChainRules =
                cycAccess.getForwardChainRules(cycAccess.getConstantByName("#$doneBy"),
                                            cycAccess.getConstantByName("HumanActivitiesMt"));
            Assert.assertNotNull(forwardChainRules);
            for (int i = 0; i < forwardChainRules.size(); i++)
                System.out.println(((CycList) forwardChainRules.get(i)).cyclify());
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }


        //--------- last.
        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testCycAccess 6 OK ****");
    }

}





