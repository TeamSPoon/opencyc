package org.opencyc.cycagent.fipaos;

import java.io.IOException;
import javax.naming.TimeLimitExceededException;
import fipaos.ont.fipa.*;
import org.opencyc.cycagent.*;
import org.opencyc.util.Timer;

/**
 * Provides the interface for interacting with the FIPA-OS agent community.<p>
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

public class FipaOsCommunityAdapter implements AgentCommunityAdapter {

    /**
     * outbound message serial number.
     */
    public int msgSerialNumber = 0;

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * name of my agent
     */
    protected String agentName;

    /**
     * Constructs a new FipaOsCommunityAdapter object given my agent's name.
     */
    public FipaOsCommunityAdapter(String agentName) {
        this.agentName = agentName;
    }

    /**
     * Sends an Agent Communication Language message.
     *
     * @param acl the Agent Communication Language message to be sent
     */
    public void sendMessage (ACL acl) {
    }

    /**
     * Notifies my agent that an Agent Communication Language message has been received.
     *
     * @param acl the Agent Communication Language message which has been received for my agent
     */
    public void messageReceived (ACL acl) {
    }

    public static final long WAIT_FOREVER = Long.MAX_VALUE;

    /**
     * Sends an Agent Communication Language message and returns the reply.
     *
     * @param acl the Agent Communication Language message to be sent
     * @param timer a Timer object contolling the maximum wait time for a reply message,
     * after which an excecption is thrown.
     * @return the Agent Communication Language reply message which has been received for my agent
     *
     * @thows TimeLimitExceededException when the time limit is exceeded before a reply message
     * is received.
     */
    public ACL converseMessage (ACL acl, Timer timer)
        throws TimeLimitExceededException, IOException {
        return acl;
    }

    /**
     * Returns the next message serial number identifier.
     *
     * @return the next message serial number identifier
     */
    public String nextMessageId () {
        return "message" + ++msgSerialNumber;
    }

    /**
     * De-register this agent.
     */
    public void deregister() {
    }

    /**
     * Terminate this agent.
     */
    public void terminate() {
    }
}