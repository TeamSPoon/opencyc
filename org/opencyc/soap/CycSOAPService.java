// remove package stmt when deploying as jws (java web service)
package org.opencyc.soap;

import javax.xml.rpc.*;
import java.net.*;
import java.util.*;
import java.rmi.RemoteException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;
import ViolinStrings.Strings;

/**
 * Provides the Cyc API as an XML SOAP service.  Note that Tomcat/AXIS 
 * deployment via Java Web Service (JWS) does not work if this module is part of
 * a package.<p>
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
public class CycSOAPService {

/*

    Required jars:

    jakarta-oro-2.0.3.jar
    jug.jar
    ViolinStrings.jar
    OpenCyc.jar


*/
    /**
     * the CycAccess object which manages the Cyc server connection and provides
     * the API methods
     */
    protected static CycAccess cycAccess = null;
    
    /**
     * Construct a new CycSOAPService object.
     */
    public CycSOAPService() {
    }

    /**
     * Provides a simple message to test the CycSOAPService without accessing
     * the Cyc server.
     */
    public String getHelloWorldMessage(String name) {
        return "Hello World to " + name;
    }

    /**
     * Provides the main method for the CycSOAPService application without the Tomcat server.
     *
     * @param args the command line arguments (not used)
     */
    public static void main (String[] args) {
        Log.makeLog("cyc-soap-service.log");
        CycSOAPService cycSOAPService = new CycSOAPService();
        String subLRequest = "(isa #$TransportationDevice)";
        String response = cycSOAPService.subLInteractor(subLRequest);
        Log.current.println("subLrequest=" + subLRequest + " result=" + response);
        subLRequest = "(constant-name #$BaseKB)";
        response = cycSOAPService.subLInteractor(subLRequest);
        Log.current.println("subLrequest=" + subLRequest + " result=" + response);
        subLRequest = "(identity \"abc \\\"def\\\" ghi\")";
        response = cycSOAPService.subLInteractor(subLRequest);
        Log.current.println("subLrequest=" + subLRequest + "\n     result=" + response);
        subLRequest = "(identity (quote (#$givenNames #$Guest \"\\\"The\\\" Guest\")))";
        response = cycSOAPService.subLInteractor(subLRequest);
        Log.current.println("subLrequest=" + subLRequest + "\n     result=" + response);
        
        Log.current.println("categorizeEntity Service");
        String entityString = "Osama Bin Laden";
        String generalEntityKindString = "PERSON";
        Log.current.println("categorizeEntity(\"" + entityString + "\", \"" + generalEntityKindString + "\")");
        response = cycSOAPService.categorizeEntity(entityString, generalEntityKindString);
        Log.current.println("response=" + response);
    }
    
    /**
     * Provides a SubL interactor service.
     *
     * @param subLRequest the given SubL request which will be submitted to the
     * Cyc server for evaluation
     * @return the result of evaluating the given SubL request
     */
    public String subLInteractor(String subLRequest) {
        try {
            if (cycAccess == null) {
                String localHostName = InetAddress.getLocalHost().getHostName();
                if (localHostName.equals("crapgame.cyc.com")) {
                    cycAccess = new CycAccess("localhost",
                            3620,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else if (localHostName.equals("proton.cyc.com") ||
                         localHostName.equals("eclipse.cyc.com")) {
                    cycAccess = new CycAccess("localhost",
                            3620,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else if (localHostName.equals("thinker")) {
                    cycAccess = new CycAccess("localhost",
                            3600,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else
                    cycAccess = new CycAccess();
            }
            Object response = null;
            try {
                response = cycAccess.converseObject(subLRequest);
            }
            catch (CycApiException e) {
                return e.getMessage();
            }
            String responseString;
            if (response instanceof CycList)
                responseString = ((CycList) response).cyclifyWithEscapeChars();
            else if (response instanceof CycFort)
                responseString = ((CycFort) response).cyclify();
            else if (response instanceof String) {
                responseString = "\"" + response.toString() + "\"";
            }
            else
                responseString = response.toString();
            return responseString;
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    
    /**
     * Categorizes the given entity within the Cyc KB.
     *
     * @param entity the given entity to categorize
     * @param entityKind the given general entity kind as determined from information
     * extraction
     * @return an XML structure consisting of the mathched entity paraphrase and Cyc category, 
     * and if unmatched return an empty string
     */
    public String categorizeEntity(String entityString, String generalEntityKindString) {
        try {
            if (cycAccess == null) {
                String localHostName = InetAddress.getLocalHost().getHostName();
                if (localHostName.equals("crapgame.cyc.com")) {
                    cycAccess = new CycAccess("localhost",
                            3620,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else if (localHostName.equals("proton.cyc.com") ||
                         localHostName.equals("eclipse.cyc.com")) {
                    cycAccess = new CycAccess("localhost",
                            3620,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else if (localHostName.equals("thinker")) {
                    cycAccess = new CycAccess("localhost",
                            3600,
                            CycConnection.ASCII_MODE,
                            true);
                }
                else
                    cycAccess = new CycAccess();
            }
            CycList denotationList = null;
            CycFort entity = null;
            CycList generalEntityKinds = new CycList();
            try {
                if (generalEntityKindString.equals("ADDRESS"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Address-LocationDesignator"));
                else if (generalEntityKindString.equals("ADDRESS_INTERNET"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("ContactInfoString")); 
                else if (generalEntityKindString.equals("CITY"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("City")); 
                else if (generalEntityKindString.equals("COMPANY"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("CommercialOrganization")); 
                else if (generalEntityKindString.equals("COUNTRY"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Country")); 
                else if (generalEntityKindString.equals("CURRENCY")) {
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Currency")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("MonetaryValue")); 
                }
                else if (generalEntityKindString.equals("DATE"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Date")); 
                else if (generalEntityKindString.equals("DAY"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("CalendarDay")); 
                else if (generalEntityKindString.equals("HOLIDAY"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Holiday")); 
                else if (generalEntityKindString.equals("LANGUAGE")) {
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("NaturalLanguage")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("HumanLanguage")); 
                }
                else if (generalEntityKindString.equals("MEASURE")) {
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("NumericValue")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("UnitOfMeasure")); 
                }
                else if (generalEntityKindString.equals("MONTH"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("CalendarMonth")); 
                else if (generalEntityKindString.equals("NOUN_GROUP"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("NounPhrase")); 
                else if (generalEntityKindString.equals("ORGANIZATION"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Organization")); 
                else if (generalEntityKindString.equals("PEOPLES")) {
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("PersonTypeByCulture")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("PersonTypeByEthnicity")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("PersonTypeByGeopoliticalAffilation")); 
                }
                else if (generalEntityKindString.equals("PERCENT")) {
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("IntervalOnNumberLine")); 
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Percent")); 
                }
                else if (generalEntityKindString.equals("PERSON"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Person")); 
                else if (generalEntityKindString.equals("PERSON_POS")) {
                    // unhandled with regard to type
                }
                else if (generalEntityKindString.equals("PHONE"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("PhoneNumber")); 
                else if (generalEntityKindString.equals("PLACE_OTHER"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("GeographicalRegion")); 
                else if (generalEntityKindString.equals("PLACE_POLIT"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("ControlledLand")); 
                else if (generalEntityKindString.equals("PLACE_REGION"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("GeographicalRegion")); 
                else if (generalEntityKindString.equals("PRODUCT"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("ProductType")); 
                else if (generalEntityKindString.equals("PROP_MISC"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Individual")); 
                else if (generalEntityKindString.equals("PUBLICATION"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("PeriodicalSeries")); 
                else if (generalEntityKindString.equals("SSN"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("SocialSecurityNumber")); 
                else if (generalEntityKindString.equals("STATE"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("")); 
                else if (generalEntityKindString.equals("State-Geopolitical"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("")); 
                else if (generalEntityKindString.equals("TIME"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("Date")); 
                else if (generalEntityKindString.equals("TIME_PERIOD"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("TimeInterval")); 
                else if (generalEntityKindString.equals("VEHICLE"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("TransportationDevice-Vehicle")); 
                else if (generalEntityKindString.equals("YEAR"))
                    generalEntityKinds.add(cycAccess.getKnownConstantByName("CalendarYear")); 
                else
                    return("error: generalEntityKindString " + generalEntityKindString + " not found");
                
                /*
                if (entityString.indexOf(" ") > 0) {
                    String[] words = Strings.split(entityString);
                    CycList multiWords = new CycList();
                    for (int i = 0; i < words.length; i++)
                        multiWords.add(words[i]);
                    denotationList = cycAccess.getMWSDenotsOfString(multiWords, generalEntityKinds);

                }
                else
                 **/
                denotationList = cycAccess.getDenotsOfString(entityString, generalEntityKinds);
            }
            catch (CycApiException e) {
                return "error: " + e.getMessage();
            }
            StringBuffer stringBuffer = new StringBuffer();
            Log.current.println("denotationList=" + denotationList.cyclify());
            if (denotationList.isEmpty()) {
                // no match in Cyc
                stringBuffer.append("<entity canonical=\"");
                stringBuffer.append(entityString);
                stringBuffer.append("\" category=\"Unknown\" />\n");
                return stringBuffer.toString();
            }
            Iterator denotationIterator = denotationList.iterator();
            while (denotationIterator.hasNext()) {
                CycFort denotation = (CycFort) denotationIterator.next();
                CycList categories = null;
                if (cycAccess.isIndividual(denotation)) 
                    categories = cycAccess.getMinIsas(denotation);
                else if (cycAccess.isCollection(denotation))
                    categories = cycAccess.getMinGenls(denotation);
                else
                    return "error: " + denotation.cyclify() + "is neither a collection nor individual";
                CycFort category = null;
                categories = transformEmbeddedNarts(categories);
                Log.current.println("categories=" + categories.cyclify());
                Iterator categoryIterator = categories.iterator();
                boolean done = false;
                while (categoryIterator.hasNext() && ! done) {
                    category = (CycFort) categoryIterator.next();
                    Iterator generalEntityKindsIterator = generalEntityKinds.iterator();
                    while (generalEntityKindsIterator.hasNext()) {
                        CycFort generalEntityKind = (CycFort) generalEntityKindsIterator.next();
                        Log.current.println("isGenlOf? " + generalEntityKind.cyclify() + " " + category.cyclify());
                        if (cycAccess.isGenlOf(generalEntityKind, category)) {
                            done = true;
                            break;
                        }
                    }
                }
                stringBuffer.append("<entity canonical=\"");
                stringBuffer.append(cycAccess.getImpreciseSingularGeneratedPhrase(denotation));
                stringBuffer.append("\" category=\"");
                if (category == null) 
                    stringBuffer.append(generalEntityKindString);
                else
                    stringBuffer.append(cycAccess.getImpreciseSingularGeneratedPhrase(category));
                stringBuffer.append("\" />\n");
            }
            return stringBuffer.toString();
        }
        catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * Transforms any contained bracketed expressions into CycNarts.  This is an artifact of
     * some ASCII API calls.
     */
    public CycList transformEmbeddedNarts (CycList cycList) {
        CycList result = new CycList();
        Iterator iter = cycList.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof CycList)
                result.add(transformEmbeddedNarts((CycList) obj));
            else if (obj instanceof CycSymbol &&
                     obj.toString().equals("#<")) {
                // convert CycList representation to CycNart
                result.add(new CycNart((CycList) iter.next()));
                // discard ">" symbol
                iter.next();
            }
            else
                result.add(obj);
        }
        return result;
    }
    
}
