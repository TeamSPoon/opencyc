package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering service Discovery.
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
import  javax.swing.event.*;
import  java.awt.event.*;
import  java.awt.*;
import  net.jini.lookup.DiscoveryAdmin;
import  org.opencyc.util.*;


public class DiscoveryAdminPanel extends JPanel {
    protected ListBox listbox;
    protected JTextField portField = new JTextField();
    protected JLabel currentPort = new JLabel();
    protected DiscoveryAdmin admin;

    // An action for adding new groups
    class AddAction extends AbstractAction {

        public void actionPerformed (ActionEvent ev) {
            System.out.println("In add!");
            String[] newGroups = new String[1];
            newGroups[0] = listbox.getField().getText();
            if (newGroups[0].equals(""))
                newGroups[0] = "<public>";
            try {
                admin.addMemberGroups(newGroups);
            } catch (Exception ex) {
                Log.current.errorPrintln("Couldn't add group: " + ex.getMessage());
            }
        }
    }

    // An action for removing groups
    class RemoveAction extends AbstractAction {

        public void actionPerformed (ActionEvent ev) {
            Object sel = listbox.getSelectedValue();
            if (sel == null) {
                return;
            }
            try {
                String[] items = new String[1];
                sel = (sel.equals("<public>") ? "" : sel);
                items[0] = (String)sel;
                admin.removeMemberGroups(items);
            } catch (Exception ex) {
                Log.current.errorPrintln("Couldn't remove group: " + ex.getMessage());
            }
        }
    }

    // An action for changing groups
    class UpdateAction extends AbstractAction {

        public void actionPerformed (ActionEvent ev) {
            try {
                String[] members = admin.getMemberGroups();
                for (int i = 0; i < members.length; i++) {
                    if (members[i].equals(""))
                        members[i] = "<public>";
                }
                listbox.getList().setListData(members);
            } catch (Exception ex) {
                System.err.println("Couldn't update: " + ex.getMessage());
                listbox.getList().setListData(new String[0]);
            }
        }
    }

    class SetPortAction extends AbstractAction {

        public void actionPerformed (ActionEvent ev) {
            try {
                admin.setUnicastPort(Integer.parseInt(portField.getText()));
                currentPort.setText(Integer.toString(admin.getUnicastPort()));
            } catch (Exception ex) {
                Log.current.errorPrintln("Couldn't change port: " + ex.getMessage());
            }
        }
    }

    public DiscoveryAdminPanel (DiscoveryAdmin da) {
        super();
        admin = da;
        setLayout(new BorderLayout());
        // Create a ListBox and initialize it with our
        // actions
        listbox = new ListBox("Member Groups", 12);
        listbox.setActions(new AddAction(), new RemoveAction(), new UpdateAction());
        JPanel portPanel = new JPanel();
        portPanel.setLayout(new BorderLayout());
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 2));
        gridPanel.add(new JLabel("Current port: "));
        try {
            currentPort.setText(Integer.toString(admin.getUnicastPort()));
        } catch (Exception ex) {
            Log.current.errorPrintln("Error setting port: " + ex.getMessage());
        }
        gridPanel.add(currentPort);
        gridPanel.add(new JLabel("New port: "));
        gridPanel.add(portField);
        portPanel.add(gridPanel, BorderLayout.CENTER);
        JButton button = new JButton("Set");
        button.addActionListener(new SetPortAction());
        portPanel.add(button, BorderLayout.SOUTH);
        add(listbox, BorderLayout.CENTER);
        add(portPanel, BorderLayout.SOUTH);
    }
}



