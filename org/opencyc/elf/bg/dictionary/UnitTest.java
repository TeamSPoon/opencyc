package org.opencyc.elf.bg.dictionary;

//// Internal Imports
import org.opencyc.cycobject.CycList;

import org.opencyc.elf.bg.expression.Operator;
import org.opencyc.elf.bg.expression.OperatorExpression;

import org.opencyc.elf.wm.state.State;
import org.opencyc.elf.wm.state.StateVariable;

//// External Imports
import java.util.Hashtable;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;


/** Provides a suite of JUnit test cases for the org.opencyc.elf.bg.dictionary package.
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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
  
  //// Constructors
  
  /** Construct a new UnitTest object.
   * 
   * @param name the test case name.
   */
  public UnitTest(String name) {
    super(name);
  }
  
  //// Public Area

  /** Runs the unit tests
   * @return the test suite
   */
  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(new UnitTest("testOperatorExpression"));

    return testSuite;
  }

  /** Tests operator expression behavior. */
  public void testOperatorExpression() {
    System.out.println("\n*** testOperatorExpression ***");
    State state = new State();
    
    // TheEmptyDictionary
    new TheEmptyDictionary();
    Operator theEmptyDictionary = TheEmptyDictionary.getInstance();
    Assert.assertNotNull(theEmptyDictionary.evaluate(null, state));
    Assert.assertEquals(new Hashtable(), theEmptyDictionary.evaluate(null, state));
    OperatorExpression operatorExpression1 = new OperatorExpression(theEmptyDictionary);
    Assert.assertEquals("(the-empty-dictionary)", operatorExpression1.toString());
    Assert.assertEquals(new Hashtable(), operatorExpression1.evaluate(state));
    
    // DictionaryEnter
    new DictionaryEnter();
    Operator dictionaryEnter = DictionaryEnter.getInstance();
    Hashtable dictionary = new Hashtable();
    operatorExpression1 = new OperatorExpression(dictionaryEnter, 
                                                 "key", 
                                                 "value", 
                                                 dictionary);
    Assert.assertEquals("(dictionary-enter \"key\" \"value\" {})", operatorExpression1.toString());
    Assert.assertEquals("key", operatorExpression1.evaluate(state));
    Assert.assertTrue(dictionary.containsKey("key"));
    
    // DictionaryLookup
    //TODO
    
    System.out.println("*** testOperatorExpression OK ***");
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
  /** Main method in case tracing is prefered over running JUnit.
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}