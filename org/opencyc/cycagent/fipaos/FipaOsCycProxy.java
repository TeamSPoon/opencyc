package org.opencyc.cycagent.fipaos;

import org.opencyc.cycagent.*;

/**
 * Provides a proxy for a cyc agent on the FIPA-OS agent community.<p>
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

public class FipaOsCycProxy {

    /**
     * the CycProxy instance.
     */
    CycProxy cycProxy;

    /**
     * Constructs a FipaOsCycProxy object.
     *
     * @param myAgentName name of the local agent
     * @param verbosity the verbosity of this agent adapter's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input
     */
    public FipaOsCycProxy (String myAgentName, int verbosity) {
        cycProxy = new CycProxy(myAgentName,
                                AgentCommunityAdapter.FIPA_OS_AGENT_COMMUNITY,
                                verbosity);
    }

    /**
     * Provides the main method.
     */
    public static void main(String[] args) {
        if(args == null || args.length < 2) {
            System.out.println("Usage: java org.opencyc.cycagent.fipaos.FipaOsCycProxy <platform.profile> <name>" );
            return;
        }
        FipaOsCommunityAdapter.platform_profile = args[0];
        FipaOsCycProxy fipaOsCycProxy =
             new FipaOsCycProxy(args[1], AgentCommunityAdapter.MAX_VERBOSITY);
        fipaOsCycProxy.cycProxy.initializeAgentCommunity();
        while (true)
            // Keep root thread running with minimal resource consumption, while awaiting
            // cyc api requests.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }
        fipaOsCycProxy.cycProxy.getFipaOsCommunityAdapter().deregister();
        System.exit(0);
    }

}