package org.opencyc.chat;

/**
 * Makes events and uses a cache to create only unique instances.<p>
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

import java.util.*;

public class DialogFsmEventFactory {

    /**
     * Caches DialogFsmEvent objects to keep from making them twice.
     * eventName --> DialogFsmEvent
     */
    static HashMap dialogFsmEventCache = new HashMap();

    /**
     * Creates a new DialogFsmEventFactory object.
     */
    public DialogFsmEventFactory() {
    }

    /**
     * Makes a new DialogFsmEvent object.
     */
    public DialogFsmEvent makeDialogFsmEvent (String eventName) {
        DialogFsmEvent dialogFsmEvent = (DialogFsmEvent) dialogFsmEventCache.get(eventName);
        if (dialogFsmEvent != null)
            return dialogFsmEvent;
        dialogFsmEvent = new DialogFsmEvent(eventName);
        dialogFsmEventCache.put(eventName, dialogFsmEvent);
        return dialogFsmEvent;
    }

    /**
     * Returns a DialogFsmEvent object given the event name.
     *
     * @param eventName the event name
     * @return the DialogFsmEvent object having the event hame or null
     * if it does not exist
     */
    public DialogFsmEvent getDialogFsmEvent (String eventName) {
        return (DialogFsmEvent) dialogFsmEventCache.get(eventName);
    }

}