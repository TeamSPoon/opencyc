package org.opencyc.elf;

import java.util.*;
import java.io.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.parser.wrapper.*;
import org.opencyc.util.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.s.*;

/**
 * Provides the lowest level Node container for the Elementary Loop
 * Functioning (ELF).<br>
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

public class LowestLevelNode extends Node {

    /**
     * Access the World via the tree interpreter which interprets
     * java statements.  This World access is shared between the
     * actuator and the sensor.
     */
    protected TreeInterpreter treeInterpreter;

    /**
     * the Actuator for this node
     */
    protected Actuator actuator;

    /**
     * the Sensor for this node
     */
    protected Sensor sensor;

    /**
     * Constructs a new LowestLevelNode object.
     */
    public LowestLevelNode() {
    }
    /**
     * Gets the Actuator for this node
     *
     * @return the Actuator for this node
     */
    public Actuator getActuator () {
        return actuator;
    }

    /**
     * Sets the Actuator for this node
     *
     * @param actuator the Actuator for this node
     */
    public void setActuator (Actuator actuator) {
        this.actuator = actuator;
    }

    /**
     * Gets the Sensor for this node
     *
     * @return the Sensor for this node
     */
    public Sensor getSensor () {
        return sensor;
    }

    /**
     * Sets the Sensor for this node
     *
     * @param sensor the Sensor for this node
     */
    public void setSensor (Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Gets the tree interpreter which interprets java statements
     *
     * @return the tree interpreter which interprets java statements
     */
    public TreeInterpreter getTreeInterpreter () {
        return treeInterpreter;
    }

    /**
     * Sets the tree interpreter which interprets java statements
     *
     * @param treeInterpreter the tree interpreter which interprets java statements
     */
    public void setTreeInterpreter (TreeInterpreter treeInterpreter) {
        this.treeInterpreter = treeInterpreter;
    }

}