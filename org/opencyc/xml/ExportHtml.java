package org.opencyc.xml;

import  java.io.*;
import  java.util.*;
import  java.net.*;
import  org.w3c.dom.*;
import  org.w3c.dom.html.*;
import  org.apache.html.dom.*;
import  org.apache.xml.serialize.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.api.*;

/**
 * HTML ontology export for OpenCyc.
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

public class ExportHtml {

    /**
     * Command indicating that the HTML export contains only the marked KB
     * subset terms.  cycKbSubsetCollectionGuid contains the KB subset collection's
     * guid.  cycKbSubsetFilterGuid contains the guid for the KB subset term that
     * filters membership in the upward closure.
     */
    public static final int EXPORT_KB_SUBSET = 1;

    /**
     * Command indicating that the HTML export contains the marked KB
     * subset terms plus all the terms in the upward closure to #$Thing.
     * cycKbSubsetCollectionGuid contains the KB subset collection's
     * guid.
     */
    public static final int EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE = 2;

    /**
     * Command indicating that the HTML export contains the collections whose
     * direct or indirect genl is the collection term indentified by rootTermGuid.
     * cycKbSubsetFilterGuid contains the guid for the KB subset term that
     * filters membership in the export set.
     */
    public static final int EXPORT_KB_SUBSET_BELOW_TERM = 3;

    /**
     * The command performed by the HTML extract process.
     */
    protected int exportCommand = 0;

    /**
     * The default verbosity of the HTML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the HTML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public int verbosity = DEFAULT_VERBOSITY;

    /**
     * Indicates whether the upward closure of terms should be exported.  If so, the
     * upward closure terms are filtered by cycKbSubsetFilterGuid below.
     */
    public boolean includeUpwardClosure = false;

    /**
     * The CycKBSubsetCollection whose elements are exported to HTML.
     */
    public CycFort cycKbSubsetCollection = null;

    /**
     * The #$CounterTerrorismConstant guid.
     */
    public static final Guid counterTerrorismConstantGuid =
        CycObjectFactory.makeGuid("bfe31c38-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$IKBConstant guid.
     */
    public static final Guid ikbConstantGuid =
        CycObjectFactory.makeGuid("bf90b3e2-9c29-11b1-9dad-c379636f7270");

    /**
     * The CycKBSubsetCollection whose elements are exported to HTML.
     */
    public Guid cycKbSubsetCollectionGuid = null;

    /**
     * The guid which identifies the CycKBSubsetCollection whose elements are exported to HTML if they
     * also generalizations of cycKbSubsetCollectionGuid collections or predicates above.
     * #$IKBConstant (not in OpenCyc)
     */
    public Guid cycKbSubsetFilterGuid = null;

    /**
     * The CycKBSubsetCollection whose elements are exported to HTML if they
     * also generalizations of cycKbSubsetCollectionGuid collections or predicates above.
     * #$IKBConstant (not in OpenCyc)
     */
    protected CycFort cycKbSubsetFilter = null;

    /**
     * Used in the export command EXPORT_KB_SUBSET_BELOW_TERM.
     * The HTML export contains the collections whose direct or indirect genl is
     * the collection term indentified by this value.
     */
    public Guid rootTermGuid = null;

    /**
     * Used in the export command EXPORT_KB_SUBSET_BELOW_TERM.
     * The HTML export contains the collections whose direct or indirect genl is
     * this collection term.
     */
    public CycFort rootTerm = null;

    /**
     * The HTML export path and file name.
     */
    public String outputPath = "export.html";

    /**
     * the html document
     */
    protected HTMLDocument htmlDocument;

    /**
     * Manages connection to the cyc server api.
     */
    protected CycAccess cycAccess;

    private Element htmlBodyElement;
    private Guid guid;
    private String name;
    private ArrayList selectedConstants = new ArrayList();
    private CycConstant cycConstant;
    private String comment;
    private CycList isas;
    private CycList genls;
    private CycList genlPreds;

    /**
     * Constructs a new ExportHtml object.
     */
    public ExportHtml() {
    }

    /**
     * Runs the ExportHtml application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        ExportHtml exportHtml = new ExportHtml();
        try {
            Guid transportationDeviceGuid =
                CycObjectFactory.makeGuid("bd58d540-9c29-11b1-9dad-c379636f7270");
            exportHtml.rootTermGuid = transportationDeviceGuid;
            exportHtml.cycKbSubsetFilterGuid = ExportHtml.counterTerrorismConstantGuid;
            exportHtml.export(ExportHtml.EXPORT_KB_SUBSET_BELOW_TERM);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Exports the desired KB content into HTML.
     */
    public void export (int exportCommand) throws UnknownHostException, IOException, CycApiException {
        this.exportCommand = exportCommand;
        setup();
        if (verbosity > 2)
            System.out.println("Getting terms from Cyc");
        CycList selectedConstants = new CycList();
        CycList selectedCycForts = null;
        if ((exportCommand == ExportHtml.EXPORT_KB_SUBSET) ||
            (exportCommand == ExportHtml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)) {
            selectedCycForts = cycAccess.getAllInstances(cycKbSubsetCollection);
        }
        else {
            // EXPORT_KB_SUBSET_BELOW_TERM
            selectedCycForts = cycAccess.getAllSpecs(rootTerm);
            selectedCycForts.add(rootTerm);
        }
        if (verbosity > 2)
            System.out.println("Selected " + selectedCycForts.size() + " CycFort terms");
        if (includeUpwardClosure) {
            CycList upwardClosureCycForts = gatherUpwardClosure(selectedCycForts);
            if (verbosity > 2)
                System.out.println("Upward closure added " + upwardClosureCycForts.size() + " CycFort terms");
            selectedCycForts.addAll(upwardClosureCycForts);
            if (verbosity > 2)
                System.out.println("All selected " + selectedCycForts.size() + " CycFort terms");
        }
        for (int i = 0; i < selectedCycForts.size(); i++) {
            CycFort selectedCycFort = (CycFort)selectedCycForts.get(i);
            if (selectedCycFort instanceof CycConstant)
                selectedConstants.add(selectedCycFort);
        }
        if (verbosity > 2)
            System.out.println("Sorting " + selectedConstants.size() + " CycConstant terms");
        Collections.sort(selectedConstants);
        //createConstantNode("PhysicalDevice");
        if (verbosity > 2)
            System.out.println("Building HTML model");
        //for (int i = 0; i < selectedConstants.size(); i++) {
        for (int i = 0; i < 60; i++) {
            CycConstant cycConstant = (CycConstant) selectedConstants.get(i);
            if (verbosity > 2)
                System.out.print(cycConstant + "  ");
            if (cycAccess.isCollection(cycConstant)) {
                if (verbosity > 2)
                    System.out.println("Collection");
            }
            else if (cycAccess.isPredicate(cycConstant)) {
                if (verbosity > 2)
                    System.out.println("Predicate");
            }
            else if (cycAccess.isIndividual(cycConstant)) {
                if (verbosity > 2)
                    System.out.print("Individual");
                populateIsas(cycConstant);
                if (verbosity > 2) {
                    String individualType = "  (type unknown)";
                    if (isas != null)
                        for (int j = 0; j < isas.size(); j++)
                            if (!isas.get(j).equals(cycKbSubsetCollection)) {
                                individualType = (" (a " + isas.get(j) + ")");
                                break;
                            }
                    System.out.println(individualType);
                }
            }
            else {
                if (verbosity > 2)
                    System.out.println("other");
                continue;
            }
            createConstantNode(cycConstant);
        }
        serialize();
    }

    /**
     * Sets up the HTML export process.
     */
    protected void setup () throws UnknownHostException, IOException, CycApiException {
        cycAccess = new CycAccess();
        if (exportCommand == ExportDaml.EXPORT_KB_SUBSET) {
            cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
            includeUpwardClosure = false;
            if (verbosity > 1)
                System.out.println("Exporting KB subset " + cycKbSubsetCollection.cyclify());
        }
        else if (exportCommand == ExportDaml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE) {
            cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
            cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
            includeUpwardClosure = true;
            if (verbosity > 1)
                System.out.println("Exporting KB subset " + cycKbSubsetCollection.cyclify() +
                                   "\n  plus upward closure to #$Thing filtered by " + cycKbSubsetFilter.cyclify());
        }
        else if (exportCommand == ExportDaml.EXPORT_KB_SUBSET_BELOW_TERM) {
            rootTerm = cycAccess.getKnownConstantByGuid(rootTermGuid);
            cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
            cycKbSubsetCollection = cycKbSubsetFilter;
            includeUpwardClosure = false;
            if (verbosity > 1)
                System.out.println("Exporting KB collections below root term " + rootTerm.cyclify() +
                                   "\n  filtered by " + cycKbSubsetFilter.cyclify());
        }
        else {
            System.err.println("Invalid export comand " + exportCommand);
            System.exit(1);
        }
        htmlDocument = new HTMLDocumentImpl();
        htmlDocument.setTitle("Cyc ontology for " + cycKbSubsetCollection.cyclify());
        Node htmlNode = htmlDocument.getChildNodes().item(0);
        htmlBodyElement = htmlDocument.createElement("body");
        htmlNode.appendChild(htmlBodyElement);


    }

    /**
     * Serializes the HTML document.
     */
    protected void serialize () throws IOException {
        if (verbosity > 2)
            System.out.println("Writing HTML output to " + outputPath);
        OutputFormat outputFormat = new OutputFormat(htmlDocument, "UTF-8", true);
        BufferedWriter htmlOut = new BufferedWriter(new FileWriter(outputPath));
        XHTMLSerializer xhtmlSerializer = new XHTMLSerializer(htmlOut, outputFormat);
        xhtmlSerializer.asDOMSerializer();
        xhtmlSerializer.serialize(htmlDocument);
        htmlOut.close();
        if (verbosity > 2)
            System.out.println("HTML export completed");
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
     * Populates the isas for a Cyc term.
     *
     * @parameter cycConstant the Cyc term for which the isas are obtained.
     */
    protected void populateIsas (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        isas = cycAccess.getIsas(cycConstant);
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
     * Creates a HTML node for a single Cyc Constant.
     * @parameter cycConstant the CycConstant from which the HTML node is created
     */
    protected void createConstantNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        guid = cycConstant.getGuid();
        populateComment(cycConstant);
        //populateIsas(cycConstant);
        if (cycAccess.isCollection(cycConstant))
            createCollectionNode(cycConstant);
        else if (cycAccess.isPredicate(cycConstant))
            createPredicateNode(cycConstant);
        else if (cycAccess.isIndividual(cycConstant))
            createIndividualNode(cycConstant);
        else {
            if (verbosity > 0)
                System.out.println("Unhandled constant: " + cycConstant.toString());
        }
    }

    /**
     * Creates a paragraph break in the HTML document.
     */
    protected void paragraphBreak () {
        Element paragraphElement = htmlDocument.createElement("p");
        htmlBodyElement.appendChild(paragraphElement);
    }

    /**
     * Creates a line break in the HTML document.
     */
    protected void lineBreak () {
        Element breakElement = htmlDocument.createElement("br");
        htmlBodyElement.appendChild(breakElement);
    }

    /**
     * Creates a horizontal rule in the HTML document.
     */
    protected void horizontalRule () {
        Element horizontalRuleElement = htmlDocument.createElement("hr");
        htmlBodyElement.appendChild(horizontalRuleElement);
    }

    /**
     * Creates HTML nodes for comment text containing CycConstants which are to be
     * represented as hyperlinks.
     */
    protected void createCommentNodes () throws IOException, CycApiException {
        StringTokenizer st = new StringTokenizer(comment);
        StringBuffer stringBuffer = new StringBuffer();
        CycConstant commentConstant;
        Node commentTextNode;
        Node linkTextNode;
        HTMLAnchorElement htmlAnchorElement;
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (word.startsWith("#$")) {
                commentConstant = CycAccess.current().getConstantByName(word);
                if (commentConstant != null &&
                    selectedConstants.contains(commentConstant)) {
                    commentTextNode = htmlDocument.createTextNode(stringBuffer.toString());
                    htmlDocument.appendChild(commentTextNode);
                    stringBuffer = new StringBuffer();
                    htmlAnchorElement = new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                    htmlAnchorElement.setHref("#" + commentConstant.cyclify());
                    htmlDocument.appendChild(htmlAnchorElement);
                    linkTextNode = htmlDocument.createTextNode(word);
                    htmlAnchorElement.appendChild(linkTextNode);
                }
                else if (commentConstant == null &&
                         word.endsWith("s")) {
                    commentConstant =
                        CycAccess.current().getConstantByName(word.substring(0, word.length() - 2));
                    if (commentConstant != null &&
                        selectedConstants.contains(commentConstant)) {
                        commentTextNode = htmlDocument.createTextNode(stringBuffer.toString());
                        htmlDocument.appendChild(commentTextNode);
                        stringBuffer = new StringBuffer();
                        htmlAnchorElement = new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                        htmlAnchorElement.setHref("#" + commentConstant.cyclify());
                        htmlDocument.appendChild(htmlAnchorElement);
                        linkTextNode = htmlDocument.createTextNode(word);
                        htmlAnchorElement.appendChild(linkTextNode);
                    }
                }
                else {
                    stringBuffer.append(" ");
                    stringBuffer.append(word);
                }
            }
            else {
                stringBuffer.append(" ");
                stringBuffer.append(word);
            }
        }
        if (stringBuffer.length() > 0) {
            commentTextNode = htmlDocument.createTextNode(stringBuffer.toString());
            htmlDocument.appendChild(commentTextNode);
        }
    }

    /**
     * Creates an HTML individual node for a single Cyc individual.
     *
     * @parameter cycConstant the Cyc individual from which the HTML individual node is created
     */
    protected void createIndividualNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        if (isas == null || isas.size() == 0)
            return;
        horizontalRule();
        Element boldElement = htmlDocument.createElement("bold");
        htmlBodyElement.appendChild(boldElement);
        Node individualTextNode = htmlDocument.createTextNode(cycConstant.cyclify());
        boldElement.appendChild(individualTextNode);
        /*
        Element individualNode = document.createElement(isas.get(0).toString());
        rdf.appendChild(individualNode);
        individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", cycConstant.toString());
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        String label = cycAccess.getSingularGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            System.out.println("  " + label);
        labelNode.appendChild(document.createTextNode(label));
        individualNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        individualNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        individualNode.appendChild(guidNode);
        */
    }

    /**
     * Creates an HTML node for a single Cyc collection.
     * @parameter cycConstant the Cyc collection from which the HTML node is created
     */
    protected void createCollectionNode (CycConstant cycConstant) throws UnknownHostException, IOException,
            CycApiException {
        horizontalRule();
        HTMLFontElement htmlFontElement = new HTMLFontElementImpl((HTMLDocumentImpl) htmlDocument, "font");
        htmlFontElement.setSize("+1");
        htmlBodyElement.appendChild(htmlFontElement);
        HTMLAnchorElement collectionAnchorElement =
            new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
        collectionAnchorElement.setName(cycConstant.cyclify());
        htmlFontElement.appendChild(collectionAnchorElement);
        Node collectionTextNode = htmlDocument.createTextNode(cycConstant.cyclify());
        collectionAnchorElement.appendChild(collectionTextNode);
        lineBreak();
        this.createCommentNodes();
        /*
        populateGenls(cycConstant);
        populateDisjointWiths(cycConstant);
        populateCoExtensionals(cycConstant);
        Element classNode = document.createElementNS(damlNamespace, "daml:Class");
        rdf.appendChild(classNode);
        classNode.setAttributeNS(rdfNamespace, "rdf:ID", cycConstant.toString());
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        String label = null;
        label = cycAccess.getPluralGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            System.out.println("  " + label);
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
        */
    }

    /**
     * Creates an HTML node for a single Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate from which the HTML node is created
     */
    protected void createPredicateNode (CycConstant cycConstant)
        throws UnknownHostException, IOException, CycApiException {
        /*
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
        String label = null;
        label = cycAccess.getGeneratedPhrase(cycConstant);
        if (verbosity > 2)
            System.out.println("  " + label);
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
        */
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
            if (selectedConstants.contains(object))
                result.add(object);
            else if (verbosity > 4)
                System.out.println(" dropping " + cycConstant);
        }
        return  result;
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
                        System.out.println("***** Invalid genl: " + genls.get(j));
                        continue;
                    }
                    if ((!upwardClosure.contains(genl)) && (!selectedCycForts.contains(genl)) && cycAccess.isa(genl,
                            cycKbSubsetFilter)) {
                        if (verbosity > 2)
                            System.out.println("Upward closure genl " + genl);
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
                            System.out.println("Upward closure genlPred " + genlPred);
                        upwardClosure.add(genlPred);
                    }
                }
            }
        }
        return  upwardClosure;
    }
}