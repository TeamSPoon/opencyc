package  org.opencyc.jini.shared;

/**
 * Receives notification of new Lookup Services.
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
import  net.jini.core.lookup.*;
import  net.jini.core.event.*;
import  org.opencyc.util.*;


public class ServiceFinderListener extends UnicastRemoteObject
        implements RemoteEventListener {

    /**
     * Reference to the ServiceFinder parent object.
     */
    protected ServiceFinder parent;

    /**
     * Constructs a ServiceFinderListener object.
     *
     * @param parent the ServiceFinder on whose behalf this object listens for new Lookup Services.
     */
    public ServiceFinderListener (ServiceFinder parent) throws RemoteException
    {
        this.parent = parent;
    }

    /**
     * Handles a change in Lookup Services.
     */
    public void notify (RemoteEvent event) throws UnknownEventException, RemoteException {
        Log.current.println("Received Lookup Service event");
        if (!(event instanceof ServiceEvent))
            throw  new UnknownEventException("ServiceFinderListener");
        ServiceEvent sevent = (ServiceEvent)event;
        ServiceItem item = sevent.getServiceItem();
        if (sevent.getTransition() == ServiceRegistrar.TRANSITION_NOMATCH_MATCH) {
            parent.addServiceItem(item);
        }
        else
            Log.current.println("Lookup Service event did not provide a matching service.");
    }
}



