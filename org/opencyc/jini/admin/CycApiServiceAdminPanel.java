/*

 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering cyc api proxy agent services
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
import  javax.swing.*;
import  java.rmi.RemoteException;
import  javax.swing.border.*;
import  org.opencyc.jini.cycproxy.*;
import  org.opencyc.util.*;
import  org.opencyc.api.*;


public class CycApiServiceAdminPanel extends JPanel {
    protected Container container;
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JButton btnSubmit = new JButton();
    JButton btnCancel = new JButton();
    JSplitPane jSplitPane1 = new JSplitPane();
    JPanel jPanel2 = new JPanel();
    JPanel jPanel3 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JEditorPane edtApiCommand = new JEditorPane();
    JTextPane txtpApiResponse = new JTextPane();

    public CycApiServiceAdminPanel (final CycApiServiceInterface admin) {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        container = this;
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                while (true) {
                    // Ascend the container ancestory path, find the containing JFrame
                    // and close it.
                    container = container.getParent();
                    if (container instanceof JFrame) {
                        ((JFrame)container).dispose();
                        break;
                    }
                }
            }
        });
        btnSubmit.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                String apiRequest = edtApiCommand.getText();
                Log.current.println("Cyc API request " + apiRequest);
                String cycResponse = "";
                try {
                    cycResponse = admin.cycApiRequest(apiRequest);
                } catch (CycApiException e) {
                    cycResponse = e.getMessage();
                } catch (RemoteException e) {}
                Log.current.println("Cyc API response " + cycResponse);
                txtpApiResponse.setText(cycResponse);
            }
        });
    }

    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        this.setLayout(borderLayout1);
        btnSubmit.setBorder(myBorder);
        btnSubmit.setMaximumSize(new Dimension(73, 35));
        btnSubmit.setMinimumSize(new Dimension(73, 35));
        btnSubmit.setPreferredSize(new Dimension(73, 35));
        btnSubmit.setToolTipText("Submit the Cyc API command");
        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed (ActionEvent e) {
                btnSubmit_actionPerformed(e);
            }
        });
        btnCancel.setBorder(myBorder);
        btnCancel.setMaximumSize(new Dimension(73, 35));
        btnCancel.setMinimumSize(new Dimension(73, 35));
        btnCancel.setPreferredSize(new Dimension(73, 35));
        btnCancel.setToolTipText("Abandon this activity, disregard a Cyc response if waiting.");
        btnCancel.setText("Cancel");
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLastDividerLocation(200);
        jPanel2.setLayout(borderLayout2);
        jPanel3.setLayout(borderLayout3);
        jLabel1.setText("Cyc Response");
        jLabel2.setText("Enter Cyc API command");
        txtpApiResponse.setToolTipText("Cyc\'s response to the API command appears here.");
        txtpApiResponse.setEnabled(false);
        txtpApiResponse.setEditable(false);
        edtApiCommand.setToolTipText("Enter the Cyc API command here.  For example (fi-find \"Thing\").");
        this.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(btnSubmit, null);
        jPanel1.add(btnCancel, null);
        this.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jPanel2, JSplitPane.TOP);
        jPanel2.add(jLabel2, BorderLayout.NORTH);
        jPanel2.add(edtApiCommand, BorderLayout.CENTER);
        jSplitPane1.add(jPanel3, JSplitPane.BOTTOM);
        jPanel3.add(jLabel1, BorderLayout.NORTH);
        jPanel3.add(txtpApiResponse, BorderLayout.CENTER);
        jSplitPane1.setDividerLocation(200);
    }

    void btnSubmit_actionPerformed (ActionEvent e) {}
}



