package org.opencyc.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;

/**
 * Provides a chat dialog interface to Cyc.<p>
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
public class ChatterBot {

    /**
     * Sends messages to the chat system.
     */
    protected ChatSender chatSender;

    /**
     * Set of chat senders, chatSender --> ChatUserModel
     */
    protected HashMap chatSenders;

    /**
     * Provides wrappers for the Cyc API and manages the connection
     * to the Cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * Makes Dialog objects for interpretation.
     */
    DialogFactory dialogFactory;

    /**
     * Interprets the chat dialog.
     */
    DialogFsmInterpreter dialogFsmInterpreter;

    /**
     * Makes Template objects for the TemplateParser.
     */
    TemplateFactory templateFactory;

    /**
     * Parses the users input text.
     */
    TemplateParser templateParser;

    /**
     * Creates a new ChatterBot object, given a ChatSender.
     *
     * @param chatSender the object which connects the ChatterBot to the
     * chat system.
     */
    public ChatterBot(ChatSender chatSender) {
        this.chatSender = chatSender;
    }

    /**
     * Initializes this ChatterBot, in particular the connection to the Cyc server.
     */
    public void initialize() throws CycApiException, UnknownHostException, IOException {
        chatSender.sendChatMessage("I am initializing");
        cycAccess = new CycAccess();
        dialogFactory = new DialogFactory();
        dialogFactory.initialize();
        dialogFsmInterpreter = new DialogFsmInterpreter(this);
        Dialog chat = dialogFactory.makeChat();
        dialogFsmInterpreter.setDialogFsmCurrentNode(chat.getInitialDialogFsmNode());
        templateFactory = new TemplateFactory();
        templateParser = new TemplateParser();
        templateParser.initialize();
        chatSender.sendChatMessage("I am ready to chat");
    }

    /**
     * Closes this ChatterBot, in particular the connection to the Cyc server.
     */
    public void finalize() throws CycApiException, UnknownHostException, IOException {
        cycAccess.close();
        chatSender.sendChatMessage("I am gone");
    }

    public void receiveChatMessage (String chatPartner, String chatMessage) {
        chatSender.sendChatMessage(chatPartner + ", I echo \"" + chatMessage + "\"");
    }



}