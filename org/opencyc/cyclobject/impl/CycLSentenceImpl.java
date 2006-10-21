package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.CycLSentence;

/*****************************************************************************
 * KB comment for #$CycLSentence as of 2002/05/07:<p>
 *
 * The collection of syntactically well-formed sentences of the CycL language.
 * Each instance of #$CycLSentence consists of a CycL expression denoting a
 * logical relation (i.e. a #$Predicate or #$TruthFunction) followed by one or
 * more CycL terms, with the entire sequence enclosed in parentheses.
 * #$CycLSentences need not obey arity constraints or other semantic constraints
 * (such as argument type constraints). #$CycLSentences are also called "logical
 * formulas", and are to be distinguished from "denotational formulas" (which
 * are also known as "NAT"s; see #$CycLNonAtomicTerms).  Note that this notion
 * of a CycL sentence is broader than the notion of "sentence" standardly used
 * in formal logic, where a sentence is defined as a _closed_ well-formed
 * formula: CycL sentences may be _open_ (i.e. contain free variables).<p>
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
public abstract class CycLSentenceImpl extends CycLFormulaImpl 
  implements CycLSentence {
  public boolean isSentence() { return true; }
}
