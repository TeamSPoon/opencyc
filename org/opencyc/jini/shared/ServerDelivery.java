package  org.opencyc.jini.shared;

/**
 * Manages the delivery of events from a service to the registered clients of the service.
 * The calling service helps to create the event objects.
 * @see ServerLandlord
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

import  java.rmi.*;
import  java.rmi.server.*;
import  java.util.*;
import  net.jini.core.lease.*;
import  net.jini.core.event.*;
import  com.sun.jini.lease.landlord.*;
import  com.sun.jini.thread.*;
import  org.opencyc.util.*;

public class ServerDelivery {
    /**
     * Service for whom deliveries are made.
     */
    protected Remote service;

    /**
     * Landlord that manages leases for the service.
     */
    protected ServerLandlord lord;

    /**
     * Event notification leases.
     */
    protected Vector leases;

    /**
     * Sequence number.
     */
    protected long seqnum = 0;

    /**
     * Pool of threads for event notification.
     */
    protected TaskManager pool;

    /**
     * Class EventInfo contains the client-specific event data which is
     * the resource subject to the client's lease.
     */
    protected class EventInfo {
        /**
         * The client's listener for events from this service.
         */
        public RemoteEventListener listener;

        /**
         * The handback object supplied with events from this service
         * sent to registered clients.
         */
        public MarshalledObject key;
    }

    /**
     * Class CallbackTask is used to run the delivery of the event in a separate
     * thread, one managed by the TaskManager pool.
     */
    protected class CallbackTask
            implements TaskManager.Task {
        /**
         * The client's listener for the event notification.
         */
        RemoteEventListener listener;

        /**
         * The event to be sent from the service to the client.
         */
        RemoteEvent re;

        /**
         * The client's lease on the session data associated with event
         * notification.
         */
        Lease lease;

        /**
         * Constructs a CallbackTask object.
         *
         * @param rel the client's listener for the event notification.
         * @param re the event to be sent from the service to the client.
         * @param l the client's lease on the event notification process.
         */
        CallbackTask (RemoteEventListener rel, RemoteEvent re, Lease l) {
            this.re = re;
            this.listener = rel;
            this.lease = l;
        }

        /**
         * Sends the event from the service to the client.
         */
        public void run () {
            try {
                Log.current.println("Notifying client of...");
                Log.current.println(re.toString());
                listener.notify(re);
            }
            catch (UnmarshalException e) {
                Log.current.println("Listening socket closed by client");
            }
            catch (Exception e) {
                Log.current.errorPrintln("Error notifying client " + e);
                Log.current.printStackTrace(e);
            }
        }

        /**
         * Provides the runAfter method required by the Task interface but does
         * nothing because this task does not have to run after any other tasks.
         * The input parameters are ignored.
         *
         * @param tasks the tasks to consider.  A read-only List, with all
         * elements instanceof Task.
         * @param size elements with index less than size should be considered.
         * @return always false
         */
        public boolean runAfter (List tasks, int size) {
            return  false;
        }
    }

    /**
     * Constructs a new ServerDelivery object.
     *
     * @param service the service instance.
     * @param ll the landlord managing leases.
     * @see ServerLandlord
     */
    public ServerDelivery (Remote service, ServerLandlord ll) {
        this.service = service;
        lord = ll;
        leases = new Vector();
        pool = new TaskManager();
    }

    /**
     * Obtains an EventRegistration for a client so that it can be notified of events
     * from the service.
     *
     * @param rel the client's listener for events from this service.
     * @param leaseDuration the client's requested lease duration for event notification.
     * @param key a handback object provided by the client which it will receive with each
     * event sent from this service.
     * @param id the ID of the remote event.
     * @return The event registration object containing the embedded lease.
     */
    public synchronized EventRegistration addListener (RemoteEventListener rel, long leaseDuration,
            MarshalledObject key, int id) throws LeaseDeniedException {
        EventInfo ei = new EventInfo();
        ei.listener = rel;
        ei.key = key;
        Lease lease = lord.newLease(ei, leaseDuration, "EventRegistration");
        leases.addElement(lease);
        return  new EventRegistration(id, service, lease, seqnum);
    }

    /**
     * Delivers an event to all listening clients.
     *
     * @param value the event content.
     */
    public void deliver () {
        long seq;
        synchronized (this) {
            seq = seqnum++;                     // Deliver request number
        }
        if (leases.isEmpty())
            return;
        Object[] allLeases = leases.toArray();
        for (int i = 0; i < allLeases.length; i++) {
            Lease lease = (Lease)allLeases[i];
            EventInfo obj = (EventInfo)lord.getSubjectOfLease(lease);
            if (obj == null) {
                leases.remove(lease);
            }
            else {
                deliverEvent(obj, seq, lease);
            }
        }
    }

    /**
     * Delivers the event to a listening client.
     *
     * @param data the session data stored at the service on behalf of the client.
     * @param seq the event sequence number.
     * @param value the event content.
     * @param lease the lease on the session data resource.
     */
    private void deliverEvent (EventInfo data, long seq, Lease lease) {
        RemoteEvent event = ((RemoteEventFactoryInterface)service).makeRemoteEvent(seq, data.key);
        Log.current.println("Creating task to deliver " + event);
        pool.add(new CallbackTask(data.listener, event, lease));
    }
}



