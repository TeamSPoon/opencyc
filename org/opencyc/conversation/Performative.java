package org.opencyc.conversation;

import java.util.*;

/**
 * Contains the attributes and behavior of a chat conversation Finite
 * State Machine Performative.<p>
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
public class Performative implements Comparable {

    /**
     * performative name
     */
    protected String performativeName;

    /**
     * optional content
     */
    protected Object content;

    /**
     * Constructs a new Performative object given its name.
     *
     * @param performativeName the performative name
     */
    public Performative(String performativeName) {
        this.performativeName = performativeName;
    }

    /**
     * Constructs a new Performative object given its name and
     * content
     *
     * @param performativeName the performative name
     * @param content the content
     */
    public Performative(String performativeName, Object content) {
        this.performativeName = performativeName;
        this.content = content;
    }

    /**
     * Returns the performative name.
     *
     * @return the performative name
     */
    public String getPerformativeName () {
        return performativeName;
    }

    /**
     * Sets the performative content.
     *
     * @param the performative content
     */
    public void setContent (Object content) {
        this.content = content;
    }

    /**
     * Returns the performative content.
     *
     * @return the performative content
     */
    public Object getContent () {
        return content;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     *
     * @param object the reference object with which to compare.
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
     public int compareTo (Object object) {
        if (! (object instanceof Performative))
            throw new ClassCastException("Must be a Performative object");
        return performativeName.compareTo(((Performative) object).performativeName);
     }

    /**
     * Returns <tt>true</tt> iff some object equals this object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Performative))
            return false;
        Performative that = (Performative) object;
        if (! (this.performativeName.equals(that.performativeName)))
            return false;
        if (this.content != null) {
            if (that.content != null)
                return this.content.equals(that.content);
            else
                return false;
        }
        if (that.content == null)
            return true;
        else
            return false;
    }

    /**
     * Returns the string representation of this perfomative
     *
     * @return the representation of the <tt>Template</tt> as a <tt>String</tt>
     */
    public String toString() {
        return performativeName;
    }

}