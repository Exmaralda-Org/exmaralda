/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.comafunctions;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.corpusbuild.AbstractCorpusChecker;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.transcriptionActions.GetStructureErrorsAction;
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
    boolean checkStratification = true;


    public StructureErrorsChecker(boolean orphanedT, boolean duplicateT, boolean orphanedA, boolean aMismatch, boolean tAnomalie, boolean strat) {
        checkOrphanedTranscriptionTiers = orphanedT;
        checkDuplicateTranscriptionTiers = duplicateT;
        checkOrphanedAnnotationTiers = orphanedA;
        checkAnnotationMismatches = aMismatch;
        checkTemporalAnomalies = tAnomalie;
        checkStratification = strat;
    }

    public StructureErrorsChecker(boolean orphanedT, boolean duplicateT, boolean orphanedA, boolean aMismatch, boolean tAnomalie) {
        checkOrphanedTranscriptionTiers = orphanedT;
        checkDuplicateTranscriptionTiers = duplicateT;
        checkOrphanedAnnotationTiers = orphanedA;
        checkAnnotationMismatches = aMismatch;
        checkTemporalAnomalies = tAnomalie;
        checkStratification = false;
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
            // issue #315
            
            for (String tierID : annotationMismatches.keySet()){
                String[] eventIDs = annotationMismatches.get(tierID);
                String category = "";
                try {
                    category = bt.getBody().getTierWithID(tierID).getCategory();
                } catch (JexmaraldaException ex) {
                    Logger.getLogger(GetStructureErrorsAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (String eventID : eventIDs){
                    addError(currentFilename, tierID, eventID, text + " [" + category + "]");
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
        
        if (checkStratification){
            List<String> nonStratifiedTiers = bt.getBody().getNonStratifiedTiers();
            String text = "Tier is not stratified";
            for (String tierID : nonStratifiedTiers){
               addError(currentFilename, tierID, "", text);
            }
            
        }


    }

}
