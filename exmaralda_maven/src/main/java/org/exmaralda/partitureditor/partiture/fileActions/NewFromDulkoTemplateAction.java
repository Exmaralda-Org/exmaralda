/*
 * NewFromSpeakertableAction.java
 *
 * Created on 6. Oktober 2003, 16:40
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.common.helpers.Internationalizer;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * creates a new file from a speakertable (which the user is prompted to create and edit)
 * and a stylesheet (which is either specified in the preferences or a built-in stylesheet is used)
 * Menu: File --> New from Dulko template
 * @author  thomas
 */

// issue #405


public class NewFromDulkoTemplateAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of NewFromSpeakertableAction
     * @param t */
    public NewFromDulkoTemplateAction(PartitureTableWithActions t) {
        super("New from Dulko template", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("newFromDulkoTemplateeAction!");
        newTranscription();
        table.transcriptionChanged = false;
        table.clearUndo();
        table.clearSearchResult();
        table.setFrameEndPosition(-2);        
    }
    
    private void newTranscription(){
        try {
            boolean proceed = true;
            if (table.transcriptionChanged){proceed = table.checkSave();}
            if (!proceed) return;
            
            Document doc = new IOUtilities().readDocumentFromResource("/org/exmaralda/dulko/resources/dulko-template.exb");
            System.out.println(IOUtilities.documentToString(doc));
            BasicTranscription newTranscription = new BasicTranscription();
            newTranscription.BasicTranscriptionFromJDOMDocument(doc);
            
            
            table.getModel().setTranscription(newTranscription);
            table.showAllTiers();
            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            //table.reexportHTMLAction.setEnabled(false);
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();
            table.setupMedia();
            
            table.status("New transcription from Dulko template");
        } catch (JDOMException | IOException | SAXException | JexmaraldaException ex) {
            Logger.getLogger(NewFromDulkoTemplateAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getMessage(), "Error with Dulko Template", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
}
