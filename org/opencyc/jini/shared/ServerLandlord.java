package org.opencyc.jini.shared;

/**
 * Responsible for issuing, cancelling and renewing leases.
 * Clients of the service remotely call these methods.
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

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import net.jini.core.lease.*;
import com.sun.jini.lease.landlord.*;
import org.opencyc.util.*;

public class ServerLandlord extends UnicastRemoteObject implements Landlord {

    /**
     * Associates a cookie with each ServerResource.
     * cookie -> ServerResource
     */
    protected Hashtable resources = new Hashtable();

    /**
     * Associates description with each lease.
     * cookie -> description
     */
    protected Hashtable descriptions = new Hashtable();

    /**
     * Maximum lease time granted in milliseconds.
     */
    protected long MAXLEASETIME = 5*60*1000;        // 5 Minutes

    /**
     * The lease policy object governing the ServerLandlord's lease issuance.
     */
    protected LeasePolicy policy;


    /**
     * Constructs a ServerLandlord object.  An new Expirer lease manager object is created
     * to receive notification of new and renewed leases.
     *
     * @exception RemoteException if communications-related problems occur during a
     * remote method call.
     * @see Expirer
     */
    public ServerLandlord() throws RemoteException {
        policy = new LeaseDurationPolicy(MAXLEASETIME, MAXLEASETIME, this, new Expirer(this), null);
    }

    /**
     * Called by the lease when its renew method is called.
     *
     * @param cookie unique object associated with the lease when it was created.
     * @param duration the milliseconds duration argument passed to
     * the Lease.renew() call.
     * @return The new duration the lease should have.
     * @exception LeaseDeniedException if the lease is denied according to the LeaseDurationPolcy.
     * @exception UnknownLeaseException if the leased resource associated with the cookie is not found.
     * @exception RemoteException if communications-related problems occur during a
     * remote method call.
     */
    public long renew(Object cookie, long duration)
            throws LeaseDeniedException, UnknownLeaseException, RemoteException {

        Log.current.println("Client renewing lease for " + descriptions.get(cookie));
        synchronized (this) {
            ServerResource sr = (ServerResource)resources.get(cookie);
            if (sr == null)
                throw new UnknownLeaseException();

            return policy.renew(sr, duration);
        }
    }

    /**
     * Called by the lease map when its renewAll method is called.
     *
     * @param cookie array of unique objects associated with the each lease when
     * the lease was created.
     * @param duration the The milliseconds duration argument for each lease from the
     * lease map.
     * @return The results of the renew as an array. For each lease from the map, contains either
     * a granted duration or a lease denied exception.
     * @exception RemoteException if communications-related problems occur during a
     * remote method call.
     */
    public Landlord.RenewResults renewAll(Object[] cookie, long[] duration)
            throws RemoteException {
        long[] granted = new long[cookie.length];
        Vector denied = new Vector();

        for (int i = 0; i < cookie.length; i++) {
            try {
                granted[i] = renew(cookie[i], duration[i]);
            } catch (LeaseException lex) {
                granted[i] = -1;
                denied.add(lex);
            }
        }
        return new Landlord.RenewResults(granted,
                denied.isEmpty() ? null : (Exception[]) denied.toArray());
    }

    /**
     * Called by the lease when its cancel method is called.
     *
     * @param cookie unique object associated with the lease when it was created.
     * @exception UnknownLeaseException if the leased resource associated with the cookie is not found.
     * @exception RemoteException if communications-related problems occur during a
     * remote method call.
     */
    public void cancel(Object cookie)
            throws UnknownLeaseException, RemoteException {

        Log.current.println("Client cancelling lease for " + descriptions.get(cookie));
        synchronized (this) {
            ServerResource sr = (ServerResource)resources.get(cookie);
            if (sr == null)
                throw new UnknownLeaseException();

            resources.remove(cookie);
        }
    }

    /**
     * Called by the lease map when its cancelAll method is called.
     *
     * @param cookie array of unique objects associated with the each lease when
     * the lease was created.
     * @exception LeaseMapException if a LeaseException occurs when one of the leases
     * in the map cannot be cancelled.
     * @exception RemoteException if communications-related problems occur during a
     * remote method call.
     */
    public Map cancelAll(Object[] cookie)
            throws RemoteException {
        Map map = null;
        for (int i = 0; i < cookie.length; i++) {
            try {
                cancel(cookie[i]);
            }
            catch (LeaseException ex) {
                if (map == null)
                    map = new HashMap();
                map.put(cookie[i], ex);
            }
        }
        if (map != null)
            throw new RuntimeException("Can't cancel all leases" + map.toString());
        return map;
    }

    // The following methods are not part of the Landlord interface
    //    and can only be called by directly using this object.

    /**
     * Returns a lease on the session data stored on behalf of a client.
     *
     * @param sessionData the leased resource consisting of the client's session data
     * stored at the service agent.
     * @param duration the granted duration of the lease in milliseconds.
     * @return the lease object.
     * @exception LeaseDeniedException if the lease is denied.
     */
    public Lease newLease(Object sessionData,
                          long duration,
                          String description) throws LeaseDeniedException {

        ServerResource sr = new ServerResource(sessionData);
        Log.current.println("Granting new lease for " + description);
        sr.lease = policy.leaseFor(sr, duration);
        Integer cookie = (Integer) sr.getCookie();
        synchronized(this) {
            resources.put(cookie, sr);
            descriptions.put(cookie, description);
        }
        return sr.lease;
    }

    // The cookie is an *internal* reference for the LandlordLease. No one outside
    //    the lease can access it.

    /**
     * Obtains the leased object associated with the specified client lease.
     *
     * @param lease the client's lease on the stored session data.
     * @return The session data.
     * @see ServerResource
     */
    public Object getSubjectOfLease(Lease lease) {
        Object subjectOfLease = null;
        synchronized (this) {
            for (Enumeration e = resources.elements();e.hasMoreElements();) {
                ServerResource sr = (ServerResource) e.nextElement();
                if (lease.equals(sr.lease)) {
                    subjectOfLease = sr.subjectOfLease;
                    break;
                }
            }
        }
        return subjectOfLease;
    }
}
