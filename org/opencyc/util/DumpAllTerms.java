package org.opencyc.util;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;


/**
 * Application which dumps all terms in the KB to a file.
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

public class DumpAllTerms {


    /**
     * the filename to which the KB terms are written
     */
    public String dumpFileName = "opencyc-0.7.0-terms.txt";

    /**
     * the CycAccess KB interface instance
     */
    protected CycAccess cycAccess;

    /**
     * Constructs a new DumpAllTerms object.
     */
    public DumpAllTerms() {
    }

    /**
     * Executes the DumpAllTerms application.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Log.makeLog("DumpAllTerms.log");
        DumpAllTerms dumpAllTerms = new DumpAllTerms();
        try {
            dumpAllTerms.dump();
        }
        catch (Exception e) {
            Log.current.println(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    protected void dump()
        throws CycApiException, IOException, UnknownHostException {
        Log.current.println("Dumping KB terms to " + dumpFileName);
        cycAccess = new CycAccess();
        PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(dumpFileName)));
        String script =
            "(clet (terms)\n" +
            "  (do-constants (term)\n" +
            "    (cpush term terms))\n" +
            "  terms)\n";
        CycList terms = cycAccess.converseList(script);
        Iterator iter = terms.iterator();
        while (iter.hasNext()) {
            CycConstant cycConstant = (CycConstant) iter.next();
            printWriter.println(cycConstant.cyclify() + " ");

        }
        printWriter.close();
        Log.current.println(terms.size() + " KB terms dumped.");
    }
}