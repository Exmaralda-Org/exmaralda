/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.net.URISyntaxException;
import java.util.Hashtable;
import org.exmaralda.partitureditor.jexmaralda.errorChecker.EditErrorsDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.jdom.Document;
import org.jdom.Element;


/**
 *
 * @author  thomas
 */
public class GetStructureErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public GetStructureErrorsAction(PartitureTableWithActions t) {
        super("Structure errors...", t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("getStructureErrors!");
        table.commitEdit(true);
        getStructureErrors();
    }
    
    private void getStructureErrors() {
        String[] duplicateTranscriptionTiers = table.getModel().getTranscription().getDuplicateTranscriptionTiers();
        String[] orphanedTranscriptionTiers = table.getModel().getTranscription().getOrphanedTranscriptionTiers();
        String[] orphanedAnnotationTiers = table.getModel().getTranscription().getOrphanedAnnotationTiers();
        String[] temporalAnomalies = table.getModel().getTranscription().getBody().getCommonTimeline().getInconsistencies();
        // this returns a hashtable with tierIDs/array of event IDs
        Hashtable<String, String[]> annotationMismatches = table.getModel().getTranscription().getAnnotationMismatches();

        Document errorsDocument = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeEmptyDocument();
        Element errors = errorsDocument.getRootElement().getChild("errors");

        String filename = table.filename;

        String text = "More than one transcription tier for one speaker";
        for (String tierID : duplicateTranscriptionTiers){
            try {
                Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, tierID, "", text);
                errors.addContent(error);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }

        text = "Temporal anomaly";
        for (String tliID : temporalAnomalies){
            try {
                Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, "", tliID, text);
                errors.addContent(error);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }

        text = "Orphaned transcription tier";
        for (String tierID : orphanedTranscriptionTiers){
            try {
                Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, tierID, "", text);
                errors.addContent(error);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }

        text = "Orphaned annotation tier";
        for (String tierID : orphanedAnnotationTiers){
            try {
                Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, tierID, "", text);
                errors.addContent(error);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }

        text = "Annotation mismatch";
        for (String tierID : annotationMismatches.keySet()){
            String[] eventIDs = annotationMismatches.get(tierID);
            for (String eventID : eventIDs){
                try {
                    Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, tierID, eventID, text);
                    errors.addContent(error);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }


        EditErrorsDialog eed = new EditErrorsDialog(table.parent, false);
        eed.setOpenSaveButtonsVisible(false);
        eed.setTitle("Structure errors");
        eed.addErrorCheckerListener(table);
        eed.setErrorList(errorsDocument);
        eed.setLocationRelativeTo(table);
        eed.setVisible(true);
    }

    
    
}
