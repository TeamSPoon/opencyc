package org.opencyc.cycobject;
import junit.framework.*;
import java.util.*;
import org.opencyc.api.*;

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

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        runTests();
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
    public static void runTests() {
        TestSuite testSuite = new TestSuite(UnitTest.class);
        testSuite.addTest(new UnitTest("testGuid"));
        testSuite.addTest(new UnitTest("testCycSymbol"));
        testSuite.addTest(new UnitTest("testVariable"));
        testSuite.addTest(new UnitTest("testCycConstant"));
        testSuite.addTest(new UnitTest("testCycNart"));
        testSuite.addTest(new UnitTest("testCycList"));
        testSuite.addTest(new UnitTest("testCycListVisitor"));
        TestResult testResult = new TestResult();
        testSuite.run(testResult);
    }

    /**
     * Tests the test harness itself.
     */
    public void testTestHarness() {
        System.out.println("** testTestHarness **");
        Assert.assertTrue(true);
        System.out.println("** testTestHarness OK **");
    }

    /**
     * Tests <tt>Guid</tt> object behavior.
     */
    public void testGuid() {
        System.out.println("** testGuid **");
        Guid.resetCache();
        Assert.assertEquals(0, Guid.getCacheSize());
        String guidString = "bd58c19d-9c29-11b1-9dad-c379636f7270";
        Guid guid = Guid.makeGuid(guidString);
        Assert.assertEquals(1, Guid.getCacheSize());
        Assert.assertEquals(guidString, guid.toString());
        Guid guid2 = Guid.getCache(guidString);
        Assert.assertEquals(guid, guid2);
        Guid guid3 = Guid.makeGuid(guidString);
        Assert.assertEquals(guid, guid3);
        Assert.assertEquals(1, Guid.getCacheSize());
        System.out.println("** testGuid OK **");
    }

    /**
     * Tests <tt>CycSymbol</tt> object behavior.
     */
    public void testCycSymbol() {
        System.out.println("** testCycSymbol **");
        CycSymbol.resetCache();
        Assert.assertEquals(4, CycSymbol.getCacheSize());
        String symbolName = "WHY-ISA?";
        CycSymbol cycSymbol = CycSymbol.makeCycSymbol(symbolName);
        Assert.assertEquals(5, cycSymbol.getCacheSize());
        Assert.assertEquals(symbolName, cycSymbol.toString());
        Assert.assertNotNull(CycSymbol.getCache(symbolName));
        CycSymbol cycSymbol2 = CycSymbol.getCache(symbolName);
        Assert.assertEquals(cycSymbol, cycSymbol2);
        CycSymbol cycSymbol3 = CycSymbol.makeCycSymbol(symbolName);
        Assert.assertEquals(cycSymbol, cycSymbol3);
        Assert.assertEquals(5, CycSymbol.getCacheSize());
        String symbolName4 = "WHY-ISA?";
        CycSymbol cycSymbol4 = CycSymbol.makeCycSymbol(symbolName4);
        Assert.assertEquals(cycSymbol.toString(), cycSymbol4.toString());
        Assert.assertEquals(cycSymbol, cycSymbol4);

        // compareTo
        ArrayList symbols = new ArrayList();
        symbols.add(CycSymbol.makeCycSymbol("isa?"));
        symbols.add(CycSymbol.makeCycSymbol("define-private"));
        symbols.add(CycSymbol.makeCycSymbol("nil"));
        Collections.sort(symbols);
        Assert.assertEquals("[DEFINE-PRIVATE, ISA?, NIL]", symbols.toString());

        // isKeyword
        CycSymbol cycSymbol5 = CycSymbol.makeCycSymbol("nil");
        Assert.assertTrue(! cycSymbol5.isKeyword());
        CycSymbol cycSymbol6 = CycSymbol.makeCycSymbol(":pos");
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

        System.out.println("** testCycSymbol OK **");
    }

    /**
     * Tests <tt>CycConstant</tt> object behavior.
     */
    public void testCycConstant() {
        System.out.println("** testCycConstant **");
        CycConstant.resetCaches();
        Assert.assertEquals(0, CycConstant.getCacheByIdSize());
        Assert.assertEquals(0, CycConstant.getCacheByNameSize());
        String guidString = "bd58c19d-9c29-11b1-9dad-c379636f7270";
        String constantName = "#$TameAnimal";
        CycConstant cycConstant1 =
            new CycConstant(constantName,
                            Guid.makeGuid(guidString),
                            new Integer(61101217));
        Assert.assertNotNull(cycConstant1);
        Assert.assertEquals(1, CycConstant.getCacheByIdSize());
        Assert.assertEquals(1, CycConstant.getCacheByNameSize());
        Assert.assertEquals(constantName.substring(2), cycConstant1.toString());
        Assert.assertEquals(constantName, cycConstant1.cyclify());
        Assert.assertEquals(guidString, cycConstant1.getGuid().toString());

        // Attempt to create a duplicate returns the cached existing object.
        CycConstant cycConstant2 =
            new CycConstant(constantName,
                            Guid.makeGuid(guidString),
                            new Integer(61101217));
        Assert.assertEquals(1, CycConstant.getCacheByIdSize());
        Assert.assertEquals(1, CycConstant.getCacheByNameSize());
        Assert.assertEquals(cycConstant1, cycConstant2);

        CycConstant cycConstant3 =
            new CycConstant(constantName,
                            Guid.makeGuid(guidString),
                            new Integer(61101217));
        Assert.assertEquals(cycConstant1.toString(), cycConstant3.toString());
        Assert.assertEquals(cycConstant1.cyclify(), cycConstant3.cyclify());
        Assert.assertEquals(cycConstant1, cycConstant3);


        // compareTo
        ArrayList constants = new ArrayList();

        constants.add(new CycConstant("#$Dog",
                                      Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"),
                                      new Integer(23200)));
        constants.add(new CycConstant("#$Cat",
                                      Guid.makeGuid("bd590573-9c29-11b1-9dad-c379636f7270"),
                                      new Integer(34163)));
        constants.add(new CycConstant("#$Brazil",
                                      Guid.makeGuid("bd588f01-9c29-11b1-9dad-c379636f7270"),
                                      new Integer(3841)));
        constants.add(new CycConstant("#$Collection",
                                      Guid.makeGuid("bd5880cc-9c29-11b1-9dad-c379636f7270"),
                                      new Integer(204)));
        Collections.sort(constants);
        Assert.assertEquals("[Brazil, Cat, Collection, Dog]", constants.toString());

        System.out.println("** testCycConstant OK **");
    }

    /**
     * Tests <tt>CycNart</tt> object behavior.
     */
    public void testCycNart() {
        System.out.println("** testCycNart **");
        CycConstant fruitFn =
            new CycConstant("FruitFn",
                            Guid.makeGuid("bd58c19d-9c29-11b1-9dad-c379636f7270"),
                            new Integer(10614));
        CycConstant appleTree =
            new CycConstant("AppleTree",
                            Guid.makeGuid("bd58c19d-9c29-11b1-9dad-c379636f0000"),
                            new Integer(16797));
        CycNart cycNart = new CycNart(fruitFn, appleTree);
        Assert.assertNotNull(cycNart);
        Assert.assertEquals("(FruitFn AppleTree)",cycNart.toString());
        Assert.assertEquals("(#$FruitFn #$AppleTree)",cycNart.cyclify());

        CycConstant fruitFn2 =
            new CycConstant("FruitFn",
                            Guid.makeGuid("bd58c19d-9c29-11b1-9dad-c379636f7270"),
                            new Integer(10614));
        CycConstant appleTree2 =
            new CycConstant("AppleTree",
                            Guid.makeGuid("bd58c19d-9c29-11b1-9dad-c379636f0000"),
                            new Integer(16797));
        CycNart cycNart2 = new CycNart(fruitFn2, appleTree2);
        Assert.assertEquals(cycNart.toString(), cycNart2.toString());
        Assert.assertEquals(cycNart, cycNart2);

        // compareTo
        ArrayList narts = new ArrayList();
        CycConstant governmentFn =
            new CycConstant("GovernmentFn",
                            Guid.makeGuid("c10aef3d-9c29-11b1-9dad-c379636f7270"),
                            new Integer(62025533));
        CycConstant brazil =
            new CycConstant("#$Brazil",
                            Guid.makeGuid("bd588f01-9c29-11b1-9dad-c379636f7270"),
                            new Integer(3841));
        CycList nartCycList = new CycList();
        nartCycList.add(governmentFn);
        nartCycList.add(brazil);
        narts.add(new CycNart(nartCycList));
        Assert.assertEquals("[(GovernmentFn Brazil)]", narts.toString());
        CycConstant plusFn =
            new CycConstant("PlusFn",
                            Guid.makeGuid("bd5880ae-9c29-11b1-9dad-c379636f7270"),
                            new Integer(174));
        CycList nartCycList2 = new CycList();
        nartCycList2.add(plusFn);
        nartCycList2.add(new Integer(100));
        narts.add(new CycNart(nartCycList2));
        CycList nartCycList3 = new CycList();
        nartCycList3.add(fruitFn2);
        nartCycList3.add(appleTree2);
        narts.add(new CycNart(nartCycList3));
        Collections.sort(narts);
        Assert.assertEquals("[(FruitFn AppleTree), (GovernmentFn Brazil), (PlusFn 100)]",
                            narts.toString());

        // coerceToCycNart
        CycNart cycNart4 = new CycNart(fruitFn2, appleTree2);
        Assert.assertEquals(cycNart4, CycNart.coerceToCycNart(cycNart4));
        CycList cycList4 = new CycList();
        cycList4.add(fruitFn2);
        cycList4.add(appleTree2);
        Assert.assertEquals(cycNart2, CycNart.coerceToCycNart(cycList4));

        System.out.println("** testCycNart OK **");
    }

    /**
     * Tests <tt>CycVariable</tt> object behavior.
     */
    public void testCycVariable() {
        System.out.println("** testCycVariable **");
        CycVariable cycVariable1 = new CycVariable("?X");
        Assert.assertNotNull(cycVariable1);
        Assert.assertEquals("?X", cycVariable1.toString());
        Assert.assertEquals("?X", cycVariable1.cyclify());
        System.out.println("** testCycVariable OK **");
        CycVariable cycVariable2 = new CycVariable("?variable");
        Assert.assertNotNull(cycVariable2);
        Assert.assertEquals("?variable", cycVariable2.toString());
        Assert.assertEquals("?variable", cycVariable2.cyclify());
        CycVariable cycVariable3 = new CycVariable("?X");
        Assert.assertEquals(cycVariable1.toString(), cycVariable3.toString());
        Assert.assertEquals(cycVariable1.cyclify(), cycVariable3.cyclify());
        Assert.assertEquals(cycVariable1, cycVariable3);

        // compareTo
        ArrayList variables = new ArrayList();
        variables.add(CycVariable.makeCycVariable("?y"));
        variables.add(CycVariable.makeCycVariable("?Z"));
        variables.add(CycVariable.makeCycVariable("?Y"));
        variables.add(CycVariable.makeCycVariable("?X"));
        variables.add(CycVariable.makeCycVariable("?z"));
        variables.add(CycVariable.makeCycVariable("?x"));
        Collections.sort(variables);
        Assert.assertEquals("[?X, ?Y, ?Z, ?x, ?y, ?z]", variables.toString());

        // makeUniqueCycVariable
        CycVariable x = CycVariable.makeCycVariable("?x");
        CycVariable x1 = CycVariable.makeUniqueCycVariable(x);
        CycVariable x2 = CycVariable.makeUniqueCycVariable(x);
        CycVariable x3 = CycVariable.makeUniqueCycVariable(x);
        Assert.assertTrue(! (x.equals(x1)));
        Assert.assertTrue(! (x.equals(x2)));
        Assert.assertTrue(! (x.equals(x3)));
        Assert.assertTrue(! (x1.equals(x2)));
        Assert.assertTrue(x.cyclify().equals("?x"));
        Assert.assertTrue(x1.cyclify().startsWith("?x_"));
        Assert.assertTrue(x3.cyclify().startsWith("?x_"));

        System.out.println("** testCycVariable OK **");
    }

    /**
     * Tests <tt>CycList</tt> object behavior.
     */
    public void testCycList() {
        System.out.println("** testCycList **");


        // Simple empty list constructor.
        ArrayList arrayList = new ArrayList();
        CycList cycList = new CycList(arrayList);
        Assert.assertNotNull(cycList);
        Assert.assertEquals("()", cycList.toString());

        // Construct list of one element.
        ArrayList arrayList2 = new ArrayList();
        CycConstant brazil =
            new CycConstant("#$Brazil",
                            Guid.makeGuid("bd588f01-9c29-11b1-9dad-c379636f7270"),
                            new Integer(3841));
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
        Assert.assertTrue(cycList5.rest().size() == 0);

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
        Assert.assertTrue(cycList7.size() == 1);
        Assert.assertEquals("(10 . Brazil)", cycList7.toString());
        //CycListParser.verbosity = 10;

        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            CycList cycList7a = cycAccess.makeCycList("(a . (b . (c . (d))))");
            Assert.assertEquals("(A B C D)", cycList7a.toString());
            CycList cycList7b = cycAccess.makeCycList("((a . b) . (c . d))");
            Assert.assertEquals("((A . B) C . D)", cycList7b.toString());
            CycList cycList7c = cycAccess.makeCycList("((a . (b)) . (c . (d)))");
            Assert.assertEquals("((A B) C D)", cycList7c.toString());
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // construct
        Object object1 = CycList.construct(brazil, CycSymbol.nil);
        Assert.assertNotNull(object1);
        Assert.assertTrue(object1 instanceof CycList);
        Assert.assertEquals("(Brazil)", object1.toString());

        CycList cycList8 = CycList.construct(brazil, "Atlantic");
        Assert.assertEquals("(Brazil . \"Atlantic\")", cycList8.toString());

        CycList cycList9 = CycList.construct(brazil, new Integer(1));
        Assert.assertEquals("(Brazil . 1)", cycList9.toString());

        CycList cycList10 = CycList.construct(brazil, CycSymbol.makeCycSymbol("foo"));
        Assert.assertEquals("(Brazil . FOO)", cycList10.toString());

        // Parse strings to make CycLists.
        try {
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
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        // subst
        try {
            CycList cycList18 = cycAccess.makeCycList("(b)");
            CycList cycList19 = cycList18.subst(CycSymbol.makeCycSymbol("x"), CycSymbol.makeCycSymbol("a"));
            Assert.assertEquals(cycAccess.makeCycList("(b)"), cycList19);
            CycList cycList20 = cycAccess.makeCycList("(a)");
            CycList cycList21 = cycList20.subst(CycSymbol.makeCycSymbol("x"), CycSymbol.makeCycSymbol("a"));
            Assert.assertEquals(cycAccess.makeCycList("(x)"), cycList21);
            CycList cycList22 = cycAccess.makeCycList("((a))");
            CycList cycList23 = cycList22.subst(CycSymbol.makeCycSymbol("x"), CycSymbol.makeCycSymbol("a"));
            Assert.assertEquals(cycAccess.makeCycList("((x))"), cycList23);
            CycList cycList24 = cycAccess.makeCycList("((a) (b c) (((d))))");
            CycList cycList25 = cycList24.subst(CycSymbol.makeCycSymbol("x"), CycSymbol.makeCycSymbol("a"));
            Assert.assertEquals(cycAccess.makeCycList("((x) (b c) (((d))))"), cycList25);
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // containsDuplicates
        try {
            CycList cycList26 = cycAccess.makeCycList("(a b c d)");
            Assert.assertTrue(! cycList26.containsDuplicates());
            CycList cycList27 = cycAccess.makeCycList("(a a c d)");
            Assert.assertTrue(cycList27.containsDuplicates());
            CycList cycList28 = cycAccess.makeCycList("(a b c c)");
            Assert.assertTrue(cycList28.containsDuplicates());
            CycList cycList29 = cycAccess.makeCycList("(a (b) (b) c)");
            Assert.assertTrue(cycList29.containsDuplicates());
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // list
        CycList cycList30 = CycList.list(CycSymbol.makeCycSymbol("a"));
        Assert.assertEquals("(A)", cycList30.toString());
        CycList cycList31 = CycList.list(CycSymbol.makeCycSymbol("a"),
                                         CycSymbol.makeCycSymbol("b"));
        Assert.assertEquals("(A B)", cycList31.toString());
        CycList cycList32 = CycList.list(CycSymbol.makeCycSymbol("a"),
                                         CycSymbol.makeCycSymbol("b"),
                                         CycSymbol.makeCycSymbol("c"));
        Assert.assertEquals("(A B C)", cycList32.toString());

        // combinationsOf
        try {
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
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // randomPermutation
        try {
            CycList cycList34 = cycAccess.makeCycList("(1 2 3 4 5 6 7 8 9 10)");
            CycList permutedCycList = cycList34.randomPermutation();
            Assert.assertEquals(10, permutedCycList.size());
            Assert.assertTrue(permutedCycList.contains(new Integer(2)));
            Assert.assertTrue(! permutedCycList.containsDuplicates());
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // doesElementPrecedeOthers
        try {
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
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // clone
        try {
            CycList cycList36 = cycAccess.makeCycList("(1 2 3 4 5)");
            CycList cycList37 = (CycList) cycList36.clone();
            Assert.assertEquals(cycList36, cycList37);
            Assert.assertTrue(cycList36 != cycList37);
            CycList cycList38 = cycAccess.makeCycList("(1 2 3 4 5 . 6)");
            CycList cycList39 = (CycList) cycList38.clone();
            Assert.assertEquals(cycList38, cycList39);
            Assert.assertTrue(cycList38 != cycList39);
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // deepCopy
        try {
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
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // last
        CycList cycList46 = cycAccess.makeCycList("(8 7 6)");
        Assert.assertEquals(new Integer(6), cycList46.last());

        try {
            cycAccess.close();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        System.out.println("** testCycList OK **");
    }

    /**
     * Tests <tt>CycListVisitor</tt> object behavior.
     */
    public void testCycListVisitor() {
        System.out.println("** testCycListVisitor **");

        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
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
            Assert.assertEquals(CycSymbol.makeCycSymbol(":foo"), e2.nextElement());
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
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            cycAccess.close();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        System.out.println("** testCycListVisitor OK **");
    }
}