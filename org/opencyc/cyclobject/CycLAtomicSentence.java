package org.opencyc.cyclobject;

/*****************************************************************************
 * KB comment for #$CycLAtomicSentence as of 2002/05/07:<p>
 *
 * The collection of syntactically well-formed atomic sentences (also called
 * "atomic logical formulas") in the CycL language. Each instance of
 * #$CycLAtomicSentence consists of a CycL constant denoting a #$Predicate
 * followed by one or more CycL terms, with the entire sequence enclosed in
 * parentheses.  Thus CycL atomic sentences never contain other sentences as
 * truth-functional components (see #$TruthFunction).  Note that "atomic" as
 * used here specifically in connection with _sentences_, has a less strict
 * meaning than it does when applied to terms generally, where it means "not
 * constructable from other terms via CycL syntax" (see #$CycLAtomicTerm).<p>
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
public interface CycLAtomicSentence extends CycLSentence {
}
