package org.opencyc.uml.statemachine;

import java.util.*;
import org.opencyc.uml.core.*;

/**
 * Event from the UML State_Machine package.
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

public class Event extends ModelElement {

    /**
     * the parameters of this event
     */
    protected ArrayList parameter = new ArrayList();

    /**
     * Constructs a new Event object.
     */
    public Event() {
    }

    /**
     * Returns true if the given object equals this object.
     *
     * @param object the given object
     * @return true if the given object equals this object, otherwise returns false
     */
    public boolean equals (Object object) {
        return object instanceof Event;
    }

    /**
     * Gets the parameters of this event.
     *
     * @return the parameters of this event
     */
    public ArrayList getParameter () {
        return parameter;
    }

    /**
     * Sets the parameters of this event.
     *
     * @param parameter the parameters of this event
     */
    public void setParameter (ArrayList parameter) {
        this.parameter = parameter;
    }
}