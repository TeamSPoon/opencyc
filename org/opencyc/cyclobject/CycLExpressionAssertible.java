package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLExpression-Assertible as of 2002/05/07:<p>
 *
 * A #$CycLExpressionType.  The collection of all compound CycL expressions that
 * either could themselves be asserted to the Cyc Knowledge Base (see
 * #$CycLSentence-Assertible) or could appear as non-atomic terms within
 * sentences that could be so asserted (see #$CycLNonAtomicTerm-Assertible).
 * More precisely, each instance of #$CycLExpression-Assertible is a CycL
 * expression that is both syntactically and semantically well-formed.  By
 * definition, any compound CycL expression is syntactically well-formed.  To be
 * semantically well-formed, a CycL expression must be constructible via the
 * syntax of CycL without violating any applicable arity or argument-type
 * constraints (see #$arity and #$ArgTypePredicate).  A CycL formula must be
 * semantically well-formed in order to be interpretable as having a "semantic
 * value", such as a truth-value (if the formula is a sentence) or a denotation
 * (if it's a #$CycLDenotationalTerm).  Note that being "assertible" in the
 * present sense does not require an expression's actually being asserted in (or
 * being a component of something asserted in) the KB.<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steven Reed
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
 *      Copyright &copy; 2000 - 2001 Cycorp, Inc.  All rights reserved.
 *****************************************************************************/
public interface CycLExpressionAssertible extends CycLExpressionAskable {
}
