package org.opencyc.cycobject;

import java.io.Serializable;
import java.io.*;
import org.opencyc.xml.*;
import org.opencyc.api.*;

/**
 * Provides the behavior and attributes of an OpenCyc Constant.
 *
 * @version $Id$
 * @author Stefano Bertolo
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
public class CycConstant extends CycFort implements Comparable, ELMT {

    /**
     * Field for storing the name of the XML tag for CycConstant objects
     */
    public static final String constantXMLTag = "constant";

    /**
     * Field for storing the name of the XML tag for the name of CycConstant objects
     */
    public static final String nameXMLTag = "name";

    /**
     * The default indentation for printing CycConstant objects to XML
     */
    public static int indentLength = 2;

    /**
     * The GUID (Globally Unique IDentifier) of the <tt>CycConstant<tt> object.
     * A string such as "c10af8ae-9c29-11b1-9dad-c379636f7270"
     */
    public Guid guid;

    /**
     * The name of the <tt>CycConstant<tt> object. A string such as "HandGrenade"
     */
    public String name;

    /**
     * Constructs a new incomplete <tt>CycConstant</tt> object.
     */
    public CycConstant () {
    }

    /**
     * Constructs a new <tt>CycConstant</tt> object from name, guid and id.
     *
     * @param name the constant name
     * @param guid the GUID that uniquely identifies the constant everywhere
     * @param id the id that uniquely identifies the constant on a given OpenCyc server
     */
    public CycConstant (String name, Guid guid, Integer id) {
        if ((name != null) && (name.startsWith("#$")))
            this.name = name.substring(2);
        else
            this.name = name;
        this.guid = guid;
        setId(id);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        try {
            if (super.getId() == null) {
                if (name == null)
                    throw new RuntimeException("Invalid CycConstant - no name to obtain id");
                    super.setId(CycAccess.current().getConstantId(name));
            }
            return super.getId();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (CycApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
        try {
            if (name == null) {
                if (super.getId() == null)
                    throw new RuntimeException("Invalid CycConstant - no id to obtain name");
                    name = CycAccess.current().getConstantName(super.getId());
            }
            return name;
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (CycApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sets the name
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the guid
     *
     * @return the guid
     */
    public Guid getGuid() {
        try {
            if (guid == null) {
                if (getId() == null)
                    throw new RuntimeException("Invalid CycConstant - no id to obtain guid");
                guid = CycAccess.current().getConstantGuid(getId());
            }
            return guid;
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (CycApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sets the guid
     *
     * @param guid the guid
     */
    public void setGuid(Guid guid) {
        this.guid = guid;
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
     * Prints the XML representation of the CycConstant to an <code>XMLWriter</code>
     *
     * @param xmlWriter an <tt>XMLWriter</tt>
     * @param indent an int that specifies by how many spaces to indent
     * @param relative a boolean; if true indentation is relative, otherwise absolute
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(constantXMLTag, indent, relative, true);
        int subIndent = indentLength;
        if (guid != null) {
            guid.toXML(xmlWriter, subIndent, true);
            subIndent = 0;
        }
        if (name != null) {
            xmlWriter.printXMLStartTag(nameXMLTag, subIndent, true, false);
            xmlWriter.print(TextUtil.doEntityReference(this.getName()));
            xmlWriter.printXMLEndTag(nameXMLTag);
            if (subIndent == indentLength)
                subIndent = 0;
        }
        if (super.getId() != null) {
            xmlWriter.printXMLStartTag(idXMLTag, subIndent, true, false);
            xmlWriter.print(this.getId().toString());
            xmlWriter.printXMLEndTag(idXMLTag);
        }
        xmlWriter.printXMLEndTag(constantXMLTag, -indentLength, true);
    }

    /**
     * Provides the hash code appropriate for the <tt>CycConstant</tt>.
     *
     * @return the hash code for the <tt>CycConstant</tt>
     */
    public int hashCode() {
        if (super.getId() != null)
            return this.getId().hashCode();
        else
            return name.hashCode();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>CycConstant</tt>. The equality check uses whatever
     * instance attributes are locally available from both CycConstant objects.  Completion of missing
     * attributes is performed if required for the comparison of like attributes.
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycConstant))
            return false;
        Integer thisId = super.getId();
        Integer thatId = ((CycFort) object).getId();
        if ((thisId != null) && (thatId != null))
            return thisId.equals(thatId);
        String thatName = ((CycConstant) object).name;
        if ((name != null) && (thatName != null))
            return name.equals(thatName);
        if (thisId != null)
            return thisId.equals(((CycConstant) object).getId());
        if (name != null)
            return name.equals(((CycConstant) object).getName());
        throw new RuntimeException("Invalid constant for comparision - missing both id and name");
    }

    /**
     * Returns a String representation of the <tt>CycConstant</tt>.
     */
    public String toString() {
        return getName();
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
        StringBuffer result = new StringBuffer("[CycConstant ");
        if (super.getId() != null)
            result.append(" id: " + super.getId());
        if (guid != null)
            result.append(" guid: " + guid);
        result.append("]");
        return result.toString();
    }

    /**
     * Returns the name of the <tt>CycConstant</tt> with "#$" prefixed.
     *
     * @return the name of the <tt>CycConstant</tt> with "#$" prefixed.
     */
    public String cyclify() {
        return "#$" + getName();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return cyclify();
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
     * Makes a valid constant name from the candidate name by substituting
     * an underline character for the invalid characters.
     */
    public static String makeValidConstantName(String candidateName) {
        String answer = candidateName;
        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '?')
                continue;
            StringBuffer answerBuf = new StringBuffer(answer);
            answerBuf.setCharAt(i, '_');
            answer = answerBuf.toString();
        }
        return answer;
    }

}
