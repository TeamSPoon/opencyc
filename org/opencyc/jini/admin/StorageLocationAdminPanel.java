package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering jini agent storage location services
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
import  java.io.*;
import  java.rmi.RemoteException;
import  javax.swing.*;
import  javax.swing.border.*;
import  com.sun.jini.admin.StorageLocationAdmin;
import  org.opencyc.util.*;

public class StorageLocationAdminPanel extends JPanel {
    JPanel panel;
    Container container;
    JFrame frame;
    StorageLocationAdmin admin;
    String storageLocation;
    JFileChooser fileChooser;
    BorderLayout borderLayout1 = new BorderLayout();
    Box boxButtons;
    JButton btnCancel = new JButton();
    JButton btnEdit = new JButton();
    Component glueButtons;
    JLabel jLabel1 = new JLabel();
    Box boxHorizontal;
    JLabel lblStorageLocation = new JLabel();
    Component glueEast;
    Component glueWest;

    /**
     * Constructs a new StorageLocationAdminPanel given the parent StorageLocationAdmin object.
     *
     * @param admin the parent StorageLocationAdmin object
     */
    public StorageLocationAdminPanel (final StorageLocationAdmin admin) {
        panel = this;
        this.admin = admin;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Handle the Cancel button.
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                getFrame().dispose();
            }
        });
        // Handle the Edit button.
        btnEdit.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((StorageLocationAdminPanel)panel).editStorageLocation();
            }
        });
        obtainStorageLocation();
    }

    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 4, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        boxButtons = Box.createHorizontalBox();
        glueButtons = Box.createGlue();
        boxHorizontal = Box.createHorizontalBox();
        glueEast = Box.createGlue();
        glueWest = Box.createGlue();
        this.setLayout(borderLayout1);
        btnCancel.setBorder(myBorder);
        btnCancel.setMaximumSize(new Dimension(100, 30));
        btnCancel.setMinimumSize(new Dimension(100, 30));
        btnCancel.setPreferredSize(new Dimension(100, 30));
        btnCancel.setText("Cancel");
        btnEdit.setBorder(myBorder);
        btnEdit.setMaximumSize(new Dimension(100, 30));
        btnEdit.setMinimumSize(new Dimension(100, 30));
        btnEdit.setPreferredSize(new Dimension(100, 30));
        btnEdit.setText("Edit");
        jLabel1.setText("Service storage location");
        lblStorageLocation.setToolTipText("");
        lblStorageLocation.setText("storage location path");
        this.add(boxButtons, BorderLayout.SOUTH);
        boxButtons.add(glueButtons, null);
        boxButtons.add(btnEdit, BorderLayout.SOUTH);
        boxButtons.add(btnCancel, BorderLayout.SOUTH);
        this.add(jLabel1, BorderLayout.NORTH);
        this.add(boxHorizontal, BorderLayout.CENTER);
        boxHorizontal.add(glueWest, null);
        boxHorizontal.add(lblStorageLocation, null);
        boxHorizontal.add(glueEast, null);
    }

    /**
     * Launch the file chooser to edit the service storage location.
     */
    private void editStorageLocation () {
        if (fileChooser == null)
            fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setApproveButtonText("Select");
        int status = fileChooser.showOpenDialog(getFrame());
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            storageLocation = selectedFile.getPath();
        }
        try {
            Log.current.println("Updating storage location " + storageLocation);
            admin.setStorageLocation(storageLocation);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not set location:\n" + ex.getMessage(), "Alert",
                    JOptionPane.ERROR_MESSAGE);
        }
        obtainStorageLocation();
    }

    /**
     * Obtain service storage location.
     */
    private void obtainStorageLocation () {
        try {
            storageLocation = admin.getStorageLocation();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Could not get location:\n" + ex.getMessage(), "Alert",
                    JOptionPane.ERROR_MESSAGE);
            storageLocation = "";
        }
        lblStorageLocation.setText(storageLocation);
    }

    /**
     * Get the frame parent of this panel.
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
}



