package org.opencyc.javashell;
/**
 * Description TODO
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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.cyclobject.*;
import org.opencyc.cycagent.*;
import ViolinStrings.Strings;


public class CycJavaShell  {

    /* once started = true  */
    public  boolean m_isInitialized = false;
    /* Introspections version of a String.getClass()  */
    public  Class stringClass;
    /* Introspections version of the String Class in a 1-D array containing a Class[0]=String.getClass()  */
    public  Class[] stringClassArrayOfOne;
    /* Debug=0 (None), Debug=1 (Minor), Debug=2 (Extreme)  */
    public  int debug=2;
    /* Dictionary of All objects available to Scripting engine  */
    public  Hashtable allObjects = null;

    public CycAccess cycAccess = null;

    public  CycSymbol CYC_NIL;
    public  CycSymbol CYC_TRUE;
    public  CycConstant cycCreateObject = null;
    public  CycConstant cycCreateObjectNamed = null;
    public  CycConstant cycDestroyObject = null;
    public  CycConstant cycForgetObject = null;
    public  CycConstant cycInvokeObject = null;
    public  CycConstant cycSetObjectField = null;
    public  CycConstant cycGetObjectField = null;
    

    public    CycConstant javaMt = null;
    public    CycConstant cycadministrator = null;
    public    CycConstant opencycproject = null;
    public    CycConstant functionalRelation = null;
    public    CycConstant cycVoid = null;
    public    CycConstant cycNull = null;
    public    CycConstant cycHasArrayMember = null;
    public    CycConstant cycHasMethod = null;
    public    CycConstant cycHasSlot = null;
    public    CycConstant cycClassInstance = null;
    public    CycConstant cycHasSlotValue = null;
    public    CycConstant geographicalRegion = null;
    public    CycConstant cycArrayOfClass = null;


    // CycConstant & Class -> CycConstant key of Fields | DataMethod | Method
    public    HashMap thisClassTemplates = new HashMap();

    // CycConstant || Class -> Class || CycConstant 
    public    HashMap cycKnowsClass = new HashMap();
    public    HashMap cycKnowsObjectAsConstant = new HashMap();
    //public    HashMap cycKnowsMicrotheory = new HashMap();

    /* Creates a non-started Server */
    public CycJavaShell() {
        if( !m_isInitialized )
            createState();
    }

    /* Ensures Scripting Engine is running*/
    public synchronized  void createState() {
        if( allObjects!=null ) return;
        stringClass = (new String()).getClass();

        try {
            stringClassArrayOfOne = (Class[])(Array.newInstance(Class.forName("java.lang.Class"),1));
        } catch( Exception e ) {
            System.out.println("invokeConstructor: " + e);
            e.printStackTrace(System.out);
        }

        Array.set(stringClassArrayOfOne,0,stringClass);
        debug=5;
        allObjects = new Hashtable();
        allObjects.put("CycJavaShell",new CycJavaShell());                
        allObjects.put("System",System.class);                
        m_isInitialized = true;
    }

    public void ensureClientSupportsShell(CycAccess cycA) {
	cycAccess = cycA;
	if(cycCreateObject==null) {
	    cycCreateObject = cycAccess.makeCycConstant("javaCreate");
	    cycCreateObjectNamed = cycAccess.makeCycConstant("javaCreateNamed");
	    cycDestroyObject = cycAccess.makeCycConstant("javaDestroy");
	    cycForgetObject = cycAccess.makeCycConstant("javaForget");
	    cycInvokeObject = cycAccess.makeCycConstant("javaInvoke");
	    cycSetObjectField = cycAccess.makeCycConstant("javaSetField");
	    cycGetObjectField = cycAccess.makeCycConstant("javaGetField");
	    CYC_NIL = new CycSymbol("NIL");
	    CYC_TRUE = new CycSymbol("T");
	    cycVoid =  makeCycConstantSafe("#$voidValue");
	    cycNull =  makeCycConstantSafe("#$nullValue");
	    cycHasArrayMember =  makeCycConstantSafe("#$javaArrayContains");
	    cycHasMethod =  makeCycConstantSafe("#$hasMethod");
	    cycHasSlot =  makeCycConstantSafe("#$relationAllExists");
	    cycClassInstance =  makeCycConstantSafe("#$ClassInstance");
	    cycHasSlotValue =  makeCycConstantSafe("#$hasSlotValue");
	    javaMt =  makeCycConstantSafe("#$JavaMt");
	    CycFort reifiedMicrotheory =  makeCycConstantSafe("#$ReifiedMicrotheory");
	    cycArrayOfClass =  makeCycConstantSafe("#$SetOfTypeFn");
	    makeCycConstantError =  makeCycConstantSafe("#$MakeCycConstantErrorFn");
	    assertIsaSafe(javaMt,     cycAccess.microtheory,        cycAccess.baseKB);

	}
    }



    /* Scripting Engine is running = true*/
    public synchronized boolean isStarted() {
        return m_isInitialized;
    }

    public  Object invoke(CycList query) throws Exception {

        CycFort pred = (CycFort)query.first();
        if( pred.equals(cycInvokeObject) )
            return invokeObject(query.second(),query.third(),(CycList)query.fourth());

        else if( pred.equals(cycCreateObject) )
            return createObject(query.second());

        else if( pred.equals(cycCreateObjectNamed) )
            return createObjectNamed(query.second(),query.third());

        else if( pred.equals(cycForgetObject) )
            return forgetObject((CycFort)query.second());

        else if( pred.equals(cycDestroyObject) )
            return destroyObject((CycFort)query.second());

        else if( pred.equals(cycSetObjectField) )
            return setObjectField(query.second(),query.third());

        else if( pred.equals(cycGetObjectField) )
            return getObjectField(query.second());

        return CYC_NIL;
    }

    /* OpenCyc Will call these */

    public  CycSymbol createObject(Object classnameObj) throws Exception {
        Object innerInstance = initObject(classnameObj);
        String constname = "Inst-" + innerClass.getName() + "-" + innerInstance.hashCode();
        CycConstant cycobj =  cycAccess.makeCycConstant(constname);
        allObjects.put(cycobj,innerInstance);
        allObjects.put(innerInstance,cycobj);
        return CYC_TRUE;
    }           

    public  CycSymbol createObjectNamed(Object classnameObj,Object constname) throws Exception {
        Object innerInstance = initObject(classnameObj);
        allObjects.put(cycobj,innerInstance);
        allObjects.put(innerInstance,cycobj);
        return CYC_TRUE;
    }           

    public  Object initObject(Object classnameObj) throws Exception {
	String classname = ""+classnameObj;
        /* Creates a new Object for a className */
        Class innerClass = Class.forName(classname);
        Object innerInstance = innerClass.newInstance();
        return innerInstance;
    }           

    public  CycSymbol forgetObject(Object cycobj) throws Exception {
        Object innerInstance = selectObjectForName(cycobj);
        if( innerInstance==null ) return CYC_TRUE;
        allObjects.remove(innerInstance);
        allObjects.remove(cycobj);
        return CYC_TRUE;
    }
    
    public  CycSymbol destroyObject(Object cycobj) throws Exception {
        Object innerInstance = selectObjectForName(cycobj);
	if( innerInstance==null ) return CYC_NIL;
        allObjects.remove(innerInstance);
        allObjects.remove(cycobj);
        if( cycobj instanceof CycConstant ) cycAccess.kill((CycConstant)cycobj);
        return CYC_TRUE;
    }

    public synchronized  CycSymbol setObjectField(CycAccess cycAccess,Object cycobj, Object fieldref, Object value) throws Exception {
	Object innerInstance = selectObjectForName(cycobj);
	if( innerInstance==null ) throw new Exception("Object not found in catalog \"" + cycobj +"\"");
	Field field = objectField(innerInstance,fieldref);
	field.set(innerInstance, matchBestClass(field.getType(),value));
	return CYC_TRUE;
    }
    
    public synchronized  CycSymbol getObjectField(CycAccess cycAccess,Object cycobj, Object fieldref) throws Exception {
	Object innerInstance = selectObjectForName(cycobj);
	if( innerInstance==null ) throw new Exception("Object not found in catalog \"" + cycobj +"\"");
	Field field = objectField(innerInstance,fieldref);
	return getObjNameCyc(field.get(innerInstance));
    }

    public  Field objectField(CycAccess cycAccess,Object innerInstance, Object fieldref) throws Exception {
	return innerInstance.getClass().getField(fieldref);
    }


    public  CycList getObjects(CycAccess cycAccess)  {
	return new CycList(allObjects.keys());
    }


    public synchronized  CycList invokeObject(CycAccess cycAccess,Object cycobj, Object methodName, CycList params) throws Exception {
        Object innerInstance = selectObjectForName(cycobj);
        if( innerInstance==null ) throw new Exception("Object not found in catalog \"" + cycobj +"\"");
        Class innerClass = innerInstance.getClass();
        Method[] meth = innerClass.getMethods();
        int len = params.size();
        for( int i=0;i<meth.length;i++ ) {
            if( meth[i].getName().equals(methodName) ) {
                Class[] pt = meth[i].getParameterTypes();
                if( pt.length==len ) {
                    Object[] args = argsToObjectVector(len,cycAccess,pt,params);
                    if( args!=null ) {
                        try {
                            return getObjNameCyc(meth[i].invoke(innerInstance,args));
                        } catch( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        throw new Exception("Method ("+methodName+") not found in " + cycobj);
    }

    public  void selectObjectForName(Object name) {
	Object innerInstance = allObjects.get(name);
	if(innerInstance!=null) return innerInstance;
	if(!(name instanceof CycObject)) return name;
	return name;    
    }

    public synchronized  Object[] argsToObjectVector(int len,CycAccess cycAccess,Class[] pt, CycList params) {
        Object[] args = new Object[len+1];
        for( int i=0; i<len; i++ ) {
                arg[i] = matchBestClass(pt[i],params.get(i));
        }
        return arg;
    }


	          public  Object matchBestClass(CycAccess cycAccess,Class oclass, Object o) {
        try {
            Class[] coclass = oclass.getClasses(); 
            for( int i=0, l=coclass.length; i<l; i++ ) {
                if( coclass==CycObject.class ) return o;
            }
            ////if( oclass==CycObject.class || oclass==Object.class || oclass==CycList.class || oclass==CycFort.class || oclass==CycConstant.class || oclass==CycNart.class) return o;
            Object derefed = allObjects.get(o);
            if( derefed!=null ) if( oclass.isInstance(derefed) ) return derefed;
            if( o!=null ) if( oclass.isInstance(o) ) return o;
            if( o instanceof CycObject ) return matchBestClass(oclass,o.toString());

            if( oclass.isArray() ) {
                if( o instanceof ArrayList ) {
                    int len = ((ArrayList)o).size();
                    Object[] arg = Array.newInstance(oclass,len);
                    for( int i=0;i<len; i++ ) 
                        arg[i] = matchBestClass(oclass,((ArrayList)o).get(0));
                    return arg;
                }  else return null;
            }

            //From here we have 'null' or a non CycObject

            if( oclass.isPrimitive() ) {
                if( oclass==int.class ) {
                    if( o==null ) return 0;
                    return new Integer.parseInt(o.toString());
                }
                if( oclass==float.class ) {
                    if( o==null ) return 0F;
                    return new Float.parseFloat(o.toString());
                }
                if( oclass==byte.class ) {
                    if( o==null ) return new Byte(0);
                    if( o instanceof String ) if( ((String)o).length()==1 ) o=((String)o).charAt(0);
		    int r = (o instanceof Number) ? ((Number)o).intValue() :(o instanceof Boolean) ? (((Boolean)o).booleanValue()?1:0) : Integer.parseInt(o.toString());
                    return new Byte(""+r).byteValue();
                }
                if( oclass==char.class ) {
                    if( o==null ) return new Character(0);
                    if( o instanceof String ) if( ((String)o).length()==1 ) return((String)o).charAt(0);
                    int r = (o instanceof Number) ? ((Number)o).intValue() :(o instanceof Boolean) ? (((Boolean)o).booleanValue()?1:0) : Integer.parseInt(o.toString());
                    return new Character(r).charValue();
                }
                if( oclass==boolean.class ) {
                    return toBool(o);
                }
            }


            if( oclass==String.class ) {
                if( o==null ) return "";
                if( o instanceof String ) return o;
                else return null;
            }

            if( oclass == Integer ) return new java.lang.Integer(o.toString());
            if( oclass == Float ) return new java.lang.Float(o.toString());
            if( oclass == Boolean)) return new Boolean(toBool(o));
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /***********************************************************
     *  Java To Cyc
     *
     **********************************************************/
    public  Object getObjNameCyc(CycAccess cycAccess,Object object) {
        return makeCycJavaObject( cycAccess, javaMt, object, false);
    }

    public  Object makeCycJavaObject(CycFort dataMt,Object object) {
        return makeCycJavaObject( dataMt, object, true);
    }
    public  Object makeCycJavaObject(CycFort dataMt,Object object, boolean assertobj) {
        return makeCycJavaObject( dataMt, object, true);
    }

    public  Object makeCycJavaObject(CycFort dataMt,Object object, boolean assertobj) {
        if( object==null ) return null;
        if( cycKnowsObjectAsConstant.containsKey(object) ) return cycKnowsObjectAsConstant.get(object);
        if( object instanceof CycFort ) return object;
        if( object instanceof CycList ) return object;
        if( object instanceof String ) return Strings.change((String)object,"\"","\\\"");
        if( object instanceof Boolean )  if( object.equals(Boolean.TRUE) ) return cycTrue;
            else return cycFalse;
        if( object instanceof Character ) return  new String("`" + object);
        if( object instanceof Integer ) return object;
        if( object instanceof Long ) return object;
        if( object instanceof Double ) return object;
        if( object instanceof Float ) return object;
        if( object instanceof Byte ) return(Integer)object;
        Class jclass = object.getClass();
        if( !(jclass.isArray()) ) {
            if( jclass.isPrimitive() ) {

                Log.current.println("\n\n PRIMITIVE!!!!\n\n PRIMITIVE!!!!\n\n PRIMITIVE!!!!");
                if( jclass == java.lang.Boolean.TYPE ) if( object.equals(Boolean.TRUE) ) return cycTrue;
                    else return cycFalse;
                if( jclass == java.lang.Integer.TYPE ) return new Integer(""+object);
                if( jclass == java.lang.Byte.TYPE ) return new Integer(""+object);
                if( jclass == java.lang.Void.TYPE ) return cycVoid;
                if( jclass == java.lang.Long.TYPE ) return new Long(""+object);
                if( jclass == java.lang.Character.TYPE ) return  new String("`" + object);
                if( jclass == java.lang.Double.TYPE ) return new Double(""+object);
                if( jclass == java.lang.Float.TYPE ) return new Float(""+object);
            }
        }

        String classname = jclass.getName();
        if( classname.startsWith("org.opencyc") ) return object;

        CycFort cycobject = null;

        cycobject =  makeCycConstantSafe("HYP-"+classname.substring(0,classname.length()-8)+object.hashCode());

        String stringKey =  ""+object.hashCode();
        if( cycKnowsObjectAsConstant.containsKey(stringKey) ) return cycKnowsObjectAsConstant.get(stringKey);

        cycKnowsObjectAsConstant.put(stringKey,cycobject);

        CycFort cycclass = makeCycClassInstance(jclass);

        try {

            assertIsaSafe((CycFort)cycobject,(CycFort)cycclass,dataMt);
        } catch( Exception e ) {
            e.printStackTrace(System.out);
        }

        // Only make this constant for object 
        if( assertobj ) assertObjectData(dataMt,object,cycobject);
        return cycobject;
    }


    /* Serializes Array into OpenCyc List */
    public synchronized  Object arrayToPLStructure(Object[] pMembs) {
        int len = pMembs.length;

        switch( len ) {
            case 0:
                return "[]";
            case 1:
                return "[" + objectRefToOpenCyc(pMembs[0]) + "]";
        }
        StringBuffer args = new StringBuffer("[" + objectRefToOpenCyc(pMembs[0]));

        for( int nMemb=1 ; nMemb < len; nMemb++ )
            args.append(",").append(objectRefToOpenCyc(pMembs[nMemb]));

        return args.append("]").toString();
    }


    /* Serializes Iterator into OpenCyc List */
    public synchronized  Object iteratorToPLStructure(Iterator pMembs) {
        if( !pMembs.hasNext() ) return "[]";
        StringBuffer args = new StringBuffer("[" + objectRefToOpenCyc(pMembs.next()));
        if( pMembs.hasNext() )
            args.append(",").append(objectRefToOpenCyc( pMembs.next()));

        return args.append("]").toString();
    }


    public synchronized  Object objectRefToOpenCyc(Object obj ) {
        if( obj==null ) return "null";
        if( obj instanceof Collection ) return  "collection(" + arrayToPLStructure(((Collection)obj).toArray() ) + ")";
        if( obj instanceof Iterator ) return  "iterator(" + iteratorToPLStructure(((Iterator)obj)) + ")";
        Class oClass = obj.getClass();
        if( oClass.isPrimitive() ) return obj.toString();
        if( oClass.isArray() ) return arrayToPLStructure((Object[])obj);

        addInstance(obj);

        try {
            if( oClass.getDeclaredMethod("toString",null)!=null ) return obj.toString();
        } catch( NoSuchMethodException e ) {
        }

        return "'$java_instance'(" +obj.hashCode() + "," +
        toScriptingName(oClass.getName()) +"('" + obj.toString() + "'))";

    }

    public synchronized  Class[] getClasses(Object[] objs) {
        return getClasses(objs, objs.length);
    }

    public synchronized  Class[] getClasses(Object[] objs, int len) {
        if( len==0 ) return null;

        Class[] toReturnClasses=null;
        try {
            toReturnClasses = (Class[])Array.newInstance(findClass("Class"),len);
        } catch( Exception e ) {
            fatalEvent(e);
        }
        for( int i = 0 ; i < len; i++ ) toReturnClasses[i]=objs[i].getClass();
        return toReturnClasses;
    }


    private  Method getMethodForObject(Object obj,String methodName,Class[] argClasses) 
    throws Exception {
        return classFromInstance(obj).getMethod(methodName,argClasses);
    }

    private  Field getFieldForObject(Object obj,String methodName) 
    throws Exception {
        return classFromInstance(obj).getField(methodName);
    }

    public synchronized  Class classFromInstance(Object obj) {
        if( obj instanceof Class )     return(Class)obj;
        return obj.getClass();
    }

    public synchronized  Object[] argsToObjectVector(Object[] args) {

        int len = args.length;
        System.err.println("argsToObjectVector=" + len);

        Object toReturnObjects[]=new Object[len];
        int source = 0;
        for( int i = 0 ; i < len ; i++ ) {
            toReturnObjects[i ]=argToObject( (Object) args[ i ]);
            if( debug >1 )
                System.err.println("Arg" + i + "=\"" + args[i] + "\" -> " + toReturnObjects[i].toString());
        }
        return toReturnObjects;
    }                     

    public synchronized  Object argToObject(Object arg) {
        if( arg instanceof String ) return stringToObj((String)arg);
        if( arg.getClass().isArray() ) return argsToObjectVector((Object[])arg);
        return arg;
    }

    public synchronized  Object stringToObj(String arg) {

        char fc = arg.charAt(0);
        switch( fc ) {
            case 'o':
                return findInstanceFromCyc(arg);
            case 'b':
                if( arg.charAt(1)=='t' ) return new Boolean(true);
                else return new Boolean(false);
            case 's':
                return arg.substring(1);
            case 'n':
                return null;
            case '$':
                return null;
            case 'i':
                try {
                    return new java.lang.Integer(arg.substring(1));
                } catch( Exception e ) {
                    warnEvent(e);
                    return new java.lang.Integer(0);
                }
            case 'l':
                try {
                    return new java.lang.Float(arg.substring(2));
                } catch( Exception e ) {
                    warnEvent(e);
                    return new java.lang.Float(0);
                }
            case 'u':
                return arg.substring(1);
            case 't':
                return arg.substring(1);
        }
        return arg;
    }



    public synchronized  Object mktype(String arg) {
        int comma = arg.indexOf(',') ;
        try {
            return makeInstanceFromClass(arg.substring(5,comma++),arg.substring(comma,arg.length()-1));
        } catch( Exception e ) {
            return makeError(e);
        }
    }

    public synchronized  Object mktype(String theType,String theData)
    throws Exception {
        if( theType.equals("Long") ) {
            try {
                return new java.lang.Long(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new java.lang.Long(0);
            }
        }
        if( theType.equals("Integer") ) {
            try {
                return new java.lang.Integer(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new java.lang.Integer(0);
            }
        }
        if( theType.equals("Short") ) {
            try {
                return new Short(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new Short((short)0);
            }
        }
        if( theType.equals("Float") ) {
            try {
                return new java.lang.Float(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new java.lang.Float(0);
            }
        }
        if( theType.equals("Byte") ) {
            try {
                return new Byte(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new Byte((byte)0);
            }
        }
        if( theType.equals("Byte") ) {
            try {
                return new Byte(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new Byte((byte)0);
            }
        }
        if( theType.equals("Boolean") ) {
            try {
                return new Boolean(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return new Boolean(false);
            }
        }
        if( theType.equals("Char") ) {
            try {
                return new Character(theData.charAt(0));
            } catch( Exception e ) {
                warnEvent(e);
                return new Character('\0');
            }
        }
        if( theType.equals("Class") ) {
            try {
                return findClass(theData);
            } catch( Exception e ) {
                warnEvent(e);
                return findClass("Object");
            }
        }
        if( theType.equals("String") ) return theData;
        // if (theType.equals("Date")) return new Date(theData);
        return makeInstanceFromClass( theType, theData);
    }

    public synchronized  Object makeInstanceFromClass(String theType,String theData)
    throws Exception {
        Class newClass = findClass(theType);
        try {
            return newClass.getConstructor(stringClassArrayOfOne).newInstance(createObjectArray(theData));
        } catch( Exception e ) {
            return newClass.newInstance();
        }
    }

     Class findClass(String theData) throws Exception {
        try {
            return Class.forName("java.lang." + theData);
        } catch( Exception e ) {
            try {
                return Class.forName( theData);
            } catch( Exception ee ) {
                throw ee;
            }
        }
    }


    /* Queries the interface for an Instance (all supers)*/
    public synchronized  Object getInstanceDef(String instanceName) {
        try {
            return getInstanceDef(findInstanceFromCyc(instanceName));
        } catch( Exception e ) {
            return makeError(e );
        }
    }

    public synchronized  Object getInstanceDef(Object obj) {
        try {
            return "'$java_object'("  + classToVector(obj.getClass())  + ":"  + obj.hashCode()+ ")";
        } catch( Exception e ) {
            return makeError(e );
        }
    }

    /* Queries the interface for an Instance (all className)*/
    public synchronized  Object getClassDef(String className) {
        try {
            return getClassDef(findClass(className));
        } catch( Exception e ) {
            return makeError(e );
        }
    }

    public synchronized  Object getClassDef(Class oclassd) {
        try {
            return classToVector(oclassd);
        } catch( Exception e ) {
            return makeError(e );
        }
    }

    /* Find an instance in allObjects based on HashCode */
    public synchronized  Object findInstanceFromCyc(String instanceName) {


        try {

            if( debug>1 ) System.out.println("searching for= " + instanceName);
            return allObjects.get(instanceName);

        } catch( Exception e ) {
            warnEvent(e);
            return null;
        }
    }


    /* Equivalent Bindings for Class Definition into OpenCyc List */
    public synchronized  Object instanceToVector(Object instance, int depth) {
        Class pClass = instance.getClass();
        StringBuffer interfaceList= new StringBuffer();
        interfaceList.append("class(" + toOpenCycString(pClass.getName())+ ",fields([");
        interfaceList.append(membersValuesToVector(instance, pClass.getFields()));
        interfaceList.append("]),methods([");
        interfaceList.append(membersValuesToVector(instance, pClass.getMethods()));
        interfaceList.append("]))");
        return interfaceList.toString();
    }


    /* Serializes Instance Members into OpenCyc List */
    public synchronized  Object membersValuesToVector(Object instance, Member[] pMembs) {
        StringBuffer interfaceList= new StringBuffer();
        for( int nMemb=0 ;nMemb < pMembs.length; nMemb++ ) {
            if( nMemb>0 ) interfaceList.append(",");
            interfaceList.append(memberValueToVector(instance, pMembs[nMemb]));
        }
        return interfaceList.toString();
    }

    /* Serializes Instance Member into OpenCyc List */
    public synchronized  Object memberValueToVector(Object instance,Member pMemb) {
        if( pMemb instanceof Method ) return methodValueToVector(instance,(Method)pMemb);
        if( pMemb instanceof Field ) return fieldValueToVector(instance,(Field)pMemb);
        return toScriptingName(pMemb.getName());
    }


    /* Serializes Methods into OpenCyc List */
    public synchronized  Object methodValueToVector(Object instance, Method pMemb) {
        String lcname=  pMemb.getName().toLowerCase();
        if( pMemb.getParameterTypes().length==0 )

            try {
                if( lcname.startsWith("get") || pMemb.getReturnType().getName().endsWith("String") )
                    return methodToVector(pMemb) + "=" + toOpenCycString(pMemb.invoke(instance,null));
            } catch( Exception e ) {
                return methodToVector(pMemb) + "=" + makeError(e);
            }
        return methodToVector(pMemb) + "=uncalled";

    }


    public synchronized  Object fieldValueToVector(Object instance, Field sField) {
        try {
            return fieldToVector(sField) + "=" + toOpenCycString(sField.get(instance));
        } catch( Exception e ) {
            return fieldToVector(sField) + "=" + makeError(e);
        }
    }



    /* Serializes Class Definition into OpenCyc List */
    public synchronized  Object classToVector(Class pClass) {
        StringBuffer interfaceList= new StringBuffer();
        interfaceList.append("class(" + toOpenCycString(pClass.getName())+ ",fields([");
        interfaceList.append(membersToVector(pClass.getFields()));
        interfaceList.append("]),methods([");
        interfaceList.append(membersToVector(pClass.getMethods()));
        interfaceList.append("]))");
        return interfaceList.toString();
    }


    /* Serializes Members into OpenCyc List */
    public synchronized  Object membersToVector(Member[] pMembs) {
        StringBuffer interfaceList= new StringBuffer();
        for( int nMemb=0 ;nMemb < pMembs.length; nMemb++ ) {
            if( nMemb>0 ) interfaceList.append(",");
            interfaceList.append(memberToVector(pMembs[nMemb]));
        }
        return interfaceList.toString();
    }

    /* Serializes Member into OpenCyc List */
    public synchronized  Object memberToVector( Member pMemb) {
        if( pMemb instanceof Method ) return methodToVector((Method)pMemb);
        if( pMemb instanceof Field ) return fieldToVector((Field)pMemb);
        return toScriptingName(pMemb.getName());
    }

    /* Serializes Methods into OpenCyc List */
    public synchronized  Object methodToVector(Method pMemb) {
        StringBuffer interfaceList= new StringBuffer();
        //                interfaceList.append(toScriptingName(Modifier.toString(pMemb.getModifiers())));
        //                interfaceList.append("(");
        interfaceList.append(toScriptingName(pMemb.getName()));
        interfaceList.append("(");
        interfaceList.append(typeToName(pMemb.getReturnType().getName()));
        //                interfaceList.append(",[");
        interfaceList.append(parameterTypesToVector(pMemb.getParameterTypes()));
        interfaceList.append(")");
        return interfaceList.toString();
    }

    public synchronized  Object parameterTypesToVector(Class[] pMembs) {
        StringBuffer interfaceList= new StringBuffer();
        for( int nMemb=0 ;nMemb < pMembs.length; nMemb++ ) {
            //if (nMemb>0) 
            interfaceList.append(",");
            interfaceList.append(parameterToVector(pMembs[nMemb]));
        }
        return interfaceList.toString();
    }

    public synchronized  Object fieldToVector(Field sField) {
        return toScriptingName(sField.getName()) + "(" + toScriptingName(sField.getType().getName()) +")";
    }

    public synchronized  Object parameterToVector(Class paramClass) {
        return typeToName(paramClass.getName());
    }

    public synchronized  void warnEvent(Exception e) {
        if( debug>0 )System.err.println("warning: " + e);
    }

    public synchronized  void fatalEvent(Exception e) {
        System.err.println("FATAL ERROR: "+e);
    }

    public synchronized  Object makeError(Exception e) {
        return "error('"+e+"')";
    }

    public synchronized  Object makeError(String e) {
        return "error('"+e+"')";
    }

    public synchronized  Object typeToName(String someName) {
        if( someName.equals("void") ) return "void";
        if( someName.equals("null") ) return "";
        if( someName.startsWith("java.lang.") ) return typeToName(someName.substring(10));
        return toScriptingName(someName);
    }
    public synchronized  Object toScriptingName(String someName) {
        return toOpenCycString(someName);
    }

    public synchronized  Object toOpenCycString(Object someName) {
        if( someName == null ) return "null";
        return "'" + someName.toString() + "'";
    }

    public synchronized  Object[] createObjectArray(Object a) {
        Object[] toReturnObject=null; 
        try {
            toReturnObject = (Object[])Array.newInstance(findClass("Object"),1); 
        } catch( Exception e ) {
            fatalEvent(e);
        }
        toReturnObject[0] = a;
        return toReturnObject;
    }

    public synchronized  Class[] makeClassArray(Class a) {
        Class[] toReturnClasses=null; 
        try {
            toReturnClasses = (Class[])Array.newInstance(findClass("Class"),1); 
        } catch( Exception e ) {
            fatalEvent(e);
        }
        toReturnClasses[0] = a;
        return toReturnClasses;
    }


    public String cyclify(Object obj) {

        if( obj instanceof CycFort ) {
            return(((CycFort)obj).cyclify());
        }

        if( obj instanceof Object ) {
            return makeCycFort(getObjNameCyc(obj)).cyclify();
        }

        if( obj instanceof CycList ) {
            return(((CycList)obj).cyclify());
        }

        if( obj instanceof String ) {
            String sobj = (String)obj;
            if( sobj.indexOf('(')<0 ) {
                return(makeCycConstantSafe((String)obj).cyclify());
            } else {
                try {
                    return cyclify(((CycList)((new CycListParser(this)).read(sobj))));
                } catch( Exception e ) {

                }
            }
        }

        return obj.toString();
    }


    //6239075950(49)


    public synchronized  void assertIsaSafe(CycFort cycobject, CycFort cycclass, CycFort cycmt) {
        try {
            assertIsa(cycobject,cycclass,cycmt);
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }


    public synchronized  CycConstant makeCycConstantSafe(String constname) {
        if( constname==null ) return null;
        try {
            return makeCycConstant(constname);
        } catch( Exception e ) {
            return null;
        }
    }


    /**
     * Preforms query in mt with parameters
     *   then unasserts the insanciated gafs derived from the query 
     *   
     *  NOTE:  Only if they exist in the same microtheory as the query is in.
     *   see queryMatch for how these insanciated gafs are produced
     *
     */


    public ArrayList deleteMatchGaf(CycList query,CycFort mt,int maxBackchains,int maxAnswers, int maxSeconds) {
        ArrayList al = queryMatch(query,mt,maxBackchains,maxAnswers,maxSeconds);
        Iterator its = al.iterator();
        while( its.hasNext() ) {
            try {
                cycAccess.unassertGaf((CycList)its.next(),mt);
            } catch( Exception e ) {
            }
        }
        return al;
    }

    /**
     * Preforms query in mt with parameters
     *   returns the insanciated gafs derived from the query 
     *  
     *   a query of (#$isa ?X #$Dog) in #$BiologyMt
     *     will return a ArrayList with a single CycList formula containing:
     *     (#$isa (#$GenericInstanceFn #$Dog) #$Dog)
     *
     */

    public ArrayList queryMatch(CycList query,CycFort mt,int maxBackchains,int maxAnswers, int maxSeconds) {

        ArrayList match = new CycList();
        try {
            CycList results = queryWithMaximums(query,mt,maxBackchains,maxAnswers,maxSeconds);
            Iterator its = results.iterator();
            while( its.hasNext() ) {
                try {
                    CycList bindingset = (CycList)its.next();
                    CycList result = replaceVarsWithBindingSet(query,bindingset).getFormula();
                    System.out.println(result);
                    match.add(result);
                } catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        } catch( Exception ee ) {
            ee.printStackTrace();
        }
        return match;
    }

    public CycList queryWithMaximums(CycList query,CycFort mt,int maxBackchains,int maxAnswers, int maxSeconds) 
    throws IOException, CycApiException {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("(clet ((*cache-inference-results* nil) ");
        queryBuffer.append("       (*compute-inference-results* nil) ");
        queryBuffer.append("       (*unique-inference-result-bindings* t) ");
        queryBuffer.append("       (*generate-readable-fi-results* nil)) ");
        queryBuffer.append("  (without-wff-semantics ");
        queryBuffer.append("    (cyc-query '" + query.cyclify() + " ");
        queryBuffer.append("                  " + mt.cyclify() + " ");
        queryBuffer.append("                  '(:backchain "+maxBackchains+" :number "+maxAnswers+" :time "+ maxSeconds +"))))");
        return converseList(queryBuffer.toString());
    }

    public QueryLiteral replaceVarsWithBindingSet(CycList query, ArrayList bindingset) {
        QueryLiteral querylit = new QueryLiteral(query);
        Iterator bindings = bindingset.iterator();
        while( bindings.hasNext() ) {
            CycList binding =  (CycList)bindings.next();
            CycVariable variable = (CycVariable)binding.first();
            Object value = binding.rest();
            querylit.substituteVariable(variable,value);
        }
        return querylit;
    }





    /***********************************************************
     * Java Editing
     *
     **********************************************************/


    public synchronized String findNamedProperty(Object target, String property) {
        // Normally more deeper analysis then string concatination (that is why the target object is here)
        return "set" + toProperCase(property);
    }

    public synchronized boolean attemptJavaSetMethod(Object target,String property,Object value) {
        if( value==null ) {
            try {
                target.getClass().getMethod(findNamedProperty(target,property),null).invoke(target,null);
                return true;
            } catch( Exception e ) {
                return false;
            }

        } else {

            Class[] sc = { value.getClass()};
            Object[] sa = { value};

            try {
                target.getClass().getMethod(findNamedProperty(target,property),sc).invoke(target,sa);
                return true;
            } catch( Exception e ) {
                return false;
            }
        }
    }

    public synchronized Method findJavaMethod(Class oclass, String methodname)
    throws NoSuchMethodError {
        Method[] meths = oclass.getMethods();
        int len = meths.length;
        for( int i = 0; i < len ; i++ )
            if( meths[i].getName().equals(methodname) ) return meths[i];

        throw new NoSuchMethodError(methodname +" for " + oclass.getName());
    }

    /***********************************************************
     * Java Typing
     *
     **********************************************************/


    public synchronized Object makeInstanceFromClass(String theType,Object value)
    throws Exception {
        if( value==null ) {
            Class newClass = Class.forName(theType);    // Can Throw ClassNotFound
            return newClass.newInstance();    // Can Throw Instanciation
        } else {
            Class newClass = Class.forName(theType);
            Class[] sc = { value.getClass()};
            Object[] sa = { value};

            try {
                return newClass.getConstructor(sc).newInstance(sa);    //  throw == second chance
            } catch( Exception e ) {
                // Second Chance
                return newClass.newInstance();    // Can Throw Instanciation
            }
        }
    }

    /***********************************************************
     * Strings
     *
     **********************************************************/

    public synchronized  String toProperCase(String property) {
        return property.substring(0,0).toUpperCase()+property.substring(1).toLowerCase();
    }



    public  void assertObjectData(CycFort dataMt, Object object,CycFort cycobject) {
        Log.current.println("assertObjectData " + object );

        if( object instanceof CycList ) {
            assertWithTranscriptNoWffCheckJava(((CycList)object).cyclify(), dataMt);
            return;
        }

        Class jclass = object.getClass();
        if( jclass.isArray() ) {
            assertArrayData(dataMt,object, cycobject);
            return;
        }

        if( object instanceof Iterator ) {
            assertIteratorData(dataMt,(Iterator)object, cycobject);
            return;
        }

        assertObjectDataFromTemplate(jclass,dataMt,object, cycobject);

    }

    public void  assertObjectDataFromTemplate(Class jclass,CycFort dataMt, Object object,CycFort cycobject) {
        Hashtable template = (Hashtable)thisClassTemplates.get(jclass);
        Iterator it = template.keySet().iterator();
        while( it.hasNext() ) {
            Object cycdataaccess = it.next();
            if( cycdataaccess instanceof CycConstant ) {
                assertMemberValue( dataMt, cycobject, object ,(CycConstant)cycdataaccess, template.get(cycdataaccess));
            }

        }
    }

    public  void assertArrayData(CycFort dataMt, Object object,CycFort cycobject) {
        Log.current.println("assertArrayData " + object );
        CycList assertme = new CycList(cycHasArrayMember);
        assertme.add(cycobject);
        assertme.add(null);
        assertme.add(null);
        for( int i=0 ; i < ((Object[])object).length; i++ ) {
            Object submember = makeCycJavaObject(dataMt,((Object[])object)[i]);
            try {
                assertme.set(2,new Integer(i));
                assertme.set(3,submember);
                assertWithTranscriptNoWffCheckJava(assertme,dataMt);
            } catch( Exception e ) {
                e.printStackTrace(System.err);
            }
        }
    }

    public  void assertIteratorData(CycFort dataMt, Iterator object,CycFort cycobject) {
        Log.current.println("assertIteratorData " + object );
        CycList assertme = new CycList(cycHasArrayMember);
        assertme.add(cycobject);
        assertme.add(3,new Integer(0));
        assertme.add(null);
        while( object.hasNext() ) {
            Object submember = makeCycJavaObject(dataMt,object.next());
            try {
                assertme.set(3,submember);
                assertWithTranscriptNoWffCheckJava(assertme,dataMt);
            } catch( Exception e ) {
                e.printStackTrace(System.err);
            }
        }
    }

    public  void assertSlotValue(CycFort dataMt,CycFort cycobject, Object slot, Object value, boolean singlevalued) {
        assertSlotValue(dataMt,cycobject,slot,value,null,singlevalued);
    }


    public  void assertSlotValue(CycFort dataMt,CycFort cycobject, Object slot, Object value, Object type, boolean singlevalued) {
        CycConstant cycslot = null;
        if( cycobject==null ) {
            Log.current.println("assertSlotValue(CycFort " + dataMt + ",CycConstant " +cycobject+", Object " +slot+", Object " +value+", boolean " +singlevalued +")");
            return;
        }

        if( slot instanceof CycConstant ) {
            cycslot = (CycConstant)slot;
        } else {
            if( slot instanceof String ) {
                cycslot = makeTypedCycFort(dataMt,"JavaSlot",(String)slot);
            }
        }

        if( singlevalued ) clearSlot(dataMt,cycobject,cycslot);

        if( value == null ) return;

        if( value instanceof Iterator ) {
            while( ((Iterator)value).hasNext() )
                assertSlotValue(dataMt,cycobject, cycslot, ((Iterator)value).next(), type,false);
            return;
        }
        if( value instanceof Enumeration ) {
            while( ((Enumeration)value).hasMoreElements() )
                assertSlotValue(dataMt,cycobject, cycslot, ((Enumeration)value).nextElement(),type, false);
            return;
        }

        if( value.getClass().isArray() ) {
            assertSlotValue(dataMt,cycobject, cycslot, Arrays.asList((Object[])value).iterator(), type, false);
            return;
        }
        Object cycvalue = makeCycJavaObject(dataMt,value,false);

        if( type!=null ) {
            if( cycvalue instanceof CycFort ) {
                assertIsaSafe((CycFort)cycvalue,(CycFort)makeCycFort(type),dataMt);
            }
            if( cycvalue instanceof CycList ) {
                assertIsaSafe(((CycFort)new CycNart((CycList)cycvalue)),(CycFort)makeCycFort(type),dataMt);
            }
        }

        if( cycvalue!=null ) {
            try {
                assertGafNoWff(dataMt,cycslot,cycobject,cycvalue);
            } catch( Exception e ) {
                e.printStackTrace(System.out);
                Log.current.println("assertSlotValue(CycFort " + dataMt + ",CycConstant " +cycobject+", Object " +slot+", Object " +value+", boolean " +singlevalued +")");
            }
        }
    }

    public  void assertMemberValue(CycFort dataMt, CycFort cycobject, Object object, CycConstant cycaccess, Object accessmember) {
        Log.current.println("while {assertObjectData " + cycobject + " " + cycaccess + " " + accessmember + "}" );
        try {
            if( accessmember instanceof DataMethod ) assertDataMethodResult( dataMt, cycobject, object, cycaccess, (DataMethod)accessmember);
            //      if ( accessmember instanceof Method ) assertMethodResult( dataMt, cycobject, object, cycaccess, (Method)accessmember);
            if( accessmember instanceof Field ) assertFieldValue( dataMt, cycobject, object, cycaccess, (Field)accessmember);
        } catch( Exception e ) {
            e.printStackTrace( System.out);
        }
    }

    public  void assertFieldValue(CycFort dataMt, CycFort cycobject, Object object, CycConstant cycaccess, Field accessmember) 
    throws Exception{
        CycList assertme = new CycList(cycHasSlotValue);    //"#$hasFieldValue"
        assertme.add(cycobject);
        assertme.add(cycaccess);
        assertme.add(makeCycJavaObject(dataMt,accessmember.get(object)));
        assertWithTranscriptNoWffCheckJava(assertme,dataMt);
    }

    public  void assertMethodResult(CycFort dataMt, CycFort cycobject, Object object, CycConstant cycaccess, Method accessmember) 
    throws Exception{
        CycList assertme = new CycList(cycHasMethod);    //"#$hasMethodValue"
        assertme.add(cycaccess);
        assertme.add(makeCycJavaObject(dataMt,accessmember.invoke(object,null)));
        assertWithTranscriptNoWffCheckJava(assertme,dataMt);
    }

    public  void assertDataMethodResult(CycFort dataMt, CycFort cycobject, Object object, CycConstant cycaccess, DataMethod accessmember) 
    throws Exception{
        CycList assertme = new CycList(cycHasSlotValue);    //"#$hasMethodValue"
        assertme.add(cycobject);
        assertme.add(cycaccess);
        assertme.add(makeCycJavaObject(dataMt,accessmember.get(object)));
        assertWithTranscriptNoWffCheckJava(assertme,dataMt);
    }

    public synchronized   void clearSlot(CycFort dataMt,CycFort cycobject, Object cycslot) {
        // Delete all previous
        CycList query = new CycList(cycslot);
        query.add(cycobject);
        CycVariable cv = new CycVariable("Prev");
        query.add(cv);

        try {
            Iterator result =  askWithVariable(query,cv,dataMt).iterator();
            while( result.hasNext() ) {
                query.set(2,result.next());
                cycAccess.unassertGaf(query,dataMt);
            }
        } catch( Exception e ) {
            e.printStackTrace(System.out);
        }

    }




    public synchronized CycFort makeCycClassInstance(Class jclass) {
        if( jclass==null ) return cycNull;
        CycConstant cycjclass  = (CycConstant)cycKnowsClass.get(jclass);
        if( cycjclass!=null ) return cycjclass;
        String classname = jclass.getName();
        if( classname.startsWith("[L") ) classname = classname.substring(2);
        if( classname.startsWith("[") ) classname = classname.substring(1);
        if( classname.endsWith(";") ) classname = classname.substring(0,classname.length()-1);
        //String packagename = jclass.getPackage().getName();
        String classextension = "Instance";
        if( jclass.isPrimitive() ) {
            /*
            jboolean z;
            jbyte    b;
            jchar    c;
            jshort   s;
            jint     i;
            jlong    j;
            jfloat   f;
            jdouble  d;
            jobject  l;
            */    
            if( jclass.isArray() ) {
                switch( classname.charAt(0) ) {
                    case 'Z':
                        classname = "boolean";
                    case 'B':
                        classname = "byte";
                    case 'C':
                        classname = "char";
                    case 'S':
                        classname = "short";
                    case 'I':
                        classname = "int";
                    case 'J':
                        classname = "long";
                    case 'F':
                        classname = "float";
                    case 'D':
                        classname = "double";
                }
                classextension = "Array";
            } else {
                classextension = "Value";
            }
        } else {
            try {
                if( jclass.isArray() ) return new CycNart((CycFort)(cycArrayOfClass),(Object)makeCycClassInstance(Class.forName(classname)));
            } catch( Exception e ) {
                e.printStackTrace();
            }
            if( classname.startsWith("java")
                || classname.startsWith("logicmoo")
                || classname.startsWith("org")
                //    || classname.startsWith("com")
              ) {
                int lp = classname.lastIndexOf(".");
                if( lp>2 ) classname = classname.substring(lp+1);
            }
        }

        classname = Strings.change(classname,".","_");
        classname = Strings.change(classname,"$","_");

        String cycclassname = classname + classextension;

        Log.current.println("cycclassname =" + cycclassname);

        try {

            cycjclass =  makeCycConstantSafe(cycclassname);    
        } catch( Exception e ) {
            Log.current.println("makeCycConstantSafe: " +cycclassname+" "+ e );
            e.printStackTrace(System.err);
        }
        cycKnowsClass.put(jclass,cycjclass);
        cycKnowsClass.put(cycjclass,jclass);

        // Save Isa
        try {

            assertIsaSafe(cycjclass,      cycClassInstance,javaMt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
        try {
            // Make super classes
            Class superjclass = jclass.getSuperclass();
            if( superjclass != null ) {
                CycFort cycsuperjclass = makeCycClassInstance(superjclass);
                assertGenls(cycjclass,cycsuperjclass,javaMt);
            }
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
        // Decide if we should make a template
        String classstring = jclass.toString();
        if( classstring.startsWith("class java.lang") 
            || classstring.startsWith("class java.io")
            || classstring.startsWith("class logicmoo.")
            || classstring.startsWith("class com.logicmoo")
            || jclass.isPrimitive() )
            return cycjclass;

        Hashtable template = new Hashtable();

        Method[] methods = jclass.getMethods();
        for( int i =0; i<methods.length;i++ ) {
            Method method = methods[i];
            CycFort cycmethodjclass = makeCycClassInstance(method.getReturnType());
            String methodname = method.getName();
            CycConstant cycmethod = makeTypedCycFort("JavaMethod",methodname + "_method");
            template.put(cycmethod,method);
            Class[] params = method.getParameterTypes();
            assertIsaJavaMethodOf(cycjclass,jclass,cycmethod,methodname,params,cycmethodjclass,method,template);
        }

        Field[] fields = jclass.getFields();
        for( int i =0; i<fields.length;i++ ) {
            Field field=fields[i];
            CycFort cycfieldjclass = makeCycClassInstance(field.getType());
            CycConstant cycfield = makeTypedCycFort("JavaSlot",field.getName() + "_field");
            template.put(cycfield,field);
            assertIsaJavaFieldOf(cycjclass,cycfield,cycfieldjclass);
        }

        thisClassTemplates.put(jclass,template);
        thisClassTemplates.put(cycjclass,template);
        return cycjclass;
    }

    public  void assertIsaJavaFieldOf(CycConstant cycjclass,CycConstant cycfield,CycFort cycfieldjclass) {
        try {

            assertWithTranscriptNoWffCheckJava(
                                          "(#$relationAllExists "  
                                          + " " + cycfield.cyclify()
                                          + " " + cycjclass.cyclify()
                                          + "  " + cycfieldjclass.cyclify() +  " )",(CycFort) javaMt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
    }

    public  void assertIsaJavaDataMethodOf(CycConstant cycjclass,CycConstant cycdatamethod, CycFort cycmethodjclass) {
        try {

            assertWithTranscriptNoWffCheckJava(
                                          "(#$relationAllExists " 
                                          + " " + cycdatamethod.cyclify()
                                          + " " + cycjclass.cyclify()
                                          + "  " + cycmethodjclass.cyclify() +  " )",(CycFort) javaMt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
    }

    public  void assertIsaJavaMethodOf(CycConstant cycjclass,Class jclass,CycConstant cycmethod,String methodname,Class[] params, CycFort cycmethodjclass,Method method,Hashtable template) {

        assertWithTranscriptNoWffCheckJava(
                                          "(#$hasJavaMethod " + cycjclass.cyclify()
                                          + " (#$JavaMethodFn " + cycmethod.cyclify() + " " + makeClassInstanceesDef(params) + " ) " 
                                          + "  " + cycmethodjclass.cyclify() +  " )",(CycFort) javaMt);
        if( params.length>0 ) return;
        if( methodname.startsWith("to") ) return;

        if( methodname.startsWith("get") ) {
            String dataname = methodname.substring(3);
            Method setmethod = null;  
            CycConstant cycdatamethod = null;
            try {
                setmethod = jclass.getMethod("set"+dataname,(Class[])Array.newInstance(method.getReturnType(),1)); 
                cycdatamethod = makeTypedCycFort("JavaSlot",dataname+ "_getSet");
            } catch( Exception e ) {
                cycdatamethod = makeTypedCycFort("JavaSlot",dataname+ "_get");
                setmethod = null;
            }
            template.put(cycjclass,new DataMethod(dataname,method,setmethod));
            assertIsaJavaDataMethodOf(cycjclass,cycdatamethod,cycmethodjclass);
            return;
        }

        if( methodname.startsWith("child") ||  methodname.endsWith("es") ) {
            String dataname = methodname;
            CycConstant cycdatamethod = makeTypedCycFort("JavaSlot",dataname+ "_getAdd");
            template.put(cycjclass,new DataMethod(dataname,method,null));
            assertIsaJavaDataMethodOf(cycjclass,cycdatamethod,cycmethodjclass);
            return;
        }

        String firstthree = methodname.substring(0,2);

        if( methodname.equalsIgnoreCase("iterator") ) {
            String dataname = methodname;
            CycConstant cycdatamethod = makeTypedCycFort("JavaSlot",dataname+ "_getIterator");
            Method setmethod = null;
            DataMethod datamethod = new DataMethod(dataname,method,setmethod);
            template.put(cycjclass,datamethod);
            assertIsaJavaDataMethodOf(cycjclass,cycdatamethod,cycmethodjclass);
            return;
        }


        if( firstthree.equalsIgnoreCase("rem")
            || firstthree.equalsIgnoreCase("add") 
            || firstthree.equalsIgnoreCase("set") 
            || firstthree.equalsIgnoreCase("clo") 
            || firstthree.equalsIgnoreCase("cle") 
            || firstthree.equalsIgnoreCase("ter") 
            ||          firstthree.equalsIgnoreCase(         "kil")          
            || firstthree.equalsIgnoreCase("cre") 
            || firstthree.equalsIgnoreCase("mak") ) return;



        /*
         if ( !(methodname.endsWith("s")) ) return;
         String dataname = methodname;
         CycConstant cycdatamethod = makeTypedCycFort("Slot",dataname+ "_get");
         Method setmethod = null;
         DataMethod datamethod = new DataMethod(dataname,method,setmethod);
         template.put(cycjclass,datamethod);
         assertIsaJavaDataMethodOf(cycjclass,cycdatamethod,cycmethodjclass);
         */
    }

    public String makeClassInstanceesDef(Class jclass[]) {
        StringBuffer cdefs = new StringBuffer(10);
        try {
            for( int i = 0 ; i < jclass.length ; i ++ ) cdefs.append(" ").append(makeCycClassInstance(jclass[i]).cyclify());
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
        return cdefs.toString();
    }

    public CycConstant makeTypedCycFort(String ctype,String name) {
        return makeTypedCycFort(javaMt, ctype, name);
    }

    public CycConstant makeTypedCycFort(CycFort dataMt, String type,String name) {
        CycConstant nameC =  makeCycConstantSafe(name);
        CycConstant typeC =  makeCycConstantSafe(type);
        try {

            assertIsaSafe(typeC,     collection,dataMt);
            assertIsaSafe(nameC,typeC,dataMt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
        return nameC;
    }


    public synchronized  PrintWriter getPrintWriter(Writer w) {
        if( w==null ) return new PrintWriter(System.out);
        if( w instanceof PrintWriter ) return(PrintWriter)w;
        return new PrintWriter(w);
    }


    public final class DataMethod {

        public String dataname = null;
        public Method getmethod = null;
        public Method setmethod = null;
        public DataMethod(String adataname, Method agetmethod, Method asetmethod) {
            dataname = adataname;  
            getmethod = agetmethod;  
            setmethod = asetmethod;  
        }

        public Object get(Object object) {
            try {
                return getmethod.invoke(object,null);
            } catch( Exception e ) {
                return "" + e;
            }
        }
        public void set(Object object,Object value) {
            try {
                Object[] param = {value} ;
                setmethod.invoke(object,param);
            } catch( Exception e ) {
            }
        }
    }

    public void  assertWithTranscriptNoWffCheckJava(CycList sentence, CycFort mt) {
        try {
            cycAccess.assertWithTranscript( sentence,mt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
    }
    public void  assertWithTranscriptNoWffCheckJava(String sentence, CycFort mt) {
        try {
            cycAccess.assertWithTranscript( toCycList(sentence),mt);
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Cyclifys a sentence a string
     */
    public  CycList toCycList(String sentence) {
        try {
            return(((CycList)((new CycListParser(cycAccess)).read(sentence))));
        } catch( Exception e ) {
            return null;
        }

    }
    /**
     * Cyclifys a sentence to a string
     */
    public  String toCycListString(CycAccess cycAccess,String sentence) {
        try {
            return(((CycList)((new CycListParser(cycAccess)).read(sentence))).cyclify());
        } catch( Exception e ) {
            return null;
        }

    }
           /*
           
           
           		 CycL
           
                    
    (#$implies
         (#$and
             (#$remoteCycServer ?Server ?Host ?Port)
             (#$evaluate ?Result (#$EvaluateSubLFn (cyc-api-remote-eval ?SUBL ?Host ?Port))))
         (#$evaluate ?Result (#$EvaluateRemoteSubLFn ?Server ?SUBL))
                
           

                    
           (ExpandSubLFn (?X) (+ 1 ?X))
           
              (evaluate ??RESULT 
                    
           
           */

    public  String cleanString(String name) {
        if( name==null ) return "null";
        String tryName = name;
        if( name.startsWith("#$") ) tryName = name.substring(2);
        tryName = Strings.change(tryName,'$','_');
        tryName = Strings.change(tryName,')','_');
        tryName = Strings.change(tryName,'(','_');
        tryName = Strings.change(tryName,' ','_');
        tryName = Strings.change(tryName,'.','_');
        return Strings.change(tryName,'@','_');
    }

    public  boolean toBool(Object o) throws Exception {
        switch( o.toString().charAt(0) ) {
            case 'T' : return true;
            case 'N' : return false;
            case 'F' : return false;
        }
        throw new Exception("toBool " + o);
    }


}



