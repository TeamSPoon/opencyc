package org.opencyc.templateparser;

import junit.framework.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.conversation.*;
import org.opencyc.cycobject.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.templateparser</tt> package<p>
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
        testSuite.addTest(new UnitTest("testTemplate"));
        testSuite.addTest(new UnitTest("testParseResults"));
        testSuite.addTest(new UnitTest("testTemplateParser"));
        return testSuite;
    }

    /**
     * Main method in case tracing is prefered over running JUnit GUI.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests the Template object.
     */
    public void testTemplate () {
        System.out.println("\n**** testTemplate ****");

        CycList templateElements = new CycList();
        templateElements.add("quit");
        Performative quitPerformative = new Performative("quit");
        Template quitTemplate = new Template(null, templateElements, quitPerformative);
        Assert.assertNotNull(quitTemplate);
        Assert.assertNotNull(quitTemplate.getPerformative());
        Assert.assertEquals(quitPerformative, quitTemplate.getPerformative());
        Assert.assertNotNull(quitTemplate.getTemplateElements());
        Assert.assertEquals(templateElements, quitTemplate.getTemplateElements());
        Assert.assertNull(quitTemplate.getMt());

        CycList templateElements2 = new CycList();
        templateElements2.add("compare");
        templateElements2.add(CycObjectFactory.makeCycVariable("?term1"));
        templateElements2.add("with");
        templateElements2.add(CycObjectFactory.makeCycVariable("?term2"));
        Performative comparePerformative = new Performative("compare");
        Template compareTemplate = new Template(null, templateElements2, comparePerformative);
        Assert.assertNotNull(compareTemplate);
        Assert.assertEquals(comparePerformative, compareTemplate.getPerformative());
        Assert.assertEquals(templateElements2, compareTemplate.getTemplateElements());
        Template compareTemplate2 = new Template(null, templateElements2, comparePerformative);

        Assert.assertEquals(compareTemplate, compareTemplate2);
        Assert.assertEquals(templateElements2.toString(), compareTemplate.toString());

        ArrayList templates = new ArrayList();
        templates.add(compareTemplate);
        templates.add(quitTemplate);
        Collections.sort(templates);
        Assert.assertEquals(2, templates.size());
        Assert.assertEquals(compareTemplate, templates.get(0));
        Assert.assertEquals(quitTemplate, templates.get(1));

        System.out.println("**** testTemplate OK ****");
    }

    /**
     * Tests the ParseResults object.
     */
    public void testParseResults () {
        System.out.println("\n**** testParseResults ****");

        CycList templateElements = new CycList();
        templateElements.add("compare");
        CycVariable term1 = CycObjectFactory.makeCycVariable("?term1");
        templateElements.add(term1);
        templateElements.add("with");
        CycVariable term2 = CycObjectFactory.makeCycVariable("?term2");
        templateElements.add(term2);

        Performative comparePerformative =
            new Performative("compare");
        Template compareTemplate = new Template(null, templateElements, comparePerformative);
        String inputText = "compare the country of Iraq with the country of Iran";
        ParseResults parseResults = new ParseResults(inputText);
        Assert.assertNotNull(parseResults);
        Assert.assertEquals(inputText, parseResults.getInputText());
        Assert.assertEquals("", parseResults.getTerminalPunctuation());
        parseResults.setTerminalPunctuation(".");
        Assert.assertEquals(".", parseResults.getTerminalPunctuation());
        parseResults.setTemplate(compareTemplate);
        Assert.assertEquals(compareTemplate, parseResults.getTemplate());
        Assert.assertNull(parseResults.getTextBinding(term1));
        Assert.assertTrue(parseResults.getBindings() instanceof ArrayList);
        Assert.assertEquals(0, parseResults.getBindings().size());

        ArrayList textBinding = new ArrayList();
        textBinding.add("the");
        textBinding.add("country");
        textBinding.add("of");
        textBinding.add("Iraq");
        parseResults.addBinding(term1, textBinding);
        Assert.assertEquals(textBinding, parseResults.getTextBinding(term1));
        Assert.assertEquals(1, parseResults.getBindings().size());
        textBinding = new ArrayList();
        textBinding.add("the");
        textBinding.add("country");
        textBinding.add("of");
        textBinding.add("Iran");
        parseResults.addBinding(term2, textBinding);
        Assert.assertEquals(textBinding, parseResults.getTextBinding(term2));
        Assert.assertEquals(2, parseResults.getBindings().size());
        Assert.assertEquals("[\"compare the country of Iraq with the country of Iran\" " +
                            "?term1-->\"the country of Iraq\" ?term2-->\"the country of Iran\"]",
                            parseResults.toString());
        System.out.println(parseResults);
        Assert.assertTrue(! parseResults.isCompleteParse);
        System.out.println("**** testParseResults OK ****");
    }

    /**
     * Tests the TemplateParser object.
     */
    public void testTemplateParser () {
        System.out.println("\n**** testTemplateParser ****");

        TemplateParser templateParser = new TemplateParser();
        Assert.assertNotNull(templateParser);
        CycList templateElements = new CycList();
        templateElements.add("compare");
        templateElements.add(CycObjectFactory.makeCycVariable("?term1"));
        templateElements.add("with");
        templateElements.add(CycObjectFactory.makeCycVariable("?term2"));
        Performative comparePerformative =
            new Performative("compare");
        Template compareTemplate = new Template(null, templateElements, comparePerformative);
        String inputText = "compare the country of Iraq with the country of Iran";
        ParseResults parseResults = templateParser.parse(inputText);
        Assert.assertNotNull(parseResults);
        Assert.assertEquals(inputText, parseResults.getInputText());
        Assert.assertEquals("", parseResults.getTerminalPunctuation());

        Object [] answer = templateParser.parseIntoWords("done");
        Assert.assertTrue(answer[0] instanceof ArrayList);
        Assert.assertEquals(1, ((ArrayList) answer[0]).size());
        Assert.assertEquals("done", ((ArrayList) answer[0]).get(0));
        Assert.assertEquals("", answer[1]);

        answer = templateParser.parseIntoWords("done.");
        Assert.assertTrue(answer[0] instanceof ArrayList);
        Assert.assertEquals(1, ((ArrayList) answer[0]).size());
        Assert.assertEquals("done", ((ArrayList) answer[0]).get(0));
        Assert.assertEquals(".", answer[1]);

        answer = templateParser.parseIntoWords("done?");
        Assert.assertTrue(answer[0] instanceof ArrayList);
        Assert.assertEquals(1, ((ArrayList) answer[0]).size());
        Assert.assertEquals("done", ((ArrayList) answer[0]).get(0));
        Assert.assertEquals("?", answer[1]);

        answer = templateParser.parseIntoWords("first second third.");
        Assert.assertTrue(answer[0] instanceof ArrayList);
        Assert.assertEquals(3, ((ArrayList) answer[0]).size());
        Assert.assertEquals("first", ((ArrayList) answer[0]).get(0));
        Assert.assertEquals("second", ((ArrayList) answer[0]).get(1));
        Assert.assertEquals("third", ((ArrayList) answer[0]).get(2));
        Assert.assertEquals(".", answer[1]);

        ArrayList partialInputWords = new ArrayList();
        partialInputWords.add("quit");
        ArrayList templateWords = new ArrayList();
        templateWords.add("quit");

        Assert.assertTrue(templateParser.canParseChunkWithoutCapturing(partialInputWords,
                                                                       templateWords));
        templateWords = new ArrayList();
        templateWords.add("done");
        Assert.assertTrue(! templateParser.canParseChunkWithoutCapturing(partialInputWords,
                                                                         templateWords));

        partialInputWords = new ArrayList();
        partialInputWords.add("first");
        partialInputWords.add("second");
        partialInputWords.add("third");
        templateElements = new CycList();
        templateElements.add("second");
        templateElements.add("third");
        answer = templateParser.parseChunkWithCapturing(partialInputWords,
                                                        templateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertEquals(Boolean.TRUE, answer[0]);
        Assert.assertTrue(answer[1] instanceof ArrayList);
        Assert.assertEquals(1, ((ArrayList) answer[1]).size());
        Assert.assertEquals("first", ((ArrayList) answer[1]).get(0));
        Assert.assertTrue(answer[2] instanceof ArrayList);
        Assert.assertEquals(0, ((ArrayList) answer[2]).size());

        partialInputWords = new ArrayList();
        partialInputWords.add("first");
        partialInputWords.add("second");
        partialInputWords.add("third");
        partialInputWords.add("fourth");
        templateElements = new CycList();
        templateElements.add("second");
        templateElements.add("third");
        answer = templateParser.parseChunkWithCapturing(partialInputWords,
                                                        templateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertEquals(Boolean.TRUE, answer[0]);
        Assert.assertTrue(answer[1] instanceof ArrayList);
        Assert.assertEquals(1, ((ArrayList) answer[1]).size());
        Assert.assertEquals("first", ((ArrayList) answer[1]).get(0));
        Assert.assertEquals(1, ((ArrayList) answer[2]).size());
        Assert.assertEquals("fourth", ((ArrayList) answer[2]).get(0));

        ArrayList inputWords = new ArrayList();
        inputWords.add("compare");
        inputWords.add("the");
        inputWords.add("country");
        inputWords.add("of");
        inputWords.add("Iraq");
        inputWords.add("with");
        inputWords.add("the");
        inputWords.add("country");
        inputWords.add("of");
        inputWords.add("Iran");
        templateWords = new ArrayList();
        templateWords.add("compare");
        answer = templateParser.parseChunkWithCapturing(inputWords,
                                                        templateWords);
        Assert.assertEquals(3, answer.length);
        Assert.assertEquals(Boolean.TRUE, answer[0]);
        Assert.assertTrue(answer[1] instanceof ArrayList);
        Assert.assertEquals(0, ((ArrayList) answer[1]).size());
        Assert.assertEquals(9, ((ArrayList) answer[2]).size());
        ArrayList remainingInputWords = new ArrayList();
        remainingInputWords.add("the");
        remainingInputWords.add("country");
        remainingInputWords.add("of");
        remainingInputWords.add("Iraq");
        remainingInputWords.add("with");
        remainingInputWords.add("the");
        remainingInputWords.add("country");
        remainingInputWords.add("of");
        remainingInputWords.add("Iran");
        Assert.assertEquals(remainingInputWords, answer[2]);



        CycList partialTemplateElements = new CycList();
        answer = templateParser.getNextTemplateWords(partialTemplateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertNull(answer[0]);
        Assert.assertEquals(new ArrayList(), answer[1]);
        Assert.assertEquals(new CycList(), answer[2]);

        partialTemplateElements = new CycList();
        partialTemplateElements.add("quit");
        answer = templateParser.getNextTemplateWords(partialTemplateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertNull(answer[0]);
        templateWords = new CycList();
        templateWords.add("quit");
        Assert.assertEquals(templateWords, answer[1]);
        Assert.assertEquals(new CycList(), answer[2]);

        partialTemplateElements = new CycList();
        partialTemplateElements.add("compare");
        CycVariable term1 = CycObjectFactory.makeCycVariable("?term1");
        partialTemplateElements.add(term1);
        partialTemplateElements.add("to");
        CycVariable term2 = CycObjectFactory.makeCycVariable("?term2");
        partialTemplateElements.add(term2);
        answer = templateParser.getNextTemplateWords(partialTemplateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertNull(answer[0]);
        templateWords = new CycList();
        templateWords.add("compare");
        Assert.assertEquals(templateWords, answer[1]);
        CycList newPartialTemplateElements = new CycList();
        newPartialTemplateElements.add(term1);
        newPartialTemplateElements.add("to");
        newPartialTemplateElements.add(term2);
        Assert.assertEquals(newPartialTemplateElements, answer[2]);

        answer = templateParser.getNextTemplateWords(newPartialTemplateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertEquals(term1, answer[0]);
        templateWords = new CycList();
        templateWords.add("to");
        Assert.assertEquals(templateWords, answer[1]);
        newPartialTemplateElements = new CycList();
        newPartialTemplateElements.add(term2);
        Assert.assertEquals(newPartialTemplateElements, answer[2]);

        answer = templateParser.getNextTemplateWords(newPartialTemplateElements);
        Assert.assertEquals(3, answer.length);
        Assert.assertEquals(term2, answer[0]);
        Assert.assertEquals(new CycList(), answer[1]);
        Assert.assertEquals(new CycList(), answer[2]);

        // parse

        inputText = "quit";
        inputWords = new ArrayList();
        inputWords.add("quit");
        templateElements = new CycList();
        templateElements.add("quit");
        parseResults = new ParseResults(inputText);
        Assert.assertTrue(! parseResults.isCompleteParse);
        answer = templateParser.parse(inputWords, templateElements, parseResults);
        partialInputWords = (ArrayList) answer[0];
        Assert.assertEquals(0, partialInputWords.size());
        partialTemplateElements = (CycList) answer[1];
        Assert.assertEquals(0, partialTemplateElements.size());
        Assert.assertTrue(parseResults.isCompleteParse);
        Assert.assertEquals(0, parseResults.getBindings().size());

        inputText = "compare the country of Iraq with the country of Iran.";
        answer = templateParser.parseIntoWords(inputText);
        inputWords = (ArrayList) answer[0];
        String terminalPunctuation = (String) answer[1];

        CycList templateElements2 = new CycList();
        templateElements2.add("compare");
        templateElements2.add(CycObjectFactory.makeCycVariable("?term1"));
        templateElements2.add("with");
        templateElements2.add(CycObjectFactory.makeCycVariable("?term2"));
        comparePerformative =
            new Performative("compare");
        compareTemplate = new Template(null, templateElements2, comparePerformative);
        parseResults = new ParseResults(inputText);
        Assert.assertTrue(! parseResults.isCompleteParse);

        answer = templateParser.parse(inputWords,
                                      compareTemplate.getTemplateElements(),
                                      parseResults);

        partialInputWords = (ArrayList) answer[0];
        Assert.assertNotNull(partialInputWords);
        Assert.assertEquals(0, partialInputWords.size());
        partialTemplateElements = (CycList) answer[1];
        Assert.assertEquals(0, partialTemplateElements.size());
        Assert.assertTrue(parseResults.isCompleteParse);
        Assert.assertEquals(2, parseResults.getBindings().size());
        Assert.assertEquals("[\"compare the country of Iraq with the country of Iran.\"" +
                            " ?term1-->\"the country of Iraq\"" +
                            " ?term2-->\"the country of Iran\"]",
                            parseResults.toString());



        System.out.println("**** testTestTemplateParser OK ****");
    }

}