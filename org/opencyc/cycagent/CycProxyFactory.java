package  org.opencyc.cycagent;

import  java.net.Socket;
import  java.io.PrintWriter;
import  java.io.*;
import  java.util.Collection;
import  fipaos.ont.fipa.fipaman.*;
import  fipaos.ont.fipa.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;

/**
 * Receives the communication initiation message from a Cyc server and creates the
 * corresponding CycProxy object.<p>
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

class CycProxyFactory implements Runnable {

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
     * Reference to parent's socket which is connected to a cyc client using the binary cfasl
     * protocol.
     */
    protected Socket cfaslSocket = null;

    /**
     * Agent Communication Language
     */
    protected String acl = null;

    /**
     * Manages connection to a cyc server.
     */
    protected CycConnection cycConnection;

    /**
     * Constructs a new CycProxyFactory object given a reference to
     * the cyc client's socket connection.
     */
    public CycProxyFactory (Socket cfaslSocket) {
        this.cfaslSocket = cfaslSocket;
    }

    /***
     * Executes the CycProxyFactory thread and processes the
     * the (cyc-to-agent-manager-init <image-id> <base-port>) message.
     */
    public void run () {
        Log.current.println("Begin CycProxyFactory thread");
        try {
            cycConnection = new CycConnection(cfaslSocket);
        }
        catch (Exception e) {
            Log.current.println("Exception creating CycConnection " + e.getMessage());
            throw new RuntimeException("Exception creating CycConnection: " + e.getMessage());
        }
        CycList cycToAgentManagerInitMessage = null;
        try {
            //cycConnection.traceOnDetailed();
            cycToAgentManagerInitMessage = cycConnection.receiveBinaryApiRequest();
        }
        catch (Exception e) {
            Log.current.println("Exception with CycConnection: " + e.getMessage());
            return;
        }
        if (! cycToAgentManagerInitMessage.first().equals(CycObjectFactory.makeCycSymbol("cyc-to-agent-manager-init"))) {
            Log.current.println("Invalid initialization message: " + cycToAgentManagerInitMessage);
            return;
        }
        String cycImageId = (String) cycToAgentManagerInitMessage.second();
        int basePort = ((Integer) cycToAgentManagerInitMessage.third()).intValue();
        String myAgentName = "cyc-api-service-" + cycImageId;
        CycProxy cycProxy = new CycProxy(myAgentName, verbosity);
        cycProxy.agentsCycConnection = cycConnection;
        CycAgentInfo cycAgentInfo = new CycAgentInfo(basePort, cycImageId, cycProxy);
        AgentManager.cycAgents.put(myAgentName, cycAgentInfo);
        cycProxy.initializeAgentCommunity();
        cycProxy.handleMessagesFromCyc();
    }

    /**
     * Closes the cyc connection.
     */
    public void close () {
        try {
            cycConnection.close();
        }
        catch (Exception e) {
            Log.current.println("Exception closing socket i/o: " + e);
        }
    }

    /**
     * Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
}






