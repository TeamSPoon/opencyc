package org.opencyc.conversation;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.chat.*;
import org.opencyc.templateparser.*;
import org.opencyc.util.*;

/**
 * Contains the attributes and behavior of a chat fsm interpreter.<p>
 *
 * The chat conversation is in the form of a text conversation using
 * asynchronous receiving and sending of messages. This interpreter models the
 * chat interaction with nested fsms in a stack.  Mixed initiative is
 * supported by a dictionary of fsm stacks, one of which is active,
 * and the rest suspended.  Sub fsms are passed a list of attribute/value
 * pairs which form the initial state attributes.  When done, sub fsms
 * pass back result attribute/value pairs to the calling fsm's state
 * attributes.
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

public class Interpreter {

    /**
     * reference to the parent ChatterBot
     */
    protected ChatterBot chatterBot;

    /**
     * Reference to the user model, which contains user state and history
     * information, persisted in the Cyc KB.
     */
    protected ChatUserModel chatUserModel;

    /**
     * dictionary of fsm stacks
     * fsmStackId --> fsm stack
     */
    protected HashMap fsmStacks;

    /**
     * Reference to the current fsm stack, which is a stack of
     * FsmInfo elements.  Each of these elements contains
     * the fsm and its state attributes.
     *
     */
    protected StackWithPointer fsmStack;

    /**
     * reference to the active fsm
     */
    protected Fsm fsm;

    /**
     * finite state machine current node
     */
    protected State currentState;

    /**
     * Dictionary of state attribute and object values.
     */
    protected HashMap stateAttributes = new HashMap();

    /**
     * Next computed Performative, or null if none.
     */
    protected Performative nextPerformative;

    /**
     * Makes Template objects for the TemplateParser.
     */
    protected TemplateFactory templateFactory;

    /**
     * Parses the users input text.
     */
    protected TemplateParser templateParser;

    /**
     * Performs fsm actions.
     */
    protected Performer performer;

    /**
     * Constructs a new Interpreter without arguments.  Used for unit
     * testing.
     */
    protected Interpreter () {
    }

    /**
     * Constructs a new Interpreter object given the parent
     * ChatterBot.
     *
     * @param chatterBot the parent ChatterBot
     * @param chatUserNickname the preferred name (possibly not unique) of the
     * chat partner
     * @param chatUserUniqueId the unique id assigned to the user by the chat
     * system
     */
    public Interpreter(ChatterBot chatterBot,
                       String chatUserNickname,
                       String chatUserUniqueId,
                       String fsmStackId,
                       Fsm fsm) {
        Log.makeLog();
        this.chatterBot = chatterBot;
        chatUserModel = chatterBot.getChatUserModel(chatUserUniqueId);
        initialize(fsmStackId, fsm);
    }

    /**
     * Receives the given chat message from the given chat partner, obtains
     * the performative, transitions to the corresponding state and performs
     * the required action.
     *
     * @param chatPartner the name of the chat partner
     * @param chatMessage the chat message
     */
    public void receiveChatMessage (String chatMessage)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
        ParseResults parseResults = templateParser.parse(chatMessage);
        setStateAttribute("parse results", parseResults);
        Performative performative = parseResults.getPerformative();
        nextPerformative = performative;
        while (nextPerformative != null) {
            Arc arc = lookupArc(nextPerformative);
            Action action = arc.getAction();
            action.setContent(nextPerformative.getContent());
            nextPerformative = null;
            transitionState(arc);
            Fsm subFsm = arc.getSubFsm();
            if (subFsm != null)
                performer.performArc(currentState, subFsm);
            else
                performer.performArc(currentState, action);
        }
    }

    /**
     * Returns the arc which corresponds to the performative received in the
     * current state.
     *
     * @param performative the performative
     * @return the arc which corresponds to the performative received in the
     * current state
     */
    protected Arc lookupArc (Performative performative) {
        Iterator arcs = currentState.getArcs().iterator();
        while (arcs.hasNext()) {
            Arc arc = (Arc) arcs.next();
            if (performative.getPerformativeName().equals(arc.getPerformative().getPerformativeName()))
                return arc;
        }
        Log.current.errorPrintln("No valid arc for state " +
                                 currentState.toString() +
                                 "\nmatching performative " + performative);
        return null;
    }

    /**
     * Transitions to the to-state given in the arc, and performs the action.
     *
     * @param arc the finite state machine arc
     */
    protected void transitionState (Arc arc) {
        currentState = arc.getTransitionToState();
        State previousState = arc.getTransitionFromState();
    }

    /**
     * Initializes the fsm interpreter.
     *
     * @param fsmStackId provides an id for the first
     * fsm stack
     * @param fsm the initial fsm, which is also the
     * sole object on the fsm stack
     */
    public void initialize (String fsmStackId,
                            Fsm fsm) {
        templateFactory = new TemplateFactory();
        templateFactory.makeAllTemplates();
        templateParser = new TemplateParser();
        templateParser.initialize();
        performer = new Performer(this);
        fsmStack = new StackWithPointer();
        this.fsm = fsm;
        FsmStateInfo fsmStateInfo =
            new FsmStateInfo(fsm, stateAttributes);
        pushFsmStateInfo(fsmStateInfo);
        fsmStacks = new HashMap();
        fsmStacks.put(fsmStackId, fsmStack);
        currentState = fsm.getInitialState();
    }

    /**
     * Sends the given chat message into the chat system.
     *
     * @param chatMessage the chat message
     */
    public void sendChatMessage (String chatMessage) throws ChatException {
        chatterBot.sendChatMessage(chatMessage);
    }

    /**
     * Sets the value for the given state attribute.
     *
     * @param attribute the key object
     * @param value the value object
     */
    public void setStateAttribute (String attribute, Object value) {
        stateAttributes.put(attribute, value);
    }

    /**
     * Sets the value of the computed next performative.
     *
     * @param nextAction the computed next performative
     */
    public void setNextPerformative (Performative nextPerformative) {
        this.nextPerformative = nextPerformative;
    }

    /**
     * Returns the value for the given state attribute.
     *
     * @param attribute the key object
     * @retrun the value for the given attribute
     */
    public Object getStateAttribute (Object attribute) {
        return stateAttributes.get(attribute);
    }

    /**
     * Sets up the given sub fsm and the input arguments as
     * a list of attribute/value pairs.
     *
     * @param fsm the new fsm
     * @param arguments a list of Object arrays of length two, the first array element is the
     * attribute and the second array element is its value
     */
    public void setupSubFsm (Fsm fsm,
                                      ArrayList arguments) {
        FsmStateInfo fsmStateInfo =
            new FsmStateInfo(fsm,
                                      new HashMap());
        pushFsmStateInfo(fsmStateInfo);
        currentState = fsm.getInitialState();
        for (int i = 0; i < arguments.size(); i++) {
            Object [] attributeValuePair = (Object []) arguments.get(i);
            String attribute = (String) attributeValuePair[0];
            Object value = attributeValuePair[1];
            setStateAttribute(attribute, value);
        }
        nextPerformative = new Performative("start-new-fsm");
    }

    /**
     * Pushes the given fsm state onto the current
     * fsm stack.
     *
     * @param fsmStateInfo the new fsm and its state
     */
    public void pushFsmStateInfo (FsmStateInfo fsmStateInfo) {
        if (fsmStack.size() > 0) {
            FsmStateInfo suspendedFsmStateInfo =
                (FsmStateInfo) fsmStack.peek();
            suspendedFsmStateInfo.currentState = currentState;
        }
        fsmStack.push(fsmStateInfo);
    }

    /**
     * Pops the fsm state stack and restores the previous
     * fsm state.
     */
    public void popFsmStateInfo () {
        ArrayList results = (ArrayList) getStateAttribute("subFsm results");
        FsmStateInfo fsmStateInfo =
            (FsmStateInfo) fsmStack.pop();
        this.fsm = fsmStateInfo.fsm;
        this.currentState = fsmStateInfo.currentState;
        this.stateAttributes = fsmStateInfo.stateAttributes;
        for (int i = 0; i < results.size(); i++) {
            Object [] attributeValuePair = (Object []) results.get(i);
            String attribute = (String) attributeValuePair[0];
            Object value = attributeValuePair[1];
            setStateAttribute(attribute, value);
        }
        nextPerformative = new Performative("resume-previous-fsm");
    }

}