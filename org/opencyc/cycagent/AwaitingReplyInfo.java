package  org.opencyc.cycagent;

/**
 * Contains information required to route a response message back to the local
 * client which originated the request.  The LocalClientSocketReaderHandler object
 * holds the open socket until the response is processed.
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

public class AwaitingReplyInfo {

    /**
     * Receipient to which the response message will be sent.
     */
    public String recipient;

    /**
     * the In-reply-to message attribute
     */
    public String inReplyTo;

    /**
     * Reference to the client's open socket from which the request message
     * originated.
     */
    public CycInputHandler cycInputHandler;

    /**
     * Creates a new AwaitingReplyInfo object.
     *
     * @param receipient the local client agent to which the response message will be sent
     * @param inReplyTo the In-reply-to message attribute
     * @param localClientSocketReaderHandler contains the client's open socket from which the
     * request message originated
     */
    public AwaitingReplyInfo(String recipient,
                             String inReplyTo,
                             CycInputHandler cycInputHandler) {
        this.recipient = recipient;
        this.inReplyTo = inReplyTo;
        this.cycInputHandler = cycInputHandler;
    }

    /**
     * Returns a string representation of an AwaitingReplyInfo object.
     *
     * @return a string representation of an AwaitingReplyInfo object
     */
    public String toString() {
        StringBuffer result = new StringBuffer("AwaitingMessage: ");
        result.append(recipient);
        result.append("\nin-reply-to ");
        result.append(inReplyTo);
        result.append("\nat ");
        result.append(cycInputHandler.socket);
        return result.toString();
    }

    /**
     * Returns <tt>true</tt> some object equals this <tt>AwaitingReplyInfo</tt> object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof AwaitingReplyInfo))
            return false;
        else
            return ((AwaitingReplyInfo) object).recipient.equals(this.recipient) &&
                   ((AwaitingReplyInfo) object).inReplyTo.equals(this.inReplyTo);
    }

}