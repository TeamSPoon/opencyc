package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.el.*;

import java.util.ArrayList;

/*****************************************************************************
 * KB comment for #$ELAssertion as of 2002/05/07:<p>
 *
 * The collection of #$ELSentences that are asserted to the Cyc Knowledge Base.
 * When an EL assertion is presented to the Knowledge Base, the
 * #$CycCanonicalizer "transforms" it into an #$HLAssertion.<p>
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
public class ELAssertionImpl extends ELSentenceAssertibleImpl
  implements ELAssertion, CycLAssertion {

  public Object assertionGuts = null;

  public boolean isEL() { return true; }

  public String toString() { return assertionGuts.toString(); }

  public String cyclify() { return assertionGuts.toString(); }

  protected ELAssertionImpl(ArrayList rep) {
    super(rep);
  }

  public static ELAssertion createELAssertion(ArrayList rep) { 
    try {
      return (ELAssertion)createFormula(rep, Class.forName("ELAssertionImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
    
}
