package org.opencyc.xml;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.opencyc.util.Log;

/**
 * Translates non-compliant OpenDirectory RDF Structure file into
 * DAML compliant format.<p>
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


public class OpenDirectoryToDaml {

    /**
     * The default verbosity of the DAML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected static final int DEFAULT_VERBOSITY = 2;
    //protected static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the DAML export output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public int verbosity = DEFAULT_VERBOSITY;

    /**
     * When true translates a sample of the Open Directory RDF structure file,
     * which is useful in testing with a small editable file.
     */
    //public boolean sample = true;
    public boolean sample = false;

    /**
     * the name of the ODP imput URL.
     */
    public String openDirectoryURLString = "http://dmoz.org/rdf/kt-structure.rdf.u8.gz";
    //public String openDirectoryURLString = "http://dmoz.org/rdf/structure.rdf.u8.gz";

    /**
     * the name of the DAML output file.
     */
    public String damlOutputPathName = "open-directory-structure.daml";

    /**
     * Open Directory RDF structure input stream
     */
    protected LineNumberReader openDirectoryInput;

    /**
     * DAML output stream
     */
    protected PrintWriter damlOutput;

    /**
     * dictionary of Open Directory topics to category ids
     */
    protected HashMap categoryIds = new HashMap();

    /**
     * number of topics translated
     */
    protected long nbrOfTopics = 0;

    /**
     * number of RDF triples translated
     */
    protected long nbrOfTriples = 0;

    /**
     * Constructs a new OpenDirectoryToDaml object.
     */
    public OpenDirectoryToDaml() {
    }

    /**
     * Provides the main class for this application.
     *
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        Log.makeLog("OpenDirectoryToDaml.log");
        OpenDirectoryToDaml openDirectoryToDaml = new OpenDirectoryToDaml();
        openDirectoryToDaml.indexCategoryIds();
        openDirectoryToDaml.translate();
    }

    /**
     * Opens the input URL and indexes the input OpenDirectory RDF category ids.
     */
    protected void indexCategoryIds () {
        URL openDirectoryURL = null;
        try {
            if (verbosity > 0)
                Log.current.println("Opening Open Directory URL " + openDirectoryURLString);
            openDirectoryURL = new URL(openDirectoryURLString);
            openDirectoryInput =
                new LineNumberReader(
                    new InputStreamReader(
                        new GZIPInputStream(openDirectoryURL.openStream()), "UTF-8"));
            indexRDF();
            openDirectoryInput.close();
            if (verbosity > 0)
                Log.current.println("OpenDirectory input URL closed.");
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Indexes the input OpenDirectory RDF category ids.
     */
    protected void indexRDF ()
        throws IOException {
        String topic = null;
        Long categoryId = null;
        boolean inDescriptionTag = false;
        if (verbosity > 0)
            Log.current.println("Indexing Open Directory category IDs.");
        while (true) {
            String textLine = openDirectoryInput.readLine();
            if (textLine.equals("</RDF>"))
                return;
            if (textLine.startsWith("<Topic r:id=")) {
                if (topic != null) {
                    Log.current.println("No catid for topic " + topic);
                    continue;
                }
                topic = textLine.substring(13, textLine.length() - 2);
            }
            else if (textLine.startsWith("  <catid>")) {
                String categoryIdDigits = textLine.substring(9, textLine.length() - 8);
                categoryId = new Long(categoryIdDigits);
                if (topic == null) {
                    Log.current.println("No topic for catid " + categoryIdDigits);
                    continue;
                }
                if (verbosity > 2)
                    Log.current.println(categoryIdDigits + " --> " + topic);
                categoryIds.put(topic, categoryId);
                topic = null;
                categoryId = null;
            }
        }
    }

    /**
     * Translates the input OpenDirectory RDF (non-compliant) structure file into
     * a DAML compliant format.  UTF-8 character encoding is used by Open Directory
     * for alternate language strings.
     */
    protected void translate () {
        URL openDirectoryURL = null;
        try {
            if (verbosity > 0)
                Log.current.println("Opening Open Directory URL " + openDirectoryURLString);
            openDirectoryURL = new URL(openDirectoryURLString);
            openDirectoryInput =
                new LineNumberReader(
                    new InputStreamReader(
                        new GZIPInputStream(openDirectoryURL.openStream()), "UTF-8"));
            damlOutput =
                new PrintWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(damlOutputPathName), "UTF8"));
        if (verbosity > 0)
            Log.current.println("DAML output file is " + damlOutputPathName);

            WriteDAMLHeader();
            BypassInputRDFHeader();
            TranslateRDF();
            WriteRDFClosingTag();

            damlOutput.close();
            if (verbosity > 0) {
                Log.current.println(nbrOfTopics + " topics");
                Log.current.println(nbrOfTriples + " RDF triples");
                Log.current.println("Translation completed.");
            }
            if (sample)
                System.exit(0);
            openDirectoryInput.close();
            if (verbosity > 0)
                Log.current.println("OpenDirectory input URL closed.");
        }
        catch (Exception e) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            System.exit(1);
        }
    }

    /**
     * Writes the XML header information to the output DAML
     * file.
     */
    protected void WriteDAMLHeader ()
        throws IOException {
        if (verbosity > 0)
            Log.current.println("Writing DAML header.");
        String xmlHeader =
            "<?xml version='1.0' encoding='UTF-8'?>";
        damlOutput.println(xmlHeader);
        damlOutput.println();
        String dtdHeader =
            "<!DOCTYPE uridef[\n" +
            "  <!ENTITY rdf   \"http://www.w3.org/1999/02/22-rdf-syntax-ns\">\n" +
            "  <!ENTITY rdfs  \"http://www.w3.org/2000/01/rdf-schema\">\n" +
            "  <!ENTITY daml  \"http://www.daml.org/2001/03/daml+oil\">\n" +
            "  <!ENTITY xsd   \"http://www.w3.org/2000/10/XMLSchema\">\n" +
            "  <!ENTITY dc    \"http://purl.org/dc/elements/1.1\">\n" +
            "  <!ENTITY dmoz  \"http://opencyc.sourceforge.net/open-directory\">\n" +
            "]>\n";
        damlOutput.print(dtdHeader);
        damlOutput.println();
        String rdfHeader =
            "<rdf:RDF\n" +
            "  xmlns:rdf  = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
            "  xmlns:rdfs = \"http://www.w3.org/2000/01/rdf-schema#\"\n" +
            "  xmlns:daml = \"http://www.daml.org/2001/03/daml+oil#\"\n" +
            "  xmlns:xsd  = \"http://www.w3.org/2000/10/XMLSchema#\"\n" +
            "  xmlns:dc   = \"http://purl.org/dc/elements/1.1#\"\n" +
            "  xmlns:dmoz = \"http://opencyc.sourceforge.net/open-directory#\"\n" +
            "  xmlns      = \"http://opencyc.sourceforge.net/open-directory#\"\n" +
            " >\n";
        damlOutput.print(rdfHeader);
        damlOutput.println();
    }

    /**
     * Bypasses the non-compliant RDF header information in the
     * Open Directory input file.
     */
    protected void BypassInputRDFHeader ()
        throws IOException {
        if (verbosity > 0)
            Log.current.println("Bypassing input RDF header.");
        while (true) {
            String inputLine = openDirectoryInput.readLine();
            if (inputLine.startsWith("<!-- Generated"))
                return;
            if (openDirectoryInput.getLineNumber() > 10) {
                Log.current.errorPrintln("Could not parse Open Directory RDF header");
                System.exit(1);
            }
        }
    }

    /**
     * Translates the Open Directory RDF content to DAML.
     */
    protected void TranslateRDF ()
        throws IOException {
        boolean inDescriptionTag = false;
        if (verbosity > 0)
            Log.current.println("Translating RDF to DAML.");
        while (true) {

            if (verbosity > 2)
                Log.current.println();
            String textLine = openDirectoryInput.readLine();
            if (verbosity > 2)
                Log.current.println(textLine);
            if (textLine.equals("</RDF>"))
                return;
            if (textLine.equals("")) {
                damlOutput.println(textLine);
                continue;
            }
            if (textLine.startsWith("<Topic r:id=")) {
                nbrOfTopics++;
                nbrOfTriples++;
                String resource = textLine.substring(13, textLine.length() - 2);
                StringBuffer stringBuffer = new StringBuffer("<dmoz:Topic rdf:ID=\"");
                String categoryId = getCategoryId(resource);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\">");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                stringBuffer = new StringBuffer();
                stringBuffer.append("  <rdfs:label xml:lang=\"EN\">");
                stringBuffer.append(resource);
                stringBuffer.append("</rdfs:label>");
                translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("</Topic>")) {
                String translatedTextLine = "</dmoz:Topic>";
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                if (sample) {
                    int lineNumber = openDirectoryInput.getLineNumber();
                    if (lineNumber > 200) {
                        if (verbosity > 2)
                            Log.current.println("Sample completed.");
                        return;
                    }
                }
                continue;
            }
            if (textLine.startsWith("  <catid>")) {
                nbrOfTriples++;
                String translatedTextLine =
                    textLine.replaceAll("catid", "dmoz:catid");
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <lastUpdate>")) {
                nbrOfTriples++;
                String translatedTextLine =
                    textLine.replaceAll("lastUpdate", "dmoz:lastUpdate");
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <editor r:resource=")) {
                nbrOfTriples++;
                String editor = textLine.substring(22, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:editor>");
                stringBuffer.append(editor);
                stringBuffer.append("</dmoz:editor>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <d:Description>")) {
                nbrOfTriples++;
                String translatedTextLine = textLine.replaceAll("d:", "dc:");
                if (translatedTextLine.indexOf("</dc:Description>") < 0) {
                    inDescriptionTag = true;
                }
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.indexOf("</d:Description>") > -1) {
                String translatedTextLine = textLine.replaceAll("d:", "dc:");
                inDescriptionTag = false;
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <d:")) {
                nbrOfTriples++;
                String translatedTextLine = textLine.replaceAll("d:", "dc:");
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                     Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <narrow r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(22, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:narrow rdf:resource=\"#");
                String categoryId = getCategoryId(resource);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <narrow1 r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(23, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:narrow1 rdf:resource=\"#");
                String categoryId = getCategoryId(resource);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <narrow2 r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(23, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:narrow2 rdf:resource=\"#");
                String categoryId = getCategoryId(resource);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <symbolic r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(24, textLine.length() - 3);
                int index = resource.indexOf(":");
                String description = resource.substring(0, index);
                String categoryId = getCategoryId(resource.substring(index + 1));
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("  <dmoz:symbolic>\n");
                stringBuffer.append("    <dmoz:SymbolicLink>\n");
                stringBuffer.append("      <dmoz:description>" + description + "</dmoz:description>\n");
                stringBuffer.append("      <dmoz:link rdf:resource=\"#" + categoryId + "\"/>\n");
                stringBuffer.append("    </dmoz:SymbolicLink>\n");
                stringBuffer.append("  </dmoz:symbolic>\n");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <symbolic1 r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(25, textLine.length() - 3);
                int index = resource.indexOf(":");
                String description = resource.substring(0, index);
                String categoryId = getCategoryId(resource.substring(index + 1));
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("  <dmoz:symbolic1>\n");
                stringBuffer.append("    <dmoz:SymbolicLink>\n");
                stringBuffer.append("      <dmoz:description>" + description + "</dmoz:description>\n");
                stringBuffer.append("      <dmoz:link rdf:resource=\"#" + categoryId + "\"/>\n");
                stringBuffer.append("    </dmoz:SymbolicLink>\n");
                stringBuffer.append("  </dmoz:symbolic1>\n");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <symbolic2 r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(25, textLine.length() - 3);
                int index = resource.indexOf(":");
                String description = resource.substring(0, index);
                String categoryId = getCategoryId(resource.substring(index + 1));
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("  <dmoz:symbolic2>\n");
                stringBuffer.append("    <dmoz:SymbolicLink>\n");
                stringBuffer.append("      <dmoz:description>" + description + "</dmoz:description>\n");
                stringBuffer.append("      <dmoz:link rdf:resource=\"#" + categoryId + "\"/>\n");
                stringBuffer.append("    </dmoz:SymbolicLink>\n");
                stringBuffer.append("  </dmoz:symbolic2>\n");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <newsGroup r:resource=")) {
                nbrOfTriples++;
                String newsGroup = textLine.substring(25, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:newsGroup>");
                stringBuffer.append(newsGroup);
                stringBuffer.append("</dmoz:newsGroup>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("<Alias r:id=")) {
                nbrOfTriples++;
                String resource = textLine.substring(13, textLine.length() - 2);
                String translatedTextLine = "<dmoz:Alias>";
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("  <rdfs:label xml:lang=\"EN\">");
                stringBuffer.append(resource);
                stringBuffer.append("</rdfs:label>");
                translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("</Alias>")) {
                String translatedTextLine = "</dmoz:Alias>";
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <Target r:resource=")) {
                nbrOfTriples++;
                String target = textLine.substring(22, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:Target rdf:resource=\"#");
                String categoryId = getCategoryId(target);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <altlang r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(23, textLine.length() - 3);
                int index = resource.indexOf(":");
                String language = resource.substring(0, index);
                String topic = resource.substring(index + 1);

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("  <dmoz:altlang>\n");
                stringBuffer.append("    <dmoz:AlternateLanguage>\n");
                stringBuffer.append("      <dmoz:language>" + language + "</dmoz:language>\n");
                stringBuffer.append("      <rdfs:label>" + topic + "</rdfs:label>\n");
                stringBuffer.append("    </dmoz:AlternateLanguage>\n");
                stringBuffer.append("  </dmoz:altlang>\n");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <letterbar r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(25, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:letterbar rdf:resource=\"#");
                stringBuffer.append(escape(resource));
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }
            if (textLine.startsWith("  <related r:resource=")) {
                nbrOfTriples++;
                String resource = textLine.substring(23, textLine.length() - 3);
                StringBuffer stringBuffer = new StringBuffer("  <dmoz:related rdf:resource=\"#");
                String categoryId = getCategoryId(resource);
                if (categoryId.equals("unknown")) {
                    if (verbosity > 2)
                        Log.current.println(textLine);
                }
                stringBuffer.append(categoryId);
                stringBuffer.append("\"/>");
                String translatedTextLine = stringBuffer.toString();
                damlOutput.println(translatedTextLine);
                if (verbosity > 2)
                    Log.current.println(translatedTextLine);
                continue;
            }

            if (inDescriptionTag)
                continue;
            Log.current.println(textLine + "\nDid not handle");
            return;
        }
    }

    /**
     * Substitutes category id for topic in resource references, as
     * Open Directory resource ids are not valid XML names.
     *
     * @param topic the Open Directory topic
     * @return the corresponding category identifier
     */
    protected String getCategoryId (String topic) {
        Long categoryId = (Long) categoryIds.get(topic);
        if (categoryId == null) {
            if (verbosity > 2)
                Log.current.println("No category ID found for topic " + topic);
            return "unknown";
        }
        else
            return "DMOZ-" + categoryId.toString();
    }

    /**
     * Escapes characters in XML names.
     *
     * @param text
     * @return the escaped text
     */
    protected String escape (String text) {
        return text;
        //return Strings.change(text,"/","..");
    }


    /**
     * Writes the RDF closing tag to the output DAML
     * file.
     */
    protected void WriteRDFClosingTag ()
        throws IOException {
        damlOutput.println("</rdf:RDF>");
    }




























}
