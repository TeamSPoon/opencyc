package org.opencyc.cycobject;

import java.io.IOException;
import java.io.Serializable;

import org.opencyc.xml.TextUtil;
import org.opencyc.xml.XMLStringWriter;
import org.opencyc.xml.XMLWriter;

/**
 * Provides the behavior and attributes of an OpenCyc variable, typically used
 * in rule and query expressions.
 *
 * @version $0.1$
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
public class CycVariable extends DefaultCycObject implements Comparable, Serializable {

    /**
     * The name of the XML tag for this object.
     */
    public static final String cycVariableXMLTag = "variable";

    /**
     * The variable represented as a <tt>String</tt>.
     */
    public String name;
    
    /**
     * Whether this variable is a meta variable.
     */
    public boolean isMetaVariable = false;

    /**
     * The ID of the <tt>CycVariable<tt> object which is an integer unique within an OpenCyc
     * KB but not necessarily unique globally.
     */
    public Integer hlVariableId;

    /**
     * Constructs a new empty <tt>CycVariable</tt> object.
     */
    public CycVariable() {
    }

    /**
     * Constructs a new <tt>CycVariable</tt> object.
     *
     * @param name the <tt>String</tt> name of the <tt>CycVariable</tt>.
     */
    public CycVariable(String name) {
        if (name.startsWith(":")) {
          this.isMetaVariable = true;
          this.name = name.substring(1);
        } else if (name.startsWith("?")) {
          this.name = name.substring(1);
        } else {
          this.name = name;
        }
    }
    
    /**
     * Returns whether this is a meta variable.
     *
     * @return whether this is a meta variable
     */
    public boolean isMetaVariable() {
      return isMetaVariable;
    }

    /**
     * Returns whether this is an HL variable.
     *
     * @return whether this is an HL variable
     */
    public boolean isHLVariable() {
      return hlVariableId != null;
    }

    /**
     * Returns the string representation of the <tt>CycVariable</tt>
     *
     * @return the representation of the <tt>CycVariable</tt> as a <tt>String</tt>
     */
    public String toString() {
        return cyclify();
    }
    
    public boolean isDontCareVariable() {
      return name.startsWith("?");
    }

    /**
     * Returns a string representation without causing additional api calls to determine
     * constant names.
     *
     * @return a string representation without causing additional api calls to determine
     * constant names
     */
    public String safeToString () {
        if (name != null)
            return name;
        StringBuffer result = new StringBuffer("[CycVariable ");
        if (hlVariableId != null)
            result.append(" id: " + hlVariableId);
        result.append("]");
        return result.toString();
    }

    /**
     * Returns the OpenCyc representation of the <tt>CycVariable</tt>
     *
     * @return the OpenCyc representation of the <tt>CycVariable</tt> as a
     * <tt>String</tt> prefixed by "?"
     */
    public String cyclify() {
      if (isMetaVariable) {
        return ":" + name;
      } else if (isHLVariable()) {
        return "?var" + hlVariableId.toString();
      } else {
        return "?" + name;
      }
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return "'" + cyclifyWithEscapeChars();
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
     * Returns <tt>true</tt> some object equals this <tt>CycVariable</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycVariable))
            return false;
        CycVariable var = (CycVariable)object;
        return (isHLVariable() == var.isHLVariable()) && 
          (isMetaVariable() == var.isMetaVariable()) &&
          var.name.equals(name);
    }

    /**
     * Provides the hash code appropriate for this object.
     *
     * @return the hash code appropriate for this object
     */
    public int hashCode() {
        return name.hashCode();
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
        if (! (object instanceof CycVariable))
            throw new ClassCastException("Must be a CycVariable object");
        return this.name.compareTo(((CycVariable) object).name);
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
     * Prints the XML representation of the CycVariable to an <code>XMLWriter</code>
     *
     * @param xmlWriter an <tt>XMLWriter</tt>
     * @param indent an int that specifies by how many spaces to indent
     * @param relative a boolean; if true indentation is relative, otherwise absolute
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(cycVariableXMLTag, indent, relative, false);
        xmlWriter.print(TextUtil.doEntityReference(name));
        xmlWriter.printXMLEndTag(cycVariableXMLTag);
    }

}
