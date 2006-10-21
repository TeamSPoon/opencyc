package org.opencyc.cyclobject.impl;

import java.io.IOException;

import org.opencyc.cyclobject.el.ELExpression;
import org.opencyc.cyclobject.el.ELVariable;
import org.opencyc.xml.XMLWriter;

/*****************************************************************************
 * KB comment for #$ELVariable as of 2002/05/07:<p>
 *
 * The collection of all #$CycLVariables that are also #$ELExpressions (q.v.).
 * An #$ELVariable is a character string consisting of a question mark `?' (as
 * its initial character) followed by one or more characters, where each of
 * these latter characters is either an (upper- or lower-case) Roman letter, an
 * Arabic numeral (i.e. `0' through `9'), a hyphen (`-'), an underscore (`_'),
 * or another question mark.  (The letters occurring in an EL variable used in
 * an actual assertion will typically be all uppercase; but this convention is
 * not enforced in any formal way.)  Examples: `?WHAT', `?OBJ-TYPE', and
 * `?var0'.  Note that this collection, like most instances of
 * #$CycLExpressionType, is "quoted" (see #$quotedCollection).<p>
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
public class ELVariableImpl extends CycLVariableImpl
  implements ELVariable, ELExpression {

  public String variableName = "";

  public boolean isEL() { return true; }
 
  public String toString() { return variableName; }

  public String cyclify() { return variableName; }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; }  

  protected ELVariableImpl(String variableName) {
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
  
  protected static ELVariableImpl createELVariable(String variableName) {
    if((variableName == null) || variableName.equals("") || 
      (containsInvalidCharacters(variableName)) || 
      (!variableName.startsWith("?"))) {
        throw new IllegalArgumentException();
    }
    String key = "ELVariableImpl:" + variableName;
    ELVariableImpl result = (ELVariableImpl)CycLTermImpl.loadFromCache(key);
    if(result == null) {
      result = new ELVariableImpl(variableName);
      CycLTermImpl.saveToCache(key, result);
    }
    return result; 
  }
    
}
