package  org.opencyc.jini.cycproxy;

/**
 * Provides a jini agent proxy and a Grid proxy for an OpenCyc server.<p>
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
import  java.io.*;
import  java.net.*;
import  java.rmi.*;
import  java.util.*;
import  javax.swing.*;
import  net.jini.lookup.entry.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.util.*;
import  org.opencyc.api.CycApiException;
import  org.opencyc.cycobject.*;

public class AgentManager extends GenericService
        implements Remote, GenericServiceInterface {
    /**
     * Message queue for routing all agent based messages.
     */
    public static AgentMessageQueue agentMessageQueue;

    /**
     * One to one ssociation of local cyc agents with their CycAgentInfo object.
     * cyc agent name -> CycAgentInfo
     */
    public static Hashtable cycAgents;

    /**
     * Collection AwaitingReplyInfo objects which represent osynchronous message requests
     * from local agent that are awaiting replies.  The socket object reference is held here
     * until the response is sent back to the local agent.
     */
    public static Vector awaitingReplies;

    /**
    /**
     * Singleton thread to listen for requests from Cyc.
     */
    protected static CycListener cycListener;
    protected static Thread cycListenerThread;

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        initialize();
        String localHostName = null;
        try {
            localHostName = InetAddress.getLocalHost().getHostName();
        }
        catch (java.net.UnknownHostException e) {
            Log.current.println("Cannot obtain local host name " + e.getMessage());
            localHostName = "";
        }
        try {
            String agentName = localHostName + " agent manager";
            AgentManager agentManager =
                AgentManager.makeAgentManager(agentName,
                                              "gold_star.gif");
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.current.errorPrintln("Exception while starting AgentManager " + e);
            System.exit(1);
        }
        while (true)
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
    }

    /**
     * Constructs a new AgentManager object.  This singleton instance manages one or more KB's on a single host.
     *
     * @param serviceIcon the icon associated with the service.
     * @param nameAttribute the name of the service.
     * @param serviceInfo a standard attribute describing the service.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public AgentManager (ImageIcon serviceIcon, Name nameAttribute, ServiceInfo serviceInfo)
        throws RemoteException {
        super(serviceIcon, nameAttribute, serviceInfo);
        // Place for testing methods.
        //Amp.test();
        agentMessageQueue = new AgentMessageQueue();
        cycAgents = new Hashtable();
        cycListener = new CycListener();
        cycListenerThread = new Thread(cycListener);
        cycListenerThread.start();
    }

    public static AgentManager makeAgentManager (String agentName, String iconPath) {
        // Name entry for LookupService.
        Log.current.println("agentName=" + agentName);
        Name nameAttribute = new Name(agentName);
        // ServiceInfo entry for LookupService.
        String name = "Agent Manager";
        String manufacturer = "Cycorp Inc.";
        String vendor = "Cycorp Inc.";
        String version = "1.0";
        String model = "";
        String serialNumber = "";
        ServiceInfo serviceInfo = new ServiceInfo(name, manufacturer, vendor, version, model, serialNumber);
        ImageIcon serviceIcon = new ImageIcon(ClassLoader.getSystemResource(iconPath));
        AgentManager agent = null;
        Log.current.println("nameAttribute=" + nameAttribute);
        try {
            agent = new AgentManager(serviceIcon, nameAttribute, serviceInfo);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln("Error while creating service instance " + e);
        }
        return  agent;
    }

    /***
     * Dispatchs a message from the agent message queue.  Lightweight tasks
     * are performed using the process resources of the calling thread. Otherwise
     * the handler corresponding to the message receiver is notified.
     */
    public static synchronized void dispatchAgentMessageQueue () {
        // Get the first queue item, and remove it from the queue.
        Amp amp = agentMessageQueue.removeFirst();
        Log.current.println("dispatchAgentMessageQueue amp: " + amp);
        // Obtain the receiver.
        String receiver = amp.receiver();
        Log.current.println("dispatchAgentMessageQueue receiver: " + receiver);
        // Handle the case where the receiver is a local java method.
        if (isLocalAgentMethod(receiver)) {
            routeToLocalAgentMethod(amp);
            return;
        }
        // Handle the case where the receiver is a Cyc agent.
        if (isCycAgent(receiver)) {
            try {
                routeToCycAgent(amp);
            }
            catch (Exception e) {
                Log.current.errorPrintln("Exception " + e + " while routing " + amp);
            }
            return;
        }
        // Otherwise send the message to the Grid.
        try {
            routeToGrid(amp);
        }
        catch (Exception e) {
            Log.current.errorPrintln("Exception " + e + " while routing " + amp);
        }
    }

    /***
     * Returns true if the argument is an agent implemented as a local java
     * method, or reachable through the Jini network.
     *
     * Note to developers - with numerous local method agents, there are
     * more efficient means of coding this method and its usage.
     *
     * @param receiver the agent receiving the message.
     * @return True if the receiver is an agent implemented as a local java
     * method or reachable through the Jini network.
     *
     */
    public static boolean isLocalAgentMethod (String receiver) {
        return  receiver.equals("CYC-AGENT-REGISTRAR");
    }

    /***
     * Answers true if the argument is an agent implemented within a local
     * non-java process (e.g. cyc image).
     * @param receiver the agent receiving the message.
     * @return True if the argument is an agent implemented within a local
     * non-java process (e.g. cyc image).
     *
     */
    public static boolean isCycAgent (String receiver) {
        return  cycAgents.containsKey(receiver);
    }

    /***
     * Dispatchs a message from the agent message queue.  Performs the local
     * method and then replies.
     *
     * @param amp the message
     */
    public static void routeToLocalAgentMethod (Amp amp) {
        String receiver = amp.receiver();
        Log.current.println("routing to local agent method amp: " + amp + "\n  receiver " + receiver);
        if (receiver.equals("CYC-AGENT-REGISTRAR")) {
            registerCycAgent(amp);
            return;
        }
        Log.current.println("routeToLocalAgentMethod, receiver not found " + receiver);
    }

    /***
     * Registers a Cyc agent.
     *
     * @param amp the registration request message
     */
    public static void registerCycAgent (Amp amp) {
        Log.current.println("registerCycAgent amp: " + amp);
        String sender = amp.sender();
        if (cycAgents.containsKey(sender)) {
            Log.current.errorPrintln("Ignoring registration request - already registered");
            return;
        }
        CycList content = new CycList(amp.content());
        Log.current.println("content: " + content.cyclify());
        if (content.size() != 2) {
            Log.current.errorPrintln("Registration content length not 2");
            return;
        }
        if (! (content.first() instanceof String)) {
            Log.current.errorPrintln("Registration cyc image name is not a string");
            return;
        }
        if (! (content.second() instanceof Integer)) {
            Log.current.errorPrintln("Registration base port number is not an integer");
            return;
        }
        String cycImageId = (String) content.first();
        int basePort = ((Integer) content.second()).intValue();
        CycAgentInfo cycAgentInfo = new CycAgentInfo();
        Log.current.println("Creating Cyc proxy for " + sender +
                            "\nhosted at image " + cycImageId +
                            " at localhost base port " + basePort);
        cycAgentInfo.cycProxy = CycProxy.makeCycProxy(sender, "cyc-logo-16.JPG", basePort);
        cycAgentInfo.cycImageId = cycImageId;
        cycAgents.put(sender, cycAgentInfo);
    }

    /***
     * Dispatches a message from the agent message queue to the receiving cyc agent.
     */
    public static synchronized void routeToCycAgent (Amp amp) throws IOException, CycApiException {
        String receiver = amp.receiver();
        Log.current.println("routeToCycAgent amp: " + amp);
        Log.current.println(" receiver: " + receiver);
        CycAgentInfo cycAgentInfo = (CycAgentInfo) cycAgents.get(receiver);
        CycProxy cycProxy = cycAgentInfo.cycProxy;
        cycProxy.routeToCycAgent(amp);
    }

    /***
     *
     * routeToGrid
     *
     * Dispatches a message from the agent message queue.  Route it to the Grid.
     *
     */
    public static void routeToGrid (Amp amp) {
        String receiver = amp.receiver();
        Log.current.println("routing to grid amp: " + amp + "\n  receiver " + receiver);
        return;
    }
}



