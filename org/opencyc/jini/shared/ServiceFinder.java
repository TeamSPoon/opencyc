package  org.opencyc.jini.shared;

/**
 * Discovers services using the LookupService.
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
import  java.rmi.*;
import  java.util.*;
import  net.jini.discovery.*;
import  net.jini.core.entry.*;
import  net.jini.core.event.*;
import  net.jini.core.lease.*;
import  net.jini.core.lookup.*;
import  net.jini.lease.*;
import  org.opencyc.util.*;


public class ServiceFinder
        implements DiscoveryListener {
    private static String[] publicGroup = new String[] {
        ""
    };
    private ServiceItem returnObject = null;
    // service ID -> ServiceItem.
    private Hashtable items = new Hashtable();
    // LookupService -> EventRegistration containing lease.
    private Hashtable leases = new Hashtable();
    private LeaseRenewalManager lrm;
    private ServiceFinderListener sfl;
    private LookupDiscovery reg;
    private ServiceTemplate template;

    /**
     * Constructs a ServiceFinder object.
     *
     * @param serviceInterface the class of the service required.
     * @IOException if an error occured within LookupDiscovery starting discovery.
     */
    public ServiceFinder (Class serviceInterface) throws IOException {
        this(publicGroup, serviceInterface, (Entry[])null);
        Log.current.println("Service finder using " + serviceInterface);
    }

    /**
     * Constructs a ServiceFinder object.
     *
     * @param serviceInterface the class of the service required.
     * @attribute a single Entry attribute describing the service required.
     * @IOException if an error occured within LookupDiscovery starting discovery.
     */
    public ServiceFinder (Class serviceInterface, Entry attribute) throws IOException {
        this(publicGroup, serviceInterface, new Entry[] {
            attribute
        });
        Log.current.println("Service finder using " + serviceInterface + " and " + attribute);
    }

    /**
     * Constructs a ServiceFinder object.
     *
     * @param serviceInterface the class of the service required.
     * @attributes an array of Entry attributes describing the service required.
     * @IOException if an error occured within LookupDiscovery starting discovery.
     */
    public ServiceFinder (Class serviceInterface, Entry[] attributes) throws IOException {
        this(publicGroup, serviceInterface, attributes);
    }

    /**
     * Constructs a ServiceFinder object.
     *
     * @param groups an array of LookupService groups in which the required service is to be found.
     * @param serviceInterface the class of the service required.
     * @attributes an array of Entry attributes describing the service required.
     * @IOException if an error occured within LookupDiscovery starting discovery.
     */
    public ServiceFinder (String[] groups, Class serviceInterface, Entry[] attributes)
        throws IOException {
        // Construct the template here for matching in the lookup service
        // We don't use the template until we actually discover a service
        Class[] name = new Class[] {
            serviceInterface
        };
        template = new ServiceTemplate(null, name, attributes);
        // Obtain a LeaseRenewalManager to maintain our leases with Lookup Services.
        lrm = new LeaseRenewalManager();
        // Obtain a ServiceFinderListener to be notificed of new Lookup Services.
        sfl = new ServiceFinderListener(this);
        // Create the facility to perform multicast discovery for all
        // lookup services
        Log.current.println("Discovering Lookup Services");
        reg = new LookupDiscovery(groups);
        reg.addDiscoveryListener(this);
    }

    /**
     * Performs actions when a lookup service is discovered
     * via the listener callback of the addDiscoveryListener method.
     *
     * @param dev the discovery event containing information about Lookup Services added or
     * discarded.
     */
    public synchronized void discovered (DiscoveryEvent dev) {
        ServiceRegistrar[] lookup = dev.getRegistrars();
        // We may have discovered one or more lookup services
        for (int i = 0; i < lookup.length; i++) {
            try {
                Log.current.println("Discovered a Lookup Service at " + lookup[i].getLocator());
                // Register for event notification. While the event registration is in effect,
                // a ServiceEvent is sent to the specified listener whenever a register,
                // lease cancellation or expiration, or attribute change operation results in
                // an item changing state in a way that satisfies the template and transition combination.
                Log.current.println("Registering at the Lookup Service for notification of matching service agents");
                EventRegistration reg = lookup[i].notify(template, ServiceRegistrar.TRANSITION_NOMATCH_MATCH,
                        sfl, null, Lease.FOREVER);
                // The lease renewal manager maintains the lease on the discovered Lookup Service.
                Log.current.println("Received lease for Lookup Service notification");
                lrm.renewUntil(reg.getLease(), Lease.FOREVER, null);
                leases.put(lookup[i], reg);
            }
            catch (RemoteException rex) {
                Log.current.errorPrintln("Registration error " + rex);
                discarded(dev);
            }
            try {
                // Find services registered at this Lookup Service that match our template.
                ServiceMatches items = lookup[i].lookup(template, Integer.MAX_VALUE);
                // Each lookup service may have zero or more registered
                // servers that implement our desired template
                for (int j = 0; j < items.items.length; j++) {
                    if (items.items[j].service != null)
                        // Put each matching service into our vector
                        addServiceItem(items.items[j]);
                    // else the service item couldn't be deserialized
                    // so the lookup() method skipped it
                }
            }
            catch (RemoteException ex) {
                Log.current.errorPrintln("ServiceFinder Error: " + ex);
                discarded(dev);
            }
        }
    }

    /**
     * Performs actions when an error talking to a lookup service caused it to be discarded
     * Removes any references to it from our hashtable, and cancels
     * our leases to that service.
     *
     * @param dev the discovery event associated with a discarded lookup service.
     */
    public synchronized void discarded (DiscoveryEvent dev) {
        ServiceRegistrar[] lookup = dev.getRegistrars();
        for (int i = 0; i < lookup.length; i++) {
            try {
                EventRegistration reg = (EventRegistration)leases.get(lookup[i]);
                if (reg != null) {
                    Log.current.println("Discarding " + lookup[i]);
                    leases.remove(lookup[i]);
                    lrm.remove(reg.getLease());
                }
            } catch (UnknownLeaseException ule) {}
        }
    }

    /**
     * Adds a ServiceItem, found at a lookup service and matching our template,
     * to a hashtable indexed by unique service ID.  Awakens threads waiting on
     * on this ServiceFinderListener.
     *
     * @item the ServiceItem matching the required service.
     */
    public synchronized void addServiceItem (ServiceItem item) {
        Log.current.println("Found service agent ID " + item.serviceID + " matching the service template");
        items.put(item.serviceID, item);
        // Awaken all threads waiting on this ServiceFinderListener,
        // specifically the one potentially waiting as a result of the
        // getObject() method.
        Log.current.println("Awakening waiting client thread");
        notifyAll();
    }

    /**
     * Obtains the first service object matching the template request when called by the client agent.
     * Waits until one is found, before returning.
     *
     * @return The ServiceItem containing a reference to the service.
     */
    public synchronized Object getObject () {
        if (returnObject == null) {
            Log.current.println("Client thread waiting for service to be found");
            while (items.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ie) {}
                Log.current.println("Client thread resuming after service found");
            }
            returnObject = (ServiceItem)items.elements().nextElement();
        }
        return  returnObject.service;
    }

    /**
     * Cleans up the service items hashtable.
     * If an error is encountered when using a service item, the client
     * should call this method. A new service item can then be obtained by calling
     * the getObject() method.
     *
     * @param obj the ServiceItem associated with an error.
     */
    public synchronized void errored (Object obj) {
        if ((obj != null) && (returnObject != null)) {
            if (obj.equals(returnObject.service)) {
                items.remove(returnObject.serviceID);
                returnObject = null;
            }
        }
    }
}



