package org.opencyc.cycobject;

import org.opencyc.api.*;

/**
 * Provides the behavior and attributes of an OpenCyc GUID (Globally Unique
 * IDentifier). Each OpenCyc constant has an associated guid.
 *
 * @version $0.1$
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
public class Guid {

    /**
     * The GUID in string form.
     */
    public String guidString;

    /**
     * Constructs a new <tt>Guid</tt> object.
     */
    public Guid (String guidString) {
        this.guidString = guidString;
    }

    /**
     * Returns <tt>true</tt> if the object equals this object.
     *
     * @return <tt>boolean</tt> indicating equality of an object with this object.
     */
    public boolean equals(Object object) {
        if (object instanceof Guid &&
            this.guidString.equals(((Guid) object).guidString)) {
            return true;
        }
        else
            return false;
    }

    /**
     * Returns a string representation of the <tt>Guid</tt>.
     *
     * @return the <tt>Guid</tt> formated as a <tt>String</tt>.
     */
    public String toString() {
        return guidString;
    }

    /**
     * Returns the GuidXmlDataBindingImpl object which contains this Guid.  The
     * xml databinding object can be subsequently serialized into xml.
     *
     * @return the GuidXmlDataBindingImpl object which contains this Guid
     */
    public GuidXmlDataBindingImpl toGuidXmlDataBindingImpl () {
        GuidXmlDataBindingImpl guidXmlDataBindingImpl = new GuidXmlDataBindingImpl();
        guidXmlDataBindingImpl.setGuidString(guidString);
        return guidXmlDataBindingImpl;
    }

}