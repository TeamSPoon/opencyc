package org.opencyc.elf.bg.procedure;

//// Internal Imports

//// External Imports
import java.util.ArrayList;

/**
 * <P>
 * Statement is the class which provides the common abstraction for Procedures and
 * SpecialForms.
 * </p>
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
public abstract class Statement {
  
  //// Constructors

  //// Public Area

  /**
   * Gets the operator which is an instance of Procedure or SpecialForm
   *
   * @return operator the operator which is an instance of Procedure or SpecialForm
   */
  public Statement getOperator () {
    return operator;
  }

  /**
   * Sets the operator which is an instance of Procedure or SpecialForm
   *
   * @param operator the operator which is an instance of Procedure or SpecialForm
   */
  public void setOperator (Statement operator) {
    this.operator = operator;
  }

  /**
   * Gets the list of operand objects for the operator
   *
   * @return operands the list of operand objects for the operator
   */
  public ArrayList getOperands () {
    return operands;
  }

  /**
   * Sets the list of operand objects for the operator
   *
   * @param operands the list of operand objects for the operator
   */
  public void setOperands (ArrayList operands) {
    this.operands = operands;
  }
  
  //// Protected Area

  //// Private Area
  
  //// Internal Rep

  /**
   * the operator which is an instance of Procedure or SpecialForm
   */
  protected Statement operator;
  
  /**
   * the list of operand objects for the operator
   */
  protected ArrayList operands;
  
  //// Main
  
}
