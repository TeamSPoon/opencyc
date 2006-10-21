
package org.opencyc.parser;

//// Internal Imports
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

//// External Imports
import java.io.*;
import java.util.*;

/**
 * <P>CycLParserUtil is designed to be the main entry point into parsing
 * CycL expressions.
 *
 * @version $Id$
 * @author Tony Brusseau
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
public class CycLParserUtil {
  
  //// Constructors
  
  /** Creates a new instance of CycLParserUtil. */
  private CycLParserUtil() {}
  
  //// Public Area
  
  public static Object parseCycLTerm(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLTerm(new StringReader(toParse), testForEOF, access);
  }
  
  public static Object parseCycLTerm(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return completeConstants(parser.term(testForEOF), access);
  }
  
  public static CycList parseCycLTermList(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLTermList(new StringReader(toParse), testForEOF, access);
  }
  
  public static CycList parseCycLTermList(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return (CycList)completeConstants(parser.termList(testForEOF), access);
  }
  
  public static CycObject parseCycLSentence(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLSentence(new StringReader(toParse), testForEOF, access);
  }
  
  public static CycObject parseCycLSentence(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return (CycObject)completeConstants(parser.sentence(testForEOF), access);
  }
  
  public static String parseCycLString(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  TokenMgrError {
    return parseCycLString(new StringReader(toParse), testForEOF, access);
  }
  
  public static String parseCycLString(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException,
  TokenMgrError{
    CycLParser parser = new CycLParser(reader, access);
    return parser.string(testForEOF);
  }
  
  public static Number parseCycLNumber(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  TokenMgrError {
    return parseCycLNumber(new StringReader(toParse), testForEOF, access);
  }
  
  public static Number parseCycLNumber(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return parser.number(testForEOF);
  }
  
  public static CycConstant parseCycLConstant(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLConstant(new StringReader(toParse), testForEOF, access);
  }
  
  public static CycConstant parseCycLConstant(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return (CycConstant)completeConstants(parser.constant(testForEOF), access);
  }
  
  public static CycVariable parseCycLVariable(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  TokenMgrError {
    return parseCycLVariable(new StringReader(toParse), testForEOF, access);
  }
  
  public static CycVariable parseCycLVariable(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return parser.variable(testForEOF);
  }
  
  public static Object parseCycLDenotationalTerm(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLDenotationalTerm(new StringReader(toParse), testForEOF, access);
  }
  
  public static Object parseCycLDenotationalTerm(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return (Object)completeConstants(parser.denotationalTerm(testForEOF), access);
  }
  
  public static Object parseCycLNonAtomicDenotationalTerm(String toParse, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    return parseCycLNonAtomicDenotationalTerm(new StringReader(toParse), testForEOF, access);
  }
  
  public static Object parseCycLNonAtomicDenotationalTerm(Reader reader, boolean testForEOF, CycAccess access) 
  throws ParseException, IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException, TokenMgrError {
    CycLParser parser = new CycLParser(reader, access);
    return (Object)completeConstants(parser.nonAtomicDenotationalTerm(testForEOF), access);
  }
  
  public static Object completeConstants(Object obj, CycAccess access) 
  throws IOException, CycApiException, CycApiServerSideException, 
  InvalidConstantNameException, InvalidConstantGuidException, 
  UnsupportedVocabularyException {
    List allConstants = DefaultCycObject.getReferencedConstants(obj);
    if ((allConstants == null) || (allConstants.size() == 0)) { return obj; }
    List namelessConstants = null;
    List guidlessConstants = null;
    for ( Iterator iter = allConstants.iterator(); iter.hasNext(); ) {
      CycConstant curConst = (CycConstant)iter.next();
      if ((curConst.name == null) && (curConst.guid == null)) {
        throw new IllegalArgumentException("Can't deal with completely bare constants.");
      }
      if (curConst.name == null) { 
        if (namelessConstants == null) { namelessConstants = new ArrayList(); }
        namelessConstants.add(curConst);
      }
      if (curConst.guid == null) { 
        if (guidlessConstants == null) { guidlessConstants = new ArrayList(); }
        guidlessConstants.add(curConst);
      }
    }
    if (namelessConstants != null) {
      access.obtainConstantNames(namelessConstants);
    }
    if (guidlessConstants != null) {
      access.obtainConstantGuids(guidlessConstants);
    }
    InvalidConstantGuidException icge = null;
    InvalidConstantNameException icne = null;
    for ( Iterator iter = allConstants.iterator(); iter.hasNext(); ){
      CycConstant curConst = (CycConstant)iter.next();
      if (curConst.safeGetName() == null) {
        if (icge == null) { icge = new InvalidConstantGuidException(); }
        icge.addInvalidConstantGuid(curConst.guid);
      }
      if (curConst.safeGetGuid() == null) {
        if (icne == null) { icne = new InvalidConstantNameException(); }
        icne.addInvalidConstantName(curConst.name);
      }
      if (access.sublQuoteFnConst.guid.equals(curConst.safeGetGuid())) {
        throw new UnsupportedVocabularyException(access.sublQuoteFnConst);
      }
      if (access.expandSubLFnConst.guid.equals(curConst.safeGetGuid())) {
        throw new UnsupportedVocabularyException(access.expandSubLFnConst);
      }
    }
    if (icne != null) { throw icne; }
    if (icge != null) { throw icge; }
    return obj;
  }
  
  //// Protected Area
  
  protected static void sanityCheck() {
    try {
      CycAccess access = new CycAccess(TEST_CYC_MACHINE, TEST_CYC_PORT);
      Object obj = parseCycLConstant("Dog", true, access);
      System.out.println("Got result: " + obj);
      obj = parseCycLConstant("#G\"bd590573-9c29-11b1-9dad-c379636f7270\"", true, access);
      System.out.println("Got result: " + obj);
      try {
        obj = parseCycLConstant("Dogqweqr", true, access);
        System.out.println("Got result: " + obj);
      } catch (Exception e) { e.printStackTrace(System.out); }
      try {
        obj = parseCycLConstant("#G\"bd590573-9c29-11b1-9dad-c379636f7279\"", true, access);
        System.out.println("Got result: " + obj);
      } catch (Exception e) { e.printStackTrace(System.out); }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Finished abnormally");
      System.exit(-1);
    }
  }
  
  //// Private Area
  
  //// Internal Reader
  
  private static String TEST_CYC_MACHINE = "localhost";
  
  private static int TEST_CYC_PORT = 3640;
  
  //// Main
  
  public static void main(String[] args) {
    System.out.println("Starting");
    try {
      sanityCheck();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Finished abnormally");
      System.exit(-1);
    }
    System.out.println("Finished");
    System.exit(0);
  }
  
}
