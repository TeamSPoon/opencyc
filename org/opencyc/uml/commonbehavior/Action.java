package org.opencyc.uml.commonbehavior;

import java.util.*;
import org.opencyc.uml.core.*;

/**
 * Action from the UML Common_Behavior package.
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

public class Action extends ModelElement {

    /**
     * recurrence of this action
     */
    protected IterationExpression recurrence;

    /**
     * target of this action's result
     */
    protected ObjectSetExpression target;

    /**
     * asynchronous action indicator
     */
    protected boolean isAsychronous;

    /**
     * this action's script
     */
    protected ActionExpression script;

    /**
     * arguments for this action
     */
    protected ArrayList actualArgument = new ArrayList();

    /**
     * action sequence for this action
     */
    protected ActionSequence actionSequence;

    /**
     * Constructs a new Action object.
     */
    public Action() {
    }

    /**
     * Gets the recurrence of this action.
     *
     * @return the recurrence of this action
     */
    public IterationExpression getRecurrence() {
        return recurrence;
    }

    /**
     * Sets the recurrence of this action.
     *
     * @param recurrence the recurrence of this action
     */
    public void setRecurrence (IterationExpression recurrence) {
        this.recurrence = recurrence;
    }

    /**
     * Gets the target of this action's result
     *
     * @return the target of this action's result
     */
    public ObjectSetExpression getTarget () {
        return target;
    }

    /**
     * Sets the target of this action's result
     *
     * @param target the target of this action's result
     */
    public void setTarget (ObjectSetExpression target) {
        this.target = target;
    }

    /**
     * Gets the asynchronous action indicator
     *
     * @return the asynchronous action indicator
     */
    public boolean isAsychronous () {
        return isAsychronous;
    }

    /**
     * Sets the asynchronous action indicator
     *
     * @param isAsychronous the asynchronous action indicator
     */
    public void setIsAsychronous (boolean isAsychronous) {
        this.isAsychronous = isAsychronous;
    }

    /**
     * Gets this action's script
     *
     * @return this action's script
     */
    public ActionExpression getScript () {
        return script;
    }

    /**
     * Sets this action's script
     *
     * @param script this action's script
     */
    public void setScript (ActionExpression script) {
        this.script = script;
    }

    /**
     * Gets the arguments for this action
     *
     * @return the arguments for this action
     */
    public ArrayList getActualArgument () {
        return actualArgument;
    }

    /**
     * Sets the arguments for this action
     *
     * @param actualArgument the arguments for this action
     */
    public void setActualArgument (ArrayList actualArgument) {
        this.actualArgument = actualArgument;
    }
    /**
     * Gets the action sequence for this action
     *
     * @return the action sequence for this action
     */
    public ActionSequence getActionSequence () {
        return actionSequence;
    }

    /**
     * Sets the action sequence for this action
     *
     * @param actionSequence the action sequence for this action
     */
    public void setActionSequence (ActionSequence actionSequence) {
        this.actionSequence = actionSequence;
    }

}