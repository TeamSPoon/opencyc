package org.opencyc.templateparser;

import java.io.*;
import java.util.*;
import ViolinStrings.*;
import org.opencyc.cycobject.*;
import org.opencyc.conversation.*;
import org.opencyc.util.*;

/**
 * Parses chat text, matching the best of a set of templates and returning an
 * event plus arguments.
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
public class TemplateParser {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * the templates
     */
    ArrayList templates = new ArrayList();

    /**
     * Constructs a new TemplateParser object.
     */
    public TemplateParser() {
        Log.makeLog();
    }

    /**
    * Initializes this object.
    */
    public void initialize () {
        templates.addAll(TemplateFactory.getTemplates());
        Collections.sort(templates);
    }

    /**
     * Parses the given text string using the first matching template.
     *
     * @param inputText the text string to be parsed
     * @return the ParseResults
     */
    public ParseResults parse (String inputText) {
        Object [] answer = this.parseIntoWords(inputText);
        ArrayList inputWords = (ArrayList) answer[0];
        String terminalPunctuation = (String) answer[1];
        for (int i = 0; i < templates.size(); i++) {
            Template template = (Template) templates.get(i);
            CycList templateElements = template.getTemplateElements();
            ParseResults parseResults = new ParseResults(inputText);
            parse (inputWords, templateElements, parseResults);
            if (parseResults.isCompleteParse) {
                parseResults.setTemplate(template);
                parseResults.setPerformative(template.getPerformative());
                return parseResults;
            }
        }
        ParseResults parseResults = new ParseResults(inputText);
        parseResults.setPerformative(new Performative("not-understand"));
        return parseResults;
    }

    /**
     * Parses the partial input words, placing the results into
     * parseResults, and returning an array of two objects: the first
     * is the new partialInputWords and the second is the new
     * partialTemplateElements.  Recurses to handle variables
     * in the template.
     *
     * @param partialInputWords the partial input text string
     * @param partialTemplateElements the partial template expression
     * @param parseResults the parseResults object so far
     * @return an array of two objects: the first
     * is the new partialInputWords and the second is the new
     * partialTemplateElements
     */
    protected Object [] parse (ArrayList partialInputWords,
                               CycList partialTemplateElements,
                               ParseResults parseResults) {
        Object [] answer = {new ArrayList(), new CycList()};
        if (canParseChunkWithoutCapturing(partialInputWords, partialTemplateElements)) {
            parseResults.isCompleteParse = true;
            return answer;
        }

        Object [] templateWordsAnswer = getNextTemplateWords(partialTemplateElements);
        CycVariable templateVariable = (CycVariable) templateWordsAnswer[0];
        ArrayList templateWords = (ArrayList) templateWordsAnswer[1];
        CycList newPartialTemplateElements = (CycList) templateWordsAnswer[2];

        Object [] parseChunkAnswer =
            parseChunkWithCapturing(partialInputWords, templateWords);
        boolean isCompleteChunkParse = ((Boolean) parseChunkAnswer[0]).booleanValue();
        if (! isCompleteChunkParse)
            return answer;
        ArrayList capturedWords = (ArrayList) parseChunkAnswer[1];
        if (templateVariable != null)
            parseResults.addBinding(templateVariable, capturedWords);
        ArrayList newPartialInputWords = (ArrayList) parseChunkAnswer[2];
        return parse(newPartialInputWords, newPartialTemplateElements, parseResults);
    }

    /**
     * Parses the input words and punctuation from given input text.  Returns an
     * array of two objects; the first object is the list of input words and the
     * second object is the terminal punctuation character, or the empty string if
     * no terminal punctuation
     *
     * @param inputText the input text
     * @return an array of two objects; the first object is the list of input words
     * and the second object is the terminal punctuation character, or the empty
     * string if no terminal punctuation
     */
    protected Object [] parseIntoWords (String inputText) {
        Object [] answer = {null, null};
        ArrayList inputWords = new ArrayList();
        String terminalPunctuation = "";
        StringTokenizer stringTokenizer = new StringTokenizer(inputText);
        if (stringTokenizer.hasMoreElements())
            while (true) {
                String word = stringTokenizer.nextToken();
                if (! stringTokenizer.hasMoreTokens()) {
                    String lastCharacter = word.substring(word.length() - 1);
                    if (Strings.isLetterOrDigit(lastCharacter))
                        inputWords.add(new String(word.getBytes()));
                    else {
                        inputWords.add(new String((word.substring(0, word.length() - 1).getBytes())));
                        terminalPunctuation = lastCharacter;
                    }
                    break;
                }
                inputWords.add(word);
            }
        answer[0] = inputWords;
        answer[1] = terminalPunctuation;
        return answer;
    }

    /**
     * Returns an array of three objects given the partialTemplateElements.  The
     * first object returned is the variable preceding the group of template
     * words (or null if there is none), the second object returned is
     * the group of template words (up to the end of the template elements or
     * the next variable, and the third is the new partialTemplateElements.
     *
     * @param partialTemplateElements the partial template elements
     * @return an array of three objects given the partialTemplateElements.  The
     * first object returned is the variable preceding the group of template
     * words (or null if there is none), the second object returned is
     * the group of template words (up to the end of the template elements or
     * the next variable), and the third is the new partialTemplateElements
     */
    protected Object [] getNextTemplateWords (CycList partialTemplateElements) {
        CycVariable cycVariable = null;
        ArrayList templateWords = new ArrayList();
        CycList newPartialTemplateElements = new CycList();
        Object [] answer = {cycVariable, templateWords, newPartialTemplateElements};
        if (partialTemplateElements.size() == 0)
            return answer;
        Object element = partialTemplateElements.get(0);
        int index = 0;
        if (element instanceof CycVariable)
            answer[index] = element;
        else
            templateWords.add(element);
        for (index = 1; index < partialTemplateElements.size(); index++) {
            element = partialTemplateElements.get(index);
            if (element instanceof CycVariable)
                break;
            templateWords.add(element);
        }
        newPartialTemplateElements.addAll(partialTemplateElements.subList(index,
                                                                          partialTemplateElements.size()));
        return answer;
    }

    /**
     * Parses the partial input words, capturing any words before matching
     * the templateWords.  Returns an array of three objects: the first is a
     * Boolean which is true iff the partial input words match the
     * given template words, the second is the list of words captured from
     * the partial input words before matching the template words,
     * the third object returned is the remaining partialInputWords after
     * the matching is performed.
     *
     * @param partialInputWords the partial input text words
     * @param templateWords the template words (up to the end of the template,
     * or to the next variable in the template)
     * @return an array of three objects: the first is a
     * Boolean which is true iff the partial input words match the
     * given template words, the second is the list of words captured from
     * the partial input words before matching the template words,
     * the third object returned is the remaining partialInputWords after
     * the matching is performed
     */
    protected Object [] parseChunkWithCapturing (ArrayList partialInputWords,
                                                 ArrayList templateWords) {
        ArrayList capturedWords = new ArrayList();
        ArrayList remainingInputWords = new ArrayList();
        Object [] answer = {Boolean.FALSE,
                            capturedWords,
                            remainingInputWords};
        if (templateWords.size() == 0) {
            answer[0] = Boolean.TRUE;
            answer[1] = partialInputWords;
            return answer;
        }
        if (templateWords.size() > partialInputWords.size())
            return answer;
        int beginIndex = 0;
        while (true) {
            if (beginIndex > (partialInputWords.size() - templateWords.size()))
                return answer;
            else {
                ArrayList chunkWords = new ArrayList(partialInputWords.subList(beginIndex,
                                                                               beginIndex + templateWords.size()));
                if (chunkWords.equals(templateWords)) {
                    answer[0] = Boolean.TRUE;
                    remainingInputWords =
                        new ArrayList(partialInputWords.subList(beginIndex + templateWords.size(),
                                                                partialInputWords.size()));
                    answer[2] = remainingInputWords;
                    return answer;
                }
            }
            capturedWords.addAll(partialInputWords.subList(beginIndex, ++beginIndex));
        }
    }

    /**
     * Returns true iff the given partial input words be parsed without
     * capturing any variable text, by the given template words.
     *
     * @param partialInputWords the partial input words
     * @param templateWords the template words (up to the end of the
     * template elements or the next variable)
     * @return true iff the given partial input words can be parsed, without
     * capturing any variable text, by the given template words
     */
    protected boolean canParseChunkWithoutCapturing (ArrayList partialInputWords,
                                                     ArrayList templateWords) {
        return templateWords.equals(partialInputWords);
    }

    /**
     * Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

}