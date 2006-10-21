package org.opencyc.cyclobject.subl;

import org.opencyc.cyclobject.*;
import org.opencyc.cyclobject.el.*;
import org.opencyc.cyclobject.hl.*;

/**
 * KB comment for #$SubLString as of 2002/05/07:<p>
 *
 * A subcollection of #$SubLAtomicTerm (q.v.) whose instances can be used to
 * denote character-strings.  In the SubL language (as in English), one can
 * refer to a particular character-string by simply putting a pair of quotation
 * marks around it.  Semantically, what this amounts to is using a certain
 * character-string to denote itself.  More precisely: a given #$SubLString
 * STRING is a character-string such that, when it appears inside a pair of
 * double-quotation marks, STRING is interpreted as denoting itself
 * (i.e. STRING).  The surrounding quotation-marks are _not_ considered to be
 * parts of STRING; rather, they are syncategorematic symbols whose role is
 * merely to indicate that a string a being referred to.  Thus, in the context
 * of the sentence `(#$firstName #$Lenat "Doug")', the four-character string
 * `Doug' denotes itself: the string `Doug'.  Note that _not_ every
 * #$CharacterString is a #$SubLString: there are some restrictions on what
 * characters a SubL-string can contain. Note also that #$SubLStrings are
 * _atomic_ terms (see #$SubLAtomicTerm and #$CycLAtomicTerm), as they cannot be
 * constructed out of other SubL (or CycL) terms via the syntax (grammar) of
 * SubL (or CycL).  Thus although the string `hotdog' can indeed be obtained
 * from `hot' and `dog' via concatenation (see #$ConcatenateStringsFn), the
 * _syntax_ of SubL (or CycL) itself contains no rule that licenses such a
 * construction (see e.g. the comments on #$SubLExpression, #$CycLExpression,
 * #$CycLTerm, #$CycLFormula, and various subcollections thereof).  Note finally
 * that #$SubLString is a "quoted-collection" (see #$quotedCollection).  Thus
 * the sentence `(#$isa "hotdog" #$SubLString)' means, just as one would expect,
 * that the six-character string `hotdog' (not the eight-character
 * quote-inclusive `"hotdog"'; see above) is an instance of #$SubLString.<p>
 * 
 * @version $Id$
 * @author
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
 *      Copyright &copy; 2000 - 2001 Cycorp, Inc.  All rights reserved.
 *****************************************************************************/
public interface SubLString extends SubLAtomicTerm {

  String getString();

}
