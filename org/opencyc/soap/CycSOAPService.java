package org.opencyc.soap;

import javax.xml.rpc.*;
import java.net.*;
import java.rmi.RemoteException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Provides the Cyc API as an XML SOAP service.<p>
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
public class CycSOAPService {

/*

    Required jars:

    jakarta-oro-2.0.3.jar
    jug.jar
    ViolinStrings.jar


*/
    protected static CycAccess cycAccess = null;

    /**
     * Construct a new CycSOAPService object.
     */
    public CycSOAPService() {
    }

    /**
     * Provides a simple message to test the CycSOAPService without accessing
     * the Cyc server.
     */
    public String getHelloWorldMessage(String name) {
        return "Hello World to " + name;
    }

    /**
     * Provides a SubL interactor service.
     *
     * @param subLRequest the given SubL request which will be submitted to the
     * Cyc server for evaluation
     * @return the result of evaluating the given SubL request
     */
    public String subLInteractor(String subLRequest) {
        try {
            if (cycAccess == null) {
                String localHostName = InetAddress.getLocalHost().getHostName();
                if (localHostName.equals("crapgame.cyc.com")) {
                    cycAccess = new CycAccess("localhost",
                            3620,
                            CycConnection.DEFAULT_COMMUNICATION_MODE,
                            true);
                }
                else if (localHostName.equals("thinker")) {
                    cycAccess = new CycAccess("localhost",
                            3600,
                            CycConnection.DEFAULT_COMMUNICATION_MODE,
                            true);
                }
                else
                    cycAccess = new CycAccess();
            }
            CycList request = cycAccess.makeCycList(subLRequest);
            Object response = null;
            try {
                response = cycAccess.converseObject(request);
            }
            catch (CycApiException e) {
                return e.getMessage();
            }
            if (response instanceof CycList)
                return ((CycList) response).cyclify();
            else if (response instanceof CycFort)
                return ((CycFort) response).cyclify();
            else
                return response.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

}