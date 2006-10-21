package org.opencyc.chat;

import java.io.IOException;
import java.net.UnknownHostException;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.uml.interpreter.Interpreter;

/**
 * Provides a chat conversation interpreter.<p>
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
public class ChatInterpreter {

    /**
     * the chatterbot
     */
    protected ChatterBot chatterBot;

    /**
     * the state machine interpreter
     */
    protected Interpreter interpreter;

    /**
     * Provides wrappers for the Cyc API and manages the connection
     * to the Cyc server.
     */
    protected CycAccess cycAccess;

    /**
     * Constructs a new ChatInterpreter object given the cyc api access object.
     *
     * @param cycAccess the given cyc api access object
     */
    public ChatInterpreter(CycAccess cycAccess) {
        this.cycAccess = cycAccess;
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
     * Receives the given chat message from the given chat partner.  Delegates
     * the message understanding and response to the
     * chat interpreter.
     *
     * @param chatUserNickname the preferred name (possibly not unique) of the
     * chat partner
     * @param chatMessage the chat message
     */
    public void receiveChatMessage (String chatUserNickname,
                                    String chatMessage)
        throws CycApiException,
               IOException,
               UnknownHostException,
               ChatException {
    }

    /**
     * Gets the state machine interpreter
     *
     * @return the state machine interpreter
     */
    public Interpreter getInterpreter () {
        return interpreter;
    }

    /**
     * Sets the state machine interpreter
     *
     * @param interpreter the state machine interpreter
     */
    public void setInterpreter (Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * Gets the chatterbot
     *
     * @return the chatterbot
     */
    public ChatterBot getChatterBot () {
        return chatterBot;
    }

    /**
     * Sets the chatterbot
     *
     * @param chatterBot the chatterbot
     */
    public void setChatterBot (ChatterBot chatterBot) {
        this.chatterBot = chatterBot;
    }
}