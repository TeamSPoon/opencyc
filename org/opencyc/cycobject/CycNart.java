package org.opencyc.cycobject;

import java.io.*;
import java.util.*;
import org.opencyc.xml.*;
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
            throw new RuntimeException("CycNart functor must be a CycFort " + cycList.cyclify());
        functor = (CycFort) cycList.first();
        arguments.addAll((CycList) cycList.rest());
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
            catch (CycApiException e) {
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
            catch (CycApiException e) {
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
     * Prints the XML representation of the <ttt>CycNart</tt> to an <tt>XMLWriter</tt>
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
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(natXMLtag, indent, relative, true);
        if (super.getId() != null) {
            xmlWriter.printXMLStartTag(idXMLTag, indentLength, true, false);
            xmlWriter.print(this.getId().toString());
            xmlWriter.printXMLEndTag(idXMLTag);
        }
        if (functor != null) {
            xmlWriter.printXMLStartTag(functorXMLtag, indentLength, true, true);
            this.getFunctor().toXML(xmlWriter, indentLength, true);
            xmlWriter.printXMLEndTag(functorXMLtag, -indentLength, true);
        }
        ListIterator iterator = this.getArguments().listIterator();
        Object arg;
        while (iterator.hasNext()) {
            xmlWriter.printXMLStartTag(argXMLtag, 0, true, true);
            arg = iterator.next();
            // Use a shared method with CycList for arbitrary elements.
            CycList.toXML(arg, xmlWriter, indentLength, true);
            xmlWriter.printXMLEndTag(argXMLtag, 0, true);
        }
        xmlWriter.printXMLEndTag(natXMLtag, -indentLength, true);
    }

    /**
     * Returns a list representation of the OpenCyc NART.
     *
     * @return a <tt>CycList</tt> representation of the OpenCyc NART.
     */
    public CycList toCycList() {
        CycList cycList = new CycList();
        if (this.functor instanceof CycNart)
            cycList.add(((CycNart) functor).toCycList());
        else
            cycList.add(functor);
        ListIterator iterator = this.getArguments().listIterator();
        while (iterator.hasNext()) {
            Object argument = iterator.next();
            if (argument instanceof CycNart)
                cycList.add(((CycNart) argument).toCycList());
            else
                cycList.add(argument);
        }
        return cycList;
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
            Object object = iterator.next().toString();
            if (object instanceof String) {
                result.append("\"");
                result.append(object.toString());
                result.append("\"");
            }
            else
                result.append(object.toString());
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
            if(object instanceof CycObject) {
               cyclifiedObject = ((CycObject) object).cyclify();
            } else if (object instanceof CycList) {
               cyclifiedObject = ((CycList) object).cyclify();
            } else if (object instanceof String) {
               cyclifiedObject = "\"" + object + "\"";
            } else {
                cyclifiedObject = object.toString();
            }
            /*
            if (object instanceof CycConstant)
                cyclifiedObject = ((CycConstant) object).cyclify();
            else if (object instanceof CycNart)
                cyclifiedObject = ((CycNart) object).cyclify();
            else
                cyclifiedObject = object.toString();*/
            result .append(" ");
            result.append(cyclifiedObject);
        }
        return result.append(")").toString();
    }

    /**
     * Returns a string representation without causing additional api calls to determine
     * constant names.
     *
     * @return a string representation without causing additional api calls to determine
     * constant names
     */
    public String safeToString () {
        StringBuffer result = new StringBuffer("(");
        if (functor != null)
            result.append(this.getFunctor().safeToString());
        else
            result.append("<uncomplete functor>");
        ListIterator iterator = this.getArguments().listIterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            String safeObject = null;
            if (object instanceof CycFort)
                safeObject = ((CycFort) object).safeToString();
            else
                safeObject = object.toString();
            result .append(" ");
            result.append(safeObject);
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
        apiValue.add(CycObjectFactory.quote);
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

    /**
     * Return a hash value for this object.
     *
     * @return a hash value for this object
     */
    public int hashCode() {
        if (super.getId() != null)
            return this.getId().hashCode();
        else
            return functor.hashCode();
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
     * Returns true if the functor and arguments are instantiated.
     *
     * @return true if the functor and arguments are instantiated
     */
    public boolean hasFunctorAndArgs() {
        return (functor != null) && (this.arguments != null);
    }

}

