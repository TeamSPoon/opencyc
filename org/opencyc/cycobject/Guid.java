package org.opencyc.cycobject;

import org.apache.oro.util.*;

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
     * Least Recently Used Cache of guids, so that a reference to an existing <tt>Guid</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cache = new CacheLRU(500);

    /**
     * The GUID in string form.
     */
    protected String guidString;

    /**
     * Returns a cached <tt>Guid</tt> object or construct a new
     * Guid object from a guid string if the guid is not found in the cache.
     *
     * @param guid a <tt>String</tt> form of a GUID.
     */
    public static Guid makeGuid(String guidString) {
        Guid guid = (Guid) cache.getElement(guidString);
        if (guid == null ) {
            guid = new Guid(guidString);
            cache.addElement(guidString, guid);
        }
        return guid;
    }

    /**
     * Constructs a new <tt>Guid</tt> object. Non-public to enforce the
     * use of the cache during object creation.
     */
    private Guid(String guidString) {
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
     * Resets the <tt>Guid</tt> cache.
     */
    public static void resetCache() {
        cache = new CacheLRU(500);
    }

    /**
     * Retrieves the <tt>Guid</tt> with <tt>guidName</tt>,
     * returning null if not found in the cache.
     *
     * @return the <tt>Guid</tt> if it is found in the cache, otherwise
     * <tt>null</tt>
     */
    public static Guid getCache(String guidName) {
        return (Guid) cache.getElement(guidName);
    }

    /**
     * Removes the <tt>Guid</tt> from the cache if it is contained within.
     */
    public static void removeCache(Guid guid) {
        Object element = cache.getElement(guid.guidString);
        if (element != null)
            cache.addElement(guid.guidString, null);
    }

    /**
     * Returns the size of the <tt>Guid</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>Guid</tt> objects in the cache
     */
    public static int getCacheSize() {
        return cache.size();
    }

}