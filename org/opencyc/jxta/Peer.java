package  org.opencyc.jxta;

import  java.io.*;
import  net.jxta.peergroup.*;
import  net.jxta.exception.*;
import  net.jxta.protocol.*;
import  net.jxta.resolver.*;
import  net.jxta.impl.protocol.ResolverResponse;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
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
    public static final String HANDLER_NAME = "CycApiHandler";

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
    public close () {
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
        if (verbosity > 2)
            Log.current.println("Starting the peer's JXTA services");
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

        // Get the Resolver service for the current peer group.
        Log.current.errorPrintln("\nGetting ResolverService");
        ResolverService resolverService = peerGroup.getResolverService();
        String localPeerId = peerGroup.getPeerID().toString();

        // Register the handler with the resolver service.
        Log.current.errorPrintln("Registering with ResolverService");
        resolverService.registerHandler(HANDLER_NAME, this);
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("cconcatenate"));
        command.add("hello");
        command.add(" ");
        command.add("world!");

        CycApiResponseMsg cycApiResponseMsg = new CycApiResponseMsg(queryAcl);
        ResolverResponse resolverResponse = new ResolverResponse(HANDLER_NAME,
                                                                 JXTA_CREDENTIALS,
                                                                 0,
                                                                 cycApiResponseMsg.toString());

        // Send (push) the (unsolicited) response using the resolver.
        Log.current.errorPrintln("Sending resolverResponse");
        try {
            Log.current.errorPrintln("Sleeping .5 second");
            Thread.sleep(500);
        }
        catch (Exception e) {
        }
        resolverService.sendResponse(null, resolverResponse);

        // Keep this process alive until killed by the user.
        while (true) {
            try {
                Log.current.errorPrintln("Sleeping 60 seconds while awaiting query from a peer");
                Thread.sleep(60000);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * Sends the echo message, and displays the echo response.
     *
     * @param echoMessageText
     */
    public void doEcho (String echoMessageText) {
        ACL acl = new ACL();
        acl.setPerformative(FIPACONSTANTS.REQUEST);
        AgentID senderAid = getAID(remoteAgentCommunity);
        acl.setSenderAID(senderAid);
        AgentID receiverAid = this.makeAID(remoteAgentName, remoteAgentCommunity);
        acl.addReceiverAID(receiverAid);
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
        System.out.println("\nSending to remote agent " + remoteAgentName + ":" + echoRequestXml);
        ACL replyAcl = null;
        try {
            //Timer timer = new Timer(this.oneMinuteDuration);
            Timer timer = new Timer(10000);
            replyAcl = converseMessage(acl, timer);
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
     * Sends an Agent Communication Language message and returns the reply.
     *
     * @param acl the Agent Communication Language message to be sent
     * @param timer the Timer object controlling the maximum wait time for a reply message,
     * after which an excecption is thrown.
     * @return the Agent Communication Language reply message which has been received for my agent
     *
     * @thows TimeLimitExceededException when the time limit is exceeded before a reply message
     * is received.
     */
    public ACL converseMessage (ACL acl, org.opencyc.util.Timer timer)
        throws TimeLimitExceededException, IOException {
        Message requestMessage = new BasicMessage(acl.getReceiverAID().getName(),
                                                  "fipa-xml",
                                                  acl.toString());
        requestMessage.setSender(regHelper.getAgentRep());
        if (verbosity > 2)
            Log.current.println("\nSending " + requestMessage.toString() +
                                "\n  receiver: " + requestMessage.getReceiver());
        String replyWith = acl.getReplyWith();
        waitingReplyThreads.put(replyWith, Thread.currentThread());
        String receiverName = acl.getReceiverAID().getName();
        AgentRep receivingAgentRep = this.lookupAgentRep(receiverName);
        if (receivingAgentRep == null)
            throw new IOException("Receiving agent " + receiverName + " not found");
        waitingReplyThreads.put(replyWith, Thread.currentThread());
        receivingAgentRep.addMessage(requestMessage);
        while (true)
            try {
                Thread.sleep(500);
                if (timer.isTimedOut())
                    throw new IOException("Time limit exceeded - " + timer.getElapsedSeconds() +
                                          " seconds, while awaiting reply message to " + replyWith);
            }
            catch (InterruptedException e) {
                ACL replyAcl = (ACL) replyMessages.get(replyWith);
                if (replyAcl == null)
                    throw new RuntimeException("No reply message for " + replyWith);
                waitingReplyThreads.remove(replyWith);
                if (verbosity > 2)
                    Log.current.println("\nReceived reply to " + replyWith + "\n" + replyAcl);
                return replyAcl;
            }
    }

    /**
     * Starts the JXTA system and obtains a reference to the netPeerGroup.
     */
    protected void startJxta () {
        try {
            // create and start the default JXTA NetPeerGroup
            peerGroup = PeerGroupFactory.newNetPeerGroup();
        } catch (PeerGroupException e) {
            // could not instantiate the group, print the stack and exit
            Log.current.errorPrintln("fatal error : group creation failure");
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Processes the Resolver query message and returns a response.
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

        // Perform the calculation.
        //answer = Math.pow(cycApiQueryMsg.getBase(), cycApiQueryMsg.getPower());

        // Create the response message.
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

}







