package org.opencyc.chat;

import java.util.HashMap;

/**
 * Makes chat dialogs which can be interpreted by the DialogFsmInterpreter.<p>
 *
 * The chat dialog is in the form of a text conversation using
 * asynchronous receiving and sending of messages.
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
public class DialogFactory {

    /**
     * Caches dialog objects to keep from making them twice.
     * name --> Dialog
     */
    static HashMap dialogCache = new HashMap();

    /**
     * DialogFsmAction object factory
     */
    DialogFsmActionFactory dialogFsmActionFactory;

    /**
     * DialogFsmEvent object factory
     */
    DialogFsmEventFactory dialogFsmEventFactory;

    /**
     * Template object factory
     */
    TemplateFactory templateFactory;

    /**
     * Constructs a new DialogFactory object.
     */
    public DialogFactory() {
        dialogFsmActionFactory = new DialogFsmActionFactory();
        dialogFsmEventFactory = new DialogFsmEventFactory();
        templateFactory = new TemplateFactory();
    }

    /**
     * Initializes this object.
     */
    public void initialize () {
        templateFactory.makeAllTemplates();
    }

    /**
     * Returns the Dialog having the given name;
     *
      * @param name the dialog name
     */
    public Dialog getDialog (String name) {
        return (Dialog) dialogCache.get(name);

    }

    /**
      * Makes a "chat" Dialog.
      * Initial state is ready.
      * 1. If we are in the ready state and get a term-query event, transition to the ready state
      * and perform the do-term-query action. <br>
      * 2. If we are in the ready state and get a quit event, transition to the final state and
      * perform the do-finalization action. <br>
      */
    public Dialog makeChat () {
        Dialog chat = (Dialog) dialogCache.get("chat");
        if (chat != null)
            return chat;
        chat = new Dialog("chat");
        DialogFsmNode readyNode = new DialogFsmNode("ready");
        chat.setInitialDialogFsmNode(readyNode);
        chat.addDialogFsmNode(readyNode);
        DialogFsmNode finalNode = new DialogFsmNode("final");
        chat.addDialogFsmNode(finalNode);

        DialogFsmAction doTermQueryAction =
            dialogFsmActionFactory.makeDialogFsmAction("do-term-query");
        DialogFsmAction doFinalizationAction =
            dialogFsmActionFactory.makeDialogFsmAction("do-finalization");
        DialogFsmEvent termQueryEvent =
            dialogFsmEventFactory.makeDialogFsmEvent("term-query");
        DialogFsmEvent quitEvent =
            dialogFsmEventFactory.makeDialogFsmEvent("quit");
        /**
         * 1. If we are in the ready state and get a term-query event, transition
         * to the ready state and perform the do-term-query action.
         */
        DialogFsmArc arc1 = new DialogFsmArc(readyNode,
                                             termQueryEvent,
                                             readyNode,
                                             doTermQueryAction);
        /**
         * 2. If we are in the ready state and get a quit event, transition
         * to the final state and perform the do-finalization action.
         */
        DialogFsmArc arc2 = new DialogFsmArc(readyNode,
                                             quitEvent,
                                             finalNode,
                                             doFinalizationAction);
        dialogCache.put(chat.name, chat);
        return chat;
    }

    /**
     * Makes a "term-query" Dialog.
     * Initial state is retrieve-fact. <br>
     * 1. If we are in the retrieve-fact state and get a term-query event, transition to the
     * prompt-for-more state and perform the reply-with-first-fact action. <br>
     * 2. If we are in the prompt-for-more state and get a quit event, transition to the done
     * state and perform the do-quit-event action. <br>
     * 3. If we are in the prompt-for-more state and get a more event, transition to the
     * prompt-for-more state and perform the reply-with-next-fact action. <br>
     * 4. If we are in the prompt-for-more state and get a done event, transition to the done
     * state and perform no action.
     */
    public Dialog makeTermQuery () {
        Dialog termQuery = (Dialog) dialogCache.get("term-query");
        if (termQuery != null)
            return termQuery;
        termQuery = new Dialog("term-query");

        DialogFsmNode retrieveFactNode = new DialogFsmNode("retrieve-fact");
        termQuery.setInitialDialogFsmNode(retrieveFactNode);
        termQuery.addDialogFsmNode(retrieveFactNode);

        DialogFsmNode promptForMoreNode = new DialogFsmNode("prompt-for-more");
        termQuery.addDialogFsmNode(promptForMoreNode);

        DialogFsmNode doneNode = new DialogFsmNode("done");
        termQuery.addDialogFsmNode(doneNode);

        DialogFsmAction replyWithFirstFactAction =
            dialogFsmActionFactory.makeDialogFsmAction("reply-with-first-fact");
        DialogFsmAction doQuitEventAction =
            dialogFsmActionFactory.makeDialogFsmAction("do-quit-event");
        DialogFsmAction replyWithNextFactAction =
            dialogFsmActionFactory.makeDialogFsmAction("reply-with-next-fact");

        DialogFsmEvent termQueryEvent = dialogFsmEventFactory.makeDialogFsmEvent("term-query");
        DialogFsmEvent quitEvent = dialogFsmEventFactory.makeDialogFsmEvent("quit");
        DialogFsmEvent moreEvent = dialogFsmEventFactory.makeDialogFsmEvent("more");
        DialogFsmEvent doneEvent = dialogFsmEventFactory.makeDialogFsmEvent("done");
        /**
         * 1. If we are in the retrieve-fact state and get a term-query event, transition
         * to the prompt-for-more state and perform the reply-with-first-fact action.
         */
        DialogFsmArc arc1 = new DialogFsmArc(retrieveFactNode,
                                             termQueryEvent,
                                             promptForMoreNode,
                                             replyWithFirstFactAction);

        /**
         * 2. If we are in the prompt-for-more state and get a quit event, transition
         * to the done state and perform the do-quit-event action.
         */
        DialogFsmArc arc2 = new DialogFsmArc(promptForMoreNode,
                                             quitEvent,
                                             doneNode,
                                             doQuitEventAction);
        /**
         * 3. If we are in the prompt-for-more state and get a more event, transition
         * to the prompt-for-more state and perform the reply-with-next-fact action.
         */
        DialogFsmArc arc3 = new DialogFsmArc(promptForMoreNode,
                                             moreEvent,
                                             promptForMoreNode,
                                             replyWithNextFactAction);
        /**
         * 4. If we are in the prompt-for-more state and get a done event, transition to the done
         * state and perform no action.
         */
        DialogFsmArc arc4 = new DialogFsmArc(promptForMoreNode,
                                             doneEvent,
                                             doneNode,
                                             null);

        dialogCache.put(termQuery.name, termQuery);
        return termQuery;
    }
}
