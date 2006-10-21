/*
 * DQLRequest.java: Formulate a DQL query and get its XML representation.
 */
package org.opencyc.xml;
import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;


public class DQLRequest {
    String premise, query, answerPattern, serverContinue, serverTerminate;
    Vector mustbind, maybind, answerkbs;
    int maxAns;
    
    public DQLRequest() {
        premise = "";
        query = "";
        answerPattern = "";
        serverContinue = "";
        serverTerminate = "";
        mustbind = new Vector();
        maybind = new Vector();
        answerkbs = new Vector();
        maxAns = -1;
    }
    
    public void addPremiseTriple(String kburl,
    String predicate,
    String source,
    String object) {
        if(kburl.startsWith(DQLClient.varns)) {
            kburl = "urn:tkb#";
        }
        predicate = DQLClient.abbreviate(predicate, kburl);
        String predns = DQLClient.getResourceNamespace(predicate);
        String predtag = predicate;
        if(!predns.equals("")) {
            predtag = "n0:" + DQLClient.getResourceTag(predicate);
            predicate = predtag + " xmlns:n0=\"" + predns + "\"";
        }
        source = DQLClient.unabbreviate(source, kburl);
        object = DQLClient.unabbreviate(object, kburl);
        premise = premise + "<rdf:Description rdf:about=\"" + source + "\">\n";
        premise = premise + "<" + predicate;
        if(object.startsWith("urn:") || object.startsWith("http://")) {
            premise = premise + " rdf:resource=\"" + object + "\"/>\n";
        }
        else {
            premise = premise + ">" + object + "</" + predtag + ">\n";
        }
        premise = premise + "</rdf:Description>\n";
    }
    
    public void addPremiseTriple(String predicate,
    String source,
    String object) {
        addPremiseTriple("", predicate, source, object);
    }
    
    
    public void addQueryTriple(String kburl,
    String predicate,
    String source,
    String object) {
        if(kburl.startsWith(DQLClient.varns)) {
            kburl = "urn:tkb#";
        }
        predicate = DQLClient.abbreviate(predicate, kburl);
        String predns = DQLClient.getResourceNamespace(predicate);
        String predtag = predicate;
        if(!predns.equals("")) {
            predtag = "n0:" + DQLClient.getResourceTag(predicate);
            predicate = predtag + " xmlns:n0=\"" + predns + "\"";
        }
        source = DQLClient.unabbreviate(source, kburl);
        object = DQLClient.unabbreviate(object, kburl);
        query = query + "<rdf:Description rdf:about=\"" + source + "\">\n";
        query = query + "<" + predicate;
        if(object.startsWith("urn:") || object.startsWith("http://")) {
            query = query + " rdf:resource=\"" + object + "\"/>\n";
        }
        else {
            query = query + ">" + object + "</" + predtag + ">\n";
        }
        query = query + "</rdf:Description>\n";
    }
    
    public void addQueryTriple(String predicate,
    String source,
    String object) {
        addQueryTriple("", predicate, source, object);
    }
    
    public void addAnswerPatternTriple(String kburl,
    String predicate,
    String source,
    String object) {
        if(kburl.startsWith(DQLClient.varns)) {
            kburl = "urn:tkb#";
        }
        predicate = DQLClient.abbreviate(predicate, kburl);
        String predns = DQLClient.getResourceNamespace(predicate);
        String predtag = predicate;
        if(!predns.equals("")) {
            predtag = "n0:" + DQLClient.getResourceTag(predicate);
            predicate = predtag + " xmlns:n0=\"" + predns + "\"";
        }
        source = DQLClient.unabbreviate(source, kburl);
        object = DQLClient.unabbreviate(object, kburl);
        answerPattern = answerPattern + "<rdf:Description rdf:about=\"" +
        source + "\">\n";
        answerPattern = answerPattern + "<" + predicate;
        if(object.startsWith("urn:") || object.startsWith("http://")) {
            answerPattern = answerPattern + " rdf:resource=\"" +
            object + "\"/>\n";
        }
        else {
            answerPattern = answerPattern + ">" + object + "</" +
            predtag + ">\n";
        }
        answerPattern = answerPattern + "</rdf:Description>\n";
    }
    
    public void addAnswerPatternTriple(String predicate,
    String source,
    String object) {
        addAnswerPatternTriple("", predicate, source, object);
    }
    
    
    public void setPremiseDAML(String daml) {
        premise = daml.trim();
    }
    
    public void setQueryDAML(String daml) {
        query = daml.trim();
    }
    
    public void setAnswerPatternDAML(String daml) {
        answerPattern = daml.trim();
    }
    
    /* Set variable to must bind, may bind, or don't bind */
    public void setVariableState(String varname, int state) {
        switch(state) {
            case DQLClient.DQL_MUSTBIND:
                for(Iterator i = mustbind.iterator(); i.hasNext(); ) {
                    if(((String) i.next()).equals(varname)) {
                        return;
                    }
                }
                mustbind.add(varname);
                break;
            case DQLClient.DQL_MAYBIND:
                for(Iterator i = maybind.iterator(); i.hasNext(); ) {
                    if(((String) i.next()).equals(varname)) {
                        return;
                    }
                }
                maybind.add(varname);
                break;
        }
    }
    
    public void setMaxAnswers(int ma) {
        maxAns = ma;
    }
    
    public void addAnswerKB(String kburl) throws MalformedURLException {
        if(kburl.startsWith(DQLClient.varns)) {
            answerkbs.add(kburl);
        }
        else {
            if(kburl.endsWith("#")) {
                kburl = kburl.substring(0, kburl.length() - 1);
            }
            answerkbs.add(new URL(kburl));
        }
    }
    public void addAnswerKB(URL kburl) {
        answerkbs.add(kburl);
    }
    public void addAnswerKB_rdf(String rdf) {
        answerkbs.add(rdf.trim());
    }
    
    public void setServerTermination(String processHandle) {
        serverTerminate = processHandle;
    }
    
    public void setServerContinuation(String processHandle) {
        serverContinue = processHandle;
    }
    
    
    public String toXML() {
        String ret =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"" +
        DQLClient.soapns + "\"\n" +
        "                   SOAP-ENV:encodingStyle=\"" +
        "http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "<SOAP-ENV:Body>\n" +
        "<dql:query xmlns:dql=\"" + DQLClient.dqlns + "\"\n" +
        "           xmlns:var=\"" + DQLClient.varns + "\"\n";
        
        if(answerkbs.size() == 1 &&
        answerkbs.elementAt(0) instanceof String &&
        !((String) answerkbs.elementAt(0)).startsWith(DQLClient.varns)) {
            String kburl = (String) answerkbs.elementAt(0);
            if(!kburl.endsWith("#")) {
                kburl = kburl + "#";
            }
            ret = ret + "           xmlns:tkb=\"" + kburl + "\"\n";
        }
        ret = ret +
        "           xmlns:rdf=\"" + DQLClient.rdfns + "\"\n" +
        "           xmlns:rdfs=\"" + DQLClient.rdfsns + "\"\n" +
        "           xmlns:daml=\"" + DQLClient.damlns + "\">\n";
        if(premise != null && !premise.equals("")) {
            if(!premise.startsWith("<rdf:RDF")) {
                premise = "<rdf:RDF>" + premise + "</rdf:RDF>";
            }
            ret = ret + "<dql:premise>\n" + premise + "\n" + "</dql:premise>\n";
        }
        if(query != null && !query.equals("")) {
            if(!query.startsWith("<rdf:RDF")) {
                query = "<rdf:RDF>" + query + "</rdf:RDF>";
            }
            ret = ret + "<dql:queryPattern>\n" + query + "\n" +
            "</dql:queryPattern>\n";
        }
        
        if(answerPattern != null && !answerPattern.equals("")) {
            if(!answerPattern.startsWith("<rdf:RDF")) {
                answerPattern = "<rdf:RDF>" + answerPattern + "</rdf:RDF>";
            }
            ret = ret + "<dql:answerPattern>\n" + answerPattern + "\n" +
            "</dql:answerPattern>\n";
        }
        if(serverTerminate != null && !serverTerminate.equals("")) {
            ret = ret + "<dql:serverTermination><dql:processHandle>" +
            serverTerminate +
            "</dql:processHandle></dql:serverTermination>\n";
        }
        if(serverContinue != null && !serverContinue.equals("")) {
            ret = ret + "<dql:serverContinuation><dql:processHandle>" +
            serverContinue +
            "</dql:processHandle></dql:serverContinuation>\n";
        }
        
        if(mustbind.size() > 0) {
            ret = ret + "<dql:mustBindVars>\n";
            Iterator i = mustbind.iterator();
            while(i.hasNext()) {
                String s = (String) i.next();
                if(s.startsWith(DQLClient.varns)) {
                    s = s.substring(DQLClient.varns.length());
                }
                ret = ret + "<var:" + s + "/>";
            }
            ret = ret + "</dql:mustBindVars>\n";
        }
        if(maybind.size() > 0) {
            ret = ret + "<dql:mayBindVars>\n";
            Iterator i = maybind.iterator();
            while(i.hasNext()) {
                String s = (String) i.next();
                if(s.startsWith(DQLClient.varns)) {
                    s = s.substring(DQLClient.varns.length());
                }
                ret = ret + "<var:" + s + "/>";
            }
            ret = ret + "</dql:mayBindVars>\n";
        }
        if(maxAns != -1) {
            ret = ret + "<dql:answerSizeBound>" + maxAns +
            "</dql:answerSizeBound>";
        }
        ret = ret + "<dql:answerKBPattern>\n";
        Iterator i = answerkbs.iterator();
        while(i.hasNext()) {
            Object o = i.next();
            if(o instanceof String) {
                String s = (String) o;
                if(s.startsWith(DQLClient.varns)) {
                    ret = ret + "<var:" +
                    s.substring(DQLClient.varns.length()) + "/>";
                }
                else {
                    if(!s.startsWith("<rdf:RDF>")) {
                        s = "<rdf:RDF>" + s + "</rdf:RDF>";
                    }
                    ret = ret + s;
                }
            }
            else if(o instanceof URL) {
                ret = ret + "<dql:kbRef rdf:resource=\"" +
                ((URL) o).toString() + "\"/>";
            }
        }
        ret = ret + "</dql:answerKBPattern>\n" +
        "</dql:query>\n" +
        "</SOAP-ENV:Body>\n" +
        "</SOAP-ENV:Envelope>\n";
        return ret;
    }
}
