package org.opencyc.javashell;

/**
 * Description TODO
 *
 * @version $Id$
 * @author Douglas R. Miles
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.lang.Thread;


public class CycJavaServer extends Thread {
    private ServerSocket serverSocket = null;
    private HashMap allShells = new HashMap();
    public CycJavaServer(CycJavaShell jshell, int port) throws IOException {
	this.jshell = jshell;
	allShells.add("localhost",jshell);
	serverSocket = new ServerSocket(port);
    }
    public void run() {
	while( !this.interrupted() ) {
	    try {
		Socket clientSock = new CycJavaClient(serverSocket.accept();
		String clientKey = clientSock.getInetAddress();
		CycJavaShell clientJshell = allShells.get(clientKey);
		if(clientJshell==null) {
		    cycAccess = new CycAccess(client.getInetAddress().getHostAddress(),
					      CycConnection.DEFAULT_BASE_PORT,CycConnection.DEFAULT_COMMUNICATION_MODE,CycAccess.DEFAULT_CONNECTION);                   
		    clientJshell = new CycJavaShell();
		    clientJshell.ensureClientSupportsShell(cycAccess);
		    allShells.put(clientKey,clientJshell);
		}
		CycJavaClient client = CycJavaClient(clientSock,clientJshell);
		client.start();
	    } catch( Exception e ) {
		e.printStackTrace();
	    }
	}

    }

}

