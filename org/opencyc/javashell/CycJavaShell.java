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
import java.net.*;
import org.opencyc.api.*;
import org.opencyc.util.*;
import org.opencyc.cycobject.*;
import org.opencyc.cyclobject.*;
import org.opencyc.cycagent.*;
import org.opencyc.inferencesupport.*;
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
    public    CycConstant makeCycConstantError = null;
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

    public void ensureClientSupportsShell(CycAccess cycA) throws UnknownHostException,IOException,CycApiException {
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
	    cycVoid =  cycAccess.makeCycConstant("#$voidValue");
	    cycNull =  cycAccess.makeCycConstant("#$nullValue");
	    cycHasArrayMember =  cycAccess.makeCycConstant("#$javaArrayContains");
	    cycHasMethod =  cycAccess.makeCycConstant("#$hasMethod");
	    cycHasSlot =  cycAccess.makeCycConstant("#$relationAllExists");
	    cycClassInstance =  cycAccess.makeCycConstant("#$ClassInstance");
	    cycHasSlotValue =  cycAccess.makeCycConstant("#$hasSlotValue");
	    javaMt =  cycAccess.makeCycConstant("#$JavaMt");
	    CycFort reifiedMicrotheory =  cycAccess.makeCycConstant("#$ReifiedMicrotheory");
	    cycArrayOfClass =  cycAccess.makeCycConstant("#$SetOfTypeFn");
	    makeCycConstantError =  cycAccess.makeCycConstant("#$MakeCycConstantErrorFn");
	   // assertIsaSafe(javaMt,     cycAccess.microtheory,        cycAccess.baseKB);

	}
    }



    /* Scripting Engine is running = true*/
    public synchronized boolean isStarted() {
        return m_isInitialized;
    }

    public  Object invoke(CycList query) throws Exception {

	/*
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
	    */

        return CYC_NIL;
    }

    /* OpenCyc Will call these */

   // Moved code because its breaking building from cvs
}



