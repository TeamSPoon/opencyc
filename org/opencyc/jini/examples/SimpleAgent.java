
package  org.opencyc.jini.examples;

/**
 * Provides an example of simple client and service behavior for agents using the
 * Agent Manager Protocol.<p>
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

import  java.util.*;
import  java.rmi.*;
import  java.rmi.server.*;
import  java.net.*;
import  javax.swing.*;
import  net.jini.lookup.entry.*;
import  org.opencyc.api.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.util.*;


public class SimpleAgent extends GenericService
        implements Remote, SimpleAgentInterface {

    /**
     * Agent name.
     */
    protected String agentName;

    /**
     * agentId -> simpleAgent
     */
    protected static Hashtable agents = new Hashtable();

    /**
     * Constructs a new SimpleAgent object.
     *
     * @param serviceIcon the icon associated with the service.
     * @param nameAttribute the name of the service.
     * @param serviceInfo a standard attribute describing the service.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public SimpleAgent (ImageIcon serviceIcon, Name nameAttribute, ServiceInfo serviceInfo)
        throws RemoteException {
        super(serviceIcon, nameAttribute, serviceInfo);
        agentName = nameAttribute.name;
    }

    /**
     * Provides main method for the agent.  Establishes a security manager.  Creates an instance
     * of the service and registers it via the JoinManager with the Lookup Service.
     *
     * @param command line arguments for the service.
     */
    public static void main (String[] args) {
        initialize();
        example();
        shutdown();
    }

    private static SimpleAgent makeExampleAgent (String agentName, String iconPath) {
        // Name entry for LookupService.
        Name nameAttribute = new Name(agentName);
        // ServiceInfo entry for LookupService.
        String name = "OpenCyc Agent";
        String manufacturer = "Cycorp Inc.";
        String vendor = "Cycorp Inc.";
        String version = "1.0";
        String model = "";
        String serialNumber = "";
        ServiceInfo serviceInfo = new ServiceInfo(name, manufacturer, vendor, version, model, serialNumber);
        URL serviceIconURL = ClassLoader.getSystemResource(iconPath);
        ImageIcon serviceIcon = new ImageIcon(serviceIconURL);
        SimpleAgent agent = null;
        try {
            agent = new SimpleAgent(serviceIcon, nameAttribute, serviceInfo);
        } catch (RemoteException e) {
            Log.current.errorPrintln("Error while creating service instance " + e);
        }
        return  agent;
    }

    /**
     * Handle an AMP (Agent Message Protocol) incoming message.
     */
    public Amp ampMessageReceived (Amp message) {
        Log.current.println(agentName + " received AMP " + message);
        Amp replyMessage = new Amp();
        replyMessage.setSender(message.receiver());
        replyMessage.setReceiver(message.sender());
        replyMessage.setInReplyTo(message.content());
        replyMessage.setLanguage(message.language());
        replyMessage.setOntology(message.ontology());
        Log.current.println(agentName + " replied with AMP " + replyMessage);
        return  replyMessage;
    }

    /**
     * example of messages sent between two agents.
     */
    public static void example () {
        String localHostName = null;
        try {
            localHostName = InetAddress.getLocalHost().getHostName();
        }
        catch (java.net.UnknownHostException e) {
            Log.current.println("Cannot obtain local host name " + e.getMessage());
            localHostName = "";
        }
        String agent1Name = localHostName + "OpenCyc Agent 1";
        String agent2Name = localHostName + "OpenCyc Agent 2";
        Amp amp1 = new Amp("(ASK-ALL " +
                           ":SENDER \"" + agent1Name +  "\" " +
                           ":RECEIVER \"" + agent2Name + "\" " +
                           ":REPLY-WITH 1 " +
                           ":CONTENT \"(example content 1)\" " +
                           ":LANGUAGE AMP " +
                           ":ONTOLOGY CYC-API ");
        Amp amp2 = new Amp("(ASK-ALL " +
                           ":SENDER \"" + agent2Name +  "\" " +
                           ":RECEIVER \"" + agent1Name + "\" " +
                           ":REPLY-WITH 1 " +
                           ":CONTENT \"(example content 2)\" " +
                           ":LANGUAGE AMP " +
                           ":ONTOLOGY CYC-API ");

        SimpleAgent agent1 = makeExampleAgent(agent1Name,
                                              "small_green_box.gif");
        SimpleAgent agent2 = makeExampleAgent(agent2Name,
                                              "small_green_box.gif");
        try {
            Log.current.println(agent1Name + " sending " + amp1);
            agent1.sendAmpMessage(amp1);
        }
        catch (RemoteException e) {
            Log.current.println("Error while sending " + amp1 + "  " + e);
        }
        catch (CycApiException e) {
            Log.current.println("Error while sending " + amp1 + "  " + e);
        }
        try {
            Log.current.println(agent2Name + " sending " + amp2);
            agent2.sendAmpMessage(amp2);
        }
        catch (RemoteException e) {
            Log.current.println("Error while sending " + amp2 + "  " + e);
        }
        catch (CycApiException e) {
            Log.current.println("Error while sending " + amp1 + "  " + e);
        }
        Log.current.println("Waiting 2 minutes for requests, then terminating");
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {}
    }
}



