package org.opencyc.uml;

import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Imports the UML model, in XMI format.<p>
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
public class ImportUmlModel {

    /**
     * the SAXBuilder instance
     */
    protected SAXBuilder saxBuilder;

    /**
     * Constructs a new ImportUmlModel object given the
     * name of the SaxDriver class.
     */
    public ImportUmlModel () {
        saxBuilder = new SAXBuilder();
    }

    /**
     * Provides the main method for the ImportUmlModel application.
     *
     * @param args optionally provide the path to the UML model
     */
    public static void main(String[] args) {
        Log.makeLog();
        String umlModelPath = "xml/uml14.xml";
        //if (args.length > 0)
        //   umlModelPath = args[0];
        ImportUmlModel importUmlModel = new ImportUmlModel();
        try {
            importUmlModel.importModel(umlModelPath);
        }
        catch (JDOMException e) {
            Log.current.println(e.getMessage());
            Log.current.printStackTrace(e);
        }
    }

    /**
     * Imports the UML model given the path its file.
     *
     * @param umlModelPath the path the uml model file in xmi format
     */
    public void importModel (String umlModelPath)
        throws JDOMException {
        Log.current.println("Parsing " + umlModelPath);
        Document document = saxBuilder.build(new File(umlModelPath));
        Element root = document.getRootElement();
        Element header = root.getChild("XMI.header");
        Log.current.println("header: " + header.toString());
        Element content = root.getChild("XMI.content");
        Log.current.println("content: " + content.toString());
        importHeader(header);
        importContent(content);
    }

    /**
     * Imports the UML model version info given the header element.
     *
     * @param header the UML header element
     */
    public void importHeader (Element header) {
        Element modelVersion = header.getChild("XMI.model");
        Log.current.println("modelVersion: " + modelVersion.toString());
        Element metamodelVersion = header.getChild("XMI.metamodel");
        Log.current.println("metamodelVersion: " + metamodelVersion.toString());
    }

    /**
     * Imports the UML content info given the content element.
     *
     * @param content the UML content element
     */
    public void importContent (Element content) {
        Iterator iterator = content.getChildren().iterator();
        Log.current.println("content: " + content.toString());
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            String elementName = element.getName();
            if (elementName.equals("Package"))
                importPackage(element);
            else {
                Log.current.println("Unexpected element " + element);
                Log.current.printStackTrace(new RuntimeException());
                System.exit(1);
            }
        }
    }

    /**
     * Imports the UML model version info given the package element.
     *
     * @param packageElement the UML package element
     */
    public void importPackage (Element packageElement) {
        Log.current.println("package: " + packageElement);
        Iterator attributes = packageElement.getAttributes().iterator();
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            Log.current.println("  attribute " + attribute);
        }
    }

}