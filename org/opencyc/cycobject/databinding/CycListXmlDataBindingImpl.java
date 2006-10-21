package org.opencyc.cycobject.databinding;

import java.util.*;

/**
 * Provides an XML databinding compatible container for CycList objects.
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

public class CycListXmlDataBindingImpl implements CycListXmlDataBinding {

    private ArrayList elementList;
    private boolean isProperListIndicator;
    private Object dottedElement;

    /**
     * Constructs a new CycListXmlDataBindingImpl object.
     */
    public CycListXmlDataBindingImpl() {
    }

    public ArrayList getElementList() {
        return elementList;
    }
    public void setElementList(ArrayList elementList) {
        this.elementList = elementList;
    }

    public boolean getIsProperListIndicator() {
        return isProperListIndicator;
    }
    public void setIsProperListIndicator (boolean isProperListIndicator) {
        this.isProperListIndicator = isProperListIndicator;
    }

    public Object getDottedElement () {
        return dottedElement;
    }
    public void setDottedElement (Object dottedElement) {
        this.dottedElement = dottedElement;
    }
}