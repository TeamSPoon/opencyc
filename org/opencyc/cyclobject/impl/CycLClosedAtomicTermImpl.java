package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.CycLClosedAtomicTerm;
import org.opencyc.cyclobject.CycLClosedDenotationalTerm;

/**
 * KB comment for #$CycLClosedAtomicTerm as of 2002/05/07:<p>
 *
 * The collection of all closed #$CycLAtomicTerms.  "Closed" here means not
 * containing any free (i.e. unbound) variables.  Since a variable itself is the
 * only type of _atomic_ term that contains a variable (and contains it _free_,
 * moreover, as a variable cannot bind itself), #$CycLClosedAtomicTerm has as
 * instances all #$CycLAtomicTerms except #$CycLVariables.  Like all CycL atomic
 * terms, Cycl closed atomic terms are "denotational" (see
 * #$CycLDenotationalTerm).  Note that this collection, like most instances of
 * #$CycLExpressionType, is "quoted" (see #$quotedCollection).<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sf.net/projects/opencyc">OpenCyc at SourceForge</a>
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
public abstract class CycLClosedAtomicTermImpl 
  extends CycLAtomicTermImpl
  implements CycLClosedAtomicTerm, CycLClosedDenotationalTerm
{

  protected Object value = null;

  public boolean isAtomic() { return true; }

  public boolean isLiteral() { return true; }

  public Object getValue() { return value; }

  public String toString() { return value.toString(); }

  public String cyclify() { return value.toString(); }

}
