package org.opencyc.xml.gui;

import org.opencyc.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Imports specified DAML xml content.<p>
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

public class ImportDamlApp {

    protected static final int MIN_MAIN_FRAME_HEIGHT = 300;
    protected static final int MIN_MAIN_FRAME_WIDTH = 300;
    protected static final int INITIAL_MAIN_FRAME_HEIGHT = 400;
    protected static final int INITIAL_MAIN_FRAME_WIDTH = 600;

    public ImportDamlApp() {
    }


    public static void main(String[] args) {
        Log.makeLog();
        Log.current.println("Starting ImportDamlApp");
        try {
            final JFrame mainFrame = new JFrame("Import DAML");
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
            ImportDamlPanel importDamlPanel = new ImportDamlPanel();
            mainFrame.getContentPane().add(importDamlPanel);
            Image icon = Toolkit.getDefaultToolkit().getImage("images/cyc-logo-16.jpg");
            mainFrame.setIconImage(icon);
            mainFrame.setSize(INITIAL_MAIN_FRAME_WIDTH,
                              INITIAL_MAIN_FRAME_HEIGHT);
            mainFrame.pack();
            mainFrame.setVisible(true);
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
        Log.current.println("Exiting ImportDamlApp");
        System.exit(0);
    }



}