package org.opencyc.xml.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.JFrame;
import org.opencyc.api.CycAccess;
import org.opencyc.util.Log;
import org.opencyc.xml.ImportOwl;
import org.opencyc.xml.OwlDocInfo;

/**
 * Imports specified OWL xml content.<p>
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

public class ImportOwlApp extends ImportOwl {

    protected static final int MIN_MAIN_FRAME_HEIGHT = 300;
    protected static final int MIN_MAIN_FRAME_WIDTH = 300;
    protected static final int INITIAL_MAIN_FRAME_HEIGHT = 400;
    protected static final int INITIAL_MAIN_FRAME_WIDTH = 600;
    protected static final Object lock = new Object();

    /** Creates a new instance of ImportOwlTest. 
     * 
     * @param cycAccess the CycAccess object
     */
    public ImportOwlApp(final CycAccess cycAccess) throws IOException {
      super(cycAccess);
    }


    public static void main(String[] args) {
        Log.makeLog();
        Log.current.println("Starting ImportOwlApp");
        try {
            final JFrame mainFrame = new JFrame("Import OWL into Cyc");
            mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    terminate();
                }
            });
            mainFrame.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    Dimension d = mainFrame.getSize();
                    if ((d.width >= MIN_MAIN_FRAME_WIDTH) &&
                        (d.height >= MIN_MAIN_FRAME_HEIGHT))
                        return;
                    int width = d.width;
                    int height = d.height;
                    if (width < MIN_MAIN_FRAME_WIDTH)
                        width = MIN_MAIN_FRAME_WIDTH;
                    if (height < MIN_MAIN_FRAME_HEIGHT)
                        height = MIN_MAIN_FRAME_HEIGHT;
                    mainFrame.setSize(width, height);
                }
            });
            Log.current.println("Establishing connection to the Cyc server");
            String localHostName = InetAddress.getLocalHost().getHostName();
            Log.current.println("Connecting to Cyc server from " + localHostName);
            //CycAccess cycAccess = new CycAccess();
            CycAccess cycAccess = new CycAccess("localhost", 3600);
            //cycAccess.traceNamesOn();

            ImportOwlPanel importOwlPanel = new ImportOwlPanel();
            mainFrame.getContentPane().add(importOwlPanel);
            Image icon = Toolkit.getDefaultToolkit().getImage("images/cyc-logo-16.jpg");
            mainFrame.setIconImage(icon);
            mainFrame.setSize(INITIAL_MAIN_FRAME_WIDTH,
                              INITIAL_MAIN_FRAME_HEIGHT);
            mainFrame.pack();
            mainFrame.setVisible(true);
            synchronized (lock) {
              lock.wait();
            }
            OwlDocInfo owlDocInfo = 
              new OwlDocInfo(importOwlPanel.owlPath, importOwlPanel.ontologyNickname, null, importOwlPanel.importMt, "OWLMappingMt"); 
            ImportOwlApp importOwlApp = new ImportOwlApp(cycAccess);
            importOwlApp.importOwl(owlDocInfo);
            System.exit(0);
        }
        catch (Exception ex) {
            Log.current.errorPrintln("Couldn't create mainFrame:" + ex.getMessage());
            Log.current.printStackTrace(ex);
            System.exit(1);
        }
    }

    /**
     * Terminate the application.
     */
    private static void terminate() {
        Log.current.println("Exiting ImportOwlApp");
        System.exit(0);
    }



}
