package org.opencyc.chat;

/**
 * Makes templates which are used by the TemplateParser to parser user input.<p>
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

import java.util.*;

public class TemplateFactory {

    /**
     * Caches template objects to keep from making them twice.
     * templateExpression --> Template
     */
    protected static HashMap templateCache = new HashMap();

    /**
     * DialogFsmEvent object factory
     */
    DialogFsmEventFactory dialogFsmEventFactory;

    /**
     * Constructs a new TemplateFactory object.
     */
    public TemplateFactory() {
        dialogFsmEventFactory = new DialogFsmEventFactory();
    }

    /**
    * Initializes this object.
    */
    public void initialize () {
    }

    /**
     * Returns the templates.
     *
     * @return the templates
     */
    public static Collection getTemplates () {
        return templateCache.values();
    }

    /**
     * Makes a new Template object given the template expression
     * and event.
     *
     * @param templateExpression the template expression
     * @param dialogFsmEvent the event
     */
    public Template makeTemplate(String templateExpression,
                                    DialogFsmEvent dialogFsmEvent) {
        Template template = (Template) templateCache.get(templateExpression);
        if (template != null)
            return template;
        template = new Template(templateExpression, dialogFsmEvent);
        templateCache.put(templateExpression, template);
        return template;
    }

    /**
     * Returns a Template object given the template expression.
     *
     * @param templateExpression the template expression
     */
    public Template getTemplate(String templateExpression) {
        return (Template) templateCache.get(templateExpression);
    }

    /**
     * Makes all templates.
     */
    public void makeAllTemplates() {
        makeDoneTemplate();
        makeMoreTemplate();
        makeQuitTemplate();
        makeTermQueryTemplate();
    }

    /**
     * Make a quit template
     */
    public Template makeQuitTemplate () {
        String templateExpression = "quit";
        Template quitTemplate = (Template) templateCache.get(templateExpression);
        if (quitTemplate != null)
            return quitTemplate;
        DialogFsmEvent quitEvent = dialogFsmEventFactory.makeDialogFsmEvent("quit");
        quitTemplate = new Template(templateExpression, quitEvent);
        templateCache.put(templateExpression, quitTemplate);
        return quitTemplate;
    }

    /**
     * Make a more template
     */
    public Template makeMoreTemplate () {
        String templateExpression = "more";
        Template moreTemplate = (Template) templateCache.get(templateExpression);
        if (moreTemplate != null)
            return moreTemplate;
        DialogFsmEvent moreEvent = dialogFsmEventFactory.makeDialogFsmEvent("more");
        moreTemplate = new Template(templateExpression, moreEvent);
        templateCache.put(templateExpression, moreTemplate);
        return moreTemplate;
    }

    /**
     * Make a done template
     */
    public Template makeDoneTemplate () {
        String templateExpression = "done";
        Template doneTemplate = (Template) templateCache.get(templateExpression);
        if (doneTemplate != null)
            return doneTemplate;
        DialogFsmEvent doneEvent = dialogFsmEventFactory.makeDialogFsmEvent("done");
        doneTemplate = new Template(templateExpression, doneEvent);
        templateCache.put(templateExpression, doneTemplate);
        return doneTemplate;
    }

    /**
     * Make a term query template
     */
    public Template makeTermQueryTemplate () {
        String templateExpression = "what do you know about ?TERM";
        Template termQueryTemplate = (Template) templateCache.get(templateExpression);
        if (termQueryTemplate != null)
            return termQueryTemplate;
        DialogFsmEvent termQueryEvent = dialogFsmEventFactory.makeDialogFsmEvent("term-query");
        termQueryTemplate = new Template(templateExpression, termQueryEvent);
        templateCache.put(templateExpression, termQueryTemplate);
        return termQueryTemplate;
    }
}