package org.opencyc.cycobject;

import java.io.*;
import java.util.*;
import org.apache.oro.util.*;
import org.opencyc.xml.XMLPrintWriter;
import org.opencyc.api.*;

/**
 * This class implements the behavior and attributes of a
 * an OpenCyc NART (Non Atomic Reified Term).
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
public class CycNart extends CycFort implements Comparable {

    /**
     * XML serialization tags.
     */
    public static final String natXMLtag = "nat";
    public static final String functorXMLtag = "functor";
    public static final String argXMLtag = "arg";

    /**
     * XML serialization indentation.
     */
    public static int indentLength = 2;

    /**
     * The functor of the <ttt>CycNart</tt> object. For example, the <tt>CycConstant</tt>
     * corresponding to #$FruitFn in (#$FruitFn #$AppleTree). This must be a CycFort
     * (i.e. cannot be assumed to be a CycConstant) because functors can themselves be
     * CycNarts.
     */
    private CycFort functor;

    /**
     * The list of the arguments of the <ttt>CycNart</tt> object.
     */
    private CycList arguments = new CycList();

    /**
     * Least Recently Used Cache of CycNarts, so that a reference to an existing <tt>CycNart</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cache = new CacheLRU(500);

    /**
     * Constructs a new incomplete <tt>CycNart</tt> object.
     */
    public CycNart () {
    }

    /**
     * Constructs a new <tt>CycNart</tt> object from the given functor and
     * argument list.
     *
     * @param functor a <tt>CycFort</tt> which is the functor of this
     * <tt>CycNart</tt> object.
     * @param arguments a <tt>CycList</tt> of the functor's arguments which
     * are cyc objects
     */
    public CycNart (CycFort functor, CycList arguments) {
        this.functor = functor;
        this.arguments = arguments;
    }

    /**
     * Constructs a new unary <tt>CycNart</tt> object from the functor and
     * argument.
     *
     * @param functor a <tt>CycFort</tt> which is the functor of this
     * <tt>CycNart</tt> object.
     * @param argument an <tt>Object</tt> most typically a <tt>CycConstant</tt>
     * which is the single argument of this <tt>CycNart</tt> object.
     */
    public CycNart (CycFort functor, Object argument) {
        this.functor = functor;
        arguments.add(argument);
        this.arguments = arguments;
    }

    /**
     * Constructs a new binary <tt>CycNart</tt> object from the functor and
     * the two arguments.
     *
     * @param functor a <tt>CycFort</tt> which is the functor of this
     * <tt>CycNart</tt> object.
     * @param argument1 an <tt>Object</tt> most typically a <tt>CycConstant</tt>
     * @param argument2 an <tt>Object</tt> most typically a <tt>CycConstant</tt>
     * which is the single argument of this <tt>CycNart</tt> object.
     */
    public CycNart (CycFort functor, Object argument1, Object argument2) {
        this.functor = functor;
        arguments.add(argument1);
        arguments.add(argument2);
        this.arguments = arguments;
    }

    /**
     * Constructs a new <tt>CycNart</tt> object from the <tt>CycList</tt> object.
     *
     * @param cycList a list representation of the <tt>CycNart</tt>
     */
    public CycNart (CycList cycList) {
        if (cycList.size() == 0)
            throw new RuntimeException("Cannot make a CycNart from an empty CycList");
        if (! (cycList.first() instanceof CycFort))
            throw new RuntimeException("CycNart functor must be a CycFort");
        functor = (CycFort) cycList.first();
        arguments.addAll(cycList.rest());
    }

    /**
     * Returns the given object if it is a <tt>CycNart</tt>, otherwise the object is expected to be
     * a <tt>CycList</tt> and a <tt>CycNart</tt> object is returned using the given
     * CycList representation.
     *
     * @param object the object to be coerced into a CycNart
     * @return the given object if it is a <tt>CycNart</tt>, otherwise the object is expected to be
     * a <tt>CycList</tt> and a <tt>CycNart</tt> object is returned using the given
     * CycList representation
     */
    public static CycNart coerceToCycNart(Object object) {
        if (object instanceof CycNart)
            return (CycNart) object;
        if (! (object instanceof CycList))
            throw new RuntimeException("Cannot coerce to CycNart " + object);
        return new CycNart((CycList) object);
    }

    /**
     * Returns the functor of the <tt>CycNart</tt>.
     *
     * @return the functor of the <tt>CycNart</tt>
     */
    public CycFort getFunctor() {
        if (functor == null) {
            CycNart cycNart = null;
            try {
                cycNart = CycAccess.current().completeCycNart(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            functor = cycNart.functor;
            arguments = cycNart.arguments;
        }
        return functor;
    }

    /**
     * Sets the functor of the <tt>CycNart</tt>.
     *
     * @param functor the <tt>CycFort</tt> functor object of the <tt>CycNart</tt>
     */
    public void setFunctor(CycFort functor) {
        this.functor = functor;
    }

    /**
     * Returns the arguments of the <tt>CycNart</tt>.
     *
     * @return the arguments of the <tt>CycNart</tt>
     */
    public List getArguments() {
        if (arguments == null) {
            CycNart cycNart = null;
            try {
                cycNart = CycAccess.current().completeCycNart(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            functor = cycNart.functor;
            arguments = cycNart.arguments;
        }
        return arguments;
    }

    /**
     * Sets the arguments of the <tt>CycNart</tt>.
     *
     * @param arguments the arguments of the <tt>CycNart</tt>
     */
    public void setArguments(CycList arguments) {
        this.arguments = arguments;
    }

    /**
     * Prints the XML representation of the <ttt>CycNart</tt> to an <tt>XMLPrintWriter</tt>
     * It is supposed to look like this:<p>
     * <pre>
     * <nat>
     *  <functor>
     *   <constant>
     *    <guid>bd58a976-9c29-11b1-9dad-c379636f7270</guid>
     *    <name>FruitFn</name>
     *   </constant>
     *  </functor>
     *  <arg>
     *   <constant>
     *    <guid>bd58c19d-9c29-11b1-9dad-c379636f7270</guid>
     *    <name>AppleTree</name>
     *   </constant>
     *  </arg>
     * </nat>
     * </pre>
     *
     * The parameter [int indent] specifies by how many spaces the XML
     * output should be indented.<p>
     *
     * The parameter [boolean relative] specifies whether the
     * indentation should be absolute -- indentation with respect to
     * the beginning of a new line, relative = false -- or relative
     * to the indentation currently specified in the indent_string field
     * of the xml_writer object, relative = true.
     *
     */
    public void toXML (XMLPrintWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(natXMLtag, indent, relative);
        xmlWriter.printXMLStartTag(functorXMLtag, indentLength, true);
        this.getFunctor().toXML(xmlWriter, indentLength, true);
        xmlWriter.printXMLEndTag(functorXMLtag, -indentLength, true);
        ListIterator iterator = this.getArguments().listIterator();
        Object arg;
        while (iterator.hasNext()) {
            xmlWriter.printXMLStartTag(argXMLtag, 0, true);
            arg = iterator.next();
            if (arg instanceof CycFort) {
                ((CycFort) arg).toXML(xmlWriter, indentLength, true);
            }
            else
                xmlWriter.indentPrintln((String)arg, indentLength, true);
            xmlWriter.printXMLEndTag(argXMLtag, -indentLength, true);
        }
        xmlWriter.printXMLEndTag(natXMLtag, -indentLength, true);
    }

    /**
     * Returns a string representation of the OpenCyc NART.
     *
     * @return a <tt>String</tt> representation of the OpenCyc NART.
     */
    public String toString() {
        StringBuffer result = new StringBuffer("(");
        result.append(this.getFunctor().toString());
        ListIterator iterator = this.getArguments().listIterator();
        while (iterator.hasNext()) {
            result.append(" ");
            result.append(iterator.next().toString());
        }
        return result.append(")").toString();
    }

    /**
     * Returns a cyclified string representation of the OpenCyc NART.
     * Embedded constants are prefixed with ""#$".
     *
     * @return a cyclified <tt>String</tt>.
     */
    public String cyclify() {
        StringBuffer result = new StringBuffer("(");
        result.append(this.getFunctor().cyclify());
        ListIterator iterator = this.getArguments().listIterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            String cyclifiedObject = null;
            if (object instanceof CycConstant)
                cyclifiedObject = ((CycConstant) object).cyclify();
            else if (object instanceof CycNart)
                cyclifiedObject = ((CycNart) object).cyclify();
            else
                cyclifiedObject = object.toString();
            result .append(" ");
            result.append(cyclifiedObject);
        }
        return result.append(")").toString();
    }

    /**
     * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>String</tt> api expression value
     */
    public String stringApiValue() {
        return "(quote " + cyclify() + ")";
    }

    /**
     * Returns this object in a form suitable for use as an <tt>CycList</tt> api expression value.
     *
     * @return this object in a form suitable for use as an <tt>CycList</tt> api expression value
     */
    public Object cycListApiValue() {
        CycList apiValue = new CycList();
        apiValue.add(CycSymbol.quote);
        apiValue.add(this);
        return apiValue;
    }

    /**
     * Returns a string representation of the <ttt>CycNart</tt> with the guid in place
     * of the constant name.
     *
     * @return a <tt>String</tt> representation of the <ttt>CycNart</tt> with <tt>Guid</tt>
     * external forms in place of the <tt>CycConstant</tt> names.
     */
    public String metaGuid() {
        String functorGuid =
            (this.functor instanceof CycConstant ?
                ((CycConstant) this.functor).getGuid().toString() : ((CycNart) this.functor).metaGuid());
        ListIterator iterator = this.arguments.listIterator();
        StringBuffer result = new StringBuffer("(");
        result.append(functorGuid);
        Object arg;
        String argGuid;
        while (iterator.hasNext()) {
            arg = iterator.next();
            if (arg instanceof CycConstant)
                argGuid = ((CycConstant)arg).getGuid().toString();
            else if (arg instanceof CycNart)
                argGuid = ((CycNart) arg).metaGuid();
            else
                argGuid = (String) arg;
            result.append(" ");
            result.append(argGuid);
        }
        return result.append(")").toString();
    }

    /**
     * Returns a metaName representation of the <tt>CycNart</tt>.
     *
     * @return a <tt>String</tt> metaName representation
     */
    public String metaName() {
        String functorName =
            (this.functor instanceof CycConstant ?
                ((CycConstant)this.getFunctor()).getName() :
                ((CycNart) this.getFunctor()).metaName());
        ListIterator iterator = this.getArguments().listIterator();
        StringBuffer result = new StringBuffer("(");
        result.append(functorName);
        Object arg;
        String argName;
        while (iterator.hasNext()) {
            arg = iterator.next();
            if (arg instanceof CycConstant)
                argName = ((CycConstant)arg).getName();
            else if (arg instanceof CycNart)
                argName = ((CycNart)arg).metaName();
            else
                argName = (String)arg;
            result.append(" ");
            result.append(argName);
        }
        return result.append(")").toString();
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>CycNart</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycNart))
            return false;
        Integer thisId = super.getId();
        Integer thatId = ((CycFort) object).getId();
        if ((thisId != null) && (thatId != null))
            return thisId.equals(thatId);
        CycNart thatNart = (CycNart) object;
        if (! this.functor.equals(thatNart.functor))
            return false;
        return this.arguments.equals(thatNart.arguments);
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
        if (! (object instanceof CycNart))
            throw new ClassCastException("Must be a CycNart object");
        return this.toString().compareTo(object.toString());
     }

    /**
     * Resets the <tt>CycNart</tt> cache.
     */
    public static void resetCache() {
        cache = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycNart</tt> to the cache.
     */
    public static void addCache(CycNart cycNart) {
        cache.addElement(cycNart.getId(), cycNart);
    }

    /**
     * Retrieves the <tt>CycNart</tt> with name, returning null if not found in the cache.
     */
    public static CycNart getCache(Integer id) {
        return (CycNart) cache.getElement(id);
    }

    /**
     * Removes the <tt>CycNart</tt> from the cache if it is contained within.
     */
    public static void removeCache(CycNart cycNart) {
        Object element = cache.getElement(cycNart.getId());
        if (element != null)
            cache.addElement(cycNart.getId(), null);
    }

    /**
     * Returns the size of the <tt>CycNart</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycNart</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }

}

