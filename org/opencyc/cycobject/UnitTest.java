package org.opencyc.cycobject;

/**
 * A suite of JUnit test cases for the <tt>org.opencyc.cycobject</tt> package.<p>
 *
 * @version $Id$
 * @author Stephen Reed
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 */

import junit.framework.*;
import java.util.*;

public class UnitTest extends TestCase {

    /**
     * Construct a new UnitTest object.
     * @param name the test case name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Run the unit tests
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
     * Test the test harness itself.
     */
    public void testTestHarness() {
        System.out.println("** testTestHarness **");
        Assert.assertTrue(true);
        System.out.println("** testTestHarness OK **");
    }

    /**
     * Test <tt>Guid</tt> object behavior.
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
     * Test <tt>CycSymbol</tt> object behavior.
     */
    public void testCycSymbol() {
        System.out.println("** testCycSymbol **");
        CycSymbol.resetCache();
        Assert.assertEquals(4, CycSymbol.getCacheSize());
        String symbolName = "why-isa?";
        CycSymbol cycSymbol = CycSymbol.makeCycSymbol(symbolName);
        Assert.assertEquals(5, cycSymbol.getCacheSize());
        Assert.assertEquals(symbolName, cycSymbol.toString());
        Assert.assertNotNull(CycSymbol.getCache(symbolName));
        CycSymbol cycSymbol2 = CycSymbol.getCache(symbolName);
        Assert.assertEquals(cycSymbol, cycSymbol2);
        CycSymbol cycSymbol3 = CycSymbol.makeCycSymbol(symbolName);
        Assert.assertEquals(cycSymbol, cycSymbol3);
        Assert.assertEquals(5, CycSymbol.getCacheSize());
        System.out.println("** testCycSymbol OK **");
    }

    /**
     * Test <tt>CycConstant</tt> object behavior.
     */
    public void testCycConstant() {
        System.out.println("** testCycConstant **");
        CycConstant.resetCache();
        Assert.assertEquals(0, CycConstant.getCacheSize());
        String guidString = "bd58c19d-9c29-11b1-9dad-c379636f7270";
        String constantName = "#$TameAnimal";
        CycConstant cycConstant1 = CycConstant.makeCycConstant(guidString, constantName);
        Assert.assertNotNull(cycConstant1);
        Assert.assertEquals(1, CycConstant.getCacheSize());
        Assert.assertEquals(constantName.substring(2), cycConstant1.toString());
        Assert.assertEquals(constantName, cycConstant1.cycName());
        Assert.assertEquals(guidString, cycConstant1.guid.toString());

        // Attempt to create a duplicate returns the cached existing object.
        CycConstant cycConstant2 = CycConstant.makeCycConstant(guidString, constantName);
        Assert.assertEquals(1, CycConstant.getCacheSize());
        Assert.assertEquals(cycConstant1, cycConstant2);
        System.out.println("** testCycConstant OK **");
    }

    /**
     * Test <tt>CycNart</tt> object behavior.
     */
    public void testCycNart() {
        System.out.println("** testCycNart **");
        CycConstant fruitFn =
            CycConstant.makeCycConstant("bd58c19d-9c29-11b1-9dad-c379636f7270",
                            "FruitFn");
        CycConstant appleTree =
            CycConstant.makeCycConstant("bd58c19d-9c29-11b1-9dad-c379636f0000",
                            "AppleTree");
        CycNart cycNart = new CycNart(fruitFn, appleTree);
        Assert.assertNotNull(cycNart);
        Assert.assertEquals("(FruitFn AppleTree)",cycNart.toString());
        Assert.assertEquals("(#$FruitFn #$AppleTree)",cycNart.cyclify());

        CycConstant fruitFn2 =
            CycConstant.makeCycConstant("bd58c19d-9c29-11b1-9dad-c379636f7270",
                            "FruitFn");
        CycConstant appleTree2 =
            CycConstant.makeCycConstant("bd58c19d-9c29-11b1-9dad-c379636f0000",
                            "AppleTree");
        CycNart cycNart2 = new CycNart(fruitFn2, appleTree2);
        Assert.assertEquals(cycNart.toString(), cycNart2.toString());
        Assert.assertEquals(cycNart, cycNart2);

        System.out.println("** testCycNart OK **");
    }

    /**
     * Test <tt>CycVariable</tt> object behavior.
     */
    public void testCycVariable() {
        System.out.println("** testCycVariable **");
        CycVariable cycVariable1 = new CycVariable("?X");
        Assert.assertNotNull(cycVariable1);
        Assert.assertEquals("X", cycVariable1.toString());
        Assert.assertEquals("?X", cycVariable1.cyclify());
        System.out.println("** testCycVariable OK **");
        CycVariable cycVariable2 = new CycVariable("?variable");
        Assert.assertNotNull(cycVariable2);
        Assert.assertEquals("variable", cycVariable2.toString());
        Assert.assertEquals("?variable", cycVariable2.cyclify());
        System.out.println("** testCycVariable OK **");
    }

    /**
     * Test <tt>CycList</tt> object behavior.
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
            CycConstant.makeCycConstant("bd58c19d-9c29-11b1-9dad-c379636f0099",
                                        "Brazil");
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
        CycList cycList7a = new CycList("(a . (b . (c . (d))))");
        Assert.assertEquals("(a b c d)", cycList7a.toString());
        CycList cycList7b = new CycList("((a . b) . (c . d))");
        Assert.assertEquals("((a . b) c . d)", cycList7b.toString());
        CycList cycList7c = new CycList("((a . (b)) . (c . (d)))");
        Assert.assertEquals("((a b) c d)", cycList7c.toString());

        // construct
        Object object1 = CycList.construct(brazil, CycSymbol.nil);
        Assert.assertNotNull(object1);
        Assert.assertTrue(object1 instanceof CycList);
        Assert.assertEquals("(Brazil)", object1.toString());

        CycList cycList8 = CycList.construct(brazil, "Atlantic");
        Assert.assertEquals("(Brazil . \"Atlantic\")", cycList8.toString());

        CycList cycList9 = CycList.construct(brazil, new Integer(1));
        Assert.assertEquals("(Brazil . 1)", cycList9.toString());

        CycList cycList10 = CycList.construct(brazil, new CycSymbol("foo"));
        Assert.assertEquals("(Brazil . foo)", cycList10.toString());

        // Parse strings to make CycLists.
        String listAsString = "()";
        CycList cycList11 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList11.toString());
        listAsString = "(1)";
        CycList cycList12 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList12.toString());
        listAsString = "(1 2 3 4 5)";
        CycList cycList13 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList13.toString());
        listAsString = "(\"1\" \"bar\" A #$Brazil z 4.25 :keyword ?collection nil)";
        CycList cycList14 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList14.cyclify());
        listAsString = "((a))";
        CycList cycList15 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList15.toString());
        listAsString = "((a) (b c) (((d))))";
        CycList cycList16 = new CycList(listAsString);
        Assert.assertEquals(listAsString, cycList16.toString());
        CycList cycList17 = new CycList(listAsString);
        Assert.assertEquals(cycList17.toString(), cycList16.toString());
        Assert.assertEquals(cycList17.toString(), cycList16.toString());
        Assert.assertEquals(new CycList("(a)"), cycList17.first());
        Assert.assertEquals(new CycList("(b c)"), cycList17.second());
        Assert.assertEquals(new CycList("(((d)))"), cycList17.third());

        System.out.println("** testCycList OK **");
    }

    /**
     * Test <tt>CycListVisitor</tt> object behavior.
     */
    public void testCycListVisitor() {
        System.out.println("** testCycListVisitor **");
        CycList cycList1 = new CycList("()");
        Enumeration e1 = cycList1.cycListVisitor();
        Assert.assertTrue(! e1.hasMoreElements());

        CycList cycList2 = new CycList("(1 \"a\" :foo #$Brazil)");
        Enumeration e2 = cycList2.cycListVisitor();
        Assert.assertTrue(e2.hasMoreElements());
        Long long1 = new Long(1);
        Object nextObject = e2.nextElement();
        Assert.assertTrue(nextObject instanceof Long);
        Assert.assertTrue(((Long) nextObject).longValue() == long1.longValue());
        Assert.assertTrue(((Long) nextObject).longValue() == 1);
        Assert.assertTrue(e2.hasMoreElements());
        Assert.assertEquals("a", e2.nextElement());
        Assert.assertTrue(e2.hasMoreElements());
        Assert.assertEquals(new CycSymbol(":foo"), e2.nextElement());
        Assert.assertTrue(e2.hasMoreElements());
        Assert.assertEquals(CycConstant.makeCycConstant("#$Brazil"), e2.nextElement());
        Assert.assertTrue(! e1.hasMoreElements());

        CycList cycList3 = new CycList("((()))");
        Enumeration e3 = cycList3.cycListVisitor();
        Assert.assertTrue(! e3.hasMoreElements());

        CycList cycList4 = new CycList("(()())");
        Enumeration e4 = cycList4.cycListVisitor();
        Assert.assertTrue(! e4.hasMoreElements());

        CycList cycList5 = new CycList("(\"a\" (\"b\") (\"c\") \"d\" \"e\")");
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

        System.out.println("** testCycListVisitor OK **");
    }


}