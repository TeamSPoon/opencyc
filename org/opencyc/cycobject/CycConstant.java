package org.opencyc.cycobject;

import java.io.Serializable;
import org.apache.oro.util.*;
//import org.opencyc.xml.XMLPrintWriter;

/**
 * Provides the behavior and attributes of an OpenCyc Constant.
 *
 * @version $Id$
 * @author Stefano Bertolo
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 OpenCyc.org, license is open source GNU LGPL.
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
public class CycConstant extends CycFort implements Comparable {

    /**
     * These XML tag names are expected to be
     * identical to those emitted by Cyc's EL-XML serialization protocol
     * as implemented in the SubL module el-xml-serialization.lisp
     */
    public static String constant_xml_tag = "constant";
    public static String name_xml_tag = "name";
    public static String guid_xml_tag = "guid";

    /**
     * Least Recently Used Cache of CycConstants, so that a reference to an existing <tt>CycConstant</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cache = new CacheLRU(500);

    public static int indent_length = 2;

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
     * Constructs a new <tt>CycConstant</tt> object, given a guidString and constant
     * name.
     *
     * @param guidString Globally Unique Identifier <tt>String</tt> representation
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public static CycConstant makeCycConstant(String guidString, String name) {
        return makeCycConstant(Guid.makeGuid(guidString), name);
    }

    /**
     * Constructs a new <tt>CycConstant</tt> object using the constant name.
     *
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public static CycConstant makeCycConstant(String name) {
        CycConstant cycConstant = (CycConstant) cache.getElement(name);
        if (cycConstant == null) {
            //TODO
            Guid guid = new Guid("** get from OpenCyc **");
            cycConstant = new CycConstant(guid, name);
            cache.addElement(cycConstant.name, cycConstant);
        }
        return cycConstant;
    }

    /**
     * Constructs a new <tt>CycConstant</tt> object given the guid and constant name.
     *
     * @param guid Globally Unique Identifier
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public static CycConstant makeCycConstant(Guid guid, String name) {
        CycConstant cycConstant = (CycConstant) cache.getElement(name);
        if (cycConstant == null) {
            cycConstant = new CycConstant(guid, name);
            cache.addElement(cycConstant.name, cycConstant);
            return cycConstant;
        }
        return cycConstant;
    }

    /**
     * Constructs a <tt>CycConstant<tt> object given the guid and constant name.
     *
     * @param guid Globally Unique Identifier
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    protected CycConstant (Guid guid, String name) {
        this.guid = guid;
        if (name.startsWith("#$"))
            this.name = name.substring(2);
        else
            this.name = name;
    }

    /**
     * Prints the XML representation of the <tt>CycConstant<tt> to an <tt>XMLPrintWriter</tt>
     * It is supposed to look like this:
     *
     * <constant>
     *  <guid>
     *   c10af8ae-9c29-11b1-9dad-c379636f7270
     *  </guid>
     *  <name>
     *   HandGrenade
     *  </name>
     * </constant>
     *
     * The parameter [int indent] specifies by how many spaces the XML
     * output should be indented.
     *
     * The parameter [boolean relative] specifies whether the
     * indentation should be absolute -- indentation with respect to
     * the beginning of a new line, relative = false -- or relative
     * to the indentation currently specified in the indent_string field
     * of the xml_writer object, relative = true.
     *
     */

/*
    public void toXML (XMLPrintWriter xml_writer, int indent, boolean relative) {
        xml_writer.printXMLStartTag(constant_xml_tag, indent, relative);
        xml_writer.printXMLStartTag(guid_xml_tag, indent_length, true);
        xml_writer.indentPrintln(this.guid, indent_length, true);
        xml_writer.printXMLEndTag(guid_xml_tag, -indent_length, true);
        xml_writer.printXMLStartTag(name_xml_tag, 0, true);
        xml_writer.indentPrintln(this.name, indent_length, true);
        xml_writer.printXMLEndTag(name_xml_tag, -indent_length, true);
        xml_writer.printXMLEndTag(constant_xml_tag, -indent_length, true);
    }
*/

    /**
     * Provides the hash code appropriate for the <tt>CycConstant</tt>.
     *
     * @return the hash code for the <tt>CycConstant</tt>
     */
    public int hashCode() {
        return this.guid.hashCode();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>CycConstant</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (object instanceof CycConstant &&
            this.guid.equals(((CycConstant)object).guid) &&
            this.name.equals(((CycConstant)object).name)) {
            return true;
        }
        else
            return false;
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
        if (! (object instanceof CycConstant))
            throw new ClassCastException("Must be a CycConstant object");
        return this.name.compareTo(((CycConstant) object).name);
     }

    /**
     * Returns a String representation of the <tt>CycConstant</tt>.
     */
    public String toString() {
        return name;
    }

    public String cyclify() {
        return cycName();
    }

    /**
     * Returns the name of the <tt>CycConstant</tt> with "#$" prefixed.
     *
     * @return the name of the <tt>CycConstant</tt> with "#$" prefixed.
     */
    public String cycName() {
        return "#$" + name;
    }

    /**
     * Resets the Cyc constant cache.
     */
    public static void resetCache() {
        cache = new CacheLRU(500);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with name, returning null if not found in the cache.
     */
    public static CycConstant getCache(String name) {
        return (CycConstant) cache.getElement(name);
    }

    /**
     * Removes the cycConstant from the cache if it is contained within.
     */
    public static void removeCache(CycConstant cycConstant) {
        Object element = cache.getElement(cycConstant.name);
        if (element != null)
            cache.addElement(cycConstant.name, null);
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
     * Returns the size of the <tt>CycConstant</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycConstant</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }
}
