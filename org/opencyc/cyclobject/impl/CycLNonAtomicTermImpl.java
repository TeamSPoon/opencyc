package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.CycLNonAtomicTerm;
import org.opencyc.cyclobject.CycLRepresentedTerm;

/*****************************************************************************
 * KB comment for #$CycLNonAtomicTerm as of 2002/05/07:<p>
 *
 * The collection of non-atomic denotational terms in the CycL language.  A CycL
 * term is _non-atomic_ if it constructible from other CycL terms via the syntax
 * of CycL.  A CycL term is said to be "denotational" if it is the type of term
 * that can have a denotatum (or assigned value; see #$CycLDenotationalTerm).
 * #$CycLNonAtomicTerm thus includes all CycL denotational terms except
 * constants and variables.  A CycL non-atomic term (or "NAT") consists of a
 * CycL expression denoting a #$Function-Denotational followed by one or more
 * CycL terms, with the entire sequence enclosed in parentheses.  The NAT itself
 * denotes the value (if any) of this function for the denotations of the other
 * terms taken as arguments.  (If there is no such value then the NAT has no
 * denotatum; see #$undefined.)  NATs are also known as "denotational formulas",
 * in contrast to "logical formulas" (i.e. sentences). Currently, there are two
 * main types of NAT: (i) #$HLNonAtomicReifiedTerms (or "NART"s), which are a
 * type of #$HLReifiedDenotationalTerm and are implemented with data structures
 * that have indexing that enables all uses of the NAT to be retrieved, and (ii)
 * #$ELNonAtomicTerms (or "NAUT"s), which have no such indexing and remain in
 * the form of an EL expression in the assertions in which they occur.<p>
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
public abstract class CycLNonAtomicTermImpl extends CycLFormulaImpl 
  implements CycLNonAtomicTerm, CycLRepresentedTerm {
  public boolean isRepresented() { return true; }
}
