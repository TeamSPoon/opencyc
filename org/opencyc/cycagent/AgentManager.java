package  org.opencyc.cycagent;

import java.net.*;
import java.util.*;
import java.io.*;
import fipaos.ont.fipa.*;
import org.opencyc.api.*;
import org.opencyc.util.*;

/**
 * Provides a FIPA-OS agent proxy and a Grid proxy for an OpenCyc server.<p>
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

public class AgentManager {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * singleton instance of AgentManager
     */
    public static AgentManager agentManager;

    /**
     * One to one ssociation of local cyc agents with their CycAgentInfo object.
     * cyc agent name -> CycAgentInfo
     */
    public static Hashtable cycAgents;

    /**
     * Well known port where the Agent Manager listens for requests from cyc clients.
     */
    public static final int LOCAL_CLIENT_LISTENER_PORT = 4444;

    /**
     * Maximum number of local cyc clients supported by this listener.
     */
    public static final int MAX_LOCAL_CLIENT_CLIENTS = 50;

    /**
     * The socket which listens for new connections.
     */
    protected ServerSocket listenerSocket = null;

    /**
     * Provides the main method.
     */
    public static void main(String[] args) {
        Log.makeLog();
        String localHostName = null;
        try {
            localHostName = InetAddress.getLocalHost().getHostName();
        }
        catch (java.net.UnknownHostException e) {
            Log.current.println("Cannot obtain local host name " + e.getMessage());
            localHostName = "";
        }
        agentManager = new AgentManager();
        if (agentManager.verbosity > 1)
            Log.current.println("Agent manager started at " + localHostName);
        agentManager.listenForCycServers();
    }

    /**
     * Constructs a new AgentManager object.
     * This singleton instance manages one or more KB's on a single host.
     */
    public AgentManager () {
        cycAgents = new Hashtable();
    }

    /**
     * Handles connecting Cyc servers.
     */
    protected void listenForCycServers () {
        try {
            listenerSocket = new ServerSocket(LOCAL_CLIENT_LISTENER_PORT, MAX_LOCAL_CLIENT_CLIENTS);
            while (true) {
                if (verbosity > 2)
                    Log.current.println("Listening on port " + LOCAL_CLIENT_LISTENER_PORT);
                Socket cycSocket = listenerSocket.accept();
                if (verbosity > 2)
                    Log.current.println("Cyc Connection accepted " + cycSocket);
                // Spawn child thread to read from the socket.
                CycProxyFactory cycProxyFactory =
                    new CycProxyFactory(cycSocket);
                Thread cycProxyFactoryThread = new Thread(cycProxyFactory);
                cycProxyFactoryThread.start();
            }
        }
        catch (IOException e) {
            Log.current.println("Failed I/O: " + e);
            System.exit(1);
        }
        finally {
            try {
                listenerSocket.close();
            }
            catch (IOException e) {
            }
        }
    }
}






