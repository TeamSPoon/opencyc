package org.opencyc.elf.bg;

import org.opencyc.elf.*;
import org.opencyc.elf.goal.*;
import org.opencyc.uml.core.*;

/**
 * Provides the Planner for ELF BehaviorGeneration.<br>
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

public abstract class Planner extends NodeComponent {

    /**
     * the commanded goal for generated behavior to achieve
     */
    protected Goal commandedGoal;

    /**
     * the behavior generation instance owning this planner
     */
    protected BehaviorGeneration behaviorGeneration;

    /**
     * the executor for this planner
     */
    protected Executor executor;

    /**
     * the generated plan to execute
     */
    protected Procedure procedureToExecute;

    /**
     * Constructs a new Planner object.
     */
    public Planner() {
    }

    /**
     * Gets the goal for generated behavior to achieve
     *
     * @return the goal for generated behavior to achieve
     */
    public Goal getCommandedGoal () {
        return commandedGoal;
    }

    /**
     * Sets the goal for generated behavior to achieve
     *
     * @param commandedGoal the goal for generated behavior to achieve
     */
    public void setCommandedGoal (Goal commandedGoal) {
        this.commandedGoal = commandedGoal;
    }

    /**
     * Gets the behavior generation instance
     *
     * @return the behavior generation instance
     */
    public BehaviorGeneration getBehaviorGeneration () {
        return behaviorGeneration;
    }

    /**
     * Sets the behavior generation instance
     *
     * @param behaviorGeneration the behavior generation instance
     */
    public void setBehaviorGeneration (BehaviorGeneration behaviorGeneration) {
        this.behaviorGeneration = behaviorGeneration;
    }

    /**
     * Gets the executor for this planner
     *
     * @return the executor for this planner
     */
    public Executor getExecutor () {
        return executor;
    }

    /**
     * Sets the executor for this planner
     *
     * @param executor the executor for this planner
     */
    public void setExecutor (Executor executor) {
        this.executor = executor;
    }

    /**
     * Gets the generated plan to execute
     *
     * @return the generated plan to execute
     */
    public Procedure getProcedure () {
        return procedureToExecute;
    }

    /**
     * Sets the generated plan to execute
     *
     * @param procedureToExecute the generated plan to execute
     */
    public void setProcedure (Procedure procedureToExecute) {
        this.procedureToExecute = procedureToExecute;
    }
}