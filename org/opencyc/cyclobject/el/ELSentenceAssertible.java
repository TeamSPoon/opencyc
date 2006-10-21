package org.opencyc.cyclobject.el;

import org.opencyc.cyclobject.CycLSentenceAssertible;

/*****************************************************************************
 * KB comment for #$ELSentence-Assertible as of 2002/05/07:<p>
 *
 * The collection of syntactically and semantically well-formed #$ELSentences.
 * Each instance of #$ELSentence-Assertible meets the necessary criteria for
 * being asserted into the Cyc Knowledge Base (at which time it is "transformed"
 * into one or more #$HLAssertions by the #$CycCanonicalizer).  Of course, a
 * sentence's being assertible does not entail that it is actually asserted, but
 * only that it could be asserted.  An assertible EL sentence consists of an EL
 * expression denoting a logical relation (i.e. a #$Predicate or
 * #$TruthFunction) followed by an appropriate number of EL terms (in accordance
 * with the #$arity of the logical relation), with the entire sequence enclosed
 * in parentheses.  Example: (#$and (#$isa #$Pittman #$HumanCyclist)
 * (#$residesInRegion #$Pittman #$CityOfAustinTX)). For a thorough discussion of
 * what constitutes a well-formed CycL formula, see the Cyc documentation.<p>
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
public interface ELSentenceAssertible 
  extends CycLSentenceAssertible, ELSentenceAskable, ELExpressionAssertible {
}
