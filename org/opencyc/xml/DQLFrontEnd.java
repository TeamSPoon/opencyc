/* 
 * DQLFrontEnd.java: DQL Front End, talks to the user and lets them interact
 * with a DQL Server.
 */

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.net.*;


public class DQLFrontEnd extends HttpServlet 
{
    private static String wineskb = 
	"http://ontolingua.stanford.edu/doc/chimaera/ontologies/wines.daml";
    private static String damlex = "http://www.daml.org/2001/03/daml+oil-ex#";

    private static String onto2 = 
	"http://onto2.stanford.edu:8080/dql/servlet/DQLServer";
    private static String panic = 
	"http://panic.stanford.edu:8080/dql/servlet/DQLServer";

    private static String default_dqlurl = onto2;

    public String replaceBindings(String kburl, String q, Vector b)
    {
	if(q == null || q.length() == 0) {
	    return null;
	}
	Iterator bit = b.iterator();
	while(bit.hasNext()) {
	    String s = (String) bit.next();
	    String vn = DQLResponse.getVariableName(s);
	    String vv = DQLResponse.getVariableValue(s);
	    while(true) {
		int i1 = q.indexOf("?" + vn);
		int i2 = q.indexOf("~" + vn);
		int i3 = q.indexOf("!" + vn);
		if(i1 == -1 && i2 == -1 && i3 == -1) {
		    break;
		}
		if(i1 != -1) {
		    q = q.substring(0, i1) + DQLClient.abbreviate(vv, kburl) + 
			q.substring(i1 + 1 + vn.length());
		}
		if(i2 != -1) {
		    q = q.substring(0, i2) + DQLClient.abbreviate(vv, kburl) + 
			q.substring(i2 + 1 + vn.length());
		}
		if(i3 != -1) {
		    q = q.substring(0, i3) + DQLClient.abbreviate(vv) + 
			q.substring(i3 + 1 + vn.length());
		}
	    }
	}
	return q;
    }

    public void printQueryPage(PrintWriter pw)
    {
	pw.println("<h1 align=center>DQL Query Service</h1>");
	pw.println("<form action=DQLFrontEnd method=POST>");
	pw.println("<table><tr><td colspan=2>");

	pw.println("<p>Enter premise and query as " + 
		   "<i>predicate subject object</i>, with ?varname for " +
		   "must-bind variables and with namespaces prepended. " + 
		   "One triple per blank. To specify a may-bind variable, " +
		   "use ~varname, and for a don't-bind variable, use " + 
		   "!varname.</p>");
	pw.println("<p>Some namespace abbreviations are defined for you. " +
		   "They are: <i>rdf, rdfs, daml, dql, var, tkb</i>. tkb is " +
		   "the target KB namespace. For examples, " +
		   "<a href=#examples>see below</a>.</p>");

	pw.println("<p></p>");
	pw.println("</tr></td>");

	pw.println("<tr><td align=right><b>DQL Server:</b></td>");
	pw.println("<td><input type=text name=dqlurl size=60 value=" + 
		   default_dqlurl + "></td></tr>");
	pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

	pw.println("<tr><td align=right><b>Premise:</b></td>");
	pw.println("<td><input size=40 name=p1 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Premise (cont.):</b></td>");
	pw.println("<td><input size=40 name=p2 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Premise (cont.):</b></td>");
	pw.println("<td><input size=40 name=p3 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Premise (cont.):</b></td>");
	pw.println("<td><input size=40 name=p4 type=text></td></tr>");

	pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

	pw.println("<tr><td align=right><b>Query:</b></td>");
	pw.println("<td><input size=40 name=q1 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Query (cont.):</b></td>");
	pw.println("<td><input size=40 name=q2 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Query (cont.):</b></td>");
	pw.println("<td><input size=40 name=q3 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Query (cont.):</b></td>");
	pw.println("<td><input size=40 name=q4 type=text></td></tr>");

	pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

	pw.println("<tr><td align=right><b>Answer Pattern:</b></td>");
	pw.println("<td><input size=40 name=a1 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Answer Pattern (cont.):</b></td>");
	pw.println("<td><input size=40 name=a2 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Answer Pattern (cont.):</b></td>");
	pw.println("<td><input size=40 name=a3 type=text></td></tr>");
	pw.println("<tr><td align=right><b>Answer Pattern (cont.):</b></td>");
	pw.println("<td><input size=40 name=a4 type=text></td></tr>");

	pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

	pw.println("<tr><td align=right nowrap><b>EITHER, Select KB:</b>" +
		   "</td>");
	pw.println("<td><select name=kbpick>");
	pw.println("<option value=>Select one...</option>");
	pw.println("<option value=" + wineskb + ">KSL Wines " +
		   "Ontology</option>");
	pw.println("<option value=" + damlex + ">DAML Example " +
		   "Ontology</option>");
	pw.println("</select></td></tr>");
	pw.println("<tr><td align=right nowrap><b>OR, Enter KB URL:</b></td>");
	pw.println("<td><input type=text name=kbenter size=60></td></tr>");

	pw.println("<tr><td align=right nowrap><b>OR, Search All:</b></td>");
	pw.println("<td><input type=checkbox name=kbvar value=1></td></tr>");

	pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

	pw.println("<tr><td align=right><b>Maximum answers:</b></td>");
	pw.println("<td><input type=text name=maxans size=10></td></tr>");

	pw.println("<tr><td align=right></td><td><input type=submit></td>" + 
		   "</tr>");
	pw.println("</table>");

	pw.println("<h2><a name=examples>Examples</a></h2>");

	pw.println("<p>For the wines KB:</p>");
	pw.println("<pre>");
	pw.println("Premise:");
	pw.println("   rdf:type tkb:NEW-COURSE tkb:SEAFOOD-COURSE");
	pw.println("   tkb:FOOD tkb:NEW-COURSE tkb:CRAB");
	pw.println("   tkb:DRINK tkb:NEW-COURSE tkb:W1");
	pw.println("Query:");
	pw.println("   tkb:COLOR tkb:W1 ?x");
	pw.println("</pre>");
	pw.println("<p>The DQL server will reason that W1 is a " +
		   "white wine.</p>");


	pw.println("<p>For the DAML Example KB:</p>");
	pw.println("<pre>");
	pw.println("Premise:");
	pw.println("   rdf:type tkb:Fred tkb:Person");
	pw.println("Query:");
	pw.println("   rdf:type tkb:Fred ?x");
	pw.println("</pre>");
	pw.println("<p>The DQL server will reason that Fred is an " +
		   "Animal, Person, and a few other things.</p>");

    }


    private String browseLink(String str, 
			      String p1, String p2, String p3, String p4,
			      String dqlurl, String kburl)
    {
	if(!str.startsWith("rdf:") && !str.startsWith("rdfs:") &&
	   !str.startsWith("daml:") && !str.startsWith("tkb") &&
	   !str.startsWith("http://"))
	{
	    return str;
	}
	String href = null;
	try {
	    href = "DQLFrontEnd?dqlurl=" + 
		URLEncoder.encode(dqlurl, "UTF-8") + "&p1=";
	    if(p1 != null && p1.length() != 0) {
		href = href + URLEncoder.encode(p1, "UTF-8");
	    }
	    href = href + "&p2=";
	    if(p2 != null && p2.length() != 0) {
		href = href + URLEncoder.encode(p2, "UTF-8");
	    }
	    href = href + "&p3=";
	    if(p3 != null && p3.length() != 0) {
		href = href + URLEncoder.encode(p3, "UTF-8");
	    }
	    href = href + "&p4=";
	    if(p4 != null && p4.length() != 0) {
		href = href + URLEncoder.encode(p4, "UTF-8");
	    }
	    href = href + "&q1=";
	    href = href + URLEncoder.encode("?x " + str + " ?y", "UTF-8");
	    href = href + "&maxans=";
	    if(kburl != null && !kburl.equals("")) {
		href = href + "&kbenter=" + URLEncoder.encode(kburl, "UTF-8");
	    }
	    return "<a href=\"" + href + "\">" + str + "</a>";
	}
	catch(Exception e) {
	    return "OOPS!";
	}

    }
	
    private String linkObjects(String triple, 
			       String p1, String p2, String p3, String p4,
			       String dqlurl, String kburl)
    {
	if(triple == null || triple.length() == 0) {
	    return null;
	}
	dbl d = nextToken(triple);
	triple = d.q;
	String arc = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String source = d.ret;

	d = nextToken(triple);
	String target = d.ret + d.q;

	arc = browseLink(arc, p1, p2, p3, p4, dqlurl, kburl);
	source = browseLink(source, p1, p2, p3, p4, dqlurl, kburl);
	target = browseLink(target, p1, p2, p3, p4, dqlurl, kburl);
	return arc + " " + source + " " + target;
    }


    public void addPremiseTriple(String kburl, String triple, DQLRequest req)
    {
	if(triple == null || triple.equals("")) {
	    return;
	}
	dbl d = nextToken(triple);
	triple = d.q;
	String arc = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String source = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String target = d.ret;

	if(arc == null || source == null || target == null) {
	    return;
	}
	req.addPremiseTriple(kburl, arc, source, target);
    }

    public String findVariable(String s, DQLRequest req)
    {
	if(s.startsWith("?")) {
	    String varname = s.substring(1);
	    req.setVariableState(varname, DQLClient.DQL_MUSTBIND);
	    return DQLClient.varns + varname;
	}
	else if(s.startsWith("~")) {
	    String varname = s.substring(1);
	    req.setVariableState(varname, DQLClient.DQL_MAYBIND);
	    return DQLClient.varns + s.substring(1);
	}
	else if(s.startsWith("!")) {
	    String varname = s.substring(1);
	    req.setVariableState(varname, DQLClient.DQL_DONTBIND);
	    return DQLClient.varns + s.substring(1);
	}
	return s;
    }

    public void addQueryTriple(String kburl, String triple, DQLRequest req)
    {
	if(triple == null || triple.equals("")) {
	    return;
	}
	dbl d = nextToken(triple);
	triple = d.q;
	String arc = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String source = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String target = d.ret;

	if(arc == null || source == null || target == null) {
	    return;
	}
	arc = findVariable(arc, req);
	source = findVariable(source, req);
	target = findVariable(target, req);
	req.addQueryTriple(kburl, arc, source, target);
    }

    public void addAnswerPatternTriple(String kburl, String triple, 
				       DQLRequest req)
    {
	if(triple == null || triple.equals("")) {
	    return;
	}
	dbl d = nextToken(triple);
	triple = d.q;
	String arc = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String source = d.ret;

	d = nextToken(triple);
	triple = d.q;
	String target = d.ret;

	if(arc == null || source == null || target == null) {
	    return;
	}
	arc = findVariable(arc, req);
	source = findVariable(source, req);
	target = findVariable(target, req);
	req.addAnswerPatternTriple(kburl, arc, source, target);
    }


    class dbl {
	String q;
	String ret;
	dbl(String _q, String _ret) { q = _q; ret = _ret; }
    }
    public dbl nextToken(String q)
    {
	int begin = 0;
	while(begin < q.length() && Character.isWhitespace(q.charAt(begin))) {
	    ++begin;
	}
	int end = begin;
	while(end < q.length() && !Character.isWhitespace(q.charAt(end))) {
	    ++end;
	}
	String ret = q.substring(begin, end);
	return new dbl(q.substring(end), ret);
    }

    private void htmlify(String s, PrintWriter pw)
    {
	StringBuffer ret = new StringBuffer();
	for(int x = 0; x < s.length(); ++x) {
	    char c = s.charAt(x);
	    if(c == '<') {
		ret.append("&lt;");
	    }
	    else if(c == '>') {
		ret.append("&gt;");
	    }
	    else if(c == '&') {
		ret.append("&amp;");
	    }
	    else {
		ret.append(c);
	    }
	}
	pw.println(ret.toString());
    }

    public String maybeAdd(String href, String varname, String toAdd)
    {
	try {
	    if(toAdd != null && !toAdd.equals("")) {
		href = href + "&" + varname + "=" + 
		    URLEncoder.encode(toAdd, "UTF-8");
	    }
	}
	catch(UnsupportedEncodingException ue) {
	}
	return href;
    }


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");
	PrintWriter pw = response.getWriter();
	int cl = request.getContentLength();
	if(cl == -1) {
	    String qs = request.getQueryString();
	    if(qs == null) {
		printQueryPage(pw);
		return;
	    }
	}
	String dqlurl = request.getParameter("dqlurl");

	String p1 = request.getParameter("p1");
	String p2 = request.getParameter("p2");
	String p3 = request.getParameter("p3");
	String p4 = request.getParameter("p4");

	String q1 = request.getParameter("q1");
	String q2 = request.getParameter("q2");
	String q3 = request.getParameter("q3");
	String q4 = request.getParameter("q4");

	String a1 = request.getParameter("a1");
	String a2 = request.getParameter("a2");
	String a3 = request.getParameter("a3");
	String a4 = request.getParameter("a4");

	String kbpick = request.getParameter("kbpick");
	String kbenter = request.getParameter("kbenter");
	String kbvar = request.getParameter("kbvar");

	String maxans = request.getParameter("maxans");

	String serverContinuation = request.getParameter("serverContinuation");
	String serverTermination = request.getParameter("serverTermination");

	String resp = null;

	try {
	    DQLRequest req = new DQLRequest();
	    DQLClient dqlcl = new DQLClient(dqlurl);
	    String kburl = null;
	    if(kbpick != null && !kbpick.equals("")) {
		kburl = kbpick;
	    }
	    else if(kbenter != null && !kbenter.equals("")) {
		kburl = kbenter;
	    }
	    else if(kbvar != null && kbvar.equals("1")) {
		kburl = DQLClient.varns + "kb";
	    }
	    
	    if((serverContinuation == null || serverContinuation.equals("")) &&
	       (serverTermination == null || serverTermination.equals("")))
	    {
		addPremiseTriple(kburl, p1, req);
		addPremiseTriple(kburl, p2, req);
		addPremiseTriple(kburl, p3, req);
		addPremiseTriple(kburl, p4, req);
	    
		addQueryTriple(kburl, q1, req);
		addQueryTriple(kburl, q2, req);
		addQueryTriple(kburl, q3, req);
		addQueryTriple(kburl, q4, req);

		addAnswerPatternTriple(kburl, a1, req);
		addAnswerPatternTriple(kburl, a2, req);
		addAnswerPatternTriple(kburl, a3, req);
		addAnswerPatternTriple(kburl, a4, req);

		if(kburl != null) {
		    req.addAnswerKB(kburl);
		}
	    }
	    if(maxans != null && !maxans.equals("")) {
		req.setMaxAnswers(Integer.parseInt(maxans));
	    }
	    if(serverContinuation != null && !serverContinuation.equals("")) {
		req.setServerContinuation(serverContinuation);
	    }
	    if(serverTermination != null && !serverTermination.equals("")) {
		req.setServerTermination(serverTermination);
	    }

	    resp = dqlcl.getResponseString(req);
	    DQLResponse dqlresp = new DQLResponse(resp);
	    
	    if(dqlresp.wasSuccessful()) {
		pw.println("<h1>Query Successful</h1>");
	    }
	    else {
		pw.println("<h1>Query Failed</h1>");
	    }
	    pw.println("<h3>Premise</h3>");
	    pw.println("<ul>");
	    if(p1 != null && p1.length() != 0) {
		pw.println("<li>" + p1 + "</li>");
	    }
	    if(p2 != null && p2.length() != 0) {
		pw.println("<li>" + p2 + "</li>");
	    }
	    if(p3 != null && p3.length() != 0) {
		pw.println("<li>" + p3 + "</li>");
	    }
	    if(p4 != null && p4.length() != 0) {
		pw.println("<li>" + p4 + "</li>");
	    }
	    pw.println("</ul>");
	    
	    pw.println("<h3>Bindings</h3>");
	    Vector answers = dqlresp.getBindings();
	    
	    Iterator ait = answers.iterator();
	    while(ait.hasNext()) {
		Vector b = (Vector) ait.next();
		pw.println("<ul>");
		String nq1 = replaceBindings(kburl, q1, b);
		String nq2 = replaceBindings(kburl, q2, b);
		String nq3 = replaceBindings(kburl, q3, b);
		String nq4 = replaceBindings(kburl, q4, b);
		
		nq1 = linkObjects(nq1, p1, p2, p3, p4, dqlurl, kburl);
		nq2 = linkObjects(nq2, p1, p2, p3, p4, dqlurl, kburl);
		nq3 = linkObjects(nq3, p1, p2, p3, p4, dqlurl, kburl);
		nq4 = linkObjects(nq4, p1, p2, p3, p4, dqlurl, kburl);
		
		if(nq1 != null) {
		    pw.println("  <li>" + nq1 + "</li>");
		}
		if(nq2 != null) {
		    pw.println("  <li>" + nq2 + "</li>");
		}
		if(nq3 != null) {
		    pw.println("  <li>" + nq3 + "</li>");
		}
		if(nq4 != null) {
		    pw.println("  <li>" + nq4 + "</li>");
		}
		pw.println("</ul>");
	    }
	    int tt = dqlresp.getTerminationType();
	    pw.println("<h3>Continuation</h3>");
	    if(tt == DQLClient.DQL_NONE) {
		pw.println("There are no more answers to your query.");
	    }
	    else if(tt == DQLClient.DQL_END) {
		pw.println("There are more answers to your query, but the " +
			   "server won't figure it out for you.");
	    }
	    else if(tt == DQLClient.DQL_CONTINUATION) {
		String ph = dqlresp.getProcessHandle();
		String edql = URLEncoder.encode(dqlurl, "UTF-8");
		String eph = URLEncoder.encode(ph, "UTF-8");
		String href = "DQLFrontEnd?dqlurl=" + edql + 
		    "&maxans=" + maxans;
		href = maybeAdd(href, "p1", p1);
		href = maybeAdd(href, "p2", p2);
		href = maybeAdd(href, "p3", p3);
		href = maybeAdd(href, "p4", p4);

		href = maybeAdd(href, "q1", q1);
		href = maybeAdd(href, "q2", q2);
		href = maybeAdd(href, "q3", q3);
		href = maybeAdd(href, "q4", q4);

		href = maybeAdd(href, "a1", a1);
		href = maybeAdd(href, "a2", a2);
		href = maybeAdd(href, "a3", a3);
		href = maybeAdd(href, "a4", a4);

		href = maybeAdd(href, "kbenter", kbenter);
		href = maybeAdd(href, "kbpick", kbpick);
		href = maybeAdd(href, "kbvar", kbvar);

		pw.println("Continue: <a href=\"" + href + 
			   "&serverContinuation=" + eph + "\">Next " +
			   "answers</a><br>");
		pw.println("Terminate: <a href=\"" + href + 
			   "&serverTermination=" + eph + "\">Forget " +
			   "answers</a><br>");
	    }

	    pw.println("<h3>Message Exchange</h3>");
	    pw.println("<p>Request message:</p>");
	    pw.println("<pre>");
	    htmlify(req.toXML(), pw);
	    pw.println("</pre>");
	    pw.println("<p>Response message:</p>");
	    pw.println("<pre>");
	    htmlify(dqlresp.toXML(), pw);
	    pw.println("</pre>");
	}
	catch(Exception e) {
	    pw.println("<p>I couldn't parse the response!</p>");
	    e.printStackTrace(pw);
	    if(resp != null) {
		pw.println("The response was:");
		pw.println(resp);
	    }
	}
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
