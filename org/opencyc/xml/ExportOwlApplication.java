/* $Id$
 *
 * Copyright (c) 2005 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.xml;

//// Internal Imports
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycConnection;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.Guid;
import org.opencyc.util.Log;
import org.opencyc.xml.ExportOwl;

//// External Imports

/**
 * <P>ExportOwlApplication is designed to export Cyc terms to OWL.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 *  date February 15, 2005, 2:14 PM
 * @version $Id$
 */
public class ExportOwlApplication {
  
  //// Constructors
  
  /** Creates a new instance of ExportOwlApplication. */
  public ExportOwlApplication() {
  }
  
  //// Public Area
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** Manages the api connection. */
  private static CycAccess cycAccess;
  
  //// Main
  
  /**
   * Provides the main method for an EELD OWL export.
   *
   * @param args The optional command line arguments.
   */
  public static void main(String[] args) {
    try {
      String host = CycConnection.DEFAULT_HOSTNAME;
//      String host = "penne.cyc.com";
      int basePort = CycConnection.DEFAULT_BASE_PORT;
      System.out.println("Host: " + host + " basePort: " + basePort);
      cycAccess = new CycAccess(host, basePort);
      ExportOwl exportOwl = new ExportOwl(cycAccess);
      exportOwl.verbosity = ExportOwl.DEFAULT_VERBOSITY;
//      exportOwl.verbosity = 9;
      exportOwl.rootTermGuid = cycAccess.getKnownConstantByName("Airplane").getGuid();
      exportOwl.cycKbSubsetFilterGuid = null;
      exportOwl.title = "Airplanes";
      exportOwl.outputPath = "ontology.owl";
      exportOwl.useResearchCycOntology = true;
      exportOwl.export(ExportOwl.EXPORT_KB_SUBSET_BELOW_TERM);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    // kill the Cyc api response handling thread
    System.exit(0);
  }
  
}
