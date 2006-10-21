package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLReifiableDenotationalTerm as of 2002/05/07:<p>
 *
 * A subcollection of both #$CycLClosedDenotationalTerm and #$CycLIndexedTerm
 * (qq.v.).  #$CycLReifiableDenotationalTerm is the collection of all CycL terms
 * that both may be reified and may denote something in the universe of
 * discourse.  It thus includes all instances of #$CycLConstant as well as any
 * NAT (see #$CycLNonAtomicTerm and #$Function-Denotational) whose functor
 * denotes an instance of #$ReifiableFunction.  For example, the NAT
 * `(#$GovernmentFn #$France)' is a #$CycLReifiableDenotationalTerm, since
 * #$GovernmentFn is an instance of #$ReifiableFunction.  Similarly,
 * `(#$JuvenileFn #$Platypus)' is a #$CycLReifiableDenotationalTerm; although it
 * is not currently reified in the KB, it is reifiable and denotational (see
 * #$CycLClosedDenotationalTerm).  Finally, `(#$BorderBetweenFn #$Canada
 * #$Mexico)' is a #$CycLReifiableDenotationalTerm; although it happens not to
 * denote anything in the universe of discourse, it is nonetheless the kind of
 * NAT that can and often does denote. Note that #$CycLVariables are not
 * considered reifiable terms.<p>
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
public interface CycLReifiableDenotationalTerm 
  extends CycLClosedDenotationalTerm, CycLIndexedTerm {
}
