package org.opencyc.uml.action;

import org.opencyc.uml.core.ModelElement;
import org.opencyc.uml.statemachine.StateVariable;

/**
 * OutputBinding which is an extenstion to the UML action package.
 *
 * This is a container for an input pin binding during a procedure call.
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

public class OutputBinding extends ModelElement {

    /**
     * the bound output pin
     */
    protected OutputPin boundOutputPin;

    /**
     * the bound output state variable
     */
    protected StateVariable boundOutputStateVariable;

    /**
     * Creates a new OutputBinding object.
     *
     * @param boundOutputPin
     * @param boundOutputValueExpression
     */
    public OutputBinding(OutputPin boundOutputPin,
                         StateVariable boundOutputStateVariable) {
        this.boundOutputPin = boundOutputPin;
        this.boundOutputStateVariable = boundOutputStateVariable;
    }

    /**
     * Creates a new OutputBinding object.
     */
    public OutputBinding() {
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString () {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(boundOutputPin.toString());
        stringBuffer.append(" --> ");
        stringBuffer.append(boundOutputStateVariable.toString());
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * Gets the bound output pin
     *
     * @return the bound output pin
     */
    public OutputPin getBoundOutputPin () {
        return boundOutputPin;
    }

    /**
     * Sets the bound output pin
     *
     * @param boundOutputPin the bound output pin
     */
    public void setBoundOutputPin (OutputPin boundOutputPin) {
        this.boundOutputPin = boundOutputPin;
    }

    /**
     * Gets the bound output state variable
     *
     * @return the bound output state variable
     */
    public StateVariable getBoundOutputStateVariable () {
        return boundOutputStateVariable;
    }

    /**
     * Sets the bound output state variable
     *
     * @param boundOutputStateVariable  the bound output state variable
     */
    public void setBoundOutputStateVariable (StateVariable boundOutputStateVariable) {
        this.boundOutputStateVariable = boundOutputStateVariable;
    }

}