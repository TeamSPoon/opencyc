/*

 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  org.opencyc.jini.cycproxy;

/**
 * Provides facilities for clients to obtain services and events
 * from the Cyc api proxy service, subject to leases.
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
import  java.io.IOException;
import  net.jini.core.event.*;
import  net.jini.core.lease.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.api.*;

public interface CycApiServiceInterface extends GenericServiceInterface {

    /**
     * Sends the API request to Cyc.
     *
     * @param lease the lease on this service maintained by the client.
     * @param apiRequest the API request form for Cyc to evaluate.
     * @return The response by Cyc to this API request.
     * @exception LeaseDeniedException if a lease request or renewal is denied.
     * @exception RemoteException if a communications related problem occurs
     * during a remote method call.
     * @exception CycApiException if an error occurs during the Cyc API call.
     *
     */
    public String cycApiRequest (Lease lease, String apiRequest)
        throws RemoteException, LeaseDeniedException, IOException, CycApiException;



    /**
     * Sends the API request to Cyc.  Lease is not required for administrative requests.
     *
     * @param apiRequest the API request form for Cyc to evaluate.
     * @return The response by Cyc to this API request.
     * @exception RemoteException if a communications related problem occurs
     * during a remote method call.
     * @exception CycApiException if an error occurs during the Cyc API call.
     *
     */
    public String cycApiRequest (String apiRequest) throws RemoteException, IOException, CycApiException;
}



