package org.opencyc.cycagent.fipaos;


import java.util.*;
import fipaos.agent.*;
import fipaos.agent.conversation.*;
import fipaos.agent.task.*;
import fipaos.mts.*;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import fipaos.parser.acl.ACLMessage;
import fipaos.platform.ams.AMSRegistrationException;
import fipaos.platform.df.DFRegistrationException;
import fipaos.util.DIAGNOSTICS;

/**
 * Provides a proxy for a cyc agent on the CoABS grid agent community.<p>
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

public class FipaOsProxy extends FIPAOSAgent {

    /**
     * Agent type
     */
    private static final String AGENT_TYPE = "cyc-api-proxy";

    /**
     * Constructs this FipaOsProxy.  Sets up an IdleTask as the default
     * ListenerTask for incoming messages, and starts off registration
     * with the AMS and DF.
     *
     * @param platform_profile_location The location of the platform.profile
     * (a fully qualified file name)
     * @param agent_name globally unique (within fipa-os)  identifier for the agent
     * @ownership the ownership of the agent
     */
    public FipaOsProxy(String platform_profile_location, String agent_name , String ownership) {
        super(platform_profile_location, agent_name, ownership);

        // Instantiate a new IdleTask to receieve all incoming communications which
        // are not associated with any-other Task
        setListenerTask(new IdleTask());

        // Now we can register with the local AMS (i.e. the platform) as the fist action our agent
        // makes
        try {
            // Attempt to register with AMS
            registerWithAMS();
            DIAGNOSTICS.println("Registered with AMS",
                                this, DIAGNOSTICS.LEVEL_MAX);
        }
        catch (AMSRegistrationException amsre) {
            // An exception has occured - this indicates that the AMS registration failed for some reason
            // Display the exception
            DIAGNOSTICS.println(amsre, this, DIAGNOSTICS.LEVEL_MAX);

            // We can easily find the exception reason from the exception....
            String reason = amsre.getExceptionReason();

            // If the exception reason is not "already-registered", we can probably ignore the failure
            // (since this Agent was probably not de-registered the last time it ran!)
            if (reason == null || !reason.equals(FIPAMANCONSTANTS.AGENT_ALREADY_REGISTERED)) {
                // Shutdown nicely :)
                shutdown();
                return;
            }
        }

        // Now we can register with the local DF
        try {
            // Register with DF
            registerWithDF(AGENT_TYPE);
            DIAGNOSTICS.println("Registered with DF",
                                this, DIAGNOSTICS.LEVEL_MAX);
        }
        catch (DFRegistrationException dfre) {
            // An exception has occured - this indicates that the DF registration failed for some reason
            // Display the exception
            DIAGNOSTICS.println(dfre, this, DIAGNOSTICS.LEVEL_MAX);

            // We can easily find the exception reason from the exception....
            String reason = dfre.getExceptionReason();

            if (reason == null
                    || !reason.equals(FIPAMANCONSTANTS.AGENT_ALREADY_REGISTERED)) {
                // Shutdown nicely :)
                shutdown();
                return;
            }
        }
    }

    /**
     * Shuts down this agent.
     */
    public synchronized void shutdown() {
        // Check if we've registered with the DF
        if (registeredWithDF() == true) {
            try {
                // Attempt to deregister with DF
                deregisterWithDF();
                DIAGNOSTICS.println("Deregistered with DF",
                                    this, DIAGNOSTICS.LEVEL_MAX);
            }
            catch (DFRegistrationException dfre) {
                // A problem deregistering occured..... we can obtain the reason though!
                String reason = dfre.getExceptionReason();

                DIAGNOSTICS.println(dfre + ":" + reason,
                                    this, DIAGNOSTICS.LEVEL_MAX);
            }
        }

        // Check if we're registered with the AMS
        if (registeredWithAMS() == true) {
            try {
                // Deregister with AMS
                deregisterWithAMS();
                DIAGNOSTICS.println("Deregistered with AMS",
                                    this, DIAGNOSTICS.LEVEL_MAX);
            }
            catch (AMSRegistrationException amsre) {
                // A problem deregistering occured..... we can obtain the reason though!
                String reason = amsre.getExceptionReason();

                DIAGNOSTICS.println(amsre + ":" + reason,
                                    this, DIAGNOSTICS.LEVEL_MAX);
            }
        }

        // Now call shutdown in the agent shell to release the core components
        super.shutdown();
    }

    /**
     * This method is invoked when GUI should be shown - generally
     * this is invoked by the AgentLoader when the Agent is in the List
     * of running Agents - the default implementation in FIPAOSAgent does nothing.
     */
    public void activate() {
        // Check if we've got a GUI
    }


    /**
     * <ul><li>On creation, spawns a DFSearchTask instance with the
     * intent of locating other PingAgent instances on the local Agent platform.
     * Upon reciept of the DFSearchTask's results, the Agent's located are added
     * to the list of known (and "alive") PingAgent's within the PingAgent.
     * <li>The IdleTask periodically instantiates PingAllTask's, which is achieved by
     * using WaitTask's to notify it every 5 seconds.
     * <li>On reciept of an incoming "request" message, a ApiResponseTask is instantiated.
     * </ul>
     */
    public class IdleTask extends Task {
        public IdleTask() {
            // Don't do anything on instantiation
        }


        /**
         * Invoked when the IdleTask has been successfully initialised.
         * This method may be invoked from a thread other than that which
         * originally constructed it.
         */
        protected void startTask() {
        }

        /**
         * Invoked when a message is received with the "request" performative
         * and belongs to a conversation which isn't associated with any Task
         * (i.e. a new incoming "ping" conversation).
         */
        public void handleRequest(Conversation conv) {
            // Indicate what is going on
            DIAGNOSTICS.println("PA: Recieved request.  Starting ApiResponseTask", DIAGNOSTICS.LEVEL_4);

            // Start a new Task to deal with this conversation
            newTask(new ApiResponseTask(conv), conv);
        }

        /**
         * This method is dynamically invoked when a ApiResponseTask
         * completes its task.
         */
        public void donePingAgent_ApiResponseTask(Task t) {
            // We can ignore this task-type completing
        }
    }

    /**
     * Provides a Task that responds to an incoming cyc api request.
     */
    public class ApiResponseTask extends Task {
        /**
         * Conversation within which we are replying
         */
        private Conversation conv;

        /**
         * Constructs a new ApiResponseTask with the given Conversation object.
         *
         * @param conv the Cyc api request conversation object
         */
        public ApiResponseTask(Conversation conv) {
            this.conv = conv;
        }

        /**
         * Invoked once this Task has been initialised.  Sends the response messages
         * and then checks if we know about the Agent pinging us.
         */
        protected void startTask() {
            // Respond to the original message

            // Send Agree
            ACL msg = conv.getACL(conv.getLatestMessageIndex());
            ACL resp = (ACL) msg.clone();
            resp.setSenderAID(_owner.getAID());
            resp.setReceiverAID(msg.getSenderAID());
            resp.setPerformative(FIPACONSTANTS.AGREE);

            // Forward via super.forward(), which binds the Conversation with this Task
            forward(resp);

            // Send Inform
            resp = (ACL) msg.clone();
            resp.setSenderAID(_owner.getAID());
            resp.setReceiverAID(msg.getSenderAID());
            resp.setPerformative(FIPACONSTANTS.INFORM);

            // Forward via super.forward()
            forward(resp);

            // The Task is complete - it can be released!
            done();
        }
    }


}
