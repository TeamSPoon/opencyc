package  org.opencyc.jini.admin;

/**
 * Provides a browser gui for administering jini agent services
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
import  java.rmi.*;
import  java.rmi.RemoteException;
import  java.util.*;
import  javax.swing.*;
import  javax.swing.border.*;
import  javax.swing.event.*;
import  javax.swing.tree.*;
import  net.jini.admin.*;
import  net.jini.core.lookup.*;
import  net.jini.discovery.*;
import  net.jini.core.event.*;
import  net.jini.lookup.entry.*;
import  com.sun.jini.lease.*;
import  org.opencyc.jini.shared.*;
import  org.opencyc.util.*;


public class AdminExplorer extends JPanel {
    protected JPanel panel;
    protected Container container;
    protected JFrame frame;
    protected LookupDiscovery disco;
    protected DiscoveryListener discoveryListener;
    protected DefaultMutableTreeNode lookupServices;
    protected DefaultMutableTreeNode lookupService;
    protected DefaultMutableTreeNode service;
    protected DefaultMutableTreeNode attribute;
    protected DefaultTreeModel treeModel;
    protected DefaultTreeSelectionModel treeSelectionModel;
    private static final int MIN_EXPLORER_FRAME_HEIGHT = 300;
    private static final int MIN_EXPLORER_FRAME_WIDTH = 300;
    private static final int MIN_ADMIN_FRAME_HEIGHT = 400;
    private static final int MIN_ADMIN_FRAME_WIDTH = 400;
    private static final int INITIAL_EXPLORER_FRAME_HEIGHT = 400;
    private static final int INITIAL_EXPLORER_FRAME_WIDTH = 600;
    private static final int INITIAL_ADMIN_FRAME_HEIGHT = 600;
    private static final int INITIAL_ADMIN_FRAME_WIDTH = 600;
    protected ServiceTemplate template = new ServiceTemplate(null, new Class[] {
        Object.class
    }, null);
    BorderLayout borderLayout1 = new BorderLayout();
    Box boxButtons;
    JButton btnExit = new JButton();
    Component glueButtons;
    JButton btnAdminister = new JButton();
    JButton btnRefresh = new JButton();
    Border border1;
    JScrollPane jScrollPane1 = new JScrollPane();
    JTree treeExplorer = new JTree();

    /**
     * Constructs a new AdminExplorer object.
     */
    public AdminExplorer () throws IOException, RemoteException {
        panel = this;
        // Provide security manager required to download classes via RMI.
        System.setSecurityManager(new RMISecurityManager());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        treeExplorer.putClientProperty("JTree.lineStyle", "Angled");
        treeExplorer.setCellRenderer(new ServiceCellRenderer());
        // Handle the Refresh button.
        btnRefresh.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((AdminExplorer)panel).refresh();
            }
        });
        // Handle the Administer button.
        btnAdminister.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                ((AdminExplorer)panel).administerService();
            }
        });
        // Handle the Exit button.
        btnExit.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                getFrame().dispose();
                terminate();
            }
        });
        lookupServices = new DefaultMutableTreeNode("Lookup Services");
        treeModel = new DefaultTreeModel(lookupServices);
        treeExplorer.setModel(treeModel);
        treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        treeExplorer.setSelectionModel(treeSelectionModel);
        treeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged (TreeSelectionEvent e) {
                ((AdminExplorer)panel).treeNodeSelected();
            }
        });
        // Set up for discovery
        Log.current.println("Discovering Lookup Services");
        disco = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        discoveryListener = new Discoverer(this);
        disco.addDiscoveryListener(discoveryListener);
    }

    /**
     * Initializes GUI (JBuilder generated).
     */
    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 4, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        boxButtons = Box.createHorizontalBox();
        glueButtons = Box.createGlue();
        border1 = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray,
                20), BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(borderLayout1);
        btnExit.setBorder(myBorder);
        btnExit.setMaximumSize(new Dimension(100, 30));
        btnExit.setPreferredSize(new Dimension(100, 30));
        btnExit.setText("Exit");
        btnAdminister.setEnabled(false);
        btnAdminister.setBorder(myBorder);
        btnAdminister.setMaximumSize(new Dimension(100, 30));
        btnAdminister.setMinimumSize(new Dimension(100, 30));
        btnAdminister.setPreferredSize(new Dimension(100, 30));
        btnAdminister.setText("Administer");
        btnRefresh.setBorder(myBorder);
        btnRefresh.setMaximumSize(new Dimension(100, 30));
        btnRefresh.setMinimumSize(new Dimension(100, 30));
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.setText("Refresh Now");
        treeExplorer.setAutoscrolls(true);
        this.setPreferredSize(new Dimension(200, 400));
        this.add(boxButtons, BorderLayout.SOUTH);
        boxButtons.add(glueButtons, null);
        boxButtons.add(btnRefresh, null);
        boxButtons.add(btnAdminister, null);
        boxButtons.add(btnExit, BorderLayout.SOUTH);
        this.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(treeExplorer, null);
    }

    /**
     * Provides the main method.
     */
    public static void main (String[] args) {
        Log.makeLog();
        Log.current.println("Starting AdminExplorer");
        try {
            final JFrame explorerFrame = new JFrame("Administer Services");
            explorerFrame.addWindowListener(new WindowAdapter() {

                public void windowClosing (WindowEvent ev) {
                    terminate();
                }
            });
            explorerFrame.addComponentListener(new ComponentAdapter() {

                public void componentResized (ComponentEvent e) {
                    Dimension d = explorerFrame.getSize();
                    if ((d.width >= MIN_EXPLORER_FRAME_WIDTH) && (d.height >= MIN_EXPLORER_FRAME_HEIGHT))
                        return;
                    int width = d.width;
                    int height = d.height;
                    if (width < MIN_EXPLORER_FRAME_WIDTH)
                        width = MIN_EXPLORER_FRAME_WIDTH;
                    if (height < MIN_EXPLORER_FRAME_HEIGHT)
                        height = MIN_EXPLORER_FRAME_HEIGHT;
                    explorerFrame.setSize(width, height);
                }
            });
            explorerFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("cyc-logo-16.jpg"));
            explorerFrame.getContentPane().add(new AdminExplorer());
            explorerFrame.pack();
            explorerFrame.setSize(INITIAL_EXPLORER_FRAME_WIDTH, INITIAL_EXPLORER_FRAME_HEIGHT);
            explorerFrame.setVisible(true);
        } catch (Exception ex) {
            Log.current.errorPrintln("Couldn't create browser:" + ex.getMessage());
            Log.current.printStackTrace(ex);
        }
    }

    /**
     * Terminates the application
     */
    private static void terminate () {
        Log.current.println("Exiting AdminExplorer");
        System.exit(0);
    }

    /**
     * Refreshes the display by rediscovering the lookup services.
     */
    private void refresh () {
        Log.current.println("Removing Discovery Listener");
        disco.removeDiscoveryListener(discoveryListener);
        discoveryListener = new Discoverer(this);
        Log.current.println("Discovering Lookup Services");
        disco.addDiscoveryListener(discoveryListener);
    }

    /**
     * Obtains Lookup Service Registrars and puts them into the tree.
     */
    private void obtainRegistrars (ServiceRegistrar regs[]) {
        lookupServices = new DefaultMutableTreeNode("Lookup Services");
        treeModel = new DefaultTreeModel(lookupServices);
        treeExplorer.setModel(treeModel);
        for (int i = 0; i < regs.length; i++) {
            ServiceRegistrar registrar = regs[i];
            String locator;
            try {
                locator = (new String("Lookup service at ")).concat(registrar.getLocator().toString());
            } catch (RemoteException ex) {
                Log.current.errorPrintln("Error getting locator: " + ex.getMessage());
                locator = registrar.toString();
            }
            NodeInfo nodeInfo = new NodeInfo(registrar, locator);
            lookupService = new DefaultMutableTreeNode(nodeInfo);
            lookupServices.add(lookupService);
            obtainServices(registrar, lookupService);
        }
        treeExplorer.expandRow(0);
        if (regs.length > 0)
            treeExplorer.expandRow(1);
    }

    /**
     * Obtains registered services and put them into the tree.
     */
    void obtainServices (ServiceRegistrar reg, DefaultMutableTreeNode lookupService) {
        ServiceMatches matches;
        try {
            // Find all registered services.
            matches = reg.lookup(template, Integer.MAX_VALUE);
        } catch (RemoteException ex) {
            showDialog("Problem contacting registrar:\n" + ex.getMessage());
            disco.discard(reg);
            return;
        }
        for (int i = 0; i < matches.totalMatches; i++) {
            ServiceItem item = matches.items[i];
            String name = null;
            // Use the Name attribute, if there is one.
            for (int j = 0; j < item.attributeSets.length; j++) {
                if (item.attributeSets[j] instanceof Name) {
                    name = ((Name)item.attributeSets[j]).name;
                    break;
                }
            }
            if (name == null) {
                if (item.service instanceof ServiceRegistrar) {
                    try {
                        name = (new String("Lookup service at ")).concat(((ServiceRegistrar)item.service).getLocator().toString());
                    } catch (RemoteException ex) {
                        Log.current.errorPrintln("Error getting locator: " + ex.getMessage());
                        name = item.service.toString();
                    }
                }
                else
                    name = declassify(item.service.getClass().getName());
            }
            NodeInfo nodeInfo = new NodeInfo(item.service, name);
            service = new DefaultMutableTreeNode(nodeInfo);
            lookupService.add(service);
            obtainAttributes(item, service);
        }
    }

    /**
     * Obtains the attributes of a service and puts them into the tree.
     */
    void obtainAttributes (ServiceItem item, DefaultMutableTreeNode service) {
        for (int i = 0; i < item.attributeSets.length; i++) {
            String name = readableAttribute(item.attributeSets[i].toString());
            NodeInfo nodeInfo = new NodeInfo(item.attributeSets[i], name);
            attribute = new DefaultMutableTreeNode(nodeInfo);
            service.add(attribute);
        }
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
     * Creates a more readable attribute.
     */
    static String readableAttribute (String attribute) {
        if (attribute == null)
            return  null;
        int index = attribute.indexOf('(');
        if (index == -1)
            return  declassify(attribute);
        String firstPart = attribute.substring(0, index);
        String lastPart = attribute.substring(index);
        firstPart = declassify(firstPart).concat("  ");
        return  firstPart.concat(lastPart);
    }

    /**
     * Cleans up a classname (strip off package part)
     */
    static String declassify (String name) {
        if (name == null)
            return  null;
        int index = name.lastIndexOf('.');
        if (index == -1)
            return  name;
        else
            return  name.substring(index + 1);
    }

    void showDialog (String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                null, null, null);
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this), "Alert!");
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(pane, BorderLayout.CENTER);
        dialog.pack();
        dialog.show();
    }

    /**
     * Administers the selected service.
     */
    private void administerService () {
        TreePath treePath = treeSelectionModel.getSelectionPath();
        if (treePath == null) {
            btnAdminister.setEnabled(false);
            return;
        }
        DefaultMutableTreeNode selectedPathComponent = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        Object selectedObject = selectedPathComponent.getUserObject();
        if (selectedObject instanceof NodeInfo) {
            Object userObject = ((NodeInfo)selectedObject).getUserObject();
            if (userObject instanceof Administrable) {
                Log.current.println("Administer " + userObject);
                AdminPanel panel = null;
                if (userObject instanceof GenericServiceInterface)
                    panel = new AdminPanel(userObject);
                else {
                    try {
                        panel = new AdminPanel(((Administrable)userObject).getAdmin());
                    } catch (RemoteException e) {
                        showDialog("Could not obtain administration interface:\n" + e.getMessage());
                        Log.current.errorPrintln("Could not obtain administration interface " + e.getMessage());
                    }
                }
                final JFrame frame = new JFrame("Administration of " + ((NodeInfo)selectedObject).toString());
                frame.addComponentListener(new ComponentAdapter() {

                    public void componentResized (ComponentEvent e) {
                        Dimension d = frame.getSize();
                        if ((d.width >= MIN_ADMIN_FRAME_WIDTH) && (d.height >= MIN_ADMIN_FRAME_HEIGHT))
                            return;
                        int width = d.width;
                        int height = d.height;
                        if (width < MIN_ADMIN_FRAME_WIDTH)
                            width = MIN_ADMIN_FRAME_WIDTH;
                        if (height < MIN_ADMIN_FRAME_HEIGHT)
                            height = MIN_ADMIN_FRAME_HEIGHT;
                        frame.setSize(width, height);
                    }
                });
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage("cyc-logo-16.jpg"));
                frame.getContentPane().add(panel);
                frame.pack();
                frame.setSize(INITIAL_ADMIN_FRAME_WIDTH, INITIAL_ADMIN_FRAME_HEIGHT);
                frame.setVisible(true);
            }
        }
    }

    /**
     * Handles a tree node selection event.
     */
    private void treeNodeSelected () {
        TreePath treePath = treeSelectionModel.getSelectionPath();
        if (treePath == null) {
            btnAdminister.setEnabled(false);
            return;
        }
        DefaultMutableTreeNode selectedPathComponent = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        Object selectedObject = selectedPathComponent.getUserObject();
        if (selectedObject instanceof NodeInfo) {
            Object userObject = ((NodeInfo)selectedObject).getUserObject();
            if (userObject instanceof Administrable) {
                btnAdminister.setEnabled(true);
            }
            else
                btnAdminister.setEnabled(false);
        }
        else
            btnAdminister.setEnabled(false);
    }

    /**
     * Class NodeInfo contains the TreeNode object and a toString() method for it.
     */
    class NodeInfo {
        private Object userObject;
        private String name;
        /**
         * Service's icon.
         */
        private Icon icon;

        NodeInfo (Object userObject, String name) {
            this.userObject = userObject;
            this.name = name;
        }

        public Object getUserObject () {
            return  userObject;
        }

        public void setIcon (Icon icon) {
            this.icon = icon;
        }

        public Icon getIcon () {
            return  icon;
        }

        public String toString () {
            return  name;
        }
    }

    /**
     * Class ServiceCellRenderer displays customized icons for services.
     */
    class ServiceCellRenderer extends DefaultTreeCellRenderer {
        final Icon defaultServiceIcon = new ImageIcon("gr_ball.gif");
        final Icon lookupServiceIcon = new ImageIcon("binoculars.gif");
        final Icon unreachableServiceIcon = new ImageIcon("rd_diam.gif");

        ServiceCellRenderer () {
            super();
            leafIcon = new ImageIcon("small_red_ball.gif");
        }

        public Component getTreeCellRendererComponent (JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            // Set instance variables with a call to the superclass method.
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // Default behavior for non-service (attribute) nodes.
            setBackgroundSelectionColor(Color.white);
            setToolTipText("a tool tip");
            // Customized behavior for service nodes.
            if (!leaf && (value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object renderedObject = ((DefaultMutableTreeNode)value).getUserObject();
                if (renderedObject instanceof NodeInfo) {
                    NodeInfo nodeInfo = (NodeInfo)renderedObject;
                    Icon serviceIcon = nodeInfo.getIcon();
                    Object userObject = nodeInfo.getUserObject();
                    if (userObject instanceof GenericServiceInterface) {
                        setBackgroundSelectionColor(Color.cyan);
                        if (serviceIcon == null) {
                            serviceIcon = defaultServiceIcon;
                            try {
                                // Get icon from the service.
                                serviceIcon = ((GenericServiceInterface) userObject).getIcon();
                                // cache service's icon to avoid calling it again.
                                nodeInfo.setIcon(serviceIcon);
                            }
                            catch (RemoteException e) {
                                serviceIcon = unreachableServiceIcon;
                                Log.current.println("Cannot get icon from " + userObject);
                            }
                        }
                    }
                    else if (userObject instanceof ServiceRegistrar) {
                        if (serviceIcon == null) {
                            serviceIcon = lookupServiceIcon;
                            nodeInfo.setIcon(serviceIcon);
                        }
                        this.setBackgroundSelectionColor(Color.cyan);
                    }
                    else if (userObject instanceof Administrable) {
                        if (serviceIcon == null) {
                            serviceIcon = defaultServiceIcon;
                            nodeInfo.setIcon(serviceIcon);
                        }
                        this.setBackgroundSelectionColor(Color.cyan);
                    }
                    setIcon(serviceIcon);
                }
            }
            return  this;
        }
    }

    /**
     * Class Discoverer handles lookup service discovery.
     */
    class Discoverer
            implements DiscoveryListener {
        protected AdminExplorer adminExplorer;

        Discoverer (AdminExplorer adminExplorer) {
            this.adminExplorer = adminExplorer;
        }

        public void discovered (DiscoveryEvent ev) {
            Log.current.println("Discovered Lookup Service " + ev);
            adminExplorer.obtainRegistrars(ev.getRegistrars());
        }

        public void discarded (DiscoveryEvent ev) {
            Log.current.println("Notified of discarded Lookup Service " + ev);
        }
    }
}



