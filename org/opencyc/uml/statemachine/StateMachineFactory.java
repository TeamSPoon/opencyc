package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.action.*;
import org.opencyc.uml.datatypes.Multiplicity;

/**
 * StateVertex from the UML State_Machines package.
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
     * machine will linked to the given namespace.  The name of the state machine is
     * also required to be the name of the context classifier
     *
     * @param namespaceName the state machine namespace name
     * @param name the identifier for the state machine within its containing
     * namespace
     * @param commentString the comment for this state machine
     * @param contextGeneralizations the list of Classifer generalizations for the
     * given context classifier
     * @return the new state machine object
     */
    public StateMachine makeStateMachine(String namespaceName,
                                         String name,
                                         String commentString) {
        stateMachine = new StateMachine();
        namespace = new Namespace(namespaceName);
        setNamespaceNameComment(stateMachine,
                                name,
                                commentString);
        return stateMachine;
    }

    /**
     * Associates the given Classifier shell with the state machine.
     *
     * @param name the name of the context Classifier
     * @param commentString the comment for the given context Classifier
     */
    public void associateClassifierToStateMachine (String name,
                                                   String commentString) {
        org.opencyc.uml.core.Class context = new org.opencyc.uml.core.Class();
        setNamespaceNameComment(context,
                                name,
                                commentString);
        context.setIsAbstract(false);
        stateMachine.setContext(context);
    }

    /**
     * Adds a direct super classifer to the context Classifier.
     *
     * @param generalizableElement the direct super Classifier
     */
    public void addGeneralizationToClassifier (Classifier generalizableElement) {
        stateMachine.getContext().addGeneralization(generalizableElement);
    }

    /**
     * Adds a state variable to the context Classifier.
     *
     * @param name the name of the state variable
     * @param commentString the comment for this state machine
     * @param targetScope indicates whether this is a class or instance attribute
     * @param type the type of the state variable
     * @param changeability indicates whether the attribute may be changed
     * @param multiplicity the multiplicity of the attribute
     * @param ordering the ordering of the attribute
     * @param initialValue the initial value expression
     */
    public void addStateVariableToClassifier (String name,
                                              String commentString,
                                              int targetScope,
                                              Object type,
                                              int changeability,
                                              Multiplicity multiplicity,
                                              int ordering,
                                              Expression initialValue) {
        Attribute attribute = new Attribute();
        setNamespaceNameComment(attribute,
                                name,
                                commentString);
        attribute.setType(type);
        attribute.setChangeability(changeability);
        attribute.setMultiplicity(multiplicity);
        attribute.setOrdering(ordering);
        attribute.setInitialValue(initialValue);
        attribute.setTargetScope(targetScope);
        stateMachine.getContext().addFeature(attribute);
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
                                    Object body,
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
     * @return the added input pin for the given procedure
     */
    public InputPin addInputPinToProcedure(String name,
                                           String commentString,
                                           Procedure procedure,
                                           java.lang.Class type) {
        InputPin inputPin = new InputPin();
        setNamespaceNameComment(inputPin,
                                name,
                                commentString);
        inputPin.setType(type);
        inputPin.setProcedure(procedure);
        procedure.getArgument().add(inputPin);
        return inputPin;
    }

    /**
     * Adds an output pin (variable) to the given procedure.
     *
     * @param name the name of the output pin (variable)
     * @param commentString the comment for this output pin
     * @param procedure the given procedure to which the output pin is to
     * be added
     * @param type the Class of the output pin (variable)
     * @return the added output pin for the given procedure
     */
    public OutputPin addOutputPinToProcedure(String name,
                                             String commentString,
                                             Procedure procedure,
                                             java.lang.Class type) {
        OutputPin outputPin = new OutputPin();
        setNamespaceNameComment(outputPin,
                                name,
                                commentString);
        outputPin.setType(type);
        outputPin.setProcedure(procedure);
        procedure.getResult().add(outputPin);
        return outputPin;
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
                                        Object body) {
        ChangeEvent changeEvent = new ChangeEvent();
        setNamespaceNameComment(changeEvent,
                                name,
                                commentString);
        BooleanExpression changeExpression = new BooleanExpression();
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
        CompletionEvent completionEvent = new CompletionEvent(state);
        setNamespaceNameComment(completionEvent,
                                name,
                                commentString);
        return completionEvent;
    }

    /**
     * Makes a new SignalEvent object.
     *
     * @param name the identifier for the signal event within its containing
     * namespace
     * @param commentString the comment for this signal event
     * @param signal the signal causing this event
     * @return the new signal event object
     */
    public SignalEvent makeSignalEvent (String name,
                                        String commentString,
                                        Signal signal) {
        SignalEvent signalEvent = new SignalEvent();
        setNamespaceNameComment(signalEvent,
                                name,
                                commentString);
        signalEvent.setSignal(signal);
        return signalEvent;
    }

    /**
     * Makes a new TimeEvent object.
     *
     * @param name the identifier for the change event within its containing
     * namespace
     * @param commentString the comment for this change event
     * @param language the language of the boolean change expression
     * @param body the body of the boolean change expression
     * @return the new change event object
     */
    public TimeEvent makeTimeEvent (String name,
                                    String commentString,
                                    String language,
                                    Object body) {
        TimeEvent timeEvent = new TimeEvent();
        setNamespaceNameComment(timeEvent,
                                name,
                                commentString);
        TimeExpression when = new TimeExpression();
        when.setLanguage(language);
        when.setBody(body);
        timeEvent.setWhen(when);
        return timeEvent;
    }


    /**
     * Adds a parameter to the given event.
     *
     * @param name the name of parameter
     * @param commentString the comment for this parameter
     * @param event the given event to which the parameter is to
     * be added
     * @param type the Class of the parameter
     * @return the adeded parameter for the given event
     */
    public Parameter addParameterToEvent(String name,
                                         String commentString,
                                         Event event,
                                         java.lang.Class type) {
        Parameter parameter = new Parameter();
        setNamespaceNameComment(parameter,
                                name,
                                commentString);
        if (event instanceof CallEvent)
            parameter.setBehavioralFeature(((CallEvent) event).getOperation());
        parameter.setType(type);
        return parameter;
    }

    /**
     * Destroys the given event, its parameters and its comment by unlinking
     * them from their associations.
     *
     * @param event the given event to be destroyed
     */
    public void destroyEvent (Event event) {
        Comment comment = event.getComment();
        Namespace namespace = comment.getNamespace();
        namespace.getOwnedElement().remove(comment);
        Iterator parameters = event.getParameter().iterator();
        while (parameters.hasNext())
            destroyParameter((Parameter) parameters.next());
        namespace.getOwnedElement().remove(event);
        event.setComment(null);
        event.setNamespace(null);
    }

    /**
     * Destroys the given comment by unlinking it from its associations.
     *
     * @param comment the given comment to destroy
     */
    public void destroyComment (Comment comment) {
        Namespace namespace = comment.getNamespace();
        namespace.getOwnedElement().remove(comment);
        comment.setComment(null);
        comment.setNamespace(null);
        comment.setAnnotatedElement(null);
    }

    /**
     * Destroys the given parameter by unlinking it from its associations.
     *
     * @param parameter the given parameter to destroy
     */
    public void destroyParameter (Parameter parameter) {
        destroyComment(parameter.getComment());
        Namespace namespace = parameter.getNamespace();
        namespace.getOwnedElement().remove(parameter);
        parameter.setComment(null);
        parameter.setNamespace(null);
        parameter.setBehavioralFeature(null);
        parameter.setDefaultValue(null);
    }

    /**
     * Makes a new simple state object.  The top (composite) state must be created
     * first, then parent states should be created before their child simple states.
     *
     * @param name the identifier for the simple state within its containing
     * namespace
     * @param commentString the comment for this simple state
     * @param container the container (parent) of this simple state, or null if top state
     * @param entry the entry action for this simple state, or null if none
     * @param exit the exit action for this simple state, or null if none
     * @param doActivity the do activity for this simple state, or null if none
     * @return the newsimple  state object
     */
    public SimpleState makeSimpleState (String name,
                                        String commentString,
                                        CompositeState container,
                                        Procedure entry,
                                        Procedure exit,
                                        Procedure doActivity) {
        SimpleState simpleState = new SimpleState();
        setNamespaceNameComment(simpleState,
                                name,
                                commentString);
        simpleState.setContainer(container);
        if (container == null)
            simpleState.setStateMachine(stateMachine);
        else
            container.getSubVertex().add(simpleState);
        simpleState.setEntry(entry);
        simpleState.setExit(exit);
        simpleState.setDoActivity(doActivity);
        return simpleState;
    }

    /**
     * Makes a new final state object.  The top (composite) state must be created
     * first, then parent states should be created before their child final states.
     *
     * @param name the identifier for the final state within its containing
     * namespace
     * @param commentString the comment for this final state
     * @param container the container (parent) of this final state, or null if top state
     * @param entry the entry action for this final state, or null if none
     * @param exit the exit action for this final state, or null if none
     * @param doActivity the do activity for this final state, or null if none
     * @return the newfinal  state object
     */
    public FinalState makeFinalState (String name,
                                      String commentString,
                                      CompositeState container,
                                      Procedure entry,
                                      Procedure exit,
                                      Procedure doActivity) {
        FinalState finalState = new FinalState();
        setNamespaceNameComment(finalState,
                                name,
                                commentString);
        finalState.setContainer(container);
        if (container == null)
            finalState.setStateMachine(stateMachine);
        else
            container.getSubVertex().add(finalState);
        finalState.setEntry(entry);
        finalState.setExit(exit);
        finalState.setDoActivity(doActivity);
        return finalState;
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
    public CompositeState makeCompositeState (String name,
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
     * Makes a new pseudo state object.
     *
     * @param name the identifier for the pseudo state within its containing
     * namespace
     * @param commentString the comment for this pseudo state
     * @param container the container of this pseudo state
     * @param kind the kind of PseudoState
     * @return the new pseudo state object
     */
    public PseudoState makePseudoState (String name,
                                        String commentString,
                                        CompositeState container,
                                        int kind) {
        PseudoState pseudoState = new PseudoState();
        setNamespaceNameComment(pseudoState,
                                name,
                                commentString);
        pseudoState.setContainer(container);
        container.getSubVertex().add(pseudoState);
        pseudoState.setKind(kind);
        return pseudoState;
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
        comment.setNamespace(namespace);
        comment.setName("");
        comment.setBody(commentString);
        comment.setAnnotatedElement(modelElement);
        modelElement.setComment(comment);
    }

    /**
     * Makes a new transition object.
     *
     * @param name the identifier for the transition within its containing
     * namespace
     * @param commentString the comment for this transition
     * @param guardExpressionLanguage the language in which the guard
     * expression is written
     * @param guardExpressionBody the body of the guard expression, or null
     * if no guard
     * @param effect the procedure which is the effect of this transition, or
     * null if no effect
     * @param trigger the event which triggers this transition
     * @param source the source state of this transition
     * @param target the target state of this transition
     */
    public Transition makeTransition (String name,
                                      String commentString,
                                      String guardExpressionLanguage,
                                      Object guardExpressionBody,
                                      Procedure effect,
                                      Event trigger,
                                      StateVertex source,
                                      StateVertex target) {
        Transition transition = new Transition();
        setNamespaceNameComment(transition,
                                name,
                                commentString);
        if (guardExpressionBody != null) {
            Guard guard = new Guard();
            setNamespaceNameComment(guard,
                                    name,
                                    commentString);
            BooleanExpression guardExpression = new BooleanExpression();
            guardExpression.setLanguage(guardExpressionLanguage);
            guardExpression.setBody(guardExpressionBody);
            guard.setexpression(guardExpression);
            guard.setTransition(transition);
            transition.setGuard(guard);
        }
        transition.setEffect(effect);
        transition.setTrigger(trigger);
        transition.setSource(source);
        transition.setTarget(target);
        transition.setStateMachine(stateMachine);
        stateMachine.getTransition().add(transition);
        if (source instanceof State &&
            source.equals(target))
            ((State) source).getInternalTransition().add(transition);
        else {
            source.getOutgoing().add(transition);
            target.getIncoming().add(transition);
        }
        return transition;
    }

    /**
     * Sets the state machine for this state machine factory in the case where
     * the state machine already exists.
     *
     * @param stateMachine the state machine to extend or modify
     */
    public void setStateMachine (StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Sets the namespace for this state machine factory in the case where
     * the state machine already exists.
     *
     * @param namespace the namespace for the state machine model elements
     */
    public void setNamespace (Namespace namespace) {
        this.namespace = namespace;
    }

    /**
     * Adds an input pin binding for the transition effect
     *
     * @param transition the given transition
     * @param inputPin the given input pin
     * @param boundObject the object bound to the given input pin
     * @return the input pin bindings for the transition effect
     */
    public void addEffectInputBinding (Transition transition,
                                       InputPin inputPin,
                                       Object boundObject) {
        transition.addEffectInputBinding(inputPin, boundObject);
    }

    /**
     * Adds an output pin binding for the transition effect
     *
     * @param transition the given transition
     * @param outputPin the given output pin
     * @param stateVariable the state variable bound to the given output pin
     * @return the output pin bindings for the transition effect
     */
    public void addEffectOutputBinding (Transition transition,
                                       OutputPin outputPin,
                                       StateVariable stateVariable) {
        transition.addEffectOutputBinding(outputPin, stateVariable);
    }

    /**
     * Adds an input pin binding for the state entry procedure
     *
     * @param state the given state
     * @param inputPin the given input pin
     * @param boundObject the object bound to the given input pin
     * @return the input pin bindings for the state entry procedure
     */
    public void addEntryInputBinding (State state,
                                      InputPin inputPin,
                                      Object boundObject) {
        state.addEntryInputBinding(inputPin, boundObject);
    }

    /**
     * Adds an output pin binding for the state entry procedure
     *
     * @param state the given state
     * @param outputPin the given output pin
     * @param stateVariable the state variable bound to the given output pin
     * @return the output pin bindings for the state entry procedure
     */
    public void addEntryOutputBinding (State state,
                                       OutputPin outputPin,
                                       StateVariable stateVariable) {
        state.addEntryOutputBinding(outputPin, stateVariable);
    }

    /**
     * Adds an input pin binding for the state exit procedure
     *
     * @param state the given state
     * @param inputPin the given input pin
     * @param boundObject the object bound to the given input pin
     * @return the input pin bindings for the state exit procedure
     */
    public void addExitInputBinding (State state,
                                     InputPin inputPin,
                                     Object boundObject) {
        state.addExitInputBinding(inputPin, boundObject);
    }

    /**
     * Adds an output pin binding for the state exit procedure
     *
     * @param state the given state
     * @param outputPin the given output pin
     * @param stateVariable the state variable bound to the given output pin
     * @return the output pin bindings for the state exit procedure
     */
    public void addExitOutputBinding (State state,
                                      OutputPin outputPin,
                                      StateVariable stateVariable) {
        state.addExitOutputBinding(outputPin, stateVariable);
    }

    /**
     * Adds an input pin binding for the state doActivity procedure
     *
     * @param state the given state
     * @param inputPin the given input pin
     * @param boundObject the object bound to the given input pin
     * @return the input pin bindings for the state doActivity procedure
     */
    public void addDoActivityInputBinding (State state,
                                           InputPin inputPin,
                                           Object boundObject) {
        state.addDoActivityInputBinding(inputPin, boundObject);
    }

    /**
     * Adds an output pin binding for the state doActivity procedure
     *
     * @param state the given state
     * @param outputPin the given output pin
     * @param stateVariable the state variable bound to the given output pin
     * @return the output pin bindings for the state doActivity procedure
     */
    public void addDoActivityOutputBinding (State state,
                                            OutputPin outputPin,
                                            StateVariable stateVariable) {
        state.addDoActivityOutputBinding(outputPin, stateVariable);
    }

}