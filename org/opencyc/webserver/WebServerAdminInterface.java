package org.opencyc.webserver;

/**
 * The WebServerAdminInterface provides facilities for clients to obtain services and events
 * from the WebServer service, subject to leases.<p>
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
import net.jini.core.lease.*;
import java.util.*;
import java.io.*;
import org.opencyc.jini.shared.*;

public interface WebServerAdminInterface extends GenericServiceInterface {
    /**
     * Gets the list of directories from which files are served by the web server.
     *
     * @param lease the lease argument is provided by the client
     * @return The list of directories from which files are served by the web server.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
   public ArrayList getDirs()
        throws RemoteException;

    /**
     * Updates the list of directories from which files are served by the web server.
     *
     * @param dirs The list of directories from which files are served by the web server.
     * @exception IOException if an file processing problem occurs.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
      */
    public void setDirs(ArrayList dirs)
        throws RemoteException, IOException;

    /**
     * Administrative accessor method that obtains number of files served.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public long getNbrFilesServed() throws RemoteException;

    /**
     * Administrative accessor method that obtains number of files served from cache.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public long getNbrCacheHits() throws RemoteException;

    /**
     * Administrative method that clears the file cache.
     * @exception RemoteException if a communications-related problem occurs while processing
     * the remote method call.
     */
    public void clearFileCache() throws RemoteException;
}
