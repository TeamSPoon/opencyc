package org.opencyc.cycagent.coabs;

import junit.framework.*;
import fipaos.ont.fipa.*;
import fipaos.parser.ParserException;

/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.cycagent.coabs</tt> package.<p>
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
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    /**
     * Construct a new UnitTest object.
     * @param name the test case name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Runs the unit tests
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();
        testSuite.addTest(new UnitTest("testGuid"));
        return testSuite;
    }

    /**
     * Tests <tt>CoAbsCommunityAdapter</tt> object behavior.
     */
    public void testGuid() {
        System.out.println("\n** testCoAbsCommunityAdapter **");
        try {
            String aclText =
                "(inform\n" +
                "  :sender CoABSRegistrationMonitor\n" +
                "  :receiver Agent1\n" +
                "  :content\n" +
                "    (done\n" +
                "      (action CoABSRegistrationMonitor\n" +
                "        (register-agent\n" +
                "          (:CoABS-agent-description\n" +
                "            (:name Agent1)\n" +
                "            (:serviceID fc2f14ee-303e-4f58-a6f4-cb0bc2ad5546)\n" +
                "            (:acls )\n" +
                "            (:contentLanguages )\n" +
                "            (:ontologies ))\n" +
                "          (:ams-description\n" +
                "            (:agent-name Agent1)))))\n" +
                "  :language SL0\n" +
                "  :in-reply-to message1\n" +
                "  :protocol fipa-request\n" +
                "  :ontology fipa-agent-management)\n";

            String fixedAclText = CoAbsCommunityAdapter.fixSenderReceiver(aclText);
            String expectedFixedAclText =
                "(inform\n" +
                "  :sender (agent-identifier :name CoABSRegistrationMonitor)\n" +
                "  :receiver (set (agent-identifier :name Agent1))\n" +
                "  :content\n" +
                "    (done\n" +
                "      (action CoABSRegistrationMonitor\n" +
                "        (register-agent\n" +
                "          (:CoABS-agent-description\n" +
                "            (:name Agent1)\n" +
                "            (:serviceID fc2f14ee-303e-4f58-a6f4-cb0bc2ad5546)\n" +
                "            (:acls )\n" +
                "            (:contentLanguages )\n" +
                "            (:ontologies ))\n" +
                "          (:ams-description\n" +
                "            (:agent-name Agent1)))))\n" +
                "  :language SL0\n" +
                "  :in-reply-to message1\n" +
                "  :protocol fipa-request\n" +
                "  :ontology fipa-agent-management)\n";
            Assert.assertEquals(expectedFixedAclText, fixedAclText);
            System.out.println(fixedAclText);

            ACL acl = new ACL(fixedAclText);
            Assert.assertEquals("inform", acl.getPerformative());
            Assert.assertEquals("CoABSRegistrationMonitor", acl.getSenderAID().getName());
            Assert.assertEquals("Agent1", acl.getReceiverAID().getName());
            Assert.assertEquals("(done " +
                                "(action CoABSRegistrationMonitor " +
                                "(register-agent " +
                                "(:CoABS-agent-description " +
                                "(:name Agent1 ) " +
                                "(:serviceID fc2f14ee-303e-4f58-a6f4-cb0bc2ad5546 ) " +
                                "(:acls ) " +
                                "(:contentLanguages ) " +
                                "(:ontologies ) ) " +
                                "(:ams-description " +
                                "(:agent-name Agent1 ) ) ) ) )", acl.getContentObject());
            Assert.assertEquals("SL0", acl.getLanguage());
            Assert.assertEquals("message1", acl.getInReplyTo());
            Assert.assertEquals("fipa-request", acl.getProtocol());
            Assert.assertEquals("fipa-agent-management", acl.getOntology());

            System.out.println(acl);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        System.out.println("** testCoAbsCommunityAdapter OK **");
    }


}