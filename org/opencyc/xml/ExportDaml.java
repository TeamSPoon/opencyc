package  org.opencyc.xml;

import  org.w3c.dom.*;
import  org.apache.xerces.dom.*;
import  org.apache.xml.serialize.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.api.*;
import  org.opencyc.util.*;

/**
 * DAML+OIL export for OpenCyc.
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
public class ExportDaml {

    /**
     * Command indicating that the DAML export contains only the marked KB
     * subset terms.  cycKbSubsetCollectionGuid contains the KB subset collection's
     * guid.  cycKbSubsetFilterGuid contains the guid for the KB subset term that
     * filters membership in the upward closure.
     */
    public static final int EXPORT_KB_SUBSET = 1;

    /**
     * Command indicating that the DAML export contains the marked KB
     * subset terms plus all the terms in the upward closure to #$Thing.
     * cycKbSubsetCollectionGuid contains the KB subset collection's
     * guid.
     */
    public static final int EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE = 2;

    /**
     * Command indicating that the DAML export contains the collections whose
     * direct or indirect genl is the collection term indentified by rootTermGuid.
     * cycKbSubsetFilterGuid contains the guid for the KB subset term that
     * filters membership in the export set.
     */
    public static final int EXPORT_KB_SUBSET_BELOW_TERM = 3;

    /**
     * Command indicating that the DAML export contains all the terms in the KB.
     */
    public static final int EXPORT_ENTIRE_KB = 4;

    /**
     * The command performed by the DAML extract process.
     */
    protected int exportCommand = 0;

    /**
     * The default verbosity of the DAML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the DAML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public int verbosity = DEFAULT_VERBOSITY;

    /**
     * Indicates whether the upward closure of terms should be exported.  If so, the
     * upward closure terms are filtered by cycKbSubsetFilterGuid below.
     */
    public boolean includeUpwardClosure = false;

    /**
     * The CycKBSubsetCollection whose elements are exported to DAML.
     */
    public CycFort cycKbSubsetCollection = null;

    /**
     * The #$CounterTerrorismConstant guid.
     */
    public static final Guid counterTerrorismConstantGuid =
        CycObjectFactory.makeGuid("bfe31c38-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$EELDSharedOntologyCoreConstant guid.
     */
    public static final Guid eeldSharedOntologyCoreConstantGuid =
        CycObjectFactory.makeGuid("c12e44bd-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$EELDSharedOntologyConstant guid.
     */
    public static final Guid eeldSharedOntologyConstantGuid =
        CycObjectFactory.makeGuid("c06e4624-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$IKBConstant guid.
     */
    public static final Guid ikbConstantGuid =
        CycObjectFactory.makeGuid("bf90b3e2-9c29-11b1-9dad-c379636f7270");

    /**
     * The CycKBSubsetCollection whose elements are exported to DAML.
     */
    public Guid cycKbSubsetCollectionGuid = null;

    /**
     * The guid which identifies the CycKBSubsetCollection whose elements are exported to DAML if they
     * also generalizations of cycKbSubsetCollectionGuid collections or predicates above.
     * #$IKBConstant (not in OpenCyc)
     */
    public Guid cycKbSubsetFilterGuid = null;

    /**
     * The CycKBSubsetCollection whose elements are exported to DAML if they
     * also generalizations of cycKbSubsetCollectionGuid collections or predicates above.
     * #$IKBConstant (not in OpenCyc)
     */
    protected CycFort cycKbSubsetFilter = null;

    /**
     * Used in the export command EXPORT_KB_SUBSET_BELOW_TERM.
     * The DAML export contains the collections whose direct or indirect genl is
     * the collection term indentified by this value.
     */
    public Guid rootTermGuid = null;

    /**
     * Used in the export command EXPORT_KB_SUBSET_BELOW_TERM.
     * The DAML export contains the collections whose direct or indirect genl is
     * this collection term.
     */
    public CycFort rootTerm = null;

    /**
     * The DAML export path and file name.
     */
    public String outputPath = "export.daml";

    /**
     * The DAML comment that titles the output file.
     */
    public String title = "EELD Shared Ontology";

    private static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";
    private static final String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String damlNamespace = "http://www.daml.org/2001/03/daml+oil#";
    private static final String cycDamlNamespace = "http://www.cyc.com/2002/04/08/cyc#";
    private static final String damlThing = "http://www.daml.org/2001/03/daml+oil#Thing";
    private static final String damlProperty = "http://www.daml.org/2001/03/daml+oil#Property";
    private static final String damlTransitiveProperty = "http://www.daml.org/2001/03/daml+oil#TransitiveProperty";
    private static final String damlClass = "http://www.daml.org/2001/03/daml+oil#Class";
    private static final String rdfsType = "http://www.w3.org/2000/01/rdf-schema#type";
    private static final String rdfsLiteral = "http://www.w3.org/2000/01/rdf-schema#Literal";
    private static final String guidComment =
        "Permanent Global Unique ID for the associated concept.";
    private CycAccess cycAccess;
    private Document document = new DocumentImpl();
    private String documentUrl = null;
    private Element rdf = null;
    private Element damlOntology = null;
    private Element damlVersionInfo = null;
    private Element rdfsComment = null;
    private Guid guid;
    private String name;
    private ArrayList damlSelectedConstants = new ArrayList();
    private CycConstant cycConstant;
    private String comment;
    private CycList isas;
    private CycList genls;
    private CycList genlPreds;
    private CycConstant arg1Isa;
    private CycConstant arg2Isa;
    private CycConstant arg1Format;
    private CycConstant arg2Format;
    private CycList disjointWiths;
    private CycList coExtensionals;

    /**
     * Constructs a new ExportDaml object.
     */
    public ExportDaml () {
        Log.makeLog();
    }

    /**
     * Provides the main method for a DAML export.
     *
     * @parameter args the optional command line arguments
     */
    public static void main (String[] args) {
        ExportDaml exportDaml = new ExportDaml();
        exportDaml.verbosity = ExportDaml.DEFAULT_VERBOSITY;

        String choice = "eeld-core";
        if (args.length > 0)
            choice = args[0];
        try {
            Log.current.println("Choosing KB selection: " + choice);
            // These require the Cycorp IKB or full KB to work as setup below.
            if (choice.equals("all")) {
                exportDaml.includeUpwardClosure = false;
                exportDaml.title = "Open Cyc Ontology";
                exportDaml.outputPath = "open-cyc.daml";
                exportDaml.export(ExportDaml.EXPORT_ENTIRE_KB);
            }
            if (choice.equals("eeld-core")) {
                exportDaml.cycKbSubsetCollectionGuid = eeldSharedOntologyCoreConstantGuid;
                exportDaml.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportDaml.title = "EELD Shared Core Ontology";
                exportDaml.outputPath = "eeld-shared-core-ontology.daml";
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);
            }
            else if (choice.equals("eeld")) {
                exportDaml.cycKbSubsetCollectionGuid = eeldSharedOntologyConstantGuid;
                exportDaml.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportDaml.title = "EELD Shared Ontology";
                exportDaml.outputPath = "eeld-shared-ontology.daml";
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);
            }
            else if (choice.equals("transportation-device")) {
                Guid transportationDeviceGuid =
                    CycObjectFactory.makeGuid("bd58d540-9c29-11b1-9dad-c379636f7270");
                exportDaml.rootTermGuid = transportationDeviceGuid;
                exportDaml.cycKbSubsetFilterGuid = counterTerrorismConstantGuid;
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET_BELOW_TERM);
            }
            else {
                System.out.println("specified choice not found - " + choice);
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Exports the desired KB content into DAML.
     */
    public void export (int exportCommand) throws UnknownHostException, IOException, CycApiException {
        this.exportCommand = exportCommand;
        setup();
        if (verbosity > 2)
            Log.current.println("Getting terms from Cyc");
        CycList selectedConstants = new CycList();
        CycList selectedCycForts = null;
        if (exportCommand == ExportDaml.EXPORT_ENTIRE_KB)
            selectedCycForts = cycAccess.getAllInstances(cycAccess.thing);
        else if ((exportCommand == ExportDaml.EXPORT_KB_SUBSET) ||
            (exportCommand == ExportDaml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)) {
            selectedCycForts = cycAccess.getAllInstances(cycKbSubsetCollection);
        }
        else {
            // EXPORT_KB_SUBSET_BELOW_TERM
            selectedCycForts = cycAccess.getAllSpecs(rootTerm);
            selectedCycForts.add(rootTerm);
        }
        if (verbosity > 2)
            Log.current.println("Selected " + selectedCycForts.size() + " CycFort terms");
        if (includeUpwardClosure) {
            CycList upwardClosureCycForts = gatherUpwardClosure(selectedCycForts);
            if (verbosity > 2)
                Log.current.println("Upward closure added " + upwardClosureCycForts.size() + " CycFort terms");
            selectedCycForts.addAll(upwardClosureCycForts);
            if (verbosity > 2)
                Log.current.println("All selected " + selectedCycForts.size() + " CycFort terms");
        }
        for (int i = 0; i < selectedCycForts.size(); i++) {
            CycFort selectedCycFort = (CycFort)selectedCycForts.get(i);
            if (selectedCycFort instanceof CycConstant)
                selectedConstants.add(selectedCycFort);
        }
        if (verbosity > 2)
            Log.current.println("Sorting " + selectedConstants.size() + " CycConstant terms");
        Collections.sort(selectedConstants);
        if ((exportCommand == ExportDaml.EXPORT_ENTIRE_KB) ||
            (exportCommand == ExportDaml.EXPORT_KB_SUBSET) ||
            (exportCommand == ExportDaml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)) {
            if (verbosity > 2)
                Log.current.println("Removing non-binary properties");
            for (int i = 0; i < selectedConstants.size(); i++) {
                CycConstant cycConstant = (CycConstant)selectedConstants.get(i);
                if (verbosity > 2) {
                    if ((verbosity > 5) || (i % 20 == 0))
                        Log.current.println("... " + cycConstant.cyclify());
                }
                if (cycAccess.isCollection(cycConstant))
                    damlSelectedConstants.add(cycConstant);
                else if (cycAccess.isUnaryPredicate(cycConstant))
                    // Do not export (for now) Cyc unary predicates, as they cannot be easily expressed in DAML.
                    continue;
                else if (cycAccess.isBinaryPredicate(cycConstant))
                    damlSelectedConstants.add(cycConstant);
                else if (cycAccess.isFunction(cycConstant))
                    // Do not export (for now) Cyc functions, as they cannot be expressed in DAML.
                    continue;
                else if (cycAccess.isPredicate(cycConstant))
                    // Do not export Cyc (for now) arity 3+ predicates, as they cannot be easily expressed in DAML.
                    continue;
                else if (cycAccess.isIndividual(cycConstant))
                    damlSelectedConstants.add(cycConstant);
            }
        }
        else {
            // EXPORT_KB_SUBSET_BELOW_TERM
            damlSelectedConstants = applyCycKbSubsetFilter(selectedConstants);
            if (verbosity > 2)
                Log.current.println("Filtered " + damlSelectedConstants.size() + " CycConstant terms");
        }

        //createConstantNode("PhysicalDevice");
        if (verbosity > 2)
            Log.current.println("Building DAML model");
        for (int i = 0; i < damlSelectedConstants.size(); i++) {
            //for (int i = 0; i < 20; i++) {
            CycConstant cycConstant = (CycConstant)damlSelectedConstants.get(i);
            if (verbosity > 2)
                Log.current.print(cycConstant + "  ");
            if (cycAccess.isCollection(cycConstant)) {
                if (verbosity > 2)
                    Log.current.println("Collection");
            }
            else if (cycAccess.isBinaryPredicate(cycConstant)) {
                if (verbosity > 2)
                    Log.current.println("BinaryPredicate");
            }
            else if (cycAccess.isIndividual(cycConstant)) {
                if (verbosity > 2)
                    Log.current.print("Individual");
                populateIsas(cycConstant);
                if (verbosity > 2) {
                    String individualType = "  (type unknown)";
                    if (isas != null)
                        for (int j = 0; j < isas.size(); j++)
                            if (!isas.get(j).equals(cycKbSubsetCollection)) {
                                individualType = (" (a " + isas.get(j) + ")");
                                break;
                            }
                    Log.current.println(individualType);
                }
            }
            else {
                if (verbosity > 2)
                    Log.current.println("other");
                continue;
            }
            createConstantNode(cycConstant);
        }
        if (verbosity > 2)
            Log.current.println("Writing DAML output to " + outputPath);
        OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
        BufferedWriter damlOut = new BufferedWriter(new FileWriter(outputPath));
        XMLSerializer xmlSerializer = new XMLSerializer(damlOut, outputFormat);
        xmlSerializer.asDOMSerializer();
        xmlSerializer.serialize(document);
        damlOut.close();
        if (verbosity > 2)
            Log.current.println("DAML export completed");
    }

    /**
     * Sets up the DAML export process.
     */
    protected void setup () throws UnknownHostException, IOException, CycApiException {
        createRdfNode();
        createDamlOntologyNode();
        createCycGuidNode();
        cycAccess =
            new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                          CycConnection.DEFAULT_BASE_PORT,
                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                          CycAccess.DEFAULT_CONNECTION);
            /*
            new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                          3620,
                          CycConnection.DEFAULT_COMMUNICATION_MODE,
                          CycAccess.DEFAULT_CONNECTION);
                          */
        if (exportCommand == ExportDaml.EXPORT_ENTIRE_KB) {
            includeUpwardClosure = false;
            if (verbosity > 1)
                Log.current.println("Exporting Entire KB subset");
        }
        else if (exportCommand == ExportDaml.EXPORT_KB_SUBSET) {
            cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
            includeUpwardClosure = false;
            if (verbosity > 1)
                Log.current.println("Exporting KB subset " + cycKbSubsetCollection.cyclify());
        }
        else if (exportCommand == ExportDaml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE) {
            cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
            cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
            includeUpwardClosure = true;
            if (verbosity > 1)
                Log.current.println("Exporting KB subset " + cycKbSubsetCollection.cyclify() +
                                   "\n  plus upward closure to #$Thing filtered by " + cycKbSubsetFilter.cyclify());
        }
        else if (exportCommand == ExportDaml.EXPORT_KB_SUBSET_BELOW_TERM) {
            rootTerm = cycAccess.getKnownConstantByGuid(rootTermGuid);
            cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
            cycKbSubsetCollection = cycKbSubsetFilter;
            includeUpwardClosure = false;
            if (verbosity > 1)
                Log.current.println("Exporting KB collections below root term " + rootTerm.cyclify() +
                                   "\n  filtered by " + cycKbSubsetFilter.cyclify());
        }
        else {
            System.err.println("Invalid export comand " + exportCommand);
            System.exit(1);
        }
    }


    /**
     * Creates an RDF node.
     */
    protected void createRdfNode () {
        rdf = document.createElementNS(rdfNamespace, "rdf:RDF");
        rdf.setAttribute("xmlns:rdf", rdfNamespace);
        rdf.setAttribute("xmlns:rdfs", rdfsNamespace);
        rdf.setAttribute("xmlns:daml", damlNamespace);
        rdf.setAttribute("xmlns", cycDamlNamespace);
        document.appendChild(rdf);
    }

    /**
     * Creates a DAML Ontology node.
     */
    protected void createDamlOntologyNode () {
        damlOntology = document.createElementNS(damlNamespace, "daml:Ontology");
        damlOntology.setAttribute("about", "");
        rdf.appendChild(damlOntology);
        damlVersionInfo = document.createElementNS(damlNamespace, "daml:versionInfo");
        damlVersionInfo.appendChild(document.createTextNode("$Id$"));
        damlOntology.appendChild(damlVersionInfo);
        rdfsComment = document.createElementNS(rdfsNamespace, "rdfs:comment");
        rdfsComment.appendChild(document.createTextNode(title));
        damlOntology.appendChild(rdfsComment);
    }

    /**
     * Creates a DAML node for a single Cyc Constant.
     * @parameter cycConstant the CycConstant from which the DAML node is created
     */
    protected void createConstantNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        guid = cycConstant.getGuid();
        populateComment(cycConstant);
        populateIsas(cycConstant);
        if (cycAccess.isCollection(cycConstant))
            createClassNode(cycConstant);
        else if (cycAccess.isBinaryPredicate(cycConstant))
            createPropertyNode(cycConstant);
        else if (cycAccess.isIndividual(cycConstant))
            createIndividualNode(cycConstant);
        else {
            if (verbosity > 0)
                Log.current.println("Unhandled constant: " + cycConstant.toString());
        }
    }

    /**
     * Creates a DAML class node for a single Cyc collection.
     * @parameter cycConstant the Cyc collection from which the DAML class node is created
     */
    protected void createClassNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        populateGenls(cycConstant);
        populateDisjointWiths(cycConstant);
        populateCoExtensionals(cycConstant);
        Element classNode = document.createElementNS(damlNamespace, "daml:Class");
        rdf.appendChild(classNode);
        classNode.setAttributeNS(rdfNamespace, "rdf:ID", cycConstant.toString());
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
        String label = null;
        label = cycAccess.getPluralGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            Log.current.println("  " + label);
        labelNode.appendChild(document.createTextNode(label));
        classNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        classNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        classNode.appendChild(guidNode);
        Element sameClassAsNode;
        if (cycConstant.equals(cycAccess.thing)) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlThing);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.equals(cycAccess.binaryPredicate)) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlProperty);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.equals(cycAccess.getKnownConstantByName("TransitiveBinaryPredicate"))) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlTransitiveProperty);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.equals(cycAccess.collection)) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlClass);
            classNode.appendChild(sameClassAsNode);
        }
        if (isas != null)
            for (int i = 0; i < isas.size(); i++) {
                Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
                typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant)isas.get(i)));
                classNode.appendChild(typeNode);
            }
        if (genls != null)
            for (int i = 0; i < genls.size(); i++) {
                Element subClassNode = document.createElementNS(rdfsNamespace, "rdfs:subClassOf");
                subClassNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant)genls.get(i)));
                classNode.appendChild(subClassNode);
            }
        if (disjointWiths != null)
            for (int i = 0; i < disjointWiths.size(); i++) {
                Element disjointWithNode = document.createElementNS(damlNamespace, "daml:disjointWith");
                disjointWithNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant)disjointWiths.get(i)));
                classNode.appendChild(disjointWithNode);
            }
        if (coExtensionals != null)
            for (int i = 0; i < coExtensionals.size(); i++) {
                sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
                sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant)coExtensionals.get(i)));
                classNode.appendChild(sameClassAsNode);
            }
    }

    /**
     * Creates a DAML individual node for a single Cyc individual.
     *
     * @parameter cycConstant the Cyc individual from which the DAML individual node is created
     */
    protected void createIndividualNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        if (isas == null || isas.size() == 0)
            return;
        Element individualNode = document.createElement(isas.get(0).toString());
        rdf.appendChild(individualNode);
        individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", cycConstant.toString());
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
        String label = cycAccess.getSingularGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            Log.current.println("  " + label);
        labelNode.appendChild(document.createTextNode(label));
        individualNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        individualNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        individualNode.appendChild(guidNode);
    }

    /**
     * Creates the DAML node that defines the guid property.
     */
    protected void createCycGuidNode () {
        Element propertyNode = document.createElementNS(damlNamespace, "daml:UniqueProperty");
        rdf.appendChild(propertyNode);
        propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", "guid");
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
        labelNode.appendChild(document.createTextNode("guid"));
        propertyNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(guidComment));
        propertyNode.appendChild(commentNode);
        Element domainNode = document.createElementNS(damlNamespace, "daml:domain");
        domainNode.setAttributeNS(rdfNamespace, "rdf:resource", damlThing);
        propertyNode.appendChild(domainNode);
        Element rangeNode = document.createElementNS(damlNamespace, "daml:range");
        rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", rdfsLiteral);
        propertyNode.appendChild(rangeNode);
    }

    /**
     * Creates a DAML property node for a single Cyc binary predicate.
     *
     * @parameter cycConstant the Cyc binary predicate from which the DAML property node is created
     */
    protected void createPropertyNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        populateGenlPreds(cycConstant);
        populateArg1Isa(cycConstant);
        populateArg2Isa(cycConstant);
        populateArg1Format(cycConstant);
        populateArg2Format(cycConstant);
        Element propertyNode;
        if ((arg1Format != null) && arg1Format.equals("SingleEntry"))
            propertyNode = document.createElementNS(damlNamespace, "daml:UnambiguousProperty");
        else if ((arg2Format != null) && arg2Format.equals("SingleEntry"))
            propertyNode = document.createElementNS(damlNamespace, "daml:UniqueProperty");
        else
            propertyNode = document.createElementNS(damlNamespace, "daml:Property");
        rdf.appendChild(propertyNode);
        propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", cycConstant.toString());
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
        String label = null;
        label = cycAccess.getGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            Log.current.println("  " + label);
        labelNode.appendChild(document.createTextNode(label));
        propertyNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        propertyNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        propertyNode.appendChild(guidNode);
        if (genlPreds != null)
            for (int i = 0; i < genlPreds.size(); i++) {
                Element subPropertyOfNode = document.createElementNS(damlNamespace, "daml:subPropertyOf");
                subPropertyOfNode.setAttributeNS(rdfNamespace, "rdf:resource", "#" + genlPreds.get(i).toString());
                propertyNode.appendChild(subPropertyOfNode);
            }
        if (arg1Isa != null) {
            Element domainNode = document.createElementNS(damlNamespace, "daml:domain");
            domainNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(arg1Isa));
            propertyNode.appendChild(domainNode);
        }
        if (arg2Isa != null) {
            Element rangeNode = document.createElementNS(damlNamespace, "daml:range");
            rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(arg2Isa));
            propertyNode.appendChild(rangeNode);
        }
    }

    /**
     * Translates a Cyc term into a kind of DAML node: DAML Thing, DAML class, DAML property or
     * DAML transitive property.
     *
     * @parameter cycConstant the Cyc term which is to be translated into a kind of DAML node.
     * @return the kind of DAML node: DAML Thing, DAML class, DAML property or
     * DAML transitive property
     */
    protected String translateTerm (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        if (cycConstant.equals(cycAccess.thing))
            return  damlThing;
        else if (cycConstant.equals(cycAccess.collection))
            return  damlClass;
        else if (cycConstant.equals(cycAccess.binaryPredicate))
            return  damlProperty;
        else if (cycConstant.equals(cycAccess.getKnownConstantByName("TransitiveBinaryPredicate")))
            return  damlTransitiveProperty;
        else
            return  "#" + cycConstant.toString();
    }

    /**
     * Populates the comment for a Cyc term.
     *
     * @parameter cycConstant the Cyc term for which the comment is obtained.
     */
    protected void populateComment (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        comment = cycAccess.getComment(cycConstant);
    }

    /**
     * Removes terms from the given list which are not elements of cycKbSubsetFilter.
     *
     * @parameter constants the given list of constants which is to be filtered
     * @return the filtered list
     */
    protected ArrayList applyCycKbSubsetFilter (CycList constants)
        throws UnknownHostException, IOException, CycApiException{
        if (verbosity > 2)
            Log.current.println("Applying " + cycKbSubsetFilter.cyclify() + " filter");
        if (constants.size() == 0)
            return  constants;
        ArrayList result = new ArrayList();
        for (int i = 0; i < constants.size(); i++) {
            Object object = constants.get(i);
            if ((object instanceof CycConstant) &&
                cycAccess.isa((CycConstant) object, cycKbSubsetFilter))
                    result.add(object);
            else if (verbosity > 4)
                Log.current.println(" dropping " + object);
        }
        return  result;
    }

    /**
     * Removes unselected terms from the given list.
     *
     * @parameter constants the given list of constants which is to be filtered
     * @return the filtered list
     */
    protected CycList filterSelectedConstants (CycList constants) {
        if (constants.size() == 0)
            return  constants;
        CycList result = new CycList();
        for (int i = 0; i < constants.size(); i++) {
            Object object = constants.get(i);
            if (isFilteredDamlSelectedConstant(object))
                result.add(object);
            else if (verbosity > 4)
                Log.current.println(" dropping " + object);
        }
        return  result;
    }

    /**
     * Substitutes more general collection constants for functional collection
     * terms.
     *
     * @parameter cycForts the given list of cycForts which is to be processed
     * @return the list of collection constant terms resulting from the substitution
     * of more general cycConstants for cycNarts
     */
    protected CycList substituteGenlConstantsForNarts (CycList cycForts)
    throws UnknownHostException, IOException, CycApiException {
        CycList result = new CycList();
        for (int i = 0; i < cycForts.size(); i++) {
            CycFort cycFort = (CycFort) cycForts.get(i);
            if (cycFort instanceof CycConstant)
                result.add(cycFort);
            else {
                CycList genls = cycAccess.getGenls(cycFort);
                if (verbosity > 0)
                    Log.current.println(" substituting genls " + genls + " for " + cycFort);
                result.addAllNew(genls);
            }
        }
        return  result;
    }


    /**
     * Return True iff the object is a selected constant. (DAML does not now
     * contain non-binary predicates nor function terms.)
     *
     * @parameter object the object under consideration as a selected constant
     * @return True iff the object is a selected constant
     */
    protected boolean isFilteredDamlSelectedConstant (Object object) {
        return  damlSelectedConstants.contains(object);
    }

    /**
     * Return True iff the object is a instance of the desired KB subset collection
     *
     * @parameter object the object under consideration as an instance of the desired KB
     * subset collection
     */
    protected boolean isFilteredSelectedConstant (Object object)
    throws UnknownHostException, IOException, CycApiException {
        if (!(object instanceof CycConstant))
            return  false;
        else
            return  cycAccess.isa(cycConstant, cycKbSubsetCollection);
    }

    /**
     * Populates the isas for a Cyc term.
     *
     * @parameter cycConstant the Cyc term for which the isas are obtained.
     */
    protected void populateIsas (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {

        isas = cycAccess.getIsas(cycConstant);
        isas = substituteGenlConstantsForNarts(isas);
        isas = filterSelectedConstants(isas);
    }

    /**
     * Populates the genls for a Cyc term.
     *
     * @parameter cycConstant the Cyc term for which the genls are obtained.
     */
    protected void populateGenls (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        genls = cycAccess.getGenls(cycConstant);
        genls = substituteGenlConstantsForNarts(genls);
        genls = filterSelectedConstants(genls);
    }

    /**
     * Populates the genlPreds for a Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate for which the genlPreds are obtained.
     */
    protected void populateGenlPreds (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        genlPreds = cycAccess.getGenlPreds(cycConstant);
        genlPreds = filterSelectedConstants(genlPreds);
    }

    /**
     * Populates the argument 1 type constaint for a Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate for which the argument 1 type constaint is obtained.
     */
    protected void populateArg1Isa (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        CycList arg1Isas = cycAccess.getArg1Isas(cycConstant);
        arg1Isas = substituteGenlConstantsForNarts(arg1Isas);
        arg1Isas = filterSelectedConstants(arg1Isas);
        if (arg1Isas.size() > 0)
            arg1Isa = (CycConstant)arg1Isas.first();
        else
            arg1Isa = null;
    }

    /**
     * Populates the argument 2 type constaint for a Cyc predicate.
     * @parameter cycConstant the Cyc predicate for which the argument 2 type constaint is obtained.
     */
    protected void populateArg2Isa (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        CycList arg2Isas = cycAccess.getArg2Isas(cycConstant);
        arg2Isas = substituteGenlConstantsForNarts(arg2Isas);
        arg2Isas = filterSelectedConstants(arg2Isas);
        if (arg2Isas.size() > 0)
            arg2Isa = (CycConstant)arg2Isas.first();
        else
            arg2Isa = null;
    }

    /**
     * Populates the argument 1 format for a Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate for which the argument 1 format is obtained.
     */
    protected void populateArg1Format (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        CycList arg1Formats = cycAccess.getArg1Formats(cycConstant);
        if (arg1Formats.size() > 0)
            arg1Format = (CycConstant)arg1Formats.first();
    }

    /**
     * Populates the argument 2 format for a Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate for which the argument 2 format is obtained.
     */
    protected void populateArg2Format (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        CycList arg2Formats = cycAccess.getArg2Formats(cycConstant);
        if (arg2Formats.size() > 0)
            arg2Format = (CycConstant)arg2Formats.first();
    }

    /**
     * Populates the disjointWiths for a Cyc collection.
     *
     * @parameter cycConstant the Cyc collection for which the disjointWiths are obtained.
     */
    protected void populateDisjointWiths (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        disjointWiths = cycAccess.getDisjointWiths(cycConstant);
        disjointWiths = substituteGenlConstantsForNarts(disjointWiths);
        disjointWiths = filterSelectedConstants(disjointWiths);
    }

    /**
     * Populates the coExtensionals for a Cyc collection.
     *
     * @parameter cycConstant the Cyc collection for which the coExtensionals are obtained.
     */
    protected void populateCoExtensionals (CycConstant cycConstant)
    throws UnknownHostException, IOException, CycApiException {
        try {
            coExtensionals = cycAccess.getCoExtensionals(cycConstant);
        }
        catch (CycApiException e) {
            e.printStackTrace();
            return;
        }
        coExtensionals = substituteGenlConstantsForNarts(coExtensionals);
        coExtensionals = filterSelectedConstants(coExtensionals);
    }

    /**
     * Gather the updward closure of the selected CycForts with regard to isas and genls
     * for collection terms, and with regard to genlPreds for predicate terms.
     *
     * @parameter the selected CycForts
     * @return the updward closure of the selected CycForts with regard to genls
     * for collection terms, and with regard to genlPreds for predicate terms
     */
    protected CycList gatherUpwardClosure (CycList selectedCycForts) throws UnknownHostException, IOException,
            CycApiException {
        CycList upwardClosure = new CycList();
        cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
        for (int i = 0; i < selectedCycForts.size(); i++) {
            CycFort cycFort = (CycFort)selectedCycForts.get(i);
            if (cycAccess.isCollection(cycFort)) {
                CycList genls = new CycList();
                genls.addAllNew(cycAccess.getAllIsa(cycFort));
                genls.addAllNew(cycAccess.getAllGenls(cycFort));
                for (int j = 0; j < genls.size(); j++) {
                    CycFort genl = null;
                    try {
                        genl = (CycFort) genls.get(j);
                    }
                    catch (ClassCastException e) {
                        Log.current.println("***** Invalid genl: " + genls.get(j));
                        continue;
                    }
                    if ((!upwardClosure.contains(genl)) && (!selectedCycForts.contains(genl)) && cycAccess.isa(genl,
                            cycKbSubsetFilter)) {
                        if (verbosity > 2)
                            Log.current.println("Upward closure genl " + genl);
                        upwardClosure.add(genl);
                    }
                }
            }
            else if ((cycFort instanceof CycConstant) && (cycAccess.isBinaryPredicate((CycConstant)cycFort))) {
                CycList genlPreds = cycAccess.getAllGenlPreds((CycConstant)cycFort);
                for (int j = 0; j < genlPreds.size(); j++) {
                    CycFort genlPred = (CycFort)genlPreds.get(j);
                    if ((!upwardClosure.contains(genlPred)) && (!selectedCycForts.contains(genlPred))
                            && cycAccess.isa(genlPred, cycKbSubsetFilter)) {
                        if (verbosity > 2)
                            Log.current.println("Upward closure genlPred " + genlPred);
                        upwardClosure.add(genlPred);
                    }
                }
            }
        }
        return  upwardClosure;
    }
}



