package org.opencyc.cycagent;

import java.io.*;
import java.util.*;
import javax.naming.TimeLimitExceededException;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import fipaos.util.*;
import org.jdom.*;
import ViolinStrings.*;
import org.opencyc.util.Timer;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
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
    //public static final int DEFAULT_VERBOSITY = AgentCommunityAdapter.QUIET_VERBOSITY;
    public static final int DEFAULT_VERBOSITY = AgentCommunityAdapter.MAX_VERBOSITY;

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
     * type of the local agent
     */
    protected String myAgentType = "fipaos";

    /**
     * the interface for interacting with the CoABS agent community
     */
    protected AgentCommunityAdapter coAbsCommunityAdapter;

    /**
     * the interface for interacting with the FIPA-OS agent community
     */
    protected AgentCommunityAdapter fipaOsCommunityAdapter;

    /**
     * The interface to either the CoABS or FIPA-OS agent community.  This reference
     * is provided for the convenience of agents which use only one of the agent
     * communities, and is null when both are specified in the constructor method.
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
     * Constructs a GenericAgent object for the given agent on both agent communities.
     *
     * @param myAgentName name of the local agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public GenericAgent(String myAgentName, int verbosity) {
        this.myAgentName = myAgentName;
        this.remoteAgentCommunity = AgentCommunityAdapter.FIPA_OS_AND_COABS_AGENT_COMMUNITIES;
        this.verbosity = verbosity;
        Log.makeLog();
        if (verbosity > 0)
            Log.current.println("Created " + this.getClass() + " for " + myAgentName +
                                "\nfor both CoABS and FIPA-OS agent communities");
    }

    /**
     * Constructs a GenericAgent object for the given agent on the given agent community.
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
        if (verbosity > 0)
            Log.current.println("Created " + this.getClass() + " for " + myAgentName +
                                "\nfor the " + this.agentCommunityName() + " agent community");
    }

    /**
     * Initialializes the agent community with the given degree of diagnostic verbosity.
     *
     */
    public void initializeAgentCommunity() {
        if (verbosity > 0)
            Log.current.println("Initializing the agent community connection");
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY ||
            remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AND_COABS_AGENT_COMMUNITIES) {
            try {
                Class coAbsCommunityAdapterClass =
                    Class.forName("org.opencyc.cycagent.coabs.CoAbsCommunityAdapter");
                coAbsCommunityAdapter = (AgentCommunityAdapter) coAbsCommunityAdapterClass.newInstance();
                coAbsCommunityAdapter.initialize(this, verbosity);
            }
            catch (Exception e) {
                Log.current.errorPrintln("Error creating CoAbsCommunityAdapter " + e.getMessage());
                if (coAbsCommunityAdapter != null)
                    coAbsCommunityAdapter.deregister();
                System.exit(1);
            }
        }
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY ||
            remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AND_COABS_AGENT_COMMUNITIES)
            fipaOsCommunityAdapter = new FipaOsCommunityAdapter(this, verbosity);
        else
            throw new RuntimeException("Invalid remote agent community " + remoteAgentCommunity);
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
            agentCommunityAdapter = coAbsCommunityAdapter;
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY)
            agentCommunityAdapter = fipaOsCommunityAdapter;
        else
            agentCommunityAdapter = null;
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (int remoteAgentCommunity, ACL acl) {
        if (verbosity > 0)
            Log.current.println("\nGenericAgent received\n" + acl);
        messageConsumed = false;
        if (acl.getOntology().equals(AgentCommunityAdapter.CYC_ECHO_ONTOLOGY)) {
            processEchoRequest(remoteAgentCommunity, acl);
            messageConsumed = true;
        }
    }

    /**
     * Processes an echo request from another agent.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param echoRequestAcl the echo request Agent Communication Language message
     */
    public void processEchoRequest (int remoteAgentCommunity, ACL echoRequestAcl) {
        if (verbosity > 0)
            Log.current.println("Processing echo request\n" + echoRequestAcl);
        ACL echoReplyAcl = (ACL) echoRequestAcl.clone();
        echoReplyAcl.setPerformative(FIPACONSTANTS.INFORM);
        echoReplyAcl.setSenderAID(getAID(remoteAgentCommunity));
        echoReplyAcl.setReceiverAID(echoRequestAcl.getSenderAID());
        echoReplyAcl.setReplyWith(null);
        echoReplyAcl.setInReplyTo(echoRequestAcl.getReplyWith());
        if (verbosity > 0)
            Log.current.println("\n\nEcho reply\n" + echoReplyAcl);
        try {
            if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
                coAbsCommunityAdapter.sendMessage(echoReplyAcl);
            else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY) {
                fipaOsCommunityAdapter.sendMessage(echoReplyAcl);
            }
            else
                Log.current.errorPrintln("Invalid remoteAgentCommunity " + remoteAgentCommunity);
        }
        catch (Exception e) {
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
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
            return "CoABS";
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY)
            return "FIPA-OS";
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AND_COABS_AGENT_COMMUNITIES)
            return "FIPA-OS and CoABS";
        else
            throw new RuntimeException("Invalid agent community " + remoteAgentCommunity);
    }

    /**
     * Returns the CoABS agent community adapter
     *
     * @return the CoABS agent community adapter
     */
    public AgentCommunityAdapter getCoAbsCommunityAdapter () {
        return coAbsCommunityAdapter;
    }

    /**
     * Returns the FIPA-OS agent community adapter
     *
     * @return the FIPA-OS agent community adapter
     */
    public AgentCommunityAdapter getFipaOsCommunityAdapter () {
        return fipaOsCommunityAdapter;
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
     * Returns my agent type (FIPA-OS requirement).
     *
     * @return my agent type
     */
    public String getAgentType () {
        return myAgentType;
    }

    /**
     * Gets the AgentID of this Agent
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     */
    public AgentID getAID (int remoteAgentCommunity) {
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY) {
            AgentID agentID = new AgentID();
            agentID.setName(myAgentName);
            return agentID;
        }
        else
            return fipaOsCommunityAdapter.getAID();
    }

    /**
     * Makes an AgentID for the given agent name
     *
     * @param agentName the name of the agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     */
    public AgentID makeAID (String agentName, int remoteAgentCommunity) {
        AgentID agentID = null;
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY) {
            agentID = new AgentID();
            agentID.setName(agentName);
        }
        else if (remoteAgentCommunity == AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY) {
            agentID = (AgentID) fipaOsCommunityAdapter.getAID().clone();
            int index = agentName.indexOf("@");
            if (index > -1)
                agentName = agentName.substring(0, index);
            agentID.setName(agentName);
            List addresses = agentID.getAddresses();
            ArrayList newAddresses = new ArrayList();
            for (int i = 0; i < addresses.size(); i++) {
                URL address = (URL) addresses.get(i);
                URL newAddress = (URL) address.clone();
                newAddresses.add(newAddress);
                newAddress.setTarget(agentName);
            }
            agentID.setAddresses(newAddresses);
        }
        else
            throw new RuntimeException ("Invalid agent community " + remoteAgentCommunity);
        return agentID;
    }

    /**
     * Returns the CycList FIPA-2001 representation of the given ACL without using CycAccess.
     *
     * @param acl the Agent Communication Lanaguage message object.
     * @return the CycList representation of the given ACL without using CycAccess
     */
    public static CycList aclToCycList(ACL acl) throws JDOMException, IOException {
        CycList aclList = new CycList();
        aclList.add(CycObjectFactory.makeCycSymbol(acl.getPerformative()));
        aclList.add(CycObjectFactory.makeCycSymbol(":sender"));
        CycList senderAID = new CycList();
        aclList.add(senderAID);
        senderAID.add(CycObjectFactory.makeCycSymbol("agent-identifier"));
        senderAID.add(acl.getSenderAID().getName());
        CycList receiverAIDSet = new CycList();
        receiverAIDSet.add(CycObjectFactory.makeCycSymbol("set"));
        CycList receiverAID = new CycList();
        receiverAIDSet.add(receiverAID);
        receiverAID.add(CycObjectFactory.makeCycSymbol("agent-identifier"));
        receiverAID.add(acl.getSenderAID().getName());
        if (acl.getReplyToAIDs() != null) {
            aclList.add(CycObjectFactory.makeCycSymbol(":reply-to"));
            CycList replyToAIDs = new CycList();
            replyToAIDs.add(CycObjectFactory.makeCycSymbol("set"));
            for (int i = 0; i < acl.getReplyToAIDs().size(); i++) {
                CycList replyToAID = new CycList();
                replyToAIDs.add(replyToAID);
                replyToAID.add(CycObjectFactory.makeCycSymbol("agent-identifier"));
                replyToAID.add(((AgentID) replyToAIDs.get(i)).getName());
            }
        }
        aclList.add(CycObjectFactory.makeCycSymbol(":content"));
        aclList.add((CycList) CycObjectFactory.unmarshall((String) acl.getContentObject()));
        if (acl.getLanguage() != null) {
            aclList.add(CycObjectFactory.makeCycSymbol(":language"));
            aclList.add(acl.getLanguage());
        }
        if (acl.getReplyWith() != null) {
            aclList.add(CycObjectFactory.makeCycSymbol(":reply-with"));
            aclList.add(acl.getReplyWith());
        }
        if (acl.getOntology() != null) {
            aclList.add(CycObjectFactory.makeCycSymbol(":ontology"));
            aclList.add(acl.getOntology());
        }
        if (acl.getProtocol() != null) {
            aclList.add(CycObjectFactory.makeCycSymbol(":protocol"));
            aclList.add(acl.getProtocol());
        }
        return aclList;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "[" + this.getClass() + " named " + myAgentName + "]";
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