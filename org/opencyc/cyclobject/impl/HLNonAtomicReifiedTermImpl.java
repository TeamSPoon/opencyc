package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.hl.*;
import org.opencyc.xml.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;

/*****************************************************************************
 * KB comment for #$HLNonAtomicReifiedTerm as of 2002/05/07:<p>
 *
 * The collection of all non-atomic denotational terms in the HL language, used
 * internally by the inference engine.  These are HL formulas with an instance
 * of #$ReifiableFunction as their functor and optionally other HL terms as
 * their arguments.  These HL NATs are often called "NARTs", which stands for
 * Non-Atomic Reified Term.  The difference between HL NATs and reifiable EL
 * NATs should be transparent to users of Cyc, because the #$CycCanonicalizer
 * automatically transforms all reifiable EL NATs into HL NATs.<p>
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
public class HLNonAtomicReifiedTermImpl extends CycLNonAtomicReifiedTermImpl
  implements HLNonAtomicReifiedTerm, HLReifiedDenotationalTerm, 
             HLReifiedFormula {

  Object nartGuts = null;
  
  public boolean isHL() { return true; }

  public boolean isDenotational() { return true; }
 
  public String toString() { return nartGuts.toString(); }

  public String cyclify() { return nartGuts.toString(); }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }

  public boolean isIndividual() {  return false; }

  public boolean isCollection() {  return false; }

  public boolean isPredicate() {  return false; }

  public boolean isFunctor() {  return false; }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; }  

  public CycLTerm getFunctor() { return null; }

  public CycLFormula getArguments() { return null; }
  
  protected HLNonAtomicReifiedTermImpl(ArrayList rep) {
    initFormula(rep);
  }

  public static HLNonAtomicReifiedTermImpl 
    createHLNonAtomicReifiedTerm(ArrayList rep) { 
    try {
      return (HLNonAtomicReifiedTermImpl)createFormula(rep, 
        Class.forName("HLNonAtomicReifiedTermImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
    
}
