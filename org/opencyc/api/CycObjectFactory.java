package org.opencyc.api;

import java.util.*;
import java.io.*;
import org.apache.oro.util.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;

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
    public static CycSymbol cons = makeCycSymbol("CONS");
    public static CycSymbol dot = makeCycSymbol(".");

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
     * Constructs a CycList from the given xml databinding object.
     *
     * @pararm cycListXmlDataBindingImpl the xml databinding object
     */
    public static CycList makeCycList (CycListXmlDataBindingImpl cycListXmlDataBindingImpl) {
        CycList cycList = new CycList();
        for (int i = 0; i < cycListXmlDataBindingImpl.getElementList().size(); i++) {
            Object element = cycListXmlDataBindingImpl.getElementList().get(i);
            if (element instanceof CycConstantXmlDataBindingImpl)
                cycList.add(makeCycConstant((CycConstantXmlDataBindingImpl) element));
            else if (element instanceof CycNartXmlDataBindingImpl)
                cycList.add(makeCycNart((CycNartXmlDataBindingImpl) element));
            else if (element instanceof CycSymbolXmlDataBindingImpl)
                cycList.add(makeCycSymbol((CycSymbolXmlDataBindingImpl) element));
            else if (element instanceof CycVariableXmlDataBindingImpl)
                cycList.add(makeCycConstant((CycConstantXmlDataBindingImpl) element));
            else if (element instanceof GuidXmlDataBindingImpl)
                cycList.add(makeGuid((GuidXmlDataBindingImpl) element));
            else
                cycList.add(element);
        }
        if (cycListXmlDataBindingImpl.getDottedElement() == null)
            return cycList;
        if (cycListXmlDataBindingImpl.getDottedElement() instanceof CycConstantXmlDataBindingImpl)
            cycList.setDottedElement(makeCycConstant((CycConstantXmlDataBindingImpl) cycListXmlDataBindingImpl.getDottedElement()));
        else if (cycListXmlDataBindingImpl.getDottedElement() instanceof CycNartXmlDataBindingImpl)
            cycList.setDottedElement(makeCycNart((CycNartXmlDataBindingImpl) cycListXmlDataBindingImpl.getDottedElement()));
        else if (cycListXmlDataBindingImpl.getDottedElement() instanceof CycSymbolXmlDataBindingImpl)
            cycList.setDottedElement(makeCycSymbol((CycSymbolXmlDataBindingImpl) cycListXmlDataBindingImpl.getDottedElement()));
        else if (cycListXmlDataBindingImpl.getDottedElement() instanceof CycVariableXmlDataBindingImpl)
            cycList.setDottedElement(makeCycVariable((CycVariableXmlDataBindingImpl) cycListXmlDataBindingImpl.getDottedElement()));
        else if (cycListXmlDataBindingImpl.getDottedElement() instanceof GuidXmlDataBindingImpl)
            cycList.setDottedElement(makeGuid((GuidXmlDataBindingImpl) cycListXmlDataBindingImpl.getDottedElement()));
        else
            cycList.setDottedElement(cycListXmlDataBindingImpl.getDottedElement());
        return cycList;
    }

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
     * Constructs a CycSymbol from the given xml databinding object.
     *
     * @pararm cycSymbolXmlDataBindingImpl the xml databinding object
     */
    public static CycSymbol makeCycSymbol (CycSymbolXmlDataBindingImpl cycSymbolXmlDataBindingImpl) {
        CycSymbol cycSymbol = getCycSymbolCache(cycSymbolXmlDataBindingImpl.getSymbolName());
        if (cycSymbol != null)
            return cycSymbol;
        cycSymbol = new CycSymbol(cycSymbolXmlDataBindingImpl.getSymbolName());
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
     * Constructs a CycConstant from the given xml databinding object.
     *
     * @pararm cycConstantXmlDataBindingImpl the xml databinding object
     */
    public static CycConstant makeCycConstant (CycConstantXmlDataBindingImpl cycConstantXmlDataBindingImpl) {
        Guid guid = makeGuid(cycConstantXmlDataBindingImpl.getGuidXmlDataBindingImpl().getGuidString());
        CycConstant cycConstant =
            getCycConstantCacheByGuid(guid);
        if (cycConstant != null)
            return cycConstant;
        cycConstant = new CycConstant(cycConstantXmlDataBindingImpl.getName(),
                                      guid,
                                      cycConstantXmlDataBindingImpl.getId());
        addCycConstantCacheByName(cycConstant);
        addCycConstantCacheByGuid(cycConstant);
        addCycConstantCacheById(cycConstant);
        return cycConstant;
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
     * Constructs a CycNart from the given xml databinding object.
     *
     * @pararm cycNartXmlDataBindingImpl the xml databinding object
     */
    public static CycNart makeCycNart (CycNartXmlDataBindingImpl cycNartXmlDataBindingImpl) {
        CycNart cycNart = getCycNartCache(cycNartXmlDataBindingImpl.getId());
        if (cycNart != null)
            return cycNart;
        cycNart = new CycNart();
        cycNart.setId(cycNartXmlDataBindingImpl.getId());
        if (cycNartXmlDataBindingImpl.getFunctor() instanceof CycConstantXmlDataBindingImpl)
            cycNart.setFunctor(makeCycConstant((CycConstantXmlDataBindingImpl)cycNartXmlDataBindingImpl.getFunctor()));
        else
             cycNart.setFunctor(makeCycNart((CycNartXmlDataBindingImpl) cycNartXmlDataBindingImpl.getFunctor()));
        cycNart.setArguments(makeCycList(cycNartXmlDataBindingImpl.getArgumentList()));
        return cycNart;
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
     * Constructs a CycVariable from the given xml databinding object.
     *
     * @pararm cycVariableXmlDataBindingImpl the xml databinding object
     */
    public static CycVariable makeCycVariable (CycVariableXmlDataBindingImpl cycVariableXmlDataBindingImpl) {
        CycVariable cycVariable = getCycVariableCache(cycVariableXmlDataBindingImpl.getName());
        if (cycVariable != null)
            return cycVariable;
        cycVariable = new CycVariable();
        cycVariable.id = cycVariableXmlDataBindingImpl.getId();
        cycVariable.name = cycVariableXmlDataBindingImpl.getName();
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
     * Constructs a Guid from the given xml databinding object.
     *
     * @pararm GuidXmlDataBindingImpl the xml databinding object
     */
    public static Guid makeGuid (GuidXmlDataBindingImpl guidXmlDataBindingImpl) {
        Guid guid =
            makeGuid(guidXmlDataBindingImpl.getGuidString());
        if (guid != null)
            return guid;
        guid = new Guid(guidXmlDataBindingImpl.getGuidString());
        addGuidCache(guid);
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

}