package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLExpression-Askable as of 2002/05/07:<p>
 *
 * The collection of #$CycLExpressions that are either themselves askable as
 * queries to the Cyc system (see #$CycLSentence-Askable) or could appear as
 * non-atomic terms within sentences that could be so asked (see
 * #$CycLNonAtomicTerm-Askable).  More precisely, each instance of
 * #$CycLExpression-Askable is a CycL expression that is constructible via the
 * syntax of CycL without violating any applicable arity constraints (see
 * #$arity).  Note that askable CycL expressions do not necesarily obey other
 * semantic constraints beyond arity, such as argument-type constraints (see
 * #$ArgTypePredicate); thus they are not always semantically well-formed in the
 * fullest sense (cf. #$CycLExpression-Assertible).<p>
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
public interface CycLExpressionAskable extends CycLTerm {
}
