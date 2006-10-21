package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLAssertion as of 2002/05/07:<p>
 *
 * The collection of semantically well-formed #$CycLSentences asserted to the
 * Cyc Knowledge Base.  Each instance of #$CycLAssertion is either (1) an
 * #$HLAssertion: a Heuristic Level CycL sentence reified in the Cyc Knowledge
 * Base (i.e. a CycL sentence that corresponds to a data structure actually in
 * the Cyc KB), or (2) an #$ELAssertion: an Epistemological Level CycL sentence
 * that can be canonicalized into one or more already extant #$HLAssertions.
 * #$CycLAssertion is used as an argument type constraint for certain
 * meta-predicates, such as #$overrides.<p>
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
public interface CycLAssertion 
  extends CycLPropositionalSentence, CycLIndexedTerm {
}

