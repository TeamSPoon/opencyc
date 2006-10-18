package  org.opencyc.xml;

//// Internal Imports
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.cycobject.ELMt;
import org.opencyc.cycobject.Guid;
import org.opencyc.util.StringUtils;
import org.opencyc.util.UUID;

//// External Imports
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import com.bbn.semweb.owl.vowlidator.Validator;
import com.bbn.semweb.owl.vowlidator.ValidatorAPI;
import com.bbn.semweb.owl.vowlidator.indications.Indication;
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
 * <p>Copyright (c) 2003 - 2006 Cycorp, Inc., license is open source GNU LGPL.
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
  public ExportOwl(final CycAccess cycAccess) {
    this(cycAccess, null);
  }
  
  /**
   * Constructs a new ExportOwl object given the CycAccess object.
   *
   * @param cycAccess The CycAccess object which manages the api connection.
   * @param uuid the given identifier that is returned with each progress event notification
   */
  public ExportOwl(final CycAccess cycAccess, final UUID uuid) {
    super(cycAccess);
    this.uuid = uuid;
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
  
  /** the indicator for whether to export qualificed cardinality restrictions */
  public boolean exportQualifiedCardinalityRestrictions = false;
  
  public static String ALL_EXPORT_CATEGORIES = "all";
  public static String EXPORT_COLLECTION_CATEGORY = "collections";
  public static String EXPORT_PREDICATE_CATEGORY = "predicates";
  public static String EXPORT_INDIVIDUAL_CATEGORY = "individuals";
  
  /** Returns the exportable content for the given term and list of selectedTerms.
   *
   * @param term the term for which exportable content is to be presented
   * @param selectedCycForts the list of all selected terms which may appear in exportable content
   * @param isExportLimitedToOpenCycContent the indicator that the OWL export should be limited to OpenCyc content
   */
  public String exportableContent(final CycFort term, 
                                  final CycList selectedCycForts, 
                                  final boolean isExportLimitedToOpenCycContent) throws Exception {
    //// Preconditions
    assert selectedCycForts != null : "selectedCycForts must not be null";
    
    this.selectedCycForts = selectedCycForts;
    this.isExportLimitedToOpenCycContent = isExportLimitedToOpenCycContent;
    selectedCycForts.addNew(term);
    exportCommand = OntologyExport.EXPORT_SELECTED_TERMS;
    cycKbSubsetFilter = cycKbSubsetFilterGuid == null ? null : cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
    cycKbSubsetCollection = cycKbSubsetFilter;
    logger.info("Obtaining exportable content for " + term.cyclify());
    initializeMappingDictionary();
    ensureSelectedTermsAreReified();
    partitionSelectedTermsByType();
    createRdfNode();
    createTermNode(term);
    final OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
    final CharArrayWriter charArrayWriter = new CharArrayWriter();
    final BufferedWriter owlOut = new BufferedWriter(charArrayWriter);
    final XMLSerializer xmlSerializer = new XMLSerializer(owlOut, outputFormat);
    xmlSerializer.asDOMSerializer();
    xmlSerializer.serialize(document);
    owlOut.close();
    final String exportableContent = charArrayWriter.toString();
    logger.info("exportable content:\n" + exportableContent);
    
    //// Postconditions
    assert exportableContent != null : "exportableContent must not be null";
    assert exportableContent.length() > 0 : "exportableContent must not be an empty string";
    
    return exportableContent;
  }
  
  /**
   * Exports the desired KB content into OWL.
   */
  public void export(final int exportCommand) throws Exception {
    this.exportCommand = exportCommand;
    logger.info("inferenceMt: " + inferenceMt.toString());
    if (areRulesExported)
      logger.info("Rules will be exported in SWRL");
    if (isExportLimitedToOpenCycContent)
      logger.info("Export is limited to OpenCyc content");
    //cycAccess.traceOn();
    reportProgress(percentProgress, null, false);
    if (isCancelled)
      return;
    setup();
    final CycConstant cycKBSubsetCollection = cycAccess.getConstantByName("CycKBSubsetCollection");
    if (cycKBSubsetCollection != null) {
      allKbSubsetCollections = cycAccess.getAllInstances(cycKBSubsetCollection);
      Collections.sort(allKbSubsetCollections);
      logger.finer("allKbSubsetCollections: " + allKbSubsetCollections.toString());
    }
    if (isCancelled) {
      cleanUp();
      return;
    }
    logger.info("Getting terms from Cyc");
    if (exportCommand == OntologyExport.EXPORT_ENTIRE_KB)
      selectedCycForts = cycAccess.getAllInstances(cycAccess.thing);
    else if ((exportCommand == OntologyExport.EXPORT_KB_SUBSET) ||
             (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) ||
             (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)) {
      selectedCycForts = cycAccess.getAllQuotedInstances(cycKbSubsetCollection, inferenceMt);
      if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE)
        includeUpwardClosure = true;
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_ASSERTIONS)
      prepareSelectedAssertions();
    else if (exportCommand != OntologyExport.EXPORT_SELECTED_TERMS) {
      // EXPORT_KB_SUBSET_BELOW_TERM
      selectedCycForts = cycAccess.getAllSpecs(rootTerm, inferenceMt);
      selectedCycForts.add(rootTerm);
    }
    if (isCancelled) {
      cleanUp();
      return;
    }
    logger.info("Selected " + selectedCycForts.size() + " terms");
    percentProgress += 1.0d;
    reportProgress(percentProgress, null, false);
    ensureSelectedTermsAreReified();
    if (includeUpwardClosure) {
      upwardClosureCycForts = gatherUpwardClosure(selectedCycForts);
      if (isCancelled) {
        cleanUp();
        return;
      }
      logger.info("Upward closure added " + upwardClosureCycForts.size() + " terms");
      selectedCycForts.addAll(upwardClosureCycForts);
      logger.info("All selected " + selectedCycForts.size() + " terms");
      percentProgress += 5.0d;
      reportProgress(percentProgress, null, false);
    }
    
    if (isCancelled) {
      cleanUp();
      return;
    }
    if (! includeNonAtomicTerms) {
      logger.info("Removing positive arity narts.");
      final CycList temp = new CycList();
      for (int i = 0; i < selectedCycForts.size(); i++) {
        final CycObject cycObject = (CycObject) selectedCycForts.get(i);
        if (cycObject instanceof CycConstant ||
           (cycObject instanceof CycNart && (((CycNart) cycObject).getArguments().isEmpty())))
          temp.add(cycObject);
        logger.info("dropped term " + cycObject.cyclify());
      }
      selectedCycForts = temp;
    }
    selectedCycForts = omitTermsWithoutComments(selectedCycForts);
    sortCycObjects(selectedCycForts);
    logger.info("\nselectedCycForts");
    for (int i = 0; i < selectedCycForts.size(); i++)
      logger.info(((CycObject) selectedCycForts.get(i)).cyclify());
    logger.info("");
    partitionSelectedTermsByType();
    final double PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS = 2.0;
    if (includeDerivedNarts) {
      if (isCancelled) {
        cleanUp();
        return;
      }
      logger.info("Adding derived narts");
      gatherSubcollectionOfWithRelationFromFns();
      owlSelectedClasses.addAllNew(subcollectionOfWithRelationFromFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherSubcollectionOfWithRelationToFns();
      owlSelectedClasses.addAllNew(subcollectionOfWithRelationToFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherSubcollectionOfWithRelationFromTypeFns();
      owlSelectedClasses.addAllNew(subcollectionOfWithRelationFromTypeFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherSubcollectionOfWithRelationToTypeFns();
      owlSelectedClasses.addAllNew(subcollectionOfWithRelationToTypeFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherCollectionIntersection2Fns();
      owlSelectedClasses.addAllNew(collectionIntersection2Fns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherCollectionIntersectionFns();
      owlSelectedClasses.addAllNew(collectionIntersectionFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
      if (isCancelled) {
        cleanUp();
        return;
      }
      gatherCollectionUnionFns();
      owlSelectedClasses.addAllNew(collectionUnionFns);
      percentProgress += PERCENT_PROGRESS_INCREMENT_FOR_DERIVED_NARTS;
      reportProgress(percentProgress, null, false);
    }
    populatePercentProgressDictionary();
    logger.info("Building OWL model");
    long startTimeMillis = System.currentTimeMillis();
    for (int i = 0; i < owlSelectedClasses.size(); i++) {
      if (isCancelled) {
        cleanUp();
        return;
      }
      final CycObject cycObject = (CycObject) owlSelectedClasses.get(i);
      logger.info(cycObject.toString() + "  Collection");
      createTermNode(cycObject);
    }
    if (! owlSelectedClasses.isEmpty())
      logger.fine("Exported " + owlSelectedClasses.size() + " classes at with an average duration of " +
                  ((System.currentTimeMillis() - startTimeMillis) / owlSelectedClasses.size()) +
                  " milliseconds each");
    startTimeMillis = System.currentTimeMillis();
    for (int i = 0; i < owlSelectedProperties.size(); i++) {
      if (isCancelled) {
        cleanUp();
        return;
      }
      final CycObject cycObject = (CycObject) owlSelectedProperties.get(i);
      logger.info(cycObject.toString() + "  BinaryPredicate");
      createTermNode(cycObject);
    }
    if (! owlSelectedProperties.isEmpty())
      logger.fine("Exported " + owlSelectedProperties.size() + " properties at with an average duration of " +
                  ((System.currentTimeMillis() - startTimeMillis) / owlSelectedProperties.size()) +
                  " milliseconds each");
    startTimeMillis = System.currentTimeMillis();
    sortCycObjects(inversePredicates);
    for (int i = 0; i < inversePredicates.size(); i++) {
      if (isCancelled) {
        cleanUp();
        return;
      }
      final CycObject cycObject = (CycObject) inversePredicates.get(i);
      logger.info(cycObject.toString() + "  inverse BinaryPredicate");
      createTermNode(cycObject);
    }
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      if (isCancelled) {
        cleanUp();
        return;
      }
      final CycObject cycObject = (CycObject) owlSelectedIndividuals.get(i);
      logger.info(cycObject.toString() + "  Individual");
      createTermNode(cycObject);
    }
    if (! owlSelectedIndividuals.isEmpty())
      logger.fine("Exported " + owlSelectedIndividuals.size() + " individuals at with an average duration of " +
                  ((System.currentTimeMillis() - startTimeMillis) / owlSelectedIndividuals.size()) +
                  " milliseconds each");
    if (! owlSelectedIndividuals.isEmpty() && (! isExportLimitedToOpenCycContent))
      createAllDifferentNode();
    if (! rules.isEmpty())
      createRuleNodes();
    serializeDocument();
    displayExportedTerms();
    if (isCancelled) {
      cleanUp();
      return;
    }
    final String validationReport = validateOWLExport();
    cleanUp();
    reportProgress(100, validationReport, true);
    logger.info("OWL export completed");
  }
  
  /** Executes the Vowlidator application to validate the exported OWL file.
   *
   * @return the string of validation messages
   */
  public String validateOWLExport() throws Exception {
    //// Preconditions
    assert outputPath != null : "outputPath must not be null";
    assert outputPath.length() > 0 : "outputPath must not be an empty string";
    
    if (areRulesExported)
      return "The Vowlidator feature is not available when rules are contained in the OWL export.";

    String indicationStrings = null;
    if ((new File("preferences.xml")).exists()) {
      logger.fine("Validating OWL file at " + outputPath);
      logger.fine("Working directory " + System.getProperty("user.dir"));

      final String[] args = {outputPath};
      final ValidatorAPI validatorAPI = new ValidatorAPI(args);
      final StringBuffer stringBuffer = new StringBuffer(10000);
      final Vector indications = validatorAPI.run(false);
      for (final Enumeration indications_enum = indications.elements(); indications_enum.hasMoreElements(); ) {
        final Indication indication = (Indication) indications_enum.nextElement();
        stringBuffer.append(indication.toString());
        stringBuffer.append('\n');
      }
      indicationStrings = stringBuffer.toString();
    }
    else {
      indicationStrings = "The Vowlidator feature is not available with Java Web Start";
    }
    logger.fine(indicationStrings);
    
    //// Postconditions
    assert indicationStrings != null : "indicationStrings must not be null";
    assert indicationStrings.length() > 0 : "indicationStrings must not be an empty string";
    
    return indicationStrings;
  }
  
  /** Sets the selected Cyc Forts.
   *
   * @param selectedCycFort the selected Cyc forts
   */
  public void setSelectedCycForts(final CycList selectedCycForts) {
    //// Preconditions
    assert selectedCycForts != null : "selectedCycForts must not be null";
    
    this.selectedCycForts = selectedCycForts;
  }
  
  /** Sets the list of applicable binary predicates
   *
   * @param applicableBinaryPredicates the the list of applicable predicates
   */
  public void setApplicableBinaryPredicates(final CycList applicableBinaryPredicates) {
    //// Preconditions
    assert applicableBinaryPredicates != null : "applicableBinaryPredicates must not be null";
    
    this.applicableBinaryPredicates = applicableBinaryPredicates;
  }
  
  /** Sets the output path for the OWL export file.
   *
   * @param outputPath the output path for the OWL export file
   */
  public void setOutputPath(final String outputPath) {
    this.outputPath = outputPath;
  }
  
  /** Sets the inference microtheory.
   *
   * @param inferenceMt the inference microtheory
   */
  public void setInferenceMt(final ELMt inferenceMt) {
    this.inferenceMt = inferenceMt;
  }
  
  /** Sets the license text.
   *
   * @param licenseText the license text
   */
  public void setLicenseText(final String licenseText) {
    this.licenseText = licenseText;
  }
  
  /** Sets the indicator that the OWL export should be limited to OpenCyc content.
   *
   * @param isExportLimitedToOpenCycContent the indicator that the OWL export should be limited to OpenCyc content
   */
  public void setIsExportLimitedToOpenCycContent(final boolean isExportLimitedToOpenCycContent) {
    this.isExportLimitedToOpenCycContent = isExportLimitedToOpenCycContent;
  }
 
  /** Sets the indicator that the OWL export should include rules in SWRL (Semantic Web Rule Language).
   *
   * @param areRulesExported the indicator that the OWL export should include rules in SWRL (Semantic Web Rule Language)
   */
  public void setAreRulesExported(final boolean areRulesExported) {
    this.areRulesExported = areRulesExported;
  }

  /** Returns an XML compliant name for the given non-atomic term.
   *
   * @param nonAtomicTerm the given non-atomic term
   * @return an XML compliant name for the given non-atomic term
   */
  public String xmlNonAtomicTermName(final CycObject nonAtomicTerm)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert nonAtomicTerm != null : "nonAtomicTerm must not be null";
    
    final CycList natFormula = (nonAtomicTerm instanceof CycNart) ? ((CycNart) nonAtomicTerm).toDeepCycList() : (CycList) nonAtomicTerm;
    String xmlName = (String) nartXMLNames.get(natFormula.toString());
    logger.finest("      natFormula: " + ((List) natFormula).toString() + " xmlName: " + xmlName);
    if (xmlName != null)
      return xmlName;
    xmlName = cycAccess.getGeneratedPhrase(nonAtomicTerm);
    if (xmlName.indexOf("</") > -1)
      xmlName = stripHTMLTags(xmlName);
    xmlName = xmlName.replace('-', '_');
    xmlName = xmlName.replace(' ', '_');
    xmlName = xmlName.replace('"', '_');
    xmlName = xmlName.replace('\'', '_');
    xmlName = StringUtils.change(xmlName, "?", "");
    xmlName = StringUtils.change(xmlName, ",", "");
    xmlName = StringUtils.change(xmlName, "{", "");
    xmlName = StringUtils.change(xmlName, "}", "");
    xmlName = StringUtils.change(xmlName, "#$", "");
    xmlName = StringUtils.change(xmlName, "#<(", "_");
    xmlName = StringUtils.change(xmlName, ")>", "_");
    if (xmlName.indexOf('_') == -1)
      xmlName = xmlName + "_";
    if (! cycAccess.isPredicate(nonAtomicTerm, inferenceMt))
      xmlName = xmlName.substring(0, 1).toUpperCase() + xmlName.substring(1);
    if (nartXMLNames.containsValue(xmlName))
      xmlName = xmlName + "_" + xmlNameSequence++;
    assert ! nartXMLNames.containsValue(xmlName) : "duplicate name for " + nonAtomicTerm.toString() + " which is " + xmlName;
    nartXMLNames.put(natFormula.toString(), xmlName);
    
    //// Postconditions
    assert xmlName != null : "xmlName must not be null";
    assert xmlName.length() > 0 : "xmlName must not be an empty string";
    
    return xmlName;
  }
  
  /** Strips HTML format tags from the given string. 
   *
   * @param string the given string
   * @return the given string stripped of HTML tags
   */
  private String stripHTMLTags(final String string) {
    if (string == null)
      throw new NullPointerException("string must not be null");
    
    if (string.length() < 3)
      return string;
    final StringBuffer stringBuffer = new StringBuffer(string.length());
    boolean withinHTMLTag = false;
    for (int i = 0; i < string.length(); i++) {
      final char c = string.charAt(i);
      if (c == '<')
        withinHTMLTag = true;
      else if (c == '>')
        withinHTMLTag = false;
      else if (! withinHTMLTag)
        stringBuffer.append(c);
    }
    return stringBuffer.toString();
  }
  
  /** Adds an OWL export progress listener.
   *
   * @param owlExportProgressEventListener a listener for OWL export progress events
   */
  public synchronized void addListener(final OWLExportProgressEventListener owlExportProgressEventListener) {
    //// Preconditions
    if (owlExportProgressEventListener == null)
      throw new IllegalArgumentException("owlExportProgressEventListener must not be null");
    if (listeners.contains(owlExportProgressEventListener))
      throw new IllegalArgumentException("listener must not be currently registered");
    assert listeners != null : "listeners must not be null";
    
    listeners.add(owlExportProgressEventListener);
    logger.fine("added an OWL export progress listener");
  }
  
  /** Removes an OWL export progress listener.
   *
   * @param owlExportProgressEventListener a listener for OWL export progress events
   */
  public synchronized void removeListener(final OWLExportProgressEventListener owlExportProgressEventListener) {
    //// Preconditions
    if (owlExportProgressEventListener == null)
      throw new IllegalArgumentException("owlExportProgressEventListener must not be null");
    assert listeners != null : "listeners must not be null";
    
    listeners.remove(owlExportProgressEventListener);
    logger.fine("removed an OWL export progress listener");
  }
  
  /** Class that provides an OWL export progress notification event. */
  public class OWLExportProgressEventObject extends EventObject {
    
    /** Constructs a new OWLExportProgressEventObject instance.
     *
     * @param source the object on which the Event initially occurred
     * @param owlExportProgressEventInfo the OWL export progress event information
     */
    public OWLExportProgressEventObject(final Object source, final OWLExportProgressEventInfo owlExportProgressEventInfo) {
      super(source);
      //// Preconditions
      if (owlExportProgressEventInfo == null)
        throw new IllegalArgumentException("owlExportProgressEventInfo must not be null");
      
      this.owlExportProgressEventInfo = owlExportProgressEventInfo;
    }
    
    /** Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
      return "OWLExportProgressEventObject (" + owlExportProgressEventInfo.toString() + ")";
    }
    
    public OWLExportProgressEventInfo getOWLExportProgressEventInfo() {
      return owlExportProgressEventInfo;
    }
    
    final OWLExportProgressEventInfo owlExportProgressEventInfo;
  }
  
  /** This class contains OWL Export progress event information. */
  public class OWLExportProgressEventInfo {
    /** the identifier for this OWL export */
    final UUID uuid;
    
    /** the OWL export percent complete */
    final private double percentComplete;
    
    /** the OWL export output path */
    final private String outputPath;
    
    /** the output from the Vowlidator */
    final private String validationReport;
    
    /** the indicator that the OWL export is done */
    final private boolean isDone;
    
    /** Creates a new OWLExportProgressEventInfo instance.
     *
     * @param uuid the identifier for this OWL export
     * @param percentComplete the OWL export percent complete
     * @param outputPath the OWL export output path
     * @param validationReport the output from the Vowlidator
     * @param isDone the indicator that the OWL export is done
     */
    public OWLExportProgressEventInfo(final UUID uuid,
    final double percentComplete,
    final String outputPath,
    final String validationReport,
    final boolean isDone) {
      //// Preconditions
      if (uuid == null)
        throw new NullPointerException("uuid must be not be null");
      if (percentComplete < 0.0d || percentComplete > 100.0d)
        throw new IllegalArgumentException("percentComplete must be in the range [0 ... 100]");
      if (outputPath == null)
        throw new NullPointerException("outputPath must be not be null");
      if (outputPath.length() == 0)
        throw new IllegalArgumentException("outputPath must be not be an empty string");
      if (isDone && validationReport == null)
        throw new NullPointerException("validationReport must be not be null");
      if (isDone && validationReport.length() == 0)
        throw new NullPointerException("validationReport must be not be an empty string");
      
      this.uuid = uuid;
      this.percentComplete = percentComplete;
      this.outputPath = outputPath;
      this.validationReport = validationReport;
      this.isDone = isDone;
    }
    
    /** Gets the identifier for this OWL export.
     *
     * @return the identifier for this OWL export
     */
    public UUID getUUID() {
      return uuid;
    }
    
    /** Gets the OWL export percent complete.
     *
     * @return the OWL export percent complete
     */
    public double getPercentComplete() {
      return percentComplete;
    }
    
    /** Gets the OWL export output path.
     *
     * @return the OWL export output path
     */
    public String getOutputPath() {
      return outputPath;
    }
    
    /** Gets the output from the Vowlidator.
     *
     * @return the output from the Vowlidator, or null if the export is not done
     */
    public String getValidationReport() {
      return validationReport;
    }
    
    /** Returns the indicator that the OWL export is done.
     *
     * @return the indicator that the OWL export is done
     */
    public boolean isDone() {
      return isDone;
    }
    
    /** Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
      final StringBuffer stringBuffer = new StringBuffer(1000);
      stringBuffer.append("[OWLExportProgressEventInfo %done=");
      stringBuffer.append(percentComplete);
      stringBuffer.append(", outputPath=");
      stringBuffer.append(outputPath);
      stringBuffer.append(", isDone=");
      stringBuffer.append(isDone);
      if (validationReport != null) {
        final int sampleLength = (validationReport.length() > 100) ? 100 : validationReport.length();
        stringBuffer.append(", validationReport=");
        stringBuffer.append(validationReport.substring(0, sampleLength));
        stringBuffer.append("...");
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
  
  /** Defines the interface for OWL export progress event listeners. */
  public interface OWLExportProgressEventListener extends EventListener {
    
    /** Notifies the listener of the given OWL export progress event.
     *
     * @param evt the OWL export progress event
     */
    void notifyOWLExportProgressEvent(OWLExportProgressEventObject evt);
  };
  
  //// Protected Area
  
  /** Reports OWL export progress to each of the listeners.
   *
   * @param percentComplete the OWL export percent complete
   * @param validationReport the output from the Vowlidator
   * @param isDone the indicator that the OWL export is done
   */
  protected void reportProgress(final double percentComplete) {
    //// Preconditions
    assert percentComplete >= 0.0d && percentComplete <= 100.0d : "percentComplete must be in the range [0 ... 100]";
    assert percentComplete >= previouslyReportedPercentComplete : "percentComplete " + percentComplete + " must not be less than the previously reported value " + previouslyReportedPercentComplete;
    
    reportProgress(percentComplete, null, false);
  }
  
  //// Private Area
  
  /** Serializes the OWL document to the XML file. */
  private void serializeDocument() throws IOException {
    //// Preconditions
    assert document != null : "document must not be null";
    assert outputPath != null : "outputPath must not be null";
    assert outputPath.length() > 0 : "outputPath must not be an empty string";
    
    logger.fine("Writing OWL output to " + outputPath);
    final OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
    final BufferedWriter owlOut = new BufferedWriter(new FileWriter(outputPath));
    final XMLSerializer xmlSerializer = new XMLSerializer(owlOut, outputFormat);
    xmlSerializer.asDOMSerializer();
    xmlSerializer.serialize(document);
    owlOut.close();
  }
  
  /** Sets up the OWL export process. */
  private void setup() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert document != null : "document must not be null";
    
    initializeMappingDictionary();
    initializeProblemStores();
    createRdfNode();
    createOwlOntologyNode();
    if (! useResearchCycOntology) {
      createDefinitionalFunctionalPropertyNode("guid", "globally unique identifier", guidComment);
      createDefinitionalFunctionalPropertyNode("externalID", "external identifier", externalIDComment);
      createDefinitionalFunctionalPropertyNode("nonAtomicFormula", "non-atomic formula", nonAtomicFormulaComment);
      createDefinitionalFunctionalPropertyNode("literalValue", "literal value", literalValueComment);
    }
    
    if (exportCommand == OntologyExport.EXPORT_ENTIRE_KB) {
      includeUpwardClosure = false;
      logger.info("Exporting Entire KB subset");
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET || exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
      includeUpwardClosure = false;
      logger.info("Exporting KB subset " + cycKbSubsetCollection.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_PLUS_UPWARD_CLOSURE) {
      cycKbSubsetCollection = cycAccess.getKnownConstantByGuid(cycKbSubsetCollectionGuid);
      cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      includeUpwardClosure = true;
      logger.info("Exporting KB subset " + cycKbSubsetCollection.cyclify() +
      "\n  plus upward closure to #$Thing filtered by " + cycKbSubsetFilter.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_KB_SUBSET_BELOW_TERM) {
      rootTerm = cycAccess.getKnownConstantByGuid(rootTermGuid);
      if (cycKbSubsetFilterGuid != null)
        cycKbSubsetFilter = cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      includeUpwardClosure = false;
      if (cycKbSubsetFilter != null)
        logger.info("Exporting KB collections below root term " + rootTerm.cyclify() +
        "\n  filtered by " + cycKbSubsetFilter.cyclify());
      else
        logger.info("Exporting KB collections below root term " + rootTerm.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_TERMS) {
      cycKbSubsetFilter = cycKbSubsetFilterGuid == null ? null : cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      logger.info("Exporting selected terms " + selectedCycForts.cyclify());
    }
    else if (exportCommand == OntologyExport.EXPORT_SELECTED_ASSERTIONS) {
      cycKbSubsetFilter = cycKbSubsetFilterGuid == null ? null : cycAccess.getKnownConstantByGuid(cycKbSubsetFilterGuid);
      cycKbSubsetCollection = cycKbSubsetFilter;
      includeUpwardClosure = false;
      logger.info("Exporting selected assertions");
    }
    else {
      System.err.println("Invalid export comand " + exportCommand);
      System.exit(1);
    }
  }
  
  /** Cleans up the OWL export process. */
  private void cleanUp() throws UnknownHostException, IOException, CycApiException {
    destroyProblemStores();    
  }

  /** Partitions the selected terms by type */
  private void partitionSelectedTermsByType() throws Exception {
    //// Preconditions
    assert selectedCycForts != null : "selectedCycForts must not be null";
    assert ! selectedCycForts.isEmpty() : "selectedCycForts must not be empty";
    assert cycAccess != null : "cycAccess must not be null";
    
    if (exportCommand != EXPORT_RESEARCH_CYC) {
      // Do not export Cyc (for now) arity 3+ predicates, as they cannot be easily expressed in OWL.
      logger.info("Removing non-binary properties");
      final CycList tempSelectedCycForts = new CycList(selectedCycForts.size());
      for (int i = 0; i < selectedCycForts.size(); i++) {
        final CycObject cycObject = (CycObject) selectedCycForts.get(i);
        if (cycAccess.isPredicate(cycObject, inferenceMt)) {
          if (cycAccess.isBinaryPredicate(cycObject, inferenceMt))
            tempSelectedCycForts.add(cycObject);
          else
            logger.info("  Dropping " + cycObject.toString());
        }
        else
          tempSelectedCycForts.add(cycObject);
      }
      selectedCycForts = tempSelectedCycForts;
    }
    logger.info("Partitioning by type.");
    for (int i = 0; i < selectedCycForts.size(); i++) {
      if (isCancelled)
        return;
      final CycObject cycObject = (CycObject) selectedCycForts.get(i);
      if (cycAccess.isCollection(cycObject, inferenceMt)) {
        owlSelectedConstants.add(cycObject);
        if (exportCommand != EXPORT_RESEARCH_CYC ||
        exportCategory.equals(ALL_EXPORT_CATEGORIES) ||
        exportCategory.equals(EXPORT_COLLECTION_CATEGORY))
          owlSelectedClasses.add(cycObject);
      }
      else if (cycAccess.isUnaryPredicate(cycObject, inferenceMt)) {
        if (exportCommand == EXPORT_RESEARCH_CYC) {
          owlSelectedConstants.add(cycObject);
          if (exportCategory.equals(ALL_EXPORT_CATEGORIES) ||
          exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
            owlSelectedProperties.add(cycObject);
        }
        else
          // Do not export (for now) Cyc unary predicates, as they cannot be easily expressed in OWL.
          continue;
      }
      else if (cycAccess.isBinaryPredicate(cycObject, inferenceMt)) {
        owlSelectedConstants.add(cycObject);
        if (exportCommand != EXPORT_RESEARCH_CYC ||
            exportCategory.equals(ALL_EXPORT_CATEGORIES) ||
            exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
          owlSelectedProperties.add(cycObject);
      }
      else if (cycAccess.isPredicate(cycObject, inferenceMt)) {
        if (exportCommand == EXPORT_RESEARCH_CYC) {
          owlSelectedConstants.add(cycObject);
          if (exportCategory.equals(ALL_EXPORT_CATEGORIES) || exportCategory.equals(EXPORT_PREDICATE_CATEGORY))
            owlSelectedProperties.add(cycObject);
        }
        else
          // Do not export Cyc (for now) arity 3+ predicates, as they cannot be easily expressed in OWL.
          continue;
      }
      else if (cycAccess.isIndividual(cycObject, inferenceMt)) {
        owlSelectedConstants.add(cycObject);
        if (exportCommand != EXPORT_RESEARCH_CYC ||
            exportCategory.equals(ALL_EXPORT_CATEGORIES) ||
            exportCategory.equals(EXPORT_INDIVIDUAL_CATEGORY))
          owlSelectedIndividuals.add(cycObject);
      }
    }
    theSetOfOWLSelectedClasses = new CycList(owlSelectedClasses.size() + 1);
    theSetOfOWLSelectedClasses.add(cycAccess.getKnownConstantByName("TheSet"));
    theSetOfOWLSelectedClasses.addAll(owlSelectedClasses);
    theSetOfOWLSelectedIndividuals = new CycList(owlSelectedIndividuals.size() + 1);
    theSetOfOWLSelectedIndividuals.add(cycAccess.getKnownConstantByName("TheSet"));
    theSetOfOWLSelectedIndividuals.addAll(owlSelectedIndividuals);
    applicableBinaryPredicates = owlSelectedProperties;
  }
  
  /** Creates a OWL node for a single Cyc term.
   *
   * @param cycObject the given term
   */
  private void createTermNode(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    guid = null;
    externalID = null;
    comment = null;
    label = null;
    nonAtomicFormula = null;
    if (cycObject instanceof CycConstant)
      guid = ((CycConstant) cycObject).getGuid();
    else {
      externalID = cycAccess.getExternalIDString(cycObject);
      nonAtomicFormula = cycObject.toString();
    }
    populateComment(cycObject);
    populateIsas(cycObject);
    if (! isExportLimitedToOpenCycContent) {
      if (areRulesExported)
        populateRules(cycObject);
      populatePropertyAssertions(cycObject);
    }
    if (cycAccess.isCollection(cycObject, inferenceMt))
      createClassNode(cycObject);
    else if (cycAccess.isBinaryPredicate(cycObject, inferenceMt))
      createPropertyNode(cycObject);
    else if (cycAccess.isIndividual(cycObject, inferenceMt))
      createIndividualNode(cycObject);
    else
      logger.info("Unhandled term: " + cycObject.toString());
    final Double percentProgressDouble = (Double) percentProgressDictionary.get(cycObject);
    if (percentProgressDouble != null)
      reportProgress(percentProgressDouble.doubleValue(), null, false);
  }
  
  /** Creates a OWL class node for a single Cyc collection.
   *
   * @param collection The Cyc collection from which the OWL class node is created.
   */
  private void createClassNode(final CycObject collection) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert collection != null : "collection must not be null";
    assert rdfNode != null : "rdfNode must not be null";
    assert document != null : "document must not be null";

    if (PROBLEM_STORE_REUSE) {
      allQueriesProblemStoreName = UUID.randomUUID().toString();
      cycAccess.initializeNamedInferenceProblemStore(allQueriesProblemStoreName, null);
    }
    final Element classNode = document.createElementNS(owlNamespace, "owl:Class");
    rdfNode.appendChild(classNode);
    if (collection instanceof CycConstant) {
      createClassNodeCommon(collection, classNode, true);
      if (PROBLEM_STORE_REUSE)
        cycAccess.destroyInferenceProblemStoreByName(allQueriesProblemStoreName);
      return;
    }
    final CycFort symmetricBinaryPredicate = cycAccess.getKnownConstantByName("SymmetricBinaryPredicate");
    final CycFort function = ((CycNart) collection).getFunctor();
    if (function.equals(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationFromFn"))) {
      createClassNodeCommon(collection, classNode, false);
      final CycNart subcollectionOfWithRelationFromFn = (CycNart) collection;
      final CycObject predicate = (CycObject) subcollectionOfWithRelationFromFn.getArguments().get(1);
      if (! cycAccess.isa(predicate, symmetricBinaryPredicate, inferenceMt)) {
        final CycObject collection1 = (CycObject) subcollectionOfWithRelationFromFn.getArguments().get(0);
        final Object thing = subcollectionOfWithRelationFromFn.getArguments().get(2);
        if (selectedCycForts.contains(predicate) && (! (thing instanceof CycObject) || selectedCycForts.contains(thing))) {
          final Element intersectionOfNode = document.createElementNS(owlNamespace, "owl:intersectionOf");
          intersectionOfNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
          final Element subcollectionClassNode = document.createElementNS(owlNamespace, "owl:Class");
          subcollectionClassNode.setAttributeNS(rdfNamespace, "rdf:about", translateTermWithClassMapping(collection1));
          final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
          final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
          final CycFort inversePredicate = new CycNart(cycAccess.getKnownConstantByName("InverseBinaryPredicateFn"), predicate);
          addInversePredicate(inversePredicate);
          onPropertyNode.setAttributeNS(rdfsNamespace, "rdf:resource", "#" + xmlNonAtomicTermName(inversePredicate));
          final Element hasValueNode = document.createElementNS(owlNamespace, "owl:hasValue");
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(thing));
          restrictionNode.appendChild(onPropertyNode);
          restrictionNode.appendChild(hasValueNode);
          intersectionOfNode.appendChild(subcollectionClassNode);
          intersectionOfNode.appendChild(restrictionNode);
          classNode.appendChild(intersectionOfNode);
        }
      }
    }
    else if (function.equals(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationToFn"))) {
      createClassNodeCommon(collection, classNode, false);
      final CycNart subcollectionOfWithRelationToFn = (CycNart) (CycNart) collection;
      final CycObject predicate = (CycObject) subcollectionOfWithRelationToFn.getArguments().get(1);
      if (! cycAccess.isa(predicate, symmetricBinaryPredicate, inferenceMt)) {
        final CycObject collection1 = (CycObject) subcollectionOfWithRelationToFn.getArguments().get(0);
        final Object thing = subcollectionOfWithRelationToFn.getArguments().get(2);
        if (selectedCycForts.contains(predicate) && (! (thing instanceof CycObject) || selectedCycForts.contains(thing))) {
          final Element intersectionOfNode = document.createElementNS(owlNamespace, "owl:intersectionOf");
          intersectionOfNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
          final Element subcollectionClassNode = document.createElementNS(owlNamespace, "owl:Class");
          subcollectionClassNode.setAttributeNS(rdfNamespace, "rdf:about", translateTermWithClassMapping(collection1));
          final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
          final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
          onPropertyNode.setAttributeNS(rdfsNamespace, "rdf:resource", translateTermWithPropertyMapping(predicate));
          final Element hasValueNode = document.createElementNS(owlNamespace, "owl:hasValue");
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(thing));
          restrictionNode.appendChild(onPropertyNode);
          restrictionNode.appendChild(hasValueNode);
          intersectionOfNode.appendChild(subcollectionClassNode);
          intersectionOfNode.appendChild(restrictionNode);
          classNode.appendChild(intersectionOfNode);
        }
      }
    }
    else if (function.equals(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationFromTypeFn"))) {
      createClassNodeCommon(collection, classNode, false);
      final CycNart subcollectionOfWithRelationFromTypeFn = (CycNart) collection;
      final CycObject predicate = (CycObject) subcollectionOfWithRelationFromTypeFn.getArguments().get(1);
      if (! cycAccess.isa(predicate, symmetricBinaryPredicate, inferenceMt)) {
        final CycObject collection1 = (CycObject) subcollectionOfWithRelationFromTypeFn.getArguments().get(0);
        final CycObject collection2 = (CycObject) subcollectionOfWithRelationFromTypeFn.getArguments().get(2);
        if (selectedCycForts.contains(predicate) && selectedCycForts.contains(collection2)) {
          final Element intersectionOfNode = document.createElementNS(owlNamespace, "owl:intersectionOf");
          intersectionOfNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
          final Element subcollectionClassNode = document.createElementNS(owlNamespace, "owl:Class");
          subcollectionClassNode.setAttributeNS(rdfNamespace, "rdf:about", translateTermWithClassMapping(collection1));
          final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
          final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
          final CycFort inversePredicate = new CycNart(cycAccess.getKnownConstantByName("InverseBinaryPredicateFn"), predicate);
          addInversePredicate(inversePredicate);
          onPropertyNode.setAttributeNS(rdfsNamespace, "rdf:resource", "#" + xmlNonAtomicTermName(inversePredicate));
          final Element hasValueNode = document.createElementNS(owlNamespace, "owl:hasValue");
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(collection2));
          restrictionNode.appendChild(onPropertyNode);
          restrictionNode.appendChild(hasValueNode);
          intersectionOfNode.appendChild(subcollectionClassNode);
          intersectionOfNode.appendChild(restrictionNode);
          classNode.appendChild(intersectionOfNode);
        }
      }
    }
    else if (function.equals(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationToTypeFn"))) {
      createClassNodeCommon(collection, classNode, false);
      final CycNart subcollectionOfWithRelationToTypeFn = (CycNart) collection;
      final CycObject predicate = (CycObject) subcollectionOfWithRelationToTypeFn.getArguments().get(1);
      if (! cycAccess.isa(predicate, symmetricBinaryPredicate, inferenceMt)) {
        final CycObject collection1 = (CycObject) subcollectionOfWithRelationToTypeFn.getArguments().get(0);
        final CycObject collection2 = (CycObject) subcollectionOfWithRelationToTypeFn.getArguments().get(2);
        if (selectedCycForts.contains(predicate) && selectedCycForts.contains(collection2)) {
          final Element intersectionOfNode = document.createElementNS(owlNamespace, "owl:intersectionOf");
          intersectionOfNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
          final Element subcollectionClassNode = document.createElementNS(owlNamespace, "owl:Class");
          subcollectionClassNode.setAttributeNS(rdfNamespace, "rdf:about", translateTermWithClassMapping(collection1));
          final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
          final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
          onPropertyNode.setAttributeNS(rdfsNamespace, "rdf:resource", translateTermWithPropertyMapping(predicate));
          final Element hasValueNode = document.createElementNS(owlNamespace, "owl:hasValue");
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(collection2));
          restrictionNode.appendChild(onPropertyNode);
          restrictionNode.appendChild(hasValueNode);
          intersectionOfNode.appendChild(subcollectionClassNode);
          intersectionOfNode.appendChild(restrictionNode);
          classNode.appendChild(intersectionOfNode);
        }
      }
    }
    else
      createClassNodeCommon(collection, classNode, true);
    if (PROBLEM_STORE_REUSE)
      cycAccess.destroyInferenceProblemStoreByName(allQueriesProblemStoreName);
  }
  
  /** Creates common sub-nodes for a OWL class node and a single Cyc collection.
   *
   * @param collection The Cyc collection from which the OWL class node is created.
   * @param classNode the class node
   * @param createTypeAndSubclass the indicator for whether to create the type and subClass nodes
   */
  private void createClassNodeCommon(final CycObject collection, final Element classNode, final boolean createTypeAndSubclass)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert collection != null : "collection must not be null";
    assert classNode != null : "classNode must not be null";
    
    if (createTypeAndSubclass)
      populateGenls(collection);
    if (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      disjointWiths = null;
      coExtensionals = null;
      restrictions = null;
    }
    else {
      restrictions = new ArrayList();
      if (! isExportLimitedToOpenCycContent) {
        populateDisjointWiths(collection);
        populateCoExtensionals(collection);
        populateInterArgIsa1_2s(collection);
        populateInterArgIsa2_1s(collection);
        populateRelationAllOnly(collection);
        populateRelationOnlyAll(collection);
        populateRelationAllExists(collection);
        populateRelationExistAlls(collection);
        populateTypeGenls(collection);
        populateRelationAllInstances(collection);
        populateRelationInstanceAlls(collection);
        populateRelationInstanceExists(collection);
        populateRelationExistsInstance(collection);
        populateRelationAllExistsCounts(collection);
        populateRelationAllExistMaxs(collection);
        populateRelationAllExistMins(collection);
        populateRelationExistsCountAlls(collection);
        populateRelationExistsMinAlls(collection);
        populateRelationExistsMaxAlls(collection);
        //populateRelationExistsAllMany(collection);
        populateArg2Cardinalities(collection);
        populateInterArgCardinalities1_2(collection);
        populateInterArgCardinalities2_1(collection);
        populateInterArgFormats1_2(collection);
        populateInterArgFormats2_1(collection);
        populateCompletelyAssertedCollectionInstances(collection);
      }
    }
    if (collection instanceof CycConstant)
      classNode.setAttributeNS(rdfNamespace, "rdf:ID", xmlName((CycConstant) collection));
    else
      classNode.setAttributeNS(rdfNamespace, "rdf:ID", xmlNonAtomicTermName(collection));
    if (exportCommand == OntologyExport.EXPORT_RESEARCH_CYC) {
      logger.fine("");
    }
    else
      label = cycAccess.getPluralGeneratedPhrase(collection);
    createCommonNodes(classNode);
    if (owlClassMappingDictionary.containsKey(collection)) {
      final String mappedConcept = (String) owlClassMappingDictionary.get(collection);
      if (mappedConcept.startsWith("owl:")) {
        final Element sameClassAsNode = document.createElementNS(owlNamespace, "owl:equivalentClass");
        sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", mappedConcept);
        classNode.appendChild(sameClassAsNode);
      }
    }
    if (createTypeAndSubclass) {
      if (isas != null)
        for (int i = 0; i < isas.size(); i++) {
          final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
          typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(isas.get(i)));
          classNode.appendChild(typeNode);
        }
      if (genls != null)
        for (int i = 0; i < genls.size(); i++) {
          final Element subClassNode = document.createElementNS(rdfsNamespace, "rdfs:subClassOf");
          subClassNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(genls.get(i)));
          classNode.appendChild(subClassNode);
        }
    }
    if (disjointWiths != null)
      for (int i = 0; i < disjointWiths.size(); i++) {
        final Element disjointWithNode = document.createElementNS(owlNamespace, "owl:disjointWith");
        disjointWithNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(disjointWiths.get(i)));
        classNode.appendChild(disjointWithNode);
      }
    if (coExtensionals != null)
      for (int i = 0; i < coExtensionals.size(); i++) {
        final Element sameClassAsNode = document.createElementNS(owlNamespace, "owl:equivalentClass");
        sameClassAsNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(coExtensionals.get(i)));
        classNode.appendChild(sameClassAsNode);
      }
    if (restrictions != null)
      createRestrictions(classNode);
    if (completelyAssertedCollectionInstances != null && ! completelyAssertedCollectionInstances.isEmpty()) {
      final Element oneOfNode = document.createElementNS(owlNamespace, "owl:oneOf");
      oneOfNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
      for (int i = 0; i < completelyAssertedCollectionInstances.size(); i++) {
        final CycObject instance = (CycObject) completelyAssertedCollectionInstances.get(i);
        final Element instanceNode = document.createElement(collection.toString());
        instanceNode.setAttributeNS(rdfNamespace, "rdf:about", translateTerm(instance));
        oneOfNode.appendChild(instanceNode);
      }
      classNode.appendChild(oneOfNode);
    }
    if (! isExportLimitedToOpenCycContent)
      createPropertyAssertionNodes(classNode);
  }
  
  /** Creates a OWL individual node for a single Cyc individual.
   *
   * @param cycObject The Cyc individual from which the OWL individual node is created.
   */
  private void createIndividualNode(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    assert document != null : "document must not be null";
    
    if (isas == null || isas.isEmpty())
      return;
    restrictions = new ArrayList();
    final CycFort isa = bestIsaForIndividual();
    isas.remove(isa);
    String isaString = translateTermWithMapping(isa);
    if (isaString.startsWith("#"))
      isaString = isaString.substring(1);
    Element individualNode;
    try {
      if (isaString.startsWith(owlNamespace)) {
        isaString = "owl:" + isaString.substring(owlNamespace.length());
        individualNode = document.createElementNS(owlNamespace, isaString);
      }
      else
        individualNode = document.createElement(isaString);
    }
    catch (Exception e) {
      logger.warning("Bypassing term " + cycObject.cyclify() + " having bad isa name " + isaString);
      return;
    }
    rdfNode.appendChild(individualNode);
    final String owlNameString = (cycObject instanceof CycConstant) ? xmlName((CycConstant) cycObject) : xmlNonAtomicTermName(cycObject);
    individualNode.setAttributeNS(rdfsNamespace, "rdf:ID", owlNameString);
    final IndividualNodeInfo individualNodeInfo = new IndividualNodeInfo(isaString, owlNameString);
    individualNodeInfoDictionary.put(cycObject, individualNodeInfo);
    if (exportCommand == EXPORT_RESEARCH_CYC)
      logger.fine("");
    else {
      label = cycAccess.getSingularGeneratedPhrase(cycObject);
      if (label.startsWith("Thing ") && cycObject instanceof CycNart)
        label = "some " + cycAccess.getSingularGeneratedPhrase(isa);
      populateEquals(cycObject);
      populateRelationInstanceAlls(cycObject);
      populateRelationInstanceExists(cycObject);
      populateRelationExistsInstance(cycObject);
    }
    createCommonNodes(individualNode);
    for (int i = 0; i < isas.size(); i++) {
      final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
      typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(isas.get(i)));
      individualNode.appendChild(typeNode);
    }
    if (equals != null)
      for (int i = 0; i < equals.size(); i++) {
        final CycObject equalTerm = (CycObject) equals.get(i);
        final Element sameAsNode = document.createElementNS(owlNamespace, "owl:sameAs");
        sameAsNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTerm(equalTerm));
        individualNode.appendChild(sameAsNode);
      }
    if (createdIndividualValues.containsKey(cycObject)) {
      final Object thing = createdIndividualValues.get(cycObject);
      assert thing != null : "thing must not be null for " + cycObject.toString();
      createLiteralValueNode(individualNode, thing);
    }
    if (restrictions != null)
      createRestrictions(individualNode);
    if (! isExportLimitedToOpenCycContent)
      createPropertyAssertionNodes(individualNode);
  }
  
  /** Creates the subnodes common to class, property and individual nodes.
   *
   * @param parentNode the parent node
   */
  private void createCommonNodes(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    createLabelNode(parentNode);
    createCommentNode(parentNode);
    createGuidNode(parentNode);
    createExternalIDNode(parentNode);
    createNonAtomicFormulaNode(parentNode);
  }
  
  /** Creates the restriction nodes for the parent class or individual node.
   *
   * @param parentNode the parent class or individual node
   */
  private void createRestrictions(final Element parentNode) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    assert restrictions != null : "restrictions must not be null";
    
    Collections.sort(restrictions);
    for (int i = 0; i < restrictions.size(); i++) {
      final Restriction restriction = (Restriction) restrictions.get(i);
      Element typeOrSubClassNode;
      if (restriction.restrictionType == SUBCLASS_RESTRICTION)
        typeOrSubClassNode = document.createElementNS(rdfsNamespace, "rdfs:subClassOf");
      else
        typeOrSubClassNode = document.createElementNS(rdfNamespace, "rdf:type");
      parentNode.appendChild(typeOrSubClassNode);
      final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
      typeOrSubClassNode.appendChild(restrictionNode);
      final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
      if (restriction.isInverseProperty) {
        final CycFort inversePredicate =
          new CycNart(cycAccess.getKnownConstantByName("InverseBinaryPredicateFn"), restriction.property);
        addInversePredicate(inversePredicate);
        onPropertyNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithPropertyMapping(inversePredicate));
      }
      else
        onPropertyNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithPropertyMapping(restriction.property));
      restrictionNode.appendChild(onPropertyNode);
      boolean valueNodeCreated = false;
      if (restriction.hasValue != null) {
        final Element hasValueNode = document.createElementNS(owlNamespace, "owl:hasValue");
        if (restriction.hasValue instanceof CycObject)
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(restriction.hasValue));
        else {
          hasValueNode.setAttributeNS(rdfNamespace, "rdf:datatype", xmlSchemaDatatype(restriction.hasValue));
          hasValueNode.appendChild(document.createTextNode(restriction.hasValue.toString()));
        }
        restrictionNode.appendChild(hasValueNode);
        valueNodeCreated = true;
      }
      else if (restriction.valuesFrom != null) {
        final Element valuesFromNode = document.createElementNS(owlNamespace, "owl:valuesFrom");
        valuesFromNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(restriction.valuesFrom));
        restrictionNode.appendChild(valuesFromNode);
        valueNodeCreated = true;
      }
      else if (restriction.allValuesFrom != null) {
        final Element allValuesFromNode = document.createElementNS(owlNamespace, "owl:allValuesFrom");
        allValuesFromNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(restriction.allValuesFrom));
        restrictionNode.appendChild(allValuesFromNode);
        valueNodeCreated = true;
      }
      else if (restriction.someValuesFrom != null) {
        final Element someValuesFromNode = document.createElementNS(owlNamespace, "owl:someValuesFrom");
        someValuesFromNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(restriction.someValuesFrom));
        restrictionNode.appendChild(someValuesFromNode);
        valueNodeCreated = true;
      }
      if (! valueNodeCreated || exportQualifiedCardinalityRestrictions) {
        if (restriction.cardinality > -1) {
          final Element cardinalityNode = document.createElementNS(owlNamespace, "owl:cardinality");
          cardinalityNode.setAttributeNS(rdfNamespace, "rdf:datatype", xsdInt);
          cardinalityNode.appendChild(document.createTextNode(Integer.toString(restriction.cardinality)));
          restrictionNode.appendChild(cardinalityNode);
        }
        else if (restriction.maxCardinality > -1) {
          final Element cardinalityNode = document.createElementNS(owlNamespace, "owl:maxCardinality");
          cardinalityNode.setAttributeNS(rdfNamespace, "rdf:datatype", xsdInt);
          cardinalityNode.appendChild(document.createTextNode(Integer.toString(restriction.maxCardinality)));
          restrictionNode.appendChild(cardinalityNode);
        }
        else if (restriction.minCardinality > -1) {
          final Element cardinalityNode = document.createElementNS(owlNamespace, "owl:minCardinality");
          cardinalityNode.setAttributeNS(rdfNamespace, "rdf:datatype", xsdInt);
          cardinalityNode.appendChild(document.createTextNode(Integer.toString(restriction.minCardinality)));
          restrictionNode.appendChild(cardinalityNode);
        }
      }
    }
  }
  
  /** Reports OWL export progress to each of the listeners.
   *
   * @param percentComplete the OWL export percent complete
   * @param validationReport the output from the Vowlidator
   * @param isDone the indicator that the OWL export is done
   */
  private void reportProgress(final double percentComplete, final String validationReport, final boolean isDone) {
    //// Preconditions
    assert percentComplete >= 0.0d && percentComplete <= 100.0d : "percentComplete must be in the range [0 ... 100]";
    assert percentComplete >= previouslyReportedPercentComplete : "percentComplete " + percentComplete + " must not be less than the previously reported value " + previouslyReportedPercentComplete;
    assert ! isDone || validationReport != null : "validationReport must be not be null";
    assert ! isDone || validationReport.length() > 0 : "validationReport must be not be an empty string";
    assert listeners != null : "listeners must not be null";
    
    if (listeners.isEmpty())
      return;
    final OWLExportProgressEventInfo owlExportProgressEventInfo =
    new OWLExportProgressEventInfo(uuid, percentComplete, outputPath, validationReport, isDone);
    final OWLExportProgressEventObject owlExportProgressEventObject = new OWLExportProgressEventObject(this, owlExportProgressEventInfo);
    for (int i = 0; i < listeners.size(); i++) {
      final OWLExportProgressEventListener owlExportProgressEventListener = (OWLExportProgressEventListener) listeners.get(i);
      logger.finest("notifying listener");
      owlExportProgressEventListener.notifyOWLExportProgressEvent(owlExportProgressEventObject);
    }
    previouslyReportedPercentComplete = percentComplete;
  }
  
  /**  Creates an RDF node. */
  private void createRdfNode() {
    //// Preconditions
    assert document != null : "document must not be null";
    
    rdfNode = document.createElementNS(rdfNamespace, "rdf:RDF");
    rdfNode.setAttribute("xmlns:xsd", xsdNamespace);
    rdfNode.setAttribute("xmlns:rdf", rdfNamespace);
    rdfNode.setAttribute("xmlns:rdfs", rdfsNamespace);
    rdfNode.setAttribute("xmlns:owl", owlNamespace);
    rdfNode.setAttribute("xmlns", cycOwlNamespace);
    rdfNode.setAttribute("xml:base", cycOwlXMLBase);
    document.appendChild(rdfNode);
  }
  
  /** Creates a OWL Ontology node. */
  private void createOwlOntologyNode() {
    //// Preconditions
    assert document != null : "document must not be null";
    
    owlOntologyNode = document.createElementNS(owlNamespace, "owl:Ontology");
    owlOntologyNode.setAttribute("rdf:about", "");
    rdfNode.appendChild(owlOntologyNode);
    owlVersionInfo = document.createElementNS(owlNamespace, "owl:versionInfo");
    owlVersionInfo.appendChild(document.createTextNode("$Id$"));
    owlOntologyNode.appendChild(owlVersionInfo);
    rdfsCommentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
    final String titleText = (exportCommand == EXPORT_RESEARCH_CYC) ?
      title + "\n\n" +
      "OpenCyc License Information\n" +
      "The contents of this file constitute portions of The OpenCyc Knowledge\n" +
      "Base. The OpenCyc Knowledge Base is protected under the following license\n" +
      "and copyrights. This license and copyright information must be included\n" +
      "with any copies or derivative works.\n" +
      "\n" +
      "Copyright Information\n" +
      "OpenCyc Knowledge Base Copyright (c) 2001 - 2006 Cycorp, Inc., Austin, TX, USA.\n" +
      "All rights reserved.\n" +
      "OpenCyc Knowledge Server Copyright (c) 2001 - 2006 Cycorp, Inc., Austin, TX, USA.\n" +
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
    rdfsCommentNode.appendChild(document.createTextNode(titleText));
    owlOntologyNode.appendChild(rdfsCommentNode);
    if (licenseText == null)
      return;
    final Element rdfsLicenseNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
    rdfsLicenseNode.appendChild(document.createTextNode(licenseText));
    owlOntologyNode.appendChild(rdfsLicenseNode);
  }
  
  /** Returns the XML Schema datatype for the given thing.
   *
   * @param thing the value
   */
  protected String xmlSchemaDatatype(final Object thing) {
    //// Preconditions
    assert thing != null : "thing must not be null";
    
    String xsdType = null;
    if (thing instanceof Long || thing instanceof Integer) {
      if (((Number) thing).longValue() >= 0)
        xsdType = xsdNonNegativeInteger;
      else
        xsdType = xsdInteger;
    }
    else if (thing instanceof Float || thing instanceof Double) {
      xsdType = xsdDouble;
    }
    else if (thing instanceof String) {
      xsdType = xsdString;
    }
    else
      throw new RuntimeException("Unhandled XML Schema value " + thing.toString());
    
    //// Postconditions
    assert xsdType != null : "xsdType must not be null";
    assert xsdType.length() > 0 : "xsdType must not be an empty string";
    
    return xsdType;
  }
  
  /** Creates a property assertions node for the given Element.
   *
   * @param element The given element.
   */
  private void createPropertyAssertionNodes(final Element node)
    throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert node != null : "node must not be null";
    assert document != null : "document must not be null";
    assert propertyAssertions != null : "propertyAssertions must not be null";
    
    Collections.sort(propertyAssertions, new CycObjectComparator());
    for (int i = 0; i < propertyAssertions.size(); i++) {
      final CycList propertyAssertion = (CycList) propertyAssertions.get(i);
      final CycObject property = (CycObject) propertyAssertion.first();
      final Object value = propertyAssertion.third();
      final Element propertyAssertionNode =
        document.createElement((property instanceof CycList) ? xmlNonAtomicTermName(property) : property.toString());
      if (value instanceof String || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double)
        propertyAssertionNode.appendChild(document.createTextNode(value.toString()));
      else if (value instanceof CycList &&
               cycAccess.isa((CycList) value,
               cycAccess.getKnownConstantByName("Date"),
               inferenceMt))
        propertyAssertionNode.appendChild(document.createTextNode(cycAccess.xmlDatetimeString((CycList) value)));
      else
        propertyAssertionNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(value));
      node.appendChild(propertyAssertionNode);
    }
  }

  /** Creates the label node for the given parent.
   *
   * @param parentNode the parent node
   */
  private void createLabelNode(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    if (label != null && label.length() > 0) {
      final Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      logger.fine("  " + label);
      labelNode.appendChild(document.createTextNode(label));
      parentNode.appendChild(labelNode);
    }
  }
  
  /** Creates the comment node for the given parent.
   *
   * @param parentNode the parent node
   */
  private void createCommentNode(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    if (comment != null && ! comment.equals("")) {
      final Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
      commentNode.appendChild(document.createTextNode(comment));
      parentNode.appendChild(commentNode);
    }
  }
  
  /** Creates the guid node for the given parent.
   *
   * @param parentNode the parent node
   */
  private void createGuidNode(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    if (guid != null) {
      final Element guidNode = document.createElement("guid");
      guidNode.appendChild(document.createTextNode(guid.toString()));
      parentNode.appendChild(guidNode);
    }
  }
  
  /** Creates the external ID node for the given parent.
   *
   * @param parentNode the parent node
   */
  private void createExternalIDNode(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    if (externalID != null) {
      final Element externalIDNode = document.createElement("externalID");
      externalIDNode.appendChild(document.createTextNode(externalID));
      parentNode.appendChild(externalIDNode);
    }
  }
  
  /** Creates the nonAtomicFormula node if the given parent is a not-atomic term.
   *
   * @param parentNode the parent node
   */
  private void createNonAtomicFormulaNode(final Element parentNode) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    if (nonAtomicFormula != null) {
      final Element nonAtomicFormulaNode = document.createElement("nonAtomicFormula");
      nonAtomicFormulaNode.appendChild(document.createTextNode(nonAtomicFormula));
      parentNode.appendChild(nonAtomicFormulaNode);
    }
  }
  
  /** Creates the literalVaule node of the given parent created-individual node.
   *
   * @param parentNode the parent node
   * @param thing the literal value
   */
  private void createLiteralValueNode(final Element parentNode, final Object thing) {
    //// Preconditions
    assert parentNode != null : "parentNode must not be null";
    
    final Element literalValueNode = document.createElement("literalValue");
    literalValueNode.appendChild(document.createTextNode(thing.toString()));
    parentNode.appendChild(literalValueNode);
  }
  
  /** Returns the best isa for the current Individual term.
   *
   * @return The best isa for the current Individual term.
   */
  private CycConstant bestIsaForIndividual() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert isas != null : "isas must not be null";
    assert ! isas.isEmpty() : "isas must not be empty";
    
    CycFort bestIsa = (CycFort) isas.get(0);
    if (isas.size() == 1 && bestIsa instanceof CycConstant)
      return (CycConstant) bestIsa;
    final CycList candidateIsas = new CycList();
    for (int i = 0; i < isas.size(); i++) {
      CycFort isa = (CycFort) isas.get(i);
      if (! allKbSubsetCollections.contains(isa) && isa instanceof CycConstant)
        candidateIsas.add(isa);
    }
    if (candidateIsas.size() == 0)
      return (CycConstant) bestIsa;
    else if (candidateIsas.size() == 1)
      return (CycConstant) candidateIsas.get(0);
    else {
      bestIsa = (CycConstant) cycAccess.getMinCol(candidateIsas, inferenceMt);
      logger.finer("    candidateIsas: " + candidateIsas +
                   " best-isa: " + bestIsa);
      return (CycConstant) bestIsa;
    }
  }
  
  /** Creates a OWL AllDifferent node for the exported Cyc individuals. */
  private void createAllDifferentNode() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert owlSelectedIndividuals != null : "owlSelectedIndividuals must not be null";
    assert ! owlSelectedIndividuals.isEmpty() : "owlSelectedIndividuals must not be empty";
    assert individualNodeInfoDictionary.size() == owlSelectedIndividuals.size() :
      "individualNodeInfoDictionary size (" + individualNodeInfoDictionary.size() +
      ") not equal to owlSelectedIndividuals size (" + owlSelectedIndividuals.size() + ")";
    assert rdfNode != null : "rdfNode must not be null";
      
    if ((owlSelectedIndividuals.size() - nonDifferentIndividuals.size()) < 2)
      // do not create an allDifferent node with one child
      return;
    logger.fine("Creating AllDifferent");
    final Element allDifferentNode = document.createElementNS(owlNamespace, "owl:AllDifferent");
    final Element distinctMembersNode = document.createElementNS(owlNamespace, "owl:distinctMembers");
    distinctMembersNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      final CycObject individual = (CycObject) owlSelectedIndividuals.get(i);
      if (nonDifferentIndividuals.contains(individual))
        continue;
      final IndividualNodeInfo individualNodeInfo = (IndividualNodeInfo) individualNodeInfoDictionary.get(individual);
      assert individualNodeInfo != null : "individualNodeInfo must not be null for " + individual.toString();
      logger.info("  " + individualNodeInfo.isaString + " " + individualNodeInfo.owlNameString);
      Element individualNode;
      if (individualNodeInfo.isaString.startsWith("owl:"))
        individualNode = document.createElementNS(owlNamespace, individualNodeInfo.isaString);
      else
        individualNode = document.createElement(individualNodeInfo.isaString);
      individualNode.setAttributeNS(rdfNamespace, "rdf:about", "#" + individualNodeInfo.owlNameString);
      distinctMembersNode.appendChild(individualNode);
    }
    allDifferentNode.appendChild(distinctMembersNode);
    rdfNode.appendChild(allDifferentNode);
  }
  
  /** Creates all rule nodes. */
  private void createRuleNodes()
    throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert document != null : "document must not be null";
    
    // first pass over the rules gathers all the variables
    final CycList variables = new CycList();
    Iterator rules_iter = rules.iterator();
    while (rules_iter.hasNext()) {
      final CycList rule = (CycList) rules_iter.next();
      final CycList antecedant = (CycList) rule.second();
      final CycList antecedantLiterals;
      if (CycAccess.and.equals(antecedant.first()))
        antecedantLiterals = (CycList) antecedant.rest();
      else {
        antecedantLiterals = new CycList(1);
        antecedantLiterals.add(antecedant);
      }
      final int antecedantLiterals_size = antecedantLiterals.size();
      for (int i = 0; i < antecedantLiterals_size; i++) {
        final CycList antecedantLiteral = (CycList) antecedantLiterals.get(i);
        gatherVariablesFromLiteral(antecedantLiteral, variables);
      }
      final CycList consequentLiteral = (CycList) rule.third();
      gatherVariablesFromLiteral(consequentLiteral, variables);
    }
    logger.fine("Creating variables");
    Collections.sort(variables);
    final int variables_size = variables.size();
    for (int i = 0; i < variables_size; i++) {
      final Element variableNode = document.createElementNS(swrlNamespace, "swrl:Variable");
      final CycVariable variable = (CycVariable) variables.get(i);
      final String variableName = variableName(variable);
      variableNode.setAttributeNS(rdfNamespace, "rdf:ID", variableName);
      rdfNode.appendChild(variableNode);
    }
    // second pass over the rules creates the rule nodes
    logger.fine("Creating rules");
    rules_iter = rules.iterator();
    while (rules_iter.hasNext()) {
      final Element impNode = document.createElementNS(rulemlNamespace, "ruleml:Imp");
      final CycList rule = (CycList) rules_iter.next();
      final CycList antecedant = (CycList) rule.second();
      final CycList consequentLiteral = (CycList) rule.third();
      final CycList antecedantLiterals;
      if (CycAccess.and.equals(antecedant.first()))
        antecedantLiterals = (CycList) antecedant.rest();
      else {
        antecedantLiterals = new CycList(1);
        antecedantLiterals.add(antecedant);
      }
      final Element bodyNode = document.createElementNS(rulemlNamespace, "ruleml:body");
      bodyNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
      final int antecedantLiterals_size = antecedantLiterals.size();
      for (int i = 0; i < antecedantLiterals_size; i++) {
        final CycList antecedantLiteral = (CycList) antecedantLiterals.get(i);
        createAtomNode(bodyNode, antecedantLiteral);
      }
      impNode.appendChild(bodyNode);
      final Element headNode = document.createElementNS(rulemlNamespace, "ruleml:head");
      headNode.setAttributeNS(rdfNamespace, "rdf:parseType", "Collection");
      createAtomNode(headNode, consequentLiteral);
      impNode.appendChild(headNode);
      rdfNode.appendChild(impNode);
    }
  }
  
  /** Gathers new variables from the the given literal into the given list of variables. 
   *
   * @param literal the given literal
   * @param variables the given list of variables
   */
  private void gatherVariablesFromLiteral(final CycList literal, final CycList variables) {
    //// Preconditions
    assert literal != null : "literal must not be null";
    assert variables != null : "variables must not be null";
    
    final int literal_size = literal.size();
    if (literal_size > 1) {
      final Object arg1 = literal.second();
      if (arg1 instanceof CycVariable)
        variables.addNew(arg1);
    }
    if (literal_size > 2) {
      final Object arg2 = literal.third();
      if (arg2 instanceof CycVariable)
        variables.addNew(arg2);
    }
  }
 
  /** Creates a rule atom node for the given literal.
   *
   * @param literal the given rule literal
   */
  private void createAtomNode(final Element parent, final CycList literal)
    throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert document != null : "document must not be null";
    assert parent != null : "parent must not be null";
    assert literal != null : "literal must not be null";
    
    final CycFort predicate = (CycFort) literal.first();
    if (predicate.equals(CycAccess.isa)) {
      final Element classAtomNode = document.createElementNS(swrlNamespace, "swrl:ClassAtom");
      final CycVariable variable = (CycVariable) literal.second();
      final CycFort collection = (CycFort) literal.third();
      final Element classPredicateNode = document.createElementNS(swrlNamespace, "swrl:ClassPredicate");
      classPredicateNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(collection));
      classAtomNode.appendChild(classPredicateNode);
      final Element argument1Node = document.createElementNS(swrlNamespace, "swrl:argument1");
      argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName(variable));
      classAtomNode.appendChild(argument1Node);
      parent.appendChild(classAtomNode);
    }
    else if (predicate.toString().equals("relationInstanceExists")) {
      final Element classAtomNode = document.createElementNS(swrlNamespace, "swrl:ClassAtom");
      final Element classPredicateNode = document.createElementNS(swrlNamespace, "swrl:ClassPredicate");
      final Element restrictionNode = document.createElementNS(owlNamespace, "owl:Restriction");
      classPredicateNode.appendChild(restrictionNode);
      final Element onPropertyNode = document.createElementNS(owlNamespace, "owl:onProperty");
      final CycFort binaryPredicate = (CycFort) literal.second();
      onPropertyNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(binaryPredicate));
      restrictionNode.appendChild(onPropertyNode);    
      final Element someValuesFromNode = document.createElementNS(owlNamespace, "owl:someValuesFrom");
      final CycFort collection = (CycFort) literal.fourth();
      someValuesFromNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(collection));
      restrictionNode.appendChild(someValuesFromNode);
      classAtomNode.appendChild(classPredicateNode);
      final Element argument1Node = document.createElementNS(swrlNamespace, "swrl:argument1");
      final CycVariable variable = (CycVariable) literal.third();
      argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName(variable));
      classAtomNode.appendChild(argument1Node);
      parent.appendChild(classAtomNode);
    }
    else if (isObjectProperty(predicate)) {
      final Element individualPropertyAtomNode = document.createElementNS(swrlNamespace, "swrl:individualPropertyAtom");
      final Element propertyPredicateNode = document.createElementNS(swrlNamespace, "swrl:propertyPredicate");
      propertyPredicateNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(predicate));
      individualPropertyAtomNode.appendChild(propertyPredicateNode);
      final Object arg1 = literal.second();
      final Element argument1Node = document.createElementNS(swrlNamespace, "swrl:argument1");
      if (arg1 instanceof CycVariable)
        argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName((CycVariable) arg1));
      else
        argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg1));
      individualPropertyAtomNode.appendChild(argument1Node);
      final Object arg2 = literal.third();
      final Element argument2Node = document.createElementNS(swrlNamespace, "swrl:argument2");
      if (arg2 instanceof CycVariable)
        argument2Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName((CycVariable) arg2));
      else
        argument2Node.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg2));
      individualPropertyAtomNode.appendChild(argument2Node);
      parent.appendChild(individualPropertyAtomNode);
    }
    else {
      final Element datavaluedPropertyAtomNode = document.createElementNS(swrlNamespace, "swrl:datavaluedPropertyAtom");
      final Element propertyPredicateNode = document.createElementNS(swrlNamespace, "swrl:propertyPredicate");
      propertyPredicateNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(predicate));
      datavaluedPropertyAtomNode.appendChild(propertyPredicateNode);
      final Object arg1 = literal.second();
      final Element argument1Node = document.createElementNS(swrlNamespace, "swrl:argument1");
      if (arg1 instanceof CycVariable)
        argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName((CycVariable) arg1));
      else
        argument1Node.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg1));
      datavaluedPropertyAtomNode.appendChild(argument1Node);
      final Object arg2 = literal.third();
      final Element argument2Node = document.createElementNS(swrlNamespace, "swrl:argument2");
      if (arg2 instanceof CycVariable)
        argument2Node.setAttributeNS(rdfNamespace, "rdf:resource", "#" + variableName((CycVariable) arg2));
      else if (arg2 instanceof String || 
               arg2 instanceof Integer || 
               arg2 instanceof Long || 
               arg2 instanceof Float || 
               arg2 instanceof Double)
        argument2Node.appendChild(document.createTextNode(arg2.toString()));
      else if (arg2 instanceof CycList &&
               cycAccess.isa((CycList) arg2, cycAccess.getKnownConstantByName("Date"), inferenceMt))
        argument2Node.appendChild(document.createTextNode(cycAccess.xmlDatetimeString((CycList) arg2)));
      else
        argument2Node.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg2));
      datavaluedPropertyAtomNode.appendChild(argument2Node);
      parent.appendChild(datavaluedPropertyAtomNode);
    }
  }
  
  /** Returns the OWL name for the given CycL variable.
   *
   * @param cycVariable the given CycL variable
   */
  private String variableName (final CycVariable variable) {
    return variable.toString().substring(1) + "_VAR";
  }
  
  
  /** Creates the OWL node that defines a definitional functional property having an rdfsLiteral as its range.
   *
   * @param name the definitional property name
   * @param label the definitional property label
   * @param comment the definitional property comment
   */
  private void createDefinitionalFunctionalPropertyNode(final String name, final String label, final String comment) {
    //// Preconditions
    assert document != null : "document must not be null";
    
    final Element propertyNode = document.createElementNS(owlNamespace, "owl:DatatypeProperty");
    rdfNode.appendChild(propertyNode);
    propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", name);
    if (exportCommand != EXPORT_RESEARCH_CYC) {
      Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      labelNode.appendChild(document.createTextNode(label));
      propertyNode.appendChild(labelNode);
    }
    final Element commentNode = document.createElementNS(rdfsNamespace, "rdfs:comment");
    commentNode.appendChild(document.createTextNode(comment));
    propertyNode.appendChild(commentNode);
    final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
    typeNode.setAttributeNS(rdfNamespace, "rdf:resource", owlFunctionalProperty);
    propertyNode.appendChild(typeNode);
    final Element rangeNode = document.createElementNS(rdfsNamespace, "rdfs:range");
    rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", rdfsLiteral);
    propertyNode.appendChild(rangeNode);
  }
  
  /** Creates a OWL property node for a single Cyc binary predicate.
   *
   * @param cycObject The Cyc binary predicate from which the OWL property node is created.
   */
  private void createPropertyNode(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    assert cycObject instanceof CycFort : "cycObject must be a CycFort";
    assert document != null : "document must not be null";
    
    final Element propertyNode;
    String datatypePropertyDatatype = null;
    String inverseDatatypePropertyDatatype = null;
    boolean isInverseProperty = false;
    if (isObjectProperty(cycObject))
      propertyNode = document.createElementNS(owlNamespace, "owl:ObjectProperty");
    else {
      datatypePropertyDatatype = getDatatypePropertyDataype(cycObject);
      if (datatypePropertyDatatype != null)
        propertyNode = document.createElementNS(owlNamespace, "owl:DatatypeProperty");
      else {
        inverseDatatypePropertyDatatype = getInverseDatatypePropertyDatatype(cycObject);
        if (inverseDatatypePropertyDatatype != null) {
          isInverseProperty = true;
          propertyNode = document.createElementNS(owlNamespace, "owl:DatatypeProperty");
        }
        else {
          logger.info("the Cyc predicate " + cycObject.cyclify() + " undecided OWL property; assuming an ObjectProperty.");
          propertyNode = document.createElementNS(owlNamespace, "owl:ObjectProperty");
        }
      }
    }
    rdfNode.appendChild(propertyNode);
    if (isFunctionalProperty(cycObject)) {
      final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
      typeNode.setAttributeNS(rdfNamespace, "rdf:resource", owlFunctionalProperty);
      propertyNode.appendChild(typeNode);
    }
    if (isInverseFunctionalProperty(cycObject)) {
      final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
      typeNode.setAttributeNS(rdfNamespace, "rdf:resource", owlInverseFunctionalProperty);
      propertyNode.appendChild(typeNode);
    }
    if (isInverseProperty) {
      createInversePropertyNode(cycObject, propertyNode);
      return;
    }
    
    if (cycObject instanceof CycConstant)
      propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlName((CycConstant) cycObject));
    else
      propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlNonAtomicTermName(cycObject));
    if (exportCommand == EXPORT_RESEARCH_CYC) {
      logger.fine("");
    }
    else
      label = cycAccess.getGeneratedPhrase(cycObject);
    createCommonNodes(propertyNode);
    if (isas != null) {
      for (int i = 0; i < isas.size(); i++) {
        final CycFort isa = (CycFort) isas.get(i);
        if ((! isa.toString().equals("BinaryPredicate")) && 
            (! isa.toString().equals("Thing"))) {
          final Element typeNode = document.createElementNS(rdfNamespace, "rdf:type");
          typeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(isa));
          propertyNode.appendChild(typeNode);
        }
      }
    }
    if (cycObject instanceof CycFort)
      populateGenlPreds((CycFort) cycObject);
    else
      genlPreds = new CycList();
    if (exportCommand == this.EXPORT_RESEARCH_CYC) {
      arg1Isas = null;
      arg2Isas = null;
    }
    else {
      populateArg1Isas(cycObject);
      populateArg2Isas(cycObject);
    }
    populateArg1Format(cycObject);
    //TODO later remove argNFormat because we query for strictlyFunctionalInArgs.
    populateArg2Format(cycObject);
    if (genlPreds != null)
      for (int i = 0; i < genlPreds.size(); i++) {
        final Element subPropertyOfNode = document.createElementNS(rdfsNamespace, "rdfs:subPropertyOf");
        subPropertyOfNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithPropertyMapping(genlPreds.get(i)));
        propertyNode.appendChild(subPropertyOfNode);
      }
    for (int i = 0; i < arg1Isas.size(); i++) {
      final Element domainNode = document.createElementNS(rdfsNamespace, "rdfs:domain");
      domainNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(arg1Isas.get(i)));
      propertyNode.appendChild(domainNode);
    }
    if (datatypePropertyDatatype != null) {
      final Element rangeNode = document.createElementNS(rdfsNamespace, "rdfs:range");
      rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", datatypePropertyDatatype);
      propertyNode.appendChild(rangeNode);
    }
    else if (inverseDatatypePropertyDatatype != null) {
      final Element rangeNode = document.createElementNS(rdfsNamespace, "rdfs:range");
      rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", inverseDatatypePropertyDatatype);
      propertyNode.appendChild(rangeNode);
    }
    else {
      for (int i = 0; i < arg2Isas.size(); i++) {
        final Element rangeNode = document.createElementNS(rdfsNamespace, "rdfs:range");
        rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg2Isas.get(i)));
        propertyNode.appendChild(rangeNode);
      }
    }
    if (! isExportLimitedToOpenCycContent)
      createPropertyAssertionNodes(propertyNode);
  }
  
  /** Creates a OWL property node for a single Cyc binary predicate's inverse.
   *
   * @param predicate The Cyc binary predicate from whose inverse the OWL property node is created.
   * @param propertyNode property node to be completed
   */
  private void createInversePropertyNode(final CycObject cycObject, final Element propertyNode)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    assert propertyNode != null : "propertyNode must not be null";
    
    final CycFort inversePredicate = new CycNart(cycAccess.getKnownConstantByName("InverseBinaryPredicateFn"), cycObject);
    propertyNode.setAttributeNS(rdfsNamespace, "rdf:ID", xmlNonAtomicTermName(cycObject));
    if (exportCommand == EXPORT_RESEARCH_CYC) {
      logger.fine("");
    }
    else {
      final Element labelNode = document.createElementNS(rdfsNamespace, "rdfs:label");
      labelNode.setAttributeNS(xmlNamespace, "xml:lang", "en");
      final String label = cycAccess.getGeneratedPhrase(cycObject);
      logger.fine("  " + label);
      labelNode.appendChild(document.createTextNode(label));
      propertyNode.appendChild(labelNode);
    }
    if (exportCommand == this.EXPORT_RESEARCH_CYC) {
      arg1Isas = new CycList(0);
      arg2Isas = new CycList(0);
    }
    else {
      populateArg1Isas(cycObject);
      populateArg2Isas(cycObject);
    }
    for (int i = 0; i < arg1Isas.size(); i++) {
      final Element domainNode = document.createElementNS(rdfsNamespace, "rdfs:domain");
      domainNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithClassMapping(arg1Isas.get(i)));
      propertyNode.appendChild(domainNode);
    }
    for (int i = 0; i < arg2Isas.size(); i++) {
      final Element rangeNode = document.createElementNS(rdfsNamespace, "rdfs:range");
      rangeNode.setAttributeNS(rdfNamespace, "rdf:resource", translateTermWithMapping(arg2Isas.get(i)));
      propertyNode.appendChild(rangeNode);
    }
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTermWithMapping(final Object obj) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert obj != null : "obj must not be null";
    
    String translatedTerm = (String) owlClassMappingDictionary.get(obj);
    if (translatedTerm == null) {
      translatedTerm = (String) owlPropertyMappingDictionary.get(obj);
    }
    if (translatedTerm == null) {
      translatedTerm = (String) xsdSchemaMappingDictionary.get(obj);
    }
    if (translatedTerm == null) {
      translatedTerm = translateTerm(obj);
    }
    //// Postconditions
    assert translatedTerm != null : "translatedTerm must not be null";
    assert translatedTerm.length() > 0 : "translatedTerm must not be an empty string";
    
    return translatedTerm;
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property, with mapping to native OWL class objects.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTermWithClassMapping(final Object obj) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert obj != null : "obj must not be null";
    
    String translatedTerm = (String) owlClassMappingDictionary.get(obj);
    if (translatedTerm == null) {
      translatedTerm = translateTerm(obj);
    }
    //// Postconditions
    assert translatedTerm != null : "translatedTerm must not be null";
    assert translatedTerm.length() > 0 : "translatedTerm must not be an empty string";
    
    return translatedTerm;
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property, with mapping to naive XML schema datatypes.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTermWithPropertyMapping(final Object obj) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert obj != null : "obj must not be null";
    
    String translatedTerm = (String) owlPropertyMappingDictionary.get(obj);
    if (translatedTerm == null) {
      translatedTerm = translateTerm(obj);
    }
    //// Postconditions
    assert translatedTerm != null : "translatedTerm must not be null";
    assert translatedTerm.length() > 0 : "translatedTerm must not be an empty string";
    
    return translatedTerm;
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property, with mapping to naive XML schema datatypes.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTermWithXSDSchemaMapping(final Object obj) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert obj != null : "obj must not be null";
    
    String translatedTerm = (String) xsdSchemaMappingDictionary.get(obj);
    if (translatedTerm == null) {
      translatedTerm = translateTerm(obj);
    }
    //// Postconditions
    assert translatedTerm != null : "translatedTerm must not be null";
    assert translatedTerm.length() > 0 : "translatedTerm must not be an empty string";
    
    return translatedTerm;
  }
  
  /** Translates a Cyc term into a kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property, without mapping to native OWL objects.
   *
   * @param obj The Cyc term which is to be translated into a kind of OWL node.
   * @return The kind of OWL node: OWL Thing, OWL class, OWL property or
   * OWL transitive property.
   */
  private String translateTerm(final Object obj) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert obj != null : "obj must not be null";
    assert ! (obj instanceof CycVariable) : obj.toString() + " must not be a CycVariable";
    
    String translatedTerm = null;
    if (obj instanceof CycNart || obj instanceof CycList)
      translatedTerm = "#" + xmlNonAtomicTermName((CycObject) obj);
    else if (obj instanceof CycConstant)
      translatedTerm =  "#" + xmlName((CycConstant) obj);
    else
      translatedTerm = obj.toString();
    
    //// Postconditions
    assert translatedTerm != null : "translatedTerm must not be null";
    assert translatedTerm.length() > 0 : "translatedTerm must not be an empty string";
    
    return translatedTerm;
  }
  
  /** Populates the comment for a Cyc term, or with an empty string if no comment.
   *
   * @param cycObject The Cyc term for which the comment is obtained.
   */
  private void populateComment(final CycObject cycObject) throws UnknownHostException, IOException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    String tempComment = null;
    try {
      tempComment = cycAccess.getComment(cycObject);
    }
    catch (CycApiException e) {
    }
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
  private ArrayList applyCycKbSubsetFilter(final CycList constants) throws UnknownHostException, IOException, CycApiException{
    //// Preconditions
    assert constants != null : "constants must not be null";
    
    if (cycKbSubsetFilter == null)
      return constants;
    logger.fine("Applying " + cycKbSubsetFilter.cyclify() + " filter");
    if (constants.size() == 0)
      return  constants;
    final ArrayList result = new ArrayList();
    for (int i = 0; i < constants.size(); i++) {
      Object object = constants.get(i);
      if ((object instanceof CycConstant) &&
          cycAccess.isQuotedIsa((CycConstant) object, cycKbSubsetFilter, inferenceMt))
        result.add(object);
      logger.finer(" dropping " + object);
    }
    return  result;
  }
  
  /** Populates the isas for a Cyc term, removing the case where a term is a type of itself.
   *
   * @param cycObject The Cyc term for which the isas are obtained.
   */
  private void populateIsas(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    if (cycObject instanceof CycList && ((CycList) cycObject).first().toString().equals("InstanceNamedFn")) {
      isas = new CycList();
      isas.add(((CycList) cycObject).third());
    }
    else
      isas = cycAccess.getIsas(cycObject, inferenceMt);
    if (isas.contains(cycObject))
      isas.remove(cycObject);
    isas = substituteGenlConstantsForNarts(isas);
    isas = findAllowedTermsOrGenls(isas);
    if (cycAccess.isBinaryPredicate(cycObject, inferenceMt)) {
      final CycFort transitiveBinaryPredicate = cycAccess.getKnownConstantByName("TransitiveBinaryPredicate");
      if (cycAccess.isa(cycObject, transitiveBinaryPredicate, inferenceMt))
        isas.addNew(transitiveBinaryPredicate);
      final CycFort symmetricBinaryPredicate = cycAccess.getKnownConstantByName("SymmetricBinaryPredicate");
      if (cycAccess.isa(cycObject, symmetricBinaryPredicate, inferenceMt))
        isas.addNew(symmetricBinaryPredicate);
      isas.remove(cycAccess.getKnownConstantByName("Individual"));
    }
    if (isas.isEmpty() && cycAccess.isIndividual(cycObject, inferenceMt))
      isas.add(cycAccess.thing);
    if (! isas.isEmpty()) {
      sortCycObjects(isas);
      logger.fine("    isas: " + isas.toString());
    }
  }
  
  /** Populates the genls for a Cyc term.
   *
   * @param cycObject The Cyc term for which the genls are obtained.
   */
  private void populateGenls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    genls = cycAccess.getGenls(cycObject, inferenceMt);
    if (genls.contains(cycObject))
      genls.remove(cycObject);
    genls = substituteGenlConstantsForNarts(genls);
    genls = findAllowedTermsOrGenls(genls);
    if (! genls.isEmpty()) {
      sortCycObjects(genls);
      logger.fine("    genls: " + genls.toString());
    }
  }
  
  /** Populates the genlPreds for a Cyc predicate.
   *
   * @param cycFort The Cyc predicate for which the genlPreds are obtained.
   */
  private void populateGenlPreds(final CycFort cycFort) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycFort != null : "cycFort must not be null";
    
    genlPreds = cycAccess.getGenlPreds(cycFort, inferenceMt);
    genlPreds = filterSelectedConstants(genlPreds);
    genlPreds = substituteGenlConstantsForNarts(genlPreds);
    if (genlPreds.contains(cycAccess.different))
      genlPreds.remove(cycAccess.different);
    sortCycObjects(genlPreds);
    if (! genlPreds.isEmpty())
      logger.fine("    genlPreds: " + genlPreds.toString());
    
    //// Postconditions
    assert genlPreds != null : "genlPreds must not be null";
  }
  
  /** Populates the argument 1 type constaints for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 1 type constaints are obtained.
   */
  private void populateArg1Isas(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    arg1Isas = cycAccess.getArg1Isas(cycObject, inferenceMt);
    arg1Isas = filterSelectedConstants(arg1Isas);
    if (arg1Isas.size() > 0) {
      if (arg1Isas.size() > 1)
        Collections.sort(arg1Isas);
      logger.fine("    arg1Isas: " + arg1Isas.toString());
    }
  }
  
  /** Populates the argument 2 type constaints for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 2 type constaints are obtained.
   */
  private void populateArg2Isas(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    arg2Isas = cycAccess.getArg2Isas(cycObject, inferenceMt);
    arg2Isas = filterSelectedConstants(arg2Isas);
    if (arg2Isas.size() > 0) {
      if (arg2Isas.size() > 1)
        Collections.sort(arg2Isas);
      logger.fine("    arg2Isas: " + arg2Isas.toString());
    }
  }
  
  /** Populates the argument 1 format for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 1 format is obtained.
   */
  private void populateArg1Format(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    CycList arg1Formats = cycAccess.getArg1Formats(cycObject, inferenceMt);
    if (arg1Formats.size() > 0) {
      arg1Format = (CycConstant)arg1Formats.first();
      logger.fine("    arg1Format: " + arg1Format.toString());
    }
  }
  
  /**
   * Populates the argument 2 format for a Cyc predicate.
   *
   * @param cycObject The Cyc predicate for which the argument 2 format is obtained.
   */
  private void populateArg2Format(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    CycList arg2Formats = cycAccess.getArg2Formats(cycObject, inferenceMt);
    if (arg2Formats.size() > 0) {
      arg2Format = (CycConstant)arg2Formats.first();
      logger.fine("    arg2Format: " + arg2Format);
    }
  }
  
  /** Populates the disjointWiths for a Cyc collection.
   *
   * @param cycObject The Cyc collection for which the disjointWiths are obtained.
   */
  private void populateDisjointWiths(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    try {
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?COL");
      final String queryString =
        "(#$and " +
        "  (#$elementOf ?COL " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$disjointWith " + cycObject.cyclify() + " ?COL))";
      final CycList query = cycAccess.makeCycList(queryString);
      disjointWiths = cycAccess.queryVariable(queryVariable, query, inferenceMt, (HashMap) null, DISJOINT_WITH_PROBLEM_STORE_NAME);
      disjointWiths = cycAccess.getMinCols(disjointWiths, inferenceMt);
      sortCycObjects(disjointWiths);
      if (! disjointWiths.isEmpty())
        logger.fine("    disjointWiths: " + disjointWiths.cyclify());
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Populates the coExtensionals for a Cyc collection.
   *
   * @param cycObject The Cyc collection for which the coExtensionals are obtained.
   */
  private void populateCoExtensionals(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    try {
      final CycList candidateCoExtensionals = cycAccess.getCoExtensionals(cycObject, inferenceMt);
      coExtensionals = new CycList();
      for (int i = 0; i < candidateCoExtensionals.size(); i++) {
        final CycObject coExtensional = (CycObject) candidateCoExtensionals.get(i);
        if (owlSelectedClasses.contains(coExtensional))
          coExtensionals.add(coExtensional);
      }
      if (! coExtensionals.isEmpty()) {
        sortCycObjects(coExtensionals);
        logger.fine("    coExtensionals: " + coExtensionals.cyclify());
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Populates the interArgIsa1-2 and interArgCondIsa1-2 pairs (PRED COL-2) for a Cyc collection COL-1 such that
   * (interArgIsa1-2 RELN COL-1 COL-2) or (interArgCondIsa1-2 PRED COL-1 COL-2)
   *
   * @param cycObject The Cyc collection for which the the interArgIsa1-2 and interArgCondIsa1-2 (RELN COL-2) pairs are obtained.
   */
  private void populateInterArgIsa1_2s(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgIsa1_2s = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      final String queryString =
        "(#$and " +
        "  (#$interArgIsa1-2 ?PRED " + cycObject.cyclify() + " ?COL-2) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$interArgIsa1-2 ?PRED ?COL-1 ?COL-2))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, INTER_ARG_ISA1_2_PROBLEM_STORE_NAME);
      logger.finer("    interArgIsa query: " + queryString);
      logger.finer("    interArgIsa bindings: " + bindings);
      int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          false,                 // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          collection2,           // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "interArgIsa1-2");      // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection2);
        interArgIsa1_2s.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgIsa1_2s.isEmpty()) {
      sortCycObjects(interArgIsa1_2s);
      logger.fine("    interArgIsa1-2: " + interArgIsa1_2s.toString());
    }
  }
  
  /** Populates the interArgIsa2-1 and interArgCondIsa2-1 pairs (PRED COL-1) for a Cyc collection COL-2 such that
   * (interArgIsa2-1 RELN COL-1 COL-2) or (interArgCondIsa2-1 PRED COL-1 COL-2) holds.
   *
   * @param cycObject The Cyc collection for which the the interArgIsa1-2 and interArgCondIsa1-2 (RELN COL-2) pairs are obtained.
   */
  private void populateInterArgIsa2_1s(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgIsa2_1s = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      final String queryString =
        "(#$and " +
        "  (#$interArgIsa2-1 ?PRED ?COL-1 " + cycObject.cyclify() + ") " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "          (#$interArgIsa2-1 ?PRED ?COL-1 ?COL-2))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, INTER_ARG_ISA2_1_PROBLEM_STORE_NAME);
      logger.finest("    interArgIsa2_1 bindings: " + bindings);
      int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          true,                  // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          collection1,           // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "interArgIsa2-1");      // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection1);
        interArgIsa2_1s.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgIsa2_1s.isEmpty()) {
      sortCycObjects(interArgIsa2_1s);
      logger.fine("    interArgIsa2_1: " + interArgIsa2_1s.toString());
    }
  }
  
  /** Populates the relationAllExists pairs (PRED COL-2) for a Cyc collection COL-1 such that
   * (relatiionAllExists PRED COL-1 COL-2).
   *
   * @param cycObject The Cyc collection for which the relationAllExists pairs (PRED COL-2) are obtained.
   */
  private void populateRelationAllExists(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllExists = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      final String queryString =
        "(#$and " +
        "  (#$relationAllExists ?PRED " + cycObject.cyclify() + " ?COL-2) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + theSetOfOWLSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "         (#$relationAllExists ?PRED ?COL-1 ?COL-2))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null); 
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_PROBLEM_STORE_NAME);
      logger.finer("    relationAllExists query: " + query.toPrettyCyclifiedString(""));
      logger.finer("    relationAllExists bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          false,                 // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          null,                  // allValuesFrom
                          collection2,           // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "relationAllExists");   // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection2);
        relationAllExists.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllExists.isEmpty()) {
      sortCycObjects(relationAllExists);
      logger.fine("    relationAllExists: " + relationAllExists.toString());
    }
  }
  
  /** Populates the relationExistsAll pairs (PRED COL-1) for a Cyc collection COL-2 such that
   * (relatiionAllExists PRED COL-1 COL-2).
   *
   * @param cycObject The Cyc collection for which the relationExistsAll pairs (PRED COL-1) are obtained.
   */
  private void populateRelationExistAlls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationExistsAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      String queryString =
        "(#$and " +
        "  (#$relationExistsAll ?PRED ?COL-1 " + cycObject.cyclify() + ") " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "         (#$relationExistsAll ?PRED ?COL-1 ?COL-2))))))";
      CycList query = cycAccess.makeCycList(queryString);
      CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_PROBLEM_STORE_NAME);
      logger.finest("    relationExistsAll query: " + query.toPrettyCyclifiedString(""));
      logger.finest("    relationExistsAll bindings: " + bindings);
      int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          true,                  // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          null,                  // allValuesFrom
                          collection1,           // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "relationExistsAll");   // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection1);
        relationExistsAlls.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsAlls.isEmpty()) {
      sortCycObjects(relationExistsAlls);
      logger.fine("    relationExistsAlls: " + relationExistsAlls.toString());
    }
  }
  
  /** Populates the relationAllInstance pairs (PRED THING) for a Cyc collection COL-1 such that
   * (relatiionAllInstance PRED COL-1 THING).
   *
   * @param cycObject The Cyc collection for which the relationAllExists pairs (PRED THING) are obtained.
   */
  private void populateRelationAllInstances(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllInstances = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable thingVariable = CycObjectFactory.makeCycVariable("?THING");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(thingVariable);
      final String queryString =
        "(#$and " +
        "  (#$relationAllInstance ?PRED " + cycObject.cyclify() + " ?THING) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$relationAllInstance ?PRED ?COL-1 ?THING))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_INSTANCE_PROBLEM_STORE_NAME);
      logger.finest("    relationAllInstance bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        Object thing = binding.second();
        if (thing instanceof CycObject && ! selectedCycForts.contains(thing))
          continue;
        if (! (thing instanceof CycObject))
          thing = possiblyCreateIndividual(predicate, false, thing);
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          predicate,             // property
                          false,                 // isInverseProperty
                          thing,                 // hasValue
                          null,                  // valuesFrom
                          null,                  // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "relationAllInstance"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(thing);
        relationAllInstances.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllInstances.isEmpty()) {
      sortCycObjects(relationAllInstances);
      logger.fine("    relationAllInstance: " + relationAllInstances.toString());
    }
  }
    
  /** Populates the relationInstanceAll pairs (PRED THING) for a Cyc collection COL such that
   * (relatiionInstanceAll PRED THING COL).
   *
   * @param cycObject The Cyc collection for which the relationInstanceAll pairs (PRED THING) are obtained.
   */
  private void populateRelationInstanceAlls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationInstanceAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collectionVariable = CycObjectFactory.makeCycVariable("?THING");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collectionVariable);
      final String queryString =
        "(#$and " + 
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$relationInstanceAll ?PRED ?THING " + cycObject.cyclify() + ") " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL " +
        "      (#$and " +
        "        (#$elementOf ?COL " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL) " +
        "        (#$different " + cycObject.cyclify() + " ?COL) " +
        "        (#$knownSentence " +
        "          (#$relationInstanceAll ?PRED ?THING ?COL))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME);
      logger.finest("    relationInstanceAll bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        Object thing = binding.second();
        if (thing instanceof CycObject && ! selectedCycForts.contains(thing))
          continue;
        if (! (thing instanceof CycObject))
          thing = possiblyCreateIndividual(predicate, true, thing);
        final Restriction restriction =
          new Restriction(TYPE_RESTRICTION,      // restrictionType
                          predicate,             // property
                          true,                  // isInverseProperty
                          thing,                 // hasValue
                          null,                  // valuesFrom
                          null,                  // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                         "relationInstanceAll"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(thing);
        relationInstanceAlls.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationInstanceAlls.isEmpty()) {
      sortCycObjects(relationInstanceAlls);
      logger.fine("    relationInstanceAll: " + relationInstanceAlls.toString());
    }
  }
  
  /** Populates the relationInstanceExists pairs (PRED COL) for a Cyc THING such that
   * (relationInstanceExists PRED THING COL).
   *
   * @param thing The Cyc individual for which the relationInstanceExists pairs (PRED COL) are obtained.
   */
  private void populateRelationInstanceExists(final CycObject thing) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert thing != null : "thing must not be null";
    
    relationInstanceExists = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collectionVariable = CycObjectFactory.makeCycVariable("?COL");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collectionVariable);
      final String queryString =
        "(#$relationInstanceExists ?PRED " + thing.cyclify() + " ?COL)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME);
      logger.finest("    relationInstanceExists bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        final CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          continue;
        final CycObject collection = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection))
          continue;
        final Restriction restriction =
          new Restriction(TYPE_RESTRICTION,         // restrictionType
                          predicate,                // property
                          false,                    // isInverseProperty
                          null,                     // hasValue
                          null,                     // valuesFrom
                          null,                     // allValuesFrom
                          collection,               // someValuesFrom
                          -1,                       // cardinality
                          -1,                       // maxCardinality
                          -1,                       // minCardinality
                         "relationInstanceExists"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection);
        relationInstanceExists.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationInstanceExists.isEmpty()) {
      sortCycObjects(relationInstanceExists);
      logger.fine("    relationInstanceExists: " + relationInstanceExists.toString());
    }
  }
  
  /** Populates the relationExistsInstance pairs (PRED COL) for a Cyc THING such that
   * (relationExistsInstance PRED COL THING).
   *
   * @param thing The Cyc individual for which the relationExistsInstance pairs (PRED COL) are obtained.
   */
  private void populateRelationExistsInstance(final CycObject thing) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert thing != null : "thing must not be null";
    
    relationExistsInstances = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collectionVariable = CycObjectFactory.makeCycVariable("?COL");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collectionVariable);
      final String queryString =
        "(#$relationExistsInstance ?PRED ?COL " + thing.cyclify() + ")";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME);
      logger.fine("    relationExistsInstance bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        final CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          continue;
        final CycObject collection = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection))
          continue;
        final Restriction restriction =
          new Restriction(TYPE_RESTRICTION,         // restrictionType
                          predicate,                // property
                          true,                     // isInverseProperty
                          null,                     // hasValue
                          null,                     // valuesFrom
                          null,                     // allValuesFrom
                          collection,               // someValuesFrom
                          -1,                       // cardinality
                          -1,                       // maxCardinality
                          -1,                       // minCardinality
                         "relationExistsInstance"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection);
        relationExistsInstances.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsInstances.isEmpty()) {
      sortCycObjects(relationExistsInstances);
      logger.fine("    relationExistsInstance: " + relationExistsInstances.toString());
    }
  }
  
  /** Populates the relationAllExistsCount tuples (PRED COL-1 COL-2 CARD) for a Cyc individual term THING such that
   * (relationAllExistsCount PRED COL-1 COL-2 NUM).
   *
   * @param cycObject The Cyc individual for which the relationAllExistsCount tuples (PRED COL-2 CARD) are obtained.
   */
  private void populateRelationAllExistsCounts(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllExistsCounts = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationAllExistsCount ?PRED " + cycObject.cyclify() + " ?COL-2 ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$relationAllExistsCount ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ? 
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_COUNT_PROBLEM_STORE_NAME);
      logger.finest("    relationAllExistsCount bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Number cardinality = (Number) binding.third();
        if (! exportQualifiedCardinalityRestrictions && cardinality.intValue() == 0)
          continue;
        Restriction restriction = null;
        if (exportQualifiedCardinalityRestrictions)
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,     // restrictionType
                            predicate,                // property
                            false,                    // isInverseProperty
                            null,                     // hasValue
                            collection2,              // valuesFrom
                            null,                     // allValuesFrom
                            null,                     // someValuesFrom
                            cardinality.intValue(),   // cardinality
                            -1,                       // maxCardinality
                            -1,                       // minCardinality
                           "relationAllExistsCount"); // sourceRuleMacroPredicate
        else if (cardinality.intValue() > 0)
          // if QCRs not exported  and card > 0, then export same as relationAllExists
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,     // restrictionType
                            (CycFort) predicate,      // property
                            false,                    // isInverseProperty
                            null,                     // hasValue
                            null,                     // valuesFrom
                            null,                     // allValuesFrom
                            collection2,              // someValuesFrom
                            -1,                       // cardinality
                            -1,                       // maxCardinality
                            -1,                       // minCardinality
                           "relationAllExistsCount"); // sourceRuleMacroPredicate
        else
          // QCRs not exported and card = 0, so do not export the restriction
          continue;
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection2);
        tuple.add(cardinality);
        relationAllExistsCounts.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllExistsCounts.isEmpty()) {
      sortCycObjects(relationInstanceAlls);
      logger.fine("    relationAllExistsCounts: " + relationAllExistsCounts.toString());
    }
  }
  
  /** Populates the relationAllExistsMax tuples (PRED COL-1 COL-2 CARD) for a Cyc individual term THING such that
   * (relationAllExistsCount PRED COL-1 COL-2 NUM).
   *
   * @param cycObject The Cyc individual for which the relationAllExistsMax tuples (PRED COL-2 CARD) are obtained.
   */
  private void populateRelationAllExistMaxs(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllExistMaxs = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationAllExistsMax ?PRED " + cycObject.cyclify() + " ?COL-2 ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$relationAllExistsMax ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_MAX_PROBLEM_STORE_NAME);
      logger.finest("    relationAllExistsMax bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Number cardinality = (Number) binding.third();
        if (! exportQualifiedCardinalityRestrictions)
          // drop this restriction if QCRs not exported
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,   // restrictionType
                          predicate,              // property
                          false,                  // isInverseProperty
                          null,                   // hasValue
                          collection2,            // valuesFrom
                          null,                   // allValuesFrom
                          null,                   // someValuesFrom
                          -1,                     // cardinality
                          cardinality.intValue(), // maxCardinality
                          -1,                     // minCardinality
                         "relationAllExistsMax"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection2);
        tuple.add(cardinality);
        relationAllExistMaxs.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllExistMaxs.isEmpty()) {
      sortCycObjects(relationAllExistMaxs);
      logger.fine("    relationAllExistMaxs: " + relationAllExistMaxs.toString());
    }
  }
  
  /** Populates the relationAllExistsMin tuples (PRED COL-1 COL-2 CARD) for a Cyc individual term THING such that
   * (relationAllExistsCount PRED COL-1 COL-2 NUM).
   *
   * @param cycObject The Cyc individual for which the relationAllExistsMin tuples (PRED COL-2 CARD) are obtained.
   */
  private void populateRelationAllExistMins(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllExistMins = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationAllExistsMin ?PRED " + cycObject.cyclify() + " ?COL-2 ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$relationAllExistsMin ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_MIN_PROBLEM_STORE_NAME);
      logger.finest("    relationAllExistsMin bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Number cardinality = (Number) binding.third();
        Restriction restriction = null;
        if (exportQualifiedCardinalityRestrictions)
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            predicate,               // property
                            false,                   // isInverseProperty
                            null,                    // hasValue
                            collection2,             // valuesFrom
                            null,                    // allValuesFrom
                            null,                    // someValuesFrom
                            -1,                      // cardinality
                            -1,                      // maxCardinality
                            cardinality.intValue(),  // minCardinality
                           "relationAllExistsMin");  // sourceRuleMacroPredicate
        else if (cardinality.intValue() > 0)
          // if QCRs not exported  and card > 0, then export same as relationAllExists
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            (CycFort) predicate,     // property
                            false,                   // isInverseProperty
                            null,                    // hasValue
                            null,                    // valuesFrom
                            null,                    // allValuesFrom
                            collection2,             // someValuesFrom
                            -1,                      // cardinality
                            -1,                      // maxCardinality
                            -1,                      // minCardinality
                           "relationAllExistsMin");  // sourceRuleMacroPredicate
        else
          // QCRs not exported and card = 0, so do not export the restriction
          continue;
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection2);
        tuple.add(cardinality);
        relationAllExistMins.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllExistMins.isEmpty()) {
      sortCycObjects(relationAllExistMins);
      logger.fine("    relationAllExistMins: " + relationAllExistMins.toString());
    }
  }
  
  /** Populates the relationExistsCountAll tuples (PRED COL-1 CARD) for a Cyc collection term COL-1 such that
   * (relationExistsCountAll PRED COL-1 COL-2 CARD).
   *
   * @param cycObject The Cyc collection for which the relationExistsCountAll tuples (PRED COL-1 CARD) are obtained.
   */
  private void populateRelationExistsCountAlls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationExistsCountAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationExistsCountAll ?PRED ?COL-1 " + cycObject.cyclify() + " ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "         (#$relationExistsCountAll ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_EXISTS_COUNT_ALL_PROBLEM_STORE_NAME);
      logger.finest("    relationExistsCountAll bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Number cardinality = (Number) binding.third();
        Restriction restriction;
        if (exportQualifiedCardinalityRestrictions)
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,     // restrictionType
                            predicate,                // property
                            true,                     // isInverseProperty
                            null,                     // hasValue
                            collection1,              // valuesFrom
                            null,                     // allValuesFrom
                            null,                     // someValuesFrom
                            cardinality.intValue(),   // cardinality
                            -1,                       // maxCardinality
                            -1,                       // minCardinality
                           "relationExistsCountAll"); // sourceRuleMacroPredicate
        else if (cardinality.intValue() > 0)
          // if QCRs not exported  and card > 0, then export same as relationExistsAll
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,     // restrictionType
                            (CycFort) predicate,      // property
                            true,                     // isInverseProperty
                            null,                     // hasValue
                            null,                     // valuesFrom
                            null,                     // allValuesFrom
                            collection1,              // someValuesFrom
                            -1,                       // cardinality
                            -1,                       // maxCardinality
                            -1,                       // minCardinality
                           "relationExistsCountAll"); // sourceRuleMacroPredicate
        else
          // if QCRs not exported and card == 0, then drop this restriction
          continue;
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection1);
        tuple.add(cardinality);
        relationExistsCountAlls.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsCountAlls.isEmpty()) {
      sortCycObjects(relationExistsCountAlls);
      logger.fine("    relationExistsCountAlls: " + relationExistsCountAlls.toString());
    }
  }
  
  /** Populates the relationExistsMaxAll tuples (PRED COL-1 CARD) for a Cyc collection term COL-2 such that
   * (relationExistsMaxAll PRED COL-1 COL-2 CARD).
   *
   * @param cycObject The Cyc collection for which the relationExistsMaxAll tuples (PRED COL-1 CARD) are obtained.
   */
  private void populateRelationExistsMaxAlls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationExistsMaxAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationExistsMaxAll ?PRED ?COL-1 " + cycObject.cyclify() + " ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "         (#$relationExistsMaxAll ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_EXISTS_MAX_ALL_PROBLEM_STORE_NAME);
      logger.finest("    relationExistsMaxAll bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Number cardinality = (Number) binding.third();
        if (! exportQualifiedCardinalityRestrictions)
          // drop this restriction if QCRs not exported
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                          predicate,               // property
                          true,                    // isInverseProperty
                          null,                    // hasValue
                          collection1,             // valuesFrom
                          null,                    // allValuesFrom
                          null,                    // someValuesFrom
                          -1,                      // cardinality
                          cardinality.intValue(),  // maxCardinality
                          -1,                      // minCardinality
                         "relationExistsMaxAll");  // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection1);
        tuple.add(cardinality);
        relationExistsMaxAlls.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsMaxAlls.isEmpty()) {
      sortCycObjects(relationExistsMaxAlls);
      logger.fine("    relationExistsMaxAlls: " + relationExistsMaxAlls.toString());
    }
  }
  
  /** Populates the relationExistsMinAll tuples (PRED COL-1 CARD) for a Cyc collection term COL-2 such that
   * (relationExistsMinAll PRED COL-1 COL-2 CARD).
   *
   * @param cycObject The Cyc collection for which the relationExistsMinAll tuples (PRED COL-1 CARD) are obtained.
   */
  private void populateRelationExistsMinAlls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationExistsMinAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$relationExistsMinAll ?PRED ?COL-1 " + cycObject.cyclify() + " ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "         (#$relationExistsMinAll ?PRED ?COL-1 ?COL-2 ?CARD))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_EXISTS_MIN_ALL_PROBLEM_STORE_NAME);
      logger.finest("    relationExistsMinAll bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Number cardinality = (Number) binding.third();
        Restriction restriction = null;
        if (exportQualifiedCardinalityRestrictions)
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            predicate,               // property
                            true,                    // isInverseProperty
                            null,                    // hasValue
                            collection1,             // valuesFrom
                            null,                    // allValuesFrom
                            null,                    // someValuesFrom
                            -1,                      // cardinality
                            -1,                      // maxCardinality
                            cardinality.intValue(),  // minCardinality
                           "relationExistsMinAll");  // sourceRuleMacroPredicate
        else if (cardinality.intValue() > 0)
          // if QCRs not exported and min > 0 then export same as relationExistsAll
          restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            (CycFort) predicate,     // property
                            true,                    // isInverseProperty
                            null,                    // hasValue
                            null,                    // valuesFrom
                            null,                    // allValuesFrom
                            collection1,             // someValuesFrom
                            -1,                      // cardinality
                            -1,                      // maxCardinality
                            -1,                      // minCardinality
                           "relationExistsMinAll");  // sourceRuleMacroPredicate
        else
          // if QCRs not exported and min == 0 then drop this restriction
          continue;
        addRestriction(restriction);
        final CycList tuple = new CycList(2);
        tuple.add(predicate);
        tuple.add(collection1);
        tuple.add(cardinality);
        relationExistsMinAlls.addNew(tuple);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsMinAlls.isEmpty()) {
      sortCycObjects(relationExistsMinAlls);
      logger.fine("    relationExistsMinAlls: " + relationExistsMinAlls.toString());
    }
  }
  
  
  // This method is de-activated.  Note that relationExistsAllMany is a spec pred of relationExistsAll, not clear
  // whether "many" can be expressed in OWL as a cardinality constraint.  If this is reinstated then add a
  // method for relationAllExistsMany.
  
  /** Populates the relationExistsAllMany pairs (PRED COL-2) for a Cyc collection term COL-1 such that
   * (relationExistsAllMany PRED COL-1 COL-2).
   *
   * @param cycObject The Cyc collection for which the relationExistsAllMany pairs (PRED COL-2) are obtained.
   */
  private void populateRelationExistsAllMany(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationExistsAllManys = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      final String queryString =
      // TODO add the genls filter if this method is re-activated
        "(#$and " + 
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$relationExistsAllMany ?PRED " + cycObject.cyclify() + " ?COL-2))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_EXISTS_ALL_MANY_PROBLEM_STORE_NAME);
      logger.finest("    relationExistsAllMany bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Number cardinality = (Number) binding.third();
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                          predicate,               // property
                          true,                    // isInverseProperty
                          null,                    // hasValue
                          null,                    // valuesFrom
                          collection2,             // allValuesFrom
                          null,                    // someValuesFrom
                          -1,                      // cardinality
                          -1,                      // maxCardinality
                          cardinality.intValue(),  // minCardinality
                         "relationExistsAllMany"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection2);
        relationExistsAllManys.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationExistsAllManys.isEmpty()) {
      sortCycObjects(relationExistsAllManys);
      logger.fine("    relationExistsAllManys: " + relationExistsAllManys.toString());
    }
  }
  
  // TODO later for completeness, we should populate arg cardinality where the constraint is on the first
  // argument  as documented in the requirements.
  
  
  /** Populates the argCardinality pairs (PRED CARD) for a Cyc collection COL such that
   * (and (arg1Isa PRED COL) (argCardinality PRED 2 CARD)).
   *
   * @param cycObject the given Cyc collection
   */
  private void populateArg2Cardinalities(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    arg2Cardinalities = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " +
        "  (#$argCardinality ?PRED 2 ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$assertedSentence " +
        "    (#$arg1Isa ?PRED " + cycObject.cyclify() + ")) " +
        "  (#$greaterThan ?CARD 1))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, ARG_CARDINALITY_PROBLEM_STORE_NAME);
      logger.finest("    argCardinality bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        final Number cardinality = (Number) binding.second();
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                          predicate,               // property
                          false,                   // isInverseProperty
                          null,                    // hasValue
                          null,                    // valuesFrom
                          null,                    // allValuesFrom
                          null,                    // someValuesFrom
                          -1,                      // cardinality
                          cardinality.intValue(),  // maxCardinality
                          -1,                      // minCardinality
                         "argCardinality");        // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(cardinality);
        arg2Cardinalities.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! arg2Cardinalities.isEmpty()) {
      sortCycObjects(arg2Cardinalities);
      logger.fine("    arg2Cardinalities: " + arg2Cardinalities.toString());
    }
  }
  
  /** Populates the interArgCardinality pairs (PRED CARD) for a Cyc collection COL such that
   * (interArgCardinality PRED 1 COL 2 CARD).
   *
   * @param cycObject the given Cyc collection
   */
  private void populateInterArgCardinalities1_2(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgCardinalities1_2 = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$interArgCardinality ?PRED 1 " + cycObject.cyclify() + " 2 ?CARD))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, INTER_ARG_CARDINALITY_PROBLEM_STORE_NAME);
      logger.finest("    interArgCardinality 1 2 bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        //TODO later assert selectedCycForts.contains(predicate)
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        final Number cardinality = (Number) binding.second();
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,      // restrictionType
                          predicate,                 // property
                          false,                     // isInverseProperty
                          null,                      // hasValue
                          null,                      // valuesFrom
                          null,                      // allValuesFrom
                          null,                      // someValuesFrom
                          -1,                        // cardinality
                          cardinality.intValue(),    // maxCardinality
                          -1,                        // minCardinality
                         "interArgCardinality 1 2"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(cardinality);
        interArgCardinalities1_2.add(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgCardinalities1_2.isEmpty()) {
      sortCycObjects(interArgCardinalities1_2);
      logger.fine("    interArgCardinalities1_2: " + interArgCardinalities1_2.toString());
    }
  }
  
  /** Populates the interArgCardinality pairs (PRED CARD) for a Cyc collection COL such that
   * (interArgCardinality PRED 2 COL 1 CARD), where PRED is the inverse form of the predicate.
   *
   * @param cycObject the given Cyc collection
   */
  private void populateInterArgCardinalities2_1(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgCardinalities1_2 = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable cardinalityVariable = CycObjectFactory.makeCycVariable("?CARD");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(cardinalityVariable);
      final String queryString =
        "(#$and " + 
        "  (#$interArgCardinality ?PRED 2 " + cycObject.cyclify() + " 1 ?CARD) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, INTER_ARG_CARDINALITY_PROBLEM_STORE_NAME);
      logger.finest("    interArgCardinality 2 1 bindings: " + bindings);
      final int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        //TODO later assert selectedCycForts.contains(predicate)
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        final Number cardinality = (Number) binding.second();
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,      // restrictionType
                          predicate,                 // property
                          true,                      // isInverseProperty
                          null,                      // hasValue
                          null,                      // valuesFrom
                          null,                      // allValuesFrom
                          null,                      // someValuesFrom
                          -1,                        // cardinality
                          cardinality.intValue(),    // maxCardinality
                          -1,                        // minCardinality
                         "interArgCardinality 1 2"); // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(cardinality);
        interArgCardinalities1_2.add(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgCardinalities1_2.isEmpty()) {
      sortCycObjects(interArgCardinalities1_2);
      logger.fine("    interArgCardinalities1_2: " + interArgCardinalities1_2.toString());
    }
  }
  
  /** Populates the interArgFormat1-2 PREDs for a Cyc collection COL such that
   * (interArgFormat1-2 PRED COL singleEntryFormatInArgs) or.
   * (interArgFormat1-2 PRED COL SingleEntry) or.
   *
   * @param cycObject the given Cyc collection
   */
  private void populateInterArgFormats1_2(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgFormats1_2 = new CycList();
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final String queryString =
        "(#$and " + 
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$or " +
        "    (#$interArgFormat1-2 ?PRED " + cycObject.cyclify() + " #$singleEntryFormatInArgs) " +
        "    (#$interArgFormat1-2 ?PRED " + cycObject.cyclify() + " #$SingleEntry)))";
      final CycList query = cycAccess.makeCycList(queryString);
      interArgFormats1_2 = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null);
//      interArgFormats1_2 = cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null, INTER_ARG_FORMATS_PROBLEM_STORE_NAME);
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgFormats1_2.isEmpty()) {
      sortCycObjects(interArgFormats1_2);
      logger.fine("    interArgFormat1_2s: " + interArgFormats1_2.toString());
      for (int i = 0; i < interArgFormats1_2.size(); i++) {
        final CycFort predicate = (CycFort) interArgFormats1_2.get(i);
        boolean haveCardinalityForPredicate = false;
        for (int j = 0; j < arg2Cardinalities.size(); j++) {
          final CycList pair = (CycList) arg2Cardinalities.get(j);
          if (pair.first().equals(predicate)) {
            haveCardinalityForPredicate = true;
            break;
          }
        }
        for (int j = 0; j < interArgFormats1_2.size(); j++) {
          final CycList pair = (CycList) interArgFormats1_2.get(j);
          if (pair.first().equals(predicate)) {
            haveCardinalityForPredicate = true;
            break;
          }
        }
        if (! selectedCycForts.contains(predicate))
          continue;
        if (! haveCardinalityForPredicate) {
          final Restriction restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            predicate,               // property
                            false,                   // isInverseProperty
                            null,                    // hasValue
                            null,                    // valuesFrom
                            null,                    // allValuesFrom
                            null,                    // someValuesFrom
                            -1,                      // cardinality
                            1,                       // maxCardinality
                            -1,                      // minCardinality
                            "interArgFormat1_2");    // sourceRuleMacroPredicate
          addRestriction(restriction);
        }
      }
    }
  }
  
  /** Populates the interArgFormat2-1 inverse PREDs for a Cyc collection COL such that
   * (interArgFormat2-1 PRED COL singleEntryFormatInArgs) or
   * (interArgFormat2-1 PRED COL SingleEntry).
   *
   * @param cycObject the given Cyc collection
   */
  private void populateInterArgFormats2_1(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    interArgFormats2_1 = new CycList();
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final String queryString =
        "(#$and " + 
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$or " +
        "    (#$interArgFormat2-1 ?PRED " + cycObject.cyclify() + " #$singleEntryFormatInArgs) " +
        "    (#$interArgFormat2-1 ?PRED " + cycObject.cyclify() + " #$SingleEntry)))";
      final CycList query = cycAccess.makeCycList(queryString);
      interArgFormats2_1 = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null);
//      interArgFormats2_1 = cycAccess.queryVariable(predicateVariable, query, inferenceMt, (HashMap) null, INTER_ARG_FORMATS_PROBLEM_STORE_NAME);
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! interArgFormats2_1.isEmpty()) {
      sortCycObjects(interArgFormats2_1);
      logger.fine("    interArgFormat2_1s: " + interArgFormats2_1.toString());
      for (int i = 0; i < interArgFormats1_2.size(); i++) {
        final CycFort predicate = (CycFort) interArgFormats2_1.get(i);
        boolean haveCardinalityForPredicate = false;
        for (int j = 0; j < arg2Cardinalities.size(); j++) {
          final CycList pair = (CycList) arg2Cardinalities.get(j);
          if (pair.first().equals(predicate)) {
            haveCardinalityForPredicate = true;
            break;
          }
        }
        for (int j = 0; j < interArgFormats2_1.size(); j++) {
          final CycList pair = (CycList) interArgFormats1_2.get(j);
          if (pair.first().equals(predicate)) {
            haveCardinalityForPredicate = true;
            break;
          }
        }
        if (! selectedCycForts.contains(predicate))
          continue;
        if (! haveCardinalityForPredicate) {
          final Restriction restriction =
            new Restriction(SUBCLASS_RESTRICTION,    // restrictionType
                            predicate,               // property
                            true,                    // isInverseProperty
                            null,                    // hasValue
                            null,                    // valuesFrom
                            null,                    // allValuesFrom
                            null,                    // someValuesFrom
                            -1,                      // cardinality
                            1,                       // maxCardinality
                            -1,                      // minCardinality
                            "interArgFormat2_1");    // sourceRuleMacroPredicate
          addRestriction(restriction);
        }
      }
    }
  }
  
  /** Populates the typeGenls COL-2 for a Cyc collection COL-1 such that (typeGenls COL-1 COL-2).
   *
   * @param cycObject The Cyc collection for which the typeGenls COL-2 for a Cyc collection COL-1 are obtained.
   */
  private void populateTypeGenls(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    typeGenls = new CycList();
    try {
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final String queryString =
        "(#$typeGenls " + cycObject.cyclify() + " ?COL-2)";
      final CycList query = cycAccess.makeCycList(queryString);
      typeGenls = cycAccess.queryVariable(collection2Variable, query, inferenceMt, (HashMap) null);
      typeGenls = findAllowedTermsOrGenls(typeGenls);
      if (! typeGenls.isEmpty()) {
        for (int i = 0; i < typeGenls.size(); i++) {
          final CycObject collection2 = (CycObject) typeGenls.get(i);
          final Restriction restriction =
            new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
            cycAccess.genls,       // property
            false,                 // isInverseProperty
            collection2,           // hasValue
            null,                  // valuesFrom
            null,                  // allValuesFrom
            null,                  // someValuesFrom
            -1,                    // cardinality
            -1,                    // maxCardinality
            -1,                    // minCardinality
            "typeGenls");          // sourceRuleMacroPredicate
          addRestriction(restriction);
        }
        
        sortCycObjects(typeGenls);
        logger.fine("    typeGenls: " + typeGenls);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Populates the relationAllOnly pairs (PRED COL-2) for a Cyc collection COL-1 such that
   * (relationAllOnly RELN COL-1 COL-2) holds.
   *
   * @param cycObject The Cyc collection for which the the relationAllOnly-2 (RELN COL-2) pairs are obtained.
   */
  private void populateRelationAllOnly(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationAllOnlys = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection2Variable);
      String queryString =
        "(#$and " +
        "  (#$relationAllOnly ?PRED " + cycObject.cyclify() + " ?COL-2) " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-2) (any-genl? ?COL-2 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-1 " +
        "      (#$and " +
        "        (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-1) " +
        "        (#$knownSentence " +
        "          (#$relationAllOnly ?PRED ?COL-1 ?COL-2))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ALL_ONLY_PROBLEM_STORE_NAME);
      int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection2 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection2))
          collection2 = findSelectedGenls(collection2);
        if (! selectedCycForts.contains(collection2))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          false,                 // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          collection2,           // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                          "relationAllOnly");    // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection2);
        relationAllOnlys.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationAllOnlys.isEmpty()) {
      sortCycObjects(relationAllOnlys);
      logger.fine("    relationAllOnly: " + relationAllOnlys.toString());
    }
  }
  
  /** Populates the relationOnlyAll pairs (PRED COL-1) for a Cyc collection COL-2 such that
   * (relationOnlyAll-1 RELN COL-1 COL-2) holds.
   *
   * @param cycObject The Cyc collection for which the the interArgIsa1-2 and interArgCondIsa1-2 (RELN COL-2) pairs are obtained.
   */
  private void populateRelationOnlyAll(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    relationOnlyAlls = new CycList();
    if (owlSelectedProperties.isEmpty())
      return;
    try {
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycList variables = new CycList();
      variables.add(predicateVariable);
      variables.add(collection1Variable);
      String queryString =
        "(#$and " +
        "  (#$relationOnlyAll ?PRED ?COL-1 " + cycObject.cyclify() + ") " +
        "  (#$trueSubL (#$ExpandSubLFn (?PRED) (any-genl-predicate? ?PRED `" + owlSelectedProperties.cyclify() + "))) " +
        "  (#$trueSubL (#$ExpandSubLFn (?COL-1) (any-genl? ?COL-1 `" + owlSelectedClasses.cyclify() + "))) " +
        "  (#$unknownSentence " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$elementOf ?COL-2 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "        (#$genls " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$different " + cycObject.cyclify() + " ?COL-2) " +
        "        (#$knownSentence " +
        "          (#$relationOnlyAll ?PRED ?COL-1 ?COL-2))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      CycList bindings = (PROBLEM_STORE_REUSE) ?
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, allQueriesProblemStoreName) :
        cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
//      CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null, RELATION_ONLY_ALL_PROBLEM_STORE_NAME);
      int bindings_size = bindings.size();
      for (int i = 0; i < bindings_size; i++) {
        final CycList binding = (CycList) bindings.get(i);
        CycObject predicate = (CycObject) binding.first();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (! selectedCycForts.contains(predicate))
          continue;
        CycObject collection1 = (CycObject) binding.second();
        if (! selectedCycForts.contains(collection1))
          collection1 = findSelectedGenls(collection1);
        if (! selectedCycForts.contains(collection1))
          continue;
        final Restriction restriction =
          new Restriction(SUBCLASS_RESTRICTION,  // restrictionType
                          (CycFort) predicate,   // property
                          true,                  // isInverseProperty
                          null,                  // hasValue
                          null,                  // valuesFrom
                          collection1,           // allValuesFrom
                          null,                  // someValuesFrom
                          -1,                    // cardinality
                          -1,                    // maxCardinality
                          -1,                    // minCardinality
                          "relationOnlyAll");    // sourceRuleMacroPredicate
        addRestriction(restriction);
        final CycList pair = new CycList(2);
        pair.add(predicate);
        pair.add(collection1);
        relationOnlyAlls.addNew(pair);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    if (! relationOnlyAlls.isEmpty()) {
      sortCycObjects(relationOnlyAlls);
      logger.fine("    relationOnlyAll: " + relationOnlyAlls.toString());
    }
  }
  
  /** Populates the instances of a completely asserted collection.
   *
   * @param collection the given Cyc collection
   */
  private void populateCompletelyAssertedCollectionInstances(final CycObject collection) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert collection != null : "collection must not be null";
    
    if (! isCompletelyAssertedCollection(collection)) {
      completelyAssertedCollectionInstances = null;
      return;
    }
    try {
      completelyAssertedCollectionInstances = cycAccess.getInstances(collection, inferenceMt);
      for (int i = 0; i < completelyAssertedCollectionInstances.size(); i++)
        if (! selectedCycForts.contains(completelyAssertedCollectionInstances.get(i))) {
          completelyAssertedCollectionInstances = null;
          return;
        }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    sortCycObjects(completelyAssertedCollectionInstances);
    logger.fine("    completelyAssertedCollectionInstances: " + completelyAssertedCollectionInstances.toString());
  }
  
  /** Populates the list of selected individuals that are equal to the given individual.
   *
   * @param cycObject The term which appears in the first argument position.
   */
  private void populateEquals(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    assert theSetOfOWLSelectedIndividuals != null : "theSetOfOWLSelectedClasses must not be null";
    
    try {
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?INDIVIDUAL");
      final String queryString =
        "(#$and " +
        "  (#$elementOf ?INDIVIDUAL " + theSetOfOWLSelectedIndividuals.cyclify() + ") " +
        "  (#$equals " + cycObject.cyclify() + " ?INDIVIDUAL))";
      final CycList query = cycAccess.makeCycList(queryString);
      equals = cycAccess.queryVariable(queryVariable, query, inferenceMt, (HashMap) null);
      equals.remove(cycObject);
      if (! equals.isEmpty()) {
        sortCycObjects(equals);
        logger.fine("    equals: " + equals.cyclify());
        nonDifferentIndividuals.addAllNew(equals);
      }
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Populates the list of implication rules in which the term appears.
   *
   * @param cycObject the given term
   */
  private void populateRules(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    if (! (cycObject instanceof CycFort))
      return;
    final String script = "(gather-filtered-el-rule-assertions-for-term " + cycObject.stringApiValue() + selectedCycForts.stringApiValue() + ")";
    CycList gatheredRulesForTerm = new CycList(0);
    try {
      gatheredRulesForTerm = cycAccess.converseList(script);
    }
    catch (Exception e) {
      logger.warning(StringUtils.getStringForException(e));
      return;
    }
    final int gatheredRulesForTerm_size = gatheredRulesForTerm.size();
    final CycList newRules = new CycList(gatheredRulesForTerm_size);
    for (int i = 0; i < gatheredRulesForTerm_size; i++) {
      final CycList gatheredRuleForTerm = (CycList) gatheredRulesForTerm.get(i);
      if (! rules.contains(gatheredRuleForTerm)) {
        rules.add(gatheredRuleForTerm);
        newRules.add(gatheredRuleForTerm);
      }
    }
    if (! newRules.isEmpty())
      logger.fine("    rules: " + newRules.cyclify());
  }
  
  /** Populates the non-definitional ground atomic formulas in which the the
   * predicate is an element of the list of applicable binary predicates and in
   * which the given term appears as the first argument.
   *
   * @param cycObject The term which appears in the first argument position.
   */
  private void populatePropertyAssertions(final CycObject cycObject) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycObject != null : "cycObject must not be null";
    
    CycList candidatePropertyAssertions = null;
    propertyAssertions = new CycList();
    if (applicableBinaryPredicates.isEmpty())
      return;
    if (termsWithNoExtraProperties.contains(cycObject)) {
      logger.finest("      term with no extra properties: " + cycObject.cyclify());
      return;
    }
    try {
      final CycObject canonicalizedObject = (CycObject) cycAccess.getHLCycTerm(cycObject.cyclify());
      if (canonicalizedObject instanceof CycFort) {
        candidatePropertyAssertions = cycAccess.getGafsForPredicates(canonicalizedObject, applicableBinaryPredicates, inferenceMt);
        logger.finest("      candidatePropertyAssertions: " + candidatePropertyAssertions.cyclify());
      }
      else
        return;
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
    candidatePropertyAssertions.remove(cycAccess.isa);
    candidatePropertyAssertions.remove(cycAccess.genls);
    for (int i = 0; i < candidatePropertyAssertions.size(); i++) {
      final CycList candidatePropertyAssertion = (CycList) candidatePropertyAssertions.get(i);
      final Object arg2 = candidatePropertyAssertion.third();
      if (arg2 instanceof CycObject) {
        if (selectedCycForts.contains(arg2)) {
          propertyAssertions.add(candidatePropertyAssertion);
          logger.fine("      " + candidatePropertyAssertion.cyclify());
        }
        else if (cycKbSubsetCollection != null)
          logger.finest("      quoted " + ((CycFort) candidatePropertyAssertion.third()).cyclify() + " is not a " + cycKbSubsetCollection.cyclify());
      }
      else {
        propertyAssertions.add(candidatePropertyAssertion);
        logger.fine("      " + candidatePropertyAssertion.cyclify());
      }
    }
    sortCycObjects(propertyAssertions);
  }
  
  /** Returns true if the given predicate is an owl:ObjectProperty.
   *
   * @param predicate the given predicate
   * @return true if the given predicate is an owl:ObjectProperty
   */
  private boolean isObjectProperty(final CycObject predicate) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    
    String query =
      "(#$and " +
      "   (#$isa " + predicate.cyclify() + " #$BinaryPredicate) " +
      "   (#$arg1Isa " + predicate.cyclify() + " ?ARG-1) " +
      "   (#$arg2Isa " + predicate.cyclify() + " ?ARG-2) " +
      "   (#$unknownSentence " +
      "       (#$thereExists ?COL-1 " +
      "           (#$thereExists ?XMLTYPE-1 " +
      "               (#$and " +
      "                 (#$owlDataTypeSemanticCorrelation ?XMLTYPE-1 ?COL-1) " +
      "                 (#$genls ?ARG-1 ?COL-1))))) " +
      "   (#$unknownSentence " +
      "       (#$thereExists ?COL-2 " +
      "           (#$thereExists ?XMLTYPE-2 " +
      "               (#$and " +
      "                   (#$owlDataTypeSemanticCorrelation ?XMLTYPE-2 ?COL-2) " +
      "                   (#$genls ?ARG-2 ?COL-2)))))) ";
    if (cycAccess.isQueryTrue(cycAccess.makeCycList(query), inferenceMt, (HashMap) null))
      return true;
    query =
      "(#$and " +
      "  (#$isa " + predicate.cyclify() + " #$VariableArityPredicate) " +
      "  (#$argsIsa " + predicate.cyclify() + " ?ARG) " +
      "  (#$unknownSentence " +
      "      (#$thereExists ?COL " +
      "          (#$thereExists ?XMLTYPE " +
      "              (#$and " +
      "                  (#$owlDataTypeSemanticCorrelation ?XMLTYPE ?COL) " +
      "                  (#$genls ?ARG ?COL)))))) ";   
    return cycAccess.isQueryTrue(cycAccess.makeCycList(query), inferenceMt, (HashMap) null);
  }
  
  /** Returns the object datatype if the given predicate is an owl:DatatypeProperty, otherwise returns null
   *
   * @param predicate the given predicate
   * @return the object datatype if the given predicate is an owl:DatatypeProperty, otherwise returns null
   */
  private String getDatatypePropertyDataype(final CycObject predicate) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    
    try {
      final CycVariable arg2Variable = CycObjectFactory.makeCycVariable("?ARG-2");
      final CycVariable xmlTypeVariable = CycObjectFactory.makeCycVariable("?XMLTYPE-2");
      final CycList variables = new CycList();
      variables.add(arg2Variable);
      variables.add(xmlTypeVariable);
      final String queryString =
        "(#$ist #$OWLMappingMt " +
        "  (#$and " +
        "    (#$isa " + predicate.cyclify() + " #$BinaryPredicate) " +
        "    (#$arg2Isa " + predicate.cyclify() + " ?ARG-2) " +
        "    (#$thereExists ?COL-2 " +
        "      (#$and " +
        "        (#$owlDataTypeSemanticCorrelation ?XMLTYPE-2 ?COL-2) " +
        "        (#$genls ?ARG-2 ?COL-2)))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
      logger.finest("    owlDataTypeSemanticCorrelation bindings: " + bindings);
      if (bindings.isEmpty())
        return null;
      final CycList binding = (CycList) bindings.first();
      final CycObject xsdTerm = (CycObject) binding.second(); 
      return translateTermToXMLSchemaDatatype(xsdTerm);
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /** Returns the translated XML Schema datatype for the given CycL term that represents it, or null if none found.
   *
   * @param xsdTerm the CycL term that represents an XML Schema datatype
   * @return the translated XML Schema datatype for the given CycL term that represents it, or null if none found
   */
  private String translateTermToXMLSchemaDatatype(final CycObject xsdTerm) {
    //// Preconditions
    assert xsdTerm != null : "term must not be null";
    
    final String xsdType = xsdTerm.toString();
    if (xsdType.equals("xsd:uriReference"))
      return xsdUriReference;
    else if (xsdType.equals("xsd:decimal"))
      return xsdDecimal;
    else if (xsdType.equals("xsd:float"))
      return xsdDouble;
    else if (xsdType.equals("xsd:date"))
      return xsdDateTime;
    else if (xsdType.equals("xsd:nonNegativeInteger"))
      return xsdNonNegativeInteger;
    else if (xsdType.equals("xsd:integer"))
      return xsdInteger;
    else if (xsdType.equals("xsd:string"))
      return xsdString;
    else
      return null;
  }
  
  /** Examines the argument contraint of the given predicate with respect to the given non-CycObject thing,
   * and returns a new individual if the argument constraint is not mapped to an XML datatype.
   *
   * @param predicate the restriction property
   * @param isInverseProperty the indicator for whether the property is expressed as an inverse property
   * @param thing the restriction owl:hasValue object
   * 
   * @return a new individual if the argument constraint is not mapped to an XML datatype, otherwise
   * return the given thing
   */
  private Object possiblyCreateIndividual(final CycObject predicate,
                                          final boolean isInverseProperty,
                                          final Object thing) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    assert thing != null : "thing must not be null";

    CycObject argConstraint = null;
    if (isInverseProperty) {
      populateArg1Isas(predicate);
      if (! arg1Isas.isEmpty())
        argConstraint = (CycObject) arg1Isas.first();
    }
    else {
      populateArg2Isas(predicate);
      if (! arg2Isas.isEmpty())
        argConstraint = (CycObject) arg2Isas.first();
    }
    if (argConstraint == null)
      return thing;
    final String translatedTerm = (String) xsdSchemaMappingDictionary.get(argConstraint);
    if (translatedTerm != null)
      return thing;
    final CycNart individualThing = 
      new CycNart(cycAccess.getKnownConstantByName("InstanceNamedFn"),
                  thing.toString(),
                  argConstraint);
    if (! owlSelectedIndividuals.contains(individualThing)) {
      owlSelectedIndividuals.add(individualThing);
      createdIndividualValues.put(individualThing, thing);
      logger.fine("    created individual: " + individualThing.toString());
      return individualThing;
    }
    else
      return thing;
  }
  
  /** Returns the object datatype if the given predicate is an inverse owl:DatatypeProperty, otherwise returns null
   *
   * @param predicate the given predicate
   * @return the object datatype if the given predicate is an inverse owl:DatatypeProperty, otherwise returns null
   */
  private String getInverseDatatypePropertyDatatype(final CycObject predicate) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    
    try {
      final CycVariable arg2Variable = CycObjectFactory.makeCycVariable("?ARG-1");
      final CycVariable xmlTypeVariable = CycObjectFactory.makeCycVariable("?XMLTYPE-2");
      final CycList variables = new CycList();
      variables.add(arg2Variable);
      variables.add(xmlTypeVariable);
      final String queryString =
        "(#$ist #$OWLMappingMt " +
        "  (#$and " +
        "    (#$isa " + predicate.cyclify() + " #$BinaryPredicate) " +
        "    (#$arg1Isa " + predicate.cyclify() + " ?ARG-1) " +
        "    (#$arg2Isa " + predicate.cyclify() + " ?ARG-2) " +
        "    (#$owlDataTypeSemanticCorrelation ?XMLTYPE ?COL-1)" +
        "    (#$genls ?ARG-1 ?COL-1)" +
        "    (#$unknownSentence " +
        "      (#$thereExists ?COL-2 " +
        "        (#$thereExists ?XMLTYPE-2 " +
        "          (#$and " +
        "            (#$owlDataTypeSemanticCorrelation ?XMLTYPE-2 ?COL-2) " +
        "            (#$genls ?ARG-2 ?COL-2)))))))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(variables, query, inferenceMt, (HashMap) null);
      logger.finest("    inverse owlDataTypeSemanticCorrelation bindings: " + bindings);
      if (bindings.isEmpty())
        return null;
      final CycList binding = (CycList) bindings.first();
      final CycObject xsdTerm = (CycObject) binding.second(); 
      return translateTermToXMLSchemaDatatype(xsdTerm);
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /** Returns true if the given predicate is an inverse owl:FunctionalProperty.
   *
   * @param predicate the given predicate
   * @return true if the given predicate is an inverse owl:FunctionalProperty
   */
  private boolean isFunctionalProperty(final CycObject predicate) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    
    final String query =
      "(#$or " +
      "  (#$isa " + predicate.cyclify() + " #$StrictlyFunctionalSlot) " +
      "  (#$strictlyFunctionalInArgs " + predicate.cyclify() + " 2) " +
      "  (#$argCardinality " + predicate.cyclify() + " 2 1))";
    final boolean result = cycAccess.isQueryTrue(cycAccess.makeCycList(query), inferenceMt,  (HashMap) null);
    return result;
  }
  
  /** Returns true if the given predicate is an inverse owl:InverseFunctionalProperty.
   *
   * @param predicate the given predicate
   * @return true if the given predicate is an inverse owl:InverseFunctionalProperty
   */
  private boolean isInverseFunctionalProperty(final CycObject predicate) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate must not be null";
    
    final String query =
      "(#$or " +
      "  (#$strictlyFunctionalInArgs " + predicate.cyclify() + " 1) " +
      "  (#$argCardinality " + predicate.cyclify() + " 1 1))";
    final boolean result = cycAccess.isQueryTrue(cycAccess.makeCycList(query), inferenceMt,  (HashMap) null);
    return result;
  }
  
  /** Returns true if the given collection is completely asserted.
   *
   * @param collection the given collection
   * @return true if the given collection is completely asserted
   */
  private boolean isCompletelyAssertedCollection(final CycObject collection) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert collection != null : "collection must not be null";
    
    final String query =
      "(#$completelyAssertedCollection " + collection.cyclify() + ")";
    final boolean result = cycAccess.isQueryTrue(cycAccess.makeCycList(query), inferenceMt,  (HashMap) null);
    return result;
  }
  
  /** Returns an XML compliant name for the given term.
   *
   * @param cycConstant the given term
   * @return an XML compliant name for the given term
   */
  private String xmlName(final CycConstant cycConstant) {
    //// Preconditions
    assert cycConstant != null : "cycConstant must not be null";
    
    String xmlName = cycConstant.toString();
    if (Character.isDigit(xmlName.charAt(0)))
      xmlName = "N_" + xmlName;
    
    //// Preconditions
    assert xmlName != null : "xmlName must not be null";
    assert xmlName.length() > 0 : "xmlName must not be an empty string";
    
    return xmlName;
  }
  
  /** Adds a new inverse predicate to the export term set.
   *
   * @param inversePredicate the inverse predicate
   */
  private void addInversePredicate(final CycFort inversePredicate) {
    //// Preconditions
    assert inversePredicate != null : "inversePredicate must not be null";
    assert inversePredicates != null : "inversePredicates must not be null";
    assert selectedCycForts != null : "selectedCycForts must not be null";
    
    if (selectedCycForts.contains(inversePredicate) || inversePredicates.contains(inversePredicate))
      return;
    logger.fine("    creating " + inversePredicate.toString());
    inversePredicates.add(inversePredicate);
  }
  
  /** Displays a list of the exported terms. */
  private void displayExportedTerms() {
    //// Preconditions
    assert percentProgressDictionary != null : "percentProgressDictionary must not be null";
    assert owlSelectedClasses != null : "owlSelectedClasses must not be null";
    assert owlSelectedProperties != null : "owlSelectedProperties must not be null";
    assert inversePredicates != null : "inversePredicates must not be null";
    assert owlSelectedIndividuals != null : "owlSelectedIndividuals must not be null";
    
    for (int i = 0; i < owlSelectedClasses.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedClasses.get(i);
      logger.info(cycObject.cyclify());
    }
    for (int i = 0; i < owlSelectedProperties.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedProperties.get(i);
      logger.info(cycObject.cyclify());
    }
    for (int i = 0; i < inversePredicates.size(); i++) {
      final CycObject cycObject = (CycObject) inversePredicates.get(i);
      logger.info(cycObject.cyclify());
    }
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedIndividuals.get(i);
      logger.info(cycObject.cyclify());
    }
  }
  
  /** Populates the OWL export percent progress dictionary, which during term export provides the
   * estimated percent progress at the given term.  Takes into account the expected differning durations of exporting
   * classes vs. properties vs. individuals. */
  private void populatePercentProgressDictionary() {
    //// Preconditions
    assert percentProgressDictionary != null : "percentProgressDictionary must not be null";
    assert owlSelectedClasses != null : "owlSelectedClasses must not be null";
    assert owlSelectedProperties != null : "owlSelectedProperties must not be null";
    assert owlSelectedIndividuals != null : "owlSelectedIndividuals must not be null";
    
    // the weight factors were obtained as the average millisecond duration of a term type on a certain test computer
    final double SELECTED_CLASSES_WEIGHT_FACTOR = 14337.0d;
    final double SELECTED_PROPERTIES_WEIGHT_FACTOR = 789.0d;
    final double SELECTED_INDIVIDUALS_WEIGHT_FACTOR = 216.0d;
    
    final double PERCENT_PROGRESS_RESERVED_FOR_VALIDATION = 2.0d;
    final double selectedClassesWeight = SELECTED_CLASSES_WEIGHT_FACTOR * (double) owlSelectedClasses.size();
    final double selectedPropertiesWeight = SELECTED_PROPERTIES_WEIGHT_FACTOR * (double) owlSelectedProperties.size();
    final double selectedIndividualsWeight = SELECTED_INDIVIDUALS_WEIGHT_FACTOR * (double) owlSelectedIndividuals.size();
    final double totalWeight = selectedClassesWeight + selectedPropertiesWeight + selectedIndividualsWeight;
    final double percentProgressAvailableForTerms = 100.0d - percentProgress - PERCENT_PROGRESS_RESERVED_FOR_VALIDATION;
    assert percentProgressAvailableForTerms > 0.0d : "percentProgressAvailableForTerms (" + percentProgressAvailableForTerms + ") must be positive";
    double percentProgressForTerm = percentProgress;
    final double percentProgressAvailableForClasses = percentProgressAvailableForTerms * selectedClassesWeight / totalWeight;
    final double percentProgressIncrementForClasses = percentProgressAvailableForClasses / (double) owlSelectedClasses.size();
    sortCycObjects(owlSelectedClasses);
    for (int i = 0; i < owlSelectedClasses.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedClasses.get(i);
      percentProgressDictionary.put(cycObject, new Double(percentProgressForTerm));
      percentProgressForTerm += percentProgressIncrementForClasses;
      logger.finest(cycObject.cyclify() + " " + percentProgressForTerm);
    }
    final double percentProgressAvailableForProperties = percentProgressAvailableForTerms * selectedPropertiesWeight / totalWeight;
    final double percentProgressIncrementForProperties = percentProgressAvailableForProperties / (double) owlSelectedProperties.size();
    sortCycObjects(owlSelectedProperties);
    for (int i = 0; i < owlSelectedProperties.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedProperties.get(i);
      percentProgressDictionary.put(cycObject, new Double(percentProgressForTerm));
      percentProgressForTerm += percentProgressIncrementForProperties;
      logger.finest(cycObject.cyclify() + " " + percentProgressForTerm);
    }
    final double percentProgressAvailableForIndividuals = percentProgressAvailableForTerms * selectedIndividualsWeight / totalWeight;
    final double percentProgressIncrementForIndividuals = percentProgressAvailableForIndividuals / (double) owlSelectedIndividuals.size();
    sortCycObjects(owlSelectedIndividuals);
    for (int i = 0; i < owlSelectedIndividuals.size(); i++) {
      final CycObject cycObject = (CycObject) owlSelectedIndividuals.get(i);
      percentProgressDictionary.put(cycObject, new Double(percentProgressForTerm));
      percentProgressForTerm += percentProgressIncrementForIndividuals;
      logger.finest(cycObject.cyclify() + " " + percentProgressForTerm);
    }
  }
  
  /** Initializes the named problem store used to speed up the longer duration queries. */
  private void initializeProblemStores() throws UnknownHostException, IOException, CycApiException {
    cycAccess.initializeNamedInferenceProblemStore(DISJOINT_WITH_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_ISA1_2_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_COND_ISA1_2_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_ISA2_1_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_COND_ISA2_1_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_EXISTS_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_INSTANCE_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_EXISTS_COUNT_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_EXISTS_MAX_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_EXISTS_MIN_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_EXISTS_COUNT_ALL_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_EXISTS_MAX_ALL_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_EXISTS_MIN_ALL_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_EXISTS_ALL_MANY_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(ARG_CARDINALITY_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_CARDINALITY_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(INTER_ARG_FORMATS_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ALL_ONLY_PROBLEM_STORE_NAME, null);
    cycAccess.initializeNamedInferenceProblemStore(RELATION_ONLY_ALL_PROBLEM_STORE_NAME, null);
  }
  
  /** Destroys the named problem store used to speed up the longer duration queries. */
  private void destroyProblemStores() throws UnknownHostException, IOException, CycApiException {
    cycAccess.destroyInferenceProblemStoreByName(DISJOINT_WITH_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_ISA1_2_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_COND_ISA1_2_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_ISA2_1_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_COND_ISA2_1_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_EXISTS_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_INSTANCE_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_EXISTS_COUNT_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_EXISTS_MAX_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_EXISTS_MIN_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_EXISTS_COUNT_ALL_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_EXISTS_MAX_ALL_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_EXISTS_MIN_ALL_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_EXISTS_ALL_MANY_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(ARG_CARDINALITY_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_CARDINALITY_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(INTER_ARG_FORMATS_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ALL_ONLY_PROBLEM_STORE_NAME);
    cycAccess.destroyInferenceProblemStoreByName(RELATION_ONLY_ALL_PROBLEM_STORE_NAME);
  }
  
  /** Initializes the CycL term to OWL term mapping dictionary. */
  private void initializeMappingDictionary() throws UnknownHostException, IOException, CycApiException {
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("Thing"), owlThing);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("Nothing"), owlNothing);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("Ontology"), owlOntology);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("Collection"), owlClass);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OWLDeprecatedClass"), owlDeprecatedClass);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("BinaryPredicate"), rdfProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("TransitiveBinaryPredicate"), owlTransitiveProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("SymmetricBinaryPredicate"), owlSymmetricProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OWLObjectProperty"), owlObjectProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OWLDatatypeProperty"), owlDatatypeProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OWLDeprecatedProperty"), owlDeprecatedProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OntologyRelatingPredicate"), owlOntologyProperty);
    owlClassMappingDictionary.put(cycAccess.getKnownConstantByName("OntologyAnnotationPredicate"), owlAnnotationProperty);
    
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("SubLAtomicTerm"), rdfsLiteral);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("isa"), rdfType);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("genls"), rdfsSubClassOf);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("genlPreds"), rdfsSubPropertyOf);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("comment"), rdfsComment);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("disjointWith"), owlDisjointWith);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("different"), owlDifferentFrom);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("equals"), owlSameAs);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("arg1Isa"), rdfsDomain);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("arg2Isa"), rdfsRange);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("ontologyVersionInfo"), owlVersionInfo);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("ontologyPriorVersion"), owlPriorVersion);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("incompatibleOntology"), owlIncompatibleWith);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("owlDifferentFrom"), owlDifferentFrom);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("backwardsCompatibleOntology"), owlBackwardCompatibleWith);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("inverseBinaryPredicateOf"), owlInverseOf);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("coExtensional"), owlEquivalentClass);
    owlPropertyMappingDictionary.put(cycAccess.getKnownConstantByName("owlRestrictionAllValuesFrom"), owlAllValuesFrom);
    
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("UniformResourceIdentifier"), xsdUriReference);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("UniformResourceLocator"), xsdUriReference);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("Date"), xsdDate);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("SubLRealNumber"), xsdDouble);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("SubLString"), xsdString);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("SubLInteger"), xsdInteger);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("PositiveInteger"), xsdPositiveInteger);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("Integer"), xsdInteger);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("NonNegativeInteger"), xsdNonNegativeInteger);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("SubLBoolean"), xsdBoolean);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("CalendarSecond"), xsdDateTime);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("CalendarDay"), xsdDate);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("CalendarMonth"), xsdGYearMonth);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("CalendarYear"), xsdGYear);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("DayOfMonthType"), xsdGDay);
    xsdSchemaMappingDictionary.put(cycAccess.getKnownConstantByName("GregorianMonthType"), xsdGMonth);
  }
  
  /** Contains information about an Individual node. */
  private class IndividualNodeInfo {
    final String isaString;
    final String owlNameString;
    
    IndividualNodeInfo(final String isaString, final String owlNameString) {
      //// Preconditions
      assert isaString != null : "isaString must not be null";
      assert isaString.length() > 0 : "isaString must not be an empty string";
      assert owlNameString != null : "owlNameString must not be null";
      assert owlNameString.length() > 0 : "owlNameString must not be an empty string";
      
      this.isaString = isaString;
      this.owlNameString = owlNameString;
    }
  }
  
  
  /** Adds the given restriction to the list of restriction objects, subsuming them by cardinality.
   *
   * @param restriction the given restriction to add
   */
  private void addRestriction(final Restriction restriction) {
    //// Preconditions
    assert restriction != null : "restriction must not be null";
    assert restrictions != null : "restrictions must not be null";
   
    if (restrictions.contains(restriction)) {
      logger.fine("      ignored dup   " + restriction.toString());
      return;
    }
    
    // sort out the existing matching restrictions
    Restriction cardinalityRestriction = null;
    Restriction maxCardinalityRestriction = null;
    Restriction minCardinalityRestriction = null;
    Restriction noCardinalityRestriction = null;
    for (int i = 0; i < restrictions.size(); i++) {
      final Restriction currentRestriction = (Restriction) restrictions.get(i);
      if (currentRestriction.matches(restriction)) {
        if (currentRestriction.cardinality > -1)
          cardinalityRestriction = currentRestriction;
        else if (currentRestriction.maxCardinality > -1)
          maxCardinalityRestriction = currentRestriction;
        else if (currentRestriction.minCardinality > -1)
          minCardinalityRestriction = currentRestriction;
        else 
          noCardinalityRestriction = currentRestriction;
      }
    }
    if (cardinalityRestriction != null) {
      // existing fixed cardinality trumps any new cardinality restriction
      logger.fine("      ignored       " + restriction.toString());
      return;
    }
    if (restriction.cardinality > -1) {
      // fixed cardinality trumps any previous max or min
      removeMatchingRestrictions(restriction);
      restrictions.add(restriction);
      logger.fine("      added new     " + restriction.toString());
      return;
    }
    if (restriction.maxCardinality > -1) {
      if (cardinalityRestriction != null || 
         (maxCardinalityRestriction != null && 
          maxCardinalityRestriction.maxCardinality >= restriction.maxCardinality)) {
        logger.fine("      ignored       " + restriction.toString());
        return;
      }
      if (maxCardinalityRestriction != null) {
        // max cardinality trumps any lower max 
        restrictions.remove(maxCardinalityRestriction);
        logger.fine("      removed       " + maxCardinalityRestriction.toString());
      }
      restrictions.add(restriction);
      logger.fine("      added new     " + restriction.toString());
      return;
    }
    if (restriction.minCardinality > -1) {
      if (cardinalityRestriction != null || 
         (minCardinalityRestriction != null && 
          minCardinalityRestriction.minCardinality <= restriction.minCardinality)) {
        logger.fine("      ignored       " + restriction.toString());
        return;
      }
      if (minCardinalityRestriction != null) {
        // min cardinality trumps any higher min
        restrictions.remove(minCardinalityRestriction);
        logger.fine("      removed       " + minCardinalityRestriction.toString());
      }
      restrictions.add(restriction);
      logger.fine("      added new     " + restriction.toString());
      return;
    }
    if (cardinalityRestriction == null &&
        maxCardinalityRestriction == null &&
        minCardinalityRestriction == null &&
        noCardinalityRestriction == null) {
      restrictions.add(restriction);
      logger.fine("      added new     " + restriction.toString());
    }
    else 
      logger.fine("      ignored redundant " + restriction.toString());
  }
  
  /** Remove any restrictions matching the given restriction
   *
   * @param restriction the given restriction
   */
  private void removeMatchingRestrictions(final Restriction restriction) {
    //// Preconditions
    assert restriction != null : "restriction must not be null";
    assert restrictions != null : "restrictions must not be null";
   
    final Iterator restrictions_iter = restrictions.iterator();
    while (restrictions_iter.hasNext()) {
      final Restriction existingRestriction = (Restriction) restrictions_iter.next();
      if (existingRestriction.matches(restriction)) {
        restrictions_iter.remove();
        logger.fine("      removed       " + existingRestriction.toString());
      }
    }
  }
    
  /** the subject is an instance of the restriction */
  private final int TYPE_RESTRICTION = 0;
  
  /** the subject is a spec of the restriction */
  private final int SUBCLASS_RESTRICTION = 1;
  
  /** Contains information about a restriction. */
  private class Restriction implements Comparable {
    
    /** the restriction type */
    final int restrictionType;
    
    /** the restriction property */
    final CycObject property;
    
    /** the indicator for whether the property is expressed as an inverse property */
    final boolean isInverseProperty;
    
    /** the restriction owl:hasValue, or null if not present */
    final Object hasValue;
    
    /** the restriction owl:valuesFrom, or null if not present */
    final CycObject valuesFrom;
    
    /** the restriction owl:allValuesFrom, or null if not present */
    final CycObject allValuesFrom;
    
    /** the restriction owl:someValuesFrom, or null if not present */
    final CycObject someValuesFrom;
    
    /** the restriction cardinality, or -1 if not used */
    final int cardinality;
    
    /** the restriction maximum cardinality, or -1 if not used */
    final int maxCardinality;
    
    /** the restriction minimum cardinality, or -1 if not used */
    final int minCardinality;
    
    /** the source rule macro predicate as a string for debugging */
    final String sourceRuleMacroPredicate;
    
    
    /** Constructs a new Restriction object.
     *
     * @param restrictionType the restriction type
     * @param property the restriction property
     * @param isInverseProperty the indicator for whether the property is expressed as an inverse property
     * @param hasValue the restriction owl:hasValue, or null if not present
     * @param allValuesFrom the restriction owl:allValuesFrom, or null if not present
     * @param valuesFrom the restriction owl:valuesFrom, or null if not present
     * @param someValuesFrom the restriction owl:someValuesFrom, or null if not present
     * @param cardinality the restriction cardinality, or -1 if not used
     * @param maxCardinality the restriction maximum cardinality, or -1 if not used
     * @param minCardinality the restriction minimum cardinality, or -1 if not used
     * @param sourceRuleMacroPredicate the distinguishing CycL source rule macro predicate for debugging, e.g. relationAllExists
     */
    Restriction(final int restrictionType,
                final CycObject property,
                final boolean isInverseProperty,
                final Object hasValue,
                final CycObject valuesFrom,
                final CycObject allValuesFrom,
                final CycObject someValuesFrom,
                final int cardinality,
                final int maxCardinality,
                final int minCardinality,
                final String sourceRuleMacroPredicate) {
      
      //// Preconditions
      assert restrictionType == TYPE_RESTRICTION || restrictionType == SUBCLASS_RESTRICTION :
        "restrictionType must be TYPE_RESTRICTION or SUBCLASS_RESTRICTION";
      assert property != null : "property must not be null";
      assert sourceRuleMacroPredicate != null : "sourceRuleMacroPredicate must not be null";
      assert sourceRuleMacroPredicate.length() > 0 : "sourceRuleMacroPredicate must not be an empty string";

      this.restrictionType = restrictionType;
      this.property = property;
      this.isInverseProperty = isInverseProperty;
      this.hasValue = hasValue;
      this.valuesFrom = valuesFrom;
      this.allValuesFrom = allValuesFrom;
      this.someValuesFrom = someValuesFrom;
      this.cardinality = cardinality;
      this.maxCardinality = maxCardinality;
      this.minCardinality = minCardinality;
      this.sourceRuleMacroPredicate = sourceRuleMacroPredicate;
    }
    
    /** Returns a string representation of this object. 
     *
     * @return a string representation of this object
     */
    public String toString() {
      final StringBuffer stringBuffer = new StringBuffer(300);
      stringBuffer.append("[Restriction: ");
      if (isInverseProperty)
        stringBuffer.append(" inverse property ");
      stringBuffer.append(property.toString());
      if (hasValue != null) {
        stringBuffer.append(" hasValue: ");
        stringBuffer.append(hasValue.toString());
      }
      if (valuesFrom != null) {
        stringBuffer.append(" valuesFrom: ");
        stringBuffer.append(valuesFrom.toString());
      }
      if (allValuesFrom != null) {
        stringBuffer.append(" allValuesFrom: ");
        stringBuffer.append(allValuesFrom.toString());
      }
      if (someValuesFrom != null) {
        stringBuffer.append(" someValuesFrom: ");
        stringBuffer.append(someValuesFrom.toString());
      }
      if (cardinality > -1) {
        stringBuffer.append(" cardinality: ");
        stringBuffer.append(cardinality);
      }
      if (maxCardinality > -1) {
        stringBuffer.append(" maxCardinality: ");
        stringBuffer.append(maxCardinality);
      }
      if (minCardinality > -1) {
        stringBuffer.append(" minCardinality: ");
        stringBuffer.append(minCardinality);
      }
      stringBuffer.append(" source: ");
      stringBuffer.append(sourceRuleMacroPredicate.toString());
      stringBuffer.append(']');
      return stringBuffer.toString();
    }
    
    /** Compares the given object to this object, returning -1 if less than, 0 if equal, and +1 if greater than.
     *
     * @param  obj the comparison object
     * @return -1 if the this object is less than the comparison object, 0 if equal, and +1 if greater than
     */
    public int compareTo(Object obj) {
      assert obj instanceof Restriction : "obj (" + obj.getClass().getName() + ") must be a Restriction";
      
      final Restriction that = (Restriction) obj;
      if (this.restrictionType == TYPE_RESTRICTION && that.restrictionType == SUBCLASS_RESTRICTION)
        return -1;
      else if (this.restrictionType == SUBCLASS_RESTRICTION && that.restrictionType == TYPE_RESTRICTION)
        return 1;
      
      final int propertyComparison = property.compareTo(that.property);
      if (propertyComparison != 0)
        return propertyComparison;
      
      if (! this.isInverseProperty && that.isInverseProperty)
        return -1;
      else if (this.isInverseProperty && ! that.isInverseProperty)
        return 1;
      
      if (this.hasValue != null && that.hasValue == null)
        return -1;
      else if (this.hasValue == null && that.hasValue != null)
        return 1;
      
      if (this.valuesFrom != null && that.valuesFrom == null)
        return -1;
      else if (this.valuesFrom == null && that.valuesFrom != null)
        return 1;
      else if (this.valuesFrom != null && that.valuesFrom != null)
        return this.valuesFrom.compareTo(that.valuesFrom);
      
      if (this.allValuesFrom != null && that.allValuesFrom == null)
        return -1;
      else if (this.allValuesFrom == null && that.allValuesFrom != null)
        return 1;
      else if (this.allValuesFrom != null && that.allValuesFrom != null)
        return this.allValuesFrom.compareTo(that.allValuesFrom);
      
      if (this.someValuesFrom != null && that.someValuesFrom == null)
        return -1;
      else if (this.someValuesFrom == null && that.someValuesFrom != null)
        return 1;
      else if (this.someValuesFrom != null && that.someValuesFrom != null)
        return this.someValuesFrom.compareTo(that.someValuesFrom);
      
      return 0;
    }
    
    /** Returns true if the given restriction object matches this one.
     *
     * @param the given object
     * @return true if the given object is equal to this one
     */
    public boolean matches(final Restriction that) {
      assert that != null : "that must not be null";
      
      if (this.restrictionType != that.restrictionType ||
          (! this.property.equals(that.property)) ||
          this.isInverseProperty != that.isInverseProperty)
        return false;
      
      if ((this.hasValue == null && that.hasValue != null) ||
          (this.hasValue != null && that.hasValue == null))
        return false;
      if (this.hasValue != null && ! this.hasValue.equals(that.hasValue))
        return false;
      
      if ((this.valuesFrom == null && that.valuesFrom != null) ||
          (this.valuesFrom != null && that.valuesFrom == null))
        return false;
      if (this.valuesFrom != null && ! this.valuesFrom.equals(that.valuesFrom))
        return false;
      
      if ((this.allValuesFrom == null && that.allValuesFrom != null) ||
          (this.allValuesFrom != null && that.allValuesFrom == null))
        return false;
      if (this.allValuesFrom != null && ! this.allValuesFrom.equals(that.allValuesFrom))
        return false;
      
      if ((this.someValuesFrom == null && that.someValuesFrom != null) ||
          (this.someValuesFrom != null && that.someValuesFrom == null))
        return false;
      if (this.someValuesFrom != null && ! this.someValuesFrom.equals(that.someValuesFrom))
        return false;
      
      return true;
    }
    
    /** Returns true if the given object is equal to this one.
     *
     * @param the given object
     * @return true if the given object is equal to this one
     */
    public boolean equals(Object obj) {
      assert obj != null : "obj must not be null";
      assert obj instanceof Restriction : "obj (" + obj.getClass().getName() + ") must be a Restriction";
      
      final Restriction that = (Restriction) obj;
      if (! this.matches(that))
        return false;
      else return
        this.cardinality == that.cardinality &&
        this.maxCardinality == that.maxCardinality &&
        this.minCardinality == that.minCardinality;
    }
  }
  
  //// Internal Rep
  
  private static final String cycOwlNamespace = "http://www.cyc.com/2004/06/04/cyc#";
  private static final String cycOwlXMLBase = "http://www.cyc.com/2004/06/04/cyc";
  private static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";
  private static final String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
  private static final String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  private static final String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
  private static final String rdfType = rdfNamespace + "type";
  private static final String rdfsLiteral = rdfsNamespace + "Literal";
  private static final String rdfsSubClassOf = rdfsNamespace + "subClassOf";
  private static final String rdfsSubPropertyOf = rdfsNamespace + "subPropertyOf";
  private static final String rdfProperty = rdfNamespace + "Property";
  private static final String rdfsComment = rdfsNamespace + "comment";
  private static final String rdfsDomain = rdfsNamespace + "domain";
  private static final String rdfsRange = rdfsNamespace + "range";
  private static final String owlNamespace = "http://www.w3.org/2002/07/owl#";
  private static final String swrlNamespace = "http://www.w3.org/2003/11/swrl#";
  private static final String rulemlNamespace = "http://www.w3.org/2003/11/swrl#";
  private static final String owlThing = owlNamespace + "Thing";
  private static final String owlNothing = owlNamespace + "Nothing";
  private static final String owlOntology = owlNamespace + "Ontology";
  private static final String owlClass = owlNamespace + "Class";
  private static final String owlDeprecatedClass = owlNamespace + "DeprecatedClass";
  private static final String owlTransitiveProperty = owlNamespace + "TransitiveProperty";
  private static final String owlSymmetricProperty = owlNamespace + "SymmetricProperty";
  private static final String owlObjectProperty = owlNamespace + "ObjectProperty";
  private static final String owlDatatypeProperty = owlNamespace + "DatatypeProperty";
  private static final String owlDeprecatedProperty = owlNamespace + "DeprecatedProperty";
  private static final String owlOntologyProperty = owlNamespace + "OntologyProperty";
  private static final String owlAnnotationProperty = owlNamespace + "AnnotationProperty";
  private static final String owlDisjointWith = owlNamespace + "disjointWith";
  private static final String owlDifferentFrom = owlNamespace + "differentFrom";
  private static final String owlSameAs = owlNamespace + "sameAs";
  private static final String owlOntologyVersionInfo = owlNamespace + "versionInfo";
  private static final String owlPriorVersion = owlNamespace + "priorVersion";
  private static final String owlIncompatibleWith = owlNamespace + "incompatibeWith";
  private static final String owlBackwardCompatibleWith = owlNamespace + "backwardCompatibeWith";
  private static final String owlInverseOf = owlNamespace + "inverseOf";
  private static final String owlEquivalentClass = owlNamespace + "equivalentClass";
  private static final String owlAllValuesFrom = owlNamespace + "allValuesFrom";
  private static final String owlInverseFunctionalProperty = owlNamespace + "InverseFunctionalProperty";
  private static final String owlFunctionalProperty = owlNamespace + "FunctionalProperty";
  
  private static final String xsdUriReference = xsdNamespace + "uriReference";
  private static final String xsdDate = xsdNamespace + "date";
  private static final String xsdDecimal = xsdNamespace + "decimal";
  private static final String xsdDouble = xsdNamespace + "double";
  private static final String xsdString = xsdNamespace + "string";
  private static final String xsdInteger = xsdNamespace + "integer";
  private static final String xsdInt = xsdNamespace + "int";
  private static final String xsdNonNegativeInteger = xsdNamespace + "nonNegativeInteger";
  private static final String xsdPositiveInteger = xsdNamespace + "positiveInteger";
  private static final String xsdBoolean = xsdNamespace + "boolean";
  private static final String xsdDateTime = xsdNamespace + "dateTime";
  private static final String xsdGYearMonth = xsdNamespace + "gYearMonth";
  private static final String xsdGYear = xsdNamespace + "gYear";
  private static final String xsdGDay = xsdNamespace + "gDay";
  private static final String xsdGMonth = xsdNamespace + "gMonth";
  
  private static final String swrlArgument1 = swrlNamespace + "argument1";
  private static final String swrlArgument2 = swrlNamespace + "argument2";
  private static final String swrlClassAtom = swrlNamespace + "ClassAtom";
  private static final String swrlClassPredicate = swrlNamespace + "classPredicate";
  private static final String swrlDatavaluedPropertyAtom = swrlNamespace + "datavaluedPropertyAtom";
  private static final String swrlIndividualPropertyAtom = swrlNamespace + "individualPropertyAtom";
  private static final String swrlPropertyPredicate = swrlNamespace + "propertyPredicate";
  private static final String swrlVariable = swrlNamespace + "Variable";
  
  private static final String rulemlBody = rulemlNamespace + "body";
  private static final String rulemlHead = rulemlNamespace + "head";
  private static final String rulemlImp = rulemlNamespace + "Imp";
  private static final String rulemlVar = rulemlNamespace + "var";
  
  private static final String guidComment = "Permanent Global Unique ID for the associated concept.";
  private static final String externalIDComment = "Permanent ID string for the associated non-atomic concept.";
  private static final String nonAtomicFormulaComment = "This is the non-atomic concept expressed in CycL (Cyc's knowledge representation language).";
  private static final String literalValueComment = "Literal value of an individual which was created to contain a datatype value for a property whose domain or range is more general than the corresponding datatype value.";
  private final Document document = new DocumentImpl();
  private String documentUrl = null;
  private Element rdfNode = null;
  private Element owlOntologyNode = null;
  private Element owlVersionInfo = null;
  private Element rdfsCommentNode = null;
  private Guid guid;
  private String externalID;
  private final ArrayList owlSelectedConstants = new ArrayList();
  private String comment;
  private String label;
  private String nonAtomicFormula;
  private CycList isas;
  private CycList genls;
  private CycList genlPreds;
  private CycList arg1Isas;
  private CycList arg2Isas;
  private CycConstant arg1Format;
  private CycConstant arg2Format;
  private CycList disjointWiths;
  private CycList equals;
  private CycList coExtensionals;
  private List restrictions;
  private CycList interArgIsa1_2s;
  private CycList interArgIsa2_1s;
  private CycList relationAllOnlys;
  private CycList relationOnlyAlls;
  private CycList relationAllExists;
  private CycList relationExistsAlls;
  private CycList typeGenls;
  private CycList relationAllInstances;
  private CycList relationInstanceAlls;
  private CycList relationInstanceExists;
  private CycList relationExistsInstances;
  private CycList relationAllExistsCounts;
  private CycList relationAllExistMaxs;
  private CycList relationAllExistMins;
  private CycList relationExistsCountAlls;
  private CycList relationExistsMinAlls;
  private CycList relationExistsMaxAlls;
  //TODO replace relationExistsAllMany with relationExistsAll (its genlPred)
  private CycList relationExistsAllManys;
  private CycList arg2Cardinalities = new CycList(0);
  private CycList interArgCardinalities1_2 = new CycList(0);
  private CycList interArgCardinalities2_1 = new CycList(0);
  private CycList interArgFormats1_2 = new CycList(0);
  private CycList interArgFormats2_1 = new CycList(0);
  private CycList completelyAssertedCollectionInstances = new CycList(0);
  private CycList propertyAssertions;
  private CycList allKbSubsetCollections = new CycList(0);
  private final HashMap owlClassMappingDictionary = new HashMap();
  private final HashMap owlPropertyMappingDictionary = new HashMap();
  private final HashMap xsdSchemaMappingDictionary = new HashMap();
  private final CycList inversePredicates = new CycList();
  private HashMap nartXMLNames = new HashMap();
  private int xmlNameSequence = 1;
  
  /** the dictionary of term --> OWL defining DOM element */
  private final HashMap termElementDictionary = new HashMap();
  
  /** the dictionary of term --> list of isas */
  private final HashMap termIsasDictionary = new HashMap();
  
  /** the dictionary of term --> list of genls */
  private final HashMap termGenlsDictionary = new HashMap();
  
  /** the dictionary of individual --> IndividualNodeInfo */
  private final HashMap individualNodeInfoDictionary = new HashMap();
  
  /* the list of individuals that are the same as one or more other individuals */
  private final CycList nonDifferentIndividuals = new CycList();
  
  /** the list of registered listeners */
  private final List listeners = new ArrayList();
  
  /** the identifier that is returned with each progress event notification, allowing multiple OWL exports to
   * be managed by the caller */
  private final UUID uuid;
  
  /** the problem store name for the disjointWith query */
  private final static String DISJOINT_WITH_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgIsa1-2 query */
  private final static String INTER_ARG_ISA1_2_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgCondIsa1-2 query */
  private final static String INTER_ARG_COND_ISA1_2_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgIsa2-1 query */
  private final static String INTER_ARG_ISA2_1_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgCondIsa2-1 query */
  private final static String INTER_ARG_COND_ISA2_1_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllExists query */
  private final static String RELATION_ALL_EXISTS_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllInstance query */
  private final static String RELATION_ALL_INSTANCE_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationInstanceAll query */
  private final static String RELATION_INSTANCE_ALL_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllExistsCount query */
  private final static String RELATION_ALL_EXISTS_COUNT_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllExistsMax query */
  private final static String RELATION_ALL_EXISTS_MAX_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllExistsMin query */
  private final static String RELATION_ALL_EXISTS_MIN_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationExistsCountAll query */
  private final static String RELATION_ALL_EXISTS_COUNT_ALL_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationExistsMaxAll query */
  private final static String RELATION_EXISTS_MAX_ALL_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationExistsMinAll query */
  private final static String RELATION_EXISTS_MIN_ALL_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationExistsAllMany query */
  private final static String RELATION_EXISTS_ALL_MANY_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the argCardinality query */
  private final static String ARG_CARDINALITY_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgCardinality query */
  private final static String INTER_ARG_CARDINALITY_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the interArgFormats query */
  private final static String INTER_ARG_FORMATS_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationAllOnly query */
  private final static String RELATION_ALL_ONLY_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for the relationOnlyAll query */
  private final static String RELATION_ONLY_ALL_PROBLEM_STORE_NAME = UUID.randomUUID().toString(); 
  
  /** the problem store name for all queries on a given term */
  private String allQueriesProblemStoreName = null; 
  
  private static final boolean PROBLEM_STORE_REUSE = false;
  
  /** the dictionary of created individual values, term --> value object */
  private final HashMap createdIndividualValues = new HashMap();
  
  /** the indicator that the OWL export should be limited to OpenCyc content */
  private boolean isExportLimitedToOpenCycContent = false;
  
  /** the indicator that the OWL export should include rules in SWRL (Semantic Web Rule Language) */
  private boolean areRulesExported = false;
  
  /** the set of exported rules */
  private final Set rules = new HashSet();
  
  /** the license text */
  private String licenseText = null;
  
  //// Main
  
  /** Executes test methods for this class.
   *
   * @param args not used
   */
  public static void main(final String[] args) {
//    try {
//      final CycAccess cycAccess = new CycAccess("localhost", 3640);
//      final CycList terms = cycAccess.makeCycList("(#$TransportationDevice #$providerOfMotiveForce #$transporter #$vehicle)");
//      final ExportOwl exportOwl = new ExportOwl(cycAccess);
//      exportOwl.selectedCycForts = terms;
//      exportOwl.cycKbSubsetFilterGuid = null;
//      exportOwl.outputPath = "/home/reed/ontology.owl";
//      exportOwl.useResearchCycOntology = false;
//      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
//      exportOwl.setAreRulesExported(true);
//      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
//      cycAccess.close();
//      System.exit(0);
//    }
//    catch (Exception e) {
//      System.err.println(e.getMessage());
//      e.printStackTrace();
//      System.exit(1);
//    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$EventOrRoleConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/event-or-role-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$ClimateConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/climate-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$FamilyRelationsConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/family-relations-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$GeodesyConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/geodesy-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$IndustrialConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/industrial-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$MereotopologyConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/mereotopology-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$MilitaryFacilityConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/military-facility-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$MilitaryOrganizationConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/military-organization-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$TerrainConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/terrain-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$WaterBodyConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/water-body-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$VegetationConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/vegetation-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$BuildingConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/building-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$MilitaryFacilityConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/military-facility-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM #$UnitOfMeasureConcept)";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/unit-of-measure-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM (#$ClumpConceptTypeByDomainFn #$Addresses-Geography-CSC))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/addresses-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM (#$ClumpConceptTypeByDomainFn #$Connections-Spatial-CSC))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/connections-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM (#$ClumpConceptTypeByDomainFn #$DirectionAndOrientationVocabulary-Spatial-CSC))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/direction-and-orientation-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM (#$ClumpConceptTypeByDomainFn #$ProximityAndLocation-Spatial-CSC))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/promity-and-location-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    try {
      final CycAccess cycAccess = new CycAccess();
      final String queryString = "(#$conceptOfDomain ?TERM (#$ClumpConceptTypeByDomainFn #$SurfacesPortalsAndCavities-Spatial-CSC))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycVariable queryVariable = CycObjectFactory.makeCycVariable("?TERM");
      final CycObject mt = cycAccess.inferencePSC;
      final CycList terms = cycAccess.queryVariable(queryVariable, query, mt, null);
      final ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.selectedCycForts = terms;
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.areRulesExported = true;
      exportOwl.outputPath = "/home/reed/surfaces-portals-and-cavities-concept.owl";
      exportOwl.useResearchCycOntology = false;
      exportOwl.setInferenceMt(cycAccess.makeELMt("CurrentWorldDataCollectorMt-NonHomocentric"));
      exportOwl.export(ExportOwl.EXPORT_SELECTED_TERMS);
      cycAccess.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    
    System.exit(0);  // kill all threads
  }
}



