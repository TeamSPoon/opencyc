package org.opencyc.util;

/**
 * Provides the behavior and attributes of a event log for OpenCyc.<p>
 *
 * Class Log provides a local log facility for OpenCyc agents.  Messages can be
 * written to a file, displayed to stdout, stderr, or ignored.<p>
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

import java.io.*;

public class Log {
    /**
     * Current log instance.  For convenience in calling, an instance of the Log
     * is kept at by the class.  Static methods are forwarded to the current instance
     * so that users of the log need not keep track of the log instance themselves.
     */
    public static Log current = null;

    /**
     * If true, write messages to the log file.
     */
    protected boolean writeToFile;

    /**
     * If true, write error messages to System.err.
     */
    protected boolean writeToErr;

    /**
     * If true, write messages to System.out.
     */
    protected boolean writeToOut;

    /**
     * If true, ignore all messages.
     */
    protected boolean ignore;

    /**
     * File pathname for the log file.
     */
    protected String logFilePath;

    /**
     * PrintWriter object for the log file.
     */
    protected PrintWriter printWriter;

    /**
     * BufferedWriter object for the log file.
     */
    protected BufferedWriter writer;

    /**
     * Default file name for the log file.
     */
    protected static final String DEFAULT_LOG_FILENAME = "agent.log";

    /**
     * Constructs a new Log object and store a reference to it at the Log class.
     */
    public static void makeLog() {
        if (current != null) {
            return;
        }
        String logProperty = System.getProperty("org.opencyc.util.log", "default");
        if (logProperty.equalsIgnoreCase("default") || logProperty.equalsIgnoreCase("all")) {
            current = new Log("agent.log", true, true, true, false);
            return;
        }
        if (logProperty.equalsIgnoreCase("display")) {
            current = new Log("", false, true, true, false);
            return;
        }
        if (logProperty.equalsIgnoreCase("file")) {
            current = new Log("agent.log", true, false, false, false);
            return;
        }
        if (logProperty.equalsIgnoreCase("errors")) {
            current = new Log("agent.log", true, true, false, false);
            return;
        }
        System.err.println("Invalid value for property org.opencyc.util.log " + logProperty + " substituting default");
        current = new Log("agent.log", true, true, true, false);
    }


    /**
     * Constructs a new Log object.  Display all messages only to
     * the default log file "agent.log".
     */
    public Log() {
        this(DEFAULT_LOG_FILENAME, false, false, false, false);
    }

    /**
     * Constructs a new Log object given the path.  Display all messages.
     *
     * @param logFilePath specifies the path for the log file.
     */
    public Log(String logFilePath) {
        this(logFilePath, true, true, true, false);
    }

    /**
     * Constructs a new Log object given all parameters.
     *
     * @param logFilePath specifies the path for the log file.
     * @param writeToFile if true, write messages to the log file.
     * @param writeToErr if true, write error messages to System.err.
     * @param writeToOut if true, write messages to System.out.
     * @param ignore if true, ignore all messages.
     */
    public Log(String logFilePath,
               boolean writeToFile,
               boolean writeToErr,
               boolean writeToOut,
               boolean ignore) {
        this.logFilePath = logFilePath;
        this.writeToFile = writeToFile;
        this.writeToErr = writeToErr;
        this.writeToOut = writeToOut;
        this.ignore = ignore;

        printWriter = null;
        if (writeToFile) {
            try {
                printWriter = new PrintWriter( new BufferedWriter(new FileWriter(logFilePath)));
            }
            catch (IOException e) {
                System.err.println("Error creating log file " + logFilePath);
                System.err.println(e);
            }
        }
    }

    /**
     * Sets the log file path to the specified location.
     */
    public void setStorageLocation(String location) throws IOException {
        if (printWriter != null)
            close();
        logFilePath = location;
        if (writeToFile) {
                printWriter = new PrintWriter( new BufferedWriter(new FileWriter(logFilePath)));
        }
    }

    /**
     * Returns the path of the log file.
     */
    public String getStorageLocation() {
        return logFilePath;
    }

    /**
     * Writes the int message to the log.
     *
     * @param message the int message to be logged.
     */
    public void println(int message) {
        println(Integer.toString(message));
    }

    /**
     * Writes a newline to the log.
     */
    public void println() {
        if (ignore)
            return;
        if (writeToOut)
            System.out.print("\n");
        if (writeToFile) {
            printWriter.print("\n");
            printWriter.flush();
        }
    }

    /**
     * Writes the String message to the log.
     *
     * @param message the String message to be logged.
     */
    public void print(String message) {
        if (ignore)
            return;
        if (writeToOut)
            System.out.print(message);
        if (writeToFile) {
            printWriter.print(message);
            printWriter.flush();
        }
    }

    /**
     * Writes the String message to the log.
     *
     * @param message the String message to be logged.
     */
    public void println(String message) {
        if (ignore)
            return;
        if (writeToOut)
            System.out.println(message);
        if (writeToFile) {
            printWriter.println(message);
            printWriter.flush();
        }
    }

    /**
     * Writes the error message to the log.
     *
     * @param errorMessage the error message to be logged.
     */
    public void errorPrintln(String errorMessage) {
        if (writeToErr)
            System.err.println(errorMessage);
        if (writeToFile) {
            printWriter.println(errorMessage);
            printWriter.flush();
        }
    }

    /**
     * Writes the exception stack trace to the log.
     *
     * @param exception the exception to be reported.
     */
    public void printStackTrace(Exception exception) {
        if (writeToErr)
            exception.printStackTrace();
        if (writeToFile) {
            exception.printStackTrace(printWriter);
            printWriter.flush();
        }
    }

    /**
     * Closes the log file.
     */
    public void close() {
        if (writeToFile) {
            printWriter.close();
        }
    }

    /**
     * Sets the current log instance.
     *
     * @param log a log object.
     */
    public static void setCurrent(Log log) {
        current = log;
    }
}