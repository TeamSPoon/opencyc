package  org.opencyc.cycagent;

/**
 * Provides a a listener for input connections from Cyc images.<p>
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
import  java.net.ServerSocket;
import  java.net.Socket;
import  java.io.*;
import  org.opencyc.util.*;


class CycListener implements Runnable {

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
     * The socket which listens for new connections.
     */
    protected ServerSocket listenerSocket = null;
    /**
     * Well known port where the Agent Manager listens for requests from cyc clients.
     */
    public static final int LOCAL_CLIENT_LISTENER_PORT = 4444;

    /**
     * Maximum number of local cyc clients supported by this listener.
     */
    public static final int MAX_LOCAL_CLIENT_CLIENTS = 50;

    /**
     * A flag to keep the localClient port listener looping until interrupted.
     */
    protected boolean localClientKeepRunning = true;


    /**
     * Constructs a new LocalClientPortListener object.
     */
    public CycListener () {
    }

    /**
     * Runs the CycListener thread.
     */
    public void run () {
        try {
            listenerSocket = new ServerSocket(LOCAL_CLIENT_LISTENER_PORT, MAX_LOCAL_CLIENT_CLIENTS);
            while (localClientKeepRunning) {
                if (verbosity > 2)
                    Log.current.println("Listening on port " + LOCAL_CLIENT_LISTENER_PORT);
                Socket cycSocket = listenerSocket.accept();
                if (verbosity > 2)
                    Log.current.println("Cyc Connection accepted " + cycSocket);
                // Spawn child thread to read from the socket.
                CycInputHandler cycInputHandler =
                    new CycInputHandler(cycSocket);
                Thread cycInputHandlerThread = new Thread(cycInputHandler);
                cycInputHandlerThread.start();
            }
        }
        catch (IOException e) {
            Log.current.println("Failed I/O: " + e);
            System.exit(1);
        }
        finally {
            try {
                listenerSocket.close();
            }
            catch (IOException e) {
            }
        }
    }

    /**
     * Sets verbosity of the constraint solver output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
}






