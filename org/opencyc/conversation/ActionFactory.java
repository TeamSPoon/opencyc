package org.opencyc.conversation;

import java.util.*;

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
public class ActionFactory {

    /**
     * Caches Action objects to keep from making them twice.
     * action --> Action
     */
    static HashMap actionCache = new HashMap();

     /**
     * Constructs a new ActionFactory object.
     */
    public ActionFactory() {
    }

    /**
     * Makes a new Action object given its name
     *
     * @param name the action name
     */
    public Action makeAction (String name) {
        Action action = (Action) actionCache.get(name);
        if (action != null)
            return action;
        action = new Action(name);
        actionCache.put(name, action);
        return action;
    }

    /**
     * Returns a Action object given the event name.
     *
     * @param action the event name
     * @return the Action object having the event hame or null
     * if it does not exist
     */
    public Action getAction (String action) {
        return (Action) actionCache.get(action);
    }
}