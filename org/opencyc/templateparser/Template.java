package org.opencyc.templateparser;

import java.util.*;
import java.io.*;
import org.opencyc.conversation.*;
import org.opencyc.cycobject.*;

/**
 * Contains a template for parsing a particular utterance, phrase or sentence
 * into an performative and arguments.<p>
 *
 * The user creates a template by telling the following:
 * <blockquote>
 *   In the context of "everything" the statement
 *   "what is the capitol of ?country"
 *   means (query (#$capitolCityOfCountry ?city ?country)).
 * </blockquote>
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
public class Template implements Comparable {

    /**
     * The microtheory in which this template applies, or null if the
     * execution of the performative does not require Cyc.
     */
    protected CycFort mt;

    /**
     * the elements of this template
     */
    protected CycList templateElements = new CycList();

    /**
     * the performative associated with this template
     */
    protected Performative performative;

    /**
     * Constructs a new Template object given the template elements
     * and performative.
     *
     * @param mt the microtheory in which this template applies, or
     * null if the execution of the performative does not require Cyc
     * @param templateElements the template elements
     * @param performative the performative
     */
    protected Template(CycFort mt,
                       CycList templateElements,
                       Performative performative) {
        this.mt = mt;
        this.templateElements = templateElements;
        this.performative = performative;
    }

    /**
     * Returns the microtheory
     *
     * @return the microtheory in which this template applies
     */
    public CycFort getMt () {
        return mt;
    }

    /**
     * Returns the template elements
     *
     * @return the template elements
     */
    public CycList getTemplateElements () {
        return templateElements;
    }

    /**
     * Returns the performative.
     *
     * @return the performative associated with this template
     */
    public Performative getPerformative() {
        return performative;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     *
     * @param object the reference object with which to compare.
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
     public int compareTo (Object object) {
        if (! (object instanceof Template))
            throw new ClassCastException("Must be a Template object");
        return templateElements.toString().compareTo(((Template) object).templateElements.toString());
     }

    /**
     * Returns <tt>true</tt> iff some object equals this object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Template))
            return false;
        return templateElements.equals(((Template) object).templateElements);
    }

    /**
     * Returns the string representation of the <tt>Template</tt>
     *
     * @return the representation of the <tt>Template</tt> as a <tt>String</tt>
     */
    public String toString() {
        return templateElements.toString();
    }

}

