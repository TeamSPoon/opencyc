package  org.opencyc.jini.admin;

/**
 * Provides an admin panel for administering jini agent services
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
import  net.jini.admin.JoinAdmin;
import  net.jini.lookup.DiscoveryAdmin;
import  com.sun.jini.admin.*;
import  java.awt.*;
import  org.opencyc.util.*;
import  org.opencyc.webserver.*;
import  org.opencyc.jini.cycproxy.*;


public class AdminPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    JTabbedPane pane = new JTabbedPane();

    public AdminPanel (Object admin) {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.current.println("admin " + admin);
        // Specialized admin panels precede the universal admin panels.
        if (admin instanceof WebServerAdminInterface) {
            WebServerPathsAdminPanel panel = new WebServerPathsAdminPanel((WebServerAdminInterface)admin);
            pane.addTab("Paths", panel);
        }
        if (admin instanceof WebServerAdminInterface) {
            WebServerCacheAdminPanel panel = new WebServerCacheAdminPanel((WebServerAdminInterface)admin);
            pane.addTab("Cache", panel);
        }
        if (admin instanceof CycApiServiceInterface) {
            CycApiServiceAdminPanel panel = new CycApiServiceAdminPanel((CycApiServiceInterface)admin);
            pane.addTab("Cyc API", panel);
        }
        if (admin instanceof DiscoveryAdmin) {
            DiscoveryAdminPanel panel = new DiscoveryAdminPanel((DiscoveryAdmin)admin);
            pane.addTab("Discovery", panel);
        }
        if (admin instanceof StorageLocationAdmin) {
            StorageLocationAdminPanel panel = new StorageLocationAdminPanel((StorageLocationAdmin)admin);
            pane.addTab("Storage", panel);
        }
        if (admin instanceof DestroyAdmin) {
            DestroyAdminPanel panel = new DestroyAdminPanel((DestroyAdmin)admin);
            pane.addTab("Destroy", panel);
        }
    }

    private void jbInit () throws Exception {
        this.setLayout(borderLayout1);
        this.setMinimumSize(new Dimension(800, 400));
        this.setPreferredSize(new Dimension(800, 400));
        this.add(pane, BorderLayout.CENTER);
    }
}



