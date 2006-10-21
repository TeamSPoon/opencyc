package  org.opencyc.xml;

//// Internal Imports
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.Guid;
import org.opencyc.util.Log;
import org.opencyc.util.StringUtils;

//// External Imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * OWL (Web Ontology Language) export for OpenCyc.
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2003 Cycorp, Inc., license is open source GNU LGPL.
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
public class ExportOwl extends OntologyExport {
 
  //// Constructors
  
  /**
   * Constructs a new ExportOwl object given the CycAccess object.
   *
   * @param cycAccess The CycAccess object which manages the api connection.
   */
  public ExportOwl(CycAccess cycAccess) {
    super(cycAccess);
  }
  
  //// Public Area
  
  /**
   * The OWL export path and file name.
   */
  public String outputPath = "export.owl";
 
  /**
   * The OWL comment that titles the output file.
   */
  public String title = "Ontology";
  
  
  /** restricts OWL export to either collections, predicates or individuals */
  public String exportCategory = ALL_EXPORT_CATEGORIES;
  
  public static String ALL_EXPORT_CATEGORIES = "all";
  public static String EXPORT_COLLECTION_CATEGORY = "collections";
  public static String EXPORT_PREDICATE_CATEGORY = "predicates";
  public static String EXPORT_INDIVIDUAL_CATEGORY = "individuals";
  
  /**
   * Exports the desired KB content into OWL.
   */
  public void export(int exportCommand) throws UnknownHostException, IOException, CycApiException {
    this.exportCommand = exportCommand;
    setup();
    final CycConstant cycKBSubsetCollection = cycAccess.getConstantByName("CycKBSubsetCollection");
    if (cycKBSubsetCollection != null) {
      allKbSubsetCollections = cycAccess.getAllInstances(cycAccess.getKnownConstantByName("CycKBSubsetCollection"));
      Collections.sort(allKbSubsetCollections);
      if (verbosity > 4)
        Log.current.println("allKbSubsetCollections: " + allKbSubsetCollections.toString());
    }
    if (verbosity > 2)
      Log.current.println("Getting terms from Cyc");
    if (exportCommand == OntologyExport.EXPORT_ENTIRE_KB)
      selectedCycForts = cycAccess.getAllInstances(cycAccess.thing);
    else if ((exportCommand == OntologyExport.EXPORT_KB_SUBSET) || 
             (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) ||
             (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)) {
      selectedCycForts = cycAccess.getAllQuotedInstances(cycKbSubsetCollection, cycAccess.inferencePSC);
      if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)
        includeUpwardClosure = true;
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_ASSERTIONS)
      prepareSelectedAssertions();
    else if (exportCommand != OntologyExport.EXPORT_SELECTED_TERMS) {
      // EXPORT_KB_SUBSET_BELOW_TERM
      selectedCycForts = cycAccess.getAllSpecs(rootTerm);
      selectedCycForts.add(rootTerm);
    }
    if (verbosity > 2)
      Log.current.println("Selected " + selectedCycForts.size() + " terms");
    if (includeUpwardClosure) {
      upwardClosureCycForts = gatherUpwardClosure(selectedCycForts);
      if (verbosity > 2)
        Log.current.println("Upward closure added " + upwardClosureCycForts.size() + " terms");
      selectedCycForts.addAll(upwardClosureCycForts);
      if (verbosity > 2)
        Log.current.println("All selected " + selectedCycForts.size() + " terms");
    }
    int selectedCycForts_size = selectedCycForts.size();
    if (! includeNonAtomicTerms) {
      if (verbosity > 2)
        Log.current.println("Removing positive arity narts.");
      final CycList temp = new CycList();
      for (int i = 0; i < selectedCycForts_size; i++) {
        CycFort selectedCycFort = (CycFort)selectedCycForts.get(i);

        //TODO accept certain narts.

        if (selectedCycFort instanceof CycConstant ||
           (selectedCycFort instanceof CycNart && (((CycNart) selectedCycFort).getArguments().isEmpty())))
          temp.add(selectedCycFort);
        else if (verbosity > 1)
          Log.current.println("dropped NART " + selectedCycFort.cyclify());
      }
      selectedCycForts = temp;
    }
    selectedCycForts_size = selectedCycForts.size();
    sortCycObjects(selectedCycForts);
    if (verbosity > 8) {
      Log.current.println("\nselectedCycForts");
      for (int i = 0; i < selectedCycForts.size(); i++) {
        Log.current.println(((CycObject) selectedCycForts.get(i)).cyclify());
      }
      Log.current.println("");
    }
    if (verbosity > 2) {
      if (exportCommand == EXPORT_RESEARCH_CYC)
        Log.current.println("Partitioning by type.");
      else
        Log.current.println("Removing non-binary properties and partitioning by type.");
    }
    for (int i = 0; i < selectedCycForts_size; i++) {
      CycObject cycObject = (CycObject) selectedCycForts.get(i);
      if (verbosity > 2) {
        if ((verbosity > 5) && (i % 100 == 0))
          Log.current.println("... " + cycObject.cyclify());
      }
      if (cycAccess.isCollection(cycObject)) {
        owlSelectedConstants.add(cycObject);
        if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_COLLECTION_CATEGORY))
          owlSelectedClasses.add(cycObject);
      }
      else if (cycAccess.isUnaryPredicate(cycObject))
        if (exportCommand == EXPORT_RESEARCH_CYC) {
          owlSelectedConstants.add(cycObject);
          if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
            owlSelectedProperties.add(cycObject);
        }
        else
          // Do not export (for now) Cyc unary predicates, as they cannot be easily expressed in OWL.
          continue;
      else if (cycAccess.isBinaryPredicate(cycObject)) {
        owlSelectedConstants.add(cycObject);
        if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
          owlSelectedProperties.add(cycObject);
      }
      else if (cycAccess.isPredicate(cycObject))
        if (exportCommand == EXPORT_RESEARCH_CYC) {
          owlSelectedConstants.add(cycObject);
          if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
            owlSelectedProperties.add(cycObject);
        }
        else
          // Do not export Cyc (for now) arity 3+ predicates, as they cannot be easily expressed in OWL.
          continue;
        else if (cycAccess.isIndividual(cycObject)) {
          owlSelectedConstants.add(cycObject);
          if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_INDIVIDUAL_CATEGORY))
            owlSelectedIndividuals.add(cycObject);
      }
    }

    //createTermNode("PhysicalDevice");
    if (verbosity > 2)
      Log.current.println("Building OWL model");
    
    for (int i = 0; i < owlSelectedClasses.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedClasses.get(i);
      if (verbosity > 2)
        Log.current.print(cycObject + "  Collection");
      createTermNode(cycObject);
    }
    for (int i = 0; i < owlSelectedProperties.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedProperties.get(i);
      if (verbosity > 2)
        Log.current.print(cycObject + "  BinaryPredicate");
      createTermNode(cycObject);
    }
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedIndividuals.get(i);
      if (verbosity > 2)
        Log.current.println(cycObject + "  Individual");
      createTermNode(cycObject);
    }
    serializeDocument();
    
    for (int i = 0; i < owlSelectedClasses.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedClasses.get(i);
      if (verbosity > 2)
        Log.current.println(cycObject.cyclify());
    }
    for (int i = 0; i < owlSelectedProperties.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedProperties.get(i);
      if (verbosity > 2)
        Log.current.println(cycObject.cyclify());
    }
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      CycObject cycObject = (CycObject) owlSelectedIndividuals.get(i);
      if (verbosity > 2)
        Log.current.println(cycObject.cyclify());
    }
    
    if (verbosity > 2)
      Log.current.println("OWL export completed");
  }
  
  /**  Creates an RDF node. */
  public void createRdfNode() {
    rdf = document.createElementNS(rdfNamespace, "rdf:RDF");
    rdf.setAttribute("xmlns:rdf", rdfNamespace);
    rdf.setAttribute("xmlns:rdfs", rdfsNamespace);
    rdf.setAttribute("xmlns:owl", owlNamespace);
    rdf.setAttribute("xmlns", cycOwlNamespace);
    document.appendChild(rdf);
  }
  
  /** Creates a OWL Ontology node. */
  public void createOwlOntologyNode() {
    owlOntology = document.createElementNS(owlNamespace, "owl:Ontology");
    owlOntology.setAttribute("rdf:about", "");
    rdf.appendChild(owlOntology);
    owlVersionInfo = document.createElementNS(owlNamespace, "owl:versionInfo");
    owlVersionInfo.appendChild(document.createTextNode("$Id$"));
    owlOntology.appendChild(owlVersionInfo);
    rdfsComment = document.createElementNS(rdfsNamespace, "rdfs:comment");
    final String titleText = (exportCommand == EXPORT_RESEARCH_CYC) ?
      title + "\n\n" +
      "OpenCyc License Information\n" +
      "The contents of this file constitute portions of The OpenCyc Knowledge\n" +
      "Base. The OpenCyc Knowledge Base is protected under the following license\n" +
      "and copyrights. This license and copyright information must be included\n" +
      "with any copies or derivative works.\n" +
      "\n" +
      "Copyright Information\n" +
      "OpenCyc Knowledge Base Copyright 2001- 2004 - 2005 Cycorp, Inc., Austin, TX, USA.\n" +
      "All rights reserved.\n" +
      "OpenCyc Knowledge Server Copyright 2001- 2004 - 2005 Cycorp, Inc., Austin, TX, USA.\n" +
      "All rights reserved.\n" +
      "Other copyrights may be found in various files.\n" +
      "\n" +
      "The OpenCyc Knowledge Base\n" +
      "The OpenCyc Knowledge Base consists of code, written in the declarative\n" +
      "language CycL, that represents or supports the representation of facts and\n" +
      "rules pertaining to consensus reality. OpenCyc is licensed using the GNU\n" +
      "Lesser General Public License, whose text can also be found on this volume.\n" +
      "The OpenCyc CycL code base is the \"library\" referred to in the LGPL\n" +
      "license. The terms of this license equally apply to renamings and other\n" +
      "logically equivalent reformulations of the Knowledge Base (or portions\n" +
      "thereof) in any natural or formal language.\n" +
      "\n" +
      "See http://www.opencyc.org for more information.\n" : title;
    rdfsComment.appendChild(document.createTextNode(titleText));
    owlOntology.appendChild(rdfsComment);
  }
  
  /** Serializes the OWL document to the XML file. */
  public void serializeDocument() throws IOException {
    if (verbosity > 2)
      Log.current.println("Writing OWL output to " + outputPath);
    OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
    BufferedWriter owlOut = new BufferedWriter(new FileWriter(outputPath));
    XMLSerializer xmlSerializer = new XMLSerializer(owlOut, outputFormat);
    xmlSerializer.asDOMSerializer();
    xmlSerializer.serialize(document);
    owlOut.close();
  }
  
  /** Sets the selected Cyc Forts.
   *
   * @param selectedCycFort the selected Cyc forts
   */
  public void setSelectedCycForts(final CycList selectedCycForts) {
    this.selectedCycForts = selectedCycForts;
  }
  
  /** Sets the list of applicable binary predicates
   *
   * @param applicableBinaryPredicates the the list of applicable predicates
   */
  public void setApplicableBinaryPredicates(final CycList applicableBinaryPredicates) {
    this.applicableBinaryPredicates = applicableBinaryPredicates;
  }
  
  //// Protected Area
  
  /** Sets up the OWL export process. */
  private void setup() throws UnknownHostException, IOException, CycApiException {
    createRdfNode();
    if (! useResearchCycOntology) {
      createOwlOntologyNode();
      createCycGuidNode();
    }
    
    if (exportCommand == OntologyExport.EXPORT_ENTIRE_KB) {
      includeUpwardClosure = false;
      if (verbosity > 1)
        Log.current.println("Exporting Entire KB subset");
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET || exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
      includeUpwardClosure = false;
      if (verbosity > 1)
        Log.current.println("Exporting KB subset " + cycKbSubsetCollection.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE) {
      cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
      cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      includeUpwardClosure = true;
      if (verbosity > 1)
        Log.current.println("Exporting KB subset " + cycKbSubsetCollection.cyclify() +
        "\n  plus upward closure to #$Thing filtered by " + cycKbSubsetFilter.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_BELOW_TERM) {
      rootTerm = cycAccess.getKnownConstantByGuid(rootTermGuid);
      if (cycKbSubsetFilterGuid != null)
        cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      includeUpwardClosure = false;
      if (verbosity > 1) {
        if (cycKbSubsetFilter != null)
          Log.current.println("Exporting KB collections below root term " + rootTerm.cyclify() +
            "\n  filtered by " + cycKbSubsetFilter.cyclify());
        else
          Log.current.println("Exporting KB collections below root term " + rootTerm.cyclify());
      }
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_TERMS) {
      cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      includeUpwardClosure = false;
      if (verbosity > 1)
        Log.current.println("Exporting selected terms " + selectedCycForts.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_ASSERTIONS) {
      cycKbSubsetFilter = cycKbSubsetFilterGuid == null ? null : cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      includeUpwardClosure = false;
      if (verbosity > 1)
        Log.current.println("Exporting selected assertions");
    }
    else {
      System.err.println("Invalid export comand " + exportCommand);
      System.exit(1);
    }
  }
  
  //// Private Area
    
  /** Creates a OWL node for a single Cyc term.
   *
   * @param cycObject the given term
   */
  private void createTermNode(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    if (cycObject instanceof CycConstant)
      guid = ((CycConstant) cycObject).getGuid();
    populateComment(cycObject);
    populateIsas(cycObject);
    populatePropertyAssertions(cycObject);
    if (cycAccess.isCollection(cycObject))
      createClassNode(cycObject);
    else if (cycAccess.isBinaryPredicate(cycObject))
      createPropertyNode(cycObject);
    else if (cycAccess.isIndividual(cycObject))
      createIndividualNode(cycObject);
    else {
      if (verbosity > 0)
        Log.current.println("Unhandled term: " + cycObject.toString());
    }
  }
  
  /** Creates a OWL class node for a single Cyc collection.
   *
   * @param cycObject The Cyc collection from which the OWL class node is created.
   */
  protected void createClassNode(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    populateGenls(cycObject);
    if (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      disjointWiths = null;
      coExtensionals = null;
    }
    else {
      populateDisjointWiths(cycObject);
      populateCoExtensionals(cycObject);
    }
    Element classNode = document.createElementNS(owlNamespace, "owl:Class");
    rdf.appendChild(classNode);
    if (cycObject instanceof CycConstant)
      classNode.setAttributeNS(rdfNamespace, "rdf:ID", xmlName((CycConstant) cycObject));
    else
      classNode.setAttributeNS(rdfNamespace, "rdf:ID", xmlNonAtomicTermName(cycObject));
    if (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      if (verbosity > 2)
        Log.current.println();
    }
    else {
      Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      String label = null;
      label = cycAccess.getPluralGeneratedPhrase(cycObject);
      if (verbosity > 2)
        Log.current.println("  " + label);
      labelNode.appendChild(document.createTextNode(label));
      classNode.appendChild(labelNode);
    }
    if (comment != null && ! comment.equals("")) {
      Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
      commentNode.appendChild(document.createTextNode(comment));
      classNode.appendChild(commentNode);
    }
    if (guid != null) {
      Element guidNode = document.createElement("guid");
      guidNode.appendChild(document.createTextNode(guid.toString()));
      classNode.appendChild(guidNode);
    }
    Element sameClassAsNode;
    if (cycObject.equals(cycAccess.thing)) {
      sameClassAsNode = document.createElementNS(owlNamespace, "owl:equivalentClass");
      sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", owlThing);
      classNode.appendChild(sameClassAsNode);
    }
    else if (cycObject.equals(cycAccess.collection)) {
      sameClassAsNode = document.createElementNS(owlNamespace, "owl:equivalentClass");
      sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", owlClass);
      classNode.appendChild(sameClassAsNode);
    }
    if (isas != null)
      for (int i = 0; i < isas.size(); i++) {
        Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
        typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(isas.get(i)));
        classNode.appendChild(typeNode);
      }
    if (genls != null)
      for (int i = 0; i < genls.size(); i++) {
        Element subClassNode = document.createElementNS(rdfsNamespace, "rdfs:subClassOf");
        subClassNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(genls.get(i)));
        classNode.appendChild(subClassNode);
      }
    if (disjointWiths != null)
      for (int i = 0; i < disjointWiths.size(); i++) {
        Element disjointWithNode = document.createElementNS(owlNamespace, "owl:disjointWith");
        disjointWithNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(disjointWiths.get(i)));
        classNode.appendChild(disjointWithNode);
      }
    if (coExtensionals != null)
      for (int i = 0; i < coExtensionals.size(); i++) {
        sameClassAsNode = document.createElementNS(owlNamespace, "owl:equivalentClass");
        sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(coExtensionals.get(i)));
        classNode.appendChild(sameClassAsNode);
      }
    createPropertyAssertionNodes(classNode);
  }
  
  /** Creates a property assertions node for the given Element.
   *
   * @param element The given element.
   */
  private void createPropertyAssertionNodes(Element node)
    throws UnknownHostException, IOException, CycApiException {
    Collections.sort(propertyAssertions, new ExportOwl.CycObjectComparator());
    for (int i = 0; i < propertyAssertions.size(); i++) {
      CycList propertyAssertion = (CycList) propertyAssertions.get(i);
      CycObject property = (CycObject) propertyAssertion.first();
      Object value = propertyAssertion.third();
      Element propertyAssertionNode = 
        document.createElement((property instanceof CycList) ? xmlNonAtomicTermName(property) : property.toString());
      if (value instanceof String || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double)
        propertyAssertionNode.appendChild(document.createTextNode(value.toString()));
      else if (value instanceof CycList && 
               cycAccess.isa((CycList) value, 
                             cycAccess.getKnownConstantByName("Date")))
        propertyAssertionNode.appendChild(document.createTextNode(cycAccess.xmlDatetimeString((CycList) value)));
      else
        propertyAssertionNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(value));
      node.appendChild(propertyAssertionNode);
    }
  }
  
  /** Creates a OWL individual node for a single Cyc individual.
   *
   * @param cycObject The Cyc individual from which the OWL individual node is created.
   */
  private void createIndividualNode(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    if (isas == null || isas.size() == 0)
      return;
    CycFort isa = bestIsaForIndividual();
    Element individualNode = document.createElement(isa.toString());
    rdf.appendChild(individualNode);
    if (cycObject instanceof CycConstant)
      individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlName((CycConstant) cycObject));
    else
      individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlNonAtomicTermName(cycObject));
    if (exportCommand == EXPORT_RESEARCH_CYC) {
      if (verbosity > 2)
        Log.current.println();
    }
    else {
      Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      String label = cycAccess.getSingularGeneratedPhrase(cycObject);
      if (label.startsWith("Thing ") && cycObject instanceof CycNart)
        label = "some " + cycAccess.getSingularGeneratedPhrase(isa);
      if (verbosity > 2)
        Log.current.println("  " + label);
      labelNode.appendChild(document.createTextNode(label));
      individualNode.appendChild(labelNode);
    }
    if (comment != null && ! comment.equals("")) {
      Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
      commentNode.appendChild(document.createTextNode(comment));
      individualNode.appendChild(commentNode);
    }
    if (guid != null) {
      Element guidNode = document.createElement("guid");
      guidNode.appendChild(document.createTextNode(guid.toString()));
      individualNode.appendChild(guidNode);
    }
    createPropertyAssertionNodes(individualNode);
  }
  
  /** Returns the best isa for the current Individual term.
   *
   * @return The best isa for the current Individual term.
   */
  private CycConstant bestIsaForIndividual() throws UnknownHostException, IOException, CycApiException {
    CycFort bestIsa = (CycFort) isas.get(0);
    if (isas.size() == 1 && bestIsa instanceof CycConstant)
      return (CycConstant) bestIsa;
    CycList candidateIsas = new CycList();
    for (int i = 0; i < isas.size(); i++) {
      CycFort isa = (CycFort) isas.get(i);
      if (! cycAccess.isQuotedCollection(isa) && isa instanceof CycConstant)
        candidateIsas.add(isa);
    }
    if (candidateIsas.size() == 0)
      return (CycConstant) bestIsa;
    else if (candidateIsas.size() == 1)
      return (CycConstant) candidateIsas.get(0);
    else {
      bestIsa = (CycConstant) cycAccess.getMinCol(candidateIsas);
      if (verbosity > 4)
        Log.current.println("    candidateIsas: " + candidateIsas +
        " best-isa: " + bestIsa);
      return (CycConstant) bestIsa;
    }
  }
  
  
  /** Creates the OWL node that defines the guid property.  Note that there is
   * no rdfs:domain statement because Cyc's guid relationship applies to predicates,
   * collections and individuals, but in OWL properties are not instances of Thing.
   * The absence of the rdfs:domain restriction allows the guid property to be applied
   * to Cyc predicates.
   */
  private void createCycGuidNode() {
    Element propertyNode = document.createElementNS(owlNamespace, "owl:FunctionalProperty");
    rdf.appendChild(propertyNode);
    propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", "guid");
    if (exportCommand != EXPORT_RESEARCH_CYC) {
      Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      labelNode.appendChild(document.createTextNode("guid"));
      propertyNode.appendChild(labelNode);
    }
    Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
    commentNode.appendChild(document.createTextNode(guidComment));
    propertyNode.appendChild(commentNode);
    Element rangeNode = document.createElementNS(owlNamespace, "rdfs:range");
    rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", rdfsLiteral);
    propertyNode.appendChild(rangeNode);
  }
  
  /** Creates a OWL property node for a single Cyc binary predicate.
   *
   * @param cycObject The Cyc binary predicate from which the OWL property node is created.
   */
  private void createPropertyNode(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    if (cycObject instanceof CycFort)
      populateGenlPreds((CycFort) cycObject);
    else
      genlPreds = new CycList();
    if (exportCommand == this.EXPORT_RESEARCH_CYC) {
      arg1Isa = null;
      arg2Isa = null;
    }
    else {
      populateArg1Isa(cycObject);
      populateArg2Isa(cycObject);
    }
    populateArg1Format(cycObject);
    populateArg2Format(cycObject);
    Element propertyNode;
    if ((arg1Format != null) && arg1Format.equals("SingleEntry"))
      propertyNode = document.createElementNS(owlNamespace, "owl:InverseFunctionalProperty");
    else if ((arg2Format != null) && arg2Format.equals("SingleEntry"))
      propertyNode = document.createElementNS(owlNamespace, "owl:FunctionalProperty");
    else
      propertyNode = document.createElementNS(owlNamespace, "owl:ObjectProperty");
    rdf.appendChild(propertyNode);
    if (cycObject instanceof CycConstant)
      propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlName((CycConstant) cycObject));
    else
      propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlNonAtomicTermName(cycObject));
    if (exportCommand == EXPORT_RESEARCH_CYC) {   
      if (verbosity > 2)
        Log.current.println();
    }
    else {
      Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      String label = null;
      label = cycAccess.getGeneratedPhrase(cycObject);
      if (verbosity > 2)
        Log.current.println("  " + label);
      labelNode.appendChild(document.createTextNode(label));
      propertyNode.appendChild(labelNode);
    }
    if (comment != null && ! comment.equals("")) {
      Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
      commentNode.appendChild(document.createTextNode(comment));
      propertyNode.appendChild(commentNode);
    }
    if (guid != null) {
      Element guidNode = document.createElement("guid");
      guidNode.appendChild(document.createTextNode(guid.toString()));
      propertyNode.appendChild(guidNode);
    }
    if (genlPreds != null)
      for (int i = 0; i < genlPreds.size(); i++) {
        Element subPropertyOfNode = document.createElementNS(owlNamespace, "rdfs:subPropertyOf");
        subPropertyOfNode.setAttributeNS(rdfNamespace, "rdf:resource", "#" + genlPreds.get(i).toString());
        propertyNode.appendChild(subPropertyOfNode);
      }
    if (arg1Isa != null) {
      Element domainNode = document.createElementNS(owlNamespace, "rdfs:domain");
      domainNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(arg1Isa));
      propertyNode.appendChild(domainNode);
    }
    if (arg2Isa != null) {
      Element rangeNode = document.createElementNS(owlNamespace, "rdfs:range");
      rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(arg2Isa));
      propertyNode.appendChild(rangeNode);
    }
    createPropertyAssertionNodes(propertyNode);
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTerm(Object obj) throws UnknownHostException, IOException, CycApiException {
    if (obj.equals(cycAccess.thing))
      return  owlThing;
    else if (obj.equals(cycAccess.collection))
      return  owlClass;
    else if (obj instanceof CycNart || obj instanceof CycList) {
      return "#" + this.xmlNonAtomicTermName((CycObject) obj);
    }
    else
      return  "#" + xmlName((CycConstant) obj);
  }
  
  /** Populates the comment for a Cyc term, or with an empty string if no comment.
   *
   * @param cycObject The Cyc term for which the comment is obtained.
   */
  private void populateComment(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    final String tempComment = cycAccess.getComment(cycObject);
    if (tempComment == null) {
      comment = null;
      return;
    }
    final StringBuffer stringBuffer = new StringBuffer();
    final int tempComment_length = tempComment.length();
    for (int i = 0; i < tempComment_length; i++) {
      char c = tempComment.charAt(i);
      if (c > 31 && c < 128)
        stringBuffer.append(c);
      else
        stringBuffer.append(' ');
    }
    comment = stringBuffer.toString();
  }
  
  /** Removes terms from the given list which are not elements of cycKbSubsetFilter.
   *
   * @param constants The given list of constants which is to be filtered.
   * @return The filtered list.
   */
  private ArrayList applyCycKbSubsetFilter(CycList constants) throws UnknownHostException, IOException, CycApiException{
    if (cycKbSubsetFilter == null)
      return constants;
    if (verbosity > 2)
      Log.current.println("Applying " + cycKbSubsetFilter.cyclify() + " filter");
    if (constants.size() == 0)
      return  constants;
    ArrayList result = new ArrayList();
    for (int i = 0; i < constants.size(); i++) {
      Object object = constants.get(i);
      if ((object instanceof CycConstant) &&
      cycAccess.isQuotedIsa((CycConstant) object, cycKbSubsetFilter))
        result.add(object);
      else if (verbosity > 4)
        Log.current.println(" dropping " + object);
    }
    return  result;
  }
    
  /** Populates the isas for a Cyc term, removing the case where a term is a type of itself.
   *
   * @param cycObject The Cyc term for which the isas are obtained.
   */
  private void populateIsas(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    if (cycObject instanceof CycList && ((CycList) cycObject).first().toString().equals("InstanceNamedFn")) {
      isas = new CycList();
      isas.add(((CycList) cycObject).third());
    }
    else
      isas = cycAccess.getIsas(cycObject);
    if (isas.contains(cycObject))
      isas.remove(cycObject);
    isas = substituteGenlConstantsForNarts(isas);
    isas = findAllowedTermsOrGenls(isas);
  }
  
  /** Populates the genls for a Cyc term.
   *
   * @param cycObject The Cyc term for which the genls are obtained.
   */
  private void populateGenls(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    genls = cycAccess.getGenls(cycObject);
    if (genls.contains(cycObject))
      genls.remove(cycObject);
    genls = substituteGenlConstantsForNarts(genls);
    genls = findAllowedTermsOrGenls(genls);
  }
  
  /** Populates the genlPreds for a Cyc predicate.
   *
   * @param cycFort The Cyc predicate for which the genlPreds are obtained.
   */
  private void populateGenlPreds(CycFort cycFort) throws UnknownHostException, IOException, CycApiException {
    genlPreds = cycAccess.getGenlPreds(cycFort);
    genlPreds = this.filterSelectedConstants(genlPreds);
    genlPreds = substituteGenlConstantsForNarts(genlPreds);
    if (genlPreds.contains(cycAccess.different))
      genlPreds.remove(cycAccess.different);
    
  }
  
  /** Populates the argument 1 type constaint for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 1 type constaint is obtained.
   */
  private void populateArg1Isa(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    CycList arg1Isas = cycAccess.getArg1Isas(cycObject);
    arg1Isas = substituteGenlConstantsForNarts(arg1Isas);
    arg1Isas = findAllowedTermsOrGenls(arg1Isas);
    if (arg1Isas.size() > 0)
      arg1Isa = (CycConstant)arg1Isas.first();
    else
      arg1Isa = null;
  }
  
  /** Populates the argument 2 type constaint for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 2 type constaint is obtained.
   */
  private void populateArg2Isa(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    CycList arg2Isas = cycAccess.getArg2Isas(cycObject);
    arg2Isas = substituteGenlConstantsForNarts(arg2Isas);
    arg2Isas = findAllowedTermsOrGenls(arg2Isas);
    if (arg2Isas.size() > 0)
      arg2Isa = (CycConstant)arg2Isas.first();
    else
      arg2Isa = null;
  }
  
  /** Populates the argument 1 format for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 1 format is obtained.
   */
  private void populateArg1Format(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    CycList arg1Formats = cycAccess.getArg1Formats(cycObject);
    if (arg1Formats.size() > 0)
      arg1Format = (CycConstant)arg1Formats.first();
  }
  
  /**
   * Populates the argument 2 format for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 2 format is obtained.
   */
  private void populateArg2Format(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    CycList arg2Formats = cycAccess.getArg2Formats(cycObject);
    if (arg2Formats.size() > 0)
      arg2Format = (CycConstant)arg2Formats.first();
  }
  
  /** Populates the disjointWiths for a Cyc collection.
   *
   * @param cycObject The Cyc collection for which the disjointWiths are obtained.
   */
  private void populateDisjointWiths(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    disjointWiths = cycAccess.getDisjointWiths(cycObject);
    disjointWiths = substituteGenlConstantsForNarts(disjointWiths);
    disjointWiths = findAllowedTermsOrGenls(disjointWiths);
  }
  
  /** Populates the coExtensionals for a Cyc collection.
   *
   * @param cycObject The Cyc collection for which the coExtensionals are obtained.
   */
  private void populateCoExtensionals(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    try {
      coExtensionals = cycAccess.getCoExtensionals(cycObject);
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    coExtensionals = substituteGenlConstantsForNarts(coExtensionals);
    coExtensionals = findAllowedTermsOrGenls(coExtensionals);
  }
  
  /** Populates the non-definitional ground atomic formulas in which the the
   * predicate is an element of the list of applicable binary predicates and in
   * which the given term appears as the first argument.
   *
   * @param cycObject The term which appears in the first argument position.
   */
  private void populatePropertyAssertions(CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    CycList candidatePropertyAssertions = null;
    propertyAssertions = new CycList();
    if (termsWithNoExtraProperties.contains(cycObject)) {
      if (verbosity > 8)
        Log.current.println("      term with no extra properties: " + cycObject.cyclify());
      return;
    }
    try {
      final CycObject canonicalizedObject = (CycObject) cycAccess.getHLCycTerm(cycObject.cyclify());
      candidatePropertyAssertions = cycAccess.getGafs(canonicalizedObject, applicableBinaryPredicates);
    if (verbosity > 8)
      Log.current.println("      candidatePropertyAssertions: " + candidatePropertyAssertions.cyclify());
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    candidatePropertyAssertions.remove(cycAccess.isa);
    candidatePropertyAssertions.remove(cycAccess.genls);
    for (int i = 0; i < candidatePropertyAssertions.size(); i++) {
      CycList candidatePropertyAssertion = (CycList) candidatePropertyAssertions.get(i);
      if (candidatePropertyAssertion.third() instanceof CycFort) {
        if (cycKbSubsetCollection == null || cycAccess.isQuotedIsa((CycFort) candidatePropertyAssertion.third(), cycKbSubsetCollection)) {
          propertyAssertions.add(candidatePropertyAssertion);
          if (verbosity > 2)
            Log.current.println("      " + candidatePropertyAssertion.cyclify());
        }
        else if (verbosity > 8)
            Log.current.println("      quoted " + ((CycFort) candidatePropertyAssertion.third()).cyclify() + " is not a " + cycKbSubsetCollection.cyclify());
      }
      else if (verbosity > 8)
          Log.current.println("      " + candidatePropertyAssertion.cyclify() + " is not a CycFort");
    }
  }
  
  /** Returns an XML compliant name for the given term.
   *
   * @param cycConstant the given term
   * @return an XML compliant name for the given term
   */
  public String xmlName(final CycConstant cycConstant) {
    String xmlName = cycConstant.toString();
    if (Character.isDigit(xmlName.charAt(0)))
      xmlName = "N_" + xmlName;
    return xmlName;
  }
  
  /** Returns an XML compliant name for the given non-atomic term.
   *
   * @param nonAtomicTerm the given non-atomic term
   * @return an XML compliant name for the given non-atomic term
   */
  public String xmlNonAtomicTermName(final CycObject nonAtomicTerm)
    throws UnknownHostException, IOException, CycApiException {
    String xmlName = cycAccess.getGeneratedPhrase(nonAtomicTerm);
    xmlName = xmlName.replace(' ', '_');
    xmlName = xmlName.replace('"', '_');
    xmlName = xmlName.replace('\'', '_');
    xmlName = StringUtils.change(xmlName, ",", "");
    xmlName = StringUtils.change(xmlName, "{", "");
    xmlName = StringUtils.change(xmlName, "}", "");
    xmlName = StringUtils.change(xmlName, "#$", "");
    return xmlName;
  }
  
  //// Internal Rep
  
  private static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";
  private static final String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  private static final String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
  private static final String owlNamespace = "http://www.w3.org/2002/07/owl#";
  private static final String cycOwlNamespace = "http://www.cyc.com/2004/06/04/cyc#";
  private static final String owlThing = "http://www.w3.org/2002/07/owl#Thing";
  private static final String owlProperty = "http://www.w3.org/2002/07/owl#Property";
  private static final String owlTransitiveProperty = "http://www.w3.org/2002/07/owl#TransitiveProperty";
  private static final String owlClass = "http://www.w3.org/2002/07/owl#Class";
  private static final String rdfsType = "http://www.w3.org/2000/01/rdf-schema#type";
  private static final String rdfsLiteral = "http://www.w3.org/2000/01/rdf-schema#Literal";
  private static final String guidComment = "Permanent Global Unique ID for the associated concept.";
  
  private final Document document = new DocumentImpl();
  private String documentUrl = null;
  private Element rdf = null;
  private Element owlOntology = null;
  private Element owlVersionInfo = null;
  private Element rdfsComment = null;
  private Guid guid;
  private final ArrayList owlSelectedConstants = new ArrayList();
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
  private CycList propertyAssertions;
  private final ArrayList owlSelectedClasses = new ArrayList();
  private final ArrayList owlSelectedProperties = new ArrayList();
  private final ArrayList owlSelectedIndividuals = new ArrayList();
  private CycList allKbSubsetCollections;
  
}



