package org.opencyc.cycobject;

import java.io.Serializable;
import org.apache.oro.util.*;
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
public class CycConstant extends CycFort implements Comparable {

    /**
     * Field for storing the name of the XML tag for CycConstant objects
     */
    public static final String constantXMLTag = "constant";

    /**
     * Field for storing the name of the XML tag for the name of CycConstant objects
     */
    public static final String nameXMLTag = "name";

    /**
     * Field for storing the name of the XML tag for the GUID of CycConstant objects
     */
    public static final String guidXMLTag = "guid";

    /**
     * The default indentation for printing CycConstant objects to XML
     */
    public static int indentLength = 2;

    /**
     * Least Recently Used Cache of CycConstants, so that a reference to an existing <tt>CycConstant</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cache = new CacheLRU(500);

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
     * Constructs a new incomplete <tt>CycConstant</tt> object, given an id.
     *
     * @param id the local KB id for this constant
     */
    public CycConstant (int id) {
        this.id = id;
    }

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
     * Constructs a new <tt>CycConstant</tt> object given the guid and constant name.
     *
     * @param guid Globally Unique Identifier
     * @param name Name of the constant. If prefixed with "#$", then the prefix is
     * removed for canonical representation.
     */
    public static CycConstant makeCycConstant(Guid guid, String name) {
        CycConstant cycConstant = getCache(guid);
        if (cycConstant == null) {
            cycConstant = new CycConstant(guid, name);
            addCache(cycConstant);
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
     * Prints the XML representation of the CycConstant to an <code>XMLWriter</code>
     *
     * @param xmlWriter a com.cyc.xml.XMLWriter
     * @param indent an int that specifies by how many spaces to indent
     * @param relative a boolean; if true indentation is relative, otherwise absolute
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws java.io.IOException {
        xmlWriter.printXMLStartTag(constantXMLTag, indent, relative, true);
        xmlWriter.printXMLStartTag(guidXMLTag, indentLength, true, false);
        xmlWriter.print(this.guid.toString());
        xmlWriter.printXMLEndTag(guidXMLTag);
        xmlWriter.printXMLStartTag(nameXMLTag, 0, true, false);
        xmlWriter.print(this.name);
        xmlWriter.printXMLEndTag(nameXMLTag);
        xmlWriter.printXMLEndTag(constantXMLTag, -indentLength, true);
    }

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
            this.guid.equals(((CycConstant)object).guid)) {
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
     * Adds the <tt>CycConstant<tt> to the cache.
     */
    public static void addCache(CycConstant cycConstant) {
        cache.addElement(cycConstant.guid, cycConstant);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with guid, returning null if not found in the cache.
     */
    public static CycConstant getCache(Guid guid) {
        return (CycConstant) cache.getElement(guid);
    }

    /**
     * Removes the cycConstant from the cache if it is contained within.
     */
    public static void removeCache(Guid guid) {
        Object element = cache.getElement(guid);
        if (element != null)
            cache.addElement(guid, null);
    }

    /**
     * Returns the size of the <tt>CycConstant</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycConstant</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
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
