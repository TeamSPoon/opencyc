package  org.opencyc.jxta;

import  java.io.*;
import  java.util.Enumeration;
import  net.jxta.document.*;


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
     * The base from the original query.
     */
    private double base = 0.0;
    /**
     * The power from the original query.
     */
    private double power = 0.0;
    /**
     * The answer value for the response.
     */
    private double answer = 0;

    /**
     * Constructs a new CycApiResponseMsg object.
     */
    public CycApiResponseMsg () {
    }

    /**
     * Creates a response object using the given answer value.
     *
     * @param   anAnswer the answer for the response.
     */
    public CycApiResponseMsg (double aBase, double aPower, double anAnswer) {
        this.base = aBase;
        this.power = aPower;
        this.answer = anAnswer;
    }

    /**
     * Creates a response object by parsing the given input stream.
     *
     * @param   stream the InputStream source of the response data.
     */
    public CycApiResponseMsg (InputStream stream) throws Exception {
        StructuredTextDocument document = (StructuredTextDocument)StructuredDocumentFactory.newStructuredDocument(new MimeMediaType("text/xml"),
                stream);
        Enumeration elements = document.getChildren();
        while (elements.hasMoreElements()) {
            TextElement element = (TextElement)elements.nextElement();
            if (element.getName().equals("answer")) {
                answer = Double.valueOf(element.getTextValue()).doubleValue();
                continue;
            }
            if (element.getName().equals("base")) {
                base = Double.valueOf(element.getTextValue()).doubleValue();
                continue;
            }
            if (element.getName().equals("power")) {
                power = Double.valueOf(element.getTextValue()).doubleValue();
                continue;
            }
        }
    }

    /**
     * Returns the answer for the response.
     *
     * @return  the answer value for the response.
     */
    public double getAnswer () {
        return  answer;
    }

    /**
     * Returns the base for the query.
     *
     * @return  the base value for the query.
     */
    public double getBase () {
        return  base;
    }

    /**
     * Returns a Document representation of the response.
     *
     * @param   asMimeType the desired MIME type representation for the response.
     * @return  a Document form of the response in the specified MIME representation.
     */
    public Document getDocument (MimeMediaType asMimeType) throws Exception {
        Element element;
        StructuredDocument document = (StructuredTextDocument)StructuredDocumentFactory.newStructuredDocument(asMimeType, "example:ExampleResponse");
        element = document.createElement("base", Double.toString(getBase()));
        document.appendChild(element);
        element = document.createElement("power", Double.toString(getPower()));
        document.appendChild(element);
        element = document.createElement("answer", (new Double(getAnswer()).toString()));
        document.appendChild(element);
        return  document;
    }

    /**
     * Returns the power for the query.
     *
     * @return  the power value for the query.
     */
    public double getPower () {
        return  power;
    }

    /**
     * Returns an XML String representation of the response.
     *
     * @return  the XML String representing this response.
     */
    public String toString () {
        try {
            StringWriter buffer = new StringWriter();
            StructuredTextDocument document = (StructuredTextDocument)getDocument(new MimeMediaType("text/xml"));
            document.sendToWriter(buffer);
            return  buffer.toString();
        } catch (Exception e) {
            return  "";
        }
    }
}



