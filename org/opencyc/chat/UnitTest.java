package org.opencyc.chat;

import java.net.*;
import java.io.*;
import junit.framework.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.statemachine.*;
import org.opencyc.uml.interpreter.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.chat</tt> package<p>
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
        testSuite.addTest(new UnitTest("testChatterBot"));
        testSuite.addTest(new UnitTest("testParser"));
        return testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit GUI.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests the ChatterBot object.
     */
    public void testChatterBot () {
        System.out.println("\n**** testChatterBot ****");
        Log.makeLog("unit-test.log");

        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost",
                                          3620,
                                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                                          true);
                //cycAccess.traceNamesOn();
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost",
                                          3600,
                                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                                          true);
            }
            else
                cycAccess = new CycAccess();
            CycExtractor cycExtractor = new CycExtractor(cycAccess);
            StateMachine stateMachine = cycExtractor.extract("ChatterBotStateMachine");
            Assert.assertTrue(stateMachine instanceof StateMachine);
            Assert.assertTrue(stateMachine.getNamespace() instanceof Namespace);
            Assert.assertEquals("ChatterBotStateMachineNamespace", stateMachine.getNamespace().getName());
            Assert.assertTrue(stateMachine.getNamespace().getOwnedElement().contains(stateMachine));
            Assert.assertEquals("ChatterBotStateMachine", stateMachine.getName());
            Assert.assertTrue(stateMachine.getComment() instanceof Comment);
            Assert.assertEquals("This is the #$UMLStateMachine used by #$CognitiveCyc to " +
                                "implement #$ChatterBot behavior.",
                                stateMachine.getComment().getBody());
            Assert.assertEquals(stateMachine, stateMachine.getComment().getAnnotatedElement());

            StateMachineReport stateMachineReport = new StateMachineReport(stateMachine);
            stateMachineReport.report();

            interpretStateMachine(stateMachine, cycAccess);

        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("**** testChatterBot OK ****");
    }

    /**
     * Tests the given instantiated state machine
     *
     * @param stateMachine the given instantiated state machine
     * @param cycAccess the cyc access instance
     */
    protected void interpretStateMachine (StateMachine stateMachine, CycAccess cycAccess) {
        int verbosity = 3;
        Interpreter interpreter = null;
        try {
            interpreter = new Interpreter(stateMachine, cycAccess, verbosity);
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(interpreter);
        Assert.assertTrue(interpreter instanceof Interpreter);
        Assert.assertNull(interpreter.getCurrentEvent());
        Assert.assertEquals(stateMachine, interpreter.getStateMachine());
        interpreter.interpret();
        if (verbosity > 2)
            System.out.print(interpreter.displayStateConfigurationTree());
    }

    /**
     * Tests the Parser object.
     */
    public void testParser () {
        System.out.println("\n**** testParser ****");
        Log.makeLog("unit-test.log");

        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            CycAccess cycAccess;
            if (localHostName.equals("crapgame.cyc.com")) {
                cycAccess = new CycAccess("localhost",
                                          3620,
                                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                                          true);
                //cycAccess.traceNamesOn();
            }
            else if (localHostName.equals("thinker")) {
                cycAccess = new CycAccess("localhost",
                                          3600,
                                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                                          true);
            }
            else
                cycAccess = new CycAccess();
            Parser parser = new Parser(cycAccess);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println("**** testParser ****");
    }


}