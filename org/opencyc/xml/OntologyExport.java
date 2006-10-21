/* $Id$
 *
 * Copyright (c) 2003 - 2005 Cycorp, Inc.  All rights reserved.
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
import org.opencyc.cycobject.Guid;
import org.opencyc.util.Log;

//// External Imports
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <P>OntologyExport is designed to...
 *
 * <P>Copyright (c) 2003 - 2005 Cycorp, Inc.  All rights reserved.
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
  public OntologyExport(CycAccess cycAccess) {
    Log.makeLog();
    this.cycAccess = cycAccess;
    if (verbosity > 2)
      Log.current.println(cycAccess.getCycConnection().connectionInfo());
  }
  
  
  //// Public Area
  
  /**
   * The default verbosity of the OWL export output.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   */
  public static final int DEFAULT_VERBOSITY = 3;
  
  /**
   * Sets verbosity of the OWL export output.  0 --> quiet ... 9 -> maximum
   * diagnostic input.
   */
  public int verbosity = DEFAULT_VERBOSITY;
  
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
  public static final Guid publicConstantGuid =
  CycObjectFactory.makeGuid("bd7abd90-9c29-11b1-9dad-c379636f7270");
  
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
   * Indicates whether to include the entire Research Cyc ontology for the exported class tags. 
   */
  public boolean useResearchCycOntology = false;
  
  /** the list of applicable binary predicates */
  public CycList applicableBinaryPredicates = new CycList();
  
  /** the list of assertions for export, used with EXPORT_SELECTED_ASSERTIONS */
  public CycList selectedAssertions = null;
  
  /** the selected Cyc FORTS, used with EXPORT_SELECTED_TERMS */
  public CycList selectedCycForts = null;
  
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
    
    if (verbosity > 2)
      Log.current.println("Pre-caching names of " + cycObjects.size() + " CycObjects");
    final int cycObjects_size = cycObjects.size();
    for (int i = 0; i < cycObjects_size; i++)
      cycObjects.get(i).toString();
    if (verbosity > 2)
      Log.current.println("Beginnng Sort of " + cycObjects.size() + " CycObjects");
    Collections.sort(cycObjects, new CycObjectComparator());
    if (verbosity > 2)
      Log.current.println("Completed sort of " + cycObjects.size() + " CycObjects");
  }
  
  /**
   * Returns the first indirect genls above the given term which is a member of the selected
   * terms.
   *
   * @param collection The cyc collection which is not a member of the selected terms.
   * @return The first indirect genls above the given term which is a member of the selected
   * terms.
   */
  final protected CycFort findSelectedGenls(final CycFort collection) throws IOException, CycApiException {
    if (collection.equals(cycAccess.getKnownConstantByName("CycKBSubsetCollection"))) {
      if (verbosity > 4)
        Log.current.println("  ignoring genls " + collection);
      return  null;
    }
    Guid cycSecureFortGuid = new Guid("bf71b522-9c29-11b1-9dad-c379636f7270");
    if (collection.equals(cycAccess.getKnownConstantByGuid(cycSecureFortGuid))) {
      if (verbosity > 4)
        Log.current.println("  ignoring genls " + collection);
      return  null;
    }
    final CycList terms = findAllowedTermsOrGenls(cycAccess.getGenls(collection));
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
    
    if (verbosity > 4)
      Log.current.println("  before filtering, terms " + terms);
    CycList tempList = filterSelectedConstants(terms);
    if (verbosity > 4)
      Log.current.println("  after filtering, terms " + tempList);
    if (tempList.size() == 0) {
      final CycList allGenls = new CycList();
      for (int i = 0; i < terms.size(); i++) {
        final CycFort term = (CycFort) terms.get(i);
        allGenls.addAllNew(cycAccess.getAllGenls(term));
      }
      tempList = cycAccess.getMinCols(filterSelectedConstants(allGenls));
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
    
    CycList result = new CycList();
    for (int i = 0; i < cycForts.size(); i++) {
      final Object obj = cycForts.get(i);
      if (obj instanceof CycFort) {
        final CycFort cycFort = (CycFort) obj;
        if (cycFort instanceof CycConstant)
          result.add((CycFort) obj);
        else {
          CycList genls = substituteGenlConstantsForNarts(cycAccess.getGenls(cycFort));
          if (verbosity > 0)
            Log.current.println(" substituting genls " + genls + " for " + cycFort);
          result.addAllNew(genls);
        }
      }
    }
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
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
    if (researchCycConstant == null)
      result = constants;
    else {
      for (int i = 0; i < constants.size(); i++) {
        Object object = constants.get(i);
        if (useResearchCycOntology) {
          if (object instanceof CycConstant) {
            if (cycAccess.isQuotedIsa((CycConstant) object, researchCycConstant));
              result.add(object);
          }
          else if (object instanceof CycNart) {
            if (cycAccess.isQuotedIsa(((CycNart) object).getFunctor(), researchCycConstant))
              result.add(object);
          }
          else if (object instanceof CycList) {
            if (cycAccess.isQuotedIsa((CycFort) ((CycList) object).first(), researchCycConstant))
              result.add(object);
          }
        }
        else if (selectedCycForts.contains(object))
          result.add(object);
      }
    }
    //// Postconditions
    assert result != null : "result cannot be null";
    
    return  result;
  }
    
  /**
   * Gathers the updward closure of the selected CycForts with regard to isas and genls
   * for collection terms, and with regard to isas and genlPreds for predicate terms.
   *
   * @param selectedCycForts The selected CycForts.
   * @return The updward closure of the selected CycForts with regard to genls
   * for collection terms, and with regard to genlPreds for predicate terms.
   */
  final protected CycList gatherUpwardClosure(final CycList selectedCycForts)
  throws UnknownHostException, IOException, CycApiException {
    //// Preconditions
    assert selectedCycForts != null : "selectedCycForts cannot be null";
    
    sortCycObjects(selectedCycForts);
    CycList upwardClosure = new CycList();
    // Redundant HashSets for efficient contains() method below.
    final HashSet selectedCycFortsSet = new HashSet(selectedCycForts);
    final HashSet upwardClosureSet = new HashSet(selectedCycForts.size());
    for (int i = 0; i < selectedCycForts.size(); i++) {
      CycFort cycFort = (CycFort)selectedCycForts.get(i);
      considerForUpwardClosure(selectedCycFortsSet,
                               upwardClosure,
                               upwardClosureSet,
                               cycAccess.getAllIsa(cycFort),
                               cycFort);
      if (cycAccess.isCollection(cycFort))
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getAllGenls(cycFort),
                                 cycFort);
      else if ((cycFort instanceof CycConstant) && (cycAccess.isPredicate(cycFort))) {
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getAllGenlPreds((CycConstant) cycFort),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg1Isas((CycConstant) cycFort),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg2Isas((CycConstant) cycFort),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg3Isas((CycConstant) cycFort),
                                 cycFort);
        considerForUpwardClosure(selectedCycFortsSet,
                                 upwardClosure,
                                 upwardClosureSet,
                                 cycAccess.getArg4Isas((CycConstant) cycFort),
                                 cycFort);
      }
    }
    //// Postconditions
    assert upwardClosure != null : "upwardClosure cannot be null";
    
    return  upwardClosure;
  }
  
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
        if (verbosity > 3)
          Log.current.println("***** term: " + term +
          " invalid genls " + terms.get(j) +
          " (" + terms.get(j).getClass() + ")");
        continue;
      }
      if ((! upwardClosureSet.contains(term)) &&
      (! selectedCycFortsSet.contains(term)) &&
      isEligibleForUpwardClosureInclusion(term)) {
        if (verbosity > 2)
          Log.current.println(sourceTerm + " upward closure term " + term);
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
    
    for (int i = 0; i < upwardClosureKbSubsetCollections.size(); i++) {
      CycFort collection = (CycFort) upwardClosureKbSubsetCollections.get(i);
      if (cycAccess.isQuotedIsa(cycFort, collection))
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
    
    if (verbosity > 3)
      Log.current.println("  specificCollections input: " + collections.cyclify());
    CycList result = new CycList();
    for (int i = 0; i < collections.size(); i++) {
      CycFort genlsCollection = (CycFort)collections.get(i);
      boolean genlsOf = false;
      for (int j = 0; j < collections.size(); j++) {
        CycFort specCollection = (CycFort)collections.get(j);
        if (i != j) {
          if (verbosity > 6)
            Log.current.println("  genlsCollection? " + genlsCollection + " specCollection? " + specCollection);
          if (cycAccess.isGenlOf(genlsCollection, specCollection)) {
            genlsOf = true;
            if (verbosity > 4)
              Log.current.println("  collection " + genlsCollection + " genls of " + specCollection + " and is dropped");
            break;
          }
        }
      }
      if (!genlsOf)
        result.add(genlsCollection);
    }
    if (verbosity > 3)
      Log.current.println("  specificCollections output: " + result.cyclify());
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
    
    if (verbosity > 4)
      Log.current.println("\npreparing the selected assertions\n");
    final int selectedAssertions_size = selectedAssertions.size();
    final HashSet predicateSet = new  HashSet();
    final HashSet termSet = new  HashSet();
    for (int i = 0; i < selectedAssertions_size; i++) {
      final CycList assertionELFormula = (CycList) selectedAssertions.get(i);
      if (verbosity > 4)
        Log.current.println("considering " + assertionELFormula.cyclify());
      assert assertionELFormula.size() == 3 : "selected asserion must be a binary gaf " + assertionELFormula.cyclify();
      final CycObject predicate = (CycObject) assertionELFormula.first();
      if (! predicateSet.contains(predicate)) {
        if (predicate instanceof CycList) {
          termSet.add(predicate);
          if (verbosity > 1)
            Log.current.println("adding naut predicate " + predicate.cyclify());
        }
        else if (predicate instanceof CycNart) {
          termSet.add(predicate);
          if (verbosity > 1)
            Log.current.println("adding nart predicate " + predicate.cyclify());
        }
        else if (verbosity > 1)
          Log.current.println("adding predicate " + predicate.cyclify());
        predicateSet.add(predicate);
      }
      final CycObject arg1Term = (CycObject) assertionELFormula.second();
      if (! termSet.contains(arg1Term)) {
        if (verbosity > 4)
          Log.current.println("adding term " + arg1Term.cyclify());
        termSet.add(arg1Term);
      }
    }
    for (int i = 0; i < selectedAssertions_size; i++) {
      final CycList assertionELFormula = (CycList) selectedAssertions.get(i);
      final Object arg2Term = assertionELFormula.third();
      if ((arg2Term instanceof CycFort  || 
          (arg2Term instanceof CycList && ! cycAccess.isa((CycList) arg2Term, cycAccess.getKnownConstantByName("Date")))) &&
          ! cycAccess.isCollection(arg2Term)) {
        if (! termSet.contains(arg2Term)) {
          if (verbosity > 4)
            Log.current.println("adding term " + ((CycObject) arg2Term).cyclify());
          termSet.add(arg2Term);
          termsWithNoExtraProperties.add(arg2Term);
        }
      }
    }
    
    applicableBinaryPredicates = new CycList();
    final Iterator predicateSet_iter = predicateSet.iterator();
    while (predicateSet_iter.hasNext()) {
      final CycObject predicate = (CycObject) predicateSet_iter.next();
      if (verbosity > 8)
        Log.current.println("applicable predicate " + predicate.cyclify());
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
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
