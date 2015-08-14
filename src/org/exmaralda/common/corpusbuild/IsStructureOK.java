/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.util.Hashtable;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class IsStructureOK {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String pathToFile = args[0];
        BasicTranscription bt = null;
        try {
            bt = new BasicTranscription(pathToFile);
        } catch (SAXException ex) {
            System.out.println("Error (SAXException)");
            System.exit(1);
        } catch (JexmaraldaException ex) {
            System.out.println("Error (JexmaraldaException)");
            System.exit(1);
        }
        String[] duplicateTranscriptionTiers = bt.getDuplicateTranscriptionTiers();
        String[] orphanedTranscriptionTiers = bt.getOrphanedTranscriptionTiers();
        String[] orphanedAnnotationTiers = bt.getOrphanedAnnotationTiers();
        // this returns a hashtable with tierIDs/array of event IDs
        Hashtable<String, String[]> annotationMismatches = bt.getAnnotationMismatches();

        boolean ok = true;
        if (duplicateTranscriptionTiers.length>0){
            System.out.println("Error (Duplicate Transcription Tier(s)");
            ok =false;
        }
        if (orphanedTranscriptionTiers.length>0){
            System.out.println("Error (Orphaned Transcription Tier(s))");
            ok =false;
        }
        if (orphanedAnnotationTiers.length>0){
            System.out.println("Error (Orphaned Annotation Tier(s))");
            ok =false;
        }
        /*if (annotationMismatches.size()>0){
            System.out.println("Error (Annotation Mismatche(s))");
            ok =false;
        }*/
        for (String tierID : annotationMismatches.keySet()){
            String[] eventIDs = annotationMismatches.get(tierID);
            if (eventIDs.length>0){
                System.out.println("Error (Annotation Mismatche(s))");
                ok =false;
                break;
            }
        }

        if (!ok){
            System.exit(1);
        }

        System.out.println("OK");
        System.exit(0);
    }

}
