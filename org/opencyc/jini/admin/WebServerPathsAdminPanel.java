package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering the webserver file serving paths.
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
import  java.util.*;
import  javax.swing.*;
import  javax.swing.border.*;
import  javax.swing.event.*;
import  org.opencyc.webserver.*;
import  org.opencyc.util.*;


// Initialize with a ref to the admin object
public class WebServerPathsAdminPanel extends JPanel {
    JPanel panel;
    Container container;
    JFrame frame;
    ArrayList dirs;
    ArrayList resetDirs;
    JFileChooser fileChooser;
    DefaultListModel dirsModel;
    ListSelectionModel dirsSelectionModel;
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    Box boxHorizontal;
    JList lstPaths = new JList();
    Box boxRightButtons;
    JButton btnAdd = new JButton();
    Border border1;
    JButton btnEdit = new JButton();
    JButton btnRemove = new JButton();
    JButton btnMoveUp = new JButton();
    JButton btnMoveDown = new JButton();
    Border border2;
    Border border3;
    JButton btnReset = new JButton();
    JButton btnOk = new JButton();
    JButton btnCancel = new JButton();
    Box boxSouthButtons;
    Component component1;
    Component component2;
    JButton btnApply = new JButton();

    public WebServerPathsAdminPanel (final WebServerAdminInterface admin) {
        panel = this;
        // Populate the directories list.
        try {
            dirs = admin.getDirs();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Couldn't get directories:\n" + ex.getMessage(), "Alert",
                    JOptionPane.ERROR_MESSAGE);
        }
        resetDirs = new ArrayList(dirs);
        dirsModel = new DefaultListModel();
        Iterator directories = dirs.iterator();
        while (directories.hasNext())
            dirsModel.addElement(directories.next());
        lstPaths.setModel(dirsModel);
        dirsSelectionModel = lstPaths.getSelectionModel();
        dirsSelectionModel.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnEdit.setEnabled(false);
        btnRemove.setEnabled(false);
        btnMoveUp.setEnabled(false);
        btnMoveDown.setEnabled(false);
        // Handle the Reset button.
        btnReset.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).resetFileDirectories();
            }
        });
        // Handle the Apply button.
        btnApply.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                dirs = new ArrayList();
                Enumeration directories = dirsModel.elements();
                while (directories.hasMoreElements())
                    dirs.add(directories.nextElement());
                try {
                    Log.current.println("Setting web server directories");
                    admin.setDirs(dirs);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Couldn't set directories:\n" + ex.getMessage(),
                            "Alert", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Handle the OK button, same as Apply but exit the frame as well.
        btnOk.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                dirs = new ArrayList();
                Enumeration directories = dirsModel.elements();
                while (directories.hasMoreElements())
                    dirs.add(directories.nextElement());
                try {
                    Log.current.println("Setting web server directories");
                    admin.setDirs(dirs);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Couldn't set directories:\n" + ex.getMessage(),
                            "Alert", JOptionPane.ERROR_MESSAGE);
                }
                getFrame().dispose();
            }
        });
        // Handle the Cancel button.
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                getFrame().dispose();
            }
        });
        // Handle the Add button.
        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).addDirectory();
            }
        });
        // Handle the Edit button.
        btnEdit.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).editDirectory();
            }
        });
        // Handle the Remove button.
        btnRemove.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).removeDirectory();
            }
        });
        // Handle the MoveUp button.
        btnMoveUp.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).moveUpDirectory();
            }
        });
        // Handle the MoveDown button.
        btnMoveDown.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((WebServerPathsAdminPanel)panel).moveDownDirectory();
            }
        });
        // Handle the directory selection list.
        dirsSelectionModel.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged (ListSelectionEvent ev) {
                ((WebServerPathsAdminPanel)panel).listValueChanged();
            }
        });
    }

    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 4, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        boxHorizontal = Box.createHorizontalBox();
        boxRightButtons = Box.createVerticalBox();
        border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                Color.white, Color.white, new Color(178, 178, 178), new Color(124, 124, 124)), BorderFactory.createEmptyBorder(5,
                5, 5, 5));
        border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(93,
                93, 93), new Color(134, 134, 134));
        border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
                Color.white, Color.white, new Color(134, 134, 134), new Color(93, 93, 93)), BorderFactory.createEmptyBorder(5,
                5, 5, 5));
        boxSouthButtons = Box.createHorizontalBox();
        component1 = Box.createHorizontalStrut(95);
        component2 = Box.createGlue();
        jLabel1.setToolTipText("The web server searches these paths, in order, when responding to " +
                "client HTTP GET requests.");
        jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
        jLabel1.setText("Web Server file paths");
        this.setLayout(borderLayout1);
        btnAdd.setBorder(myBorder);
        btnAdd.setMaximumSize(new Dimension(100, 30));
        btnAdd.setMinimumSize(new Dimension(100, 30));
        btnAdd.setText("Add...");
        lstPaths.setBorder(border1);
        lstPaths.setMaximumSize(new Dimension(10000, 10000));
        lstPaths.setMinimumSize(new Dimension(100, 100));
        lstPaths.setPreferredSize(new Dimension(2000, 1000));
        btnEdit.setBorder(myBorder);
        btnEdit.setMaximumSize(new Dimension(100, 30));
        btnEdit.setMinimumSize(new Dimension(100, 30));
        btnEdit.setText("Edit");
        btnRemove.setBorder(myBorder);
        btnRemove.setMaximumSize(new Dimension(100, 30));
        btnRemove.setMinimumSize(new Dimension(100, 30));
        btnRemove.setText("Remove");
        btnMoveUp.setBorder(myBorder);
        btnMoveUp.setMaximumSize(new Dimension(100, 30));
        btnMoveUp.setMinimumSize(new Dimension(100, 30));
        btnMoveUp.setText("Move Up");
        btnMoveDown.setBorder(myBorder);
        btnMoveDown.setMaximumSize(new Dimension(100, 30));
        btnMoveDown.setMinimumSize(new Dimension(100, 30));
        btnMoveDown.setText("Move Down");
        this.setMinimumSize(new Dimension(800, 800));
        this.setPreferredSize(new Dimension(2000, 2000));
        btnReset.setBorder(myBorder);
        btnReset.setMaximumSize(new Dimension(100, 30));
        btnReset.setMinimumSize(new Dimension(100, 30));
        btnReset.setPreferredSize(new Dimension(100, 30));
        btnReset.setText("Reset");
        btnOk.setBorder(myBorder);
        btnOk.setMaximumSize(new Dimension(100, 30));
        btnOk.setMinimumSize(new Dimension(100, 30));
        btnOk.setPreferredSize(new Dimension(100, 30));
        btnOk.setText("OK");
        btnCancel.setBorder(myBorder);
        btnCancel.setMaximumSize(new Dimension(100, 30));
        btnCancel.setMinimumSize(new Dimension(100, 30));
        btnCancel.setPreferredSize(new Dimension(100, 30));
        btnCancel.setText("Cancel");
        btnApply.setBorder(myBorder);
        btnApply.setMaximumSize(new Dimension(100, 30));
        btnApply.setMinimumSize(new Dimension(100, 30));
        btnApply.setPreferredSize(new Dimension(100, 30));
        btnApply.setText("Apply");
        this.add(jLabel1, BorderLayout.NORTH);
        this.add(boxHorizontal, BorderLayout.CENTER);
        this.add(boxSouthButtons, BorderLayout.SOUTH);
        boxSouthButtons.add(btnReset, null);
        boxSouthButtons.add(component2, null);
        boxSouthButtons.add(btnApply, null);
        boxSouthButtons.add(btnOk, null);
        boxSouthButtons.add(btnCancel, null);
        boxSouthButtons.add(component1, null);
        boxHorizontal.add(lstPaths, null);
        boxHorizontal.add(boxRightButtons, null);
        boxRightButtons.add(btnAdd, null);
        boxRightButtons.add(btnEdit, null);
        boxRightButtons.add(btnRemove, null);
        boxRightButtons.add(Box.createVerticalGlue());
        boxRightButtons.add(btnMoveUp, null);
        boxRightButtons.add(btnMoveDown, null);
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

    /**
     * Reset the file directory list.
     */
    private void resetFileDirectories () {
        Iterator directories = resetDirs.iterator();
        dirsModel.clear();
        while (directories.hasNext())
            dirsModel.addElement(directories.next());
    }

    /**
     * Add a file to the file directory list.
     */
    private void addDirectory () {
        // Launch a file chooser.
        if (fileChooser == null)
            fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setApproveButtonText("Add");
        int status = fileChooser.showOpenDialog(getFrame());
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Log.current.println("Selected path " + selectedFile);
            dirsModel.addElement(selectedFile.getPath());
        }
    }

    /**
     * Edit the selected file on the file directory list.
     */
    private void editDirectory () {
        int index = dirsSelectionModel.getMaxSelectionIndex();
        String oldFilename = (String)dirsModel.elementAt(index);
        File oldFile = new File(oldFilename);
        // Launch a file chooser.
        if (fileChooser == null)
            fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setCurrentDirectory(oldFile);
        int status = fileChooser.showOpenDialog(getFrame());
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Log.current.println("Selected path " + selectedFile);
            String newFilename = selectedFile.getPath();
            dirsModel.set(index, newFilename);
        }
    }

    /**
     * Remove a selected file from the file directory list.
     */
    private void removeDirectory () {
        dirsModel.removeElementAt(dirsSelectionModel.getMaxSelectionIndex());
    }

    /**
     * Move up a selected file on the file directory list.
     */
    private void moveUpDirectory () {
        int sourceIndex = dirsSelectionModel.getMaxSelectionIndex();
        int destinationIndex = sourceIndex - 1;
        String temp = (String)dirsModel.elementAt(destinationIndex);
        dirsModel.set(destinationIndex, dirsModel.elementAt(sourceIndex));
        dirsModel.set(sourceIndex, temp);
        dirsSelectionModel.setSelectionInterval(destinationIndex, destinationIndex);
    }

    /**
     * Move down a selected file on the file directory list.
     */
    private void moveDownDirectory () {
        int sourceIndex = dirsSelectionModel.getMaxSelectionIndex();
        int destinationIndex = sourceIndex + 1;
        String temp = (String)dirsModel.elementAt(destinationIndex);
        dirsModel.set(destinationIndex, dirsModel.elementAt(sourceIndex));
        dirsModel.set(sourceIndex, temp);
        dirsSelectionModel.setSelectionInterval(destinationIndex, destinationIndex);
    }

    /**
     * Handle a changed selection in the directories list.
     */
    private void listValueChanged () {
        btnEdit.setEnabled(true);
        btnRemove.setEnabled(true);
        int maxDirsIndex = dirs.size() - 1;
        if (maxDirsIndex == 0)
            return;
        if (dirsSelectionModel.getMaxSelectionIndex() > 0)
            btnMoveUp.setEnabled(true);
        else
            btnMoveUp.setEnabled(false);
        if (dirsSelectionModel.getMaxSelectionIndex() < maxDirsIndex)
            btnMoveDown.setEnabled(true);
        else
            btnMoveDown.setEnabled(false);
    }
}



