package  org.opencyc.jini.shared;

/**
 * Listens for events sent to the client from the service.
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
import  net.jini.core.event.*;
import  org.opencyc.util.*;


public class ServiceEventHandler extends UnicastRemoteObject
        implements RemoteEventListener {

    /**
     * Reference to the client object.
     */
    protected ClientHelperCallbackInterface client;

    /**
     * Constructs a ServiceEventHandler object.
     *
     * @exception RemoteException if a communications-related problem occurs during
     * a remote method call.
     */
    public ServiceEventHandler (ClientHelperCallbackInterface client) throws RemoteException {
        this.client = client;
    }

    /**
     * Handles an event notification from the service agent by delegating it to
     * the client agent.
     *
     * @param rev the remote event from the service agent.
     * @exception RemoteException if a communications-related problem occurs during
     * a remote method call.
     */
    public void notify (RemoteEvent rev) throws RemoteException {
        Log.current.println("Listener received remote event " + rev);
        client.notify(rev);
    }
}



