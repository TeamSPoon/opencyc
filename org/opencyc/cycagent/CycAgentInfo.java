package  org.opencyc.cycagent;

/**
 * Contains the attributes of a cyc agent.
 * Instances are stored in the cycAgents and cycImages hashtables. More than one agent
 * may be hosted in the Cyc executable which communicates with the agent manager via one
 * CycProxy object per agent.
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

class CycAgentInfo {

    /**
     * The tcp port from which the asciiPort and cfaslPorts are derived.
     */
    public int basePort;

    /**
     * The unique name of the Cyc image which hosts the agent.
     */
    public String cycImageId;

    /**
     * The CycProxy object which manages connections to the Cyc image and connections to the
     * agent community for this agent.
     */
    public CycProxy cycProxy;

    /**
     * Constructs a new AgentInfo container object given its contents.
     *
     * @param basePort tcp port from which the asciiPort and cfaslPorts are derived
     * @param cycImageId unique name of the Cyc image which hosts the agent
     * @param cycProxy
     */
    public CycAgentInfo (int basePort, String cycImageId, CycProxy cycProxy) {
        this.basePort = basePort;
        this.cycImageId = cycImageId;
        this.cycProxy = cycProxy;
    }
}








