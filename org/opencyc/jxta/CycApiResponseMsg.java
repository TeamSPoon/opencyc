package  org.opencyc.jxta;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;

import org.jdom.JDOMException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycList;

import fipaos.ont.fipa.ACL;

/**
 * Provides a container for the JXTA Cyc api response.<p>
 *
 * See www.jxta.org for more information on the Juxtaposition peer to peer
 * infrastructure.  OpenCyc uses the JXTA Resolver Service to pass messages to
 * discovered JXTA peers.  Between OpenCyc peers, the message content is a
 * FIPA-OS envelope and an enclosed FIPA-OS message represented in XML format.
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
public class CycApiResponseMsg {
    /**
     * the Agent Communication Language response
     */
    public ACL responseAcl;

    /**
     * the Agent Communication Language response
     */
    public CycList response;

    /**
     * Constructs a new CycApiResponseMsg object.
     */
    public CycApiResponseMsg () {
    }

    /**
     * Creates a response object using the Agent Communication Language response.
     *
     * @param   responseAcl the Agent Communication Language response
     */
    public CycApiResponseMsg (ACL responseAcl) {
        this.responseAcl = responseAcl;
        String contentXml = (String) responseAcl.getContentObject();
        try {
			response = (CycList) CycObjectFactory.unmarshall(contentXml);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Creates a response object by parsing the given input stream.
     *
     * @param   stream the InputStream source of the response data.
     */
    public CycApiResponseMsg (InputStream stream) throws Exception {
        StructuredTextDocument document =
            (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(
                new MimeMediaType("text/xml"),
                stream);
        Enumeration elements = document.getChildren();
        while (elements.hasMoreElements()) {
            TextElement element = (TextElement)elements.nextElement();
            if (element.getName().equals("acl")) {
                responseAcl = new ACL(element.getTextValue());
                String contentXml = (String) responseAcl.getContentObject();
                response = (CycList) CycObjectFactory.unmarshall(contentXml);
                break;
            }
        }
    }

    /**
     * Returns an XML String representation of the response.
     *
     * @return  the XML String representing this response.
     */
    public String toString () {
        return responseAcl.toString();
    }
}







