package org.opencyc.xml;

/*
 * DQLServer.java: DQL Server for Cyc
 */

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;

public class DQLServer extends HttpServlet {
    /* By default return them all */
    private static int DQL_DEFAULT_ANSWERS = -1;
    
    private static String dqlns = "http://www.daml.org/2002/10/dql-syntax#";
    private static String varns = "http://www.daml.org/2002/10/dql-variables#";
    private static String rdfns =
    "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static String soapns =
    "http://schemas.xmlsoap.org/soap/envelope/";
    
    protected Hashtable drcs;
    protected Vector phnds;
    
    private String _getNamespaceFromResource(String url) {
        int pound = url.lastIndexOf('#');
        if(pound != -1) {
            return url.substring(0, pound + 1);
        }
        int slash = url.lastIndexOf('/');
        if(slash != -1) {
            return url.substring(0, slash + 1);
        }
        return "";
    }
    private String _getTagFromResource(String url) {
        int pound = url.lastIndexOf('#');
        if(pound != -1) {
            return url.substring(pound + 1);
        }
        int slash = url.lastIndexOf('/');
        if(slash != -1) {
            return url.substring(slash + 1);
        }
        return url;
    }
    
    private String _getTag(Element el) {
        Namespace nss = el.getNamespace();
        String ns = nss != null ? nss.getURI() : "";
        String tag = ns + el.getName();
        return tag;
    }
    
    /**
     * Returns the corresponding CycL form of the OWL token.
     *
     * @param ret the given OWL token
     * @return the corresponding CycL form of the OWL token
     */
    public static String tokenToCycL(String ret) {
        if(ret.startsWith("var:")) {
            ret = "?" + ret.substring(4);
        }
        else if(ret.startsWith(varns)) {
            ret = "?" + ret.substring(varns.length());
        }
        else {
            int rpi = ret.lastIndexOf('#');
            if(rpi != -1) {
                String uri = ret.substring(0, rpi);
                if(uri.equals(rdfns)) {
                    ret = "|" + uri + "#|::" + ret.substring(rpi+1);
                }
                else {
                    ret = "|" + uri + "#|::|" + ret.substring(rpi+1) + "|";
                }
            }
        }
        return ret;
    }
    
    /**
     * Returns the target of the given element.
     * 
     * @param e the given element
     * @return the target of the given element
     */
    public static String _getTarget(Element e) {
        String ret = e.getAttributeValue("resource",
        Namespace.getNamespace(rdfns));
        if(ret == null) {
            List c = e.getChildren();
            if(c != null && !c.isEmpty()) {
                ret = ((Element) c.get(0)).getTextTrim();
            }
        }
        if(ret != null) {
            ret = tokenToCycL(ret);
        }
        return ret;
    }
    
    public static String unKIF(String kif) {
        StringBuffer ret = new StringBuffer();
        for(int x = 0; x < kif.length(); ++x) {
            char c = kif.charAt(x);
            if(c == '|') {
                if(kif.substring(x).startsWith("|::|")) {
                    x += 3;
                }
            }
            else if(c == ':' && (kif.charAt(x+1) == ':')) {
                ++x;
            }
            else {
                ret.append(c);
            }
        }
        return ret.toString();
    }
    
    public static String dqlToKIF(Element query, PrintWriter pw) {
        /* replace variables, translate to KIF */
        Iterator it = query.getChildren().iterator();
        String kif = "";
        
        try {
            Element rdf = (Element) query.getChildren().iterator().next();
            XMLOutputter xo = new XMLOutputter();
            String daml = xo.outputString(rdf);
            DAMLParser p = new DAMLParser();
            StringReader sr = new StringReader(daml);
            
            ClauseIterator i = p.parse(sr);
            while(i.hasNext()) {
                Clause c = (Clause) i.next();
                Iterator j = c.literals().iterator();
                while(j.hasNext()) {
                    jtp.fol.Literal lit = (jtp.fol.Literal) j.next();
                    Symbol s = lit.getRelation();
                    String pkg = s.getPackage();
                    String name = s.getName();
                    kif = kif + "(" + tokenToCycL(pkg + name) + " ";
                    List args = lit.getArgs();
                    Iterator k = args.iterator();
                    while(k.hasNext()) {
                        jtp.fol.Symbol trm = (jtp.fol.Symbol) k.next();
                        String tpkg = trm.getPackage();
                        String tname = trm.getName();
                        kif = kif + tokenToCycL(tpkg + tname) + " ";
                    }
                    kif = kif + ")";
                }
            }
        }
        catch(ParsingException e) {
            pw.println("I caught an exception parsing your query!");
            return "";
        }
        catch(IOException e) {
            pw.println("I have no idea what just happened. XMLOutputter " +
            "threw an IOException");
        }
        while(true) {
            int i = kif.indexOf("(?");
            if(i == -1) {
                break;
            }
            kif = kif.substring(0, i) + "(holds " + kif.substring(i + 1);
        }
        return kif;
    }
    
    public void addVars(Element e, Vector v) {
        Iterator it = e.getChildren().iterator();
        while(it.hasNext()) {
            Element c = (Element) it.next();
            Namespace ns = c.getNamespace();
            if(ns.getURI().equals(varns)) {
                v.add(c.getName());
            }
        }
    }
    
    
    /* These are copied from DamlQueryAnwerer because for some reason
       they're protected. */
    protected static Map getTopLevelBindings(ReasoningStep rs) {
        Map bindings = getBindings(rs);
        Map retBindings = new HashMap(bindings.size());
        Set topLevelVars = getQueryVariables(rs);
        
        for (Iterator it = topLevelVars.iterator(); it.hasNext(); ) {
            Object v = it.next();
            retBindings.put(v, bindings.get(v));
        }
        return retBindings;
    }
    
    protected static Map getBindings(ReasoningStep rs) {
        if (rs.getGoal() instanceof String)
            rs = (ReasoningStep)rs.getSubProofs().get(0);
        return RSUtils.getRecursiveBindings(rs, null);
    }
    
    protected static Set getQueryVariables(ReasoningStep rs) {
        Object goal = rs.getGoal();
        if (goal instanceof CNFSentence)
            return getQueryVariables((CNFSentence)goal);
        else {
            ReasoningStep subRS = (ReasoningStep)rs.getSubProofs().get(0);
            return getQueryVariables((CNFSentence)subRS.getGoal());
        }
    }
    
    static Set getQueryVariables(CNFSentence sent) {
        Set vars = new HashSet(10);
        for (Iterator i = sent.clauses().iterator(); i.hasNext(); ) {
            Clause cl = (Clause)i.next();
            for (Iterator j = cl.literals().iterator(); j.hasNext(); ) {
                jtp.fol.Literal lit = (jtp.fol.Literal)j.next();
                if (lit.getArgs() instanceof Unifyable)
                    vars.addAll(((Unifyable)lit.getArgs()).getVariables(null));
            }
        }
        return vars;
    }
    
    /* End copy and paste */
    
    public Element replaceVars(Element e, Hashtable vars, PrintWriter pw) {
        Namespace ns = e.getNamespace();
        String name = e.getName();
        if(ns != null && ns.getURI().equals(varns)) {
            String val = (String) vars.get(name);
            if(val != null) {
                String nns = _getNamespaceFromResource(val);
                ns = (nns != null && !nns.equals("")
                ? Namespace.getNamespace(nns)
                : null);
                name = _getTagFromResource(val);
            }
        }
        Element n = new Element(name, ns);
        
        Iterator ai = e.getAttributes().iterator();
        while(ai.hasNext()) {
            Attribute a = (Attribute) ai.next();
            Namespace ans = a.getNamespace();
            String aname = a.getName();
            String aval = a.getValue();
            String valns = _getNamespaceFromResource(aval);
            String valname = _getTagFromResource(aval);
            if(valns != null && valns.equals(varns)) {
                String nval = (String) vars.get(valname);
                if(nval != null) {
                    valns = _getNamespaceFromResource(nval);
                    valname = _getTagFromResource(nval);
                }
            }
            n.getAttributes().add(new Attribute(aname, valns + valname, ans));
        }
        
        Iterator i = e.getChildren().iterator();
        while(i.hasNext()) {
            Element c = (Element) i.next();
            n.getChildren().add(replaceVars(c, vars, pw));
        }
        return n;
    }
    
    class DQLProcess {
        public String kburl;
        public Element qap;
        public Iterator ai;
        public DQLProcess(String kb, Element qa, Iterator a) {
            kburl = kb;
            qap = qa;
            ai = a;
        }
    }
    
    
    public DamlReasoningContext findKB(String kburl)
    throws ServletException {
        DamlReasoningContext drc = (DamlReasoningContext) drcs.get(kburl);
        if(drc == null) {
            drc = new DamlReasoningContext();
            try {
                drc.setUp();
                drc.setMaxDepth(40);
                URL kb = new URL(kburl);
                drc.loadKB(kb);
                drcs.put(kburl, drc);
            }
            catch(MalformedURLException e) {
                throw new ServletException("Bad URL for default KB");
            }
            catch(ReasoningException e) {
                throw new ServletException("Reasoning exception: " + e);
            }
            catch(IOException e) {
                throw new ServletException("Failed with I/O exception " +
                "reading KB");
            }
            catch(Exception e) {
                throw new ServletException("I have no idea what just " +
                "happened.");
            }
        }
        return drc;
    }
    
    public void printAnswersFromIterator(boolean needEmpty, Iterator ai,
    int count, PrintWriter pw,
    String kbvar, String kburl,
    Element qap)
    throws IOException {
        for(int x = 0; ai.hasNext() && (count == -1 || x < count); ++x) {
            Vector aset = (Vector) ai.next();
            needEmpty = false;
            pw.println("  <dql:answer>");
            pw.println("    <dql:binding-set>");
            if(kbvar != null) {
                pw.println("      <var:" + kbvar +
                " rdf:resource=\"" + kburl + "\"/>");
            }
            
            Iterator ansit = aset.iterator();
            Hashtable vars = new Hashtable();
            while(ansit.hasNext()) {
                ans a = (Binding) ansit.next();
                pw.println("      <var:" + a.name +
                " rdf:resource=\"" + a.value + "\"/>");
                vars.put(a.name, a.value);
            }
            pw.println("    </dql:binding-set>");
            Element qtop = (Element) qap.getChildren().iterator().next();
            Element atop = replaceVars(qtop, vars, pw);
            pw.println("    <dql:answerPatternInstance>");
            XMLOutputter xo = new XMLOutputter();
            pw.println(xo.outputString(atop));
            pw.println("    </dql:answerPatternInstance>");
            pw.println("  </dql:answer>");
        }
        /* This indicates a successful query when no vars are used. */
        if(needEmpty) {
            pw.println("  <dql:answer>");
            pw.println("    <dql:binding-set>");
            pw.println("    </dql:binding-set>");
            pw.println("  </dql:answer>");
        }
    }
    public void printContinuation(Iterator ai, int count, Element qap,
    PrintWriter pw, String kburl) {
        pw.println("  <dql:continuation>");
        if(ai.hasNext()) {
            if(count != -1) {
                int ph = phnds.size();
                DQLProcess dp = new DQLProcess(kburl, qap, ai);
                phnds.add(dp);
                String pound = "";
                if(!kburl.endsWith("#") || !kburl.endsWith("/")) {
                    pound = "#";
                }
                pw.println("    <dql:processHandle>" +
                ph + "</dql:processHandle>");
            }
            else {
                /* currently this will never happen */
                pw.println("    <dql:termination-token>");
                pw.println("      <dql:end/>");
                pw.println("    </dql:termination-token>");
            }
        }
        else {
            pw.println("    <dql:termination-token>");
            pw.println("      <dql:none/>");
            pw.println("    </dql:termination-token>");
        }
        pw.println("  </dql:continuation>");
    }
    
    public void printHeader(PrintWriter pw) throws IOException {
        pw.println("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"" +
        soapns + "\"");
        pw.println("                   SOAP-ENV:encodingStyle=" +
        "\"http://schemas.xmlsoap.org/soap/" +
        "encoding/\">");
        pw.println("<SOAP-ENV:Body>");
        
        pw.println("<dql:answerBundle xmlns:dql=\"" +
        dqlns + "\"");
        pw.println("                  xmlns:var=\"" +
        varns + "\"");
        pw.println("                  xmlns:rdf=\"" +
        rdfns + "\">");
    }
    public void printFooter(PrintWriter pw) throws IOException {
        pw.println("</dql:BindingwerBundle>");
        pw.println("</SOAP-ENV:Body>");
        pw.println("</SOAP-ENV:Envelope>");
    }
    
    public void init() {
        drcs = new Hashtable();
        phnds = new Vector();
    }
    
    /**
     * Class whose instances are containers for an Bindingwer.
     */
    class Binding {
        
        /**
         * the variable name
         */
        public String name;
        
        /**
         * the value bound to the variable
         */
        public String value;
        
        /**
         * Constructs a new Binding 
         */
        public Binding(String n, String v) {
            name = n;
            value = v;
        }
    }
    
    /**
     * Performs the servlet GET method.
     * 
     * @param request the given DQL request
     * @param response the resulting DQL response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        response.setContentType("text/xml");
        BufferedReader rd = request.getReader();
        PrintWriter pw = response.getWriter();
        int cl = request.getContentLength();
        if(cl == -1) {
            pw.println("Sorry! You have to give me some content!");
            return;
        }
        char data[] = new char[cl];
        int nr = 0, rv = 0;
        while(nr < cl) {
            rv = rd.read(data, nr, cl - nr);
            if(rv < 1) {
                throw new IOException();
            }
            nr += rv;
        }
        String reqxml = new String(data);
        StringReader sr = new StringReader(reqxml);
        
        String premiseSet = null;
        String querySet = null;
        String queryXML = null;
        String kbvar = null;
        
        Element query = null;
        Element answerPattern = null;
        
        Vector musts = new Vector();
        Vector mays = new Vector();
        
        int count = DQL_DEFAULT_ANSWERS;
        
        Vector kbset = new Vector();
        
        try {
            Element env = new SAXBuilder().build(sr).getRootElement();
            Namespace soap = Namespace.getNamespace(soapns);
            Namespace dql = Namespace.getNamespace(dqlns);
            Namespace var = Namespace.getNamespace(varns);
            Element body = env.getChild("Body", soap);
            Element dqlreq = body.getChild("query", dql);
            
            Element premise = dqlreq.getChild("premise", dql);
            query = dqlreq.getChild("queryPattern", dql);
            answerPattern = dqlreq.getChild("answerPattern", dql);
            Element mustvars = dqlreq.getChild("mustBindVars", dql);
            Element mayvars = dqlreq.getChild("mayBindVars", dql);
            Element akb = dqlreq.getChild("answerKBPattern", dql);
            Element asb = dqlreq.getChild("answerSizeBound", dql);
            
            Element serverContinuation =
            dqlreq.getChild("serverContinuation", dql);
            Element serverTermination =
            dqlreq.getChild("serverTermination", dql);
            
            if(asb != null) {
                count = Integer.parseInt(asb.getTextTrim());
            }
            if(serverContinuation != null) {
                Element ph = serverContinuation.getChild("processHandle", dql);
                int phid = Integer.parseInt(ph.getTextTrim());
                printHeader(pw);
                DQLProcess dp = (DQLProcess) phnds.elementAt(phid);
                
                
                printAnswersFromIterator(false, dp.ai, count, pw, null,
                dp.kburl, dp.qap);
                printContinuation(dp.ai, count, dp.qap, pw, dp.kburl);
                printFooter(pw);
                return;
            }
            if(serverTermination != null) {
                Element ph = serverTermination.getChild("processHandle", dql);
                int phid = Integer.parseInt(ph.getTextTrim());
                printHeader(pw);
                DQLProcess dp = (DQLProcess) phnds.elementAt(phid);
                phnds.setElementAt(null, phid);
                printContinuation(dp.ai, count, dp.qap, pw, dp.kburl);
                printFooter(pw);
                return;
            }
            
            if(premise != null) {
                Element rdf = premise.getChild("RDF",
                Namespace.getNamespace(rdfns));
                XMLOutputter xo = new XMLOutputter();
                premiseSet = xo.outputString(rdf);
            }
            
            querySet = dqlToKIF(query, pw);
            if(querySet == null) {
                pw.println("I couldn't make sense of your query!");
                return;
            }
            else {
                XMLOutputter xo = new XMLOutputter();
                queryXML = xo.outputString(query);
            }
            if(mustvars != null) {
                addVars(mustvars, musts);
            }
            if(mayvars != null) {
                addVars(mayvars, mays);
            }
            if(akb != null) {
                Iterator ait = akb.getChildren().iterator();
                while(ait.hasNext()) {
                    Element kb = (Element) ait.next();
                    String kbname = kb.getName();
                    Namespace kbns = kb.getNamespace();
                    if(kbns.getURI().equals(varns)) {
                        kbvar = kbname;
                    }
                    else if(kbname.equals("kbRef") &&
                    kbns.getURI().equals(dqlns)) {
                        String qkb =
                        kb.getAttributeValue("resource",
                        Namespace.getNamespace(rdfns));
                        if(qkb == null || qkb.equals("")) {
                            pw.println("I don't know about a KB named " + qkb);
                            return;
                        }
                        kbset.add(qkb);
                    }
                }
            }
        }
        catch(Exception e) {
            pw.println("I caught an exception parsing your request!");
            e.printStackTrace(pw);
            return;
        }
        
        if(kbvar != null && kbset.size() == 0) {
            Enumeration ki = drcs.keys();
            while(ki.hasMoreElements()) {
                kbset.add((String) ki.nextElement());
            }
        }
        
        Iterator di = kbset.iterator();
        while(di.hasNext()) {
            String kburl = (String) di.next();
            DamlReasoningContext drc = findKB(kburl);
            synchronized(drc) {
                SnapshotUndoManager sum =
                (SnapshotUndoManager) drc.getUndoManager();
                Snapshot snap = sum.getSnapshot();
                try {
                    if(premiseSet != null) {
                        drc.tellString(premiseSet);
                    }
                    printHeader(pw);
                    
                    pw.println("  " + queryXML);
                    
                    ReasoningStepIterator rsi = drc.ask(querySet);
                    Vector allBind = new Vector();
                    Vector answers = new Vector();
                    ReasoningStep rs = rsi.next();
                    boolean needEmpty = (rs != null);
                    for (int x = 0; (count == -1 || x < count) && rs != null;
                    rs = rsi.next()) {
                        Map bindings = getTopLevelBindings(rs);
                        if (allBind.contains(bindings) || bindings.isEmpty()) {
                            continue;
                        }
                        Vector aset = new Vector();
                        
                        Iterator it = bindings.entrySet().iterator();
                        while(it.hasNext()) {
                            Map.Entry ent = (Map.Entry)it.next();
                            Object key = ent.getKey();
                            if(key == null) {
                                continue;
                            }
                            String varname = key.toString().substring(1);
                            Object ev = ent.getValue();
                            if(ev == null) {
                                continue;
                            }
                            String varval = unKIF(ev.toString());
                            if(varval.startsWith("Anon_") ||
                            varval.startsWith("jtp.frame.") ||
                            (!musts.contains(varname) &&
                            !mays.contains(varname))) {
                                continue;
                            }
                            aset.add(new Binding(varname, varval));
                        }
                        /* Make sure we got bindings for all the musts */
                        Iterator i = musts.iterator();
                        while(i.hasNext()) {
                            String vn = (String) i.next();
                            Iterator ai = aset.iterator();
                            boolean hadIt = false;
                            while(!hadIt && ai.hasNext()) {
                                Binding a = (Binding) ai.next();
                                if(a.name.equals(vn)) {
                                    hadIt = true;
                                }
                            }
                            if(!hadIt) {
                                aset = new Vector();
                                break;
                            }
                        }
                        allBind.add(bindings);
                        
                        if(aset.isEmpty()) {
                            continue;
                        }
                        answers.add(aset);
                    }
                    
                    Iterator ai = answers.iterator();
                    Element ansP = (answerPattern != null
                    ? answerPattern : query);
                    printAnswersFromIterator(needEmpty, ai, count, pw,
                    kbvar, kburl, ansP);
                    printContinuation(ai, count, ansP, pw, kburl);
                    printFooter(pw);
                }
                catch(Exception e) {
                    pw.println("Caught exception working with query!");
                    e.printStackTrace(pw);
                }
                try {
                    sum.revertToSnapshot(snap);
                }
                catch(Exception e) {
                    pw.println("Caught exception reverting to snapshot");
                    e.printStackTrace(pw);
                }
            }
        }
    }
    
    /**
     * Performs the servlet POST method.
     *
     * @param request the given DQL request
     * @param repsponse the resulting DQL response
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        doGet(request, response);
    }
    
}
