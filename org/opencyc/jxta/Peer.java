package  org.opencyc.jxta;

import  java.io.*;
import  javax.naming.TimeLimitExceededException;
import  net.jxta.peergroup.*;
import  net.jxta.exception.*;
import  net.jxta.protocol.*;
import  net.jxta.resolver.*;
import  net.jxta.impl.protocol.ResolverResponse;
import  fipaos.ont.fipa.*;
import  fipaos.ont.fipa.fipaman.*;
import  fipaos.util.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.cycagent.*;
import  org.opencyc.xml.*;
import  org.opencyc.util.*;

/**
 * Provides generic jxta peer capablilities for cyc agents.<p>
 *
 * See www.jxta.org for more information on the Juxtaposition peer to peer
 * infrastructure.  OpenCyc uses the JXTA Resolver Service to pass messages to
 * discovered JXTA peers.  Between OpenCyc peers, the message content is a
 * FIPA-OS envelope and an enclosed FIPA-OS message represented in XML format.
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

public class Peer implements QueryHandler {

    /**
     * A name to use to register the example handler with the Resolver service.
     */
    public static final String ECHO_HANDLER_NAME = "opencyc.org-echo-handler";

    /**
     * JXTA credentials
     */
    public static final String JXTA_CREDENTIALS = "JXTACRED";

    /**
     * outbound message serial number.
     */
    public int msgSerialNumber = 0;

    /**
     * jxta peer group
     */
    protected PeerGroup peerGroup = null;

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
     * CycAccess connection to the local cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * Constructs a new Peer object.
     */
    public Peer () {
        Log.makeLog();
        cycAccess = new CycAccess();
    }

    /**
     * Closes the peer's local cyc connection and disconnects the peer from JXTA.
     */
    public void close () {
        if (verbosity > 2)
            Log.current.println("Closing the peer's Cyc connection");
        if (cycAccess != null)
            cycAccess.close();
        if (verbosity > 2)
            Log.current.println("Stopping the peer's JXTA services");
        peerGroup.stopApp();
    }

    /**
     * Provides a main method for testing this class.
     *
     * @param args not used
     */
    public static void main (String args[]) {
        Log.makeLog();
        Peer peer = new Peer();
        peer.startJxta();
        peer.test();
        peer.close();
        System.exit(0);
    }

    /**
     * Tests the Peer.
     */
    protected void test () {
        Log.current.println("\nHello from JXTA group " + peerGroup.getPeerGroupName());
        Log.current.println("  Group ID = " + peerGroup.getPeerGroupID().toString());
        Log.current.println("  Peer name = " + peerGroup.getPeerName());
        Log.current.println("  Peer ID = " + peerGroup.getPeerID().toString() + "\n");

        doResolverEcho("hello world!", 10);

        CycApiResponseMsg cycApiResponseMsg = new CycApiResponseMsg(queryAcl);
        ResolverResponse resolverResponse = new ResolverResponse(HANDLER_NAME,
                                                                 JXTA_CREDENTIALS,
                                                                 0,
                                                                 cycApiResponseMsg.toString());

        resolverService.sendResponse(null, resolverResponse);


        String echoReplyXml = (String) replyAcl.getContentObject();
        Log.current.println("\nReceived from remote agent " + remoteAgentName + ":" + echoReplyXml);

    }

    /**
     * Sends the echo message to peers via the ResolverService, and displays the echo responses.
     *
     * @param echoMessageText the echo message
     * @param waitSeconds seconds to wait for peer responses
     */
    public void doResolverEcho (String echoMessageText, int waitSeconds) {
        ACL acl = new ACL();
        acl.setPerformative(FIPACONSTANTS.REQUEST);
        AgentID senderAid = new AgentID();
        agentID.setName(peerGroup.getPeerName());
        acl.setSenderAID(senderAid);
        String echoRequestXml =
            "\n<list>\n" +
            "  <symbol>ECHO</symbol>\n" +
            "  <string>" + TextUtil.doEntityReference(echoMessageText) + "</string>\n" +
            "</list>";
        acl.setContentObject(echoRequestXml, ACL.BYTELENGTH_ENCODING);
        acl.setLanguage(FIPACONSTANTS.XML);
        acl.setOntology(AgentCommunityAdapter.CYC_ECHO_ONTOLOGY);
        acl.setReplyWith(nextMessageId());
        acl.setProtocol(FIPACONSTANTS.FIPA_REQUEST);

        System.out.println("\nBroadcasting to peers:" + echoRequestXml +
                           "\nwaiting " + waitSeconds + " seconds for responses");
        resolverQuery(acl);
        try {
            Thread.sleep(1000 * waitSeconds);
        }
        catch (Exception e) {
        }
        Log.current.errorPrintln(waitSeconds + " elapsed");
    }


    /**
     * Starts the JXTA system and obtains a reference to the netPeerGroup.
     */
    protected void startJxta () {
        if (verbosity > 2)
            Log.current.println("Starting the peer's JXTA services");
        try {
            // create and start the default JXTA NetPeerGroup
            peerGroup = PeerGroupFactory.newNetPeerGroup();
        }
        catch (PeerGroupException e) {
            // could not instantiate the group, print the stack and exit
            Log.current.errorPrintln("fatal error : group creation failure");
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Registers the echo service.
     */
    protected void registerEchoService () {
        // Get the Resolver service for the current peer group.
        Log.current.errorPrintln("\nGetting ResolverService");
        ResolverService resolverService = peerGroup.getResolverService();
        String localPeerId = peerGroup.getPeerID().toString();

        // Register the handler with the resolver service.
        Log.current.errorPrintln("Registering with ResolverService");
        resolverService.registerHandler(ECHO_HANDLER_NAME, this);
    }

    /**
     * Queries the ResolverService with the given ACL.
     *
     * @param queryAcl the Agent Communication Language query
     */
    protected resolverQuery (ACL queryAcl) {
    }

    /**
     * Processes the resolver query, and generate the response.
     *
     * @param resolverQueryMsg the message which this handler processes
     * @return the response to this message
     */
    public ResolverResponseMsg processQuery(ResolverQueryMsg query)
        throws IOException,
               NoResponseException,
               DiscardQueryException,
               ResendQueryException {
        Log.current.println("Processing query...");
        ResolverResponse resolverResponse;
        CycApiQueryMsg cycApiQueryMsg;
        double answer = 0.0;

        try {
            cycApiQueryMsg =
                new CycApiQueryMsg(new ByteArrayInputStream((query.getQuery()).getBytes()));
        }
        catch (Exception e) {
            throw new IOException();
        }
        CycApiResponseMsg cycApiResponseMsg = new CycApiResponseMsg(cycApiQueryMsg.getBase(),
                                                                    cycApiQueryMsg.getPower(),
                                                                    answer);

        // Wrap the response message in a resolver response message.
        resolverResponse = new ResolverResponse(HANDLER_NAME,
                                                JXTA_CREDENTIALS,
                                                query.getQueryId(),
                                                cycApiResponseMsg.toString());

        return resolverResponse;
    }

    /**
     * Process a Resolver response message.
     *
     * @param resolverResponseMsg the response message to a previous query.
     */
    public void processResponse(ResolverResponseMsg resolverResponseMsg) {
        Log.current.println("Processing response...");
        CycApiResponseMsg cycApiResponseMsg;

        try {
            // Extract the message from the Resolver response.
            cycApiResponseMsg =
                new CycApiResponseMsg(new ByteArrayInputStream((resolverResponseMsg.getResponse()).getBytes()));

            // Print out the answer given in the response.
            Log.current.println("\nThe value of " + cycApiResponseMsg.getBase() +
                                " raised to " + cycApiResponseMsg.getPower() +
                                " is " + cycApiResponseMsg.getAnswer() + "\n");
        }
        catch (Exception e) {
            // This is not the right type of response message, or
            // the message is improperly formed. Ignore the message,
            // do nothing.
        }
    }

    /**
     * Returns the next message serial number identifier.
     *
     * @return the next message serial number identifier
     */
    public String nextMessageId () {
        return "message" + ++msgSerialNumber;
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



