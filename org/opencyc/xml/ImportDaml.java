package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
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
     * Ontology library nicknames, which become namespace identifiers
     * upon import into Cyc.
     * namespace uri --> ontologyNickname
     */
    protected HashMap ontologyNicknames;

    /**
     * Ontology import microtheory name.
     */
    protected String importMt;

    /**
     * The current DAML Restriction object being constructed from sequential
     * RDF triples.
     */
    protected DamlRestriction damlRestriction;

    /**
     * CycAccess object to manage api connection the the Cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * The name of the KB Subset collection which identifies ontology import
     * terms in Cyc.
     */
    protected String kbSubsetCollectionName;

    /**
     * Constructs a new ImportDaml object.
     *
     * @param cycAccess the CycAccess instance which manages the connection
     * to the Cyc server and provides Cyc API services
     * @param ontologyNicknames the dictionary associating each ontology uri with
     * the nickname used for the Cyc namespace qualifier
     * @param kbSubsetCollectionName the name of the Cyc KbSubsetCollection
     * which identifies each of the imported terms
     */
    public ImportDaml(CycAccess cycAccess,
                      HashMap ontologyNicknames,
                      String kbSubsetCollectionName) {
        this.cycAccess = cycAccess;
        this.ontologyNicknames = ontologyNicknames;
        this.kbSubsetCollectionName = kbSubsetCollectionName;
        arp = new ARP();
        arp.setStatementHandler(this);
    }

    /**
     * Initializes the ImportDaml object.
     */
    protected void initialize ()
        throws IOException, UnknownHostException, CycApiException {
    }

    /**
     * Parses and imports the given DAML URL.
     *
     * @param damlPath the URL to import
     * @param importMt the microtheory into which DAML content is asserted
     */
    protected void importDaml (String damlPath,
                               String importMt)
        throws IOException, CycApiException {
        this.importMt = importMt;
        if (verbosity > 0)
            Log.current.println("\nImporting " + damlPath + "\ninto " + importMt);
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
        if (subject.isAnonymous())
            processRestrictionSubject(subject, predicate, object);
        else if (object.isAnonymous())
            processRestrictionObject(subject, predicate, object);
        else {
            DamlTermInfo subjectTermInfo = resource(subject);
            DamlTermInfo predicateTermInfo = resource(predicate);
            DamlTermInfo objectTermInfo = resource(object);

            displayTriple(subjectTermInfo,
                          predicateTermInfo,
                          objectTermInfo);
        }
        Log.current.println();
    }

    /**
     * Provides the ARP statement handler for triple having an Literal.
     *
     * @param subject the RDF Triple Subject
     * @param predicate the RDF Triple Predicate
     * @param literal the RDF Triple Literal
     */
    public void statement(AResource subject, AResource predicate, ALiteral literal) {
        if (subject.isAnonymous())
            processRestrictionSubject(subject, predicate, literal);
        else {
            DamlTermInfo subjectTermInfo = resource(subject);
            DamlTermInfo predicateTermInfo = resource(predicate);
            DamlTermInfo literalTermInfo = literal(literal);

            displayTriple(subjectTermInfo,
                          predicateTermInfo,
                          literalTermInfo);
        }
        Log.current.println();
    }

    /**
     * Displays the RDF triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param predicateTermInfo the predicate DamlTermInfo object
     * @param objLitTermInfo the object or literal DamlTermInfo object
     */
    protected void displayTriple (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo predicateTermInfo,
                                  DamlTermInfo objLitTermInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(subjectTermInfo.toString());
        stringBuffer.append(" ");
        stringBuffer.append(predicateTermInfo.toString());
        stringBuffer.append(" ");
        stringBuffer.append(objLitTermInfo.toString());
        Log.current.print(stringBuffer.toString());
    }

    /**
     * Returns the DamlTerm info of the given RDF resource.
     *
     * @param aResource the RDF resource
     * @return the DamlTerm info of the given RDF resource
     */
    protected DamlTermInfo resource(AResource aResource) {
        DamlTermInfo damlTermInfo = new DamlTermInfo();
        Resource resource = translateResource(aResource);
        if (aResource.isAnonymous()) {
            damlTermInfo.isAnonymous = true;
            damlTermInfo.anonymousId = aResource.getAnonymousID();
        }
        else if (! hasUriNamespaceSyntax(aResource.getURI())) {
            damlTermInfo.isURI = true;
            damlTermInfo.uri = resource.toString();
        }
        else {
            String localName = resource.getLocalName();
            damlTermInfo.localName = localName;
            String nameSpace = resource.getNameSpace();
            damlTermInfo.nameSpace = nameSpace;
            if (localName == null ||
                nameSpace == null)
                throw new RuntimeException("Invalid nameSpace " + nameSpace +
                                           " localName " + localName +
                                           " for resource " + resource.toString());
            String ontologyNickname = getOntologyNickname(nameSpace, resource);
            damlTermInfo.ontologyNickname = ontologyNickname;
            String constantName = ontologyNickname + ":" + localName;
            damlTermInfo.constantName = constantName;
        }
        return damlTermInfo;
    }

    /**
     * Returns the DamlTerm info of the given RDF literal.
     *
     * @param literal the RDF literal
     * @return the DamlTerm info of the given RDF literal
     */
    protected DamlTermInfo literal(ALiteral literal) {
        DamlTermInfo damlTermInfo = new DamlTermInfo();
        damlTermInfo.isLiteral = true;
        damlTermInfo.literal = literal.toString();
        return damlTermInfo;
    }

    /**
     * Records the components of a DAML Restriction.
     *
     * @param subject the RDF Triple anonymous Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    protected void processRestrictionSubject(AResource subject,
                                             AResource predicate,
                                             AResource object) {
        DamlTermInfo subjectTermInfo = resource(subject);
        DamlTermInfo predicateTermInfo = resource(predicate);
        DamlTermInfo objectTermInfo = resource(object);

        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      objectTermInfo);
    }

    /**
     * Records the components of a DAML Restriction.
     *
     * @param subject the RDF Triple anonymous Subject
     * @param predicate the RDF Triple Predicate
     * @param literal the RDF Triple Literal
     */
    protected void processRestrictionSubject(AResource subject,
                                            AResource predicate,
                                            ALiteral literal) {
        DamlTermInfo subjectTermInfo = resource(subject);
        DamlTermInfo predicateTermInfo = resource(predicate);
        DamlTermInfo literalTermInfo = literal(literal);

        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      literalTermInfo);
    }

    /**
     * Records the components of a DAML Restriction.
     *
     * @param subject the RDF Triple anonymous Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    protected void processRestrictionObject(AResource subject,
                                            AResource predicate,
                                            AResource object) {
        DamlTermInfo subjectTermInfo = resource(subject);
        DamlTermInfo predicateTermInfo = resource(predicate);
        DamlTermInfo objectTermInfo = resource(object);

        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      objectTermInfo);
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
    protected Resource translateResource(AResource aResource) {
        if (aResource.isAnonymous()) {
            String id = aResource.getAnonymousID();
            Resource rr = (Resource) aResource.getUserData();
            if (rr == null) {
                rr = new ResourceImpl();
                aResource.setUserData(rr);
            }
            return rr;
        }
        else
            return new ResourceImpl(aResource.getURI());
    }

    /**
     * Assert that the given term is an instance of the DamlSonatConstant KB
     * subset collection.
     *
     * @param cycConstantName the name of the Cyc constant
     */
    protected void assertIsaDamlSonatConstant (String cycConstantName)
        throws IOException, CycApiException {
        cycAccess.assertIsa(cycConstantName, kbSubsetCollectionName, "BookkeepingMt");
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
     * Records the DAML term information for Cyc import.
     */
    protected class DamlTermInfo {
        boolean isAnonymous = false;
        boolean isURI = false;
        boolean isLiteral = false;
        String anonymousId;
        String nameSpace;
        String ontologyNickname;
        String localName;
        String constantName;
        String uri;
        String literal;

        /**
         * Constructs a new DamlTermInfo object.
         */
        public DamlTermInfo() {
        }

        /**
         * Returns a string representation of this object.
         *
         * @return a string representation of this object
         */
        public String toString() {
            if (isAnonymous)
                return "anon-" + anonymousId;
            else if (isURI)
                return uri;
            else if (isLiteral)
                return "\"" + literal + "\"";
            else
                return constantName;
        }
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