package org.opencyc.api;

import junit.framework.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;
import org.opencyc.cycobject.*;
import org.opencyc.cycagent.*;
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

    public static final String myAgentName = "ApiTestAgent";
    public static final String cycProxyAgentName = "CycProxyAgent";
    //public static final int agentCommunity = AgentCommunityAdapter.COABS_AGENT_COMMUNITY;
    public static final int agentCommunity = AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY;

    /**
     * Indicates the use of a local CycConnection object to connect with
     * a Cyc server.
     */
    public static final int LOCAL_CYC_CONNECTION = 1;

    /**
     * Indicates the use of a local RemoteCycConnection object to connect with
     * a remote CycConnection object via a CoABS grid or FIPA-OS agent community.
     * The remote CycConnection has a connection with a Cyc server.
     */
    public static final int REMOTE_CYC_CONNECTION = 2;

    //public static int connectionMode = REMOTE_CYC_CONNECTION;
    public static int connectionMode = LOCAL_CYC_CONNECTION;

    /**
     * Indicates whether unit tests should be performed only in binary api mode.
     */
    //public static boolean performOnlyBinaryApiModeTests = false;
    public static boolean performOnlyBinaryApiModeTests = true;

    /**
     * Creates a <tt>UnitTest</tt> object with the given name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Returns the test suite.
     *
     * @return the test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();

        testSuite.addTest(new UnitTest("testAsciiCycConnection"));
        testSuite.addTest(new UnitTest("testBinaryCycConnection1"));
        testSuite.addTest(new UnitTest("testBinaryCycConnection2"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess1"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess1"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess2"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess2"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess3"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess3"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess4"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess4"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess5"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess5"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess6"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess6"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess7"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess7"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess8"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess8"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess9"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess9"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess10"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess10"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess11"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess12"));
        testSuite.addTest(new UnitTest("testMakeValidConstantName"));

        return testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        if (connectionMode == REMOTE_CYC_CONNECTION &&
            agentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
            System.exit(0);
    }

    /**
     * Compares expected object to the test object without causing a unit test failure, reporting if
     * the parameters are not equal.
     *
     * @param expectedObject the expected object
     * @param testObject the test object
     */
    public static void nofailAssertEquals (Object expectedObject, Object testObject) {
        if (! expectedObject.equals(testObject))
            System.out.println("Expected <" + expectedObject + "> \nfound <" + testObject);
    }

    /**
     * Reports if the given boolen expression is false, without causing a unit test failure.
     *
     * @param expression the boolean expression expected to be true
     */
    public static void nofailAssertTrue (boolean testExpression, String message) {
        if (! testExpression)
            System.out.println("Test expression not true\n" + message);
    }

    /**
     * Tests the makeValidConstantName method.
     */
    public void testMakeValidConstantName () {
        System.out.println("\n**** testMakeValidConstantName ****");
        String candidateName = "abc";
        Assert.assertEquals(candidateName, CycConstant.makeValidConstantName(candidateName));
        candidateName = "()[]//abc";
        String expectedValidName = "______abc";
        Assert.assertEquals(expectedValidName, CycConstant.makeValidConstantName(candidateName));
        System.out.println("**** testMakeValidConstantName OK ****");
    }

    /**
     * Tests the fundamental aspects of the ascii api connection to the OpenCyc server.
     */
    public void testAsciiCycConnection () {
        if (connectionMode == REMOTE_CYC_CONNECTION) {
            System.out.println("\n**** bypassing testAsciiCycConnection in remote usage ****");
            return;
        }
        System.out.println("\n**** testAsciiCycConnection ****");
        CycConnectionInterface cycConnection = null;
        try {
            CycAccess cycAccess = null;
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
            cycConnection = cycAccess.cycConnection;
            //cycConnection.trace = true;
        }
        catch (ConnectException e) {
            System.out.println("Could not connect to host " + CycConnection.DEFAULT_HOSTNAME +
                               " port " + CycConnection.DEFAULT_BASE_PORT);
            Assert.fail(e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // Test return of atom.
        String command = "(+ 2 3)";
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(new Integer(5), response[1]);

        // Test return of string.
        command = "(quote " + '\"' + "abc" + '\"' + ")";
        try {
            //cycConnection.trace = 1;
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = "(quote (a b (c d (e) f)))";
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test return of improper list.
        command = "(quote (a . b))";
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A . B)", response[1].toString());

        // Test function evaluation.
        command = "(member? #$Dog '(#$DomesticPet #$Dog))";
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(CycObjectFactory.t, response[1]);

        // Test KB Ask.
        command = "(removal-ask '(#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt)";
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertTrue(response[1] instanceof CycList);
        Assert.assertEquals("((((T . T)) ((:GENLS (#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt :TRUE-DEF))))",
                            ((CycList) response[1]).cyclify());

        cycConnection.close();
        System.out.println("**** testAsciiCycConnection OK ****");
    }

    /**
     * Tests the fundamental aspects of the binary (cfasl) api connection to the
     * OpenCyc server.
     */
    public void testBinaryCycConnection1 () {
        System.out.println("\n**** testBinaryCycConnection1 ****");
        CycAccess cycAccess = null;
        CycConnectionInterface cycConnection = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION) {
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            }
            else if (connectionMode == REMOTE_CYC_CONNECTION) {
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
                //System.out.println("RemoteCycConnection created");
            }
            else
                Assert.fail("Invalid connection mode " + connectionMode);
            cycConnection = cycAccess.cycConnection;
            //cycConnection.trace = true;
        }
        catch (ConnectException e) {
            System.out.println("Could not connect to host " + CycConnection.DEFAULT_HOSTNAME +
                               " port " + CycConnection.DEFAULT_BASE_PORT);
            Assert.fail(e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        //cycAccess.traceOn();
        // turn on api if not on.
        String script = "(pwhen (cand (boundp '*eval-in-api?*) \n" +
                        "             (cnot *eval-in-api?*)) \n" +
                        "       (csetq *eval-in-api?* t))";
        try {
            cycAccess.converseVoid(script);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // Test return of atom.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("+"));
        command.add(new Integer(2));
        command.add(new Integer(3));
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(new Integer(5), response[1]);

        // Test return of string.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        command.add("abc");
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        CycList cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycObjectFactory.makeCycSymbol("a"));
        cycList2.add(CycObjectFactory.makeCycSymbol("b"));
        CycList cycList3 = new CycList();
        cycList2.add(cycList3);
        cycList3.add(CycObjectFactory.makeCycSymbol("c"));
        cycList3.add(CycObjectFactory.makeCycSymbol("d"));
        CycList cycList4 = new CycList();
        cycList3.add(cycList4);
        cycList4.add(CycObjectFactory.makeCycSymbol("e"));
        cycList3.add(CycObjectFactory.makeCycSymbol("f"));
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test return of improper list.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycObjectFactory.makeCycSymbol("A"));
        cycList2.setDottedElement(CycObjectFactory.makeCycSymbol("B"));
        try {
            //cycConnection.trace = true;
            response = cycConnection.converse(command);
            //cycConnection.trace = false;
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A . B)", response[1].toString());

        // Test error return
        command = new CycList();
        command.add(CycObjectFactory.nil);
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(response[1].toString().indexOf("Operator NIL is not defined in the API") > -1);

        cycConnection.close();
        System.out.println("**** testBinaryCycConnection1 OK ****");
    }

    /**
     * Tests the fundamental aspects of the binary (cfasl) api connection to the OpenCyc server.
     * CycAccess is set to null;
     */
    public void testBinaryCycConnection2 () {
        if (connectionMode == REMOTE_CYC_CONNECTION)
            return;
        System.out.println("\n**** testBinaryCycConnection2 ****");
        CycConnection cycConnection = null;
        try {
            cycConnection = new CycConnection((CycAccess) null);
            //cycConnection.trace = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // Test return of atom.
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("+"));
        command.add(new Integer(2));
        command.add(new Integer(3));
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            cycConnection.close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(new Integer(5), response[1]);

        // Test return of string.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        command.add("abc");
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            cycConnection.close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        CycList cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycObjectFactory.makeCycSymbol("a"));
        cycList2.add(CycObjectFactory.makeCycSymbol("b"));
        CycList cycList3 = new CycList();
        cycList2.add(cycList3);
        cycList3.add(CycObjectFactory.makeCycSymbol("c"));
        cycList3.add(CycObjectFactory.makeCycSymbol("d"));
        CycList cycList4 = new CycList();
        cycList3.add(cycList4);
        cycList4.add(CycObjectFactory.makeCycSymbol("e"));
        cycList3.add(CycObjectFactory.makeCycSymbol("f"));
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            cycConnection.close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test return of improper list.
        command = new CycList();
        command.add(CycObjectFactory.quote);
        cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycObjectFactory.makeCycSymbol("A"));
        cycList2.setDottedElement(CycObjectFactory.makeCycSymbol("B"));
        try {
            //cycConnection.trace = true;
            response = cycConnection.converse(command);
            //cycConnection.trace = false;
        }
        catch (Exception e) {
            cycConnection.close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A . B)", response[1].toString());

        // Test error return
        command = new CycList();
        command.add(CycObjectFactory.nil);
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            cycConnection.close();
            Assert.fail(e.toString());
        }
        // various error messages to effect that NIL is not defined in the API.
        Assert.assertTrue(response[1].toString().indexOf("API") > -1);

        cycConnection.close();
        System.out.println("**** testBinaryCycConnection2 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess1 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.TRANSIENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess1(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 1 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess1 () {
        System.out.println("\n**** testBinaryCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.TRANSIENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nException: " + e.getMessage());
            Assert.fail(e.toString());
        }

        //cycAccess.traceOnDetailed();
        doTestCycAccess1(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 1 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess1(CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {
            // turn on api if not on.
            String script = "(pwhen (cand (boundp '*eval-in-api?*) \n" +
                            "             (cnot *eval-in-api?*)) \n" +
                            "       (csetq *eval-in-api?* t))";
            cycAccess.converseVoid(script);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        CycObjectFactory.resetCycConstantCaches();
        // getConstantByName.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByName("#$Dog");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("bd58daa0-9c29-11b1-9dad-c379636f7270", cycConstant.getGuid().toString());

        // getConstantByGuid.
        try {
            cycConstant = cycAccess.getConstantByGuid(CycObjectFactory.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cyclify());
        Assert.assertEquals("Dog", cycConstant.getName());

        // getConstantById
        cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByGuid(CycObjectFactory.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"));
            cycConstant = cycAccess.getConstantById(cycConstant.getId());
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cyclify());
        Assert.assertEquals("Dog", cycConstant.getName());
        Assert.assertEquals(CycObjectFactory.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"),
                            cycConstant.getGuid());

        // getComment.
        String comment = null;
        try {
            CycConstant raindrop = cycAccess.getKnownConstantByGuid("bd58bec6-9c29-11b1-9dad-c379636f7270");
            comment = cycAccess.getComment(raindrop);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("The collection of drops of liquid water emitted by clouds in instances of #$RainProcess.",
                            comment);

        // getIsas.
        List isas = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            isas = cycAccess.getIsas(dog);
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        try {
            CycConstant organismClassificationType =
                cycAccess.getKnownConstantByGuid("bd58dfe4-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(isas.contains(organismClassificationType));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess2 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 2 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        doTestCycAccess2 (cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 2 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess2 () {
        System.out.println("\n**** testBinaryCycAccess 2 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.CONCURRENT_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        //cycAccess.traceOnDetailed();
        doTestCycAccess2(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 2 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess2 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();

        // getGenls.
        List genls = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            genls = cycAccess.getGenls(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertTrue(genls.toString().indexOf("CanineAnimal") > -1);
        Assert.assertTrue(genls.toString().indexOf("DomesticatedAnimal") > -1);

        // getGenlPreds.
        List genlPreds = null;
        try {
            CycConstant target = cycAccess.getKnownConstantByGuid("c10afaed-9c29-11b1-9dad-c379636f7270");
            genlPreds = cycAccess.getGenlPreds(target);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlPreds);
        Assert.assertTrue((genlPreds.toString().equals("(preActors)")) ||
                          (genlPreds.toString().equals("(actors)")));

        // getAllGenlPreds.
        List allGenlPreds = null;
        try {
            CycConstant target = cycAccess.getKnownConstantByGuid("c10afaed-9c29-11b1-9dad-c379636f7270");
            allGenlPreds = cycAccess.getAllGenlPreds(target);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(allGenlPreds);
        Assert.assertTrue(allGenlPreds.size() > 2);

        // getArg1Formats.
        List arg1Formats = null;
        try {
            CycConstant target = cycAccess.getKnownConstantByGuid("c10afaed-9c29-11b1-9dad-c379636f7270");
            arg1Formats = cycAccess.getArg1Formats(target);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("(SetTheFormat)", arg1Formats.toString());

        // getArg1Formats.
        arg1Formats = null;
        try {
            CycConstant constantName = cycAccess.getKnownConstantByGuid("bd7183b0-9c29-11b1-9dad-c379636f7270");
            arg1Formats = cycAccess.getArg1Formats(constantName);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("(SingleEntry)", arg1Formats.toString());


        // getArg2Formats.
        List arg2Formats = null;
        try {
            CycConstant internalParts = cycAccess.getKnownConstantByGuid("bd58cf63-9c29-11b1-9dad-c379636f7270");
            arg2Formats = cycAccess.getArg2Formats(internalParts);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Formats);
        Assert.assertEquals("(SetTheFormat)", arg2Formats.toString());

        // getDisjointWiths.
        List disjointWiths = null;
        try {
            CycConstant vegetableMatter =
                cycAccess.getKnownConstantByGuid("bd58c455-9c29-11b1-9dad-c379636f7270");
            disjointWiths = cycAccess.getDisjointWiths(vegetableMatter);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(disjointWiths);
        Assert.assertTrue(disjointWiths.toString().indexOf("AnimalBLO") > 0);

        // getCoExtensionals.
        List coExtensionals = null;
        try {
            //cycAccess.traceOn();
            CycConstant cycLTerm = cycAccess.getKnownConstantByGuid("c107fffb-9c29-11b1-9dad-c379636f7270");
            coExtensionals = cycAccess.getCoExtensionals(cycLTerm);
            //cycAccess.traceOff();
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("(CycLExpression)", coExtensionals.toString());

        // getCoExtensionals.
        coExtensionals = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            coExtensionals = cycAccess.getCoExtensionals(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("()", coExtensionals.toString());

        // getArg1Isas.
        List arg1Isas = null;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            arg1Isas = cycAccess.getArg1Isas(doneBy);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Isas);
        Assert.assertEquals("(Event)", arg1Isas.toString());

        // getArg2Isas.
        List arg2Isas = null;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            arg2Isas = cycAccess.getArg2Isas(doneBy);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Isas);
        Assert.assertEquals("(SomethingExisting)", arg2Isas.toString());

        // getArgNIsas.
        List argNIsas = null;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            argNIsas = cycAccess.getArgNIsas(doneBy, 1);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argNIsas);
        Assert.assertEquals("(Event)", argNIsas.toString());

        // getArgNGenls.
        List argGenls = null;
        try {
            CycConstant superTaxons = cycAccess.getKnownConstantByGuid("bd58e36e-9c29-11b1-9dad-c379636f7270");
            argGenls = cycAccess.getArgNGenls(superTaxons, 2);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argGenls);
        Assert.assertEquals("(Organism-Whole)", argGenls.toString());

        // isCollection.
        boolean answer = false;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isCollection(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isCollection.
        answer = true;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isCollection(doneBy);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // isBinaryPredicate.
        answer = false;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isBinaryPredicate(doneBy);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isBinaryPredicate.
        answer = true;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isBinaryPredicate(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // getPluralGeneratedPhrase.
        String phrase = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            phrase = cycAccess.getPluralGeneratedPhrase(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertTrue(phrase.indexOf("dogs (") > -1);

        // getSingularGeneratedPhrase.
        phrase = null;
        try {
            CycConstant brazil = cycAccess.getKnownConstantByGuid("bd588f01-9c29-11b1-9dad-c379636f7270");
            phrase = cycAccess.getSingularGeneratedPhrase(brazil);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertTrue(phrase.indexOf("Brazil (") > -1);

        // getGeneratedPhrase.
        phrase = null;
        try {
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            phrase = cycAccess.getGeneratedPhrase(doneBy);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
       Assert.assertNotNull(phrase);
        Assert.assertTrue(phrase.indexOf("doer (") > -1);
        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess3 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 3 ****");
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

        doTestCycAccess3 (cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 3 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess3 () {
        System.out.println("\n**** testBinaryCycAccess 3 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        doTestCycAccess3(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 3 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess3 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();
        // getComment.
        String comment = null;
        try {
            CycConstant brazil = cycAccess.getKnownConstantByGuid("bd588f01-9c29-11b1-9dad-c379636f7270");
            comment = cycAccess.getComment(brazil);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("An instance of #$IndependentCountry.  #$Brazil is the " +
                            "largest country in South America, and is bounded on the " +
                            "northwest by #$Colombia; on the north by #$Venezuela, " +
                            "#$Guyana, #$Suriname, and #$FrenchGuiana; on the east by " +
                            "the #$AtlanticOcean; on the south by #$Uruguay; on the " +
                            "southwest by #$Argentina and #$Paraguay; and on the west " +
                            "by #$Bolivia and #$Peru.", comment);

        // getIsas.
        List isas = null;
        try {
            CycConstant brazil = cycAccess.getKnownConstantByGuid("bd588f01-9c29-11b1-9dad-c379636f7270");
            isas = cycAccess.getIsas(brazil);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        Assert.assertTrue(isas.toString().indexOf("Entity") > 0);
        Assert.assertTrue(isas.toString().indexOf("IndependentCountry") > 0);

        // getGenls.
        List genls = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            genls = cycAccess.getGenls(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertTrue(genls.toString().indexOf("CanineAnimal") > -1);
        Assert.assertTrue(genls.toString().indexOf("DomesticatedAnimal") > -1);

        // getMinGenls.
        List minGenls = null;
        try {
            CycConstant lion = cycAccess.getKnownConstantByGuid("bd58c467-9c29-11b1-9dad-c379636f7270");
            minGenls = cycAccess.getMinGenls(lion);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(minGenls);
        Assert.assertTrue(minGenls instanceof CycList);
        minGenls = ((CycList) minGenls).sort();
        Assert.assertEquals("(FelidaeFamily)", minGenls.toString());

        // getMinGenls mt.
        minGenls = null;
        try {
            CycConstant lion = cycAccess.getKnownConstantByGuid("bd58c467-9c29-11b1-9dad-c379636f7270");
            // #$BiologyVocabularyMt
            minGenls = cycAccess.getMinGenls(lion, cycAccess.getKnownConstantByGuid("bdd51776-9c29-11b1-9dad-c379636f7270"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(minGenls);
        Assert.assertTrue(minGenls instanceof CycList);
        minGenls = ((CycList) minGenls).sort();
        Assert.assertEquals("(FelidaeFamily)", minGenls.toString());

        // getSpecs.
        List specs = null;
        try {
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            specs = cycAccess.getSpecs(canineAnimal);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(specs);
        Assert.assertTrue(specs instanceof CycList);
        specs = ((CycList) specs).sort();
        Assert.assertEquals("(Coyote-Animal Dog Fox Jackal Wolf)", specs.toString());

        // getMaxSpecs.
        List maxSpecs = null;
        try {
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            maxSpecs = cycAccess.getMaxSpecs(canineAnimal);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(maxSpecs);
        Assert.assertTrue(maxSpecs instanceof CycList);
        maxSpecs = ((CycList) maxSpecs).sort();
        Assert.assertTrue(maxSpecs.toString().indexOf("Coyote-Animal") > 0);
        Assert.assertTrue(maxSpecs.toString().indexOf("Dog") > 0);
        Assert.assertTrue(maxSpecs.toString().indexOf("Fox") > 0);
        Assert.assertTrue(maxSpecs.toString().indexOf("Jackal") > 0);
        Assert.assertTrue(maxSpecs.toString().indexOf("Wolf") > 0);

        // getGenlSiblings.
        List genlSiblings = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            genlSiblings = cycAccess.getGenlSiblings(dog);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlSiblings);
        Assert.assertTrue(genlSiblings instanceof CycList);
        genlSiblings = ((CycList) genlSiblings).sort();
        Assert.assertTrue(genlSiblings.toString().indexOf("Individual") > -1);
        Assert.assertTrue(genlSiblings.toString().indexOf("JuvenileAnimal") > -1);

        /* long running.
        // getSiblings.
        List siblings = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            siblings = cycAccess.getSiblings(dog);
            Assert.assertNotNull(siblings);
            Assert.assertTrue(siblings instanceof CycList);
            CycConstant gooseDomestic = cycAccess.getKnownConstantByGuid("bd5ca864-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(siblings.contains(gooseDomestic));
            CycConstant goatDomestic = cycAccess.getKnownConstantByGuid("bd58e278-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(siblings.contains(goatDomestic));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        // getSpecSiblings.
        List specSiblings = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            specSiblings = cycAccess.getSpecSiblings(dog);
            Assert.assertNotNull(specSiblings);
            Assert.assertTrue(specSiblings instanceof CycList);
            CycConstant gooseDomestic = cycAccess.getKnownConstantByGuid("bd5ca864-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(specSiblings.contains(gooseDomestic));
            CycConstant goatDomestic = cycAccess.getKnownConstantByGuid("bd58e278-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(specSiblings.contains(goatDomestic));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        */

        // getAllGenls.
        List allGenls = null;
        try {
            CycConstant existingObjectType = cycAccess.getKnownConstantByGuid("bd65d880-9c29-11b1-9dad-c379636f7270");
            allGenls = cycAccess.getAllGenls(existingObjectType);
            Assert.assertNotNull(allGenls);
            Assert.assertTrue(allGenls instanceof CycList);
            CycConstant objectType = cycAccess.getKnownConstantByGuid("bd58ab9d-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allGenls.contains(objectType));
            Assert.assertTrue(allGenls.contains(CycAccess.thing));
            }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getAllSpecs.
        List allSpecs = null;
        try {
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            allSpecs = cycAccess.getAllSpecs(canineAnimal);
            Assert.assertNotNull(allSpecs);
            Assert.assertTrue(allSpecs instanceof CycList);
            CycConstant jackal = cycAccess.getKnownConstantByGuid("bd58c2de-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allSpecs.contains(jackal));
            CycConstant retrieverDog = cycAccess.getKnownConstantByGuid("bd58e24b-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allSpecs.contains(retrieverDog));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getAllGenlsWrt.
        List allGenlsWrt = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            allGenlsWrt = cycAccess.getAllGenlsWrt(dog, animal);
            Assert.assertNotNull(allGenlsWrt);
            Assert.assertTrue(allGenlsWrt instanceof CycList);
            CycConstant tameAnimal = cycAccess.getKnownConstantByGuid("c0fcd4a1-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allGenlsWrt.contains(tameAnimal));
            CycConstant airBreathingVertebrate = cycAccess.getKnownConstantByGuid("bef7c9c1-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allGenlsWrt.contains(airBreathingVertebrate));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getAllDependentSpecs.
        List allDependentSpecs = null;
        try {
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            allDependentSpecs = cycAccess.getAllDependentSpecs(canineAnimal);
            Assert.assertNotNull(allDependentSpecs);
            CycConstant wolfGray = cycAccess.getKnownConstantByGuid("becf2f96-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allDependentSpecs instanceof CycList);
            Assert.assertTrue(allDependentSpecs.contains(wolfGray));
            CycConstant wolf = cycAccess.getKnownConstantByGuid("bd58c31f-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allDependentSpecs.contains(wolf));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        // getSampleLeafSpecs.
        List sampleLeafSpecs = null;
        try {
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            sampleLeafSpecs = cycAccess.getSampleLeafSpecs(canineAnimal, 3);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(sampleLeafSpecs);
        Assert.assertTrue(sampleLeafSpecs instanceof CycList);
        //System.out.println("sampleLeafSpecs: " + sampleLeafSpecsArrayList);
        Assert.assertTrue(sampleLeafSpecs.size() > 0);

        // isSpecOf.
        boolean answer = true;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isSpecOf(dog, animal);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isGenlOf.
        answer = true;
        try {
            CycConstant wolf = cycAccess.getKnownConstantByGuid("bd58c31f-9c29-11b1-9dad-c379636f7270");
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isGenlOf(canineAnimal, wolf);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areTacitCoextensional.
        answer = true;
        try {
            CycConstant singlePurposeDevice = cycAccess.getKnownConstantByGuid("bd5897aa-9c29-11b1-9dad-c379636f7270");
            CycConstant physicalDevice = cycAccess.getKnownConstantByGuid("bd58c72f-9c29-11b1-9dad-c379636f7270");
            //cycAccess.traceOn();
            answer = cycAccess.areTacitCoextensional(singlePurposeDevice, physicalDevice);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areAssertedCoextensional.
        answer = true;
        try {
            CycConstant singlePurposeDevice = cycAccess.getKnownConstantByGuid("bd5897aa-9c29-11b1-9dad-c379636f7270");
            CycConstant physicalDevice = cycAccess.getKnownConstantByGuid("bd58c72f-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.areAssertedCoextensional(singlePurposeDevice, physicalDevice);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areIntersecting.
        answer = true;
        //cycAccess.traceOn();
        try {
            CycConstant domesticatedAnimal = cycAccess.getKnownConstantByGuid("c10c22cd-9c29-11b1-9dad-c379636f7270");
            CycConstant tameAnimal = cycAccess.getKnownConstantByGuid("c0fcd4a1-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.areIntersecting(domesticatedAnimal, tameAnimal);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);
        //cycAccess.traceOff();

        // areHierarchical.
        answer = true;
        try {
            CycConstant wolf = cycAccess.getKnownConstantByGuid("bd58c31f-9c29-11b1-9dad-c379636f7270");
            CycConstant canineAnimal = cycAccess.getKnownConstantByGuid("bd58d044-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.areHierarchical(canineAnimal, wolf);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // getParaphrase.
        String phrase = null;
        try {
            //cycAccess.traceOn();
            phrase = cycAccess.getParaphrase(cycAccess.makeCycList("(#$isa #$Brazil #$Country)"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertTrue(phrase.indexOf("Brazil (") > -1);

        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess4 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 4 ****");
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

        doTestCycAccess4(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 4 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess4 () {
        System.out.println("\n**** testBinaryCycAccess 4 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        //cycAccess.traceOnDetailed();
        doTestCycAccess4(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 4 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess4 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();
        // getWhyGenl.
        CycList whyGenl = null;
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            whyGenl = cycAccess.getWhyGenl(dog, animal);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyGenl);
        System.out.println("whyGenl "+ whyGenl);
        /*
        CycSymbol whyGenlFirst = (CycSymbol) ((CycList) ((CycList) whyGenl.first()).first()).second();
        CycSymbol whyGenlLast = (CycSymbol) ((CycList) ((CycList) whyGenl.last()).first()).third();
        try {
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            Assert.assertEquals(dog, whyGenlFirst);
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            Assert.assertEquals(animal, whyGenlLast);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        */
        // getWhyCollectionsIntersect.
        List whyCollectionsIntersect = null;
        try {
            CycConstant domesticatedAnimal = cycAccess.getKnownConstantByGuid("c10c22cd-9c29-11b1-9dad-c379636f7270");
            CycConstant nonPersonAnimal = cycAccess.getKnownConstantByGuid("bd58e066-9c29-11b1-9dad-c379636f7270");
            whyCollectionsIntersect =
                cycAccess.getWhyCollectionsIntersect(domesticatedAnimal, nonPersonAnimal);
            Assert.assertNotNull(whyCollectionsIntersect);
            Assert.assertTrue(whyCollectionsIntersect instanceof CycList);
            System.out.println("whyCollectionsIntersect " + whyCollectionsIntersect);
            CycList expectedWhyCollectionsIntersect =
                cycAccess.makeCycList("(((#$genls #$DomesticatedAnimal #$TameAnimal) :TRUE) " +
                                      "((#$genls #$TameAnimal #$NonPersonAnimal) :TRUE))");
            /**
            Assert.assertEquals(expectedWhyCollectionsIntersect.toString(), whyCollectionsIntersect.toString());
            Assert.assertEquals(expectedWhyCollectionsIntersect, whyCollectionsIntersect);
            */
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getCollectionLeaves.
        List collectionLeaves = null;
        try {
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            collectionLeaves = cycAccess.getCollectionLeaves(animal);
            Assert.assertNotNull(collectionLeaves);
            Assert.assertTrue(collectionLeaves instanceof CycList);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getLocalDisjointWith.
        List localDisjointWiths = null;
        try {
            CycConstant vegetableMatter =
                cycAccess.getKnownConstantByGuid("bd58c455-9c29-11b1-9dad-c379636f7270");
            localDisjointWiths = cycAccess.getDisjointWiths(vegetableMatter);
            Assert.assertNotNull(localDisjointWiths);
            Assert.assertTrue(localDisjointWiths.toString().indexOf("AnimalBLO") > 0);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // areDisjoint.
        boolean answer = true;
        try {
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            CycConstant plant = cycAccess.getKnownConstantByGuid("bd58c6e1-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.areDisjoint(animal, plant);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // getMinIsas.
        List minIsas = null;
        try {
            CycConstant wolf = cycAccess.getKnownConstantByGuid("bd58c31f-9c29-11b1-9dad-c379636f7270");
            minIsas = cycAccess.getMinIsas(wolf);
            CycConstant organismClassificationType = cycAccess.getKnownConstantByGuid("bd58dfe4-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(minIsas.contains(organismClassificationType));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getInstances.
        List instances = null;
        try {
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            instances = cycAccess.getInstances(animal);
            Assert.assertTrue(instances instanceof CycList);
            CycConstant bigfoot = cycAccess.getKnownConstantByGuid("bdc6bd06-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(((CycList) instances).contains(bigfoot));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getAllIsa.
        List allIsas = null;
        try {
            //cycAccess.traceOn();
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            allIsas = cycAccess.getAllIsa(animal);
            //System.out.println(allIsas);
            CycConstant organismClassificationType = cycAccess.getKnownConstantByGuid("bd58dfe4-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allIsas.contains(organismClassificationType));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // getAllInstances.
        List allInstances = null;
        try {
            CycConstant plant = cycAccess.getKnownConstantByGuid("bd58c6e1-9c29-11b1-9dad-c379636f7270");
            allInstances = cycAccess.getAllInstances(plant);
            CycConstant treatyOak = cycAccess.getKnownConstantByGuid("bfc0aa80-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allInstances.contains(treatyOak));
            CycConstant burningBushOldTestament = cycAccess.getKnownConstantByGuid("be846866-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(allInstances.contains(burningBushOldTestament));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // isa.
        answer = true;
        try {
            CycConstant plant = cycAccess.getKnownConstantByGuid("bd58c6e1-9c29-11b1-9dad-c379636f7270");
            CycConstant treatyOak = cycAccess.getKnownConstantByGuid("bfc0aa80-9c29-11b1-9dad-c379636f7270");
            answer = cycAccess.isa(treatyOak, plant);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // getWhyCollectionsIntersectParaphrase.
        ArrayList whyCollectionsIntersectParaphrase = null;
        try {
            //cycAccess.traceOn();
            CycConstant domesticatedAnimal = cycAccess.getKnownConstantByGuid("c10c22cd-9c29-11b1-9dad-c379636f7270");
            CycConstant nonPersonAnimal = cycAccess.getKnownConstantByGuid("bd58e066-9c29-11b1-9dad-c379636f7270");
            System.out.println("bypassing getWhyCollectionsIntersectParaphrase");
            /*
            whyCollectionsIntersectParaphrase =
                cycAccess.getWhyCollectionsIntersectParaphrase(domesticatedAnimal, nonPersonAnimal);
                */
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        /*
        Assert.assertNotNull(whyCollectionsIntersectParaphrase);
        String oneExpectedCollectionsIntersectParaphrase =
            "every domesticated animal (tame animal) is a tame animal";
        //System.out.println(whyCollectionsIntersectParaphrase);
        Assert.assertTrue(whyCollectionsIntersectParaphrase.contains(oneExpectedCollectionsIntersectParaphrase));
*/
        // getWhyGenlParaphrase.
        ArrayList whyGenlParaphrase = null;
        try {
            //cycAccess.traceOn();
            CycConstant dog = cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            CycConstant animal = cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            System.out.println("bypassing getWhyGenlParaphrase");
            /*
            whyGenlParaphrase = cycAccess.getWhyGenlParaphrase(dog, animal);
            */
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        /*
        Assert.assertNotNull(whyGenlParaphrase);
        String oneExpectedGenlParaphrase =
            "every tame animal is a non-human animal";

        //for (int i = 0; i < whyGenlParaphrase.size(); i++) {
        //    System.out.println(whyGenlParaphrase.get(i));
        //}

        Assert.assertTrue(whyGenlParaphrase.contains(oneExpectedGenlParaphrase));
*/



        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess5 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 5 ****");
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

        doTestCycAccess5(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 5 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess5 () {
        System.out.println("\n**** testBinaryCycAccess 5 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        //cycAccess.traceOnDetailed();
        doTestCycAccess5(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 5 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess5 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();
        //cycAccess.traceOn();
        // createNewPermanent.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.getName());

        // kill.
        try {
            cycAccess.kill(cycConstant);
        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        // assertComment.
        cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.getName());

        CycConstant baseKb = null;
        try {
            baseKb = cycAccess.getConstantByName("BaseKB");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("BaseKB", baseKb.getName());
        String assertedComment = "A test comment";
        try {
            //cycAccess.traceOn();
            cycAccess.assertComment(cycConstant, assertedComment, baseKb);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        String comment = null;
        try {
            comment = cycAccess.getComment(cycConstant);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertEquals(assertedComment, comment);

        try {
            cycAccess.kill(cycConstant);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestConstant"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // isValidConstantName.
        boolean answer = true;
        try {
            answer = cycAccess.isValidConstantName("abc");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName(" abc");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName("[abc]");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // isConstantNameAvailable

        answer = true;
        try {
            answer = cycAccess.isConstantNameAvailable("agent");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        answer = false;
        try {
            answer = cycAccess.isConstantNameAvailable("myAgent");
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // createMicrotheory.
        CycConstant mt = null;
        ArrayList genlMts = new ArrayList();
        try {
            CycConstant modernMilitaryMt = cycAccess.getKnownConstantByGuid("c040a2f0-9c29-11b1-9dad-c379636f7270");
            CycConstant microtheory = cycAccess.getKnownConstantByGuid("bd5880d5-9c29-11b1-9dad-c379636f7270");
            genlMts.add(modernMilitaryMt);
            mt = cycAccess.createMicrotheory("CycAccessTestMt",
                                             "a unit test comment for the CycAccessTestMt microtheory.",
                                             microtheory,
                                             genlMts);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(mt);
        try {
            cycAccess.kill(mt);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // createMicrotheorySystem.
        CycConstant[] mts = {null, null, null};
        genlMts = new ArrayList();
        try {
            genlMts.add(cycAccess.baseKB);
            mts = cycAccess.createMicrotheorySystem("CycAccessTest",
                                                    "a unit test comment for the CycAccessTestMt microtheory.",
                                                    genlMts);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        Assert.assertTrue(mts.length == 3);
        Assert.assertNotNull(mts[0]);
        Assert.assertEquals("#$CycAccessTestMt", mts[0].cyclify());
        Assert.assertNotNull(mts[1]);
        Assert.assertEquals("#$CycAccessTestVocabMt", mts[1].cyclify());
        Assert.assertNotNull(mts[2]);
        Assert.assertEquals("#$CycAccessTestDataMt", mts[2].cyclify());
        try {
            cycAccess.kill(mts);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // askWithVariable
        try {
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)");
            CycVariable variable = CycObjectFactory.makeCycVariable("?WHAT");
            CycConstant everythingPSC = CycAccess.current().getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
            mt = everythingPSC;
            CycList response = CycAccess.current().askWithVariable(query, variable, mt);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains(CycAccess.current().getConstantByName("#$UniversityOfTexasAtAustin")));
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // askWithVariables
        try {
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation ?WHAT ?WHERE)");
            ArrayList variables = new ArrayList();
            variables.add(CycObjectFactory.makeCycVariable("?WHAT"));
            variables.add(CycObjectFactory.makeCycVariable("?WHERE"));
            CycConstant universeDataMt = CycAccess.current().getKnownConstantByGuid("bd58d0f3-9c29-11b1-9dad-c379636f7270");
            CycList response = CycAccess.current().askWithVariables(query, variables, universeDataMt);
            Assert.assertNotNull(response);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // isQueryTrue
        try {
            //cycAccess.traceOn();
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfAustinTX)");
            CycConstant everythingPSC = CycAccess.current().getKnownConstantByGuid("be7f041b-9c29-11b1-9dad-c379636f7270");
            mt = everythingPSC;
            Assert.assertTrue(CycAccess.current().isQueryTrue(query, mt));
            query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfHoustonTX)");
            Assert.assertTrue(! CycAccess.current().isQueryTrue(query, mt));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // countAllInstances
        try {
            cycAccess = CycAccess.current();
            CycConstant country = cycAccess.getKnownConstantByGuid("bd588879-9c29-11b1-9dad-c379636f7270");
            CycConstant worldGeographyMt = cycAccess.getKnownConstantByGuid("bfaac020-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(cycAccess.countAllInstances(country, worldGeographyMt) > 0);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess6 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 6 ****");
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

        doTestCycAccess6(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 6 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess6 () {
        System.out.println("\n**** testBinaryCycAccess 6 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
       }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        //cycAccess.traceOnDetailed();
        doTestCycAccess6(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 6 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess6 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();
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
            Assert.assertEquals(cycAccess.getConstantByName("numericallyEquals"), CycAccess.numericallyEqual);
            Assert.assertEquals(cycAccess.getConstantByName("or"), CycAccess.or);
            Assert.assertEquals(cycAccess.getConstantByName("PlusFn"), CycAccess.plusFn);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // Test isBackchainRequired, isBackchainEncouraged, isBackchainDiscouraged, isBackchainForbidden
        try {
            CycConstant keRequirement = cycAccess.getKnownConstantByGuid("c1141606-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(cycAccess.isBackchainRequired(keRequirement, cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainEncouraged(keRequirement, cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainDiscouraged(keRequirement, cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainForbidden(keRequirement, cycAccess.baseKB));

            CycConstant nearestIsa = cycAccess.getKnownConstantByGuid("bf411eed-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(! cycAccess.isBackchainRequired(nearestIsa, cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainEncouraged(nearestIsa, cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainDiscouraged(nearestIsa, cycAccess.baseKB));
            Assert.assertTrue(cycAccess.isBackchainForbidden(nearestIsa, cycAccess.baseKB));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

/*
        // Test getBackchainRules.
        try {

            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            CycConstant humanActivitiesMt = cycAccess.getKnownConstantByGuid("bd58fe73-9c29-11b1-9dad-c379636f7270");
            CycList backchainRules =
                cycAccess.getBackchainRules(doneBy, humanActivitiesMt);
            Assert.assertNotNull(backchainRules);
            //for (int i = 0; i < backchainRules.size(); i++)
            //    System.out.println(((CycList) backchainRules.get(i)).cyclify());
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // Test getForwardChainRules.
        try {
            //cycAccess.traceOn();
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            CycConstant humanActivitiesMt = cycAccess.getKnownConstantByGuid("bd58fe73-9c29-11b1-9dad-c379636f7270");
            CycList forwardChainRules =
                cycAccess.getForwardChainRules(doneBy,humanActivitiesMt);
            Assert.assertNotNull(forwardChainRules);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
*/
        // setSymbolValue, getSymbolValue
        try {
            CycSymbol a = CycObjectFactory.makeCycSymbol("a");
            cycAccess.setSymbolValue(a, new Integer(1));
            Assert.assertEquals(new Integer(1), cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, "abc");
            Assert.assertEquals("abc", cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, CycObjectFactory.t);
            Assert.assertEquals(CycObjectFactory.t, cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, CycObjectFactory.nil);
            Assert.assertEquals(CycObjectFactory.nil, cycAccess.getSymbolValue(a));
            //cycAccess.traceOnDetailed();
            CycConstant brazil = cycAccess.getConstantByName("#$Brazil");
            cycAccess.setSymbolValue(a, brazil);
            Assert.assertEquals(brazil, cycAccess.getSymbolValue(a));
            CycList valueList1 = cycAccess.makeCycList("(QUOTE (#$France #$Brazil))");
            CycList valueList2 = cycAccess.makeCycList("(#$France #$Brazil)");
            cycAccess.setSymbolValue(a, valueList1);
            Assert.assertEquals(valueList2, cycAccess.getSymbolValue(a));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // Test getCycNartById
        Integer nartId = new Integer(1);
        try {
            CycNart nart1 = cycAccess.getCycNartById(nartId);
            Assert.assertNotNull(nart1);
            Assert.assertNotNull(nart1.getFunctor());
            Assert.assertTrue(nart1.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart1.getArguments());
            Assert.assertTrue(nart1.getArguments() instanceof CycList);
            //System.out.println(nart1.cyclify());
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // Narts in a list.
        try {
            //cycAccess.traceOn();
            CycNart nart1 = cycAccess.getCycNartById(nartId);
            CycNart nart2 = cycAccess.getCycNartById(nartId);
            Assert.assertEquals(nart1, nart2);
            CycList valueList = new CycList();
            valueList.add(CycObjectFactory.quote);
            CycList nartList = new CycList();
            valueList.add(nartList);
            nartList.add(nart1);
            nartList.add(nart2);
            CycSymbol a = CycObjectFactory.makeCycSymbol("a");
            cycAccess.setSymbolValue(a, valueList);
            Object object = cycAccess.getSymbolValue(a);
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof CycList);
            CycList nartList1 = (CycList) object;
            Object element1 = nartList1.first();
            Assert.assertTrue((element1 instanceof CycNart) || (element1 instanceof CycList));
            if (element1 instanceof CycList)
                element1 = CycNart.coerceToCycNart(element1);
            CycNart nart3 = (CycNart) element1;
            Assert.assertNotNull(nart3.getFunctor());
            Assert.assertTrue(nart3.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart3.getArguments());
            Assert.assertTrue(nart3.getArguments() instanceof CycList);
            Object element2 = nartList1.second();
            Assert.assertTrue((element2 instanceof CycNart) || (element2 instanceof CycList));
            if (element2 instanceof CycList)
                element2 = CycNart.coerceToCycNart(element2);
            CycNart nart4 = (CycNart) element2;
            Assert.assertNotNull(nart4.getFunctor());
            Assert.assertTrue(nart4.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart4.getArguments());
            Assert.assertTrue(nart4.getArguments() instanceof CycList);
            if (cycAccess.getCommunicationMode() == CycConnection.BINARY_MODE) {
                Assert.assertEquals(nart1.cyclify(), nart3.cyclify());
                Assert.assertEquals(nart1.cyclify(), nart4.cyclify());
            }
            else {
                Assert.assertEquals(nart1.toString().toUpperCase(), nart3.toString().toUpperCase());
                Assert.assertEquals(nart1.toString().toUpperCase(), nart4.toString().toUpperCase());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // isWellFormedFormula
        try {
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Animal)")));
            // Not true, but still well formed.
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Plant)")));
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls ?X #$Animal)")));
            Assert.assertTrue(! cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Brazil)")));
            Assert.assertTrue(! cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls ?X #$Brazil)")));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // isEvaluatablePredicate
        try {
            Assert.assertTrue(cycAccess.isEvaluatablePredicate(CycAccess.different));
            CycConstant doneBy = cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(! cycAccess.isEvaluatablePredicate(doneBy));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        // hasSomePredicateUsingTerm
        try {
            CycConstant algeria = cycAccess.getKnownConstantByGuid("bd588c92-9c29-11b1-9dad-c379636f7270");
            CycConstant percentOfRegionIs = cycAccess.getKnownConstantByGuid("bfb0c6e5-9c29-11b1-9dad-c379636f7270");
            CycConstant ciaWorldFactbook1995Mt = cycAccess.getKnownConstantByGuid("c0a41a91-9c29-11b1-9dad-c379636f7270");
            CycConstant InferencePSC = cycAccess.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");


            Assert.assertTrue(cycAccess.hasSomePredicateUsingTerm(percentOfRegionIs,
                                                                  algeria,
                                                                  new Integer(1),
                                                                  ciaWorldFactbook1995Mt));

            Assert.assertTrue(cycAccess.hasSomePredicateUsingTerm(percentOfRegionIs,
                                                                  algeria,
                                                                  new Integer(1),
                                                                  InferencePSC));
            Assert.assertTrue(! cycAccess.hasSomePredicateUsingTerm(percentOfRegionIs,
                                                                    algeria,
                                                                    new Integer(2),
                                                                    ciaWorldFactbook1995Mt));
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }

        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess7 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 7 ****");
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

        doTestCycAccess7(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 7 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess7 () {
        System.out.println("\n**** testBinaryCycAccess 7 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);

            //cycAccess.traceOnDetailed();

            // turn on api if not on.
            String script = "(pwhen (cand (boundp '*eval-in-api?*) \n" +
                            "             (cnot *eval-in-api?*)) \n" +
                            "       (csetq *eval-in-api?* t))";
            cycAccess.converseVoid(script);
            script = "(clear-environment)";
            cycAccess.converseVoid(script);

            // Java ByteArray  and SubL byte-vector are used only in the binary api.
            script = "(csetq my-byte-vector (vector 0 1 2 3 4 255))";
            Object responseObject = cycAccess.converseObject(script);
            Assert.assertNotNull(responseObject);
            Assert.assertTrue(responseObject instanceof ByteArray);
            byte[] myBytes = {0, 1, 2, 3, 4, -1};
            ByteArray myByteArray = new ByteArray(myBytes);
            Assert.assertEquals(myByteArray, responseObject);
            CycList command = new CycList();
            command.add(CycObjectFactory.makeCycSymbol("equalp"));
            command.add(CycObjectFactory.makeCycSymbol("my-byte-vector"));
            CycList command1 = new CycList();
            command.add(command1);
            command1.add(CycObjectFactory.quote);
            command1.add(myByteArray);
            Assert.assertTrue(cycAccess.converseBoolean(command));
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }


        doTestCycAccess7(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 7 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess7 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        CycObjectFactory.resetCycConstantCaches();

        //cycAccess.traceOn();
        // SubL scripts
        try {
            String script;
            Object responseObject;
            CycList responseList;
            String responseString;
            boolean responseBoolean;

            // turn on api if not on.
            script = "(pwhen (cand (boundp '*eval-in-api?*) \n" +
                     "             (cnot *eval-in-api?*)) \n" +
                     "       (csetq *eval-in-api?* t))";
            cycAccess.converseVoid(script);

            // environment
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertNotNull(responseString);

            // definition
            script =
                "(define my-copy-tree (tree) \n" +
                "  (ret \n" +
                "    (fif (atom tree) \n" +
                "         tree \n" +
                "         ;; else \n" +
                "         (cons (my-copy-tree (first tree)) \n" +
                "               (my-copy-tree (rest tree))))))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-copy-tree"), responseObject);
            script = "(csetq a '(((#$Brazil #$Dog) #$Plant)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(((#$Brazil #$Dog) #$Plant))"),
                                responseList);
            script = "(csetq b (my-copy-tree a))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(((#$Brazil #$Dog) #$Plant))"),
                                responseList);
            script = "(cand (equal a b) (cnot (eq a b)))";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script =
                "(define my-floor (x y) \n" +
                "  (clet (results) \n" +
                "    (csetq results (multiple-value-list (floor x y))) \n" +
                "    (ret results)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-floor"), responseObject);
            script = "(my-floor 5 3)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);

            script =
                "(defmacro my-macro (a b c) \n" +
                "  (ret `(list ,a ,b ,c)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-macro"), responseObject);
            script = "(my-macro #$Dog #$Plant #$Brazil)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(#$Dog #$Plant #$Brazil)"), responseList);

            script =
                "(defmacro my-floor-macro (x y) \n" +
                "  (ret `(floor ,x ,y)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-floor-macro"), responseObject);
            script =
                "(define my-floor-macro-test (x y) \n" +
                "    (ret (multiple-value-list (my-floor-macro x y))))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-floor-macro-test"), responseObject);
            script = "(my-floor-macro-test 5 3)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);

            script =
                "(defmacro my-floor-macro (a b) \n" +
                "  (ret `(floor ,x ,y)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-floor-macro"), responseObject);
            script = "(my-floor-macro-test 5 3)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);

            // assignment
            script = "(csetq a '(1 #$Dog #$Plant))";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 #$Dog #$Plant)"), responseList);

            script = "(csetq a -1)";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(-1), responseObject);

            script =
                "(csetq a '(1 #$Dog #$Plant) \n" +
                "       b '(2 #$Dog #$Plant) \n" +
                "       c 3)";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 #$Dog #$Plant)"), responseList);
            script = "(symbol-value 'b)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(2 #$Dog #$Plant)"), responseList);
            script = "(symbol-value 'c)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(3), responseObject);

            script = "(clet ((a 0)) (cinc a) a)";
            Assert.assertEquals(new Integer(1), cycAccess.converseObject(script));

            script = "(clet ((a 0)) (cinc a 10) a)";
            Assert.assertEquals(new Integer(10), cycAccess.converseObject(script));

            script = "(clet ((a 0)) (cdec a) a)";
            Assert.assertEquals(new Integer(-1), cycAccess.converseObject(script));

            script = "(clet ((a 0)) (cdec a 10) a)";
            Assert.assertEquals(new Integer(-10), cycAccess.converseObject(script));

            script = "(cpush 4 a)";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(4 1 #$Dog #$Plant)"), responseList);

            script = "(cpop a)";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 #$Dog #$Plant)"), responseList);

            script = "(fi-set-parameter 'my-parm '(1 #$Dog #$Plant))";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'my-parm)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 #$Dog #$Plant)"), responseList);

            script =
                "(clet (a b) \n" +
                "  (csetq a '(1 2 3)) \n" +
                "  (csetq b (cpop a)) \n" +
                "  (list a b))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((2 3) (2 3))"), responseList);

            // boundp
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            Assert.assertTrue(! cycAccess.converseBoolean("(boundp 'a)"));
            cycAccess.converseVoid("(csetq a nil)");
            Assert.assertTrue(cycAccess.converseBoolean("(boundp 'a)"));

            // fi-get-parameter
            script = "(csetq my-parm '(2 #$Dog #$Plant))";
            cycAccess.converseVoid(script);
            script = "(fi-get-parameter 'my-parm)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(2 #$Dog #$Plant)"), responseList);

            // eval
            script = "(eval '(csetq a 4))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);
            script = "(eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);

            script = "(eval (list 'csetq 'a 5))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);
            script = "(eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);

            script = "(fi-eval '(csetq a 4))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);
            script = "(fi-eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);

            script = "(fi-eval (list 'csetq 'a 5))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);
            script = "(fi-eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);

            script = "(fi-local-eval '(csetq a 4))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);
            script = "(fi-local-eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);

            script = "(fi-local-eval (list 'csetq 'a 5))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);
            script = "(fi-local-eval 'a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(5), responseObject);


            // apply
            script = "(apply #'+ '(1 2 3))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(6), responseObject);

            script = "(apply #'+ 1 2 '(3 4 5))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(15), responseObject);

            script = "(apply (function +) '(1 2 3))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(6), responseObject);

            script = "(apply (function +) 1 2 '(3 4 5))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(15), responseObject);

            script = "(apply #'my-copy-tree '((1 (2 (3)))))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 (2 (3)))"),
                                responseList);

            // funcall
            script = "(funcall #'+ 1 2 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(6), responseObject);

            script = "(funcall (function +) 1 2 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(6), responseObject);

            script = "(funcall #'my-copy-tree (1 (2 (3))))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 (2 (3)))"),
                                responseList);

            // multiple values
            script = "(multiple-value-list (floor 5 3))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);

            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(cmultiple-value-bind (a b) \n" +
                     "    (floor 5 3) \n" +
                     "  (csetq answer (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseList);

            script =
                "(define my-multiple-value-fn (arg1 arg2) \n" +
                "  (ret (values arg1 arg2 (list arg1 arg2) 0)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-multiple-value-fn"), responseObject);

            script = "(my-multiple-value-fn #$Brazil #$Dog)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.getKnownConstantByGuid("bd588f01-9c29-11b1-9dad-c379636f7270"),
                                responseObject);

            script = "(cmultiple-value-bind (a b c d) \n" +
                     "    (my-multiple-value-fn #$Brazil #$Dog) \n" +
                     "  (csetq answer (list a b c d)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(#$Brazil #$Dog (#$Brazil #$Dog) 0)"), responseList);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(#$Brazil #$Dog (#$Brazil #$Dog) 0)"), responseList);

            // trace and untrace
            script = "(untrace)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(clear-trace-log)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(get-trace-log)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);

            script = "(trace my-copy-tree)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(my-copy-tree)"), responseObject);

            script = "(trace)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(my-copy-tree)"), responseObject);

            script = "(my-copy-tree '(1 (2 (3))))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 (2 (3)))"),
                                responseList);

            script = "(get-trace-log)";
            responseString = cycAccess.converseString(script);
            String expectedTraceLog =
                "\n" +
                "  0: (MY-COPY-TREE (1 (2 (3))))\n" +
                "    1: (MY-COPY-TREE 1)\n" +
                "    1: returned 1 \n" +
                "    1: (MY-COPY-TREE ((2 (3))))\n" +
                "      2: (MY-COPY-TREE (2 (3)))\n" +
                "        3: (MY-COPY-TREE 2)\n" +
                "        3: returned 2 \n" +
                "        3: (MY-COPY-TREE ((3)))\n" +
                "          4: (MY-COPY-TREE (3))\n" +
                "            5: (MY-COPY-TREE 3)\n" +
                "            5: returned 3 \n" +
                "            5: (MY-COPY-TREE NIL)\n" +
                "            5: returned NIL \n" +
                "          4: returned (3) \n" +
                "          4: (MY-COPY-TREE NIL)\n" +
                "          4: returned NIL \n" +
                "        3: returned ((3)) \n" +
                "      2: returned (2 (3)) \n" +
                "      2: (MY-COPY-TREE NIL)\n" +
                "      2: returned NIL \n" +
                "    1: returned ((2 (3))) \n" +
                "  0: returned (1 (2 (3))) \n";
            Assert.assertEquals(expectedTraceLog,
                                responseString);

            script = "(trace floor)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(FLOOR)"), responseObject);

            script = "(floor 5 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            script = "(get-trace-log)";
            responseString = cycAccess.converseString(script);
            expectedTraceLog =
                "\n" +
                "  0: (FLOOR 5 3)\n" +
                "  0: returned 1 2 \n";
            Assert.assertEquals(expectedTraceLog,
                                responseString);

            script = "(floor 5 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);
            script = "(floor 5 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            script = "(clear-trace-log)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(get-trace-log)";
            responseString = cycAccess.converseString(script);
            expectedTraceLog = "\n";
            Assert.assertEquals(expectedTraceLog,
                                responseString);

            script = "(trace)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(FLOOR MY-COPY-TREE)"), responseObject);

            script = "(untrace floor)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(FLOOR)"), responseObject);

            script = "(floor 5 3)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            script = "(get-trace-log)";
            responseString = cycAccess.converseString(script);
            expectedTraceLog = "\n";
            Assert.assertEquals(expectedTraceLog,
                                responseString);

            script = "(untrace)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            // arithmetic
            script = "(add1 2)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(3), responseObject);

            script = "(eq (add1 2) 3)";
            Assert.assertTrue(cycAccess.converseBoolean(script));

            script = "(sub1 10)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(9), responseObject);

            script = "(eq (sub1 10) 9)";
            Assert.assertTrue(cycAccess.converseBoolean(script));

            // sequence
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            script = "(csetq a nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(progn (csetq a nil) (csetq a (list a)) (csetq a (list a)))";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((nil))"), responseList);

            // sequence with variable bindings
            script = "(clet (a b) " +
                     "  (csetq a 1) " +
                     "  (csetq b (+ a 3)) " +
                     "  b)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(4), responseObject);

            script = "(clet ((a nil)) " +
                     "  (cpush 1 a) " +
                     "  a)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(1)"), responseList);

            script = "(clet (a b) " +
                     "  (csetq a '(1 2 3)) " +
                     "  (csetq b (cpop a)) " +
                     "  (list a b))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((2 3) (2 3))"), responseList);

            script = "(clet ((a 1) " +
                     "       (b (add1 a)) " +
                     "       (c (sub1 b))) " +
                     "  c)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            script = "(clet ((*my-parm* 1)) " +
                     "  (csetq a (fi-get-parameter '*my-parm*)) " +
                     "  a)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            // boolean expressions
            script = "(cand t nil t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(! responseBoolean);

            script = "(cand t t t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cand t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cand nil)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(! responseBoolean);

            script = "(cand t #$Dog)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.t, responseObject);

            script = "(cor t nil t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cor nil nil nil)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(! responseBoolean);

            script = "(cor t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cor nil)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(! responseBoolean);

            script = "(cor nil #$Plant)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.t, responseObject);

            script = "(cnot nil)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cnot t)";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(! responseBoolean);

            script = "(cnot (cand t nil))";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            script = "(cand (cnot nil) (cor t nil))";
            responseBoolean = cycAccess.converseBoolean(script);
            Assert.assertTrue(responseBoolean);

            // conditional sequencing
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(pcond ((eq 0 0) \n" +
                     "        (csetq answer \"clause 1 true\")) \n" +
                     "       ((> 1 4) \n" +
                     "        (csetq answer \"clause 2 true\")) \n" +
                     "       (t \n" +
                     "        (csetq answer \"clause 3 true\")))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);

            script = "(pcond ((eq 1 0) \n" +
                     "        (csetq answer \"clause 1 true\")) \n" +
                     "       ((> 5 4) \n" +
                     "        (csetq answer \"clause 2 true\")) \n" +
                     "       (t \n" +
                     "        (csetq answer \"clause 3 true\")))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);

            script = "(pcond ((eq 1 0) \n" +
                     "        (csetq answer \"clause 1 true\")) \n" +
                     "       ((> 1 4) \n" +
                     "        (csetq answer \"clause 2 true\")) \n" +
                     "       (t \n" +
                     "        (csetq answer \"clause 3 true\")))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 3 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 3 true", responseString);

            script = "(pif (string= \"abc\" \"abc\") \n" +
                     "     (csetq answer \"clause 1 true\") \n" +
                     "     ;; else \n" +
                     "     (csetq answer \"clause 2 true\"))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);

            script = "(pif (string> \"abc\" \"abc\") \n" +
                     "     (csetq answer \"clause 1 true\") \n" +
                     "     ;; else \n" +
                     "     (csetq answer \"clause 2 true\"))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);

            script = "(csetq answer \n" +
                     "       (fif (string= \"abc\" \"abc\") \n" +
                     "            \"clause 1 true\" \n" +
                     "            ;; else \n" +
                     "            \"clause 2 true\"))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);

            script = "(csetq answer \n" +
                     "       (fif (string> \"abc\" \"abc\") \n" +
                     "            \"clause 1 true\" \n" +
                     "            ;; else \n" +
                     "            \"clause 2 true\"))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);

            script = "(progn \n" +
                     "  (csetq answer \"clause 1 true\") \n" +
                     "  (pwhen (string= \"abc\" \"abc\") \n" +
                     "         (csetq answer \"clause 2 true\")))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);

            script = "(progn \n" +
                     "  (csetq answer \"clause 1 true\") \n" +
                     "  (pwhen (string> \"abc\" \"abc\") \n" +
                     "         (csetq answer \"clause 2 true\")))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);

            script = "(progn \n" +
                     "  (csetq answer \"clause 1 true\") \n" +
                     "  (punless (string> \"abc\" \"abc\") \n" +
                     "           (csetq answer \"clause 2 true\")))";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 2 true", responseString);

            script = "(progn \n" +
                     "  (csetq answer \"clause 1 true\") \n" +
                     "  (punless (string= \"abc\" \"abc\") \n" +
                     "           (csetq answer \"clause 2 true\")))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("clause 1 true", responseString);

            // iteration
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(clet ((i 11)) \n" +
                     "  (csetq answer -10) \n" +
                     "  ;;(break \"environment\") \n" +
                     "  (while (> i 0) \n" +
                     "    (cdec i) \n" +
                     "    (cinc answer)))";
            cycAccess.converseVoid(script);
            script = "(symbol-value 'answer)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);

            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(progn \n" +
                     "  (cdo ((x 0 (add1 x)) \n" +
                     "        (y (+ 0 1) (+ y 2)) \n" +
                     "        (z -10 (- z 1))) \n" +
                     "       ((> x 3)) \n" +
                     "    (cpush (list 'x x 'y y 'z z) answer)) \n" +
                     "  (csetq answer (nreverse answer)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((x 0 y 1 z -10) " +
                                                      " (x 1 y 3 z -11) " +
                                                      " (x 2 y 5 z -12) " +
                                                      " (x 3 y 7 z -13))"),
                                responseList);

            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(progn \n" +
                     "  (clet ((x '(1 2 3))) \n" +
                     "    (cdo nil ((null x) (csetq x 'y)) \n" +
                     "      (cpush x answer) \n" +
                     "      (cpop x)) \n" +
                     "    x) \n" +
                     "  (csetq answer (reverse answer)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((1 2 3) " +
                                                      " (2 3) " +
                                                      " (3))"),
                                responseList);

            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(cdolist (x '(1 2 3 4)) \n" +
                     "  (cpush x answer))";
            Assert.assertEquals(CycObjectFactory.nil, cycAccess.converseObject(script));
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(4 3 2 1)"), responseList);

            // mapping
            script = "(mapcar #'list '(a b c))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((a) (b) (c))"), responseList);

            script = "(mapcar #'list '(a b c) '(d e f))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("((a d) (b e) (c f))"), responseList);

            script = "(mapcar #'eq '(a b c) '(d b f))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(cycAccess.makeCycList("(nil t nil)"), responseList);

            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(csetq answer nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);

            script = "(csetq my-small-dictionary nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            // Wrap the dictionary assignment in a progn that returns nil, to avoid sending the
            // dictionary itself back to the client, where it is not supported.
            script = "(progn (csetq my-small-dictionary (new-dictionary #'eq 3)) nil)";
            responseObject = cycAccess.converseObject(script);
            script = "(progn \n" +
                     "  (dictionary-enter my-small-dictionary 'a 1) \n" +
                     "  (dictionary-enter my-small-dictionary 'b 2) \n" +
                     "  (dictionary-enter my-small-dictionary 'c 3))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("c"), responseObject);
            script =
                "(define my-mapdictionary-fn (key value) \n" +
                "  (cpush (list key value) answer) \n" +
                "  (ret nil))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-mapdictionary-fn"), responseObject);

            script = "(mapdictionary my-small-dictionary #'my-mapdictionary-fn)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(a 1)")));
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(b 2)")));
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(c 3)")));

            script = "(csetq my-large-dictionary nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(progn (csetq my-large-dictionary (new-dictionary #'eq 200)) nil)";
            responseObject = cycAccess.converseObject(script);
            script = "(clet ((cities (remove-duplicates \n" +
                     "                 (with-all-mts \n" +
                     "                   (instances #$IndependentCountry)))) \n" +
                     "        capital-city) \n" +
                     "  (cdolist (city cities) \n" +
                     "    (csetq capital-city (pred-values-in-any-mt city #$capitalCity)) \n" +
                     "    (dictionary-enter my-large-dictionary \n" +
                     "                      city \n" +
                     "                      (fif (consp capital-city) \n" +
                     "                           (first capital-city) \n" +
                     "                           ;; else \n" +
                     "                           nil))))";
            responseObject = cycAccess.converseObject(script);

            script = "(mapdictionary my-large-dictionary #'my-mapdictionary-fn)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertTrue(responseList.contains(
                cycAccess.makeCycList("(#$Brazil #$CityOfBrasiliaBrazil)")));

            script =
                "(define my-parameterized-mapdictionary-fn (key value args) \n" +
                "  (cpush (list key value args) answer) \n" +
                "  (ret nil))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-parameterized-mapdictionary-fn"),
                                responseObject);

            script = "(mapdictionary-parameterized my-small-dictionary #'my-parameterized-mapdictionary-fn '(\"x\"))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(a 1 (\"x\"))")));
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(b 2 (\"x\"))")));
            Assert.assertTrue(responseList.contains(cycAccess.makeCycList("(c 3 (\"x\"))")));

            script = "(mapdictionary-parameterized my-large-dictionary #'my-parameterized-mapdictionary-fn '(1 2))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(symbol-value 'answer)";
            responseList = cycAccess.converseList(script);
            Assert.assertTrue(responseList.contains(
                cycAccess.makeCycList("(#$Brazil #$CityOfBrasiliaBrazil (1 2))")));

            // ccatch and throw
            script =
                "(define my-super () \n" +
                "  (clet (result) \n" +
                "    (ccatch :abort \n" +
                "      result \n" +
                "      (my-sub) \n" +
                "      (csetq result 0)) \n" +
                "  (ret result)))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-super"),
                                responseObject);

            script =
                "(define my-sub () \n" +
                "  (clet ((a 1) (b 2)) \n" +
                "  (ignore a b) \n" +
                "  (ret (throw :abort 99))))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-sub"),
                                responseObject);
            script = "(my-super)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(99), responseObject);

            // ignore-errors, cunwind-protect
            //cycAccess.traceOn();
            script =
                "(clet (result) \n" +
                "  (ignore-errors \n" +
                "    (cunwind-protect \n" +
                "	(/ 1 0) \n" +
                "      (csetq result \"protected\"))) \n" +
                "  result)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals("protected", responseObject);

            // get-environment
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n", responseString);
            script = "(csetq a nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(csetq b -1)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(-1), responseObject);
            script = "(get-environment)";
            responseString = cycAccess.converseString(script);
            Assert.assertEquals("\n\n(B . -1)\n(A)", responseString);

            // cdestructuring-bind
            script = "(cdestructuring-bind () '() (print 'foo))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("foo"),
                                responseObject);

            script = "(cdestructuring-bind (&whole a) () (print 'foo))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("foo"),
                                responseObject);

            script = "(cdestructuring-bind (&whole a b c) '(1 2) (print (list a b c)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("((1 2) 1 2)"));

            script = "(cdestructuring-bind (a b . c) '(1 2 3 4) (print (list a b c)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 2 (3 4))"));

            script = "(cdestructuring-bind (&optional a) '(1) (print (list a)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1)"));

            script = "(cdestructuring-bind (a &optional b) '(1 2) (print (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 2)"));

            script = "(cdestructuring-bind (&whole a &optional b) '(1) (print (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("((1) 1)"));

            script = "(cdestructuring-bind (&rest a) '(1 2) (print (list a)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("((1 2))"));

            script = "(cdestructuring-bind (&whole a b &rest c) '(1 2 3) (print (list a b c)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("((1 2 3) 1 (2 3))"));

            script = "(cdestructuring-bind (&key a b) '(:b 2 :a 1) (print (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 2)"));

            script = "(cdestructuring-bind (&key a b) '(:b 2 :allow-other-keys t :a 1 :c 3) (print (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 2)"));

            script = "(cdestructuring-bind (&key ((key a) 23 b)) '(key 1) (print (list a b)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 T)"));

            script = "(cdestructuring-bind (a &optional b &key c) '(1 2 :c 3) (print (list a b c)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("(1 2 3)"));

            script = "(cdestructuring-bind (&whole a b &optional c &rest d &key e &allow-other-keys &aux f) '(1 2 :d 4 :e 3) (print (list a b c d e f)))";
            responseList = cycAccess.converseList(script);
            Assert.assertEquals(responseList, cycAccess.makeCycList("((1 2 :D 4 :E 3) 1 2 (:D 4 :E 3) 3 NIL)"));

            // type testing
            script = "(clear-environment)";
            cycAccess.converseVoid(script);

            script = "(csetq a 1)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(new Integer(1), responseObject);
            script = "(numberp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a \"abc\")";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals("abc", responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a 2.14)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertTrue(responseObject instanceof Double);
            Assert.assertTrue(((Double) responseObject).doubleValue() > 2.13999);
            Assert.assertTrue(((Double) responseObject).doubleValue() < 2.14001);
            script = "(numberp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a 'my-symbol)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-symbol"), responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a '(1 . 2))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 . 2)"), responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a '(1 2))";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(cycAccess.makeCycList("(1 2)"), responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));

            script = "(csetq a nil)";
            responseObject = cycAccess.converseObject(script);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));

            // empty list is treated the same as nil.
            CycList command = new CycList();
            command.add(CycObjectFactory.makeCycSymbol("csetq"));
            command.add(CycObjectFactory.makeCycSymbol("a"));
            command.add(new CycList());
            responseObject = cycAccess.converseObject(command);
            Assert.assertEquals(CycObjectFactory.nil, responseObject);
            script = "(numberp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(integerp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(stringp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(atom a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(floatp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(symbolp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(consp a)";
            Assert.assertTrue(! cycAccess.converseBoolean(script));
            script = "(listp a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));
            script = "(null a)";
            Assert.assertTrue(cycAccess.converseBoolean(script));

            /*
            // constant name with embedded slash
            //cycAccess.traceOn();
            script =
              "(rtp-parse-exp-w/vpp \"Symptoms of EEE begin 4-10 days after infection\" \n" +
                "(fort-for-string \"STemplate\") \n" +
                "(fort-for-string \"AllEnglishTemplateMt\") \n" +
                "(fort-for-string \"RKFParsingMt\"))";
            responseList = cycAccess.converseList(script);
            */

            // check-type
            script = "(clear-environment)";
            cycAccess.converseVoid(script);
            script =
                "(clet (result) \n" +
                "  (ignore-errors \n" +
                "    (check-type 1 numberp) \n" +
                "    (csetq result t)) \n" +
                "  result)";
            Assert.assertEquals((Object) CycObjectFactory.t, cycAccess.converseObject(script));
            script =
                "(clet (result) \n" +
                "  (ignore-errors \n" +
                "    (check-type 1 stringp) \n" +
                "    (csetq result t)) \n" +
                "  result)";
            Assert.assertEquals((Object) CycObjectFactory.nil, cycAccess.converseObject(script));


            //cycAccess.traceOn();
        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess8 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 8 ****");
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

        doTestCycAccess8(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 8 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess8 () {
        System.out.println("\n**** testBinaryCycAccess 8 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        //cycAccess.traceOnDetailed();
        doTestCycAccess8(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 8 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess8 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {

            // isQuotedCollection
            CycConstant coreConstant =
                cycAccess.getKnownConstantByGuid("c0dd1b7c-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(cycAccess.isQuotedCollection(coreConstant, cycAccess.baseKB));
            Assert.assertTrue(cycAccess.isQuotedCollection(coreConstant));
            CycConstant animal =
                cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(! cycAccess.isQuotedCollection(animal));

            // List containing null is coerced to list containing NIL.
            if (cycAccess.communicationMode == CycConnection.BINARY_MODE) {
                String script =
                    "(put-api-user-variable 'a '(nil 1))";
                Object responseObject = cycAccess.converseObject(script);
                Assert.assertEquals(CycObjectFactory.nil, responseObject);

                script =
                    "(get-api-user-variable 'a)";
                CycList responseList = cycAccess.converseList(script);
                Assert.assertEquals(cycAccess.makeCycList("(nil 1)"),
                                    responseList);
            }

            // rkfPhraseReader
            CycFort inferencePsc =
                cycAccess.getKnownConstantByGuid("bd58915a-9c29-11b1-9dad-c379636f7270");
            CycFort rkfEnglishLexicalMicrotheoryPsc =
                cycAccess.getKnownConstantByGuid("bf6df6e3-9c29-11b1-9dad-c379636f7270");
            String text = "penguins";
            CycList parseExpressions = cycAccess.rkfPhraseReader(text,
                                                                rkfEnglishLexicalMicrotheoryPsc,
                                                                inferencePsc);
            CycList parseExpression = (CycList) parseExpressions.first();
            CycList spanExpression = (CycList) parseExpression.first();
            CycList terms = (CycList) parseExpression.second();
            // #$Penguin
            CycFort penguin = cycAccess.getKnownConstantByGuid("bd58a986-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(terms.contains(penguin));
            // #$PittsburghPenguins
            CycFort pittsburghPenguins = cycAccess.getKnownConstantByGuid("c08dec11-9c29-11b1-9dad-c379636f7270");
            Assert.assertTrue(terms.contains(pittsburghPenguins));

            // generateDisambiguationPhraseAndTypes
            CycList objects = new CycList();
            objects.add(penguin);
            objects.add(pittsburghPenguins);
            CycList disambiguationExpression = cycAccess.generateDisambiguationPhraseAndTypes(objects);
            System.out.println("disambiguationExpression\n" + disambiguationExpression);
            Assert.assertEquals(2, disambiguationExpression.size());
            CycList penguinDisambiguationExpression = (CycList) disambiguationExpression.first();
            System.out.println("penguinDisambiguationExpression\n" + penguinDisambiguationExpression);
            Assert.assertTrue(penguinDisambiguationExpression.contains("penguin"));
            Assert.assertTrue(penguinDisambiguationExpression.contains("bird"));
            CycList pittsburghPenguinDisambiguationExpression = (CycList) disambiguationExpression.second();
            System.out.println("pittsburghPenguinDisambiguationExpression\n" +
                               pittsburghPenguinDisambiguationExpression);
            Assert.assertTrue(pittsburghPenguinDisambiguationExpression.contains("the Pittsburgh Penguins"));
            Assert.assertTrue(pittsburghPenguinDisambiguationExpression.contains("ice hockey team"));

        }
        catch (Exception e) {
            e.printStackTrace();
            CycAccess.current().close();
            Assert.fail(e.toString());
        }


        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess9 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 9 ****");
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

        System.out.println(cycAccess.getCycConnection().connectionInfo());
        //cycAccess.traceOn();
        doTestCycAccess9(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 9 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess9 () {
        System.out.println("\n**** testBinaryCycAccess 9 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.SERIAL_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        System.out.println(cycAccess.getCycConnection().connectionInfo());
        //cycAccess.traceOn();
        doTestCycAccess9(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 9 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess9 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {

            CycConstant brazil = cycAccess.getKnownConstantByGuid("bd588f01-9c29-11b1-9dad-c379636f7270");
            CycConstant country = cycAccess.getKnownConstantByGuid("bd588879-9c29-11b1-9dad-c379636f7270");
            CycConstant worldGeographyMt = cycAccess.getKnownConstantByGuid("bfaac020-9c29-11b1-9dad-c379636f7270");
            CycConstant dog =
                cycAccess.getKnownConstantByGuid("bd58daa0-9c29-11b1-9dad-c379636f7270");
            CycConstant animal =
                cycAccess.getKnownConstantByGuid("bd58b031-9c29-11b1-9dad-c379636f7270");
            CycConstant biologyVocabularyMt =
                cycAccess.getKnownConstantByGuid("bdd51776-9c29-11b1-9dad-c379636f7270");
            CycConstant performedBy =
                cycAccess.getKnownConstantByGuid("bd58a962-9c29-11b1-9dad-c379636f7270");
            CycConstant doneBy =
                cycAccess.getKnownConstantByGuid("c0fd4798-9c29-11b1-9dad-c379636f7270");
            CycConstant siblings =
                cycAccess.getKnownConstantByGuid("bd58e3e9-9c29-11b1-9dad-c379636f7270");
            CycConstant generalLexiconMt =
                cycAccess.getKnownConstantByGuid("c109b867-9c29-11b1-9dad-c379636f7270");
            CycConstant paraphraseMt =
                cycAccess.getKnownConstantByGuid("bf3ab672-9c29-11b1-9dad-c379636f7270");

            // isa
            Assert.assertTrue(cycAccess.isa(brazil, country, worldGeographyMt));
            Assert.assertTrue(cycAccess.isa(brazil, country));

            // isGenlOf
            Assert.assertTrue(cycAccess.isGenlOf(animal, dog, biologyVocabularyMt));
            Assert.assertTrue(cycAccess.isGenlOf(animal, dog));

            // isGenlPredOf
            Assert.assertTrue(cycAccess.isGenlPredOf(doneBy, performedBy, cycAccess.baseKB));
            Assert.assertTrue(cycAccess.isGenlPredOf(doneBy, performedBy));

            // isGenlInverseOf
            Assert.assertTrue(cycAccess.isGenlInverseOf(siblings, siblings, biologyVocabularyMt));
            Assert.assertTrue(cycAccess.isGenlInverseOf(siblings, siblings));

            // isGenlMtOf
            Assert.assertTrue(cycAccess.isGenlMtOf(cycAccess.baseKB, biologyVocabularyMt));

            // getAllInstancesHashSet
            HashSet allCountries = cycAccess.getAllInstancesHashSet(country, worldGeographyMt);
            Assert.assertTrue(allCountries instanceof HashSet);
            Assert.assertTrue(allCountries.contains(brazil));
            allCountries = cycAccess.getAllInstancesHashSet(country);
            Assert.assertTrue(allCountries instanceof HashSet);
            Assert.assertTrue(allCountries.contains(brazil));

            // getAllSpecsHashSet
            HashSet allAnimals = cycAccess.getAllSpecsHashSet(animal, biologyVocabularyMt);
            Assert.assertTrue(allAnimals instanceof HashSet);
            Assert.assertTrue(allAnimals.contains(dog));
            allAnimals = cycAccess.getAllSpecsHashSet(animal);
            Assert.assertTrue(allAnimals instanceof HashSet);
            Assert.assertTrue(allAnimals.contains(dog));

            // getAllSpecPredsHashSet
            HashSet allDoers = cycAccess.getAllSpecPredsHashSet(doneBy, cycAccess.baseKB);
            Assert.assertTrue(allDoers instanceof HashSet);
            Assert.assertTrue(allDoers.contains(performedBy));
            allDoers = cycAccess.getAllSpecPredsHashSet(doneBy);
            Assert.assertTrue(allDoers instanceof HashSet);
            Assert.assertTrue(allDoers.contains(performedBy));

            // getAllSpecInversesHashSet
            HashSet allSpecInverses = cycAccess.getAllSpecInversesHashSet(siblings, biologyVocabularyMt);
            Assert.assertTrue(allSpecInverses instanceof HashSet);
            Assert.assertTrue(allSpecInverses.contains(siblings));
            allSpecInverses = cycAccess.getAllSpecInversesHashSet(siblings);
            Assert.assertTrue(allSpecInverses instanceof HashSet);
            Assert.assertTrue(allSpecInverses.contains(siblings));

            // getAllSpecMtsHashSet
            HashSet allSpecMts = cycAccess.getAllSpecMtsHashSet(generalLexiconMt);
            Assert.assertTrue(allSpecMts instanceof HashSet);
            Assert.assertTrue(allSpecMts.contains(paraphraseMt));

            /*
            // tests proper receipt of narts from the server.
            String script = "(csetq all-narts nil)";
            cycAccess.converseVoid(script);
            script = "(progn \n" +
                     "  (do-narts (nart) \n" +
                     "    (cpush nart all-narts)) \n" +
                     "  nil)";
            cycAccess.converseVoid(script);
            script = "(clet (nart) \n" +
                     "  (csetq nart (first all-narts)) \n" +
                     "  (csetq all-narts (rest all-narts)) \n" +
                     "  nart)";
            long numberGood = 0;
            long numberNil = 0;
            while (true) {
                Object obj = cycAccess.converseObject(script);
                if (obj.equals(CycObjectFactory.nil))
                    break;
                Assert.assertTrue(obj instanceof CycNart);
                CycNart cycNart = (CycNart) obj;
                Assert.assertTrue(cycNart.cyclify() instanceof String);
                String script2 = "(find-nart " + cycNart.stringApiValue() + ")";
                Object obj2 = cycAccess.converseObject(script2);
                if (cycNart.equals(obj))
                    numberGood++;
                else
                    numberNil++;
            }
            Assert.assertTrue(numberGood > 20 * numberNil);
            script = "(csetq all-narts nil)";
            cycAccess.converseVoid(script);
            */
        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }


        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess10 () {
        if (performOnlyBinaryApiModeTests ||
            (connectionMode == REMOTE_CYC_CONNECTION))
            return;
        System.out.println("\n**** testAsciiCycAccess 10 ****");
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

        System.out.println(cycAccess.getCycConnection().connectionInfo());
        //cycAccess.traceOn();
        doTestCycAccess10(cycAccess);

        cycAccess.close();
        System.out.println("**** testAsciiCycAccess 10 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess10 () {
        System.out.println("\n**** testBinaryCycAccess 10 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.CONCURRENT_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        System.out.println(cycAccess.getCycConnection().connectionInfo());
        //cycAccess.traceOn();
        doTestCycAccess10(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 10 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess10 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {
            // demonstrate quoted strings
            CycList cycList53 = cycAccess.makeCycList("(\"abc\")");
            Assert.assertEquals(1, cycAccess.converseInt("(length '" + cycList53.cycListApiValue() + ")"));
            Assert.assertEquals(3, cycAccess.converseInt("(length (first '" + cycList53.cycListApiValue() + "))"));
            String string = "abc";
            CycList cycList54 = new CycList();
            cycList54.add(CycObjectFactory.makeCycSymbol("length"));
            cycList54.add(string);
            Assert.assertEquals(3, cycAccess.converseInt(cycList54));
            String quotedString = "\"abc\" def";
            CycList cycList55 = new CycList();
            cycList55.add(CycObjectFactory.makeCycSymbol("length"));
            cycList55.add(quotedString);
            // Note that in binary mode, that Cyc's cfasl input will insert the required escape
            // chars for embedded quotes.
            // And in ascii mode note that CycConnection will insert the required escape
            // chars for embedded quotes.
            Assert.assertEquals(9, cycAccess.converseInt(cycList55));

            // demonstrate quoted strings with the CycListParser
            CycList cycList56 = cycAccess.makeCycList("(\"" + string + "\")");
            Assert.assertEquals(1, cycAccess.converseInt("(length '" + cycList56.stringApiValue() + ")"));
            Assert.assertEquals(3, cycAccess.converseInt("(length (first '" + cycList56.stringApiValue() + "))"));
            String embeddedQuotesString = "(" + "\"\\\"abc\\\" def\"" + ")";
            CycList cycList57 = cycAccess.makeCycList(embeddedQuotesString);
            String script = "(length '" + cycList57.stringApiValue() + ")";
            int actualLen = cycAccess.converseInt(script);
            Assert.assertEquals(1, actualLen);
            Assert.assertEquals(9, cycAccess.converseInt("(length (first '" + cycList57.stringApiValue() + "))"));

            script = "(identity '(#$givenNames #$Guest \"\\\"The\\\" Guest\"))";
            CycList answer = cycAccess.converseList(script);
            Object third = answer.third();
            Assert.assertTrue(third instanceof String);
            Assert.assertEquals(11, ((String) third).length());

            // isFormulaWellFormed
            CycList formula1 =
                cycAccess.makeCycList("(#$isa #$Brazil #$IndependentCountry)");
            CycConstant mt = cycAccess.getKnownConstantByName("WorldPoliticalGeographyDataVocabularyMt");
            Assert.assertTrue(cycAccess.isFormulaWellFormed(formula1, mt));
            CycList formula2 =
                cycAccess.makeCycList("(#$genls #$Brazil #$Collection)");
            Assert.assertTrue(! cycAccess.isFormulaWellFormed(formula2, mt));

            // isCycLNonAtomicReifableTerm
            CycList formula3 =
                cycAccess.makeCycList("(#$TheCovering #$Watercraft-Surface #$Watercraft-Subsurface)");
            Assert.assertTrue(cycAccess.isCycLNonAtomicReifableTerm(formula3));
            CycList formula4 = cycAccess.makeCycList("(#$isa #$Plant #$Animal)");
            Assert.assertTrue(! cycAccess.isCycLNonAtomicReifableTerm(formula4));
            CycList formula5 = cycAccess.makeCycList("(#$PlusFn 1)");
            Assert.assertTrue(! cycAccess.isCycLNonAtomicReifableTerm(formula5));

            // isCycLNonAtomicUnreifableTerm
            CycList formula6 =
                cycAccess.makeCycList("(#$TheCovering #$Watercraft-Surface #$Watercraft-Subsurface)");
            Assert.assertTrue(! cycAccess.isCycLNonAtomicUnreifableTerm(formula6));
            CycList formula7 = cycAccess.makeCycList("(#$isa #$Plant #$Animal)");
            Assert.assertTrue(! cycAccess.isCycLNonAtomicUnreifableTerm(formula7));
            CycList formula8 = cycAccess.makeCycList("(#$PlusFn 1)");
            Assert.assertTrue(cycAccess.isCycLNonAtomicUnreifableTerm(formula8));
        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }


        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess11 () {
        System.out.println("\n**** testBinaryCycAccess 11 ****");
        CycObjectFactory.resetCaches();
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.CONCURRENT_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        //cycAccess.traceOnDetailed();
        doTestCycAccess11(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 11 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess11 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {
            String script = "(+ 1 2)";
            int answer = cycAccess.converseInt(script);
            Assert.assertEquals(3, answer);
        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        System.out.println("Concurrent API requests.");
        ArrayList apiRequestors = new ArrayList();

        ApiRequestor apiRequestor = new ApiRequestor("Long",
                                                     1,
                                                     "60000000",
                                                     cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short1",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short2",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short3",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short4",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short5",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short6",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short7",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        apiRequestor = new ApiRequestor("Short8",
                                        4,
                                        "700000",
                                        cycAccess);
        apiRequestor.start();
        apiRequestors.add(apiRequestor);

        while (true) {
            boolean apiRequestorTheadRunning = false;
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                break;
            }
            for (int i = 0; i < apiRequestors.size(); i++) {
                apiRequestor = (ApiRequestor) apiRequestors.get(i);
                if (! apiRequestor.done) {
                    apiRequestorTheadRunning = true;
                    System.out.println("  " + apiRequestor.name + " is still running");
                    break;
                    }
            }
            if (! apiRequestorTheadRunning)
                break;
        }
        long endMilliseconds = System.currentTimeMillis();
        System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");
    }

    /**
     * Class ApiRequestor.
     */
    protected class ApiRequestor extends Thread {


        private CycAccess cycAccess;
        public String name;
        private int repeatCount;
        private String durationFactor;
        public boolean done = false;

        /**
         * Constructs a ApiRequestor object.
         */
        public ApiRequestor(String name,
                            int repeatCount,
                            String durationFactor,
                            CycAccess cycAccess) {
            this.name = name;
            this.repeatCount = repeatCount;
            this.durationFactor = durationFactor;
            this.cycAccess = cycAccess;
        }

        /**
         * Makes some API requests.
         */
        public void run() {
            System.out.println("ApiRequestor " + name + " starting.");
            try {
                for (int i = 0; i < repeatCount; i++) {
                    String testPhrase = name + "-" +Integer.toString(i + 1);
                    String script = "(progn (cdotimes (x " + durationFactor + "))\n" +
                                    " \"" + testPhrase + "\")";
                    String answer = cycAccess.converseString(script);
                    System.out.println(name + " iteration " + answer + " done.");
                    if (! answer.equals(testPhrase))
                        throw new RuntimeException(testPhrase + " not equal to " + answer);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println("ApiRequestor " + name + " done.");
            done = true;
        }
    }

    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess12 () {
        System.out.println("\n**** testBinaryCycAccess 12 ****");
        CycAccess cycAccess = null;
        try {
            if (connectionMode == LOCAL_CYC_CONNECTION)
                cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                          CycConnection.DEFAULT_BASE_PORT,
                                          CycConnection.BINARY_MODE,
                                          CycAccess.PERSISTENT_CONNECTION,
                                          CycConnection.DEFAULT_MESSAGING_MODE);
            else if (connectionMode == REMOTE_CYC_CONNECTION)
                cycAccess = new CycAccess(myAgentName, cycProxyAgentName, agentCommunity);
            else
                Assert.fail("Invalid connection mode " + connectionMode);
        }
        catch (Exception e) {
            CycAccess.current().close();
            Assert.fail(e.toString());
        }
        doTestCycAccess12(cycAccess);

        cycAccess.close();
        System.out.println("**** testBinaryCycAccess 12 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess12 (CycAccess cycAccess) {
        long startMilliseconds = System.currentTimeMillis();
        try {
            //cycAccess.traceOn();
            String utf8String = "ABCdef";
            Assert.assertEquals(utf8String, cycAccess.converseString("(identity \"" + utf8String + "\")"));

            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader =
                    new InputStreamReader(
                        new FileInputStream(
                            new File("utf8-sample.html")), "UTF-8");
            }
            catch (IOException e) {
                return;
            }
            StringBuffer utf8StringBuffer = new StringBuffer();
            while (true) {
                int ch = inputStreamReader.read();
                if (ch == -1)
                    break;
                if (ch == '\n' ||
                    ch == '\r')
                    utf8StringBuffer.append(' ');
                else
                    utf8StringBuffer.append((char) ch);
            }
            utf8String = utf8StringBuffer.toString();

            PrintWriter utf8Output =
                new PrintWriter(
                    new OutputStreamWriter(
                        new FileOutputStream("utf8-sample-without-newlines.html"), "UTF8"));
            utf8Output.print(utf8String);
            utf8Output.close();

            String escapedUtf8String = StringUtils.escapeDoubleQuotes(utf8String);

            CycList command = new CycList();
            command.add(CycObjectFactory.makeCycSymbol("identity"));
            command.add(escapedUtf8String);
            String echoUtf8Sting = cycAccess.converseString(command);

            utf8Output =
                new PrintWriter(
                    new OutputStreamWriter(
                        new FileOutputStream("utf8-sample-from-cyc.html"), "UTF8"));
            utf8Output.print(utf8String);
            utf8Output.close();

            System.out.println("utf8String\n" + utf8String);
            System.out.println("escapedUtf8String\n" + escapedUtf8String);
            System.out.println("echoUtf8Sting\n" + echoUtf8Sting);
            Assert.assertEquals(escapedUtf8String, echoUtf8Sting);


            CycFort myTerm = cycAccess.getConstantByName("my-term");
            if (myTerm != null)
                cycAccess.kill(myTerm);
            myTerm = cycAccess.findOrCreate("my-term");
            cycAccess.assertComment(myTerm, escapedUtf8String, cycAccess.baseKB);




        }
        catch (Exception e) {
            CycAccess.current().close();
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }





}





