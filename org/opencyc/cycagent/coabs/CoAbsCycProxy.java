package org.opencyc.cycagent.coabs;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.*;
import net.jini.lookup.ServiceIDListener;
import com.globalinfotek.coabsgrid.*;
import com.globalinfotek.coabsgrid.entry.fipa98.AMSAgentDescription;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import fipaos.util.*;
import fipaos.parser.ParserException;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.xml.*;
import org.opencyc.cycagent.*;








/**
 * Provides a proxy for a cyc agent on the CoABS grid agent community.<p>
 *
 * An agent to test AgentRegistrationHelper and Directory methods.  This class
 * implements MessageListener so that it will be notified when messages get
 * added to its message queue.  It implements AgentTestInterface so that it can
 * be used by AgentTestGUI.  This agent demonstrates the two main ways of
 * interacting with other agents - using the Directory.forward() method or directly
 * communicating using AgentRep.addMessage().  When this agent receives a
 * message that has a sender field containing an AgentRep, it calls the
 * AgentRep.addMessage() method to acknowledge receipt.  AgentTestGUI has
 * buttons to test the AgentRegistrationHelper registerAgent(),
 * deregisterAgent(), and modifyAgent() methods and the Directory forward()
 * method.  A default receiver and message are displayed for the forward()
 * button.  You can edit the defaults in the window to send messages to any
 * agent that is up and registered.  When the register button is pressed, this
 * agent tries to get its ServiceID from a file.  If the file does not exist,
 * the agent asks the LUS to generate its ServiceID and the agent writes that
 * ServiceID to the file for use the next time the agent needs to register.<p>
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

public class CoAbsCycProxy implements MessageListener, ShutdownHook {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 1;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * the agent name.
     */
    protected String myAgentName = "Agent1";

    /**
     * the CoABS AgentRestrationHelper object.
     */
    protected AgentRegistrationHelper regHelper;

    /**
     * message serial number.
     */
    protected int count;

    /**
     * The conversation state.
     */
    protected String conversationState = "initial";

    /**
     * Cached AgentRep objects which reduce lookup overhead.
     * myAgentName --> AgentRep instance
     */
    protected static Hashtable agentRepCache = new Hashtable();

    /**
     * Cached CycConnection objects which preserve Cyc session state.
     * myAgentName --> CycConnection instance
     */
    protected static Hashtable cycConnectionCache = new Hashtable();

    /**
     * Constructs a new CoAbsCycProxy object.
     * This agent instantiates an AgentRegistrationHelper, which in turn
     * instantiates a DefaultAgentRep, a MessageQueue, and GridAgentHelper.
     * This object then makes itself a messageListener to the queue, adds an
     * agent advertisement and registers itself.
     *
     * @param myAgentName the unique name of this Cyc proxy agent.
     */
    public CoAbsCycProxy(String myAgentName) throws IOException {
        this.myAgentName = myAgentName;
        execute();
    }

    /**
     * Constructs a new CoAbsCycProxy object.
     * This agent instantiates an AgentRegistrationHelper, which in turn
     * instantiates a DefaultAgentRep, a MessageQueue, and GridAgentHelper.
     * This object then makes itself a messageListener to the queue, adds an
     * agent advertisement and registers itself.
     */
    public CoAbsCycProxy() throws IOException {
        execute();
    }

    /**
     * Executes the cyc api service.
     */
    protected void execute() throws IOException {
        if (verbosity > 1)
            Log.current.println("Starting CoAbsCycProxy " + myAgentName);
        regHelper = new AgentRegistrationHelper(myAgentName);
        regHelper.addMessageListener(this);
        //Entry[] entries = {new AMSAgentDescription(myAgentName)};
        //regHelper.addAdvertisedCapabilities(entries);
        count = 1;
        conversationState = "register";
        register();
        ShutdownHandler.addHook(this);
    }

    /**
     * Provides the main method.
     */
    public static void main(String[] args) {
        if (Log.current == null)
            Log.makeLog();
        try {
            CoAbsCycProxy coAbsCycProxy = new CoAbsCycProxy();
        }
        catch (IOException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
        while (true)
            // Keep root thread running with minimal resource consumption.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
    }

    /**
     * Deregisters this agent when the application is terminated.
     */
    public void cleanup() {
        if (regHelper.isRegistered()) {
            if (verbosity > 1)
                Log.current.println("de-registering " + regHelper.getAgentRep().getName());
            deregister();
        }
    }

    // FIPA AMS

    /**
     * Register this agent.
     * Tests registry.registerAgent()
     */
    public void register() throws IOException {
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
        try {
            // The agent version of registerAgent(), used below, sends a message
            // confirming that registration has been initiated.
            regHelper.registerAgent ("message" + count++);
        }
        catch (IOException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            }
        long oneMinuteDuration = 60000;
        org.opencyc.util.Timer timer = new org.opencyc.util.Timer(oneMinuteDuration);
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
            }
        if (verbosity > 2)
            Log.current.println(myAgentName + " registered with CoABS grid");
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
     * Deregisters this agent.
     */
    public void deregister() {
        if (verbosity > 2)
            Log.current.println(myAgentName +
                                " calling AgentRegistrationHelper.deregisterAgent()...");
        try {
            regHelper.deregisterAgent(regHelper.getAgentRep(),
                                      "message" + count++);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    // FIPA ACC

    /**
     * Processes the Cyc API request message.  Replies with the Cyc API
     * result.
     *
     * @param message the message added to the message queue.
     */
    public void messageAdded(Message message) {
        Date time = regHelper.getTimeLastMessageReceived();
        AgentRep agentRep = message.getSenderAgentRep();
        String fromAgentName = "Unknown";
        if (agentRep != null)
            fromAgentName = agentRep.getName();
        if (verbosity > 2)
            Log.current.println("\n" + myAgentName + " received:\n" + message.toString() +
                                "\n  ACL: " + message.getACL() +
                                "\n  Time message received: " + time +
                                "\n  Sender AgentRep: " + agentRep +
                                "\n  From: " + fromAgentName);
        if (message.getRawText().startsWith("(inform")) {
            if (conversationState.equals("register")) {
                conversationState = "api ready";
                if (verbosity > 2)
                    Log.current.println("Received reply for the registration request" +
                                        "\n  now ready for Cyc Api requests");
            }
            else {
                if (verbosity > 2)
                    Log.current.println("Ignoring INFORM performative");
            }
            return;
        }
        if (message.getACL().equals("naturalLanguage")  &&
            (! message.getRawText().startsWith("("))) {
            if (verbosity > 2)
                Log.current.println("Cannot parse api command " + message.getRawText());
            return;
        }

        if (! conversationState.equals("api ready")) {
            Log.current.errorPrintln("Conversation state not api ready: " + conversationState);
            return;
        }
        try {
            processCycApiRequest(message);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln(e.getMessage());
        }
    }

    /**
     * Processes a cyc api request
     *
     * @param message the received cyc api request message
     */
    protected void processCycApiRequest (Message apiRequestMessage) throws RemoteException {
        conversationState = "api request";
        ACL coAbsRequestAcl = null;
        CycList apiRequest = null;
        String senderName = apiRequestMessage.getSenderAgentRep().getName();
        CycConnection cycConnection = (CycConnection) cycConnectionCache.get(senderName);
        try {
            if (cycConnection == null) {
                cycConnection = new CycConnection();
                cycConnectionCache.put(senderName, cycConnection);
                if (verbosity > 1)
                    Log.current.print("created cyc connection to " + cycConnection.connectionInfo() +
                                      "\nfor " + senderName);
            }
            coAbsRequestAcl = new ACL(apiRequestMessage.getRawText());
            String contentXml = (String) coAbsRequestAcl.getContentObject();
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
                cycConnectionCache.remove(senderName);
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
                //cycConnection.traceOnDetailed();
                //cycConnection.traceOn();
                response = cycConnection.converse(apiRequest);
            }
            catch (Exception e) {
                Log.current.errorPrintln(e.getMessage());
                Log.current.printStackTrace(e);
                return;
            }
        }
        ACL coAbsReplyAcl = (ACL) coAbsRequestAcl.clone();
        coAbsReplyAcl.setPerformative(FIPACONSTANTS.INFORM);
        coAbsReplyAcl.setSenderAID(coAbsRequestAcl.getReceiverAID());
        coAbsReplyAcl.setReceiverAID(coAbsRequestAcl.getSenderAID());
        CycList responseCycList = new CycList();
        if (response[0].equals(Boolean.TRUE))
            responseCycList.add(CycObjectFactory.t);
        else if (response[0].equals(Boolean.FALSE))
            responseCycList.add(CycObjectFactory.nil);
        else
            new RuntimeException("response[0] not Boolean " + response[0]);
        responseCycList.add(response[1]);
        try {
            coAbsReplyAcl.setContentObject("\n" + Marshaller.marshall(responseCycList));
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception while marshalling " + responseCycList);
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return;
        }
        coAbsReplyAcl.setReplyWith(null);
        coAbsReplyAcl.setInReplyTo(coAbsRequestAcl.getReplyWith());

        Message replyMessage = new BasicMessage(coAbsRequestAcl.getSenderAID().getName(),
                                                regHelper.getAgentRep(),
                                                "fipa-xml",
                                                coAbsReplyAcl.toString());
        if (verbosity > 2)
            Log.current.println("\nReplying with " + replyMessage.toString());
        AgentRep requestingAgentRep = apiRequestMessage.getSenderAgentRep();
        requestingAgentRep.addMessage(replyMessage);

        conversationState = "api ready";
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
        // create a Directory to use for lookups
        Directory directory = new Directory();
        ServiceItem[] items = directory.lookup((net.jini.core.lookup.ServiceTemplate) null);
        if (verbosity > 0)
            Log.current.println("searching through " + items.length + " directory items for " + agentName);
        for (int i = 0; i < items.length; i++) {
            ServiceItem si = items[i];
            Object service =  si.service;
            if (verbosity > 0)
                Log.current.println("  directory service " + service);
            if (service instanceof AgentRep) {
                agentRep = (AgentRep) service;
                if (verbosity > 0)
                    Log.current.println("    directory agent " + agentRep.getName());
                if (agentName.equals(agentRep.getName())) {
                    agentRepCache.put(agentName, agentRep);
                    if (verbosity > 0)
                        Log.current.println("\ncached AgentRep for " + agentName);
                    return agentRep;
                }
            }
        }
        return null;
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
