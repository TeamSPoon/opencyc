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
     * jxta peer group
     */
    protected PeerGroup peerGroup = null;

    /**
     * Constructs a new Peer object.
     */
    public Peer () {
        Log.makeLog();
    }

    /**
     * Provides a main method for testing this class.
     *
     * @param args not used
     */
    public static void main (String args[]) {
        Log.makeLog();
        Log.current.println("Starting JXTA ....");
        Peer peer = new Peer();
        peer.startJxta();
        peer.test();
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
        double base = 0.0;
        double power = 0.0;
        double answer = 0;
        CycApiResponseMsg cycApiResponseMsg = new CycApiResponseMsg(base, power, answer);
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
        answer = Math.pow(cycApiQueryMsg.getBase(), cycApiQueryMsg.getPower());

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
}



