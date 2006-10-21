package org.opencyc.cyclobject.impl;

import java.io.IOException;

import org.opencyc.cyclobject.subl.SubLCharacter;
import org.opencyc.xml.XMLWriter;

/*****************************************************************************
 * KB comment for #$SubLCharacter as of 2002/05/07:<p>
 *
 * The collection of all character-denoting terms in the CycL language that are
 * #$SubLAtomicTerms (q.v.). (SubL, which subsumes CycL, is the underlying
 * implementation language of the Cyc system.)  A given #$SubLCharacter
 * CHAR-TERM is itself a string consisting of the hash-symbol (`#'), followed by
 * the backslash (`\'), followed (in most cases) by the character CHAR that
 * CHAR-TERM denotes.  For example, the SubLCharacter `#\A' denotes the
 * character `A'.  (An exception to the above is when CHAR is a non-printing or
 * control character, in which case a specially-designated string appears after
 * the `#\'.)<p>
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
public class SubLCharacterImpl extends SubLAtomicTermImpl
  implements SubLCharacter {

  public boolean isCharacterLiteral() { return true; }

  public Character getCharacter() { return (Character)getValue(); }

  public char getChar() { return ((Character)getValue()).charValue(); }

  public String toString() { return "'" + getValue() + "'"; }

  public String cyclify() { return "'" + getValue() + "'"; }

  public String toXMLString() throws IOException { return ""; }

  public void toXML(XMLWriter xmlWriter, int indent, boolean relative) 
    throws IOException {
    return;
  }
  
  public boolean equals(Object object) { return true; }

  public int hashCode() { return 0; } 
 
  protected SubLCharacterImpl(Object val) { 
    if(!(val instanceof Character)) { throw new IllegalArgumentException(); } 
    setValue(val);
  }

  public static SubLCharacterImpl createSubLCharacter(Character character) { 
    try {
      return (SubLCharacterImpl)getSubLAtomicTerm(character, 
        Class.forName("SubLCharacterImpl"));
    } catch (Exception e) { e.printStackTrace(); } //can't happen
    return null; //will never get here
  }
}
