package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Imports DAML xml content for the Open Directory Structure.<p>
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

public class ImportOpenDirectoryStructure extends ImportDaml {

    /**
     * When true, bypasses some KB assertion activities.
     */
    //public boolean quickTesting = true;
    public boolean quickTesting = false;

    /**
     * the list of DAML documents and import microtheories
     */
    protected ArrayList damlDocInfos = new ArrayList();

    /**
     * CycAccess object to manage api connection the the Cyc server
     */
    protected CycAccess cycAccess;

    /**
     * Ontology library nicknames, which become namespace identifiers
     * upon import into Cyc.
     * namespace uri --> ontologyNickname
     */
    protected HashMap ontologyNicknames = new HashMap();

    /**
     * Cyc terms which have semantic counterparts in DAML.
     * DAML term --> Cyc term
     */
    protected HashMap equivalentDamlCycTerms;

    /**
     * the name of the KB Subset collection which identifies ontology import
     * terms in Cyc
     */
    protected String kbSubsetCollectionName = "DamlOdpConstant";

    /**
     * Constructs a new ImportOpenDirectoryStructure object.
     */
    public ImportOpenDirectoryStructure()
        throws IOException, UnknownHostException, CycApiException {
    }

    /**
     * Provides the main method for the ImportOdpDaml application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Log.makeLog("import-opend-directory-daml.log");
        Log.current.println("Import DAML starting");
        ImportOpenDirectoryStructure importOpenDirectoryStructure;
        try {

            importOpenDirectoryStructure = new ImportOpenDirectoryStructure();
            importOpenDirectoryStructure.initializeOntologyNicknames();
            importOpenDirectoryStructure.gatherOdpTitles();
            importOpenDirectoryStructure.initializeDamlVocabulary();
            importOpenDirectoryStructure.importDaml();
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Gathers the ODP topic titles.
     */
    protected void gatherOdpTitles () throws IOException {
        GatherOpenDirectoryTitles gatherOpenDirectoryTitles =
            new GatherOpenDirectoryTitles(ontologyNicknames);
        gatherOpenDirectoryTitles.gatherTitles("file:///H:/OpenCyc/open-directory-structure.daml");
    }

    /**
     * Import the ODP DAML ontologies into Cyc.
     */
    protected void importDaml ()
        throws IOException, UnknownHostException, CycApiException {

        initializeDocumentsToImport();
        initializeMappedTerms();
        //importDaml.actuallyImport = false;
        for (int i = 0; i < damlDocInfos.size(); i++) {
            DamlDocInfo damlDocInfo = (DamlDocInfo) damlDocInfos.get(i);
            String damlPath = damlDocInfo.getDamlPath();
            String importMt = damlDocInfo.getImportMt();
            initializeDamlOntologyMt(importMt);
            initialize();
            importDaml(damlPath, importMt);
        }
    }

    /**
     * Initializes the documents to import.
     */
    protected void initializeDocumentsToImport () {
        // 0
        damlDocInfos.add(new DamlDocInfo("file:///H:/OpenCyc/open-directory.daml",
                                         "OpenDirectoryStructureMt"));
        // 1
        damlDocInfos.add(new DamlDocInfo("file:///H:/OpenCyc/open-directory-structure.daml",
                                         "OpenDirectoryStructureMt"));
    }

    /**
     * Initializes the Ontology nicknames mapping.
     */
    protected void initializeOntologyNicknames () {
        ontologyNicknames.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
        ontologyNicknames.put("http://www.w3.org/2000/01/rdf-schema", "rdfs");
        ontologyNicknames.put("http://www.w3.org/2000/10/XMLSchema", "xsd");
        // DMOZ stale reference
        ontologyNicknames.put("http://www.w3.org/TR/RDF/", "rdf");
        ontologyNicknames.put("http://www.daml.org/2001/03/daml+oil", "daml");
        ontologyNicknames.put("http://purl.org/dc/elements/1.1", "dublincore");
        ontologyNicknames.put("http://purl.org/dc/elements/1.0/", "dublincore");

        ontologyNicknames.put("http://dmoz.org/rdf", "dmoz");
        ontologyNicknames.put("http://dmoz.org/rdf/structure.example.txt", "dmoz");
        ontologyNicknames.put("file:///H:/OpenCyc/open-directory-structure.daml", "dmoz");
        ontologyNicknames.put("file:/H:/OpenCyc/open-directory-structure.daml", "dmoz");
        ontologyNicknames.put("file:/H:/OpenCyc/open-directory-structure", "dmoz");
        ontologyNicknames.put("http://opencyc.sourceforge.net/open-directory", "dmoz");
        ontologyNicknames.put("file:///H:/OpenCyc/open-directory.daml", "dmoz");
        ontologyNicknames.put("file:/H:/OpenCyc", "dmoz");
        ontologyNicknames.put("file:/H:/OpenCyc/open-directory.daml", "dmoz");

    }

    protected void initializeMappedTerms ()
        throws IOException, UnknownHostException, CycApiException {
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

        // Open Directory Mappings
        assertArgumentMapping("dmoz:narrow",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow",
                              "genls",
                              "(2 1)");

        assertArgumentMapping("dmoz:narrow1",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow1",
                              "genls",
                              "(2 1)");

        assertArgumentMapping("dmoz:narrow2",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow2",
                              "genls",
                              "(2 1)");

        assertMapping("dmoz:related",
                      "http://opencyc.sourceforge.net/open-directory",
                      "http://opencyc.sourceforge.net/open-directory#related",
                      "conceptuallyRelated");



        // Get the above mappings plus any previously defined in the KB.
        getMappings();
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
     */
    protected void assertMapping (String damlTermName,
                                  String damlOntology,
                                  String damlURI,
                                  String cycTermName)
        throws IOException, UnknownHostException, CycApiException {
        CycFort cycTerm = cycAccess.findOrCreate(cycTermName);
        Log.current.println("Mapping " + damlTermName + " to " + cycTerm.cyclify());
        cycAccess.assertSynonymousExternalConcept(cycTermName,
                                                  "WorldWideWeb-DynamicIndexedInfoSource",
                                                  damlTermName,
                                                  "OpenDirectoryStructureMt");
        CycFort damlTerm = cycAccess.findOrCreate(damlTermName);
        if (quickTesting)
            return;
        cycAccess.assertComment(damlTerm,
                                damlTerm.cyclify() +
                                " is an imported DAML/XML term equivalent to the Cyc term " +
                                cycTerm.cyclify(),
                                cycAccess.getKnownConstantByName("OpenDirectoryStructureMt"));
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
     */
    protected void assertArgumentMapping (String damlPropertyName,
                                          String damlOntology,
                                          String damlURI,
                                          String cycBinaryPredicateName,
                                          String argumentMappingList)
        throws IOException, UnknownHostException, CycApiException {
        CycFort cycBinaryPredicate = cycAccess.findOrCreate(cycBinaryPredicateName);
        Log.current.println("Mapping " + damlPropertyName + " to " + cycBinaryPredicate.cyclify());
        cycAccess.assertSynonymousExternalConcept(cycBinaryPredicateName,
                                                  "WorldWideWeb-DynamicIndexedInfoSource",
                                                  damlPropertyName,
                                                  "OpenDirectoryStructureMt");
        //TODO assert synonymousRelnArgs

        CycFort damlProperty = cycAccess.findOrCreate(damlPropertyName);
        cycAccess.assertComment(damlProperty,
                                damlProperty.cyclify() +
                                " is an imported DAML/XML property equivalent to the Cyc predicate " +
                                cycBinaryPredicate.cyclify() +
                                " (with the arguments reversed).",
                                cycAccess.getKnownConstantByName("OpenDirectoryStructureMt"));
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
        equivalentDamlCycTerms = new HashMap();
        CycList mappings =
            cycAccess.getSynonymousExternalConcepts("WorldWideWeb-DynamicIndexedInfoSource",
                                                    "OpenDirectoryStructureMt");
        for (int i = 0; i < mappings.size(); i++) {
            CycList pair = (CycList) mappings.get(i);
            CycFort cycTerm = (CycFort) pair.first();
            String damlTerm = (String) pair.second();
            Log.current.println(damlTerm + " --> " + cycTerm.toString());
            equivalentDamlCycTerms.put(damlTerm, cycTerm.toString());
        }
    }

    /**
     * Initializes the DAML ontology vocabulary if not present.
     */
    protected void initializeDamlVocabulary ()
        throws IOException, UnknownHostException, CycApiException {
        String localHostName = InetAddress.getLocalHost().getHostName();
        Log.current.println("Connecting to Cyc server from " + localHostName);
        if (localHostName.equals("crapgame.cyc.com")) {
            cycAccess = new CycAccess("localhost",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else if (localHostName.equals("thinker")) {
            cycAccess = new CycAccess("TURING",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else {
            cycAccess = new CycAccess();
        }
        Log.current.println("Creating DAML vocabulary");
        if (cycAccess.isOpenCyc()) {
            cycAccess.setCyclist("CycAdministrator");
            cycAccess.setKePurpose("OpenCycProject");
        }
        else {
            cycAccess.setCyclist("SteveReed");
            cycAccess.setKePurpose("DAMLProject");
        }
        // DamlConstant
        String term = "DamlConstant";
        String comment = "The KB subset collection of DAML terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "CycLConstant");

        // DamlOdpConstant
        term = "DamlOdpConstant";
        comment = "The KB subset collection of DAML ODP terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlConstant");

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

        if (cycAccess.find("OpenDirectoryStructureMt") == null) {
            // #$OpenDirectoryStructureMt
            ArrayList genlMts = new ArrayList();
            genlMts.add("BaseKB");
            cycAccess.createMicrotheory(
                "OpenDirectoryStructureMt",
                "The microtheory which contains imported Open Directory structure assertions.",
                "Microtheory",
                genlMts);
        }

        // #$URLFn
        cycAccess.createIndivDenotingUnaryFunction(
            "URLFn",
            "An instance of both IndividualDenotingFunction and ReifiableFunction. " +
            "Given a URL string as its single argument, URLFn returns the corresponding " +
            "instance of UniformResourceLocator.",
            "BaseKB",
            "CharacterString",
            "UniformResourceLocator");

        // #$OpenDirectoryTopicFn
        cycAccess.createCollectionDenotingUnaryFunction(
            "OpenDirectoryTopicFn",
            "An instance of both CollectionDenotingFunction and ReifiableFunction. " +
            "Given an Open Directory category ID string as its single argument, " +
            "OpenDirectoryTopicFn returns the collection of Open Directory indexed " +
            "web resources for that category.",
            "BaseKB",
            "CharacterString",
            "Collection");

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
            "DamlConstant",
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
            "DamlConstant",
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
     * Provides a container for specifying the ODP DAML document paths and
     * the Cyc import microtheory for each.
     */
    protected class DamlDocInfo {
        /**
         * path (url) to the ODP DAML document
         */
        protected String damlPath;

        /**
         * microtheory into which DAML content is imported
         */
        protected String importMt;

        public DamlDocInfo (String damlPath, String importMt) {
            this.damlPath = damlPath;
            this.importMt = importMt;
        }

        /**
         * Returns the daml document path.
         *
         * @return the daml document path
         */
        public String getDamlPath () {
            return damlPath;
        }

        /**
         * Returns the microtheory into which DAML content is imported.
         *
         * @return the microtheory into which DAML content is imported
         */
        public String getImportMt () {
            return importMt;
        }
    }
}