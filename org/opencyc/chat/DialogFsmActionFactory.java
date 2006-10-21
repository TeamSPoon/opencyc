package org.opencyc.chat;

import java.util.HashMap;

/**
 * Makes actions and uses a cache to create only unique instances.<p>
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
public class DialogFsmActionFactory {

    /**
     * Caches DialogFsmAction objects to keep from making them twice.
     * action --> DialogFsmAction
     */
    static HashMap dialogFsmActionCache = new HashMap();

     /**
     * Constructs a new DialogFsmActionFactory object.
     */
    public DialogFsmActionFactory() {
    }

    /**
     * Makes a new DialogFsmAction object.
     */
    public DialogFsmAction makeDialogFsmAction (String action) {
        DialogFsmAction dialogFsmAction = (DialogFsmAction) dialogFsmActionCache.get(action);
        if (dialogFsmAction != null)
            return dialogFsmAction;
        dialogFsmAction = new DialogFsmAction(action);
        dialogFsmActionCache.put(action, dialogFsmAction);
        return dialogFsmAction;
    }

    /**
     * Returns a DialogFsmAction object given the event name.
     *
     * @param action the event name
     * @return the DialogFsmAction object having the event hame or null
     * if it does not exist
     */
    public DialogFsmAction getDialogFsmAction (String action) {
        return (DialogFsmAction) dialogFsmActionCache.get(action);
    }
}