package org.opencyc.cyclobject.impl;

import java.io.Serializable;

import org.apache.oro.util.CacheLRU;
import org.opencyc.cyclobject.CycLTerm;

/*****************************************************************************
 * KB comment for #$CycLTerm as of 2002/05/07:<p>
 *
 * The collection of all syntactically well-formed expressions in the CycL
 * language that can be used as terms, i.e. that can be combined with other
 * expressions to form non-atomic terms or formulas.  Since the grammar of the
 * CycL language allows any CycL expression to be used as a term, #$CycLTerm and
 * #$CycLExpression are coextensional collections. Note that, as with most
 * #$CycLExpressionTypes, #$CycLTerm, is a #$quotedCollection (q.v.).<p>
 *
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
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
 *****************************************************************************/
public abstract class CycLTermImpl implements CycLTerm, Serializable {
  
  static CacheLRU termCache = new CacheLRU(20000);

  public boolean isOpen() { return false; }
  public boolean isAtom() { return false; }
  public boolean isAtomic() { return false; }
  public boolean isFormula() { return false; }
  public boolean isSentence() { return false; }
  public boolean isDenotational() { return false; }
  public boolean isConstant() { return false; }
  public boolean isLiteral() { return false; }
  public boolean isCharacterLiteral() { return false; }
  public boolean isStringLiteral() { return false; }
  public boolean isRealNumberLiteral() { return false; }
  public boolean isSymbolLiteral() { return false; }
  public boolean isAssertion() { return false; }
  public boolean isIndexed() { return false; }
  public boolean isReified() { return false; }
  public boolean isReifiable() { return false; }
  public boolean isRepresented() { return false; }
  public boolean isAskable() { return false; }
  public boolean isAssertible() { return false; }
  public boolean isVariable() { return false; }
  public boolean isGAF() { return (!isOpen()) && isAtomic() && isFormula(); }
  public boolean isEL() { return false; }
  public boolean isHL() { return false; }

  abstract public String cyclify();

  abstract public String toString();

  static protected CycLTerm loadFromCache(String key) {
    synchronized(termCache) {
      return (CycLTerm)termCache.getElement(key);
    }
  }

  static protected void saveToCache(String key, CycLTerm val) {
    synchronized(termCache) {
      termCache.addElement(key, val);
    }
  }
  
}
