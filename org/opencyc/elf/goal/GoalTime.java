package org.opencyc.elf.goal;

//// Internal Imports

//// External Imports

/**
 * GoalTime contains the goal achievement time and tolerance.
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
public class GoalTime {
  
  //// Constructors
  
  /** Creates a new instance of GoalTime. */
  public GoalTime() {
  }
  
  /** 
   * Creates a new instance of GoalTime given the goal achievment time and tolerance.
   *
   * @param goalTimeMilliseconds the goal achievement time in milliseconds
   * @param goalToleranceMilliseconds the goal achievement tolerance in milliseconds
   */
  public GoalTime(long goalTimeMilliseconds, long goalToleranceMilliseconds) {
    this.goalTimeMilliseconds = goalTimeMilliseconds;
    this.goalToleranceMilliseconds = goalToleranceMilliseconds;
  }
  
  //// Public Area
  
  /**
   * Gets the goal achievement time in milliseconds
   *
   * @return the goal achievement time in milliseconds
   */
  public long getGoalTimeMilliseconds () {
    return goalTimeMilliseconds;
  }

  /**
   * Sets the goal achievement time in milliseconds
   *
   * @param goalTimeMilliseconds the goal achievement time in milliseconds
   */
  public void setGoalTimeMilliseconds (long goalTimeMilliseconds) {
    this.goalTimeMilliseconds = goalTimeMilliseconds;
  }

  /**
   * Gets the goal achievement tolerance in milliseconds
   *
   * @return the goal achievement tolerance in milliseconds
   */
  public long getGoalToleranceMilliseconds () {
    return goalToleranceMilliseconds;
  }

  /**
   * Sets the goal achievement tolerance in milliseconds
   *
   * @param goalToleranceMilliseconds the goal achievement tolerance in milliseconds
   */
  public void setGoalToleranceMilliseconds (long goalToleranceMilliseconds) {
    this.goalToleranceMilliseconds = goalToleranceMilliseconds;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(String.valueOf(goalTimeMilliseconds));
    if (goalToleranceMilliseconds != 0) {
      stringBuffer.append(" +/- ");
      stringBuffer.append(String.valueOf(goalToleranceMilliseconds));
    }
    return stringBuffer.toString();
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the goal achievement time in milliseconds
   */
  protected long goalTimeMilliseconds;  
  
  /**
   * the goal achievement tolerance in milliseconds
   */
  protected long goalToleranceMilliseconds = 0;
    
  //// Main
  
}
