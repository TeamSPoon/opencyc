package org.opencyc.elf.bg.procedure.specialform;

//// Internal Imports
import org.opencyc.elf.bg.procedure.Statement;

//// External Imports

/**
 * <P>
 * SpecialForm is the class which provides the common abstraction for built-in statements
 * which have special handling (non-evaluation) of some or all of their arguments.  Each
 * subclass implements the method that provides its special behavior.  So that behavior
 * is aligned with the Procedure instances, each subclass of special form has a 
 * singleton instance that is its recipie.</p>
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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
 */
public abstract class SpecialForm extends Statement {

  //// Constructors

  //// Public Area
  
  /**
   * Makes the singleton instance of this special form.
   */
  public abstract void makeInstance ();
  
  /**
   * Gets the singleton instance of this special form.
   *
   * @return the singleton instance of this special form
   */
  public SpecialForm getInstance () {
    return specialForm;
  }
  
  //// Protected Area

  //// Private Area
  
  //// Internal Rep

  /**
   * the singleton instance of this special form
   */
  protected static SpecialForm specialForm;
  
  //// Main
  
}
