package org.opencyc.util;

import java.util.*;
import junit.framework.*;

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
     * Constructs a new UnitTest object.
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
        TestResult testResult = new TestResult();
        testSuite.run(testResult);
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
}
