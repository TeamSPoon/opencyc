/*
 * DefaultCycObject.java
 *
 * Created on May 10, 2002, 11:35 AM
 */

package org.opencyc.cycobject;

import java.io.IOException;
import org.opencyc.xml.XMLWriter;
import org.opencyc.api.CycAccess;
import org.opencyc.util.StringUtils;
import java.util.*;
import java.math.BigInteger;

/**
 * This is the default implementation of a CycL object.
 *
 * @version $Id$
 * @author  tbrussea
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
 *
 */
public abstract class DefaultCycObject implements CycObject {

  /**
   * Field for storing the name of the XML tag for CycConstant objects
   */
  public static final String objectXMLTag = "cycl-object";
      
  /**
   * Returns a cyclified string representation of the CycL object.
   * By default, just returns the result of calling "toString()".
   * A cyclified string representation is one where constants have been
   * prefixed with #$.
   *
   * @return a cyclified <tt>String</tt>.
   */ 
  public String cyclify() { return toString(); }
      
  /**
   * Returns a cyclified string representation of the CycL object.
   * By default, just returns the result of calling "cyclify()".
   * A cyclified string representation with escape chars is one where
   * constants have been prefixed with #$ and Strings have had an escape
   * character inserted before each character that needs to be escaped in SubL.
   *
   * @return a cyclified <tt>String</tt> with escape characters.
   */ 
  public String cyclifyWithEscapeChars() { return cyclify(); }
  
    /**
     * Returns a cyclified string representation of the OpenCyc <tt>CycList</tt>.
     * Embedded constants are prefixed with "#$".  Embedded quote chars in strings
     * are escaped.
     *
     * @return a <tt>String</tt> representation in cyclified form.
     *
     */
  public static String cyclifyWithEscapeChars(Object obj)  { 
    if ((obj == null) || (!isCycLObject(obj))) {
      throw new RuntimeException("Cannot cyclify (escaped): '" + obj + "'.");
    }
    if (obj instanceof CycObject) {
      return ((CycObject)obj).cyclifyWithEscapeChars();
    }
    if (obj instanceof String) {
      String str=(String)obj;
      if(StringUtils.is7BitASCII(str))
	return "\"" + StringUtils.escapeDoubleQuotes(str) + "\"";
      else return "\""+StringUtils.UnicodeEscaped(str) + "\"";
    }
    return obj.toString(); 
  }
  
  /**
   * Returns a cyclified string representation of the given <tt>Object</tt>.
   * Embedded constants are prefixed with "#$".
   *
   * @return a <tt>String</tt> representation in cyclified form.
   *
   */
  public static String cyclify(Object obj) {    
    if (obj == null) {      
      throw new RuntimeException("Cannot cyclify null obj");
    }
    else if (!isCycLObject(obj)) {      
      throw new RuntimeException("Cannot cyclify: '" + obj + "' (" + obj.getClass().getName() + ").");
    }
    if (obj instanceof CycObject) {
      return ((CycObject)obj).cyclify();
    }
    if (obj instanceof String) {
      return "\"" + (String)obj + "\"";
    }
    return obj.toString(); 
  }
  
  public static List getReferencedConstants(Object obj) {
    if (obj == null) { return new ArrayList(); }
    if ((obj == null) || (!isCycLObject(obj))) {      
      throw new RuntimeException("Got an object that is not a valid CycL term: '" + obj + "' (" + obj.getClass().getName() + ").");
    }
    if (!(obj instanceof CycObject)) { return new ArrayList(); }
    return ((CycObject)obj).getReferencedConstants();
  }
  
  private static final List emptyList = Arrays.asList(new Object[0]);
  
  public List getReferencedConstants() {
    return emptyList;
  }

  /**
   * Returns the given <tt>Object</tt> in a form suitable for use as a <tt>String</tt> api expression value.
   *
   * @return the given <tt>Object</tt> in a form suitable for use as a <tt>String</tt> api expression value
   */
  public static String stringApiValue(Object obj) {    
    if ((obj == null) || (!isCycLObject(obj)))
      throw new RuntimeException(obj + " cannot be converted to a form suitable for use as a String api expression value.");
    if (obj instanceof CycObject)
      return ((CycObject)obj).stringApiValue();
    return cyclifyWithEscapeChars(obj);
  }
  
  /**
   * Returns true iff the given object is an object than can be contained
   * in a CycL expression.
   *
   * @return true iff the given object is an object than can be contained
   * in a CycL expression
   */
  public static boolean isCycLObject(Object obj) {
    return 
      (obj instanceof CycObject ||
       obj instanceof String ||
       obj instanceof Integer ||
       obj instanceof Long ||
       obj instanceof BigInteger ||
       obj instanceof Guid ||
       obj instanceof Float ||
       obj instanceof Double);
  }
  
  /**
   * Returns a pretty CycL representation of this object.
   *
   * @return a string representation without causing additional api calls to determine
   * constant names
   */
  public static String toPrettyString(Object obj) {
    if (obj instanceof String) {
      return "\"" + obj.toString() + "\"";
    } else if (obj instanceof CycList) {
      return ((CycList)obj).toPrettyString("");
    } 
    return obj.toString();
  }
    /**
   * Returns this object in a form suitable for use as an <tt>String</tt> api expression value.
   *
   * @return this object in a form suitable for use as an <tt>String</tt> api expression value
   */
  public String stringApiValue()  {
    return cyclifyWithEscapeChars();
  }
  
  /**
   * Returns this object in a form suitable for use as an <tt>CycList</tt> api expression value.
   *
   * @return this object in a form suitable for use as an <tt>CycList</tt> api expression value
   */
  public Object cycListApiValue() {
    return cyclify();
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
      xmlWriter.printXMLStartTag(objectXMLTag, indent, relative, true);
      xmlWriter.print(stringApiValue());
      xmlWriter.printXMLEndTag(objectXMLTag, -indent, true);
    }

  /**
   * Returns whether the given Object represents a Cyc Collection.
   *
   * @return whether the given Object represents a Cyc Collection.
   */
    public static boolean isCollection(Object term, CycAccess cycAccess) throws IOException {
      return cycAccess.isCollection(term);
    }

  public static int getCycObjectType(Object object){
    if(object instanceof ByteArray)        return CYCOBJECT_BYTEARRAY;
    else if(object instanceof CycAssertion)return CYCOBJECT_CYCASSERTION;
    else if(object instanceof CycFort)     return CYCOBJECT_CYCFORT;
    else if(object instanceof CycSymbol)   return CYCOBJECT_CYCSYMBOL;
    else if(object instanceof CycVariable) return CYCOBJECT_CYCVARIABLE;
    else if(object instanceof CycList)     return CYCOBJECT_CYCLIST;
    else if(object instanceof Double)      return CYCOBJECT_DOUBLE;
    else if(object instanceof Float)       return CYCOBJECT_FLOAT;
    else if(object instanceof Guid)        return CYCOBJECT_GUID;
    else if(object instanceof Integer)     return CYCOBJECT_INTEGER;
    else if(object instanceof Long)        return CYCOBJECT_LONG;
    else if(object instanceof BigInteger)  return CYCOBJECT_BIGINTEGER;
    else if(object instanceof String)      return CYCOBJECT_STRING;
    else                                   return CYCOBJECT_UNKNOWN;
  }
    
}














