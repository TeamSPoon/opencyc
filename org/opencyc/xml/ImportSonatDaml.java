package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Imports DAML xml content for the DAML SONAT ontologies.<p>
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

public class ImportSonatDaml {

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
    protected String kbSubsetCollectionName = "DamlSonatConstant";

    /**
     * head of the SONAT DAML microtheory spindle
     */
    protected String damlSonatSpindleHeadMt = "DamlSonatSpindleHeadMt";

    /**
     * collector (bottom) of the SONAT DAML microtheory spindle
     */
    protected String damlSonatSpindleCollectorMt = "DamlSonatSpindleCollectorMt";


    /**
     * Constructs a new ImportSonatDaml object.
     */
    public ImportSonatDaml()
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
            cycAccess = new CycAccess("MCCARTHY",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else {
            cycAccess = new CycAccess();
        }

        initializeDamlVocabulary();
    }

    /**
     * Provides the main method for the ImportSonatDaml application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Log.makeLog("import-sonat-daml.log");
        Log.current.println("Import DAML starting");
        ImportSonatDaml importSonatDaml;
        try {
            importSonatDaml = new ImportSonatDaml();
            importSonatDaml.importDaml();
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Import the SONAT DAML ontologies into Cyc.
     */
    protected void importDaml ()
        throws IOException, UnknownHostException, CycApiException {

        initializeDocumentsToImport();
        initializeOntologyNicknames();
        initializeMappedTerms();
        ImportDaml importDaml =
            new ImportDaml(cycAccess,
                           ontologyNicknames,
                           equivalentDamlCycTerms,
                           kbSubsetCollectionName);
        //importDaml.actuallyImport = false;
        //for (int i = 29; i < damlDocInfos.size(); i++) {
        for (int i = 0; i < damlDocInfos.size(); i++) {
            DamlDocInfo damlDocInfo = (DamlDocInfo) damlDocInfos.get(i);
            String damlPath = damlDocInfo.getDamlPath();
            String importMt = damlDocInfo.getImportMt();
            if(importDaml.actuallyImport) {
                initializeDamlOntologyMt(importMt);
                if (! cycAccess.isOpenCyc()) {
                    cycAccess.assertGenlMt(importMt, damlSonatSpindleHeadMt);
                    cycAccess.assertGenlMt(damlSonatSpindleCollectorMt, importMt);
                }
            }
            importDaml.initialize();
            importDaml.importDaml(damlPath, importMt);
        }
    }

    /**
     * Initializes the documents to import.
     */
    protected void initializeDocumentsToImport () {
        // 0
        damlDocInfos.add(new DamlDocInfo("http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont.daml",
                                         "DamlSonatDrcVesOntologyMt"));
        // 1
        damlDocInfos.add(new DamlDocInfo("http://orlando.drc.com/daml/ontology/DC/3.2/dces-ont.daml",
                                         "DamlSonatDrcDcesOntologyMt"));
        // 2
        damlDocInfos.add(new DamlDocInfo("http://xmlns.com/foaf/0.1/",
                                         "DamlSonatFoafOntologyMt"));
        // 3
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/10/html/airport-ont.daml",
                                         "DamlSonatAirportOntologyMt"));
        // 4
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/09/countries/fips-10-4-ont",
                                         "DamlSonatFips10-4OntologyMt"));
        // 5
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/09/countries/fips.daml",
                                         "DamlSonatFipsOntologyMt"));
        // 6
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/09/countries/iso-3166-ont",
                                         "DamlSonatISOCountriesOntologyMt"));
        // 7
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2002/02/chiefs/chiefs-ont.daml",
                                         "DamlSonatChiefsOntologyMt"));
        // 8
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2002/02/chiefs/af.daml",
                                         "DamlSonatChiefsAfOntologyMt"));
        // 9
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/12/factbook/factbook-ont.daml",
                                         "DamlSonatCiaFactbookOntologyMt"));
        // 10
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2001/12/factbook/internationalOrganizations.daml",
                                         "DamlSonatCiaFactbookOrganizationOntologyMt"));
        // 11
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/agency-ont.daml",
                                         "DamlSonatAgencyOntologyMt"));
        // 12
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/CINC-ont.daml",
                                         "DamlSonatCincOntologyMt"));
        // 13
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/assessment-ont.daml",
                                         "DamlSonatAssessmentOntologyMt"));
        // 14
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                                         "DamlSonatEconomicElementsOntologyMt"));
        // 15
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/elements-ont.daml",
                                         "DamlSonatElementsOfNationalPowerOntologyMt"));
//new
        // 16
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/enp-characteristics.daml",
                                         "DamlSonatENPCharacteristicsOntologyMt"));
        // 17
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/entity-ont.daml",
                                         "DamlSonatMilitaryEntityOntologyMt"));
        // 18
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/entity.daml",
                                         "DamlSonatMilitaryEntityInstancesMt"));

        // 19
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/information-elements-ont.daml",
                                         "DamlSonatInformationElementsOntologyMt"));
        // 20
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                                         "DamlSonatInfrastructureElementsOntologyMt"));
        // 21
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/location-ont.daml",
                                         "DamlSonatLocationOntologyMt"));
        // 22
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/military-elements-ont.daml",
                                         "DamlSonatMilitaryElementsOntologyMt"));
        // 23
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/objectives-ont.daml",
                                         "DamlSonatObjectivesOntologyMt"));
        // 24
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/operation-ont.daml",
                                         "DamlSonatOperationOntologyMt"));
        // 25
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                                         "DamlSonatPoliticalElementsOntologyMt"));
        // 26
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                                         "DamlSonatSocialElementsOntologyMt"));
//new
        // 27
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/target-ont.daml",
                                         "DamlSonatTargetOntologyMt"));
        // 28
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/unit-ont.daml",
                                         "DamlSonatMilitaryUnitOntologyMt"));
        // 29
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/unit-status.daml",
                                         "DamlSonatMilitaryUnitStatusOntologyMt"));
        // 30
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/experiment/ontology/unit.daml",
                                         "DamlSonatMilitaryUnitInstancesMt"));
        // 31
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2002/09/milservices/milservices-ont",
                                         "DamlSonatMilitaryServicesOntologyMt"));
        // 32
        damlDocInfos.add(new DamlDocInfo("http://www.daml.org/2002/09/milservices/us",
                                         "DamlSonatUSMilitaryServicesInstancesMt"));
    }

    /**
     * Initializes the Ontology nicknames mapping.
     */
    protected void initializeOntologyNicknames () {
        ontologyNicknames.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
        ontologyNicknames.put("http://www.w3.org/2000/01/rdf-schema", "rdfs");
        ontologyNicknames.put("http://www.w3.org/2000/10/XMLSchema", "xsd");

        ontologyNicknames.put("http://www.daml.org/2001/03/daml+oil", "daml");

        ontologyNicknames.put("http://xmlns.com/foaf/0.1", "foaf");
        ontologyNicknames.put("http://xmlns.com/foaf", "foaf");
        ontologyNicknames.put("http://xmlns.com/wot/0.1", "wot");
        ontologyNicknames.put("http://xmlns.com/wordnet/1.6", "wn");
        ontologyNicknames.put("http://www.w3.org/2001/08/rdfweb", "rdfweb");
        ontologyNicknames.put("http://purl.org/dc/elements/1.1", "dublincore");
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

    protected void initializeMappedTerms ()
        throws IOException, UnknownHostException, CycApiException {
        assertMapping("daml:Thing", "Thing");
        assertMapping("rdfs:Resource", "Thing");

        assertMapping("daml:Class", "Collection");
        assertMapping("rdfs:Class", "Collection");

        assertMapping("daml:Ontology", "AbstractInformationStructure");

        assertMapping("daml:DatatypeProperty", "DamlDatatypeProperty");

        assertMapping("daml:ObjectProperty", "DamlObjectProperty");

        assertMapping("daml:Property", "BinaryPredicate");
        assertMapping("rdfs:Property", "BinaryPredicate");
        assertMapping("rdf:Property", "BinaryPredicate");

        assertMapping("daml:TransitiveProperty", "TransitiveBinaryPredicate");

        assertMapping("daml:Literal", "SubLAtomicTerm");
        assertMapping("rdfs:Literal", "SubLAtomicTerm");

        assertMapping("xsd:string", "SubLString");
        assertMapping("xsd:decimal", "SubLRealNumber");
        assertMapping("xsd:integer", "SubLInteger");
        assertMapping("xsd:float", "SubLRealNumber");
        assertMapping("xsd:double", "SubLRealNumber");
        assertMapping("xsd:date", "Date");
        assertMapping("xsd:uriReference", "UniformResourceLocator");
        assertMapping("xsd:anyURI", "UniformResourceLocator");

        // Binary predicates
        assertMapping("daml:subClassOf", "genls");
        assertMapping("rdfs:subClassOf", "genls");

        assertMapping("daml:type", "isa");
        assertMapping("rdfs:type", "isa");
        assertMapping("rdf:type", "isa");

        assertMapping("daml:subPropertyOf", "genlPreds");
        assertMapping("rdfs:subPropertyOf", "genlPreds");

        assertMapping("daml:label", "nameString");
        assertMapping("rdfs:label", "nameString");

        assertMapping("daml:comment", "comment");
        assertMapping("rdfs:comment", "comment");

        assertMapping("daml:seeAlso", "conceptuallyRelated");
        assertMapping("rdfs:seeAlso", "conceptuallyRelated");

        assertMapping("daml:isDefinedBy", "containsInformationAbout");
        assertMapping("rdfs:isDefinedBy", "containsInformationAbout");

        assertMapping("daml:domain", "arg1Isa");
        assertMapping("rdfs:domain", "arg1Isa");

        assertMapping("daml:range", "arg2Isa");
        assertMapping("rdfs:range", "arg2Isa");

        assertMapping("daml:differentIndividualFrom", "different");

        assertMapping("daml:samePropertyAs", "equalSymbols");

        assertMapping("daml:disjointWith", "different");
        getMappings();
    }


    /**
     * Asserts that the given DAML/RDFS/RDF term is mapped to the
     * given Cyc term.
     *
     * @param damlTerm the given DAML/RDFS/RDF term
     * @param cycTerm the given Cyc term
     */
    protected void assertMapping (String damlTerm, String cycTerm)
        throws IOException, UnknownHostException, CycApiException {
        cycAccess.assertSynonymousExternalConcept(cycTerm,
                                                  "WorldWideWeb-DynamicIndexedInfoSource",
                                                  damlTerm,
                                                  "DamlSonatSpindleHeadMt");
    }

    /**
     * Gets the asserted mappings between DAML/RDFS/RDF terms and Cyc terms.
     *
     */
    protected void getMappings ()
        throws IOException, UnknownHostException, CycApiException {
        equivalentDamlCycTerms = new HashMap();
        CycList mappings =
            cycAccess.getSynonymousExternalConcepts("WorldWideWeb-DynamicIndexedInfoSource",
                                                    "DamlSonatSpindleHeadMt");
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

        // DamlSonatConstant
        term = "DamlSonatConstant";
        comment = "The KB subset collection of DAML SONAT terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlConstant");

        // #$DamlDatatypeProperty
        cycAccess.createCollection("DamlDatatypeProperty",
                                   "The collection of #$Predicates having a " +
                                   "SubLAtomicTerm as the second argument.",
                                   "BaseKB",
                                   "PredicateCategory",
                                   "IrreflexiveBinaryPredicate");
        // #$DamlObjectProperty
        cycAccess.createCollection("DamlObjectProperty",
                                   "The collection of #$Predicates not having a " +
                                   "SubLAtomicTerm as the second argument.",
                                   "BaseKB",
                                   "PredicateCategory",
                                   "BinaryPredicate");

        if (cycAccess.find("WorldWideWeb-DynamicIndexedInfoSource") == null)
            // #$WorldWideWeb-DynamicIndexedInfoSource
            cycAccess.createIndividual("WorldWideWeb-DynamicIndexedInfoSource",
                                       "The WorldWideWeb-DynamicIndexedInfoSource is an instance of " +
                                       "DynamicIndexedInfoSource. It is all of the information content " +
                                       "of the WorldWideWeb-Concrete.",
                                       "BaseKB",
                                       "IndexedInformationSource");

        if (cycAccess.find("DamlSonatSpindleHeadMt") == null) {
            // #$DamlSonatSpindleHeadMt
            ArrayList genlMts = new ArrayList();
            genlMts.add("BaseKB");
            cycAccess.createMicrotheory("DamlSonatSpindleHeadMt",
                                        "The microtheory which is superior to all the DAML SONAT " +
                                        " ontology microtheories.",
                                        "Microtheory",
                                        genlMts);
        }

        // #$URLFn
        cycAccess.createIndivDenotingUnaryFunction(
            "URLFn",
            "An instance of both IndividualDenotingFunction and ReifiableFunction. " +
            "Given a URL string as its single argument, URLFn returns the correspond " +
            " instance of UniformResourceLocator.",
            "BaseKB",
            "CharacterString",
            "UniformResourceLocator");

        // #$damlOntology
        String genlPreds = null;
        if (! cycAccess.isOpenCyc())
            genlPreds = "salientURL";
        cycAccess.createBinaryPredicate("damlOntology",
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
                                        "()"
                                        );
        // #$damlURI
        cycAccess.createBinaryPredicate("damlURI",
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
                                        "()"
                                        );

        // #$xmlNameSpace
        cycAccess.createBinaryPredicate("xmlNameSpace",
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
                                        "()"
                                        );

    }

    /**
     * Initializes the DAML ontology mt.
     */
    protected void initializeDamlOntologyMt (String mtName)
        throws IOException, UnknownHostException, CycApiException {
        Log.current.println("Creating " + mtName);
        String comment = "A microtheory to contain imported SONAT DAML assertions.";
        ArrayList genlMts = new ArrayList();
        genlMts.add("BaseKB");
        String isaMtName = "SourceMicrotheory";
        cycAccess.createMicrotheory(mtName, comment, isaMtName, genlMts);
    }

    /**
     * Provides a container for specifying the SONAT DAML document paths and
     * the Cyc import microtheory for each.
     */
    protected class DamlDocInfo {
        /**
         * path (url) to the SONAT DAML document
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