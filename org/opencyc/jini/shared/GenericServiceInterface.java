package org.opencyc.jini.shared;

/**
 * The GenericServiceInterface provides facilities for clients to obtain services and events
 * from the service, subject to leases.<p>
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

import java.io.*;
import java.rmi.*;
import javax.swing.*;
import net.jini.core.discovery.*;
import net.jini.core.entry.*;
import net.jini.core.event.*;
import net.jini.core.lease.*;
import net.jini.admin.*;
import com.sun.jini.admin.*;
import org.opencyc.api.*;
import org.opencyc.util.*;

public interface GenericServiceInterface extends Remote,
                                                 Administrable,
                                                 DestroyAdmin,
                                                 StorageLocationAdmin {

    /**
     * Receives an AMP (Agent Message Protocol) from a remote client.
     * @param message the Agent Message Protocol received.
     * @return The response in the form of an AMP message.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public Amp sendAmpMessage(Lease lease, Amp message)
        throws RemoteException, LeaseDeniedException, CycApiException;

    /**
     * Obtains an EventRegistration object containing a lease to the client enabling the client
     * to receive notification of events from the service
     * @param leaseDuration the duration of the lease in milliseconds.
     * @param rel the event listener provided by the client for the service's events.
     * @param key a handback object stored by the service and returned with each event.
     * @return An EventRegistration object containing the event notification lease.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     * @exception LeaseDeniedException if the lease request is denied.
     */
    public EventRegistration requestNofication(long leaseDuration,
                                               RemoteEventListener rel,
                                               MarshalledObject handback) throws RemoteException,
                                                                                 LeaseDeniedException;

    /**
     * Obtains a service lease for the requested duration.
     * @param leaseDuration the duration of the service lease in milliseconds.
     * @return The service lease.
     */
    public Lease requestServiceLease(long leaseDuration) throws RemoteException, LeaseDeniedException;

    /**
     * Returns the service administration proxy object, which in this interface is the same object as
     * the service proxy (RMI stub) object.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public Object getAdmin() throws RemoteException;

    /**
     * Returns an icon that represents the service.
     */
    public Icon getIcon() throws RemoteException;

    /**
     * Shutsdown the service.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public void destroy() throws RemoteException;

    /**
     * Sets the location of the service's persistent storage, moving all current persistent storage
     * from the current location to the specified new location.
     * @param location the platform specific directory pathname.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     * @exception IOException if moving the persistent storage fails.
     */
    public void setStorageLocation(String loc)
        throws RemoteException, IOException;

    /**
     * Returns the location of the service's persistent storage.
     * @exception RemoteException if a communications-related problem occurs during the processing of a
     * remote method call.
     */
    public String getStorageLocation() throws RemoteException;


}
