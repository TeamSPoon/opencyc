package org.opencyc.elf.a;

import org.opencyc.uml.core.*;
import org.opencyc.uml.interpreter.*;
import org.opencyc.elf.*;

/**
 * Provides Actuators for the Elementary Loop Functioning (ELF).<br>
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

public class Actuator extends NodeComponent {

    /**
     * the commanded action
     */
    protected Procedure commandedAction;

    /**
     * the procedure interpreter
     */
    protected ProcedureInterpreter procedureInterpreter;

   /**
     * Constructs a new Actuator object.
     */
    public Actuator() {
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "Actuator for " + node.getName();
    }

    /**
     * Gets the commanded action
     *
     * @return the commanded action
     */
    public Procedure getCommandedAction () {
        return commandedAction;
    }

    /**
     * Sets the commanded action
     *
     * @param commandedAction the commanded action
     */
    public void setCommandedAction (Procedure commandedAction) {
        this.commandedAction = commandedAction;
    }

    /**
     * Gets the procedure interpreter
     *
     * @return the procedure interpreter
     */
    public ProcedureInterpreter getProcedureInterpreter () {
        return procedureInterpreter;
    }

    /**
     * Sets the procedure interpreter
     *
     * @param procedureInterpreter the procedure interpreter
     */
    public void setProcedureInterpreter (ProcedureInterpreter procedureInterpreter) {
        this.procedureInterpreter = procedureInterpreter;
    }
}