package org.opencyc.templateparser;

import java.util.*;
import org.opencyc.api.*;
import org.opencyc.conversation.*;
import org.opencyc.cycobject.*;

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
public class TemplateFactory {

    /**
     * Caches template objects to keep from making them twice.
     * templateExpression --> Template
     */
    protected static HashMap templateCache = new HashMap();

    /**
     * Constructs a new TemplateFactory object.
     */
    public TemplateFactory() {
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
     * Makes a new Template object given the template elements
     * and performative.
     *
     * @param mt the microtheory in which this template applies
     * @param templateElements the template elements
     * @param performative the performative
     */
    public Template makeTemplate(CycFort mt,
                                 CycList templateElements,
                                 Performative performative) {
        Template template = (Template) templateCache.get(templateElements);
        if (template != null)
            return template;
        template = new Template(mt, templateElements, performative);
        templateCache.put(templateElements, template);
        return template;
    }

    /**
     * Returns a Template object from the cache given the template elements.
     *
     * @param templateElements the template elements
     */
    public Template getTemplate(CycList templateElements) {
        return (Template) templateCache.get(templateElements);
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
        CycList templateExpression = new CycList();
        templateExpression.add("quit");
        Template quitTemplate = (Template) templateCache.get(templateExpression);
        if (quitTemplate != null)
            return quitTemplate;
        Performative quitPerformative =
            new Performative("quit");
        quitTemplate = new Template(null, templateExpression, quitPerformative);
        templateCache.put(templateExpression, quitTemplate);
        return quitTemplate;
    }

    /**
     * Make a more template
     */
    public Template makeMoreTemplate () {
        CycList templateExpression = new CycList();
        templateExpression.add("more");
        Template moreTemplate = (Template) templateCache.get(templateExpression);
        if (moreTemplate != null)
            return moreTemplate;
        Performative morePerformative = new Performative("more");
        moreTemplate = new Template(null, templateExpression, morePerformative);
        templateCache.put(templateExpression, moreTemplate);
        return moreTemplate;
    }

    /**
     * Make a done template
     */
    public Template makeDoneTemplate () {
        CycList templateExpression = new CycList();
        templateExpression.add("done");
        Template doneTemplate = (Template) templateCache.get(templateExpression);
        if (doneTemplate != null)
            return doneTemplate;
        Performative donePerformative = new Performative("done");
        doneTemplate = new Template(null, templateExpression, donePerformative);
        templateCache.put(templateExpression, doneTemplate);
        return doneTemplate;
    }

    /**
     * Make a term query template
     */
    public Template makeTermQueryTemplate () {
        CycList templateExpression = new CycList();
        templateExpression.add("what");
        templateExpression.add("do");
        templateExpression.add("you");
        templateExpression.add("know");
        templateExpression.add("about");
        templateExpression.add(CycObjectFactory.makeCycVariable("?term"));
        Template termQueryTemplate = (Template) templateCache.get(templateExpression);
        if (termQueryTemplate != null)
            return termQueryTemplate;
        Performative termQueryPerformative =
            new Performative("term-query");
        termQueryTemplate = new Template(null, templateExpression, termQueryPerformative);
        templateCache.put(templateExpression, termQueryTemplate);
        return termQueryTemplate;
    }
}