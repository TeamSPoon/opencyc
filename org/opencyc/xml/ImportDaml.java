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
 * Abstract class to provide common functions for importing DAML xml content.
 * Subclasses will provide additional behavior tailored for the particular
 * DAML imports.<p>
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
public abstract class ImportDaml implements StatementHandler {

    /**
     * The default verbosity of this application.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * When T, indicates that the import is performed othwise when false
     * indicates that the DAML document should be parsed but not imported.
     */
    public boolean actuallyImport = true;

    /**
     * Sets verbosity of this application.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public int verbosity = DEFAULT_VERBOSITY;

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
     * Ontology import microtheory name.
     */
    protected String importMtName;

    /**
     * Ontology import microtheory.
     */
    protected CycConstant importMt;

    /**
     * The current DAML Restriction object being constructed from sequential
     * RDF triples.
     */
    protected DamlRestriction damlRestriction;

    /**
     * CycAccess object to manage api connection the the Cyc server.
     */
    public CycAccess cycAccess;

    /**
     * The name of the KB Subset collection which identifies ontology import
     * terms in Cyc.
     */
    public String kbSubsetCollectionName;

    /**
     * The KB Subset collection which identifies ontology import
     * terms in Cyc.
     */
    protected CycConstant kbSubsetCollection;

    /**
     * The #$BookkeepingMt.
     */
    protected CycConstant bookkeepingMt;

    /**
     * Character encoding scheme of the DAML input (e.g. UTF-8), specified
     * if not ASCII.
     */
    protected String characterEncoding = null;

    /**
     * Constructs a new ImportDaml object.
     */
    public ImportDaml() {
        arp = new ARP();
        arp.setStatementHandler(this);
    }

    /**
     * Initializes the ImportDaml object.
     */
    public void initialize ()
        throws IOException, UnknownHostException, CycApiException {
        kbSubsetCollection = cycAccess.getKnownConstantByName(kbSubsetCollectionName);
        bookkeepingMt = cycAccess.getKnownConstantByName("BookkeepingMt");
    }

    /**
     * Parses and imports the given DAML URL.
     *
     * @param damlOntologyDefiningURLString the URL to import
     * @param importMtName the microtheory into which DAML content is asserted
     */
    public void importDaml (String damlOntologyDefiningURLString,
                            String importMtName)
        throws IOException, CycApiException {
        importDaml (damlOntologyDefiningURLString,
                    importMtName,
                    null);
    }

    /**
     * Parses and imports the given DAML URL.
     *
     * @param damlOntologyDefiningURLString the URL to import
     * @param importMtName the microtheory into which DAML content is asserted
     * @param characterEncoding the character encoding scheme of the DAML imput
     * (e.g. "UTF-8", used when not the default ASCII
     */
    public void importDaml (String damlOntologyDefiningURLString,
                            String importMtName,
                            String characterEncoding)
        throws IOException, CycApiException {
        this.damlOntologyDefiningURLString = damlOntologyDefiningURLString;
        this.importMtName = importMtName;
        this.characterEncoding = characterEncoding;
        if (verbosity > 0) {
            if (characterEncoding == null)
                Log.current.println("\nImporting " + damlOntologyDefiningURLString +
                                    "\ninto " + importMtName);
            else
                Log.current.println("\nImporting " + damlOntologyDefiningURLString +
                                    " encoding " + characterEncoding +
                                    "\ninto " + importMtName);
        }
        importMt = cycAccess.getKnownConstantByName(importMtName);
        damlOntologyDefiningURL =
            new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                        damlOntologyDefiningURLString);
        Log.current.println("Defining URL " + damlOntologyDefiningURL.cyclify());
        CycList gaf = new CycList();
        gaf.add(cycAccess.getKnownConstantByName("xmlNameSpace"));
        String nickname = (String) ontologyNicknames.get(damlOntologyDefiningURLString);
        if (nickname == null)
            throw new RuntimeException("Nickname not found for " +
                                       damlOntologyDefiningURLString);
        gaf.add(nickname);
        gaf.add(damlOntologyDefiningURL);
        cycAccess.assertGaf(gaf, importMt);
        Log.current.println("\nStatements\n");
        //cycAccess.traceOn();
        InputStreamReader in;
        URL url;
        try {
            File ff = new File(damlOntologyDefiningURLString);
            if (characterEncoding == null)
                in = new InputStreamReader(new FileInputStream(ff));
            else
                in = new InputStreamReader(new FileInputStream(ff), characterEncoding);
            url = ff.toURL();
        }
        catch (Exception ignore) {
            try {
                url = new URL(damlOntologyDefiningURLString);
                if (characterEncoding == null)
                    in = new InputStreamReader(url.openStream());
                else
                    in = new InputStreamReader(url.openStream(), characterEncoding);
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
            Log.current.println("\nDone importing " + damlOntologyDefiningURLString + "\n");
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
            processRestrictionSubject(subject, predicate, object);
                return;
            }
            if (object.isAnonymous()) {
                processRestrictionObject(subject, predicate, object);
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
            if (subject.isAnonymous()) {
                processRestrictionSubject(subject, predicate, literal);
            }
            else {
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
        if (! actuallyImport)
            return;
        if (predicateTermInfo.isURI)
            predicateTermInfo.coerceToNamespace();
        String damlPredicate = predicateTermInfo.toString();
        if (! subjectTermInfo.hasEquivalentCycTerm()) {
            if (damlPredicate.equals("#$isa")) {
                importIsa(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$genls")) {
                // TODO generalize for argument order in KB.
                if (predicateTermInfo.constantName.startsWith("dmoz:narrow"))
                    importGenls(objLitTermInfo, subjectTermInfo);
                else
                    importGenls(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("daml:versionInfo")) {
                importVersionInfo(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("daml:imports")) {
                importImports(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$comment")) {
                importComment(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$nameString")) {
                importNameString(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$synonymousExternalConcept")) {
                importSynonymousExternalConcept(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$arg1Isa")) {
                importArg1Isa(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$arg2Isa")) {
                importArg2Isa(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$conceptuallyRelated")) {
                importConceptuallyRelated(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$containsInformationAbout")) {
                importContainsInformationAbout(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (damlPredicate.equals("#$genlPreds")) {
                importGenlPreds(subjectTermInfo, objLitTermInfo);
                return;
            }
            if (predicateTermInfo.ontologyNickname.equals("daml") ||
                predicateTermInfo.ontologyNickname.equals("rdfs") ||
                predicateTermInfo.ontologyNickname.equals("rdf")) {
                Log.current.println("\n\nUnhandled predicate: " + damlPredicate + "\n");
                return;
            }
        }
        CycFort subject = importTerm(subjectTermInfo);
        CycFort predicate = cycAccess.getConstantByName(damlPredicate);
        if (predicate == null) {
            predicate = importTerm(predicateTermInfo);
            cycAccess.assertIsaBinaryPredicate(predicate);
        }
        CycList arg1Constraints =
            cycAccess.getArg1Isas((CycConstant) predicate, cycAccess.universalVocabularyMt);
        assertForwardArgConstraints(subject, arg1Constraints);
        CycList arg2Constraints =
            cycAccess.getArg2Isas((CycConstant) predicate, cycAccess.universalVocabularyMt);

        // TODO - remove OpenCyc condition when inter-arg-isa1-2 is in the api.
        if (! cycAccess.isOpenCyc())
            arg2Constraints.addAllNew(cycAccess.getInterArgIsa1_2_forArg2((CycConstant) predicate,
                                                                          subject,
                                                                          cycAccess.universalVocabularyMt));

        if (objLitTermInfo.isLiteral) {
            if (! subjectTermInfo.hasEquivalentCycTerm())
                // No need to make assertions about mapped Cyc terms.
                importLiteralTriple(subject, predicate, objLitTermInfo, arg2Constraints);
            return;
        }
        CycFort object = cycAccess.getConstantByName(objLitTermInfo.toString());
        if (object == null)
            object = importTerm(objLitTermInfo);
        assertForwardArgConstraints(object, arg2Constraints);
        cycAccess.assertGaf(importMt,
                            predicate,
                            subject,
                            object);
        Log.current.println("(" + predicate.cyclify() + " " +
                            subject.cyclify() + " " +
                            object.cyclify() + ")\n");
    }

    /**
     * Imports the RDF literal triple.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param LiteralTermInfo the literal DamlTermInfo object
     * @param arg2Constraints the argument constraints on the type of literal permitted
     * by this assertion
     */
    protected void importLiteralTriple (CycFort subject,
                                        CycFort predicate,
                                        DamlTermInfo LiteralTermInfo,
                                        CycList arg2Constraints)
        throws IOException, UnknownHostException, CycApiException {
        if (arg2Constraints.size() == 0) {
            cycAccess.assertGaf(importMt,
                                predicate,
                                subject,
                                (String) escaped(LiteralTermInfo.literal));
        }
        else {
            if (arg2Constraints.size() > 1) {
            Log.current.println("*** ignoring extra literal argument constraints " +
                                arg2Constraints.cyclify());
            //TODO find most specific of the arg constraints.
            }
            CycFort arg2Constraint = (CycFort) arg2Constraints.first();
            if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLString")) ||
                arg2Constraint.equals(cycAccess.getKnownConstantByName("CharacterString"))) {
                cycAccess.assertGaf(importMt,
                                    predicate,
                                    subject,
                                    (String) escaped(LiteralTermInfo.literal));

            }
            else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLRealNumber"))) {
                cycAccess.assertGaf(importMt,
                                    predicate,
                                    subject,
                                    new Double(LiteralTermInfo.literal));

            }
            else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLInteger"))) {
                cycAccess.assertGaf(importMt,
                                    predicate,
                                    subject,
                                    new Integer(LiteralTermInfo.literal));

            }
            else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("Date"))) {
                CycFort date = new CycNart(cycAccess.getKnownConstantByName("DateDecodeStringFn"),
                                           "YYYY-MM-DD",
                                           LiteralTermInfo.literal);

                cycAccess.assertGaf(importMt,
                                    predicate,
                                    subject,
                                    date);

            }
            else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("UniformResourceLocator"))) {
                CycFort urlFn = new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                           LiteralTermInfo.literal);

                cycAccess.assertGaf(importMt,
                                    predicate,
                                    subject,
                                    urlFn);

            }
            else
                Log.current.println("*** unhandled literal type constraint " +
                                    arg2Constraint.cyclify() + "\n");
        }
        Log.current.println("(" + predicate.cyclify() + " " +
                            subject.cyclify() + " \"" +
                            (String) LiteralTermInfo.literal + "\")\n");
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
     * Asserts argument constraints on a forward referenced term used
     * in an assertion.
     *
     * @param term the given term
     * @param argConstraints the list of collections for which term must be an instance
     */
    protected void assertForwardArgConstraints (CycFort term, CycList argConstraints)
        throws IOException, UnknownHostException, CycApiException  {
        for (int i = 0; i < argConstraints.size(); i++) {
            CycFort argConstraint = (CycFort) argConstraints.get(i);
            if (! cycAccess.isa(term, argConstraint)) {
                cycAccess.assertIsa(term, argConstraint, cycAccess.universalVocabularyMt);
                Log.current.println("*** asserting forward arg constraint " +
                                    "(#$isa " + term.cyclify() + " " +
                                    argConstraint.cyclify() + ")");
            }
        }
    }

    /**
     * Imports the rdf:type triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importIsa (DamlTermInfo subjectTermInfo,
                              DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort term = importTerm(subjectTermInfo);
        if (! subjectTermInfo.isURI) {
            cycAccess.assertGaf(cycAccess.bookkeepingMt,
                                cycAccess.getKnownConstantByName("damlOntology"),
                                term,
                                damlOntologyDefiningURL);
            if (! subjectTermInfo.isAnonymous)
                cycAccess.assertGaf(cycAccess.bookkeepingMt,
                                    cycAccess.getKnownConstantByName("damlURI"),
                                    term,
                                    new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                                subjectTermInfo.uri));
        }
        if (objectTermInfo.constantName.equals("daml:UnambiguousProperty")) {
            cycAccess.assertArg1FormatSingleEntry(subjectTermInfo.cycFort);
            Log.current.println("(#$arg1Format " +
                                term.cyclify() + " #$SingleEntryFormat)");
            return;
        }
        CycFort collection = importTerm(objectTermInfo);
        if (! cycAccess.isCollection(collection)) {
            cycAccess.assertIsaCollection(collection);
            Log.current.println("*** forward reference to collection " + collection.cyclify());
        }
        cycAccess.assertIsa(term,
                            collection);
        Log.current.println("(#$isa " +
                            term.cyclify() + " " +
                            collection.cyclify() + ")");
    }

    /**
     * Imports the rdf:subClassOf triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importGenls (DamlTermInfo subjectTermInfo,
                                DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort term = importTerm(subjectTermInfo);
        if (! cycAccess.isCollection(term)) {
            cycAccess.assertIsaCollection(term);
            Log.current.println("*** forward reference to collection " + term.cyclify());
        }
        CycFort collection = importTerm(objectTermInfo);
        if (! cycAccess.isCollection(collection)) {
            cycAccess.assertIsaCollection(collection);
            Log.current.println("*** forward reference to collection " + collection.cyclify());
        }
        cycAccess.assertGenls(term,
                              collection);
        Log.current.println("(#$genls " +
                            term.cyclify() + " " +
                            collection.cyclify() + ")");
    }

    /**
     * Imports the daml:samePropertyAs triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importSynonymousExternalConcept (DamlTermInfo subjectTermInfo,
                                                    DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        importTerm(subjectTermInfo);
        if (objectTermInfo.cycFort == null)
            importTerm(objectTermInfo);
        cycAccess.assertSynonymousExternalConcept(subjectTermInfo.toString(),
                                                  "WorldWideWeb-DynamicIndexedInfoSource",
                                                  objectTermInfo.toOriginalString(),
                                                  importMtName);
        Log.current.println("(#$synonymousExternalConcept " +
                            subjectTermInfo.cycFort.cyclify() + " " +
                            "WorldWideWeb-DynamicIndexedInfoSource \"" +
                            objectTermInfo.toOriginalString() + "\")");
    }

    /**
     * Imports the rdfs:domain triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importArg1Isa (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort term1 = importTerm(subjectTermInfo);
        CycFort term2 = importTerm(objectTermInfo);
        if (! cycAccess.isCollection(term2)) {
            cycAccess.assertIsaCollection(term2);
            Log.current.println("*** forward reference to collection " + term2.cyclify());
        }
        cycAccess.assertArgIsa(term1, 1, term2);
        Log.current.println("(#$arg1Isa " +
                            term1.cyclify() + " " +
                            term2.cyclify() + ")");
    }

    /**
     * Imports the rdfs:range triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importArg2Isa (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort term1 = importTerm(subjectTermInfo);
        CycFort term2 = importTerm(objectTermInfo);
        if (! cycAccess.isCollection(term2)) {
            cycAccess.assertIsaCollection(term2);
            Log.current.println("*** forward reference to collection " + term2.cyclify());
        }
        cycAccess.assertArgIsa(term1, 2, term2);
        Log.current.println("(#$arg2Isa " +
                          term1.cyclify() + " " +
                          term2.cyclify() + ")");
    }

    /**
     * Imports the rdfs:seeAlso triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importConceptuallyRelated (DamlTermInfo subjectTermInfo,
                                              DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort term1 = importTerm(subjectTermInfo);
        CycFort term2 = importTerm(objectTermInfo);
        cycAccess.assertConceptuallyRelated(term1,
                                            term2,
                                            importMt);
        Log.current.println("(#$conceptuallyRelated " +
                          term1.cyclify() + " " +
                          term2.cyclify() + ")");
    }

    /**
     * Imports the daml:subPropertyOf triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param objectTermInfo the object DamlTermInfo object
     */
    protected void importGenlPreds (DamlTermInfo subjectTermInfo,
                                    DamlTermInfo objectTermInfo)
        throws IOException, UnknownHostException, CycApiException  {
        CycFort specPred = importTerm(subjectTermInfo);
        CycFort genlPred = importTerm(objectTermInfo);
        if (! cycAccess.isBinaryPredicate((CycConstant) genlPred)) {
            // forward reference
            cycAccess.assertIsaBinaryPredicate(genlPred);
            Log.current.println("*** forward reference to predicate " + genlPred.cyclify());
        }
        cycAccess.assertGenlPreds(specPred,
                                  genlPred);
        Log.current.println("(#$genlPreds " +
                          specPred.cyclify() + " " +
                          genlPred.cyclify() + ")");
    }

    /**
     * Imports the daml:versionInfo triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param literalTermInfo the object DamlTermInfo object
     */
    protected void importVersionInfo (DamlTermInfo subjectTermInfo,
                                      DamlTermInfo literalTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        CycFort term = importTerm(subjectTermInfo);
        //TODO
    }

    /**
     * Imports the daml:imports triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param literalTermInfo the object DamlTermInfo object
     */
    protected void importImports (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo literalTermInfo)
        throws IOException, UnknownHostException, CycApiException {
    }

    /**
     * Imports the rdfs:comment triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param literalTermInfo the object DamlTermInfo object
     */
    protected void importComment (DamlTermInfo subjectTermInfo,
                                  DamlTermInfo literalTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        CycFort term = importTerm(subjectTermInfo);
        String comment = literalTermInfo.literalValue().replace('\n', ' ');
        cycAccess.assertComment(term,
                                comment,
                                importMt);
        Log.current.println("(#$comment " +
                          term.cyclify() + " \"" +
                          comment + "\")");
    }

    /**
     * Imports the rdfs:label triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param literalTermInfo the object DamlTermInfo object
     */
    protected void importNameString (DamlTermInfo subjectTermInfo,
                                     DamlTermInfo literalTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        CycFort term = importTerm(subjectTermInfo);
        String nameString = literalTermInfo.literalValue();
        cycAccess.assertNameString(term,
                                   literalTermInfo.literalValue(),
                                   importMt);
        Log.current.println("(#$nameString " +
                          term.cyclify() + " \"" +
                          literalTermInfo.literalValue() + "\")");
        CycList nameStrings = cycAccess.getNameStrings(term, importMt);
        if (! nameStrings.contains(nameString)) {
            if (verbosity > 1) {
                Log.current.println("asserted " + nameString);
                if (nameStrings.size() == 1)
                    Log.current.println("queried  " + nameStrings.get(0));
                else
                    Log.current.println("queried  " + nameStrings);
            }
        }
    }

    /**
     * Imports the rdfs:isDefinedBy triple.
     *
     * @param subjectTermInfo the subject DamlTermInfo object
     * @param literalTermInfo the object DamlTermInfo object
     */
    protected void importContainsInformationAbout (DamlTermInfo subjectTermInfo,
                                                   DamlTermInfo uriTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        CycFort term = importTerm(subjectTermInfo);
        CycFort resource = importTerm(uriTermInfo);
        cycAccess.assertGaf(importMt,
                            cycAccess.getKnownConstantByName("containsInformationAbout"),
                            resource,
                            term);
        Log.current.println("(#$containsInformationAbout " +
                            resource.cyclify() + " " +
                            term.cyclify() + ")");
    }

    /**
     * Imports the given DAML term and returns the Cyc term.
     *
     * @param damlTermInfo the given daml term information
     * @return the Cyc term resulting from the import of the given DAML term
     */
    protected CycFort importTerm (DamlTermInfo damlTermInfo)
        throws IOException, UnknownHostException, CycApiException {
        if (damlTermInfo.cycFort != null &&
            previousDamlTermInfo != null &&
            previousDamlTermInfo.equals(damlTermInfo))
            return previousDamlTermInfo.cycFort;

        CycFort cycFort = null;
        if (damlTermInfo.equivalentDamlCycTerm != null) {
            // not an imported term, but an equivalent existing Cyc term
            //Log.current.println("*** not importing " + cycFort.cyclify());
            previousDamlTermInfo = damlTermInfo;
            return damlTermInfo.cycFort;
        }
        else if (damlTermInfo.isURI) {
            cycFort = new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                  damlTermInfo.toString());
            damlTermInfo.cycFort = cycFort;
            previousDamlTermInfo = damlTermInfo;
            return cycFort;
        }
        else if (damlTermInfo.constantName != null &&
                 damlTermInfoWrapperTest(damlTermInfo.constantName))
            cycFort = damlTermInfoWrapper(damlTermInfo.constantName);
        else {
            String term = damlTermInfo.toString();
            try {
                cycFort = cycAccess.findOrCreate(term);
            }
            catch (CycApiException e) {
                Log.current.println("Error while importing " + term);
                Log.current.printStackTrace(e);
                System.exit(1);
            }
            if (cycFort == null)
                // error
                return cycFort;
        }
        if (verbosity > 1)
            Log.current.println("(#$isa  " + cycFort.cyclify() + " " +
                                kbSubsetCollection.cyclify() + ")");
        cycAccess.assertIsa(cycFort,
                            kbSubsetCollection,
                            bookkeepingMt);
        if (damlTermInfo.isAnonymous) {
            if (verbosity > 1)
                Log.current.println("(#$isa  " + cycFort.cyclify() + " #$DamlAnonymousClass)");
            cycAccess.assertIsa(cycFort,
                                cycAccess.getKnownConstantByName("DamlAnonymousClass"),
                                importMt);
        }
        additionalLexicalAssertions(cycFort);
        damlTermInfo.cycFort = cycFort;
        previousDamlTermInfo = damlTermInfo;
        return cycFort;
    }

    /**
     * Determines if the imported term should be wrapped with a functional
     * expression and imported as a NART.  Subclasses may override for desired
     * behavior.
     * @param termName the daml term to be substituted and wrapped
     */
    protected boolean damlTermInfoWrapperTest (String termName) {
        return false;
    }

    /**
     * Wraps the impored term with the appropriate functional expression
     * so that it can be imported as a NART.  Subclasses may override for desired
     * behavior.
     * @param termName the daml term to be substituted and wrapped
     */
    protected CycNart damlTermInfoWrapper (String termName)
        throws IOException, CycApiException {
        return null;
    }

    /**
     * Asserts additional lexical assertions about the given term which are specific to
     * the import of Open Directory terms. Subclasses may override for desired
     * behavior.
     *
     * @param term the imported Open Directory (wrapped) term
     */
    protected void additionalLexicalAssertions (CycFort term)
        throws IOException, CycApiException {
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
        if (verbosity < 1)
            return;
        Log.current.println();
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
        if (equivalentDamlCycTerms.containsKey(constantName)) {
            damlTermInfo.equivalentDamlCycTerm =
                (CycFort) equivalentDamlCycTerms.get(constantName);
            damlTermInfo.cycFort = damlTermInfo.equivalentDamlCycTerm;
        }
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
     * Records the components of a DAML Restriction.
     *
     * @param subject the RDF Triple anonymous Subject
     * @param predicate the RDF Triple Predicate
     * @param object the RDF Triple Object
     */
    protected void processRestrictionSubject(AResource subject,
                                             AResource predicate,
                                             AResource object)
        throws IOException, UnknownHostException, CycApiException {
        DamlTermInfo subjectTermInfo = resource(subject, null);
        DamlTermInfo predicateTermInfo = resource(predicate, null);
        DamlTermInfo objectTermInfo = resource(object, null);
        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      objectTermInfo);
        if (predicateTermInfo.toString().equals("isa") &&
            objectTermInfo.toString().equals("daml:Restriction")) {
            damlRestriction = new DamlRestriction(this);
            damlRestriction.anonymousId = subject.getAnonymousID();
        }
        else if (damlRestriction == null) {
            // Not a Restriction, but an anonymous Class.
            importTriple(subjectTermInfo,
                         predicateTermInfo,
                         objectTermInfo);
            return;
        }
        else if (predicateTermInfo.toString().equals("daml:onProperty")) {
            if (objectTermInfo.cycFort == null)
                importTerm(objectTermInfo);
            damlRestriction.property = objectTermInfo.cycFort;
        }
        else if (predicateTermInfo.toString().equals("daml:toClass")) {
            if (objectTermInfo.cycFort == null)
                importTerm(objectTermInfo);
            damlRestriction.toClasses.add(objectTermInfo.cycFort);
        }
        else
            throw new RuntimeException("Unexpected restriction property " +
                                       predicateTermInfo.toString());
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
                                             ALiteral literal)
        throws IOException, UnknownHostException, CycApiException {
        DamlTermInfo subjectTermInfo = resource(subject, null);
        DamlTermInfo predicateTermInfo = resource(predicate, null);
        DamlTermInfo literalTermInfo = literal(literal);
        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      literalTermInfo);
        if (damlRestriction == null) {
            // Not a Restriction, but an anonymous Class.
            importTriple(subjectTermInfo,
                         predicateTermInfo,
                         literalTermInfo);
            return;
        }
        if (predicateTermInfo.toString().equals("daml:maxCardinality") ||
            predicateTermInfo.toString().equals("daml:cardinality"))
            damlRestriction.maxCardinality = new Integer(literalTermInfo.literalValue());
        else
            //TODO
            Log.current.println("\n*** unimplemented restriction triple \n    " +
                                subjectTermInfo.toString() + " " +
                                predicateTermInfo.toString() + " " +
                                literalTermInfo.toString() + "\n");
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
                                            AResource object)
        throws IOException, UnknownHostException, CycApiException {
        DamlTermInfo subjectTermInfo = resource(subject, null);
        DamlTermInfo predicateTermInfo = resource(predicate, null);
        DamlTermInfo objectTermInfo = resource(object, null);
        displayTriple(subjectTermInfo,
                      predicateTermInfo,
                      objectTermInfo);
        if (damlRestriction == null) {
            // Not a Restriction, but an anonymous Class.
            try {
                importTriple(subjectTermInfo,
                             predicateTermInfo,
                             objectTermInfo);
            }
            catch (Exception e) {
                Log.current.printStackTrace(e);
                System.exit(1);
            }
            return;
        }
        if (predicateTermInfo.toString().equals("genls")) {
            if (subjectTermInfo.cycFort == null)
                importTerm(subjectTermInfo);
            damlRestriction.fromClass = subjectTermInfo.cycFort;
        }
        else
            throw new RuntimeException("Unexpected restriction property " +
                                       predicateTermInfo.toString());
        try {
            damlRestriction.formInterArgConstraints();
            if (damlRestriction.interArgIsaConstraint != null) {
                if(actuallyImport)
                    cycAccess.assertGaf(damlRestriction.interArgIsaConstraint,
                                        importMt);
                Log.current.println(damlRestriction.interArgIsaConstraint.cyclify());
            }
            if (damlRestriction.interArgFormatConstraint != null) {
                if(actuallyImport)
                    cycAccess.assertGaf(damlRestriction.interArgFormatConstraint,
                                        importMt);
                Log.current.println(damlRestriction.interArgFormatConstraint.cyclify());
            }
            Log.current.println();
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            System.exit(1);
        }
        damlRestriction = null;
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
     * Initializes the DAML ontology mt.
     */
    public void initializeDamlOntologyMt (String mtName)
        throws IOException, UnknownHostException, CycApiException {
        Log.current.println("Creating " + mtName);
        String comment = "A microtheory to contain imported DAML assertions.";
        ArrayList genlMts = new ArrayList();
        genlMts.add("BaseKB");
        String isaMtName = "SourceMicrotheory";
        cycAccess.createMicrotheory(mtName, comment, isaMtName, genlMts);
    }

    /**
     * Initializes the Ontology nicknames mapping.
     */
    public void initializeCommonOntologyNicknames () {
        if (verbosity > 1)
            Log.current.println("Creating common ontology nicknames");
        if (ontologyNicknames == null)
            ontologyNicknames = new HashMap();
        ontologyNicknames.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
        ontologyNicknames.put("http://www.w3.org/2000/01/rdf-schema", "rdfs");
        ontologyNicknames.put("http://www.w3.org/2000/10/XMLSchema", "xsd");
        ontologyNicknames.put("http://www.daml.org/2001/03/daml+oil", "daml");

        ontologyNicknames.put("http://xmlns.com/foaf/0.1", "foaf");
        ontologyNicknames.put("http://xmlns.com/foaf/0.1/", "foaf");
        ontologyNicknames.put("http://xmlns.com/foaf", "foaf");
        ontologyNicknames.put("http://xmlns.com/wot/0.1", "wot");
        ontologyNicknames.put("http://xmlns.com/wordnet/1.6", "wn");
        ontologyNicknames.put("http://www.w3.org/2001/08/rdfweb", "rdfweb");
        ontologyNicknames.put("http://purl.org/dc/elements/1.1", "dublincore");
        ontologyNicknames.put("http://purl.org/dc/elements/1.0/", "dublincore");

        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/DC/3.2", "drc-dc");
        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/VES/3.2", "ves");
        ontologyNicknames.put("http://orlando.drc.com/daml/Ontology/daml-extension/3.2/daml-ext-ont", "daml-ext");
        ontologyNicknames.put("http://www.daml.org/cgi-bin/geonames", "geonames");
        ontologyNicknames.put("http://www.daml.org/cgi-bin/airport", "airport");
        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/factbook-ont.daml", "factbook");
        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/factbook-ont", "factbook");
        ontologyNicknames.put("http://www.daml.org/2001/12/factbook", "factbook");
        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/af.daml", "factbook");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology", "daml-experiment");

        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/internationalOrganizations.daml", "factbkorg");
        ontologyNicknames.put("http://www.daml.org/2001/12/factbook/internationalOrganizations", "factbkorg");

        ontologyNicknames.put("http://www.daml.org/2002/02/chiefs/chiefs-ont.daml", "chiefs-ont");
        ontologyNicknames.put("http://www.daml.org/2002/02/chiefs/chiefs-ont", "chiefs-ont");

        ontologyNicknames.put("http://www.daml.org/2002/02/chiefs/af.daml", "chiefs");
        ontologyNicknames.put("http://www.daml.org/2002/02/chiefs/af", "chiefs");

        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/DC/3.2/dces-ont.daml", "dces");
        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/DC/3.2/dces-ont", "dces");

        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont.daml", "ves");
        ontologyNicknames.put("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont", "ves");

        ontologyNicknames.put("http://www.daml.org/2001/10/html/airport-ont.daml", "airport");
        ontologyNicknames.put("http://www.daml.org/2001/10/html/airport-ont", "airport");

        ontologyNicknames.put("http://www.daml.org/2001/09/countries/fips-10-4-ont", "fips10-4");

        ontologyNicknames.put("http://www.daml.org/2001/09/countries/fips.daml", "fips");
        ontologyNicknames.put("http://www.daml.org/2001/09/countries/fips", "fips");

        ontologyNicknames.put("http://www.daml.org/2001/09/countries/iso-3166-ont", "iso3166");

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

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/enp-characteristics.daml", "enp-char");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/enp-characteristics", "enp-char");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/entity-ont.daml", "entity-ont");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/entity-ont", "entity-ont");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/entity.daml", "entity");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/entity", "entity");


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

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/target-ont.daml", "target-ont");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/target-ont", "target-ont");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit-ont.daml", "unit-ont");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit-ont", "unit-ont");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit-status.daml", "unit-status");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit-status", "unit-status");

        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit.daml", "unit");
        ontologyNicknames.put("http://www.daml.org/experiment/ontology/unit", "unit");

        ontologyNicknames.put("http://www.daml.org/2002/09/milservices/milservices-ont", "milsvcs-ont");

        ontologyNicknames.put("http://www.daml.org/2002/09/milservices/us", "milsvcs-us");
    }

    public void setOntologyNickname (String uri, String nickname) {
        if (ontologyNicknames == null)
            ontologyNicknames = new HashMap();
        ontologyNicknames.put(uri, nickname);
    }

    /**
     * Initializes common DAML mapped terms.
     */
    public void initializeCommonMappedTerms ()
        throws IOException, UnknownHostException, CycApiException {
        if (verbosity > 1)
            Log.current.println("Creating common mapped terms");
        assertMapping("daml:Thing",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#Thing",
                      "Thing");
        assertMapping("rdfs:Resource",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#Resource",
                      "Thing");

        assertMapping("daml:Class",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#Class",
                      "Collection");
        assertMapping("rdfs:Class",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#Class",
                      "Collection");

        assertMapping("daml:Ontology",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#Ontology",
                      "AbstractInformationStructure");

        assertMapping("daml:DatatypeProperty",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#DatatypeProperty",
                      "DamlDatatypeProperty");

        assertMapping("daml:ObjectProperty",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#ObjectProperty",
                      "DamlObjectProperty");

        assertMapping("daml:Property",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#Property",
                      "BinaryPredicate");
        assertMapping("rdfs:Property",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#Property",
                      "BinaryPredicate");
        assertMapping("rdf:Property",
                      "http://www.w3.org/1999/02/22-rdf-syntax-ns",
                      "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property",
                      "BinaryPredicate");

        assertMapping("daml:TransitiveProperty",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#TransitiveProperty",
                      "TransitiveBinaryPredicate");

        assertMapping("daml:Literal",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#Literal",
                      "SubLAtomicTerm");
        assertMapping("rdfs:Literal",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#Literal",
                      "SubLAtomicTerm");

        assertMapping("xsd:string",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#string",
                      "SubLString");
        assertMapping("xsd:decimal",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#decimal",
                      "SubLRealNumber");
        assertMapping("xsd:integer",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#integer",
                      "SubLInteger");
        assertMapping("xsd:float",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#float",
                      "SubLRealNumber");
        assertMapping("xsd:double",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#double",
                      "SubLRealNumber");
        assertMapping("xsd:date",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#date",
                      "Date");
        assertMapping("xsd:uriReference",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#uriReference",
                      "UniformResourceLocator");
        assertMapping("xsd:anyURI",
                      "http://www.w3.org/2000/10/XMLSchema",
                      "http://www.w3.org/2000/10/XMLSchema#anyURI",
                      "UniformResourceLocator");

        // Binary predicates
        assertMapping("daml:subClassOf",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#subClassOf",
                      "genls");
        assertMapping("rdfs:subClassOf",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#subClassOf",
                      "genls");

        assertMapping("daml:type",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#type",
                      "isa");
        assertMapping("rdfs:type",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#type",
                      "isa");
        assertMapping("rdf:type",
                      "http://www.w3.org/1999/02/22-rdf-syntax-ns",
                      "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                      "isa");

        assertMapping("daml:subPropertyOf",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#subPropertyOf",
                      "genlPreds");
        assertMapping("rdfs:subPropertyOf",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#subPropertyOf",
                      "genlPreds");

        assertMapping("daml:label",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#label",
                      "nameString");
        assertMapping("rdfs:label",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#label",
                      "nameString");

        assertMapping("daml:comment",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#comment",
                      "comment");
        assertMapping("rdfs:comment",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#comment",
                      "comment");

        assertMapping("daml:seeAlso",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#seeAlso",
                      "conceptuallyRelated");
        assertMapping("rdfs:seeAlso",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#seeAlso",
                      "conceptuallyRelated");

        assertMapping("daml:isDefinedBy",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#isDefinedBy",
                      "containsInformationAbout");
        assertMapping("rdfs:isDefinedBy",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#isDefinedBy",
                      "containsInformationAbout");

        assertMapping("daml:domain",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#domain",
                      "arg1Isa");
        assertMapping("rdfs:domain",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#domain",
                      "arg1Isa");

        assertMapping("daml:range",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#range",
                      "arg2Isa");
        assertMapping("rdfs:range",
                      "http://www.w3.org/2000/01/rdf-schema",
                      "http://www.w3.org/2000/01/rdf-schema#range",
                      "arg2Isa");

        assertMapping("daml:differentIndividualFrom",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#differentIndividualFrom",
                      "different");

        assertMapping("daml:samePropertyAs",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#samePropertyAs",
                      "synonymousExternalConcept");

        assertMapping("daml:disjointWith",
                      "http://www.daml.org/2001/03/daml+oil",
                      "http://www.daml.org/2001/03/daml+oil#disjointWith",
                      "disjointWith");



        // Get the above mappings plus any previously defined in the KB.
        getMappings();
    }

    /**
     * Asserts that the given DAML/RDFS/RDF term is mapped to the
     * given Cyc term, with the assertion placed in #$BaseKB.
     *
     * @param damlTermName the given DAML/RDFS/RDF term
     * @param damlOntology the Uniform Resource Locator in which the definition of
     * the daml term is found
     * @param damlURI the Uniform Resource Locator which uniquely identifies the daml term
     * @param cycTermName the given Cyc term
     */
    protected void assertMapping (String damlTermName,
                                  String damlOntology,
                                  String damlURI,
                                  String cycTermName)
        throws IOException, UnknownHostException, CycApiException {
        assertMapping (damlTermName,
                       damlOntology,
                       damlURI,
                       cycTermName,
                       "BaseKB");
    }

    /**
     * Asserts that the given DAML/RDFS/RDF term is mapped to the
     * given Cyc term.
     *
     * @param damlTermName the given DAML/RDFS/RDF term
     * @param damlOntology the Uniform Resource Locator in which the definition of
     * the daml term is found
     * @param damlURI the Uniform Resource Locator which uniquely identifies the daml term
     * @param cycTermName the given Cyc term
     * @param mappingMt the microtheory in which the assertions are placed
     */
    protected void assertMapping (String damlTermName,
                                  String damlOntology,
                                  String damlURI,
                                  String cycTermName,
                                  String mappingMt)
        throws IOException, UnknownHostException, CycApiException {
        CycFort cycTerm;
        cycTermName = Strings.stripLeadingBlanks(cycTermName);
        cycTermName = Strings.stripTrailingBlanks(cycTermName);
        if (cycTermName.startsWith("(")) {
            CycList cycNartList = cycAccess.makeCycList(cycTermName);
            cycTerm = new CycNart(cycNartList);
        }
        else
            cycTerm = cycAccess.findOrCreate(cycTermName);
        if (verbosity > 1)
            Log.current.println("Mapping " + damlTermName + " to " + cycTerm.cyclify());
        cycAccess.assertSynonymousExternalConcept(cycTerm,
                                                  cycAccess.find("WorldWideWeb-DynamicIndexedInfoSource"),
                                                  damlTermName,
                                                  cycAccess.find(mappingMt));
        CycFort damlTerm = cycAccess.findOrCreate(damlTermName);
        cycAccess.assertComment(damlTerm,
                                damlTerm.cyclify() +
                                " is an imported DAML/XML term equivalent to the Cyc term " +
                                cycTerm.cyclify(),
                                cycAccess.getKnownConstantByName(mappingMt));
        // assert (#$isa damlTerm #$DamlConstant) in BookkeepingMt
        cycAccess.assertIsa(damlTerm,
                            cycAccess.getKnownConstantByName("DamlConstant"),
                            cycAccess.bookkeepingMt);
        // assert (#$damlOntology damlTerm ontologyURL) in BookkeepingMt
        cycAccess.assertGaf(cycAccess.bookkeepingMt,
                            cycAccess.getKnownConstantByName("damlOntology"),
                            damlTerm,
                            new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                       damlOntology));
        // assert (#$damlURI damlTerm uri) in BookkeepingMt
        cycAccess.assertGaf(cycAccess.bookkeepingMt,
                            cycAccess.getKnownConstantByName("damlURI"),
                            damlTerm,
                            new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                       damlURI));
    }

    /**
     * Asserts that the given DAML/RDFS/RDF property is mapped to the
     * given Cyc predicate with the arguments reversed.
     *
     * @param damlPropertyName the given DAML/RDFS/RDF property
     * @param damlOntology the Uniform Resource Locator in which the definition of
     * the daml term is found
     * @param damlURI the Uniform Resource Locator which uniquely identifies the daml term
     * @param cycBinaryPredicateName the given Cyc binary predicate
     * @argumentMappingList a list of argument positions
     * @param mappingMt the microtheory in which the assertions are placed
     */
    protected void assertArgumentMapping (String damlPropertyName,
                                          String damlOntology,
                                          String damlURI,
                                          String cycBinaryPredicateName,
                                          String argumentMappingList)
        throws IOException, UnknownHostException, CycApiException {
            assertArgumentMapping (damlPropertyName,
                                   damlOntology,
                                   damlURI,
                                   cycBinaryPredicateName,
                                   argumentMappingList,
                                   "UniversalVocabularyMt");
    }

    /**
     * Asserts that the given DAML/RDFS/RDF property is mapped to the
     * given Cyc predicate with the arguments reversed.
     *
     * @param damlPropertyName the given DAML/RDFS/RDF property
     * @param damlOntology the Uniform Resource Locator in which the definition of
     * the daml term is found
     * @param damlURI the Uniform Resource Locator which uniquely identifies the daml term
     * @param cycBinaryPredicateName the given Cyc binary predicate
     * @argumentMappingList a list of argument positions
     * @param mappingMt the microtheory in which the assertions are placed
     */
    protected void assertArgumentMapping (String damlPropertyName,
                                          String damlOntology,
                                          String damlURI,
                                          String cycBinaryPredicateName,
                                          String argumentMappingList,
                                          String mappingMt)
        throws IOException, UnknownHostException, CycApiException {
        CycFort cycBinaryPredicate = cycAccess.findOrCreate(cycBinaryPredicateName);
        Log.current.println("Mapping " + damlPropertyName + " to " + cycBinaryPredicate.cyclify());
        cycAccess.assertSynonymousExternalConcept(cycBinaryPredicateName,
                                                  "WorldWideWeb-DynamicIndexedInfoSource",
                                                  damlPropertyName,
                                                  mappingMt);
        //TODO assert synonymousRelnArgs

        CycFort damlProperty = cycAccess.findOrCreate(damlPropertyName);
        cycAccess.assertComment(damlProperty,
                                damlProperty.cyclify() +
                                " is an imported DAML/XML property equivalent to the Cyc predicate " +
                                cycBinaryPredicate.cyclify() +
                                " (with the arguments reversed).",
                                cycAccess.getKnownConstantByName(mappingMt));
        // assert (#$isa damlTerm #$DamlConstant) in BookkeepingMt
        cycAccess.assertIsa(damlProperty,
                            cycAccess.getKnownConstantByName("DamlConstant"),
                            cycAccess.bookkeepingMt);
        // assert (#$damlOntology damlProperty ontologyURL) in BookkeepingMt
        cycAccess.assertGaf(cycAccess.bookkeepingMt,
                            cycAccess.getKnownConstantByName("damlOntology"),
                            damlProperty,
                            new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                       damlOntology));
        // assert (#$damlURI damlProperty uri) in BookkeepingMt
        cycAccess.assertGaf(cycAccess.bookkeepingMt,
                            cycAccess.getKnownConstantByName("damlURI"),
                            damlProperty,
                            new CycNart(cycAccess.getKnownConstantByName("URLFn"),
                                       damlURI));
    }

    /**
     * Gets the asserted mappings between DAML/RDFS/RDF terms and Cyc terms.
     */
    protected void getMappings ()
        throws IOException, UnknownHostException, CycApiException {
        getMappings("BaseKB");
    }

    /**
     * Gets the asserted mappings between DAML/RDFS/RDF terms and Cyc terms.
     *
     * @param mappingMt the microtheory from which the assertions are gathered
     */
    protected void getMappings (String mappingMt)
        throws IOException, UnknownHostException, CycApiException {
        equivalentDamlCycTerms = new HashMap();
        CycList mappings =
            cycAccess.getSynonymousExternalConcepts("WorldWideWeb-DynamicIndexedInfoSource",
                                                    mappingMt);
        for (int i = 0; i < mappings.size(); i++) {
            CycList pair = (CycList) mappings.get(i);
            CycFort cycTerm = (CycFort) pair.first();
            String damlTerm = (String) pair.second();
            Log.current.println(damlTerm + " --> " + cycTerm.cyclify());
            equivalentDamlCycTerms.put(damlTerm, cycTerm);
        }
    }

    /**
     * Initializes the DAML ontology vocabulary if not present.
     */
    public void initializeCommonDamlVocabulary ()
        throws IOException, UnknownHostException, CycApiException {
        if (verbosity > 1)
            Log.current.println("Creating common DAML vocabulary");
        if (cycAccess.isOpenCyc()) {
            cycAccess.setCyclist("CycAdministrator");
            cycAccess.setKePurpose("OpenCycProject");
        }
        else {
            cycAccess.setCyclist("SteveReed");
            cycAccess.setKePurpose("DAMLProject");
        }
        // #$DamlFort
        String term = "DamlFort";
        String comment = "The KB subset collection of DAML terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "CycLReifiableDenotationalTerm");

        // DamlConstant
        term = "DamlConstant";
        comment = "The KB subset collection of DAML terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlFort");

        // #$DamlNart
        term = "DamlNart";
        comment = "The KB subset collection of DAML refified functional terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlFort");

        // #$DamlDatatypeProperty
        cycAccess.createCollection(
            "DamlDatatypeProperty",
            "The collection of #$Predicates having a " +
            "SubLAtomicTerm as the second argument.",
            "BaseKB",
            "PredicateCategory",
            "IrreflexiveBinaryPredicate");

        // #$DamlObjectProperty
        cycAccess.createCollection(
            "DamlObjectProperty",
            "The collection of #$Predicates not having a " +
            "SubLAtomicTerm as the second argument.",
            "BaseKB",
            "PredicateCategory",
            "BinaryPredicate");

        // #$DamlAnonymousClass
        cycAccess.createCollection(
            "DamlAnonymousClass",
            "The collection of DAML anonymous classes not having a " +
            "Uniform Resource Identifier (URI).",
            "BaseKB",
            "ObjectType",
            "IndeterminateTerm");

        if (cycAccess.find("WorldWideWeb-DynamicIndexedInfoSource") == null)
            // #$WorldWideWeb-DynamicIndexedInfoSource
            cycAccess.createIndividual(
                "WorldWideWeb-DynamicIndexedInfoSource",
                "The WorldWideWeb-DynamicIndexedInfoSource is an instance of " +
                "DynamicIndexedInfoSource. It is all of the information content " +
                "of the WorldWideWeb-Concrete.",
                "BaseKB",
                "IndexedInformationSource");

        // #$URLFn
        cycAccess.createIndivDenotingUnaryFunction(
            "URLFn",
            "An instance of both IndividualDenotingFunction and ReifiableFunction. " +
            "Given a URL string as its single argument, URLFn returns the corresponding " +
            "instance of UniformResourceLocator.",
            "BaseKB",
            "CharacterString",
            "UniformResourceLocator");

        // #$damlOntology
        String genlPreds = null;
        if (! cycAccess.isOpenCyc())
            genlPreds = "salientURL";
        cycAccess.createBinaryPredicate(
            "damlOntology",
            // predicate type
            null,
            // comment
            "A predicate relating an imported DAML (Darpa " +
            "Agent Markup Language) concept with its source" +
            "URL document.",
            // arg1Isa
            "CycLReifiableDenotationalTerm",
            // arg2Isa
            "UniformResourceLocator",
            // arg1Format
            null,
            // arg2Format
            "SingleEntry",
            genlPreds,
            // genFormatString
            "~a's DAML ontology URL is ~a",
            // genFormatList
            "()");

        // #$damlURI
        cycAccess.createBinaryPredicate(
            "damlURI",
            // predicate type
            null,
            // comment
            "A predicate relating an imported DAML (Darpa " +
            "Agent Markup Language) concept with its source" +
            "Uniform Resource Identifier.",
            // arg1Isa
            "CycLReifiableDenotationalTerm",
            // arg2Isa
            "UniformResourceLocator",
            // arg1Format
            null,
            // arg2Format
            "SingleEntry",
            // genlPreds
            "damlOntology",
            // genFormatString
            "~a's DAML URI is ~a",
            // genFormatList
            "()");

        // #$xmlNameSpace
        cycAccess.createBinaryPredicate(
            "xmlNameSpace",
            // predicate type
            null,
            // comment
            "A predicate relating an imported XML namespace string with its " +
            "source Uniform Resource Identifier.",
            // arg1Isa
            "SubLString",
            // arg2Isa
            "UniformResourceLocator",
            // arg1Format
            null,
            // arg2Format
            "SingleEntry",
            // genlPreds
            "conceptuallyRelated",
            // genFormatString
            "~a is an abbreviated reference for the xml namespace of ~a",
            // genFormatList
            "()");
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
        ImportDaml parent;
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
        CycFort equivalentDamlCycTerm;
        CycFort cycFort;

        /**
         * Constructs a new DamlTermInfo object.
         */
        public DamlTermInfo(ImportDaml parent) {
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
                return equivalentDamlCycTerm.cyclify();
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


    /**
     * Records property use restrictions which get imported
     * as Cyc interArgIsa1-2 or interArgFormat1-2 assertions.
     */
    protected class DamlRestriction {

        /**
         * the parent ImportDaml object
         */
        ImportDaml importDaml;

        /**
         * Identifies all the RDF triples which contribute to this DAML
         * Restriction.
         */
        public String anonymousId;

        /**
         * The domain (Cyc arg1) class whose intstances are the subject of the property.
         */
        public CycFort fromClass;

        /**
         * The property (Cyc predicate arg0) which relates the subject and predicate instances.
         */
        public CycFort property;

        /**
         * The range (Cyc arg2) classes whose instances may be objects of the property in the
         * cases where subject is an instance of the given fromClass.
         */
        public ArrayList toClasses = new ArrayList();

        /**
         * the maxCardinality restriction
         */
        public Integer maxCardinality;

        /**
         * the interArgIsa constraint sentence
         */
        public CycList interArgIsaConstraint;

        /**
         * the interArgFormat constraint sentence
         */
        public CycList interArgFormatConstraint;

        /**
         * Constructs a new DamlRestriction object.
         */
        public DamlRestriction (ImportDaml importDaml) {
            this.importDaml = importDaml;
        }

        /**
         * Forms the restriction constraints.
         */
        protected void formInterArgConstraints()
            throws IOException, UnknownHostException, CycApiException {
            if (maxCardinality != null)
                formInterArgFormatConstraint();
            if (toClasses.size() > 0)
                formInterArgIsaConstraint();
        }

        /**
         * Forms the interArgIsa1-2 constraint.
         */
        protected void formInterArgIsaConstraint()
            throws IOException, UnknownHostException, CycApiException {
            if (toClasses.size() == 1) {
                if (! cycAccess.isBinaryPredicate((CycConstant) property)) {
                    cycAccess.assertIsaBinaryPredicate(property);
                    Log.current.println("*** forward reference to predicate " + property.cyclify());
                }
                CycFort toClass = (CycFort) toClasses.get(0);
                if (! cycAccess.isCollection(toClass)) {
                    cycAccess.assertIsaCollection(toClass);
                    Log.current.println("*** forward reference to collection " + toClass.cyclify());
                }
                String interArgIsaConstraintString =
                    "(#$interArgIsa1-2 " +
                    property.cyclify() + " " +
                    fromClass.cyclify() + " " +
                    toClass.cyclify() + ")";
                interArgIsaConstraint = cycAccess.makeCycList(interArgIsaConstraintString);
            }
            else
                //TODO
                throw new RuntimeException("Unhandled interArgIsa restriction case: " +
                                           toClasses.toString());
        }

        /**
         * Forms the interArgFormat1-2 constraint.
         */
        protected void formInterArgFormatConstraint()
            throws IOException, UnknownHostException, CycApiException {
            if (this.maxCardinality.intValue() == 1) {
                String interArgFormatConstraintString =
                    "(#$interArgFormat1-2 " +
                    property.cyclify() + " " +
                    fromClass.cyclify() + " " +
                    "#$SingleEntry)";
                interArgFormatConstraint = cycAccess.makeCycList(interArgFormatConstraintString);
            }
            else
                //TODO
                throw new RuntimeException("Unhandled maxCardinality restriction case: " +
                                           maxCardinality.toString());
        }

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
            stringBuffer.append(" ");
            stringBuffer.append(property);
            stringBuffer.append(" (");
            for (int i = 0; i < toClasses.size(); i++) {
                if (i > 0)
                    stringBuffer.append(", ");
                stringBuffer.append(toClasses.get(i).toString());
            }
            stringBuffer.append(") ");
            if (interArgIsaConstraint != null)
                stringBuffer.append(interArgIsaConstraint.cyclify());
            return stringBuffer.toString();
        }
    }
    /**
     * Implements a SAX ErrorHandler to report the XML parsing errors.
     */
    protected class MyErrorHandler implements ErrorHandler {

        /**
         * Constructs a new MyErrorHandler object.
         */
        public MyErrorHandler () {
        }

        /**
         * Handles SAX XML parsing warnings.
         */
        public void warning(SAXParseException exception) {
            Log.current.println("SAX warning: " + exception.getMessage());
        }

        /**
         * Handles SAX XML parsing errors.
         */
        public void error(SAXParseException exception) {
            Log.current.println("SAX error: " + exception.getMessage());
        }

        /**
         * Handles SAX XML parsing fatal errors.
         */
        public void fatalError(SAXParseException exception) {
            Log.current.println("SAX fatal error: " + exception.getMessage());
        }
    }



}