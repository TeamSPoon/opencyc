package org.opencyc.cycagent;

import java.io.IOException;

import org.opencyc.api.CycConnection;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.util.Log;

import fipaos.ont.fipa.ACL;
import fipaos.ont.fipa.FIPACONSTANTS;
import fipaos.ont.fipa.fipaman.AgentID;
import fipaos.parser.ParserException;

/**
 * Provides a proxy for a cyc agent on the CoABS grid or FIPA-OS agent community.<p>
 *
 * An instance of this class is created for each unique cyc agent which makes
 * itself known to the CycProxy.  A cyc image can host one or more cyc
 * agents.  Each message envelope from a cyc agent contains a parameter to
 * indicate which agent agent community processes the messge - either the CoABS
 * grid (Darpa & gov) or the FIPA-OS platform (OpenCyc).
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

public class CycAgentProxy extends GenericAgent {

    /**
     * One CycConnection object to send and receive asynchronous messsages with the Cyc server.  All
     * Cyc-hosted agents share this bidirectional connection.
     */
    public CycConnection agentsCycConnection;

    /**
     * Constructs a CycProxy object for the given agent on both agent communities.
     *
     * @param myAgentName name of the local agent
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CycAgentProxy(String myAgentName, int verbosity) {
        super(myAgentName, verbosity);
    }

    /**
     * Constructs a CycProxy object for the given agent on the given agent community.
     *
     * @param myAgentName name of the local agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CycAgentProxy(String myAgentName, int remoteAgentCommunity, int verbosity) {
        super(myAgentName, remoteAgentCommunity, verbosity);
    }
    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (int remoteAgentCommunity, ACL acl) {
        super.messageReceived(remoteAgentCommunity, acl);
        if (! messageConsumed)
            handleMessageToCyc(remoteAgentCommunity, acl);
    }

    /**
     * Handles a message from a remote agent to a Cyc-hosted agent.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param processApiRequest the echo request Agent Communication Language message
     */
    public void handleMessageToCyc(int remoteAgentCommunity, ACL acl) {
        CycList fipaTransportMessage = new CycList();
        CycList envelope = new CycList();
        fipaTransportMessage.add(envelope);
        envelope.add(CycObjectFactory.makeCycSymbol("envelope"));
        envelope.add(CycObjectFactory.makeCycSymbol(":to"));
        envelope.add(acl.getReceiverAID().getName());
        envelope.add(CycObjectFactory.makeCycSymbol(":from"));
        envelope.add(acl.getSenderAID().getName());
        envelope.add(CycObjectFactory.makeCycSymbol(":X-agent-community"));
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
            envelope.add(CycObjectFactory.makeCycSymbol(":COABS"));
        else
            envelope.add(CycObjectFactory.makeCycSymbol(":FIPA-OS"));
        CycList payload = new CycList();
        fipaTransportMessage.add(payload);
        payload.add(CycObjectFactory.makeCycSymbol("payload"));
        try {
            payload.add(aclToCycList(acl));
        }
        catch (Exception e) {
            Log.current.errorPrintln("Exception while converting to CycList\n" + acl);
            return;
        }
        if (verbosity > 2)
            Log.current.println("Sending message to Cyc\n" + fipaTransportMessage.safeToString());
        try {
            //agentsCycConnection.traceOnDetailed();
            agentsCycConnection.sendBinary(fipaTransportMessage);
        }
        catch (Exception e) {
            Log.current.errorPrintln("Exception while sending message to Cyc\n" + fipaTransportMessage);
            return;
        }
    }

    /**
     * Handles one inbound asynchronous message from a Cyc-hosted agent.  Creates a
     * CycAgentProxy object if the Cyc-hosted agent is new.  Forwards the inbound
     * message to the CycAgentProxy, which in turn forwards it to the agent
     * community.
     *
     * @param fipaTransportMessage the message from a Cyc-hosted agent
     */
    protected void handleMessageFromCyc(CycList fipaTransportMessage) {
        if (verbosity > 2)
            Log.current.println("\nCycAgentProxy received message from cyc agent for forwarding\n" + fipaTransportMessage);
        CycList envelope = (CycList) ((CycList) fipaTransportMessage).second();
        CycSymbol agentCommunity =
            (CycSymbol) envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":X-agent-community"));
        AgentCommunityAdapter agentCommunityAdapter = null;
        if (agentCommunity.equals(CycObjectFactory.makeCycSymbol(":COABS")))
            agentCommunityAdapter = coAbsCommunityAdapter;
        else if (agentCommunity.equals(CycObjectFactory.makeCycSymbol(":FIPA-OS")))
            agentCommunityAdapter = coAbsCommunityAdapter;
        else {
            if (agentCommunity == null) {
                Log.current.println("agent community is missing");
                throw new RuntimeException("agent community is missing");
            }
            else {
                Log.current.println(agentCommunity + " is neither :COABS nor :FIPA-OS");
                throw new RuntimeException(agentCommunity + " is neither :COABS nor :FIPA-OS");
            }
        }
        if (! (((CycList) fipaTransportMessage).third() instanceof CycList)) {
            Log.current.println(((CycList) fipaTransportMessage).third() +
                                "\npayload is not a CycList");
            throw new RuntimeException(((CycList) fipaTransportMessage).third() +
                                       "\npayload is not a CycList");
        }
        CycList payload = (CycList) ((CycList) fipaTransportMessage).third();
        if (! (payload.second() instanceof CycList)) {
            Log.current.println(payload.second() + "\naclList is not a CycList");
            throw new RuntimeException(payload.second() + "\naclList is not a CycList");
        }
        CycList aclList = (CycList) payload.second();
        ACL acl = new ACL();
        acl.setPerformative(aclList.first().toString());
        Object senderObj = aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":sender"));
        if (! (senderObj instanceof CycList)) {
            Log.current.println(senderObj + "\nsenderObj is not a CycList");
            throw new RuntimeException(senderObj + "\nsenderObj is not a CycList");
        }
        AgentID senderAID = null;
        try {
            senderAID = new AgentID(((CycList) senderObj).cyclify());
        }
        catch (ParserException e) {
            Log.current.println(e.getMessage() + "\ncannot parse sender " + senderObj);
            throw new RuntimeException(e.getMessage() + "\ncannot parse sender " + senderObj);
        }
        acl.setSenderAID(senderAID);
        Object receiverListObj = aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":receiver"));
        if (! (receiverListObj instanceof CycList)) {
            Log.current.println(receiverListObj + "\nreceiverObj is not a CycList");
            throw new RuntimeException(receiverListObj + "\nreceiverObj is not a CycList");
        }
        Object receiverObj = ((CycList) receiverListObj).second();
        AgentID receiverAID = null;
        try {
            receiverAID = new AgentID(((CycList) receiverObj).cyclify());
        }
        catch (ParserException e) {
            Log.current.println(e.getMessage() + "\ncannot parse receiver " + receiverObj);
            throw new RuntimeException(e.getMessage() + "\ncannot parse receiver " + receiverObj);
        }
        acl.setReceiverAID(receiverAID);
        Object contentObj = aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":content"));
        if (! (contentObj instanceof CycList)) {
            Log.current.println(contentObj + "\ncontentObj is not a CycList");
            throw new RuntimeException(contentObj + "\ncontentObj is not a CycList");
        }
        String contentXml = null;
        try {
            contentXml = "\n" + ((CycList) contentObj).toXMLString();
        }
        catch (IOException e) {
            Log.current.println(e.getMessage() +
                                "\nCannot convert to XML string " + contentObj);
            throw new RuntimeException(e.getMessage() +
                                       "\nCannot convert to XML string " + contentObj);
        }
        acl.setContentObject(contentXml, ACL.BYTELENGTH_ENCODING);
        acl.setLanguage(FIPACONSTANTS.XML);
        acl.setOntology(aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":ontology")).toString());
        acl.setReplyWith(agentCommunityAdapter.nextMessageId());

        try {
            agentCommunityAdapter.sendMessage(acl);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.current.println("Exception when sending\n" + acl + "\n" + e.getMessage());
        }

    }
}