package  org.opencyc.cycagent;

import  java.net.Socket;
import  java.io.PrintWriter;
import  java.io.*;
import  java.util.Collection;
import  fipaos.ont.fipa.*;
import  fipaos.parser.*;
import  org.opencyc.api.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.util.*;

/**
 * Provides a handler to read cyc image input and write message to the agent
 * message queue.  The handler closes its socket without reply after the message
 * is processed.<p>
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

class CycInputHandler implements Runnable {

    /**
     * The default verbosity of the solution output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

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
        Object fipaTransportMessage = null;
        try {
            fipaTransportMessage = cfaslInputStream.readObject();
        }
        catch (Exception e) {
            Log.current.println("Exception reading socket i/o: " + e);
            return;
        }
        if (! (fipaTransportMessage instanceof CycList)) {
            Log.current.println(fipaTransportMessage + "\nis not a CycList");
            throw new RuntimeException(fipaTransportMessage + "\nis not a CycList");
        }
        if (((CycList) fipaTransportMessage).size() != 3) {
            Log.current.println(fipaTransportMessage + "\nhas invalid length of " +
                                ((CycList) fipaTransportMessage).size());
            throw new RuntimeException(fipaTransportMessage + "\nhas invalid length of " +
                                ((CycList) fipaTransportMessage).size());
        }
        if (! (((CycList) fipaTransportMessage).first().equals(CycObjectFactory.makeCycSymbol("FIPA-ENVELOPE")))) {
            Log.current.println("Invalid cyc message directive " + fipaTransportMessage);
            throw new RuntimeException("Invalid cyc message directive " + fipaTransportMessage);
        }

        Object envelope = ((CycList) fipaTransportMessage).second();
        if (! (envelope instanceof CycList)) {
            Log.current.println(envelope + "\nis not a CycList");
            throw new RuntimeException(envelope + "\nis not a CycList");
        }
        Object payload = ((CycList) fipaTransportMessage).third();
        if (! (envelope instanceof CycList)) {
            Log.current.println(envelope + "\nis not a CycList");
            throw new RuntimeException(envelope + "\nis not a CycList");
        }



/*
        if (verbosity > 0)
            Log.current.println("Processing the message without waiting for the reply");
        String aclString = ((CycList) cycRequest.second()).cyclify();
        Log.current.println("aclString: " + aclString);
        ACL acl = null;
        try {
            acl = new ACL(aclString);
            AgentManager.agentManager.getAgentCommunityAdapter().converseMessage(acl, new Timer());
        }
        catch (ParserException e) {
            Log.current.println("Cannot parse message " + aclString + e.getMessage());
        }
        catch (Exception e) {
            Log.current.println("Exception when sending\n" + acl + "\n" + e.getMessage());
        }
        close();
        */
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

    /**
     * Sets verbosity of the output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
}






