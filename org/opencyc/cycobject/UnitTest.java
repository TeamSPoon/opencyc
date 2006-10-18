package org.opencyc.cycobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycConnection;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.util.CycUtils;
import org.opencyc.util.MyStreamTokenizer;
import org.opencyc.xml.Marshaller;
import org.opencyc.xml.XMLStringWriter;

/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.cycobject</tt> package.<p>
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
public class UnitTest extends TestCase {
  
  private static String HOSTNAME = CycConnection.DEFAULT_HOSTNAME;
  
  private static int PORT = CycConnection.DEFAULT_BASE_PORT;
  
  /**
   * Main method in case tracing is prefered over running JUnit.
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
    // close any threads left
    System.exit(0);
  }
  /**
   * Construct a new UnitTest object.
   * @param name the test case name.
   */
  public UnitTest(String name) {
    super(name);
  }
  
  /**
   * Runs the unit tests
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testELMTCycList"));
    testSuite.addTest(new UnitTest("testGuid"));
    testSuite.addTest(new UnitTest("testByteArray"));
    testSuite.addTest(new UnitTest("testCycAssertion"));
    testSuite.addTest(new UnitTest("testCycSymbol"));
    testSuite.addTest(new UnitTest("testCycVariable"));
    testSuite.addTest(new UnitTest("testCycConstant"));
    // testSuite.addTest(new UnitTest("testCycNart"));
    testSuite.addTest(new UnitTest("testStreamTokenizer"));
    testSuite.addTest(new UnitTest("testCycList"));
    testSuite.addTest(new UnitTest("testCycListVisitor"));
    testSuite.addTest(new UnitTest("testUnicodeString"));
    testSuite.addTest( new UnitTest("testCycListPrettyStringDetails"));
    return testSuite;
  }
  
  /**
   * Tests the test harness itself.
   */
  public void testTestHarness() {
    System.out.println("\n*** testTestHarness ***");
    Assert.assertTrue(true);
    System.out.println("*** testTestHarness OK ***");
  }
  
  /**
   * Tests <tt>Guid</tt> object behavior.
   */
  public void testGuid() {
    System.out.println("\n*** testGuid ***");
    CycObjectFactory.resetGuidCache();
    Assert.assertEquals(0, CycObjectFactory.getGuidCacheSize());
    String guidString = "bd58c19d-9c29-11b1-9dad-c379636f7270";
    Guid guid = CycObjectFactory.makeGuid(guidString);
    Assert.assertEquals(1, CycObjectFactory.getGuidCacheSize());
    Assert.assertEquals(guidString, guid.toString());
    Guid guid2 = CycObjectFactory.getGuidCache(guidString);
    Assert.assertEquals(guid, guid2);
    Guid guid3 = CycObjectFactory.makeGuid(guidString);
    Assert.assertEquals(guid, guid3);
    Assert.assertEquals(1, CycObjectFactory.getGuidCacheSize());
    
    // toXML, toXMLString, unmarshall
    XMLStringWriter xmlStringWriter = new XMLStringWriter();
    try {
      guid.toXML(xmlStringWriter, 0, false);
      Assert.assertEquals("<guid>bd58c19d-9c29-11b1-9dad-c379636f7270</guid>\n", xmlStringWriter.toString());
      Assert.assertEquals("<guid>bd58c19d-9c29-11b1-9dad-c379636f7270</guid>\n", guid.toXMLString());
      String guidXMLString = guid.toXMLString();
      CycObjectFactory.resetGuidCache();
      Object object = CycObjectFactory.unmarshall(guidXMLString);
      Assert.assertTrue(object instanceof Guid);
      Assert.assertEquals(guid, (Guid) object);
      Assert.assertTrue(CycObjectFactory.unmarshall(guidXMLString) ==
      CycObjectFactory.unmarshall(guidXMLString));
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testGuid OK ***");
  }
  
  /**
   * Tests <tt>CycSymbol</tt> object behavior.
   */
  public void testCycSymbol() {
    System.out.println("\n*** testCycSymbol ***");
    CycObjectFactory.resetCycSymbolCache();
    Assert.assertEquals(4, CycObjectFactory.getCycSymbolCacheSize());
    String symbolName = "WHY-ISA?";
    CycSymbol cycSymbol = CycObjectFactory.makeCycSymbol(symbolName);
    Assert.assertEquals(5, CycObjectFactory.getCycSymbolCacheSize());
    Assert.assertEquals(symbolName, cycSymbol.toString());
    Assert.assertNotNull(CycObjectFactory.getCycSymbolCache(symbolName));
    CycSymbol cycSymbol2 = CycObjectFactory.getCycSymbolCache(symbolName);
    Assert.assertEquals(cycSymbol, cycSymbol2);
    CycSymbol cycSymbol3 = CycObjectFactory.makeCycSymbol(symbolName);
    Assert.assertEquals(cycSymbol, cycSymbol3);
    Assert.assertEquals(5, CycObjectFactory.getCycSymbolCacheSize());
    String symbolName4 = "WHY-ISA?";
    CycSymbol cycSymbol4 = CycObjectFactory.makeCycSymbol(symbolName4);
    Assert.assertEquals(cycSymbol.toString(), cycSymbol4.toString());
    Assert.assertEquals(cycSymbol, cycSymbol4);
    
    // compareTo
    ArrayList symbols = new ArrayList();
    symbols.add(CycObjectFactory.makeCycSymbol("isa?"));
    symbols.add(CycObjectFactory.makeCycSymbol("define-private"));
    symbols.add(CycObjectFactory.makeCycSymbol("nil"));
    Collections.sort(symbols);
    Assert.assertEquals("[DEFINE-PRIVATE, ISA?, NIL]", symbols.toString());
    
    // isKeyword
    CycSymbol cycSymbol5 = CycObjectFactory.makeCycSymbol("nil");
    Assert.assertTrue(! cycSymbol5.isKeyword());
    CycSymbol cycSymbol6 = CycObjectFactory.makeCycSymbol(":pos");
    Assert.assertTrue(cycSymbol6.isKeyword());
    
    // isValidSymbolName
    Assert.assertTrue(CycSymbol.isValidSymbolName("t"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("nil"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("a_"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("a-b"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("a-b"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("a-9b"));
    Assert.assertTrue(CycSymbol.isValidSymbolName("*MY-SYMBOL*"));
    Assert.assertTrue(! CycSymbol.isValidSymbolName(" "));
    Assert.assertTrue(! CycSymbol.isValidSymbolName("#$Brazil"));
    Assert.assertTrue(! CycSymbol.isValidSymbolName("\"a-string\""));
    
    //packages
    CycSymbol symbol7 = new CycSymbol("CYC", "BLAH");
    CycSymbol symbol8 = new CycSymbol("|CYC|", "BLAH");
    CycSymbol symbol9 = new CycSymbol("CYC", "|BLAH|");
    CycSymbol symbol10 = new CycSymbol("|CYC|", "|BLAH|");
    Assert.assertEquals("CYC", symbol7.getPackageName());
    Assert.assertEquals("CYC", symbol8.getPackageName());
    Assert.assertEquals("CYC", symbol9.getPackageName());
    Assert.assertEquals("CYC", symbol10.getPackageName());
    Assert.assertEquals("CYC", symbol7.getPackageNamePrecise());
    Assert.assertEquals("CYC", symbol8.getPackageNamePrecise());
    Assert.assertEquals("CYC", symbol9.getPackageNamePrecise());
    Assert.assertEquals("CYC", symbol10.getPackageNamePrecise());
    Assert.assertEquals("BLAH", symbol7.getSymbolName());
    Assert.assertEquals("BLAH", symbol8.getSymbolName());
    Assert.assertEquals("BLAH", symbol9.getSymbolName());
    Assert.assertEquals("BLAH", symbol10.getSymbolName());
    Assert.assertEquals("BLAH", symbol7.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol8.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol9.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol10.getSymbolNamePrecise());
    Assert.assertEquals(symbol7, symbol8);
    Assert.assertEquals(symbol7, symbol9);
    Assert.assertEquals(symbol7, symbol10);
    Assert.assertEquals("BLAH", symbol7.toString());
    Assert.assertEquals("BLAH", symbol8.toString());
    Assert.assertEquals("BLAH", symbol9.toString());
    Assert.assertEquals("BLAH", symbol10.toString());
    Assert.assertEquals("CYC:BLAH", symbol7.toFullStringForced());
    Assert.assertEquals("CYC:BLAH", symbol8.toFullStringForced());
    Assert.assertEquals("CYC:BLAH", symbol9.toFullStringForced());
    Assert.assertEquals("CYC:BLAH", symbol10.toFullStringForced());
    Assert.assertEquals("CYC:BLAH", symbol7.toFullString("SL"));
    Assert.assertEquals("CYC:BLAH", symbol8.toFullString("SL"));
    Assert.assertEquals("CYC:BLAH", symbol9.toFullString("SL"));
    Assert.assertEquals("CYC:BLAH", symbol10.toFullString("SL"));
    Assert.assertEquals("BLAH", symbol10.toFullString("CYC"));
    Assert.assertFalse(symbol7.isKeyword());
    Assert.assertFalse(symbol8.isKeyword());
    Assert.assertFalse(symbol9.isKeyword());
    Assert.assertFalse(symbol10.isKeyword());
    
    CycSymbol symbol11 = new CycSymbol("|CYC RuLeS|", "|BLAH BiTeS|");
    CycSymbol symbol12 = new CycSymbol("CYC RuLeS", "BLAH BiTeS");
    Assert.assertEquals("CYC RuLeS", symbol11.getPackageName());
    Assert.assertEquals("CYC RuLeS", symbol12.getPackageName());
    Assert.assertEquals("|CYC RuLeS|", symbol11.getPackageNamePrecise());
    Assert.assertEquals("|CYC RuLeS|", symbol12.getPackageNamePrecise());
    Assert.assertEquals("BLAH BiTeS", symbol11.getSymbolName());
    Assert.assertEquals("BLAH BiTeS", symbol12.getSymbolName());
    Assert.assertEquals("|BLAH BiTeS|", symbol11.getSymbolNamePrecise());
    Assert.assertEquals("|BLAH BiTeS|", symbol12.getSymbolNamePrecise());
    Assert.assertEquals(symbol11, symbol12);
    Assert.assertEquals("|BLAH BiTeS|", symbol11.toString());
    Assert.assertEquals("|BLAH BiTeS|", symbol12.toString());
    Assert.assertEquals("|CYC RuLeS|:|BLAH BiTeS|", symbol11.toFullStringForced());
    Assert.assertEquals("|CYC RuLeS|:|BLAH BiTeS|", symbol12.toFullStringForced());
    Assert.assertEquals("|CYC RuLeS|:|BLAH BiTeS|", symbol11.toFullString("SL"));
    Assert.assertEquals("|CYC RuLeS|:|BLAH BiTeS|", symbol12.toFullString("SL"));
    Assert.assertEquals("|BLAH BiTeS|", symbol12.toFullString("CYC RuLeS"));
    Assert.assertFalse(symbol11.isKeyword());
    Assert.assertFalse(symbol12.isKeyword());
    
    CycSymbol symbol13 = new CycSymbol("KEYWORD", "BLAH");
    CycSymbol symbol14 = new CycSymbol("|KEYWORD|", "BLAH");
    CycSymbol symbol15 = new CycSymbol("", ":BLAH");
    CycSymbol symbol16 = new CycSymbol(null, ":BLAH");
    Assert.assertEquals("KEYWORD", symbol13.getPackageName());
    Assert.assertEquals("KEYWORD", symbol14.getPackageName());
    Assert.assertEquals("KEYWORD", symbol15.getPackageName());
    Assert.assertEquals("KEYWORD", symbol16.getPackageName());
    Assert.assertEquals("KEYWORD", symbol13.getPackageNamePrecise());
    Assert.assertEquals("KEYWORD", symbol14.getPackageNamePrecise());
    Assert.assertEquals("KEYWORD", symbol15.getPackageNamePrecise());
    Assert.assertEquals("KEYWORD", symbol16.getPackageNamePrecise());
    Assert.assertEquals("BLAH", symbol13.getSymbolName());
    Assert.assertEquals("BLAH", symbol14.getSymbolName());
    Assert.assertEquals("BLAH", symbol15.getSymbolName());
    Assert.assertEquals("BLAH", symbol16.getSymbolName());
    Assert.assertEquals("BLAH", symbol13.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol14.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol15.getSymbolNamePrecise());
    Assert.assertEquals("BLAH", symbol16.getSymbolNamePrecise());
    Assert.assertEquals(symbol13, symbol14);
    Assert.assertEquals(symbol13, symbol15);
    Assert.assertEquals(symbol13, symbol16);
    Assert.assertEquals(":BLAH", symbol13.toString());
    Assert.assertEquals(":BLAH", symbol14.toString());
    Assert.assertEquals(":BLAH", symbol15.toString());
    Assert.assertEquals(":BLAH", symbol16.toString());
    Assert.assertEquals("KEYWORD:BLAH", symbol13.toFullStringForced());
    Assert.assertEquals("KEYWORD:BLAH", symbol14.toFullStringForced());
    Assert.assertEquals("KEYWORD:BLAH", symbol15.toFullStringForced());
    Assert.assertEquals("KEYWORD:BLAH", symbol16.toFullStringForced());
    Assert.assertEquals(":BLAH", symbol13.toFullString("SL"));
    Assert.assertEquals(":BLAH", symbol14.toFullString("SL"));
    Assert.assertEquals(":BLAH", symbol15.toFullString("SL"));
    Assert.assertEquals(":BLAH", symbol16.toFullString("SL"));
    Assert.assertEquals(":BLAH", symbol16.toFullString("KEYWORD"));
    Assert.assertTrue(symbol13.isKeyword());
    Assert.assertTrue(symbol14.isKeyword());
    Assert.assertTrue(symbol15.isKeyword());
    Assert.assertTrue(symbol16.isKeyword());
    
    // toXML, toXMLString, unmarshall
    XMLStringWriter xmlStringWriter = new XMLStringWriter();
    try {
      cycSymbol6.toXML(xmlStringWriter, 0, false);
      Assert.assertEquals("<symbol>:POS</symbol>\n", xmlStringWriter.toString());
      Assert.assertEquals("<symbol>:POS</symbol>\n", cycSymbol6.toXMLString());
      String cycSymbolXMLString = cycSymbol6.toXMLString();
      Object object = CycObjectFactory.unmarshall(cycSymbolXMLString);
      Assert.assertTrue(object instanceof CycSymbol);
      Assert.assertEquals(cycSymbol6, (CycSymbol) object);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycSymbol OK ***");
  }
  
  /**
   * Tests <tt>CycConstant</tt> object behavior.
   */
  public void testCycConstant() {
    System.out.println("\n*** testCycConstant ***");
    try {
      final CycAccess cycAccess = new CycAccess(HOSTNAME, PORT);
      CycObjectFactory.resetCycConstantCaches();
      Assert.assertEquals(0, CycObjectFactory.getCycConstantCacheByNameSize());
      String guidString = "bd58c19d-9c29-11b1-9dad-c379636f7270";
      String constantName = "#$TameAnimal";
      CycConstant cycConstant1 = new CycConstant(constantName, CycObjectFactory.makeGuid(guidString));
      CycObjectFactory.addCycConstantCache(cycConstant1);
      Assert.assertNotNull(cycConstant1);
      Assert.assertEquals(1, CycObjectFactory.getCycConstantCacheByNameSize());
      Assert.assertEquals(constantName.substring(2), cycConstant1.toString());
      Assert.assertEquals(constantName, cycConstant1.cyclify());
      Assert.assertEquals(guidString, cycConstant1.getGuid().toString());

      // Attempt to create a duplicate returns the cached existing object.
      CycConstant cycConstant2 = new CycConstant(constantName, CycObjectFactory.makeGuid(guidString));
      CycObjectFactory.addCycConstantCache(cycConstant2);
      Assert.assertEquals(1, CycObjectFactory.getCycConstantCacheByNameSize());
      Assert.assertEquals(cycConstant1, cycConstant2);

      CycConstant cycConstant3 = new CycConstant(constantName, CycObjectFactory.makeGuid(guidString));
      CycObjectFactory.addCycConstantCache(cycConstant3);
      Assert.assertEquals(cycConstant1.toString(), cycConstant3.toString());
      Assert.assertEquals(cycConstant1.cyclify(), cycConstant3.cyclify());
      Assert.assertEquals(cycConstant1, cycConstant3);


      // compareTo
      ArrayList constants = new ArrayList();

      constants.add(new CycConstant("#$Dog", CycObjectFactory.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270")));
      constants.add(new CycConstant("#$Cat", CycObjectFactory.makeGuid("bd590573-9c29-11b1-9dad-c379636f7270")));
      constants.add(new CycConstant("#$Brazil", CycObjectFactory.makeGuid("bd588f01-9c29-11b1-9dad-c379636f7270")));
      constants.add(new CycConstant("#$Collection", CycObjectFactory.makeGuid("bd5880cc-9c29-11b1-9dad-c379636f7270")));
      Collections.sort(constants);
      Assert.assertEquals("[Brazil, Cat, Collection, Dog]", constants.toString());

      CycConstant cycConstant4 =
      new CycConstant("#$TransportationDevice-Vehicle", CycObjectFactory.makeGuid("c0bce169-9c29-11b1-9dad-c379636f7270"));

      XMLStringWriter xmlStringWriter = new XMLStringWriter();
      cycConstant4.toXML(xmlStringWriter, 0, false);
      String expectedXML =
      "<constant>\n" +
      "  <guid>c0bce169-9c29-11b1-9dad-c379636f7270</guid>\n" +
      "  <name>TransportationDevice-Vehicle</name>\n" +
      "</constant>\n";
      Assert.assertEquals(expectedXML, xmlStringWriter.toString());
      Assert.assertEquals(expectedXML, cycConstant4.toXMLString());
      String cycConstantXMLString = cycConstant4.toXMLString();
      CycObjectFactory.resetCycConstantCaches();
      Object object = CycObjectFactory.unmarshall(cycConstantXMLString);
      Assert.assertTrue(object instanceof CycConstant);
      Assert.assertEquals(cycConstant4, (CycConstant) object);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycConstant OK ***");
  }
  
  /**
   * Tests <tt>CycNart</tt> object behavior.
   */
  public void testCycNart() {
    System.out.println("\n*** testCycNart ***");
    CycAccess cycAccess = null;
    try {
      cycAccess = new CycAccess(HOSTNAME, PORT);
      CycConstant arityRelationFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bf361058-9c29-11b1-9dad-c379636f7270"));
      CycNart cycNart = new CycNart(arityRelationFn, new Integer(1));
      CycNart arityRelationFn1 = cycNart;
      Assert.assertNotNull(cycNart);
      Assert.assertEquals("(ArityRelationFn 1)",cycNart.toString());
      Assert.assertEquals("(#$ArityRelationFn 1)",cycNart.cyclify());
      
      CycNart cycNart2 = new CycNart(arityRelationFn, new Integer(1));
      Assert.assertEquals(cycNart.toString(), cycNart2.toString());
      Assert.assertEquals(cycNart, cycNart2);
      
      // compareTo
      ArrayList narts = new ArrayList();
      CycConstant yearFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd58f29a-9c29-11b1-9dad-c379636f7270"));
      CycList nartCycList = new CycList();
      nartCycList.add(yearFn);
      nartCycList.add(new Integer(2000));
      CycNart year2K = new CycNart(nartCycList);
      narts.add(year2K);
      Assert.assertEquals("[(YearFn 2000)]", narts.toString());
      CycConstant transportFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("c10afb3b-9c29-11b1-9dad-c379636f7270"));
      CycConstant person =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd588092-9c29-11b1-9dad-c379636f7270"));
      CycList nartCycList2 = new CycList();
      nartCycList2.add(transportFn);
      nartCycList2.add(person);
      narts.add(new CycNart(nartCycList2));
      CycList nartCycList3 = new CycList();
      nartCycList3.add(arityRelationFn);
      nartCycList3.add(new Integer(1));
      narts.add(new CycNart(nartCycList3));
      Collections.sort(narts);
      Assert.assertEquals("[(ArityRelationFn 1), (ConveyFn Person), (YearFn 2000)]",
      narts.toString());
      
      // hasFunctorAndArgs
      Assert.assertTrue(arityRelationFn1.hasFunctorAndArgs());
      Assert.assertTrue(! (new CycNart()).hasFunctorAndArgs());
      
      // toCycList()
      CycList cycList = new CycList();
      cycList.add(arityRelationFn);
      cycList.add(new Integer(1));
      Assert.assertEquals(cycList, arityRelationFn1.toCycList());
      
      
      // check cfasl representation of narts in a list
      CycList myNarts = new CycList();
      myNarts.add(arityRelationFn1);
      CycNart arityRelationFn2 = new CycNart(arityRelationFn, new Integer(2));
      myNarts.add(arityRelationFn2);
      
      for (int i = 0; i < myNarts.size(); i++)
        Assert.assertTrue(myNarts.get(i) instanceof CycNart);
      CycList command = new CycList();
      command.add(CycObjectFactory.makeCycSymbol("csetq"));
      command.add(CycObjectFactory.makeCycSymbol("my-narts"));
      command.addQuoted(myNarts);
      CycList myNartsBackFromCyc = cycAccess.converseList(command);
      for (int i = 0; i < myNartsBackFromCyc.size(); i++) {
        Assert.assertTrue(myNartsBackFromCyc.get(i) instanceof CycNart);
        CycNart myNartBackFromCyc = (CycNart) myNartsBackFromCyc.get(i);
        Assert.assertTrue(myNartBackFromCyc.getFunctor() instanceof CycFort);
        Assert.assertTrue(myNartBackFromCyc.getArguments() instanceof ArrayList);
        ArrayList args = (ArrayList) myNartBackFromCyc.getArguments();
        for (int j = 0; j < args.size(); j++) {
          Object arg = args.get(j);
          Assert.assertTrue(arg instanceof Integer);
        }
        
      }
      
      // coerceToCycNart
      CycNart cycNart4 = new CycNart(arityRelationFn, new Integer(1));
      Assert.assertEquals(cycNart4, CycNart.coerceToCycNart(cycNart4));
      CycList cycList4 = new CycList();
      cycList4.add(arityRelationFn);
      cycList4.add(new Integer(1));
      Assert.assertEquals(cycNart2, CycNart.coerceToCycNart(cycList4));
      
      // toXML, toXMLString
      XMLStringWriter xmlStringWriter = new XMLStringWriter();
      cycNart4.toXML(xmlStringWriter, 0, false);
      System.out.println(xmlStringWriter.toString());
      
      String cycNartXMLString = cycNart4.toXMLString();
      System.out.println("cycNartXMLString\n" + cycNartXMLString);
      Object object = CycObjectFactory.unmarshall(cycNartXMLString);
      Assert.assertTrue(object instanceof CycNart);
      Assert.assertEquals(cycNart4, (CycNart) object);
      
      CycConstant theList = cycAccess.getKnownConstantByGuid("bdcc9f7c-9c29-11b1-9dad-c379636f7270");
      CycNart cycNart5 = new CycNart(theList, new Integer(1), "a string");
      cycNartXMLString = cycNart5.toXMLString();
      System.out.println("cycNartXMLString\n" + cycNartXMLString);
      object = CycObjectFactory.unmarshall(cycNartXMLString);
      Assert.assertTrue(object instanceof CycNart);
      Assert.assertEquals(cycNart5, (CycNart) object);
      
      // Check whether stringApiValue() behaves properly on a NART with a string argument
      CycConstant cityNamedFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd6870a6-9c29-11b1-9dad-c379636f7270"));
      CycConstant ontario =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd58b6d5-9c29-11b1-9dad-c379636f7270"));
      CycNart attawapiskat = new CycNart(cityNamedFn, "Attawapiskat", ontario);
      
      Object result = CycUtils.evalSubLWithWorker(cycAccess, attawapiskat.stringApiValue());
      Assert.assertTrue(result instanceof CycNart);
      Assert.assertEquals(attawapiskat, (CycNart) result);
      
      // Check whether stringApiValue() behaves properly on a NART
      // with a string that contains a character that needs to be escaped in SubL
      CycConstant registryKeyFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("e475c6b0-1695-11d6-8000-00a0c9efe6b4"));
      CycNart hklmSam = new CycNart(registryKeyFn, "HKLM\\SAM");
      
      Object result0 = CycUtils.evalSubLWithWorker(cycAccess, hklmSam.stringApiValue());
      Assert.assertTrue(result0 instanceof CycNart);
      Assert.assertEquals(hklmSam, (CycNart) result0);
      
            /*
            CycAssertion cycAssertion = cycAccess.getAssertionById(new Integer(968857));
            CycNart complexNart = (CycNart) cycAssertion.getFormula().second();
            System.out.println(complexNart.toString());
            System.out.println(complexNart.cyclify());
             */
      cycAccess.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycNart OK ***");
  }
  
  /**
   * Tests <tt>CycVariable</tt> object behavior.
   */
  public void testCycVariable() {
    System.out.println("\n*** testCycVariable ***");
    CycVariable cycVariable1 = new CycVariable("?X");
    Assert.assertNotNull(cycVariable1);
    Assert.assertEquals("?X", cycVariable1.toString());
    Assert.assertEquals("?X", cycVariable1.cyclify());
    Assert.assertEquals("'?X", cycVariable1.stringApiValue());
    CycVariable cycVariable2 = new CycVariable("?variable");
    Assert.assertNotNull(cycVariable2);
    Assert.assertEquals("?variable", cycVariable2.toString());
    Assert.assertEquals("?variable", cycVariable2.cyclify());
    Assert.assertEquals("'?variable", cycVariable2.stringApiValue());
    CycVariable cycVariable3 = new CycVariable("?X");
    Assert.assertEquals(cycVariable1.toString(), cycVariable3.toString());
    Assert.assertEquals(cycVariable1.cyclify(), cycVariable3.cyclify());
    Assert.assertEquals(cycVariable1.stringApiValue(), cycVariable3.stringApiValue());
    Assert.assertEquals(cycVariable1, cycVariable3);
    
    // compareTo
    ArrayList variables = new ArrayList();
    variables.add(CycObjectFactory.makeCycVariable("?y"));
    variables.add(CycObjectFactory.makeCycVariable("?Z"));
    variables.add(CycObjectFactory.makeCycVariable("?Y"));
    variables.add(CycObjectFactory.makeCycVariable("?X"));
    variables.add(CycObjectFactory.makeCycVariable("?z"));
    variables.add(CycObjectFactory.makeCycVariable("?x"));
    Collections.sort(variables);
    Assert.assertEquals("[?X, ?Y, ?Z, ?x, ?y, ?z]", variables.toString());
    CycVariable cycVariable1000 = new CycVariable(":X");
    Assert.assertNotSame(cycVariable1, cycVariable1000);
    
    // makeUniqueCycVariable
    CycVariable x = CycObjectFactory.makeCycVariable("?x");
    CycVariable x1 = CycObjectFactory.makeUniqueCycVariable(x);
    CycVariable x2 = CycObjectFactory.makeUniqueCycVariable(x);
    CycVariable x3 = CycObjectFactory.makeUniqueCycVariable(x);
    Assert.assertTrue(! (x.equals(x1)));
    Assert.assertTrue(! (x.equals(x2)));
    Assert.assertTrue(! (x.equals(x3)));
    Assert.assertTrue(! (x1.equals(x2)));
    Assert.assertTrue(x.cyclify().equals("?x"));
    Assert.assertTrue(x1.cyclify().startsWith("?x_"));
    Assert.assertTrue(x3.cyclify().startsWith("?x_"));
    
    // toXML, toXMLString, unmarshall
    XMLStringWriter xmlStringWriter = new XMLStringWriter();
    try {
      x.toXML(xmlStringWriter, 0, false);
      Assert.assertEquals("<variable>x</variable>\n", xmlStringWriter.toString());
      Assert.assertEquals("<variable>x</variable>\n", x.toXMLString());
      String cycVariableXMLString = x.toXMLString();
      CycObjectFactory.resetCycVariableCache();
      Object object = CycObjectFactory.unmarshall(cycVariableXMLString);
      Assert.assertTrue(object instanceof CycVariable);
      Assert.assertEquals(x, (CycVariable) object);
      Assert.assertTrue(CycObjectFactory.unmarshall(cycVariableXMLString) ==
      CycObjectFactory.unmarshall(cycVariableXMLString));
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycVariable OK ***");
  }
  
  /**
   * Tests StreamTokenizer CycList parsing behavior.
   */
  public void testStreamTokenizer() {
    System.out.println("\n*** testStreamTokenizer ***");
    try {
      String string = "()";
      MyStreamTokenizer st = CycListParser.makeStreamTokenizer(string);
      Assert.assertEquals(40, st.nextToken());
      Assert.assertEquals(41, st.nextToken());
      Assert.assertEquals(st.TT_EOF, st.nextToken());
      
      string = "(1)";
      st = CycListParser.makeStreamTokenizer(string);
      Assert.assertEquals(40, st.nextToken());
      
      int token = st.nextToken();
      Assert.assertEquals(st.TT_WORD, token);
      Assert.assertEquals("1", st.sval);
      
      Assert.assertEquals(41, st.nextToken());
      Assert.assertEquals(st.TT_EOF, st.nextToken());
      string = "(-10 -2 -1.0 -5.2E05)";
      st = CycListParser.makeStreamTokenizer(string);
      Assert.assertEquals(40, st.nextToken());
      
      token = st.nextToken();
      Assert.assertEquals(st.TT_WORD, token);
      Assert.assertEquals("-10", st.sval);
      
      token = st.nextToken();
      Assert.assertEquals(st.TT_WORD, token);
      Assert.assertEquals("-2", st.sval);
      
      token = st.nextToken();
      Assert.assertEquals(st.TT_WORD, token);
      Assert.assertEquals("-1.0", st.sval);
      
      token = st.nextToken();
      Assert.assertEquals(st.TT_WORD, token);
      Assert.assertEquals("-5.2E05", st.sval);
      
      Assert.assertEquals(41, st.nextToken());
      Assert.assertEquals(st.TT_EOF, st.nextToken());
      
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    
    System.out.println("*** testStreamTokenizer OK ***");
  }
  
  /**
   * Tests <tt>CycList</tt> object behavior.
   */
  public void testCycList() {
    System.out.println("\n*** testCycList ***");
    
    try {
      CycAccess cycAccess = null;
    
      // Simple empty list constructor.
      ArrayList arrayList = new ArrayList();
      CycList cycList = new CycList(arrayList);
      Assert.assertNotNull(cycList);
      Assert.assertEquals("()", cycList.toString());

      // Construct list of one element.
      ArrayList arrayList2 = new ArrayList();
      CycConstant brazil =
      new CycConstant("#$Brazil", CycObjectFactory.makeGuid("bd588f01-9c29-11b1-9dad-c379636f7270"));
      CycObjectFactory.addCycConstantCache(brazil);
      arrayList2.add(brazil);
      CycList cycList2 = new CycList(arrayList2);
      Assert.assertEquals("(Brazil)", cycList2.toString());
      Assert.assertEquals("(#$Brazil)", cycList2.cyclify());

      // Construct list with embedded sublist.
      ArrayList arrayList3 = new ArrayList();
      arrayList3.add(brazil);
      arrayList3.add(cycList);
      arrayList3.add(cycList2);
      CycList cycList3 = new CycList(arrayList3);
      Assert.assertEquals("(Brazil () (Brazil))", cycList3.toString());
      Assert.assertEquals("(#$Brazil () (#$Brazil))", cycList3.cyclify());

      // isValid()
      Assert.assertTrue(cycList.isValid());
      Assert.assertTrue(cycList2.isValid());
      Assert.assertTrue(cycList3.isValid());
      CycList cycList4 = new CycList(new Hashtable());
      Assert.assertTrue(! cycList4.isValid());

      // first(), rest()
      ArrayList arrayList5 = new ArrayList();
      arrayList5.add(brazil);
      CycList cycList5 = new CycList(arrayList5);
      Assert.assertEquals("(Brazil)", cycList5.toString());
      Assert.assertEquals("(#$Brazil)", cycList5.cyclify());
      Assert.assertEquals(cycList5.first(), brazil);
      Assert.assertTrue(((CycList) (cycList5.rest())).size() == 0);
      CycList cycList5a = new CycList();
      cycList5a.add("a");
      cycList5a.setDottedElement("b");
      Assert.assertEquals("b", cycList5a.rest());

      // reverse()
      Assert.assertEquals(cycList5.toString(), cycList5.reverse().toString());
      Assert.assertEquals("((#$Brazil) () #$Brazil)", cycList3.reverse().cyclify());

      // reverse of strings.
      ArrayList arrayList6 = new ArrayList();
      arrayList6.add("z");
      arrayList6.add("y");
      arrayList6.add("x");
      CycList cycList6 = new CycList(arrayList6);
      Assert.assertEquals("(\"z\" \"y\" \"x\")", cycList6.toString());
      Assert.assertEquals("(\"x\" \"y\" \"z\")", cycList6.reverse().toString());

      // Improper lists.
      ArrayList arrayList7 = new ArrayList();
      arrayList7.add(new Integer(10));
      CycList cycList7 = new CycList(arrayList7);
      cycList7.setDottedElement(brazil);
      Assert.assertTrue(cycList7.size() == 2);
      Assert.assertEquals("(10 . Brazil)", cycList7.toString());
      //CycListParser.verbosity = 10;
    
      CycListParser cycListParser = new CycListParser(null);
      CycList cycList7_1 = cycListParser.read("(a b c)");
      Assert.assertEquals("(A B C)", cycList7_1.toString());

      cycAccess = new CycAccess(HOSTNAME, PORT);
    
      CycList cycList7a = cycAccess.makeCycList("(a . (b . (c . (d))))");
      Assert.assertEquals("(A B C D)", cycList7a.toString());
      CycList cycList7b = cycAccess.makeCycList("((a . b) . (c . d))");
      Assert.assertEquals("((A . B) C . D)", cycList7b.toString());
      CycList cycList7c = cycAccess.makeCycList("((a . (b)) . (c . (d)))");
      Assert.assertEquals("((A B) C D)", cycList7c.toString());
      CycList cycList7d = cycAccess.makeCycList("(a b . c)");
      Assert.assertEquals("(A B . C)", cycList7d.toString());
      CycList cycList7e = cycAccess.makeCycList("(a b c . d)");
      Assert.assertEquals("(A B C . D)", cycList7e.toString());
      // construct
      Object object1 = CycList.construct(brazil, CycObjectFactory.nil);
      Assert.assertNotNull(object1);
      Assert.assertTrue(object1 instanceof CycList);
      Assert.assertEquals("(Brazil)", object1.toString());

      CycList cycList8 = CycList.construct(brazil, "Atlantic");
      Assert.assertEquals("(Brazil . \"Atlantic\")", cycList8.toString());

      CycList cycList9 = CycList.construct(brazil, new Integer(1));
      Assert.assertEquals("(Brazil . 1)", cycList9.toString());

      CycList cycList10 = CycList.construct(brazil, CycObjectFactory.makeCycSymbol("foo"));
      Assert.assertEquals("(Brazil . FOO)", cycList10.toString());
      
      // Parse strings to make CycLists.
      String listAsString = "()";
      CycList cycList11 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList11.toString());
      listAsString = "(1)";
      CycList cycList12 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList12.toString());
      listAsString = "(1 2 3 4 5)";
      CycList cycList13 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList13.toString());
      listAsString = "(\"1\" \"bar\" A #$Brazil Z 4.25 :KEYWORD ?collection NIL)";
      CycList cycList14 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList14.cyclify());
      listAsString = "((A))";
      CycList cycList15 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList15.toString());
      listAsString = "((A) (B C) (((D))))";
      CycList cycList16 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList16.toString());
      CycList cycList17 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(cycList17.toString(), cycList16.toString());
      Assert.assertEquals(cycList17.toString(), cycList16.toString());
      Assert.assertEquals(cycAccess.makeCycList("(A)"), cycList17.first());
      Assert.assertEquals(cycAccess.makeCycList("(B C)"), cycList17.second());
      Assert.assertEquals(cycAccess.makeCycList("(((D)))"), cycList17.third());
      listAsString = "(apply #'+ '(1 2 3))";
      CycList cycList18 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals("(APPLY (FUNCTION +) (QUOTE (1 2 3)))",
      cycList18.toString());
      listAsString = "(1 2 \n" +
      " ;; a comment \n" +
      " 3 4 5)";
      CycList cycList19 = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(cycList13, cycList19);
      listAsString = "(" + Double.toString(1.0E-05) + ")";
      CycList cycList19a = cycAccess.makeCycList(listAsString);
      Assert.assertEquals(listAsString, cycList19a.cyclify());
      cycListParser = new CycListParser(cycAccess);
      listAsString = "(1 2 3) 4 \"5 6\" 7 (8 9 10) 11 test";
      CycList cycList19b = cycListParser.read(listAsString);
      Assert.assertEquals("(1 2 3)", cycList19b.toString());
      Assert.assertEquals(" 4 \"5 6\" 7 (8 9 10) 11 test", cycListParser.remainingString());
      listAsString =
      "(#$ist-Asserted \n" +
      "  (#$totalInvestmentEarningsForStockTypeBoughtDuring  \n" +
      "    #$TechStock  \n" +
      "    (#$MinusFn (#$Pound-GreatBritain 330000000000))  \n" +
      "    (#$EarlyPartFn (#$YearFn 2000)))  \n" +
      "  #$TheMotleyFoolUKCorpusMt))";
      final CycList cycList19c = cycListParser.read(listAsString);
      Assert.assertTrue(cycList19c.cyclify().indexOf("330000000000") > -1);
      Assert.assertTrue(DefaultCycObject.cyclify(cycList19c).indexOf("330000000000") > -1);
      
      // subst
      cycList18 = cycAccess.makeCycList("(b)");
      cycList19 = cycList18.subst(CycObjectFactory.makeCycSymbol("x"), CycObjectFactory.makeCycSymbol("a"));
      Assert.assertEquals(cycAccess.makeCycList("(b)"), cycList19);
      CycList cycList20 = cycAccess.makeCycList("(a)");
      CycList cycList21 = cycList20.subst(CycObjectFactory.makeCycSymbol("x"), CycObjectFactory.makeCycSymbol("a"));
      Assert.assertEquals(cycAccess.makeCycList("(x)"), cycList21);
      CycList cycList22 = cycAccess.makeCycList("((a))");
      CycList cycList23 = cycList22.subst(CycObjectFactory.makeCycSymbol("x"), CycObjectFactory.makeCycSymbol("a"));
      Assert.assertEquals(cycAccess.makeCycList("((x))"), cycList23);
      CycList cycList24 = cycAccess.makeCycList("((a) (b c) (((d))))");
      CycList cycList25 = cycList24.subst(CycObjectFactory.makeCycSymbol("x"), CycObjectFactory.makeCycSymbol("a"));
      Assert.assertEquals(cycAccess.makeCycList("((x) (b c) (((d))))"), cycList25);
    
      // containsDuplicates
      CycList cycList26 = cycAccess.makeCycList("(a b c d)");
      Assert.assertTrue(! cycList26.containsDuplicates());
      CycList cycList27 = cycAccess.makeCycList("(a a c d)");
      Assert.assertTrue(cycList27.containsDuplicates());
      CycList cycList28 = cycAccess.makeCycList("(a b c c)");
      Assert.assertTrue(cycList28.containsDuplicates());
      CycList cycList29 = cycAccess.makeCycList("(a (b) (b) c)");
      Assert.assertTrue(cycList29.containsDuplicates());
    
      // list
      CycList cycList30 = CycList.list(CycObjectFactory.makeCycSymbol("a"));
      Assert.assertEquals("(A)", cycList30.toString());
      CycList cycList31 = CycList.list(CycObjectFactory.makeCycSymbol("a"),
      CycObjectFactory.makeCycSymbol("b"));
      Assert.assertEquals("(A B)", cycList31.toString());
      CycList cycList32 = CycList.list(CycObjectFactory.makeCycSymbol("a"),
      CycObjectFactory.makeCycSymbol("b"),
      CycObjectFactory.makeCycSymbol("c"));
      Assert.assertEquals("(A B C)", cycList32.toString());
    
      // combinationsOf
      CycList cycList33 = cycAccess.makeCycList("(1 2 3 4)");
      Assert.assertEquals("((1) (2) (3) (4))", cycList33.combinationsOf(1).toString());
      Assert.assertEquals("((1 2) (1 3) (1 4) (2 3) (2 4) (3 4))",
      cycList33.combinationsOf(2).toString());
      Assert.assertEquals("((1 2 3 4))",
      cycList33.combinationsOf(4).toString());
      Assert.assertEquals("()",
      cycList33.combinationsOf(0).toString());
      Assert.assertEquals("()",
      (new CycList()).combinationsOf(4).toString());
    
      // randomPermutation
      CycList cycList34 = cycAccess.makeCycList("(1 2 3 4 5 6 7 8 9 10)");
      CycList permutedCycList = cycList34.randomPermutation();
      Assert.assertEquals(10, permutedCycList.size());
      Assert.assertTrue(permutedCycList.contains(new Integer(2)));
      Assert.assertTrue(! permutedCycList.containsDuplicates());
    
      // doesElementPrecedeOthers
      CycList cycList35 = cycAccess.makeCycList("(1 2 3 4 5 6 7 8 9 10)");
      Assert.assertTrue(cycList35.doesElementPrecedeOthers(new Integer(1),
      cycAccess.makeCycList("(8 7 6)")));
      Assert.assertTrue(cycList35.doesElementPrecedeOthers(new Integer(9),
      cycAccess.makeCycList("(10)")));
      Assert.assertTrue(cycList35.doesElementPrecedeOthers(new Integer(10),
      cycAccess.makeCycList("(18 17 16)")));
      Assert.assertTrue(! cycList35.doesElementPrecedeOthers(new Integer(12),
      cycAccess.makeCycList("(1 2 10)")));
      Assert.assertTrue(! cycList35.doesElementPrecedeOthers(new Integer(9),
      cycAccess.makeCycList("(8 7 6)")));
    
      // clone
      CycList cycList36 = cycAccess.makeCycList("(1 2 3 4 5)");
      CycList cycList37 = (CycList) cycList36.clone();
      Assert.assertEquals(cycList36, cycList37);
      Assert.assertTrue(cycList36 != cycList37);
      CycList cycList38 = cycAccess.makeCycList("(1 2 3 4 5 . 6)");
      CycList cycList39 = (CycList) cycList38.clone();
      
      Assert.assertEquals(cycList38, cycList39);
      Assert.assertTrue(cycList38 != cycList39);
    
      // deepCopy
      CycList cycList40 = cycAccess.makeCycList("(1 2 3 4 5)");
      CycList cycList41 = (CycList) cycList40.deepCopy();
      Assert.assertEquals(cycList40, cycList41);
      Assert.assertTrue(cycList40 != cycList41);
      CycList cycList42 = cycAccess.makeCycList("(1 2 3 4 5 . 6)");
      CycList cycList43 = (CycList) cycList42.deepCopy();
      Assert.assertEquals(cycList42, cycList43);
      Assert.assertTrue(cycList42 != cycList43);
      CycList cycList44 = cycAccess.makeCycList("(1 (2 3) (4 5) ((6)))");
      CycList cycList45 = (CycList) cycList44.deepCopy();
      Assert.assertEquals(cycList44, cycList45);
      Assert.assertTrue(cycList44 != cycList45);
      Assert.assertEquals(cycList44.first(), cycList45.first());
      Assert.assertTrue(cycList44.first() == cycList45.first());
      Assert.assertEquals(cycList44.second(), cycList45.second());
      Assert.assertTrue(cycList44.second() != cycList45.second());
      Assert.assertEquals(cycList44.fourth(), cycList45.fourth());
      Assert.assertTrue(cycList44.fourth() != cycList45.fourth());
      Assert.assertEquals(((CycList) cycList44.fourth()).first(),
      ((CycList) cycList45.fourth()).first());
      Assert.assertTrue(((CycList) cycList44.fourth()).first() !=
      ((CycList) cycList45.fourth()).first());
    
      // addNew
      CycList cycList46 = cycAccess.makeCycList("(1 2 3 4 5)");
      Assert.assertEquals(5, cycList46.size());
      cycList46.addNew(new Integer(6));
      Assert.assertEquals(6, cycList46.size());
      cycList46.addNew(new Integer(2));
      Assert.assertEquals(6, cycList46.size());
      // addAllNew
      CycList cycList47 = cycAccess.makeCycList("(1 2 3 4 5)");
      Assert.assertEquals(5, cycList47.size());
      CycList cycList48 = cycAccess.makeCycList("(6 7 8 9 10)");
      Assert.assertEquals(5, cycList48.size());
      cycList47.addAllNew(cycList48);
      Assert.assertEquals(10, cycList47.size());
      CycList cycList49 = cycAccess.makeCycList("(2 5 8 9 11)");
      Assert.assertEquals(5, cycList49.size());
      cycList47.addAllNew(cycList49);
      Assert.assertEquals(11, cycList47.size());
      
      // last
      cycList46 = cycAccess.makeCycList("(8 7 6)");
      Assert.assertEquals(new Integer(6), cycList46.last());
      // toXML, toXMLString
      listAsString = "(\"1\" A (#$Brazil . Z) 4.25 :KEYWORD ?collection NIL . #$Dog)";
      cycList47 = cycAccess.makeCycList(listAsString);
      XMLStringWriter xmlStringWriter = new XMLStringWriter();
      String cycListXMLString = cycList47.toXMLString();
      Object object = CycObjectFactory.unmarshall(cycListXMLString);
      Assert.assertTrue(object instanceof CycList);
      Assert.assertEquals(cycList47, (CycList) object);
      cycList48 =
        cycAccess.makeCycList("(T (#$BiologicalTaxon " +
                              "#$BiologicalSpecies " +
                              "#$OrganismClassificationType " +
                              "#$CycLTerm " +
                              "#$CollectionType))");
      cycListXMLString = Marshaller.marshall(cycList48);
//      System.out.println(cycListXMLString);
      object = CycObjectFactory.unmarshall(cycListXMLString);
      Assert.assertTrue(object instanceof CycList);
      Assert.assertEquals(cycList48, (CycList) object);
      cycListXMLString =
          "\n<list>\n" +
          "  <symbol>QUOTE</symbol>\n" +
          "  <list>\n" +
          "    <symbol>A</symbol>\n" +
          "    <dotted-element>\n" +
          "      <symbol>B</symbol>\n" +
          "    </dotted-element>\n" +
          "  </list>\n" +
          "</list>\n";
      object = CycObjectFactory.unmarshall(cycListXMLString);
      Assert.assertTrue(object instanceof CycList);
      cycList49 = cycAccess.makeCycList("(QUOTE (A . B))");
      Assert.assertEquals(cycList49, object);
      
      // toHTMLPrettyString
      CycList cycList50 = cycAccess.makeCycList("(QUOTE (#$and (#$isa ?UNIT #$ModernMilitaryOrganization) (#$objectFoundInLocation ?UNIT #$Illinois-State) (#$behaviorCapable ?UNIT (#$ReactionToSituationTypeFn #$ChemicalAttack) #$performedBy)))");
      Assert.assertEquals("<html><body>(QUOTE<br>&nbsp&nbsp(and<br>&nbsp&nbsp&nbsp&nbsp(isa ?UNIT ModernMilitaryOrganization)<br>&nbsp&nbsp&nbsp&nbsp(objectFoundInLocation ?UNIT Illinois-State)<br>&nbsp&nbsp&nbsp&nbsp(behaviorCapable ?UNIT<br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp(ReactionToSituationTypeFn ChemicalAttack) performedBy)))</body></html>", cycList50.toHTMLPrettyString(""));
      // treeContains
      cycList50 = cycAccess.makeCycList("(DEFMACRO-IN-API MY-MACRO (A B C) (RET ` (LIST , A , B , C)))");
      Assert.assertTrue(cycList50.treeContains(CycObjectFactory.backquote));
    
      // getValueForKeyword
      cycList50 = cycAccess.makeCycList(
      "(fipa-transport-message\n" +
      "  (envelope\n" +
      "    :to my-remote-agent\n" +
      "    :from my-cyc-agent\n" +
      "    :date 3215361678\n" +
      "    :X-agent-community :coabs\n" +
      "    :X-cyc-image-id \"balrog-200111112091457-939\"\n" +
      "    :X-base-tcp-port 3600)\n" +
      "  (payload\n" +
      "    (inform\n" +
      "      :sender my-cyc-agent\n" +
      "      :receiver my-remote-agent\n" +
      "      :reply-to message1\n" +
      "      :content \"Hello from my-cyc-agent\"\n" +
      "      :language :cycl\n" +
      "      :reply-with \"my cookie\"\n" +
      "      :ontology cyc-api\n" +
      "      :protocol :fipa-request)))");
      Assert.assertEquals(cycList50.size(), 3);
      Assert.assertEquals(cycList50.first(), CycObjectFactory.makeCycSymbol("fipa-transport-message"));
      Assert.assertTrue(cycList50.second() instanceof CycList);
      CycList envelope = (CycList) cycList50.second();
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-remote-agent"),
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":to")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-cyc-agent"),
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":from")));
      Assert.assertEquals(new Long("3215361678"),
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":date")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol(":coabs"),
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":X-agent-community")));
      Assert.assertEquals("balrog-200111112091457-939",
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":X-cyc-image-id")));
      Assert.assertEquals(new Integer(3600),
      envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":X-base-tcp-port")));
      Assert.assertNull(envelope.getValueForKeyword(CycObjectFactory.makeCycSymbol(":not-there")));
      Assert.assertTrue(cycList50.third() instanceof CycList);
      Assert.assertTrue(cycList50.third() instanceof CycList);
      CycList payload = (CycList) cycList50.third();
      Assert.assertTrue(payload.second() instanceof CycList);
      CycList aclList = (CycList) payload.second();
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-cyc-agent"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":sender")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("my-remote-agent"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":receiver")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("message1"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":reply-to")));
      Assert.assertEquals("Hello from my-cyc-agent",
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":content")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol(":cycl"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":language")));
      Assert.assertEquals("my cookie",
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":reply-with")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol("cyc-api"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":ontology")));
      Assert.assertEquals(CycObjectFactory.makeCycSymbol(":fipa-request"),
      aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":protocol")));
      Assert.assertNull(aclList.getValueForKeyword(CycObjectFactory.makeCycSymbol(":not-there")));
      
      // addQuoted
      CycList cycList51 = new CycList();
      cycList51.add(new Integer(1));
      cycList51.addQuoted(CycObjectFactory.makeCycSymbol("quote-me"));
      Assert.assertEquals("(1 (QUOTE QUOTE-ME))", cycList51.toString());

      // toString (with null element)
      CycList cycList52 = new CycList();
      cycList52.add(null);
      Assert.assertNull(cycList52.first());
      Assert.assertEquals("(null)", cycList52.toString());
    
      // getSpecifiedObject
      CycList cycList53 = cycAccess.makeCycList("(1 (2 3 (4)) 5)");
      CycList pathSpecification = cycAccess.makeCycList("(0)");
      Object obj = cycList53.getSpecifiedObject(pathSpecification);
      Object expectedObj = new Integer(1);
      Assert.assertEquals(expectedObj, obj);
      
      pathSpecification = cycAccess.makeCycList("(1)");
      obj = cycList53.getSpecifiedObject(pathSpecification);
      expectedObj = cycList53.second();
      Assert.assertEquals(expectedObj, obj);
      
      pathSpecification = cycAccess.makeCycList("(2)");
      obj = cycList53.getSpecifiedObject(pathSpecification);
      expectedObj = cycList53.third();
      Assert.assertEquals(expectedObj, obj);
      
      pathSpecification = cycAccess.makeCycList("(1 2 0)");
      obj = cycList53.getSpecifiedObject(pathSpecification);
      expectedObj = new Integer(4);
      Assert.assertEquals(expectedObj, obj);
      
      // setSpecifedObject
      pathSpecification = cycAccess.makeCycList("(0)");
      cycList53.setSpecifiedObject(pathSpecification, "a");
      expectedObj = cycAccess.makeCycList("(\"a\" (2 3 (4)) 5)");
      Assert.assertEquals(expectedObj, cycList53);
      
      pathSpecification = cycAccess.makeCycList("(2)");
      cycList53.setSpecifiedObject(pathSpecification, "b");
      expectedObj = cycAccess.makeCycList("(\"a\" (2 3 (4)) \"b\")");
      Assert.assertEquals(expectedObj, cycList53);
      
      pathSpecification = cycAccess.makeCycList("(1 2 0)");
      cycList53.setSpecifiedObject(pathSpecification, "c");
      expectedObj = cycAccess.makeCycList("(\"a\" (2 3 (\"c\")) \"b\")");
      Assert.assertEquals(expectedObj, cycList53);
      
      CycConstant arityRelationFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bf361058-9c29-11b1-9dad-c379636f7270"));
      CycNart cycNart = new CycNart(arityRelationFn, new Integer(1));
      cycList53.add(cycNart);
      expectedObj = cycAccess.makeCycList("(\"a\" (2 3 (\"c\")) \"b\" (#$ArityRelationFn 2))");
      pathSpecification = cycAccess.makeCycList("(3 1)");
      cycList53.setSpecifiedObject(pathSpecification, new Integer(2));
      Assert.assertEquals(expectedObj, cycList53);
      
      // test getArgPositionsForTerm
      
      CycList list = new CycList();
      CycList subList1 = new CycList();
      CycList subList2 = new CycList();
      CycList subSubList = new CycList();
      list.add(subList1);
      list.add("a");
      list.add("b");
      list.add("c");
      list.add(subList2);
      subList1.add("c");
      subList1.add("1");
      subList1.add("2");
      subList2.add("a");
      subList2.add(subSubList);
      subList2.add("c");
      subList2.add("2");
      subSubList.add("c");
      subSubList.add("10");
      subSubList.add("11");
      Assert.assertEquals("((\"c\" \"1\" \"2\") \"a\" \"b\" \"c\" (\"a\" (\"c\" \"10\" \"11\") \"c\" \"2\"))", list.toString());
      List result = list.getArgPositionsForTerm("a");
      Assert.assertEquals("([1] [4, 0])", (new CycList(result)).toString());
      List result1 = list.getArgPositionsForTerm("c");
      Assert.assertEquals("([0, 0] [3] [4, 1, 0] [4, 2])", (new CycList(result1)).toString());
      List result2 = list.getArgPositionsForTerm("d");
      Assert.assertEquals("()", (new CycList(result2)).toString());
      
      // treeConstants
      CycList cycList54 =
      cycAccess.makeCycList("(T (#$BiologicalTaxon " +
      "#$BiologicalSpecies " +
      "#$OrganismClassificationType " +
      "#$CycLTerm " +
      "#$CollectionType))");
      cycList54.add(new CycNart(cycAccess.getKnownConstantByName("FruitFn"), cycAccess.getKnownConstantByName("PumpkinPlant")));
      CycList cycList55 = cycList54.treeConstants();
      Assert.assertEquals(7, cycList55.size());
      
      // stringApiValue()
      CycConstant ontario = null;
      ontario =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd58b6d5-9c29-11b1-9dad-c379636f7270"));
      CycList cycList56 = new CycList(ontario);
      Object result56 = CycUtils.evalSubLWithWorker(cycAccess, cycList56.stringApiValue());
      Assert.assertTrue(result56 instanceof CycList);
      Assert.assertEquals(cycList56, (CycList) result56);
      // Check whether stringApiValue works properly on a CycList with a CycNart element
      CycConstant cityNamedFn =
      cycAccess.getKnownConstantByGuid(
      CycObjectFactory.makeGuid("bd6870a6-9c29-11b1-9dad-c379636f7270"));
      CycNart attawapiskat = new CycNart(cityNamedFn, "Attawapiskat", ontario);
      CycList cycListWithNart = new CycList(ontario, attawapiskat);
      Object resultObj = CycUtils.evalSubLWithWorker(cycAccess, cycListWithNart.stringApiValue());
      Assert.assertTrue(resultObj instanceof CycList);
      Assert.assertEquals(cycListWithNart.cyclify(), ((CycList) resultObj).cyclify());
      // stringApiValue() on a CycList containing a String containing a double-quote
      CycList cycListWithString = new CycList(new String("How much \"wood\" would a \"woodchuck\" \"chuck\"?"));
      resultObj = CycUtils.evalSubLWithWorker(cycAccess, cycListWithString.stringApiValue());
      Assert.assertTrue(resultObj instanceof CycList);
      Assert.assertEquals(cycListWithString, (CycList) resultObj);
    
      // stringApiValue() on a dotted CycList
      CycList dottedCycList = new CycList("first element", "second element");
      dottedCycList.setDottedElement("dotted element");
      resultObj = CycUtils.evalSubLWithWorker(cycAccess, dottedCycList.stringApiValue());
      Assert.assertTrue(resultObj instanceof CycList);
      Assert.assertEquals(dottedCycList, (CycList) resultObj);
      // Parse a list containing a string with a backslash
      String script = "(identity \"abc\")";
      resultObj = CycUtils.evalSubLWithWorker(cycAccess, script);
      Assert.assertTrue(resultObj instanceof String);
      script = "(identity \"abc\\\\\")";
      resultObj = CycUtils.evalSubLWithWorker(cycAccess, script);
      Assert.assertTrue(resultObj instanceof String);
      CycList command = new CycList();
      command.add(CycObjectFactory.makeCycSymbol("identity"));
      command.add("abc\\");
      script = command.cyclifyWithEscapeChars();
      resultObj = CycUtils.evalSubLWithWorker(cycAccess, script);
      Assert.assertTrue(resultObj instanceof String);
      cycAccess.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycList OK ***");
  }
  
  /**
   * Tests <tt>CycListVisitor</tt> object behavior.
   */
  public void testCycListVisitor() {
    System.out.println("\n*** testCycListVisitor ***");
    
    CycListParser.verbosity = 0;
    CycAccess cycAccess = null;
    try {
      cycAccess = new CycAccess(HOSTNAME, PORT);
    }
    catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    
    try {
      CycList cycList2000 = cycAccess.makeCycList("(1 . 24)");
      CycList cycList2001 = cycAccess.makeCycList("(1 . 23)");
      Assert.assertFalse(cycList2001.equals(cycList2000));
      
      CycList cycList1 = cycAccess.makeCycList("()");
      Enumeration e1 = cycList1.cycListVisitor();
      Assert.assertTrue(! e1.hasMoreElements());
      
      CycList cycList2 = cycAccess.makeCycList("(1 \"a\" :foo #$Brazil)");
      Enumeration e2 = cycList2.cycListVisitor();
      Assert.assertTrue(e2.hasMoreElements());
      Integer integer1 = new Integer(1);
      Object nextObject = e2.nextElement();
      Assert.assertTrue(nextObject instanceof Integer);
      Assert.assertTrue(((Integer) nextObject).intValue() == integer1.intValue());
      Assert.assertTrue(((Integer) nextObject).intValue() == 1);
      Assert.assertTrue(e2.hasMoreElements());
      Assert.assertEquals("a", e2.nextElement());
      Assert.assertTrue(e2.hasMoreElements());
      Assert.assertEquals(CycObjectFactory.makeCycSymbol(":foo"), e2.nextElement());
      Assert.assertTrue(e2.hasMoreElements());
      Assert.assertEquals(cycAccess.makeCycConstant("#$Brazil"),
      e2.nextElement());
      Assert.assertTrue(! e1.hasMoreElements());
      
      CycList cycList3 = cycAccess.makeCycList("((()))");
      Enumeration e3 = cycList3.cycListVisitor();
      Assert.assertTrue(! e3.hasMoreElements());
      
      CycList cycList4 = cycAccess.makeCycList("(()())");
      Enumeration e4 = cycList4.cycListVisitor();
      Assert.assertTrue(! e4.hasMoreElements());
      
      CycList cycList5 = cycAccess.makeCycList("(\"a\" (\"b\") (\"c\") \"d\" \"e\")");
      Enumeration e5 = cycList5.cycListVisitor();
      Assert.assertTrue(e5.hasMoreElements());
      Assert.assertEquals("a", e5.nextElement());
      Assert.assertTrue(e5.hasMoreElements());
      Assert.assertEquals("b", e5.nextElement());
      Assert.assertTrue(e5.hasMoreElements());
      Assert.assertEquals("c", e5.nextElement());
      Assert.assertTrue(e5.hasMoreElements());
      Assert.assertEquals("d", e5.nextElement());
      Assert.assertTrue(e5.hasMoreElements());
      Assert.assertEquals("e", e5.nextElement());
      Assert.assertTrue(! e5.hasMoreElements());
      
      CycList cycList6 = cycAccess.makeCycList("(\"a\" (\"b\" \"c\") (\"d\" \"e\"))");
      Enumeration e6 = cycList6.cycListVisitor();
      Assert.assertTrue(e6.hasMoreElements());
      Assert.assertEquals("a", e6.nextElement());
      Assert.assertTrue(e6.hasMoreElements());
      Assert.assertEquals("b", e6.nextElement());
      Assert.assertTrue(e6.hasMoreElements());
      Assert.assertEquals("c", e6.nextElement());
      Assert.assertTrue(e6.hasMoreElements());
      Assert.assertEquals("d", e6.nextElement());
      Assert.assertTrue(e6.hasMoreElements());
      Assert.assertEquals("e", e6.nextElement());
      Assert.assertTrue(! e6.hasMoreElements());
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    try {
      cycAccess.close();
    }
    catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    
    System.out.println("*** testCycListVisitor OK ***");
  }
  
  /**
   * Tests the CycAssertion class.
   */
  public void testCycAssertion() {
    System.out.println("\n*** testCycAssertion ***");
    CycAccess cycAccess = null;
    try {
      cycAccess = new CycAccess(HOSTNAME, PORT);
    }
    catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    
    // stringApiValue() on a random assertion
    try {
      CycAssertion assertion = cycAccess.getRandomAssertion();
      Assert.assertNotNull(assertion);
      String assertionAsString = assertion.stringApiValue();
      final Object assertionObject2 = cycAccess.converseObject(assertionAsString);
      if (assertionObject2 instanceof CycAssertion) {
        final CycAssertion assertion2 = (CycAssertion) assertionObject2;
        Assert.assertEquals(assertion, assertion2);
      }
      else 
        System.err.println(assertionAsString + "\ndoes not returns the following which is not the expected assertion:\n" + assertionObject2);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    // toXMLString()() on a random assertion
    try {
      final CycAssertion assertion = cycAccess.getRandomAssertion();
      Assert.assertNotNull(assertion);
      final String assertionAsXML = assertion.toXMLString();
      Assert.assertNotNull(assertionAsXML);
      System.out.println();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    //TODO
        /*
        // toXML, toXMLString, unmarshall
        XMLStringWriter xmlStringWriter = new XMLStringWriter();
        try {
            String xmlString =
                "<assertion>\n" +
                "  <id>1000</id>\n" +
                "</assertion>\n";
            Object object = CycObjectFactory.unmarshall(xmlString);
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof CycAssertion);
            CycAssertion cycAssertion = (CycAssertion) object;
            cycAssertion.toXML(xmlStringWriter, 0, false);
            Assert.assertEquals(xmlString, xmlStringWriter.toString());
            Assert.assertEquals(xmlString, cycAssertion.toXMLString());
            CycAssertion cycAssertion2 = new CycAssertion(new Integer (1000));
            Assert.assertEquals(cycAssertion2, cycAssertion);
            CycList cycList = new CycList();
            cycList.add(cycAssertion);
            //System.out.println(cycList.toXMLString());
         
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
         */
    System.out.println("*** testCycAssertion OK ***");
  }
  /**
   * Tests the ByteArray class.
   */
  public void testByteArray() {
    System.out.println("\n*** testByteArray ***");
    byte[] bytes = {0, 1, 2, 3, 4, -128};
    ByteArray byteArray1 = new ByteArray(bytes);
    Assert.assertNotNull(byteArray1);
    Assert.assertEquals(6, byteArray1.byteArrayValue().length);
    Assert.assertEquals(0, byteArray1.byteArrayValue()[0]);
    Assert.assertEquals(1, byteArray1.byteArrayValue()[1]);
    Assert.assertEquals(2, byteArray1.byteArrayValue()[2]);
    Assert.assertEquals(3, byteArray1.byteArrayValue()[3]);
    Assert.assertEquals(4, byteArray1.byteArrayValue()[4]);
    Assert.assertEquals(-128, byteArray1.byteArrayValue()[5]);
    byte[] bytes2 = {0, 1, 2, 3, 4, -128};
    ByteArray byteArray2 = new ByteArray(bytes2);
    Assert.assertEquals(byteArray1, byteArray1);
    Assert.assertEquals(byteArray1, byteArray2);
    byte[] bytes3 = {0, -1, 2, 3, 4, -128};
    ByteArray byteArray3 = new ByteArray(bytes3);
    Assert.assertTrue(! byteArray1.equals(byteArray3));
    Assert.assertEquals("[ByteArray len:6 0,1,2,3,4,-128]", byteArray1.toString());
    
    // toXML, toXMLString, unmarshall
    XMLStringWriter xmlStringWriter = new XMLStringWriter();
    try {
      byteArray1.toXML(xmlStringWriter, 0, false);
      String expectedXmString =
      "<byte-vector>\n" +
      "  <length>6</length>\n" +
      "  <byte>0</byte>\n" +
      "  <byte>1</byte>\n" +
      "  <byte>2</byte>\n" +
      "  <byte>3</byte>\n" +
      "  <byte>4</byte>\n" +
      "  <byte>-128</byte>\n" +
      "</byte-vector>\n";
      
      Assert.assertEquals(expectedXmString, xmlStringWriter.toString());
      Assert.assertEquals(expectedXmString, byteArray1.toXMLString());
      Assert.assertEquals(byteArray1, CycObjectFactory.unmarshall(byteArray1.toXMLString()));
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    System.out.println("*** testByteArray OK ***");
  }
  
  /**
   * Tests the ELMTCycList class.
   */
  public void testELMTCycList() {
    System.out.println("\n*** testELMTCycList ***");
    
    try {
      final CycAccess cycAccess = new CycAccess(HOSTNAME, PORT);
      if (! cycAccess.isOpenCyc()) {
        final CycObject mt = cycAccess.makeCycList("(#$MtSpace #$CycorpBusinessMt (#$MtTimeWithGranularityDimFn (#$MonthFn #$January (#$YearFn 2004)) #$TimePoint))");
        Assert.assertNotNull(cycAccess.getComment(cycAccess.isa, mt));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    System.out.println("*** testELMTCycList OK ***");
  }
  /**
   * Tests the Unicode support in the DefaultCycObject class.
   */
  public void testUnicodeString() {
    System.out.println("\n*** testUnicodeString ***");
    String result=DefaultCycObject.cyclifyWithEscapeChars("abc");
    //System.out.println("abc test |"+result+"|");
    Assert.assertTrue("abc test","\"abc\"".equals(result));
    
    
    
    result=DefaultCycObject.cyclifyWithEscapeChars("a\\b");
    //System.out.println("a\\b test |"+result+"|");
    Assert.assertTrue("a\\\\b test","\"a\\\\b\"".equals(result));
    
    result=DefaultCycObject.cyclifyWithEscapeChars("a\"b");
    //System.out.println("a\"b test |"+result+"|");
    Assert.assertTrue("a\"c test","\"a\\\"b\"".equals(result));
    
    StringBuffer sb=new StringBuffer();
    sb.append('a');
    sb.append((char)(0x140));
    result=DefaultCycObject.cyclifyWithEscapeChars(sb.toString());
    //System.out.println("a&u140 test |"+result+"|");
    Assert.assertEquals("\"a&u140;\"",result);
    
    
    System.out.println("*** testUnicodeString OK ***");
  }
  
  private void checkPrettyStringDetail(Map map, CycList curPos, 
      int expectedBegin, int expectedEnd) {
    int[] locs = (int[])map.get(curPos);
    assertNotNull(locs);
    assertEquals(2, locs.length);
    assertEquals(expectedBegin, locs[0]);
    assertEquals(expectedEnd, locs[1]);
  }
  
  /**
   * Test the CycList pretty printer
   */
  public void testCycListPrettyStringDetails() {
    System.out.println("\n*** testCycListPrettyStringDetails ***");
    CycAccess cycAccess = null;
    try {
      cycAccess = new CycAccess(HOSTNAME, PORT);
      CycList example = null;
      java.util.HashMap map = null;
      CycList curPos = null;
      example = (CycList)org.opencyc.parser.CycLParserUtil.
        parseCycLTerm("(#$isa #$Muffet #$Dog)", true, cycAccess);
      map = example.getPrettyStringDetails();
      checkPrettyStringDetail(map, new CycList(), 0, 16);
      checkPrettyStringDetail(map, new CycList(new Integer(0)), 1, 4);
      checkPrettyStringDetail(map, new CycList(new Integer(1)), 5, 11);
      checkPrettyStringDetail(map, new CycList(new Integer(2)), 12, 15);
      
      example = (CycList)org.opencyc.parser.CycLParserUtil.parseCycLTerm(
      "(#$isa (#$InstanceNamedFn \"Muffet\" (#$JuvenileFn #$Dog)) (#$JuvenileFn #$Dog))",
      true, cycAccess);
      map = example.getPrettyStringDetails();
      checkPrettyStringDetail(map, new CycList(), 0, 74);
      checkPrettyStringDetail(map, new CycList(new Integer(0)), 1, 4);
      checkPrettyStringDetail(map, new CycList(new Integer(1), new Integer(0)), 8, 23);
      checkPrettyStringDetail(map, new CycList(new Integer(1), new Integer(1)), 24, 32);
      curPos = new CycList(new Integer(1), new Integer(2));
      curPos.add(new Integer(0));
      checkPrettyStringDetail(map, curPos, 38, 48);
      curPos = new CycList(new Integer(1), new Integer(2));
      curPos.add(new Integer(1));
      checkPrettyStringDetail(map, curPos, 49, 52);
      checkPrettyStringDetail(map, new CycList(new Integer(1), new Integer(2)), 37, 53);
      checkPrettyStringDetail(map, new CycList(new Integer(1)), 7, 54);
      checkPrettyStringDetail(map, new CycList(new Integer(2), new Integer(0)), 58, 68);
      checkPrettyStringDetail(map, new CycList(new Integer(2), new Integer(1)), 69, 72);
      checkPrettyStringDetail(map, new CycList(new Integer(2)), 57, 73);
      
      final CycList testList = new CycList();
      final StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append('"');
      stringBuffer.append("abc");
      testList.add(stringBuffer.toString());
      final String testEscapedCyclifiedString = testList.toPrettyEscapedCyclifiedString("");
      Assert.assertEquals("(\"\\\"abc\")", testEscapedCyclifiedString);     
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    
    
    System.out.println("*** testCycListPrettyStringDetails OK ***");
  }
  
}
