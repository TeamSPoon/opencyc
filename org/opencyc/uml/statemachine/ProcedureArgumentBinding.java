package org.opencyc.uml.statemachine;

import org.opencyc.uml.action.*;

/**
 * Contains a binding of a procedure pin with a state machine variable or
 * literal.
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

public class ProcedureArgumentBinding {

    /**
     * the procedure argument pin
     */
    protected Pin pin;

    /**
     * the bound state machine variable or literal
     */
    protected Object boundObject;

    /**
     * Constructs a new ProcedureArgumentBinding object
     */
    public ProcedureArgumentBinding(Pin pin, Object boundObject) {
        if (pin instanceof OutputPin &&
            ! (boundObject instanceof StateVariable))
            throw new RuntimeException("Cannot bind the output pin " + pin.toString() +
                                       " to the non state variable " + boundObject.toString());
        this.pin = pin;
        this.boundObject = boundObject;
    }

    /**
     * Gets the procedure argument pin
     *
     * @return the procedure argument pin
     */
    public Pin getPin () {
        return pin;
    }

    /**
     * Gets the bound state machine variable or literal
     *
     * @return the bound state machine variable or literal
     */
    public Object getBoundObject () {
        return boundObject;
    }


}