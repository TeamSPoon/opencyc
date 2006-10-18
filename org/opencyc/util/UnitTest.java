package org.opencyc.util;

import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycList;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.textui.TestRunner;
import junit.framework.TestSuite;


/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.constraintsolver</tt> package.<p>
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
  
  /**
   * Main method in case tracing is prefered over running JUnit.
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
  
  /**
   * Constructs a new UnitTest object.
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
    testSuite.addTest(new UnitTest("testChange"));
    testSuite.addTest(new UnitTest("testHasDuplicates"));
    testSuite.addTest(new UnitTest("testHasIntersection"));
    testSuite.addTest(new UnitTest("testRemoveDelimiters"));
    testSuite.addTest(new UnitTest("testIsDelimitedString"));
    testSuite.addTest(new UnitTest("testIsNumeric"));
    testSuite.addTest(new UnitTest("testWordsToString"));
    testSuite.addTest(new UnitTest("testEscapeDoubleQuotes"));
    testSuite.addTest(new UnitTest("testBase64"));
    testSuite.addTest(new UnitTest("testMyStreamTokenizer"));
    testSuite.addTest(new UnitTest("testIs7BitASCII"));
    testSuite.addTest(new UnitTest("testUnicodeEscaped"));  
    testSuite.addTest(new UnitTest("testIsWhitespace"));  
    testSuite.addTest(new UnitTest("testStripLeading"));  
    testSuite.addTest(new UnitTest("testStripTrailing"));  
    testSuite.addTest(new UnitTest("testStripTrailingBlanks"));  
    testSuite.addTest(new UnitTest("testLogPrintln")); 
    return testSuite;
  }
  
  /** Tests the StringUtils.change method. */
  public void testChange() {
    System.out.println("** testChange **");
    Assert.assertEquals("", StringUtils.change("", "", ""));
    Assert.assertEquals("a", StringUtils.change("a", "b", "c"));
    Assert.assertEquals("z", StringUtils.change("a", "a", "z"));
    Assert.assertEquals("xyz", StringUtils.change("abc", "abc", "xyz"));
    Assert.assertEquals("zbc", StringUtils.change("abc", "a", "z"));
    Assert.assertEquals("azc", StringUtils.change("abc", "b", "z"));
    Assert.assertEquals("abz", StringUtils.change("abc", "c", "z"));
    Assert.assertEquals("", StringUtils.change("abc", "abc", ""));
    Assert.assertEquals("a123c", StringUtils.change("abc", "b", "123"));
    Assert.assertEquals("123bc", StringUtils.change("abc", "a", "123"));
    Assert.assertEquals("ab123", StringUtils.change("abc", "c", "123"));
    final StringBuffer stringBuffer = new StringBuffer(100);
    stringBuffer.append("abc");
    stringBuffer.append('\n');
    stringBuffer.append("def");
    Assert.assertEquals("abc\\ndef", StringUtils.change(stringBuffer.toString(), "\n", "\\n"));
    System.out.println("** testChange OK **");
  }
  
  /**
   * Tests the OcCollectionUtils.hasDuplicates method.
   */
  public void testHasDuplicates() {
    System.out.println("** testHasDuplicates **");
    ArrayList collection1 = new ArrayList();
    collection1.add("a");
    collection1.add("b");
    collection1.add("b");
    Assert.assertTrue(OcCollectionUtils.hasDuplicates(collection1));
    ArrayList collection2 = new ArrayList();
    collection2.add("a");
    collection2.add("b");
    collection2.add("c");
    ArrayList arrayList = new ArrayList();
    arrayList.add("d");
    collection2.add(arrayList);
    collection2.add(arrayList);
    Assert.assertTrue(OcCollectionUtils.hasDuplicates(collection2));
    ArrayList collection3 = new ArrayList();
    collection3.add("a");
    collection3.add("b");
    collection3.add("c");
    Assert.assertTrue(! OcCollectionUtils.hasDuplicates(collection3));
    System.out.println("** testHasDuplicates OK **");
  }
  
  /**
   * Tests the OcCollectionUtils.hasIntersection method.
   */
  public void testHasIntersection() {
    System.out.println("** testHasIntersection **");
    ArrayList a = new ArrayList();
    ArrayList b = new ArrayList();
    Assert.assertTrue(! OcCollectionUtils.hasIntersection(a, b));
    a.add("a");
    Assert.assertTrue(! OcCollectionUtils.hasIntersection(a, b));
    b.add("a");
    Assert.assertTrue(OcCollectionUtils.hasIntersection(a, b));
    b.remove("a");
    Assert.assertTrue(! OcCollectionUtils.hasIntersection(a, b));
    a.add("b");
    a.add("d");
    a.add("e");
    a.add("f");
    b.add("f");
    Assert.assertTrue(OcCollectionUtils.hasIntersection(a, b));
    ArrayList bigA = new ArrayList();
    for (int i = 0; i < 150; i++)
      bigA.add(new Integer(i));
    ArrayList bigB = new ArrayList();
    for (int i = 0; i < 100; i++)
      bigB.add(new Integer(i + 200));
    Assert.assertTrue(! OcCollectionUtils.hasIntersection(bigA, bigB));
    Assert.assertTrue(! OcCollectionUtils.hasIntersection(bigB, bigA));
    Assert.assertTrue(OcCollectionUtils.hasIntersection(bigA, bigA));
    bigB.add(new Integer(10));
    Assert.assertTrue(OcCollectionUtils.hasIntersection(bigA, bigB));
    Assert.assertTrue(OcCollectionUtils.hasIntersection(bigB, bigA));
    System.out.println("** testHasIntersection OK **");
  }
  
  /**
   * Tests the StringUtils.removeDelimiters method.
   */
  public void testRemoveDelimiters() {
    System.out.println("** testRemoveDelimiters **");
    Assert.assertEquals("abc", StringUtils.removeDelimiters("\"abc\""));
    System.out.println("** testRemoveDelimiters OK**");
  }
  
  /**
   * Tests the StringUtils.isDelimitedString method.
   */
  public void testIsDelimitedString() {
    System.out.println("** testIsDelimitedString **");
    Assert.assertTrue(StringUtils.isDelimitedString("\"abc\""));
    Assert.assertTrue(StringUtils.isDelimitedString("\"\""));
    Assert.assertTrue(! StringUtils.isDelimitedString("\""));
    Assert.assertTrue(! StringUtils.isDelimitedString(new Integer(1)));
    Assert.assertTrue(! StringUtils.isDelimitedString("abc\""));
    Assert.assertTrue(! StringUtils.isDelimitedString("\"abc"));
    System.out.println("** testIsDelimitedString OK **");
  }
  
  /**
   * Tests the StringUtils.isNumeric method.
   */
  public void testIsNumeric() {
    System.out.println("** testIsNumeric **");
    Assert.assertTrue(StringUtils.isNumeric("0"));
    Assert.assertTrue(StringUtils.isNumeric("1"));
    Assert.assertTrue(StringUtils.isNumeric("2"));
    Assert.assertTrue(StringUtils.isNumeric("3"));
    Assert.assertTrue(StringUtils.isNumeric("4"));
    Assert.assertTrue(StringUtils.isNumeric("5"));
    Assert.assertTrue(StringUtils.isNumeric("6"));
    Assert.assertTrue(StringUtils.isNumeric("7"));
    Assert.assertTrue(StringUtils.isNumeric("8"));
    Assert.assertTrue(StringUtils.isNumeric("9"));
    Assert.assertTrue(! StringUtils.isNumeric("A"));
    Assert.assertTrue(! StringUtils.isNumeric("@"));
    Assert.assertTrue(! StringUtils.isNumeric("."));
    Assert.assertTrue(StringUtils.isNumeric("12345"));
    Assert.assertTrue(! StringUtils.isNumeric("123.45"));
    Assert.assertTrue(! StringUtils.isNumeric("123-45"));
    Assert.assertTrue(! StringUtils.isNumeric("12345+"));
    Assert.assertTrue(! StringUtils.isNumeric("+"));
    Assert.assertTrue(! StringUtils.isNumeric("-"));
    Assert.assertTrue(StringUtils.isNumeric("+1"));
    Assert.assertTrue(StringUtils.isNumeric("-1"));
    Assert.assertTrue(StringUtils.isNumeric("+12345"));
    Assert.assertTrue(StringUtils.isNumeric("-12345"));
    System.out.println("** testIsNumeric OK **");
  }
  
  /**
   * Tests the StringUtils.wordsToString method.
   */
  public void testWordsToString() {
    System.out.println("** testWordsToString **");
    ArrayList words = new ArrayList();
    Assert.assertEquals("", StringUtils.wordsToPhrase(words));
    words.add("word1");
    Assert.assertEquals("word1", StringUtils.wordsToPhrase(words));
    words.add("word2");
    Assert.assertEquals("word1 word2", StringUtils.wordsToPhrase(words));
    words.add("word3");
    Assert.assertEquals("word1 word2 word3", StringUtils.wordsToPhrase(words));
    
    System.out.println("** testWordsToString OK **");
  }
  
  /**
   * Tests the StringUtils.escapeDoubleQuotes method.
   */
  public void testEscapeDoubleQuotes() {
    System.out.println("** testEscapeDoubleQuotes **");
    String string = "";
    Assert.assertEquals(string, StringUtils.escapeDoubleQuotes(string));
    string = "1 2 3";
    Assert.assertEquals(string, StringUtils.escapeDoubleQuotes(string));
    string = "'1' '2' '3'";
    Assert.assertEquals(string, StringUtils.escapeDoubleQuotes(string));
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\"");
    stringBuffer.append("abc");
    stringBuffer.append("\"");
    string = stringBuffer.toString();
    String expectedString = "\\\"abc\\\"";
    String escapedString = StringUtils.escapeDoubleQuotes(string);
    Assert.assertEquals(expectedString, escapedString);
    
    System.out.println("** testEscapeDoubleQuotes OK **");
  }
  
  /**
   * Tests the Base64 methods.
   */
  public void testBase64() {
    System.out.println("** testBase64 **");
    CycList request = new CycList();
    request.add(CycObjectFactory.makeCycSymbol("list"));
    request.add(":none");
    request.add(CycObjectFactory.makeCycSymbol(":none"));
    String encodedRequest = null;
    Object response = null;
    try {
      encodedRequest = org.opencyc.util.Base64.encodeCycObject(request, 0);
      response = org.opencyc.util.Base64.decodeCycObject(encodedRequest, 0);
    }
    catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    Assert.assertNotNull(response);
    Assert.assertTrue(response instanceof CycList);
    Assert.assertEquals(request, (CycList) response);
    
    request = new CycList();
    request.add(CycObjectFactory.makeCycSymbol("A"));
    request.setDottedElement(CycObjectFactory.makeCycSymbol("B"));
    encodedRequest = null;
    response = null;
    try {
      encodedRequest = org.opencyc.util.Base64.encodeCycObject(request, 0);
      response = org.opencyc.util.Base64.decodeCycObject(encodedRequest, 0);
    }
    catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    Assert.assertNotNull(response);
    Assert.assertTrue(response instanceof CycList);
    Assert.assertEquals(request, (CycList) response);
    System.out.println("** testBase64 OK **");
  }
  
  /**
   * Tests the MyStreamTokenizer methods.
   */
  public void testMyStreamTokenizer() {
    System.out.println("** testMyStreamTokenizer **");
    
    final String testString1 = "xyz\n;abc\ndef";
    
    final StringReader stringReader = new StringReader(testString1);
    final MyStreamTokenizer st = new MyStreamTokenizer(stringReader);
    st.resetSyntax();
    st.ordinaryChar('(');
    st.ordinaryChar(')');
    st.ordinaryChar('\'');
    st.ordinaryChar('`');
    st.ordinaryChar('.');
    st.whitespaceChars(0, ' ');
    st.quoteChar('"');
    st.wordChars('0', '9');
    st.wordChars('a', 'z');
    st.wordChars('A', 'Z');
    st.wordChars(128 + 32, 255);
    st.wordChars('=', '=');
    st.wordChars('+', '+');
    st.wordChars('-', '-');
    st.wordChars('_', '_');
    st.wordChars('<', '<');
    st.wordChars('>', '>');
    st.wordChars('*', '*');
    st.wordChars('/', '/');
    st.wordChars('#', '#');
    st.wordChars(':', ':');
    st.wordChars('!', '!');
    st.wordChars('$', '$');
    st.wordChars('?', '?');
    st.wordChars('%', '%');
    st.wordChars('&', '&');
    st.wordChars('.', '.');
    st.slashSlashComments(false);
    st.slashStarComments(false);
    st.commentChar(';');
    st.wordChars('?', '?');
    st.wordChars('%', '%');
    st.wordChars('&', '&');
    st.eolIsSignificant(false);
    
    try {
      st.nextToken();
      Assert.assertEquals("xyz", st.sval);
      st.nextToken();
      Assert.assertEquals("def", st.sval);
    }
    catch (IOException e) {
      Assert.fail(e.getMessage());
    }
    


    System.out.println("** testMyStreamTokenizer OK **");
  }


  /**
   * Tests the OcCollectionUtils.hasDuplicates method.
   */
  public void testIs7BitASCII() {
    System.out.println("** testIs7BitASCII **");
    Assert.assertTrue(StringUtils.is7BitASCII("abc"));
    StringBuffer sb=new StringBuffer();
    sb.append('a');
    sb.append((char)140);
    Assert.assertTrue(!StringUtils.is7BitASCII(sb.toString()));
    sb.append('c');
    Assert.assertTrue(!StringUtils.is7BitASCII(sb.toString()));
    System.out.println("** testIs7BitASCII OK **");
  }

  public void testUnicodeEscaped(){
    System.out.println("** testUnicodeEscaped **");
    Assert.assertEquals("abc", (StringUtils.UnicodeEscaped("abc")));
    Assert.assertEquals("ab\\\"c", StringUtils.UnicodeEscaped("ab\"c"));
    Assert.assertEquals("ab\\\\c", StringUtils.UnicodeEscaped("ab\\c"));
    StringBuffer sb=new StringBuffer();
    sb.append((char)0xff00);
    String testString = sb.toString();
    //System.out.println(testString);
    Assert.assertEquals("&uff00;", StringUtils.UnicodeEscaped(testString));
    sb=new StringBuffer();
    sb.append((char)0xff00);    
    sb.append(';');
    testString = sb.toString();
    //System.out.println(testString);
    Assert.assertEquals("&uff00;;", StringUtils.UnicodeEscaped(testString));
    sb=new StringBuffer();
    sb.append((char)0xff00);    
    sb.append(';');
    sb.append('a');
    testString = sb.toString();
    //System.out.println(testString);
    Assert.assertEquals("&uff00;;a", StringUtils.UnicodeEscaped(testString));
    System.out.println("** testUnicodeEscaped OK **");
  }

  /** Test isWhitespace. */
  public void testIsWhitespace() {
    System.out.println("** testIsWhitespace **");
    String string = "abc";
    Assert.assertTrue(! StringUtils.isWhitespace(string));
    string = " abc ";
    Assert.assertTrue(! StringUtils.isWhitespace(string));
    string = "";
    Assert.assertTrue(! StringUtils.isWhitespace(string));
    string = " ";
    Assert.assertTrue(StringUtils.isWhitespace(string));
    string = " \n\r\t  ";
    Assert.assertTrue(StringUtils.isWhitespace(string));
    System.out.println("** testIsWhitespace OK **");
  }
  
  /** Test stripLeading. */
  public void testStripLeading() {
    System.out.println("** testStripLeading **");
    String string = "abc";
    Assert.assertEquals("abc", StringUtils.stripLeading(string, ' '));
    string = "";
    Assert.assertEquals("", StringUtils.stripLeading(string, ' '));
    string = " abc ";
    Assert.assertEquals("abc ", StringUtils.stripLeading(string, ' '));
    string = "zzzzzzzzabc ";
    Assert.assertEquals("abc ", StringUtils.stripLeading(string, 'z'));
    string = "\n";
    Assert.assertEquals("", StringUtils.stripLeading(string, '\n'));
    System.out.println("** testStripLeading OK **");
  }
  
  /** Test stripTrailing. */
  public void testStripTrailing() {
    System.out.println("** testStripTrailing **");
    String string = "abc";
    Assert.assertEquals("abc", StringUtils.stripTrailing(string, ' '));
    string = "";
    Assert.assertEquals("", StringUtils.stripTrailing(string, ' '));
    string = " abc ";
    Assert.assertEquals(" abc", StringUtils.stripTrailing(string, ' '));
    string = " abczzzzzzzz";
    Assert.assertEquals(" abc", StringUtils.stripTrailing(string, 'z'));
    System.out.println("** testStripTrailing OK **");
  }
  
  /** Test stripTrailingBlanks. */
  public void testStripTrailingBlanks() {
    System.out.println("** testStripTrailingBlanks **");
    String string = "abc";
    Assert.assertEquals("abc", StringUtils.stripTrailingBlanks(string));
    string = "";
    Assert.assertEquals("", StringUtils.stripTrailingBlanks(string));
    string = " abc ";
    Assert.assertEquals(" abc", StringUtils.stripTrailingBlanks(string));
    string = " abc     ";
    Assert.assertEquals(" abc", StringUtils.stripTrailingBlanks(string));
    System.out.println("** testStripTrailingBlanks OK **");
  }
  /** Test Log println. */
  public void testLogPrintln() {
    System.out.println("** testLogPrintln **");
    Log.current.println("test log line");
    System.out.println("** testLogPrintln OK **");
  }
  
}

