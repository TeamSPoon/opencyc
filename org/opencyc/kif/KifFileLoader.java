package org.opencyc.kif;

import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.awt.*;

import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.inferencesupport.*;
import org.opencyc.util.*;
import org.opencyc.xml.*;
import ViolinStrings.*;



/**
 * Provides the behavior and attributes of OpenCyc KifFileLoader.<p>
 * <p>
 * Assertions are read from a file load(File file) or added in via addOrder()
 * The assertions may then be added to a OpenCyc server in the given setdefaultMt
 *
 *   Most of the routines are based on StreamTokenizer with settings:
 *
 *	commentChar(';');
 *      quoteChar('"');
 *	eolIsSignificant(false);
 *
 *   Unless the public boolean load(StreamTokenizer st) is dirrectly called
 *
 * @version $Id$
 * @author Douglas R. Miles
 *
 */

public class KifFileLoader extends CycBulkAssertions {

    /**
     * Constructs a new KifFileLoader.
     *
     * @param ca the CycAssertionsFactory for this KifFileLoader
     */

    public KifFileLoader(CycAssertionsFactory ca) throws Exception {
	super(ca);
	globalFlags = new ArrayList();
    }


    /**
     * Verbosity indicator <tt>0</tt> indicates quiet on a range of
     * <tt>0</tt> ... <tt>10</tt>
     */
    public static int verbosity = 0;

    /**
     * Cyc api support.
     */
    protected CycAssertionsFactory CycAssertionsFactory;
    /*
     *  Assertion SubCollections
     */

    protected ArrayList cycAssertionsDefintional;
    protected ArrayList cycAssertionsPredicateDefs;
    protected ArrayList cycKifFileLoaderDefs;
    protected ArrayList cycAssertionsSpecialCollectionDefs;
    protected ArrayList cycAssertionsSecondaryConstantDefs;
    protected ArrayList cycAssertionsOfRest;
    protected ArrayList kifToCycLMap;
    protected ArrayList cycUnOrderedAssertions;


    /*
     *  The isSuoKif flag (defaulted to false) tells this AssertionCollection
     *  to try to convert some constants and expressions that may map to CycL from IEEE SUO-KIF
     *
     *  Examples:
     *    subclass -> genls
     *    documentation -> comment
     * 
     */

    public boolean isSuoKif = true;
    public boolean isOrderedLoad = true;

    protected boolean isAssertedDuringLoad = false;

    /**
     * Adds the File to KB in defaultMt.
     *
     * @param file for sentences for this KifFileLoader
     */
    public void addFile(Writer feedbackMl,File file)  throws Exception  {
	addFile( feedbackMl,file, defaultMt);
    }

    /**
     * Adds the File to KB into Mt.
     *
     * @param file for sentences 
     * @param mt for sentences 
     */
    public void addFile(Writer feedbackMl,File file, String mt)  throws Exception  {
	setDefaultMt(mt);
	resetAll();
	load(file);
	commitAssertions( feedbackMl);
    }

    /**
     * Adds the File to KB into Mt.
     *
     * @param file for sentences 
     * @param mt for sentences 
     */
    public void addFile(Writer feedbackMl,File file, CycFort mt)  throws Exception  {
	setDefaultMt(mt);
	resetAll();
	load(file);
	commitAssertions(feedbackMl);
    }

    /**
     * Adds the File to KB into Mt.
     *
     * @param file for sentences 
     * @param mt for sentences 
     */
    public void addFileEachLine(Writer feedbackMl,File file, String mt)  throws FileNotFoundException,IOException {
	PrintWriter fb = null;
	if ( feedbackMl==null ) {
	    fb = new PrintWriter(System.out);
	} else {
	    fb = new PrintWriter(feedbackMl);
	}
	CycFort cycmt = null;
	try {
	    cycmt = setDefaultMt(mt);
	} catch ( Exception io ) {
	    fb.println("setDefaultMt");
	    io.printStackTrace(fb);
	}
	BufferedReader br = new BufferedReader(new FileReader(file));
	File tempfile = null;
	tempfile = tempfile.createTempFile(mt,"tmp");
	PrintWriter pw = new PrintWriter(new FileOutputStream(tempfile));
	String line = null;
	try {
	    while ( (line = br.readLine())!=null ) {
		line = line.trim();
		if ( !line.startsWith(";") ) {
		    line = Strings.change(" " + line,"("," ( ");
		    line = Strings.change(line,")"," ) ");
		    line = Strings.change(line,"     "," ");
		    line = Strings.change(line,"   "," ");
		    line = Strings.change(line,"  "," ");
		    line = Strings.change(line,"  "," ");
		    line = Strings.change(line,"<=>"," #$equiv ");
		    line = Strings.change(line,"=>"," #$implies ");
		    line = Strings.change(line," forall "," #$forAll ");
		    line = Strings.change(line,"="," #$equals ");
		    line = Strings.change(line," exists "," #$thereExists ");
		    line = Strings.change(line," @ROW "," ?ROWVAR1 ?ROWVAR2 ");
		    pw.print(" " + line);
		}
	    }
	    br.close();
	    pw.close();
	  

	} catch ( IOException e ) {
	    fb.println("Converting File");
	    e.printStackTrace(fb);
	}

	StreamTokenizer st = new StreamTokenizer(new BufferedReader(new FileReader(tempfile)));
	st.commentChar( ';' ); st.ordinaryChar( '(' ); st.ordinaryChar( ')' ); st.ordinaryChar( '\'' );
	st.ordinaryChar( '`' );  st.ordinaryChar( '.' ); st.wordChars( '=', '=' ); st.wordChars( '+', '+' );
	st.wordChars( '-', '-' );  st.wordChars( '_', '_' ); st.wordChars( '<', '<' ); st.wordChars( '>', '>' );
	st.wordChars( '*', '*' );  st.wordChars( '/', '/' );  st.wordChars( '.', '.' );  st.wordChars( '#', '#' );
	st.wordChars( ':', ':' );  st.wordChars( '!', '!' );  st.wordChars( '$', '$' );  st.wordChars( '?', '?' );
	st.wordChars( '%', '%' );  st.wordChars( '&', '&' );  st.quoteChar('"');  st.eolIsSignificant(false);

	CycListKifParser klp = new CycListKifParser(cycAccess);
	CycList sentence = null;

	while ( st.ttype != st.TT_EOF) {
	    try {
		sentence = klp.read(st);
		doLine(sentence);
		//System.out.println(sentence.cyclify());
		fb.println("<font color=green>"+sentence+"</font>");
	    } catch ( Exception e ) {
		fb.println("<font color=red>"+e+"</font>");
	      //  e.printStackTrace();
	    }
	}
	st = null;
	tempfile.delete();
    }

    public void doLine(CycList sentence) throws Exception {
	if ( sentence==null ) return;
	if ( sentence.first() instanceof CycSymbol ) {
	    if ( sentence.first().equals(new CycSymbol(":MT")) ) {
		setDefaultMt(sentence.second().toString());
		return;
	    }
	    if ( sentence.first().equals(new CycSymbol(":SET")) ) {
		setGlobalFlag(sentence.second().toString());
		return;
	    }
	    if ( sentence.first().equals(new CycSymbol(":UNSET")) ) {
		unsetGlobalFlag(sentence.second().toString());
		return;
	    }
	    return; 
	}

	if (defaultMt==null) throw new CycApiException("defaultMt null;");

	if ( getGlobalFlag(":FORWARD") ) {
	    cycAccess.converseVoid(
				  "(clet ((*the-cyclist* #$CycAdministrator))\n" +
				  "   (without-wff-semantics (cyc-assert\n" +
				  "    '" + sentence.cyclify() + "\n" +
				  "    " + defaultMt.cyclify() + " '(:DIRECTION :FORWARD :STRENGTH :MONOTONIC) )))");
	} else {
	    cycAccess.converseVoid(
				  "(clet ((*the-cyclist* #$CycAdministrator))\n" +
				  "   (without-wff-semantics (cyc-assert\n" +
				  "    '" + sentence.cyclify() + "\n" +
				  "    " + defaultMt.cyclify() + ")))");
	}

    }


    private ArrayList globalFlags = new ArrayList();
    public void setGlobalFlag(String flag) {
	if ( !globalFlags.contains(flag) ) globalFlags.add(flag);
    }
    public void unsetGlobalFlag(String flag) {
	if ( globalFlags.contains(flag) ) globalFlags.remove(flag);
    }
    public boolean getGlobalFlag(String flag) {
	return globalFlags.contains(flag);
    }
    /**
     * Adds the File to this AssertionCollection.
     *
    * @param file to load sentences for this KifFileLoader
     */

    public void load(File file) throws Exception {
	load((Reader)new FileReader(file));
    }

    /**
     * Adds the contents of the InputStream to this AssertionCollection.
     *
    * @param is for InputStream of sentences for this KifFileLoader
     */
    public void load(InputStream is) throws Exception {
	load((Reader)new InputStreamReader(is)); 
    }

    /**
     * Adds the contents of the Reader to this AssertionCollection.
     *
     * @param r for Reader of sentences for this KifFileLoader
     */

    public void load(Reader r) throws Exception {
	if ( kifToCycLMap == null )  kifToCycLMap = new ArrayList();
	BufferedReader br = new BufferedReader(r);
	StringBuffer sb = new StringBuffer("");
	String line;
	String[] findreplace;

	while ( (line=br.readLine()) != null ) {
	    line = line.trim() + " ";
	    if ( !line.startsWith(";") ) {
		if ( isSuoKif ) {
		    line = Strings.change(" " + line,"("," ( ");
		    line = Strings.change(line,")"," ) ");
		    line = Strings.change(line,"     "," ");
		    line = Strings.change(line,"   "," ");
		    line = Strings.change(line,"  "," ");
		    line = Strings.change(line,"  "," ");
		    line = Strings.change(line," <=> "," #$equiv ");
		    line = Strings.change(line," => "," #$implies ");
		    line = Strings.change(line," forall "," #$forAll ");
		    line = Strings.change(line," = "," #$equals ");
		    line = Strings.change(line," exists "," #$thereExists ");
		    line = Strings.change(line," @ROW "," ?ROWVAR1 ?ROWVAR2 ");
		    Iterator it = kifToCycLMap.iterator();
		    while ( it.hasNext() ) {
			findreplace = (String[]) it.next();
			line = Strings.change(line," " + findreplace[0] + " " ," " + findreplace[1] + " ");
		    }
		}
		sb.append(line).append(" ");
		// System.out.println(line);
	    }
	}

	//  // System.out.println(sb.toString());
	load(new StreamTokenizer(new StringReader(sb.toString())));
    }

    /**
     * Adds the contents of the StreamTokenizer to this AssertionCollection.
     * If loading a KIF file be sure first to:
     *
     *	st.commentChar(';');
     *      st.quoteChar('"');
     *	st.eolIsSignificant(false);
     *
     * @param st for sentences for this KifFileLoader
     */

    public void load(StreamTokenizer st) throws Exception {
	st.commentChar( ';' );   st.ordinaryChar( '(' ); st.ordinaryChar( ')' ); st.ordinaryChar( '\'' );
	st.ordinaryChar( '`' );  st.ordinaryChar( '.' ); st.wordChars( '=', '=' ); st.wordChars( '+', '+' );
	st.wordChars( '-', '-' );  st.wordChars( '_', '_' ); st.wordChars( '<', '<' ); st.wordChars( '>', '>' );
	st.wordChars( '*', '*' );  st.wordChars( '/', '/' );  st.wordChars( '.', '.' );  st.wordChars( '#', '#' );
	st.wordChars( ':', ':' );  st.wordChars( '!', '!' );  st.wordChars( '$', '$' );  st.wordChars( '?', '?' );
	st.wordChars( '%', '%' );  st.wordChars( '&', '&' );  st.quoteChar('"');  st.eolIsSignificant(false);
	while ( st.ttype != st.TT_EOF )
	    addSentence((new CycListKifParser(cycAccess)).read(st));
    }

    public ArrayList getKifCycLMap() {
	if ( kifToCycLMap == null )  kifToCycLMap = new ArrayList();
	return kifToCycLMap;
    }

    public void setKifCycLMap(ArrayList kifmap) {
	if ( kifmap == null )  kifmap = new ArrayList();
	kifToCycLMap = kifmap;
    }

    public void clearKifCycLMap() {
	if ( kifToCycLMap == null )  kifToCycLMap = new ArrayList();
	kifToCycLMap.clear();
    }

    public void addKifCycLMap(String kif, String cycl) {
	if ( kifToCycLMap == null )  kifToCycLMap = new ArrayList();
	String[] vect = {kif,cycl};
	kifToCycLMap.add(vect);
    }

    public void makeStdSUOKIFMap() {
	/*
	addKifCycLMap("equal","equals");
	addKifCycLMap("domainSubclass","argGenl");
	addKifCycLMap("domain","argIsa");
	addKifCycLMap("rangeSubclass","resultGenl");
	addKifCycLMap("range","resultIsa");
	addKifCycLMap("instance","isa");
	addKifCycLMap("subclass","genls");
	addKifCycLMap("documentation","comment");
	addKifCycLMap("Attribute","AttributeValue");
	addKifCycLMap("Collection","KIFcollection");
	addKifCycLMap("Class","Collection");
	addKifCycLMap("KIFcollection","Group");
	addKifCycLMap("Object","Indivigual");
	addKifCycLMap("Entity","Thing");                
	addKifCycLMap("subrelation","genlPreds");                
	addKifCycLMap("inverse","genlInverse");                
	addKifCycLMap("Formula","ELFormula");                
	addKifCycLMap("valence","arity");                
	addKifCycLMap("subAttribute","genlAttributes");   
	addKifCycLMap("agent","actors");   
	addKifCycLMap("holds"," ");   
	addKifCycLMap("Process","Event");   
	addKifCycLMap("MegaByte","Megabyte");  
	addKifCycLMap("SymbolicString","TextString"); 
	*/ 
    }


    /**
     * Adds the sentence (String) to this AssertionCollection.
     *
     * @param String of the sentence for this KifFileLoader
     */
    public boolean addSentence(String sentence) throws Exception {
	if ( sentence==null ) return false;
	load(new StringReader(sentence));
	return true;
    }

    /**
     * Adds the AssertionCollection to Mt
    *
    * @param mt for sentences for this KifFileLoader
    *
    * [08:09] <eca-home> All microtheory definitions 
    * [08:09] <eca-home>  isa and other definitional assertions) second.
    * [08:10] <eca-home> All predicate definitions (isa, arity, argIsa, and other definitional assertions) third.
    * [08:11] <eca-home> All collection definitions, in descending order of type, fourth.
    * [08:11] <eca-home> e.g. ThirdOrderCollections, then SecondOrderCollections (CollectionTypes), then Collections which genls Individual.
    * [08:11] <eca-home> All other constant definitions fifth
    * [08:11] <eca-home> Then all other assertions -- the non-definitional ones.
    * (and (isa ?GAF CycLClosedAtomicSentence) (operatorSentences ?PRED ?GAF) (isa ?PRED OpenCycDefinitionalPredicate))
     */

    public void orderAssertions() {
	try {

	    if ( isOrderedLoad ) {
		cycOrderedAssertions = cycUnOrderedAssertions;
	    } else {
		Iterator unordered = cycUnOrderedAssertions.iterator();
		while ( unordered.hasNext() ) addOrder((CycList)unordered.next());
		cycOrderedAssertions = new ArrayList();
		concatArrayList(cycOrderedAssertions,cycKifFileLoaderDefs);
		concatArrayList(cycOrderedAssertions,cycAssertionsDefintional);
		concatArrayList(cycOrderedAssertions,cycAssertionsPredicateDefs);
		concatArrayList(cycOrderedAssertions,cycAssertionsSpecialCollectionDefs);
		concatArrayList(cycOrderedAssertions,cycAssertionsSecondaryConstantDefs);
		concatArrayList(cycOrderedAssertions,cycAssertionsOfRest);
	    }
	} catch ( Exception e ) {
	    System.err.println(e);
	}

    }


    /**
     * Adds the sentence (CycList) to this AssertionCollection.
     *
     * @param (CycList) of the sentence for this KifFileLoader
     */

    public boolean addOrder(CycList sentence) throws Exception {
	if ( sentence==null ) return false;

	if ( isOrderedLoad ) {
	    cycAssertionsOfRest.add(sentence);
	    return true;
	}

	/* ArrayList simpler = ConstraintRule.simplifyConstraintRuleExpression(sentence);
	   if ( simpler.size()>1 ) {
	   Iterator simpleIterations = simpler.iterator();
	   while ( simpleIterations.hasNext() )
	   addOrder((CycList) simpleIterations.next());
	   return;
	   } */

	// Variables cause this sentence to be asserted last 
	if ( !(new ConstraintRule(sentence).isGround()) ) return addOrderNonGround(sentence);

	Object cycPredicate = sentence.first();

	// Predicate argument is not a constant cause (Can assume only Gafs beyond here)
	if ( !(cycPredicate instanceof CycFort) )  return addOrderNonFortPred(sentence);

	CycFort cycFortPredicate = (CycFort) cycPredicate;

	if ( cycFortPredicate.equals(CycAssertionsFactory.genlMt) ) return  addOrderGenlMt(sentence);

	if ( cycFortPredicate.equals(CycAssertionsFactory.isa) )  return addOrderIsa(sentence);

	if ( cycFortPredicate.equals(CycAssertionsFactory.genls) ) return  addOrderGenls(sentence);

	if ( cycFortPredicate.equals(CycAssertionsFactory.comment) )  return addOrderComment(sentence);

	if ( CycAssertionsFactory.isMetaRelation(cycFortPredicate ) )
	    return addOrderMetaRelation(cycFortPredicate.cyclify(), sentence);

	// Since Gaf add it to first part of cycAssertionsOfRest
	return addOrderGround(sentence);
    }


    /**
     * Adds the MetaRelation based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderGenlMt(CycList sentence) {
	// System.out.println("cycAssertionsForBaseKB: " + sentence.cyclify());
	cycAssertionsForBaseKB.add(sentence);
	return true;
    }

    /**
     * Adds the MetaRelation based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderMetaRelation(String cycPredicate, CycList sentence) throws Exception  {
	/*
	try {
	if (cycPredicate.equals("#$argIsa") || cycPredicate.equals("#$argGenl")) {
	 //   addIsa((CycConstant)sentence.second(),"#$Relation");
	addCollection((CycConstant)sentence.fourth());
	}
	if (cycPredicate.equals("#$resultIsa") || cycPredicate.equals("#$resultGenl")) {
	addFunction((CycConstant)sentence.second());
	addCollection((CycConstant)sentence.third());
	}
	if (cycPredicate.equals("#genlInverse") || cycPredicate.equals("#genlPreds")) {
	addPredicate((CycConstant)sentence.second());
	addPredicate((CycConstant)sentence.third());
	}
	} catch ( Exception e) {
	
	}
	 */   
	cycAssertionsPredicateDefs.add(sentence);
	// System.out.println("addOrderMetaRelation: " + sentence.cyclify());

	return true;
    }

    /**
     * Adds the gaf based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderNonFortPred(CycList sentence) {
	cycAssertionsOfRest.add(sentence);
	// System.out.println("addOrderNonFortPred: " + sentence.cyclify());
	return true;
    }


    /**
     * Adds the gaf based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderGround(CycList sentence) throws Exception {
	Iterator args = sentence.iterator();
	CycConstant pred = (CycConstant) args.next();
	addPredicate(pred);

	cycAssertionsOfRest.add(0,sentence);
	// System.out.println("addOrderGround:"  + sentence.cyclify());
	return true;
    }
    /**
     * Adds the non gaf based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderNonGround(CycList sentence) throws Exception {
	cycAssertionsOfRest.add(sentence);
	// System.out.println("addOrderNonGround:" + sentence.cyclify());
	return true;
    }
    /**
     * Adds the comment based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderComment(CycList sentence) throws Exception {

	cycAssertionsDefintional.add(sentence);
	// System.out.println("addOrderComment: "  + sentence.cyclify());
	return true;
    }

    /**
     * Adds the genls based sentence (CycList) to this AssertionCollection.
     *
     * @param sentence (CycList) to be added to this KifFileLoader
     */
    public boolean addOrderGenls(CycList sentence) throws Exception {
	addCollection((CycConstant)sentence.second());
	addCollection((CycConstant)sentence.third());
	cycAssertionsDefintional.add(sentence);
	return true;
    }

    public void addCollection(CycConstant collection)   throws Exception {
	cycKifFileLoaderDefs.add(CycAssertionsFactory.makeGaf(CycAssertionsFactory.isa,collection,CycAssertionsFactory.collection));
    }

    public void addPredicate(CycConstant predicate)   throws Exception {
	addIsa(predicate,"#$Predicate");
    }

    public void addIsa(CycConstant term, String type)   throws Exception {
	cycKifFileLoaderDefs.add(CycAssertionsFactory.makeIsa(term,type));
    }


    /**
     * Adds the isa based sentence (CycList) to this AssertionCollection.
     *
     * @param (CycList) of the sentence for this KifFileLoader
     */

    public boolean addOrderIsa(CycList sentence) throws Exception {
	try {
	    Object cycCollection = sentence.third();

	    // System.out.println("addOrderIsa: " + sentence.cyclify());

	    if ( !(cycCollection instanceof CycConstant) ) {
		cycAssertionsOfRest.add(sentence);
		return true;
	    }

	    // Is this a Mt Defintion?
	    if ( cycCollection.equals(CycAssertionsFactory.microtheory) ) {
		cycAssertionsForBaseKB.add(0,sentence);
		return true;
	    }

	    addCollection((CycConstant)sentence.third());
	    cycAssertionsDefintional.add(sentence);

	    // Is this a Collection Defintion?
	    if ( cycCollection.equals(CycAssertionsFactory.getConstantByName("#$Collection")) ) {
		addCollection((CycConstant)sentence.second());
		return true;
	    }

	    // Is this already a Known Collection?
	    if ( CycAssertionsFactory.isa((CycFort) cycCollection,(CycFort) CycAssertionsFactory.collection) ) {
		cycAssertionsDefintional.add(0,sentence);
		return true;
	    }

	    // This is not a known about collection yet
	    cycAssertionsDefintional.add(sentence);
	} catch ( Exception e ) {
	}

	return true;

    }


    public static void concatArrayList(ArrayList holder, ArrayList list) {
	if ( list == null ) return;
	Iterator listit = list.iterator();

	while ( listit.hasNext() ) {
	    Object insert  = listit.next();
	    if ( !holder.contains(insert) )
		holder.add(insert);
	}
    }


}




