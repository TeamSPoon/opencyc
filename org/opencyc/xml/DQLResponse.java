/* 
 * DQLResponse.java: Handles DQL responses from remote servers.
 */

import java.io.*;
import java.text.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.net.*;


public class DQLResponse
{
    String xml;
    boolean success;
    Vector answers;
    Vector damls;
    int terminationType;
    String processHandle;


    public void parseResponse(String x)
	throws JDOMException
    {
	StringReader sr = new StringReader(x);
	Element env = new SAXBuilder().build(sr).getRootElement();
	Namespace soap = Namespace.getNamespace(DQLClient.soapns);
	Namespace dql = Namespace.getNamespace(DQLClient.dqlns);
	Namespace var = Namespace.getNamespace(DQLClient.varns);
	Element body = env.getChild("Body", soap);
	Element dqe = body.getChild("answerBundle", dql);
	Iterator cit = dqe.getChildren().iterator();
	answers = new Vector();
	success = false;

	while(cit.hasNext()) {
	    Element c = (Element) cit.next();
	    String name = c.getName();
	    Namespace ns = c.getNamespace();
	    if(name.equals("answer") && ns.getURI().equals(DQLClient.dqlns)) {
		success = true;
		Element bs = c.getChild("binding-set", 
			    Namespace.getNamespace(DQLClient.dqlns));
		Iterator bsit = bs.getChildren().iterator();
		Vector bindings = new Vector();
		answers.addElement(bindings);
		while(bsit.hasNext()) {
		    Element v = (Element) bsit.next();
		    String vn = v.getName();
		    String binding = v.getAttributeValue("resource",
				Namespace.getNamespace(DQLClient.rdfns));
		    binding = DQLClient.abbreviate(binding);
		    bindings.add(vn + "=" + binding);
		}
	    }
	    else if(name.equals("continuation") && 
		    ns.getURI().equals(DQLClient.dqlns)) 
	    {
		Element none = c.getChild("none", 
				     Namespace.getNamespace(DQLClient.dqlns));
		Element end = c.getChild("end", 
				     Namespace.getNamespace(DQLClient.dqlns));
		Element ph = c.getChild("processHandle", 
				     Namespace.getNamespace(DQLClient.dqlns));
		if(none != null) {
		    terminationType = DQLClient.DQL_NONE;
		}
		else if(end != null) {
		    terminationType = DQLClient.DQL_END;
		}
		else if(ph != null) {
		    terminationType = DQLClient.DQL_CONTINUATION;
		    processHandle = ph.getTextTrim();
		}
	    }
	}
    }

    public void parseResponse() throws JDOMException
    {
	parseResponse(xml);
    }

    public DQLResponse(String x) throws JDOMException
    {
	xml = x;
	success = false;
	answers = null;
	damls = null;
	terminationType = DQLClient.DQL_NONE;
	processHandle = null;
	parseResponse();
    }

    public boolean wasSuccessful()
    {
	return success;
    }


    public static String getVariableName(String str)
    {
	int i = str.indexOf("=");
	return str.substring(0, i);
    }
    public static String getVariableValue(String str)
    {
	int i = str.indexOf("=");
	return str.substring(i+1);
    }


    public int getTerminationType()
    {
	return terminationType;
    }
    public String getProcessHandle()
    {
	return processHandle;
    }


    public Vector getBindings()
    {
	return answers;
    }

    public Vector getVariableValues(String wanted)
    {
	Vector ret = new Vector();
	Vector answers = getBindings();
	for(Iterator ia = answers.iterator(); ia.hasNext(); ) {
	    Vector bindings = (Vector) ia.next();
	    for(Iterator ib = bindings.iterator(); ib.hasNext(); ) {
		String var = (String) ib.next();
		if(getVariableName(var).equals(wanted)) {
		    ret.add(getVariableValue(var));
		}
	    }
	}
	return ret;
    }

    public String[] getDAMLset()
    {
	String ret[] = new String[damls.size()];
	for(int x = 0; x < damls.size(); ++x) {
	    ret[x] = (String) damls.elementAt(x);
	}
	return ret;
    }
    public String getDAML()
    {
	String set[] = getDAMLset();
	String ret = "<rdf:RDF>";
	for(int x = 0; x < set.length; ++x) {
	    ret = ret + set[x];
	}
	return ret + "</rdf:RDF>";
    }

    public String toXML()
    {
	return xml;
    }
}
