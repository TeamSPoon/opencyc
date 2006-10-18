/* $Id$
 *
 * Copyright (c) 2003 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

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
import org.opencyc.cycobject.CycVariable;
import org.opencyc.cycobject.ELMt;
import org.opencyc.cycobject.Guid;

//// External Imports
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * <P>OntologyExport is an abstract class designed to export ontologies from Cyc.
 *
 * <P>Copyright (c) 2003 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 * @date December 20, 2004, 10:38 AM
 * @version $Id$
 */
public abstract class OntologyExport {
  
  //// Constructors
  
  /**
   * Constructs a new OntologyExport object given the CycAccess object.
   *
   * @param cycAccess The CycAccess object which manages the api connection.
   */
  public OntologyExport(CycAccess cycAccess) throws CycApiException {
    CycConstant cycKBSubsetCollection = null;
    CycConstant researchCycConstant = null;
    CycConstant cycSecureFort = null;
    logger = Logger.getLogger("org.opencyc.xml.OntologyExport");
    try {
      this.cycAccess = cycAccess;
      logger.fine(cycAccess.getCycConnection().connectionInfo());
      cycKBSubsetCollection = cycAccess.getConstantByName("CycKBSubsetCollection");
      researchCycConstant = cycAccess.getConstantByGuid(researchCycConstantGuid);
      final Guid cycSecureFortGuid = new Guid("bf71b522-9c29-11b1-9dad-c379636f7270");
      cycSecureFort = cycAccess.getConstantByGuid(cycSecureFortGuid);
    } catch (IOException e) {
    }
    this.cycKBSubsetCollection = cycKBSubsetCollection;
    this.researchCycConstant = researchCycConstant;
    this.cycSecureFort = cycSecureFort;
    inferenceMt = cycAccess.inferencePSC;
  }
  
  
  //// Public Area
  
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
   * Command indicating that the OWL export contains all the terms in the KB.
   */
  public static final int EXPORT_ENTIRE_KB = 4;
  
  /**
   * Command indicating that the OWL export contains the selected terms.
   */
  public static final int EXPORT_SELECTED_TERMS = 5;
  
  /**
   * Command indicating that the OWL export is restricted to ResearchCyc definitional assertions.
   */
  public static final int EXPORT_RESEARCH_CYC = 6;
  
  /**
   * Command indicating that the OWL export contains the selected assertions.
   */
  public static final int EXPORT_SELECTED_ASSERTIONS = 7;
  
  /**
   * Upward closure filtering kb subset collections guids.  These constrain the selected
   * upward closure collection terms to be members of any of these kb subset
   * collections.
   */
  public ArrayList upwardClosureKbSubsetCollectionGuids = new ArrayList();
  
  /**
   * The CycKBSubsetCollection whose elements are exported.
   */
  public CycFort cycKbSubsetCollection = null;
  
  /**
   * The #$IKBConstant guid.
   */
  public static final Guid ikbConstantGuid = CycObjectFactory.makeGuid("bf90b3e2-9c29-11b1-9dad-c379636f7270");
  
  /**
   * The #$ResearchCycConstant guid.
   */
  public static final Guid researchCycConstantGuid = CycObjectFactory.makeGuid("66021322-a19c-41d7-884d-9182c08388b7");
  
  /**
   * The #$PublicConstant guid.
   */
  public static final Guid publicConstantGuid = CycObjectFactory.makeGuid("bd7abd90-9c29-11b1-9dad-c379636f7270");
  
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
   * #$rewriteOf guid
   */
  public static final Guid rewriteOfGuid = CycObjectFactory.makeGuid("c13bc0c4-9c29-11b1-9dad-c379636f7270");
  
  /**
   * #$equalSymbols guid
   */
  public static final Guid equalSymbolsGuid = CycObjectFactory.makeGuid("c05e110e-9c29-11b1-9dad-c379636f7270");
  
  /**
   * #$InferencePSC guid
   */
  public static final Guid inferencePSCGuid = CycObjectFactory.makeGuid("bd58915a-9c29-11b1-9dad-c379636f7270"); 
  
  /**
   * Indicates whether to export non-atomic terms as paraphrases, or omit non-atomic terms. 
   */
  public boolean includeNonAtomicTerms = true;
  
  /** 
   * Indicates whether to export non-atomic reified terms that are derived from the selected terms.
   */
  public boolean includeDerivedNarts = true;
  
  /**
   * Indicates whether to include the entire Research Cyc ontology for the exported class tags. 
   */
  public boolean useResearchCycOntology = false;
  
  /** the list of applicable binary predicates */
  public CycList applicableBinaryPredicates = new CycList();
  
  /** the list of assertions for export, used with EXPORT_SELECTED_ASSERTIONS */
  public CycList selectedAssertions = null;
  
  /** the selected Cyc FORTS, used with EXPORT_SELECTED_TERMS */
  public CycList selectedCycForts = null;

  /** the indicator for whether terms without comments are omitted from the ontology export */
  public boolean areTermsWithoutCommentsOmitted = false;

  /** the inference microtheory */
  public ELMt inferenceMt;
  
  /**
   * Upward closure filtering kb subset collections.  These constrain the selected
   * upward closure collection terms to be members of any of these kb subset
   * collections.
   */
  public  ArrayList upwardClosureKbSubsetCollections = new ArrayList();
  
  /* This class implements a comparator for CycObjects */
  public static class CycObjectComparator implements Comparator {
    
    /** Creates a new CycObjectComparator instance. */
    public CycObjectComparator () {
    }
    
    /** Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, 
     * equal to, or greater than the second.
     *
     * @param o1 the first term (CycConstant, CycNart or CycList)
     * @param o2 the first term (CycConstant, CycNart or CycList)
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    public int compare(Object o1, Object o2) {
      //// Preconditions
      assert o1 != null : "o1 cannot be null";
      assert o1 instanceof CycObject : "o1 must be a CycObject";
      assert o2 != null : "o2 cannot be null";
      assert o2 instanceof CycObject : "o2 must be a CycObject";
      
      final String string1 = o1.toString();
      final String string2 = o2.toString();
      return string1.compareTo(string2);
    }
  }
    
  /** Sets the indicator that the indicator that the upward closure of Cyc collection terms is automatically added to the set
   * of selected terms.
   *
   * @param includeUpwardClosure the indicator that the upward closure of Cyc collection terms is automatically added to the set
   * of selected terms
   */
  public void setIncludeUpwardClosure (final boolean includeUpwardClosure) {
    this.includeUpwardClosure = includeUpwardClosure;
  }
  
  /** Sets the list of selected Cyc forts for export. 
   *
   * @param selectedCycForts the list of selected Cyc forts for export 
   */
  public void setSelectedCycForts(final CycList selectedCycForts) {
    this.selectedCycForts = selectedCycForts;
  }
  
  /** Sets the indicator that the published Research Cyc ontology in to be used in reference to the exported ontology.
   *
   * @param useResearchCycOntology the indicator that the published Research Cyc ontology in to be used in reference to the exported ontology
   */
  public void useResearchCycOntology(final boolean useResearchCycOntology) {
    this.useResearchCycOntology = useResearchCycOntology;
  }
  
  /** Cancels this export. */
  public void cancel() {
    logger.info("Cancelling export");
    isCancelled = true;
  }
  
  /**
   * Gathers the updward closure of the selected CycForts with regard to isas and genls
   * for collection terms, and with regard to isas and genlPreds for predicate terms.
   *
   * @param selectedCycForts The selected CycForts.
   * @return The updward closure of the selected CycForts with regard to genls
   * for collection terms, and with regard to genlPreds for predicate terms.
   */
  final public CycList gatherUpwardClosure(final CycList selectedCycForts)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert selectedCycForts != null : "selectedCycForts cannot be null";
    
    final CycList upwardClosure = new CycList();
    if (selectedCycForts.isEmpty())
      return upwardClosure;
    final double PERCENT_PROGRESS_ALLOCATED_FOR_UPWARD_CLOSURE = 2.0d;
    final double percentProgressIncrement = PERCENT_PROGRESS_ALLOCATED_FOR_UPWARD_CLOSURE / (double) selectedCycForts.size();
    logger.info("Gathering upward closure");
    sortCycObjects(selectedCycForts);
    // Redundant HashSets for efficient contains() method below.
    final HashSet selectedCycFortsSet = new HashSet(selectedCycForts);
    final HashSet upwardClosureSet = new HashSet(selectedCycForts.size());
    for (int i = 0; i < selectedCycForts.size(); i++) {
      if (isCancelled)
        return upwardClosure;
      final CycObject cycObject = (CycObject) selectedCycForts.get(i);
      if (! (cycObject instanceof CycFort))
        continue;
      final CycFort cycFort = (CycFort) cycObject;
      considerForUpwardClosure(selectedCycFortsSet,
                               upwardClosure,
                               upwardClosureSet,
                               cycAccess.getAllIsa(cycFort, inferenceMt),
                               cycFort);
      if (cycAccess.isCollection(cycFort, inferenceMt))
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getAllGenls(cycFort, inferenceMt),
                                 cycFort);
      else if ((cycFort instanceof CycConstant) && (cycAccess.isPredicate(cycFort, inferenceMt))) {
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getAllGenlPreds((CycConstant) cycFort, inferenceMt),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg1Isas((CycConstant) cycFort, inferenceMt),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg2Isas((CycConstant) cycFort, inferenceMt),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg3Isas((CycConstant) cycFort, inferenceMt),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg4Isas((CycConstant) cycFort, inferenceMt),
                                 cycFort);
      }
      percentProgress += percentProgressIncrement;
      reportProgress(percentProgress);
    }
    //// Postconditions
    assert upwardClosure != null : "upwardClosure cannot be null";
    
    return  upwardClosure;
  }
  
  /** Sets the logger.
   *
   * @param logger the logger
   */
  public void setLogger(final Logger logger) {
    //// Preconditions
    if (logger == null)
      throw new NullPointerException("logger must not be null");
    
    this.logger = logger;
  }
  
  
  //// Protected Area
  
  /**
   * The command performed by the HTML extract process.
   */
  protected int exportCommand = 0;
  
  /**
   * Indicates whether the upward closure of terms should be exported.  If so, the
   * upward closure terms are filtered by cycKbSubsetFilterGuid below.
   */
  protected boolean includeUpwardClosure = false;
  
  /** the Cyc access object for communicating with Cyc */
  protected CycAccess cycAccess;
    
  /* the list of terms for which no extra properties are required */
  protected HashSet termsWithNoExtraProperties = new HashSet();
  
  /**
   * The CycKBSubsetCollection whose elements are exported to HTML if they
   * also generalizations of cycKbSubsetCollectionGuid collections or predicates above.
   * #$IKBConstant (not in OpenCyc)
   */
  protected CycFort cycKbSubsetFilter = null;
  
  /** the upward closure FORTs */
  protected CycList upwardClosureCycForts = new CycList(0);
  
  /** Sorts the given list of CycObjects.
   *
   * @param cycObjects the given list of CycObjects
   */
  final protected void sortCycObjects(final CycList cycObjects) {
    //// Preconditions
    assert cycObjects != null : "cycObjects cannot be null";
    
    Collections.sort(cycObjects, new CycObjectComparator());
  }
  
  /**
   * Returns the first indirect genls above the given term which is a member of the selected
   * terms.
   *
   * @param collection The cyc collection which is not a member of the selected terms.
   * @return The first indirect genls above the given term which is a member of the selected
   * terms.
   */
  final protected CycFort findSelectedGenls(final CycObject collection) throws IOException, CycApiException {
    //// Preconditions
    assert collection != null : "collection cannot be null";
    
    if (cycKBSubsetCollection != null && collection.equals(cycKBSubsetCollection)) {
      logger.finer("  ignoring genls " + collection);
      return  null;
    }
    if (cycSecureFort != null && collection.equals(cycSecureFort)) {
      logger.finer("  ignoring genls " + collection);
      return  null;
    }
    final CycList terms = findAllowedTermsOrGenls(cycAccess.getGenls(collection, inferenceMt));
    if (terms.isEmpty())
      return  null;
    else
      return (CycFort) terms.first();
  }
  
  /** Filters the given list of collection terms according to those allowed for output.  If the term is
   * not allowed, then its more genl terms are traversed until one is found that is acceptable.
   *
   * @param terms the list of collection terms
   * @return the given list of collection term with replacements if required
   */
  final protected CycList findAllowedTermsOrGenls(final CycList terms) throws IOException, CycApiException {
    //// Preconditions
    assert terms != null : "terms cannot be null";
    
    logger.finer("  before filtering, terms: " + terms);
    CycList tempList = filterSelectedConstants(terms);
    logger.finer("  after filtering, terms: " + tempList);
    if (tempList.size() < terms.size()) {
      final CycList allGenls = new CycList();
      for (int i = 0; i < terms.size(); i++) {
        final CycObject term = (CycObject) terms.get(i);
        if (! tempList.contains(term)) {
          tempList.addAllNew(filterSelectedConstants(cycAccess.getAllGenls(term, inferenceMt)));
        }
      }
      tempList = cycAccess.getMinCols(tempList, inferenceMt);
      logger.finer("  after adding genls, terms: " + tempList);
    }
    //// Postconditions
    assert tempList != null : "tempList cannot be null";
    
    return tempList;
  }
    
  /** Filters the given list of collection terms according to those allowed for output.  If the term is
   * not allowed, then candidate spec terms are examined until the best one acceptable one is found.
   *
   * @param terms the list of collection terms
   * @return the given list of collection term with replacements if required
   */
  final protected CycList findAllowedTermsOrSpecs(final CycList terms) throws IOException, CycApiException {
    //// Preconditions
    assert terms != null : "terms cannot be null";
    
    logger.finer("  before filtering, terms: " + terms);
    CycList tempList = filterSelectedConstants(terms);
    logger.finer("  after filtering, terms: " + tempList);
    if (tempList.size() < terms.size()) {
      for (int i = 0; i < terms.size(); i++) {
        final CycObject term = (CycObject) terms.get(i);
        if (! tempList.contains(term)) {
        for (int j = 0; i < selectedCycForts.size(); j++) {
          final CycObject cycObject = (CycObject) selectedCycForts.get(j);
          if (! tempList.contains(cycObject) &&
              cycAccess.isCollection(cycObject, inferenceMt) &&
              cycAccess.isSpecOf(cycObject, term, inferenceMt))
            tempList.addNew(cycObject);
          }
        }
      }
      tempList = cycAccess.getMaxCols(tempList, inferenceMt);
      logger.finer("  after adding specs, terms: " + tempList);
    }
    //// Postconditions
    assert tempList != null : "tempList cannot be null";
    
    return tempList;
  }
    
  /** Substitutes more general collection constants for functional collection
   * terms.
   *
   * @param cycForts The given list of cycForts which is to be processed.
   * @return The list of collection constant terms resulting from the substitution
   * of more general cycConstants for cycNarts.
   */
  final protected CycList substituteGenlConstantsForNarts(CycList cycForts) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycForts != null : "cycForts cannot be null";
    
    if (includeNonAtomicTerms)
      return cycForts;
    genlsLoopDetector.clear();
    genlsLoopDetector.addAll(cycForts);
    CycList result = substituteGenlConstantsForNartsInternal(cycForts);
    
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
  
  /** Substitutes more general collection constants for functional collection
   * terms.
   *
   * @param cycForts The given list of cycForts which is to be processed.
   * @return The list of collection constant terms resulting from the substitution
   * of more general cycConstants for cycNarts.
   */
  final protected CycList substituteGenlConstantsForNartsInternal(CycList cycForts) throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycForts != null : "cycForts cannot be null";
    
    CycList result = new CycList();
    for (int i = 0; i < cycForts.size(); i++) {
      final Object obj = cycForts.get(i);
      if (obj instanceof CycFort) {
        final CycFort cycFort = (CycFort) obj;
        if (cycFort instanceof CycConstant)
          result.add((CycFort) obj);
        else if (! genlsLoopDetector.contains(cycFort)) {
          genlsLoopDetector.add(cycFort);
          CycList genls = cycAccess.getGenls(cycFort, inferenceMt);
          genls = substituteGenlConstantsForNartsInternal(genls);
          logger.info(" substituting genls " + genls + " for " + cycFort);
          result.addAllNew(genls);
        }
      }
    }
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
  
  /**
   * Returns the first indirect genlPreds above the given predicate which is a member of the selected
   * terms, or null if none exist.
   *
   * @param predicate The cyc predicate which is not a member of the selected terms.
   * @return The first genlPreds above the given predicate which is a member of the selected
   * terms, or null if none exist
   */
  final protected CycObject findSelectedGenlPreds(final CycObject predicate) throws IOException, CycApiException {
    //// Preconditions
    assert predicate != null : "predicate cannot be null";
    
    final CycList genlPredicates = (cycAccess.getGenlPreds(predicate, inferenceMt));
    if (genlPredicates.isEmpty())
      return  null;
    for (int i = 0; i < genlPredicates.size(); i++) {
      final CycObject genlPredicate = (CycObject) genlPredicates.get(i);
      if (selectedCycForts.contains(genlPredicate))
        return genlPredicate;
    }
    return null;
  }
  
  /**
   * Removes unselected terms from the given list.
   *
   * @param constants The given list of constants which is to be filtered.
   * @return The filtered list.
   */
  final protected CycList filterSelectedConstants(final CycList constants) throws IOException, CycApiException {
    //// Preconditions
    assert constants != null : "constants cannot be null";
    
    if (constants.size() == 0)
      return  constants;
    CycList result = new CycList();
    
    final CycFort researchCycConstant = cycAccess.getConstantByName("ResearchCycConstant");
    for (int i = 0; i < constants.size(); i++) {
      Object object = constants.get(i);
      if (useResearchCycOntology && researchCycConstant != null) {
        if (object instanceof CycConstant) {
          if (cycAccess.isQuotedIsa((CycConstant) object, researchCycConstant, inferenceMt))
            result.add(object);
        }
        else if (object instanceof CycNart) {
          if (cycAccess.isQuotedIsa(((CycNart) object).getFunctor(), researchCycConstant, inferenceMt))
            result.add(object);
        }
        else if (object instanceof CycList) {
          if (cycAccess.isQuotedIsa((CycFort) ((CycList) object).first(), researchCycConstant, inferenceMt))
            result.add(object);
        }
      }
      else if (selectedCycForts.contains(object))
        result.add(object);
    }

    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
    
  /** Removes constant terms without comments from the list, when specified.
   *
   * @param constants The given list of constants which is to be filtered.
   * @return The filtered list.
   */
  final protected CycList omitTermsWithoutComments(final CycList terms) throws IOException, CycApiException {
    //// Preconditions
    assert terms != null : "terms cannot be null";
    
    final int terms_size = terms.size();
    if (! areTermsWithoutCommentsOmitted ||  terms_size == 0)
      return  terms;
    logger.fine("Dropping terms with no comments");
    final CycList result = new CycList(terms_size);
    for (int i = 0; i < terms_size; i++) {
      final CycObject term = (CycObject) terms.get(i);
      final String comment = cycAccess.getComment(term, inferenceMt);
      if (term instanceof CycNart || (comment != null && comment.length() > 0))
        result.add(term);
      logger.fine(term.cyclify() + " has no comment, dropped");
    }
    
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
    
  /** Reports export progress to each of the listeners.
   *
   * @param percentProgress the OWL export percent complete
   */
  protected abstract void reportProgress(final double percentProgress);
  
  /** Considers the terms for inclusion in the upward closure.
   *
   * @param selectedCycFortsSet the set of selected terms
   * @param upwardClosure the upward closure terms in list form
   * @param upwardClosureSet the upward closure terms in set form
   * @param terms the candidate terms for inclusion in the upward closure
   * @param sourceTerm the source from which the candidate terms arose
   */
  final protected void considerForUpwardClosure(final HashSet selectedCycFortsSet,
                                          final CycList upwardClosure,
                                          final HashSet upwardClosureSet,
                                          final CycList terms,
                                          final CycFort sourceTerm)
    throws UnknownHostException, IOException, CycApiException { 
    //// Preconditions
    assert selectedCycFortsSet != null : "selectedCycFortsSet cannot be null";
    assert upwardClosure != null : "upwardClosure cannot be null";
    assert upwardClosureSet != null : "upwardClosureSet cannot be null";
    assert terms != null : "terms cannot be null";
    assert sourceTerm != null : "sourceTerm cannot be null";
    
    for (int j = 0; j < terms.size(); j++) {
      CycFort term = null;
      try {
        term = (CycFort) terms.get(j);
      } catch (ClassCastException e) {
        logger.fine("***** term: " + term +
                    " invalid genls " + terms.get(j) +
                    " (" + terms.get(j).getClass() + ")");
        continue;
      }
      if ((! upwardClosureSet.contains(term)) &&
          (! selectedCycFortsSet.contains(term)) &&
          isEligibleForUpwardClosureInclusion(term)) {
        logger.fine(sourceTerm + " upward closure term " + term);
        upwardClosure.add(term);
        upwardClosureSet.add(term);
      }
    }
  }
  
  /**
   * Returns true if the given term is eligible for incusion in the upward closure.
   *
   * @param cycFort the given term
   * @return true if the given term is eligible for incusion in the upward closure
   */
  final protected boolean isEligibleForUpwardClosureInclusion(final CycFort cycFort)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert cycFort != null : "cycFort cannot be null";
    
    if (upwardClosureKbSubsetCollections.isEmpty())
      return true;
    
    for (int i = 0; i < upwardClosureKbSubsetCollections.size(); i++) {
      CycFort collection = (CycFort) upwardClosureKbSubsetCollections.get(i);
      if (cycAccess.isQuotedIsa(cycFort, collection, inferenceMt))
        return true;
    }
    return false;
  }
  
  /**
   * Given a set of collection terms, returns a set which does not contain any collections
   * are more genl than the remaining collections.
   *
   * @param collections The given set of collection terms.
   * @return A set of collection terms which does not contain any collections are more genl than the
   * remaining collections.
   */
  final protected CycList specificCollections(CycList collections) throws IOException, CycApiException {
    //// Preconditions
    assert collections != null : "collections cannot be null";
    
    logger.fine("  specificCollections input: " + collections.cyclify());
    CycList result = new CycList();
    for (int i = 0; i < collections.size(); i++) {
      CycFort genlsCollection = (CycFort)collections.get(i);
      boolean genlsOf = false;
      for (int j = 0; j < collections.size(); j++) {
        CycFort specCollection = (CycFort)collections.get(j);
        if (i != j) {
          logger.finer("  genlsCollection? " + genlsCollection + " specCollection? " + specCollection);
          if (cycAccess.isGenlOf(genlsCollection, specCollection, inferenceMt)) {
            genlsOf = true;
            logger.finer("  collection " + genlsCollection + " genls of " + specCollection + " and is dropped");
            break;
          }
        }
      }
      if (!genlsOf)
        result.add(genlsCollection);
    }
    logger.fine("  specificCollections output: " + result.cyclify());
    
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
  
  /** Prepares the selected assertions by gathering all the arg1 terms.  Used with the
   * EXPORT_SELECTED_ASSERTIONS option. 
   */
  protected  void prepareSelectedAssertions() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert selectedAssertions != null : "selectedAssertions cannot be null";
    assert ! selectedAssertions.isEmpty() : "selectedAssertions cannot be empty";
    
    logger.finer("\npreparing the selected assertions\n");
    final int selectedAssertions_size = selectedAssertions.size();
    final HashSet predicateSet = new  HashSet();
    final HashSet termSet = new  HashSet();
    for (int i = 0; i < selectedAssertions_size; i++) {
      final CycList assertionELFormula = (CycList) selectedAssertions.get(i);
      logger.finer("considering " + assertionELFormula.cyclify());
      assert assertionELFormula.size() == 3 : "selected asserion must be a binary gaf " + assertionELFormula.cyclify();
      final CycObject predicate = (CycObject) assertionELFormula.first();
      if (! predicateSet.contains(predicate)) {
        if (predicate instanceof CycList) {
          termSet.add(predicate);
          logger.fine("adding naut predicate " + predicate.cyclify());
        }
        else if (predicate instanceof CycNart) {
          termSet.add(predicate);
          logger.fine("adding nart predicate " + predicate.cyclify());
        }
        logger.fine("adding predicate " + predicate.cyclify());
        predicateSet.add(predicate);
      }
      final CycObject arg1Term = (CycObject) assertionELFormula.second();
      if (! termSet.contains(arg1Term)) {
        logger.finer("adding term " + arg1Term.cyclify());
        termSet.add(arg1Term);
      }
    }
    for (int i = 0; i < selectedAssertions_size; i++) {
      final CycList assertionELFormula = (CycList) selectedAssertions.get(i);
      final Object arg2Term = assertionELFormula.third();
      if ((arg2Term instanceof CycFort  || 
          (arg2Term instanceof CycList && 
           ! cycAccess.isa((CycList) arg2Term, cycAccess.getKnownConstantByName("Date"), inferenceMt))) &&
          (! (arg2Term instanceof CycObject) || ! cycAccess.isCollection((CycObject) arg2Term, inferenceMt))) {
        if (! termSet.contains(arg2Term)) {
          logger.finer("adding term " + ((CycObject) arg2Term).cyclify());
          termSet.add(arg2Term);
          termsWithNoExtraProperties.add(arg2Term);
        }
      }
    }
    
    applicableBinaryPredicates = new CycList();
    final Iterator predicateSet_iter = predicateSet.iterator();
    while (predicateSet_iter.hasNext()) {
      final CycObject predicate = (CycObject) predicateSet_iter.next();
      logger.finest("applicable predicate " + predicate.cyclify());
      applicableBinaryPredicates.add(predicate);
    }
    selectedCycForts = new CycList();
    final Iterator termSet_iter = termSet.iterator();
    while (termSet_iter.hasNext())
      selectedCycForts.add(termSet_iter.next());
   
    //// Postconditions
    assert applicableBinaryPredicates != null : "applicableBinaryPredicates cannot be null";
    assert ! applicableBinaryPredicates.isEmpty() : "applicableBinaryPredicates cannot be empty";
    assert selectedCycForts != null : "selectedCycForts cannot be null";
    assert ! selectedCycForts.isEmpty() : "selectedCycForts cannot be empty";
  }
  
//  /** Populates the disjoint collections dictionary using the given list of collections.
//   *
//   * @param owlSelectedClasses the given list of collections for disjointness processing
//   */
//  protected void populateDisjointWithDictionary(final CycList owlSelectedClasses) throws UnknownHostException, IOException, CycApiException {
//    //// Preconditions 
//    assert selectedCycForts != null : "selectedCycForts must not be null";
//    
//    logger.info("populating disjointWiths");
//    final int owlSelectedClasses_size = owlSelectedClasses.size();
//    final CycList candidateDisjointWiths = new CycList(owlSelectedClasses_size);
//    for (int i = 0; i < owlSelectedClasses_size; i++) {
//      final CycObject owlSelectedClass = (CycObject) owlSelectedClasses.get(i);
//      CycList cycDisjointWiths = cycAccess.getDisjointWiths(owlSelectedClass);
//      cycDisjointWiths = findAllowedTermsOrSpecs(cycDisjointWiths);
//      logger.fine("raw disjointWith " + owlSelectedClass.cyclify() + " --> " + cycDisjointWiths.cyclify());
//      CycList disjointWiths = (CycList) disjointWithDictionary.get(owlSelectedClass);
//      if (disjointWiths == null) {
//        disjointWiths = new CycList();
//        disjointWithDictionary.put(owlSelectedClass, disjointWiths);
//      }
//      final int cycDisjointWiths_size = cycDisjointWiths.size();
//      for (int j = 0; j < cycDisjointWiths_size; j++) {
//        final CycObject cycDisjointWith = (CycObject) cycDisjointWiths.get(j);
//        if (owlSelectedClasses.contains(cycDisjointWith) && ! disjointWiths.contains(cycDisjointWith))
//          disjointWiths.add(cycDisjointWith);
//        else {
//          for (int k = 0; k < owlSelectedClasses_size; k++) {
//            final CycObject owlSelectedClass1 = (CycObject) owlSelectedClasses.get(k);
//            if (owlSelectedClass1 instanceof CycFort && 
//                cycDisjointWith instanceof CycFort &&
//                 cycAccess.isSpecOf((CycFort) owlSelectedClass1, (CycFort) cycDisjointWith)) {
//              if (! disjointWiths.contains(owlSelectedClass1))
//              disjointWiths.add(owlSelectedClass1);              
//              logger.finest("    disjointWith spec - yes " + owlSelectedClass1.cyclify() + " --> " + cycDisjointWith.cyclify());
//            }
//            else
//              logger.finest("    disjointWith spec - no  " + owlSelectedClass1.cyclify() + " --> " + cycDisjointWith.cyclify());
//          }
//        }
//      }
//      Collections.sort(disjointWiths);
//      logger.info("    disjointWith " + owlSelectedClass.cyclify() + " --> " + disjointWiths.cyclify());
//      final CycList minDisjointWiths = cycAccess.getMaxCols(disjointWiths);
//      disjointWiths.clear();
//      disjointWiths.addAll(minDisjointWiths);
//      Collections.sort(disjointWiths);
//      logger.info("min disjointWith " + owlSelectedClass.cyclify() + " --> " + disjointWiths.cyclify());
//      final int disjointWiths_size = disjointWiths.size();
//      for (int j = 0; j < disjointWiths_size; j++) {
//        final CycObject disjointWith = (CycObject) disjointWiths.get(j);
//        CycList inverseDisjointWiths = (CycList) disjointWithDictionary.get(disjointWith);
//        if (inverseDisjointWiths == null) {
//          inverseDisjointWiths = new CycList();
//          disjointWithDictionary.put(disjointWith, inverseDisjointWiths);
//        }
//        if (! inverseDisjointWiths.contains(owlSelectedClass)) {
//          inverseDisjointWiths.add(owlSelectedClass);
//          final CycList minInverseDisjointWiths = cycAccess.getMaxCols(inverseDisjointWiths);
//          inverseDisjointWiths.clear();
//          inverseDisjointWiths.addAll(minInverseDisjointWiths);
//          Collections.sort(inverseDisjointWiths);
//        }
//        logger.info("inv disjointWith " + disjointWith.cyclify() + " --> " + inverseDisjointWiths.cyclify());
//      }
//      logger.info("");
//    }
//  }
//  
  
  /** Ensures that the selected reifiable terms are reified, drop any duplicate terms, and
   * drop terms that are not CycObjects (bad input). 
   */
  protected void ensureSelectedTermsAreReified() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions 
    assert selectedCycForts != null : "selectedCycForts must not be null";
    
    final CycList reifiedTerms = new CycList();
    final int selectedCycForts_size = selectedCycForts.size();
    for (int i = 0; i < selectedCycForts_size; i++) {
      final Object term = selectedCycForts.get(i);
      if (term instanceof CycList) {
        final Object reifiedTerm = cycAccess.getHLCycTerm(((CycList)term).cyclify());
        if (reifiedTerm instanceof CycFort)
          if (reifiedTerms.contains(reifiedTerm)) {
            logger.info("dropping duplicate input term " + ((CycFort)reifiedTerm).cyclify());
          }
          else
            reifiedTerms.add(reifiedTerm);
        else {
          logger.info("cannot reify NAUT " + ((CycList)term).cyclify());
          if (reifiedTerms.contains(term)) {
            logger.info("dropping duplicate input term " + ((CycList)term).cyclify());
          }
          else
            reifiedTerms.add(term);
        }
      }
      else if (term instanceof CycFort) {
        if (reifiedTerms.contains(term)) {
          logger.info("dropping duplicate input term " + ((CycFort)term).cyclify());
        }
        else
          reifiedTerms.add(term);
      }
      else
        logger.info("dropping bad input term " + term.toString());
    }
    selectedCycForts = reifiedTerms;
    
    //// Postconditions 
    assert selectedCycForts != null : "selectedCycForts must not be null";
  }
  
  /** Gathers the (SubcollectionOfWithRelationFromFn COL PRED THING) narts for the selected collections COL. */
  protected void gatherSubcollectionOfWithRelationFromFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    subcollectionOfWithRelationFromFns = new CycList();
    logger.fine("(SubcollectionOfWithRelationFromFn COL PRED THING)");
    try {
      final CycVariable collectionVariable = CycObjectFactory.makeCycVariable("?COL");
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable thingVariable = CycObjectFactory.makeCycVariable("?THING");
      final CycList queryVariables = new CycList(2);
      queryVariables.add(collectionVariable);
      queryVariables.add(predicateVariable);
      queryVariables.add(thingVariable);
      final String queryString = 
        "(#$and " +
        "  (#$elementOf ?COL " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$genls (#$SubcollectionOfWithRelationFromFn ?COL ?PRED ?THING) ?COL))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(queryVariables, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < bindings.size(); i++) {
        final CycList tuple = (CycList) bindings.get(i);
        CycObject collection = (CycObject) tuple.first();
        CycObject predicate = (CycObject) tuple.second();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (predicate != null) {
          Object thing = tuple.third();
          if (thing != null && selectedCycForts.contains(thing)) {
            final CycNart subcollectionOfWithRelationFromFn = 
              new CycNart(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationFromFn"), 
                          collection,
                          predicate,
                          thing);
            logger.fine("  " + subcollectionOfWithRelationFromFn.toString());
            subcollectionOfWithRelationFromFns.add(subcollectionOfWithRelationFromFn);           
          }
        }
      }
      sortCycObjects(subcollectionOfWithRelationFromFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (SubcollectionOfWithRelationToFn COL PRED THING) narts for the given collection COL. */
  protected void gatherSubcollectionOfWithRelationToFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    subcollectionOfWithRelationToFns = new CycList();
    logger.fine("(SubcollectionOfWithRelationToFn COL PRED THING)");
    try {
      final CycVariable collectionVariable = CycObjectFactory.makeCycVariable("?COL");
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable thingVariable = CycObjectFactory.makeCycVariable("?THING");
      final CycList queryVariables = new CycList(2);
      queryVariables.add(collectionVariable);
      queryVariables.add(predicateVariable);
      queryVariables.add(thingVariable);
      final String queryString = 
        "(#$and " +
        "  (#$elementOf ?COL " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$genls (#$SubcollectionOfWithRelationToFn ?COL ?PRED ?THING) ?COL))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(queryVariables, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < bindings.size(); i++) {
        final CycList tuple = (CycList) bindings.get(i);
        final CycObject collection = (CycObject) tuple.first();
        CycObject predicate = (CycObject) tuple.second();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (predicate != null) {
          Object thing = tuple.third();
          if (thing != null && selectedCycForts.contains(thing)) {
            final CycNart subcollectionOfWithRelationToFn = 
              new CycNart(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationToFn"), 
                          collection,
                          predicate,
                          thing);
            logger.fine("  " + subcollectionOfWithRelationToFn.toString());
            subcollectionOfWithRelationToFns.add(subcollectionOfWithRelationToFn);           
          }
        }
      }
      sortCycObjects(subcollectionOfWithRelationToFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (SubcollectionOfWithRelationFromTypeFn COL-1 PRED COL-2) narts for the given collection COL-1. */
  protected void gatherSubcollectionOfWithRelationFromTypeFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    subcollectionOfWithRelationFromTypeFns = new CycList();
    logger.fine("(SubcollectionOfWithRelationFromTypeFn COL-1 PRED COL-2)");
    try {
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList queryVariables = new CycList(2);
      queryVariables.add(collection1Variable);
      queryVariables.add(predicateVariable);
      queryVariables.add(collection2Variable);
      final String queryString = 
        "(#$and " +
        "  (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$genls (#$SubcollectionOfWithRelationFromTypeFn ?COL-1 ?PRED ?COL-2) ?COL-1))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(queryVariables, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < bindings.size(); i++) {
        final CycList tuple = (CycList) bindings.get(i);
        final CycObject collection1 = (CycObject) tuple.first();
        CycObject predicate = (CycObject) tuple.second();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (predicate != null) {
          CycObject collection2 = (CycObject) tuple.third();
          if (collection2 != null && selectedCycForts.contains(collection2)) {
            final CycNart subcollectionOfWithRelationFromTypeFn = 
              new CycNart(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationFromTypeFn"), 
                          collection1,
                          predicate,
                          collection2);
            logger.fine("  " + subcollectionOfWithRelationFromTypeFn.toString());
            subcollectionOfWithRelationFromTypeFns.add(subcollectionOfWithRelationFromTypeFn);           
          }
        }
      }
      sortCycObjects(subcollectionOfWithRelationFromTypeFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (SubcollectionOfWithRelationToTypeFn COL-1 PRED COL-2) narts for the given collection COL-1. */
  protected void gatherSubcollectionOfWithRelationToTypeFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    subcollectionOfWithRelationToTypeFns = new CycList();
    logger.fine("(SubcollectionOfWithRelationToTypeFn COL-1 PRED COL-2)");
    try {
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable predicateVariable = CycObjectFactory.makeCycVariable("?PRED");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList queryVariables = new CycList(2);
      queryVariables.add(collection1Variable);
      queryVariables.add(predicateVariable);
      queryVariables.add(collection2Variable);
      final String queryString = 
        "(#$and " +
        "  (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$genls (#$SubcollectionOfWithRelationToTypeFn ?COL-1 ?PRED ?COL-2) ?COL-1))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(queryVariables, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < bindings.size(); i++) {
        final CycList tuple = (CycList) bindings.get(i);
        final CycObject collection1 = (CycObject) tuple.first();
        CycObject predicate = (CycObject) tuple.second();
        if (! selectedCycForts.contains(predicate))
          predicate = findSelectedGenlPreds(predicate);
        if (predicate != null) {
          CycObject collection2 = (CycObject) tuple.third();
          if (collection2 != null && selectedCycForts.contains(collection2)) {
            final CycNart subcollectionOfWithRelationToTypeFn = 
              new CycNart(cycAccess.getKnownConstantByName("SubcollectionOfWithRelationToTypeFn"), 
                          collection1,
                          predicate,
                          collection2);
            logger.fine("  " + subcollectionOfWithRelationToTypeFn.toString());
            subcollectionOfWithRelationToTypeFns.add(subcollectionOfWithRelationToTypeFn);           
          }
        }
      }
      sortCycObjects(subcollectionOfWithRelationToTypeFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (CollectionIntersection2 COL-1 COL-2) narts for the given collection COL-1. */
  protected void gatherCollectionIntersection2Fns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    collectionIntersection2Fns = new CycList();
    logger.fine("(CollectionIntersection2 COL-1 COL-2)");
    try {
      final CycVariable collection1Variable = CycObjectFactory.makeCycVariable("?COL-1");
      final CycVariable collection2Variable = CycObjectFactory.makeCycVariable("?COL-2");
      final CycList queryVariables = new CycList(2);
      queryVariables.add(collection1Variable);
      queryVariables.add(collection2Variable);
      final String queryString = 
        "(#$and " +
        
        // TODO try filtering COL-2 in the query
        // why not a termOfUnit query? as this is expensive
        
        "  (#$elementOf ?COL-1 " + theSetOfOWLSelectedClasses.cyclify() + ") " +
        "  (#$genls (#$CollectionIntersection2Fn ?COL-1 ?COL-2) ?COL-1))";
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList bindings = cycAccess.queryVariables(queryVariables, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < bindings.size(); i++) {
        final CycList tuple = (CycList) bindings.get(i);
        final CycObject collection1 = (CycObject) tuple.first();
        final CycObject collection2 = (CycObject) tuple.second();
        if (! selectedCycForts.contains(collection2))
          continue;
        final CycNart collectionIntersection2Fn = 
          new CycNart(cycAccess.getKnownConstantByName("CollectionIntersection2Fn"), 
                      collection1,
                      collection2);
        logger.fine("  " + collectionIntersection2Fn.toString());
        collectionIntersection2Fns.add(collectionIntersection2Fn);           
      }
      sortCycObjects(collectionIntersection2Fns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (CollectionIntersectionFn SET) narts for the given collection COL-1. */
  protected void gatherCollectionIntersectionFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    collectionIntersectionFns = new CycList();
    logger.fine("(CollectionIntersectionFn SET)");
    try {
      final CycVariable setVariable = CycObjectFactory.makeCycVariable("?SET");
      final String queryString = 
        "(and " +
        "   (evaluate ?TERM-SET " +
        "       (SetExtentFn " + theSetOfOWLSelectedClasses.cyclify() + ")) " +
        "   (elementOf ?COL-1 ?TERM-SET) " +
        "   (termOfUnit ?TOU " +
        "       (CollectionIntersectionFn ?SET)) " +
        "   (operatorFormulas TheSet ?SET) " +
        "   (elementOf ?COL-1 ?SET) " +
        "   (equals " +
        "       (SetOrCollectionIntersection2Fn ?TERM-SET ?SET) ?SET)) ";      
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList results = cycAccess.queryVariable(setVariable, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < results.size(); i++) {
        CycList set = (CycList) results.get(i);
        if (set.size() < 2)
          continue;
        set = (CycList) set.rest();
        boolean allCollectionsIncluded = true;
        for (int j = 0; j < set.size(); j++) {
          final CycObject intersectingCollection = (CycObject) set.get(j);
          if (! owlSelectedClasses.contains(intersectingCollection)) {
            allCollectionsIncluded = false;
            break;
          }
        }
        if (allCollectionsIncluded) {
          Collections.sort(set);
          final CycList canonicalSet = new CycList(set.size() + 1);
          canonicalSet.add(cycAccess.getKnownConstantByName("TheSet"));
          canonicalSet.addAll(set);
          final CycNart collectionIntersectionFn = 
            new CycNart(cycAccess.getKnownConstantByName("CollectionIntersectionFn"), 
                        canonicalSet);
          if (! collectionIntersectionFns.contains(collectionIntersectionFn)) {
            logger.fine("  " + collectionIntersectionFn.toString());
            collectionIntersectionFns.add(collectionIntersectionFn);      
          }
        }
      }
      sortCycObjects(collectionIntersectionFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
  
  /** Gathers the (CollectionUnionFn SET) narts for the given collection COL-1. */
  protected void gatherCollectionUnionFns() throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert theSetOfOWLSelectedClasses != null : "theSetOfOWLSelectedClasses must not be null";
    
    collectionUnionFns = new CycList();
    logger.fine("(CollectionUnionFn SET)");
    try {
      final CycVariable setVariable = CycObjectFactory.makeCycVariable("?SET");
      final String queryString = 
        "(and " +
        "   (evaluate ?TERM-SET " +
        "       (SetExtentFn " + theSetOfOWLSelectedClasses.cyclify() + ")) " +
        "   (elementOf ?COL-1 ?TERM-SET) " +
        "   (termOfUnit ?TOU " +
        "       (CollectionUnionFn ?SET)) " +
        "   (operatorFormulas TheSet ?SET) " +
        "   (elementOf ?COL-1 ?SET) " +
        "   (equals " +
        "       (SetOrCollectionIntersection2Fn ?TERM-SET ?SET) ?SET)) ";      
      final CycList query = cycAccess.makeCycList(queryString);
      final CycList results = cycAccess.queryVariable(setVariable, query, inferenceMt, (HashMap) null);
      for (int i = 0; i < results.size(); i++) {
        CycList set = (CycList) results.get(i);
        if (set.size() < 2)
          continue;
        set = (CycList) set.rest();
        boolean allCollectionsIncluded = true;
        for (int j = 0; j < set.size(); j++) {
          final CycObject intersectingCollection = (CycObject) set.get(j);
          if (! owlSelectedClasses.contains(intersectingCollection)) {
            allCollectionsIncluded = false;
            break;
          }
        }
        if (allCollectionsIncluded) {
          Collections.sort(set);
          final CycList canonicalSet = new CycList(set.size() + 1);
          canonicalSet.add(cycAccess.getKnownConstantByName("TheSet"));
          canonicalSet.addAll(set);
          final CycNart collectionUnionFn = 
            new CycNart(cycAccess.getKnownConstantByName("CollectionUnionFn"), 
                        canonicalSet);
          if (! collectionUnionFns.contains(collectionUnionFn)) {
            logger.fine("  " + collectionUnionFn.toString());
            collectionUnionFns.add(collectionUnionFn);      
          }
        }
      }
      sortCycObjects(collectionUnionFns);      
    }
    catch (CycApiException e) {
      e.printStackTrace();
      return;
    }
  }
    
  //// Private Area
  
  //// Internal Rep

  /** the disjointWith dictionary, selected collection term --> list of most general selected disjoint collection terms */
  protected HashMap disjointWithDictionary = new HashMap();
  
  /* the #$CycKBSubsetCollection constant */
  private final CycConstant cycKBSubsetCollection;
  
  /* the #$ResearchCycConstant constant */
  private final CycConstant researchCycConstant;
  
  /* the #$CycSecureFort constant */
  private final CycConstant cycSecureFort;
  
  /** the list of narts found during a genls upward search, to prevent looping on mutual genls */
  private final CycList genlsLoopDetector = new CycList();

  /** the logger */
  protected Logger logger;
  
  /** the selected collections for export */
  protected final CycList owlSelectedClasses = new CycList();
  
  /** the selected predicates for export */
  protected final CycList owlSelectedProperties = new CycList();
  
  /** the selected individuals for export */
  protected final CycList owlSelectedIndividuals = new CycList();
  
  /** the derived SubcollectionOfWithRelationFromFns for export */
  protected CycList subcollectionOfWithRelationFromFns = new CycList(0);
  
  /** the derived SubcollectionOfWithRelationToFns for export */
  protected CycList subcollectionOfWithRelationToFns = new CycList(0);
  
  /** the derived SubcollectionOfWithRelationFromTypeFns for export */
  protected CycList subcollectionOfWithRelationFromTypeFns = new CycList(0);
  
  /** the derived SubcollectionOfWithRelationToTypeFns for export */
  protected CycList subcollectionOfWithRelationToTypeFns = new CycList(0);
  
  /** the derived CollectionIntersection2Fns for export */
  protected CycList collectionIntersection2Fns = new CycList(0);
  
  /** the derived CollectionIntersectionFns for export */
  protected CycList collectionIntersectionFns = new CycList(0);
  
  /** the derived CollectionUnionFns for export */
  protected CycList collectionUnionFns = new CycList(0);
  
  /** the selected collection terms represented as a CycL TheSet NAUT */
  protected CycList theSetOfOWLSelectedClasses = null;
  
  /** the selected individual terms represented as a CycL TheSet NAUT */
  protected CycList theSetOfOWLSelectedIndividuals = null;
  
  /** the indicator that this export is to be cancelled */
  protected boolean isCancelled = false;
  
  /** the export percent progress */
  protected double percentProgress = 0.0;
  
  /** the previous value of the reported percent progress, used for asserting that percent progress monotonically increases */
  protected double previouslyReportedPercentComplete = -1.0;
  
  /** the dictionary of exported term --> percent progress (Double). */
  protected final HashMap percentProgressDictionary = new HashMap();
  
  //// Main
  
}
