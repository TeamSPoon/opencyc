package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering the webserver file cache.
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

import  java.awt.*;
import  java.awt.event.*;
import  java.rmi.RemoteException;
import  javax.swing.*;
import  javax.swing.border.*;
import  org.opencyc.webserver.*;
import  org.opencyc.util.*;


public class WebServerCacheAdminPanel extends JPanel {
    JPanel panel;
    Container container;
    JFrame frame;
    long nbrFilesServed;
    long nbrCacheHits;
    long cacheHitPercent;
    WebServerAdminInterface admin;
    BorderLayout borderLayout1 = new BorderLayout();
    Box boxButtons;
    JButton btnOk = new JButton();
    Component glueButtons;
    Box boxHorizontal;
    Box boxVertical;
    JPanel pnlCacheInfo = new JPanel();
    TitledBorder titledBorder1;
    GridLayout gridLayout1 = new GridLayout();
    JLabel lblNbrFilesServed = new JLabel();
    JLabel lblCacheHitPercent = new JLabel();
    JButton btnRefresh = new JButton();
    Component glueSouth;
    Component glueNorth;
    Component glueEast;
    Component glueWest;
    Border border1;
    JButton btnClear = new JButton();

    /**
     * Constructs a new WebServerCacheAdminPanel.
     */
    public WebServerCacheAdminPanel (final WebServerAdminInterface admin) {
        panel = this;
        this.admin = admin;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Handle the OK button.
        btnOk.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                getFrame().dispose();
            }
        });
        // Handle the Refresh button.
        btnRefresh.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerCacheAdminPanel)panel).obtainCacheInfo();
            }
        });
        // Handle the Clear file cache button.
        btnClear.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerCacheAdminPanel)panel).clearFileCache();
            }
        });
        obtainCacheInfo();
    }

    /**
     * Initializes the panel (JBuilder generated).
     */
    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 4, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        boxButtons = Box.createHorizontalBox();
        glueButtons = Box.createGlue();
        boxHorizontal = Box.createHorizontalBox();
        boxVertical = Box.createVerticalBox();
        titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(134,
                134, 134)), "Cache Information");
        glueSouth = Box.createGlue();
        glueNorth = Box.createGlue();
        glueEast = Box.createGlue();
        glueWest = Box.createGlue();
        border1 = BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                new Color(134, 134, 134)), "Cache Information"), BorderFactory.createEmptyBorder(20,
                20, 20, 20));
        this.setLayout(borderLayout1);
        btnOk.setBorder(myBorder);
        btnOk.setMaximumSize(new Dimension(100, 30));
        btnOk.setMinimumSize(new Dimension(100, 30));
        btnOk.setPreferredSize(new Dimension(100, 30));
        btnOk.setText("OK");
        pnlCacheInfo.setBorder(border1);
        pnlCacheInfo.setLayout(gridLayout1);
        gridLayout1.setColumns(1);
        gridLayout1.setRows(4);
        lblNbrFilesServed.setToolTipText("Total number of files served since launch.  Reset when cache is cleared.");
        lblNbrFilesServed.setText("Files served: 999");
        lblCacheHitPercent.setToolTipText("(files served from cache) / (total files served) as a percentage");
        lblCacheHitPercent.setText("Cache hit ratio:  50%");
        btnRefresh.setBorder(myBorder);
        btnRefresh.setMaximumSize(new Dimension(100, 30));
        btnRefresh.setMinimumSize(new Dimension(100, 30));
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.setToolTipText("Remove potentially stale files from the web server file cache");
        btnRefresh.setText("Refresh");
        btnClear.setBorder(myBorder);
        btnClear.setText("Clear file cache");
        this.add(boxButtons, BorderLayout.SOUTH);
        boxButtons.add(glueButtons, null);
        boxButtons.add(btnOk, null);
        this.add(boxHorizontal, BorderLayout.CENTER);
        boxHorizontal.add(glueWest, null);
        boxHorizontal.add(boxVertical, null);
        boxHorizontal.add(glueEast, null);
        boxVertical.add(glueNorth, null);
        boxVertical.add(pnlCacheInfo, null);
        boxVertical.add(glueSouth, null);
        pnlCacheInfo.add(lblNbrFilesServed, null);
        pnlCacheInfo.add(lblCacheHitPercent, null);
        pnlCacheInfo.add(btnRefresh, null);
        pnlCacheInfo.add(btnClear, null);
    }

    /**
     * Gets the frame parent of this panel.
     */
    private JFrame getFrame () {
        if (frame != null)
            return  frame;
        container = this;
        while (true) {
            // Ascend the container ancestory path, finding the containing JFrame.
            if (container instanceof JFrame) {
                frame = (JFrame)container;
                return  frame;
            }
            container = container.getParent();
        }
    }

    /**
     * Obtains file cache information from the web server.
     */
    private void obtainCacheInfo () {
        Log.current.println("Obtaining file cache information");
        try {
            nbrFilesServed = admin.getNbrFilesServed();
            nbrCacheHits = admin.getNbrCacheHits();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Could not get cache information:\n" + ex.getMessage(),
                    "Alert", JOptionPane.ERROR_MESSAGE);
        }
        if (nbrFilesServed == 0)
            cacheHitPercent = 0;
        else
            cacheHitPercent = (nbrCacheHits*100)/nbrFilesServed;
        lblNbrFilesServed.setText("Files served:  " + nbrFilesServed);
        lblCacheHitPercent.setText("Cache hit ratio:  " + cacheHitPercent + " %");
    }

    /**
     * Clears the web server file cache.
     */
    private void clearFileCache () {
        Log.current.println("Clearing web server file cache");
        try {
            admin.clearFileCache();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Could not clear web server file cache:\n" + ex.getMessage(),
                    "Alert", JOptionPane.ERROR_MESSAGE);
        }
        obtainCacheInfo();
    }
}



