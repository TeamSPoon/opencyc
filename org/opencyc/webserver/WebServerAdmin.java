package  org.opencyc.webserver;

/**
 * Provides the behavior and attributes of a web server administrator for OpenCyc.<p>
 * <p>
 * Class WebServerAdmin implements the services provided by this agent.
 * It accepts client registrations for service and notification of events.  Clients
 * obtain leases via the registrations.
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

import  java.util.*;
import  java.io.*;
import  java.net.*;
import  java.rmi.*;
import  java.rmi.server.*;
import  javax.swing.*;
import  net.jini.admin.*;
import  net.jini.core.lease.*;
import  net.jini.lookup.entry.*;
import  net.jini.admin.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.util.*;


public class WebServerAdmin extends GenericService
        implements Remote, WebServerAdminInterface {
    /**
     * Reference to the web server being adminstered.
     */
    protected WebServer webServer;

    /**
     * Constructs a new WebServerAdmin object.
     *
     * @param serviceIcon the icon associated with the service.
     * @param nameAttribute the name of the service.
     * @param serviceInfo a standard attribute describing the service.
     * @exception RemoteException if a communications-related problem occurs during the
     * execution of a remote method call.
     */
    public WebServerAdmin (ImageIcon serviceIcon,
                           Name nameAttribute,
                           ServiceInfo serviceInfo,
                           WebServer webServer) throws RemoteException {
        super(serviceIcon, nameAttribute, serviceInfo);
        this.webServer = webServer;
    }

    /**
     * Provides the main method for the service agent.  A security manager is established.
     * An instance of the service is created and registered by the JoinManager with the Lookup Service.
     *
     * @param command line arguments for the service.
     */
    public static void makeWebServerAdmin (WebServer webServer) {
        initialize();
        // Name entry for LookupService, set the property on the command line.
        //Name nameAttribute = new Name(System.getProperty("org.opencyc.jini.service.name", "default service"));
        Name nameAttribute = null;
        try {
            nameAttribute = new Name(InetAddress.getLocalHost().getHostName() + " web server");
        }
        catch (java.net.UnknownHostException e) {
            nameAttribute = new Name("unknown web server");
        }
        // ServiceInfo entry for LookupService, clients match by name.
        String name = "WebServerAdmin Service";
        String manufacturer = "Cycorp Inc.";
        String vendor = "Cycorp Inc.";
        String version = "1.0";
        String model = "";
        String serialNumber = "";
        ServiceInfo serviceInfo = new ServiceInfo(name,
                                                  manufacturer,
                                                  vendor,
                                                  version,
                                                  model,
                                                  serialNumber);
        URL serviceIconURL = ClassLoader.getSystemResource("w.jpg");
        ImageIcon serviceIcon = new ImageIcon(serviceIconURL);
        WebServerAdmin service = null;
        try {
            service = new WebServerAdmin(serviceIcon, nameAttribute, serviceInfo, webServer);
        }
        catch (RemoteException e) {
            Log.current.errorPrintln("Error while creating service instance " + e);
        }
    }

    /**
     * Gets the list of directories from which files are served by the web server.
     *
     * @param lease the lease argument is provided by the client
     * @return The list of directories from which files are served by the web server.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public ArrayList getDirs () throws RemoteException {
        Log.current.println("Responding to client's request for current directories");
        return  webServer.getDirs();
    }

    /**
     * Updates the list of directories from which files are served by the web server.
     *
     * @param dirs The list of directories from which files are served by the web server.
     * @exception IOException if an file processing problem occurs.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public void setDirs (ArrayList dirs) throws RemoteException, IOException {
        Log.current.println("Responding to client's request to update directories");
        webServer.setDirs(dirs);
    }

    /**
     * Administrative accessor method that obtains the number of files served.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public long getNbrFilesServed () throws RemoteException {
        return  webServer.getNbrFilesServed();
    }

    /**
     * Administrative accessor method that obtains the number of files served from cache.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public long getNbrCacheHits () throws RemoteException {
        return  webServer.getNbrCacheHits();
    }

    /**
     * Administrative method that clears the file cache.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public synchronized void clearFileCache () throws RemoteException {
        webServer.clearFileCache();
    }
}



