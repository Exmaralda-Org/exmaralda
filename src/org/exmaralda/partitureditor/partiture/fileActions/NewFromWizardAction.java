/*
 * NewFromSpeakertableAction.java
 *
 * Created on 6. Oktober 2003, 16:40
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import java.util.Vector;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.exakt.wizard.newtranscriptionwizard.NewTranscriptionWizard;

/**
 * creates a new file from a speakertable (which the user is prompted to create and edit)
 * and a stylesheet (which is either specified in the preferences or a built-in stylesheet is used)
 * Menu: File --> New from speakertable
 * @author  thomas
 */
public class NewFromWizardAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of NewFromSpeakertableAction */
    public NewFromWizardAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("New from wizard...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("newFromWizardAction!");
        newTranscription();
        table.transcriptionChanged = true;
        table.clearUndo();
        table.clearSearchResult();
        table.setFrameEndPosition(-2);        
    }

    private int addTiersForSpeaker(Speaker s, String[][] theSpecs, int count, BasicTranscription newTranscription) {
        int c = count;
        for (String[] specs : theSpecs){
            Tier newTier = new Tier();
            newTier.setID(newTranscription.getBody().getFreeID());
            newTier.setType(specs[0]);
            newTier.setCategory(specs[1]);
            if (s!=null){
                newTier.setSpeaker(s.getID());
                newTier.setDisplayName(s.getAbbreviation() + " [" + newTier.getCategory() + "]");
            } else {
                newTier.setDisplayName(" [" + newTier.getCategory() + "]");
            }
            try {
                newTranscription.getBody().addTier(newTier);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
            c++;
        }
        return c;
    }
    
    private void newTranscription(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;

        NewTranscriptionWizard wizard = new NewTranscriptionWizard(table.parent, true);
        wizard.setLocationRelativeTo(table);
        wizard.setVisible(true);

        if (wizard.isComplete()){

            Object[] data = wizard.getData();
            MetaInformation metaInformation = (MetaInformation)(data[0]);
            String[] recordings = (String[])(data[1]);
            String[] speakers = (String[])(data[2]);
            String mainCategory = (String)(data[3]);
            String[][][] tierSpecifications = (String[][][]) (data[4]);


            BasicTranscription newTranscription = new BasicTranscription();
            
            // Recordings and other meta-information
            Vector<String> refFiles = new Vector<String>();
            if ((recordings[2]!=null) && (recordings[2].trim().length()>0)){
                refFiles.addElement(recordings[2]);
            }
            if ((recordings[0]!=null) && (recordings[0].trim().length()>0)){
                refFiles.addElement(recordings[0]);
            }
            if ((recordings[1]!=null) && (recordings[1].trim().length()>0)){
                refFiles.addElement(recordings[1]);
            }
            if (refFiles.size()>0){
                metaInformation.setReferencedFiles(refFiles);
            }
            newTranscription.getHead().setMetaInformation(metaInformation);

            // speakers
            int count=0;
            for (String abb : speakers){
                Speaker newSpeaker = new Speaker();
                newSpeaker.setID("SPK" + Integer.toString(count));
                newSpeaker.setAbbreviation(abb);
                try {
                    newTranscription.getHead().getSpeakertable().addSpeaker(newSpeaker);
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
                count++;
            }

            newTranscription.getBody().getCommonTimeline().addTimelineItem();
            newTranscription.getBody().getCommonTimeline().addTimelineItem();

            // tiers
            count = 0;
            String[][] beforeSpecs = tierSpecifications[0];
            String[][] afterSpecs = tierSpecifications[1];

            for (int pos=0; pos<newTranscription.getHead().getSpeakertable().getNumberOfSpeakers(); pos++){
                Speaker s = newTranscription.getHead().getSpeakertable().getSpeakerAt(pos);

                //before main tier
                count = addTiersForSpeaker(s, beforeSpecs, count, newTranscription);

                // main tier
                Tier mainTier = new Tier();
                mainTier.setID("TIE" + Integer.toString(count));
                mainTier.setType("t");
                mainTier.setCategory(mainCategory);
                mainTier.setSpeaker(s.getID());
                mainTier.setDisplayName(s.getAbbreviation() + " [" + mainTier.getCategory() + "]");
                try {
                    newTranscription.getBody().addTier(mainTier);
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
                count++;

                //after main tier
                count = addTiersForSpeaker(s, afterSpecs, count, newTranscription);
            }

            // no speakers, bottom
            String[][] bottomNoSpeakerSpecs = tierSpecifications[3];
            count = addTiersForSpeaker(null, bottomNoSpeakerSpecs, count, newTranscription);

            // speakers, bottom
            String[][] bottomSpecs = tierSpecifications[2];
            for (int pos=0; pos<newTranscription.getHead().getSpeakertable().getNumberOfSpeakers(); pos++){
                Speaker s = newTranscription.getHead().getSpeakertable().getSpeakerAt(pos);
                count = addTiersForSpeaker(s, bottomSpecs, count, newTranscription);
            }


            table.getModel().setTranscription(newTranscription);
            table.showAllTiers();
            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            //table.reexportHTMLAction.setEnabled(false);
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();
            table.setupMedia();


            table.anchorTimelineItemAction.actionPerformed(null);

            table.status("New transcription");

        }


    }
    
    
}
