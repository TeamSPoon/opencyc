package  org.opencyc.cycagent;

/**
 * Contains the attributes and behavior of an agent message queue.
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

import  java.util.Vector;
import  java.util.Enumeration;
import  org.opencyc.util.Amp;

class AgentMessageQueue {
    protected static Vector ampMessages = null;

    /**
     * Constructs an AgentMessageQueue object.
     */
    public AgentMessageQueue () {
        ampMessages = new Vector();
    }

    /**
     * Adds an agent message queue item from a string.
     *
     * @param message the message which is added to the agent's message queue
     */
    public synchronized void add (String message) {
        ampMessages.add(new Amp(message));
    }

    /**
     * Adds an agent message queue item from an AMP message.
     *
     * @param amp the message which is added to the agent's message queue
     */
    public synchronized void add (Amp amp) {
        ampMessages.add(amp);
    }

    /**
     * Returns an enumeration of the AMP messages.
     *
     * @return an enumeration of the AMP messages
     */
    public synchronized Enumeration elements () {
        return  ampMessages.elements();
    }

    /**
     * Returns and removes the first AMP message.
     *
     * @return the first AMP message from the queue
     */
    public synchronized Amp removeFirst () {
        if (ampMessages.isEmpty())
            return  null;
        Amp amp = (Amp)ampMessages.firstElement();
        ampMessages.remove(0);
        return  amp;
    }

    /**
     * Returns the receiver type with regard to message routing.<p>
     *
     * "local" = agent manager method.<p>
     * "cyc"   = route to a cyc socket.<p>
     * "grid"  = route to the Grid.<p>
     *
     * @param amp the message
     * @return the receiver type with regard to message routing
     */
    public static String receiverType (Amp amp) {
        String receiver = amp.receiver();
        return  "amp";
    }

    /**
     * Creates an example agent message queue item.
     */
    public static Amp example () {
        AgentMessageQueue agentMessageQueue = new AgentMessageQueue();
        String exampleMessage = "(evaluate\n" +
                                "  :sender cyc-image-initialization-DOUBLE-MOUNTAIN-6920-19991129101956\n" +
                                "  :receiver HttpPortAllocator\n" +
                                "  :content (obtain-http-port-number)\n" +
                                "  :ontology cyc-api\n" +
                                "  :language cyc-api\n" +
                                "  :reply-with t\n" +
                                "  :in-reply-to null)";
        agentMessageQueue.add(exampleMessage);
        return  agentMessageQueue.removeFirst();
    }
}






