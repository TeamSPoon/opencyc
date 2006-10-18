package org.opencyc.xml;

//// Internal Imports
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.util.Log;
import org.opencyc.util.StringUtils;

//// External Imports
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.arp.StatementHandler;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/** Abstract class to provide common functions for importing OWL xml content.
 * Subclasses will provide additional behavior tailored for the particular
 * OWL imports.<p>
 * <p>
 * The Another RDF Parser (ARP) is used to parse the input OWL document.
 * This class implements statement callbacks from ARP. Each triple in the
 * input file causes a call on one of the statement methods.
 * The same triple may occur more than once in a file, causing repeat calls
 * to the method.
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
public abstract class ImportOwl implements StatementHandler {
  
  //// Constructors
  
  /** Constructs a new ImportOwl object. 
   *
   * @param cycAccess the CycAccess object
   */
  public ImportOwl(final CycAccess cycAccess) throws IOException {
    this.cycAccess = cycAccess;
    startMilliseconds = System.currentTimeMillis();
    arp = new ARP();
    arp.setStatementHandler(this);
    initialize();
  }
  
  //// Public Area
  
  /** The default verbosity of this application.  0 --> quiet ... 9 -> maximum diagnostic input. */
  public static final int DEFAULT_VERBOSITY = 3;
  
  /** When T, indicates that the import is performed othwise when false
   * indicates that the OWL document should be parsed but not imported.
   */
  public boolean actuallyImport = true;
  
  /** Sets verbosity of this application.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   */
  public int verbosity = DEFAULT_VERBOSITY;
  
  /** Parses and imports the given OWL URL.
   *
   * @param owlDocInfo contains information about the OWL document to import
   */
  public void importOwl(OwlDocInfo owlDocInfo)
  throws IOException, CycApiException {
    owlPath = owlDocInfo.getOwlPath();
    nickname = owlDocInfo.getNickname();
    importMtName = owlDocInfo.getImportMtName();
    importGenlMtName = owlDocInfo.getImportGenlMtName();
    characterEncoding = owlDocInfo.getCharacterEncoding();
    if (verbosity > 0) {
      if (characterEncoding == null)
        Log.current.println("\nImporting " + owlPath +
                            "\ninto " + importMtName);
      else
        Log.current.println("\nImporting " + owlPath +
                            " encoding " + characterEncoding +
                            "\ninto " + importMtName);
    }
    importMt = cycAccess.getConstantByName(importMtName);
    if (importMt == null) {
      String comment = "This microtheory contains #$OWL-WebOntologyLanguage imported axioms from " + owlPath + ".";
      ArrayList genlMts = new ArrayList();
      CycFort genlMt = cycAccess.getKnownConstantByName(importGenlMtName);
      if (genlMt == null)
        throw new RuntimeException("cannot find " + importGenlMtName);
      genlMts.add(importGenlMtName);
      importMt = cycAccess.createMicrotheory(importMtName, comment, "Microtheory", genlMts);
    }
    owlOntologyDefiningURL = owlPath;
    Log.current.println("Defining URI " + owlOntologyDefiningURL);
    Log.current.println("\nStatements\n");
    //cycAccess.traceOn();
    InputStreamReader in;
    URL url;
    try {
      File ff = new File(owlPath);
      if (characterEncoding == null)
        in = new InputStreamReader(new FileInputStream(ff));
      else
        in = new InputStreamReader(new FileInputStream(ff), characterEncoding);
      url = ff.toURL();
    }
    catch (Exception ignore) {
      try {
        url = new URL(owlPath);
        if (characterEncoding == null)
          in = new InputStreamReader(url.openStream());
        else
          in = new InputStreamReader(url.openStream(), characterEncoding);
      }
      catch (Exception e) {
        System.err.println("ARP: Failed to open: " + owlPath);
        System.err.println("    " + ParseException.formatMessage(ignore));
        System.err.println("    " + ParseException.formatMessage(e));
        return;
      }
    }
    try {
      arp.load(in, url.toExternalForm());
    }
    catch (IOException e) {
      System.err.println("Error: " + owlPath + ": " + ParseException.formatMessage(e));
    }
    catch (SAXException sax) {
      System.err.println("Error: " + owlPath + ": " + ParseException.formatMessage(sax));
    }
    Log.current.println("\nDone importing " + owlPath + "\n" + (System.currentTimeMillis() - startMilliseconds) + " milliseconds");
    
    
  }
  
  /** Provides the ARP statement handler for triple having an Object.
   *
   * @param subject the RDF Triple Subject
   * @param predicate the RDF Triple Predicate
   * @param object the RDF Triple Object
   */
  public void statement(AResource subject, AResource predicate, AResource object) {
    try {
      if (subject.isAnonymous()) {
        processRestrictionSubject(subject, predicate, object);
        return;
      }
      if (object.isAnonymous()) {
        processRestrictionObject(subject, predicate, object);
        return;
      }
      OwlTermInfo subjectTermInfo = resource(subject, null);
      OwlTermInfo predicateTermInfo = resource(predicate, null);
      OwlTermInfo objectTermInfo = resource(object, predicateTermInfo);
      displayTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
      importTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
    }
    catch (Exception e) {
      Log.current.errorPrintln(e + e.getMessage());
      Log.current.printStackTrace(e);
    }
  }
  
  /** Provides the ARP statement handler for triple having an Literal.
   *
   * @param subject the RDF Triple Subject
   * @param predicate the RDF Triple Predicate
   * @param literal the RDF Triple Literal
   */
  public void statement(AResource subject, AResource predicate, ALiteral literal) {
    try {
      if (subject.isAnonymous()) {
        processRestrictionSubject(subject, predicate, literal);
      }
      else {
        OwlTermInfo subjectTermInfo = resource(subject, null);
        OwlTermInfo predicateTermInfo = resource(predicate, null);
        OwlTermInfo literalTermInfo = literal(literal);
        displayTriple(subjectTermInfo, predicateTermInfo, literalTermInfo);
        importTriple(subjectTermInfo, predicateTermInfo, literalTermInfo);
      }
    }
    catch (Exception e) {
      Log.current.errorPrintln(e.getMessage());
      Log.current.printStackTrace(e);
    }
  }
  
  /** Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   *
   * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
   */
  public void setVerbosity(int verbosity) {
    this.verbosity = verbosity;
  }
  
  //// Protected Area
  
  /** Initializes the ImportOwl object. */
  protected void initialize() throws IOException, UnknownHostException, CycApiException {
    kbSubsetCollection = cycAccess.getKnownConstantByName("OWLConstant");
    if (cycAccess.isOpenCyc()) {
      cycAccess.setCyclist("CycAdministrator");
      cycAccess.setKePurpose("OpenCycProject");
    }
    else {
      cycAccess.setCyclist("SteveReed");
      cycAccess.setKePurpose("ResearchCycProject");
    }
    initializeOntologyNicknames();
    getMappings();
  }
  
  /**
   * Initializes the Ontology nicknames mapping.
   */
  protected void initializeOntologyNicknames() {
    ontologyNicknames.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
    ontologyNicknames.put("http://www.w3.org/2000/01/rdf-schema", "rdfs");
    ontologyNicknames.put("http://www.w3.org/2000/10/XMLSchema", "xsd");
    ontologyNicknames.put("http://www.w3.org/2001/XMLSchema", "xsd");
    ontologyNicknames.put("http://www.w3.org/2002/07/owl", "owl");
    /*
    ontologyNicknames.put("http://metadata3.jivanet.net/core.owl", "core");
    ontologyNicknames.put("http://metadata3.jivanet.net/icmsp.owl", "icmsp");
    ontologyNicknames.put("http://metadata3.jivanet.net/ct.owl", "ct");
    if (nickname != null)
      ontologyNicknames.put(owlPath, nickname);
    */
  } 
  
  /** Imports the RDF triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param predicateTermInfo the predicate OwlTermInfo object
   * @param objLitTermInfo the object or literal OwlTermInfo object
   */
  protected void importTriple(OwlTermInfo subjectTermInfo,
                              OwlTermInfo predicateTermInfo,
                              OwlTermInfo objLitTermInfo)
  throws IOException, UnknownHostException, CycApiException {
    if (! actuallyImport)
      return;
    if (predicateTermInfo.isURI)
      predicateTermInfo.coerceToNamespace();
    String owlPredicate = predicateTermInfo.toString();
    if (! subjectTermInfo.hasEquivalentCycTerm()) {
      if (owlPredicate.equals("#$isa")) {
        importIsa(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$genls")) {
        importGenls(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("owl:imports")) {
        importImports(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$comment")) {
        importComment(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$nameString")) {
        importNameString(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$arg1Isa")) {
        importArg1Isa(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$arg2Isa")) {
        importArg2Isa(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$conceptuallyRelated")) {
        importConceptuallyRelated(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$containsInformationAbout")) {
        importContainsInformationAbout(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$genlPreds")) {
        importGenlPreds(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$ontologyVersionInfo")) {
        importOntologyVersionInfo(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (owlPredicate.equals("#$inverseBinaryPredicateOf")) {
        importInverseBinaryPredicateOf(subjectTermInfo, objLitTermInfo);
        return;
      }
      if (predicateTermInfo.ontologyNickname != null && 
          (predicateTermInfo.ontologyNickname.equals("owl") ||
           predicateTermInfo.ontologyNickname.equals("rdfs") ||
           predicateTermInfo.ontologyNickname.equals("rdf"))) {
        Log.current.println("\n\nUnhandled predicate: " + owlPredicate + "\n");
        return;
      }
    }
    CycFort subject = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    CycFort predicate = predicateTermInfo.cycFort;
    if (predicate == null) {
      predicate = importTerm(predicateTermInfo, PROPERTY_TERM);
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(predicate);
        gaf.add(cycAccess.binaryPredicate);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        final CycObject mt = cycAccess.getKnownConstantByName("UniversalVocabularyMt");
        cycAccess.assertHLGaf(gaf, mt, strength);
        final CycList gaf2 = new CycList();
        gaf2.add(cycAccess.getKnownConstantByName("arity"));
        gaf2.add(predicate);
        gaf2.add(new Integer(2));
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaBinaryPredicate(predicate);
    }
    CycList arg1Constraints =
      cycAccess.getArg1Isas((CycFort) predicate, importMt);
    assertForwardArgConstraints(subject, arg1Constraints);
    CycList arg2Constraints =
      cycAccess.getArg2Isas((CycFort) predicate, importMt);
    
    arg2Constraints.addAllNew(cycAccess.getInterArgIsa1_2_forArg2((CycFort) predicate,
                                                                  subject,
                                                                  importMt));
    if (objLitTermInfo.isLiteral) {
      if (! subjectTermInfo.hasEquivalentCycTerm())
        // No need to make assertions about mapped Cyc terms.
        importLiteralTriple(subject, predicate, objLitTermInfo, arg2Constraints);
      return;
    }
    CycFort object = objLitTermInfo.cycFort;
    if (object == null)
      object = importTerm(objLitTermInfo, INDIVIDUAL_TERM);
    assertForwardArgConstraints(object, arg2Constraints);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(predicate);
      gaf.add(subject);
      gaf.add(object);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGaf(importMt,
                          predicate,
                          subject,
                          object);
    Log.current.println("(" + predicate.cyclify() + " " +
    subject.cyclify() + " " +
    object.cyclify() + ")\n");
  }
  
  /** Imports the RDF literal triple.
   *
   * @param subject the subject
   * @param predicate the predicate
   * @param LiteralTermInfo the literal OwlTermInfo object
   * @param arg2Constraints the argument constraints on the type of literal permitted
   * by this assertion
   */
  protected void importLiteralTriple(CycFort subject,
                                     CycFort predicate,
                                     OwlTermInfo LiteralTermInfo,
                                     CycList arg2Constraints)
  throws IOException, UnknownHostException, CycApiException {
    if (arg2Constraints.size() == 0) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(predicate);
        gaf.add(subject);
        gaf.add((String) escaped(LiteralTermInfo.literal));
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertGaf(importMt,
                            predicate,
                            subject,
                            (String) escaped(LiteralTermInfo.literal));
    }
    else {
      if (arg2Constraints.size() > 1) {
        Log.current.println("*** ignoring extra literal argument constraints " +
        arg2Constraints.cyclify());
        //TODO find most specific of the arg constraints.
      }
      CycFort arg2Constraint = (CycFort) arg2Constraints.first();
      if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLString")) ||
          arg2Constraint.equals(cycAccess.getKnownConstantByName("CharacterString"))) {
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(predicate);
          gaf.add(subject);
          gaf.add((String) escaped(LiteralTermInfo.literal));
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              predicate,
                              subject,
                              (String) escaped(LiteralTermInfo.literal));
      }
      else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLRealNumber"))) {
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(predicate);
          gaf.add(subject);
          gaf.add(new Double(LiteralTermInfo.literal));
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              predicate,
                              subject,
                              new Double(LiteralTermInfo.literal));   
      }
      else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("SubLInteger"))) {
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(predicate);
          gaf.add(subject);
          gaf.add(new Integer(LiteralTermInfo.literal));
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              predicate,
                              subject,
                              new Integer(LiteralTermInfo.literal));       
      }
      else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("Date"))) {
        CycFort date = new CycNart(cycAccess.getKnownConstantByName("DateDecodeStringFn"),
                                   "YYYY-MM-DD",
                                   LiteralTermInfo.literal);
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(predicate);
          gaf.add(subject);
          gaf.add(date);
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              predicate,
                              subject,
                              date);
      }
      else if (arg2Constraint.equals(cycAccess.getKnownConstantByName("UniformResourceLocator"))) {
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(predicate);
          gaf.add(subject);
          gaf.add(LiteralTermInfo.literal);
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              predicate,
                              subject,
                              LiteralTermInfo.literal);
      }
      else
        Log.current.println("*** unhandled literal type constraint " +
        arg2Constraint.cyclify() + "\n");
    }
    Log.current.println("(" + predicate.cyclify() + " " +
    subject.cyclify() + " \"" +
    (String) LiteralTermInfo.literal + "\")\n");
  }
  
  /** Returns the given string argument with embedded double quote characters
   * escaped.
   *
   * @param text The given string.
   * @return the given string argument with embedded double quote characters
   * escaped.
   */
  protected String escaped(String text) {
    String result = text.replaceAll("\"", "\\\"");
    result = result.replace('\n', ' ');
    result = result.replace('\r', ' ');
    return result;
  }
  
  /** Asserts argument constraints on a forward referenced term used
   * in an assertion.
   *
   * @param term the given term
   * @param argConstraints the list of collections for which term must be an instance
   */
  protected void assertForwardArgConstraints(CycFort term, CycList argConstraints)
  throws IOException, UnknownHostException, CycApiException  {
    for (int i = 0; i < argConstraints.size(); i++) {
      CycFort argConstraint = (CycFort) argConstraints.get(i);
      if (! cycAccess.isa(term, argConstraint)) {
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(cycAccess.isa);
          gaf.add(term);
          gaf.add(argConstraint);
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertIsa(term, argConstraint, importMt);
        Log.current.println("*** asserting forward arg constraint " +
                            "(#$isa " + term.cyclify() + " " +
                            argConstraint.cyclify() + ")");
      }
    }
  }
  
  /** Imports the rdf:type triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importIsa(OwlTermInfo subjectTermInfo,
                           OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term = null;
    if (objectTermInfo.shortName.equals("owl:Class"))
      term = importTerm(subjectTermInfo, CLASS_TERM);
    else if (objectTermInfo.shortName.equals("owl:Property") || 
             objectTermInfo.shortName.equals("owl:ObjectProperty") ||
             objectTermInfo.shortName.equals("owl:DatatypeProperty"))
      term = importTerm(subjectTermInfo, PROPERTY_TERM);
    else
      term = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    if (! subjectTermInfo.isURI) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.getKnownConstantByName("owlDefiningOntologyURI"));
        gaf.add(term);
        gaf.add(owlOntologyDefiningURL);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertGaf(importMt,
                            cycAccess.getKnownConstantByName("owlDefiningOntologyURI"),
                            term,
                            owlOntologyDefiningURL);
      if (! subjectTermInfo.isAnonymous)
        if (isFastAssertion) {
          final CycList gaf = new CycList();
          gaf.add(cycAccess.getKnownConstantByName("owlURI"));
          gaf.add(term);
          gaf.add(subjectTermInfo.uri);
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(importMt,
                              cycAccess.getKnownConstantByName("owlURI"),
                              term,
                              subjectTermInfo.uri);
    }
    if (objectTermInfo.shortName.equals("owl:UnambiguousProperty")) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.getKnownConstantByGuid("bd8a36e1-9c29-11b1-9dad-c379636f7270"));
        gaf.add(term);
        gaf.add(new Integer(1));
        gaf.add(cycAccess.getKnownConstantByGuid("bd5880eb-9c29-11b1-9dad-c379636f7270"));
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertArg1FormatSingleEntry(subjectTermInfo.cycFort);
      Log.current.println("(#$arg1Format " +  
                          term.cyclify() + " #$SingleEntryFormat)");
      return;
    }
    CycFort collection = importTerm(objectTermInfo, CLASS_TERM);
    if (! cycAccess.isCollection(collection)) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(collection);
        gaf.add(cycAccess.collection);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaCollection(collection);
      Log.current.println("*** forward reference to collection " + collection.cyclify());
    }
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.isa);
      gaf.add(term);
      gaf.add(collection);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertIsa(term,
                          collection);
    Log.current.println("(#$isa " +
                        term.cyclify() + " " +
                        collection.cyclify() + ")");
  }
  
  /** Imports the rdf:subClassOf triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importGenls(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term = importTerm(subjectTermInfo, CLASS_TERM);
    if (! cycAccess.isCollection(term)) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(term);
        gaf.add(cycAccess.collection);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaCollection(term);
      Log.current.println("*** forward reference to collection " + term.cyclify());
    }
    CycFort collection = importTerm(objectTermInfo, CLASS_TERM);
    if (! cycAccess.isCollection(collection)) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(collection);
        gaf.add(cycAccess.collection);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaCollection(collection);
      Log.current.println("*** forward reference to collection " + collection.cyclify());
    }
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.genls);
      gaf.add(term);
      gaf.add(collection);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGenls(term,
                            collection);
                            Log.current.println("(#$genls " +
                            term.cyclify() + " " +
                            collection.cyclify() + ")");
  }
  
  /** Imports the owl:equivalentProperty triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importEquivalentProperty (OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    importTerm(subjectTermInfo, PROPERTY_TERM);
    if (objectTermInfo.cycFort == null)
      importTerm(objectTermInfo, PROPERTY_TERM);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("genlPreds"));
      gaf.add(subjectTermInfo.cycFort);
      gaf.add(objectTermInfo.cycFort);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGenlPreds(subjectTermInfo.cycFort, objectTermInfo.cycFort);
    Log.current.println("(#$genlPreds " +
                        subjectTermInfo.cycFort.cyclify() + " " +
                        objectTermInfo.cycFort.cyclify() + "\")");
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("genlPreds"));
      gaf.add(objectTermInfo.cycFort);
      gaf.add(subjectTermInfo.cycFort);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGenlPreds(objectTermInfo.cycFort, subjectTermInfo.cycFort);
    Log.current.println("(#$genlPreds " +
                        objectTermInfo.cycFort.cyclify() + " " +
                        subjectTermInfo.cycFort.cyclify() + "\")");
  }
  
  /** Imports the rdfs:domain triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importArg1Isa(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term1 = importTerm(subjectTermInfo, PROPERTY_TERM);
    CycFort term2 = importTerm(objectTermInfo, CLASS_TERM);
    if (! cycAccess.isCollection(term2)) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(term2);
        gaf.add(cycAccess.collection);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaCollection(term2);
      Log.current.println("*** forward reference to collection " + term2.cyclify());
    }
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("argIsa"));
      gaf.add(term1);
      gaf.add(new Integer(1));
      gaf.add(term2);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertArgIsa(term1, 1, term2);
    Log.current.println("(#$arg1Isa " +
                        term1.cyclify() + " " +
                        term2.cyclify() + ")");
  }
  
  /** Imports the rdfs:range triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importArg2Isa(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term1 = importTerm(subjectTermInfo, PROPERTY_TERM);
    CycFort term2 = importTerm(objectTermInfo, CLASS_TERM);
    if (! cycAccess.isCollection(term2)) {
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(term2);
        gaf.add(cycAccess.collection);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaCollection(term2);
      Log.current.println("*** forward reference to collection " + term2.cyclify());
    }
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("argIsa"));
      gaf.add(term1);
      gaf.add(new Integer(2));
      gaf.add(term2);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertArgIsa(term1, 2, term2);
    Log.current.println("(#$arg2Isa " +
                        term1.cyclify() + " " +
                        term2.cyclify() + ")");
  }
  
  /** Imports the rdfs:seeAlso triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importConceptuallyRelated(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term1 = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    CycFort term2 = importTerm(objectTermInfo, INDIVIDUAL_TERM);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("conceptuallyRelated"));
      gaf.add(term1);
      gaf.add(term2);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertConceptuallyRelated(term1, term2, importMt);
    Log.current.println("(#$conceptuallyRelated " +
                        term1.cyclify() + " " +
                        term2.cyclify() + ")");
  }
  
  /** Imports the owl:inverseOf triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importInverseBinaryPredicateOf(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort term1 = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    CycFort term2 = importTerm(objectTermInfo, INDIVIDUAL_TERM);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("inverseBinaryPredicateOf"));
      gaf.add(term1);
      gaf.add(term2);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGaf(importMt,
                          cycAccess.getKnownConstantByName("inverseBinaryPredicateOf"),
                          term1,
                          term2);
    Log.current.println("(#$inverseBinaryPredicateOf " +
                        term1.cyclify() + " " +
                        term2.cyclify() + ")");
  }
  
  /** Imports the rdfs:subPropertyOf triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param objectTermInfo the object OwlTermInfo object
   */
  protected void importGenlPreds(OwlTermInfo subjectTermInfo, OwlTermInfo objectTermInfo)
  throws IOException, UnknownHostException, CycApiException  {
    CycFort specPred = importTerm(subjectTermInfo, PROPERTY_TERM);
    CycFort genlPred = importTerm(objectTermInfo, PROPERTY_TERM);
    if (! cycAccess.isBinaryPredicate((CycFort) genlPred)) {
      // forward reference
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(genlPred);
        gaf.add(cycAccess.binaryPredicate);
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
        final CycList gaf2 = new CycList();
        gaf2.add(cycAccess.getKnownConstantByName("arity"));
        gaf2.add(genlPred);
        gaf2.add(new Integer(2));
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsaBinaryPredicate(genlPred);
      Log.current.println("*** forward reference to predicate " + genlPred.cyclify());
    }
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("genlPreds"));
      gaf.add(specPred);
      gaf.add(genlPred);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGenlPreds(specPred, genlPred);
    Log.current.println("(#$genlPreds " +
                        specPred.cyclify() + " " +
                        genlPred.cyclify() + ")");
  }
  
  /** Imports the owl:imports triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param literalTermInfo the object OwlTermInfo object
   */
  protected void importImports(OwlTermInfo subjectTermInfo, OwlTermInfo literalTermInfo)
  throws IOException, UnknownHostException, CycApiException {
  }
  
  /** Imports the rdfs:comment triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param literalTermInfo the object OwlTermInfo object
   */
  protected void importComment(OwlTermInfo subjectTermInfo, OwlTermInfo literalTermInfo)
  throws IOException, UnknownHostException, CycApiException {
    CycFort term = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    String comment = literalTermInfo.literalValue().replace('\n', ' ');
    comment = StringUtils.escapeQuoteChars(comment);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("comment"));
      gaf.add(term);
      gaf.add(comment);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertComment(term, comment, importMt);
    Log.current.println("(#$comment " +
                        term.cyclify() + " \"" +
                        comment + "\")");
  }
  
  /** Imports the rdfs:label triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param literalTermInfo the object OwlTermInfo object
   */
  protected void importNameString(OwlTermInfo subjectTermInfo, OwlTermInfo literalTermInfo)
  throws IOException, UnknownHostException, CycApiException {
    CycFort term = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    String nameString = literalTermInfo.literalValue();
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("nameString"));
      gaf.add(term);
      gaf.add(nameString);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertNameString(term, nameString, importMt);
    Log.current.println("(#$nameString " +  term.cyclify() + " \"" + nameString + "\")");
    CycList nameStrings = cycAccess.getNameStrings(term, importMt);
    if (! nameStrings.contains(nameString)) {
      if (verbosity > 1) {
        Log.current.println("asserted " + nameString);
        if (nameStrings.size() == 1)
          Log.current.println("queried  " + nameStrings.get(0));
        else
          Log.current.println("queried  " + nameStrings);
      }
    }
  }
  
  /** Imports the rdfs:isDefinedBy triple.
   *
   * @param subjectTermInfo The subject OwlTermInfo object.
   * @param uriTermInfo The object OwlTermInfo object.
   */
  protected void importContainsInformationAbout(OwlTermInfo subjectTermInfo, OwlTermInfo uriTermInfo)
  throws IOException, UnknownHostException, CycApiException {
    CycFort term = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    CycFort resource = importTerm(uriTermInfo, INDIVIDUAL_TERM);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("containsInformationAbout"));
      gaf.add(resource);
      gaf.add(term);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGaf(importMt,
                          cycAccess.getKnownConstantByName("containsInformationAbout"),
                          resource,
                          term);
    Log.current.println("(#$containsInformationAbout " +
                        resource.cyclify() + " " +
                        term.cyclify() + ")");
  }
  
  /** Imports the owl:versionInfo triple.
   *
   * @param subjectTermInfo The subject OwlTermInfo object.
   * @param uriTermInfo The object OwlTermInfo object.
   */
  protected void importOntologyVersionInfo(OwlTermInfo subjectTermInfo, OwlTermInfo literalTermInfo)
  throws IOException, UnknownHostException, CycApiException {
    CycFort term = importTerm(subjectTermInfo, INDIVIDUAL_TERM);
    String version = literalTermInfo.literalValue().replace('\n', ' ');
    version = StringUtils.escapeQuoteChars(version);
    if (isFastAssertion) {
      final CycList gaf = new CycList();
      gaf.add(cycAccess.getKnownConstantByName("ontologyVersionInfo"));
      gaf.add(version);
      gaf.add(term);
      final CycSymbol strength = CycObjectFactory.makeCycSymbol(":default");
      cycAccess.assertHLGaf(gaf, importMt, strength);
    }
    else
      cycAccess.assertGaf(importMt,
                          cycAccess.getKnownConstantByName("ontologyVersionInfo"),
                          term,
                          version);
    Log.current.println("(#$ontologyVersionInfo " + term.cyclify() + " \"" + version + "\")");
  }
  
  /** Imports the given OWL term and returns the Cyc term.
   *
   * @param owlTermInfo the given owl term information
   * @param defaultTermType specifies the default term type to create, either class, individual or property
   * @return the Cyc term resulting from the import of the given OWL term
   */
  protected CycFort importTerm(OwlTermInfo owlTermInfo, int defaultTermType)
  throws IOException, UnknownHostException, CycApiException {
    if (owlTermInfo.cycFort != null &&
        previousOwlTermInfo != null &&
        previousOwlTermInfo.equals(owlTermInfo))
      return previousOwlTermInfo.cycFort;
    
    CycFort cycFort = null;
    if (owlTermInfo.equivalentOwlCycTerm != null) {
      // not an imported term, but an equivalent existing Cyc term
      //Log.current.println("*** not importing " + cycFort.cyclify());
      previousOwlTermInfo = owlTermInfo;
      return owlTermInfo.cycFort;
    }
    else if (owlTermInfo.isURI) {
      cycFort = new CycNart(cycAccess.getKnownConstantByName("URIFn"),
                            owlTermInfo.toString());
      owlTermInfo.cycFort = cycFort;
      previousOwlTermInfo = owlTermInfo;
      return cycFort;
    }
    else {
      cycFort = (CycFort) importedTermsDictionary.get(owlTermInfo.uri);
      if (cycFort != null)
        return cycFort;
      CycFort functor = null;
      if (defaultTermType == CLASS_TERM)
        functor = cycAccess.getKnownConstantByName("OWLClassFn");
      else if (defaultTermType == INDIVIDUAL_TERM)
        functor = cycAccess.getKnownConstantByName("OWLIndividualFn");
      else if (defaultTermType == PROPERTY_TERM)
        functor = cycAccess.getKnownConstantByName("OWLPropertyFn");
      if (owlTermInfo.isAnonymous) 
        cycFort = new CycNart(functor, new CycNart(cycAccess.getKnownConstantByName("URIFn"), owlTermInfo.uri));
      else
        cycFort = new CycNart(functor, owlTermInfo.uri);
    }
    
    if (! cycAccess.isOpenCyc()) {
      if (cycFort instanceof CycConstant && (! cycAccess.isQuotedIsa(cycFort, kbSubsetCollection))) {
        if (verbosity > 1)
          Log.current.println("(#$quotedIsa " + cycFort.cyclify() + " " +
                              kbSubsetCollection.cyclify() + ")");
        final CycList gaf = new CycList();
        gaf.add(cycAccess.getKnownConstantByName("quotedIsa"));
        gaf.add(cycFort);
        gaf.add(kbSubsetCollection);
        if (isFastAssertion) {
          final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
          cycAccess.assertHLGaf(gaf, importMt, strength);
        }
        else
          cycAccess.assertGaf(gaf, importMt);
      }
    }
    if (owlTermInfo.isAnonymous && defaultTermType == CLASS_TERM) {
      if (verbosity > 1)
        Log.current.println("(#$isa " + cycFort.cyclify() + " #OWLAnonymousClass)");
      if (isFastAssertion) {
        final CycList gaf = new CycList();
        gaf.add(cycAccess.isa);
        gaf.add(cycFort);
        gaf.add(cycAccess.getKnownConstantByName("OWLAnonymousClass"));
        final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
        cycAccess.assertHLGaf(gaf, importMt, strength);
      }
      else
        cycAccess.assertIsa(cycFort,
                            cycAccess.getKnownConstantByName("OWLAnonymousClass"),
                            importMt);
    }
    owlTermInfo.cycFort = cycFort;
    previousOwlTermInfo = owlTermInfo;
    importedTermsDictionary.put(owlTermInfo.uri, cycFort);
    return cycFort;
  }
  
  /** Displays the RDF triple.
   *
   * @param subjectTermInfo the subject OwlTermInfo object
   * @param predicateTermInfo the predicate OwlTermInfo object
   * @param objLitTermInfo the object or literal OwlTermInfo object
   */
  protected void displayTriple(OwlTermInfo subjectTermInfo,
                               OwlTermInfo predicateTermInfo,
                               OwlTermInfo objLitTermInfo) {
    if (verbosity < 1)
      return;
    Log.current.println();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(subjectTermInfo.toOriginalString());
    stringBuffer.append(" ");
    stringBuffer.append(predicateTermInfo.toOriginalString());
    stringBuffer.append(" ");
    stringBuffer.append(objLitTermInfo.toOriginalString());
    Log.current.println(stringBuffer.toString());
  }
  
  /** Returns the OwlTerm info of the given RDF resource.
   *
   * @param aResource the RDF resource
   * @param predicateTermInfo when processing the RDF triple object,
   * contains the predicate term info, otherwise is null;
   * @return the OwlTerm info of the given RDF resource
   */
  protected OwlTermInfo resource(AResource aResource, OwlTermInfo predicateTermInfo) {
    OwlTermInfo owlTermInfo = new OwlTermInfo(this);
    String localName;
    String nameSpace;
    Resource resource = translateResource(aResource);
    owlTermInfo.uri = resource.toString();
    if (aResource.isAnonymous()) {
      owlTermInfo.isAnonymous = true;
      owlTermInfo.anonymousId = aResource.getAnonymousID();
      return owlTermInfo;
    }
    else if (aResource.getURI().indexOf("?") > -1) {
      owlTermInfo.isURI = false;
      int index = aResource.getURI().indexOf("?");
      localName = aResource.getURI().substring(index + 1);
      nameSpace = aResource.getURI().substring(0, index + 1);
    }
    else if (! hasUriNamespaceSyntax(aResource.getURI())) {
      owlTermInfo.isURI = true;
      if (! owlTermInfo.mustBeUri(predicateTermInfo))
        owlTermInfo.coerceToNamespace();
      return owlTermInfo;
    }
    else {
      localName = resource.getLocalName();
      nameSpace = resource.getNameSpace();
    }
    if (localName == null ||
    nameSpace == null)
      throw new RuntimeException("Invalid nameSpace " + nameSpace +
      " localName " + localName +
      " for resource " + resource.toString());
    owlTermInfo.localName = localName;
    owlTermInfo.nameSpace = nameSpace;
    String ontologyNickname = getOntologyNickname(nameSpace, resource);
    owlTermInfo.ontologyNickname = ontologyNickname;
    if (ontologyNickname == null) {
      owlTermInfo.shortName = localName;
      if (equivalentOwlCycTerms.containsKey(owlTermInfo.uri)) {
        owlTermInfo.equivalentOwlCycTerm = (CycFort) equivalentOwlCycTerms.get(owlTermInfo.uri);
        owlTermInfo.cycFort = owlTermInfo.equivalentOwlCycTerm;
      }
    }
    else {
      owlTermInfo.shortName = ontologyNickname + ":" + localName;
      if (equivalentOwlCycTerms.containsKey(owlTermInfo.shortName)) {
        owlTermInfo.equivalentOwlCycTerm = (CycFort) equivalentOwlCycTerms.get(owlTermInfo.shortName);
        owlTermInfo.cycFort = owlTermInfo.equivalentOwlCycTerm;
      }
      else if (ontologyNickname != null && ontologyNickname.equals("xsd"))
        Log.current.println("\n*** unhandled primitive datatype: " + owlTermInfo.shortName + "\n");
      }
    return owlTermInfo;
  }
  
  /** Returns the OwlTerm info of the given RDF literal.
   *
   * @param literal the RDF literal
   * @return the OwlTerm info of the given RDF literal
   */
  protected OwlTermInfo literal(ALiteral literal) {
    OwlTermInfo owlTermInfo = new OwlTermInfo(this);
    String literalString = literal.toString();
    if (this.isProbableUri(literalString)) {
      owlTermInfo.isURI = true;
      owlTermInfo.uri = literalString;
    }
    else {
      owlTermInfo.isLiteral = true;
      owlTermInfo.literal = literalString;
    }
    return owlTermInfo;
  }
  
  /** Returns true if the given string is likely to be a URI.
   *
   * @param string the given string
   * @return true if the given string is likely to be a URI
   */
  protected boolean isProbableUri(String string) {
    return
      string.startsWith("http://") ||
      string.startsWith("https://") ||
      string.startsWith("ftp://") ||
      string.startsWith("file:/") ||
      string.startsWith("urn:");
  }
  
  /** Returns true if the given URI has embedded XML namespace separators.
   *
   * @param uri the URI
   * @return true if the given URI has embedded XML namespace separators, otherwise
   * false
   */
  protected boolean hasUriNamespaceSyntax(String uri) {
    return (uri.indexOf(":", 9) > -1) || (uri.indexOf("#") > -1);
  }
  
  /** Records the components of a OWL Restriction.
   *
   * @param subject the RDF Triple anonymous Subject
   * @param predicate the RDF Triple Predicate
   * @param object the RDF Triple Object
   */
  protected void processRestrictionSubject(AResource subject, AResource predicate, AResource object)
  throws IOException, UnknownHostException, CycApiException {
    OwlTermInfo subjectTermInfo = resource(subject, null);
    OwlTermInfo predicateTermInfo = resource(predicate, null);
    OwlTermInfo objectTermInfo = resource(object, null);
    displayTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
    Log.current.println("  predicateTermInfo " + predicateTermInfo.toString() + 
                        " objectTermInfo " + objectTermInfo.toString());
    if (predicateTermInfo.toString().equals("#$isa") &&
        objectTermInfo.toString().equals("owl:Restriction")) {
      owlRestriction = new OwlRestriction(this);
      owlRestriction.anonymousId = subject.getAnonymousID();
    }
    else if (owlRestriction == null) {
      // Not a Restriction, but an anonymous Class.
      importTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
      return;
    }
    if (subjectTermInfo.cycFort == null  &&
        subjectTermInfo.isAnonymous) {
    }
    owlRestriction.fromClass = subjectTermInfo.cycFort;
    if (predicateTermInfo.toString().equals("owl:onProperty")) {
      if (objectTermInfo.cycFort == null)
        importTerm(objectTermInfo, PROPERTY_TERM);
      owlRestriction.property = objectTermInfo.cycFort;
    }
    else if (predicateTermInfo.toString().equals("#$owlRestrictionAllValuesFrom")) {
      if (objectTermInfo.cycFort == null)
        importTerm(objectTermInfo, CLASS_TERM);
      owlRestriction.toClasses.add(objectTermInfo.cycFort);
    }
    else if (predicateTermInfo.toString().equals("owl:someValuesFrom")) {
      if (objectTermInfo.cycFort == null)
        importTerm(objectTermInfo, CLASS_TERM);
      owlRestriction.hasClasses.add(objectTermInfo.cycFort);
    }
    else if (predicateTermInfo.toString().equals("#$isa")) {
    }
    else 
      throw new RuntimeException("Unexpected restriction property " + predicateTermInfo.toString());
  }
  
  /** Records the components of a OWL Restriction.
   *
   * @param subject the RDF Triple anonymous Subject
   * @param predicate the RDF Triple Predicate
   * @param literal the RDF Triple Literal
   */
  protected void processRestrictionSubject(AResource subject, AResource predicate, ALiteral literal)
  throws IOException, UnknownHostException, CycApiException {
    OwlTermInfo subjectTermInfo = resource(subject, null);
    OwlTermInfo predicateTermInfo = resource(predicate, null);
    OwlTermInfo literalTermInfo = literal(literal);
    displayTriple(subjectTermInfo, predicateTermInfo, literalTermInfo);
    if (owlRestriction == null) {
      // Not a Restriction, but an anonymous Class.
      importTriple(subjectTermInfo, predicateTermInfo, literalTermInfo);
      return;
    }
    if (predicateTermInfo.toString().equals("owl:maxCardinality") ||
        predicateTermInfo.toString().equals("owl:cardinality"))
      owlRestriction.maxCardinality = new Integer(literalTermInfo.literalValue());
    else
      //TODO
      Log.current.println("\n*** unimplemented restriction triple \n    " +
                          subjectTermInfo.toString() + " " +
                          predicateTermInfo.toString() + " " +
                          literalTermInfo.toString() + "\n");
  }
  
  /** Records the components of a OWL Restriction.
   *
   * @param subject the RDF Triple anonymous Subject
   * @param predicate the RDF Triple Predicate
   * @param object the RDF Triple Object
   */
  protected void processRestrictionObject(AResource subject, AResource predicate, AResource object)
  throws IOException, UnknownHostException, CycApiException {
    OwlTermInfo subjectTermInfo = resource(subject, null);
    OwlTermInfo predicateTermInfo = resource(predicate, null);
    OwlTermInfo objectTermInfo = resource(object, null);
    displayTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
    if (owlRestriction == null) {
      // Not a Restriction, but an anonymous Class.
      try {
        importTriple(subjectTermInfo, predicateTermInfo, objectTermInfo);
      }
      catch (Exception e) {
        Log.current.printStackTrace(e);
        System.exit(1);
      }
      return;
    }
    if (subjectTermInfo.cycFort == null)
      importTerm(subjectTermInfo, CLASS_TERM);
    if (predicateTermInfo.toString().equals("#$genls")) {
      owlRestriction.fromClass = subjectTermInfo.cycFort;
    }
    else if (predicateTermInfo.toString().equals("owl:equivalentClass")) {
      //TODO see if this is actually needed in the KB.
      owlRestriction = null;
      return;
    }
    else
      throw new RuntimeException("Unexpected restriction property " +
      predicateTermInfo.toString());
    try {
      owlRestriction.formInterArgConstraints();
      if (owlRestriction.interArgIsaConstraint != null) {
        if(actuallyImport)
          if (isFastAssertion) {
            final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
            cycAccess.assertHLGaf(owlRestriction.interArgIsaConstraint, importMt, strength);
          }
          else
            cycAccess.assertGaf(owlRestriction.interArgIsaConstraint, importMt);
        Log.current.println(owlRestriction.interArgIsaConstraint.cyclify());
      }
      if (owlRestriction.interArgFormatConstraint != null) {
        if(actuallyImport)
          if (isFastAssertion) {
            final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
            cycAccess.assertHLGaf(owlRestriction.interArgFormatConstraint, importMt, strength);
          }
          else
            cycAccess.assertGaf(owlRestriction.interArgFormatConstraint, importMt);
        Log.current.println(owlRestriction.interArgFormatConstraint.cyclify());
      }
      Log.current.println();
    }
    catch (Exception e) {
      Log.current.printStackTrace(e);
      System.exit(1);
    }
    owlRestriction = null;
  }
  
  /** Returns the ontology nickname for the given XML namespace.
   *
   * @param nameSpace the XML namespace for which the nickname is sought
   * @param resource the resource containing the namespace, used for error messages
   * @return the ontology nickname for the given XML namespace
   */
  protected String getOntologyNickname(String nameSpace, Resource resource) {
    int len = nameSpace.length() - 1;
    String key = nameSpace.substring(0, len);
    String nickname = (String) ontologyNicknames.get(key);
    return nickname;
  }
  
  /** Converts an ARP resource into a Jena resource.
   *
   * @param aResource The ARP resource.
   * @return The Jena resource.
   */
  protected Resource translateResource(AResource aResource) {
    if (aResource.isAnonymous()) {
      String id = aResource.getAnonymousID();
      Resource rr = (Resource) aResource.getUserData();
      if (rr == null) {
        rr = new ResourceImpl();
        aResource.setUserData(rr);
      }
      return rr;
    }
    else
      return new ResourceImpl(aResource.getURI());
  }
  
  /** Gets the asserted mappings between OWL/RDFS/RDF terms and Cyc terms. */
  protected void getMappings()
  throws IOException, UnknownHostException, CycApiException {
    getMappings("OWLMappingMt");
  }
  
  /** Gets the asserted mappings between OWL/RDFS/RDF terms and Cyc terms.
   *
   * @param mappingMt the microtheory from which the assertions are gathered
   */
  protected void getMappings(String mappingMt)
  throws IOException, UnknownHostException, CycApiException {
    equivalentOwlCycTerms = new HashMap();
    CycList mappings =
      cycAccess.getSynonymousExternalConcepts("WorldWideWeb-DynamicIndexedInfoSource",
                                              mappingMt);
    for (int i = 0; i < mappings.size(); i++) {
      CycList pair = (CycList) mappings.get(i);
      CycFort cycTerm = (CycFort) pair.first();
      String owlTerm = (String) pair.second();
      Log.current.println(owlTerm + " --> " + cycTerm.cyclify());
      equivalentOwlCycTerms.put(owlTerm, cycTerm);
    }
  }
  
  /** Records the OWL term information for Cyc import. */
  protected class OwlTermInfo {
    /** the parent ImportOwl instance */
    protected ImportOwl parent;
    
    /** when true, indicates that the term is anonymous */ 
    protected boolean isAnonymous = false;
    
    /** when true, indicates that the term is a URI */
    protected boolean isURI = false;
    
    /** when true, indicates that the term is a literal */
    protected boolean isLiteral = false;
    
    /** the anonymous term id */
    protected String anonymousId;
    
    /** the XML name space */
    protected String nameSpace;
    
    /** the ontology (namespace) nickname */
    protected String ontologyNickname;
    
    /** the local name */
    protected String localName;
    
    /** the short name consisting of nickname : name */
    protected String shortName;
    
    /** the uri */
    protected String uri;
    
    /** the literal */
    protected String literal;
    
    /** the equivalent Cyc term, if any */
    protected CycFort equivalentOwlCycTerm;
    
    /** the CycFort for this term */
    protected CycFort cycFort;
    
    /** Constructs a new OwlTermInfo object. */
    public OwlTermInfo(ImportOwl parent) {
      this.parent = parent;
    }
    
    /** Returns true if some object equals this object
     *
     * @param object the object for equality comparison
     * @return equals boolean value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
      if (! (object instanceof OwlTermInfo))
        return false;
      return this.toString().equals(object.toString());
    }
    
    /** Returns a non-substituted string representation of this object.
     *
     * @return a non-substituted string representation of this object
     */
    public String toOriginalString() {
      if (equivalentOwlCycTerm == null)
        return this.toString();
      else
        return shortName;
    }
    
    /** Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
      if (isAnonymous)
        return "anon-" + anonymousId;
      else if (isURI)
        return uri;
      else if (isLiteral)
        return "\"" + literal + "\"";
      else if (equivalentOwlCycTerm != null)
        return equivalentOwlCycTerm.cyclify();
      else
        return uri;
    }
    
    /** Returns the literal value of this object.
     *
     * @return the literal value of this object
     */
    public String literalValue() {
      if (isLiteral)
        return literal;
      else
        throw new RuntimeException(this.toString() + " is not a literal");
    }
    
    /**Coerces a namespace:localname from the URI.
     * For example, http://xmlns.com/foaf/0.1/Person -->
     * http://xmlns.com/foaf/0.1#Person
     */
    public void coerceToNamespace() {
      int index = uri.lastIndexOf("/");
      nameSpace = uri.substring(0, index);
      localName = uri.substring(index + 1);
      if (localName.equals(""))
        return;
      ontologyNickname = (String) ontologyNicknames.get(nameSpace);
      if (ontologyNickname == null) {
        Log.current.println("*** nickname not found for " + nameSpace +
                            "\nuri " + uri);
        ontologyNickname = "unknown";
      }
      shortName = ontologyNickname + ":" + localName;
      isURI = false;
    }
    
    /** Returns true if the uri does not represent an RDF
     * object.  Heuristic patterns are used.
     *
     * @param predicateTermInfo when present indicates that this
     * is the object of the RDF triple
     * @return true if the uri does not represent an RDF
     * object
     */
    public boolean mustBeUri(OwlTermInfo predicateTermInfo) {
      if (predicateTermInfo != null) {
        if (predicateTermInfo.toString().equals("owl:imports") ||
            predicateTermInfo.toString().equals("rdfs:isDefinedBy") ||
            predicateTermInfo.toString().equals("rdfs:seeAlso"))
          return true;
      }
      if (parent.ontologyNicknames.containsKey(uri))
        return true;
      if (uri.endsWith(".owl") ||
          uri.startsWith("news:"))
        return true;
      else
        return false;
    }
    
    /** Returns true if this term has an equivalent existing Cyc term.
     *
     * @return true if this term has an equivalent existing Cyc term
     */
    public boolean hasEquivalentCycTerm() {
      return equivalentOwlCycTerm != null;
    }
  }
  
  
  /** Records property use restrictions which get imported
   * as Cyc interArgIsa1-2 or interArgFormat1-2 assertions.
   */
  protected class OwlRestriction {
    
    /** the parent ImportOwl object */
    ImportOwl importOwl;
    
    /** Identifies all the RDF triples which contribute to this OWL Restriction. */
    public String anonymousId;
    
    /** The domain (Cyc arg1) class whose instances are the subject of the property. */
    public CycFort fromClass;
    
    /** The property (Cyc predicate arg0) which relates the subject and predicate instances. */
    public CycFort property;
    
    /** The range (Cyc arg2) classes whose instances may be objects of the property in the
     * cases where subject is an instance of the given fromClass.
     */
    public ArrayList toClasses = new ArrayList();
    
    /** The range (Cyc arg2) classes whose one or more instances are objects of the property in the
     * cases where subject is an instance of the given fromClass.
     */
    public ArrayList hasClasses = new ArrayList();
    
    /** the maxCardinality restriction */
    public Integer maxCardinality;
    
    /** the interArgIsa constraint sentence */
    public CycList interArgIsaConstraint;
    
    /** the interArgFormat constraint sentence */
    public CycList interArgFormatConstraint;
    
    /** Constructs a new OwlRestriction object. */
    public OwlRestriction(ImportOwl importOwl) {
      this.importOwl = importOwl;
    }
   
    /** Forms the restriction constraints. */
    protected void formInterArgConstraints()
    throws IOException, UnknownHostException, CycApiException {
      if (maxCardinality != null)
        formInterArgFormatConstraint();
      if (toClasses.size() > 0)
        formInterArgIsaConstraint();
    }
    
    /** Forms the interArgIsa1-2 constraint. */
    protected void formInterArgIsaConstraint()
    throws IOException, UnknownHostException, CycApiException {
      if (toClasses.size() == 1) {
        if (! cycAccess.isBinaryPredicate(property)) {
          if (isFastAssertion) {
            final CycList gaf = new CycList();
            gaf.add(cycAccess.isa);
            gaf.add(property);
            gaf.add(cycAccess.binaryPredicate);
            final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
            cycAccess.assertHLGaf(gaf, importMt, strength);
            final CycList gaf2 = new CycList();
            gaf2.add(cycAccess.getKnownConstantByName("arity"));
            gaf2.add(property);
            gaf2.add(new Integer(2));
            cycAccess.assertHLGaf(gaf, importMt, strength);
          }
          else
            cycAccess.assertIsaBinaryPredicate(property);
          Log.current.println("*** forward reference to predicate " + property.cyclify());
        }
        CycFort toClass = (CycFort) toClasses.get(0);
        if (! cycAccess.isCollection(toClass)) {
          if (isFastAssertion) {
            final CycList gaf = new CycList();
            gaf.add(cycAccess.isa);
            gaf.add(toClass);
            gaf.add(cycAccess.collection);
            final CycSymbol strength = CycObjectFactory.makeCycSymbol(":monotonic");
            cycAccess.assertHLGaf(gaf, importMt, strength);
          }
          else
            cycAccess.assertIsaCollection(toClass);
          Log.current.println("*** forward reference to collection " + toClass.cyclify());
        }
        String interArgIsaConstraintString =
          "(#$interArgIsa1-2 " +
          property.cyclify() + " " +
          fromClass.cyclify() + " " +
          toClass.cyclify() + ")";
        interArgIsaConstraint = cycAccess.makeCycList(interArgIsaConstraintString);
      }
      else
        //TODO
        throw new RuntimeException("Unhandled interArgIsa restriction case: " +
        toClasses.toString());
    }
   
    /** Forms the interArgFormat1-2 constraint. */
    protected void formInterArgFormatConstraint()
    throws IOException, UnknownHostException, CycApiException {
      if (this.maxCardinality.intValue() == 1) {
        String interArgFormatConstraintString =
          "(#$interArgFormat1-2 " +
          property.cyclify() + " " +
          fromClass.cyclify() + " " +
          "#$SingleEntry)";
        interArgFormatConstraint = cycAccess.makeCycList(interArgFormatConstraintString);
      }
      else
        //TODO
        throw new RuntimeException("Unhandled maxCardinality restriction case: " +
        maxCardinality.toString());
    }
    
    /** Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("anon-");
      stringBuffer.append(anonymousId);
      stringBuffer.append(": ");
      stringBuffer.append(fromClass);
      stringBuffer.append(" ");
      stringBuffer.append(property);
      stringBuffer.append(" (");
      for (int i = 0; i < toClasses.size(); i++) {
        if (i > 0)
          stringBuffer.append(", ");
        stringBuffer.append(toClasses.get(i).toString());
      }
      stringBuffer.append(") ");
      if (interArgIsaConstraint != null)
        stringBuffer.append(interArgIsaConstraint.cyclify());
      return stringBuffer.toString();
    }
  }
  
  /** Implements a SAX ErrorHandler to report the XML parsing errors. */
  protected class MyErrorHandler implements ErrorHandler {
    
    /** Constructs a new MyErrorHandler object. */
    public MyErrorHandler() {
    }
    
    /** Handles SAX XML parsing warnings. */
    public void warning(SAXParseException exception) {
      Log.current.println("SAX warning: " + exception.getMessage());
    }
    
    /** Handles SAX XML parsing errors. */
    public void error(SAXParseException exception) {
      Log.current.println("SAX error: " + exception.getMessage());
    }
    
    /** Handles SAX XML parsing fatal errors. */
    public void fatalError(SAXParseException exception) {
      Log.current.println("SAX fatal error: " + exception.getMessage());
    }
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** Another RDF Parser instance. */
  protected ARP arp;
  
  /** Cyc terms which have semantic counterparts in OWL.  OWL term --> Cyc term */
  protected static HashMap equivalentOwlCycTerms;
  
  /** Ontology library nicknames, which become namespace identifiers
   * upon import into Cyc.
   * namespace uri --> ontologyNickname
   */
  protected HashMap ontologyNicknames = new HashMap();
  
  /** Previously imported term used to avoid redundant assertions. */
  protected OwlTermInfo previousOwlTermInfo = null;
  
  /** URL string which defines the imported owl ontology */
  protected String owlPath;
  
  /** the ontology nickname */
  protected String nickname;
  
  /** URL which defines the imported owl ontology */
  protected String owlOntologyDefiningURL;
  
  /** Ontology import microtheory name. */
  protected String importMtName;
  
  /** Ontology import genl microtheory name. */
  protected String importGenlMtName;
  
  /** Ontology import microtheory. */
  protected CycConstant importMt;
  
  /**The current OWL Restriction object being constructed from sequential RDF triples. */
  protected OwlRestriction owlRestriction;
  
  /** The KB Subset collection which identifies ontology import terms in Cyc. */
  protected CycConstant kbSubsetCollection;
  
  /** Character encoding scheme of the OWL input (e.g. UTF-8), specified if not ASCII. */
  protected String characterEncoding = null;
  
  /** indicates that a (#$OWLClassFn (#$URIFn <uri-string>)) is to be created */
  protected static final int CLASS_TERM = 1;
  
  /** indicates that a (#$OWLIndividualFn (#$URIFn <uri-string>)) is to be created */
  protected static final int INDIVIDUAL_TERM = 2;
  
  /** indicates that a (#$OWLPropertyFn (#$URIFn <uri-string>)) is to be created */
  protected static final int PROPERTY_TERM = 3;
  
  /** the dictionary of imported terms, uri --> CycFort */
  protected HashMap importedTermsDictionary = new HashMap();
  
  /** CycAccess object to manage api connection the the Cyc server. */
  final private CycAccess cycAccess;
  
  /** indicates that fast HL asertions are used during the OWL import (no forward inference) */
  public static boolean isFastAssertion = false; 
    
  /** the start time milliseconds */
  private long startMilliseconds;
  
  //// Main
  
  
}
