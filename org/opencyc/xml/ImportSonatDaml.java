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

public class ImportSonatDaml extends ImportDaml {

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
            importSonatDaml.doImport();
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Import the SONAT DAML ontologies into Cyc.
     */
    protected void doImport ()
        throws IOException, UnknownHostException, CycApiException {

        initializeDocumentsToImport();

        initializeCommonOntologyNicknames();
        initializeOntologyNicknames();

        initializeCommonMappedTerms();
        initializeMappedTerms();

        //actuallyImport = false;
        for (int i = 33; i < 35; i++) {
        //for (int i = 0; i < damlDocInfos.size(); i++) {
            DamlDocInfo damlDocInfo = (DamlDocInfo) damlDocInfos.get(i);
            String damlPath = damlDocInfo.getDamlPath();
            String importMt = damlDocInfo.getImportMt();
            if(actuallyImport) {
                initializeDamlOntologyMt(importMt);
                cycAccess.assertGenlMt(importMt, damlSonatSpindleHeadMt);
                cycAccess.assertGenlMt(damlSonatSpindleCollectorMt, importMt);
            }
            initialize();
            importDaml(damlPath, importMt);
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
    }

    protected void initializeMappedTerms ()
        throws IOException, UnknownHostException, CycApiException {
        assertMapping(
                "soci:Religion",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Religion",
                "Religion");

        assertMapping(
                "soci:EthnicGroups",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml#EthnicGroups",
                "PersonTypeByEthnicity");

        assertMapping(
                "soci:Population",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Population",
                "(#$PopulationOfTypeFn #$Person)");

        assertMapping(
                "poli:GovernmentType",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml#GovernmentType",
                "SystemOfGovernment");

        assertMapping(
                "poli:PoliticalParty",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PoliticalParty",
                "PoliticalParty");
        assertMapping(
                "poli:PressureGroup",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PressureGroup",
                "PoliticalInterestGroup");

        assertMapping(
                "econ:Industry",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Industry",
                "Industry-Localized");

        assertMapping(
                "econ:Agriculture",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Agriculture",
                "AgriculturalEconomicSector");

        assertMapping(
                "econ:Services",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Services",
                "ServiceEconomicSector");

        assertMapping(
                "econ:NaturalResource",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#NaturalResource",
                "NaturalResource");

        assertMapping(
                "infr:EducationAndScience",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#EducationAndScience",
                "(#$CollectionUnionFn \n" +
                "  (#$InfrastructureFn (#$OfFn #$EducationalOrganization)) \n" +
                "  (#$InfrastructureFn (#$OfFn #$AcademicOrganization)) \n" +
                "  (#$InfrastructureFn (#$OfFn #$Science)))");

        assertMapping(
                "infr:Airport",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Airport",
                "(#$InfrastructureFn (#$OfFn #$Airport-Physical))");

        assertMapping(
                "infr:Bridge",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Bridge",
                "(#$InfrastructureFn (#$OfFn Bridge");

        assertMapping(
                "infr:Railroad",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Railroad",
                "(#$InfrastructureFn (#$OfFn #$Railway))");

        assertMapping(
                "infr:Highway",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Highway",
                "(#$InfrastructureFn (#$OfFn #$Highway))");

        assertMapping(
                "infr:Port",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Port",
                "(#$InfrastructureFn (#$OfFn #$PortFacility))");

        assertMapping(
                "infr:OilRefinery",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#OilRefinery",
                "(#$InfrastructureFn (#$OfFn #$OilRefinery))");

        assertMapping(
                "infr:PowerGrid",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#PowerGrid",
                "(#$InfrastructureFn (#$OfFn #$ElectricalPowerGrid");

        assertMapping(
                "infr:College",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.dam",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#College",
                "(#$InfrastructureFn (#$OfFn #$College))");

        assertMapping(
                "infr:GradeSchool",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#GradeSchool",
                "(#$InfrastructureFn (#$OfFn #$ElementarySchoolInstitution))");

        assertMapping(
                "infr:HighSchool",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HighSchool",
                "(#$InfrastructureFn (#$OfFn #$HighSchoolInstitution))");

        assertMapping(
                "infr:JuniorCollege",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#JuniorCollege",
                "(#$InfrastructureFn (#$OfFn #$College-2Year))");

        assertMapping(
                "infr:Kindergarden",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Kindergarden",
                "(#$InfrastructureFn (#$OfFn #$KindergartenInstitution))");

        assertMapping(
                "infr:School",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#School",
                "(#$InfrastructureFn (#$OfFn #$AcademicOrganization))");

        assertMapping(
                "infr:SecondarySchool",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#SecondarySchool",
                "(#$InfrastructureFn (#$OfFn #$HighSchoolInstitution))");

        assertMapping(
                "infr:AstronomicalStation",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AstronomicalStation",
                "(#$InfrastructureFn (#$OfFn #$AstronomicalObservatory))");

        assertMapping(
                "infr:AtomicCenter",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AtomicCenter",
                "(#$InfrastructureFn (#$OfFn #$NuclearWeaponResearchFacility))");

        assertMapping(
                "infr:BoatYard",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#BoatYard",
                "(#$InfrastructureFn (#$OfFn #$Shipyard))");

        assertMapping(
                "infr:Camp",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Camp",
                "(#$InfrastructureFn (#$OfFn #$Campsite))");

        assertMapping(
                "infr:Capital",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Capital",
                "(#$InfrastructureFn (#$OfFn #$CapitalCityOfRegion))");

        assertMapping(
                "infr:City",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#City",
                "(#$InfrastructureFn (#$OfFn #$City))");

        assertMapping(
                "infr:Clinic",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Clinic",
                "(#$InfrastructureFn (#$OfFn #$MedicalClinic))");

        assertMapping(
                "infr:Dam",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dam",
                "(#$InfrastructureFn (#$OfFn #$Dam))");

        assertMapping(
                "infr:Dike",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dike",
                "(#$InfrastructureFn (#$OfFn #$Dike))");

        assertMapping(
        "infr:Distribution",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                      "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Distribution",
                      "(#$InfrastructureFn (#$OfFn #$ProductDistributionOrganization))");

        assertMapping(
                "infr:Dock",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dock",
                "(#$InfrastructureFn (#$OfFn #$Dock))");


        assertMapping(
                "infr:Electricity",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Electricity",
                "(#$InfrastructureFn (#$OfFn #$ElectricalPowerGeneration");

        assertMapping(
                "infr:Ferry",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Ferry",
                "(#$InfrastructureFn (#$OfFn #$Ferry))");

        assertMapping(
                "infr:Fuel",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Fuel",
                "(#$InfrastructureFn (#$OfFn #$FossilFuel))");

        assertMapping(
                "infr:FuelDepot",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#FuelDepot",
                "(#$InfrastructureFn (#$OfFn #$FuelTank))");

        assertMapping(
                "infr:Harbor",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Harbor",
                "(#$InfrastructureFn (#$OfFn #$Harbor))");

        assertMapping(
                "infr:Hospital",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Hospital",
                "(#$InfrastructureFn (#$OfFn #$Hospital))");

        assertMapping(
                "infr:HydroElectric",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HydroElectric",
                "(#$InfrastructureFn (#$OfFn #$Hydropower))");

        assertMapping(
                "infr:Infrastructure",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Infrastructure",
                "(#$InfrastructureFn)");

        assertMapping(
                "infr:InfrastructureFacility",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InfrastructureFacility",
                "(#$InfrastructureFn (#$OfFn #$ConstructionArtifact))");

        assertMapping(
                "infr:InlandWaterway",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InlandWaterway",
                "(#$InfrastructureFn (#$OfFn #$(#$CollectionUnionFn #$River #$Canal #$Lake)))");

/*
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
     * Initializes the DAML ontology vocabulary if not present.
     */
    protected void initializeDamlVocabulary ()
        throws IOException, UnknownHostException, CycApiException {
        Log.current.println("Creating SONAT DAML vocabulary");
        // DamlSonatConstant
        String term = "DamlSonatConstant";
        String comment = "The KB subset collection of DAML SONAT terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlConstant");

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