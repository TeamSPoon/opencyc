package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.xml.sax.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import ViolinStrings.Strings;

/**
 * Gathers Open Directory Titles and constructs a dictionary associating
 * topic resource IDs with their titles.<p>
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
public class GatherOpenDirectoryTitles implements StatementHandler {

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
     * Cyc terms which have semantic counterparts in DAML.
     * DAML term --> Cyc term
     */
    protected static HashMap equivalentDamlCycTerms;

    /**
     * Ontology library nicknames, which become namespace identifiers
     * upon import into Cyc.
     * namespace uri --> ontologyNickname
     */
    protected HashMap ontologyNicknames;

    /**
     * Previously imported term used to avoid redundant assertions.
     */
    protected DamlTermInfo previousDamlTermInfo = null;

    /**
     * URL string which defines the imported daml ontology
     */
    protected String damlOntologyDefiningURLString;

    /**
     * URL which defines the imported daml ontology
     */
    protected CycFort damlOntologyDefiningURL;

    /**
     * Constructs a new GatherOpenDirectoryTitles object.
     *
     * @param cycAccess the CycAccess instance which manages the connection
     * to the Cyc server and provides Cyc API services
     * @param ontologyNicknames the dictionary associating each ontology uri with
     * the nickname used for the Cyc namespace qualifier
     * @param kbSubsetCollectionName the name of the Cyc KbSubsetCollection
     * which identifies each of the imported terms
     */
    public GatherOpenDirectoryTitles(HashMap ontologyNicknames,
                                     HashMap equivalentDamlCycTerms) {
        this.ontologyNicknames = ontologyNicknames;
        this.equivalentDamlCycTerms = equivalentDamlCycTerms;
        arp = new ARP();
        arp.setStatementHandler(this);
    }

    /**
     * Parses and imports the given DAML URL.
     *
     * @param damlOntologyDefiningURLString the URL to import
     * @param importMtName the microtheory into which DAML content is asserted
     */
    protected void gatherTitles (String damlOntologyDefiningURLString)
        throws IOException, CycApiException {
        this.damlOntologyDefiningURLString = damlOntologyDefiningURLString;
        if (verbosity > 0)
            Log.current.println("\nGathering titles from " + damlOntologyDefiningURLString);
        InputStream in;
        URL url;
        try {
            File ff = new File(damlOntologyDefiningURLString);
            in = new FileInputStream(ff);
            url = ff.toURL();
        }
        catch (Exception ignore) {
            try {
                url = new URL(damlOntologyDefiningURLString);
                in = url.openStream();
            }
            catch (Exception e) {
                System.err.println("ARP: Failed to open: " + damlOntologyDefiningURLString);
                System.err.println("    " + ParseException.formatMessage(ignore));
                System.err.println("    " + ParseException.formatMessage(e));
                return;
            }
        }
        try {
            arp.load(in, url.toExternalForm());
        }
        catch (IOException e) {
            System.err.println("Error: " + damlOntologyDefiningURLString + ": " + ParseException.formatMessage(e));
        }
        catch (SAXException sax) {
            System.err.println("Error: " + damlOntologyDefiningURLString + ": " + ParseException.formatMessage(sax));
        }
        if (verbosity > 0)
            Log.current.println("\nDone gathering titles from " + damlOntologyDefiningURLString + "\n");
    }

    /**
     * Provides the ARP statement handler for triple having an Object.
     *
     * @param subject the RDF Triple Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    public void statement(AResource subject, AResource predicate, AResource object) {
        try {
            if (subject.isAnonymous()) {
                return;
            }
            if (object.isAnonymous()) {
                return;
            }
            DamlTermInfo subjectTermInfo = resource(subject, null);
            DamlTermInfo predicateTermInfo = resource(predicate, null);
            DamlTermInfo objectTermInfo = resource(object, predicateTermInfo);
            displayTriple(subjectTermInfo,
                          predicateTermInfo,
                          objectTermInfo);
            importTriple(subjectTermInfo,
                         predicateTermInfo,
                         objectTermInfo);
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Provides the ARP statement handler for triple having an Literal.
     *
     * @param subject the RDF Triple Subject
     * @param predicate the RDF Triple Predicate
     * @param literal the RDF Triple Literal
     */
    public void statement(AResource subject, AResource predicate, ALiteral literal) {
        try {
            if (! subject.isAnonymous()) {
                DamlTermInfo subjectTermInfo = resource(subject, null);
                DamlTermInfo predicateTermInfo = resource(predicate, null);
                DamlTermInfo literalTermInfo = literal(literal);
                displayTriple(subjectTermInfo,
                              predicateTermInfo,
                              literalTermInfo);

                importTriple(subjectTermInfo,
                             predicateTermInfo,
                             literalTermInfo);
            }
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Imports the RDF triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param predicateTermInfo the predicate DamlTermInfo object
     * @param objLitTermInfo the object or literal DamlTermInfo object
     */
    protected void importTriple (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo predicateTermInfo,
                                  DamlTermInfo objLitTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        if (predicateTermInfo.isURI)
            predicateTermInfo.coerceToNamespace();
        String damlPredicate = predicateTermInfo.toString();
        if (! subjectTermInfo.hasEquivalentCycTerm()) {
            if (damlPredicate.equals("isa")) {




                return;
            }
            if (damlPredicate.equals("nameString")) {






                return;
            }
        }
    }

    /**
     * Returns the given string argument with embedded double quote characters
     * escaped.
     *
     * @param string the given string
     * @return the given string argument with embedded double quote characters
     * escaped
     */
    protected String escaped (String text) {
        String result = Strings.change(text, "\"", "\\\"");
        result = Strings.change(result, "\n", " ");
        result = Strings.change(result, "\r", " ");
        return result;
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
        stringBuffer.append(subjectTermInfo.toOriginalString());
        stringBuffer.append(" ");
        stringBuffer.append(predicateTermInfo.toOriginalString());
        stringBuffer.append(" ");
        stringBuffer.append(objLitTermInfo.toOriginalString());
        Log.current.println(stringBuffer.toString());
    }

    /**
     * Returns the DamlTerm info of the given RDF resource.
     *
     * @param aResource the RDF resource
     * @param predicateTermInfo when processing the RDF triple object,
     * contains the predicate term info, otherwise is null;
     * @return the DamlTerm info of the given RDF resource
     */
    protected DamlTermInfo resource(AResource aResource,
                                    DamlTermInfo predicateTermInfo) {
        DamlTermInfo damlTermInfo = new DamlTermInfo(this);
        String localName;
        String nameSpace;
        Resource resource = translateResource(aResource);
        damlTermInfo.uri = resource.toString();
        if (aResource.isAnonymous()) {
            damlTermInfo.isAnonymous = true;
            damlTermInfo.anonymousId = aResource.getAnonymousID();
            return damlTermInfo;
        }
        else if (aResource.getURI().indexOf("?") > -1) {
            damlTermInfo.isURI = false;
            int index = aResource.getURI().indexOf("?");
            localName = aResource.getURI().substring(index + 1);
            nameSpace = aResource.getURI().substring(0, index + 1);
        }
        else if (! hasUriNamespaceSyntax(aResource.getURI())) {
            damlTermInfo.isURI = true;
            if (! damlTermInfo.mustBeUri(predicateTermInfo))
                damlTermInfo.coerceToNamespace();
            return damlTermInfo;
        }
        else {
            localName = resource.getLocalName();
            nameSpace = resource.getNameSpace();
        }
        if (localName == null ||
            nameSpace == null)
            throw new RuntimeException("Invalid nameSpace " + nameSpace +
                                       " localName " + localName +
                                       " for resource " + resource.toString());
        damlTermInfo.localName = localName;
        damlTermInfo.nameSpace = nameSpace;
        String ontologyNickname = getOntologyNickname(nameSpace, resource);
        damlTermInfo.ontologyNickname = ontologyNickname;
        String constantName = ontologyNickname + ":" + localName;
        damlTermInfo.constantName = constantName;
        if (equivalentDamlCycTerms.containsKey(constantName))
            damlTermInfo.equivalentDamlCycTerm =
                (String) equivalentDamlCycTerms.get(constantName);
        else if (ontologyNickname.equals("xsd"))
            Log.current.println("\n*** unhandled primitive datatype: " + constantName + "\n");
        return damlTermInfo;
    }

    /**
     * Returns the DamlTerm info of the given RDF literal.
     *
     * @param literal the RDF literal
     * @return the DamlTerm info of the given RDF literal
     */
    protected DamlTermInfo literal(ALiteral literal) {
        DamlTermInfo damlTermInfo = new DamlTermInfo(this);
        String literalString = literal.toString();
        if (this.isProbableUri(literalString)) {
            damlTermInfo.isURI = true;
            damlTermInfo.uri = literalString;
        }
        else {
            damlTermInfo.isLiteral = true;
            damlTermInfo.literal = literalString;
        }
        return damlTermInfo;
    }

    /**
     * Returns true if the given string is likely to be a URI.
     *
     * @param string the given string
     * @return true if the given string is likely to be a URI
     */
    protected boolean isProbableUri (String string) {
        return
            string.startsWith("http://") ||
            string.startsWith("https://") ||
            string.startsWith("ftp://") ||
            string.startsWith("file:/") ||
            string.startsWith("urn:");
    }

    /**
     * Returns true if the given URI has embedded XML namespace separators.
     *
     * @param uri the URI
     * @return true if the given URI has embedded XML namespace separators, otherwise
     * false
     */
    protected boolean hasUriNamespaceSyntax (String uri) {
        return (uri.indexOf(":", 9) > -1) || (uri.indexOf("#") > -1);
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
            Log.current.println("\n*** Ontology nickname not found for " + key +
                                "\nResource " + resource.toString());
            nickname = "unknown";
        }
        return nickname;
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
        GatherOpenDirectoryTitles parent;
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
        String equivalentDamlCycTerm;
        CycFort cycFort;

        /**
         * Constructs a new DamlTermInfo object.
         */
        public DamlTermInfo(GatherOpenDirectoryTitles parent) {
            this.parent = parent;
        }

        /**
         * Returns <tt>true</tt> some object equals this object
         *
         * @param object the <tt>Object</tt> for equality comparison
         * @return equals <tt>boolean</tt> value indicating equality or non-equality.
         */
        public boolean equals(Object object) {
            if (! (object instanceof DamlTermInfo))
                return false;
            return this.toString().equals(object.toString());
        }

        /**
         * Returns a non-substituted string representation of this object.
         *
         * @return a non-substituted string representation of this object
         */
        public String toOriginalString() {
            if (equivalentDamlCycTerm == null)
                return this.toString();
            else
                return constantName;
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
            else if (equivalentDamlCycTerm != null)
                return equivalentDamlCycTerm;
            else
                return constantName;
        }

        /**
         * Returns the literal value of this object.
         *
         * @return the literal value of this object
         */
        public String literalValue() {
            if (isLiteral)
                return literal;
            else
                throw new RuntimeException(this.toString() + " is not a literal");
        }

        /**
         * Coerces a namespace:localname from the URI.
         * For example, http://xmlns.com/foaf/0.1/Person -->
         * http://xmlns.com/foaf/0.1#Person
         */
        public void coerceToNamespace() {
            int index = uri.lastIndexOf("/");
            nameSpace = uri.substring(0, index);
            localName = uri.substring(index + 1);
            if (localName.equals(""))
                return;
            ontologyNickname = (String) ontologyNicknames.get(nameSpace);
            if (ontologyNickname == null) {
                Log.current.println("*** nickname not found for " + nameSpace +
                                    "\nuri " + uri);
                ontologyNickname = "unknown";
            }
            constantName = ontologyNickname + ":" + localName;
            isURI = false;
        }

        /**
         * Returns true if the uri does not represent an RDF
         * object.  Heuristic patterns are used.
         *
         * @param predicateTermInfo when present indicates that this
         * is the object of the RDF triple
         * @return true if the uri does not represent an RDF
         * object
         */
        public boolean mustBeUri(DamlTermInfo predicateTermInfo) {
            if (predicateTermInfo != null) {
                if (predicateTermInfo.toString().equals("daml:imports") ||
                    predicateTermInfo.toString().equals("rdfs:isDefinedBy") ||
                    predicateTermInfo.toString().equals("rdfs:seeAlso"))
                    return true;
            }
            if (parent.ontologyNicknames.containsKey(uri))
                return true;
            if (uri.endsWith(".daml") ||
                uri.startsWith("news:") ||
                (uri.indexOf("daml+oil") > -1))
                return true;
            return false;
        }

        /**
         * Returns true if this term has an equivalent existing Cyc term.
         *
         * @return true if this term has an equivalent existing Cyc term
         */
        public boolean hasEquivalentCycTerm () {
            return equivalentDamlCycTerm != null;
        }
    }




}