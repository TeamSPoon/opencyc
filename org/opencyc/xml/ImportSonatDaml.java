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
     * the list of DAML documents and import microtheories
     */
    protected ArrayList damlDocInfos = new ArrayList();

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
            //cycAccess.traceNamesOn();
        }
        else if (localHostName.equals("thinker")) {
            cycAccess = new CycAccess("localhost",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else {
            cycAccess = new CycAccess();
        }
        initializeCommonDamlVocabulary();
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
        kbSubsetCollectionName = "DamlSonatConstant";

        for (int i = 0; i < damlDocInfos.size(); i++) {
        //for (int i = 16; i < 17; i++) {
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
        if (cycAccess.find("Religion") != null)
            assertMapping(
                    "soci:Religion",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Religion",
                    "Religion");

        if (cycAccess.find("PersonTypeByEthnicity") != null)
            assertMapping(
                    "soci:EthnicGroups",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml#EthnicGroups",
                    "PersonTypeByEthnicity");

        if (cycAccess.find("PopulationOfTypeFn") != null) {
            cycAccess.ensureWffConstraints("Person", null, "Animal");
            assertMapping(
                    "soci:Population",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/social-elements-ont.daml#Population",
                    "(#$PopulationOfTypeFn #$Person)");
        }

        if (cycAccess.find("SystemOfGovernment") != null)
            assertMapping(
                    "poli:GovernmentType",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml#GovernmentType",
                    "SystemOfGovernment");

        if (cycAccess.find("PoliticalParty") != null)
            assertMapping(
                    "poli:PoliticalParty",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PoliticalParty",
                    "PoliticalParty");

        if (cycAccess.find("PoliticalInterestGroup") != null)
            assertMapping(
                    "poli:PressureGroup",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/political-elements-ont.daml#PressureGroup",
                    "PoliticalInterestGroup");

        if (cycAccess.find("Industry-Localized") != null)
            assertMapping(
                    "econ:Industry",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Industry",
                    "Industry-Localized");

        if (cycAccess.find("") != null)
            assertMapping(
                    "econ:Agriculture",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Agriculture",
                    "AgriculturalEconomicSector");

        if (cycAccess.find("AgriculturalEconomicSector") != null)
            assertMapping(
                    "econ:Services",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#Services",
                    "ServiceEconomicSector");

        if (cycAccess.find("NaturalResource") != null)
            assertMapping(
                    "econ:NaturalResource",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/economic-elements-ont.daml#NaturalResource",
                    "NaturalResource");

        if (cycAccess.find("AcademicOrganization") != null &&
            cycAccess.find("ScientificFieldOfStudy") != null)
            assertMapping(
                    "infr:EducationAndScience",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#EducationAndScience",
                    "(#$CollectionUnionFn \n" +
                    "  (#$TheSet \n" +
                    "    (#$InfrastructureOfFn #$EducationalOrganization) \n" +
                    "    (#$InfrastructureOfFn #$AcademicOrganization) \n" +
                    "    (#$InfrastructureOfFn #$ScientificFieldOfStudy)))");

        if (cycAccess.find("Airport-Physical") != null)
            assertMapping(
                    "infr:Airport",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Airport",
                    "(#$InfrastructureOfFn #$Airport-Physical)");

        if (cycAccess.find("Bridge") != null)
            assertMapping(
                    "infr:Bridge",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Bridge",
                    "(#$InfrastructureOfFn #$Bridge)");

        if (cycAccess.find("Railway") != null)
            assertMapping(
                    "infr:Railroad",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Railroad",
                    "(#$InfrastructureOfFn #$Railway)");

        if (cycAccess.find("Highway") != null)
            assertMapping(
                    "infr:Highway",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Highway",
                    "(#$InfrastructureOfFn #$Highway)");

        if (cycAccess.find("PortFacility") != null)
            assertMapping(
                    "infr:Port",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Port",
                    "(#$InfrastructureOfFn #$PortFacility)");

        if (cycAccess.find("OilRefinery") != null)
            assertMapping(
                    "infr:OilRefinery",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#OilRefinery",
                    "(#$InfrastructureOfFn #$OilRefinery)");

        if (cycAccess.find("ElectricalPowerGrid") != null)
            assertMapping(
                    "infr:PowerGrid",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#PowerGrid",
                    "(#$InfrastructureOfFn #$ElectricalPowerGrid)");

        if (cycAccess.find("College") != null)
            assertMapping(
                    "infr:College",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.dam",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#College",
                    "(#$InfrastructureOfFn #$College)");

        if (cycAccess.find("ElementarySchoolInstitution") != null)
            assertMapping(
                    "infr:GradeSchool",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#GradeSchool",
                    "(#$InfrastructureOfFn #$ElementarySchoolInstitution)");

        if (cycAccess.find("HighSchoolInstitution") != null)
            assertMapping(
                    "infr:HighSchool",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HighSchool",
                    "(#$InfrastructureOfFn #$HighSchoolInstitution)");

        if (cycAccess.find("College-2Year") != null)
            assertMapping(
                    "infr:JuniorCollege",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#JuniorCollege",
                    "(#$InfrastructureOfFn #$College-2Year)");

        if (cycAccess.find("KindergartenInstitution") != null)
            assertMapping(
                    "infr:Kindergarden",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Kindergarden",
                    "(#$InfrastructureOfFn #$KindergartenInstitution)");

        if (cycAccess.find("AcademicOrganization") != null)
            assertMapping(
                    "infr:School",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#School",
                    "(#$InfrastructureOfFn #$AcademicOrganization)");

        if (cycAccess.find("HighSchoolInstitution") != null)
            assertMapping(
                    "infr:SecondarySchool",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#SecondarySchool",
                    "(#$InfrastructureOfFn #$HighSchoolInstitution)");

        if (cycAccess.find("AstronomicalObservatory") != null)
            assertMapping(
                    "infr:AstronomicalStation",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AstronomicalStation",
                    "(#$InfrastructureOfFn #$AstronomicalObservatory)");

        if (cycAccess.find("NuclearWeaponResearchFacility") != null)
            assertMapping(
                    "infr:AtomicCenter",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#AtomicCenter",
                    "(#$InfrastructureOfFn #$NuclearWeaponResearchFacility)");

        if (cycAccess.find("Shipyard") != null)
            assertMapping(
                    "infr:BoatYard",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#BoatYard",
                    "(#$InfrastructureOfFn #$Shipyard)");

        if (cycAccess.find("Campsite") != null)
            assertMapping(
                    "infr:Camp",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Camp",
                    "(#$InfrastructureOfFn #$Campsite)");

        if (cycAccess.find("CapitalCityOfRegion") != null)
            assertMapping(
                    "infr:Capital",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Capital",
                    "(#$InfrastructureOfFn #$CapitalCityOfRegion)");

        if (cycAccess.find("City") != null)
            assertMapping(
                    "infr:City",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#City",
                    "(#$InfrastructureOfFn #$City)");

        if (cycAccess.find("MedicalClinic") != null)
            assertMapping(
                    "infr:Clinic",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Clinic",
                    "(#$InfrastructureOfFn #$MedicalClinic)");

        if (cycAccess.find("Dam") != null)
            assertMapping(
                    "infr:Dam",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dam",
                    "(#$InfrastructureOfFn #$Dam)");

        if (cycAccess.find("Dike") != null)
        assertMapping(
                "infr:Dike",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dike",
                "(#$InfrastructureOfFn #$Dike)");

        if (cycAccess.find("ProductDistributionOrganization") != null)
            assertMapping(
                    "infr:Distribution",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Distribution",
                    "(#$InfrastructureOfFn #$ProductDistributionOrganization)");

        if (cycAccess.find("Dock") != null)
            assertMapping(
                    "infr:Dock",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Dock",
                    "(#$InfrastructureOfFn #$Dock)");


        if (cycAccess.find("ElectricalPowerGeneration") != null)
            assertMapping(
                    "infr:Electricity",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Electricity",
                    "(#$InfrastructureOfFn #$ElectricalPowerGeneration)");

        if (cycAccess.find("Ferry") != null)
            assertMapping(
                    "infr:Ferry",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Ferry",
                    "(#$InfrastructureOfFn #$Ferry)");

        if (cycAccess.find("FossilFuel") != null)
            assertMapping(
                    "infr:Fuel",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Fuel",
                    "(#$InfrastructureOfFn #$FossilFuel)");

        if (cycAccess.find("FuelTank") != null)
            assertMapping(
                    "infr:FuelDepot",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#FuelDepot",
                    "(#$InfrastructureOfFn #$FuelTank)");

        if (cycAccess.find("Harbor") != null)
            assertMapping(
                    "infr:Harbor",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Harbor",
                    "(#$InfrastructureOfFn #$Harbor)");

        if (cycAccess.find("Hospital") != null)
            assertMapping(
                    "infr:Hospital",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Hospital",
                    "(#$InfrastructureOfFn #$Hospital)");

        if (cycAccess.find("Hydropower") != null)
            assertMapping(
                    "infr:HydroElectric",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#HydroElectric",
                    "(#$InfrastructureOfFn #$Hydropower)");

        assertMapping(
                "infr:Infrastructure",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#Infrastructure",
                "(#$InfrastructureOfFn #$PartiallyTangible)");

        if (cycAccess.find("ConstructionArtifact") != null)
            assertMapping(
                    "infr:InfrastructureFacility",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InfrastructureFacility",
                    "(#$InfrastructureOfFn #$ConstructionArtifact)");

        if (cycAccess.find("River") != null &&
            cycAccess.find("Canal") != null &&
            cycAccess.find("Lake") != null)
            assertMapping(
                    "infr:InlandWaterway",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml",
                    "http://www.daml.org/experiment/ontology/infrastructure-elements-ont.daml#InlandWaterway",
                    "(#$InfrastructureOfFn (#$CollectionUnionFn (#$TheSet #$River #$Canal #$Lake)))");

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
        if (cycAccess.find("WorldWideWeb-DynamicIndexedInfoSource") == null)
            cycAccess.createIndividual("WorldWideWeb-DynamicIndexedInfoSource",
                                       "The WorldWideWeb-DynamicIndexedInfoSource is " +
                                       "an instance of #$DynamicIndexedInfoSource. It " +
                                       "is all of the information content of the " +
                                       "WorldWideWeb-Concrete.",
                                       "ComputerGMt",
                                       "IndexedInformationSource");
        else
            cycAccess.assertIsa("WorldWideWeb-DynamicIndexedInfoSource",
                                "IndexedInformationSource");

        // #$InfrastructureOfFn
        cycAccess.createCollectionDenotingUnaryFunction(
            // function
            "InfrastructureOfFn",
            // comment
            "An instance of both CollectionDenotingFunction and ReifiableFunction. " +
            "(#$InfrastructureOfFn COLLECTION) results in the collection of elements " +
            "denoting the physical infrastructure aspect of the elements of the argument " +
            "COLLECTION.",
            // comment mt
            "BaseKB",
            // arg1Isa
            "Collection",
            // arg1Genl
            "Thing",
            // resultIsa
            "Collection",
            // resultGenls
            "PartiallyTangible");
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