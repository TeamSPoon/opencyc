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
            cycAccess = new CycAccess("TURING",
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
        for (int i = 33; i < 35; i++) {
        //for (int i = 0; i < damlDocInfos.size(); i++) {
            DamlDocInfo damlDocInfo = (DamlDocInfo) damlDocInfos.get(i);
            String damlPath = damlDocInfo.getDamlPath();
            String importMt = damlDocInfo.getImportMt();
            if(importDaml.actuallyImport) {
                initializeDamlOntologyMt(importMt);
                cycAccess.assertGenlMt(importMt, damlSonatSpindleHeadMt);
                cycAccess.assertGenlMt(damlSonatSpindleCollectorMt, importMt);
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
        // 33
        damlDocInfos.add(new DamlDocInfo("file:///H:/OpenCyc/open-directory.daml",
                                         "OpenDirectoryStructureMt"));
        // 34
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

        ontologyNicknames.put("http://xmlns.com/foaf/0.1", "foaf");
        ontologyNicknames.put("http://xmlns.com/foaf/0.1/", "foaf");
        ontologyNicknames.put("http://xmlns.com/foaf", "foaf");
        ontologyNicknames.put("http://xmlns.com/wot/0.1", "wot");
        ontologyNicknames.put("http://xmlns.com/wordnet/1.6", "wn");
        ontologyNicknames.put("http://www.w3.org/2001/08/rdfweb", "rdfweb");
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



/*
        // SONAT Mappings
        assertMapping("soci:Religion",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Religion",
                      "Religion");

        assertMapping("soci:EthnicGroups",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml#EthnicGroups",
                      "PersonTypeByEthnicity");

        assertMapping("soci:Population",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Population",
                      "(#$PopulationOfTypeFn #$Person)");

        assertMapping("poli:GovernmentType",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml#GovernmentType",
                      "SystemOfGovernment");

        assertMapping("poli:PoliticalParty",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PoliticalParty",
                      "PoliticalParty");

        assertMapping("poli:PressureGroup",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PressureGroup",
                      "PoliticalInterestGroup");

        assertMapping("econ:Industry",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Industry",
                      "Industry-Localized");

        assertMapping("econ:Agriculture",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Agriculture",
                      "AgriculturalEconomicSector");

        assertMapping("econ:Services",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Services",
                      "ServiceEconomicSector");

        assertMapping("econ:NaturalResource",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#NaturalResource",
                      "NaturalResource");

        assertMapping("infr:EducationAndScience",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#EducationAndScience",
                      "(#$CollectionUnionFn \n" +
                      "  (#$InfrastructureFn (#$OfFn #$EducationalOrganization)) \n" +
                      "  (#$InfrastructureFn (#$OfFn #$AcademicOrganization)) \n" +
                      "  (#$InfrastructureFn (#$OfFn #$Science)))");

        assertMapping("infr:Airport",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Airport",
                      "(#$InfrastructureFn (#$OfFn #$Airport-Physical))");

        assertMapping("infr:Bridge",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Bridge",
                      "(#$InfrastructureFn (#$OfFn Bridge");

        assertMapping("infr:Railroad",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Railroad",
                      "(#$InfrastructureFn (#$OfFn #$Railway))");

        assertMapping("infr:Highway",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Highway",
                      "(#$InfrastructureFn (#$OfFn #$Highway))");

        assertMapping("infr:Port",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Port",
                      "(#$InfrastructureFn (#$OfFn #$PortFacility))");

        assertMapping("infr:OilRefinery",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#OilRefinery",
                      "(#$InfrastructureFn (#$OfFn #$OilRefinery))");

        assertMapping("infr:PowerGrid",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#PowerGrid",
                      "(#$InfrastructureFn (#$OfFn #$ElectricalPowerGrid");

        assertMapping("infr:College",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.dam",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#College",
                      "(#$InfrastructureFn (#$OfFn #$College))");

        assertMapping("infr:GradeSchool",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#GradeSchool",
                      "(#$InfrastructureFn (#$OfFn #$ElementarySchoolInstitution))");

        assertMapping("infr:HighSchool",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HighSchool",
                      "(#$InfrastructureFn (#$OfFn #$HighSchoolInstitution))");

        assertMapping("infr:JuniorCollege",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#JuniorCollege",
                      "(#$InfrastructureFn (#$OfFn #$College-2Year))");

        assertMapping("infr:Kindergarden",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Kindergarden",
                      "(#$InfrastructureFn (#$OfFn #$KindergartenInstitution))");

        assertMapping("infr:School",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#School",
                      "(#$InfrastructureFn (#$OfFn #$AcademicOrganization))");

        assertMapping("infr:SecondarySchool",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#SecondarySchool",
                      "(#$InfrastructureFn (#$OfFn #$HighSchoolInstitution))");

        assertMapping("infr:AstronomicalStation",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AstronomicalStation",
                      "(#$InfrastructureFn (#$OfFn #$AstronomicalObservatory))");

        assertMapping("infr:AtomicCenter",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AtomicCenter",
                      "(#$InfrastructureFn (#$OfFn #$NuclearWeaponResearchFacility))");

        assertMapping("infr:BoatYard",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#BoatYard",
                      "(#$InfrastructureFn (#$OfFn #$Shipyard))");

        assertMapping("infr:Camp",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Camp",
                      "(#$InfrastructureFn (#$OfFn #$Campsite))");

        assertMapping("infr:Capital",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Capital",
                      "(#$InfrastructureFn (#$OfFn #$CapitalCityOfRegion))");

        assertMapping("infr:City",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#City",
                      "(#$InfrastructureFn (#$OfFn #$City))");

        assertMapping("infr:Clinic",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Clinic",
                      "(#$InfrastructureFn (#$OfFn #$MedicalClinic))");

        assertMapping("infr:Dam",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dam",
                      "(#$InfrastructureFn (#$OfFn #$Dam))");

        assertMapping("infr:Dike",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dike",
                      "(#$InfrastructureFn (#$OfFn #$Dike))");

        assertMapping("infr:Distribution",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Distribution",
                      "(#$InfrastructureFn (#$OfFn #$ProductDistributionOrganization))");

        assertMapping("infr:Dock",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dock",
                      "(#$InfrastructureFn (#$OfFn #$Dock))");


        assertMapping("infr:Electricity",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Electricity",
                      "(#$InfrastructureFn (#$OfFn #$ElectricalPowerGeneration");

        assertMapping("infr:Ferry",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Ferry",
                      "(#$InfrastructureFn (#$OfFn #$Ferry))");

        assertMapping("infr:Fuel",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Fuel",
                      "(#$InfrastructureFn (#$OfFn #$FossilFuel))");

        assertMapping("infr:FuelDepot",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#FuelDepot",
                      "(#$InfrastructureFn (#$OfFn #$FuelTank))");

        assertMapping("infr:Harbor",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Harbor",
                      "(#$InfrastructureFn (#$OfFn #$Harbor))");

        assertMapping("infr:Hospital",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Hospital",
                      "(#$InfrastructureFn (#$OfFn #$Hospital))");

        assertMapping("infr:HydroElectric",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HydroElectric",
                      "(#$InfrastructureFn (#$OfFn #$Hydropower))");

        assertMapping("infr:Infrastructure",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Infrastructure",
                      "(#$InfrastructureFn)");

        assertMapping("infr:InfrastructureFacility",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InfrastructureFacility",
                      "(#$InfrastructureFn (#$OfFn #$ConstructionArtifact))");

        assertMapping("infr:InlandWaterway",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InlandWaterway",
                      "(#$InfrastructureFn (#$OfFn #$(#$CollectionUnionFn #$River #$Canal #$Lake)))");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

        assertMapping("",
                      "",
                      "",
                      "");

*/

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
                                                  "DamlSonatSpindleHeadMt");
        CycFort damlTerm = cycAccess.findOrCreate(damlTermName);
        if (quickTesting)
            return;
        cycAccess.assertComment(damlTerm,
                                damlTerm.cyclify() +
                                " is an imported DAML/XML term equivalent to the Cyc term " +
                                cycTerm.cyclify(),
                                cycAccess.getKnownConstantByName("DamlSonatSpindleHeadMt"));
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
                                                  "DamlSonatSpindleHeadMt");
        //TODO assert synonymousRelnArgs

        CycFort damlProperty = cycAccess.findOrCreate(damlPropertyName);
        cycAccess.assertComment(damlProperty,
                                damlProperty.cyclify() +
                                " is an imported DAML/XML property equivalent to the Cyc predicate " +
                                cycBinaryPredicate.cyclify() +
                                " (with the arguments reversed).",
                                cycAccess.getKnownConstantByName("DamlSonatSpindleHeadMt"));
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

        if (cycAccess.find("DamlSonatSpindleHeadMt") == null) {
            // #$DamlSonatSpindleHeadMt
            ArrayList genlMts = new ArrayList();
            genlMts.add("BaseKB");
            cycAccess.createMicrotheory(
                "DamlSonatSpindleHeadMt",
                "The microtheory which is superior to all the DAML SONAT " +
                "ontology microtheories.",
                "Microtheory",
                genlMts);
        }

        if (cycAccess.find("DamlSonatSpindleCollectorMt") == null) {
            // #$DamlSonatSpindleHeadMt
            ArrayList genlMts = new ArrayList();
            cycAccess.createMicrotheory(
                "DamlSonatSpindleCollectorMt",
                "The microtheory which is inferior to all the DAML SONAT " +
                "ontology microtheories.",
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
/*
        // #$UsedInEventFn
        cycAccess.createCollectionDenotingBinaryFunction(
            // function
            "UsedInEventFn",
            // comment
            "An instance of both CollectionDenotingFunction and ReifiableFunction. " +
            "(#$UsedInEventFn COLLECTION EVENT-TYPE) results in the subcollection of " +
            " COLLECTION in which COLLECTION instances are used as the " +
            "#$instrument-Generic role in some event instances of EVENT-TYPE.",
            // comment mt
            "BaseKB",
            // arg1Isa
            null,
            // arg2Isa
            null,
            // arg1Genl
            "PartiallyTangible",
            // arg2Genl
            "Event",
            // resultIsa
            "Collection");
*/
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