package org.opencyc.elf.bg;

import org.opencyc.elf.*;
import org.opencyc.elf.goal.*;

/**
 * Provides ELF Behavior Generation to accomplish goals.<br>
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

public class BehaviorGenerationForGoal extends BehaviorGeneration {

    /**
     * the commanded goal for generated behavior to achieve
     */
    protected Goal commandedGoal;

    /**
     * Constructs a new BehaviorGenerationForGoal object.
     */
    public BehaviorGenerationForGoal() {
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
}