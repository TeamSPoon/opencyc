package  org.opencyc.jini.shared;

/**
 * Provides a facility for tracking expiring leases.
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
import  com.sun.jini.thread.*;
import  com.sun.jini.lease.landlord.*;
import  org.opencyc.util.*;


public class Expirer
        implements LeaseManager {
    /**
     * The interface for owners of leased resources.
     * @see ServerLandlord
     */
    Landlord landlord;

    /**
     * Task scheduler to handle lease expirations.
     */
    WakeupManager manager;

    /**
     * leased resource -> task ticket.
     *
     * Each leased resource has an associated task which
     * is to awaken when the resource lease expires.
     */
    HashMap tickets;

    /**
     * Instances of this class are scheduled to run at a specific
     * time by the wakeup manager -- they will run when the lease
     * has expired, and they will cancel the lease. A lease renewal
     * will remove the task from the wakeup managers queue.
     */
    class ExpirerTask
            implements Runnable {
        /**
         * The interface defining a leased resource.
         */
        LeasedResource resource;

        /**
         * Constructs an ExpirerTask object.
         *
         * @param r the leased resource.
         */
        ExpirerTask (LeasedResource r) {
            resource = r;
        }

        /**
         * Starts this thread when the associated lease is due to expire.
         * If the lease on the resource was renewed, then this thread is
         * rescheduled for the new expiration time.  Otherwise the lease
         * expired and is cancelled.
         *
         * @see ServerLandlord#cancel
         */
        public void run () {
            long now = System.currentTimeMillis();
            long expir = resource.getExpiration();
            Log.current.println("Checking an expiring lease ");
            if (expir > now) {
                // It was renewed, but we didn't cancel the task in time.
                Log.current.println("Lease was renewed");
                WakeupManager.Ticket ticket = manager.schedule(expir, this);
                tickets.put(resource, ticket);
            }
            else
                try {
                    landlord.cancel(resource.getCookie());
                } catch (Exception e) {}
        }
    }

    /**
     * Constructs an Expirer object.
     *
     * @param landlord the Landlord managing the leases.
     * @see ServerLandlord
     */
    public Expirer (Landlord landlord) {
        this.landlord = landlord;
        tickets = new HashMap(13);
        manager = new WakeupManager();
    }

    /**
     * Notifies the manager of a lease being renewed. A side-effect of registering
     * the new duration will be to cancel the old task from the taskmanager queue.
     *
     * @param the resource associated with the new lease.
     * @param duration the duration of the lease.
     * @param oldExpiration the expiration the resource had before it was renewed.
     */
    public void renewed (LeasedResource resource, long duration, long oldExpiration) {
        register(resource, duration);
    }

    /**
     * Notifies the manager of a new lease being created.
     * Cancel its old expiration task and create a new
     * expiration task to cancel the lease after it expires.
     *
     * @param the resource associated with the new lease.
     * @param duration the duration of the lease.
     */
    public void register (LeasedResource resource, long duration) {
        WakeupManager.Ticket ticket;
        ticket = (WakeupManager.Ticket)tickets.remove(resource);
        if (ticket != null)
            manager.cancel(ticket);
        ExpirerTask task = new ExpirerTask(resource);
        ticket = manager.schedule(resource.getExpiration(), task);
        tickets.put(resource, ticket);
    }
}



