package  org.opencyc.cycagent;

import  java.net.Socket;
import  java.io.PrintWriter;
import  java.io.*;
import  java.util.Collection;
import  fipaos.ont.fipa.fipaman.*;
import  fipaos.ont.fipa.*;
import  fipaos.parser.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;

/**
 * Receives the communication initiation message from a Cyc server and creates the
 * corresponding CycProxy object.<p>
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

class CycProxyFactory implements Runnable {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Reference to parent's socket which is connected to a cyc client using the binary cfasl
     * protocol.
     */
    protected Socket cfaslSocket = null;

    /**
     * Agent Communication Language
     */
    protected String acl = null;

    /**
     * Manages connection to a cyc server.
     */
    protected CycConnection cycConnection;

    /**
     * Constructs a new CycProxyFactory object given a reference to
     * the cyc client's socket connection.
     */
    public CycProxyFactory (Socket cfaslSocket) {
        this.cfaslSocket = cfaslSocket;
    }

    /***
     * Executes the CycProxyFactory thread.
     */
    public void run () {
        Log.current.println("Begin CycProxyFactory thread");
        try {
            cycConnection = new CycConnection(cfaslSocket);
        }
        catch (Exception e) {
            Log.current.println("Exception creating CycConnection " + e.getMessage());
            throw new RuntimeException("Exception creating CycConnection: " + e.getMessage());
        }
        CycList cycToAgentManagerInitMessage = null;
        try {
            cycToAgentManagerInitMessage = (CycList) cycConnection.receiveBinary();
        }
        catch (Exception e) {
            Log.current.println("Exception reading CycConnection: " + e.getMessage());
            return;
        }
        /*
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
        acl.setReplyWith(AgentManager.agentManager.getAgentCommunityAdapter().nextMessageId());

        if (verbosity > 0)
            Log.current.println("Processing the message without waiting for the reply");

        try {
            AgentManager.agentManager.getAgentCommunityAdapter().sendMessage(acl);
        }
        catch (Exception e) {
            Log.current.println("Exception when sending\n" + acl + "\n" + e.getMessage());
        }
        */

        CycList acknowledgement = new CycList();
        acknowledgement.add(CycObjectFactory.t);
        acknowledgement.add(CycObjectFactory.t);

        try {
            cycConnection.sendBinary(acknowledgement);
        }
        catch (Exception e) {
            Log.current.println("Exception sending acknowledgement: " + e.getMessage());
            return;
        }
        close();
    }

    /**
     * Closes the cyc connection.
     */
    public void close () {
        try {
            cycConnection.close();
        }
        catch (Exception e) {
            Log.current.println("Exception closing socket i/o: " + e);
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






