package org.opencyc.chat;

import java.util.ArrayList;
import java.util.Collections;

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
     * the templates
     */
    ArrayList templates;

    /**
     * Constructs a new TemplateParser object.
     */
    public TemplateParser() {
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
     * @param text the text string to be parsed
     * @return the found event or null.
     */
    public void parse (String text) {
        for (int i = 0; i < templates.size(); i++) {
            String templateExpression = ((Template) templates.get(i)).getTemplateExpression();

            // Divide up the template expression into exact match chunks with the
            // text capturing variables in between.
            // A match happens if the text contains all of the exact match chunks, in an
            // orderly non-overlapping manner.
        }
    }


}