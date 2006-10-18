/* $Id$
 *
 * Copyright (c) 2003 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.xml;

//// Internal Imports
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycConnection;
import org.opencyc.util.Log;

//// External Imports
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * Imports an OWL xml document to test the ImportOwl class.<p>
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

public class ImportOwlTest extends ImportOwl {
  
  //// Constructors
  
  /** Creates a new instance of ImportOwlTest. 
   * 
   * @param cycAccess the CycAccess object
   */
  public ImportOwlTest(final CycAccess cycAccess) throws IOException {
    super(cycAccess);
  }
  
  //// Public Area
  
  //// Protected Area
  
  /**
   * Performs import of the OWL test document.
   */
  protected void doImport()
  throws IOException, CycApiException {
    getMappings();
    
    importOwl();
  }
  
  /**
   * Import the OWL document into Cyc.
   */
  protected void importOwl()
  throws IOException, UnknownHostException, CycApiException {
    
    OwlDocInfo owlDocInfo = new OwlDocInfo("http://www.cs.man.ac.uk/~horrocks/OWL/Ontologies/ka.owl",
                                           "ka",
                                           null,
                                           "OWL-TestMt",
                                           "OWLMappingMt"); 
    importOwl(owlDocInfo);
  }
  
      
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
  /**
   * Provides the main method for the ImportOwlTest application.
   *
   * @param args ignored.
   */
  public static void main(String[] args) {
    Log.makeLog("import-owl.log");
    ImportOwlTest importOwlTest = null;
    try {
      Log.current.println("Import OWL document starting");
      String localHostName = InetAddress.getLocalHost().getHostName();
      Log.current.println("Connecting to Cyc server from " + localHostName);
      CycAccess cycAccess = new CycAccess();
      Log.current.println(cycAccess.getCycConnection().connectionInfo());
      importOwlTest = new ImportOwlTest(cycAccess);
      importOwlTest.doImport();
    }
    catch (Exception e) {
      Log.current.printStackTrace(e);
      System.exit(1);
    }
  }
  
}
