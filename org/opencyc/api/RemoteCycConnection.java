package org.opencyc.api;

import  java.io.*;
import  javax.naming.TimeLimitExceededException;
import  fipaos.ont.fipa.*;
import  fipaos.ont.fipa.fipaman.*;
import  fipaos.util.*;
import  org.jdom.JDOMException;
import  org.opencyc.util.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.cycagent.*;
import  org.opencyc.cycagent.coabs.*;
import  org.opencyc.cycagent.fipaos.*;

/**
 * Provides remote access a binary connection and an ascii connection to the OpenCyc server.
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

public class RemoteCycConnection extends GenericAgent implements CycConnectionInterface {

    /**
     * No api trace.
     */
    public static final int API_TRACE_NONE = 0;

    /**
     * Message-level api trace.
     */
    public static final int API_TRACE_MESSAGES = 1;

    /**
     * Detailed api trace.
     */
    public static final int API_TRACE_DETAILED = 2;

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server.
     */
    protected int trace = API_TRACE_NONE;

    protected static final long thirtyMinutesDuration = 30 * 60 * 1000;

    /**
     * the interface to either the CoABS or FIPA-OS agent community
     */
    protected AgentCommunityAdapter agentCommunityAdapter;

    /**
     * Constructs a new RemoteCycConnection object to the given CycProxyAgent in the given
     * agent community.
     *
     * @param myAgentName the name of the local agent
     * @param remoteAgentName the name of the cyc proxy agent
     * @param agentCommunity the agent community to which the cyc proxy agent belongs
     */
    public RemoteCycConnection(String myAgentName,
                               String remoteAgentName,
                               int remoteAgentCommunity) throws IOException {
        super(myAgentName, remoteAgentCommunity, AgentCommunityAdapter.DEFAULT_VERBOSITY);
        if (remoteAgentCommunity == AgentCommunityAdapter.COABS_AGENT_COMMUNITY)
            agentCommunityAdapter = coAbsCommunityAdapter;
        else
            agentCommunityAdapter = fipaOsCommunityAdapter;
        super.remoteAgentName = remoteAgentName;
        super.initializeAgentCommunity();
    }

    /**
     * Send a message to Cyc and return the <tt>Boolean</tt> true as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element.  If an error occurs the first element is <tt>Boolean</tt>
     * false and the second element is the error message string.
     *
     * @param message the api command
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message) throws IOException, CycApiException {
        return converse(message, new Timer());
    }

    /**
     * Send a message to Cyc and return the response code as the first
     * element of an object array, and the cyc response Symbolic Expression as
     * the second element, spending no less time than the specified timer allows
     * but throwing a <code>TimeOutException</code> at the first opportunity
     * where that time limit is exceeded.
     * If an error occurs the second element is the error message string.
     *
     * @param message the api command which must be a String or a CycList
     * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
     * @return an array of two objects, the first is an Integer response code, and the second is the
     * response object or error string.
     */
    public Object[] converse (Object message, Timer timer)
        throws IOException, TimeOutException, CycApiException {
        if (trace > API_TRACE_NONE) {
            if (message instanceof String)
                System.out.println(message + " --> cyc");
            else if (message instanceof CycList)
                System.out.println(((CycList) message).cyclify());
            else
                throw new CycApiException("Invalid message class " + message);
            System.out.print("cyc --> ");
        }
        Object [] response = {null, null};

        ACL acl = new ACL();
        acl.setPerformative(FIPACONSTANTS.REQUEST);
        AgentID senderAid = new AgentID();
        senderAid.setName(myAgentName);
        acl.setSenderAID(senderAid);
        AgentID receiverAid = new AgentID();
        receiverAid.setName(remoteAgentName);
        acl.addReceiverAID(receiverAid);
        CycList apiRequest = null;
        String apiRequestXml;
        try {
            if (message instanceof String)
                apiRequest = CycAccess.sharedCycAccessInstance.makeCycList((String) message);
            else
                apiRequest = (CycList) message;
            apiRequestXml = "\n" + apiRequest.toXMLString();
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return response;
        }
        acl.setContentObject(apiRequestXml, ACL.BYTELENGTH_ENCODING);
        acl.setLanguage(FIPACONSTANTS.XML);
        acl.setOntology(AgentCommunityAdapter.CYC_API_ONTOLOGY);
        acl.setReplyWith(agentCommunityAdapter.nextMessageId());

        ACL replyAcl = null;
        try {
            replyAcl = agentCommunityAdapter.converseMessage(acl, timer);
        }
        catch (TimeLimitExceededException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            return response;
        }
        String contentXml = (String) replyAcl.getContentObject();
        CycList apiResponse = null;
        try {
            apiResponse = (CycList) CycObjectFactory.unmarshall(contentXml);
        }
        catch (JDOMException e) {
            throw new RuntimeException("JDOMException " + e.getMessage() + "\n" + contentXml);
        }
        if (apiResponse.size() != 2)
            throw new RuntimeException("Invalid api response " + apiResponse);
        response[0] = apiResponse.first();
        response[1] = apiResponse.second();

        if (trace > API_TRACE_NONE)
            if (response[1] instanceof CycList)
                System.out.println(response[0] + " " + ((CycList) response[1]).cyclify());
            else
                System.out.println(response[0] + " " + response[1]);
        if (response[0].equals(CycObjectFactory.t))
            response[0] = Boolean.TRUE;
        else if (response[0].equals(CycObjectFactory.nil))
            response[0] = Boolean.FALSE;
        else
            throw new RuntimeException("Invalid response[0] " + response[0]);
        return response;
    }

    /**
     * Close the api sockets and streams.
     */
    public void close () {
        agentCommunityAdapter.deregister();
    }

    /**
     * Returns the trace value.
     *
     * @return the trace value
     */
    public int getTrace() {
        return trace;
    }

    /**
     * Sets the trace value.
     * @param trace the trace value
     */
    public void setTrace(int trace) {
        this.trace = trace;
    }

    /**
     * Turns on the diagnostic trace of socket messages.
     */
    public void traceOn() {
        trace = API_TRACE_MESSAGES;
    }

    /**
     * Turns on the detailed diagnostic trace of socket messages.
     */
    public void traceOnDetailed() {
        trace = API_TRACE_DETAILED;
    }

    /**
     * Turns off the diagnostic trace of socket messages.
     */
    public void traceOff() {
        trace = API_TRACE_NONE;
    }

    /**
     * Returns connection information, suitable for diagnostics.
     *
     * @return connection information, suitable for diagnostics
     */
    public String connectionInfo () {
        return "cyc proxy agent " + remoteAgentName +
               ", agent community " + super.agentCommunityName();
    }


}