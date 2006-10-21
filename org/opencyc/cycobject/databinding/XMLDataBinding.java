package org.opencyc.cycobject.databinding;

import  java.io.*;
import  org.enhydra.zeus.*;
import  org.enhydra.zeus.source.StreamSource;
import  org.enhydra.zeus.Source;


/**
 * Provides a wrapper for the Zeus XML data binding methods.
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
public class XMLDataBinding {

    /**
     * Constructs a new XMLDataBinding object.
     */
    public XMLDataBinding () {
    }

    /**
     * Unmarshall the given XML using the Zeus marshaller
     *
     * @param xml XML document to unmarshall
     * @param def_package Default package to look for classes to unmarshall the XML into
     * @return An object representing the XML
     */
    public static Object zeusUnmarshall (String xml, String def_package) throws java.io.IOException {
        org.enhydra.zeus.Unmarshaller unmarshaller = new org.enhydra.zeus.Unmarshaller();
        unmarshaller.setJavaPackage(def_package);
        UnmarshalledObject unmarshalledObject =
            unmarshaller.unmarshal((Source)new StreamSource(new StringReader(xml)));
        return  unmarshalledObject.getObject();
    }

    /**
     * Marshall the given Object using the Zeus marshaller
     *
     * @param obj Object to marshall
     * @return The XML document representing the Object
     */
    public static String zeusMarshall (Object obj) throws java.io.IOException {
        org.enhydra.zeus.Marshaller marshaller = new org.enhydra.zeus.Marshaller();
        StringWriter writer = new StringWriter();
        org.enhydra.zeus.result.StreamResult result = new org.enhydra.zeus.result.StreamResult(writer);
        marshaller.marshal(obj, result);
        return  writer.getBuffer().toString();
    }
}
