package org.opencyc.uml.statemachine;

import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.action.*;

/**
 * Provides a factory for creating UML State_Machines objects.
 * <p>
 * Make objects in this order:<br>
 * 1. state machine<br>
 * 2. procedures<br>
 * 3. events<br>
 * 4. states<br>
 * 5. state vertices<br>
 * 6. transistions<p>
 *
 * The context object for the state machine contains the all the variables
 * used in guards, actions, procedures and events.  The context object also
 * implements the operations performed by the state machine.
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

public class StateMachineFactory {

    /**
     * the namespace for this state machine and its components
     */
    protected Namespace namespace;

    /**
     * the state machine being assembled
     */
    protected StateMachine stateMachine;

    /**
     * Constructs a new StateMachineFactory object.
     */
    public StateMachineFactory() {
    }

    /**
     * Makes a new state machine object.  All further objects defined for this state
     * machine will linked to the given namespace.
     *
     * @param namespaceName the state machine namespace name
     * @param name the identifier for the state machine within its containing
     * namespace
     * @param commentString the comment for this state machine
     * @param context the context Classifier of this state machine, which contains the
     * variables that distinguish the state, and the operations which
     * can be performed during state transitions.
     * @return the new state machine object
     */
    public StateMachine makeStateMachine(String namespaceName,
                                         String name,
                                         String commentString,
                                         Object context) {
        stateMachine = new StateMachine();
        namespace = new Namespace(namespaceName);
        setNamespaceNameComment(stateMachine,
                                name,
                                commentString);
        stateMachine.setContext(context);
        return stateMachine;
    }

    /**
     * Makes a new procedure object.
     *
     * @param name the identifier for the procedure within its containing
     * namespace
     * @param commentString the comment for this procedure
     * @param language the name of the language in which the body attribute is written
     * @param body the text of the procedure written in the given language
     * @param isList true when the arguments to the procedure are passed as
     * attributes of a single object, or false when passed separately
     * @param expression the text of the procedure written in the given language
     * @param method a method which is performed by the procedure
     * @return the new procedure object
     */
    public Procedure makeProcedure (String name,
                                    String commentString,
                                    String language,
                                    String body,
                                    boolean isList) {
        Procedure procedure = new Procedure();
        setNamespaceNameComment(procedure,
                                name,
                                commentString);
        procedure.setLanguage(language);
        procedure.setBody(body);
        procedure.setIsList(isList);
        return procedure;
    }

    /**
     * Adds an input pin (variable) to the given procedure.
     *
     * @param name the name of the input pin (variable)
     * @param commentString the comment for this input pin
     * @param procedure the given procedure to which the input pin is to
     * be added
     * @param type the Class of the input pin (variable)
     */
    public void addInputPinToProcedure(String name,
                                       String commentString,
                                       Procedure procedure,
                                       Class type) {
        InputPin inputPin = new InputPin();
        setNamespaceNameComment(inputPin,
                                name,
                                commentString);
        inputPin.setType(type);
        inputPin.setProcedure(procedure);
        procedure.getArgument().add(inputPin);
    }

    /**
     * Adds an output pin (variable) to the given procedure.
     *
     * @param name the name of the output pin (variable)
     * @param commentString the comment for this output pin
     * @param procedure the given procedure to which the output pin is to
     * be added
     * @param type the Class of the output pin (variable)
     */
    public void addOutputPinToProcedure(String name,
                                        String commentString,
                                        Procedure procedure,
                                        Class type) {
        OutputPin outputPin = new OutputPin();
        setNamespaceNameComment(outputPin,
                                name,
                                commentString);
        outputPin.setType(type);
        outputPin.setProcedure(procedure);
        procedure.getResult().add(inputPin);
    }

    /**
     * Makes a new Event object.
     *
     * @param name the identifier for the event within its containing
     * namespace
     * @param commentString the comment for this event
     * @return the new event object
     */
    public Event makeEvent (String name,
                            String commentString) {
        Event event = new Event();
        setNamespaceNameComment(event,
                                name,
                                commentString);
        return event;
    }

    /**
     * Makes a new CallEvent object.
     *
     * @param name the identifier for the call event within its containing
     * namespace
     * @param commentString the comment for this call event
     * @param specification the specification of the operation called by this event
     * @return the new call event object
     */
    public CallEvent makeCallEvent (String name,
                                    String commentString,
                                    String specification) {
        CallEvent callEvent = new CallEvent();
        setNamespaceNameComment(callEvent,
                                name,
                                commentString);
        Operation operation = new Operation();
        setNamespaceNameComment(operation,
                                name,
                                commentString);
        operation.setSpecification(specification);
        callEvent.setOperation(operation);
        return callEvent;
    }

    /**
     * Makes a new ChangeEvent object.
     *
     * @param name the identifier for the change event within its containing
     * namespace
     * @param commentString the comment for this change event
     * @param language the language of the boolean change expression
     * @param body the body of the boolean change expression
     * @return the new change event object
     */
    public ChangeEvent makeChangeEvent (String name,
                                        String commentString,
                                        String language,
                                        String body) {
        ChangeEvent changeEvent = new ChangeEvent();
        setNamespaceNameComment(changeEvent,
                                name,
                                commentString);
        BooleanExpression changeExpression = new BooleanExpression();
        setNamespaceNameComment(changeExpression,
                                name,
                                commentString);
        changeExpression.setLanguage(language);
        changeExpression.setBody(body);
        changeEvent.setChangeExpression(changeExpression);
        return changeEvent;
    }

    /**
     * Makes a new CompletionEvent object.
     *
     * @param name the identifier for the completion event within its containing
     * namespace
     * @param commentString the comment for this completion event
     * @param state the state issuing this completion expression
     * @return the new completion event object
     */
    public CompletionEvent makeCompletionEvent (String name,
                                                String commentString,
                                                State state) {
        CompletionEvent completionEvent = new CompletionEvent();
        setNamespaceNameComment(completionEvent,
                                name,
                                commentString);
        completionEvent.setState(state);
        return completionEvent;
    }

    /**
     * Adds a parameter to the given event.
     *
     * @param name the name of parameter
     * @param commentString the comment for this parameter
     * @param event the given event to which the parameter is to
     * be added
     * @param type the Class of the parameter
     */
    public void addParameterToEvent(String name,
                                    String commentString,
                                    Event event,
                                    Class type) {
        Parameter parameter = new Parameter();
        setNamespaceNameComment(parameter,
                                name,
                                commentString);
        parameter.setBehavioralFeature(Event);
        parameter.setType(type);
    }

    /**
     * Makes a new state vertex object.
     *
     * @param name the identifier for the state vertex within its containing
     * namespace
     * @param commentString the comment for this state vertex
     * @param container the container of this state vertex
     * @return the new state vertex object
     */
    public StateVertex makeStateVertex (String name,
                                        String commentString,
                                        CompositeState container) {
        StateVertex stateVertex = new StateVertex();
        setNamespaceNameComment(stateVertex,
                                name,
                                commentString);
        stateVertex.setContainer(container);
        container.getSubVertex().add(stateVertex);
        return stateVertex;
    }

    /**
     * Makes a new state object.  The top (composite) state must be created
     * first, then parent states should be created before their child states.
     *
     * @param name the identifier for the state within its containing
     * namespace
     * @param commentString the comment for this state
     * @param container the container (parent) of this state, or null if top state
     * @param entry the entry action for this state, or null if none
     * @param exit the exit action for this state, or null if none
     * @param doActivity the do activity for this state, or null if none
     * @return the new state object
     */
    public State makeState (String name,
                            String commentString,
                            CompositeState container,
                            Procedure entry,
                            Procedure exit,
                            Procedure doActivity) {
        State state = new State();
        setNamespaceNameComment(state,
                                name,
                                commentString);
        state.setContainer(container);
        if (container == null)
            state.setStateMachine(stateMachine);
        else
            container.getSubVertex().add(state);
        state.setEntry(entry);
        state.setExit(exit);
        state.setDoActivity(doActivity);
        return state;
    }

    /**
     * Makes a new composite state object.
     *
     * @param name the identifier for the composite state within its containing
     * namespace
     * @param commentString the comment for this composite state
     * @param container the container of this composite state, or null if top state
     * @param entry the entry action for this composite state, or null if none
     * @param exit the exit action for this composite state, or null if none
     * @param doActivity the do activity for this composite state, or null if none
     * @param isConcurrent true if concurrent processes, otherwise false
     * @return the new composite state object
     */
    public State makeCompositeState (String name,
                                     String commentString,
                                     CompositeState container,
                                     Procedure entry,
                                     Procedure exit,
                                     Procedure doActivity,
                                     boolean isConcurrent) {
        CompositeState compositeState = new CompositeState();
        setNamespaceNameComment(compositeState,
                                name,
                                commentString);
        compositeState.setContainer(container);
        compositeState.setIsRegion(false);
        if (container == null)
            compositeState.setStateMachine(stateMachine);
        else {
            container.getSubVertex().add(compositeState);
            if (container.isConcurrent())
                compositeState.setIsRegion(true);
        }
        compositeState.setEntry(entry);
        compositeState.setExit(exit);
        compositeState.setDoActivity(doActivity);
        compositeState.setIsConcurrent(isConcurrent);
        return compositeState;
    }

    /**
     * Sets the namespace, name and comment string for the new
     * model element.
     *
     * @param modelElement the given model element
     * @param name the identifier for the state machine within its containing
     * namespace
     * @param commentString the comment for this state machine
     */
    protected void setNamespaceNameComment (ModelElement modelElement,
                                            String name,
                                            String commentString) {
        namespace.addOwnedElement(modelElement);
        modelElement.setNamespace(namespace);
        modelElement.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(modelElement);
        modelElement.setComment(comment);
    }


}