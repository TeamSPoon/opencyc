package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering jini agent shutdown (destroy) services
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
import  javax.swing.*;
import  javax.swing.border.*;
import  java.awt.event.*;
import  java.awt.*;
import  java.rmi.RemoteException;
import  com.sun.jini.admin.DestroyAdmin;

public class DestroyAdminPanel extends JPanel {
    protected Container container;
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JPanel jPanel1 = new JPanel();
    FlowLayout flowLayout1 = new FlowLayout();
    JButton btnTerminate = new JButton();
    JButton btnCancel = new JButton();

    /**
     * Constructs a new DestroyAdminPanel object given a reference to the DestroyAdmin service.
     */
    public DestroyAdminPanel (final DestroyAdmin admin) {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        container = this;
        btnTerminate.addActionListener(new ActionListener() {

            public void actionPerformed (ActionEvent ev) {
                try {
                    admin.destroy();
                } catch (RemoteException e) {}
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
    }

    private void jbInit () throws Exception {
        Border myBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2,
                2), BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134,
                134, 134), new Color(93, 93, 93)));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("CAUTION:");
        this.setLayout(borderLayout1);
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Clicking will terminate the service!");
        jPanel1.setLayout(flowLayout1);
        btnTerminate.setBorder(myBorder);
        btnTerminate.setMaximumSize(new Dimension(91, 35));
        btnTerminate.setMinimumSize(new Dimension(91, 35));
        btnTerminate.setPreferredSize(new Dimension(91, 35));
        btnTerminate.setText("Terminate");
        btnCancel.setBorder(myBorder);
        btnCancel.setMaximumSize(new Dimension(73, 35));
        btnCancel.setMinimumSize(new Dimension(73, 35));
        btnCancel.setPreferredSize(new Dimension(73, 35));
        btnCancel.setText("Cancel");
        this.add(jLabel1, BorderLayout.NORTH);
        this.add(jLabel2, BorderLayout.CENTER);
        this.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(btnTerminate, null);
        jPanel1.add(btnCancel, null);
    }
}



