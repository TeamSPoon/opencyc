package  org.opencyc.jini.cycproxy;

/**
 * Provides an agent for OpenCyc using services provided by agents on
 * the jini network and on the CoABS grid.<p>
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
import  java.io.*;
import  java.net.*;
import  java.rmi.*;
import  java.rmi.server.*;
import  java.util.*;
import  javax.swing.*;
import  net.jini.core.lease.*;
import  net.jini.lookup.entry.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;

public class CycProxy extends GenericService
        implements Remote, CycApiServiceInterface, GenericServiceInterface {

    /**
     * CycAccess object which manages the connection to the Cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * Constructs a new CycProxy object.  This instance corresponds to one Cyc image on the Grid and
     * on the jini agent network.
     *
     * @param serviceIcon the icon associated with the service.
     * @param nameAttribute the name of the service.
     * @param serviceInfo a standard attribute describing the service.
     * @param basePort the base (HTML serving) TCP socket port number
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public CycProxy (ImageIcon serviceIcon,
                     Name nameAttribute,
                     ServiceInfo serviceInfo,
                     int basePort) throws RemoteException, IOException, CycApiException {
        super(serviceIcon, nameAttribute, serviceInfo);
        cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                  basePort,
                                  CycConnection.DEFAULT_COMMUNICATION_MODE,
                                  CycAccess.DEFAULT_CONNECTION);
    }

    /**
     * Makes a CycProxy object.
     *
     * @param agentName the name of this OpenCyc image
     * @param iconPath the filename of the icon used to represent this agent in the admin explorer
     * @param localClientItem the container for this agent's attributes
     * @return a CycProxy object
     */
    public static CycProxy makeCycProxy (String agentName, String iconPath, int basePort) {
        // Name entry for LookupService.
        Name nameAttribute = new Name(agentName);
        // ServiceInfo entry for LookupService.
        String name = "Cyc Proxy";
        String manufacturer = "Cycorp Inc.";
        String vendor = "Cycorp Inc.";
        String version = "1.0";
        String model = "";
        String serialNumber = "";
        ServiceInfo serviceInfo = new ServiceInfo(name, manufacturer, vendor, version, model, serialNumber);
        ImageIcon serviceIcon = new ImageIcon(iconPath);
        CycProxy agent = null;
        try {
            agent = new CycProxy(serviceIcon, nameAttribute, serviceInfo, basePort);
        } catch (Exception e) {
            Log.current.errorPrintln("Error while creating service instance " + e);
        }
        return  agent;
    }

    /**
     * Handles an AMP (Agent Message Protocol) incoming message from the Grid or CoABS Grid.
     *
     * @param message the incoming api function request
     * @return the response message resulting from the api request
     * @exception CycApiException if an error occurs during the Cyc API call.
     */
    public Amp ampMessageReceived (Amp message) throws RemoteException, CycApiException {
        Log.current.println("Received AMP " + message);
        Amp replyMessage = new Amp();
        replyMessage.setSender(message.receiver());
        replyMessage.setReceiver(message.sender());
        replyMessage.setInReplyTo(message.content());
        replyMessage.setLanguage(message.language());
        replyMessage.setOntology(message.ontology());
        try {
            if (message.ontology().equalsIgnoreCase("CYC-API"))
                replyMessage.setContent(cycApiRequestInternal(message.content()));
        }
        catch (IOException e) {
            throw new CycApiException("IOException: " + e.getMessage());
        }
        Log.current.println("Returned AMP " + replyMessage);
        return  replyMessage;
    }

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
    public String cycApiRequest (String apiRequest) throws RemoteException, IOException, CycApiException {
        Log.current.println("Responding to an API request");
        return  cycApiRequestInternal(apiRequest);
    }

    /**
     * Sends the API request to Cyc.
     * @param lease the lease on this service maintained by the client.
     *
     * @param apiRequest the API request form for Cyc to evaluate.
     * @return The response by Cyc to this API request.
     * @exception LeaseDeniedException if a lease request or renewal is denied.
     * @exception RemoteException if a communications related problem occurs
     * during a remote method call.
     * @exception CycApiException if an error occurs during the Cyc API call.
     *
     */
    public String cycApiRequest (Lease lease, String apiRequest)
        throws RemoteException, LeaseDeniedException, IOException, CycApiException {
        Log.current.println("Responding to client's request");
        validateServiceLease(lease);
        return  cycApiRequestInternal(apiRequest);
    }

    /**
     * Sends the API request to Cyc.
     *
     * @param apiRequest the API request form for Cyc to evaluate.
     * @return The response by Cyc to this API request.
     * @exception RemoteException if a communications related problem occurs
     * during a remote method call.
     */
    protected String cycApiRequestInternal (String apiRequest)
        throws RemoteException, IOException, CycApiException {
        Log.current.println("Request " + apiRequest);
        Object apiResponse =  cycAccess.converseObject(apiRequest);
        if (apiResponse instanceof CycList)
            return ((CycList) apiResponse).cyclify();
        else if (apiResponse instanceof CycConstant)
            return ((CycConstant) apiResponse).cyclify();
        else if (apiResponse instanceof CycNart)
            return ((CycNart) apiResponse).cyclify();
        else
            return apiResponse.toString();
    }

    /**
     * Routes a message to a Cyc agent.
     */
    public void routeToCycAgent (Amp amp) throws CycApiException, IOException {
        CycList command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("grid-to-cyc"));
        CycList command1 = new CycList(amp.toString());
        command.add(command1);
        cycAccess.converseVoid(command);
    }
}



