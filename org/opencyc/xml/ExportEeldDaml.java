package org.opencyc.xml;

import  java.io.*;
import  java.net.*;
import  java.util.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.api.*;
import  org.opencyc.util.*;

/**
 * DAML+OIL Evidence Extraction and Link Discovery export for OpenCyc.
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

public class ExportEeldDaml {

    /**
     * The #$CounterTerrorismConstant guid.
     */
    public static final Guid counterTerrorismConstantGuid =
        CycObjectFactory.makeGuid("bfe31c38-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$EELDSharedOntologyCoreConstant guid.
     */
    public static final Guid eeldSharedOntologyCoreConstantGuid =
        CycObjectFactory.makeGuid("c12e44bd-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$EELDSharedOntologyConstant guid.
     */
    public static final Guid eeldSharedOntologyConstantGuid =
        CycObjectFactory.makeGuid("c06e4624-9c29-11b1-9dad-c379636f7270");

    /**
     * The #$IKBConstant guid.
     */
    public static final Guid ikbConstantGuid =
        CycObjectFactory.makeGuid("bf90b3e2-9c29-11b1-9dad-c379636f7270");

    /**
     * Manages the api connection.
     */
    protected static CycAccess cycAccess;

    /**
     * Provides the main method for an EELD DAML export.
     *
     * @parameter args the optional command line arguments
     */
    public static void main (String[] args) {
        try {
            cycAccess =

                new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                              CycConnection.DEFAULT_BASE_PORT,
                              CycConnection.DEFAULT_COMMUNICATION_MODE,
                              CycAccess.DEFAULT_CONNECTION);
            /*
                new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                              3620,
                              CycConnection.DEFAULT_COMMUNICATION_MODE,
                              CycAccess.DEFAULT_CONNECTION);
            */
            ExportDaml exportDaml = new ExportDaml(cycAccess);
            exportDaml.verbosity = ExportDaml.DEFAULT_VERBOSITY;
            //exportDaml.verbosity = 9;

            String choice = "eeld-core";
            if (args.length > 0)
                choice = args[0];

            Log.current.println("Choosing KB selection: " + choice);
            // These require the Cycorp IKB or full KB to work as setup below.
            if (choice.equals("all")) {
                exportDaml.includeUpwardClosure = false;
                exportDaml.title = "Open Cyc Ontology";
                exportDaml.outputPath = "open-cyc.daml";
                exportDaml.export(ExportDaml.EXPORT_ENTIRE_KB);
            }
            if (choice.equals("eeld-core")) {
                exportDaml.cycKbSubsetCollectionGuid = eeldSharedOntologyCoreConstantGuid;
                exportDaml.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportDaml.title = "EELD Shared Core Ontology";
                exportDaml.outputPath = "eeld-shared-core-ontology.daml";
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);
            }
            else if (choice.equals("eeld")) {
                exportDaml.cycKbSubsetCollectionGuid = eeldSharedOntologyConstantGuid;
                exportDaml.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportDaml.title = "EELD Shared Ontology";
                exportDaml.outputPath = "eeld-shared-ontology.daml";
                CycList kbSubsetCollections = new CycList();
                kbSubsetCollections.add(cycAccess.getKnownConstantByName("EELDSyntheticDataConstant"));
                kbSubsetCollections.add(cycAccess.getKnownConstantByName("EELDNaturalDataConstant"));
                CycList applicableBinaryPredicates =
                    cycAccess.getApplicableBinaryPredicates(kbSubsetCollections);
                applicableBinaryPredicates.remove(cycAccess.isa);
                applicableBinaryPredicates = applicableBinaryPredicates.sort();
                Log.current.println("applicableBinaryPredicates: \n" + applicableBinaryPredicates.cyclify());
                exportDaml.applicableBinaryPredicates = applicableBinaryPredicates;
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET);

            }
            else if (choice.equals("transportation-device")) {
                Guid transportationDeviceGuid =
                    CycObjectFactory.makeGuid("bd58d540-9c29-11b1-9dad-c379636f7270");
                exportDaml.rootTermGuid = transportationDeviceGuid;
                exportDaml.cycKbSubsetFilterGuid = counterTerrorismConstantGuid;
                exportDaml.export(ExportDaml.EXPORT_KB_SUBSET_BELOW_TERM);
            }
            else {
                System.out.println("specified choice not found - " + choice);
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}