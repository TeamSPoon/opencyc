package  org.opencyc.cycagent;

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

import  java.net.*;
import  java.util.*;
import  fipaos.ont.fipa.*;
import  org.opencyc.util.*;
import  org.opencyc.api.CycApiException;
import  org.opencyc.cycobject.*;

public class AgentManager  {
    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * One to one ssociation of local cyc agents with their CycAgentInfo object.
     * cyc agent name -> CycAgentInfo
     */
    public static Hashtable cycAgents;

    /**
     * Singleton thread to listen for requests from Cyc.
     */
    protected static CycListener cycListener;
    protected static Thread cycListenerThread;

    /**
     * Sington instance of AgentManager.
     */
    public static AgentManager agentManager;

    /**
     * Provides the main method.
     */
    public static void main(String[] args) {
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
            Log.current.print("Agent manager started at " + localHostName);
        while (true)
            // Keep root thread running with minimal resource consumption.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
    }

    /**
     * Constructs a new AgentManager object.
     * This singleton instance manages one or more KB's on a single host.
     */
    public AgentManager () {
        cycAgents = new Hashtable();
        cycListener = new CycListener();
        cycListenerThread = new Thread(cycListener);
        cycListenerThread.start();
    }

    /**
     * Forwards a message from a cyc agent to the receipient agent.
     *
     * @param acl the representation of an ACL communication within an Agent.
     */
    public void forward (ACL acl) {
    }


    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        if (cycListener != null)
            cycListener.setVerbosity(verbosity);
    }
}






