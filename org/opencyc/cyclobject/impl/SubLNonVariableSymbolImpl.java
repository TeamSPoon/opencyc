package org.opencyc.cyclobject.impl;

import java.io.IOException;

import org.opencyc.cyclobject.subl.SubLNonVariableSymbol;
import org.opencyc.xml.XMLWriter;

/*****************************************************************************
 * KB comment for #$SubLNonVariableSymbol as of 2002/05/07:<p>
 *
 * The collection of all #$SubLSymbols except #$SubLVariables (qq.v.); a
 * subcollection of #$CycLClosedAtomicTerm.  Note that `symbol' has a very
 * specific, technical meaning in SubL; #$SubLNonVariableSymbols are rarely used
 * in CycL assertions, except within those built with certain
 * #$CycInferenceDescriptorPredicates like #$defnIff. Examples of SubL
 * non-variable symbols include the symbols `GENLS' and
 * `CYC-SYSTEM-NON-VARIABLE-SYMBOL-P'.  Note that this collection, like most
 * instances of #$CycLExpressionType, is "quoted" (see #$quotedCollection).<p>
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
public class SubLNonVariableSymbolImpl extends SubLAtomicTermImpl
  implements SubLNonVariableSymbol {

  public boolean isSymbolLiteral() { return true; }

  public String toString() { return "\"" + getValue() + "\""; }

  public String cyclify() { return "\"" + getValue() + "\""; }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; } 
  
  protected SubLNonVariableSymbolImpl(Object val) { 
    if(!(val instanceof String)) { throw new IllegalArgumentException(); } 
    setValue(val);
  }

  public static SubLNonVariableSymbolImpl createSubLNonVariableSymbolImpl(String str) { 
    try {
      return (SubLNonVariableSymbolImpl)getSubLAtomicTerm(str, 
        Class.forName("SubLNonVariableSymbolImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
    

}
