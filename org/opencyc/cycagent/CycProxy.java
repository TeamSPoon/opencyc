package org.opencyc.cycagent;

import java.io.*;
import java.util.*;
import fipaos.ont.fipa.*;
import fipaos.ont.fipa.fipaman.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import org.opencyc.xml.*;

/**
 * Provides a proxy for a cyc agent on the CoABS grid or FIPA-OS agent community.<p>
 *
 * An instance of this class is created for each unique cyc agent which makes
 * itself known to the agent manager.  A cyc image can host one or more cyc
 * agents.  Each message envelope from a cyc agent contains a parameter to
 * indicate which agent agent community processes the messge - either the CoABS
 * grid (Darpa & gov) or the FIPA-OS platform (OpenCyc).
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

public class CycProxy extends GenericAgent {

    /**
     * Cached CycConnection objects which preserve Cyc session state.
     * myAgentName --> CycConnection instance
     */
    protected static Hashtable cycConnectionCache = new Hashtable();

    /**
     * Constructs a CycProxy object.
     *
     * @param myAgentName name of the local agent
     * @param remoteAgentCommunity indicates either CoAbs or FIPA-OS agent community
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CycProxy(String myAgentName, int remoteAgentCommunity, int verbosity) {
        super(myAgentName, remoteAgentCommunity, verbosity);
        if (Log.current == null)
            Log.makeLog();
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (ACL acl) {
        super.messageReceived(acl);
        if ((! messageConsumed) &&
            (acl.getOntology().equals(AgentCommunityAdapter.CYC_API_ONTOLOGY)))
                processApiRequest(acl);
    }

    /**
     * Processes a cyc api request from another agent.
     *
     * @param processApiRequest the echo request Agent Communication Language message
     */
    public void processApiRequest (ACL apiRequestAcl) {
        CycList apiRequest = null;
        String senderName = apiRequestAcl.getSenderAID().getName();
        CycConnection cycConnection = (CycConnection) cycConnectionCache.get(senderName);
        try {
            if (cycConnection == null) {
                cycConnection = new CycConnection();
                cycConnectionCache.put(senderName, cycConnection);
                if (verbosity > 1)
                    Log.current.print("created cyc connection to " + cycConnection.connectionInfo() +
                                      "\nfor " + senderName);
            }
            String contentXml = (String) apiRequestAcl.getContentObject();
            apiRequest = (CycList) CycObjectFactory.unmarshall(contentXml);
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return;
        }

        if (apiRequest.first().equals(CycObjectFactory.makeCycSymbol("cyc-kill"))) {
            CycObjectFactory.removeCaches((CycConstant) apiRequest.second());
            if (verbosity > 2)
                System.out.println("killed cached version of " + (CycConstant) apiRequest.second());
        }

        boolean cycConnectionEnded = false;
        try {
            if (apiRequest.equals(CycObjectFactory.END_CYC_CONNECTION)) {
                if (verbosity > 0)
                    Log.current.println("ending cyc connection for " + senderName);
                    cycConnection.close();
                cycConnectionCache.remove(senderName);
                cycConnectionEnded = true;
                }
            }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }

        Object [] response = {null, null};
        if (cycConnectionEnded) {
            response[0] = Boolean.TRUE;
            response[1] = CycObjectFactory.nil;
        }
        else {
            try {
                if (verbosity == 0)
                    cycConnection.traceOff();
                else if (verbosity < 3)
                    cycConnection.traceOn();
                else
                    cycConnection.traceOnDetailed();
                response = cycConnection.converse(apiRequest);
            }
            catch (Exception e) {
                Log.current.errorPrintln(e.getMessage());
                Log.current.printStackTrace(e);
                return;
            }
        }
        ACL apiReplyAcl = (ACL) apiRequestAcl.clone();
        apiReplyAcl.setPerformative(FIPACONSTANTS.INFORM);
        apiReplyAcl.setSenderAID(apiRequestAcl.getReceiverAID());
        apiReplyAcl.setReceiverAID(apiRequestAcl.getSenderAID());
        CycList responseCycList = new CycList();
        if (response[0].equals(Boolean.TRUE))
            responseCycList.add(CycObjectFactory.t);
        else if (response[0].equals(Boolean.FALSE))
            responseCycList.add(CycObjectFactory.nil);
        else
            new RuntimeException("response[0] not Boolean " + response[0]);
        responseCycList.add(response[1]);
        try {
            apiReplyAcl.setContentObject("\n" + Marshaller.marshall(responseCycList));
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception while marshalling " + responseCycList);
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return;
        }
        apiReplyAcl.setReplyWith(null);
        apiReplyAcl.setInReplyTo(apiRequestAcl.getReplyWith());
        try {
            agentCommunityAdapter.sendMessage(apiReplyAcl);
        }
        catch (IOException e) {
            Log.current.errorPrintln("Exception " + e.getMessage() +
                                     "\nwhile replying to api request with\n" + apiReplyAcl);
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