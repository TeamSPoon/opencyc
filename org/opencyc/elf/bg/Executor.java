package org.opencyc.elf.bg;

import org.opencyc.elf.*;
import org.opencyc.uml.core.*;

/**
 * Provides the Executor for ELF BehaviorGeneration.<br>
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

public class Executor extends NodeComponent {

    /**
     * the procedure to execute
     */
    protected Procedure procedure;

    /**
     * the behavior generation instance which owns this executor
     */
    protected BehaviorGeneration behaviorGeneration;

    /**
     * the planner whose plans this executor executes
     */
    protected Planner planner;

    /**
     * Constructs a new Executor object.
     */
    public Executor() {
    }

    /**
     * Gets the procedure to execute
     *
     * @return the procedure to execute
     */
    public Procedure getProcedure () {
        return procedure;
    }

    /**
     * Sets the procedure to execute
     *
     * @param procedure the procedure to execute
     */
    public void setProcedure (Procedure procedure) {
        this.procedure = procedure;
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
     * Gets the planner whose plans this executor executes
     *
     * @return the planner whose plans this executor executes
     */
    public Planner getPlanner () {
        return planner;
    }

    /**
     * Sets the planner whose plans this executor executes
     *
     * @param planner the planner whose plans this executor executes
     */
    public void setPlanner (Planner planner) {
        this.planner = planner;
    }

}
