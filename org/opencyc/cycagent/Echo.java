package org.opencyc.cycagent;

import java.io.*;
import javax.naming.TimeLimitExceededException;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import org.opencyc.util.Timer;
import org.opencyc.cycagent.coabs.*;
import org.opencyc.cycagent.fipaos.*;
import org.opencyc.xml.*;
import org.opencyc.util.*;

/**
 * Provides an example agent demonstrating the echo role.
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

public class Echo extends GenericAgent {

    protected static final long oneMinuteDuration = 60 * 1000;

    public String echoMessageText = "default echo message";

    /**
     * Constructs a new Echo agent object.
     */
    public Echo() {
        super();
    }

    /**
     * Provides the main method to run the Echo class as an application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("invalid args, usage: remote-agent-name remote-agent-community message-string");
            System.exit(1);
        }
        Echo echo = new Echo();
        echo.remoteAgentName = args[0];
        if (args[1].equals("coabs"))
            echo.remoteAgentCommunity = AgentCommunityAdapter.COABS_AGENT_COMMUNTITY;
        else if (args[1].equals("fipa-os"))
            echo.remoteAgentCommunity = AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNTITY;
        else {
            Log.current.errorPrintln("remote agent community must be coabs or fipa-os");
            System.exit(1);
        }
        echo.echoMessageText = args[2];
        echo.myAgentName = "EchoAgent";
        echo.initializeAgentCommunity(AgentCommunityAdapter.QUIET_VERBOSITY);
        echo.doEcho();
        echo.agentCommunityAdapter.deregister();
        System.exit(0);
    }

    /**
     * Initializes the agent community adapter, sends the echo message, and displays the echo response.
     */
    public void doEcho () {
        ACL acl = new ACL();
        acl.setPerformative(FIPACONSTANTS.REQUEST);
        AgentID senderAid = new AgentID();
        senderAid.setName(myAgentName);
        acl.setSenderAID(senderAid);
        AgentID receiverAid = new AgentID();
        receiverAid.setName(remoteAgentName);
        acl.addReceiverAID(receiverAid);
        String echoRequestXml =
            "\n<list>\n" +
            "  <symbol>ECHO</symbol>\n" +
            "  <string>" + TextUtil.doEntityReference(echoMessageText) + "</string>\n" +
            "</list>";
        acl.setContentObject(echoRequestXml, ACL.BYTELENGTH_ENCODING);
        acl.setLanguage(FIPACONSTANTS.XML);
        acl.setOntology(AgentCommunityAdapter.CYC_ECHO_ONTOLOGY);
        acl.setReplyWith(agentCommunityAdapter.nextMessageId());
        System.out.println("\nSending to remote agent " + remoteAgentName + ":" + echoRequestXml);
        ACL replyAcl = null;
        try {
            //Timer timer = new Timer(this.oneMinuteDuration);
            Timer timer = new Timer();
            replyAcl = agentCommunityAdapter.converseMessage(acl, timer);
        }
        catch (TimeLimitExceededException e) {
            Log.current.errorPrintln("No reply from " + remoteAgentName + " within the time limit");
            System.exit(1);
        }
        catch (IOException e) {
            Log.current.errorPrintln("Error communicating with " + remoteAgentName + "\n" + e.getMessage());
            System.exit(1);
        }
        String echoReplyXml = (String) replyAcl.getContentObject();
        Log.current.println("\nReceived from remote agent " + remoteAgentName + ":" + echoReplyXml);
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (ACL acl) {
        super.messageReceived(acl);
        if (! messageConsumed)
            Log.current.println("Ignoring message\n" + acl);
    }


}