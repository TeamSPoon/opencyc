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
import  org.opencyc.util.*;

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
     * The HTML exported vocabulary path and file name.
     */
    public String exportedVocabularyOutputPath = "exported-vocabulary.html";

    /**
     * The HTML exported hierarchy path and file name.
     */
    public String exportedHierarchyOutputPath = "exported-hierarchy.html";

    /**
     * the html document
     */
    protected HTMLDocument htmlDocument;

    /**
     * Manages connection to the cyc server api.
     */
    protected CycAccess cycAccess;

    /**
     * the HTML body element
     */
    protected Element htmlBodyElement;

    /**
     * the selected terms
     */
    protected CycList selectedCycForts;

    /**
     * Indicates the presence of a comment for the current term.
     */
    protected boolean hasComment = false;

    /**
     * indicates which terms have been previously expanded in the hierarchy page
     */
    protected HashSet previouslyExpandedTerms;

    /**
     * Constructs a new ExportHtml object.
     */
    public ExportHtml() {
        Log.makeLog();
    }

    /**
     * Runs the ExportHtml application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        ExportHtml exportHtml = new ExportHtml();
        try {
            exportHtml.cycKbSubsetCollectionGuid = ExportHtml.counterTerrorismConstantGuid;
            exportHtml.cycKbSubsetFilterGuid = ExportHtml.ikbConstantGuid;
            exportHtml.exportedVocabularyOutputPath = "counter-terrorism-vocabulary.html";
            exportHtml.exportedHierarchyOutputPath = "counter-terrorism-hierarchy.html";
            exportHtml.export(ExportHtml.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE);
        }
        catch (Exception e) {
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
            Log.current.println("Getting terms from Cyc");
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
            Log.current.println("Selected " + selectedCycForts.size() + " CycFort terms");
        if (includeUpwardClosure) {
            CycList upwardClosureCycForts = gatherUpwardClosure(selectedCycForts);
            if (verbosity > 2)
                Log.current.println("Upward closure added " + upwardClosureCycForts.size() + " CycFort terms");
            selectedCycForts.addAll(upwardClosureCycForts);
            if (verbosity > 2)
                Log.current.println("All selected " + selectedCycForts.size() + " CycFort terms");
        }
        if (verbosity > 2)
            Log.current.println("Sorting " + selectedCycForts.size() + " CycFort terms");

        Collections.sort(selectedCycForts);

        createVocabularyPage();
        if (rootTerm != null)
            createHierarchyPage(rootTerm);
        else if (includeUpwardClosure)
            createHierarchyPage(CycAccess.thing);
        else if (verbosity > 0)
            Log.current.println("Ommiting ontology hierarchy export page");
        if (verbosity > 0)
            Log.current.println("HTML export completed");
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
     * Creates vocabulary HTML page.
     */
    protected void createVocabularyPage () throws UnknownHostException, IOException, CycApiException {
        if (verbosity > 2)
            Log.current.println("Building HTML model for vocabulary page");
        htmlDocument = new HTMLDocumentImpl();
        String title = "Cyc ontology vocabulary for " + cycKbSubsetCollection.cyclify();
        htmlDocument.setTitle(title);
        Node htmlNode = htmlDocument.getChildNodes().item(0);
        htmlBodyElement = htmlDocument.createElement("body");
        htmlNode.appendChild(htmlBodyElement);
        Element headingElement = htmlDocument.createElement("h1");
        htmlBodyElement.appendChild(headingElement);
        Node headingTextNode = htmlDocument.createTextNode(title);
        headingElement.appendChild(headingTextNode);
        for (int i = 0; i < selectedCycForts.size(); i++) {
        //for (int i = 0; i < 20; i++) {

            CycFort cycFort = (CycFort) selectedCycForts.get(i);
            if (verbosity > 2)
                Log.current.print(cycFort + "  ");
            if (cycAccess.isCollection(cycFort)) {
                if (verbosity > 2)
                    Log.current.println("Collection");
            }
            else if (cycAccess.isPredicate(cycFort)) {
                if (verbosity > 2)
                    Log.current.println("Predicate");
            }
            else if (cycAccess.isIndividual(cycFort)) {
                if (verbosity > 2)
                    Log.current.print("Individual");
                if (verbosity > 2) {
                    String individualType = "  (type unknown)";
                    Log.current.println(individualType);
                }
            }
            else {
                if (verbosity > 2)
                    Log.current.println("other");
                continue;
            }
            if (cycFort instanceof CycConstant)
                createCycConstantNode((CycConstant) cycFort);
            else
                createCycNartNode((CycNart) cycFort);
        }
        serialize(htmlDocument, exportedVocabularyOutputPath);
    }

    /**
     * Creates a HTML node for a single Cyc Nart.
     * @parameter cycNart the CycNart from which the HTML node is created
     */
    protected void createCycNartNode (CycNart cycNart)
        throws UnknownHostException, IOException, CycApiException {
        horizontalRule();
        HTMLFontElement htmlFontElement =
            new HTMLFontElementImpl((HTMLDocumentImpl) htmlDocument, "font");
        htmlFontElement.setSize("+1");
        htmlBodyElement.appendChild(htmlFontElement);
        HTMLAnchorElement htmlAnchorElement =
            new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
        htmlAnchorElement.setName(cycNart.cyclify());
        htmlFontElement.appendChild(htmlAnchorElement);
        String generatedPhrase = cycAccess.getSingularGeneratedPhrase(cycNart);
        if (generatedPhrase.endsWith("(unclassified term)"))
            generatedPhrase = generatedPhrase.substring(0, generatedPhrase.length() - 20);
        Node nartTextNode =
            htmlDocument.createTextNode(cycNart.cyclify() + "  (" + generatedPhrase + ")");
        htmlAnchorElement.appendChild(nartTextNode);
        Element blockquoteElement = htmlDocument.createElement("blockquote");
        htmlBodyElement.appendChild(blockquoteElement);
        createIsaNodes(cycNart, blockquoteElement);
        createGenlNodes(cycNart, blockquoteElement);
    }

    /**
     * Creates a HTML node for a single Cyc Constant.
     * @parameter cycConstant the CycConstant from which the HTML node is created
     */
    protected void createCycConstantNode (CycConstant cycConstant)
        throws UnknownHostException, IOException, CycApiException {
        horizontalRule();
        HTMLFontElement htmlFontElement =
            new HTMLFontElementImpl((HTMLDocumentImpl) htmlDocument, "font");
        htmlFontElement.setSize("+1");
        htmlBodyElement.appendChild(htmlFontElement);
        HTMLAnchorElement htmlAnchorElement =
            new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
        htmlAnchorElement.setName(cycConstant.cyclify());
        htmlFontElement.appendChild(htmlAnchorElement);
        String generatedPhrase;
        if (cycAccess.isCollection(cycConstant))
            generatedPhrase = cycAccess.getPluralGeneratedPhrase(cycConstant);
        else
            generatedPhrase = cycAccess.getSingularGeneratedPhrase(cycConstant);
        if (generatedPhrase.endsWith("(unclassified term)"))
            generatedPhrase = generatedPhrase.substring(0, generatedPhrase.length() - 20);
        Node collectionTextNode =
            htmlDocument.createTextNode(cycConstant.cyclify() + "  (" + generatedPhrase + ")");
        htmlAnchorElement.appendChild(collectionTextNode);
        Element blockquoteElement = htmlDocument.createElement("blockquote");
        htmlBodyElement.appendChild(blockquoteElement);
        createCommentNodes(cycConstant, blockquoteElement);
        createGuidNode(cycConstant, blockquoteElement);
        createIsaNodes(cycConstant, blockquoteElement);

        if (cycAccess.isCollection(cycConstant))
            createCollectionNode(cycConstant, blockquoteElement);
        else if (cycAccess.isPredicate(cycConstant))
            createPredicateNode(cycConstant, blockquoteElement);
        else if (cycAccess.isIndividual(cycConstant))
            createIndividualNode(cycConstant, blockquoteElement);
        else {
            if (verbosity > 0)
                Log.current.println("Unhandled constant: " + cycConstant.toString());
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
     *
     * @param parentElement the parent HTML DOM element
     */
    protected void lineBreak (Element parentElement) {
        Element breakElement = htmlDocument.createElement("br");
        parentElement.appendChild(breakElement);
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
     *
     * @param cycConstant the CycConstant for which isa links are to be created
     * @param parentElement the parent HTML DOM element
     */
    protected void createCommentNodes (CycConstant cycConstant, Element parentElement)
        throws IOException, CycApiException {
        String comment = cycAccess.getComment(cycConstant);
        if (comment.equals("")) {
            hasComment = false;
            return;
        }
        hasComment = true;
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
                    selectedCycForts.contains(commentConstant)) {
                    stringBuffer.append(" ");
                    commentTextNode = htmlDocument.createTextNode(stringBuffer.toString());
                    parentElement.appendChild(commentTextNode);
                    stringBuffer = new StringBuffer();
                    htmlAnchorElement = new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                    htmlAnchorElement.setHref("#" + commentConstant.cyclify());
                    parentElement.appendChild(htmlAnchorElement);
                    linkTextNode = htmlDocument.createTextNode(word);
                    htmlAnchorElement.appendChild(linkTextNode);
                }
                else if (commentConstant == null &&
                         word.endsWith("s")) {
                    commentConstant =
                        CycAccess.current().getConstantByName(word.substring(0, word.length() - 2));
                    if (commentConstant != null &&
                        selectedCycForts.contains(commentConstant)) {
                        stringBuffer.append(" ");
                        commentTextNode = htmlDocument.createTextNode(stringBuffer.toString());
                        parentElement.appendChild(commentTextNode);
                        stringBuffer = new StringBuffer();
                        htmlAnchorElement = new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                        htmlAnchorElement.setHref("#" + commentConstant.cyclify());
                        parentElement.appendChild(htmlAnchorElement);
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
            parentElement.appendChild(commentTextNode);
        }
    }

    /**
     * Creates HTML node for guid.
     *
     * @param cycConstant the CycConstant for which isa links are to be created
     * @param parentElement the parent HTML DOM element
     */
    protected void createGuidNode (CycConstant cycConstant, Element parentElement)
        throws IOException, CycApiException {
        Guid guid = cycConstant.getGuid();
        if (hasComment)
            lineBreak(parentElement);
        Element bElement = htmlDocument.createElement("b");
        parentElement.appendChild(bElement);
        Node guidLabelTextNode = htmlDocument.createTextNode("guid: ");
        bElement.appendChild(guidLabelTextNode);
        Node guidTextNode = htmlDocument.createTextNode(guid.toString());
        parentElement.appendChild(guidTextNode);
    }

    /**
     * Creates HTML nodes for isa links.
     *
     * @param cycConstant the CycConstant for which isa links are to be created
     * @param parentElement the parent HTML DOM element
     */
    protected void createIsaNodes (CycFort cycFort, Element parentElement)
        throws IOException, CycApiException {
        CycList isas = filterSelectedConstants(cycAccess.getIsas(cycFort));
        if (verbosity > 4)
            Log.current.println("isas " + isas);
        lineBreak(parentElement);
        Element bElement = htmlDocument.createElement("b");
        parentElement.appendChild(bElement);
        Node isasLabelTextNode = htmlDocument.createTextNode("direct instance of: ");
        bElement.appendChild(isasLabelTextNode);
        for (int i = 0; i < isas.size(); i++) {
            CycFort isa = (CycFort) isas.get(i);
            if (selectedCycForts.contains(isa)) {
                HTMLAnchorElement isaAnchorElement =
                    new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                isaAnchorElement.setHref("#" + isa.cyclify());
                parentElement.appendChild(isaAnchorElement);
                Node isaTextNode = htmlDocument.createTextNode(isa.cyclify());
                isaAnchorElement.appendChild(isaTextNode);
                Node spacesTextNode = htmlDocument.createTextNode("  ");
                parentElement.appendChild(spacesTextNode);
            }
        }
    }

    /**
     * Creates HTML nodes for genl links.
     *
     * @param cycConstant the CycConstant for which genl links are to be created
     * @param parentElement the parent HTML DOM element
     */
    protected void createGenlNodes (CycFort cycFort, Element parentElement)
        throws IOException, CycApiException {
        CycList genls = filterSelectedConstants(cycAccess.getGenls(cycFort));
        if (verbosity > 4)
            Log.current.println("genls " + genls);
        lineBreak(parentElement);
        Element bElement = htmlDocument.createElement("b");
        parentElement.appendChild(bElement);
        Node genlsLabelTextNode = htmlDocument.createTextNode("direct subset of: ");
        bElement.appendChild(genlsLabelTextNode);
        for (int i = 0; i < genls.size(); i++) {
            CycFort genl = (CycFort) genls.get(i);
            if (selectedCycForts.contains(genl)) {
                HTMLAnchorElement genlAnchorElement =
                    new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                genlAnchorElement.setHref("#" + genl.cyclify());
                parentElement.appendChild(genlAnchorElement);
                Node genlTextNode = htmlDocument.createTextNode(genl.cyclify());
                genlAnchorElement.appendChild(genlTextNode);
                Node spacesTextNode = htmlDocument.createTextNode("&nbsp;&nbsp;");
                parentElement.appendChild(spacesTextNode);
            }
        }
        if (genls.size() > 0) {
            Node spacesTextNode = htmlDocument.createTextNode("&nbsp;&nbsp;");
            parentElement.appendChild(spacesTextNode);
            HTMLAnchorElement hierarchyAnchorElement =
                new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
            hierarchyAnchorElement.setHref("./" + exportedHierarchyOutputPath + "#" + cycFort.cyclify());
            parentElement.appendChild(hierarchyAnchorElement);
            Node hierarchyTextNode = htmlDocument.createTextNode("hierarchy");
            hierarchyAnchorElement.appendChild(hierarchyTextNode);
        }
    }

    /**
     * Creates HTML nodes for genlPreds links.
     *
     * @param cycConstant the CycConstant for which genlPreds links are to be created
     * @param parentElement the parent HTML DOM element
     */
    protected void createGenlPredsNodes (CycConstant cycConstant, Element parentElement)
        throws IOException, CycApiException {
        CycList genlPreds = filterSelectedConstants(cycAccess.getGenlPreds(cycConstant));
        if (verbosity > 4)
            Log.current.println("genlPreds " + genlPreds);
        lineBreak(parentElement);
        Element bElement = htmlDocument.createElement("b");
        parentElement.appendChild(bElement);
        Node genlsPredsLabelTextNode = htmlDocument.createTextNode("direct specialization of: ");
        bElement.appendChild(genlsPredsLabelTextNode);
        for (int i = 0; i < genlPreds.size(); i++) {
            CycConstant genlPred = (CycConstant) genlPreds.get(i);
            if (selectedCycForts.contains(genlPred)) {
                HTMLAnchorElement genlPredAnchorElement =
                    new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
                genlPredAnchorElement.setHref("#" + genlPred.cyclify());
                parentElement.appendChild(genlPredAnchorElement);
                Node genlPredTextNode = htmlDocument.createTextNode(genlPred.cyclify());
                genlPredAnchorElement.appendChild(genlPredTextNode);
                Node spacesTextNode = htmlDocument.createTextNode("  ");
                parentElement.appendChild(spacesTextNode);
            }
        }
    }

    /**
     * Creates an HTML individual node for a single Cyc individual.
     *
     * @parameter cycConstant the Cyc individual from which the HTML individual node is created
     * @param parentElement the parent HTML DOM element
     */
    protected void createIndividualNode (CycConstant cycConstant, Element parentElement)
        throws UnknownHostException, IOException, CycApiException {
        Element bElement = htmlDocument.createElement("b");
        parentElement.appendChild(bElement);
        Node individualLabelTextNode = htmlDocument.createTextNode("Individual");
        bElement.appendChild(individualLabelTextNode);
    }

    /**
     * Creates an HTML node for a single Cyc collection.
     *
     * @parameter cycConstant the Cyc collection from which the HTML node is created
     * @param parentElement the parent HTML DOM element
     */
    protected void createCollectionNode (CycConstant cycConstant, Element parentElement)
        throws UnknownHostException, IOException,
            CycApiException {
        createGenlNodes(cycConstant, parentElement);
    }

    /**
     * Creates an HTML node for a single Cyc predicate.
     *
     * @parameter cycConstant the Cyc predicate from which the HTML node is created
     * @param parentElement the parent HTML DOM element
     */
    protected void createPredicateNode (CycConstant cycConstant, Element parentElement)
        throws UnknownHostException, IOException, CycApiException {
        createGenlPredsNodes(cycConstant, parentElement);
    }

    /**
     * Creates hierarchy HTML page.
     *
     * @param rootTerm the root term of the hierarchy tree
     */
    protected void createHierarchyPage (CycFort rootTerm) throws UnknownHostException, IOException, CycApiException {
        if (verbosity > 2)
            Log.current.println("Building HTML model for hierarchy page");
        htmlDocument = new HTMLDocumentImpl();
        String title = "Cyc ontology hierarchy for " + cycKbSubsetCollection.cyclify();
        htmlDocument.setTitle(title);
        Node htmlNode = htmlDocument.getChildNodes().item(0);
        htmlBodyElement = htmlDocument.createElement("body");
        htmlNode.appendChild(htmlBodyElement);
        Element headingElement = htmlDocument.createElement("h1");
        htmlBodyElement.appendChild(headingElement);
        Node headingTextNode = htmlDocument.createTextNode(title);
        headingElement.appendChild(headingTextNode);
        previouslyExpandedTerms = new HashSet();
        createHierarchyNodes(rootTerm, 0);
        serialize(htmlDocument, exportedHierarchyOutputPath);
    }

    /**
     * Recursively creates hierarchy nodes for the given term and its spec collection terms.
     *
     * @param cycFort the given term for which hierarchy nodes will be created
     * @param indent the current indent level
     */
    protected void createHierarchyNodes(CycFort cycFort, int indent)
        throws IOException, CycApiException {
        if (indent > 0) {
            StringBuffer spaces = new StringBuffer(indent);
            StringBuffer nonBreakingSpaces = new StringBuffer(indent);
            for (int i = 0; i < indent; i++) {
                spaces.append(' ');
                nonBreakingSpaces.append("&nbsp;");
            }
            Node spacesText = htmlDocument.createTextNode(nonBreakingSpaces.toString());
            htmlBodyElement.appendChild(spacesText);
            if (verbosity > 2)
                Log.current.println(spaces.toString() + cycFort);
        }
        else {
            if (verbosity > 2)
                Log.current.println(cycFort.toString());
        }
        HTMLAnchorElement vocabularyAnchorElement =
            new HTMLAnchorElementImpl((HTMLDocumentImpl) htmlDocument, "a");
        vocabularyAnchorElement.setHref("./" + exportedVocabularyOutputPath + "#" + cycFort.cyclify());
        htmlBodyElement.appendChild(vocabularyAnchorElement);
        Node hierarchyTermTextNode = htmlDocument.createTextNode(cycFort.cyclify());
        vocabularyAnchorElement.appendChild(hierarchyTermTextNode);
        String generatedPhrase = cycAccess.getPluralGeneratedPhrase(cycFort);
        if (generatedPhrase.endsWith("(unclassified term)"))
            generatedPhrase = generatedPhrase.substring(0, generatedPhrase.length() - 20);
        Node generatedPhraseTextNode =
            htmlDocument.createTextNode("&nbsp;&nbsp;" + generatedPhrase + "");
        htmlBodyElement.appendChild(generatedPhraseTextNode);
        CycList specs = CycAccess.current().getSpecs(cycFort);
        specs = filterSelectedConstants(specs);
        if (specs.size() == 0) {
            vocabularyAnchorElement.setName(cycFort.cyclify());
            lineBreak(htmlBodyElement);
        }
        else if (previouslyExpandedTerms.contains(cycFort)) {
            Node previouslyExpandedTextNode =
                htmlDocument.createTextNode("&nbsp;&nbsp;... see above");
            htmlBodyElement.appendChild(previouslyExpandedTextNode);
            lineBreak(htmlBodyElement);
        }
        else {
            previouslyExpandedTerms.add(cycFort);
            vocabularyAnchorElement.setName(cycFort.cyclify());
            lineBreak(htmlBodyElement);
            for (int i = 0; i < specs.size(); i++)
                createHierarchyNodes((CycFort) specs.get(i), indent + 2);
        }
    }

    /**
     * Serializes the given HTML document to the given path.
     *
     * @param htmlDocument the HTML document model for serialization
     * @param outputPath the file name of the serialized HTML document
     */
    protected void serialize (HTMLDocument htmlDocument, String outputPath) throws IOException {
        if (verbosity > 2)
            Log.current.println("Writing HTML output to " + outputPath);
        OutputFormat outputFormat = new OutputFormat(htmlDocument, "UTF-8", true);
        BufferedWriter htmlOut = new BufferedWriter(new FileWriter(outputPath));
        XHTMLSerializer xhtmlSerializer = new XHTMLSerializer(htmlOut, outputFormat);
        xhtmlSerializer.asDOMSerializer();
        xhtmlSerializer.serialize(htmlDocument);
        htmlOut.close();
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
            if (selectedCycForts.contains(object))
                result.add(object);
            else if (verbosity > 4)
                Log.current.println(" dropping " + object);
        }
        return  result;
    }

    /**
     * Gather the updward closure of the selected CycForts with regard to isas and genls
     * for collection terms, and with regard to isas and genlPreds for predicate terms.
     *
     * @parameter the selected CycForts
     * @return the updward closure of the selected CycForts with regard to genls
     * for collection terms, and with regard to genlPreds for predicate terms
     */
    protected CycList gatherUpwardClosure (CycList selectedCycForts) throws UnknownHostException, IOException,
            CycApiException {
        if (verbosity > 2) {
            Log.current.println("Sorting " + selectedCycForts.size() + " CycFort terms");
            Collections.sort(selectedCycForts);
        }
        CycList upwardClosure = new CycList();
        // Redundant HashSets for efficient contains() method below.
        HashSet selectedCycFortsSet = new HashSet(selectedCycForts);
        HashSet upwardClosureSet = new HashSet(selectedCycForts.size());
        cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
        for (int i = 0; i < selectedCycForts.size(); i++) {
            CycFort cycFort = (CycFort)selectedCycForts.get(i);
            if (cycAccess.isCollection(cycFort)) {
                CycList isasGenls = new CycList();
                isasGenls.addAllNew(cycAccess.getAllIsa(cycFort));
                isasGenls.addAllNew(cycAccess.getAllGenls(cycFort));
                for (int j = 0; j < isasGenls.size(); j++) {
                    CycFort isaGenl = null;
                    try {
                        isaGenl = (CycFort) isasGenls.get(j);
                    }
                    catch (ClassCastException e) {
                        Log.current.println("***** Invalid term: " + isasGenls.get(j));
                        continue;
                    }
                    if ((!upwardClosureSet.contains(isaGenl)) &&
                        (!selectedCycFortsSet.contains(isaGenl)) &&
                        cycAccess.isa(isaGenl, cycKbSubsetFilter)) {
                        if (verbosity > 2)
                            Log.current.println(cycFort + " upward closure isa/genl " + isaGenl);
                        upwardClosure.add(isaGenl);
                        upwardClosureSet.add(isaGenl);
                    }
                }
            }
            else if ((cycFort instanceof CycConstant) && (cycAccess.isPredicate((CycConstant)cycFort))) {
                CycList isasGenlPreds = new CycList();
                isasGenlPreds.addAllNew(cycAccess.getAllIsa(cycFort));
                isasGenlPreds.addAllNew(cycAccess.getAllGenlPreds((CycConstant) cycFort));
                for (int j = 0; j < isasGenlPreds.size(); j++) {
                    CycFort isaGenlPred = (CycFort) isasGenlPreds.get(j);
                    if ((!upwardClosureSet.contains(isaGenlPred)) &&
                        (!selectedCycFortsSet.contains(isaGenlPred)) &&
                        cycAccess.isa(isaGenlPred, cycKbSubsetFilter)) {
                        if (verbosity > 2)
                            Log.current.println(cycFort + " upward closure isa/genlPred " + isaGenlPred);
                        upwardClosure.add(isaGenlPred);
                        upwardClosureSet.add(isaGenlPred);
                    }
                }
            }
        }
        return  upwardClosure;
    }
}