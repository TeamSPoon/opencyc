package org.opencyc.conversation;

import org.opencyc.util.*;
/**
 * Performs actions for the chat conversation interpreter.<p>
 *
 * The chat conversation is in the form of a text conversation using
 * asynchronous receiving and sending of messages. This interpreter contains
 * all the possible finite state machine actions and interprets the
 * actions required for a state transition.
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

public class Performer {

    /**
     * reference to the parent Interpreter object
     */
    Interpreter interpreter;

    /**
     * Constructs a new Performer object given the reference to the parent
     * finite state machine interpreter object.
     *
     * @param interpreter the parent finite state machine interpreter
     */
    public Performer(Interpreter interpreter) {
        Log.makeLog();
        this.interpreter = interpreter;
    }

    /**
     * Performs the action given the current state and the action
     *
     * @param currentState the current state of the finite state machine
     * @param action the action to be performed
     */
    protected void performArc (State currentState, Action action) {
        if (action.getName().equals("do-not-understand")) {
            doNotUnderstand(currentState, action);
        }
        else if (action.getName().equals("do-finalization")) {
            doFinalization();
        }
        else if (action.getName().equals("do-term-query")) {
            doTermQuery(currentState, action);
        }
        else if (action.getName().equals("reply-with-first-fact")) {
            doReplyWithFirstFact(currentState, action);
        }
        else if (action.getName().equals("reply-with-next-fact")) {
            doReplyWithNextFact(currentState, action);
        }
        else {
            Log.current.errorPrintln("Unhandled action " + action);
        }
    }

    /**
     * Performs the do-not-understand action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doNotUnderstand (State currentState, Action action) {
    }

    /**
     * Performs the do-finalization action.
     *
     * @param action the action object.
     *
     */
    protected void doFinalization () {
        interpreter.chatterBot.finalize();
    }

    /**
     * Performs the do-term-query action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doTermQuery (State currentState, Action action) {
    }

    /**
     * Performs the repy-with-first-fact action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doReplyWithFirstFact (State currentState, Action action) {
    }

    /**
     * Performs the repy-with-next-fact action.
     *
     * @param currentState the current conversation state
     * @param action the action object.
     */
    protected void doReplyWithNextFact (State currentState, Action action) {
    }


}