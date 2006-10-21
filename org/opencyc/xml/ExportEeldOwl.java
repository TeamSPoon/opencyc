package org.opencyc.xml;

import  java.io.*;
import  java.net.*;
import  java.util.*;
import  org.opencyc.cycobject.*;
import  org.opencyc.api.*;
import  org.opencyc.util.*;

/**
 * OWL (Web Ontology Language) Evidence Extraction and Link Discovery export for OpenCyc.
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2003 Cycorp, Inc., license is open source GNU LGPL.
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

public class ExportEeldOwl {

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
     * Provides the main method for an EELD OWL export.
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
            ExportOwl exportOwl = new ExportOwl(cycAccess);
            exportOwl.verbosity = ExportOwl.DEFAULT_VERBOSITY;
            //exportOwl.verbosity = 9;

            String choice = "all";
            if (args.length > 0)
                choice = args[0];

            Log.current.println("Choosing KB selection: " + choice);
            if (choice.equals("all")) {
                exportOwl.includeUpwardClosure = false;
                exportOwl.title = "Open Cyc Ontology";
                exportOwl.outputPath = "open-cyc.owl";
                exportOwl.export(ExportOwl.EXPORT_ENTIRE_KB);
            }
            // These require the Cycorp IKB or full KB to work as setup below.
            else if (choice.equals("eeld-core")) {
                exportOwl.cycKbSubsetCollectionGuid = eeldSharedOntologyCoreConstantGuid;
                exportOwl.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportOwl.title = "EELD Shared Core Ontology";
                exportOwl.outputPath = "eeld-shared-core-ontology.owl";
                exportOwl.export(ExportOwl.EXPORT_KB_SUBSET);
            }
            else if (choice.equals("eeld")) {
                exportOwl.cycKbSubsetCollectionGuid = eeldSharedOntologyConstantGuid;
                exportOwl.cycKbSubsetFilterGuid = ikbConstantGuid;
                exportOwl.title = "EELD Shared Ontology";
                exportOwl.outputPath = "eeld-shared-ontology.owl";
                CycList kbSubsetCollections = new CycList();
                kbSubsetCollections.add(cycAccess.getKnownConstantByName("EELDSyntheticDataConstant"));
                kbSubsetCollections.add(cycAccess.getKnownConstantByName("EELDNaturalDataConstant"));
                CycList applicableBinaryPredicates =
                    cycAccess.getApplicableBinaryPredicates(kbSubsetCollections);
                applicableBinaryPredicates.remove(cycAccess.isa);
                applicableBinaryPredicates = applicableBinaryPredicates.sort();
                Log.current.println("applicableBinaryPredicates: \n" + applicableBinaryPredicates.cyclify());
                exportOwl.applicableBinaryPredicates = applicableBinaryPredicates;
                exportOwl.export(ExportOwl.EXPORT_KB_SUBSET);

            }
            else if (choice.equals("transportation-device")) {
                Guid transportationDeviceGuid =
                    CycObjectFactory.makeGuid("bd58d540-9c29-11b1-9dad-c379636f7270");
                exportOwl.rootTermGuid = transportationDeviceGuid;
                exportOwl.cycKbSubsetFilterGuid = counterTerrorismConstantGuid;
                exportOwl.export(ExportOwl.EXPORT_KB_SUBSET_BELOW_TERM);
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