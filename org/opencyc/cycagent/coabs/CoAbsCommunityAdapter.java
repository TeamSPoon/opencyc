package org.opencyc.cycagent.coabs;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import javax.naming.TimeLimitExceededException;
import net.jini.core.entry.Entry;
import net.jini.lookup.ServiceIDListener;
import net.jini.core.lookup.*;
import fipaos.ont.fipa.*;
import fipaos.parser.ParserException;
import com.globalinfotek.coabsgrid.*;
import com.globalinfotek.coabsgrid.entry.fipa98.AMSAgentDescription;
import org.opencyc.cycagent.*;
import org.opencyc.util.*;

/**
 * Provides the interface for interacting with the CoABS agent community.<p>
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

public class CoAbsCommunityAdapter
    implements AgentCommunityAdapter, MessageListener, ShutdownHook{

    /**
     * outbound message serial number.
     */
    public int msgSerialNumber = 0;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * name of my agent
     */
    protected String agentName;

    /**
     * the CoABS AgentRestrationHelper object.
     */
    protected AgentRegistrationHelper regHelper;

    /**
     * Implements an association:  message id --> waiting thread for the reply.
     */
    protected Hashtable waitingReplyThreads = new Hashtable();

    /**
     * Implements an association:  message id --> reply message acl.
     */
    protected Hashtable replyMessages = new Hashtable();

    /**
     * The conversation state.
     */
    protected String conversationState = "initial";

    /**
     * Cached AgentRep objects which reduce lookup overhead.
     * agentName --> agentRep
     */
    protected static Hashtable agentRepCache = new Hashtable();

    /**
     * Constructs a new CoAbsCommunityAdapter for the given CoAbs agent name.
     *
     * @param agentName the name of my agent in the CoAbs community.
     */
    public CoAbsCommunityAdapter(String agentName) throws IOException {
        this.agentName = agentName;
        if (Log.current == null)
            Log.makeLog();
        register();
    }

    /**
     * Registers the CoABS agent on the grid.
     */
    protected void register() throws IOException {
        if (verbosity > 1)
            Log.current.println("Starting CoAbsCommunityAdapter for " + agentName);
        regHelper = new AgentRegistrationHelper(agentName);
        regHelper.addMessageListener(this);
        //Entry[] entries = {new AMSAgentDescription(agentName)};
        //regHelper.addAdvertisedCapabilities(entries);
        ShutdownHandler.addHook(this);
        conversationState = "register";
        if (verbosity > 2)
            Log.current.println(agentName +
                                " calling AgentRegistrationHelper.registerAgent()...");
        String filename = agentName + "ServiceIDFile";
        try {
            regHelper.readServiceIDFromFile(filename);
            if (verbosity > 2)
                Log.current.println("Read ServiceID from file " + filename + ".");
        }
        catch (IOException e) {
            if (verbosity > 2)
                Log.current.println("Could not get ServiceID from file - " +
                                    "will get one from LUS.");
            addServiceIDListener(filename);
        }
        catch (ClassNotFoundException e) {
            if (verbosity > 2)
                Log.current.println("Could not get ServiceID from file - " +
                                    "will get one from LUS.");
            addServiceIDListener(filename);
        }
        String registrationMessageId = nextMessageId();
        try {
            regHelper.registerAgent (registrationMessageId);
        }
        catch (IOException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            }
        long tenSecondsDuration = 10000;
        org.opencyc.util.Timer timer = new org.opencyc.util.Timer(tenSecondsDuration);
        waitingReplyThreads.put(registrationMessageId, Thread.currentThread());
        while (true)
            try {
                Thread.sleep(500);
                if (timer.isTimedOut()) {
                    Log.current.errorPrintln("Time limit exceeded while awaiting CoABS registration");
                    throw new IOException("Time limit exceeded while awaiting CoABS registration");
                }
            }
            catch (InterruptedException e) {
                break;
            }
        if (verbosity > 2)
            Log.current.println(agentName + " registered with CoABS grid");
        waitingReplyThreads.remove(registrationMessageId);
        replyMessages.remove(registrationMessageId);
    }

    /**
     * Fixes the :sender and :receiver parameter in the CoABS message to make them FIPA ACL compatible.
     *
     * @param aclText the ACL expression in string form with :sender <sender-symbol> and
     * with :receiver <receiver-symbol>
     * @return the ACL expression in string form with :sender (agent-identifier :name <sender-symbol> ) and
     * with :receiver (set (agent-identifier :name <receiver-symbol>))
     */
    protected static String fixSenderReceiver (String aclText) throws ParserException {
        StringBuffer correctedSenderAclText = new StringBuffer();
        int index = aclText.indexOf(":sender");
        if (index < 0)
            throw new ParserException("No :sender parameter in " + aclText);
        index = index + 8;
        while (true) {
            char ch = aclText.charAt(index);
            if (ch == '(')
                return aclText;
            if (Character.isWhitespace(ch)) {
                index++;
                continue;
            }
            break;
        }
        correctedSenderAclText.append(aclText.substring(0, index - 1));
        correctedSenderAclText.append(" (agent-identifier :name ");
        while (true) {
            char ch = aclText.charAt(index);
            if (Character.isWhitespace(ch))
                break;
            correctedSenderAclText.append(ch);
            index++;
        }
        correctedSenderAclText.append(')');
        correctedSenderAclText.append(aclText.substring(index));
        String tempText = correctedSenderAclText.toString();
        StringBuffer correctedReceiverAclText = new StringBuffer();
        index = tempText.indexOf(":receiver");
        if (index < 0)
            throw new ParserException("No :receiver parameter in " + tempText);
        index = index + 10;
        while (true) {
            char ch = tempText.charAt(index);
            if (ch == '(')
                return tempText;
            if (Character.isWhitespace(ch)) {
                index++;
                continue;
            }
            break;
        }
        correctedReceiverAclText.append(tempText.substring(0, index - 1));
        correctedReceiverAclText.append(" (set (agent-identifier :name ");
        while (true) {
            char ch = tempText.charAt(index);
            if (Character.isWhitespace(ch))
                break;
            correctedReceiverAclText.append(ch);
            index++;
        }
        correctedReceiverAclText.append("))");
        correctedReceiverAclText.append(tempText.substring(index));
        return correctedReceiverAclText.toString();
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
     * Sends an Agent Communication Language message.
     *
     * @param acl the Agent Communication Language message to be sent
     */
    public void sendMessage (ACL acl) throws IOException {
        Message requestMessage = new BasicMessage(acl.getReceiverAID().getName(),
                                                  "fipa-xml",
                                                  acl.toString());
        if (verbosity > 2)
            Log.current.println("\nSending " + requestMessage.toString() +
                                "\n  receiver: " + requestMessage.getReceiver());
        AgentRep receivingAgentRep = this.lookupAgentRep(acl.getReceiverAID().getName());
        receivingAgentRep.addMessage(requestMessage);
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (ACL acl){
        if (verbosity > 2)
            Log.current.println("\nIgnoring " + acl);
    }

    /**
     * Sends an Agent Communication Language message and returns the reply.
     *
     * @param acl the Agent Communication Language message to be sent
     * @param timeoutMilliseconds the maximum wait time for a reply message, after which an
     * excecption is thrown.
     * @return the Agent Communication Language reply message which has been received for my agent
     *
     * @thows TimeLimitExceededException when the time limit is exceeded before a reply message
     * is received.
     */
    public ACL converseMessage (ACL acl, long timeoutMilliseconds)
        throws TimeLimitExceededException, IOException {
        Message requestMessage = new BasicMessage(acl.getReceiverAID().getName(),
                                                  "fipa-xml",
                                                  acl.toString());
        if (verbosity > 2)
            Log.current.println("\nSending " + requestMessage.toString() +
                                "\n  receiver: " + requestMessage.getReceiver());
        String replyWith = acl.getReplyWith();
        waitingReplyThreads.put(replyWith, Thread.currentThread());
        AgentRep receivingAgentRep = this.lookupAgentRep(acl.getReceiverAID().getName());
        receivingAgentRep.addMessage(requestMessage);
        org.opencyc.util.Timer timer = new org.opencyc.util.Timer(timeoutMilliseconds);
        waitingReplyThreads.put(replyWith, Thread.currentThread());
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
     * Receives messages from the CoABS grid.
     *
     * @param message the message received from the CoABS grid
     */
    public void messageAdded(Message message) {
        Date time = regHelper.getTimeLastMessageReceived();
        AgentRep agentRep = message.getSenderAgentRep();
        String fromAgentName = "Unknown";
        if (agentRep != null)
            fromAgentName = agentRep.getName();
        if (verbosity > 2)
            Log.current.println("\n" + agentName + " received:\n" + message.toString() +
                                "\n  ACL: " + message.getACL() +
                                "\n  Time message received: " + time +
                                "\n  Sender AgentRep: " + agentRep +
                                "\n  From: " + fromAgentName);

        ACL acl = null;
        try {
            acl = new ACL(fixSenderReceiver(message.getRawText()));
        }
        catch (ParserException e) {
            Log.current.errorPrintln(e.getMessage());
            return;
        }
        if (acl.getPerformative().equals(FIPACONSTANTS.INFORM)) {
            String inReplyTo = acl.getInReplyTo();
            Thread waitingForReply = (Thread) this.waitingReplyThreads.get(inReplyTo);
            if (waitingForReply != null) {
                this.replyMessages.put(inReplyTo, acl);
                waitingForReply.interrupt();
                return;
            }
        }
        messageReceived(acl);
    }

    /**
     * Adds a Service ID Listener.
     *
     * @param filename the file to which the serice ID is written.
     */
    private void addServiceIDListener(final String filename) {
        final AgentRegistrationHelper finalRegHelper = regHelper;
        ServiceIDListener sidl = new ServiceIDListener() {
            public void serviceIDNotify(ServiceID id) {
                try {
                    finalRegHelper.writeServiceIDToFile(filename);
                    if (verbosity > 2)
                        Log.current.println("Wrote ServiceID to file " + filename + ".");
                }
                catch (IOException exc) {
                    if (verbosity > 2)
                        Log.current.println("Couldn't save ServiceID to file.");
                }
                finalRegHelper.removeServiceIDListener(this);
            }
        };
        regHelper.addServiceIDListener(sidl);
    }

    /**
     * Returns the AgentRep object for the given agent name.
     *
     * @param agentName the agent name
     * @return the AgentRep object for the given agent name
     */
    protected AgentRep lookupAgentRep(String agentName) throws IOException {
        AgentRep agentRep = (AgentRep) agentRepCache.get(agentName);
        if (agentRep != null)
            return agentRep;
        // create a Directory to use for lookups
        Directory directory = new Directory();
        System.out.println("*** Looking up everything:");
        ServiceItem[] items = directory.lookup((net.jini.core.lookup.ServiceTemplate) null);
        for (int i = 0; i < items.length; i++) {
            System.out.println();
            System.out.println("Match " + (i + 1));
            System.out.println("---------");
            ServiceItem si = items[i];
            Object service =  si.service;
            System.out.println("Service (" + service.getClass() + ") = " + service);
            if (service instanceof AgentRep) {
                agentRep = (AgentRep) service;
                System.out.println("Agent name " + agentRep.getName());
                if (agentName.equals(agentRep.getName())) {
                    agentRepCache.put(agentName, agentRep);
                    if (verbosity > 2)
                        Log.current.print("cached AgentRep for " + agentName);
                    return agentRep;
                }
            }
        }
        throw new IOException("Agent not found " + agentName);
    }


    // FIPA ACC

    /**
     * Forwards the message to the CoABS grid.
     *
     * @param message the CoABS message
     */
    public void xforward(Message message, String messageId) {
        try {
            regHelper.getDirectory().forward(message,
                                             regHelper.getAgentRep(),
                                             messageId);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Deregisters this agent when the application is terminated.
     */
    public void cleanup() {
        if (regHelper.isRegistered()) {
            if (verbosity > 1)
                Log.current.println("de-registering" + regHelper.getAgentRep().getName());
            deregister();
        }
    }

    /**
     * Deregisters this agent.
     */
    public void deregister() {
        if (verbosity > 2)
            Log.current.println(agentName +
                                " calling AgentRegistrationHelper.deregisterAgent()...");
        try {
            regHelper.deregisterAgent(regHelper.getAgentRep(),
                                      "message" + msgSerialNumber++);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Terminate this agent.
     */
    public void terminate() {
        deregister();
    }
}