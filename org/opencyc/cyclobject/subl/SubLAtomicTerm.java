package org.opencyc.cyclobject.subl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.el.*;
import org.opencyc.cyclobject.hl.*;


/**
 * KB comment for #$SubLAtomicTerm as of 2002/05/07:<p>
 *
 * The collection of all atomic denotational terms in the CycL
 * language that are not explicitly represented in CycL
 * (i.e. they are neither #$CycLConstants nor #$CycLVariables)
 * but are represented in SubL, the underlying implementation
 * language of the Cyc system.  "Atomic" here means not
 * constructable from other terms via the SubL syntax.  Examples
 * include the terms `212', `:NOUN', `#x', and `VARIABLE-P'. Do
 * not confuse this collection with #$SubLAtom, most of whose
 * instances, while they are expressions of SubL, are _not_ part
 * of CycL.  Note that this collection, like most instances of
 * #$CycLExpressionType, is "quoted" (see #$quotedCollection).<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2002 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
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
 */

public interface SubLAtomicTerm 
  extends HLExpression, ELExpression, CycLClosedAtomicTerm {
  public Object getValue();
}
