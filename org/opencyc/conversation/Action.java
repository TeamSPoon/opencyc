package org.opencyc.conversation;

/**
 * Contains the attributes and behavior of a chat conversation Finite
 * State Machine Action.<p>
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

public class Action {

    /**
     * The unique identifying name of this action
     */
    protected String name;

    /**
     * optional content
     */
    protected Object content;

    /**
     * Constructs a new Action object givent the action name.
     *
     * @param name the action name
     */
    protected Action(String name) {
        this.name = name;
    }

    /**
     * Returns the action name.
     *
     * @return the action name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the action content.
     *
     * @param the action content
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
     * Returns <tt>true</tt> iff some object equals this object
     *
     * @param object the <tt>Object</tt> for equality comparison
     * @return equals <tt>boolean</tt> value indicating equality or non-equality.
     */
    public boolean equals(Object object) {
        if (! (object instanceof Action))
            return false;
        Action that = (Action) object;
        if (! (this.name.equals(that.name)))
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
     * Returns the string representation of this action
     *
     * @return the representation of the <tt>Action</tt> as a <tt>String</tt>
     */
    public String toString() {
        return name;
    }

}