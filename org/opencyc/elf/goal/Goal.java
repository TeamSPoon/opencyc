package org.opencyc.elf.goal;


import java.util.*;
import org.opencyc.elf.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.statemachine.*;

/**
 * Provides the Goal container for the Elementary Loop Functioning (ELF).<br>
 *
 * @version $Id$
 * @author Stephen L. Reed
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
 */

public class Goal extends ELFObject {

    /**
     * the goal state
     */
    protected State goalState;

    /**
     * the goal sentence stated in CycL
     */
    protected CycAssertion goalSentence;

//think about parameterized goals (procedure signatures)

//add list of goal failure states.



    /**
     * the parent goal of this goal
     */
    protected Goal parent;

    /**
     * the subgoals of this goal
     */
    protected ArrayList subGoals = new ArrayList();

    /**
     * the goal importance
     */
    protected Importance importance;

    /**
     * the context of the goal
     */
    protected CycFort context;

    /**
     * Constructs a new Goal object.
     */
    public Goal () {
    }

    /**
     * Gets the goal state
     *
     * @return the goal state
     */
    public State getGoalState () {
        return goalState;
    }

    /**
     * Sets the goal state
     *
     * @param goalState the goal state
     */
    public void setGoalState (State goalState) {
        this.goalState = goalState;
    }

    /**
     * Gets the goal sentence stated in CycL
     *
     * @return the goal sentence stated in CycL
     */
    public CycAssertion getGoalSentence () {
        return goalSentence;
    }

    /**
     * Sets the goal sentence stated in CycL
     *
     * @param goalSentence the goal sentence stated in CycL
     */
    public void setGoalSentence (CycAssertion goalSentence) {
        this.goalSentence = goalSentence;
    }

    /**
     * Gets the parent goal of this goal
     *
     * @return the parent goal of this goal
     */
    public Goal getParent () {
        return parent;
    }

    /**
     * Sets the parent goal of this goal
     *
     * @param parent the parent goal of this goal
     */
    public void setParent (Goal parent) {
        this.parent = parent;
    }

    /**
     * Gets the subgoals of this goal
     *
     * @return the subgoals of this goal
     */
    public ArrayList getSubGoals () {
        return subGoals;
    }

    /**
     * Sets the subgoals of this goal
     *
     * @param subGoals the subgoals of this goal
     */
    public void setSubGoals (ArrayList subGoals) {
        this.subGoals =subGoals;
    }

    /**
     * Gets the goal importance
     *
     * @return the goal importance
     */
    public Importance getImportance () {
        return importance;
    }

    /**
     * Sets the goal importance
     *
     * @param importance the goal importance
     */
    public void setImportance (Importance importance) {
        this.importance = importance;
    }

    /**
     * Gets the context of the goal
     *
     * @return the context of the goal
     */
    public CycFort getContext () {
        return context;
    }

    /**
     * Sets the context of the goal
     *
     * @param context the context of the goal
     */
    public void setContext (CycFort context) {
        this.context = context;
    }
}