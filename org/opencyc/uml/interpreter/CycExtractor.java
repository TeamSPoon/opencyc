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
     * The quiet verbosity of this object's output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int QUIET_VERBOSITY = 0;

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
     * #$UMLStateMachineSpindleCollectorMt
     */
    public static final String umlStateMachineSpindleCollectorMtName =
            "UMLStateMachineSpindleCollectorMt";

    /**
     * the relevant inference microtheory
     */
    protected CycFort stateMachineDefinitionMtTerm;

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
     * the current state vertex name
     */
    protected String stateVertexName;

    /**
     * the current state vertex comment string
     */
    protected String stateVertexCommentString;

    /**
     * the entry procedure for the current state
     */
    protected Procedure entryProcedure;

    /**
     * the exit procedure for the current state
     */
    protected Procedure exitProcedure;

    /**
     * the doActivity procedure for the current state
     */
    protected Procedure doActivityProcedure;

    /**
     * the container of the current state vertex
     */
    protected CompositeState container;


    /**
     * Construct a new CycExtractor object given the CycAccess
     * server connection.
     *
     * @param cycAcess the given CycAccess Cyc KB server connection
     */
    public CycExtractor(CycAccess cycAccess) {
        this.cycAccess = cycAccess;
        verbosity = DEFAULT_VERBOSITY;
        stateMachineFactory = new StateMachineFactory();
    }

    /**
     * Construct a new CycExtractor object given the CycAccess
     * server connection.
     *
     * @param cycAcess the given CycAccess Cyc KB server connection
     * @param verbosity the verbosity of this object
     */
    public CycExtractor(CycAccess cycAccess, int verbosity) {
        this.cycAccess = cycAccess;
        this.verbosity = verbosity;
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
        this.stateMachineName = stateMachineName;
        stateMachineTerm = cycAccess.getConstantByName(stateMachineName);
        if (stateMachineTerm == null)
            throw new CycApiException("Expected state machine term not found for " +
                                      stateMachineName);
        stateMachineDefinitionMtTerm =
            (CycConstant) cycAccess.getArg2("umlStateMachineDefinition",
                                            stateMachineName,
                                            umlStateMachineSpindleCollectorMtName);
        if (stateMachineDefinitionMtTerm == null)
            throw new CycApiException("Expected umlStateMachineDefinition not found for\n  " +
                                      stateMachineTerm.cyclify() + " in " +
                                      umlStateMachineSpindleCollectorMtName);
        if (verbosity > 2)
            Log.current.println(stateMachineTerm.cyclify() + " is defined in " +
                                stateMachineDefinitionMtTerm.cyclify());
        stateMachine = extractStateMachine();
        extractProcedures();
        extractStates();
        extractTransitions();
        return stateMachine;
    }

    /**
     * Extracts the state machine from Cyc.
     */
    protected StateMachine extractStateMachine ()
            throws IOException, CycApiException, ClassNotFoundException {
        CycConstant namespaceTerm =
            (CycConstant) cycAccess.getArg2("umlNamespaceLink",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (namespaceTerm == null)
            throw new CycApiException("Expected umlNamespaceLink not found for \n  " +
                                      stateMachineName + " in " +
                                      stateMachineDefinitionMtTerm.cyclify());
        String namespaceName = namespaceTerm.toString();
        String commentString = cycAccess.getComment(stateMachineTerm);
        Object context = stateMachineDefinitionMtTerm;
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
            CycFort procedureDefinitionMt =
                (CycFort) cycAccess.getArg2("umlProcedureDefinition",
                                            procedureTerm,
                                            stateMachineDefinitionMtTerm);
            if (procedureDefinitionMt == null)
                throw new CycApiException("Expected umlProcedureDefinition not found for \n  " +
                                          procedureTerm.cyclify() + " in " +
                                          stateMachineDefinitionMtTerm.cyclify());
            String commentString = cycAccess.getComment(procedureTerm);
            if (commentString == null)
                throw new CycApiException("Expected comment not found for \n  " +
                                          procedureTerm.cyclify());
            String language =
                (String) cycAccess.getArg2("umlLanguage",
                                           procedureTerm,
                                           procedureDefinitionMt).toString();
            if (language == null)
                throw new CycApiException("Expected umlLanguage not found for \n  " +
                                          procedureTerm.cyclify() + " in " +
                                          procedureDefinitionMt.cyclify());
            Object body =
                cycAccess.getArg2("umlBody",
                                  procedureTerm,
                                  procedureDefinitionMt);
            if (body == null)
                throw new CycApiException("Expected umlBody not found for \n  " +
                                          procedureTerm.cyclify() + " in " +
                                          procedureDefinitionMt.cyclify());
            boolean isList =
                cycAccess.isa(procedureTerm, "UMLProcedure-IsList");
            Procedure procedure =
                stateMachineFactory.makeProcedure(procedureTerm.toString(),
                                                  commentString,
                                                  language,
                                                  body,
                                                  isList);
            procedureDictionary.put(procedureTerm, procedure);
            getArgumentAndResultTerms(procedureTerm, procedureDefinitionMt);
            Iterator iter2 = argumentTerms.iterator();
            while (iter2.hasNext()) {
                CycConstant argumentTerm = (CycConstant) iter2.next();
                Object object = cycAccess.getArg2("umlName",
                                               argumentTerm,
                                               procedureDefinitionMt);
                if (object == null ||
                    ! (object instanceof String))
                    throw new CycApiException("Expected umlName not found for \n  " +
                                              argumentTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                String name = (String) object;
                commentString = cycAccess.getComment(argumentTerm);
                if (commentString == null)
                    throw new CycApiException("Expected comment not found for \n  " +
                                              argumentTerm.cyclify());
                CycConstant typeTerm =
                    (CycConstant) cycAccess.getArg2("umlType",
                                                    argumentTerm,
                                                    procedureDefinitionMt);
                if (typeTerm == null)
                    throw new CycApiException("Expected umlType not found for \n  " +
                                              argumentTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                Class type = translateType(typeTerm);
                stateMachineFactory.addInputPinToProcedure(name,
                                                           commentString,
                                                           procedure,
                                                           type);
            }
            iter2 = resultTerms.iterator();
            while (iter2.hasNext()) {
                CycConstant resultTerm = (CycConstant) iter2.next();
                Object object =
                    cycAccess.getArg2("umlName",
                                      resultTerm,
                                      procedureDefinitionMt);
                if (object == null ||
                    ! (object instanceof String))
                    throw new CycApiException("Expected umlName not found for \n  " +
                                              resultTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                String name = (String) object;
                commentString = cycAccess.getComment(resultTerm);
                if (commentString == null)
                    throw new CycApiException("Expected comment not found for \n  " +
                                              resultTerm.cyclify());
                CycConstant typeTerm =
                    (CycConstant) cycAccess.getArg2("umlType",
                                                    resultTerm,
                                                    procedureDefinitionMt);
                if (typeTerm == null)
                    throw new CycApiException("Expected umlType not found for \n  " +
                                              resultTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
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
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveBoolean")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveBoolean");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveChar")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveChar");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveDouble")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveDouble");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveFloat")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveFloat");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveInt")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveInt");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveLong")))
            return Class.forName("org.opencyc.uml.statemachine.PrimitiveLong");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLString")))
            return Class.forName("java.lang.String");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLCycLExpression")))
            return Class.forName("org.opencyc.cycobject.CycList");
        //TODO add other types as required
        throw new RuntimeException("Unhandled typeTerm " + typeTerm.cyclify());
    }

    /**
     * Gets the argument and result terms for the given procedure term.
     *
     * @param procedureTerm the given procedure term
     * @param procedureDefinitionMt the given procedure definition microtheory
     */
    protected void getArgumentAndResultTerms (CycConstant procedureTerm,
                                              CycFort procedureDefinitionMt)
        throws IOException, CycApiException {
        argumentTerms = new CycList();
        CycList candidateArgumentTerms =
            cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlProcedureLink"),
                               procedureTerm,
                               procedureDefinitionMt);
        Iterator iter = candidateArgumentTerms.iterator();
        while (iter.hasNext()) {
            CycConstant term = (CycConstant) iter.next();
            if (cycAccess.isa(term, "UMLInputPin")) {
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
            if (cycAccess.isa(term, "UMLOutputPin")) {
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
        CycConstant topStateTerm =
            (CycConstant) cycAccess.getArg2("umlTop",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (topStateTerm == null)
            throw new CycApiException("Expected umlTop not found for \n  " +
                                      stateMachineTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
        Iterator iter = stateVertexTerms.iterator();
        while (iter.hasNext()) {
            CycConstant stateVertexTerm = (CycConstant) iter.next();
            stateVertexName = stateVertexTerm.toString();
            stateVertexCommentString =
                cycAccess.getComment(stateVertexTerm);
            CycConstant containerTerm =
                (CycConstant) cycAccess.getArg2("umlContainer",
                                                stateVertexTerm,
                                                stateMachineDefinitionMtTerm);
            if (containerTerm == null &&
                (! (stateVertexTerm.equals(topStateTerm))))
                throw new CycApiException("Expected umlContainer not found for \n  " +
                                          stateVertexTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
            container = (CompositeState) stateVertexDictionary.get(containerTerm);
            if (cycAccess.isa(stateVertexTerm, "UMLPseudoState")) {
                extractPseudoState(stateVertexTerm);
                continue;
            }
            getEntryExitDoActivityProcedures(stateVertexTerm);
            if (cycAccess.isa(stateVertexTerm, "UMLCompositeState"))
                extractCompositeState(stateVertexTerm);
            else if (cycAccess.isa(stateVertexTerm, "UMLSimpleState"))
                extractSimpleState(stateVertexTerm);
            else if (cycAccess.isa(stateVertexTerm, "UMLFinalState"))
                extractFinalState(stateVertexTerm);
            else
                throw new RuntimeException("Unhandled stateVertexTerm " +
                                           stateVertexTerm.cyclify());
        }
        State topState = (State) stateVertexDictionary.get(topStateTerm);
        if (topState == null)
            throw new CycApiException("Expected top state not found for \n  " +
                                      stateMachineTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
        stateMachine.setTop(topState);
    }

    /**
     * Extracts a composite state from Cyc given its Cyc term
     *
     * @param compositeStateTerm the given composite state term
     */
    protected void extractCompositeState (CycConstant compositeStateTerm)
        throws IOException, CycApiException {
        boolean isConcurrent =
            cycAccess.isa(compositeStateTerm, "UMLCompositeState-IsConcurrent");
        CompositeState compositeState =
            stateMachineFactory.makeCompositeState(stateVertexName,
                                                   stateVertexCommentString,
                                                   container,
                                                   entryProcedure,
                                                   exitProcedure,
                                                   doActivityProcedure,
                                                   isConcurrent);
        stateVertexDictionary.put(compositeStateTerm, compositeState);
        if (verbosity > 2)
            Log.current.println("Extracted composite state " + compositeStateTerm.cyclify());
    }

    /**
     * Extracts a pseudo state from Cyc given its Cyc term
     *
     * @param pseuodStateTerm the given pseudo state term
     */
    protected void extractPseudoState (CycConstant pseudoStateTerm)
        throws IOException, CycApiException {
        int kind;
        if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-Choice"))
            kind = PseudoState.PK_CHOICE;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-DeepHistory"))
            kind = PseudoState.PK_DEEPHISTORY;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-Fork"))
            kind = PseudoState.PK_FORK;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-Initial"))
            kind = PseudoState.PK_INITIAL;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-Join"))
            kind = PseudoState.PK_JOIN;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-Junction"))
            kind = PseudoState.PK_JUNCTION;
        else if (cycAccess.isa(pseudoStateTerm, "UMLPseudoState-ShallowHistory"))
            kind = PseudoState.PK_SHALLOWHISTORY;
        else
            throw new RuntimeException("Invalid kind of pseudoStateTerm " +
                                       pseudoStateTerm.cyclify());
        PseudoState pseudoState =
            stateMachineFactory.makePseudoState(stateVertexName,
                                                stateVertexCommentString,
                                                container,
                                                kind);
        stateVertexDictionary.put(pseudoStateTerm, pseudoState);
        if (verbosity > 2)
            Log.current.println("Extracted pseudo state " + pseudoStateTerm.cyclify());
    }

    /**
     * Extracts a simple state from Cyc given its Cyc term
     *
     * @param simpleStateTerm the given state vertex term
     */
    protected void extractSimpleState (CycConstant simpleStateTerm)
        throws IOException, CycApiException {
        SimpleState simpleState =
            stateMachineFactory.makeSimpleState(stateVertexName,
                                                stateVertexCommentString,
                                                container,
                                                entryProcedure,
                                                exitProcedure,
                                                doActivityProcedure);
        stateVertexDictionary.put(simpleStateTerm, simpleState);
        if (verbosity > 2)
            Log.current.println("Extracted simple state " + simpleStateTerm.cyclify());
    }

    /**
     * Extracts a final state from Cyc given its Cyc term
     *
     * @param finalStateTerm the given state vertex term
     */
    protected void extractFinalState (CycConstant finalStateTerm)
        throws IOException, CycApiException {
        FinalState finalState =
            stateMachineFactory.makeFinalState(stateVertexName,
                                               stateVertexCommentString,
                                               container,
                                               entryProcedure,
                                               exitProcedure,
                                               doActivityProcedure);
        stateVertexDictionary.put(finalStateTerm, finalState);
        if (verbosity > 2)
            Log.current.println("Extracted final state " + finalStateTerm.cyclify());
    }

    /**
     * Finds the state terms of the state machine term.
     */
    protected void getStateTerms ()
        throws IOException, CycApiException {
        stateVertexTerms = new CycList();
        CycConstant topStateTerm =
            (CycConstant) cycAccess.getArg2("umlTop",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (topStateTerm == null)
            throw new CycApiException("Expected umlTop not found for \n  " +
                                      stateMachineTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
        Stack stateTermStack = new Stack();
        stateTermStack.push(topStateTerm);
        while (! stateTermStack.isEmpty()) {
            CycConstant stateVertexTerm = (CycConstant) stateTermStack.pop();
            stateVertexTerms.add(stateVertexTerm);
            if (verbosity > 2)
                Log.current.println("Found state " + stateVertexTerm.cyclify());
            if (cycAccess.isa(stateVertexTerm, "UMLCompositeState")) {
                CycList subStates =
                    cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlContainer"),
                                       stateVertexTerm,
                                       stateMachineDefinitionMtTerm);
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
     * Gets the entry, exit and doActivity procedures for the given state term
     *
     * @param state the given state term
     */
    protected void getEntryExitDoActivityProcedures (CycConstant stateTerm)
            throws IOException, CycApiException {
        CycConstant entryProcedureTerm =
            (CycConstant) cycAccess.getArg2("umlEntry",
                                            stateTerm,
                                            stateMachineDefinitionMtTerm);
        entryProcedure = (Procedure) procedureDictionary.get(entryProcedureTerm);

        CycConstant exitProcedureTerm =
            (CycConstant) cycAccess.getArg2("umlExit",
                                            stateTerm,
                                            stateMachineDefinitionMtTerm);
        exitProcedure = (Procedure) procedureDictionary.get(exitProcedureTerm);

        CycConstant doActivityProcedureTerm =
            (CycConstant) cycAccess.getArg2("umlDoActivity",
                                            stateTerm,
                                            stateMachineDefinitionMtTerm);
        doActivityProcedure = (Procedure) procedureDictionary.get(doActivityProcedureTerm);
    }

    /**
     * Returns the transition terms of the state machine.
     */
    protected void getTransitionTerms ()
        throws IOException, CycApiException {
        CycList stateMachineReferents =
            cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlStateMachineLink"),
                               stateMachineTerm,
                               stateMachineDefinitionMtTerm);
        transitionTerms = new CycList();
        Iterator iter = stateMachineReferents.iterator();
        while(iter.hasNext()) {
            CycConstant modelElementTerm = (CycConstant) iter.next();
            if (cycAccess.isa(modelElementTerm, "UMLTransition")) {
                transitionTerms.add(modelElementTerm);
                if (verbosity > 2)
                    Log.current.println("Found transition " + modelElementTerm.cyclify());
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
            if (cycAccess.isa(stateVertexTerm,"UMLState")) {
                CycConstant entryProcedureTerm =
                    (CycConstant) cycAccess.getArg2("umlEntry",
                                                    stateVertexTerm,
                                                    stateMachineDefinitionMtTerm);
                if (entryProcedureTerm != null) {
                    procedureTerms.add(entryProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + entryProcedureTerm.cyclify());
                }
                CycConstant exitProcedureTerm =
                    (CycConstant) cycAccess.getArg2("umlExit",
                                                    stateVertexTerm,
                                                    stateMachineDefinitionMtTerm);
                if (exitProcedureTerm != null) {
                    procedureTerms.add(exitProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + exitProcedureTerm.cyclify());
                }
                CycConstant doActivityProcedureTerm =
                    (CycConstant) cycAccess.getArg2("umlDoActivity",
                                                    stateVertexTerm,
                                                    stateMachineDefinitionMtTerm);
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
                (CycConstant) cycAccess.getArg2("umlEffect",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (effectProcedureTerm != null) {
                procedureTerms.add(effectProcedureTerm);
                if (verbosity > 2)
                    Log.current.println("Extracted procedure " + effectProcedureTerm.cyclify());
                CycList gatherArgs = new CycList();
                gatherArgs.add(new Integer(2));
                gatherArgs.add(new Integer(3));
                CycList inputBindingTuples =
                    cycAccess.getPredicateValueTuplesInMt(transitionTerm,
                                                          cycAccess.getKnownConstantByName("umlEffectInputBinding"),
                                                          1,
                                                          gatherArgs,
                                                          stateMachineDefinitionMtTerm);
                Iterator inputBindingTuplesIter = inputBindingTuples.iterator();
                while (inputBindingTuplesIter.hasNext()) {
                    CycList inputBindingTuple = (CycList) inputBindingTuplesIter.next();

                }

            }
        }
    }

    /**
     * Extracts the transitions for the state machine from Cyc.
     */
    protected void extractTransitions ()
            throws IOException, CycApiException, ClassNotFoundException {
        Iterator iter = transitionTerms.iterator();
        while (iter.hasNext()) {
            CycConstant transitionTerm = (CycConstant) iter.next();
            String commentString =
                cycAccess.getComment(transitionTerm);
            String transitionName = transitionTerm.toString();
            commentString =  cycAccess.getComment(transitionTerm);
            CycConstant guardTerm =
                (CycConstant) cycAccess.getArg2("umlGuardLink",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            String guardExpressionLanguage = null;
            Object guardExpressionBody = null;
            if (guardTerm != null) {
                CycConstant booleanExpressionTerm =
                    (CycConstant) cycAccess.getArg2("umlExpressionLink",
                                                    guardTerm,
                                                    stateMachineDefinitionMtTerm);
                if (booleanExpressionTerm == null)
                    throw new CycApiException("Expected umlExpressionLink not found for \n  " +
                                              guardTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
                guardExpressionLanguage =
                    (String) cycAccess.getArg2("umlLanguage",
                                               booleanExpressionTerm,
                                               stateMachineDefinitionMtTerm).toString();
                if (guardExpressionLanguage == null)
                    throw new CycApiException("Expected umlLanguage not found for \n  " +
                                              booleanExpressionTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
                guardExpressionBody =
                    cycAccess.getArg2("umlBody",
                                      booleanExpressionTerm,
                                      stateMachineDefinitionMtTerm);
                if (guardExpressionBody == null)
                    throw new CycApiException("Expected umlBody not found for \n  " +
                                              booleanExpressionTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
            }
            Procedure effect = null;
            CycConstant effectTerm =
                (CycConstant) cycAccess.getArg2("umlEffect",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (effectTerm != null)
                effect = (Procedure) procedureDictionary.get(effectTerm);

            Event trigger = null;
            CycConstant triggerTerm =
                (CycConstant) cycAccess.getArg2("umlTrigger",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (triggerTerm != null)
                trigger = translateTermToEvent(triggerTerm);

            StateVertex source = null;
            CycConstant sourceTerm =
                (CycConstant) cycAccess.getArg2("umlSource",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (sourceTerm != null)
                source = (StateVertex) stateVertexDictionary.get(sourceTerm);

            StateVertex target = null;
            CycConstant targetTerm =
                (CycConstant) cycAccess.getArg2("umlTarget",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (targetTerm != null)
                target = (StateVertex) stateVertexDictionary.get(targetTerm);

            Transition transition =
                stateMachineFactory.makeTransition(transitionName,
                                                   commentString,
                                                   guardExpressionLanguage,
                                                   guardExpressionBody,
                                                   effect,
                                                   trigger,
                                                   source,
                                                   target);
            transitionDictionary.put(transitionTerm, transition);
            if (verbosity > 2)
                Log.current.println("Extracted transition " + transitionTerm.cyclify());
        }
    }

    /**
     * Returns the Event corresponding to the given event term
     *
     * @param eventTerm the given event term
     * @return the Event corresponding to the given event term
     */
    protected Event translateTermToEvent (CycConstant eventTerm)
            throws IOException, CycApiException, ClassNotFoundException {
        if (cycAccess.isa(eventTerm, "UMLCallEvent"))
            return new CallEvent();
        if (cycAccess.isa(eventTerm, "UMLChangeEvent"))
            return new ChangeEvent();
        if (cycAccess.isa(eventTerm, "UMLCompletionEvent"))
            return new CompletionEvent();
        if (cycAccess.isa(eventTerm, "UMLSignalEvent"))
            return new SignalEvent();
        if (cycAccess.isa(eventTerm, "UMLTimeEvent"))
            return new TimeEvent();
        throw new RuntimeException("Unknown event type " + eventTerm.cyclify());
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