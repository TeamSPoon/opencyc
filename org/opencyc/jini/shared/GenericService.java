package  org.opencyc.jini.shared;

/**
 * Implements the services provided by all agents.<p>
 *
 * Accepts client registrations for service and notification of events.  Clients
 * obtain leases via the registrations.  Also contains the ability for agents to
 * send messages via the AMP (Agent Message Protocol) knowning only the name of the
 * receiving agent.
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
import  java.io.*;
import  java.rmi.*;
import  java.rmi.server.*;
import  javax.swing.*;
import  net.jini.admin.*;
import  net.jini.discovery.*;
import  net.jini.core.lease.*;
import  net.jini.core.event.*;
import  net.jini.core.discovery.*;
import  net.jini.lookup.entry.*;
import  net.jini.core.entry.*;
import  net.jini.admin.*;
import  net.jini.lease.*;
import  net.jini.lookup.*;
import  org.opencyc.api.*;
import  org.opencyc.util.*;

public class GenericService extends UnicastRemoteObject
        implements GenericServiceInterface,
                   Remote,
                   RemoteEventFactoryInterface,
                   ClientHelperCallbackInterface {
    /**
     * ServerLandlord instance.
     */
    protected ServerLandlord lord;

    /**
     * ServerDelivery instance.
     */
    protected ServerDelivery sender;

    /**
     * Icon for the service which appears in the Administrator panel.
     */
    protected ImageIcon serviceIcon;

    /**
     * JoinManager instance.
     */
    protected JoinManager joinManager;

    /**
     * Identifying name of the service.
     */
    protected String serviceName;

    /**
     * Table that associates
     * genericService -> genericServiceInfo
     */
    protected static Hashtable genericServices = new Hashtable();

    /**
     * Provides a container for service information.
     */
    class GenericServiceInfo {
        GenericService genericService;
        String name;
        JoinManager joinManager;

        public GenericServiceInfo (GenericService genericService,
                                   String name,
                                   JoinManager joinManager) {
            this.genericService = genericService;
            this.name = name;
            this.joinManager = joinManager;
        }
    }

    /**
     * respondingAgent name -> respondingAgentInfo
     */
    protected static Hashtable respondingAgents = new Hashtable();

    /**
     * provides a container for responding agent information.
     */
    class RespondingAgentInfo {
        String name;
        GenericServiceInterface respondingAgent;
        ClientHelper clientHelper;

        public RespondingAgentInfo (GenericServiceInterface
                                    respondingAgent,
                                    String name,
                                    ClientHelper clientHelper) {
            this.respondingAgent = respondingAgent;
            this.name = name;
            this.clientHelper = clientHelper;
        }
    }

    /**
     * A ClientHelper instance that encapsulates the Jini methods for locating a service.
     */
    protected ClientHelper clientHelper;

    /**
     * Constructs a new GenericService object.
     * Obtains a ServerLandlord object to manage the client leases.
     * Obtains a ServerDelivery object to manage the sending of events to clients.
     *
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     * @see ServerLandlord
     * @see ServerDelivery
     */
    public GenericService () throws RemoteException {
        lord = new ServerLandlord();
        sender = new ServerDelivery(this, lord);
    }

    /**
     * Constructs a new GenericService object.
     * Obtain a ServerLandlord object to manage the client leases.
     * Obtain a ServerDelivery object to manage the sending of events to clients.
     *
     * @param serviceIcon the icon associated with the service.
     * @param nameAttribute the name of the service.
     * @param serviceInfo a standard attribute describing the service.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     * @see ServerLandlord
     * @see ServerDelivery
     */
    public GenericService (ImageIcon serviceIcon,
                           Name nameAttribute,
                           ServiceInfo serviceInfo) throws RemoteException {
        this();
        Log.current.println("Starting service " + nameAttribute.name + ", with icon " + serviceIcon);
        if (this instanceof Administrable)
            Log.current.errorPrintln("Service is Administrable");
        else
            Log.current.errorPrintln("Service is not Administrable");
        joinLookupServices(this, nameAttribute, serviceInfo);
        this.serviceIcon = serviceIcon;
        serviceName = nameAttribute.name;
        // Gather relevant information about this service.
        GenericServiceInfo genericServiceInfo = new GenericServiceInfo(this, serviceName, joinManager);
        // Associate the relevant information so that it is available when the service is terminated.
        genericServices.put(this, genericServiceInfo);
    }

    /**
     * Initializes the service agent.  A security manager is established.  An instance of the
     * service is created and registered by the JoinManager with the Lookup Service.
     *
     * @param command line arguments for the service.
     */
    public static void initialize () {
        Log.makeLog();
        SecurityManager securityManager = System.getSecurityManager();
        if ((securityManager == null) || (!(securityManager instanceof RMISecurityManager))) {
            Log.current.println("Setting RMI security manager");
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    /**
     * Shutsdown the agents.
     */
    public static void shutdown () {
        Log.current.println("Exiting agents");
        Enumeration services = genericServices.elements();
        while (services.hasMoreElements()) {
            GenericServiceInfo genericServiceInfo = (GenericServiceInfo)services.nextElement();
            genericServiceInfo.genericService.shutdownAgent();
        }
        Log.current.close();
        System.exit(0);
    }

    /**
     * Shutsdown one agent.
     */
    public void shutdownAgent () {
        Log.current.println("Exiting agent " + serviceName);
        Log.current.println("Cancelling Lookup Service lease(s)");
        joinManager.terminate();
        genericServices.remove(this);
        Enumeration responders = respondingAgents.elements();
        while (responders.hasMoreElements()) {
            RespondingAgentInfo respondingAgentInfo = (RespondingAgentInfo)responders.nextElement();
            respondingAgentInfo.clientHelper.finalize();
        }
    }

    /**
     * Obtain the service object specified by the argument name.
     *
     * @param name the name of the service.
     * @return The proxy object for the service, which exposes the remote service methods.
     */
    public GenericServiceInterface getService (String name) {
        RespondingAgentInfo respondingAgentInfo = (RespondingAgentInfo)respondingAgents.get(name);
        if (respondingAgentInfo == null)
            return  null;
        else
            return  respondingAgentInfo.clientHelper.getService();
    }

    /**
     * Sends an AMP (Agent Message Protocol) message to the Grid.
     *
     * @param message the Agent Message Protocol to be sent.
     * @return The response in the form of an AMP message.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public Amp sendAmpMessage (Amp message) throws RemoteException, CycApiException {
        Log.current.println("Sending AMP " + message);
        // Obtain the proxy for the receiver of this message.
        String receiverName = message.receiver();
        if (!respondingAgents.containsKey(receiverName)) {
            findAgent(receiverName);
        }
        RespondingAgentInfo respondingAgentInfo = (RespondingAgentInfo)respondingAgents.get(receiverName);
        GenericServiceInterface respondingAgent = respondingAgentInfo.respondingAgent;
        ClientHelper clientHelper = respondingAgentInfo.clientHelper;
        Lease lease = clientHelper.getServiceLease();
        Amp reply = null;
        try {
            // Send the message to its receiver.
            Log.current.println("Responding agent " + respondingAgent);
            reply = respondingAgent.sendAmpMessage(lease, message);
        } catch (LeaseDeniedException e) {
            Log.current.errorPrintln("Lease denied when accessing " + respondingAgentInfo.name + "  "
                    + e);
        }
        return  reply;
    }

    /**
     * Finds the specified agent and obtain a lease on its services.
     *
     * @param name the name of the agent to find.
     * @exception RemoteException if the agent cannot be located.
     */
    private void findAgent (String name) throws RemoteException {
        // Name entry for LookupService.
        Name nameAttribute = new Name(name);
        clientHelper = new ClientHelper(this, nameAttribute);
        GenericServiceInterface respondingAgent = (GenericServiceInterface)clientHelper.getService();
        Log.current.println("Caching agent info for " + name + " service: " + respondingAgent);
        RespondingAgentInfo respondingAgentInfo = new RespondingAgentInfo(respondingAgent, name, clientHelper);
        respondingAgents.put(name, respondingAgentInfo);
    }

    /**
     * Receives an AMP (Agent Message Protocol) message from the Grid.
     * Subclasses may override with a specific implementation.
     *
     * @param message the Agent Message Protocol to be sent.
     * @return The response in the form of an AMP message.
     */
    public Amp ampMessageReceived (Amp message) throws RemoteException, CycApiException {
        // Subclasses override this method.
        return  new Amp();
    }

    /**
     * Receives an AMP (Agent Message Protocol) from a remote client.
     *
     * @param message the Agent Message Protocol received.
     * @return The response in the form of an AMP message.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public Amp sendAmpMessage (Lease lease, Amp message) throws RemoteException, LeaseDeniedException,
            CycApiException {
        Log.current.println("Responding to client's request");
        validateServiceLease(lease);
        return  ampMessageReceived(message);
    }

    /**
     * Handles an event notification from another service agent.
     *
     * @param rev the remote event from the other service agent.
     * @exception RemoteException if a communications-related problem occurs during
     * a remote method call.
     */
    public void notify (RemoteEvent rev) throws RemoteException {
        if (!(rev instanceof TerminateServiceEvent)) {
            Log.current.errorPrintln("Unexpected event type");
        }
        TerminateServiceEvent cev = (TerminateServiceEvent)rev;
        GenericServiceInterface agent = (GenericServiceInterface)cev.getSource();
        Log.current.println("Received terminate service event notification from service agent " + agent);
        // Finalize communications with this agent.
        ClientHelper clientHelper = null;
        clientHelper.finalize();
    }

    /**
     * Uses the JoinManager to register this service with lookup services.
     *
     * @param service the service instance.
     */
    public void joinLookupServices (Object service, Name nameAttribute, ServiceInfo serviceInfo) {
        Entry[] attributes = new Entry[2];
        attributes[0] = nameAttribute;
        attributes[1] = serviceInfo;
        try {
            Log.current.println("Joining Lookup Service(s) with " + service.getClass() + " and " + nameAttribute);
            joinManager = new JoinManager(service,
                                          attributes,
                                          (ServiceIDListener) null,
                                          (DiscoveryManagement) null,
                                          (LeaseRenewalManager) null);
        } catch (IOException e) {
            Log.current.errorPrintln("Error joining LookupService " + e);
        }
    }

    /**
     * Provides a new event registration object to the client.
     *
     * @param leaseDuration the event notification lease duration in milliseconds.
     * @param rel the event listener provided by the client to receive events from the service.
     * @param handback the optional object provided by the client, which is returned to the client
     * with each event.
     * @return The new event registration object.
     * @see ServerDelivery
     */
    public EventRegistration requestNofication (long leaseDuration,
                                                RemoteEventListener rel,
                                                MarshalledObject handback) throws RemoteException,
            LeaseDeniedException {
        Log.current.println("Providing notification of service events");
        return  sender.addListener(rel, leaseDuration, handback, TerminateServiceEvent.ID);
    }

    /**
     * Provide a new service lease to the client.
     *
     * @param duration the service lease duration in milliseconds.
     * @return The new service registration lease.
     */
    public Lease requestServiceLease (long leaseDuration) throws RemoteException, LeaseDeniedException {
        Hashtable ht = new Hashtable(13);
        Log.current.println("Providing service registration");
        return  lord.newLease(new Hashtable(), leaseDuration, "service registration");
    }

    /**
     * Return a service event to the ServiceDelivery caller.
     *
     * @param sequenceNumber the event sequence number.
     * @handback the handback object provided by the client upon registration.
     * @return A remote event object from the service for delivery to registered clients.
     * @see RemoteEventFactory
     */
    public RemoteEvent makeRemoteEvent (long sequenceNumber, MarshalledObject handback) {
        return  new TerminateServiceEvent(this, sequenceNumber, handback);
    }

    /**
     * Throws an exception if the service lease is expired.
     *
     * @param lease the service lease.
     * @exception if the service lease is expired.
     */
    public void validateServiceLease (Lease lease) throws LeaseDeniedException {
        if (lord.getSubjectOfLease(lease) == null)
            throw  new LeaseDeniedException("Lease expired");
    }

    /**
     * Returns the service administration proxy object, which in this interface is the same object as
     * the service proxy (RMI stub) object.
     *
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public Object getAdmin () throws RemoteException {
        return  this;
    }

    /**
     * Returns an icon that represents the service.
     * Subclasses should override this method, or set serviceIcon to the appropriate
     * distinguishing icon.
     */
    public Icon getIcon () throws RemoteException {
        if (serviceIcon == null)
            serviceIcon = new ImageIcon("small_green_box.gif");
        return  serviceIcon;
    }

    /**
     * Shutsdown the service.
     *
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public void destroy () throws RemoteException {
        shutdown();
    }

    /**
     * Sets the location of the service's persistent storage, moving all current persistent storage
     * from the current location to the specified new location.
     *
     * @param location the platform specific directory pathname.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     * @exception IOException if moving the persistent storage fails.
     */
    public void setStorageLocation (String location) throws RemoteException, IOException {
        Log.current.println("Updating log storage location to " + location);
        Log.current.setStorageLocation(location);
    }

    /**
     * Returns the location of the service's persistent storage.
     *
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public String getStorageLocation () throws RemoteException {
        String location = Log.current.getStorageLocation();
        Log.current.println("Providing to admin the log storage location " + location);
        return  location;
    }

    /**
     * Gets the current attribute sets for the service.
     *
     * @return The current attribute sets for the service.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public Entry[] getLookupAttributes () throws RemoteException {
        return  joinManager.getAttributes();
    }

    /**
     * Adds attribute sets for the service. The resulting set will be used for all future joins.
     * The attribute sets are also added to all currently-joined lookup services.
     *
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * @param attrSets the attribute sets to add
     * remote method call.
     */
    public void addLookupAttributes (Entry[] attrSets) throws RemoteException {
        joinManager.addAttributes(attrSets, true);
    }

    /**
     * Modifies the current attribute sets, using the same semantics as ServiceRegistration.modifyAttributes.
     * The resulting set will be used for all future joins. The same modifications are also made to all
     * currently-joined lookup services.
     *
     * @param attrSetTemplates the templates for matching attribute sets.
     * @param attrSets the modifications to make to matching sets.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public void modifyLookupAttributes (Entry[] attrSetTemplates, Entry[] attrSets)
        throws RemoteException {
        joinManager.modifyAttributes(attrSetTemplates, attrSets, true);
    }


}



