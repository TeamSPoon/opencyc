package org.opencyc.cycagent.coabs;

import org.opencyc.cycagent.AgentCommunityAdapter;
import org.opencyc.cycagent.CycProxy;

/**
 * Provides a proxy for a cyc agent on the CoABS grid agent community.<p>
 *
 * The package org.opencyc.cycagent.coabs is an optional package for OpenCyc.  If the
 * developer does not have access to the CoABS grid classes from Global Infotek, then
 * the org.opencyc.cycagent.coabs package can be ommitted from the build.  The FIPA-OS
 * agent community is freely available as open source and OpenCyc can be configured to
 * work with it alone, or in combination with CoABS when available.<p>
 *
 * An instance of this class is created for each unique cyc agent which makes
 * itself known to the agent manager.  A cyc image can host one or more cyc
 * agents.  Each message envelope from a cyc agent contains a parameter to
 * indicate which agent agent community processes the messge - either the CoABS
 * grid (Darpa & gov) or the FIPA-OS platform (OpenCyc).
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

public class CoAbsCycProxy {

    /**
     * the CycProxy instance.
     */
    CycProxy cycProxy;

    /**
     * Constructs a CoAbsCycProxy object.
     *
     * @param myAgentName name of the local agent
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public CoAbsCycProxy (String myAgentName, int verbosity) {
        cycProxy = new CycProxy(myAgentName,
                                AgentCommunityAdapter.COABS_AGENT_COMMUNITY,
                                verbosity);
    }

    /**
     * Provides the main method.
     */
    public static void main(String[] args) {
        CoAbsCycProxy coAbsCycProxy =
            new CoAbsCycProxy("Agent1", AgentCommunityAdapter.QUIET_VERBOSITY);
        coAbsCycProxy.cycProxy.initializeAgentCommunity();
        while (true)
            // Keep root thread running with minimal resource consumption, while awaiting
            // cyc api requests.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }
        coAbsCycProxy.cycProxy.getCoAbsCommunityAdapter().deregister();
        System.exit(0);
    }

}