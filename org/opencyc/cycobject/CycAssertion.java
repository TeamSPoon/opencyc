package  org.opencyc.cycobject;

import  java.io.IOException;
import  java.util.*;
import  org.opencyc.api.*;
import  org.opencyc.xml.*;


/**
 * Provides the behavior and attributes of OpenCyc assertions.<p>
 * <p>
 * Assertions are communicated over the binary API using their Id number (an int).
 * The associated formula, microtheory, truth-value, direction, and remaining attributes are
 * is fetched later.
 *
 * @version $Id$
 * @author Stephen L. Reed
 * @author Dan Lipofsky
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
public class CycAssertion extends CycObject {
    /**
     * The name of the XML tag for this object.
     */
    public static final String cycAssertionXMLTag = "assertion";

    /**
     * The default indentation for printing objects to XML
     */
    public static int indentLength = 2;

    /**
     * The name of the XML tag for id objects
     */
    public static final String idXMLTag = "id";

    /**
     * Assertion id assigned by the local KB server.  Not globally unique.
     */
    public Integer id;

    /**
     * The assertion in the form of a <tt>CycList</tt>.
     */
    private CycList formula;

    /**
     * Constructs an incomplete <tt>CycAssertion</tt> object given its local KB id.
     *
     * @param id the assertion id assigned by the local KB
     */
    public CycAssertion (Integer id) {
        this.id = id;
    }

    /**
     * Indicates whether the object is equal to this object.
     *
     * @return <tt>true</tt> if the object is equal to this object, otherwise
     * returns <tt>false</tt>
     */
    public boolean equals (Object object) {
        if (!(object instanceof CycAssertion))
            return  false;
        CycAssertion that  = (CycAssertion) object;
        return this.id.equals(that.id);
    }

    /**
     * Returns a <tt>String</tt> representation of the <tt>CycAssertion</tt>.
     *
     * @return a <tt>String</tt> representation of the <tt>CycAssertion</tt>
     */
    public String toString () {
        if (formula == null)
            return "assertion-with-id:" + id;
        else
            return formula.cyclify();
    }

    /**
     * Returns a string representation without causing additional api calls.
     *
     * @return a string representation without causing additional api calls
     */
    public String safeToString () {
        StringBuffer result = new StringBuffer("[CycAssertion ");
        if (id != null)
            result.append(" id: " + id);
        result.append("]");
        return result.toString();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return formula.cyclify();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>CycList</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>CycList</tt> api expression value
     */
    public Object cycListApiValue() {
        return this;
    }

    /**
     * Returns the formula for this assertion.
     *
     * @return the formula for this assertion
     */
    public CycList getFormula () {
        if (formula == null) {
            CycAssertion cycAssertion = null;
            try {
                cycAssertion = CycAccess.current().completeCycAssertion(this);
            }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            formula = cycAssertion.formula;
        }
        return formula;
    }

    /**
     * Sets the formula for this assertion.
     *
     * @param formula the formula for this assertion
     */
    public void setFormula (CycList formula) {
        this.formula = formula;
    }

    /**
     * Returns the id for this assertion.
     *
     * @return the id for this assertion
     */
    public Integer getId () {
        return id;
    }

    /**
     * Returns the XML representation of this object.
     *
     * @return the XML representation of this object
     */
    public String toXMLString () throws IOException {
        XMLStringWriter xmlStringWriter = new XMLStringWriter();
        toXML(xmlStringWriter, 0, false);
        return xmlStringWriter.toString();
    }

    /**
     * Prints the XML representation of the CycAssertion to an <code>XMLWriter</code>
     *
     * @param xmlWriter an <tt>XMLWriter</tt>
     * @param indent an int that specifies by how many spaces to indent
     * @param relative a boolean; if true indentation is relative, otherwise absolute
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(cycAssertionXMLTag, indent, relative, true);
        xmlWriter.printXMLStartTag(idXMLTag, 2, true, false);
        xmlWriter.print(id.toString());
        xmlWriter.printXMLEndTag(idXMLTag);
        xmlWriter.printXMLEndTag(cycAssertionXMLTag, -indentLength, true);
    }
}







