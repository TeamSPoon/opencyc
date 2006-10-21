/*
 * DQLClient.java: DQL Client, takes queries and handles the cruft
 * of asking over the network for it.
 */

package org.opencyc.xml;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

import org.jdom.JDOMException;

public class DQLClient {
    public static String dqlns = "http://www.daml.org/2002/10/dql-syntax#";
    public static String varns = "http://www.daml.org/2002/10/dql-variables#";
    public static String rdfns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String rdfsns = "http://www.w3.org/2000/01/rdf-schema#";
    public static String damlns = "http://www.daml.org/2001/03/daml+oil#";
    public static String soapns = "http://schemas.xmlsoap.org/soap/envelope/";
    
    public static final int DQL_MUSTBIND = 1;
    public static final int DQL_MAYBIND = 2;
    public static final int DQL_DONTBIND = 3;
    
    public static final int DQL_NONE = 4;
    public static final int DQL_END = 5;
    public static final int DQL_CONTINUATION = 6;
    
    
    public URL svr;
    
    public static String abbreviate(String res, String pfx, String url) {
        return (res.startsWith(url)
        ? pfx + ":" + res.substring(url.length())
        : res);
    }
    public static String abbreviate(String res) {
        String ret = abbreviate(res, "dql", dqlns);
        ret = abbreviate(ret, "var", varns);
        ret = abbreviate(ret, "rdf", rdfns);
        ret = abbreviate(ret, "rdfs", rdfsns);
        return abbreviate(ret, "daml", damlns);
    }
    public static String abbreviate(String res, String kburl) {
        String ret = abbreviate(res);
        return kburl.equals("") ? ret : abbreviate(ret, "tkb", kburl);
    }
    
    public static String unabbreviate(String res, String pfx, String url) {
        String npfx = pfx + ":";
        String filler = "";
        if(!url.endsWith("/") && !url.endsWith("#")) {
            url = url + "#";
        }
        return (res.startsWith(npfx)
        ? url + res.substring(npfx.length())
        : res);
    }
    public static String unabbreviate(String res) {
        String ret = unabbreviate(res, "dql", dqlns);
        ret = unabbreviate(ret, "var", varns);
        ret = unabbreviate(ret, "rdf", rdfns);
        ret = unabbreviate(ret, "rdfs", rdfsns);
        return unabbreviate(ret, "daml", damlns);
    }
    public static String unabbreviate(String res, String kburl) {
        String ret = unabbreviate(res);
        return kburl.equals("") ? ret : unabbreviate(ret, "tkb", kburl);
    }
    
    public static String getResourceNamespace(String url) {
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
    
    public static String getResourceTag(String url) {
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
    
    public DQLClient() {
        svr = null;
    }
    public DQLClient(URL url) {
        svr = url;
    }
    public DQLClient(String url) throws MalformedURLException {
        svr = new URL(url);
    }
    public void setURL(String url) throws MalformedURLException {
        svr = new URL(url);
    }
    public void setURL(URL url) {
        svr = url;
    }
    
    public String getResponseString(DQLRequest dqlreq, URL u)
    throws IOException, JDOMException {
        String request = dqlreq.toXML();
        URLConnection conn = u.openConnection();
        conn.setDoOutput(true);
        byte req[] = request.getBytes();
        conn.setRequestProperty("Content-length",
        Integer.toString(req.length));
        conn.setRequestProperty("Content-type", "text/xml");
        OutputStream os = conn.getOutputStream();
        os.write(req);
        
        Object resp = conn.getContent();
        String ct = conn.getContentType();
        InputStream body = (InputStream) resp;
        InputStreamReader isr = new InputStreamReader(body);
        LineNumberReader lr = new LineNumberReader(isr);
        StringBuffer ret = new StringBuffer();
        
        while(true) {
            String line = lr.readLine();
            if(line == null) {
                break;
            }
            ret.append(line);
            ret.append("\n");
        }
        return ret.toString();
    }
    public String getResponseString(DQLRequest req)
    throws IOException, JDOMException {
        return getResponseString(req, svr);
    }
    
    
    public DQLResponse getResponse(DQLRequest dqlreq, URL u)
    throws IOException, JDOMException {
        return new DQLResponse(getResponseString(dqlreq, u));
    }
    public DQLResponse getResponse(DQLRequest req)
    throws IOException, JDOMException {
        return getResponse(req, svr);
    }
    public DQLResponse getResponse(DQLRequest req, String url)
    throws MalformedURLException, IOException, JDOMException {
        return getResponse(req, new URL(url));
    }
    
}
