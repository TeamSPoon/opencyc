package org.opencyc.xml;

import java.util.*;
import junit.framework.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

/**
 * Provides a suite of JUnit test cases for the <tt>org.opencyc.xml</tt> package.<p>
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
     * Main method in case tracing is prefered over running JUnit GUI.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Returns the test suite.
     *
     * @return the test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();
        testSuite = new TestSuite();
        //testSuite.addTest(new UnitTest("testExportDaml1"));
        //testSuite.addTest(new UnitTest("testExportDaml2"));
        testSuite.addTest(new UnitTest("testTextUtil"));
        return testSuite;
    }

    /**
     * Constructs a new UnitTest object.
     * @param name the test case name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Tests the ExportDaml class.
     */
    public void xtestExportDaml1() {
        System.out.println("\n*** testExportDaml1 ***");
        try {
            ExportDaml exportDaml = new ExportDaml(new CycAccess());
            exportDaml.verbosity = 3;
            // Export #$PublicConstant terms to DAML.
            exportDaml.cycKbSubsetCollectionGuid =
                CycObjectFactory.makeGuid("bd7abd90-9c29-11b1-9dad-c379636f7270");
            exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        System.out.println("*** testExportDaml1 OK ***");
    }

    /**
     * Tests the ExportDaml class.
     */
    public void testExportDaml2() {
        System.out.println("\n*** testExportDaml2 ***");
        try {
            ExportDaml exportDaml = new ExportDaml(new CycAccess());
            exportDaml.verbosity = 3;
            // Export #$IKBConstant terms to DAML.
            exportDaml.cycKbSubsetCollectionGuid =
                CycObjectFactory.makeGuid("bf90b3e2-9c29-11b1-9dad-c379636f7270");
            exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        System.out.println("*** testExportDaml2 OK ***");
    }

    /**
     * Tests the TextUtil class.
     */
    public void testTextUtil() {
        System.out.println("\n*** testTextUtil ***");
        String xmlText = "abc def";
        Assert.assertEquals(xmlText, TextUtil.doEntityReference(xmlText));
        Assert.assertEquals(xmlText, TextUtil.undoEntityReference(xmlText));
        xmlText = "abc&def<hij>klm";
        Assert.assertEquals("abc&amp;def&lt;hij&gt;klm", TextUtil.doEntityReference(xmlText));
        Assert.assertEquals(xmlText, TextUtil.undoEntityReference("abc&amp;def&lt;hij&gt;klm"));
        System.out.println("*** testTextUtil OK ***");
    }


}
