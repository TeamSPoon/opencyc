package org.opencyc.uml.statemachine;

import java.util.ArrayList;
import org.opencyc.uml.action.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;

/**
 * Transition from the UML State_Machines package.
 *
 * The passing from one state to another is performed when a transition
 * is triggered by an event that occurs.
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

public class Transition extends ModelElement {

    /**
     * the guard for this transition
     */
    protected Guard guard;

    /**
     * the effect of this transition
     */
    protected Procedure effect;

    /**
     * (extension) the input pin bindings
     */
    protected ArrayList effectInputBinding;

    /**
     * the input pin bindings for the transition effect
     */
    protected ArrayList effectInputBindings;

    /**
     * the output pin bindings for the transition effect
     */
    protected ArrayList effectOutputBindings;

    /**
     * the event which triggered this transition
     */
    protected Event trigger;

    /**
     * the source state of this transition
     */
    protected StateVertex source;

    /**
     * the target state of this transition
     */
    protected StateVertex target;

    /**
     * the state machine containing this transition
     */
    protected StateMachine stateMachine;

    /**
     * Constructs a new Transition object.
     */
    public Transition() {
    }

    /**
     * Gets the guard for this transition
     *
     * @return the guard for this transition
     */
    public Guard getGuard () {
        return guard;
    }

    /**
     * Sets the guard for this transition
     *
     * @param guard the guard for this transition
     */
    public void setGuard (Guard guard) {
        this.guard = guard;
    }

    /**
     * Gets the effect of this transition
     *
     * @return the effect of this transition
     */
    public Procedure getEffect () {
        return effect;
    }

    /**
     * Sets the effect of this transition
     *
     * @param effect the effect of this transition
     */
    public void setEffect (Procedure effect) {
        this.effect = effect;
    }

    /**
     * Gets the input pin bindings for the transition effect
     *
     * @return the input pin bindings for the transition effect
     */
    public ArrayList getEffectInputBindings () {
        return effectInputBindings;
    }

    /**
     * Adds an input pin binding for the transition effect
     *
     * @param inputPin the given input pin
     * @param boundObject the object bound to the given input pin
     * @return the input pin bindings for the transition effect
     */
    public void addEffectInputBinding (InputPin inputPin, Object boundObject) {
        InputBinding inputBinding =
            new InputBinding(inputPin, boundObject);
        if (effectInputBindings == null)
            effectInputBindings = new ArrayList();
        effectInputBindings.add(inputBinding);
    }

    /**
     * Sets the input pin bindings for the transition effect
     *
     * @param effectInputBindings the input pin bindings for the transition effect
     */
    public void setEffectInputBindings (ArrayList effectInputBindings) {
        this.effectInputBindings = effectInputBindings;
    }

    /**
     * Gets the output pin bindings for the transition effect
     *
     * @return the output pin bindings for the transition effect
     */
    public ArrayList getEffectOutputBindings () {
        return effectOutputBindings;
    }

    /**
     * Adds an output pin binding for the transition effect
     *
     * @param outputPin the given output pin
     * @param stateVariable the state variable bound to the given output pin
     * @return the output pin bindings for the transition effect
     */
    public void addEffectOutputBinding (OutputPin outputPin, StateVariable stateVariable) {
        OutputBinding outputBinding =
            new OutputBinding(outputPin, stateVariable);
        if (effectOutputBindings == null)
            effectOutputBindings = new ArrayList();
        effectOutputBindings.add(outputBinding);
    }

    /**
     * Sets the output pin bindings for the transition effect
     *
     * @param effectOutputBindings the output pin bindings for the transition effect
     */
    public void setEffectOutputBindings (ArrayList effectOutputBindings) {
        this.effectOutputBindings = effectOutputBindings;
    }

    /**
     * Gets the event which triggered this transition
     *
     * @return the event which triggered this transition
     */
    public Event getTrigger () {
        return trigger;
    }

    /**
     * Sets the event which triggered this transition
     *
     * @param trigger the event which triggered this transition
     */
    public void setTrigger (Event trigger) {
        this.trigger = trigger;
    }

    /**
     * Gets the source state of this transition
     *
     * @return the source state of this transition
     */
    public StateVertex getSource () {
        return source;
    }

    /**
     * Sets source state of this transition
     *
     * @param sthe ource source state of this transition
     */
    public void setSource (StateVertex source) {
        this.source = source;
    }

    /**
     * Gets the target state of this transition
     *
     * @return the target state of this transition
     */
    public StateVertex getTarget () {
        return target;
    }

    /**
     * Sets the target state of this transition
     *
     * @param target the target state of this transition
     */
    public void setTarget (StateVertex target) {
        this.target = target;
    }

    /**
     * Gets the state machine containing this transition
     *
     * @return the state machine containing this transition
     */
    public StateMachine getStateMachine () {
        return stateMachine;
    }

    /**
     * Sets the state machine containing this transition
     *
     * @param xxxx the state machine containing this transition
     */
    public void setStateMachine (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Gets whether this is a self transition, meaning that the source
     * and target states are the same state.
     *
     * @return  whether this is a self transition, meaning that the source
     * and target states are the same state
     */
    public boolean isSelfTransition () {
        return source.equals(target);
    }
}