package org.opencyc.api;

import java.util.*;
import java.io.*;
import org.apache.oro.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.xml.*;

/**
 * Provides the way to create cyc objects and reuse previously cached instances.<br>
 *
 * All methods are static.<p>
 *
 * Collaborates with the <tt>CycConnection</tt> class which manages the api connections.
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
public class CycObjectFactory {

    /**
     * Least Recently Used Cache of CycSymbols, so that a reference to an existing <tt>CycSymbol</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cycSymbolCache = new CacheLRU(500);

    /**
     * Built in CycSymbols.
     */
    public static CycSymbol t = makeCycSymbol("T");
    public static CycSymbol nil = makeCycSymbol("NIL");
    public static CycSymbol quote = makeCycSymbol("QUOTE");
    public static CycSymbol backquote = makeCycSymbol("`");
    public static CycSymbol cons = makeCycSymbol("CONS");
    public static CycSymbol dot = makeCycSymbol(".");

    /**
     * The api command which is intercepted by the CycProxy agent to close the CycAccess object
     * associated with the connection between this agent and the particular cyc image.
     */
    public static final CycList END_CYC_CONNECTION = (new CycList(makeCycSymbol("end-cyc-connection")));

    /**
     * Least Recently Used Cache of CycConstants, so that a reference to an existing <tt>CycConstant</tt>
     * is returned instead of constructing a duplicate.  Indexed via the name, so is optimised for the ascii api.
     */
    protected static Cache cycConstantCacheByName = new CacheLRU(10000);

    /**
     * Least Recently Used Cache of CycConstants, so that a reference to an existing <tt>CycConstant</tt>
     * is returned instead of constructing a duplicate.  Indexed via the id, so is optimised for the binary api.
     */
    protected static Cache cycConstantCacheById = new CacheLRU(10000);

    /**
     * Least Recently Used Cache of CycConstants, so that a reference to an existing <tt>CycConstant</tt>
     * is returned instead of constructing a duplicate.  Indexed via the guid.
     */
    protected static Cache cycConstantCacheByGuid = new CacheLRU(10000);

    /**
     * Least Recently Used Cache of CycNarts, so that a reference to an existing <tt>CycNart</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cycNartCache = new CacheLRU(500);

    /**
     * Least Recently Used Cache of CycAssertions, so that a reference to an existing <tt>CycAssertion</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache assertionCache = new CacheLRU(500);

    /**
     * Least Recently Used Cache of CycVariables, so that a reference to an existing <tt>CycVariable</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache cycVariableCache = new CacheLRU(500);

    /**
     * A variable name suffix used to make unique names.
     */
    protected static int suffix = 1;

    /**
     * Least Recently Used Cache of guids, so that a reference to an existing <tt>Guid</tt>
     * is returned instead of constructing a duplicate.
     */
    protected static Cache guidCache = new CacheLRU(500);

    /**
     * Constructs a new <tt>CycSymbol</tt> object.
     *
     * @param symbolName a <tt>String</tt> name.
     */
    public static CycSymbol makeCycSymbol(String symbolNameAnyCase) {
        String symbolName = symbolNameAnyCase.toUpperCase();
        CycSymbol cycSymbol = (CycSymbol) cycSymbolCache.getElement(symbolName);
        if (cycSymbol == null) {
            cycSymbol = new CycSymbol(symbolName);
            cycSymbolCache.addElement(symbolName, cycSymbol);
        }
        return cycSymbol;
    }

    /**
     * Resets the <tt>CycSymbol</tt> cache.
     */
    public static void resetCycSymbolCache() {
        cycSymbolCache = new CacheLRU(500);
        nil = makeCycSymbol("NIL");
        quote = makeCycSymbol("QUOTE");
        cons = makeCycSymbol("CONS");
        dot = makeCycSymbol(".");
    }

    /**
     * Retrieves the <tt>CycSymbol</tt> with <tt>symbolName</tt>,
     * returning null if not found in the cache.
     *
     * @return a <tt>CycSymbol</tt> if found in the cache, otherwise <tt>null</tt>
     */
    public static CycSymbol getCycSymbolCache(String symbolName) {
        return (CycSymbol) cycSymbolCache.getElement(symbolName);
    }

    /**
     * Removes the <tt>CycSymbol</tt> from the cache if it is contained within.
     */
    public static void removeCycSymbolCache(CycSymbol cycSymbol) {
        Object element = cycSymbolCache.getElement(cycSymbol.symbolName);
        if (element != null)
            cycSymbolCache.addElement(cycSymbol.symbolName, null);
    }

    /**
     * Returns the size of the <tt>Guid</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycSymbol</tt> objects in the cache
     */
    public static int getCycSymbolCacheSize() {
        return cycSymbolCache.size();
    }
    /**
     * Resets the Cyc constant caches.
     */
    public static void resetCycConstantCaches() {
        cycConstantCacheById = new CacheLRU(500);
        cycConstantCacheByName = new CacheLRU(500);
        cycConstantCacheByGuid = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycConstant<tt> to the cyc contstant cache by id.
     */
    public static void addCycConstantCacheById(CycConstant cycConstant) {
        if (((CycFort) cycConstant).getId() == null)
            throw new RuntimeException("Invalid constant for caching " + cycConstant);
        cycConstantCacheById.addElement(cycConstant.getId(), cycConstant);
    }

    /**
     * Adds the <tt>CycConstant<tt> to the cache by name.
     */
    public static void addCycConstantCacheByName(CycConstant cycConstant) {
        if (cycConstant.name == null)
            throw new RuntimeException("Invalid constant for caching " + cycConstant);
        cycConstantCacheByName.addElement(cycConstant.getName(), cycConstant);
    }

    /**
     * Adds the <tt>CycConstant<tt> to the cache by guid.
     */
    public static void addCycConstantCacheByGuid(CycConstant cycConstant) {
        if (cycConstant.guid == null)
            throw new RuntimeException("Invalid constant for caching " + cycConstant);
        cycConstantCacheByGuid.addElement(cycConstant.getGuid(), cycConstant);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with id, returning null if not found in the cache.
     */
    public static CycConstant getCycConstantCacheById(Integer id) {
        return (CycConstant) cycConstantCacheById.getElement(id);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with name, returning null if not found in the cache.
     */
    public static CycConstant getCycConstantCacheByName(String name) {
        return (CycConstant) cycConstantCacheByName.getElement(name);
    }

    /**
     * Retrieves the <tt>CycConstant<tt> with guid, returning null if not found in the cache.
     */
    public static CycConstant getCycConstantCacheByGuid(Guid guid) {
        return (CycConstant) cycConstantCacheByGuid.getElement(guid);
    }

    /**
     * Removes the <tt>CycConstant</tt> from the caches if it is contained within.
     */
    public static void removeCaches(CycConstant cycConstant) {
        if (cycConstant.name != null) {
            Object element = cycConstantCacheByName.getElement(cycConstant.name);
            if (element != null)
                cycConstantCacheByName.addElement(cycConstant.name, null);
        }
        if (((CycFort) cycConstant).getId() != null) {
            Object element = cycConstantCacheById.getElement(cycConstant.getId());
            if (element != null)
                cycConstantCacheById.addElement(cycConstant.getId(), null);
        }
        if (cycConstant.guid != null) {
            Object element = cycConstantCacheByGuid.getElement(cycConstant.guid);
            if (element != null)
                cycConstantCacheByGuid.addElement(cycConstant.guid, null);
        }
    }

    /**
     * Returns the size of the <tt>CycConstant</tt> object cache by id.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycConstant</tt> objects in the cache by id
     */
    public static int getCycConstantCacheByIdSize() {
        return cycConstantCacheById.size();
    }

    /**
     * Returns the size of the <tt>CycConstant</tt> object cache by id.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycConstant</tt> objects in the cache by id
     */
    public static int getCycConstantCacheByNameSize() {
        return cycConstantCacheByName.size();
    }

    /**
     * Resets the <tt>CycNart</tt> cache.
     */
    public static void resetCycNartCache() {
        cycNartCache = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycNart</tt> to the cache.
     */
    public static void addCycNartCache(CycNart cycNart) {
        cycNartCache.addElement(cycNart.getId(), cycNart);
    }

    /**
     * Retrieves the <tt>CycNart</tt> with name, returning null if not found in the cache.
     */
    public static CycNart getCycNartCache(Integer id) {
        return (CycNart) cycNartCache.getElement(id);
    }

    /**
     * Removes the <tt>CycNart</tt> from the cache if it is contained within.
     */
    public static void removeCycNartCache(CycNart cycNart) {
        Object element = cycNartCache.getElement(cycNart.getId());
        if (element != null)
            cycNartCache.addElement(cycNart.getId(), null);
    }

    /**
     * Returns the size of the <tt>CycNart</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycNart</tt> objects in the cache
     */
    public static int getCycNartCacheSize() {
        return cycNartCache.size();
    }
    /**
     * Resets the Cyc assertion cache.
     */
    public static void resetAssertionCache() {
        assertionCache = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycAssertion</tt> to the cache.
     */
    public static void addAssertionCache(CycAssertion cycAssertion) {
        assertionCache.addElement(cycAssertion.id, cycAssertion);
    }

    /**
     * Retrieves the <tt>CycAssertion</tt> with id, returning null if not found in the cache.
     */
    public static CycAssertion getAssertionCache(Integer id) {
        return (CycAssertion) assertionCache.getElement(id);
    }

    /**
     * Removes the CycAssertion from the cache if it is contained within.
     */
    public static void removeAssertionCache(Integer id) {
        Object element = assertionCache.getElement(id);
        if (element != null)
            assertionCache.addElement(id, null);
    }

    /**
     * Returns the size of the <tt>CycAssertion</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycAssertion</tt> objects in the cache
     */
    public static int getAssertionCacheSize() {
        return assertionCache.size();
    }

    /**
     * Constructs a new <tt>CycVariable</tt> object using the variable name.
     *
     * @param name a <tt>String</tt> name.
     */
    public static CycVariable makeCycVariable(String name) {
        if (name.startsWith("?"))
            name = name.substring(1);
        CycVariable cycVariable = (CycVariable) cycVariableCache.getElement(name);
        if (cycVariable == null) {
            cycVariable = new CycVariable(name);
            cycVariableCache.addElement(name, cycVariable);
        }
        return cycVariable;
    }

    /**
     * Constructs a new <tt>CycVariable</tt> object by suffixing the given variable.
     *
     * @param modelCycVariable a <tt>CycVariable</tt> to suffix
     */
    public static CycVariable makeUniqueCycVariable(CycVariable modelCycVariable) {
        String name = modelCycVariable.name + "_" + suffix++;
        CycVariable cycVariable = (CycVariable) cycVariableCache.getElement(name);
        if (cycVariable == null) {
            cycVariable = new CycVariable(name);
            cycVariableCache.addElement(name, cycVariable);
        }
        return cycVariable;
    }

    /**
     * Resets the <tt>CycVariable</tt> cache.
     */
    public static void resetCycVariableCache() {
        cycVariableCache = new CacheLRU(500);
    }

    /**
     * Adds the <tt>CycVariable<tt> to the cache.
     */
    public static void addCycVariableCache(CycVariable cycVariable) {
        if (cycVariable.name == null)
            throw new RuntimeException("Invalid variable for caching " + cycVariable);
        cycVariableCache.addElement(cycVariable.name, cycVariable);
    }

    /**
     * Retrieves the <tt>CycVariable</tt> with <tt>name</tt>,
     * returning null if not found in the cache.
     *
     * @return a <tt>CycVariable</tt> if found in the cache, otherwise
     * <tt>null</tt>
     */
    public static CycVariable getCycVariableCache(String name) {
        return (CycVariable) cycVariableCache.getElement(name);
    }

    /**
     * Removes the <tt>CycVariable</tt> from the cache if it is contained within.
     */
    public static void removeCycVariableCache(CycVariable cycVariable) {
        Object element = cycVariableCache.getElement(cycVariable.name);
        if (element != null)
            cycVariableCache.addElement(cycVariable.name, null);
    }

    /**
     * Returns the size of the <tt>CycVariable</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>CycVariable</tt> objects in the cache
     */
    public static int getCycVariableCacheSize() {
        return cycVariableCache.size();
    }

    /**
     * Returns a cached <tt>Guid</tt> object or construct a new
     * Guid object from a guid string if the guid is not found in the cache.
     *
     * @param guid a <tt>String</tt> form of a GUID.
     */
    public static Guid makeGuid(String guidString) {
        Guid guid = (Guid) guidCache.getElement(guidString);
        if (guid == null ) {
            guid = new Guid(guidString);
            guidCache.addElement(guidString, guid);
        }
        return guid;
    }

    /**
     * Adds the <tt>Guid</tt> to the cache.
     */
    public static void addGuidCache(Guid guid) {
        guidCache.addElement(guid.guidString, guid);
    }

    /**
     * Resets the <tt>Guid</tt> cache.
     */
    public static void resetGuidCache() {
        guidCache = new CacheLRU(500);
    }

    /**
     * Retrieves the <tt>Guid</tt> with <tt>guidName</tt>,
     * returning null if not found in the cache.
     *
     * @return the <tt>Guid</tt> if it is found in the cache, otherwise
     * <tt>null</tt>
     */
    public static Guid getGuidCache(String guidName) {
        return (Guid) guidCache.getElement(guidName);
    }

    /**
     * Removes the <tt>Guid</tt> from the cache if it is contained within.
     */
    public static void removeGuidCache(Guid guid) {
        Object element = guidCache.getElement(guid.guidString);
        if (element != null)
            guidCache.addElement(guid.guidString, null);
    }

    /**
     * Returns the size of the <tt>Guid</tt> object cache.
     *
     * @return an <tt>int</tt> indicating the number of <tt>Guid</tt> objects in the cache
     */
    public static int getGuidCacheSize() {
        return guidCache.size();
    }

    /**
     * Unmarshalls a cyc object from an XML representation.
     *
     * @param xmlString the XML representation of the cyc object
     * @return the cyc object
     */
    public static Object unmarshall (String xmlString) throws JDOMException, IOException {
        Object object = null;
        SAXBuilder saxBuilder = new SAXBuilder(false);
        Document document = saxBuilder.build(new StringReader(xmlString));
        Element root = document.getRootElement();
        return unmarshallElement(root, document);
    }

    /**
     * Unmarshalls a cyc object from the given element in an XML Document object.
     *
     * @param element the element representing the cyc object
     * @param document the XML document containing the element
     * @return the cyc object
     */
    protected static Object unmarshallElement(Element element, Document document) throws IOException {
        String elementName = element.getName();
        if (elementName.equals("guid"))
            return unmarshallGuid(element);
        else if (elementName.equals("symbol"))
            return unmarshallCycSymbol(element);
        else if (elementName.equals("variable"))
            return unmarshallCycVariable(element);
        else if (elementName.equals("constant"))
            return unmarshallCycConstant(element, document);
        else if (elementName.equals("nat"))
            return unmarshallCycNart(element, document);
        else if (elementName.equals("list"))
            return unmarshallCycList(element, document);
        else if (elementName.equals("string"))
            return TextUtil.undoEntityReference(element.getText());
        else if (elementName.equals("integer"))
            return new Integer(element.getTextTrim());
        else if (elementName.equals("double"))
            return new Double(element.getTextTrim());
        else if (elementName.equals("byte-vector"))
            return unmarshallByteArray(element, document);
        else if (elementName.equals("assertion"))
            return unmarshallCycAssertion(element);
        else
            throw new IOException("Invalid element name " + elementName);
    }

    /**
     * Unmarshalls a Guid from the given element in an XML Document object.
     *
     * @param guidElement the guid xml element
     * @return the guid or cached reference to an existing guid object
     */
    protected static Guid unmarshallGuid (Element guidElement) {
        String guidString = guidElement.getTextTrim();
        Guid guid = getGuidCache(guidString);
        if (guid != null)
            return guid;
        return makeGuid(guidString);
    }

    /**
     * Unmarshalls a CycSymbol from the given element in an XML Document object.
     *
     * @param cycSymbolElement the CycSymbol xml element
     * @return the CycSymbol or cached reference to an existing CycSymbol object
     */
    protected static CycSymbol unmarshallCycSymbol (Element cycSymbolElement) {
        String symbolName = TextUtil.undoEntityReference(cycSymbolElement.getTextTrim());
        CycSymbol cycSymbol = getCycSymbolCache(symbolName);
        if (cycSymbol != null)
            return cycSymbol;
        return makeCycSymbol(symbolName);
    }

    /**
     * Unmarshalls a CycAssertion from the given element in an XML Document object.
     *
     * @param cycAssertionElement the CycAssertion xml element
     * @return the CycAssertion object
     */
    protected static CycAssertion unmarshallCycAssertion (Element cycAssertionElement) {
        Integer id = null;
        Element idElement = cycAssertionElement.getChild("id");
        if (idElement != null) {
            id = new Integer(idElement.getTextTrim());
        }
        return new CycAssertion(id);
    }

    /**
     * Unmarshalls a CycVariable from the given element in an XML Document object.
     *
     * @param cycVariableElement the CycVariable xml element
     * @return the CycVariable or cached reference to an existing CycVariable object
     */
    protected static CycVariable unmarshallCycVariable (Element cycVariableElement) {
        String name = TextUtil.undoEntityReference(cycVariableElement.getTextTrim());
        CycVariable cycVariable = getCycVariableCache(name);
        if (cycVariable != null)
            return cycVariable;
        return makeCycVariable(name);
    }


    /**
     * Unmarshalls a CycConstant from the given element in an XML Document object.
     *
     * @param cycConstantElement the element representing the CycConstant
     * @param document the XML document containing the element
     * @return the CycConstant
     */
    protected static CycConstant unmarshallCycConstant(Element cycConstantElement, Document document) {
        CycConstant cycConstant = null;
        Guid guid = null;
        Element guidElement = cycConstantElement.getChild("guid");
        if (guidElement != null) {
            guid = makeGuid(guidElement.getTextTrim());
            cycConstant = getCycConstantCacheByGuid(guid);
            if (cycConstant != null)
                return cycConstant;
        }
        String name = null;
        Element nameElement = cycConstantElement.getChild("name");
        if (nameElement != null) {
            name = TextUtil.undoEntityReference(nameElement.getTextTrim());
            cycConstant = getCycConstantCacheByName(name);
            if (cycConstant != null)
                return cycConstant;
        }
        Integer id = null;
        Element idElement = cycConstantElement.getChild("id");
        if (idElement != null) {
            id = new Integer(idElement.getTextTrim());
            cycConstant = getCycConstantCacheById(id);
            if (cycConstant != null)
                return cycConstant;
        }

        cycConstant = new CycConstant(name, guid, id);
        if (guid != null)
            addCycConstantCacheByGuid(cycConstant);
        if (id != null)
            addCycConstantCacheById(cycConstant);
        if (name != null)
            addCycConstantCacheByName(cycConstant);
        return cycConstant;
    }

    /**
     * Unmarshalls a CycNart from the given element in an XML Document object.
     *
     * @param cycNartElement the element representing the CycNart
     * @param document the XML document containing the element
     * @return the CycNart
     */
    protected static CycNart unmarshallCycNart(Element cycNartElement, Document document)
        throws IOException {
        Integer id = null;
        Element idElement = cycNartElement.getChild("id");
        if (idElement != null)
            id = new Integer(idElement.getTextTrim());
        CycFort functor = null;
        Element functorElement = cycNartElement.getChild("functor");
        if (functorElement != null) {
            Element cycConstantFunctorElement = functorElement.getChild("constant");
            Element cycNartFunctorElement = functorElement.getChild("nat");
            if (cycConstantFunctorElement != null) {
                if (cycNartFunctorElement != null)
                    throw new IOException("Invalid CycNart functor" + functorElement);
                functor = unmarshallCycConstant(cycConstantFunctorElement, document);
            }
            else if (cycNartFunctorElement != null)
                functor = unmarshallCycNart(cycNartFunctorElement, document);
            else
                throw new IOException("Missing functor constant/nart from CycNart " + cycNartElement);
        }
        List argElements = cycNartElement.getChildren("arg");
        CycList arguments = new CycList();
        for (int i = 0; i < argElements.size(); i++) {
            Element argElement = (Element) argElements.get(i);
            arguments.add(unmarshallElement((Element) argElement.getChildren().get(0), document));
        }
        CycNart cycNart = new CycNart(functor, arguments);
        cycNart.setId(id);
        return cycNart;
    }

    /**
     * Unmarshalls a CycList from the given element in an XML Document object.
     *
     * @param cycListElement the element representing the CycList
     * @param document the XML document containing the element
     * @return the CycList
     */
    protected static CycList unmarshallCycList(Element cycListElement, Document document)
        throws IOException {
        List elements = cycListElement.getChildren();
        CycList cycList = new CycList();
        for (int i = 0; i < elements.size(); i++) {
            Element element = (Element) elements.get(i);
            if (element.getName().equals("dotted-element"))
                cycList.setDottedElement(unmarshallElement((Element) element.getChildren().get(0), document));
            else
                cycList.add(unmarshallElement(element, document));
        }
        return cycList;
    }

    /**
     * Unmarshalls a ByteArray from the given element in an XML Document object.
     *
     * @param byteArrayElement the element representing the CycList
     * @param document the XML document containing the element
     * @return the ByteArray
     */
    protected static ByteArray unmarshallByteArray(Element byteArrayElement, Document document)
        throws IOException {
        List elements = byteArrayElement.getChildren();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element element = (Element) elements.get(i);
            if (element.getName().equals("byte"))
                arrayList.add(new Byte(element.getTextTrim()));
        }
        byte[] bytes = new byte[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++)
            bytes[i] = ((Byte) arrayList.get(i)).byteValue();
        return new ByteArray(bytes);
    }



}