package org.opencyc.elf.wm.state;

//// Internal Imports

//// External Imports
import java.util.Iterator;

/**
 * Situation is designed to contain a situation described by a list of
 * stateVariable/values.
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
public class Situation {
  //// Constructors

  /**Creates a new instance of Situation. */
  public Situation() {
    state = new State();
  }

  /**
   * Creates a new instance of Situation given an existing situation.
   * 
   * @param situation the given situation
   */
  public Situation(Situation situation) {
    state = (State) situation.state.clone();
  }

  //// Public Area

  /**
   * Returns true if the given object equals this situation.
   * 
   * @param obj the given object
   * 
   * @return true if the given object equals this situation
   */
  public boolean equals(Object obj) {
    if (obj instanceof Situation) {
      return state.equals(((Situation) obj).getState());
    }
    else {
      return false;
    }
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[Situation:\n");
    stringBuffer.append(state.toString());
    stringBuffer.append("]");

    return stringBuffer.toString();
  }

  /**
   * Return the situation state.
   * 
   * @return the situation state
   */
  public State getState() {
    return state;
  }

  /** the state that constitutes this situation */
  protected State state;

  //// Main
}