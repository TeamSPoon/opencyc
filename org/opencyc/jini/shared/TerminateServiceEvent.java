package org.opencyc.jini.shared;

/**
 * Implements an unsolicited terminate service event sent from the service agent to
 * the client agent.<p>
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


import java.rmi.*;
import net.jini.core.event.*;

/**
 * An unsolicited terminate service event sent from the service agent to the client agent.
 */
public class TerminateServiceEvent extends RemoteEvent {

    /**
     * An identifier distinguishing this event type from others offered by the service.
     */
    public final static int ID = 1;

    /**
     * Constructs a ConvertEvent object.
     *
     * @param source an object reference to the source of the event.
     * @param sequenceNumber an event sequence number, guaranteed to be ascending by the agent.
     * @param handback An object provided by the client to the service at the time of registration.
     */
    public TerminateServiceEvent(Object source, long sequenceNumber, MarshalledObject handback) {
        super(source, ID, sequenceNumber, handback);
    }

}