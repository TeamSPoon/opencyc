package org.opencyc.xml;

/**
 * Prints RDF triples from DAML xml content.<p>
 * <p>
 * The Another RDF Parser (ARP) is used to parse the input DAML document.
 * This class implements statement callbacks from ARP. Each triple in the
 * input file causes a call on one of the statement methods.
 * The same triple may occur more than once in a file, causing repeat calls
 * to the method.
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

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import com.hp.hpl.jena.rdf.arp.*;

public class RDFTriples implements StatementHandler {

    /**
     * Another RDF Parser instance.
     */
    protected ARP arp;

    /**
     * Constructs a new RDFTriples object.
     */
    public RDFTriples () {
        arp = new ARP();
        arp.setStatementHandler(this);
    }

    /**
     * Starts an RDF/XML to RDFTriples converter.
     *
     * @param args the DAML URL to parse
     */
    static public void main(String args[]) {
        RDFTriples rdfTriples = new RDFTriples();
        rdfTriples.process(args[0]);
    }

    /**
     * Parses the given URL.
     *
     * @param surl the URL to parse into RDF triples
     */
    protected void process(String surl) {
        InputStream in;
        URL url;
        try {
            File ff = new File(surl);
            in = new FileInputStream(ff);
            url = ff.toURL();
        }
        catch (Exception ignore) {
            try {
                url = new URL(surl);
                in = url.openStream();
            }
            catch (Exception e) {
                System.err.println("ARP: Failed to open: " + surl);
                System.err.println("    " + ParseException.formatMessage(ignore));
                System.err.println("    " + ParseException.formatMessage(e));
                return;
            }
        }
        try {
            arp.load(in, url.toExternalForm());
        }
        catch (IOException e) {
            System.err.println("Error: " + surl + ": " + ParseException.formatMessage(e));
        }
        catch (SAXException sax) {
            System.err.println("Error: " + surl + ": " + ParseException.formatMessage(sax));
        }
    }

    /**
     * Provides the ARP statement handler for triple having an Object.
     *
     * @param subject the RDF Triple Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    public void statement(AResource subject, AResource predicate, AResource object) {
        resource(subject);
        resource(predicate);
        resource(object);
        System.out.println(".");
    }

    /**
     * Provides the ARP statement handler for triple having an Literal.
     *
     * @param subject the RDF Triple Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    public void statement(AResource subject, AResource predicate, ALiteral literal) {
        String lang = literal.getLang();
        String parseType = literal.getParseType();
        if (parseType != null) {
            System.out.print("# ");
            if (parseType != null)
                System.out.print("'" + parseType + "'");
            System.out.println();
        }
        resource(subject);
        resource(predicate);
        literal(literal);
        System.out.println();
    }

    /**
     * Displays the given RDF resource.
     *
     * @param resource the RDF resource to be displayed
     */
    static private void resource(AResource resource) {
        if (resource.isAnonymous())
            System.out.print("_:j" + resource.getAnonymousID() + " ");
        else {
            System.out.print("<");
            System.out.print(resource.getURI());
            System.out.print("> ");
        }
    }

    /**
     * Displays the given RDF literal.
     *
     * @param literal the RDF literal to be displayed
     */
    static private void literal(ALiteral literal) {
        if (literal.isWellFormedXML())
            System.out.print("xml");
        System.out.print("\"");
        System.out.print(literal.toString());
        System.out.print("\"");
        String lang = literal.getLang();
        if (lang != null && !lang.equals(""))
            System.out.print("-" + lang);
        System.out.print(" ");
    }

}