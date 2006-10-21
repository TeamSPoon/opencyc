package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.CycLOpenDenotationalTerm;
import org.opencyc.cyclobject.CycLVariable;

/*****************************************************************************
 * KB comment for #$CycLVariable as of 2002/05/07:<p>
 *
 * The collection of all variables in the CycL language.  A #$CycLVariable is a
 * character string consisting of a question mark `?' (as its initial character)
 * followed by one or more characters, where each of these latter characters is
 * either an (upper- or lower-case) Roman letter, an Arabic numeral (i.e. `0'
 * through `9'), a hyphen (`-'), an underscore (`_'), or another question mark.
 * (The letters occurring in a CycL variable used in an actual assertion will
 * typically be all uppercase; but this convention is not enforced in any formal
 * way.)  Examples: `?WHAT', '?OBJ-TYPE', and `?var0'.<p>
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
public abstract class CycLVariableImpl 
  extends CycLRepresentedAtomicTermImpl
  implements CycLVariable, CycLOpenDenotationalTerm {

  public boolean isVariable() { return true; }

}
