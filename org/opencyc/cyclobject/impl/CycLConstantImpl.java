package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.el.*;
import org.opencyc.cyclobject.hl.*;
import org.opencyc.cycobject.Guid;
import org.opencyc.xml.XMLWriter;

import java.io.IOException;
import java.util.List;

/*****************************************************************************
 * KB comment for #$CycLConstant as of 2002/05/07:<p>
 *
 * The collection of all constants in the CycL language. #$CycLConstant has as
 * instances all #$CycLAtomicTerms other than #$CycLVariables and
 * #$SubLAtomicTerms (qq.v.).  Orthographically, CycL constants are those atomic
 * terms (i.e. terms not constructable from other terms via CycL syntax) that
 * are prefixed by "#$" in their printed (as opposed to their HTML-displayed)
 * representations.  For example, `#$Dog' and `#$isa' are CycL constants, while
 * other CycL terms like `?X', `42', and `(#$GovernmentFn #$France)' are not.<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2002 Cycorp, Inc., license is open source GNU LGPL.
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
public class CycLConstantImpl extends CycLClosedAtomicTermImpl
  implements CycLConstant, CycLRepresentedAtomicTerm,
             HLReifiedDenotationalTerm, ELExpression {
  
  String constName = "";
  Guid guid = null;
  boolean isIndividual = false;
  boolean isCollection = false;
  boolean isPredicate = false;
  boolean isFunctor = false;
  
  public boolean isReified() { return true; }

  public boolean isIndexed() { return true; }

  public boolean isRepresented() { return true; }

  public boolean isHL() { return true; }

  public boolean isEL() { return true; }

  public boolean isIndividual() {  return isIndividual; }

  public boolean isCollection() {  return isCollection; }

  public boolean isPredicate() {  return isPredicate; }

  public boolean isFunctor() {  return isFunctor; }

  public String toString() { return constName; }

  public String cyclify() { return "#$" + constName; }

  public Guid getGuid() { return guid; }

  public String getName() { return constName; }
  
  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; } 

  protected CycLConstantImpl(String constantName, Guid guid, 
                             boolean isIndividual, boolean isCollection, 
                             boolean isPredicate, boolean isFunctor) {
    if((constantName == null) || constantName.equals("") || 
      (containsInvalidCharacters(constantName)) || (guid == null)) {
        throw new IllegalArgumentException();
    }
    if(constantName == null) { 
      throw new IllegalArgumentException("Null not allowed!"); 
    }
    this.constName = constantName;
    this.guid = guid;
    this.isIndividual = isIndividual;
    this.isCollection = isCollection;
    this.isPredicate = isPredicate;
    this.isFunctor = isFunctor;
  }
  
  public static boolean 
    containsInvalidCharacters(String constantName) {
    //FIX ME!!!!!!!!!!
    return false;
  }
  
  protected static CycLConstantImpl 
    createCycLConstant(String constantName, Guid guid, 
                       boolean isIndividual, boolean isCollection, 
                       boolean isPredicate, boolean isFunctor) {
    if((constantName == null) || constantName.equals("") || 
      (containsInvalidCharacters(constantName)) || (guid == null)) {
        throw new IllegalArgumentException();
    }
    String key = "CycLConstant:" + guid;
    CycLConstantImpl result = (CycLConstantImpl)CycLTermImpl.loadFromCache(key);
    if(result == null) {
      result = new CycLConstantImpl(constantName, guid, isIndividual, 
        isCollection, isPredicate,  isFunctor);
      CycLTermImpl.saveToCache(key, result);
    }
    return result; 
  }
    
}
