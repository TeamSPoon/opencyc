package org.opencyc.xml.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import org.opencyc.api.CycAccess;
import org.opencyc.util.Log;
import org.opencyc.xml.OwlDocInfo;

/**
 * Permits user to specify the URL for OWL import.<p>
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

public class ImportOwlPanel extends JPanel {

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

    protected static final int NBR_OF_HISTORY_ITEMS = 10;

    /**
     * Array of URL history items for the URL input combo box
     */
    protected String [] urlHistory;

    /**
     * Array of ontology nickname history items for the URL input combo box
     */
    protected String [] ontologyNicknameHistory;

    /**
     * Array of import microtheory history items for the URL input combo box
     */
    protected String [] importMtHistory;

    /**
     * input history properties
     */
    protected Properties properties = null;

    /**
     * input history properties file name
     */
    public String propertiesFilename = "ImportOwlPanel.prop";


    /**
     * The path (URL) to the OWL XML content
     */
    public String owlPath;

    /**
     * The ontology nickname used as a namespace prefix for
     * imported terms
     */
    public String ontologyNickname;

    /**
     * The import microtheory
     */
    public String importMt;

    /**
     * CycAccess object to manage api connection the the Cyc server
     */
    protected CycAccess cycAccess;

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
        jLabel1.setText("     ");
        urlInputLabel.setText("URL");
        ontologyNicknameInputLabel.setText("Ontology Nickname");
        importMtInputLabel.setText("Import Microtheory");
        urlInputComboBox.setMinimumSize(new Dimension(130, 22));
        urlInputComboBox.setPreferredSize(new Dimension(400, 22));
        urlInputComboBox.setEditable(true);
        importMtInputComboBox.setPreferredSize(new Dimension(350, 22));
        importMtInputComboBox.setEditable(true);
        urlInputPanel.setPreferredSize(new Dimension(442, 32));
        urlInputPanel.setToolTipText("");
        this.setPreferredSize(new Dimension(542, 139));
        ontologyNicknameInputComboBox.setEditable(true);
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
        ontologyNicknameInputPanel.add(ontologyNicknameInputLabel);
        ontologyNicknameInputPanel.add(ontologyNicknameInputComboBox);
        ontologyNicknameInputPanel.add(Box.createHorizontalGlue());

        box1.add(importMtInputPanel, null);
        importMtInputPanel.add(Box.createHorizontalGlue());
        importMtInputPanel.add(importMtInputLabel);
        importMtInputPanel.add(importMtInputComboBox);
        importMtInputPanel.add(Box.createHorizontalGlue());
    }

    public ImportOwlPanel() {
        panel = this;
        try {
            jbInit();
            initializeComboBoxes();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Handle the Exit button.
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                getFrame().dispose();
                Log.current.println("Exiting ImportOwlApp");
                System.exit(0);
            }
        });
        // Handle the Import button.
        btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                owlPath = (String) obtainOwlPath();
                if (owlPath.equals("")) {
                    JOptionPane errorMessage = new JOptionPane("URL is required",
                                                               JOptionPane.ERROR_MESSAGE);
                    JDialog errorMessageDialog = errorMessage.createDialog(panel, "ERROR");
                    errorMessageDialog.show();
                }
                ontologyNickname = (String) obtainOntologyNickname();
                if (ontologyNickname.equals("")) {
                    JOptionPane errorMessage = new JOptionPane("Ontology nickname is required",
                                                               JOptionPane.ERROR_MESSAGE);
                    JDialog errorMessageDialog = errorMessage.createDialog(panel, "ERROR");
                    errorMessageDialog.show();
                }
                importMt = (String) obtainImportMt();
                if (importMt.equals("")) {
                    JOptionPane errorMessage = new JOptionPane("Import microtheory is required",
                                                               JOptionPane.ERROR_MESSAGE);
                    JDialog errorMessageDialog = errorMessage.createDialog(panel, "ERROR");
                    errorMessageDialog.show();
                }
                importOwlIntoCyc();
                saveProperties();
            }
        });
        // Handle the url input combo box
        urlInputComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String selectedURL = urlInputComboBox.getSelectedItem().toString();
                System.out.println("Selected URL " + selectedURL);
                ImportOwlPanel.arrangeHistoryItems(selectedURL, urlHistory);
            }
        });
        // Handle the ontology nickname input combo box
        ontologyNicknameInputComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String selectedOntologyNickname = ontologyNicknameInputComboBox.getSelectedItem().toString();
                System.out.println("Selected ontology nickname " + selectedOntologyNickname);
                ImportOwlPanel.arrangeHistoryItems(selectedOntologyNickname, ontologyNicknameHistory);
            }
        });
        // Handle the import microtheory input combo box
        importMtInputComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String selectedImportMt = importMtInputComboBox.getSelectedItem().toString();
                System.out.println("Selected import microtheory " + selectedImportMt);
                ImportOwlPanel.arrangeHistoryItems(selectedImportMt, importMtHistory);
            }
        });
        obtainOwlPath();
    }

    /**
     * Imports the selected OWL ontology into Cyc
     */
    protected void importOwlIntoCyc () {
        try {
            OwlDocInfo owlDocInfo = new OwlDocInfo(owlPath, ontologyNickname, null, importMt, "OWLMappingMt");
            synchronized (ImportOwlApp.lock) {
              ImportOwlApp.lock.notify();
            }
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            JOptionPane errorMessagePane = new JOptionPane("Error occurred during OWL import\n" +
                                                           "",
                                                           JOptionPane.ERROR_MESSAGE);
            JDialog errorMessageDialog = errorMessagePane.createDialog(panel, "ERROR");
            errorMessageDialog.show();
            System.exit(1);
        }
    }

    /**
     * Initializes the ComboBox controls for this panel.
     */
    protected void initializeComboBoxes() {
        urlHistory = new String[NBR_OF_HISTORY_ITEMS];
        ontologyNicknameHistory = new String[NBR_OF_HISTORY_ITEMS];
        importMtHistory = new String[NBR_OF_HISTORY_ITEMS];

        try {
            properties = new Properties();
            properties.load(new FileInputStream(propertiesFilename));
        }
        catch (IOException e) {
            // If no property file, then initialize from scratch.
            urlHistory[0] = "http://www.cs.man.ac.uk/~horrocks/OWL/Ontologies/ka.owl";
            ontologyNicknameHistory[0] = "ka";
            importMtHistory[0] = "OWL-TestMt";
        }
        Enumeration propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            if (propertyName.startsWith("urlHistory")) {
                // urlHistoryN
                int i = Integer.parseInt(propertyName.substring(10));
                urlHistory[i] = properties.getProperty(propertyName);
            }
            if (propertyName.startsWith("ontologyNicknameHistory")) {
                // ontologyNicknameHistoryN
                int i = Integer.parseInt(propertyName.substring(23));
                ontologyNicknameHistory[i] = properties.getProperty(propertyName);
            }
            if (propertyName.startsWith("importMtHistory")) {
                // importMtHistoryN
                int i = Integer.parseInt(propertyName.substring(15));
                importMtHistory[i] = properties.getProperty(propertyName);
            }
        }
        for (int i = 0; i < urlHistory.length; i++) {
            Object item = urlHistory[i];
            if (item == null)
                break;
            urlInputComboBox.addItem(item);
        }
        for (int i = 0; i < ontologyNicknameHistory.length; i++) {
            Object item = ontologyNicknameHistory[i];
            if (item == null)
                break;
            ontologyNicknameInputComboBox.addItem(item);
        }
        for (int i = 0; i < importMtHistory.length; i++) {
            Object item = importMtHistory[i];
            if (item == null)
                break;
            importMtInputComboBox.addItem(item);
        }
    }


    /**
     * Saves input combo box history items in a properties file.
     */
    protected void saveProperties () {
        properties = new Properties();
        String propertyName;
        for (int i = 0; i < urlHistory.length; i++) {
            Object item = urlHistory[i];
            if (item == null)
                break;
            propertyName = "urlHistory" + Integer.toString(i);
            properties.setProperty(propertyName, urlHistory[i]);

        }
        for (int i = 0; i < ontologyNicknameHistory.length; i++) {
            Object item = ontologyNicknameHistory[i];
            if (item == null)
                break;
            propertyName = "ontologyNicknameHistory" + Integer.toString(i);
            properties.setProperty(propertyName, ontologyNicknameHistory[i]);
        }
        for (int i = 0; i < importMtHistory.length; i++) {
            Object item = importMtHistory[i];
            if (item == null)
                break;
            propertyName = "importMtHistory" + Integer.toString(i);
            properties.setProperty(propertyName, importMtHistory[i]);
        }
        try {
            properties.store(new FileOutputStream(propertiesFilename),
                             "Properties remembered for the ImportOwlPanel.");
        }
        catch (IOException e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }


    /**
     * Obtains the specified OWL path.
     *
     * @return the specified OWL path
     *
     */
    protected String obtainOwlPath() {
        return urlInputComboBox.getSelectedItem().toString();
    }

    /**
     * Obtains the specified ontology nickname.
     *
     * @return the specified ontology nickname
     */
    protected Object obtainOntologyNickname() {
        return ontologyNicknameInputComboBox.getSelectedItem();
    }

    /**
     * Obtains the specified import microtheory.
     *
     * @return the specified import microtheory
     */
    protected Object obtainImportMt() {
        return importMtInputComboBox.getSelectedItem();
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

    static protected void arrangeHistoryItems (String selectedItem, String [] historyItems) {
        String [] newHistoryItems = new String[historyItems.length];
        newHistoryItems[0] = selectedItem;
        int i = 1;
        for (int j = 0; j < historyItems.length; j++) {
            String item = (String) historyItems[j];
            if (item == null)
                break;
            if (selectedItem.equals(item))
                continue;
            if (! item.equals(""))
                newHistoryItems[i++] = historyItems[j];
        }
        for (i = 0; i < historyItems.length; i++)
            historyItems[i] = newHistoryItems[i];
    }

}
