package org.opencyc.cyclobject.impl;

import java.util.ArrayList;

import org.opencyc.cyclobject.CycLSentenceAskable;
import org.opencyc.cyclobject.el.ELExpressionAskable;
import org.opencyc.cyclobject.el.ELSentenceAskable;

/*****************************************************************************
 * KB comment for #$ELSentence-Askable as of 2002/05/07:<p>
 *
 * The subcollection of syntactically well-formed sentences in the EL language
 * which obey arity constraints, but but do not necessarily obey other semantic
 * constraints (e.g. argument type constraints).  These sentences 'make enough
 * sense' to be asked as a query, after being converted into HL form by the
 * #$CycCanonicalizer.  Of course, just because a sentence is askable does not
 * require it to be used in a query.  Each instance of this collection involves
 * a logical relation (a #$Predicate or #$TruthFunction) applied to some number
 * of arguments, as permitted by the arity of the relation. Note that an
 * instance of #$ELSentence-Askable does not mean that the sentence must be used
 * in a query; only that it can be used in a query.<p>
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
public class ELSentenceAskableImpl
  extends ELSentenceImpl
  implements ELSentenceAskable, ELExpressionAskable, CycLSentenceAskable {
    
  public boolean isEL() { return true; }
  
  protected ELSentenceAskableImpl(ArrayList rep) {
    super(rep);
  }

  public static ELSentenceAskableImpl createELSentenceAskable(ArrayList rep) { 
    try {
      return (ELSentenceAskableImpl)createFormula(rep, 
        Class.forName("ELSentenceAskableImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
}
