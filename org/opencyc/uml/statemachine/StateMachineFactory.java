package org.opencyc.uml.statemachine;

import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;

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
     * the state machine being assembled
     */
    StateMachine stateMachine;

    /**
     * Constructs a new StateMachineFactory object.
     */
    public StateMachineFactory() {
    }

    /**
     * Makes a new state machine object.
     *
     * @param namespace the state machine namespace
     * @param name the identifier for the state machine within its containing
     * namespace
     * @param commentString the comment for this state machine
     * @param context the context Classifier of this state machine, which contains the
     * variables that distinguish the state, and the operations which
     * can be performed during state transitions.
     * @return the new state machine object
     */
    public StateMachine makeStateMachine(String namespace,
                                         String name,
                                         String commentString,
                                         Object context) {
        stateMachine = new StateMachine();
        stateMachine.setNamespace(namespace);
        stateMachine.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(stateMachine);
        stateMachine.setComment(comment);
        stateMachine.setContext(context);
        return stateMachine;
    }

    /**
     * Makes a new procedure object.
     *
     *
     * @param namespace the state vertex namespace
     * @param name the identifier for the state vertex within its containing
     * namespace
     * @param commentString the comment for this state vertex
     * @param language the name of the language in which the body attribute is written
     * @param isList true when the arguments to the procedure are passed as
     * attributes of a single object, or false when passed separately
     * @param expression the text of the procedure written in the given language
     * @param method a method which is performed by the procedure
     * @return the new procedure object
     */
    public Procedure makeProcedure (String namespace,
                                    String name,
                                    String commentString,
                                    String language,
                                    boolean isList,
                                    Expression expression,
                                    Method method) {
        Procedure procedure = new Procedure();
        procedure.setNamespace(namespace);
        procedure.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(procedure);
        procedure.setComment(comment);
        procedure.setLanguage(language);
        procedure.setIsList(isList);
        procedure.setExpression(expression);
        procedure.setMethod(method);
        return procedure;
    }


    /**
     * Makes a new state vertex object.
     *
     * @param namespace the state vertex namespace
     * @param name the identifier for the state vertex within its containing
     * namespace
     * @param commentString the comment for this state vertex
     * @param container the container of this state vertex
     * @return the new state vertex object
     */
    public StateVertex makeStateVertex (String namespace,
                                        String name,
                                        String commentString,
                                        CompositeState container) {
        StateVertex stateVertex = new StateVertex();
        stateVertex.setNamespace(namespace);
        stateVertex.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(stateVertex);
        stateVertex.setComment(comment);
        stateVertex.setContainer(container);
        container.getSubVertex().add(stateVertex);
        return stateVertex;
    }

    /**
     * Makes a new state object.  The top (composite) state must be created
     * first, then parent states should be created before their child states.
     *
     * @param namespace the state namespace
     * @param name the identifier for the state within its containing
     * namespace
     * @param commentString the comment for this state
     * @param container the container (parent) of this state, or null if top state
     * @param entry the entry action for this state, or null if none
     * @param exit the exit action for this state, or null if none
     * @param doActivity the do activity for this state, or null if none
     * @return the new state object
     */
    public State makeState (String namespace,
                            String name,
                            String commentString,
                            CompositeState container,
                            Procedure entry,
                            Procedure exit,
                            Procedure doActivity) {
        State state = new State();
        state.setNamespace(namespace);
        state.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(state);
        state.setComment(comment);
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
     * @param namespace the composite state namespace
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
    public State makeCompositeState (String namespace,
                                     String name,
                                     String commentString,
                                     CompositeState container,
                                     Procedure entry,
                                     Procedure exit,
                                     Procedure doActivity,
                                     boolean isConcurrent) {
        CompositeState compositeState = new CompositeState();
        compositeState.setNamespace(namespace);
        compositeState.setName(name);
        Comment comment = new Comment();
        comment.setBody(commentString);
        comment.setAnnotatedElement(compositeState);
        compositeState.setComment(comment);
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

}