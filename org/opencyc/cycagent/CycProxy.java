package org.opencyc.cycagent;

import java.io.*;
import java.util.*;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import  fipaos.parser.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import org.opencyc.xml.*;

/**
 * Provides a proxy for a cyc agent on the CoABS grid or FIPA-OS agent community.<p>
 *
 * An instance of this class is created for each unique cyc agent which makes
 * itself known to the agent manager.  A cyc image can host one or more cyc
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

public class CycProxy extends GenericAgent {

    /**
     * Cached CycConnection objects which preserve Cyc session state.
     * myAgentName --> CycConnection instance
     */
    protected static Hashtable cycApiConnectionCache = new Hashtable();

    /**
     * One CycConnection object to send and receive asynchronous messsages with the Cyc server.  All
     * Cyc-hosted agents represented by this CycProxy object share this bidirectional connection.
     */
    public CycConnection agentsCycConnection;

    /**
     * Constructs a CycProxy object for the given agent on both agent communities.
     *
     * @param myAgentName name of the local agent
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CycProxy(String myAgentName, int verbosity) {
        super(myAgentName, verbosity);
        if (Log.current == null)
            Log.makeLog();
    }

    /**
     * Constructs a CycProxy object for the given agent on the given agent community.
     *
     * @param myAgentName name of the local agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CycProxy(String myAgentName, int remoteAgentCommunity, int verbosity) {
        super(myAgentName, remoteAgentCommunity, verbosity);
        if (Log.current == null)
            Log.makeLog();
    }

    /**
     * Handles inbound asynchronous messages from Cyc-hosted agents.  The process blocks while
     * awaiting the next message.
     */
    protected void handleMessagesFromCyc() {
        while (true) {
            if (verbosity > 2)
                Log.current.print("Awaiting message from any Cyc agent");
            try {
                Object[] response = this.agentsCycConnection.receiveBinary();
                this.handleMessageFromCyc(response[1]);
            }
            catch (Exception e) {
                Log.current.println("Exception while awaiting message from any cyc agent\n" + e.getMessage());
                return;
            }
        }
    }

    /**
     * Handles one inbound asynchronous message from a Cyc-hosted agent.
     *
     * @param fipaTransportMessage the message from a Cyc-hosted agent
     */
    protected void handleMessageFromCyc(Object fipaTransportMessage) {
        if (verbosity > 2)
            Log.current.println("\nreceived message from cyc agent for forwarding\n" + fipaTransportMessage);
        if (! (fipaTransportMessage instanceof CycList)) {
            Log.current.println(fipaTransportMessage + "\nfipaTransportMessage is not a CycList");
            throw new RuntimeException(fipaTransportMessage + "\nfipaTransportMessage is not a CycList");
        }
        if (((CycList) fipaTransportMessage).size() != 3) {
            Log.current.println(fipaTransportMessage + "\nfipaTransportMessage has invalid length of " +
                                ((CycList) fipaTransportMessage).size());
            throw new RuntimeException(fipaTransportMessage + "\nfipaTransportMessage has invalid length of " +
                                ((CycList) fipaTransportMessage).size());
        }
        if (! (((CycList) fipaTransportMessage).first().equals(CycObjectFactory.makeCycSymbol("FIPA-ENVELOPE")))) {
            Log.current.println("Invalid cyc message directive " + fipaTransportMessage);
            throw new RuntimeException("Invalid cyc message directive " + fipaTransportMessage);
        }

        if (! (((CycList) fipaTransportMessage).second() instanceof CycList)) {
            Log.current.println(((CycList) fipaTransportMessage).second() +
                                "\nenvelope is not a CycList");
            throw new RuntimeException(((CycList) fipaTransportMessage).second() +
                                       "\nenvelope is not a CycList");
        }
        CycList envelope = (CycList) ((CycList) fipaTransportMessage).second();
        CycSymbol agentCommunity =
            (CycSymbol) envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol("X-agent-community"));
        AgentCommunityAdapter agentCommunityAdapter = null;
        if (agentCommunity.equals(CycObjectFactory.makeCycSymbol(":COABS")))
            agentCommunityAdapter = coAbsCommunityAdapter;
        else if (agentCommunity.equals(CycObjectFactory.makeCycSymbol(":FIPA-OS")))
            agentCommunityAdapter = coAbsCommunityAdapter;
        else {
            Log.current.println(agentCommunity + " is neither :COABS nor :FIPA-OS");
            throw new RuntimeException(agentCommunity + " is neither :COABS nor :FIPA-OS");
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
        Object senderObj = CycObjectFactory.makeCycSymbol(":sender");
        if (! (senderObj instanceof CycList)) {
            Log.current.println(senderObj + "\nsenderObj is not a CycList");
            throw new RuntimeException(senderObj + "\nsenderObj is not a CycList");
        }
        AgentID senderAID = null;
        try {
            senderAID = new AgentID(((CycList) senderObj).cyclify());
        }
        catch (ParserException e) {
            Log.current.println(e.getMessage() + "\nannot parse sender " + senderObj);
            throw new RuntimeException(e.getMessage() + "\nannot parse sender " + senderObj);
        }
        acl.setSenderAID(senderAID);
        Object receiverObj = CycObjectFactory.makeCycSymbol(":receiver");
        if (! (receiverObj instanceof CycList)) {
            Log.current.println(receiverObj + "\nreceiverObj is not a CycList");
            throw new RuntimeException(receiverObj + "\nreceiverObj is not a CycList");
        }
        AgentID receiverAID = null;
        try {
            receiverAID = new AgentID(((CycList) receiverObj).cyclify());
        }
        catch (ParserException e) {
            Log.current.println(e.getMessage() + "\ncannot parse receiver " + receiverObj);
            throw new RuntimeException(e.getMessage() + "\nannot parse receiver " + receiverObj);
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
        acl.setOntology(AgentCommunityAdapter.CYC_API_ONTOLOGY);
        acl.setReplyWith(agentCommunityAdapter.nextMessageId());

        if (verbosity > 0)
            Log.current.println("Processing the message without waiting for the reply");

        try {
            agentCommunityAdapter.sendMessage(acl);
        }
        catch (Exception e) {
            Log.current.println("Exception when sending\n" + acl + "\n" + e.getMessage());
        }

    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (int remoteAgentCommunity, ACL acl) {
        super.messageReceived(remoteAgentCommunity, acl);
        if (! messageConsumed) {
            if (acl.getOntology().equals(AgentCommunityAdapter.CYC_API_ONTOLOGY))
                processApiRequest(remoteAgentCommunity, acl);
            else
                handleMessageToCyc(remoteAgentCommunity, acl);
        }
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
        try {
            agentsCycConnection.sendBinary(fipaTransportMessage);
        }
        catch (Exception e) {
            Log.current.errorPrintln("Exception while sending message to Cyc\n" + fipaTransportMessage);
            return;
        }
    }

    /**
     * Processes a cyc api request from another agent.
     *
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param processApiRequest the echo request Agent Communication Language message
     */
    public void processApiRequest (int remoteAgentCommunity, ACL apiRequestAcl) {
        CycList apiRequest = null;
        String senderName = apiRequestAcl.getSenderAID().getName();
        CycConnection cycConnection = (CycConnection) cycApiConnectionCache.get(senderName);
        try {
            if (cycConnection == null) {
                cycConnection = new CycConnection();
                cycApiConnectionCache.put(senderName, cycConnection);
                if (verbosity > 1)
                    Log.current.print("created cyc connection to " + cycConnection.connectionInfo() +
                                      "\nfor " + senderName);
            }
            String contentXml = (String) apiRequestAcl.getContentObject();
            apiRequest = (CycList) CycObjectFactory.unmarshall(contentXml);
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return;
        }

        if (apiRequest.first().equals(CycObjectFactory.makeCycSymbol("cyc-kill"))) {
            CycObjectFactory.removeCaches((CycConstant) apiRequest.second());
            if (verbosity > 2)
                System.out.println("killed cached version of " + (CycConstant) apiRequest.second());
        }

        boolean cycConnectionEnded = false;
        try {
            if (apiRequest.equals(CycObjectFactory.END_CYC_CONNECTION)) {
                if (verbosity > 0)
                    Log.current.println("ending cyc connection for " + senderName);
                    cycConnection.close();
                cycApiConnectionCache.remove(senderName);
                cycConnectionEnded = true;
                }
            }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }

        Object [] response = {null, null};
        if (cycConnectionEnded) {
            response[0] = Boolean.TRUE;
            response[1] = CycObjectFactory.nil;
        }
        else {
            try {
                if (verbosity == 0)
                    cycConnection.traceOff();
                else if (verbosity < 3)
                    cycConnection.traceOn();
                else
                    cycConnection.traceOnDetailed();
                response = cycConnection.converse(apiRequest);
            }
            catch (Exception e) {
                Log.current.errorPrintln(e.getMessage());
                Log.current.printStackTrace(e);
                return;
            }
        }
        ACL apiReplyAcl = (ACL) apiRequestAcl.clone();
        apiReplyAcl.setPerformative(FIPACONSTANTS.INFORM);
        apiReplyAcl.setSenderAID(apiRequestAcl.getReceiverAID());
        apiReplyAcl.setReceiverAID(apiRequestAcl.getSenderAID());
        CycList responseCycList = new CycList();
        if (response[0].equals(Boolean.TRUE))
            responseCycList.add(CycObjectFactory.t);
        else if (response[0].equals(Boolean.FALSE))
            responseCycList.add(CycObjectFactory.nil);
        else
            new RuntimeException("response[0] not Boolean " + response[0]);
        responseCycList.add(response[1]);
        try {
            apiReplyAcl.setContentObject("\n" + Marshaller.marshall(responseCycList));
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception while marshalling " + responseCycList);
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return;
        }
        apiReplyAcl.setReplyWith(null);
        apiReplyAcl.setInReplyTo(apiRequestAcl.getReplyWith());
        try {
            if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
                coAbsCommunityAdapter.sendMessage(apiReplyAcl);
            else
                fipaOsCommunityAdapter.sendMessage(apiReplyAcl);
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception " + e.getMessage() +
                                     "\nwhile replying to api request with\n" + apiReplyAcl);
        }
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