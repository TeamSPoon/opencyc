package  org.opencyc.jini.shared;

/**
 * Meets interface requirements for LeaseDurationPolicy.
 * Maps each leased resource to a unique cookie, and contains the expiration
 * time of the leased resource.
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

import  net.jini.core.lease.*;
import  com.sun.jini.lease.landlord.*;


public class ServerResource
        implements LeasedResource {
    /**
     * Incremented to provide unique new cookie.
     */
    protected static int token = 0;

    /**
     * A unique identifier that can be used by the grantor of the resource to
     * identify it in the context of a Landlord.renew() or Landlord.cancel() call.
     */
    protected Integer cookie;

    /**
     * Expiration time of the leased resource.
     */
    protected long expiration;

    /**
     * Copy of the client's lease.
     */
    public Lease lease;         // Only a copy (not the client's)

    /**
     * The object which is leased.
     */
    public Object subjectOfLease;

    // Simple resource mapper -- using a static int to ensure that
    // each lease resource will have a unique cookie

    /**
     * Constructs a ServerResource object.
     *
     * @param subjectOfLease the object which is leased.
     */
    public ServerResource (Object subjectOfLease) {
        synchronized (ServerResource.class) {
            cookie = new Integer(token++);
        }
        this.subjectOfLease = subjectOfLease;
    }

    /**
     * Gets the expiration time of the leased resource.
     *
     * @return The expiration time of the leased resource.
     */
    public long getExpiration () {
        return  expiration;
    }

    /**
     * Sets the expiration time of the leased resource.
     *
     * @param expire time in milliseconds until expiration.
     */
    public void setExpiration (long expire) {
        expiration = expire;
    }

    /**
     * Gets the cookie uniquely associated with the leased resource.
     *
     * @return The cookie object.
     */
    public Object getCookie () {
        return  cookie;
    }
}



