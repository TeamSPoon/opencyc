package org.opencyc.elf.bg.command;

//// Internal Imports
import org.opencyc.elf.bg.predicate.PredicateExpression;


//// External Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** LearningEpisodeCommand is a type of command that scopes a reinforcement learning episode.
 * Predicate expressions provide rules for determining the applicable reward for each choice
 * point within the scope.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
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
 * @version $Id$
 */

public class LearningEpisodeCommand implements Command {
  
  //// Constructors
  
  /** Creates a new instance of LearningEpisodeCommand. 
   * 
   * @param name the name of this learning episode command
   * @param commands the commands that constitute the episode
   * @param rewardInfos the list of eligibility expressions and associated reward amounts
   */
  public LearningEpisodeCommand(String name, List commands, List rewardInfos) {
    this.name = name;
    this.commands = commands;
    this.rewardInfos = rewardInfos;
  }
  
  //// Public Area
  
  /** Creates and returns a copy of this object. */
  public Object clone() {
    ArrayList clonedCommands = new ArrayList(commands.size());
    Iterator iter = commands.iterator();
    while (iter.hasNext())
      clonedCommands.add(((Command) iter.next()).clone());
    return new LearningEpisodeCommand(name, clonedCommands, rewardInfos);
  }
  
  /** Gets the name of this learning episode command.
   *
   * @return the name of this learning episode command
   */
  public String getName() {
    return name;
  }
  
  /** Gets the commands that constitute the episode
   *
   * @return the commands that constitute the episode
   */
  public List getCommands () {
    return commands;
  }

  //// Protected Area
  
  /** Contains the reward amount for a given eligibility condition. */
  class RewardInfo {
    
    /** Constructs a new RewardInfo object. 
     *
     * @param eligibilityExpression the reward eligibility expression that when true indicates that the award amount is
     * to be awarded to the choices within scope given the state at the time of the choice
     * @param amount the reward amount
     */
    public RewardInfo(PredicateExpression eligibilityExpression, int amount) {
      this.eligibilityExpression = eligibilityExpression;
      this.amount = amount;
    }
    
    /** Gets the reward eligibility expression that when true indicates that the award amount is
     * to be awarded to the choices within scope, given the state at the time of the choice.
     *
     * @return tthe reward eligibility expression that when true indicates that the award amount is
     * to be awarded to the choices within scope, given the state at the time of the choice
     */
    public PredicateExpression getEligibilityExpression () {
      return eligibilityExpression;
    }
    
    /** Gets the reward amount.
     *
     * @return the reward amount
     */
    public int getAmount () {
      return amount;
    }

    /** the reward eligibility expression that when true indicates that the award amount is
     * to be awarded to the choices within scope, given the state at the time of the choice. */
    protected PredicateExpression eligibilityExpression;
    
    /** the reward amount */
    protected int amount;
  }
  
  //// Private Area
  
  //// Internal Rep
  
  /** the name of this learning episode command */
  protected String name;
  
  /** the commands that constitute the episode */
  protected List commands;
  
  /** the list of eligibility expressions and associated reward amounts */
  protected List rewardInfos;
  
  
  //// Main
  
}
