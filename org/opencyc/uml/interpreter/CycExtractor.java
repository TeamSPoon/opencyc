package org.opencyc.uml.interpreter;

import java.io.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.action.*;
import org.opencyc.uml.statemachine.*;
import org.opencyc.util.*;

/**
 * Extracts a state machine model from the Cyc KB.
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


public class CycExtractor {

    /**
     * The default verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * the name of the relevant inference microtheory
     */
    public static final String mtName = "UMLStateMachineSpindleCollectorMt";

    /**
     * the relevant inference microtheory
     */
    protected CycFort mtTerm;

    /**
     * the CycAccess object which manages the Cyc server connection
     */
    protected CycAccess cycAccess;

    /**
     * the state machine factory
     */
    protected StateMachineFactory stateMachineFactory;

    /**
     * the state machine term
     */
    protected CycConstant stateMachineTerm;

    /**
     * the state machine name
     */
    protected String stateMachineName;

    /**
     * the state machine
     */
    protected StateMachine stateMachine;

    /**
     * the list of state vertex terms
     */
    protected CycList stateVertexTerms;

    /**
     * the dictionary with the key a stateTerm and the value its StateVertex
     */
    protected HashMap stateVertexDictionary = new HashMap();

    /**
     * the list of transition terms
     */
    protected CycList transitionTerms;

    /**
     * the list of procedure terms
     */
    protected CycList procedureTerms;

    /**
     * the dictionary with the key a transitionTerm and the value its Transition
     */
    protected HashMap transitionDictionary = new HashMap();

    /**
     * the dictionary with the key a procedureTerm and the value its Procedure
     */
    protected HashMap procedureDictionary = new HashMap();

    /**
     * the argument terms for the current procedure
     */
    protected CycList argumentTerms;

    /**
     * the result terms for the current procedure
     */
    protected CycList resultTerms;


    /**
     * Construct a new CycExtractor object given the CycAccess
     * server connection.
     *
     * @param cycAcess the given CycAccess Cyc KB server connection
     */
    public CycExtractor(CycAccess cycAccess) {
        this.cycAccess = cycAccess;
        stateMachineFactory = new StateMachineFactory();
    }

    /**
     * Extracts the state machine model specified by the given name.
     *
     * @param stateMachineName the name of the state machine to be extracted from Cyc
     * @return the state machine model specified by the given name
     */
    public StateMachine extract (String stateMachineName)
        throws IOException, CycApiException, ClassNotFoundException {
        mtTerm = cycAccess.getKnownConstantByName(mtName);
        this.stateMachineName = stateMachineName;
        stateMachine = extractStateMachine();
        extractProcedures();
        extractStates();
        return stateMachine;
    }

    /**
     * Extracts the state machine from Cyc.
     */
    protected StateMachine extractStateMachine ()
            throws IOException, CycApiException, ClassNotFoundException {
        stateMachineTerm = cycAccess.getConstantByName(stateMachineName);
        CycConstant namespaceTerm =
            (CycConstant) cycAccess.getArg2("umlNamespaceLink",
                                            stateMachineName,
                                            mtName);
        String namespaceName = namespaceTerm.toString();
        String commentString = cycAccess.getComment(stateMachineTerm);
        Object context = this;
        return stateMachineFactory.makeStateMachine(namespaceName,
                                                    stateMachineName,
                                                    commentString,
                                                    context);
    }

    /**
     * Extracts the procedures for the state machine from Cyc.
     */
    protected void extractProcedures ()
            throws IOException, CycApiException, ClassNotFoundException {
        getStateTerms();
        getTransitionTerms();
        getProcedureTerms();
        Iterator iter = procedureTerms.iterator();
        while (iter.hasNext()) {
            CycConstant procedureTerm = (CycConstant) iter.next();
            String commentString =
                cycAccess.getComment(procedureTerm);
            String language =
                (String) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlLanguage"),
                                           procedureTerm,
                                           mtTerm).toString();
            String body =
                (String) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlBody"),
                                           procedureTerm,
                                           mtTerm);
            boolean isList =
                cycAccess.isa(procedureTerm,
                              cycAccess.getKnownConstantByName("UMLProcedure-IsList"));
            Procedure procedure =
                stateMachineFactory.makeProcedure(procedureTerm.toString(),
                                                  commentString,
                                                  language,
                                                  body,
                                                  isList);
            procedureDictionary.put(procedureTerm, procedure);
            getArgumentAndResultTerms(procedureTerm);
            Iterator iter2 = argumentTerms.iterator();
            while (iter2.hasNext()) {
                CycConstant argumentTerm = (CycConstant) iter2.next();
                String name =
                    (String) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlName"),
                                               argumentTerm,
                                               mtTerm);
                commentString = cycAccess.getComment(argumentTerm);
                CycConstant typeTerm =
                    (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlType"),
                                                    argumentTerm,
                                                    mtTerm);
                Class type = translateType(typeTerm);
                stateMachineFactory.addInputPinToProcedure(name,
                                                           commentString,
                                                           procedure,
                                                           type);
            }
            iter2 = resultTerms.iterator();
            while (iter2.hasNext()) {
                CycConstant resultTerm = (CycConstant) iter2.next();
                String name =
                    (String) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlName"),
                                               resultTerm,
                                               mtTerm);
                commentString = cycAccess.getComment(resultTerm);
                CycConstant typeTerm =
                    (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlType"),
                                                    resultTerm,
                                                    mtTerm);
                Class type = translateType(typeTerm);
                stateMachineFactory.addOutputPinToProcedure(name,
                                                            commentString,
                                                            procedure,
                                                            type);
            }
        }
    }

    /**
     * Returns the java class denoted by the given Cyc type term.
     *
     * @param typeTerm the given type term
     * @return the java class denoted by the given Cyc type term
     */
    protected Class translateType (CycConstant typeTerm)
        throws IOException, CycApiException, ClassNotFoundException {
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveInt")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveInt");
        else
            throw new RuntimeException("Unhandled typeTerm " + typeTerm.cyclify());
    }

    /**
     * Gets the argument and result terms for the given procedure term.
     *
     * @param procedureTerm the given procedure term
     */
    protected void getArgumentAndResultTerms (CycConstant procedureTerm)
        throws IOException, CycApiException {
        argumentTerms = new CycList();
        CycList candidateArgumentTerms =
            cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlProcedureLink"),
                               procedureTerm,
                               mtTerm);
        Iterator iter = candidateArgumentTerms.iterator();
        while (iter.hasNext()) {
            CycConstant term = (CycConstant) iter.next();
            if (cycAccess.isa(term, cycAccess.getKnownConstantByName("UMLInputPin"))) {
                if (verbosity > 2)
                    Log.current.println("Extracted InputPin " + term.cyclify() +
                                        " for " + procedureTerm.cyclify());
                argumentTerms.add(term);
            }
        }
        resultTerms = new CycList();
        iter = candidateArgumentTerms.iterator();
        while (iter.hasNext()) {
            CycConstant term = (CycConstant) iter.next();
            if (cycAccess.isa(term, cycAccess.getKnownConstantByName("UMLOutputPin"))) {
                if (verbosity > 2)
                    Log.current.println("Extracted OutputPin " + term.cyclify() +
                                        " for " + procedureTerm.cyclify());
                resultTerms.add(term);
            }
        }
    }

    /**
     * Extracts the states for the state machine from Cyc.
     */
    protected void extractStates ()
            throws IOException, CycApiException, ClassNotFoundException {
        Iterator iter = stateVertexTerms.iterator();
        while (iter.hasNext()) {
            CycConstant stateVertexTerm = (CycConstant) iter.next();
            if (cycAccess.isa(stateVertexTerm,
                              cycAccess.getKnownConstantByName("UMLCompositeState")))
                extractCompositeState(stateVertexTerm);
            else if (cycAccess.isa(stateVertexTerm,
                                   cycAccess.getKnownConstantByName("UMLPseudoState")))
                extractPseudoState(stateVertexTerm);
            else if (cycAccess.isa(stateVertexTerm,
                                   cycAccess.getKnownConstantByName("UMLSimpleState")))
                extractSimpleState(stateVertexTerm);
            else if (cycAccess.isa(stateVertexTerm,
                                   cycAccess.getKnownConstantByName("UMLFinalState")))
                extractFinalState(stateVertexTerm);
            else
                throw new RuntimeException("Unhandled stateVertexTerm " +
                                           stateVertexTerm.cyclify());
        }
    }

    /**
     * Extracts a composite state from Cyc given its Cyc term
     *
     * @param compositeStateTerm the given composite state term
     */
    protected void extractCompositeState (CycConstant compositeStateTerm)
        throws IOException, CycApiException {
        String name = compositeStateTerm.toString();
        String commentString =
            cycAccess.getComment(compositeStateTerm);
        CompositeState container = null;
        Procedure entry = null;
        Procedure exit = null;
        Procedure doActivity = null;
        boolean isConcurrent = false;

        CompositeState topState =
            stateMachineFactory.makeCompositeState(name,
                                                   commentString,
                                                   container,
                                                   entry,
                                                   exit,
                                                   doActivity,
                                                   isConcurrent);
    }

    /**
     * Extracts a pseudo state from Cyc given its Cyc term
     *
     * @param pseuodStateTerm the given pseudo state term
     */
    protected void extractPseudoState (CycConstant pseudoStateTerm)
        throws IOException, CycApiException {
        String name = pseudoStateTerm.toString();
        String commentString =
            cycAccess.getComment(pseudoStateTerm);
    }

    /**
     * Extracts a simple state from Cyc given its Cyc term
     *
     * @param simpleStateTerm the given state vertex term
     */
    protected void extractSimpleState (CycConstant simpleStateTerm)
        throws IOException, CycApiException {
        String name = simpleStateTerm.toString();
        String commentString =
            cycAccess.getComment(simpleStateTerm);
    }

    /**
     * Extracts a final state from Cyc given its Cyc term
     *
     * @param finalStateTerm the given state vertex term
     */
    protected void extractFinalState (CycConstant finalStateTerm)
        throws IOException, CycApiException {
        String name = finalStateTerm.toString();
        String commentString =
            cycAccess.getComment(finalStateTerm);
    }

    /**
     * Finds the state terms of the state machine term.
     */
    protected void getStateTerms ()
        throws IOException, CycApiException {
        stateVertexTerms = new CycList();
        CycConstant topStateTerm =
            (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlTop"),
                                            stateMachineTerm,
                                            mtTerm);
        Stack stateTermStack = new Stack();
        stateTermStack.push(topStateTerm);
        while (! stateTermStack.isEmpty()) {
            CycConstant stateVertexTerm = (CycConstant) stateTermStack.pop();
            stateVertexTerms.add(stateVertexTerm);
            if (verbosity > 2)
                Log.current.println("Extracted state " + stateVertexTerm.cyclify());
            if (cycAccess.isa(stateVertexTerm,
                              cycAccess.getKnownConstantByName("UMLCompositeState"))) {
                CycList subStates =
                    cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlContainer"),
                                       stateVertexTerm,
                                       mtTerm);
                Iterator iter = subStates.iterator();
                while (iter.hasNext()) {
                    Object object = iter.next();
                    if (object instanceof CycConstant)
                        stateTermStack.push(object);
                }
            }
        }
    }

    /**
     * Returns the transition terms of the state machine.
     */
    protected void getTransitionTerms ()
        throws IOException, CycApiException {
        CycList stateMachineReferents =
            cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlStateMachineLink"),
                               stateMachineTerm,
                               mtTerm);
        transitionTerms = new CycList();
        Iterator iter = stateMachineReferents.iterator();
        while(iter.hasNext()) {
            CycConstant modelElementTerm = (CycConstant) iter.next();
            if (cycAccess.isa(modelElementTerm,
                             cycAccess.getKnownConstantByName("UMLTransition"))) {
                transitionTerms.add(modelElementTerm);
                if (verbosity > 2)
                    Log.current.println("Extracted transition " + modelElementTerm.cyclify());
            }
        }
    }

    /**
     * Returns the procedure terms of the state machine.
     */
    protected void getProcedureTerms ()
        throws IOException, CycApiException {
        procedureTerms = new CycList();
        Iterator iter = stateVertexTerms.iterator();
        while (iter.hasNext()) {
            CycConstant stateVertexTerm = (CycConstant) iter.next();
            if (cycAccess.isa(stateVertexTerm,
                              cycAccess.getKnownConstantByName("UMLState"))) {
                CycConstant entryProcedureTerm =
                    (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlEntry"),
                                                    stateVertexTerm,
                                                    mtTerm);
                if (entryProcedureTerm != null) {
                    procedureTerms.add(entryProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + entryProcedureTerm.cyclify());
                }
                CycConstant exitProcedureTerm =
                    (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlExit"),
                                                    stateVertexTerm,
                                                    mtTerm);
                if (exitProcedureTerm != null) {
                    procedureTerms.add(exitProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + exitProcedureTerm.cyclify());
                }
                CycConstant doActivityProcedureTerm =
                    (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlDoActivity"),
                                                    stateVertexTerm,
                                                    mtTerm);
                if (doActivityProcedureTerm != null) {
                    procedureTerms.add(doActivityProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + doActivityProcedureTerm.cyclify());
                }
            }
        }
        iter = transitionTerms.iterator();
        while (iter.hasNext()) {
            CycConstant transitionTerm = (CycConstant) iter.next();
            CycConstant effectProcedureTerm =
                (CycConstant) cycAccess.getArg2(cycAccess.getKnownConstantByName("umlEffect"),
                                                transitionTerm,
                                                mtTerm);
            if (effectProcedureTerm != null) {
                procedureTerms.add(effectProcedureTerm);
                if (verbosity > 2)
                    Log.current.println("Extracted procedure " + effectProcedureTerm.cyclify());
            }
        }
    }

    /**
     * Sets verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

}