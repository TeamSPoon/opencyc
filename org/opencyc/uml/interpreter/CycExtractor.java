package org.opencyc.uml.interpreter;

import java.io.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.uml.core.*;
import org.opencyc.uml.commonbehavior.*;
import org.opencyc.uml.datatypes.Multiplicity;
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
    protected CycFort stateMachineTerm;

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
     * the input pin terms for the current procedure
     */
    protected CycList inputPinTerms;

    /**
     * the dictionary with the key an inputPinTerm and the value its InputPin
     */
    protected HashMap inputPinDictionary = new HashMap();

    /**
     * the output pin terms for the current procedure
     */
    protected CycList outputPinTerms;

    /**
     * the dictionary with the key an outputPinTerm and the value its OutputPin
     */
    protected HashMap outputPinDictionary = new HashMap();

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
     * the dictionary of state variables whose key is the state variable term and whose
     * value is the StateVariable instance
     */
    protected HashMap stateVariableDictionary = new HashMap();

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
            (CycFort) cycAccess.getArg2("umlStateMachineDefinition",
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
        extractContextClassifier();
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
        CycFort namespaceTerm =
            (CycFort) cycAccess.getArg2("umlNamespaceLink",
                                        stateMachineTerm,
                                        stateMachineDefinitionMtTerm);
        if (namespaceTerm == null)
            throw new CycApiException("Expected umlNamespaceLink not found for \n  " +
                                      stateMachineName + " in " +
                                      stateMachineDefinitionMtTerm.cyclify());
        String namespaceName = namespaceTerm.toString();
        String commentString = cycAccess.getComment(stateMachineTerm);
        return stateMachineFactory.makeStateMachine(namespaceName,
                                                    stateMachineName,
                                                    commentString);
    }

    /**
     * Extracts the context classifier from Cyc.
     */
    protected void extractContextClassifier ()
        throws IOException, CycApiException, ClassNotFoundException {
        CycFort classifierTerm =
                (CycFort) cycAccess.getArg2("umlContext",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (classifierTerm == null)
            throw new CycApiException("Expected umlContext not found for \n  " +
                                      stateMachineName + " in " +
                                      stateMachineDefinitionMtTerm.cyclify());
        String commentString = cycAccess.getComment(classifierTerm);
        if (commentString == null)
            throw new CycApiException("Expected comment not found for \n  " +
                                      classifierTerm.cyclify() + " in " +
                                      stateMachineDefinitionMtTerm.cyclify());
        stateMachineFactory.associateClassifierToStateMachine(classifierTerm.toString(),
                                                              commentString);
        // get the state variables
        CycList stateVariableTerms =
            cycAccess.getArg2s("umlFeature",
                               classifierTerm,
                               stateMachineDefinitionMtTerm);
        Iterator iter = stateVariableTerms.iterator();
        while (iter.hasNext()) {
            CycFort stateVariableTerm = (CycFort) iter.next();
            commentString = cycAccess.getComment(stateVariableTerm);
            CycFort typeTerm =
                    (CycFort) cycAccess.getArg2("umlType",
                                                stateVariableTerm,
                                                stateMachineDefinitionMtTerm);
            if (typeTerm == null)
                throw new CycApiException("Expected umlType not found for \n  " +
                                          stateVariableTerm.cyclify() + " in " +
                                          stateMachineDefinitionMtTerm.cyclify());
            Object type = translateType(typeTerm);
            Expression initialValue = null;
            CycFort initialExpressionTerm =
                    (CycFort) cycAccess.getArg2("umlInitialExpression",
                                                stateVariableTerm,
                                                stateMachineDefinitionMtTerm);
            if (initialExpressionTerm != null) {
                initialValue = new Expression();
                String language =
                        (String) cycAccess.getArg2("umlLanguage",
                                                    initialExpressionTerm,
                                                    stateMachineDefinitionMtTerm);
                if (language == null)
                    throw new CycApiException("Expected umlLanguage not found for \n  " +
                                              initialExpressionTerm.cyclify() + " in " +
                                              stateMachineDefinitionMtTerm.cyclify());
                initialValue.setLanguage(language);
                CycList body =
                        (CycList) cycAccess.getArg2("umlBody",
                                                    initialExpressionTerm,
                                                    stateMachineDefinitionMtTerm);
                if (body == null)
                    throw new CycApiException("Expected umlBody not found for \n  " +
                                              initialExpressionTerm.cyclify() + " in " +
                                              stateMachineDefinitionMtTerm.cyclify());
                initialValue.setBody(body);
            }
            StateVariable stateVariable =
                stateMachineFactory.addStateVariableToClassifier(stateVariableTerm.toString(),
                                                                 commentString,
                                                                 StructuralFeature.SK_INSTANCE,
                                                                 type,
                                                                 StructuralFeature.CK_CHANGEABLE,
                                                                 new Multiplicity(1, 1, false),
                                                                 StructuralFeature.OK_UNORDERED,
                                                                 initialValue);
            stateVariableDictionary.put(stateVariableTerm, stateVariable);
        }
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
            CycFort procedureTerm = (CycFort) iter.next();
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
            getPinTerms(procedureTerm, procedureDefinitionMt);
            Iterator iter2 = inputPinTerms.iterator();
            while (iter2.hasNext()) {
                CycFort inputPinTerm = (CycFort) iter2.next();
                Object object = cycAccess.getArg2("umlName",
                                               inputPinTerm,
                                               procedureDefinitionMt);
                if (object == null ||
                    ! (object instanceof String))
                    throw new CycApiException("Expected umlName not found for \n  " +
                                              inputPinTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                String name = (String) object;
                commentString = cycAccess.getComment(inputPinTerm);
                if (commentString == null)
                    throw new CycApiException("Expected comment not found for \n  " +
                                              inputPinTerm.cyclify());
                CycFort typeTerm =
                    (CycFort) cycAccess.getArg2("umlType",
                                                inputPinTerm,
                                                procedureDefinitionMt);
                if (typeTerm == null)
                    throw new CycApiException("Expected umlType not found for \n  " +
                                              inputPinTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                java.lang.Class type = translateType(typeTerm);
                stateMachineFactory.addInputPinToProcedure(name,
                                                           commentString,
                                                           procedure,
                                                           type);
            }
            iter2 = outputPinTerms.iterator();
            while (iter2.hasNext()) {
                CycFort outputPinTerm = (CycFort) iter2.next();
                Object object =
                    cycAccess.getArg2("umlName",
                                      outputPinTerm,
                                      procedureDefinitionMt);
                if (object == null ||
                    ! (object instanceof String))
                    throw new CycApiException("Expected umlName not found for \n  " +
                                              outputPinTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                String name = (String) object;
                commentString = cycAccess.getComment(outputPinTerm);
                if (commentString == null)
                    throw new CycApiException("Expected comment not found for \n  " +
                                              outputPinTerm.cyclify());
                CycFort typeTerm =
                    (CycFort) cycAccess.getArg2("umlType",
                                                    outputPinTerm,
                                                    procedureDefinitionMt);
                if (typeTerm == null)
                    throw new CycApiException("Expected umlType not found for \n  " +
                                              outputPinTerm.cyclify() + " in " +
                                              procedureDefinitionMt.cyclify());
                java.lang.Class type = translateType(typeTerm);
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
    protected java.lang.Class translateType (CycFort typeTerm)
        throws IOException, CycApiException, ClassNotFoundException {
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveBoolean")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveBoolean");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveChar")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveChar");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveDouble")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveDouble");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveFloat")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveFloat");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveInt")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveInt");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLPrimitiveLong")))
            return java.lang.Class.forName("org.opencyc.uml.statemachine.PrimitiveLong");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLString")))
            return java.lang.Class.forName("java.lang.String");
        if (typeTerm.equals(cycAccess.getKnownConstantByName("UMLCycLExpression")))
            return java.lang.Class.forName("org.opencyc.cycobject.CycList");
        //TODO add other types as required
        throw new RuntimeException("Unhandled typeTerm " + typeTerm.cyclify());
    }

    /**
     * Gets the input pin terms and the output pin terms for the given procedure term.
     *
     * @param procedureTerm the given procedure term
     * @param procedureDefinitionMt the given procedure definition microtheory
     */
    protected void getPinTerms (CycFort procedureTerm,
                                CycFort procedureDefinitionMt)
        throws IOException, CycApiException {
        inputPinTerms = new CycList();
        CycList candidateInputPinTerms =
            cycAccess.getArg1s(cycAccess.getKnownConstantByName("umlProcedureLink"),
                               procedureTerm,
                               procedureDefinitionMt);
        Iterator iter = candidateInputPinTerms.iterator();
        while (iter.hasNext()) {
            CycFort term = (CycFort) iter.next();
            if (cycAccess.isa(term, "UMLInputPin")) {
                if (verbosity > 2)
                    Log.current.println("Extracted InputPin " + term.cyclify() +
                                        " for " + procedureTerm.cyclify());
                inputPinTerms.add(term);
            }
        }
        outputPinTerms = new CycList();
        iter = outputPinTerms.iterator();
        while (iter.hasNext()) {
            CycFort term = (CycFort) iter.next();
            if (cycAccess.isa(term, "UMLOutputPin")) {
                if (verbosity > 2)
                    Log.current.println("Extracted OutputPin " + term.cyclify() +
                                        " for " + procedureTerm.cyclify());
                inputPinTerms.add(term);
            }
        }
    }

    /**
     * Extracts the states for the state machine from Cyc.
     */
    protected void extractStates ()
            throws IOException, CycApiException, ClassNotFoundException {
        CycFort topStateTerm =
            (CycFort) cycAccess.getArg2("umlTop",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (topStateTerm == null)
            throw new CycApiException("Expected umlTop not found for \n  " +
                                      stateMachineTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
        Iterator iter = stateVertexTerms.iterator();
        while (iter.hasNext()) {
            CycFort stateVertexTerm = (CycFort) iter.next();
            stateVertexName = stateVertexTerm.toString();
            stateVertexCommentString =
                cycAccess.getComment(stateVertexTerm);
            CycFort containerTerm =
                (CycFort) cycAccess.getArg2("umlContainer",
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
    protected void extractCompositeState (CycFort compositeStateTerm)
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
    protected void extractPseudoState (CycFort pseudoStateTerm)
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
    protected void extractSimpleState (CycFort simpleStateTerm)
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
    protected void extractFinalState (CycFort finalStateTerm)
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
        CycFort topStateTerm =
            (CycFort) cycAccess.getArg2("umlTop",
                                            stateMachineTerm,
                                            stateMachineDefinitionMtTerm);
        if (topStateTerm == null)
            throw new CycApiException("Expected umlTop not found for \n  " +
                                      stateMachineTerm.cyclify() + " in " + stateMachineDefinitionMtTerm.cyclify());
        Stack stateTermStack = new Stack();
        stateTermStack.push(topStateTerm);
        while (! stateTermStack.isEmpty()) {
            CycFort stateVertexTerm = (CycFort) stateTermStack.pop();
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
                    if (object instanceof CycFort)
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
    protected void getEntryExitDoActivityProcedures (CycFort stateTerm)
            throws IOException, CycApiException {
        CycFort entryProcedureTerm =
            (CycFort) cycAccess.getArg2("umlEntry",
                                            stateTerm,
                                            stateMachineDefinitionMtTerm);
        entryProcedure = (Procedure) procedureDictionary.get(entryProcedureTerm);

        CycFort exitProcedureTerm =
            (CycFort) cycAccess.getArg2("umlExit",
                                            stateTerm,
                                            stateMachineDefinitionMtTerm);
        exitProcedure = (Procedure) procedureDictionary.get(exitProcedureTerm);

        CycFort doActivityProcedureTerm =
            (CycFort) cycAccess.getArg2("umlDoActivity",
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
            CycFort modelElementTerm = (CycFort) iter.next();
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
        getProcedureTermsFromStates();
        getProcedureTermsFromTransitions();
    }

    /**
     * Returns the procedure terms of the state machine from the
     * calling states
     */
    protected void getProcedureTermsFromStates ()
        throws IOException, CycApiException {
        Iterator iter = stateVertexTerms.iterator();
        while (iter.hasNext()) {
            CycFort stateVertexTerm = (CycFort) iter.next();
            if (cycAccess.isa(stateVertexTerm,"UMLState")) {
                CycFort entryProcedureTerm =
                    (CycFort) cycAccess.getArg2("umlEntry",
                                                 stateVertexTerm,
                                                stateMachineDefinitionMtTerm);
                if (entryProcedureTerm != null) {
                    procedureTerms.add(entryProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + entryProcedureTerm.cyclify());
                    extractEntryProcedurePinBindings(stateVertexTerm);
                }
                CycFort exitProcedureTerm =
                    (CycFort) cycAccess.getArg2("umlExit",
                                                stateVertexTerm,
                                                stateMachineDefinitionMtTerm);
                if (exitProcedureTerm != null) {
                    procedureTerms.add(exitProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + exitProcedureTerm.cyclify());
                    extractExitProcedurePinBindings(stateVertexTerm);
                }
                CycFort doActivityProcedureTerm =
                    (CycFort) cycAccess.getArg2("umlDoActivity",
                                                stateVertexTerm,
                                                stateMachineDefinitionMtTerm);
                if (doActivityProcedureTerm != null) {
                    procedureTerms.add(doActivityProcedureTerm);
                    if (verbosity > 2)
                        Log.current.println("Extracted procedure " + doActivityProcedureTerm.cyclify());
                    extractExitProcedurePinBindings(stateVertexTerm);
                }
            }
        }
    }

    /**
     * Extracts the state entry procedure pin bindings given the state term
     *
     * @param stateVertexTerm the given state term
     */
    protected void extractEntryProcedurePinBindings (CycFort stateVertexTerm)
        throws IOException, CycApiException {

        CycList inputPinBindings =
            (CycList) cycAccess.getArg2s("umlEntryInputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator inputPinBindingsIter = inputPinBindings.iterator();
        while (inputPinBindingsIter.hasNext()) {
            CycFort inputPinBindingTerm = (CycFort) inputPinBindingsIter.next();
            Object[] result = extractInputBindings(inputPinBindingTerm);
            InputPin inputPin = (InputPin) result[0];
            Object boundInputValueExpression = result[1];
            if (verbosity > 2)
                Log.current.println("Extracted input pin binding " + inputPin.toString() +
                                    "\n  bound to " + boundInputValueExpression.toString() +
                                    "\n  called from state entry " + stateVertexTerm.toString());
            stateMachineFactory.addEntryInputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                     inputPin,
                                                     boundInputValueExpression);
        }
        CycList outputPinBindings =
            (CycList) cycAccess.getArg2s("umlEntryOutputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator outputPinBindingsIter = outputPinBindings.iterator();
        while (outputPinBindingsIter.hasNext()) {
            CycFort outputPinBindingTerm = (CycFort) outputPinBindingsIter.next();
            Object[] result = extractOutputBindings(outputPinBindingTerm);
            OutputPin outputPin = (OutputPin) result[0];
            StateVariable stateVariable = (StateVariable) result[1];
            if (verbosity > 2)
                Log.current.println("Extracted output pin binding " + outputPin.toString() +
                                    "\n  bound to state variable " + stateVariable.toString() +
                                    "\n  called from state entry " + stateVertexTerm.toString());
            stateMachineFactory.addEntryOutputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                      outputPin,
                                                      stateVariable);
        }

    }

    /**
     * Extracts the state exit procedure pin bindings given the state term
     *
     * @param stateVertexTerm the given state term
     */
    protected void extractExitProcedurePinBindings (CycFort stateVertexTerm)
        throws IOException, CycApiException {

        CycList inputPinBindings =
            (CycList) cycAccess.getArg2s("umlExitInputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator inputPinBindingsIter = inputPinBindings.iterator();
        while (inputPinBindingsIter.hasNext()) {
            CycFort inputPinBindingTerm = (CycFort) inputPinBindingsIter.next();
            Object[] result = extractInputBindings(inputPinBindingTerm);
            InputPin inputPin = (InputPin) result[0];
            Object boundInputValueExpression = result[1];
            if (verbosity > 2)
                Log.current.println("Extracted input pin binding " + inputPin.toString() +
                                    "\n  bound to " + boundInputValueExpression.toString() +
                                    "\n  called from state exit " + stateVertexTerm.toString());
            stateMachineFactory.addExitInputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                    inputPin,
                                                    boundInputValueExpression);
        }
        CycList outputPinBindings =
            (CycList) cycAccess.getArg2s("umlExitOutputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator outputPinBindingsIter = outputPinBindings.iterator();
        while (outputPinBindingsIter.hasNext()) {
            CycFort outputPinBindingTerm = (CycFort) outputPinBindingsIter.next();
            Object[] result = extractOutputBindings(outputPinBindingTerm);
            OutputPin outputPin = (OutputPin) result[0];
            StateVariable stateVariable = (StateVariable) result[1];
            if (verbosity > 2)
                Log.current.println("Extracted output pin binding " + outputPin.toString() +
                                    "\n  bound to state variable " + stateVariable.toString() +
                                    "\n  called from state exit " + stateVertexTerm.toString());
            stateMachineFactory.addExitOutputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                     outputPin,
                                                     stateVariable);
        }

    }

    /**
     * Extracts the state doActivity procedure pin bindings given the state term
     *
     * @param stateVertexTerm the given state term
     */
    protected void extractDoActivityProcedurePinBindings (CycFort stateVertexTerm)
        throws IOException, CycApiException {

        CycList inputPinBindings =
            (CycList) cycAccess.getArg2s("umlDoActivityInputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator inputPinBindingsIter = inputPinBindings.iterator();
        while (inputPinBindingsIter.hasNext()) {
            CycFort inputPinBindingTerm = (CycFort) inputPinBindingsIter.next();
            Object[] result = extractInputBindings(inputPinBindingTerm);
            InputPin inputPin = (InputPin) result[0];
            Object boundInputValueExpression = result[1];
            if (verbosity > 2)
                Log.current.println("Extracted input pin binding " + inputPin.toString() +
                                    "\n  bound to " + boundInputValueExpression.toString() +
                                    "\n  called from state doActivity " + stateVertexTerm.toString());
            stateMachineFactory.addExitInputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                    inputPin,
                                                    boundInputValueExpression);
        }
        CycList outputPinBindings =
            (CycList) cycAccess.getArg2s("umlDoActivityOutputPinBinding",
                                         stateVertexTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator outputPinBindingsIter = outputPinBindings.iterator();
        while (outputPinBindingsIter.hasNext()) {
            CycFort outputPinBindingTerm = (CycFort) outputPinBindingsIter.next();
            Object[] result = extractOutputBindings(outputPinBindingTerm);
            OutputPin outputPin = (OutputPin) result[0];
            StateVariable stateVariable = (StateVariable) result[1];
            if (verbosity > 2)
                Log.current.println("Extracted output pin binding " + outputPin.toString() +
                                    "\n  bound to state variable " + stateVariable.toString() +
                                    "\n  called from state doActivity " + stateVertexTerm.toString());
            stateMachineFactory.addExitOutputBinding((State) stateVertexDictionary.get(stateVertexTerm),
                                                     outputPin,
                                                     stateVariable);
        }

    }

    /**
     * Returns the procedure terms of the state machine from the
     * calling transitions
     */
    protected void getProcedureTermsFromTransitions ()
        throws IOException, CycApiException {
        Iterator iter = transitionTerms.iterator();
        while (iter.hasNext()) {
            CycFort transitionTerm = (CycFort) iter.next();
            CycFort effectProcedureTerm =
                (CycFort) cycAccess.getArg2("umlEffect",
                                            transitionTerm,
                                            stateMachineDefinitionMtTerm);
            if (effectProcedureTerm != null) {
                procedureTerms.add(effectProcedureTerm);
                if (verbosity > 2)
                    Log.current.println("Extracted procedure " + effectProcedureTerm.cyclify());
                extractEffectProcedurePinBindings(transitionTerm);
            }
        }
    }

    /**
     * Extracts the transition effect procedure pin bindings given the transition term
     *
     * @param transitionTerm the given transition term
     */
    protected void extractEffectProcedurePinBindings (CycFort transitionTerm)
        throws IOException, CycApiException {

        CycList inputPinBindings =
            (CycList) cycAccess.getArg2s("umlEffectInputPinBinding",
                                         transitionTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator inputPinBindingsIter = inputPinBindings.iterator();
        while (inputPinBindingsIter.hasNext()) {
            CycFort inputPinBindingTerm = (CycFort) inputPinBindingsIter.next();
            Object[] result = extractInputBindings(inputPinBindingTerm);
            InputPin inputPin = (InputPin) result[0];
            Object boundInputValueExpression = result[1];
            if (verbosity > 2)
                Log.current.println("Extracted input pin binding " + inputPin.toString() +
                                    "\n  bound to " + boundInputValueExpression.toString() +
                                    "\n  called from transition " + transitionTerm.toString());
            stateMachineFactory.addEffectInputBinding((Transition) transitionDictionary.get(transitionTerm),
                                                      inputPin,
                                                      boundInputValueExpression);
        }
        CycList outputPinBindings =
            (CycList) cycAccess.getArg2s("umlEffectOutputPinBinding",
                                         transitionTerm,
                                         stateMachineDefinitionMtTerm);
        Iterator outputPinBindingsIter = outputPinBindings.iterator();
        while (outputPinBindingsIter.hasNext()) {
            CycFort outputPinBindingTerm = (CycFort) outputPinBindingsIter.next();
            Object[] result = extractOutputBindings(outputPinBindingTerm);
            OutputPin outputPin = (OutputPin) result[0];
            StateVariable stateVariable = (StateVariable) result[1];
            if (verbosity > 2)
                Log.current.println("Extracted output pin binding " + outputPin.toString() +
                                    "\n  bound to state variable " + stateVariable.toString() +
                                    "\n  called from transition " + transitionTerm.toString());
            stateMachineFactory.addEffectOutputBinding((Transition) transitionDictionary.get(transitionTerm),
                                                       outputPin,
                                                       stateVariable);
        }

    }

    /**
     * Extracts the input bindings for a procedure call given an input pin binding term
     *
     * @param inputPinBindingTerm the input pin binding term
     * @param return an Object array of two objects of which the first is the inputPin
     * and the second is the boundInputValueExpression
     */
    protected Object [] extractInputBindings (CycFort inputPinBindingTerm)
        throws IOException, CycApiException {
        CycFort inputPinTerm =
            (CycFort) cycAccess.getArg2("umlBoundInputPin",
                                        inputPinBindingTerm,
                                        stateMachineDefinitionMtTerm);
        InputPin inputPin = (InputPin) inputPinDictionary.get(inputPinTerm);
        if (inputPin == null) {
            throw new CycApiException("Expected InputPin not found for \n  " +
                                      inputPinTerm.cyclify());
        }
        Object boundInputValueExpressionTerm =
            (CycFort) cycAccess.getArg2("umlBoundInputValueExpression",
                                        inputPinBindingTerm,
                                        stateMachineDefinitionMtTerm);
        Object boundInputValueExpression = null;
        if (boundInputValueExpressionTerm instanceof CycConstant) {
            boundInputValueExpression =
                stateVariableDictionary.get(boundInputValueExpressionTerm);
            if (boundInputValueExpression == null)
                throw new CycApiException("Expected StateVariable not found for \n  " +
                                          ((CycConstant) boundInputValueExpressionTerm).cyclify());
        }
        else if (boundInputValueExpressionTerm instanceof CycFort)
            //TODO handle the evaluation of this expression
            throw new CycApiException("Expression evaluation not yet implemented for \n  " +
                                      ((CycFort) boundInputValueExpressionTerm).cyclify());
        else if (boundInputValueExpressionTerm instanceof CycList)
            //TODO handle the evaluation of this expression
            throw new CycApiException("Expression evaluation not yet implemented for \n  " +
                                      ((CycList) boundInputValueExpressionTerm).cyclify());
        else
            // The Cyc api transforms CycL literals to their appropriate java types.
            boundInputValueExpression = boundInputValueExpressionTerm;
        Object [] result = {inputPin, boundInputValueExpression};
        return result;
    }

    /**
     * Extracts the output bindings for a procedure call given an output pin binding term
     *
     * @param outputPinBindingTerm the output pin binding term
     * @param return an Object array of two objects of which the first is the outputPin
     * and the second is the boundOutputStateVariable
     */
    protected Object [] extractOutputBindings (CycFort outputPinBindingTerm)
        throws IOException, CycApiException {
        CycFort outputPinTerm =
            (CycFort) cycAccess.getArg2("umlBoundOutputPin",
                                        outputPinBindingTerm,
                                        stateMachineDefinitionMtTerm);
        OutputPin outputPin = (OutputPin) outputPinDictionary.get(outputPinTerm);
        if (outputPin == null) {
            throw new CycApiException("Expected OutputPin not found for \n  " +
                                      outputPinTerm.cyclify());
        }
        CycFort boundOutputStateVariableTerm =
            (CycFort) cycAccess.getArg2("umlBoundOutputStateVariable",
                                        outputPinBindingTerm,
                                        stateMachineDefinitionMtTerm);
        StateVariable stateVariable =
            (StateVariable) stateVariableDictionary.get(boundOutputStateVariableTerm);
        if (stateVariable == null)
            throw new CycApiException("Expected StateVariable not found for \n  " +
                                      ((CycConstant) boundOutputStateVariableTerm).cyclify());
        Object [] result = {outputPin, stateVariable};
        return result;
    }

    /**
     * Extracts the transitions for the state machine from Cyc.
     */
    protected void extractTransitions ()
            throws IOException, CycApiException, ClassNotFoundException {
        Iterator iter = transitionTerms.iterator();
        while (iter.hasNext()) {
            CycFort transitionTerm = (CycFort) iter.next();
            String commentString =
                cycAccess.getComment(transitionTerm);
            String transitionName = transitionTerm.toString();
            commentString =  cycAccess.getComment(transitionTerm);
            CycFort guardTerm =
                (CycFort) cycAccess.getArg2("umlGuardLink",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            String guardExpressionLanguage = null;
            Object guardExpressionBody = null;
            if (guardTerm != null) {
                CycFort booleanExpressionTerm =
                    (CycFort) cycAccess.getArg2("umlExpressionLink",
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
            CycFort effectTerm =
                (CycFort) cycAccess.getArg2("umlEffect",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (effectTerm != null)
                effect = (Procedure) procedureDictionary.get(effectTerm);

            Event trigger = null;
            CycFort triggerTerm =
                (CycFort) cycAccess.getArg2("umlTrigger",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (triggerTerm != null)
                trigger = translateTermToEvent(triggerTerm);

            StateVertex source = null;
            CycFort sourceTerm =
                (CycFort) cycAccess.getArg2("umlSource",
                                                transitionTerm,
                                                stateMachineDefinitionMtTerm);
            if (sourceTerm != null)
                source = (StateVertex) stateVertexDictionary.get(sourceTerm);

            StateVertex target = null;
            CycFort targetTerm =
                (CycFort) cycAccess.getArg2("umlTarget",
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
    protected Event translateTermToEvent (CycFort eventTerm)
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