package org.opencyc.elf.goal;

//// Internal Imports

import org.opencyc.elf.bg.state.State;

//// External Imports

/**
 * <P>GoalTime contains the timing constraint on achieving the goal plus modifiers such as tolerance
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
 *
 * @author reed
 * @date August 11, 2003, 3:52 PM
 * @version $Id$
 */
public class GoalTime {
  
  //// Constructors
  
  /** Creates a new instance of GoalTime. */
  public GoalTime() {
  }
  
  //// Public Area
  
  /**
   * Gets the goal time constaint in milliseconds
   *
   * @return the goal time constaint in milliseconds
   */
  public long getTimeMillis () {
    return ((Long) state.getStateValue("goalTimeConstaintMillis")).longValue();
  }

  /**
   * Sets the goal time constaint in milliseconds
   *
   * @param timeMillis the goal time constaint in milliseconds
   */
  public void setTimeMillis (long timeMillis) {
    state.setStateValue("goalTimeConstaintMillis", new Long(timeMillis));
  }
  
  //// Protected Area
  
  /**
   * the state of the goal time constraint
   */
  protected State state;
  
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
