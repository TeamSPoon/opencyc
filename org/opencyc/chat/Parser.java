package org.opencyc.chat;

import java.io.IOException;
import java.net.UnknownHostException;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

/**
 * Provides a chat conversation parser.<p>
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

public class Parser {

    /**
     * the Cyc server connection and API wrapper
     */
    CycAccess cycAccess;

    /**
     * Constructs a new Parser object given a CycAccess connection.
     *
     * @param cycAccess the Cyc server connection and API wrapper
     */
    public Parser(CycAccess cycAccess) {
        this.cycAccess = cycAccess;
    }

    /**
     * Parses the given user input string and returns the equivalent
     * SubL command.
     *
     * @param userInput the given user input string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    public Object[] parseUserInput (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        if (userInput.toLowerCase().startsWith("create "))
            return parseCreateCommand(userInput);
        if (userInput.toLowerCase().startsWith("rename "))
            return parseRenameCommand(userInput);
        if (userInput.toLowerCase().startsWith("kill "))
            return parseKillCommand(userInput);
        if (userInput.toLowerCase().startsWith("assert "))
            return parseAssertCommand(userInput);
        if (userInput.toLowerCase().startsWith("unassert "))
            return parseUnassertCommand(userInput);
        if (userInput.toLowerCase().startsWith("ask "))
            return parseAskCommand(userInput);
        if (userInput.toLowerCase().startsWith("summarize "))
            return parseSummarizeCommand(userInput);
        if (userInput.toLowerCase().startsWith("help"))
            return parseHelpCommand(userInput);
        answer[1] = "I do not understand this command.  Try help.";
        return answer;
    }

    /**
     * Parses the create command and returns the equivalent
     * SubL command.
     * @param userInput the given create command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseCreateCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        String text = userInput.substring(6);
        int index = 0;
        while (true) {
            if (index >= text.length()) {
                answer[0] = "The constant name is not properly quoted.";
                return answer;
            }
            if (text.charAt(index) == '"') {
                break;
            }
            if (text.charAt(index) == ' ') {
                index++;
                continue;
            }
            answer[0] = "The constant name is not properly quoted.";
            return answer;
        }
        StringBuffer constantNameBuffer = new StringBuffer();
        if (index < text.length() - 1)
            text = text.substring(index + 1);
        index = 0;
        while (true) {
            if (index >= text.length()) {
                answer[0] = "The constant name is not properly quoted.";
                return answer;
            }
            if (text.charAt(index) == '"') {
                break;
            }
            constantNameBuffer.append(text.charAt(index));
        }
        String constantName = constantNameBuffer.toString();
        String script = "(cyc-create \"" + constantName + "\")";
        CycList createCommand = cycAccess.makeCycList(script);
        return answer;
    }

    /**
     * Parses the rename command and returns the equivalent
     * SubL command.
     * @param userInput the given rename command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseRenameCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        String text = userInput.substring(6);
        int index = 0;
        while (true) {
            if (index >= text.length()) {
                answer[0] = "I do not recognize the constant to be renamed.";
                return answer;
            }
            if (text.charAt(index) == '#') {
                break;
            }
            if (text.charAt(index) == ' ') {
                index++;
                continue;
            }
            answer[0] = "I do not recognize the constant to be renamed.";
            return answer;
        }
        StringBuffer constantNameBuffer = new StringBuffer();
        text = text.substring(index);
        index = 0;
        while (true) {
            if (index >= text.length()) {
                answer[0] = "I do not recognize the new constant name.";
                return answer;
            }
            if (text.charAt(index) == '"') {
                break;
            }
            constantNameBuffer.append(text.charAt(index));
        }
        String constantName = constantNameBuffer.toString();
        String script = "(cyc-create \"" + constantName + "\")";
        CycList createCommand = cycAccess.makeCycList(script);
        return answer;
    }

    /**
     * Parses the kill command and returns the equivalent
     * SubL command.
     * @param userInput the given kill command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseKillCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        return answer;
    }

    /**
     * Parses the assert command and returns the equivalent
     * SubL command.
     * @param userInput the given assert command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseAssertCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        return answer;
    }

    /**
     * Parses the unassert command and returns the equivalent
     * SubL command.
     * @param userInput the given unassert command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseUnassertCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        return answer;
    }

    /**
     * Parses the ask command and returns the equivalent
     * SubL command.
     * @param userInput the given ask command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseAskCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        return answer;
    }

    /**
     * Parses the summarize command and returns the equivalent
     * SubL command.
     * @param userInput the given summarize command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseSummarizeCommand (String userInput)
        throws CycApiException, IOException, UnknownHostException {
        Object[] answer = {null, null};
        return answer;
    }

    /**
     * Parses the help command and returns the equivalent
     * SubL command.
     * @param userInput the given help command string
     * @return an object array whose first element is
     * the equivalent SubL command and whose second
     * element is the error message
     */
    protected Object[] parseHelpCommand (String userInput) {
        Object[] answer = {null, null};
        answer[0] =
                "I understand these commands:\n" +
                "create <constant name>.\n" +
                "rename <constant> to <new constant name>.\n" +
                "kill <fort>.\n" +
                "assert <sentence> in <mt>.\n" +
                "assert <sentence>.\n" +
                "unassert <sentence> in <mt>.\n" +
                "unassert <sentence>.\n" +
                "ask <query> in <mt>.\n" +
                "ask <query>.\n" +
                "summarize <fort>.";
        return answer;
    }
}