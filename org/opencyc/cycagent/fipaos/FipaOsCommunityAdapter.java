package org.opencyc.cycagent.fipaos;

import java.io.IOException;
import java.util.*;
import javax.naming.TimeLimitExceededException;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import fipaos.agent.*;
import fipaos.agent.task.*;
import fipaos.platform.ams.AMSRegistrationException;
import fipaos.platform.df.DFRegistrationException;
import fipaos.util.DIAGNOSTICS;
import fipaos.agent.conversation.*;
import org.opencyc.cycagent.*;
import org.opencyc.util.*;

/**
 * Provides the interface for interacting with the FIPA-OS agent community.<p>
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

public class FipaOsCommunityAdapter extends FIPAOSAgent implements AgentCommunityAdapter {

    /**
     * outbound message serial number.
     */
    public int msgSerialNumber = 0;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * reference to the name of my agent
     */
    protected String myAgentName;

    /**
     * The parent agent object which implements the MessageReceiver interface.
     */
    protected MessageReceiver messageReceiver;

    /**
     * the platform profile location
     */
    public static String platform_profile = "e:\\fipa-os\\profiles\\platform.profile";

    /**
     * the FIPA-OS agent owner
     */
    public static final String OWNER = "OpenCyc";

    /**
     * the FIPA-OS diagnostic level
     */
    protected int diagnosticLevel = DIAGNOSTICS.LEVEL_MAX;

    /**
     * Implements an association:  message id --> waiting thread for the reply.
     */
    protected Hashtable waitingReplyThreads = new Hashtable();

    /**
     * Implements an association:  message id --> reply message acl.
     */
    protected Hashtable replyMessages = new Hashtable();


    /**
     * Indicates whether this agent is registered.
     */
    protected boolean isRegistered = false;

    /**
     * Constructs a new FipaOsCommunityAdapter object for the given agent, with the given verbosity.
     *
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public FipaOsCommunityAdapter(MessageReceiver messageReceiver, int verbosity) {
        super(platform_profile,
              messageReceiver.getMyAgentName(),
              FipaOsCommunityAdapter.OWNER,
              true);
        this.messageReceiver = messageReceiver;
        if (Log.current == null)
            Log.makeLog();
        if (verbosity > 2)
            Log.current.println("messageReceiver " + messageReceiver);
        myAgentName = messageReceiver.getMyAgentName();
        this.verbosity = verbosity;
        if (verbosity == 0)
            diagnosticLevel = DIAGNOSTICS.LEVEL_MIN;
        else if (verbosity < 2)
            diagnosticLevel = DIAGNOSTICS.LEVEL_2;
        else if (verbosity < 5)
            diagnosticLevel = DIAGNOSTICS.LEVEL_3;
        else if (verbosity < 9)
            diagnosticLevel = DIAGNOSTICS.LEVEL_4;
        else
            diagnosticLevel = DIAGNOSTICS.LEVEL_MAX;
        register();
        if (verbosity > 2)
            Log.current.println("My agent ID\n  " + this.getAID());
        Runtime.getRuntime().addShutdownHook(new Shutdown());
    }

    /**
     * Registers the agent on the FIPA-OS agent community.
     */
    protected void register() {
        if (verbosity > 0)
            Log.current.println("Starting FipaOsCommunityAdapter for " + myAgentName);
        setListenerTask(new IdleTask());
        try {
            registerWithAMS();
            DIAGNOSTICS.println("Registered with AMS", this, diagnosticLevel);
        }
        catch (AMSRegistrationException amsre) {
            DIAGNOSTICS.println(amsre, this, diagnosticLevel);
            String reason = amsre.getExceptionReason();
            if (reason == null ||
                ! reason.equals(FIPAMANCONSTANTS.AGENT_ALREADY_REGISTERED)) {
                shutdown();
                return;
            }
            DIAGNOSTICS.println("Already registered with AMS", this, diagnosticLevel);
        }
        try {
            if (verbosity > 2)
                Log.current.print("Registering with DF for type " +
                                  messageReceiver.getAgentType());
            registerWithDF(messageReceiver.getAgentType());
            DIAGNOSTICS.println("Registered with DF", this, diagnosticLevel);
        }
        catch (DFRegistrationException dfre) {
            DIAGNOSTICS.println(dfre, this, diagnosticLevel);
            String reason = dfre.getExceptionReason();
            if (reason == null ||
                ! reason.equals(FIPAMANCONSTANTS.AGENT_ALREADY_REGISTERED)) {
                shutdown();
                return;
            }
            DIAGNOSTICS.println("Already registered with DF", this, diagnosticLevel);
        }
        isRegistered = true;
    }


    /**
     * Sends an Agent Communication Language message.
     *
     * @param acl the Agent Communication Language message to be sent
     */
    public void sendMessage (ACL acl) {
        if (acl.getPerformative().equals(FIPACONSTANTS.INFORM)) {
            String inReplyTo = acl.getInReplyTo();
            Thread waitingForReply = (Thread) this.waitingReplyThreads.get(inReplyTo);
            if (waitingForReply != null) {
                replyMessages.put(inReplyTo, acl);
                waitingForReply.interrupt();
                return;
            }
        }
        forward(acl);
    }

    public static final long WAIT_FOREVER = Long.MAX_VALUE;

    /**
     * Sends an Agent Communication Language message and returns the reply.
     *
     * @param acl the Agent Communication Language message to be sent
     * @param timer a Timer object contolling the maximum wait time for a reply message,
     * after which an excecption is thrown.
     * @return the Agent Communication Language reply message which has been received for my agent
     *
     * @thows TimeLimitExceededException when the time limit is exceeded before a reply message
     * is received.
     */
    public ACL converseMessage (ACL acl, org.opencyc.util.Timer timer)
        throws TimeLimitExceededException, IOException {
        ACL replyAcl = null;
        Object result = null;
        if (verbosity > 2)
            Log.current.println("converseMessage sending\n" + acl);
        try {
            long remainingMilliSeconds = timer.getRemainingMilliSeconds();
            // Handle different timer behavior between org.opencyc.util.Timer and FIPA-OS.
            if (remainingMilliSeconds >
                (Long.MAX_VALUE / 1024))
                remainingMilliSeconds = Long.MAX_VALUE / 1024;
            result =
                SynchronousTask.executeTask(_tm,
                                            new RequestTask(acl),
                                            remainingMilliSeconds);
            if (verbosity > 2)
                Log.current.println("result:\n" + result);

        }
        catch( Throwable t) {
            DIAGNOSTICS.println( "Exception when starting a Task",
                                t, diagnosticLevel);
        }
        if (result instanceof SynchronousTask.TimeoutResult)
            throw new TimeLimitExceededException();
        else
            replyAcl = (ACL) result;
        return replyAcl;
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
     * De-register this agent.
     */
    public void deregister() {
        if (! isRegistered)
            return;
        try {
            deregisterWithDF();
        }
        catch (DFRegistrationException e) {
            DIAGNOSTICS.println(e, this, diagnosticLevel);
            shutdown();
            return;
        }
        try {
            deregisterWithAMS();
        }
        catch (AMSRegistrationException e) {
            DIAGNOSTICS.println(e, this, diagnosticLevel);
            shutdown();
            return;
        }
        isRegistered = false;
    }

    /**
     * Terminate this agent.
     */
    public void terminate() {
        shutdown();
        return;
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        if (verbosity == 0)
            diagnosticLevel = DIAGNOSTICS.LEVEL_MIN;
        else if (verbosity < 2)
            diagnosticLevel = DIAGNOSTICS.LEVEL_2;
        else if (verbosity < 5)
            diagnosticLevel = DIAGNOSTICS.LEVEL_3;
        else if (verbosity < 9)
            diagnosticLevel = DIAGNOSTICS.LEVEL_4;
        else
            diagnosticLevel = DIAGNOSTICS.LEVEL_MAX;
    }

    /**
     * Returns a string representation of this object.
     *
     * @param a string representation of this object
     */
    public String toString() {
        return "[" + this.getClass() + " named " + this.myAgentName + "]";
    }

    /**
     * Provides an idle task which handles incomming messages which are not
     * otherwise associated with an in-progress conversation.
     */
    public class IdleTask extends Task {

        /**
         * Constructs a new IdleTask object.
         */
        public IdleTask() {
        }

        /**
         * Starts the IdleTask task.
         */
        protected void startTask() {
            DIAGNOSTICS.println("Started IdleTask", diagnosticLevel);
        }

        /**
         * Handles a new conversation whose first message is a request performative.
         *
         * @param conv the Request conversation
         */
        public void handleRequest(Conversation conv) {
            DIAGNOSTICS.println( "Received request", diagnosticLevel);
            if (conv.getProtocol().equals(FIPACONSTANTS.FIPA_REQUEST))
                newTask(new AgreeInformTask(conv), conv);
            else
                DIAGNOSTICS.println( "Ignored request", diagnosticLevel);
        }

        /**
         * Handles completion of an AgreePerform task.
         */
        public void doneFipaOsCommunityAdapter_AgreeInformTask(Task task) {
            DIAGNOSTICS.println("Completed AgreeInform task\n" + task, diagnosticLevel);
        }
    }

    /**
     * Provides for the initiation and handling of a fipa-request conversation.  This task
     * is involked via a SynchronousTask wrapper and the result is returned via the done
     * method.
     */
    public class RequestTask extends Task {
        /**
         * the request message sent to the specified receiver.
         */
        ACL requestAcl;

        /**
         * The inform message received as the reply.
         */
        ACL replyAcl;

        /**
         * Constructs a new RequestTask object given the request ACL.
         *
         * @param requestAcl the request message Agent Communication Language
         */
        public RequestTask (ACL requestAcl) {
            this.requestAcl = requestAcl;
            if (verbosity > 2)
                Log.current.print("\nconstructed RequestTask for\n" + requestAcl);
        }

        /**
         * Starts this task and initiates the fipa-request conversation.  The conversation
         * manager will assign a conversationID to the requestAcl object and create a new
         * fipa-request conversation to track it.
         */
        protected void startTask() {
            if (verbosity > 2)
                Log.current.print("\nstarting RequestTask and sending\n" + requestAcl);
            forward(requestAcl);
            DIAGNOSTICS.println("Sent request to " + requestAcl.getReceiverAID().getName(),
                                diagnosticLevel);
        }

        /**
         * Handle the request message which is part of the fipa-request conversation by doing
         * nothing.
         */
        public void handleRequest(Conversation fipaRequest) {
            if (verbosity > 2)
                Log.current.println("\nhandling Request " + fipaRequest);
        }

        /**
         * Handle the inform message which is part of the fipa-request conversation by doing
         * nothing.
         */
        public void handleAgree(Conversation fipaRequest) {
            if (verbosity > 2)
                Log.current.println("\nhandling Agree " + fipaRequest);
        }

        /**
         * Handle the inform message which is part of the RequestProtocol.  The replyAcl
         * is returned via the done method.
         */
        public void handleInform(Conversation fipaRequest) {
            if (verbosity > 2)
                Log.current.println("\nhandling Inform " + fipaRequest);
            replyAcl = fipaRequest.getACL(fipaRequest.getLatestMessageIndex());
            done(replyAcl);
        }
    }

    /**
     * Provides for handling the reply to the request message of a
     * fipa-request conversation.
     */
    public class AgreeInformTask extends Task {
        /**
         * Conversation within which we are replying.
         */
        private Conversation fipaRequest;

        /**
         * Constructs a new AgreeInformTask task given the conversation.
         *
         * @param fipaRequest conversation within which we are replying
         */
        public AgreeInformTask (Conversation fipaRequest) {
            this.fipaRequest = fipaRequest;
        }

        /**
         * Starts this task, sends the agree, and  sends the reply.
         */
        protected void startTask() {
            if (verbosity > 0)
                Log.current.println("\n\nAgreeInformTask responding to fipaRequest\n" +
                                    fipaRequest);
            ACL requestAcl =
                fipaRequest.getACL(fipaRequest.getLatestMessageIndex());
            ACL agreeAcl = (ACL) requestAcl.clone();
            agreeAcl.setPerformative(FIPACONSTANTS.AGREE);
            agreeAcl.setSenderAID(getAID());
            agreeAcl.setReceiverAID(requestAcl.getSenderAID());
            if (verbosity > 0)
                Log.current.println("\nSending agree acl\n" + agreeAcl);
            forward(agreeAcl);
            waitingReplyThreads.put(requestAcl.getReplyWith(), Thread.currentThread());
            messageReceiver.messageReceived(AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY,
                                            requestAcl);
            while (true)
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;
                }
            ACL replyAcl = (ACL) replyMessages.get(requestAcl.getReplyWith());
            replyAcl.setSenderAID(getAID());
            if (verbosity > 0)
                Log.current.println("\nSending inform acl\n" + replyAcl);
            forward(replyAcl);
            done();
        }
    }

    /**
     * Thread which shuts down the agent nicely.
     */
    protected class Shutdown extends Thread {
        /**
         * Constructs a new Shutdown object.
         */
        public Shutdown () {
        }

        /**
         * Runs the shutdown thread.
         */
        public void run () {
            System.out.println("Shutting down");
            deregister();
            shutdown();
        }
    }
}