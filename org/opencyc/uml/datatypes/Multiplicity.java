package org.opencyc.uml.datatypes;

/**
 * Multiplicity from the UML Data Types Package
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

public class Multiplicity  {

    /**
     * the non-negative lower limit of multiplicity
     */
    protected int lower;

    /**
     * the upper limit of multiplicity
     */
    protected long upper;

    /**
     * when true, indicates that the multiplicity has an unlimited
     * upper range
     */
    boolean isUnlimited;

    /**
     * Constructs a new Multiplicity object
     */
    public Multiplicity() {
    }

    /**
     * Gets the non-negative lower limit of multiplicity
     *
     * @return the non-negative lower limit of multiplicity
     */
    public int getLower () {
        return lower;
    }

    /**
     * Sets the non-negative lower limit of multiplicity
     *
     * @param lower the non-negative lower limit of multiplicity
     */
    public void setLower (int lower) {
        this.lower = lower;
    }

    /**
     * Gets the upper limit of multiplicity
     *
     * @return the upper limit of multiplicity
     */
    public long getUpper () {
        return upper;
    }

    /**
     * Sets the upper limit of multiplicity
     *
     * @param upperthe upper limit of multiplicity
     */
    public void setUpper (long upper) {
        this.upper = upper;
    }

    /**
     * Gets the unlimited upper bound indicator
     *
     * @return the unlimited upper bound indicator
     */
    public boolean isUnlimited () {
        return isUnlimited;
    }

    /**
     * Sets the unlimited upper bound indicator
     *
     * @param isUnlimited the unlimited upper bound indicator
     */
    public void setIsUnlimited (boolean isUnlimited) {
        this.isUnlimited = isUnlimited;
    }


}