package  org.opencyc.cycobject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.xml.XMLStringWriter;
import org.opencyc.xml.XMLWriter;


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
public class CycAssertion extends DefaultCycObject{
    /**
     * The name of the XML tag for this object.
     */
    public static final String cycAssertionXMLTag = "assertion";

    /**
     * The default indentation for printing objects to XML
     */
    public static int indentLength = 2;

    /** the assertion in HL form */
    private CycList hlFormula;

    /** the assertion mt */
    private CycObject mt;
    
    /**
     * Constructs an assertion object given its HL formula and assertion mt.
     *
     * @param hlFormula the assertion in HL form
     * @param mt the assertion mt
     */
    public CycAssertion (CycList hlFormula, CycObject mt) {
        //// Preconditions
        assert hlFormula != null : "hlFormula cannot be null";
        assert ! hlFormula.isEmpty() : "hlFormula cannot be empty";
        assert mt != null : "mt cannot be null";
        
        this.hlFormula = hlFormula;
        this.mt = mt;
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
        if (! this.mt.equals(that.mt))
          return false;
        else
          return this.hlFormula.equals(that.hlFormula);
    }

    /**
     * Returns a <tt>String</tt> representation of the <tt>CycAssertion</tt>.
     *
     * @return a <tt>String</tt> representation of the <tt>CycAssertion</tt>
     */
    public String toString () {
        return hlFormula.cyclify();
    }

    /**
     * Returns a string representation without causing additional api calls.
     *
     * @return a string representation without causing additional api calls
     */
    public String safeToString () {
        return hlFormula.safeToString();
    }

  /**
   * Returns a cyclified string representation of the CycL assertion.
   * A cyclified string representation with escape chars is one where
   * constants have been prefixed with #$ and Strings have had an escape
   * character inserted before each character that needs to be escaped in SubL.
   *
   * @return a cyclified <tt>String</tt> with escape characters.
   */ 
    public String cyclifyWithEscapeChars() {
      return hlFormula.cyclifyWithEscapeChars();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return "(find-assertion " + hlFormula.stringApiValue() + " " + mt.stringApiValue() + ")";
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
     * Returns the HL formula for this assertion.
     *
     * @return the HL formula for this assertion
     */
    public CycList getFormula () {
        return hlFormula;
    }

    /**
     * Returns the Ground Atomic Formula (gaf) for this assertion.
     *
     * @return the Ground Atomic Formula (gaf) for this assertion
     */
    public CycList getGaf () {
        //// Preconditions
        assert ! hlFormula.isEmpty() : "hlFormula cannot be empty";
        final Object negativeLiterals = ((CycList) hlFormula).first();
        assert negativeLiterals.equals(CycObjectFactory.nil) : ((CycList) hlFormula).cyclify() + " negativeLiterals must be nil";
        assert ((CycList) hlFormula).size() == 2 : ((CycList) hlFormula).cyclify() + " must be of the form (nil ( ... ))";
        
        final CycList positiveLiterals = (CycList) hlFormula.second();
        if (positiveLiterals.size() == 1)
            return (CycList) positiveLiterals.first();
        final CycList gaf = new CycList();
        gaf.add(CycAccess.current().and);
        gaf.addAll(positiveLiterals);
        
        //// Postconditions
        assert gaf != null : "gaf cannot be null";
        assert ! gaf.isEmpty() : "gaf cannot be empty";
        
        return gaf;
    }

    /**
     * Returns the microtheory for this assertion.
     *
     * @return the microtheory for this assertion
     */
    public CycObject getMt () {
        return mt;
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
        hlFormula.toXML(xmlWriter, indent, relative);
        mt.toXML(xmlWriter, indent, relative);
        xmlWriter.printXMLEndTag(cycAssertionXMLTag, -indentLength, true);
    }
    
   /**
   * Returns a list of all constants refered to by this CycObject.
   * For example, a CycConstant will return a List with itself as the
   * value, a nart will return a list of its functor and all the constants refered
   * to by its arguments, a CycList will do a deep search for all constants,
   * a symbol or variable will return the empty list.
   * @return a list of all constants refered to by this CycObject
   **/
  public List getReferencedConstants() {
    List result = null;
    if (getFormula() != null) {
      result = DefaultCycObject.getReferencedConstants(getFormula());
      if (getMt() != null) {
        result.addAll(getMt().getReferencedConstants());
      }
      return result;
    }
    if (getMt() != null) {
      result = DefaultCycObject.getReferencedConstants(getMt());
    }
    return (result == null) ? new ArrayList() : result;
  }
  
  public int compareTo(Object o){
    if(!(o instanceof CycAssertion)) return toString().compareTo(o.toString());
    CycAssertion cao=(CycAssertion)o;
    int ret= this.getMt().compareTo(cao.getMt());
    if(ret!=0) return ret;
    return this.getFormula().compareTo(cao.getFormula());
  }
}







