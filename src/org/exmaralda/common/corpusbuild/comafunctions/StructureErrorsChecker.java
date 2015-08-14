/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.comafunctions;

import java.net.URISyntaxException;
import java.util.Hashtable;
import org.exmaralda.common.corpusbuild.AbstractCorpusChecker;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class StructureErrorsChecker extends AbstractCorpusChecker {


    boolean checkOrphanedTranscriptionTiers = true;
    boolean checkDuplicateTranscriptionTiers = true;
    boolean checkOrphanedAnnotationTiers = true;
    boolean checkAnnotationMismatches = true;
    boolean checkTemporalAnomalies = true;


    public StructureErrorsChecker(boolean orphanedT, boolean duplicateT, boolean orphanedA, boolean aMismatch, boolean tAnomalie) {
        checkOrphanedTranscriptionTiers = orphanedT;
        checkDuplicateTranscriptionTiers = duplicateT;
        checkOrphanedAnnotationTiers = orphanedA;
        checkAnnotationMismatches = aMismatch;
        checkTemporalAnomalies = tAnomalie;
    }



    @Override
    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, SAXException {


        if (checkDuplicateTranscriptionTiers){
            String[] duplicateTranscriptionTiers = bt.getDuplicateTranscriptionTiers();
            String text = "More than one transcription tier for one speaker";
            for (String tierID : duplicateTranscriptionTiers){
               addError(currentFilename, tierID, "", text);
            }
        }

        if (checkOrphanedTranscriptionTiers){
            String[] orphanedTranscriptionTiers = bt.getOrphanedTranscriptionTiers();
            String text = "Orphaned transcription tier";
            for (String tierID : orphanedTranscriptionTiers){
               addError(currentFilename, tierID, "", text);
            }
        }
        if (checkOrphanedAnnotationTiers){
            String[] orphanedAnnotationTiers = bt.getOrphanedAnnotationTiers();
            String text = "Orphaned annotation tier";
            for (String tierID : orphanedAnnotationTiers){
               addError(currentFilename, tierID, "", text);
            }
        }

        if (checkAnnotationMismatches){
            Hashtable<String, String[]> annotationMismatches = bt.getAnnotationMismatches();
            String text = "Annotation mismatch";
            for (String tierID : annotationMismatches.keySet()){
                String[] eventIDs = annotationMismatches.get(tierID);
                for (String eventID : eventIDs){
                    addError(currentFilename, tierID, eventID, text);
                }
            }
        }

        if (checkTemporalAnomalies){
            String[] temporalAnomalies = bt.getBody().getCommonTimeline().getInconsistencies();
            String text = "Temporal anomaly";
            for (String tliID : temporalAnomalies){
                addError(currentFilename, "", tliID, text);
            }
        }


    }

}
