package org.opencyc.kif;

// Java
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;


// OpenCyc
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
* Provides wrappers for the OpenCyc API.<p>
*
* Collaborates with the <tt>Jamud</tt> class which manages the api connections.
*
* @version $Id$
* @author Douglas R. Miles
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

public class ExportKif {

    public static CycAccess cyc=null;

    public static void main(String[] args) {
	try {
	    ExportKif cm = null;
	    if( args.length==2 ) {
		cm = new ExportKif(args[0], Integer.parseInt(args[1]),              
				    CycConnection.DEFAULT_COMMUNICATION_MODE,
				    CycAccess.DEFAULT_CONNECTION);

	    } else {
		cm = new ExportKif();
	    }
	    cm.sendAllCycOneFile();
	} catch( Exception e ) {
	    e.printStackTrace();
	}
    }


    /**
     * Constructs a new CycAssertionsFactory object.
     */
    public ExportKif() throws IOException, CycApiException {
	if( cyc==null )	cyc =  new CycAccess();
	primaryStart();
	System.out.println("loaded ExportKif");
    }

    /**
     * Constructs a new ExportKif object to the given CycProxyAgent in the given
     * agent community.
     *
     * @param myAgentName the name of the local agent
     * @param cycProxyAgentName the name of the cyc proxy agent
     * @param agentCommunity the agent community to which the cyc proxy agent belongs
     */
    public ExportKif (String myAgentName,
		       String cycProxyAgentName,
		       int agentCommunity) throws IOException, CycApiException  {
	if( cyc==null )	cyc = new CycAccess(myAgentName,cycProxyAgentName,agentCommunity);
	primaryStart();
	System.out.println("loaded ExportKif");
    }

    /**
     * Constructs a new ExportKif object given a host name, port, communication mode and persistence indicator.
     *
     * @param hostName the host name
     * @param basePort the base (HTML serving) TCP socket port number
     * @param communicationMode either ASCII_MODE or BINARY_MODE
     * @param persistentConnection when <tt>true</tt> keep a persistent socket connection with
     * the OpenCyc server
     */
    public ExportKif(String hostName, int basePort, int communicationMode, boolean persistentConnection)
    throws IOException, UnknownHostException, CycApiException {
	if( cyc==null )	cyc = new CycAccess(hostName,basePort,communicationMode,persistentConnection);
	primaryStart();
	System.out.println("loaded ExportKif");
    }


    public CycAccess getCycAccess() {
	return(CycAccess)cyc;
    }

    private synchronized void primaryStart() {
	Log.makeLog();
    }

    public void sendAllCycOneFile() throws Exception {
	Iterator allMts = cyc.converseList("(all-instances (find-constant \"Microtheory\"))").iterator();
	File dumpfile = new File("cyc.pcache");
	FileWriter fw = new FileWriter(dumpfile);
	PrintWriter dump = new PrintWriter(fw);
	while( allMts.hasNext() ) {
	    CycFort mt = (CycFort)allMts.next();
	    System.out.println("Sending " +mt + " to " + dumpfile);
	    mtSend(dump,mt);
	}
	fw.close();
    }

    public void sendCycMtEachFile() throws Exception {
	Iterator allMts = cyc.converseList("(all-instances (find-constant \"Microtheory\"))").iterator();
	while( allMts.hasNext() ) {
	    CycFort mt = (CycFort)allMts.next();
	    try {
		File dumpfile = new File(mt.toString()+".pcache");
		System.out.println("Sending " +mt + " to " + dumpfile);
		FileWriter fw = new FileWriter(dumpfile);
		PrintWriter dump = new PrintWriter(fw);
		mtSend(dump,mt);
		fw.close();
	    } catch( Exception e ) {
		e.printStackTrace();
	    }
	}
    }
    
    public void mtSend(PrintWriter dump, CycFort mt) {
	Iterator ata = null;
	try {
	    ata = cyc.converseList("(GATHER-MT-INDEX #$" + mt.toString() + ")").iterator();
	    while( ata.hasNext() ) {
		sendAssertion(mt,dump,ata.next().toString());
	    }
	} catch( Exception ee ) {
	    ee.printStackTrace();
	    dump.println("/*");
	    dump.println(""+mt);
	    dump.println(""+ee);
	    dump.println("*/");
	}
    }

    public void sendAssertion(CycFort mt,PrintWriter dump,Object term) throws Exception {
	try {
	    String id = term.toString().split(":")[1];
	    CycList af = cyc.converseList("(ASSERTION-FORMULA (find-assertion-by-id " + id + "))");
	    Object as = cyc.converseObject("(ASSERTION-STRENGTH (find-assertion-by-id " + id + "))");
	    Object ad = cyc.converseObject("(ASSERTION-DIRECTION (find-assertion-by-id " + id + "))");
	    Object at = cyc.converseObject("(ASSERTION-TRUTH (find-assertion-by-id " + id + "))");
	    try {
		if( !af.first().equals(cyc.comment) ) {
		    dump.println("'$jCycAssertion'(" + toPrologCycTerm(af)+",'"+mt.cyclify()+"','"+as+"','"+ad+"','"+at+"').");
		}
	    } catch( Exception e ) {
		e.printStackTrace();
	    }
	} catch( Exception ee ) {
	    ee.printStackTrace();
	}
    }

    public static String toPrologCycTerm(Object term) {
	if( term==null ) return "null";
	if( term instanceof CycConstant ) return toPrologCycAString(((CycConstant)term).cyclify());
	if( term instanceof CycSymbol )	return  toPrologCycAString(term.toString());
	if( term instanceof CycNart ) return "'$jCycNart'(" + toPrologCycString(((CycNart)term).cyclify()) +")";
	if( term instanceof CycList ) return "'$jCycList'(" + toPrologCycList((CycList)term) +")";
	if( term instanceof String ) return "'$jString'(" + toPrologCycString(term.toString()) +")";
	if( term instanceof Integer ) return(term.toString());
	if( term instanceof Float ) return(term.toString());
	if( term instanceof CycVariable ) {
	    CycVariable cv = (CycVariable)term;
	    return "'$jCycVariable'('?"+cv.name +"','"+cv.id+"')";
	}
	if( term instanceof CycAssertion ) {
	    CycAssertion ca = ((CycAssertion)term);
	    //  ca.getS
	    return "'$jCycAssertion'("+toPrologCycTerm(ca.getFormula()) +")";
	}
	return "'$jObject'(" +toPrologCycAString(term.toString())+")";
    }


    public static String toPrologCycList(CycList cyclist) {
	if( cyclist==null ) return "[]";
	if( cyclist.isEmpty() )	return "[]";
	StringBuffer sb = new StringBuffer("[");
	if( !cyclist.isProperList() ) {
	    for( int i =0; i < cyclist.size(); i++ ) {
		sb.append(toPrologCycTerm(cyclist.get(i))).append(", ");
	    }
	    return sb.append(toPrologCycTerm(cyclist.rest())).append("]").toString();
	}
	for( int i =0; i < cyclist.size()-1; i++ ) {
	    sb.append(toPrologCycTerm(cyclist.get(i))).append(", ");
	}

	return sb.append(toPrologCycTerm(cyclist.get(cyclist.size()-1))).append("]").toString();
    }


    public static String toPrologCycString(String term) {
	return "\""+replaceStr(term,"\"","\\\"")+"\"";
    }

    public static String toPrologCycAString(String term) {
	return "'"+replaceStr(term,"'","\\'")+"'";
    }

    private static String replaceStr(String str, String pattern, String replace) {

	int s = 0;

	int e = 0;

	StringBuffer result = new StringBuffer();



	while( (e = str.indexOf(pattern, s)) >= 0 ) {

	    result.append(str.substring(s, e));

	    result.append(replace);

	    s = e+pattern.length();

	}

	result.append(str.substring(s));

	return result.toString();

    }



}








