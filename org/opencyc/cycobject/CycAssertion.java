package org.opencyc.cycobject;

/**
 * Provides the behavior and attributes of an <tt>CycAssertion</tt>.<p>
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 OpenCyc.org, license is open source GNU LGPL.
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
public class CycAssertion {

    /**
     * The assertion in the form of a <tt>CycList</tt>.
     */
    private CycList cycList;

    /**
     * Constructs a new <tt>CycAssertion</tt> object from a <tt>CycList</tt>.
     */
    public CycAssertion(CycList cycList) {
        this.cycList = cycList;
    }

    /**
     * Returns the assertion in the form of a <tt>CycList</tt>.
     *
     * @return the assertion in the form of a <tt>CycList</tt>
     */
    public CycList asCycList() {
        return cycList;
    }

    /**
     * Indicates whether the object is equal to this object.
     *
     * @return <tt>true</tt> if the object is equal to this object, otherwise
     * returns <tt>false</tt>
     */
    public boolean equals(Object object) {
        if (! (object instanceof CycAssertion))
            return false;
        CycAssertion cycAssertion = (CycAssertion) object;
        return cycList.equals(cycAssertion.asCycList());
    }

    /**
     * Returns a <tt>String</tt> representation of the <tt>CycAssertion</tt>.
     *
     * @return a <tt>String</tt> representation of the <tt>CycAssertion</tt>
     */
    public String toString() {
        return cycList.toString();
    }

    /**
     * Returns an <tt>String</tt> representation of the <tt>CycAssertion</tt>,
     * in which CycConstants are prefixed with "#$".
     *
     * @return an <tt>String</tt> representation of the <tt>CycAssertion</tt>
     * in which CycConstants are prefixed with "#$".
     */
    public String cyclify() {
        return cycList.cyclify();
    }
}