package org.opencyc.uml.interpreter;

import java.util.*;
import java.io.*;
import junit.framework.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.parser.wrapper.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.uml.interpreter</tt> package<p>
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
     * Creates a <tt>UnitTest</tt> object with the given name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Returns the test suite.
     *
     * @return the test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();
        testSuite.addTest(new UnitTest("testExpressionEvaluation"));
        return testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests expression evaluation.
     */
    public void testExpressionEvaluation () {
        System.out.println("\n**** testExpressionEvaluation ****");
        // Create the interpreter. It will use the default JavaCC parser.
        TreeInterpreter interpreter = new TreeInterpreter(new JavaCCParserFactory());
        Integer integer1 = new Integer(1);
        interpreter.defineVariable("integer1", integer1);
        StringReader stringReader = new StringReader("integer1.intValue() + 1;");
        Object result = interpreter.interpret(stringReader, "statement");
        Assert.assertTrue(result instanceof Integer);
        Assert.assertEquals(new Integer(2), result);

        StringBuffer statements = new StringBuffer();
        statements.append("String testString;\n");
        statements.append("testString = \"abcdef\";\n");
        statements.append("testString.substring(3);\n");
        result = interpreter.interpret(new StringReader(statements.toString()), "statements");
        Assert.assertTrue(result instanceof String);
        Assert.assertEquals("def", result);

        statements = new StringBuffer();
        statements.append("String testString2;\n");
        statements.append("testString2 = testString + \"1234\";\n");
        statements.append("testString2.startsWith(\"abc\");\n");
        result = interpreter.interpret(new StringReader(statements.toString()), "statements");
        Assert.assertTrue(result instanceof Boolean);
        Assert.assertEquals(Boolean.TRUE, result);

        System.out.println("\n**** testExpressionEvaluation ****");
    }

}