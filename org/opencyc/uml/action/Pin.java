package org.opencyc.uml.action;

import org.opencyc.uml.core.*;

/**
 * Pin from the UML Action package.
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

public abstract class Pin extends ModelElement {

    /**
     * the type of this pin
     */
    protected Class type;

    /**
     * the number of values this pin may hold at any one time
     */
    protected int multiplicity;

    /**
     * indicates whether the set of values held by this pin is
     * ordered or not
     */
    protected int ordering;

    public static final int UNORDERED = 1;
    public static final int ORDERED = 2;

    /**
     * Constructs a new Pin object
     */
    public Pin () {
    }

    /**
     * Gets the type of this pin.
     *
     * @return the type of this pin
     */
    public Class getType () {
        return type;
    }

    /**
     * Sets the type of this pin.
     *
     * @param type the type of this pin
     */
    public void setType (Class type) {
        this.type = type;
    }

    /**
     * Gets the number of values this pin may hold at any one time
     *
     * @return the number of values this pin may hold at any one time
     */
    public int getMultiplicity () {
        return multiplicity;
    }

    /**
     * Sets the number of values this pin may hold at any one time
     *
     * @param multiplicity the number of values this pin may hold at any one time
     */
    public void setMultiplicity (int multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Gets whether the set of values held by this pin is
     * ordered or not
     *
     * @return whether the set of values held by this pin is
     * ordered or not
     */
    public int getOrdering () {
        return ordering;
    }

    /**
     * Sets whether the set of values held by this pin is
     * ordered or not
     *
     * @param ordering whether the set of values held by this pin is
     * ordered or not
     */
    public void setOrdering (int ordering) {
        this.ordering = ordering;
    }

}