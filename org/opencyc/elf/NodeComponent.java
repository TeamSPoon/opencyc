package org.opencyc.elf;

import java.util.*;
import org.opencyc.util.*;
import org.opencyc.uml.core.*;

/**
 * Provides common attributes and behavior for Elementary Loop
 * Functioning (ELF) node components.<br>
 *
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

public abstract class NodeComponent extends ELFObject {

    /**
     * The default verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Reference to the ELF Node which contains this object.
     */
    protected Node node;

    /**
     * indicates a pending interruption
     */
    protected boolean pendingInterruption = false;

    /**
     * the interruption procedure to execute
     */
    protected Procedure interruptionRequest;

    /**
     * Gets the ELF Node which contains this object.
     *
     * @return the ELF Node which contains this object
     */
    public Node getNode () {
        return node;
    }

    /**
     * Requests an interruption of the current processing of this
     * node component to execute the given procedure and to return
     * the list of the output pin values.
     *
     * @param interruptionRequest the given interruption procedure to execute
     * @return the list of the output pin values
     */
    public ArrayList interrupt(Procedure interruptionRequest) {
        ArrayList values = new ArrayList();
        this.interruptionRequest = interruptionRequest;
        pendingInterruption = true;
        try {
            while (true) {
                Thread.sleep(100);
                if (! pendingInterruption)
                    break;
            }
        }
        catch (InterruptedException e) {
        }
        return interruptionRequest.getResult();
    }

    /**
     * Sets the ELF Node which contains this object.
     *
     * @param node the ELF Node which contains this object
     */
    public void setNode (Node node) {
        this.node = node;
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

}