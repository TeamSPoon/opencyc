package org.opencyc.xml.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import org.opencyc.util.*;

/**
 * Permits user to specify the URL for DAML import.<p>
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

public class ImportDamlPanel extends JPanel {

    protected JPanel panel;
    protected Container container;
    protected JFrame frame;
    protected String storageLocation;
    protected JFileChooser fileChooser;
    protected BorderLayout borderLayout1 = new BorderLayout();
    protected Box boxButtons;
    protected JButton btnExit = new JButton();
    protected JButton btnImport = new JButton();
    protected Component glueButtons;
    protected JLabel jLabel1 = new JLabel();

    protected String urlHistory1 = "http://orlando.drc.com/daml/ontology/VES/3.2/drc-ves-ont.daml";
    protected String urlHistory2 = "http://orlando.drc.com/daml/ontology/DC/3.2/dces-ont.daml";
    protected String urlHistory3 = "http://xmlns.com/foaf/0.1/";
    protected String urlHistory4;
    protected String urlHistory5;
    protected String urlHistory6;
    protected String urlHistory7;
    protected String urlHistory8;
    protected String urlHistory9;
    private Box box1;
    private JPanel urlInputPanel;
    private JPanel ontologyNicknameInputPanel;
    private JPanel importMtInputPanel;
    private Box urlInputBox;
    private Box ontologyNicknameInputBox;
    private Box importMtInputBox;
    private JLabel urlInputLabel;
    private JLabel ontologyNicknameInputLabel;
    private JLabel importMtInputLabel;
    private JComboBox urlInputComboBox;
    private JComboBox ontologyNicknameInputComboBox;
    private JComboBox importMtInputComboBox;

    public ImportDamlPanel() {
        System.out.println("Constructing ImportDamlPanel");
        panel = this;
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Handle the Exit button.
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                getFrame().dispose();
                Log.current.println("Exiting ImportDamlApp");
                System.exit(0);
            }
        });
        // Handle the Import button.
        btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String damlPath = (String) obtainDamlPath();
                System.out.println("Importing DAML statements from " + damlPath);
            }
        });
        obtainDamlPath();
    }

    private void jbInit() throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2,4,2,2),BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)));
        boxButtons = Box.createHorizontalBox();
        box1 = Box.createVerticalBox();
        glueButtons = Box.createGlue();

        urlInputPanel = new JPanel();
        ontologyNicknameInputPanel = new JPanel();
        importMtInputPanel = new JPanel();

        urlInputBox = Box.createHorizontalBox();;
        ontologyNicknameInputBox = Box.createHorizontalBox();;
        importMtInputBox = Box.createHorizontalBox();;

        urlInputLabel = new JLabel();
        ontologyNicknameInputLabel = new JLabel();
        importMtInputLabel = new JLabel();

        urlInputComboBox = new JComboBox();
        ontologyNicknameInputComboBox = new JComboBox();
        importMtInputComboBox = new JComboBox();

        this.setLayout(borderLayout1);
        btnExit.setBorder(myBorder);
        btnExit.setMaximumSize(new Dimension(100, 30));
        btnExit.setMinimumSize(new Dimension(100, 30));
        btnExit.setPreferredSize(new Dimension(100, 30));
        btnExit.setActionCommand("Exit");
        btnExit.setText("Exit");
        btnImport.setBorder(myBorder);
        btnImport.setMaximumSize(new Dimension(100, 30));
        btnImport.setMinimumSize(new Dimension(100, 30));
        btnImport.setPreferredSize(new Dimension(100, 30));
        btnImport.setActionCommand("Import");
        btnImport.setText("Import");
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Import DAML Statements");
        this.add(boxButtons, BorderLayout.SOUTH);
        boxButtons.add(glueButtons, null);
        boxButtons.add(btnImport, BorderLayout.SOUTH);
        boxButtons.add(btnExit, BorderLayout.SOUTH);
        this.add(jLabel1, BorderLayout.NORTH);
        this.add(box1, BorderLayout.CENTER);

        box1.add(urlInputPanel, null);
        urlInputPanel.add(Box.createHorizontalGlue());
        urlInputPanel.add(urlInputLabel);
        urlInputPanel.add(urlInputComboBox);
        urlInputPanel.add(Box.createHorizontalGlue());

        box1.add(ontologyNicknameInputPanel, null);
        ontologyNicknameInputPanel.add(Box.createHorizontalGlue());
        ontologyNicknameInputPanel.add(urlInputLabel);
        ontologyNicknameInputPanel.add(urlInputComboBox);
        ontologyNicknameInputPanel.add(Box.createHorizontalGlue());

        box1.add(importMtInputBox, null);
        importMtInputBox.add(Box.createHorizontalGlue());
        urlInputPanel.add(urlInputLabel);
        urlInputPanel.add(urlInputComboBox);
        importMtInputBox.add(Box.createHorizontalGlue());
    }

    /**
     * Obtain DAML path.
     */
    protected Object obtainDamlPath() {
        return null;
        //return jComboBox1.getSelectedItem();
    }


    /**
     * Get the frame parent of this panel.
     */
    private JFrame getFrame() {
        if (frame != null)
            return frame;
        container = this;
        while (true) {
            // Ascend the container ancestory path, finding the containing JFrame.
            if (container instanceof JFrame) {
                frame = (JFrame) container;
                return frame;
            }
            container = container.getParent();
        }
    }


}