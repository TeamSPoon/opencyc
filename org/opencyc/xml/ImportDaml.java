package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import org.opencyc.util.*;

/**
 * Imports DAML xml content.<p>
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
public class ImportDaml implements StatementHandler {

    /**
     * The default verbosity of this application.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this application.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Another RDF Parser instance.
     */
    protected ARP arp;

    /**
     * The list of DAML web documents to import.
     */
    public static ArrayList documentsToImport = new ArrayList();

    /**
     * Ontology library nicknames, which become namespace identifiers
     * upon import into Cyc.
     */
    protected HashMap ontologyNicknames = new HashMap();

    /**
     * The current DAML Restriction object being constructed from sequential
     * RDF triples.
     */
    DamlRestriction damlRestriction;

    /**
     * Constructs a new ImportDaml object.
     */
    public ImportDaml() {
        arp = new ARP();
        arp.setStatementHandler(this);
    }

    /**
     * Provides the main method for the ImportDaml application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Log.makeLog();
        initializeDocumentsToImport();
        for (int i = 16; i < 17; i++) {
        //for (int i = 0; i < documentsToImport.size(); i++) {
        //for (int i = 0; i < 5; i++) {
            String damlPath = (String) documentsToImport.get(i);
            ImportDaml importDaml = new ImportDaml();
            importDaml.initialize();
            importDaml.importDaml(damlPath);
        }
    }

    /**
     * Initializes the documents to import.
     */
    protected static void initializeDocumentsToImport () {
        documentsToImport.add("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont.daml");
        documentsToImport.add("http://www.daml.org/2001/10/html/airport-ont.daml");
        documentsToImport.add("http://www.daml.org/2001/09/countries/fips.daml");
        documentsToImport.add("http://www.daml.org/2001/09/countries/fips-10-4.daml");
        documentsToImport.add("http://www.daml.org/2001/12/factbook/factbook-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/agency-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/CINC-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/af-a.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/assessment-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/economic-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/information-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/location-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/military-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/objectives-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/operation-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/political-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/social-elements-ont.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/example1.daml");
        documentsToImport.add("http://www.daml.org/experiment/ontology/example2.daml");
    }

    /**
     * Initializes the ImportDaml object.
     */
    protected void initialize () {
        initializeOntologyNicknames();
    }

    /**
     * Initializes the ImportDaml object.
     */
    protected void initializeOntologyNicknames () {
        ontologyNicknames.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
        ontologyNicknames.put("http://www.w3.org/2000/01/rdf-schema", "rdfs");
        ontologyNicknames.put("http://www.w3.org/2000/10/XMLSchema", "xsd");

        ontologyNicknames.put("http://www.daml.org/2001/03/daml+oil", "daml");

        ontologyNicknames.put("http://orlando.drc.com/daml/Ontology/daml-extension/3.2/daml-ext-ont", "daml-ext");

        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/factbook-ont.daml", "factbook");

        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont.daml", "ves");
        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont", "ves");

        ontologyNicknames.put("http://www.daml.org/2001/10/html/airport-ont.daml", "airport");

        ontologyNicknames.put("http://www.daml.org/2001/09/countries/fips-10-4-ont", "fips10-4");

        ontologyNicknames.put("http://www.daml.org/2001/09/countries/fips.daml", "fips");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/elements-ont.daml", "enp");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/elements-ont", "enp");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/objectives-ont.daml", "obj");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/objectives-ont", "obj");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/social-elements-ont.daml", "soci");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/social-elements-ont", "soci");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/political-elements-ont.daml", "poli");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/political-elements-ont", "poli");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/economic-elements-ont.daml", "econ");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/economic-elements-ont", "econ");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml", "infr");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/infrastructure-elements-ont", "infr");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/information-elements-ont.daml", "info");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/information-elements-ont", "info");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/military-elements-ont.daml", "mil");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/military-elements-ont", "mil");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/ona.xsd", "dt");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/location-ont.daml", "loc");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/location-ont", "loc");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/assessment-ont.daml", "assess");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/assessment-ont", "assess");

        ontologyNicknames.put("http://www.daml.org/2001/02/geofile/geofile-dt.xsd", "geodt");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/CINC-ont.daml", "cinc");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/CINC-ont", "cinc");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/cinc-ont", "cinc");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/agency-ont.daml", "agent");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/agency-ont", "agent");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/operation-ont.daml", "oper");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/operation-ont", "oper");
    }


    /**
     * Parses and imports the given DAML URL.
     *
     * @param damlPath the URL to import
     */
    protected void importDaml (String damlPath) {
        if (verbosity > 0)
            Log.current.println("\nImporting " + damlPath);
        Log.current.println("\nStatements\n");
        InputStream in;
        URL url;
        try {
            File ff = new File(damlPath);
            in = new FileInputStream(ff);
            url = ff.toURL();
        }
        catch (Exception ignore) {
            try {
                url = new URL(damlPath);
                in = url.openStream();
            }
            catch (Exception e) {
                System.err.println("ARP: Failed to open: " + damlPath);
                System.err.println("    " + ParseException.formatMessage(ignore));
                System.err.println("    " + ParseException.formatMessage(e));
                return;
            }
        }
        try {
            arp.load(in, url.toExternalForm());
        }
        catch (IOException e) {
            System.err.println("Error: " + damlPath + ": " + ParseException.formatMessage(e));
        }
        catch (SAXException sax) {
            System.err.println("Error: " + damlPath + ": " + ParseException.formatMessage(sax));
        }
        if (verbosity > 0)
            Log.current.println("\nDone importing " + damlPath + "\n");
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
        Log.current.println();
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
            Log.current.print("# ");
            if (parseType != null)
                Log.current.print("'" + parseType + "'");
            Log.current.println();
        }
        resource(subject);
        resource(predicate);
        literal(literal);
        Log.current.println();
    }

    /**
     * Displays the given RDF resource.
     *
     * @param aResource the RDF resource to be displayed
     */
    protected void resource(AResource aResource) {
        Resource resource = this.translate(aResource);
        if (aResource.isAnonymous())
            Log.current.print("anon-" + aResource.getAnonymousID() + " ");
        else {
            String localName = resource.getLocalName();
            String nameSpace = resource.getNameSpace();
            if (localName == null ||
                nameSpace == null) {
                Log.current.print(resource.toString() + " ");
            }
            else if (! hasUriNamespaceSyntax(aResource.getURI())) {
                Log.current.print(resource.toString() + " ");
            }
            else {
                String nickname = getOntologyNickname(nameSpace, resource);
                String constantName = nickname + ":" + localName;
                Log.current.print(constantName + " ");
            }
        }
    }

    /**
     * Displays the given RDF literal.
     *
     * @param literal the RDF literal to be displayed
     */
    static private void literal(ALiteral literal) {
        if (literal.isWellFormedXML())
            Log.current.print("xml");
        Log.current.print("\"");
        Log.current.print(literal.toString());
        Log.current.print("\"");
        String lang = literal.getLang();
        if (lang != null && !lang.equals(""))
            Log.current.print("-" + lang);
        Log.current.print(" ");
    }

    /**
     * Returns the ontology nickname for the given XML namespace.
     *
     * @param nameSpace the XML namespace for which the nickname is sought
     * @param resource the resource containing the namespace, used for error messages
     * @return the ontology nickname for the given XML namespace
     */
    protected String getOntologyNickname (String nameSpace, Resource resource) {
        int len = nameSpace.length() - 1;
        String key = nameSpace.substring(0, len);
        String nickname = (String) ontologyNicknames.get(key);
        if (nickname == null) {
            boolean ans = hasUriNamespaceSyntax(resource.getURI());
            throw new RuntimeException("Ontology nickname not found for " + key +
                                       "\nResource " + resource.toString());
        }
        return nickname;

    }

    /**
     * Returns true if the given URI has embedded XML namespace separators.
     *
     * @param uri the URI
     * @return true if the given URI has embedded XML namespace separators, otherwise
     * false
     */
    protected boolean hasUriNamespaceSyntax (String uri) {
        return (uri.indexOf(":", 6) > -1) || (uri.indexOf("#") > -1);
    }

    /**
     * Converts an ARP resource into a Jena resource.
     *
     * @param aResource The ARP resource.
     * @return The Jena resource.
     */
    static public Resource translate(AResource aResource) {
        if (aResource.isAnonymous()) {
            String id = aResource.getAnonymousID();
            Resource rr = (Resource) aResource.getUserData();
            if (rr == null) {
                rr = new ResourceImpl();
                aResource.setUserData(rr);
            }
            return rr;
        } else
            return new ResourceImpl(aResource.getURI());
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Records property use restrictions which get imported
     * as Cyc interArgIsa1-2 assertions.
     */
    protected class DamlRestriction {

        /**
         * Identifies all the RDF triples which contribute to this DAML
         * Restriction.
         */
        String anonymousId;

        /**
         * The domain (Cyc arg1) class whose intstances are the subject of the property.
         */
        String fromClass;

        /**
         * The property (Cyc predicate arg0) which relates the subject and predicate instances.
         */
        String property;

        /**
         * The range (Cyc arg2) classes whose instances may be objects of the property in the
         * cases where subject is an instance of the given fromClass.
         */
        ArrayList toClasses;

        /**
         * Returns a string representation of this object.
         *
         * @return a string representation of this object
         */
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("anon-");
            stringBuffer.append(anonymousId);
            stringBuffer.append(": ");
            stringBuffer.append(fromClass);
            stringBuffer.append(" (");
            stringBuffer.append(property);
            for (int i = 0; i < toClasses.size(); i++) {
                if (i > 0)
                    stringBuffer.append(", ");
                stringBuffer.append(toClasses.get(i).toString());
            }
            stringBuffer.append(")");
            return stringBuffer.toString();
        }
    }

}