package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLClosedDenotationalTerm as of 2002/05/07:<p>
 *
 * The collection of all closed denotational terms in the CycL language.  An
 * expression is said to be "closed" if it contains no free variables (see
 * #$CycLClosedExpression).  A CycL term is said to be "denotational" if it is
 * the right sort of term to have a denotation (or value) in the universe of
 * discourse (see #$CycLDenotationalTerm).  CycL sentences, while terms of CycL,
 * are not considered denotational terms.  Each instance of
 * #$CycLClosedDenotationalTerm is either a #$CycLClosedAtomicTerm (i.e. a
 * #$CycLConstant or #$SubLAtomicTerm) or a #$CycLClosedNonAtomicTerm (i.e. a
 * "NAT" with no free variables).  Examples of closed denotational terms include
 * `#$Muffet', `(#$JuvenileFn #$Dog)', `(#$TheSetOf ?X (#$objectHasColor ?X
 * GreenColor))', and `212'.  Note that these are also examples:
 * `(#$BorderBetweenFn #$Canada #$Mexico)' (despite the fact that it fails
 * actually to denote anything) and `(#$JuvenileFn #$isa #$genls #$JuvenileFn)'
 * (despite the fact that it is not semantically well-formed).<p>
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
public interface CycLClosedDenotationalTerm 
  extends CycLDenotationalTerm, CycLClosedExpression {}
