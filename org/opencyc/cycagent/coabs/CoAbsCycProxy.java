package org.opencyc.cycagent.coabs;

import com.globalinfotek.coabsgrid.AgentRegistrationHelper;
import com.globalinfotek.coabsgrid.AgentRep;
import com.globalinfotek.coabsgrid.MessageListener;
import com.globalinfotek.coabsgrid.Message;
import com.globalinfotek.coabsgrid.BasicMessage;
import com.globalinfotek.coabsgrid.ShutdownHook;
import com.globalinfotek.coabsgrid.ShutdownHandler;
import com.globalinfotek.coabsgrid.entry.fipa98.AMSAgentDescription;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Date;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;

/**
 * Provides a proxy for a cyc agent on the CoABS grid agent community.<p>
 *
 *  An agent to test AgentRegistrationHelper and Directory methods.  This class
 *  implements MessageListener so that it will be notified when messages get
 *  added to its message queue.  It implements AgentTestInterface so that it can
 *  be used by AgentTestGUI.  This agent demonstrates the two main ways of
 *  interacting with other agents - using the Directory.forward() method or directly
 *  communicating using AgentRep.addMessage().  When this agent receives a
 *  message that has a sender field containing an AgentRep, it calls the
 *  AgentRep.addMessage() method to acknowledge receipt.  AgentTestGUI has
 *  buttons to test the AgentRegistrationHelper registerAgent(),
 *  deregisterAgent(), and modifyAgent() methods and the Directory forward()
 *  method.  A default receiver and message are displayed for the forward()
 *  button.  You can edit the defaults in the window to send messages to any
 *  agent that is up and registered.  When the register button is pressed, this
 *  agent tries to get its ServiceID from a file.  If the file does not exist,
 *  the agent asks the LUS to generate its ServiceID and the agent writes that
 *  ServiceID to the file for use the next time the agent needs to register.<p>
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

public class CoAbsCycProxy implements MessageListener, AgentTestInterface,
    ShutdownHook{

    /**
     * the agent name.
     */
    protected String agentName;

    /**
     * the CoABS AgentRestrationHelper object.
     */
    protected AgentRegistrationHelper regHelper;

    /**
     * the message.
     */
    protected Message message;

    /**
     * message count
     */
    protected int count;

    /**
     * message listeners which are notified when messages are added to the
     * message queue by the CoABS message transport.
     */
    protected Vector messageListeners;

    /**
     * Keeps track of modify state.
     */
    protected boolean modifyP;

    /**
     * Constructs a new CoAbsCycProxy object.
     * This agent instantiates an AgentRegistrationHelper, which in turn
     * instantiates a DefaultAgentRep, a MessageQueue, and GridAgentHelper.
     * This object then makes itself a messageListener to the queue, adds an
     * agent advertisement, and creates the message that it will send.
     *
     * @param agentName the name of this agent
     * @param defaultRecipientName the name of the default recipient
     * @param acl the agent communication language to use
     * @param defaultMessageText the default message text sent by this agent
     */
    public CoAbsCycProxy(String agentName,
                         String defaultRecipientName,
                         String acl,
                         String defaultMessageText) throws IOException {
        this.agentName = agentName;
        regHelper = new AgentRegistrationHelper(agentName);
        regHelper.addMessageListener(this);
        Entry[] entries = {new AMSAgentDescription(agentName)};
        regHelper.addAdvertisedCapabilities(entries);
        message = new BasicMessage(defaultRecipientName,
                                   regHelper.getAgentRep(),
                                   "naturalLanguage",
                                   defaultMessageText);
        count = 1;
        messageListeners = new Vector();
        modifyP = false;
        ShutdownHandler.addHook(this);
    }

    public CoAbsCycProxy() throws IOException {
        this ("Agent1",
              "Agent2",
              "naturalLanguage",
              "[Sending message using\n" +
                  " registryAgent.forward()]\n" +
                  "Hi Agent2!\n" +
                  "I provide the Cyc API service via CycAccess\n" +
                  "- Agent1");
    }

    /**
     * Deregisters this agent when the application is terminated.
     */
    public void cleanup() {
        if (regHelper.isRegistered()) deregister();
    }

    /**
     * Gets the agent name for the test GUI title.
     */
    public String getName() {
        return agentName;
    }

    /**
     * Gets the the message receiver for the test GUI.
     */
    public String getReceiver() {
        return message.getReceiver();
    }

    /**
     * Sets the message receiver.
     * Used by the GUI when the user edits the message receiver.
     *
     * @param receiver the message receiver
     */
    public void setReceiver(String receiver) {
        message.setReceiver(receiver);
    }

    /**
     * Gets the message text. Used by the GUI to display the message text.
     */
    public String getRawText() {
        return message.getRawText();
    }

    /**
     * Sets the message text.
     * Used by the GUI when the user edits the message text.
     *
     * @param text the message text
     */
    public void setRawText(String text) {
        message.setRawText(text);
    }

    /**
     * Adds a message listener.
     * Just for working with the AgentTestFrame.
     * Lets the frame be a listener for new messages on the queue
     * so it can print them out.
     *
     * @param ml the message listener
     */
    public void addMessageListener(MessageListener ml) {
        messageListeners.addElement(ml);
    }

    /**
     * Notifies message listeners when messages are added to the queue.
     * Just for working with the AgentTestFrame. Notifies the test frame when
     * messages are added to the queue.
     *
     * @param message the message
     */
    private void notifyListeners(Message message) {
        MessageListener ml;
        for (int i=0; i<messageListeners.size(); i++) {
            ml = (MessageListener) messageListeners.elementAt(i);
            ml.messageAdded(message);
        }
    }

    protected AgentRegistrationHelper getRegistrationHelper () {
        return regHelper;
    }

    // FIPA AMS

    /**
     * Register this agent.
     * Tests registry.registerAgent()
     */
    public void register() {
        System.out.println(agentName +
                           " calling AgentRegistrationHelper.registerAgent()...");
        String filename = agentName + "ServiceIDFile";
        try {
            regHelper.readServiceIDFromFile(filename);
            System.out.println("Read ServiceID from file " + filename + ".");
        }
        catch (IOException e) {
            System.out.println("Could not get ServiceID from file - " +
                               "will get one from LUS.");
            addServiceIDListener(filename);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Could not get ServiceID from file - " +
                               "will get one from LUS.");
            addServiceIDListener(filename);
        }
        try {
            // The agent version of registerAgent(), used below, sends a message
            // confirming that registration has been initiated.
            regHelper.registerAgent ("message" + count++);}
        catch (IOException e) {e.printStackTrace();}
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
                    System.out.println("Wrote ServiceID to file " + filename + ".");
                }
                catch (IOException exc) {
                    System.out.println("Couldn't save ServiceID to file.");
                }
                finalRegHelper.removeServiceIDListener(this);
            }
        };
        regHelper.addServiceIDListener(sidl);
    }

    /**
     * Deregisters this agent.
     * Tests registry.deregisterAgent()
     */
    public void deregister() {
        System.out.println(agentName +
                           " calling AgentRegistrationHelper.deregisterAgent()...");
        try {
            regHelper.deregisterAgent(regHelper.getAgentRep(),
                                      "message" + count++);
        } catch (RemoteException e) {e.printStackTrace();}
    }

    /**
     * Modifies this agent's advertised capabilities.
     * Tests registry.modifyAgent()
     */
    public void modify() {
        System.out.println(agentName +
                           " calling AgentRegistrationHelper.modifyAgent()...");
        String state;
        if (modifyP == false) {
            state = "active";
            modifyP = true;
        }
        else {
            state = "waiting";
            modifyP = false;
        }
        Entry[] entries = regHelper.getAdvertisedCapabilities();
        for (int i = 0; entries != null && i < entries.length; i++)
        {
            if (entries[i] instanceof AMSAgentDescription)
            {
                ((AMSAgentDescription) entries[i]).apState = state;
                break;
            }
        }
        regHelper.setAdvertisedCapabilities(entries);
        try {regHelper.modifyAgent (regHelper.getAgentRep(),
                                    "message" + count++);}
        catch (RemoteException e) {e.printStackTrace();}
    }

    // FIPA ACC

    /**
     * Forwards the message.
     * Tests registry.forward()
     */
    public void forward() {
        try {
            regHelper.getDirectory().forward(message, regHelper.getAgentRep(),
                                             "message" + count++);
        } catch (RemoteException e) {e.printStackTrace();}
    }

    /**
     * Implements MessageListener Interface. When a message is received which
     * has an agentRep in the sender attribute, a reply is created and
     * sent directly to the sender.
     *
     * @param message the message added to the message queue.
     */
    public void messageAdded(Message message) {
        notifyListeners(message);
        Date time = regHelper.getTimeLastMessageReceived();
        AgentRep agentRep = message.getSenderAgentRep();
        System.out.print("**** " + agentName + " received:\n" + message.toString());
        System.out.println("**** Time message received: " + time);
        System.out.println("**** Sender AgentRep: " + agentRep);
        System.out.println();
        if (agentRep != null) {
            String recipientName = agentRep.getName();
            String txt = "[Demonstrating reply using\n" +
                "direct communication by calling\n" +
                "message.getSenderAgentRep().addMessage()]\n" +
                recipientName + ":\n" +
                "  " + agentName + " acknowledging receipt\n" +
                "  of your message at\n" +
                "  " + time + "\ncontents:\n" + message.getRawText();
            Message replyMessage = new BasicMessage(recipientName,
                                                    "naturalLanguage",
                                                    txt);
            try {
                agentRep.addMessage(replyMessage);
            }
            catch (RemoteException e) {e.printStackTrace();}
        }
    }
}
