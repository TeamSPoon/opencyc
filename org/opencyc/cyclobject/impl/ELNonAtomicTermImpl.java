package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.el.*;

import java.util.ArrayList;

/*****************************************************************************
 * KB comment for #$ELNonAtomicTerm as of 2002/05/07:<p>
 *
 * The collection of non-atomic denotational terms in the EL language. Each
 * instance of #$ELNonAtomicTerm has as its functor either an instance of
 * #$Function-Denotational or a function-denoting function. Also it optionally
 * has other EL terms as additional arguments. If the functor is an instance of
 * #$ReifiableFunction, an #$ELNonAtomicTerm can be reified, whereupon it
 * becomes an #$HLNonAtomicReifiedTerm, or 'NART'.<p>
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
public class ELNonAtomicTermImpl
  extends CycLNonAtomicTermImpl
  implements ELNonAtomicTerm, ELFormula {
    
  public boolean isEL() { return true; }
   
  protected ELNonAtomicTermImpl(ArrayList rep) {
    initFormula(rep);
  }

  public static ELNonAtomicTermImpl createELNonAtomicTerm(ArrayList rep) { 
    try {
      return (ELNonAtomicTermImpl)createFormula(rep, 
        Class.forName("ELNonAtomicTermImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
}
