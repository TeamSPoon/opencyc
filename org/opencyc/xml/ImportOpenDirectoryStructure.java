package org.opencyc.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Imports DAML xml content for the Open Directory Structure.<p>
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

public class ImportOpenDirectoryStructure extends ImportDaml {

    /**
     * the list of DAML documents and import microtheories
     */
    protected ArrayList damlDocInfos = new ArrayList();

    /**
     * reference to the GatherOpenDirectoryTitles object
     */
    protected GatherOpenDirectoryTitles gatherOpenDirectoryTitles;


    /**
     * Constructs a new ImportOpenDirectoryStructure object.
     */
    public ImportOpenDirectoryStructure()
        throws IOException, UnknownHostException, CycApiException {
    }

    /**
     * Provides the main method for the ImportOdpDaml application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Log.makeLog("import-opend-directory-daml.log");
        ImportOpenDirectoryStructure importOpenDirectoryStructure = null;
        try {
            importOpenDirectoryStructure = new ImportOpenDirectoryStructure();
            importOpenDirectoryStructure.doImport();
        }
        catch (Exception e) {
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Performs import of the Open Directory Structure.
     */
    protected void doImport()
        throws IOException, CycApiException {
        if (verbosity > 0)
            Log.current.println("Import Open Directory Structure starting");
        initializeCommonOntologyNicknames();
        initializeOntologyNicknames();

        gatherOdpTitles();

        String localHostName = InetAddress.getLocalHost().getHostName();
        Log.current.println("Connecting to Cyc server from " + localHostName);
        if (localHostName.equals("crapgame.cyc.com")) {
            cycAccess = new CycAccess("localhost",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else if (localHostName.equals("thinker")) {
            cycAccess = new CycAccess("TURING",
                                      3600,
                                      CycConnection.DEFAULT_COMMUNICATION_MODE,
                                      true);
        }
        else {
            cycAccess = new CycAccess();
        }
        initializeCommonDamlVocabulary();
        initializeDamlVocabulary();

        initializeCommonMappedTerms();
        initializeMappedTerms();

        kbSubsetCollectionName = "DamlOdpFort";

        importDaml();
    }
    /**
     * Gathers the ODP topic titles.
     *
     * Win32 - file:///H:/OpenCyc/open-directory-structure.daml
     *
     * Linux - file:///home/reed/opencyc/open-directory.daml
     */
    protected void gatherOdpTitles () throws IOException {
        gatherOpenDirectoryTitles =
            new GatherOpenDirectoryTitles(ontologyNicknames);
        gatherOpenDirectoryTitles.gatherTitles("file:///home/reed/opencyc/open-directory-structure.daml");
    }

    /**
     * Import the ODP DAML ontologies into Cyc.
     */
    protected void importDaml ()
        throws IOException, UnknownHostException, CycApiException {

        initializeDocumentsToImport();
        //importDaml.actuallyImport = false;
        for (int i = 0; i < damlDocInfos.size(); i++) {
            DamlDocInfo damlDocInfo = (DamlDocInfo) damlDocInfos.get(i);
            String damlPath = damlDocInfo.getDamlPath();
            String characterEncoding = damlDocInfo.getCharacterEncoding();
            String importMt = damlDocInfo.getImportMt();
            initializeDamlOntologyMt(importMt);
            initialize();
            if (characterEncoding == null)
                importDaml(damlPath, importMt);
            else
                importDaml(damlPath, importMt, characterEncoding);
        }
    }

    /**
     * Initializes the documents to import.
     */
    protected void initializeDocumentsToImport () {
        // 0
        damlDocInfos.add(new DamlDocInfo("file:///home/reed/opencyc/open-directory.daml",
                                         null,
                                         "OpenDirectoryStructureMt"));
        // 1
        damlDocInfos.add(new DamlDocInfo("file:///home/reed/opencyc/open-directory-structure.daml",
                                         "UTF-8",
                                         "OpenDirectoryStructureMt"));
    }

    /**
     * Initializes the Ontology nicknames mapping.
     */
    protected void initializeOntologyNicknames () {
        // DMOZ stale reference
        ontologyNicknames.put("http://www.w3.org/TR/RDF/", "rdf");
        ontologyNicknames.put("http://purl.org/dc/elements/1.1", "dublincore");
        ontologyNicknames.put("http://purl.org/dc/elements/1.0/", "dublincore");
        ontologyNicknames.put("http://dmoz.org/rdf", "dmoz");
        ontologyNicknames.put("http://dmoz.org/rdf/structure.example.txt", "dmoz");
        ontologyNicknames.put("file:///home/reed/opencyc/open-directory-structure.daml", "dmoz");
        ontologyNicknames.put("file:/home/reed/opencyc/open-directory-structure.daml", "dmoz");
        ontologyNicknames.put("file:/home/reed/opencyc/open-directory-structure", "dmoz");
        ontologyNicknames.put("http://opencyc.sourceforge.net/open-directory", "dmoz");
        ontologyNicknames.put("file:///home/reed/opencyc/open-directory.daml", "dmoz");
        ontologyNicknames.put("file:/home/reed/opencyc", "dmoz");
        ontologyNicknames.put("file:/home/reed/opencyc/open-directory.daml", "dmoz");

    }

    protected void initializeMappedTerms ()
        throws IOException, UnknownHostException, CycApiException {
        assertArgumentMapping("dmoz:narrow",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow",
                              "genls",
                              "(2 1)");

        assertArgumentMapping("dmoz:narrow1",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow1",
                              "genls",
                              "(2 1)");

        assertArgumentMapping("dmoz:narrow2",
                              "http://opencyc.sourceforge.net/open-directory",
                              "http://opencyc.sourceforge.net/open-directory#narrow2",
                              "genls",
                              "(2 1)");

        assertMapping("dmoz:related",
                      "http://opencyc.sourceforge.net/open-directory",
                      "http://opencyc.sourceforge.net/open-directory#related",
                      "conceptuallyRelated");



        // Get the above mappings plus any previously defined in the KB.
        getMappings();
    }


    /**
     * Initializes the DAML ontology vocabulary if not present.
     */
    protected void initializeDamlVocabulary ()
        throws IOException, UnknownHostException, CycApiException {
        Log.current.println("Creating DAML vocabulary");
        // DamlOdpFort
        String term = "DamlOdpFort";
        String comment = "The KB subset collection of DAML ODP terms.";
        cycAccess.findOrCreate(term);
        cycAccess.assertComment(term, comment, "BaseKB");
        cycAccess.assertIsa(term, "VariableOrderCollection");
        cycAccess.assertGenls(term, "DamlFort");

        if (cycAccess.find("OpenDirectoryStructureMt") == null) {
            // #$OpenDirectoryStructureMt
            ArrayList genlMts = new ArrayList();
            genlMts.add("BaseKB");
            cycAccess.createMicrotheory(
                "OpenDirectoryStructureMt",
                "The microtheory which contains imported Open Directory structure assertions.",
                "Microtheory",
                genlMts);
        }

        // #$OpenDirectoryTopicFn
        cycAccess.createCollectionDenotingUnaryFunction(
            "OpenDirectoryTopicFn",
            "An instance of both CollectionDenotingFunction and ReifiableFunction. " +
            "Given an Open Directory category ID string as its single argument, " +
            "OpenDirectoryTopicFn returns the collection of Open Directory indexed " +
            "web resources for that category.",
            "BaseKB",
            "CharacterString",
            null,
            "Collection",
            null);
    }

    /**
     * Asserts additional lexical assertions about the given term which are specific to
     * the import of Open Directory terms. Overriden from the superclass for desired
     * behavior.
     *
     * @param term the imported Open Directory (wrapped) term
     */
    protected void additionalLexicalAssertions (CycFort term)
        throws IOException, CycApiException {
        String odpTopic =
            (String) gatherOpenDirectoryTitles.odpTitles.get(term.toString());
        if (odpTopic == null) {
            if (verbosity > 3)
                Log.current.println("No topic found for " + term.toString());
            return;
        }
        // (#$genPhrase <term> #$CountNoun #$singular "OPD <odpTopic>")
        String topic = "ODP " + odpTopic;
        if (verbosity > 1)
            Log.current.println("(#$genPhrase " + term.cyclify() +
                                " #$CountNoun #$singular \"" + topic + "\"");
        cycAccess.assertGenPhraseCountNounSingular(term, topic);
    }

    /**
     * Provides a container for specifying the ODP DAML document paths and
     * the Cyc import microtheory for each.
     */
    protected class DamlDocInfo {
        /**
         * path (url) to the ODP DAML document
         */
        protected String damlPath;

        /**
         * character encoding (e.g. UTF-8)
         */
        protected String characterEncoding;

        /**
         * microtheory into which DAML content is imported
         */
        protected String importMt;

        /**
         * Constructs a new DamlDocInfo object
         *
         * @param damlPath the path to the DAML docuement - as a URI
         * @param characterEncoding the character encoding scheme of the input DAML
         * document or null if default ASCII encoding
         * @param importMt the microtheory into which the non-definitional assertions are
         * placed
         */
        public DamlDocInfo (String damlPath, String characterEncoding, String importMt) {
            this.damlPath = damlPath;
            this.characterEncoding = characterEncoding;
            this.importMt = importMt;
        }

        /**
         * Returns the daml document path.
         *
         * @return the daml document path
         */
        public String getDamlPath () {
            return damlPath;
        }

        /**
         * Returns the character encoding scheme of the input DAML
         * document.
         *
         * @return the the character encoding scheme of the input DAML
         * document
         */
        public String getCharacterEncoding () {
            return characterEncoding;
        }

        /**
         * Returns the microtheory into which DAML content is imported.
         *
         * @return the microtheory into which DAML content is imported
         */
        public String getImportMt () {
            return importMt;
        }
    }
}