package org.opencyc.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.conversation.*;

/**
 * Provides a chat conversation interface to Cyc.<p>
 *
 * The chat conversation is in the form of a text conversation using
 * asynchronous receiving and sending of messages.  This class intializes
 * the Cyc server connection, and initializes the fsm interpreter,
 * for each chat partner, then delegates the conversation understanding and
 * responses to the chat partner's fsm interpreter.
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
     * Dictionary of chat user models, chatUserUniqueId --> ChatUserModel
     */
    protected HashMap chatUserModels = new HashMap();

    /**
     * Provides wrappers for the Cyc API and manages the connection
     * to the Cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * Makes Fsm objects for interpretation.
     */
    public FsmFactory fsmFactory;

    /**
     * Dictionary of fsm finite state machine interpreters.
     * chatPartner --> fsmFsmInterpreter
     */
    protected HashMap interpreters = new HashMap();

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
    public void initialize() throws CycApiException,
                                    UnknownHostException,
                                    IOException,
                                    ChatException {
        chatSender.sendChatMessage("I am initializing");
        cycAccess = new CycAccess();
        fsmFactory = new FsmFactory();
        fsmFactory.initialize();
        chatSender.sendChatMessage("I am ready to chat");
    }

    /**
     * Makes a new fsm finite state machine interpreter object for
     * a new chat partner, and stores it in the dictionary for later lookup.
     *
     * @param chatUserNickname the preferred name (possibly not unique) of the
     * chat partner
     * @param chatUserUniqueId the unique id assigned to the user by the chat
     * system
     * @return the new fsm finite state machine interpreter
     */
    protected Interpreter makeInterpreter (String chatUserNickname,
                                           String chatUserUniqueId) {
        Fsm chat = fsmFactory.getFsm("chat");
        Interpreter interpreter =
            new Interpreter(this,
                            chatUserNickname,
                            chatUserUniqueId,
                            "initial",
                            chat);
        interpreters.put(chatUserUniqueId, interpreter);
        return interpreter;
    }

    /**
     * Closes this ChatterBot, in particular the connection to the Cyc server.
     */
    public void finalize() {
        cycAccess.close();
        try {
            chatSender.sendChatMessage("I am gone");
        }
        catch (ChatException e) {
        }
    }

    /**
     * Receives the given chat message from the given chat partner.  Delegates
     * the message understanding and fsmal response to the
     * fsmFsmInterpreter object.
     *
     * @param chatUserNickname the preferred name (possibly not unique) of the
     * chat partner
     * @param chatUserUniqueId the unique id assigned to the user by the chat
     * system
     * @param chatMessage the chat message
     */
    public void receiveChatMessage (String chatUserNickname,
                                    String chatUserUniqueId,
                                    String chatMessage)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
        Interpreter interpreter =
            (Interpreter) interpreters.get(chatUserUniqueId);
        if (interpreter == null)
            interpreter = makeInterpreter(chatUserNickname, chatUserUniqueId);
        interpreter.receiveChatMessage(chatMessage);
    }

    /**
     * Sends the given chat message into the chat system.
     *
     * @param chatMessage the chat message
     */
    public void sendChatMessage (String chatMessage) throws ChatException {
        chatSender.sendChatMessage(chatMessage);
    }

    /**
     * Returns the chat user model for the given chat partner.  If not
     * cached, retreives the stored user model from the KB, or creates
     * a new one if the chat partner is new.
     *
     * @param chatUserUniqueId the unique id assigned to the user by the chat
     * system
     * @return the chat user model
     */
    public ChatUserModel getChatUserModel (String chatUserUniqueId) {
        ChatUserModel chatUserModel = (ChatUserModel) chatUserModels.get(chatUserUniqueId);
        if (chatUserModel == null) {
            chatUserModel = new ChatUserModel(chatUserUniqueId);
        }
        return chatUserModel;
    }

}