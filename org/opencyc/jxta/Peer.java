package  org.opencyc.jxta;

import  java.io.*;
import  java.util.*;
import  javax.naming.TimeLimitExceededException;
import  net.jxta.peergroup.*;
import  net.jxta.peer.*;
import  net.jxta.exception.*;
import  net.jxta.protocol.*;
import  net.jxta.resolver.*;
import  net.jxta.rendezvous.*;
import  net.jxta.impl.protocol.*;
import  fipaos.ont.fipa.*;
import  fipaos.ont.fipa.fipaman.*;
import  fipaos.util.*;
import  fipaos.parser.ParserException;
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
     * outbound message serial number
     */
    public int msgSerialNumber = 0;

    /**
     * jxta ResolverService query id
     */
    public int queryId = 0;

    /**
     * jxta peer group
     */
    protected PeerGroup peerGroup = null;

    /**
     * jxta ResolverService
     */
    protected ResolverService resolverService;

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
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
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
        peer.registerEchoService();
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
        Log.current.println("  Peer ID = " + peerGroup.getPeerID().toString());
        RendezVousService rendezVousService = peerGroup.getRendezVousService();
        if (rendezVousService.isRendezVous())
            Log.current.println("  is a rendezvous server");
        else
            Log.current.println("  is not a rendezvous server");
        if (rendezVousService.isConnectedToRendezVous())
            Log.current.println("  is connected to a rendezvous server");
        else
            Log.current.println("  is not connected to a rendezvous server");

        doResolverEcho("hello world!", 60);
    }

    /**
     * Sends the echo message to peers via the ResolverService, and displays the echo responses.
     *
     * @param echoMessageText the echo message
     * @param waitSeconds seconds to wait for peer responses
     */
    public void doResolverEcho (String echoMessageText, int waitSeconds) {
        ACL queryAcl = new ACL();
        queryAcl.setPerformative(FIPACONSTANTS.REQUEST);
        AgentID senderAid = new AgentID();
        senderAid.setName(peerGroup.getPeerName());
        queryAcl.setSenderAID(senderAid);
        String echoRequestXml =
            "\n<list>\n" +
            "  <symbol>ECHO</symbol>\n" +
            "  <string>" + TextUtil.doEntityReference(echoMessageText) + "</string>\n" +
            "</list>";
        queryAcl.setContentObject(echoRequestXml, ACL.BYTELENGTH_ENCODING);
        queryAcl.setLanguage(FIPACONSTANTS.XML);
        queryAcl.setOntology(AgentCommunityAdapter.CYC_ECHO_ONTOLOGY);
        queryAcl.setReplyWith(nextMessageId());
        queryAcl.setProtocol(FIPACONSTANTS.FIPA_REQUEST);

        System.out.println("\nBroadcasting to peers:" + echoRequestXml +
                           "\nwaiting " + waitSeconds + " seconds for responses");
        ResolverQuery resolverQuery =
            new ResolverQuery(ECHO_HANDLER_NAME,
                              JXTA_CREDENTIALS,
                              peerGroup.getPeerID().toString(),
                              queryAcl.toString(),
                              ++queryId);
        sendQuery(null, resolverQuery);
        try {
            Thread.sleep((long) (1000 * waitSeconds));
        }
        catch (Exception e) {
        }
        Log.current.println("\n" + waitSeconds + " seconds elapsed\n");
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
        Log.current.println("\nGetting ResolverService");
        resolverService = peerGroup.getResolverService();
        String localPeerId = peerGroup.getPeerID().toString();

        // Register the handler with the resolver service.
        Log.current.println("Registering with ResolverService");
        resolverService.registerHandler(ECHO_HANDLER_NAME, this);
    }

    /**
     * Sends the query to the given peer.  If the peer is null, then the
     * query is propagated.
     *
     * @param resolverQueryMsg the query
     * @param receivingPeer the peer to which the query will be sent
     */
    protected void sendQuery (String receivingPeer, ResolverQueryMsg resolverQueryMsg) {
        System.out.println("\nsendQuery:" + resolverQueryMsg + " to peer " + receivingPeer);
        resolverService.sendQuery(receivingPeer, resolverQueryMsg);
    }

    /**
     * Receives the echo query from a peer, and generates the echo response.
     *
     * @param resolverQueryMsg the message which this handler processes
     * @return the response to this message
     */
    public ResolverResponseMsg processQuery(ResolverQueryMsg resolverQueryMsg)
        throws IOException,
               NoResponseException,
               DiscardQueryException,
               ResendQueryException {
        ACL queryAcl = null;
        try {
            queryAcl = new ACL(resolverQueryMsg.getQuery());
        }
        catch (ParserException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
        Log.current.println("Processing " + ECHO_HANDLER_NAME +
                            " query\n" + queryAcl.toString());
        ACL responseAcl = (ACL) queryAcl.clone();
        AgentID senderAid = new AgentID();
        senderAid.setName(peerGroup.getPeerName());
        responseAcl.setSenderAID(senderAid);
        responseAcl.setReceiverAID(queryAcl.getSenderAID());
        ResolverResponse resolverResponse =
            new ResolverResponse(ECHO_HANDLER_NAME,
                                 JXTA_CREDENTIALS,
                                 resolverQueryMsg.getQueryId(),
                                 responseAcl.toString());

        Log.current.println("Responding with\n" + responseAcl.toString());
        return resolverResponse;
    }

    /**
     * Processes the echo response messages.
     *
     * @param resolverResponseMsg the response message to a previous query.
     */
    public void processResponse(ResolverResponseMsg resolverResponseMsg) {
        ACL responseAcl = null;
        try {
            responseAcl = new ACL(resolverResponseMsg.getResponse());
        }
        catch (ParserException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
        Log.current.println("Received " + ECHO_HANDLER_NAME +
                            " response\n" + responseAcl.toString());
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



