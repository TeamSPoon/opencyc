package org.opencyc.uml.action;

import org.opencyc.uml.core.ModelElement;

/**
 * InputBinding which is an extenstion to the UML action package.
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

public class InputBinding extends ModelElement {

    /**
     * the bound input pin
     */
    protected InputPin boundInputPin;

    /**
     * the bound input value expression which could be a literal,
     * state variable, or expression
     */
    protected Object boundInputValueExpression;

    /**
     * Creates a new InputBinding object.
     */
    public InputBinding() {
    }

    /**
     * Gets the bound input pin
     *
     * @return the bound input pin
     */
    public InputPin getBoundInputPin () {
        return boundInputPin;
    }

    /**
     * Sets the bound input pin
     *
     * @param boundInputPin the bound input pin
     */
    public void setBoundInputPin (InputPin boundInputPin) {
        this.boundInputPin = boundInputPin;
    }

    /**
     * Gets the bound input value expression which could be a literal,
     * state variable, or expression
     *
     * @return the bound input value expression
     */
    public Object getBoundInputValueExpression () {
        return boundInputValueExpression;
    }

    /**
     * Sets the bound input value expression which could be a literal,
     * state variable, or expression
     *
     * @param boundInputValueExpression the bound input value expression
     */
    public void setBoundInputValueExpression (Object boundInputValueExpression) {
        this.boundInputValueExpression = boundInputValueExpression;
    }

}