package org.opencyc.templateparser;

import java.util.*;
import org.opencyc.conversation.*;
import org.opencyc.cycobject.*;

/**
 * Contains the attributes and behavior of the results of a template parse.
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
public class ParseResults {

    /**
     * input text
     */
    protected String inputText;

    /**
     * input words
     */
    protected ArrayList inputWords;

    /**
     * template used for the parse
     */
    protected Template template;

    /**
     * Performative from the template if parsed OK, otherwise set to the
     * not-understood performative.
     */
    protected Performative performative;

    /**
     * Input text terminal punctuation, which defaults to none.
     */
    protected String terminalPunctuation = "";

    /**
     * Variables and their bound text as an list of string pairs.  Each
     * element of this list is a object array of two items, the first
     * being the CycVariable and the second being the bound text String.
     */
    protected ArrayList bindings = new ArrayList();

    /**
     * Indicates whether the complete input text was parsed.
     */
    public boolean isCompleteParse = false;

    /**
     * Constructs a new ParseResults object given the text for parsing.
     *
     * @param inputText the input text for parsing
     */
    public ParseResults(String inputText) {
        this.inputText = inputText;
    }

    /**
     * Returns the input text
     */
    public String getInputText () {
        return inputText;
    }

    /**
     * Sets the input words.
     *
     * @param inputWords the input words
     */
    public void setInputWords (ArrayList inputWords) {
        this.inputWords = inputWords;
    }

    /**
     * Returns the input words.
     *
     * @return the input words
     */
    public ArrayList getInputWords () {
        return inputWords;
    }

    /**
     * Sets the terminalPunctuation.
     *
     * @param terminalPunctuation the last punctuation character
     * in the input text.
     */
    public void setTerminalPunctuation (String terminalPunctuation) {
        this.terminalPunctuation = terminalPunctuation;
    }

    /**
     * Returns the terminalPunctuation.
     *
     * @return the terminal punctuation character
     */
    public String getTerminalPunctuation () {
        return terminalPunctuation;
    }

    /**
     * Sets the template used for the parse.
     *
     * @param template the template used for the parse
     */
    public void setTemplate (Template template) {
        this.template = template;
    }

    /**
     * Returns the template used for the parse.
     *
     * @param the template used for the parse
     */
    public Template getTemplate () {
        return template;
    }

    /**
     * Sets the Performative to the given value.
     *
     * @param performative the performative
     */
    public void setPerformative (Performative performative) {
        this.performative = performative;
    }

    /**
     * Returns the Performative.
     *
     * @return the Performative
     */
    public Performative getPerformative () {
        return this.performative;
    }

    /**
     * Adds a template variable and its text binding to the list of bindings.
     *
     * @param templateVariable the template variable
     * @param textBinding the portion of the input text bound to the given
     * template variable
     */
    public void addBinding (CycVariable templateVariable, ArrayList textBinding) {
        for (int i = 0; i < bindings.size(); i++) {
            Object [] existingBinding = (Object []) bindings.get(i);
            if (existingBinding[0].equals(templateVariable)) {
                existingBinding[1] = textBinding;
                return;
            }
        }
        Object [] bindingPair = new Object [2];
        bindingPair[0] = templateVariable;
        bindingPair[1] = textBinding;
        bindings.add(bindingPair);
    }

    /**
     * Returns the list of bindings.
     *
     * @return the list of bindings
     */
    public ArrayList getBindings () {
        return bindings;
    }

    /**
     * Returns the text binding for the given template variable.
     *
     * @return the text binding for the given template variable, or
     * null if not found
     */
    public ArrayList getTextBinding (CycVariable templateVariable) {
        Object [] bindingPair = {null, null};
        for (int i = 0; i < bindings.size(); i++) {
            bindingPair =  (Object []) bindings.get(i);
            if (bindingPair[0].equals(templateVariable))
                return (ArrayList) bindingPair[1];
        }
        return null;
    }

    /**
     * Returns the string representation of the <tt>ParseResults</tt>
     *
     * @return the representation of the <tt>ParseResults</tt> as a <tt>String</tt>
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append("\"");
        stringBuffer.append(inputText);
        stringBuffer.append("\" ");
        for (int i = 0; i < bindings.size(); i++) {
            if (i > 0)
                stringBuffer.append(" ");
            Object [] binding = (Object []) bindings.get(i);
            stringBuffer.append(binding[0].toString());
            stringBuffer.append("-->\"");
            ArrayList bindingText = (ArrayList) binding[1];
            for (int j = 0; j < bindingText.size(); j++) {
                if (j > 0)
                    stringBuffer.append(" ");
                stringBuffer.append(bindingText.get(j));
            }
            stringBuffer.append("\"");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}