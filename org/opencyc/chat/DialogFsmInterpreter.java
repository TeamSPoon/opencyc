package org.opencyc.chat;

/**
 * Contains the attributes and behavior of a chat dialog interpreter.<p>
 *
 * The chat dialog is in the form of a text conversation using
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

public class DialogFsmInterpreter {

    /**
     * reference to the parent ChatterBot
     */
    protected ChatterBot chatterBot;

    /**
     * finite state machine current node
     */
    protected DialogFsmNode dialogFsmCurrentNode;

    /**
     * Constructs a new DialogFsmInterpreter object given the parent
     * ChatterBot.
     *
     * @param chatterBot the parent ChatterBot
     */
    public DialogFsmInterpreter(ChatterBot chatterBot) {
        this.chatterBot = chatterBot;
    }

    /**
     * Initializes the fsm interpreter
     */
    public void initialize () {
    }

    /**
     * Sets the current state of the fsm interpreter.  Used to set the initial
     * state only.
     *
     * @param dialogFsmCurrentNode the current state
     */
    public void setDialogFsmCurrentNode (DialogFsmNode dialogFsmCurrentNode) {
        this.dialogFsmCurrentNode = dialogFsmCurrentNode;
    }

}