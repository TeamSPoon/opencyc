package org.opencyc.cycobject;

import java.io.*;
import org.opencyc.xml.*;

/**
 * Provides the behavior and attributes of an OpenCyc symbol, typically used
 * to represent api function names, and non <tt>CycConstant</tt> parameters.
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
public class CycSymbol extends CycObject implements Comparable, Serializable {

    /**
     * The name of the XML tag for this object.
     */
    public static final String cycSymbolXMLTag = "symbol";

    /**
     * The symbol represented as a <tt>String</tt>.
     */
    public String symbolName;

    /**
     * Constructs a new <tt>CycSymbol</tt> object.
     *
     * @param symbolName the <tt>String</tt> name of the <tt>CycSymbol</tt>.
     */
    public CycSymbol(String symbolName) {
        if (! (symbolName.equals(symbolName.toUpperCase())))
            throw new RuntimeException("symbol name must be upper case " + symbolName);
        this.symbolName = symbolName;
    }

    /**
     * Returns the string representation of the <tt>CycSymbol</tt>
     *
     * @return the representation of the <tt>CycSymbol</tt> as a <tt>String</tt>
     */
    public String toString() {
        return symbolName;
    }

    /**
     * Returns <tt>true</tt> iff some object equals this <tt>CycSymbol</tt>
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycSymbol))
            return false;
        return ((CycSymbol) object).toString().equals(symbolName);
    }

    /**
     * Returns <tt>true</tt> iff this symbol is a SubL keyword.
     *
     * @return <tt>true</tt> iff this symbol is a SubL keyword
     */
    public boolean isKeyword() {
        return this.symbolName.startsWith(":");
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
        if (! (object instanceof CycSymbol))
            throw new ClassCastException("Must be a CycSymbol object");
        return this.symbolName.compareTo(((CycSymbol) object).symbolName);
     }

    /**
     * Returns <tt>true</tt> iff the given string is a valid symbol name.
     *
     * @param string the name to be tested
     * @return <tt>true</tt> iff the given string is a valid symbol name
     */
    public static boolean isValidSymbolName(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (! (Character.isLetterOrDigit(c) ||
                   c == '-' ||
                   c == '_' ||
                   c == '*' ||
                   c == '?'||
                   c == ':'))
                return false;
        }
        return true;
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
     * Prints the XML representation of the CycSymbol to an <code>XMLWriter</code>
     *
     * @param xmlWriter an <tt>XMLWriter</tt>
     * @param indent an int that specifies by how many spaces to indent
     * @param relative a boolean; if true indentation is relative, otherwise absolute
     */
    public void toXML (XMLWriter xmlWriter, int indent, boolean relative)
        throws IOException {
        xmlWriter.printXMLStartTag(cycSymbolXMLTag, indent, relative, false);
        xmlWriter.print(TextUtil.doEntityReference(symbolName));
        xmlWriter.printXMLEndTag(cycSymbolXMLTag);
    }
}