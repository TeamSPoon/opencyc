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
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * reference to the name of my agent
     */
    protected String myAgentName;

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
     * myAgentName --> agentRep
     */
    protected static Hashtable agentRepCache = new Hashtable();

    /**
     * The parent agent object which implements the MessageReceiver interface.
     */
    MessageReceiver messageReceiver;

    /**
     * CoABS Directory helper.
     */
    Directory directory;

    /**
     * Constructs a new CoAbsCommunityAdapter for the given CoAbs agent, with the given verbosity.
     *
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CoAbsCommunityAdapter(MessageReceiver messageReceiver, int verbosity) throws IOException {
        myAgentName = messageReceiver.getMyAgentName();
        this.messageReceiver = messageReceiver;
        this.verbosity = verbosity;
        if (Log.current == null)
            Log.makeLog();
        register();
    }

    /**
     * Registers the CoABS agent on the grid.
     */
    protected void register() throws IOException {
        if (verbosity > 0)
            Log.current.println("Starting CoAbsCommunityAdapter for " + myAgentName);
        regHelper = new AgentRegistrationHelper(myAgentName);
        regHelper.addMessageListener(this);
        //Entry[] entries = {new AMSAgentDescription(myAgentName)};
        //regHelper.addAdvertisedCapabilities(entries);
        ShutdownHandler.addHook(this);
        conversationState = "register";
        if (verbosity > 2)
            Log.current.println(myAgentName +
                                " calling AgentRegistrationHelper.registerAgent()...");
        String filename = myAgentName + "ServiceIDFile";
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
        long oneMinuteDuration = 60000;
        org.opencyc.util.Timer timer = new org.opencyc.util.Timer(oneMinuteDuration);
        waitingReplyThreads.put(registrationMessageId, Thread.currentThread());
        while (true)
            try {
                Thread.sleep(10000);
                if (verbosity > 0)
                    System.out.print(".");
                if (timer.isTimedOut()) {
                    Log.current.errorPrintln("Time limit exceeded while awaiting CoABS registration");
                    throw new IOException("Time limit exceeded while awaiting CoABS registration");
                }
                if (this.lookupAgentRep(myAgentName) != null)
                    break;
            }
            catch (InterruptedException e) {
                if (this.lookupAgentRep(myAgentName) != null)
                    break;
            }
        if (verbosity > 2)
            Log.current.println(myAgentName + " registered with CoABS grid");
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
        requestMessage.setSender(regHelper.getAgentRep());
        if (verbosity > 2)
            Log.current.println("\nSending " + requestMessage.toString() +
                                "\n  receiver: " + requestMessage.getReceiver());
        AgentRep receivingAgentRep = this.lookupAgentRep(acl.getReceiverAID().getName());
        receivingAgentRep.addMessage(requestMessage);
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
        receivingAgentRep.addMessage(requestMessage);
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
        if (verbosity > 2) {
            Log.current.println("\n" + myAgentName + " received:\n" + message.toString());
            if (verbosity > 3)
                Log.current.println("  ACL: " + message.getACL() +
                                    "\n  Time message received: " + time +
                                    "\n  Sender AgentRep: " + agentRep +
                                    "\n  From: " + fromAgentName);
        }
        if (agentRep != agentRepCache.get(fromAgentName))
            agentRepCache.put(fromAgentName, agentRep);
        ACL acl = null;
        String fixedText = null;
        try {
            fixedText = fixSenderReceiver(message.getRawText());
            acl = new ACL(fixedText);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.current.errorPrintln(e.getMessage() + "\n in message:\n" + fixedText);
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
        if (messageReceiver != null)
            messageReceiver.messageReceived(AgentCommunityAdapter.COABS_AGENT_COMMUNITY, acl);
        else
            Log.current.println("no message receiver instance to process\n" + acl);
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
        if (verbosity > 2)
            Log.current.println("directory lookup for " + agentName);
        AgentRep agentRep = (AgentRep) agentRepCache.get(agentName);
        if (agentRep != null)
            return agentRep;
        ServiceTemplate serviceTemplate = new ServiceTemplate(null, null, null);
        Directory directory = new Directory();
        if (verbosity > 0)
            Log.current.println("Getting service registrars from directory " + directory);
        ServiceRegistrar[] serviceRegistrars = directory.getServiceRegistrars();
        for (int i = 0; i < serviceRegistrars.length; i++) {
            ServiceRegistrar serviceRegistrar = (ServiceRegistrar) serviceRegistrars[i];
            if (verbosity > 0)
                Log.current.println("Using service registrar " + serviceRegistrar);
            ServiceMatches serviceMatches =
                serviceRegistrar.lookup(serviceTemplate, Integer.MAX_VALUE);
            ServiceItem[] items = serviceMatches.items;
            if (verbosity > 0)
                Log.current.println("searching through " + items.length + " directory items for " + agentName);
            for (int j = 0; j < items.length; j++) {
                ServiceItem si = items[j];
                Object service =  si.service;
                if (verbosity > 0)
                    Log.current.println("  registered service " + service);
                if (service instanceof AgentRep) {
                    agentRep = (AgentRep) service;
                    if (verbosity > 0)
                        Log.current.println("    registered agent " + agentRep.getName());
                    if (agentName.equals(agentRep.getName())) {
                        agentRepCache.put(agentName, agentRep);
                        if (verbosity > 0)
                            Log.current.println("\ncached AgentRep for " + agentName);
                        return agentRep;
                    }
                }
            }
        }
        return null;
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
        if (! regHelper.isTerminated()) {
            if (regHelper.isRegistered()) {
                if (verbosity > 1)
                    Log.current.println("de-registering" + regHelper.getAgentRep().getName());
                deregister();
            }
            if (verbosity > 1)
                Log.current.println("terminating");
            regHelper.terminate();
        }
    }

    /**
     * Deregisters this agent.
     */
    public void deregister() {
        if (verbosity > 2)
            Log.current.println(myAgentName +
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
        if (regHelper.isRegistered())
            deregister();
        regHelper.terminate();
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

}