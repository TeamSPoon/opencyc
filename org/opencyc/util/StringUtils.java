package org.opencyc.util;

import java.util.*;
import ViolinStrings.*;

/**
 * Provides <tt>String</tt> utilities not otherwise provided by Violin Strings.  All methods
 * are static.  There is no need to instantiate this class.<p>
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
public class StringUtils {

    /**
     * Returns true iff all characters in the given string are numeric.  A prefix character of
     * "+" or "-" is accepted as numeric.  Decimal points are not accepted as numeric by this
     * method.
     *
     * @param string the string to be tested
     * @return <tt>true</tt> iff all characters are numeric
     */
    public static boolean isNumeric (String string) {
        String testString = string;
        if ((string.charAt(0) == '+') ||
            (string.charAt(0) == '-')) {
            if (string.length() > 1)
                testString = string.substring(1);
            else
                return false;
        }
        for (int i = 0; i < testString.length(); i++) {
            if (! Character.isDigit(testString.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Returns true iff the given object is a string delimited by double quotes.
     *
     * @param object the object to be tested
     * @return <tt>true</tt> iff the given object is a string delimited by double quotes
     */
    public static boolean isDelimitedString (Object object) {
        if (! (object instanceof String))
            return false;
        String string = (String) object;
        if (string.length() < 2)
            return false;
        if (! string.startsWith("\""))
            return false;
        return string.endsWith("\"");
    }

    /**
     * Removes delimiter characters from the given string.
     *
     * @param string the string which has delimiters to be removed
     * @return a input string without its delimiters
     */
    public static String removeDelimiters(String string) {
        int length = string.length();
        if (length < 3)
            throw new RuntimeException("Cannot remove delimters from " + string);
        return string.substring(1, length - 1);
    }

    /**
     * Returns the phrase formed from the given list of words
     *
     * @param words the phrase words
     * @return the phrase formed from the given list of words
     */
    public static String wordsToPhrase (List words) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < words.size(); i++) {
            if (i > 0)
                stringBuffer.append(" ");
            stringBuffer.append(words.get(i));
        }
        return stringBuffer.toString();
    }

}