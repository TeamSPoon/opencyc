package org.opencyc.uml.statemachine;

/**
 * PseudoState from the UML State_Machines package.
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

public class PseudoState extends StateVertex {

    /**
     * the kind of PseudoState
     */
    protected int kind;

    public static final int PK_CHOICE = 1;
    public static final int PK_DEEPHISTORY = 2;
    public static final int PK_FORK = 3;
    public static final int PK_INITIAL = 4;
    public static final int PK_JOIN = 5;
    public static final int PK_JUNCTION = 6;
    public static final int PK_SHALLOWHISTORY = 7;


    /**
     * Constructs a new StateVertex object.
     */
    public PseudoState() {
    }

    /**
     * Gets the kind of PseudoState
     *
     * @return the kind of PseudoState
     */
    public int getKind () {
        return kind;
    }

    /**
     * Sets the kind of PseudoState
     *
     * @param kind the kind of PseudoState
     */
    public void setKind (int kind) {
        this.kind = kind;
    }
}