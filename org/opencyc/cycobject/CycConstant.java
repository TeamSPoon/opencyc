package org.opencyc.cycobject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.xml.TextUtil;
import org.opencyc.xml.XMLStringWriter;
import org.opencyc.xml.XMLWriter;

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
public class CycConstant extends CycFort implements Comparable, Serializable {

    static final long serialVersionUID = -8728481441510819469L;
    
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
     * When true, indicates that the constant has been deleted and the guid and
     * id are not present.
     */
    protected boolean isFree = false;
    
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
     * Constructs a new <tt>CycConstant</tt> object from name, guid and id.
     *
     * @param name the constant name
     * @param guid the GUID that uniquely identifies the constant everywhere
     */
    public CycConstant (String name, Guid guid) {
        if ((name != null) && (name.startsWith("#$")))
            this.name = name.substring(2);
        else
            this.name = name;
        this.guid = guid;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        try {
            if (super.getId() == null) {
                if (name != null)
                    super.setId(CycAccess.current().getConstantId(name));
                else if (guid != null)
                    super.setId(CycAccess.current().getConstantId(guid));
                else
                    throw new RuntimeException("Invalid CycConstant - no name or guid to obtain id");
            }
            return super.getId();
        }
        catch (IOException e) {
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
        }
        catch (CycApiException e) {
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
        }
    }

    /**
     * Safely gets the name without an api call
     *
     * @return the name
     */
    public String safeGetName() {
        if (isFree)
            return ("FREE");
        else
            return name;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
        if (isFree)
            return ("FREE");
        try {
            if (name == null) {
                /*JOptionPane.showMessageDialog(null, 
                  "OpenCyc is doing something very naughty. \n" + 
                  "Please contact Steve Reed about about sneaky communications with Cyc.\n" + 
                  org.opencyc.util.StringUtils.getStringForException(new Exception()), "alert", 
                  JOptionPane.ERROR_MESSAGE);*/
                //new Exception().printStackTrace();
                if (super.getId() != null)
                    name = CycAccess.current().getConstantName(super.getId());
                else if (guid != null)
                    name = CycAccess.current().getConstantName(guid);
                else
                    throw new RuntimeException("Invalid CycConstant - no id to obtain name");
            }
            return name;
        }
        catch (IOException e) {
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
        }
        catch (CycApiException e) {
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
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
     * Gets the guid without an api access.
     *
     * @return the guid
     */
    public Guid safeGetGuid () {
        return guid;
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
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
        }
        catch (CycApiException e) {
            throw new RuntimeException(org.opencyc.util.StringUtils.
              getStringForException(e));
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
        return getGuid().hashCode();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>CycConstant</tt>. The equality check uses only the guid.
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycConstant))
            return false;
        Guid thisGuid = getGuid();
        Guid thatGuid = ((CycConstant) object).getGuid();
        return thisGuid.equals(thatGuid);
    }

    /**
     * Sets this constant as deleted
     */
    public void setFree() {
        isFree = true;
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
        if (isFree)
            return "FREE";
        else
            return "#$" + getName();
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
    
  /**
   * Returns a list of all constants refered to by this CycObject.
   * For example, a CycConstant will return a List with itself as the
   * value, a nart will return a list of its functor and all the constants refered
   * to by its arguments, a CycList will do a deep search for all constants,
   * a symbol or variable will return the empty list.
   * @return a list of all constants refered to by this CycObject
   **/
  public List getReferencedConstants() {
    List result = new ArrayList();
    result.add(this);
    return result;
  }

  //// serialization implementation
  private void writeObject(ObjectOutputStream stream) throws java.io.IOException {
    stream.defaultWriteObject();
    stream.writeUTF( guid.guidString);
    stream.writeUTF( name);
  }
   
  private void readObject(ObjectInputStream stream) throws java.io.IOException, 
  java.lang.ClassNotFoundException {
    stream.defaultReadObject();
    guid = new Guid( stream.readUTF());
    name = stream.readUTF();
  }
  
}
