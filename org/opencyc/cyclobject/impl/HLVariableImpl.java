package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.hl.*;
import org.opencyc.xml.XMLWriter;

import java.io.IOException;
import java.util.List;

/*****************************************************************************
 * KB comment for #$HLVariable as of 2002/05/07:<p>
 *
 * The collection of all variables in the HL language, used internally by the
 * inference engine and not normally visible to users of Cyc.<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://sf.net/projects/opencyc">OpenCyc at SourceForge</a>
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
public class HLVariableImpl extends CycLVariableImpl
  implements HLVariable, HLExpression {

  public String variableName = "";

  public boolean isHL() { return true; }
 
  public String toString() { return variableName; }

  public String cyclify() { return variableName; }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; }  

  
  protected HLVariableImpl(String variableName) {
    if((variableName == null) || variableName.equals("") || 
      (containsInvalidCharacters(variableName)) || 
      (!variableName.startsWith("?"))) {
        throw new IllegalArgumentException();
    }
    this.variableName = variableName;
  }
  
  public static boolean containsInvalidCharacters(String constantName) {
    //FIX ME!!!!!!!!!!
    return false;
  }
  
  protected static HLVariableImpl createHLVariable(String variableName) {
    if((variableName == null) || variableName.equals("") || 
      (containsInvalidCharacters(variableName)) || 
      (!variableName.startsWith("?"))) {
        throw new IllegalArgumentException();
    }
    String key = "HLVariableImpl:" + variableName;
    HLVariableImpl result = (HLVariableImpl)CycLTermImpl.loadFromCache(key);
    if(result == null) {
      result = new HLVariableImpl(variableName);
      CycLTermImpl.saveToCache(key, result);
    }
    return result; 
  }
    
}
