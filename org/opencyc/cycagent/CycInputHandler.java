package  org.opencyc.jini.cycproxy;

/**
 * Provides a handler to read cyc image input and write message to the agent
 * message queue.  The cyc image closes its socket after the message is processed.<p>
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
import  java.net.Socket;
import  java.io.PrintWriter;
import  java.io.*;
import  java.util.Collection;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;


class CycInputHandler implements Runnable {
    /**
     * Automatically flush the PrintWriter object.
     */
    protected static final boolean AUTOFLUSH = true;

    /**
     * Reference to parent's socket which is connected to a cyc client.
     */
    protected Socket socket = null;

    /**
     * The binary interface input stream</tt>.
     */
    protected CfaslInputStream cfaslInputStream;

    /**
     * The binary interface output stream</tt>.
     */
    protected CfaslOutputStream cfaslOutputStream;

    /**
     * No api trace.
     */
    public static final int API_TRACE_NONE = 0;

    /**
     * Message-level api trace.
     */
    public static final int API_TRACE_MESSAGES = 1;

    /**
     * Detailed api trace.
     */
    public static final int API_TRACE_DETAILED = 2;

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server.
     */
    protected int trace = API_TRACE_NONE;

    /**
     * Agent Communication Language (e.g. "amp")
     */
    private String acl = null;

    /**
     * Constructs a new CycInputHandler object given a reference to
     * the cyc client's socket connection.
     */
    public CycInputHandler (Socket clientSocket) {
        this.socket = clientSocket;
    }

    /***
     * Executes the CycInputHandler thread.
     */
    public void run () {
        Log.current.println("Begin CycInputHandler thread");
        try {
            cfaslInputStream = new CfaslInputStream(socket.getInputStream());
            cfaslInputStream.trace = trace;
            cfaslOutputStream = new CfaslOutputStream(socket.getOutputStream());
            cfaslOutputStream.trace = trace;
        }
        catch (Exception e) {
            Log.current.println("Exception creating socket i/o: " + e);
            throw new RuntimeException("Exception creating socket i/o: " + e);
        }
        Object cycRequestObject = null;
        try {
            cycRequestObject = cfaslInputStream.readObject();
            if (! (cycRequestObject instanceof CycList)) {
                Log.current.println(cycRequestObject + " is not a CycList");
                throw new RuntimeException(cycRequestObject + " is not a CycList");
            }
            CycList cycRequest = (CycList) cycRequestObject;
        }
        catch (Exception e) {
            Log.current.println("Exception reading socket i/o: " + e);
            return;
        }
        CycList cycRequest = (CycList) cycRequestObject;
        if (! (cycRequest.first() instanceof CycSymbol)) {
            Log.current.println("Invalid cyc message directive " + cycRequestObject);
            throw new RuntimeException("Invalid cyc message directive " + cycRequestObject);
        }
        CycSymbol cycMessageDirective = (CycSymbol) cycRequest.first();
        if (cycMessageDirective.equals(CycObjectFactory.makeCycSymbol("CYC-TO-GRID-NOWAIT"))) {
            Log.current.println("Processing the message without waiting for the reply");
            String ampString = ((CycList) cycRequest.second()).cyclify();
            Log.current.println("ampString: " + ampString);
            // Register the sender of the message.
            Amp amp = new Amp(ampString);
            // Place message on agent message queue and find a handler for it.
            AgentManager.agentMessageQueue.add(amp);
            AgentManager.dispatchAgentMessageQueue();
        }
        else {
            Log.current.println("Invalid cyc message directive " + cycRequestObject);
            throw new RuntimeException("Invalid cyc message directive " + cycRequestObject);
        }
    }

    /**
     * Replies to the awaiting cyc client.
     *
     * @param message the response message.
     */
    public void write(Object message) {
        try {
            cfaslOutputStream.writeObject(message);
        }
        catch (Exception e) {
            Log.current.println("Exception writing socket i/o: " + e);
        }
    }

    /**
     * Closes the socket
     */
    public void close () {
        try {
            socket.close();
        }
        catch (Exception e) {
            Log.current.println("Exception closing socket i/o: " + e);
        }
    }

}



