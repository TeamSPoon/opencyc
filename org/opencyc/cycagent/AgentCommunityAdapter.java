package org.opencyc.cycagent;

import java.io.IOException;
import javax.naming.TimeLimitExceededException;
import fipaos.ont.fipa.*;
import org.opencyc.util.Timer;

/**
 * Defines the interface for interacting with an agent community such as CoABS or FIPA-OS.<p>
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

public interface AgentCommunityAdapter {

    /**
     * Quiet verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int QUIET_VERBOSITY = 0;

    /**
     * Low verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int LOW_VERBOSITY = 1;

    /**
     * Medium verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int MEDIUM_VERBOSITY = 3;

    /**
     * Maximum verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int MAX_VERBOSITY = 9;

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = LOW_VERBOSITY;

    /**
     * Indicates the CoABS agent community.
     */
    public static final int COABS_AGENT_COMMUNTITY = 1;

    /**
     * Indicates the FIPA-OS agent community.
     */
    public static final int FIPA_OS_AGENT_COMMUNTITY = 2;

    /**
     * Indicates the cyc-api ontology (role).
     */
    public static final String CYC_API_ONTOLOGY = "cyc-api";

    /**
     * Indicates the cyc-echo ontology (role).
     */
    public static final String CYC_ECHO_ONTOLOGY = "cyc-echo";

    /**
     * Sends an Agent Communication Language message.
     *
     * @param acl the Agent Communication Language message to be sent
     */
    public void sendMessage (ACL acl) throws IOException;

    /**
     * Sends an Agent Communication Language message and returns the reply.
     *
     * @param acl the Agent Communication Language message to be sent
     * @param timer the Timer object controlling the maximum wait time for a reply message,
     * after which an excecption is thrown.
     * @return the Agent Communication Language reply message which has been received for my agent
     *
     * @thows TimeLimitExceededException when the time limit is exceeded before a reply message
     * is received.
     */
    public ACL converseMessage (ACL acl, org.opencyc.util.Timer timer)
        throws TimeLimitExceededException, IOException;

    /**
     * Returns the next message serial number identifier.
     *
     * @return the next message serial number identifier
     */
    public String nextMessageId ();

    /**
     * De-register this agent.
     */
    public void deregister();

    /**
     * Terminate this agent.
     */
    public void terminate();

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity);

}