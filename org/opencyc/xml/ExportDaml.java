package org.opencyc.xml;

import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

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

    public static boolean verboseFiltering = false;

    private static final String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String damlNamespace = "http://www.daml.org/2001/03/daml+oil#";
    private static final String cycDamlNamespace = "http://opencyc.sourceforge.net/daml/cyc#";
    private static final String damlThing = "http://www.daml.org/2001/03/daml+oil#Thing";
    private static final String damlProperty = "http://www.daml.org/2001/03/daml+oil#Property";
    private static final String damlTransitiveProperty = "http://www.daml.org/2001/03/daml+oil#TransitiveProperty";
    private static final String damlClass = "http://www.daml.org/2001/03/daml+oil#Class";
    private static final String rdfsType = "http://www.w3.org/2000/01/rdf-schema#type";
    private static final String rdfsLiteral = "http://www.w3.org/2000/01/rdf-schema#Literal";
    private static final String guidComment = "Permanent Global Unique ID for the associated concept " +
        "-- which enables concept renaming.  " +
        "Users should not depend upon the DAML ID nor label as fixed for all time.";

    private CycAccess cycAccess;
    private Document document = new DocumentImpl();
    private String documentUrl = null;
    private Element rdf = null;
    private Element damlOntology = null;
    private Element damlVersionInfo = null;
    private Element rdfsComment = null;
    private Guid guid;
    private String name;
    private Vector damlPublicConstants = new Vector();
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

    public ExportDaml() {
    }
    public static void main(String[] args) {
        ExportDaml exportDaml = new ExportDaml();
        exportDaml.export();
    }

    public void export() {
        createRdfNode();
        createDamlOntologyNode();
        createCycGuidNode();

        cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //cycAccess.traceOn();

        CycList publicConstants = null;
        try {
            publicConstants = cycAccess.getPublicConstants();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Collections.sort(publicConstants);
        // Remove non-binary properties.
        for (int i = 0; i < publicConstants.size(); i++) {
            CycConstant cycConstant = null;
            try {
                cycConstant = (CycConstant) publicConstants.get(i);
                if (cycAccess.isCollection(cycConstant))
                    damlPublicConstants.add(cycConstant);
                else if (cycAccess.isUnaryPredicate(cycConstant))
                    // Do not export (for now) Cyc unary predicates, as they cannot be easily expressed in DAML.
                    continue;
                else if (cycAccess.isBinaryPredicate(cycConstant))
                    damlPublicConstants.add(cycConstant);
                else if (cycAccess.isFunction(cycConstant))
                    // Do not export (for now) Cyc functions, as they cannot be expressed in DAML.
                    continue;
                else if (cycAccess.isPredicate(cycConstant))
                    // Do not export Cyc (for now) arity 3+ predicates, as they cannot be easily expressed in DAML.
                    continue;
                else if (cycAccess.isIndividual(cycConstant))
                    damlPublicConstants.add(cycConstant);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //createConstantNode("PhysicalDevice");

        //for (int i = 0; i < damlPublicConstants.size(); i++) {
        for (int i = 0; i < 20; i++) {
            CycConstant cycConstant = null;
            try {
                cycConstant = (CycConstant) damlPublicConstants.elementAt(i);
                System.out.print(cycConstant + "  ");
                if (cycAccess.isCollection(cycConstant))
                    System.out.println("Collection");
                else if (cycAccess.isBinaryPredicate(cycConstant))
                    System.out.println("BinaryPredicate");
                else if (cycAccess.isIndividual(cycConstant)) {
                    System.out.print("Individual");
                    populateIsas(cycConstant);
                    String individualType = "  (type unknown)";
                    if (isas != null)
                        for (int j = 0; j < isas.size(); j++)
                            if (! isas.get(j).equals(cycAccess.getKnownConstantByName("PublicConstant"))) {
                                individualType = (" (a " + isas.get(j) + ")");
                                break;
                            }
                    System.out.println(individualType);
                }
                else {
                    System.out.println("other");
                    continue;
                }
            createConstantNode(cycConstant);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
        try {
            BufferedWriter damlOut = new BufferedWriter(new FileWriter("/home/reed/cyc.daml"));
            XMLSerializer xmlSerializer = new XMLSerializer(damlOut, outputFormat);
            xmlSerializer.asDOMSerializer();
            xmlSerializer.serialize(document);
            damlOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createRdfNode() {
        rdf = document.createElementNS(rdfNamespace, "rdf:RDF");
        rdf.setAttribute("xmlns:rdf", rdfNamespace);
        rdf.setAttribute("xmlns:rdfs", rdfsNamespace);
        rdf.setAttribute("xmlns:daml", damlNamespace);
        rdf.setAttribute("xmlns", cycDamlNamespace);
        document.appendChild(rdf);
    }

    private void createDamlOntologyNode () {
        damlOntology = document.createElementNS(damlNamespace, "daml:Ontology");
        damlOntology.setAttribute("about", "");
        rdf.appendChild(damlOntology);
        damlVersionInfo = document.createElementNS(damlNamespace, "daml:versionInfo");
        damlVersionInfo.appendChild(document.createTextNode("$Id$"));
        damlOntology.appendChild(damlVersionInfo);
        rdfsComment = document.createElementNS(rdfsNamespace, "rdfs:comment");
        rdfsComment.appendChild(document.createTextNode("The Cyc Upper Ontology"));
        damlOntology.appendChild(rdfsComment);
    }

    private void createConstantNode (String constantName) {
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByName(constantName);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        createConstantNode(cycConstant);
    }

    private void createConstantNode (CycConstant cycConstant) {
        guid = cycConstant.guid;
        populateComment(cycConstant);
        populateIsas(cycConstant);
        try {
            if (cycAccess.isCollection(cycConstant))
                createClassNode(cycConstant);
            else if (cycAccess.isBinaryPredicate(cycConstant))
                createPropertyNode(cycConstant);
            else if (cycAccess.isIndividual(cycConstant))
                createIndividualNode(cycConstant);
            else
                System.out.println("Unhandled constant: " + cycConstant.name);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createClassNode(CycConstant cycConstant)
    throws UnknownHostException, IOException {
        populateGenls(cycConstant);
        populateDisjointWiths(cycConstant);
        populateCoExtensionals(cycConstant);
        Element classNode = document.createElementNS(damlNamespace, "daml:Class");
        rdf.appendChild(classNode);
        classNode.setAttributeNS(rdfNamespace, "rdf:ID", cycConstant.name);
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        String label = null;
        try {
            label = cycAccess.getPluralGeneratedPhrase(cycConstant);
            System.out.println("  " + label);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        labelNode.appendChild(document.createTextNode(label));
        classNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        classNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        classNode.appendChild(guidNode);
        Element sameClassAsNode;
        if (cycConstant.name.equals("Thing")) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlThing);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.name.equals("BinaryPredicate")) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlProperty);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.name.equals("TransitiveBinaryPredicate")) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlTransitiveProperty);
            classNode.appendChild(sameClassAsNode);
        }
        else if (cycConstant.name.equals("Collection")) {
            sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
            sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", damlClass);
            classNode.appendChild(sameClassAsNode);
        }


        if (isas != null)
            for (int i = 0; i < isas.size(); i++) {
                Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
                typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant) isas.get(i)));
                classNode.appendChild(typeNode);
            }
        if (genls != null)
            for (int i = 0; i < genls.size(); i++) {
                Element subClassNode = document.createElementNS(rdfsNamespace, "rdfs:subClassOf");
                subClassNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant) genls.get(i)));
                classNode.appendChild(subClassNode);
            }
         if (disjointWiths != null)
            for (int i = 0; i < disjointWiths.size(); i++) {
                Element disjointWithNode = document.createElementNS(damlNamespace, "daml:disjointWith");
                disjointWithNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant) disjointWiths.get(i)));
                classNode.appendChild(disjointWithNode);
            }
       if (coExtensionals != null)
            for (int i = 0; i < coExtensionals.size(); i++) {
                sameClassAsNode = document.createElementNS(damlNamespace, "daml:sameClassAs");
                sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm((CycConstant) coExtensionals.get(i)));
                classNode.appendChild(sameClassAsNode);
            }
    }

    private void createIndividualNode(CycConstant cycConstant) {
        if (isas == null || isas.size() == 0)
            return;
        Element individualNode = document.createElement((String) isas.get(0));
        rdf.appendChild(individualNode);
        individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", cycConstant.name);
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        String label = null;
        try {
            label = cycAccess.getSingularGeneratedPhrase(cycConstant);
            System.out.println("  " + label);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        labelNode.appendChild(document.createTextNode(label));
        individualNode.appendChild(labelNode);
        Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
        commentNode.appendChild(document.createTextNode(comment));
        individualNode.appendChild(commentNode);
        Element guidNode = document.createElement("guid");
        guidNode.appendChild(document.createTextNode(guid.toString()));
        individualNode.appendChild(guidNode);
    }

    private void createCycGuidNode () {
        Element propertyNode = document.createElementNS(damlNamespace, "daml:UniqueProperty");
        rdf.appendChild(propertyNode);
        propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", "guid");
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
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

    private void createPropertyNode(CycConstant cycConstant)
    throws UnknownHostException, IOException {
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
        propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", cycConstant.name);
        Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
        String label = null;
        try {
            label = cycAccess.getGeneratedPhrase(cycConstant);
            System.out.println("  " + label);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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
                subPropertyOfNode.setAttributeNS(rdfNamespace, "rdf:resource", "#" +
                                                 ((CycConstant) genlPreds.get(i)).toString());
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

    private String translateTerm (CycConstant cycConstant)
    throws UnknownHostException, IOException {
        if (cycConstant.equals(cycAccess.thing))
            return damlThing;
        else if (cycConstant.equals(cycAccess.collection))
            return damlClass;
        else if (cycConstant.equals(cycAccess.binaryPredicate))
            return damlProperty;
        else if (cycConstant.equals(cycAccess.getKnownConstantByName("TransitiveBinaryPredicate")))
            return damlTransitiveProperty;
        else
            return "#" + cycConstant.toString();
    }

    private void populateComment (CycConstant cycConstant) {
        comment = null;
        try {
            comment = cycAccess.getComment(cycConstant);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private CycList filterPublicConstants (CycList constants) {
        if (constants.size() == 0)
            return constants;
        CycList result = new CycList();
        for (int i = 0; i < constants.size(); i++ ) {
            Object object = constants.get(i);
            if (isFilteredDamlPublicConstant(object))
                result.add(object);
            else if (verboseFiltering)
                System.out.println(" dropping " + cycConstant);
        }
        return result;
    }

    /**
     * Return True iff the object is a daml public constant. (DAML does not now
     * contain non-binary predicates nor function terms.)
     */
    private boolean isFilteredDamlPublicConstant(Object object) {
        return damlPublicConstants.contains(object);
    }

    private boolean isFilteredPublicConstant(Object object) {
        if (! (object instanceof CycConstant))
            return false;
        try {
            return cycAccess.isPublicConstant(cycConstant);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private void populateIsas (CycConstant cycConstant) {
        isas = null;
        try {
            isas = cycAccess.getIsas(cycConstant);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        isas = filterPublicConstants(isas);
    }

    private void populateGenls (CycConstant cycConstant) {
        genls = null;
        try {
            genls = cycAccess.getGenls(cycConstant);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        genls = filterPublicConstants(genls);
    }

    private void populateGenlPreds (CycConstant cycConstant) {
        genlPreds = null;
        try {
            genlPreds = cycAccess.getGenlPreds(cycConstant);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        genlPreds = filterPublicConstants(genlPreds);
    }

    private void populateArg1Isa (CycConstant cycConstant) {
        arg1Isa = null;
        try {
            CycList arg1Isas = cycAccess.getArg1Isas(cycConstant);
            arg1Isas = filterPublicConstants(arg1Isas);
            if (arg1Isas.size() > 0)
                arg1Isa = (CycConstant) arg1Isas.first();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void populateArg2Isa (CycConstant cycConstant) {
        arg2Isa = null;
        try {
            CycList arg2Isas = cycAccess.getArg2Isas(cycConstant);
            arg2Isas = filterPublicConstants(arg2Isas);
            if (arg2Isas.size() > 0)
                arg2Isa = (CycConstant) arg2Isas.first();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void populateArg1Format (CycConstant cycConstant) {
        arg1Format = null;
        try {
            CycList arg1Formats = cycAccess.getArg1Formats(cycConstant);
            if (arg1Formats.size() > 0)
                arg1Format = (CycConstant) arg1Formats.first();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void populateArg2Format (CycConstant cycConstant) {
        arg2Format = null;
        try {
            CycList arg2Formats = cycAccess.getArg2Formats(cycConstant);
            if (arg2Formats.size() > 0)
                arg2Format = (CycConstant) arg2Formats.first();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void populateDisjointWiths (CycConstant cycConstant) {
        disjointWiths = null;
        try {
            disjointWiths = cycAccess.getDisjointWiths(cycConstant);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        disjointWiths = filterPublicConstants(disjointWiths);
    }

    private void populateCoExtensionals (CycConstant cycConstant) {
        coExtensionals = null;
        try {
            coExtensionals = cycAccess.getCoExtensionals(cycConstant);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        coExtensionals = filterPublicConstants(coExtensionals);
    }

}