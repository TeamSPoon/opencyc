package org.opencyc.uml.action;

import org.opencyc.uml.core.*;

/**
 * InputPin from the UML Action package.   This class is not completely implemented
 * because procedure bodies are encoded in the surface language java which is
 * interpreted by the dynamic java package, therefore no need to represent and
 * interpret the details of UML Action Semantics.
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

public class InputPin extends Pin {

    /**
     * the procedure that owns this pin as an input
     */
    protected Procedure procedure;

    /**
     * Constructs a new InputPin object.
     */
    public InputPin() {
    }

    /**
     * Gets the procedure that owns this pin as an input.
     *
     * @return the procedure that owns this pin as an input
     */
    public Procedure getProcedure () {
        return procedure;
    }

    /**
     * Sets the procedure that owns this pin as an input.
     *
     * @param procedure the procedure that owns this pin as an input
     */
    public void setProcedure (Procedure procedure) {
        this.procedure = procedure;
    }

}