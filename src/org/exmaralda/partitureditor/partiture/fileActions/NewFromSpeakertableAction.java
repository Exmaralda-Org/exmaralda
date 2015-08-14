/*
 * NewFromSpeakertableAction.java
 *
 * Created on 6. Oktober 2003, 16:40
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 * creates a new file from a speakertable (which the user is prompted to create and edit)
 * and a stylesheet (which is either specified in the preferences or a built-in stylesheet is used)
 * Menu: File --> New from speakertable
 * @author  thomas
 */
public class NewFromSpeakertableAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of NewFromSpeakertableAction */
    public NewFromSpeakertableAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("New from speakertable...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("newFromSpeakertableAction!");
        newTranscription();
        table.transcriptionChanged = false;
        table.clearUndo();
        table.clearSearchResult();
        table.setFrameEndPosition(-2);        
    }
    
    private void newTranscription(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;

        Speakertable st = new Speakertable();
        Speaker s = new Speaker();
        s.setID("SPK0");
        s.setAbbreviation("X");
        try {st.addSpeaker(s);} catch (JexmaraldaException je) {}
        
        EditSpeakerTableDialog dialog = new EditSpeakerTableDialog(table.parent, true, st);
        dialog.setTitle(Internationalizer.getString("New transcription from speakertable"));
        proceed = dialog.editSpeakertable();
        if (!proceed) return;
        
        Speakertable speakertable = dialog.getSpeakertable();
        
        BasicTranscription newTranscription = new BasicTranscription();
        boolean done = false;        
        if (table.speakertable2TranscriptionStylesheet.length()>0){
             try {
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();

                String transcriptionString = sf.applyExternalStylesheetToString(table.speakertable2TranscriptionStylesheet, 
                                                                              speakertable.toXML());
                newTranscription = new BasicTranscription();
                newTranscription.BasicTranscriptionFromString(transcriptionString);
                done = true;
             } catch (Exception e){
                String text = new String("There was a problem with " + System.getProperty("line.separator"));
                text+=table.speakertable2TranscriptionStylesheet + " : " + System.getProperty("line.separator");
                text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                text+="Using internal stylesheet instead.";                    
                javax.swing.JOptionPane.showMessageDialog(table.getParent(), text);           
             }                
        }
        // if no custon stylesheet is specified,
        // simply apply the built in stylesheet
        if (!done){
             try {
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();

                String transcriptionString = sf.applyInternalStylesheetToString(
                        "/org/exmaralda/partitureditor/jexmaralda/xsl/Speakertable2BasicTranscription.xsl",
                        speakertable.toXML());
                newTranscription.BasicTranscriptionFromString(transcriptionString);
                done = true;
             } catch (Exception e){} 
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

        table.status("New transcription");
    }
    
    
}
