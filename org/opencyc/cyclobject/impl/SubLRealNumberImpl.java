package org.opencyc.cyclobject.impl;

import java.io.IOException;

import org.opencyc.cyclobject.subl.SubLRealNumber;
import org.opencyc.xml.XMLWriter;

/*****************************************************************************
 * KB comment for #$SubLRealNumber as of 2002/05/07:<p>
 *
 * The collection of all number-denoting expressions in the CycL language that
 * are _not_ CycL constants or NATs, but are terms of SubL, the underlying
 * implementation language of the Cyc system. #$SubLRealNumbers are numeric
 * strings of the Arabic decimal system, including the decimal point and
 * scientific notation.  Examples include the terms `212' and `3.14159d0'.
 * Non-examples include the expressions `#$One', `(#$Meter 6)', `(#$Unity 3)',
 * `:34', `#$PlusInfinity', and `Avogadro's number'.  Note that this collection,
 * like most instances of #$CycLExpressionType, is "quoted" (see
 * #$quotedCollection).<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
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
 *****************************************************************************/
public class SubLRealNumberImpl extends SubLAtomicTermImpl
  implements SubLRealNumber {

  public boolean isRealNumberLiteral() { return true; }

  public Double getDouble() { return (Double)getValue(); }

  public double getDoubleValue() { return ((Double)getValue()).doubleValue(); }

  public String toString() { return getValue().toString(); }

  public String cyclify() { return getValue().toString(); }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; }

  protected SubLRealNumberImpl(Object val) { 
    if(!(val instanceof Double)) { throw new IllegalArgumentException(); } 
    setValue(val);
  }

  public static SubLRealNumberImpl createSubLCharacter(Double num) { 
    try {
      return (SubLRealNumberImpl)getSubLAtomicTerm(num, 
        Class.forName("SubLRealNumberImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }

}
