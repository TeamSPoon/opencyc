package org.opencyc.soap;

import javax.xml.rpc.*;
import java.net.*;
import java.rmi.RemoteException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.opencyc.util.Log;

/**
 * Accesses the Cyc API as an XML SOAP client.<p>
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
public class CycSOAPClient {



    /**
     * Construct a new CycSOAPClient object.
     */
    public CycSOAPClient () {
    }

    /**
     * Provides the main method for the CycSOAPClient application.
     *
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        Log.makeLog("Cyc-SOAP-client.log");
        CycSOAPClient cycSOAPClient = new CycSOAPClient();
        try {
            cycSOAPClient.helloWorld();
            cycSOAPClient.remoteSubLInteractor();
        }
        catch( Exception e ) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Provides a simple test of the SOAP service without Cyc access.
     */
    protected void helloWorld ()
        throws ServiceException, MalformedURLException, RemoteException {
        String endpointURL = "http://localhost:9080/axis/CycSOAPService.jws";
        String methodName = "getHelloWorldMessage";
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(endpointURL));
        call.setOperationName(methodName);
        call.addParameter("name",
                          XMLType.XSD_STRING,
                          ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);
        String result = (String) call.invoke(new Object[] {"AXIS"});
        Log.current.println(result);
    }

    /**
     * Provides a remote SubL Interactor.
     */
    protected void remoteSubLInteractor ()
        throws ServiceException, MalformedURLException, RemoteException {
        String endpointURL = "http://localhost:8080/axis/CycSOAPService.jws";
        String methodName = "subLInteractor";
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(endpointURL));
        call.setOperationName(methodName);
        call.addParameter("name",
                          XMLType.XSD_STRING,
                          ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);
        String result = (String) call.invoke(new Object[] {"(isa #$TransportationDevice)"});
        Log.current.println(result);
    }
}



