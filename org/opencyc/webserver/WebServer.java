package org.opencyc.webserver;

/**
 * Provides the behavior and attributes of a web server for OpenCyc.<p>
 * <p>
 * Class WebServer is simple multithreaded HTTP server
 * with CGI limited to a Cyc connection on default port 3600.
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

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.text.*;
import org.opencyc.util.*;

public class WebServer extends Thread {
    /**
     * Singleton WebServer instance.
     */
    public static WebServer current;

    /**
     * Default HTTP port.
     */
    protected static int DEFAULT_PORT = 80;

    /**
     * Default Cyc base port.
     */
    protected static int DEFAULT_CYC_PORT = 3600;

    /**
     * Default directory to serve files from on non-Windows OS.
     */
    protected static String DEFAULT_DIR = "/";

    /**
     * Default directory to serve files from on Windows.
     */
    protected static String DEFAULT_WIN_DIR = "C:\\";

    /**
     * File cache capacity.
     */
    protected static final int CACHE_CAPACITY = 100;

    /**
     * File cache to improve file serving performance.
     */
    protected static Hashtable fileCache = new Hashtable(CACHE_CAPACITY);

    /**
     * Number of files served from this web server.
     */
    protected static long nbrFilesServed = 0;

    /**
     * Number of files served from this web server that were found in the cache.
     */
    protected static long nbrCacheHits = 0;

    /**
     * Server socket for accepting connections.
     */
    protected ServerSocket server;

    /**
     * Directories to serve files from.
     */
    protected ArrayList dirs;

    /**
     * Map from String (jar root) to JarFile[] (jar class path).
     */
    protected HashMap map;

    /**
     * Webserver HTTP port.
     */
    protected int port;

    /**
     * Cyc HTML port.
     */
    protected int cycPort;

    /**
     * Expand jar tress.
     */
    protected boolean trees;

    /**
     * Requests flag.
     */
    protected boolean traceRequests;

    /**
     * Constructs a WebServer object.
     *
     * @param port the port to use
     * @param directories the directory to serve files from
     * @param trees true if files within jar files should be served up
     * @param traceRequests true if client's request text should be logged.
     * @exception IOException if the listening socket cannot be opened, or problem opening jar files.
     */
    public WebServer() throws IOException {
        getProperties();
        server = new ServerSocket(port);
        processDirectories();
        WebServerAdmin.makeWebServerAdmin(this);
    }

    /**
     * Class Task processes a single HTTP request.
     */
    protected class Task extends Thread {
        /**
         * Socket for the incoming request.
         */
        protected Socket sock;

        /**
         * Client socket to the Cyc KB HTML server.
         */
        protected Socket cycHtmlSocket;

        /**
         * Output tcp stream.
         */
        protected DataOutputStream out;

        /**
         * Contains the file request path for a not-found error message.
         */
        protected String notFoundPath;

        /**
         * Contains the first line of a request message.
         */
        protected String methodLine;

        /**
         * Contains the body of a POST method.
         */
        protected String bodyLine;

        /**
         * Constructs a Task object.
         * @param sock the socket assigned for this request.
         */
        public Task(Socket sock) {
            this.sock = sock;
        }

        /**
         * Processes the HTTP request.
         */
        public void run() {
            if (traceRequests)
                Log.current.println("connection accepted from " + sock.getInetAddress());
            notFoundPath = "";
            try {
                out = new DataOutputStream(sock.getOutputStream());
                try {
                    getBytes();
                }
                catch (Exception e) {
                    Log.current.println("file not found: " + notFoundPath);
                    try {
                        out.writeBytes("HTTP/1.1 404 Not Found\r\n");
                        out.writeBytes("Server: Cyc WebServer\r\n");
                        out.writeBytes("Connection: close\r\n");
                        out.writeBytes("Content-Type: text/html\r\n\r\n");
                        out.writeBytes("<HTML><HEAD>\n");
                        out.writeBytes("<TITLE>404 Not Found</TITLE>\n");
                        out.writeBytes("</HEAD><BODY>\n");
                        out.writeBytes("<H1>404 - Not Found</H1>\n");
                        out.writeBytes("</BODY></HTML>");
                        out.flush();
                    }
                    catch (SocketException se) {
                    }
                }
            }
            catch (Exception e) {
                Log.current.printStackTrace(e);
            }
            finally {
                try {
                    sock.close();
                }
                catch (IOException e) {
                }
            }
        }

        /**
         * Reads the HTTP request and obtains the response.
         * @exception IOException when HTTP request has an invalid format.
         */
        private void getBytes() throws IOException {

            // Below logic is complex because web browsers do not close the
            // socket after sending the request, so must parse message to find
            // the end.
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            ArrayList inBytes = new ArrayList(200);
            int ch = 0;
            boolean postMethod;
            methodLine = in.readLine();
            //if (traceRequests)
            //    Log.current.println("methodLine=" + methodLine);
            bodyLine = "";
            if (methodLine.startsWith("POST /"))
                postMethod = true;
            else
                postMethod = false;
            //if (traceRequests)
            //    Log.current.println("postMethod=" + postMethod);

            int ch1 = -1;
            int ch2 = -1;
            int ch3 = -1;
            int ch4 = -1;

            // Read the HTTP request headers.
            while (true) {
                ch = in.read();
                inBytes.add(new Integer(ch));
                ch1 = ch2;
                ch2 = ch3;
                ch3 = ch4;
                ch4 = ch;
                if (ch1 == '\r' && ch2 == '\n' && ch3 == '\r' && ch4 == '\n')
                    break;
                if ((! postMethod) &&
                    (! in.ready()) &&
                    ch1 == -1 &&
                    ch2 == -1 &&
                    ch3 == '\r' &&
                    ch4 == '\n') {
                    inBytes.add(new Integer('\r'));
                    inBytes.add(new Integer('\n'));
                    break;
                }
            }
            byte[] byteArray = new byte[inBytes.size()];
            for (int i = 0; i < inBytes.size(); i++) {
                Integer ich = (Integer) inBytes.get(i);
                byteArray[i] = ich.byteValue();
            }
            String headers = new String(byteArray);
            if (postMethod) {
                String lcHeaders = headers.toLowerCase();
                int i = lcHeaders.indexOf("content-length: ");
                String contentLength = lcHeaders.substring(i + 16);
                int j = contentLength.indexOf("\r\n");
                contentLength = contentLength.substring(0, j);
                int bodyLen = (new Integer(contentLength)).intValue();
                for (int k = 0; k < bodyLen; k++) {
                    bodyLine = bodyLine + (new Character((char) in.read())).toString();
                }
            }
            String line = methodLine + "\r\n" + headers + bodyLine;
            if (traceRequests)
                Log.current.println(line);
            if (postMethod)
                processHttpPost();
            else
                if (line.startsWith("GET /"))
                    processHttpGet(line.substring(4));
                else {
                    Log.current.println("Invalid request = " + line);
                    throw new IOException();
                }
        }

        /**
         * Processes an HTTP GET method.
         * @param httpGetPath the path of the file to get.
         * @exception IOException if the file is not found.
         */
        private void processHttpGet(String httpGetPath) throws IOException {
            int i = httpGetPath.indexOf(' ');
            if (i > 0)
                httpGetPath = httpGetPath.substring(0, i);
            Log.current.println(methodLine + " from " + sock.getInetAddress().getHostName());
            i = httpGetPath.indexOf("cg?");
            if (i > 0) {
                cycHtmlRequest(httpGetPath.substring(i + 3));
                return;
            }
            notFoundPath = httpGetPath;
            i = httpGetPath.indexOf('/');
            if (i < 0 || map == null) {
                if (map == null || httpGetPath.endsWith(".jar")) {
                    for (int j = 0; j < dirs.size(); j++) {
                        String dir = (String) dirs.get(j);
                        String nativePath = dir + httpGetPath.replace('/', File.separatorChar);
                        if (fileCache.containsKey(nativePath)) {
                            writeDataBytes((byte[]) fileCache.get(nativePath));
                            Log.current.println("...cached");
                            nbrCacheHits++;
                            nbrFilesServed++;
                            return;
                        }
                        try {
                            File f = new File(nativePath);
                            byte[] fileBytes = getBytes(new FileInputStream(f), f.length());
                            writeDataBytes(fileBytes);
                            if (fileCache.size() >= CACHE_CAPACITY)
                                fileCache.clear();
                            fileCache.put(nativePath, fileBytes);
                            Log.current.println("...from " + nativePath);
                            nbrFilesServed++;
                            return;
                        }
                        catch (IOException e) {
                        }
                    }
                }
                throw new IOException();
            }
            String jar = httpGetPath.substring(0, i);
            httpGetPath = httpGetPath.substring(i + 1);
            JarFile[] jfs = (JarFile[]) map.get(jar);
            if (jfs == null)
                throw new IOException();
            for (i = 0; i < jfs.length; i++) {
                JarEntry je = jfs[i].getJarEntry(httpGetPath);
                if (je == null)
                    continue;
                writeDataBytes(getBytes(jfs[i].getInputStream(je), je.getSize()));
                nbrFilesServed++;
                return;
            }
            throw new IOException();
        }

        /**
         * Processes an HTTP POST method.
         * @exception IOException if the file is not found.
         */
        private void processHttpPost() throws IOException {
            Log.current.println("POST " + bodyLine + " from " + sock.getInetAddress().getHostName());
            cycHtmlRequest(bodyLine);
        }

        /**
         * Reads the specified number of bytes and always close the stream.
         * @param in the file to be read for subsequent downloading.
         * @param length the number of bytes to read from the file.
         * @return An array of bytes from the file.
         * @exception IOException if an error occurs when processing the file.
         */
        private byte[] getBytes(InputStream in, long length) throws IOException {
            DataInputStream din = new DataInputStream(in);
            byte[] bytes = new byte[ (int) length];
            try {
                din.readFully(bytes);
            }
            finally {
                din.close();
            }
            return bytes;
        }

        /**
         * Sends the HTML request to Cyc.
         * @param cycPath the portion of the URL which is given to the Cyc HTML server.
         *
         */
        private void cycHtmlRequest(String cycPath) {
            String request = sock.getInetAddress().getHostName() + "&" + cycPath + "#";
            ArrayList bytes = new ArrayList(10000);
            try {
                cycHtmlSocket = new Socket("localhost", cycPort);
                BufferedReader cycIn = new BufferedReader(new InputStreamReader(cycHtmlSocket.getInputStream()));
                PrintWriter cycOut = new PrintWriter(cycHtmlSocket.getOutputStream(), true);
                cycOut.println(request);
                cycOut.flush();
                int ch = 0;
                while (ch >= 0) {
                    ch = cycIn.read();
                    bytes.add(new Integer(ch));
                }
            }
            catch (Exception e) {
                Log.current.printStackTrace(e);
            }
            byte[] byteArray = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                Integer ich = (Integer) bytes.get(i);
                byteArray[i] = ich.byteValue();
            }
            try {
                writeTextBytes(byteArray);
            }
            catch (Exception e) {
                Log.current.printStackTrace(e);
            }
        }

        /**
         * Responds to the HTTP client with data content from the requested URL.
         * @param bytes the array of bytes from the URL.
         * @exception IOException if there is an error writing to the HTTP client.
         */
        public void writeDataBytes(byte[] bytes) throws IOException {
            out.writeBytes("HTTP/1.1 200 OK\r\n");
            out.writeBytes("Server: Cyc WebServer\r\n");
            out.writeBytes("Connection: close\r\n");
            out.writeBytes("Content-Length: " + bytes.length + "\r\n");
            String prefix = (new String(bytes)).toLowerCase();
            if (prefix.indexOf("<html>") > -1)
                out.writeBytes("Content-Type: text/html\r\n\r\n");
            else
                out.writeBytes("Content-Type: application/java\r\n\r\n");
            out.write(bytes);
            out.flush();
        }

        /**
         * Respond to the HTTP client with text content from the requested URL.
         * @param bytes the array of bytes from the URL.
         * @exception IOException if there is an error writing to the HTTP client.
         */
        public void writeTextBytes(byte[] bytes) throws IOException {
            out.writeBytes("HTTP/1.1 200 OK\r\n");
            out.writeBytes("Server: Cyc WebServer\r\n");
            out.writeBytes("Connection: close\r\n");
            out.writeBytes("Content-Length: " + bytes.length + "\r\n");
            out.writeBytes("Content-Type: text/html\r\n\r\n");
            out.write(bytes);
            out.flush();
        }
    }


    /**
     * Gets properties governing the web server's behavior.
     */
    private void getProperties() {
        port = DEFAULT_PORT;
        String portProperty = System.getProperty("org.opencyc.webserver.port", "");
        if (! portProperty.equalsIgnoreCase(""))
            port = (new Integer(portProperty)).intValue();
        Log.current.println("Listening on port " + port);

        cycPort = DEFAULT_CYC_PORT;
        String cycPortProperty = System.getProperty("org.opencyc.webserver.cycPort", "");
        if (! cycPortProperty.equalsIgnoreCase(""))
            cycPort = (new Integer(cycPortProperty)).intValue();
        Log.current.println("Cyc connections directed to port " + cycPort);

        String dirsProperty = System.getProperty("org.opencyc.webserver.dirs", "");
        dirs = new ArrayList(3);
        StringTokenizer st = new StringTokenizer(dirsProperty, ";", false);
        while (st.hasMoreTokens()) {
            String dir = st.nextToken();
            dirs.add(dir);
        }

        trees = false;
        String treesProperty = System.getProperty("org.opencyc.webserver.trees", "");
        if (! treesProperty.equalsIgnoreCase(""))
            trees = true;

        traceRequests = false;
        String traceRequestsProperty = System.getProperty("org.opencyc.webserver.traceRequests", "");
        if (! traceRequestsProperty.equalsIgnoreCase("")) {
            traceRequests = true;
            Log.current.println("tracing requests");
        }
    }

    /**
     * Adds transitive Class-Path jars to jfs.
     * @param jar the jar file
     * @param jfs the list of jar files to serve.
     * @param dir the jar file directory.
     * @exception IOException if an I/O error has occurred with the jar file.
     */
    private void addJar(String jar, ArrayList jfs, String dir) throws IOException {
        Log.current.println("Serving jar files from: " + dir + jar);
        JarFile jf = new JarFile(dir + jar);
        jfs.add(jf);
        Manifest man = jf.getManifest();
        if (man == null)
            return;
        Attributes attrs = man.getMainAttributes();
        if (attrs == null)
            return;
        String val = attrs.getValue(Attributes.Name.CLASS_PATH);
        if (val == null)
            return;
        dir = dir + jar.substring(0, jar.lastIndexOf(File.separatorChar) + 1);
        StringTokenizer st = new StringTokenizer(val);
        while (st.hasMoreTokens()) {
            addJar(st.nextToken().replace('/', File.separatorChar), jfs, dir);
        }
    }

    /**
     * Administrative accessor method that obtains list of directories from which files are served.
     */
    public ArrayList getDirs() {
        return dirs;
    }

    /**
     * Administrative method that updates the list of directories from which files are served.
     */
    public synchronized void setDirs(ArrayList dirs) throws IOException {
        this.dirs = dirs;
        fileCache.clear();
        processDirectories();
    }

    /**
     * Administrative accessor method that obtains number of files served.
     * @return The number of files served.
     */
    public long getNbrFilesServed() {
        return nbrFilesServed;
    }

    /**
     * Administrative accessor method that obtains number of files served from cache.
     * @return The number of files served from the cache.
     */
    public long getNbrCacheHits() {
        return nbrCacheHits;
    }

    /**
     * Administrative method that clears the file cache.
     */
    public synchronized void clearFileCache() {
        Log.current.println("Clearing file cache");
        fileCache.clear();
        nbrFilesServed = 0;
        nbrCacheHits = 0;
    }

    /**
     * Processes the directories from which files are served, expanding jar trees if
     * directed.
     * @exception IOException if problem occurs while processing the jar files.
     */
    private void processDirectories() throws IOException {
        if (dirs.size() == 0)
            if (File.separatorChar == '\\')
                dirs.add(DEFAULT_WIN_DIR);
            else
                dirs.add(DEFAULT_DIR);

        Iterator directories = dirs.iterator();
        while (directories.hasNext())
            Log.current.println("Serving from " + directories.next());

        if (trees) {
            map = new HashMap();
            for (int j = 0; j < dirs.size(); j++) {
                String dir = (String) dirs.get(j);
                String[] files = new File(dir).list();
                for (int i = 0; i < files.length; i++) {
                    String jar = files[i];
                    if (!jar.endsWith(".jar"))
                        continue;
                    ArrayList jfs = new ArrayList(1);
                    addJar(jar, jfs, dir);
                    map.put(jar.substring(0, jar.length() - 4), jfs.toArray(new JarFile[jfs.size()]));
                }
            }
        }
    }

    /**
     * Provides the command line interface for creating an HTTP server.
     * The properties are:
     *
     * <pre>
     * org.opencyc.webserver.port=<HTTP listening port>
     * </pre>
     * which defaults to 80.
     *
     * <pre>
     * org.opencyc.webserver.cycPort=<Cyc connection port>
     * </pre>
     * which defaults to 3600.
     *
     * <pre>
     * org.opencyc.webserver.dirs=<path>;<path> ... ;<path>
     * </pre>
     * with the argument enclosed in quotes if any path contains an
     * embedded space.
     * The default directory on Windows is C:
     * and the default on other systems is / the default
     * can be overridden with this property.  By default, all files
     * under this directory (including all subdirectories) are served
     * up via HTTP.  If the pathname of a file is <var>path</var> relative
     * to the top-level directory, then the file can be downloaded using
     * the URL
     * <pre>
     * http://<var>host</var>:<var>port</var>/<var>path</var>
     * </pre>
     * Caching of file contents is performed.
     *
     * <pre>
     * org.opencyc.util.log=all
     * </pre>
     * If the all value is given, then all attempts to download files
     * are output.
     *
     * <pre>
     * org.opencyc.webserver.traceRequests
     * </pre>
     * If this property has any value, then the client HTTP requests are
     * output.<p>
     *
     * <pre>
     * org.opencyc.webserver.trees
     * </pre>
     * This property can be used to serve up individual files stored
     * within jar files in addition to the files that are served up by
     * default.  If the property has any value, the server finds all jar files
     * in the top-level directory (not in subdirectories).  For each
     * jar file, if the name of the jar file is <var>name</var>.jar, then any
     * individual file named <var>file</var> within that jar file (or within
     * the jar or zip files referenced transitively in the Class-Path manifest
     * attribute, can be downloaded using a URL of the form:
     * <pre>
     * http://<var>host</var>:<var>port</var>/<var>name</var>/<var>file</var>
     * </pre>
     * When this property has any value, an open file descriptor and cached
     * information are held for each jar file, for the life of the process.
     * @param args an unused array of command line arguments.
     */
    public static void main(String[] args) {
        Log.makeLog();
        System.out.println("OpenCyc Web Server");

        try {
            // Launch thread to accept HTTP connections.
            current = new WebServer();
            current.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Just keep looping, spawning a new thread for each incoming request.
     */
    public void run() {
        try {
            while (true) {
                // Launch thread to process one HTTP request.
                new Task(server.accept()).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}