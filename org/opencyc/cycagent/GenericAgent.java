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
 * Provides generic agent capablilities for cyc agents participating in the CoABS and FIPA-OS
 * agent communities.
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

public class GenericAgent implements MessageReceiver {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 1;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * name of the local agent
     */
    protected String myAgentName = "my-agent";

    /**
     * name of the remote agent
     */
    protected String remoteAgentName;

    /**
     * agent community to which the remote agent belongs
     */
    protected int remoteAgentCommunity;

    /**
     * the interface for interacting with an agent community such as CoABS or FIPA-OS
     */
    protected AgentCommunityAdapter agentCommunityAdapter;

    /**
     * Indicates whether this class consumed the received message.
     */
    protected boolean messageConsumed = false;

    /**
     * Constructs a GenericAgent object.
     */
    public GenericAgent() {
        Log.makeLog();
    }

    /**
     * Constructs a GenericAgent object.
     *
     * @param myAgentName name of the local agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public GenericAgent(String myAgentName, int remoteAgentCommunity, int verbosity) {
        this.myAgentName = myAgentName;
        this.remoteAgentCommunity = remoteAgentCommunity;
        this.verbosity = verbosity;
        Log.makeLog();
    }

    /**
     * Initialializes the agent community with the given degree of diagnostic verbosity.
     *
     */
    public void initializeAgentCommunity() {
        if (verbosity > 0)
            Log.current.println("Initializing the agent community connection");
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNTITY) {
            try {
                agentCommunityAdapter = new CoAbsCommunityAdapter(this, verbosity);
            }
            catch (IOException e) {
                Log.current.errorPrintln("Error creating CoAbsCommunityAdapter " + e.getMessage());
                agentCommunityAdapter.deregister();
                System.exit(1);
            }
        }
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNTITY)
            agentCommunityAdapter = new FipaOsCommunityAdapter(this, verbosity);
        else
            throw new RuntimeException("Invalid remote agent community " + remoteAgentCommunity);
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (ACL acl) {
        messageConsumed = false;
        if (acl.getOntology().equals(AgentCommunityAdapter.CYC_ECHO_ONTOLOGY)) {
            processEchoRequest(acl);
            messageConsumed = true;
        }
    }

    /**
     * Processes an echo request from another agent.
     *
     * @param echoRequestAcl the echo request Agent Communication Language message
     */
    public void processEchoRequest (ACL echoRequestAcl) {
        ACL echoReplyAcl = (ACL) echoRequestAcl.clone();
        echoReplyAcl.setPerformative(FIPACONSTANTS.INFORM);
        echoReplyAcl.setSenderAID(echoRequestAcl.getReceiverAID());
        echoReplyAcl.setReceiverAID(echoRequestAcl.getSenderAID());
        echoReplyAcl.setReplyWith(null);
        echoReplyAcl.setInReplyTo(echoRequestAcl.getReplyWith());
        try {
            agentCommunityAdapter.sendMessage(echoReplyAcl);
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception " + e.getMessage() +
                                     "\nwhile replying to echo request with\n" + echoReplyAcl);
        }
    }

    /**
     * Returns the agent community.
     *
     * @return the agent community
     */
    public int agentCommunity () {
        return remoteAgentCommunity;
    }

    /**
     * Returns the agent community name.
     *
     * @return the agent community name
     */
    public String agentCommunityName () {
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNTITY)
            return "CoABS";
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNTITY)
            return "FIPA-OS";
        else
            throw new RuntimeException("Invalid agent community " + remoteAgentCommunity);
    }

    /**
     * Returns the agent community adapter
     *
     * @return the agent community adapter
     */
    public AgentCommunityAdapter getAgentCommunityAdapter () {
        return agentCommunityAdapter;
    }

    /**
     * Returns my agent name.
     *
     * @return my agent name
     */
    public String getMyAgentName () {
        return myAgentName;
    }

    /**
     * Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
}